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
                    "字母选择条", "LetterIndexBarActivity"),
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
