package com.app.monitor.activity.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import com.app.monitor.MonitorConfig;
import com.app.monitor.core.datas.DataManager;
import com.app.monitor.utils.ServiceKeys;

/**
 * 写入crash 日志到本地文件保存
 * 
 * @author yx
 * 
 */
public class CrashLogManager {
    @SuppressLint("SimpleDateFormat")
    public static void uncaughtException(Thread t, Throwable e) {
        long currentTimeStamp = System.currentTimeMillis();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        Date currentTime = new Date(currentTimeStamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH_mm_ss");
        String dateString = formatter.format(currentTime);
        if (DataManager.getInstance().getService() != null) {
            // 如果service不为空，日志名用包名和日期
            dateString = getApplicationName(DataManager.getInstance().getService()) + dateString;
        }
        String filename = dateString + MonitorConfig.CRASH_LOG_POSTFIX;

        if (MonitorConfig.localPath != null) {
            Log.e("yjl", "日志存储路径：" + MonitorConfig.CRASH_LOG_PATH);
            writeToFile(stacktrace, filename);
        }
        if (MonitorConfig.url != null) {
            sendToServer(stacktrace, filename);
        }

    }

    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(ServiceKeys.packgeName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     * 写入日志文件
     * 
     * @param stacktrace
     * @param filename
     */
    private static void writeToFile(String stacktrace, String filename) {
        try {
            File fileDir = new File(MonitorConfig.CRASH_LOG_PATH);
            String filePath = MonitorConfig.CRASH_LOG_PATH + "/" + filename;
            if (!fileDir.exists()) {
                // 先创建文件夹
                if (fileDir.mkdirs()) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        Log.e("yjl", "创建文件：" + filename);
                        file.createNewFile();
                    }
                }
            } else {
                File file = new File(filePath);
                if (!file.exists()) {
                    Log.e("yjl", "创建文件：" + filename);
                    file.createNewFile();
                }
            }

            BufferedWriter bos = new BufferedWriter(new FileWriter(filePath));
            PrintWriter pw = new PrintWriter(bos);

            dumpPhoneInfo(pw);

            bos.write(stacktrace);
            pw.print("----------------------------------------");
            pw.println();
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 写入手机基本信息
    private static void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
        // 写入发生时间
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        pw.println(df.format(date));

        // 应用的版本名称和版本号
        if (DataManager.getInstance().getService() != null) {
            PackageManager pm = DataManager.getInstance().getService().getPackageManager();
            PackageInfo pi =
                    pm.getPackageInfo(DataManager.getInstance().getService().getPackageName(),
                            PackageManager.GET_ACTIVITIES);
            pw.print("App Version: ");
            pw.print(pi.versionName);
            pw.print('_');
            pw.println(pi.versionCode);
            pw.println();
        }


        // android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        pw.println();

        // 手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        pw.println();

        // 手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);
        pw.println();

        // cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
        pw.println();
    }

    /**
     * 预留接口，发送至服务器
     * 
     * @param stacktrace
     * @param filename
     */
    private static void sendToServer(String stacktrace, String filename) {

    }
}
