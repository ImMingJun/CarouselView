package com.ming.carousel;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * created by cmj on 2020/3/27
 * for:轮播图页面转换
 */
public class CarouselPageTransformer implements ViewPager2.PageTransformer {
    private static final float DEFAULT_CENTER = 0.5f;
    private static final float DEFAULT_MIN_SCALE = 1.0f;
    private float mMinScale = DEFAULT_MIN_SCALE;
    private static final float DEFAULT_MIN_ALPHA = 1.0f;
    private float mMinAlpha = DEFAULT_MIN_ALPHA;
    private int mOrientation = ViewPager2.ORIENTATION_HORIZONTAL;

    public CarouselPageTransformer() {
    }

    public CarouselPageTransformer(float mMinScale, float mMinAlpha, int orientation) {
        this.mMinScale = mMinScale;
        this.mMinAlpha = mMinAlpha;
        this.mOrientation = orientation;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();
        page.setPivotY(pageHeight >> 1);
        page.setPivotX(pageWidth >> 1);
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(mMinAlpha);
            page.setScaleX(mMinScale);
            page.setScaleY(mMinScale);
            if (mOrientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                page.setPivotX(pageWidth);
            } else {
                page.setPivotY(pageHeight);
            }
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            if (position < 0) { //1-2:1[0,-1] ;2-1:1[-1,0]
                float factor = mMinAlpha + (1 - mMinAlpha) * (1 + position);
                page.setAlpha(factor);
                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                if (mOrientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    page.setPivotX(pageWidth * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));
                } else {
                    page.setPivotY(pageHeight * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));
                }
            } else { //1-2:2[1,0] ;2-1:2[0,1]
                float factor = mMinAlpha + (1 - mMinAlpha) * (1 - position);
                page.setAlpha(factor);
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                if (mOrientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    page.setPivotX(pageWidth * ((1 - position) * DEFAULT_CENTER));
                } else {
                    page.setPivotY(pageHeight * ((1 - position) * DEFAULT_CENTER));
                }
            }
        } else { // (1,+Infinity]
            page.setAlpha(mMinAlpha);
            page.setScaleX(mMinScale);
            page.setScaleY(mMinScale);
            if (mOrientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                page.setPivotX(0);
            } else {
                page.setPivotY(0);
            }
        }
    }
}
