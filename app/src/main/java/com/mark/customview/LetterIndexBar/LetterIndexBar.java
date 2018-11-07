package com.mark.customview.LetterIndexBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.mark.customview.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     author : Mark
 *     e-mail : makun.cai@aorise.org
 *     time   : 2018/11/05
 *     desc   : 字母索引条，支持绑定ListView和RecycleView(绑定时传入LayoutManager)
 *     version: 1.0
 * </pre>
 */
public class LetterIndexBar extends View {
    private static final String TAG = LetterIndexBar.class.getSimpleName();
    private Paint mNormalPaint;
    private Paint mSelectlPaint;
    private int mLetterTextSize;
    private int mNormalLetterTextColor;
    private int mSelectLetterTextColor;
    private boolean measured;
    private int mWidth;
    private int mHeight;
    private int mGapHeight;
    private List<String> mIndexDatas;
    private List<? extends LetterIndexAbstract> mSourceData;
    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private boolean isNeedRealIndex = false;
    private int mCurrentIndex = -1;

    private TextView mPressedShowTextView;
    private LinearLayoutManager mLayoutManager;
    private OnSelectListener mOnSelectListener;
    private ListView mListView;
    private int mHeaderViewCount;

    public LetterIndexBar(Context context) {
        this(context, null);
    }

