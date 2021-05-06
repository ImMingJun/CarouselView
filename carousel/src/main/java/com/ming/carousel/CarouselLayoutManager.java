package com.ming.carousel;

import android.content.Context;
import android.view.View;

import com.ming.carousel.layoutmanager.ViewPagerLayoutManager;

/**
 * created by 陈明军 on 2019/1/25
 * for:轮播布局管理器
 */
public class CarouselLayoutManager extends ViewPagerLayoutManager {

    public static final int LEFT_ON_TOP = 1;
    public static final int RIGHT_ON_TOP = 2;
    public static final int CENTER_ON_TOP = 3;

    private float itemSpace;//item间距
    private float minScale;//大小比例
    private float moveSpeed;//滑动速度
    private float maxAlpha;//中间的透明度
    private float minAlpha;//两侧的透明度
    private int zAlignment;

    public CarouselLayoutManager(Context context, int itemSpace) {
        this(new Builder(context, itemSpace));
    }

    public CarouselLayoutManager(Context context, int itemSpace, int orientation) {
        this(new Builder(context, itemSpace).setOrientation(orientation));
    }

    public CarouselLayoutManager(Context context, int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(context, itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public CarouselLayoutManager(Builder builder) {
        this(builder.context, builder.itemSpace, builder.minScale, builder.maxAlpha, builder.minAlpha,
                builder.orientation, builder.moveSpeed, builder.maxVisibleItemCount, builder.distanceToBottom,
                builder.reverseLayout, builder.zAlignment);
    }

    private CarouselLayoutManager(Context context, float itemSpace, float minScale, float maxAlpha, float minAlpha,
                                  int orientation, float moveSpeed, int maxVisibleItemCount, int distanceToBottom,
                                  boolean reverseLayout, int zAlignment) {
        super(context, orientation, reverseLayout);
        setEnableBringCenterToFront(true);
        setDistanceToBottom(distanceToBottom);
        setMaxVisibleItemCount(maxVisibleItemCount);
        this.itemSpace = itemSpace;
        this.minScale = minScale;
        this.moveSpeed = moveSpeed;
        this.maxAlpha = maxAlpha;
        this.minAlpha = minAlpha;
        this.zAlignment = zAlignment;
    }

    public float getItemSpace() {
        return itemSpace;
    }

    public float getMinScale() {
        return minScale;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public float getMaxAlpha() {
        return maxAlpha;
    }

    public float getMinAlpha() {
        return minAlpha;
    }

    public void setItemSpace(float itemSpace) {
        assertNotInLayoutOrScroll(null);
        if (this.itemSpace == itemSpace) {
            return;
        }
        this.itemSpace = itemSpace;
        removeAllViews();
    }

    public void setMinScale(float minScale) {
        assertNotInLayoutOrScroll(null);
        if (this.minScale == minScale) {
            return;
        }
        this.minScale = minScale;
        removeAllViews();
    }

    public void setMaxAlpha(float maxAlpha) {
        assertNotInLayoutOrScroll(null);
        if (maxAlpha > 1) {
            maxAlpha = 1;
        }
        if (this.maxAlpha == maxAlpha) {
            return;
        }
        this.maxAlpha = maxAlpha;
        requestLayout();
    }

    public void setMinAlpha(float minAlpha) {
        assertNotInLayoutOrScroll(null);
        if (minAlpha < 0) {
            minAlpha = 0;
        }
        if (this.minAlpha == minAlpha) {
            return;
        }
        this.minAlpha = minAlpha;
        requestLayout();
    }

    public void setMoveSpeed(float moveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.moveSpeed == moveSpeed) {
            return;
        }
        this.moveSpeed = moveSpeed;
    }

    public void setZAlignment(int zAlignment) {
        assertNotInLayoutOrScroll(null);
        assertZAlignmentState(zAlignment);
        if (this.zAlignment == zAlignment) {
            return;
        }
        this.zAlignment = zAlignment;
        requestLayout();
    }

    @Override
    protected float setInterval() {
        return itemSpace + mDecoratedMeasurement;
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        float scale = calculateScale(targetOffset + mSpaceMain);
        if (Float.isNaN(scale)) {
            return;
        }
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
        final float alpha = calAlpha(targetOffset);
        itemView.setAlpha(alpha);
    }

    /**
     * @param x start positon of the view you want scale
     * @return the scale rate of current scroll mOffset
     */
    private float calculateScale(float x) {
        float deltaX = Math.abs(x - mSpaceMain);
        if (deltaX - mDecoratedMeasurement > 0) {
            deltaX = mDecoratedMeasurement;
        }
        return 1f - deltaX / mDecoratedMeasurement * (1f - minScale);
    }

    private float calAlpha(float targetOffset) {
        final float offset = Math.abs(targetOffset);
        float alpha = (minAlpha - maxAlpha) / mInterval * offset + maxAlpha;
        if (offset >= mInterval) {
            alpha = minAlpha;
        }
        return alpha;
    }

    @Override
    protected float getDistanceRatio() {
        if (moveSpeed == 0) {
            return Float.MAX_VALUE;
        }
        return 1 / moveSpeed;
    }

    /**
     * 设置View绘画的高度
     * 可以设置zAlignment来使不同位置的View后绘画（后绘画的View显示在最上面）
     *
     * @param itemView     指定位置的View
     * @param targetOffset 偏移量
     * @return 用于判断View绘画的高度
     */
    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        switch (zAlignment) {
            case LEFT_ON_TOP:
                return (540 - targetOffset) / 72;
            case RIGHT_ON_TOP:
                return (targetOffset - 540) / 72;
            default:
                return (360 - Math.abs(targetOffset)) / 72;
        }
    }

    private static void assertZAlignmentState(int zAlignment) {
        if (zAlignment != LEFT_ON_TOP && zAlignment != RIGHT_ON_TOP && zAlignment != CENTER_ON_TOP) {
            throw new IllegalArgumentException("zAlignment must be one of LEFT_ON_TOP, RIGHT_ON_TOP and CENTER_ON_TOP");
        }
    }

    public static Builder Builder(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private static final float SCALE_RATE = 0.8f;
        private static final float DEFAULT_SPEED = 1f;
        private static float MIN_ALPHA = 1f;
        private static float MAX_ALPHA = 1f;

        private float itemSpace;
        private int orientation;
        private float minScale;
        private float moveSpeed;
        private float maxAlpha;
        private float minAlpha;
        private boolean reverseLayout;
        private Context context;
        private int maxVisibleItemCount;
        private int distanceToBottom;
        private int zAlignment;

        public Builder(Context context) {
            this(context, 0);
        }

        public Builder(Context context, int itemSpace) {
            this.itemSpace = itemSpace;
            this.context = context;
            orientation = ViewPagerLayoutManager.HORIZONTAL;
            minScale = SCALE_RATE;
            this.moveSpeed = DEFAULT_SPEED;
            maxAlpha = MAX_ALPHA;
            minAlpha = MIN_ALPHA;
            reverseLayout = false;
            distanceToBottom = ViewPagerLayoutManager.INVALID_SIZE;
            maxVisibleItemCount = ViewPagerLayoutManager.DETERMINE_BY_MAX_AND_MIN;
            zAlignment = CENTER_ON_TOP;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setReverseLayout(boolean reverseLayout) {
            this.reverseLayout = reverseLayout;
            return this;
        }

        public Builder setItemSpace(float itemSpace) {
            this.itemSpace = itemSpace;
            return this;
        }

        public Builder setMinScale(float minScale) {
            this.minScale = minScale;
            return this;
        }

        public Builder setMaxAlpha(float maxAlpha) {
            if (maxAlpha > 1) {
                maxAlpha = 1;
            }
            this.maxAlpha = maxAlpha;
            return this;
        }

        public Builder setMinAlpha(float minAlpha) {
            if (minAlpha < 0) {
                minAlpha = 0;
            }
            this.minAlpha = minAlpha;
            return this;
        }

        public Builder setMoveSpeed(float moveSpeed) {
            this.moveSpeed = moveSpeed;
            return this;
        }

        public Builder setZAlignment(int zAlignment) {
            assertZAlignmentState(zAlignment);
            this.zAlignment = zAlignment;
            return this;
        }

        public Builder setMaxVisibleItemCount(int maxVisibleItemCount) {
            this.maxVisibleItemCount = maxVisibleItemCount;
            return this;
        }

        /**
         * 设置距离底部距离，使列表往上移动
         * 比较鸡肋
         *
         * @param distanceToBottom 距离
         */
        @Deprecated
        public Builder setDistanceToBottom(int distanceToBottom) {
            this.distanceToBottom = distanceToBottom;
            return this;
        }

        public CarouselLayoutManager build() {
            return new CarouselLayoutManager(this);
        }
    }
}