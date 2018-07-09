package com.example.sharan.iotsmartchain.di.component;

import android.content.SharedPreferences;

import com.example.sharan.iotsmartchain.di.module.SharedPrefsModule;
import com.example.sharan.iotsmartchain.di.scope.PerApp;
import dagger.Component;

/**
 * Created by Sharan on 20-03-2018.
 */

@PerApp
@Component(dependencies = AppComponent.class, modules = SharedPrefsModule.class)
public interface SharedPrefsComponent {
    SharedPreferences getSharedPrefs();
    SharedPreferences.Editor getSharedPrefsEditor();
}
