package com.ming.carousel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ming.carousel.layoutmanager.OrientationHelper;
import com.ming.carousel.layoutmanager.PageSnapHelper;
import com.ming.carousel.layoutmanager.ViewPagerLayoutManager;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * created by 陈明军 at 2019/1/25
 * for:通用轮播组件
 * 注意：
 * 1、设置setOrientation(ViewPagerLayoutManager.VERTICAL)最好和固定的CarouselView高度配合使用
 * 2、缺陷：作为列表头部时，不能垂直滑动
 */
public class CarouselView<E> extends CarouselBaseView<E> {

    //view
    protected RecyclerView mRvCarousel;
    protected RecyclerView mRvIndicators;
    protected CarouselLayoutManager mCarouselLayoutManager;
    protected DividerItemDecoration mIndicatorDivider;
    protected GradientDrawable mIndicatorDividerDrawable;

    //data
    protected int mUnSelectIndicatorRes, mSelectedIndicatorRes;
    protected int mUnSelectIndicatorColor, mSelectedIndicatorColor;
    protected boolean mAutoPlay = false;
    //state
    protected boolean mIsPlaying = false;
    protected boolean mCanPlay = false;
    protected OnPageChangeListener mOnPageChangeListener;
    protected CarouselTouchListener mCarouselTouchListener;

    public CarouselView(Context context) {
        this(context, null);
    }

    public CarouselView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void initView(Context context, AttributeSet attrs) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray ta = null;
        if (null != attrs) {
            ta = context.obtainStyledAttributes(attrs, R.styleable.CarouselView);
        }
        int carouselOrientation = optInt(ta, R.styleable.CarouselView_mj_carousel_orientation, OrientationHelper.HORIZONTAL);
        boolean carouselReverseLayout = optBoolean(ta, R.styleable.CarouselView_mj_carousel_reverse_layout, false);
        boolean carouselInfinite = optBoolean(ta, R.styleable.CarouselView_mj_carousel_infinite, true);
        float carouselScale = optFloat(ta, R.styleable.CarouselView_mj_carousel_scale, 0.8f);
        float carouselItemInterval = optDimension(ta, R.styleable.CarouselView_mj_carousel_item_interval, 0f);
        float carouselAlphaCenter = optFloat(ta, R.styleable.CarouselView_mj_carousel_alpha_center, 1f);
        float carouselAlphaSide = optFloat(ta, R.styleable.CarouselView_mj_carousel_alpha_side, 1f);
        int carouselMaxVisibleCount = optInt(ta, R.styleable.CarouselView_mj_carousel_max_visible_count, ViewPagerLayoutManager.DETERMINE_BY_MAX_AND_MIN);
        int carouselZAlignment = optInt(ta, R.styleable.CarouselView_mj_carousel_z_alignment, CarouselLayoutManager.CENTER_ON_TOP);

