package com.app.monitor.core.views;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.app.monitor.R;

public class MainFloatView extends BaseFloatView {


    public void init(Context context) {
        super.init(context);

        initViews(R.layout.layout_monitor_floatview);
        View item_test = rootView.findViewById(R.id.item_test);

        if (isFloating) {
            item_test.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    x = event.getRawX();
                    y = event.getRawY() - getStatusBarHeight();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            mTouchStartX = event.getX();
                            mTouchStartY = event.getY();
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            wmParams.x = (int) (x - mTouchStartX);
                            updateViewPosition();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            if (Math.abs(event.getX() - mTouchStartX) > 5
                                    || Math.abs(event.getY() - mTouchStartY) > 5) {
                                wmParams.x = (int) (x - mTouchStartX);

                                if (wmParams.x > windowManager.getDefaultDisplay().getWidth() / 2
                                        && wmParams.x != (windowManager.getDefaultDisplay()
                                                .getWidth() - rootView.getWidth())) {
                                    wmParams.x =
                                            windowManager.getDefaultDisplay().getWidth()
                                                    - rootView.getWidth();
                                } else if (wmParams.x > 0
                                        && wmParams.x < windowManager.getDefaultDisplay()
                                                .getWidth() / 2) {
                                    wmParams.x = 0;
                                }
                                updateViewPosition();
                                mTouchStartX = mTouchStartY = 0;
                            } else {
                                if (mOnMonitorListener != null
                                        && Math.abs(event.getX() - mTouchStartX) <= 5
                                        && Math.abs(event.getY() - mTouchStartY) <= 5) {
                                    mOnMonitorListener.handlerClick(0);
                                }
                            }

                            break;
                        }
                    }
                    return true;
                }
            });
        }

    }

    @Override
    public void updateViews() {
        // TODO Auto-generated method stub

    }

}
