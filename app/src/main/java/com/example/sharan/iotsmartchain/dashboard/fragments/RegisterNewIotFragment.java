package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.NormalFlow.activities.RegisterIoTDeviceActivity;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.NormalFlow.adapter.AdapterRegisterIoTDevices;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;
import com.example.sharan.iotsmartchain.model.RegisterIoTInfo;
import com.example.sharan.iotsmartchain.qrcodescanner.QrCodeActivity;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterNewIotFragment extends BaseFragment {

    private static String TAG = "RegisterNewIotFragment";
    private static final int REQUEST_CODE_QR_SCAN = 101;

    private ListView mListView;
    private FloatingActionButton mFloatingActionButtonAddManually;
    private FloatingActionButton mFloatingActionButtonScanner;
    //Dialog editText
    private EditText mEditTextUID;

    private Dialog dialog;
    private AlertDialog.Builder builder;
    private AdapterRegisterIoTDevices adapterRegisterIoTDevices;
    private ArrayList<RegisterIoTInfo> mList;
    private String mUrl;

    public static RegisterNewIotFragment newInstance() {
        RegisterNewIotFragment registerNewIotFragment = new RegisterNewIotFragment();
        return registerNewIotFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = App.getAppComponent().getApiServiceUrl();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_db_reg_iot, container, false);
        mListView = (ListView) root.findViewById(R.id.listView_db_add_iot);
        mFloatingActionButtonAddManually = (FloatingActionButton) root.findViewById(R.id.floatingActionButton_manually);
        mFloatingActionButtonScanner = (FloatingActionButton) root.findViewById(R.id.floatingActionButton_scanner);

        mList = new ArrayList<>();
        adapterRegisterIoTDevices = new AdapterRegisterIoTDevices(getActivity(), mList);
        mListView.setAdapter(adapterRegisterIoTDevices);


        mFloatingActionButtonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Scanner Not available", Toast.LENGTH_SHORT).show();
                //TODO call qr code scanner
                Intent intent = new Intent(getActivity(), QrCodeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
            }
        });

        mFloatingActionButtonAddManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  showDialog(DIALOG_ADD_IOT);
                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dailog_add_iot);

                builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View rootView = inflater.inflate(R.layout.dailog_add_iot, null);

                mEditTextUID = (EditText)rootView.findViewById(R.id.editText_UID);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(rootView);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                        Log.d("SH : ", mEditTextUID.getText().toString().trim());
                        RegisterIoTInfo registerIoTInfo = new RegisterIoTInfo();
                        registerIoTInfo.setSensorName(mEditTextUID.getText().toString().trim());
                        String strData = Utils.getDataFormat();
                        registerIoTInfo.setTimeStamp(strData);

                        mList.add(registerIoTInfo);
                        adapterRegisterIoTDevices.notifyDataSetChanged();

                        //clear data
                        mEditTextUID.setText("");
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //clear data
                        mEditTextUID.setText("");
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, " requestCode : "+requestCode+" \nresultCode : "+resultCode+" \nData : "+data);
        if(resultCode != Activity.RESULT_OK)
        {
            Log.d("BT","COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.example.sharan.iotsmartchain.qrcodescanner.got_qr_scan_relult");
            Log.d("BT","Have scan result in your app activity :"+ result);
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            //TODO Register IoT Device and Check is required
            /*
             * API : Connect server to Register IoT devices
             *
             * */
//            registerIoTDeviceAsync = new RegisterIoTDeviceAsync(RegisterIoTDeviceActivity.this,
//                    loginId, result);

            RegisterIoTInfo registerIoTInfo = new RegisterIoTInfo();
            registerIoTInfo.setSensorName(result);
            registerIoTInfo.setTimeStamp(Utils.getDataFormat());

            mList.add(registerIoTInfo);
            adapterRegisterIoTDevices.notifyDataSetChanged();
        }
    }

    /*Represents an asynchronous registration Iot device  */
    public class RegisterIoTDeviceAsync extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mIoTAdd;
        private Context mContext;

        public RegisterIoTDeviceAsync(Context mContext, String mEmail, String mIotAdd) {
            this.mEmail = mEmail;
            this.mIoTAdd = mIotAdd;
            this.mContext = mContext;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormEncodingBuilder()
                    .add("emailId", mEmail)
                    .add("iotadd", mIoTAdd)
                    .add("isApp", "true")
                    .build();
            Request request = new Request.Builder()
                    .url(mUrl + "/RegIoT")
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
//                    AuthResponse authResponse = new GsonBuilder()
//                            .create()
//                            .fromJson(authResponseStr, AuthResponse.class);
//                    String tokenStr = authResponse.getData().getToken();

                    //TODO goto DASH BROAD / HOME SCREEN
//                    Intent homeIntent = new Intent(mContext, HomeActivity.class);
//                    startActivity(homeIntent);
//                    finish();

                    RegisterIoTInfo registerIoTInfo = new RegisterIoTInfo();
                    registerIoTInfo.setSensorName(mIoTAdd);
                    registerIoTInfo.setSensorStatus("Status : ON ");
                    registerIoTInfo.setTimeStamp(Utils.getDataFormat());

                    mList.add(registerIoTInfo);
                    adapterRegisterIoTDevices.notifyDataSetChanged();

                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at Register IoT device: " + e.getMessage());
            } catch (NullPointerException e1){
                Log.e("ERROR: ", "null pointer Exception at IoT device: " + e1.getMessage());
            }
            return retVal;
        }
    }
}


