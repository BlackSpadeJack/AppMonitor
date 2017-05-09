package com.app.monitor.core.datas;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.app.monitor.R;
import com.app.monitor.activity.ScreenActivity;
import com.app.monitor.activity.SettingsActivity;
import com.app.monitor.activity.manager.CrashLogManager;
import com.app.monitor.model.DebugMessageModel;
import com.app.monitor.model.MesseageModel;
import com.app.monitor.service.MonitorService;
import com.app.monitor.utils.CpuInfo;
import com.app.monitor.utils.ProcessInfo;
import com.app.monitor.utils.Programe;
import com.app.monitor.utils.ServiceKeys;

/**
 * 对线程和数据做统一管理
 * 
 * @author yx
 * 
 */
public class DataManager {
    // 当前手机所有应用
    private List<Programe> processList;
    private ProcessInfo processInfo;
    private Intent monitorService;
    private int uid;
    private static DataManager instance;
    private CpuInfo cpuInfo;
    private ArrayList<RefreshDataListener> refreshDataList;
    public static int PID;
    public Service mService;

    private DataManager() {

    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    /**
     * 根据包名获取指定的应用
     * 
     * @return Programe
     */
    public Programe getMyPrograme(Context context, String packageName) {
        Programe Pro = null;
        try {
            if (processInfo == null) {
                processInfo = new ProcessInfo();
            }
            processList = processInfo.getAllPackages(context);
            for (Programe pro : processList) {
                Log.e("yjl", pro.getPackageName() + "----" + pro.getProcessName());
                if (pro.getPackageName().equals(packageName)) {
                    Pro = pro;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Pro;
    }

    /**
     * 开始监控service
     */
    public void startMonitorService(Context context) {
        String processName = "";
        ProcessInfo processInfo = new ProcessInfo();
        monitorService =
                context.getPackageManager().getLaunchIntentForPackage(ServiceKeys.packgeName);
        PID = processInfo.getPidByPackageName(context, ServiceKeys.packgeName);
        processName =
                processInfo.getProgrameByPackageName(context, ServiceKeys.packgeName)
                        .getProcessName();
        monitorService.setClass(context, MonitorService.class);
        monitorService.putExtra("processName", processName);
        monitorService.putExtra("pid", PID);
        monitorService.putExtra("uid", uid);
        monitorService.putExtra("packageName", ServiceKeys.packgeName);
        Log.e("yjl", "PID:" + PID + "----uid:" + uid + "-----packageName:" + ServiceKeys.packgeName
                + "----processName:" + processName);
        Log.e("yjl", "startMonitorService");
        context.startService(monitorService);
    }

    /**
     * 停止监控service
     * 
     * @param context
     */
    public void stopMonitorService(Context context) {
        Toast.makeText(context,
                context.getString(R.string.test_result_file_toast) + MonitorService.resultFilePath,
                Toast.LENGTH_LONG).show();
        Log.e("yjl", "停止测试!");
        context.stopService(monitorService);
    }

    /**
     * 打开设置页面
     * 
     * @param context
     */
    public void goToSettingsActivity(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, SettingsActivity.class);
        context.startActivity(intent);
    }


    public CpuInfo getCpuInfo() {
        return cpuInfo;
    }


    public void addRefreshDataListener(RefreshDataListener listener) {
        if (refreshDataList == null) {
            refreshDataList = new ArrayList<RefreshDataListener>();
        }
        if (listener != null) {
            refreshDataList.add(listener);
        }
    }

    public void updateView(ArrayList<String> processInfo) {
        if (refreshDataList != null) {
            for (RefreshDataListener listener : refreshDataList) {
                listener.updateDataInfo(processInfo);
            }
        }
    }


    public void removeRefreshDataListener() {
        if (refreshDataList != null) {
            refreshDataList.clear();
        }
    }

    public void removeRefreshDataListener(RefreshDataListener listener) {
        if (refreshDataList != null) {
            refreshDataList.remove(listener);
        }
    }

    /**
     * 将对应的crash 日志写入设置的路径中
     * 
     * @param t
     * @param e
     */
    public void writeCrashLog(Thread t, Throwable e) {
        CrashLogManager.uncaughtException(t, e);
    }

    public void setService(Service service) {
        this.mService = service;
    }

    public Service getService() {
        return mService;
    }

    public void addNetWorkMesseage(String mUrl, HashMap<String, String> mParams, String mResult,
            String mStatus) {
        String mParam = null;
        if (mParams != null && mParams.size() > 0) {
            StringBuilder sb = new StringBuilder(mUrl);
            sb.append("?");
            Iterator iter = mParams.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();

                sb.append(entry.getKey());
                sb.append("=");
                try {
                    sb.append(URLEncoder.encode(entry.getValue().toString(), "GBK"));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                sb.append("&");
            }

            sb = sb.deleteCharAt(sb.length() - 1);
            mParam = sb.toString();
        }
        addNetWorkMesseage(mUrl, mParam, mResult, mStatus);
    }

    public void addNetWorkMesseage(String mUrl, String mParams, String mResult, String mStatus) {
        MesseageModel messeage = new MesseageModel();
        messeage.mUrl = mUrl;
        messeage.mParams = mParams;
        messeage.mResult = mResult;
        messeage.mStatus = mStatus;
        DebugMessageModel.addMessage(messeage);
    }
}
