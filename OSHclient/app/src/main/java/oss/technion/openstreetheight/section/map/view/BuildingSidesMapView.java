package oss.technion.openstreetheight.section.map.view;


import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import oss.technion.openstreetheight.model.data.LatLng;
import oss.technion.openstreetheight.section.SnackbarDuration;

public interface BuildingSidesMapView {
    void addDefaultMarker(int id, double lat, double lon);

    void showSnackbarText(@StringRes int text, SnackbarDuration type);

    void setActionBarTitle(@StringRes int text);

    void setActionBarSubtitle(@StringRes int text);

    void cleanActionBarSubtitle();

    void setMarkerText(int id, @StringRes int text);

    void setMenuItemEnabled(@IdRes int id, boolean enabled);

    void resetMarkerToDefault(int id);

    void deleteMarker(int markerId);

    void changeTogglebearingDrawable(@DrawableRes int ic_navigation_on_24dp);

    void moveMapTo(LatLng location, float zoom);

    void moveMapTo(LatLng location);

    void checkLocationPermission();

    void checkLocationSettings();

    void setMyLocationMarkerEnabled();

    void dismissCurrentSnackbar();

    void rotateMapAroundImmed(LatLng lastLocation, float bearing);

    void showBackButton(boolean isShowBackButton);
}
