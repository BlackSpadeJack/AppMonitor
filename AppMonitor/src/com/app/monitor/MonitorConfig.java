package com.app.monitor;

import java.io.File;

import android.os.Environment;

public class MonitorConfig {
    public static String url; // 发送服务器的url
    // 默认本地的文件存储地址
    // public static String localPath = Environment.getExternalStorageDirectory().getAbsolutePath()
    // + "/monitor";

    private static String LogPath = "/com.yjd.app/monitor";

    public static String localPath = Environment.getExternalStorageDirectory() // 日志存储文件夹
            .getAbsolutePath() + LogPath;
    // 医界贷日志path
    public static String YjdLogPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "dd_crash_log";
    // crash日志存储路径
    public static final String CRASH_LOG_PATH = localPath + File.separator + "crashLog";
    // 日志文件后缀格式
    public static final String CRASH_LOG_POSTFIX = ".log";
    // csv日志存储路径
    public static final String CSV_LOG_PATH = localPath + File.separator + "csvLog";
    // 网络日志路径
    public static final String NetLogPath = MonitorConfig.localPath + File.separator + "network";

    // 录制视频路径
    public static String ScreenVideoPath = MonitorConfig.localPath + File.separator + "screenVideo";
}
