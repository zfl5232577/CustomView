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
import android.util.Log;
import android.view.View;


/**
 * RecyclerView分割线,优化含有头部和脚布局时也有分割线的BUG
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {
    //水平
    public static final int HORIZONTAL_LIST = RecyclerView.HORIZONTAL;
    //垂直
    public static final int VERTICAL_LIST = RecyclerView.VERTICAL;
    //水平+垂直
    public static final int BOTH_SET = 2;
    private Context mContext;
    private Paint mPaint;
    private Drawable mDrawable;
    private Drawable mDivider;
    private int mDividerHeight = 2;//分割线高度，默认为1px
    private int mOrientation;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private int mSpaceLeft = 0;
    private int mSpaceRight = 0;
    private boolean mShowBottomLine = true;
    private int mHeaderViewCount;
    private int mFooterViewCount;

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context
     * @param orientation 列表方向
     */
    public RecyclerViewDivider(Context context, int orientation) {
//        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
//            throw new IllegalArgumentException("请输入正确的参数！");
//        }
        this.mContext = context;
        mOrientation = orientation;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation 列表方向
     * @param drawableId  分割线图片
     */
    public RecyclerViewDivider(Context context, int orientation, int drawableId) {
        this(context, orientation);
        this.mContext = context;
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDivider.getIntrinsicHeight();
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public RecyclerViewDivider(Context context, int orientation, int dividerHeight, int dividerColor) {
        this(context, orientation);
        this.mContext = context;
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     * @param paddingLeft   左边间距
     * @param paddingRight  右边间距
     */
    public RecyclerViewDivider(Context context, int orientation, int dividerHeight, int dividerColor, int paddingLeft, int paddingRight) {
        this(context, orientation);
        this.mContext = context;
        mSpaceLeft = paddingLeft;
        mSpaceRight = paddingRight;
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation    列表方向
     * @param dividerHeight  分割线高度
     * @param dividerColor   分割线颜色
     * @param paddingLeft    左边间距
     * @param paddingRight
     * @param ShowBottomLine 是否显示最后一行的分割线
     */
    public RecyclerViewDivider(Context context, int orientation, int dividerHeight, int dividerColor, int paddingLeft, int paddingRight, boolean ShowBottomLine) {
        this(context, orientation);
        this.mContext = context;
        mSpaceLeft = paddingLeft;
        mSpaceRight = paddingRight;
        mShowBottomLine = ShowBottomLine;
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation    列表方向
     * @param dividerHeight  分割线高度
     * @param dividerColor   分割线颜色
     * @param ShowBottomLine 是否显示最后一行分割线
     */
    public RecyclerViewDivider(Context context, int orientation, int dividerHeight, int dividerColor, boolean ShowBottomLine) {
        this(context, orientation);
        this.mContext = context;
        mShowBottomLine = ShowBottomLine;
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
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
        //获取layoutParams参数
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        //当前位置
        int itemPosition = layoutParams.getViewLayoutPosition();
        if (itemPosition < mHeaderViewCount) {//测量的是HeaderView
            return;
        }
        itemPosition = itemPosition - mHeaderViewCount;
        //ItemView数量
        int childCount = parent.getAdapter().getItemCount() - mHeaderViewCount - mFooterViewCount;
        if (itemPosition == childCount) {//测量的是FooterView
            return;
        }
        switch (mOrientation) {
            case BOTH_SET:
                //获取Layout的相关参数
                int spanCount = this.getSpanCount(parent);
                if (isFristColum(parent, itemPosition, spanCount, childCount)) {
                    // 如果是最后一行，则不需要绘制底部
                    outRect.set(0, 0, mDividerHeight / 2, mDividerHeight);
                } else if (isLastColum(parent, itemPosition, spanCount, childCount)) {
                    // 如果是最后一列，则不需要绘制右边
                    outRect.set(mDividerHeight / 2, 0, 0, mDividerHeight);
                } else {
                    outRect.set(mDividerHeight / 2, 0, mDividerHeight / 2, mDividerHeight);
                }
//                if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
//                    // 如果是最后一行，则不需要绘制底部
//                    outRect.set(mDividerHeight/2, 0, mDividerHeight/2, 0);
//                }else {
//                    outRect.set(mDividerHeight/2, 0, mDividerHeight/2, mDividerHeight);
//                }
                break;
            case VERTICAL_LIST:
                childCount -= 1;
                //水平布局右侧留Margin,如果是最后一列,就不要留Margin了
                outRect.set(0, 0, (itemPosition != childCount) ? mDividerHeight : 0, 0);
                break;
            case HORIZONTAL_LIST:
                childCount -= 1;
                //垂直布局底部留边，最后一行不留
                outRect.set(0, 0, 0, (itemPosition != childCount || mShowBottomLine) ? mDividerHeight : 0);
                break;
        }
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        Log.e("caimakun", "onDraw:===================================== ");
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            if (parent.getLayoutManager() instanceof GridLayoutManager) {
                drawGridVertical(c, parent);
            }else {
                drawVertical(c, parent);
            }
        } else if (mOrientation == LinearLayoutManager.HORIZONTAL){
            drawHorizontal(c, parent,state);
        }else {
            if (parent.getLayoutManager() instanceof LinearLayoutManager) {
                drawVertical(c, parent);
            }else {
                drawGridVertical(c, parent);
            }
            drawHorizontal(c, parent,state);
        }
    }

    private void drawGridVertical(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        int childSize = parent.getChildCount() - mHeaderViewCount;
        int spanCount = this.getSpanCount(parent);
        for (int i = mHeaderViewCount; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (!isLastColum(parent,i-mHeaderViewCount,spanCount,childSize-mFooterViewCount)){
                if (mDrawable != null) {
                    mDrawable.setBounds(left, top, right, bottom);
                    mDrawable.draw(canvas);
                }
                if (mPaint != null) {
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }


    /**
     * 绘制横向 item 分割线
     *
     * @param canvas 画布
     * @param parent 父容器
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent,RecyclerView.State state) {
        int childSize = parent.getChildCount();
        int x = parent.getPaddingLeft() + mSpaceLeft;
        int width = parent.getMeasuredWidth() - parent.getPaddingRight() - mSpaceRight;
        int spanCount = this.getSpanCount(parent);
        for (int i = 0; i < childSize; i += spanCount) {
            final View child = parent.getChildAt(i);
            int pos = parent.getChildLayoutPosition(child);
            if (pos<mHeaderViewCount
                    || pos == state.getItemCount()-mFooterViewCount
                    || (!mShowBottomLine && pos==state.getItemCount()-mFooterViewCount - 1)){
                Log.e("caimakun", "drawHorizontal: =============>>>>>>>>>>>>>>>>>>>>>>>>>"+pos+mShowBottomLine+state.getItemCount() );
                continue;
            }
            RecyclerView.LayoutParams layoutParams =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            //item底部的Y轴坐标+margin值
            final int y = child.getBottom() + layoutParams.bottomMargin;
            final int height = y + mDividerHeight;
            if (mDrawable != null) {
                //setBounds(x,y,width,height); x:组件在容器X轴上的起点 y:组件在容器Y轴上的起点
                // width:组件的长度 height:组件的高度
                mDrawable.setBounds(x, y, width, height);
                mDrawable.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(x, y, width, height, mPaint);
            }
        }
    }

    /**
     * 绘制纵向 item 分割线
     *
     * @param canvas
     * @param parent
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        int childSize = 0;
        if (mShowBottomLine) {
            childSize = parent.getChildCount() - mHeaderViewCount;
        } else {
            childSize = parent.getChildCount() - mHeaderViewCount - 1;
        }
        for (int i = mHeaderViewCount; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDrawable != null) {
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    //dp转px
    public int dp2px(float dipValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取列数
     *
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent) {
        int spanCount = 1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
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
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        int orientation;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            orientation = ((GridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一行，则不需要绘制底部
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)
                    return true;
            } else {// StaggeredGridLayoutManager 横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                    return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一行，则不需要绘制底部
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)
                    return true;
            } else {// StaggeredGridLayoutManager 横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                    return true;
            }
        }
        return false;
    }
}
