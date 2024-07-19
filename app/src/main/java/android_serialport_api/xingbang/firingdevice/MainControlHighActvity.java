package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.custom.DeviceAdapter;
import android_serialport_api.xingbang.custom.ListViewForScrollView;
import android_serialport_api.xingbang.models.DeviceBean;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainControlHighActvity extends SerialPortActivity {
    @BindView(R.id.lv)
    ListViewForScrollView lv;
    @BindView(R.id.btn_net_test)
    Button btnNetTest;
    @BindView(R.id.btn_prepare_charge)
    Button btnPrepareCharge;
    @BindView(R.id.btn_qibao)
    Button btnQibao;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_address)
    TextView tvAddress;

    private List<DeviceBean> list_device = new ArrayList<>();
    private DeviceAdapter adapter;
    private boolean isDeviceConnet;

    private int qibaoNum = 0;
    public boolean cs=true;
    public boolean cd=false;
    public boolean qb=false;
    public boolean qh=false;
    private String TAG = "MainControlHighActvity";
    private String qbFlag = "qb";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxjl_maincontrol);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        initPower();                // 初始化上电方式()
        powerOnDevice(PIN_ADSL);    // 上电
    }

    public synchronized void sendCmd(byte[] mBuffer) {//0627添加synchronized,尝试加锁
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Utils.writeRecord("->:" + str);
                Log.e("发送命令", str);
                mOutputStream.write(mBuffer);
                //实验有没有用
//                mOutputStream.flush();
//                mSerialPort.tcflush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        byte[] localBuf = Utils.hexStringToBytes(fromCommad);
        doWithReceivData(localBuf);//处理cmd命令
    }

    /**
     * 处理芯片返回命令
     */
    private void doWithReceivData( byte[] cmdBuf) {
        String res = Utils.bytesToHexFun(cmdBuf);
        String cmd = DefCommand.getCmd(res);
        Log.e("收到命令", "res: " + res + " 当前指令是："  + cmd);
        Message msg = new Message();
        if ("B1".equals(cmd)) {
            Log.e(TAG,"收到B1命令了--" + "res: " + res + "--当前指令是："  + cmd);
            //同步 C000700101015947C0
            msg.what = 0;
            DeviceBean bean = new DeviceBean();
            bean.setRes(res.substring(8, 10));
            bean.setCode(res.substring(10,12));
            bean.setInfo("在线");
            msg.obj = bean;
            handler_msg.sendMessage(msg);
        } else if ("B2".equals(cmd)) {
            Log.e(TAG,"收到B2命令了--" + "res: " + res + "--当前指令是："  + cmd);
        } else if ("B3".equals(cmd)) {
            Log.e(TAG,"收到B3命令了--" + "res: " + res + "--当前指令是："  + cmd);
        } else if ("B4".equals(cmd)) {
            Log.e(TAG,"收到B4命令了--" + "res: " + res + "--当前指令是："  + cmd);
        } else if ("B5".equals(cmd)) {
            Log.e(TAG,"收到B5命令了--" + "res: " + res + "--当前指令是："  + cmd);
        } else if ("B6".equals(cmd)) {
            Log.e(TAG,"收到B6命令了--" + "res: " + res + "--当前指令是："  + cmd);
        } else if ("B7".equals(cmd)) {
            Log.e(TAG,"收到B7命令了--" + "res: " + res + "--当前指令是："  + cmd);
        }
    }
    private void initView() {
        adapter = new DeviceAdapter(this, list_device,false);
        lv.setAdapter(adapter);
    }


    private Handler handler_msg = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.obj!=null){
                switch (msg.what) {
                    case 0:
                        DeviceBean bean = (DeviceBean) msg.obj;
                        if (bean.getRes() != null&&msg.obj!=null) {
                            Log.e("判断", "bean.getRes(): " + bean.getRes());
                            String res = bean.getRes();//进页面为空

                            if (res.equals("01")) {
                                //受控指令
                                if (!isDeviceConnet) {
                                    isDeviceConnet = true;
                                }
                                Log.e("判断", "list_device.contains(bean): " + list_device.contains(bean));
                                Log.e("判断", "list_device: " + list_device.toString());
                                Log.e("判断", "bean: " + bean.toString());
                                Boolean chongfu = true;
                                for (DeviceBean a : list_device) {
                                    if (a.getCode().equals(bean.getCode())) {
                                        chongfu = false;
                                    }
                                }
                                if (chongfu) {
                                    list_device.add(bean);
                                    adapter.notifyDataSetChanged();
                                    //同步成功
//                                    byte[] powerCmd = JiLianCmd.send70("00","01" + bean.getCode());//70
//                                    sendCmd(powerCmd);
                                    //更新设备个数
                                    Message message = handler_msg.obtainMessage();
                                    int collectionSize = list_device.size();
                                    message.what = 15;
                                    message.arg1 = collectionSize;
                                    handler_msg.sendMessage(message);
                                } else {
                                    show_Toast(bean.getCode() + "已注册");
                                }
                            } else {
                                show_Toast("未识别的设备在连接");
                            }
                        }
                        break;
                    case 15:
                        //根据设备个数  超过1个时，点起爆按钮发送切换模式命令  1个就还是发送起爆命令
                        if (msg.arg1 <= 1) {
                            qbFlag = "qb";
                        } else {
                            qbFlag = "qh";
                        }
                        break;
                    case 1:
                        //数据发送错误
                        tvTip.setText("控制错误");
                        break;
                    case 6:
                        String ip = (String) msg.obj;
                        tvAddress.setText(ip);
                        break;
                    case 7:
                        DeviceBean bean3 = (DeviceBean) msg.obj;
                        //受控指令
                        if (!isDeviceConnet) {
                            isDeviceConnet = true;
                        }
                        if (!list_device.contains(bean3)) {
                            for (int a = 0; a < list_device.size(); a++) {
                                if (list_device.get(a).getCode().equals(bean3.getCode())) {
                                    list_device.get(a).setErrNum(bean3.getErrNum());
                                    list_device.get(a).setTrueNum(bean3.getTrueNum());
                                    list_device.get(a).setInfo(bean3.getInfo());
                                }
                            }

                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case 8:
                        DeviceBean bean4 = (DeviceBean) msg.obj;
                        //受控指令
                        if (!isDeviceConnet) {
                            isDeviceConnet = true;
                        }
                        if (!list_device.contains(bean4)) {
                            for (int a = 0; a < list_device.size(); a++) {
                                if (list_device.get(a).getCode().equals(bean4.getCode())) {
                                    list_device.get(a).setInfo(bean4.getInfo());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case 99:
                        DeviceBean bean2 = (DeviceBean) msg.obj;
                        String res2 = bean2.getRes();
                        Log.e("handler信息", "list_device.size(): " + list_device.size());
                        if (list_device.size() >= 0) {
                            show_Toast("设备：" + bean2.getCode() + "已断线");
                            for (int i = 0; i < list_device.size(); i++) {
                                if (bean2.getCode().equals(list_device.get(i).getCode())) {
                                    list_device.remove(i);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            Message message = handler_msg.obtainMessage();
                            int collectionSize = list_device.size();
                            message.what = 15;
                            message.arg1 = collectionSize;
                            handler_msg.sendMessage(message);
                        }
                        break;
                }
            }
            return false;
        }
    });


    @OnClick({R.id.btn_net_test, R.id.btn_prepare_charge, R.id.btn_qibao, R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_net_test:
                if(cs){
                    writeData("A2");//网络测试指令
                    Log.e(TAG,"发送起爆测试A2指令");
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_prepare_charge:
                if(cd){
                    writeData("A4");//准备充电指令
                    Log.e(TAG,"发送准备充电A4指令");
                }else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_qibao:
                if (cd) {
                    if (qbFlag.equals("qh")) {
                        //切换模式
                        if (qh) {
                            writeData("A5");//切换模式指令
                            Log.e(TAG,"发送切换模式指令A5指令");
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    } else {
                        //起爆
                        if (qb) {
                            writeData("A6");//准备起爆指令
                            Log.e(TAG,"发送起爆指令A6指令");
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    }
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_exit:
                //准备退出
                writeData("A7");//准备退出指令
                Log.e(TAG,"发送退出指令A7指令");
                break;
        }
    }
    private void fuwei() {
        cs=true;
        cd=false;
        qb=false;
    }
    private void writeData(final String data) {
        String tip ="";
        switch (data){
            case "A2":
                tip="起爆测试";
                break;
            case "A4":
                tip="准备充电";
                break;
            case "A5":
                tip="起爆";
                break;
            case "A6":
                tip="起爆";
                break;
            case "A7":
                tip="退出流程";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确定进行" + tip + "吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    show_Toast("正在执行" + data);
                    switch (Build.DEVICE) {
                        case "M900":
                            setTipText(data);
                            break;
                    }
                })
                .setNeutralButton("取消", null)
                .setNegativeButton("取消", null)
                .show();
    }

    private void setTipText(String data){
        switch (data){
            case "A2":
                tvTip.setText("正在起爆测试...");
                cs=false;
                cd=true;
                sendCmd(ThreeFiringCmd.wxjl_cmd(data,"01"));//A2
                break;
            case "A4":
                tvTip.setText("正在充电...");
                qb=true;
                qh=true;
                sendCmd(ThreeFiringCmd.wxjl_cmd(data,"01"));//A4
                break;
            case "A5":
                tvTip.setText("正在执行起爆...");
                fuwei();
                sendCmd(ThreeFiringCmd.wxjl_cmd(data,"01"));//A5
                break;
            case "A6":
                tvTip.setText("正在执行起爆...");
                fuwei();
                sendCmd(ThreeFiringCmd.wxjl_cmd(data,"01"));//A6
                break;
            case "A7":
                tvTip.setText("执行退出指令...");
                list_device.clear();
//                lastReceivedMessages.clear();
                adapter.notifyDataSetChanged();
                cs=true;
                cd=false;
                qb=false;
                qh=false;
                sendCmd(ThreeFiringCmd.wxjl_cmd(data,"01"));//A7
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EMgpio.SetGpioDataLow(94);
    }
}

