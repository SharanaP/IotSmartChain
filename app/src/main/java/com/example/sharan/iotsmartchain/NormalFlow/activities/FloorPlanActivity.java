package com.example.sharan.iotsmartchain.NormalFlow.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sharan.iotsmartchain.R;
import com.example.sharan.iotsmartchain.dashboard.activity.DashBoardActivity;
import com.example.sharan.iotsmartchain.fp.FilePickerBuilder;
import com.example.sharan.iotsmartchain.fp.FilePickerConst;
import com.example.sharan.iotsmartchain.fp.models.sort.SortingTypes;
import com.example.sharan.iotsmartchain.fp.utils.Orientation;
import com.example.sharan.iotsmartchain.main.activities.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class FloorPlanActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    public static final int RC_FILE_PICKER_PERM = 321;
    private static final int CUSTOM_REQUEST_CODE = 532;
    private static String TAG = "FloorPlanActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.button_next)
    Button mBtnNext;
    @BindView(R.id.progressBar_webView)
    ProgressBar progressBar;
    @BindView(R.id.cardview_new)
    CardView cardViewCreateNew;
    @BindView(R.id.cardview_import)
    CardView cardViewImportFile;
    @BindView(R.id.cardview_list)
    CardView cardViewList;
    @BindView(R.id.cardview_nonspatial)
    CardView cardViewNonSpatial;
    private int MAX_ATTACHMENT_COUNT = 1;
    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floorplan);

        injectViews();

        setupToolbar();

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FloorPlanActivity.this, DashBoardActivity.class);
                startActivity(intent);
            }
        });


        cardViewCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(cardViewCreateNew, "Floor Plan", Snackbar.LENGTH_SHORT).show();
                // display a floor plan
//                Fragment fragment = FloorPlanViewFragment.newInstance();
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.frame_layout, fragment);
//                fragmentTransaction.commit();

                //Intent webview
                Intent webViewIntent = new Intent(FloorPlanActivity.this, WebViewActivity.class);
                startActivity(webViewIntent);
            }
        });

        /*create a non spatial plan */
        cardViewNonSpatial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(cardViewCreateNew, "Non-Spatail Plan", Snackbar.LENGTH_SHORT).show();
                //TODO init
                Intent intent = new Intent(FloorPlanActivity.this, CreateNonSpatialActivity.class);
                startActivity(intent);
            }
        });

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

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @AfterPermissionGranted(RC_FILE_PICKER_PERM)
    @OnClick(R.id.cardview_import)
    public void pickDocClicked() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickDoc();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_doc_picker),
                    RC_FILE_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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


        Toast.makeText(this, "Num of files selected: " + filePaths.size(), Toast.LENGTH_SHORT).show();
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
}
