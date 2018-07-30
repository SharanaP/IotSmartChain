package com.example.sharan.iotsmartchain.dashboard.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.model.SpatialDataModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpatialAdapter extends ArrayAdapter<SpatialDataModel> {
    private static String TAG = SpatialAdapter.class.getSimpleName();
    private Context context;
    private List<SpatialDataModel> mDataList;
    private ViewHolder viewHolder;

    public SpatialAdapter(@NonNull Context context, ArrayList<SpatialDataModel> mList) {
        super(context, R.layout.row_spatial_item);
        this.context = context;
        this.mDataList = mList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = null;
        SpatialDataModel spatialDataModel = null;

        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_spatial_item, parent, false);

                viewHolder.mTvTitle = (TextView) convertView.findViewById(R.id.textView_module_title);
                viewHolder.mTvMac = (TextView) convertView.findViewById(R.id.textView_mac);
                viewHolder.mTvDetails = (TextView) convertView.findViewById(R.id.textView_details);
                viewHolder.mTvStatus = (TextView) convertView.findViewById(R.id.textView_status);
                viewHolder.mTvTimeStamp = (TextView) convertView.findViewById(R.id.textView_time);
                viewHolder.mImageLocation = (CircleImageView) convertView.findViewById(R.id.imageView_location);
                viewHolder.mImageLocation.setVisibility(View.INVISIBLE);
                viewHolder.mImageFloorPlan = (ImageView)convertView.findViewById(R.id.imageView_floorplan);
                viewHolder.mImageFloorPlan.setVisibility(View.VISIBLE);
                viewHolder.mImageLocation.setTag(viewHolder);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }

        viewHolder.spatialDataModel = getItem(position);

        if (viewHolder.spatialDataModel != null) {
            if (viewHolder.spatialDataModel.getType() != null) {
                viewHolder.mTvTitle.setText(viewHolder.spatialDataModel.getType());
            }
            if (viewHolder.spatialDataModel.get_Id() != null) {
                viewHolder.mTvMac.setText(viewHolder.spatialDataModel.get_Id());
            }
            if (viewHolder.spatialDataModel.getDetails() != null) {
                String text = viewHolder.spatialDataModel.getDetails();
                viewHolder.mTvDetails.setText(text);
            }
            if (viewHolder.spatialDataModel.getStatus() != null) {
                if (viewHolder.spatialDataModel.getStatus().equalsIgnoreCase("true")) {
                    viewHolder.mTvStatus.setText("Active");
                    viewHolder.mTvStatus.setTextColor(Color.parseColor("#2CCC44"));
                } else {
                    viewHolder.mTvStatus.setText("Inactive");
                    viewHolder.mTvStatus.setTextColor(Color.parseColor("#CC2C2C"));
                }
            }
            if (viewHolder.spatialDataModel.getTimeStamp() != null) {
                String time = Utils.convertTime(Long.parseLong(viewHolder.spatialDataModel.getTimeStamp()));
                viewHolder.mTvTimeStamp.setText(time);
            }
//            if (!ViewHolder.spatialDataModel.getLongitude().isEmpty()
//                    && !ViewHolder.spatialDataModel.getLatitude().isEmpty()) {
//                //TODO ViewHolder.mImageLocation based on longitude and latitude goto map
//                ViewHolder.mImageLocation.setVisibility(View.VISIBLE);
//            }else{
//                ViewHolder.mImageLocation.setVisibility(View.GONE);
//            }

            /*Floor plan */
            viewHolder.mImageFloorPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Floor plan clicked ");
                }
            });

        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Nullable
    @Override
    public SpatialDataModel getItem(int position) {
        return mDataList.get(position);
    }

    public static class ViewHolder {
        TextView mTvTitle;
        TextView mTvMac;
        TextView mTvStatus;
        TextView mTvTimeStamp;
        TextView mTvDetails;
        CircleImageView mImageLocation;
        ImageView mImageFloorPlan;
        SpatialDataModel spatialDataModel;
    }
}