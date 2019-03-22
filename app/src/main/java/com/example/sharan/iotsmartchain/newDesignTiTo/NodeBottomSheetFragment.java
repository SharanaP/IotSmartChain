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
import com.example.sharan.iotsmartchain.model.EndNodeModel;

public class NodeBottomSheetFragment extends BottomSheetDialogFragment {
    private NodeInterface nodeInterface;
    private TextView textViewTitle;
    private TextView textViewInfo;
    private ImageView imageViewEdit;
    private ImageView imageViewDelete;
    private ImageView imageViewCancel;
    private EndNodeModel endNodeModel;
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

    public NodeBottomSheetFragment() {
    }

    @SuppressLint("ValidFragment")
    public NodeBottomSheetFragment(NodeInterface nodeInterface) {
        this.nodeInterface = nodeInterface;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);
        textViewTitle = (TextView) contentView.findViewById(R.id.textview_title);
        textViewInfo = (TextView) contentView.findViewById(R.id.text_bridge_info);
        imageViewEdit = (ImageView) contentView.findViewById(R.id.fab_edit);
        imageViewDelete = (ImageView) contentView.findViewById(R.id.fab_delete);
        imageViewCancel = (ImageView) contentView.findViewById(R.id.fab_cancel);
        dialog.setContentView(contentView);

        //set bridge info
        if (endNodeModel != null) {
            textViewInfo.setText("Node :\t" + endNodeModel.getEndNodeUid());
            textViewTitle.setText("Update and Delete options for Node device.");
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
                nodeInterface.onClickNodeOption(0);
                dialog.dismiss();
            }
        });

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeInterface.onClickNodeOption(1);
                dialog.dismiss();
            }
        });

        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeInterface.onClickNodeOption(2);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (textViewTitle != null)
            textViewTitle.setText("Update and Delete options for Node device.");
        Bundle bundle = getArguments();
        endNodeModel = (EndNodeModel) bundle.getSerializable("NODE");
        Log.e("", endNodeModel.toString());
        if (textViewInfo != null && endNodeModel != null) {
            textViewInfo.setText("Node : " + endNodeModel.getEndNodeUid());
        }
    }
}
