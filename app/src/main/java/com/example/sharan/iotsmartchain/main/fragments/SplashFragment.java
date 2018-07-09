package com.example.sharan.iotsmartchain.main.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.BusProvider;

/**
 * Created by Sharan on 19-03-2018.
 */

public class SplashFragment extends Fragment {

    private static final int SPLASH_TIME = 5000;
    private static final String TAG = "SplashFragment";
    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        // Set font
        TextView appName = (TextView) view.findViewById(R.id.appName);
        appName.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/MyriadPro-Light.otf"));

        //geokno or app powered by logo
        ImageView imageLogo = (ImageView)view.findViewById(R.id.geoknoLogo);

        // Dismiss timer
        Runnable exitRunnable = new Runnable() {
            @Override
            public void run() {
                 BusProvider.getInstance().post(new SplashEvent());
            }
        };

        mHandler.postDelayed(exitRunnable, SPLASH_TIME);

        // Dismiss on click
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 BusProvider.getInstance().post(new SplashEvent());
            }
        });

        //Start animation title
        Animation animation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.alpha);
        animation.reset();
        appName.setAnimation(animation);
        appName.clearAnimation();
        appName.startAnimation(animation);

        //start animation logo
        Animation animLogo= AnimationUtils.loadAnimation(this.getActivity(), R.anim.translate);
        animLogo.reset();
        imageLogo.setAnimation(animLogo);
        imageLogo.clearAnimation();
        imageLogo.setAnimation(animLogo);

        return view;
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public static class SplashEvent {
        //
    }

}