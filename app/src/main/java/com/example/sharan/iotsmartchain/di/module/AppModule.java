package com.example.sharan.iotsmartchain.di.module;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sharan on 20-03-2018.
 */
@Module
public class AppModule {
    private Application mAppApplication;

    public AppModule(Application application) {
        this.mAppApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mAppApplication;
    }

    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus(ThreadEnforcer.ANY);
    }
}
