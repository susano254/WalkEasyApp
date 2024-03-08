package com.susano.WalkEasy.ESP_Cam;

import static com.google.vr.cardboard.ThreadUtils.runOnUiThread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.nio.ByteBuffer;

public class RenderFrameImplementation implements RenderFrame {
    String descriptor;
    ImageView imageViewLeft, imageViewRight;
    cameraView leftCamera, rightCamera;

    @Override
    public void render(ByteBuffer frame, String descriptor) {
        {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    byte[] jpegData = frame.array();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
                    Log.d("MyServer", " message width: " + bitmap.getWidth() +
                            " message height: " + bitmap.getHeight());

                    if (leftCamera.which_camera()){
                        imageViewLeft.setImageBitmap(bitmap);
                    }else {   imageViewRight.setImageBitmap(bitmap);}

                }
            });

        }


    }
}