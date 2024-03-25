package com.susano.WalkEasy.ESP_Cam;

import java.net.InetSocketAddress;

public class Camera {
    public InetSocketAddress leftCamera;
    public boolean isConnected = false;

    public Camera(InetSocketAddress leftCamera){
        this.leftCamera = leftCamera;
    }
}
