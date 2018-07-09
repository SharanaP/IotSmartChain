package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FloorPlanFragment extends BaseFragment {
    private static String TAG = "FloorPlanFragment";

    @BindView(R.id.webView_db_floorplan)
    WebView mWebView;

    @BindView(R.id.progressBar_db_webView)
    ProgressBar mProgressBar;

    public static FloorPlanFragment newInstance() {
        FloorPlanFragment floorPlanFragment = new FloorPlanFragment();
        return floorPlanFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_db_floorplan, container, false);

        mWebView = (WebView)rootView.findViewById(R.id.webView_db_floorplan);
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progressBar_db_webView);

        /*Set/Call web page load inside a activity*/
        setWebView();

        return rootView;
    }

    private void setWebView() {
        mWebView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);
        mWebView.loadUrl("https://floorplanner.com/");
    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    public boolean canGoBack() {
        return this.mWebView != null && this.mWebView.canGoBack();
    }

    public void goBack() {
        if (this.mWebView != null) {
            this.mWebView.goBack();
        }
    }

    public class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mProgressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    }

}
