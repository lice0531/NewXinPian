package android_serialport_api.xingbang.firingdevice;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.cmd.vo.From32DenatorFiring;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.cmd.vo.To52Test;
import android_serialport_api.xingbang.databinding.ActivityFiringPageBinding;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DenatorHis_Detail;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.models.VoDenatorBaseInfo;
import android_serialport_api.xingbang.models.VoFireHisMain;
import android_serialport_api.xingbang.models.VoFiringTestError;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android_serialport_api.xingbang.Application.getDaoSession;

/**
 * @author zenghp
 *         起爆页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FiringMainActivity extends SerialPortActivity {
    private SimpleCursorAdapter adapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private View getlistview;

    private Button btn_return1;
    private Button btn_return2;
    private Button btn_return4;
    private Button btn_return6;
    private Button btn_return7;
    private Button btn_return8;

    private Button btn_continueOk_4;//继续
    private Button btn_pressbt_7;//起爆
    private Button btn_firing_lookError_4;//查看错误
    private Button btn_fir_over;//起爆按键

    private Handler busHandler = null;//总线信息
    private Handler mHandler_tip = null;//提示
    private Handler mHandler_showdialoy = null;//显示弹窗
    private static Handler mHandler_1 = null;//更新视图
    private static Handler noReisterHandler = null;//没有注册的雷管
    private TextView firstTxt;
    private TextView secondTxt;
    private TextView fourTxt;
    private TextView sixTxt;
    private TextView eightTxt;
    private TextView ll_firing_Volt_2;
    private TextView ll_firing_IC_2;
    private TextView ll_firing_Volt_4;
    private TextView ll_firing_IC_4;
    private TextView ll_firing_Volt_5;
    private TextView ll_firing_IC_5;
    private TextView ll_firing_deAmount_4;//雷管数
    private TextView ll_firing_deAmount_2;//雷管数
    private TextView ll_firing_errorAmount_4;//错误数
    private TextView ll_firing_errorAmount_2;//错误数
    private TextView tv__qb_dianliu_1;//参考电流
    private TextView tv__qb_dianliu_2;//参考电流
    private TextView ll_firing_Volt_6;
    private TextView ll_firing_IC_6;
    private TextView ll_firing_Volt_7;
    private TextView ll_firing_IC_7;
    private TextView ll_firing_Hv_7;
    private TextView ll_firing_Hv_6;
    private To52Test writeVo;
    private static volatile int stage;//
    private static volatile int startFlag = 0;
    private volatile int zeroCount = 0;//其实阶段计数器，发出关闭电源指令时间
    private volatile int zeroCmdReFlag = 0;//第0阶段结束标志 为1时0阶段结束
    private volatile int firstWaitCount = 9;
    private volatile int Wait_Count = 5;
    private volatile int firstCmdReFlag = 0;//发出打开电源命令是否返回
    private volatile int secondCount = 0;//第二阶段 计时器
    private volatile int secondCmdFlag = 0;//发出进入起爆模式命令是否返回
    private volatile int fourthDisplay = 0;//第4步，是否显示
    private volatile int thirdWriteCount;//雷管发送计数器
    private volatile int sevenDisplay = 0;//第7步，是否显示
    private volatile int sixExchangeCount = 280;//第6阶段计时
    private volatile int sixCmdSerial = 1;//命令倒计时
    private volatile int eightCount = 5;//第8阶段
    private volatile int eightCmdFlag = 0;//第八阶段命令发出起爆
    private volatile int qibaoNoFlag = 1;//第八阶段命令发出起爆
    private volatile int eightCmdExchangePower = 0;//切换电源命令
    private volatile int neightCount = 0;//
    private long thirdStartTime = 0;//第三阶段每个雷管返回命令计时器
    private String userId = "";

    private volatile int revPowerFlag = 0;
    private volatile int reThirdWriteCount = 0;//当芯片返回命令时,数量加一,用以防止上一条命令未返回,
    private VoFiringTestError thirdWriteErrorDenator;//写入错误雷管
    private int fourOnlineDenatorFlag = -1;//是否存在未注册雷管 1:36命令未返回 2:存在 3:不存在
    private int twoErrorDenatorFlag = 0;//错误雷管
    private int denatorCount = 0;//雷管总数
    private ThreadFirst firstThread;
    //    private Thread2 thread2;
    private GetBusInfoThread busInfoThread;
    private From42Power busInfo;
    private LinearLayout ll_1;
    private LinearLayout ll_2;
    private LinearLayout ll_4;
    private LinearLayout ll_6;
    private LinearLayout ll_7;
    private LinearLayout ll_8;
    private static VoDenatorBaseInfo writeDenator;
    private ConcurrentLinkedQueue<VoDenatorBaseInfo> allBlastQu;//雷管队列
    private ConcurrentLinkedQueue<VoFiringTestError> errorList;//错误雷管队列
    private List<VoFireHisMain> denatorHis_Main_list = new ArrayList<>();//起爆雷管历史集合
    int m6KeyDown_Action, m6KeyUp_Action;
    int m0KeyDown_Action, m0KeyUp_Action;
    long m0UpTime = 0;
    long m5DownTime = 0;
    int keyFlag = 0;
    int keyFireCmd = 0;
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private int Preparation_time;//准备时间
    private int ChongDian_time;//充电时间
    private int JianCe_time;//准备时间
    private String qiaosi_set = "";//是否检测桥丝
    private String hisInsertFireDate;
    private ArrayList<Map<String, Object>> errDeData = new ArrayList<Map<String, Object>>();//错误雷管
    ArrayList<Map<String, Object>> hisListData = new ArrayList<Map<String, Object>>();//起爆雷管
    private String qbxm_id = "-1";
    private int isshow = 0;
    private float cankao_ic = 0;

    private List<VoDenatorBaseInfo> list = new ArrayList<>();
    private ActivityFiringPageBinding inflate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityFiringPageBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        ButterKnife.bind(this);

        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 21);
        db = mMyDatabaseHelper.getWritableDatabase();
        getUserMessage();//获取用户信息
        initParam();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        qbxm_id = (String) bundle.get("qbxm_id");
        if (qbxm_id == null) {
            qbxm_id = "-1";
        }
        Utils.writeLog("起爆页面-qbxm_id:" + qbxm_id);
        startFlag = 1;
        initView();
        initHandle();
        loadBlastModel();
        ctlLinePanel(1);//初始化页面
        firstThread = new ThreadFirst(allBlastQu);//全部线程
//        thread2 = new Thread2(allBlastQu);//31指令线程
    }

    private void initView() {

        ll_1 = (LinearLayout) findViewById(R.id.ll_firing_1);
        ll_2 = (LinearLayout) findViewById(R.id.ll_firing_2);
        ll_4 = (LinearLayout) findViewById(R.id.ll_firing_4);
        ll_6 = (LinearLayout) findViewById(R.id.ll_firing_6);
        ll_7 = (LinearLayout) findViewById(R.id.ll_firing_7);
        ll_8 = (LinearLayout) findViewById(R.id.ll_firing_8);
        firstTxt = (TextView) findViewById(R.id.ll_waiting_txt_firing_1);
        secondTxt = (TextView) findViewById(R.id.ll_txt_firing_2);
        fourTxt = (TextView) findViewById(R.id.ll_txt_firing_4);
        sixTxt = (TextView) findViewById(R.id.ll_txt_firing_6);
        eightTxt = (TextView) findViewById(R.id.ll_txt_firing_8);

        firstTxt.setText("" + firstWaitCount);
        ll_firing_Volt_2 = (TextView) findViewById(R.id.ll_firing_Volt_2);
        ll_firing_IC_2 = (TextView) findViewById(R.id.ll_firing_IC_2);
        ll_firing_Volt_4 = (TextView) findViewById(R.id.ll_firing_Volt_4);
        ll_firing_IC_4 = (TextView) findViewById(R.id.ll_firing_IC_4);
        ll_firing_Volt_5 = (TextView) findViewById(R.id.ll_firing_Volt_5);
        ll_firing_IC_5 = (TextView) findViewById(R.id.ll_firing_IC_5);
        ll_firing_deAmount_4 = (TextView) findViewById(R.id.ll_firing_deAmount_4);//雷管数
        ll_firing_deAmount_2 = (TextView) findViewById(R.id.ll_firing_deAmount_2);//雷管数
        ll_firing_errorAmount_2 = (TextView) findViewById(R.id.ll_firing_errorAmount_2);//错误数
        ll_firing_errorAmount_4 = (TextView) findViewById(R.id.ll_firing_errorAmount_4);//错误数
        tv__qb_dianliu_1 = (TextView) findViewById(R.id.tv__qb_dianliu_1);//错误数
        tv__qb_dianliu_2 = (TextView) findViewById(R.id.tv__qb_dianliu_2);//错误数
        ll_firing_Volt_6 = (TextView) findViewById(R.id.ll_firing_Volt_6);
        ll_firing_IC_6 = (TextView) findViewById(R.id.ll_firing_IC_6);
        ll_firing_Volt_7 = (TextView) findViewById(R.id.ll_firing_Volt_7);
        ll_firing_IC_7 = (TextView) findViewById(R.id.ll_firing_IC_7);
        ll_firing_Hv_7 = (TextView) findViewById(R.id.ll_firing_Hv_7);//起爆电压
        ll_firing_Hv_6 = (TextView) findViewById(R.id.ll_firing_Hv_6);//起爆电压

        btn_fir_over = (Button) findViewById(R.id.btn_fir);//起爆电压
        btn_fir_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyFireCmd = 1;
            }
        });
        // threadTest = new ThreadTest();
        //busInfoThread = new GetBusInfoThread();
        btn_return1 = (Button) findViewById(R.id.btn_firing_return_1);
        btn_return1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThread();
                closeForm();
                // mHandler_1.sendMessage(mHandler_1.obtainMessage());
            }
        });
        btn_return2 = (Button) findViewById(R.id.btn_firing_return_2);
        btn_return2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThread();
                closeForm();
            }
        });
        btn_return4 = (Button) findViewById(R.id.btn_firing_return_4);
        btn_return4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThread();
                closeForm();
            }
        });
        btn_return6 = (Button) findViewById(R.id.btn_firing_return_6);
        btn_return6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThread();
                closeForm();
            }
        });
        btn_return7 = (Button) findViewById(R.id.btn_firing_return_7);
        btn_return7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThread();
                closeForm();
            }
        });
        btn_return8 = (Button) findViewById(R.id.btn_firing_return_8);
        btn_return8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThread();
                closeForm();
            }
        });
        btn_firing_lookError_4 = (Button) findViewById(R.id.btn_test_lookError);
        btn_firing_lookError_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadErrorBlastModel();
                createDialog();
            }
        });
        //继续起爆
        btn_continueOk_4 = (Button) findViewById(R.id.btn_firing_continue_4);
        btn_continueOk_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase(6);
            }
        });
    }

    private void initHandle() {
        //接受消息之后更新imageview视图
        mHandler_1 = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                execStage(msg);
            }
        };
        mHandler_tip = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String time = (String) msg.obj;
                show_Toast("起爆记录条数最大30条,已删除" + time + "记录");
            }
        };
        mHandler_showdialoy= new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                firstThread.exit = true;
                firstThread.interrupt();
                sixExchangeCount--;
                try {
                    firstThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AlertDialog dialog = new Builder(FiringMainActivity.this)
                        .setTitle("总线通讯失败")//设置对话框的标题//"成功起爆"
                        .setMessage("总线通讯失败,请检查线路后再次启动起爆流程")//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                                sendCmd(reCmd);
                                dialog.dismiss();
                                closeThread();
                                closeForm();
                                finish();

                            }
                        })
//                        .setNeutralButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int i) {
//                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                                firstThread=new ThreadFirst(allBlastQu);
//                                firstThread.exit = false;
//                                firstThread.start();
//                                dialog.dismiss();
//                            }
//                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                dialog.show();
                setDialogTextColor(dialog,Color.RED);


            }
        };
        noReisterHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (fourOnlineDenatorFlag == 2) {
                    disPlayNoReisterDenator(0);//提示框
                    Log.e("未注册雷管", "线上有未注册雷管弹出框");
                }
                if (twoErrorDenatorFlag == 1) {
                    twoErrorDenatorFlag = 0;
                    String err = ll_firing_errorAmount_2.getText().toString();
                    if (err.length() < 1) err = "0";
                    ll_firing_errorAmount_2.setText("" + (Integer.parseInt(err) + 1));
                    ll_firing_errorAmount_2.setTextColor(Color.RED);
                }


            }
        };

        busHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (busInfo != null) {
                    ll_firing_Volt_2.setText("" + busInfo.getBusVoltage() + "V");
                    float f = busInfo.getBusCurrentIa() * 1000;
                    BigDecimal b = new BigDecimal(f);//处理大额数据专用类
                    String displayIcStr = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() + "μA";//保留两位小数
//                    Log.e("电源信息", "电压: "+ busInfo.getBusVoltage());
//                    Log.e("电源信息", "电流: "+ displayIcStr);
                    float displayIc = busInfo.getBusCurrentIa();
                    //displayIc =
                    if (displayIc * 1000 > (denatorCount * 40 * 2)) {// "电流过大";
                        displayIcStr = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() + "μA" + "(电流过大)";
                        ll_firing_IC_2.setTextColor(Color.RED);
                        ll_firing_IC_4.setTextColor(Color.RED);
                        ll_firing_IC_5.setTextColor(Color.RED);
                        ll_firing_IC_6.setTextColor(Color.RED);
                        ll_firing_IC_7.setTextColor(Color.RED);
                    } else {
                        ll_firing_IC_2.setTextColor(Color.GREEN);
                        ll_firing_IC_4.setTextColor(Color.GREEN);
                        ll_firing_IC_5.setTextColor(Color.GREEN);
                        ll_firing_IC_6.setTextColor(Color.GREEN);
                        ll_firing_IC_7.setTextColor(Color.GREEN);
                    }
                    ll_firing_IC_2.setText("" + displayIcStr);
                    ll_firing_Volt_4.setText("" + busInfo.getBusVoltage() + "V");
                    ll_firing_Volt_5.setText("" + busInfo.getBusVoltage() + "V");
                    ll_firing_IC_4.setText("" + displayIcStr);
                    ll_firing_IC_5.setText("" + displayIcStr);
                    ll_firing_Volt_6.setText("" + busInfo.getBusVoltage() + "V");
                    ll_firing_IC_6.setText("" + displayIcStr);
                    ll_firing_Volt_7.setText("" + busInfo.getBusVoltage() + "V");
                    ll_firing_IC_7.setText("" + displayIcStr);
                    ll_firing_Hv_7.setText("" + busInfo.getFiringVoltage() + "V");
                    ll_firing_Hv_6.setText("" + busInfo.getFiringVoltage() + "V");
                    if (busInfo.getPowerStatusName().trim().length() > 0) {
                        /**
                         Toast.makeText(FiringMainActivity.this,busInfo.getPowerStatusName(),
                         Toast.LENGTH_SHORT).show();
                         **/
                    }
                }
