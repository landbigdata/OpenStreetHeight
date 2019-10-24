package oss.technion.openstreetheight.section.corners.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.MapFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.section.corners.presenter.CornerPointsOnPhotoPresenter;
import oss.technion.openstreetheight.util.DrawingView;

public class CornerPointsOnPhotoFragment extends Fragment implements CornerPointsOnPhotoView {
    private CornerPointsOnPhotoPresenter presenter = new CornerPointsOnPhotoPresenter(this);

    // Fields
    private ActionBar actionBar;

    @BindView(R.id.photo_image)
    ImageView photoImage;
    @BindView(R.id.photo_drawing_surface)
    DrawingView drawSurface;
    @BindView(R.id.photo_touch_area_magnifier_left)
    ImageView touchAreaMagnifierLeft;
    @BindView(R.id.photo_touch_area_magnifier_right)
    ImageView touchAreaMagnifierRight;
    @BindView(R.id.photo_touch_area_magnifier_left_container)
    ViewGroup touchAreaMagnifierLeftContainer;
    @BindView(R.id.photo_touch_area_magnifier_right_container)
    ViewGroup touchAreaMagnifierRightContainer;

    @BindView(R.id.corner_point_bottom_left)
    View pointBl;
    @BindView(R.id.corner_point_bottom_center)
    View pointBc;
    @BindView(R.id.corner_point_bottom_right)
    View pointBr;
    @BindView(R.id.corner_point_top_left)
    View pointTl;
    @BindView(R.id.corner_point_top_center)
    View pointTc;
    @BindView(R.id.corner_point_top_right)
    View pointTr;

    private Bitmap photoBitmap;

    // used for onTouch
    private float dX;
    private float dY;

    // touch area magnifier
    @BindDimen(R.dimen.photo_touch_area_magnifier_side)
    int TOUCH_AREA_MAGNIFIER_SIDE;
    @BindDimen(R.dimen.photo_touch_area_magnifier_mask)
    int TOUCH_AREA_MAGNIFIER_MASK;
    private Matrix magnifierMatrix = new Matrix();
    private static float SCALE = 7f;

    // used for line drawing

    @BindDimen(R.dimen.corner_point_side)
    int CORNER_POINT_SIDE;
    @BindDimen(R.dimen.photo_line_width)
    int PHOTO_LINE_WIDTH;

    private Paint paint = new Paint();

