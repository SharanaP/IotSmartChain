package com.example.sharan.iotsmartchain.WifiNetwork;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.BlueTooth.BleConnectionManager;
import com.example.sharan.iotsmartchain.R;

import java.util.LinkedList;
import java.util.List;

public class WifiConnectionActivity extends AppCompatActivity {

    private static String TAG = WifiConnectionActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextView mTvWifiStatus, mTvWifiResult;
    private Switch mSwitchWifi;
    private ListView mListView;
    private View mViewWIFI;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_conection);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewWIFI = (View) findViewById(R.id.view_wifi);
        mTvWifiStatus = (TextView) findViewById(R.id.textview_wifi_on_off);
        mTvWifiResult = (TextView) findViewById(R.id.textview_wifi_result);
        mSwitchWifi = (Switch) findViewById(R.id.switch_wifi);
        mListView = (ListView) findViewById(R.id.listview_scanned_wifi);

        //setUp toolbar
        setupToolbar();

        //check permission
        checkPersmissions();

        //init and check default wi-fi status
        connectionManager = new ConnectionManager(WifiConnectionActivity.this);
        if (connectionManager.CheckWifiStatus()) {
            mTvWifiStatus.setText("On");
            mSwitchWifi.setChecked(true);

            showProgressDialog();

            mTvWifiResult.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);

            scanResultList = connectionManager.getScannedWifiList();
            Log.e(TAG, "\nLIST OF WIFI \n" + connectionManager.getScannedWifiList());
            //set wifi adapter
            scannedWiFiAdapter = new ScannedWiFiAdapter(WifiConnectionActivity.this, scanResultList);
            mListView.setAdapter(scannedWiFiAdapter);

        } else {
            mTvWifiStatus.setText("Off");
            mSwitchWifi.setChecked(false);
            mTvWifiResult.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }

        /*Switch listener*/
        mSwitchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (connectionManager != null)
                        connectionManager.enableWifi();
                    else {
                        connectionManager = new ConnectionManager(WifiConnectionActivity.this);
                        connectionManager.enableWifi();
                    }

                    showProgressDialog();

                    mTvWifiResult.setVisibility(View.INVISIBLE);
                    mListView.setVisibility(View.VISIBLE);

                    Snackbar.make(mViewWIFI, "WIFI Turned On", Snackbar.LENGTH_LONG).show();
                    mTvWifiStatus.setText("On");

                    scanResultList = connectionManager.getScannedWifiList();
                    Log.e(TAG, "\nLIST OF WIFI \n" + connectionManager.getScannedWifiList());

                    //set wifi adapter
                    scannedWiFiAdapter = new ScannedWiFiAdapter(WifiConnectionActivity.this, scanResultList);
                    mListView.setAdapter(scannedWiFiAdapter);
                    scannedWiFiAdapter.notifyDataSetChanged();

                } else {

                    if (connectionManager != null)
                        connectionManager.disableWifi();
                    else {
                        connectionManager = new ConnectionManager(WifiConnectionActivity.this);
                        connectionManager.disableWifi();
                    }

                    //clear all
                    scannedWiFiAdapter.clear();
                    scannedWiFiAdapter.notifyDataSetChanged();

                    Snackbar.make(mViewWIFI, "WIFI Turned Off", Snackbar.LENGTH_LONG).show();
                    mTvWifiStatus.setText("Off");

                    mListView.setVisibility(View.GONE);
                    mTvWifiResult.setVisibility(View.VISIBLE);
                }
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult selectedWifi = (ScanResult) parent.getAdapter().getItem(position);
                Log.e(TAG, "selected ssid : " + selectedWifi.SSID);

                //Show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(WifiConnectionActivity.this);
                LayoutInflater inflater = WifiConnectionActivity.this.getLayoutInflater();
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

                        //TODO show dialog BLE connection and set timeout for 2sec
                        BleConnectionManager bleConnectionManager =
                                new BleConnectionManager(WifiConnectionActivity.this, ssidStr, pswStr);
                        bleConnectionManager.StartBleScan();
                    }
                });

                builder.create().show();

            }
        });

        //progress dialog listner
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setWiFiPsw(String ssidStr, String pswStr) {

        if (ssidStr != null && !ssidStr.isEmpty() && !TextUtils.isEmpty(ssidStr)) {
            if (pswStr != null && !pswStr.isEmpty() && !TextUtils.isEmpty(pswStr)) {
                int retVal = connectionManager.requestWIFIConnection(ssidStr, pswStr);

                switch (retVal) {
                    case -1:
                        Snackbar.make(mViewWIFI, "Scan WIFI SSID name " + ssidStr +
                                " Not Found and Enter Proper WIFI SSID name!!!", Snackbar.LENGTH_LONG).show();
                        break;
                    case -2:
                        //COULDNOT_ADD_NETWORK
                        Snackbar.make(mViewWIFI, "Couldn't add network due to" +
                                " incorrect password", Snackbar.LENGTH_LONG).show();
                        break;
                    case 100:
                        Snackbar.make(mViewWIFI, "Current WIFI network already connected ",
                                Snackbar.LENGTH_LONG).show();
                        break;
                    case 300:

                        Snackbar.make(mViewWIFI, "WIFI network connection requested",
                                Snackbar.LENGTH_LONG).show();
                        break;
                    case 500:
                        Snackbar.make(mViewWIFI, "Unable to find Security type " +
                                "for current WIFI try again...!!!", Snackbar.LENGTH_LONG).show();
                        break;
                    default:
                        Snackbar.make(mViewWIFI, "Failed to Connect WIFI and " +
                                "enter proper SSID and PSw...!!!", Snackbar.LENGTH_LONG).show();
                        break;
                }
            } else {
                Snackbar.make(mViewWIFI, "Password should not be empty and " +
                        "enter proper SSID and PSw...!!!", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(mViewWIFI, "WIFI Network SSID name should not be empty and " +
                    "enter proper SSID and PSw...!!!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.CustomDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Searching for Wi-Fi networks....");
        progressDialog.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1500);
    }

    private void checkPersmissions() {
        //Check for permissions
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
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
        } else
            Log.d(TAG, "Permissions already granted");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d(TAG, "permission granted: " + permissions[0]);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "permission denied: " + permissions[0]);
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}