//                assert busInfo != null;
                if (sixExchangeCount == 100 && busInfo.getBusVoltage() < 15) {
                    Log.e("总线电压", "busInfo.getBusVoltage()" + busInfo.getBusVoltage());
                    AlertDialog dialog = new Builder(FiringMainActivity.this)
                            .setTitle("高压充电失败")//设置对话框的标题//"成功起爆"
                            .setMessage("起爆器高压充电失败,请再次启动起爆流程,进行起爆")//设置对话框的内容"本次任务成功起爆！"
                            //设置对话框的按钮
                            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    closeThread();
                                    closeForm();
                                    finish();
                                }
                            })
                            .create();
                    dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                    dialog.show();
                }
                if (eightCount > 1 && busInfo.getBusVoltage() < 6.3) {
                    closeThread();
                    AlertDialog dialog = new Builder(FiringMainActivity.this)
                            .setTitle("总线电压过低")//设置对话框的标题//"成功起爆"
                            .setMessage("当前起爆器电压异常,可能会导致总线短路,请检查线路后再次启动起爆流程,进行起爆")//设置对话框的内容"本次任务成功起爆！"
                            //设置对话框的按钮
                            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                                    sendCmd(reCmd);
                                    dialog.dismiss();
//                                    closeThread();
                                    closeForm();
                                    finish();
                                }
                            })
                            .create();
                    dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                    dialog.show();
                }
                /**
                 * 检测电流小于参考值的80%提示弹框
                 * */
                if (stage == 6 && busInfo.getBusCurrentIa() * 1000 <= cankao_ic * 0.8 && isshow == 0) {
                    isshow = 1;
                    firstThread.exit = true;
                    firstThread.interrupt();
                    try {
                        firstThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    AlertDialog dialog = new Builder(FiringMainActivity.this)
                            .setTitle("总线电流偏低")//设置对话框的标题//"成功起爆"
                            .setMessage("当前起爆器电流异常,可能是总线短路导致,请检查线路后再次启动起爆流程,进行起爆")//设置对话框的内容"本次任务成功起爆！"
                            //设置对话框的按钮
                            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                                    sendCmd(reCmd);
                                    dialog.dismiss();
                                    closeThread();
                                    closeForm();
                                    finish();

                                }
                            })
                            .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    firstThread=new ThreadFirst(allBlastQu);
                                    firstThread.exit = false;
                                    firstThread.start();
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                    dialog.show();

                }
                if(stage==2){
                    cankao_ic = busInfo.getBusCurrentIa() * 1000;//记录参考电流
//                    Log.e(TAG, "记录的参考电流cankao_ic: "+cankao_ic );
                }

                busInfo = null;
            }
        };
    }

    private void setDialogTextColor(AlertDialog dialog, int red){
        try {
            //获取mAlert对象
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(dialog);
            //获取mMessageView并设置大小颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextColor(red);
            mMessageView.setTextSize(30);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /***
     * 初始化变量
     */
    private void initParam() {
        FiringMainActivity.stage = 0;
        writeVo = null;
        firstWaitCount = 9;
        Wait_Count = JianCe_time;//充电检测时间
        firstCmdReFlag = 0;
        secondCmdFlag = 0;
        zeroCount = 0;
        zeroCmdReFlag = 0;
        secondCount = Preparation_time;//第二阶段 计时器
        fourthDisplay = 0;//第4步，是否显示
        thirdWriteCount = 0;//雷管发送计数器
        sevenDisplay = 0;//第7步，是否显示
        sixExchangeCount = ChongDian_time*10;//第6阶段计时(充电时间)
        sixCmdSerial = 1;//命令倒计时
        eightCount = 5;//第8阶段
        neightCount = 0;//
        eightCmdFlag = 0;
        thirdStartTime = 0;//第三阶段每个雷管返回命令计时器
        isshow = 0;//弹窗标志
    }

    private void getUserMessage() {
        String selection = "id = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"1"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_MESSQGE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {  //cursor不位空,可以移动到第一行
            pro_bprysfz = cursor.getString(1);
            pro_htid = cursor.getString(2);
            pro_xmbh = cursor.getString(3);
            equ_no = cursor.getString(4);
            pro_coordxy = cursor.getString(5);
            qiaosi_set = cursor.getString(10);
            Preparation_time = Integer.parseInt(cursor.getString(11));
            ChongDian_time = Integer.parseInt(cursor.getString(12));
            pro_dwdm = cursor.getString(15);
            JianCe_time = Integer.parseInt(cursor.getString(16));// java.lang.NumberFormatException: Invalid int: "null"
            cursor.close();
        }
        Log.e("Preparation_time", Preparation_time + "");
        Log.e("ChongDian_time", ChongDian_time + "");
        Log.e("JianCe_time", JianCe_time + "");
    }


    //提示对话框,提示有未注册雷管或者未处理雷管
    public void disPlayNoReisterDenator(final int flag) {
        Builder builder = new Builder(FiringMainActivity.this);
        builder.setTitle(getString(R.string.text_alert_tip));
        builder.setMessage(getString(R.string.text_alert_tip2));//"总线上有未处理的雷管，是否继续起爆？"
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (flag == 1) {
                } else {
                    fourOnlineDenatorFlag = 3;
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                closeThread();
                closeForm();
            }
        });
        builder.show();
        if (flag == 1) {
            increase(6);//第六阶段
        } else {
            fourOnlineDenatorFlag = 3;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeThread();
            closeForm();
            return true;
        }
        return true;
    }

    /***
     * 得到错误雷管数
     */
    private void getErrorBlastCount() {
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where  statusCode=? and errorCode<> ?";
        Cursor cursor = db.rawQuery(sql, new String[]{"02", "FF"});
        int totalNum = cursor.getCount();//得到数据的总条数
        cursor.close();
        ll_firing_errorAmount_4.setText("" + totalNum);
        if (totalNum != 0) {
            ll_firing_errorAmount_4.setTextColor(Color.RED);
        } else {
            ll_firing_errorAmount_4.setTextColor(Color.GREEN);
        }

    }

    /***
     * 关闭表单
     */
    private void closeForm() {
        mHandler_1.removeMessages(0);
        startFlag = 0;
        increase(0);
        initParam();
        Intent intentTemp = new Intent();
        intentTemp.putExtra("backString", "");
        setResult(1, intentTemp);
        finish();
        //android.os.Process.killProcess((int)Thread.currentThread().getId());
    }

    /**
     * 关闭守护线程
     */
    private void closeThread() {
        if (firstThread != null) {
            firstThread.exit = true;  // 终止线程thread
            firstThread.interrupt();
        }
//        if (thread2 != null) {
//            thread2.exit = true;  // 终止线程thread
//            thread2.interrupt();
//        }
        if (busInfoThread != null) {
            busInfoThread.exit = true;  // 终止线程thread
            busInfoThread.interrupt();
        }

    }

    /***
     * 控制页面
     * @param stage
     */
    private void ctlLinePanel(int stage) {
        ll_1.setVisibility(View.GONE);
        ll_2.setVisibility(View.GONE);
        ll_4.setVisibility(View.GONE);//是否进入充电检测
        ll_6.setVisibility(View.GONE);
        ll_7.setVisibility(View.GONE);
        ll_8.setVisibility(View.GONE);
        switch (stage) {
            case 1:
                ll_1.setVisibility(View.VISIBLE);
                break;
            case 2:
                ll_2.setVisibility(View.VISIBLE);
                break;
            case 3:
                ll_2.setVisibility(View.VISIBLE);
                break;
            case 4:
                ll_4.setVisibility(View.VISIBLE);
                break;
            case 5:
                ll_4.setVisibility(View.VISIBLE);
                break;
            case 6:
                ll_6.setVisibility(View.VISIBLE);
                break;
            case 7:
                ll_7.setVisibility(View.VISIBLE);
                break;
            case 8:
                ll_8.setVisibility(View.VISIBLE);
                break;
            case 10:
                ll_4.setVisibility(View.VISIBLE);
                fourTxt.setText("当前电流为0,请检查线路是否正确连接");
                Log.e("流程", "10: ");
                break;
        }
    }

    /***
     * 加载错误雷管
     */
    private void loadErrorBlastModel() {
        errDeData.clear();
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryErrLeiGuan();
        for (DenatorBaseinfo d : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("serialNo", d.getBlastserial());
            item.put("shellNo", d.getShellBlastNo());
            item.put("errorName", d.getErrorName());
            item.put("delay", d.getDelay());
            errDeData.add(item);
        }
        Log.e(TAG, "errDeData: " + errDeData.toString());
    }

    /***
     * 建立错误对话框
     */
    public void createDialog() {
        LayoutInflater inflater = LayoutInflater.from(FiringMainActivity.this);
        getlistview = inflater.inflate(R.layout.firing_error_listview, null);
        // 给ListView绑定内容
        ListView listview = (ListView) getlistview.findViewById(R.id.X_listview);
        SimpleAdapter adapter = new SimpleAdapter(this, errDeData, R.layout.firing_error_item,
                new String[]{"serialNo", "shellNo", "errorName", "delay"},
                new int[]{R.id.X_item_no, R.id.X_item_shellno, R.id.X_item_errorname, R.id.X_item_delay});
        // 给listview加入适配器
        listview.setAdapter(adapter);
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.text_alert_tablename1));//错误雷管列表
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /***
     * 加载雷管信息
     */
    private void loadBlastModel() {

        allBlastQu = new ConcurrentLinkedQueue<>();
        errorList = new ConcurrentLinkedQueue<>();
        //int count=0;
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, "statusCode=?", new String[]{"02"}, null, null, " blastserial asc");

        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {

                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                String shellNo = cursor.getString(3);//管壳号
                short delay = cursor.getShort(5);//获取第三列的值
                VoDenatorBaseInfo vo = new VoDenatorBaseInfo();
                vo.setBlastserial(serialNo);

                vo.setDelay(delay);
                vo.setShellBlastNo(shellNo);
                allBlastQu.offer(vo);
                list.add(vo);
            }
            cursor.close();
        }
        denatorCount = allBlastQu.size();

        ll_firing_deAmount_4.setText("" + allBlastQu.size());
        ll_firing_deAmount_2.setText("" + allBlastQu.size());
        tv__qb_dianliu_1.setText(denatorCount * 35 + "μA");
        tv__qb_dianliu_2.setText(denatorCount * 35 + "μA");
    }

    public synchronized void increase(int val) {
        stage = val;
    }

    public void execStage(Message msg) {//页面切换
        switch (stage) {
            case 0:
                if (startFlag == 1) {
                }
                break;
            case 1:
                firstTxt.setText("" + firstWaitCount);

                if (firstWaitCount <= 0) {//等待结束
                    if (firstCmdReFlag == 1) {
                        //发出进入起爆模式命令,根据偏好设置,选择是否检测桥丝
                        //没有桥丝串口返回命令: C000300009C9C0
                        //  有桥丝串口返回命令: C000300009C9C0
                        if (qiaosi_set.equals("true")) {//0101,起爆检测桥丝有问题,先改成不检测桥丝
                            byte[] initBuf = ThreeFiringCmd.setToXbCommon_Firing_Init23_2("0101");//30指令进入起爆模式(同时检测桥丝)
                            sendCmd(initBuf);
                        } else {
                            byte[] initBuf = ThreeFiringCmd.setToXbCommon_Firing_Init23_2("0100");//30指令
                            sendCmd(initBuf);
                        }
                        ctlLinePanel(2);
                        increase(2);
                        Log.e("increase", "2");
                    }
                }


                break;
            case 2:
                secondTxt.setText(getString(R.string.text_firing_tip7) + secondCount + ")");//"测试准备 ("
//                Log.e(TAG, "secondCount: "+secondCount );
                break;
            case 3:
                if (thirdWriteErrorDenator != null) {//写入未返回的错误雷管
                    show_Toast(thirdWriteErrorDenator.getShellBlastNo() + "芯片写入命令未返回");
                    thirdWriteErrorDenator = null;//设置错误雷管
                }
                if (errorList != null && errorList.size() >= 0) {
                    int errLoop = 0;
                    while (!errorList.isEmpty()) {//写入错误雷管
                        VoFiringTestError er = errorList.poll();
                        if (er != null) {
                            From32DenatorFiring df = new From32DenatorFiring();
                            df.setShellNo(er.getShellBlastNo());
                            df.setCommicationStatus("AF");
                            df.setDelayTime(er.getDelay());
                            this.updateDenator(df, er.getDelay());
                        }
                    }
                }
                secondTxt.setText(getString(R.string.text_firing_tip9) + thirdWriteCount + getString(R.string.text_firing_tip10));
                //写入通信未返回

                break;
            case 4:
                ctlLinePanel(4);//修改页面显示项
                getErrorBlastCount();
                fourthDisplay = 1;
                //disPlayNoReisterDenator();
                break;
            case 5:
                secondTxt.setText("雷管测试准备(" + Wait_Count + "s)");//"充电检测 ("
                if (Wait_Count <= 0) {//等待结束
//                    byte[] powerCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//00400101
//                    sendCmd(powerCmd);
                    secondCmdFlag = 1;
                    thirdWriteCount = 0;
                    increase(3);
                    Log.e("increase", "3");
                }
                break;
            case 6://
                fourthDisplay = 0;
                ctlLinePanel(6);
                if(sixExchangeCount%10==0||sixExchangeCount==0){
                    sixTxt.setText(getString(R.string.text_firing_tip4) + sixExchangeCount/10 + ") ");//"正在充电，请稍后 \n"
                }


                if (sixExchangeCount == 8 && sixCmdSerial == 1) {
                    /**
                     Toast.makeText(FiringMainActivity.this, "发出充电命令未返回",
                     Toast.LENGTH_LONG).show();
                     **/
                }
                if (sixExchangeCount == 23 && sixCmdSerial == 2) {
                    /**
                     Toast.makeText(FiringMainActivity.this, "发出高压输出命令未返回",
                     Toast.LENGTH_LONG).show();
                     **/
                }
                if (sixExchangeCount == 30 && sixCmdSerial == 3) {
                    /**
                     Toast.makeText(FiringMainActivity.this, "发出切换电源命令未返回",
                     Toast.LENGTH_LONG).show();
                     **/
                }


                if (sixExchangeCount == -1) {
                    AlertDialog dialog = new Builder(this)
                            .setTitle("高压充电失败")//设置对话框的标题//"成功起爆"
                            .setMessage("起爆器高压充电失败,请再次启动起爆流程,进行起爆")//设置对话框的内容"本次任务成功起爆！"
                            //设置对话框的按钮
                            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    closeThread();
                                    closeForm();
                                    finish();
                                }
                            })
                            .create();
                    dialog.show();
                }
                break;
            case 7:
                ctlLinePanel(7);
                break;
            case 8:
                ctlLinePanel(8);
                eightTxt.setText(getString(R.string.text_firing_tip13) + eightCount + "");//"倒计时\n"
                if (eightCount <= -5) {
                    /**
                     Toast.makeText(FiringMainActivity.this, "发出起爆命令未返回",
                     Toast.LENGTH_LONG).show();
                     **/
                }
                if (eightCount == 0 && eightCmdExchangePower == 0) {
                    /**
                     Toast.makeText(FiringMainActivity.this, "起爆前，切换电源未返回命令",
                     Toast.LENGTH_LONG).show();
                     **/
                }
                break;
            case 9://起爆之后,弹出对话框
                eightTxt.setText("起爆成功!");//"起爆成功！"
                if (eightCmdFlag == 2) {
                    eightCmdFlag = 0;

                    AlertDialog dialog = new Builder(this)
                            .setTitle(getString(R.string.text_firing_tip15))//设置对话框的标题//"成功起爆"
                            .setMessage(getString(R.string.text_firing_tip16))//设置对话框的内容"本次任务成功起爆！"
                            //设置对话框的按钮
                            .setNegativeButton(getString(R.string.text_test_exit), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setPositiveButton(getString(R.string.text_firing_tip17), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(FiringMainActivity.this, QueryHisDetail.class);
                                    startActivityForResult(intent, 1);
                                    dialog.dismiss();
                                    closeThread();
                                    closeForm();
                                }
                            }).create();
                    dialog.show();
                }
                break;
            case 10:
                ctlLinePanel(4);//修改页面显示项
                getErrorBlastCount();
                fourthDisplay = 1;
                break;
            default:

        }

    }

    public int modifyUploadStatus(String id, String delay) {
        ContentValues values = new ContentValues();
        values.put("uploadStatus", delay);
        db.update(DatabaseHelper.TABLE_NAME_HISMAIN, values, "blastdate=?", new String[]{"" + id});
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
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

    private String TAG = "起爆页面";

    /**
     * 保存起爆数据
     */
    public synchronized void saveFireResult() {
        int totalNum = (int) getDaoSession().getDenatorBaseinfoDao().count();//得到数据的总条数
        Log.e(TAG, "saveFireResult-雷管总数totalNum: " + totalNum);
        if (totalNum < 1) return;
        //如果总数大于30,删除第一个数据
        int hisTotalNum = (int) getDaoSession().getDenatorHis_MainDao().count();//得到雷管表数据的总条数
        Log.e(TAG, "saveFireResult-雷管总数hisTotalNum: " + hisTotalNum);
        if (hisTotalNum > 30) {
            String time = loadHisMainData();
            Message message = new Message();
            message.obj = time;
            mHandler_tip.sendMessage(message);
            delHisInfo(time);
        }
        String xy[] = pro_coordxy.split(",");//经纬度
        int maxNo = getHisMaxNumberNo();
        maxNo++;
        String fireDate = hisInsertFireDate;//Utils.getDateFormatToFileName();
        DenatorHis_Main his = new DenatorHis_Main();
        his.setBlastdate(fireDate);
        his.setUploadStatus("未上传");
        his.setRemark("已起爆");
        his.setUserid(userId);
        his.setEqu_no(equ_no);
        his.setSerialNo(maxNo);
        his.setPro_htid(pro_htid);
        his.setPro_xmbh(pro_xmbh);
        his.setPro_dwdm(pro_dwdm);
        if (pro_coordxy.length() > 4) {
            his.setLongitude(xy[0]);
            his.setLatitude(xy[1]);
        }
        getDaoSession().getDenatorHis_MainDao().insert(his);//插入起爆历史记录主表

        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao().loadAll();
        for (DenatorBaseinfo dbf : list) {
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
            getDaoSession().getDenatorHis_DetailDao().insert(denatorHis_detail);//插入起爆历史雷管记录表
        }

        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 保存数据到所有表
     */
    public synchronized void saveFireResult_All() {
        Cursor cursor = db.rawQuery("Select * from denatorBaseinfo_all", null);
        int totalNum = cursor.getCount();//得到数据的总条数
        if (totalNum < 1) return;
        //如果总数大于30,删除第一个数据
        cursor = db.rawQuery("Select * from denatorHis_Main_all", null);
        int hisTotalNum = cursor.getCount();//得到雷管表数据的总条数
        if (hisTotalNum > 30) {
            delHisInfo_all(loadHisMainData());
        }
        Log.e("起爆页面--保存数据2", "totalNum: " + totalNum);
        ContentValues values = new ContentValues();
        int maxNo = getHisMaxNumberNo();
        maxNo++;
        String fireDate = hisInsertFireDate;//Utils.getDateFormatToFileName();
        values.put("blastdate", fireDate);
        values.put("uploadStatus", "未上传");
        values.put("longitude", "0");
        values.put("latitude", "0");
        values.put("remark", "已起爆");
        values.put("userid", userId);
        values.put("firedNo", equ_no);
        values.put("serialNo", "" + maxNo);
        db.insert("denatorHis_Main_all", null, values);

        cursor = db.query("denatorBaseinfo_all", null, null, null, null, null, " blastserial asc");
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                values.clear();
                values.put("blastserial", cursor.getInt(1));
                values.put("sithole", cursor.getInt(2));
                values.put("shellBlastNo", cursor.getString(3));
                values.put("denatorId", cursor.getString(4));
                values.put("delay", cursor.getInt(5));
                values.put("statusCode", cursor.getString(6));
                values.put("statusName", cursor.getString(7));
                values.put("errorName", cursor.getString(8));
                values.put("errorCode", cursor.getString(9));
                values.put("authorization", cursor.getString(10));
                values.put("remark", "未注册");
                values.put("blastdate", fireDate);
                db.insert("denatorHis_Detail_all", null, values);
            }
            cursor.close();
        }
        Utils.saveFile();//把软存中的数据存入磁盘中
//        db.delete("denatorBaseinfo_all", null, null);//3.5后起爆后不清除数据
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


    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Log.e("发送命令", str);
//                Utils.writeLog("发送命令:" + str);
//                Utils.writeLog("Firing sendTo:" + str);
                mOutputStream.write(mBuffer);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    public void updateDenator(From32DenatorFiring fromData, int writeDelay) {
        if (fromData.getShellNo() == null || fromData.getShellNo().trim().length() < 1) return;
        ContentValues values = new ContentValues();

        values.put("errorName", fromData.getCommicationStatusName(this));//key为字段名，value为值
        values.put("errorCode", fromData.getCommicationStatus());//key为字段名，value为值
        //values.put("statusCode", fromData.getDenatorStatus());//key为字段名，value为值
        //values.put("statusName", fromData.getDenatorStatusName());//key为字段名，value为值

        db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "shellBlastNo=?", new String[]{fromData.getShellNo()});
        //判断雷管状态是否是错误和延时和写入的是否一致
        if (!"FF".equals(fromData.getCommicationStatus()) || (writeDelay != fromData.getDelayTime())) {
            twoErrorDenatorFlag = 1;
            noReisterHandler.sendMessage(noReisterHandler.obtainMessage());
        } else if ("02".equals(fromData.getCommicationStatus())) {
            show_Toast(getString(R.string.text_error_tip51));//桥丝检测不正常
        }
        Utils.writeLog("返回延时:" + "管码" + fromData.getShellNo() + "-返回延时" + fromData.getDelayTime() + "-写入延时" + writeDelay);
//        Log.e("写入延时时间", "返回延时" +fromData.getDelayTime());
//        Log.e("写入延时时间", "写入延时" +writeDelay);
        //db.close();
    }

    public void displayInputKeyboard(View v, boolean hasFocus) {
        //获取系统 IMM
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!hasFocus) {
            //隐藏 软键盘  
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } else {
            //显示 软键盘  
            imm.showSoftInput(v, 0);
        }
    }


    @Override
    protected void onStart() {
        /***
         * 发送初始化命令
         */
        //	byte[] initBuf = FiveTestingCmd.setToXbCommon_InCheckModel_Init25("00");
        //sendCmd(initBuf);
        if (!firstThread.isAlive())
            firstThread.start();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (db != null) db.close();

//        Utils.saveFile();//把软存中的数据存入磁盘中
        closeThread();
        closeForm();
        super.onDestroy();
        fixInputMethodManagerLeak(this);
        removeActivity();
    }

    @Override
    public void sendInterruptCmd() {
        byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35
        sendCmd(reCmd);
        super.sendInterruptCmd();
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {

        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
//            Utils.writeLog("Firing reFrom:" + fromCommad);
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd(fromCommad);
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                    doWithReceivData(cmd, localBuf, localSize);//处理cmd命令

                }
            }
        }
    }

    /***
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf, int size) {
        byte[] locatBuf = new byte[size];//命令字节数组
        System.arraycopy(cmdBuf, 0, locatBuf, 0, size);
        if (DefCommand.CMD_1_REISTER_4.equals(cmd)) {//13 收到关闭电源命令
            increase(1);
            Log.e("increase", "1");
            zeroCmdReFlag = 1;
            byte[] powerCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41
            sendCmd(powerCmd);
        } else if (DefCommand.CMD_3_DETONATE_1.equals(cmd)) {//30 进入起爆模式
            //得到电流电压信息
            byte[] powerCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//00400101获取电源状态指令
            sendCmd(powerCmd);
//            byte[] powerCmd = ThreeFiringCmd.setToXbCommon_FiringExchange("00");//0038
//            sendCmd(powerCmd);

            //处理返回的起爆模式命令
//            secondCmdFlag = 1;
//            thirdWriteCount = 0;
//            increase(3);

        } else if (DefCommand.CMD_3_DETONATE_9.equals(cmd)) {//38 进入充电检测模式
            //处理返回的起爆模式命令
//            secondCmdFlag = 1;
//            thirdWriteCount = 0;
//            increase(3);
//            thread2.start();//发送31写入延时指令
//            firstThread.exit=false;
//            firstThread.run();
//            increase(5);
//            mHandler_1.sendMessage(mHandler_1.obtainMessage());
            Log.e("起爆页面", "进入充电检测");
        } else if (DefCommand.CMD_3_DETONATE_2.equals(cmd)) {//31 写入延时时间，检测结果看雷管是否正常
            From32DenatorFiring fromData = ThreeFiringCmd.decodeFromReceiveDataWriteDelay23_2("00", locatBuf);
            if (fromData != null && writeDenator != null) {
                VoDenatorBaseInfo temp = writeDenator;
                String fromCommad = Utils.bytesToHexFun(locatBuf);
                short writeDelay = temp.getDelay();
                fromData.setShellNo(temp.getShellBlastNo());
                updateDenator(fromData, writeDelay);//更新雷管状态
                writeDenator = null;
                reThirdWriteCount++;
            }

            assert fromData != null;
            Log.e(TAG, "雷管状态fromData.getCommicationStatus(): "+fromData.toString());
            if(FiringMainActivity.stage == 6&&!fromData.getCommicationStatus().equals("FF")){
//                showdialog();
            }


        } else if (DefCommand.CMD_3_DETONATE_3.equals(cmd)) {//32 充电（雷管充电命令 等待6S（500米线，200发雷管），5.5V充电）
            //发送 高压输出命令
            sixCmdSerial = 2;

        } else if (DefCommand.CMD_3_DETONATE_4.equals(cmd)) {//33 高压输出（继电器切换，等待12S（500米线，200发雷管）16V充电）
            //收到高压充电完成命令
            //stage=7;
            sixCmdSerial = 3;

        } else if (DefCommand.CMD_3_DETONATE_5.equals(cmd)) {//34 起爆
            if (qibaoNoFlag < 5) {
//                Log.e("起爆", "第" + qibaoNoFlag + "次发送起爆指令: ");
                byte[] initBuf = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_5("00");//34起爆
                sendCmd(initBuf);
                qibaoNoFlag++;
            } else {
                //stage=9;
                eightCmdFlag = 2;
                hisInsertFireDate = Utils.getDateFormatToFileName();//记录的起爆时间
                saveFireResult();
                saveFireResult_All();

                if (!qbxm_id.equals("-1")) {
                    updataState(qbxm_id);
                }
                increase(9);//跳到第9阶段
                Log.e("increase", "9");
                try {
                    Thread.sleep(50);
                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35 退出起爆
                    sendCmd(reCmd);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } else if (DefCommand.CMD_3_DETONATE_6.equals(cmd)) {//35 退出起爆模式
            //发出关闭获取得到电压电流
//            byte[] powerCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//40
//            sendCmd(powerCmd);
        } else if (DefCommand.CMD_3_DETONATE_7.equals(cmd)) {//36 在网读ID检测是否有未注册雷管
            String fromCommad = Utils.bytesToHexFun(locatBuf);
            String noReisterFlag = ThreeFiringCmd.getCheckFromXbCommon_FiringExchange_5523_7_reval("00", fromCommad);
            Log.e("是否有未注册雷管", "返回结果: " + noReisterFlag);
            if ("FF".equals(noReisterFlag)) {
                fourOnlineDenatorFlag = 3;
//                increase(6);//0635此处功能为直接跳到第六阶段
            } else {
                fourOnlineDenatorFlag = 2;
                noReisterHandler.sendMessage(noReisterHandler.obtainMessage());
            }

        } else if (DefCommand.CMD_3_DETONATE_8.equals(cmd)) {//37 异常终止起爆

        } else if (DefCommand.CMD_4_XBSTATUS_1.equals(cmd)) {//40 获取电源状态指令
            busInfo = FourStatusCmd.decodeFromReceiveDataPower24_1("00", locatBuf);
            busHandler.sendMessage(busHandler.obtainMessage());

        } else if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//41 切换电源
            //说明打开电源命令成功
            if (FiringMainActivity.stage == 1) firstCmdReFlag = 1;
            if(FiringMainActivity.stage == 8){
                byte[] initBuf = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_5("00");//34  起爆
                sendCmd(initBuf);
            }
        } else {

        }

    }
    /**
     * 充电流程中的弹出框
     * 在高压充电前最后检测第一发雷管
     *如果错误就提示
     * */
    private void showdialog() {
        mHandler_showdialoy.sendMessage(mHandler_showdialoy.obtainMessage());
    }


    /***
     * 全部
     * @author zenghp
     *
     */
    private class ThreadFirst extends Thread {
        public volatile boolean exit = false;
        private VoDenatorBaseInfo tempBaseInfo = null;
        private ConcurrentLinkedQueue<VoDenatorBaseInfo> blastQueue;//雷管队列

        public ThreadFirst(ConcurrentLinkedQueue<VoDenatorBaseInfo> queue) {
            this.blastQueue = queue;
        }

        public void run() {
            try {
                byte[] initBuf;
                while (!isInterrupted() && !exit) {
                    switch (stage) {
                        case 0:
                            Thread.sleep(100);
                            if (zeroCount == 0) {
                                //关闭电源
                                byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");//13
                                sendCmd(powerCmd);
                            }
                            if (zeroCmdReFlag == 1) {
                                break;
                            }
                            zeroCount++;
                            if (zeroCount > 50) {//等待时间答应5秒，退出
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                exit = true;
                            }
                            break;
                        case 1://等待总线稳定时间
                            Thread.sleep(1000);
                            firstWaitCount--;
                            //说明电源打开命令未返回
                            if (firstCmdReFlag == 0 && firstWaitCount < 1) {
                                exit = true;
                            }
                            if (firstWaitCount > 2) {
                                byte[] Cmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//00400101获取电源状态指令
                                sendCmd(Cmd);
                            }
                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;
                        case 2://
                            //发出进入起爆模式命令  准备测试计时器
                            if (secondCount == 1 && secondCmdFlag == 0) {//
                                byte[] powerCmd = ThreeFiringCmd.setToXbCommon_FiringExchange("00");//0038充电
                                sendCmd(powerCmd);
                                increase(5);
                                Log.e("第5阶段-increase", "5");
                                Log.e("充电检测WaitCount", Wait_Count + "");
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            } else {
                                //得到电流电压信息
                                byte[] powerCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//00400101获取电源状态指令
                                sendCmd(powerCmd);
                            }
                            Thread.sleep(1000);
                            secondCount--;
                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;
                        case 3://写入延时时间，检测结果看雷管是否正常
                            if (reThirdWriteCount == thirdWriteCount) {//判断是否全部测试完成
//                                Thread.sleep(50);
                                thirdStartTime = 0;
                                writeDenator = null;
                                if (blastQueue == null || blastQueue.size() < 1) {
                                    increase(4);//之前是4
                                    Log.e("第4阶段-increase", "4-2");
                                    try {
                                        Thread.sleep(1000);
                                        initBuf = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_7("00");//36在网检测
                                        sendCmd(initBuf);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    fourOnlineDenatorFlag = 0;
                                    break;
                                }
                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo = write;

                                String data = "";
                                String shellStr = write.getShellBlastNo();
                                if (shellStr == null || shellStr.length() != 13)
                                    continue;//// 判读是否是十三位

//                                String denatorId = Utils.DetonatorShellToSerialNo_new(shellStr);//新协议
                                String denatorId = Utils.DetonatorShellToSerialNo(shellStr);//旧协议
//                                Log.e("雷管转化", "denatorId: " + denatorId);
//                                if(denatorId.equals("0")){
//                                    show_Toast_ui("当前版本号只支持0-F,"+shellStr+"雷管超出范围");
//                                    continue;
//                                }
                                denatorId = Utils.getReverseDetonatorNo(denatorId);
                                short delayTime = write.getDelay();
                                byte[] delayBye = Utils.shortToByte(delayTime);
                                String delayStr = Utils.bytesToHexFun(delayBye);//延时时间
                                data = denatorId + delayStr;
                                //发送31命令---------------------------------------------
                                initBuf = ThreeFiringCmd.setToXbCommon_CheckDenator23_2("00", data);//31写入延时时间
                                sendCmd(initBuf);

                                // Utils.writeLog("tname:" + Thread.currentThread().getName() + ",bl:" + blastQueue.size() + ",tount=" + thirdWriteCount + ",no=" + shellStr);
                                thirdStartTime = System.currentTimeMillis();
                                writeDenator = write;
                                thirdWriteCount++;


                                /**
                                 Message message = Message.obtain();
                                 message.what = 1;
                                 message.obj = thirdWriteCount;
                                 **/
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());

                            } else {
                                long thirdEnd = System.currentTimeMillis();
                                long spanTime = thirdEnd - thirdStartTime;
                                if (spanTime > 4000 && tempBaseInfo != null) {//发出本发雷管时，没返回超时了
                                    thirdStartTime = 0;
                                    //充电检测错误 tempBaseInfo报错 tempBaseInfo为空 未返回
//                                    Log.e("雷管异常", "tempBaseInfo: "+tempBaseInfo.toString());//雷管超时容易报错,这个就是起爆检测闪退的地方
                                    VoFiringTestError errorDe = new VoFiringTestError();
                                    errorDe.setBlastserial(tempBaseInfo.getBlastserial());//
                                    errorDe.setShellBlastNo(tempBaseInfo.getShellBlastNo());
                                    errorDe.setDelay(tempBaseInfo.getDelay());
                                    errorDe.setError(1);
                                    thirdWriteErrorDenator = errorDe;
                                    errorList.offer(errorDe);
                                    //发出错误
                                    //mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    //writeDenator = null;
                                    tempBaseInfo = null;
                                    reThirdWriteCount++;
                                } else {
                                    Thread.sleep(20);
                                }
                            }
                            break;
                        case 4:
                            if (fourthDisplay == 0)
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;
                        case 5://充电检测阶段38指令计时器
                            Wait_Count--;
                            Thread.sleep(1000);
                            Log.e("充电检测WaitCount", Wait_Count + "");
                            //说明电源打开命令未返回
                            if (Wait_Count == 1) {
//                                exit = true;
                                secondCmdFlag = 1;
                                thirdWriteCount = 0;
                                increase(3);
                                Log.e("第3阶段-increase", "3");
                            }
                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;
                        case 6://充电阶段
                            if (sixExchangeCount == ChongDian_time*10) {
                                initBuf = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_3("00");//32充电
                                sendCmd(initBuf);
                            }
                            //跳转高压前要再检测一次列表的第一发雷管,如果正确就继续,错误就提示
                            if (sixExchangeCount == (ChongDian_time - 7)*10&&list.size()>0) {
//                                String denatorId = Utils.DetonatorShellToSerialNo_new(list.get(0).getShellBlastNo());//新协议
                                String denatorId = Utils.DetonatorShellToSerialNo(list.get(0).getShellBlastNo());//旧协议
//                                Log.e("雷管转化", "denatorId: " + denatorId);
//                                if(denatorId.equals("0")){
//                                    show_Toast_ui("当前版本号只支持0-F,"+shellStr+"雷管超出范围");
//                                    continue;
//                                }
                                denatorId = Utils.getReverseDetonatorNo(denatorId);
                                short delayTime = list.get(0).getDelay();
                                byte[] delayBye = Utils.shortToByte(delayTime);
                                String delayStr = Utils.bytesToHexFun(delayBye);//延时时间
                                String data = denatorId + delayStr;
                                //发送31命令---------------------------------------------
                                initBuf = ThreeFiringCmd.setToXbCommon_CheckDenator23_2("00", data);//31写入延时时间
                                sendCmd(initBuf);
                            }
                            if (sixExchangeCount == (ChongDian_time - 8)*10) {//第8秒时,发送高压充电指令,继电器应该响
                                byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_4("00");//33高压输出
                                sendCmd(reCmd);
                            }
                            if (sixExchangeCount == 0) {
                                if (sixCmdSerial == 3) {
                                    //byte[] reCmd  = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");
                                    //sendCmd(reCmd);
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    Thread.sleep(1000);
                                    increase(7);
                                    Log.e("第7阶段-increase", "7");
                                    break;
                                }
                            }

                            if (sixExchangeCount == -1) {//切换电源是否返回正确
                                exit = true;
//                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                break;
                            }
                            Thread.sleep(100);
                            sixExchangeCount--;
                            //得到电流电压信息210  190
                            if (sixExchangeCount%5==0&&(sixExchangeCount> (ChongDian_time - 7)*10 || sixExchangeCount < (ChongDian_time - 9)*10)) {
                                byte[] powerCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//00400101
                                sendCmd(powerCmd);
                            }
                            if (stage == 6) mHandler_1.sendMessage(mHandler_1.obtainMessage());

                            break;
                        case 7:
                            if (sevenDisplay == 0)
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            sevenDisplay = 1;
                            if (keyFireCmd == 1) {

                                increase(8);
                                Log.e("increase", "8");
                                keyFireCmd = 0;
                                eightCmdExchangePower = 1;
                            }
                            break;
                        case 8://起爆阶段
//                            if (eightCount == 1) {
//                                byte[] reCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41开启总线电源指令,切换低压
//                                sendCmd(reCmd);
//                            }
                            if (eightCount >= 1) {
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                Thread.sleep(1000);
                                eightCount--;
                            } else {
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                if (eightCmdFlag == 0) {
                                    if (eightCmdExchangePower == 1) {//
                                        byte[] reCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41开启总线电源指令,切换低压
                                        sendCmd(reCmd);
                                        //发出34 起爆命令
//                                        initBuf = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_5("00");
//                                        sendCmd(initBuf);
                                        Log.e("起爆", "第一次发送起爆指令: ");
                                        eightCmdFlag = 1;
                                    } else {
                                        exit = true;
                                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                        break;
                                    }

                                }
                                if (eightCount <= -5) {
                                    exit = true;
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    break;
                                }
                                Thread.sleep(1000);
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            }
                            break;
                        case 9:
                            if (neightCount == 0) {
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            }
                            neightCount++;
                            break;
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }

    private class GetBusInfoThread extends Thread {
        public volatile boolean exit = false;
        byte[] initBuf = null;

        public void run() {//每0.5秒查询一下电源信息
            try {
                while (!isInterrupted() && exit == false) {
                    initBuf = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00");//40
                    sendCmd(initBuf);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block

                //e.printStackTrace();
            }
            //setCtlWriteBtn(true);
        }
    }

    /**
     * 按1和5起爆
     * */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();
        Log.e("起爆页面", "keyCode: " + keyCode);
        Log.e("起爆页面", "event.getAction() : " + event.getAction());
        if (keyCode == KeyEvent.KEYCODE_1) {
            m0UpTime = System.currentTimeMillis();
            Log.e("起爆页面", "m0UpTime: " + m0UpTime);
        } else if (keyCode == KeyEvent.KEYCODE_5) {
            m5DownTime = System.currentTimeMillis();
            Log.e("起爆页面", "m5DownTime: " + m5DownTime);
            long spanTime = m5DownTime - m0UpTime;
            Log.e("起爆页面", "spanTime: " + spanTime);
            if (spanTime < 500) {
                if (stage == 7) {
                    keyFireCmd = 1;
                    Log.e("起爆页面", "keyFireCmd: " + keyFireCmd);
                }
                Log.e("起爆页面", "keyFlag: " + keyFlag);
            }
        }

        return super.dispatchKeyEvent(event);
    }


    //删除历史记录第一行
    private void delHisInfo(String blastdate) {
        if (blastdate == null) return;
        if (getString(R.string.text_alert_tip3).equals(blastdate)) {//"当前雷管记录"
            show_Toast(getString(R.string.text_error_tip52));
            return;
        }
        //从表
        String selection = "blastdate = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {blastdate + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        db.delete(DatabaseHelper.TABLE_NAME_HISDETAIL, selection, selectionArgs);
        //主表
        db.delete(DatabaseHelper.TABLE_NAME_HISMAIN, selection, selectionArgs);
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    //删除历史记录第一行
    private void delHisInfo_all(String blastdate) {
        if (blastdate == null) return;
        if (getString(R.string.text_alert_tip3).equals(blastdate)) {//"当前雷管记录"
            show_Toast(getString(R.string.text_error_tip52));
            return;
        }
        //从表
        String selection = "blastdate = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {blastdate + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        db.delete(DatabaseHelper.TABLE_NAME_HISDETAIL_ALL, selection, selectionArgs);
        //主表
        db.delete(DatabaseHelper.TABLE_NAME_HISMAIN_ALL, selection, selectionArgs);
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    private String loadHisMainData() {
        List<DenatorHis_Main> list = getDaoSession().getDenatorHis_MainDao().loadAll();
        return list.get(0).getBlastdate();
    }
}
