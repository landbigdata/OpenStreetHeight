package oss.technion.openstreetheight.section.corners.intro.dialog.view;

public interface CameraDialogView {
    void setPixelSizeText(double sensorSize);

    void setFocalLengthText(double focalLength);

    void setApproveButtonEnabled(boolean enabled);
}
