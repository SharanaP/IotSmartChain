package com.example.sharan.iotsmartchain.di.component;

import com.example.sharan.iotsmartchain.Presenter.MainActivityPresenter;
import com.example.sharan.iotsmartchain.di.module.MainActivityModule;
import com.example.sharan.iotsmartchain.di.scope.PerActivity;
import com.example.sharan.iotsmartchain.main.activities.MainActivity;

import dagger.Component;

/**
 * Created by Sharan on 20-03-2018.
 */

@PerActivity
@Component(dependencies = AppComponent.class,
        modules = { MainActivityModule.class})
public interface MainActivityComponent extends AbstractActivityComponent {

    void inject(MainActivity activity);

    MainActivity getMainActivity();
    MainActivityPresenter getMainActivityPresenter();
}