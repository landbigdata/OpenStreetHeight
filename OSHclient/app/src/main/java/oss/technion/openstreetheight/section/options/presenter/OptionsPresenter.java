package oss.technion.openstreetheight.section.options.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.model.InternetAccess;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.options.view.OptionsView;

public class OptionsPresenter {

    private final OptionsView view;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public OptionsPresenter(OptionsView view) {
        this.view = view;
    }

    public void onStart() {
        disposables.add(
                InternetAccess.getBehSubject() // immediate action
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::setOsmAuthButtonEnabled)
        );
    }

    public void onStop() {
        disposables.clear();
    }

    public void onAboutClick() {
        // TODO
    }

    public void onHowItWorksClick() {
        // TODO
    }

    public void onLicensesClick() {
        view.showLicenses();
    }

    public void onCamParamsClick() {
        Router.Options.onCamParamsClick();
    }

    public void onOsmAuthClick() {
        Router.Options.onOsmAuthClick();
    }

    public void onMeasureClick() {
        Router.Options.onMeasureClick();
    }
}
