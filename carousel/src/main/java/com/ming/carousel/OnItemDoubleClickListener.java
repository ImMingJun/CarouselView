package com.ming.carousel;

/**
 * created by cmj on 2019-05-29
 * for:轮播图双击监听
 *
 * @author ming
 */
public interface OnItemDoubleClickListener<E> {

    /**
     * 双击
     *
     * @param itemData 点击数据
     * @param position item位置
     */
    void onItemDoubleClick(E itemData, int position);
}
