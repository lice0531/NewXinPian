package android_serialport_api.xingbang.firingdevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.tencent.bugly.proguard.D;

import org.angmarch.views.NiceSpinner;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.custom.WxSearchDevicesAdapter;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.models.DeviceBean;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.upload.InitConst;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WxjlSettingActivity extends SerialPortActivity {
    /**
     * 界面
     */
    private String TAG = "无线级联配置页面";
    private String wxjlDeviceId = "01";//目前只有一台设备  所以设备地址先固定01
    private boolean isRemote = false;//是否可以进行远距离无线级联
    @BindView(R.id.tv_xindao)
    TextView tvXinDao;
    @BindView(R.id.ns_xd)
    NiceSpinner nsXd;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    @BindView(R.id.lv_devices)
    ListView lvDevices;
    private String dataLength82 = "", data82 = "";
    private boolean receive82 = false;//发出82命令是否返回
    private Handler handler_msg = new Handler();
    private EnterJcms enterJcms;
    Handler openHandler = new Handler();//重新打开串口
    private boolean isRestarted = false;
    private int errorNum;
    private List<String> xdlist = new ArrayList<>();
    private List<DeviceBean> deviceslist = new ArrayList<>();
    private WxSearchDevicesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxjl_setting);
        ButterKnife.bind(this);
        initData();
        initHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onRestart() {
        isRestarted = true;
        super.onRestart();
    }

    private void initData(){
        xdlist.add("CHO - 19.2kbps - 0FEC");
        xdlist.add("CH1 - 19.2kbps - 1FEC");
        xdlist.add("CH2 - 19.2kbps - 2FEC");
        xdlist.add("CH3 - 19.2kbps - 3FEC");
        xdlist.add("CH4 - 19.2kbps - 4FEC");
        xdlist.add("CH5 - 9.6kbps  - 0FEC");
        xdlist.add("CH6 - 9.6kbps  - 1FEC");
        xdlist.add("CH7 - 9.6kbps  - 2FEC");
        xdlist.add("CH8 - 9.6kbps  - 3FEC");
        xdlist.add("CH9 - 9.6kbps  - 4FEC");
        xdlist.add("CH1O - 4.8kbps - 0FEC");
        xdlist.add("CH11 - 4.8kbps  - 1FEC");
        xdlist.add("CH12 - 4.8kbps  - 2FEC");
        xdlist.add("CH13 - 4.8kbps  - 3FEC");
        xdlist.add("CH14 - 4.8kbps  - 4FEC");
        xdlist.add("CH15 - 2.4kbps  - 0FEC");
        xdlist.add("CH16 - 2.4kbps  - 1FEC");
        xdlist.add("CH17 - 2.4kbps  - 2FEC");
        xdlist.add("CH18 - 2.4kbps  - 3FEC");
        xdlist.add("CH19 - 2.4kbps  - 4FEC");
        xdlist.add("CH2O - 19.2kbps - 0");
        xdlist.add("CH21 - 19.2kbps - 1");
        xdlist.add("CH22 - 19.2kbps - 2");
        xdlist.add("CH23 - 19.2kbps - 3");
        xdlist.add("CH24 - 19.2kbps - 4");
        xdlist.add("CH25 - 9.6kbps  - 0");
        xdlist.add("CH26 - 9.6kbps  - 1");
        xdlist.add("CH27 - 9.6kbps  - 2");
        xdlist.add("CH28 - 9.6kbps  - 3");
        xdlist.add("CH29 - 9.6kbps  - 4");
        xdlist.add("CH30 - 4.8kbps  - 0");
        xdlist.add("CH31 - 4.8kbps  - 1");
        xdlist.add("CH32 - 4.8kbps  - 2");
        xdlist.add("CH33 - 4.8kbps  - 3");
        xdlist.add("CH34 - 4.8kbps  - 4");
        xdlist.add("CH35 - 2.4kbps  - 0");
        xdlist.add("CH36 - 2.4kbps  - 1");
        xdlist.add("CH37 - 2.4kbps  - 2");
        xdlist.add("CH38 - 2.4kbps  - 3");
        xdlist.add("CH39 - 2.4kbps  - 4");
        tvXinDao.setText(xdlist.get(36));
        nsXd.attachDataSource(xdlist);
        mAdapter = new WxSearchDevicesAdapter(this,R.layout.item_wx_device);
        lvDevices.setAdapter(mAdapter);
    }

    private void initHandler() {
        handler_msg = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:
                        String jcmsResult = (String) msg.obj;
                        closeThread();
                        if ("true".equals(jcmsResult)) {
                            enterRemotePage();
                        } else {
                            show_Toast("芯片未返回，请退出APP后再重新级联");
                        }
                        break;
                    case 2:
                        enterRemotePage();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        openHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openSerial();
            }
        }, 2000);
    }

    private void openSerial(){
        if (isRestarted) {
            initSerialPort(InitConst.TX_RATE);
            Log.e(TAG,"重新开启打开串口" + InitConst.TX_RATE);
        }
    }


    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        Log.e(TAG + "-返回命令", fromCommad);
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
                Log.e(TAG,TAG + "--返回命令" + cmd);
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
        if (DefCommand.CMD_5_TRANSLATE_82.equals(cmd)) {//82 无线级联：进入检测模式
            Log.e(TAG, "收到82指令了");
            receive82 = true;
            Message message = new Message();
            message.what = 2;
            message.obj = "true";
            handler_msg.sendMessage(message);
        } else {
            Log.e(TAG, TAG + "-返回命令没有匹配对应的命令-cmd: " + cmd);
        }
    }

    private void enterRemotePage() {
        closeSerial();
        Intent intent = new Intent(WxjlSettingActivity.this, WxjlRemoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("wxjlDeviceId", wxjlDeviceId);
        startActivity(intent);
    }

    private void closeThread(){
        if (enterJcms != null) {
            enterJcms.exit = true;
            enterJcms.interrupt();
        }
    }

    private class EnterJcms extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;
            while (!exit) {
                try {
                    if (receive82) {
                        exit = true;
                        break;
                    }
                    if (zeroCount > 0 && zeroCount <= 5 && !receive82) {
                        String b = Utils.intToHex(1);
                        dataLength82 = Utils.addZero(b, 2);
                        data82 = "01";
                        sendCmd(ThreeFiringCmd.sendWxjl82(wxjlDeviceId, dataLength82, data82));
                        Log.e(TAG, "发送82进入检测模式指令");
                        Thread.sleep(1500);
                    } else if (zeroCount > 5){
                        Log.e(TAG,"82指令未返回已发送5次，停止发送82指令");
                        exit = true;
                        Message message = new Message();
                        message.what = 0;
                        message.obj = "error";
                        handler_msg.sendMessage(message);
                        break;
                    }
                    zeroCount++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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


    private void closeSerial() {
        isRestarted = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication.closeSerialPort();
                Log.e(TAG,"调用mApplication.closeSerialPort()开始关闭串口了。。");
                mSerialPort = null;
            }
        }).start();
    }

    @OnClick({R.id.rl_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                //开始发指令搜索附近无线设备  现在先写点假数据
                deviceslist.add(new DeviceBean("KKF23WS00000001","CH1 无线<->有线 2023-07-06 V2.7"));
                mAdapter.setListData(deviceslist);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
        String msg = event.getMsg();
        Log.e(TAG,"eventBus收到消息了" + msg);
        if (event.getMsg().equals("is81End")) {
            //说明此时近距离级联81指令已结束  并未执行82指令   此时点击远距离级联需要先发送82指令  等82成功再远距离级联
            isRemote = true;
        } else if (event.getMsg().equals("errorLgNum")) {
            errorNum = Integer.parseInt(event.getData());
            Log.e(TAG,"eventBus收到errorLgNum了:" + errorNum);
        }
    }

    @Override
    protected void onDestroy() {
        sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
        closeSerial();
        super.onDestroy();
        closeThread();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
