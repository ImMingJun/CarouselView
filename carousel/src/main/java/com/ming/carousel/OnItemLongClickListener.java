package com.ming.carousel;

/**
 * created by cmj on 2019-05-29
 * for:轮播图长按事件
 * @author ming
 */
public interface OnItemLongClickListener<E> {

    /**
     * 长按
     * @param itemData 长按数据
     * @param position item位置
     */
    void onItemLongClick(E itemData, int position);
}
