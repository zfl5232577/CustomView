package com.mark.customview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.promeg.pinyinhelper.Pinyin;
import com.mark.customview.LetterIndexBar.LetterIndexAbstract;
import com.mark.customview.LetterIndexBar.LetterIndexBar;

import java.util.ArrayList;
import java.util.List;

public class LetterIndexBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_index_bar);
        final LetterIndexBar letterIndexBar = findViewById(R.id.letterIndexBar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        TextView  textView = findViewById(R.id.text);
        final List<LetterIndex> indexList = new ArrayList<>();
        String s = "地质学，与数学，物理，化学，生物并列的自然科学五大基础学科之一。地质学是一门探讨地球如何演化的自然哲学，地质学的产生源于人类社会对石油、煤炭、金属、非金属等矿产资源的需求，由地质学所指导的地质矿产资源勘探是人类社会生存与发展的根本源泉。地质学是研究地球的物质组成、内部构造、外部特征、各层圈之间的相互作用和演变历史的知识体系。随着社会生产力的发展，人类活动对地球的影响越来越大，地质环境对人类的制约作用也越来越明显。如何合理有效的利用地球资源、维护人类生存的环境，已成为当今世界所共同关注的问题。因此，地质学研究领域进一步拓展到人地相互作用。" +
                "地质学（geology）的研究对象为地球的固体硬壳---地壳或岩石圈，她主要研究地球的物质组成、内部构造、外部特征、各层圈之间的相互作用和演变历史的知识体系。是研究地球及其演变的一门自然科学。" + "地球自形成以来，经历了约46亿年的演化过程，进行过错综复杂的物理、化学变化，同时还受天文变化的影响，所以各个层圈均在不断演变,约在35亿年前，地球上出现了生命现象，于是生物成为一种地质营力。最晚在距今200～300万年前，开始有人类出现。人类为了生存和发展，一直在努力适应和改变周围的环境。利用坚硬岩石作为用具和工具，从矿石中提取铜、铁等金属，对人类社会的历史产生过划时代的影响。";
        for (int i = 0, size = s.length(); i < size; i++) {
            indexList.add(new LetterIndex(s.substring(i, i + 1)));
        }
        letterIndexBar.setNeedRealIndex(true);
        letterIndexBar.setSourceData(indexList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        letterIndexBar.setupWithTextViewAndLinearLayoutManager(textView, layoutManager,0);
        recyclerView.setLayoutManager(layoutManager);
        LetterDataAdapter adapter = new LetterDataAdapter(R.layout.item_layout_letter_data_list,indexList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
                letterIndexBar.setCurrentLetterTag(indexList.get(firstItemPosition).getSuspensionTag());
            }
        });
    }

    public static class LetterDataAdapter extends BaseQuickAdapter<LetterIndex, BaseViewHolder> {
        public LetterDataAdapter(int layoutResId, @Nullable List<LetterIndex> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, LetterIndex item) {
            helper.setText(R.id.tv_name, item.getName());
        }
    }

    public static class LetterIndex extends LetterIndexAbstract {

        private String name;
        private String mSuspensionTag;
        private String mIndexPinyin;

        public LetterIndex(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getSuspensionTag() {
            if (TextUtils.isEmpty(mSuspensionTag)) {
                mSuspensionTag = getIndexPinyin().substring(0, 1);
                if (!mSuspensionTag.matches("[A-Z]")) {
                    mSuspensionTag = "#";
                }
            }
            return mSuspensionTag;
        }

        @Override
        public String getIndexPinyin() {
            if (TextUtils.isEmpty(mIndexPinyin)) {
                StringBuilder pySb = new StringBuilder();
                for (int i = 0; i < name.length(); i++) {
                    pySb.append(Pinyin.toPinyin(name.charAt(i)).toUpperCase());
                }
                mIndexPinyin = pySb.toString();
            }
            return mIndexPinyin;
        }
    }
}
