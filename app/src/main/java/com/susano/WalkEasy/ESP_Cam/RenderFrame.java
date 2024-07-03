package com.susano.WalkEasy.ESP_Cam;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.susano.WalkEasy.ObjectDetection.tflite.TFLiteYoloV5;

import org.java_websocket.WebSocket;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;

public class RenderFrame implements RenderFrameInterface {
    Activity context;
    TFLiteYoloV5 detector;
    ImageView imageViewLeft, imageViewRight, imageViewDepth;

    public RenderFrame(Activity context, ImageView imageViewLeft, ImageView imageViewDepth, ImageView imageViewRight, TFLiteYoloV5 detector){
        this.context = context;
        this.detector = detector;
        this.imageViewLeft = imageViewLeft;
        this.imageViewRight = imageViewRight;
        this.imageViewDepth = imageViewDepth;
    }
    @Override
    public void process(ByteBuffer frame, String path) {
        context.runOnUiThread(() -> {
            byte[] jpegData = frame.array();
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);

            // for the openCL processing in the C++ code
            Mat mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);
            updateImage(mat.getNativeObjAddr(), path);
            // render(mat, path);

            Mat depthMap = new Mat();
            getDepthMap(depthMap.getNativeObjAddr());
//            Log.d("StereoGlassesInfo", "DepthMap address: " + depthMap.getNativeObjAddr() + " DepthMap rows are: " + depthMap.rows());
            renderDepth(depthMap);
        });
    }

    @Override
    public void render(Mat frame, String path) {
        if(path.equals("/Left")) {
            Bitmap bitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(frame, bitmap);
            imageViewLeft.setImageBitmap(bitmap);
        }
        else if(path.equals("/Right")) {
            Bitmap bitmap = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(frame, bitmap);
            imageViewRight.setImageBitmap(bitmap);
        }

    }

    @Override
    public void renderDepth(Mat depthMap) {
        if(depthMap != null && !depthMap.empty()) {
            Log.d("StereoGlassesInfo", "Rendering DepthMap");
            Bitmap bitmap = Bitmap.createBitmap(depthMap.width(), depthMap.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(depthMap, bitmap);
            imageViewDepth.setImageBitmap(bitmap);
            Log.d("StereoGlassesInfo", "DepthMap rendered successfully");
        }
    }

    public native void updateImage(long matAddr, String path);

    public native void getDepthMap(long matAddr);
}
