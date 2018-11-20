package com.mark.customview.RecyclerView;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
 *     desc   : 左右滑动飞出卡片布局
 *     version: 1.0
 * </pre>
 */
public class CardLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = CardLayoutManager.class.getSimpleName();
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView.Recycler recycler;
    private int mMaxShowCount = 4;
    private int mCurrentPosition = 0;

    public CardLayoutManager(final RecyclerView recyclerView, int maxShowCount) {
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.itemView == getChildAt(getChildCount() - 1)) {
                    return makeMovementFlags(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END);
                }
                return 0;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.e(TAG, "onSwiped: ================>");
                viewHolder.itemView.setRotation(0);
                mCurrentPosition = (mCurrentPosition + 1) % getItemCount();
                Log.e(TAG, "onSwiped: " + mCurrentPosition + "====" + getChildCount());
                fillView(recycler);
            }

            @Override
            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                return 0.5f;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                int rotation = (int) (dX / getWidth() / 2 * 45);
                viewHolder.itemView.setRotation(rotation);
            }
        });
        mMaxShowCount = maxShowCount;
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public boolean isViewCenterInHorizontal(View view) {
        int viewCenterX = (int) (view.getX() + view.getWidth() / 2);
        Log.e(TAG, "isViewCenterInHorizontal: " + viewCenterX);
        return viewCenterX >= view.getLeft() && viewCenterX <= view.getRight();
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
        this.recycler = recycler;
        detachAndScrapAttachedViews(recycler);
        fillView(recycler);
    }

    private void fillView(RecyclerView.Recycler recycler) {
        Log.e(TAG, "fillView: " + mCurrentPosition);
        int childCount = getChildCount();
        if (childCount == 0) {
            for (int i = mMaxShowCount - 1; i >= 0; i--) {
                CardView view = (CardView) recycler.getViewForPosition((mCurrentPosition + i) % getItemCount());
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                //我们在布局时，将childView居中处理，这里也可以改为只水平居中
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 3,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 3 + getDecoratedMeasuredHeight(view));
                view.setTranslationY(75 * i);
                view.setScaleX((float) (1 - 0.09 * i));
                view.setScaleY((float) (1 - 0.09 * i));
                view.setCardElevation(mMaxShowCount - i);
            }
        } else if (childCount == mMaxShowCount) {
            Log.e(TAG, "fillView: " + childCount);
            removeAndRecycleView(getChildAt(getChildCount() - 1), recycler);
            Log.e(TAG, "fillView: "+getChildAt(getChildCount() - 1) );
            CardView view = (CardView) recycler.getViewForPosition((mCurrentPosition + mMaxShowCount - 1) % getItemCount());
            addView(view, 0);
            Log.e(TAG, "fillView: "+view );
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
