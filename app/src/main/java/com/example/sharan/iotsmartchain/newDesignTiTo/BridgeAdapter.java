package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
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
import com.example.sharan.iotsmartchain.model.BridgeModel;

import java.util.ArrayList;

public class BridgeAdapter extends ArrayAdapter<BridgeModel> {

    private Context mContext;
    private ArrayList<BridgeModel> mGatewayDeviceList;
    private BridgeModel bridgeModel = new BridgeModel();
    private ViewHolder viewHolder = new ViewHolder();

    public BridgeAdapter(@NonNull Context context, ArrayList<BridgeModel> list) {
        super(context, R.layout.row_gateway_item);
        this.mContext = context;
        this.mGatewayDeviceList = list;
    }

    @Override
    public int getCount() {
        return mGatewayDeviceList.size();
    }

    @Nullable
    @Override
    public BridgeModel getItem(int position) {
        return mGatewayDeviceList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = null;
        bridgeModel = new BridgeModel();
        bridgeModel = mGatewayDeviceList.get(position);
        try {

            if (convertView == null) {
                viewHolder = new ViewHolder();
                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row_gateway_item, parent, false);
                viewHolder.cardView = (CardView) convertView.findViewById(R.id.cardview_gateway);
                viewHolder.imageViewDeviceIcon = (ImageView) convertView.findViewById(R.id.image_icon);
                viewHolder.tvGatewayLabel = (TextView) convertView.findViewById(R.id.text_label_val);
                viewHolder.tvGatewayUID = (TextView) convertView.findViewById(R.id.text_gateway);
                viewHolder.tvStatus = (TextView) convertView.findViewById(R.id.text_status);
                viewHolder.tvDeviceTimeStamp = (TextView) convertView.findViewById(R.id.text_timestamp);
                viewHolder.imageViewInfo = (ImageView) convertView.findViewById(R.id.image_info);
                viewHolder.imageViewArrow = (ImageView) convertView.findViewById(R.id.image_arrow);
                viewHolder.imageSetting = (ImageView) convertView.findViewById(R.id.image_setting);
                viewHolder.imageViewArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  Toast.makeText(mContext, " On click of arrow option", Toast.LENGTH_SHORT).show();
                    }
                });
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }

        viewHolder.bridgeModel = getItem(position);
        Log.d("SHARAN ", "" + viewHolder.bridgeModel.toString());

        if (viewHolder.bridgeModel != null) {
            /*device type like END or GATEWAY */
            if (viewHolder.bridgeModel.getGatewayUid() != null)
                viewHolder.tvGatewayUID.setText(viewHolder.bridgeModel.getGatewayUid().toString().trim());
            /*Iot device ID */
            if (viewHolder.bridgeModel.getGatewayLabel() != null)
                viewHolder.tvGatewayLabel.setText(viewHolder.bridgeModel.getGatewayLabel().toString());
            /*Status*/
            if (viewHolder.bridgeModel.isConfigure() != false) {
                viewHolder.tvStatus.setText("Configured");
                viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#ccede2"));
            } else {
                viewHolder.tvStatus.setText("Installed");
                viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#e9fff9c4"));
            }
            /*Time Stamp*/
            if (viewHolder.bridgeModel.getTimeStamp() != -1)
                viewHolder.tvDeviceTimeStamp.setText(Utils.convertTime(viewHolder.bridgeModel.getTimeStamp()));

            viewHolder.imageViewInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.bridgeModel = getItem(position);
                    Log.e("SH : ", viewHolder.bridgeModel.toString());

                    if (viewHolder.bridgeModel != null)
                        showDialog(viewHolder.bridgeModel);
                }
            });

            viewHolder.imageSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  Toast.makeText(mContext, " Onn click of Setting option", Toast.LENGTH_SHORT).show();
                    viewHolder.bridgeModel = getItem(position);
                    Intent intent = new Intent(mContext, BridgeSettingActivity.class);
                    intent.putExtra("BRIDGE", viewHolder.bridgeModel);
                    mContext.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    private void showDialog(BridgeModel bridgeModel) {
        // Build an AlertDialog
        Dialog dialog = new Dialog(mContext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        editDesp.setVisibility(View.GONE);

        builder.setView(rootView);

        textTitle.setText("Bridge Info");
        if (bridgeModel != null) {
            editUID.setText(bridgeModel.getGatewayUid());
            editLabel.setText(bridgeModel.getGatewayLabel());
            textLat.setText("" + bridgeModel.getLatitude());
            textLng.setText("" + bridgeModel.getLongitude());
            textAdd.setText(bridgeModel.getAddress());
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
        CardView cardView;
        ImageView imageViewDeviceIcon;
        TextView tvGatewayUID;
        TextView tvGatewayLabel;
        TextView tvStatus;
        ImageView imageViewInfo;
        ImageView imageViewArrow;
        TextView tvDeviceTimeStamp;
        ImageView imageSetting;
        BridgeModel bridgeModel;
    }
}
