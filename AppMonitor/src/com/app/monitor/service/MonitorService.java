package com.app.monitor.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.app.monitor.MonitorConfig;
import com.app.monitor.R;
import com.app.monitor.activity.DebugTabActivity;
import com.app.monitor.activity.ScreenActivity;
import com.app.monitor.core.MonitorWindowManager;
import com.app.monitor.core.datas.DataManager;
import com.app.monitor.utils.Constants;
import com.app.monitor.utils.CpuInfo;
import com.app.monitor.utils.CurrentInfo;
import com.app.monitor.utils.EncryptData;
import com.app.monitor.utils.FpsInfo;
import com.app.monitor.utils.MailSender;
import com.app.monitor.utils.MemoryInfo;
import com.app.monitor.utils.ServiceKeys;
import com.app.monitor.utils.Settings;

public class MonitorService extends Service {
    private static final String TAG = "yjl";
    private MonitorWindowManager monitorWindowManager = null;
    public static String resultFilePath;
    public static String rootFilePath;
    public static boolean isStop = false;
    private String startTime = "";
    // 基础监控信息
    public CpuInfo cpuInfo;
    private String totalBatt;
    private String temperature;
    private String voltage;
    private CurrentInfo currentInfo;
    private BatteryInfoBroadcastReceiver batteryBroadcast = null;
    private int delaytime = 2000;
    private DecimalFormat fomart;
    private MemoryInfo memoryInfo;
    private Handler handler = new Handler();
    private EncryptData des;

    // 监控的线程信息
    private String processName, packageName, startActivity;
    private int pid, uid;

    // 设置的邮箱信息
    private String sender, password, recipients, smtp;
    private String[] receivers;
    private boolean isFloating;
    private boolean isRoot;
    private boolean isAutoStop = false;
    private static final String BLANK_STRING = "";

