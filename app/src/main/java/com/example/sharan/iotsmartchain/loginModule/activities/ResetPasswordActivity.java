package com.example.sharan.iotsmartchain.loginModule.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
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

import butterknife.BindView;

public class ResetPasswordActivity extends BaseActivity {
    private static String TAG = ResetPasswordActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cardView_one)
    CardView mCardView_One;
    @BindView(R.id.cardView_two)
    CardView mCardView_Two;
    @BindView(R.id.editText_email_mob)
    EditText mEditEmail;
    @BindView(R.id.editText_email_otp)
    EditText mEditOTP;
    @BindView(R.id.editText_password)
    EditText mEditPsw;
    @BindView(R.id.editText_reenter_password)
    EditText mEditReEnterPsw;
    @BindView(R.id.button_send_reset_link)
    Button mBtnSendReSetLink;
    @BindView(R.id.button_send_otp)
    Button mSendOtpButton;
    @BindView(R.id.button_submit)
    Button mBtnSubmit;
    @BindView(R.id.linearlayout_main)
    LinearLayout linearLayoutMain;
    @BindView(R.id.relativeLayout_progress)
    View mLayoutProgress;
    @BindView(R.id.progressBar_reset)
    View mProgressBar;
    @BindView(R.id.ic_newShowPassword)
    ImageView mImageNewPswView;
    @BindView(R.id.ic_reEnterShowPassword)
    ImageView mImageReEnterPswView;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private String mUrl, deviceId, deviceName, deviceToken;
    private SendResetLinkAync sendResetLinkAync = null;
    private VerifyEmailViaOtpAsync verifyEmailViaOtpAsync = null;
    private UpdateNewPasswordAsync updateNewPasswordAsync = null;

    private View.OnTouchListener mNewPswListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final boolean isOutsideView = event.getX() < 0 ||
                    event.getX() > v.getWidth() ||
                    event.getY() < 0 ||
                    event.getY() > v.getHeight();

            // change input type will reset cursor position, so we want to save it
            final int cursor = mEditPsw.getSelectionStart();

            if (isOutsideView || MotionEvent.ACTION_UP == event.getAction())
                mEditPsw.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
            else
                mEditPsw.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            mEditPsw.setSelection(cursor);

            return true;
        }
    };

    private View.OnTouchListener mReEnterPswListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final boolean isOutsideView = event.getX() < 0 ||
                    event.getX() > v.getWidth() ||
                    event.getY() < 0 ||
                    event.getY() > v.getHeight();

            // change input type will reset cursor position, so we want to save it
            final int cursor = mEditReEnterPsw.getSelectionStart();

            if (isOutsideView || MotionEvent.ACTION_UP == event.getAction())
                mEditReEnterPsw.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
            else
                mEditReEnterPsw.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            mEditReEnterPsw.setSelection(cursor);

            return true;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reset_psw);
        injectViews();
        setupToolbar();

        //init url
        mUrl = App.getAppComponent().getApiServiceUrl();
        deviceId = Utils.getDeviceId(ResetPasswordActivity.this);
        deviceName = Utils.getDeviceName();
        deviceToken = FirebaseInstanceId.getInstance().getToken();

        //cardView_two, edit text_otp and sendOTP invisible
        mCardView_Two.setVisibility(View.INVISIBLE);
        mEditOTP.setVisibility(View.INVISIBLE);
        mSendOtpButton.setVisibility(View.INVISIBLE);

        /*Email entering*/
        mEditEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.EnableSoftKeyBoard(ResetPasswordActivity.this, mEditEmail);
            }
        });

        /*enter OTP*/
        mEditOTP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.EnableSoftKeyBoard(ResetPasswordActivity.this, mEditOTP);
            }
        });

        /*Enter password */
        mEditPsw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.EnableSoftKeyBoard(ResetPasswordActivity.this, mEditPsw);
            }
        });

        /*Re-enter password*/
        mEditReEnterPsw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.EnableSoftKeyBoard(ResetPasswordActivity.this, mEditReEnterPsw);
            }
        });

        //button Send reset link listener
        mBtnSendReSetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendResetPswRequest();
                //Close  keyboard
                Utils.DisableSoftKeyBoard(ResetPasswordActivity.this);
            }
        });

        //Send email and OTP for verification
        mSendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyViaOtp();
                //Close  keyboard
                Utils.DisableSoftKeyBoard(ResetPasswordActivity.this);
            }
        });

        //Set new login password button listener
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable functionality of send reset link button
                mBtnSendReSetLink.setEnabled(false);
                ValidatePassword();
                //Close  keyboard
                Utils.DisableSoftKeyBoard(ResetPasswordActivity.this);
            }
        });

        mImageNewPswView.setOnTouchListener(mNewPswListener);

        mImageReEnterPswView.setOnTouchListener(mReEnterPswListener);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*Validate input Password and send request to server update new password */
    private void ValidatePassword() {
        boolean cancel = false;
        View focusView = null;

        String password = mEditPsw.getText().toString();
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mEditPsw.setError(getString(R.string.error_invalid_password));
            focusView = mEditPsw;
            cancel = true;
        }

        String reEnterPsw = mEditReEnterPsw.getText().toString();
        if (TextUtils.isEmpty(reEnterPsw) && !isPasswordValid(reEnterPsw)) {
            mEditReEnterPsw.setError(getString(R.string.error_invalid_password));
            focusView = mEditReEnterPsw;
            cancel = true;
        }

        if (password.equals(reEnterPsw)) ;
        else {
            focusView = mEditReEnterPsw;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            showProgress(true);
            //Update new  Password
            updateNewPasswordAsync = new UpdateNewPasswordAsync(getApplicationContext(),
                    mEditEmail.getText().toString(), password);
            updateNewPasswordAsync.execute((Void) null);
        }

    }

    /*Validate input email and send a request to server to get OTP to verify user email id */
    private void SendResetPswRequest() {
        boolean cancel = false;
        View focusView = null;

        String email = mEditEmail.getText().toString();
        if (TextUtils.isEmpty(email) && !isEmailValid(email)) {
            mEditEmail.setError(getString(R.string.error_field_required));
            focusView = mEditEmail;
            mEditEmail.requestFocus();
            cancel = true;
        } else {

            if (!isEmailValid(email)) {
                mEditEmail.setError(getString(R.string.error_invalid_email));
                focusView = mEditEmail;
                mEditEmail.requestFocus();
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //async request for reset password
            showProgress(true);
            sendResetLinkAync = new SendResetLinkAync(getApplicationContext(), email);
            sendResetLinkAync.execute((Void) null);
        }
    }

    /*Send email and otp to server for validation */
    private void VerifyViaOtp() {
        boolean cancel = false;
        View focusView = null;

        String email = mEditEmail.getText().toString();
        if (TextUtils.isEmpty(email) && !isEmailValid(email)) {
            mEditEmail.setError(getString(R.string.error_field_required));
            focusView = mEditEmail;
            mEditEmail.requestFocus();
            cancel = true;
        } else {

            if (!isEmailValid(email)) {
                mEditEmail.setError(getString(R.string.error_invalid_email));
                focusView = mEditEmail;
                mEditEmail.requestFocus();
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            String otp = mEditOTP.getText().toString();

            if (TextUtils.isEmpty(otp)) {
                mEditOTP.setError(getString(R.string.error_field_required));
                focusView = mEditOTP;
                mEditOTP.requestFocus();
            } else {
                //Send to server
                showProgress(true);
                verifyEmailViaOtpAsync = new VerifyEmailViaOtpAsync(ResetPasswordActivity.this,
                        email, otp);
                verifyEmailViaOtpAsync.execute((Void) null);
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
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

            mLayoutProgress.setVisibility(show ? View.GONE : View.VISIBLE);
            mLayoutProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLayoutProgress.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mLayoutProgress.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showLoginView() {
        Snackbar sn = Utils.SnackBarView(ResetPasswordActivity.this,
                coordinatorLayout, "Updated new password...",
                ALERTCONSTANT.WARNING);
        sn.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event == DISMISS_EVENT_TIMEOUT) {
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    //send Request : to reset for password reset
    public class SendResetLinkAync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String email;
        private DataModel authResponse = new DataModel();
        private boolean retVal = false;

        public SendResetLinkAync(Context context, String email) {
            this.context = context;
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.e(TAG, "deviceId : " + deviceId);
            Log.e(TAG, "deviceName : " + deviceName);
            Log.e(TAG, "deviceToken : " + deviceToken);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("deviceName", deviceName);
                jsonObject.put("deviceTokenId", deviceToken);
                jsonObject.put("isApp", "true");
                jsonObject.put("signUp", "false");
                jsonObject.put("forgotPassword", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpClient client = new OkHttpClient();
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(mUrl + "forgot-password")
                    .post(formBody)
                    .build();
            Log.d(TAG, "SH : URL " + mUrl + "forgot-password");
            Log.d(TAG, "SH : email  " + email);
            retVal = false;
            try {
                Response response = client.newCall(request).execute();
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
                    retVal = true;
                } else {
                    retVal = false;
                }

            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at ResetPswFragment SendResetLink: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at ResetPswFragment SendRestLink: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            sendResetLinkAync = null;
            showProgress(false);
            if (success) {
                // mCardView_Two.setVisibility(View.VISIBLE);
                mSendOtpButton.setVisibility(View.VISIBLE);
                mEditOTP.setVisibility(View.VISIBLE);
                mBtnSendReSetLink.setVisibility(View.INVISIBLE);
                Utils.SnackBarView(ResetPasswordActivity.this,
                        coordinatorLayout, authResponse.getMessage(),
                        ALERTCONSTANT.SUCCESS);
            } else {
                mCardView_Two.setVisibility(View.INVISIBLE);
                mEditEmail.setError((authResponse.getMessage()));
                mEditEmail.requestFocus();
                Utils.SnackBarView(ResetPasswordActivity.this,
                        coordinatorLayout, authResponse.getMessage(),
                        ALERTCONSTANT.WARNING);
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            sendResetLinkAync = null;
            showProgress(false);
            super.onCancelled(aBoolean);
        }
    }

    //Send email and OTP for verification
    public class VerifyEmailViaOtpAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String email;
        private String otp;
        private DataModel authResponse = new DataModel();
        private boolean retVal = false;

        public VerifyEmailViaOtpAsync(Context context, String email, String otp) {
            this.context = context;
            this.email = email;
            this.otp = otp;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.e(TAG, "deviceId : " + deviceId);
            Log.e(TAG, "deviceName : " + deviceName);
            Log.e(TAG, "deviceToken : " + deviceToken);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
                jsonObject.put("OTP", otp);
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("deviceName", deviceName);
                jsonObject.put("deviceTokenId", deviceToken);
                jsonObject.put("isApp", "true");
                jsonObject.put("signUp", "false");
                jsonObject.put("forgotPassword", "false");
                jsonObject.put("isPassword", "false");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "forgot-password")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "forgot-password");
            Log.d(TAG, "SH : email  " + email);

            retVal = false;
            try {
                Response response = client.newCall(request).execute();
                String authResponseStr = response.body().string();
                Log.d(TAG, "SH : authResponseStr  " + authResponseStr);

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
                    retVal = true;
                } else {
                    retVal = false;
                }

            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at ResetPswFragment :verifyEmailViaOtpAsync: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at ResetPswFragment :verifyEmailViaOtpAsync " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            verifyEmailViaOtpAsync = null;
            showProgress(false);
            if (success) {
                mSendOtpButton.setVisibility(View.VISIBLE);
                mEditOTP.setVisibility(View.VISIBLE);
                mBtnSendReSetLink.setVisibility(View.INVISIBLE);
                mCardView_Two.setVisibility(View.VISIBLE);
                Utils.SnackBarView(ResetPasswordActivity.this,
                        coordinatorLayout, authResponse.getMessage(),
                        ALERTCONSTANT.SUCCESS);

            } else {
                Utils.SnackBarView(ResetPasswordActivity.this,
                        coordinatorLayout, authResponse.getMessage(),
                        ALERTCONSTANT.WARNING);
            }
        }

        @Override
        protected void onCancelled() {
            verifyEmailViaOtpAsync = null;
            showProgress(false);
            super.onCancelled();
        }
    }

    //Reset password and add new password
    public class UpdateNewPasswordAsync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String email;
        private String password;
        private DataModel authResponse = new DataModel();
        private boolean retVal = false;

        public UpdateNewPasswordAsync(Context context, String email, String password) {
            this.context = context;
            this.email = email;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            Log.e(TAG, "deviceId : " + deviceId);
            Log.e(TAG, "deviceName : " + deviceName);
            Log.e(TAG, "deviceToken : " + deviceToken);

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
                jsonObject.put("password", password);
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("deviceName", deviceName);
                jsonObject.put("deviceTokenId", deviceToken);
                jsonObject.put("isApp", "true");
                jsonObject.put("signUp", "false");
                jsonObject.put("forgotPassword", "false");
                jsonObject.put("isPassword", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "forgot-password")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "forgot-password");
            Log.d(TAG, "SH : email  " + email);

            retVal = false;
            try {
                Response response = client.newCall(request).execute();
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
                    retVal = true;
                } else {
                    retVal = false;
                }

            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at ResetPasswordActivity :updateNewPasswordAsync: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at ResetPasswordActivity :updateNewPasswordAsync " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            updateNewPasswordAsync = null;
            showProgress(false);
            if (success) {
                //  showLoginView();
                Utils.SnackBarView(ResetPasswordActivity.this,
                        coordinatorLayout, authResponse.getMessage(),
                        ALERTCONSTANT.SUCCESS);

                //Call login activity
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();

            } else {
                Utils.SnackBarView(ResetPasswordActivity.this,
                        coordinatorLayout, authResponse.getMessage(),
                        ALERTCONSTANT.WARNING);
            }
        }

        @Override
        protected void onCancelled() {
            updateNewPasswordAsync = null;
            showProgress(false);
            super.onCancelled();
        }
    }
}
