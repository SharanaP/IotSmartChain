package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.app.Activity;
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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.BlueTooth.IotViaBleConnectionManager;
import com.example.sharan.iotsmartchain.FloatingActionMenu.OptionsFabLayout;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.LocationManagerUtils;
import com.example.sharan.iotsmartchain.global.NetworkUtil;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.BridgeModel;
import com.example.sharan.iotsmartchain.model.EndNodeModel;
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

public class EndNodeActivity extends BaseActivity {
    public static final String TAG = EndNodeActivity.class.getSimpleName();
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
    /*@BindView(R.id.fab_gatway)
    FloatingActionButton fabAddGateway;*/
    @BindView(R.id.bottom_sheet)
    View bottomSheet;

    private EditText mEditTextUID;
    private Dialog dialog;
    private AlertDialog.Builder builder;
    private EditText mEditTextLabel, mEditTextDesp;

    //location
    private LocationManager locationManager;
    private String mProvider;
    private double latitude;
    private double longitude;
    private String locationAddress;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LocationManagerUtils locationManagerUtils = null;
    private NodeAdapter nodeAdapter = null;
    private ArrayList<EndNodeModel> nodeList = new ArrayList<>();

    //server comm
    private String mUrl, token, mEmail;
    private String mDeviceId;
    private AddEndNodeAsync addEndNodeAsync = null;
    private GetNodeListAsync getNodeListAsync = null;
    private DeleteNodeAsync deleteNodeAsync = null;
    private UpdateNodeAsync updateNodeAsync = null;

