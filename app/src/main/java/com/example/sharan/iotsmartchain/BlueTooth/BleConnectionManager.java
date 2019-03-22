package com.example.sharan.iotsmartchain.BlueTooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.model.BridgeModel;


public class BleConnectionManager extends AppCompatActivity {
    private static final String TAG = BleConnectionManager.class.getSimpleName();
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_BLUETOOTH_ENABLE = 2;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private Context context;
    private Activity activity;
    private String mSSID, mPassword;
    private String mService, mCharacteristic;
    private String mReadCharacteristic, mWriteCharacteristic;
    private BridgeModel bridgeModel;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private boolean isLocationPermissionGranted;
    private boolean isBluetoothEnabled;
    private boolean isScanning;

    private BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device.getName() != null) {
                        if (device.getName().contains("tito")) {
                            Log.e(TAG, "TITO device found, device name is: " + device.getName()
                                    + "and address is: " + device.getAddress());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                bluetoothAdapter.stopLeScan(bleScanCallback);
                            }
                            //Show BLE Device Control activity
                            Intent intent = new Intent(context,
                                    DeviceControlActivity.class);
                            intent.putExtra("DEVICE_NAME", device.getName());
                            intent.putExtra("DEVICE_ADDRESS", device.getAddress());
                            intent.putExtra("SSID", mSSID);
                            intent.putExtra("BRIDGE", bridgeModel);
                            intent.putExtra("PASSWORD", mPassword);
                            context.startActivity(intent);
                        }
                    } else {
                        Log.e(TAG, "BLE :: name : " + device.getName() + " add : " + device.getAddress());
                    }
                }
            });
        }
    };

    //This constructor using for bridge and node read or write operation.
    public BleConnectionManager(Context context, String mSSID, String mPassword,
                                String mService, String mCharacteristic, BridgeModel bridgeModel) {
        this.context = context;
        this.mSSID = mSSID;
        this.mPassword = mPassword;
        this.mService = mService;
        this.mCharacteristic = mCharacteristic;
        this.bridgeModel = bridgeModel;
        activity = (Activity) context;

        //set service and characteristic
        TargetGattAttributes.setTargetBleCharacteristic(mCharacteristic);
        TargetGattAttributes.setTargetBleService(mService);
    }

    public BleConnectionManager(Context context, String mSSID, String mPassword, String mService,
                                String mReadCharacteristic, String mWriteCharacteristic, BridgeModel bridgeModel) {
        this.context = context;
        this.mSSID = mSSID;
        this.mPassword = mPassword;
        this.mService = mService;
        this.mReadCharacteristic = mReadCharacteristic;
        this.mWriteCharacteristic = mWriteCharacteristic;
        this.bridgeModel = bridgeModel;

        TargetGattAttributes.setTargetBleService(mService);
        TargetGattAttributes.setTitoBleReadCharacteristic(mReadCharacteristic);
        TargetGattAttributes.setTitoBleWriteCharacteristic(mWriteCharacteristic);
    }

    public BleConnectionManager(Context context, String ssid, String psw) {
        this.context = context;
        this.mSSID = ssid;
        this.mPassword = psw;
        activity = (Activity) context;
    }

    public BleConnectionManager(Context context, String bleName, String service, String characteristic) {
        this.context = context;
        this.mService = service;
        this.mCharacteristic = characteristic;
        activity = (Activity) context;
    }

    public BleConnectionManager(Context context) {
        this.context = context;
        activity = (Activity) context;
    }

    public void StartBleScan() {
        handler = new Handler();

        // Check BLE is supporting on device
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.e(TAG, "BLE is support");
        }

        // Initialize bluetooth adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Check if bluetooth supporting device
        if (bluetoothAdapter == null) {
            Toast.makeText(context, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        // Request for enable Bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Intent intentEnableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intentEnableBt, REQUEST_BLUETOOTH_ENABLE);
            Toast.makeText(activity, "Enabling Bluetooth!!", Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, " BLE is enabled :: " + bluetoothAdapter.isEnabled());
        }

        if (checkDeviceLocationPermission())
            scanBleDevices(true);

    }

    public void initBle() {
        handler = new Handler();
        // Check BLE is supporting on device
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.e(TAG, "BLE is support");
        }

        // Initialize bluetooth adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Check if bluetooth supporting device
        if (bluetoothAdapter == null) {
            Toast.makeText(context, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        // Request for enable Bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Intent intentEnableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intentEnableBt, REQUEST_BLUETOOTH_ENABLE);
            Toast.makeText(activity, "Enabling Bluetooth!!", Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, " BLE is enabled :: " + bluetoothAdapter.isEnabled());
        }
    }

    private boolean checkDeviceLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(activity)
                        .setTitle(R.string.permission_needed)
                        .setMessage(R.string.location_permission_message)
                        .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(activity, "Without location permission App will not work properly",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .create().show();
            } else {
                //No explanation needed, we can request permission
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_BLUETOOTH_ENABLE:
                Intent intentEnableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(intentEnableBt, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                Log.e(TAG, "ACTION BLE ENABLE");
                break;

            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionGranted = true;
                    Log.d(TAG, "Location Permission Granted");

                    // Launch activity for enabling bluetooth
                    context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    Log.d(TAG, "After getting permission, now calling BLE scan");
                    scanBleDevices(true);
                }
            }
            break;
        }
    }

    private void scanBleDevices(final boolean enable) {
        if (enable) {
            Log.e(TAG, "Ready for scan BLE devices");
            isScanning = true;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        bluetoothAdapter.stopLeScan(bleScanCallback);
                        Log.d(TAG, "BLE device scan stop");
                    }
                }
            }, SCAN_PERIOD);

            isScanning = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bluetoothAdapter.startLeScan(bleScanCallback);
            }
        } else {
            Log.e(TAG, "Still something missing");
            isScanning = false;
        }
    }
}
