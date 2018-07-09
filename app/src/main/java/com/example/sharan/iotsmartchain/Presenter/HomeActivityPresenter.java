package com.example.sharan.iotsmartchain.Presenter;


import com.example.sharan.iotsmartchain.NormalFlow.activities.HomeActivity;

import javax.inject.Inject;

/**
 * Created by Sharan on 21-03-2018.
 */

public class HomeActivityPresenter implements ActivityPresenterBase {

    HomeActivity mHomeActivity;

    @Inject
    public HomeActivityPresenter(HomeActivity activity){mHomeActivity = activity;}

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
