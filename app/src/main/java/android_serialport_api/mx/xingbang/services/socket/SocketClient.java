package android_serialport_api.mx.xingbang.services.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SocketClient extends Thread{

	
    private final Socket socket;
    private Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;

    public SocketClient(Socket socket, Handler handler){
        setName("ConnectThread");
       
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {

        if(socket==null){
            return;
        }
        handler.sendEmptyMessage(SocketConant.DEVICE_CONNECTED);
        try {
            //获取数据流
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            
            byte[] buffer = new byte[1024];
            int bytes;
            while (true){
                //读取数据
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    final byte[] data = new byte[bytes];
                    System.arraycopy(buffer, 0, data, 0, bytes);
                    String msg = new String(data);
                    Message message = Message.obtain();
                    message.what = SocketConant.GET_MSG;
                    Bundle bundle = new Bundle();
                    bundle.putString("MSG",msg);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    if(msg.indexOf("1000#")>=0) {                    	
                    	outputStream.write("1000#testNo001\n".getBytes());
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     */
    public void sendData(String msg){
        
        if(outputStream!=null){
            try {
                outputStream.write(msg.getBytes());
               
                Message message = Message.obtain();
                message.what = SocketConant.SEND_MSG_SUCCSEE;
                Bundle bundle = new Bundle();
                bundle.putString("MSG",new String(msg));
                message.setData(bundle);
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = SocketConant.SEND_MSG_ERROR;
                Bundle bundle = new Bundle();
                bundle.putString("MSG",new String(msg));
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }
    }
}
