package com.app.monitor.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.app.monitor.R;
import com.app.monitor.activity.DebugTabActivity;
import com.app.monitor.activity.FileBrowser;
import com.app.monitor.activity.MailSettingsActivity;
import com.app.monitor.activity.ScreenActivity;
import com.app.monitor.boommenu.Util;
import com.app.monitor.core.datas.DataManager;
import com.app.monitor.core.views.BaseFloatView;
import com.app.monitor.core.views.MainFloatView;
import com.app.monitor.core.views.MobileInfoFloatView;
import com.app.monitor.core.views.NetManagerFloatView;
import com.app.monitor.core.views.PopupMenuView;
import com.app.monitor.core.views.SystemInfoFloatView;

public class MonitorWindowManager {

    private final static String LOG_TAG = "monitor-" + MonitorWindowManager.class.getSimpleName();

    private Service mService;

    public interface OnMonitorListener {
        public void handlerStop();

        public void handlerRemove(String tag);

        public void handlerClick(int pos);
    }

    HashMap<String, BaseFloatView> floatViewMap = null;

    public MonitorWindowManager(final Service service) {
        mService = service;

        if (floatViewMap == null) {
            floatViewMap = new HashMap<String, BaseFloatView>();
        }

        addFloatView(MainFloatView.class.getSimpleName(), new OnMonitorListener() {

            @Override
            public void handlerStop() {

            }

            @Override
            public void handlerClick(int pos) {

                popupMenu(service.getApplicationContext());

            }

            @Override
            public void handlerRemove(String tag) {

                removeFloatView(tag);
            }

        });
    }


    private void addFloatView(String name, OnMonitorListener l) {
        if (floatViewMap.containsKey(name)) {
            Log.e("yjl", name + "正在显示");
            return;
        }
        Class<?> aClass;
        try {
            aClass = Class.forName("com.app.monitor.core.views." + name);
            if (aClass != null) {
                BaseFloatView floatView = (BaseFloatView) aClass.newInstance();
                floatView.init(mService);
                floatView.setOnMonitorListener(l);
                addFloatViewEx(name, floatView);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void addFloatViewEx(String tag, BaseFloatView floatView) {
        if (tag == null || floatView == null) {
            return;
        }

        if (floatViewMap.containsKey(tag)) {
            floatViewMap.remove(tag);
        }
        floatViewMap.put(tag, floatView);
    }

    public void removeFloatView(String tag) {
        if (tag == null || floatViewMap == null) {
            return;
        }
        if (floatViewMap.containsKey(tag)) {
            BaseFloatView floatView = floatViewMap.get(tag);
            if (floatView != null) {
                DataManager.getInstance().removeRefreshDataListener(floatView);
                floatView.onDestroy();
                floatView = null;
            }
            floatViewMap.remove(tag);
        }

    }

    public void removeAllFloatView() {
        if (floatViewMap == null) {
            return;
        }
        Iterator iter = floatViewMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();

            BaseFloatView floatView = (BaseFloatView) entry.getValue();
            if (floatView != null) {
                floatView.onDestroy();
            }
        }
    }

    public void onDestroy() {

        Iterator iter = floatViewMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();

            BaseFloatView floatView = (BaseFloatView) entry.getValue();
            if (floatView != null) {
                floatView.onDestroy();
            }
        }

    }


    public void updateViews() {

        Iterator iter = floatViewMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();

            BaseFloatView floatView = (BaseFloatView) entry.getValue();
            if (floatView != null) {
                floatView.updateViews();
            }
        }
    }


    private String[] Colors = {"#F44336", "#E91E63", "#9C27B0", "#2196F3", "#03A9F4", "#00BCD4",
            "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722",
            "#795548", "#9E9E9E", "#607D8B"};

    public int GetRandomColor() {
        Random random = new Random();
        int p = random.nextInt(Colors.length);
        return Color.parseColor(Colors[p]);
    }


