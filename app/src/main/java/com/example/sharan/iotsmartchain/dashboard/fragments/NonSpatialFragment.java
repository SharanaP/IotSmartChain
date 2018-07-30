package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSmoothScroller;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.adapter.NonSpatialAdapter;
import com.example.sharan.iotsmartchain.dashboard.adapter.SpatialAdapter;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;
import com.example.sharan.iotsmartchain.model.SpatialDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class NonSpatialFragment extends BaseFragment {
    private static String TAG = NonSpatialFragment.class.getSimpleName();
    private ListView mListView;
    private SpatialDataModel nonSpatialDataModel = new SpatialDataModel();
    private Map<String, SpatialDataModel> mMap = new LinkedHashMap<>();
    private ArrayList<SpatialDataModel> mArrayList = new ArrayList<>();
    private String mUrl, loginId, token;
    private NonSpatialAdapter nonSpatialAdapter = null;
    private String mNonSpatialStr = "{\"tokenid\":\"84h39873423h823\",\"emailid\":\"personName@gmail.com\",\"status\":\"true\",\"message\":\"Successfully\",\"isSpatial\":\"false\", \"ModuleDetails\":[" +
            "{\"deviceId\":\"371je1ooj293u102938\",\"deviceType\":\"office item \",\"status\":\"true\",\"longitude\":\"78.429385\",\"latitude\":\"17.240263\",\"timeStamp\":\"1531368931338\",\"details\":\"This Sensor initialized inside a item and item is moving from banglore to hyderabad\"}," +
            "{\"deviceId\":\"371je1ooj2lkjlkjlkj02938\",\"deviceType\":\"WorkStation items\",\"status\":\"true\",\"longitude\":\"77.594563\",\"latitude\":\"12.971599\",\"timeStamp\":\"1531285245000\",\"details\":\"This Sensor initialized workstation item\"}," +
            "{\"deviceId\":\"371jesdfjhgaf43452938\",\"deviceType\":\"vehicle no 168\",\"status\":\"false\",\"longitude\":\"78.437735\",\"latitude\":\"17.251160\",\"timeStamp\":\"1531119645000\",\"details\":\" inside a vehicle sensor initialized \"}, " +
            "{\"deviceId\":\"29847239sdjkfhskdjfh9287\",\"deviceType\":\"Electronic good's\",\"status\":\"true\",\"longitude\":\"\",\"latitude\":\"\",\"timeStamp\":\"1531338345000\",\"details\":\"This Sensor initialized in a vehicle\"}," +
            "{\"deviceId\":\"kjh9872482374982ajhakjsdh\",\"deviceType\":\"Important Things\",\"status\":\"true\",\"longitude\":\"78.437735\",\"latitude\":\"17.251160\",\"timeStamp\":\"1531011959000\",\"details\":\"This Sensor initialized inside a item.\"}]}\n";

    public static NonSpatialFragment newInstance(){
        return new NonSpatialFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup action bar title and sub title
        getActivity().setTitle("iSmartLink");
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setSubtitle("Non-Spatial");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_non_spatial, container, false);

        mListView = (ListView)rootView.findViewById(R.id.listview_nonspatial);

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        Log.d(TAG, "" + mNonSpatialStr.toString());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //set adapter and init
        nonSpatialAdapter = new NonSpatialAdapter(getActivity(), mArrayList);
        mListView.setAdapter(nonSpatialAdapter);

        getNonSpatialDataList();

        return rootView;
    }

    /*get a list of data*/

    private void getNonSpatialDataList(){
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(mNonSpatialStr);
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

            nonSpatialDataModel.setSpatial(isSpatial);
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
                nonSpatialDataModel = new SpatialDataModel();
                nonSpatialDataModel.set_Id(object.getString("deviceId"));
                nonSpatialDataModel.setType(object.getString("deviceType"));
                nonSpatialDataModel.setStatus(object.getString("status"));
                nonSpatialDataModel.setLongitude(object.getString("longitude"));
                nonSpatialDataModel.setLatitude(object.getString("latitude"));
                nonSpatialDataModel.setTimeStamp(object.getString("timeStamp"));
                nonSpatialDataModel.setDetails(object.getString("details"));

                mMap.put(object.getString("deviceId"), nonSpatialDataModel);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "Modules list mMap : "+mMap.values());


        mArrayList = new ArrayList<SpatialDataModel>(mMap.values());
        Log.d(TAG, "arrayListOfModules : "+mArrayList.toString());

        nonSpatialAdapter = new NonSpatialAdapter(getActivity(), mArrayList);
        mListView.setAdapter(nonSpatialAdapter);
    }
}

