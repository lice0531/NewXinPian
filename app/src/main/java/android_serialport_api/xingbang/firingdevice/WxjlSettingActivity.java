package android_serialport_api.xingbang.firingdevice;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.custom.WxSearchDevicesAdapter;
import android_serialport_api.xingbang.models.DeviceBean;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.upload.InitConst;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WxjlSettingActivity extends SerialPortActivity {
    private String TAG = "无线级联配置页面";
    @BindView(R.id.tv_xd)
    TextView tvXinDao;
    @BindView(R.id.ns_xd)
    NiceSpinner nsXd;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    @BindView(R.id.lv_devices)
    ListView lvDevices;
    @BindView(R.id.ll_sp_xd)
    LinearLayout llSpXd;
    @BindView(R.id.btn_qbkXd)
    Button btnQbkXd;
    @BindView(R.id.btn_zjqXd)
    Button btnZjqXd;
    @BindView(R.id.btn_exit)
    Button btnExit;
    private boolean isCanSetXd = true;//是否展示设置信道的布局  默认不展示
    private boolean receiveF9 = false;//发出F9命令是否返回
    private boolean receiveAB = false;//发出AB命令是否返回
    private Handler handler_msg = new Handler();
    private boolean isOpened = false;//串口是否已打开
    Handler openHandler = new Handler();//重新打开串口
    private boolean isRestarted = false;
    private List<String> xdlist = new ArrayList<>();
    private List<DeviceBean> deviceslist = new ArrayList<>();
    private WxSearchDevicesAdapter mAdapter;
    private String zjqSerid = "KKF23WS00000001";//无线中继器序列号  目前先写死
    /**
     * 起爆卡和无线驱动器信道设置成功后想要切换为其他信道进行通信，需要先发送AB设置新信道的指令，
     * 这样的目的是将无线驱动器信道修改到新的信道，之后再设置起爆卡信道-》设置驱动器信道
     * 目前信道这块  只有为27时，可以直接通讯，是直接可以进行通讯的，所以一上来就默认27信道
     */
    private int xinDaoId = 0;//信道

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxjl_setting);
        ButterKnife.bind(this);
        initData();
        initHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isOpened = true;
                initSerialPort(InitConst.TX_RATE);
                Log.e(TAG, "串口已打开" + InitConst.TX_RATE);
                show_Toast("串口已打开");
            }
        }, 2000);
    }

    private void initData(){
        int xindao = (int) MmkvUtils.getcode("xinDao", 7);
        Log.e(TAG, "app信道: " + xindao);
        tvXinDao.setText("当前信道:" + xindao);
//        //这是不同速率的信道
        xdlist.add("CH0-19.2kbps-0FEC");
        xdlist.add("CH1-19.2kbps-1FEC");
        xdlist.add("CH2-19.2kbps-2FEC");
        xdlist.add("CH3-19.2kbps-3FEC");
        xdlist.add("CH4-19.2kbps-4FEC");
        xdlist.add("CH5-9.6kbps-0FEC");
        xdlist.add("CH6-9.6kbps-1FEC");
        xdlist.add("CH7-9.6kbps-2FEC");
        xdlist.add("CH8-9.6kbps-3FEC");
        xdlist.add("CH9-9.6kbps-4FEC");
        xdlist.add("CH10-4.8kbps-0FEC");
        xdlist.add("CH11-4.8kbps-1FEC");
        xdlist.add("CH12-4.8kbps-2FEC");
        xdlist.add("CH13-4.8kbps-3FEC");
        xdlist.add("CH14-4.8kbps-4FEC");
        xdlist.add("CH15-2.4kbps-0FEC");
        xdlist.add("CH16-2.4kbps-1FEC");
        xdlist.add("CH17-2.4kbps-2FEC");
        xdlist.add("CH18-2.4kbps-3FEC");
        xdlist.add("CH19-2.4kbps-4FEC");
        xdlist.add("CH20-19.2kbps-0");
        xdlist.add("CH21-19.2kbps-1");
        xdlist.add("CH22-19.2kbps-2");
        xdlist.add("CH23-19.2kbps-3");
        xdlist.add("CH24-19.2kbps-4");
        xdlist.add("CH25-9.6kbps-0");
        xdlist.add("CH26-9.6kbps-1");
        xdlist.add("CH27-9.6kbps-2");
        xdlist.add("CH28-9.6kbps-3");
        xdlist.add("CH29-9.6kbps-4");
        xdlist.add("CH30-4.8kbps-0");
        xdlist.add("CH31-4.8kbps-1");
        xdlist.add("CH32-4.8kbps-2");
        xdlist.add("CH33-4.8kbps-3");
        xdlist.add("CH34-4.8kbps-4");
        xdlist.add("CH35-2.4kbps-0");
        xdlist.add("CH36-2.4kbps-1");
        xdlist.add("CH37-2.4kbps-2");
        xdlist.add("CH38-2.4kbps-3");
        xdlist.add("CH39-2.4kbps-4");
        nsXd.attachDataSource(xdlist);
        Log.e(TAG,"设置的信道是:" + xinDaoId);
        Utils.writeLog("设置的信道是:" + xinDaoId);
        nsXd.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
//                xinDaoId = xdlist.get(position);
                if (getXdId(xdlist.get(position)) == -1) {
                    show_Toast("信道获取异常");
                    return;
                }
                xinDaoId = getXdId(xdlist.get(position));
                Utils.writeLog("设置的信道是:" + xinDaoId);
                Log.e(TAG,"选中的信道是:" + xdlist.get(position) + "截取后的信道id:" + xinDaoId);
            }
        });
        mAdapter = new WxSearchDevicesAdapter(this,R.layout.item_wx_device);
        lvDevices.setAdapter(mAdapter);
    }

    private void initHandler() {
        handler_msg = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        String qbkResult = (String) msg.obj;
                        if ("true".equals(qbkResult)) {
                            btnQbkXd.setText("1.起爆卡信道已配置为:" + xinDaoId);
                            show_Toast("起爆卡配置成功");
                        } else {
                            show_Toast("起爆卡配置失败，请重新配置");
                            btnQbkXd.setText("1.起爆卡配置");
                        }
                        break;
                    case 2:
                        String zjqResult = (String) msg.obj;
                        if ("true".equals(zjqResult)) {
                            AlertDialog dialog = new AlertDialog.Builder(WxjlSettingActivity.this)
                                    .setTitle("无线配置成功")//设置对话框的标题//"成功起爆"
                                    .setCancelable(false)
                                    .setMessage("无线配置成功,请退出APP后,重新进入程序!")//设置对话框的内容"本次任务成功起爆！"
                                    //设置对话框的按钮
                                    .setNegativeButton("确认", (dialog13, which) -> {
                                        dialog13.dismiss();
                                        removeALLActivity();
                                        finish();
                                    })
//                .setNeutralButton("确认", (dialog2, which) -> {
//                    dialog2.dismiss();
//                })
                                    .create();
                            dialog.show();
                        } else {
                            show_Toast("无线中继器配置失败，请重新配置");
                            btnZjqXd.setText("2.无线中继器配置");
                        }
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 提取字符串中 "CH" 后面的数字部分
     *
     * @param input 输入字符串
     * @return "CH" 后面的数字部分，或者如果没有找到匹配则返回空字符串
     */
    public int getXdId(String input) {
        // 正则表达式匹配 "CH" 后面的数字部分
        Pattern pattern = Pattern.compile("CH([A-Za-z0-9]+)-");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            // 返回第一个捕获组的内容
            return Integer.parseInt(matcher.group(1));
        }
        // 如果没有找到匹配项，返回空字符串
        Log.e(TAG,"No match found for input: " + input);
        return -1;
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        Log.e(TAG + "-返回命令", fromCommad);
        Utils.writeLog("<-:" + fromCommad);
        if (fromCommad.startsWith("C5C5") && fromCommad.endsWith("E5E5")) {
            String cmd = DefCommand.getWxSDKCmd(fromCommad);
            Log.e(TAG,"返回命令正常--具体命令:" + cmd);
            byte[] localBuf = Utils.hexStringToBytes(fromCommad);
            doWithReceivData(cmd, localBuf);//处理cmd命令
        } else {
            Log.e(TAG,"返回命令出错:" + fromCommad);
        }
    }

    /***
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, byte[] locatBuf) {
        if (DefCommand.CMD_QBK_F9.equals(cmd)) {//F9 无线级联：收到起爆卡设置信道指令了
            Log.e(TAG, "收到起爆卡设置信道指令了");
            receiveF9 = true;
            Message message = new Message();
            message.what = 1;
            message.obj = "true";
            handler_msg.sendMessage(message);
        } else if (DefCommand.CMD_ZJQ_AB.equals(cmd)) {//AB 无线级联：收到无线中继器设置信道指令了
            Log.e(TAG, "收到无线中继器设置信道指令了");
            receiveAB = true;
            Message message = new Message();
            message.what = 2;
            message.obj = "true";
            handler_msg.sendMessage(message);
        } else {
            Log.e(TAG, TAG + "-返回命令没有匹配对应的命令-cmd: " + cmd);
        }
    }

    private void enterNearPage() {
        closeSerial();
        Intent intent = new Intent(WxjlSettingActivity.this, XingbangMain1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Log.e(TAG + "发送命令", str);
                Utils.writeLog("->:" + str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void closeSerial() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication.closeSerialPort();
                Log.e(TAG, "串口已关闭。。");
                mSerialPort = null;
            }
        }).start();
    }

    @OnClick({R.id.rl_search,R.id.btn_qbkXd,R.id.btn_zjqXd,R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                //开始发指令搜索附近无线设备  现在先写点假数据
                deviceslist.clear();
                deviceslist.add(new DeviceBean(zjqSerid,"CH1 无线<->有线 2023-07-06 V2.7"));
                mAdapter.setListData(deviceslist);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_qbkXd:
                if (!isOpened) {
                    show_Toast("正在打开串口，打开后您再操作");
                    return;
                }
                sendF9();
                break;
            case R.id.btn_zjqXd:
                if (!isOpened) {
                    show_Toast("正在打开串口，打开后您再操作");
                    return;
                }
                sendAB();
                break;
            case R.id.btn_exit:
                finish();
                break;
        }
    }

    private void sendF9(){
        /**
         * 将起爆卡信道设置为 1 的指令及回复如下：
         * 指令： C5C5  02  F9    01  AE  E5E5
         *      包头  长度 指令码 信道  CRC 包尾
         * 发送CRC:不包含包头、指令长度和包尾
         * 回复： C5C5  02   F9    00   A9  E5E5
         *       包头 长度 指令码 回复数据 CRC 包尾
         * 回复CRC:不包含包头和包尾
         */
        String b = Utils.intToHex(xinDaoId);
        String xdId = Utils.addZero(b, 2);
