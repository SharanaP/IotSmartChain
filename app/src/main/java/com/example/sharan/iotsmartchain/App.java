package com.example.sharan.iotsmartchain;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.example.sharan.iotsmartchain.di.component.AppComponent;
import com.example.sharan.iotsmartchain.di.component.DaggerAppComponent;
import com.example.sharan.iotsmartchain.di.component.DaggerHomeActivityComponent;
import com.example.sharan.iotsmartchain.di.component.DaggerLoginActivityComponent;
import com.example.sharan.iotsmartchain.di.component.DaggerMainActivityComponent;
import com.example.sharan.iotsmartchain.di.component.DaggerSharedPrefsComponent;
import com.example.sharan.iotsmartchain.di.component.HomeActivityComponent;
import com.example.sharan.iotsmartchain.di.component.LoginActivityComponent;
import com.example.sharan.iotsmartchain.di.component.MainActivityComponent;
import com.example.sharan.iotsmartchain.di.component.SharedPrefsComponent;
import com.example.sharan.iotsmartchain.di.module.ApiServiceModule;
import com.example.sharan.iotsmartchain.di.module.AppModule;
import com.example.sharan.iotsmartchain.di.module.HomeActivityModule;
import com.example.sharan.iotsmartchain.di.module.LoginActivityModule;
import com.example.sharan.iotsmartchain.di.module.MainActivityModule;
import com.example.sharan.iotsmartchain.di.module.SharedPrefsModule;
import com.example.sharan.iotsmartchain.global.API_QUERY_TYPE;
import com.example.sharan.iotsmartchain.global.OnFinishedListener;
import com.example.sharan.iotsmartchain.NormalFlow.activities.HomeActivity;
import com.example.sharan.iotsmartchain.loginModule.activities.LoginActivity;
import com.example.sharan.iotsmartchain.main.activities.MainActivity;
import com.example.sharan.iotsmartchain.model.PersonLogInfo;

/**
 * Created by Sharan on 19-03-2018.
 */

public class App extends Application implements OnFinishedListener {

//   private final String API_SERVICE_URL = "http://192.168.1.59:10000/IOTSensor/";  //Local server

    private final String API_SERVICE_URL = "http://111.93.31.170/IOTSensor/"; //GeoKno Server

    private final String SHARED_PREF_NAME = "IotSmartInfo";
    private final int POLL_INTERVAL_MSEC = 5000;
    private int mFailureCount = 0;
    Handler mRetryHandler = new Handler();

    private static String mTokenStr;
    private static String mLoginId;
    private static PersonLogInfo mLoggedInPersonInfo;
    private static boolean mIsConnectedToServer = false;

    private static AppComponent mAppComponent;
    private static SharedPrefsComponent mSharedPrefsComponent;
    private static LoginActivityComponent mLoginActivityComponent;
    private static MainActivityComponent mMainActivityComponent;
    private static HomeActivityComponent mHomeActivityComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        //setup Fabric crashlytics
        //TODO for development time disable Crashlytics feature
//        Fabric.with(this, new Crashlytics());

        //setup App component
        setupAppComponent();

        //setup shared preference
        setupSharedPrefsComponent();

        mAppComponent.inject(this);

        //IOTObject 3D Loader 
       //  Object3DLoader.startLoading(getApplicationContext());
    }

    public static String getTokenStr() {
        return mTokenStr;
    }

    public static void setTokenStr(String token) {
        App.mTokenStr = token;
    }

    public static String getLoginId() {
        return mLoginId;
    }

    public static void setLoginId(String id) {
        App.mLoginId = id;
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static SharedPrefsComponent getSharedPrefsComponent(){
        return mSharedPrefsComponent;
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public static MainActivityComponent getMainActivityComponent() {
        return mMainActivityComponent;
    }

    public static LoginActivityComponent getLoginActivityComponent(){
        return mLoginActivityComponent;
    }

    public static HomeActivityComponent getHomeActivityComponent(){
        return mHomeActivityComponent;
    }

    public static void inject(LoginActivity activity) {
        App.setupLoginActivityComponent(activity);
    }

    public static void inject(MainActivity activity){
        App.setupMainActivityComponent(activity);
    }

    public static void inject(HomeActivity activity){App.setupHomeActivityComponent(activity);}

    private void setupAppComponent() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .apiServiceModule(new ApiServiceModule(API_SERVICE_URL))
                .build();
    }

    private void setupSharedPrefsComponent() {
        mSharedPrefsComponent = DaggerSharedPrefsComponent.builder()
                .sharedPrefsModule(new SharedPrefsModule(getApplicationContext(), SHARED_PREF_NAME))
                .appComponent(mAppComponent)
                .build();
    }

    private static void setupLoginActivityComponent(LoginActivity activity) {
        mLoginActivityComponent = DaggerLoginActivityComponent.builder()
                .loginActivityModule(new LoginActivityModule(activity))
                .appComponent(mAppComponent)
                .build();
    }

    private static void setupMainActivityComponent(MainActivity activity) {
        mMainActivityComponent = DaggerMainActivityComponent.builder()
                .mainActivityModule(new MainActivityModule(activity))
                .appComponent(mAppComponent)
                .build();
    }

    private static void setupHomeActivityComponent(HomeActivity activity){
         mHomeActivityComponent = DaggerHomeActivityComponent
                 .builder().homeActivityModule(new HomeActivityModule(activity))
                 .appComponent(mAppComponent)
                 .build();
    }

    @Override
    public void onSuccess(API_QUERY_TYPE api_query_type) {

    }

    @Override
    public void onFailure(API_QUERY_TYPE api_query_type) {

    }

    /*public volatile ArrayList<PointValue> temperatureValues, pressureValues, humidityValues;
    public volatile ArrayList<ArrayList<PointValue>> accelerometerValues, gyroscopeValues, compassValues, teapotValues;

    public void initSensorValues() {
        // 2D
        temperatureValues = new ArrayList<>();
        pressureValues = new ArrayList<>();
        humidityValues = new ArrayList<>();

        // 3D
        accelerometerValues = new ArrayList<ArrayList<PointValue>>() {{
            add(new ArrayList<PointValue>());
            add(new ArrayList<PointValue>());
            add(new ArrayList<PointValue>());
        }};
        gyroscopeValues = new ArrayList<ArrayList<PointValue>>() {{
            add(new ArrayList<PointValue>());
            add(new ArrayList<PointValue>());
            add(new ArrayList<PointValue>());
        }};
        compassValues = new ArrayList<ArrayList<PointValue>>() {{
            add(new ArrayList<PointValue>());
            add(new ArrayList<PointValue>());
            add(new ArrayList<PointValue>());
        }};
        teapotValues = new ArrayList<ArrayList<PointValue>>() {{
            add(new ArrayList<PointValue>());
            add(new ArrayList<PointValue>());
            add(new ArrayList<PointValue>());
        }};
    }*/

}














