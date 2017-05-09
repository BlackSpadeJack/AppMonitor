package com.app.monitor.core.views;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.app.monitor.R;
import com.app.monitor.core.datas.DataManager;
import com.app.monitor.utils.Constants;
import com.app.monitor.utils.CurrentInfo;
import com.app.monitor.utils.MemoryInfo;

/**
 * cpu,内存,流量监控
 * 
 * @author yx
 * 
 */
public class SystemInfoFloatView extends BaseFloatView {

    private MemoryInfo memoryInfo;
    private DecimalFormat fomart;
    private TextView item_cpu;
    private TextView item_fps;
    private TextView item_mem;
    private TextView itemBatt;
    private TextView itemTemperature;
    private CurrentInfo currentInfo;

    public void init(Context context) {
        super.init(context);

        initViews(R.layout.layout_app_info);
        item_cpu = bindView(R.id.item_cpu);
        item_fps = bindView(R.id.item_fps);
        item_mem = bindView(R.id.item_mem);
        itemBatt = bindView(R.id.item_battay);
        itemTemperature = bindView(R.id.item_temperatrue);
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
        memoryInfo = new MemoryInfo();
        fomart = new DecimalFormat();
        fomart.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        fomart.setGroupingUsed(false);
        fomart.setMaximumFractionDigits(2);
        fomart.setMinimumFractionDigits(0);
        currentInfo = new CurrentInfo();
        Log.e("yjl", "SystemInfoFloatView init ");
        DataManager.getInstance().addRefreshDataListener(this);
    }

    @Override
    public void updateViews() {

        // CpuInfo cpuinfo = DataManager.getInstance().getCpuInfo();
        // if (cpuinfo != null) {
        // item_fps.setText("FPS：" + FpsInfo.fps());
        // }

    }

    @Override
    public void updateDataInfo(ArrayList<String> processInfo) {
        if (processInfo != null) {
            analysisInfo(processInfo);
            itemBatt.setText("电量:" + processInfo.get(3));
            itemTemperature.setText("温度:" + processInfo.get(4));
        }
        // updateViews();
    }

    private static final String BLANK_STRING = "";

    private void analysisInfo(ArrayList<String> processInfo) {
        // long totalMemorySize = memoryInfo.getTotalMemory();
        int pidMemory = memoryInfo.getPidMemorySize(DataManager.PID, mContext);
        long freeMemory = memoryInfo.getFreeMemorySize(mContext);

        if (isFloating) {
            String processCpuRatio = "0.00";
            String totalCpuRatio = "0.00";
            String trafficSize = "0";
            long tempTraffic = 0L;
            double trafficMb = 0;
            boolean isMb = false;
            String freeMemoryKb = fomart.format((double) freeMemory / 1024);
            String processMemory = fomart.format((double) pidMemory / 1024);
            String currentBatt = String.valueOf(currentInfo.getCurrentValue());
            if (!processInfo.isEmpty()) {
                processCpuRatio = processInfo.get(0);
                totalCpuRatio = processInfo.get(1);
                trafficSize = processInfo.get(2);
                if (!(BLANK_STRING.equals(trafficSize)) && !("-1".equals(trafficSize))) {
                    tempTraffic = Long.parseLong(trafficSize);
                    if (tempTraffic > 1024) {
                        isMb = true;
                        trafficMb = (double) tempTraffic / 1024;
                    }
                }
                // 如果cpu使用率存在且都不小于0，则输出
                if (processCpuRatio != null && totalCpuRatio != null) {
                    item_cpu.setText(mContext.getString(R.string.process_free_mem) + processMemory
                            + "/" + freeMemoryKb + "MB");
                    item_mem.setText(mContext.getString(R.string.process_overall_cpu)
                            + processCpuRatio + "%/" + totalCpuRatio + "%");
                    String batt = mContext.getString(R.string.current) + currentBatt;
                    if ("-1".equals(trafficSize)) {
                        item_fps.setText(batt + Constants.COMMA
                                + mContext.getString(R.string.traffic) + Constants.NA);
                    } else if (isMb)
                        item_fps.setText(batt + Constants.COMMA
                                + mContext.getString(R.string.traffic) + fomart.format(trafficMb)
                                + "MB");
                    else
                        item_fps.setText(batt + Constants.COMMA
                                + mContext.getString(R.string.traffic) + trafficSize + "KB");

                }
            }
        }
    }
}
