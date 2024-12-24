package android_serialport_api.xingbang.jilian;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kfree.expd.ExpdDevMgr;
import com.kfree.expd.OnOpenSerialPortListener;
import com.kfree.expd.OnSerialPortDataListener;
import com.kfree.expd.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.firingdevice.FiringMainActivity;
import android_serialport_api.xingbang.firingdevice.TestDenatorActivity;
import android_serialport_api.xingbang.firingdevice.VerificationActivity;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 热点级联设置
 */
public class SyncActivityYouxian extends BaseActivity {

    @BindView(R.id.btn_test)
    Button btnTest;
    @BindView(R.id.btn_test1)
    Button btnTest1;
    @BindView(R.id.tv_delay)
    TextView tvDelay;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.a_et)
    AutoCompleteTextView aEt;
    @BindView(R.id.et_ip)
    EditText etIp;

    private Socket socket = null;
    private boolean socketStatus = false;
    private boolean status485 = false;

    private OutputStream outputStream = null;
    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    private boolean isTongBu = false;
    private int num = 0;

    /**
     * 接收服务器消息 变量
     */
    DataInputStream dis;
    // 输入流对象
    InputStream is;
    // 输入流读取器对象
    InputStreamReader isr;
    BufferedReader br;
    // 接收服务器发送过来的消息
    String response;

    /**
     * 界面
     */
    private static final int REQUEST_CODE_NET = 101;
    private static final int REQUEST_CODE_CHONGDIAN = 102;
    private static final int REQUEST_CODE_QIBAO = 103;

    private boolean isConnect;
    private boolean isExit = false;
    private int delay = 0;
    private String Yanzheng = "";//是否验证地理位置

    private boolean A002 = true;
    private boolean A003 = true;
    private boolean A004 = true;
    private boolean A5Other = true;//A5起爆指令是否是其他子设备接收起爆指令
    private boolean A5Main = true;//A5起爆指令是否是主的子设备接收起爆指令
    //主的子设备是否已经处理了A5起爆指令  如果已经处理 就算接受到其他子设备的起爆指令也不执行任何操作
    private boolean isHandleMainQb = false;
    private String TAG = "子机有线级联同步页面";
    private String qbxm_id = "-1";
    private String qbxm_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mThreadPool = Executors.newCachedThreadPool();

        mBuffer = new byte[50];
        delay = (Integer) MmkvUtils.getcode("delay", 0);
        tvDelay.setText(getString(R.string.text_sync_dqyc) + delay);

        tvCode.setText(MmkvUtils.getcode("ACode", "").equals("") ? getString(R.string.text_sync_szbh) : (String) MmkvUtils.getcode("ACode", ""));
        getPropertiesData();


        switch (Build.DEVICE) {
            case "KT50":
            case "KT50_B2": {
                break;
            }
            case "T-QBZD-Z6":
            case "M900": {
                mExpDevMgr = new ExpdDevMgr(this);
                break;
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        delay = (Integer) MmkvUtils.getcode("delay", 0);
        tvDelay.setText(getString(R.string.text_sync_dqyc) + delay);
        tvCode.setText(MmkvUtils.getcode("ACode", "").equals("") ? getString(R.string.text_sync_szbh) : (String) MmkvUtils.getcode("ACode", ""));
    }

    private void getPropertiesData() {
        Yanzheng = (String) MmkvUtils.getcode("Yanzheng", "验证");
        Log.e("级联", "Yanzheng: " + Yanzheng);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            qbxm_id = !TextUtils.isEmpty((String)bundle.get("qbxm_id")) ?
                    (String)bundle.get("qbxm_id") : "";
            qbxm_name = !TextUtils.isEmpty((String) bundle.get("qbxm_name")) ?
                    (String) bundle.get("qbxm_name") : "";
        } else {
            qbxm_id = "-1";
            qbxm_name = "";
        }
    }


    /**
     * 1，子机先拉低，发送数据同步
     */
    String mResponse = "";

    //    @Override
//    protected void onDataReceived(byte[] buffer, int size) {
//        String ar = new String(buffer).trim();
//        Log.e("消息返回", "ar: "+ar );
//        mResponse = ar;
//        Message msg = Message.obtain();
//        msg.what = 0;
//        msg.obj = ar;
//        handler.sendMessage(msg);
//    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    String response = (String) msg.obj;
                    if (response == null) {
                        show_Toast(getString(R.string.text_sync_tip1));
                        break;
                    }
