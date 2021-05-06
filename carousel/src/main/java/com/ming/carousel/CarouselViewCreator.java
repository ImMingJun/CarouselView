package com.ming.carousel;

import android.view.View;

/**
 * created by cmj on 2019/2/1
 * for:轮播ItemView处理
 */
public interface CarouselViewCreator<E> {

    /**
     * 注意：使用{@link CarouselView2#viewPager2}模式下，
     * item布局的{@link android.view.ViewGroup}容器<p>宽高</p>必须是{@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}
     * @see {@link androidx.viewpager2.widget.ViewPager2#enforceChildFillListener}
     *
     * @return item布局
     */
    int layoutId();

    /**
     * 绑定数据
     * @param container 容器
     * @param item 数据
     */
    void convert(View container, E item);
}
