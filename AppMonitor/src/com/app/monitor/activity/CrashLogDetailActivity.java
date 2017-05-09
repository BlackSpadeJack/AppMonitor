package com.app.monitor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.app.monitor.R;
import com.app.monitor.utils.ServiceKeys;

/**
 * crash log 详细内容查看
 * 
 * @author yx
 * 
 */
public class CrashLogDetailActivity extends Activity {
    TextView crashTimeTextView;
    TextView crashDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_log_detail_activity);

        crashTimeTextView = (TextView) findViewById(R.id.crash_time);
        crashDetailTextView = (TextView) findViewById(R.id.crash_detail);
        String crashTime = getIntent().getStringExtra(ServiceKeys.KEY_CRASH_LOG_NAME);
        if (null != crashTime) {
            crashTimeTextView.setText(crashTime);
        }

        String crashContent = getIntent().getStringExtra(ServiceKeys.KEY_CRASH_LOG_CONTENT);
        if (null != crashContent) {
            crashDetailTextView.setText(crashContent);
        }
    }
}
