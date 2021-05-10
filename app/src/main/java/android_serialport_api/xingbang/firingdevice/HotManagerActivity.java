package android_serialport_api.xingbang.firingdevice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.DeviceControl;
import android.text.InputType;
import android.text.TextUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.services.socket.DenatorClient;
import android_serialport_api.xingbang.services.socket.SocketClient;
import android_serialport_api.xingbang.services.socket.SocketConant;
import android_serialport_api.xingbang.services.socket.SocketService;
import android_serialport_api.xingbang.services.wifi.ConnectThread;
import android_serialport_api.xingbang.services.wifi.ListenerThread;
import android_serialport_api.xingbang.services.wifi.WifiListAdapter;


public class HotManagerActivity extends Activity {
	private ListView listView;
    private Button btn_start_serice;
    private Button btn_close;
    
    private Button btn_connect;
    private Button btn_test;
    private TextView textview;
    private TextView text_state;
    private int sendCount=0;
    private WifiManager wifiManager;
    private WifiListAdapter wifiListAdapter;
    private WifiConfiguration config;
    private int wcgID;

  
    private static final int WIFICIPHER_NOPASS = 1;
    private static final int WIFICIPHER_WEP = 2;
    private static final int WIFICIPHER_WPA = 3;


    /**
     * 连接线程
     */
    private ConnectThread connectThread;

