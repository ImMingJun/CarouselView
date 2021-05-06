package com.ming.carousel;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * created by cmj on 2019-05-29
 * for:轮播图点击事件
 */
class CarouselTouchListener<E> implements RecyclerView.OnItemTouchListener {

    private GestureDetectorCompat mGestureDetector;

    private OnItemClickListener<E> mOnItemClickListener;
    private OnItemLongClickListener<E> mOnItemLongClickListener;
    private OnItemDoubleClickListener<E> mOnItemDoubleClickListener;

    CarouselTouchListener(OnItemClickListener<E> mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    CarouselTouchListener(OnItemLongClickListener<E> mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    CarouselTouchListener(OnItemDoubleClickListener<E> mOnItemDoubleClickListener) {
        this.mOnItemDoubleClickListener = mOnItemDoubleClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    void setOnItemClickListener(OnItemClickListener<E> mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    void setOnItemLongClickListener(OnItemLongClickListener<E> mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public OnItemDoubleClickListener getOnItemDoubleClickListener() {
        return mOnItemDoubleClickListener;
    }

    void setOnItemDoubleClickListener(OnItemDoubleClickListener<E> mOnItemDoubleClickListener) {
        this.mOnItemDoubleClickListener = mOnItemDoubleClickListener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (null == mGestureDetector) {
            mGestureDetector = new GestureDetectorCompat(rv.getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (null != mOnItemClickListener && null != childView) {
                        int position = rv.getChildLayoutPosition(childView);

                        if (position != NO_POSITION && rv.getAdapter() instanceof CarouselAdapter) {
                            CarouselAdapter<E> adapter = (CarouselAdapter<E>) rv.getAdapter();
                            mOnItemClickListener.onItemClick(adapter.getItem(position), position);
                            return true;
                        }
                    }
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (null != mOnItemLongClickListener && null != childView) {
                        int position = rv.getChildLayoutPosition(childView);
                        if (position != NO_POSITION && rv.getAdapter() instanceof CarouselAdapter) {
                            CarouselAdapter<E> adapter = (CarouselAdapter<E>) rv.getAdapter();
                            mOnItemLongClickListener.onItemLongClick(adapter.getItem(position), position);
                        }
                    }
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    if (e.getAction() == MotionEvent.ACTION_UP) {
                        View childView = rv.findChildViewUnder(e.getX(), e.getY());
                        if (null != mOnItemDoubleClickListener && null != childView) {
                            int position = rv.getChildLayoutPosition(childView);
                            if (position != NO_POSITION && rv.getAdapter() instanceof CarouselAdapter) {
                                CarouselAdapter<E> adapter = (CarouselAdapter<E>) rv.getAdapter();
                                mOnItemDoubleClickListener.onItemDoubleClick(adapter.getItem(position), position);
                            }
                        }
                    }
                    return super.onDoubleTapEvent(e);
                }

            });
        }
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
