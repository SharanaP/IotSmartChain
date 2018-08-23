package com.example.sharan.iotsmartchain.loginModule.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.SMS.OnSmsCatchListener;
import com.example.sharan.iotsmartchain.SMS.SmsVerifyCatcher;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
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

public class MobEmailValidationActivity extends BaseActivity {
    private static String TAG = MobEmailValidationActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.cardView_sms_verify)
    CardView mCardViewSms;
    @BindView(R.id.cardView_email_verify)
    CardView mCardViewEmail;
    @BindView(R.id.editText_via_sms)
    EditText mEditSMS;
    @BindView(R.id.editText_via_email)
    EditText mEditEmail;
    @BindView(R.id.button_submit_smsOTP)
    Button mSmsOtpButton;
    @BindView(R.id.button_submit_emailOTP)
    Button mEmailOtpButton;
    @BindView(R.id.button_resend_smsOTP)
    Button mResendSmsButton;
    @BindView(R.id.button_resend_emailOTP)
    Button mResendEmailButton;
    @BindView(R.id.imageView_email_status)
    ImageView mImageViewEmailStatus;
    @BindView(R.id.imageView_sms_status)
    ImageView mImageViewSmsStatus;
    @BindView(R.id.button_done_verify)
    Button mButtonDoneVerify;
    @BindView(R.id.verify_progress)
    ProgressBar progressBar;
    @BindView(R.id.relativeLayout_view)
    View mView;
    private SmsVerifyCatcher smsVerifyCatcher;
    private VerifyAccViaOtpAsync verifyAccViaOtpAsync = null;
    private String mUrl, deviceId = "";
    private String mOTP = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_mob_verification);
        injectViews();
        setUpToolBar();

        /*get url and device ID*/
        mUrl = App.getAppComponent().getApiServiceUrl();
        deviceId = Utils.getDeviceId(getApplicationContext());

        //init SmsVerifyCatcher
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                mEditSMS.setText(code);//set code in edit text

                //then you can send verification code to server
            }
        });

        //set phone number filter if needed
        smsVerifyCatcher.setPhoneNumberFilter("Notice");
        //smsVerifyCatcher.setFilter("Verification code:");

        //Default email card view invisible
        mCardViewEmail.setVisibility(View.INVISIBLE);
        mButtonDoneVerify.setVisibility(View.INVISIBLE);

        /*TODO via SMS otp :: then you can send verification code to server */
        mSmsOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Set OTP and send to server verify mobil num */
                mOTP = mEditSMS.getText().toString();

                showProgress(true);
                verifyAccViaOtpAsync = new VerifyAccViaOtpAsync(MobEmailValidationActivity.this,
                        false, true, mOTP);
                verifyAccViaOtpAsync.execute((Void) null);
            }
        });

        /*TODO via email otp :: then you can send verification code to server*/
        mEmailOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Set OTP and send to server verify email*/
                mOTP = mEditEmail.getText().toString();

                showProgress(true);
                verifyAccViaOtpAsync = new VerifyAccViaOtpAsync(MobEmailValidationActivity.this,
                        true, false, mOTP);
                verifyAccViaOtpAsync.execute((Void) null);
            }
        });

        /*Resend a OTP via mobile */
        mResendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO requesting to server for new or regenerate a OTP via mobile based */
            }
        });

        /*Resend a OTP via email */
        mResendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO requesting to server for new OTP via email based*/
            }
        });

        /* login screen*/
        mButtonDoneVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO goto next screen like login */
                Intent intentLogIn = new Intent(MobEmailValidationActivity.this, LoginActivity.class);
                intentLogIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentLogIn);
                finish();
            }
        });

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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MobEmailValidationActivity.this.finish();
    }

    private void setUpToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("Validation and Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
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

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /*API : This api connect to server send OTP via sms or email*/
    public class VerifyAccViaOtpAsync extends AsyncTask<Void, String, String> {
        private Context context;
        private boolean isEmail = false;
        private boolean isMobile = false;
        private String mOTP = "";
        private String retVal = "false";
        private String respMessage = "";

        public VerifyAccViaOtpAsync(Context context, boolean isEmail, boolean isMobile, String mOTP) {
            this.context = context;
            this.isEmail = isEmail;
            this.isMobile = isMobile;
            this.mOTP = mOTP;
        }

        @Override
        protected String doInBackground(Void... voids) {

            // create your json here
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("isEmail", isEmail);
                jsonObject.put("isMobile", isMobile);
                jsonObject.put("OTP", mOTP);
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("isApp", "true");
                jsonObject.put("signUp", "true");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(mUrl + "mobile-email-otp-verification")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL " + mUrl + "mobile-email-otp-verification");
            Log.d(TAG, "SH : formBody  " + formBody.toString());
            Log.d(TAG, "SH : request " + request.getClass().toString());

            try {
                Response response = client.newCall(request).execute();
                Log.e(TAG, "" + response.toString());


                String authResponseStr = response.body().string();
                Log.e(TAG, "authResponseStr :: " + authResponseStr);

                //Json object
                try {
                    JSONObject TestJson = new JSONObject(authResponseStr);

                    Log.e(TAG, "authResponse :: " + TestJson.toString());
                    Log.e(TAG, "authResponse :: " + TestJson.getString("body").toString());

                    String strData = TestJson.getString("body").toString();
                    Log.e(TAG, "strData :: " + strData.toString());

                    JSONObject respData = new JSONObject(strData);
                    /*{"message":"Mobile OTP Confirm","status":"true"}*/

                    Log.e(TAG, respData.getString("message"));
                    Log.e(TAG, respData.getString("status"));

                    retVal = respData.getString("status");
                    respMessage = respData.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at Verify email and mobile number: " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at verify email and mobile number: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String success) {
            super.onPostExecute(success);

            if (success.equalsIgnoreCase("true")) {
                /*Email based*/
                if (isEmail) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mImageViewEmailStatus.setBackground(getResources().getDrawable(R.drawable.ic_check_green_24dp));
                    }
                    mImageViewEmailStatus.setVisibility(View.VISIBLE);
                }

                /*mobile based */
                if (isMobile) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mImageViewSmsStatus.setBackground(getResources().getDrawable(R.drawable.ic_check_green_24dp));
                    }
                    mImageViewSmsStatus.setVisibility(View.VISIBLE);
                    mCardViewEmail.setVisibility(View.VISIBLE);
                }

                mButtonDoneVerify.setVisibility(View.VISIBLE);
                Snackbar snackbar = Snackbar.make(mView, respMessage+" Valid information & Thank you", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(getResources().getColor(R.color.color_yellow));
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(mView, "Not valid information ", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(getResources().getColor(R.color.color_yellow));
                snackbar.show();
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            verifyAccViaOtpAsync = null;
            super.onCancelled();
        }
    }

}