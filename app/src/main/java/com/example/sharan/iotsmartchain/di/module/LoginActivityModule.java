package com.example.sharan.iotsmartchain.di.module;

import com.example.sharan.iotsmartchain.Presenter.LoginActivityPresenter;
import com.example.sharan.iotsmartchain.di.scope.PerActivity;
import com.example.sharan.iotsmartchain.loginModule.activities.LoginActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sharan on 20-03-2018.
 */

@Module
public class LoginActivityModule {
    private LoginActivity mActivity;

    public LoginActivityModule(LoginActivity activity) {
        mActivity = activity;
    }

    @Provides
    @PerActivity
    LoginActivity provideLoginActivity() {
        return mActivity;
    }

    @Provides
    @PerActivity
    LoginActivityPresenter provideLoginActivityPresenter(LoginActivity activity ) {
        return new LoginActivityPresenter(activity);
    }
}