    private int getStartTimeCount = 0;
    private boolean isGetStartTime = true;
    private static final int MAX_START_TIME_COUNT = 5;
    public static FileWriter writer = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service onCreate");
        if (monitorWindowManager == null) {
            monitorWindowManager = new MonitorWindowManager(this);
        }
        DataManager.getInstance().setService(this);
        isStop = false;
        memoryInfo = new MemoryInfo();
        fomart = new DecimalFormat();
        fomart.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        fomart.setGroupingUsed(false);
        fomart.setMaximumFractionDigits(2);
        fomart.setMinimumFractionDigits(0);
        des = new EncryptData("appmonitor");
        currentInfo = new CurrentInfo();
        batteryBroadcast = new BatteryInfoBroadcastReceiver();
        registerReceiver(batteryBroadcast, new IntentFilter(ServiceKeys.BATTERY_CHANGED));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "service onStartCommand");
        try {
            if (monitorWindowManager != null) {
                monitorWindowManager.updateViews();
            }
            initData(intent, startId);
            if (TextUtils.isEmpty(resultFilePath) || new File(resultFilePath).exists()) {
                createResultCsv();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("NewApi")
    private void initData(Intent intent, int startId) {
        PendingIntent contentIntent =
                PendingIntent.getActivity(getBaseContext(), 0, new Intent(this,
                        DebugTabActivity.class), 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.icon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle("医界贷");
        startForeground(startId, builder.build());
        if (intent != null) {
            pid = intent.getExtras().getInt("pid");
            uid = intent.getExtras().getInt("uid");
            processName = intent.getExtras().getString("processName");
            packageName = intent.getExtras().getString("packageName");
            startActivity = intent.getExtras().getString("startActivity");
            cpuInfo = new CpuInfo(getBaseContext(), pid, Integer.toString(uid));
        } else {
            Log.e("yjl", "MonitorService  intent  is null!");
        }
        readSettingInfo();
    }

    public static void initCreatFile() {
        File pathLog = new File(MonitorConfig.CSV_LOG_PATH);
        try {
            if (!pathLog.exists()) {
                Log.e("yjl", "创建路径:" + pathLog.mkdirs());
            }
            File resultFile = new File(resultFilePath);
            if (resultFile.createNewFile()) {
                Log.e("yjl", "创建新csv文件名：" + resultFilePath);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * write the test result to csv format report.
     */
    private void createResultCsv() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String mDateTime;
        String heapData = "";
        if ((Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"))) {
            mDateTime = formatter.format(cal.getTime().getTime() + 8 * 60 * 60 * 1000);
        } else {
            mDateTime = formatter.format(cal.getTime().getTime());
        }
        if (TextUtils.isEmpty(resultFilePath)) {
            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                // 在4.0以下的低版本上/sdcard连接至/mnt/sdcard，而4.0以上版本则连接至/storage/sdcard0，所以有外接sdcard，/sdcard路径一定存在
                rootFilePath =
                        "/sdcard" + File.separator + ServiceKeys.packgeName + File.separator
                                + "monitor" + File.separator + "csvLog";
                resultFilePath = rootFilePath + "/Monitor_TestResult_" + mDateTime + ".csv";
            } else {
                rootFilePath = MonitorConfig.CSV_LOG_PATH;
                resultFilePath =
                        rootFilePath + File.separator + "Monitor_TestResult_" + mDateTime + ".csv";
            }
        }
        initCreatFile();
        long totalMemorySize = memoryInfo.getTotalMemory();
        String totalMemory = fomart.format((double) totalMemorySize / 1024);
        String multiCpuTitle = BLANK_STRING;
        // titles of multiple cpu cores
        if (cpuInfo == null) {
            cpuInfo = new CpuInfo(getBaseContext(), pid, Integer.toString(uid));
        }
        ArrayList<String> cpuList = cpuInfo.getCpuList();
        if (cpuList != null) {
            for (int i = 0; i < cpuList.size(); i++) {
                multiCpuTitle += Constants.COMMA + cpuList.get(i) + getString(R.string.total_usage);
            }
        }
        writeToFile(getString(R.string.process_package) + Constants.COMMA + packageName
                + Constants.LINE_END + getString(R.string.process_name) + Constants.COMMA
                + processName + Constants.LINE_END + getString(R.string.process_pid)
                + Constants.COMMA + pid + Constants.LINE_END + getString(R.string.mem_size)
                + Constants.COMMA + totalMemory + "MB" + Constants.LINE_END
                + getString(R.string.cpu_type) + Constants.COMMA + cpuInfo.getCpuName()
                + Constants.LINE_END + getString(R.string.android_system_version) + Constants.COMMA
                + memoryInfo.getSDKVersion() + Constants.LINE_END + getString(R.string.mobile_type)
                + Constants.COMMA + memoryInfo.getPhoneType() + Constants.LINE_END + "UID"
                + Constants.COMMA + uid + Constants.LINE_END);
        if (isGrantedReadLogsPermission()) {
            writeToFile(ServiceKeys.START_TIME);
        }
        if (isRoot) {
            heapData =
                    getString(R.string.native_heap) + Constants.COMMA
                            + getString(R.string.dalvik_heap) + Constants.COMMA;
        }
        writeToFile(getString(R.string.timestamp) + Constants.COMMA
                + getString(R.string.top_activity) + Constants.COMMA + heapData
                + getString(R.string.used_mem_PSS) + Constants.COMMA
                + getString(R.string.used_mem_ratio) + Constants.COMMA
                + getString(R.string.mobile_free_mem) + Constants.COMMA
                + getString(R.string.app_used_cpu_ratio) + Constants.COMMA
                + getString(R.string.total_used_cpu_ratio) + multiCpuTitle + Constants.COMMA
                + getString(R.string.traffic) + Constants.COMMA + getString(R.string.battery)
                + Constants.COMMA + getString(R.string.current) + Constants.COMMA
                + getString(R.string.temperature) + Constants.COMMA + getString(R.string.voltage)
                + Constants.COMMA + getString(R.string.fps) + Constants.LINE_END);
        handler.postDelayed(task, 2000);
    }

    public void closeOpenedStream() {

        writeToFile(getString(R.string.comment1) + Constants.LINE_END
                + getString(R.string.comment2) + Constants.LINE_END + getString(R.string.comment3)
                + Constants.LINE_END + getString(R.string.comment4) + Constants.LINE_END);
    }

    private boolean isGrantedReadLogsPermission() {
        int permissionState =
                getPackageManager().checkPermission(android.Manifest.permission.READ_LOGS,
                        getPackageName());
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    // 读取默认的设置信息
    private void readSettingInfo() {
        SharedPreferences preferences =
                Settings.getDefaultSharedPreferences(getApplicationContext());
        int interval = preferences.getInt(Settings.KEY_INTERVAL, 5);
        delaytime = interval * 1000;
        isFloating = preferences.getBoolean(Settings.KEY_ISFLOAT, true);
        sender = preferences.getString(Settings.KEY_SENDER, BLANK_STRING);
        password = preferences.getString(Settings.KEY_PASSWORD, BLANK_STRING);
        recipients = preferences.getString(Settings.KEY_RECIPIENTS, BLANK_STRING);
        receivers = recipients.split("\\s+");
        smtp = preferences.getString(Settings.KEY_SMTP, BLANK_STRING);
        isRoot = preferences.getBoolean(Settings.KEY_ROOT, false);
        isAutoStop = preferences.getBoolean(Settings.KEY_AUTO_STOP, false);
        // Log.e("yjl", "sender:" + sender);
        // Log.e("yjl", "password:" + password);
        // Log.e("yjl", "recipients:" + recipients);
        // Log.e("yjl", "smtp:" + smtp);
    }

    boolean isSendSuccessfully = false;

    @Override
    public void onDestroy() {
        handler.removeCallbacks(task);
        Log.e("yjl", "monitor service onDestroy ！");
        closeOpenedStream();
        ScreenActivity.stopScreenShot();
        isStop = true;
        unregisterReceiver(batteryBroadcast);
        readSettingInfo();
        if (!BLANK_STRING.equals(startTime)) {
            replaceFileString(resultFilePath, ServiceKeys.START_TIME,
                    getString(R.string.start_time) + startTime + Constants.LINE_END);
        } else {
            replaceFileString(resultFilePath, ServiceKeys.START_TIME, BLANK_STRING);
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "未设置邮箱!", Toast.LENGTH_LONG).show();
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final String desStr = des.decrypt(password);
                    Log.e("yjl", "pas====" + desStr);

                    isSendSuccessfully =
                            MailSender.sendTextMail(sender, desStr, smtp,
                                    "Emmagee Performance Test Report", "see attachment",
                                    resultFilePath, receivers);
                } catch (Exception e) {
                    e.printStackTrace();
                    isSendSuccessfully = false;
                }
                if (isSendSuccessfully) {
                    Log.e(TAG, "邮件发送成功：" + recipients);
                    // Toast.makeText(getBaseContext(),
                    // getBaseContext().getString(R.string.send_success_toast) + recipients,
                    // Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "邮件发送失败：" + resultFilePath);
                    // Toast.makeText(getBaseContext(),
                    // getBaseContext().getString(R.string.send_fail_toast) + resultFilePath,
                    // Toast.LENGTH_LONG).show();
                }
            }
        }).start();


        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable task = new Runnable() {

        public void run() {
            // Log.e(TAG, "task run");
            getStartTimeFromLogcat();
            dataRefresh();
            handler.postDelayed(this, 2000);
        }
    };

    private void dataRefresh() {
        // Log.e("yjl", "dataRefresh");
        String currentBatt = String.valueOf(currentInfo.getCurrentValue());
        // 异常数据过滤
        try {
            if (Math.abs(Double.parseDouble(currentBatt)) >= 500) {
                currentBatt = Constants.NA;
            }
        } catch (Exception e) {
            currentBatt = Constants.NA;
        }
        ArrayList<String> processInfo =
                cpuInfo.getCpuRatioInfo(totalBatt, currentBatt, temperature, voltage,
                        String.valueOf(FpsInfo.fps()), isRoot);
        DataManager.getInstance().updateView(processInfo);
    }

    private void getStartTimeFromLogcat() {
        if (!isGetStartTime || getStartTimeCount >= MAX_START_TIME_COUNT) {
            return;
        }
        try {
            // filter logcat by Tag:ActivityManager and Level:Info
            String logcatCommand = "logcat -v time -d ActivityManager:I *:S";
            Process process = Runtime.getRuntime().exec(logcatCommand);
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder strBuilder = new StringBuilder();
            String line = BLANK_STRING;

            while ((line = bufferedReader.readLine()) != null) {
                strBuilder.append(line);
                strBuilder.append(Constants.LINE_END);
                String regex = ".*Displayed.*" + startActivity + ".*\\+(.*)ms.*";
                if (line.matches(regex)) {
                    Log.w("my logs", line);
                    if (line.contains("total")) {
                        line = line.substring(0, line.indexOf("total"));
                    }
                    startTime =
                            line.substring(line.lastIndexOf("+") + 1, line.lastIndexOf("ms") + 2);
                    Toast.makeText(MonitorService.this, getString(R.string.start_time) + startTime,
                            Toast.LENGTH_LONG).show();
                    isGetStartTime = false;
                    break;
                }
            }
            getStartTimeCount++;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void replaceFileString(String filePath, String replaceType, String replaceString) {
        try {
            File file = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = BLANK_STRING;
            String oldtext = BLANK_STRING;
            while ((line = reader.readLine()) != null) {
                oldtext += line + Constants.LINE_END;
            }
            reader.close();
            // replace a word in a file
            String newtext = oldtext.replaceAll(replaceType, replaceString);
            BufferedWriter writer =
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),
                            getString(R.string.csv_encoding)));
            writer.write(newtext);
            writer.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * 写入日志文件
     * 
     * @param stacktrace
     * @param filename
     */
    public static void writeToFile(String content) {
        try {
            if (!new File(resultFilePath).exists()) {
                initCreatFile();
            }
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(resultFilePath, true);
            writer.write("\n");
            writer.write(content.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 电池信息监控监听器
     */
    public class BatteryInfoBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                // 得到系统当前电量
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                // 取得系统总电量
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                totalBatt = String.valueOf(level * 100 / scale);
                voltage =
                        String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) * 1.0 / 1000);
                temperature =
                        String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) * 1.0 / 10);
                // Log.e("yjl", "totalBatt:" + totalBatt);
                // Log.e("yjl", "voltage:" + voltage);
                // Log.e("yjl", "temperature:" + temperature);
            }
        }

    }
}
