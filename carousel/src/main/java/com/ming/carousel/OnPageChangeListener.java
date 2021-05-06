package com.ming.carousel;

import androidx.viewpager2.widget.ViewPager2;

/**
 * 轮播图页面改变监听
 * @author ming
 */
public interface OnPageChangeListener {
    /**
     * 页面切换回调
     *
     * @param position 第几页面
     */
    void onPageSelected(int position);

    /**
     * 页面滑动状态改变回调
     *
     * @param state 新滑动状态
     */
    void onPageScrollStateChanged(@ViewPager2.ScrollState int state);
}