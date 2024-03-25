package com.susano.WalkEasy.ESP_Cam;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import org.java_websocket.WebSocket;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;

public class RenderFrame implements RenderFrameInterface {
    Activity context;
    ImageView imageViewLeft, imageViewRight;

    public RenderFrame(Activity context, ImageView imageViewLeft, ImageView imageViewRight){
        this.context = context;
        this.imageViewLeft = imageViewLeft;
        this.imageViewRight = imageViewRight;
    }
    @Override
    public void render(ByteBuffer frame, String path) {
        context.runOnUiThread(() -> {
            byte[] jpegData = frame.array();
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
            Mat mat = new Mat();
            mat.getNativeObjAddr();
            Utils.bitmapToMat(bitmap, mat);
            updateImage(mat.getNativeObjAddr(), path);
            Log.d("MyServer", " message width: " + bitmap.getWidth()  + " message height: " + bitmap.getHeight());

            if(path.equals("/Left"))
                imageViewLeft.setImageBitmap(bitmap);
            else if(path.equals("/Right"))
                imageViewRight.setImageBitmap(bitmap);
        });
    }

    public native void updateImage(long matAddr, String path);
}
