package com.example.sharan.iotsmartchain.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.BusProvider;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.fragments.SplashFragment;
import com.squareup.otto.Subscribe;

/**
 * Created by Sharan on 19-03-2018.
 */
public class SplashActivity extends BaseActivity {
    private static String TAG = "SplashActivity";
    private boolean exitEventReceived;

    @Nullable
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate()");
        injectViews();
        /*Splash Fragment */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fragment);
        Utils.replaceFragment(this, new SplashFragment(), R.id.fragment_container, false);
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BusProvider.getInstance().unregister(this);
        SplashActivity.this.finish();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSplashExit(final SplashFragment.SplashEvent event) {
        if (exitEventReceived)
            return;
        exitEventReceived = true;
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
