package com.example.sharan.iotsmartchain.dashboard.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.model.NotificationDetailModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListOfNotificationAdapter extends ArrayAdapter<NotificationDetailModel> {
    private static String TAG = ListOfNotificationAdapter.class.getSimpleName();
    private Context mContext;
    private List<NotificationDetailModel> mNotificationsList;
    private NotificationDetailModel mNotificationModel = new NotificationDetailModel();
    private ViewHolder viewHolder;

    public ListOfNotificationAdapter(Context context, List<NotificationDetailModel> mList) {
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
    public NotificationDetailModel getItem(int position) {
        return mNotificationsList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = null;
        mNotificationModel = new NotificationDetailModel();
        mNotificationModel = mNotificationsList.get(position);
        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_notification_item, parent, false);
                viewHolder.mCardView = (CardView) convertView.findViewById(R.id.cardView_notify);
                viewHolder.mImageViewIcon = (ImageView) convertView.findViewById(R.id.icon_iot);
                viewHolder.mTextViewLabel = (TextView) convertView.findViewById(R.id.textView_label);
                viewHolder.mTextViewIotSn = (TextView) convertView.findViewById(R.id.textView_iot_sn);
                viewHolder.mTextViewDetails = (TextView) convertView.findViewById(R.id.textView_details);
                viewHolder.mTextViewTimeStamp = (TextView) convertView.findViewById(R.id.textView_timeStamp);
                viewHolder.mImageViewNotifyType = (CircleImageView) convertView.findViewById(R.id.imageview_notify_type);

                viewHolder.mTextViewDetails.setTag(viewHolder);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }

        viewHolder.notificationDetailModel = getItem(position);
        Log.d(TAG, "" + viewHolder.notificationDetailModel.toString());

        if (viewHolder.notificationDetailModel != null) {
            if (viewHolder.notificationDetailModel.getLabel() != null)
                viewHolder.mTextViewLabel.setText(viewHolder.notificationDetailModel.getLabel().trim());

            if (viewHolder.notificationDetailModel.getGatewaySn() != null) {
                if (viewHolder.notificationDetailModel.getIotDeviceSn() != null) {
                    viewHolder.mTextViewIotSn.setText(viewHolder.notificationDetailModel.getGatewaySn().trim() + " / "
                            + viewHolder.notificationDetailModel.getIotDeviceSn().trim());
                    if (viewHolder.notificationDetailModel.isRead()) {
                        viewHolder.mTextViewIotSn.setTextColor(mContext.getResources().getColor(R.color.color_grey_medium));
                    } else {
                        viewHolder.mTextViewIotSn.setTextColor(mContext.getResources().getColor(R.color.color_black_dark));
                    }
                }

            }

            if (viewHolder.notificationDetailModel.getDetails() != null)
                viewHolder.mTextViewDetails.setText(viewHolder.notificationDetailModel.getDetails());

            if (viewHolder.notificationDetailModel.getTimeStamp() != -1) {
                //   Long timeStamp = Long.parseLong(viewHolder.notificationDetailModel.getTimeStamp());
                //  String dateFormat = Utils.convertTime(timeStamp);
                viewHolder.mTextViewTimeStamp.setText(Utils.convertTime(viewHolder.notificationDetailModel.getTimeStamp()));
                viewHolder.mTextViewTimeStamp.setTextColor(mContext.getResources().getColor(R.color.color_cyan));
            }

            if (viewHolder.notificationDetailModel.isRead()) {
                viewHolder.mTextViewTimeStamp.setTextColor(mContext.getResources().getColor(R.color.color_grey_medium));
                viewHolder.mTextViewLabel.setTypeface(null, Typeface.NORMAL);
            } else {
                viewHolder.mTextViewLabel.setTypeface(null, Typeface.BOLD);
                viewHolder.mTextViewTimeStamp.setTextColor(mContext.getResources().getColor(R.color.color_cyan));
            }

            //notification priority
            if (viewHolder.notificationDetailModel.getNotifyType() != -1) {
                int notifyType = viewHolder.notificationDetailModel.getNotifyType();
                Log.d(TAG, "notifyType : " + notifyType);
                switch (notifyType) {
                    case 0:
                        viewHolder.mImageViewNotifyType.setImageDrawable(mContext.getResources().getDrawable(R.drawable.priority_normal));
                        break;
                    case 1:
                        viewHolder.mImageViewNotifyType.setImageDrawable(mContext.getResources().getDrawable(R.drawable.priority_low));
                        break;
                    case 2:
                        viewHolder.mImageViewNotifyType.setImageDrawable(mContext.getResources().getDrawable(R.drawable.priority_medium));
                        break;
                    case 3:
                        viewHolder.mImageViewNotifyType.setImageDrawable(mContext.getResources().getDrawable(R.drawable.priority_high));
                        break;
                }
            }
        }

        return convertView;
    }

    static class ViewHolder {
        CardView mCardView;
        ImageView mImageViewIcon;
        TextView mTextViewLabel;
        TextView mTextViewIotSn;
        TextView mTextViewDetails;
        TextView mTextViewTimeStamp;
        CircleImageView mImageViewNotifyType;
        NotificationDetailModel notificationDetailModel;
    }

}

