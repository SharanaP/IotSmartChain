package com.example.sharan.iotsmartchain.IotDeviceConfigure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class WiFiActivity extends BaseActivity {
    private static String TAG = WiFiActivity.class.getSimpleName();
    public WifiManager wifi;
    private List<ScanResult> results;

    private int size = 0;
    private String ITEM_KEY = "key";
    private ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter adapter;

    @BindView(R.id.toolbar2)Toolbar toolbar;
    @BindView(R.id.textView_wifi)TextView mTextViewWifi;
    @BindView(R.id.switch_wifi)Switch mSwitchWifi;
    @BindView(R.id.listview_wifi)ListView lv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        injectViews();
        setupToolbar();

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        this.adapter = new SimpleAdapter(WiFiActivity.this, arraylist,
                R.layout.row_wifi_item, new String[] { ITEM_KEY }, new int[] { R.id.textView_wifi_title });
        lv.setAdapter(this.adapter);

        registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = wifi.getScanResults();
                size = results.size();
                Log.e(TAG, "results : "+results.toString());
                Log.e(TAG, "size : "+size);
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        /*Switch*/
        mSwitchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                arraylist.clear();

                if(isChecked){
                    mTextViewWifi.setText("On");
                    wifi.startScan();
                    Toast.makeText(WiFiActivity.this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
                    try
                    {
                        size = size - 1;
                        while (size >= 0)
                        {
                            HashMap<String, String> item = new HashMap<String, String>();
                            item.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).capabilities);

                            arraylist.add(item);
                            size--;
                            adapter.notifyDataSetChanged();
                        }
                    }
                    catch (Exception e)
                    { }

                }else{
                    mTextViewWifi.setText("Off");
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_refresh:
                //TODO call API to get updated Analytics status
                Toast.makeText(this, "Wifi updated", Toast.LENGTH_SHORT).show();
                wifi.startScan();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void setWiFiStatus() {
//
//        try{
//            // Setup WiFi
//            wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//            // Get WiFi status
//            WifiInfo info = wifi.getConnectionInfo();
//            mTextViewWifiName.append("\n\nWiFi Status: " + info.toString());
//
//            // List available networks
//            List<WifiConfiguration> configs = wifi.getConfiguredNetworks();
//            for (WifiConfiguration config : configs) {
//                mTextViewWifiName.append("\n\n" + config.toString());
//            }
//
//            // Register Broadcast Receiver
//            if (receiver == null)
//                receiver = new WiFiScanReceiver(WiFiActivity.this);
//
//            registerReceiver(receiver, new IntentFilter(
//                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//            Log.d(TAG, "onCreate()");
//        }catch (NullPointerException ex){
//            ex.printStackTrace();
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Wifi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
