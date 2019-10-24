package oss.technion.openstreetheight.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class InternetAccess {
    private static final BehaviorSubject<Boolean> subject = BehaviorSubject.create();
    private static Context context;

    public static void initialize(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        InternetAccess.context = context;

        cm.registerNetworkCallback(new NetworkRequest.Builder().build(), callback);

        subject.onNext(isOnline());
    }

    public static void deInitialize() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        cm.unregisterNetworkCallback(callback);
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean connected = (netInfo != null && netInfo.isConnectedOrConnecting());

        return connected;
    }

    /**
     * @return BehaviorSubject
     */
    public static Observable<Boolean> getBehSubject() {
        return subject;
    }

    private static NetworkCallback callback = new NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            subject.onNext(true);
        }

        @Override
        public void onLost(Network network) {
            subject.onNext(false);
        }
    };

}
