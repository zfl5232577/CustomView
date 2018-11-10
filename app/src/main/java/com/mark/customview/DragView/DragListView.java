package com.mark.customview.DragView;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/08
 *     desc   : 附带菜单的布局，关键掌握ViewDragHelper和事件分发
 *     v1.0事件不能连贯解决方案(https://blog.csdn.net/y874961524/article/details/79780321)——当然也可是使用NestScroll机制进行解决，NestScroll机制没有上述的两种问题。
 *     version: 1.0
 * </pre>
 */
public class DragListView extends FrameLayout {
    private static final String TAG = DragListView.class.getSimpleName();
    private ViewDragHelper mViewDragHelper;
    private ViewDragHelper mMenuViewDragHelper;
    private View mMenuView;
    private View mDragContentView;
    private int mMenuHeight;

    private static final int STATE_OPEN = 1;
    private static final int STATE_CLOSE = 0;
    private int state = STATE_CLOSE;
    private boolean needReset = true;

    @IntDef({STATE_OPEN, STATE_CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }


    public DragListView(Context context) {
        this(context, null);
    }

    public DragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private float mScrollY;

    private void init() {
        mMenuViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mMenuView;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (top >= 0) {
                    return 0;
                }
                return top > -mMenuHeight ? top : -mMenuHeight;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                mMenuView.setTranslationY(-top);
                mDragContentView.offsetTopAndBottom(dy);
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (yvel > 3000) {
                    mMenuViewDragHelper.smoothSlideViewTo(mMenuView, 0, 0);
                    setState(STATE_OPEN);
                } else if (yvel < -3000) {
                    mMenuViewDragHelper.smoothSlideViewTo(mMenuView, 0, -mMenuHeight);
                    setState(STATE_CLOSE);
                } else {
                    if (mDragContentView.getTop() > mMenuHeight / 2) {
                        mMenuViewDragHelper.smoothSlideViewTo(mMenuView, 0, 0);
                        setState(STATE_OPEN);
                    } else {
                        mMenuViewDragHelper.smoothSlideViewTo(mMenuView, 0, -mMenuHeight);
                        setState(STATE_CLOSE);
                    }
                }
                invalidate();
            }
        });
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mDragContentView;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (child.getTop() == 0) {//手指假如显示滑动列表再拖动，那么dy就会假如滑动列表的距离
                    top = (int) (top - mScrollY);
                }
                if (top <= 0) {
                    return 0;
                }
                return top < mMenuHeight ? top : mMenuHeight;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (mMenuView.getTop() < 0 ) {
                    mMenuView.setTranslationY(mMenuHeight-top);
                    mMenuView.offsetTopAndBottom(dy);
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (yvel > 3000) {
                    openMenu();
                } else if (yvel < -3000) {
                    closeMenu();
                } else {
                    if (releasedChild.getTop() > mMenuHeight / 2) {
                        openMenu();
                    } else {
                        closeMenu();
                    }
                }
            }

        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMenuHeight = getChildAt(0).getMeasuredHeight();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取LinearLayout
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new RuntimeException("container must have tow child view");
        }

        //伪代码，写死的，后期可以优化
        mMenuView = getChildAt(0);
        mDragContentView = getChildAt(1);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        //super.requestDisallowInterceptTouchEvent(disallowIntercept);
        // 但是需要继续向上传递，否则ListView之类的控件很多状态会错误
        getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private float mDownY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onInterceptTouchEvent: ACTION_DOWN");
                needReset = true;
                mDownY = ev.getY();
                mMenuViewDragHelper.processTouchEvent(ev);
                mViewDragHelper.processTouchEvent(ev);//防止拦截后mViewDragHelper收不到一套完整的事件，导致无法拖动
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onInterceptTouchEvent: ACTION_MOVE");
                if (isOpen()) {
                    return true;
                } else {
                    if (ev.getY() > mDownY && !canChildScrollUp()) {
                        mScrollY = ev.getY() - mDownY;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onInterceptTouchEvent: ACTION_UP");
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onTouchEvent: ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                if (needReset && event.getY() - mScrollY < mDownY && mDragContentView.getTop() == 0) {
                    setState(STATE_CLOSE);
                    resetTouchEvent(event);
                }
                Log.e(TAG, "onTouchEvent: ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onTouchEvent: ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG, "onTouchEvent: ACTION_CANCEL");
                break;
        }
        try {
            mMenuViewDragHelper.processTouchEvent(event);
            mViewDragHelper.processTouchEvent(event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return true;
    }

    // 使子View恢复事件
    private void resetTouchEvent(MotionEvent event) {
        needReset = false;
        MotionEvent ev = MotionEvent.obtain(event);
        ev.setAction(MotionEvent.ACTION_UP);
        MotionEvent ev2 = MotionEvent.obtain(event);
        ev2.setAction(MotionEvent.ACTION_DOWN);
        dispatchTouchEvent(ev);
        dispatchTouchEvent(ev2);
        ev.recycle();
        ev2.recycle();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mMenuViewDragHelper.continueSettling(true)) {
            invalidate();
        }
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    private void openMenu() {
        setState(STATE_OPEN);
        mViewDragHelper.settleCapturedViewAt(0, mMenuHeight);
        invalidate();
    }

    private void closeMenu() {
        setState(STATE_CLOSE);
        mViewDragHelper.settleCapturedViewAt(0, 0);
        invalidate();
    }

    @State
    private int getState() {
        return state;
    }

    private void setState(@State int state) {
        this.state = state;
    }

    public boolean isOpen() {
        return this.state == STATE_OPEN;
    }

    public boolean canChildScrollUp() {
        if (mDragContentView instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mDragContentView, -1);
        }
        return mDragContentView.canScrollVertically(-1);
    }
}