package com.example.a51zonedrone_app;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SendReceive extends Thread{
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String receive;
    private SendReceive sendReceive;

    static final int MESSAGE_READ = 1;

    public String getReceive(){
        return receive;
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    receive = tempMsg;
                    break;
            }
            return true;
        }
    });

    public SendReceive(Socket skt){
        socket = skt;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            if(outputStream == null){
                outputStream = socket.getOutputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while(socket != null){
            try {
                bytes = inputStream.read(buffer);
                if(bytes > 0){
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes){
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}