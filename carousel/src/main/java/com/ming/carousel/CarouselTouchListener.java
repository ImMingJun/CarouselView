package com.ming.carousel;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * created by cmj on 2019-05-29
 * for:轮播图点击事件
 */
class CarouselTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetectorCompat mGestureDetector;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemDoubleClickListener mOnItemDoubleClickListener;

    CarouselTouchListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    CarouselTouchListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    CarouselTouchListener(OnItemDoubleClickListener mOnItemDoubleClickListener) {
        this.mOnItemDoubleClickListener = mOnItemDoubleClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public OnItemDoubleClickListener getOnItemDoubleClickListener() {
        return mOnItemDoubleClickListener;
    }

    void setOnItemDoubleClickListener(OnItemDoubleClickListener mOnItemDoubleClickListener) {
        this.mOnItemDoubleClickListener = mOnItemDoubleClickListener;
    }


    @Override
    public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent e) {
        if (null == mGestureDetector) {
            mGestureDetector = new GestureDetectorCompat(rv.getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (null != mOnItemClickListener && null != childView) {
                        int position = rv.getChildLayoutPosition(childView);

                        if (position != NO_POSITION && rv.getAdapter() instanceof CarouselAdapter) {
                            mOnItemClickListener.onItemClick(childView, (CarouselAdapter) rv.getAdapter(), position);
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
                            mOnItemLongClickListener.onItemLongClick(childView, (CarouselAdapter) rv.getAdapter(), position);
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
                                mOnItemDoubleClickListener.onItemDoubleClick(childView, (CarouselAdapter) rv.getAdapter(), position);
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
