package com.mark.customview.FlowLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.text.TextUtilsCompat;
import android.util.AttributeSet;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.mark.customview.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/07
 *     desc   : 热门标签流式布局
 *     version: 1.0
 * </pre>
 */
public class FlowLayout extends ViewGroup {

    private int mGravity;
    private List<Integer> mLineViewCount = new ArrayList<Integer>();
    private List<Integer> mLineHeight = new ArrayList<Integer>();
    private List<Integer> mLineWidth = new ArrayList<Integer>();

    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int CENTER = 3;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mGravity = array.getInt(R.styleable.FlowLayout_flowLayoutGravity, LEFT);
        int layoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault());
        if (layoutDirection == LayoutDirection.RTL) {
            if (mGravity == LEFT) {
                mGravity = RIGHT;
            } else {
                mGravity = LEFT;
            }
        }
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        mLineViewCount.clear();
        mLineWidth.clear();
        mLineHeight.clear();

        // wrap_content
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;
        int lineViewCount = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) continue;
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                mLineViewCount.add(lineViewCount);
                lineViewCount = 1;
                mLineWidth.add(lineWidth);
                mLineHeight.add(lineHeight);
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
            } else {
                lineViewCount++;
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            if (i == childCount - 1) {
                mLineViewCount.add(lineViewCount);
                mLineWidth.add(lineWidth);
                mLineHeight.add(lineHeight);
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }

        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int line = 0;
        int viewCount = 0;//当前行需要layout的数量
        int top = getPaddingTop();
        int left = 0;
        int right = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            if (i >= viewCount) {
                top += line > 0 ? mLineHeight.get(line - 1) : 0;
                viewCount += mLineViewCount.get(line);
                switch (this.mGravity) {
                    case LEFT:
                        left = getPaddingLeft();
                        break;
                    case CENTER:
                        left = (getWidth() + getPaddingLeft() - getPaddingRight() - mLineWidth.get(line)) / 2;
                        break;
                    case RIGHT:
                        right = getWidth()-getPaddingRight();
                        break;
                }
                line++;
            }
            if (mGravity != RIGHT) {
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + childWidth;
                int bc = tc + childHeight;
                childView.layout(lc, tc, rc, bc);
                left += childWidth + lp.leftMargin + lp.rightMargin;
            } else {
                int rc = right - lp.rightMargin;
                int lc = rc - childWidth;
                int tc = top + lp.topMargin;
                int bc = tc + childHeight;
                childView.layout(lc, tc, rc, bc);
                right -= childWidth + lp.leftMargin + lp.rightMargin;
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}
