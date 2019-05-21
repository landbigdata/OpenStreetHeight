package oss.technion.openstreetheight.hub;


import android.os.Bundle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

public class MessageHub {

    public static class MsgTuple<T> {
        public final Class<T> type ;
        public final T message;

        public MsgTuple(Class<T> type, T message) {
            this.type = type;
            this.message = message;
        }
    }


    public interface Message extends Serializable {} // marker interface

    private static  Map<Class<? extends Message>, Message> archive = new HashMap<>();

    public static PublishSubject<MsgTuple<?>> signal = PublishSubject.create();

    public static <T extends Message> void put(Class<T> type, T message) {
        archive.put(type, message);
    }

    public static <T extends Message> void sendSignal(Class<T> type, T message) {
        signal.onNext(new MsgTuple<>(type, message));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Message> T get(Class<T> type) {
        return (T) archive.get(type);
    }




    private static final String ARHCIVE_BUNDLE_KEY = "archive";

    public static void saveState(Bundle state) {
        state.putSerializable(ARHCIVE_BUNDLE_KEY, (HashMap) archive);
    }

    @SuppressWarnings("unchecked")
    public static void restoreState(Bundle state) {
        archive = (Map<Class<? extends Message>, Message>) state.getSerializable(ARHCIVE_BUNDLE_KEY);
    }
}
