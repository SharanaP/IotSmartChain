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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.NormalFlow.adapter.AdapterRegisterIoTDevices;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.AuthResponse;
import com.example.sharan.iotsmartchain.model.RegisterIoTInfo;
import com.example.sharan.iotsmartchain.qrcodescanner.QrCodeActivity;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;

public class RegisterIoTDeviceActivity extends BaseActivity {

    private static String TAG = "RegisterIoTDeviceActivity";
    private static final int REQUEST_CODE_QR_SCAN = 101;
    public static int DIALOG_ADD_IOT = 1;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.listRegisterIoTs) ListView mLvRegIoTDevice;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.fab_camera) FloatingActionButton mFabCamera;
    @BindView(R.id.button_next) Button mBtnNext;

    private EditText mEditTextUID;
    private Dialog dialog;
    private AlertDialog.Builder builder;

    private AdapterRegisterIoTDevices adapterRegisterIoTDevices;
    private ArrayList<RegisterIoTInfo> mList;

    private String mUrl, loginId, token;
    private RegisterIoTDeviceAsync registerIoTDeviceAsync;
    private GetListOfRegDevicesAync getListOfRegDevicesAync;

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

        //call API to get a list of IOT device sensors
        getListOfRegDevicesAync = new GetListOfRegDevicesAync(loginId, token, this);
        getListOfRegDevicesAync.execute((Void)null);

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
                startActivityForResult( i,REQUEST_CODE_QR_SCAN);

            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterIoTDeviceActivity.this, InstalConfigureIoTActivity.class);
                startActivity(intent);
            }
        });
    }

    private void testMessage(){
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

        switch (id){
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

        mEditTextUID = (EditText)rootView.findViewById(R.id.editText_UID);
//        mStatus = (TextView)rootView.findViewById(R.id.textView_Status);
//        mImageScanCode = (ImageView)rootView.findViewById(R.id.ImageCamera);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
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
                Log.d("SH : ", mEditTextUID.getText().toString().trim());
                RegisterIoTInfo registerIoTInfo = new RegisterIoTInfo();
                registerIoTInfo.setSensorName(mEditTextUID.getText().toString().trim());
                registerIoTInfo.setSensorStatus("Active");
                String strData = Utils.getDataFormat();
                registerIoTInfo.setTimeStamp(strData);

                mList.add(registerIoTInfo);
                adapterRegisterIoTDevices.notifyDataSetChanged();

                //TODO validate
//                if(loginId != null && !loginId.isEmpty()){
//                    registerIoTDeviceAsync = new RegisterIoTDeviceAsync(RegisterIoTDeviceActivity.this,
//                            loginId, mEditTextUID.getText().toString().trim());
//                    registerIoTDeviceAsync.execute((Void)null);
//                }
                //clear data
                mEditTextUID.setText("");
            }
        });

//        mImageScanCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Scan QR code / Bar Code
//            }
//        });

        dialog = builder.create();

        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            Log.d("BT","COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
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
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.got_qr_scan_relult");
            Log.d("BT","Have scan result in your app activity :"+ result);
            AlertDialog alertDialog = new AlertDialog.Builder(RegisterIoTDeviceActivity.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            //TODO Register IoT Device and Check is required
            /*
            * API : Connect server to Register IoT devices
            *
            * */
            registerIoTDeviceAsync = new RegisterIoTDeviceAsync(RegisterIoTDeviceActivity.this,
                    loginId, result);
            registerIoTDeviceAsync.execute((Void)null);
//TODO
//            RegisterIoTInfo registerIoTInfo = new RegisterIoTInfo();
//            registerIoTInfo.setIoT_UID(result);
//            registerIoTInfo.setRegStatus("Status : ON ");
//            registerIoTInfo.setTimeStamp(Utils.getDataFormat());
//
//            mList.add(registerIoTInfo);
//            adapterRegisterIoTDevices.notifyDataSetChanged();
        }
    }

    /*Represents an asynchronous registration Iot device  */
    public class RegisterIoTDeviceAsync extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mIoTAdd;
        private Context mContext;

        public RegisterIoTDeviceAsync(Context mContext, String mEmail, String mIotAdd) {
            this.mEmail = mEmail;
            this.mIoTAdd = mIotAdd;
            this.mContext = mContext;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormEncodingBuilder()
                    .add("email", loginId)
                    .add("tokenid", token)
                    .add("sensorid", mIoTAdd)
                    .add("isApp", "true")
                    .build();
            Request request = new Request.Builder()
                    .url(mUrl + "/regiot")
                    .post(formBody)
                    .build();

            boolean retVal = false;
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    retVal = false;
                } else {
                    retVal = true;
                    String authResponseStr = response.body().string();
//                    AuthResponse authResponse = new GsonBuilder()
//                            .create()
//                            .fromJson(authResponseStr, AuthResponse.class);
//                    String tokenStr = authResponse.getData().getToken();

                    //TODO goto DASH BROAD / HOME SCREEN
//                    Intent homeIntent = new Intent(mContext, HomeActivity.class);
//                    startActivity(homeIntent);
//                    finish();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at Register IoT device: " + e.getMessage());
            } catch (NullPointerException e1){
                Log.e("ERROR: ", "null pointer Exception at IoT device: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            registerIoTDeviceAsync = null;
            if(aBoolean){

            }
            RegisterIoTInfo registerIoTInfo = new RegisterIoTInfo();
            registerIoTInfo.setSensorName(mIoTAdd);
            registerIoTInfo.setTimeStamp(Utils.getDataFormat());

            mList.add(registerIoTInfo);
            adapterRegisterIoTDevices.notifyDataSetChanged();
        }
    }

    //get a list of User Registered IOT device Sensors
    public class GetListOfRegDevicesAync extends AsyncTask<Void, Void, Boolean>{

        private final String mEmail;
        private final String mIoTAdd;
        private Context mContext;
        private LinkedList<RegisterIoTInfo> linkedList = new LinkedList<>();

        public GetListOfRegDevicesAync(String mEmail, String mIoTAdd, Context mContext) {
            this.mEmail = mEmail;
            this.mIoTAdd = mIoTAdd;
            this.mContext = mContext;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormEncodingBuilder()
                    .add("email", loginId)
                    .add("tokenid", token)
                    .add("isApp", "true")
                    .build();
            Request request = new Request.Builder()
                    .url(mUrl + "/usersensorlist")
                    .post(formBody)
                    .build();

            boolean retVal = false;
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    retVal = false;
                } else {
                    retVal = true;
                    String authResponseStr = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(authResponseStr);

                        String respStatus = (String)jsonObject.get("status");

                        Log.d(TAG, "SH :: "+respStatus);

                        String respMessage = (String)jsonObject.get("message");

                        Log.d(TAG, "SH :: "+respMessage);

                        JSONArray jsonArray = jsonObject.getJSONArray("sensorlist");

                        Log.d(TAG, "SH :: "+jsonArray.toString());

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonobj = jsonArray.getJSONObject(i);

                            RegisterIoTInfo regInfo = new GsonBuilder()
                            .create()
                            .fromJson(jsonobj.toString(), RegisterIoTInfo.class);
                            mList.add(regInfo);

                            Log.d(TAG, "\n SH : "+regInfo.toString());
                        }
                        Log.d(TAG, "SH :: "+respStatus);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at Get a list of Register IoT device: " + e.getMessage());
            } catch (NullPointerException e1){
                Log.e("ERROR: ", "null pointer Exception at IoT device: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            registerIoTDeviceAsync = null;
            adapterRegisterIoTDevices.notifyDataSetChanged();
        }

    }
}
