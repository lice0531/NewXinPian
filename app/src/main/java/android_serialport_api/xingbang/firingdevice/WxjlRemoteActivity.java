package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.custom.DeviceAdapter;
import android_serialport_api.xingbang.custom.ErrListAdapter;
import android_serialport_api.xingbang.custom.ListViewForScrollView;
import android_serialport_api.xingbang.custom.LoadHisFireAdapter_all;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DenatorHis_Detail;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.models.DeviceBean;
import android_serialport_api.xingbang.server.MySocketServer;
import android_serialport_api.xingbang.server.PollingReceiver;
import android_serialport_api.xingbang.server.PollingUtils;
import android_serialport_api.xingbang.server.WebConfig;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.upload.InitConst;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WxjlRemoteActivity extends SerialPortActivity implements AdapterView.OnItemClickListener, LoadHisFireAdapter_all.InnerItemOnclickListener {

    @BindView(R.id.lv_device)
    ListView lv;
    MySocketServer server;
    @BindView(R.id.btn_net_test)
    Button btnNetTest;
    @BindView(R.id.btn_prepare_charge)
    Button btnPrepareCharge;
    @BindView(R.id.btn_qibao)
    Button btnQibao;
    @BindView(R.id.btn_err)
    Button btnErr;
    @BindView(R.id.btn_exit)
    Button btnExit;
//    @BindView(R.id.lv_net)
//    ListViewForScrollView lvNet;
//    @BindView(R.id.lv_qibao)
//    ListViewForScrollView lvQibao;
//    @BindView(R.id.lv_chongdian)
//    ListViewForScrollView lvChongdian;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    private List<DeviceBean> list_device = new ArrayList<>();
    private List<DeviceBean> cmdList = new ArrayList<>();
    private List<DeviceBean> pollingList = new ArrayList<>();
    private DeviceAdapter adapter;
    private boolean isDeviceConnet;
    private int qibaoNum = 0;
    public boolean cs = true;
    public boolean cd_zt = false;//检测结束之后置打开,点充电按钮后关闭
    public boolean qb_zt = false;//升高压之后置打开,点起爆按钮后关闭
    public boolean qh = false;
    private boolean isStopGetLgNum = false;//充电开始就不再获取雷管数量了，在充电前获取雷管数量并记录下来展示即可
    private ConcurrentHashMap<String, String> lastReceivedMessages = new ConcurrentHashMap<>();
    //起爆按钮点击时，值为qh:发送A6命令（切换模式）   值为qb:发送A4命令（起爆）
    private String qbFlag = "qb";
    private String TAG = "无线级联远距离页面";
    private String wxjlDeviceId = "";
    private SyncDevices syncDevices;
    private SendA1Cmd sendA1Cmd;
    private SendA4Cmd sendA4Cmd;
    private ExitJl exitJl;
    private QueryError queryError;
    private QueryError2 queryError2;
    private DaoJiShiThread daoJiShiThread;
    private boolean reciveB0 = false;//发出同步命令是否返回
    private boolean reciveB1 = false;//发出检测A2命令是否返回
    private boolean reciveB4 = false;//发出起爆A4命令是否返回
    private boolean reciveB5 = false;//发出A5命令是否返回
    private boolean reciveB7 = false;//发出A7命令是否返回
    private boolean reciveB8 = false;//发出A8命令是否返回
    private int zeroCount = 1;//A5指令无返回的次数
    private String hisInsertFireDate;
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private int ChongDian_time;//充电时间
    private int JianCe_time;//准备时间
    private String qiaosi_set = "";//是否检测桥丝
    private String version = "02";//版本号
    private String qbxm_id = "-1";
    private String qbxm_name = "";
    private SQLiteDatabase db;
    private DatabaseHelper mMyDatabaseHelper;
    private Handler mHandler_tip = null;//提示
    Handler openHandler = new Handler();
    private boolean isShowError;
    private boolean showDialog1;
    private boolean showDialog2;
    private boolean showDialog3;
    private boolean showDialog4;
    private boolean showDialog5;
    private boolean showDialog6;
    private boolean showDialog7;
    private boolean showDialog8;
    private boolean showDialog9;
    private boolean showDialog10;
    private int errorNum;//错误雷管数量
    private int currentCount;//当前A8发送的次数
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private int xinDaoId = -1;
    private String xinDaoValue = "";//完整信道值
    private boolean receiveAB = false;//发出AB命令是否返回
    private boolean isReSendAB = true;//是否是切换信道，如果是切换信道，需要先发送AB设置无线驱动指令，再依次发送F9,AB
    private SetZJQThread setZjqThread;
    private boolean end84 = false;//84命令是否结束
    private ArrayList<Map<String, Object>> errDeData = new ArrayList<>();//错误雷管
    private int errorTotalNum;//错误雷管总数
    private boolean kaiguan = true;
    private boolean jiance_end = true;
    private boolean chongdian_end = true;
    private boolean huoqu_end = false;

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxjl_remote);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        getUserMessage();//获取用户信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        qbxm_id = (String) bundle.get("qbxm_id");
        qbxm_name = (String) bundle.get("qbxm_name");
        if (qbxm_id == null) {
            qbxm_id = "-1";
            qbxm_name = " ";
        }
        Utils.writeLog("无线级联页面-qbxm_id:" + qbxm_name);
        Log.e(TAG, "项目编号：" + qbxm_id + "--项目名称：" + qbxm_name);
        initView();
        mHandler_tip = new Handler(msg -> {
            String time = (String) msg.obj;
            delHisInfo(time);
            show_Toast("起爆记录条数最大30条,已删除" + time + "记录");
            return false;
        });
