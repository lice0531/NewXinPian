package android_serialport_api.xingbang.firingdevice;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Build;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.SecondNetTestCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.cmd.vo.From22WriteDelay;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.models.VoDenatorBaseInfo;
import android_serialport_api.xingbang.models.VoFiringTestError;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.Utils;

/**
 * 测试页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TestDenatorActivity extends SerialPortActivity {

    private DatabaseHelper mMyDatabaseHelper;
    private List<VoBlastModel> list = new ArrayList<>();

    private SQLiteDatabase db;
    private View getlistview;
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
    private volatile int firstCount = 0;
    private volatile VoDenatorBaseInfo writeDenator;
    private long thirdStartTime = 0;//第三阶段每个雷管返回命令计时器
    private VoFiringTestError thirdWriteErrorDenator;//写入错误雷管

    private Queue<VoDenatorBaseInfo> blastQueue;//雷管队列

    private Queue<VoFiringTestError> errorList;//错误雷管队列
    private volatile int thirdWriteCount;//雷管发送计数器
    private volatile int reThirdWriteCount;//获得 返回 数量

    private From42Power busInfo;

    private ThreadFirst firstThread;

    private ArrayList<Map<String, Object>> errDeData = new ArrayList<Map<String, Object>>();//错误雷管
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
    private float dangqian_ic = 0;//当前电流

    private byte[] initBuf ;//发送的命令
    private int stage_state=0;
    private int errtotal=0;
    //初始化
    private void initParam() {
        initCloseCmdReFlag = 0;
        revCloseCmdReFlag = 0;
        revOpenCmdReFlag = 0;
        revOpenCmdTestFlag = 0;//收到了打开测试命令
        thirdStartTime = 0;
        stage = 0;
        tipInfoFlag = 0;
        thirdWriteCount = 0;
        writeDenator = null;
        busInfo = null;
        thirdWriteErrorDenator = null;
        errDeData.clear();
        errorList.clear();
        firstCount = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writedelay_denator);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 21);
        db = mMyDatabaseHelper.getReadableDatabase();
        blastQueue = new LinkedList<>();
        errorList = new LinkedList<>();
        initView();
        loadMoreData();//读取数据
        Utils.writeLog("开始测试,雷管总数为"+denatorCount);
        if (denatorCount < 1) {
            show_Toast(getResources().getString(R.string.text_error_tip30));
            closeThread();
            Intent intentTemp = new Intent();
            intentTemp.putExtra("backString", "");
            setResult(1, intentTemp);
            finish();
            return;
        }
        stage = 0;
        initHandler();

        ll_firing_deAmount_4.setText("" + denatorCount);
        // getDenatorType();
    }

    private void initHandler() {
        //接受消息之后更新imageview视图
        mHandler_1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                execStage(msg);
                //sendEmptyMessageDelayed(0, 1000);
            }
        };
        busHandler_dianliu = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (busInfo != null) {
                    float f = busInfo.getBusCurrentIa() * 1000;
                    BigDecimal b = new BigDecimal(f);
                    String displayIcStr = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "μA";//保留两位小数
                    float displayIc = busInfo.getBusCurrentIa();
                    dangqian_ic = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    Log.e("当前电流", "dangqian_ic: " + dangqian_ic);

                }
                if (firstCount == 5) {
                    Log.e("总线电压", "firstCount" + firstCount);
                    Log.e("总线电压", "busInfo.getBusVoltage()" + busInfo.getBusVoltage());
                }
                if ((firstCount == 5 && busInfo.getBusVoltage() < 6.5)) {
                    Log.e("总线电压", "busInfo.getBusVoltage()" + busInfo.getBusVoltage());
                    AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
                            .setTitle("当前电压过低")//设置对话框的标题//"成功起爆"
                            .setMessage("起爆器电压过低,请再次启动测试流程,进行测试")//设置对话框的内容"本次任务成功起爆！"
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
//                busInfo = null;
            }
        };
        errHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String errAmoutStr = ll_firing_errorNum_4.getText().toString();
                if (errAmoutStr == null || errAmoutStr.trim().length() < 1) {
                    errAmoutStr = "0";
//                    ll_firing_errorNum_4.setTextColor(Color.GREEN);
                }
                ll_firing_errorNum_4.setText("" + (Integer.parseInt(errAmoutStr) + 1));
                ll_firing_errorNum_4.setTextColor(Color.RED);

            }
        };
        noReisterHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
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


            }
        };
        Handler_tip = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();
                String shellStr = b.getString("shellStr");
                if (msg.arg1 == 1) {
                    show_Toast("当前版本只支持0-F," + shellStr + "雷管超出范围");
                }


            }
        };
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
        // ll_1.setVisibility(View.GONE);
        ll_2.setVisibility(View.GONE);

        btn_return = (Button) findViewById(R.id.btn_firing_return_4);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThread();
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
            }
        });

        btn_return_complete = (Button) findViewById(R.id.btn_test_return);
        btn_return_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThread();
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
            }
        });

        btn_firing_lookError_4 = (Button) findViewById(R.id.btn_test_lookError);
        btn_firing_lookError_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadErrorBlastModel();
//                loadMoreData();
                createDialog();
            }
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


    //提示对话框,提示有未注册雷管或者未处理雷管
    public void disPlayNoReisterDenator(final int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestDenatorActivity.this);
        int blastNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
        builder.setTitle(getString(R.string.text_alert_tip));
            builder.setMessage("请检查雷管总数是否正确,确认无误后可忽略本提示");//"总线上有未处理的雷管，是否继续起爆？"
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (flag == 1) {
//                    fireUserCheck();
//                    increase(6);
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
                //  builder.
            }
        });
        builder.show();
        if (flag == 1) {
//                increase(6);
        } else {
            fourOnlineDenatorFlag = 3;
//                increase(6);
        }

    }

    /***
     * 得到错误雷管数
     */
    private void getErrorBlastCount() {
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where statusCode =? and errorCode<> ?";
        Cursor cursor = db.rawQuery(sql, new String[]{"02", "FF"});
        int totalNum = cursor.getCount();//得到数据的总条数
        if (cursor != null) cursor.close();
        ll_firing_errorNum_4.setText("" + totalNum);
        int total = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
        float IC = dangqian_ic;
        Log.e("总线电流", "getErrorBlastCount: " + IC);
        if (IC > (total - totalNum) * 70) {
            AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
                    .setTitle("当前实际电流过大")//设置对话框的标题//"成功起爆"
                    .setMessage("雷管正常数量为:" + (total - totalNum) + ",当前电流超过参考电流" + ((total - totalNum) * 70) + "μA,请排查错误雷管后重新进行检测")//设置对话框的内容"本次任务成功起爆！"
                    //设置对话框的按钮
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
            show_Toast("当前电流过大");
        }
    }

    /***
     * 加载错误雷管
     */
    private void loadErrorBlastModel() {

        //Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null, null, null, null, " blastserial asc");
//        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where statusCode =? order by blastserial asc";
//        Cursor cursor = db.rawQuery(sql, new String[]{"02"});
        String sql = DatabaseHelper.SELECT_ALL_DENATOBASEINFO_ZHENGCHANG;
        Cursor cursor = db.rawQuery(sql, null);
        Log.e("总数", "sum: "+cursor.getCount() );
        errDeData.clear();
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                String errorCode = cursor.getString(9);//管壳号
                String stCode = cursor.getString(6);//状态
                if (errorCode != null) {//说明已经通信
                    if ("FF".equals(errorCode)) {continue;}
                }
                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                String shellNo = cursor.getString(3);//管壳号
                String errorName = cursor.getString(8);//错误信息
                String delay = cursor.getString(5);//延时
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("serialNo", serialNo);
                item.put("shellNo", shellNo);
                item.put("errorName", errorName);
                item.put("delay", delay);
                errDeData.add(item);

            }
            cursor.close();
        }
    }

    /***
     * 建立错误对话框
     */
    public void createDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);

        getlistview = inflater.inflate(R.layout.firing_error_listview, null);
        // 给ListView绑定内容
        ListView errlistview = (ListView) getlistview.findViewById(R.id.X_listview);
        SimpleAdapter adapter = new SimpleAdapter(this, errDeData, R.layout.firing_error_item,
                new String[]{"serialNo", "shellNo", "errorName", "delay"},
                new int[]{R.id.X_item_no, R.id.X_item_shellno, R.id.X_item_errorname, R.id.X_item_delay});
        // 给listview加入适配器

        errlistview.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_alert_tablename1));//"错误雷管列表"
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void endTest() {
        closeThread();
        initParam();
    }

    @Override
    protected void onStart() {
        initParam();
        sendOpenThread = new SendOpenPower();
        sendOpenThread.start();
        super.onStart();
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
//        String sql =  "Select * from denatorBaseinfo where statusCode = ? order by blastserial asc";
//        Cursor cursor = db.rawQuery(sql, new String[]{"02"});
        String sql = DatabaseHelper.SELECT_ALL_DENATOBASEINFO_ZHENGCHANG;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                int holeNo = cursor.getInt(2);
                String shellNo = cursor.getString(3);//管壳号
                int delay = cursor.getInt(5);
                String stCode = cursor.getString(6);//状态
                String stName = cursor.getString(7);//
                String errorName = cursor.getString(8);//错误名称
                String errorCode = cursor.getString(9);//状态

                VoBlastModel item = new VoBlastModel();
                item.setBlastserial(serialNo);
                item.setSithole(holeNo);
                item.setDelay((short) delay);
                item.setShellBlastNo(shellNo);
                item.setErrorCode(errorCode);
                item.setErrorName(errorName);
                item.setStatusCode(stCode);
                item.setStatusName(stName);

                if(stCode.equals("02")){
                    list.add(item);
                    blastQueue.offer(item);
                }

            }
            cursor.close();
            denatorCount = blastQueue.size();
            Log.e("雷管队列", "denatorCount: " + denatorCount);
            tv_dianliu.setText(denatorCount * 35 + "μA");//参考电流
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (db != null) db.close();
//        Utils.saveFile();//把软存中的数据存入磁盘中
        super.onDestroy();
        fixInputMethodManagerLeak(this);
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
                if (firstCount >= 30) {//未收到开启测试命令
                    if (firstCount == 30)
                        show_Toast(getString(R.string.text_error_tip34));
                    break;
                }
                if (busInfo != null) {
                    String displayIcStr = "" + busInfo.getBusCurrentIa() * 1000 + "μA";
                    float displayIc = busInfo.getBusCurrentIa();
                    //displayIc =
                    dangqian_ic = busInfo.getBusCurrentIa() * 1000;
                    ll_firing_Volt_4.setText("" + busInfo.getBusVoltage() + "V");
                    ll_firing_IC_4.setText("" + displayIcStr);
                    if (busInfo.getBusCurrentIa() == 0) {
                        ll_firing_IC_4.setTextColor(Color.RED);// "电流过大";
                        show_Toast_long("");
                        stage = 5;
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                        AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
//                                .setTitle("当前电流为0")//设置对话框的标题//"成功起爆"
//                                .setMessage("当前电流为0,请检查线路是否正确连接,当前爆破网络故障未排除,如若继续会发生未知风险")//设置对话框的内容"本次任务成功起爆！"
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
//                                        stage = 5;
//                                        closeThread();
//                                        closeForm();
//
//                                        Utils.writeLog("电压过低,电压="+busInfo.getBusVoltage()+"V");
//                                        finish();
//
//                                    }
//                                })
//                                .create();
//                        dialog.show();

                        return;
                    }
                    if (displayIc * 1000 < 7) {
                        ll_firing_IC_4.setTextColor(Color.RED);
                        show_Toast_long("当前电流为0,请检查线路是否正确连接");
                        stage = 5;
                        Utils.writeLog("电流为0");
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                        AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
//                                .setTitle("电流异常")//设置对话框的标题//"成功起爆"
//                                .setMessage("当前电流为0,请检查线路是否正确连接,当前爆破网络故障未排除,如若继续会发生未知风险")//设置对话框的内容"本次任务成功起爆！"
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
//                                        stage = 5;
//                                        closeThread();
//                                        closeForm();
//
//                                        Utils.writeLog("电压过低,电压="+busInfo.getBusVoltage()+"V");
//                                        finish();
//
//                                    }
//                                })
//                                .create();
//                        dialog.show();

                        return;
                    }
                    if (busInfo.getBusVoltage() < 6) {
                        ll_firing_Volt_4.setTextColor(Color.RED);
                        show_Toast_long("当前电压异常,请检查线路是否出现短路等情况");
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                        stage = 5;
                        Utils.writeLog("电压异常,电压为"+busInfo.getBusVoltage() +"V");
                        return;
                    }
                    if (displayIc * 1000 > (denatorCount * 75) && firstCount > 3) {
                        ll_firing_IC_4.setTextColor(Color.RED);// "电流过大";
                        show_Toast_long("当前电流过大,请检查线路是否正确连接");
                        Utils.writeLog("电流过大,电流为"+displayIc * 1000+"μA");
                        stage = 5;
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                        AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
//                                .setTitle("电流异常")//设置对话框的标题//"成功起爆"
//                                .setMessage("当前电流过大,请检查线路是否正确连接,当前爆破网络故障未排除,如若继续会发生未知风险")//设置对话框的内容"本次任务成功起爆！"
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
//                                        stage = 5;
//                                        closeThread();
//                                        closeForm();
//
//                                        Utils.writeLog("电压过低,电压="+busInfo.getBusVoltage()+"V");
//                                        finish();
//
//                                    }
//                                })
//                                .create();
//                        dialog.show();

                        return;
                    } else {
                        ll_firing_IC_4.setTextColor(Color.GREEN);
                    }
                    if (displayIc * 1000 > (denatorCount * 50) && firstCount > 5 && denatorCount > 300) {
                        show_Toast_long("当前电流过大,请检查线路是否正确连接");
                        ll_firing_IC_4.setTextColor(Color.RED);// "电流过大";
                        stage = 5;
                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                        Utils.writeLog("电流过大");

//                        AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
//                                .setTitle("电流异常")//设置对话框的标题//"成功起爆"
//                                .setMessage("当前电流过大,请检查线路是否正确连接,当前爆破网络故障未排除,如若继续会发生未知风险")//设置对话框的内容"本次任务成功起爆！"
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
//                                        stage = 5;
//                                        closeThread();
//                                        closeForm();
//
//                                        Utils.writeLog("电流异常,电流="+busInfo.getBusCurrentIa() * 1000+"μA");
//                                        finish();
//
//                                    }
//                                })
//                                .create();
//                        dialog.show();


                        return;
                    }
                    if (busInfo.getBusVoltage() < 6.3) {
                        AlertDialog dialog = new AlertDialog.Builder(TestDenatorActivity.this)
                                .setTitle("总线电压过低")//设置对话框的标题//"成功起爆"
                                .setMessage("当前起爆器电压异常,可能会导致总线短路,请检查线路后再次启动起爆流程,进行起爆")//设置对话框的内容"本次任务成功起爆！"
                                //设置对话框的按钮
                                .setNeutralButton("继续", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                                        sendCmd(reCmd);
                                        dialog.dismiss();
                                        closeThread();
                                        closeForm();
                                        Utils.writeLog("电压过低,电压="+busInfo.getBusVoltage()+"V");
                                        finish();

                                    }
                                })
                                .create();
                        dialog.show();
                    }

