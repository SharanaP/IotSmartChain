package com.example.sharan.iotsmartchain.NormalFlow.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.model.RegisterIoTInfo;

import java.util.ArrayList;

public class AdapterRegisterIoTDevices extends ArrayAdapter<RegisterIoTInfo> {

    private Context mContext;
    private ArrayList<RegisterIoTInfo> mRegIoTDeviceList;
    private RegisterIoTInfo mRegisterIoTInfo = new RegisterIoTInfo();
    private ViewHolder viewHolder;

    public AdapterRegisterIoTDevices(Context context, ArrayList<RegisterIoTInfo> mList) {
        super(context, R.layout.row_reg_iot);
        this.mContext = context;
        this.mRegIoTDeviceList = mList;
    }

    @Override
    public int getCount() {
        return mRegIoTDeviceList.size();
    }

    @Nullable
    @Override
    public RegisterIoTInfo getItem(int position) {
        return mRegIoTDeviceList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = null;

        mRegisterIoTInfo = new RegisterIoTInfo();

        mRegisterIoTInfo = mRegIoTDeviceList.get(position);
        try {

            if (convertView == null) {
                viewHolder = new ViewHolder();

                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_reg_iot, parent, false);

                viewHolder.imageViewDeviceIcon = (ImageView) convertView.findViewById(R.id.icon_iot);
                viewHolder.tvDeviceName = (TextView) convertView.findViewById(R.id.textView_name);
                viewHolder.tvDeviceType = (TextView) convertView.findViewById(R.id.textView_type);
                viewHolder.tvRegStatus  = (TextView) convertView.findViewById(R.id.textView_iot_status);
                viewHolder.tvDetails = (TextView) convertView.findViewById(R.id.textView_details);
                viewHolder.tvDeviceTimeStamp = (TextView) convertView.findViewById(R.id.textView_timeStamp);

                viewHolder.tvDetails.setTag(viewHolder);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }

        viewHolder.registerIoTInfo = getItem(position);
        Log.d("SHARAN ", ""+viewHolder.registerIoTInfo.toString());

        if(viewHolder.registerIoTInfo != null){
            if(viewHolder.registerIoTInfo.getSensorName() != null)
                viewHolder.tvDeviceName.setText("Name : "+viewHolder.registerIoTInfo.getSensorName().trim());
            if(viewHolder.registerIoTInfo.getSensorType() != null)
                viewHolder.tvDeviceType.setText("Type : "+viewHolder.registerIoTInfo.getSensorType().trim());
            if(viewHolder.registerIoTInfo.getSensorStatus() != null)
                viewHolder.tvRegStatus.setText("Status : "+viewHolder.registerIoTInfo.getSensorStatus().trim());
            if(viewHolder.registerIoTInfo.getTimeStamp() != null )
                viewHolder.tvDeviceTimeStamp.setText(viewHolder.registerIoTInfo.getTimeStamp().trim());
            if(viewHolder.registerIoTInfo.getSensorDetails()!= null ){
                viewHolder.tvDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO display a dialog
                        if(getItem(position).getSensorDetails() != null)
                        showDialog(getItem(position).getSensorDetails().toString());
                    }
                });
            }
        }

        return convertView;
    }

    private void showDialog(String details){
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        // Set a title for alert dialog
        builder.setTitle("Register Iot Device details");

        // Ask the final question
        builder.setMessage(details);

        // Set click listener for alert dialog buttons
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // User clicked the Yes button
                        builder.setMessage("");
                        break;
                }
            }
        };

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Ok", dialogClickListener);

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

    static class ViewHolder {
        ImageView imageViewDeviceIcon;
        TextView tvDeviceName;
        TextView tvDeviceType;
        TextView tvRegStatus;
        TextView tvDetails;
        TextView tvDeviceTimeStamp;
        RegisterIoTInfo registerIoTInfo;
    }
}