//                    if (response.contains("A001" + MmkvUtils.getcode("ACode", ""))) {
                    if (response.startsWith("A1" + MmkvUtils.getcode("ACode", ""))) {
                        //同步成功
                        //收到服务器的同步确认指令
                        isTongBu = true;
                        handler.sendEmptyMessageDelayed(5, 1000);
                        show_Toast(getString(R.string.text_sync_tip2));
                        btnTest.setEnabled(false);
                        btnTest.setText(R.string.text_sync_tip3);
//                    } else if (response.contains("A002")) {
                    } else if (response.startsWith("A2")) {
                        if (A002) {
                            show_Toast(getString(R.string.text_sync_tip4));
                            String str5 = "级联起爆";
//                            if (Yanzheng.equals("验证")) {
//                                //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
//                                Intent intent5 = new Intent(SyncActivityYouxian.this, VerificationActivity.class);//验证爆破范围页面
//                                intent5.putExtra("dataSend", str5);
//                                startActivityForResult(intent5, REQUEST_CODE_QIBAO);
//                            } else {
                                Intent intent5 = new Intent(SyncActivityYouxian.this, FiringMainActivity.class);//金建华
                                Bundle bundle = new Bundle();
                                bundle.putString("qbxm_id", qbxm_id);
                                bundle.putString("qbxm_name", qbxm_name);
                                intent5.putExtras(bundle);
                                intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent5.putExtra("dataSend", str5);
                                intent5.putExtra("isJl","Y");
                                startActivityForResult(intent5, REQUEST_CODE_QIBAO);
                                Log.e(TAG,"qbxm_id:" + qbxm_id + "--qbxm_name:" + qbxm_name);
//                            }
                            A002 = false;
                        } else {
                            Log.e(TAG,"A2已经在起爆页面了");
//                            show_Toast("正在起爆流程中");
                        }
                    } else if (response.startsWith("ABA2")) {
                        Log.e(TAG,"收到重新检测指令了" + response + "--" + response.substring(response.length() - 2));
                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(response.length() - 2))) {
                            if (A002) {
                                show_Toast(getString(R.string.text_sync_tip4));
                                String str5 = "级联起爆";
//                                if (Yanzheng.equals("验证")) {
//                                    //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
//                                    Intent intent5 = new Intent(SyncActivityYouxian.this, VerificationActivity.class);//验证爆破范围页面
//                                    intent5.putExtra("dataSend", str5);
//                                    startActivityForResult(intent5, REQUEST_CODE_QIBAO);
//                                } else {
                                    Intent intent5 = new Intent(SyncActivityYouxian.this, FiringMainActivity.class);//金建华
                                    Bundle bundle = new Bundle();
                                    bundle.putString("qbxm_id", qbxm_id);
                                    bundle.putString("qbxm_name", qbxm_name);
                                    intent5.putExtras(bundle);
                                    intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent5.putExtra("dataSend", str5);
                                    intent5.putExtra("isJl","Y");
                                    intent5.putExtra("isResJc","Y");
                                    startActivityForResult(intent5, REQUEST_CODE_QIBAO);
                                Log.e(TAG,"qbxm_id:" + qbxm_id + "--qbxm_name:" + qbxm_name);
//                                }
                                A002 = false;
                            } else {
                                Log.e(TAG,"ABA2已经在起爆页面了");
//                            show_Toast("正在起爆流程中");
                                send485Cmd("EEA2" + MmkvUtils.getcode("ACode", ""));
                            }
                        } else {
                            Log.e(TAG,response + "--重新检测指令不是当前子设备的");
                        }
                    } else if (response.startsWith("A3")) {
                        //收到主控轮询的命令
                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(response.length() - 2))) {
//                            Log.e(TAG,MmkvUtils.getcode("ACode", "") + "设备收到" + response);
                            EventBus.getDefault().post(new FirstEvent("pollMsg"));
                        }
//                    } else if (response.contains("A003")) {
                        //收到主控的充电指令
                    } else if (response.startsWith("A4")) {
//                        if (getStringAfterA4(response).equals(MmkvUtils.getcode("ACode", ""))) {
//                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(2))) {
//                        show_Toast(getString(R.string.text_sync_tip5));
                        EventBus.getDefault().post(new FirstEvent("jixu"));
//                            send485Cmd("B4" + MmkvUtils.getcode("ACode", ""));
//                        }
//                        Intent intent = new Intent(SyncActivity.this, FiringMainActivity.class);
//                        startActivityForResult(intent, REQUEST_CODE_CHONGDIAN);
//                    } else if (response.contains("A004")) {
                    } else if (response.startsWith("ABA4")) {
                        Log.e(TAG,"收到重新充电指令了" + response + "--" + response.substring(response.length() - 2));
                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(response.length() - 2))) {
//                            send485Cmd("BBA4" + MmkvUtils.getcode("ACode", ""));
//                            show_Toast(getString(R.string.text_sync_tip5));
                            EventBus.getDefault().post(new FirstEvent("rejixu"));
                        } else {
                            Log.e(TAG,response + "--重新充电指令不是当前子设备的");
                        }
