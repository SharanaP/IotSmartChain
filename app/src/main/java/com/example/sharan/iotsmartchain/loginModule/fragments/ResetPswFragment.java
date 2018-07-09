package com.example.sharan.iotsmartchain.loginModule.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.loginModule.activities.LoginActivity;
import com.example.sharan.iotsmartchain.model.DataModel;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class ResetPswFragment extends Fragment {
    private static String TAG = "ResetPswFragment";
    private Toolbar mToolBar;
    private CardView mCardView_One;
    private CardView mCardView_Two;
    private EditText mEditEmailMob;
    private EditText mEditPsw;
    private EditText mEditReEnterPsw;
    private Button mBtnSendReSetLink, mBtnSubmit;
    private LinearLayout linearLayoutMain;
    private View mLayoutProgress;
    private View mProgressBar;
    private ImageView mImageNewPswView;
    private ImageView mImageReEnterPswView;

    private String mUrl;
    private SendResetLinkAync sendResetLinkAync = null;
    private ResetNewPswAync resetNewPswAync = null;

    public ResetPswFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reset_psw, container, false);
        mToolBar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mCardView_One = (CardView) rootView.findViewById(R.id.cardView_one);
        mCardView_Two = (CardView) rootView.findViewById(R.id.cardView_two);
        mEditEmailMob = (EditText) rootView.findViewById(R.id.editText_email_mob);
        mEditPsw = (EditText) rootView.findViewById(R.id.editText_password);
        mEditReEnterPsw = (EditText) rootView.findViewById(R.id.editText_reenter_password);
        mBtnSendReSetLink = (Button) rootView.findViewById(R.id.button_send_reset_link);
        mBtnSubmit = (Button) rootView.findViewById(R.id.button_submit);
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearlayout_main);
        mLayoutProgress = (View) rootView.findViewById(R.id.relativeLayout_progress);
        mProgressBar = (View) rootView.findViewById(R.id.progressBar_reset);
        mImageNewPswView = (ImageView)rootView.findViewById(R.id.ic_newShowPassword);
        mImageReEnterPswView = (ImageView)rootView.findViewById(R.id.ic_reEnterShowPassword);

        mUrl = App.getAppComponent().getApiServiceUrl();

        //set toolbar
        setToolBar();

        //cardView two invisible
        mCardView_Two.setVisibility(View.INVISIBLE);

        //button Send reset link listener
        mBtnSendReSetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendResetPswRequest();
            }
        });

        //Set new login password button listener
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable functionality of send reset link button
                mBtnSendReSetLink.setEnabled(false);
                ValidatePassword();
            }
        });

        mImageNewPswView.setOnTouchListener(mNewPswListener);

        mImageReEnterPswView.setOnTouchListener(mReEnterPswListener);

        return rootView;
    }

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


    private void setToolBar() {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setTitle("Reset login Password");
        appCompatActivity.setSupportActionBar(mToolBar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.navigation_bar_background));
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

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

            String email = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
            String tokenId = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);
            Log.d(TAG, "email\n" + email + "token\n" + tokenId);
            //TODO async request for reset password
            showProgress(true);
            //Reset Password
            resetNewPswAync = new ResetNewPswAync(getActivity(),
                    mEditEmailMob.getText().toString(), tokenId, password);
            resetNewPswAync.execute((Void) null);
        }

    }

    private void SendResetPswRequest() {
        boolean cancel = false;
        View focusView = null;

        String email = mEditEmailMob.getText().toString();
        if (TextUtils.isEmpty(email) && !isEmailValid(email)) {
            mEditEmailMob.setError(getString(R.string.error_field_required));
            focusView = mEditEmailMob;
            cancel = true;
        } else {

            if (!isEmailValid(email)) {
                mEditEmailMob.setError(getString(R.string.error_invalid_email));
                focusView = mEditEmailMob;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //async request for reset password
            showProgress(true);
            sendResetLinkAync = new SendResetLinkAync(getActivity(), email);
            sendResetLinkAync.execute((Void) null);
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

    //send Request : to reset for password reset
    public class SendResetLinkAync extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String email;

        public SendResetLinkAync(Context context, String email) {
            this.context = context;
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            com.squareup.okhttp.OkHttpClient okHttpClient = new com.squareup.okhttp.OkHttpClient();

            com.squareup.okhttp.RequestBody requestBody = new FormEncodingBuilder()
                    .add("email", email)
                    .build();
            Request request = new Request.Builder()
                    .url(mUrl + "forgetpassword")
                    .post(requestBody)
                    .build();

            boolean retVal = false;
            try {

                Response response = okHttpClient.newCall(request).execute();

                if (response.code() != 200) {
                    retVal = false;
                } else {

                    String authResponseStr = response.body().string();
                    DataModel authResponse = new GsonBuilder().create().fromJson(authResponseStr, DataModel.class);
                    String tokenStr = authResponse.getToken();
                    String emailStr = authResponse.getEmailId();
                    String status = authResponse.getMessage();

                    Log.d(TAG, " tokenStr : " + tokenStr);
                    Log.d(TAG, " emailStr : " + emailStr);
                    Log.d(TAG, " status : " + status);


                    if (!tokenStr.isEmpty()) {
                        retVal = true;
                        SharedPreferences.Editor
                                editor = App.getSharedPrefsComponent().getSharedPrefsEditor();
                        editor.putString("TOKEN", tokenStr);
                        editor.putString("AUTH_EMAIL_ID", email);
                        editor.apply();
                        App.setLoginId(email);
                        App.setTokenStr(tokenStr);
                    } else {
                        retVal = false;
                        Log.d(TAG, "Invalid email....!");
                    }
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
                mCardView_Two.setVisibility(View.VISIBLE);
            } else {
                mCardView_Two.setVisibility(View.INVISIBLE);

                mEditEmailMob.setError(getString(R.string.email_not_in_contactbook));
                mEditEmailMob.requestFocus();
                Snackbar sEvents = Snackbar.make(linearLayoutMain,
                        "Unable to reach the server - Try again later!",
                        Snackbar.LENGTH_SHORT);
                sEvents.show();
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            sendResetLinkAync = null;
            showProgress(false);
            super.onCancelled(aBoolean);
        }
    }

    //Reset password and add new password
    public class ResetNewPswAync extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String email;
        private String token;
        private String password;

        public ResetNewPswAync(Context context, String email, String token, String password) {
            this.context = context;
            this.email = email;
            this.token = token;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            com.squareup.okhttp.OkHttpClient okHttpClient = new com.squareup.okhttp.OkHttpClient();
            com.squareup.okhttp.RequestBody requestBody = new FormEncodingBuilder()
                    .add("email", email)
                    .add("tokenid", token)
                    .add("password", password)
                    .build();
            Request request = new Request.Builder()
                    .url(mUrl + "resetpassword")
                    .post(requestBody)
                    .build();

          //  http://192.168.1.61:10000/IOTSensor/resetpassword
            Log.d(TAG, "SH : URL " + mUrl);

            boolean retVal = false;
            try {
                Response response = okHttpClient.newCall(request).execute();

                if (response.code() != 200) {
                    retVal = false;
                } else {

                    String authResponseStr = response.body().string();
                    DataModel authResponse = new GsonBuilder()
                            .create()
                            .fromJson(authResponseStr, DataModel.class);

                    String message = authResponse.getMessage();
                    Log.d(TAG, "message : "+message);
                    boolean status = authResponse.isStatus();
                    Log.d(TAG, "Status : "+status);
                    if (status) {
                        retVal = true;
                    } else {
                        retVal = false;
                        Log.d(TAG, "Failed Reset new password ....try again!");
                    }
                }
            } catch (IOException e) {
                Log.e("ERROR: ", "Exception at ResetPswFragment : ResetPswAync " + e.getMessage());
            } catch (NullPointerException e1) {
                Log.e("ERROR: ", "null pointer Exception at ResetPswFragment ResetPswAync: " + e1.getMessage());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            sendResetLinkAync = null;
            showProgress(false);
            if (success) {
                showLoginView();
            } else {
                Snackbar sEvents = Snackbar.make(linearLayoutMain,
                        "Unable to reach the server - Try again later!",
                        Snackbar.LENGTH_SHORT);
                sEvents.show();
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            showProgress(false);
            super.onCancelled(aBoolean);
        }
    }

    private void showLoginView(){
        Snackbar sn = Snackbar.make(linearLayoutMain,
                "ReSet New Password Success! ", Snackbar.LENGTH_LONG);
        sn.show();
        sn.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event == DISMISS_EVENT_TIMEOUT) {
                    //Go back login screen : try for login with new password
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }
}
