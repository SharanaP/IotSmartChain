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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.NormalFlow.activities.RegisterIoTDeviceActivity;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.DashBoardActivity;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import butterknife.BindView;

public class OtpLoginActivity extends BaseActivity {
    private static String TAG = OtpLoginActivity.class.getSimpleName();

    @BindView(R.id.textView_title)TextView textViewTitle;
    @BindView(R.id.editText_enter_mobile)EditText mEditTextMobile;
    @BindView(R.id.editText_enter_otp)EditText mEditTextOTP;
    @BindView(R.id.textView_Status)TextView mTextViewStatus;
    @BindView(R.id.button_RegenerateOTP)Button mResendOtpButton;
    @BindView(R.id.button_submitOTP)Button mSubmitOtpButton;
    @BindView(R.id.login_progress)ProgressBar mProgressView;
    @BindView(R.id.relativeLayout_login_form)View mView;

    private UserLoginOTPAsync userLoginOTPAsync = null;
    private String deviceId, mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_otp_login);
        injectViews();

        //get url and device id
        mUrl = App.getAppComponent().getApiServiceUrl();
        deviceId = Utils.getDeviceId(OtpLoginActivity.this);

        mResendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Regenerate OTP number
            }
        });

        mSubmitOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO submit otp number via mobile
                showProgress(true);
                userLoginOTPAsync = new UserLoginOTPAsync(OtpLoginActivity.this,
                        mEditTextMobile.getText().toString(), mEditTextOTP.getText().toString());
                userLoginOTPAsync.execute((Void)null);
            }
        });
    }

    private void DashBoardScreen() {
        Intent homeIntent = new Intent(OtpLoginActivity.this, DashBoardActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void RegisterIoTScreen() {
        Intent intent = new Intent(OtpLoginActivity.this, RegisterIoTDeviceActivity.class);
        startActivity(intent);
        finish();
    }

    /*TODO Server API : To request server for login through a mobile OTP*/
    public class UserLoginOTPAsync extends AsyncTask<Void, String, Boolean>{

        private Context context;
        private String mobile;
        private String otp;
        private boolean retVal = false;

        public UserLoginOTPAsync(Context context, String mobile, String otp) {
            this.context = context;
            this.mobile = mobile;
            this.otp = otp;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showProgress(false);
        }
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
}


