package android_serialport_api.mx.xingbang.services.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

public class ChinaFireSocketClient extends Thread{

	
    private final Socket socket;
    private Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int responFlag =0 ;
    private String resposeText ="";
    private int dataCount = 0;
    private boolean exitFlag = false;
    private List<String> cmdDatas = new ArrayList<String>();
    public int getResponFlag(){
    	return responFlag;
    }
    public String getResposeText(){
    	return resposeText;
    }
    public ChinaFireSocketClient(Socket socket, Handler handler){
        setName("ConnectThread");
       
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {

        if(socket==null){
            return;
        }
       // handler.sendEmptyMessage(SocketConant.DEVICE_CONNECTED);
        try {
            //获取数据流
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            
            byte[] buffer = new byte[1024];
            int bytes;
            while (exitFlag ==false){
            	if(inputStream.available() != 0){
                //读取数据
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    final byte[] data = new byte[bytes];
                    System.arraycopy(buffer, 0, data, 0, bytes);
                    
                    
                    String msg = new String(data);
                   
                    System.out.println(msg);
                  //  resposeText =msg;
                	//responFlag = 2;
                    //if(handler!=null)handler.sendMessage(message);
                    if(dataCount<cmdDatas.size()){
	                	String str = cmdDatas.get(dataCount);
	                	dataCount++;
	                	try {
	        				outputStream.write(str.getBytes());
	        			} catch (IOException e) {
	        				// TODO Auto-generated catch block
	        				e.printStackTrace();
	        			}
                    }else{
                    	responFlag=2;
                    }

                }
            }}
        } catch (IOException e) {
            //e.printStackTrace();
        	try {
                if (inputStream != null) {
                	inputStream.close();
                }
                if (socket != null)
                    socket.close();
            } catch (Exception ex) {
            	
            }

        }
    }
    public void stopSocket(){
    	
    	if(socket!=null)
			try {
				exitFlag = true;
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    }
    /**
     * 发送数据
     */
    public void sendData(List<String> datas){
    	cmdDatas.clear();
    	cmdDatas.addAll(datas);
    	responFlag = 1;
        if(outputStream!=null && cmdDatas.size()>0){
        	String data = cmdDatas.get(dataCount);
        	dataCount++;
        	try {
				outputStream.write(data.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    /**
     * 发送数据
     */
    public void sendData(String msg){
        
        if(outputStream!=null){
            try {
            	resposeText ="";
            	responFlag = 1;
            	outputStream.write(msg.getBytes());
              
                
               /*
                Message message = Message.obtain();
                message.what = SocketConant.SEND_MSG_SUCCSEE;
                Bundle bundle = new Bundle();
                bundle.putString("MSG",new String(msg));
                message.setData(bundle);
                if(handler!=null)handler.sendMessage(message);
            	*/
            } catch (IOException e) {
            	/*
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = SocketConant.SEND_MSG_ERROR;
                Bundle bundle = new Bundle();
                bundle.putString("MSG",new String(msg));
                message.setData(bundle);
                if(handler!=null)handler.sendMessage(message);
            	**/
            }
        }
    }
}