//                    ll_firing_errorNum_4.setText(""+busInfo.getFiringVoltage()) ;
                }
                secondTxt.setText(getString(R.string.text_test_tip1) + firstCount + "s");//"等待总线稳定:"
                break;
            case 3:

                if (thirdWriteErrorDenator != null) {//写入未返回的错误雷管
                    thirdWriteErrorDenator = null;//设置错误雷管
                }
                if (errorList != null && errorList.size() >= 0) {
                    int iLoop = 0;
                    while (!errorList.isEmpty()) {//写入错误雷管
                        VoFiringTestError er = errorList.poll();
                        if (er != null) {
                            From22WriteDelay df = new From22WriteDelay();
                            df.setShellNo(er.getShellBlastNo());
                            df.setCommicationStatus("AF");
                            df.setDelayTime(er.getDelay());
                            this.updateDenator(df, er.getDelay());
                            iLoop++;
                        }
                    }

                }
                secondTxt.setText(getString(R.string.text_test_tip2) + thirdWriteCount + getString(R.string.text_test_tip3));

                break;
            case 4:
                byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Test("00");//14核心板自检
                sendCmd(powerCmd);
//                byte[] initBuf2 = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_7("00");//36 在网读ID检测
//                sendCmd(initBuf2);
                endTest();
                ll_1.setVisibility(View.GONE);
                ll_2.setVisibility(View.VISIBLE);
                secondTxt.setText(R.string.text_test_tip4);
