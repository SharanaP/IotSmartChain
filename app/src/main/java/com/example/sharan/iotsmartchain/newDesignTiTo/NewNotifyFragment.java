package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.AllNotificationDetailsActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.IotDeviceNotificationActivity;
import com.example.sharan.iotsmartchain.dashboard.adapter.NotificationGridAdapter;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.NetworkUtil;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;
import com.example.sharan.iotsmartchain.model.NotificationCountModel;
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
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewNotifyFragment extends BaseFragment {
    public static final String TAG = NewNotifyFragment.class.getSimpleName();
    private String mUrl, loginId, token, registrationId;
    private CoordinatorLayout mCoordinatorLayout;
    private CircleImageView circleImageView;
    private TextView tvTitle;
    private TextView tvUnreadCunt;
    private GridView gridView;
    private RelativeLayout rlAllNotification;
    private ProgressBar progressBar;
    private View view;
    private List<NotificationCountModel> mList = new ArrayList<>();
    private NotificationGridAdapter mAdapter = null;
    private GetNotificationCountAsync getNotificationCountAsync = null;

    public static NewNotifyFragment newInstance() {
        return new NewNotifyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        registrationId = FirebaseInstanceId.getInstance().getToken();

        //Set title and sub title
        getActivity().setTitle((getResources().getString(R.string.app_name)));
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle("Notifications");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_notification, container,
                false);
        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar);
        view = (View) rootView.findViewById(R.id.progressView);
        circleImageView = (CircleImageView) rootView.findViewById(R.id.imageView_icon);
        tvTitle = (TextView) rootView.findViewById(R.id.textView_all_notifications);
        tvUnreadCunt = (TextView) rootView.findViewById(R.id.textView_unreadBadge);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        rlAllNotification = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_all);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text
                NotificationCountModel selectedItem = (NotificationCountModel) parent.getAdapter().getItem(position);
                Log.e(TAG, "gridView === " + selectedItem.getIotSensorSn());
                String iotDeviceSn = selectedItem.getIotSensorSn().toString();

                if (TextUtils.isEmpty(iotDeviceSn)) {
                    Log.e(TAG, "iotDeviceSn is empty");
                } else {
                    Intent intent = new Intent(getActivity(), IotDeviceNotificationActivity.class);
                    intent.putExtra("IOT_DEVICE_SN", iotDeviceSn);
                    intent.putExtra("FLOW", "IOT_NOTIFY");
                    startActivity(intent);
                }
            }
        });

        //init adapter and set list grid view
        mAdapter = new NotificationGridAdapter(getActivity(), mList);
        gridView.setAdapter(mAdapter);

        rlAllNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show a list all unread notification
                Intent allNotificationIntent = new Intent(getActivity(), AllNotificationDetailsActivity.class);
                allNotificationIntent.putExtra("FLOW", "ALL_NOTIFY");
                startActivity(allNotificationIntent);
            }
        });


        int isNetwork = NetworkUtil.getConnectivityStatus(getActivity());
        if (isNetwork == 0) {
            Utils.SnackBarView(getActivity(), mCoordinatorLayout,
                    getString(R.string.no_internet), ALERTCONSTANT.ERROR);
        } else {
            Utils.showProgress(getActivity(), view, progressBar, true);
            getNotificationCountAsync = new GetNotificationCountAsync(getActivity(), loginId, token, registrationId);
            getNotificationCountAsync.execute((Void) null);
        }

        return rootView;
    }


    /*get unread count notification */
    public class GetNotificationCountAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String mEmail;
        private String mToken;
        private String deviceId;
        private String unReadCount;
        private String authResponseStr = null;
        private String message = null;
        private long timeStamp;
        private List<NotificationCountModel> notificationCountModelList = new ArrayList<>();

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
                    unReadCount = TestJson.getString("totalNotify");
                    message = TestJson.getString("message");
                    timeStamp = TestJson.getLong("timestamp");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String strData = null;
                try {
                    strData = TestJson.getString("body").toString();
                    JSONArray jsonArray = TestJson.getJSONArray("body");
                    Log.e(TAG, "jsonArray :: " + jsonArray.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobj = jsonArray.getJSONObject(i);

                        NotificationCountModel notificationCountInfo = new GsonBuilder()
                                .create()
                                .fromJson(jsonobj.toString(), NotificationCountModel.class);
                        Log.d(TAG, "notificationCountInfo : " + notificationCountInfo.toString());
                        notificationCountModelList.add(notificationCountInfo);

                        Log.d(TAG, "\n SH : " + notificationCountInfo.toString());
                    }
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
            getNotificationCountAsync = null;
            if (getActivity() != null) {
                Utils.showProgress(getActivity(), view, progressBar, false);
                if (unReadCount == null || unReadCount.equalsIgnoreCase("0")) {
                    tvUnreadCunt.setVisibility(View.INVISIBLE);
                } else {
                    tvUnreadCunt.setText(unReadCount); //Total no of unread count messages
                    tvUnreadCunt.setVisibility(View.VISIBLE);
                }

                //Set value
                for (NotificationCountModel notificationCountModel : notificationCountModelList) {
                    mList.add(notificationCountModel);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                Log.e(TAG, "getActivity is null");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(getActivity(), view, progressBar, false);
        }
    }
}
