package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.sharan.iotsmartchain.NormalFlow.fragment.FloorPlanViewFragment;
import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.ALERTCONSTANT;
import com.example.sharan.iotsmartchain.global.Utils;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import butterknife.BindView;


public class WebViewActivity extends BaseActivity {
    private static String TAG = WebViewActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.view_message)
    View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        injectViews();
        setupToolbar();

        //call Fragment to display web view floor plan
        Fragment fragment = FloorPlanViewFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Create an Floor Plan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_battery_status, menu);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        menuRefresh.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_refresh:
                Utils.SnackBarView(WebViewActivity.this, mCoordinatorLayout, "Refresh", ALERTCONSTANT.INFO);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
