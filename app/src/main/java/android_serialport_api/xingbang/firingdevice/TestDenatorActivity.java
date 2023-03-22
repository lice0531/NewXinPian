package android_serialport_api.xingbang.firingdevice;

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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.os.Build;

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
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.models.VoDenatorBaseInfo;
import android_serialport_api.xingbang.models.VoFiringTestError;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.CommonDialog;
import android_serialport_api.xingbang.utils.Utils;

import static android_serialport_api.xingbang.Application.getDaoSession;

/**
 * 测试页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TestDenatorActivity extends SerialPortActivity {

    private DatabaseHelper mMyDatabaseHelper;
    private List<VoDenatorBaseInfo> list_lg = new ArrayList<>();
    private SQLiteDatabase db;
    private TextView secondTxt;
    private TextView ll_firing_Volt_4;
    private TextView ll_firing_IC_4;
    private TextView ll_firing_deAmount_4;
    private TextView ll_firing_errorNum_4;
    private TextView tv_dianliu;//参考电流
    private LinearLayout ll_1;
    private LinearLayout ll_2;
    private static int tipInfoFlag = 0;
    private Button btn_return;
    private Button btn_return_complete;
    private Button btn_firing_lookError_4;//查看错误
    private Handler mHandler_1 = null;//总线稳定
    private Handler busHandler_dianliu = null;//电流电压信息
    private Handler errHandler = null;//总线信息
    private Handler Handler_tip = null;//提示信息
    private static volatile int stage;
    private volatile int firstCount = 28;
    private volatile int sixCount = 0;
    private volatile VoDenatorBaseInfo writeDenator;
    private long thirdStartTime = 0;//第三阶段每个雷管返回命令计时器
    private VoFiringTestError thirdWriteErrorDenator;//写入错误雷管
    private Queue<VoDenatorBaseInfo> blastQueue;//雷管队列
    private Queue<VoFiringTestError> errorList;//错误雷管队列
    private List<VoDenatorBaseInfo> denatorlist1 = new ArrayList<>();
    private List<VoDenatorBaseInfo> denatorlist2 = new ArrayList<>();
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
    public static final int RESULT_SUCCESS = 1;
    private String mRegion;     // 区域
    private int ic_cankao = 19;//雷管参考电流
    private boolean send_kg = true;//是否已经发送了40
    private boolean version_1 = true;//是否发送46

    //初始化
    private void initParam() {
        initCloseCmdReFlag = 0;
        revCloseCmdReFlag = 0;
        revOpenCmdReFlag = 0;//打开电压指令
        revOpenCmdTestFlag = 0;//收到了打开测试命令
        thirdStartTime = 0;
        stage = 0;
        tipInfoFlag = 0;

        writeDenator = null;
        busInfo = null;
        thirdWriteErrorDenator = null;
        errDeData.clear();
        errorList.clear();
        firstCount = Preparation_time;
        sixCount = 0;
        thirdWriteCount = 0;//雷管发送计数器
        reThirdWriteCount = 0;//检测返回数量
        errtotal = 0;//错误数量
        totalerrorNum = 0;//错误数量总数
        denatorCount = 0;
        send_kg = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
//        Utils.saveFile();//把软存中的数据存入磁盘中
        closeThread();

        super.onDestroy();
        fixInputMethodManagerLeak(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writedelay_denator);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //获取区号
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        blastQueue = new LinkedList<>();
        errorList = new LinkedList<>();
        getUserMessage();//放在前面
        initParam();//清空所有数据,要放在读取数据的方法之前
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
        Log.e(TAG, "stage: " + stage);

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
                show_Toast("当前版本只支持0-F," + shellStr + "雷管超出范围");
            } else if (msg.what == 2) {
                AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
                        .setTitle("当前雷管信息不完整")//设置对话框的标题
                        .setMessage("当前雷管信息不完整,请先进行项目下载更新雷管信息后再进行操作")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("退出", (dialog1, which) -> {
                            dialog1.dismiss();
                            finish();
                        })
//                        .setNeutralButton("确定", (dialog12, which) -> dialog12.dismiss())
                        .create();
                dialog.show();
            }
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
        ll_2.setVisibility(View.GONE);

        btn_return = (Button) findViewById(R.id.btn_firing_return_4);
        btn_return.setOnClickListener(v -> {
            closeThread();
            Intent intentTemp = new Intent();
            intentTemp.putExtra("backString", "");
            setResult(1, intentTemp);
            finish();
        });

        btn_return_complete = findViewById(R.id.btn_test_return);

        btn_return_complete.setOnClickListener(v -> {
            closeThread();
            Intent intentTemp = new Intent();
            intentTemp.putExtra("backString", "");
            setResult(1, intentTemp);
            finish();
        });

        btn_firing_lookError_4 = (Button) findViewById(R.id.btn_test_lookError);
        btn_firing_lookError_4.setOnClickListener(v -> {
            loadErrorBlastModel();
//                loadMoreData();
            createDialog();
        });
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
//            Preparation_time = Integer.parseInt(message.get(0).getJiance_time());//跟起爆测试一样
            Preparation_time = Integer.parseInt(message.get(0).getPreparation_time());
            version = message.get(0).getVersion();
            Log.e(TAG, "version: " + version);
        }
    }


    //提示对话框,提示有未注册雷管或者未处理雷管
    public void disPlayNoReisterDenator(final int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestDenatorActivity.this);
        int blastNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
        builder.setTitle(getString(R.string.text_alert_tip));
        builder.setMessage("请检查雷管总数是否正确,确认无误后可忽略本提示");//"总线上有未处理的雷管，是否继续起爆？"
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
        List<DenatorBaseinfo> list = master.queryErrLeiGuan(mRegion);//带参数是查一个区域,不带参数是查所有
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
        List<DenatorBaseinfo> list = master.queryErrLeiGuan(mRegion);//带参数是查一个区域,不带参数是查所有
        for (DenatorBaseinfo d : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("serialNo", d.getBlastserial());
            item.put("shellNo", d.getShellBlastNo());
            item.put("errorName", d.getErrorName());
            item.put("delay", d.getDelay());
            item.put("duanNo", d.getDuanNo());
            errDeData.add(item);
        }
        Log.e(TAG, "errDeData: " + errDeData.toString());
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
        ErrListAdapter mAdapter = new ErrListAdapter(this, errDeData, R.layout.firing_error_item_duan);
        errlistview.setAdapter(mAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_alert_tablename1));//"错误雷管列表"
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    private void endTest() {
        closeThread();
        initParam();
    }


    @Override
    public void sendInterruptCmd() {
        byte[] reCmd = SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00");//22 退出注册模式
        sendCmd(reCmd);
        super.sendInterruptCmd();
    }

    /**
     * 加载数据
     */
    private void loadMoreData() {
        blastQueue.clear();
        errorList.clear();
        denatorlist1.clear();
        denatorlist2.clear();
        List<DenatorBaseinfo> denatorBaseinfos = new GreenDaoMaster().queryDetonatorRegionAsc(mRegion);//不分区域
        denatorlist1 = new ArrayList<>();
        denatorlist2 = new ArrayList<>();
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
            item.setVersion(a.getAuthorization());
            if (a.getAuthorization().equals("02")) {
                denatorlist2.add(item);
            }else {
                denatorlist1.add(item);
            }
        }
        for (VoDenatorBaseInfo b : denatorlist2) {
            blastQueue.offer(b);
            list_lg.add(b);
        }
        for (VoDenatorBaseInfo a : denatorlist1) {
            blastQueue.offer(a);
            list_lg.add(a);
        }
        denatorCount = blastQueue.size();
        Log.e("雷管队列", "denatorCount: " + denatorCount);
        tv_dianliu.setText(denatorCount * ic_cankao + "μA");//参考电流

    }


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
                    String displayIcStr = "" + (int) busInfo.getBusCurrentIa() + "μA";
                    float displayIc = busInfo.getBusCurrentIa();
                    //displayIc =
                    dangqian_ic = busInfo.getBusCurrentIa();
                    ll_firing_Volt_4.setText("" + busInfo.getBusVoltage() + "V");
                    ll_firing_IC_4.setText(displayIcStr);
                    if (displayIc == 0 && firstCount < Preparation_time * 0.4) {
                        ll_firing_IC_4.setTextColor(Color.RED);
                        show_Toast("当前电流为0,请检查线路是否正确连接");
                        stage = 5;
                        Utils.writeRecord("总线电流为0");
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                        return;
                    }
                    if (displayIc < denatorCount * ic_cankao * 0.25 && firstCount < Preparation_time * 0.5) {//总线电流小于参考值一半,可能出现断路
                        ll_firing_IC_4.setTextColor(Color.RED);
                        show_Toast("当前电流过小,请检查线路是否出现断路");
                        stage = 5;
                        Utils.writeRecord("总线电流小于参考值一半,可能出现断路");
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                        return;
                    }
                    if (busInfo.getBusVoltage() < 6 && firstCount < Preparation_time * 0.5) {
                        sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22 退出组网测试
                        ll_firing_Volt_4.setTextColor(Color.RED);
                        show_Toast("当前电压异常,请检查线路是否出现短路等情况");
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                        stage = 5;
                        Utils.writeRecord("电压异常,电压为" + busInfo.getBusVoltage() + "V");
                        return;
                    }
                    //判断电流过大是用的之前的参数,这个后续会改
                    if (displayIc > 9000) {
                        displayIcStr = displayIcStr + "(疑似短路)";
                        ll_firing_IC_4.setTextColor(Color.RED);
                        Utils.writeRecord("--电流:" + displayIcStr + "μA  --电压:" + busInfo.getBusVoltage() + "V,疑似短路");

                    } else if (displayIc > (denatorCount * ic_cankao * 2) && firstCount < Preparation_time * 0.2) {//5
                        Log.e(TAG, "电流过大: ");
                        displayIcStr = displayIcStr + "(电流过大)";
                        ll_firing_IC_4.setTextColor(Color.RED);// "电流过大";
                        ll_firing_IC_4.setTextSize(20);
                        Utils.writeRecord("电流:" + busInfo.getBusCurrentIa() + "μA  --电压:" + busInfo.getBusVoltage() + "V" + ",当前电流过大");
                    }
