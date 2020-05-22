package com.ming.carousel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * created by cmj on 2020/3/27
 * for:
 */
public class CarouselView2<E> extends CarouselBaseView<E> {

    protected ViewPager2 mViewPager2;
    protected ViewPager mViewPager;
    protected GradientDrawable mCarouselDividerDrawable;
    protected RecyclerView mRvIndicators;
    protected DividerItemDecoration mIndicatorDivider;
    protected GradientDrawable mIndicatorDividerDrawable;

    protected int mUnSelectIndicatorRes, mSelectedIndicatorRes;
    protected int mUnSelectIndicatorColor, mSelectedIndicatorColor;
    protected boolean mAutoPlay = false;
    protected boolean mIsPlaying = false;
    protected boolean mCanPlay = false;
    protected boolean useViewPager2;
    protected boolean isInfinite;

    protected OnPageChangeListener mOnPageChangeListener;

    public CarouselView2(@NonNull Context context) {
        this(context, null);
    }

    public CarouselView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        TypedArray ta = null;
        if (null != attrs) {
            ta = mContext.obtainStyledAttributes(attrs, R.styleable.CarouselView2);
        }
        int carouselOrientation = optInt(ta, R.styleable.CarouselView2_mj_carousel_orientation, RecyclerView.HORIZONTAL);
        float carouselScale = optFloat(ta, R.styleable.CarouselView2_mj_carousel_scale, 0.8f);
        float carouselAlphaSide = optFloat(ta, R.styleable.CarouselView2_mj_carousel_alpha_side, 1f);
        float carouselItemInterval = optDimension(ta, R.styleable.CarouselView2_mj_carousel_item_interval, 0f);
        int carouselMarginToParent = optDimension(ta, R.styleable.CarouselView2_mj_carousel_margin_to_parent, 0);
        isInfinite = optBoolean(ta, R.styleable.CarouselView2_mj_carousel_infinite, true);
        useViewPager2 = optBoolean(ta, R.styleable.CarouselView2_mj_carousel_userViewPager2, true);

        int indicatorVisibility = optInt(ta, R.styleable.CarouselView2_mj_indicator_visibility, View.GONE);
        int indicatorResUnSelect = optResourceId(ta, R.styleable.CarouselView2_mj_indicator_src_unSelect, R.drawable.carousel_ic_indicator_unselect);
        int indicatorResSelected = optResourceId(ta, R.styleable.CarouselView2_mj_indicator_src_selected, R.drawable.carousel_ic_indicator_selected);
        int indicatorColorUnSelect = optColor(ta, R.styleable.CarouselView2_mj_indicator_color_unSelect, 0);
        int indicatorColorSelected = optColor(ta, R.styleable.CarouselView2_mj_indicator_color_selected, 0);
        int indicatorDividerSpace = optDimension(ta, R.styleable.CarouselView2_mj_indicator_divider_space, 0);
        int indicatorGravity = optInt(ta, R.styleable.CarouselView2_mj_indicator_gravity, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        int indicatorMarginLeft = optDimension(ta, R.styleable.CarouselView2_mj_indicator_margin_left, 0);
        int indicatorMarginTop = optDimension(ta, R.styleable.CarouselView2_mj_indicator_margin_top, 0);
        int indicatorMarginRight = optDimension(ta, R.styleable.CarouselView2_mj_indicator_margin_right, 0);
        int indicatorMarginBottom = optDimension(ta, R.styleable.CarouselView2_mj_indicator_margin_bottom, 0);

        mPlayDuration = optInt(ta, R.styleable.CarouselView2_mj_play_duration, -1);
        mAutoPlay = optBoolean(ta, R.styleable.CarouselView2_mj_play_auto, false);
        if (ta != null) {
            ta.recycle();
        }

        View container = inflate(mContext, getContentLayoutId(), this);
        mViewPager = container.findViewById(R.id.viewPager_carousel);
        mViewPager2 = container.findViewById(R.id.viewPager2_carousel);
        mRvIndicators = container.findViewById(R.id.recyclerView_indicators);

        if (useViewPager2) {
            mViewPager.setVisibility(GONE);
            mViewPager2.setVisibility(VISIBLE);
            mViewPager2.setOffscreenPageLimit(1);
            mViewPager2.setOrientation(carouselOrientation);
            CarouselPageChangeListener carouselPageChangeListener = new CarouselPageChangeListener();
            mViewPager2.registerOnPageChangeCallback(carouselPageChangeListener);
            CompositePageTransformer transformers = new CompositePageTransformer();
            transformers.addTransformer(new CarouselPageTransformer(carouselScale, carouselAlphaSide, carouselOrientation));
            transformers.addTransformer(new MarginPageTransformer((int) carouselItemInterval));
            mViewPager2.setPageTransformer(transformers);
            View childView = mViewPager2.getChildAt(0);
            if (childView instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) childView;
                recyclerView.setClipToPadding(false);
                if (carouselOrientation == RecyclerView.HORIZONTAL) {
                    recyclerView.setPadding(carouselMarginToParent, 0, carouselMarginToParent, 0);
                } else {
                    recyclerView.setPadding(0, carouselMarginToParent, 0, carouselMarginToParent);
                }
            }
        } else {
            mViewPager2.setVisibility(GONE);
            mViewPager.setVisibility(VISIBLE);
            //TODO
        }

