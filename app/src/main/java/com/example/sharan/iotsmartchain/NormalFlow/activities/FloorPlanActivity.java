package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;


import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.DashBoardActivity;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import butterknife.BindView;

public class FloorPlanActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button_next)
    Button mBtnNext;

    @BindView(R.id.webView_floorplan)
    WebView webView;

    @BindView(R.id.progressBar_webView)
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floorplan);

        injectViews();

        setupToolbar();

        /*Set/Call web page load inside a activity*/
        setWebView();

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FloorPlanActivity.this, DashBoardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setWebView() {
        webView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);
        webView.loadUrl("https://floorplanner.com/");
    }

    public class CustomWebViewClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }



        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Floor Plan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                this.finish();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }

    }
}
