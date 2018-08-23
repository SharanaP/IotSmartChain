package com.example.sharan.iotsmartchain.dashboard.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.fp.FilePickerBuilder;
import com.example.sharan.iotsmartchain.fp.FilePickerConst;
import com.example.sharan.iotsmartchain.fp.models.sort.SortingTypes;
import com.example.sharan.iotsmartchain.fp.utils.Orientation;
import com.example.sharan.iotsmartchain.main.activities.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class FloorPlanFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks  {
    private static String TAG = "FloorPlanFragment";

//    @BindView(R.id.webView_db_floorplan)
//    WebView mWebView;
//
//    @BindView(R.id.progressBar_db_webView)
//    ProgressBar mProgressBar;

    public static final int RC_FILE_PICKER_PERM = 321;
    private static final int CUSTOM_REQUEST_CODE = 532;
    private int MAX_ATTACHMENT_COUNT = 1;
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();

    public static FloorPlanFragment newInstance() {
        FloorPlanFragment floorPlanFragment = new FloorPlanFragment();
        return floorPlanFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  ButterKnife.bind(getActivity());


        //Setup action bar title and sub title
        getActivity().setTitle("iSmartLink");
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle("FloorPlan");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashbroad_floorplan, container, false);

        ButterKnife.bind(this, rootView);

//        mWebView = (WebView)rootView.findViewById(R.id.webView_db_floorplan);
//        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progressBar_db_webView);

        /*Set/Call web page load inside a activity*/
        //  setWebView();

        return rootView;
    }

    @AfterPermissionGranted(RC_FILE_PICKER_PERM)
    @OnClick(R.id.cardview_import)
    public void pickDocClicked() {
        if (EasyPermissions.hasPermissions(getActivity(), FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickDoc();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_doc_picker),
                    RC_FILE_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CUSTOM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                break;

            case FilePickerConst.REQUEST_CODE_DOC:

                Log.e(TAG, "SH : Request code for doc ");
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();


                    Log.e(TAG, "SH : docPaths " + docPaths);
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;
        }

        addThemToView(photoPaths, docPaths);
    }

    private void addThemToView(ArrayList<String> imagePaths, ArrayList<String> docPaths) {
        ArrayList<String> filePaths = new ArrayList<>();
        if (imagePaths != null) filePaths.addAll(imagePaths);

        if (docPaths != null) filePaths.addAll(docPaths);


        Toast.makeText(getActivity(), "Num of files selected: " + filePaths.size(), Toast.LENGTH_SHORT).show();
    }

    public void onPickPhoto() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.FilePickerTheme)
                .setActivityTitle("Please select media")
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(false)
                .enableSelectAll(false)
                .enableImagePicker(true)
                .setCameraPlaceholder(R.drawable.custom_camera)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickPhoto(this, CUSTOM_REQUEST_CODE);
    }

    public void onPickDoc() {
        String[] dwgs = {".dwg"};
        String[] dxfs = {".dxf"};
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(docPaths)
                .setActivityTheme(R.style.FilePickerTheme)
                .setActivityTitle("Please select doc")
                .addFileSupport("DWG", dwgs)
                .addFileSupport("DXF", dxfs)
                .enableDocSupport(false)
                .enableSelectAll(false)
                .sortDocumentsBy(SortingTypes.name)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickFile(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

//    private void setWebView() {
//        mWebView.setWebViewClient(new CustomWebViewClient());
//        WebSettings webSetting = mWebView.getSettings();
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setDisplayZoomControls(true);
//        mWebView.loadUrl("https://floorplanner.com/");
//    }

    // To handle "Back" key press event for WebView to go back to previous screen.
//    public boolean canGoBack() {
//        return this.mWebView != null && this.mWebView.canGoBack();
//    }
//
//    public void goBack() {
//        if (this.mWebView != null) {
//            this.mWebView.goBack();
//        }
//    }

//    public class CustomWebViewClient extends WebViewClient {
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            mProgressBar.setVisibility(View.VISIBLE);
//            view.loadUrl(url);
//            return true;
//        }
//
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            mProgressBar.setVisibility(View.INVISIBLE);
//        }
//
//    }

}
