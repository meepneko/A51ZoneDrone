package com.example.a51zonedrone_app.WiFiDirect;

import com.example.a51zonedrone_app.Controller.controllerpage_waypoint;
import com.example.a51zonedrone_app.Drone.dronepage_on_flight;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendReceiveThread extends Thread {
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;

    SendReceiveThread(Socket socket)
    {
        this.socket=socket;

        try {
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] a=new byte[1024];
        int byteCount;
        while (socket!=null)
        {
            try {
                byteCount=inputStream.read(a);
                if(byteCount>0)
                {
                    if(dronepage_on_flight.handler != null )dronepage_on_flight.handler.obtainMessage(1,byteCount,-1,a).sendToTarget();
                    if(controllerpage_waypoint.handler != null )controllerpage_waypoint.handler.obtainMessage(1,byteCount,-1,a).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes)
    {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}