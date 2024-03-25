package com.susano.WalkEasy.ESP_Cam;

import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

// Define a callback interface
public interface RenderFrameInterface {
    void render(ByteBuffer frame, String path);
}
