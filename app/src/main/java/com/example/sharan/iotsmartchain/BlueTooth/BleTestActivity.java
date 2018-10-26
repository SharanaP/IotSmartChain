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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.GyroscopeModel;

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

public class BleTestActivity extends BaseActivity {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private static String TAG = BleTestActivity.class.getSimpleName();
    //number of test case
    private static int NUM_OF_TEST_CASE = 0;

    private static int ENTER_SSID_PSW_DIALOG = 110;
    private static boolean isShowDialog = false;
    private static int numOfLoop = 0;
    private static Float accX = 0.0f;
    private static Float gyroX = 0.0f;
    private static Float gyroY = 0.0f;
    private static Float gyroZ = 0.0f;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
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

    /*progress bar */
    private int progressStatus = 0;
    private Handler handler = new Handler();

    private Handler mGyroXHandler = new Handler();
    private Handler mGyroYHandler = new Handler();
    private Handler mGyroZHandler = new Handler();
    private Handler mBleReadHandler = new Handler();
    private Handler mCheckSensorMovedHandler = new Handler();
    private List<Line> lines = null;
    private List<PointValue> pointValuesGyroX = new ArrayList<>();
    private List<PointValue> pointValuesGyroY = new ArrayList<>();
    private List<PointValue> pointValuesGyroZ = new ArrayList<>();
    private ArrayList<GyroscopeModel> gyroDataList = new ArrayList<>();
    private GyroscopeModel gyroscopeModel = new GyroscopeModel();
    private int maxNumberOfPoints = 100;
    private String mDeviceName;
    private String mDeviceAddress;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic characteristic = null;
    private int charaProp = -1;

    /*Show dialog*/
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private View rootView;
    private ProgressDialog progressDialog;

