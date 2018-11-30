package com.example.sharan.iotsmartchain.dashboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.LocationModule.MapsActivity;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.NetworkUtil;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.NotificationDetailModel;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;

public class ViewNotificationActivity extends BaseActivity {
    private static String TAG = ViewNotificationActivity.class.getSimpleName();
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.relativeLayout_view)
    RelativeLayout view;
    @BindView(R.id.edittext_label)
    EditText editTextLabel;
    @BindView(R.id.edittext_serial_num)
    EditText editTextIotSn;
    @BindView(R.id.edittext_desp)
    EditText editTextDesp;
    @BindView(R.id.edittext_message)
    EditText editTextMessage;
    @BindView(R.id.textView_timestamp)
    TextView textViewTimeStamp;
    @BindView(R.id.cardview_imageview)
    CardView cardViewImageView;
    @BindView(R.id.imageview_url)
    ImageView imageView;
    @BindView(R.id.cardview_action)
    CardView cardViewAction;
    @BindView(R.id.textview_action)
    TextView textViewAction;
    @BindView(R.id.imageView_location)
    ImageView imageViewLocation;

    private NotificationDetailModel notificationDetailModel = new NotificationDetailModel();
    private UpdateIsReadNotifyAsync updateIsReadNotifyAsync = null;
    private String isFlow = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);
        injectViews();
        setupToolbar();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            notificationDetailModel = (NotificationDetailModel) extras.getSerializable("NotificationDetailModel");
            editTextLabel.setText(notificationDetailModel.getLabel());
            editTextDesp.setText(notificationDetailModel.getDescription());
            editTextIotSn.setText(notificationDetailModel.getGatewaySn() + " / " + notificationDetailModel.getIotDeviceSn());
            editTextMessage.setText(notificationDetailModel.getDetails());
            textViewTimeStamp.setText(Utils.convertTime(notificationDetailModel.getTimeStamp()));
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFlow = bundle.getString("FLOW");
            if (isFlow.equalsIgnoreCase("ALL_NOTIFY"))
                Log.d(TAG, "ALL_NOTIFY");
            else
                Log.d(TAG, "" + isFlow);
        }

        int isNetwork = NetworkUtil.getConnectivityStatus(ViewNotificationActivity.this);
        if (isNetwork == 0) {
            Utils.SnackBarView(ViewNotificationActivity.this, coordinatorLayout,
                    getString(R.string.no_internet), ALERTCONSTANT.ERROR);
        } else {

            Log.e(TAG, "" + notificationDetailModel.toString());
            if (notificationDetailModel.isRead()) ;
            else {
                Utils.showProgress(ViewNotificationActivity.this, view, progressBar, true);
                updateIsReadNotifyAsync = new UpdateIsReadNotifyAsync(ViewNotificationActivity.this,
                        notificationDetailModel.get_id());
                updateIsReadNotifyAsync.execute((Void) null);
            }
        }

        //GOTO google map based on click listener
        imageViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGoogleMap = new Intent(ViewNotificationActivity.this, MapsActivity.class);
                intentGoogleMap.putExtra("latitude", notificationDetailModel.getLatitude());
                intentGoogleMap.putExtra("longitude", notificationDetailModel.getLongitude());
                startActivity(intentGoogleMap);
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Notification Details");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isFlow.equalsIgnoreCase("ALL_NOTIFY")) {
                    Intent intent = new Intent(ViewNotificationActivity.this, AllNotificationDetailsActivity.class);
                    startActivity(intent);
                } else if (isFlow.equalsIgnoreCase("IOT_NOTIFY")) {
                    Intent intent = new Intent(ViewNotificationActivity.this, IotDeviceNotificationActivity.class);
                    intent.putExtra("IOT_DEVICE_SN", notificationDetailModel.getIotDeviceSn());
                    intent.putExtra("FLOW", "IOT_NOTIFY");
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.menu_refresh:
                //call API to get updated battery status
                Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFlow.equalsIgnoreCase("ALL_NOTIFY")) {
            Intent intent = new Intent(ViewNotificationActivity.this, AllNotificationDetailsActivity.class);
            startActivity(intent);
        } else if (isFlow.equalsIgnoreCase("IOT_NOTIFY")) {
            Intent intent = new Intent(ViewNotificationActivity.this, IotDeviceNotificationActivity.class);
            intent.putExtra("IOT_DEVICE_SN", notificationDetailModel.getIotDeviceSn());
            intent.putExtra("FLOW", "IOT_NOTIFY");
            startActivity(intent);
        }
        this.finish();
    }

    //Update notification is read
    public class UpdateIsReadNotifyAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String email;
        private String mUrl;
        private String deviceId;
        private String tokenId;
        private String notificationId;
        private String registrationId;
        private String authResponseStr = null;
        private String message = "";

        public UpdateIsReadNotifyAsync(Context context, String notificationId) {
            this.context = context;
            this.notificationId = notificationId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean retVal = false;
            mUrl = App.getAppComponent().getApiServiceUrl();
            email = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            tokenId = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

            JSONObject jsonObject = new JSONObject();
            try {
                deviceId = Utils.getDeviceId(context);

                jsonObject.put("email", email);
                jsonObject.put("userId", tokenId);
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("update", true);
                jsonObject.put("notificationId", notificationId);
                jsonObject.put("isApp", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "get-all-notification")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "get-all-notification");
            Log.d(TAG, "SH : formBody  " + formBody.toString());
            Log.d(TAG, "SH : request " + request.getClass().toString());

            Response response = null;
            try {
                response = client.newCall(request).execute();
                Log.e(TAG, "" + response.toString());
                authResponseStr = response.body().string();
                Log.e(TAG, "authResponseStr :: " + authResponseStr);

                if (response.code() != 200) {
                    retVal = false;
                } else {
                    retVal = true;
                    JSONObject TestJson = null;
                    try {
                        TestJson = new JSONObject(authResponseStr);
                        String strData = TestJson.getString("body").toString();
                        JSONObject jsonObjectData = new JSONObject(strData);
                        message = jsonObjectData.getString("message");
                        Log.e(TAG, "" + message);
                        retVal = jsonObjectData.getBoolean("status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "SH :: " + TestJson);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success)
                Utils.SnackBarView(ViewNotificationActivity.this, coordinatorLayout, message, ALERTCONSTANT.SUCCESS);
            else
                Utils.SnackBarView(ViewNotificationActivity.this, coordinatorLayout,
                        message, ALERTCONSTANT.ERROR);
            Utils.showProgress(ViewNotificationActivity.this, view, progressBar, false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(ViewNotificationActivity.this, view, progressBar, false);
        }
    }
}
