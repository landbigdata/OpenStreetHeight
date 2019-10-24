package oss.technion.openstreetheight.section.camparams.autosearch.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.AutosearchMsg;
import oss.technion.openstreetheight.model.InternetAccess;
import oss.technion.openstreetheight.model.PhoneParams;
import oss.technion.openstreetheight.model.camera.params.LensFetcher;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.camparams.autosearch.view.AutosearchView;

public class AutosearchPresenter {
    private AutosearchView view;

    private CompositeDisposable disposables = new CompositeDisposable();
    private Disposable lensRequest;

    public AutosearchPresenter(AutosearchView view) {
        this.view = view;
    }

    public void onStart() {
        // Check Internet presence

        if (InternetAccess.isOnline()) {
            execLensRequest();
        } else {
            // toggle sections
            view.showSectionWait(false);
            view.showSectionRetry(true);

            // Listen for network change
            disposables.add(
                    InternetAccess.getBehSubject()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(isOnline -> {
                                if (isOnline) {
                                    // Fetch lens params
                                    execLensRequest();

                                    // toggle sections
                                    view.showSectionWait(true);
                                    view.showSectionRetry(false);
                                } else {
                                    // Cancel lens request
                                    lensRequest.dispose();

                                    // toggle sections
                                    view.showSectionWait(false);
                                    view.showSectionRetry(true);
                                }
                            })
            );
        }


    }

    private void execLensRequest() {

        lensRequest = LensFetcher.getLensParams(PhoneParams.MODEL)
                .subscribe(
                        t -> {
                            AutosearchMsg message = new AutosearchMsg(t.focalLenght, t.pixelSize);
                            MessageHub.put(AutosearchMsg.class, message);
                            Router.onAutoCamParamsContinue();
                        },
                        Throwable::printStackTrace
                );

        disposables.add(lensRequest);
    }

    public void onStop() {
        disposables.clear();
    }
}
