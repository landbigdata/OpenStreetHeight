package oss.technion.openstreetheight.section.finish.presenter;


import com.evernote.android.state.State;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.BuildingMsg;
import oss.technion.openstreetheight.hub.messages.PhotoCornerPointsMsg;
import oss.technion.openstreetheight.model.camera.params.CameraParams;
import oss.technion.openstreetheight.model.InternetAccess;
import oss.technion.openstreetheight.model.Osm;
import oss.technion.openstreetheight.model.server.MyServer;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.finish.view.FinishView;

public class FinishPresenter {
    private FinishView view;
    private CompositeDisposable disposables = new CompositeDisposable();
    @State double heigthVal = -1;

    private Disposable heightRequestDisposable;
    private Disposable internetControlDisposable;

    public FinishPresenter(FinishView view) {
        this.view = view;
    }

    public void onRepeatClick() {
        Router.onRepeatClickInFinish();
    }

    public void onShareHeightOsmClick() {
        BuildingMsg building = MessageHub.get(BuildingMsg.class);

        disposables.add(
                Osm
                        .getWayTagValue("height", building.osm_id)
                        .map(Double::valueOf)
                        .subscribe(
                                // onSuccess - we have height set already
                                height -> view.makeTwoButtonDialog(
                                        R.string.finish_overwrite_height_title,
                                        R.string.finish_overwrite_height_body_placeholder, height,
                                        R.string.yes,
                                        R.string.cancel
                                )
                                        .setOkAction(() -> disposables.add(
                                                MyServer
                                                        .putHeightToOsm(building.osm_id, heigthVal)
                                                        .subscribe(() -> view.showSnackbarText(R.string.finish_height_sent_to_osm, SnackbarDuration.LONG))
                                                )
                                        )
                                        .show(),

                                // onError
                                Throwable::printStackTrace,

                                // onComplete - no height was set before
                                () -> disposables.add(
                                        MyServer
                                                .putHeightToOsm(building.osm_id, heigthVal)
                                                .subscribe(() -> view.showSnackbarText(R.string.finish_height_sent_to_osm, SnackbarDuration.LONG))
                                )
                        )
        );
    }

    public void onShareHeightTextClick() {
        // TODO
    }

    public void onHowItWorksClick() {
        // TODO
    }

    public void onLicensesClick() {
        view.showAboutActivity();
    }

    public void onStart() {
        view.showBackButton(Router.isShowBackButton);

        if (heigthVal == -1) {
            view.setButtonEnabled(R.id.button_share_height_text, false);
            view.setButtonEnabled(R.id.button_share_height_osm, false);

            internetControlDisposable =
                    InternetAccess.getBehSubject().subscribe(isOnline -> {
                        if (isOnline) {
                            executeHeightRequest();

                            view.dismissCurrentSnackbar();
                        } else {
                            if(heightRequestDisposable != null) heightRequestDisposable.dispose();

                            view.showSnackbarText(R.string.finish_snack_enable_internet, SnackbarDuration.INDEFINITE);
                        }
                    });

            disposables.add(internetControlDisposable);

        } else {
            view.setButtonEnabled(R.id.button_share_height_text, true);
            view.setButtonEnabled(R.id.button_share_height_osm, true);

            view.setHeightText(R.string.finish_meter_short_placeholder, heigthVal);
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
                            this.heigthVal = heightVal;

                            view.setButtonEnabled(R.id.button_share_height_osm, true);
                            view.setButtonEnabled(R.id.button_share_height_text, true);

                            if (internetControlDisposable != null) internetControlDisposable.dispose();
                        });

        disposables.add(heightRequestDisposable);

    }

}
