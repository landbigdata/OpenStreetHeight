package oss.technion.openstreetheight.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class InternetAccess {
    private static final InternetAccessReceiver internetStateReceiver = new InternetAccessReceiver();

    private static final BehaviorSubject<Boolean> subject = BehaviorSubject.create(); // always filled

    private static Context context;

    public static void initialize(Context context) {
        InternetAccess.context = context;

        context.registerReceiver(internetStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        subject.onNext(isOnline());
    }

    public static void deInitialize() {
        context.unregisterReceiver(internetStateReceiver);
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean connected = (netInfo != null && netInfo.isConnectedOrConnecting());

        return connected;
    }

    public static Observable<Boolean> getBehSubject() {
        return subject;
    }

    public static class InternetAccessReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            subject.onNext(isOnline());
        }
    }
}
