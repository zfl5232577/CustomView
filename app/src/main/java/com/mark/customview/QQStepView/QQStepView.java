package com.mark.customview.QQStepView;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.mark.customview.R;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/04
 *     desc   : 仿写QQ计步器自定义控件
 *     version: 1.0
 * </pre>
 */
public class QQStepView extends View {
    private int mOuterColor;
    private int mInnerColor;
    private int mBorderWidth;
    private int mStepTextSize;
    private int mStepTextColor;
    private Paint mOuterPaint;
    private Paint mInnerPaint;
    private Paint mTextPaint;
    private int mDuration = 1000;
    private int mStepMax = 10000;
    private int mCurrentStep = 4000;
    private float animatorValue;
    private ValueAnimator valueAnimator;

    public QQStepView(Context context) {
        this(context, null);
    }

    public QQStepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //1.分析效果
        //2.确定自定义属性，编写Attrs.xml
        //3.在布局中使用
        //4.在自定义View中获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.QQStepView);
        mOuterColor = array.getColor(R.styleable.QQStepView_outerColor, Color.BLUE);
        mInnerColor = array.getColor(R.styleable.QQStepView_innerColor, Color.RED);
        mBorderWidth = (int) array.getDimension(R.styleable.QQStepView_borderWidth, dp2px(context, 12));
        mStepTextColor = array.getColor(R.styleable.QQStepView_stepTextColor, Color.RED);
        mStepTextSize = array.getDimensionPixelSize(R.styleable.QQStepView_stepTextSize, sp2px(context, 25));
        array.recycle();
        init();
    }

    private void init() {
        mOuterPaint = new Paint();
        mOuterPaint.setColor(mOuterColor);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOuterPaint.setStrokeWidth(mBorderWidth);
        mInnerPaint = new Paint();
        mInnerPaint.setColor(mInnerColor);
        mInnerPaint.setStyle(Paint.Style.STROKE);
        mInnerPaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setStrokeWidth(mBorderWidth);
        mTextPaint = new Paint();
        mTextPaint.setColor(mStepTextColor);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextSize(mStepTextSize);
        mTextPaint.setAntiAlias(true);
        startAnimator();
    }

    //5.onMeasure
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(width, height), Math.min(width, height));
    }

    //6.onDraw
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //6.1画外圆
        int dwidth = mBorderWidth / 2;
        RectF rectF = new RectF(dwidth, dwidth, getWidth() - dwidth, getHeight() - dwidth);
        canvas.drawArc(rectF, 135, 270, false, mOuterPaint);
        //6.1画内圆
        float sweepStep = (float) mCurrentStep / (float) mStepMax * animatorValue;
        canvas.drawArc(rectF, 135, 270 * sweepStep, false, mInnerPaint);
        //6.1画文字
        String text = String.valueOf((int) (mCurrentStep * animatorValue));
        Rect rect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), rect);
        Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
        int baseline = (getHeight() - fontMetricsInt.bottom - fontMetricsInt.top) / 2 ;
        canvas.drawText(text, getWidth() / 2 - rect.width() / 2, baseline, mTextPaint);
    }

    private void startAnimator() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, 1);
        } else {
            valueAnimator.cancel();
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(mDuration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
    }

    public int getOuterColor() {
        return mOuterColor;
    }

    public void setOuterColor(int outerColor) {
        if (outerColor == mOuterColor) {
            return;
        }
        mOuterColor = outerColor;
        invalidate();
    }

    public int getInnerColor() {
        return mInnerColor;
    }

    public void setInnerColor(int innerColor) {
        if (innerColor == mInnerColor) {
            return;
        }
        mInnerColor = innerColor;
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }
        mBorderWidth = borderWidth;
        invalidate();
    }

    public int getStepTextSize() {
        return mStepTextSize;
    }

    public void setStepTextSize(int stepTextSize) {
        if (stepTextSize == mStepTextSize) {
            return;
        }
        mStepTextSize = stepTextSize;
        invalidate();
    }

    public int getStepTextColor() {
        return mStepTextColor;
    }

    public void setStepTextColor(int stepTextColor) {
        if (stepTextColor == mStepTextColor) {
            return;
        }
        mStepTextColor = stepTextColor;
        invalidate();
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        if (duration == mDuration) {
            return;
        }
        if (duration == 0) {
            mDuration = 0;
            return;
        }
        mDuration = duration;
        startAnimator();
    }

    public int getStepMax() {
        return mStepMax;
    }

    public void setStepMax(int stepMax) {
        if (stepMax == mStepMax) {
            return;
        }
        if (stepMax < mCurrentStep) {
            throw new RuntimeException("stepMax not < mCurrentStep");
        }
        mStepMax = stepMax;
        startAnimator();
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    public void setCurrentStep(int currentStep) {
        if (currentStep == mCurrentStep) {
            return;
        }
        if (currentStep > mStepMax) {
            throw new RuntimeException("mCurrentStep not > mStepMax");
        }
        mCurrentStep = currentStep;
        startAnimator();
    }

    private static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    private static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }
}
