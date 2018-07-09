package com.example.sharan.iotsmartchain.dashboard.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.adapter.ListOfLockAdapter;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.DeviceLockerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;

public class MasterLockActivity extends BaseActivity {
    private static String TAG = MasterLockActivity.class.getSimpleName();

    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.switch_lock_all)SwitchCompat mSwitchLockAll;
    @BindView(R.id.listViewLocks)ListView mLvDeviceLocks;

    private DeviceLockerModel deviceLockerModel;
    private ListOfLockAdapter mAdapter;
    private HashMap<String, DeviceLockerModel> mDeviceMap = new LinkedHashMap<>();
    private String mUrl, loginId, token;
    private ArrayList<DeviceLockerModel> arrayListOfLock = new ArrayList<>();

    private String listDeviceLockStr = "{\"tokenid\": \"84h39873423h823\",\"emailid\": \"personName@gmail.com\", \"status\": \"true\", \n" +
            "\"deviceLockInfo\": [\n" +
            "{\"deviceId\": \"371je1ooj293u102938\", \"deviceType\": \"offic e main door\", \"isLocked\": true}, \n" +
            "{\"deviceId\": \"371je1ooj2lkjlkjlkj02938\", \"deviceType\": \"WorkStation locker\", \"isLocked\": false}, \n" +
            "{\"deviceId\": \"371jesdfjhgaf43452938\", \"deviceType\": \"Home main door\", \"isLocked\": false}, \n" +
            "{\"deviceId\": \"371rtyrtydfjhgafu102938\", \"deviceType\": \"office locker door\", \"isLocked\": true}, \n" +
            "{\"deviceId\": \"371jesdfsdfsafu102938\", \"deviceType\": \"Home locker door\", \"isLocked\": true}]}";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_lock);

        injectViews();

        setupToolbar();

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        Log.d(TAG, "" + listDeviceLockStr.toString());

        deviceLockerModel = new DeviceLockerModel();

        mAdapter = new ListOfLockAdapter(MasterLockActivity.this, arrayListOfLock);
        mLvDeviceLocks.setAdapter(mAdapter);

        mSwitchLockAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        readStringTOlist();
    }

    private void readStringTOlist() {
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(listDeviceLockStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String emailId = null;
        String tokenid = null;
        String status = null;
        try {
            emailId = (String) jObject.get("emailid");
            tokenid = (String) jObject.get("tokenid");
            status = (String) jObject.get("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(emailId);
        System.out.println(tokenid);
        System.out.println(status);

        JSONArray jsonArray = null;
        try {
            jsonArray = jObject.getJSONArray("deviceLockInfo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mDeviceMap = new LinkedHashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                deviceLockerModel = new DeviceLockerModel();
                deviceLockerModel.setDeviceId(object.getString("deviceId"));
                deviceLockerModel.setLocked(object.getBoolean("isLocked"));
                deviceLockerModel.setDeviceType(object.getString("deviceType"));
                mDeviceMap.put(jsonArray.getString(i), deviceLockerModel);

                Log.d(TAG, "mDeviceMap : "+mDeviceMap.values());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        arrayListOfLock = new ArrayList<DeviceLockerModel>(mDeviceMap.values());
        Log.d(TAG, "arrayListOfLock : "+arrayListOfLock.toString());

        mAdapter = new ListOfLockAdapter(MasterLockActivity.this, arrayListOfLock);
        mLvDeviceLocks.setAdapter(mAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("Master locks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        menuRefresh.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
