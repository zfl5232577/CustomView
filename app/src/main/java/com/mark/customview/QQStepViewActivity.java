package com.mark.customview;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mark.customview.QQStepView.QQStepView;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/04
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class QQStepViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqstepview);
        final QQStepView qqStepView = findViewById(R.id.step_view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                qqStepView.setCurrentStep(8000);
            }
        }, 3000);
    }
}
