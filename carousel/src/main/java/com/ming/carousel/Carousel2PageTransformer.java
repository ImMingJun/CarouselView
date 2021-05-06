package com.ming.carousel;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * created by cmj on 2020/3/27
 * for:轮播图页面转换
 * @author ming
 */
public class Carousel2PageTransformer implements ViewPager2.PageTransformer {
    private static final float DEFAULT_CENTER = 0.5f;
    private static final float DEFAULT_MIN_SCALE = 1.0f;
    private float minScale = DEFAULT_MIN_SCALE;
    private static final float DEFAULT_MIN_ALPHA = 1.0f;
    private float minAlpha = DEFAULT_MIN_ALPHA;
    private int mOrientation = ViewPager2.ORIENTATION_HORIZONTAL;

    public Carousel2PageTransformer() {
    }

    public Carousel2PageTransformer(float minScale, float minAlpha, int orientation) {
        this.minScale = minScale;
        this.minAlpha = minAlpha;
        this.mOrientation = orientation;
    }

    public void setMinAlpha(float minAlpha) {
        this.minAlpha = minAlpha;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();
        page.setPivotY(pageHeight >> 1);
        page.setPivotX(pageWidth >> 1);
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(minAlpha);
            page.setScaleX(minScale);
            page.setScaleY(minScale);
            if (mOrientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                page.setPivotX(pageWidth);
            } else {
                page.setPivotY(pageHeight);
            }
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            if (position < 0) { //1-2:1[0,-1] ;2-1:1[-1,0]
                float factor = minAlpha + (1 - minAlpha) * (1 + position);
                page.setAlpha(factor);
                float scaleFactor = (1 + position) * (1 - minScale) + minScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                if (mOrientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    page.setPivotX(pageWidth * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));
                } else {
                    page.setPivotY(pageHeight * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));
                }
            } else { //1-2:2[1,0] ;2-1:2[0,1]
                float factor = minAlpha + (1 - minAlpha) * (1 - position);
                page.setAlpha(factor);
                float scaleFactor = (1 - position) * (1 - minScale) + minScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                if (mOrientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    page.setPivotX(pageWidth * ((1 - position) * DEFAULT_CENTER));
                } else {
                    page.setPivotY(pageHeight * ((1 - position) * DEFAULT_CENTER));
                }
            }
        } else { // (1,+Infinity]
            page.setAlpha(minAlpha);
            page.setScaleX(minScale);
            page.setScaleY(minScale);
            if (mOrientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                page.setPivotX(0);
            } else {
                page.setPivotY(0);
            }
        }
    }
}
