package com.example.sharan.iotsmartchain.Presenter;


import com.example.sharan.iotsmartchain.loginModule.activities.LoginActivity;

import javax.inject.Inject;

/**
 * Created by Sharan on 20-03-2018.
 */

public class LoginActivityPresenter implements ActivityPresenterBase {

    LoginActivity mLoginActivity;

    @Inject
    public LoginActivityPresenter(LoginActivity loginActivity) {
        mLoginActivity = loginActivity;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void destroy() {

    }

}
