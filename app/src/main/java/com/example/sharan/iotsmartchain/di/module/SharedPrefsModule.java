package com.example.sharan.iotsmartchain.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sharan.iotsmartchain.di.scope.PerApp;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sharan on 20-03-2018.
 */
@Module
public class SharedPrefsModule {
    private String PREF_NAME = "IotDevicesModelJson";
    private Context mContext;

    public SharedPrefsModule(Context context, String sharedPrefName) {
        this.PREF_NAME = new String(sharedPrefName);
        this.mContext = context;
    }

    @Provides
    @PerApp
    SharedPreferences provideSharedPrefs() {
        return mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @PerApp
    SharedPreferences.Editor provideSharedPrefsEditor(SharedPreferences sharedPreferences) {
        return sharedPreferences.edit();
    }
}