        int indicatorVisibility = optInt(ta, R.styleable.CarouselView_mj_indicator_visibility, View.GONE);
        int indicatorResUnSelect = optResourceId(ta, R.styleable.CarouselView_mj_indicator_src_unSelect, R.drawable.carousel_ic_indicator_unselect);
        int indicatorResSelected = optResourceId(ta, R.styleable.CarouselView_mj_indicator_src_selected, R.drawable.carousel_ic_indicator_selected);
        int indicatorColorUnSelect = optColor(ta, R.styleable.CarouselView_mj_indicator_color_unSelect, 0);
        int indicatorColorSelected = optColor(ta, R.styleable.CarouselView_mj_indicator_color_selected, 0);
        int indicatorDividerSpace = optDimension(ta, R.styleable.CarouselView_mj_indicator_divider_space, 0);
        int indicatorGravity = optInt(ta, R.styleable.CarouselView_mj_indicator_gravity, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        int indicatorMarginLeft = optDimension(ta, R.styleable.CarouselView_mj_indicator_margin_left, 0);
        int indicatorMarginTop = optDimension(ta, R.styleable.CarouselView_mj_indicator_margin_top, 0);
        int indicatorMarginRight = optDimension(ta, R.styleable.CarouselView_mj_indicator_margin_right, 0);
        int indicatorMarginBottom = optDimension(ta, R.styleable.CarouselView_mj_indicator_margin_bottom, 0);

        mPlayDuration = optInt(ta, R.styleable.CarouselView_mj_play_duration, -1);
        mAutoPlay = optBoolean(ta, R.styleable.CarouselView_mj_play_auto, false);
        if (ta != null) {
            ta.recycle();
        }

        View container = inflate(context, getContentLayoutId(), this);
        mRvCarousel = container.findViewById(R.id.recyclerView_carousel);
        mRvIndicators = container.findViewById(R.id.recyclerView_indicators);

        //布局管理器
        mCarouselLayoutManager = CarouselLayoutManager
                .Builder(context)
                .setOrientation(carouselOrientation)
                .setReverseLayout(carouselReverseLayout)
                .setMinScale(carouselScale)
                .setItemSpace(carouselItemInterval)
                .setMaxAlpha(carouselAlphaCenter)
                .setMinAlpha(carouselAlphaSide)
                .setMaxVisibleItemCount(carouselMaxVisibleCount)
                .setZAlignment(carouselZAlignment)
                .build();
        mCarouselLayoutManager.setInfinite(carouselInfinite);//默认无限循环
        mCarouselLayoutManager.setOnPageChangeListener(new ViewPagerLayoutManager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                CarouselView.this.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                CarouselView.this.onPageScrollStateChanged(state);
            }
        });
        mRvCarousel.setLayoutManager(mCarouselLayoutManager);

        //单页滑动
        PageSnapHelper pagerSnapHelper = new PageSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mRvCarousel);

        //设置indicators布局参数
        LayoutParams layoutParams = (LayoutParams) mRvIndicators.getLayoutParams();
        layoutParams.gravity = indicatorGravity;
        layoutParams.leftMargin = indicatorMarginLeft;
        layoutParams.topMargin = indicatorMarginTop;
        layoutParams.rightMargin = indicatorMarginRight;
        layoutParams.bottomMargin = indicatorMarginBottom;
        mRvIndicators.setLayoutParams(layoutParams);
        //设置indicators分割线
        mIndicatorDivider = new DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL);
        mIndicatorDividerDrawable = new GradientDrawable();
        mIndicatorDividerDrawable.setSize(indicatorDividerSpace, MATCH_PARENT);
        mIndicatorDivider.setDrawable(mIndicatorDividerDrawable);
        mRvIndicators.addItemDecoration(mIndicatorDivider);
        //设置indicators布局管理器
        mRvIndicators.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        //设置indicators适配器
        setIndicators(indicatorResUnSelect, indicatorResSelected);//设置指示器图片资源
        setIndicatorsColor(indicatorColorUnSelect, indicatorColorSelected);
        setIndicatorsVisibility(indicatorVisibility);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_carousel_view;
    }

    @Override
    protected void onPageSelected(int position) {
        super.onPageSelected(position);
        //刷新指示器
        refreshIndicators(position);
        if (null != mOnPageChangeListener) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void smoothScrollToNextPage() {
        if (null != mCarouselLayoutManager) {
            smoothScrollToPositionForCarousel(mCarouselLayoutManager.getCurrentPositionOffset() + 1);
        }
    }

    @Override
    protected void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                if (mCanPlay) startPlay();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                if (mCanPlay) stopPlay();//说动拖动停止播放
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
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
     * 设置页面布局和数据
     *
     * @param carouselViewCreator 布局创建者
     * @param data                数据
     */
    public void setPages(CarouselViewCreator<E> carouselViewCreator, List<E> data) {
        this.mData = data;
        mRvCarousel.setAdapter(new CarouselAdapter<>(carouselViewCreator, data));
        if (mUnSelectIndicatorRes != 0 || mSelectedIndicatorRes != 0) {
            mRvIndicators.setAdapter(new IndicatorsAdapter(data.size(), mUnSelectIndicatorRes, mSelectedIndicatorRes, mUnSelectIndicatorColor, mSelectedIndicatorColor));
        }
        if (mAutoPlay) {
            startPlay();
        }
    }

    /**
     * 设置方向
     *
     * @param orientation 方向
     */
    public void setOrientation(int orientation) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setOrientation(orientation);
        }
    }

    /**
     * 设置是否逆向排序
     *
     * @param reverseLayout 排序方向
     */
    public void setReverseLayout(boolean reverseLayout) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setReverseLayout(reverseLayout);
        }
    }

    /**
     * 设置Recyclerview的over-scroll mode
     * {@link RecyclerView#setOverScrollMode}
     */
    public void setRecyclerOverScrollMode(int overScrollMode) {
        mRvCarousel.setOverScrollMode(overScrollMode);
    }

    /**
     * 设置无限轮播
     *
     * @param isInfinite 是否支持无限轮播
     */
    public void setInfinite(boolean isInfinite) {
        if (null != mCarouselLayoutManager) {
            if (!isInfinite && null != mData &&
                    (mCarouselLayoutManager.getCurrentPositionOffset() >= mData.size() ||
                            mCarouselLayoutManager.getCurrentPositionOffset() < 0)) {
                mCarouselLayoutManager.scrollToPosition(0);//如果关闭无限循环功能，就先移动到0位置，否则就会导致消失
            }
            mCarouselLayoutManager.setInfinite(isInfinite);
        }
    }

    /**
     * @return 是否无限轮播
     */
    public boolean isInfinite() {
        return null != mCarouselLayoutManager && mCarouselLayoutManager.getInfinite();
    }

    /**
     * 设置中间和两侧的大小比例
     *
     * @param scale 比例
     */
    public void setScale(@FloatRange(from = 0.0f, to = 1.0) float scale) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setMinScale(scale);
        }
    }

    /**
     * 设置中间选中的Item的透明度
     *
     * @param alpha 透明度
     */
    public void setAlphaCenter(@FloatRange(from = 0.0f, to = 1.0) float alpha) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setMaxAlpha(alpha);
        }
    }

    /**
     * 设置两侧的透明度
     *
     * @param alpha 透明度
     */
    public void setAlphaSide(@FloatRange(from = 0.0f, to = 1.0) float alpha) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setMinAlpha(alpha);
        }
    }

    /**
     * 设置item之间的间距
     */
    public void setItemInterval(float dpInterval) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setItemSpace(dpToPx(dpInterval));
        }
    }

    /**
     * 设置居中的视图显示在最上面（默认zAlignment = CENTER_ON_TOP）
     * 或者左侧靠上，右侧靠上
     *
     * @param zAlignment 靠上参数
     * @see CarouselLayoutManager
     */
    public void setZAlignment(int zAlignment) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setZAlignment(zAlignment);
        }
    }

    /**
     * 设置显示Item最大的数量
     * 最好设置为屏幕上一次显示的数量
     * 也可以不设置，一般不用
     *
     * @param maxVisibleItemCount 数量
     */
    public void setMaxVisibleItemCount(int maxVisibleItemCount) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setMaxVisibleItemCount(maxVisibleItemCount);
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
        mRvIndicators.addItemDecoration(mIndicatorDivider);//重刷分割线,直接调用requestLayout不能马上生效
    }

    /**
     * 设置指示器位置
     *
     * @param gravity 相对位置
     * @see Gravity
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


    /**
     * @return 当前中间Item的位置
     */
    public int getCurrentCenterPosition() {
        return null == mCarouselLayoutManager ? 0 : mCarouselLayoutManager.getCurrentPosition();
    }

    /**
     * 跳转到目标位置
     * 需要在{@link #setPages(CarouselViewCreator, List)}方法之后调用
     *
     * @param targetPosition 目标位置
     */
    public void scrollToPosition(int targetPosition) {
        if (targetPosition < 0) return;
        if (targetPosition >= mData.size()) return;
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.scrollToPosition(targetPosition);
        }
    }

    /**
     * 滚动到目标位置
     *
     * @param targetView 目标位置
     */
    public void smoothScrollToTargetViewForCarousel(View targetView) {
        final RecyclerView.LayoutManager layoutManager = mRvCarousel.getLayoutManager();
        if (!(layoutManager instanceof ViewPagerLayoutManager)) return;
        final int targetPosition = ((ViewPagerLayoutManager) layoutManager).getLayoutPositionOfView(targetView);
        smoothScrollToPositionForCarousel(targetPosition);
    }

    /**
     * 滚动到目标位置
     * 废弃原因：轮播图位置有-100，100这种大于数量的位置，使用此方法区跳转到指定轮播图会不准
     *
     * @param targetPosition 目标位置
     */
    public void smoothScrollToPositionForCarousel(int targetPosition) {
        final int delta = mCarouselLayoutManager.getOffsetToPosition(targetPosition);
        if (mCarouselLayoutManager.getOrientation() == RecyclerView.VERTICAL) {
            mRvCarousel.smoothScrollBy(0, delta);
        } else {
            mRvCarousel.smoothScrollBy(delta, 0);
        }
    }

    /**
     * 设置点击事件
     *
     * @param onItemClickListener 点击监听器
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (null == mCarouselTouchListener) {
            mCarouselTouchListener = new CarouselTouchListener(onItemClickListener);
            mRvCarousel.addOnItemTouchListener(mCarouselTouchListener);
        } else {
            mCarouselTouchListener.setOnItemClickListener(onItemClickListener);
        }
    }

    /**
     * 设置长按事件
     *
     * @param onItemLongClickListener 长按监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        if (null == mCarouselTouchListener) {
            mCarouselTouchListener = new CarouselTouchListener(onItemLongClickListener);
            mRvCarousel.addOnItemTouchListener(mCarouselTouchListener);
        } else {
            mCarouselTouchListener.setOnItemLongClickListener(onItemLongClickListener);
        }
    }

    /**
     * 设置双击事件
     *
     * @param onItemLongClickListener 双击监听器
     */
    public void setOnItemDoubleClickListener(OnItemDoubleClickListener onItemLongClickListener) {
        if (null == mCarouselTouchListener) {
            mCarouselTouchListener = new CarouselTouchListener(onItemLongClickListener);
            mRvCarousel.addOnItemTouchListener(mCarouselTouchListener);
        } else {
            mCarouselTouchListener.setOnItemDoubleClickListener(onItemLongClickListener);
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

    private float touchSlop = 0;
    private float initialX = 0f;
    private float initialY = 0f;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int orientation = mCarouselLayoutManager.getOrientation();
        // Early return if child can't scroll in same direction as parent
        if (canChildScroll(orientation, -1f) || canChildScroll(orientation, 1f)) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                initialX = e.getX();
                initialY = e.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = e.getX() - initialX;
                float dy = e.getY() - initialY;
                boolean isVpHorizontal = orientation == RecyclerView.HORIZONTAL;
                // assuming ViewPager2 touch-slop is 2x touch-slop of child
                float scaledDx = dx * (isVpHorizontal ? .5f : 1f);
                float scaledDy = dy * (isVpHorizontal ? 1f : .5f);
                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    if (isVpHorizontal == (scaledDy > scaledDx)) {
                        // Gesture is perpendicular, allow all parents to intercept
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        // Gesture is parallel, query child if movement in that direction is possible
                        if (canChildScroll(orientation, isVpHorizontal ? dx : dy)) {
                            // Child can scroll, disallow all parents to intercept
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            // Child cannot scroll, allow all parents to intercept
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(e);
    }

    private boolean canChildScroll(int orientation, float delta) {
        int direction = (int) -Math.signum(delta);
        switch (orientation) {
            case RecyclerView.HORIZONTAL:
                return getChildAt(0).canScrollHorizontally(direction);
            case RecyclerView.VERTICAL:
                return getChildAt(0).canScrollVertically(direction);
            default:
                return false;
        }

    }

}
