package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.NormalFlow.adapter.AdapterNonSpatialPlan;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.LocationManagerUtils;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.NonSpatialModel;
import com.example.sharan.iotsmartchain.qrcodescanner.QrCodeActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CreateNonSpatialActivity extends BaseActivity {
    private static String TAG = CreateNonSpatialActivity.class.getSimpleName();
    //QR-code
    private static int REQUEST_CODE_QR_SCAN = 101;
    @BindView(R.id.toolbar_ns)
    Toolbar toolbar;
    @BindView(R.id.reg_iot_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.relativeLayout_reg_iot)
    View mView;
    @BindView(R.id.listView_init_nonspatial)
    ListView mListView;
    @BindView(R.id.button_ns_done)
    Button mDoneBtn;
    @BindView(R.id.fab_manually)
    FloatingActionButton mFabManually;
    @BindView(R.id.fab_camera)
    FloatingActionButton mFabQrScanner;
    //dialog
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private boolean isLocationCheck = false;
    private boolean isCameraCheck = false;
    //location
    private LocationManager locationManager;
    private String mProvider;
    private double latitude;
    private double longitude;
    private String locationAddress;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LocationManagerUtils locationManagerUtils = null;
    private AdapterNonSpatialPlan adapterNonSpatialPlan = null;
    private ArrayList<NonSpatialModel> arrayList = new ArrayList<>();
    //server comm
    private String mUrl, token, mEmail;
    private String mDeviceId;
    private CreateNonSpatialAsync createNonSpatialAsync = null;
    private GetListOfNonSpatialAsync getListOfNonSpatialAsync = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_nonspatial_layout);
        injectViews();
        setupToolbar();

        //get  values url, token and email
        mUrl = App.getAppComponent().getApiServiceUrl();
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);

        //init adapter
        adapterNonSpatialPlan = new AdapterNonSpatialPlan(CreateNonSpatialActivity.this, arrayList);
        mListView.setAdapter(adapterNonSpatialPlan);

        //check location permission
        locationManagerUtils = new LocationManagerUtils(CreateNonSpatialActivity.this);
        locationManagerUtils.initLocationManager();
        boolean isCheck = locationManagerUtils.checkPermissionLM();

        //get a list installed device non-spatial plans

        //list view on item click and check local test
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NonSpatialModel nonSpatialModel  = (NonSpatialModel) parent.getAdapter().getItem(position);
                Intent intent = new Intent(CreateNonSpatialActivity.this, NonSpatialDetailActivity.class);
                intent.putExtra("NonSpatialModel", (Serializable) nonSpatialModel);
                startActivity(intent);
            }
        });

        //manually capture and send to server
        mFabManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(mView, "Manually", Snackbar.LENGTH_LONG).show();
                locationManagerUtils.getLocationLatLng();
                if (dialog != null) {
                    if (dialog.isShowing()) dialog.dismiss();
                }
                //Manually enter the all input data
                DialogManuallyDataInit("");
            }
        });

        //Scan QR code and send to server
        mFabQrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check both location and camera permission
                Snackbar.make(mView, "Qr code scanner", Snackbar.LENGTH_LONG).show();
                Intent qrIntent = new Intent(CreateNonSpatialActivity.this, QrCodeActivity.class);
                startActivityForResult(qrIntent, REQUEST_CODE_QR_SCAN);
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Non-Spatial plan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        menuRefresh.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_refresh:
                Snackbar.make(mView, "Refresh", Snackbar.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "requestCode : " + requestCode);
        if (requestCode == 123)
            locationManagerUtils.checkPermissionLM();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "location enabled requestCode : " + requestCode);
        if (requestCode == 222) {
            locationManagerUtils.checkPermissionLM();
        }

        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null) return;
            Snackbar.make(mView, "QR-Code successfully Scanned... ", Snackbar.LENGTH_SHORT).show();
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.got_qr_scan_relult");
            //Display a dialog for create a Non-spatial plan
            if (result != null) DialogManuallyDataInit(result);
            else {
                Snackbar.make(mView, "Try again...", Snackbar.LENGTH_LONG).show();
            }
            Log.d(TAG, "Have scan result in your app activity :" + result);

        }
    }

    /*Display dialog : To capture info and location details for installation time*/
    private void DialogManuallyDataInit(String iotSerialNum) {
        dialog = new Dialog(CreateNonSpatialActivity.this);
        builder = new AlertDialog.Builder(CreateNonSpatialActivity.this);
        LayoutInflater layoutInflater = CreateNonSpatialActivity.this.getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_init_non_spatial_item, null);
        EditText editTextIotAdd = (EditText) rootView.findViewById(R.id.edittext_iot_add);
        EditText editTextIotLabel = (EditText) rootView.findViewById(R.id.edittext_iot_label);
        EditText editTextIotDes = (EditText) rootView.findViewById(R.id.edittext_iot_des);
        RelativeLayout relativeLayoutLat = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_lat);
        RelativeLayout relativeLayoutLng = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_lng);
        RelativeLayout relativeLayoutAdd = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_add);

        TextView textViewLat = (TextView) rootView.findViewById(R.id.textview_lat);
        TextView textViewLng = (TextView) rootView.findViewById(R.id.textview_lng);
        TextView textViewAdd = (TextView) rootView.findViewById(R.id.textview_add);

        builder.setView(rootView);

        if (!TextUtils.isEmpty(iotSerialNum) && !iotSerialNum.isEmpty()) {
            editTextIotAdd.setText(iotSerialNum);
            editTextIotAdd.setEnabled(false);
        }

        NonSpatialModel nonSpatialModel = new NonSpatialModel();
        latitude = locationManagerUtils.getLatitude();
        longitude = locationManagerUtils.getLongitude();
        addresses = locationManagerUtils.getAddresses();

        if (latitude != -1) {
            nonSpatialModel.setLatitude(latitude);
            textViewLat.setText("" + latitude);
        } else {
            relativeLayoutLat.setVisibility(View.GONE);
        }

        if (longitude != -1) {
            nonSpatialModel.setLongitude(longitude);
            textViewLng.setText("" + longitude);
        } else {
            relativeLayoutLng.setVisibility(View.GONE);
        }

        if (addresses != null && addresses.size() > 0) {
            locationAddress = addresses.get(0).getAddressLine(0);
            textViewAdd.setText("" + locationAddress);
            nonSpatialModel.setAddress(locationAddress);
        } else {
            relativeLayoutAdd.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String deviceAdd = editTextIotAdd.getText().toString();
                String label = editTextIotLabel.getText().toString();
                String description = editTextIotDes.getText().toString();

                nonSpatialModel.setIotDeviceSerialNum(deviceAdd);
                nonSpatialModel.setLabel(label);
                nonSpatialModel.setDescription(description);

//                arrayList.add(nonSpatialModel);
//                adapterNonSpatialPlan.notifyDataSetChanged();

                createNonSpatialAsync = new CreateNonSpatialAsync(CreateNonSpatialActivity.this, nonSpatialModel);
                createNonSpatialAsync.execute((Void)null);

                Snackbar.make(mView, "Done \n" + editTextIotAdd.getText().toString(),
                        Snackbar.LENGTH_LONG).show();

                //TODO IOT details and information send to server
                //show result in list view
                dialog.dismiss();
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Snackbar.make(mView, "Cancel", Snackbar.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    //TODO write server API for IoT installation
    public class CreateNonSpatialAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private NonSpatialModel nonSpatialModel = new NonSpatialModel();
        private boolean retVal = false;
        private String message;
        private Long timeStamp;
        private String mService;
        private String mCharacteristic;
        private String mIotDeviceSN;
        private String mLabel;
        private String mDescription;
        private double mLatitude;
        private double mLongitude;
        private String mAddress;

        public CreateNonSpatialAsync(Context context, NonSpatialModel nonSpatialModel) {
            this.context = context;
            this.nonSpatialModel = nonSpatialModel;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);
            Log.e(TAG, "DeviceId :: " + mDeviceId);

            if (nonSpatialModel != null) {
                mIotDeviceSN = nonSpatialModel.getIotDeviceSerialNum();
                mLabel = nonSpatialModel.getLabel();
                mDescription = nonSpatialModel.getDescription();
                mLatitude = nonSpatialModel.getLatitude();
                mLongitude = nonSpatialModel.getLongitude();
                mAddress = nonSpatialModel.getAddress();
            }

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("iotDeviceSN", mIotDeviceSN);
                jsonObject.put("label", mLabel);
                jsonObject.put("description", mDescription);
                jsonObject.put("latitude", mLatitude);
                jsonObject.put("longitude", mLongitude);
                jsonObject.put("address", mAddress);
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("isSpatial", "false");
                jsonObject.put("isApp", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "iot-device-installation")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "iot-device-installation");
            Log.d(TAG, "SH : formBody  " + formBody.toString());
            Log.d(TAG, "SH : request " + request.getClass().toString());


            retVal = false;
            try {
                Response response = client.newCall(request).execute();
                Log.e(TAG, "" + response.toString());

                String authResponseStr = response.body().string();
                Log.e(TAG, "authResponseStr :: " + authResponseStr);

                //Json object
                try {
                    JSONObject TestJson = new JSONObject(authResponseStr);
                    Log.e(TAG, "TestJson :: " + TestJson.toString());
                    Log.e(TAG, "TestJson : body :: " + TestJson.getString("body").toString());

                    String strData = TestJson.getString("body").toString();
                    Log.e(TAG, "strData :: " + strData.toString());

                    JSONObject respData = new JSONObject(strData);
                    retVal = respData.getBoolean("status");
                    message = respData.getString("message");
                    timeStamp = respData.getLong("timestamp");
                    mService = respData.getString("service");
                    mCharacteristic = respData.getString("characteristic");

                    nonSpatialModel.setStatus(""+retVal);
                    nonSpatialModel.setMessage(message);
                    nonSpatialModel.setTimeStamp(""+timeStamp);
                    nonSpatialModel.setService(mService);
                    nonSpatialModel.setCharacteristic(mCharacteristic);


                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at RegistrationActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at RegistrationActivity: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            Utils.showProgress(CreateNonSpatialActivity.this, mView, mProgressBar, false);
            if(isSuccess){
                Snackbar.make(mView, message, Snackbar.LENGTH_LONG).show();
                arrayList.add(nonSpatialModel);
                adapterNonSpatialPlan.notifyDataSetChanged();
            }else{
                Snackbar.make(mView, message, Snackbar.LENGTH_LONG).show();
            }
            createNonSpatialAsync = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            createNonSpatialAsync = null;
            Utils.showProgress(CreateNonSpatialActivity.this, mView, mProgressBar, false);
        }
    }

    //TODO get a list Installed IOT server API
    public class GetListOfNonSpatialAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;

        public GetListOfNonSpatialAsync(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

//    //check location and camera permission
//    private void  checkPermission(String permissionType ) {
//        if(permissionType.equalsIgnoreCase("location")){
//            if (ContextCompat.checkSelfPermission(this,
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "Requesting for location permission");
//                //request permission
//                ActivityCompat.requestPermissions(this,
//                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                                android.Manifest.permission.ACCESS_FINE_LOCATION},
//                        123);
//            }else if(ContextCompat.checkSelfPermission(this,
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                Log.d(TAG, "Location Permission already granted");
//                isLocationCheck = true;
//
//                //manually turn on location
//                LocationManagerCheck();
//
//                //TODO Call dialog
//            }
//        } else if(permissionType.equalsIgnoreCase("camera")){
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "Requesting for Camera permission");
//                //request permission
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.CAMERA},
//                        111);
//            }else if(ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//                    Log.d(TAG, "Camera permission already granted ");
//                    isCameraCheck = true;
//                    //TODO read device qr-code and display dialog
//            }
//        }
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 123:
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted
//                    Log.d(TAG, "permission granted : location  " + permissions[0]);
//                    isLocationCheck = true;
//
//                    //check location
//                    LocationManagerCheck();
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Log.d(TAG, "permission denied : location" + permissions[0]);
//                    isLocationCheck = false;
//                }
//                break;
//            case 111:
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    Log.d(TAG, "Permission granted : camera"+permissions[0]);
//                    isCameraCheck = true;
//                }else{
//                    Log.d(TAG, "Permission denied : camera "+permissions[0]);
//                    isCameraCheck = false;
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        }
//    }
//
//    private void LocationManagerCheck(){
//        if(locationManager == null)
//        locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
//
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//        Log.e(TAG, "LOCATION MANAGER START SERVICE ");
//        try {
//            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            Log.d(TAG, "gps_enabled : "+gps_enabled);
//        } catch(Exception ex) {}
//
//        try {
//            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            Log.d(TAG, "network_enabled : "+network_enabled);
//        } catch(Exception ex) {}
//
//        if(!gps_enabled && !network_enabled) {
//
//            // notify user
//            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
//            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings),
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            // TODO Auto-generated method stub
//                            Intent myIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(myIntent);
//                            //get gps
//                            initLocationManager();
//                        }
//                    });
//            dialog.setNegativeButton(this.getString(R.string.dialog_cancel_button),
//                    new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            // TODO Auto-generated method stub
//                            paramDialogInterface.dismiss();
//                        }
//                    });
//            dialog.show();
//        }else{
//            Log.e(TAG, "Location already turn on");
//            //get gps
//            initLocationManager();
//        }
//    }
//
//    private void initLocationManager() {
//        Log.e(TAG, "init location manager");
//        if(locationManager ==null)
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        mProvider = locationManager.getBestProvider(criteria, false);
//
//        if (mProvider != null && !mProvider.equals("")) {
//            if (ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            Location location = locationManager.getLastKnownLocation(mProvider);
//            locationManager.requestLocationUpdates(mProvider, 10000, 1, this);
//
//            if (location != null)
//                onLocationChanged(location);
//            else
//                Toast.makeText(getBaseContext(), "No Location Provider Found Check Your Code",
//                        Toast.LENGTH_SHORT).show();
//
//            if (locationManager != null) {
//                location = locationManager
//                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            }
//
//            if (location != null) {
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//
//                mProvider = "Latitude : " + latitude + " Longitude : " + longitude;
//                Log.e(TAG, mProvider);
//               // textViewResult.setText(mProvider);
//
//                //init GeoCoder
//                geocoder = new Geocoder(CreateNonSpatialActivity.this, Locale.getDefault());
//
//                Log.e("latitude", "inside latitude--" + latitude);
//                try {
//                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (addresses != null && addresses.size() > 0) {
//                    String address = addresses.get(0).getAddressLine(0);
//                    String city = addresses.get(0).getLocality();
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName();
//                  //  textViewResult.setText("Latitude : "+latitude+"\nLongitude : "+longitude+"\n"+address + " " + city + " " + country);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//
//            mProvider = "Latitude : " + latitude + " Longitude : " + longitude;
//            Log.e(TAG, mProvider);
//          //  textViewResult.setText(mProvider);
//
//            //init GeoCoder
//            geocoder = new Geocoder(CreateNonSpatialActivity.this, Locale.getDefault());
//
//            Log.e("latitude", "inside latitude--" + latitude);
//            try {
//                addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (addresses != null && addresses.size() > 0) {
//                String address = addresses.get(0).getAddressLine(0);
//                String city = addresses.get(0).getLocality();
//                String state = addresses.get(0).getAdminArea();
//                String country = addresses.get(0).getCountryName();
//                String postalCode = addresses.get(0).getPostalCode();
//                String knownName = addresses.get(0).getFeatureName();
//              //  textViewResult.setText("Latitude : "+latitude+"\nLongitude : "+longitude+"\n"+address + " " + city + " " + country);
//            }
//        }
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
}
