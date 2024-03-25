package com.susano.WalkEasy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.susano.WalkEasy.ESP_Cam.MyWebSocketServer;
import com.susano.WalkEasy.ESP_Cam.RenderFrame;
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
    ImageView imageViewLeft, imageViewRight;
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
        imageViewLeft = findViewById(R.id.textureView1);
        imageViewRight = findViewById(R.id.textureView2);

        // Comment this two lines if you can't connect the hardware
        // Create and start the WebSocket server on a specific port
        RenderFrame callBack = new RenderFrame(this, imageViewLeft, imageViewRight);
        webSocketServer = new MyWebSocketServer(new InetSocketAddress(8000), callBack);
        webSocketServer.start();

        OpenCVLoader.initLocal();

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
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
    public native String init();


}