    private void setupPaint() { // called when Context is obtained
        paint.setStrokeWidth(PHOTO_LINE_WIDTH);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.WHITE);
        paint.setAlpha(128);
    }

    // Actual code

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_corner_points, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnTouch({
            R.id.corner_point_top_left,
            R.id.corner_point_top_center,
            R.id.corner_point_top_right,
            R.id.corner_point_bottom_left,
            R.id.corner_point_bottom_center,
            R.id.corner_point_bottom_right
    })
    boolean onPointTouch(View cornerPoint, MotionEvent event) {
        // zoom touch area
        RectF photoBounds = new RectF();
        Drawable drawable = photoImage.getDrawable();
        photoImage.getImageMatrix().mapRect(photoBounds, new RectF(drawable.getBounds()));

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = cornerPoint.getX() - event.getRawX();
                dY = cornerPoint.getY() - event.getRawY();

                setMagnifierImage(
                        (cornerPoint.getX() + CORNER_POINT_SIDE / 2 - photoBounds.left) / photoBounds.width(),
                        (cornerPoint.getY() + CORNER_POINT_SIDE / 2 - photoBounds.top) / photoBounds.height()
                );

                makeLeftOrRightMagnifierVisible(cornerPoint);
                return true;

            case MotionEvent.ACTION_MOVE:
                // do not allow user to leave image view
                Rect photoViewRect = new Rect();
                photoImage.getGlobalVisibleRect(photoViewRect);


                if (event.getRawX() + dX + CORNER_POINT_SIDE / 2 < photoBounds.left
                        ||
                        event.getRawX() + dX + CORNER_POINT_SIDE / 2 > photoBounds.right)
                    return true;

                if (event.getRawY() + dY + CORNER_POINT_SIDE / 2 < photoBounds.top
                        ||
                        event.getRawY() + dY + CORNER_POINT_SIDE / 2 > photoBounds.bottom)
                    return true;

                cornerPoint.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();

                // draw lines
                drawSurface.invalidate();

                setMagnifierImage(
                        (cornerPoint.getX() + CORNER_POINT_SIDE / 2 - photoBounds.left) / photoBounds.width(),
                        (cornerPoint.getY() + CORNER_POINT_SIDE / 2 - photoBounds.top) / photoBounds.height()
                );

                makeLeftOrRightMagnifierVisible(cornerPoint);
                return true;

            case MotionEvent.ACTION_UP:
                touchAreaMagnifierLeftContainer.setVisibility(View.GONE);
                touchAreaMagnifierRightContainer.setVisibility(View.GONE);
                return true;

            default:
                return false;
        }
    }

    private void makeLeftOrRightMagnifierVisible(View cornerPoint) {
        if (
                (cornerPoint.getX() + CORNER_POINT_SIDE / 2) < TOUCH_AREA_MAGNIFIER_MASK
                        &&
                        (cornerPoint.getY() + CORNER_POINT_SIDE / 2) < TOUCH_AREA_MAGNIFIER_MASK
                ) {
            touchAreaMagnifierLeftContainer.setVisibility(View.GONE);
            touchAreaMagnifierRightContainer.setVisibility(View.VISIBLE);
        } else {
            touchAreaMagnifierLeftContainer.setVisibility(View.VISIBLE);
            touchAreaMagnifierRightContainer.setVisibility(View.GONE);
        }
    }

    private void setMagnifierImage(float bitmapTouchXPercent, float bitmapTouchYPercent) {
        magnifierMatrix.reset();

        // First
        RectF drawableRect = new RectF(0, 0, photoBitmap.getWidth(), photoBitmap.getHeight());
        RectF viewRect = new RectF(0, 0, TOUCH_AREA_MAGNIFIER_SIDE, TOUCH_AREA_MAGNIFIER_SIDE);
        magnifierMatrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

        RectF magnifierImageBounds = new RectF();
        magnifierMatrix.mapRect(magnifierImageBounds, drawableRect);

        // Second
        float pivotX = magnifierImageBounds.left + magnifierImageBounds.width() * bitmapTouchXPercent;
        float pivotY = magnifierImageBounds.top + magnifierImageBounds.height() * bitmapTouchYPercent;

        float scale = SCALE;
        float mWidth = TOUCH_AREA_MAGNIFIER_SIDE;
        float mHeight = TOUCH_AREA_MAGNIFIER_SIDE;
        float x = pivotX;
        float y = pivotY;

        magnifierMatrix.postScale(scale, scale);

        // move to center
        magnifierMatrix.postTranslate(
                -(mWidth * scale - mWidth) / 2,
                -(mHeight * scale - mHeight) / 2
        );

        // move x and y distance
        magnifierMatrix.postTranslate(-(x - (mWidth / 2)) * scale, 0);
        magnifierMatrix.postTranslate(0, -(y - (mHeight / 2)) * scale);

        touchAreaMagnifierLeft.setImageMatrix(magnifierMatrix);
        touchAreaMagnifierRight.setImageMatrix(magnifierMatrix);
    }

    private static void drawLine(View start, View end, int centerOffset, Canvas canvas, Paint paint) {
        canvas.drawLine(
                start.getX() + centerOffset,
                start.getY() + centerOffset,
                end.getX() + centerOffset,
                end.getY() + centerOffset,
                paint
        );
    }

    private void drawLinesBetweenPoints(Canvas canvas) {
        int centerOffset = CORNER_POINT_SIDE / 2;

        drawLine(pointBl, pointTl, centerOffset, canvas, paint);
        drawLine(pointBr, pointTr, centerOffset, canvas, paint);
        drawLine(pointTl, pointTc, centerOffset, canvas, paint);
        drawLine(pointTc, pointTr, centerOffset, canvas, paint);
        drawLine(pointBl, pointBc, centerOffset, canvas, paint);
        drawLine(pointBc, pointBr, centerOffset, canvas, paint);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        // prepare views
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();


        drawSurface.subscribeOnDraw(this::drawLinesBetweenPoints);
        drawSurface.invalidate();

        touchAreaMagnifierLeftContainer.setVisibility(View.GONE);
        touchAreaMagnifierRightContainer.setVisibility(View.GONE);

        // do additional init
        setupPaint();

        presenter.onStart();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_photo_corners, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_finish:
                presenter.onFinishButtonClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setBackground(String path) {
        photoBitmap = BitmapFactory.decodeFile(path);
        photoImage.setImageBitmap(photoBitmap);
        touchAreaMagnifierLeft.setImageBitmap(photoBitmap);
        touchAreaMagnifierRight.setImageBitmap(photoBitmap);
    }

    @Override
    public void setActionBarTitle(@StringRes int text) {
        actionBar.setTitle(text);
    }

    @Override
    public void fillCornerPointsArray(double[][] result) {
        RectF bounds = new RectF();
        Drawable drawable = photoImage.getDrawable();
        photoImage.getImageMatrix().mapRect(bounds, new RectF(drawable.getBounds()));

        // map view id to position of corner
        Map<Integer, View> map = new HashMap<>(6);
        map.put(0, pointBl);
        map.put(1, pointBc);
        map.put(2, pointBr);
        map.put(3, pointTl);
        map.put(4, pointTc);
        map.put(5, pointTr);

        for (int i = 0; i < 6; i++) {
            View cornerPoint = map.get(i);
            //View cornerPoint = getView().findViewById(viewId);
            double x = cornerPoint.getX() + CORNER_POINT_SIDE / 2 - bounds.left;
            double y = cornerPoint.getY() + CORNER_POINT_SIDE / 2 - bounds.top;

            result[0][i] = x;
            result[1][i] = y;
        }
    }

    @Override
    public void showBackButton(boolean isShowBackButton) {
        actionBar.setDisplayShowHomeEnabled(isShowBackButton);
        actionBar.setDisplayHomeAsUpEnabled(isShowBackButton);
    }


}
