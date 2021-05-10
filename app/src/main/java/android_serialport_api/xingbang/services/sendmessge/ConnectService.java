package android_serialport_api.xingbang.services.sendmessge;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android_serialport_api.xingbang.utils.Utils;

public class ConnectService extends Service {
	private MyBinder mBinder = new MyBinder();
    public static Boolean mainThreadFlag = true;
    public static Boolean ioThreadFlag = true;
    ServerSocket serverSocket = null;
    final int SERVER_PORT = 10081;
    File testFile;
    private sysBroadcastReceiver sysBR;
    private ThreadReadWriterIOSocket clientSocket;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", Thread.currentThread().getName() + "---->" + "  onCreate");
        /* 创建内部类sysBroadcastReceiver 并注册registerReceiver */
        sysRegisterReceiver();
        new Thread() {
            public void run() {
                doListen();
            };
        }.start();
    }

    private void doListen() {
        Log.d("TAG", Thread.currentThread().getName() + "---->"
                + " doListen() START");
        serverSocket = null;
        try {
            Log.d("TAG", Thread.currentThread().getName() + "---->"
                    + " doListen() new serverSocket");
            //Utils.writeLog("00000");
            serverSocket = new ServerSocket(SERVER_PORT);
            //Utils.writeLog("111111");
            boolean mainThreadFlag = true;
            while (mainThreadFlag) {
                Log.d("TAG", Thread.currentThread().getName() + "---->"
                        + " doListen() listen");
              //  Utils.writeLog("2222");
                Socket client = serverSocket.accept();
                if(clientSocket!=null)clientSocket= null;
                
                clientSocket = new ThreadReadWriterIOSocket(this, client);
                new Thread(clientSocket).start();
            }
        } catch (IOException e1) {
            Log.e("TAG", Thread.currentThread().getName()
                    + "---->" + "new serverSocket error");
            e1.printStackTrace();
        }
    }

    /* 创建内部类sysBroadcastReceiver 并注册registerReceiver */
    private void sysRegisterReceiver() {
        Log.e("TAG", Thread.currentThread().getName() + "---->"
                + "sysRegisterReceiver");
        sysBR = new sysBroadcastReceiver();
        /* 注册BroadcastReceiver */
        IntentFilter filter1 = new IntentFilter();
        /* 新的应用程序被安装到了设备上的广播 */
        filter1.addAction("android.intent.action.PACKAGE_ADDED");
        filter1.addDataScheme("package");
        filter1.addAction("android.intent.action.PACKAGE_REMOVED");
        filter1.addDataScheme("package");
        getApplication().registerReceiver(sysBR, filter1);
    }

    /* 内部类：BroadcastReceiver 用于接收系统事件 */
    private class sysBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase("android.intent.action.PACKAGE_ADDED")) {
                // ReadInstalledAPP();
            } else if (action
                    .equalsIgnoreCase("android.intent.action.PACKAGE_REMOVED")) {
                // ReadInstalledAPP();
            }
            Log.e("TAG", Thread.currentThread().getName() + "---->"
                    + "sysBroadcastReceiver onReceive");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 关闭线程
        mainThreadFlag = false;
        ioThreadFlag = false;
        // 关闭服务器
        try {
            Log.e("TAG", Thread.currentThread().getName() + "---->"
                    + "serverSocket.close()");
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("TAG", Thread.currentThread().getName() + "---->"
                + "**************** onDestroy****************");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("TAG", Thread.currentThread().getName() + "---->" + " onStart()");
        super.onStart(intent, startId);

    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d("TAG", "  onBind");
        return mBinder;
      //  return null;
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(clientSocket!=null){
			
		}
		return super.onStartCommand(intent, flags, startId);
	}
	public class MyBinder extends Binder {
        public void StartBB(final String data) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                   
                	if(clientSocket!=null)
                		clientSocket.sendData(data);
                	else{
                		Utils.writeLog(data);
                        EventBus.getDefault().post(new MessageEvent("请检查是否连接到电脑"));
                	}
                    stopSelf();
                }
            }).start();
         
        }
    }
}