//                    else if (displayIc < 4 + denatorCount * ic_cankao && firstCount < Preparation_time * 0.2) {//5
//                        displayIcStr = displayIcStr + "(疑似断路)";
//                        ll_firing_IC_4.setTextColor(Color.BLACK);// "疑似断路";
//                        ll_firing_IC_4.setTextSize(20);
//                        Utils.writeRecord("电流:" + busInfo.getBusCurrentIa() + "μA  --电压:" + busInfo.getBusVoltage() + "V" + ",疑似断路");
//                    }
                    else {
                        ll_firing_IC_4.setTextColor(Color.GREEN);
                        Utils.writeRecord("电流:" + busInfo.getBusCurrentIa() + "μA  --电压:" + busInfo.getBusVoltage() + "V" + ",当前电流正常");
                    }
                    ll_firing_IC_4.setText(displayIcStr);


                    //电流大于9000
//                    Log.e(TAG, "displayIc: " + displayIc);
                    if (displayIc > 9000 && firstCount < Preparation_time * 0.2) {
                        stage = 5;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mHandler_1.handleMessage(Message.obtain());
                        sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22 退出组网测试
//                        if (!chongfu) {
//                            initDialog("当前检测到总线电流过大,正在准备重新进行网络检测,请耐心等待。");//弹出框
//                        } else {
                        initDialog_zanting("当前电流过大,请检查线夹等部位是否存在浸水或母线短路等情况,排查处理浸水后,重新进行检测。");//弹出框
//                        }
                        endTest();
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

                if (thirdWriteErrorDenator != null) {//写入未返回的错误雷管
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
                    stopXunHuan();
                } else if (totalerrorNum == denatorCount && busInfo.getBusCurrentIa() > 9000) {//大于9000u ，全错
                    Log.e(TAG, "大于9000u ，全错: ");
                    sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22
//                    if (chongfu) {
                    initDialog_zanting("请检查线夹等部位是否有进水进泥等短路情况,确认无误后再重新检测。");//弹出框
//                    } else {
//                        initDialog("当前有雷管检测错误,系统正在进行2次检测,如果依然检测错误,请检查线夹等部位是否有进水进泥等短路情况,确认无误后点击继续进行检测。");//弹出框
//                    }
                } else if (totalerrorNum == denatorCount && busInfo.getBusCurrentIa() < 9000) {//小于9000u ，全错
                    sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22 退出组网测试
//                    if (chongfu) {
                    initDialog_zanting("请检查线夹等部位是否有进水进泥等短路情况,确认无误后再重新检测。");//弹出框
//                    } else {
//                        initDialog("当前有雷管检测错误,系统正在进行2次检测,如果依然检测错误,请检查线夹等部位是否有进水进泥等短路情况,确认无误后点击继续进行检测。");//弹出框
//                    }

                    Log.e(TAG, "小于9000u ，全错: stage=" + stage);
                } else if (totalerrorNum > 0 && busInfo.getBusCurrentIa() < denatorCount * ic_cankao + 100) {//小于参考值 ，部分错
                    sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22 退出组网测试
//                    if (chongfu) {
//                        initDialog_zanting2("查看错误雷管列表,疑似部分雷管连接线断开,请检查是否存在雷管连接线断开,管壳码输入错误等情况,检查完毕后点击继续按钮进行检测!");//弹出框
//                    } else {
                    initDialog_zanting2("请检查错误的雷管是否存在连接线断开或管壳码输入错误等情况!检查无误后再重新检测。");//弹出框
//                    }
                    Log.e(TAG, "小于参考值 ，部分错: stage=" + stage);
                } else if (totalerrorNum < denatorCount && totalerrorNum != 0 && busInfo.getBusCurrentIa() > (denatorCount * ic_cankao + 100)) {//大于参考值 ，部分错
                    sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22 退出组网测试
                    if (chongfu) {
                        initDialog_zanting2("请更换错误雷管,疑似部分雷管出现进水进泥等情况,检查无误后,再重新检测。");//弹出框
                    } else {
                        initDialog_zanting2("请检查错误的雷管是否存在连接线断开或线夹进水进泥等情况!检查无误后再重新检测。");//弹出框
                    }
                    Log.e(TAG, "大于参考值 ，部分错: stage=" + stage);
                } else if (errtotal > 0 && busInfo.getBusCurrentIa() > (denatorCount * ic_cankao * 0.9) && busInfo.getBusCurrentIa() < (denatorCount * ic_cankao * 1.1)) {
                    initDialog_tip("疑似部分雷管雷管号输入有误,请检查雷管号是否输入正确!");
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
                secondTxt.setText("请检查线路是否正确连接");
                break;

            case 6://等待处理错误时间
                Log.e(TAG, "放电阶段--execStage: " + sixCount);
                secondTxt.setText("正在放电" + sixCount);//"等待总线稳定:"
                break;
            case 7://等待处理错误时间
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
            firstThread.exit = true;  // 终止线程thread
            firstThread.interrupt();
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
        Application.getDaoSession().update(denator);

//        ContentValues values = new ContentValues();
//        values.put("errorName", fromData.getCommicationStatusName());//key为字段名，value为值
//        values.put("errorCode", fromData.getCommicationStatus());//key为字段名，value为值
//        db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "shellBlastNo=?", new String[]{fromData.getShellNo()});
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
                            if (firstCount == Preparation_time) {//经过测试初始化命令需要6秒
                                //切换模块芯片版本
                                if (version.equals("01")) {
                                    sendCmd(FourStatusCmd.send46("00", "01",denatorCount));//20(第一代)
                                } else {
                                    sendCmd(FourStatusCmd.send46("00", "02",denatorCount));//20(第二代)
                                }
                            }
                            Thread.sleep(1000);
                            if (firstCount == Preparation_time) {//经过测试初始化命令需要6秒
                                //进入测试模式
                                byte[] powerCmd = SecondNetTestCmd.setToXbCommon_Testing_Init22_1("00");//20
                                sendCmd(powerCmd);
                            }
                            if (firstCount == 0) {
//                                revOpenCmdTestFlag = 1;//跳转发送测试命令阶段
                                Thread.sleep(1000);//为了发40后等待
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                stage = 3;
                                break;
                            }

                            if (firstCount == Preparation_time - 10) {//Preparation_time-1
                                sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00"));//22
                            }
                            if (firstCount == Preparation_time - 13) {//Preparation_time-1
                                sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启电源指令

                            }
//                            if (firstCount == Preparation_time-14) {//经过测试初始化命令需要6秒
//                                //切换模块芯片版本
//                                if (version.equals("01")) {
//                                    sendCmd(FourStatusCmd.send46("00", "02",denatorCount));//20(第一代)
//                                } else {
//                                    sendCmd(FourStatusCmd.send46("00", "02",denatorCount));//20(第二代)
//                                }
//                            }
                            if (firstCount == Preparation_time - 14) {//Preparation_time-1
                                sendCmd(SecondNetTestCmd.setToXbCommon_Testing_Init22_1("00"));//20 //进入测试模式
                            }
                            if (firstCount < (Preparation_time - 15) && firstCount > 1) {//Preparation_time-1  // && firstCount < Preparation_time - 1
                                sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40
                            }
                            if (firstCount == 1) {//Preparation_time-1  // && firstCount < Preparation_time - 1
                                sendCmd(FourStatusCmd.send46("00", "02",denatorCount));//20(第一代)
                            }

                            firstCount--;
                            if (firstCount == -10) {
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
//                                    exit = true;//结束线程
//                                    byte[] initBuf2 = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_7("00");//36 在网读ID检测
//                                    sendCmd(initBuf2);
                                    stage = 4;
                                    Thread.sleep(500);
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    break;
                                }
                                if (version_1 && thirdWriteCount == denatorlist2.size() && denatorlist1.size() != 0) {
                                    version_1 = false;//发一次就不要发了
                                    sendCmd(FourStatusCmd.send46("00", "01"));//20(第一代)
                                    Thread.sleep(500);
                                    continue;
                                }


                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo = write;
                                Log.e(TAG, "ShellBlastNo: " + write.getShellBlastNo() + " version:" + write.getVersion());

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
                                sendCmd(initBuf);//后面的shellStr没用上
                                thirdStartTime = System.currentTimeMillis();
                                writeDenator = write;
                                thirdWriteCount++;
                                Thread.sleep(50);//
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            } else {
                                long thirdEnd = System.currentTimeMillis();
                                long spanTime = thirdEnd - thirdStartTime;
                                if (spanTime > 3000) {//发出本发雷管时，没返回超时了
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
                                    }
                                    tempBaseInfo = null;
                                    reThirdWriteCount++;
                                } else {
                                    Thread.sleep(20);
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
//        Log.e("返回命令--测试页面", "onDataReceived: "+fromCommad );
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd(fromCommad);
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
            String noReisterFlag = ThreeFiringCmd.getCheckFromXbCommon_FiringExchange_5523_7_reval("00", fromCommad);
            Log.e("是否有未注册雷管", "noReisterFlag: " + noReisterFlag);
            byte[] powerCmd = SecondNetTestCmd.setToXbCommon_Testing_Exit22_3("00");//22
            sendCmd(powerCmd);
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
            if (busHandler_dianliu == null) return;
//            busHandler_dianliu.sendMessage(busHandler_dianliu.obtainMessage());

        } else if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//41 开启总线电源指令
            if (send_kg) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                revOpenCmdReFlag = 1;
                sendOpenThread.exit = true;
                Log.e("开启电源指令", "revOpenCmdReFlag: " + revOpenCmdReFlag);
                stage = 1;
                if (blastQueue.size() > 0 && !chongfu) {
                    firstThread = new ThreadFirst();
                    firstThread.start();
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
    private java.util.Timer mOffTime;
    private android.app.Dialog mDialog;
    private CommonDialog dialog;//自定义dialog


    /**
     * 倒计结束后,重新上电开始检测
     */
    private void off() {
        initParam();//先重置数据
        chongfu = true;//已经检测了一次
        loadMoreData();
        mHandler_1.sendMessage(mHandler_1.obtainMessage());
        byte[] powerCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41
        sendCmd(powerCmd);
        Log.e(TAG, "blastQueue.size(): " + blastQueue.size());
//        if(blastQueue.size() == 0){
//            blastQueue = allBlastQu;
//        }
//        ll_firing_deAmount_4.setText("0");
        ll_firing_errorNum_4.setText("0");
        ll_firing_Volt_4.setText("0V");
        ll_firing_IC_4.setText("0μA");
        ll_firing_errorNum_4.setTextColor(Color.GREEN);
    }

    private void stopXunHuan() {
        endTest();
        ll_1.setVisibility(View.GONE);
        ll_2.setVisibility(View.VISIBLE);
        secondTxt.setText(R.string.text_test_tip4);
        byte[] initBuf2 = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_7("00");//36 在网读ID检测
        sendCmd(initBuf2);
    }

    private void initDialog(String tip) {

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
                .setNegativeButton("退出", (dialog, id) -> {
                    dialog.cancel();
                    mOffTime.cancel();
                    closeThread();
                    closeForm();
                })

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
                off();//关闭后的操作
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
                if (countTime == 118) {
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

    private void initDialog_tip(String tip) {
        AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
                .setTitle("系统提示")//设置对话框的标题//"成功起爆"
                .setMessage(tip)//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
//                .setNeutralButton("继续", (dialog1, which) -> {
//                    off();//重新检测
//                    dialog1.dismiss();
//                })
                .setNegativeButton("退出", (dialog12, which) -> {
                    stopXunHuan();
                })
                .create();
        dialog.show();
    }

    private void initDialog_zanting(String tip) {
        chongfu = true;//已经检测了一次
        AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
                .setTitle("系统提示")//设置对话框的标题//"成功起爆"
                .setMessage(tip)//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
//                .setNeutralButton("继续", (dialog1, which) -> {
//                    off();//重新检测
//                    dialog1.dismiss();
//                })
                .setNegativeButton("退出", (dialog12, which) -> {
                    stopXunHuan();
                })
                .create();
        dialog.show();
    }

    private void initDialog_zanting2(String tip) {
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
//        ListView errlistview = (ListView) getlistview.findViewById(R.id.X_listview);
//        errlistview.setVisibility(View.GONE);
//        SimpleAdapter adapter = new SimpleAdapter(this, errDeData, R.layout.firing_error_item,
//                new String[]{"serialNo", "shellNo", "errorName", "delay"},
//                new int[]{R.id.X_item_no, R.id.X_item_shellno, R.id.X_item_errorname, R.id.X_item_delay});
//        // 给listview加入适配器
//        errlistview.setAdapter(adapter);

        // 给ListView绑定内容
        ListView errlistview = getlistview.findViewById(R.id.X_listview);
        errlistview.setVisibility(View.GONE);
        ErrListAdapter mAdapter = new ErrListAdapter(this, errDeData, R.layout.firing_error_item_duan);
        errlistview.setAdapter(mAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统提示");//"错误雷管列表"
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
            dialogOFF(dialog);
            stopXunHuan();
            dialog.dismiss();


        });
//        builder.setNeutralButton("继续", (dialog, which) -> {
//            dialogOFF(dialog);
//            off();//重新检测
//            dialog.dismiss();
//        });
        builder.setNegativeButton("查看错误雷管", (dialog, which) -> {
//            stopXunHuan();
            llview.setVisibility(View.VISIBLE);
            text_tip.setVisibility(View.GONE);
            errlistview.setVisibility(View.VISIBLE);
            dialogOn(dialog);
        });
        builder.create().show();
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
}
