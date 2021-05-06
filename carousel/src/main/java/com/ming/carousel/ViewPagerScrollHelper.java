package com.ming.carousel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ming
 * @date 2020/9/16
 * for:滑动协助类
 */
public class ViewPagerScrollHelper {
    private static final int DURATION_DEFAULT = 400;
    /**
     * 保存前一个animatedValue
     */
    private static Map<View, Integer> previousValueMap;
    private static Interpolator interpolator;

    public static void setCurrentItem(final View pager, int item) {
        if (null == interpolator) {
            interpolator = CarouselInterpolatorUtil.getCarouselInterpolator(CarouselInterpolatorEnum.DEFAULT);
        }
        setCurrentItem(pager, item, interpolator);
    }

    public static void setCurrentItem(final View pager, int item, Interpolator interpolator) {
        setCurrentItem(pager, item, interpolator, DURATION_DEFAULT);
    }

    /**
     * 设置当前Item
     *
     * @param pager    viewpager2
     * @param item     下一个跳转的item
     * @param duration scroll时长
     */
    public static void setCurrentItem(final View pager, int item, Interpolator interpolator, long duration) {

        int dragPx;
        int pagePx;
        int currentItem;

        if (pager instanceof ViewPager2) {
            ViewPager2 viewPager2 = (ViewPager2) pager;
            currentItem = viewPager2.getCurrentItem();
            int orientation = viewPager2.getOrientation();
            pagePx = orientation == ViewPager2.ORIENTATION_VERTICAL
                    ? viewPager2.getHeight() : viewPager2.getWidth();
            View childView = viewPager2.getChildAt(0);
            if (childView instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) childView;
                pagePx -= (orientation == ViewPager2.ORIENTATION_VERTICAL
                        ? recyclerView.getPaddingTop() : recyclerView.getPaddingLeft()) * 2;
            }
        } else if (pager instanceof ViewPager) {
            ViewPager viewPager = (ViewPager) pager;
            currentItem = viewPager.getCurrentItem();
            pagePx = viewPager.getWidth();
        } else if (pager instanceof VerticalViewPager) {
            VerticalViewPager verticalViewPager = (VerticalViewPager) pager;
            currentItem = verticalViewPager.getCurrentItem();
            pagePx = verticalViewPager.getHeight();
        } else {
            return;
        }

        dragPx = pagePx * (item - currentItem);
        animateFakeDrag(pager, dragPx, interpolator, duration);
    }

    private static void animateFakeDrag(final View pager, int dragPx, Interpolator interpolator, long duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, dragPx);
        valueAnimator.addUpdateListener(animation -> {
            if (null == previousValueMap) {
                previousValueMap = new HashMap<>(8);
            }
            Integer integer = previousValueMap.get(pager);
            int previousValue = null == integer ? 0 : integer;
            int currentValue = (int) animation.getAnimatedValue();
            float currentPxToDrag = (float) (currentValue - previousValue);
            if (pager instanceof ViewPager2) {
                ((ViewPager2) pager).fakeDragBy(-currentPxToDrag);
            } else if (pager instanceof ViewPager) {
                ((ViewPager) pager).fakeDragBy(-currentPxToDrag);
            } else if (pager instanceof VerticalViewPager) {
                ((VerticalViewPager) pager).fakeDragBy(-currentPxToDrag);
            }
            previousValueMap.put(pager, currentValue);
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (pager instanceof ViewPager2) {
                    ((ViewPager2) pager).beginFakeDrag();
                } else if (pager instanceof ViewPager) {
                    ((ViewPager) pager).beginFakeDrag();
                } else if (pager instanceof VerticalViewPager) {
                    ((VerticalViewPager) pager).beginFakeDrag();
                }
                if (null == previousValueMap) {
                    previousValueMap = new HashMap<>(8);
                } else {
                    previousValueMap.put(pager, 0);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (pager instanceof ViewPager2) {
                    ((ViewPager2) pager).endFakeDrag();
                } else if (pager instanceof ViewPager) {
                    ((ViewPager) pager).endFakeDrag();
                } else if (pager instanceof VerticalViewPager) {
                    ((VerticalViewPager) pager).endFakeDrag();
                }
                if (null != previousValueMap) {
                    previousValueMap.remove(pager);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

}
