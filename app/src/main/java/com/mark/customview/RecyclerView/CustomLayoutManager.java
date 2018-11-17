package com.mark.customview.RecyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/13
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class CustomLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = CustomLayoutManager.class.getSimpleName();
    private Context mContext;
    private static final int LAYOUT_END = 1;
    private static final int LAYOUT_START = -1;
    private int mLayoutDirection = LAYOUT_END;

    private int mLastPosition = 0;
    private int mFristPosition = 0;
    private int mVerticalScrollOffset;
    private boolean mInfinite = true;
    private int mExtra = 0;

    public CustomLayoutManager(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }



    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e(TAG, "onLayoutChildren: getItemCount()" + getItemCount() + "-----" + getChildCount());
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        fillView(recycler, mLayoutDirection);
//        recycleAndFillView(recycler, state);
    }

    private void fillView(RecyclerView.Recycler recycler, int layoutDirection) {
        int verticalSpace = getVerticalSpace();
        while ((mInfinite || isFillAndRecycler(recycler, layoutDirection, verticalSpace)) && haveMoreItem()) {
            View view;
            if (layoutDirection == LAYOUT_END) {
                view = recycler.getViewForPosition(mLastPosition);
                mLastPosition += layoutDirection;
            } else {
                if (mFristPosition == 0) {
                    return ;
                }
                mFristPosition += layoutDirection;
                view = recycler.getViewForPosition(mFristPosition);
            }
            if (view == null) {
                return;
            }
            if (layoutDirection == LAYOUT_END) {
                addView(view);
            } else {
                addView(view, 0);
            }
            measureChildWithMargins(view, 0, 0);
            int left, top, right, bottom;
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);
            left = getPaddingLeft();
            right = left + width;
            if (layoutDirection == LAYOUT_END) {
                if (getChildCount() == 1) {
                    top = getPaddingTop();
                } else {
                    top = getDecoratedBottom(getChildAt(getChildCount() - 2));
                }
                bottom = top + height;
                if (mInfinite && bottom > getVerticalSpace()) {
                    mInfinite = false;
                }
            } else {
                bottom = getDecoratedTop(getChildAt(1));
                top = bottom - height;
            }
            layoutDecoratedWithMargins(view, left, top, right, bottom);
        }
        Log.e(TAG, "fillView: " + getChildCount());
    }

    private boolean isFillAndRecycler(RecyclerView.Recycler recycler, int layoutDirection, int verticalSpace) {
        boolean isFill = false;
        View childViewEnd = getChildAt(getChildCount() - 1);
        View childViewStar = getChildAt(0);
        if (layoutDirection == LAYOUT_END) {
            if (childViewEnd != null) {
                if (getDecoratedBottom(childViewEnd) - verticalSpace < mExtra) {
                    isFill = true;
                }
            }
            if (childViewStar != null) {
                if (-getDecoratedBottom(childViewStar) > mExtra) {
                    removeAndRecycleView(childViewStar, recycler);
                    mFristPosition += layoutDirection;
                }
            }
        } else {
            if (childViewStar != null) {
                if (-getDecoratedTop(childViewStar) < mExtra) {
                    isFill = true;
                }
            }
            if (childViewEnd != null) {
                if (getDecoratedTop(childViewEnd) - verticalSpace > mExtra) {
                    removeAndRecycleView(childViewEnd, recycler);
                    mLastPosition += layoutDirection;
                }
            }

        }
        return isFill;
    }

    private boolean haveMoreItem() {
        return mFristPosition >= 0 && mLastPosition < getItemCount();
    }

    @Override
    public boolean canScrollVertically() {
        // 返回true表示可以纵向滑动
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e(TAG, "scrollVerticallyBy: " + dy + "----" + getChildCount());

        if (getChildCount() == 0 || dy == 0) {
            return 0;
        }
        mExtra= Math.abs(dy);
        int travel = dy;
        // 如果滑动到最顶部
        if (mVerticalScrollOffset + dy < 0) {
            travel = -mVerticalScrollOffset;
        }
        if (mLastPosition == getItemCount()&&getDecoratedBottom(getChildAt(getChildCount()-1))-dy<getVerticalSpace()){
                travel =getDecoratedBottom(getChildAt(getChildCount()-1))-getVerticalSpace();
        }
        mVerticalScrollOffset += travel;

        mLayoutDirection = dy > 0 ? LAYOUT_END : LAYOUT_START;
        fillView(recycler, mLayoutDirection);
        offsetChildrenVertical(-travel);
        return travel;
    }

    private int getVerticalSpace() {
        // 计算RecyclerView的可用高度，除去上下Padding值
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    @Override
    public boolean canScrollHorizontally() {
        // 返回true表示可以横向滑动
        return super.canScrollHorizontally();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler,
                                    RecyclerView.State state) {
        // 在这个方法中处理水平滑动
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
