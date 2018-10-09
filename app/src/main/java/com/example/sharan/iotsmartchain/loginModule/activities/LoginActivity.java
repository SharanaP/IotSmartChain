package com.example.sharan.iotsmartchain.loginModule.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.FireBaseMessagModule.Config;
import com.example.sharan.iotsmartchain.NormalFlow.activities.HomeActivity;
import com.example.sharan.iotsmartchain.NormalFlow.activities.RegisterIoTDeviceActivity;
import com.example.sharan.iotsmartchain.Presenter.LoginActivityPresenter;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.Services.RegistrationIntentService;
import com.example.sharan.iotsmartchain.dashboard.activity.DashBoardActivity;
import com.example.sharan.iotsmartchain.global.NetworkUtil;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.loginModule.fragments.ResetPswFragment;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.DataModel;
import com.example.sharan.iotsmartchain.model.LoginResultType;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Sharan on 15-03-2018.
 */

public class LoginActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static final int TIME_INTERVAL = 2000; // milliseconds, time between two back presses.
    public static String TAG = "LoginActivity";
    public static int ALERT_DIALOG_ID = 1;
    @BindView(R.id.app_logo)
    ImageView mImageAppLogo;
    @BindView(R.id.textView_app_title)
    TextView mTvAppTitle;
    @BindView(R.id.email_login_form)
    LinearLayout mLinearLoginLayout;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.relativeLayout_login_form)
    View mLoginFormView;
    @BindView(R.id.login_email)
    AutoCompleteTextView mEmailView;
    @BindView(R.id.login_password)
    EditText mPasswordView;
    @BindView(R.id.ic_showPassword)
    ImageView mPasswordVisibility;
    @BindView(R.id.textView_forgot_psw)
    TextView mTvForgotPsw;
    //  @BindView(R.id.checkBox_Request_Otp) CheckBox mCheckRequestOtp;
    @BindView(R.id.email_sign_in_button)
    Button mSignInButton;
    @BindView(R.id.otp_sign_in_button)
    Button mOtpLoginButton;
    @BindView(R.id.tv_signup)
    TextView mTvForSignUp;
    @BindView(R.id.email_sign_up_button)
    Button mSingUpButton;
    private String mUrl, loginId, token;
    private UserLoginAsync mAuthTask = null;
    private LoginActivityPresenter mPresenter;
    private String registrationId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private long mBackPressed;
    private View.OnTouchListener mPasswordVisibleTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final boolean isOutsideView = event.getX() < 0 ||
                    event.getX() > v.getWidth() ||
                    event.getY() < 0 ||
                    event.getY() > v.getHeight();

            // change input type will reset cursor position, so we want to save it
            final int cursor = mPasswordView.getSelectionStart();

            if (isOutsideView || MotionEvent.ACTION_UP == event.getAction())
                mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
            else
                mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            mPasswordView.setSelection(cursor);

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.login_activity);
        setContentView(R.layout.activity_login_screen);

        injectViews();

        App.inject(this);

        Log.d(TAG, "" + NetworkUtil.getConnectivityStatusString(this));

        Log.d(TAG, "" + NetworkUtil.getConnectivityStatus(this));

        //Self Check permissions
        insertDummyContactWrapper();


        /*get email*/
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String email = bundle.getString("email");
            if(email != null)
            mEmailView.setText(email);
        }

        //Handle a Push notification
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "" + intent.getAction().toString());
                // checking for type intent filter
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Log.d(TAG, "" + message);

                    Toast.makeText(getApplicationContext(), "Push notification: " + message,
                            Toast.LENGTH_LONG).show();

                }
            }
        };

        //TODO check
        registrationId = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "registrationId : " + registrationId);

        mPresenter = App.getLoginActivityComponent().getLoginActivityPresenter();
        mUrl = App.getAppComponent().getApiServiceUrl();
        //   populateAutoComplete();

        //TODO check permission
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
        } else {
            //TODO
            startRegisterService(); //TODO
        }

        mPasswordVisibility.setOnTouchListener(mPasswordVisibleTouchListener);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // normally login through emailId and Psw
                attemptSignIn();
                //Close  keyboard
                Utils.CloseKeyboard(LoginActivity.this);
            }
        });

        mOtpLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call by activity
                Intent otpIntent = new Intent(LoginActivity.this, OtpLoginActivity.class);
                startActivity(otpIntent);
            }
        });

        mSingUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerEmailSignUp();
            }
        });

        mTvForgotPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close  keyboard
                Utils.CloseKeyboard(LoginActivity.this);

                //Reset password link and set new password
                Intent intentForgotPsw = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intentForgotPsw);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void startRegisterService() {
        Intent intent = new Intent(LoginActivity.this, RegistrationIntentService.class);
//        Log.d(TAG, "DEVICE_ID : "+getDeviceId());
//        Log.d(TAG, "DEVICE_NAME : "+getDeviceName());
        intent.putExtra("DEVICE_ID", getDeviceId());
        intent.putExtra("DEVICE_NAME", getDeviceName());
        startService(intent);
    }

    @SuppressLint("MissingPermission")
    private String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        Log.d(TAG, telephonyManager.getDeviceId());
        return telephonyManager.getDeviceId();
    }

    private String getDeviceName() {
        String deviceName = Build.MODEL;
        String deviceMan = Build.MANUFACTURER;
        Log.d(TAG, deviceMan + " " + deviceName);
        return deviceMan + " " + deviceName;
    }

    private void attemptSignIn() {

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered mBtnOne.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //TODO showProgress(true);
            showProgress(true);
            mAuthTask = new UserLoginAsync(this, email, password, registrationId);
            mAuthTask.execute((Void) null);

        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void registerEmailSignUp() {
        Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(regIntent);
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified mBtnOne.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        EditText mEditTextOTP;
        Button mRegenerateOTP;
        Button mSubmitOTP;
        TextView mStatus;

        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = LoginActivity.this.getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_otp_login, null);

        mEditTextOTP = (EditText) rootView.findViewById(R.id.editText_enter_otp);
        mRegenerateOTP = (Button) rootView.findViewById(R.id.button_RegenerateOTP);
        mSubmitOTP = (Button) rootView.findViewById(R.id.button_submitOTP);
        mStatus = (TextView) rootView.findViewById(R.id.textView_Status);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView);

        mRegenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mSubmitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpStr = mEditTextOTP.getText().toString();
                Toast.makeText(getApplicationContext(), "----" + otpStr, Toast.LENGTH_SHORT).show();
                //TODO
                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });
        dialog = builder.create();

        return dialog;
    }

    private void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Write Contacts");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read External Storage");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write External Storage");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Phone");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            }
                        });
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            return;
        }


        insertDummyContact();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void insertDummyContact() {
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    insertDummyContact();
                } else {
                    // Permission Denied
                    Toast.makeText(LoginActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }

                //TODO check play services

            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void DashBoardScreen() {
        Intent homeIntent = new Intent(LoginActivity.this, DashBoardActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(homeIntent);
        LoginActivity.this.finish();
    }

    private void RegisterIoTScreen() {
        Intent intent = new Intent(LoginActivity.this, RegisterIoTDeviceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        //TODO NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        Toast onBackPressedToast = Toast
                .makeText(getBaseContext(), "Tap back again to exit", Toast.LENGTH_SHORT);
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis() && count == 0) {
            super.onBackPressed();
            onBackPressedToast.cancel();
            return;
        } else {
            onBackPressedToast.show();
        }
        getFragmentManager().popBackStack();
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Log.d(TAG, ":: HOME");
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /*Represents an asynchronous login/registration task used a authenticate to a user  */
    public class UserLoginAsync extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mRegTokenId;
        private Context mContext;
        private LoginResultType mLoginResultType;
        private String deviceId = "";
        private String deviceName = "";
        private String deviceToken = "";
        private DataModel authResponse = new DataModel();

        public UserLoginAsync(Context mContext, String mEmail, String mPassword, String registrationId) {
            this.mEmail = mEmail;
            this.mPassword = mPassword;
            this.mContext = mContext;
            mRegTokenId = registrationId;
            mLoginResultType = LoginResultType.LOGIN_SERVER_ERROR;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            deviceId = Utils.getDeviceId(getApplicationContext());
            deviceName = Utils.getDeviceName();
            deviceToken = FirebaseInstanceId.getInstance().getToken();

            Log.e(TAG, "deviceId : " + deviceId);
            Log.e(TAG, "deviceName : " + deviceName);
            Log.e(TAG, "deviceToken : " + deviceToken);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("password", mPassword);
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("deviceName", deviceName);
                jsonObject.put("deviceTokenId", deviceToken);
                jsonObject.put("isApp", "true");
                jsonObject.put("signUp", "false");
                jsonObject.put("loginType", "email");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "register")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl);
            Log.d(TAG, "SH : email  " + mEmail);
            Log.d(TAG, "SH : password " + mPassword);

            boolean retVal = false;
            try {

                Response response = client.newCall(request).execute();

                if (response.code() != 200) {
                    mLoginResultType = LoginResultType.LOGIN_FAILED;
                    retVal = false;
                } else {

                    String authResponseStr = response.body().string();

                    //Json object
                    try {
                        JSONObject TestJson = new JSONObject(authResponseStr);

                        Log.e(TAG, "authResponse :: " + TestJson.toString());
                        Log.e(TAG, "authResponse :: " + TestJson.getString("body").toString());

                        String strData = TestJson.getString("body").toString();
                        Log.e(TAG, "strData :: " + strData.toString());

                        authResponse = new GsonBuilder()
                                .create()
                                .fromJson(strData, DataModel.class);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    String emailStr = authResponse.getEmailId();
                    Log.d(TAG, "emailStr : " + emailStr);
                    String message = authResponse.getMessage();
                    Log.d(TAG, "message : " + message);
                    boolean status = authResponse.isStatus();
                    Log.d(TAG, "Status : " + status);
                    String tokenid = authResponse.getToken();
                    Log.d(TAG, "" + tokenid);

                    if (status) {
                        mLoginResultType = LoginResultType.LOGIN_SUCCESS;
                        retVal = true;

                        if (!tokenid.isEmpty()) {
                            SharedPreferences.Editor
                                    editor = App.getSharedPrefsComponent().getSharedPrefsEditor();
                            editor.putString("TOKEN", tokenid);
                            editor.putString("AUTH_EMAIL_ID", authResponse.getEmailId());
                            editor.putString("NAME", authResponse.getName());
                            editor.putString("PHONE", authResponse.getPhone());
                            editor.apply();
                            App.setLoginId(authResponse.getEmailId());
                            App.setTokenStr(tokenid);

                        } else {
                            Log.d(TAG, "Invalid emial Id....!");
                        }
                    } else {
                        mLoginResultType = LoginResultType.LOGIN_FAILED;
                        retVal = false;
                        Log.d(TAG, "Failed login email Id....try again!");
                    }
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at LoginActivity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at LoginActivity: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {

                //TODO first Time Login goto Register Iot devices Screen
                RegisterIoTScreen();

                //TODO goto DASH BROAD / HOME SCREEN
                //   DashBoardScreen();

            } else {

                if (authResponse.getMessage().toString().equalsIgnoreCase("Invalid Password")) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                } else {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                }

                Snackbar sEvents = Snackbar.make(mLoginFormView,
                        authResponse.getMessage() + " and User is unable login - Try again later!",
                        Snackbar.LENGTH_LONG);
                sEvents.show();
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            mAuthTask = null;
            showProgress(false);
            super.onCancelled(aBoolean);
        }
    }
}
