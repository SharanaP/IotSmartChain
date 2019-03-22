package com.example.sharan.iotsmartchain.main.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.NetworkUtil;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.loginModule.activities.LoginActivity;

import butterknife.ButterKnife;

/**
 * Created by Sharan on 14-03-2018.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Set navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.color_Primary));
        }

        //check internet
        checkInternetConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void injectViews() {
        ButterKnife.bind(this);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /*check internet*/
    private void checkInternetConnection(){
        int isNetwork = NetworkUtil.getConnectivityStatus(BaseActivity.this);
        if(isNetwork == 0){
            //Show dialog :: no internet
            Utils.ShowAlertDialog(BaseActivity.this, getResources().getString(R.string.no_internet), getResources().getString(R.string.close));
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

}
