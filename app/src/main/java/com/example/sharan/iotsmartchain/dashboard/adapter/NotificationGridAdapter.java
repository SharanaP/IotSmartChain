package com.example.sharan.iotsmartchain.dashboard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.model.NotificationModuleData;

import java.util.ArrayList;
import java.util.List;

public class NotificationGridAdapter extends ArrayAdapter<NotificationModuleData>{
    private Context context;
    private List<NotificationModuleData> mList = new ArrayList<>();
    private NotificationModuleData notificationModuleData;
    private ViewHolder viewHolder;

    public NotificationGridAdapter(Context context, List<NotificationModuleData> mList) {
        super(context, R.layout.row_grid_item);
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public NotificationModuleData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = null;
        NotificationModuleData notificationModuleData = null;

        try{
            if (convertView == null) {
                viewHolder = new ViewHolder();
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_grid_item, parent, false);

                viewHolder.textViewTopic = (TextView) convertView.findViewById(R.id.textview_grid_title);
                viewHolder.imageViewIcon = (ImageView)convertView.findViewById(R.id.imageview_grid_icon);
                viewHolder.textViewTS = (TextView)convertView.findViewById(R.id.textview_last_time);
                viewHolder.textViewUnread = (TextView)convertView.findViewById(R.id.textview_grid_unread);
                viewHolder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.relativeLayout_grid);

                viewHolder.relativeLayout.setTag(viewHolder);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

        }catch(ClassCastException cce){
            cce.printStackTrace();
        }

        viewHolder.notificationModuleData = getItem(position);
        Log.d("SHARAN ", "" + viewHolder.notificationModuleData.toString());

        if (viewHolder.notificationModuleData != null) {
            /*Device Title*/
            if (viewHolder.notificationModuleData.getDeviceType()!= null)
                viewHolder.textViewTopic.setText(viewHolder.notificationModuleData.getDeviceType());
            /*Un read count */
            if (viewHolder.notificationModuleData.getUnReadCount() != null){
                viewHolder.textViewUnread.setText(viewHolder.notificationModuleData.getUnReadCount());
                if(viewHolder.notificationModuleData.getUnReadCount().isEmpty()){
                    viewHolder.textViewUnread.setVisibility(View.GONE);
                }else{
                    viewHolder.textViewUnread.setVisibility(View.VISIBLE);
                }
            }
            /*TimeStamp*/
            if (viewHolder.notificationModuleData.getTimeStamp() != null){
                Long timeStamp = Long.parseLong(viewHolder.notificationModuleData.getTimeStamp());
                String dateFormat = Utils.convertTime(timeStamp);
                viewHolder.textViewTS.setText(dateFormat);
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView textViewTopic;
        ImageView imageViewIcon;
        TextView textViewTS;
        TextView textViewUnread;
        RelativeLayout relativeLayout;
        NotificationModuleData notificationModuleData;
    }
}
