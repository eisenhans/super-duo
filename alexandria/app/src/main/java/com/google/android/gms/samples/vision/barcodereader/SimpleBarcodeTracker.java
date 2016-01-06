package com.google.android.gms.samples.vision.barcodereader;

import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

public class SimpleBarcodeTracker extends Tracker<Barcode> {
    private static final String LOG_TAG = SimpleBarcodeTracker.class.getName();

    private Callback callback;

    public SimpleBarcodeTracker(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onFound(String barcodeValue);
    }

    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode barcode) {
        if (barcode != null) {
            Log.i(LOG_TAG, "Barcode found: " + barcode.displayValue);
            callback.onFound(barcode.displayValue);
        }
    }
}
