package oss.technion.openstreetheight.section.map.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.evernote.android.state.StateSaver;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.map.presenter.BuildingMapPresenter;

public class BuildingMapFragment extends Fragment implements BuildingMapView,
        OnMapReadyCallback, OnMapLongClickListener, OnMarkerClickListener {

    // Presenter
    private BuildingMapPresenter presenter = new BuildingMapPresenter(this);
    private Map<Integer, Marker> markerIndex = new HashMap<>();

    // Views
    private GoogleMap map;
    private ActionBar actionBar;
    private Menu menu;
    private Snackbar curSnackbar;

    // State Save
    private @Nullable
    Bundle savedInstanceState;

    private boolean wasPaused;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_building_sides_map, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        this.savedInstanceState = savedInstanceState;

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.inner_container, mapFragment, "MAP_FRAGMENT")
                .commit();


        mapFragment.getMapAsync(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

        wasPaused = true;
        presenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(wasPaused) presenter.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_building_sides_map, menu);
        this.menu = menu;

        setMenuItemEnabled(R.id.nav_continue, false);
        setMenuItemEnabled(R.id.nav_undo, false);

        semaphoreStart();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_continue:
                presenter.onNavContinueClick();
                return true;

            case R.id.nav_undo:
                presenter.onNavUndoClick();
                return true;

            case R.id.toggle_follow_bearing:
                presenter.onToggleFollowBearingClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        // restore map state if needed
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        semaphoreStart();

        // set map properties
        OsmTileProvider tileProvider = new OsmTileProvider(256, 256);
        map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

        map.setMapType(GoogleMap.MAP_TYPE_NONE);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setIndoorEnabled(false);

        // attach listeners
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    private void semaphoreStart() {

        if (map != null && menu != null) {
            boolean shouldRestoreViewState = (savedInstanceState != null);
            presenter.onStart(shouldRestoreViewState);
        }
    }

    @Override
    public void addDefaultMarker(int id, double lat, double lon) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
        );

        marker.setTag(id);

        markerIndex.put(id, marker);
    }

    private void showSnackbarText(String text, SnackbarDuration type) {
        int snackbarType = -1;

        switch (type) {

            case LONG:
                snackbarType = Snackbar.LENGTH_LONG;
                break;

            case SHORT:
                snackbarType = Snackbar.LENGTH_SHORT;
                break;

            case INDEFINITE:
                snackbarType = Snackbar.LENGTH_INDEFINITE;
                break;
        }

        curSnackbar = Snackbar
                .make(getView(), text, snackbarType);

        curSnackbar.show();
    }

    @Override
    public void showSnackbarText(@StringRes int text, SnackbarDuration type) {
        showSnackbarText(getString(text), type);
    }

    @Override
    public void showSnackbarText(@StringRes int text, String args, SnackbarDuration type) {
        showSnackbarText(getString(text, args), type);
    }

    @Override
    public void setActionBarTitle(@StringRes int text) {
        actionBar.setTitle(text);
    }

    @Override
    public void setActionBarSubtitle(@StringRes int text) {
        actionBar.setSubtitle(text);
    }

    @Override
    public void cleanActionBarSubtitle() {
        actionBar.setSubtitle(null);
    }


    @Override
    public void setMarkerText(int markerId, @StringRes int text) {
        Marker marker = markerIndex.get(markerId);

        IconGenerator iconGen = new IconGenerator(getContext());
        Bitmap textBitmap = iconGen.makeIcon(getString(text));

        marker.setIcon(BitmapDescriptorFactory.fromBitmap(textBitmap));
    }

    @Override
    public void setMenuItemEnabled(@IdRes int id, boolean enabled) {
        menu.findItem(id).setEnabled(enabled);
        menu.findItem(id).getIcon().setAlpha(enabled ? 255 : 64);
    }

    @Override
    public void changeToggleBearingDrawable(@DrawableRes int image) {
        menu.findItem(R.id.toggle_follow_bearing).setIcon(image);
    }

    @Override
    public void rotateMapAroundImmed(oss.technion.openstreetheight.model.data.LatLng location, float azimuth) {
        LatLng mapLocation = new LatLng(location.lat, location.lon);

        CameraPosition curCamPosition = map.getCameraPosition();
        CameraPosition newCamPosition = new CameraPosition(mapLocation, curCamPosition.zoom, curCamPosition.tilt, azimuth);

        map.moveCamera(CameraUpdateFactory.newCameraPosition(newCamPosition));
    }

    @Override
    public void showBackButton(boolean isShowBackButton) {
        actionBar.setDisplayShowHomeEnabled(isShowBackButton);
        actionBar.setDisplayHomeAsUpEnabled(isShowBackButton);
    }

    @Override
    public void vibrateLongPress() {
        getView().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    @Override
    public void checkLocationPermissionSilent() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            presenter.onLocationPermissionSatisfied();
        }

    }

    @Override
    public void checkLocationSettingsSilent() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(getContext());

        Task<LocationSettingsResponse> task = client
                .checkLocationSettings(settingsRequest);

        task.addOnSuccessListener(e -> presenter.onLocationSettingsSatisfied());
    }


    @Override
    public void moveMapTo(oss.technion.openstreetheight.model.data.LatLng location, float zoom) {
        LatLng mapLocation = new LatLng(location.lat, location.lon);

        CameraPosition curCamPosition = map.getCameraPosition();
        CameraPosition newCamPosition = new CameraPosition(mapLocation, zoom, curCamPosition.tilt, curCamPosition.bearing);

        map.moveCamera(CameraUpdateFactory.newCameraPosition(newCamPosition));
    }

    @Override
    public void moveMapTo(oss.technion.openstreetheight.model.data.LatLng location) {
        LatLng mapLocation = new LatLng(location.lat, location.lon);

        CameraPosition curCamPosition = map.getCameraPosition();
        CameraPosition newCamPosition = new CameraPosition(mapLocation, curCamPosition.zoom, curCamPosition.tilt, curCamPosition.bearing);

        map.moveCamera(CameraUpdateFactory.newCameraPosition(newCamPosition));
    }

    @Override
    public void deleteMarker(int markerId) {
        markerIndex.get(markerId).remove();
        markerIndex.remove(markerId);
    }


    @Override
    public void resetMarkerToDefault(int id) {
        Marker marker = markerIndex.get(id);

        marker.setIcon(BitmapDescriptorFactory.defaultMarker());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int markerId = (int) marker.getTag();

        presenter.onMarkerClick(markerId, marker.getPosition().latitude, marker.getPosition().longitude);
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        presenter.onMapLongClick(latLng.latitude, latLng.longitude);
    }

    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ************************* THIS SECTION DEALS WITH STATE RESTORING **************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ZOOM = "zoom";
    private static final String BEARING = "bearing";
    private static final String TILT = "tilt";

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        // map
        if (map != null) {
            CameraPosition position = map.getCameraPosition();

            bundle.putDouble(LATITUDE, position.target.latitude);
            bundle.putDouble(LONGITUDE, position.target.longitude);
            bundle.putFloat(ZOOM, position.zoom);
            bundle.putFloat(TILT, position.tilt);
            bundle.putFloat(BEARING, position.bearing);
        }

        // presenter
        StateSaver.saveInstanceState(presenter, bundle);

        super.onSaveInstanceState(bundle);
    }

    private void onRestoreInstanceState(@NonNull Bundle bundle) {
        // map
        double longitude = bundle.getDouble(LONGITUDE);
        double latitude = bundle.getDouble(LATITUDE);
        float zoom = bundle.getFloat(ZOOM);
        float tilt = bundle.getFloat(TILT);
        float bearing = bundle.getFloat(BEARING);

        LatLng target = new LatLng(latitude, longitude);
        CameraPosition restoreCamPosition = new CameraPosition(target, zoom, tilt, bearing);
        map.moveCamera(CameraUpdateFactory.newCameraPosition(restoreCamPosition));

        // presenter
        StateSaver.restoreInstanceState(presenter, savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void setMyLocationMarkerEnabled(boolean enabled) {
        map.setMyLocationEnabled(enabled);
    }

    @Override
    public void dismissCurrentSnackbar() {
        if (curSnackbar != null) {
            curSnackbar.dismiss();
        }
    }

    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ****************** THIS SECTION DEALS WITH LOCATION PERMISSION RESOLUTION ******************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************

    private static final int MY_LOCATION_REQUEST_CODE = 1;

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            presenter.onLocationPermissionSatisfied();


        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                // Happens when user clicked do not ask again when permission pop-up was showed
                // or manually disabled it in Settings

            } else {

                // No explanation needed, we can request the permission.

                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE
                );
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    Objects.equals(permissions[0], Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                presenter.onLocationPermissionSatisfied();


            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************* THIS SECTION DEALS WITH LOCATION SETTINGS DIALOG *********************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************
    // ********************************************************************************************

    private static final int REQUEST_CHECK_SETTINGS = 100;

    public void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(getContext());

        Task<LocationSettingsResponse> task = client
                .checkLocationSettings(settingsRequest);

        task.addOnSuccessListener(e -> presenter.onLocationSettingsSatisfied());

        task.addOnFailureListener(e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                // Location settings are not satisfied, but this can
                // be fixed by showing the user a dialog
                try {
                    // Show the dialog by calling
                    // startResolutionForResult(), and check the
                    // result in onActivityResult()
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    presenter.onLocationSettingsSatisfied();
                }
                break;
        }


    }
}
