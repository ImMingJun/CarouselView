package com.ming.carousel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.xinhuamm.carousel.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * created by cmj on 2020/3/27
 * for:轮播图第二版
 *
 * @author ming
 * 特点：
 * 1、内部使用{@link ViewPager2}和{@link ViewPager}实现的无限轮播图组件，支持这俩切换使用
 * 2、只支持部分属性动态配置，比{@link CarouselView}少
 * 3、内部使用{@link ViewPager}实现时，垂直Vertical样式不支持嵌套滑动
 * 4、CarouselView2为{@link ViewPager2}和{@link RecyclerView#VERTICAL}模式时，本身的布局高度必须固定，不允许使用{@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
 * 5、不允许使用负数间距，效果不好，请使用{@link CarouselView}，结合{@link CarouselLayoutManager#CENTER_ON_TOP}达到中间完全显示，两边被遮住的效果
 * 6、可设置轮播item切换动画插值器{@link CarouselView#setCarouselInterpolator(Interpolator)}
 * 7、ViewPager的自动切换页面效果不好，速度太快，可使用{@link CarouselView}
 */
public class CarouselView2<E> extends BaseCarouselView<E> {

    /**
     * 视图相关
     */
    protected ViewPager2 viewPager2;
    protected CarouselViewPager viewPager;
    protected CarouselVerticalViewPager verticalViewPager;

    /**
     * 配置相关
     */
    protected boolean useViewPager2;
    protected int carouselOrientation;
    protected boolean userInputEnable;
    protected Interpolator interpolator;
    protected Carousel2PageTransformer carousel2PageTransformer;
    protected Carousel2MarginPageTransformer carousel2MarginPageTransformer;
    protected CarouselPageTransformer carouselPageTransformer;

    public CarouselView2(@NonNull Context context) {
        this(context, null);
    }

    public CarouselView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    protected void initView(Context context, AttributeSet attrs) {
        TypedArray ta = null;
        if (null != attrs) {
            ta = mContext.obtainStyledAttributes(attrs, R.styleable.CarouselView2);
        }
        carouselOrientation = optInt(ta, R.styleable.CarouselView2_mj_carousel_orientation, RecyclerView.HORIZONTAL);
        float carouselScale = optFloat(ta, R.styleable.CarouselView2_mj_carousel_scale, 0.8f);
        float carouselAlphaSide = optFloat(ta, R.styleable.CarouselView2_mj_carousel_alpha_side, 1f);
        float carouselItemInterval = optDimension(ta, R.styleable.CarouselView2_mj_carousel_item_interval, 0f);
        int carouselMarginToParent = optDimension(ta, R.styleable.CarouselView2_mj_carousel_margin_to_parent, 0);
        isInfinite = optBoolean(ta, R.styleable.CarouselView2_mj_carousel_infinite, true);
        useViewPager2 = optBoolean(ta, R.styleable.CarouselView2_mj_carousel_useViewPager2, true);
        userInputEnable = optBoolean(ta, R.styleable.CarouselView2_mj_carousel_user_input_enable, true);
        int carouselInterpolator = optInt(ta, R.styleable.CarouselView2_mj_carousel_interpolator, CarouselInterpolatorEnum.DEFAULT);
        interpolator = CarouselInterpolatorUtil.getCarouselInterpolator(carouselInterpolator);

        int indicatorVisibility = optInt(ta, R.styleable.CarouselView2_mj_indicator_visibility, View.GONE);
        int indicatorResUnSelect = optResourceId(ta, R.styleable.CarouselView2_mj_indicator_src_unSelect, R.drawable.ic_carousel_indicator_unselect);
        int indicatorResSelected = optResourceId(ta, R.styleable.CarouselView2_mj_indicator_src_selected, R.drawable.ic_carousel_indicator_selected);
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
        mRvIndicators = container.findViewById(R.id.recyclerView_indicators);

        if (useViewPager2) {
            viewPager2 = container.findViewById(R.id.viewPager2_carousel);
            viewPager2.setOffscreenPageLimit(1);
            viewPager2.setUserInputEnabled(userInputEnable);
            viewPager2.setOrientation(carouselOrientation);
            ViewPager2PageChangeListener viewPager2PageChangeListener = new ViewPager2PageChangeListener();
            viewPager2.registerOnPageChangeCallback(viewPager2PageChangeListener);
            carousel2PageTransformer = new Carousel2PageTransformer(carouselScale, carouselAlphaSide, carouselOrientation);
            carousel2MarginPageTransformer = new Carousel2MarginPageTransformer((int) carouselItemInterval);
            CompositePageTransformer transformers = new CompositePageTransformer();
            transformers.addTransformer(carousel2PageTransformer);
            transformers.addTransformer(carousel2MarginPageTransformer);
            viewPager2.setPageTransformer(transformers);
            View childView = viewPager2.getChildAt(0);
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
            carouselPageTransformer = new CarouselPageTransformer(carouselScale, carouselAlphaSide, carouselOrientation);
            if (carouselOrientation == RecyclerView.VERTICAL) {
                verticalViewPager = container.findViewById(R.id.vertical_viewPager_carousel);
                verticalViewPager.setOffscreenPageLimit(3);
                verticalViewPager.setScrollable(userInputEnable);
                ViewPagerPageChangeListener viewPagerPageChangeListener = new ViewPagerPageChangeListener();
                verticalViewPager.setOnPageChangeListener(viewPagerPageChangeListener);
                verticalViewPager.setPageMargin((int) carouselItemInterval);
                verticalViewPager.setPageTransformer(true, carouselPageTransformer);
                verticalViewPager.setPadding(0, carouselMarginToParent, 0, carouselMarginToParent);
            } else {
                viewPager = container.findViewById(R.id.viewPager_carousel);
                viewPager.setOffscreenPageLimit(3);
                viewPager.setScrollable(userInputEnable);
                ViewPagerPageChangeListener viewPagerPageChangeListener = new ViewPagerPageChangeListener();
                viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
                viewPager.setPageMargin((int) carouselItemInterval);
                viewPager.setPageTransformer(true, carouselPageTransformer);
                viewPager.setPadding(carouselMarginToParent, 0, carouselMarginToParent, 0);
            }
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
        setIndicators(indicatorResUnSelect, indicatorResSelected);
        setIndicatorsColor(indicatorColorUnSelect, indicatorColorSelected);
        setIndicatorsVisibility(indicatorVisibility);
    }

    @Override
    protected int getContentLayoutId() {
        if (useViewPager2) {
            return R.layout.layout_carousel_view2_viewpager2;
        } else {
            if (carouselOrientation == RecyclerView.VERTICAL) {
                return R.layout.layout_carousel_view2_viewpager_vertical;
            } else {
                return R.layout.layout_carousel_view2_viewpager;
            }
        }
    }

    /**
     * 切换到下一页会出现滑动过快的问题，但是使用ViewPagerScrollHelper实现的拖拽切换效果优化后会有新的问题
     * setCurrentItem方法中判断了ViewPager是否正处于拖拽切换，是则直接抛出异常。
     * 所以拖拽切换页面使用不合适，只能使用原生方法
     */
    @Override
    public void smoothScrollToNextPage() {
        if (useViewPager2) {
            if (null != viewPager2) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
//                ViewPagerScrollHelper.setCurrentItem(viewPager2, viewPager2.getCurrentItem() + 1, interpolator);
            }
        } else {
            if (carouselOrientation == RecyclerView.VERTICAL) {
                if (null != verticalViewPager) {
                    verticalViewPager.setCurrentItem(verticalViewPager.getCurrentItem() + 1);
//                    ViewPagerScrollHelper.setCurrentItem(verticalViewPager, verticalViewPager.getCurrentItem() + 1, interpolator);
                }
            } else {
                if (null != viewPager) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
//                    ViewPagerScrollHelper.setCurrentItem(viewPager, viewPager.getCurrentItem() + 1, interpolator);
                }
            }
        }
    }

    @Override
    protected void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
        switch (state) {
            case ViewPager2.SCROLL_STATE_IDLE:
                if (mCanPlay && userInputEnable) {
                    startPlay();
                }
                scrollToFirstPosition();
                break;
            case ViewPager2.SCROLL_STATE_DRAGGING:
                if (mCanPlay && userInputEnable) {
                    //拖动停止播放
                    stopPlay();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 滑动到最开始的位置
     */
    private void scrollToFirstPosition() {
        if (isInfinite) {
            if (useViewPager2) {
                RecyclerView.Adapter adapter = viewPager2.getAdapter();
                if (adapter instanceof CarouselAdapter) {
                    CarouselAdapter carouselAdapter = (CarouselAdapter) adapter;
                    int position = viewPager2.getCurrentItem();
                    if (position == 0) {
                        position = carouselAdapter.getMiddlePosition();
                        viewPager2.setCurrentItem(position, false);
                    } else if (position == carouselAdapter.getItemCount() - 1) {
                        position = carouselAdapter.getMiddlePosition() + carouselAdapter.getRealItemCount() - 1;
                        viewPager2.setCurrentItem(position, false);
                    }
                }
            } else {
                PagerAdapter pagerAdapter;
                if (carouselOrientation == RecyclerView.VERTICAL) {
                    pagerAdapter = verticalViewPager.getAdapter();
                    if (pagerAdapter instanceof CarouselPagerAdapter) {
                        CarouselPagerAdapter carouselPagerAdapter = (CarouselPagerAdapter) pagerAdapter;
                        int position = verticalViewPager.getCurrentItem();
                        if (position == 0) {
                            position = carouselPagerAdapter.getMiddlePosition();
                            verticalViewPager.setCurrentItem(position, false);
                        } else if (position == carouselPagerAdapter.getCount() - 1) {
                            position = carouselPagerAdapter.getMiddlePosition() + carouselPagerAdapter.getRealItemCount() - 1;
                            verticalViewPager.setCurrentItem(position, false);
                        }
                    }
                } else {
                    pagerAdapter = viewPager.getAdapter();
                    if (pagerAdapter instanceof CarouselPagerAdapter) {
                        CarouselPagerAdapter carouselPagerAdapter = (CarouselPagerAdapter) pagerAdapter;
                        int position = viewPager.getCurrentItem();
                        if (position == 0) {
                            position = carouselPagerAdapter.getMiddlePosition();
                            viewPager.setCurrentItem(position, false);
                        } else if (position == carouselPagerAdapter.getCount() - 1) {
                            position = carouselPagerAdapter.getMiddlePosition() + carouselPagerAdapter.getRealItemCount() - 1;
                            viewPager.setCurrentItem(position, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setUserInputEnable(boolean enable) {
        if (useViewPager2) {
            viewPager2.setUserInputEnabled(enable);
        } else {
            if (carouselOrientation == RecyclerView.VERTICAL) {
                verticalViewPager.setScrollable(enable);
            } else {
                viewPager.setScrollable(enable);
            }
        }
    }

    @Override
    public void setRecyclerOverScrollMode(int overScrollMode) {
        if (useViewPager2) {
            viewPager2.setOverScrollMode(overScrollMode);
        } else {
            if (carouselOrientation == RecyclerView.VERTICAL) {
                verticalViewPager.setOverScrollMode(overScrollMode);
            } else {
                viewPager.setOverScrollMode(overScrollMode);
            }
        }
    }

    @Override
    protected void refreshScale(float scale) {
        if (useViewPager2) {
            carousel2PageTransformer.setMinScale(scale);
            viewPager2.requestTransform();
        } else {
            carouselPageTransformer = new CarouselPageTransformer(scale,
                    carouselPageTransformer.getMinAlpha(), carouselOrientation);
            PagerAdapter pagerAdapter;
            if (carouselOrientation == RecyclerView.VERTICAL) {
                verticalViewPager.setPageTransformer(true, carouselPageTransformer);
                pagerAdapter = verticalViewPager.getAdapter();
            } else {
                viewPager.setPageTransformer(true, carouselPageTransformer);
                pagerAdapter = viewPager.getAdapter();
            }
            if (pagerAdapter instanceof CarouselPagerAdapter) {
                if (mCanPlay) {
                    stopPlay();
                }
                CarouselPagerAdapter<E> carouselPagerAdapter = (CarouselPagerAdapter<E>) pagerAdapter;
                CarouselViewCreator<E> carouselViewCreator = carouselPagerAdapter.getCarouselViewCreator();
                List<E> data = mData;
                mData = null;
                if (carouselOrientation == RecyclerView.VERTICAL) {
                    verticalViewPager.setAdapter(null);
                } else {
                    viewPager.setAdapter(null);
                }
                setPages(carouselViewCreator, data);
                if (mCanPlay) {
                    startPlay();
                }
            }
        }
    }

    @Override
    protected void refreshAlphaSide(float alpha) {
        if (useViewPager2) {
            carousel2PageTransformer.setMinAlpha(alpha);
            viewPager2.requestTransform();
        } else {
            carouselPageTransformer = new CarouselPageTransformer(carouselPageTransformer.getMinScale(),
                    alpha, carouselOrientation);
            if (carouselOrientation == RecyclerView.VERTICAL) {
                verticalViewPager.setPageTransformer(true, carouselPageTransformer);
            } else {
                viewPager.setPageTransformer(true, carouselPageTransformer);
            }
        }
    }

    @Override
    public void setItemInterval(float dpInterval) {
        int marginPx = (int) dpToPx(dpInterval);
        if (useViewPager2) {
            carousel2MarginPageTransformer.setMargin(marginPx);
            viewPager2.requestTransform();
        } else {
            PagerAdapter pagerAdapter;
            if (carouselOrientation == RecyclerView.VERTICAL) {
                verticalViewPager.setPageMargin(marginPx);
                pagerAdapter = verticalViewPager.getAdapter();
            } else {
                viewPager.setPageMargin(marginPx);
                pagerAdapter = viewPager.getAdapter();
            }
            if (pagerAdapter instanceof CarouselPagerAdapter) {
                if (mCanPlay) {
                    stopPlay();
                }
                CarouselPagerAdapter<E> carouselPagerAdapter = (CarouselPagerAdapter<E>) pagerAdapter;
                CarouselViewCreator<E> carouselViewCreator = carouselPagerAdapter.getCarouselViewCreator();
                List<E> data = mData;
                mData = null;
                if (carouselOrientation == RecyclerView.VERTICAL) {
                    verticalViewPager.setAdapter(null);
                } else {
                    viewPager.setAdapter(null);
                }
                setPages(carouselViewCreator, data);
                if (mCanPlay) {
                    startPlay();
                }
            }
        }
    }

    /**
     * 设置距离父布局的边距
     *
     * @param dpMargin dp
     */
    public void setMarginToParent(float dpMargin) {
        int marginPx = (int) dpToPx(dpMargin);
        if (useViewPager2) {
            View childView = viewPager2.getChildAt(0);
            if (childView instanceof RecyclerView) {
                // 设置边距
                RecyclerView recyclerView = (RecyclerView) childView;
                recyclerView.setClipToPadding(false);
                if (carouselOrientation == RecyclerView.HORIZONTAL) {
                    recyclerView.setPadding(marginPx, 0, marginPx, 0);
                } else {
                    recyclerView.setPadding(0, marginPx, 0, marginPx);
                }
                // 重新刷新的适配器
                RecyclerView.Adapter adapter = viewPager2.getAdapter();
                if (adapter instanceof CarouselAdapter) {
                    if (mCanPlay) {
                        stopPlay();
                    }
                    CarouselAdapter<E> carouselAdapter = ((CarouselAdapter) adapter);
                    CarouselViewCreator<E> carouselViewCreator = carouselAdapter.getCarouselViewCreator();
                    List<E> data = mData;
                    mData = null;
                    viewPager2.setAdapter(null);
                    setPages(carouselViewCreator, data);
                    if (mCanPlay) {
                        startPlay();
                    }
                }
            }
        } else {
            PagerAdapter pagerAdapter;
            if (carouselOrientation == RecyclerView.VERTICAL) {
                verticalViewPager.setPadding(0, marginPx, 0, marginPx);
                pagerAdapter = verticalViewPager.getAdapter();
            } else {
                viewPager.setPadding(marginPx, 0, marginPx, 0);
                pagerAdapter = viewPager.getAdapter();
            }
            if (pagerAdapter instanceof CarouselPagerAdapter) {
                if (mCanPlay) {
                    stopPlay();
                }
                CarouselPagerAdapter<E> carouselPagerAdapter = (CarouselPagerAdapter<E>) pagerAdapter;
                CarouselViewCreator<E> carouselViewCreator = carouselPagerAdapter.getCarouselViewCreator();
                List<E> data = mData;
                mData = null;
                if (carouselOrientation == RecyclerView.VERTICAL) {
                    verticalViewPager.setAdapter(null);
                } else {
                    viewPager.setAdapter(null);
                }
                setPages(carouselViewCreator, data);
                if (mCanPlay) {
                    startPlay();
                }
            }
        }
    }

    @Override
    protected void refreshInfinite() {
        if (useViewPager2) {
            RecyclerView.Adapter adapter = viewPager2.getAdapter();
            if (adapter instanceof CarouselAdapter) {
                if (mCanPlay) {
                    stopPlay();
                }
                CarouselAdapter<E> carouselAdapter = ((CarouselAdapter) adapter);
                carouselAdapter.setInfinite(isInfinite);
                carouselAdapter.notifyDataSetChanged();
                if (isInfinite) {
                    viewPager2.setCurrentItem(carouselAdapter.getMiddlePosition(), false);
                } else {
                    viewPager2.setCurrentItem(0, false);
                }
                if (mCanPlay) {
                    startPlay();
                }
            }
        } else {
            PagerAdapter pagerAdapter;
            if (carouselOrientation == RecyclerView.VERTICAL) {
                pagerAdapter = verticalViewPager.getAdapter();
            } else {
                pagerAdapter = viewPager.getAdapter();
            }
            if (pagerAdapter instanceof CarouselPagerAdapter) {
                if (mCanPlay) {
                    stopPlay();
                }
                CarouselPagerAdapter<E> carouselPagerAdapter = (CarouselPagerAdapter<E>) pagerAdapter;
                carouselPagerAdapter.setInfinite(isInfinite);
                carouselPagerAdapter.notifyDataSetChanged();
                int position = 0;
                if (isInfinite) {
                    position = carouselPagerAdapter.getMiddlePosition();
                }
                if (carouselOrientation == RecyclerView.VERTICAL) {
                    verticalViewPager.setCurrentItem(position, false);
                } else {
                    viewPager.setCurrentItem(position, false);
                }
                if (mCanPlay) {
                    startPlay();
                }
            }
        }
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
        if (useViewPager2) {
            CarouselAdapter<E> carouselAdapter;
            RecyclerView.Adapter adapter = viewPager2.getAdapter();
            if (null == adapter) {
                carouselAdapter = new CarouselAdapter<>(carouselViewCreator, mData, isInfinite);
                viewPager2.setAdapter(carouselAdapter);
            } else {
                carouselAdapter = (CarouselAdapter<E>) adapter;
                carouselAdapter.setCarouselViewCreator(carouselViewCreator);
                carouselAdapter.setData(mData);
                carouselAdapter.setInfinite(isInfinite);
                carouselAdapter.notifyDataSetChanged();
            }
            if (isInfinite) {
                viewPager2.setCurrentItem(carouselAdapter.getMiddlePosition(), false);
            }
        } else {
            CarouselPagerAdapter<E> carouselPagerAdapter;
            if (carouselOrientation == RecyclerView.VERTICAL) {
                PagerAdapter pagerAdapter = verticalViewPager.getAdapter();
                if (null == pagerAdapter) {
                    carouselPagerAdapter = new CarouselPagerAdapter<>(carouselViewCreator, data, isInfinite);
                    verticalViewPager.setAdapter(carouselPagerAdapter);
                } else {
                    carouselPagerAdapter = (CarouselPagerAdapter<E>) pagerAdapter;
                    carouselPagerAdapter.setCarouselViewCreator(carouselViewCreator);
                    carouselPagerAdapter.setData(mData);
                    carouselPagerAdapter.setInfinite(isInfinite);
                    carouselPagerAdapter.notifyDataSetChanged();
                }
                if (isInfinite) {
                    verticalViewPager.setCurrentItem(carouselPagerAdapter.getMiddlePosition(), false);
                }
            } else {
                PagerAdapter pagerAdapter = viewPager.getAdapter();
                if (null == pagerAdapter) {
                    carouselPagerAdapter = new CarouselPagerAdapter<>(carouselViewCreator, data, isInfinite);
                    viewPager.setAdapter(carouselPagerAdapter);
                } else {
                    carouselPagerAdapter = (CarouselPagerAdapter<E>) pagerAdapter;
                    carouselPagerAdapter.setCarouselViewCreator(carouselViewCreator);
                    carouselPagerAdapter.setData(mData);
                    carouselPagerAdapter.setInfinite(isInfinite);
                    carouselPagerAdapter.notifyDataSetChanged();
                }
                if (isInfinite) {
                    viewPager.setCurrentItem(carouselPagerAdapter.getMiddlePosition(), false);
                }
            }
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

    /**
     * 设置页面切换监听
     *
     * @param onPageChangeListener 监听器
     */
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    /**
     * 设置点击事件
     *
     * @param onItemClickListener 点击监听器
     */
    public void setOnItemClickListener(OnItemClickListener<E> onItemClickListener) {
        if (useViewPager2) {
            RecyclerView.Adapter adapter = viewPager2.getAdapter();
            if (adapter instanceof CarouselAdapter) {
                CarouselAdapter<E> carouselAdapter = (CarouselAdapter) adapter;
                carouselAdapter.setOnItemClickListener(onItemClickListener);
            }
        } else {
            PagerAdapter adapter;
            if (carouselOrientation == RecyclerView.VERTICAL) {
                adapter = verticalViewPager.getAdapter();
            } else {
                adapter = viewPager.getAdapter();
            }
            if (adapter instanceof CarouselPagerAdapter) {
                CarouselPagerAdapter<E> pagerAdapter = (CarouselPagerAdapter) adapter;
                pagerAdapter.setOnItemClickListener(onItemClickListener);
            }
        }
    }

    /**
     * 设置长按事件
     *
     * @param onItemLongClickListener 长按监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener<E> onItemLongClickListener) {
        if (useViewPager2) {
            RecyclerView.Adapter adapter = viewPager2.getAdapter();
            if (adapter instanceof CarouselAdapter) {
                CarouselAdapter<E> carouselAdapter = (CarouselAdapter) adapter;
                carouselAdapter.setOnItemLongClickListener(onItemLongClickListener);
            }
        } else {
            PagerAdapter adapter;
            if (carouselOrientation == RecyclerView.VERTICAL) {
                adapter = verticalViewPager.getAdapter();
            } else {
                adapter = viewPager.getAdapter();
            }
            if (adapter instanceof CarouselPagerAdapter) {
                CarouselPagerAdapter<E> pagerAdapter = (CarouselPagerAdapter) adapter;
                pagerAdapter.setOnItemLongClickListener(onItemLongClickListener);
            }
        }
    }

    /**
     * 设置轮播图插值器
     * <p>
     * 废弃原因：拖拽效果切换页面容易导致奔溃
     *
     * @param interpolator 插值器
     */
    @Deprecated
    public void setCarouselInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    public void setOrientation(@RecyclerView.Orientation int orientation) {
        super.setOrientation(orientation);
        if (carouselOrientation == orientation) {
            return;
        }
        carouselOrientation = orientation;
        if (useViewPager2) {
            viewPager2.setOrientation(orientation);
        }
        // 不使用ViewPager2不支持动态更换方向
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

    private class ViewPager2PageChangeListener extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            position = getRealItemPosition(position);
            CarouselView2.this.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            CarouselView2.this.onPageScrollStateChanged(state);
        }
    }

    private class ViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            position = getRealItemPosition(position);
            CarouselView2.this.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            CarouselView2.this.onPageScrollStateChanged(state);
        }
    }

    private int getRealItemPosition(int position) {
        if (isInfinite) {
            if (useViewPager2) {
                CarouselAdapter<E> carouselAdapter = (CarouselAdapter) viewPager2.getAdapter();
                if (carouselAdapter != null) {
                    position = carouselAdapter.getRealItemPosition(position);
                }
            } else {
                PagerAdapter carouselPagerAdapter;
                if (carouselOrientation == RecyclerView.VERTICAL) {
                    carouselPagerAdapter = verticalViewPager.getAdapter();
                } else {
                    carouselPagerAdapter = viewPager.getAdapter();
                }
                if (carouselPagerAdapter instanceof CarouselPagerAdapter) {
                    position = ((CarouselPagerAdapter) carouselPagerAdapter).getRealItemPosition(position);
                }
            }
        }
        return position;
    }

}
