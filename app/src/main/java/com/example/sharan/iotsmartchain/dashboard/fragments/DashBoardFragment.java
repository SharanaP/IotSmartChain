package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.AnalyticsActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.BatteryStatusActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.MasterLockActivity;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;


public class DashBoardFragment extends BaseFragment {
    private static String TAG = DashBoardFragment.class.getSimpleName();
    private TextView mTvModulesInUse;
    private TextView mTvModulesNotInUse;
    private LinearLayout mLlAnalytics;
    private LinearLayout mLlBattery;
    private LinearLayout mLlMasterLock;
    private LinearLayout mLlListOfModules;

    public static DashBoardFragment newInstance(){
        DashBoardFragment dashBoardFragment = new DashBoardFragment();
        return dashBoardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_db_dashboard, container, false);

        mTvModulesInUse = (TextView) rootView.findViewById(R.id.textView_modulesInUse);
        mTvModulesNotInUse = (TextView) rootView.findViewById(R.id.textView_ModulesNotInUse);
        mLlAnalytics = (LinearLayout) rootView.findViewById(R.id.linearLayout_analytics);
        mLlBattery = (LinearLayout) rootView.findViewById(R.id.linearLayout_battery);
        mLlMasterLock = (LinearLayout) rootView.findViewById(R.id.linearLayout_masterLock);
        mLlListOfModules = (LinearLayout) rootView.findViewById(R.id.linearLayout_listOfModules);

        mTvModulesInUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modulesInUseView(v);
            }
        });

        mTvModulesNotInUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modulesNotInUse(v);
            }
        });

        mLlAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyticsView(v);
            }
        });

        mLlBattery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                batteryView(v);
            }
        });

        mLlMasterLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                masterLockView(v);
            }
        });

        mLlListOfModules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOfModulesView(v);
            }
        });

        return rootView;
    }

    private void listOfModulesView(View v) {
        Toast.makeText(getActivity(), "list Of ModulesView", Toast.LENGTH_SHORT).show();
    }

    private void masterLockView(View v) {
       // Toast.makeText(getActivity(), "masterLockView", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), MasterLockActivity.class);
        startActivity(intent);
    }

    private void batteryView(View v) {
        //Toast.makeText(getActivity(), "batteryView", Toast.LENGTH_SHORT).show();
        //call Battery status screen
        Intent intent = new Intent(getActivity(), BatteryStatusActivity.class);
        startActivity(intent);
    }

    private void analyticsView(View v) {
        //Toast.makeText(getActivity(), "analyticsView", Toast.LENGTH_SHORT).show();
        Intent analyticsIntent = new Intent(getActivity(), AnalyticsActivity.class);
        startActivity(analyticsIntent);
    }

    //Modules not in use
    private void modulesNotInUse(View v) {
        Toast.makeText(getActivity(), "modules Not In Use", Toast.LENGTH_SHORT).show();
    }

    //ModulesInUse
    private void modulesInUseView(View v) {
        Toast.makeText(getActivity(), "modules In Use View", Toast.LENGTH_SHORT).show();
    }


}
