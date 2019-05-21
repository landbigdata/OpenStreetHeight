package oss.technion.openstreetheight.section.corners.intro.dialog.presenter;

import com.evernote.android.state.State;

import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.CameraParamsSetMsg;
import oss.technion.openstreetheight.section.corners.intro.dialog.view.CameraDialogView;
import oss.technion.openstreetheight.model.camera.params.CameraParams;
import oss.technion.openstreetheight.mvp.Router;
import io.reactivex.disposables.CompositeDisposable;

public class CameraDialogPresenter {
    private CameraDialogView view;
    private CompositeDisposable disposables = new CompositeDisposable();

    @State String sensorSizeText = "";
    @State String focalLengthText = "";

    @State boolean isFocalLenghtButtonEnabled;

    public CameraDialogPresenter(CameraDialogView view) {
        this.view = view;
    }

    // assuming that internet is present
    public void onStart(boolean isFirstStart) {

        if (isFirstStart) {
            if (CameraParams.getFocalLength() != -1 && CameraParams.getPixelSize() != -1)
            {
                double pixelSizeMicroM = CameraParams.getPixelSize() * 1e6;
                double focalLengthMm = CameraParams.getFocalLength() * 1e3;

                view.setFocalLengthText(focalLengthMm);
                view.setPixelSizeText(pixelSizeMicroM);

                view.setApproveButtonEnabled(true);
            } else {
                view.setApproveButtonEnabled(false);
            }
        } else {
            view.setApproveButtonEnabled(isFocalLenghtButtonEnabled);
        }


    }

    public void onStop() {
        disposables.clear();
    }

    public void onApproveButtonClick(double focalLengthMm, double pixelSizeMicroM) {
        CameraParams.setLensParams(focalLengthMm * 1e-3, pixelSizeMicroM * 1e-6);

        MessageHub.sendSignal(CameraParamsSetMsg.class, new CameraParamsSetMsg());
    }

    public void onExitButtonClick() {
        Router.onCameraDialogExitButtonClick();
    }

    public void onSensorSizeTextChanged(String newText) {
        sensorSizeText = newText;

        isFocalLenghtButtonEnabled = isDouble(sensorSizeText) && isDouble(focalLengthText);

        view.setApproveButtonEnabled(isFocalLenghtButtonEnabled);
    }

    public void onFocalLenghtTextChanged(String newText) {
        focalLengthText = newText;

        isFocalLenghtButtonEnabled = isDouble(sensorSizeText) && isDouble(focalLengthText);

        view.setApproveButtonEnabled(isFocalLenghtButtonEnabled);
    }

    private boolean isDouble(String number) {
        try
        {
            Double.parseDouble(number);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }
}
