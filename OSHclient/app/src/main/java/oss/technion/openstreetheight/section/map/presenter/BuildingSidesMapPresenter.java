package oss.technion.openstreetheight.section.map.presenter;



import com.evernote.android.state.State;

import java.util.HashMap;
import java.util.Map;

import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.BuildingMsg;
import oss.technion.openstreetheight.model.InternetAccess;
import oss.technion.openstreetheight.model.PhoneParams;
import oss.technion.openstreetheight.model.compass.Compass;
import oss.technion.openstreetheight.model.FusedLocation;
import oss.technion.openstreetheight.model.geo.GeoCoding;
import oss.technion.openstreetheight.model.data.LatLng;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.map.view.BuildingSidesMapView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BuildingSidesMapPresenter {
    enum PickState {PICK_BUILDING, PICK_MIDDLE_CORNER, PICK_LEFT_CORNER, PICK_RIGHT_CORNER, FINISH}

    private BuildingSidesMapView view;
    private CompositeDisposable disposables = new CompositeDisposable();

    @State
    PickState state;

    @State
    LatLng leftMarkerPos;
    @State
    LatLng rightMarkerPos;
    @State
    LatLng middleMarkerPos;

    @State
    int leftMarkerId;
    @State
    int rightMarkerId;
    @State
    int middleMarkerId;

    @State
    HashMap<Integer, LatLng> buildingPointsAsMarkers = new HashMap<>();
    @State
    long osm_id;

    @State
    boolean isFollowBearingEnabled;

    @State
    boolean isLocationSettingsSatisfied;
    @State
    boolean isLocationPermissionSatisfied;

    public BuildingSidesMapPresenter(BuildingSidesMapView view) {
        this.view = view;
    }

    public void onStart(boolean shouldRestoreViewState) {
        view.showBackButton(Router.isShowBackButton);


        if (shouldRestoreViewState) {
            restoreViewState();
        } else {
            state = PickState.PICK_BUILDING;
            updateActionBarTitleAndSubtitle();

            view.checkLocationSettings();
            view.checkLocationPermission();

            LatLng losAngles4Seasons = new LatLng(34.066814, -118.4009591);
            view.moveMapTo(losAngles4Seasons, 18);

            // isFollowBearingEnabled = true; <---- Set to true when both geolocation settings and permission satisfied
        }

        if (FusedLocation.getLastLocation() == null) {
            view.setMenuItemEnabled(R.id.toggle_follow_bearing, false);

            disposables.add(
                    FusedLocation
                            .getBehSubject()
                            .firstElement()
                            .subscribe(firstFix -> view.setMenuItemEnabled(R.id.toggle_follow_bearing, true))
            );
        }

        disposables.add(
                Compass
                        .getAccuracySubject()
                        .subscribe(accuracy -> {
                            switch (accuracy) {
                                case LOW:
                                    view.showSnackbarText(R.string.map_snack_compass_accuracy_low, SnackbarDuration.INDEFINITE);
                                    break;

                                case UNRELIABLE:
                                    view.showSnackbarText(R.string.map_snack_compass_accuracy_unreliable, SnackbarDuration.INDEFINITE);
                                    break;
                            }
                        })
        );

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
                                    if (Compass.getAccuracy() == Compass.Accuracy.HIGH || Compass.getAccuracy() == Compass.Accuracy.MEDIUM) {
                                        view.rotateMapAroundImmed(
                                                FusedLocation.getLastLocation(),
                                                azimuth.floatValue()
                                        );
                                    }
                                }
                        )
        );
    }

    public void onLocationSettingsSatisfied() {
        isLocationSettingsSatisfied = true;

        if (isLocationSettingsSatisfied && isLocationPermissionSatisfied) {
            view.setMyLocationMarkerEnabled();

            // we may not have fix at this point of time
            disposables.add(
                    FusedLocation
                            .getBehSubject()
                            .firstElement()
                            .subscribe(firstFix -> {
                                isFollowBearingEnabled = true;

                                view.changeTogglebearingDrawable(R.drawable.ic_navigation_on_24dp);
                                view.showSnackbarText(R.string.map_snack_disable_follow_bearing, SnackbarDuration.LONG);

                                Compass.enable();
                            })
            );
        }
    }

    public void onLocationPermissionSatisfied() {
        isLocationPermissionSatisfied = true;

        FusedLocation.enable();
        view.setMyLocationMarkerEnabled();

        if (isLocationSettingsSatisfied && isLocationPermissionSatisfied) {
            view.setMyLocationMarkerEnabled();

            // we may not have fix at this point of time
            disposables.add(
                    FusedLocation
                            .getBehSubject()
                            .firstElement()
                            .subscribe(firstFix -> {
                                isFollowBearingEnabled = true;

                                view.changeTogglebearingDrawable(R.drawable.ic_navigation_on_24dp);
                                view.showSnackbarText(R.string.map_snack_disable_follow_bearing, SnackbarDuration.LONG);

                                Compass.enable();
                            })
            );
        }
    }

    public void onPause() {
        Compass.disable();
        FusedLocation.disable();
    }

    public void onResume(boolean wasPausedBefore) {
        if (wasPausedBefore) {
            if (isLocationPermissionSatisfied && isLocationSettingsSatisfied) {
                FusedLocation.enable();
            }

            if (isFollowBearingEnabled) {
                Compass.enable();
            }
        }
    }

    public void onStop() {
        disposables.clear();

        view.cleanActionBarSubtitle();
        view.dismissCurrentSnackbar();
    }

    public void onToggleFollowBearingClick() {
        if (!isLocationSettingsSatisfied || !isLocationPermissionSatisfied) {
            view.checkLocationSettings();
            view.checkLocationPermission();
            return;
        }

        isFollowBearingEnabled = !isFollowBearingEnabled;

        if (isFollowBearingEnabled) {
            view.changeTogglebearingDrawable(R.drawable.ic_navigation_on_24dp);

            view.moveMapTo(FusedLocation.getLastLocation());
            Compass.enable();

            switch (Compass.getAccuracy()) {
                case LOW:
                    view.showSnackbarText(R.string.map_snack_compass_accuracy_low, SnackbarDuration.INDEFINITE);
                    break;

                case UNRELIABLE:
                    view.showSnackbarText(R.string.map_snack_compass_accuracy_unreliable, SnackbarDuration.INDEFINITE);
                    break;
            }
        } else {
            view.changeTogglebearingDrawable(R.drawable.ic_navigation_off_24dp);
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
            view.changeTogglebearingDrawable(R.drawable.ic_navigation_on_24dp);

            Compass.enable();

            switch (Compass.getAccuracy()) {
                case LOW:
                    view.showSnackbarText(R.string.map_snack_compass_accuracy_low, SnackbarDuration.INDEFINITE);
                    break;

                case UNRELIABLE:
                    view.showSnackbarText(R.string.map_snack_compass_accuracy_unreliable, SnackbarDuration.INDEFINITE);
                    break;
            }
        }

        if (isLocationPermissionSatisfied) {
            view.setMyLocationMarkerEnabled();
            FusedLocation.enable();
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

        BuildingMsg buildingMsg = new BuildingMsg(osm_id, leftSideLenght, rightSideLength);
        MessageHub.put(BuildingMsg.class, buildingMsg);

        Router.onContinueClickInBuildingSidesMap();
    }

    public void onNavUndoClick() {
        switch (state) {

            case PICK_MIDDLE_CORNER:
                state = PickState.PICK_BUILDING;
                updateActionBarTitleAndSubtitle();

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
                view.showSnackbarText(R.string.map_snack_enable_internet, SnackbarDuration.LONG);
                return;
            }

            disposables.add(
                    GeoCoding
                            .getBuilding(lat, lon)
                            .subscribe(
                                    // onSuccess
                                    way -> {
                                        state = PickState.PICK_MIDDLE_CORNER;
                                        updateActionBarTitleAndSubtitle();

                                        this.osm_id = way.osm_id;

                                        view.setMenuItemEnabled(R.id.nav_undo, true);

                                        if (isFollowBearingEnabled) {
                                            onToggleFollowBearingClick();
                                        }

                                        for (int i = 0; i < way.points.size(); i++) {
                                            LatLng point = way.points.get(i);
                                            view.addDefaultMarker(i, point.lat, point.lon);

                                            buildingPointsAsMarkers.put(i, point);
                                        }
                                    },
                                    // onError
                                    t -> {
                                        throw new Exception(t);
                                    },
                                    // onComplete
                                    () -> view.showSnackbarText(
                                            R.string.map_error_no_building_found,
                                            SnackbarDuration.SHORT
                                    )
                            )
            );
        }
    }
}
