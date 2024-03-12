package com.susano.WalkEasy.ObjectDetection.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static org.tensorflow.lite.DataType.UINT8;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.susano.WalkEasy.ObjectDetection.tflite.Classifier;
import com.susano.WalkEasy.ObjectDetection.tflite.TFLiteObjectDetectionSSDAPIModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class object_main extends AppCompatActivity {
    private Classifier detectorSSD;
    private List<TFLiteObjectDetectionSSDAPIModel.Recognition> detectorSSDResult = new ArrayList<>();

    public void ObjectDetection() {

        try {
            detectorSSD =
                    TFLiteObjectDetectionSSDAPIModel.create(
                            this.getAssets(),
                            "ssdlite_mobilenet_v2_quantized.tflite",
                            "",
                            300,
                            Classifier.Device.CPU,
                            UINT8,
                            0.5f,
                            1,
                            640,
                            480
                    );
            detectorSSD.startThread();
        } catch (final IOException e) {
            Log.e(TAG, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            this, "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}

