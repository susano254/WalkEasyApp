package com.susano.WalkEasy.ESP_Cam;


import org.java_websocket.WebSocket;

import java.util.Objects;


public class cameraView {


    public boolean which_camera(String descriptor){



        if (Objects.equals(descriptor, "/left")) return true;
        else if (Objects.equals(descriptor, "/right")) return false;


        return false;
    }
}
