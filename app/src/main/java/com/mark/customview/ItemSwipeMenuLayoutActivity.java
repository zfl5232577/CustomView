package com.mark.customview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.promeg.pinyinhelper.Pinyin;
import com.mark.customview.LetterIndexBar.LetterIndexAbstract;

import java.util.ArrayList;
import java.util.List;

public class ItemSwipeMenuLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemswipemenu);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final List<String> data = new ArrayList<>();
        String s = "地质学，与数学，物理，化学，生物并列的自然科学五大基础学科之一。地质学是一门探讨地球如何演化的自然哲学，地质学的产生源于人类社会对石油、煤炭、金属、非金属等矿产资源的需求，由地质学所指导的地质矿产资源勘探是人类社会生存与发展的根本源泉。地质学是研究地球的物质组成、内部构造、外部特征、各层圈之间的相互作用和演变历史的知识体系。随着社会生产力的发展，人类活动对地球的影响越来越大，地质环境对人类的制约作用也越来越明显。如何合理有效的利用地球资源、维护人类生存的环境，已成为当今世界所共同关注的问题。因此，地质学研究领域进一步拓展到人地相互作用。" +
                "地质学（geology）的研究对象为地球的固体硬壳---地壳或岩石圈，她主要研究地球的物质组成、内部构造、外部特征、各层圈之间的相互作用和演变历史的知识体系。是研究地球及其演变的一门自然科学。" + "地球自形成以来，经历了约46亿年的演化过程，进行过错综复杂的物理、化学变化，同时还受天文变化的影响，所以各个层圈均在不断演变,约在35亿年前，地球上出现了生命现象，于是生物成为一种地质营力。最晚在距今200～300万年前，开始有人类出现。人类为了生存和发展，一直在努力适应和改变周围的环境。利用坚硬岩石作为用具和工具，从矿石中提取铜、铁等金属，对人类社会的历史产生过划时代的影响。";
        for (int i = 0, size = s.length(); i < size; i += 4) {
            data.add(s.substring(i, i + 4 >= size ? size - 1 : i + 4));
        }
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        TestAdapter adapter = new TestAdapter(R.layout.item_swipe_layout_item, data);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(ItemSwipeMenuLayoutActivity.this, "item被点击" + position, Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.lift_view:
                        Toast.makeText(ItemSwipeMenuLayoutActivity.this, "lift", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.right_view:
                        Toast.makeText(ItemSwipeMenuLayoutActivity.this, "right", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tv_name:
                        Toast.makeText(ItemSwipeMenuLayoutActivity.this, "tv_name", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public static class TestAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        public TestAdapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_name, item);
            helper.addOnClickListener(R.id.lift_view)
                    .addOnClickListener(R.id.right_view);
        }
    }
}
