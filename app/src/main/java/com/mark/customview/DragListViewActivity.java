package com.mark.customview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/04
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class DragListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draglistview);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("污污污污污");
        }
        recyclerView.setAdapter(new SimpleAdapterextends(list));
    }

    public static class SimpleAdapterextends extends BaseQuickAdapter<String, BaseViewHolder> {
        public SimpleAdapterextends(List<String> data) {

            super(R.layout.item_layout_letter_data_list,data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_name,item);
        }
    }
}
