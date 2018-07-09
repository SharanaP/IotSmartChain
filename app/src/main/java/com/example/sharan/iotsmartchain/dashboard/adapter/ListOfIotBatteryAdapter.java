package com.example.sharan.iotsmartchain.dashboard.adapter;

import android.content.Context;
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
import com.example.sharan.iotsmartchain.model.BatteryModel;

import java.util.List;

public class ListOfIotBatteryAdapter extends ArrayAdapter<BatteryModel> {

    private Context mContext;
    private List<BatteryModel> mBatteryList;
    private BatteryModel mBatteryModel = new BatteryModel();
    private ViewHolder viewHolder;

    public ListOfIotBatteryAdapter(@NonNull Context context, List<BatteryModel> list) {
        super(context, R.layout.row_battery_item);
        this.mContext = context;
        this.mBatteryList = list;
    }

    @Override
    public int getCount() {
        return mBatteryList.size();
    }

    @Nullable
    @Override
    public BatteryModel getItem(int position) {
        return mBatteryList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = null;

        mBatteryModel = new BatteryModel();

        mBatteryModel = mBatteryList.get(position);

        try {

            if (convertView == null) {
                viewHolder = new ViewHolder();

                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_battery_item, parent, false);

                viewHolder.mTextViewBatteryLabel = (TextView) convertView.findViewById(R.id.textView_label);
                viewHolder.mTextViewBatteryValue = (TextView) convertView.findViewById(R.id.textView_batteryValue);
                viewHolder.mImageViewBatteryIcon = (ImageView) convertView.findViewById(R.id.imageView_battery);

                viewHolder.mImageViewBatteryIcon.setTag(viewHolder);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }

        viewHolder.mBatteryModel = getItem(position);
        Log.d("SHARAN ", "" + viewHolder.mBatteryModel.toString());

        if (viewHolder.mBatteryModel != null) {
            int battery = -1;
            if (viewHolder.mTextViewBatteryLabel != null)
                viewHolder.mTextViewBatteryLabel.setText(viewHolder.mBatteryModel.getDeviceType().trim());

            if (viewHolder.mBatteryModel.getBatteryValue() != null) {
                battery = Integer.valueOf(viewHolder.mBatteryModel.getBatteryValue().toString());
                viewHolder.mTextViewBatteryValue.setText(viewHolder.mBatteryModel.getBatteryValue()+"%");
            }

            //TODO set Battery
            if (battery == 0 || battery <= 5) {
                viewHolder.mImageViewBatteryIcon.setImageResource(R.drawable.battery_5_empty);
                viewHolder.mTextViewBatteryValue.setTextColor(getContext().getResources().getColor(R.color.color_battery_empty));
            } else if (battery <= 20 && battery >= 5) {
                viewHolder.mImageViewBatteryIcon.setImageResource(R.drawable.battery_4_low);
                viewHolder.mTextViewBatteryValue.setTextColor(getContext().getResources().getColor(R.color.color_battery_low));
            } else if (battery <= 60 && battery >= 20) {
                viewHolder.mImageViewBatteryIcon.setImageResource(R.drawable.battery_3_med);
                viewHolder.mTextViewBatteryValue.setTextColor(getContext().getResources().getColor(R.color.color_battery_med));
            } else if (battery <= 80 && battery >= 60) {
                viewHolder.mImageViewBatteryIcon.setImageResource(R.drawable.battery_2_avg);
                viewHolder.mTextViewBatteryValue.setTextColor(getContext().getResources().getColor(R.color.color_battery_avg));
            } else if (battery <= 100 && battery >= 80) {
                viewHolder.mImageViewBatteryIcon.setImageResource(R.drawable.battery_1_full);
                viewHolder.mTextViewBatteryValue.setTextColor(getContext().getResources().getColor(R.color.color_battery_full));
            }
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView mTextViewBatteryLabel;
        TextView mTextViewBatteryValue;
        ImageView mImageViewBatteryIcon;
        BatteryModel mBatteryModel;
    }
}
