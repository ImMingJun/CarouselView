package com.ming.carousel;

import android.view.View;

/**
 * created by cmj on 2019-05-29
 * for:
 */
public interface OnItemLongClickListener {

    void onItemLongClick(View childView, CarouselAdapter adapter, int position);
}