    private void popupMenu(final Context context) {
        int number = 9;

        final Drawable[] drawables = new Drawable[number];
        int[] drawablesResource =
                new int[] {R.drawable.mark, R.drawable.refresh, R.drawable.copy, R.drawable.heart,
                        R.drawable.info, R.drawable.like, R.drawable.record, R.drawable.search,
                        R.drawable.settings};
        for (int i = 0; i < number; i++)
            drawables[i] = mService.getResources().getDrawable(drawablesResource[i]);// ContextCompat.getDrawable(mContext,
                                                                                     // drawablesResource[i]);

        String[] STRINGS =
                new String[] {"性能监控", "帧率", "网络设置", "应用和手机信息", "文件夹浏览", "查看Crash Log", "录制视频",
                        "设置邮件", "停止测试"};

        final String[] strings = new String[number];
        for (int i = 0; i < number; i++)
            strings[i] = STRINGS[i];

        final int[][] colors = new int[number][2];
        for (int i = 0; i < number; i++) {
            colors[i][1] = GetRandomColor();
            colors[i][0] = Util.getInstance().getPressedColor(colors[i][1]);
        }

        BaseFloatView floatView = floatViewMap.get(MainFloatView.class.getSimpleName());

        final PopupMenuView menu = new PopupMenuView();
        menu.init(mService);

        int position[] = new int[2];
        position[0] = floatView.getLayoutParams().x;
        position[1] = floatView.getLayoutParams().y;

        menu.setPositon(position);
        if (ScreenActivity.isRun()) {
            strings[6] = "停止录制视频";
        } else {
            strings[6] = "开始录制视频";
        }
        menu.initDatas(drawables, strings, colors);
        menu.setOnMonitorListener(new OnMonitorListener() {

            @Override
            public void handlerStop() {
                // TODO Auto-generated method stub

            }

            @Override
            public void handlerRemove(String tag) {
                // TODO Auto-generated method stub

            }

            @Override
            public void handlerClick(int pos) {
                Log.i("", "pos = " + pos);
                Intent intent = null;
                switch (pos) {
                    case 0: {
                        Log.e("yjl", "点击0");
                        // 系统信息
                        if (!floatViewMap.containsKey(SystemInfoFloatView.class.getSimpleName())) {
                            addFloatView(SystemInfoFloatView.class.getSimpleName(), null);
                        } else {
                            removeFloatView(SystemInfoFloatView.class.getSimpleName());
                        }
                        break;
                    }
                    case 1: {
                        Log.e("yjl", "点击1");
                        break;
                    }
                    case 2:
                        // wifi开关
                        Log.e("yjl", "点击2");
                        if (!floatViewMap.containsKey(NetManagerFloatView.class.getSimpleName())) {
                            addFloatView(NetManagerFloatView.class.getSimpleName(), null);
                        } else {
                            removeFloatView(NetManagerFloatView.class.getSimpleName());
                        }
                        break;
                    case 3:
                        // 手机信息
                        if (!floatViewMap.containsKey(MobileInfoFloatView.class.getSimpleName())) {
                            addFloatView(MobileInfoFloatView.class.getSimpleName(), null);
                        } else {
                            removeFloatView(MobileInfoFloatView.class.getSimpleName());
                        }
                        break;
                    case 4:
                        // 文件夹浏览
                        intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(mService, FileBrowser.class);
                        mService.startActivity(intent);
                        break;
                    case 5:
                        // debug信息，包括网络日志和crash日志
                        intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(mService, DebugTabActivity.class);
                        mService.startActivity(intent);
                        break;
                    case 6:
                        // 开启视频录制
                        if (Build.VERSION.SDK_INT < 22) {
                            Toast.makeText(mService, "录制屏幕功能需要5.0以上手机~~", Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        if (ScreenActivity.isRun()) {
                            Toast.makeText(mService, "停止屏幕录制~~", Toast.LENGTH_SHORT).show();
                            ScreenActivity.stopScreenShot();
                            return;
                        }
                        intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(mService, ScreenActivity.class);
                        mService.startActivity(intent);
                        break;
                    case 7:
                        // 发送邮箱设置
                        intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(mService, MailSettingsActivity.class);
                        mService.startActivity(intent);
                        break;
                    case 8: {
                        // 停止测试服务
                        DataManager.getInstance().removeRefreshDataListener();
                        removeAllFloatView();
                        mService.stopSelf();
                        break;
                    }
                }
            }

        });

        menu.shoot();

    }
}
