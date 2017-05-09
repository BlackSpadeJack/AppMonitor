package com.app.monitor.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.app.monitor.MonitorConfig;
import com.app.monitor.R;
import com.app.monitor.model.CrashMessage;
import com.app.monitor.utils.ServiceKeys;

/**
 * crash log 详细内容查看
 * 
 * @author yx
 * 
 */
public class PastNetLogDetailActivity extends Activity {
    TextView crashTimeTextView;
    TextView crashDetailTextView;
    private String fileName;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_log_detail_activity);
        crashTimeTextView = (TextView) findViewById(R.id.crash_time);
        crashDetailTextView = (TextView) findViewById(R.id.crash_detail);
        String name = getIntent().getStringExtra(ServiceKeys.KEY_NET_LOG_NAME);
        if (null != name) {
            crashTimeTextView.setText(name);
        }
        // String name = getIntent().getStringExtra(ServiceKeys.KEY_NET_LOG_CONTENT);
        fileName =
                MonitorConfig.NetLogPath + File.separator + name + MonitorConfig.CRASH_LOG_POSTFIX;
        if (!TextUtils.isEmpty(name)) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    initLog();
                }
            });
        }
    }

    public void initLog() {
        try {
            // String path = MonitorConfig.YjdLogPath;
            String path = fileName;
            Log.e("yjl", "网络日志路径：" + path);
            File file = new File(fileName);
            try {
                FileInputStream fin = new FileInputStream(file);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                content = new String(buffer, "UTF-8");
                fin.close();
                String fileName = file.getName();
                String[] nameArray = fileName.split("\\.log");
                if (nameArray.length > 0) {
                    String intStr = nameArray[0];
                    CrashMessage crashMessage = new CrashMessage();
                    crashMessage.crashLogName = intStr;
                    crashMessage.crashContent = content;
                    Log.e("yjl", "网络日志-----" + crashMessage.crashLogName);
                }

            } catch (FileNotFoundException e) {

            } catch (IOException e2) {
                e2.printStackTrace();
            }

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
                    // listAdapter.notifyDataSetChanged();
                    crashDetailTextView.setText(content);
                    crashDetailTextView.postInvalidate();
                    break;
            }

        }
    };

}
