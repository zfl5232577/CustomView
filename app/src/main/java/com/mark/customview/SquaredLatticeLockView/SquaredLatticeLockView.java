package com.mark.customview.SquaredLatticeLockView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mark.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/10
 *     desc   : 九宫格解锁控件
 *     version: 1.0
 * </pre>
 */
public class SquaredLatticeLockView extends View {

    private static final String TAG = SquaredLatticeLockView.class.getSimpleName();
    private Paint mNormalPaint;
    private Paint mSelectPaint;
    private Paint mErrorPaint;
    private int mNormalPaintColor;
    private int mSelectPaintColor;
    private int mErrorPaintColor;
    private int mSingleLineDotCount = 3;
    private List<Dot> mDotList;
    private List<Integer> mSelectDotIndexList;
    private int accuracyRadius;
    private int mDw;
    private int mDh;
    private float mEventX;
    private float mEvenY;
    private boolean unlocking;
    private String mCorrectPassword = "3214789";
    private UnlockListener mUnlockListener;
    private boolean isPasswordError;

    public SquaredLatticeLockView(Context context) {
        this(context, null);
    }

    public SquaredLatticeLockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquaredLatticeLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SquaredLatticeLockView);
        mNormalPaintColor = array.getColor(R.styleable.SquaredLatticeLockView_normalColor, Color.BLACK);
        mSelectPaintColor = array.getColor(R.styleable.SquaredLatticeLockView_selectColor, Color.BLUE);
        mErrorPaintColor = array.getColor(R.styleable.SquaredLatticeLockView_errorColor, Color.RED);
        array.recycle();
        init(context);
    }

    private void init(Context context) {
        mNormalPaint = new Paint();
        mNormalPaint.setColor(mNormalPaintColor);
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setStrokeWidth(dp2px(context, 2));
        mNormalPaint.setStyle(Paint.Style.STROKE);
        mNormalPaint.setStrokeCap(Paint.Cap.ROUND);

        mSelectPaint = new Paint();
        mSelectPaint.setColor(mSelectPaintColor);
        mSelectPaint.setAntiAlias(true);
        mSelectPaint.setStrokeWidth(dp2px(context, 2));
        mSelectPaint.setStyle(Paint.Style.STROKE);
        mSelectPaint.setStrokeCap(Paint.Cap.ROUND);

        mErrorPaint = new Paint();
        mErrorPaint.setColor(mErrorPaintColor);
        mErrorPaint.setAntiAlias(true);
        mErrorPaint.setStrokeWidth(dp2px(context, 2));
        mErrorPaint.setStyle(Paint.Style.STROKE);
        mErrorPaint.setStrokeCap(Paint.Cap.ROUND);


        accuracyRadius = dp2px(context, 25);
        mSelectDotIndexList = new ArrayList<>();
    }

    private void initDot() {
        if (mDotList == null) {
            int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
            mDw = width / mSingleLineDotCount / 2;
            mDh = height / mSingleLineDotCount / 2;
            int size = mSingleLineDotCount * mSingleLineDotCount;
            mDotList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Dot dot = new Dot(mDw + i % mSingleLineDotCount * 2 * mDw + getPaddingLeft(), mDh + i / mSingleLineDotCount * 2 * mDh + getPaddingTop(), i + 1);
                mDotList.add(dot);
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(width, height), Math.min(width, height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDot();
        for (Dot dot : mDotList) {
            switch (dot.getState()) {
                case Dot.STATE_NORMAL:
                    mNormalPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(dot.centreX, dot.centreY, 15, mNormalPaint);
                    mNormalPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(dot.centreX, dot.centreY, accuracyRadius, mNormalPaint);
                    break;
                case Dot.STATE_SELECT:
                    mSelectPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(dot.centreX, dot.centreY, 15, mSelectPaint);
                    mSelectPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(dot.centreX, dot.centreY, accuracyRadius, mSelectPaint);
                    break;
                case Dot.STATE_ERROR:
                    mErrorPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(dot.centreX, dot.centreY, 15, mErrorPaint);
                    mErrorPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(dot.centreX, dot.centreY, accuracyRadius, mErrorPaint);
                    break;
            }
        }
        int selectCount = mSelectDotIndexList.size();
        if (selectCount > 0) {
            Path path = new Path();
            for (int i = 0; i < selectCount; i++) {
                Dot dot = mDotList.get(mSelectDotIndexList.get(i) - 1);
                if (i == 0) {
                    path.moveTo(dot.centreX, dot.centreY);
                } else {
                    path.lineTo(dot.centreX, dot.centreY);
                }
                if (i == selectCount - 1 && unlocking) {
                    path.lineTo(mEventX, mEvenY);
                }
            }
            canvas.drawPath(path, isPasswordError ? mErrorPaint : mSelectPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                unlocking = true;
                isPasswordError = false;
                for (Integer integer : mSelectDotIndexList) {
                    mDotList.get(integer - 1).setState(Dot.STATE_NORMAL);
                }
                mSelectDotIndexList.clear();
                removeCallbacks(refresh);
            case MotionEvent.ACTION_MOVE:
                mEventX = event.getX();
                mEvenY = event.getY();
                int column = (int) ((mEventX - getPaddingLeft()) / mDw / 2 + 1);
                int row = (int) ((mEvenY - getPaddingTop()) / mDh / 2 + 1);
                if (row > 0 && column > 0 && row <= mSingleLineDotCount && column <= mSingleLineDotCount) {
                    int index = (row - 1) * mSingleLineDotCount + column;
                    Dot dot = mDotList.get(index - 1);
                    if (Math.abs(dot.centreX - mEventX) + Math.abs(dot.centreY - mEvenY) < accuracyRadius * 1.2 && dot.getState() == Dot.STATE_NORMAL) {
                        if (mSelectDotIndexList.size() > 0) {
                            int lastIndex = mSelectDotIndexList.get(mSelectDotIndexList.size() - 1);
                            if ((lastIndex % 2 == 0 && (lastIndex + index) == 10) || (lastIndex % 2 != 0 && lastIndex != 5 && index != 5 && (lastIndex + index) % 2 == 0)) {
                                int centreIndex = (lastIndex + index) / 2;
                                if (!mSelectDotIndexList.contains(centreIndex)) {
                                    mSelectDotIndexList.add((lastIndex + index) / 2);
                                    mDotList.get((lastIndex + index) / 2 - 1).setState(Dot.STATE_SELECT);
                                }
                            }
                        }
                        mSelectDotIndexList.add(index);
                        dot.setState(Dot.STATE_SELECT);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                unlocking = false;
                StringBuilder password = new StringBuilder();
                for (Integer integer : mSelectDotIndexList) {
                    password.append(integer);
                }
                if (mCorrectPassword != null && mCorrectPassword.equals(password.toString())) {
                    if (mUnlockListener != null) {
                        mUnlockListener.onUnlockSuccess();
                    }
                    isPasswordError = false;
                } else {
                    if (mUnlockListener != null) {
                        mUnlockListener.onUnlockFail();
                    }
                    isPasswordError = true;
                }
                for (Integer integer : mSelectDotIndexList) {
                    mDotList.get(integer - 1).setState(isPasswordError ? Dot.STATE_ERROR : Dot.STATE_NORMAL);
                }
                if (!isPasswordError) {
                    mSelectDotIndexList.clear();
                } else {
                    postDelayed(refresh, 2000);
                }
                break;
        }
        invalidate();
        return true;
    }

    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            for (Integer integer : mSelectDotIndexList) {
                mDotList.get(integer - 1).setState(Dot.STATE_NORMAL);
            }
            mSelectDotIndexList.clear();
            invalidate();
        }
    };

    public int getNormalPaintColor() {
        return mNormalPaintColor;
    }

    public void setNormalPaintColor(int normalPaintColor) {
        mNormalPaintColor = normalPaintColor;
    }

    public int getSelectPaintColor() {
        return mSelectPaintColor;
    }

    public void setSelectPaintColor(int selectPaintColor) {
        mSelectPaintColor = selectPaintColor;
    }

    public int getErrorPaintColor() {
        return mErrorPaintColor;
    }

    public void setErrorPaintColor(int errorPaintColor) {
        mErrorPaintColor = errorPaintColor;
    }

    public String getCorrectPassword() {
        return mCorrectPassword;
    }

    public void setCorrectPassword(String correctPassword) {
        mCorrectPassword = correctPassword;
    }

    public UnlockListener getUnlockListener() {
        return mUnlockListener;
    }

    public void setUnlockListener(UnlockListener unlockListener) {
        mUnlockListener = unlockListener;
    }

    private int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5F);

    }

    public interface UnlockListener {
        void onUnlockSuccess();

        void onUnlockFail();
    }

    private static class Dot {
        int centreX;
        int centreY;
        int index;
        private static final int STATE_NORMAL = 1;
        private static final int STATE_SELECT = 2;
        private static final int STATE_ERROR = 3;
        int state = STATE_NORMAL;

        public Dot(int centreX, int centreY, int index) {
            this.centreX = centreX;
            this.centreY = centreY;
            this.index = index;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }
    }
}
