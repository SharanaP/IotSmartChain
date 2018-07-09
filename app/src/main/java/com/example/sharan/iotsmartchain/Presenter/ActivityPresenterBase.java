package com.example.sharan.iotsmartchain.Presenter;

/**
 * Created by Sharan on 20-03-2018.
 */

public interface ActivityPresenterBase {

    //Activity/Fragment's onResume()
    void resume();

    //Activity/Fragment's onPause()
    void pause();

    //Activity/Fragment's onDestroy()
    void destroy();
}
