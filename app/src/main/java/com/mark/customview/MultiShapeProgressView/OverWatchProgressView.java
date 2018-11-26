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
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
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
public class OverWatchProgressView extends View {
    private static final String TAG = OverWatchProgressView.class.getSimpleName();
    private Paint mPaint;
    private int mOverWatchProgressColor;
    private float animationValue;
    private int centreWidth;
    private int centreHeight;
    private int length;
    private OverWatchViewItem[] mOverWatchViewItems;
    private int previousIndex = 0;
    private ValueAnimator mValueAnimator;
    private boolean isShowing = true;

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centreWidth = w / 2;
        centreHeight = h / 2;
        length = w / 6;
        CornerPathEffect corEffect = new CornerPathEffect(length / 8);
        mPaint.setPathEffect(corEffect);
        mOverWatchViewItems = new OverWatchViewItem[7];
        for (int i = 0; i < 7; i++) {
            if (i == 6) {
                mOverWatchViewItems[i] = new OverWatchViewItem(new PointF(centreWidth, centreHeight), length);
            } else {
                mOverWatchViewItems[i] = new OverWatchViewItem(new PointF((float) (centreWidth + Math.cos(Math.PI * (2 - i) / 3) * (length * Math.cos(Math.PI / 6) * 21 / 10)), (float) (centreHeight - Math.sin(Math.PI * (2 - i) / 3) * (length * Math.cos(Math.PI / 6) * 21 / 10))), length);
            }
        }
        changeShape();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < 7; i++) {
            mOverWatchViewItems[i].drawViewItem(canvas, mPaint);
        }

    }

    private void changeShape() {
        mValueAnimator = ValueAnimator.ofFloat(0, 7);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationValue = (float) animation.getAnimatedValue();
                int index;
                if (isShowing) {
                    index = (int) animationValue;
                } else {
                    index = (int) (7 - animationValue);
                }
                if (index == 7) {
                    index = 6;
                }
                if (previousIndex != index) {//上个六边形动画AnimationFloat可能没有等于0或者1，就执行这个六边形了。
                    if (index == 0) {
                        mOverWatchViewItems[previousIndex].setAnimationFloat(isShowing ? 0 : 1);
                    } else {
                        mOverWatchViewItems[previousIndex].setAnimationFloat(isShowing ? 1 : 0);
                    }
                    previousIndex = index;
                }
                mOverWatchViewItems[index].setAnimationFloat(isShowing ? animationValue - index : animationValue - (6 - index));
                invalidate();
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                isShowing = !isShowing;
            }
        });
        mValueAnimator.setDuration(1400);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mValueAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mValueAnimator.cancel();
    }

    class OverWatchViewItem {
        private PointF centrePoint;
        private int length;
        private PointF[] mPointFS;
        private float mAnimationFloat = 0;

        public OverWatchViewItem(PointF centrePoint, int length) {
            this.centrePoint = centrePoint;
            this.length = length;
            mPointFS = new PointF[6];
            for (int i = 0; i < 6; i++) {
                mPointFS[i] = new PointF((float) (centrePoint.x + Math.cos(Math.PI * (5 - 2 * i) / 6) * length), (float) (centrePoint.y + Math.sin(Math.PI * (5 - 2 * i) / 6) * length));
            }
        }

        public void setAnimationFloat(float animationFloat) {
            mAnimationFloat = animationFloat;
        }

        public void drawViewItem(Canvas canvas, Paint paint) {
            if (mAnimationFloat == 0) {
                return;
            }
            paint.setAlpha((int) (255 * mAnimationFloat));
            canvas.save();
            canvas.scale(mAnimationFloat, mAnimationFloat, centrePoint.x, centrePoint.y);
            Path path = new Path();
            path.moveTo(mPointFS[0].x, mPointFS[0].y);
            path.lineTo(mPointFS[1].x, mPointFS[1].y);
            path.lineTo(mPointFS[2].x, mPointFS[2].y);
            path.lineTo(mPointFS[3].x, mPointFS[3].y);
            path.lineTo(mPointFS[4].x, mPointFS[4].y);
            path.lineTo(mPointFS[5].x, mPointFS[5].y);
            path.close();
            canvas.drawPath(path, paint);
            canvas.restore();
        }

    }
}
