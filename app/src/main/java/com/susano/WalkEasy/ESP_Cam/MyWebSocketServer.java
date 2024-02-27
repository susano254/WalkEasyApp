package com.susano.WalkEasy.ESP_Cam;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class MyWebSocketServer extends WebSocketServer {
    RenderFrame callBack;

    public MyWebSocketServer(InetSocketAddress address, RenderFrame callBack) {
        super(address);
        this.callBack = callBack;
    }


    @Override
    public void onStart() {
        Log.d("MyServer", "started server socket on port 8000 address: " + getAddress());
    }
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Handle a new connection
        Log.d("MyServer", "New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Handle connection close
        Log.d("MyServer", "Connection closed: " + conn.getRemoteSocketAddress());
    }


    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        super.onMessage(conn, message);
        Log.d("MyServer", "Received message from " + conn.getRemoteSocketAddress() + " message length: " + message.capacity());

        callBack.render(message);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Handle incoming messages from clients
        Log.d("MyServer", "Received message from " + conn.getRemoteSocketAddress() + " message length: " + message.length() + " message: " + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Handle errors
        Log.d("MyServer", "Error on connection " + conn.getRemoteSocketAddress() + ":");
        ex.printStackTrace();
    }
}
