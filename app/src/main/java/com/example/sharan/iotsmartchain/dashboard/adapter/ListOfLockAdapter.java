package com.example.sharan.iotsmartchain.dashboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.model.DeviceLockerModel;

import java.util.ArrayList;
import java.util.List;

public class ListOfLockAdapter extends ArrayAdapter<DeviceLockerModel> {

    private static String TAG = ListOfLockAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<DeviceLockerModel> mDeviceLockList = new ArrayList<>();
    private DeviceLockerModel deviceLockerModel = new DeviceLockerModel();
    private ViewHolder viewHolder;

    public ListOfLockAdapter(@NonNull Context context, ArrayList<DeviceLockerModel> list) {
        super(context, R.layout.row_device_lock_item);
        this.mContext = context;
        this.mDeviceLockList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = null;
        deviceLockerModel = new DeviceLockerModel();

        deviceLockerModel = getItem(position);

        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_device_lock_item, parent, false);

                viewHolder.mTvDeviceName = (TextView) convertView.findViewById(R.id.textView_lock_item);
                viewHolder.mSwitch = (SwitchCompat) convertView.findViewById(R.id.switch_lock);

                viewHolder.mSwitch.setTag(viewHolder);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }

        viewHolder.deviceLockerModel = getItem(position);

        if (viewHolder.deviceLockerModel != null) {
            if (viewHolder.deviceLockerModel.getDeviceType() != null) {
                viewHolder.mTvDeviceName.setText(viewHolder.deviceLockerModel.getDeviceType());
            }

            if (viewHolder.deviceLockerModel.isLocked()) {
                viewHolder.mSwitch.setChecked(true);
            } else {
                viewHolder.mSwitch.setChecked(false);
            }

            if(viewHolder.deviceLockerModel.isMasterLocked()){
                viewHolder.mSwitch.setEnabled(false);
                viewHolder.mSwitch.setChecked(true);
            }else{
                viewHolder.mSwitch.setEnabled(true);
                viewHolder.mSwitch.setChecked(false);
            }
        }

        viewHolder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO send switch status to server  API
                if (isChecked) {
                    Log.d(TAG, "" + isChecked);
                } else {

                }
            }
        });
        return convertView;
    }

    @Nullable
    @Override
    public DeviceLockerModel getItem(int position) {
        return mDeviceLockList.get(position);
    }


    @Override
    public int getCount() {
        return mDeviceLockList.size();
    }

    public static class ViewHolder {
        TextView mTvDeviceName;
        SwitchCompat mSwitch;
        DeviceLockerModel deviceLockerModel;
    }

}
