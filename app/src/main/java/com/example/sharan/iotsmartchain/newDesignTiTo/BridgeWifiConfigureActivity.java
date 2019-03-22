package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.BlueTooth.BleConnectionManager;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.WifiNetwork.ConnectionManager;
import com.example.sharan.iotsmartchain.WifiNetwork.ScannedWiFiAdapter;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.Check_SDK;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.BridgeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BridgeWifiConfigureActivity extends BaseActivity {
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static String TAG = BridgeWifiConfigureActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextView mTvWifiStatus, mTvWifiResult;
    private Switch mSwitchWifi;
    private ListView mListView;
    private View mViewWIFI;
    private CoordinatorLayout mCoordinatorLayout;
    private WifiManager wifiManager;
    private WifiConfiguration wifiConfig;
    private List<ScanResult> scanResultList = new LinkedList<>();
    private String ssidStr;
    private String pswStr;
    private ConnectionManager connectionManager = null;
    private ScannedWiFiAdapter scannedWiFiAdapter = null;
    private List<ScanResult> scanWifiList = new LinkedList<>();
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;

    private LocationManager locationManager;
    private boolean GpsStatus = false;
    private WifiReceiver receiverWifi;
    private StringBuilder sb = null;
    private Check_SDK check_sdk;
    private BridgeModel bridgeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_wifi_ble);
        setContentView(R.layout.activity_wifi_conection);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mViewWIFI = (View) findViewById(R.id.view_wifi);
        mTvWifiStatus = (TextView) findViewById(R.id.textview_wifi_on_off);
        mTvWifiResult = (TextView) findViewById(R.id.textview_wifi_result);
        mSwitchWifi = (Switch) findViewById(R.id.switch_wifi);
        mListView = (ListView) findViewById(R.id.listview_scanned_wifi);

        //setUp toolbar
        setupToolbar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bridgeModel = (BridgeModel) bundle.getSerializable("BRIDGE");
            if (bridgeModel != null)
                Log.e(TAG, " bridgeModel : " + bridgeModel.toString());
        }

        //manually check enable a location and bluetooth device
        checkLocationPermissions();

        //init wifi manager
        wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Check current version API
        check_sdk = new Check_SDK(BridgeWifiConfigureActivity.this);
        Log.e(TAG, "" + check_sdk.getCurrentVersion());
        int currentApiSdk = check_sdk.getCurrentVersion();
        if (currentApiSdk >= android.os.Build.VERSION_CODES.P) {
            GetScannedWifiList();
        }

        //ble
        BleConnectionManager bleConnectionManager = new BleConnectionManager(BridgeWifiConfigureActivity.this);
        bleConnectionManager.initBle();

        //init and check and update wi-fi networks status
        if (connectionManager == null) {
            if (wifiManager != null)
                connectionManager = new ConnectionManager(BridgeWifiConfigureActivity.this, wifiManager);
        }

        //adapter init
        scannedWiFiAdapter = new ScannedWiFiAdapter(BridgeWifiConfigureActivity.this, scanResultList);
        mListView.setAdapter(scannedWiFiAdapter);

        //updates wifi network
        boolean isCheck = UpdateWifiNetworks(GPSStatus());

        /*Switch listener*/
        mSwitchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UpdateWifiNetworks(isChecked);
            }
        });

        /*Listener */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult selectedWifi = (ScanResult) parent.getAdapter().getItem(position);
                Log.e(TAG, "selected ssid : " + selectedWifi.SSID);

                //Show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(BridgeWifiConfigureActivity.this);
                LayoutInflater inflater = BridgeWifiConfigureActivity.this.getLayoutInflater();
                View rootView = inflater.inflate(R.layout.dailog_ssid_psw, null);

                final EditText editTextSSID = (EditText) rootView.findViewById(R.id.edittext_ssid);
                final EditText editTextPSW = (EditText) rootView.findViewById(R.id.edittext_psw);
                editTextSSID.setText(selectedWifi.SSID);
                editTextSSID.setEnabled(false);
                builder.setView(rootView);

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editTextPSW.setText("");
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ssidStr = editTextSSID.getText().toString();
                        pswStr = editTextPSW.getText().toString();
                        setWiFiPsw(ssidStr, pswStr);
                        editTextPSW.setText("");
                        dialog.dismiss();

                        if (!TextUtils.isEmpty(ssidStr) && !TextUtils.isEmpty(pswStr)) {

                            if (bridgeModel != null) {
                                BleConnectionManager bleConnectionManager = new
                                        BleConnectionManager(BridgeWifiConfigureActivity.this,
                                        ssidStr, pswStr, bridgeModel.getService(),
                                        bridgeModel.getCharacteristic(), bridgeModel);
                                bleConnectionManager.StartBleScan();
                            } else {
                                Toast.makeText(BridgeWifiConfigureActivity.this, "Try again!!!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(BridgeWifiConfigureActivity.this, "Should't be empty!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.create().show();

            }
        });

        //progress dialog listener
        if (progressDialog != null)
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.cancel();
                }
            });

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Wi-Fi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setWiFiPsw(String ssidStr, String pswStr) {
        String message = "";
        if (ssidStr != null && !ssidStr.isEmpty() && !TextUtils.isEmpty(ssidStr)) {
            if (pswStr != null && !pswStr.isEmpty() && !TextUtils.isEmpty(pswStr)) {
                int retVal = connectionManager.requestWIFIConnection(ssidStr, pswStr);

                switch (retVal) {
                    case -1:
                        message = "Scan WIFI SSID name " + ssidStr +
                                " Not Found and Enter Proper WIFI SSID name!!!";
                        Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                                message, ALERTCONSTANT.ERROR);
                        break;
                    case -2:
                        //COULDNOT_ADD_NETWORK
                        message = "Couldn't add network due to incorrect password";
                        Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                                message, ALERTCONSTANT.WARNING);
                        break;
                    case 100:
                        message = "Current WIFI network already connected ";
                        Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                                message, ALERTCONSTANT.SUCCESS);
                        break;
                    case 300:
                        message = "WIFI network connection requested";
                        Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                                message, ALERTCONSTANT.INFO);
                        break;
                    case 500:
                        message = "Unable to find Security type for current WIFI try again...!!!";
                        Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                                message, ALERTCONSTANT.WARNING);
                        break;
                    default:
                        message = "Failed to Connect WFII and enter proper SSID and PSw...!!!";
                        Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                                message, ALERTCONSTANT.ERROR);
                        break;
                }
            } else {
                message = "Password should not be empty and enter proper SSID and PSw...!!!";
                Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                        message, ALERTCONSTANT.ERROR);
            }
        } else {
            message = "WIFI Network SSID name should not be empty and enter proper SSID and PSw...!!!";
            Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                    message, ALERTCONSTANT.ERROR);
        }
    }

    private void showProgressDialog() {
        Log.d(TAG, "progress dialog started");
        progressDialog = new ProgressDialog(this, R.style.CustomDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Searching for Wi-Fi networks....");
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) progressDialog.show();
        }

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.cancel();
                    progressDialog.dismiss();
                    Log.d(TAG, "progress dialog is dismiss");
                }
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 2000);
    }

    private boolean GPSStatus() {
        boolean isCheck = false;
        locationManager = (LocationManager) BridgeWifiConfigureActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(BridgeWifiConfigureActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage(BridgeWifiConfigureActivity.this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(BridgeWifiConfigureActivity.this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    BridgeWifiConfigureActivity.this.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(BridgeWifiConfigureActivity.this.getString(R.string.close), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                            "Location not enabled", ALERTCONSTANT.ERROR);
                }
            });
            dialog.show();
        } else {
            isCheck = true;
        }

        return isCheck;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        GPSStatus(); //call gps and enable location

        switch (requestCode) {

            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BLUETOOTH, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BLUETOOTH_ADMIN, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Log.e(TAG, "all permissions granted");
                } else {
                    // Permission Denied
                    Log.e(TAG, "Permission Denied");
                    Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                            "Some Permission is Denied", ALERTCONSTANT.ERROR);
                }
                //TODO check play services
            }
            break;

            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d(TAG, "permission granted: " + permissions[0]);

                    //TODO init and BLE check it
                    BleConnectionManager bleConnectionManager = new BleConnectionManager(BridgeWifiConfigureActivity.this);
                    bleConnectionManager.initBle();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "permission denied: " + permissions[0]);
                }
            }
            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkLocationPermissions() {
        //Check for permissions
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "Requesting permissions");

            //Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN},
                    123);


        } else {
            Log.d(TAG, "Permissions already granted");

        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
    }

    private boolean UpdateWifiNetworks(boolean isChecked) {
        if (isChecked) {
            if (connectionManager != null)
                connectionManager.enableWifi();
            else {
                connectionManager = new ConnectionManager(BridgeWifiConfigureActivity.this, wifiManager);
                connectionManager.enableWifi();
            }

            showProgressDialog();

            mTvWifiResult.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);

            Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                    "WIFI Turned On", ALERTCONSTANT.INFO);
            mTvWifiStatus.setText("On");
            mSwitchWifi.setChecked(true);

            scanResultList = connectionManager.getScannedWifiList();
            Log.e(TAG, "\nLIST OF WIFI \n" + connectionManager.getScannedWifiList());

            //set wifi adapter
            scannedWiFiAdapter = new ScannedWiFiAdapter(BridgeWifiConfigureActivity.this, scanResultList);
            mListView.setAdapter(scannedWiFiAdapter);
            scannedWiFiAdapter.notifyDataSetChanged();

            //pie version check
            check_sdk = new Check_SDK(BridgeWifiConfigureActivity.this);
            Log.e(TAG, "" + check_sdk.getCurrentVersion());
            int currentApiSdk = check_sdk.getCurrentVersion();
            if (currentApiSdk >= android.os.Build.VERSION_CODES.P) {
                GetScannedWifiList();
            }

        } else {

            if (connectionManager != null)
                connectionManager.disableWifi();
            else {
                connectionManager = new ConnectionManager(BridgeWifiConfigureActivity.this, wifiManager);
                connectionManager.disableWifi();
            }

            //clear all
            scannedWiFiAdapter.clear();
            scannedWiFiAdapter.notifyDataSetChanged();

            Utils.SnackBarView(BridgeWifiConfigureActivity.this, mCoordinatorLayout,
                    "WIFI Turned Off", ALERTCONSTANT.INFO);

            mTvWifiStatus.setText("Off");
            mSwitchWifi.setChecked(false);

            mListView.setVisibility(View.GONE);
            mTvWifiResult.setVisibility(View.VISIBLE);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_refresh:
                //updates wifi network
                boolean isCheck = UpdateWifiNetworks(GPSStatus());
                Log.e(TAG, "Refresh isCheck : " + isCheck);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /*PIE version: Get list of scanned result :: wifi list */
    private void GetScannedWifiList() {
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            Log.e(TAG, "Failure");
            scanFailure();
        } else {
            Log.e(TAG, "Success");
        }
    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        Map<String, ScanResult> map = new LinkedHashMap<>();
        for (ScanResult scanResult : results) {
            map.put(scanResult.BSSID.toString(), scanResult);
        }

        List<ScanResult> finalWifi = new ArrayList<>(map.values());
        Log.e(TAG, "wifi\n\n" + finalWifi.toString());
        scannedWiFiAdapter = new ScannedWiFiAdapter(BridgeWifiConfigureActivity.this, finalWifi);
        mListView.setAdapter(scannedWiFiAdapter);

        Log.i(TAG, "scanSuccess\n" + results.toString());
        //  ... use new scan results ...
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        Log.e(TAG, "scanFailure\n" + results.toString());
        //  ... potentially use older scan results ...
    }

    /*Broad Cast Receiver*/
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            ArrayList<String> connections = new ArrayList<String>();
            ArrayList<Float> Signal_Strength = new ArrayList<Float>();

            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = wifiManager.getScanResults();
            for (int i = 0; i < wifiList.size(); i++) {
                connections.add(wifiList.get(i).SSID);
            }
            Log.d(TAG, "connections : " + connections.toString());
        }
    }

}