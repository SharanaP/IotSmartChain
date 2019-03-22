package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.model.EndNodeModel;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NodeAdapter extends ArrayAdapter<EndNodeModel> {
    private Context context;
    private Map<String, EndNodeModel> modelMap;
    private ArrayList<EndNodeModel> arrayList;
    private EndNodeModel endNodeModel = new EndNodeModel();
    private ViewHolder viewHolder = new ViewHolder();

    public NodeAdapter(@NonNull Context context, ArrayList<EndNodeModel> arrayList) {
        super(context, R.layout.row_node_item);
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public void add(@Nullable EndNodeModel object) {
        super.add(object);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Nullable
    @Override
    public EndNodeModel getItem(int position) {
        return arrayList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = null;
        endNodeModel = new EndNodeModel();
        endNodeModel = arrayList.get(position);

        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_node_item, parent, false);

                //init ui
                viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.textView_type);
                viewHolder.mImageViewIcon = (ImageView) convertView.findViewById(R.id.imageview_icon_iot);
                viewHolder.mTextViewSerialNum = (TextView) convertView.findViewById(R.id.textview_iot_sn);
                viewHolder.mTextViewLabel = (TextView) convertView.findViewById(R.id.textview_label_val);
                viewHolder.mTextViewDescription = (TextView) convertView.findViewById(R.id.textview_des);
                viewHolder.mTextViewStatus = (TextView) convertView.findViewById(R.id.textview_status);
                viewHolder.mTextViewTimeStamp = (TextView) convertView.findViewById(R.id.textView_timeStamp);
                viewHolder.mImageInfo = (CircleImageView) convertView.findViewById(R.id.image_info);
                viewHolder.mImageMore = (ImageView) convertView.findViewById(R.id.image_setting);

                viewHolder.mTextViewTimeStamp.setTag(viewHolder);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }

        viewHolder.endNodeModel = arrayList.get(position);

        if (viewHolder.endNodeModel != null) {
            //set all values
            if (!TextUtils.isEmpty(viewHolder.endNodeModel.getEndNodeUid())
                    && viewHolder.endNodeModel.getEndNodeUid() != null) {
                String iotDeviceType = viewHolder.endNodeModel.getEndNodeUid();
                if (!TextUtils.isEmpty(iotDeviceType) && iotDeviceType != null) {
                    viewHolder.mTextViewSerialNum.setText(viewHolder.endNodeModel.getEndNodeUid());
                    if (iotDeviceType.contains("GES"))
                        viewHolder.mTextViewTitle.setText("End Node");
                }
            }
            if (!TextUtils.isEmpty(viewHolder.endNodeModel.getLabel())
                    && viewHolder.endNodeModel.getLabel() != null)
                viewHolder.mTextViewLabel.setText(viewHolder.endNodeModel.getLabel());
            if (!TextUtils.isEmpty(viewHolder.endNodeModel.getDescription())
                    && viewHolder.endNodeModel.getDescription() != null)
                viewHolder.mTextViewDescription.setText(viewHolder.endNodeModel.getDescription());
            if (!TextUtils.isEmpty(viewHolder.endNodeModel.getStatus())
                    && viewHolder.endNodeModel.getStatus() != null)
                viewHolder.mTextViewStatus.setText(viewHolder.endNodeModel.getStatus());
            if ((viewHolder.endNodeModel.getTimeStamp() != -1)
                    && viewHolder.endNodeModel.getTimeStamp() != -1)
                viewHolder.mTextViewTimeStamp.setText(Utils.convertTime(viewHolder.endNodeModel.getTimeStamp()));

            viewHolder.mImageInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Info", Toast.LENGTH_SHORT).show();
                    viewHolder.endNodeModel = getItem(position);

                    if (viewHolder.endNodeModel != null) {
                        showDialog(viewHolder.endNodeModel);
                    }
                }
            });

            viewHolder.mImageMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.endNodeModel = getItem(position);
                    Toast.makeText(context, "Setting", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, NodeSettingActivity.class);
                    intent.putExtra("NODE", viewHolder.endNodeModel);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    private void showDialog(EndNodeModel endNodeModel) {
        // Build an AlertDialog
        Dialog dialog = new Dialog(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = layoutInflater.inflate(R.layout.dialog_bridge_info, null);

        TextView textTitle = (TextView) rootView.findViewById(R.id.textView_title);
        EditText editUID = (EditText) rootView.findViewById(R.id.edittext_uid);
        EditText editLabel = (EditText) rootView.findViewById(R.id.edittext_label);
        EditText editDesp = (EditText) rootView.findViewById(R.id.edittext_desp);
        TextView textLat = (TextView) rootView.findViewById(R.id.textview_lat);
        TextView textLng = (TextView) rootView.findViewById(R.id.textview_lng);
        TextView textAdd = (TextView) rootView.findViewById(R.id.textview_add);

        editUID.setEnabled(false);
        editLabel.setEnabled(false);
        editDesp.setEnabled(false);

        builder.setView(rootView);

        textTitle.setText("Node Info");
        if (endNodeModel != null) {
            editUID.setText(endNodeModel.getEndNodeUid());
            editLabel.setText(endNodeModel.getLabel());
            editDesp.setText(endNodeModel.getDescription());
            textLat.setText("" + endNodeModel.getLatitude());
            textLng.setText("" + endNodeModel.getLongitude());
            textAdd.setText(endNodeModel.getAddress());
        }

        // Set click listener for alert dialog buttons
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // User clicked the Yes button
                        builder.setMessage("");
                        break;
                }
            }
        };

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Ok", dialogClickListener);

        dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

    static class ViewHolder {
        TextView mTextViewTitle;
        ImageView mImageViewIcon;
        TextView mTextViewSerialNum;
        TextView mTextViewLabel;
        TextView mTextViewDescription;
        TextView mTextViewStatus;
        TextView mTextViewTimeStamp;
        CircleImageView mImageInfo;
        ImageView mImageMore;
        EndNodeModel endNodeModel;
    }
}

