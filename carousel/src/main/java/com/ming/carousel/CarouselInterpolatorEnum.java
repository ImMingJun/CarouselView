package com.ming.carousel;

/**
 * 轮播图插值器枚举
 *
 * @author ming
 * @since 2020/6/15
 */
public interface CarouselInterpolatorEnum {
    int DEFAULT = -1;
    int LINEAR_INTERPOLATOR = 1;
    int DECELERATE_INTERPOLATOR = 2;
    int ACCELERATE_INTERPOLATOR = 3;
    int ACCELERATE_DECELERATE_INTERPOLATOR = 4;
    int ANTICIPATE_INTERPOLATOR = 5;
    int ANTICIPATE_OVERSHOOT_INTERPOLATOR = 6;
    int BOUNCE_INTERPOLATOR = 7;
    int CYCLE_INTERPOLATOR = 8;
    int OVERSHOOT_INTERPOLATOR = 9;
}
