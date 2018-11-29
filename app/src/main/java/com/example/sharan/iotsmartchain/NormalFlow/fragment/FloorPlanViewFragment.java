package com.example.sharan.iotsmartchain.NormalFlow.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
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

public class FloorPlanViewFragment extends BaseFragment {

    private static final String TAG = FloorPlanViewFragment.class.getSimpleName();
    @BindView(R.id.webView_db_floorplan)
    WebView mWebView;
    @BindView(R.id.progressBar_db_webView)
    ProgressBar mProgressBar;

    public static FloorPlanViewFragment newInstance() {
        FloorPlanViewFragment fragment = new FloorPlanViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_floor_plan_view, container, false);
        ButterKnife.bind(this, rootView);

        mWebView = (WebView) rootView.findViewById(R.id.webView_db_floorplan);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_db_webView);
        setWebView();

        return rootView;
    }

    private void setWebView() {
        mWebView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);

        //mWebView.loadUrl("http://111.93.31.170:10000/Floorplan/#");

      //  mWebView.loadUrl("http://192.168.1.27:10000/Floorplan/create_test.jsp");// 19th nov 2018
        mWebView.loadUrl("http://192.168.1.27:10000/Floorplan/alert.html");// 19th nov 2018
        //  mWebView.loadUrl("http://192.168.1.110:10002/geoserver/floorplan/wms?service=WMS&version=1.1.0&request=GetMap&layers=floorplan:floorplangroup&styles=&bbox=-0.0789120569825172,6.67328691482544,86.5327606201172,84.0403442382813&width=768&height=686&srs=EPSG:32643&format=application/openlayers");
    }

    //To handle "Back" key press event for WebView to go back to previous screen.
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
