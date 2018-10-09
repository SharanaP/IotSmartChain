package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.NormalFlow.adapter.AdapterRegisterIoTDevices;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;

public class RegisterIoTDeviceActivity extends BaseActivity {

    private static final int REQUEST_CODE_QR_SCAN = 101;
    public static int DIALOG_ADD_IOT = 1;
    private static String TAG = "RegisterIoTDeviceActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.listRegisterIoTs)
    ListView mLvRegIoTDevice;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.fab_camera)
    FloatingActionButton mFabCamera;
    @BindView(R.id.button_next)
    Button mBtnNext;
    @BindView(R.id.reg_iot_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.relativeLayout_reg_iot)
    View mView;

    private EditText mEditTextUID;
    private Dialog dialog;
    private AlertDialog.Builder builder;

    private AdapterRegisterIoTDevices adapterRegisterIoTDevices;
    private ArrayList<RegisterIoTInfo> mList;

    private String mUrl, loginId, token;
    private RegisterIoTDeviceAsync registerIoTDeviceAsync = null;
    private GetListOfRegDevicesAsync getListOfRegDevicesAsync = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_iot_device);

        injectViews();

        setupToolbar();

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

        mList = new ArrayList<>();
        adapterRegisterIoTDevices = new AdapterRegisterIoTDevices(RegisterIoTDeviceActivity.this, mList);
        mLvRegIoTDevice.setAdapter(adapterRegisterIoTDevices);

        //TODO call API to get a list of IOT device sensors
        Utils.showProgress(RegisterIoTDeviceActivity.this, mView, mProgressBar, true);
        getListOfRegDevicesAsync = new GetListOfRegDevicesAsync(loginId, token, this);
        getListOfRegDevicesAsync.execute((Void)null);

        //Floating button
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ADD_IOT);
            }
        });

        //Read QR code
        mFabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO call QR code reader Intent
                //Start the qr scan activity
                Intent i = new Intent(RegisterIoTDeviceActivity.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);

            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterIoTDeviceActivity.this, InstalConfigureIoTActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void testMessage() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra("jid", "918290819246" + "@s.whatsapp.net"); //phone number without "+" prefix
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            //   ToastHelper.MakeShortText("Whatsapp have not been installed.");
            Log.d(TAG, "Whatsapp have not been installed.");
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("Register IoT Devices");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        builder = new AlertDialog.Builder(RegisterIoTDeviceActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = RegisterIoTDeviceActivity.this.getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dailog_add_iot, null);
        mEditTextUID = (EditText) rootView.findViewById(R.id.editText_UID);
        builder.setView(rootView);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mEditTextUID.setText("");
            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, " SH : " + mEditTextUID.getText().toString().trim());
                //Iot Device id
                String mIotDeviceId = mEditTextUID.getText().toString().trim();
                if (loginId != null && !loginId.isEmpty()) {
                    if (!TextUtils.isEmpty(mIotDeviceId)) {
                        //API : Registering IOT device
                        Utils.showProgress(RegisterIoTDeviceActivity.this, mView, mProgressBar, true);
                        registerIoTDeviceAsync = new RegisterIoTDeviceAsync(RegisterIoTDeviceActivity.this,
                                loginId, mIotDeviceId);
                        registerIoTDeviceAsync.execute((Void) null);
                    } else {
                        Snackbar.make(mView, "Enter the IOT device Id and it should not be empty!!!",
                                Snackbar.LENGTH_LONG).show();
                    }
                }

                //clear data
                mEditTextUID.setText("");
            }
        });

        dialog = builder.create();

        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Log.d("BT", "COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterIoTDeviceActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)return;
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.got_qr_scan_relult");
            Log.d("BT", "Have scan result in your app activity :" + result);
//            AlertDialog alertDialog = new AlertDialog.Builder(RegisterIoTDeviceActivity.this).create();
//            alertDialog.setTitle("Scan result");
//            alertDialog.setMessage(result);
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();

            Snackbar sb = Snackbar.make(mView, "Successfully scan device ID...", Snackbar.LENGTH_LONG);
            sb.show();

            // Registering device via AWS server.
            Utils.showProgress(RegisterIoTDeviceActivity.this, mView, mProgressBar, true);
            registerIoTDeviceAsync = new RegisterIoTDeviceAsync(RegisterIoTDeviceActivity.this,
                    loginId, result);
            registerIoTDeviceAsync.execute((Void) null);
        }
    }

    /*Represents an asynchronous registration Iot device  */
    public class RegisterIoTDeviceAsync extends AsyncTask<Void, Void, Boolean> {
        private String mEmail;
        private String mIotDeviceSN;
        private String mDeviceId;
        private Context mContext;
        private boolean retVal = false;

        private String message;
        private String deviceType;
        private long timeStamp = -1;

        public RegisterIoTDeviceAsync(Context context, String email, String iotDeviceSn) {
            this.mEmail = email;
            this.mIotDeviceSN = iotDeviceSn;
            this.mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            mDeviceId = Utils.getDeviceId(mContext);
            Log.e(TAG, "DeviceId :: " + mDeviceId);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("iotDeviceSN", mIotDeviceSN);
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("isApp", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "iot-device-registration")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "iot-device-registration");
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
                    deviceType = respData.getString("deviceType");

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
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            registerIoTDeviceAsync = null;
            Utils.showProgress(mContext, mView, mProgressBar, false);
            if (success) {
                RegisterIoTInfo registerIoTInfo = new RegisterIoTInfo();
                registerIoTInfo.setSensorName(mIotDeviceSN);
                registerIoTInfo.setTimeStamp(Utils.convertTime(timeStamp));
                registerIoTInfo.setSensorStatus("true");
                registerIoTInfo.setDeviceType(deviceType);

                mList.add(registerIoTInfo);
                adapterRegisterIoTDevices.notifyDataSetChanged();

                Snackbar sb = Snackbar.make(mView, message, Snackbar.LENGTH_LONG);
                sb.show();

            } else {
                Snackbar sb = Snackbar.make(mView, message + " Try again...", Snackbar.LENGTH_LONG);
                sb.show();
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            registerIoTDeviceAsync = null;
            Utils.showProgress(RegisterIoTDeviceActivity.this, mView, mProgressBar, false);

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
                    Log.e(TAG, " SH : array list : "+jsonArray.toString());

                    mList = new ArrayList<>();
                    registerHashMap = new HashMap<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        registerIoTInfo = new RegisterIoTInfo();

                        registerIoTInfo.setSensorName(object.getString("iot_device_sn"));
                        registerIoTInfo.setTimeStamp(Utils.convertTime(object.getLong("reg_time")));
                        registerIoTInfo.setSensorStatus("true");
                        registerIoTInfo.setDeviceType(object.getString("device_type"));

                        //add register item into hash map
                        registerHashMap.put(registerIoTInfo.getSensorName().toString(), registerIoTInfo);
                    }

                    Log.e(TAG, "SH : reg registerHashMap : "+registerHashMap.toString());


                    for(RegisterIoTInfo reg : registerHashMap.values()){
                        mList.add(reg);
                    }

                    Log.e(TAG, "SH : reg iot list : "+mList.toString());


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
            registerIoTDeviceAsync = null;
            Utils.showProgress(RegisterIoTDeviceActivity.this, mView, mProgressBar, false);
            if(aBoolean){
                adapterRegisterIoTDevices.clear();
                //adapterRegisterIoTDevices.addAll(regDevicesList);
                adapterRegisterIoTDevices = new AdapterRegisterIoTDevices(mContext, mList);
                mLvRegIoTDevice.setAdapter(adapterRegisterIoTDevices);
                //adapterRegisterIoTDevices.notifyDataSetChanged();

                Snackbar sb = Snackbar.make(mView, message, Snackbar.LENGTH_LONG );
                sb.show();

            }else{
                Snackbar sb = Snackbar.make(mView, message, Snackbar.LENGTH_LONG );
                sb.show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            registerIoTDeviceAsync = null;
            Utils.showProgress(RegisterIoTDeviceActivity.this, mView, mProgressBar, false);
        }
    }
}
