package com.example.sharan.iotsmartchain.dashboard.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.FireBaseMessagModule.Config;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.adapter.NotificationGridAdapter;
import com.example.sharan.iotsmartchain.dashboard.fragments.AllNotificationsFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.DashBoardFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.FloorPlanFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.HomeFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.MenuFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.NotificationsFragment;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.RegisterIoTInfo;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
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

        Log.d(TAG, mUrl);
        Log.d(TAG, loginId);
        Log.d(TAG, token);
        Log.d(TAG, registrationId);

        //Handle a Push notification
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, ""+intent.getAction().toString());
                // checking for type intent filter
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Log.d(TAG, ""+message);

                    Toast.makeText(getApplicationContext(), "Push notification: " + message,
                            Toast.LENGTH_LONG).show();
                }
            }
        };

        //TODO FirebaseMessaging.getInstance().subscribeToTopic("NEWS");

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        //TODO based cunt value show badge should be visible or invisible
        setBadge(false , "");

        //TODO
//        token = "c9013c9f-f189-4c2d-93eb-fe7d9c57fef4";
//        loginId = "sharan@gmail.com";
//        registrationId = "cdNDSWda5z0:APA91bE7U3u4eOTjqi8LdLDqRDsCLWgXXh-3Y7H3zLSUcbvO6M1Sz2E0yA_l1LLHASMC32vafGbLeP6ra4VgUJKuE281XSjGnncBaYjUsdalmn5OHRQT5oo47x1RH9XZWeHiA90N1gl0gma4xusNCSpQ2EjpDiCHFw";

        //Get a notification count value
        getNotificationCountAsync = new GetNotificationCountAsync(DashBoardActivity.this,
                loginId, token, registrationId);
        getNotificationCountAsync.execute((Void)null);

        // Manually Start default Fragment screen
        Fragment selectedFragment = HomeFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, DashBoardFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void setBadge(boolean isVisible, String unReadCount){
        //Display a notification badge
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(2);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        View badge = LayoutInflater.from(this)
                .inflate(R.layout.notification_badge, bottomNavigationMenuView, false);
        TextView tv = (TextView)badge.findViewById(R.id.notifications_badge);

        if(isVisible){
            tv.setText(unReadCount);
            itemView.addView(badge);
            badge.setVisibility(View.VISIBLE);
        }else{
            badge.setVisibility(View.INVISIBLE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("iSmartLink");
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
        this.finish();
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
    public class GetNotificationCountAsync extends AsyncTask<Void, Void, Boolean>{
        private Context context;
        private String mEmail;
        private String mToken;
        private String registrationId;
        private String unReadCount = "";

        public GetNotificationCountAsync(Context context, String mEmail, String mToken,
                                         String registrationId) {
            this.context = context;
            this.mEmail = mEmail;
            this.mToken = mToken;
            this.registrationId = registrationId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormEncodingBuilder()
                    .add("email", mEmail)
                    .add("tokenid", mToken)
                    .add("mtoken", registrationId)
                    .build();
            Request request = new Request.Builder()
                    .url(mUrl + "/notificationCountValue")
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
                        //{"data":{"count":"0"},"status":"true"}
                        if(respStatus.equalsIgnoreCase("true")){
                            retVal = true;

                            JSONObject dataJsonObj = jsonObject.getJSONObject("data");
                            String count = dataJsonObj.getString("count");
                            Log.d(TAG, "count :: "+unReadCount);
                            if(count.equals("0")){
                                unReadCount = "";
                            }else{
                                unReadCount = count;
                            }
                        }else{
                            retVal = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at Get a notification count value : " + e.getMessage());
            } catch (NullPointerException e1){
                Log.e("ERROR: ", "null pointer Exception at Notification count value : " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean Success) {
            super.onPostExecute(Success);
            //Set value
            if(Success){
                if(!unReadCount.isEmpty())
                setBadge(true , unReadCount);
            }else{
                setBadge(false , unReadCount);
            }
            getNotificationCountAsync = null;
        }
    }

}
