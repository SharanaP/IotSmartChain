package com.example.sharan.iotsmartchain.dashboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.model.NotificationModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationsListAdapter.MyViewHolder> {

    private Context context;
    private List<NotificationModel> mList;

    public NotificationsListAdapter(Context context, List<NotificationModel> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NotificationModel notificationModel = mList.get(position);
        holder.mTextViewTitle.setText(notificationModel.getTitle());
        holder.mTextViewBody.setText(notificationModel.getBody());
        holder.mTextViewStatus.setText("Status : "+notificationModel.isStatus());
        holder.mTextViewTimeStamp.setText(notificationModel.getTitle());

        holder.mTextViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO show in details dialog
            }
        });
    }

    @Override

    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewTitle, mTextViewBody,
                mTextViewDetails, mTextViewTimeStamp, mTextViewStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
//            mTextViewTitle = (TextView)itemView.findViewById(R.id.textView_model_name);
//            mTextViewBody = (TextView)itemView.findViewById(R.id.textView_body);
            mTextViewStatus = (TextView)itemView.findViewById(R.id.textView_status);
            mTextViewTimeStamp = (TextView)itemView.findViewById(R.id.textView_timeStamp);
            mTextViewDetails = (TextView)itemView.findViewById(R.id.textView_details);
        }
    }
}




