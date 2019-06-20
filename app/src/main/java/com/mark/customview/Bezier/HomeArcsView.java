package com.mark.customview.Bezier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2019/03/08
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class HomeArcsView extends View {

    private int mHeight;
    private int mWidth;
    private int assist;
    private Paint mPaint;
    private Paint mPaint2;
    private Path path2;
    private Path path;

    public HomeArcsView(Context context) {
        this(context, null);
    }

    public HomeArcsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeArcsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 防抖动
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        // 笔宽
        mPaint.setStrokeWidth(1);
        // 空心
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint2 = new Paint();
        // 抗锯齿
        mPaint2.setAntiAlias(true);
        // 防抖动
        mPaint2.setDither(true);
        mPaint2.setColor(Color.WHITE);
        // 笔宽
        mPaint2.setStrokeWidth(0);
        mPaint2.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = dp2px(getContext(), 72);
        mHeight = dp2px(getContext(), 12);
        assist = dp2px(getContext(), 10);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (path2 == null) {
            path2 = new Path();
            path2.moveTo(0, mHeight);
            path2.quadTo(assist, mHeight, mWidth / 4, mHeight / 2);
            path2.quadTo(mWidth / 2 - assist, 0, mWidth / 2, 0);
            path2.quadTo(mWidth / 2 + assist, 0, mWidth * 3 / 4, mHeight / 2);
            path2.quadTo(mWidth - assist, mHeight, mWidth, mHeight);
            path2.close();
        }
        if (path == null) {
            path = new Path();
            path.moveTo(0, mHeight);
            path.quadTo(assist, mHeight, mWidth / 4, mHeight / 2);
            path.quadTo(mWidth / 2 - assist, 0, mWidth / 2, 0);
            path.quadTo(mWidth / 2 + assist, 0, mWidth * 3 / 4, mHeight / 2);
            path.quadTo(mWidth - assist, mHeight, mWidth, mHeight);
        }
        canvas.drawPath(path2, mPaint2);
        canvas.drawPath(path, mPaint);
    }

    private static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

}
