package com.mark.customview.MaterialDesign;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/12
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class ScaleUpShowBehavior extends FloatingActionButton.Behavior {
    private static final String TAG = ScaleUpShowBehavior.class.getSimpleName();
    private boolean isShow = true;

    public ScaleUpShowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        Log.e(TAG, "layoutDependsOn: ");
        if (isShow) {
            isShow =false;
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            child.setTranslationY(layoutParams.bottomMargin + child.getMeasuredHeight());
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyConsumed > 0 && dyUnconsumed == 0) {
            if (!isShow) {
                show(child);
                isShow = true;
            }
            System.out.println("上滑中。。。");
        }
        if (dyConsumed == 0 && dyUnconsumed > 0) {
            System.out.println("到边界了还在上滑。。。");
        }
        if (dyConsumed < 0 && dyUnconsumed == 0) {
            System.out.println("下滑中。。。");
        }
        if (dyConsumed == 0 && dyUnconsumed < 0) {
            System.out.println("到边界了，还在下滑。。。");
            if (isShow) {
                hint(child);
                isShow = false;
            }
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
    }

    private void hint(View view) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        view.animate().translationY(layoutParams.bottomMargin + view.getMeasuredHeight()).setDuration(500).start();
    }

    private void show(View view) {
        view.animate().translationY(0).setDuration(500).start();
    }
}
