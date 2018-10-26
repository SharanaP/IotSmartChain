package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.NormalFlow.adapter.AdapterNonSpatialPlan;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.LocationManagerUtils;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.NonSpatialModel;
import com.example.sharan.iotsmartchain.model.RegisterIoTInfo;
import com.example.sharan.iotsmartchain.qrcodescanner.QrCodeActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

public class CreateNonSpatialActivity extends BaseActivity {
    private static String TAG = CreateNonSpatialActivity.class.getSimpleName();
    //QR-code
    private static int REQUEST_CODE_QR_SCAN = 101;
    @BindView(R.id.toolbar_ns)
    Toolbar toolbar;
    @BindView(R.id.reg_iot_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.relativeLayout_reg_iot)
    View mView;
    @BindView(R.id.listView_init_nonspatial)
    ListView mListView;
    @BindView(R.id.button_ns_done)
    Button mDoneBtn;
    @BindView(R.id.fab_manually)
    FloatingActionButton mFabManually;
    @BindView(R.id.fab_camera)
    FloatingActionButton mFabQrScanner;
    //dialog
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private boolean isLocationCheck = false;
    private boolean isCameraCheck = false;
    //location
    private LocationManager locationManager;
    private String mProvider;
    private double latitude;
    private double longitude;
    private String locationAddress;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LocationManagerUtils locationManagerUtils = null;
    private AdapterNonSpatialPlan adapterNonSpatialPlan = null;
    private ArrayList<NonSpatialModel> arrayList = new ArrayList<>();
    //server comm
    private String mUrl, token, mEmail;
    private String mDeviceId;
    private CreateNonSpatialAsync createNonSpatialAsync = null;
    private GetListOfNonSpatialAsync getListOfNonSpatialAsync = null;
    //get list of registered device info
    private ArrayAdapter arrayAdapter = null;
    private String mIotDeviceSnList[] = null;
    private ArrayList<String> mList = new ArrayList<>();
    private HashMap<String, NonSpatialModel> modelHashMap = new LinkedHashMap<>();
    private GetListOfRegDevicesAsync getListOfRegDevicesAsync = null;

    //dialog related
    private Spinner spinnerIotAdd;
    private EditText editTextIotAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_nonspatial_layout);
        injectViews();
        setupToolbar();

        //get  values url, token and email
        mUrl = App.getAppComponent().getApiServiceUrl();
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);

        //init adapter
        adapterNonSpatialPlan = new AdapterNonSpatialPlan(CreateNonSpatialActivity.this, arrayList);
        mListView.setAdapter(adapterNonSpatialPlan);

        //check location permission
        locationManagerUtils = new LocationManagerUtils(CreateNonSpatialActivity.this);
        locationManagerUtils.initLocationManager();
        boolean isCheck = locationManagerUtils.checkPermissionLM();

        //Creating the ArrayAdapter instance having the country list
        arrayAdapter = new ArrayAdapter(CreateNonSpatialActivity.this,
                android.R.layout.simple_spinner_item, mList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //get a list of registered device
        getRegisterIotDevices();

        //get list of installed iot devices API
        getListOfInstalledDevices();

        //list view on item click and check local test
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NonSpatialModel nonSpatialModel = (NonSpatialModel) parent.getAdapter().getItem(position);
                Intent intent = new Intent(CreateNonSpatialActivity.this, NonSpatialDetailActivity.class);
                intent.putExtra("NonSpatialModel", (Serializable) nonSpatialModel);
                startActivity(intent);
            }
        });

        //manually capture and send to server
        mFabManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(mView, "Manually", Snackbar.LENGTH_LONG).show();
                locationManagerUtils.getLocationLatLng();
                if (dialog != null) {
                    if (dialog.isShowing()) dialog.dismiss();
                }
                //Manually enter the all input data
                DialogManuallyDataInit("");
            }
        });

        //Scan QR code and send to server
        mFabQrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check both location and camera permission
                Snackbar.make(mView, "Qr code scanner", Snackbar.LENGTH_LONG).show();
                Intent qrIntent = new Intent(CreateNonSpatialActivity.this, QrCodeActivity.class);
                startActivityForResult(qrIntent, REQUEST_CODE_QR_SCAN);
            }
        });
    }

    private void getRegisterIotDevices() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //get a list of registered device
                getListOfRegDevicesAsync = new GetListOfRegDevicesAsync(mEmail, token,
                        CreateNonSpatialActivity.this);
                getListOfRegDevicesAsync.execute((Void) null);
            }
        });
        thread.run();
    }

    private void getListOfInstalledDevices() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //get list of installed iot devices API
                getListOfNonSpatialAsync = new GetListOfNonSpatialAsync(CreateNonSpatialActivity.this);
                getListOfNonSpatialAsync.execute((Void) null);
            }
        }).run();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Non-Spatial plan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        menuRefresh.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_refresh:
                Snackbar.make(mView, "Refresh", Snackbar.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "requestCode : " + requestCode);
        if (requestCode == 123)
            locationManagerUtils.checkPermissionLM();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "location enabled requestCode : " + requestCode);
