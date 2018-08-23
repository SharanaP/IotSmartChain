package com.example.sharan.iotsmartchain.fp.utils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.sharan.iotsmartchain.fp.FilePickerConst;
import com.example.sharan.iotsmartchain.fp.cursors.DocScannerTask;
import com.example.sharan.iotsmartchain.fp.cursors.loadercallbacks.FileMapResultCallback;
import com.example.sharan.iotsmartchain.fp.cursors.loadercallbacks.FileResultCallback;
import com.example.sharan.iotsmartchain.fp.cursors.loadercallbacks.PhotoDirLoaderCallbacks;
import com.example.sharan.iotsmartchain.fp.models.Document;
import com.example.sharan.iotsmartchain.fp.models.FileType;
import com.example.sharan.iotsmartchain.fp.models.PhotoDirectory;

import java.util.Comparator;
import java.util.List;

public class MediaStoreHelper {

  public static void getPhotoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
    if(activity.getSupportLoaderManager().getLoader(FilePickerConst.MEDIA_TYPE_IMAGE)!=null)
      activity.getSupportLoaderManager().restartLoader(FilePickerConst.MEDIA_TYPE_IMAGE, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    else
      activity.getSupportLoaderManager().initLoader(FilePickerConst.MEDIA_TYPE_IMAGE, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
  }

  public static void getVideoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
    if(activity.getSupportLoaderManager().getLoader(FilePickerConst.MEDIA_TYPE_VIDEO)!=null)
      activity.getSupportLoaderManager().restartLoader(FilePickerConst.MEDIA_TYPE_VIDEO, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    else
      activity.getSupportLoaderManager().initLoader(FilePickerConst.MEDIA_TYPE_VIDEO, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
  }

  public static void getDocs(FragmentActivity activity,
                             List<FileType> fileTypes,
                             Comparator<Document> comparator,
                             FileMapResultCallback fileResultCallback)
  {
    new DocScannerTask(activity, fileTypes, comparator, fileResultCallback).execute();
  }
}