package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.NormalFlow.adapter.AdapterListOfDevices;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Sharan on 21-03-2018.
 */

public class HomeActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    /*@Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.listViewSensors)
    ListView mListViewSensors;*/

    private static String TAG = "HomeActivity";
    private boolean isScanning = false;
    private AdapterListOfDevices adapterListOfDevices;
    private DeviceInfo deviceInfo;
    private Map<String, DeviceInfo> mListOfDevices = new LinkedHashMap<>();

    String mStrDeviceList = "{\n" +
            "  \"tokenid\": \"84h39873423h823\",\n" +
            "  \"emailid\": \"personName@gmail.com\",\n" +
            "  \"deviceinfo\": [\n" +
            "    {\n" +
            "      \"deviceId\": \"Iot module dhfsdk\",\n" +
            "      \"batteryStatus\": \"60%\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"deviceId\": \"Iot module w234fr\",\n" +
            "      \"batteryStatus\": \"80%\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"deviceId\": \"Iot module s34sfd\",\n" +
            "      \"batteryStatus\": \"40%\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    Toolbar toolbar;
    ListView mListViewSensors;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);
        setContentView(R.layout.activity_home_list_sensor);
        Log.d(TAG, "onCreate()");

        //init all UI component
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mListViewSensors = (ListView)findViewById(R.id.listViewSensors);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

       // injectViews();

        setupToolbar();

        //Set navigation bar color
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigation_bar_background));
//        }

        App.inject(this);

        deviceInfo = new DeviceInfo();

        JSONObject jObject = null;
        try {
            jObject = new JSONObject(mStrDeviceList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String emailId = null;
        String tokenid = null;
        try {
            emailId = (String) jObject.get("emailid");
            tokenid = (String)jObject.get("tokenid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(emailId);
        System.out.println(tokenid);

        JSONArray jsonArray = null;
        try {
            jsonArray = jObject.getJSONArray("deviceinfo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mListOfDevices = new LinkedHashMap<>();
        for(int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                deviceInfo = new DeviceInfo();
                deviceInfo.setDeviceId(object.getString("deviceId"));
                deviceInfo.setBatteryStatus(object.getString("batteryStatus"));

                mListOfDevices.put(jsonArray.getString(i), deviceInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Set list of sensors to main screen
        adapterListOfDevices = new AdapterListOfDevices(this, mListOfDevices);
        mListViewSensors.setAdapter(adapterListOfDevices);
        mListViewSensors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(HomeActivity.this, "Goto Sensors Reports", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, SensorsActivity.class);
                startActivity(intent);
            }
        });


        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("IOT Smart Modules");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setSubtitle("Powered by GeoKno");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItemScan = menu.findItem(R.id.menu_scan);
        menuItemScan.setTitle(isScanning ? R.string.menu_stop_scan : R.string.menu_scan);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                if (isScanning) {
                   // stopScan();
                } else {
//                    cancelConnection();
//                    startScan();
                    //TODO  call API to get a list sensors
                }
                break;

            case R.id.menu_info:
//                Intent intent = new Intent(this, InfoActivity.class);
//                startActivity(intent);
                Toast.makeText(this, "IOT Sensors Info ", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_sensor_settings) {

        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
