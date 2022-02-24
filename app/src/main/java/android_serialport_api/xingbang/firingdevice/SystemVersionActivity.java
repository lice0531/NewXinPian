package android_serialport_api.xingbang.firingdevice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigInteger;
import java.util.regex.Pattern;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemVersionActivity extends SerialPortActivity {

    SharedPreferences.Editor edit;
    @BindView(R.id.btn_Soft_Version)
    TextView btnSoftVersion;
    @BindView(R.id.btn_Hardware_Version)
    TextView btnHardwareVersion;
    @BindView(R.id.et_Hardware_Version)
    EditText etHardwareVersion;
    @BindView(R.id.set_Hardware_Version)
    Button setHardwareVersion;
    private Handler Handler_tip = null;//提示信息


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_version);//version
        ButterKnife.bind(this);

        SharedPreferences sp = getSharedPreferences("config", 0);
        //获取偏好设置的编辑器
        edit = sp.edit();
        //赋值给控件
        Handler_tip = new Handler(msg -> {
            Bundle b = msg.getData();
            String ver = b.getString("版本号");
            if (msg.what == 1) {
                btnSoftVersion.setText(msg.obj.toString());
            } else if(msg.what == 2){
                btnHardwareVersion.setText(msg.obj.toString());
            }else {
                show_Toast("切换的单片机本号:"+msg.obj.toString());
            }
            return false;
        });
        byte[] reCmd1 = FourStatusCmd.getSoftVersion("00");//43
        sendCmd(reCmd1);
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd(fromCommad);
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                    doWithReceivData(cmd, localBuf, localSize);
                }
            }
        }
    }

    /***
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf, int size) {
        byte[] locatBuf = new byte[size];
        System.arraycopy(cmdBuf, 0, locatBuf, 0, size);//将cmdBuf数组复制到locatBuf数组
        String fromCommad =  Utils.bytesToHexFun(locatBuf);
        String realyCmd1 = DefCommand.decodeCommand(fromCommad);
        if (DefCommand.CMD_4_XBSTATUS_4.equals(cmd)) {//获取软件版本号 43
            Log.e("软件版本返回的命令", "realyCmd1: "+realyCmd1 );
            String a =realyCmd1.substring(6);//2020031201
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < a.length(); i+=2) {
                String str = a.substring(i, i+2);
                output.append((char)Integer.parseInt(str, 16));
            }
            Log.e("软件版本返回的命令", "output: "+output);

            MmkvUtils.savecode("yj_version",output);
            Handler_tip.sendMessage(Handler_tip.obtainMessage(1, output));
            byte[] reCmd2 = FourStatusCmd.getHardVersion("00");//44
            sendCmd(reCmd2);
        } else if (DefCommand.CMD_4_XBSTATUS_5.equals(cmd)) {//获取硬件版本号 44
            String a =realyCmd1.substring(6);//2020031201
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < a.length(); i+=2) {
                String str = a.substring(i, i+2);
                output.append((char)Integer.parseInt(str, 16));
            }
            Handler_tip.sendMessage(Handler_tip.obtainMessage(2, output));
        }else if (DefCommand.CMD_4_XBSTATUS_6.equals(cmd)) {//设置单片机版本
            String a =realyCmd1.substring(6);//2020031201
            String data1=a.substring(0,2);
            String data2=a.substring(2,4);
            String data3=a.substring(4,6);
            String data4=a.substring(6);
            String data=data4+data3+data2+data1;
            int c = new BigInteger(data, 16).intValue();

            Log.e("软件版本返回的命令", "a: "+a );
            Log.e("软件版本返回的命令", "c: "+c );
            Handler_tip.sendMessage(Handler_tip.obtainMessage(3, c));
        }
    }

    //发送命令
    public synchronized void sendCmd(byte[] mBuffer) {//0627添加synchronized,尝试加锁
        if (mSerialPort != null && mOutputStream != null) {
            try {
//					mOutputStream.write(mBuffer);
                String str = Utils.bytesToHexFun(mBuffer);
//                Utils.writeLog("Reister sendTo:" + str);
                Log.e("发送命令", "sendCmd: " + str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    public static boolean isNumber(String str) {
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);

    }

    public final static boolean isNumericT(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();

    }


    @OnClick({R.id.btn_Soft_Version, R.id.btn_Hardware_Version, R.id.set_Hardware_Version})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_Hardware_Version:
                if(etHardwareVersion.getText().toString().length()!=10){
                    show_Toast("版本号长度错误,请输入10位版本号");
                    break;
                }
                String version=  etHardwareVersion.getText().toString();
                String strHex = Integer.toHexString(Integer.parseInt(version)).toUpperCase();
                Log.e("16进制", "strHex: "+strHex );
                //反转版本
                String strHex_fan = strHex.substring(6) + strHex.substring(4, 6) + strHex.substring(2, 4) + strHex.substring(0, 2);
                Log.e("16进制", "strHex_fan: "+strHex_fan );
                byte[] reCmd3 = FourStatusCmd.setHardVersion("00", strHex_fan);//
                sendCmd(reCmd3);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
