package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WxjlNearActivity extends SerialPortActivity {
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.btn_send_data)
    Button btnSendData;
    @BindView(R.id.btn_enter_jcms)
    Button btnEnterJcms;
    private String mSportName;
    private int mPowerIndex;
    private int sendNum = 1;
    private String deviceId = "", dataLength81 = "", data81 = "", dataLength82 = "", data82 = "", serId = "";
    //    protected OutputStream mOutputStream;
//    private InputStream mInputStream;
    private String TAG = "无线级联近距离页面";
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private String mRegion = "1";     // 区域
    private Handler handler_msg = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxjl_near);
        ButterKnife.bind(this);
        deviceId = "01";
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initPower();                // 初始化上电方式()
        powerOnDevice(PIN_ADSL);    // 上电
        initLgData();
    }

    private void initLgData() {
        //         获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
        handler_msg = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:
                        show_Toast("数据传输已结束");
                        break;
                }
                return false;
            }
        });
    }

    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Log.e("发送命令", str);
                Utils.writeLog("->:" + str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        Log.e("返回命令", fromCommad);
        Utils.writeLog("<-:" + fromCommad);
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd2(fromCommad);
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                    doWithReceivData(cmd, localBuf);//处理cmd命令
                }
            }
        }
    }

    /***
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, byte[] locatBuf) {
        if (DefCommand.CMD_5_TRANSLATE_80.equals(cmd)) {//80 无线级联：进行设备注册
            Log.e(TAG, "接收到80设备注册指令,发送80指令给主节点");
            //此时拿到设备号，然后在远距离级联页面时候使用
            EventBus.getDefault().post(new FirstEvent("deviceId", deviceId));
        } else if (DefCommand.CMD_5_TRANSLATE_81.equals(cmd)) {//81 无线级联：子节点与主节点进行数据传输
            Log.e(TAG, "收到81指令了");
            sendNum++;
            send81cmd();
        } else if (DefCommand.CMD_5_TRANSLATE_82.equals(cmd)) {//82 无线级联：进入检测模式
            Log.e(TAG, "收到82指令了");
        } else {
            Log.e(TAG, "返回命令没有匹配对应的命令-cmd: " + cmd);
        }
    }

    private void send81cmd() {
        //10条数据发一次   81指令：C0+设备号+81+数据体长度+数据体+后面跟通用的一样
        int dataLength;
        int limit = 1;
        if (mListData.size() >= limit) {
            dataLength = mListData.size() / limit >= sendNum ? (limit * 9 + 1) : Math.max(mListData.size() % limit, 0) * 9 + 1;
        } else {
            dataLength = mListData.size() % limit * 9 + 1;
        }
        if (!(dataLength > 1)) {
            handler_msg.sendMessage(handler_msg.obtainMessage(0));
            return;
        }
//        dataLength = 10;
        //数据体长度
        String b1 = Utils.intToHex(dataLength);
        dataLength81 = Utils.addZero(b1, 2);
        /**
         * 数据体   10个雷管信息拼接
         * 单个雷管信息：编号（2byte）+ 芯片ID（4byte） + 延时数据（2byte） + 演示参数（2byte） +
         * 芯片类型（由于都是PT且长度是1byte的，就都是00）
         */
        StringBuilder sBuilder = new StringBuilder();
        String b = Utils.intToHex(sendNum);
        serId = Utils.addZero(b, 2);
        for (int i = 0; i < mListData.size(); i++) {
            if (i >= (sendNum - 1) * limit && i <= sendNum * limit - 1) {
                DenatorBaseinfo write = mListData.get(i);
                String data = "";
                String denatorId = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorId());//新芯片
                denatorId = Utils.getReverseDetonatorNo(denatorId);
                String lid = write.getShellBlastNo();
                int delayTime = write.getDelay();
                byte[] delayBye = Utils.intToByte(delayTime);
                String delayStr = Utils.bytesToHexFun(delayBye);//延时时间
                data = denatorId + delayStr + write.getZhu_yscs() + "00";
                if (write.getDenatorIdSup() != null && write.getDenatorIdSup().length() > 4) {
                    String denatorIdSup = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorIdSup());//新芯片
                    denatorIdSup = Utils.getReverseDetonatorNo(denatorIdSup);
                    data = denatorId + delayStr + write.getZhu_yscs() + denatorIdSup + write.getCong_yscs();
                }
                Log.e(TAG, lid + "--雷管序号:" + serId + "--denatorId:" + denatorId + "--delayTime:" + delayTime + "--delayStr:" + delayStr +
                        "--延时参数:" + write.getZhu_yscs() + "--数据体长度:" + dataLength81 + "--data81:" + data);
                sBuilder.append(data);
                data81 = sBuilder.toString();
            }
        }
        Log.e("81指令", dataLength81 + serId + data81);
//        sendCmd(ThreeFiringCmd.sendWxjl81(deviceId,dataLength81,"018527E8005A00FD0400"));
        sendCmd(ThreeFiringCmd.sendWxjl81(deviceId, dataLength81, serId, data81));
    }

    @OnClick({R.id.btn_register, R.id.btn_exit, R.id.btn_send_data, R.id.btn_enter_jcms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                sendCmd(ThreeFiringCmd.sendWxjl80(deviceId));
                break;
            case R.id.btn_send_data:
                //雷管数据10条发一次
                send81cmd();
                break;
            case R.id.btn_enter_jcms:
                String b = Utils.intToHex(1);
                dataLength82 = Utils.addZero(b, 2);
                data82 = "01";
                Log.e(TAG,"82指令--数据体长度：" + dataLength82 + "--数据体：" + data82);
                sendCmd(ThreeFiringCmd.sendWxjl82(deviceId, dataLength82, data82));
                Log.e(TAG, "发送82进入检测模式指令");
                //通讯速率需从115200降到2400
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mApplication.closeSerialPort();
//                        Log.e(TAG, "调用mApplication.closeSerialPort()开始关闭串口了。。");
//                        mSerialPort = null;
//                    }
//                }).start();
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                initSerialPort(2400);
//                Log.e(TAG, "3秒后重新打开串口，将波特率降为2400");
                EventBus.getDefault().post(new FirstEvent("deviceId", deviceId));
                break;
            case R.id.btn_exit:
                byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");//13
                sendCmd(powerCmd);
                finish();
                break;
        }
    }
}

