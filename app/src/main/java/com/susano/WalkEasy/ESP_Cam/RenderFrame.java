package com.susano.WalkEasy.ESP_Cam;

import java.nio.ByteBuffer;

// Define a callback interface
public interface RenderFrame {
    void render(ByteBuffer frame, String descriptor);
}
