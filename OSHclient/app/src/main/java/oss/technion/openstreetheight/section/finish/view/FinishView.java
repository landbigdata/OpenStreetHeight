package oss.technion.openstreetheight.section.finish.view;


import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import oss.technion.openstreetheight.func.Action;
import oss.technion.openstreetheight.section.SnackbarDuration;

public interface FinishView {
    void showBackButton(boolean isShowBackButton);

    interface Dialog {
        Dialog setOkAction(Action action);

        Dialog setCancelAction(Action action);

        void show();
    }


    void setHeightText(@StringRes int heightText, double heightVal);

    void showSnackbarText(@StringRes int text, SnackbarDuration type);

    void setButtonEnabled(@IdRes int button, boolean enabled);

    void showAboutActivity();

    void dismissCurrentSnackbar();

    Dialog makeTwoButtonDialog(
            @StringRes int title,
            @StringRes int message, Object arg,
            @StringRes int button_yes,
            @StringRes int button_cancel
    );
}
