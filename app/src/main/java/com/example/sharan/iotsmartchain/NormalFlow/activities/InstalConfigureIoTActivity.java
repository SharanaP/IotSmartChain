package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.IotDeviceConfigure.WifiSecondDemo;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.WifiNetwork.MainWifiBleActivity;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import butterknife.BindView;

public class InstalConfigureIoTActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button_WIFI)
    Button mBtnWiFi;

    @BindView(R.id.button_GSM)
    Button mBtnGSM;

    @BindView(R.id.textView_status)
    TextView mTextStatus;

    @BindView(R.id.image_wifi_check)
    ImageView mImageWifiCheck;

    @BindView(R.id.image_gsm_check)
    ImageView mImageGsmCheck;

    @BindView(R.id.button_next)
    Button mBtnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_iot_instal);

        injectViews();

        setupToolBar();

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(InstalConfigureIoTActivity.this,
                        FloorPlanActivity.class);
                startActivity(intent);
            }
        });

        mBtnWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                final ComponentName cn = new ComponentName("com.android.settings",
//                        "com.android.settings.wifi.WifiSettings");
//                intent.setComponent(cn);
//                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);

                Intent intentWifi = new Intent(InstalConfigureIoTActivity.this,
                        MainWifiBleActivity.class);
                startActivity(intentWifi);
            }
        });

    }

    private void setupToolBar() {
        setSupportActionBar(toolbar);
        setTitle("Wi-Fi & BLE Gateway");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(InstalConfigureIoTActivity.this, RegisterIoTDeviceActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(InstalConfigureIoTActivity.this, RegisterIoTDeviceActivity.class);
                startActivity(intent);
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
