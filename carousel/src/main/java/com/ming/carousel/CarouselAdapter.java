package com.ming.carousel;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * created by cmj on 2019/2/1
 * for:轮播图适配器
 *
 * @author ming
 */
public class CarouselAdapter<E> extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
    public static final int MULTIPLE_COUNT = 1000;
    private boolean isInfinite;
    private List<E> mData;
    private CarouselViewCreator<E> mCarouselViewCreator;
    private OnItemClickListener<E> onItemClickListener;
    private OnItemLongClickListener<E> onItemLongClickListener;

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
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(mCarouselViewCreator.layoutId(), parent, false);
        return new CarouselViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        position = isInfinite ? getRealItemPosition(position) : position;
        mCarouselViewCreator.convert(holder.itemView, mData.get(position));
        bindClickListener(holder.itemView, position);
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
    public int getItemCount() {
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
        this.mCarouselViewCreator = carouselViewCreator;
    }

    public CarouselViewCreator<E> getCarouselViewCreator() {
        return mCarouselViewCreator;
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

    static class CarouselViewHolder extends RecyclerView.ViewHolder {

        CarouselViewHolder(View itemView) {
            super(itemView);
        }
    }
}
