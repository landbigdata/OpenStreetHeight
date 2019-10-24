package oss.technion.openstreetheight.section.photo.presenter;

import io.reactivex.disposables.CompositeDisposable;
import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.PhotoMsg;
import oss.technion.openstreetheight.model.camera.params.CameraParams;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.photo.view.PhotoView;


public class PhotoPresenter {
    // Fields
    private PhotoView view;
    private CompositeDisposable disposables = new CompositeDisposable();


    // Methods
    public PhotoPresenter(PhotoView view) {
        this.view = view;
    }

    public void onStart() {
        view.showBackButton(Router.isShowBackButton);
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
