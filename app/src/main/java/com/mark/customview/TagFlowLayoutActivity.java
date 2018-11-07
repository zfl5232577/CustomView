package com.mark.customview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mark.customview.FlowLayout.FlowLayout;
import com.mark.customview.FlowLayout.TagFlowLayout;

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
public class TagFlowLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagflowlayout);
        TagFlowLayout tagFlowLayout = findViewById(R.id.tagFlowLayout);
        List<Tag> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            int random = (int) (Math.random()*6+1);
            String name="";
            for (int j = 0; j < random; j++) {
                name += "污";
            }
            Tag tag = new Tag(name,i);
            list.add(tag);
        }
        TestAdapter adapter = new TestAdapter(list);
        tagFlowLayout.setAdapter(adapter);
        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public void onClick(View v, int position, FlowLayout parent) {
                Toast.makeText(TagFlowLayoutActivity.this,position+"被点击了",Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class Tag{
        private String tagName;
        private int clickCount;

        public Tag(String tagName, int clickCount) {
            this.tagName = tagName;
            this.clickCount = clickCount;
        }
    }

    public static class TestAdapter extends TagFlowLayout.TagAdapter<Tag>{

        public TestAdapter(List<Tag> tagDatas) {
            super(tagDatas);
        }

        @Override
        public View getView(int position, ViewGroup parent ,Tag tag) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_tagflowlayout,parent,false);
            TextView tvName = view.findViewById(R.id.tv_name);
            TextView tvCount = view.findViewById(R.id.tv_count);
            tvName.setText(tag.tagName);
            tvCount.setText(tag.clickCount+"");
            return view;
        }
    }
}
