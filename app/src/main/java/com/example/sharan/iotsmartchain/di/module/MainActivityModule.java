package com.example.sharan.iotsmartchain.di.module;

import com.example.sharan.iotsmartchain.Presenter.MainActivityPresenter;
import com.example.sharan.iotsmartchain.di.scope.PerActivity;
import com.example.sharan.iotsmartchain.main.activities.MainActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sharan on 20-03-2018.
 */

@Module
public class MainActivityModule {

    private MainActivity mMainActivity;

    public MainActivityModule(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
    }

    @Provides
    @PerActivity
    MainActivity provideMainActivity() {
        return mMainActivity;
    }

    @Provides
    @PerActivity
    MainActivityPresenter provideMainActivityPresenter(MainActivity mainActivity) {
        return new MainActivityPresenter(mainActivity);
    }
}
