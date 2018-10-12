package com.example.sharan.iotsmartchain.NormalFlow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.model.NonSpatialModel;

import java.util.ArrayList;
import java.util.Map;

public class AdapterNonSpatialPlan extends ArrayAdapter<NonSpatialModel> {
    private Context context;
    private Map<String, NonSpatialModel> modelMap;
    private ArrayList<NonSpatialModel> arrayList;
    private NonSpatialModel nonSpatialModel = new NonSpatialModel();
    private ViewHolder viewHolder = new ViewHolder();

    public AdapterNonSpatialPlan(@NonNull Context context, ArrayList<NonSpatialModel> arrayList) {
        super(context, R.layout.row_new_non_spatial_item);
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public void add(@Nullable NonSpatialModel object) {
        super.add(object);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Nullable
    @Override
    public NonSpatialModel getItem(int position) {
        return arrayList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = null;
        nonSpatialModel = new NonSpatialModel();
        nonSpatialModel = arrayList.get(position);

        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_new_non_spatial_item, parent, false);

                //init ui
                viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.textView_type);
                viewHolder.mImageViewIcon = (ImageView) convertView.findViewById(R.id.imageview_icon_iot);
                viewHolder.mTextViewSerialNum = (TextView) convertView.findViewById(R.id.textview_iot_sn);
                viewHolder.mTextViewLabel = (TextView) convertView.findViewById(R.id.textview_label_val);
                viewHolder.mTextViewDescription = (TextView) convertView.findViewById(R.id.textview_des);
                viewHolder.mTextViewStatus = (TextView) convertView.findViewById(R.id.textview_status);
                viewHolder.mTextViewTimeStamp = (TextView) convertView.findViewById(R.id.textView_timeStamp);

                viewHolder.mTextViewTimeStamp.setTag(viewHolder);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }

        viewHolder.nonSpatialModel = arrayList.get(position);

        if (viewHolder.nonSpatialModel != null) {
            //set all values
            if (!TextUtils.isEmpty(viewHolder.nonSpatialModel.getIotDeviceSerialNum())
                    && viewHolder.nonSpatialModel.getIotDeviceSerialNum() != null) {
                String iotDeviceType = viewHolder.nonSpatialModel.getIotDeviceSerialNum();
                if (!TextUtils.isEmpty(iotDeviceType) && iotDeviceType != null) {
                    viewHolder.mTextViewSerialNum.setText(viewHolder.nonSpatialModel.getIotDeviceSerialNum());
                    if (iotDeviceType.contains("GES"))
                        viewHolder.mTextViewTitle.setText("IoT End Node device");
                    else if (iotDeviceType.contains("GGS"))
                        viewHolder.mTextViewTitle.setText("GateWay device");
                }
            }
            if (!TextUtils.isEmpty(viewHolder.nonSpatialModel.getLabel())
                    && viewHolder.nonSpatialModel.getLabel() != null)
                viewHolder.mTextViewLabel.setText(viewHolder.nonSpatialModel.getLabel());
            if (!TextUtils.isEmpty(viewHolder.nonSpatialModel.getDescription())
                    && viewHolder.nonSpatialModel.getDescription() != null)
                viewHolder.mTextViewDescription.setText(viewHolder.nonSpatialModel.getDescription());
            if (!TextUtils.isEmpty(viewHolder.nonSpatialModel.getStatus())
                    && viewHolder.nonSpatialModel.getStatus() != null)
                viewHolder.mTextViewStatus.setText(viewHolder.nonSpatialModel.getStatus());
            if (!TextUtils.isEmpty(viewHolder.nonSpatialModel.getTimeStamp())
                    && viewHolder.nonSpatialModel.getTimeStamp() != null)
                viewHolder.mTextViewTimeStamp.setText(viewHolder.nonSpatialModel.getTimeStamp());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView mTextViewTitle;
        ImageView mImageViewIcon;
        TextView mTextViewSerialNum;
        TextView mTextViewLabel;
        TextView mTextViewDescription;
        TextView mTextViewStatus;
        TextView mTextViewTimeStamp;
        NonSpatialModel nonSpatialModel;
    }
}

