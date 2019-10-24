package oss.technion.openstreetheight.model;

import oss.technion.openstreetheight.model.camera.params.CameraParams;

public class FirstLaunch {
    public static boolean isInCamParamSetMode = CameraParams.getFocalLength() == -1 && CameraParams.getPixelSize() == -1;
}
