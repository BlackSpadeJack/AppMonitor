package com.app.monitor.core.views;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.app.monitor.core.MonitorWindowManager.OnMonitorListener;
import com.app.monitor.core.datas.RefreshDataListener;

public abstract class BaseFloatView implements RefreshDataListener {

    protected float mTouchStartX;
    protected float mTouchStartY;
    protected float x;
    protected float y;

    protected boolean isFloating = true;

    protected WindowManager windowManager = null;
    protected WindowManager.LayoutParams wmParams = null;

    protected OnMonitorListener mOnMonitorListener;

    protected Context mContext;

    protected View rootView;


    protected <T extends View> T bindView(int nResId) {
        return (T) rootView.findViewById(nResId);
    }

    public void updateViews() {}

    public void init(Context context) {
        mContext = context;

        windowManager = (WindowManager) context.getApplicationContext().getSystemService("window");
    }


    int mPositon[] = null;

    public void setPositon(int positon[]) {
        mPositon = positon;
    }


    public void setIsFloat(boolean isFloat) {
        isFloating = isFloat;
    }

    public void setOnMonitorListener(OnMonitorListener listener) {
        mOnMonitorListener = listener;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return wmParams;
    }

    public void initViews(int layout) {

        rootView = LayoutInflater.from(mContext).inflate(layout, null);

        wmParams = new WindowManager.LayoutParams();
        wmParams.type = 2002;
        wmParams.flags |= 8;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;
        windowManager.addView(rootView, wmParams);

    }


    public void updateViewPosition() {
        wmParams.y = (int) (y - mTouchStartY);
        if (rootView != null) {
            windowManager.updateViewLayout(rootView, wmParams);
        }
    }


    public void onDestroy() {
        Log.i("", this.getClass().getSimpleName() + ": onDestroy");
        isFloating = false;
        if (windowManager != null) {
            windowManager.removeView(rootView);
            rootView = null;
            windowManager = null;
            mContext = null;
            wmParams = null;
            mOnMonitorListener = null;
        }
    }

    public int getStatusBarHeight() {
        // set status bar height to 25
        int barHeight = 25;
        int resourceId =
                mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            barHeight = mContext.getResources().getDimensionPixelSize(resourceId);
        }

        return barHeight;
    }

    @Override
    public void updateDataInfo(ArrayList<String> processInfo) {

    }

    public boolean getIsFloating() {
        return isFloating;
    }
}
