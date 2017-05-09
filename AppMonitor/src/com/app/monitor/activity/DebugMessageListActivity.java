package com.app.monitor.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.monitor.R;
import com.app.monitor.model.DebugMessageModel;


public class DebugMessageListActivity extends Activity {

    private TextView title;
    private ListView messageListView;
    private Button lookNetworklog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_message_list);

        // title = (TextView) findViewById(R.id.topview_title);
        // title.setText("协议log");
        messageListView = (ListView) findViewById(R.id.debugMessageList);

        DebugListAdapter debugAdapter = new DebugListAdapter(this);
        messageListView.setAdapter(debugAdapter);
        messageListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub

                int size = DebugMessageModel.messageList.size();

                Intent intent =
                        new Intent(DebugMessageListActivity.this, DebugDetailActivity.class);
                intent.putExtra("position", size - 1 - arg2);
                startActivity(intent);

            }
        });
        lookNetworklog = (Button) findViewById(R.id.look_networklog);
        lookNetworklog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DebugMessageListActivity.this, PastNetLogActivity.class);
                startActivity(intent);
            }
        });
    }

    class DebugListAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;

        public DebugListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return DebugMessageModel.messageList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return DebugMessageModel.messageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.debug_message_item, null);
                holder.time = (TextView) convertView.findViewById(R.id.debug_item_time);
                holder.message = (TextView) convertView.findViewById(R.id.debug_item_message);
                holder.request = (TextView) convertView.findViewById(R.id.debug_item_request);
                holder.response = (TextView) convertView.findViewById(R.id.debug_item_response);
                holder.netSize = (TextView) convertView.findViewById(R.id.debug_item_netSize);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            int size = DebugMessageModel.messageList.size();
            DebugMessageModel.messageList.get(size - 1 - position).toString();
            holder.time.setText(DebugMessageModel.messageList.get(size - 1 - position).startTime);
            holder.message.setText(DebugMessageModel.messageList.get(size - 1 - position).message);
            holder.request.setText(DebugMessageModel.messageList.get(size - 1 - position).requset);
            holder.response
                    .setText(DebugMessageModel.messageList.get(size - 1 - position).response);
            holder.netSize.setText(DebugMessageModel.messageList.get(size - 1 - position).netSize);

            return convertView;
        }

        class ViewHolder {
            private TextView time;
            private TextView message;
            private TextView request;
            private TextView response;
            private TextView netSize;
        }

    }
}
