package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.custom.ErrListAdapter;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.upload.InitConst;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WxjlNearActivity extends SerialPortActivity {
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_send_data)
    TextView tvSendData;
    @BindView(R.id.tv_enter_jcms)
    TextView tvEnterJcms;
    @BindView(R.id.tv_look_error)
    TextView tvLookError;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.btn_register)
    RelativeLayout btnRegister;
    @BindView(R.id.btn_exit)
    RelativeLayout btnExit;
    @BindView(R.id.btn_send_data)
    RelativeLayout btnSendData;
    @BindView(R.id.btn_enter_jcms)
    RelativeLayout btnEnterJcms;
    @BindView(R.id.btn_look_error)
    RelativeLayout btnLookError;
    private int sendNum = 1;
    private String deviceId = "", dataLength81 = "", data81 = "", dataLength82 = "", data82 = "", serId = "";
    private String TAG = "无线级联近距离页面";
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private Handler handler_msg = new Handler();
    private RegisterDevices rDevices;
    private EnterJcms enterJcms;
    private boolean receive80 = false;//发出80命令是否返回
    private boolean receive81 = false;//81命令是否结束
    private boolean receive82 = false;//发出82命令是否返回
    private boolean receive84 = false;//发出84命令是否返回
    private String flag = "";//接收是否需要将波特率升至115200
    Handler openHandler = new Handler();//重新打开串口
    private int currentCount;//当前84发送的次数
    private boolean isOpened = false;//串口是否已打开
    private ArrayList<Map<String, Object>> errDeData = new ArrayList<>();//错误雷管
    private int errorTotalNum;//错误雷管总数
    private int clickType = 1;// 1:80  2:81  3:82  4:84
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
        flag = (getIntent().getStringExtra("transhighRate") != null) ?
                getIntent().getStringExtra("transhighRate") : "";
        errorTotalNum = getIntent().getIntExtra("errorTotalNum",0);
        if (TextUtils.isEmpty(flag)) {
            errorTotalNum = getIntent().getIntExtra("errorNum",0);
        }
        Log.e(TAG,"intent传值的错误雷管总数：" + errorTotalNum);
        openHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initSerialPort(InitConst.TX_RATE);
                Log.e(TAG, "重新打开串口，波特率为" + InitConst.TX_RATE);
                isOpened = true;
                show_Toast("串口已打开");
            }
        }, 2000);
        mListData = new GreenDaoMaster().queryDetonatorRegionAsc();
        Log.e(TAG,"雷管总数量：" + mListData.size());
        handler_msg = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:
                        String registResult = (String) msg.obj;
                        if ("true".equals(registResult)) {
                            tvRegister.setText("1.设备已注册");
                            show_Toast("设备已注册");
                        } else {
                            tvRegister.setText("1.设备注册");
                            sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
                            show_Toast("设备注册失败，请退出APP后再重新注册");
                        }
                        close80Thread();
                        break;
                    case 1:
                        tvSendData.setText("2.数据传输结束");
                        show_Toast("数据传输结束");
                        receive81 = true;
                        break;
                    case 2:
                        String jcmsResult = (String) msg.obj;
                        if ("true".equals(jcmsResult)) {
//                            sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
                            tvEnterJcms.setText("3.数据检测结束");
                            show_Toast("数据检测结束");
                            closeSerial();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            EventBus.getDefault().post(new FirstEvent("is81End", "Y"));
                            enterRemotePage();
                        } else {
                            tvEnterJcms.setText("3.进入检测模式");
                            show_Toast("数据检测失败，请退出APP后再重新检测");
                        }
                        close82Thread();
                        break;
                    case 3:
                        tvLookError.setText("4.点击查看错误雷管列表");
                        show_Toast("错误雷管读取已结束");
                        break;
                }
                return false;
            }
        });
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

    private void enterRemotePage() {
        finish();
        Intent intent = new Intent(WxjlNearActivity.this, WxjlRemoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("wxjlDeviceId", deviceId);
        startActivity(intent);
    }

    private void close80Thread() {
        if (rDevices != null) {
            rDevices.exit = true;
            rDevices.interrupt();
        }
    }

    private void close82Thread() {
        if (enterJcms != null) {
            enterJcms.exit = true;
            enterJcms.interrupt();
        }
    }

    private class RegisterDevices extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;
            while (!exit) {
                try {
                    if (receive80) {
                        exit = true;
                        break;
                    }
                    if (zeroCount > 0 && zeroCount <= 5 && !receive80) {
                        Log.e(TAG, "发送80注册指令");
                        sendCmd(ThreeFiringCmd.sendWxjl80(deviceId));
                        Thread.sleep(1500);
                    } else if (zeroCount > 5) {
                        Log.e(TAG, "80指令未返回已发送5次，停止发送80指令");
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
                        sendCmd(ThreeFiringCmd.sendWxjl82(deviceId, dataLength82, data82));
                        Log.e(TAG, "发送82进入检测模式指令");
                        Thread.sleep(1500);
                    } else if (zeroCount > 5) {
                        Log.e(TAG, "82指令未返回已发送5次，停止发送82指令");
                        exit = true;
                        Message message = new Message();
                        message.what = 2;
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

    private String mAfter = "";
    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        if ((!fromCommad.startsWith("C0") || !fromCommad.endsWith("C0")) && TextUtils.isEmpty(mAfter)) {
            Log.e(TAG, "AA拼接前：" + mAfter + "--mAfter为空拼接后:" + fromCommad);
            mAfter = fromCommad;
        } else if (!fromCommad.startsWith("C0") || !fromCommad.endsWith("C0")) {
            fromCommad = mAfter + fromCommad;
            Log.e(TAG, "BB拼接前：" + mAfter + "--mAfter不为空拼接后:" + fromCommad);
            mAfter = "";
        }
        Log.e(TAG + "-返回命令", fromCommad);
        Utils.writeLog("<-:" + fromCommad);
        if (fromCommad.startsWith("C0") && fromCommad.endsWith("C0") && fromCommad.length() > 12) {
            String cmd = DefCommand.getCmd(fromCommad);
            if (cmd != null) {
                int localSize = fromCommad.length() / 2;
                byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                doWithReceivData(cmd, fromCommad);
            }
        } else {
            Log.e(TAG, "-返回命令不完整" + fromCommad);
        }
    }

    /***
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, String completeCmd) {
        if (DefCommand.CMD_5_TRANSLATE_80.equals(cmd)) {//80 无线级联：进行设备注册
            Log.e(TAG, "收到80命令了");
            receive80 = true;
            Message message = new Message();
            message.what = 0;
            message.obj = "true";
            handler_msg.sendMessage(message);
        } else if (DefCommand.CMD_5_TRANSLATE_81.equals(cmd)) {//81 无线级联：子节点与主节点进行数据传输
            Log.e(TAG, "收到81命令了");
            sendNum++;
            send81cmd();
        } else if (DefCommand.CMD_5_TRANSLATE_82.equals(cmd)) {//82 无线级联：进入检测模式
            Log.e(TAG, "收到82命令了");
            EventBus.getDefault().post(new FirstEvent("is81End", "Y"));
            receive82 = true;
            Message message = new Message();
            message.what = 2;
            message.obj = "true";
            handler_msg.sendMessage(message);
        } else if (DefCommand.CMD_5_TRANSLATE_84.equals(cmd)) {//84 无线级联：读取错误雷管
            doWith84(completeCmd);
        } else {
            Log.e(TAG, TAG + "-返回命令没有匹配对应的命令-cmd:" + cmd);
        }
    }

    private void doWith84(String completeCmd) {
        /**
         * 芯片84指令一次最多给返回20条错误雷管数据，如超过20，则需要再次发送84指令获取剩下的错误雷管
         * 拿到84指令后，展示错误雷管数据  3发错误雷管：C00184 03 0300 0500 0D00 C6DDC0
         * 截取出错误雷管的序号，0300 0500 0D00就是发81指令时候的雷管顺序  得通过这个序号找到对应的雷管id给错误雷管更新状态
         * 同时在当前页面展示出错误雷管列表
         */
        currentCount ++;
        Log.e(TAG, "收到84命令了--完整的84指令:" + completeCmd + "--当前发送84次数：" + currentCount);
        String errorLgCmd = completeCmd.substring(8, completeCmd.length() - 6);
//        Log.e(TAG,"错误雷管cmd是:" + errorLgCmd);
        //取到错误雷管cmd后  4个一组  每个都只取前两位  将其转为十进制就可以知道是错误芯片的81发送顺序  然后再找到对应的雷管id 再改变通信状态
        int aa = errorLgCmd.length() / 4;
        for (int i = 0; i < aa; i++) {
            String value = errorLgCmd.substring(4 * i, 4 * (i + 1));
            String idCmd = value.substring(0,2);
            int id = Integer.parseInt(idCmd, 16);
//            Log.e(TAG,"cmd错误编号：" + idCmd + "--完整的cmd错误编号：" + value + "--十进制错误编号：" + id);
            for (int j = 0; j < mListData.size(); j++) {
                if (id == j + 1) {
                    //得到当前的错误雷管index  denatorId即为错误雷管的芯片ID
                    String blastNo = mListData.get(j).getShellBlastNo();
                    Log.e(TAG,"错误雷管id：" + blastNo + "-现在去更新数据库的状态了");
                    updateLgStatus(mListData.get(j),2);
                }
            }
        }
        Log.e(TAG,"错误雷管数量：" + errorTotalNum);
        int maxCount = 20;//芯片一次最多返回20条错误雷管
        int sendCount = (errorTotalNum % maxCount) > 0 ? (errorTotalNum / maxCount) + 1 : errorTotalNum / maxCount;
        Log.e(TAG,"需要发送84的次数是:" + sendCount);
        if (currentCount >= sendCount) {
            receive84 = true;
            Log.e(TAG,"错误雷管数量小于20，不需要发84命令了");
            Message message = new Message();
            message.what = 3;
            handler_msg.sendMessage(message);
            return;
        }
        send84();
        Log.e(TAG,"错误雷管数量大于20，再次发84命令了");
    }

    private void updateLgErrorStatus() {
        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao()
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF"))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
//        Log.e(TAG,"错误雷管数量：" + list.size());
        for (DenatorBaseinfo baseinfo : list) {
            updateLgStatus(baseinfo,1);
        }
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
        Log.e(TAG, "errDeData: " + errDeData.toString());
    }


    private void showErrorLgList() {
        if (!WxjlNearActivity.this.isFinishing()) {
            loadErrorBlastModel();
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
            builder.create().show();
        }
    }

    private void updateLgStatus(DenatorBaseinfo dbf,int type) {
        //更新雷管状态
        DenatorBaseinfo denator = Application.getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(dbf.getShellBlastNo())).unique();
        denator.setErrorCode(type == 1 ? "FF" : "00");
        denator.setErrorName(type == 1 ? getString(R.string.text_communication_state4) :
                getString(R.string.text_communication_state1));
//        Log.e(TAG,"84命令更新雷管通信状态了。。。" + denator.getShellBlastNo() + "--" + denator.getErrorCode() +
//                "--" + denator.getErrorName());
        Application.getDaoSession().update(denator);
    }

    private void send81cmd() {
        //10条数据发一次   81指令：C0+设备号+81+数据体长度+数据体+后面跟通用的一样
        int dataLength;
        //limit字段：设置每次81指令发送几条雷管数据
        int limit = 10;
        if (mListData.size() >= limit) {
            dataLength = mListData.size() / limit >= sendNum ? (limit * 9 + 1) : mListData.size() / limit + 1 >= sendNum ? Math.max(mListData.size() % limit, 0) * 9 + 1 : 1;
        } else {
            dataLength = mListData.size() % limit * 9 + 1;
        }
        if (!(dataLength > 1)) {
            handler_msg.sendMessage(handler_msg.obtainMessage(1));
            EventBus.getDefault().post(new FirstEvent("is81End", "Y"));
            Log.e(TAG, "81已结束");
            return;
        }
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
        Log.e("81指令", "十进制datalength：" + dataLength + dataLength81 + serId + data81);
        sendCmd(ThreeFiringCmd.sendWxjl81(deviceId, dataLength81, serId, data81));
    }

    private boolean isSend84 = false;//防止用户多次点击频发84
    @OnClick({R.id.btn_register, R.id.btn_exit, R.id.btn_send_data, R.id.btn_enter_jcms, R.id.btn_look_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (!isOpened) {
                    show_Toast("正在打开串口，打开后您再操作");
                    return;
                }
                if (receive80) {
                    show_Toast("设备已注册，请勿重复注册!");
                } else {
                    show_Toast("设备注册中...");
                    tvRegister.setText("1.设备注册中...");
                    rDevices = new RegisterDevices();
                    rDevices.start();
                }
                break;
            case R.id.btn_send_data:
                if (!isOpened) {
                    show_Toast("正在打开串口，打开后您再操作");
                    return;
                }
                if (!receive80) {
                    show_Toast("请先进行设备注册!");
                    return;
                }
                if (receive81) {
                    show_Toast("数据传输已结束，无需重复传输!");
                } else {
                    //雷管数据10条发一次  但目前暂定1条发一次
                    show_Toast("数据传输中...");
                    tvSendData.setText("2.数据传输中...");
                    send81cmd();
                }
                break;
            case R.id.btn_enter_jcms:
                if (!isOpened) {
                    show_Toast("正在打开串口，打开后您再操作");
                    return;
                }
                if (!receive80) {
                    show_Toast("请先进行设备注册!");
                    return;
                }
                if (receive82) {
                    show_Toast("数据检测已结束，无需重复检测!");
                } else {
                    show_Toast("数据检测中...");
                    tvEnterJcms.setText("3.数据检测中...");
                    enterJcms = new EnterJcms();
                    enterJcms.start();
                }
                break;
            case R.id.btn_look_error:
                if (!isOpened) {
                    show_Toast("正在打开串口，打开后您再操作");
                    return;
                }
                if (TextUtils.isEmpty(flag) && errorTotalNum <= 0) {
                    show_Toast("当前无错误雷管");
                    return;
                }
                //发送84指令查看错误雷管信息
                if (receive84) {
                    tvLookError.setText("4.点击查看错误雷管列表");
                    showErrorLgList();
                } else {
                    tvLookError.setText("4.读取错误雷管中...");
                    updateLgErrorStatus();
                    send84();
                }
                break;
            case R.id.btn_exit:
                exitNear();
                break;
        }
    }

    private void send84() {
        //发送84指令查看错误雷管信息
        String b = Utils.intToHex(0);
        String datalenght = Utils.addZero(b, 2);
        sendCmd(ThreeFiringCmd.sendWxjl84(deviceId, datalenght));
    }
    private void exitNear() {
        sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
        finish();
    }

    @Override
    protected void onDestroy() {
        close80Thread();
        close82Thread();
        sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
        super.onDestroy();
    }
}

