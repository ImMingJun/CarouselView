package com.ming.carousel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xinhuamm.carousel.R;

/**
 * created by cmj on 2019/2/16
 * for:轮播图指示器列表适配器
 */
public class IndicatorsAdapter extends RecyclerView.Adapter<IndicatorsAdapter.ViewHolder> {

    private int mSelectedPosition, mItemCount;
    private int mUnSelectIndicatorRes, mSelectedIndicatorRes;
    private int mUnSelectIndicatorColor, mSelectedIndicatorColor;

    IndicatorsAdapter(int itemCount, int unSelectIndicatorRes, int selectedIndicatorRes, int unSelectIndicatorColor, int selectedIndicatorColor) {
        this.mItemCount = itemCount;
        this.mUnSelectIndicatorRes = unSelectIndicatorRes;
        this.mSelectedIndicatorRes = selectedIndicatorRes;
        this.mUnSelectIndicatorColor = unSelectIndicatorColor;
        this.mSelectedIndicatorColor = selectedIndicatorColor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_carousel_indicators, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView ivIndicator = holder.ivIndicator;
        if (position == mSelectedPosition) {
            ivIndicator.setImageResource(mSelectedIndicatorRes);
            if (0 != mSelectedIndicatorColor) {
                ivIndicator.setColorFilter(mSelectedIndicatorColor);
            }
        } else {
            ivIndicator.setImageResource(mUnSelectIndicatorRes);
            if (0 != mUnSelectIndicatorColor) {
                ivIndicator.setColorFilter(mUnSelectIndicatorColor);
            }
        }
    }

    void setIndicatorsRes(int unSelectIndicatorRes, int selectedIndicatorRes) {
        this.mUnSelectIndicatorRes = unSelectIndicatorRes;
        this.mSelectedIndicatorRes = selectedIndicatorRes;
        notifyDataSetChanged();
    }

    void setIndicatorsColor(int unSelectIndicatorColor, int selectedIndicatorColor) {
        this.mUnSelectIndicatorColor = unSelectIndicatorColor;
        this.mSelectedIndicatorColor = selectedIndicatorColor;
        notifyDataSetChanged();
    }

    void setSelectedPosition(int selectedPosition) {
        if (selectedPosition < 0) return;
        if (selectedPosition > mItemCount) return;
        this.mSelectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    public void setItemCount(int itemCount) {
        this.mItemCount = itemCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            ivIndicator = itemView.findViewById(R.id.iv_carousel_indicator);
        }
    }
}
