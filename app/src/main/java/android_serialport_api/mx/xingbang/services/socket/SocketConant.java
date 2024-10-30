package android_serialport_api.mx.xingbang.services.socket;

public class SocketConant {

	public static final int PORT = 9999;
    public static final int DEVICE_CONNECTING = 1;//有设备正在连接热点
    public static final int DEVICE_CONNECTED = 2;//有设备连上热点
    public static final int SEND_MSG_SUCCSEE = 3;//发送消息成功
    public static final int SEND_MSG_ERROR = 4;//发送消息失败
    public static final int GET_MSG = 6;//获取新消息
    public static final int HAVE_CLIENT = 60;//有客户端接入
    
    public static final String BSSID="SINOHOT0001";
    
    public static String intToIp(int i)  {
    	return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "."+((i >> 24 ) & 0xFF);
    }
}
