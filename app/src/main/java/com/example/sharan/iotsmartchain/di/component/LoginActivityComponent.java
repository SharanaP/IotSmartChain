package com.example.sharan.iotsmartchain.di.component;

import com.example.sharan.iotsmartchain.Presenter.LoginActivityPresenter;
import com.example.sharan.iotsmartchain.di.module.LoginActivityModule;
import com.example.sharan.iotsmartchain.di.scope.PerActivity;
import com.example.sharan.iotsmartchain.loginModule.activities.LoginActivity;


import dagger.Component;

/**
 * Created by Sharan on 20-03-2018.
 */

@PerActivity
@Component(dependencies = AppComponent.class,
        modules = LoginActivityModule.class)
public interface LoginActivityComponent extends AbstractActivityComponent {
    void inject(LoginActivity activity);

    LoginActivity getActivity();
    LoginActivityPresenter getLoginActivityPresenter();
}

