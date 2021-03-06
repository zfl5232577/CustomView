package com.mark.customview;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mark.customview.QQStepView.QQStepView;
import com.mark.customview.SquaredLatticeLockView.SquaredLatticeLockView;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/04
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class SquaredLatticeLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squared_lattice_lock);
        SquaredLatticeLockView squaredLatticeLockView = findViewById(R.id.squaredLatticeLockView);
        squaredLatticeLockView.setUnlockListener(new SquaredLatticeLockView.UnlockListener() {
            @Override
            public void onUnlockSuccess() {
               finish();
            }

            @Override
            public void onUnlockFail() {

            }
        });
    }
}
