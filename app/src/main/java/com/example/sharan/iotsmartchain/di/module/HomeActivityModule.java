package com.example.sharan.iotsmartchain.di.module;

import com.example.sharan.iotsmartchain.Presenter.HomeActivityPresenter;
import com.example.sharan.iotsmartchain.di.scope.PerActivity;
import com.example.sharan.iotsmartchain.NormalFlow.activities.HomeActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sharan on 21-03-2018.
 */

@Module
public class HomeActivityModule {
    private HomeActivity mActivity;

    public HomeActivityModule(HomeActivity activity){mActivity = activity;}

    @Provides
    @PerActivity
    HomeActivity provideHomeActivity(){return mActivity;}

    @Provides
    @PerActivity
    HomeActivityPresenter provideHomeActivityPresenter(HomeActivity activity){
        return new HomeActivityPresenter(activity);
    }

}
