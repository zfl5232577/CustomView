package com.mark.customview.RecyclerView;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.mark.customview.RecyclerView.ItemTouchHelper.ItemTouchHelper;

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
    private SnapHelper mSnapHelper;
    private int mMaxShowCount = 1;//默认显示一张图片。可以显示多张
    private int mCurrentPosition = 0;
    private int mSurplusWidth = 0;//左右两边剩余的宽度

    public BannerLayoutManager(final RecyclerView recyclerView, int maxShowCount) {
        mMaxShowCount = maxShowCount;
        mSnapHelper = new LinearSnapHelper();
//        mSnapHelper.attachToRecyclerView(recyclerView);
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
            for (int i = 0; i <mMaxShowCount+2; i++) {
                View view = recycler.getViewForPosition((mCurrentPosition + getItemCount()-1+i) % getItemCount());
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int width = getDecoratedMeasuredWidth(view);
                int height = getDecoratedMeasuredHeight(view);
                int dividerWidth = (getWidth()-width*mMaxShowCount-2*mSurplusWidth)/(mMaxShowCount+1);
                //我们在布局时，将childView居中处理，这里也可以改为只水平居中
                int left =  (mSurplusWidth-width)+i*(width+dividerWidth);
                int top = getPaddingTop();
                int right = left+width;
                int bottom = top+height;
                layoutDecoratedWithMargins(view, left, top, right, bottom);
            }
        } else if (childCount == mMaxShowCount) {
            Log.e(TAG, "fillView: " + childCount);
            removeAndRecycleView(getChildAt(getChildCount() - 1), recycler);
            CardView view = (CardView) recycler.getViewForPosition((mCurrentPosition + mMaxShowCount - 1) % getItemCount());
            addView(view, 0);
            measureChildWithMargins(view, 0, 0);
            int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
            int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
            //我们在布局时，将childView居中处理，这里也可以改为只水平居中
            layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 3,
                    widthSpace / 2 + getDecoratedMeasuredWidth(view),
                    heightSpace / 3 + getDecoratedMeasuredHeight(view));
            view.setTranslationY(75 * mMaxShowCount);
            view.setScaleX((float) (1 - 0.09 * mMaxShowCount));
            view.setScaleY((float) (1 - 0.09 * mMaxShowCount));
            view.setCardElevation(0);
            for (int i = mMaxShowCount - 1; i >= 0; i--) {
                ((CardView) getChildAt(i)).setCardElevation(i + 1);
                ViewCompat.animate(getChildAt(i)).scaleX((float) (1 - 0.09 * (mMaxShowCount - 1 - i)))
                        .scaleY((float) (1 - 0.09 * (mMaxShowCount - 1 - i)))
                        .translationY(75 * (mMaxShowCount - 1 - i)).setDuration(250).start();
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
        offsetChildrenHorizontal(-dx);
        return dx;
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
