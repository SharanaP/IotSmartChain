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
import com.example.sharan.iotsmartchain.model.NotificationCountModel;
import com.example.sharan.iotsmartchain.model.NotificationModuleData;

import java.util.ArrayList;
import java.util.List;

public class NotificationGridAdapter extends ArrayAdapter<NotificationCountModel> {
    private static String TAG = NotificationGridAdapter.class.getSimpleName();
    private Context context;
    private List<NotificationCountModel> mList = new ArrayList<>();
    private NotificationCountModel notificationCountModel;
    private ViewHolder viewHolder;

    public NotificationGridAdapter(Context context, List<NotificationCountModel> mList) {
        super(context, R.layout.row_grid_item);
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public NotificationCountModel getItem(int position) {
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

        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_grid_item, parent, false);

                viewHolder.textViewTopic = (TextView) convertView.findViewById(R.id.textview_grid_title);
                viewHolder.textViewIotSN = (TextView)convertView.findViewById(R.id.textview_iot_sn);
                viewHolder.textViewDescription= (TextView)convertView.findViewById(R.id.textview_desp);
                viewHolder.imageViewIcon = (ImageView) convertView.findViewById(R.id.imageview_grid_icon);
                viewHolder.textViewTS = (TextView) convertView.findViewById(R.id.textview_last_time);
                viewHolder.textViewUnread = (TextView) convertView.findViewById(R.id.textview_grid_unread);
                viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout_grid);

                viewHolder.relativeLayout.setTag(viewHolder);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }

        viewHolder.notificationCountModel = getItem(position);
        Log.d("SHARAN ", "" + viewHolder.notificationCountModel.toString());

        if (viewHolder.notificationCountModel != null) {
            /*Device Title*/
            if (viewHolder.notificationCountModel.getLabel() != null)
                viewHolder.textViewTopic.setText(viewHolder.notificationCountModel.getLabel());
            /*Description*/
            if(viewHolder.notificationCountModel.getDescription() != null)
                viewHolder.textViewDescription.setText(viewHolder.notificationCountModel.getDescription());
            /*Iot Sensor SN*/
            if(viewHolder.notificationCountModel.getIotSensorSn() != null)
                viewHolder.textViewIotSN.setText(viewHolder.notificationCountModel.getIotSensorSn());
            /*Un read count */
            if (viewHolder.notificationCountModel.getNotificationsCount() != null) {
                String count = (viewHolder.notificationCountModel.getNotificationsCount());
                if (count == null && count.equalsIgnoreCase("0")) {
                    Log.d(TAG, " viewHolder.textViewUnread.setText " + count);
                    viewHolder.textViewUnread.setVisibility(View.GONE);
                    viewHolder.textViewUnread.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.textViewUnread.setVisibility(View.VISIBLE);
                    viewHolder.textViewUnread.setText(count);
                    Log.d(TAG, " ELSE : viewHolder.textViewUnread.setText " + count);
                }
            } else {
                viewHolder.textViewUnread.setVisibility(View.INVISIBLE);
            }

            /*TimeStamp*/
            if (viewHolder.notificationCountModel.getInstalledTimeStamp() != -1) {
                Long timeStamp = (viewHolder.notificationCountModel.getInstalledTimeStamp());
                String dateFormat = Utils.convertTime(timeStamp);
                viewHolder.textViewTS.setText(dateFormat);
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView textViewTopic;
        TextView textViewIotSN;
        TextView textViewDescription;
        ImageView imageViewIcon;
        TextView textViewTS;
        TextView textViewUnread;
        RelativeLayout relativeLayout;
        NotificationCountModel notificationCountModel;
    }
}
