package com.mark.customview.RecyclerView;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
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
 *     desc   : Banner布局，卡片横向滑动布局
 *     version: 1.0
 * </pre>
 */
public class BannerLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = BannerLayoutManager.class.getSimpleName();
    private RecyclerView.Recycler mRecycler;
    private SnapHelper mSnapHelper;
    private int mMaxShowCount = 1;//默认显示一张图片。可以显示多张
    private int mCurrentPosition = 0;
    private int mSurplusWidth = 0;//左右两边剩余的宽度
    private int mItemWidth;
    private int mItemHeight;
    private int mDividerWidth;
    private int mScrollOffset;

    public BannerLayoutManager(final RecyclerView recyclerView, int maxShowCount) {
        mMaxShowCount = maxShowCount;
        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e(TAG, "onLayoutChildren: getItemCount()" + getItemCount() + "-----" + getChildCount());
        if (getItemCount() <= 0 || state.isPreLayout() || getChildCount() > 0) {
            return;
        }
        mRecycler = recycler;
        detachAndScrapAttachedViews(recycler);
        fillView(recycler);
    }

    public void setMaxShowCount(int maxShowCount) {
        mMaxShowCount = maxShowCount;
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }

    public void setSurplusWidth(int surplusWidth) {
        mSurplusWidth = surplusWidth;
    }

    private void fillView(RecyclerView.Recycler recycler) {
        int childCount = getChildCount();
        if (childCount == 0) {
            for (int i = 0; i < mMaxShowCount + 2; i++) {
                View view = recycler.getViewForPosition((mCurrentPosition + getItemCount() - 1 + i) % getItemCount());
                addView(view);
                measureChildWithMargins(view, 0, 0);
                mItemWidth = getDecoratedMeasuredWidth(view);
                mItemHeight = getDecoratedMeasuredHeight(view);
                mDividerWidth = (getWidth() - mItemWidth * mMaxShowCount - 2 * mSurplusWidth) / (mMaxShowCount + 1);
                //我们在布局时，将childView居中处理，这里也可以改为只水平居中
                int left = (mSurplusWidth - mItemWidth) + i * (mItemWidth + mDividerWidth);
                int top = (getHeight() - mItemHeight) / 2;
                int right = left + mItemWidth;
                int bottom = top + mItemHeight;
                layoutDecoratedWithMargins(view, left, top, right, bottom);
            }
        } else {
            if (Math.abs(mScrollOffset) > mItemWidth / 2) {
                Log.e(TAG, "fillView: " + childCount);
                if (mScrollOffset > 0) {
                    int addViewPosition = (mCurrentPosition + getItemCount() + mMaxShowCount + 1) % getItemCount();
                    View lastView = getChildAt(getChildCount() - 1);
                    if (getPosition(lastView) != addViewPosition) {
                        View view = recycler.getViewForPosition(addViewPosition);
                        addView(view);
                        measureChildWithMargins(view, 0, 0);
                        layoutDecoratedWithMargins(view, lastView.getRight() + mDividerWidth, lastView.getTop()
                                , lastView.getRight() + mDividerWidth + mItemWidth, lastView.getTop() + mItemHeight);
                    }
                } else {
                    int addViewPosition = (mCurrentPosition + getItemCount() - 2) % getItemCount();
                    View firstView = getChildAt(0);
                    if (getPosition(firstView) != addViewPosition) {
                        Log.e(TAG, "fillView:---------------------- " + getPosition(firstView) + addViewPosition);
                        View view = recycler.getViewForPosition(addViewPosition);
                        addView(view, 0);
                        measureChildWithMargins(view, 0, 0);
                        layoutDecoratedWithMargins(view, firstView.getLeft() - mDividerWidth - mItemWidth, firstView.getTop()
                                , firstView.getLeft() - mDividerWidth, firstView.getTop() + mItemHeight);
                    }
                }
            }
        }
        Log.e(TAG, "fillView: " + getChildCount());
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e(TAG, "scrollHorizontallyBy: " + dx + "=============" + getChildCount());
        int travel = dx;
        if (Math.abs(mScrollOffset + dx) > (mItemWidth + mDividerWidth) * mMaxShowCount) {
            travel = mScrollOffset > 0 ? (mItemWidth + mDividerWidth) * mMaxShowCount - mScrollOffset : -mScrollOffset - (mItemWidth + mDividerWidth) * mMaxShowCount;
        }
        mScrollOffset += travel;
        fillView(recycler);
        offsetChildrenHorizontal(-travel);
        return travel;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            //滚动停止时回收多余的View
            Log.e(TAG, "onScrollStateChanged: ==============================>>>>>>>" + mScrollOffset);
            if (mScrollOffset != 0 && Math.abs(mScrollOffset) % (mItemWidth + mDividerWidth) == 0) {
                mCurrentPosition = ((mCurrentPosition + mScrollOffset / (mItemWidth + mDividerWidth))+getItemCount()) % getItemCount();
                Log.e(TAG, "onScrollStateChanged: " + mScrollOffset / (mItemWidth + mDividerWidth)+"===="+mCurrentPosition);
                mScrollOffset = 0;
                int childCount = getChildCount();
                if (childCount > mMaxShowCount + 2) {
                    View currentView = findViewByPosition(mCurrentPosition);
                    int currentViewIndex = -1;
                    for (int i = 0; i < childCount; i++) {
                        if (getChildAt(i) == currentView) {
                            currentViewIndex = i;
                            break;
                        }
                    }
                    for (int i = 0; currentViewIndex > 0 && i < childCount; i++) {
                        if (i - currentViewIndex < -1 || i - currentViewIndex > mMaxShowCount) {
                            removeAndRecycleViewAt(i, mRecycler);
                        }
                    }
                }
            }


        }
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
