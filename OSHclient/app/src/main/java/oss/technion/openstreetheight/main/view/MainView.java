package oss.technion.openstreetheight.main.view;

import androidx.annotation.StringRes;

import oss.technion.openstreetheight.section.SnackbarDuration;

public interface MainView {
    void showSnackbarText(@StringRes int text, SnackbarDuration type);
    void dismissCurrentSnackbar();
}
