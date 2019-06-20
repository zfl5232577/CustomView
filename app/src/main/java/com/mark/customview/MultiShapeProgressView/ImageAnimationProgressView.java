package com.mark.customview.MultiShapeProgressView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;

import com.mark.customview.R;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/05
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class ImageAnimationProgressView extends View {
    private static final String TAG = ImageAnimationProgressView.class.getSimpleName();
    private Paint mPaint;
    private float animationTranslateValue;
    private float animationFlipValue = 1;
    private ValueAnimator mFlipAnimator;
    private ValueAnimator mTranslateAnimator;
    private Bitmap bitmapShape;
    private Bitmap bitmapEye;
    private Matrix matrixShape;
    private Matrix matrixEye;

    public ImageAnimationProgressView(Context context) {
        this(context, null);
    }

    public ImageAnimationProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageAnimationProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        bitmapShape = BitmapFactory.decodeResource(getResources(), R.drawable.progress_image1);
        bitmapEye = BitmapFactory.decodeResource(getResources(), R.drawable.progress_image_2);
        matrixShape = new Matrix();
        matrixEye = new Matrix();
        changeShape();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * 1.2);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int bw = bitmapShape.getWidth();
        float scale = 1f * (w - dp2px(getContext(), 5)) / bw;
        bitmapShape = scaleBitmap(bitmapShape, scale);
        bitmapEye = scaleBitmap(bitmapEye, scale);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centreWidth = getWidth() / 2;
        float shadowHeight = centreWidth / 6f;
        int centreHeight = (int) ((bitmapShape.getHeight() >> 1) + (getHeight() - shadowHeight - bitmapShape.getHeight())
                * (Math.abs(animationTranslateValue) > 1 ? 1 : Math.abs(animationTranslateValue)));

        //画阴影
        mPaint.setColor(Color.parseColor("#D3D3D3"));
        RectF rect = new RectF(centreWidth - centreWidth * (0.5f + 0.3f * Math.abs(animationTranslateValue)), getHeight() - shadowHeight - shadowHeight * (0.5f + 0.3f * Math.abs(animationTranslateValue))
                , centreWidth + centreWidth * (0.5f + 0.3f * Math.abs(animationTranslateValue)), getHeight() - shadowHeight + shadowHeight * (0.5f + 0.3f * Math.abs(animationTranslateValue)));
        canvas.drawOval(rect, mPaint);


        matrixEye.setTranslate(centreWidth - (bitmapEye.getWidth() >> 1), centreHeight - (bitmapEye.getHeight() >> 1));
        if (Math.abs(animationTranslateValue) > 1) {
            matrixEye.postScale(1, 2 - Math.abs(animationTranslateValue), centreWidth, getHeight() - shadowHeight);
        } else {
            matrixEye.postScale(1, animationFlipValue, centreWidth, centreHeight);
        }

        canvas.drawBitmap(bitmapEye, matrixEye, null);

        matrixShape.setTranslate(centreWidth - (bitmapShape.getWidth() >> 1), centreHeight - (bitmapShape.getHeight() >> 1));
        if (Math.abs(animationTranslateValue) > 1) {
            matrixShape.postScale(1, 2 - Math.abs(animationTranslateValue), centreWidth, getHeight() - shadowHeight);
        } else {
            matrixShape.postRotate(180 * (animationTranslateValue + 1), centreWidth, centreHeight);
        }
        canvas.drawBitmap(bitmapShape, matrixShape, null);
    }

    private void changeShape() {
        mTranslateAnimator = ValueAnimator.ofFloat(-1, 1);
        mTranslateAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        mTranslateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationTranslateValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mTranslateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFlipAnimator.start();
            }
        });
        mTranslateAnimator.setDuration(1300);
        mTranslateAnimator.start();

        mFlipAnimator = ValueAnimator.ofFloat(1, -1, 1);
        mFlipAnimator.setInterpolator(new LinearInterpolator());
        mFlipAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationFlipValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mFlipAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mTranslateAnimator.start();
            }
        });
        mFlipAnimator.setDuration(300);
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin 原图
     * @param scale  缩放比例
     * @return new Bitmap
     */
    private Bitmap scaleBitmap(Bitmap origin, float scale) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTranslateAnimator.cancel();
        mFlipAnimator.cancel();
        bitmapShape.recycle();
        bitmapEye.recycle();
    }

    private static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }
}
