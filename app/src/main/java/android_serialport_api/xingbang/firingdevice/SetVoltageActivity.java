package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.regex.Pattern;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FiveTestingCmd;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetVoltageActivity extends SerialPortActivity {

    @BindView(R.id.et_set_lowVoltage)
    EditText etSetlowVoltage;
    @BindView(R.id.btn_lowVoltage)
    Button btnLowVoltage;
    @BindView(R.id.et_set_highVoltage)
    EditText etSethighVoltage;
    @BindView(R.id.btn_highVoltage)
    Button btnHighVoltage;

    SharedPreferences.Editor edit;
    private Handler Handler_tip = null;//提示信息
    private boolean send_low=true;
    private boolean send_high=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_voltage);
        ButterKnife.bind(this);

        SharedPreferences sp = getSharedPreferences("config", 0);
        //获取偏好设置的编辑器
        edit = sp.edit();
        //存数据：int long bool string


        //根据key找值，找不到返回一个默认值
        String lowVoltage = sp.getString("lowVoltage", "7");
        String highVoltage = sp.getString("highVoltage", "16");
        //赋值给控件
        etSetlowVoltage.setText(lowVoltage);
        etSethighVoltage.setText(highVoltage);
        Handler_tip = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();
                String shellStr = b.getString("shellStr");
                Log.e("命令", "msg.arg1: "+msg.arg1 );
                if (msg.arg1 == 1) {
                    show_Toast("设置低压成功");
                    send_low=true;
                    btnLowVoltage.setEnabled(true);
                    btnHighVoltage.setEnabled(true);
                    btnLowVoltage.setBackgroundResource(R.drawable.bt_mainpage_style);
                }else if(msg.arg1 == 2) {
                    show_Toast("设置高压成功");
                    send_high=true;
                    btnLowVoltage.setEnabled(true);
                    btnHighVoltage.setEnabled(true);
                    btnHighVoltage.setBackgroundResource(R.drawable.bt_mainpage_style);
                }else if(msg.arg1 == 3) {
                    show_Toast("您输入电压不符合规范,请重新输入");
                }else {
                    show_Toast("设置高压成功");
                }


            }
        };
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
                    Log.e("返回命令","fromCommad : "+ fromCommad +"  cmd : "+cmd);
                    doWithReceivData(cmd, localBuf,localSize);
                }
            }
        }
    }
    /***
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf, int size) {
        byte[] locatBuf = new byte[size];
        System.arraycopy(cmdBuf, 0, locatBuf, 0, size);
        if (DefCommand.CMD_5_TEST_8.equals(cmd)) {//5B设置低压
            //普通版本 C0005B00D91EC0
            //调整版本 C0005B04 41 06 00 FF 867F C0
            String a =FiveTestingCmd.decodeCmd5B("00", locatBuf);

            Message msg = Handler_tip.obtainMessage();
            msg.arg1 = 1;
            Handler_tip.sendMessage(msg);

            Log.e("命令", "send_low: "+send_low );
        }else  if(DefCommand.CMD_5_TEST_9.equals(cmd)){//5C设置高压
            Message msg = Handler_tip.obtainMessage();
            msg.arg1 = 2;
            Handler_tip.sendMessage(msg);
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
        }
    }
    public static boolean isNumber(String str){
        String reg ="^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);

    }
    public final static boolean isNumericT(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();

    }

    @OnClick({R.id.btn_lowVoltage, R.id.btn_highVoltage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_lowVoltage://设置低压
                if(send_low){
                    btnLowVoltage.setEnabled(false);
                    btnHighVoltage.setEnabled(false);
                    btnLowVoltage.setBackgroundResource(R.drawable.bt_mainpage_style_2);
                    String str_lowVoltage=  etSetlowVoltage.getText().toString();
                    int low = Double.valueOf(Utils.convertToDouble(str_lowVoltage,7.0)*10).intValue();
                    Log.e("设置电压", "low: "+low );
                    if(isNumber(str_lowVoltage)&& low> 60 && low< 120){
                        edit.putString("lowVoltage", str_lowVoltage);
                        edit.commit();//点击提交编辑器
                        byte[] delayBye = Utils.shortToByte((short) low);
                        String delayStr = Utils.bytesToHexFun(delayBye);
                        String b=delayStr.substring(0,2);
                        String c=delayStr.substring(2);
                        byte[] reCmd1 = FourStatusCmd.setToXbCommon_SetLowVoltage("00", c+b);//
                        sendCmd(reCmd1);
                        send_low=false;
                        Log.e("设置低压", " c+b: "+ c+b);
                    }else {
                        Message msg = Handler_tip.obtainMessage();
                        msg.arg1 = 3;
                        Handler_tip.sendMessage(msg);
                    }
                }else {
                    show_Toast("请等待单片机返回");
                }


                break;
            case R.id.btn_highVoltage://设置高压
                if(send_high){
                    String str_highVoltage=  etSethighVoltage.getText().toString();
                    int high = Double.valueOf(Utils.convertToDouble(str_highVoltage,16.0)*10).intValue();
                    Log.e("设置电压", "low: "+high );
                    if(isNumber(str_highVoltage)&&high>100&&high<200){
                        edit.putString("highVoltage", str_highVoltage);
                        edit.commit();//点击提交编辑器
                        byte[] delayBye = Utils.shortToByte((short) high);
                        String delayStr = Utils.bytesToHexFun(delayBye);
                        String b=delayStr.substring(0,2);
                        String c=delayStr.substring(2);
                        byte[] reCmd1 = FourStatusCmd.setToXbCommon_SetHighVoltage("00",  c+b);
                        sendCmd(reCmd1);
                        Log.e("设置高压", " c+b: "+ c+b);
                        send_high=false;
                    }else {
                        Message msg = Handler_tip.obtainMessage();
                        msg.arg1 = 3;
                        Handler_tip.sendMessage(msg);
                    }
                }else {
                    show_Toast("请等待单片机返回");
                }

                break;
        }
    }
}
