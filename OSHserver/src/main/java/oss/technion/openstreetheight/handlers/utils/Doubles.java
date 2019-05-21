package oss.technion.openstreetheight.handlers.utils;

import java.util.Arrays;

public class Doubles {
    public static double[][] unbox2d(Object array) {
        Object[] objArray = (Object[]) array;

        int n = objArray.length;
        int m = ((Object[]) objArray[0]).length;

        double[][] output = new double[n][m];

        for (int i = 0; i < n; i++) {
            Object[] objInnerArray = (Object[]) objArray[i];
            Double[] doubleObjArray = Arrays.copyOf(objInnerArray, objInnerArray.length, Double[].class);

            for (int j = 0; j < m; j++) {
                output[i][j] = doubleObjArray[j];
            }
        }

        return output;
    }

    public static double[] unbox1d(Object array) {
        Object[] objArray = (Object[]) array;

        int n = objArray.length;

        double[] output = new double[n];

        Double[] doubleObjArray = Arrays.copyOf(objArray, objArray.length, Double[].class);
        for (int i = 0; i < n; i++) {
            output[i] = doubleObjArray[i];
        }

        return output;
    }
}
