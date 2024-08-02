package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.custom.DeviceAdapter;
import android_serialport_api.xingbang.custom.ListViewForScrollView;
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

public class WxjlRemoteActivity extends SerialPortActivity {

    @BindView(R.id.lv)
    ListViewForScrollView lv;
    MySocketServer server;
    @BindView(R.id.btn_net_test)
    Button btnNetTest;
    @BindView(R.id.btn_prepare_charge)
    Button btnPrepareCharge;
    @BindView(R.id.btn_qibao)
    Button btnQibao;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.lv_net)
    ListViewForScrollView lvNet;
    @BindView(R.id.lv_qibao)
    ListViewForScrollView lvQibao;
    @BindView(R.id.lv_chongdian)
    ListViewForScrollView lvChongdian;
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
    public boolean cd = false;
    public boolean qb = false;
    public boolean qh = false;
    private boolean isStopGetLgNum = false;//充电开始就不再获取雷管数量了，在充电前获取雷管数量并记录下来展示即可
    private ConcurrentHashMap<String, String> lastReceivedMessages = new ConcurrentHashMap<>();
    //起爆按钮点击时，值为qh:发送A6命令（切换模式）   值为qb:发送A4命令（起爆）
    private String qbFlag = "qb";
    private String TAG = "无线级联远距离页面";
    private String wxjlDeviceId = "";
    private SyncDevices syncDevices;
    private SendA1Cmd sendA1Cmd;
    private boolean reciveB0 = false;//发出同步命令是否返回
    private boolean reciveB1 = false;//发出起爆测试命令是否返回
    private boolean reciveB5 = false;//发出A5命令是否返回
    private int zeroCount = 1;//A5指令无返回的次数
    private boolean isOpenLowRate = false;//是否开启2400低频
    int rate = isOpenLowRate ? 2400 : 115200;
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
    Handler openHandler = new Handler();//重新打开串口
    /**
     * 默认为0:不显示弹窗
     * 1:显示电流过大的弹窗  2:显示电压异常的弹窗
     * 3:显示错误雷管的弹窗  4:显示高压充电失败的弹窗
     * 5:显示起爆失败的弹窗
     */
    private boolean showDialog1;
    private boolean showDialog2;
    private boolean showDialog3;
    private boolean showDialog4;
    private boolean showDialog5;
    private boolean isGetErrorLg = false;//是否已经发送过获取错误雷管信息的命令
    private int errorNum;//错误雷管数量
    private int currentCount;//当前84发送的次数
    private List<DenatorBaseinfo> mListData = new ArrayList<>();

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
        Log.e(TAG,"项目编号：" + qbxm_id + "--项目名称：" + qbxm_name);
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


    private void closeSerial(){
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
//        if (isOpenLowRate) {
//            //通讯速率需从115200降到2400
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    mApplication.closeSerialPort();
//                    Log.e(TAG, "调用mApplication.closeSerialPort()开始关闭串口了。。");
//                    mSerialPort = null;
//                }
//            }).start();
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            initSerialPort(rate);
//            Log.e(TAG, "3秒后重新打开串口，将波特率降为" + rate);
//        }
        openSerial();
        initData();
    }

    private void openSerial() {
        openHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initSerialPort(rate);
                Log.e(TAG, "重新打开串口，波特率为" + rate);
            }
        }, 2500);
    }

    private void initData() {
        wxjlDeviceId = !TextUtils.isEmpty(getIntent().getStringExtra("wxjlDeviceId")) ?
                getIntent().getStringExtra("wxjlDeviceId") : "01";
        Log.e(TAG,"设备号：" + getIntent().getStringExtra("wxjlDeviceId"));
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
                        Log.e(TAG,"发送A0同步指令");
                        sendCmd(ThreeFiringCmd.setWxjlA0(wxjlDeviceId));
                        Thread.sleep(1500);
                    } else if (zeroCount > 5){
                        Log.e(TAG,"A0指令未返回已发送5次，停止发送A0指令");
                        Message message = new Message();
                        message.what = 16;
                        message.obj = "设备同步失败，请退出APP重新同步";
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

    private class SendA1Cmd extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;
            while (!exit) {
                try {
                    if (reciveB1) {
                        exit = true;
                        break;
                    }
                    if (zeroCount > 0 && zeroCount <= 5 && !reciveB1) {
                        Log.e(TAG,"发送A1同步指令");
                        sendCmd(ThreeFiringCmd.setWxjlA1(wxjlDeviceId));
                        Thread.sleep(1500);
                    } else if (zeroCount > 5){
                        Log.e(TAG,"A1指令未返回已发送5次，停止发送A1指令");
                        Message message = new Message();
                        message.what = 16;
                        message.obj = "起爆测试失败，请退出APP重新级联";
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

    /**
     * 处理芯片返回命令
     */
    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        Log.e(TAG + "-返回命令", fromCommad);
        Utils.writeLog("<-:" + fromCommad);
        doWithReceivData(fromCommad);//处理cmd命令
    }

    private void closeA0Thread() {
        if (syncDevices != null) {
            syncDevices.exit = true;  // 终止线程thread
            syncDevices.interrupt();
        }
    }

    private void closeA1Thread() {
        if (syncDevices != null) {
            syncDevices.exit = true;  // 终止线程thread
            syncDevices.interrupt();
        }
    }

    private void closeLx(){
        PollingUtils.stopPollingService(WxjlRemoteActivity.this, PollingReceiver.class, PollingUtils.ACTION);
        Log.e(TAG, "轮询已关闭");
    }

    /**
     * 处理芯片返回命令
     */
    private void doWithReceivData(String res) {
        Message msg = new Message();
        if (res.startsWith(DefCommand.CMD_MC_SEND_B0)) {
            //已收到同步B0指令
            reciveB0 = true;
            Log.e(TAG,"已收到B0指令,不再发送A0指令");
            closeA0Thread();
            //同步
            msg.what = 0;
            DeviceBean bean = new DeviceBean();
            bean.setRes(res);
            bean.setCurrentPeak("0");
            bean.setCode(res.substring(res.length() - 2));
            bean.setInfo("在线");
            msg.obj = bean;
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B1)) {
            reciveB1 = true;
            closeA1Thread();
            cs = false;
            cd = true;
            // 起爆检测
            msg.what = 9;
            msg.obj = receiveMsg(res, "在线");
            //开启轮询  10秒轮询一次接收到的消息  来更新当前设备列表信息
            if (isStopGetLgNum) {
                //只需要获取状态信息和电流即可
                sendCmd(ThreeFiringCmd.setWxjlA5(wxjlDeviceId,"122130"));
            } else {
                //检测过程中状态充电信息雷管数据都需要获取展示
                sendCmd(ThreeFiringCmd.setWxjlA5(wxjlDeviceId,"1423324160"));
            }
            PollingUtils.startPollingService(WxjlRemoteActivity.this, InitConst.POLLING_TIME,
                    PollingReceiver.class, PollingUtils.ACTION);
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B2)) {
            qb = true;
            qh = true;
            // 充电
            msg.what = 9;
            msg.obj = receiveMsg(res, "正在充电");
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B3)) {
            // 充电升高压
            msg.what = 9;
            msg.obj = receiveMsg(res, "升高压中");
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B4)) {
            fuwei();
            // 单个设备起爆
            msg.what = 9;
            msg.obj = receiveMsg(res, "起爆中");
        } else if (res.startsWith("4")) {
            //频率降下来后  芯片返回的命令总是两截的：雷管全部数量和错误数量是单独发一条  所以这么来处理
            Log.e(TAG,"收到雷管数据的B5消息是：" + res);
            DeviceBean bean = new DeviceBean();
            receB5Data(bean,res,1);
            msg.what = 7;
            msg.obj = bean;
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B5)) {
            reciveB5 = true;
            zeroCount = 0;
            Log.e(TAG,"得到的B5消息是：" + res);
            DeviceBean bean = new DeviceBean();
            receB5Data(bean,res,2);
            msg.what = 8;
            msg.obj = bean;
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B6)) {
            // 切换模式起爆
            msg.what = 9;
            msg.obj = receiveMsg(res, "起爆中");
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B7)) {
            // 退出
            msg.what = InitConst.CODE_EXIT;
            msg.obj = receiveMsg(res, "");
        } else if (res.startsWith(DefCommand.CMD_MC_SEND_B8)) {
            // 获取错误雷管信息
            Log.e(TAG,"收到B8命令了res:" + res);
            doWithReceivB8(res);
        } else {
            Log.e(TAG, "无线级联远距离页面-没有匹配对应的命令-cmd:" + res);
        }
        handler_msg.sendMessage(msg);
    }

    private void doWithReceivB8(String completeCmd) {
            /**
             * 芯片84指令一次最多给返回20条错误雷管数据，如超过20，则需要再次发送84指令获取剩下的错误雷管
             * 拿到84指令后，展示错误雷管数据  3发错误雷管：C00184 03 0300 0500 0D00 C6DDC0
             * 截取出错误雷管的序号，0300 0500 0D00就是发81指令时候的雷管顺序  得通过这个序号找到对应的雷管id给错误雷管更新状态
             * 同时在当前页面展示出错误雷管列表
             */
            currentCount ++;
            Log.e(TAG, "收到B8命令了--完整的B8指令:" + completeCmd + "--当前发送B8次数：" + currentCount);
            String errorLgCmd = completeCmd.substring(6);
            Log.e(TAG,"错误雷管cmd是:" + errorLgCmd);
            //取到错误雷管cmd后  2个一组  每个都只取前两位  将其转为十进制就可以知道是错误芯片的81发送顺序  然后再找到对应的雷管id 再改变通信状态
            int aa = errorLgCmd.length() / 4;
            for (int i = 0; i < aa; i++) {
                String value = errorLgCmd.substring(4 * i, 4 * (i + 1));
                String idCmd = value.substring(0,2);
                int id = Integer.parseInt(idCmd, 16);
                Log.e(TAG,"cmd错误编号：" + idCmd + "--完整的cmd错误编号：" + value + "--十进制错误编号：" + id);
                for (int j = 0; j < mListData.size(); j++) {
                    if (id == j + 1) {
                        //得到当前的错误雷管index  denatorId即为错误雷管的芯片ID
                        String denatorId = mListData.get(j).getDenatorId();
                        Log.e(TAG,"错误雷管id：" + denatorId + "-现在去更新数据库的状态了");
                        updateLgStatus(mListData.get(j),2);
                    }
//                    else {
//                        updateLgStatus(mListData.get(j),1);
//                    }
                }
            }
            Log.e(TAG,"错误雷管数量：" + errorNum);
            int maxCount = 20;//芯片一次最多返回20条错误雷管
            int sendCount = (errorNum % maxCount) > 0 ? (errorNum / maxCount) + 1 : errorNum / maxCount;
            Log.e(TAG,"需要发送B8的次数是:" + sendCount);
            if (currentCount >= sendCount) {
                Log.e(TAG,"错误雷管数量小于20，不需要发B8命令了");
                Message message = new Message();
                message.what = 3;
                message.obj = "true";
                handler_msg.sendMessage(message);
                return;
            }
            sendA8();
            Log.e(TAG,"错误雷管数量大于20，再次发B8命令了");
    }

    private void updateLgStatus(DenatorBaseinfo dbf,int type) {
        //更新雷管状态
        DenatorBaseinfo denator = Application.getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(dbf.getShellBlastNo())).unique();
        denator.setErrorCode(type == 1 ? "FF" : "00");
        denator.setErrorName(type == 1 ? getString(R.string.text_communication_state4) :
                getString(R.string.text_communication_state1));
        Log.e(TAG,"充电中更新雷管通信状态了。。。" + denator.getShellBlastNo() + "--" + denator.getErrorCode() +
                "--" + denator.getErrorName());
        Application.getDaoSession().update(denator);
    }

    private void receB5Data(DeviceBean bean,String res,int type) {
        Log.e(TAG,"处理接收到的B5消息：" + res + "--type : " + type);
        int index;
        if (type == 1) {
            index = 0;
        } else {
            bean.setRes(res);
            bean.setCode(res.substring(2,4));
            index = 1;
        }
        // 1:检测状态  2：电压数据   3：电流数据   4：雷管总数   6：错误数量
        int aa = res.length() / 4;
        for (int i = index; i < aa; i++) {
            String value = res.substring(4 * i, 4 * (i + 1));
            switch (value.substring(0, 1)){
                case "1":
                    //根据不同的值展示状态
                    Log.e(TAG,"得到的B5消息中拿到检测状态了" + value.substring(value.length() - 3));
                    /**
                     * 001：同步      100：检测中      101：检测结束
                     * 200：正在充电   201：充电结束    202：充电失败
                     * 300：升高压中   301：升高压结束  302：升高压失败
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
                            break;
                        case "200":
                            bean.setInfo("正在充电");
                            break;
                        case "201":
                            bean.setInfo("充电结束");
                            break;
                        case "202":
                            bean.setInfo("充电失败");
                            break;
                        case "300":
                            bean.setInfo("升高压中");
                            break;
                        case "301":
                            bean.setInfo("升压结束");
                            break;
                        case "302":
                            bean.setInfo("升压失败");
                            break;
                        case "400":
                            bean.setInfo("起爆中");
                            break;
                        case "401":
                            bean.setInfo("起爆结束");
                            break;
                        case "402":
                            bean.setInfo("起爆失败");
                            break;
                    }
                    break;
                case "2":
                    //电压数据
                    String dy = value.substring(value.length() - 3);
                    String bv = Utils.addZero(dy, 4);
                    bean.setBusVoltage(calateBv(bv));
                    Log.e(TAG,"得到的B5消息中拿到电压信息了:" + dy + "--补零后：" + bv + "--要显示的电压是：" + calateBv(bv));
                    break;
                case "3":
                    String dl = value.substring(value.length() - 3);
                    String cp = Utils.addZero(dl, 4);
                    bean.setCurrentPeak(showCp(cp));
                    Log.e(TAG,"得到的B5消息中拿到电流信息了:" + dl + "--补零后：" + cp + "--要显示的电流是：" + showCp(cp));
                    break;
                case "4":
                    //拿到全部雷管数量后只展示最后三位数即可
                    bean.setTrueNum(showLgNum(value));
                    Log.e(TAG,"得到的B5消息中拿到全部雷管数量了:" + showLgNum(value));
                    break;
                case "6":
                    //拿到错误雷管数量后只展示最后三位数即可
                    bean.setErrNum(showLgNum(value));
                    Log.e(TAG,"得到的B5消息中拿到错误数量了:" + showLgNum(value));
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
        //可调电压版本,系数为0.011,不可调为0.006
//				double voltTotal =(volthigh+voltLowInt)/4.095*3.0 * 0.006;
        double voltTotal = (volthigh + voltLowInt) * 3.0 * 11 / 4.096 / 1000;//新芯片
//				double voltTotal =(volthigh+voltLowInt)/4.095*3.0 * 0.011;//可调电压
        float busVoltage = (float) voltTotal;
        busVoltage = Utils.getFloatToFormat(busVoltage, 2, 4);
        Log.e(TAG,"得到的电压数据：" + busVoltage);
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
//				double icTotal =(ichigh+ icLowInt)/4.096*3.0 * 0.0098;//普通版本
        double icTotal = (ichigh + icLowInt) * 3.0 / (4.096 * 0.35);//新芯片
        float f1 = (float) (icTotal*1.8*2)-10;//*400//减10是减0带载的电流
        BigDecimal b = new BigDecimal(f1);
        float busCurrent = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//保留两位小数
        if (busCurrent < 0) {
            busCurrent = 0;
        }
        busCurrent = Utils.getFloatToFormat(busCurrent, 0, 4);
        return String.valueOf((int)busCurrent);
    }

    private DeviceBean receiveMsg(String res, String desc) {
        //接收到485消息后处理
        DeviceBean bean = new DeviceBean();
        bean.setRes(res);
        bean.setCode(res.substring(2));
        bean.setInfo(desc);
        return bean;
    }

    private void initView() {
        adapter = new DeviceAdapter(this, list_device, true);
        lv.setAdapter(adapter);
    }

    private void initSocket() {
        WebConfig webConfig = new WebConfig();
        webConfig.setPort(9002);
        webConfig.setMaxParallels(10);
        server = new MySocketServer(webConfig, handler_msg);
        server.startServerAsync();
        server.heart();//心跳监听
    }

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
                    case 15:
                        //根据设备个数  超过1个时，点起爆按钮发送切换模式命令  1个就还是发送起爆命令
                        if (msg.arg1 <= 1) {
                            qbFlag = "qb";
                        } else {
                            qbFlag = "qh";
                        }
                        break;
                    case 16:
                        //根据设备个数  超过1个时，点起爆按钮发送切换模式命令  1个就还是发送起爆命令
                        String toastMsg = (String) msg.obj;
                        if (toastMsg.contains("级联")) {
                            closeLx();
                        }
                        Log.e(TAG,"case16吐司：" + toastMsg);
                        show_Toast_long(toastMsg);
                        break;
                    case InitConst.CODE_EXIT:
                        closeLx();
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
                    case 7:
                        DeviceBean bn = (DeviceBean) msg.obj;
                        //受控指令
                        if (!isDeviceConnet) {
                            isDeviceConnet = true;
                        }
                        Log.e(TAG, "收到不完整的B5消息数据了：" + list_device.toString() + "--res:" +
                                bn.getTrueNum() + bn.getErrNum());
                        for (int a = 0; a < list_device.size(); a++) {
                            list_device.get(a).setTrueNum(bn.getTrueNum());
                            list_device.get(a).setErrNum(bn.getErrNum());
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case 8:
                        DeviceBean bean4 = (DeviceBean) msg.obj;
                        //受控指令
                        if (!isDeviceConnet) {
                            isDeviceConnet = true;
                        }
                        Log.e(TAG,"B5指令返回数据了：" + list_device.toString() + "--res:" + bean4.getRes() +
                                "--code:" +bean4.getCode());
                        if (!list_device.contains(bean4)) {
                            for (int a = 0; a < list_device.size(); a++) {
                                if (list_device.get(a).getCode().equals(bean4.getCode())) {
                                    list_device.get(a).setRes(bean4.getRes());
                                    list_device.get(a).setInfo(bean4.getInfo());
                                    if (bean4.getCurrentPeak() != null) {
                                        list_device.get(a).setCurrentPeak(bean4.getCurrentPeak());
                                    }
                                    if (!isStopGetLgNum) {
                                        list_device.get(a).setTrueNum(bean4.getTrueNum());
                                        list_device.get(a).setErrNum(bean4.getErrNum());
                                    }
                                }
                            }
                            if (!TextUtils.isEmpty(bean4.getCurrentPeak()) && !showDialog1) {
                                if (Float.parseFloat(bean4.getCurrentPeak()) > 21000) {
                                    showDialog1 = true;
                                    showErrorDialog("当前电流过大,请检查线夹等部位是否存在浸水或母线短路等情况,排查处理浸水后,重新进行级联");
                                }
                            }
                            if (!"起爆结束".equals(bean4.getInfo()) && !TextUtils.isEmpty(String.valueOf(bean4.getBusVoltage())) && !showDialog2) {
                                if (bean4.getBusVoltage() < 6) {
                                    showDialog2 = true;
                                    showErrorDialog("当前起爆器电压异常,可能会导致总线短路,请并退出当前页面,检查线路后重新进行级联");
                                }
                            }
                            if (!TextUtils.isEmpty(bean4.getInfo())) {
                                if ("检测结束".equals(bean4.getInfo())) {
                                    //检测结束  得到错误雷管数量  此时显示出弹窗，询问用户是否需要查看错误雷管
                                    if (getErrorLgNum(bean4.getErrNum()) > 0  && !showDialog3) {
                                        errorNum = getErrorLgNum(bean4.getErrNum());
                                        showDialog3 = true;
                                        showAlertDialog("系统提示", "当前有错误雷管，是否继续进行?",
                                                "查看错误雷管", "继续", 1);
                                    }
                                } else if ("正在充电".equals(bean4.getInfo()) && !isGetErrorLg) {
                                    sendA8();//发送A8指令去修改通信失败雷管状态
                                } else if ("升压失败".equals(bean4.getInfo()) && !showDialog4) {
                                    showDialog4 = true;
                                    showErrorDialog("起爆器高压充电失败,请退出当前页面,重新进行级联");
                                } else if ("起爆结束".equals(bean4.getInfo())) {
                                    if (!TextUtils.isEmpty(bean4.getCurrentPeak()) && bean4.getCurrentPeak().equals("0")) {
                                        Log.e(TAG, "起爆结束了.....");
                                        MmkvUtils.savecode("endTime", System.currentTimeMillis());
                                        closeLx();
                                        //获取起爆时间,中爆上传用到了时间,会根据日期截取对应的位数,如果修改日期格式,要同时修改中爆上传方法
                                        hisInsertFireDate = Utils.getDateFormatLong(new Date());//记录的起爆时间(可以放到更新ui之后,这样会显得快一点)
                                        saveFireResult();
                                        if (!qbxm_id.equals("-1")) {
                                            updataState(qbxm_id);
                                            Log.e(TAG, "更新起爆状态");
                                        }
                                    }
                                } else if ("起爆失败".equals(bean4.getInfo()) && !showDialog5) {
                                    showDialog5 = true;
                                    MmkvUtils.savecode("endTime", System.currentTimeMillis());
                                    showErrorDialog("起爆失败,请退出当前页面,重新进行级联");
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case 9:
                        DeviceBean db = (DeviceBean) msg.obj;
                        //受控指令
                        if (!isDeviceConnet) {
                            isDeviceConnet = true;
                        }
                        Log.e(TAG,"充电返回数据了：" + list_device.toString() + "--res:" + db.getRes() +
                                "--code:" +db.getCode());
                        if (!list_device.contains(db)) {
                            for (int a = 0; a < list_device.size(); a++) {
                                if (list_device.get(a).getCode().equals(db.getCode())) {
                                    list_device.get(a).setRes(db.getRes());
                                    list_device.get(a).setInfo(db.getInfo());
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
                }
            }
            return false;
        }
    });

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

    /**
     * 保存起爆数据
     */
    public synchronized void saveFireResult() {
        int totalNum = (int) getDaoSession().getDenatorBaseinfoDao().count();//得到数据的总条数
//        Log.e(TAG, "saveFireResult-雷管总数totalNum: " + totalNum);
        if (totalNum < 1) return;
        //如果总数大于30,删除第一个数据
        int hisTotalNum = (int) getDaoSession().getDenatorHis_MainDao().count();//得到雷管表数据的总条数
//        Log.e(TAG, "saveFireResult-历史记录条目数hisTotalNum: " + hisTotalNum);
        if (hisTotalNum > 30) {
            String time = loadHisMainData();
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
        Log.e(TAG,"起爆结束，开始插入起爆历史记录表" + fireDate);
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
        Log.e(TAG,"保存起爆历史");
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    @OnClick({R.id.btn_net_test, R.id.btn_prepare_charge, R.id.btn_qibao, R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_net_test:
                if (cs) {
                    //准备测试
                    Log.e(TAG, "发送起爆测试A1指令");
                    writeData("A1");//起爆测试指令
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_prepare_charge:
                if (cd) {
                    writeData("A2");//准备充电指令
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_qibao:
                if (cd) {
                    if (qbFlag.equals("qh")) {
                        //切换模式
                        if (qh) {
                            writeData("A6");//切换模式指令
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    } else {
                        //起爆
                        if (qb) {
                            writeData("A4");//准备起爆指令
                        } else {
                            show_Toast("请按顺序进行操作");
                        }
                    }
                } else {
                    show_Toast("请按顺序进行操作");
                }
                break;
            case R.id.btn_exit:
                writeData("A7");//准备退出指令
                break;
        }
    }

    private void writeData(final String data) {
        String tip = "";
        switch (data) {
            case "A1":
                tip = "起爆测试";
                break;
            case "A2":
                tip = "准备充电";
                break;
            case "A4":
                tip = "起爆";
                break;
            case "A6":
                tip = "起爆";
                break;
            case "A7":
                tip = "退出流程";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确定进行" + tip + "吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    show_Toast("正在执行" + data);
                    switch (Build.DEVICE) {
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

                })
                .setNeutralButton("取消", null)
                .show();
    }

    public void showAlertDialog(String title,String content,String cancleText,String sureText,int type) {
        if (!WxjlRemoteActivity.this.isFinishing()) {
            AlertDialog dialog = new AlertDialog.Builder(WxjlRemoteActivity.this)
                    .setTitle("系统提示")
                    .setMessage(content)
                    .setCancelable(false)
                    .setPositiveButton(sureText,(dialog1, which) -> {
                        dialog1.dismiss();
                    })
                    //设置对话框的按钮
                    .setNeutralButton(cancleText, (dialog1, which) -> {
                        dialog1.dismiss();
                        exitRemotePage();
                        //跳转到近距离页面（如果当前页面低速通道已开启，此时需先将波特率升至115200高速通道再跳转）
                        Intent intent = new Intent(WxjlRemoteActivity.this,WxjlNearActivity.class);
                        //由于现在都是高速波特率  所以暂时先注释此代码
                        intent.putExtra("errorTotalNum",errorNum);
                        intent.putExtra("transhighRate","Y");
                        startActivity(intent);
                    }).create();
            dialog.setCanceledOnTouchOutside(false);  // 设置点击对话框外部不消失
            dialog.show();
        }
    }

    private void showErrorDialog(String tip) {
        if (!WxjlRemoteActivity.this.isFinishing()) {
            AlertDialog dialog = new AlertDialog.Builder(WxjlRemoteActivity.this)
                    .setTitle("系统提示")//设置对话框的标题//"成功起爆"
                    .setMessage(tip)//设置对话框的内容"本次任务成功起爆！"
                    .setCancelable(false)
                    //设置对话框的按钮
                    .setNegativeButton("退出", (dialog1, which) -> {
                        exitRemotePage();
                        dialog1.dismiss();
                    }).create();
            dialog.setCanceledOnTouchOutside(false);  // 设置点击对话框外部不消失
            dialog.show();
        }
    }

    private void exitRemotePage(){
        closeLx();
        sendCmd(ThreeFiringCmd.setWxjlA7("01"));
        closeSerial();
        finish();
    }

    private void setTipText(String data) {
        switch (data) {
            case "A1":
                tvTip.setText("正在起爆测试...");
                sendA1Cmd = new SendA1Cmd();
                sendA1Cmd.start();
//                sendCmd(ThreeFiringCmd.setWxjlA1("01"));
                break;
            case "A2":
                tvTip.setText("正在充电...");
                isStopGetLgNum = true;
                sendCmd(ThreeFiringCmd.setWxjlA2("01"));
                break;
            case "A4":
                tvTip.setText("正在执行起爆...");
                sendCmd(ThreeFiringCmd.setWxjlA4("01"));
                break;
            case "A6":
                tvTip.setText("正在执行起爆...");
                sendCmd(ThreeFiringCmd.setWxjlA6("01", "01"));
                fuwei();
                break;
            case "A7":
                tvTip.setText("执行退出指令...");
                sendCmd(ThreeFiringCmd.setWxjlA7("01"));
                list_device.clear();
                lastReceivedMessages.clear();
                adapter.notifyDataSetChanged();
                cs = true;
                cd = false;
                qb = false;
                qh = false;
                break;
        }
    }

    private void fuwei() {
        cs = true;
        cd = false;
        qb = false;
        qh = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            sendCmd(ThreeFiringCmd.setWxjlA7("01"));
            finish();
        }
        return true;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
        Log.e(TAG,"eventBus收到: " + event.getMsg());
        if (event.getMsg().equals("pollingService")) {
            // 到时间了  发起轮询消息
            Iterator<Map.Entry<String, String>> iterator = lastReceivedMessages.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                if (next.getKey().startsWith("B5")) {
                    iterator.remove();
                }
            }
            Log.e(TAG,"A5线程监控次数:" + zeroCount);
            //询问检测状态（15）  电流信息（33） 全部雷管数量（42）   错误雷管数量（60）
            if (isStopGetLgNum) {
                //只需要获取状态信息和电流即可
                sendCmd(ThreeFiringCmd.setWxjlA5(wxjlDeviceId,"122130"));
            } else {
                //检测过程中状态充电信息雷管数据都需要获取展示
                sendCmd(ThreeFiringCmd.setWxjlA5(wxjlDeviceId,"1423324160"));
            }
            if (zeroCount > 0 && zeroCount <= 8 && !reciveB5) {
                Log.e(TAG,"A5线程监控次数,次数：" + zeroCount);
            } else if (zeroCount > 8){
                Log.e(TAG,"A5指令未返回已发送8次，停止发送A5指令");
                Message message = new Message();
                message.what = 16;
                message.obj = "芯片无响应，请退出APP重新级联";
                handler_msg.sendMessage(message);
            }
            zeroCount++;
        }
    }

    private void sendA8() {
        //发送A8指令查看错误雷管信息
        Log.e(TAG,"发送A8命令了");
        sendCmd(ThreeFiringCmd.setWxjlA8(wxjlDeviceId));
        isGetErrorLg = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendCmd(ThreeFiringCmd.setWxjlA7("01"));
        try {
            if (server != null) {
                server.stopServerAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeLx();
        closeA0Thread();
        closeA1Thread();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
