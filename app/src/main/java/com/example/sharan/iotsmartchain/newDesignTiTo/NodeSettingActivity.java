package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.NetworkUtil;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.EndNodeModel;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class NodeSettingActivity extends BaseActivity {
    public static final String TAG = NodeSettingActivity.class.getSimpleName();
    public static final int CONTACT_PICKER_REQUEST = 111;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    /*Battery*/
    @BindView(R.id.switch_battery_alert)
    SwitchCompat switchBatteryAlert;
    @BindView(R.id.text_battery_value)
    TextView currentBatteryVal;
    /*Motion*/
    @BindView(R.id.text_lasttime_motion)
    TextView textLastMotionTimeStamp;
    @BindView(R.id.switch_motion_alert)
    SwitchCompat switchAlertAllTypes;
    @BindView(R.id.switch_motion_push_alert)
    SwitchCompat switchPushNotification;
    @BindView(R.id.switch_motion_sms_alert)
    SwitchCompat switchSMSAlert;
    @BindView(R.id.switch_motion_call_alert)
    SwitchCompat switchCallAlert;
    @BindView(R.id.switch_motion_email_alert)
    SwitchCompat switchEmailAlert;
    /*Alert when user in home or away*/
    @BindView(R.id.switch_home_alert)
    SwitchCompat switchAlertAtHome;
    @BindView(R.id.switch_away_alert)
    SwitchCompat switchAlertAway;
    @BindView(R.id.switch_night_alert)
    SwitchCompat switchAlertNight;
    /*contact */
    @BindView(R.id.fab_add_contact)
    CircleImageView mImageContacts;
    @BindView(R.id.button_save)
    Button buttonSave;
    @BindView(R.id.rl_addPerson)
    LinearLayout linearlayout;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.progressView)
    View view;
    private TextView textView1;
    private EndNodeModel nodeModel;
    private ArrayList<ContactResult> results = new ArrayList<>();
    private NodeSettingAsync nodeSettingAsync = null;
    private ArrayList<String> contactList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_settings);
        injectViews();
        getNodeModel();//get Node data
        setupToolbar();

        setDefaultSwitch();

        mImageContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleContactPicker();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO validate inputs and send to server
            }
        });

        switchAlertAllTypes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchPushNotification.setChecked(true);
                    switchEmailAlert.setChecked(true);
                    switchCallAlert.setChecked(true);
                    switchSMSAlert.setChecked(true);
                } else {
                    switchPushNotification.setChecked(true);
                    switchEmailAlert.setChecked(false);
                    switchCallAlert.setChecked(false);
                    switchSMSAlert.setChecked(false);
                }
            }
        });

        switchPushNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        switchEmailAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        switchCallAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        switchSMSAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        int isNetwork = NetworkUtil.getConnectivityStatus(NodeSettingActivity.this);
        if (isNetwork == 0) {
            Utils.SnackBarView(NodeSettingActivity.this, coordinatorLayout,
                    getString(R.string.no_internet), ALERTCONSTANT.ERROR);
        } else {
            Utils.showProgress(NodeSettingActivity.this, view, progressBar, true);
            nodeSettingAsync = new NodeSettingAsync(NodeSettingActivity.this, nodeModel, contactList);
            nodeSettingAsync.execute((Void) null);
        }
    }

    /*Set default switches*/
    private void setDefaultSwitch() {
        switchBatteryAlert.setChecked(true);
        switchBatteryAlert.setClickable(false);
        switchPushNotification.setChecked(true);
        switchPushNotification.setClickable(false);
    }

    /*Contacts : Multiple contact picker */
    private void multipleContactPicker() {

        new MultiContactPicker.Builder(NodeSettingActivity.this) //Activity/fragment context
                .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(NodeSettingActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(NodeSettingActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .setTitleText("Select Contacts") //Optional - default: Select Contacts
                .setSelectedContacts(results) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                .setActivityAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
               /* List<ContactResult> results = MultiContactPicker.obtainResult(data);
                Log.d("MyTag", results.get(0).getDisplayName())*/

                //clear result and get new list
                results.clear();
                linearlayout.removeAllViews();
                //set new contacts list
                results.addAll(MultiContactPicker.obtainResult(data));
                if (results.size() > 0 && results.size() <= 5) {
                    Log.d(TAG, results.get(0).getDisplayName() + " # " + results.get(0).getPhoneNumbers());

                    for (ContactResult contactResult : results) {
                        Log.e(TAG, "Name : " + contactResult.getDisplayName());
                        ArrayList<PhoneNumber> list = new ArrayList<>();
                        list.addAll(contactResult.getPhoneNumbers());
                        DisplayAddContactList(textView1, contactResult.getDisplayName(), list.get(0).toString());
                        //  mapList.put(contactResult.getDisplayName(), contactResult);
                    }

                } else {
                    Utils.SnackBarView(NodeSettingActivity.this, coordinatorLayout,
                            "Not more then 5 contacts", ALERTCONSTANT.WARNING);
                }

            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }

    private void getNodeModel() {
        //get bridge info
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            nodeModel = (EndNodeModel) bundle.getSerializable("NODE");
            Log.e(TAG, "NODE : " + nodeModel.toString());
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Node Settings");
        if (nodeModel != null)
            getSupportActionBar().setSubtitle("Node : " + nodeModel.getEndNodeUid());
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
                Utils.SnackBarView(NodeSettingActivity.this, coordinatorLayout, "Refresh",
                        ALERTCONSTANT.INFO);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DisplayAddContactList(TextView textView1, String PersonName, String phone) {
        textView1 = new TextView(NodeSettingActivity.this);
        textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView1.setText(PersonName);
        textView1.setTextColor(Color.WHITE);
        textView1.setTag(phone.toString());
        textView1.setBackgroundColor(0xFF446db2); // hex color 0xAARRGGBB
        textView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_red_24dp, 0);
        textView1.setPadding(5, 5, 5, 5); // in pixels (left, top, right, bottom)
        textView1.setTextSize(15.0f);
        textView1.setGravity(Gravity.CENTER);
        textView1.setBackgroundResource(R.drawable.roundedcornerselector);
        linearlayout.addView(textView1, 0);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearlayout.removeView(v);
                // displayList();
            }
        });
    }

    /*Update setting details */
    public class NodeSettingAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private EndNodeModel endNodeModel;
        private ArrayList<String> contactList = new ArrayList<>();
        private boolean retVal = false;
        private String token, url, email;
        private String deviceId, message;
        private long timeStamp;

        public NodeSettingAsync(Context context, EndNodeModel endNodeModel, ArrayList<String> contactList) {
            this.context = context;
            this.endNodeModel = endNodeModel;
            this.contactList = contactList;
            url = App.getAppComponent().getApiServiceUrl();
            email = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            deviceId = Utils.getDeviceId(context);
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("email", email);
                jsonObject.put("userId", token);
                jsonObject.put("bridgeId", nodeModel.getEndNodeUid());
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("isApp", "true");
                jsonObject.put("motionAlert", "true");
                jsonObject.put("sms", "false");
                jsonObject.put("call", "false");
                jsonObject.put("email", "false");
                jsonObject.put("alertAtHome", "true");
                jsonObject.put("alertAtAway", "false");
                jsonObject.put("alertAtNight", "true");
                jsonObject.put("contacts", contactList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url + "tito-node_setting")
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
                Log.e("ERROR: ", "Exception at NodeSettingActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at NodeSettingActivity: " + e1.getMessage());
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Utils.showProgress(NodeSettingActivity.this, view, progressBar, false);
            if (aBoolean) {

            } else {

            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(NodeSettingActivity.this, view, progressBar, false);

        }
    }

    /*get setting parameters and display */
}
