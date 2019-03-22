package com.example.sharan.iotsmartchain.main.fragments;

import android.app.Fragment;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
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

    private static final int SPLASH_TIME = 4000;
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

        //App title logo
        VideoView videoView = (VideoView) view.findViewById(R.id.video_view);
//        ImageView mImageIcon = (ImageView) view.findViewById(R.id.app_logo);
//        ImageView mImageTitle = (ImageView) view.findViewById(R.id.app_title_logo);
//        mImageIcon.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        String path = "android.resource://" + PACKAGE_NAME + "/" + R.raw.sample_test_3;
        Log.e(TAG, "path main :: " + path);

        Uri uri = Uri.parse(path);
        Log.e(TAG, "uri main :: " + uri.toString());

        videoView.setVideoURI(uri);
        videoView.requestFocus();

        //Set the surface holder height to the screen dimensions
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        videoView.getHolder().setFixedSize(size.x, size.y);
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setLooping(false);
            }
        });
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });

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