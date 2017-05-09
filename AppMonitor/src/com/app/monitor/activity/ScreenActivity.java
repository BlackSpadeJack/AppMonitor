package com.app.monitor.activity;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.app.monitor.MonitorConfig;
import com.app.monitor.screen.ScreenRecorder;

/**
 * 用于启动屏幕录制的功能
 * 
 * @author yx
 * 
 */
@SuppressLint({"NewApi", "SimpleDateFormat"})
public class ScreenActivity extends Activity {

    // 屏幕录制参数
    private MediaProjectionManager mMediaProjectionManager;
    private static ScreenRecorder mRecorder;
    private static final int REQUEST_SCREEN_CODE = 1;
    Button startBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置窗口没有标题
        // setContentView(R.layout.main_activity);
        // startBut = (Button) findViewById(R.id.start_screen);
        // startBut.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // startScreen();
        // }
        // });
        startScreen();
    }

    public void startScreen() {
        mMediaProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_SCREEN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MediaProjection mediaProjection =
                mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e("yjl", "media projection is null");
            return;
        }
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String dateString = formatter.format(currentTime);
        // 视频宽高
        final int width = this.getResources().getDisplayMetrics().widthPixels;
        final int height = this.getResources().getDisplayMetrics().heightPixels;
        File filePath = new File(MonitorConfig.ScreenVideoPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File file =
                new File(MonitorConfig.ScreenVideoPath, "record-" + width + "x" + height + "-"
                        + dateString + ".mp4");
        final int bitrate = 6000000;
        mRecorder =
                new ScreenRecorder(width, height, bitrate, 1, mediaProjection,
                        file.getAbsolutePath());
        mRecorder.start();
        // mButton.setText("Stop Recorder");
        Log.e("yjl", "Screen recorder is running...");
        Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
        // moveTaskToBack(true);
        this.finish();
    }

    /**
     * 录制屏幕是否正在运行
     * 
     * @return
     */
    public static boolean isRun() {
        if (mRecorder != null) {
            return mRecorder.isAlive();
        }
        return false;
    }

    public static void stopScreenShot() {
        if (mRecorder != null) {
            mRecorder.quit();
            mRecorder = null;
        }
    }

    // @Override
    // protected void onDestroy() {
    // super.onDestroy();
    // if (mRecorder != null) {
    // mRecorder.quit();
    // mRecorder = null;
    // }
    // }
}
