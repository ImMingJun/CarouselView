package com.ming.carousel;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * created by cmj on 2020/3/27
 * for:CarouselView基类
 *
 * @author ming
 */
public abstract class BaseCarouselView<E> extends FrameLayout {
    protected static int WHAT_AUTO_PLAY = 666;

    protected boolean mAutoPlay = false;
    protected long mPlayDuration = -1;
    protected Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                smoothScrollToNextPage();
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mPlayDuration);
            }
            return false;
        }
    });

    /**
     * 视图相关
     */
    protected RecyclerView mRvIndicators;
    protected DividerItemDecoration mIndicatorDivider;
    protected GradientDrawable mIndicatorDividerDrawable;

    protected Context mContext;
    /**
     * 配置相关
     */
    protected List<E> mData;
    protected int mUnSelectIndicatorRes, mSelectedIndicatorRes;
    protected int mUnSelectIndicatorColor, mSelectedIndicatorColor;
    protected boolean isInfinite;

    /**
     * 状态相关
     */
    protected boolean mIsPlaying = false;
    protected boolean mCanPlay = false;
    protected OnPageChangeListener mOnPageChangeListener;

    public BaseCarouselView(@NonNull Context context) {
        this(context, null);
    }

    public BaseCarouselView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCarouselView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    /**
     * 获取布局文件id
     *
     * @return int
     */
    protected abstract int getContentLayoutId();

    /**
     * 滑动切换到下一页
     */
    public abstract void smoothScrollToNextPage();

    /**
     * @return 获取数据
     */
    public List<E> getData() {
        return mData;
    }

    /**
     * 页面切换监听
     *
     * @param position 真是位置
     */
    protected void onPageSelected(int position) {
        if (null != mOnPageChangeListener) {
            mOnPageChangeListener.onPageSelected(position);
        }
        //刷新指示器
        refreshIndicators(position);
    }

    /**
     * 页面切换滑动状态监听
     *
     * @param state 状态
     */
    protected void onPageScrollStateChanged(int state) {
        if (null != mOnPageChangeListener) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    /**
     * @return 是否无限轮播
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    /**
     * 设置无限轮播
     *
     * @param isInfinite 是否支持无限轮播
     */
    public void setInfinite(boolean isInfinite) {
        if (this.isInfinite != isInfinite) {
            this.isInfinite = isInfinite;
            refreshInfinite();
        }
    }

    /**
     * 刷新无限循环状态
     */
    protected void refreshInfinite() {

    }

    /**
     * 设置中间和两侧的大小比例
     *
     * @param scale 比例
     */
    public void setScale(@FloatRange(from = 0.0f, to = 1.0) float scale) {
        if (floatEnable(scale)) {
            refreshScale(scale);
        }
    }

    private boolean floatEnable(float scale) {
        if (scale < 0 || scale > 1) {
            return false;
        }
        if (Float.isNaN(scale)) {
            return false;
        }
        return true;
    }

    /**
     * 刷新两侧大小比例
     *
     * @param scale 比例
     */
    protected abstract void refreshScale(float scale);

    /**
     * 设置两侧的透明度
     *
     * @param alpha 透明度
     */
    public void setAlphaSide(@FloatRange(from = 0.0f, to = 1.0) float alpha) {
        if (floatEnable(alpha)) {
            refreshAlphaSide(alpha);
        }
    }

    /**
     * 设置两侧的透明度
     *
     * @param alpha 透明度
     */
    protected abstract void refreshAlphaSide(float alpha);

    /**
     * 设置item之间的间距
     */
    public void setItemInterval(float dpInterval) {

    }

    /**
     * 设置方向
     *
     * @param orientation 方向
     */
    public void setOrientation(@RecyclerView.Orientation int orientation) {

    }

    /**
     * 设置Recyclerview的over-scroll mode
     * {@link RecyclerView#setOverScrollMode}
     * {@link ViewPager2#setOverScrollMode}
     * {@link androidx.viewpager.widget.ViewPager#setOverScrollMode}
     * {@link VerticalViewPager#setOverScrollMode}
     */
    public void setRecyclerOverScrollMode(int overScrollMode) {

    }

    /**
     * 设置轮播图是否支持手势操作
     *
     * @param enable 默认支持
     */
    public void setUserInputEnable(boolean enable) {

    }

    /**
     * 设置轮播间隔
     *
     * @param playDuration 间隔时间
     */
    public void setPlayDuration(@IntRange(from = 0) int playDuration) {
        this.mPlayDuration = playDuration;
    }

    /**
     * 刷新指示器
     *
     * @param position 选中位置
     */
    private void refreshIndicators(int position) {
        if (null != mRvIndicators &&
                mRvIndicators.getVisibility() == VISIBLE &&
                null != mRvIndicators.getAdapter()) {
            ((IndicatorsAdapter) mRvIndicators.getAdapter()).setSelectedPosition(position);
        }
    }

    /**
     * 设置指示器显示与隐藏
     *
     * @param visibility 显示与隐藏
     */
    public void setIndicatorsVisibility(int visibility) {
        if (null != mRvIndicators &&
                visibility != getIndicatorsVisibility()) {
            mRvIndicators.setVisibility(visibility);
        }
    }

    /**
     * @return 指示器显示隐藏状态
     */
    public int getIndicatorsVisibility() {
        return null == mRvIndicators ? GONE : mRvIndicators.getVisibility();
    }

    /**
     * 设置指示器图片资源
     *
     * @param unSelectIndicatorRes 未选中的指示器资源
     * @param selectedIndicatorRes 已选中的指示器资源
     */
    public void setIndicators(int unSelectIndicatorRes, int selectedIndicatorRes) {
        this.mUnSelectIndicatorRes = unSelectIndicatorRes;
        this.mSelectedIndicatorRes = selectedIndicatorRes;
        if (null != mRvIndicators.getAdapter() && mRvIndicators.getAdapter() instanceof IndicatorsAdapter) {
            ((IndicatorsAdapter) mRvIndicators.getAdapter()).setIndicatorsRes(unSelectIndicatorRes, selectedIndicatorRes);
        }
    }


    /**
     * 设置指示器颜色资源
     *
     * @param unSelectIndicatorColor 未选中的指示器颜色
     * @param selectedIndicatorColor 已选中的指示器颜色
     */
    public void setIndicatorsColor(int unSelectIndicatorColor, int selectedIndicatorColor) {
        this.mUnSelectIndicatorColor = unSelectIndicatorColor;
        this.mSelectedIndicatorColor = selectedIndicatorColor;
        if (null != mRvIndicators.getAdapter() && mRvIndicators.getAdapter() instanceof IndicatorsAdapter) {
            ((IndicatorsAdapter) mRvIndicators.getAdapter()).setIndicatorsColor(unSelectIndicatorColor, selectedIndicatorColor);
        }
    }

    /**
     * 设置指示器分割线宽度
     *
     * @param space 宽度
     */
    public void setIndicatorsDividerSpace(@IntRange(from = 0) int space) {
        mIndicatorDividerDrawable.setSize(space, MATCH_PARENT);
        if (mRvIndicators.getItemDecorationCount() > 0) {
            mRvIndicators.removeItemDecoration(mIndicatorDivider);
        }
        //重刷分割线,直接调用requestLayout不能马上生效
        mRvIndicators.addItemDecoration(mIndicatorDivider);
    }

    /**
     * 设置指示器位置
     *
     * @param gravity 相对位置
     * @see android.view.Gravity
     */
    public void setIndicatorsGravity(int gravity) {
        LayoutParams layoutParams = (LayoutParams) mRvIndicators.getLayoutParams();
        layoutParams.gravity = gravity;
        mRvIndicators.setLayoutParams(layoutParams);
    }

    /**
     * 设置指示器底部间距
     *
     * @param dpBottomMargin dp值
     */
    public void setIndicatorsBottomMargin(int dpBottomMargin) {
        LayoutParams layoutParams = (LayoutParams) mRvIndicators.getLayoutParams();
        layoutParams.bottomMargin = (int) dpToPx(dpBottomMargin);
        mRvIndicators.setLayoutParams(layoutParams);
    }

    /**
     * 设置指示器顶部间距
     *
     * @param dpTopMargin dp值
     */
    public void setIndicatorsTopMargin(int dpTopMargin) {
        LayoutParams layoutParams = (LayoutParams) mRvIndicators.getLayoutParams();
        layoutParams.topMargin = (int) dpToPx(dpTopMargin);
        mRvIndicators.setLayoutParams(layoutParams);
    }

    /**
     * 设置指示器左侧间距
     *
     * @param dpLeftMargin dp值
     */
    public void setIndicatorsLeftMargin(int dpLeftMargin) {
        LayoutParams layoutParams = (LayoutParams) mRvIndicators.getLayoutParams();
        layoutParams.leftMargin = (int) dpToPx(dpLeftMargin);
        mRvIndicators.setLayoutParams(layoutParams);
    }

    /**
     * 设置指示器右侧间距
     *
     * @param dpRightMargin dp值
     */
    public void setIndicatorsRightMargin(int dpRightMargin) {
        LayoutParams layoutParams = (LayoutParams) mRvIndicators.getLayoutParams();
        layoutParams.rightMargin = (int) dpToPx(dpRightMargin);
        mRvIndicators.setLayoutParams(layoutParams);
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
