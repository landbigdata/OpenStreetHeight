package oss.technion.openstreetheight.section.finish.view;


import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.evernote.android.state.StateSaver;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.franmontiel.attributionpresenter.AttributionPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.func.Action;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.finish.presenter.FinishPresenter;

public class FinishFragment extends Fragment implements FinishView {
    private FinishPresenter presenter = new FinishPresenter(this);

    private ActionBar actionBar;
    private Snackbar curSnackbar;

    private static class DialogImpl implements Dialog {
        private AlertDialog dialog;
        private Action okAction;
        private Action cancelAction;

        public DialogImpl() {
        }

        @Override
        public Dialog setOkAction(Action action) {
            this.okAction = action;
            return this;
        }

        @Override
        public Dialog setCancelAction(Action action) {
            this.cancelAction = action;
            return this;
        }

        @Override
        public void show() {
            dialog.show();
        }

        public DialogInterface.OnClickListener onOkClick = (__, ___) -> {
            if (okAction != null) okAction.run();
        };

        public DialogInterface.OnClickListener onCancelClick = (__, ___) -> {
            if (cancelAction != null) cancelAction.run();
        };

        public void setDialog(AlertDialog dialog) {
            this.dialog = dialog;
        }
    }

    @BindView(R.id.finish_height) TextView heightText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_finish, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        actionBar.setTitle("");


        StateSaver.restoreInstanceState(presenter, savedInstanceState);
        presenter.onStart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        StateSaver.saveInstanceState(presenter, outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.onStop();
    }

    @OnClick(R.id.button_share_height_osm) void onShareHeightOsmClick() {
        presenter.onShareHeightOsmClick();
    }

    @OnClick(R.id.button_share_height_text) void onShareHeightTextClick() {
        presenter.onShareHeightTextClick();
    }

    @OnClick(R.id.button_how_it_works) void onHowItWorksClick() {
        presenter.onHowItWorksClick();
    }

    @OnClick(R.id.button_repeat) void onRepeatClick() {
        presenter.onRepeatClick();
    }

    @OnClick(R.id.button_licenses) void onLicensesClick() {
        presenter.onLicensesClick();
    }


    @Override
    public void showBackButton(boolean isShowBackButton) {
        actionBar.setDisplayShowHomeEnabled(isShowBackButton);
        actionBar.setDisplayHomeAsUpEnabled(isShowBackButton);
    }


    @Override
    public void setHeightText(@StringRes int placeholder, double heightVal) {
        heightText.setText(getString(placeholder, heightVal));
    }

    @Override
    public void showSnackbarText(@StringRes int text, SnackbarDuration type) {
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

        curSnackbar =  Snackbar
                .make(getView(), text, snackbarType);

        curSnackbar.show();
    }



    @Override
    public void setButtonEnabled(@IdRes int button, boolean enabled) {
        Button buttonView = getView().findViewById(button);

        buttonView.setEnabled(enabled);
        if (buttonView.getCompoundDrawables()[0] != null) {
            // handle case of button with left drawable
            Drawable leftIcon = buttonView.getCompoundDrawables()[0];
            leftIcon.setAlpha(enabled ? 255 : 64);
            buttonView.setCompoundDrawables(leftIcon, null, null, null);
        }
    }

    @Override
    public void showAboutActivity() {
        ListAdapter adapter = new AttributionPresenter.Builder(getContext())
                .addAttributions(LicenseProvider.getAttributions())
                .build()
                .getAdapter();

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.finish_licenses)
                .setPositiveButton(android.R.string.ok, (__, ___) -> {})
                .setAdapter(adapter, null)
                .show();
    }

    @Override
    public void dismissCurrentSnackbar() {
        if (curSnackbar != null) {
            curSnackbar.dismiss();
        }
    }

    @Override
    public Dialog makeTwoButtonDialog(@StringRes int title, @StringRes int message, Object messageArg, @StringRes int button_yes, @StringRes int button_cancel) {
        DialogImpl dialogImpl = new DialogImpl();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(String.format(getString(message), messageArg))
                .setPositiveButton(button_yes, dialogImpl.onOkClick)
                .setNegativeButton(button_cancel, dialogImpl.onCancelClick)
                .create();

        dialogImpl.setDialog(dialog);

        return dialogImpl;
    }

}
