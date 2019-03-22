package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.app.Activity;
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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.FloatingActionMenu.OptionsFabLayout;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.LocationManagerUtils;
import com.example.sharan.iotsmartchain.global.NetworkUtil;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.BridgeModel;
import com.example.sharan.iotsmartchain.qrcodescanner.QrCodeActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

public class AddBridgeActivity extends BaseActivity {
    public static final String TAG = AddBridgeActivity.class.getSimpleName();
    public static final int REQUEST_CODE_QR_SCAN = 1111;
    public static final int DIALOG_ADD_IOT = 121;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.listview_gateway)
    ListView listView;
    @BindView(R.id.reg_iot_progress)
    ProgressBar progressBar;
    @BindView(R.id.relativeLayout_reg_iot)
    View view;
    @BindView(R.id.fab_l)
    OptionsFabLayout fabWithOptions;
    @BindView(R.id.bottom_sheet)
    View bottomSheet;

    private String mUrl, loginId, token;
    private EditText mEditTextUID;
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private EditText mEditTextLabel;
    //location
    private LocationManager locationManager;
    private String mProvider;
    private double latitude;
    private double longitude;
    private String locationAddress;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LocationManagerUtils locationManagerUtils = null;
    private ArrayList<String> arrayOfLabels = new ArrayList<>();

    private BridgeAdapter bridgeAdapter = null;
    private ArrayList<BridgeModel> mBridgeList = new ArrayList<>();
    private DeleteBridgeAsync deleteBridgeAsync = null;
    private BridgeInitAsync bridgeInitAsync = null;
    private UpdateBridgeAsync updateBridgeAsync = null;
    private GetListOfBridgeAsync getListOfBridgeAsync = null;
    private BottomSheetBehavior mBottomSheetBehavior;
    private BridgeInterface bridgeInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gateway);
        injectViews();
        setupToolbar();

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

        //init bottom sheet
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //By default set BottomSheet Behavior as Collapsed and Height 0
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setPeekHeight(0);

        //If you want to handle callback of Sheet Behavior you can use below code
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d(TAG, "State Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(TAG, "State Dragging");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d(TAG, "State Expanded");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d(TAG, "State Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d(TAG, "State Settling");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        //check location permission
        locationManagerUtils = new LocationManagerUtils(AddBridgeActivity.this);
        locationManagerUtils.initLocationManager();
        boolean isCheck = locationManagerUtils.checkPermissionLM();

        //init Adapter
        bridgeAdapter = new BridgeAdapter(this, mBridgeList);
        listView.setAdapter(bridgeAdapter);

        //get list of Bridge devices
        //Internet check
        int isNetwork = NetworkUtil.getConnectivityStatus(AddBridgeActivity.this);
        if (isNetwork == 0) {
            Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout,
                    getString(R.string.no_internet), ALERTCONSTANT.ERROR);
        } else {
            Utils.showProgress(AddBridgeActivity.this, view, progressBar, true);
            getListOfBridgeAsync = new GetListOfBridgeAsync(AddBridgeActivity.this);
            getListOfBridgeAsync.execute((Void) null);
        }

        fabWithOptions = (OptionsFabLayout) findViewById(R.id.fab_l);
        initFabActionMenu();
        fabWithOptions.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabWithOptions.isOptionsMenuOpened()) {
                    fabWithOptions.closeOptionsMenu();
                } else {
                    fabWithOptions.isOptionsMenuOpened();
                }
            }
        });

        //list view on click item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //check if already device configured or not
                BridgeModel bridgeModel = (BridgeModel) parent.getAdapter().getItem(position);
                if (bridgeModel.isConfigure()) {
                    //  Toast.makeText(AddBridgeActivity.this, "Bridge is Configured", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddBridgeActivity.this, EndNodeActivity.class);
                    intent.putExtra("GATEWAY", bridgeModel);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    ShowConfigureDialog(bridgeModel);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                BridgeModel bridgeModel = (BridgeModel) parent.getAdapter().getItem(position);
                //Show the Bottom Sheet Fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("GATEWAY", bridgeModel);
                BottomSheetDialogFragment bottomSheetDialogFragment = new BridgeBottomSheetFragment(new BridgeInterface() {
                    @Override
                    public void onClickBridgeOption(int option) {
                        // Toast.makeText(AddBridgeActivity.this, "" + option, Toast.LENGTH_LONG).show();

                        switch (option) {
                            case 0:
                                DialogUpdateBridge(bridgeModel);
                                break;
                            case 1:
                                int isNetwork = NetworkUtil.getConnectivityStatus(AddBridgeActivity.this);
                                if (isNetwork == 0) {
                                    Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout,
                                            getString(R.string.no_internet), ALERTCONSTANT.ERROR);
                                } else {
                                    Utils.showProgress(AddBridgeActivity.this, view,
                                            progressBar, true);
                                    deleteBridgeAsync = new DeleteBridgeAsync(AddBridgeActivity.this, bridgeModel);
                                    deleteBridgeAsync.execute((Void) null);
                                }
                                break;
                            case 2:
                                break;
                        }

                    }
                });
                bottomSheetDialogFragment.setArguments(bundle);
                //  bottomSheetDialogFragment.set
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
                return true;
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("TiTo");
        getSupportActionBar().setSubtitle("Bridges");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initFabActionMenu() {
        //Set mini fab's colors.
        fabWithOptions.setMiniFabsColors(R.color.colorPrimary, R.color.green_fab, R.color.color_red); // no of FAB child's

        //Set main fab clicklistener.
        fabWithOptions.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(view.getContext(), "Main fab clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        //Set mini fabs clicklisteners.
        fabWithOptions.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.fab_add:
                        DialogBridge(""); //manually enter the gateway UID
                        fabWithOptions.closeOptionsMenu();
                        break;
                    case R.id.fab_qr:
                        QrCodeScanner(); //Qr code scanner
                        fabWithOptions.closeOptionsMenu();
                        break;
                    case R.id.fab_cancel:
                        fabWithOptions.closeOptionsMenu();
                    default:
                        break;
                }
            }
        });
    }

    /*qr code scanner method*/
    private void QrCodeScanner() {
        try {
            Intent intentQrCode = new Intent(AddBridgeActivity.this, QrCodeActivity.class);
            startActivityForResult(intentQrCode, REQUEST_CODE_QR_SCAN);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        MenuItem menuDashBoard = menu.findItem(R.id.action_db);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_db:
                Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout, "DashBoard",
                        ALERTCONSTANT.INFO);

                Intent intent = new Intent(AddBridgeActivity.this, NewDashBoardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(intent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Display dialog : To capture info and location details for installation time*/
    private void DialogBridge(String iotSerialNum) {
        dialog = new Dialog(AddBridgeActivity.this);
        builder = new AlertDialog.Builder(AddBridgeActivity.this);
        LayoutInflater layoutInflater = AddBridgeActivity.this.getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_add_gateway, null);
        Spinner spinnerLabels = (Spinner) rootView.findViewById(R.id.spinner_label);
        mEditTextUID = (EditText) rootView.findViewById(R.id.edittext_gateway);
        mEditTextLabel = (EditText) rootView.findViewById(R.id.edittext_label);
        RelativeLayout relativeLayoutLat = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_lat);
        RelativeLayout relativeLayoutLng = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_lng);
        RelativeLayout relativeLayoutAdd = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_add);
        TextView textViewLat = (TextView) rootView.findViewById(R.id.textview_lat);
        TextView textViewLng = (TextView) rootView.findViewById(R.id.textview_lng);
        TextView textViewAdd = (TextView) rootView.findViewById(R.id.textview_add);

        arrayOfLabels.add("Home");
        arrayOfLabels.add("Office");
        arrayOfLabels.add("Locker");
        arrayOfLabels.add("Room");
        arrayOfLabels.add("Work Station");
        arrayOfLabels.add("Remote Location");

        builder.setView(rootView);

        Log.e(TAG, "SH : dialog : reg list : " + arrayOfLabels);
        ArrayAdapter arrayAdapter = new ArrayAdapter(AddBridgeActivity.this,
                android.R.layout.simple_spinner_item, arrayOfLabels);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLabels.setAdapter(arrayAdapter);

        if (!TextUtils.isEmpty(iotSerialNum) && !iotSerialNum.isEmpty()) {
            mEditTextUID.setText(iotSerialNum);
            mEditTextUID.setEnabled(false);
        } else {
            mEditTextUID.setText(iotSerialNum);
            mEditTextUID.setEnabled(true);
        }

        BridgeModel bridgeModel = new BridgeModel();
        latitude = locationManagerUtils.getLatitude();
        longitude = locationManagerUtils.getLongitude();
        addresses = locationManagerUtils.getAddresses();

        spinnerLabels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String label = parent.getItemAtPosition(position).toString();
                Log.e(TAG, "Dialog item selection : label " + label);
                mEditTextLabel.setText(label);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String label = (String) parent.getSelectedItem();
                mEditTextLabel.setText(label);
            }

        });

        if (latitude != -1) {
            bridgeModel.setLatitude(latitude);
            textViewLat.setText("" + latitude);
        } else {
            relativeLayoutLat.setVisibility(View.GONE);
        }

        if (longitude != -1) {
            bridgeModel.setLongitude(longitude);
            textViewLng.setText("" + longitude);
        } else {
            relativeLayoutLng.setVisibility(View.GONE);
        }

        if (addresses != null && addresses.size() > 0) {
            locationAddress = addresses.get(0).getAddressLine(0);
            textViewAdd.setText("" + locationAddress);
            bridgeModel.setAddress(locationAddress);
        } else {
            relativeLayoutAdd.setVisibility(View.GONE);
        }

        //Time Stamp
        Timestamp ts = new Timestamp(new Date().getTime());
        bridgeModel.setTimeStamp(ts.getTime());
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String deviceUID = spinnerLabels.getSelectedItem().toString();
                    String label = mEditTextLabel.getText().toString();
                    deviceUID = mEditTextUID.getText().toString();

                    Log.e(TAG, "deviceUID gateway UID : " + deviceUID);
                    if (deviceUID != null)
                        bridgeModel.setGatewayUid(deviceUID);
                    else Log.e(TAG, " DEVICE ADD is NULL");

                    if (label != null)
                        bridgeModel.setGatewayLabel(label);
                    else Log.e(TAG, " LABEL is NULL");

                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                //Internet check
                int isNetwork = NetworkUtil.getConnectivityStatus(AddBridgeActivity.this);
                if (isNetwork == 0) {
                    Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout,
                            getString(R.string.no_internet), ALERTCONSTANT.ERROR);
                } else {
                    //Call API for Init gateway
                    if (bridgeModel.getGatewayUid().contains("GGS")) {
                        Utils.showProgress(AddBridgeActivity.this, view, progressBar, true);
                        bridgeInitAsync = new BridgeInitAsync(AddBridgeActivity.this, bridgeModel);
                        bridgeInitAsync.execute((Void) null);
                    } else {
                        Utils.SnackBarView(AddBridgeActivity.this,
                                coordinatorLayout, bridgeModel.getGatewayUid()
                                        + " is not Bridge device", ALERTCONSTANT.WARNING);
                    }
                }

                //show result in list view
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout,
                        "Cancel", ALERTCONSTANT.WARNING);
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void DialogUpdateBridge(BridgeModel bridgeModel) {
        dialog = new Dialog(AddBridgeActivity.this);
        builder = new AlertDialog.Builder(AddBridgeActivity.this);
        LayoutInflater layoutInflater = AddBridgeActivity.this.getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_node_update, null);
        TextView textViewTitle = (TextView) rootView.findViewById(R.id.textView_title);
        mEditTextUID = (EditText) rootView.findViewById(R.id.edittext_end_node);
        mEditTextLabel = (EditText) rootView.findViewById(R.id.edittext_label);
        EditText mEditTextDesp = (EditText) rootView.findViewById(R.id.edittext_iot_des);
        textViewTitle.setText("Bridge Update");
        mEditTextUID.setEnabled(false);
        mEditTextDesp.setVisibility(View.GONE);
        mEditTextLabel.requestFocus();

        builder.setView(rootView);

        if (bridgeModel != null) {
            mEditTextUID.setText(bridgeModel.getGatewayUid());
            mEditTextLabel.setText(bridgeModel.getGatewayLabel());
        }

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.showProgress(AddBridgeActivity.this, view, progressBar, true);
                String bridgeUID = mEditTextUID.getText().toString();
                String label = mEditTextLabel.getText().toString();

                if (!TextUtils.isEmpty(bridgeUID) && bridgeUID != null)
                    bridgeModel.setGatewayUid(bridgeUID);
                if (!TextUtils.isEmpty(label) && label != null)
                    bridgeModel.setGatewayLabel(label);

                //Internet check
                int isNetwork = NetworkUtil.getConnectivityStatus(AddBridgeActivity.this);
                if (isNetwork == 0) {
                    Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout,
                            getString(R.string.no_internet), ALERTCONSTANT.ERROR);
                } else {
                    Utils.showProgress(AddBridgeActivity.this, view, progressBar, true);
                    updateBridgeAsync = new UpdateBridgeAsync(AddBridgeActivity.this, bridgeModel);
                    updateBridgeAsync.execute((Void) null);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
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
        if (requestCode == 222) {
            locationManagerUtils.checkPermissionLM();
        }
        if (requestCode == REQUEST_CODE_QR_SCAN && resultCode == RESULT_OK && data != null) {
            Log.e(TAG, " HELLO " + data.toString());
        }
        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(AddBridgeActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;
        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null) return;
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.got_qr_scan_relult");
            Log.d(TAG, "Have scan result in your app activity :" + result);
            Utils.SnackBarView(AddBridgeActivity.this,
                    coordinatorLayout, "Successfully scan device ID...", ALERTCONSTANT.SUCCESS);

            int isNetwork = NetworkUtil.getConnectivityStatus(AddBridgeActivity.this);
            if (isNetwork == 0) {
                Utils.SnackBarView(AddBridgeActivity.this,
                        coordinatorLayout, getString(R.string.no_internet), ALERTCONSTANT.WARNING);
            } else {
                if (result != null)
                    if (result.contains("GGS")) {
                        DialogBridge(result);
                    } else {
                        Utils.SnackBarView(AddBridgeActivity.this,
                                coordinatorLayout, result + " is not Bridge device", ALERTCONSTANT.WARNING);
                    }
            }
        }
    }

    /*Sort based on time stamp */
    private ArrayList<BridgeModel> getSortedList(ArrayList<BridgeModel> mBridgeList) {
        Collections.sort(mBridgeList, new Comparator<BridgeModel>() {
            @Override
            public int compare(BridgeModel o1, BridgeModel o2) {
                return Long.valueOf(o2.getTimeStamp()).compareTo(Long.valueOf(o1.getTimeStamp()));
            }
        });
        return mBridgeList;
    }

    /*show dialog gateway is configured or not */
    private void ShowConfigureDialog(BridgeModel bridgeModel) {
        dialog = new Dialog(AddBridgeActivity.this);
        builder = new AlertDialog.Builder(AddBridgeActivity.this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_configure_layout, null);

        TextView textTitle = (TextView) rootView.findViewById(R.id.textview_title);
        TextView textSubTitle = (TextView) rootView.findViewById(R.id.textview_sub_title);
        Button buttonWifi = (Button) rootView.findViewById(R.id.button_wifi);
        Button buttonGsm = (Button) rootView.findViewById(R.id.button_GSM);

        if (bridgeModel != null) {
            textTitle.setText("Bridge " + bridgeModel.getGatewayUid() + " Configure");
            textSubTitle.setText("Set Bridge Configuration");
        }

        buttonWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(AddBridgeActivity.this, "WIFI", Toast.LENGTH_SHORT).show();
                Intent intentWifi = new Intent(AddBridgeActivity.this,
                        BridgeWifiConfigureActivity.class);
                intentWifi.putExtra("BRIDGE", bridgeModel);
                intentWifi.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentWifi);
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setView(rootView);
        dialog = builder.create();
        dialog.show();
    }

    /*Add new Bridge API*/
    public class BridgeInitAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private BridgeModel bridgeModel;
        private String mEmail;
        private String mUrl;
        private String mDeviceId;
        private boolean retVal = false;
        private String message;
        private long timeStamp = -1;

        public BridgeInitAsync(Context context, BridgeModel bridgeModel) {
            this.context = context;
            this.bridgeModel = bridgeModel;
            mUrl = App.getAppComponent().getApiServiceUrl();
            mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);
            Log.e(TAG, "DeviceId :: " + mDeviceId);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("bridgeId", bridgeModel.getGatewayUid());
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("isApp", "true");
                jsonObject.put("type", 1);
                jsonObject.put("label", bridgeModel.getGatewayLabel());
                jsonObject.put("latitude", bridgeModel.getLatitude());
                jsonObject.put("longitude", bridgeModel.getLongitude());
                jsonObject.put("address", bridgeModel.getAddress());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "tito-bridge")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "tito-bridge");
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
                    Log.e(TAG, TestJson.toString());
                    String strData = TestJson.getString("body").toString();
                    Log.e(TAG, strData.toString());
                    JSONObject respData = new JSONObject(strData);
                    Log.e(TAG, "respData :: " + respData.toString());
                    retVal = respData.getBoolean("status");
                    message = respData.getString("message");
                    timeStamp = respData.getLong("timestamp");
                    if (retVal) {
                        bridgeModel = new BridgeModel();
                        bridgeModel.setTimeStamp(timeStamp);
                        bridgeModel.set_Id(respData.getString("_Id"));
                        bridgeModel.setGatewayUid(respData.getString("bridgeId"));
                        bridgeModel.setGatewayLabel(respData.getString("label"));
                        bridgeModel.setLatitude(respData.getDouble("latitude"));
                        bridgeModel.setLongitude(respData.getDouble("longitude"));
                        bridgeModel.setAddress(respData.getString("address"));
                        bridgeModel.setStatus(respData.getString("status"));
                        bridgeModel.setService(respData.getString("service"));
                        bridgeModel.setCharacteristic(respData.getString("characteristic"));
                        bridgeModel.setConfigure(respData.getBoolean("isConfigured"));
                    }

                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));
                    Log.e(TAG, " SH : bridgeModel : " + bridgeModel.toString());

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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Utils.showProgress(AddBridgeActivity.this, view, progressBar, false);
            if (retVal) {
                if (bridgeModel != null) {
                    //add to listview
                    //TODO arrayListOfGateway.add(bridgeModel);
                    mBridgeList.add(0, bridgeModel);
                    bridgeAdapter.notifyDataSetChanged();
                    Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout,
                            "Done \n" + bridgeModel.getGatewayUid(), ALERTCONSTANT.SUCCESS);

                }
            } else {
                Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout,
                        bridgeModel.getGatewayUid() + "\n" + message, ALERTCONSTANT.WARNING);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(AddBridgeActivity.this, view, progressBar, false);
        }
    }

    /*Get a list of installed Bridge devices.*/
    public class GetListOfBridgeAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String token, mEmail, mUrl;
        private BridgeModel bridgeModel;
        private boolean retVal = false;
        private String mDeviceId;
        private boolean status;
        private String message;
        private Long timeStamp;
        private HashMap<String, BridgeModel> deviceMap = new LinkedHashMap<>();

        public GetListOfBridgeAsync(Context context) {
            this.context = context;
            mUrl = App.getAppComponent().getApiServiceUrl();
            mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);
            Log.e(TAG, "DeviceId :: " + mDeviceId);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("isApp", "true");
                jsonObject.put("type", 2);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "tito-bridge")
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
                    String strData = TestJson.getString("body").toString();
                    Log.e(TAG, "strData :: " + strData.toString());
                    JSONObject respData = new JSONObject(strData);
                    JSONArray jsonArray = respData.getJSONArray("list");
                    Log.e(TAG, "respData :: " + respData.toString());
                    retVal = respData.getBoolean("status");
                    message = respData.getString("message");

                    if (retVal) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            bridgeModel = new BridgeModel();
                            bridgeModel.setTimeStamp(object.getLong("timestamp"));
                            bridgeModel.set_Id(object.getString("_id"));
                            bridgeModel.setGatewayUid(object.getString("bridge_id"));
                            bridgeModel.setGatewayLabel(object.getString("label"));
                            bridgeModel.setLatitude(object.getDouble("latitude"));
                            bridgeModel.setLongitude(object.getDouble("longitude"));
                            bridgeModel.setAddress(object.getString("address"));
                            bridgeModel.setConfigure(object.getBoolean("is_configured"));
                            bridgeModel.setService(object.getString("service"));
                            bridgeModel.setCharacteristic(object.getString("characteristic"));
                            deviceMap.put(bridgeModel.get_Id(), bridgeModel);
                        }
                    }

                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));
                    Log.e(TAG, " SH : bridgeModel : " + bridgeModel.toString());

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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Utils.showProgress(AddBridgeActivity.this, view, progressBar, false);
            if (aBoolean) {
                mBridgeList = new ArrayList<>(deviceMap.values());
                mBridgeList = getSortedList(mBridgeList); //sorted bridge list
                bridgeAdapter = new BridgeAdapter(context, mBridgeList);
                listView.setAdapter(bridgeAdapter);
                Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout,
                        message, ALERTCONSTANT.SUCCESS);
            } else {
                Utils.SnackBarView(AddBridgeActivity.this, coordinatorLayout,
                        message, ALERTCONSTANT.WARNING);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(AddBridgeActivity.this, view, progressBar, false);
        }
    }

    /*Delete Bridge API*/
    public class DeleteBridgeAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private BridgeModel bridgeModel;
        private String mEmail, mUrl, token;
        private boolean retVal = false;
        private String mDeviceId;
        private boolean status;
        private String message;
        private Long timeStamp;

        public DeleteBridgeAsync(Context context, BridgeModel bridgeModel) {
            this.context = context;
            this.bridgeModel = bridgeModel;
            mUrl = App.getAppComponent().getApiServiceUrl();
            mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);
            Log.e(TAG, "DeviceId :: " + mDeviceId);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("bridgeId", bridgeModel.getGatewayUid());
                jsonObject.put("_id", bridgeModel.get_Id());
                jsonObject.put("isApp", "true");
                jsonObject.put("type", 4);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(mUrl + "tito-bridge")
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
                    String strData = TestJson.getString("body").toString();
                    JSONObject respData = new JSONObject(strData);
                    retVal = respData.getBoolean("status");
                    message = respData.getString("message");
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
        protected void onPostExecute(Boolean isStatus) {
            super.onPostExecute(isStatus);
            Utils.showProgress(AddBridgeActivity.this, view, progressBar, false);
            if (isStatus) {
                mBridgeList.remove(bridgeModel);
                bridgeAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(AddBridgeActivity.this, view, progressBar, false);
        }
    }

    /*Update Bridge*/
    public class UpdateBridgeAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private BridgeModel bridgeModel, updateBridgeModel;
        private String mEmail, mUrl, token;
        private boolean retVal = false;
        private String mDeviceId;
        private boolean status;
        private String message;
        private Long timeStamp;

        public UpdateBridgeAsync(Context context, BridgeModel bridgeModel) {
            this.context = context;
            this.bridgeModel = bridgeModel;
            mUrl = App.getAppComponent().getApiServiceUrl();
            mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);
            Log.e(TAG, "DeviceId :: " + mDeviceId);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("bridgeId", bridgeModel.getGatewayUid());
                jsonObject.put("_id", bridgeModel.get_Id());
                jsonObject.put("label", bridgeModel.getGatewayLabel());
                jsonObject.put("isApp", "true");
                jsonObject.put("type", 3);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(mUrl + "tito-bridge")
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
                    String strData = TestJson.getString("body").toString();
                    JSONObject respData = new JSONObject(strData);
                    retVal = respData.getBoolean("status");
                    message = respData.getString("message");
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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Utils.showProgress(AddBridgeActivity.this, view, progressBar, false);
            if (aBoolean) {
                mBridgeList.remove(bridgeModel);
                bridgeAdapter.notifyDataSetChanged();
                mBridgeList.add(0, bridgeModel);
                bridgeAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(AddBridgeActivity.this, view, progressBar, false);
        }
    }

}
