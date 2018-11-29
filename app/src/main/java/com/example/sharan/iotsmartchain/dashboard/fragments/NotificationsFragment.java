package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.adapter.ListOfNotificationAdapter;
import com.example.sharan.iotsmartchain.dashboard.adapter.NotificationsListAdapter;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;
import com.example.sharan.iotsmartchain.model.NotificationModel;
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
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends BaseFragment {
    private static String TAG = NotificationsFragment.class.getSimpleName();

    private ListView mRecyclerView;
    private List<NotificationModel> mList = new ArrayList<>();
  //  private NotificationsListAdapter mAdapter;
    private ListOfNotificationAdapter mAdapter;
    private GetNotificationDetailLists getNotificationDetailLists = null;
    private String mUrl, token, loginId, registrationId;

    public static NotificationsFragment newInstance(){
        NotificationsFragment notificationsFragment = new NotificationsFragment();
        return notificationsFragment;
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
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setSubtitle("Notifications");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_db_notifications, container,
                false);

        mRecyclerView = (ListView)rootView.findViewById(R.id.notification_recycler_view);
       // mAdapter = new ListOfNotificationAdapter(getActivity(), mList);
      //  mRecyclerView.setAdapter(mAdapter);

        //TODO get all notifications from server and display
//        token = "c9013c9f-f189-4c2d-93eb-fe7d9c57fef4";
//        loginId = "sharan@gmail.com";
//        registrationId = "cdNDSWda5z0:APA91bE7U3u4eOTjqi8LdLDqRDsCLWgXXh-3Y7H3zLSUcbvO6M1Sz2E0yA_l1LLHASMC32vafGbLeP6ra4VgUJKuE281XSjGnncBaYjUsdalmn5OHRQT5oo47x1RH9XZWeHiA90N1gl0gma4xusNCSpQ2EjpDiCHFw";

//        getNotificationDetailLists = new GetNotificationDetailLists(getActivity(), loginId, token, registrationId);
//        getNotificationDetailLists.execute((Void)null);

        return rootView;
    }

    public class GetNotificationDetailLists extends AsyncTask<Void, Void, Boolean>{
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
//            for(NotificationModel notificationModel : notificationModelList){
//                mList.add(notificationModel);
//                mAdapter.notifyDataSetChanged();
//            }
            getNotificationDetailLists = null;
        }
    }
}
