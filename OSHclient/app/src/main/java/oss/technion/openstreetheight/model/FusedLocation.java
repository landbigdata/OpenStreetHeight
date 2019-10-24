package oss.technion.openstreetheight.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import oss.technion.openstreetheight.model.data.LatLng;
import oss.technion.openstreetheight.model.data.LatLngAlt;

public class FusedLocation {
    private static final String LAST_LOCATION_KEY = "last_location_key";

    private static final BehaviorSubject<LatLngAlt> locationSubject = BehaviorSubject.create();

    private static final PublishSubject<Float> horizontalAccuracySubject = PublishSubject.create();

    private static final LocationRequest locationRequest = LocationRequest.create();
    private static FusedLocationProviderClient fusedLocation;

    private static @Nullable LatLngAlt lastLocation;
    private static @Nullable Float horAccuracy;

    static {
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public static void initialize(Context context) {
        fusedLocation = LocationServices.getFusedLocationProviderClient(context);
    }

    public static void saveState(Bundle b) {
        b.putSerializable(LAST_LOCATION_KEY, lastLocation);
    }

    public static void restoreState(Bundle b) {
        lastLocation = (LatLngAlt) b.getSerializable(LAST_LOCATION_KEY);
    }


    @SuppressLint("MissingPermission")
    public static void enable() {
        fusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    public static void disable() {
        fusedLocation.removeLocationUpdates(locationCallback);
    }

    public static Observable<LatLngAlt> getBehSubject() {
        return locationSubject;
    }

    public static Observable<Float> getHorizontalAccuracySubject() {
        return horizontalAccuracySubject;
    }

    private static LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {

        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            lastLocation = new LatLngAlt(
                    locationResult.getLastLocation().getLatitude(),
                    locationResult.getLastLocation().getLongitude(),
                    locationResult.getLastLocation().getAltitude()
            );

            horAccuracy = locationResult.getLastLocation().getAccuracy();
            horizontalAccuracySubject.onNext(horAccuracy);

            locationSubject.onNext(lastLocation);
        }
    };

    @Nullable
    public static LatLng getLastLocation() {
        return lastLocation;
    }

    @Nullable
    public static Float getHorAccuracy() {
        return horAccuracy;
    }
}
