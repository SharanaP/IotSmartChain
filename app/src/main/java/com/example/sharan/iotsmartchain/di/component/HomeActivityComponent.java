package com.example.sharan.iotsmartchain.di.component;

import com.example.sharan.iotsmartchain.Presenter.HomeActivityPresenter;
import com.example.sharan.iotsmartchain.di.module.HomeActivityModule;
import com.example.sharan.iotsmartchain.di.scope.PerActivity;
import com.example.sharan.iotsmartchain.NormalFlow.activities.HomeActivity;


import dagger.Component;

/**
 * Created by Sharan on 21-03-2018.
 */
@PerActivity
@Component(dependencies = AppComponent.class,
        modules = HomeActivityModule.class)
public interface HomeActivityComponent extends AbstractActivityComponent {
    void inject(HomeActivity homeActivity);

    HomeActivity getHomeActivity();
    HomeActivityPresenter getHomeActivityPresenter();
}
