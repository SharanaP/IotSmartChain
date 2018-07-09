package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
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

    }

    private void setupToolBar() {
        setSupportActionBar(toolbar);
        setTitle("Installation & Configure ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
