package oss.technion.openstreetheight.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

public class OsmAuth {

    private static SharedPreferences prefs;
    private static String OSM_TOKEN = "osm_token";
    private static String OSM_TOKEN_SECRET = "osm_token_secret";

    // oAuth
    private static String CONSUMER_KEY = "R8wMU2ezkp3ijpIcuAnKD4HSRCqfmf69NknPoSNA";
    private static String CONSUMER_SECRET = "40CZ42WcGZQ4PGBEqmi5lkUqrWGIh0xMZd6pZ1ap";

    private static String token;
    private static String tokenSecret;
    private static OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

    private static OAuthProvider provider = new DefaultOAuthProvider(
            "https://www.openstreetmap.org/oauth/request_token",
            "https://www.openstreetmap.org/oauth/access_token",
            "https://www.openstreetmap.org/oauth/authorize"
    );

    public static void onStart(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        restoreFromPrefs();
    }

    public static boolean isAuthorized() {
        return prefs.contains(OSM_TOKEN);
    }

    // Step 1
    public static Single<String> retrieveRequestToken() {
        return Single.fromCallable(() -> provider.retrieveRequestToken(consumer, "https://osh"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static boolean shouldInterceptCallback(String check) {
        return check.contains("oauth_verifier=");
    }

    // Step 2
    public static Completable retrieveAccessToken(String callbackUrl) {
        String oAuthVerifier = callbackUrl.split("oauth_verifier=")[1];

        return Completable
                .fromAction(() -> provider.retrieveAccessToken(consumer, oAuthVerifier))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Step 3
    public static void saveTokenToPrefs() {
        token = consumer.getToken();
        tokenSecret = consumer.getTokenSecret();

        prefs
                .edit()
                .putString(OSM_TOKEN, consumer.getToken())
                .putString(OSM_TOKEN_SECRET, consumer.getTokenSecret())
                .apply();
    }

    public static void restoreFromPrefs() {
        if(prefs.contains(OSM_TOKEN)) {
            token = prefs.getString(OSM_TOKEN, null);
            tokenSecret = prefs.getString(OSM_TOKEN_SECRET, null);

            consumer.setTokenWithSecret(token, tokenSecret);
        }
    }

    public static OAuthConsumer getInProcessConsumer() {
        return consumer;
    }

    public static OAuthConsumer getSavedConsumer() {
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        consumer.setTokenWithSecret(token, tokenSecret);

        return consumer;
    }
}
