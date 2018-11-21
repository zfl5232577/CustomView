package com.mark.customview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SourceLinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mark.customview.RecyclerView.GalleryLayoutManager;
import com.mark.customview.RecyclerView.RecyclerViewHorizontalDivider;
import com.mark.customview.RecyclerView.RecyclerViewVerticalDivider;

import java.util.ArrayList;
import java.util.List;

public class SourceLinearLayoutManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemsbannermenu);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final List<String> data = new ArrayList<>();
        data.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3249675290,838025564&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=403466667,3234263217&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4205053055,3935487921&fm=11&gp=0.jpg");
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542475239141&di=4bc26578c89f4b5ad29805a6ec517573&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fbaike%2Fpic%2Fitem%2Fb64543a98226cffcca2cab21b2014a90f603ea1b.jpg");
        data.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3163471846,3867214877&fm=26&gp=0.jpg");
        data.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1188898371,3144820224&fm=26&gp=0.jpg");
        data.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=864899323,2101003777&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3249675290,838025564&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=403466667,3234263217&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4205053055,3935487921&fm=11&gp=0.jpg");
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542475239141&di=4bc26578c89f4b5ad29805a6ec517573&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fbaike%2Fpic%2Fitem%2Fb64543a98226cffcca2cab21b2014a90f603ea1b.jpg");
        data.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3163471846,3867214877&fm=26&gp=0.jpg");
        data.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1188898371,3144820224&fm=26&gp=0.jpg");
        data.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=864899323,2101003777&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3249675290,838025564&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=403466667,3234263217&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4205053055,3935487921&fm=11&gp=0.jpg");
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542475239141&di=4bc26578c89f4b5ad29805a6ec517573&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fbaike%2Fpic%2Fitem%2Fb64543a98226cffcca2cab21b2014a90f603ea1b.jpg");
        data.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3163471846,3867214877&fm=26&gp=0.jpg");
        data.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1188898371,3144820224&fm=26&gp=0.jpg");
        data.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=864899323,2101003777&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3249675290,838025564&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=403466667,3234263217&fm=26&gp=0.jpg");
        data.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4205053055,3935487921&fm=11&gp=0.jpg");
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542475239141&di=4bc26578c89f4b5ad29805a6ec517573&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fbaike%2Fpic%2Fitem%2Fb64543a98226cffcca2cab21b2014a90f603ea1b.jpg");
        data.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3163471846,3867214877&fm=26&gp=0.jpg");
        data.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1188898371,3144820224&fm=26&gp=0.jpg");
        data.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=864899323,2101003777&fm=26&gp=0.jpg");
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new RecyclerViewVerticalDivider(this,45, Color.WHITE));
        layoutManager.setOrientation(SourceLinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        CardAdapter adapter = new CardAdapter(R.layout.item_layout_bannerlayoutmanager, data);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                recyclerView.smoothScrollToPosition(position);
//                Toast.makeText(BannerLayoutManagerActivity.this, "item被点击" + position, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(1);
    }

    public static class CardAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        public CardAdapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
//            setHasStableIds(true);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            Log.e(TAG, "onBindViewHolder: =================" );
            super.onBindViewHolder(holder, position);
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.e(TAG, "onCreateViewHolder: =================" );
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            Glide.with(mContext).load(item).into((ImageView) helper.getView(R.id.iv_image));
        }

//        @Override
//        public long getItemId(int position) {
//            return getItemViewType(position);
//        }
    }
}
