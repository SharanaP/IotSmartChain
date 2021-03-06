package com.example.sharan.iotsmartchain.loginModule.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.model.DataModel;
import com.example.sharan.iotsmartchain.model.LoginResultType;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Sharan on 02-04-2018.
 */

public class RegisterActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = "RegisterActivity";

    @BindView(R.id.view_firstName) EditText viewFirstName;
    @BindView(R.id.view_lastName) EditText viewLastName;
    @BindView(R.id.view_email) EditText viewEmailID;
    @BindView(R.id.view_phone_number) EditText viewPhoneNum;
    @BindView(R.id.view_password_one) EditText viewFstPsw;
    @BindView(R.id.view_password_second) EditText viewSecPsw;
    @BindView(R.id.button_register) Button buttonReg;
    @BindView(R.id.login_progress_reg) View mProgressView;
    @BindView(R.id.login_form_reg) View mLoginFormView;
    @BindView(R.id.ic_showPassword) ImageView mImagePswShow;
    @BindView(R.id.ic_reEnter_showPassword) ImageView mImageReEnterPswShow;


    private String mFName, mLName, mEmailId, mPhoneNum, mPsw1st, mPsw2nd;
    private boolean mStatus = false;
    private String mUrl;
    private UserSignUpAsync mSignUpTask = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_register);
        setContentView(R.layout.activity_signup_screen);


        injectViews();

        mUrl = App.getAppComponent().getApiServiceUrl();

        viewFirstName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == R.id.fname || actionId == EditorInfo.IME_NULL) {
                validateInputData();
                return true;
            }
            return false;
        });

        viewLastName.setOnEditorActionListener((TextView textView, int id, KeyEvent keyEvent) -> {
            if (id == R.id.lname || id == EditorInfo.IME_NULL) {
                validateInputData();
                return true;
            }
            return false;
        });

        viewEmailID.setOnEditorActionListener((TextView textView, int id, KeyEvent keyEvent) -> {
            if (id == R.id.email || id == EditorInfo.IME_NULL) {
                validateInputData();
                return true;
            }
            return false;
        });

        viewPhoneNum.setOnEditorActionListener((TextView textView, int id, KeyEvent keyEvent) -> {
            if (id == R.id.phonenumber || id == EditorInfo.IME_NULL) {
                validateInputData();
                return true;
            }
            return false;
        });

        viewFstPsw.setOnEditorActionListener((TextView textView, int id, KeyEvent keyEvent) -> {
            if (id == R.id.psw1 || id == EditorInfo.IME_NULL) {
                validateInputData();
                return true;
            }
            return false;
        });

        mImagePswShow.setOnTouchListener(mViewPsw);

        viewSecPsw.setOnEditorActionListener((TextView textView, int id, KeyEvent keyEvent) -> {
            if (id == R.id.psw2 || id == EditorInfo.IME_NULL) {
                validateInputData();
                return true;
            }
            return false;
        });

        mImageReEnterPswShow.setOnTouchListener(mViewReEnterPsw);

        buttonReg.setOnClickListener(v -> validateInputData());
    }

    /*Validate all inputs */
    private void validateInputData() {

        if(mSignUpTask != null){
            return;
        }

        viewFirstName.setError(null);
        viewLastName.setError(null);
        viewEmailID.setError(null);
        viewPhoneNum.setError(null);
        viewFstPsw.setError(null);
        viewSecPsw.setError(null);

        mFName = viewFirstName.getText().toString();
        mLName = viewLastName.getText().toString();
        mEmailId = viewEmailID.getText().toString();
        mPhoneNum = viewPhoneNum.getText().toString();
        mPsw1st = viewFstPsw.getText().toString();
        mPsw2nd = viewSecPsw.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mFName)) {
            viewFirstName.setError(getString(R.string.error_field_required));
            focusView = viewFirstName;
            cancel = true;
        }

        if(TextUtils.isEmpty(mLName)){
            viewLastName.setError(getString(R.string.error_field_required));
            focusView = viewLastName;
            cancel = true;
        }

        if(TextUtils.isEmpty(mEmailId)){
            viewEmailID.setError(getString(R.string.error_field_required));
            focusView = viewEmailID;
            cancel = true;
        }

        if(!isEmailValid(mEmailId)){
            viewEmailID.setError(getString(R.string.error_invalid_email));
            focusView = viewEmailID;
            cancel = true;
        }

        if(TextUtils.isEmpty(mPhoneNum)){
            viewPhoneNum.setError(getString(R.string.error_field_required));
            focusView = viewPhoneNum;
            cancel = true;
        }

        if(isValidMobile(mPhoneNum)){
        }else{
            viewPhoneNum.setError(getString(R.string.error_invalid_phone_number));
            focusView = viewPhoneNum;
            cancel = true;
        }

        if(TextUtils.isEmpty(mPsw1st)){
            viewFstPsw.setError(getString(R.string.error_invalid_password));
            focusView = viewFstPsw;
            cancel = true;
        }

        if( !isPasswordValid(mPsw1st)){
            viewFstPsw.setError(getString(R.string.error_invalid_password));
            focusView = viewFstPsw;
            cancel = true;
        }

        if(TextUtils.isEmpty(mPsw2nd)){
            viewSecPsw.setError(getString(R.string.error_invalid_password));
            focusView = viewSecPsw;
            cancel = true;
        }

        if(!isPasswordValid(mPsw2nd)){
            viewSecPsw.setError(getString(R.string.error_invalid_password));
            focusView = viewSecPsw;
            cancel = true;
        }

        if(isConfirmPasswordValid(mPsw1st, mPsw2nd));
        else{
            viewSecPsw.setError(getString(R.string.error_confirm_invalid_password));
            focusView = viewSecPsw;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            //TODO check signup API
            showProgress(true);
            mSignUpTask = new UserSignUpAsync(mFName, mLName, mEmailId, mPhoneNum,
                            mPsw1st, RegisterActivity.this);
            mSignUpTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private boolean isConfirmPasswordValid(String password, String confirm_password) {
        return password.equals(confirm_password);
    }

    private boolean isValidMobile(String phone) {
        if(TextUtils.isEmpty(phone)) return false;
        else{
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    private View.OnTouchListener mViewPsw = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            final boolean isOutsideView = event.getX() < 0 ||
                    event.getX() > v.getWidth() ||
                    event.getY() < 0 ||
                    event.getY() > v.getHeight();

            // change input type will reset cursor position, so we want to save it
            final int cursor = viewFstPsw.getSelectionStart();

            if (isOutsideView || MotionEvent.ACTION_UP == event.getAction())
                viewFstPsw.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
            else
                viewFstPsw.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            viewFstPsw.setSelection(cursor);

            return true;
        }
    };

    private View.OnTouchListener mViewReEnterPsw = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final boolean isOutsideView = event.getX() < 0 ||
                    event.getX() > v.getWidth() ||
                    event.getY() < 0 ||
                    event.getY() > v.getHeight();

            // change input type will reset cursor position, so we want to save it
            final int cursor = viewSecPsw.getSelectionStart();

            if (isOutsideView || MotionEvent.ACTION_UP == event.getAction())
                viewSecPsw.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
            else
                viewSecPsw.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            viewSecPsw.setSelection(cursor);

            return true;
        }
    };

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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

       //TODO viewEmailID.setAdapter(adapter);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /*Represents an Asynchronous SingUp/Registration new user */
    public class UserSignUpAsync extends AsyncTask<Void, Void, Boolean> {
        private String mFirstName = "";
        private String mLastName = "";
        private String mEmail = "";
        private String mPhoneNum = "";
        private String mPassword = "";
        private Context mContext;
        private LoginResultType mLoginResultType;

        public UserSignUpAsync(String mFirstName, String mLastName, String mEmail, String mPhoneNum,
                               String mPassword, Context mContext) {
            this.mFirstName = mFirstName;
            this.mLastName = mLastName;
            this.mEmail = mEmail;
            this.mPhoneNum = mPhoneNum;
            this.mPassword = mPassword;
            this.mContext = mContext;
            mLoginResultType = LoginResultType.LOGIN_SERVER_ERROR;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormEncodingBuilder()
                    .add("fname", mFirstName)
                    .add("lname", mLastName)
                    .add("email", mEmail)
                    .add("phone", mPhoneNum)
                    .add("password", mPassword)
                    .add("isApp", "true")
                    .add("signup", "true")
                    .build();
            Request request = new Request.Builder()
                    .url(mUrl + "register")
                    .post(formBody)
                    .build();

            Log.d(TAG, "SH : URL "+mUrl);
            Log.d(TAG, "SH : formBody  "+formBody.toString());
            Log.d(TAG, "SH : request "+request.getClass().toString());


            boolean retVal = false;
            try {
                Response response = client.newCall(request).execute();
                if(response.code() != 200){
                    mLoginResultType = LoginResultType.LOGIN_FAILED;
                    retVal = false;
                } else {
                    mLoginResultType = LoginResultType.LOGIN_SUCCESS;
                    retVal = true;

                    String authResponseStr = response.body().string();
                    DataModel authResponse = new GsonBuilder()
                            .create()
                            .fromJson(authResponseStr, DataModel.class);

                    String tokenStr = authResponse.getToken();

                    Log.d(TAG, "tokenStr : "+tokenStr);
                    Log.d(TAG, "email : "+authResponse.getEmailId());

                    if(!tokenStr.isEmpty()){
                        SharedPreferences.Editor
                                editor = App.getSharedPrefsComponent().getSharedPrefsEditor();
                        editor.putString("TOKEN", tokenStr);
                        editor.putString("AUTH_EMAIL_ID", authResponse.getEmailId());
                        editor.apply();
                        App.setLoginId(authResponse.getEmailId());
                        App.setTokenStr(tokenStr);

                    }else{
                        Log.d(TAG, "Invalid emial Id....!");
                    }
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at RegistrationActivity: " + e.getMessage());
            } catch (NullPointerException e1){
                Log.e("ERROR: ", "null pointer Exception at RegistrationActivity: " + e1.getMessage());
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSignUpTask = null;
            showProgress(false);
            if (success) {
                viewFirstName.setText("");
                viewLastName.setText("");
                viewEmailID.setText("");
                viewFstPsw.setText("");
                viewSecPsw.setText("");
                viewPhoneNum.setText("");
                showLoginView();

                //TODO register Device
//                if (checkPlayServices()) {
//                    startRegisterProcess();
//                }

                //TODO Goto Next screen


            } else {
                viewEmailID.setError(getString(R.string.email_not_in_contactbook));
                viewEmailID.requestFocus();

                Snackbar sEvents = Snackbar.make(mLoginFormView,
                            "Unable to reach the server - Try again later!",
                            Snackbar.LENGTH_SHORT);
                    sEvents.show();
            }
        }

        @Override
        protected void onCancelled() {
            mSignUpTask = null;
            showProgress(false);
            super.onCancelled();
        }
    }

    private void showLoginView(){
        Snackbar sn = Snackbar.make(findViewById(R.id.button_register),
                "Registration Success! ", Snackbar.LENGTH_LONG);
        sn.show();
        sn.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event == DISMISS_EVENT_TIMEOUT) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**/
}
