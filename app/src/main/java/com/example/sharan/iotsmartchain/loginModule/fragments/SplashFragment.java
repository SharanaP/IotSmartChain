package com.example.sharan.iotsmartchain.loginModule.fragments;

import android.app.Fragment;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.global.BusProvider;


/**
 * Created by Sharan on 19-03-2018.
 */

public class SplashFragment extends Fragment {

    private static final int SPLASH_TIME = 3000;
    private static final String TAG = "SplashFragment";
    public static String PACKAGE_NAME = "";
    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        PACKAGE_NAME = getActivity().getPackageName();

        //App title and logo
        VideoView videoView = (VideoView) view.findViewById(R.id.video_view);
//        ImageView mImageIcon = (ImageView) view.findViewById(R.id.app_logo);
//        ImageView mImageTitle = (ImageView) view.findViewById(R.id.app_title_logo);
//        mImageIcon.setVisibility(View.GONE);

        String path = "android.resource://" + PACKAGE_NAME + "/" + R.raw.sample_test_3;
        Log.e(TAG, "path :: " + path);
        Uri uri = Uri.parse(path);
        Log.e(TAG, "uri :: " + uri.toString());
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

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