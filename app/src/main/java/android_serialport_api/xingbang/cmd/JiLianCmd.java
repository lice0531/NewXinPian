package android_serialport_api.xingbang.cmd;

import android.util.Log;

public class JiLianCmd {


    /**
     * 7.0、级联,发送
     * @param addr
     * @param data
     * @return
     */
    public static byte[] send70(String addr,String data){
        String command = addr +  DefCommand.CMD_7_70+"01"+data;
        return DefCommand.getCommadBytes(command);
    }

    public static byte[] send71(String addr,String data){
        String command = addr +  DefCommand.CMD_7_71+data;
        return DefCommand.getCommadBytes(command);
    }

    public static byte[] send72(String addr,String data){
        String command = addr +  DefCommand.CMD_7_72+data;
        return DefCommand.getCommadBytes(command);
    }

    public static byte[] send73(String addr,String data){
        String command = addr +  DefCommand.CMD_7_73+data;
        return DefCommand.getCommadBytes(command);
    }

    /***
     * 解析5B
     * @param realyCmd1
     * @return
     */
    private static String decode70(String realyCmd1){

        String dataHex =  realyCmd1.substring(4, 15);//取得返回数据
        Log.e("解析70", "dataHex: "+dataHex );
        return "0";
    }
}
