package com.mark.customview.MultiShapeProgressView;

import android.view.animation.Interpolator;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/05
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */

public class HesitateInterpolator implements Interpolator {

    public HesitateInterpolator() {}

    @Override
    public float getInterpolation(float input) {
        float x = 2.0f * input - 1.0f;
        return 0.5f * (x * x * x + 1.0f);
    }
}
