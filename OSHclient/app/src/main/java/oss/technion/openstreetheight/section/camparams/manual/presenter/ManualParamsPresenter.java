package oss.technion.openstreetheight.section.camparams.manual.presenter;

import com.evernote.android.state.State;

import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.AutosearchMsg;
import oss.technion.openstreetheight.model.camera.params.CameraParams;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.camparams.manual.view.ManualParamsView;


public class ManualParamsPresenter {
    // Fields
    private ManualParamsView view;


    @State String pixelSizeText = "";
    @State String focalLengthText = "";
    @State boolean shouldEnableApprove;



    public ManualParamsPresenter(ManualParamsView view) {
        this.view = view;
    }

    // before onStart, fields of presenter is restored
    public void onStart(boolean isFirstStart) {

        view.showBackButton(Router.isShowBackButton);

        if (isFirstStart) { // no input from user at this point
            float focalLengthMm = -1;
            float pixelSizeMicroM = -1;

            if (CameraParams.getFocalLength() != -1) focalLengthMm = CameraParams.getFocalLength() * 1e3f;
            if (CameraParams.getPixelSize() != -1) pixelSizeMicroM = CameraParams.getPixelSize() * 1e6f;

            if (CameraParams.getFocalLength() != -1 || CameraParams.getPixelSize() != -1) // we have params set before
            {
                view.setApproveButtonEnabled(true);
            }

            // Get message from autosearch
            AutosearchMsg message = MessageHub.get(AutosearchMsg.class);
            if (message != null) {
                if (message.focalLength != -1) focalLengthMm = message.focalLength * 1e3f;
                if (message.pixelSize != -1) pixelSizeMicroM = message.pixelSize * 1e6f;
            }

            if (focalLengthMm != -1) {
                view.setFocalLengthText(String.valueOf(focalLengthMm));
                focalLengthText = String.valueOf(focalLengthMm);
            }

            if (pixelSizeMicroM != -1) {
                view.setPixelSizeText(String.valueOf(pixelSizeMicroM));
                pixelSizeText = String.valueOf(pixelSizeMicroM);
            }


        }


    }


    public void onStop() {

    }

    public void onApproveButtonClick(float focalLengthMm, float pixelSizeMicroM) {
        CameraParams.setLensParams(focalLengthMm * 1e-3f, pixelSizeMicroM * 1e-6f);
        CameraParams.saveToPreferences();

        Router.onManualCamParamsDone();
    }

    public void onSensorSizeTextChanged(String newText) {
        pixelSizeText = newText;
        shouldEnableApprove = isDouble(pixelSizeText) && isDouble(focalLengthText);
        view.setApproveButtonEnabled(shouldEnableApprove);
    }

    public void onFocalLenghtTextChanged(String newText) {
        focalLengthText = newText;
        shouldEnableApprove = isDouble(pixelSizeText) && isDouble(focalLengthText);
        view.setApproveButtonEnabled(shouldEnableApprove);
    }

    private boolean isDouble(String number) {
        try
        {
            Float.parseFloat(number);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }
}
