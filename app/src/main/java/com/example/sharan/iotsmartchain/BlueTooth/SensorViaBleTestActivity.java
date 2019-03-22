package com.example.sharan.iotsmartchain.BlueTooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.DashBoardActivity;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.GyroscopeModel;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class SensorViaBleTestActivity extends BaseActivity {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private static String TAG = SensorViaBleTestActivity.class.getSimpleName();
    private static boolean isShowDialog = false;
    private static int numOfLoop = 0;
    private static Float accX = 0.0f;
    private static Float gyroX = 0.0f;
    private static Float gyroY = 0.0f;
    private static Float gyroZ = 0.0f;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_message)
    View viewMessage;
    @BindView(R.id.button_connect)
    Button btnConnect;
    @BindView(R.id.textview_status)
    TextView textViewStatus;
    @BindView(R.id.edittext_input)
    EditText editTextInput;
    @BindView(R.id.button_read)
    Button buttonRead;
    @BindView(R.id.button_write)
    Button buttonWrite;
    @BindView(R.id.textview_ble_data)
    TextView textViewBleData;
    @BindView(R.id.linechartview_ble_data)
    LineChartView lineChartView;
    @BindView(R.id.button_test)
    Button buttonTest;
    @BindView(R.id.progress_bar_test)
    ProgressBar progressBarTest;
    @BindView(R.id.imageview_status)
    ImageView imageViewStatus;
    @BindView(R.id.textview_progressbar_value)
    TextView tvProgressBarValue;
    @BindView(R.id.relativeLayout_conform)
    RelativeLayout relativeLayoutConform;
    @BindView(R.id.button_conform)
    Button buttonConform;
    @BindView(R.id.imageview_conform_status)
    ImageView imageViewConform;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.relativeLayout_db)
    RelativeLayout relativeLayoutDone;
    @BindView(R.id.button_done)
    Button buttonDone;

    /*Test cases for device sensor*/
    private TextView textViewTitle;
    private TextView dialogTextMessage;
    private ImageView imageViewIcon;
    private RelativeLayout relativeLayoutCard;
    private CheckedTextView checkedTextViewDoor;
    private ImageView imageViewDoor;
    private CheckedTextView checkedTextViewOne;
    private CheckedTextView checkedTextViewTwo;
    private CheckedTextView checkedTextViewThree;
    private TextView buttonCardTest;
    private TextView textViewTestStatus;
    private RelativeLayout relativeLayoutProgressBar;
    private ProgressBar progressBarInDialog;
    private TextView tvProgressBarValueDialog;
    /*API for update sensor test status*/
    private DeviceTestUpdateAsync deviceTestUpdateAsync = null;

    /*progress bar */
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private Handler mGyroXHandler = new Handler();
    private Handler mGyroYHandler = new Handler();
    private Handler mGyroZHandler = new Handler();
    private Handler mBleReadHandler = new Handler();
    private Handler mCheckSensorMovedHandler = new Handler();
    private boolean isDataRunning = false;
    private boolean isGyroXRunning = false;
    private boolean isGyroYRunning = false;
    private boolean isGyroZRunning = false;
    private boolean isMovedRunning = false;
    private List<Line> lines = null;
    private List<PointValue> pointValuesGyroX = new ArrayList<>();
    private List<PointValue> pointValuesGyroY = new ArrayList<>();
    private List<PointValue> pointValuesGyroZ = new ArrayList<>();
    private ArrayList<GyroscopeModel> gyroDataList = new ArrayList<>();
    private GyroscopeModel gyroscopeModel = new GyroscopeModel();
    private int maxNumberOfPoints = 100;
    private int numOfLatestGyroList = 20;
    private int minNumOfPoints = 50;
    /*Read gyro x-axis */
    Runnable mRunnableGyroX = new Runnable() {
        private int i = 0;

        @Override
        public void run() {
            try {
                isGyroXRunning = true;
                LineChartData data = lineChartView.getLineChartData();
                pointValuesGyroX.add(new PointValue(i++, gyroX));
                data.getLines().get(0).setValues(new ArrayList<>(pointValuesGyroX));
                lineChartView.setLineChartData(data);
                setViewportGyroX();
                mGyroXHandler.postDelayed(this, 50);
            } catch (Exception e) {
                e.printStackTrace();
                isGyroXRunning = false;
            }
        }
    };
    /*Read gyro y-axis*/
    Runnable mRunnableGyroY = new Runnable() {
        private int i = 0;

        @Override
        public void run() {
            try {
                isGyroYRunning = true;
                LineChartData data = lineChartView.getLineChartData();
                pointValuesGyroY.add(new PointValue(i++, gyroY));
                data.getLines().get(1).setValues(new ArrayList<>(pointValuesGyroY));
                lineChartView.setLineChartData(data);
                setViewportGyroY();
                mGyroYHandler.postDelayed(this, 50);
            } catch (Exception e) {
                e.printStackTrace();
                isGyroYRunning = false;
            }
        }
    };
    /*Read gyro z-axis*/
    Runnable mRunnableGyroZ = new Runnable() {
        private int i = 0;

        @Override
        public void run() {
            try {
                isGyroZRunning = true;
                LineChartData data = lineChartView.getLineChartData();
                pointValuesGyroZ.add(new PointValue(i++, gyroZ));
                data.getLines().get(2).setValues(new ArrayList<>(pointValuesGyroZ));
                lineChartView.setLineChartData(data);
                setViewportGyroZ();
                mGyroZHandler.postDelayed(this, 50);
            } catch (Exception e) {
                e.printStackTrace();
                isGyroZRunning = false;
            }
        }
    };
    private String mDeviceName;
    private String mDeviceAddress;
    private String mIotSN;
    private BluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                textViewStatus.setText("Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            boolean isConnected = mBluetoothLeService.connect(mDeviceAddress);
            if (isConnected) {
                Log.e(TAG, "CONNECTED ");
            } else {
                Log.e(TAG, "NOT CONNECTED ");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic characteristic = null;
    private BluetoothGattCharacteristic readCharacteristic = null;
    private BluetoothGattCharacteristic writeCharacteristic = null;
    private int readCharaProp = -1;
    private int writeCharaProp = -1;
    /*Show dialog*/
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private View rootView;
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
            Log.e(TAG, "action : " + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.e(TAG, "Connected");
                textViewStatus.setText("BLE Connected");
                Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                        "BLE Connected", ALERTCONSTANT.SUCCESS);
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) progressDialog.setMessage("BLE Connected");
                    progressDialog.dismiss();
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.d(TAG, "Disconnected");
                textViewStatus.setText("BLE Disconnected");
                Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                        "BLE is disconnected and try gain for connection", ALERTCONSTANT.SUCCESS);
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        finish();
                    }
                }
            } else if (BluetoothLeService.EXTRA_DATA.equals(action)) {
                Log.e(TAG, "EXTRA_DATA" + intent.getStringExtra("EXTRA_DATA"));
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, " loop value " + numOfLoop++);
                boolean isDataFormat = false;
                /*Bundle[{com.example.bluetooth.le.EXTRA_DATA={:9.80:3.92:113.96:1.12:-3.43:-1.89:25.25:50:}
                74 65 6D 70 3A 32 37 2E 33 34 2C 20 70 72 65 73 3A 39 34 37 2E 38 33 2C 68 75 6D 3A 34 39 2E 32 31 }]*/
                String bleData = intent.getExtras().getString("com.example.bluetooth.le.EXTRA_DATA");
                textViewStatus.setText("Read BLE data Successfully");
                //Ble data pack checking format
                if (bleData.contains("{") && bleData.contains("}")) {
                    bleData = bleData.replace("{:", "");
                    bleData = bleData.replace("}", "");
                    isDataFormat = true;
                } else {
                    isDataFormat = false;
                }

                String[] arrayBleData = bleData.split(":", 0);
                Log.e(TAG, arrayBleData.toString());
                try {
                    if (arrayBleData != null && isDataFormat) {
                        accX = Float.parseFloat(arrayBleData[0].toString());
                        gyroX = Float.parseFloat(arrayBleData[3].toString());
                        gyroY = Float.parseFloat(arrayBleData[4].toString());
                        gyroZ = Float.parseFloat(arrayBleData[5].toString());

                        gyroscopeModel = new GyroscopeModel();
                        gyroscopeModel.setGyro_x(gyroX);
                        gyroscopeModel.setGyro_y(gyroY);
                        gyroscopeModel.setGyro_z(gyroZ);
                        gyroDataList.add(gyroscopeModel);
                        Log.e(TAG, " gyroDataList : " + gyroDataList.toString());
                    }
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }

                String sensorValue = "";
                try {
                    if (isDataFormat)
                        sensorValue = "Acc X : " + arrayBleData[0] + " Acc Y : " + arrayBleData[1] + " Acc Z : " + arrayBleData[2] +
                                "\nGyro X : " + arrayBleData[3] + " Gyro Y : " + arrayBleData[4] + " Gyro Z " + arrayBleData[5] +
                                "\nTemp in C : " + arrayBleData[6] +
                                "\nBattery : " + arrayBleData[7];
                    Log.d(TAG, sensorValue);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                } catch (RuntimeException rte) {
                    rte.printStackTrace();
                }
                textViewBleData.setText("");
                if (isDataFormat)
                    textViewBleData.setText(sensorValue);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                Log.e(TAG, "ALL SUPPORTED SERVICE" + " " + mBluetoothLeService.getSupportedGattServices().toString());
                List<BluetoothGattService> servicesList;
                servicesList = mBluetoothLeService.getSupportedGattServices();
                Log.e(TAG, "servicesList :: " + servicesList.toString());
                Iterator<BluetoothGattService> iter = servicesList.iterator();
                Log.e(TAG, "servicesList :: " + servicesList.toString());
                while (iter.hasNext()) {
                    BluetoothGattService bService = (BluetoothGattService) iter.next();
                    Log.e(TAG, "Start\n");
                    List<BluetoothGattCharacteristic> mListCars = bService.getCharacteristics();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        Log.e(TAG, "UUID : " + bService.getUuid().toString());
                    }
                    Log.e(TAG, "TARGET : " + TargetGattAttributes.TEST_BLE_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if (bService.getUuid().toString()
                                .equals(TargetGattAttributes.TEST_BLE_SERVICE)) {
                            Log.e("HRS SERVICE", bService.toString());
                            /*characteristic =
                                    bService.getCharacteristic(
                                            UUID.fromString(TargetGattAttributes.TEST_BLE_CHARACTERISTIC));
                            Log.e(TAG, "characteristic uuid :: " + characteristic.getUuid().toString());
                            if (characteristic != null) {
                                charaProp = characteristic.getProperties();
                                //display read char
                                // showReadCharacteristic();
                                isShowDialog = true;
                            } else {
                                isShowDialog = false;
                            }*/

                            readCharacteristic =
                                    bService.getCharacteristic(
                                            UUID.fromString(TargetGattAttributes.TEST_BLE_CHARACTERISTIC));
                            Log.e(TAG, "Read characteristic uuid :: " + readCharacteristic.getUuid().toString());

                            writeCharacteristic =
                                    bService.getCharacteristic(
                                            UUID.fromString(TargetGattAttributes.TEST_BLE_CHARACTERISTIC));
                            Log.e(TAG, "write characteristic uuid :: " + writeCharacteristic.getUuid().toString());

                            if (readCharacteristic != null && writeCharacteristic != null) {
                                readCharaProp = readCharacteristic.getProperties();
                                writeCharaProp = writeCharacteristic.getProperties();
                                //display read char
                                // showReadCharacteristic();
                                isShowDialog = true;
                            } else {
                                isShowDialog = false;
                            }
                            break; /* Once Service found, no need to rendering */
                        }
                    }
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
    /*This runnable thread for reading Iot Sensor data*/
    Runnable mBleRunnable = new Runnable() {
        int i = 0;

        @Override
        public void run() {
            try {
                isDataRunning = true;
                showReadCharacteristic();
                mBleReadHandler.postDelayed(this, 50);
            } catch (Exception ex) {
                isDataRunning = false;
            }
        }

    };
    //line chart view
    private LineChartData data;
    private int numberOfLines = 3;
    private int maxNumberOfLines = 4;//4
    private int numberOfPoints = 12;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE; //shape of chart
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    private boolean hasGradientToTransparent = false;
    //init once only for showing door option
    private boolean flagMessageOnce = false;
    /*Check Iot Sensor moved or not */
    Runnable mRunnableCheckSensorMoved = new Runnable() {
        @Override
        public void run() {
            try {
                isMovedRunning = true;
                checkDeviceMoved(dialogTextMessage, imageViewIcon, checkedTextViewOne, checkedTextViewTwo,
                        checkedTextViewThree);
                mCheckSensorMovedHandler.postDelayed(this, 30);
            } catch (Exception e) {
                e.printStackTrace();
                isMovedRunning = false;
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

    //read characteristic
    private boolean showReadCharacteristic() {
        boolean isReadChar = false;
        if ((readCharaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            if (mNotifyCharacteristic != null) {
                if (mBluetoothLeService == null) return false;
                if (mBluetoothLeService != null)
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
                isReadChar = false;
            } else {
                Log.e(TAG, "mNotifyCharacteristic is null");
            }

            if (mBluetoothLeService != null) {
                isReadChar = mBluetoothLeService.readCharacteristic(readCharacteristic);
                if ((readCharaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(
                            readCharacteristic, true);
                    byte[] data = readCharacteristic.getValue();
                    if (data != null) {
                        for (double val : data)
                            Log.e(TAG, "aaa " + val);
                    }
                }
            } else {
                Log.e(TAG, "mBluetoothLeService is null");
            }

            Log.d(TAG, "get properties : " + readCharacteristic.getProperties());
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
        return isReadChar;
    }

    //    BLE write characteristic
    private void showWriteOnBleCharacteristic(String message) {
        if (message != null && !message.isEmpty()) {
            byte[] byteMessage = message.getBytes();
            byte[] value = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                byteArrayOutputStream.write(byteMessage);
                value = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //write BLE characteristic
            if ((writeCharaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                if (writeCharacteristic != null) {
                    if (value != null) {
                        boolean isWrite = mBluetoothLeService.writeCharacteristic(writeCharacteristic, value);
                        if (isWrite) {
                            Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                                    "BLE Write Successfully", ALERTCONSTANT.SUCCESS);
                            //show alert dialog message
                            showBleGatewayDialog("Successfully write to BLE ", ALERTCONSTANT.SUCCESS, false);
                            progressDialog.setMessage("Successfully write to BLE ");

                        } else {
                            Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                                    "Fail to write input message", ALERTCONSTANT.WARNING);
                            //show alert dialog message
                            showBleGatewayDialog("Failed to set input message and try again!!!",
                                    ALERTCONSTANT.ERROR, false);
                            progressDialog.setMessage("Failed to set input message and try again!!!");
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
            }

        } else {
            Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                    "Enter input in text and it should not be empty!!!",
                    ALERTCONSTANT.WARNING);
            //Dismiss progress dialog
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
                progressDialog.dismiss();
                Log.d(TAG, "progress dialog is dismiss");
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_test);
        injectViews();
        setupToolBar();

        initAlertDialog(true);//init show local device test UI

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mIotSN = intent.getStringExtra("IotSN");
        Log.e("DEVICE_INFO :: ", mDeviceName + " " + mDeviceAddress);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        Log.d(TAG, "progress dialog started");
        progressDialog = new ProgressDialog(this, R.style.CustomDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Trying to connect BLE and Read....");
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) progressDialog.show();
        }


        //edit text input for write into BLE
        editTextInput.setText("1");
        editTextInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextInput.getWindowToken(), 0);
                }
            }
        });

        /*BLE connection */
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewBleData.setText("");
                if (btnConnect.getText().toString().equalsIgnoreCase("connect")) {
                    if (mBluetoothLeService != null) {
                        final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                        if (result) btnConnect.setText("Disconnected");
                        else btnConnect.setText("connect");
                    }
                } else {
                    if (mBluetoothLeService != null) {
                        mBluetoothLeService.disconnect();
                        btnConnect.setText("connect");
                    }
                }
            }
        });

        /*BLE Read characteristic */
        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChartView.setVisibility(View.VISIBLE);
                textViewBleData.setText("");
                if (progressDialog != null) {
                    progressDialog.setMessage("Trying to Read characteristic....");
                    if (!progressDialog.isShowing()) progressDialog.show();
                }
                //display read char
                boolean isReadChar = false;
                isReadChar = showReadCharacteristic();
                if (isReadChar) {
                    textViewStatus.setText("Read Char is Successfully ");
                    showGyroData();
                    readBleData();
                    //show progress bar
                    showProgressBar(progressBarTest, tvProgressBarValue, buttonTest);
                } else {
                    textViewStatus.setText("Ble fail to read ");
                }
                if (!progressDialog.isShowing()) progressDialog.dismiss();
            }
        });

        /*BLE Write characteristic*/
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write to BLE characteristic
                if (progressDialog != null) {
                    progressDialog.setMessage("Trying to write characteristic....");
                    if (!progressDialog.isShowing()) progressDialog.show();
                }
                showWriteOnBleCharacteristic(editTextInput.getText().toString());
            }
        });

        /*local test*/
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call ble sensor test
                boolean isTested = false;
                if (gyroscopeModel != null) {
                    testGyroscopeSensor(gyroscopeModel);
                    //show dialog message
                    showBleGatewayDialog("Start test on Ble sensor\nNOTE: Rotate or Move the sensor device",
                            ALERTCONSTANT.NONE,
                            true);
                    Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                            "Start test on Ble sensor", ALERTCONSTANT.INFO);
                }
            }
        });

        /*Conformation button */
        buttonConform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO send to API
                deviceTestUpdateAsync = new DeviceTestUpdateAsync(SensorViaBleTestActivity.this,
                        true, mIotSN);
                deviceTestUpdateAsync.execute((Void) null);

                //TODO GOTO DAHSBOARD

            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                        "Conformation Local Sensor Test is Done", ALERTCONSTANT.SUCCESS);
                Intent intentDashBroad = new Intent(SensorViaBleTestActivity.this,
                        DashBoardActivity.class);
                startActivity(intentDashBroad);
            }
        });

    }

    private void setupToolBar() {
        setSupportActionBar(toolbar);
        setTitle("Iot Sensor Test Via Ble");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_refresh);
        menuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                        "Refresh", ALERTCONSTANT.INFO);
                ClearAllRunnableCallBacks();
                StartAllRunnableCallBacks();
                break;
            case android.R.id.home:
                mBluetoothLeService.disconnect();//BLE Disconnect
                ClearAllRunnableCallBacks();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mBluetoothLeService.disconnect();//BLE Disconnect
        ClearAllRunnableCallBacks();
        finish();
    }

    /*Clear all runnable call backs*/
    private void ClearAllRunnableCallBacks() {
        if (isDataRunning) {
            isDataRunning = false;
            Log.d(TAG, "isDataRunning : " + isDataRunning);
            mBleReadHandler.removeCallbacks(mBleRunnable);
        }
        if (isGyroXRunning) {
            isGyroXRunning = false;
            Log.d(TAG, "isGyroXRunning : " + isGyroXRunning);
            mGyroXHandler.removeCallbacks(mRunnableGyroX);
        }
        if (isGyroYRunning) {
            isGyroYRunning = false;
            Log.d(TAG, "isGyroYRunning : " + isGyroYRunning);
            mGyroYHandler.removeCallbacks(mRunnableGyroY);
        }
        if (isGyroZRunning) {
            Log.d(TAG, "isGyroZRunning : " + isGyroZRunning);
            isGyroZRunning = false;
            mGyroZHandler.removeCallbacks(mRunnableGyroZ);
        }
        if (isMovedRunning) {
            Log.d(TAG, "isMovedRunning : " + isMovedRunning);
            isMovedRunning = false;
            mCheckSensorMovedHandler.removeCallbacks(mRunnableCheckSensorMoved);
        }
    }

    /*Start all runnable */
    private void StartAllRunnableCallBacks() {
        //TODO IndexOutOfBoundsException
        try {
            if (!isDataRunning) {
                Log.d(TAG, "start : isDataRunning : " + isDataRunning);
                mBleRunnable.run();
            }
            if (!isGyroXRunning) {
                Log.d(TAG, "start : isGyroXRunning : " + isGyroXRunning);
                mRunnableGyroX.run();
            }
            if (!isGyroYRunning) {
                Log.d(TAG, "start : isGyroYRunning : " + isGyroYRunning);
                mRunnableGyroY.run();
            }
            if (!isGyroZRunning) {
                Log.d(TAG, "start : isGyroZRunning : " + isGyroZRunning);
                mRunnableGyroZ.run();
            }
            if (!isMovedRunning) {
                Log.d(TAG, "start : isMovedRunning : " + isMovedRunning);
                mRunnableCheckSensorMoved.run();
            }
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            onRestart();
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
        } else {
            Log.e(TAG, "mBluetoothLeService is null for connect ");
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
        mBluetoothLeService.disconnect();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    /*Display snack bar*/
    private void displayData(String data) {
        if (data != null) {
            Log.e("DATA", data);
        }
    }

    //init all UI elements for ALERT DIALOG FOR TEST SENSOR
    private void initAlertDialog(boolean isDeviceTestUI) {
        builder = new AlertDialog.Builder(SensorViaBleTestActivity.this);
        inflater = SensorViaBleTestActivity.this.getLayoutInflater();
        rootView = inflater.inflate(R.layout.alert_message_layout, null);

        textViewTitle = (TextView) rootView.findViewById(R.id.alert_title);
        dialogTextMessage = (TextView) rootView.findViewById(R.id.alert_message);
        imageViewIcon = (ImageView) rootView.findViewById(R.id.imageview_alert_icon);

        /*Device Test cases related */
        relativeLayoutCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_test_card);
        checkedTextViewDoor = (CheckedTextView) rootView.findViewById(R.id.checkedtextview_condition);
        imageViewDoor = (ImageView) rootView.findViewById(R.id.imageview_door);
        checkedTextViewOne = (CheckedTextView) rootView.findViewById(R.id.checkedtextview_test_one);
        checkedTextViewTwo = (CheckedTextView) rootView.findViewById(R.id.checkedtextview_test_two);
        checkedTextViewThree = (CheckedTextView) rootView.findViewById(R.id.checkedtextview_test_three);
        buttonCardTest = (TextView) rootView.findViewById(R.id.button_card_test);
        textViewTestStatus = (TextView) rootView.findViewById(R.id.textview_card_status);
        progressBarInDialog = (ProgressBar) rootView.findViewById(R.id.progressbar_card_test);
        tvProgressBarValueDialog = (TextView) rootView.findViewById(R.id.textview_card_progressbar_value);
        relativeLayoutProgressBar = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_progressbar);

        buttonCardTest.setBackgroundColor(getResources().getColor(R.color.white));
        checkedTextViewOne.setChecked(false);
        checkedTextViewTwo.setChecked(false);
        checkedTextViewThree.setChecked(false);
        //default invisible all test status
        checkedTextViewOne.setVisibility(View.GONE);
        checkedTextViewTwo.setVisibility(View.GONE);
        checkedTextViewThree.setVisibility(View.GONE);
        /*Check device testing or conformation testing*/
        if (isDeviceTestUI) {
            //relativeLayoutTestConform.setVisibility(View.GONE);//card view conform is GONE
            relativeLayoutCard.setVisibility(View.VISIBLE); //card view test is Visible
        } else {
            // relativeLayoutTestConform.setVisibility(View.VISIBLE);
            relativeLayoutCard.setVisibility(View.GONE);
        }
    }

    /*Show alert constant*/
    private void showBleGatewayDialog(String message, ALERTCONSTANT alertConstant, boolean showTestCase) {
        //Show dialog
        initAlertDialog(true);
        textViewTitle.setText("Sensor Message");
        dialogTextMessage.setText(message);
        //default invisible all test status
        checkedTextViewOne.setVisibility(View.GONE);
        checkedTextViewTwo.setVisibility(View.GONE);
        checkedTextViewThree.setVisibility(View.GONE);
        checkedTextViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewTestStatus.setText("Sensor 1st test case done");
                checkedTextViewOne.setChecked(true);
                checkedTextViewOne.setCheckMarkDrawable(R.drawable.ic_check_green_24dp);
                flagMessageOnce = false;
                mRunnableCheckSensorMoved.run();
                showProgressBar(progressBarInDialog, tvProgressBarValueDialog, buttonTest);
            }
        });
        checkedTextViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewTestStatus.setText("Sensor 2nd test case done");
                checkedTextViewTwo.setChecked(true);
                checkedTextViewTwo.setCheckMarkDrawable(R.drawable.ic_check_green_24dp);
                flagMessageOnce = false;
                mRunnableCheckSensorMoved.run();
                showProgressBar(progressBarInDialog, tvProgressBarValueDialog, buttonTest);
            }
        });
        checkedTextViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewTestStatus.setText("Sensor 3rd test case done");
                checkedTextViewThree.setChecked(true);
                checkedTextViewThree.setCheckMarkDrawable(R.drawable.ic_check_green_24dp);
                flagMessageOnce = false;
                mRunnableCheckSensorMoved.run();
                showProgressBar(progressBarInDialog, tvProgressBarValueDialog, buttonTest);
            }
        });
        //check show test card option
        if (showTestCase) {
            relativeLayoutCard.setVisibility(View.VISIBLE);
        } else {
            relativeLayoutCard.setVisibility(View.GONE);
        }
        displayDialogAlertIcon(alertConstant, imageViewIcon);
        //door check
        checkedTextViewDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextViewDoor.isChecked()) {
                    checkedTextViewDoor.setCheckMarkDrawable(R.drawable.ic_error_red_cc0000_24dp);
                    checkedTextViewDoor.setChecked(false);
                    imageViewDoor.setImageDrawable(getResources().getDrawable(R.drawable.ic_door_opened));
                    textViewTestStatus.setText("Door opened");
                } else {
                    checkedTextViewDoor.setCheckMarkDrawable(R.drawable.ic_check_green_24dp);
                    checkedTextViewDoor.setChecked(true);
                    imageViewDoor.setImageDrawable(getResources().getDrawable(R.drawable.ic_door_closed));
                    textViewTestStatus.setText("Door closed");
                }
            }
        });
        builder.setView(rootView);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isConformButton();
                relativeLayoutDone.setVisibility(View.VISIBLE);
                //reset all test case
                checkedTextViewOne.setChecked(false);
                checkedTextViewTwo.setChecked(false);
                checkedTextViewThree.setChecked(false);
                //     mCheckSensorMovedHandler.removeCallbacksAndMessages(mRunnableCheckSensorMoved);//clear
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isConformButton();
                relativeLayoutDone.setVisibility(View.VISIBLE);
                checkedTextViewOne.setChecked(false);
                checkedTextViewTwo.setChecked(false);
                checkedTextViewThree.setChecked(false);
                // mCheckSensorMovedHandler.removeCallbacksAndMessages(mRunnableCheckSensorMoved);
                dialog.dismiss();
            }
        });

        if (showTestCase)
            mCheckSensorMovedHandler.postDelayed(mRunnableCheckSensorMoved, 30);
        builder.create().show();
    }

    /*Show alert constant*/
    private void displayDialogAlertIcon(ALERTCONSTANT alertConstant, ImageView imageView) {
        switch (alertConstant) {
            case INFO:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_info_purple_17a2b8_24dp));
                break;
            case ERROR:
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_red_cc0000_24dp));
                break;
            case SUCCESS:
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));
                break;
            case WARNING:
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
                break;
            case NONE:
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_none_purple_24dp));
                break;
            default:
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_none_purple_24dp));
                break;
        }
    }

    /*Show conform button based on test check*/
    private void isConformButton() {
        //show status and conform button based check on main UI
        if (checkedTextViewOne.isChecked() && checkedTextViewTwo.isChecked()
                && checkedTextViewThree.isChecked()) {
            relativeLayoutConform.setVisibility(View.VISIBLE);
            imageViewStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));
            buttonConform.setVisibility(View.VISIBLE);
        } else {
            relativeLayoutConform.setVisibility(View.INVISIBLE);
            imageViewStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
            buttonConform.setVisibility(View.GONE);
        }
    }

    /*line chart draw Gyro  */
    private void showGyroData() {
        lineChartView.setInteractive(true);
        lineChartView.setMaxZoom(1000.0f);
//        lineChartView.setInteractive(true);//todo
        lineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lines = new ArrayList<>();
        Line line = null;
        LineChartData data = null;

        for (int i = 0; i < 3; i++) {
            line = new Line();
            line.setHasLines(true);
            line.setStrokeWidth(1);
            line.setCubic(true);
            line.setHasPoints(false);
            line.setColor(ChartUtils.COLORS[i]);
            lines.add(line);
        }
        data = new LineChartData(lines);

        Axis axisX = new Axis().setName("Time in Sec");
        Axis axisY = new Axis().setHasLines(true).setName("Gyroscope in " + "\u00B0/S");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChartView.animate();
        lineChartView.setLineChartData(data);

        drawGraphGyroX();

        drawGraphGyroY();

        drawGraphGyroZ();
    }

    /*Line chart draw for gyro X*/
    private void drawGraphGyroX() {
        mGyroXHandler.postDelayed(mRunnableGyroX, 50);
    }

    /*Set line chart graph gyro X */
    private void setViewportGyroX() {
        int size = pointValuesGyroX.size();
        if (size > maxNumberOfPoints) {
            final Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
            viewport.left = size - minNumOfPoints;
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }
    }

    /*line chart draw Gyro Y */
    private void drawGraphGyroY() {
        mGyroYHandler.postDelayed(mRunnableGyroY, 50);
    }

    /*Set line chart graph gyro Y */
    private void setViewportGyroY() {
        int size = pointValuesGyroY.size();
        if (size > maxNumberOfPoints) {
            final Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
            viewport.left = size - minNumOfPoints;
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }
    }

    /*Line chart draw for gyro Z*/
    private void drawGraphGyroZ() {
        mGyroZHandler.postDelayed(mRunnableGyroZ, 50);
    }

    /*Set line chart graph gyro Z */
    private void setViewportGyroZ() {
        int size = pointValuesGyroZ.size();
        if (size > maxNumberOfPoints) {
            final Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
            viewport.left = size - minNumOfPoints;
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }
    }

    /*Ble read data */
    private void readBleData() {
        mBleReadHandler.postDelayed(mBleRunnable, 50);
    }

    /*Test gyroscope sensor...*/
    private boolean testGyroscopeSensor(GyroscopeModel currentGyroData) {
        boolean isTest = false;
        int threshold = 5;
        List<GyroscopeModel> tail = null;
        float gyroX = 0, gyroY = 0, gyroZ = 0;
        float avgGyroX = 0, avgGyroY = 0, avgGyroZ = 0;

        if (gyroDataList.size() > numOfLatestGyroList) {
            tail = gyroDataList.subList(Math.max(gyroDataList.size() - numOfLatestGyroList, 0), gyroDataList.size());
            Log.w(TAG, "Finally a last 50 values tail : " + tail.toString());
            isTest = true;
        } else {
            // Snackbar.make(viewMessage, "Wait few seconds to read data", Snackbar.LENGTH_SHORT).show();
            return isTest;
        }

        if (tail != null && isTest) {

            for (int i = 0; i < numOfLatestGyroList; i++) {
                GyroscopeModel tempGyroData = new GyroscopeModel();
                tempGyroData = tail.get(i);
                gyroX += tempGyroData.getGyro_x();
                gyroY += tempGyroData.getGyro_y();
                gyroZ += tempGyroData.getGyro_z();
            }

            avgGyroX = (gyroX / numOfLatestGyroList);
            avgGyroY = (gyroY / numOfLatestGyroList);
            avgGyroZ = (gyroZ / numOfLatestGyroList);
        }

        //set min fraction
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        avgGyroX = Float.parseFloat(df.format(avgGyroX));
        avgGyroY = Float.parseFloat(df.format(avgGyroY));
        avgGyroZ = Float.parseFloat(df.format(avgGyroZ));

        Log.w(TAG, "AVG X : " + avgGyroX);
        Log.w(TAG, "AVG Y : " + avgGyroY);
        Log.w(TAG, "AVG Z : " + avgGyroZ);
        Log.w(TAG, "CUR X : " + currentGyroData.getGyro_x());
        Log.w(TAG, "CUR Y : " + currentGyroData.getGyro_y());
        Log.w(TAG, "CUR Z : " + currentGyroData.getGyro_z());

        if ((currentGyroData.getGyro_x() > (avgGyroX + threshold) || currentGyroData.getGyro_x() < (avgGyroX - threshold)) ||
                (currentGyroData.getGyro_y() > (avgGyroY + threshold) || currentGyroData.getGyro_y() < (avgGyroY - threshold)) ||
                (currentGyroData.getGyro_z() > (avgGyroZ + threshold) || currentGyroData.getGyro_z() < (avgGyroZ - threshold))) {
            // check is required
            avgGyroX = 0.0f;
            avgGyroY = 0.0f;
            avgGyroZ = 0.0f;
            gyroDataList.clear();//reset array of gyro
            isTest = true;
        } else {
            isTest = false;
        }
        return isTest;
    }

    /*Show progress bar*/
    private void showProgressBar(ProgressBar progressBar, TextView textViewStatus, Button buttonForTest) {
        progressBar.setVisibility(View.VISIBLE);
        progressStatus = gyroDataList.size();
        // Start the lengthy operation in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (gyroDataList.size() < numOfLatestGyroList) {
                    // Try to sleep the thread for 20 milliseconds
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(gyroDataList.size());
                            // Show the progress on TextView
                            textViewStatus.setText(gyroDataList.size() + "");
                            if (gyroDataList.size() >= numOfLatestGyroList) {
                                //test button visible
                                buttonForTest.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }).start(); // Start the operation
    }

    /*Check device moved or not */
    private void checkDeviceMoved(TextView dialogTextMessage, ImageView imageViewIcon,
                                  CheckedTextView checkedTextViewOne, CheckedTextView checkedTextViewTwo,
                                  CheckedTextView checkedTextViewThree) {
        boolean isMoved = false;
        //flagMessageOnce = false;
        if (gyroscopeModel != null) {
            isMoved = testGyroscopeSensor(gyroscopeModel);
            if (isMoved) flagMessageOnce = true; //set flag dialog message
            if (isMoved) {
                dialogTextMessage.setText(R.string.did_u_moved_sensor);
                dialogTextMessage.setTextColor(getResources().getColor(R.color.color_cyan));
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));

                isSensorMoved(isMoved);//check door open or closed
                mCheckSensorMovedHandler.removeCallbacksAndMessages(mRunnableCheckSensorMoved);

                if (!checkedTextViewOne.isChecked() && !checkedTextViewTwo.isChecked() && !checkedTextViewThree.isChecked()) {
                    checkedTextViewOne.setVisibility(View.VISIBLE);
                } else if (checkedTextViewOne.isChecked() && !checkedTextViewTwo.isChecked() && !checkedTextViewThree.isChecked()) {
                    checkedTextViewTwo.setVisibility(View.VISIBLE);
                } else if (checkedTextViewOne.isChecked() && checkedTextViewTwo.isChecked() && !checkedTextViewThree.isChecked()) {
                    checkedTextViewThree.setVisibility(View.VISIBLE);
                }

            } else {
                if (!flagMessageOnce) {
                    //show message and status
                    imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
                    dialogTextMessage.setText(R.string.rotate_r_move_sensor);
                    dialogTextMessage.setTextColor(getResources().getColor(R.color.color_yellow));
                }
            }
        }
    }

    /*Device is moved then call door open or close*/
    private void isSensorMoved(boolean isMoved) {
        if (isMoved) {
            if (checkedTextViewDoor.isChecked()) {
                checkedTextViewDoor.setCheckMarkDrawable(R.drawable.ic_error_red_cc0000_24dp);
                checkedTextViewDoor.setChecked(false);
                imageViewDoor.setImageDrawable(getResources().getDrawable(R.drawable.ic_door_opened));
                textViewTestStatus.setText("Door opened");
                dialogTextMessage.setText(R.string.did_u_moved_sensor);
                dialogTextMessage.setTextColor(getResources().getColor(R.color.color_cyan));
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));
            } else {
                checkedTextViewDoor.setCheckMarkDrawable(R.drawable.ic_check_green_24dp);
                checkedTextViewDoor.setChecked(true);
                imageViewDoor.setImageDrawable(getResources().getDrawable(R.drawable.ic_door_closed));
                textViewTestStatus.setText("Door closed");
                dialogTextMessage.setText(R.string.did_u_moved_sensor);
                dialogTextMessage.setTextColor(getResources().getColor(R.color.color_cyan));
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));
            }
        } else {
            dialogTextMessage.setText(R.string.rotate_r_move_sensor);
            dialogTextMessage.setTextColor(getResources().getColor(R.color.color_yellow));
            imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
        }
    }

    /*Show Alert  message */
    private void alertMessageDialog() {
        new AlertDialog.Builder(SensorViaBleTestActivity.this)
                .setIcon(R.drawable.tito_logo_v1)
                .setTitle("Alert Message")
                .setMessage("Do you want test another IoT Sensor device then click ok!")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                SensorViaBleTestActivity.this.finish();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create().show();

    }

    /*API: Write a api to update IoT sensor test is done */
    public class DeviceTestUpdateAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private boolean retVal = false;
        private String message;
        private Long timeStamp;
        private String mDeviceId;
        private boolean isTested = false;
        private String mUrl, mEmail;
        private String token;
        private String mIotDeviceSN;


        public DeviceTestUpdateAsync(Context context, boolean isTested, String iotSn) {
            this.context = context;
            this.isTested = isTested;
            this.mIotDeviceSN = iotSn;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);

            mUrl = App.getAppComponent().getApiServiceUrl();
            token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
            mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);

            Log.e(TAG, "DeviceId :: " + mDeviceId);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("iotDeviceSN", mIotDeviceSN);
                jsonObject.put("isInstalling", "false");
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("isSpatial", "false");
                jsonObject.put("isApp", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "iot-device-installation")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "iot-device-installation");
            Log.d(TAG, "SH : formBody  " + formBody.toString());
            Log.d(TAG, "SH : request " + request.getClass().toString());


            retVal = false;
            try {
                Response response = client.newCall(request).execute();
                Log.e(TAG, "" + response.toString());

                String authResponseStr = response.body().string();
                Log.e(TAG, "authResponseStr :: " + authResponseStr);

                //Json object
                try {
                    JSONObject TestJson = new JSONObject(authResponseStr);
                    Log.e(TAG, "TestJson :: " + TestJson.toString());
                    Log.e(TAG, "TestJson : body :: " + TestJson.getString("body").toString());

                    String strData = TestJson.getString("body").toString();
                    Log.e(TAG, "strData :: " + strData.toString());

                    JSONObject respData = new JSONObject(strData);
                    retVal = respData.getBoolean("status");
                    message = respData.getString("message");
                    timeStamp = respData.getLong("timestamp");

                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at RegistrationActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at RegistrationActivity: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            Utils.showProgress(SensorViaBleTestActivity.this, relativeLayoutProgressBar,
                    progressBarInDialog, false);
            alertMessageDialog();
            if (isSuccess) {
                Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                        message, ALERTCONSTANT.SUCCESS);
            } else {
                Utils.SnackBarView(SensorViaBleTestActivity.this, mCoordinatorLayout,
                        message, ALERTCONSTANT.WARNING);
            }
            deviceTestUpdateAsync = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            deviceTestUpdateAsync = null;
            Utils.showProgress(SensorViaBleTestActivity.this, relativeLayoutProgressBar,
                    progressBarInDialog, false);
        }
    }
}



