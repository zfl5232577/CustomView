package com.mark.customview.RecyclerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


/**
 * RecyclerView分割线,优化含有头部和脚布局时也有分割线的BUG
 */
public class RecyclerViewVerticalDivider extends RecyclerView.ItemDecoration {
    private Context mContext;
    private Paint mPaint;
    private Drawable mDrawable;
    private int mDividerWidth = 1;//分割线宽度，默认为1px
    private int mSpaceTop = 0;
    private int mSpaceBottom = 0;
    private int mHeaderViewCount;
    private int mFooterViewCount;

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context
     */
    public RecyclerViewVerticalDivider(Context context) {
        this.mContext = context;
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param drawableId 分割线图片
     */
    public RecyclerViewVerticalDivider(Context context, int drawableId) {
        this(context);
        mDrawable = ContextCompat.getDrawable(context, drawableId);
        mDividerWidth = mDrawable.getIntrinsicWidth();
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerWidth 分割线高度
     * @param dividerColor 分割线颜色
     */
    public RecyclerViewVerticalDivider(Context context, int dividerWidth, int dividerColor) {
        this(context);
        mDividerWidth = dividerWidth;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerWidth  分割线宽度
     * @param dividerColor  分割线颜色
     * @param paddingTop    上边间距
     * @param paddingBottom 下边间距
     */
    public RecyclerViewVerticalDivider(Context context, int dividerWidth, int dividerColor, int paddingTop, int paddingBottom) {
        this(context, dividerWidth, dividerColor);
        mSpaceTop = paddingTop;
        mSpaceBottom = paddingBottom;
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerWidth  分割线高度
     * @param dividerColor  分割线颜色
     * @param paddingTop    上边间距
     * @param paddingBottom 下边间距
     */
    public RecyclerViewVerticalDivider(Context context, int dividerWidth, int dividerColor, int paddingTop, int paddingBottom, boolean isHasHeaderView, boolean isHasFooterView) {
        this(context, dividerWidth, dividerColor, paddingTop, paddingBottom);
        if (isHasHeaderView) {
            mHeaderViewCount = 1;//就添加了多头部布局，都是放在第一个Item里面，数量只有一个，脚部布局也一样。。
        }
        if (isHasFooterView)
            mFooterViewCount = 1;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public void setDividerWidth(int dividerWidth) {
        mDividerWidth = dividerWidth;
    }

    public void setSpaceTop(int spaceTop) {
        mSpaceTop = spaceTop;
    }

    public void setSpaceBottom(int spaceBottom) {
        mSpaceBottom = spaceBottom;
    }

    public void isHasHeaderView(boolean hasHeaderView) {
        if (hasHeaderView)
            mHeaderViewCount = 1;
    }

    public void isHasFooterView(boolean hasFooterView) {
        if (hasFooterView)
            mFooterViewCount = 1;
    }

    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //下面super...代码其实调用的就是那个过时的getItemOffsets,也就是说这个方法体内容也可以通通移到那个过时的getItemOffsets中
        super.getItemOffsets(outRect, view, parent, state);
        int spanCount = this.getSpanCount(parent, state);
        if (spanCount == 1) {
            return;
        }
        //获取layoutParams参数
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        //当前位置
        int itemPosition = layoutParams.getViewLayoutPosition();
        if (itemPosition < mHeaderViewCount) {//测量的是HeaderView
            return;
        }
        itemPosition = itemPosition - mHeaderViewCount;
        //ItemView数量
        int childCount = state.getItemCount() - mHeaderViewCount - mFooterViewCount;
        if (itemPosition == childCount) {//测量的是FooterView
            return;
        }
        if (isFristColum(parent, itemPosition, spanCount, childCount)) {
            // 如果是最后一行，则不需要绘制底部
            outRect.set(0, 0, mDividerWidth / 2, 0);
        } else if (isLastColum(parent, itemPosition, spanCount, childCount)) {
            // 如果是最后一列，则不需要绘制右边
            outRect.set(mDividerWidth / 2, 0, 0, 0);
        } else {
            outRect.set(mDividerWidth / 2, 0, mDividerWidth / 2, 0);
        }
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int spanCount = this.getSpanCount(parent, state);
        if (spanCount == 1) {
            return;
        }
        final int top = parent.getPaddingTop() + mSpaceTop;
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom() - mSpaceBottom;
        int childSize = parent.getChildCount();//界面显示的View的个数
        for (int i = 0; i < childSize; i++) {
            if (i > spanCount) {
                break;
            }
            final View child = parent.getChildAt(i);
            //假如HeaderView和FooterView就不画
            int pos = parent.getChildLayoutPosition(child);
            if (pos < mHeaderViewCount
                    || pos == state.getItemCount() - mFooterViewCount) {
                continue;
            }
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerWidth;
            if (mDrawable != null) {
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(c);
            }
            if (mPaint != null) {
                c.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    /**
     * 获取列数
     *
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent, RecyclerView.State state) {
        int spanCount = 1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        } else {
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                return state.getItemCount();
            }
        }
        return spanCount;
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int orientation = ((GridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0)
                    return true;
            } else {
                childCount = childCount - childCount % spanCount == 0 ? spanCount : childCount % spanCount;
                // 如果是最后一列，则不需要绘制右边
                if (pos >= childCount)
                    return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0)
                    return true;
            } else {
                childCount = childCount - childCount % spanCount == 0 ? spanCount : childCount % spanCount;
                // 如果是最后一列，则不需要绘制右边
                if (pos >= childCount)
                    return true;
            }
        } else {
            if (spanCount == childCount && pos == childCount - 1) {
                return true;
            }
        }
        return false;
    }

    private boolean isFristColum(RecyclerView parent, int pos, int spanCount,
                                 int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int orientation = ((GridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 1)
                    return true;
            } else {
                if (pos < spanCount)
                    return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 1)
                    return true;
            } else {
                if (pos < spanCount)
                    return true;
            }
        } else {
            if (spanCount == childCount && pos == 0) {
                return true;
            }
        }
        return false;
    }
}
