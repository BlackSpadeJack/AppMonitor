package com.app.monitor.activity.weight.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.monitor.R;
import com.app.monitor.model.CrashMessage;


public class PastNetLogAdapter extends BaseAdapter {
    public ArrayList dataList = new ArrayList();
    protected Context mContext;
    protected LayoutInflater mInflater = null;

    public PastNetLogAdapter(Context c, ArrayList dataList) {
        super();
        mContext = c;
        mInflater = LayoutInflater.from(c);
        this.dataList = dataList;
    }

    public class LogCellHolder {
        TextView logTimeTextView;
        TextView logContentTextView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return -1;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LogCellHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.crash_log_item, null);
            holder = new LogCellHolder();
            holder.logTimeTextView = (TextView) convertView.findViewById(R.id.crash_time);
            holder.logContentTextView = (TextView) convertView.findViewById(R.id.crash_content);
            convertView.setTag(holder);
        } else {
            holder = (LogCellHolder) convertView.getTag();
        }
        int size = dataList.size();
        CrashMessage message = (CrashMessage) dataList.get(size - 1 - position);
        holder.logTimeTextView.setText(message.crashLogName);
        holder.logContentTextView.setText(message.crashContent);
        return convertView;
    }

}
