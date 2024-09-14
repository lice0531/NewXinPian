package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.SecondNetTestCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.cmd.vo.From22WriteDelay;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.custom.ErrListAdapter;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.models.VoDenatorBaseInfo;
import android_serialport_api.xingbang.models.VoFiringTestError;
import android_serialport_api.xingbang.utils.CommonDialog;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;

/**
 * 测试页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TestDenatorActivity extends SerialPortActivity {

    private DatabaseHelper mMyDatabaseHelper;
    private List<VoBlastModel> list_lg = new ArrayList<>();
    private SQLiteDatabase db;
    private TextView secondTxt;
    private TextView ll_firing_Volt_4;
    private TextView ll_firing_IC_4;
    private TextView ll_firing_deAmount_4;
    private TextView ll_firing_errorNum_4;
    private TextView tv_dianliu;//参考电流
    private LinearLayout ll_1;
    private LinearLayout ll_2;
    private LinearLayout ll_3;
    private static int tipInfoFlag = 0;
    private Button btn_return;
    private Button btn_ssqb;
    private Button btn_jixu;
    private Button btn_return_complete;
    private Button btn_firing_lookError_4;//查看错误
    private Handler mHandler_1 = null;//总线稳定
    private Handler busHandler_dianliu = null;//电流电压信息
    private Handler errHandler = null;//总线信息
    private Handler errHandler_update = null;//总线信息
    private Handler Handler_tip = null;//提示信息
    private Handler checkHandler = null;//更正错误
    private static volatile int stage;
    private volatile int firstCount = 0;
    private volatile int firstCount_min = 25;
    private int firstCount_panduan = 0;//低压合格次数
    private volatile int sixCount = 0;
    private volatile VoDenatorBaseInfo writeDenator;
    private long thirdStartTime = 0;//第三阶段每个雷管返回命令计时器
    private VoFiringTestError thirdWriteErrorDenator;//写入错误雷管
    private Queue<VoDenatorBaseInfo> blastQueue;//雷管队列
    private Queue<VoFiringTestError> errorList;//错误雷管队列
    private volatile int thirdWriteCount;//雷管发送计数器
    private volatile int reThirdWriteCount;//获得 返回 数量
    private From42Power busInfo;
    private ThreadFirst firstThread;
    private ArrayList<Map<String, Object>> errDeData = new ArrayList<>();//错误雷管
    private SendOpenPower sendOpenThread;
    private int fourOnlineDenatorFlag = -1;//是否存在未注册雷管 2存在3不存在
    private static Handler noReisterHandler = null;//没有注册的雷管
    private volatile int initCloseCmdReFlag = 0;
    private volatile int revCloseCmdReFlag = 0;
    private volatile int revOpenCmdReFlag = 0;
    private volatile int revOpenCmdTestFlag = 0;//收到了打开测试命令
    private volatile int revPowerFlag = 0;
    private int maxSecond = 0;
    private int denatorCount = 0;//雷管数量
    private float dangqian_ic = 0;//记录当前电流,用于判断当前实际雷管电流是否过大
    private byte[] initBuf;//发送的命令
    private int stage_state = 0;
    private int errtotal = 0;//错误数量
    private int Preparation_time;//准备时间
    private int totalerrorNum;//错误雷管数量
    private String TAG = "组网测试";
    private String version = "02";
    private boolean chongfu = false;//是否已经检测了一次
    private boolean send_kg = true;//是否发送41
    public static final int RESULT_SUCCESS = 1;
    private String mRegion;     // 区域
    private  int cankaodianliu = 15;
    private List<DenatorBaseinfo> errlist;
    private String Yanzheng = "";//是否验证地理位置
    private String changjia = "TY";
    List<Float> list_dianliu = new ArrayList();
    private boolean isSerialPortClosed = false;//是否已关闭串口
    //最大线程数设置为2，队列最大能存2，使用主线程执行的拒绝策略
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,2,0, TimeUnit.SECONDS,new LinkedBlockingQueue<>(2),new ThreadPoolExecutor.CallerRunsPolicy());

    //初始化
    //off()方法 true 获取全部雷管  flase 获取错误雷管
    private void initParam(boolean all_lg) {
        initCloseCmdReFlag = 0;
        revCloseCmdReFlag = 0;
        revOpenCmdReFlag = 0;//打开电压指令
        revOpenCmdTestFlag = 0;//收到了打开测试命令
        thirdStartTime = 0;
        stage = 0;
        tipInfoFlag = 0;

        writeDenator = null;
//        busInfo = null;
        thirdWriteErrorDenator = null;
        errDeData.clear();
        errorList.clear();
//        firstCount = Preparation_time;
        sixCount = 0;
        thirdWriteCount = 0;//雷管发送计数器
        reThirdWriteCount = 0;//检测返回数量
        errtotal = 0;//错误数量
        totalerrorNum = 0;//错误数量总数
        if (all_lg) {
            denatorCount = 0;
        }
        send_kg = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writedelay_denator);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
        if(changjia.equals("CQ")){
            cankaodianliu=15;
        }else {
            cankaodianliu=16;
        }
        //获取区号
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        Yanzheng = (String) MmkvUtils.getcode("Yanzheng", "验证");
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        blastQueue = new LinkedList<>();
        errorList = new LinkedList<>();
        getUserMessage();//放在前面
        initParam(false);//清空所有数据,要放在读取数据的方法之前
        initView();
        loadMoreData();//读取数据
        initHandler();


        if (denatorCount < 1) {
            show_Toast(getResources().getString(R.string.text_error_tip30));
            closeThread();
            Intent intentTemp = new Intent();
            intentTemp.putExtra("backString", "");
            setResult(1, intentTemp);
            finish();
            return;
        }

        ll_firing_deAmount_4.setText("" + denatorCount);
        // getDenatorType();
        Utils.writeRecord("---进入组网测试页面---");
        Utils.writeRecord("开始测试,雷管总数为" + denatorCount);
        sendOpenThread = new SendOpenPower();
        sendOpenThread.start();

    }

    private void initHandler() {
        //接受消息之后更新imageview视图
        mHandler_1 = new Handler(msg -> {
            execStage(msg);
            return false;
        });
        busHandler_dianliu = new Handler(msg -> {
            if (busInfo != null) {
                String displayIcStr = busInfo.getBusCurrentIa() + "μA";//保留两位小数
                float displayIc = busInfo.getBusCurrentIa();
                dangqian_ic = busInfo.getBusCurrentIa();
                Log.e("当前电流", "dangqian_ic: " + dangqian_ic);

            }
//            if ((Preparation_time*0.2 == 9 && busInfo.getBusVoltage() < 6.5)) {
//                Log.e("总线电压", "busInfo.getBusVoltage()" + busInfo.getBusVoltage());
//                AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
//                        .setTitle("当前电压过低")//设置对话框的标题//"成功起爆"
//                        .setMessage("起爆器电压过低,请再次启动测试流程,进行测试")//设置对话框的内容"本次任务成功起爆！"
//                        //设置对话框的按钮
//                        .setNegativeButton("退出", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                closeThread();
//                                closeForm();
//                                finish();
//                            }
//                        })
//                        .create();
//                dialog.show();
//            }
            return false;
        });
        errHandler = new Handler(msg -> {
            String errAmoutStr = ll_firing_errorNum_4.getText().toString();
            if (errAmoutStr.trim().length() < 1) {
                errAmoutStr = "0";
//                    ll_firing_errorNum_4.setTextColor(Color.GREEN);
            }
            ll_firing_errorNum_4.setText("" + (Integer.parseInt(errAmoutStr) + 1));
            totalerrorNum = Integer.parseInt(errAmoutStr) + 1;
            ll_firing_errorNum_4.setTextColor(Color.RED);
            return false;
        });
        errHandler_update = new Handler(msg -> {
            VoFiringTestError errorDe = (VoFiringTestError) msg.obj;
            DenatorBaseinfo denator = Application.getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(errorDe.getShellBlastNo())).unique();
            denator.setErrorCode("00");
            denator.setErrorName("命令未返回");
            Application.getDaoSession().update(denator);
            errHandler.sendMessage(errHandler.obtainMessage());
            return false;
        });
        noReisterHandler = new Handler(msg -> {
            if (fourOnlineDenatorFlag == 2) {
                disPlayNoReisterDenator(0);//提示框
                Log.e("未注册雷管", "线上有未注册雷管: ");
            }
//                if (twoErrorDenatorFlag == 1) {
//                    twoErrorDenatorFlag = 0;
//                    String err = ll_firing_errorAmount_2.getText().toString();
//                    if (err == null || err.length() < 1) err = "0";
//                    ll_firing_errorAmount_2.setText("" + (Integer.parseInt(err) + 1));
//                    ll_firing_errorAmount_2.setTextColor(Color.RED);
//                }
            return false;
        });
        Handler_tip = new Handler(msg -> {
            Bundle b = msg.getData();
            String shellStr = b.getString("shellStr");
            if (msg.what == 1) {
                show_Toast(getResources().getString(R.string.text_qberr3) + shellStr + getResources().getString(R.string.text_qberr4));
            } else if (msg.what == 2) {
                AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
                        .setTitle(R.string.text_test_title1)//设置对话框的标题
                        .setMessage(R.string.text_text_msg1)//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton(getResources().getString(R.string.text_sync_tc), (dialog1, which) -> {
                            dialog1.dismiss();
                            finish();
                        })
//                        .setNeutralButton("确定", (dialog12, which) -> dialog12.dismiss())
                        .create();
                if (!TestDenatorActivity.this.isFinishing()) {//xActivity即为本界面的Activity
                    dialog.show();
                }
            } else if (msg.what == 3) {
                ll_firing_errorNum_4.setText("0");
                totalerrorNum = 0;
            }
            return false;
        });
        checkHandler = new Handler(msg -> {
            String errNumStr = ll_firing_errorNum_4.getText().toString();
//            String tureNumStr = ll_firing_tureNum.getText().toString();
//            if (tureNumStr.trim().length() < 1) {
//                tureNumStr = "0";
//            }
            if (Integer.parseInt(errNumStr) > 0) {//更正错误雷管前有个获取错误总数的方法,可能会导致-1,所以要判断一下
                ll_firing_errorNum_4.setText("" + ((Integer.parseInt(errNumStr) - 1)));
                totalerrorNum = Integer.parseInt(errNumStr) - 1;
                ll_firing_errorNum_4.setTextColor(Color.GREEN);
            }


//            ll_firing_tureNum.setText("" + (Integer.parseInt(tureNumStr) + 1));
//            totaltureNum = Integer.parseInt(tureNumStr) + 1;
            return false;
        });
    }

    private void initView() {
        secondTxt = (TextView) findViewById(R.id.ll_txt_firing_4);
        ll_firing_Volt_4 = (TextView) findViewById(R.id.ll_firing_Volt_4);
        ll_firing_IC_4 = (TextView) findViewById(R.id.ll_firing_IC_4);
        ll_firing_deAmount_4 = (TextView) findViewById(R.id.ll_firing_deAmount_4);
        ll_firing_errorNum_4 = (TextView) findViewById(R.id.ll_firing_errorAmount_4);
        tv_dianliu = (TextView) findViewById(R.id.tv_dianliu);

        ll_1 = (LinearLayout) findViewById(R.id.ll_test_st_bt);
        ll_2 = (LinearLayout) findViewById(R.id.ll_test_end_bt);
        ll_3 = (LinearLayout) findViewById(R.id.ll_test_ssqb);
        ll_2.setVisibility(View.GONE);
        btn_return = (Button) findViewById(R.id.btn_firing_return_4);
        btn_ssqb = (Button) findViewById(R.id.btn_firing_ssqb);
        btn_return.setOnClickListener(v -> {
            closeThread();
            closeForm();
        });
        btn_ssqb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (needIgnoreClick()) {
                    return;
                }
                enterFiringPage();
            }
        });
        btn_return_complete = findViewById(R.id.btn_test_return);

        btn_return_complete.setOnClickListener(v -> {
            closeThread();
            closeForm();
        });

        btn_jixu = (Button) findViewById(R.id.btn_test_jixu);
        btn_jixu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (needIgnoreClick()) {
                    return;
                }
                enterFiringPage();
            }
        });
        btn_firing_lookError_4 = (Button) findViewById(R.id.btn_test_lookError);
        btn_firing_lookError_4.setOnClickListener(v -> {
            loadErrorBlastModel();
//                loadMoreData();
            createDialog();
        });
    }

    private void enterFiringPage() {
        sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22
        closeThread();
        mHandler_1.removeMessages(0);
        busHandler_dianliu.removeMessages(0);
        errHandler.removeMessages(0);
        errHandler_update.removeMessages(0);
        Handler_tip.removeMessages(0);
        checkHandler.removeMessages(0);
        if (db != null) db.close();
        fixInputMethodManagerLeak(TestDenatorActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication.closeSerialPort();
                Log.e(TAG, "调用mApplication.closeSerialPort()开始关闭串口了。。");
                mSerialPort = null;
                isSerialPortClosed = true;
            }
        }).start();
        finish();
        String str5 = "起爆";
        Log.e("验证2", "Yanzheng: " + Yanzheng);
        Intent intent;//金建华
        if (Yanzheng.equals("验证")) {
            //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
            intent = new Intent(TestDenatorActivity.this, VerificationActivity.class);
        } else {
            intent = new Intent(TestDenatorActivity.this, FiringMainActivity.class);
        }
        intent.putExtra("dataSend", str5);
        startActivity(intent);
    }

    private void getDenatorType() {
        String selection = "isSelected = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"是"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOR_TYPE, null, selection, selectionArgs, null, null, null);
        String second = "0";
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getString(2).matches("\\d+")) {//判断是否是数字
                second = cursor.getString(2);
            }
            cursor.close();
        }
        maxSecond = Integer.parseInt(second) * 1000;
    }

    private void getUserMessage() {
        List<MessageBean> message = getDaoSession().getMessageBeanDao().queryBuilder().where(MessageBeanDao.Properties.Id.eq((long) 1)).list();
        if (message.size() > 0) {
            Preparation_time = Integer.parseInt(message.get(0).getPreparation_time());//跟起爆测试一样
            version = message.get(0).getVersion();
            Log.e(TAG, "version: " + version);
        }
    }


    //提示对话框,提示有未注册雷管或者未处理雷管
    public void disPlayNoReisterDenator(final int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestDenatorActivity.this);
        int blastNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
        builder.setTitle(getString(R.string.text_alert_tip));
        builder.setMessage(getResources().getString(R.string.text_alert_tip2));//"总线上有未处理的雷管，是否继续起爆？"
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {
            if (flag != 1) {
                fourOnlineDenatorFlag = 3;
            }
            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
            dialog.dismiss();
            closeThread();
            closeForm();
            //  builder.
        });
        builder.show();
        if (flag != 1) {
            fourOnlineDenatorFlag = 3;
        }

    }

    /***
     * 得到错误雷管数
     */
    private void getErrorBlastCount() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryErrLeiGuan();//带参数是查一个区域,不带参数是查所有
        totalerrorNum = list.size();//得到数据的总条数
        ll_firing_errorNum_4.setText("" + totalerrorNum);
