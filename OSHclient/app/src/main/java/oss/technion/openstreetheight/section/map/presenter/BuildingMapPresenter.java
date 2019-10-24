package oss.technion.openstreetheight.section.map.presenter;


import com.evernote.android.state.State;

import java.util.HashMap;
import java.util.Map;

import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.BuildingMsg;
import oss.technion.openstreetheight.model.InternetAccess;
import oss.technion.openstreetheight.model.OsmApi;
import oss.technion.openstreetheight.model.PhoneParams;
import oss.technion.openstreetheight.model.compass.Compass;
import oss.technion.openstreetheight.model.FusedLocation;
import oss.technion.openstreetheight.model.geo.GeoCoding;
import oss.technion.openstreetheight.model.data.LatLng;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.map.view.BuildingMapView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BuildingMapPresenter {
    enum PickState {PICK_BUILDING, PICK_MIDDLE_CORNER, PICK_LEFT_CORNER, PICK_RIGHT_CORNER, FINISH}

    private BuildingMapView view;
    private CompositeDisposable disposables = new CompositeDisposable();

    @State PickState state;

    @State LatLng leftMarkerPos;
    @State LatLng rightMarkerPos;
    @State LatLng middleMarkerPos;

    @State int leftMarkerId;
    @State int rightMarkerId;
    @State int middleMarkerId;

    @State HashMap<Integer, LatLng> buildingPointsAsMarkers = new HashMap<>();
    @State long osm_id;
    @State String buildingAddress;

    @State boolean isFollowBearingEnabled;

    private boolean isLocationSettingsSatisfied;
    private boolean isLocationPermissionSatisfied;
    private boolean isQueryingForBuilding;

    public BuildingMapPresenter(BuildingMapView view) {
        this.view = view;
    }

    public void onStart(boolean shouldRestoreViewState) {
        view.showBackButton(Router.isShowBackButton);

        // Will be enabled when all permissions are satisfied
        view.setMenuItemEnabled(R.id.toggle_follow_bearing, false);

        // Once permissions satisfied, we enable FusedLocation
        // First start, rotation, app load/unload
        view.checkLocationSettings();
        view.checkLocationPermission();

        if (shouldRestoreViewState) {
            restoreViewState();
        } else {
            state = PickState.PICK_BUILDING;
            updateActionBarTitleAndSubtitle();

            // By default, map centers at (0, 0) with zoom 0
        }

        disposables.add(
                Compass
                        .getAccuracySubject()
                        .subscribe(this::checkCompassAccuracy)
        );


        if (FusedLocation.getLastLocation() == null || !shouldRestoreViewState) { // Either first enter or enter w/o fix


            disposables.add(
                    FusedLocation
                            .getBehSubject()
                            .firstElement()
                            .subscribe(firstFix -> {

                                view.moveMapTo(firstFix, 18);

                                isFollowBearingEnabled = true;
                                view.changeToggleBearingDrawable(R.drawable.ic_navigation_on_24dp);

                                // It may happen that despite it is first enter,
                                // permission is not granted but fix is present.
                                //
                                // Remaining actions will be done when all permissions are obtained.
                                if(!shouldRestoreViewState && !isLocationPermissionSatisfied) {
                                    return;
                                }

                                view.setMenuItemEnabled(R.id.toggle_follow_bearing, true);
                                view.setMyLocationMarkerEnabled(true);

                                view.showSnackbarText(R.string.map_snack_disable_follow_bearing, SnackbarDuration.LONG);

                                Compass.enable();
                            })
            );
        }

        disposables.add(
                Compass
                        .getAzimuthSubject()
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(floatBearing -> {
                            switch (PhoneParams.getRotation()) {

                                case ROTATION_0:

                                    break;

                                case ROTATION_90:
                                    floatBearing += 90;
                                    break;

                                case ROTATION_180:
                                    floatBearing += 180;
                                    break;

                                case ROTATION_270:
                                    floatBearing -= 270;
                                    break;
                            }

                            if (floatBearing < 0) floatBearing += 360;
                            else if (floatBearing > 360) floatBearing -= 360;

                            return floatBearing;
                        })
                        .subscribe(azimuth -> {
                                    if (
                                            (Compass.getAccuracy() == Compass.Accuracy.HIGH ||
                                            Compass.getAccuracy() == Compass.Accuracy.MEDIUM)
                                            //&& FusedLocation.getLastLocation() != null
                                    ) {
                                        view.rotateMapAroundImmed(FusedLocation.getLastLocation(), azimuth.floatValue());
                                    }
                                }
                        )
        );
    }

    private void checkCompassAccuracy(Compass.Accuracy accuracy) {
        switch (accuracy) {
            case LOW:
                view.showSnackbarText(R.string.map_snack_compass_accuracy_low, SnackbarDuration.INDEFINITE);
                break;

            case MEDIUM:
                view.showSnackbarText(R.string.map_snack_compass_accuracy_medium, SnackbarDuration.INDEFINITE);
                break;

            case UNRELIABLE:
                view.showSnackbarText(R.string.map_snack_compass_accuracy_unreliable, SnackbarDuration.INDEFINITE);
                break;
        }
    }

    // Called both from onStart and onResume
    public void onLocationSettingsSatisfied() {
        isLocationSettingsSatisfied = true;

        // Happens after all location requirements done
        if(isLocationPermissionSatisfied && isLocationSettingsSatisfied && (FusedLocation.getLastLocation() != null)) {
            view.setMenuItemEnabled(R.id.toggle_follow_bearing, true);

            if(isFollowBearingEnabled) {
                Compass.enable();
            }

        }
    }

    // Called both from onStart and onResume
    public void onLocationPermissionSatisfied() {
        isLocationPermissionSatisfied = true;

        view.setMyLocationMarkerEnabled(true);

        if(isLocationPermissionSatisfied && isLocationSettingsSatisfied && (FusedLocation.getLastLocation() != null)) {
            view.setMenuItemEnabled(R.id.toggle_follow_bearing, true);

            if(isFollowBearingEnabled) {
                Compass.enable();
            }

        }

        FusedLocation.enable();
    }

    // Called before onStop anyways
    public void onPause() {
        isLocationSettingsSatisfied = false;
        isLocationPermissionSatisfied = false;

        view.setMenuItemEnabled(R.id.toggle_follow_bearing, false);

        Compass.disable();
        FusedLocation.disable();
    }

    // Called only after onPause
    public void onResume() {
        view.checkLocationPermissionSilent(); // It will enable Fused Location if permissions were satisfied
        view.checkLocationSettingsSilent();
    }

    // Prior to this onPause was called
    public void onStop() {
        disposables.clear();

        view.cleanActionBarSubtitle();
        view.dismissCurrentSnackbar();
    }

    public void onToggleFollowBearingClick() {
        isFollowBearingEnabled = !isFollowBearingEnabled;

        if (isFollowBearingEnabled) {
            view.changeToggleBearingDrawable(R.drawable.ic_navigation_on_24dp);
            Compass.enable();
            checkCompassAccuracy(Compass.getAccuracy());
        } else {
            view.changeToggleBearingDrawable(R.drawable.ic_navigation_off_24dp);
            Compass.disable();
            view.dismissCurrentSnackbar(); // if compass accuracy is not good
        }
    }

    public void onMarkerClick(int markerId, double lat, double lon) {
        switch (state) {
            case PICK_MIDDLE_CORNER:
                state = PickState.PICK_LEFT_CORNER;
                updateActionBarTitleAndSubtitle();

                middleMarkerPos = new LatLng(lat, lon);
                middleMarkerId = markerId;

                view.setMarkerText(markerId, R.string.map_marker_type_middle);
                break;

            case PICK_LEFT_CORNER:
                if (middleMarkerId == markerId) break;

                state = PickState.PICK_RIGHT_CORNER;
                updateActionBarTitleAndSubtitle();

                leftMarkerPos = new LatLng(lat, lon);
                leftMarkerId = markerId;

                view.setMarkerText(markerId, R.string.map_marker_type_left);
                break;

            case PICK_RIGHT_CORNER:
                if (leftMarkerId == markerId) break;

                state = PickState.FINISH;
                updateActionBarTitleAndSubtitle();

                rightMarkerPos = new LatLng(lat, lon);
                rightMarkerId = markerId;

                view.setMenuItemEnabled(R.id.nav_continue, true);
                view.setMarkerText(markerId, R.string.map_marker_type_right);
                break;
        }

    }

    private void restoreViewState() {
        // "Follow bearing" mode
        if (isFollowBearingEnabled) {
            view.changeToggleBearingDrawable(R.drawable.ic_navigation_on_24dp);

            Compass.enable();

            checkCompassAccuracy(Compass.getAccuracy());
        }


        // action bar title
        updateActionBarTitleAndSubtitle();

        // map state
        switch (state) {
            case FINISH:
            case PICK_RIGHT_CORNER:
            case PICK_LEFT_CORNER:
            case PICK_MIDDLE_CORNER:
                for (Map.Entry<Integer, LatLng> entry : buildingPointsAsMarkers.entrySet()) {
                    view.addDefaultMarker(entry.getKey(), entry.getValue().lat, entry.getValue().lon);
                }

                view.showSnackbarText(R.string.map_snack_cur_building_tip, buildingAddress, SnackbarDuration.INDEFINITE);
                break;
        }

        // map state - fallthrough!
        switch (state) {
            case FINISH:
                view.setMenuItemEnabled(R.id.nav_continue, true);
                view.setMarkerText(rightMarkerId, R.string.map_marker_type_right);

            case PICK_RIGHT_CORNER:
                view.setMarkerText(leftMarkerId, R.string.map_marker_type_left);

            case PICK_LEFT_CORNER:
                view.setMarkerText(middleMarkerId, R.string.map_marker_type_middle);

            case PICK_MIDDLE_CORNER:
                view.setMenuItemEnabled(R.id.nav_undo, true);

            case PICK_BUILDING:
                // do nothing
        }
    }

    private void updateActionBarTitleAndSubtitle() {
        switch (state) {

            case PICK_BUILDING:
                view.setActionBarTitle(R.string.map_1_long_tap_title);
                view.setActionBarSubtitle(R.string.map_on_your_building_subtitle);
                break;

            case PICK_MIDDLE_CORNER:
                view.setActionBarTitle(R.string.map_2_tap_title);
                view.setActionBarSubtitle(R.string.map_on_the_closest_corner_subtitle);
                break;

            case PICK_LEFT_CORNER:
                view.setActionBarTitle(R.string.map_3_tap_title);
                view.setActionBarSubtitle(R.string.map_on_the_left_corner_subtitle);
                break;

            case PICK_RIGHT_CORNER:
                view.setActionBarTitle(R.string.map_4_tap_title);
                view.setActionBarSubtitle(R.string.map_on_the_right_corner_subtitle);
                break;

            case FINISH:
                view.setActionBarTitle(R.string.nav_continue);
                view.cleanActionBarSubtitle();
                break;
        }
    }

    public void onNavContinueClick() {
        double leftSideLenght = middleMarkerPos.distanceTo(leftMarkerPos);
        double rightSideLength = middleMarkerPos.distanceTo(rightMarkerPos);

        BuildingMsg buildingMsg = new BuildingMsg(osm_id, buildingAddress, leftSideLenght, rightSideLength);
        MessageHub.put(BuildingMsg.class, buildingMsg);

        Router.onContinueClickInBuildingSidesMap();
    }

    public void onNavUndoClick() {
        switch (state) {

            case PICK_MIDDLE_CORNER:
                state = PickState.PICK_BUILDING;
                updateActionBarTitleAndSubtitle();

                view.dismissCurrentSnackbar(); // dismiss snackbar with building address

                view.setMenuItemEnabled(R.id.nav_undo, false);

                for (int markerId : buildingPointsAsMarkers.keySet()) {
                    view.deleteMarker(markerId);
                }

                buildingPointsAsMarkers.clear();
                break;

            case PICK_LEFT_CORNER:
                state = PickState.PICK_MIDDLE_CORNER;
                updateActionBarTitleAndSubtitle();

                view.resetMarkerToDefault(middleMarkerId);

                break;

            case PICK_RIGHT_CORNER:
                state = PickState.PICK_LEFT_CORNER;
                updateActionBarTitleAndSubtitle();

                view.resetMarkerToDefault(leftMarkerId);

                break;

            case FINISH:
                state = PickState.PICK_RIGHT_CORNER;
                updateActionBarTitleAndSubtitle();

                view.setMenuItemEnabled(R.id.nav_continue, false);
                view.resetMarkerToDefault(rightMarkerId);

                break;
        }
    }

    public void onMapLongClick(double lat, double lon) {
        if (state == PickState.PICK_BUILDING) {
            if (!InternetAccess.isOnline()) {
                //view.showSnackbarText(R.string.map_snack_enable_internet, SnackbarDuration.LONG);
                return;
            }

            if(isQueryingForBuilding) {
                return;
            }

            isQueryingForBuilding = true;

            view.vibrateLongPress();
            view.showSnackbarText(R.string.map_snack_building_search, SnackbarDuration.INDEFINITE);

            disposables.add(
                    GeoCoding
                            .getBuilding(lat, lon)
                            .subscribe(
                                    // onSuccess
                                    way -> disposables.add(
                                            OsmApi
                                                    .getBuildingAddress(way.osm_id)
                                                    .subscribe(address -> {
                                                        view.showSnackbarText(
                                                                R.string.map_snack_cur_building_tip,
                                                                address,
                                                                SnackbarDuration.INDEFINITE
                                                        );

                                                        buildingAddress = address;

                                                        //
                                                        isQueryingForBuilding = false;

                                                        state = PickState.PICK_MIDDLE_CORNER;
                                                        this.osm_id = way.osm_id;

                                                        view.setMenuItemEnabled(R.id.nav_undo, true);
                                                        updateActionBarTitleAndSubtitle();

                                                        //
                                                        if (isFollowBearingEnabled) {
                                                            onToggleFollowBearingClick();
                                                        }

                                                        for (int i = 0; i < way.points.size(); i++) {
                                                            LatLng point = way.points.get(i);
                                                            view.addDefaultMarker(i, point.lat, point.lon);

                                                            buildingPointsAsMarkers.put(i, point);
                                                        }

                                                    })
                                    ),
                                    // onError
                                    t -> {
                                        t.printStackTrace();
                                        throw new Exception(t);
                                    },
                                    // onComplete
                                    () -> {
                                        isQueryingForBuilding = false;

                                        view.dismissCurrentSnackbar(); // dismiss building search snackbar


                                        view.showSnackbarText(
                                                R.string.map_error_no_building_found,
                                                SnackbarDuration.SHORT
                                        );
                                    }
                            )
            );
        }
    }
}
