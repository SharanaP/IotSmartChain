package com.example.sharan.iotsmartchain.WifiNetwork;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;

import java.util.List;

public class ScannedWiFiAdapter extends ArrayAdapter<ScanResult> {

    private Context mContext;
    private List<ScanResult> mScannedWifiList;
    private ScanResult scanResult=null;
    private ViewHolder viewHolder;

    public ScannedWiFiAdapter(@NonNull Context context, List<ScanResult> list) {
        super(context, R.layout.row_wifi_item);
        this.mContext = context;
        this.mScannedWifiList = list;
    }

    @Override
    public int getCount() {
        return mScannedWifiList.size();
    }

    @Nullable
    @Override
    public ScanResult getItem(int position) {
        return mScannedWifiList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = null;

        scanResult = mScannedWifiList.get(position);
        try {

            if (convertView == null) {
                viewHolder = new ViewHolder();

                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_wifi_item, parent, false);

                viewHolder.mTextViewBatteryLabel = (TextView) convertView.findViewById(R.id.textView_wifi_title);
                viewHolder.mImageViewBatteryIcon = (ImageView) convertView.findViewById(R.id.image_wifi_icon);

                viewHolder.mImageViewBatteryIcon.setTag(viewHolder);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }

        viewHolder.scanResult = getItem(position);
        Log.d("SHARAN ", "" + viewHolder.scanResult.toString());

        if (viewHolder.scanResult != null) {
            if (viewHolder.mTextViewBatteryLabel != null)
                viewHolder.mTextViewBatteryLabel.setText(viewHolder.scanResult.SSID.toString());
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView mTextViewBatteryLabel;
        ImageView mImageViewBatteryIcon;
        ScanResult scanResult;
    }
}
