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
    ImageView imageViewLeft, imageViewRight;

    public RenderFrame(Activity context, ImageView imageViewLeft, ImageView imageViewRight, TFLiteYoloV5 detector){
        this.context = context;
        this.detector = detector;
        this.imageViewLeft = imageViewLeft;
        this.imageViewRight = imageViewRight;
    }
    @Override
    public void render(ByteBuffer frame, String path) {
        context.runOnUiThread(() -> {
            byte[] jpegData = frame.array();
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);

            // for the openCL processing in the C++ code
            Mat mat = new Mat();
            mat.getNativeObjAddr();
            Utils.bitmapToMat(bitmap, mat);
            updateImage(mat.getNativeObjAddr(), path);

//            Log.d("MyServer", " message width: " + bitmap.getWidth()  + " message height: " + bitmap.getHeight());

            // display and apply object detection if needed on both the left and right images
            if(path.equals("/Left")) {
//                detector.recognizeImage(bitmap);
                imageViewLeft.setImageBitmap(bitmap);
            }
            else if(path.equals("/Right")) {
                imageViewRight.setImageBitmap(bitmap);
            }
        });
    }

    public native void updateImage(long matAddr, String path);
}
