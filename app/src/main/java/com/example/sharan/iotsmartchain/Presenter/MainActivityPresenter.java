package com.example.sharan.iotsmartchain.Presenter;

import android.content.Intent;
import android.util.Log;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.dashboard.activity.DashBoardActivity;
import com.example.sharan.iotsmartchain.NormalFlow.activities.HomeActivity;
import com.example.sharan.iotsmartchain.loginModule.activities.LoginActivity;
import com.example.sharan.iotsmartchain.main.activities.MainActivity;
import com.example.sharan.iotsmartchain.newDesignTiTo.AddBridgeActivity;

import javax.inject.Inject;

/**
 * Created by Sharan on 20-03-2018.
 */

public class MainActivityPresenter implements ActivityPresenterBase {
    private static String TAG = MainActivityPresenter.class.getSimpleName();
    MainActivity mMainActivity;

    @Inject
    public MainActivityPresenter(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
        App.getMainActivityComponent().inject(mainActivity);
    }

    @Override
    public void pause() {
        Log.e(TAG, "Pause");
    }

    @Override
    public void resume() {
        Log.e(TAG, "resume");
    }

    @Override
    public void destroy() {
        Log.e(TAG, "destroy");
    }

    public void onApiDataLoadedEvent() {
        mMainActivity.showFlowView();
    }

    public void launchFlowView() {
        /*Launch home screen */
        Intent homeActivityIntent = new Intent(this.mMainActivity, HomeActivity.class);
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
        //TODO Launch RegIoTDeviceActivity
       /* Intent regIotActivityIntent = new Intent(this.mMainActivity,
                RegisterIoTDeviceActivity.class);
        regIotActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mMainActivity.startActivity(regIotActivityIntent);
        mMainActivity.finish();*/

        Intent regIotActivityIntent = new Intent(this.mMainActivity,
                AddBridgeActivity.class);
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