    //get list of registered device info
    private HashMap<String, EndNodeModel> modelHashMap = new LinkedHashMap<>();
    private BridgeModel bridgeModel;
    private BottomSheetBehavior mBottomSheetBehavior;
    private NodeInterface nodeInterface;


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_node);
        injectViews();

        //initialize BLE connection
        IotViaBleConnectionManager iotViaBleConnectionManager =
                new IotViaBleConnectionManager(EndNodeActivity.this, coordinatorLayout);
        iotViaBleConnectionManager.initBle();

        //get bridge info
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bridgeModel = (BridgeModel) bundle.getSerializable("GATEWAY");
            Log.e(TAG, "GATEWAY : " + bridgeModel.toString());
        }

        setupToolbar();

        mUrl = App.getAppComponent().getApiServiceUrl();
        mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
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
        locationManagerUtils = new LocationManagerUtils(EndNodeActivity.this);
        locationManagerUtils.initLocationManager();
        boolean isCheck = locationManagerUtils.checkPermissionLM();

        //init end node adapter
        nodeAdapter = new NodeAdapter(EndNodeActivity.this, nodeList);
        listView.setAdapter(nodeAdapter);

        //init EndNodes adapter 
        getListOfInstalledDevices();

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
                //TODO local test
                EndNodeModel endNodeModel = (EndNodeModel) parent.getAdapter().getItem(position);
                IotViaBleConnectionManager iotViaBleConnectionManager =
                        new IotViaBleConnectionManager(EndNodeActivity.this,
                                endNodeModel.getService(), endNodeModel.getCharacteristic(),
                                endNodeModel.getEndNodeUid(),
                                coordinatorLayout);
                iotViaBleConnectionManager.StartBleScan();
                EndNodeActivity.this.finish();

            }
        });

        //on long click listener
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                EndNodeModel endNodeModel = (EndNodeModel) parent.getAdapter().getItem(position);
                //Show the Bottom Sheet Fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("NODE", endNodeModel);
                BottomSheetDialogFragment bottomSheetDialogFragment = new NodeBottomSheetFragment(new NodeInterface() {
                    @Override
                    public void onClickNodeOption(int option) {
                        switch (option) {
                            case 0:
                                showUpdateDialog(endNodeModel);
                                break;
                            case 1:
                                int isNetwork = NetworkUtil.getConnectivityStatus(EndNodeActivity.this);
                                if (isNetwork == 0) {
                                    Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                                            getString(R.string.no_internet), ALERTCONSTANT.ERROR);
                                } else {
                                    Utils.showProgress(EndNodeActivity.this, view, progressBar, true);
                                    deleteNodeAsync = new DeleteNodeAsync(EndNodeActivity.this, endNodeModel);
                                    deleteNodeAsync.execute((Void) null);
                                }

                                break;
                            case 2:
                                break;
                        }
                    }
                });
                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
                return true;
            }
        });

    }

    //Get a list of installed devices
    private void getListOfInstalledDevices() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int isNetwork = NetworkUtil.getConnectivityStatus(EndNodeActivity.this);
                if (isNetwork == 0) {
                    Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                            getString(R.string.no_internet), ALERTCONSTANT.ERROR);
                } else {
                    Utils.showProgress(EndNodeActivity.this, view, progressBar, true);
                    //get list of installed iot devices API
                    getNodeListAsync = new GetNodeListAsync(EndNodeActivity.this);
                    getNodeListAsync.execute((Void) null);
                }
            }
        }).run();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("TiTo");
        if (bridgeModel != null)
            getSupportActionBar().setSubtitle("Bridge : " + bridgeModel.getGatewayUid());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFabActionMenu() {
        //Set mini fab's colors.
        fabWithOptions.setMiniFabsColors(R.color.colorPrimary, R.color.green_fab, R.color.color_red); // no of FAB child's

        //Set main fab clicklistener.
        fabWithOptions.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(view.getContext(), "Main fab clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        //Set mini fabs clicklisteners.
        fabWithOptions.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.fab_add:
                        DialogGateway(""); //manually enter the gateway UID
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
            Intent intentQrCode = new Intent(EndNodeActivity.this, QrCodeActivity.class);
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
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout, "DashBoard",
                        ALERTCONSTANT.INFO);
                Intent intent = new Intent(EndNodeActivity.this, NewDashBoardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Display dialog : To capture info and location details for installation time*/
    private void DialogGateway(String iotSerialNum) {
        dialog = new Dialog(EndNodeActivity.this);
        builder = new AlertDialog.Builder(EndNodeActivity.this);
        LayoutInflater layoutInflater = EndNodeActivity.this.getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_end_node, null);

        mEditTextUID = (EditText) rootView.findViewById(R.id.edittext_end_node);
        mEditTextLabel = (EditText) rootView.findViewById(R.id.edittext_label);
        mEditTextDesp = (EditText) rootView.findViewById(R.id.edittext_iot_des);

        RelativeLayout relativeLayoutLat = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_lat);
        RelativeLayout relativeLayoutLng = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_lng);
        RelativeLayout relativeLayoutAdd = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_add);

        TextView textViewLat = (TextView) rootView.findViewById(R.id.textview_lat);
        TextView textViewLng = (TextView) rootView.findViewById(R.id.textview_lng);
        TextView textViewAdd = (TextView) rootView.findViewById(R.id.textview_add);

        builder.setView(rootView);

        if (!TextUtils.isEmpty(iotSerialNum) && !iotSerialNum.isEmpty()) {
            mEditTextUID.setText(iotSerialNum);
            mEditTextUID.setEnabled(false);
        } else {
            mEditTextUID.setText(iotSerialNum);
            mEditTextUID.setEnabled(true);
        }

        EndNodeModel endNodeModel = new EndNodeModel();
        latitude = locationManagerUtils.getLatitude();
        longitude = locationManagerUtils.getLongitude();
        addresses = locationManagerUtils.getAddresses();

        if (latitude != -1) {
            endNodeModel.setLatitude(latitude);
            textViewLat.setText("" + latitude);
        } else {
            relativeLayoutLat.setVisibility(View.GONE);
        }

        if (longitude != -1) {
            endNodeModel.setLongitude(longitude);
            textViewLng.setText("" + longitude);
        } else {
            relativeLayoutLng.setVisibility(View.GONE);
        }

        if (addresses != null && addresses.size() > 0) {
            locationAddress = addresses.get(0).getAddressLine(0);
            textViewAdd.setText("" + locationAddress);
            endNodeModel.setAddress(locationAddress);
        } else {
            relativeLayoutAdd.setVisibility(View.GONE);
        }

        //Time Stamp
        Timestamp ts = new Timestamp(new Date().getTime());
        endNodeModel.setTimeStamp(ts.getTime());

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String deviceUID = "", description = "";
                    String label = mEditTextLabel.getText().toString();
                    deviceUID = mEditTextUID.getText().toString();
                    description = mEditTextDesp.getText().toString();

                    Log.e(TAG, "deviceUID gateway UID : " + deviceUID);
                    if (deviceUID != null)
                        endNodeModel.setEndNodeUid(deviceUID);
                    else Log.e(TAG, " DEVICE ADD is NULL");

                    if (label != null)
                        endNodeModel.setLabel(label);
                    else Log.e(TAG, " LABEL is NULL");

                    if (description != null)
                        endNodeModel.setDescription(description);

                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                //Internet check
                int isNetwork = NetworkUtil.getConnectivityStatus(EndNodeActivity.this);
                if (isNetwork == 0) {
                    Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                            getString(R.string.no_internet), ALERTCONSTANT.ERROR);
                } else {
                    //Call API for Init gateway
                    if (endNodeModel.getEndNodeUid().contains("GES")) {
                        Utils.showProgress(EndNodeActivity.this, view, progressBar, true);
                        addEndNodeAsync = new AddEndNodeAsync(EndNodeActivity.this, endNodeModel);
                        addEndNodeAsync.execute((Void) null);
                    } else {
                        Utils.SnackBarView(EndNodeActivity.this,
                                coordinatorLayout, endNodeModel.getEndNodeUid()
                                        + " is not End node device", ALERTCONSTANT.WARNING);
                    }

                }

                //show result in list view
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        "Cancel", ALERTCONSTANT.WARNING);
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
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(EndNodeActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
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
            Utils.SnackBarView(EndNodeActivity.this,
                    coordinatorLayout, "Successfully scan device ID...", ALERTCONSTANT.SUCCESS);

            int isNetwork = NetworkUtil.getConnectivityStatus(EndNodeActivity.this);
            if (isNetwork == 0) {
                Utils.SnackBarView(EndNodeActivity.this,
                        coordinatorLayout, getString(R.string.no_internet), ALERTCONSTANT.WARNING);
            } else {

                if (result != null)
                    if (result.contains("GES")) {
                        DialogGateway(result);
                    } else {
                        Utils.SnackBarView(EndNodeActivity.this,
                                coordinatorLayout, result + " is not End node device", ALERTCONSTANT.WARNING);
                    }
            }
        }
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

    private boolean validateInputs(EndNodeModel endNodeModel) {
        boolean isStatus = false;
        if (endNodeModel.getEndNodeUid() != null) {
            isStatus = true;
        } else {
            isStatus = false;
            Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                    "Iot Sensor number is NULL", ALERTCONSTANT.WARNING);
        }
        if (endNodeModel.getLabel() != null) {
            isStatus = true;
        } else {
            isStatus = false;
            Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                    "Label is NULL", ALERTCONSTANT.WARNING);
        }
        if (endNodeModel.getDescription() != null) {
            isStatus = true;
        } else {
            isStatus = false;
            Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                    "Description is NULL", ALERTCONSTANT.WARNING);
        }
        if (endNodeModel.getLongitude() != -1) {
            isStatus = true;
        } else {
            isStatus = false;
            Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                    "Longitude number is NULL", ALERTCONSTANT.WARNING);
        }
        if (endNodeModel.getLatitude() != -1) {
            isStatus = true;
        } else {
            isStatus = false;
            Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                    "Latitude number is NULL", ALERTCONSTANT.WARNING);
        }
        if (endNodeModel.getAddress() != null) {
            isStatus = true;
        } else {
            isStatus = false;
            Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                    "Address number is NULL", ALERTCONSTANT.WARNING);
        }
        return isStatus;
    }

    //Show dialog for update/edit node info like label and description.
    private void showUpdateDialog(EndNodeModel endNodeModel) {
        dialog = new Dialog(EndNodeActivity.this);
        builder = new AlertDialog.Builder(EndNodeActivity.this);
        LayoutInflater layoutInflater = EndNodeActivity.this.getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_node_update, null);

        mEditTextUID = (EditText) rootView.findViewById(R.id.edittext_end_node);
        mEditTextLabel = (EditText) rootView.findViewById(R.id.edittext_label);
        mEditTextDesp = (EditText) rootView.findViewById(R.id.edittext_iot_des);
        mEditTextUID.setEnabled(false);

        if (endNodeModel != null) {
            mEditTextUID.setText(endNodeModel.getEndNodeUid());
            mEditTextLabel.setText(endNodeModel.getLabel());
            mEditTextDesp.setText(endNodeModel.getDescription());
        }

        builder.setView(rootView);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String label = mEditTextLabel.getText().toString();
                String desp = mEditTextDesp.getText().toString();

                if (!TextUtils.isEmpty(label) && !TextUtils.isEmpty(desp)) {
                    endNodeModel.setLabel(label);
                    endNodeModel.setDescription(desp);

                    //Internet check
                    int isNetwork = NetworkUtil.getConnectivityStatus(EndNodeActivity.this);
                    if (isNetwork == 0) {
                        Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                                getString(R.string.no_internet), ALERTCONSTANT.ERROR);
                    } else {
                        //Call API for Init gateway
                        Utils.showProgress(EndNodeActivity.this, view, progressBar, true);
                        updateNodeAsync = new UpdateNodeAsync(EndNodeActivity.this, endNodeModel);
                        updateNodeAsync.execute((Void) null);
                    }

                } else {
                    Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                            "Label and Description should't be empty!",
                            ALERTCONSTANT.WARNING);
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        "Cancel",
                        ALERTCONSTANT.WARNING);
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    //API for end node device installation
    public class AddEndNodeAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private EndNodeModel endNodeModel = new EndNodeModel();
        private boolean retVal = false;
        private String message;
        private Long timeStamp;
        private String mService;
        private String mCharacteristic;
        private String mEndNodeUID;
        private String mLabel;
        private String mDescription;
        private double mLatitude;
        private double mLongitude;
        private String mAddress;

        public AddEndNodeAsync(Context context, EndNodeModel endNodeModel) {
            this.context = context;
            this.endNodeModel = endNodeModel;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);
            Log.e(TAG, "DeviceId :: " + mDeviceId);

            if (endNodeModel != null) {
                mEndNodeUID = endNodeModel.getEndNodeUid();
                mLabel = endNodeModel.getLabel();
                mDescription = endNodeModel.getDescription();
                mLatitude = endNodeModel.getLatitude();
                mLongitude = endNodeModel.getLongitude();
                mAddress = endNodeModel.getAddress();
            }

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("bridgeId", bridgeModel.getGatewayUid());
                jsonObject.put("nodeId", mEndNodeUID);
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("isApp", "true");
                jsonObject.put("type", 1);
                jsonObject.put("label", mLabel);
                jsonObject.put("description", mDescription);
                jsonObject.put("latitude", mLatitude);
                jsonObject.put("longitude", mLongitude);
                jsonObject.put("address", mAddress);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(mUrl + "tito-node")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "tito-node");
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

                    if (retVal) {
                        mService = respData.getString("service");
                        mCharacteristic = respData.getString("characteristic");
                        endNodeModel.setService(mService);
                        endNodeModel.setCharacteristic(mCharacteristic);
                        String _id = respData.getString("_Id");
                        endNodeModel.set_Id(_id);
                        endNodeModel.setLatitude(respData.getDouble("latitude"));
                        endNodeModel.setLongitude(respData.getDouble("longitude"));
                        endNodeModel.setAddress(respData.getString("address"));

                    }

                    endNodeModel.setStatus("" + retVal);
                    endNodeModel.setMessage(message);
                    endNodeModel.setTimeStamp((timeStamp));

                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at EndNodeActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at EndNodeActivity: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            Utils.showProgress(EndNodeActivity.this, view, progressBar, false);
            if (isSuccess) {
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        message, ALERTCONSTANT.SUCCESS);
                nodeList.add(endNodeModel);
                nodeAdapter.notifyDataSetChanged();
            } else {
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        message, ALERTCONSTANT.WARNING);
            }
            addEndNodeAsync = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            addEndNodeAsync = null;
            Utils.showProgress(EndNodeActivity.this, view, progressBar, false);
        }
    }

    // get a list Installed node server API
    public class GetNodeListAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private EndNodeModel endNodeModel = new EndNodeModel();
        private boolean retVal = false;
        private String message;
        private Long timeStamp;
        private HashMap<String, EndNodeModel> mListOfIotDeviceInfo = new LinkedHashMap<>();

        public GetNodeListAsync(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("_id", bridgeModel.get_Id());
                jsonObject.put("type", 2);
                jsonObject.put("isApp", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(mUrl + "tito-node")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "tito-node");
            Log.d(TAG, "SH : formBody  " + formBody.toString());
            Log.d(TAG, "SH : request " + request.getClass().toString());


            retVal = false;
            try {
                Response response = client.newCall(request).execute();
                Log.e(TAG, "" + response.toString());

                String authResponseStr = response.body().string();
                Log.e(TAG, "GetListOfRegDevicesAsync : authResponseStr :: " + authResponseStr);

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
                    if (retVal) {

                    }
                    JSONArray jsonArray = respData.getJSONArray("list");

                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));
                    Log.e(TAG, " SH : array list : " + jsonArray.toString());
                    endNodeModel.setStatus("" + retVal);
                    endNodeModel.setMessage(message);

                    if (retVal) {
                        nodeList = new ArrayList<>();
                        modelHashMap = new HashMap<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            endNodeModel = new EndNodeModel();
                            endNodeModel.set_Id(object.getString("_pid"));
                            endNodeModel.setEndNodeUid(object.getString("node_id"));
                            endNodeModel.setLabel(object.getString("label"));
                            endNodeModel.setTimeStamp((object.getLong("timestamp")));
                            endNodeModel.setDescription(object.getString("description"));
                            endNodeModel.setLatitude(object.getDouble("latitude"));
                            endNodeModel.setLongitude(object.getDouble("longitude"));
                            endNodeModel.setAddress(object.getString("address"));
                            endNodeModel.setService(object.getString("service"));
                            endNodeModel.setCharacteristic(object.getString("characteristic"));
                            //add register item into hash map
                            modelHashMap.put(endNodeModel.getEndNodeUid().toString(), endNodeModel);
                        }
                    } else {
                        Log.e(TAG, message);
                    }

                    //add into array list
                    for (EndNodeModel endNodeModel : modelHashMap.values()) {
                        nodeList.add(endNodeModel);
                    }

                    Log.e(TAG, " SH : status : " + respData.getBoolean("status"));
                    Log.e(TAG, " SH : message : " + respData.getString("message"));
                    Log.e(TAG, " SH : Array of installed iot devices : " + nodeList.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at EndNodeActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at EndNodeActivity: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            Utils.showProgress(EndNodeActivity.this, view, progressBar, false);
            if (isSuccess) {
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        message, ALERTCONSTANT.SUCCESS);
                nodeAdapter = new NodeAdapter(EndNodeActivity.this, nodeList);
                listView.setAdapter(nodeAdapter);
                nodeAdapter.notifyDataSetChanged();
            } else {
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        message, ALERTCONSTANT.WARNING);
            }
            getNodeListAsync = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(EndNodeActivity.this, view, progressBar, false);
            getNodeListAsync = null;
        }
    }

    //delete a node
    public class DeleteNodeAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private EndNodeModel endNodeModel;
        private String mUrl, mEmail, token;
        private boolean retVal = false;
        private String message;

        public DeleteNodeAsync(Context context, EndNodeModel endNodeModel) {
            this.context = context;
            this.endNodeModel = endNodeModel;
            mUrl = App.getAppComponent().getApiServiceUrl();
            mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDeviceId = Utils.getDeviceId(context);
            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("userId", token);
                jsonObject.put("deviceId", mDeviceId);
                jsonObject.put("_id", bridgeModel.get_Id());
                jsonObject.put("nodeId", endNodeModel.getEndNodeUid());
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
                    .url(mUrl + "tito-node")
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
                Log.e("ERROR: ", "Exception at EndNodeActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at EndNodeActivity: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean isStatus) {
            super.onPostExecute(isStatus);
            Utils.showProgress(EndNodeActivity.this, view, progressBar, false);
            if (isStatus) {
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        "Node Removed Successfully", ALERTCONSTANT.SUCCESS);

                nodeList.remove(endNodeModel);
                nodeAdapter.notifyDataSetChanged();
            } else {
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        message, ALERTCONSTANT.WARNING);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(EndNodeActivity.this, view, progressBar, false);
        }
    }

    //update oe edit node details
    public class UpdateNodeAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private EndNodeModel endNodeModel;
        private boolean retVal;
        private String message, status;
        private String mUrl, mEmail, token;

        public UpdateNodeAsync(Context context, EndNodeModel endNodeModel) {
            this.context = context;
            this.endNodeModel = endNodeModel;
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
                jsonObject.put("_id", bridgeModel.get_Id());
                jsonObject.put("nodeId", endNodeModel.getEndNodeUid());
                jsonObject.put("label", endNodeModel.getLabel());
                jsonObject.put("description", endNodeModel.getDescription());
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
                    .url(mUrl + "tito-node")
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

                Log.e("ERROR: ", "Exception at EndNodeActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at EndNodeActivity: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean isStatus) {
            super.onPostExecute(isStatus);
            Utils.showProgress(context, view, progressBar, false);
            if (isStatus) {
                nodeList.remove(endNodeModel);
                nodeAdapter.notifyDataSetChanged();
                nodeList.add(0, endNodeModel);
                nodeAdapter.notifyDataSetChanged();
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        "Node updated Successfully", ALERTCONSTANT.SUCCESS);
            } else {
                Utils.SnackBarView(EndNodeActivity.this, coordinatorLayout,
                        message, ALERTCONSTANT.WARNING);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utils.showProgress(context, view, progressBar, false);
        }
    }

}
