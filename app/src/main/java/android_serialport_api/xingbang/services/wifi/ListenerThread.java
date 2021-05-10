package android_serialport_api.xingbang.services.wifi;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android_serialport_api.xingbang.firingdevice.HotManagerActivity;
import android_serialport_api.xingbang.services.socket.SocketConant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 监听线程
 * Created by 坤 on 2016/9/7.
 */
public class ListenerThread extends Thread{

    private ServerSocket serverSocket = null;
    private Handler handler;
    private int port;
    private Socket socket;

    public ListenerThread(int port, Handler handler){
        setName("ListenerThread");
        this.port = port;
        this.handler = handler;
        try {
            serverSocket=new ServerSocket(port);//监听本机的12345端口
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true){
            try {
                 Log.w("AAA","阻塞");
                //阻塞，等待设备连接
                socket = serverSocket.accept();
                Message message = Message.obtain();
                message.what = SocketConant.DEVICE_CONNECTING;
                handler.sendMessage(message);
            } catch (IOException e) {
                Log.w("AAA","error:"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