    public LetterIndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterIndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LetterIndexBar);
        mNormalLetterTextColor = array.getColor(R.styleable.LetterIndexBar_normalLetterTextColor, Color.GRAY);
        mSelectLetterTextColor = array.getColor(R.styleable.LetterIndexBar_selectLetterTextColor, Color.BLUE);
        mLetterTextSize = array.getDimensionPixelSize(R.styleable.LetterIndexBar_letterTextSize, sp2px(context, 12));
        array.recycle();
        init();
    }

    private void init() {
        mIndexDatas = Arrays.asList(INDEX_STRING);
        mNormalPaint = new Paint();
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setTextSize(mLetterTextSize);
        mNormalPaint.setColor(mNormalLetterTextColor);
        mSelectlPaint = new Paint();
        mSelectlPaint.setAntiAlias(true);
        mSelectlPaint.setTextSize((float) (mLetterTextSize * 1.2));
        mSelectlPaint.setColor(mSelectLetterTextColor);
    }

    public void setupWithTextViewAndLinearLayoutManager(TextView pressedShowTextView, LinearLayoutManager layoutManager, int headerViewCount) {
        mPressedShowTextView = pressedShowTextView;
        mLayoutManager = layoutManager;
        mHeaderViewCount = headerViewCount;
    }

    public void setupWithTextViewAndListView(TextView pressedShowTextView, ListView listView) {
        mPressedShowTextView = pressedShowTextView;
        mListView = listView;
        if (mListView != null) {
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    setCurrentLetterTag(mSourceData.get(firstVisibleItem).getSuspensionTag());
                }
            });
        }
    }

    public void setSourceData(List<? extends LetterIndexAbstract> sourceData) {
        if (sourceData == null || sourceData.size() == 0) {
            throw new RuntimeException("sourceData can't null or size() must >0");
        }
        if (mSourceData == sourceData) {
            return;
        }
        Collections.sort(sourceData);
        mSourceData = sourceData;
        if (isNeedRealIndex) {
            mIndexDatas = new ArrayList<>();
            for (LetterIndexAbstract pinyinInterface : mSourceData) {
                String baseIndexTag = pinyinInterface.getSuspensionTag();
                if (!mIndexDatas.contains(baseIndexTag)) {
                    mIndexDatas.add(baseIndexTag);
                }
            }
            mCurrentIndex = 0;
        } else {
            mCurrentIndex = mIndexDatas.indexOf(mSourceData.get(0).getSuspensionTag());
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //取出宽高的MeasureSpec  Mode 和Size
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 防止输入法顶上去之后 indexbar的高度也发生了变化
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.e(TAG, "onMeasure: "+mHeight );

        int measureWidth = 0, measureHeight = 0;//最终测量出来的宽高
        //得到合适宽度：
        Rect indexBounds = new Rect();//存放每个绘制的index的Rect区域
        String index = "A";//每个要绘制的index内容
        mNormalPaint.setTextSize(mLetterTextSize);
        mNormalPaint.getTextBounds(index, 0, index.length(), indexBounds);//测量计算文字所在矩形，可以得到宽高
        measureWidth = Math.max(indexBounds.width(), measureWidth);//循环结束后，得到index的最大宽度
        measureHeight = Math.max(indexBounds.height(), measureHeight);//循环结束后，得到index的最大高度，然后*size
        measureHeight *= mIndexDatas.size();
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                mHeight = Math.min(measureWidth, mHeight);//wSize此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        //得到合适的高度：
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                mHeight = Math.min(measureHeight, mHeight);//mHeight此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mGapHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / mIndexDatas.size();
        int t = getPaddingTop() + (mHeight - mIndexDatas.size() * mGapHeight) / 2;//top的基准点(支持padding)
        int l = getPaddingLeft();
        String index;//每个要绘制的index内容
        Paint paint;
        for (int i = 0; i < mIndexDatas.size(); i++) {
            index = mIndexDatas.get(i);
            if (mCurrentIndex == i) {
                paint = mSelectlPaint;
            } else {
                paint = mNormalPaint;
            }
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();//获得画笔的FontMetrics，用来计算baseLine。因为drawText的y坐标，代表的是绘制的文字的baseLine的位置
            int baseline = (int) ((mGapHeight - fontMetrics.bottom - fontMetrics.top) / 2);//计算出在每格index区域，竖直居中的baseLine值
            canvas.drawText(index, l + mWidth / 2 - mNormalPaint.measureText(index) / 2, (float) (t + mGapHeight * i + baseline), paint);//调用drawText，居中显示绘制index
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                setBackgroundColor(mPressedBackground);//手指按下时背景变色
                //注意这里没有break，因为down时，也要计算落点 回调监听器
                if (null != mOnSelectListener) {
                    mOnSelectListener.onSelectBefore(mCurrentIndex, mIndexDatas.get(mCurrentIndex));
                }
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                //通过计算判断落点在哪个区域：
                int pressI = (int) ((y - (int) (getPaddingTop() + (mHeight - (mIndexDatas.size() + 0.5) * mGapHeight) / 2)) / mGapHeight);
                //边界处理（在手指move时，有可能已经移出边界，防止越界）
                if (pressI < 0) {
                    pressI = 0;
                } else if (pressI >= mIndexDatas.size()) {
                    pressI = mIndexDatas.size() - 1;
                }
                if (mCurrentIndex == pressI) {
                    return true;
                }
                //回调监听器
                String tag = mIndexDatas.get(pressI);
                int sourcePosition = containsTag(tag);
                if (isNeedRealIndex || sourcePosition >= 0) {
                    if (null != mOnSelectListener) {
                        mOnSelectListener.onSelected(pressI, mIndexDatas.get(pressI));
                    }
                    if (mPressedShowTextView != null) {
                        mPressedShowTextView.setVisibility(VISIBLE);
                        mPressedShowTextView.setText(tag);
                    }
                    if (mLayoutManager != null) {
                        mLayoutManager.scrollToPositionWithOffset(sourcePosition + mHeaderViewCount, 0);
                    }
                    if (mListView != null) {
                        mListView.setSelection(sourcePosition);
                    }
                    mCurrentIndex = pressI;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
//                setBackgroundResource(android.R.color.transparent);//手指抬起时背景恢复透明
//                //回调监听器
                if (null != mOnSelectListener) {
                    mOnSelectListener.onSelectAfter(mCurrentIndex, mIndexDatas.get(mCurrentIndex));
                }
                if (mPressedShowTextView != null) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPressedShowTextView.setVisibility(GONE);
                        }
                    }, 1000);
                }
                break;
        }
        return true;
    }

    /**
     * @param tag
     * @return
     */
    private int containsTag(String tag) {
        if (null == mSourceData || mSourceData.isEmpty()) {
            return -1;
        }
        if (TextUtils.isEmpty(tag)) {
            return -1;
        }
        for (int i = 0, size = mSourceData.size(); i < size; i++) {
            if (tag.equals(mSourceData.get(i).getSuspensionTag())) {
                return i;
            }
        }
        return -1;
    }

    private static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }

    /**
     * 当前被按下的index的监听器
     */
    public interface OnSelectListener {
        void onSelectBefore(int sourceP, CharSequence letterText);

        void onSelected(int index, CharSequence letterText);

        void onSelectAfter(int index, CharSequence letterText);
    }

    public int getLetterTextSize() {
        return mLetterTextSize;
    }

    public void setLetterTextSize(int letterTextSize) {
        if (mLetterTextSize == letterTextSize) {
            return;
        }
        mLetterTextSize = letterTextSize;
        invalidate();
    }

    public int getNormalLetterTextColor() {
        return mNormalLetterTextColor;
    }

    public void setNormalLetterTextColor(int normalLetterTextColor) {
        if (mNormalLetterTextColor == normalLetterTextColor) {
            return;
        }
        mNormalLetterTextColor = normalLetterTextColor;
        invalidate();
    }

    public int getSelectLetterTextColor() {
        return mSelectLetterTextColor;
    }

    public void setSelectLetterTextColor(int selectLetterTextColor) {
        if (mSelectLetterTextColor == selectLetterTextColor) {
            return;
        }
        mSelectLetterTextColor = selectLetterTextColor;
        invalidate();
    }

    public boolean isNeedRealIndex() {
        return isNeedRealIndex;
    }

    public void setNeedRealIndex(boolean needRealIndex) {
        if (isNeedRealIndex == needRealIndex) {
            return;
        }
        isNeedRealIndex = needRealIndex;
        invalidate();
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public void setCurrentLetterTag(String tag) {
        if (!mIndexDatas.contains(tag)) {
            return;
        }
        if (mIndexDatas.get(mCurrentIndex).equals(tag)) {
            return;
        }
        setPressedShow(tag);
        mCurrentIndex = mIndexDatas.indexOf(tag);
        invalidate();
    }

    public void setCurrentIndex(int currentIndex) {
        if (currentIndex >= mIndexDatas.size() || currentIndex < 0) {
            throw new IndexOutOfBoundsException("currentIndex < 0 or >= size(), IndexOutOfBoundsException ");
        }
        if (mCurrentIndex == currentIndex) {
            return;
        }
        setPressedShow(mIndexDatas.get(currentIndex));
        mCurrentIndex = currentIndex;
        invalidate();
    }

    private void setPressedShow(String TAG) {
        if (mPressedShowTextView == null) {
            return;
        }
        mPressedShowTextView.setVisibility(VISIBLE);
        mPressedShowTextView.setText(TAG);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mPressedShowTextView.setVisibility(GONE);
            }
        }, 1000);
    }

    public OnSelectListener getOnSelectListener() {
        return mOnSelectListener;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }
}