//        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where statusCode =? and errorCode<> ?";
//        Cursor cursor = db.rawQuery(sql, new String[]{"02", "FF"});
//        totalerrorNum = cursor.getCount();//得到数据的总条数
//        cursor.close();
//        ll_firing_errorNum_4.setText("" + totalerrorNum);
//        int total = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
//        if (dangqian_ic > (total - totalerrorNum) * 24) {
//            AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
//                    .setTitle("当前实际电流过大")//设置对话框的标题
//                    .setMessage("雷管正常数量为:" + (total - totalerrorNum) + ",当前电流超过参考电流" + ((total - totalerrorNum) * 70) + "μA,请排查错误雷管后重新进行检测")//设置对话框的内容
//                    //设置对话框的按钮
//                    .setNegativeButton("退出", (dialog12, which) -> {
//                        dialog12.dismiss();
//                        finish();
//                    })
//                    .setNeutralButton("确定", (dialog1, which) -> dialog1.dismiss())
//                    .create();
//            dialog.show();
//            show_Toast("当前电流过大");
//        }
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
//        Log.e(TAG, "errDeData: " + errDeData.toString());
    }

    /***
     * 建立错误对话框
     */
    public void createDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View getlistview = inflater.inflate(R.layout.firing_error_listview, null);

        // 给ListView绑定内容
        ListView errlistview = getlistview.findViewById(R.id.X_listview);
