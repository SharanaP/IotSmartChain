package com.example.sharan.iotsmartchain.dashboard.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.adapter.AnalyticsAdapter;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.AnalyticsDataModel;
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

public class AnalyticsActivity extends BaseActivity {

    private static String TAG = AnalyticsActivity.class.getSimpleName();

    @BindView(R.id.expandableListView)ExpandableListView mExpandableListView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private AnalyticsAdapter analyticsAdapter;
    private List<AnalyticsDataModel> mParentList = new ArrayList<>();
    private HashMap<String, AnalyticsDataModel> modelHashMap = new LinkedHashMap<>();

    private String mUrl, token, loginId;
    private AnalyticsDataModel analyticsDataModel;

    private String mAnalyticsDataStr = "{\"tokenid\":\"84h39873423h823\"," +
            "\"emailid\":\"personName@gmail.com\",\"status\":\"true\",\"message\":\"Successfully\"," +
            "\"analyticsDetails\":[{\"deviceId\":\"371je1ooj293u102938\",\"deviceType\":\"office main door\"," +
            "\"temperature\":\"34\",\"numOfDoorOpen\":\"Number of door open from last two days : 135 times\"," +
            "\"humidity\":\"NA\",\"timeStamp\":\"1531368931338\"," +
            "\"details\":\"This Sensor initialized office main entrance door.\"}, " +
            "{\"deviceId\":\"371je1ooj2lkjlkjlkj02938\",\"deviceType\":\"WorkStation locker\"," +
            "\"temperature\":\"32\",\"numOfDoorOpen\":\"Number of door open from last one days : 8 times\"," +
            "\"humidity\":\"25%\",\"timeStamp\":\"1531285245000\"," +
            "\"details\":\"This Sensor initialized office work station.\"}," +
            "{\"deviceId\":\"371jesdfjhgaf43452938\",\"deviceType\":\"Home main door\"," +
            "\"temperature\":\"29\",\"numOfDoorOpen\":\"Number of door open from last two days : 10 times\"," +
            "\"humidity\":\"NA\",\"timeStamp\":\"1531119645000\",\"details\":\"This Sensor initialized main home door.\"}," +
            " {\"deviceId\":\"29847239sdjkfhskdjfh9287\",\"deviceType\":\"office locker room\"," +
            "\"temperature\":\"28\",\"numOfDoorOpen\":\"Number of door open from last two days : 3 times\"," +
            "\"humidity\":\"27\",\"timeStamp\":\"1531338345000\",\"details\":\"This Sensor initialized office locker room.\"}," +
            "{\"deviceId\":\"kjh9872482374982ajhakjsdh\",\"deviceType\":\"Home locker room\"," +
            "\"temperature\":\"30\",\"numOfDoorOpen\":\"Number of door open from last two days : 4times\"," +
            "\"humidity\":\"25\",\"timeStamp\":\"1531011959000\",\"details\":\"This Sensor initialized home locker room.\"}]}";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        Log.d(TAG, "onCreate");

        injectViews();

        setupToolbar();

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

        //get a list of analytics details
        prepareAnalyticsData();

        //init and set adapter
//        analyticsAdapter = new AnalyticsAdapter(this, mParentList, modelHashMap);
//        mExpandableListView.setAdapter(analyticsAdapter);

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //TODO change read more and less based on selection
                return false;
            }
        });

        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

    }

    private void prepareAnalyticsData() {
        Log.d(TAG, "Analytics : "+mAnalyticsDataStr);
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(mAnalyticsDataStr);
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
            jsonArray = jObject.getJSONArray("analyticsDetails");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        modelHashMap = new LinkedHashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                analyticsDataModel = new AnalyticsDataModel();
                analyticsDataModel.set_Id(object.getString("deviceId"));
                analyticsDataModel.setType(object.getString("deviceType"));
                analyticsDataModel.setDetails(object.getString("details"));
                analyticsDataModel.setTemperature(object.getString("temperature"));
                analyticsDataModel.setNumOfDoorOpen(object.getString("numOfDoorOpen"));
                analyticsDataModel.setHumidity(object.getString("humidity"));
                analyticsDataModel.setTimeStamp(object.getString("timeStamp"));
                mParentList.add(analyticsDataModel);//add into list
                //jsonArray.getString(i)
                modelHashMap.put(analyticsDataModel.get_Id(), analyticsDataModel);//add into hash map

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, " mParentList : "+mParentList.toString());
        Log.d(TAG, " modelHashMap : "+modelHashMap.toString());
        analyticsAdapter = new AnalyticsAdapter(this, mParentList, modelHashMap);
        mExpandableListView.setAdapter(analyticsAdapter);
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
                //TODO call API to get updated Analytics status
                Toast.makeText(this, "Analytics updated", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("Analytics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //getList of analytics data from server API
    public class getListOfAnalytics extends AsyncTask<Void, String, String>{

        private Context context;
        private String mEmail;
        private String mToken;
        private HashMap<String, AnalyticsDataModel> mAnalyticsHashMap;

        public getListOfAnalytics(Context context, String mEmail, String mToken,
                                  HashMap<String, AnalyticsDataModel> mAnalyticsHashMap) {
            this.context = context;
            this.mEmail = mEmail;
            this.mToken = mToken;
            this.mAnalyticsHashMap = mAnalyticsHashMap;
        }

        @Override
        protected String doInBackground(Void... voids) {

            String retVal = "false";

            OkHttpClient okHttpClient = new OkHttpClient();

            RequestBody formBody = new FormEncodingBuilder()
                    .add("email", mEmail)
                    .add("tokenid", mToken)
                    .build();
            Request request = new Request.Builder()
                    .url(mUrl + "/getAnalytics")
                    .post(formBody)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if (response.code() != 200) {
                    retVal = "false";
                } else {
                    retVal = "true";
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
                Log.e("ERROR: ", "Exception at Get a analytics : " + e.getMessage());
            } catch (NullPointerException e1){
                Log.e("ERROR: ", "null pointer Exception at analytics: " + e1.getMessage());
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("true")){

            }
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }
    }

}
