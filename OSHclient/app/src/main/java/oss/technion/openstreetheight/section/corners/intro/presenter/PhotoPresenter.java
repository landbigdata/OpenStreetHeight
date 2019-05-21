package oss.technion.openstreetheight.section.corners.intro.presenter;

import com.evernote.android.state.State;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.CameraParamsSetMsg;
import oss.technion.openstreetheight.model.InternetAccess;
import oss.technion.openstreetheight.model.PhoneParams;
import oss.technion.openstreetheight.model.camera.params.CameraParams;
import oss.technion.openstreetheight.model.camera.params.LensFetcher;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.hub.messages.PhotoMsg;
import oss.technion.openstreetheight.section.SnackbarDuration;
import oss.technion.openstreetheight.section.corners.intro.view.PhotoView;


public class PhotoPresenter {
    private PhotoView view;
    private CompositeDisposable disposables = new CompositeDisposable();

    private Disposable lensFetcherDisposable;
    private Disposable internetFillerDisposable;

    @State
    boolean isSetParamsDialogShowing;

    public PhotoPresenter(PhotoView view) {
        this.view = view;
    }

    public void onStart() {
        view.showBackButton(Router.isShowBackButton);

        if (CameraParams.getPixelSize() == -1 && CameraParams.getFocalLength() == -1) {
            view.setMakePhotoButtonEnabled(false);
            view.setCameraParamsButtonEnabled(false);
        }

        disposables.add(
                MessageHub.signal
                        .filter(t -> t.type == CameraParamsSetMsg.class)
                        .subscribe(__ -> {
                            view.setMakePhotoButtonEnabled(true);
                            view.setCameraParamsButtonEnabled(true);
                            isSetParamsDialogShowing = false;

                            if (internetFillerDisposable != null)
                                internetFillerDisposable.dispose();
                        })
        );

        if (CameraParams.getPixelSize() == -1 && CameraParams.getFocalLength() == -1 && !isSetParamsDialogShowing) {
            internetFillerDisposable =
                    InternetAccess.getBehSubject()
                            .subscribe(isOnline -> {
                                if (isOnline) {
                                    execLensRequest();


                                    view.dismissCurrentSnackbar(); // it could be previously on
                                } else {
                                    if (lensFetcherDisposable != null)
                                        lensFetcherDisposable.dispose();


                                    view.showSnackbarText(R.string.photo_snack_enable_internet, SnackbarDuration.INDEFINITE);
                                }


                            });

            disposables.add(internetFillerDisposable);

        }


    }

    public void onSetCameraParamsButtonClick() {
        showCameraParamsDialog();
    }


    private void execLensRequest() {

        lensFetcherDisposable = LensFetcher
                .getLensParams(PhoneParams.MODEL)
                .subscribe(
                        t -> {
                            CameraParams.setLensParams(t.focalLenght, t.pixelSize);

                            view.setMakePhotoButtonEnabled(true);
                            view.setCameraParamsButtonEnabled(true);
                        },
                        e -> e.printStackTrace(),
                        () -> showCameraParamsDialog()

                );

        disposables.add(lensFetcherDisposable);

    }

    private void showCameraParamsDialog() {
        view.showCameraParamsDialog();

        isSetParamsDialogShowing = true;
    }

    public void onStop() {
        disposables.clear();
    }


    public void onMakeShotButtonClick() {
        view.showCameraActivity();
    }

    public void onPhotoObtained(String path, int height, int width) {
        CameraParams.setImageParams(width, height);

        PhotoMsg photoMsg = new PhotoMsg(path);
        MessageHub.put(PhotoMsg.class, photoMsg);

        Router.onPhotoShotInPhoto();
    }
}
