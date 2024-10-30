package android_serialport_api.mx.xingbang.services.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SocketService extends Thread{
	private Handler servicehandler;
    public List<DenatorClient> mList = new ArrayList<DenatorClient>();
    //WifiListAdapter wifiList =  null;
    private ServerSocket server;
    private ExecutorService mExecutorService = null; //thread pool
    public volatile boolean exit = false; 
    private String msg = "";
    
    public SocketService(Handler handler) {
        try {        	
        	//String ip = getWifiApIpAddress();
        	server = new ServerSocket(SocketConant.PORT);;
        	servicehandler = handler;
            mExecutorService = Executors.newCachedThreadPool();  //create a thread pool
            System.out.print("服务器已启动...");
           
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  List<DenatorClient> getWifiClientList(){
    	
    	return mList;
    }
    public void closeSockService(){
    	if(server!=null){
    		try {
				  server.close();
				  int num =mList.size();
				  for (int index = 0; index < num; index ++) {
			    	   DenatorClient cl = mList.get(index);
			    	   ClientService cs= cl.getClient();
			           Socket mSocket = cs.socket;
			           try {
			        	   mSocket.close();
			           }catch (IOException e) {
			               e.printStackTrace();
			           }
			       }				  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
    	}
    }
    public String getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                           
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
           // Log.e("Main", ex.toString());
        }
        return null;
    }
    @Override  
    public void run() {  
    	try {
	    	 Socket client = null;
	    	 while(!exit) {
	             
					client = server.accept();
					  //把客户端放入客户端集合中
	                 DenatorClient cl = new DenatorClient();
	                 
	                 ClientService clientServ = new ClientService(client) ;
	                 cl.setClient(clientServ);
	                 cl.setIp(client.getInetAddress().getHostAddress());
	                 mList.add(cl);
	                // wifiList.addAll(mList);
	                 mExecutorService.execute(clientServ); //start a new thread to handle the connection
	                //sleep(10);
	                 sentToClient(client,"1000#");
	                 
			 }
    	 } catch (IOException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
		}
			                    
    }  
    public void sendmsg(String msg) {
    	this.msg = msg;
    	sendmsg();
    }
    public void sentToClient(Socket mSocket,String info){
    	PrintWriter pout = null;
        try {
            pout = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(mSocket.getOutputStream())),true);
            pout.println(info);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
   //循环遍历客户端集合，给每个客户端都发送信息。
   public void sendmsg() {
       System.out.println(msg);
       int num =mList.size();
       for (int index = 0; index < num; index ++) {
    	   DenatorClient cl = mList.get(index);
    	   ClientService cs= cl.getClient();
           Socket mSocket = cs.socket;
           sentToClient(mSocket,msg);
           
       }
   }
   
   void shutdownAndAwaitTermination(ExecutorService pool) {
	   pool.shutdown(); // Disable new tasks from being submitted
	   try {
		   // Wait a while for existing tasks to terminate
		   if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
			   pool.shutdownNow(); // Cancel currently executing tasks
			   // Wait a while for tasks to respond to being cancelled
		   if (!pool.awaitTermination(60, TimeUnit.SECONDS))
			   System.err.println("Pool did not terminate");
		   }
	   } catch (InterruptedException ie) {
		   // (Re-)Cancel if current thread also interrupted
		   pool.shutdownNow();
		   // Preserve interrupt status
		   Thread.currentThread().interrupt();
	   }
	}
   
   public DenatorClient getClient(String ip) {
       
       int num =mList.size();
       for (int index = 0; index < num; index ++) {
    	   DenatorClient cl = mList.get(index);
    	   if(ip!=null&&ip.indexOf(cl.getIp())>=0)return cl;           
       }
       return null  ;
    }
   public void setClient(String ip,String equNo) {
       
       int num =mList.size();
       for (int index = 0; index < num; index ++) {
    	   DenatorClient cl = mList.get(index);
    	   if(ip!=null&&ip.indexOf(cl.getIp())>=0){
    		    cl.setEquNo(equNo);
    		    return;
    	   }           
       }
       return   ;
    }
   public String getRev(String ip,String revStr){
	   Message message = Message.obtain();
	   if(revStr.indexOf("1000#")>=0){
		   
			   String equNo = revStr.replaceAll("1000#", "");
			   setClient(ip,equNo);
			   message.what = SocketConant.HAVE_CLIENT;			  
		       servicehandler.sendMessage(message);
	   }else{
		   message.what = SocketConant.GET_MSG;
		   Bundle bundle = new Bundle();
	       bundle.putString("MSG",revStr);
	       message.setData(bundle);
	       servicehandler.sendMessage(message);
       }
      
	   return revStr;
   }
    class ClientService implements Runnable {
            private Socket socket;
            private BufferedReader in = null;
                        
            public ClientService(Socket socket) {
                this.socket = socket;
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //客户端只要一连到服务器，便向客户端发送下面的信息。
                    msg = "user" +this.socket.getInetAddress() + "come toal:"
                        +mList.size();
                  //  this.sendmsg();
                } catch (IOException e) {
                    e.printStackTrace();
                }           
            }
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    while(!isInterrupted() && !exit) {
                        if((msg = in.readLine())!= null) {
                            //当客户端发送的信息为：exit时，关闭连接
                            if(msg.equals("exit")) {
                                System.out.println("ssssssss");
                                mList.remove(socket);
                                in.close();
                                msg = "user:" + socket.getInetAddress()
                                    + "exit total:" + mList.size();
                                socket.close();
                               // this.sendmsg();
                                getRev(socket.getInetAddress().getHostAddress(),msg);
                                break;
                                //接收客户端发过来的信息msg，然后发送给客户端。
                            } else {
                               // msg = socket.getInetAddress() + ":" + msg;
                                //this.sendmsg();
                            	getRev(socket.getInetAddress().getHostAddress(),msg);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
           
        }    
}