//        SimpleAdapter adapter = new SimpleAdapter(this, errDeData, R.layout.firing_error_item,
//                new String[]{"serialNo", "shellNo", "errorName", "delay"},
//                new int[]{R.id.X_item_no, R.id.X_item_shellno, R.id.X_item_errorname, R.id.X_item_delay});
//        // 给listview加入适配器
//        errlistview.setAdapter(adapter);
        ErrListAdapter mAdapter = new ErrListAdapter(this, errDeData, R.layout.firing_error_item);
        errlistview.setAdapter(mAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_alert_tablename1));//"错误雷管列表"
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    private void endTest() {
        closeThread();
        initParam(false);
        //说明网检结束
        MmkvUtils.savecode("isTestDenator", "Y");
    }


    @Override
    public void sendInterruptCmd() {
        byte[] reCmd = SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00");//23 退出注册模式
        sendCmd(reCmd);
        super.sendInterruptCmd();
    }

    /**
     * 加载数据
     */
    private void loadMoreData() {
        blastQueue.clear();
        errorList.clear();

        List<DenatorBaseinfo> denatorBaseinfos = new GreenDaoMaster().queryDetonatorRegionAsc();
//        List<DenatorBaseinfo> denatorBaseinfos = new GreenDaoMaster().queryErrLeiGuan();//带参数是查一个区域,不带参数是查所有
        //int count=0;
        for (DenatorBaseinfo a : denatorBaseinfos) {
            VoBlastModel item = new VoBlastModel();
            item.setBlastserial(a.getBlastserial());
            item.setDelay((short) a.getDelay());
            item.setShellBlastNo(a.getShellBlastNo());
            item.setDenatorId(a.getDenatorId());
            item.setDenatorIdSup(a.getDenatorIdSup());
            item.setZhu_yscs(a.getZhu_yscs());
            item.setCong_yscs(a.getCong_yscs());
//            if (a.getStatusCode().equals("02")) {
            list_lg.add(item);
            blastQueue.offer(item);
//            }
        }

        denatorCount = blastQueue.size();
        Log.e("雷管队列", "denatorCount: " + denatorCount);
        tv_dianliu.setText(denatorCount * cankaodianliu + "μA");//参考电流
    }

    /**
     * 加载数据
     */
    private void loadMoreData_err() {
        blastQueue.clear();
        errorList.clear();

//        List<DenatorBaseinfo> denatorBaseinfos = new GreenDaoMaster().queryDetonatorRegionAsc();
        List<DenatorBaseinfo> denatorBaseinfos = new GreenDaoMaster().queryErrLeiGuan();//带参数是查一个区域,不带参数是查所有
        //int count=0;
        for (DenatorBaseinfo a : denatorBaseinfos) {
            VoBlastModel item = new VoBlastModel();
            item.setBlastserial(a.getBlastserial());
            item.setDelay((short) a.getDelay());
            item.setShellBlastNo(a.getShellBlastNo());
            item.setDenatorId(a.getDenatorId());
            item.setDenatorIdSup(a.getDenatorIdSup());
            item.setZhu_yscs(a.getZhu_yscs());
            item.setCong_yscs(a.getCong_yscs());
//            if (a.getStatusCode().equals("02")) {
            list_lg.add(item);
            blastQueue.offer(item);
//            }
        }

//        denatorCount = blastQueue.size();
        Log.e("错误加雷管队列", "denatorCount: " + denatorCount);
//        tv_dianliu.setText(denatorCount * 12 + "μA");//参考电流
    }

    /**
     * 获取错误雷管
     */
    private void getErrblastQueue() {
        blastQueue = new ConcurrentLinkedQueue<>();
        errorList = new ConcurrentLinkedQueue<>();
//        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao().loadAll();
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryErrLeiGuan();//带参数是查一个区域,不带参数是查所有
        for (DenatorBaseinfo denatorBaseinfo : list) {
            int serialNo = denatorBaseinfo.getBlastserial(); //获取第二列的值 ,序号
            String shellNo = denatorBaseinfo.getShellBlastNo();//管壳号
            String denatorId = denatorBaseinfo.getDenatorId();//管壳号
            String denatorIdSup = denatorBaseinfo.getDenatorIdSup();//管壳号
            short delay = (short) denatorBaseinfo.getDelay();//获取第三列的值
            VoDenatorBaseInfo vo = new VoDenatorBaseInfo();
            vo.setBlastserial(serialNo);
            vo.setDelay(delay);
            vo.setShellBlastNo(shellNo);
            vo.setDenatorId(denatorId);
            vo.setDenatorIdSup(denatorIdSup);
            vo.setZhu_yscs(denatorBaseinfo.getZhu_yscs());
            vo.setCong_yscs(denatorBaseinfo.getCong_yscs());
            blastQueue.offer(vo);
        }
        reThirdWriteCount = 0;
        thirdWriteCount = 0;
//        firstThread.blastQueue = blastQueue;
    }


    @SuppressLint("SetTextI18n")
    public void execStage(Message msg) {
        switch (stage) {
            case 0:
                if (tipInfoFlag == 3) {//未收到关闭电源命令
                    show_Toast(getString(R.string.text_error_tip32));//未收到关闭电源命令
                }
                if (tipInfoFlag == 4) {//未收到打开电源命令
                    show_Toast(getString(R.string.text_error_tip33));
                }
                break;
            case 1:
                if (firstCount == -10) {//未收到开启测试命令
//                    if (firstCount == 400)
                    show_Toast(getString(R.string.text_error_tip34));
                    break;
                }
                if (busInfo != null) {//8秒后再显示电压电流
                    String displayIcStr = (int) busInfo.getBusCurrentIa() + "μA";
                    float displayIc = busInfo.getBusCurrentIa();
                    //displayIc =
                    dangqian_ic = busInfo.getBusCurrentIa();
                    ll_firing_Volt_4.setText("" + busInfo.getBusVoltage() + "V");
                    ll_firing_IC_4.setText("" + displayIcStr);
                    if (displayIc == 0 && denatorCount > 5 && firstCount < 5&&firstCount >2) {//Preparation_time * 0.2
                        ll_firing_IC_4.setTextColor(Color.RED);
                        show_Toast(getString(R.string.text_test_tip12));
                        btn_jixu.setVisibility(View.GONE);
                        stage = 5;
                        Utils.writeRecord("总线电流为0");
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                        return;
                    }

//                    if (displayIc < denatorCount * cankaodianliu * 0.25 && firstCount < Preparation_time * 0.25) {//总线电流小于参考值一半,可能出现断路
//                        ll_firing_IC_4.setTextColor(Color.RED);
//                        show_Toast("当前电流过小,请检查线路是否出现断路");
//                        stage = 5;
//                        Utils.writeRecord("总线电流小于参考值一半,可能出现断路");
//                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                        return;
//                    }
                    if (busInfo.getBusVoltage() < 6 && firstCount < 10) {//Preparation_time * 0.5
                        ll_firing_Volt_4.setTextColor(Color.RED);
                        btn_jixu.setVisibility(View.GONE);
                        show_Toast(getString(R.string.text_test_tip13));
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                        stage = 5;
                        Utils.writeRecord("电压异常,电压为" + busInfo.getBusVoltage() + "V");
                        return;
                    }
                    //判断电流过大是用的之前的参数,这个后续会改
                    if (displayIc > 21000 ) {//Preparation_time * 0.5
                        displayIcStr = displayIcStr + getString(R.string.text_text_ysdl);
                        ll_firing_IC_4.setTextColor(Color.RED);
                        ll_firing_IC_4.setTextSize(20);
                        Utils.writeRecord("--电流:" + displayIcStr + "μA  --电压:" + busInfo.getBusVoltage() + "V,疑似短路");

                    } else if (displayIc > (denatorCount * cankaodianliu +1000) && displayIc < (denatorCount * cankaodianliu +4000) && displayIc > 10) {// "电流偏大";
                        displayIcStr = displayIcStr + getString(R.string.text_test_dlpd);
                        ll_firing_IC_4.setTextColor(Color.RED);// "电流过大";
                        ll_firing_IC_4.setTextSize(20);
                        Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,电流偏大");
                    }else if (displayIc > (denatorCount * cankaodianliu +4000) ) {//Preparation_time * 0.5
                        Log.e(TAG, "电流过大: ");
                        displayIcStr = displayIcStr + getString(R.string.text_test_dlgd);
                        ll_firing_IC_4.setTextColor(Color.RED);// "电流过大";
                        ll_firing_IC_4.setTextSize(20);
                        Utils.writeRecord("电流:" + busInfo.getBusCurrentIa() + "μA  --电压:" + busInfo.getBusVoltage() + "V" + ",当前电流过大");
                    } else if (displayIc < 4 + denatorCount * 6 ) {//Preparation_time * 0.5
                        displayIcStr = displayIcStr + getString(R.string.text_test_ysdl);
                        ll_firing_IC_4.setTextColor(Color.RED);// "疑似断路";
                        ll_firing_IC_4.setTextSize(20);
                        Utils.writeRecord("电流:" + busInfo.getBusCurrentIa() + "μA  --电压:" + busInfo.getBusVoltage() + "V" + ",疑似断路");
                    } else {
                        ll_firing_IC_4.setTextColor(Color.GREEN);
                        Utils.writeRecord("电流:" + busInfo.getBusCurrentIa() + "μA  --电压:" + busInfo.getBusVoltage() + "V" + ",当前电流正常");
                    }
                    ll_firing_IC_4.setText(displayIcStr);


                    //电流大于4800
//                    Log.e(TAG, "displayIc: " + displayIc);
                    if (displayIc > 21000 && firstCount >= 20) {
                        stage = 7;
                        mHandler_1.handleMessage(Message.obtain());
//                        if (!chongfu) {
//                            initDialog("当前检测到总线电流过大,正在准备重新进行网络检测,请耐心等待。",true);//弹出框
//                        } else {
                        sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22
                        initDialog_zanting_xiadian(getResources().getString(R.string.text_fir_dialog6));//弹出框
//                        }
                        return;
                    }
//                    if (busInfo.getBusVoltage() < 6.3&& firstCount > Preparation_time * 0.5) {
//                        AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
//                                .setTitle("总线电压过低")//设置对话框的标题//"成功起爆"
//                                .setMessage("当前起爆器电压异常,可能会导致总线短路,请检查线路后再次启动起爆流程,进行起爆")//设置对话框的内容"本次任务成功起爆！"
//                                //设置对话框的按钮
//                                .setNeutralButton("继续", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
//                                        sendCmd(reCmd);
//                                        dialog.dismiss();
//                                        closeThread();
//                                        closeForm();
//                                        Utils.writeRecord("电压过低,电压="+busInfo.getBusVoltage()+"V");
//                                        finish();
//
//                                    }
//                                })
//                                .create();
//                        dialog.show();
//                    }

//                    ll_firing_errorNum_4.setText(""+busInfo.getFiringVoltage()) ;
                }
                secondTxt.setText(getString(R.string.text_test_tip1) + firstCount);//"等待总线稳定:"
                break;
            case 3:
                if (thirdWriteCount == 1) {
                    ll_firing_errorNum_4.setText("0");
                }
                if (thirdWriteErrorDenator != null) {//写入未返回的错误雷管
                    show_Toast(thirdWriteErrorDenator.getShellBlastNo() + getResources().getString(R.string.text_xpwfh));
                    thirdWriteErrorDenator = null;//设置错误雷管
                }
//                if (errorList != null && errorList.size() >= 0) {
//                    int iLoop = 0;
//                    while (!errorList.isEmpty()) {//写入错误雷管
//                        VoFiringTestError er = errorList.poll();
//                        if (er != null) {
//                            From22WriteDelay df = new From22WriteDelay();
//                            df.setShellNo(er.getShellBlastNo());
//                            df.setCommicationStatus("AF");
//                            df.setDelayTime(er.getDelay());
//                            this.updateDenator(df, er.getDelay());
//                            iLoop++;
//                        }
//                    }
//
//                }
                secondTxt.setText(getString(R.string.text_test_tip2) + thirdWriteCount + getString(R.string.text_test_tip3));

                break;
            case 4:
                if (totalerrorNum == 0) {
//                    stopXunHuan();
                    toFiringPage();
                } else if (totalerrorNum == denatorCount && busInfo.getBusCurrentIa() > 21000) {//大于4800u ，全错
                    Log.e(TAG, "大于21000u ，全错: ");
                    byte[] reCmd = SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00");//22
                    sendCmd(reCmd);
//                    if (chongfu) {
                    initDialog_zanting(getString(R.string.text_test_tip5));//弹出框
//                    } else {
//                        initDialog("当前有雷管检测错误,系统正在进行2次检测,如果依然检测错误,请检查线夹等部位是否有进水进泥等短路情况,确认无误后点击继续进行检测。",false);//弹出框
//                    }
                } else if (totalerrorNum == denatorCount && busInfo.getBusCurrentIa() < 21000) {//小于4800u ，全错
//                    byte[] reCmd = SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00");//22
//                    sendCmd(reCmd);
//                    if (chongfu) {
                    initDialog_zanting(getResources().getString(R.string.text_fir_dialog11));//弹出框
//                    } else {
//                        initDialog("当前有雷管检测错误,系统正在进行2次检测,如果依然检测错误,请检查线夹等部位是否有进水进泥等短路情况,确认无误后点击继续进行检测。",false);//弹出框
//                    }

                    Log.e(TAG, "小于21000u ，全错: stage=" + stage);
                } else if (totalerrorNum > 0 && busInfo.getBusCurrentIa() < denatorCount * cankaodianliu + 100) {//小于参考值 ，部分错
                    // (从上面取下来的条件)
//                    byte[] reCmd = SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00");//22
//                    sendCmd(reCmd);
//                    if (chongfu) {//李斌要修改之前的
                    initDialog_zanting2(getString(R.string.text_test_tip7));//弹出框
//                    } else {
//                        initDialog("当前有雷管检测错误,系统正在进行2次检测,请稍等。",false);//弹出框
//                    }
//                    if (chongfu) {
//                        initDialog_zanting2("请检查错误的雷管是否存在连接线断开或管壳码输入错误等情况!检查无误后,点击继续重新检测。");//弹出框
//                    } else {
//                        initDialog("查看错误雷管列表,疑似部分雷管连接线断开,请检查是否存在雷管连接线断开,管壳码输入错误等情况,检查完毕后点击继续按钮进行检测!",false);//弹出框
//                    }
                    Log.e(TAG, "小于参考值 ，部分错: stage=" + stage);
                } else if (totalerrorNum < denatorCount && totalerrorNum != 0 && busInfo.getBusCurrentIa() > (denatorCount * cankaodianliu + 100)) {//大于参考值 ，部分错
//                    byte[] reCmd = SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00");//22
//                    sendCmd(reCmd);
//                    if (chongfu) {
                    initDialog_zanting2(getString(R.string.text_test_tip8));//弹出框
//                    } else {
//                        initDialog("当前有雷管检测错误,系统正在进行2次检测,请稍等。",false);//弹出框
//                    }
                    Log.e(TAG, "大于参考值 ，部分错: stage=" + stage);
                } else if (errtotal > 0 && busInfo != null && busInfo.getBusCurrentIa() > (denatorCount * cankaodianliu * 0.9) && busInfo.getBusCurrentIa() < (denatorCount * cankaodianliu * 1.1)) {
                    initDialog_tip(getString(R.string.text_test_tip9));
                    stopXunHuan();//检测完成
                } else {
                    stopXunHuan();//检测完成
                }
                //(22/10/09)因浩宇反馈,有出现过错误数量为0,但是实际是有雷管错误的,所以再检测完后再获取一下错误数量
                getErrorBlastCount();
                break;
            case 5:
                endTest();
                ll_1.setVisibility(View.GONE);
                ll_2.setVisibility(View.VISIBLE);
                secondTxt.setText(R.string.text_test_tip10);
                break;

            case 6://等待处理错误时间
                Log.e(TAG, "放电阶段--execStage: " + sixCount);
                secondTxt.setText(getString(R.string.text_test_tip11) + sixCount);//"等待总线稳定:"
                break;
            case 7://等待处理错误时间
                break;
            case 44:
                secondTxt.setText(getResources().getString(R.string.text_cfd) + thirdWriteCount + getResources().getString(R.string.text_firing_err_lg));
                //写入通信未返回

                break;
            default:
        }
    }

    /***
     * 关闭表单
     */
    private void closeForm() {
        mHandler_1.removeMessages(0);
        Intent intentTemp = new Intent();
        intentTemp.putExtra("backString", "");
        setResult(1, intentTemp);
        finish();
    }

    /**
     * 关闭守护线程
     */
    private void closeThread() {
        //Thread_stage_1 ttst_1
        if (firstThread != null) {
            Log.e(TAG, "终止线程thread--firstThread.exit: " + firstThread.exit);
            firstThread.exit = true;  // 终止线程thread
            threadPoolExecutor.shutdown();
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
//        firstThread = null;
    }

    /**
     * 更新雷管状态
     */
    public void updateDenator(From22WriteDelay fromData, int writeDelay) {
        if (fromData.getShellNo() == null || fromData.getShellNo().trim().length() < 1) return;
        //greendao更新0817更新
        DenatorBaseinfo denator = Application.getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(fromData.getShellNo())).unique();
        denator.setErrorCode(fromData.getCommicationStatus());
        denator.setErrorName(fromData.getCommicationStatusName());
        if (fromData.getCommicationStatus().equals("FF")) {
            denator.setZhu_yscs(fromData.getYscs());
        }
        Application.getDaoSession().update(denator);

        errtotal++;
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
     * @author zenghp
     *
     */
    private class ThreadFirst extends Thread {
        public volatile boolean exit = false;
        private VoDenatorBaseInfo tempBaseInfo = null;

        public void run() {

            while (!exit) {
                try {
                    switch (stage) {
                        case 1:
                            //先发46
                            //再发20
                            //询问电流 40
                            //第10秒下电--13
                            //第13秒上电--41
                            //第14秒发46
                            //第15秒发20
                            if (firstCount == 0) {//经过测试初始化命令需要6秒
                                //切换模块芯片版本
                                if (changjia.equals("CQ")) {
                                    sendCmd(FourStatusCmd.send46("00", "03", denatorCount));//20(第一代)
                                } else {
                                    sendCmd(FourStatusCmd.send46("00", "01", denatorCount));//20(第一代)
                                }
                            }
                            Thread.sleep(1000);
                            if (firstCount == 0) {//经过测试初始化命令需要6秒
                                //进入测试模式
                                sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Init22_1("00"));//20
                            }


                            if (firstCount > 0 && firstCount < 5) {//Preparation_time-1
                                sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40
                            }
                            Log.e(TAG, "firstCount1: " + firstCount + "--" + Preparation_time);
                            Log.e(TAG, "firstCount: " + firstCount + "--" + "firstCount_max = " + firstCount_min);
                            if (firstCount >= firstCount_min && list_dianliu.get(list_dianliu.size() - 1) - list_dianliu.get(list_dianliu.size() - 5) < 20 && list_dianliu.get(list_dianliu.size() - 1) - list_dianliu.get(list_dianliu.size() - 5) >-20) {
                                firstCount_panduan++;
                                Log.e(TAG, "list_dianliu.size(): " + list_dianliu.size()+"--电流"+list_dianliu.get(list_dianliu.size()-1));
                                Log.e(TAG, "list_dianliu.size()-5: " + (list_dianliu.size() - 5)+"--电流"+list_dianliu.get(list_dianliu.size()- 5));
                                Log.e(TAG, "list_dianliu.get(list_dianliu.size()-1)-list_dianliu.get(list_dianliu.size()-5): " + (list_dianliu.get(list_dianliu.size() - 1) - list_dianliu.get(list_dianliu.size() - 5)));

//                                revOpenCmdTestFlag = 1;//跳转发送测试命令阶段
                                Log.e(TAG, "firstCount_panduan: " +firstCount_panduan);
//                                Thread.sleep(1000);//为了发40后等待
//                                stage = 3;
//                                mHandler_1.sendMessage(mHandler_1.obtainMessage());

                            }

                            //到达最大时间,直接跳转
                            if (firstCount >= Preparation_time){
                                Thread.sleep(1000);//为了发40后等待
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                stage = 3;
                            }
                            if (firstCount == 6) {//Preparation_time-1
//                                sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22
                                // 13 退出注册模式
                                sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));
                            }
                            if (firstCount == 8) {//Preparation_time-1
                                sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启电源指令

                            }
                            if (firstCount == 9) {//经过测试初始化命令需要6秒
                                //切换模块芯片版本
                                if (changjia.equals("CQ")) {
                                    sendCmd(FourStatusCmd.send46("00", "03", denatorCount));//20(第一代)
                                } else {
                                    sendCmd(FourStatusCmd.send46("00", "01", denatorCount));//20(第一代)
                                }
                            }
                            if (firstCount == 10) {//Preparation_time-1
                                sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Init22_1("00"));//20 //进入测试模式
                            }
                            if (firstCount > 10) {//Preparation_time-1  // && firstCount < Preparation_time - 1
                                sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40
                            }

                            if(firstCount_panduan>5){//判断是否符合条件,跳转阶段
                                Thread.sleep(1000);//为了发40后等待
                                stage = 3;
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                break;
                            }
                            firstCount++;
                            if (firstCount == -10) {//最大值
                                Log.e(TAG, "退出流程: ");
                                exit = true;
                                break;
                            }

                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;

                        case 3://写入延时时间，检测结果看雷管是否正常
                            if (reThirdWriteCount == thirdWriteCount) {
                                thirdStartTime = 0;
                                writeDenator = null;
                                if (blastQueue == null || blastQueue.size() < 1) {//待测雷管数小于1执行方法
                                    Thread.sleep(1000);
                                    if (denatorCount >= 300 && totalerrorNum != 0) {
                                        Handler_tip.sendMessage(Handler_tip.obtainMessage(3));
                                        Log.e(TAG, "重发错误雷管: ----------");
                                        getErrblastQueue();//重新给雷管队列赋值
                                        stage = 44;
                                    } else {
                                        //36指令
                                        int a = Integer.parseInt(ll_firing_errorNum_4.getText().toString());
                                        GreenDaoMaster master = new GreenDaoMaster();
                                        errlist = master.queryErrLeiGuan(mRegion);//带参数是查一个区域,不带参数是查所有
                                        if (a == 1 && errlist != null && errlist.size() > 0) {
                                            sendCmd(ThreeFiringCmd.send_36("00", errlist.get(0).getZhu_yscs()));//36 在网读ID检测
                                        } else {
                                            sendCmd(ThreeFiringCmd.send_36("00", "0000"));//36 在网读ID检测
                                        }
                                        Thread.sleep(500);//等待36返回
                                        Log.e(TAG, "跳转: ");
                                        stage = 4;
                                    }
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    break;
                                }

                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo = write;

                                String data = "";
                                if (write.getDenatorId() == null || write.getDenatorId().length() < 8) {
                                    Message msg = Handler_tip.obtainMessage();
                                    msg.what = 2;
                                    Bundle b = new Bundle();
                                    msg.setData(b);
                                    Handler_tip.sendMessage(msg);
                                    closeThread();
                                    break;
                                }
//                                String shellStr = write.getShellBlastNo();
//                                if (shellStr.substring(7, 8).getBytes()[0] > 70) {
//                                    Message msg = Handler_tip.obtainMessage();
//                                    msg.arg1 = 1;
//                                    Bundle b = new Bundle();
//                                    b.putString("shellStr", shellStr);
//                                    msg.setData(b);
//                                    Handler_tip.sendMessage(msg);
//                                    break;
//                                }
//                                if (shellStr == null)
//                                    continue;// || shellStr.length() != 13  //判读是否是十三位
//                                String denatorId = Utils.DetonatorShellToSerialNo_new(shellStr);//新编码
//                                String denatorId = Utils.DetonatorShellToSerialNo(shellStr);//旧编码
                                String denatorId = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorId());//新芯片编码
//                                Log.e("写入延时", "denatorId: " + denatorId);
                                denatorId = Utils.getReverseDetonatorNo(denatorId);

                                short delayTime = 0;
                                byte[] delayBye = Utils.shortToByte(delayTime);
                                String delayStr = Utils.bytesToHexFun(delayBye);
                                data = denatorId + delayStr + write.getZhu_yscs();//雷管id+延时时间+主芯片延时参数
//                                Log.e("测试21延时", "data  " + denatorId + "--" + delayStr);
                                //发送命令21写入延时时间，检测结果看雷管是否正常
                                initBuf = SecondNetTestCmd.send21("00", data);//
                                revCmd = "";//清空缓存
                                sendCmd(initBuf);//后面的shellStr没用上
                                thirdStartTime = System.currentTimeMillis();
                                writeDenator = write;
                                thirdWriteCount++;
                                Thread.sleep(100);//
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            } else {
                                long thirdEnd = System.currentTimeMillis();
                                long spanTime = thirdEnd - thirdStartTime;
                                //原本这里是设的5秒  但时间太长了就先暂时改为3.5秒
                                if (spanTime > 3500) {//发出本发雷管时，没返回超时了
                                    Log.e("当前雷管写入延时", "超时了，reThirdWriteCount: " + reThirdWriteCount);
                                    thirdStartTime = 0;
                                    //未返回
                                    if (tempBaseInfo != null) {
                                        VoFiringTestError errorDe = new VoFiringTestError();
                                        errorDe.setBlastserial(tempBaseInfo.getBlastserial());
                                        errorDe.setShellBlastNo(tempBaseInfo.getShellBlastNo());
                                        errorDe.setDelay(tempBaseInfo.getDelay());
                                        errorDe.setError(1);
                                        thirdWriteErrorDenator = errorDe;
                                        errorList.offer(errorDe);
                                        Message message = new Message();
                                        message.obj = errorDe;
                                        errHandler_update.sendMessage(message);
                                        Log.e("当前雷管写入延时", "超时了，开始errHandler_update");
                                    }
                                    tempBaseInfo = null;
                                    revCmd = "";//清空缓存
                                    reThirdWriteCount++;
                                } else {
                                    Thread.sleep(50);
                                }
                            }
                            break;
                        case 4:

                            break;
                        case 5:

                            break;
                        case 6://放电阶段

                            Thread.sleep(1000);
                            Log.e(TAG, "放电阶段:sixCount " + sixCount);
                            if (sixCount >= 120) {
                                exit = true;
                                break;
                            }
                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            sixCount++;
                            break;
                        case 7:
                            break;
                        case 44://写入延时时间，检测结果看雷管是否正常
                            if (reThirdWriteCount == thirdWriteCount) {
                                thirdStartTime = 0;
                                writeDenator = null;
                                if (blastQueue == null || blastQueue.size() < 1) {//待测雷管数小于1执行方法
                                    Thread.sleep(1000);
                                    //36指令
                                    int a = Integer.parseInt(ll_firing_errorNum_4.getText().toString());
                                    GreenDaoMaster master = new GreenDaoMaster();
                                    errlist = master.queryErrLeiGuan(mRegion);//带参数是查一个区域,不带参数是查所有
                                    Log.e(TAG, "a: " + a + "  errlist" + errlist.size());
                                    if (a == 1 && errlist != null && errlist.size() > 0) {
                                        sendCmd(ThreeFiringCmd.send_36("00", errlist.get(0).getZhu_yscs()));//36 在网读ID检测
                                    } else {
                                        sendCmd(ThreeFiringCmd.send_36("00", "0000"));//36 在网读ID检测
                                    }
                                    Thread.sleep(500);//等待36返回
                                    Log.e(TAG, "跳转: ");
                                    stage = 4;
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    break;
                                }

                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo = write;

                                String data = "";
                                if (write.getDenatorId() == null || write.getDenatorId().length() < 8) {
                                    Message msg = Handler_tip.obtainMessage();
                                    msg.what = 2;
                                    Bundle b = new Bundle();
                                    msg.setData(b);
                                    Handler_tip.sendMessage(msg);
                                    closeThread();
                                    break;
                                }

                                String denatorId = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorId());//新芯片编码
//                                Log.e("写入延时", "denatorId: " + denatorId);
                                denatorId = Utils.getReverseDetonatorNo(denatorId);

                                short delayTime = 0;
                                byte[] delayBye = Utils.shortToByte(delayTime);
                                String delayStr = Utils.bytesToHexFun(delayBye);
                                data = denatorId + delayStr + write.getZhu_yscs();//雷管id+延时时间+主芯片延时参数
//                                Log.e("测试21延时", "data  " + denatorId + "--" + delayStr);
                                //发送命令21写入延时时间，检测结果看雷管是否正常
                                initBuf = SecondNetTestCmd.send21("00", data);//
                                revCmd = "";//清空缓存
                                sendCmd(initBuf);//后面的shellStr没用上
                                thirdStartTime = System.currentTimeMillis();
                                writeDenator = write;
                                thirdWriteCount++;
                                Thread.sleep(100);//
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            } else {
                                long thirdEnd = System.currentTimeMillis();
                                long spanTime = thirdEnd - thirdStartTime;
                                if (spanTime > 3500) {//发出本发雷管时，没返回超时了
                                    thirdStartTime = 0;
                                    //未返回
                                    if (tempBaseInfo != null) {
                                        VoFiringTestError errorDe = new VoFiringTestError();
                                        errorDe.setBlastserial(tempBaseInfo.getBlastserial());
                                        errorDe.setShellBlastNo(tempBaseInfo.getShellBlastNo());
                                        errorDe.setDelay(tempBaseInfo.getDelay());
                                        errorDe.setError(1);
                                        thirdWriteErrorDenator = errorDe;
                                        errorList.offer(errorDe);
                                        Message message = new Message();
                                        message.obj = errorDe;
                                        errHandler_update.sendMessage(message);
                                    }
                                    tempBaseInfo = null;
                                    revCmd = "";//清空缓存
                                    reThirdWriteCount++;
                                } else {
                                    Thread.sleep(50);
                                }
                            }
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected synchronized void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];

        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);
        Utils.writeLog("<-:" + fromCommad);
