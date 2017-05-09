package com.app.monitor.activity;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.monitor.R;
import com.app.monitor.utils.OpenFileUtils;

/**
 * 文件夹浏览页面
 * 
 * @author yx
 * 
 */
public class FileBrowser extends Activity {
    private ListView mainListView = null;
    private List<Map<String, Object>> list = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("文件浏览器");
        mainListView = new ListView(this);
        setContentView(mainListView);

        File file = Environment.getRootDirectory();
        String pathx = file.getAbsolutePath();
        this.setTitle(pathx);
        // android的总目录就是"/"
        list_init("/");
    }

    void list_init(String path) {
        File file = new File(path);

        File[] fileList = file.listFiles();
        // for (File file2 : list1) {
        // Log.e("yjl", "文件名：" + file2.getName());
        // }
        list = new ArrayList<Map<String, Object>>();
        Map<String, Object> item;
        item = new HashMap<String, Object>();
        if (path.equals("/")) {
            item.put("ico", R.drawable.home);
            item.put("name", "总目录列表");
            item.put("path", "/");
            list.add(item);
        } else {
            item.put("ico", R.drawable.back);
            item.put("name", "返回上一级");
            item.put("path", file.getParent());
            list.add(item);
        }
        if (fileList != null) {
            List<File> listFile = new ArrayList<File>();
            for (File f : fileList) {
                listFile.add(f);
            }
            Collections.sort(listFile);
            for (int i = 0; i < listFile.size(); i++) {
                item = new HashMap<String, Object>();
                if (listFile.get(i).isDirectory()) {
                    if (listFile.get(i).list() == null || listFile.get(i).list().length < 1) {
                        item.put("ico", R.drawable.file1);
                    } else {
                        item.put("ico", R.drawable.file2);
                    }
                } else {
                    item.put("ico", R.drawable.content);
                }
                item.put("name", listFile.get(i).getName());
                item.put("path", listFile.get(i).getAbsolutePath());
                list.add(item);
            }
        }
        final MyAdapter ma = new MyAdapter(this, list);
        // mainListView=new ListView(this);
        mainListView.setAdapter(ma);
        mainListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 > 0 && (Integer) (list.get(arg2).get("ico")) == R.drawable.content) {
                    // 非文件夹图标，点击无效
                    String path = (String) ((Map<String, Object>) ma.getItem(arg2)).get("path");
                    Log.e("yjl", "点击文件路径:" + path);
                    OpenFileUtils.openFiles(FileBrowser.this, path);
                } else {
                    // 打开下一级文件目录列表
                    list_init((String) (list.get(arg2).get("path")));
                }
            }
        });
        this.setTitle(path);
    }

    public class MyAdapter extends BaseAdapter {

        Context context = null;
        List<Map<String, Object>> list = null;

        MyAdapter(Context context, List<Map<String, Object>> list) {
            this.context = context;
            this.list = list;
        }

        public int getCount() {
            return list.size();
        }

        public Map<String, Object> getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout returnView = new LinearLayout(context);
            returnView.setLayoutParams(new ListView.LayoutParams(-1, -2));// 注意:ListView.LayoutParams
            // 图标
            ImageView iv = new ImageView(context);
            LinearLayout.LayoutParams lp_iv = new LinearLayout.LayoutParams(-2, -2);
            lp_iv.rightMargin = 10;
            iv.setLayoutParams(lp_iv);
            iv.setScaleType(ScaleType.CENTER_INSIDE);
            iv.setImageResource((Integer) ((list.get(position)).get("ico")));
            returnView.addView(iv);
            // 文件名
            TextView name = new TextView(context);
            LinearLayout.LayoutParams lp_tv = new LinearLayout.LayoutParams(-2, -2);
            name.setLayoutParams(lp_tv);
            name.setTextSize(name.getTextSize() / 2);
            name.setText((String) (list.get(position).get("name")));
            returnView.addView(name);
            //
            return returnView;
        }
    }
}
