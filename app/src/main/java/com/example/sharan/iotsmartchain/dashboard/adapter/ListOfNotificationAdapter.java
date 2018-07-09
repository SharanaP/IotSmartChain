package com.example.sharan.iotsmartchain.dashboard.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class ListOfNotificationAdapter extends ArrayAdapter<NotificationModel> {

    private Context mContext;
    private List<NotificationModel> mNotificationsList;
    private NotificationModel mNotificationModel = new NotificationModel();
    private ViewHolder viewHolder;

    public ListOfNotificationAdapter(Context context, List<NotificationModel> mList) {
        super(context, R.layout.row_notification_item);
        this.mContext = context;
        this.mNotificationsList = mList;
    }

    @Override
    public int getCount() {
        return mNotificationsList.size();
    }

    @Nullable
    @Override
    public NotificationModel getItem(int position) {
        return mNotificationsList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = null;

        mNotificationModel = new NotificationModel();

        mNotificationModel = mNotificationsList.get(position);
        try {

            if (convertView == null) {
                viewHolder = new ViewHolder();

                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_notification_item, parent, false);

                viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.textView_model_name);
                viewHolder.mTextViewBody = (TextView) convertView.findViewById(R.id.textView_body);
                viewHolder.mTextViewStatus = (TextView) convertView.findViewById(R.id.textView_status);
                viewHolder.mTextViewTimeStamp = (TextView) convertView.findViewById(R.id.textView_timeStamp);
                viewHolder.mTextViewDetails = (TextView) convertView.findViewById(R.id.textView_details);

                viewHolder.mTextViewDetails.setTag(viewHolder);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }

        viewHolder.notificationModel = getItem(position);
        Log.d("SHARAN ", "" + viewHolder.notificationModel.toString());

        if (viewHolder.notificationModel != null) {
            if (viewHolder.notificationModel.getTitle() != null)
                viewHolder.mTextViewTitle.setText("Name : " + viewHolder.notificationModel.getTitle().trim());

            if (viewHolder.notificationModel.getBody() != null)
                viewHolder.mTextViewBody.setText("Type : " + viewHolder.notificationModel.getBody().trim());

//            if (viewHolder.notificationModel.isStatus())
                viewHolder.mTextViewStatus.setText("Status : " + viewHolder.notificationModel.isStatus());

            if (viewHolder.notificationModel.getTimeStamp() != null){

                Long timeStamp = Long.parseLong(viewHolder.notificationModel.getTimeStamp());
                String dateFormat = Utils.convertTime(timeStamp);

                viewHolder.mTextViewTimeStamp.setText(dateFormat);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView mTextViewTitle;
        TextView mTextViewBody;
        TextView mTextViewDetails;
        TextView mTextViewTimeStamp;
        TextView mTextViewStatus;
        NotificationModel notificationModel;
    }

}

