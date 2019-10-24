package oss.technion.openstreetheight.section.camparams.manual.view;


public interface ManualParamsView {
    void setApproveButtonEnabled(boolean enabled);
    void setFocalLengthText(String focalLengthMm);
    void setPixelSizeText(String pixelSizeMicroM);
    void showBackButton(boolean isShowBackButton);
}
