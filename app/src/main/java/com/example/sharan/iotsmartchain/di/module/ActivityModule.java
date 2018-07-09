package com.example.sharan.iotsmartchain.di.module;

import android.app.Activity;

import com.example.sharan.iotsmartchain.di.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sharan on 20-03-2018.
 */
@Module
public class ActivityModule {
    private final Activity mActivityContext;

    public ActivityModule(Activity activity) {
        this.mActivityContext = activity;
    }

    @Provides
    @PerActivity
    Activity getActivityContext() {
        return mActivityContext;
    }
}

