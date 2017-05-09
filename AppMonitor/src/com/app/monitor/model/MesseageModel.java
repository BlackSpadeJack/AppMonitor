package com.app.monitor.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.app.monitor.MonitorConfig;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MesseageModel {
    public String timeStamp;
    public String endTimeStamp;

    public String startTime;
    public String message;
    public String requset;
    public String response;
    public String netSize;

    public String mUrl; // 请求url
    public String mParams; // 请求参数
    public String mResult; // 请求结果
    public String mStatus; // 请求状态
    static Handler mHandler = null;
    static Date bandwidthMeasurementDate = new Date();
    public static Date throttleWakeUpTime = null;
    public static long maxBandwidthPerSecond = 14800;
    // 上一秒所有线程传输数据大小
    public static long bandwidthUsedInLastSecond = 0;
    public static long averageBandwidthUsedPerSecond = 0;
    static boolean forceThrottleBandwith = false;
    static Object bandwidthThrottlingLock = new Object();
    Timer timer;
    TimerTask task;

    public MesseageModel() {
        long currentTimestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH时mm分ss秒SSS");
        this.timeStamp = sdf.format(new Date(currentTimestamp));
        if (null == mHandler) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        MesseageModel.this.performThrottling();
                    }
                }
            };
        }


        task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        };
        timer = new Timer(true);

        if (MesseageModel.isBandwidthThrottled()) {
            timer.schedule(task, 250, 250);
        }

    }

    public void performThrottling() {
        if (MesseageModel.isBandwidthThrottled()) {
            MesseageModel.measureBandwidthUsage();
        }

    }

    static void measureBandwidthUsage() {
        try {
            if (MesseageModel.isBandwidthThrottled()) {
                synchronized (bandwidthThrottlingLock) {
                    if (null == bandwidthMeasurementDate
                            || bandwidthMeasurementDate.before(new Date())) {
                        MesseageModel.recordBandwidthUsage();
                    }

                    long bytesRemaining = maxBandwidthPerSecond - bandwidthUsedInLastSecond;

                    if (bytesRemaining < 0) {
                        double extraSleepTime = (-bytesRemaining / (maxBandwidthPerSecond * 1.0));
                        throttleWakeUpTime = new Date();
                        throttleWakeUpTime.setTime(throttleWakeUpTime.getTime()
                                + (int) extraSleepTime * 1000);
                    }
                }

                if (null != throttleWakeUpTime) {
                    String throttle =
                            "[THROTTLING] Sleeping request until after "
                                    + throttleWakeUpTime.toString();
                    System.out.print(throttle);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> bandwidthUseageTracker = new ArrayList<Integer>();

    static void recordBandwidthUsage() {
        if (bandwidthUsedInLastSecond == 0) {
            bandwidthUseageTracker.clear();
        } else {
            Date nowDate = new Date();
            long interval = bandwidthMeasurementDate.getTime() - nowDate.getTime();

            while ((interval < 0 || bandwidthUseageTracker.size() > 5)
                    && bandwidthUseageTracker.size() > 0) {
                bandwidthUseageTracker.remove(0);
                interval++;
            }
        }

        Log.d("THROTTLING", "[THROTTLING] ===Used:" + bandwidthUsedInLastSecond
                + " bytes of bandwidth in last measurement period===");

        bandwidthUseageTracker.add(Integer.valueOf((int) bandwidthUsedInLastSecond));
        bandwidthMeasurementDate = new Date();
        bandwidthMeasurementDate.setTime(bandwidthMeasurementDate.getTime() + 1000);

        bandwidthUsedInLastSecond = 0;

        long totalBytes = 0;
        for (int i = 0; i < bandwidthUseageTracker.size(); i++) {
            Integer bytes = bandwidthUseageTracker.get(i);
            totalBytes += bytes.longValue();
        }

        averageBandwidthUsedPerSecond = totalBytes / bandwidthUseageTracker.size();
    }

    static long maxUploadReadLength() {
        long toRead = maxBandwidthPerSecond / 4;
        try {
            synchronized (bandwidthThrottlingLock) {
                if (maxBandwidthPerSecond > 0
                        && (bandwidthUsedInLastSecond + toRead > maxBandwidthPerSecond)) {
                    toRead = maxBandwidthPerSecond - bandwidthUsedInLastSecond;
                    if (toRead < 0) {
                        toRead = 0;
                    }
                }

                if (0 == toRead || null == bandwidthMeasurementDate
                        || bandwidthMeasurementDate.before(new Date())) {
                    throttleWakeUpTime = bandwidthMeasurementDate;
                }
            }
        } catch (Exception e) {

        }
        return toRead;
    }

    public static void incrementBandwidthUsedInLastSecond(long bytes) {
        try {
            synchronized (bandwidthThrottlingLock) {
                bandwidthUsedInLastSecond += bytes;
            }
        } catch (Exception e) {

        }
    }

    public static boolean isBandwidthThrottled() {
        if (forceThrottleBandwith) {
            return true;
        }

        return false;
    }


    @Override
    public String toString() {
        String msgDesc = "";

        msgDesc += "创建时间：" + this.timeStamp + "\n\n";
        startTime = "创建时间：" + this.timeStamp;

        if (null != this.endTimeStamp) {
            msgDesc += "结束时间：" + this.endTimeStamp + "\n\n";
        }

        msgDesc += "消息：" + this.mUrl + "\n\n";
        message = "消息：" + this.mUrl;

        if (null != this.mParams) {
            msgDesc += "请求：" + this.mParams + "\n\n";
            // requset = "请求："+this.params.toString();
            // requset = "请求：\n" + JsonFormatTool.formatJson(this.mParams.toString(), "    ");
            requset = "请求：\n" + this.mParams + "    ";
        } else {
            msgDesc += "请求：{}\n\n";
            requset = "请求：" + "{}";
        }

        if (null != this.mResult) {
            msgDesc += "响应：" + this.mResult + "\n\n";
            // response = "响应："+this.getResult().toString()+"\n\n";

            response = "响应：\n" + "json:" + mResult + "    ";

            float f = this.mResult.getBytes().length;
            if (this.mResult.toString().getBytes().length > 1024) {
                float a = f / 1024;
                DecimalFormat df = new DecimalFormat("#.##");
                msgDesc += "网络包大小：" + df.format(a) + "k";
                netSize = "网络包大小：" + df.format(a) + "k";
            } else {
                msgDesc += "网络包大小：" + this.mResult.toString().getBytes().length + "b";
                netSize = "网络包大小：" + this.mResult.toString().getBytes().length + "b";
            }

        } else {
            String str = null;
            // try {
            if (null != this.mStatus) {
                // str = new String(this.getStatus().getData(), this.getEncoding());
                str = mStatus;
            }

            msgDesc += "响应：" + str + "\n\n";
            response = "响应：" + str;
            // } catch (UnsupportedEncodingException e) {
            // e.printStackTrace(); // To change body of catch statement use File | Settings | File
            // // Templates.
            // }

        }

        return msgDesc;
    }


}
