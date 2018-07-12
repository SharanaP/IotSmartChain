package com.example.sharan.iotsmartchain.dashboard.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.model.AnalyticsDataModel;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import static android.media.CamcorderProfile.get;

public class AnalyticsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<AnalyticsDataModel> mParentList;
    private HashMap<String, AnalyticsDataModel> mChildHashMap;

    public AnalyticsAdapter(Context context, List<AnalyticsDataModel> mParentList,
                            HashMap<String, AnalyticsDataModel> mChildHashMap) {
        this.context = context;
        this.mParentList = mParentList;
        this.mChildHashMap = mChildHashMap;
    }

    @Override
    public int getGroupCount() {
        return mParentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        AnalyticsDataModel analyticsDataModel = this.mChildHashMap.get(this.mParentList.get(groupPosition).get_Id());
        return 1;

        //this._listDataChild.get(this._listDataHeader.get(groupPosition))
        //                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        Log.e("SH : ", " getGroup : "+mParentList.get(groupPosition));

        return mParentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.e("SH : ", " getChild : "+this.mChildHashMap.get(this.mParentList.get(childPosition).get_Id()));
        return this.mChildHashMap.get(this.mParentList.get(groupPosition).get_Id());

        //this._listDataChild.get(this._listDataHeader.get(groupPosition))
        //                .get(childPosititon);
     }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        AnalyticsDataModel analyticsDataModel = (AnalyticsDataModel)getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parent_layout_analytics, null);
        }
        TextView tvTitle = (TextView)convertView.findViewById(R.id.textview_title);
        TextView tvTimeStamp = (TextView)convertView.findViewById(R.id.tetxview_timestamp);
        TextView tvReadMore = (TextView)convertView.findViewById(R.id.textview_readmore);
        ImageView imageIndicator = (ImageView)convertView.findViewById(R.id.imageview_indicator);

        if(analyticsDataModel != null){
            tvTitle.setText(analyticsDataModel.getType());
            String timeStamp = Utils.convertTime(Long.parseLong(analyticsDataModel.getTimeStamp()));
            tvTimeStamp.setText(timeStamp);
        }

        if (isExpanded) {
            imageIndicator.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            tvReadMore.setText("Read less");
            tvTimeStamp.setVisibility(View.INVISIBLE);
        } else {
            imageIndicator.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
            tvReadMore.setText("Read more");
            tvTimeStamp.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        AnalyticsDataModel analyticsDataModel = (AnalyticsDataModel)getChild(groupPosition, childPosition);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_layout_analytics, null);
        }

        TextView tvTemp = (TextView)convertView.findViewById(R.id.textview_temp);
        TextView tvNumOfDoorOpen = (TextView)convertView.findViewById(R.id.textview_numofdooropen);
        TextView tvHumidity = (TextView)convertView.findViewById(R.id.textview_humidity);
        TextView tvDetails = (TextView)convertView.findViewById(R.id.textview_details);
        TextView tvTimeStamp = (TextView)convertView.findViewById(R.id.textView_timeFormat);

        if(analyticsDataModel != null){
            tvTemp.setText("Temperature : "+analyticsDataModel.getTemperature());
            tvNumOfDoorOpen.setText(analyticsDataModel.getNumOfDoorOpen());
            tvHumidity.setText("Humidity : "+analyticsDataModel.getHumidity());
            tvDetails.setText("Details : "+analyticsDataModel.getDetails());
            tvTimeStamp.setText(Utils.convertTime(Long.parseLong(analyticsDataModel.getTimeStamp())));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
