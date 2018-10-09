package com.example.sharan.iotsmartchain.dashboard.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;

public class FeedBackActivity extends BaseActivity {
    private static String TAG = FeedBackActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edittext_email)
    EditText mEditTextEmail;
    @BindView(R.id.edittext_feedback)
    EditText mEditTextFeedBack;
    @BindView(R.id.button_submit)
    Button mButtonSubmit;
    @BindView(R.id.constrainlayout_feedback)
    ConstraintLayout constraintLayout;

    private FeedBackAsync feedBackAsync = null;
    private String feedBack;
    private String mEmail, mToken, mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        injectViews();
        setupToolbar();

        feedBack = mEditTextFeedBack.getText().toString();

        mUrl = App.getAppComponent().getApiServiceUrl();
        mEmail = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        mToken = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

        if (mEmail != null) mEditTextEmail.setText(mEmail);

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Send your commits to server */
                if (!feedBack.isEmpty()) {
                    feedBackAsync = new FeedBackAsync(FeedBackActivity.this, feedBack);
                    feedBackAsync.execute((Void) null);
                } else {
                    Snackbar.make(constraintLayout,
                            "Write your comments and it should not be a empty!",
                            Snackbar.LENGTH_SHORT).show();
                }
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
                finish();
                break;
            case R.id.menu_refresh:
                //TODO call API to get updated Analytics status
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
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
        finish();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("FeedBack");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public class FeedBackAsync extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String feedbackStr;

        public FeedBackAsync(Context context, String feedbackStr) {
            this.context = context;
            this.feedbackStr = feedbackStr;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean retVal = false;
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("email", mEmail)
                        .add("tokenid", mToken)
                        .add("feedBack", feedBack)
                        .build();
                Request request = new Request.Builder()
                        .url(mUrl + "/sendFeedBack")
                        .post(formBody)
                        .build();
                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    if (response.code() != 200) {
                        retVal = false;
                    } else {
                        retVal = true;
                        String authResponseStr = response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(authResponseStr);

                            String respStatus = (String) jsonObject.get("status");

                            Log.d(TAG, "SH :: " + respStatus);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (ClassCastException ex) {
                ex.printStackTrace();
            } catch (NullPointerException npex) {
                npex.printStackTrace();
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {

            } else {
                feedBackAsync = null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            feedBackAsync = null;
        }
    }
}
