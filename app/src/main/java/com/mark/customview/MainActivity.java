package com.mark.customview;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView mListView = (ListView) findViewById(R.id.listView);
        // 添加ListItem，设置事件响应
        mListView.setAdapter(new DemoListAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View v, int index,
                                    long arg3) {
                onListItemClick(index);
            }
        });
    }

    private void onListItemClick(int index) {
        Intent intent = new Intent();
        intent.setClassName(this,getPackageName()+"."+DEMOS[index].activityName);
        startActivity(intent);
    }

    private static final DemoInfo[] DEMOS = {
            new DemoInfo("QQStepView",
                    "仿写QQ计步器自定义控件", "QQStepViewActivity"),
            new DemoInfo("MultiShapeProgressView",
                    "仿写58同城加载进度图形", "MultiShapeProgressActivity"),
            new DemoInfo("LetterIndexBar",
                    "字母索引条", "LetterIndexBarActivity"),
            new DemoInfo("FlowLayout",
                    "仿写热门标签流式布局", "FlowLayoutActivity"),
            new DemoInfo("TagFlowLayout",
                    "仿写热门标签流式布局,使用Adapter设计模式", "TagFlowLayoutActivity"),
            new DemoInfo("SlidingMenuLayout",
                    "侧滑菜单，同时可以实现抽屉效果", "SlidingMenuActivity"),
            new DemoInfo("qqSlidingMenuLayout",
                    "QQ6.0侧滑菜单效果", "QQSlidingMenuActivity"),
            new DemoInfo("DragListView",
                    "拖拽折叠列表", "DragListViewActivity"),

            new DemoInfo("TestDragView",
                    "拖拽控件", "TestDragViewActivity"),
            new DemoInfo("SquaredLatticeLockView",
                    "九宫格解锁控件，同时支持更多格子", "SquaredLatticeLockActivity"),
            new DemoInfo("MaterialDesign_NavigationView",
                    "MaterialDesign控件练习", "MaterialDesign.TestMDNavigationViewActivity"),
            new DemoInfo("MaterialDesign_CollapsingToolbarLayout",
                    "MaterialDesign控件练习", "MaterialDesign.TestCollapsingToolbarLayoutActivity"),
            new DemoInfo("CustomBehavior",
                    "MaterialDesign控件练习", "MaterialDesign.CustomBehaviorActivity"),
            new DemoInfo("ItemSwipeMenuLayout",
                    "列表Item侧滑菜单", "ItemSwipeMenuLayoutActivity"),
            new DemoInfo("RecyclerViewDivider",
                    "RecyclerViewDivider,RecyclerView分割线，自定义分割线", "RecyclerViewDividerActivity"),
            new DemoInfo("CustomLayoutManager",
                    "自定义LayoutManager", "CustomLayoutManagerActivity"),
            new DemoInfo("CardLayoutManager",
                    "自定义LayoutManager,卡片效果", "CardLayoutManagerActivity"),
            new DemoInfo("Test",
                    "拖拽控件", "DragListViewActivity"),

    };

    private class DemoListAdapter extends BaseAdapter {
        public DemoListAdapter() {
            super();
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            convertView = View.inflate(MainActivity.this,
                    R.layout.demo_info_item, null);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            title.setText(DEMOS[index].title);
            desc.setText(DEMOS[index].desc);
            if (index >= 25) {
                title.setTextColor(Color.YELLOW);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return DEMOS.length;
        }

        @Override
        public Object getItem(int index) {
            return DEMOS[index];
        }

        @Override
        public long getItemId(int id) {
            return id;
        }
    }

    private static class DemoInfo {
        private String title;
        private String desc;
        private String activityName;

        public DemoInfo(String title, String desc, String activityName) {
            this.title = title;
            this.desc = desc;
            this.activityName = activityName;
        }
    }
}
