package com.mark.customview.RecyclerView;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.mark.customview.MultiShapeProgressView.HesitateInterpolator;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/13
 *     desc   : Banner布局
 *     version: 1.0
 * </pre>
 */
public class BannerLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = BannerLayoutManager.class.getSimpleName();
    private final RecyclerView mRecyclerView;
    private SnapHelper mSnapHelper;
    private int mMaxShowCount = 1;//默认显示一张图片。可以显示多张
    private int mCurrentPosition = 0;
    private int mSurplusWidth = 0;//左右两边剩余的宽度
    private int mItemWidth;
    private int mItemHeight;
    private int mDividerWidth;
    private int mScrollOffset;
    private Runnable autoLoop = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "run: ==============================");
            smoothScrollToPosition(mRecyclerView, null, (mCurrentPosition + 1 + getItemCount()) % getItemCount());
        }
    };
    private OrientationHelper mHorizontalHelper;

    public BannerLayoutManager(final RecyclerView recyclerView, int maxShowCount) {
        mRecyclerView = recyclerView;
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
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        if (state.getItemCount() != 0 && !state.didStructureChange()) {
            Log.d(TAG, "onLayoutChildren: ignore extra layout step");
            return;
        }
        detachAndScrapAttachedViews(recycler);
        fillView(recycler,0);
        mRecyclerView.postDelayed(autoLoop, 3000);
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

    private void fillView(RecyclerView.Recycler recycler, int dx) {
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
            int leftEdge = getOrientationHelper().getStartAfterPadding();
            int rightEdge = getOrientationHelper().getEndAfterPadding();
            //1.remove and recycle the view that disappear in screen
            View child;
            if (dx >= 0) {
                //remove and recycle the left off screen view
                int fixIndex = 0;
                for (int i = 0; i < getChildCount(); i++) {
                    child = getChildAt(i + fixIndex);
                    if (getDecoratedRight(child) - dx < leftEdge) {
                        removeAndRecycleView(child, recycler);
                        Log.e(TAG, "fillView: "+child );
                        mCurrentPosition++;
                    } else {
                        break;
                    }
                }
            } else { //dx<0
                //remove and recycle the right off screen view
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    child = getChildAt(i);
                    if (getDecoratedLeft(child) - dx > rightEdge) {
                        removeAndRecycleView(child, recycler);
                        mCurrentPosition--;
                    }
                }
            }

            //2.Add or reattach item view to fill screen
            long start = System.currentTimeMillis();
            int startPosition = mCurrentPosition;
            int startOffset = -1;
            int top = 0;
            View scrap;
            if (dx >= 0) {
                if (getChildCount() != 0) {
                    View lastView = getChildAt(getChildCount() - 1);
                    startPosition = getPosition(lastView) + 1; //start layout from next position item
                    startOffset = getDecoratedRight(lastView);
                    top = getDecoratedTop(lastView);
                }
                for (int i = startPosition; startOffset < rightEdge + dx; i++) {
                    scrap = recycler.getViewForPosition(i % getItemCount());
                    Log.e(TAG, "fillView: "+scrap );
                    addView(scrap);
                    measureChildWithMargins(scrap, 0, 0);
                    mItemWidth = getDecoratedMeasuredWidth(scrap);
                    mItemHeight = getDecoratedMeasuredHeight(scrap);
                    layoutDecoratedWithMargins(scrap, startOffset + mDividerWidth, top,
                            startOffset + mDividerWidth + mItemWidth, top + mItemHeight);
                    startOffset = startOffset + mDividerWidth + mItemWidth;
                }
            } else {
                //dx<0
                if (getChildCount() > 0) {
                    View firstView = getChildAt(0);
                    startPosition = getPosition(firstView) - 1; //start layout from previous position item
                    startOffset = getDecoratedLeft(firstView);
                    top = getDecoratedTop(firstView);
                }
                for (int i = startPosition; startOffset > leftEdge + dx; i--) {
                    scrap = recycler.getViewForPosition((i + getItemCount()) % getItemCount());
                    addView(scrap, 0);
                    Log.e(TAG, "fillView: "+scrap );
                    measureChildWithMargins(scrap, 0, 0);
                    mItemWidth = getDecoratedMeasuredWidth(scrap);
                    mItemHeight = getDecoratedMeasuredHeight(scrap);
                    layoutDecoratedWithMargins(scrap, startOffset - mDividerWidth - mItemWidth, top, startOffset - mDividerWidth, top + mItemHeight);
                    startOffset = startOffset - mDividerWidth - mItemWidth;
                }
            }
            long end = System.currentTimeMillis();
            Log.e(TAG, "fillView: " + (end - start) + getChildCount());
        }
    }

    private OrientationHelper getOrientationHelper() {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(this);
        }
        return mHorizontalHelper;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || dx == 0) {
            return 0;
        }
        removeCallbacks(autoLoop);
        Log.e(TAG, "scrollHorizontallyBy: " + dx + "=============" + getChildCount());
        int travel = dx;
        if (Math.abs(mScrollOffset + dx) > (mItemWidth + mDividerWidth) * mMaxShowCount) {
            travel = mScrollOffset > 0 ? (mItemWidth + mDividerWidth) * mMaxShowCount - mScrollOffset : -mScrollOffset - (mItemWidth + mDividerWidth) * mMaxShowCount;
        }
        mScrollOffset += travel;
        fillView(recycler,travel);
        offsetChildrenHorizontal(-travel);
        return travel;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

            @Override
            public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                return -mItemWidth - mDividerWidth;
            }

            //This returns the milliseconds it takes to
            //scroll one pixel.
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                Log.e(TAG, "calculateSpeedPerPixel: ======================" + displayMetrics.density);
                return 0.4f / displayMetrics.density;
                //返回滑动一个pixel需要多少毫秒
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (mScrollOffset != 0 && Math.abs(mScrollOffset) % (mItemWidth + mDividerWidth) == 0) {
                mScrollOffset = 0;
            }
            mRecyclerView.postDelayed(autoLoop, 3000);
        }
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        mRecyclerView.removeCallbacks(autoLoop);
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
