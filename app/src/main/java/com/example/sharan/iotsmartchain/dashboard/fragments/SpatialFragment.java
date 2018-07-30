package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.MasterLockActivity;
import com.example.sharan.iotsmartchain.dashboard.adapter.ListOfLockAdapter;
import com.example.sharan.iotsmartchain.dashboard.adapter.SpatialAdapter;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;
import com.example.sharan.iotsmartchain.model.DeviceLockerModel;
import com.example.sharan.iotsmartchain.model.SpatialDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpatialFragment extends BaseFragment{

    private static String TAG = SpatialFragment.class.getSimpleName();
    private ListView mListView;
    private SpatialAdapter mSpatialAdapter = null;
    private SpatialDataModel spatialDataModel = new SpatialDataModel();
    private Map<String, SpatialDataModel> mMap = new LinkedHashMap<>();
    private ArrayList<SpatialDataModel> mArrayList = new ArrayList<>();

    private String mUrl, token, loginId;

    private String mModuleListStr = "{\"tokenid\":\"84h39873423h823\",\"emailid\":\"personName@gmail.com\"," +
            "\"status\":\"true\",\"message\":\"Successfully\",\"isSpatial\":\"true\"," +
            "\"ModuleDetails\":[" +
            "{\"deviceId\":\"371je1ooj293u102938\",\"deviceType\":\"office main door\",\"status\":\"true\",\"longitude\":\"78.429385\",\"latitude\":\"17.240263\",\"timeStamp\":\"1531368931338\",\"details\":\"This Sensor initialized office main entrance door.\"}," +
            "{\"deviceId\":\"371je1ooj2lkjlkjlkj02938\",\"deviceType\":\"WorkStation locker\",\"status\":\"true\",\"longitude\":\"\",\"latitude\":\"\",\"timeStamp\":\"1531285245000\",\"details\":\"This Sensor initialized office work station.\"}," +
            "{\"deviceId\":\"371jesdfjhgaf43452938\",\"deviceType\":\"Home main door\",\"status\":\"false\",\"longitude\":\"78.437735\",\"latitude\":\"17.251160\",\"timeStamp\":\"1531119645000\",\"details\":\"This Sensor initialized main home door.\"}, " +
            "{\"deviceId\":\"29847239sdjkfhskdjfh9287\",\"deviceType\":\"office locker room\",\"status\":\"true\",\"longitude\":\"\",\"latitude\":\"\",\"timeStamp\":\"1531338345000\",\"details\":\"This Sensor initialized office locker room.\"}," +
            "{\"deviceId\":\"kjh9872482374982ajhakjsdh\",\"deviceType\":\"Home locker room\",\"status\":\"true\",\"longitude\":\"\",\"latitude\":\"\",\"timeStamp\":\"1531011959000\",\"details\":\"This Sensor initialized home locker room.\"}]}\n";

    public static SpatialFragment newInstance(){
        return new SpatialFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup action bar title and sub title
        getActivity().setTitle("iSmartLink");
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setSubtitle("Spatial");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spatial, container, false);
        mListView = (ListView)rootView.findViewById(R.id.listview_spatial);

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        Log.d(TAG, "" + mModuleListStr.toString());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //init adapter and set listView.
        mSpatialAdapter = new SpatialAdapter(getActivity(), mArrayList);
        mListView.setAdapter(mSpatialAdapter);

        getSpatialDataList();

        return rootView;
    }

    private void getSpatialDataList(){
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(mModuleListStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String emailId = null;
        String tokenid = null;
        String status = null;
        String message = null;
        String isSpatial = null;
        try {
            emailId = (String) jObject.get("emailid");
            tokenid = (String) jObject.get("tokenid");
            status = (String) jObject.get("status");
            message = (String)jObject.get("message");
            isSpatial = (String)jObject.get("isSpatial");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(emailId);
        System.out.println(tokenid);
        System.out.println(status);
        System.out.println(message);
        System.out.println(isSpatial);

        JSONArray jsonArray = null;
        try {
            jsonArray = jObject.getJSONArray("ModuleDetails");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mMap = new LinkedHashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                spatialDataModel = new SpatialDataModel();
                spatialDataModel.set_Id(object.getString("deviceId"));
                spatialDataModel.setType(object.getString("deviceType"));
                spatialDataModel.setStatus(object.getString("status"));
                spatialDataModel.setLongitude(object.getString("longitude"));
                spatialDataModel.setLatitude(object.getString("latitude"));
                spatialDataModel.setTimeStamp(object.getString("timeStamp"));
                spatialDataModel.setDetails(object.getString("details"));

                mMap.put(object.getString("deviceId"), spatialDataModel);

                } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "Modules list mMap : "+mMap.values());


        mArrayList = new ArrayList<SpatialDataModel>(mMap.values());
        Log.d(TAG, "arrayListOfModules : "+mArrayList.toString());

        mSpatialAdapter = new SpatialAdapter(getActivity(), mArrayList);
        mListView.setAdapter(mSpatialAdapter);
    }
}