    /**
     * 监听线程
     */
    //private ListenerThread listenerThread;
    private SocketService socketService;
    private  SocketClient socketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificlient);
        initVIew();
       // initBroadcastReceiver();

        //wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //listenerThread = new ListenerThread(PORT, handler);
       // listenerThread.start();
    }
    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        registerReceiver(receiver, intentFilter);
    }
    private void initVIew() {
        listView = (ListView) findViewById(R.id.wificlient_query_listview);
        
        btn_start_serice = (Button) findViewById(R.id.btn_start_serice);
        
        btn_close = (Button) findViewById(R.id.btn_return);
        
        btn_connect = (Button) findViewById(R.id.btn_connect_serice);
        btn_test = (Button) findViewById(R.id.btn_connect_test);
        
        textview = (TextView) findViewById(R.id.textview);
        text_state = (TextView) findViewById(R.id.text_state);
        
        btn_start_serice.setOnClickListener(new View.OnClickListener() {
	           @Override
	           public void onClick(View v) {	              

	        	   if(socketService==null){
		           		socketService = new SocketService(handler);
		           		socketService.start();
		           	
		           		btn_start_serice.setText("停止服务");
	           		}else{
	           			
	           			socketService.closeSockService();
	           			socketService.exit = true;
	           			socketService = null;
	           			btn_start_serice.setText("启动服务");
	           		}
	        	   
	           }
	       });
        btn_close.setOnClickListener(new View.OnClickListener() {
	           @Override
	           public void onClick(View v) {	              
	        	   Intent intentTemp = new Intent();
	               intentTemp.putExtra("backString","");
	               setResult(1,intentTemp);
	               finish();
	           }
	       });
        
        btn_connect.setOnClickListener(new View.OnClickListener() {
	           @Override
	           public void onClick(View v) {	              
	        	   initWifi();
	           }
	       });
        btn_test.setOnClickListener(new View.OnClickListener() {
	           @Override
	           public void onClick(View v) {	              
	        	   Intent intentTemp = new Intent();
	               intentTemp.putExtra("backString","");
	               setResult(1,intentTemp);
	               finish();
	           }
	       });
        
        wifiListAdapter = new WifiListAdapter(this, R.layout.wifi_list_item);
        
        listView.setAdapter(wifiListAdapter);
        DenatorClient c = new DenatorClient();
        c.setEquNo("333");
c.setSerial(12);
        wifiListAdapter.add(c);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               
                
            }
        });
    }

    private void initWifi(){
    	
    	initBroadcastReceiver();

        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        
         int type = WIFICIPHER_WPA;
         ScanResult sr1 = null;
		Constructor<ScanResult> ctor;
		try {
			ctor = ScanResult.class.getDeclaredConstructor(ScanResult.class);
			ctor.setAccessible(true);
	        ScanResult sr = ctor.newInstance(sr1);
	        sr.BSSID = SocketConant.BSSID;        	
	        sr.SSID = SocketConant.BSSID;
	        
	    	config = isExsits(sr.SSID);
	        if (config == null) {
	        	config = createWifiInfo(sr.SSID, "123456789", type);
	        	//connect(config);
	        }
	        connect(config);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        	
    }
    private void connect(WifiConfiguration config) {
    	
        text_state.setText("连接中...");
        wcgID = wifiManager.addNetwork(config);
        wifiManager.enableNetwork(wcgID, true);
    }


/**
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_hostspot:
                createWifiHotspot();
                break;
            case R.id.btn_close_hostspot:
                //closeWifiHotspot();
            	socketService.sendmsg("hello--"+sendCount++);
                break;
            case R.id.btn_send:
            	socketClient.sendData("这是来自息---1111");
            	
                break;
            case R.id.btn_search:
                search();
                break;
        }
    }
**/
    /**
     * 创建Wifi热点
     */
    private void createWifiHotspot() {
    	
    	
    	/***
        if (wifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            wifiManager.setWifiEnabled(false);
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = WIFI_HOTSPOT_SSID;
        config.preSharedKey = "123456789";
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        //通过反射调用设置热点
        try {
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean enable = (Boolean) method.invoke(wifiManager, config, true);
            if (enable) {
                textview.setText("热点已开启 SSID:" + WIFI_HOTSPOT_SSID + " password:123456789");
            } else {
                textview.setText("创建热点失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            textview.setText("创建热点失败");
        }**/
    }

    /**
     * 关闭WiFi热点
     */
    public void closeWifiHotspot() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
            Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(wifiManager, config, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        textview.setText("热点已关闭");
        text_state.setText("wifi已关闭");
    }

    /**
     * 获取连接到热点上的手机ip
     *
     * @return
     */
    private String getConnectedIP() {
      //  ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    if(ip!=null&&ip.indexOf(".")>0)
                    	return ip;
                   // connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 搜索wifi热点
     */
    private void search() {
        if (!wifiManager.isWifiEnabled()) {
            //开启wifi
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
		@Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SocketConant.DEVICE_CONNECTING:
                  //  connectThread = new ConnectThread(listenerThread.getSocket(),handler);
                   // connectThread.start();
                    break;
                case SocketConant.DEVICE_CONNECTED:
                    textview.setText("设备连接成功");
                    break;
                case SocketConant.SEND_MSG_SUCCSEE:
                    textview.setText("发送消息成功:" + msg.getData().getString("MSG"));
                    break;
                case SocketConant.SEND_MSG_ERROR:
                    textview.setText("发送消息失败:" + msg.getData().getString("MSG"));
                    break;
                case SocketConant.GET_MSG:
                    textview.setText("收到消息:" + msg.getData().getString("MSG"));
                    break;
                case SocketConant.HAVE_CLIENT:
                	wifiListAdapter.clear();
                	wifiListAdapter.addAll(socketService.getWifiClientList());
                	break;
            }
        }
    };
  
    	    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
               // Log.w("BBB", "SCAN_RESULTS_AVAILABLE_ACTION");
                // wifi已成功扫描到可用wifi。
              ///  List<ScanResult> scanResults = wifiManager.getScanResults();
              //  wifiListAdapter.clear();
             //   wifiListAdapter.addAll(scanResults);
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
               // Log.w("BBB", "WifiManager.WIFI_STATE_CHANGED_ACTION");
                int wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        //获取到wifi开启的广播时，开始扫描
                        wifiManager.startScan();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        //wifi关闭发出的广播
                        break;
                }
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                //Log.w("BBB", "WifiManager.NETWORK_STATE_CHANGED_ACTION");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    text_state.setText("连接已断开");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    DhcpInfo reinfo = wifiManager.getDhcpInfo();
                    final String serverAddress = SocketConant.intToIp(reinfo.serverAddress);
                    text_state.setText("已连接到网络:" + wifiInfo.getSSID());
                    String ssid = wifiInfo.getSSID();
                    ssid= ssid.replaceAll("\"", "");
                    //Log.w("AAA","wifiInfo.getSSID():"+wifiInfo.getSSID()+"  WIFI_HOTSPOT_SSID:"+WIFI_HOTSPOT_SSID);
                    if (ssid.trim().equals(SocketConant.BSSID)) {
                        //如果当前连接到的wifi是热点,则开启连接线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                   // ArrayList<String> connectedIP = getConnectedIP();
                                   // wifiInfo.getIpAddress();
                                	//String connectedIP = getConnectedIP();
                                	
                                	

                                   // String ip=getConnectedIP();//SocketConant.intToIp(wifiInfo.getIpAddress()); 
                                   if(serverAddress!=null){
                                	   Socket socket = new Socket(serverAddress, SocketConant.PORT);
                                    
                                	   socketClient = new SocketClient(socket,handler);
                                	   socketClient.start();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } else {
                    NetworkInfo.DetailedState state = info.getDetailedState();
                    if (state == state.CONNECTING) {
                        text_state.setText("连接中...");
                    } else if (state == state.AUTHENTICATING) {
                        text_state.setText("正在验证身份信息...");
                    } else if (state == state.OBTAINING_IPADDR) {
                        text_state.setText("正在获取IP地址...");
                    } else if (state == state.FAILED) {
                        text_state.setText("连接失败");
                    }
                }

            }
           /* else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                    text_state.setText("连接已断开");
                    wifiManager.removeNetwork(wcgID);
                } else {
                    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    text_state.setText("已连接到网络:" + wifiInfo.getSSID());
                }
            }*/
        }
    };



    /**
     * 判断当前wifi是否有保存
     *
     * @param SSID
     * @return
     */
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public WifiConfiguration createWifiInfo(String SSID, String password,
                                            int type) {
       // Log.w("AAA", "SSID = " + SSID + "password " + password + "type ="
           //     + type);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (type == WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
    }
}
