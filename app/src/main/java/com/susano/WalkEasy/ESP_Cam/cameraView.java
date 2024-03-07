package com.susano.WalkEasy.ESP_Cam;


import org.java_websocket.WebSocket;

import java.util.Objects;


public class cameraView {
    WebSocket conn ;
//    public void camera(){}
    public boolean which_camera(){

        String descriptor = conn.getResourceDescriptor();
        if (Objects.equals(descriptor, "Left")) return true;
        else if (Objects.equals(descriptor, "Right")) return false;


        return false;
    }
}
