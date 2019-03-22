package com.example.sharan.iotsmartchain.main.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.Presenter.MainActivityPresenter;
import com.example.sharan.iotsmartchain.R;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private MainActivityPresenter mPresenter;
    private ProgressDialog mProgress;

    @Inject
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");
        injectViews();
        App.inject(this);
        mPresenter = App.getMainActivityComponent().getMainActivityPresenter();
        Log.d(TAG, "Attempt connecting...");
        String tokenStr = App.getSharedPrefsComponent()
                .getSharedPrefs()
                .getString("TOKEN", "");
        Log.d(TAG, "token : " + tokenStr);
        if (!tokenStr.isEmpty()) {
            //TODO showLoadingDialog();
            /*show App home Activity */
            mPresenter.launchRegisterIotActivity();
//            mPresenter.launchDashBoardActivity();
        } else {
            //show login activity
            mPresenter.launchLoginActivity();
//            mPresenter.launchDashBoardActivity();
        }
    }

    public void showFlowView() {
        dismissLoadingDialog();
        mPresenter.launchFlowView();
        finish();
    }

    public void showLoadingDialog() {
        if (mProgress == null) {
            mProgress = new ProgressDialog(this);
        }
        mProgress.setCancelable(true);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.setTitle("Connecting");
        mProgress.setMessage("Trying to connect to the server...");
        mProgress.show();
    }

    public void dismissLoadingDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.cancel();
        }
    }

}