    //line chart view
    private LineChartData data;
    private int numberOfLines = 3;
    private int maxNumberOfLines = 4;//4
    private int numberOfPoints = 12;
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
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
    private int INIT_ONCE = 1;

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
                Snackbar.make(viewMessage, "BLE Connected", Snackbar.LENGTH_LONG).show();
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) progressDialog.setMessage("BLE Connected");
                    progressDialog.dismiss();
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.d(TAG, "Disconnected");
                textViewStatus.setText("BLE Disconnected");
                Snackbar.make(viewMessage, "BLE is disconnected adn try again", Snackbar.LENGTH_LONG).show();

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
//                        if (gyroDataList.size() > 10) {
//                            List<GyroscopeModel> tail = gyroDataList.subList(Math.max(gyroDataList.size() - 10, 0), gyroDataList.size());
//                            Log.e(TAG, "Finally a last ten values tail : " + tail.toString());
//                        }
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

                            characteristic =
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
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            if (mNotifyCharacteristic != null) {
                if (mBluetoothLeService == null) return false;
                if (mBluetoothLeService != null)
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
                isReadChar = false;
            }

            if (mBluetoothLeService != null) {
                isReadChar = mBluetoothLeService.readCharacteristic(characteristic);
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(
                            characteristic, true);
                }
            }

            Log.d(TAG, "get properties : " + characteristic.getProperties());
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
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                if (characteristic != null) {
                    if (value != null) {
                        boolean isWrite = mBluetoothLeService.writeCharacteristic(characteristic, value);
                        if (isWrite) {
                            Snackbar.make(viewMessage, "write Successfully ",
                                    Snackbar.LENGTH_LONG).show();
                            //show alert dialog message
                            showBleGatewayDialog("Successfully write to BLE ", ALERTCONSTANT.SUCCESS, false);
                            progressDialog.setMessage("Successfully write to BLE ");

                        } else {
                            Snackbar.make(viewMessage, "Failed to write input message",
                                    Snackbar.LENGTH_LONG).show();
                            //show alert dialog message
                            showBleGatewayDialog("Failed to set input message and try again!!!", ALERTCONSTANT.ERROR, false);
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
            Snackbar.make(viewMessage, "Enter input in text and it should not be empty!!!",
                    Snackbar.LENGTH_LONG).show();
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

        initAlertDialog();//init

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
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
                if (gyroscopeModel != null){
                    testGyroscopeSensor(gyroscopeModel);
                    //show dialog message
                    showBleGatewayDialog("Start test on Ble sensor\nNOTE: Rotate or Move the sensor device", ALERTCONSTANT.NONE,
                            true);
                    Snackbar.make(viewMessage, "Start test on Ble sensor", Snackbar.LENGTH_LONG).show();
                    imageViewStatus.setVisibility(View.VISIBLE);

//                    if(isTested){
//                        //show dialog message
//                        showBleGatewayDialog("Tested Successfully...", ALERTCONSTANT.SUCCESS,
//                                true);
//                        Snackbar.make(viewMessage, "Tested Successfully", Snackbar.LENGTH_LONG).show();
//                        imageViewStatus.setVisibility(View.VISIBLE);
//
//                        //clear last values form array and show progresses bar
//                        gyroDataList.clear();
//                        showProgressBar(progressBarTest, tvProgressBarValue, buttonTest);
//
//                    }else{
//                        //show dialog message
//                        showBleGatewayDialog("Rotate or Move the sensor device and try to test again",
//                                ALERTCONSTANT.WARNING, true);
//                    }
                }
            }
        });


    }

    private void setupToolBar() {
        setSupportActionBar(toolbar);
        setTitle("Ble Test");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_refresh);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                showMessage(viewMessage, "Refresh");
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showMessage(View view, String message) {
        Snackbar sb = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        sb.show();
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
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void displayData(String data) {
        if (data != null) {
            Log.e("DATA", data);
        }
    }

    //init all UI elements for ALERT DIALOG FOR TEST SENSOR
    private void initAlertDialog(){
        builder = new AlertDialog.Builder(BleTestActivity.this);
        inflater = BleTestActivity.this.getLayoutInflater();
        rootView = inflater.inflate(R.layout.alert_message_layout, null);

        textViewTitle = (TextView) rootView.findViewById(R.id.alert_title);
        dialogTextMessage = (TextView) rootView.findViewById(R.id.alert_message);
        imageViewIcon = (ImageView)rootView.findViewById(R.id.imageview_alert_icon);
        relativeLayoutCard = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_test_card);
        checkedTextViewDoor = (CheckedTextView)rootView.findViewById(R.id.checkedtextview_condition);
        imageViewDoor = (ImageView)rootView.findViewById(R.id.imageview_door);
        checkedTextViewOne = (CheckedTextView)rootView.findViewById(R.id.checkedtextview_test_one);
        checkedTextViewTwo = (CheckedTextView)rootView.findViewById(R.id.checkedtextview_test_two);
        checkedTextViewThree = (CheckedTextView)rootView.findViewById(R.id.checkedtextview_test_three);
        buttonCardTest = (TextView)rootView.findViewById(R.id.button_card_test);
        textViewTestStatus = (TextView)rootView.findViewById(R.id.textview_card_status);
        progressBarInDialog = (ProgressBar)rootView.findViewById(R.id.progressbar_card_test);
        tvProgressBarValueDialog = (TextView)rootView.findViewById(R.id.textview_card_progressbar_value);
        relativeLayoutProgressBar = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_progressbar);

        buttonCardTest.setBackgroundColor(getResources().getColor(R.color.white));

        checkedTextViewOne.setChecked(false);
        checkedTextViewTwo.setChecked(false);
        checkedTextViewThree.setChecked(false);

        //default invisible all test status
        checkedTextViewOne.setVisibility(View.GONE);
        checkedTextViewTwo.setVisibility(View.GONE);
        checkedTextViewThree.setVisibility(View.GONE);
    }

    /*Show alert constant*/
    private void showBleGatewayDialog(String message, ALERTCONSTANT alertConstant, boolean showTestCase) {
        //Show dialog
//        builder = new AlertDialog.Builder(BleTestActivity.this);
//        inflater = BleTestActivity.this.getLayoutInflater();
//        rootView = inflater.inflate(R.layout.alert_message_layout, null);
//
//        textViewTitle = (TextView) rootView.findViewById(R.id.alert_title);
//        dialogTextMessage = (TextView) rootView.findViewById(R.id.alert_message);
//        imageViewIcon = (ImageView)rootView.findViewById(R.id.imageview_alert_icon);
//        relativeLayoutCard = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_test_card);
//        checkedTextViewDoor = (CheckedTextView)rootView.findViewById(R.id.checkedtextview_condition);
//        imageViewDoor = (ImageView)rootView.findViewById(R.id.imageview_door);
//        checkedTextViewOne = (CheckedTextView)rootView.findViewById(R.id.checkedtextview_test_one);
//        checkedTextViewTwo = (CheckedTextView)rootView.findViewById(R.id.checkedtextview_test_two);
//        checkedTextViewThree = (CheckedTextView)rootView.findViewById(R.id.checkedtextview_test_three);
//        buttonCardTest = (Button)rootView.findViewById(R.id.button_card_test);
//        textViewTestStatus = (TextView)rootView.findViewById(R.id.textview_card_status);

        initAlertDialog();

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
                dialogTextMessage.setText("2nd Test Case done");
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
                dialogTextMessage.setText("3rd Test Case done");
                mRunnableCheckSensorMoved.run();
                showProgressBar(progressBarInDialog, tvProgressBarValueDialog, buttonTest);
            }
        });

        //check show test card option
        if(showTestCase){
            relativeLayoutCard.setVisibility(View.VISIBLE);
        }else{
            relativeLayoutCard.setVisibility(View.GONE);
        }

        switch (alertConstant){
            case INFO:
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_info_purple_17a2b8_24dp));
                break;
            case ERROR:
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_red_cc0000_24dp));
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

        //door check
        checkedTextViewDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkedTextViewDoor.isChecked()){
                    checkedTextViewDoor.setCheckMarkDrawable(R.drawable.ic_error_red_cc0000_24dp);
                    checkedTextViewDoor.setChecked(false);
                    imageViewDoor.setImageDrawable(getResources().getDrawable(R.drawable.ic_door_opened));
                    textViewTestStatus.setText("Door opened");
                }else{
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
                checkedTextViewOne.setChecked(false);
                checkedTextViewTwo.setChecked(false);
                checkedTextViewThree.setChecked(false);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedTextViewOne.setChecked(false);
                checkedTextViewTwo.setChecked(false);
                checkedTextViewThree.setChecked(false);
                dialog.dismiss();
            }
        });

        if(showTestCase)
        mCheckSensorMovedHandler.postDelayed(mRunnableCheckSensorMoved, 10);
        builder.create().show();
    }

    /*line chart draw Gyro  */
    private void showGyroData() {
        lineChartView.setInteractive(true);
        lineChartView.setZoomType(ZoomType.VERTICAL);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        lines = new ArrayList<>();
        Line line = null;
        LineChartData data = null;

        for (int i = 0; i < 3; i++) {
            line = new Line();
            line.setHasLines(true);
            line.setCubic(true);
            line.setHasPoints(false);
            line.setColor(ChartUtils.COLORS[i]);
            lines.add(line);
        }
        data = new LineChartData(lines);

        Axis axisX = new Axis().setName("Time in Sec");
        Axis axisY = new Axis().setHasLines(true).setName("Gyroscope in " + "\u00B0 /S");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChartView.animate();
        lineChartView.setLineChartData(data);

        drawGraphGyroX();

        drawGraphGyroY();

        drawGraphGyroZ();
    }

    private void drawGraphGyroX() {
        mGyroXHandler.postDelayed(mRunnableGyroX, 25);
    }

    private void setViewportGyroX() {
        int size = pointValuesGyroX.size();
        if (size > maxNumberOfPoints) {
            final Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
            viewport.left = size - 50;
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }
    }

    /*line chart draw Gyro Y */
    private void drawGraphGyroY() {
        mGyroYHandler.postDelayed(mRunnableGyroY, 25);
    }

    private void setViewportGyroY() {
        int size = pointValuesGyroY.size();
        if (size > maxNumberOfPoints) {
            final Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
            viewport.left = size - 50;
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }
    }

    /*Line chart draw for gyro Z*/
    private void drawGraphGyroZ() {
        mGyroZHandler.postDelayed(mRunnableGyroZ, 25);
    }

    private void setViewportGyroZ() {
        int size = pointValuesGyroZ.size();
        if (size > maxNumberOfPoints) {
            final Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
            viewport.left = size - 50;
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        }
    }

    /*Ble read data */
    private void readBleData() {
        mBleReadHandler.postDelayed(mBleRunnable, 25);
    }

    Runnable mBleRunnable = new Runnable() {
        int i = 0;

        @Override
        public void run() {
            showReadCharacteristic();
            mBleReadHandler.postDelayed(this, 50);
        }
    };

    Runnable mRunnableGyroX = new Runnable() {
        private int i = 0;

        @Override
        public void run() {
            LineChartData data = lineChartView.getLineChartData();
            pointValuesGyroX.add(new PointValue(i++, gyroX));
            data.getLines().get(0).setValues(new ArrayList<>(pointValuesGyroX));
            lineChartView.setLineChartData(data);
            setViewportGyroX();
            mGyroXHandler.postDelayed(this, 50);
        }
    };

    Runnable mRunnableGyroY = new Runnable() {
        private int i = 0;

        @Override
        public void run() {
            LineChartData data = lineChartView.getLineChartData();
            pointValuesGyroY.add(new PointValue(i++, gyroY));
            data.getLines().get(1).setValues(new ArrayList<>(pointValuesGyroY));
            lineChartView.setLineChartData(data);
            setViewportGyroY();
            mGyroYHandler.postDelayed(this, 50);
        }
    };

    Runnable mRunnableGyroZ = new Runnable() {
        private int i = 0;

        @Override
        public void run() {
            LineChartData data = lineChartView.getLineChartData();
            pointValuesGyroZ.add(new PointValue(i++, gyroZ));
            data.getLines().get(2).setValues(new ArrayList<>(pointValuesGyroZ));
            lineChartView.setLineChartData(data);
            setViewportGyroZ();
            mGyroZHandler.postDelayed(this, 50);
        }
    };

    Runnable mRunnableCheckSensorMoved = new Runnable() {
        @Override
        public void run() {
            checkDeviceMoved(dialogTextMessage, imageViewIcon, checkedTextViewOne, checkedTextViewTwo,
                    checkedTextViewThree);
            mCheckSensorMovedHandler.postDelayed(this, 10);

        }
    };

    /*Test gyroscope sensor...*/
    private boolean testGyroscopeSensor(GyroscopeModel currentGyroData) {
        boolean isTest = false;
        int threshold = 5;
        List<GyroscopeModel> tail = null;
        float gyroX = 0, gyroY = 0, gyroZ = 0;
        float avgGyroX = 0, avgGyroY = 0, avgGyroZ = 0;

        if (gyroDataList.size() > 50) {
            tail = gyroDataList.subList(Math.max(gyroDataList.size() - 50, 0), gyroDataList.size());
            Log.w(TAG, "Finally a last 50 values tail : " + tail.toString());
            isTest = true;
        } else {
          //  Snackbar.make(viewMessage, "Wait few seconds to read data", Snackbar.LENGTH_SHORT).show();
            return isTest;
        }

        if (tail != null && isTest) {

            for (int i = 0; i < 50; i++) {
                GyroscopeModel tempGyroData = new GyroscopeModel();
                tempGyroData = tail.get(i);
                gyroX += tempGyroData.getGyro_x();
                gyroY += tempGyroData.getGyro_y();
                gyroZ += tempGyroData.getGyro_z();
            }

            avgGyroX = (gyroX / 50);
            avgGyroY = (gyroY / 50);
            avgGyroZ = (gyroZ / 50);
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

        if ((currentGyroData.getGyro_x() > (avgGyroX+threshold) || currentGyroData.getGyro_x() < (avgGyroX-threshold)) ||
                (currentGyroData.getGyro_y() > (avgGyroY+threshold) || currentGyroData.getGyro_y() < (avgGyroY-threshold)) ||
                (currentGyroData.getGyro_z() > (avgGyroZ+threshold) || currentGyroData.getGyro_z() < (avgGyroZ-threshold))) {

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
    private void showProgressBar(ProgressBar progressBar, TextView textViewStatus, Button buttonForTest){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressStatus = gyroDataList.size();
        // Start the lengthy operation in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(gyroDataList.size() < 50){
                    // Try to sleep the thread for 20 milliseconds
                    try{
                        Thread.sleep(50);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(gyroDataList.size());
                            // Show the progress on TextView
                            textViewStatus.setText(gyroDataList.size()+"");
                            if(gyroDataList.size() >= 50) {
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
                                  CheckedTextView checkedTextViewThree){
        boolean isMoved = false;

        if (gyroscopeModel != null) {
            isMoved = testGyroscopeSensor(gyroscopeModel);

            if(isMoved){
                isSensorMoved(isMoved);//check door open or closed
                mCheckSensorMovedHandler.removeCallbacksAndMessages(mRunnableCheckSensorMoved);

                dialogTextMessage.setText(R.string.did_u_moved_sensor);
                dialogTextMessage.setTextColor(getResources().getColor(R.color.color_cyan));
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));

                if(!checkedTextViewOne.isChecked() && !checkedTextViewTwo.isChecked() && !checkedTextViewThree.isChecked()){
                    checkedTextViewOne.setVisibility(View.VISIBLE);
                    dialogTextMessage.setText(R.string.did_u_moved_sensor);
                    dialogTextMessage.setTextColor(getResources().getColor(R.color.color_cyan));
                    imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));
                }else if(checkedTextViewOne.isChecked() && !checkedTextViewTwo.isChecked() && !checkedTextViewThree.isChecked()){
                    checkedTextViewTwo.setVisibility(View.VISIBLE);
                    dialogTextMessage.setText(R.string.did_u_moved_sensor);
                    dialogTextMessage.setTextColor(getResources().getColor(R.color.color_cyan));
                    imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));
                }else if(checkedTextViewOne.isChecked() && checkedTextViewTwo.isChecked() && !checkedTextViewThree.isChecked()){
                    checkedTextViewThree.setVisibility(View.VISIBLE);
                    dialogTextMessage.setText(R.string.did_u_moved_sensor);
                    dialogTextMessage.setTextColor(getResources().getColor(R.color.color_cyan));
                    imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));
                }
            }else{
                //show message and status
                imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
                dialogTextMessage.setText(R.string.rotate_r_move_sensor);
                dialogTextMessage.setTextColor(getResources().getColor(R.color.color_yellow));
            }
        }
    }

    //Device is moved then call door open or close
    private void isSensorMoved(boolean isMoved){
        if(isMoved ){
            if(checkedTextViewDoor.isChecked()){
                checkedTextViewDoor.setCheckMarkDrawable(R.drawable.ic_error_red_cc0000_24dp);
                checkedTextViewDoor.setChecked(false);
                imageViewDoor.setImageDrawable(getResources().getDrawable(R.drawable.ic_door_opened));
                textViewTestStatus.setText("Door opened");
                dialogTextMessage.setText(R.string.did_u_moved_sensor);
            }else{
                checkedTextViewDoor.setCheckMarkDrawable(R.drawable.ic_check_green_24dp);
                checkedTextViewDoor.setChecked(true);
                imageViewDoor.setImageDrawable(getResources().getDrawable(R.drawable.ic_door_closed));
                textViewTestStatus.setText("Door closed");
                dialogTextMessage.setText(R.string.did_u_moved_sensor);
            }

            dialogTextMessage.setText(R.string.did_u_moved_sensor);
            dialogTextMessage.setTextColor(getResources().getColor(R.color.color_cyan));
            imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_24dp));

        }else{
            dialogTextMessage.setText(R.string.rotate_r_move_sensor);
            dialogTextMessage.setTextColor(getResources().getColor(R.color.color_yellow));
            imageViewIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
        }
    }
}