//        initSocket();
        initPower();                // 初始化上电方式()
        powerOnDevice(PIN_ADSL);
        show_Toast("正在同步设备...");
        initSerRate();
    }


    private void closeSerial() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication.closeSerialPort();
                Log.e(TAG, "调用mApplication.closeSerialPort()开始关闭串口了。。");
                mSerialPort = null;
            }
        }).start();
    }

    private void initSerRate() {
        openSerial();
        initData();
    }

    private void openSerial() {
        openHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initSerialPort();
            }
        }, 2000);
    }

    private void initData() {
        xinDaoValue = (String) MmkvUtils.getcode("xinDaoValue", "");
        xinDaoId = (int) MmkvUtils.getcode("xinDao", -1);
        Log.e(TAG, "当前信道Id: " + xinDaoId + "--信道值:" + xinDaoValue);
        Utils.writeLog("级联页面当前信道id:" + xinDaoId + "--信道value:" + xinDaoValue);
        wxjlDeviceId = !TextUtils.isEmpty(getIntent().getStringExtra("wxjlDeviceId")) ?
                getIntent().getStringExtra("wxjlDeviceId") : "01";
        Log.e(TAG, "设备号：" + getIntent().getStringExtra("wxjlDeviceId"));
        mListData = new GreenDaoMaster().queryDetonatorRegionAsc();
        syncDevices = new SyncDevices();
        syncDevices.start();
    }

    public synchronized void sendCmd(byte[] mBuffer) {
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


    private class SyncDevices extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;
            while (!exit) {
                try {
                    if (reciveB0) {
                        exit = true;
                        break;
                    }
                    if (zeroCount > 0 && zeroCount <= 5 && !reciveB0) {
                        Log.e(TAG, "发送A0同步指令");
                        sendCmd(ThreeFiringCmd.sendWxjlA0(wxjlDeviceId));
                        Thread.sleep(1500);
                    } else if (zeroCount > 5) {
                        Log.e(TAG, "A0指令未返回已发送5次，停止发送A0指令");
                        Message message = new Message();
                        message.what = 16;
                        message.obj = "设备同步失败，请退出APP后再重新同步";
                        Utils.writeLog("级联页面A0指令芯片命令无响应了");
                        handler_msg.sendMessage(message);
                        exit = true;
                        break;
                    }
                    zeroCount++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int zerCountB1 = 0;
    private class SendA1Cmd extends Thread {
        public volatile boolean exit = false;

        public void run() {
//            int zeroCount = 0;
            while (!exit) {
                try {
                    if (reciveB1) {
                        exit = true;
                        break;
                    }
                    if (zerCountB1 > 0 && zerCountB1 <= 5 && !reciveB1) {
                        Log.e(TAG, "发送A1检测指令");
                        sendCmd(ThreeFiringCmd.sendWxjlA1(wxjlDeviceId));
                        Thread.sleep(1500);
                    } else if (zerCountB1 > 5) {
                        Log.e(TAG, "A1指令未返回已发送5次，停止发送A1指令");
                        Message message = new Message();
                        message.what = 16;
//                        message.what = 22;
                        message.obj = "检测失败，请退出APP后再重新进行无线级联";
                        Utils.writeLog("级联页面A1指令芯片命令无响应了");
                        handler_msg.sendMessage(message);
                        exit = true;
                        break;
                    }
                    zerCountB1++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int zeroCountB4 = 0;
    private class SendA4Cmd extends Thread {
        public volatile boolean exit = false;

        public void run() {
//            int zeroCount = 0;
            while (!exit) {
                try {
                    if (reciveB4) {
                        exit = true;
                        break;
                    }
                    if (zeroCountB4 > 0 && zeroCountB4 <= 5 && !reciveB4) {
                        Log.e(TAG, "发送A4起爆指令");
                        sendCmd(ThreeFiringCmd.sendWxjlA4("01"));
                        Thread.sleep(1500);
                    } else if (zeroCountB4 > 5) {
                        Log.e(TAG, "A4指令未返回已发送5次，停止发送A4指令");
                        Message message = new Message();
                        message.what = 16;
                        message.obj = "起爆失败，请退出APP后再重新起爆";
                        Utils.writeLog("级联页面A4指令芯片命令无响应了");
                        handler_msg.sendMessage(message);
                        exit = true;
                        break;
                    }
                    zeroCountB4++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int zeroA8 = 0;
    private class QueryError extends Thread {
        public volatile boolean exit = false;

        public void run() {
//            int zeroCount = 0;
            while (!exit) {
                try {
                    if (reciveB8) {
                        exit = true;
                        break;
                    }
                    if (zeroA8 > 0 && zeroA8 <= 5 && !reciveB8) {
                        sendA8();
                        Thread.sleep(1500);
                    } else if (zeroA8 > 5) {
                        Log.e(TAG, "A8指令未返回已发送5次，停止发送A8指令");
                        exit = true;
                        Message message = new Message();
                        message.what = 17;
                        message.obj = "error";
                        handler_msg.sendMessage(message);
                        break;
                    }
                    zeroA8++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int zeroCountA7 = 0;
    private class ExitJl extends Thread {
        public volatile boolean exit = false;

        public void run() {
//            int zeroCount = 0;
            while (!exit) {
                try {
                    if (reciveB7) {
                        exit = true;
                        break;
                    }
                    Log.e(TAG,"A7线程--zeroCountA7:" + zeroCountA7 + "--reciveB7:" + reciveB7);
                    if (zeroCountA7 > 0 && zeroCountA7 <= 2 && !reciveB7) {
                        sendCmd(ThreeFiringCmd.sendWxjlA7("01"));
                        Log.e(TAG, "发送A7退出级联页面指令");
                        Thread.sleep(1300);
                    } else if (zeroCountA7 > 2) {
                        Log.e(TAG, "A7指令未返回已发送2次，停止发送A7指令");
                        exit = true;
                        Message message = new Message();
                        message.what = 16;
                        message.obj = "退出级联页面失败,稍候退出APP后请您再重新进行无线级联";
                        Utils.writeLog("级联页面A7指令芯片命令无响应了");
                        handler_msg.sendMessage(message);
                        break;
                    }
                    zeroCountA7++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int zeroCountAB = 0;

    private class SetZJQThread extends Thread {
        public volatile boolean exit = false;

        public void run() {
            while (!exit) {
                try {
                    Log.e(TAG, "AB指令发送次数:" + zeroCountAB);
                    if (zeroCountAB <= 1 && !receiveAB) {
                        sendAB();
                        Log.e(TAG, "发送AB设置无线中继器信道指令了");
                        Thread.sleep(1500);
                    } else if (zeroCountAB > 1) {
                        Log.e(TAG, "AB指令未返回已发送1次，停止发送AB指令");
                        exit = true;
                        Message message = new Message();
                        message.what = 11;
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

    private void sendAB() {
        /**
         * 给中继 KKF23WS00000001 设置信道 6 指令及回复如下：
         * 指令：C5C5 12   AB   4B4B46323357533030303030303031    06      07   F9  E5E5
         *      包头 长度 指令码 中继卡序列号(KKF23WS00000001)     主卡信道 固定不变  CRC 包尾
         * 发送CRC:不包含包头、指令长度和包尾
         * 回复：C5C5  02   AB     00  8F  E5E5
         *      包头 长度 指令码 回复数据 CRC 包尾
         * 回复CRC:不包含包头和包尾
         * 进入级联页面，直接将信道切为1
         */
        xinDaoId = 1;
        String b1 = Utils.intToHex(xinDaoId);
        String xdId1 = Utils.addZero(b1, 2);
//        String zjqXdCmd = "C5C512AB4B4B46323357533030303030303031" + xdId1 + "07AEE5E5";
//        sendCmd(CRC16.hexStringToByte(zjqXdCmd));
        sendCmd(ThreeFiringCmd.sendWx_Zjq_AB("4B4B46323357533030303030303031", xdId1));
    }

    private void sendF9() {
        /**
         * 将起爆卡信道设置为 1 的指令及回复如下：
         * 指令： C5C5  02  F9    01  AE  E5E5
         *      包头  长度 指令码 信道  CRC 包尾
         * 发送CRC:不包含包头、指令长度和包尾
         * 回复： C5C5  02   F9    00   A9  E5E5
         *       包头 长度 指令码 回复数据 CRC 包尾
         * 回复CRC:不包含包头和包尾
         */
        xinDaoId = 1;
        String b = Utils.intToHex(xinDaoId);
        String xdId = Utils.addZero(b, 2);
//        String qbzXdCmd = "C5C502F9" + xdId + "AEE5E5";
//        sendCmd(CRC16.hexStringToByte(qbzXdCmd));
        sendCmd(ThreeFiringCmd.sendWx_Qbk_F9(xdId));
        Log.e(TAG, "发送F9指令了");
    }

    private String mAfter = "";

    /**
     * 处理芯片返回命令
     */
    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        String strB8 = "";
        boolean isBwz = false;//B8指令是否完整
//        if (fromCommad.startsWith("B8")) {
//            strB8 = fromCommad;
//            String eNum16 = strB8.substring(4,6);
//            //得到错误数量
//            int eNum = Integer.parseInt(eNum16, 16);
//            //取到错误雷管cmd后  2个一组  每个都只取前两位  将其转为十进制就可以知道是错误芯片的81发送顺序  然后再找到对应的雷管id 再改变通信状态
//            int aa = strB8.substring(6).length() / 4;
//            Log.e(TAG,"错误雷管数量是:" + eNum + "--错误编号组数:" + aa);
//            if (aa != eNum) {
//                //错误雷管组数不等于错误数量 B8命令不完整
//                isBwz = true;
//                mAfter = fromCommad;
//                Log.e(TAG + "-说明B8命令不完整", fromCommad);
//            } else {
//                Log.e(TAG + "-说明B8命令正常", fromCommad);
//            }
//        }
//        if (!fromCommad.startsWith("B") && TextUtils.isEmpty(mAfter)) {
//            Log.e(TAG, "B8拼接前：" + mAfter + "--mAfter为空拼接后:" + fromCommad);
//            mAfter = fromCommad;
//        } else {
//            fromCommad =  fromCommad + mAfter;
//            Log.e(TAG, "B8拼接前：" + mAfter + "--mAfter不为空拼接后:" + fromCommad);
//            mAfter = "";
//        }
//        if (isBwz) {
//            if (TextUtils.isEmpty(mAfter)) {
//                Log.e(TAG, "B8拼接前：" + mAfter + "--mAfter为空拼接后:" + fromCommad);
//                mAfter = fromCommad;
//            } else {
//                fromCommad = mAfter + fromCommad;
//                Log.e(TAG, "B8拼接前：" + mAfter + "--mAfter不为空拼接后:" + fromCommad);
//                mAfter = "";
//            }
//        }
//        if (fromCommad.startsWith("B8") || fromCommad.endsWith("B8")) {
//            if (!fromCommad.endsWith("B8") && TextUtils.isEmpty(mAfter)) {
//                mAfter = fromCommad;
//                Log.e(TAG, "mAfter为空--cmd拼接前：" + mAfter + "--拼接后:" + fromCommad);
//            } else if (!fromCommad.startsWith("B8")) {
//                fromCommad = mAfter + fromCommad;
//                Log.e(TAG, "mAfter不为空--cmd拼接前：" + mAfter + "--拼接后:" + fromCommad);
//                mAfter = "";
//            }
//        }

        Log.e(TAG + "处理后--返回命令", fromCommad);
        Utils.writeLog("<-:" + fromCommad);
        if (fromCommad.startsWith("B") || fromCommad.startsWith("3") || fromCommad.startsWith("4")) {
            doWithReceivData(fromCommad);//处理cmd命令
        } else if (fromCommad.startsWith("C5C5") && fromCommad.endsWith("E5E5")) {
            String cmd = DefCommand.getWxSDKCmd(fromCommad);
            Log.e(TAG, "无线配置返回命令正常--具体命令:" + cmd);
            byte[] localBuf = Utils.hexStringToBytes(fromCommad);
            doWithWxpzReceivData(cmd, localBuf);//处理cmd命令
        } else if (fromCommad.startsWith("B8") && fromCommad.endsWith("B8")) {
            doWithReceivData(fromCommad);//处理cmd命令
        } else {
            Log.e(TAG, "-返回命令不完整" + fromCommad);
        }
    }

    /***
     * 处理芯片返回无线配置命令
     */
    private void doWithWxpzReceivData(String cmd, byte[] locatBuf) {
        if (DefCommand.CMD_QBK_F9.equals(cmd)) {//F9 无线级联：收到起爆卡设置信道指令了
            Log.e(TAG, "收到起爆卡设置信道指令了");
            Message message = new Message();
            message.what = 10;
            message.obj = "true";
            handler_msg.sendMessage(message);
        } else if (DefCommand.CMD_ZJQ_AB.equals(cmd)) {//AB 无线级联：收到无线中继器设置信道指令了
            Log.e(TAG, "收到无线中继器设置信道指令了");
            receiveAB = true;
            Message message = new Message();
            message.what = 11;
            message.obj = "true";
            handler_msg.sendMessage(message);
        } else {
            Log.e(TAG, TAG + "-无线配置返回命令没有匹配对应的命令-cmd: " + cmd);
        }
    }

    private void closeA0Thread() {
        if (syncDevices != null) {
            syncDevices.exit = true;  // 终止线程thread
            syncDevices.interrupt();
            Log.e(TAG, "A0线程已关闭");
        }
    }

    private void closeA1Thread() {
        if (syncDevices != null) {
            syncDevices.exit = true;  // 终止线程thread
            syncDevices.interrupt();
            Log.e(TAG, "A1线程已关闭");
        }
    }

    private void closeA4Thread() {
        if (sendA4Cmd != null) {
            sendA4Cmd.exit = true;  // 终止线程thread
            sendA4Cmd.interrupt();
            Log.e(TAG, "A4线程已关闭");
        }
    }

    private void closeA7Thread() {
        if (exitJl != null) {
            exitJl.exit = true;  // 终止线程thread
            exitJl.interrupt();
            Log.e(TAG, "A7线程已关闭");
        }
    }

    private void closeA8Thread() {
        if (queryError != null) {
            queryError.exit = true;  // 终止线程thread
            queryError.interrupt();
            Log.e(TAG, "A8线程已关闭");
        }
    }

    private void closeABThread() {
        if (setZjqThread != null) {
            setZjqThread.exit = true;
            setZjqThread.interrupt();
            Log.e(TAG, "AB线程已关闭");
        }
    }

    private boolean isCloseLx;

    private void closeLx() {
        isCloseLx = true;
        PollingUtils.stopPollingService(WxjlRemoteActivity.this, PollingReceiver.class, PollingUtils.ACTION);
        Log.e(TAG, "轮询已关闭");
        Utils.writeLog("级联页面轮询已关闭");
    }

    /**
     * 处理芯片返回命令
     */
    private void doWithReceivData(String res) {
        Message msg = new Message();
        if (res.startsWith(DefCommand.CMD_MC_SEND_B0)) {
            //已收到同步B0指令
            reciveB0 = true;
            Log.e(TAG, "已收到B0指令,不再发送A0指令");
            closeA0Thread();
            //同步
            msg.what = 0;
            DeviceBean bean = new DeviceBean();
            bean.setRes(res);
            bean.setCurrentPeak("0μA");
            bean.setCode(res.substring(res.length() - 2));
            bean.setInfo("在线");
            msg.obj = bean;
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B1)) {
            reciveB1 = true;
            closeA1Thread();
            cs = false;
            cd_zt = true;
            // 起爆检测
            msg.what = 9;
            msg.obj = receiveMsg(res, "在线");
            //开启轮询  10秒轮询一次接收到的消息  来更新当前设备列表信息
            if (isStopGetLgNum) {
                //只需要获取状态信息和电流即可
                sendCmd(ThreeFiringCmd.sendWxjlA5(wxjlDeviceId, "122130"));
            } else {
                //检测过程中状态充电信息雷管数据都需要获取展示
                sendCmd(ThreeFiringCmd.sendWxjlA5(wxjlDeviceId, "1423324160"));
            }
            PollingUtils.startPollingService(WxjlRemoteActivity.this, InitConst.POLLING_TIME,
                    PollingReceiver.class, PollingUtils.ACTION);
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B2)) {
            qh = true;
            cd_zt = false;
            // 充电
            msg.what = 9;
            msg.obj = receiveMsg(res, "充电");
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B3)) {
            // 充电升高压
            msg.what = 9;
            msg.obj = receiveMsg(res, "高压充电");
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B4)) {
            // 单个设备起爆
            reciveB4 = true;
            closeA4Thread();
            msg.what = 9;
            msg.obj = receiveMsg(res, "起爆中");
        } else if (res.startsWith("3") || res.startsWith("4")) {
            //频率降下来后  芯片返回的命令总是两截的：雷管全部数量和错误数量是单独发一条  所以这么来处理
            Log.e(TAG, "收到不完整B5消息是：" + res);
            DeviceBean bean = new DeviceBean();
            receB5Data(bean, res, 1);
            msg.what = 7;
            msg.obj = bean;
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B5)) {
            reciveB5 = true;
            zeroCount = 0;
            DeviceBean bean = new DeviceBean();
            receB5Data(bean, res, 2);
            msg.what = 8;
            msg.obj = bean;
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B6)) {
            // 切换模式起爆
            msg.what = 9;
            msg.obj = receiveMsg(res, "起爆中");
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B7)) {
            // 退出
            Log.e(TAG, "已收到B7指令,不再发送A7指令");
            reciveB7 = true;
            closeA7Thread();
            msg.what = InitConst.CODE_EXIT;
            msg.obj = receiveMsg(res, "");
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B8) && res.endsWith(DefCommand.CMD_MC_SEND_B8)) {
            // 获取错误雷管信息
            Log.e(TAG, "收到B8命令了res:" + res);
            if (currentCount == 0) {
                reciveB8 = true;
                updateLgStatus(true);
                Message message = new Message();
                message.what = 17;
                message.obj = "true";
                handler_msg.sendMessage(message);
            }
            if (isCanSendA8) {
                doWithReceivB8(res);
            }
        } else {
            Log.e(TAG, "无线级联远距离页面-没有匹配对应的命令-cmd:" + res);
        }
        handler_msg.sendMessage(msg);
    }

    private void doWithReceivB8(String completeCmd) {
        /**
         * 芯片A8指令一次最多给返回4条错误雷管数据，如超过4，则需要再次发送A8指令获取剩下的错误雷管
         * 拿到A8指令后，展示错误雷管数据  3发错误雷管：C00184 03 0300 0500 0D00 C6DDC0
         * 截取出错误雷管的序号，0300 0500 0D00就是发81指令时候的雷管顺序  得通过这个序号找到对应的雷管id给错误雷管更新状态
         * 同时在当前页面展示出错误雷管列表
         */
        currentCount++;
        Log.e(TAG, "收到B8了:" + completeCmd + "--当前发送A8次数：" + currentCount);
        String errorLgCmd = completeCmd.substring(6,completeCmd.length()-2);
        Log.e(TAG, "errorLgCmd: "+errorLgCmd );
        Log.e(TAG, "errorLgCmd之前: "+completeCmd.substring(6) );
        //取到错误雷管cmd后  4个一组  将每组数像B5指令获取正确错误雷管一样输出下错误雷管的编号   遍历下全部雷管数据然后再找到对应的雷管id 改变通信状态
        int aa = errorLgCmd.length() / 4;
        for (int i = 0; i < aa; i++) {
            String value = errorLgCmd.substring(4 * i, 4 * (i + 1));
            String idCmd = showLgNum(value);
//            int id = getErrorLgNum(idCmd);
            int id = Integer.parseInt(value,16);

//            String value = errorLgCmd.substring(4 * i, 4 * (i + 1));
//            String idCmd = value.substring(0,4);
//            int id2 = hexToDecimalLowHigh(idCmd);
            Log.e(TAG, "id: "+id );
//            Log.e(TAG, "id2: "+id2 );
            for (int j = 0; j < mListData.size(); j++) {
                if (id == j + 1) {
                    //得到当前的错误雷管index  denatorId即为错误雷管的芯片ID
                    String blastNo = mListData.get(j).getShellBlastNo();
                    Log.e(TAG, "错误雷管编号:" + id + "--错误雷管id:" + blastNo + "-现在去更新数据库的状态了");
                    updateLgStatus(mListData.get(j), 2);
                }
            }
        }
        int maxCount = 4;//芯片一次最多返回20条错误雷管
        int sendCount = (errorNum % maxCount) > 0 ? (errorNum / maxCount) + 1 : errorNum / maxCount;
        if (currentCount >= sendCount) {
            Message message = new Message();
            message.what = 18;
            message.obj = "错误";
            handler_msg.sendMessage(message);
            Log.e(TAG, "错误雷管数量小于4，不需要发A8了，重新开启轮训");
            Utils.writeRecord("级联页面错误雷管查询结束，错误数量:" + errorNum + "--A8发送次数:" + sendCount);
            if (stage != 4) {
                //当起爆结束后再查看错误雷管时  就不需要再重新开启轮询
                if (isStopGetLgNum) {
                    //只需要获取状态信息和电流即可
                    sendCmd(ThreeFiringCmd.sendWxjlA5(wxjlDeviceId, "122130"));
                } else {
                    //检测过程中状态充电信息雷管数据都需要获取展示
                    sendCmd(ThreeFiringCmd.sendWxjlA5(wxjlDeviceId, "1423324160"));
                }
                PollingUtils.startPollingService(WxjlRemoteActivity.this, InitConst.POLLING_TIME,
                        PollingReceiver.class, PollingUtils.ACTION);
            }
            huoqu_end = false;
            return;
        }
        sendA8();
        Log.e(TAG, "错误雷管数量>4，发A8了--错误总数量:" + errorNum + "--需要发A8的次数是:" + sendCount);
    }

    private boolean isCanSendA8 = false;

    private void updateLgStatus(boolean isNeedSendA8) {
        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao()
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF"))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
        int index = 0;
        for (DenatorBaseinfo baseinfo : list) {
            updateLgStatus(baseinfo, 1);
            index++;
        }
        if (isNeedSendA8) {
            if (index == list.size()) {
                isCanSendA8 = true;
                Log.e(TAG, "可以发A8了");
            }
        }
    }

    private void updateLgStatus(DenatorBaseinfo dbf, int type) {
        //更新雷管状态
        DenatorBaseinfo denator = Application.getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(dbf.getShellBlastNo())).unique();
        denator.setErrorCode(type == 1 ? "FF" : "00");
        denator.setErrorName(type == 1 ? getString(R.string.text_communication_state4) :
                getString(R.string.text_communication_state1));
        Log.e(TAG,"更新雷管状态--" + denator.getShellBlastNo() + "--" + denator.getErrorCode() +
                "--" + denator.getErrorName());
        Application.getDaoSession().update(denator);
    }

    private void receB5Data(DeviceBean bean, String res, int type) {
        Log.e(TAG, "处理接收到的B5消息：" + res + "--type : " + type);
        int index;
        if (type == 1) {
            index = 0;
        } else {
            bean.setRes(res);
            bean.setCode(res.substring(2, 4));
            index = 1;
        }
        // 1:检测状态  2：电压数据   3：电流数据   4：雷管总数   6：错误数量
        int aa = res.length() / 4;
        for (int i = index; i < aa; i++) {
            String value = res.substring(4 * i, 4 * (i + 1));
            switch (value.substring(0, 1)) {
                case "1":
                    //根据不同的值展示状态
//                    Log.e(TAG,"得到的B5消息中拿到检测状态了" + value.substring(value.length() - 3));
                    /**
                     * 001：同步      100：检测中      101：检测结束
                     * 200：充电   201：充电结束    202：充电失败
                     * 300：高压充电   301：高压充电结束  302：高压充电失败
                     * 400：起爆中     401：起爆结束   402：起爆失败
                     */
                    switch (value.substring(value.length() - 3)) {
                        case "001":
                            bean.setInfo("同步");
                            break;
                        case "100":
                            bean.setInfo("检测中");
                            break;
                        case "101":
                            bean.setInfo("检测结束");
//                            if (errorNum == 0) {
//                                Message message = new Message();
//                                message.what = 21;
//                                message.obj = "更新状态";
//                                handler_msg.sendMessage(message);
//                            }
                            break;
                        case "200":
                            bean.setInfo("充电");
                            break;
                        case "201":
                            bean.setInfo("充电结束");
                            break;
                        case "202":
                            bean.setInfo("充电失败");
                            break;
                        case "300":
                            bean.setInfo("高压充电");
                            break;
                        case "301":
                            bean.setInfo("高压充电结束");
                            qb_zt = true;
                            break;
                        case "302":
                            bean.setInfo("高压充电失败");
                            break;
                        case "400":
                            bean.setInfo("起爆中");
                            break;
                        case "401":
                            bean.setInfo("起爆结束");
//                            fuwei();
                            break;
                        case "402":
                            bean.setInfo("起爆失败");
                            break;
                    }
//                    Log.e(TAG,value + "--A5得到的状态:" + bean.getInfo());
                    break;
                case "2":
                    //电压数据
                    String dy = value.substring(value.length() - 3);
//                    Log.e(TAG, "dy: " + dy);
                    String bv = Utils.addZero(dy, 4);
                    bean.setBusVoltage(calateBv(bv));
                    Log.e(TAG, "B5消息中电压信息:" + calateBv(bv));
                    break;
                case "3":
                    String dl = value.substring(value.length() - 3);
                    String cp = Utils.addZero(dl, 4);
                    bean.setCurrentPeak(showCp(cp));
                    Log.e(TAG, "B5消息中电流信息:" + showCp(cp));
                    break;
                case "4":
                    //拿到全部雷管数量后只展示最后三位数即可
                    bean.setTrueNum(showLgNum(value));
//                    Log.e(TAG, "B5消息中全部雷管数量:" + showLgNum(value));
                    break;
                case "6":
                    //拿到错误雷管数量后只展示最后三位数即可
                    bean.setErrNum(showLgNum(value));
//                    Log.e(TAG, "B5消息中错误数量:" + showLgNum(value));
                    break;
            }
        }
    }

    //得到错误雷管数量
    public static int getErrorLgNum(String str) {
        if (str != null) {
            // 如果第一个字符是 '0'，去掉前导零
            if (str.charAt(0) == '0') {
                return Integer.parseInt(str.substring(1));
            } else {
                return Integer.parseInt(str);
            }
        } else {
            return 0;
        }
    }


    //显示雷管数量
    private String showLgNum(String data) {
        String hexString = data.substring(data.length() - 3);
        String decimal = String.valueOf(Integer.parseInt(hexString, 16));
        if (decimal.length() > 2) {
            return decimal;
        } else {
            return Utils.addZero(decimal, 3);
        }
    }

    //电压
    private float calateBv(String tempData) {
//        String strLow = tempData.substring(0, 2);
//        String strHigh = tempData.substring(2);
        //和40指令的展示电流稍微有些区别：这次是高位在前低位在后  之前是地位在前高位在后 上面注释掉的代码即是40指令展示电流的高低位代码
        String strHigh = tempData.substring(0, 2);
        String strLow = tempData.substring(2);
        int volthigh = Integer.parseInt(strHigh, 16) * 256;
        int voltLowInt = Integer.parseInt(strLow, 16);
        double voltTotal = (volthigh + voltLowInt) * 3.6 * 11 / 4.096 / 1000;//新芯片
        float busVoltage = (float) voltTotal;
        busVoltage = Utils.getFloatToFormat(busVoltage, 2, 4);
        return busVoltage;
    }

    //显示电流
    private String showCp(String tempData) {
//        String strLow = tempData.substring(0, 2);
//        String strHigh = tempData.substring(2);
        //和40指令的展示电流稍微有些区别：这次是高位在前低位在后  之前是地位在前高位在后 上面注释掉的代码即是40指令展示电流的高低位代码
        String strHigh = tempData.substring(0, 2);
        String strLow = tempData.substring(2);
        if (strLow.isEmpty()) {
            strLow = "00";
        }
        if (strHigh.isEmpty()) {
            strHigh = "00";
        }
        int ichigh = Integer.parseInt(strHigh, 16) * 256;
        int icLowInt = Integer.parseInt(strLow, 16);
        double icTotal = (ichigh + icLowInt) * 3.6 / (4.096 * 0.35);//新芯片
        float f1 = (float) (icTotal * 1.8 * 2);//*400//减10是减0带载的电流
        BigDecimal b = new BigDecimal(f1);
        float busCurrent = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//保留两位小数
        if (busCurrent < 0) {
            busCurrent = 0;
        }
        busCurrent = Utils.getFloatToFormat(busCurrent, 0, 4);
        return String.valueOf((int) busCurrent);
    }

    private DeviceBean receiveMsg(String res, String desc) {
        //接收到485消息后处理
        DeviceBean bean = new DeviceBean();
        bean.setRes(res);
        if ("在线".equals(desc)) {
            bean.setCurrentPeak("0μA");
        }
        bean.setCode(res.substring(2));
        bean.setInfo(desc);
        return bean;
    }

    private void initView() {
        adapter = new DeviceAdapter(this, list_device, true);
        adapter.setOnInnerItemOnClickListener(this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void itemClick(View v) {
        switch (v.getId()) {

            case R.id.bt_delete:

                break;
            default:
                break;
        }

    }

    private void initSocket() {
        WebConfig webConfig = new WebConfig();
        webConfig.setPort(9002);
        webConfig.setMaxParallels(10);
        server = new MySocketServer(webConfig, handler_msg);
        server.startServerAsync();
        server.heart();//心跳监听
    }

    private void resetQbLcStatus() {
        isCanSendA8 = false;
        huoqu_end = false;
        currentCount = 0;
        isQbjsCloseLx = true;
        zerCountB1 = 0;
        reciveB1 = false;
        zeroCountA7 = 0;
        reciveB7 = false;
        zeroA8 = 0;
        reciveB8 = false;
        isStopGetLgNum = false;
        showDialog1 = false;
        showDialog2 = false;
        showDialog3 = false;
        showDialog4 = false;
        showDialog5 = false;
        showDialog6 = false;
        showDialog7 = false;
        showDialog8 = false;
        showDialog9 = false;
        showDialog10 = false;
        isGetErr = true;
        lastCheckTimes.clear();
        isCanJc = true;
    }

    private static final long DIYA_TIME_MS = 15000; // 低压暂定15秒
    private static final long GAOYA_TIME_MS = 20000; // 高压暂定20秒
    private Map<String, Long> lastCheckTimes = new HashMap<>();
    private boolean isGetErr = true;//检测结束错误总数量获取一次就不再获取
    private boolean isSaveQbResult = true;//保存起爆历史记录一次即可
    private boolean isQbjsCloseLx = true;//起爆结束后轮询关闭一次即可
    private boolean isReQblc = false;//重新执行起爆流程
    private boolean isXpNoResponse = false;//芯片是否指令无响应
    private Handler handler_msg = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.obj != null) {
                switch (msg.what) {
                    case 0:
                        DeviceBean bean = (DeviceBean) msg.obj;
                        if (bean.getRes() != null && msg.obj != null) {
                            Log.e("判断", "bean.getRes(): " + bean.getRes());
                            String res = bean.getRes();//进页面为空

                            if (res.contains("B0")) {
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
                                    //同步成功 更新设备个数
                                    Message message = handler_msg.obtainMessage();
                                    int collectionSize = list_device.size();
                                    message.what = 15;
                                    message.arg1 = collectionSize;
                                    handler_msg.sendMessage(message);
                                }
                                show_Toast(bean.getCode() + "设备已注册");
                            } else {
                                show_Toast("未识别的设备在连接");
                            }
                        }
                        break;
                    case 7:
                        DeviceBean bn = (DeviceBean) msg.obj;
                        //受控指令
                        if (!isDeviceConnet) {
                            isDeviceConnet = true;
                        }
//                        Log.e(TAG, "case7收到B5消息了：" + bn.toString());
                        for (int a = 0; a < list_device.size(); a++) {
                            list_device.get(a).setTrueNum(bn.getTrueNum());
                            list_device.get(a).setErrNum(bn.getErrNum());
                        }
                        errorNum = getErrorLgNum(bn.getErrNum());
                        Log.e(TAG, "case7中错误雷管个数:" + errorNum);
                        Utils.writeLog("case7中错误雷管个数:" + errorNum);
                        adapter.notifyDataSetChanged();
                        break;
                    case 8:
                        DeviceBean bean4 = (DeviceBean) msg.obj;
                        if (!TextUtils.isEmpty(bean4.getInfo())) {
                            if (bean4.getInfo().equals("检测结束")) {
                                Log.e(TAG, "检测结束: ");
                                jiance_end = false;
                            }
                            if (bean4.getInfo().equals("高压充电结束")) {
                                chongdian_end = false;
                            }
                        }
                        if(daojishi==5||daojishi==0){
                            if (kaiguan) {
                                tvTip.setText(!TextUtils.isEmpty(bean4.getInfo()) ?
                                        bean4.getInfo() : "" + "   ");
                                kaiguan = false;
                            } else {
                                tvTip.setText(!TextUtils.isEmpty(bean4.getInfo()) ?
                                        bean4.getInfo() : "" + "···");
                                kaiguan = true;
                            }
//                            Log.e(TAG,"kaiguan:" + kaiguan + "--daojishi:" + daojishi);
                        }


//                        Log.e(TAG, "case8收到B5消息了：" + bean4.toString());
                        //受控指令
                        if (!isDeviceConnet) {
                            isDeviceConnet = true;
                        }
                        for (int a = 0; a < list_device.size(); a++) {
                            if (list_device.get(a).getCode().equals(bean4.getCode())) {
                                list_device.get(a).setRes(bean4.getRes());
                                list_device.get(a).setInfo(bean4.getInfo());
                                list_device.get(a).setBusVoltage(bean4.getBusVoltage());
                                if ("起爆结束".equals(bean4.getInfo())) {
                                    list_device.get(a).setCurrentPeak("0μA");
                                    list_device.get(a).setBusVoltage(0);
                                } else {
                                    if (!isShowError && bean4.getCurrentPeak() != null) {
                                        list_device.get(a).setCurrentPeak(bean4.getCurrentPeak() + "μA");
                                    }
                                }
                                if (!isStopGetLgNum) {
                                    list_device.get(a).setTrueNum(bean4.getTrueNum());
                                    list_device.get(a).setErrNum(bean4.getErrNum());
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(bean4.getInfo())) {
                            if ("检测结束".equals(bean4.getInfo())) {
                                isCkCwLg = true;
                                isCanJc = true;
                                if (isGetErr) {
                                    isGetErr = false;
                                    errorNum = getErrorLgNum(bean4.getErrNum());
                                    Log.e(TAG, "case8中错误雷管个数:" + errorNum);
                                    Utils.writeLog("case8中错误雷管个数:" + errorNum);
                                }
                                //检测结束  得到错误雷管数量  此时显示出弹窗，询问用户是否需要查看错误雷管
                                if (errorNum > 0 && !showDialog2) {
                                    if (stage == 1) {
                                        Log.e(TAG, "检测结束有错误雷管显示dialog--错误雷管个数:" + errorNum);
                                        showDialog2 = true;
                                        Utils.writeLog("级联页面" + bean4.getInfo() + "展示查看错误雷管dialog");
                                        showErrLgAlertDialog("当前有错误雷管，可点击查看错误雷管按钮查看详情",
                                                "确定");
                                    }
                                }
                            }
                            if ("检测中".equals(bean4.getInfo()) || "高压充电".equals(bean4.getInfo())) {
                                int pdTime = "检测中".equals(bean4.getInfo()) ? 15000 : 25000;
                                if (!TextUtils.isEmpty(String.valueOf(bean4.getBusVoltage()))) {
                                    if (bean4.getBusVoltage() < 6) {
                                        long currentTime = System.currentTimeMillis();
                                        if (!lastCheckTimes.containsKey("isDyyc")) {
                                            lastCheckTimes.put("isDyyc", currentTime);
                                        } else {
                                            long firstTime = lastCheckTimes.get("isDyyc");
                                            if ((currentTime - firstTime) >= pdTime && !showDialog1 && !showDialog3 && !showDialog4) {
                                                Log.e(TAG, "电压异常开启A7线程--倒计时后:" + bean4.getBusVoltage());
                                                isShowError = true;
                                                closeLx();
                                                zeroCountA7 = 0;
                                                reciveB7 = false;
                                                exitRemotePage();
                                                showDialog1 = true;
                                                Utils.writeLog("级联页面" + bean4.getInfo() + "电压异常");
                                                showErrorDialog("当前起爆器电压异常,可能会导致总线短路,请并退出当前页面,检查线路后重新进行级联");
                                            }
                                        }
                                    }
                                }
                                if (!TextUtils.isEmpty(bean4.getCurrentPeak())) {
                                    if (Float.parseFloat(bean4.getCurrentPeak()) <= 8) {
                                        long currentTime = System.currentTimeMillis();
                                        if (!lastCheckTimes.containsKey("isDl")) {
                                            lastCheckTimes.put("isDl", currentTime);
                                        } else {
                                            long firstTime = lastCheckTimes.get("isDl");
                                            if ((currentTime - firstTime) >= pdTime && !showDialog3) {
                                                Log.e(TAG, "断路开启A7线程--倒计时后:" + bean4.getCurrentPeak());
                                                isShowError = true;
                                                closeLx();
                                                zeroCountA7 = 0;
                                                reciveB7 = false;
                                                exitRemotePage();
                                                showDialog3 = true;
                                                Utils.writeLog("级联页面" + bean4.getInfo() + "电流断路");
                                                showErrorDialog("当前电流疑似断路，请退出当前页面,重新进行级联");
                                            }
                                        }
                                    }
                                    //暂时先不加电流过小的判断
//                                    else if (Float.parseFloat(bean4.getCurrentPeak()) < (mListData.size() * 15 * 0.7)
//                                            && Float.parseFloat(bean4.getCurrentPeak()) > 8) {
//                                        long currentTime = System.currentTimeMillis();
//                                        // 记录第一次检测到异常的时间
//                                        if (!lastCheckTimes.containsKey("isDlgx")) {
//                                            lastCheckTimes.put("isDlgx", currentTime);
//                                        } else {
//                                            long firstTime = lastCheckTimes.get("isDlgx");
//                                            // 检查是否超过了 10 秒且尚未显示对话框
//                                            if ((currentTime - firstTime) >= MINIMUM_EXCESS_TIME_MS && !showDialog6) {
//                                                Log.e(TAG, "电流过小开启A7线程--倒计时后:" + bean4.getCurrentPeak());
//                                                isShowError = true;
//                                                closeLx();
//                                                exitRemotePage();
//                                                showDialog6 = true;
//                                                Utils.writeRecord("级联页面" + bean4.getInfo() + "电流过小");
//                                                showErrorDialog("当前电流过小,请排查线路后,重新进行级联");
//                                            }
//                                        }
//                                    }
                                    if ("高压充电".equals(bean4.getInfo())) {
                                        if (Float.parseFloat(bean4.getCurrentPeak()) > 30000) {
                                            long currentTime = System.currentTimeMillis();
                                            if (!lastCheckTimes.containsKey("isSgyDuanLu")) {
                                                lastCheckTimes.put("isSgyDuanLu", currentTime);
                                            } else {
                                                long firstTime = lastCheckTimes.get("isSgyDuanLu");
                                                if ((currentTime - firstTime) >= pdTime && !showDialog7) {
                                                    Log.e(TAG, "升高压短路开启A7线程--倒计时后:" + bean4.getCurrentPeak());
                                                    isShowError = true;
                                                    zeroCountA7 = 0;
                                                    reciveB7 = false;
                                                    closeLx();
                                                    exitRemotePage();
                                                    showDialog7 = true;
                                                    Utils.writeLog("级联页面" + bean4.getInfo() + "电流短路");
                                                    showErrorDialog("当前电流疑似短路,请退出当前页面,重新进行级联");
                                                }
                                            }
                                        } else {
                                            if (Float.parseFloat(bean4.getCurrentPeak()) > (mListData.size() * 15 * 2)) {
                                                long currentTime = System.currentTimeMillis();
                                                if (!lastCheckTimes.containsKey("isGyDlgd")) {
                                                    lastCheckTimes.put("isGyDlgd", currentTime);
                                                } else {
                                                    long firstTime = lastCheckTimes.get("isGyDlgd");
                                                    if ((currentTime - firstTime) >= pdTime && !showDialog10) {
                                                        Log.e(TAG, "高压充电电流过大显示dialog--倒计时后:" + bean4.getCurrentPeak());
                                                        showDialog10 = true;
                                                        Utils.writeLog("级联页面" + bean4.getInfo() + "电流过大");
                                                        showAlertDialog("当前电流过大,建议先排查线路,是否继续进行?",
                                                                "退出", "继续");
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (Float.parseFloat(bean4.getCurrentPeak()) > 21000) {
                                            long currentTime = System.currentTimeMillis();
                                            if (!lastCheckTimes.containsKey("isPtDuanLu")) {
                                                lastCheckTimes.put("isPtDuanLu", currentTime);
                                            } else {
                                                long firstTime = lastCheckTimes.get("isPtDuanLu");
                                                if ((currentTime - firstTime) >= pdTime && !showDialog4) {
                                                    Log.e(TAG, "检测或充电短路开启A7线程--倒计时后:" + bean4.getCurrentPeak());
                                                    isShowError = true;
                                                    zeroCountA7 = 0;
                                                    reciveB7 = false;
                                                    closeLx();
                                                    exitRemotePage();
                                                    showDialog4 = true;
                                                    Utils.writeLog("级联页面" + bean4.getInfo() + "电流短路");
                                                    showErrorDialog("当前电流疑似短路,请退出当前页面,重新进行级联");
                                                }
                                            }
                                        } else {
                                            //参考电流目前15
                                            if (Float.parseFloat(bean4.getCurrentPeak()) > (mListData.size() * 15 * 2)) {
                                                long currentTime = System.currentTimeMillis();
                                                if (!lastCheckTimes.containsKey("isDlgd")) {
                                                    lastCheckTimes.put("isDlgd", currentTime);
                                                } else {
                                                    long firstTime = lastCheckTimes.get("isDlgd");
                                                    if ((currentTime - firstTime) >= pdTime && !showDialog5) {
                                                        Log.e(TAG, "检测或充电电流过大显示dialog--倒计时后:" + bean4.getCurrentPeak());
                                                        showDialog5 = true;
                                                        Utils.writeLog("级联页面" + bean4.getInfo() + "电流过大");
                                                        showAlertDialog("当前电流过大,建议先排查线路,是否继续进行?",
                                                                "退出", "继续");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
//                            if (!"起爆结束".equals(bean4.getInfo()) || !"充电".equals(bean4.getInfo())) {
//                                if (!TextUtils.isEmpty(String.valueOf(bean4.getBusVoltage()))) {
//                                    if (bean4.getBusVoltage() < 6) {
//                                        long currentTime = System.currentTimeMillis();
//                                        if (!lastCheckTimes.containsKey("isDyyc")) {
//                                            lastCheckTimes.put("isDyyc", currentTime);
//                                        } else {
//                                            long firstTime = lastCheckTimes.get("isDyyc");
//                                            if ((currentTime - firstTime) >= pdTime && !showDialog1 && !showDialog3 && !showDialog4) {
//                                                Log.e(TAG, "电压异常开启A7线程--倒计时后:" + bean4.getBusVoltage());
//                                                isShowError = true;
//                                                exitRemotePage();
//                                                closeLx();
//                                                showDialog1 = true;
//                                                Utils.writeRecord("级联页面" + bean4.getInfo() + "电压异常");
//                                                showErrorDialog("当前起爆器电压异常,可能会导致总线短路,请并退出当前页面,检查线路后重新进行级联");
//                                            }
//                                        }
//                                    }
//                                }
//                            }
                            if ("升压失败".equals(bean4.getInfo())) {
                                if (!showDialog8) {
                                    showDialog8 = true;
                                    zeroCountA7 = 0;
                                    reciveB7 = false;
                                    Utils.writeLog("级联页面升压失败");
                                    showErrorDialog("起爆器高压充电失败,请退出当前页面,重新进行级联");
                                }
                            } else if ("高压充电结束".equals(bean4.getInfo())) {
                                //高压充电结束关闭轮询   待B4起爆指令返回后再重新读取等信息  等起爆结束也关闭轮训
                                closeLx();
                            } else if ("起爆结束".equals(bean4.getInfo())) {
                                openHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isSaveQbResult) {
                                            isSaveQbResult = false;
                                            isQbjs = true;
                                            stage = 4;
                                            isCanCd = true;
                                            isCanQb = true;
                                            daojishi = 5;
                                            zeroCountB4 = 0;
                                            reciveB4 = false;
                                            chongdian_end = true;
                                            isCanJc = true;
//                                            resetQbLcStatus();
                                            MmkvUtils.savecode("qbEndTime", System.currentTimeMillis());//应该是从退出页面开始计时
                                            Log.e(TAG, "起爆结束了.....");
                                            MmkvUtils.savecode("endTime", System.currentTimeMillis());
                                            //获取起爆时间,中爆上传用到了时间,会根据日期截取对应的位数,如果修改日期格式,要同时修改中爆上传方法
                                            hisInsertFireDate = Utils.getDateFormatLong(new Date());//记录的起爆时间(可以放到更新ui之后,这样会显得快一点)
                                            saveFireResult();
                                            if (!qbxm_id.equals("-1")) {
                                                updataState(qbxm_id);
                                                Log.e(TAG, "更新起爆状态");
                                            }
                                        }
                                    }
                                }, 1000);
                                openHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isQbjsCloseLx) {
                                            isQbjsCloseLx = false;
                                            closeLx();
                                        }
                                    }
                                },8000);
                            } else if ("起爆失败".equals(bean4.getInfo())) {
                                if (!showDialog9) {
                                    zeroCountA7 = 0;
                                    reciveB7 = false;
                                    closeLx();
                                    exitRemotePage();
                                    showDialog9 = true;
                                    Utils.writeLog("级联页面起爆失败");
                                    MmkvUtils.savecode("endTime", System.currentTimeMillis());
                                    showErrorDialog("起爆失败,请退出当前页面,重新进行级联");
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case 9:
                        DeviceBean db = (DeviceBean) msg.obj;
                        //受控指令
                        if (!isDeviceConnet) {
                            isDeviceConnet = true;
                        }
//                        Log.e(TAG,"case9返回数据了：" + list_device.toString() + "--res:" + db.getRes() +
//                                "--code:" +db.getCode());
                        if (!list_device.contains(db)) {
                            for (int a = 0; a < list_device.size(); a++) {
                                if (list_device.get(a).getCode().equals(db.getCode())) {
                                    list_device.get(a).setRes(db.getRes());
                                    list_device.get(a).setInfo(db.getInfo());
                                    list_device.get(a).setBusVoltage(db.getBusVoltage());
                                    list_device.get(a).setCurrentPeak(db.getCurrentPeak());
                                }
                            }
                            adapter.notifyDataSetChanged();
//                            if (db.getRes().startsWith("B2")) {
//                                Log.e(TAG, "收到B2了开始判断是否发送A8--错误数量:" + errorNum);
//                                if (errorNum > 0) {
//                                    Utils.writeRecord("级联页面有错误雷管开始发送A8查询错误雷管");
//                                    Log.e(TAG, "正在充电有错误雷管--先关闭轮询发送A8指令了,等A8结束了再开启轮询");
//                                    //发送A8指令去修改通信失败雷管状态
//                                    PollingUtils.stopPollingService(WxjlRemoteActivity.this, PollingReceiver.class, PollingUtils.ACTION);
//                                    queryError = new QueryError();
//                                    queryError.start();
//                                } else {
//                                    updateLgStatus(false);
//                                    Log.e(TAG, "无错误雷管，不需要发A8,同时修改数据库错误雷管状态为FF");
//                                    Utils.writeRecord("级联页面无错误雷管");
//                                }
//                            }
                            //只需要获取状态信息和电流即可
                            sendCmd(ThreeFiringCmd.sendWxjlA5(wxjlDeviceId, "122130"));
                            PollingUtils.startPollingService(WxjlRemoteActivity.this, InitConst.POLLING_TIME,
                                    PollingReceiver.class, PollingUtils.ACTION);
                        }
                        break;
                    case 10:
                        String qbkResult = (String) msg.obj;
                        if ("true".equals(qbkResult)) {
                            setZjqThread = new SetZJQThread();
                            setZjqThread.start();
                            Utils.writeLog("级联页面F9指令接收成功");
                            Log.e(TAG, "查看错误雷管--信道已配置:" + xinDaoId + "--启动AB线程了");
                        } else {
                            Utils.writeLog("级联页面切换信道先发送AB指令接收失败");
                            show_Toast("切换信道F9指令失败");
                            Log.e(TAG, "F9指令无返回");
                        }
                        break;
                    case 11:
                        String zjqResult = (String) msg.obj;
                        closeABThread();
                        if ("true".equals(zjqResult)) {
                            if (isReSendAB) {
                                Utils.writeLog("级联页面第一次发送AB指令接收成功，开始发F9指令");
                                sendF9();
                                zeroCountAB = 0;
                                receiveAB = false;
                                isReSendAB = false;
                            } else {
                                Utils.writeLog("级联页面成功切换到1信道");
                                enterNearPage();
                                dialog.dismiss();
                                errlgDialog.dismiss();
                            }
                        } else {
                            Utils.writeLog("级联页面AB指令无响应");
                            show_Toast("切换信道AB指令无返回");
                            Log.e(TAG, "AB指令无返回");
                        }
                        break;
                    case 99:
                        DeviceBean bean2 = (DeviceBean) msg.obj;
                        String res2 = bean2.getRes();
                        Log.e("handler信息", "list_device.size(): " + list_device.size());
                        if (list_device.size() >= 0) {
                            show_Toast("设备：" + bean2.getCode() + "已断线");
                            Utils.writeLog("级联页面设备：" + bean2.getCode() + "已断线");
                            for (int i = 0; i < list_device.size(); i++) {
                                if (bean2.getCode().equals(list_device.get(i).getCode())) {
                                    list_device.remove(i);
                                }
                            }
                            for (int i = 0; i < cmdList.size(); i++) {
                                if (bean2.getCode().equals(cmdList.get(i).getCode())) {
                                    cmdList.remove(i);
                                }
                            }
                            for (int i = 0; i < pollingList.size(); i++) {
                                if (bean2.getCode().equals(pollingList.get(i).getCode())) {
                                    pollingList.remove(i);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            //根据设备个数更新起爆和切换模式按钮
                            Message message = handler_msg.obtainMessage();
                            int collectionSize = list_device.size();
                            message.what = 15;
                            message.arg1 = collectionSize;
                            handler_msg.sendMessage(message);
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
                    case 16:
                        String toastMsg = (String) msg.obj;
                        isXpNoResponse = true;
                        Utils.writeLog("芯片指令无响应:" + toastMsg);
                        Log.e(TAG, "case16返回信息：" + toastMsg);
                        show_centerToast_long(toastMsg);
                        if (toastMsg.contains("级联")) {
                            closeLx();
                            sendCmd(ThreeFiringCmd.sendWxjlA7("01"));
                            closeSerial();
                            lastCheckTimes.clear();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.putExtra("finishRemote", "Y");
                                    intent.putExtra("errorTotalNum", errorNum);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            },1500);
                        }
                        break;
                    case 17:
                        String data = (String) msg.obj;
                        closeA8Thread();
                        Log.e(TAG, "case17返回信息：" + (data.equals("true") ? "A8第一次指令已收到" : "A8无返回"));
                        break;
                    case InitConst.CODE_EXIT:
                        if (isReQblc) {
                            Log.e(TAG,"重新执行起爆流程");
                        } else {
                            if (!isEnterNear) {
                                if (isA7) {
                                    cmdDialog.dismiss();
                                    list_device.clear();
                                    lastReceivedMessages.clear();
                                    adapter.notifyDataSetChanged();
                                    cs = true;
                                    cd_zt = false;
                                    qb_zt = false;
                                    qh = false;
                                    closeLx();
                                    closeSerial();
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("errorTotalNum", errorNum);
                                    resultIntent.putExtra("finishRemote", "Y");
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    Log.e(TAG, "finish远距离页面了errorNum:" + errorNum);
                                    finish();
                                } else {
                                    if (!isShowError) {
                                        closeLx();
                                        closeSerial();
                                        //跳转到近距离页面
                                        Intent intent = new Intent();
                                        intent.putExtra("finishRemote", "Y");
                                        intent.putExtra("errorTotalNum", errorNum);
                                        intent.putExtra("transhighRate", "Y");
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    }
                                }
                            }
                        }
                        break;
                    case InitConst.CODE_UPDAE_STATUS:
                        //接收到子机发送的起爆不同状态消息  此时需更新每个设备的轮询状态
                        if (msg.obj != null) {
                            DeviceBean dc = (DeviceBean) msg.obj;
                            if (dc.getRes() != null && msg.obj != null && dc.getRes().length() == 18) {
                                Log.e("map是", lastReceivedMessages.toString());
                                //得到当前的消息
                                String currentMsg = dc.getRes();
                                String currentCode = dc.getCode();
                                if (lastReceivedMessages.get(currentCode) != null &&
                                        currentMsg.length() >= 4 &&
                                        lastReceivedMessages.get(currentCode).length() >= 4 &&
                                        currentMsg.substring(0, 4).equals(lastReceivedMessages.get(currentCode).substring(0, 4))) {
                                    // 如果上一条接收的消息不为null，并且与当前消息的前四个字符相同，则执行相应的逻辑
                                    Log.e("接受的还是旧消息", "不做处理");
                                } else {
                                    //说明已经处理过当前设备的消息   此时就不再处理
                                    if (dc.getRes().length() > 1) {
                                        String value = dc.getRes().substring(1, 2);
                                        if ("3".equals(value)) {
                                            String lastMsg = lastReceivedMessages.get(currentMsg.substring(0, 4));
                                            lastReceivedMessages.put(currentMsg.substring(0, 4), currentMsg);
                                            for (DeviceBean reBean : list_device) {
                                                if (reBean.getCode().equals(dc.getCode())) {
                                                    reBean.setRes(dc.getRes());
                                                    reBean.setSend(dc.getIsSend());
                                                    reBean.setInfo(dc.getInfo());
                                                    reBean.setErrNum(dc.getErrNum());
                                                    reBean.setCurrentPeak(dc.getCurrentPeak());
                                                    reBean.setTrueNum(dc.getTrueNum());
                                                }
                                            }
//                                            int a = 0;
//                                            for (int i = 0; i < list_device.size(); i++) {
//                                                if (list_device.get(i).getCode().equals(dc.getCode())) {
//                                                    a = i + 1;
//                                                }
//                                            }
//                                            if (list_device.size() != 1 && a < list_device.size()) {
//                                                send485Cmd("A5" + list_device.get(a).getCode());
//                                                // 说明接收到了子机的轮询消息  点击按钮单独接收子机响应的消息时，将list_device作为参数循环去给子机发消息
//                                                Log.e("轮询发送下一条子机消息了，指令", "A5" + list_device.get(a).getCode() + "index是：" + a +
//                                                        list_device.toString());
//                                            }
                                        } else {
                                            lastReceivedMessages.put(currentMsg, currentMsg);
                                            String lastMsg = lastReceivedMessages.get(currentMsg);
                                            Log.e("按钮：旧消息：" + lastMsg + "新消息", currentMsg + "消息已去重");
                                            for (DeviceBean reBean : list_device) {
                                                if (reBean.getCode().equals(dc.getCode())) {
                                                    reBean.setRes(dc.getRes());
                                                    reBean.setSend(dc.getIsSend());
                                                }
                                            }
                                            int a = 0;
                                            for (int i = 0; i < list_device.size(); i++) {
                                                if (list_device.get(i).getCode().equals(dc.getCode())) {
                                                    a = i + 1;
                                                }
                                            }
//                                            if (!"2".equals(value) && list_device.size() != 1 && a < list_device.size()) {
//                                                send485Cmd("A" + dc.getRes().substring(1, 2) + list_device.get(a).getCode());
//                                                // 说明接收到了子机的轮询消息  点击按钮单独接收子机响应的消息时，将list_device作为参数循环去给子机发消息
//                                                Log.e("按钮点击开始发送下一条子机消息了，指令", "A" + dc.getRes().substring(1, 2)
//                                                        + list_device.get(a).getCode() + "index是：" + a + list_device.toString());
//                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case 3:
//                        tvLookError.setText("4.点击查看错误雷管列表");
                        show_Toast("错误雷管读取已结束");
                        break;
                    case 18:
                        Log.e(TAG, "弹出框: ");
                        //弹出框
                        loadErrorBlastModel();
                        if (errDeData.size() > 0) {
                            Log.e(TAG,"case18开始展示错误雷管弹窗");
                            Utils.writeLog("case18开始展示错误雷管弹窗");
                            showErrorLgList();
                        } else {
                            Log.e(TAG,"case18--cwsl为0--不展示错误雷管弹窗");
                            Utils.writeLog("case18--cwsl为0--不展示错误雷管弹窗");
                            show_Toast("当前没有错误雷管");
                        }
                        break;
                    case 19:
                        if(!tvTip.getText().toString().contains("倒计时")){

                        }
                        tvTip.setText("起爆倒计时:" + msg.obj);
                        break;
                    case 20:
                        tvTip.setText("正在执行起爆...");
                        sendA4Cmd = new SendA4Cmd();
                        sendA4Cmd.start();
//                        sendCmd(ThreeFiringCmd.sendWxjlA4("01"));
                        break;
                    case 21:
                        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao()
                                .queryBuilder()
                                .where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF"))
                                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                                .list();
                        Log.e(TAG, "更新雷管状态2: ");
                        for (DenatorBaseinfo baseinfo : list) {
                            updateLgStatus(baseinfo, 1);
                        }
                        Log.e(TAG, "雷管状态重置" );
                        break;
//                    case 22:
//                        String a1Msg = (String) msg.obj;
//                        isXpNoResponse = true;
//                        Utils.writeLog("芯片指令无响应:" + a1Msg);
//                        Log.e(TAG, "case22返回信息：" + a1Msg);
//                        show_centerToast_long(a1Msg);
//                        closeLx();
//                        sendCmd(ThreeFiringCmd.sendWxjlA7("01"));
//                        closeSerial();
//                        lastCheckTimes.clear();
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent();
//                                intent.putExtra("finishRemote", "Y");
//                                intent.putExtra("errorTotalNum", errorNum);
//                                setResult(Activity.RESULT_OK, intent);
//                                finish();
//                            }
//                        }, 1500);
//                        break;
                }
            }
            return false;
        }
    });

    public void show_centerToast_long(String text) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View inflate = layoutInflater.inflate(R.layout.toast_layout, null);
        TextView toast_msg = inflate.findViewById(R.id.toast_msg);
        toast_msg.setText(text);
        Toast toast = new Toast(this);
        toast.setView(inflate);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        new Handler().postDelayed(toast::cancel, 5000); // Dismiss after 10 seconds
    }

    private void getUserMessage() {
        List<MessageBean> message = getDaoSession().getMessageBeanDao().queryBuilder().where(MessageBeanDao.Properties.Id.eq((long) 1)).list();
        if (message.size() > 0) {
            pro_bprysfz = message.get(0).getPro_bprysfz();
            pro_htid = message.get(0).getPro_htid();
            pro_xmbh = message.get(0).getPro_xmbh();
            equ_no = message.get(0).getEqu_no();
            pro_coordxy = message.get(0).getPro_coordxy();
            qiaosi_set = message.get(0).getQiaosi_set();
            ChongDian_time = Integer.parseInt(message.get(0).getChongdian_time());
            pro_dwdm = message.get(0).getPro_dwdm();
            JianCe_time = Integer.parseInt(message.get(0).getJiance_time());
            if (message.get(0).getVersion() != null) {
                version = message.get(0).getVersion();
            }

            Log.e(TAG, "version: " + version);
        }
        Log.e("ChongDian_time", ChongDian_time + "");
        Log.e("JianCe_time", JianCe_time + "");
    }

    //删除历史记录第一行
    private void delHisInfo(String blastdate) {
        if (blastdate == null) return;
        if (getString(R.string.text_alert_tip3).equals(blastdate)) {//"当前雷管记录"
            show_Toast(getString(R.string.text_error_tip52));
            return;
        }
        new GreenDaoMaster().deleteType(blastdate);//删除历史数据

        //从表
        String selection = "blastdate = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {blastdate + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        db.delete(DatabaseHelper.TABLE_NAME_HISDETAIL, selection, selectionArgs);
        //主表
        db.delete(DatabaseHelper.TABLE_NAME_HISMAIN, selection, selectionArgs);
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 更新下载项目中的起爆状态
     */
    public int updataState(String id) {
        Log.e("更新起爆状态", "id: " + id);
        int i = getHisDetailList(hisInsertFireDate);
        ContentValues values = new ContentValues();
        values.put("qbzt", "已起爆");
        values.put("blastdate", hisInsertFireDate);
        values.put("qblgNum", i);
        db.update(DatabaseHelper.TABLE_NAME_SHOUQUAN, values, "id=?", new String[]{"" + id});
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }

    /**
     * 获取起爆历史详细信息
     */
    private int getHisDetailList(String blastdate) {
//        String selection = "blastdate = ? and errorCode = ?"; // 选择条件，给null查询所有//+" and errorCode = ?"   new String[]{"FF"}
//        String[] selectionArgs = {blastdate, "FF"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        String selection = "blastdate = ? "; // 选择条件，给null查询所有//+" and errorCode = ?"   new String[]{"FF"}
        String[] selectionArgs = {blastdate};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_HISDETAIL, null, selection, selectionArgs, null, null, "blastserial asc");
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    private int getHisMaxNumberNo() {
        Cursor cursor = db.rawQuery("select max(serialNo) from " + DatabaseHelper.TABLE_NAME_HISMAIN, null);
        if (cursor != null && cursor.moveToNext()) {
            String maxStr = cursor.getString(0);
            int maxNo = 0;
            if (maxStr != null && maxStr.trim().length() > 0) {
                maxNo = Integer.parseInt(maxStr);
            }
            cursor.close();
            return maxNo;
        }
        return 1;
    }

    private String loadHisMainData() {
        List<DenatorHis_Main> list = getDaoSession().getDenatorHis_MainDao().loadAll();
        Log.e(TAG, "查询第一条历史记录: " + list.get(0).toString());
        return list.get(0).getBlastdate();
    }

    private String getFirstTime() {
        //直接查询出起爆历史记录表中的blastdate（起爆时间）列字段  然后获取到第一条blastdate即可
        List<String> timeList = new ArrayList<>();
        String sql = "select blastdate from denatorHis_Main";
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                timeList.add(cursor.getString(0));
            }
            cursor.close();
            Log.e(TAG, "起爆历史表中数据总条数: "+ timeList.size() + "--第一条数据:" + timeList.get(0));
            return timeList.get(0);
        } else {
            Log.e(TAG, "获取起爆历史表第一条数据起爆时间报错");
            return "";
        }
    }

    /**
     * 保存起爆数据
     */
    public synchronized void saveFireResult() {
        int totalNum = (int) getDaoSession().getDenatorBaseinfoDao().count();//得到数据的总条数
//        Log.e(TAG, "saveFireResult-雷管总数totalNum: " + totalNum);
        if (totalNum < 1) return;
        //如果总数大于30,删除第一个数据
        int hisTotalNum = (int) getDaoSession().getDenatorHis_MainDao().count();//得到起爆历史记录数据的总条数
        Log.e(TAG, "起爆历史记录总条目数: " + hisTotalNum);
        if (hisTotalNum > 30) {
//            String time = loadHisMainData();
            String time = getFirstTime();
            Message message = new Message();
            message.obj = time;
            mHandler_tip.sendMessage(message);
        }
        String xy[] = pro_coordxy.split(",");//经纬度
        int maxNo = getHisMaxNumberNo();
        maxNo++;
        String fireDate = hisInsertFireDate;//Utils.getDateFormatToFileName();
        DenatorHis_Main his = new DenatorHis_Main();
        his.setBlastdate(fireDate);
        his.setUploadStatus("未上传");
        his.setRemark("已起爆");
        his.setEqu_no(equ_no);
        his.setUserid(qbxm_name);
        his.setPro_htid(pro_htid);
        his.setPro_xmbh(pro_xmbh);
        his.setPro_dwdm(pro_dwdm);
        his.setSerialNo(Integer.parseInt(qbxm_id));
        his.setLog(Utils.readLog(Utils.getDate(new Date())));
        if (pro_coordxy.length() > 4) {
            his.setLongitude(xy[0]);
            his.setLatitude(xy[1]);
        }
        getDaoSession().getDenatorHis_MainDao().insert(his);//插入起爆历史记录主表
        Log.e(TAG, "起爆结束，开始插入起爆历史记录表" + fireDate);
        Utils.deleteRecord();//删除日志
        List<DenatorBaseinfo> list = new GreenDaoMaster().queryDetonatorRegionAsc();
        GreenDaoMaster master = new GreenDaoMaster();
        for (DenatorBaseinfo dbf : list) {
            master.updateDetonatorTypezt(dbf.getShellBlastNo(), "已起爆");//更新授权库中状态
            DenatorHis_Detail denatorHis_detail = new DenatorHis_Detail();
            denatorHis_detail.setBlastserial(dbf.getBlastserial());
            denatorHis_detail.setSithole(dbf.getSithole());
            denatorHis_detail.setShellBlastNo(dbf.getShellBlastNo());
            denatorHis_detail.setDenatorId(dbf.getDenatorId());
            denatorHis_detail.setDelay(dbf.getDelay());
            denatorHis_detail.setStatusName(dbf.getStatusName());
            denatorHis_detail.setStatusCode(dbf.getStatusCode());
            denatorHis_detail.setErrorName(dbf.getErrorName());
            denatorHis_detail.setErrorCode(dbf.getErrorCode());
            denatorHis_detail.setAuthorization(dbf.getAuthorization());
            denatorHis_detail.setRemark(dbf.getRemark());
            denatorHis_detail.setBlastdate(fireDate);
            denatorHis_detail.setPiece(dbf.getPiece());
            getDaoSession().getDenatorHis_DetailDao().insert(denatorHis_detail);//插入起爆历史雷管记录表
        }
        Log.e(TAG, "保存起爆历史");
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    private boolean isSend80 = true;//防止用户多次点击频发80
    private boolean isSend81 = true;//防止用户多次点击频发81
    private boolean isSend82 = true;//防止用户多次点击频发82
    private boolean isSend84 = true;//防止用户多次点击频发84
    //全局定义
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 2000; // 快速点击间隔
    private boolean isCanQb = true;//是否可以起爆
    private boolean isCanJc = true;//是否可以检测
    private boolean isCanCd = true;//是否可以充电
    private boolean isQbjs = false;//是否起爆结束
    private boolean isCkCwLg = false;//是否查看错误雷管
    private int stage = 0;//0:初始状态  1:检测   2:充电   3:起爆   4:起爆结束
    private int djsTime = 10000;

    private void reQbSendA1() {
        Message message = new Message();
        message.what = 21;
        message.obj = "更新状态";
        handler_msg.sendMessage(message);
        jiance_end = true;
        errorNum = 0;
        isSaveQbResult = true;
    }

    @OnClick({R.id.btn_net_test, R.id.btn_prepare_charge, R.id.btn_qibao, R.id.btn_err, R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_net_test:
                //之前只能整个起爆流程只能走一次,目前只要不手动点退出按钮或者查看错误雷管后,都可以多次走起爆流程
                if (cs) {
                    //准备检测
                    writeData("A1");//起爆测试指令
                } else {
                    if (stage == 4) {
                        //起爆结束后重新开始起爆流程
                        long tt = System.currentTimeMillis();
                        long et = (long) MmkvUtils.getcode("qbEndTime", (long) 0);
                        if (tt - et < djsTime) {//第二次启动时间不重置
                            int a = (int) (djsTime - (tt - et)) / 1000 + 5;
                            if (a < djsTime) {
                                initDialog_fangdian("当前系统检测到您高压充电后,系统尚未放电成功,目前仍然处于高危状态，为保证检测效果及安全,请等待放电结束后再进行检测", a);
                                return;
                            }
                        }
                        if (isCanJc) {
                            writeData("A1");//起爆测试指令
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    } else if (stage == 1 && isCkCwLg) {
                        if (isCanJc) {
//                            sendCmd(ThreeFiringCmd.sendWxjlA7("01"));
                            writeData("A1");//起爆测试指令
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    } else {
                        show_Toast("请按顺序进行操作");
                    }
                }
                break;
            case R.id.btn_prepare_charge:
                if (jiance_end) {
                    show_Toast("请在检测结束后再进行充电。");
                    return;
                }
                Log.e(TAG, "充电状态cd_zt: "+cd_zt );
                if (cd_zt) {
                    if (isCanCd) {
                        writeData("A2");//准备充电指令
                    } else {
                        show_Toast("请按顺序进行操作");
                    }
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_qibao:
                if (chongdian_end) {
                    show_Toast("请在高压充电结束后再进行起爆。");
                    return;
                }
                Log.e(TAG, "qbFlag: "+qbFlag );
                Log.e(TAG, "qb_zt: "+qb_zt );
                Log.e(TAG, "isCanQb: "+isCanQb);
//                if (cd_zt) {
                    if (qbFlag.equals("qh")) {
                        //切换模式
                        if (qh) {
                            writeData("A6");//切换模式指令
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    } else {
                        //起爆
                        if (qb_zt) {
                            if (isCanQb) {
                                writeData("A4");//准备起爆指令
                            } else {
                                show_Toast("请按顺序进行操作");
                            }
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    }
//                } else {
//                    show_Toast("请按顺序进行操作");
//                }
                break;
            case R.id.btn_exit:
                writeData("A7");//准备退出指令
                break;
            case R.id.btn_err:
                //防止快速点击
                if (jiance_end) {
                    show_Toast("请在检测结束后再查看错误雷管。");
                    return;
                }
                if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                    Log.e("验证", "多次点击: " );
                    show_Toast("正在获取错误雷管,请勿多次点击。");
                    return;
                }
                if (huoqu_end) {
                    show_Toast("正在查询请稍等。");
                    return;
                }
                lastClickTime = System.currentTimeMillis();
                Log.e(TAG, "查看错误雷管按钮--errorNum: " + errorNum);
                Log.e(TAG, "查看错误雷管按钮--reciveB8: " + reciveB8);
                Utils.writeLog("查看错误雷管按钮errorNum: " + errorNum + "--reciveB8: " + reciveB8);
                if (errorNum == 0) {
                    show_Toast("当前没有错误雷管");
                    List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao()
                            .queryBuilder()
                            .where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF"))
                            .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                            .list();
                    Log.e(TAG, "更新雷管状态1: ");
                    for (DenatorBaseinfo baseinfo : list) {
                        updateLgStatus(baseinfo, 1);
                    }
                    Utils.writeLog("查看错误雷管按钮--当前无错误雷管，所有雷管状态重置为FF");
                    Log.e(TAG, "当前无错误雷管，所有雷管状态重置为FF" );
                    return;
                }
                if (errorNum > 0 && !reciveB8) {
                    show_Toast("读取错误雷管中，请勿重复点击...");
                    Utils.writeLog("级联页面有错误雷管开始发送A8查询错误雷管");
                    Log.e(TAG, "正在充电有错误雷管--先关闭轮询发送A8指令了,等A8结束了再开启轮询");
                    Log.e(TAG, "reciveB8" + reciveB8);
                    //发送A8指令去修改通信失败雷管状态
                    PollingUtils.stopPollingService(WxjlRemoteActivity.this, PollingReceiver.class, PollingUtils.ACTION);
                    queryError = new QueryError();
                    queryError.start();
                    huoqu_end = true;
                } else {
                    loadErrorBlastModel();
                    if (errDeData.size() > 0) {
                        Log.e(TAG,"查看错误雷管按钮--开始展示错误雷管弹窗");
                        Utils.writeLog("查看错误雷管按钮--开始展示错误雷管弹窗");
                        showErrorLgList();
                    } else {
                        Utils.writeLog("查看错误雷管按钮--cwsl为0--不展示错误雷管弹窗");
                        Log.e(TAG,"查看错误雷管按钮--cwsl为0--不展示错误雷管弹窗");
                    }
                }
//                }
                break;
        }
    }

    AlertDialog cmdDialog;
    String tip = "";
    private boolean isShowCmdDialog = true;//保证就算快速先后点击指令按钮时，也只会弹出第一个弹窗
    private void writeData(final String data) {
        switch (data) {
            case "A1":
                tip = "检测";
                break;
            case "A2":
                tip = "充电";
                break;
            case "A4":
                tip = "起爆";
                break;
            case "A6":
                tip = "起爆";
                break;
            case "A7":
                tip = "退出";
                break;
        }
        String content = tip.equals("A7") ? "确定要" + tip : "确定进行" + tip;
        if (!WxjlRemoteActivity.this.isFinishing()) {
            if (isShowCmdDialog) {
                isShowCmdDialog = false;
                cmdDialog = new AlertDialog.Builder(WxjlRemoteActivity.this)
                        .setTitle("提示")
                        .setMessage(content + "吗？")
                        .setPositiveButton("确定", (dialog1, which) -> {
                            if (!"A7".equals(data)) {
                                show_Toast("正在执行" + tip);
                                Utils.writeLog("级联页面正在执行" + data + "指令");
                                dialog1.dismiss();
                            }
                            switch (Build.DEVICE) {
                                case "T-QBZD-Z6":
                                case "M900":
                                    if (reciveB0) {
                                        setTipText(data);
                                    } else {
                                        show_Toast("设备同步失败，请退出APP重新同步");
                                    }
                                    break;
                                default:
                                    if (server != null) {
                                        server.writeData(data, list_device);
                                    }
                                    break;
                            }
                        }).setNeutralButton("取消", (dialog1, which) -> {
                            if ("A4".equals(data)) {
                                isCanQb = true;
                            } else if ("A7".equals(data)) {
                                if (stage == 3) {
                                    qb_zt = false;
                                } else {
                                    qb_zt = true;
                                    isCanQb = true;
                                    isSaveQbResult = true;
                                    isQbjsCloseLx = true;
                                    isCanCd = true;
                                }
                                isGetErr = true;
                            }
                        })
                        .create();
                cmdDialog.setCanceledOnTouchOutside(false);
                cmdDialog.show();
                cmdDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isShowCmdDialog = true;
                    }
                });
            } else {
                show_Toast("请按顺序进行操作");
                Log.e(TAG,"重复弹窗给提示了");
            }
        }
    }

    AlertDialog dialog,errlgDialog;

    public void showAlertDialog(String content, String cancleText, String sureText) {
        if (!WxjlRemoteActivity.this.isFinishing()) {
            dialog = new AlertDialog.Builder(WxjlRemoteActivity.this)
                    .setTitle("系统提示")
                    .setMessage(content)
                    .setCancelable(false)
                    .setPositiveButton(sureText, (dialog1, which) -> {
                        dialog1.dismiss();
                    })
                    //设置对话框的按钮
                    .setNeutralButton(cancleText, (dialog1, which) -> {
                        isReQblc = false;
                        isShowError = true;
                        isA7 = true;
                        zeroCountA7 = 0;
                        reciveB7 = false;
                        closeLx();
                        exitRemotePage();
                        dialog1.dismiss();
                    }).create();
            dialog.setCanceledOnTouchOutside(false);  // 设置点击对话框外部不消失
            dialog.show();
        }
    }

    public void showErrLgAlertDialog(String content,String sureText) {
        if (!WxjlRemoteActivity.this.isFinishing()) {
            errlgDialog = new AlertDialog.Builder(WxjlRemoteActivity.this)
                    .setTitle("系统提示")
                    .setMessage(content)
                    .setCancelable(false)
                    //设置对话框的按钮
                    .setNeutralButton(sureText, (dialog1, which) -> {
                        closeLx();
                        dialog1.dismiss();
                    }).create();
            errlgDialog.setCanceledOnTouchOutside(false);  // 设置点击对话框外部不消失
            errlgDialog.show();
        }
    }

    AlertDialog errorAialog;

    private void showErrorDialog(String tip) {
        if (!WxjlRemoteActivity.this.isFinishing()) {
            errorAialog = new AlertDialog.Builder(WxjlRemoteActivity.this)
                    .setTitle("系统提示")//设置对话框的标题
                    .setMessage(tip)//设置对话框的内容
                    .setCancelable(false)
                    //设置对话框的按钮
                    .setNeutralButton("退出", (dialog1, which) -> {
                        sendCmd(ThreeFiringCmd.sendWxjlA7("01"));
                        isReQblc = true;
                        closeSerial();
                        dialog1.dismiss();
                        lastCheckTimes.clear();
                        Intent intent = new Intent();
                        intent.putExtra("finishRemote", "Y");
                        intent.putExtra("errorTotalNum", errorNum);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }).create();
            errorAialog.setCanceledOnTouchOutside(false);  // 设置点击对话框外部不消失
            errorAialog.show();
        }
    }

    private boolean isEnterNear = false;

    private void enterNearPage() {
        isEnterNear = true;
        sendCmd(ThreeFiringCmd.sendWxjlA7("01"));
        xinDaoValue = "CH1-19.2kbps-1FEC";
        MmkvUtils.savecode("xinDaoValue", xinDaoValue);
        MmkvUtils.savecode("xinDao", 1);
        closeLx();
        closeSerial();
        //跳转到近距离页面
        Intent intent = new Intent();
        intent.putExtra("finishRemote", "Y");
        intent.putExtra("errorTotalNum", errorNum);
        intent.putExtra("isQueryError", "Y");
        intent.putExtra("transhighRate", "Y");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private boolean threadStarted = false;
    private void exitRemotePage() {
        exitJl = new ExitJl();
        exitJl.start();
        Log.e(TAG, "已开启A7线程--reciveB7:" + reciveB7 + "--zeroCountA7:" + zeroCountA7);
    }

    private boolean isA7 = false;
    private int num = 0;

    private void setTipText(String data) {
        switch (data) {
            case "A1":
                if (stage == 4) {
                    isReQblc = true;
                    resetQbLcStatus();
                    sendCmd(ThreeFiringCmd.sendWxjlA7("01"));
                    Log.e(TAG, "isQbjs:" + isQbjs + "--isQbjsCloseLx:" + isQbjsCloseLx +
                            "发送起爆检测A1指令");
                    reQbSendA1();
                    zeroA8 = 0;
                    reciveB8 = false;
                    isCanJc = false;
                } else if (stage == 1 && isCkCwLg) {
                    isReQblc = true;
                    sendCmd(ThreeFiringCmd.sendWxjlA7("01"));
                    zeroCountB4 = 0;
                    reciveB4 = false;
                    resetQbLcStatus();
                    Log.e(TAG, "isCkCwLg:" + isCkCwLg + "发送起爆检测A1指令");
                    reQbSendA1();
                    isCanJc = false;
                } else {
                    Log.e(TAG, "首次起爆流程--发送起爆检测A1指令");
                }
                stage = 1;
                tvTip.setText("正在检测...");
                if (isReQblc) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendA1Cmd = new SendA1Cmd();
                            sendA1Cmd.start();
                        }
                    }, 500);
                } else {
                    sendA1Cmd = new SendA1Cmd();
                    sendA1Cmd.start();
                }
                break;
            case "A2":
                stage = 2;
                tvTip.setText("正在充电...");
                isStopGetLgNum = true;
                sendCmd(ThreeFiringCmd.sendWxjlA2("01"));
                if (isReQblc) {
                    isCanJc = false;
                }
                isCanCd = false;
                break;
            case "A4":
                stage = 3;
                DaoJiShiThread daoJiShiThread = new DaoJiShiThread();
                daoJiShiThread.start();
                isCanQb = false;
                qb_zt = false;
                break;
            case "A6":
                stage = 3;
                tvTip.setText("正在执行起爆...");
                sendCmd(ThreeFiringCmd.sendWxjlA6("01", "01"));
                fuwei();
                break;
            case "A7":
                isReQblc = false;
                stage = 0;
                isA7 = true;
                tvTip.setText("执行退出指令...");
                reciveB7 = false;
                zeroCountA7 = 0;
                exitRemotePage();
//                sendCmd(ThreeFiringCmd.setWxjlA7("01"));
                break;
        }
    }

    private void fuwei() {
        cs = true;
        cd_zt = false;
        qb_zt = false;
        qh = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            isCanQb = true;
            isCanCd = true;
            isReQblc = false;
            reciveB7 = false;
            zeroCountA7 = 0;
            writeData("A7");//准备退出指令
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
//        Log.e(TAG,"eventBus收到: " + event.getMsg());
        if (event.getMsg().equals("pollingService")) {
            // 到时间了  发起轮询消息
            Iterator<Map.Entry<String, String>> iterator = lastReceivedMessages.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (next.getKey().startsWith("B5")) {
                    iterator.remove();
                }
            }
//            Log.e(TAG,"A5线程监控次数:" + zeroCount);
            //询问检测状态（15）  电流信息（33） 全部雷管数量（42）   错误雷管数量（60）
            if (isStopGetLgNum) {
                //只需要获取状态信息和电流即可
                sendCmd(ThreeFiringCmd.sendWxjlA5(wxjlDeviceId, "122130"));
            } else {
                //检测过程中状态充电信息雷管数据都需要获取展示
                sendCmd(ThreeFiringCmd.sendWxjlA5(wxjlDeviceId, "1423324160"));
            }
            if (zeroCount > 0 && zeroCount <= 8 && !reciveB5) {
//                Log.e(TAG,"A5线程监控次数,次数：" + zeroCount);
            } else if (zeroCount > 8) {
                Log.e(TAG, "A5指令未返回已发送8次，停止发送A5指令");
                if (!isCloseLx) {
                    Message message = new Message();
                    message.what = 16;
                    message.obj = "操作超时，稍候退出APP后请您再重新进行无线级联";
                    Utils.writeLog("级联页面A5指令芯片命令无响应了");
                    handler_msg.sendMessage(message);
                }
            }
            zeroCount++;
        }
    }

    private void sendA8() {
        //发送A8指令查看错误雷管信息
        Log.e(TAG, "发送A8命令了");
        String b = Utils.intToHex(currentCount + 1);
        String serId = Utils.addZero(b, 2);
        sendCmd(ThreeFiringCmd.sendWxjlA8(wxjlDeviceId, serId));
    }


    /***
     * 加载错误雷管
     */
    private void loadErrorBlastModel() {
        errDeData.clear();
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryErrLeiGuan();//带参数是查一个区域,不带参数是查所有
        for (DenatorBaseinfo d : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("serialNo", d.getBlastserial());
            item.put("konghao", d.getDuan() + "-" + d.getDuanNo());
            item.put("shellNo", d.getShellBlastNo());
            item.put("errorName", d.getErrorName());
            item.put("delay", d.getDelay());
            item.put("piece", d.getPiece());
            errDeData.add(item);
        }
        Log.e(TAG, "数据库查询错误雷管总数: " + errDeData.size());
    }

    private void showErrorLgList() {
        if (!WxjlRemoteActivity.this.isFinishing()) {
            Log.e(TAG,"错误雷管弹窗已展示");
            Utils.writeLog("错误雷管弹窗已展示");
            LayoutInflater inflater = LayoutInflater.from(this);
            View getlistview = inflater.inflate(R.layout.firing_error_listview, null);
            LinearLayout llview = getlistview.findViewById(R.id.ll_dialog_err);
            llview.setVisibility(View.GONE);
            // 给ListView绑定内容
            ListView errlistview = getlistview.findViewById(R.id.X_listview);
            ErrListAdapter mAdapter = new ErrListAdapter(this, errDeData, R.layout.firing_error_item);
            errlistview.setAdapter(mAdapter);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("错误雷管列表");
            builder.setView(getlistview);
            builder.setNegativeButton("确定", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setCancelable(false);
            builder.create().show();
        } else {
            Utils.writeLog("错误雷管弹窗无法展示");
            Log.e(TAG,"错误雷管弹窗无法展示");
        }
    }

    private boolean receive84 = false;//发出84命令是否返回

    private class QueryError2 extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;
            while (!exit) {
                try {
                    if (receive84) {
                        exit = true;
                        break;
                    }
                    if (zeroCount > 0 && zeroCount <= 5 && !receive84) {
                        send84();
                        Log.e(TAG, "发送84查询错误雷管指令");
                        Thread.sleep(1500);
                    } else if (zeroCount > 5) {
                        Log.e(TAG, "84指令未返回已发送5次，停止发送84指令");
                        exit = true;
                        Message message = new Message();
                        message.what = 5;
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

    private void send84() {
        //发送84指令查看错误雷管信息
        String b = Utils.intToHex(currentCount + 1);
        String serId = Utils.addZero(b, 2);
        sendCmd(ThreeFiringCmd.sendWxjl84("01", serId));
    }

    private boolean isCanSend84 = false;


    private void doWith84(String completeCmd) {
        /**
         * 芯片84指令一次最多给返回10条错误雷管数据，如超过10，则需要再次发送84指令获取剩下的错误雷管
         * 84指令：84后面:错误数量  序号(发送84的次数) 错误雷管编号)  错误雷管：C00184 0B 01 0D001500200046004F0051005400560059005A005C00 0642C0
         * 截取出错误雷管的序号，0300 0500 0D00（转化时，低位在前，高位在后）就是发84指令时候的雷管顺序
         * 得通过这个序号找到对应的雷管id给错误雷管更新状态
         * 同时在当前页面展示出错误雷管列表
         */
        currentCount++;
        Log.e(TAG, "收到84了:" + completeCmd + "--当前发送84次数：" + currentCount);
        String errorLgCmd = completeCmd.substring(10, completeCmd.length() - 6);
        //取到错误雷管cmd后  4个一组  每个都只取前两位  将其转为十进制就可以知道是错误芯片的81发送顺序  然后再找到对应的雷管id 再改变通信状态
        int aa = errorLgCmd.length() / 4;
        for (int i = 0; i < aa; i++) {
            String value = errorLgCmd.substring(4 * i, 4 * (i + 1));
            String idCmd = value.substring(0, 4);
//            int id = Integer.parseInt(idCmd, 16);
            int id = hexToDecimalLowHigh(idCmd);
            for (int j = 0; j < mListData.size(); j++) {
                if (id == j + 1) {
                    //得到当前的错误雷管index  denatorId即为错误雷管的芯片ID
                    String blastNo = mListData.get(j).getShellBlastNo();
                    Log.e(TAG, "错误雷管编号:" + id + "--错误雷管id:" + blastNo + "-现在去更新数据库的状态了");
                    updateLgStatus(mListData.get(j), 2);
                }
            }
        }
        int maxCount = 10;//芯片一次最多返回20条错误雷管
        int sendCount = (errorTotalNum % maxCount) > 0 ? (errorTotalNum / maxCount) + 1 : errorTotalNum / maxCount;
        if (currentCount >= sendCount) {
            end84 = true;
            Log.e(TAG, "错误雷管数量小于20，不需要发84命令了");
            Message message = new Message();
            message.what = 3;
            handler_msg.sendMessage(message);
            return;
        }
        send84();
        Log.e(TAG, "错误数量>20，发84了--错误总数量:" + errorTotalNum + "--需要发送84的次数是:" + sendCount);
    }
    //B8 01 04 01010102 01030104 B8
    // 转换方法
    private int hexToDecimalLowHigh(String hexStr) {
        // 确保输入是有效的16进制字符串并且长度是4
        if (hexStr.length() != 4 || !hexStr.matches("[0-9A-Fa-f]{4}")) {
            throw new IllegalArgumentException("输入必须是一个4位的有效16进制字符串");
        }
        // 分割低位和高位
        String lowByte = hexStr.substring(0, 2);
        String highByte = hexStr.substring(2);
        // 合并低位和高位
        String combinedHexStr = highByte + lowByte;
        // 转换为十进制
        return Integer.parseInt(combinedHexStr, 16);
    }

    private int daojishi = 5;

    private class DaoJiShiThread extends Thread {
        public volatile boolean exit = false;

        public void run() {
            while (!exit) {
                try {

                    if (daojishi > 0) {
                        Log.e(TAG, "daojishi-----:" + daojishi);
                        Message message = new Message();
                        message.what = 19;
                        message.obj = daojishi;
                        handler_msg.sendMessage(message);
                        Thread.sleep(1000);
                        daojishi--;
                    } else {
                        exit = true;
                        Message message = new Message();
                        message.what = 20;
                        message.obj = daojishi;
                        handler_msg.sendMessage(message);
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void closeDaoJiShiThread() {
        if (daoJiShiThread != null) {
            daoJiShiThread.exit = true;  // 终止线程thread
            daoJiShiThread.interrupt();
            Log.e(TAG, "daoJiShiThread线程已关闭");
        }
    }

    private TextView mOffTextView;
    private Handler mOffHandler;
    private java.util.Timer mOffTime;
    private android.app.Dialog mDialog;

    private void initDialog_fangdian(String tip, int daojishi) {
        Log.e(TAG, "倒计时: " + daojishi);
        mOffTextView = new TextView(this);
        mOffTextView.setTextSize(25);
        mOffTextView.setText(tip + "\n放电倒计时：");
        mDialog = new AlertDialog.Builder(this)
                .setTitle("系统提示")
                .setCancelable(false)
                .setView(mOffTextView)
//                .setPositiveButton("确定", (dialog, id) -> {
//                    mOffTime.cancel();//清除计时
//                    stopXunHuan();//关闭后的一些操作
//                })
                .setNeutralButton("确定", (dialog, id) -> {
                    dialog.cancel();
                    mOffTime.cancel();
                })
//                .setNegativeButton("继续", (dialog2, which) -> {
//                    dialog2.dismiss();
//                    //可以重新检测了
//                    Log.e(TAG, "起爆结束，重新发起起爆流程检测A1指令");
//                    writeData("A1");//起爆测试指令
//                    mOffTime.cancel();
//                })
                .create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);

        mOffHandler = new Handler(msg -> {
            if (msg.what > 0) {
                //动态显示倒计时
                mOffTextView.setText(tip + "\n放电倒计时：" + msg.what);
            } else {
                //倒计时结束自动关闭
                if (mDialog != null) {
                    mDialog.dismiss();

                }
//                off();//关闭后的操作
                mOffTime.cancel();//终止此计时器，丢弃任何当前计划的任务
                mOffTime.purge();//从此计时器的任务队列中删除所有取消的任务
            }
            return false;
        });

        //倒计时

        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            private int countTime = daojishi;

            public void run() {
//                if(countTime==0){
//                    mOffTime.cancel();
//                    mOffTime.purge();
//                }
                if (countTime > 0) {
                    countTime--;
                }
                Log.e(TAG, "countTime: " + countTime);
                Message msg = new Message();
                msg.what = countTime;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 1000, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (server != null) {
                server.stopServerAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeA0Thread();
        closeA1Thread();
        closeA4Thread();
        closeA7Thread();
        closeA8Thread();
        closeABThread();
        closeDaoJiShiThread();
        if (errorAialog != null && errorAialog.isShowing()) {
            errorAialog.dismiss();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (errlgDialog != null && errlgDialog.isShowing()) {
            errlgDialog.dismiss();
        }
        openHandler.removeCallbacksAndMessages(null);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
