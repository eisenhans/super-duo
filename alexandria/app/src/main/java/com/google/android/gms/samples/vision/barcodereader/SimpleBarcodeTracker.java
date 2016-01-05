package com.google.android.gms.samples.vision.barcodereader;

import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

public class SimpleBarcodeTracker extends Tracker<Barcode> {
    private static final String LOG_TAG = SimpleBarcodeTracker.class.getName();

    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode barcode) {
        Log.i(LOG_TAG, "Barcode found: " + barcode.displayValue);
        Log.i(LOG_TAG, "first detectionResult: " + detectionResults.getDetectedItems().get(0).displayValue);
    }
}
