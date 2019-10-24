package oss.technion.openstreetheight.main.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.main.view.MainView;
import oss.technion.openstreetheight.model.InternetAccess;
import oss.technion.openstreetheight.section.SnackbarDuration;

public class MainPresenter {
    private final MainView view;
    private CompositeDisposable disposables = new CompositeDisposable();

    public MainPresenter(MainView view) {
        this.view = view;
    }

    public void onStart() {
        // As this app is completely dependant on Internet (OsmAuth, Building Picker, Height Calc, Osm Submission),
        // MainActivity is in charge of showing internet pop-up

        disposables.add(
                InternetAccess.getBehSubject() // immediate action
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isOnline -> {
                            if (isOnline) {
                                view.dismissCurrentSnackbar();
                            } else {
                                view.showSnackbarText(R.string.main_internet_tip, SnackbarDuration.INDEFINITE);
                            }
                        })
        );

    }

    public void onStop() {
        disposables.clear();
    }
}
