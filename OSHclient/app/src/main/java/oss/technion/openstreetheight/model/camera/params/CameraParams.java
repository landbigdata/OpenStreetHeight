package oss.technion.openstreetheight.model.camera.params;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CameraParams {
    private static double focalLenght = -1;
    private static double pixelSize = -1;

    private static int imageHeight = -1;
    private static int imageWidth = -1;

    private static SharedPreferences prefs;


    // measured in metres
    public static double getPixelSize() {
        return pixelSize;
    }

    // measured in metres
    public static double getFocalLength() {
        return focalLenght;
    }

    public static int getImageHeight() {
        return imageHeight;
    }

    public static int getImageWidth() {
        return imageWidth;
    }

    public static void setLensParams(double focalLength, double pixelSize) {
        CameraParams.focalLenght = focalLength;
        CameraParams.pixelSize = pixelSize;
    }

    public static void setImageParams(int imageWidth, int imageHeight) {
        CameraParams.imageHeight = imageHeight;
        CameraParams.imageWidth = imageWidth;
    }



    public static void initialize(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveToPreferences() {
        prefs
                .edit()
                .putFloat("focalLength", (float) focalLenght)
                .putFloat("pixelSize", (float) pixelSize)
                .putInt("imageWidth", imageWidth)
                .putInt("imageHeight", imageHeight)
                .commit();
    }

    public static void restoreFromPreferences() {
        focalLenght = prefs.getFloat("focalLength", 0);
        pixelSize = prefs.getFloat("pixelSize", 0);
        imageWidth = prefs.getInt("imageWidth", 0);
        imageHeight = prefs.getInt("imageHeight", 0);
    }
}
