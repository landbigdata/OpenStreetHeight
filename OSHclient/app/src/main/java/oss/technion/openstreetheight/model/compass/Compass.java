package oss.technion.openstreetheight.model.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class Compass {
    public enum Accuracy {HIGH, MEDIUM, LOW, UNRELIABLE, UNDEFINED}

    private static PublishSubject<Double> azimuthSubject = PublishSubject.create();
    private static PublishSubject<Accuracy> accuracySubject = PublishSubject.create();

    private static SensorManager sensorManager;

    private static float grav[] = new float[3]; // Gravity (a.k.a// accelerometer data)
    private static float mag[] = new float[3]; // Magnetic
    private static float rotation[] = new float[9]; // Rotation matrix in// Android format
    private static float orientation[] = new float[3]; // azimuth, pitch, roll
    private static float smoothed[] = new float[3];

    private static Accuracy sensorAccuracy = Accuracy.UNDEFINED;

    private static SensorEventListener mSensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            switch (sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    switch (accuracy) {
                        case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                            sensorAccuracy = Accuracy.HIGH;
                            break;

                        case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                            sensorAccuracy = Accuracy.MEDIUM;
                            break;

                        case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                            sensorAccuracy = Accuracy.LOW;
                            break;

                        case SensorManager.SENSOR_STATUS_UNRELIABLE:
                            sensorAccuracy = Accuracy.UNRELIABLE;
                            break;
                    }

                    accuracySubject.onNext(sensorAccuracy);
                    break;
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                smoothed = LowPassFilter.filter(event.values, grav);
                grav[0] = smoothed[0];
                grav[1] = smoothed[1];
                grav[2] = smoothed[2];
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                smoothed = LowPassFilter.filter(event.values, mag);
                mag[0] = smoothed[0];
                mag[1] = smoothed[1];
                mag[2] = smoothed[2];
            }

            // Get rotation matrix given the gravity and geomagnetic matrices
            SensorManager.getRotationMatrix(rotation, null, grav, mag);
            SensorManager.getOrientation(rotation, orientation);
            double floatBearing = orientation[0];

            // Convert from radians to degrees
            floatBearing = Math.toDegrees(floatBearing); // degrees east of true north (180 to -180)

            if (floatBearing < 0) floatBearing += 360;

            azimuthSubject.onNext(floatBearing);
        }
    };

    public static void initialize(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public static void enable() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(mSensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(mSensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public static void disable() {
        sensorManager.unregisterListener(mSensorEventListener);
    }

    public static Observable<Double> getAzimuthSubject() {
        return azimuthSubject;
    }

    public static Observable<Accuracy> getAccuracySubject() {
        return accuracySubject;
    }

    public static Accuracy getAccuracy() {
        return sensorAccuracy;
    }

    public static void saveState(Bundle saveState) {
        saveState.putSerializable("sensorAccuracy", sensorAccuracy);
    }

    public static void restoreState(Bundle saveState) {
        sensorAccuracy = (Accuracy) saveState.getSerializable("sensorAccuracy");
    }
}
