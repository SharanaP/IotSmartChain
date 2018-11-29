package com.example.sharan.iotsmartchain.dashboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.adapter.ListOfNotificationAdapter;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.NetworkUtil;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.NotificationDetailModel;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.GsonBuilder;
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

public class IotDeviceNotificationActivity extends BaseActivity {
    private static String TAG = IotDeviceNotificationActivity.class.getSimpleName();
    @BindView(R.id.listview_all_notification)
    ListView mListView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.relativeLayout_notify)
    View mView;
    @BindView(R.id.linearLayout_no_notification)
    LinearLayout mLlNoNotification;
    private int unReadNotify = 0;
    private MenuItem menuNotify;
    private RelativeLayout badgeLayout;
    private TextView textViewUnreadNotify;
    private List<NotificationDetailModel> mList = new ArrayList<>();
    private HashMap<String, NotificationDetailModel> hashMap = new LinkedHashMap<>();
    private ListOfNotificationAdapter mAdapter;
    private GetNotificationDetailLists getNotificationDetailLists = null;
    private String mUrl, token, loginId, registrationId;
    private String iotDeviceSn;
    private String isFlow;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        injectViews();
        setupToolbar();
        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        registrationId = FirebaseInstanceId.getInstance().getToken();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            iotDeviceSn = bundle.getString("IOT_DEVICE_SN");
            if (TextUtils.isEmpty(iotDeviceSn)) {
                getSupportActionBar().setSubtitle("Notifications");
            } else {
                getSupportActionBar().setSubtitle(iotDeviceSn + " Notifications");
            }
            isFlow = bundle.getString("FLOW");
            if(isFlow == null);
            else{
                if (TextUtils.isEmpty(isFlow))
                    Log.e(TAG, isFlow);
                else
                    Log.e(TAG, isFlow);
            }

        }

        //init and set adapter
        mAdapter = new ListOfNotificationAdapter(IotDeviceNotificationActivity.this, mList);
        mListView.setAdapter(mAdapter);

        int isNetwork = NetworkUtil.getConnectivityStatus(IotDeviceNotificationActivity.this);
        if (isNetwork == 0) {
            Utils.SnackBarView(IotDeviceNotificationActivity.this, mCoordinatorLayout,
                    getString(R.string.no_internet), ALERTCONSTANT.ERROR);
        } else {
            if (TextUtils.isEmpty(iotDeviceSn)) {
                Utils.SnackBarView(IotDeviceNotificationActivity.this, mCoordinatorLayout,
                        "Check it Once again!!!", ALERTCONSTANT.ERROR);
            } else {
                Utils.showProgress(IotDeviceNotificationActivity.this, mView, mProgressBar, true);
                //get all notifications from server and display
                getNotificationDetailLists = new GetNotificationDetailLists(IotDeviceNotificationActivity.this,
                        loginId, token, registrationId, iotDeviceSn);
                getNotificationDetailLists.execute((Void) null);
            }
        }

        //list view listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationDetailModel notificationDetailModel = (NotificationDetailModel) parent.getAdapter().getItem(position);
                Intent intent = new Intent(IotDeviceNotificationActivity.this, ViewNotificationActivity.class);
                intent.putExtra("NotificationDetailModel", (Serializable) notificationDetailModel);
                intent.putExtra("FLOW", "IOT_NOTIFY");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (TextUtils.isEmpty(iotDeviceSn)) {
            getSupportActionBar().setSubtitle("Notifications");
        } else {
            getSupportActionBar().setSubtitle(iotDeviceSn + " Notifications");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_notify, menu);
        menuNotify = menu.findItem(R.id.menu_messages);
        MenuItemCompat.setActionView(menuNotify, R.layout.badge_layout);
        View view = MenuItemCompat.getActionView(menuNotify);
        textViewUnreadNotify = (TextView) view.findViewById(R.id.textview_unread_notify);
        textViewUnreadNotify.setVisibility(View.INVISIBLE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public class GetNotificationDetailLists extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String email, tokenId, registrationId;
        private String deviceId;
        private String authResponseStr = null;
        private String iotDeviceSn;
        private List<NotificationDetailModel> notificationModelList = new ArrayList<>();

        public GetNotificationDetailLists(Context context, String loginId, String token,
                                          String registrationId, String iotDeviceSn) {
            this.context = context;
            this.email = loginId;
            this.tokenId = token;
            this.registrationId = registrationId;
            this.iotDeviceSn = iotDeviceSn;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean retVal = false;
            mUrl = App.getAppComponent().getApiServiceUrl();
            email = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            tokenId = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

            try {
                JSONObject jsonObject = new JSONObject();
                try {
                    deviceId = Utils.getDeviceId(context);

                    jsonObject.put("email", email);
                    jsonObject.put("userId", tokenId);
                    jsonObject.put("deviceId", deviceId);
                    jsonObject.put("iotDeviceSn", iotDeviceSn);
                    jsonObject.put("update", false);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (response.code() != 200) {
                    retVal = false;
                } else {
                    retVal = true;

                    //Json object
                    JSONObject TestJson = null;
                    try {
                        TestJson = new JSONObject(authResponseStr);
                        Log.d(TAG, "SH :: " + TestJson);

                        String strData = null;
                        try {
                            strData = TestJson.getString("body").toString();
                            JSONArray jsonArray = TestJson.getJSONArray("body");
                            Log.e(TAG, "jsonArray :: " + jsonArray.toString());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonobj = jsonArray.getJSONObject(i);

                                NotificationDetailModel notificationInfo = new GsonBuilder()
                                        .create()
                                        .fromJson(jsonobj.toString(), NotificationDetailModel.class);
                                notificationModelList.add(notificationInfo);
                                hashMap.put(notificationInfo.get_id(), notificationInfo);
                                Log.d(TAG, "\n SH : " + notificationInfo.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at IoT device: " + e1.getMessage());
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d(TAG, "SH : " + mList.toString());
            for (NotificationDetailModel notificationDetailModel : hashMap.values()) {
                if (!notificationDetailModel.isRead()) unReadNotify++;
                mList.add(notificationDetailModel);
                mAdapter.notifyDataSetChanged();
            }
            //set textViewUnreadNotify value in toolbar
            if (textViewUnreadNotify != null && toolbar != null) {
                if (unReadNotify != 0) {
                    textViewUnreadNotify.setVisibility(View.VISIBLE);
                    textViewUnreadNotify.setText("" + unReadNotify);
                } else {
                    textViewUnreadNotify.setVisibility(View.INVISIBLE);
                }
            }

            if (!mList.isEmpty()) mLlNoNotification.setVisibility(View.GONE);
            Utils.showProgress(IotDeviceNotificationActivity.this, mView, mProgressBar, false);
            getNotificationDetailLists = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(IotDeviceNotificationActivity.this, mView, mProgressBar, false);
        }
    }

}
