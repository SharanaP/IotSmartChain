package com.example.sharan.iotsmartchain.global;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sharan on 19-03-2018.
 */

public class Utils {

    /*Fragment replace*/
    public static void replaceFragment(final Activity activity, final Fragment fragment,
                                       final int layoutResourceId, final boolean addToBackstack) {
        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.replace(layoutResourceId, fragment);
        if (addToBackstack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
         fragmentTransaction.commit();
    }

    /*Show Progress */
    public static void showProgress(final Context context, final View fragmentContainer,
                                    final View progressView, final boolean show) {
        showView(fragmentContainer, !show);
        animate(context, fragmentContainer, !show);
        showView(progressView, show);
        animate(context, progressView, show);
    }

    /*Show progress visible or gone*/
    private static void showView(final View view, final boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /*Animation */
    private static void animate(final Context context, final View view, final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in the
        // progress spinner.
        int shortAnimTime = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        ViewPropertyAnimator animator = view.animate();
        animator.setDuration(shortAnimTime);
        if (show) {
            animator.alpha(1);
        } else {
            animator.alpha(0);
        }
        animator.setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(final Animator animation) {
                showView(view, show);
            }
        });
    }

    /**
     * @param r Reader
     * @return String
     */
    public static String readerToString(BufferedReader r) {
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = r.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /*Get Current data and time */
    public static String getDataFormat(){
        String dataStr = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy, hh:mm a");
        dataStr = simpleDateFormat.format(new Date());
        Log.d("getDataFormat", " :: "+dataStr);
        return dataStr;
    }

    public static String convertTime(long time){
        String dateString = new SimpleDateFormat("yyyy MMM dd, HH:mm aa").format(new Date(time));
        Log.d("Utils", "dateString : "+dateString);

        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MMM dd, HH:mm aa");
        return format.format(date);
    }
}
