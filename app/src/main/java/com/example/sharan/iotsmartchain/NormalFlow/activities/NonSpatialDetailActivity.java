package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.NonSpatialModel;

import butterknife.BindView;

public class NonSpatialDetailActivity extends BaseActivity {
    private static String TAG = NonSpatialDetailActivity.class.getSimpleName();
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.view_message)View mView;
    @BindView(R.id.textview_status)TextView mTvStatus;
    @BindView(R.id.relativeLayout_test)CardView mCardViewTest;
    @BindView(R.id.button_test_iot)Button mBtnLocalTest;
    @BindView(R.id.scrollview)ScrollView mScrollView;
    @BindView(R.id.linear_layout_one)LinearLayout mLinearLayout;
    @BindView(R.id.edittext_serial_num)EditText mEditTextSerialNum;
    @BindView(R.id.edittext_service)EditText mEditTextService;
    @BindView(R.id.edittext_characteristic)EditText mEditTextCharacteristic;
    @BindView(R.id.edittext_label)EditText mEditTextLabel;
    @BindView(R.id.edittext_description)EditText mEditTextDescription;
    @BindView(R.id.edittext_timestamp)EditText mEditTextTimeStamp;
    @BindView(R.id.edittext_latitude)EditText mEditTextLatitude;
    @BindView(R.id.edittext_longitude)EditText mEditTextLongitude;
    @BindView(R.id.edittext_address)EditText mEditTextAddress;

    private NonSpatialModel nonSpatialModel = new NonSpatialModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_spatial_details);
        injectViews();
        setupToolbar();

        NonEditable();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            nonSpatialModel = (NonSpatialModel) extras.getSerializable("NonSpatialModel");
            Log.e(TAG, nonSpatialModel.toString());
            if(nonSpatialModel != null){
                mEditTextSerialNum.setText(nonSpatialModel.getIotDeviceSerialNum());
                mEditTextLabel.setText(nonSpatialModel.getLabel());
                mEditTextDescription.setText(nonSpatialModel.getDescription());
                mEditTextLatitude.setText(""+nonSpatialModel.getLatitude());
                mEditTextLongitude.setText(""+nonSpatialModel.getLongitude());
                mEditTextAddress.setText(nonSpatialModel.getAddress());
                mEditTextService.setText(nonSpatialModel.getService());
                mEditTextCharacteristic.setText(nonSpatialModel.getCharacteristic());
                mEditTextTimeStamp.setText(nonSpatialModel.getTimeStamp());
            }
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Non-Spatial Plan Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_non_spatial_details, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_item_refresh);
        MenuItem menuEdit = menu.findItem(R.id.menu_item_edit);
        MenuItem menuDelete = menu.findItem(R.id.menu_item_delete);
        menuRefresh.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_item_refresh:
                Snackbar.make(mView, "Refresh", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.menu_item_delete:
                Snackbar.make(mView, "Deleted", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.menu_item_edit:
                Snackbar.make(mView, "Updated", Snackbar.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Editable(){
        mEditTextSerialNum.setEnabled(true);
        mEditTextLabel.setEnabled(true);
        mEditTextDescription.setEnabled(true);
        mEditTextLatitude.setEnabled(true);
        mEditTextLongitude.setEnabled(true);
        mEditTextAddress.setEnabled(true);
        mEditTextService.setEnabled(true);
        mEditTextCharacteristic.setEnabled(true);
        mEditTextTimeStamp.setEnabled(true);
    }

    private void NonEditable(){
        mEditTextSerialNum.setEnabled(false);
        mEditTextLabel.setEnabled(false);
        mEditTextDescription.setEnabled(false);
        mEditTextLatitude.setEnabled(false);
        mEditTextLongitude.setEnabled(false);
        mEditTextAddress.setEnabled(false);
        mEditTextService.setEnabled(false);
        mEditTextCharacteristic.setEnabled(false);
        mEditTextTimeStamp.setEnabled(false);
    }
}