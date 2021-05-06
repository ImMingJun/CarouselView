package com.ming.carousel;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 轮播图封装使用RecyclerView
 *
 * @author ming
 * @since 2020/6/18
 */
final class CarouselRecyclerView extends ScrollableRecyclerView {
    public CarouselRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CarouselRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

}
