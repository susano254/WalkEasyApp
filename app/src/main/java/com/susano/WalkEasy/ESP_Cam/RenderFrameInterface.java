package com.susano.WalkEasy.ESP_Cam;

import org.java_websocket.WebSocket;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;

// Define a callback interface
public interface RenderFrameInterface {
    void process(ByteBuffer frame, String path);
    void render(Mat frame, String path);

    void renderDepth(Mat depthMap);
}
