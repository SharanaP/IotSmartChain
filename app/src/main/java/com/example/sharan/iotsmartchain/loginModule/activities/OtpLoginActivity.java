package com.example.sharan.iotsmartchain.loginModule.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.NormalFlow.activities.RegisterIoTDeviceActivity;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.SMS.OnSmsCatchListener;
import com.example.sharan.iotsmartchain.SMS.SmsVerifyCatcher;
import com.example.sharan.iotsmartchain.dashboard.activity.DashBoardActivity;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.DataModel;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

public class OtpLoginActivity extends BaseActivity {
    private static String TAG = OtpLoginActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.editText_enter_mobile)
    EditText mEditTextMobile;
    @BindView(R.id.editText_enter_otp)
    EditText mEditTextOTP;
    @BindView(R.id.textView_Status)
    TextView mTextViewStatus;
    @BindView(R.id.button_RegenerateOTP)
    Button mResendOtpButton;
    @BindView(R.id.button_submitOTP)
    Button mVerifyOtpButton;
    @BindView(R.id.login_progress)
    ProgressBar mProgressView;
    @BindView(R.id.relativeLayout_login_form)
    View mView;
    @BindView(R.id.button_request_otp)
    Button mRequestOtpButton;
    @BindView(R.id.textView_TimeDownCounter)
    TextView mTextViewTimeCountDowner;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    private SmsVerifyCatcher smsVerifyCatcher;
    private UserLoginOTPAsync userLoginOTPAsync = null;
    private RequestOtpAsync requestOtpAsync = null;
    private String deviceId, deviceName, deviceToken, mUrl;

    private CountDownTimer countDownTimer = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_otp_login);
        injectViews();
        setupToolbar();

        //get url and device id
        mUrl = App.getAppComponent().getApiServiceUrl();
        deviceId = Utils.getDeviceId(OtpLoginActivity.this);
        deviceName = Utils.getDeviceName();
        deviceToken = FirebaseInstanceId.getInstance().getToken();

        mEditTextOTP.setVisibility(View.INVISIBLE);
        mResendOtpButton.setVisibility(View.INVISIBLE);

        //init SmsVerifyCatcher
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                Log.e(TAG, "SH : msg :  " + message);
                String code = parseCode(message);//Parse verification code
                Log.e(TAG, "SH : otp : " + code);
                mEditTextOTP.setText(code);//set code in edit text

                //Time down counter cancel and start new timer verification
                if (countDownTimer != null) {
                    countDownTimer.cancel(); //Time down counter
                    countDownTimer.onFinish();
                    mTextViewTimeCountDowner.setText("");
                }

                countDownTimer = Utils.showTimeCountDowner(mTextViewTimeCountDowner, 1);
                countDownTimer.start();

                //then you can send verification code to server
            }
        });

        //set phone number filter if needed
        String str = "XX-Notice";
        Log.e(TAG, "" + str.substring(2));
        smsVerifyCatcher.setPhoneNumberFilter(str.substring(2));
        //smsVerifyCatcher.setFilter("Verification code:");

        mResendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Regenerate OTP number
                String mobileStr = mEditTextMobile.getText().toString();
                if (!mobileStr.isEmpty()) {
                    showProgress(true);
                    requestOtpAsync = new RequestOtpAsync(OtpLoginActivity.this, mobileStr);
                    requestOtpAsync.execute((Void) null);
                } else {
                    mEditTextMobile.setError(getString(R.string.error_field_required));
                    mEditTextMobile.requestFocus();
                    Utils.SnackBarView(OtpLoginActivity.this,
                            mCoordinatorLayout, "Please enter registered mobile number...",
                            ALERTCONSTANT.ERROR);
                }
            }
        });

        mRequestOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // requesting for OTP
                String mobile = mEditTextMobile.getText().toString();
                if (!mobile.isEmpty()) {

                    if (mobile.length() == 10) {
                        if (isValidMobile(mobile)) {
                            showProgress(true);
                            requestOtpAsync = new RequestOtpAsync(OtpLoginActivity.this, mobile);
                            requestOtpAsync.execute((Void) null);
                        } else {
                            mEditTextMobile.setError(getString(R.string.error_invalid_phone_number));
                            mEditTextMobile.requestFocus();
                            Utils.SnackBarView(OtpLoginActivity.this,
                                    mCoordinatorLayout, "Please enter registered mobile number...",
                                    ALERTCONSTANT.WARNING);
                        }
                    } else {
                        mEditTextMobile.setError(getString(R.string.error_invalid_phone_number));
                        mEditTextMobile.requestFocus();
                        Utils.SnackBarView(OtpLoginActivity.this,
                                mCoordinatorLayout, "Please enter 10-digit mobile number...!",
                                ALERTCONSTANT.WARNING);
                    }

                } else {
                    mEditTextMobile.setError(getString(R.string.error_field_required));
                    mEditTextMobile.requestFocus();
                    Utils.SnackBarView(OtpLoginActivity.this,
                            mCoordinatorLayout, "Please enter registered mobile number...",
                            ALERTCONSTANT.ERROR);
                }
            }
        });

        mVerifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // submit otp number via mobile
                String mobileStr = mEditTextMobile.getText().toString();
                String otpStr = mEditTextOTP.getText().toString();

                if (countDownTimer != null) {
                    countDownTimer.cancel(); //Time down counter
                    countDownTimer.onFinish();
                    mTextViewTimeCountDowner.setText("");
                }

                if (!mobileStr.isEmpty()) {
                    if (isValidMobile(mobileStr)) {
                        if (!otpStr.isEmpty()) {
                            showProgress(true);
                            userLoginOTPAsync = new UserLoginOTPAsync(OtpLoginActivity.this,
                                    mobileStr, otpStr);
                            userLoginOTPAsync.execute((Void) null);
                        } else {
                            mEditTextOTP.setError(getString(R.string.error_field_required));
                            mEditTextOTP.requestFocus();
                            Utils.SnackBarView(OtpLoginActivity.this,
                                    mCoordinatorLayout, "Enter OTP number...!",
                                    ALERTCONSTANT.WARNING);
                        }

                    } else {
                        mEditTextMobile.setError(getString(R.string.error_invalid_phone_number));
                        mEditTextMobile.requestFocus();

                        Utils.SnackBarView(OtpLoginActivity.this,
                                mCoordinatorLayout, "Please enter registered mobile number...!",
                                ALERTCONSTANT.WARNING);
                    }
                } else {
                    mEditTextMobile.setError(getString(R.string.error_field_required));
                    mEditTextMobile.requestFocus();
                    Utils.SnackBarView(OtpLoginActivity.this,
                            mCoordinatorLayout, "Enter mobile number it should not be empty!",
                            ALERTCONSTANT.WARNING);
                }
            }
        });
    }

    private void DashBoardScreen() {
        Intent homeIntent = new Intent(OtpLoginActivity.this, DashBoardActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(homeIntent);
        OtpLoginActivity.this.finish();
    }

    private void RegisterIoTScreen() {
        Intent intent = new Intent(OtpLoginActivity.this, RegisterIoTDeviceActivity.class);
        startActivity(intent);
        finish();
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

            mView.setVisibility(show ? View.GONE : View.VISIBLE);
            mView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("Login via OTP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        menuRefresh.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isValidMobile(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    /* Server API : To request server for login through a mobile OTP*/
    public class UserLoginOTPAsync extends AsyncTask<Void, String, Boolean> {

        private Context context;
        private String mobile;
        private String otp;
        private boolean retVal = false;
        private DataModel authResponse = new DataModel();

        public UserLoginOTPAsync(Context context, String mobile, String otp) {
            this.context = context;
            this.mobile = mobile;
            this.otp = otp;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("phone", mobile);
                jsonObject.put("loginOTP", otp);
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("deviceName", deviceName);
                jsonObject.put("deviceTokenId", deviceToken);
                jsonObject.put("isApp", "true");
                jsonObject.put("signUp", "false");
                jsonObject.put("loginType", "otp");
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
            Log.d(TAG, "SH : mobile  " + mobile);
            Log.d(TAG, "SH : otp " + otp);
            retVal = false;
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
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
                    if (authResponse.isStatus()) {
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
                        }
                    } else {
                        retVal = false;
                    }
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at OTP login activity: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at OTP login Activity: " + e1.getMessage());
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            userLoginOTPAsync = null;
            showProgress(false);
            if (success) {
                //TODO first Time Login goto Register Iot devices Screen
                // RegisterIoTScreen();
                //TODO goto DASH BROAD / HOME SCREEN
                DashBoardScreen();
                Snackbar sEvents = Snackbar.make(mEditTextMobile,
                        authResponse.getMessage(),
                        Snackbar.LENGTH_LONG);
                sEvents.show();
                Utils.SnackBarView(OtpLoginActivity.this,
                        mCoordinatorLayout, authResponse.getMessage(),
                        ALERTCONSTANT.SUCCESS);
            } else {
                if (authResponse.getMessage().toString().equalsIgnoreCase("Invalid Password")) {
                    mEditTextOTP.setError(getString(R.string.error_otp_not_match));
                    mEditTextOTP.requestFocus();
                    Utils.SnackBarView(OtpLoginActivity.this,
                            mCoordinatorLayout, getString(R.string.error_otp_not_match),
                            ALERTCONSTANT.ERROR);
                } else if (authResponse.getMessage().toString().equalsIgnoreCase("OTP not match")) {
                    mEditTextOTP.setError(getString(R.string.error_otp_not_match));
                    mEditTextOTP.requestFocus();
                    Utils.SnackBarView(OtpLoginActivity.this,
                            mCoordinatorLayout, getString(R.string.error_otp_not_match),
                            ALERTCONSTANT.ERROR);
                } else {
                    mEditTextMobile.setError(authResponse.getMessage());
                    mEditTextMobile.requestFocus();
                    Utils.SnackBarView(OtpLoginActivity.this,
                            mCoordinatorLayout, authResponse.getMessage(),
                            ALERTCONSTANT.WARNING);
                }
                Utils.SnackBarView(OtpLoginActivity.this,
                        mCoordinatorLayout, authResponse.getMessage() + " and User is unable login - Try again later!",
                        ALERTCONSTANT.WARNING);
            }
        }

        @Override
        protected void onCancelled() {
            userLoginOTPAsync = null;
            super.onCancelled();
            showProgress(false);
        }
    }

    /* Requesting for OTP based on Registered mobile number */
    public class RequestOtpAsync extends AsyncTask<Void, String, Boolean> {
        private Context context;
        private String mobile;
        private boolean retVal = false;
        private DataModel authResponse = new DataModel();

        public RequestOtpAsync(Context context, String mobile) {
            this.context = context;
            this.mobile = mobile;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("phone", mobile);
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("deviceName", deviceName);
                jsonObject.put("deviceTokenId", deviceToken);
                jsonObject.put("isApp", "true");
                jsonObject.put("signUp", "false");
                jsonObject.put("loginType", "otp");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(mUrl + "generate-login-otp")
                    .post(formBody)
                    .build();
            Log.d(TAG, "SH : URL " + mUrl);
            Log.d(TAG, "SH : mobile  " + mobile);
            retVal = false;
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
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

                    if (authResponse.isStatus()) {
                        retVal = true;
                    } else {
                        retVal = false;
                    }
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at generating login OTP  " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at generating login OTP " + e1.getMessage());
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            requestOtpAsync = null;
            showProgress(false);
            if (success) {
                if (countDownTimer != null) {
                    countDownTimer.cancel(); //Time down counter
                    countDownTimer.onFinish();
                    mTextViewTimeCountDowner.setText("");
                }

                countDownTimer = Utils.showTimeCountDowner(mTextViewTimeCountDowner, 1);
                countDownTimer.start();
                //clear otp
                mEditTextOTP.setText("");
                mVerifyOtpButton.setVisibility(View.VISIBLE);
                mRequestOtpButton.setVisibility(View.INVISIBLE);
                mEditTextOTP.setVisibility(View.VISIBLE);
                mResendOtpButton.setVisibility(View.VISIBLE);
                Utils.SnackBarView(OtpLoginActivity.this,
                        mCoordinatorLayout, authResponse.getMessage(), ALERTCONSTANT.SUCCESS);
            } else {
                mVerifyOtpButton.setVisibility(View.INVISIBLE);
                mRequestOtpButton.setVisibility(View.VISIBLE);
                mResendOtpButton.setVisibility(View.INVISIBLE);
                mEditTextMobile.setError(getString(R.string.error_unregister_phone_number));
                mEditTextMobile.requestFocus();
                Utils.SnackBarView(OtpLoginActivity.this,
                        mCoordinatorLayout,
                        authResponse.getMessage() + " and Please enter registered mobile number",
                        ALERTCONSTANT.WARNING);
            }
            super.onPostExecute(success);
        }

        @Override
        protected void onCancelled() {
            requestOtpAsync = null;
            super.onCancelled();
            showProgress(false);
        }
    }

}
