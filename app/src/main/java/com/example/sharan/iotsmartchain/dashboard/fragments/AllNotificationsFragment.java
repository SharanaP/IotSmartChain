package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.NotificationListActivity;
import com.example.sharan.iotsmartchain.dashboard.adapter.NotificationGridAdapter;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;
import com.example.sharan.iotsmartchain.model.NotificationModel;
import com.example.sharan.iotsmartchain.model.NotificationModuleData;
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
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllNotificationsFragment extends BaseFragment {
    private static String TAG = AllNotificationsFragment.class.getSimpleName();
    private String mUrl, loginId, token, registrationId;

    private CircleImageView circleImageView;
    private TextView tvTitle;
    private TextView tvUnreadCunt;
    private GridView gridView;
    private RelativeLayout rlAllNotification;
    private GetNotificationDetailLists getNotificationDetailLists=null;
    private List<NotificationModuleData> mList = new ArrayList<>();
    private NotificationGridAdapter mAdapter = null;
//    private String getListOfNotification = "{\"tokenid\":\"84h39873423h823\"," +
//            "\"emailid\":\"personName@gmail.com\",\"status\":\"true\"," +
//            "\"message\":\"The list of module notification Successfully\"," +
//            "\"ListNotificationDetail\":[{\"deviceId\":\"371je1ooj293u102938\",\"deviceType\":\"office main door\",\"timeStamp\":\"1531368931338\"," +
//            "\"details\":\"This Sensor initialized office main entrance door.\", \"unReadCount\":\"\"}," +
//            "{\"deviceId\":\"371je1ooj2lkjlkjlkj02938\",\"deviceType\":\"WorkStation locker\",\"timeStamp\":\"1531285245000\"," +
//            "\"details\":\"This Sensor initialized office work station.\", \"unReadCount\": \"5\"}," +
//            "{\"deviceId\":\"371jesdfjhgaf43452938\",\"deviceType\":\"Home main door\",\"timeStamp\":\"1531119645000\"," +
//            "\"details\":\"This Sensor initialized main home door.\", \"unReadCount\":\"\"}," +
//            " {\"deviceId\":\"29847239sdjkfhskdjfh9287\",\"deviceType\":\"office locker room\",\"timeStamp\":\"1531338345000\"," +
//            "\"details\":\"This Sensor initialized office locker room.\", \"unReadCount\":\"1\"}," +
//            "{\"deviceId\":\"kjh9872482374982ajhakjsdh\",\"deviceType\":\"Home locker room\",\"timeStamp\":\"1531011959000\"," +
//            "\"details\":\"This Sensor initialized home locker room.\", \"unReadCount\":\"3\"}]}\n";


    private String getListOfNotification = "{\"tokenid\":\"84h39873423h823\",\"emailid\":\"personName@gmail.com\",\"status\":\"true\",\"message\":\"The list of module notification Successfully\",\"ListNotificationDetail\":[{\"deviceId\":\"371je1ooj293u102938\",\"deviceType\":\"office main door\",\"timeStamp\":\"1531368931338\",\"details\":\"This Sensor initialized office main entrance door.\", \"unReadCount\":\"\"},{\"deviceId\":\"371je1ooj2lkjlkjlkj02938\",\"deviceType\":\"WorkStation locker\",\"timeStamp\":\"1531285245000\",\"details\":\"This Sensor initialized office work station.\", \"unReadCount\": \"5\"},{\"deviceId\":\"371jesdfjhgaf43452938\",\"deviceType\":\"Home main door\",\"timeStamp\":\"1531119645000\",\"details\":\"This Sensor initialized main home door.\", \"unReadCount\":\"\"}, {\"deviceId\":\"29847239sdjkfhskdjfh9287\",\"deviceType\":\"office locker room\",\"timeStamp\":\"1531338345000\",\"details\":\"This Sensor initialized office locker room.\", \"unReadCount\":\"1\"},{\"deviceId\":\"kjh9872482374982ajhakjsdh\",\"deviceType\":\"Home locker room\",\"timeStamp\":\"1531011959000\",\"details\":\"This Sensor initialized home locker room.\", \"unReadCount\":\"3\"},{\"deviceId\":\"353dfsdfsfds45353ad\",\"deviceType\":\"office main door\",\"timeStamp\":\"1531368931338\",\"details\":\"This Sensor initialized office main entrance door.\", \"unReadCount\":\"\"},{\"deviceId\":\"908jshd67jhgds675jhg76\",\"deviceType\":\"WorkStation locker\",\"timeStamp\":\"1531285245000\",\"details\":\"This Sensor initialized office work station.\", \"unReadCount\": \"5\"},{\"deviceId\":\"54jh67df65fd5f65df65d6\",\"deviceType\":\"Home main door\",\"timeStamp\":\"1531119645000\",\"details\":\"This Sensor initialized main home door.\", \"unReadCount\":\"\"}, {\"deviceId\":\"hj234hg2j4hg2jh4g2jh34g2j\",\"deviceType\":\"office locker room\",\"timeStamp\":\"1531338345000\",\"details\":\"This Sensor initialized office locker room.\", \"unReadCount\":\"1\"},{\"deviceId\":\"nmbmnbmnb89897s98dfs9dfs98d\",\"deviceType\":\"Home locker room\",\"timeStamp\":\"1531011959000\",\"details\":\"This Sensor initialized home locker room.\", \"unReadCount\":\"\"},{\"deviceId\":\"dfs89d7fs98df7s9d8f7s98df7\",\"deviceType\":\"office main door\",\"timeStamp\":\"1531368931338\",\"details\":\"This Sensor initialized office main entrance door.\", \"unReadCount\":\"\"},{\"deviceId\":\"jhgjhgsdf76s7d8f6s87df6s87df6\",\"deviceType\":\"WorkStation locker\",\"timeStamp\":\"1531285245000\",\"details\":\"This Sensor initialized office work station.\", \"unReadCount\": \"5\"},{\"deviceId\":\"sdfsdf876sd8f76s8d7f6sd87f\",\"deviceType\":\"Home main door\",\"timeStamp\":\"1531119645000\",\"details\":\"This Sensor initialized main home door.\", \"unReadCount\":\"\"}, {\"deviceId\":\"sf78sdfs786fs78df6s7d8f6\",\"deviceType\":\"office locker room\",\"timeStamp\":\"1531338345000\",\"details\":\"This Sensor initialized office locker room.\", \"unReadCount\":\"1\"},{\"deviceId\":\"lkjlk987987987987sdf7s98df7\",\"deviceType\":\"Home locker room\",\"timeStamp\":\"1531011959000\",\"details\":\"This Sensor initialized home locker room.\", \"unReadCount\":\"\"}]}\n";

    public static AllNotificationsFragment newInstance(){
        return new AllNotificationsFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_dashboard_notification, container,
                false);

        circleImageView = (CircleImageView)rootView.findViewById(R.id.imageView_icon);
        tvTitle = (TextView)rootView.findViewById(R.id.textView_all_notifications);
        tvUnreadCunt = (TextView)rootView.findViewById(R.id.textView_unreadBadge);
        gridView = (GridView)rootView.findViewById(R.id.gridview);
        rlAllNotification = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_all);

        tvUnreadCunt.setText("9"); //Total no of unread count messages

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "SH : "+parent.getAdapter().getItem(position).toString());

                // Get the selected item text
                String selectedItem = parent.getItemAtPosition(position).toString();

                Log.d(TAG, " === "+selectedItem);

            }
        });

        //init adapter and set list grid view
        mAdapter = new NotificationGridAdapter(getActivity(), mList);
        gridView.setAdapter(mAdapter);

        rlAllNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show a list all unread notification
                Intent allNotificationIntent = new Intent(getActivity(), NotificationListActivity.class);
                startActivity(allNotificationIntent);
            }
        });

        getModuleNotifications();

        return rootView;
    }

    private void getModuleNotifications(){
        try {
            JSONObject jsonObject = new JSONObject(getListOfNotification);

            String respStatus = (String)jsonObject.get("status");

            Log.d(TAG, "SH :: "+respStatus);

            if(respStatus.equalsIgnoreCase("true")){

                String respMessage = (String)jsonObject.get("message");

                Log.d(TAG, "SH :: "+respMessage);

                JSONArray jsonArray = jsonObject.getJSONArray("ListNotificationDetail");

                Log.d(TAG, "SH :: "+jsonArray.toString());

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonobj = jsonArray.getJSONObject(i);

                    NotificationModuleData notificationInfo = new GsonBuilder()
                            .create()
                            .fromJson(jsonobj.toString(), NotificationModuleData.class);
                    mList.add(notificationInfo);

                    Log.d(TAG, "\n SH : "+notificationInfo.toString());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Get  a list of unread not*/
    public class GetNotificationDetailLists extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String email;
        private String tokenId;
        private String registrationId;
        private List<NotificationModuleData> notificationModelList = new ArrayList<>();

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
                    .url(mUrl + "/listOfModuleNotification")
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

                            JSONArray jsonArray = jsonObject.getJSONArray("ListNotificationDetail");

                            Log.d(TAG, "SH :: "+jsonArray.toString());

                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonobj = jsonArray.getJSONObject(i);

                                NotificationModuleData notificationInfo = new GsonBuilder()
                                        .create()
                                        .fromJson(jsonobj.toString(), NotificationModuleData.class);
                                notificationModelList.add(notificationInfo);

                                Log.d(TAG, "\n SH : "+notificationInfo.toString());
                            }

                            mAdapter = new NotificationGridAdapter(getActivity(), mList);
                            gridView.setAdapter(mAdapter);
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
            for(NotificationModuleData notificationModel : notificationModelList){
                mList.add(notificationModel);
                mAdapter.notifyDataSetChanged();
            }
            getNotificationDetailLists = null;
        }
    }
}
