package oss.technion.openstreetheight.model.camera.params;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CameraParams {
    private static float focalLenght = -1;
    private static float pixelSize = -1;

    private static int imageHeight = -1;
    private static int imageWidth = -1;

    private static SharedPreferences prefs;


    // measured in metres
    public static float getPixelSize() {
        return pixelSize;
    }

    // measured in metres
    public static float getFocalLength() {
        return focalLenght;
    }

    public static int getImageHeight() {
        return imageHeight;
    }

    public static int getImageWidth() {
        return imageWidth;
    }

    public static void setLensParams(float focalLength, float pixelSize) {
        CameraParams.focalLenght = focalLength;
        CameraParams.pixelSize = pixelSize;
    }

    public static void setImageParams(int imageWidth, int imageHeight) {
        CameraParams.imageHeight = imageHeight;
        CameraParams.imageWidth = imageWidth;
    }



    public static void initialize(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        restoreFromPreferences();
    }

    public static void saveToPreferences() {
        prefs
                .edit()
                .putFloat("focalLength", focalLenght)
                .putFloat("pixelSize", pixelSize)
                .putInt("imageWidth", imageWidth)
                .putInt("imageHeight", imageHeight)
                .apply();
    }

    public static void restoreFromPreferences() {
        focalLenght = prefs.getFloat("focalLength", -1);
        pixelSize = prefs.getFloat("pixelSize", -1);
        imageWidth = prefs.getInt("imageWidth", -1);
        imageHeight = prefs.getInt("imageHeight", -1);
    }
}
