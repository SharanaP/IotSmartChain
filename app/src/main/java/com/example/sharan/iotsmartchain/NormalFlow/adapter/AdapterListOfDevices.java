package com.example.sharan.iotsmartchain.NormalFlow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.model.DeviceInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sharan on 23-03-2018.
 */

public class AdapterListOfDevices extends ArrayAdapter<DeviceInfo> {

    private Context mContext;
    private ArrayList<DeviceInfo> mListOfDevices;
    private DeviceInfo mDeviceInfo = new DeviceInfo();
    private ViewHolder viewHolder;

    public AdapterListOfDevices(Context context,
                                Map<String, DeviceInfo> mList) {
        super(context, R.layout.row_device_item);
        this.mContext = context;
        this.mListOfDevices = new ArrayList<>(mList.values());

        /*Get Login token id and email id*/

    }

    @Override
    public int getCount() {
        return mListOfDevices.size();
    }

    @Nullable
    @Override
    public DeviceInfo getItem(int position) {
        return mListOfDevices.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = null;

        mDeviceInfo = mListOfDevices.get(position);
        try {

            if (convertView == null) {
                viewHolder = new ViewHolder();

                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_device_item, parent, false);

                viewHolder.imageViewDeviceIcon = (CircleImageView) convertView.findViewById(R.id.imageViewDevice);
                viewHolder.tvDeviceTitle = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tvDeviceSubTitle = (TextView) convertView.findViewById(R.id.tv_sub_title);
                viewHolder.imageViewBatteryIcon = (ImageView) convertView.findViewById(R.id.image_battery_icon);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }


        Picasso.get().load(R.drawable.sensor_icon)
                /*.networkPolicy(NetworkPolicy.NO_CACHE)*/
                .placeholder(R.drawable.sensor_icon)
                .into(viewHolder.imageViewDeviceIcon);
        viewHolder.tvDeviceTitle.setText(mDeviceInfo.getDeviceId().trim());
        viewHolder.tvDeviceSubTitle.setText(mDeviceInfo.getBatteryStatus().trim());
        return convertView;

    }

    static class ViewHolder {
        CircleImageView imageViewDeviceIcon;
        TextView tvDeviceTitle;
        TextView tvDeviceSubTitle;
        ImageView imageViewBatteryIcon;
    }

}




