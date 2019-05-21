package oss.technion.openstreetheight.model;

import android.content.Context;
import android.os.Build;
import android.view.WindowManager;

public class PhoneParams {
    public static String MODEL = Build.MODEL;
    private static Context context;

    public enum Rotation {ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270}


    public static void initialize(Context context) {
        PhoneParams.context = context;
    }

    public static Rotation getRotation() {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        // see Surface.ROTATION_0, Surface.ROTATION_90 etc.
        return Rotation.values()[windowManager.getDefaultDisplay().getRotation()];
    }

}
