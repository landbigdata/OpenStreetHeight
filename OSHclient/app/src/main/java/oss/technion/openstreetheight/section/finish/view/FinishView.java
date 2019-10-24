package oss.technion.openstreetheight.section.finish.view;


import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import oss.technion.openstreetheight.func.Action;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.ToastDuration;

public interface FinishView {

    interface Dialog {
        Dialog setOkAction(Action action);

        Dialog setCancelAction(Action action);

        void show();
    }

    void replaceProgressBarWithHeightText();

    void setHeightText(@StringRes int heightText, double heightVal);

    void showSnackbarText(@StringRes int text, SnackbarDuration type);

    void setButtonEnabled(@IdRes int button, boolean enabled);

    void dismissCurrentSnackbar();

    void showBackButton(boolean isShowBackButton);

    void showToast(int string_id, ToastDuration dur);

    Dialog makeTwoButtonDialog(
            @StringRes int title,
            @StringRes int message, Object[] args,
            @StringRes int button_yes,
            @StringRes int button_cancel
    );
}
