package com.mark.customview.SlidingMenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.mark.customview.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/08
 *     desc   : 侧滑菜单几乎使用伪代码实现，这是练手用的，项目中还是用的系统的DrawerLayout
 *     version: 1.0
 * </pre>
 */
public class QQSlidingMenuLayout extends HorizontalScrollView {

    private static final String TAG = SlidingMenuLayout.class.getSimpleName();
    private int mMenuMargin;
    private int mMenuWidth;
    private boolean isIntercept;
    boolean isFirstTouch = true;
    boolean enableScorll = true;
    private final GestureDetector mGestureDetector;
    private View mContentView, mMenuView;

    private static final int STATE_OPEN = 1;
    private static final int STATE_CLOSE = 0;
    private float x;
    private float y;
    private View mShadownView;

    @IntDef({STATE_OPEN, STATE_CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    private int state = STATE_CLOSE;

    public QQSlidingMenuLayout(Context context) {
        this(context, null);
    }

    public QQSlidingMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQSlidingMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenuLayout);
        mMenuMargin = (int) array.getDimension(R.styleable.SlidingMenuLayout_menuMargin, dp2px(context, 80));
        mMenuWidth = getScreenWidth(context) - mMenuMargin;
        array.recycle();
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (isOpen()) {
                    if (velocityX < 0) {
                        closeMenu();
                        return true;
                    }
                } else {
                    if (velocityX > 0) {
                        openMenu();
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }


    //伪代码，布局文件结构固定，此控件做为根，里面包含的第一个子View必须是横向LinearLayout,然后
    //LinearLayout中只能包含两个子View.分别是菜单部分和内容不。嵌套较多，不适合实际项目
    // 这个方法是布局解析完毕也就是 XML 布局文件解析完毕
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取LinearLayout
        ViewGroup container = (ViewGroup) getChildAt(0);
        int containerChildCount = container.getChildCount();
        if (containerChildCount != 2) {
            throw new RuntimeException("container must have tow child view");
        }

        mMenuView = container.getChildAt(0);
        ViewGroup.LayoutParams menuParams = mMenuView.getLayoutParams();
        menuParams.width = mMenuWidth;
        mMenuView.setLayoutParams(menuParams);

        mContentView = container.getChildAt(1);
        ViewGroup.LayoutParams contentParams = mContentView.getLayoutParams();
        contentParams.width = mMenuWidth + mMenuMargin;
        mContentView.setLayoutParams(contentParams);

        container.removeView(mContentView);

        FrameLayout mContentViewParent = new FrameLayout(mContentView.getContext());
        mContentViewParent.setLayoutParams(contentParams);
        mContentViewParent.addView(mContentView);

        mShadownView = new View(mContentView.getContext());
        mShadownView.setLayoutParams(contentParams);
        mShadownView.setBackgroundColor(Color.parseColor("#333333"));
        mShadownView.setAlpha(0f);
        mContentViewParent.addView(mShadownView);

        container.addView(mContentViewParent);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollTo(mMenuWidth, 0);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        isIntercept = false;
        if (isOpen()) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            if (x > mMenuWidth) {
                isIntercept = true;
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (enableScorll && mGestureDetector.onTouchEvent(ev)) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = ev.getRawX();
                y = ev.getRawY();
                isFirstTouch = true;
                enableScorll = true;
                break;
            case MotionEvent.ACTION_UP:
                if (isIntercept) {
                    closeMenu();
                    return true;
                }
                int scrollX = getScrollX();
                if (scrollX > mMenuWidth / 2) {
                    closeMenu();
                } else {
                    openMenu();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isFirstTouch) {
                    isFirstTouch = false;
                    if (!isIntercept && Math.abs(ev.getRawY() - y) > Math.abs(ev.getRawX() - x)) {
                        enableScorll = false;
                    }
                }
                if (!enableScorll) {
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        isIntercept = false;
        float scale = ((float) l) / mMenuWidth;
        mMenuView.setTranslationX(l * 0.4f);
        mShadownView.setAlpha((float) (0.6*(1 - scale)));

    }

    private void openMenu() {
        setState(STATE_OPEN);
        smoothScrollTo(0, 0);
    }

    private void closeMenu() {
        setState(STATE_CLOSE);
        smoothScrollTo(mMenuWidth, 0);
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        this.state = state;
    }

    public boolean isOpen() {
        return this.state == STATE_OPEN;
    }

    private static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5F);
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
