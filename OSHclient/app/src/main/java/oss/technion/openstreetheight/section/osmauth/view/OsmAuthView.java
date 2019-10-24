package oss.technion.openstreetheight.section.osmauth.view;

import androidx.annotation.StringRes;

import oss.technion.openstreetheight.section.SnackbarDuration;

public interface OsmAuthView {
    void showBackButton(boolean isShowBackButton);
    void loadUrl(String url);
    void showSuccessText();
    void clearWebviewCookies();

    void setApproveButtonEnabled(boolean b);

    void setStatusTextAuth(String displayName);
    void setStatusTextNonAuth();

    void showSnackbarText(@StringRes int text, SnackbarDuration type);

    void setSkipButtonVisibility(boolean isInCamParamSetMode);

    void authStatusShortBlink();
}
