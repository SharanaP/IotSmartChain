package com.example.sharan.iotsmartchain.BlueTooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_BLUETOOTH_ENABLE = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private boolean isLocationPermissionGranted;
    private boolean isBluetoothEnabled;
    private boolean isScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        // Check BLE is supporting on device
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize bluetooth adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Check if bluetooth supporting device
        if(bluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Request for enable Bluetooth
        if(!bluetoothAdapter.isEnabled()) {
            Intent intentEnableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentEnableBt, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }

        if(checkDeviceLocationPermission())
            scanBleDevices(true);
    }

    private boolean checkDeviceLocationPermission() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //Should we show an explanation?
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle(R.string.permission_needed)
                        .setMessage(R.string.location_permission_message)
                        .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Without location permission App will not work properly",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .create().show();
            } else {
                //No explanation needed, we can request permission
                ActivityCompat.requestPermissions(this,
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
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty

                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionGranted = true;
                    Log.d(TAG, "Location Permission Granted");

                    // Launch activity for enabling bluetooth
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    Log.d(TAG, "After getting permission, now calling BLE scan");
                    scanBleDevices(true);
                }
            }
            break;
        }
    }

    private void scanBleDevices(final boolean enable) {
        if(enable) {
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

    private BluetoothAdapter.LeScanCallback bleScanCallback
            = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(device.getName() != null) {
                        if(device.getName().contains("QUECTEL")) {
                            Log.e(TAG, "Nodic device found, device name is: " + device.getName()
                            +"and address is: " + device.getAddress());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                bluetoothAdapter.stopLeScan(bleScanCallback);
                            }

                            Intent intent = new Intent(MainActivity.this,
                                    DeviceControlActivity.class);
                            intent.putExtra("DEVICE_NAME", device.getName());
                            intent.putExtra("DEVICE_ADDRESS", device.getAddress());
                            startActivity(intent);
                        }
                    }else{
                        Log.e(TAG, "BLE :: name : "+device.getName()+ " add : "+device.getAddress());
                    }
                }
            });
        }
    };
}