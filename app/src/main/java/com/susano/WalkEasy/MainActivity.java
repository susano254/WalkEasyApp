package com.susano.WalkEasy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.susano.WalkEasy.ESP_Cam.MyWebSocketServer;
import com.susano.WalkEasy.ESP_Cam.RenderFrame;
import com.susano.WalkEasy.ObjectDetection.tflite.TFLiteYoloV5;
import com.susano.WalkEasy.SpatialSound.SpatialSound;
import com.susano.WalkEasy.databinding.ActivityMainBinding;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TFLiteYoloV5 detector;
    ImageView imageViewLeft, imageViewRight, imageViewDepth;
    MyWebSocketServer webSocketServer;
    Thread th;

    // Used to load the 'opencl' library on application startup.
    static {
        System.loadLibrary("WalkEasy");
        System.loadLibrary("opencv_java4");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        imageViewLeft = findViewById(R.id.imageLeft);
        imageViewRight = findViewById(R.id.imageRight);
        imageViewDepth = findViewById(R.id.depthMap);

        // Comment this two lines if you can't connect the hardware
        // Create and start the WebSocket server on a specific port
        RenderFrame callBack = new RenderFrame(this, imageViewLeft, imageViewRight, imageViewDepth, detector);
        Log.d("MyServer", "Creating WebSocket server");
        webSocketServer = new MyWebSocketServer(new InetSocketAddress(8000), callBack);
        webSocketServer.start();
        Log.d("MyServer", "WebSocket server started");

//        try {
//            detector = new TFLiteYoloV5(getAssets(), "ssd_mobilenet_v1_1_metadata_1.tflite", "ssd_labels.txt", 300, true);
////            detector = new TFLiteYoloV5(getAssets(), "yolov5s-fp16-320-metadata.tflite", "coco_label.txt", 320);
//            Log.d("WalkEasy", "Detector created");
//        } catch (Exception e) {
//            Log.e("WalkEasy", "Error creating detector", e);
//            e.printStackTrace();
//        }

        OpenCVLoader.initLocal();

        main();
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            Log.d("MyServer", "Stopping WebSocket server");
            webSocketServer.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
//
    @Override
    protected void onStop() {
        super.onStop();
        try {
            Log.d("MyServer", "Stopping WebSocket server");
            webSocketServer.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Log.d("MyServer", "Stopping WebSocket server");
            webSocketServer.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * A native method that is implemented by the 'opencl' native library,
     * which is packaged with this application.
     */
    public native void main();

    public native void exit();


    public void exit(View view) {
        try {
            exit();
            Log.d("MyServer", "Stopping WebSocket server");
            webSocketServer.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finish();
    }
}