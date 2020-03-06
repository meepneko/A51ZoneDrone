package com.example.a51zonedrone_app;

import android.util.Log;
import android.widget.Toast;

import java.net.ServerSocket;
import java.net.Socket;

public class Server_Side_Thread extends Thread {

    Socket socket;
    ServerSocket serverSocket;

    @Override
    public void run() {
        try{
            serverSocket=new ServerSocket(8888);
            socket=serverSocket.accept();
            controllerpage_waypoint.sendReceiveThread=new SendReceiveThread(socket);
            controllerpage_waypoint.sendReceiveThread.start();
        }
        catch (Exception e)
        {
            Log.d("EXception Caught ",e.toString());
        }
    }
}