//                    } else if (response.contains("A004")) {
                    } else if (response.startsWith("A5")) {
                        Log.e("接收到A5指令了",response);
                        //收到主控切换模式的命令  此时通知板子进入起爆模式
                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(2, 4))) {
                            Log.e(TAG, "一进来的mainA5Str:" + mainA5Str);
                            if (mainA5Str.equals(response)) {
                                //多次重复收到A5消息时，只处理一次
                                Log.e(TAG, "主的子设备多次接收到A5消息，不处理");
                                break;
                            }
//                            if (A5Main) {
                            mainA5Str = response;
                            Log.e(TAG, "处理前的mainA5Str:" + mainA5Str);
                            isHandleMainQb = true;
                            A5Main = false;
                            //主的子设备
//                            show_Toast(getString(R.string.text_sync_tip6));
                            Log.e("主的子设备已接收到切换模式指令", response);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            send485Cmd("B5" + MmkvUtils.getcode("ACode", ""));
                            EventBus.getDefault().post(new FirstEvent("sendCmd83"));
                            //此时在起爆页面展示一个文字提示，内容为：时钟校验中，等待起爆，请稍等
                            EventBus.getDefault().post(new FirstEvent("sendWaitQb"));
//                                Log.e(TAG,"处理结束的mainA5Str:" + mainA5Str);
//                            } else {
//                                Log.e(TAG,"重复接收到A5起爆主的子设备消息，不处理");
//                            }
                        } else {
                            Log.e(TAG, "一进来的otherA5Str:" + otherA5Str);
                            if (otherA5Str.equals(response)) {
                                //多次重复收到A5消息时，只处理一次
                                Log.e(TAG, "其他子设备多次接收到A5消息，不处理");
                                break;
                            }
//                            if (!isHandleMainQb) {
//                                if (A5Other) {
                            otherA5Str = response;
                            Log.e(TAG, "处理前的otherA5Str:" + otherA5Str);
                            A5Other = false;
//                            show_Toast(getString(R.string.text_sync_tip6));
//                                    EventBus.getDefault().post(new FirstEvent("otherA5"));
                            //其他子设备
                            Log.e(TAG, "其他子设备接收到A5指令了");
                            closeM900Rs485((String) MmkvUtils.getcode("ACode", ""));
                            Utils.writeLog("其他子设备：" + MmkvUtils.getcode("ACode", "") + "开始关闭485指令");
                            AppLogUtils.writeAppLog("其他子设备：" + MmkvUtils.getcode("ACode", "") + "开始关闭485指令");
                            //此时在起爆页面展示一个文字提示，内容为：时钟校验中，等待起爆，请稍等
                            EventBus.getDefault().post(new FirstEvent("sendWaitQb"));
//                                } else {
//                                    Log.e(TAG,"重复接收到A5起爆其他设备消息，不处理");
////                                }
//                            } else {
//                                Log.e(TAG,"主的子设备已经处理了起爆指令，接收到了其他子设备起爆指令也不用执行");
//                            }
                        }

//                    } else if (response.contains("A003")) {
                        //收到主控的起爆指令
                    } else if (response.startsWith("A6")) {
//                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(2))) {
//                        show_Toast(getString(R.string.text_sync_tip6));
                        EventBus.getDefault().post(new FirstEvent("qibao"));
                        send485Cmd("B6" + MmkvUtils.getcode("ACode", ""));
//                        }
//                        Intent intent = new Intent(SyncActivity.this, FiringMainActivity.class);
//                        if (response.length() >= 5) {
//                            intent.putExtra("itemId", response.substring(4));
//                        } else {
//                            intent.putExtra("itemId", "");
//                        }
//                        startActivityForResult(intent, REQUEST_CODE_QIBAO);
                        //收到主控的退出指令
//                    } else if (response.contains("A005")) {
                    } else if (response.startsWith("A7")) {
                        A002 = true;
                        A5Other = true;
                        A5Main = true;
                        isHandleMainQb = false;
                        EventBus.getDefault().post(new FirstEvent("finish"));
                        send485Cmd("B7" + MmkvUtils.getcode("ACode", ""));
//                        show_Toast("收到退出指令");
                        finish();
                    } else if (response.startsWith("A8")) {
                        A002 = true;
                        A5Other = true;
                        A5Main = true;
                        isHandleMainQb = false;
                        EventBus.getDefault().post(new FirstEvent("exitPage"));
                        //收到主控退到有线级联页面指令
                    } else if (response.startsWith("A012")) {
                        //说明子设备出现异常情况，有继续和退出按钮，主控来操控子设备是否继续
                        Log.e(TAG,"收到主控A012消息了" + response);
                        if (MmkvUtils.getcode("ACode", "").equals(response.substring(4,6))) {
                            EventBus.getDefault().post(new FirstEvent("handleJx",response.substring(response.length() - 2)));
                        }
                    } else if (response.contains("A006")) {
//                        EventBus.getDefault().post(new FirstEvent("qibaoTag"));
                    } else if (response.contains("A008")) {
//                        toCheck6();
                    }

                    break;
                case 1:
//                    show_Toast("同步指令错误");
                    break;
                case 2:
                    show_Toast(getString(R.string.text_sync_tip7));
                    break;
                case 3:
                    //心跳数据
                    mNumber = 0;
                    break;
                case 5:
                    /*if (mNumber != 0) {
                        finish();
                    }
                    mNumber++;
                    handler.sendEmptyMessageDelayed(5, 3600);*/
                    break;
                case 6:
                    btnTest.setText(R.string.text_sync_tip8);
                    btnTest.setEnabled(false);
//                    final String data2 = "0001" + MmkvUtils.getcode("ACode", "") + "\n";
                    final String data2 = "B1" + MmkvUtils.getcode("ACode", "") + "\n";
                    writeData(data2);
                    break;
                case 10:
//                    btnConnect.setText("已连接");
//                    btnTest.setEnabled(true);
//                    btnTest1.setEnabled(true);
                    break;
                case 16:
