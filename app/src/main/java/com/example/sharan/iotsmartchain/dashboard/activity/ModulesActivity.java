package com.example.sharan.iotsmartchain.dashboard.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.fragments.NonSpatialFragment;
import com.example.sharan.iotsmartchain.dashboard.fragments.SpatialFragment;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import butterknife.BindView;

public class ModulesActivity extends BaseActivity {
    private static String TAG = ModulesActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.navigation_modules)
    BottomNavigationView bottomNavigationView;

    SpatialFragment spatialFragment;
    Fragment selectedFragment = null;
    int mSelectedItem;
    private String mUrl, loginId, token;

    private BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectedFragment = null;
            spatialFragment = null;
            mSelectedItem = item.getItemId();

            switch (item.getItemId()) {

                case R.id.navigation_spatial:
                    selectedFragment = SpatialFragment.newInstance();
                    spatialFragment = (SpatialFragment) selectedFragment;
                    break;

                case R.id.navigation_nonspatial:
                    selectedFragment = NonSpatialFragment.newInstance();
                    break;
            }

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_modules, selectedFragment);
            fragmentTransaction.commit();

            return true;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moduleslist);
        Log.d(TAG, "onCreate()");

        injectViews();

        setupToolBar();

        mUrl = App.getAppComponent().getApiServiceUrl();
        loginId = App.getSharedPrefsComponent().getSharedPrefs().getString("AUTH_EMAIL_ID", null);
        token = App.getSharedPrefsComponent().getSharedPrefs().getString("TOKEN", null);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation_modules);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //TODO    bottomNavigationView.getMenu().findItem(R.id.navigation_modules).setChecked(true);
        bottomNavigationView.getMenu().findItem(R.id.navigation_modules);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        // setupBottomNavigationView();

        // Manually Start default Fragment screen
        Fragment selectedFragment = SpatialFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_modules, SpatialFragment.newInstance());
        fragmentTransaction.commit();
    }


    private void setupBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.getMenu().findItem(R.id.navigation_modules).setChecked(true);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
    }

    private void setupToolBar() {
        setSupportActionBar(toolbar);
        setTitle("iSmartLink");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