//        Log.e("返回命令--测试页面", "fromCommad: "+fromCommad );
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd2(fromCommad);
//                Log.e("返回命令--测试页面", "cmd: "+cmd );
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                    doWithReceivData(cmd, localBuf, localSize);
                }
            }
        }

    }

    /***
     * 处理芯片返回
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf, int size) {

        byte[] locatBuf = new byte[size];
        System.arraycopy(cmdBuf, 0, locatBuf, 0, size);

        if (DefCommand.CMD_1_REISTER_4.equals(cmd)) {//13 关闭电源
            if (initCloseCmdReFlag == 1) {//打开电源
                revCloseCmdReFlag = 1;
                sendOpenThread = new SendOpenPower();
                sendOpenThread.start();
            }
        } else if (DefCommand.CMD_2_NETTEST_1.equals(cmd)) {//20 进入测试模式
//            revOpenCmdTestFlag = 1;//跳转发送测试命令阶段
        } else if (DefCommand.CMD_2_NETTEST_2.equals(cmd)) {//21写入延时时间，检测结果看雷管是否正常

            From22WriteDelay fromData = SecondNetTestCmd.decodeFromReceiveDataWriteCommand22("00", locatBuf);
//            if((!fromData.getCommicationStatusName(this).equals("通信成功"))&&stage_state<1){
//                sendCmd(initBuf);
//                Log.e("错误重新发送", "次数: "+stage_state );
//                stage_state++;
//                return;
//            }else {
//                stage_state=0;
//            }
            if (writeDenator != null) {
                VoDenatorBaseInfo temp = writeDenator;
                short writeDelay = temp.getDelay();
                fromData.setShellNo(temp.getShellBlastNo());//设置管壳码
                fromData.setDenaId(temp.getDenatorId());//设置芯片码
                updateDenator(fromData, writeDelay);
                if (!"FF".equals(fromData.getCommicationStatus())) {
                    errHandler.sendMessage(errHandler.obtainMessage());
                }
                Utils.writeRecord("--测试结果:" + fromData);
                Log.e("测试返回数据", "fromData: " + fromData);
//
                writeDenator = null;
                reThirdWriteCount++;
            }

        } else if (DefCommand.CMD_2_NETTEST_3.equals(cmd)) {//22 关闭测试
            //发出关闭获取得到电压电流

        } else if (DefCommand.CMD_3_DETONATE_7.equals(cmd)) {//36在网读ID检测
            String fromCommad = Utils.bytesToHexFun(locatBuf);
//            String noReisterFlag = ThreeFiringCmd.getCheckFromXbCommon_FiringExchange_5523_7_reval("00", fromCommad);
            String noReisterFlag = ThreeFiringCmd.jiexi_36("00", fromCommad);
            Log.e("是否有未注册雷管", "noReisterFlag: " + noReisterFlag);
//            Log.e("36指令", "fromCommad: " + fromCommad);//A621F0027F506
            //C0003607 FF 00000000 0000 DA2D C0

            if (!fromCommad.startsWith("00000000", 10)) {
                if (errlist != null && errlist.size() == 1) {
                    DenatorBaseinfo denator = Application.getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(errlist.get(0).getShellBlastNo())).unique();
                    String a = fromCommad.substring(10, 18);
                    String b = "A6240" + a.substring(6, 8) + a.substring(4, 6) + a.substring(2, 4) + a.substring(0, 2);
                    if (Utils.duibi(denator.getDenatorId().substring(5), b.substring(5)) == 7) {//只有一位不一样的时候更新
                        denator.setDenatorId(b);
                        denator.setZhu_yscs(fromCommad.substring(18, 22));
                        denator.setErrorCode("FF");
                        denator.setErrorName("通信成功");
                        Application.getDaoSession().update(denator);
                        checkHandler.sendMessage(checkHandler.obtainMessage());//错误数-1 正确数 +1
                    }
                }
            }
            //C000360700AB4427007A051BE3C0
            if (noReisterFlag.equals("00") && errlist.size() != 0) {
                noReisterFlag = "FF";
            }

//            byte[] powerCmd = SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00");//22
//            sendCmd(powerCmd);
            //在测试流程,返回都是FF
            if ("FF".equals(noReisterFlag)) {
                fourOnlineDenatorFlag = 3;
//                increase(6);//0635此处功能为直接跳到第六阶段
            } else {
                fourOnlineDenatorFlag = 2;
                noReisterHandler.sendMessage(noReisterHandler.obtainMessage());
            }

        } else if (DefCommand.CMD_4_XBSTATUS_1.equals(cmd)) {//40 获取电源状态指令
            busInfo = FourStatusCmd.decodeFromReceiveDataPower24_1("00", locatBuf);
            list_dianliu.add(busInfo.getBusCurrentIa());
            Log.e(TAG, "list_dianliu: " + list_dianliu);
            if (busHandler_dianliu == null) return;
//            busHandler_dianliu.sendMessage(busHandler_dianliu.obtainMessage());

        } else if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//41 开启总线电源指令

            if (send_kg) {//防止上电再上电新建线程
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                revOpenCmdReFlag = 1;
                sendOpenThread.exit = true;
                Log.e("开启电源指令", "revOpenCmdReFlag: " + revOpenCmdReFlag);
                stage = 1;
                if (blastQueue.size() > 0 && !chongfu) {//二次检测时不新建线程
                    firstThread = new ThreadFirst();
                    threadPoolExecutor.execute(firstThread);
                }
                send_kg = false;
            }
        } else if (DefCommand.CMD_4_XBSTATUS_7.equals(cmd)) {//46 切换版本

        }
    }

    private class SendOpenPower extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    if (zeroCount == 0) {
                        byte[] powerCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41
                        sendCmd(powerCmd);
                    }
                    if (revOpenCmdReFlag == 1) {
                        exit = true;
                        break;
                    }
                    Thread.sleep(100);
                    if (zeroCount > 100) {
                        tipInfoFlag = 4;
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
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

    private class CloseOpenPower extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    if (zeroCount == 0) {
                        initCloseCmdReFlag = 1;
                        byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");//13 退出注册模式
                        sendCmd(powerCmd);
                    }
                    if (revCloseCmdReFlag == 1) {
                        exit = true;
                        break;
                    }
                    Thread.sleep(100);
                    if (zeroCount > 80) {
                        tipInfoFlag = 3;
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
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


    private TextView mOffTextView;
    private Handler mOffHandler;
    private Timer mOffTime;
    private android.app.Dialog mDialog;
    private CommonDialog dialog;//自定义dialog


    /**
     * 倒计结束后,重新上电开始检测
     * //off()方法 true 获取全部雷管  flase 获取错误雷管
     */
    private void off(boolean all_lg) {
        initParam(all_lg);//先重置数据
        chongfu = true;//已经检测了一次
        if (all_lg) {
            loadMoreData();
        } else {
            loadMoreData_err();
        }
        firstCount=0;
        mHandler_1.sendMessage(mHandler_1.obtainMessage());
        byte[] powerCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41
        sendCmd(powerCmd);
        Log.e(TAG, "blastQueue.size(): " + blastQueue.size());
//        if(blastQueue.size() == 0){
//            blastQueue = allBlastQu;
//        }
//        ll_firing_deAmount_4.setText("0");
//        ll_firing_errorNum_4.setText("0");
//        ll_firing_Volt_4.setText("0V");
//        ll_firing_IC_4.setText("0μA");
//        ll_firing_errorNum_4.setTextColor(Color.GREEN);
    }

    private void stopXunHuan() {
        endTest();
        ll_1.setVisibility(View.GONE);
        ll_2.setVisibility(View.VISIBLE);
        btn_jixu.setVisibility(View.VISIBLE);
        secondTxt.setText(R.string.text_test_tip4);
//        byte[] initBuf2 = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_7("00");//36 在网读ID检测
//        sendCmd(initBuf2);
    }

    private void toFiringPage() {
        endTest();
        secondTxt.setText(R.string.text_test_tip4);
        ll_1.setVisibility(View.GONE);
        ll_2.setVisibility(View.GONE);
        ll_3.setVisibility(View.VISIBLE);
        btn_return.setText(R.string.text_firing_ssqb);
    }

    //off()方法 true 获取全部雷管  flase 获取错误雷管
    private void initDialog(String tip, boolean all_lg) {

        mOffTextView = new TextView(this);
        mOffTextView.setTextSize(25);
        mOffTextView.setText(tip + "\n" +  getResources().getString(R.string.text_fir_dialog1));
        mDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.text_fir_dialog2)).setCancelable(false).setView(mOffTextView)
