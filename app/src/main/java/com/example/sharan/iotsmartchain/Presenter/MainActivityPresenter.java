package com.example.sharan.iotsmartchain.Presenter;

import android.content.Intent;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.dashboard.activity.AnalyticsActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.BatteryStatusActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.DashBoardActivity;
import com.example.sharan.iotsmartchain.NormalFlow.activities.HomeActivity;
import com.example.sharan.iotsmartchain.NormalFlow.activities.RegisterIoTDeviceActivity;
import com.example.sharan.iotsmartchain.dashboard.activity.MasterLockActivity;
import com.example.sharan.iotsmartchain.loginModule.activities.LoginActivity;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;
import com.example.sharan.iotsmartchain.main.activities.MainActivity;

import javax.inject.Inject;

/**
 * Created by Sharan on 20-03-2018.
 */

public class MainActivityPresenter implements ActivityPresenterBase {

    MainActivity mMainActivity;

    @Inject
    public MainActivityPresenter(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
        App.getMainActivityComponent().inject(mainActivity);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void destroy() {

    }

    public void onApiDataLoadedEvent() {
        mMainActivity.showFlowView();
    }

    public void launchFlowView() {
        /*Launch home screen */
        Intent homeActivityIntent = new Intent(mMainActivity.getApplicationContext(), HomeActivity.class);
        homeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mMainActivity.startActivity(homeActivityIntent);
        mMainActivity.finish();
    }

    public void launchLoginActivity() {
        //Launch LoginView
        Intent loginActivityIntent = new Intent(mMainActivity.getApplicationContext(),
                LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mMainActivity.startActivity(loginActivityIntent);
        mMainActivity.finish();
    }

    public void launchRegisterIotActivity(){
        //Launch RegIoTDeviceActivity
        Intent regIotActivityIntent = new Intent(mMainActivity.getApplicationContext(),
                DashBoardActivity.class);
        regIotActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mMainActivity.startActivity(regIotActivityIntent);
        mMainActivity.finish();
    }

    public void launchDashBoardActivity(){
        //Launch DashBoardActivity
        Intent dashBoardActivityIntent = new Intent(mMainActivity.getApplicationContext(),
                DashBoardActivity.class);
        dashBoardActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mMainActivity.startActivity(dashBoardActivityIntent);
        mMainActivity.finish();
    }

}
