package com.app.monitor.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.monitor.MonitorConfig;
import com.app.monitor.R;
import com.app.monitor.activity.weight.xlist.XListView;
import com.app.monitor.service.MonitorService;
import com.app.monitor.utils.OpenFileUtils;


public class ScanScreenVideoActivity extends Activity {
    private XListView listView;
    private ArrayList<File> filelist = new ArrayList<File>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle_activity);
        getFiles(filelist, MonitorConfig.ScreenVideoPath);
        listView = (XListView) findViewById(R.id.activitylist);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);
        listView.setRefreshTime();
        LogAdapter logAdapter = new LogAdapter();

        listView.setAdapter(logAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (filelist != null && filelist.size() > 0
                        && filelist.get(filelist.size() - position) != null) {
                    OpenFileUtils.openFiles(ScanScreenVideoActivity.this,
                            filelist.get(filelist.size() - position).getPath());
                }

            }
        });
    }


    class LogAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return filelist == null ? 0 : filelist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView =
                        LayoutInflater.from(ScanScreenVideoActivity.this).inflate(
                                R.layout.log_item, null);
                holder.logName = (TextView) convertView.findViewById(R.id.item_log_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (filelist != null && filelist.size() > 0
                    && filelist.get(filelist.size() - 1 - position) != null) {
                holder.logName.setText(filelist.get(filelist.size() - 1 - position).getName());
            }
            return convertView;
        }

    }
    class ViewHolder {
        private TextView logName;
    }

    private void getFiles(ArrayList<File> logFilesList, String path) {
        File[] allFiles = new File(path).listFiles();
        if (allFiles != null) {
            for (int i = 0; i < allFiles.length; i++) {
                File file = allFiles[i];
                if (file.isFile() && file.getName().contains(".mp4")) {
                    logFilesList.add(file);
                }
            }
        }
    }
}
