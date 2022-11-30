package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.jilian.SyncActivity;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestICActivity extends SerialPortActivity {
    @BindView(R.id.but_pre)
    Button butPre;
    @BindView(R.id.tv_ceshi_dianliu)
    TextView tvCeshiDianliu;
    @BindView(R.id.tv_ceshi_dianya)
    TextView tvCeshiDianya;
    private From42Power busInfo;
    private ThreadFirst firstThread;
    private static volatile int stage;
    private SendOpenPower sendOpenThread;//打开电源
    private CloseOpenPower closeOpenThread;//关闭电源

    private volatile int initCloseCmdReFlag = 0;
    private volatile int revCloseCmdReFlag = 0;
    private volatile int revOpenCmdReFlag = 0;
    private volatile int revOpenCmdTestFlag = 0;//收到了打开测试命令
    private Handler handler_zhuce;
    private SendPower sendPower;
    private volatile int firstCount = 0;
    private Handler busHandler = null;//总线信息
    private int denatorCount = 0;//雷管总数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_icactivity);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        loadMoreData();

        busHandler = new Handler(msg -> {
            if (busInfo != null) {
                BigDecimal b = BigDecimal.valueOf(busInfo.getBusCurrentIa());//处理大额数据专用类
                float dianliu = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                String displayIcStr = dianliu + "μA";// 保留两位小数

//                if (dianliu > 11000) {
//                    displayIcStr = displayIcStr + "(疑似短路)";
//                    tvCeshiDianliu.setTextColor(Color.RED);
//                } else if (dianliu < 11000 && dianliu > denatorCount * 24) {
//                    displayIcStr = displayIcStr + "(电流过大)";
//                    tvCeshiDianliu.setTextColor(Color.RED);
//                } else if (dianliu < 4+denatorCount*6) {
//                    displayIcStr = displayIcStr + "(疑似断路)";
//                    tvCeshiDianliu.setTextColor(Color.RED);
//                } else {
//                    tvCeshiDianliu.setTextColor(Color.GREEN);
//                }
                tvCeshiDianliu.setText(displayIcStr);
                tvCeshiDianya.setText(busInfo.getBusVoltage() + "V");
            }
            busInfo = null;
            return false;
        });

    }

    private void loadMoreData() {

        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao().loadAll();
        denatorCount = list.size();
    }



    @OnClick({R.id.but_pre})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.but_pre://进入级联页面
                if (revOpenCmdTestFlag == 0) {
                    byte[] powerCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41
                    sendCmd(powerCmd);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendPower = new SendPower();//40指令线程
                    sendPower.exit = false;
                    sendPower.start();
                    revOpenCmdTestFlag = 1;
                    butPre.setText("停止测试");
                } else {
                    sendPower.exit = true;
                    sendPower.interrupt();
                    try {
                        sendPower.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");//13 退出注册模式
                    sendCmd(powerCmd);
                    butPre.setText("开始测试");
                    tvCeshiDianliu.setText("0.0μA");
                    tvCeshiDianya.setText("0.0V");
                    tvCeshiDianliu.setTextColor(Color.BLACK);
                    tvCeshiDianya.setTextColor(Color.BLACK);
                    revOpenCmdTestFlag = 0;
                }
                break;
        }
    }
    @Override
    public void sendInterruptCmd() {
        byte[] reCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");
        try {
            mOutputStream.write(reCmd);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.sendInterruptCmd();
    }

    //接收串口数据
    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//将数组转化为16进制字符串

        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) {
                this.revCmd = this.afterCmd;
            } else {
                this.revCmd = "";
            }
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);//返回命令解码
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {

            } else {
                String cmd = DefCommand.getCmd(fromCommad);//得到 返回命令
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);//将字符串转化为数组
                    doWithReceivData(cmd, localBuf, localSize);
                }
            }
        }
    }

    /**
     * 处理接收到的cmd命令
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf, int size) {
        Log.e("软件版本返回的命令", "cmd: " + cmd);
        byte[] locatBuf = new byte[size];
        System.arraycopy(cmdBuf, 0, locatBuf, 0, size); // 将cmdBuf数组复制到locatBuf数组
        String fromCommad = Utils.bytesToHexFun(locatBuf);
        String realyCmd1 = DefCommand.decodeCommand(fromCommad);
        if (DefCommand.CMD_2_NETTEST_1.equals(cmd)) {//进入测试模式
            //stage=3;
            revOpenCmdTestFlag = 1;


        } else if ("40".equals(cmd)) {
            busInfo = FourStatusCmd.decodeFromReceiveDataPower24_1("00", locatBuf);
            Log.e("命令", "busInfo: " + busInfo.toString());
            busHandler.sendMessage(busHandler.obtainMessage());

        } else if ("22".equals(cmd)) { // 关闭测试
            //发出关闭获取得到电压电流
            sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));

        } else if ("13".equals(cmd)) { // 关闭电源
            if (initCloseCmdReFlag == 1) { // 打开电源
                revCloseCmdReFlag = 1;
                closeOpenThread.exit = true;
                sendOpenThread = new SendOpenPower();
                sendOpenThread.start();
            }
        } else if ("41".equals(cmd)) { // 开启总线电源指令
//            sendOpenThread = new SendOpenPower();
//            sendOpenThread.start();
//            sendOpenThread.exit = true;
//            revOpenCmdReFlag = 1;
        }
    }

    /**
     * 发送命令
     */
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Log.e("发送", str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    /**
     * 自定义一个线程
     */
    private class ThreadFirst extends Thread {
        public volatile boolean exit = false;

        public void run() {

            while (!exit) {
                try {

                    switch (stage) {

                        case 1:
                            Thread.sleep(1000);
                            // 40
                            sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));
                            firstCount++;
                            if (firstCount >= 40) {
                                busHandler.sendMessage(busHandler.obtainMessage());
                                exit = true;
                                break;
                            } else {
                                busHandler.sendMessage(busHandler.obtainMessage());
                            }
                            break;

                    }


                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 一个发送开启电源的线程
     */
    private class SendOpenPower extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    if (zeroCount == 0) {
                        // 41
                        sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));
                    }
                    if (revOpenCmdReFlag == 1) {
                        exit = true;
                        break;
                    }
                    Thread.sleep(500);
                    if (zeroCount > 100) {
                        busHandler.sendMessage(busHandler.obtainMessage());
                        exit = true;
                        break;
                    }
                    zeroCount++;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取电源信息
     */
    private class SendPower extends Thread {
        public volatile boolean exit = true;

        public void run() {

            while (!exit) {
                try {
                    //发送获取电源信息
                    sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00"));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 一个发送关闭电源的线程
     */
    private class CloseOpenPower extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    if (zeroCount == 0) {
                        initCloseCmdReFlag = 1;
                        sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));
                    }
                    if (revCloseCmdReFlag == 1) {
                        exit = true;
                        break;
                    }
                    Thread.sleep(100);
                    if (zeroCount > 80) {
                        busHandler.sendMessage(busHandler.obtainMessage());
                        exit = true;
                        break;
                    }
                    zeroCount++;
                } catch (InterruptedException e) {
                    // TODO Auto-generatedx catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 关闭守护线程
     */
    private void closeThread() {
        //Thread_stage_1 ttst_1

        if (sendPower != null) {
            sendPower.exit = true;  // 终止线程thread
            try {
                sendPower.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (firstThread != null) {
            firstThread.exit = true;  // 终止线程thread
            try {
                firstThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (sendOpenThread != null) {
            sendOpenThread.exit = true;  // 终止线程thread
            try {
                sendOpenThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (closeOpenThread != null) {
            closeOpenThread.exit = true;  // 终止线程thread
            try {
                closeOpenThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        firstThread = null;
    }
}