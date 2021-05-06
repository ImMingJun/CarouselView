package com.ming.carousel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

/**
 * 已{@link ViewPager}和{@link VerticalViewPager}实现的轮播组件数据适配器
 *
 * @author ming
 * @since 2020/6/11
 */
public class CarouselPagerAdapter<E> extends PagerAdapter {

    public static final int MULTIPLE_COUNT = 1000;
    private boolean isInfinite;
    private List<E> mData;
    private CarouselViewCreator<E> carouselViewCreator;
    private OnItemClickListener<E> onItemClickListener;
    private OnItemLongClickListener<E> onItemLongClickListener;

    CarouselPagerAdapter(CarouselViewCreator<E> carouselViewCreator, List<E> data, boolean isInfinite) {
        this.carouselViewCreator = carouselViewCreator;
        this.mData = data;
        this.isInfinite = isInfinite;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).
                inflate(carouselViewCreator.layoutId(), null, false);
        position = isInfinite ? getRealItemPosition(position) : position;
        carouselViewCreator.convert(view, mData.get(position));
        bindClickListener(view, position);
        container.addView(view);
        return view;
    }

    private void bindClickListener(View itemView, int position) {
        itemView.setOnClickListener(v -> {
            if (null != onItemClickListener) {
                onItemClickListener.onItemClick(getItem(position), position);
            }
        });
        itemView.setOnLongClickListener(v -> {
            if (null != onItemLongClickListener) {
                onItemLongClickListener.onItemLongClick(getItem(position), position);
            }
            return true;
        });
    }

    @Override
    public int getCount() {
        return isInfinite ? getRealItemCount() * MULTIPLE_COUNT : getRealItemCount();
    }

    public int getRealItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public int getMiddlePosition() {
        return MULTIPLE_COUNT * getRealItemCount() >> 1;
    }

    /**
     * 获取真实item位置
     *
     * @param position 假位置
     * @return 真实位置
     */
    public int getRealItemPosition(int position) {
        int realCount = getRealItemCount();
        if (realCount == 0) {
            return 0;
        }
        return position % realCount;
    }

    public void setOnItemClickListener(OnItemClickListener<E> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<E> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setCarouselViewCreator(CarouselViewCreator<E> carouselViewCreator) {
        this.carouselViewCreator = carouselViewCreator;
    }

    public CarouselViewCreator<E> getCarouselViewCreator() {
        return carouselViewCreator;
    }

    public void setData(List<E> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    public List<E> getData() {
        return mData;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public void setInfinite(boolean infinite) {
        isInfinite = infinite;
    }

    @Nullable
    public E getItem(@IntRange(from = 0) int position) {
        if (position >= 0 && position < mData.size()) {
            return mData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

}
