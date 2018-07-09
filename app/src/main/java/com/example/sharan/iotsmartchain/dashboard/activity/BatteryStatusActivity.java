package com.example.sharan.iotsmartchain.dashboard.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.adapter.ListOfIotBatteryAdapter;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.BatteryModel;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

public class BatteryStatusActivity extends BaseActivity {
    private static String TAG = BatteryStatusActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.listView_batteryStatus)
    ListView mListView;
    private String mUrl, loginId, token;
    private String mBatteryList = "{\"tokenid\": \"84h39873423h823\",\"emailid\": \"personName@gmail.com\", " +
            "\"status\": \"true\", \"deviceBatteryInfo\": [{\"deviceId\": \"371je1ooj293u102938\", " +
            "\"deviceType\": \"office main door\", \"batteryStatus\": \"90\"}, {\"deviceId\": \"371je1ooj2lkjlkjlkj02938\"," +
            " \"deviceType\": \"WorkStation locaker\", \"batteryStatus\": \"70\"}, " +
            "{\"deviceId\": \"371jesdfjhgaf43452938\", \"deviceType\": \"Home main door\", " +
            "\"batteryStatus\": \"40\"}, {\"deviceId\": \"371rtyrtydfjhgafu102938\"," +
            " \"deviceType\": \"office locker door\", \"batteryStatus\": \"15\"}," +
            " {\"deviceId\": \"371jesdfsdfsafu102938\", \"deviceType\": \"Home locker door\", \"batteryStatus\": \"4\"}]}";

    private BatteryModel batteryModel;
    private List<BatteryModel> batteryModelList = new ArrayList<>();
    private ListOfIotBatteryAdapter mAdapter;
    private HashMap<String, BatteryModel> mListOfDevices = new LinkedHashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_status);

        injectViews();

        setupToolbar();

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        Log.d(TAG, "" + mBatteryList.toString());

        batteryModel = new BatteryModel();

        //Adapter and get a list of battery device status info
        mAdapter = new ListOfIotBatteryAdapter(BatteryStatusActivity.this, batteryModelList);
        mListView.setAdapter(mAdapter);

        readStringToArrayList();

    }

    private void readStringToArrayList() {
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(mBatteryList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String emailId = null;
        String tokenid = null;
        String status = null;
        try {
            emailId = (String) jObject.get("emailid");
            tokenid = (String) jObject.get("tokenid");
            status = (String) jObject.get("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(emailId);
        System.out.println(tokenid);
        System.out.println(status);

        JSONArray jsonArray = null;
        try {
            jsonArray = jObject.getJSONArray("deviceBatteryInfo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mListOfDevices = new LinkedHashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                batteryModel = new BatteryModel();
                batteryModel.setDeviceId(object.getString("deviceId"));
                batteryModel.setBatteryValue(object.getString("batteryStatus"));
                batteryModel.setDeviceType(object.getString("deviceType"));
                mListOfDevices.put(jsonArray.getString(i), batteryModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        batteryModelList = new ArrayList<BatteryModel>(mListOfDevices.values());
        mAdapter = new ListOfIotBatteryAdapter(BatteryStatusActivity.this, batteryModelList);
        mListView.setAdapter(mAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("Battery Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_refresh:
                //TODO call API to get updated battery status
                Toast.makeText(this, "Battery status updated", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Get a list of updated battery status
    public class getListOfBatteryStatus extends AsyncTask<Void, Void, Boolean>{
        private Context context;
        private String mEmail;
        private String mToken;
        private HashMap<String, BatteryModel> mListOfBatteryMap = new HashMap<>();

        public getListOfBatteryStatus(Context context, String mEmail, String mToken,
                                      HashMap<String, BatteryModel> mListOfBatteryMap) {
            this.context = context;
            this.mEmail = mEmail;
            this.mToken = mToken;
            this.mListOfBatteryMap = mListOfBatteryMap;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean retVal = false;

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody formBody = new FormEncodingBuilder()
                    .add("email", mEmail)
                    .add("tokenid", mToken)
                    .build();
            Request request = new Request.Builder()
                    .url(mUrl + "/getListOfBatteryStatus")
                    .post(formBody)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if (response.code() != 200) {
                    retVal = false;
                } else {
                    retVal = true;
                    String authResponseStr = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(authResponseStr);

                        String respStatus = (String)jsonObject.get("status");

                        Log.d(TAG, "SH :: "+respStatus);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at Get a battery status : " + e.getMessage());
            } catch (NullPointerException e1){
                Log.e("ERROR: ", "null pointer Exception at Notification battery status : " + e1.getMessage());
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(isSuccess){
                //TODO
            }else{
                //TODO
            }
        }
    }
}
