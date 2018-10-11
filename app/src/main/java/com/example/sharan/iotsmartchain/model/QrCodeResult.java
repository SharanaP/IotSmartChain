package com.example.sharan.iotsmartchain.model;

import javax.inject.Inject;

public class QrCodeResult {
    private String scannedResult;
    private boolean isScanned;

    @Inject
    public QrCodeResult() {
        isScanned = false;
    }

    public String getScannedResult() {
        return scannedResult;
    }

    public void setScannedResult(String scannedResult) {
        this.scannedResult = scannedResult;
    }

    public boolean isScanned() {
        return isScanned;
    }

    public void setScanned(boolean scanned) {
        isScanned = scanned;
    }
}
