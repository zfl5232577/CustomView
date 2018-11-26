package com.mark.customview.MultiShapeProgressView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

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
public class MultiShapeProgressView extends View {
    private static final String TAG = MultiShapeProgressView.class.getSimpleName();
    private final int mCircularColor;
    private Shape mCurrentShape = Shape.Circular;
    private Paint mPaint;
    private int mSquareColor;
    private int mTriangleColor;
    private float animationValue;
    private ValueAnimator mValueAnimator;

    public MultiShapeProgressView(Context context) {
        this(context, null);
    }

    public MultiShapeProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiShapeProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MultiShapeProgressView);
        mCircularColor = array.getColor(R.styleable.MultiShapeProgressView_circularColor, Color.BLUE);
        mSquareColor = array.getColor(R.styleable.MultiShapeProgressView_squareColor, Color.BLUE);
        mTriangleColor = array.getColor(R.styleable.MultiShapeProgressView_triangleColor, Color.BLUE);
        array.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        changeShape();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * 3;
        setMeasuredDimension(width, height);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centreWidth = getWidth() / 2;
        int centreHeight = (int) (centreWidth+(getHeight()-2*centreWidth)*Math.abs(animationValue));
        switch (mCurrentShape) {
            case Circular:
                mPaint.setColor(mCircularColor);
                canvas.drawCircle(centreWidth, centreHeight, centreWidth*5/6, mPaint);
                break;
            case Square:
                mPaint.setColor(mSquareColor);
                canvas.save();
                canvas.rotate( 360*(animationValue+1),centreWidth,centreHeight);
                int borderLength = (int) (2*(centreWidth-centreWidth*Math.cos(Math.PI/4)));
                canvas.drawRect(centreWidth-borderLength,centreHeight-borderLength,centreWidth+borderLength,centreHeight+borderLength,mPaint);
                canvas.restore();
                break;
            case Triangle:
                mPaint.setColor(mTriangleColor);
                canvas.save();
                canvas.rotate( 360*(animationValue+1),centreWidth,centreHeight);
                Path path = new Path();
                path.moveTo(centreWidth, (float) (centreHeight-centreWidth));
                path.lineTo((float) (centreWidth+centreWidth*Math.cos(Math.PI/6)), (float) (centreHeight+centreWidth*Math.sin(Math.PI/6)));
                path.lineTo((float) (centreWidth-centreWidth*Math.cos(Math.PI/6)), (float) (centreHeight+centreWidth*Math.sin(Math.PI/6)));
                path.close();
                canvas.drawPath(path,mPaint);
                canvas.restore();
                break;
        }
        //画阴影
        mPaint.setColor(Color.parseColor("#DDDDDD"));
        RectF rect = new RectF(centreWidth-centreWidth*Math.abs(animationValue),getHeight()-centreWidth/6-centreWidth/6*Math.abs(animationValue)
                ,centreWidth+centreWidth*Math.abs(animationValue),getHeight()-centreWidth/6+centreWidth/6*Math.abs(animationValue));
        canvas.drawOval(rect,mPaint);
    }

    private void changeShape(){
        mValueAnimator = ValueAnimator.ofFloat(-1,1);
        mValueAnimator.setInterpolator(new HesitateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
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
        mValueAnimator.setDuration(1000);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mValueAnimator.start();

    }

    private enum Shape {
        Circular(), Square(), Triangle();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mValueAnimator.cancel();
    }
}
