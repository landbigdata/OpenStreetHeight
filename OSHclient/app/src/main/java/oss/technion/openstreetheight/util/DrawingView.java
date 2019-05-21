package oss.technion.openstreetheight.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class DrawingView extends View {
    private PublishSubject<Canvas> publishSubject = PublishSubject.create();

    public Disposable subscribeOnDraw(Consumer<Canvas> onNext) {
        return publishSubject.subscribe(onNext);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        publishSubject.onNext(canvas);
    }

    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