//                    EMgpio.SetGpioDataLow(94);
                    try {
                        Thread.sleep(12);

//                        mOutputStream.write(("0001" + MmkvUtils.getcode("ACode","")).getBytes());
//                        Thread.sleep(12);
//                        EMgpio.SetGpioDataHigh(94);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessageDelayed(16, 1000);
                    break;
                case 20:
                    if (!isTongBu) {
                        num++;
//                        EMgpio.SetGpioDataLow(94);
                        try {
                            Thread.sleep(12);

                            String a = myInfo();
//                            mOutputStream.write(("0001" + MmkvUtils.getcode("ACode","") + ",_*").getBytes());
//                            mOutputStream.write((a + ",_*").getBytes());
                            show_Toast(getString(R.string.text_sync_tip9));
                            Thread.sleep(20);
//                            EMgpio.SetGpioDataHigh(94);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (num < 2) {
                            handler.sendEmptyMessageDelayed(20, 1000);
                        }
                    } else {
                        num = 0;
                    }

                    break;
                case 666:
                    IntentBean item = (IntentBean) msg.obj;
                    int resultCode = item.getResultCode();
                    Intent data = item.getData();
                    switch (item.getRequestCode()) {
                        case REQUEST_CODE_NET:
                            if (resultCode == TestDenatorActivity.RESULT_SUCCESS) {
                                //网络测试回调
                                String code = (String) MmkvUtils.getcode("ACode", "");
                                String type = data.getStringExtra("type");
                                String tNum = data.getStringExtra("tNum");
                                String faultNum = data.getStringExtra("faultNum");
                                String tU = data.getStringExtra("tU");
                                String tI = data.getStringExtra("tI");
                                String tip = data.getStringExtra("tip");

//                    show_Toast("正在回传数据");
                                String a = "0002" + code + "," + type + "," + tNum + "," + faultNum + "," + tU + "," + tI + "," + tip;
                                show_Toast(a);
                                writeData(a);
                            }
                            break;
                        case REQUEST_CODE_CHONGDIAN:
//                            if (resultCode == TempChongdianActivity.RESULT_SUCCESS) {
//                                //充电
//                                String code = MmkvUtils.getcode("ACode","");
//                                String type = data.getStringExtra("type");
////                    String tip = data.getStringExtra("tip");
//                                String U = data.getStringExtra("U");
//                                String I = data.getStringExtra("I");
//                                show_Toast("正在回传数据");
//                                String a = "0003" + code + "," + type + "," + U + "," + I;
//                                writeData(a);
//                            }
                            break;
                        case REQUEST_CODE_QIBAO:
                            if (resultCode == FiringMainActivity.RESULT_SUCCESS) {
                                //起爆
                                String code = (String) MmkvUtils.getcode("ACode", "");
                                String type = data.getStringExtra("type");
                                String tip = data.getStringExtra("tip");

                                show_Toast(getString(R.string.text_sync_tip10));
                                String a = "0004" + code + "," + type + "," + tip;
                                writeData(a);
                            }
                            break;
                    }
                    break;
            }

            return false;
        }
    });

    private String mainA5Str = "";//用来记录是否重复接收到主的子设备A5指令
    private String otherA5Str = "";//用来记录是否重复接收到其他子设备A5指令
    //因为充电指令收到的消息中有A4数据粘连的情况  所以采取截取string方式处理接收到的485指令
    public  String getStringAfterA4(String str) {
        // 找到 "A4" 在 str 中的位置
        int index = str.indexOf("A4");
        if (index != -1 && index + 2 < str.length()) {
            // 截取两个字符，从 "A4" 的末尾开始
            return str.substring(index + 2, index + 4);
        }
        // 如果未找到 "A4"，或者 "A4" 后面不足两个字符，则返回空字符串
        return "";
    }
    private void toCheck6() {
        String code = (String) MmkvUtils.getcode("ACode", "");
        String check = MmkvUtils.getcode("delay", 0) + "";
        String info = "";
        if (check.equals("1")) {

        } else {
//            List<NXmDataBean> mList = db.getNXM2();
//            for (int i = 0; i < mList.size(); i++) {
//                NXmDataBean item = mList.get(i);
//                if (i == 0) {
//                    info = item.getId() + ";" + item.getName();
//                } else {
//                    info = info + "," + item.getId() + ";" + item.getName();
//                }
//            }
        }
        show_Toast("正在回传数据");
        String a;
        if (check.equals("1")) {
            a = "0008" + code + ",0" + check;
        } else {
            a = "0008" + code + ",0" + check + "," + info;
        }
        writeData(a);
    }

    private int mNumber = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentBean item = new IntentBean();
        item.setRequestCode(requestCode);
        item.setResultCode(resultCode);
        item.setData(data);
        Message msg = new Message();
        msg.obj = item;
        msg.what = 666;
        handler.sendMessageDelayed(msg, delay * 1000);
        /*if (delay > 0) {
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        /*switch (requestCode) {
            case REQUEST_CODE_NET:
                if (resultCode == TempNetActivity.RESULT_SUCCESS) {
                    //网络测试回调
                    String code = (String) MmkvUtils.getcode("ACode","");
                    String type = data.getStringExtra("type");
                    String tNum = data.getStringExtra("tNum");
                    String faultNum = data.getStringExtra("faultNum");
                    String tU = data.getStringExtra("tU");
                    String tI = data.getStringExtra("tI");
                    String tip = data.getStringExtra("tip");

//                    show_Toast("正在回传数据");
                    String a = "0002" + code + "," + type + "," + tNum + "," + faultNum + "," + tU + "," + tI + "," + tip;
                    show_Toast(a);
                    writeData(a);
                }
                break;
            case REQUEST_CODE_CHONGDIAN:
                if (resultCode == TempChongdianActivity.RESULT_SUCCESS) {
                    //充电
                    String code = (String) MmkvUtils.getcode("ACode","");
                    String type = data.getStringExtra("type");
//                    String tip = data.getStringExtra("tip");
                    String U = data.getStringExtra("U");
                    String I = data.getStringExtra("I");

                    show_Toast("正在回传数据");
                    String a = "0003" + code + "," + type + "," + U + "," + I;
                    writeData(a);
                }
                break;
            case REQUEST_CODE_QIBAO:
                if (resultCode == TempQiBaoActivity.RESULT_SUCCESS) {
                    //起爆
                    String code = (String) MmkvUtils.getcode("ACode","");
                    String type = data.getStringExtra("type");
                    String tip = data.getStringExtra("tip");

                    show_Toast("正在回传数据");
                    String a = "0004" + code + "," + type + "," + tip;
                    writeData(a);
                }
                break;
        }*/
    }

    private void connect() {
// 初始化线程池
        mThreadPool.execute(() -> {
            if (!socketStatus) {
                try {
//                        socket = new Socket(aEt.getText().toString().trim(), 9002);
                    socket = new Socket(getWifiRouteIPAddress(), 9002);
//                        socket = new Socket("192.168.43.109", 9002);
                    Log.e("socket输出", socket.isConnected() + " " + getWifiRouteIPAddress());
                    if (socket == null) {
                    } else {
                        socketStatus = true;
                    }
                    if (socket.isConnected()) {
                        handler.sendEmptyMessage(10);
//                            show_Toast("连接成功!");
                        // 步骤1：创建输入流对象InputStream
                        is = socket.getInputStream();
                        // 步骤2：创建输入流读取器对象 并传入输入流对象
                        // 该对象作用：获取服务器返回的数据
                        isr = new InputStreamReader(is);
                        br = new BufferedReader(isr);
                        if (isConnect) {
                            handler.sendEmptyMessage(6);
                            isConnect = false;
                        }

                        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                        while (!isExit && (response = br.readLine()) != null) {
                            // 步骤4:通知主线程,将接收的消息显示到界面
//                                log.e(response + "--");
//                            if (response.startsWith("A0")) {
                            Log.e("485response",response);
                            if (response.startsWith("A")) {
                                Message msg = Message.obtain();
                                msg.what = 0;
                                msg.obj = response;
                                handler.sendMessage(msg);
                            } else if (response.startsWith("FF")) {
                                //心跳数据
                                handler.sendEmptyMessage(3);
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private String getWifiRouteIPAddress() {
        WifiManager wifi_service = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        //        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        //        System.out.println("Wifi info----->" + wifiinfo.getIpAddress());
        //        System.out.println("DHCP info gateway----->" + Formatter.formatIpAddress(dhcpInfo.gateway));
        //        System.out.println("DHCP info netmask----->" + Formatter.formatIpAddress(dhcpInfo.netmask));
        //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
        String routeIp = Formatter.formatIpAddress(dhcpInfo.gateway);
        Log.e("网络route ip", "wifi route ip：" + routeIp);

        return routeIp;
    }

    /**
     * 头部00，服务器头部A0
     * 01发出同步指令同事携带编码号，服务器收到01返回01则确认同步成功
     * 如果没有收到指令，那么则需要延迟5秒再次发送指令
     * 1.通信协议请求受控指令等待主APP发出指令，收到指令则连接成功
     * 2.主APP发出网络检测的指令，分机APP进行网络监测的功能，检测完成发出指令结果，收到主APP应答
     * 3.充电指令
     * 4.起爆指令，时间同步
     */
    @OnClick({R.id.btn_test, R.id.btn_test1, R.id.btn_tongbu_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_test:
                if (MmkvUtils.getcode("ACode", "").equals("")) {
                    show_Toast(getString(R.string.text_sync_szbh));
                    return;
                }
                btnTest.setText(getString(R.string.text_sync_tip8));
                btnTest.setEnabled(false);

//                handler.sendEmptyMessageDelayed(16,1000);

//                EMgpio.SetGpioDataLow(94);//gpio下电操作
//                try {
//                    Thread.sleep(12);
//
//                    String a = myInfo();
//                    Log.e("级联", "a: "+a );
////                    mOutputStream.write(("0001" + MmkvUtils.getcode("ACode","") + ",_*").getBytes());
//                    mOutputStream.write((a + ",_*").getBytes());
//                    Log.e("级联", "(a + ,_*): "+(a + ",_*"));
//                    show_Toast("发送了");
//                    Thread.sleep(20);
////                    EMgpio.SetGpioDataHigh(94);////gpio下电操作
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                handler.sendEmptyMessageDelayed(20, 1000);

                switch (Build.DEVICE) {
                    case "KT50":
                    case "KT50_B2": {
                        if (socket == null) {
                            isConnect = true;
//                    show_Toast("请重新打开APP，确保服务端已经在运行");
                            btnTest.setText(getString(R.string.text_sync_tip8));
                            btnTest.setEnabled(false);
                            connect();
                        } else {
                            btnTest.setText(getString(R.string.text_sync_tip8));
                            btnTest.setEnabled(false);
                            Log.e("同步", "ACode: " + MmkvUtils.getcode("ACode", ""));
//                            final String data = "0001" + MmkvUtils.getcode("ACode", "") + "\n";
                            final String data = "B1" + MmkvUtils.getcode("ACode", "") + "\n";
                            writeData(data);
                        }
                        break;
                    }
                    case "T-QBZD-Z6":
                    case "M900": {

                        //串口打开监听
                        OnOpenSerialPortListener listener = new OnOpenSerialPortListener() {
                            @Override
                            public void onSuccess(File file) {
                                Log.e("485接口-串口状态监听", "打开成功-file: " + file.toString());
                            }

                            @Override
                            public void onFail(File file, Status status) {
                                Log.e("485接口-串口状态监听", "打开失败-status: " + status);
                            }
                        };
                        //串口数据监听
                        OnSerialPortDataListener listener2 = new OnSerialPortDataListener() {
                            @Override
                            public void onDataReceived(byte[] bytes) {
                                String fromCommad = Utils.bytesToHexFun(bytes);//将数组转化为16进制字符串
                                Log.e("485接口-接收数据", "onDataReceived: " + fromCommad);
//                                if (fromCommad.startsWith("A0")) {
                                if (fromCommad.startsWith("A")) {
//                                    if(fromCommad.equals("A6")) {
//                                        EventBus.getDefault().post(new FirstEvent("qibao"));
//                                    } else {
                                        Message msg = Message.obtain();
                                        msg.what = 0;
                                        msg.obj = fromCommad;
                                        handler.sendMessage(msg);
//                                    }
                                } else if (fromCommad.startsWith("FF")) {
                                    //心跳数据
                                    handler.sendEmptyMessage(3);
                                }
                            }

                            @Override
                            public void onDataSent(byte[] bytes) {
                                String fromCommad = Utils.bytesToHexFun(bytes);//将数组转化为16进制字符串
                                Log.e("485接口-发送数据", "onDataSent: " + fromCommad);
                            }
                        };
                        mExpDevMgr.set12VEnable(true);
                        mExpDevMgr.openRs485(listener, listener2, 115200);
//                        final String data = "0001" + MmkvUtils.getcode("ACode", "") + "\n";
                        final String data = "B1" + MmkvUtils.getcode("ACode", "") + "\n";
                        send485Cmd(data);
                        break;
                    }

                }


                break;
            case R.id.btn_test1:
                show_Toast(getString(R.string.text_sync_tip11));
                isTongBu = false;
                switch (Build.DEVICE) {
                    case "T-QBZD-Z6":
                    case "M900":
                        send485Cmd("0005" + MmkvUtils.getcode("ACode", ""));
                        Utils.writeLog(MmkvUtils.getcode("ACode", "") + "子设备发起退出级联0005指令");
                        AppLogUtils.writeAppXBLog(MmkvUtils.getcode("ACode", "") + "子设备发起退出级联0005指令");
                        break;
                    default:
                        closeSocket();
                        break;
                }

                finish();
//                String a = "0002" + "D0000005" + "," + "1" + "," + "100" + "," + "0" + "," + "200" + "," + "300" + "," + "";
////                writeData(a);
                break;
            case R.id.btn_tongbu_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }


    /**
     * 发送485命令
     */
    public void send485Cmd(String data) {
//        int delay = Integer.parseInt((String) MmkvUtils.getcode("ACode", ""));
//        try {
//            Thread.sleep(delay * 2L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        byte[] powerCmd = Utils.hexStringToBytes(data);
        mExpDevMgr.sendBytesRs485(powerCmd);
//        String str = Utils.bytesToHexFun(powerCmd);
//        Log.e("子机485发送-data", data);
//        Log.e("485发送-str", str);
    }

    private String myInfo() {
        String code = (String) MmkvUtils.getcode("ACode", "");
        String check = MmkvUtils.getcode("check", 1) + "";
        String info = "";
        if (check.equals("1")) {

        } else {
//            List<NXmDataBean> mList = db.getNXM2();
//            for (int i = 0; i < mList.size(); i++) {
//                NXmDataBean item = mList.get(i);
//                if (i == 0) {
//                    info = item.getId() + ";" + item.getName();
//                } else {
//                    info = info + "," + item.getId() + ";" + item.getName();
//                }
//            }
        }

        show_Toast(getString(R.string.text_sync_tip12));
        String a;
        if (check.equals("1")) {
            a = "0001" + code + ",0" + check;
        } else {
            a = "0001" + code + ",0" + check + "," + info;
        }

        return a;
    }

    byte[] mBuffer;

    private void closeSocket() {
        writeData("0005");//退出
        int len = (Protcol.QuitFunc(mBuffer)) & 0xff;
        SendData(mBuffer, len);
        isExit = true;
        handler.removeMessages(3);
        handler.removeMessages(5);
        if (mThreadPool != null) {
            mThreadPool.shutdown();
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }

    private void SendData(byte[] buffer, int size) {
        /*int sendlg = 0;
        byte[] tmpBuffer = new byte[50];
        byte[] sendbuf = new byte[50];

        for (int i = 0; i < size - 4; i++) {
            tmpBuffer[i] = buffer[i + 1];
        }
        sendlg = (MyFunc.exslip_pack(tmpBuffer, size - 4, sendbuf)) & 0xff;

        try {
            mOutputStream.write(sendbuf, 0, sendlg);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    //串口发送消息
//    private void writeData(String data) {
////        data = data + ",_*";
////        EMgpio.SetGpioDataLow(94);
//        try {
////            Thread.sleep(60);
//            Thread.sleep(80);
//            mOutputStream.write(data.getBytes());
//            Thread.sleep(200);
////            EMgpio.SetGpioDataHigh(94);
//            show_Toast("发送数据了..." + data);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * socket发送消息
     */
    private void writeData(final String data) {
        mThreadPool.execute(() -> {
            if (socketStatus) {
                try {
                    outputStream = socket.getOutputStream();
                    outputStream.write(data.getBytes());
                    // 步骤3：发送数据到服务端
                    outputStream.flush();
                    Log.e("级联", "发送数据: " + data);
                    handler.sendEmptyMessage(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendDelay() {
        int delay = Integer.parseInt((String) MmkvUtils.getcode("ACode", ""));
        try {
            Thread.sleep(delay * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
        String msg = event.getMsg();
//        Log.e("同步页面收到起爆页面485消息了",msg);
        if (msg.equals("qibao")) {
            String a = "0006";
            writeData(a);
        } else if (msg.equals("B2")) {
            //说明子机已进入起爆页面  此时需给主控发消息告知
            sendDelay();
            send485Cmd("B2" + MmkvUtils.getcode("ACode", ""));
        } else if (msg.equals("B8")) {
            //说明子机出现了不同异常情况的弹窗  此时通知主控
            sendDelay();
            send485Cmd("B8" + MmkvUtils.getcode("ACode", "") + event.getData() + event.getTureNum() + event.getErrNum() + event.getCurrentPeak());
        } else if (msg.equals("B9")) {
            //说明子机出现了限制起爆情况的弹窗  此时通知主控
            sendDelay();
            send485Cmd("B9" + MmkvUtils.getcode("ACode", "") + event.getData() + event.getTureNum() + event.getErrNum() + event.getCurrentPeak());
        }  else if (msg.equals("clycjg")) {
            Log.e(TAG + "返回clycjg测试结果", "tureNum: " + event.getTureNum() + "--errNum: " + event.getErrNum() + "--currentPeak: " + event.getCurrentPeak());
            sendDelay();
            send485Cmd("B012" + MmkvUtils.getcode("ACode", "") + event.getData() + event.getTureNum() + event.getErrNum() + event.getCurrentPeak());
        } else if (msg.equals("BBA2")) {
            //说明子机已进入起爆页面  此时需给主控发消息告知
            sendDelay();
            send485Cmd("BBA2" + MmkvUtils.getcode("ACode", ""));
        } else if (msg.equals("ssjc")) {
            //说明子机已接到轮询指令  此时需给主控发消息告知
            String pollData = "B3" + MmkvUtils.getcode("ACode", "") + event.getData() + event.getTureNum()
                    + event.getErrNum() + event.getCurrentPeak();
            if (pollData.startsWith("B3") && pollData.length() == 18) {
                send485Cmd(pollData);
            }
        } else if (msg.equals("open485")) {
            switch (Build.DEVICE) {
                case "T-QBZD-Z6":
                case "M900":
                    AppLogUtils.writeAppXBLog(MmkvUtils.getcode("ACode", "") + "子设备已收到重新打开485串口指令");
                    Utils.writeLog(MmkvUtils.getcode("ACode", "") + "子设备已收到重新打开485串口指令");
                    openM900Rs485(event.getData());
                    mainA5Str = "";
                    otherA5Str = "";
                    break;
                default:
                    Log.e("执行关闭socket操作","。。。。。");
                    closeSocket();
                    break;
            }

        } else if (msg.equals("close485")) {
            //此时关闭485接收  让板子子机去执行起爆命令
            AppLogUtils.writeAppXBLog("主的子设备：" + MmkvUtils.getcode("ACode", "") + "开始关闭485指令");
            Utils.writeLog("主的子设备：" + MmkvUtils.getcode("ACode", "") + "开始关闭485指令");
            closeM900Rs485("B5" + MmkvUtils.getcode("ACode", ""));
        } else if (msg.equals("sendA4Data")) {
            sendDelay();
            if (event.getData().startsWith("B4") && event.getData().length() == 18) {
                Log.e(TAG + "收到充电指令后发送的数据正常",event.getData());
                send485Cmd(event.getData());
            } else {
                Log.e(TAG + "收到充电指令后发送的数据有误",event.getData());
            }
        } else if (msg.equals("reSendA4Data")) {
            sendDelay();
            if (event.getData().startsWith("BBA4") && event.getData().length() == 20) {
                Log.e(TAG + "收到充电指令后发送的数据正常",event.getData());
                send485Cmd(event.getData());
            } else {
                Log.e(TAG + "收到充电指令后发送的数据有误",event.getData());
            }
//        } else if (msg.equals("otherClose")) {
//            Utils.writeRecord("其他子设备：" + MmkvUtils.getcode("ACode", "") + "开始关闭485指令");
//            Utils.writeLog("其他子设备：" + MmkvUtils.getcode("ACode", "") + "开始关闭485指令");
//            Log.e("其他子设备已接收到切换模式指令","现在开始关闭485" + MmkvUtils.getcode("ACode", ""));
//            closeM900Rs485((String)MmkvUtils.getcode("ACode", ""));
        } else if (msg.equals("qbjs")) {
            String qbResult = event.getData();
            AppLogUtils.writeAppXBLog("其他子设备:" + MmkvUtils.getcode("ACode", "") + "已起爆结束,现在将起爆结果通知主控");
            Utils.writeLog("其他子设备:" + MmkvUtils.getcode("ACode", "") + "已起爆结束,现在将起爆结果通知主控:" + event.getData());
            Log.e("其他子设备",MmkvUtils.getcode("ACode", "") + "已起爆结束,现在将起爆结果通知主控:" + event.getData());
            sendDelay();
            if (qbResult.startsWith("B005") && qbResult.length() == 20) {
                Log.e(TAG + "起爆结束后发送的数据正常",qbResult);
                send485Cmd(qbResult);
            } else {
                Log.e(TAG + "起爆结束后发送的数据有误",qbResult);
            }
        }
    }

    private void openM900Rs485(String data){
        Log.e(TAG,"重新打开485--data:" + data);
        mExpDevMgr = new ExpdDevMgr(this);
        //串口打开监听
        OnOpenSerialPortListener listener = new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File file) {
                Log.e("485接口-串口状态监听", "打开成功-file: " + file.toString());
            }

            @Override
            public void onFail(File file, Status status) {
                Log.e("485接口-串口状态监听", "打开失败-status: " + status);
            }
        };
        //串口数据监听
        OnSerialPortDataListener listener2 = new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                String fromCommad = Utils.bytesToHexFun(bytes);//将数组转化为16进制字符串
                Log.e("重新打开485接口-接收数据", "onDataReceived: " + fromCommad);
//                                if (fromCommad.startsWith("A0")) {
                if (fromCommad.startsWith("A")) {
//                    if(fromCommad.equals("A6")) {
//                        EventBus.getDefault().post(new FirstEvent("qibao"));
//                    } else {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = fromCommad;
                        handler.sendMessage(msg);
//                    }
                } else if (fromCommad.startsWith("FF")) {
                    //心跳数据
                    handler.sendEmptyMessage(3);
                }
            }

            @Override
            public void onDataSent(byte[] bytes) {
                String fromCommad = Utils.bytesToHexFun(bytes);//将数组转化为16进制字符串
                Log.e("485接口-发送数据", "onDataSent: " + fromCommad);
            }
        };
        mExpDevMgr.set12VEnable(true);
        mExpDevMgr.openRs485(listener, listener2, 115200);
//        sendDelay();
//        if (qbResult.startsWith("B005") && qbResult.length() == 20) {
//            Log.e(TAG + "起爆结束后发送的数据正常",qbResult);
//            send485Cmd(qbResult);
//        } else {
//            Log.e(TAG + "起爆结束后发送的数据有误",qbResult);
//        }
    }

    private void closeM900Rs485(String code) {
        switch (Build.DEVICE) {
            case "T-QBZD-Z6":
            case "M900":
                if (mExpDevMgr != null) {
                    mExpDevMgr.closeRs485();
                    mExpDevMgr.set12VEnable(false);
                    Log.e(TAG + "关闭485，设备是",code);
                    AppLogUtils.writeAppXBLog("子设备：" + MmkvUtils.getcode("ACode", "") + "已关闭485指令");
                    Utils.writeLog("子设备：" + MmkvUtils.getcode("ACode", "") + "已关闭485指令");
                }
                break;
            default:
                closeSocket();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            show_Toast(getString(R.string.text_sync_tip11));
            isTongBu = false;
            switch (Build.DEVICE) {
                case "T-QBZD-Z6":
                case "M900":
                    send485Cmd("0005" + MmkvUtils.getcode("ACode", ""));
                    AppLogUtils.writeAppXBLog(MmkvUtils.getcode("ACode", "") + "子设备发起退出级联0005指令");
                    Utils.writeLog(MmkvUtils.getcode("ACode", "") + "子设备发起退出级联0005指令");
                    break;
                default:
                    closeSocket();
                    break;
            }
            finish();
            AppLogUtils.writeAppXBLog("---点击返回按键退出有线级联界面---");
            Utils.writeLog("---点击返回按键退出有线级联界面---");
            return true;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EMgpio.SetGpioDataLow(94);//下电
        closeM900Rs485("页面销毁时正常关闭485");
        AppLogUtils.writeAppXBLog("子设备：" + MmkvUtils.getcode("ACode", "") + "页面退出时开始关闭485指令");
        Utils.writeLog("子设备：" + MmkvUtils.getcode("ACode", "") + "页面退出时开始关闭485指令");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
