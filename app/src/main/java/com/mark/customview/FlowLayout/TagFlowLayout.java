package com.mark.customview.FlowLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/07
 *     desc   : TODO
 *     version: 1.0
 * </pre>
 */
public class TagFlowLayout extends FlowLayout {

    private TagAdapter mAdapter;
    private OnTagClickListener mOnTagClickListener;
    private TagAdapter.OnDataChangedListener mOnDataChangedListener = new TagAdapter.OnDataChangedListener() {
        @Override
        public void onChanged() {
            changeAdapter();
        }
    };

    public TagFlowLayout(Context context) {
        this(context, null);
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TagAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(TagAdapter adapter) {
        if (adapter==null){
            throw new NullPointerException("TagFlowLayout setAdapter(TagAdapter adapter) adapter is null");
        }
        mAdapter = adapter;
        mAdapter.setOnDataChangedListener(mOnDataChangedListener);
        changeAdapter();
    }

    private void changeAdapter() {
        removeAllViews();
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            final int position = i;
            View view = mAdapter.getView(position,this,mAdapter.getItem(position));
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnTagClickListener!=null){
                        mOnTagClickListener.onClick(v,position,TagFlowLayout.this);
                    }
                }
            });
            addView(view);
        }
    }

    public OnTagClickListener getOnTagClickListener() {
        return mOnTagClickListener;
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        mOnTagClickListener = onTagClickListener;
    }

    public interface OnTagClickListener {
        void onClick(View v,int position,FlowLayout parent);
    }

    public abstract static class TagAdapter<T> {

        private List<T> mTagDatas;
        private OnDataChangedListener mOnDataChangedListener;

        public TagAdapter(List<T> tagDatas) {
            mTagDatas = tagDatas;
        }

        public int getCount() {
            return mTagDatas == null ? 0 : mTagDatas.size();
        }

        public abstract View getView(int position, ViewGroup parent,T t);

        public T getItem(int position) {
            return mTagDatas.get(position);
        }

        public void notifyDataChanged() {
            if (mOnDataChangedListener != null)
                mOnDataChangedListener.onChanged();
        }

        void setOnDataChangedListener(OnDataChangedListener listener) {
            mOnDataChangedListener = listener;
        }

        interface OnDataChangedListener {
            void onChanged();
        }
    }
}
