package com.mark.customview.ItemSwipeMenuLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.mark.customview.R;


/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/12
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class ItemSwipeMenuLayout extends FrameLayout {

    private static final String TAG = ItemSwipeMenuLayout.class.getSimpleName();
    private final Scroller mScroller;
    private final int mScaledTouchSlop;
    private int mContentViewId;
    private final int mLeftMenuViewId;
    private boolean mEnableRightSwipe;
    private int mRightMenuViewId;
    private boolean mEnableLeftSwipe;
    private View mContentView;
    private View mLeftMenuView;
    private View mRightMenuView;
    private boolean isSwipeing;
    private PointF mFirstP;
    private State mState = State.CLOSE;
    private MarginLayoutParams mRightMenuViewLp;
    private MarginLayoutParams mLeftMenuViewLp;
    private MarginLayoutParams mContentViewLp;

    public ItemSwipeMenuLayout(@NonNull Context context) {
        this(context, null);
    }

    public ItemSwipeMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemSwipeMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ItemSwipeMenuLayout);
        mContentViewId = array.getResourceId(R.styleable.ItemSwipeMenuLayout_contentView, 0);
        mLeftMenuViewId = array.getResourceId(R.styleable.ItemSwipeMenuLayout_leftMenuView, 0);
        mRightMenuViewId = array.getResourceId(R.styleable.ItemSwipeMenuLayout_rightMenuView, 0);
        mEnableLeftSwipe = array.getBoolean(R.styleable.ItemSwipeMenuLayout_enableLeftSwipe, true);
        mEnableRightSwipe = array.getBoolean(R.styleable.ItemSwipeMenuLayout_enableRightSwipe, true);
        array.recycle();
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
        this.setFocusable(true);
        setFocusableInTouchMode(true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && mState!=State.CLOSE){
                    handlerSwipeMenu(State.CLOSE);
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = findViewById(mContentViewId);
        if (mContentView == null) {
            throw new RuntimeException("mContentView is null, app:contentView must add");
        }
        mLeftMenuView = findViewById(mLeftMenuViewId);
        mRightMenuView = findViewById(mRightMenuViewId);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        mContentViewLp = (MarginLayoutParams) mContentView.getLayoutParams();
        int cl = paddingLeft + mContentViewLp.leftMargin;
        int ct = paddingTop + mContentViewLp.topMargin;
        int cr = cl + mContentView.getMeasuredWidth();
        int cb = ct + mContentView.getMeasuredHeight();
        mContentView.layout(cl, ct, cr, cb);

        if (mLeftMenuView != null) {
            mLeftMenuViewLp = (MarginLayoutParams) mLeftMenuView.getLayoutParams();
            int lr = 0 - mLeftMenuViewLp.rightMargin;
            int ll = lr - mLeftMenuView.getMeasuredHeight();
            int lt = paddingTop + mLeftMenuViewLp.topMargin;
            int lb = lt + mLeftMenuView.getMeasuredHeight();
            mLeftMenuView.layout(ll, lt, lr, lb);
        }

        if (mRightMenuView != null) {
            mRightMenuViewLp = (MarginLayoutParams) mRightMenuView.getLayoutParams();
            int rl = mContentView.getRight() + mContentViewLp.rightMargin + mRightMenuViewLp.leftMargin;
            int rr = rl + mRightMenuView.getMeasuredWidth();
            int rt = paddingTop + mRightMenuViewLp.topMargin;
            int rb = rt + mRightMenuView.getMeasuredHeight();
            mRightMenuView.layout(rl, rt, rr, rb);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                requestFocus();
                if (mFirstP == null) {
                    mFirstP = new PointF();
                }
                mFirstP.set(event.getRawX() + getScrollX(), event.getRawY());
                Log.e(TAG, "onInterceptTouchEvent: ACTION_DOWN");
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                Log.e(TAG, "onInterceptTouchEvent: ACTION_MOVE");
                //滑动时拦截点击时间
                if (Math.abs(mFirstP.x - event.getRawX()) > mScaledTouchSlop) {
                    Log.e(TAG, "onInterceptTouchEvent: 拦截");
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                Log.e(TAG, "onInterceptTouchEvent: ACTION_UP");
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Log.e(TAG, "onTouchEvent: ACTION_DOWN");
                isSwipeing = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                isSwipeing = true;
                Log.e(TAG, "onTouchEvent: ACTION_MOVE"+hasFocus());
                float distanceX = mFirstP.x - ev.getRawX();
                Log.e(TAG, "onTouchEvent: " + distanceX);
                scrollTo((int) (distanceX), 0);//滑动使用scrollBy
                //越界修正
                if (getScrollX() < 0) {
                    if (!mEnableRightSwipe || mLeftMenuView == null) {
                        scrollTo(0, 0);
                    } else {//左滑
                        if (getScrollX() < mLeftMenuView.getLeft() - mLeftMenuViewLp.leftMargin) {
                            scrollTo(mLeftMenuView.getLeft() - mLeftMenuViewLp.leftMargin, 0);
                        }
                    }
                } else if (getScrollX() > 0) {
                    if (!mEnableLeftSwipe || mRightMenuView == null) {
                        scrollTo(0, 0);
                    } else {
                        if (getScrollX() > mRightMenuView.getMeasuredWidth() + mRightMenuViewLp.leftMargin + mRightMenuViewLp.rightMargin) {
                            scrollTo(mRightMenuView.getMeasuredWidth() + mRightMenuViewLp.leftMargin + mRightMenuViewLp.rightMargin, 0);
                        }
                    }
                }
                //当处于水平滑动时，禁止父类拦截
                if (Math.abs(distanceX) > mScaledTouchSlop) {
                    //  Log.i(TAG, ">>>>当处于水平滑动时，禁止父类拦截 true");
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                Log.e(TAG, "onTouchEvent: ACTION_UP");
                handlerSwipeMenu(isShouldOpen(getScrollX()));
                if (!isSwipeing && mState != State.CLOSE) {
                    handlerSwipeMenu(State.CLOSE);
                    return true;
                }
                break;
            }
            default: {
                break;
            }
        }
        return isSwipeing ? isSwipeing : super.onTouchEvent(ev);

    }

    @Override
    public void computeScroll() {
        //判断Scroller是否执行完毕：
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //通知View重绘-invalidate()->onDraw()->computeScroll()
            invalidate();
        }
    }

    /**
     * 自动设置状态
     *
     * @param result
     */

    private void handlerSwipeMenu(State result) {
        if (result == State.LEFTOPEN) {
            mScroller.startScroll(getScrollX(), 0, mLeftMenuView.getLeft() - getScrollX(), 0);
        } else if (result == State.RIGHTOPEN) {
            mScroller.startScroll(getScrollX(), 0, mRightMenuView.getRight() - mRightMenuView.getLeft() - getScrollX(), 0);
        } else {
            mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
        }
        mState = result;
        invalidate();
    }


    /**
     * 根据当前的scrollX的值判断松开手后应处于何种状态
     *
     * @param
     * @param scrollX
     * @return
     */
    private State isShouldOpen(int scrollX) {
        if (!(mScaledTouchSlop < Math.abs(scrollX))) {
            return mState;
        }
        if (scrollX < 0) {
            //➡滑动
            //1、展开左边按钮
            //获得leftView的测量长度
            if (getScrollX() < 0 && mLeftMenuView != null) {
                if (Math.abs(mLeftMenuView.getWidth() * 0.3) < Math.abs(getScrollX())) {
                    return State.LEFTOPEN;
                }
            }
            //2、关闭右边按钮

            if (getScrollX() > 0 && mRightMenuView != null) {
                return State.CLOSE;
            }
        } else if (scrollX > 0) {
            //⬅️滑动
            //3、开启右边菜单按钮
            if (getScrollX() > 0 && mRightMenuView != null) {

                if (Math.abs(mRightMenuView.getWidth() * 0.3) < Math.abs(getScrollX())) {
                    return State.RIGHTOPEN;
                }

            }
            //关闭左边
            if (getScrollX() < 0 && mLeftMenuView != null) {
                return State.CLOSE;
            }
        }

        return State.CLOSE;

    }

    public enum State {
        LEFTOPEN,
        RIGHTOPEN,
        CLOSE,
    }
}
