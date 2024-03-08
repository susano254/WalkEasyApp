package com.susano.WalkEasy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.vr.sdk.audio.GvrAudioEngine;
import com.susano.WalkEasy.ESP_Cam.MyWebSocketServer;
//import com.susano.WalkEasy.ESP_Cam.RenderFrame;
import com.susano.WalkEasy.ESP_Cam.RenderFrameImplementation;
//import com.susano.WalkEasy.ESP_Cam.cameraView;
import com.susano.WalkEasy.databinding.ActivityMainBinding;

import org.opencv.android.OpenCVLoader;

import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {
    ImageView imageViewLeft, imageViewRight;


    MyWebSocketServer webSocketServer;
    private static final String SOUND_FILE = "audio/HelloVR_Loop.ogg";

    // Used to load the 'opencl' library on application startup.
    static {
        System.loadLibrary("WalkEasy");
        System.loadLibrary("opencv_java4");
    }

    RenderFrameImplementation callBack = new RenderFrameImplementation();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.susano.WalkEasy.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        imageViewLeft = findViewById(R.id.textureView1);
        imageViewRight =  findViewById(R.id.textureView2);

        // Comment this two lines if you can't connect the hardware
        // Create and start the WebSocket server on a specific port
        webSocketServer = new MyWebSocketServer(new InetSocketAddress(8000), callBack);
        webSocketServer.start();

        if(OpenCVLoader.initDebug()){
            Log.d("Loaded", "Success");
        }
        else {
            Log.d("Loaded", "ERROR");
        }


        GvrAudioEngine gvrAudioEngine = new GvrAudioEngine(this, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);
        float[] modelPosition = {0.0f, 1.0f, 10.0f};
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        // Preload the sound file
                        gvrAudioEngine.preloadSoundFile(SOUND_FILE);
                        // Create the 'sourceId' sound object.
                        // You can create multiple sound objects using the same sound file.
                        int sourceId = gvrAudioEngine.createSoundObject(SOUND_FILE);
                        // Set the sound object position relative to the room model.
                        gvrAudioEngine.setSoundObjectPosition(
                                sourceId, modelPosition[0], modelPosition[1], modelPosition[2]);
                        // Start audio playback of the sound object sound file at the
                        // cube model position. You can update the sound object position
                        // whenever the cube moves during run time.
                        gvrAudioEngine.playSound(sourceId, true /* looped playback */);
                    }
                })
                .start();
        stringFromJNI();
    }


    /**
     * A native method that is implemented by the 'opencl' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}