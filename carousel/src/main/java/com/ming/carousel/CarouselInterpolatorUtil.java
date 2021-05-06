package com.ming.carousel;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * @author ming
 * @date 2020/9/17
 * for:轮播图插值器工具类
 */
public class CarouselInterpolatorUtil {

    /**
     * 获取轮播图插值器
     *
     * @param interpolatorEnum 枚举
     * @return Interpolator, 默认插值器来自 {@link androidx.recyclerview.widget.RecyclerView}
     */
    public static Interpolator getCarouselInterpolator(int interpolatorEnum) {
        switch (interpolatorEnum) {
            case CarouselInterpolatorEnum.LINEAR_INTERPOLATOR:
                return new LinearInterpolator();
            case CarouselInterpolatorEnum.DECELERATE_INTERPOLATOR:
                return new DecelerateInterpolator();
            case CarouselInterpolatorEnum.ACCELERATE_INTERPOLATOR:
                return new AccelerateInterpolator();
            case CarouselInterpolatorEnum.ACCELERATE_DECELERATE_INTERPOLATOR:
                return new AccelerateDecelerateInterpolator();
            case CarouselInterpolatorEnum.ANTICIPATE_INTERPOLATOR:
                return new AnticipateInterpolator();
            case CarouselInterpolatorEnum.ANTICIPATE_OVERSHOOT_INTERPOLATOR:
                return new AnticipateOvershootInterpolator();
            case CarouselInterpolatorEnum.BOUNCE_INTERPOLATOR:
                return new BounceInterpolator();
            case CarouselInterpolatorEnum.CYCLE_INTERPOLATOR:
                return new CycleInterpolator(1);
            case CarouselInterpolatorEnum.OVERSHOOT_INTERPOLATOR:
                return new OvershootInterpolator();
            case CarouselInterpolatorEnum.DEFAULT:
            default:
                return t -> {
                    t -= 1.0f;
                    return t * t * t * t * t + 1.0f;
                };
        }
    }
}
