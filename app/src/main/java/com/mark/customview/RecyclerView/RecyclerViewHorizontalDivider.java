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
 * RecyclerView水平分割线,优化含有头部和脚布局时也有分割线的BUG
 */
public class RecyclerViewHorizontalDivider extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Paint mPaint;
    private Drawable mDrawable;
    private int mDividerHeight = 1;//分割线高度，默认为1px
    private int mSpaceLeft = 0;
    private int mSpaceRight = 0;
    private boolean mShowBottomLine = true;
    private int mHeaderViewCount;
    private int mFooterViewCount;

    /**
     * 默认分割线：高度为2px，颜色为灰色
     */
    public RecyclerViewHorizontalDivider(Context context) {
        this.mContext = context;
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param drawableId  分割线图片
     */
    public RecyclerViewHorizontalDivider(Context context,int drawableId) {
        this(context);
        mDrawable = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDrawable.getIntrinsicHeight();
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public RecyclerViewHorizontalDivider(Context context,int dividerHeight, int dividerColor) {
        this(context);
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     * @param paddingLeft   左边间距
     * @param paddingRight  右边间距
     */
    public RecyclerViewHorizontalDivider(Context context, int dividerHeight, int dividerColor, int paddingLeft, int paddingRight) {
        this(context,dividerHeight,dividerColor);
        mSpaceLeft = paddingLeft;
        mSpaceRight = paddingRight;
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerHeight  分割线高度
     * @param dividerColor   分割线颜色
     * @param paddingLeft    左边间距
     * @param paddingRight
     * @param ShowBottomLine 是否显示最后一行的分割线
     */
    public RecyclerViewHorizontalDivider(Context context, int dividerHeight, int dividerColor, int paddingLeft, int paddingRight, boolean ShowBottomLine) {
        this(context,dividerHeight,dividerColor,paddingLeft,paddingRight);
        mShowBottomLine = ShowBottomLine;
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerHeight  分割线高度
     * @param dividerColor   分割线颜色
     * @param ShowBottomLine 是否显示最后一行分割线
     * @param isHasHeaderView 列表是否有头部布局
     * @param isHasFooterView 列表是否有脚部布局
     */
    public RecyclerViewHorizontalDivider(Context context, int dividerHeight, int dividerColor,int paddingLeft, int paddingRight, boolean ShowBottomLine,boolean isHasHeaderView,boolean isHasFooterView) {
        this(context,dividerHeight,dividerColor,paddingLeft,paddingRight,ShowBottomLine);
        if(isHasHeaderView){
            mHeaderViewCount = 1;//就添加了多头部布局，都是放在第一个Item里面，数量只有一个，脚部布局也一样。。
        }
        if (isHasFooterView)
            mFooterViewCount = 1;
    }


    public void isHasHeaderView(boolean hasHeaderView) {
        if (hasHeaderView)
            mHeaderViewCount = 1;
    }

    public void isHasFooterView(boolean hasFooterView) {
        if (hasFooterView)
            mFooterViewCount = 1;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public void setDividerHeight(int dividerHeight) {
        mDividerHeight = dividerHeight;
    }

    public void setSpaceLeft(int spaceLeft) {
        mSpaceLeft = spaceLeft;
    }

    public void setSpaceRight(int spaceRight) {
        mSpaceRight = spaceRight;
    }

    public void setShowBottomLine(boolean showBottomLine) {
        mShowBottomLine = showBottomLine;
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
        if (itemPosition < mHeaderViewCount) {//当前是HeaderView，不需要测量
            return;
        }
        itemPosition = itemPosition - mHeaderViewCount;
        //ItemView数量
        int childCount = parent.getAdapter().getItemCount() - mHeaderViewCount - mFooterViewCount;
        if (itemPosition == childCount) {//当前是FooterView，不需要测量
            return;
        }
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager){
            throw new RuntimeException( "RecyclerViewHorizontalDivider not support StaggeredGridLayoutManager");
        } else {
            childCount -= 1;
            //垂直布局底部留边，最后一行不留
            outRect.set(0, 0, 0, (itemPosition != childCount || mShowBottomLine) ? mDividerHeight : 0);
        }
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int childSize = parent.getChildCount();//界面显示的View的个数
        int x = parent.getPaddingLeft() + mSpaceLeft;
        int width = parent.getMeasuredWidth() - parent.getPaddingRight() - mSpaceRight;
        int spanCount = this.getSpanCount(parent);
        for (int i = 0; i < childSize; i += spanCount) {
            final View child = parent.getChildAt(i);
            //假如HeaderView和FooterView就不画
            int pos = parent.getChildLayoutPosition(child);
            if (pos<mHeaderViewCount
                    || pos == state.getItemCount()-mFooterViewCount
                    || (!mShowBottomLine && pos>=state.getItemCount()-mFooterViewCount - spanCount)){
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
                mDrawable.draw(c);
            }
            if (mPaint != null) {
                c.drawRect(x, y, width, height, mPaint);
            }
        }
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
}
