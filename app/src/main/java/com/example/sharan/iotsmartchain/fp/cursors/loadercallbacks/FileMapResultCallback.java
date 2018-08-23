package com.example.sharan.iotsmartchain.fp.cursors.loadercallbacks;


import com.example.sharan.iotsmartchain.fp.models.Document;
import com.example.sharan.iotsmartchain.fp.models.FileType;

import java.util.List;
import java.util.Map;

public interface FileMapResultCallback {
    void onResultCallback(Map<FileType, List<Document>> files);
}