//        String qbzXdCmd = "C5C502F9" + xdId + "AEE5E5";
//        sendCmd(CRC16.hexStringToByte(qbzXdCmd));
        sendCmd(ThreeFiringCmd.sendWx_Qbk_F9(xdId));
    }

    private void sendAB() {
        /**
         * 给中继 KKF23WS00000001 设置信道 6 指令及回复如下：
         * 指令：C5C5 12   AB   4B4B46323357533030303030303031    06      07   F9  E5E5
         *      包头 长度 指令码 中继卡序列号(KKF23WS00000001)     主卡信道 固定不变  CRC 包尾
         * 发送CRC:不包含包头、指令长度和包尾
         * 回复：C5C5  02   AB     00  8F  E5E5
         *      包头 长度 指令码 回复数据 CRC 包尾
         * 回复CRC:不包含包头和包尾
         */
        String b1 = Utils.intToHex(xinDaoId);
        String xdId1 = Utils.addZero(b1, 2);
//        String zjqXdCmd = "C5C512AB4B4B46323357533030303030303031" + xdId1 + "07AEE5E5";
//        sendCmd(CRC16.hexStringToByte(zjqXdCmd));
        sendCmd(ThreeFiringCmd.sendWx_Zjq_AB("4B4B46323357533030303030303031",xdId1));
    }


    @Override
    protected void onDestroy() {
        closeSerial();
        super.onDestroy();
        openHandler.removeCallbacksAndMessages(null);
    }
}
