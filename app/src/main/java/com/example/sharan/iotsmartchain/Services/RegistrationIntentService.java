package com.example.sharan.iotsmartchain.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.FireBaseMessagModule.Config;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    private static String TAG = RegistrationIntentService.class.getSimpleName();

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String deviceId = intent.getStringExtra("DEVICE_ID");
        String deviceName = intent.getStringExtra("DEVICE_NAME");
        String registrationId = "";

        registrationId = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "registrationId : " + registrationId);

        //Call register device info
        final String finalRegistrationId = registrationId;

        displayFireBaseRegId();

//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                registerDeviceInfo mRegisterDeviceInfo = new registerDeviceInfo(deviceId,
//                        deviceName, finalRegistrationId);
//                mRegisterDeviceInfo.execute((Void) null);
//            }
//        };
//        runnable.run();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFireBaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            // txtRegId.setText("Firebase Reg Id: " + regId);
            Log.d(TAG, "" + regId);
        else
            // txtRegId.setText("Firebase Reg Id is not received yet!");
            Log.d(TAG, "Firebase Reg Id is not received yet!");
    }
    //register a device id and info
    public class registerDeviceInfo extends AsyncTask<Void, String, String> {
        private Context context;
        private String deviceId;
        private String deviceName;
        private String registerNameId;
        private String mUrl, loginId, token;

        public registerDeviceInfo(String deviceId, String deviceName,
                                  String registerNameId) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.registerNameId = registerNameId;

            Log.d("REG", "registerNameId : " + registerNameId);

            /**read token and login id from shared preferance file**/
            mUrl = App.getAppComponent().getApiServiceUrl();

            token = App.getSharedPrefsComponent()
                    .getSharedPrefs()
                    .getString("TOKEN", "");

            loginId = App.getSharedPrefsComponent()
                    .getSharedPrefs()
                    .getString("AUTH_EMAIL_ID", "");
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormEncodingBuilder()
                    .add("device", "android")
                    .add("deviceId", deviceId)
                    .add("deviceName", deviceName)
                    .add("registerNameId", registerNameId)
                    .build();
            Request request = new Request.Builder()
                    .addHeader("email-id", loginId)
                    .addHeader("x-access-token", token)
                    .url(mUrl + "/api/device/register")
                    .post(formBody)
                    .build();

            String retVal = "false";
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    retVal = "false";
                } else {
                    retVal = "true";
                    //TODO
//                    Intent intent = new Intent(HomeScreenActivity.REGISTRATION_PROCESS);
//                    intent.putExtra("result", "success");
//                    intent.putExtra("message",response.message().toString());
//                    LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(intent);
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at register device info: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at register device info : " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}