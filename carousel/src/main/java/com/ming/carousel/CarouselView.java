package com.ming.carousel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ming.carousel.layoutmanager.OrientationHelper;
import com.ming.carousel.layoutmanager.PageSnapHelper;
import com.ming.carousel.layoutmanager.ViewPagerLayoutManager;
import com.xinhuamm.carousel.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * created by 陈明军 at 2019/1/25
 * for:通用轮播组件
 *
 * @author ming
 * 特点：
 * 1、内部使用{@link RecyclerView}实现的无限轮播图组件
 * 2、所有的属性都可以调用java方法动态配置，example{@link CarouselView#setItemInterval(float)}，{@link CarouselView2}不行
 * 3、轮播item区域较小时，大幅度快速滑动会出现回弹效果
 * 4、{@link CarouselView#setOrientation(int)} 最好和固定的控件高度配合使用
 * 5、可使用负数的item间距，{@link CarouselView2}不行
 * 6、可设置轮播item切换动画插值器{@link CarouselView#setCarouselInterpolator(Interpolator)}
 */
public class CarouselView<E> extends BaseCarouselView<E> {

    /**
     * 视图相关
     */
    protected CarouselRecyclerView mRvCarousel;
    protected CarouselLayoutManager mCarouselLayoutManager;

    protected boolean userInputEnable;

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
//        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray ta = null;
        if (null != attrs) {
            ta = context.obtainStyledAttributes(attrs, R.styleable.CarouselView);
        }
        int carouselOrientation = optInt(ta, R.styleable.CarouselView_mj_carousel_orientation, OrientationHelper.HORIZONTAL);
        boolean carouselReverseLayout = optBoolean(ta, R.styleable.CarouselView_mj_carousel_reverse_layout, false);
        isInfinite = optBoolean(ta, R.styleable.CarouselView_mj_carousel_infinite, true);
        float carouselScale = optFloat(ta, R.styleable.CarouselView_mj_carousel_scale, 0.8f);
        float carouselItemInterval = optDimension(ta, R.styleable.CarouselView_mj_carousel_item_interval, 0f);
        float carouselAlphaCenter = optFloat(ta, R.styleable.CarouselView_mj_carousel_alpha_center, 1f);
        float carouselAlphaSide = optFloat(ta, R.styleable.CarouselView_mj_carousel_alpha_side, 1f);
        int carouselMaxVisibleCount = optInt(ta, R.styleable.CarouselView_mj_carousel_max_visible_count, ViewPagerLayoutManager.DETERMINE_BY_MAX_AND_MIN);
        int carouselZaxisAlignment = optInt(ta, R.styleable.CarouselView_mj_carousel_z_alignment, CarouselLayoutManager.CENTER_ON_TOP);
        int carouselInterpolator = optInt(ta, R.styleable.CarouselView_mj_carousel_interpolator, -1);
        userInputEnable = optBoolean(ta, R.styleable.CarouselView_mj_carousel_user_input_enable, true);

        int indicatorVisibility = optInt(ta, R.styleable.CarouselView_mj_indicator_visibility, View.GONE);
        int indicatorResUnSelect = optResourceId(ta, R.styleable.CarouselView_mj_indicator_src_unSelect, R.drawable.ic_carousel_indicator_unselect);
        int indicatorResSelected = optResourceId(ta, R.styleable.CarouselView_mj_indicator_src_selected, R.drawable.ic_carousel_indicator_selected);
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

        mRvCarousel.setScrollable(userInputEnable);
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
                .setZAlignment(carouselZaxisAlignment)
                .build();
        // 嵌套滚到到0或者size位置会导致滑动冲突，虽然处理了滑动但是效果不明显，所以不用这个，也已备注废弃
        mCarouselLayoutManager.setInfinite(false);
        mCarouselLayoutManager.setSmoothScrollInterpolator(CarouselInterpolatorUtil.getCarouselInterpolator(carouselInterpolator));
        mCarouselLayoutManager.setOnPageChangeListener(new ViewPagerLayoutManager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (isInfinite) {
                    RecyclerView.Adapter adapter = mRvCarousel.getAdapter();
                    if (adapter instanceof CarouselAdapter) {
                        CarouselAdapter carouselAdapter = (CarouselAdapter) adapter;
                        position = carouselAdapter.getRealItemPosition(position);
                    }
                }
                CarouselView.this.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRvCarousel.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                CarouselView.this.onPageScrollStateChanged(newState);
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
        setIndicators(indicatorResUnSelect, indicatorResSelected);
        setIndicatorsColor(indicatorColorUnSelect, indicatorColorSelected);
        setIndicatorsVisibility(indicatorVisibility);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_carousel_view;
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
                if (mCanPlay && userInputEnable) {
                    startPlay();
                }
                // 无限循环状态下，切换到最后的位置后，需要自动切换回中间位置
                // 但是CarouselView发现会自动摇摆，遂先不支持自动切换回中间位置功能
//                scrollToFirstPosition();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                if (mCanPlay && userInputEnable) {
                    stopPlay();//说动拖动停止播放
                }
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                break;
            default:
                break;
        }
    }

    @Override
    public void setUserInputEnable(boolean enable) {
        mRvCarousel.setScrollable(enable);
    }

    /**
     * 设置页面布局和数据
     *
     * @param carouselViewCreator 布局创建者
     * @param data                数据
     */
    public void setPages(CarouselViewCreator<E> carouselViewCreator, List<E> data) {
        // 设置数据
        if (mData != data) {
            if (null == mData) {
                mData = new ArrayList<>(data);
            } else {
                mData.clear();
                mData.addAll(data);
            }
        }
        // 设置轮播图
        CarouselAdapter<E> carouselAdapter;
        RecyclerView.Adapter adapter = mRvCarousel.getAdapter();
        if (null == adapter) {
            carouselAdapter = new CarouselAdapter<>(carouselViewCreator, mData, isInfinite);
            mRvCarousel.setAdapter(carouselAdapter);
        } else {
            carouselAdapter = (CarouselAdapter<E>) adapter;
            carouselAdapter.setCarouselViewCreator(carouselViewCreator);
            carouselAdapter.setInfinite(isInfinite);
            carouselAdapter.setData(mData);
        }
        if (isInfinite) {
            mRvCarousel.scrollToPosition(carouselAdapter.getMiddlePosition());
        }
        // 设置指示器
        if (mUnSelectIndicatorRes != 0 || mSelectedIndicatorRes != 0) {
            IndicatorsAdapter indicatorsAdapter;
            RecyclerView.Adapter adapter1 = mRvIndicators.getAdapter();
            if (null == adapter1) {
                indicatorsAdapter = new IndicatorsAdapter(mData.size(),
                        mUnSelectIndicatorRes, mSelectedIndicatorRes,
                        mUnSelectIndicatorColor, mSelectedIndicatorColor);
            } else {
                indicatorsAdapter = (IndicatorsAdapter) adapter1;
                indicatorsAdapter.setItemCount(mData.size());
                indicatorsAdapter.notifyDataSetChanged();
            }
            mRvIndicators.setAdapter(indicatorsAdapter);
        }
        if (mAutoPlay) {
            startPlay();
        }
    }

    @Override
    public void setOrientation(@RecyclerView.Orientation int orientation) {
        super.setOrientation(orientation);
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

    @Override
    public void setRecyclerOverScrollMode(int overScrollMode) {
        mRvCarousel.setOverScrollMode(overScrollMode);
    }

    @Override
    protected void refreshInfinite() {
        RecyclerView.Adapter adapter = mRvCarousel.getAdapter();
        if (adapter instanceof CarouselAdapter) {
            if (mCanPlay) {
                stopPlay();
            }
            CarouselAdapter<E> carouselAdapter = ((CarouselAdapter) adapter);
            carouselAdapter.setInfinite(isInfinite);
            carouselAdapter.notifyDataSetChanged();
            if (isInfinite) {
                scrollToPosition(carouselAdapter.getMiddlePosition());
            } else {
                scrollToPosition(0);
            }
            if (mCanPlay) {
                startPlay();
            }
        }
    }

    @Override
    protected void refreshScale(float scale) {
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
    @Override
    protected void refreshAlphaSide(float alpha) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setMinAlpha(alpha);
        }
    }

    /**
     * 设置item之间的间距
     */
    @Override
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
     * 设置轮播图插值器
     *
     * @param interpolator 插值器
     */
    public void setCarouselInterpolator(Interpolator interpolator) {
        if (null != mCarouselLayoutManager) {
            mCarouselLayoutManager.setSmoothScrollInterpolator(interpolator);
        }
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
        if (mIsPlaying) {
            return;
        }
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
        if (mCanPlay) {
            startPlay();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCanPlay) {
            stopPlay();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            if (mCanPlay) {
                startPlay();
            }
        } else {
            if (mCanPlay) {
                stopPlay();
            }
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
        if (targetPosition < 0) {
            return;
        }
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
        if (!(layoutManager instanceof ViewPagerLayoutManager)) {
            return;
        }
        final int targetPosition = ((ViewPagerLayoutManager) layoutManager).getLayoutPositionOfView(targetView);
        smoothScrollToPositionForCarousel(targetPosition);
    }

    /**
     * 滚动到目标位置
     * 私有原因：轮播图位置有-100，100这种大于数量的位置，使用此方法区跳转到指定轮播图会不准
     *
     * @param targetPosition 目标位置
     */
    protected void smoothScrollToPositionForCarousel(int targetPosition) {
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
    public void setOnItemClickListener(OnItemClickListener<E> onItemClickListener) {
        RecyclerView.Adapter adapter = mRvCarousel.getAdapter();
        if (adapter instanceof CarouselAdapter) {
            CarouselAdapter<E> carouselAdapter = (CarouselAdapter<E>) adapter;
            carouselAdapter.setOnItemClickListener(onItemClickListener);
        }
    }

    /**
     * 设置长按事件
     *
     * @param onItemLongClickListener 长按监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener<E> onItemLongClickListener) {
        RecyclerView.Adapter adapter = mRvCarousel.getAdapter();
        if (adapter instanceof CarouselAdapter) {
            CarouselAdapter<E> carouselAdapter = (CarouselAdapter<E>) adapter;
            carouselAdapter.setOnItemLongClickListener(onItemLongClickListener);
        }
    }

    /**
     * 设置双击事件
     *
     * @param onItemLongClickListener 双击监听器
     */
    @Deprecated
    public void setOnItemDoubleClickListener(OnItemDoubleClickListener<E> onItemLongClickListener) {

    }

    /**
     * 设置页面切换监听
     *
     * @param onPageChangeListener 监听器
     */
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

// 原本通过ViewPagerLayoutManager实现无限循环时，CarouselView嵌套在ViewPager中会有滑动冲突，
// 通过下面的事件分发逻辑解决嵌套滑动冲突问题，但是后续发现ViewPagerLayoutManager实现无限循环
// ，会出现轮播图有数据但是不会显示，白屏的问题，所以改用其他方案代替无限循环功能，也就不需要下面的事件分发逻辑了。
//    private float touchSlop = 0;
//    private float initialX = 0f;
//    private float initialY = 0f;
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent e) {
//        int orientation = mCarouselLayoutManager.getOrientation();
//        // Early return if child can't scroll in same direction as parent
//        if (canChildScroll(orientation, -1f) || canChildScroll(orientation, 1f)) {
//            if (e.getAction() == MotionEvent.ACTION_DOWN) {
//                initialX = e.getX();
//                initialY = e.getY();
//                getParent().requestDisallowInterceptTouchEvent(true);
//            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
//                float dx = e.getX() - initialX;
//                float dy = e.getY() - initialY;
//                boolean isVpHorizontal = orientation == RecyclerView.HORIZONTAL;
//                // assuming ViewPager2 touch-slop is 2x touch-slop of child
//                float scaledDx = dx * (isVpHorizontal ? .5f : 1f);
//                float scaledDy = dy * (isVpHorizontal ? 1f : .5f);
//                if (scaledDx > touchSlop || scaledDy > touchSlop) {
//                    if (isVpHorizontal == (scaledDy > scaledDx)) {
//                        // Gesture is perpendicular, allow all parents to intercept
//                        getParent().requestDisallowInterceptTouchEvent(false);
//                    } else {
//                        // Gesture is parallel, query child if movement in that direction is possible
//                        if (canChildScroll(orientation, isVpHorizontal ? dx : dy)) {
//                            // Child can scroll, disallow all parents to intercept
//                            getParent().requestDisallowInterceptTouchEvent(true);
//                        } else {
//                            // Child cannot scroll, allow all parents to intercept
//                            getParent().requestDisallowInterceptTouchEvent(false);
//                        }
//                    }
//                }
//            }
//        }
//        return super.onInterceptTouchEvent(e);
//    }
//
//    private boolean canChildScroll(int orientation, float delta) {
//        int direction = (int) -Math.signum(delta);
//        switch (orientation) {
//            case RecyclerView.HORIZONTAL:
//                return getChildAt(0).canScrollHorizontally(direction);
//            case RecyclerView.VERTICAL:
//                return getChildAt(0).canScrollVertically(direction);
//            default:
//                return false;
//        }
//    }

}