//                .setPositiveButton("确定", (dialog, id) -> {
//                    mOffTime.cancel();//清除计时
//                    stopXunHuan();//关闭后的一些操作
//                })
//                .setNegativeButton("退出", (dialog, id) -> {
//                    dialog.cancel();
//                    mOffTime.cancel();
//                    closeThread();
//                    closeForm();
//                })

                .create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);

        mOffHandler = new Handler(msg -> {
            if (msg.what > 0) {
                //动态显示倒计时
                mOffTextView.setText(tip + "\n" + getResources().getString(R.string.text_fir_dialog1) + msg.what);
            } else {
                //倒计时结束自动关闭
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                off(all_lg);//关闭后的操作
                mOffTime.cancel();
            }
            return false;
        });

        //倒计时

        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            private int countTime = 5;

            public void run() {
                if (countTime > 0) {
                    countTime--;
                }
                if (countTime == 4) {
                    byte[] reCmd = SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00");//22
                    sendCmd(reCmd);
                }
                Message msg = new Message();
                msg.what = countTime;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 1000, 1000);
    }

    //off()方法 true 获取全部雷管  flase 获取错误雷管
    private void initDialog_tip(String tip) {
        if (!TestDenatorActivity.this.isFinishing()) {
            AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this).setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题//"成功起爆"
                    .setMessage(tip)//设置对话框的内容"本次任务成功起爆！"
                    //设置对话框的按钮
                    .setNegativeButton(getResources().getString(R.string.text_firing_jixu), (dialog1, which) -> {
                        off(true);//重新检测
                        dialog1.dismiss();
                    }).setNeutralButton (getResources().getString(R.string.text_test_exit), (dialog12, which) -> {
                        stopXunHuan();
                    }).create();
            if (!TestDenatorActivity.this.isFinishing()) {//xActivity即为本界面的Activity
                dialog.show();
            }
        }
    }

    private void initDialog_zanting_xiadian(String tip) {
        endTest();
        if (!TestDenatorActivity.this.isFinishing()) {
            chongfu = true;//已经检测了一次
            AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this).setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题//"成功起爆"
                    .setMessage(tip)//设置对话框的内容"本次任务成功起爆！"
                    //设置对话框的按钮
                    .setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog12, which) -> {
//                        stopXunHuan();
                        ll_1.setVisibility(View.GONE);
                        ll_2.setVisibility(View.VISIBLE);
                        secondTxt.setText(R.string.text_test_tip4);
                    }).create();
            if (!TestDenatorActivity.this.isFinishing()) {//xActivity即为本界面的Activity
                dialog.show();
            }
        }
    }

    private void initDialog_zanting(String tip) {
        if (!TestDenatorActivity.this.isFinishing()) {
            chongfu = true;//已经检测了一次
            AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this).setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题//"成功起爆"
                    .setMessage(tip)//设置对话框的内容"本次任务成功起爆！"
                    .setCancelable(false)
                    //设置对话框的按钮
                    .setNegativeButton (getResources().getString(R.string.text_firing_jixu), (dialog1, which) -> {
                        off(true);//重新检测
                        dialog1.dismiss();
                    }).setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog12, which) -> {
                        stopXunHuan();
                    }).create();
            dialog.show();
        }
    }

    private void initDialog_zanting2(String tip) {
        //xActivity即为本界面的Activity
        if (!TestDenatorActivity.this.isFinishing()) {
            chongfu = true;//已经检测了一次
            loadErrorBlastModel();
            LayoutInflater inflater = LayoutInflater.from(this);
            View getlistview = inflater.inflate(R.layout.firing_error_listview, null);
            LinearLayout llview = getlistview.findViewById(R.id.ll_dialog_err);
            llview.setVisibility(View.GONE);
            TextView text_tip = getlistview.findViewById(R.id.dialog_tip);
            text_tip.setText(tip);
            text_tip.setVisibility(View.VISIBLE);
            // 给ListView绑定内容
            ListView errlistview = getlistview.findViewById(R.id.X_listview);
            errlistview.setVisibility(View.GONE);
            ErrListAdapter mAdapter = new ErrListAdapter(this, errDeData, R.layout.firing_error_item);
            errlistview.setAdapter(mAdapter);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.text_fir_dialog2));//"错误雷管列表"
            builder.setView(getlistview);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.text_firing_jixu), (dialog, which) -> {
                dialogOFF(dialog);
                off(false);//重新检测
                dialog.dismiss();

            });
            builder.setNeutralButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
                dialogOFF(dialog);
                stopXunHuan();
                dialog.dismiss();
            });
            builder.setNegativeButton(getResources().getString(R.string.text_fir_dialog5), (dialog, which) -> {
//            stopXunHuan();
                llview.setVisibility(View.VISIBLE);
                text_tip.setVisibility(View.GONE);
                errlistview.setVisibility(View.VISIBLE);
                dialogOn(dialog);
            });
            builder.create().show();
        }

    }

    /**
     * 让dialog一直显示
     */
    private void dialogOn(DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialogOFF(DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        closeThread();
        closeForm();
//        Utils.saveFile();//把软存中的数据存入磁盘中
        Log.e(TAG, "onDestroy: ==========");
        if (!isSerialPortClosed) {
            sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mApplication.closeSerialPort();
                    Log.e(TAG, "调用mApplication.closeSerialPort()开始关闭串口了。。");
                    mSerialPort = null;
                }
            }).start();
        }
        super.onDestroy();
        fixInputMethodManagerLeak(this);
    }

    /**
     * 防止多次点击方法
     * */
    private static final int MIN_CLICK_DELAY_TIME = 1000; // 点击间隔1秒
    private long mLastClickTime;
    private boolean needIgnoreClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < MIN_CLICK_DELAY_TIME) {
            return true;
        }
        mLastClickTime = currentTime;
        return false;
    }
}
