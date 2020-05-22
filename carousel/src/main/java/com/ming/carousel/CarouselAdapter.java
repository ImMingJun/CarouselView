package com.ming.carousel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * created by cmj on 2019/2/1
 * for:轮播图适配器
 */
public class CarouselAdapter<E> extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
    public static final int MULTIPLE_COUNT = 1000;
    private boolean isInfinite;
    private List<E> mData;
    private CarouselViewCreator<E> mCarouselViewCreator;

    CarouselAdapter(CarouselViewCreator<E> mCarouselViewCreator, List<E> data) {
        this(mCarouselViewCreator, data, false);
    }

    CarouselAdapter(CarouselViewCreator<E> mCarouselViewCreator, List<E> data, boolean isInfinite) {
        this.mCarouselViewCreator = mCarouselViewCreator;
        this.mData = data;
        this.isInfinite = isInfinite;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CarouselViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(mCarouselViewCreator.layoutId(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselAdapter.CarouselViewHolder holder, int position) {
        position = isInfinite ? toRealItemPosition(position) : position;
        mCarouselViewCreator.convert(holder.itemView, mData.get(position));
    }

    public int toRealItemPosition(int position) {
        int realCount = getRealItemCount();
        if (realCount == 0)
            return 0;
        return position % realCount;
    }

    @Override
    public int getItemCount() {
        return isInfinite ? getRealItemCount() * MULTIPLE_COUNT : getRealItemCount();
    }

    public int getRealItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public int getMiddlePosition() {
        return MULTIPLE_COUNT * getRealItemCount() >> 1;
    }

    public CarouselViewCreator<E> getCarouselViewCreator() {
        return mCarouselViewCreator;
    }

    @NonNull
    public List<E> getData() {
        return mData;
    }

    @Nullable
    public E getItem(@IntRange(from = 0) int position) {
        if (position >= 0 && position < mData.size())
            return mData.get(position);
        else
            return null;
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {

        CarouselViewHolder(View itemView) {
            super(itemView);
        }
    }
}
