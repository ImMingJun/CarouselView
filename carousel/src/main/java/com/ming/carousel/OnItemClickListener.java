package com.ming.carousel;

/**
 * created by cmj on 2019-05-29
 * for:轮播图点击事件监听
 *
 * @author ming
 */
public interface OnItemClickListener<E> {

    /**
     * 点击
     *
     * @param itemData item数据
     * @param position item位置
     */
    void onItemClick(E itemData, int position);
}
