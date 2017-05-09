package com.app.monitor.core.views;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.app.monitor.R;
import com.app.monitor.core.datas.DataManager;

/**
 * 手机基本信息
 * 
 * @author yx
 * 
 */
public class MobileInfoFloatView extends BaseFloatView {
    private TextView itemMonbile;
    private TextView itemSystem;
    private TextView itemOs;
    private TextView itemBrand;

    public void init(Context context) {
        super.init(context);

        initViews(R.layout.layout_mobile_info);
        itemMonbile = bindView(R.id.item_mobile);
        itemSystem = bindView(R.id.item_system);
        itemOs = bindView(R.id.item_os);
        itemBrand = bindView(R.id.item_brand);
        if (isFloating) {
            rootView.setOnTouchListener(new OnTouchListener() {
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

                            if (Math.abs(event.getX() - mTouchStartX) > 2
                                    || Math.abs(event.getY() - mTouchStartY) > 2) {
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

                                if (mOnMonitorListener != null) {
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
        itemMonbile.setText("手机系统：" + Build.MODEL);
        itemSystem.setText("系统号:" + Build.VERSION.SDK_INT);
        itemOs.setText("系统版本:" + Build.VERSION.RELEASE);
        itemBrand.setText("厂商:" + Build.BRAND);
        DataManager.getInstance().addRefreshDataListener(this);
    }


}
