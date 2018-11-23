package uk.whitecrescent.waqti.android.customview;

import android.content.Context;
import android.os.Handler;

class AutoScroller {
    enum AutoScrollMode {
        POSITION, COLUMN
    }

    enum ScrollDirection {
        UP, DOWN, LEFT, RIGHT
    }

    interface AutoScrollListener {
        void onAutoScrollPositionBy(int dx, int dy);

        void onAutoScrollColumnBy(int columns);
    }

    private static final int SCROLL_SPEED_DP = 8;
    private static final int AUTO_SCROLL_UPDATE_DELAY = 12;
    private static final int COLUMN_SCROLL_UPDATE_DELAY = 1000;

    private Handler mHandler = new Handler();
    private AutoScrollListener mListener;
    private boolean mIsAutoScrolling;
    private int mScrollSpeed;
    private long mLastScrollTime;
    private AutoScrollMode mAutoScrollMode = AutoScrollMode.POSITION;

    AutoScroller(Context context, AutoScrollListener listener) {
        mListener = listener;
        mScrollSpeed = (int) (context.getResources().getDisplayMetrics().density * SCROLL_SPEED_DP);
    }

    void setAutoScrollMode(AutoScrollMode autoScrollMode) {
        mAutoScrollMode = autoScrollMode;
    }

    boolean isAutoScrolling() {
        return mIsAutoScrolling;
    }

    void stopAutoScroll() {
        mIsAutoScrolling = false;
    }

    void startAutoScroll(ScrollDirection direction) {
        switch (direction) {
            case UP:
                startAutoScrollPositionBy(0, mScrollSpeed);
                break;
            case DOWN:
                startAutoScrollPositionBy(0, -mScrollSpeed);
                break;
            case LEFT:
                if (mAutoScrollMode == AutoScrollMode.POSITION) {
                    startAutoScrollPositionBy(mScrollSpeed, 0);
                } else {
                    startAutoScrollColumnBy(1);
                }
                break;
            case RIGHT:
                if (mAutoScrollMode == AutoScrollMode.POSITION) {
                    startAutoScrollPositionBy(-mScrollSpeed, 0);
                } else {
                    startAutoScrollColumnBy(-1);
                }
                break;
        }
    }

    private void startAutoScrollPositionBy(int dx, int dy) {
        if (!mIsAutoScrolling) {
            mIsAutoScrolling = true;
            autoScrollPositionBy(dx, dy);
        }
    }

    private void autoScrollPositionBy(final int dx, final int dy) {
        if (mIsAutoScrolling) {
            mListener.onAutoScrollPositionBy(dx, dy);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    autoScrollPositionBy(dx, dy);
                }
            }, AUTO_SCROLL_UPDATE_DELAY);
        }
    }

    private void startAutoScrollColumnBy(int columns) {
        if (!mIsAutoScrolling) {
            mIsAutoScrolling = true;
            autoScrollColumnBy(columns);
        }
    }

    private void autoScrollColumnBy(final int columns) {
        if (mIsAutoScrolling) {
            if (System.currentTimeMillis() - mLastScrollTime > COLUMN_SCROLL_UPDATE_DELAY) {
                mListener.onAutoScrollColumnBy(columns);
                mLastScrollTime = System.currentTimeMillis();
            } else {
                mListener.onAutoScrollColumnBy(0);
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    autoScrollColumnBy(columns);
                }
            }, AUTO_SCROLL_UPDATE_DELAY);
        }
    }
}
