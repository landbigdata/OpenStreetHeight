package oss.technion.openstreetheight.section.finish.presenter;


import com.evernote.android.state.State;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.BuildingMsg;
import oss.technion.openstreetheight.hub.messages.PhotoCornerPointsMsg;
import oss.technion.openstreetheight.model.OsmAuth;
import oss.technion.openstreetheight.model.camera.params.CameraParams;
import oss.technion.openstreetheight.model.InternetAccess;
import oss.technion.openstreetheight.model.OsmApi;
import oss.technion.openstreetheight.model.server.MyServer;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.ToastDuration;
import oss.technion.openstreetheight.section.finish.view.FinishView;

public class FinishPresenter {
    private final FinishView view;
    private final CompositeDisposable disposables = new CompositeDisposable();
    @State double heightVal = -1;
    @State boolean isHeightSentToOsm;

    private Disposable heightRequestDisposable;
    private Disposable requestInternetControlDisposable;

    public FinishPresenter(FinishView view) {
        this.view = view;
    }

    public void onRepeatClick() {
        Router.finish_onRepeatClick();
    }

    public void onShareHeightOsmClick() {
        if (!OsmAuth.isAuthorized()) {
            view.showToast(R.string.finish_osm_auth_tip, ToastDuration.LONG);

            Router.finish_OnShareHeightOsmNonAuthClick();
            return;
        }

        BuildingMsg building = MessageHub.get(BuildingMsg.class);

        disposables.add(
                OsmApi
                        .getWayTagValue("height", building.osm_id)
                        .map(Double::valueOf)
                        .subscribe(
                                // onSuccess - we have height set already
                                oldHeight -> view.makeTwoButtonDialog(
                                        R.string.finish_overwrite_height_title,
                                        R.string.finish_overwrite_height_body_placeholder, new Object[]{building.address, oldHeight, heightVal},
                                        R.string.yes,
                                        R.string.cancel
                                )
                                        .setOkAction(() -> disposables.add(
                                                OsmApi
                                                        .changeBuildingHeightTag(building.osm_id, heightVal)
                                                        .subscribe(() -> {
                                                            isHeightSentToOsm = true;
                                                            view.setButtonEnabled(R.id.button_share_height_osm, false);

                                                            view.showSnackbarText(R.string.finish_height_sent_to_osm, SnackbarDuration.LONG);

                                                        })
                                                )
                                        )
                                        .show(),

                                // onError
                                Throwable::printStackTrace,

                                // onComplete - no height was set before
                                () -> disposables.add(
                                        OsmApi
                                                .changeBuildingHeightTag(building.osm_id, heightVal)
                                                .subscribe(() -> {
                                                    isHeightSentToOsm = true;
                                                    view.setButtonEnabled(R.id.button_share_height_osm, false);

                                                    view.showSnackbarText(R.string.finish_height_sent_to_osm, SnackbarDuration.LONG);
                                                })
                                )
                        )
        );
    }

    public void onShareHeightTextClick() {
        // TODO
    }


    public void onStart() {
        view.showBackButton(Router.isShowBackButton);

        if (isHeightSentToOsm) {
            view.setButtonEnabled(R.id.button_share_height_osm, false);
        }

        // Once we have height calculated, we set buttons enabled according to Internet connection
        disposables.add(
                InternetAccess.getBehSubject()

                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())

                        .subscribe(isOnline -> {
                            if (heightVal != -1) {
                                view.setButtonEnabled(R.id.button_share_height_friends, isOnline);

                                if (!isHeightSentToOsm) {
                                    view.setButtonEnabled(R.id.button_share_height_osm, isOnline);
                                }
                            }
                        })
        );

        if (heightVal == -1) { // height was not yet calculated
            view.setButtonEnabled(R.id.button_share_height_friends, false);
            view.setButtonEnabled(R.id.button_share_height_osm, false);

            requestInternetControlDisposable =
                    InternetAccess.getBehSubject()

                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())

                            .subscribe(isOnline -> {

                                if (isOnline) {
                                    executeHeightRequest();

                                    //view.dismissCurrentSnackbar();
                                } else {
                                    if (heightRequestDisposable != null) {
                                        heightRequestDisposable.dispose();

                                    }


                                    //view.showSnackbarText(R.string.finish_snack_enable_internet, SnackbarDuration.INDEFINITE);
                                }
                            });

            disposables.add(requestInternetControlDisposable);


        } else { // height was already calculated
            view.replaceProgressBarWithHeightText();
            view.setHeightText(R.string.finish_meter_short_placeholder, heightVal);
        }

    }

    public void onStop() {
        disposables.clear();
        view.dismissCurrentSnackbar();
    }

    private void executeHeightRequest() {
        BuildingMsg building = MessageHub.get(BuildingMsg.class);

        PhotoCornerPointsMsg cornerPointsMsg = MessageHub.get(PhotoCornerPointsMsg.class);

        heightRequestDisposable =
                MyServer.getHeight(
                        CameraParams.getFocalLength(),
                        CameraParams.getPixelSize(),
                        CameraParams.getImageWidth(),
                        CameraParams.getImageHeight(),
                        cornerPointsMsg.points,
                        building.leftSide,
                        building.rightSide
                )
                        .subscribe(heightVal -> {
                            view.setHeightText(R.string.finish_meter_short_placeholder, heightVal);
                            this.heightVal = heightVal;

                            view.setButtonEnabled(R.id.button_share_height_osm, true);
                            view.setButtonEnabled(R.id.button_share_height_friends, true);

                            view.replaceProgressBarWithHeightText();

                            if (requestInternetControlDisposable != null)
                                requestInternetControlDisposable.dispose();
                        });

        disposables.add(heightRequestDisposable);

    }

}
