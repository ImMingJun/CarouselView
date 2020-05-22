package com.ming.carousel.layoutmanager;

import androidx.recyclerview.widget.RecyclerView;

/**
 * The implementation will snap the center of the target child view to the center of
 * the attached {@link RecyclerView}. And per Child per fling.
 * changed by 陈明军 on 2019-02-15 16:19:17
 * 优化滑动切换算法
 */
public class PageSnapHelper extends CenterSnapHelper {

    private static final int MIN_VELOCITY = 2000;//最小速度

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        ViewPagerLayoutManager layoutManager = (ViewPagerLayoutManager) mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }

        if (!layoutManager.getInfinite() &&
                (layoutManager.mOffset == layoutManager.getMaxOffset()
                        || layoutManager.mOffset == layoutManager.getMinOffset())) {
            return false;
        }

        final int minFlingVelocity = mRecyclerView.getMinFlingVelocity();

        if (layoutManager.mOrientation == ViewPagerLayoutManager.VERTICAL &&
                Math.abs(velocityY) > minFlingVelocity) {
            final int currentPosition = layoutManager.getCurrentPositionOffset();
            int finalPosition = 0;
            if (mStartPosition == currentPosition) {
                int offsetPosition = Math.abs(velocityY) > MIN_VELOCITY ? velocityY > 0 ? 1 : -1 : 0;
                finalPosition = layoutManager.getReverseLayout() ? -mStartPosition - offsetPosition : mStartPosition + offsetPosition;
            } else {
                if (Math.abs(velocityY) > MIN_VELOCITY) {
                    int offsetPosition = velocityY > 0 ? 1 : -1;
                    finalPosition = layoutManager.getReverseLayout() ? -mStartPosition - offsetPosition : mStartPosition + offsetPosition;
                } else {
                    finalPosition = layoutManager.getReverseLayout() ? -currentPosition : currentPosition;
                }
            }
            ScrollHelper.smoothScrollToPosition(mRecyclerView, layoutManager, finalPosition);
            return true;
        } else if (layoutManager.mOrientation == ViewPagerLayoutManager.HORIZONTAL &&
                Math.abs(velocityX) > minFlingVelocity) {
            final int currentPosition = layoutManager.getCurrentPositionOffset();//惯性滑动开始时的当前位置
            int finalPosition = 0;//最终需要滑动到的位置
            if (mStartPosition == currentPosition) {//若开始滑动时的位置和惯性滑动开始时的位置一致，解决短促滑动切换的效果
                int offsetPosition = Math.abs(velocityX) > MIN_VELOCITY ? velocityX > 0 ? 1 : -1 : 0;
                finalPosition = layoutManager.getReverseLayout() ? -mStartPosition - offsetPosition : mStartPosition + offsetPosition;
            } else {//若开始滑动时的位置和惯性滑动开始时的位置不一致
                if (Math.abs(velocityX) > MIN_VELOCITY) {//滑动速度大于一定值，则值切换一个位置
                    int offsetPosition = velocityX > 0 ? 1 : -1;
                    finalPosition = layoutManager.getReverseLayout() ? -mStartPosition - offsetPosition : mStartPosition + offsetPosition;
                } else {//滑动速度小于一定值，则滑动到什么位置就停留在什么位置
                    finalPosition = layoutManager.getReverseLayout() ? -currentPosition : currentPosition;
                }
            }
            ScrollHelper.smoothScrollToPosition(mRecyclerView, layoutManager, finalPosition);
            return true;
        }

        return true;
    }
}
