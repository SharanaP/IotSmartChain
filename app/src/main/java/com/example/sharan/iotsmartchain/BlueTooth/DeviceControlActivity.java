package com.example.sharan.iotsmartchain.BlueTooth;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.WifiNetwork.ConnectionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * The Activity communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */

public class DeviceControlActivity extends Activity {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    private static int ENTER_SSID_PSW_DIALOG = 110;
    private static boolean isShowDialog = false;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private BluetoothGatt gatt;
    private String mDeviceName;
    private String mDeviceAddress;
    private String mSsidName;
    private String mPassword;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private View mView;
    private TextView mTextView;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                mTextView.setText("Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    /*Show dialog*/
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private EditText mEditTextSsid, mEditTextPsw;
    private BluetoothGattCharacteristic characteristic = null;
    private int charaProp = -1;
    private ProgressDialog progressDialog;

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.e(TAG, "Connected");
                mTextView.setText("BLE Connected");
                Snackbar.make(mView, "BLE Connected", Snackbar.LENGTH_LONG).show();
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) progressDialog.setMessage("BLE Connected");
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.d(TAG, "Disconnected");
                mTextView.setText("BLE Disconnected");
                Snackbar.make(mView, "BLE is disconnected adn try again", Snackbar.LENGTH_LONG).show();

                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        finish();
                    }
                }

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                Log.e(TAG, "ALL SUPPORTED SERVICE" + " " + mBluetoothLeService.getSupportedGattServices().toString());

                List<BluetoothGattService> servicesList;
                servicesList = mBluetoothLeService.getSupportedGattServices();
                Iterator<BluetoothGattService> iter = servicesList.iterator();
                while (iter.hasNext()) {
                    BluetoothGattService bService = (BluetoothGattService) iter.next();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        Log.e(TAG, "UUID : " + bService.getUuid().toString());
                    }
                    Log.e(TAG, "TARGET : " + TargetGattAttributes.TARGET_BLE_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if (bService.getUuid().toString()
                                .equals(TargetGattAttributes.TARGET_BLE_SERVICE)) {
                            Log.e("HRS SERVICE", bService.toString());

                            characteristic =
                                    bService.getCharacteristic(
                                            UUID.fromString(TargetGattAttributes.TARGET_BLE_CHARACTERISTIC));
                            charaProp = characteristic.getProperties();

                            //                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            //                            // If there is an active notification on a characteristic, clear
                            //                            // it first so it doesn't update the data field on the user interface.
                            //                            if (mNotifyCharacteristic != null) {
                            //                                mBluetoothLeService.setCharacteristicNotification(
                            //                                        mNotifyCharacteristic, false);
                            //                                mNotifyCharacteristic = null;
                            //                            }
                            //                            mBluetoothLeService.readCharacteristic(characteristic);
                            //                        }

                            //                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            //                            mNotifyCharacteristic = characteristic;
                            //                            mBluetoothLeService.setCharacteristicNotification(
                            //                                    characteristic, true);
                            //                        }

                            isShowDialog = true;

                            break; /* Once Service found, no need to rendering */
                        }
                    }
                }

                //TODO show dialog box : enter SSID nad PSW
                if (isShowDialog) {
                    //  showDialog(ENTER_SSID_PSW_DIALOG);
                    if (progressDialog != null) {
                        if (progressDialog.isShowing())
                            progressDialog.setMessage("writing to BLE and wait few seconds...");
                    }
                    writeOnBleCharacteristic(mSsidName, mPassword);
                }

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void writeOnBleCharacteristic(String mSsid, String mPsw) {
        if (mSsid != null && !mSsid.isEmpty() && !TextUtils.isEmpty(mSsid)) {

            if (mPsw != null && !mPsw.isEmpty() && !TextUtils.isEmpty(mPsw)) {

                byte[] byteSsid = mSsid.getBytes();
                byte[] bytePsw = mPsw.getBytes();
                byte[] value = null;

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    byteArrayOutputStream.write(byteSsid);
                    byteArrayOutputStream.write(58); //ASCII value colon
                    byteArrayOutputStream.write(bytePsw);
                    value = byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //TODO write BLE characteristic
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                    if (characteristic != null) {
                        if (value != null) {
                            boolean isWrite = mBluetoothLeService.writeCharacteristic(characteristic, value);
                            if (isWrite) {
                                Snackbar.make(mView, "Set SSID is Successfully ",
                                        Snackbar.LENGTH_LONG).show();

                                showBleGatewayDialog("Successfully write to BLE gateway");
                                progressDialog.setMessage("Successfully write to BLE gateway");

                            } else {
                                Snackbar.make(mView, "Failed to set SSID",
                                        Snackbar.LENGTH_LONG).show();
                                showBleGatewayDialog("Failed to set SSID and try again!!!");
                                progressDialog.setMessage("Failed to set SSID and try again!!!");
                            }
                        } else {
                            Log.e(TAG, " value is null ");
                        }
                    } else {
                        Log.e(TAG, "characteristic is null");
                    }
                }

                //Dismiss progress dialog
                if (progressDialog.isShowing()) {
                    progressDialog.cancel();
                    progressDialog.dismiss();
                    Log.d(TAG, "progress dialog is dismiss");

                    //disconnect Wi-Fi network.
                    ConnectionManager connectionManager = new ConnectionManager(DeviceControlActivity.this);
                    connectionManager.disableWifi();

                }

            } else {
                Snackbar.make(mView, "Enter the Gateway device Password and it should not be empty!!!",
                        Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(mView, "Enter the Gateway device SSID and it should not be empty!!!",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        mTextView = (TextView) findViewById(R.id.textview);
        mView = (View) findViewById(R.id.view_show);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mSsidName = intent.getStringExtra("SSID");
        mPassword = intent.getStringExtra("PASSWORD");

        Log.e("DEVICE_INFO :: ", mDeviceName + " " + mDeviceAddress);
        Log.e("Wi-Fi :: ", mSsidName + " " + mPassword);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        Log.d(TAG, "progress dialog started");
        progressDialog = new ProgressDialog(this, R.style.CustomDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Trying to connect BLE and write....");
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) progressDialog.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            Log.e("DATA", data);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        builder = new AlertDialog.Builder(DeviceControlActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = DeviceControlActivity.this.getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dailog_ssid_psw, null);

        TextView mTextViewTitle = (TextView) rootView.findViewById(R.id.dailog_title);
        mEditTextSsid = (EditText) rootView.findViewById(R.id.edittext_ssid);
        mEditTextPsw = (EditText) rootView.findViewById(R.id.edittext_psw);

        mTextViewTitle.setText("BLE");
        if (!TextUtils.isEmpty(mPassword)) {
            mEditTextPsw.setText(mPassword);
            mEditTextPsw.setEnabled(false);
        }
        if (!TextUtils.isEmpty(mSsidName)) {
            mEditTextSsid.setText(mSsidName);
            mEditTextSsid.setEnabled(false);
        }


        builder.setView(rootView);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mEditTextSsid.setText("");
                mEditTextPsw.setText("");
            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, " SH : " + mEditTextSsid.getText().toString().trim());
                Log.e(TAG, " SH : " + mEditTextPsw.getText().toString().trim());
                //Iot Device id
                String mSsid = mEditTextSsid.getText().toString().trim();
                String mPsw = mEditTextPsw.getText().toString().trim();

                if (mSsid != null && !mSsid.isEmpty() && !TextUtils.isEmpty(mSsid)) {

                    if (mPsw != null && !mPsw.isEmpty() && !TextUtils.isEmpty(mPsw)) {

                        byte[] byteSsid = mSsid.getBytes();
                        byte[] bytePsw = mPsw.getBytes();
                        byte[] value = null;

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        try {
                            byteArrayOutputStream.write(byteSsid);
                            byteArrayOutputStream.write(58); //ASCII value colon
                            byteArrayOutputStream.write(bytePsw);

                            value = byteArrayOutputStream.toByteArray();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //TODO write BLE characteristic
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            if (value != null) {
                                boolean isWrite = mBluetoothLeService.writeCharacteristic(characteristic, value);
                                Log.e(TAG, "written is " + isWrite);
                                if (isWrite) {
                                    Snackbar.make(mView, "Set SSID is Successfully ",
                                            Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(mView, "Failed to set SSID",
                                            Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                Log.e(TAG, " value is null ");
                            }
                        }

                    } else {
                        Snackbar.make(mView, "Enter the Gateway device Password and it should not be empty!!!",
                                Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(mView, "Enter the Gateway device SSID and it should not be empty!!!",
                            Snackbar.LENGTH_LONG).show();
                }

                //clear data
                mEditTextSsid.setText("");
                mEditTextPsw.setText("");

            }
        });

        dialog = builder.create();

        return dialog;
    }

    private void showBleGatewayDialog(String message) {
        //Show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceControlActivity.this);
        LayoutInflater inflater = DeviceControlActivity.this.getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dailog_ssid_psw, null);

        final TextView textViewTitle = (TextView) rootView.findViewById(R.id.dailog_title);
        final EditText editTextSSID = (EditText) rootView.findViewById(R.id.edittext_ssid);
        final EditText editTextPSW = (EditText) rootView.findViewById(R.id.edittext_psw);

        textViewTitle.setText("BLE Gateway Message");
        editTextSSID.setText(mSsidName);
        editTextSSID.setEnabled(false);

        editTextPSW.setText(message);
        editTextPSW.setEnabled(false);

        builder.setView(rootView);

        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editTextSSID.setText("");
                editTextPSW.setText("");

                DeviceControlActivity.this.finish();
            }
        });

        builder.create().show();
    }

}
