package com.mark.customview.MultiShapeProgressView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

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
public class OverWatchProgressView extends View {
    private static final String TAG = OverWatchProgressView.class.getSimpleName();
    private Paint mPaint;
    private int mOverWatchProgressColor;
    private float animationValue;
    private int centreWidth;
    private int centreHeight;
    private int length;

    public OverWatchProgressView(Context context) {
        this(context, null);
    }

    public OverWatchProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverWatchProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.OverWatchProgressView);
        mOverWatchProgressColor = array.getColor(R.styleable.MultiShapeProgressView_circularColor, Color.parseColor("#FFC200"));
        array.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mOverWatchProgressColor);
        mPaint.setAntiAlias(true);
        changeShape();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width;
        centreWidth = width/2;
        centreHeight = height/2;
        length = width/6;
        CornerPathEffect corEffect = new CornerPathEffect(length);
        mPaint.setPathEffect(corEffect);
        setMeasuredDimension(width, height);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

    private void changeShape(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(-1,1);
        valueAnimator.setInterpolator(new HesitateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                switch (mCurrentShape){
                    case Circular:
                        mCurrentShape = Shape.Triangle;
                        break;
                    case Square:
                        mCurrentShape = Shape.Circular;
                        break;
                    case Triangle:
                        mCurrentShape = Shape.Square;
                        break;
                }
                invalidate();
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
    }

    class OverWatchViewItem{

    }
}
