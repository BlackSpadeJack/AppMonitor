package com.app.monitor.core.views;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.app.monitor.R;
import com.app.monitor.core.datas.DataManager;

/**
 * 网络wifi
 * 
 * @author yx
 * 
 */
public class NetManagerFloatView extends BaseFloatView {

    private TextView mOpenWifi;
    private TextView mCloseWifi;
    private TextView mOpen3G;
    private WifiManager wifiManager;

    public void init(Context context) {
        super.init(context);

        initViews(R.layout.layout_net_manager);
        mOpenWifi = bindView(R.id.item_openwifi);
        mCloseWifi = bindView(R.id.item_close_wifi);
        mOpen3G = bindView(R.id.item_open_3G);
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
        if (wifiManager == null) {
            wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        }
        mOpenWifi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (wifiManager != null) {
                    Toast.makeText(mContext, "打开wifi", Toast.LENGTH_SHORT).show();
                    wifiManager.setWifiEnabled(true);
                }
            }
        });
        mCloseWifi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (wifiManager != null) {
                    if (wifiManager != null) {
                        Toast.makeText(mContext, "关闭wifi", Toast.LENGTH_SHORT).show();
                        wifiManager.setWifiEnabled(false);
                    }
                }
            }
        });

        mOpen3G.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext, "模拟3G", Toast.LENGTH_SHORT).show();
            }
        });
        DataManager.getInstance().addRefreshDataListener(this);
    }

    @Override
    public void updateViews() {

    }

    // private void setMobileDataEnabled(Context context, boolean enabled) {
    // final String TAG = "yjl";
    // final ConnectivityManager conman =
    // (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    // Class conmanClass;
    // try {
    // conmanClass = Class.forName(conman.getClass().getName());
    // final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
    // iConnectivityManagerField.setAccessible(true);
    // final Object iConnectivityManager = iConnectivityManagerField.get(conman);
    // final Class iConnectivityManagerClass =
    // Class.forName(iConnectivityManager.getClass().getName());
    // final Method setMobileDataEnabledMethod =
    // iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",
    // Boolean.TYPE);
    // setMobileDataEnabledMethod.setAccessible(true);
    // setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    // } catch (ClassNotFoundException e) {
    // // TODO Auto-generated catch block
    // Log.d(TAG, "ClassNotFoundException");
    // } catch (NoSuchFieldException e) {
    // Log.d(TAG, "NoSuchFieldException");
    // } catch (IllegalArgumentException e) {
    // Log.d(TAG, "IllegalArgumentException");
    // } catch (IllegalAccessException e) {
    // Log.d(TAG, "IllegalAccessException");
    // } catch (NoSuchMethodException e) {
    // Log.d(TAG, "NoSuchMethodException");
    // } catch (InvocationTargetException e) {
    // Log.d(TAG, "InvocationTargetException");
    // } finally {
    //
    // }
    //
    // }
    //
    // /**
    // * 设置网络模式
    // *
    // * @param context
    // * @param mode
    // */
    // public void setPreferedNetworkType(Context context, int mode) {
    // // <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS"/>
    // Settings.Secure.putInt(context.getContentResolver(), "preferred_network_mode", mode);
    // // change mode
    // Intent intent = new Intent("com.android.phone.CHANGE_NETWORK_MODE");
    // intent.putExtra("com.android.phone.NEW_NETWORK_MODE", mode);
    // context.sendBroadcast(intent);
    // }
    //
    //
    // /**
    // * 获取当前网络模式
    // *
    // * @param context
    // * @return
    // * @throws SettingNotFoundException
    // */
    // public int getPreferedNetworkType(Context context) throws SettingNotFoundException {
    // // wifi,没有移动网 ,3G 18
    // // 4G 20
    // return Settings.Secure.getInt(context.getContentResolver(), "preferred_network_mode");
    // }
}
