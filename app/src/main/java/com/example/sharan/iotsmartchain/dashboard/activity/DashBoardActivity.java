package com.example.sharan.iotsmartchain.dashboard.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.FireBaseMessagModule.Config;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.fragments.AllNotificationsFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.DashBoardFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.FloorPlanFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.HomeFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.MenuFragment;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;

public class DashBoardActivity extends BaseActivity {

    private static String TAG = DashBoardActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    FloorPlanFragment floorPlanFragment = null;
    Fragment selectedFragment = null;
    int mSelectedItem;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String mUrl, token, loginId, registrationId;
    private GetNotificationCountAsync getNotificationCountAsync = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectedFragment = null;
            floorPlanFragment = null;
            mSelectedItem = item.getItemId();

            switch (item.getItemId()) {
                /*case R.id.navigation_home:
                    selectedFragment = HomeFragment.newInstance();
                    break;*/
                case R.id.navigation_dashboard:
                    selectedFragment = DashBoardFragment.newInstance();
                    break;
              /*  case R.id.navigation_Register_iot:
                    selectedFragment = RegisterNewIotFragment.newInstance();
                    break;*/
                case R.id.navigation_floorplan:
                    selectedFragment = FloorPlanFragment.newInstance();
                    floorPlanFragment = (FloorPlanFragment) selectedFragment;

                    break;
                case R.id.navigation_notifications:
                    //  selectedFragment = NotificationsFragment.newInstance();
                    selectedFragment = AllNotificationsFragment.newInstance();
                    break;
                case R.id.navigation_menu:
                    selectedFragment = MenuFragment.newInstance();
                    break;
            }

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, selectedFragment);
            fragmentTransaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        injectViews();

        setupToolbar();

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        registrationId = FirebaseInstanceId.getInstance().getToken();

        //Handle a Push notification
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "" + intent.getAction().toString());
                // checking for type intent filter
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Log.d(TAG, "" + message);

                    Toast.makeText(getApplicationContext(), "Push notification: " + message,
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        //TODO FirebaseMessaging.getInstance().subscribeToTopic("NEWS");
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        //based cunt value show badge should be visible or invisible
        setBadge(false, "");

        //Get a notification count value
        getNotificationCountAsync = new GetNotificationCountAsync(DashBoardActivity.this,
                loginId, token, registrationId);
        getNotificationCountAsync.execute((Void) null);

        // Manually Start default Fragment screen
        Fragment selectedFragment = HomeFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, DashBoardFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void setBadge(boolean isVisible, String unReadCount) {
        //Display a notification badge
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(2);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        View badge = LayoutInflater.from(this)
                .inflate(R.layout.notification_badge, bottomNavigationMenuView, false);
        TextView tv = (TextView) badge.findViewById(R.id.notifications_badge);

        if (isVisible) {
            tv.setText(unReadCount);
            itemView.addView(badge);
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.INVISIBLE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    //WebView Back button floorPlanFragment
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && DashBoardActivity.this != null && this.floorPlanFragment != null) {
//            if (this.floorPlanFragment.canGoBack())
//                this.floorPlanFragment.goBack();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        //TODO NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    //Get unread Notification count value
    public class GetNotificationCountAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String mEmail;
        private String mToken;
        private String deviceId;
        private int unReadCount;
        private String authResponseStr = null;
        private String message = null;
        private long timeStamp;

        public GetNotificationCountAsync(Context context, String mEmail, String mToken,
                                         String registrationId) {
            this.context = context;
            this.mEmail = mEmail;
            this.mToken = mToken;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mUrl = App.getAppComponent().getApiServiceUrl();
            mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            mToken = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

            boolean retVal = false;
            try {
                JSONObject jsonObject = new JSONObject();
                try {
                    deviceId = Utils.getDeviceId(context);

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
                        .url(mUrl + "notification-count")
                        .post(formBody)
                        .build();

                Log.d(TAG, "SH : URL " + mUrl + "notification-count");
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

                //Json object
                JSONObject TestJson = null;
                try {
                    TestJson = new JSONObject(authResponseStr);
                    unReadCount = TestJson.getInt("totalNotify");
                    message = TestJson.getString("message");
                    timeStamp = TestJson.getLong("timestamp");
                    if (unReadCount == 0) {
                        unReadCount = 0;
                        retVal = false;
                    } else {
                        retVal = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String strData = null;
                try {
                    strData = TestJson.getString("body").toString();
                    JSONArray jsonArray = TestJson.getJSONArray("body");
                    Log.e(TAG, "jsonArray :: " + jsonArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "strData :: " + strData.toString());

            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at Notification count value : " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean Success) {
            super.onPostExecute(Success);
            //Set value
            if (Success) {
                if (unReadCount != 0)
                    setBadge(true, "" + unReadCount);
            } else {
                setBadge(false, "" + unReadCount);
            }
            getNotificationCountAsync = null;
        }
    }

}