        //设置indicators布局参数
        LayoutParams layoutParams = (LayoutParams) mRvIndicators.getLayoutParams();
        layoutParams.gravity = indicatorGravity;
        layoutParams.leftMargin = indicatorMarginLeft;
        layoutParams.topMargin = indicatorMarginTop;
        layoutParams.rightMargin = indicatorMarginRight;
        layoutParams.bottomMargin = indicatorMarginBottom;
        mRvIndicators.setLayoutParams(layoutParams);
        //设置indicators分割线
        mIndicatorDivider = new DividerItemDecoration(mContext, LinearLayoutManager.HORIZONTAL);
        mIndicatorDividerDrawable = new GradientDrawable();
        mIndicatorDividerDrawable.setSize(indicatorDividerSpace, MATCH_PARENT);
        mIndicatorDivider.setDrawable(mIndicatorDividerDrawable);
        mRvIndicators.addItemDecoration(mIndicatorDivider);
        //设置indicators布局管理器
        mRvIndicators.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        //设置indicators适配器
        setIndicators(indicatorResUnSelect, indicatorResSelected);//设置指示器图片资源
        setIndicatorsColor(indicatorColorUnSelect, indicatorColorSelected);
        setIndicatorsVisibility(indicatorVisibility);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_carousel_view2;
    }

    @Override
    protected void onPageSelected(int position) {
        super.onPageSelected(position);
        if (useViewPager2) {
            CarouselAdapter carouselAdapter = (CarouselAdapter) mViewPager2.getAdapter();
            if (carouselAdapter != null) {
                position = isInfinite ? carouselAdapter.toRealItemPosition(position) : position;
            }
        } else {
            //TODO
        }
        //刷新指示器
        refreshIndicators(position);
        if (null != mOnPageChangeListener) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void smoothScrollToNextPage() {
        if (useViewPager2) {
            if (null != mViewPager2) {
                mViewPager2.setCurrentItem(mViewPager2.getCurrentItem() + 1);
            }
        } else {
            if (null != mViewPager) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        }
    }

    @Override
    protected void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
        switch (state) {
            case ViewPager2.SCROLL_STATE_IDLE:
                if (mCanPlay) startPlay();
                if (useViewPager2 && isInfinite) {
                    RecyclerView.Adapter adapter = mViewPager2.getAdapter();
                    if (adapter instanceof CarouselAdapter) {
                        CarouselAdapter carouselAdapter = (CarouselAdapter) adapter;
                        int position = mViewPager2.getCurrentItem();
                        if (position == 0) {
                            position = carouselAdapter.getMiddlePosition();
                            mViewPager2.setCurrentItem(position, false);
                        } else if (position == carouselAdapter.getItemCount() - 1) {
                            position = carouselAdapter.getMiddlePosition() + carouselAdapter.getRealItemCount() - 1;
                            mViewPager2.setCurrentItem(position, false);
                        }
                    }
                }
                break;
            case ViewPager2.SCROLL_STATE_DRAGGING:
                if (mCanPlay) stopPlay();//说动拖动停止播放
                break;
            case ViewPager2.SCROLL_STATE_SETTLING:
                break;
        }
    }

    /**
     * 刷新指示器my_bg_grade.png
     *
     * @param position 选中位置
     */
    protected void refreshIndicators(int position) {
        if (null != mRvIndicators &&
                mRvIndicators.getVisibility() == VISIBLE &&
                null != mRvIndicators.getAdapter()) {
            ((IndicatorsAdapter) mRvIndicators.getAdapter()).setSelectedPosition(position);
        }
    }

    /**
     * 设置无限轮播
     *
     * @param isInfinite 是否支持无限轮播
     */
    public void setInfinite(boolean isInfinite) {
        if (this.isInfinite != isInfinite) {
            this.isInfinite = isInfinite;
            RecyclerView.Adapter adapter = mViewPager2.getAdapter();
            if (adapter instanceof CarouselAdapter) {
                if (mCanPlay) stopPlay();
                CarouselAdapter<E> carouselAdapter = ((CarouselAdapter) adapter);
                CarouselViewCreator<E> carouselViewCreator = carouselAdapter.getCarouselViewCreator();
                List<E> data = carouselAdapter.getData();
                setPages(carouselViewCreator, data);
                if (mCanPlay) startPlay();
            }
        }
    }

    /**
     * @return 是否无限轮播
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    /**
     * 设置页面布局和数据
     *
     * @param carouselViewCreator 布局创建者
     * @param data                数据
     */
    public void setPages(CarouselViewCreator<E> carouselViewCreator, List<E> data) {
        this.mData = data;
        if (useViewPager2) {
            CarouselAdapter carouselAdapter = new CarouselAdapter<>(carouselViewCreator, data, isInfinite);
            mViewPager2.setAdapter(carouselAdapter);
            if (isInfinite) mViewPager2.setCurrentItem(carouselAdapter.getMiddlePosition(), false);
        } else {
            //TODO
        }
        if (mUnSelectIndicatorRes != 0 || mSelectedIndicatorRes != 0) {
            mRvIndicators.setAdapter(new IndicatorsAdapter(data.size(), mUnSelectIndicatorRes, mSelectedIndicatorRes, mUnSelectIndicatorColor, mSelectedIndicatorColor));
        }
        if (mAutoPlay) {
            startPlay();
        }
    }

    /**
     * 设置页面切换监听
     *
     * @param onPageChangeListener 监听器
     */
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
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
     * 设置轮播间隔
     *
     * @param playDuration 间隔时间
     */
    public void setPlayDuration(@IntRange(from = 0) int playDuration) {
        this.mPlayDuration = playDuration;
    }

    /**
     * @return 是否正在轮播
     */
    public boolean isPlaying() {
        return mIsPlaying;
    }

    /**
     * 开始自动轮播
     */
    public void startPlay() {
        if (mIsPlaying) return;
        mIsPlaying = true;
        mCanPlay = true;
        mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mPlayDuration);
    }

    /**
     * 暂停自动轮播
     */
    public void stopPlay() {
        if (mIsPlaying) {
            mIsPlaying = false;
            mHandler.removeMessages(WHAT_AUTO_PLAY);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mCanPlay) startPlay();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCanPlay) stopPlay();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            if (mCanPlay) startPlay();
        } else {
            if (mCanPlay) stopPlay();
        }
    }

    private class CarouselPageChangeListener extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            CarouselView2.this.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            CarouselView2.this.onPageScrollStateChanged(state);
        }
    }
}