//                getErrorBlastCount();//待商榷,既然已经每次错误累加了,还有没有必要再询问一次数据库
                break;
            case 5:
                endTest();
                ll_1.setVisibility(View.GONE);
                ll_2.setVisibility(View.VISIBLE);
                secondTxt.setText("请检查线路是否正确连接");
//                getErrorBlastCount();
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
        firstThread = null;
    }

    /**
     * 更新雷管状态
     */
    public void updateDenator(From22WriteDelay fromData, int writeDelay) {
        if (fromData.getShellNo() == null || fromData.getShellNo().trim().length() < 1) return;
        ContentValues values = new ContentValues();
        values.put("errorName", fromData.getCommicationStatusName(this));//key为字段名，value为值
        values.put("errorCode", fromData.getCommicationStatus());//key为字段名，value为值
        db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "shellBlastNo=?", new String[]{fromData.getShellNo()});
        errtotal++;
    }

    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Log.e("发送命令", str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
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
                            Thread.sleep(1000);
                            if (firstCount == 19) {//经过测试初始化命令需要6秒
                                //进入测试模式
                                byte[] powerCmd = SecondNetTestCmd.setToXbCommon_Testing_Init22_1("00");//20
                                sendCmd(powerCmd);
                            }

                            if (firstCount < 18) {
                                byte[] powerCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//40
                                sendCmd(powerCmd);
                            }

                            if (revOpenCmdTestFlag == 1) {
                                Thread.sleep(1000);
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                stage = 3;
                                break;
                                //}
                            }
                            firstCount++;
                            if (firstCount >= 40) {
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                exit = true;
                                break;
                            } else {
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            }
                            break;

                        case 3://写入延时时间，检测结果看雷管是否正常
                            if (reThirdWriteCount == thirdWriteCount) {
                                thirdStartTime = 0;
                                writeDenator = null;
                                if (blastQueue == null || blastQueue.size() < 1) {//待测雷管数小于1执行方法
                                    exit = true;
                                    byte[] initBuf2 = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_7("00");//36 在网读ID检测
                                    sendCmd(initBuf2);
                                    stage = 4;
                                    Thread.sleep(500);
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    break;
                                }

                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo = write;
                                String data = "";
                                String shellStr = write.getShellBlastNo();
//                                if (shellStr.substring(7, 8).getBytes()[0] > 70) {
//                                    Message msg = Handler_tip.obtainMessage();
//                                    msg.arg1 = 1;
//                                    Bundle b = new Bundle();
//                                    b.putString("shellStr", shellStr);
//                                    msg.setData(b);
//                                    Handler_tip.sendMessage(msg);
//                                    break;
//                                }
                                if (shellStr == null)
                                    continue;// || shellStr.length() != 13  //判读是否是十三位
//                                String denatorId = Utils.DetonatorShellToSerialNo_new(shellStr);//新编码
                                String denatorId = Utils.DetonatorShellToSerialNo(shellStr);//旧编码

                                denatorId = Utils.getReverseDetonatorNo(denatorId);
                                short delayTime = 66;
                                byte[] delayBye = Utils.shortToByte(delayTime);
                                String delayStr = Utils.bytesToHexFun(delayBye);
                                data = denatorId + delayStr;//雷管id+延时时间
                                Log.e("测试21延时", "data  " + denatorId + "--" + delayStr);
                                //发送命令21写入延时时间，检测结果看雷管是否正常
                                initBuf = SecondNetTestCmd.setToXbCommon_WriteDelay22("00", data);//
                                //sendCmd(initBuf);
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
            Log.e("打开电源标志", "initCloseCmdReFlag: "+initCloseCmdReFlag );
            if (initCloseCmdReFlag == 1) {//打开电源
                revCloseCmdReFlag = 1;
                sendOpenThread = new SendOpenPower();
                sendOpenThread.start();
            }
        } else if (DefCommand.CMD_2_NETTEST_1.equals(cmd)) {//20 进入测试模式
            //stage=3;
            revOpenCmdTestFlag = 1;
            //发出获取获取电压电流命令
            //byte[] powerCmd =FourStatusCmd.setToXbCommon_Power_Status24_1("00","01");
            //sendCmd(powerCmd);


        } else if (DefCommand.CMD_2_NETTEST_2.equals(cmd)) {//21写入延时时间，检测结果看雷管是否正常

            From22WriteDelay fromData = SecondNetTestCmd.decodeFromReceiveDataWriteCommand22("00", locatBuf);
//            Log.e("测试返回数据", "状态: " + fromData.getCommicationStatusName(this));
            if((!fromData.getCommicationStatusName(this).equals("通信成功"))&&stage_state<2){
                sendCmd(initBuf);
                Log.e("错误重新发送", "次数: "+stage_state );
                stage_state++;
                return;
            }else {
                stage_state=0;
            }
            if (fromData != null && writeDenator != null) {
                VoDenatorBaseInfo temp = writeDenator;
                String fromCommad = Utils.bytesToHexFun(locatBuf);
                short writeDelay = temp.getDelay();
                fromData.setShellNo(temp.getShellBlastNo());
                updateDenator(fromData, writeDelay);
                if (!"FF".equals(fromData.getCommicationStatus())) {
                    errHandler.sendMessage(errHandler.obtainMessage());
                }
                Log.e("测试返回数据", "fromData: " + fromData.toString());
                writeDenator = null;
                reThirdWriteCount++;
            }

        }else if (DefCommand.CMD_2_NETTEST_3.equals(cmd)) {//22 关闭测试
            //发出关闭获取得到电压电流
//            byte[] powerCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//40
//            sendCmd(powerCmd);

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

        }else if (DefCommand.CMD_4_XBSTATUS_1.equals(cmd)) {//40 获取电源状态指令

            //byte[] powerCmd =FourStatusCmd.setToXbCommon_Power_Status24_1("00","01");
            // sendCmd(powerCmd);
            From42Power fromData = FourStatusCmd.decodeFromReceiveDataPower24_1("00", locatBuf);
            busInfo = fromData;
            if (busHandler_dianliu == null) return;
//            busHandler_dianliu.sendMessage(busHandler_dianliu.obtainMessage());

        }  else if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//41 开启总线电源指令
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            revOpenCmdReFlag = 1;
            sendOpenThread.exit = true;
            Log.e("开启电源指令", "revOpenCmdReFlag: "+revOpenCmdReFlag );
            stage = 1;
            if (blastQueue.size() > 0) {
                firstThread = new ThreadFirst();
                firstThread.start();
            }
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
                    // TODO Auto-generated catch block
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
}
