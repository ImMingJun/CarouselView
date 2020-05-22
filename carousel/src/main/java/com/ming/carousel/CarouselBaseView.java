package com.ming.carousel;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * created by cmj on 2020/3/27
 * for:
 */
public abstract class CarouselBaseView<E> extends FrameLayout {
    //constants
    protected static int WHAT_AUTO_PLAY = 666;

    protected long mPlayDuration = -1;
    protected Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                smoothScrollToNextPage();
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mPlayDuration);
            }
            return false;
        }
    });


    protected Context mContext;
    protected List<E> mData;

    public CarouselBaseView(@NonNull Context context) {
        this(context, null);
    }

    public CarouselBaseView(@NonNull Context context, @Nullable  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselBaseView(@NonNull  Context context, @Nullable  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    protected abstract int getContentLayoutId();

    /**
     * @return 获取数据
     */
    public List<E> getData() {
        return mData;
    }

    protected void onPageSelected(int position) {

    }

    protected void onPageScrollStateChanged(int state) {

    }

    public abstract void smoothScrollToNextPage();

    /**
     * 设置轮播间隔
     *
     * @param playDuration 间隔时间
     */
    public void setPlayDuration(@IntRange(from = 0) int playDuration) {
        this.mPlayDuration = playDuration;
    }

    /**
     * 距离换算
     *
     * @param dpValue dp
     * @return px
     */
    protected float dpToPx(float dpValue) {
        float scale;
        if (null == getContext()) {
            scale = Resources.getSystem().getDisplayMetrics().density;
        } else {
            scale = getContext().getResources().getDisplayMetrics().density;
        }
        return (dpValue * scale + 0.5f);
    }

    protected int optInt(TypedArray typedArray,
                         int index,
                         int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getInt(index, def);
    }

    protected float optFloat(TypedArray typedArray,
                             int index,
                             float def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getFloat(index, def);
    }

    protected float optDimension(TypedArray typedArray,
                                 int index,
                                 float def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getDimension(index, def);
    }

    protected int optDimension(TypedArray typedArray,
                               int index,
                               int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getDimensionPixelSize(index, def);
    }

    protected boolean optBoolean(TypedArray typedArray,
                                 int index,
                                 boolean def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getBoolean(index, def);
    }

    protected int optResourceId(TypedArray typedArray,
                                int index,
                                int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getResourceId(index, def);
    }

    protected int optColor(TypedArray typedArray,
                           int index,
                           int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getColor(index, def);
    }
}
