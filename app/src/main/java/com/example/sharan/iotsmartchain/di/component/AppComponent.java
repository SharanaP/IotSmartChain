package com.example.sharan.iotsmartchain.di.component;

import android.app.Application;

import com.example.sharan.iotsmartchain.App;
import com.example.sharan.iotsmartchain.di.module.ApiServiceModule;
import com.example.sharan.iotsmartchain.di.module.AppModule;
import com.example.sharan.iotsmartchain.di.module.DataModelModule;
import com.squareup.otto.Bus;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Sharan on 20-03-2018.
 */
@Singleton
@Component(modules = {AppModule.class, ApiServiceModule.class, DataModelModule.class})
public interface AppComponent {

    void inject(App app);

    Application getApplication();
    @Named("ApiServiceUrl") String getApiServiceUrl();
    Bus getBus();
}
