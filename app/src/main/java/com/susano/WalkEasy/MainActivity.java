package com.susano.WalkEasy;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.vr.sdk.audio.GvrAudioEngine;
import com.susano.WalkEasy.databinding.ActivityMainBinding;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private static final String SOUND_FILE = "audio/HelloVR_Loop.ogg";

    // Used to load the 'opencl' library on application startup.
    static {
        System.loadLibrary("WalkEasy");
        System.loadLibrary("opencv_java4");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
        
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
    }

    /**
     * A native method that is implemented by the 'opencl' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}