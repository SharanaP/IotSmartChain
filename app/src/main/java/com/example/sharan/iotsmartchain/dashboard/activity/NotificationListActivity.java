package com.example.sharan.iotsmartchain.dashboard.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.adapter.ListOfNotificationAdapter;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.NotificationModel;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NotificationListActivity extends BaseActivity {
    private static String TAG = NotificationListActivity.class.getSimpleName();

    @BindView(R.id.listview_all_notification) ListView mListView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.linearLayout_no_notification) LinearLayout mLlNoNotification;
    private List<NotificationModel> mList = new ArrayList<>();

    private ListOfNotificationAdapter mAdapter;
    private GetNotificationDetailLists getNotificationDetailLists = null;
    private String mUrl, token, loginId, registrationId;

    @Override
    protected void onPause() {
        super.onPause();
    }

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


        //init and set adapter
        mAdapter = new ListOfNotificationAdapter(NotificationListActivity.this, mList);
        mListView.setAdapter(mAdapter);

        //TODO get all notifications from server and display
        getNotificationDetailLists = new GetNotificationDetailLists(NotificationListActivity.this, loginId, token, registrationId);
        getNotificationDetailLists.execute((Void)null);
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("All Notifications");

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
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_refresh:
                //TODO call API to get updated battery status
                Toast.makeText(this, " Updated notifications", Toast.LENGTH_SHORT).show();
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
        private String email;
        private String tokenId;
        private String registrationId;
        private List<NotificationModel> notificationModelList = new ArrayList<>();

        public GetNotificationDetailLists(Context context, String email, String tokenId,
                                          String registrationId) {
            this.context = context;
            this.email = email;
            this.tokenId = tokenId;
            this.registrationId = registrationId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            boolean retVal = false;
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("email", email)
                        .add("tokenid", tokenId)
                        .add("mtoken", registrationId)
                        .build();
                Request request = new Request.Builder()
                        .url(mUrl + "/notificationList")
                        .post(formBody)
                        .build();
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

                        if(respStatus.equalsIgnoreCase("true")){
                            retVal = true;
                            String respMessage = (String)jsonObject.get("message");

                            Log.d(TAG, "SH :: "+respMessage);

                            JSONArray jsonArray = jsonObject.getJSONArray("notificationlist");

                            Log.d(TAG, "SH :: "+jsonArray.toString());

                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonobj = jsonArray.getJSONObject(i);

                                NotificationModel notificationInfo = new GsonBuilder()
                                        .create()
                                        .fromJson(jsonobj.toString(), NotificationModel.class);
                                notificationModelList.add(notificationInfo);

                                Log.d(TAG, "\n SH : "+notificationInfo.toString());
                            }
                        }else{
                            retVal = false;
                        }
                        Log.d(TAG, "SH ::retVal :: "+retVal);

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
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            Log.d(TAG, "SH : "+mList.toString());
            for(NotificationModel notificationModel : notificationModelList){
                mList.add(notificationModel);
                mAdapter.notifyDataSetChanged();
            }
            if(!mList.isEmpty()) mLlNoNotification.setVisibility(View.GONE);

            getNotificationDetailLists = null;
        }
    }
}
