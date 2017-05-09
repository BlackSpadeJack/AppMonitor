package com.app.monitor.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Stack;

import android.util.Log;

import com.app.monitor.MonitorConfig;
import com.app.monitor.core.datas.DataManager;

/**
 * 网络信息存储model
 * 
 * @author yx
 * 
 */
public class DebugMessageModel {
    public static Stack<MesseageModel> messageList = new Stack<MesseageModel>();
    public static ArrayList<MesseageModel> sendingmessageList = new ArrayList<MesseageModel>();
    public static String filename = "networkLog";
    private static boolean isInit = false;

    /**
     * 初始化创建网络日志文件
     */
    private static void initLog() {
        if (!isInit) {
            Date currentTime = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
            String dateString = formatter.format(currentTime);
            if (DataManager.getInstance().getService() != null) {
                // 如果service不为空，日志名用包名和日期
                dateString =
                        DataManager.getInstance().getService().getApplicationInfo().processName
                                + filename + "_" + dateString;
            }
            filename = dateString + MonitorConfig.CRASH_LOG_POSTFIX;
            File fileDir = new File(MonitorConfig.NetLogPath);
            filename = MonitorConfig.NetLogPath + "/" + filename;
            Log.e("yjl", "网络请求日志名:" + filename);
            File file = new File(filename);
            try {
                if (!fileDir.exists()) {
                    // 先创建文件夹
                    if (fileDir.mkdirs()) {
                        if (!file.exists()) {
                            Log.e("yjl", "创建文件：" + filename);
                            if (file.createNewFile()) {
                                isInit = true;
                            }
                        }
                    }
                } else {
                    if (!file.exists()) {
                        Log.e("yjl", "创建文件：" + filename);
                        if (file.createNewFile()) {
                            isInit = true;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    public static void addMessage(MesseageModel msg) {
        initLog();
        messageList.push(msg);
        sendingmessageList.add(msg);
        writeToFile(msg);
    }

    public static void finishMessage(MesseageModel msg) {
        long currentTimestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH时mm分ss秒");
        msg.endTimeStamp = sdf.format(new Date(currentTimestamp));
        sendingmessageList.remove(msg);
    }

    public static boolean isSendingMessage(String url) {
        for (int i = 0; i < sendingmessageList.size(); i++) {
            MesseageModel msg = sendingmessageList.get(i);
        }
        return false;
    }


    /**
     * 写入日志文件
     * 
     * @param stacktrace
     * @param filename
     */
    private static void writeToFile(MesseageModel model) {
        try {
            if (model != null) {
                model.toString();
            }
            FileWriter writer = null;
            try {
                // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                writer = new FileWriter(filename, true);
                writer.write("\n");
                writer.write(model.toString());
                writer.write("\n");
                writer.write("----------------------------------");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
