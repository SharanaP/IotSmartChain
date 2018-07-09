package com.example.sharan.iotsmartchain.di.component;

import com.example.sharan.iotsmartchain.di.module.ActivityModule;
import com.example.sharan.iotsmartchain.di.scope.PerActivity;


import dagger.Component;

/**
 * Created by Sharan on 20-03-2018.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface AbstractActivityComponent {

    //Activity getActivityContext();
}
