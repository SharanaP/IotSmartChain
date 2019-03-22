package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.model.BridgeModel;

/**
 * Created by sharan
 */

public class BridgeBottomSheetFragment extends BottomSheetDialogFragment {

    private BridgeModel bridgeModel;
    private TextView textViewTitle;
    private TextView textViewInfo;
    private ImageView imageViewEdit;
    private ImageView imageViewDelete;
    private ImageView imageViewCancel;
    private BridgeInterface bridgeInterface;

    //Bottom Sheet Callback
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback
            = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public BridgeBottomSheetFragment() {
    }

    @SuppressLint("ValidFragment")
    public BridgeBottomSheetFragment(BridgeInterface bridgeInterface) {
        this.bridgeInterface = bridgeInterface;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (textViewTitle != null)
            textViewTitle.setText("Update and Delete options for Bridge device.");
        bridgeModel = (BridgeModel) bundle.getSerializable("GATEWAY");
        Log.e("BridgeBottomSheetFragment : ", bridgeModel.toString());
        if (textViewInfo != null && bridgeModel != null) {
            textViewInfo.setText("Bridge : " + bridgeModel.getGatewayUid());
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Get the content View
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);
        textViewTitle = (TextView) contentView.findViewById(R.id.textview_title);
        textViewInfo = (TextView) contentView.findViewById(R.id.text_bridge_info);
        imageViewEdit = (ImageView) contentView.findViewById(R.id.fab_edit);
        imageViewDelete = (ImageView) contentView.findViewById(R.id.fab_delete);
        imageViewCancel = (ImageView) contentView.findViewById(R.id.fab_cancel);
        dialog.setContentView(contentView);

        //set bridge info
        if (bridgeModel != null) {
            textViewInfo.setText("Bridge :\t" + bridgeModel.getGatewayUid());
            textViewTitle.setText("Update and Delete options for Bridge device.");
        }

        //Set the coordinator layout behavior
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        //Set callback
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bridgeInterface.onClickBridgeOption(0);
                dialog.dismiss();
            }
        });

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bridgeInterface.onClickBridgeOption(1);
                dialog.dismiss();
            }
        });

        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bridgeInterface.onClickBridgeOption(2);
                dialog.dismiss();
            }
        });
    }
}
