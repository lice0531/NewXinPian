package android_serialport_api.xingbang.firingdevice;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import android_serialport_api.xingbang.db.DenatorHis_Detail;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_DetailDao;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.upload.InitConst;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 新的无线级联近距离页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NewNearJlActivity extends SerialPortActivity {
    @BindView(R.id.tv_tip1)
   TextView tvTip;
    @BindView(R.id.tv_tip2)
    TextView tvTip2;
    private static int tipInfoFlag = 0;
    @BindView(R.id.btnExit)
    Button btnExit;
    @BindView(R.id.btnLookError)
    Button btnLookError;
    private Handler mHandler_1 = null;//总线稳定
    private static volatile int stage;
    private byte[] initBuf;//发送的命令
    private int stage_state = 0;
    private String TAG = "近距离无线级联页面";
    private ThreadFirst firstThread;
    private int sendNum = 1;
    private String deviceId = "01", dataLength81 = "", data81 = "", dataLength82 = "", data82 = "", serId = "";
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private Handler handler_msg = new Handler();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_nearjl);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        ButterKnife.bind(this);
        //获取区号
        initView();
        initHandler();
        Log.e(TAG,"---进入近距离无线级联页面---");
        initData();
    }

    private void initHandler() {
        //接受消息之后更新imageview视图
        mHandler_1 = new Handler(msg -> {
            execStage(msg);
            return false;
        });
    }

    private void initView() {
        btnExit.setOnClickListener(v -> {
            closeThread();
            finish();
        });
    }

    private void initData() {
        flag = (getIntent().getStringExtra("transhighRate") != null) ?
                getIntent().getStringExtra("transhighRate") : "";
        errorTotalNum = getIntent().getIntExtra("errorTotalNum",0);
        openHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initSerialPort(InitConst.TX_RATE);
                Log.e(TAG, "重新打开串口，波特率为" + InitConst.TX_RATE);
                isOpened = true;
                show_Toast("串口已打开");
                firstThread = new ThreadFirst();
                firstThread.start();
            }
        }, 2500);
        mListData = new GreenDaoMaster().queryDetonatorRegionAsc();

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
        Intent intent = new Intent(NewNearJlActivity.this, WxjlRemoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("wxjlDeviceId", deviceId);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    public void execStage(Message msg) {
        switch (stage) {
            case 0:
//                if (receive80) {
//                    tvTip.setText("1.设备注册中...");
//                } else {
//                    tvTip.setText("1.设备已注册");
//                }
//                if (receive81) {
//                    tvTip.setText("2.数据传输中...");
//                } else {
//                    tvTip.setText("2.数据传输已结束");
//                }
//                if (receive82) {
//                    tvTip.setText("3.数据检测中...");
//                } else {
//                    tvTip.setText("3.数据检测已结束");
//                }
                break;
        }
    }

    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Utils.writeLog("->:" + str);
                Log.e("发送命令", str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 全部
     */
    private class ThreadFirst extends Thread {
        public volatile boolean exit = false;

        public void run() {

            while (!exit) {
                try {
                    switch (stage) {
                        case 0:
                            //等待串口打开需要3秒
                            sendCmd(ThreeFiringCmd.sendWxjl80(deviceId));
                            Thread.sleep(1500);
                            Log.e(TAG, "case0--发送80");
                            if (receive80) {
                                Log.e(TAG, "case0--80已返回: ");
                                send81cmd();
                                Thread.sleep(1500);
                            }
                            if (receive81) {
                                Log.e(TAG, "case0--81已返回: ");
                                String b = Utils.intToHex(1);
                                dataLength82 = Utils.addZero(b, 2);
                                data82 = "01";
                                sendCmd(ThreeFiringCmd.sendWxjl82(deviceId, dataLength82, data82));
                                Log.e(TAG, "发送82进入检测模式指令");
                                Thread.sleep(1500);
                            }
                            if (receive82) {
                                Log.e(TAG, "case0--82已返回: ");
                                closeThread();
                                sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
                                closeSerial();
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                enterRemotePage();
                            }
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void closeThread() {
        mHandler_1.removeMessages(0);
        if (firstThread != null) {
            Log.e(TAG, "firstThread.exit: " + firstThread.exit);
            firstThread.exit = true;  // 终止线程thread
            firstThread.interrupt();
            try {
                firstThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        Log.e(TAG + "-返回命令", fromCommad);
        Utils.writeLog("<-:" + fromCommad);
        if (fromCommad.startsWith("C0") && fromCommad.endsWith("C0") && fromCommad.length() > 12) {
            String cmd = DefCommand.getCmd(fromCommad);
            if (cmd != null) {
                int localSize = fromCommad.length() / 2;
                byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                doWithReceivData(cmd,fromCommad);
            }
        } else {
            handler_msg.sendMessage(handler_msg.obtainMessage(4));
            Log.e(TAG,"返回命令不完整");
        }
    }

    /***
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, String completeCmd) {
        if (DefCommand.CMD_5_TRANSLATE_80.equals(cmd)) {//80 无线级联：进行设备注册
            //此时拿到设备号，然后在远距离级联页面时候使用
            Log.e(TAG, "收到80命令了");
            receive80 = true;
//            mHandler_1.sendMessage(mHandler_1.obtainMessage(0));
        } else if (DefCommand.CMD_5_TRANSLATE_81.equals(cmd)) {//81 无线级联：子节点与主节点进行数据传输
            Log.e(TAG, "收到81命令了");
//            EventBus.getDefault().post(new FirstEvent("is81End", "Y"));
            sendNum++;
            send81cmd();
        } else if (DefCommand.CMD_5_TRANSLATE_82.equals(cmd)) {//82 无线级联：进入检测模式
            Log.e(TAG, "收到82命令了");
//            EventBus.getDefault().post(new FirstEvent("is81End", "Y"));
            receive82 = true;
        } else if (DefCommand.CMD_5_TRANSLATE_84.equals(cmd)) {//84 无线级联：读取错误雷管
            /**
             * 芯片84指令一次最多给返回20条错误雷管数据，如超过20，则需要再次发送84指令获取剩下的错误雷管
             * 拿到84指令后，展示错误雷管数据  3发错误雷管：C00184 03 0300 0500 0D00 C6DDC0
             * 截取出错误雷管的序号，0300 0500 0D00就是发81指令时候的雷管顺序  得通过这个序号找到对应的雷管id给错误雷管更新状态
             * 同时在当前页面展示出错误雷管列表
             */
            currentCount ++;
            Log.e(TAG, "收到84命令了--完整的84指令:" + completeCmd + "--当前发送84次数：" + currentCount);
            String errorLgCmd = completeCmd.substring(8, completeCmd.length() - 6);
            Log.e(TAG,"错误雷管cmd是:" + errorLgCmd);
            //取到错误雷管cmd后  4个一组  每个都只取前两位  将其转为十进制就可以知道是错误芯片的81发送顺序  然后再找到对应的雷管id 再改变通信状态
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
                        updateLgStatus(mListData.get(j));
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
                message.obj = "true";
                handler_msg.sendMessage(message);
                return;
            }
            send84();
            Log.e(TAG,"错误雷管数量大于20，再次发84命令了");
        } else {
            Log.e(TAG, TAG + "-返回命令没有匹配对应的命令-cmd:" + cmd);
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
        if (!NewNearJlActivity.this.isFinishing()) {
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

    private void updateLgStatus(DenatorBaseinfo dbf) {
        //更新雷管信息的错误雷管状态为00
        DenatorBaseinfo denator = Application.getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(dbf.getShellBlastNo())).unique();
        denator.setErrorCode("00");
        denator.setErrorName(getString(R.string.text_communication_state1));
        Log.e(TAG,"更新雷管通信状态了。。。" + dbf.getShellBlastNo());
        Application.getDaoSession().update(denator);
        //更新历史记录表的错误雷管状态为00
        DenatorHis_Detail his_detail = Application.getDaoSession().getDenatorHis_DetailDao().queryBuilder().where(DenatorHis_DetailDao.Properties.ShellBlastNo.eq(dbf.getShellBlastNo())).unique();
        his_detail.setErrorCode("00");
        his_detail.setErrorName(getString(R.string.text_communication_state1));
        Log.e(TAG, "更新雷管通信状态为00了。。。" + his_detail.getShellBlastNo());
        Application.getDaoSession().update(his_detail);
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
            Log.e(TAG, "81已结束");
            receive81 = true;
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

    private void send84() {
        //发送84指令查看错误雷管信息
        String b = Utils.intToHex(0);
        String datalenght = Utils.addZero(b, 2);
        sendCmd(ThreeFiringCmd.sendWxjl84(deviceId, datalenght));
    }
    @Override
    protected void onDestroy() {
        closeThread();
        sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
        super.onDestroy();
    }
}
