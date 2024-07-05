package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.util.regex.Pattern;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FiveTestingCmd;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.utils.MmkvUtils;
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
    @BindView(R.id.btn_sys)
    Button btnSys;
    @BindView(R.id.sp_changjia)
    Spinner spChangjia;

    SharedPreferences.Editor edit;
    private Handler Handler_tip = null;//提示信息
    private boolean send_low = true;
    private boolean send_high = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_voltage);
        ButterKnife.bind(this);
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        SharedPreferences sp = getSharedPreferences("config", 0);
        //获取偏好设置的编辑器
        edit = sp.edit();
        //存数据：int long bool string


        //根据key找值，找不到返回一个默认值
        String lowVoltage = sp.getString("lowVoltage", "8.5");
        String highVoltage = sp.getString("highVoltage", "16");
        //赋值给控件
        etSetlowVoltage.setText(lowVoltage);
        etSethighVoltage.setText(highVoltage);
        Handler_tip = new Handler(msg -> {
            Log.e("命令", "msg.arg1: " + msg.arg1);
            if (msg.arg1 == 1) {
                if (msg.obj != null) {
                    show_Toast("设置低压成功,实际低压输出为" + msg.obj + "V");
                } else {
                    show_Toast("设置低压成功");
                }
                send_low = true;
                btnLowVoltage.setEnabled(true);
                btnHighVoltage.setEnabled(true);
                btnLowVoltage.setBackgroundResource(R.drawable.bt_mainpage_style);
            } else if (msg.arg1 == 2) {
                if (msg.obj != null) {
                    show_Toast("设置高压成功,实际低压输出为" + msg.obj + "V");
                } else {
                    show_Toast("设置高压成功");
                }
                send_high = true;
                btnLowVoltage.setEnabled(true);
                btnHighVoltage.setEnabled(true);
                btnHighVoltage.setBackgroundResource(R.drawable.bt_mainpage_style);
            } else if (msg.arg1 == 3) {
                show_Toast("您输入电压不符合规范,请重新输入");
            }
            return false;
        });
        Utils.writeRecord("---进入设置电压页面---");
        Log.e("设置高压", "send_high: " + send_high);




        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sp_changjia, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChangjia.setAdapter(adapter);
        int position_save = (int)MmkvUtils.getcode("sys_ver",0);
        spChangjia.setSelection(position_save);
        spChangjia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 当选择下拉列表中的一个项时，会调用此方法
                // 可以在这里处理选中事件
                String selectedItem = parent.getItemAtPosition(position).toString();
                Log.e("下拉列表", "selectedItem: "+selectedItem );
                Log.e("下拉列表", "position: "+position );
                // 使用选中的值
                MmkvUtils.savecode("sys_ver",position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 无选项被选中时的处理
            }
        });


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
        System.arraycopy(cmdBuf, 0, locatBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(locatBuf);
        String realyCmd1 = DefCommand.decodeCommand(fromCommad);//005B04BD070000
        if (DefCommand.CMD_5_TEST_8.equals(cmd)) {//5B设置低压
            //普通版本 C0005B00D91EC0
            //调整版本 C0005B04410600FF867FC0
            Message msg = Handler_tip.obtainMessage();
            if (size > 7) {
                //芯片设置值(目前为0)
                String b = realyCmd1.substring(10, 12);
                String b1 = realyCmd1.substring(12, 14);
                String strLow = realyCmd1.substring(6, 8);
                String strHigh = realyCmd1.substring(8, 10);
                int volthigh = Integer.parseInt(strHigh, 16) * 256;
                int voltLowInt = Integer.parseInt(strLow, 16);
                //可调电压版本,系数为0.011,不可调为0.006
                double voltTotal = (volthigh + voltLowInt) * 3.0 * 11 / 4.096 / 1000;//新芯片
                msg.obj = Utils.getFloatToFormat((float) voltTotal, 2, 4);
            }
            msg.arg1 = 1;
            Handler_tip.sendMessage(msg);
            Log.e("命令", "send_low: " + send_low);
        } else if (DefCommand.CMD_5_TEST_9.equals(cmd)) {//5C设置高压
            Message msg = Handler_tip.obtainMessage();
            if (size > 7) {
                //芯片设置值(目前为0)
                String b = realyCmd1.substring(10, 12);
                String b1 = realyCmd1.substring(12, 14);

                String strLow = realyCmd1.substring(6, 8);
                String strHigh = realyCmd1.substring(8, 10);
                int volthigh = Integer.parseInt(strHigh, 16) * 256;
                int voltLowInt = Integer.parseInt(strLow, 16);
                //可调电压版本,系数为0.011,不可调为0.006
                double voltTotal = (volthigh + voltLowInt) / 4.095 * 3.0 * 0.011;//普通版本
                msg.obj = Utils.getFloatToFormat((float) voltTotal, 2, 4);
            }

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

    public static boolean isNumber(String str) {
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);

    }

    public final static boolean isNumericT(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();

    }

    @OnClick({R.id.btn_lowVoltage, R.id.btn_highVoltage, R.id.btn_sys})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_lowVoltage://设置低压
                if (send_low) {
                    btnLowVoltage.setEnabled(false);
                    btnHighVoltage.setEnabled(false);
                    btnLowVoltage.setBackgroundResource(R.drawable.bt_mainpage_style_2);
                    String str_lowVoltage = etSetlowVoltage.getText().toString();
                    int low = Double.valueOf(Utils.convertToDouble(str_lowVoltage, 7.0) * 10).intValue();
                    Log.e("设置电压", "low: " + low);
                    Utils.writeRecord("-设置低压:" + low);
                    if (isNumber(str_lowVoltage) && low > 60 && low < 120) {
                        edit.putString("lowVoltage", str_lowVoltage);
                        edit.commit();//点击提交编辑器
                        byte[] delayBye = Utils.shortToByte((short) low);
                        String delayStr = Utils.bytesToHexFun(delayBye);
                        String b = delayStr.substring(0, 2);
                        String c = delayStr.substring(2);
                        byte[] reCmd1 = FourStatusCmd.setToXbCommon_SetLowVoltage("00", c + b);//
                        sendCmd(reCmd1);
                        send_low = false;
                        Log.e("设置低压", " c+b: " + c + b);
                    } else {
                        Message msg = Handler_tip.obtainMessage();
                        msg.arg1 = 3;
                        Handler_tip.sendMessage(msg);
                    }
                } else {
                    show_Toast("请等待单片机返回");
                }


                break;
            case R.id.btn_highVoltage://设置高压
                Log.e("设置高压", "send_high: " + send_high);
                if (send_high) {
                    String str_highVoltage = etSethighVoltage.getText().toString();
                    int high = Double.valueOf(Utils.convertToDouble(str_highVoltage, 16.0) * 10).intValue();
                    Log.e("设置电压", "high: " + high);
                    Utils.writeRecord("-设置高压:" + high);
                    if (isNumber(str_highVoltage) && high > 100 && high < 200) {
                        edit.putString("highVoltage", str_highVoltage);
                        edit.commit();//点击提交编辑器
                        byte[] delayBye = Utils.shortToByte((short) high);
                        String delayStr = Utils.bytesToHexFun(delayBye);
                        String b = delayStr.substring(0, 2);
                        String c = delayStr.substring(2);
                        sendCmd(FourStatusCmd.setToXbCommon_SetHighVoltage("00", c + b));//5C
                        Log.e("设置高压", " c+b: " + c + b);
                        send_high = false;
                    } else {
                        Message msg = Handler_tip.obtainMessage();
                        msg.arg1 = 3;
                        Handler_tip.sendMessage(msg);
                    }
                } else {
                    show_Toast("请等待单片机返回");
                }

                break;
            case R.id.btn_sys://保存设置
                break;
        }
    }

    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication.closeSerialPort();
                Log.e("SetVoltageActivity","调用mApplication.closeSerialPort()开始关闭串口了。。");
                mSerialPort = null;
            }
        }).start();
        super.onDestroy();
    }
}
