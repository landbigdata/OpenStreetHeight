package oss.technion.openstreetheight.section.corners.intro.view;


import androidx.annotation.StringRes;
import oss.technion.openstreetheight.section.SnackbarDuration;

public interface PhotoView {
    void showCameraActivity();

    void showBackButton(boolean isShowBackButton);

    void showCameraParamsDialog();

    void dismissCurrentSnackbar();

    void showSnackbarText(@StringRes int res, SnackbarDuration indefinite);

    void setMakePhotoButtonEnabled(boolean enabled);

    void setCameraParamsButtonEnabled(boolean enabled);
}
