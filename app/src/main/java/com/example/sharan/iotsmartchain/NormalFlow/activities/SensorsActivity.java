package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import butterknife.BindView;

/**
 * Created by Sharan on 26-03-2018.
 */

public class SensorsActivity extends BaseActivity {

    private static String TAG = "SensorsActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.temperatureView)
    RelativeLayout mRelLayTempView;

    @BindView(R.id.temperatureImageEmpty)
    ImageView mTempEmptyImage;

    /* @Bind(R.id.temperatureChart)
     LineChartView mLineChartTempView;*/

    @BindView(R.id.temperatureImageFull)
    ImageView mTempFullImage;

    @BindView(R.id.temperatureLabel)
    TextView mTextTempStatus;

//    @Bind(R.id.compassShape)
//    GLSurfaceView mCompassGLSurfaceView;

    @BindView(R.id.compassShape)
    ImageView mCompassView;

    @BindView(R.id.compassView)
    RelativeLayout mRelLayCompassView;

    @BindView(R.id.accelerometerIcon)
    ImageView mImageAccelerometer;

    @BindView(R.id.humidityImageFull)
    ImageView mImageHumidity;

    @BindView(R.id.gyroscopeShape)
    ImageView mImageGyroScope;

//    @Bind(R.id.drawer_layout)
//    android.support.v4.widget.DrawerLayout mDrawerLayout;
//
//    @Bind(R.id.imagebtn)
//    ImageView mImageLeftIcon;
//
//    @Bind(R.id.imagebtnalias)
//    ImageButton mDrawerSliderAlias_right;

    private float mDrawerOffset = 0.0f;
    private boolean mDrawerState = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reports);

        injectViews();

        //Set navigation bar color
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigation_bar_background));
        }

        //setup toolbar
        setupToolbar();

        //TODO add left drawable fragment
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        ProfileSettingsFragment profileSettingsFragment = new ProfileSettingsFragment();
//        fragmentTransaction.replace(R.id.rightDrawer, profileSettingsFragment);
//        fragmentTransaction.commit();

//        Utils.replaceFragment(this, new ProfileSettingsFragment(), R.id.rightDrawer, false);

//        DrawerLayout.SimpleDrawerListener simpleDrawerListener = new DrawerLayout.SimpleDrawerListener() {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                mDrawerOffset = slideOffset;
//                super.onDrawerSlide(drawerView, slideOffset);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                mDrawerState = true;
//                mDrawerSliderAlias_right.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                mDrawerState = false;
//                mDrawerSliderAlias_right.setVisibility(View.VISIBLE);
//            }
//        };
//
//        mDrawerLayout.setDrawerListener(simpleDrawerListener);
//
//        mImageLeftIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
//            }
//        });
//
//        mDrawerSliderAlias_right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawerSliderAlias_right.setVisibility(View.INVISIBLE);
//                mDrawerLayout.openDrawer(Gravity.LEFT);
//            }
//

        //Animation

        Animation animComp = AnimationUtils.loadAnimation(this, R.anim.shake);
        mCompassView.setAnimation(animComp);

        Animation animAcc = AnimationUtils.loadAnimation(this, R.anim.shake);
        mImageAccelerometer.setAnimation(animAcc);

        Animation animGyr = AnimationUtils.loadAnimation(this, R.anim.shake);
        mImageGyroScope.setAnimation(animGyr);

        //Temperature sensor
        mRelLayTempView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SensorsActivity.this, SensorReportActivity.class);
                intent.putExtra("SENSOR", "Temperature");
                intent.putExtra("RESULT", " 28 "+"\u2103");
                startActivity(intent);
             }
        });

    }

    @Override
    public void onBackPressed() {
//        if (!mDrawerState ) {
//            super.onBackPressed();
//            SensorsActivity.this.finish();
//        } else {
//            mDrawerLayout.closeDrawer(Gravity.LEFT);
//        }
        super.onBackPressed();
        SensorsActivity.this.finish();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Sensors");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sensor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}