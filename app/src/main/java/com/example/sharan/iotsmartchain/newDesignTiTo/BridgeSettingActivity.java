package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.NetworkUtil;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.BridgeModel;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;

public class BridgeSettingActivity extends BaseActivity {
    public static final String TAG = BridgeSettingActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.buton_wifi)
    Button buttonWifi;
    @BindView(R.id.fab_done)
    FloatingActionButton fabDone;
    @BindView(R.id.text_netwrok_status)
    TextView tvNetworkStatus;
    @BindView(R.id.text_battery_value)
    TextView tvBatteryValue;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.progressView)
    View view;
    private BridgeModel bridgeModel;
    private GetCurrentStatusAsync getCurrentStatusAsync = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_setting);
        injectViews();
        getBridgeModel();
        setupToolbar();

        //get current status
        int isNetwork = NetworkUtil.getConnectivityStatus(BridgeSettingActivity.this);
        if (isNetwork == 0) {
            Utils.SnackBarView(BridgeSettingActivity.this, coordinatorLayout,
                    getString(R.string.no_internet), ALERTCONSTANT.ERROR);
        } else {
            Utils.showProgress(BridgeSettingActivity.this, view, progressBar, true);
            getCurrentStatusAsync = new GetCurrentStatusAsync(BridgeSettingActivity.this, bridgeModel);
            getCurrentStatusAsync.execute((Void) null);
        }

        /*Done */
        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*Wifi*/
        buttonWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentWifi = new Intent(BridgeSettingActivity.this,
                        BridgeWifiConfigureActivity.class);
                intentWifi.putExtra("BRIDGE", bridgeModel);
                intentWifi.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentWifi);
            }
        });
    }

    private void getBridgeModel() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bridgeModel = (BridgeModel) bundle.getSerializable("BRIDGE");
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Bridge Settings");
        if (bridgeModel != null)
            getSupportActionBar().setSubtitle("Bridge : " + bridgeModel.getGatewayUid());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        menuRefresh.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_refresh:
                Utils.SnackBarView(BridgeSettingActivity.this, coordinatorLayout, "Refresh",
                        ALERTCONSTANT.INFO);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Get current status */
    public class GetCurrentStatusAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String token, email, url;
        private boolean retVal = false;
        private String mDeviceId, message;
        private Long timeStamp;
        private BridgeModel bridgeModel;

        public GetCurrentStatusAsync(Context context, BridgeModel bridgeModel) {
            this.context = context;
            this.bridgeModel = bridgeModel;
            url = App.getAppComponent().getApiServiceUrl();
            email = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("email", email);
                jsonObject.put("userId", token);
                jsonObject.put("bridgeId", bridgeModel.getGatewayUid());
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("isApp", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url + "tito-bridge-setting")
                    .post(formBody)
                    .build();

            retVal = false;
            try {
                Response response = client.newCall(request).execute();
                Log.e(TAG, "" + response.toString());

                String authResponseStr = response.body().string();
                Log.e(TAG, "authResponseStr :: " + authResponseStr);

                //Json object
                try {
                    JSONObject TestJson = new JSONObject(authResponseStr);
                    Log.e(TAG, TestJson.toString());
                    String strData = TestJson.getString("body").toString();
                    Log.e(TAG, strData.toString());
                    JSONObject respData = new JSONObject(strData);
                    Log.e(TAG, "respData :: " + respData.toString());
                    retVal = respData.getBoolean("status");
                    message = respData.getString("message");
                    timeStamp = respData.getLong("timestamp");
                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at BridgeSettingActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at BridgeSettingActivity: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Utils.showProgress(BridgeSettingActivity.this, view, progressBar, false);
            if (aBoolean) {

            } else {

            }
            getCurrentStatusAsync = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(BridgeSettingActivity.this, view, progressBar, false);
            getCurrentStatusAsync = null;
        }
    }
}