//        Log.e(TAG, "data : " + data.toString());
        if (requestCode == 222) {
            locationManagerUtils.checkPermissionLM();
        }

        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null) return;
            Snackbar.make(mView, "QR-Code successfully Scanned... ", Snackbar.LENGTH_SHORT).show();
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.got_qr_scan_relult");
            //Display a dialog for create a Non-spatial plan
            Log.e(TAG, "Result :: " + result);
            if (result != null) DialogManuallyDataInit(result);
            else {
                Snackbar.make(mView, "Try again...", Snackbar.LENGTH_LONG).show();
            }
            Log.d(TAG, "Have scan result in your app activity :" + result);

        }
    }

    /*Display dialog : To capture info and location details for installation time*/
    private void DialogManuallyDataInit(String iotSerialNum) {
        dialog = new Dialog(CreateNonSpatialActivity.this);
        builder = new AlertDialog.Builder(CreateNonSpatialActivity.this);
        LayoutInflater layoutInflater = CreateNonSpatialActivity.this.getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_init_non_spatial_item, null);
        spinnerIotAdd = (Spinner) rootView.findViewById(R.id.spinner_iot_add);
        editTextIotAdd = (EditText) rootView.findViewById(R.id.edittext_iot_add);
        EditText editTextIotLabel = (EditText) rootView.findViewById(R.id.edittext_iot_label);
        EditText editTextIotDes = (EditText) rootView.findViewById(R.id.edittext_iot_des);
        RelativeLayout relativeLayoutLat = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_lat);
        RelativeLayout relativeLayoutLng = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_lng);
        RelativeLayout relativeLayoutAdd = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_add);

        TextView textViewLat = (TextView) rootView.findViewById(R.id.textview_lat);
        TextView textViewLng = (TextView) rootView.findViewById(R.id.textview_lng);
        TextView textViewAdd = (TextView) rootView.findViewById(R.id.textview_add);

        builder.setView(rootView);

        Log.e(TAG, "SH : dialog : reg list : " + mList);
        arrayAdapter = new ArrayAdapter(CreateNonSpatialActivity.this,
                android.R.layout.simple_spinner_item, mList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIotAdd.setAdapter(arrayAdapter);

        if (!TextUtils.isEmpty(iotSerialNum) && !iotSerialNum.isEmpty()) {
            editTextIotAdd.setText(iotSerialNum);
            editTextIotAdd.setEnabled(false);
            spinnerIotAdd.setVisibility(View.GONE);
        } else {
            editTextIotAdd.setText(iotSerialNum);
            editTextIotAdd.setEnabled(true);
            spinnerIotAdd.setVisibility(View.VISIBLE);
        }

        NonSpatialModel nonSpatialModel = new NonSpatialModel();
        latitude = locationManagerUtils.getLatitude();
        longitude = locationManagerUtils.getLongitude();
        addresses = locationManagerUtils.getAddresses();

        spinnerIotAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String strSerialNum = parent.getItemAtPosition(position).toString();
                Log.e(TAG, "Dialog item selection : strIotSn " + strSerialNum);
                editTextIotAdd.setText(strSerialNum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String strSerialNum = (String) parent.getSelectedItem();
                editTextIotAdd.setText(strSerialNum);
            }

        });

        if (latitude != -1) {
            nonSpatialModel.setLatitude(latitude);
            textViewLat.setText("" + latitude);
        } else {
            relativeLayoutLat.setVisibility(View.GONE);
        }

        if (longitude != -1) {
            nonSpatialModel.setLongitude(longitude);
            textViewLng.setText("" + longitude);
        } else {
            relativeLayoutLng.setVisibility(View.GONE);
        }

        if (addresses != null && addresses.size() > 0) {
            locationAddress = addresses.get(0).getAddressLine(0);
            textViewAdd.setText("" + locationAddress);
            nonSpatialModel.setAddress(locationAddress);
        } else {
            relativeLayoutAdd.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String deviceAdd = spinnerIotAdd.getSelectedItem().toString();
                String label = editTextIotLabel.getText().toString();
                String description = editTextIotDes.getText().toString();
                deviceAdd = editTextIotAdd.getText().toString();

                Log.e(TAG, "deviceAdd iot serial num : " + deviceAdd);
                nonSpatialModel.setIotDeviceSerialNum(deviceAdd);
                nonSpatialModel.setLabel(label);
                nonSpatialModel.setDescription(description);

                createNonSpatialAsync = new CreateNonSpatialAsync(CreateNonSpatialActivity.this, nonSpatialModel);
                createNonSpatialAsync.execute((Void) null);

                Snackbar.make(mView, "Done \n" + spinnerIotAdd.getSelectedItem().toString(),
                        Snackbar.LENGTH_LONG).show();

                //TODO IOT details and information send to server
                //show result in list view
                dialog.dismiss();
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Snackbar.make(mView, "Cancel", Snackbar.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    //Write server API for IoT installation
    public class CreateNonSpatialAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private NonSpatialModel nonSpatialModel = new NonSpatialModel();
        private boolean retVal = false;
        private String message;
        private Long timeStamp;
        private String mService;
        private String mCharacteristic;
        private String mIotDeviceSN;
        private String mLabel;
        private String mDescription;
        private double mLatitude;
        private double mLongitude;
        private String mAddress;

        public CreateNonSpatialAsync(Context context, NonSpatialModel nonSpatialModel) {
            this.context = context;
            this.nonSpatialModel = nonSpatialModel;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);
            Log.e(TAG, "DeviceId :: " + mDeviceId);

            if (nonSpatialModel != null) {
                mIotDeviceSN = nonSpatialModel.getIotDeviceSerialNum();
                mLabel = nonSpatialModel.getLabel();
                mDescription = nonSpatialModel.getDescription();
                mLatitude = nonSpatialModel.getLatitude();
                mLongitude = nonSpatialModel.getLongitude();
                mAddress = nonSpatialModel.getAddress();
            }

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("iotDeviceSN", mIotDeviceSN);
                jsonObject.put("label", mLabel);
                jsonObject.put("description", mDescription);
                jsonObject.put("latitude", mLatitude);
                jsonObject.put("longitude", mLongitude);
                jsonObject.put("address", mAddress);
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

                    if (retVal) {
                        mService = respData.getString("service");
                        mCharacteristic = respData.getString("characteristic");
                        nonSpatialModel.setService(mService);
                        nonSpatialModel.setCharacteristic(mCharacteristic);
                    }


                    nonSpatialModel.setStatus("" + retVal);
                    nonSpatialModel.setMessage(message);
                    nonSpatialModel.setTimeStamp("" + timeStamp);

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
            Utils.showProgress(CreateNonSpatialActivity.this, mView, mProgressBar, false);
            if (isSuccess) {
                Snackbar.make(mView, message, Snackbar.LENGTH_LONG).show();
                arrayList.add(nonSpatialModel);
                adapterNonSpatialPlan.notifyDataSetChanged();
            } else {
                Snackbar.make(mView, message, Snackbar.LENGTH_LONG).show();
            }
            createNonSpatialAsync = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            createNonSpatialAsync = null;
            Utils.showProgress(CreateNonSpatialActivity.this, mView, mProgressBar, false);
        }
    }

    // get a list Installed IOT server API
    public class GetListOfNonSpatialAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private NonSpatialModel nonSpatialModel = new NonSpatialModel();
        private boolean retVal = false;
        private String message;
        private Long timeStamp;
        private HashMap<String, NonSpatialModel> mListOfIotDeviceInfo = new LinkedHashMap<>();

        public GetListOfNonSpatialAsync(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
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
                    .url(mUrl + "get-installed-iot-devices")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "get-installed-iot-devices");
            Log.d(TAG, "SH : formBody  " + formBody.toString());
            Log.d(TAG, "SH : request " + request.getClass().toString());


            retVal = false;
            try {
                Response response = client.newCall(request).execute();
                Log.e(TAG, "" + response.toString());

                String authResponseStr = response.body().string();
                Log.e(TAG, "GetListOfRegDevicesAsync : authResponseStr :: " + authResponseStr);

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
                    JSONArray jsonArray = respData.getJSONArray("installedDevices");

                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));
                    Log.e(TAG, " SH : array list : " + jsonArray.toString());
                    nonSpatialModel.setStatus("" + retVal);
                    nonSpatialModel.setMessage(message);
                    nonSpatialModel.setTimeStamp("" + timeStamp);

                    if (retVal) {
                        arrayList = new ArrayList<>();
                        modelHashMap = new HashMap<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            nonSpatialModel = new NonSpatialModel();
                            nonSpatialModel.setIotDeviceSerialNum(object.getString("iot_device_sn"));
                            nonSpatialModel.setTimeStamp(Utils.convertTime(object.getLong("_time")));
                            nonSpatialModel.setLabel(object.getString("label"));
                            nonSpatialModel.setDescription(object.getString("description"));
                            nonSpatialModel.setLatitude(object.getDouble("latitude"));
                            nonSpatialModel.setLongitude(object.getDouble("longitude"));
                            nonSpatialModel.setAddress(object.getString("address"));
                            nonSpatialModel.setService(object.getString("service"));
                            nonSpatialModel.setCharacteristic(object.getString("characteristic"));

                            //add register item into hash map
                            modelHashMap.put(nonSpatialModel.getIotDeviceSerialNum().toString(), nonSpatialModel);
                        }
                    } else {
                        Log.e(TAG, message);
                    }

                    //add into array list
                    for (NonSpatialModel nonSpatialModel : modelHashMap.values()) {
                        arrayList.add(nonSpatialModel);
                    }

                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));
                    Log.e(TAG, " SH : Array of installed iot devices : " + arrayList.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at CreateNonSpatialActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at CreateNonSpatialActivity: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            Utils.showProgress(CreateNonSpatialActivity.this, mView, mProgressBar, false);
            if (isSuccess) {
                Snackbar.make(mView, message, Snackbar.LENGTH_LONG).show();
                adapterNonSpatialPlan = new AdapterNonSpatialPlan(CreateNonSpatialActivity.this, arrayList);
                mListView.setAdapter(adapterNonSpatialPlan);
                adapterNonSpatialPlan.notifyDataSetChanged();
            } else {
                Snackbar.make(mView, message, Snackbar.LENGTH_LONG).show();
            }
            getListOfNonSpatialAsync = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(CreateNonSpatialActivity.this, mView, mProgressBar, false);
            getListOfNonSpatialAsync = null;
        }
    }

    //get a list of User Registered IOT device Sensors
    public class GetListOfRegDevicesAsync extends AsyncTask<Void, Void, Boolean> {
        private String mEmail;
        private String token;
        private String deviceId;
        private Context mContext;
        private boolean retVal = false;
        private String message;
        private RegisterIoTInfo registerIoTInfo = new RegisterIoTInfo();
        // private ArrayList<RegisterIoTInfo> regDevicesList = new ArrayList<>();
        private HashMap<String, RegisterIoTInfo> registerHashMap = new LinkedHashMap<>();

        public GetListOfRegDevicesAsync(String mEmail, String token, Context mContext) {
            this.mEmail = mEmail;
            this.token = token;
            this.mContext = mContext;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            retVal = false;
            deviceId = Utils.getDeviceId(mContext);
            try {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", mEmail);
                    jsonObject.put("userId", token);
                    jsonObject.put("deviceId", deviceId);
                    jsonObject.put("isApp", "true");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                OkHttpClient client = new OkHttpClient();
                MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");
                RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(mUrl + "get-iot-devices")
                        .post(formBody)
                        .build();

                Log.d(TAG, "SH : URL " + mUrl + "get-iot-devices");
                Log.d(TAG, "SH : formBody  " + formBody.toString());
                Log.d(TAG, "SH : request " + request.getClass().toString());

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
                    JSONArray jsonArray = respData.getJSONArray("deviceList");

                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));
                    Log.e(TAG, " SH : array list : " + jsonArray.toString());

                    mList = new ArrayList<>();
                    registerHashMap = new HashMap<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        registerIoTInfo = new RegisterIoTInfo();

                        boolean isRegistered = object.getBoolean("is_registered");
                        boolean isInstalled = object.getBoolean("is_installed");

                        registerIoTInfo.setSensorName(object.getString("iot_device_sn"));
                        registerIoTInfo.setTimeStamp(Utils.convertTime(object.getLong("reg_time")));
                        registerIoTInfo.setSensorStatus("true");
                        registerIoTInfo.setDeviceType(object.getString("device_type"));
                        registerIoTInfo.setRegistered(isRegistered);
                        registerIoTInfo.setInstalled(isInstalled);

                        //add register item into hash map
                        registerHashMap.put(registerIoTInfo.getSensorName().toString(), registerIoTInfo);
                    }

                    Log.e(TAG, "SH : reg registerHashMap : " + registerHashMap.toString());


                    for (RegisterIoTInfo reg : registerHashMap.values()) {
                        mList.add(reg.getSensorName());
                    }

                    Log.e(TAG, "SH : reg iot list : " + mList.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at RegistrationActivity: get iot devices " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at RegistrationActivity: get iot devices" + e1.getMessage());
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            getListOfRegDevicesAsync = null;
            Utils.showProgress(CreateNonSpatialActivity.this, mView, mProgressBar, false);
            if (aBoolean) {
                Snackbar sb = Snackbar.make(mView, message, Snackbar.LENGTH_LONG);
                sb.show();
            } else {
                Snackbar sb = Snackbar.make(mView, message, Snackbar.LENGTH_LONG);
                sb.show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            getListOfRegDevicesAsync = null;
            Utils.showProgress(CreateNonSpatialActivity.this, mView, mProgressBar, false);
        }
    }

}
