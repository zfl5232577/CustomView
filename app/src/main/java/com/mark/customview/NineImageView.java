package com.mark.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/06
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class NineImageView extends ViewGroup {
    public NineImageView(Context context) {
        super(context);
    }

    public NineImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NineImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
