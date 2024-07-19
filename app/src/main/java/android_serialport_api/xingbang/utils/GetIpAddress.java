package android_serialport_api.xingbang.utils;

import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by suwen on 2018/4/8.
 */

public class GetIpAddress {
    public static String IP;
    public static int PORT;

    public static String getIP() {
        return IP;
    }

    public static int getPort() {
        return PORT;
    }

    public static void getLocalIpAddress(ServerSocket serverSocket) {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    String mIP = inetAddress.getHostAddress().substring(0, 3);
                    if (mIP.equals("192")) {
                        IP = inetAddress.getHostAddress();    //获取本地IP
                        PORT = serverSocket.getLocalPort();    //获取本地的PORT
                        Log.e("本机IP",IP + "  " + PORT);//127.0.0.1
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }
}
