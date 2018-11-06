package com.mark.customview.LetterIndexBar;


import android.support.annotation.NonNull;

public abstract class LetterIndexAbstract implements Comparable<LetterIndexAbstract> {

    //首字母
    public abstract String getSuspensionTag();

    //拼音
    public abstract String getIndexPinyin();

    @Override
    public int compareTo(@NonNull LetterIndexAbstract letterIndexAbstract) {
        if (getIndexPinyin()==null){
            return -1;
        }
        if (getSuspensionTag().equals("#") && letterIndexAbstract.getSuspensionTag().equals("#")){
            return 0;
        }
        if (getSuspensionTag().equals("#")){
            return 1;
        }
        if (letterIndexAbstract.getSuspensionTag().equals("#")){
            return -1;
        }
        return getIndexPinyin().compareTo(letterIndexAbstract.getIndexPinyin());
    }
}
