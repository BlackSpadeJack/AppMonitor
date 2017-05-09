package com.app.monitor.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.app.monitor.MonitorConfig;
import com.app.monitor.R;
import com.app.monitor.activity.weight.adapter.PastNetLogAdapter;
import com.app.monitor.activity.weight.xlist.XListView;
import com.app.monitor.model.CrashMessage;
import com.app.monitor.utils.ServiceKeys;

/**
 * 往期测试网络日志 查看列表
 * 
 * @author yx
 * 
 */
public class PastNetLogActivity extends Activity {
    private ArrayList<File> logFilesList = new ArrayList<File>();

    private ArrayList<CrashMessage> crashMessageArrayList = new ArrayList<CrashMessage>();

    private XListView logListView;

    private PastNetLogAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_log_activity);
        logListView = (XListView) findViewById(R.id.log_list_view);

        logListView.setPullLoadEnable(false);
        logListView.setPullRefreshEnable(false);
        logListView.setRefreshTime();
        listAdapter = new PastNetLogAdapter(this, crashMessageArrayList);
        logListView.setAdapter(listAdapter);

        handler.post(new Runnable() {

            @Override
            public void run() {
                initLog();
            }
        });
        logListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int size = crashMessageArrayList.size();
                CrashMessage crashMessage = crashMessageArrayList.get(size - position);
                Intent it = new Intent(PastNetLogActivity.this, PastNetLogDetailActivity.class);
                it.putExtra(ServiceKeys.KEY_NET_LOG_NAME, crashMessage.crashLogName);
                it.putExtra(ServiceKeys.KEY_NET_LOG_CONTENT, crashMessage.crashContent);
                startActivity(it);
            }
        });

    }


    public void initLog() {
        try {
            // String path = MonitorConfig.YjdLogPath;
            String path = MonitorConfig.NetLogPath;
            Log.e("yjl", "网络日志路径：" + path);
            getFiles(logFilesList, path);
            for (int i = 0; i < logFilesList.size(); i++) {
                File file = logFilesList.get(i);
                try {
                    FileInputStream fin = new FileInputStream(file);
                    int length = fin.available();
                    byte[] buffer = new byte[1024];
                    fin.read(buffer);
                    String content = new String(buffer, "UTF-8");
                    fin.close();
                    String fileName = file.getName();
                    String[] nameArray = fileName.split("\\.log");
                    if (nameArray.length > 0) {
                        String intStr = nameArray[0];
                        CrashMessage crashMessage = new CrashMessage();
                        crashMessage.crashLogName = intStr;
                        crashMessage.crashContent = content;
                        // Log.e("yjl", "网络日志-----" + crashMessage.crashLogName);
                        crashMessageArrayList.add(crashMessage);
                    }

                } catch (FileNotFoundException e) {

                } catch (IOException e2) {
                    e2.printStackTrace();
                }

            }
            // listAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            handler.sendEmptyMessage(0);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    listAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };

    private void getFiles(ArrayList<File> logFilesList, String path) {
        File[] allFiles = new File(path).listFiles();
        int length = allFiles.length;
        if (length >= 1) {
            // 去除当前新建的log
            length = length - 1;
        }
        for (int i = 0; i < length; i++) {
            File file = allFiles[i];
            if (file.isFile() && file.getName().contains(MonitorConfig.CRASH_LOG_POSTFIX)) {
                logFilesList.add(file);
            }
        }
    }
}
