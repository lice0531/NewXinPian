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
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.cmd.vo.From32DenatorFiring;
import android_serialport_api.xingbang.cmd.vo.From38ChongDian;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.cmd.vo.To52Test;
import android_serialport_api.xingbang.custom.ErrListAdapter;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DenatorHis_Detail;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.QuYu;
import android_serialport_api.xingbang.db.SysLog;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.db.greenDao.QuYuDao;
import android_serialport_api.xingbang.db.greenDao.SysLogDao;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.models.VoDenatorBaseInfo;
import android_serialport_api.xingbang.models.VoFireHisMain;
import android_serialport_api.xingbang.models.VoFiringTestError;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.CommonDialog;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.shoushi.PatternHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.annotation.NonNull;

import com.github.ihsg.patternlocker.OnPatternChangeListener;
import com.github.ihsg.patternlocker.PatternLockerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author lice
 * 起爆页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FiringMainActivity extends SerialPortActivity {
    @BindView(R.id.pattern_lock_view)
    PatternLockerView patternLockView;
    @BindView(R.id.textMsg)
    TextView textMsg;
    @BindView(R.id.ll_firing_return_7)
    LinearLayout ll_firing_return_7;
    private Button btn_return1;
    private Button btn_return2;
    private Button btn_return4;
    private Button btn_return6;
    private Button btn_return7;
    private Button btn_return8;
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
    private TextView ll_txt_firing_7;
    private Button btn_continueOk_4;//继续
    private Button btn_pressbt_7;//起爆
    private Button btn_firing_lookError_4;//查看错误
    private Button btn_fir_over;//起爆按键
    private LinearLayout ll_1;
    private LinearLayout ll_2;
    private LinearLayout ll_4;
    private LinearLayout ll_6;
    private LinearLayout ll_7;
    private LinearLayout ll_8;

    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private Handler busHandler = null;//总线信息
    private Handler mHandler_tip = null;//提示
    private Handler Handler_tip = null;//提示
    private Handler checkHandler = null;//更正错误
    private static Handler mHandler_1 = null;//更新视图
    private static Handler noReisterHandler = null;//没有注册的雷管
    private static Handler errorLgHandler = null;//级联出错的雷管
    private To52Test writeVo;
    private static volatile int stage;//0: 发送13指令 1:阶段1 2:阶段2,充电 3:检测阶段 4: 5:38指令 6:高压充电
    private static volatile int startFlag = 0;
    private volatile int zeroCount = 0;//起始阶段计数器，发出关闭电源指令时间
    private volatile int zeroCmdReFlag = 0;//第0阶段结束标志 为1时0阶段结束
    private volatile int firstWaitCount = 3;//第一阶段计时
    private int oneCount = 0;//低压时间
    private int oneCount_panduan = 0;//低压合格次数
    private volatile int oneCount_max = 20;//10发以内雷管，检测时间:低压15秒  高压20秒
    private int twoCount = 0;//高压时间
    private int twoCount_panduan = 0;//高压时间
    private volatile int sixExchangeCount = 25;//第6阶最大时间
    private int gaoya_cankaoSun = 25;//高压记录参考电流的时间
    private int yichang_time = 0;//出现错误的时间
    private int cuowuSun = 0;//电流异常错误次数
    private volatile int Wait_Count = 5;
    private volatile int firstCmdReFlag = 0;//发出打开电源命令是否返回
    private volatile int secondCount = 0;//第二阶段 计时器
    private volatile int secondCmdFlag = 0;//发出进入起爆模式命令是否返回
    private volatile int fourthDisplay = 0;//第4步，是否显示
    private volatile int thirdWriteCount;//雷管发送计数器
    private volatile int thirdWriteCount2;//雷管发送计数器
    private volatile int sevenDisplay = 0;//第7步，是否显示
    private volatile int sevenCount = 0;//第7阶段计时
    private volatile int sixCmdSerial = 1;//命令倒计时
    private volatile int eightCount = 5;//第8阶段
    private volatile int thirteenCount = 5;//第13阶段
    private volatile int eightCmdFlag = 0;//第八阶段命令发出起爆
    private volatile int qibaoNoFlag = 1;//第八阶段命令发出起爆
    private volatile int eightCmdExchangePower = 0;//切换电源命令
    private volatile int thirteenCmdExchangePower = 0;//切换电源命令
    private volatile int neightCount = 0;//
    private int elevenCount = 10;//放电时间
    private long thirdStartTime = 0;//第三阶段每个雷管返回命令计时器
    private String userId = "";
    private volatile int revPowerFlag = 0;
    private volatile int jixu = 0;
    private volatile int reThirdWriteCount = 0;//当芯片返回命令时,数量加一,用以防止上一条命令未返回,
    private volatile int reThirdWriteCount2 = 0;//当芯片返回命令时,数量加一,用以防止上一条命令未返回,
    private VoFiringTestError thirdWriteErrorDenator;//写入错误雷管
    private VoFiringTestError thirdWriteErrorDenator2;//写入错误雷管
    private int fourOnlineDenatorFlag = -1;//是否存在未注册雷管 1:36命令未返回 2:存在 3:不存在
    private int twoErrorDenatorFlag = 0;//错误雷管
    private int denatorCount = 0;//雷管总数
    private ThreadFirst firstThread;
    private From42Power busInfo;
    private static VoDenatorBaseInfo writeDenator;
    private ConcurrentLinkedQueue<VoDenatorBaseInfo> allBlastQu;//雷管队列
    private ConcurrentLinkedQueue<VoFiringTestError> errorList;//错误雷管队列
    private ConcurrentLinkedQueue<VoFiringTestError> errorList2;//错误雷管队列
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
    private String pro_name = "";//项目名称
    private int ChongDian_time;//充电时间
    private int JianCe_time;//准备时间
    private int Qibaotime;//起爆倒计时时间
    private String qiaosi_set = "";//是否检测桥丝
    private String version = "02";//版本号
    private String hisInsertFireDate;
    private ArrayList<Map<String, Object>> errDeData = new ArrayList<>();//错误雷管
    ArrayList<Map<String, Object>> hisListData = new ArrayList<>();//起爆雷管
    private String qbxm_id = "-1";
    private String qbxm_name = "";
    private int isshow = 0;
    private int isshow2 = 0;//电流过大弹窗
    private boolean isshow3 = true;//电压过低弹窗
    private int duanlu_sun=0;//电流过大次数
    private float cankao_ic_gaoya = 0;
    private float cankao_ic_diya = 0;
    private float cankao_IV = 0;
    private boolean save_ic = true;
    private List<VoDenatorBaseInfo> list_all_lg = new ArrayList<>();
    private boolean chongfu = false;//是否已经检测了一次
    private int totalerrorNum;//错误雷管数量
    private int totalerrorCDNum;//充电错误雷管数量
    private int errorNumJl;//错误雷管数量
    private String TAG = "起爆页面";
    public static final int RESULT_SUCCESS = 1;
    private String mRegion;     // 区域
    private boolean mRegion1, mRegion2, mRegion3, mRegion4, mRegion5 = true;//是否选中区域1,2,3,4,5
    private boolean dengdai = true;
    private int cankaodianliu = 15;
    private boolean kaiguan = true;
    private List<DenatorBaseinfo> errlist = new ArrayList<>();
    /**
     * 显示设备状态:
     * 01在线 02等待检测 03检测结束 04正在充电 05等待起爆 06起爆结束07起爆失败 08退出起爆 09雷管信息不完整
     * 11电流异常  12电流过大  13短路 14电流异常波动 15有充电失败雷管 16有复检失败雷管 17请检查雷管总数 18请先注册雷管
     * 21存在错误雷管,限制起爆 22（高压充电失败,限制起爆 ）23（电压异常,限制起爆） 24（总线低压异常,限制起爆）
     * 25（总线高压异常,限制起爆） 26（总线存在短路,限制起爆） 27（长时间高压,限制起爆）
     */
    private String deviceStatus = "01";
    private String cPeak = "0";//主控要显示的子机电流信息
    private String qbResult = "";//给主控更新起爆信息
    private boolean isSendWaitQb = false;//是否收到主控切换模式指令
    private boolean isGetQbResult = false;//是否收到起爆结束指令
    private float befor_dianliu = 0;
    private float befor_dianya = 0;
    private String changjia = "TY";
    private String Shangchuan = "";
    private String Qzqb = "";
    private String Fujian = "复检";
    private boolean isCasePeakWd, isCaseVoltageWd;//当前电流是否不稳定  当前电压是否不稳定
    private boolean isJL = false;//是否是从级联的指令进入的起爆页面
    List<Float> list_dianliu = new ArrayList();
    private String jlFlag;//接收有线级联进来起爆页面的flag
    private String isResJc;//是否是掉线的子设备接到了重新检测指令
    //出现只有退出的异常弹窗时，收到主控充电或起爆指令，子机不响应指令
    private boolean isQbFail = false;//是否限制级联起爆
    //出现带继续的异常弹窗时，收到主控充电或起爆指令，子机不响应指令
    private boolean isInterunptQb = false;
    private boolean isGyError = false;//如果弹出了高压充电失败或长时间处于高压的弹窗 就不再提示电流不稳定
    private boolean isHaveError = false;//子机是否出现异常情况弹窗
    private List<Integer> qyIdList = new ArrayList<>();//用户多选的区域id
    PatternHelper helper = new PatternHelper();
    private String ShouShi = "";//是否打开了图形起爆功能

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firing_page);
        ButterKnife.bind(this);

        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        getUserMessage();//获取用户信息
        changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
        Fujian = (String) MmkvUtils.getcode("Fujian", "不复检");
        Qzqb = (String) MmkvUtils.getcode("Qzqb", "是");
        Log.e(TAG,"是否强制起爆: " + Qzqb);
        AppLogUtils.writeAppXBLog("是否强制起爆: " + Qzqb);
        Shangchuan = (String) MmkvUtils.getcode("Shangchuan","否");
        if(changjia.equals("CQ")){
            cankaodianliu=17;
        }else {
            cankaodianliu=16;
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            qbxm_id = !TextUtils.isEmpty((String) bundle.get("qbxm_id")) ?
                    (String) bundle.get("qbxm_id") : "-1";
            qbxm_name = !TextUtils.isEmpty((String) bundle.get("qbxm_name")) ?
                    (String) bundle.get("qbxm_name") : "";
        } else {
            qbxm_id = "-1";
            qbxm_name = " ";
        }
        Log.e(TAG,"qbxm_id:" + qbxm_id + "--qbxm_name:" + qbxm_name);
        jlFlag = !TextUtils.isEmpty(intent.getStringExtra("isJl")) ?
                intent.getStringExtra("isJl") : "";
        isResJc = !TextUtils.isEmpty(intent.getStringExtra("isResJc")) ?
                intent.getStringExtra("isResJc") : "";
        isJL = !TextUtils.isEmpty(jlFlag) ? true : false;
        Log.e(TAG,"进入起爆页面");
        Utils.writeLog("起爆页面-qbxm_id:" + qbxm_name);
        AppLogUtils.writeAppLog("起爆页面-qbxm_id:" + qbxm_name);
        AppLogUtils.writeAppXBLog("起爆倒计时elevenCount:" + elevenCount);
        startFlag = 1;
        qyIdList = new GreenDaoMaster().getSelectedQyIdList();
        ShouShi = (String) MmkvUtils.getcode("ShouShi", "否");
        AppLogUtils.writeAppXBLog("是否打开手势起爆功能: " + ShouShi);
        initParam();//重置参数
        initView();
        initHandle();
        loadBlastModel();
        ctlLinePanel(1);//初始化页面
        firstThread = new ThreadFirst(allBlastQu);//全部线程
        Utils.writeRecord("---进入起爆页面---");
        Utils.writeRecord("开始测试,雷管总数为" + denatorCount + "--当前区域:" + mRegion);
//        elevenCount = getMaxDelay() / 1000 + 1;
        elevenCount = getRegionMaxDelay(qyIdList) / 1000 + 1;
        Log.e(TAG, "起爆放电倒计时值elevenCount: " + elevenCount);
        Utils.writeLog("起爆放电倒计时值:" + elevenCount);
        AppLogUtils.writeAppXBLog("起爆页面-qbxm_id:" + qbxm_name);
        Log.e(TAG, "isTestDenator: " + MmkvUtils.getcode("isTestDenator", ""));
        if (isJL) {
            //给主机发消息告知已进入起爆页面
            if (TextUtils.isEmpty(isResJc)) {
                EventBus.getDefault().post(new FirstEvent("B2"));
            } else {
                EventBus.getDefault().post(new FirstEvent("BBA2"));
            }
        }
    }

    private void initView() {
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);
        mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);
        mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);
        mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);
        mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);
        if (isJL) {
            // 获取原始label
            String originalTitle = getResources().getString(R.string.title_activity_firingpage); // 从资源中获取
            String newTitle = originalTitle + "      子机: " + MmkvUtils.getcode("ACode", "");

            // 设置Toolbar标题
            getSupportActionBar().setTitle(newTitle);
        }
        ll_1 = findViewById(R.id.ll_firing_1);
        ll_2 = findViewById(R.id.ll_firing_2);
        ll_4 = findViewById(R.id.ll_firing_4);
        ll_6 = findViewById(R.id.ll_firing_6);
        ll_7 = findViewById(R.id.ll_firing_7);
        ll_8 = findViewById(R.id.ll_firing_8);

        firstTxt = findViewById(R.id.ll_waiting_txt_firing_1);
        secondTxt = findViewById(R.id.ll_txt_firing_2);
        fourTxt = findViewById(R.id.ll_txt_firing_4);
        sixTxt = findViewById(R.id.ll_txt_firing_6);
        eightTxt = findViewById(R.id.ll_txt_firing_8);

        firstTxt.setText((oneCount) + "s");
        secondTxt.setText(getString(R.string.text_firing_tip7) + (oneCount) + "s)");
        ll_firing_Volt_2 = findViewById(R.id.ll_firing_Volt_2);
        ll_firing_IC_2 = findViewById(R.id.ll_firing_IC_2);
        ll_firing_Volt_4 = findViewById(R.id.ll_firing_Volt_4);
        ll_firing_IC_4 = findViewById(R.id.ll_firing_IC_4);
        ll_firing_Volt_5 = findViewById(R.id.ll_firing_Volt_5);
        ll_firing_IC_5 = findViewById(R.id.ll_firing_IC_5);
        ll_firing_deAmount_4 = findViewById(R.id.ll_firing_deAmount_4);//雷管数
        ll_firing_deAmount_2 = findViewById(R.id.ll_firing_deAmount_2);//雷管数
        ll_firing_errorAmount_2 = findViewById(R.id.ll_firing_errorAmount_2);//错误数
        ll_firing_errorAmount_4 = findViewById(R.id.ll_firing_errorAmount_4);//错误数
        tv__qb_dianliu_1 = findViewById(R.id.tv__qb_dianliu_1);//错误数
        tv__qb_dianliu_2 = findViewById(R.id.tv__qb_dianliu_2);//错误数
        ll_firing_Volt_6 = findViewById(R.id.ll_firing_Volt_6);
        ll_firing_IC_6 = findViewById(R.id.ll_firing_IC_6);
        ll_firing_Volt_7 = findViewById(R.id.ll_firing_Volt_7);
        ll_firing_IC_7 = findViewById(R.id.ll_firing_IC_7);
        ll_firing_Hv_7 = findViewById(R.id.ll_firing_Hv_7);//起爆电压
        ll_firing_Hv_6 = findViewById(R.id.ll_firing_Hv_6);//起爆电压
        ll_txt_firing_7 = findViewById(R.id.ll_txt_firing_7);//起爆提示

        String device = Build.DEVICE;
        switch (device) {
            case "KT50":
            case "KT50_B2": {
                ll_txt_firing_7.setText(R.string.text_firing_tip5_2);
                break;
            }
            case "ST327":
            case "S337": {
                ll_txt_firing_7.setText(R.string.text_firing_tip5);
                break;
            }
        }

        btn_fir_over = findViewById(R.id.btn_fir);//起爆电压
        btn_fir_over.setOnClickListener(view -> keyFireCmd = 1);
        // threadTest = new ThreadTest();
        btn_return1 = findViewById(R.id.btn_firing_return_1);
        btn_return1.setOnClickListener(v -> {
            AppLogUtils.writeAppXBLog("点击了退出按钮");
            exitJl();
            closeThread();
            closeForm();
            // mHandler_1.sendMessage(mHandler_1.obtainMessage());
        });
        btn_return2 = findViewById(R.id.btn_firing_return_2);
        btn_return2.setOnClickListener(v -> {
            AppLogUtils.writeAppXBLog("点击了退出按钮");
            exitJl();
            closeThread();
            closeForm();
            Utils.writeRecord("---点击退出按钮---");
        });
        btn_return4 = findViewById(R.id.btn_firing_return_4);
        btn_return4.setOnClickListener(v -> {
            AppLogUtils.writeAppXBLog("点击了退出按钮");
            exitJl();
            closeThread();
            closeForm();
            Utils.writeRecord("---点击退出按钮---");
        });
        btn_return6 = findViewById(R.id.btn_firing_return_6);
        btn_return6.setOnClickListener(v -> {
            AppLogUtils.writeAppXBLog("点击了退出按钮");
            exitJl();
            closeThread();
            closeForm();
            Utils.writeRecord("---点击退出按钮---");
        });
        btn_return7 = findViewById(R.id.btn_firing_return_7);
        btn_return7.setOnClickListener(v -> {
            exitJl();
            closeThread();
            closeForm();
            Utils.writeRecord("---点击退出按钮---");
        });
        btn_return8 = findViewById(R.id.btn_firing_return_8);
        btn_return8.setOnClickListener(v -> {
            AppLogUtils.writeAppXBLog("点击了退出按钮");
            exitJl();
            closeThread();
            closeForm();
            Utils.writeRecord("---点击退出按钮---");
        });
        btn_firing_lookError_4 = findViewById(R.id.btn_test_lookError);
        btn_firing_lookError_4.setOnClickListener(v -> {
            loadErrorBlastModel();
            createDialog();
        });
        //继续起爆
        btn_continueOk_4 = findViewById(R.id.btn_firing_continue_4);
        btn_continueOk_4.setOnClickListener(v -> {
//            mExpDevMgr = new ExpdDevMgr(this);//被关闭了??
//            if(!mExpDevMgr.isSafeSwitchOpen()){
//                createDialog_kaiguan();
//                return;
//            }
            String err = ll_firing_errorAmount_4.getText().toString();
            if (err.equals("0")) {
                AppLogUtils.writeAppLog("检测结束，点继续按钮进行充电了");
//                increase(33);//之前是4
                increase(6);//充电阶段
                duanlu_sun=0;
//                version_1 = true;
            } else {
                if ("是".equals(Qzqb)) {
                    haveErrLgNotQzqb(true);
                } else {
                    Log.e(TAG,"有错误雷管:不继续");
                    haveErrLgNotQzqb(false);
                }
            }
        });
        //密码锁
        patternLockView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(@NonNull PatternLockerView patternLockerView) {
                //根据需要添加业务逻辑
            }

            @Override
            public void onChange(@NonNull PatternLockerView patternLockerView, @NonNull List<Integer> list) {
                //根据需要添加业务逻辑
            }

            @Override
            public void onComplete(@NonNull PatternLockerView patternLockerView, @NonNull List<Integer> list) {
                //根据需要添加业务逻辑
                boolean isOk = isPatternOk(list);
                patternLockerView.updateStatus(!isOk);
                textMsg.setText(helper.getMessage());
                if (isOk) {
                    increase(8);//起爆
                    Log.e("increase", "8");
                    keyFireCmd = 0;
                    eightCmdExchangePower = 1;
                }
            }

            @Override
            public void onClear(@NonNull PatternLockerView patternLockerView) {
                //根据需要添加业务逻辑
//                finishIfNeeded();
            }
        });

        if(ShouShi.equals("是")){
            patternLockView.setVisibility(View.VISIBLE);
            textMsg.setVisibility(View.VISIBLE);
        }
    }

    private boolean isPatternOk(List<Integer> list) {
        helper.validateForChecking(list);
        return helper.isOk();
    }

    private void haveErrLgNotQzqb(boolean isContinue) {
        String isTestDenator = (String) MmkvUtils.getcode("isTestDenator", "");
        if (TextUtils.isEmpty(isTestDenator) || isTestDenator.equals("N")) {
            if (!isJL) {
                if (isContinue) {
                    showErrorLgDialog(getResources().getString(R.string.text_qberr6),isContinue);
                } else {
                    showErrorLgDialog(getResources().getString(R.string.text_qberr1),isContinue);
                }
                //未网检就直接起爆且存在错误雷管只能退出  所以接收到级联指令也不操作
            } else {
                if (isContinue) {
                    showErrorLgDialog(getResources().getString(R.string.text_qberr5), isContinue);
                } else {
                    showErrorLgDialog(getResources().getString(R.string.text_qberr2), isContinue);
                }
            }
        } else {
            if (isContinue) {
                showErrorLgDialog(getResources().getString(R.string.text_qberr5), isContinue);
            } else {
                showErrorLgDialog(getResources().getString(R.string.text_qberr2), isContinue);
            }
//            //厂家为XJ:有错误雷管只能退出  所以接收到级联指令也不操作
//            xzqb();
//            //XJ存在错误雷管 限制起爆
//            sendXzqbData("01");
//            deviceStatus = "21";
        }
    }

    private void xzqb(){
        //级联模式下子机是否是限制起爆状态
        isQbFail = true;
    }

    private void sendXzqbData(String xzqbMsgStatus) {
        isHaveError = true;
        int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
        String stureNum = Utils.strPaddingZero(allNum, 3);
        String serrNum = Utils.strPaddingZero(errorNumJl, 3);
        String currentPeak = Utils.strPaddingZero(cPeak, 6);
        EventBus.getDefault().post(new FirstEvent("B9",xzqbMsgStatus,currentPeak,stureNum,serrNum));
    }

    //判断当前子机级联是否可以继续进行
    private boolean checkIsStopJl() {
        String err = ll_firing_errorAmount_4.getText().toString();
//        if (changjia.equals("XJ")) {
//            if (!err.equals("0")) {
//                // 未网检就直接起爆且存在错误雷管只能退出，所以接收到级联指令也不操作
//                Log.e(TAG, "错误数量:" + err);
//                return true; // 厂家为"XJ":返回 true
//            }
//            Log.e(TAG, "厂家是:" + changjia);
//        }
        if ("否".equals(Qzqb)) {
            if (!err.equals("0")) {
                // 未网检就直接起爆且存在错误雷管只能退出，所以接收到级联指令也不操作
                Log.e(TAG, "错误数量:" + err);
                return true; // 只有在这里返回 true
            }
        }
        return false; // 其他情况返回 false
    }

    public void createDialog_kaiguan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("安全提醒");//"说明"
        builder.setMessage("检测到您的安全开关处于关闭状态,请先打开掌机右侧的安全开关,再进行充电!");
        builder.setNegativeButton("返回", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showErrorLgDialog(String content,boolean isContinue) {
        TextView view = new TextView(this);
        view.setTextSize(25);
        view.setTextColor(Color.RED);
        view.setText(content);
        view.setTypeface(null, Typeface.BOLD);
        AlertDialog dialog;
        if (isContinue) {
            //可以强制起爆: 有错误雷管可以继续
            dialog = new Builder(FiringMainActivity.this)
                    .setTitle(getResources().getString(R.string.text_alert_tip))//设置对话框的标题
                    .setView(view)
                    .setNeutralButton(R.string.text_dialog_qx, (dialog2, which) -> {
                        AppLogUtils.writeAppLog("检测结束," + content + "点取消按钮弹窗消失了");
                    })
                    .setPositiveButton(R.string.text_firing_jixu, (dialog2, which) -> {
                        AppLogUtils.writeAppLog("检测结束，点继续按钮进行充电了");
                        Log.e(TAG,"有错误雷管:继续");
                        list_dianliu.clear();
                        increase(6);//充电阶段
                        Log.e(TAG, "eightCount: " + eightCount);
                    })
                    .create();
        } else {
            AppLogUtils.writeAppLog("检测结束," + content + "--不能强制起爆");
            //厂家为XJ:有错误雷管只能退出  所以接收到级联指令也不操作
            xzqb();
            //XJ存在错误雷管 限制起爆
            sendXzqbData("01");
            deviceStatus = "21";
            //不能强制起爆: 有错误雷管不继续
            dialog = new Builder(FiringMainActivity.this)
                    .setTitle(getResources().getString(R.string.text_alert_tip))//设置对话框的标题
                    .setView(view)
                    .setNeutralButton(R.string.text_qd, (dialog2, which) -> {
                    })
                    .create();
        }
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void initHandle() {
        Handler_tip = new Handler(msg -> {
            Bundle b = msg.getData();
            String shellStr = b.getString("shellStr");
            if (msg.what == 1) {
                show_Toast(getResources().getString(R.string.text_qberr3) + shellStr + getResources().getString(R.string.text_qberr4));
            } else if (msg.what == 2) {
                deviceStatus = "09";
                sendXzqbData("09");
                AlertDialog dialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_test_title1))//设置对话框的标题
                        .setMessage(getResources().getString(R.string.text_text_msg1))//设置对话框的内容
                        //设置对话框的按钮
                        .setNeutralButton(getResources().getString(R.string.text_sync_tc), (dialog1, which) -> {
                            exitJl();
                            dialog1.dismiss();
                            finish();
                            MmkvUtils.savecode("isTestDenator", "N");
                        })
//                        .setNeutralButton("确定", (dialog12, which) -> dialog12.dismiss())
                        .create();
                dialog.show();
            } else if (msg.what == 3) {
                String tip = "";
                switch (msg.obj.toString()) {
                    case "00":
                        tip = getResources().getString(R.string.text_xl);
                        break;
                    case "01":
                        tip = getResources().getString(R.string.text_dy);
                        break;
                    case "02":
                        tip = getResources().getString(R.string.text_dl);
                        break;
                }

                AlertDialog dialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_qbwc))//设置对话框的标题
                        .setMessage(getResources().getString(R.string.text_xtjcd) + tip + getResources().getString(R.string.text_czyc))//设置对话框的内容
                        //设置对话框的按钮
                        .setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog1, which) -> {
                            exitJl();
                            dialog1.dismiss();
                            finish();
                        })
                        .setPositiveButton(getString(R.string.text_firing_tip17), (dialog14, which) -> {
                            Intent intent = new Intent(FiringMainActivity.this, QueryHisDetail.class);
                            startActivityForResult(intent, 1);
                            dialog14.dismiss();
                            closeThread();
                            closeForm();
                        })
                        .create();
                dialog.show();
            } else if (msg.what == 4) {
                if (isJL) {
                    //电流异常显示弹窗  有线级联通知主控
                    sendErrorData("01");
                    deviceStatus = "11";
                    isDlycShow = true;
                }
                AppLogUtils.writeAppLog("显示电流异常弹窗了");
                dlycDialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_dlyc))//设置对话框的标题
                        .setMessage(getResources().getString(R.string.text_jcyc1))//设置对话框的内容
                        //设置对话框的按钮
                        .setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog1, which) -> {
                            AppLogUtils.writeAppLog("点击了退出按钮");
                            exitJl();
                            dialog1.dismiss();
                            finish();
                        })
                        .setPositiveButton(getResources().getString(R.string.text_dialog_jxqb), (dialog12, i) -> {
                            dialog12.dismiss();
                            AppLogUtils.writeAppLog("电流异常弹窗--点击了继续按钮");
                            if (isJL) {
                                isDlycShow = false;
                                if (isDlycShow) {
                                    sendErrorData("01");
                                    deviceStatus = "11";
                                } else if (isDlgdShow) {
                                    sendErrorData("02");
                                    deviceStatus = "12";
                                }  else if (isDlShow) {
                                    sendErrorData("03");
                                    deviceStatus = "13";
                                } else if (isDlycbdShow) {
                                    sendErrorData("04");
                                    deviceStatus = "14";
                                } else if (isCheckLgNumShow) {
                                    sendErrorData("07");
                                    deviceStatus = "17";
                                }  else {
                                    isInterunptQb = false;
                                    isHaveError = false;
                                    deviceStatus = "05";
                                }
                                sendClycjg();
                            }
                        })
                        .create();
                dlycDialog.setCanceledOnTouchOutside(false);
                dlycDialog.show();
            } else if (msg.what == 5) {
                show_Toast(getResources().getString(R.string.text_dwlg) + msg.obj + getResources().getString(R.string.text_fjsb));
//                AlertDialog dialog = new Builder(FiringMainActivity.this)
//                        .setTitle("雷管复检异常!")//设置对话框的标题
//                        .setMessage("系统检测到:当前雷管复检异常,可能导致起爆失败,请检查错误后再进行起爆")//设置对话框的内容
//                        //设置对话框的按钮
//                        .setNegativeButton("退出", (dialog1, which) -> {
//                            dialog1.dismiss();
//                            finish();
//                        })
//                        .setNeutralButton("继续起爆", (dialog12, which) -> dialog12.dismiss())
//                        .create();
//                dialog.show();
            } else if (msg.what == 6) {
                ll_firing_errorAmount_2.setText("0");
                ll_firing_errorAmount_4.setText("0");
                totalerrorNum = 0;
                errorNumJl = 0;
            } else if (msg.what == 7) {
//                //当前有充电失败雷管
                isInterunptQb = true;
                deviceStatus = "15";
                sendErrorData("05");
                initDialog_chongdian(getResources().getString(R.string.cdsv1));
            }else if (msg.what == 8) {
                isInterunptQb = true;
                deviceStatus = "16";
                Log.e(TAG,"复检失败了isInterunptQb:" + isInterunptQb);
                sendErrorData("06");
                initDialog_chongdian(getResources().getString(R.string.cdsv2));
            }
            return false;
        });
        //接受消息之后更新imageview视图
        mHandler_1 = new Handler(msg -> {
            execStage(msg);
            return false;
        });
        mHandler_tip = new Handler(msg -> {
            String time = (String) msg.obj;
            delHisInfo(time);
            show_Toast(getResources().getString(R.string.text_scjl) + time + getResources().getString(R.string.text_jl));
            return false;
        });
        noReisterHandler = new Handler(msg -> {
            if (fourOnlineDenatorFlag == 2) {
                disPlayNoReisterDenator(0);//提示框
                Log.e("未注册雷管", "线上有未注册雷管弹出框");
            }
            if (twoErrorDenatorFlag == 1) {//错误数量加1
                twoErrorDenatorFlag = 0;
                String err = ll_firing_errorAmount_2.getText().toString();

                if (err.length() < 1) err = "0";
                ll_firing_errorAmount_2.setText("" + (Integer.parseInt(err) + 1));
                ll_firing_errorAmount_4.setText("" + (Integer.parseInt(err) + 1));
                ll_firing_errorAmount_2.setTextColor(Color.RED);
                ll_firing_errorAmount_4.setTextColor(Color.RED);
                totalerrorNum = Integer.parseInt(err) + 1;
            }
            return false;
        });
        errorLgHandler = new Handler(msg -> {
            String err = ll_firing_errorAmount_2.getText().toString();
            if (err.length() < 1) err = "0";
            errorNumJl = Integer.parseInt(err);
            return false;
        });
        busHandler = new Handler(msg -> {
            if (busInfo != null) {
                ll_firing_Volt_2.setText("" + busInfo.getBusVoltage() + "V");
                String displayIcStr = (int) busInfo.getBusCurrentIa() + "μA";//保留两位小数
                float displayIc = busInfo.getBusCurrentIa();
                if (displayIc > 21000 && stage != 6 && stage != 7) {
                    Log.e(TAG, "疑似短路stage: " + stage);
                    displayIcStr = displayIcStr + getString(R.string.text_text_ysdl);
                    setIcView(Color.RED);//设置颜色
                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,疑似短路");
                }
//                else if (displayIc > (denatorCount * cankaodianliu +1000) && displayIc < (denatorCount * cankaodianliu +4000) && displayIc > 10 && stage != 6 && stage != 7) {// "电流偏大";
//                    displayIcStr = displayIcStr + getString(R.string.text_test_dlpd);
//                    setIcView(Color.RED);//设置颜色
//                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,电流偏大");
//                } else if (displayIc > (denatorCount * cankaodianliu*1.3 +2400) && displayIc < (denatorCount * cankaodianliu*1.3 +8000) && displayIc > 10 && (stage == 6||stage == 7) ) {// "电流偏大";
//                    displayIcStr = displayIcStr + getString(R.string.text_test_dlpd);
//                    setIcView(Color.RED);//设置颜色
//                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,电流偏大");
//                }
                else if (denatorCount<200&&displayIc > (denatorCount * cankaodianliu *2) && displayIc > 10 && stage != 6 && stage != 7) {// "电流过大";
                    displayIcStr = displayIcStr + getString(R.string.text_test_dlgd);
                    setIcView(Color.RED);//设置颜色
                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,电流过大");
                }else if (denatorCount<200&&displayIc > (denatorCount * cankaodianliu*1.3*2) && displayIc > 10 && (stage == 6||stage == 7) ) {// "电流过大";
                    displayIcStr = displayIcStr + getString(R.string.text_test_dlgd);
                    setIcView(Color.RED);//设置颜色
                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,电流过大");
                }
                else if (denatorCount >= 200 &&displayIc > (denatorCount * cankaodianliu +4000) && displayIc > 10 && stage != 6 && stage != 7) {// "电流过大";
                    displayIcStr = displayIcStr + getString(R.string.text_test_dlgd);
                    setIcView(Color.RED);//设置颜色
                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,电流过大");
                }else if (denatorCount >= 200 &&displayIc > (denatorCount * cankaodianliu*1.3 +8000) && displayIc > 10 && (stage == 6||stage == 7) ) {// "电流过大";
                    displayIcStr = displayIcStr + getString(R.string.text_test_dlgd);
                    setIcView(Color.RED);//设置颜色
                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,电流过大");
                } else if (denatorCount >= 200 &&displayIc < (4 + denatorCount * 6) && stage != 6) {
                    displayIcStr = displayIcStr + getString(R.string.text_test_ysdl);
                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,疑似断路");
                    setIcView(Color.RED);//设置颜色
                }else if (displayIc > 30000 && stage == 6) {
                    Log.e(TAG, "疑似短路stage: " + stage);
                    displayIcStr = displayIcStr + getString(R.string.text_text_ysdl);
                    setIcView(Color.RED);//设置颜色
                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,疑似短路");
                }
//                else if (displayIc > ( cankao_ic_gaoya *0.8) && displayIc < (cankao_ic_gaoya *0.9) && displayIc > 10 && stage == 6) {// "电流过大";
//                    displayIcStr = displayIcStr + "电流偏低";
//                    setIcView(Color.RED);//设置颜色
//                    Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,电流偏低");
//                }
                else {
                    ll_firing_IC_2.setTextColor(Color.GREEN);
                    ll_firing_IC_4.setTextColor(Color.GREEN);
                    ll_firing_IC_5.setTextColor(Color.GREEN);
                    ll_firing_IC_6.setTextColor(Color.GREEN);
                    ll_firing_IC_7.setTextColor(Color.GREEN);
                    if (displayIc < 8) {
                        Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,疑似短路");
                    } else {
                        Utils.writeRecord("--起爆测试--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,电流正常");
                    }
                }
                ll_firing_IC_2.setText("" + displayIcStr);
                ll_firing_Volt_4.setText("" + busInfo.getBusVoltage() + "V");
                ll_firing_Volt_5.setText("" + busInfo.getBusVoltage() + "V");
                ll_firing_IC_4.setText("" + displayIcStr);
                cPeak = ((int) busInfo.getBusCurrentIa()) + "";
                ll_firing_IC_5.setText("" + displayIcStr);
                ll_firing_Volt_6.setText("" + busInfo.getBusVoltage() + "V");
                ll_firing_IC_6.setText("" + displayIcStr);
                ll_firing_Volt_7.setText("" + busInfo.getBusVoltage() + "V");
                ll_firing_IC_7.setText("" + displayIcStr);
                ll_firing_Hv_7.setText("" + busInfo.getFiringVoltage() + "V");
                ll_firing_Hv_6.setText("" + busInfo.getFiringVoltage() + "V");

            }

            if (isshow3&&(sixExchangeCount - twoCount) <= 10 && busInfo.getBusVoltage() < 14) {
                isshow3=false;
                Log.e(TAG, "高压充电失败: 1");
                Utils.writeRecord("--起爆测试--:高压充电失败");
                Log.e("总线电压", "busInfo.getBusVoltage()" + busInfo.getBusVoltage());
                if (isJL) {
                    xzqb();
                    isGyError = true;
                    sendCmd(ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00"));//35退出起爆
                    //高压充电失败 限制起爆
                    sendXzqbData("02");
                    deviceStatus = "22";
                    isInterunptQb = true;
                }
                AlertDialog dialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_fir_dialog17))//设置对话框的标题//"成功起爆"
                        .setMessage(getResources().getString(R.string.text_fir_dialog18))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog13, which) -> {
                            exitJl();
                            dialog13.dismiss();
                            closeThread();
                            closeForm();
                            finish();
                            MmkvUtils.savecode("isTestDenator", "N");
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                dialog.show();
            }

//            Log.e(TAG, "-----------: ");
//            Log.e(TAG, "isshow2: "+isshow2 );
//            Log.e(TAG, "oneCount: "+oneCount );
//            Log.e(TAG, "busInfo.getBusCurrentIa(): "+busInfo.getBusCurrentIa() );
            Log.e(TAG, "stage: "+stage );
            if (isshow2 == 0 && oneCount >=gaoya_cankaoSun*0.9 && denatorCount >= 200 && busInfo.getBusCurrentIa() > (denatorCount * cankaodianliu +4000) && busInfo.getBusCurrentIa() > 10 && stage == 2) {// "电流过大";
                Log.e(TAG, "低压:雷管>=200:电流过大gaoya_cankaoSun: "+gaoya_cankaoSun );
                Log.e(TAG, "低压:雷管>=200:stage: "+stage );
                Log.e(TAG, "低压:雷管>=200:busInfo.getBusCurrentIa(): "+busInfo.getBusCurrentIa() );
                if (isJL) {
                    if (!isGyError && !checkIsStopJl()) {
                        duanlu_sun++;
                        if (duanlu_sun > 1) {
                            isshow2 = 1;
                            //电流过大显示弹窗  有线级联通知主控
                            sendErrorData("02");
                            deviceStatus = "12";
                            showDlgdDialog();
                            Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压电流过大了");
                        }
                    } else {
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压电流过大了");
                    }
                } else {
                    duanlu_sun++;
                    if (duanlu_sun > 1) {
                        isshow2 = 1;
                        //电流过大显示弹窗  有线级联通知主控
                        sendErrorData("02");
                        deviceStatus = "12";
                        showDlgdDialog();
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压电流过大了");
                    }
                    Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压电流过大了");

                }
            }

            if (isshow2 == 0 && oneCount >= gaoya_cankaoSun * 0.9 && denatorCount < 200 && busInfo.getBusCurrentIa() > (denatorCount * cankaodianliu * 2) && busInfo.getBusCurrentIa() > 10 && stage == 2) {// "电流过大";
                Log.e(TAG, "低压:雷管<200:电流过大gaoya_cankaoSun: "+gaoya_cankaoSun );
                Log.e(TAG, "低压:雷管<200:stage: "+stage );
                Log.e(TAG, "低压:雷管<200:busInfo.getBusCurrentIa(): "+busInfo.getBusCurrentIa() );
                if (isJL) {
                    if (!isGyError && !checkIsStopJl()) {
                        duanlu_sun++;
                        if (duanlu_sun > 1) {
                            isshow2 = 1;
                            //电流过大显示弹窗  有线级联通知主控
                            sendErrorData("02");
                            deviceStatus = "12";
                            showDlgdDialog();
                            Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压电流过大了");
                        }
                    } else {
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压电流过大了");
                    }
                } else {
                    duanlu_sun++;
                    if (duanlu_sun > 1) {
                        isshow2 = 1;
                        //电流过大显示弹窗  有线级联通知主控
                        sendErrorData("02");
                        deviceStatus = "12";
                        showDlgdDialog();
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压电流过大了");
                    }
                    Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压电流过大了");
                }
            }
            if (isshow2 == 0 && twoCount >=gaoya_cankaoSun*0.9 && denatorCount >= 200 && busInfo.getBusCurrentIa() > (denatorCount * cankaodianliu * 1.3+8000) && busInfo.getBusCurrentIa() > 10 &&  stage == 6) {// "电流过大";
                Log.e(TAG, "高压:雷管>=200:电流过大gaoya_cankaoSun: "+gaoya_cankaoSun );
                Log.e(TAG, "高压:雷管>=200:stage: "+stage );
                Log.e(TAG, "高压:雷管>=200:busInfo.getBusCurrentIa(): "+busInfo.getBusCurrentIa() );
                if (isJL) {
                    if (!isGyError) {
                        duanlu_sun++;
                        if(duanlu_sun>1){
                            isshow2 = 1;
                            //电流过大显示弹窗  有线级联通知主控
                            sendErrorData("02");
                            deviceStatus = "12";
                            showDlgdDialog();
                            Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--高压电流过大了");
                        }
                    } else {
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--高压电流过大了");
                    }
                } else {
                    duanlu_sun++;
                    if(duanlu_sun>1){
                        isshow2 = 1;
                        //电流过大显示弹窗  有线级联通知主控
                        sendErrorData("02");
                        deviceStatus = "12";
                        showDlgdDialog();
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--高压电流过大了");
                    }
                }
            }
            if (isshow2 == 0 && twoCount >= gaoya_cankaoSun * 0.9 && denatorCount < 200 && busInfo.getBusCurrentIa() > (denatorCount * cankaodianliu * 1.3 * 2) && busInfo.getBusCurrentIa() > 10 && stage == 6) {// "电流过大";
                Log.e(TAG, "高压:雷管<200:电流过大gaoya_cankaoSun: "+gaoya_cankaoSun );
                Log.e(TAG, "高压:雷管<200:stage: "+stage );
                Log.e(TAG, "高压:雷管<200:busInfo.getBusCurrentIa(): "+busInfo.getBusCurrentIa() );
                if (isJL) {
                    if (!isGyError) {
                        duanlu_sun++;
                        if(duanlu_sun>1){
                            isshow2 = 1;
                            //电流过大显示弹窗  有线级联通知主控
                            sendErrorData("02");
                            deviceStatus = "12";
                            showDlgdDialog();
                            Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--高压电流过大了");
                        }
                    } else {
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--高压电流过大了");
                    }
                } else {
                    duanlu_sun++;
                    if(duanlu_sun>1){
                        isshow2 = 1;
                        //电流过大显示弹窗  有线级联通知主控
                        sendErrorData("02");
                        deviceStatus = "12";
                        showDlgdDialog();
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--高压电流过大了");
                    }
                }
            }

            //电流大于4000,重启检测阶段
            if (oneCount > 20 && stage == 2 && busInfo != null) {
                if (isJL) {
                    if (!isGyError && !checkIsStopJl()) {
                        Log.e(TAG, "busInfo: " + busInfo.toString());
                        float displayIc = busInfo.getBusCurrentIa();
                        if (displayIc > 21000) {
                            if (!chongfu) {
                                //低压短路
                                isInterunptQb = true;
                                deviceStatus = "13";
                                sendErrorData("03");
                                initDialog_zanting_stop(getResources().getString(R.string.text_dlyc3));//弹出框
                            } else {
                                Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压检测中短路了");
                            }
                        }
                    } else {
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压检测中短路了");
                    }
                } else {
                    Log.e(TAG, "busInfo: " + busInfo.toString());
                    float displayIc = busInfo.getBusCurrentIa();
                    if (displayIc > 21000) {
                        if (!chongfu) {
                            //低压短路
                            isInterunptQb = true;
                            deviceStatus = "13";
                            sendErrorData("03");
                            initDialog_zanting_stop(getResources().getString(R.string.text_dlyc3));//弹出框
                            Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--低压检测中短路了");
                        }
                    }
                }
            }
            if (twoCount > sixExchangeCount && stage == 6 && busInfo != null) {
                if (isJL) {
                    if (!isGyError) {
                        Log.e(TAG, "busInfo: " + busInfo.toString());
                        float displayIc = busInfo.getBusCurrentIa();
                        if (displayIc > 30000) {
                            if (!chongfu) {
                                //高压短路
                                isInterunptQb = true;
                                deviceStatus = "13";
                                sendErrorData("03");
                                initDialog_zanting_stop(getResources().getString(R.string.text_dlyc4));//弹出框
                                Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--高压短路了");
                            }
                        }
                    } else {
                        Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--高压短路了");
                    }
                } else {
                    Log.e(TAG, "busInfo: " + busInfo.toString());
                    float displayIc = busInfo.getBusCurrentIa();
                    if (displayIc > 30000) {
                        if (!chongfu) {
                            //高压短路
                            isInterunptQb = true;
                            deviceStatus = "13";
                            sendErrorData("03");
                            initDialog_zanting_stop(getResources().getString(R.string.text_dlyc4));//弹出框
                        } else {
                            Log.e(TAG,"duanlu_sun:" + duanlu_sun + "--isJl:" + isJL + "--高压短路了");
                        }
                    }
                }
            }

            if (isshow3&&oneCount > gaoya_cankaoSun * 0.5 && busInfo.getBusVoltage() < 6) {
                //电压过低显示弹窗  有线级联通知主控  子机只能做退出操作  因此也是限制起爆
                xzqb();
                //总线电压过低 限制起爆
                sendXzqbData("03");
                deviceStatus = "23";
                isshow3=false;
                Log.e(TAG, secondCount + "----" + JianCe_time * 0.4 + "当前电流：" + busInfo.getBusVoltage());
                Utils.writeRecord("--起爆阶段--:总线短路");
                closeThread();
                AlertDialog dialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_fir_dialog7))//设置对话框的标题//"成功起爆"
                        .setMessage(getResources().getString(R.string.text_fir_dialog8))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog12, which) -> {
                            exitJl();
                            isshow3=false;
                            byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                            sendCmd(reCmd);
                            dialog12.dismiss();
//                                    closeThread();
                            closeForm();
                            finish();
                            MmkvUtils.savecode("isTestDenator", "N");
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                dialog.show();
            }

            if (twoCount - yichang_time > 1 && denatorCount > 5 && stage == 6 && twoCount > gaoya_cankaoSun && busInfo.getBusCurrentIa() < cankao_ic_diya) {
                cuowuSun = cuowuSun + 1;
                yichang_time = twoCount;
                Log.e(TAG, "高压: 1" );
            }

            if (twoCount - yichang_time > 1 && denatorCount > 5 && stage == 6 && twoCount > gaoya_cankaoSun && busInfo.getBusCurrentIa() - cankao_ic_gaoya > cankao_ic_gaoya * 0.3) {//检测电流大于低压参考值记录一次
                cuowuSun = cuowuSun + 1;
                yichang_time = twoCount;
                Log.e(TAG, "低压: 2" );
            }


            if (stage == 7 && cuowuSun >= 3 && isshow == 0 && !isGyError) {
                isshow = 1;
//                firstThread.exit = true;
//                firstThread.interrupt();
//                try {
//                    firstThread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                Utils.writeRecord("---电流波动弹窗---");
                if (isJL) {
                    //电流异常波动显示弹窗
                    sendErrorData("04");
                    deviceStatus = "14";
                    isDlycbdShow = true;
                }
//                AlertDialog dialog = new Builder(FiringMainActivity.this)
                dlycbdDialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_dlyc1))//设置对话框的标题//"成功起爆"
                        .setMessage(getResources().getString(R.string.text_dlyc5))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNeutralButton (getResources().getString(R.string.text_test_exit), (dialog1, which) -> {
                            exitJl();
                            byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                            sendCmd(reCmd);
                            dialog1.dismiss();
                            closeThread();
                            closeForm();
                            finish();
                        })
                        .setNegativeButton(getResources().getString(R.string.text_firing_jixu), (dialog16, i) -> {
//                                firstThread = new ThreadFirst(allBlastQu);
//                                firstThread.exit = false;
//                                firstThread.start();
                            Utils.writeRecord("---电流波动弹窗--继续---");
                            dialog16.dismiss();
                            if (isJL) {
                                isDlycbdShow = false;
                                if (isDlycbdShow) {
                                    sendErrorData("04");
                                    deviceStatus = "14";
                                } else if (isDlgdShow) {
                                    sendErrorData("02");
                                    deviceStatus = "12";
                                } else if (isDlShow) {
                                    sendErrorData("03");
                                    deviceStatus = "13";
                                } else if (isDlycShow) {
                                    sendErrorData("01");
                                    deviceStatus = "11";
                                }  else if (isCheckLgNumShow) {
                                    sendErrorData("07");
                                    deviceStatus = "17";
                                } else {
                                    isInterunptQb = false;
                                    isHaveError = false;
                                    deviceStatus = "05";
                                }
                                sendClycjg();
                            }
                        })
                        .create();
                dlycbdDialog.setCanceledOnTouchOutside(false);
                dlycbdDialog.show();
//                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
//                dialog.show();
            }

            if(busInfo.getPowerStatus().equals("01")){
                //低压异常显示弹窗  有线级联通知主控
                xzqb();
                //低压异常 限制起爆
                sendXzqbData("04");
                deviceStatus = "24";
                Utils.writeRecord("电流状态低压异常");
                closeThread();
                AlertDialog dialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_dyyc))//设置对话框的标题//
                        .setMessage(getResources().getString(R.string.dialog_dyyc))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNeutralButton(getResources().getString(R.string.text_tc), (dialog12, which) -> {
                            exitJl();
                            byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                            sendCmd(reCmd);
                            dialog12.dismiss();
//                                    closeThread();
                            closeForm();
                            finish();
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                if (!FiringMainActivity.this.isFinishing()) {//xActivity即为本界面的Activity
                    dialog.show();
                }
            }
            if(busInfo.getPowerStatus().equals("02")){
                //高压异常显示弹窗  有线级联通知主控
                xzqb();
                //总线高压异常 限制起爆
                sendXzqbData("05");
                if (isJL) {
                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                    sendCmd(reCmd);
                }
                deviceStatus = "25";
                isInterunptQb = true;
                Utils.writeRecord("电流状态高压异常");
                closeThread();
                AlertDialog dialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_gyyc))//设置对话框的标题//"成功起爆"
                        .setMessage(getResources().getString(R.string.dialog_gyyc))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNeutralButton(getResources().getString(R.string.text_tc), (dialog12, which) -> {
                            exitJl();
                            byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                            sendCmd(reCmd);
                            dialog12.dismiss();
//                                    closeThread();
                            closeForm();
                            finish();
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                if (!FiringMainActivity.this.isFinishing()) {//xActivity即为本界面的Activity
                    dialog.show();
                }
            }
            if(busInfo.getPowerStatus().equals("04")){
                //总线存在短路显示弹窗  有线级联通知主控
                xzqb();
                //总线存在短路 限制起爆
                sendXzqbData("06");
                deviceStatus = "26";
                Utils.writeRecord("电流状态短路");
                closeThread();
                AlertDialog dialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_czdl))//设置对话框的标题//"成功起爆"
                        .setMessage(getResources().getString(R.string.dialog_czdl))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNeutralButton(getResources().getString(R.string.text_tc), (dialog12, which) -> {
                            exitJl();
                            byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                            sendCmd(reCmd);
                            dialog12.dismiss();
//                                    closeThread();
                            closeForm();
                            finish();
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                if (!FiringMainActivity.this.isFinishing()) {//xActivity即为本界面的Activity
                    dialog.show();
                }
            }
            cankao_ic_gaoya = busInfo.getBusCurrentIa();//记录参考电流
            //检测电流小于低压参考值记录一次

//            busInfo = null;
            return false;
        });

        checkHandler = new Handler(msg -> {
            String errNumStr = ll_firing_errorAmount_2.getText().toString();
//            String tureNumStr = ll_firing_tureNum.getText().toString();
//            if (tureNumStr.trim().length() < 1) {
//                tureNumStr = "0";
//            }
            ll_firing_errorAmount_2.setText("" + (Integer.parseInt(errNumStr) - 1));
            ll_firing_errorAmount_4.setText("" + (Integer.parseInt(errNumStr) - 1));
            ll_firing_errorAmount_2.setTextColor(Color.GREEN);
            ll_firing_errorAmount_4.setTextColor(Color.GREEN);
            totalerrorNum = Integer.parseInt(errNumStr) + 1;

//            ll_firing_tureNum.setText("" + (Integer.parseInt(tureNumStr) + 1));
//            totaltureNum = Integer.parseInt(tureNumStr) + 1;
            return false;
        });
    }

    AlertDialog dlDialog,dlgdDialog,dlycbdDialog,cdfailDialog,dlycDialog;
    boolean isDlShow = false,isDlgdShow = false,isDlycbdShow = false,isDlycShow = false,
            isCheckLgNumShow = false;

    private void showDlgdDialog() {
        if (isJL) {
            isDlgdShow = true;
        }
        Utils.writeRecord("---电流过大弹窗---");
        dlgdDialog = new Builder(FiringMainActivity.this)
                .setTitle(getResources().getString(R.string.text_dlyc1))//设置对话框的标题//"成功起爆"
                .setMessage(getResources().getString(R.string.text_dlyc2))//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                . setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog1, which) -> {
                    exitJl();
                    sendCmd(ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00"));//35退出起爆
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dialog1.dismiss();
                    closeThread();
                    closeForm();
                    finish();
                })
                .setNegativeButton(getResources().getString(R.string.text_firing_jixu), (dialog15, i) -> {
//                            firstThread = new ThreadFirst(allBlastQu);
//                            firstThread.exit = false;
//                            firstThread.start();
                    if (isJL) {
                        isDlgdShow = false;
                        if (isDlgdShow) {
                            sendErrorData("02");
                            deviceStatus = "12";
                        } else if (isDlycShow) {
                            sendErrorData("01");
                            deviceStatus = "11";
                        }  else if (isDlShow) {
                            sendErrorData("03");
                            deviceStatus = "13";
                        } else if (isDlycbdShow) {
                            sendErrorData("04");
                            deviceStatus = "14";
                        } else if (isCheckLgNumShow) {
                            sendErrorData("07");
                            deviceStatus = "17";
                        }  else {
                            updateJlStatus();
                        }
                        sendClycjg();
                    }
                    dialog15.dismiss();
                })
                .create();
        dlgdDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dlgdDialog.show();
    }
    private void sendErrorData(String errMsgStatus) {
        isHaveError = true;
        isInterunptQb = true;
        int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
        String stureNum = Utils.strPaddingZero(allNum, 3);
        String serrNum = Utils.strPaddingZero(errorNumJl, 3);
        String currentPeak = Utils.strPaddingZero(cPeak, 6);
        EventBus.getDefault().post(new FirstEvent("B8",errMsgStatus,currentPeak,stureNum,serrNum));
    }

    private void zanting() {
        //由于加了暂停线程，停留在按起爆键超过5分钟时“长时间处于高压”的弹窗无法显示，所以屏蔽暂停线程代码
//        Log.e(TAG, "暂停线程:-------------------- ");
//        firstThread.exit = true;
//        firstThread.interrupt();
//        try {
//            firstThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private void jixu() {
        //由于加了暂停线程，停留在按起爆键超过5分钟时“长时间处于高压”的弹窗无法显示，所以屏蔽暂停后重启线程代码
//        Log.e(TAG, "继续线程:------------------ ");
//        firstThread = new ThreadFirst(allBlastQu);
//        firstThread.exit = false;
//        firstThread.start();
    }

    private void setIcView(int red) {
        ll_firing_IC_2.setTextColor(red);
        ll_firing_IC_4.setTextColor(red);
        ll_firing_IC_5.setTextColor(red);
        ll_firing_IC_6.setTextColor(red);
        ll_firing_IC_7.setTextColor(red);
    }

    private void setDialogTextColor(AlertDialog dialog, int red) {
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
        Qibaotime = Integer.parseInt((String) MmkvUtils.getcode("Qibaotime", "5"));
        FiringMainActivity.stage = 0;
        writeVo = null;
        firstWaitCount = 3;
        Wait_Count = 5;
        firstCmdReFlag = 0;
        secondCmdFlag = 0;
        zeroCount = 0;
        zeroCmdReFlag = 0;
//        secondCount = JianCe_time;//第二阶段 计时器
        fourthDisplay = 0;//第4步，是否显示
        thirdWriteCount = 0;//雷管发送计数器
        sevenDisplay = 0;//第7步，是否显示
//        sixExchangeCount = ChongDian_time;//第6阶段计时(充电时间)
        sixCmdSerial = 1;//命令倒计时
        eightCount = Qibaotime;//第8阶段
        neightCount = 0;//
        eightCmdFlag = 0;
        thirdStartTime = 0;//第三阶段每个雷管返回命令计时器
        isshow = 0;//弹窗标志
        reThirdWriteCount = 0;
        totalerrorNum = 0;
        errorNumJl = 0;
        oneCount_max = (denatorCount <= 10) ? 15 : 20;
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

    AlertDialog checkLgNumDialog;
    int checkLgNumFlag;
    //提示对话框,提示有未注册雷管或者未处理雷管
    public void disPlayNoReisterDenator(final int flag) {
        if (isJL) {
            sendErrorData("07");
            deviceStatus = "17";
            isCheckLgNumShow = true;
        }
        this.checkLgNumFlag = flag;
        checkLgNumDialog = new Builder(FiringMainActivity.this)
                .setTitle(getString(R.string.text_alert_tip))
                .setMessage(getString(R.string.text_alert_tip2))//"总线上有未处理的雷管，是否继续起爆？"
                .setPositiveButton(getString(R.string.text_firing_jixu), (dialog, which) -> {
                    if (isJL) {
                        isCheckLgNumShow = false;
                        if (isCheckLgNumShow) {
                            sendErrorData("07");
                            deviceStatus = "17";
                        } else if (isDlgdShow) {
                            sendErrorData("02");
                            deviceStatus = "12";
                        }  else if (isDlShow) {
                            sendErrorData("03");
                            deviceStatus = "13";
                        } else if (isDlycbdShow) {
                            sendErrorData("04");
                            deviceStatus = "14";
                        } else if (isDlycShow) {
                            sendErrorData("01");
                            deviceStatus = "11";
                        } else {
                            updateJlStatus();
                        }
                        sendClycjg();
                    }
                    if (flag == 1) {
                    } else {
                        fourOnlineDenatorFlag = 3;
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(getResources().getString(R.string.text_test_exit), (dialog, which) -> {
                    exitJl();
                    dialog.dismiss();
                    closeThread();
                    closeForm();
                }).create();
        checkLgNumDialog.setCanceledOnTouchOutside(false);
        checkLgNumDialog.show();
        if (flag == 1) {
            increase(6);//第六阶段
        } else {
            fourOnlineDenatorFlag = 3;
        }
    }


    /***
     * 得到错误雷管数
     */
    private void getErrorBlastCount() {
        GreenDaoMaster master = new GreenDaoMaster();
        errlist = master.queryErrLeiGuanNew(qyIdList);//带参数是查一个区域,不带参数是查所有

        int totalNum = errlist.size();//得到数据的总条数
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
        MmkvUtils.savecode("isTestDenator", "N");
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
            case 0:
                ll_1.setVisibility(View.VISIBLE);
                firstTxt.setText((oneCount) + "s");
                secondTxt.setText(getString(R.string.text_firing_tip7) + (oneCount) + "s)");
                break;
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
                fourTxt.setText(getResources().getString(R.string.text_test_tip12));
                Log.e("流程", "10: ");
                break;
            case 11:
                btn_return8.setVisibility(View.GONE);
                break;
            case 12:
                //为了防止起爆结果不返回，要把退出按钮显示出来
                ll_7.setVisibility(View.GONE);
                ll_8.setVisibility(View.VISIBLE);
                break;
            case 13:
                //为了防止起爆结果不返回，要把退出按钮显示出来
                ll_7.setVisibility(View.GONE);
                ll_8.setVisibility(View.VISIBLE);
                break;
        }
    }

    /***
     * 加载错误雷管
     */
    private void loadErrorBlastModel() {
        errDeData.clear();
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryErrLeiGuanNew(qyIdList);//带参数是查一个区域,不带参数是查所有
        for (DenatorBaseinfo d : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("serialNo", d.getBlastserial());
            item.put("konghao", d.getPai()+ "-" +d.getBlastserial() + "-" + d.getDuanNo());
            item.put("shellNo", d.getShellBlastNo());
            item.put("errorName", d.getErrorName());
            item.put("delay", d.getDelay());
            item.put("piece", d.getPiece());
            errDeData.add(item);
        }
        Log.e(TAG, "errDeData: " + errDeData.toString());
    }
    /***
     * 加载充电错误雷管
     */
    private void loadErrCDBlastModel() {
        errDeData.clear();
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryErrLeiGuan_CDNew(qyIdList);//带参数是查一个区域,不带参数是查所有
        for (DenatorBaseinfo d : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("serialNo", d.getBlastserial());
            item.put("konghao", d.getPai()+ "-" +d.getBlastserial() + "-" + d.getDuanNo());
            item.put("shellNo", d.getShellBlastNo());
            item.put("errorName", d.getErrorName());
            item.put("delay", d.getDelay());
            item.put("piece", d.getPiece());
            errDeData.add(item);
        }
        Log.e(TAG, "errDeData: " + errDeData.toString());
    }
    /***
     * 建立错误对话框
     */
    public void createDialog() {
        LayoutInflater inflater = LayoutInflater.from(FiringMainActivity.this);
        View getlistview = inflater.inflate(R.layout.firing_error_listview, null);
        // 给ListView绑定内容
//        ListView listview = getlistview.findViewById(R.id.X_listview);
//        SimpleAdapter adapter = new SimpleAdapter(this, errDeData, R.layout.firing_error_item,
//                new String[]{"serialNo", "shellNo", "errorName", "delay"},
//                new int[]{R.id.X_item_no, R.id.X_item_shellno, R.id.X_item_errorname, R.id.X_item_delay});
//        // 给listview加入适配器
//        listview.setAdapter(adapter);

        // 给ListView绑定内容
        ListView errlistview = getlistview.findViewById(R.id.X_listview);
        ErrListAdapter mAdapter = new ErrListAdapter(this, errDeData, R.layout.firing_error_item);
        errlistview.setAdapter(mAdapter);

        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.text_alert_tablename1));//错误雷管列表
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    /***
     * 加载雷管信息
     */
    private void loadBlastModel() {
        list_all_lg.clear();
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        allBlastQu = new ConcurrentLinkedQueue<>();
        errorList = new ConcurrentLinkedQueue<>();
        GreenDaoMaster master = new GreenDaoMaster();
//        List<DenatorBaseinfo> denatorlist = master.queryDenatorBaseinfo();
        List<DenatorBaseinfo> denatorlist = master.queryDetonatorRegionAscNew(qyIdList);//不分区域
        for (DenatorBaseinfo d : denatorlist) {
            VoDenatorBaseInfo vo = new VoDenatorBaseInfo();
            vo.setBlastserial(d.getBlastserial());
            vo.setDelay((short) d.getDelay());
            vo.setShellBlastNo(d.getShellBlastNo());
            vo.setDenatorId(d.getDenatorId());
            vo.setDenatorIdSup(d.getDenatorIdSup());
            vo.setZhu_yscs(d.getZhu_yscs());
            vo.setCong_yscs(d.getCong_yscs());
            vo.setLgzt(d.getErrorCode());
            allBlastQu.offer(vo);
            list_all_lg.add(vo);
        }
        denatorCount = allBlastQu.size();

        ll_firing_deAmount_4.setText("" + allBlastQu.size());
        ll_firing_deAmount_2.setText("" + allBlastQu.size());
        tv__qb_dianliu_1.setText(denatorCount * cankaodianliu + "μA");
        tv__qb_dianliu_2.setText(denatorCount * cankaodianliu + "μA");
        if (denatorCount <= 200) {
            gaoya_cankaoSun = 25;
            if (denatorCount <= 10) {
                sixExchangeCount = 20;
            }
        } else if (  denatorCount < 300) {
            gaoya_cankaoSun = 30;
            sixExchangeCount = 30;
        } else  {
            gaoya_cankaoSun = 40;
            sixExchangeCount = 40;
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


    /**
     * 保存起爆数据
     */
    public synchronized void saveFireResult() {
        if (denatorCount > 0) {
            int totalNum = (int) getDaoSession().getDenatorBaseinfoDao().count();//得到数据的总条数
//        Log.e(TAG, "saveFireResult-雷管总数totalNum: " + totalNum);
            if (totalNum < 1) return;
            //如果总数大于30,删除第一个数据
            int hisTotalNum = (int) getDaoSession().getDenatorHis_MainDao().count();//得到雷管表数据的总条数
//        Log.e(TAG, "saveFireResult-历史记录条目数hisTotalNum: " + hisTotalNum);
            if (hisTotalNum > 80) {
//            String time = loadHisMainData();
                String time = getFirstTime();
                Message message = new Message();
                message.obj = time;
                mHandler_tip.sendMessage(message);

            }
            String xy[] = pro_coordxy.split(",");//经纬度
            int maxNo = getHisMaxNumberNo();
            maxNo++;
            Project usedProject = Application.getDaoSession().getProjectDao().queryBuilder().where(ProjectDao.Properties.Selected.eq("true")).unique();            String fireDate = hisInsertFireDate;//Utils.getDateFormatToFileName();
            DenatorHis_Main his = new DenatorHis_Main();
            his.setBlastdate(fireDate);
            his.setUploadStatus("未上传");
            his.setRemark("已起爆");
            his.setEqu_no(equ_no);
            his.setUserid(usedProject.getProject_name());
            his.setPro_htid(pro_htid);
            his.setPro_xmbh(pro_xmbh);
            his.setPro_dwdm(pro_dwdm);
            his.setPro_bprysfz(usedProject.getBprysfz());
            his.setSerialNo(Integer.parseInt(qbxm_id));
            his.setLog(Utils.readLog(Utils.getDate(new Date())));
            if (pro_coordxy.length() > 4) {
                his.setLongitude(xy[0]);
                his.setLatitude(xy[1]);
            }
            getDaoSession().getDenatorHis_MainDao().insert(his);//插入起爆历史记录主表
            Utils.deleteRecord();//删除日志
//        Utils.deleteRecord_cmd();//删除cmd日志,删之前要保存到历史记录里面

            List<DenatorBaseinfo> list = new GreenDaoMaster().queryDetonatorRegionAscNew(qyIdList);
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
                denatorHis_detail.setPai(dbf.getPai());
                denatorHis_detail.setDuan(dbf.getDuan());
                denatorHis_detail.setDuanNo(dbf.getDuanNo());
                getDaoSession().getDenatorHis_DetailDao().insert(denatorHis_detail);//插入起爆历史雷管记录表
            }

            Utils.saveFile();//把软存中的数据存入磁盘中
        } else {
            Log.e(TAG,"雷管数量为0,不再生成起爆历史记录");
            AppLogUtils.writeAppLog("雷管数量为0,不再生成起爆历史记录");
        }
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
        int pid = Process.myPid(); // 获取当前进程的ID
        int tid = Process.myTid(); // 获取当前线程的ID
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Log.e("发送命令", str);//pid + "-" + tid +
                Utils.writeLog("->:" + str);
                mOutputStream.write(mBuffer);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void updateDenator(From32DenatorFiring fromData, int writeDelay) {
        if (fromData.getShellNo() == null || fromData.getShellNo().trim().length() < 1) return;
        //greendao更新0817更新
        DenatorBaseinfo denator = Application.getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(fromData.getShellNo())).unique();
        denator.setErrorCode(fromData.getCommicationStatus());
        denator.setErrorName(fromData.getCommicationStatusName());
        //判断雷管状态是否是错误和延时和写入的是否一致
        if ("FF".equals(fromData.getCommicationStatus()) && writeDelay != fromData.getDelayTime()) {
            denator.setErrorCode("01");
            denator.setErrorName("延时写入不一致");
            Log.e("延时不一致", "fromData.getDelayTime(): " + fromData.getDelayTime() + "--writeDelay:" + writeDelay);
        }
        Application.getDaoSession().update(denator);

        //判断雷管状态是否正价错误数量
        if (!"FF".equals(fromData.getCommicationStatus()) || (writeDelay != fromData.getDelayTime())) {
            twoErrorDenatorFlag = 1;
            noReisterHandler.sendMessage(noReisterHandler.obtainMessage());
            errorLgHandler.sendMessage(errorLgHandler.obtainMessage());
//            Log.e("更新雷管状态", "雷管错误状态" + fromData.getCommicationStatus() + "--writeDelay:" + writeDelay + "--fromData.getDelayTime()" + fromData.getDelayTime());
        } else if ("02".equals(fromData.getCommicationStatus())) {
            show_Toast(getString(R.string.text_error_tip51));//桥丝检测不正常
            Utils.writeRecord("--起爆检测错误:" + fromData.toString());
            AppLogUtils.writeAppLog("--起爆检测错误:" + fromData.toString());
        }
        AppLogUtils.writeAppLog("返回延时:" + "管码" + fromData.getShellNo() + "-返回延时" + fromData.getDelayTime() + "-写入延时" + writeDelay);
        Utils.writeRecord("返回延时:" + "管码" + fromData.getShellNo() + "-返回延时" + fromData.getDelayTime() + "-写入延时" + writeDelay);
    }

    /**
     * 38指令更新状态
     */
    public void updateDenator(From38ChongDian fromData) {
        if (fromData.getShellNo() == null || fromData.getShellNo().trim().length() < 1) return;


        //判断雷管状态是否正价错误数量
//        if (!"FF".equals(fromData.getCommicationStatus())) {//只有充电错误更新状态
        DenatorBaseinfo denator = Application.getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(fromData.getShellNo())).unique();
        denator.setStatusCode(fromData.getCommicationStatus());//00
        denator.setErrorName(fromData.getCommicationStatusName());//充电异常
        Application.getDaoSession().update(denator);
        if (!"FF".equals(fromData.getCommicationStatus())) {
            totalerrorCDNum = totalerrorCDNum + 1;
        }

        twoErrorDenatorFlag = 1;
        noReisterHandler.sendMessage(noReisterHandler.obtainMessage());
//        }
        AppLogUtils.writeAppLog("充电状态:" + "管码" + fromData.getShellNo() + "-返回延时" + fromData.getCommicationStatus());
        Utils.writeRecord("充电状态:" + "管码" + fromData.getShellNo() + "-返回延时" + fromData.getCommicationStatus());
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


    AlertDialog registDialog;
    @Override
    protected void onStart() {
        Log.e(TAG, "denatorCount: " + denatorCount);

        long time = System.currentTimeMillis();
        long endTime = (long) MmkvUtils.getcode("endTime", (long) 0);
        Log.e(TAG, "time: " + time);
        Log.e(TAG, "endTime: " + endTime);
        //发送初始化命令
        if (!firstThread.isAlive()) {
            if (denatorCount == 0) {
                if (isJL) {
                    sendErrorData("08");
                    deviceStatus = "18";
                }
                registDialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_fir_dialog23))//设置对话框的标题//"成功起爆"
                        .setMessage(getResources().getString(R.string.text_fir_dialog24))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNeutralButton (getResources().getString(R.string.text_test_exit), (dialog13, which) -> {
                            exitJl();
                            dialog13.dismiss();
                            finish();
                            MmkvUtils.savecode("isTestDenator", "N");
                        })
                        .setNegativeButton(getResources().getString(R.string.text_firing_jixu), (dialog2, which) -> {
                            if (isJL) {
                                isHaveError = false;
                                isInterunptQb = false;
                                deviceStatus = "01";
                                sendClycjg();
                            }
                            dialog2.dismiss();
                            firstThread.start();
                        })
                        .create();
                registDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                registDialog.show();
            } else {
                if (!firstThread.exit) {//级联改成暂停后,出现的问题
                    firstThread.start();
                }


            }

        }

        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            //级联接收命令注册的eventbus
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

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
        int pid = Process.myPid(); // 获取当前进程的ID
        int tid = Process.myTid(); // 获取当前线程的ID
        //pid + "-" + tid +
        Utils.writeLog("<-:" + fromCommad);//找到问题可以把这个进程id去掉
//        AppLogUtils.writeAppXBLog("<-:" + fromCommad);
//        Log.e("返回命令--起爆页面", fromCommad);
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
//            Utils.writeLog("Firing reFrom:" + fromCommad);
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd2(fromCommad);
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                    doWithReceivData(cmd, localBuf);//处理cmd命令

                }
            }
        } else {
            AppLogUtils.writeAppXBLog("芯片返回指令不完整:" + fromCommad);
        }
    }

    /***
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, byte[] locatBuf) {
        if (DefCommand.CMD_1_REISTER_4.equals(cmd)) {//13 收到关闭电源命令
            AppLogUtils.writeAppXBLog("已收到芯片13指令");
            increase(1);
            Log.e(TAG, "increase: 1");
            zeroCmdReFlag = 1;
            byte[] powerCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41
            sendCmd(powerCmd);
//            int pid = Process.myPid(); // 获取当前进程的ID
//            int Tid = Process.myTid(); // 获取当前线程的ID
//            Log.e(TAG, "收到41--1阶段firstCmdReFlag: "+firstCmdReFlag+"  oneCount:"+oneCount +"  stage:"+stage+"  pid:"+pid+"  Tid:"+Tid);
//            Utils.writeRecord("收到41--1阶段firstCmdReFlag: "+firstCmdReFlag+"  oneCount:"+oneCount +"  stage:"+stage+"  pid:"+pid+"  Tid:"+Tid);
            AppLogUtils.writeAppLog("已收到芯片13指令,开始发送41指令");
            Log.e(TAG, "收到13指令，开始发送41指令");
        } else if (DefCommand.CMD_3_DETONATE_1.equals(cmd)) {//30 进入起爆模式
            deviceStatus = "02";//等待检测
            int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
            String stureNum = Utils.strPaddingZero(allNum, 3);
            String serrNum = Utils.strPaddingZero(errorNumJl, 3);
            String currentPeak = Utils.strPaddingZero(cPeak, 6);
            //热点级联发送等待检测
            EventBus.getDefault().post(new FirstEvent("ddjc", stureNum,serrNum,currentPeak));
            //得到电流电压信息
//            byte[] powerCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//00400101获取电源状态指令
//            sendCmd(powerCmd);
//            byte[] powerCmd = ThreeFiringCmd.setToXbCommon_FiringExchange("00");//0038
//            sendCmd(powerCmd);

            //处理返回的起爆模式命令
//            secondCmdFlag = 1;
//            thirdWriteCount = 0;
//            increase(3);
            Log.e(TAG, "收到30指令，进入等待检测阶段");
        } else if (DefCommand.CMD_3_DETONATE_2.equals(cmd)) {//31 写入延时时间，检测结果看雷管是否正常
            From32DenatorFiring fromData = ThreeFiringCmd.decodeFromReceiveDataWriteDelay23_2("00", locatBuf);

            if (fromData != null && writeDenator != null) {
                VoDenatorBaseInfo temp = writeDenator;
                short writeDelay = temp.getDelay();
                fromData.setShellNo(temp.getShellBlastNo());
                fromData.setDenaId(temp.getDenatorId());//芯片码
                Utils.writeRecord("--起爆测试结果:" + fromData);
                AppLogUtils.writeAppLog("--起爆测试结果:" + fromData);
                updateDenator(fromData, writeDelay);//更新雷管状态
                writeDenator = null;
                reThirdWriteCount++;
            }
            Log.e("起爆测试结果", "fromData.toString(): " + fromData.toString());
//            Log.e(TAG, "错误雷管数量--totalerrorNum: " + totalerrorNum);
//            Log.e(TAG, "收到31指令，开始给雷管写入延时");

        } else if (DefCommand.CMD_3_DETONATE_3.equals(cmd)) {//32 充电（雷管充电命令 等待6S（500米线，200发雷管），5.5V充电）
            //发送 高压输出命令
            sixCmdSerial = 2;
            boolean isStopJl = checkIsStopJl();
            if (isStopJl) {
                xzqb();
            } else {
                Log.e(TAG,"zzcd--isInterunptQb:" + isInterunptQb);
                if (!isInterunptQb) {
                    deviceStatus = "04";//正在充电
                    int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
                    String stureNum = Utils.strPaddingZero(allNum, 3);
                    String serrNum = Utils.strPaddingZero(totalerrorNum, 3);
                    String currentPeak = Utils.strPaddingZero(cPeak, 6);
                    //热点级联发送准备充电
                    Log.e(TAG,"正在充电错误数量:" + totalerrorNum);
                    EventBus.getDefault().post(new FirstEvent("zzcd", stureNum,serrNum,currentPeak));
                }
            }
        } else if (DefCommand.CMD_3_DETONATE_4.equals(cmd)) {//33 高压输出（继电器切换，等待12S（500米线，200发雷管）16V充电）
            //收到高压充电完成命令
            //stage=7;
            sixCmdSerial = 3;
            Log.e(TAG, "33指令已返回,充电倒计时值twoCount:" + twoCount);
        } else if (DefCommand.CMD_3_DETONATE_5.equals(cmd)) {//34 起爆
            AppLogUtils.writeAppXBLog("已收到芯片34指令");
            isGetQbResult = true;
            String fromCommad = Utils.bytesToHexFun(locatBuf);
            //C000340100ABCDC0
            String qbzt = fromCommad.substring(8, 10);
            //
            if (!isJL) {
                MmkvUtils.savecode("endTime", System.currentTimeMillis());//起爆完成也更新一下结束时间
            }
//            if (qibaoNoFlag < 5) {
//                Utils.writeRecord("第" + (qibaoNoFlag + 1) + "次发送起爆指令--");
////                Log.e("起爆", "第" + qibaoNoFlag + "次发送起爆指令: ");
//                byte[] initBuf = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_5("00");//34起爆
//                sendCmd(initBuf);
//                qibaoNoFlag++;
//            } else {
            //stage=9;
            eightCmdFlag = 2;
            //获取起爆时间,中爆上传用到了时间,会根据日期截取对应的位数,如果修改日期格式,要同时修改中爆上传方法
            hisInsertFireDate = Utils.getDateFormatLong(new Date());//记录的起爆时间(可以放到更新ui之后,这样会显得快一点)
            saveFireResult();
            //批量更新区域状态为已起爆   该功能屏蔽暂时不做了
            GreenDaoMaster master =  new GreenDaoMaster();
            master.updateQyQbStataus(qyIdList);
            AppLogUtils.writeAppLog("34已返回,生成起爆历史记录");
//            saveFireResult_All();
            if (!qbxm_id.equals("-1")) {
                updataState(qbxm_id);
            }
            if (!isJL) {
                if (!"FF".equals(qbzt) && fromCommad.length() == 16) {
                    Message msg = Handler_tip.obtainMessage();
                    msg.what = 3;
                    msg.obj = qbzt;
                    Handler_tip.sendMessage(msg);
                    deviceStatus = "07";//起爆失败
                } else {
                    deviceStatus = "06";//起爆结束
                    increase(11);//跳到第9阶段
                    Log.e("increase", "9");
                }
            } else {
                boolean isStopJl = checkIsStopJl();
                if (isStopJl) {
                    xzqb();
                    AppLogUtils.writeAppXBLog(MmkvUtils.getcode("ACode", "") + "子设备收到34起爆指令但由于出错不需要起爆");
                    Utils.writeLog(MmkvUtils.getcode("ACode", "") + "子设备收到34起爆指令但由于出错不需要起爆");
                } else {
                    if (!isInterunptQb) {
//                        deviceStatus = "06";//起爆结束
                        //有线级联起爆结束打开串口
//                        EventBus.getDefault().post(new FirstEvent("open485", "B005" + MmkvUtils.getcode("ACode", "") +
//                                deviceStatus + qbResult));
//                        Log.e("起爆结束了", "去重新打开485接口--起爆结果是: " + "B005" + MmkvUtils.getcode("ACode", "") +
//                                deviceStatus + qbResult);
                        EventBus.getDefault().post(new FirstEvent("open485", "Y"));
                        Log.e("起爆结束了", "去重新打开485接口--msg是: open485--data:Y");
                        increase(11);//跳到第9阶段
                        Log.e("increase", "9");
                        AppLogUtils.writeAppXBLog(MmkvUtils.getcode("ACode", "") + "子设备已收到34起爆指令,重新打开485串口");
                        Utils.writeLog(MmkvUtils.getcode("ACode", "") + "子设备已收到34起爆指令,重新打开485串口");
//                        //热点级联起爆结束发消息
//                        int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
//                        String stureNum = Utils.strPaddingZero(allNum, 3);
//                        String serrNum = Utils.strPaddingZero(errorNumJl, 3);
//                        String currentPeak = Utils.strPaddingZero(cPeak, 6);
//                        //热点级联发送起爆结果
//                        EventBus.getDefault().post(new FirstEvent("qbjg", deviceStatus, currentPeak, stureNum, serrNum));
                    }
                }
            }

//                try {
//                    Thread.sleep(50);
//                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35 退出起爆
//                    sendCmd(reCmd);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

        } else if (DefCommand.CMD_3_DETONATE_6.equals(cmd)) {//35 退出起爆模式
            //发出关闭获取得到电压电流
//            byte[] powerCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//40
//            sendCmd(powerCmd);
            Log.e(TAG, "收到35指令，进入退出起爆模式");
        } else if (DefCommand.CMD_3_DETONATE_7.equals(cmd)) {//36 在网读ID检测是否有未注册雷管
            String fromCommad = Utils.bytesToHexFun(locatBuf);
//            String noReisterFlag = ThreeFiringCmd.getCheckFromXbCommon_FiringExchange_5523_7_reval("00", fromCommad);
            String noReisterFlag = ThreeFiringCmd.jiexi_36("00", fromCommad);
            Log.e(TAG, "是否有未注册雷管,返回结果: " + noReisterFlag);
            if (noReisterFlag.equals("00") && errlist.size() != 0) {
                noReisterFlag = "FF";
            }
            //在测试流程,返回都是FF
            if ("FF".equals(noReisterFlag)) {
                fourOnlineDenatorFlag = 3;
            } else {
                fourOnlineDenatorFlag = 2;
                noReisterHandler.sendMessage(noReisterHandler.obtainMessage());
            }

            if (!fromCommad.startsWith("00000000", 10)) {
                if (errlist.size() == 1) {
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

        } else if (DefCommand.CMD_3_DETONATE_8.equals(cmd)) {//37 异常终止起爆
            Log.e(TAG, "收到37指令，异常终止起爆");
        } else if (DefCommand.CMD_3_DETONATE_9.equals(cmd)) {//38 进入充电检测模式
            //处理返回的起爆模式命令
//            secondCmdFlag = 1;
//            thirdWriteCount = 0;
//            increase(3);
//            firstThread.exit=false;
//            firstThread.run();
//            increase(5);
//            mHandler_1.sendMessage(mHandler_1.obtainMessage());

//            Log.e(TAG, "38指令已返回,进入充电检测");

            if (stage == 33||stage == 55) {
                From38ChongDian fromData = ThreeFiringCmd.jiexi_38("00", locatBuf);
                Log.e(TAG, "雷管充电状态: "+fromData.getCommicationStatus()+" -- "+fromData.getCommicationStatus() );
                if (fromData != null && writeDenator != null) {
                    VoDenatorBaseInfo temp = writeDenator;
                    fromData.setShellNo(temp.getShellBlastNo());
                    fromData.setDenaId(temp.getDenatorId());//芯片码
                    Utils.writeRecord("--起爆测试结果:" + fromData);
                    AppLogUtils.writeAppLog("38指令--起爆测试结果:" + fromData);
//                    updateDenator(fromData);//更新雷管状态
                    writeDenator = null;
                }
                reThirdWriteCount++;
            }
        } else if (DefCommand.CMD_3_DETONATE_10.equals(cmd)) {//39 进入充电复检模式

            From38ChongDian fromData = ThreeFiringCmd.jiexi_39("00", locatBuf);
            VoDenatorBaseInfo temp = writeDenator;
            if (fromData != null && writeDenator != null) {
                fromData.setShellNo(temp.getShellBlastNo());
                fromData.setDenaId(temp.getDenatorId());//芯片码
                Utils.writeRecord("--起爆测试结果:" + fromData);
                AppLogUtils.writeAppLog("39指令--起爆测试结果:" + fromData);
//                updateDenator(fromData);//更新雷管状态
                writeDenator = null;
                reThirdWriteCount++;
            }
            Log.e(TAG, "fromData.getCommicationStatus(): " + fromData.getCommicationStatus());
            if (fromData.getCommicationStatus().equals("00")) {
                Message msg = new Message();
                msg.what = 5;
                msg.obj = temp.getShellBlastNo();
                Handler_tip.sendMessage(msg);
            }

        } else if (DefCommand.CMD_4_XBSTATUS_1.equals(cmd)) {//40 获取电源状态指令
            busInfo = FourStatusCmd.decodeFromReceiveDataPower24_1("00", locatBuf);
            list_dianliu.add(busInfo.getBusCurrentIa());
//            Log.e(TAG, "list_dianliu: " + list_dianliu.size() + "--" + list_dianliu);
            if (busHandler != null) {
                busHandler.sendMessage(busHandler.obtainMessage());
            } else {
                Utils.writeRecord("busHandler为空");
                AppLogUtils.writeAppLog("40指令解析busHandler为空");
            }
//            if (!isJL) {
//                if (stage == 8 && eightCount != Qibaotime) {
//                    Log.e(TAG, "case8按1+5后的电流: " + busInfo.getBusCurrentIa() + "--beforeC:" + befor_dianliu +
//                            "--电压：" + busInfo.getBusVoltage() + "--beforeV:" + befor_dianya);
//                    if (isDifferenceWithin(busInfo.getBusCurrentIa(), befor_dianliu, 90, 1) ||
//                            isDifferenceWithin(busInfo.getBusVoltage(), befor_dianya, 10, 2)) {
//                        Log.e(TAG, "case8电流或者电压不稳定,需延长5秒轮训40指令，页面上显示起爆中");
//                        increase(13);
//                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                    } else {
//                        Log.e(TAG, "case8电流电压稳定，可以发34指令");
//                        eightCmdExchangePower = 1;
//                    }
//                }
//                if (stage == 13 && thirteenCount != 5) {
//                    Log.e(TAG, "case13thirteenCount：" + thirteenCount + "--起爆中的电流: " + busInfo.getBusCurrentIa()
//                            + "--beforeC:" + befor_dianliu + "--电压：" + busInfo.getBusVoltage() + "--beforeV:" + befor_dianya);
//                    if (isDifferenceWithin(busInfo.getBusCurrentIa(), befor_dianliu, 90, 1) &&
//                            isDifferenceWithin(busInfo.getBusVoltage(), befor_dianya, 10, 2)) {
//                        isCasePeakWd = true;
//                        isCaseVoltageWd = true;
//                        Log.e(TAG, "case13电压和电流都不稳定,需展示出强制起爆的dialog");
//                        increase(14);
//                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                    } else if (isDifferenceWithin(busInfo.getBusCurrentIa(), befor_dianliu, 90, 1)) {
//                        isCasePeakWd = true;
//                        isCaseVoltageWd = false;
//                        Log.e(TAG, "case13电流不稳定,需展示出强制起爆的dialog");
//                        increase(14);
//                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                    } else if (isDifferenceWithin(busInfo.getBusVoltage(), befor_dianya, 10, 2)) {
//                        isCasePeakWd = false;
//                        isCaseVoltageWd = true;
//                        Log.e(TAG, "case13电压不稳定,需展示出强制起爆的dialog");
//                        increase(14);
//                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                    } else {
//                        Log.e(TAG, "case13电流电压稳定，可以发34指令");
//                        isCasePeakWd = false;
//                        isCaseVoltageWd = false;
//                        thirteenCmdExchangePower = 1;
//                    }
//                }
//                befor_dianliu = busInfo.getBusCurrentIa();
//                befor_dianya = busInfo.getBusVoltage();
//            }
        } else if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//41 切换电源
            //说明打开电源命令成功
            if (FiringMainActivity.stage == 1) {
                firstCmdReFlag = 1;
                if (changjia.equals("CQ")) {
                    sendCmd(FourStatusCmd.send46("00", "03", denatorCount));//20(第一代)
                } else {
                    sendCmd(FourStatusCmd.send46("00", "01", denatorCount));//20(第一代)
                }
            }
            if (FiringMainActivity.stage == 8) {
                byte[] initBuf = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_5("00");//34  起爆
                sendCmd(initBuf);
                Utils.writeRecord("第一次发送起爆指令--");
                AppLogUtils.writeAppLog("第一次发送起爆指令--");
            }
        } else if (DefCommand.CMD_4_XBSTATUS_7.equals(cmd)) {
            Log.e("起爆页面", "收到46指令成功切换版本");
        } else {
//            Log.e("起爆页面", "返回命令没有匹配对应的命令-cmd: " + cmd);
        }

    }

    public boolean isDifferenceWithin(float int1, float int2, int cha, int type) {
        float difference = Math.abs(int1 - int2);
        if (type == 1) {
            Log.e(TAG, "倒计时的电流差值： " + difference);
        } else {
            Log.e(TAG, "倒计时的电压差值： " + difference);
        }
        return difference > cha;
    }

    public synchronized void increase(int val) {
//        Log.e(TAG, "increase--改变stage: " + val);
        stage = val;
    }

    private boolean isEnterQbDjs = false;//级联是否已经进入起爆倒计时
    @SuppressLint("SetTextI18n")
    public void execStage(Message msg) {//页面切换
        switch (stage) {
            case 0:
                if (startFlag == 1) {

                }
                break;
            case 1:
                firstTxt.setText((oneCount) + "s");
                secondTxt.setText(getString(R.string.text_firing_tip7) + (oneCount) + "s)");
                if (isJL) {
                    if (oneCount >= 3) {//等待结束
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
                        if (firstCmdReFlag == 1) {
                            ctlLinePanel(2);
                            increase(2);
                            Log.e("increase", "2");
                        }
                    }
                } else {
                    if (oneCount == 3) {//等待结束
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
                        if (firstCmdReFlag == 1) {
                            ctlLinePanel(2);
                            increase(2);
                            Log.e("increase", "2");
                        }
                    }
                }


                break;
            case 2:
                secondTxt.setText(getString(R.string.text_firing_tip7) + (oneCount) + "s)");//"测试准备 ("
                break;
            case 3:
                if (thirdWriteErrorDenator != null) {//写入未返回的错误雷管
                    show_Toast(thirdWriteErrorDenator.getShellBlastNo() + "芯片写入命令未返回");
                    thirdWriteErrorDenator = null;//设置错误雷管
                }
                //看着这个方法有点问题
//                if (errorList != null && errorList.size() >= 0) {
//                    while (!errorList.isEmpty()) {//写入错误雷管
//                        VoFiringTestError er = errorList.poll();
//                        if (er != null) {
//                            From32DenatorFiring df = new From32DenatorFiring();
//                            df.setShellNo(er.getShellBlastNo());
//                            df.setCommicationStatus("AF");
//                            df.setDelayTime(er.getDelay());
//                            this.updateDenator(df, er.getDelay());
//                        }
//                    }
//                }
                secondTxt.setText(getString(R.string.text_firing_tip9) + thirdWriteCount + getString(R.string.text_firing_tip10));
                break;
            case 4:
                if (dengdai) {
                    int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
                    String stureNum = Utils.strPaddingZero(allNum, 3);
                    String serrNum = Utils.strPaddingZero(errorNumJl, 3);
                    String currentPeak = Utils.strPaddingZero(cPeak, 6);
                    //级联发送检测结果
                    boolean isStopJl = checkIsStopJl();
                    if (isStopJl) {
                        if (!isHaveError) {
                            xzqb();
                            sendXzqbData("01");
                            deviceStatus = "21";
                        }
                    } else {
                        Log.e(TAG,"检测结束--isHaveError:" + isHaveError + "--deviceStatus:" + deviceStatus);
                        if (!isHaveError) {
                            //有异常弹窗处理了才可更新状态
                            deviceStatus = "03";//检测结束
                            EventBus.getDefault().post(new FirstEvent("jcjg", stureNum, serrNum, currentPeak));
                            Log.e("eventBus开始传送"+"jcjg数据", "currentPeak: " + currentPeak);
                        }
                    }
                    ctlLinePanel(4);//修改页面显示项
                    getErrorBlastCount();
                    fourthDisplay = 1;
                    dengdai = false;
                }
                Log.e("错误数量", "totalerrorNum: " + totalerrorNum);
                //disPlayNoReisterDenator();
//                Log.e(TAG, "busInfo.getBusCurrentIa(): " + busInfo.getBusCurrentIa());
//                if (totalerrorNum == denatorCount && busInfo.getBusCurrentIa() > 4800) {//大于4000u ，全错
//                    Log.e(TAG, "大于4000u ，全错: ");
//                    if (chongfu) {
//                        initDialog_zanting("请检查线夹等部位是否有进水进泥等短路情况,确认无误后点继续进行重新检测。");//弹出框
//                    } else {
//                        initDialog("当前有雷管检测错误,系统正在进行2次检测,如果依然检测错误,请检查线夹等部位是否有进水进泥等短路情况,确认无误后点击继续进行检测。");//弹出框
//                    }
//                } else if (totalerrorNum == denatorCount && busInfo.getBusCurrentIa() < 4800) {//小于4000u ，全错
//
//                    if (chongfu) {
//                        initDialog_zanting("请检查线夹等部位是否有进水进泥等短路情况,确认无误后点继续进行重新检测。");//弹出框
//                    } else {
//                        initDialog("当前有雷管检测错误,系统正在进行2次检测,如果依然检测错误,请检查线夹等部位是否有进水进泥等短路情况");//弹出框
//                    }
//
//                    Log.e(TAG, "小于4000u ，全错: stage=" + stage);
//                } else if (totalerrorNum < denatorCount && totalerrorNum != 0 && busInfo.getBusCurrentIa() < (denatorCount * 12 + 100)) {//小于参考值 ，部分错
//                    if (chongfu) {
//                        initDialog_zanting2("请查看错误雷管列表,更换错误雷管后,点击继续按钮进行重新检测!");//弹出框
//                    } else {
//                        initDialog_zanting2("请查错误的雷管是否正确连接!检查无误后,点击继续重新检测。");//弹出框
//                    }
//                    Log.e(TAG, "小于参考值 ，部分错: stage=" + stage + "-totalerrorNum:" + totalerrorNum + "-denatorCount:" + denatorCount);
//                } else if (totalerrorNum < denatorCount && totalerrorNum != 0 && busInfo.getBusCurrentIa() > (denatorCount * 12 + 100)) {//大于参考值 ，部分错
//                    if (chongfu) {
//                        initDialog_zanting2("请更换错误雷管,检查无误后,点击继续进行重新检测。");//弹出框
//                    } else {
//                        initDialog_zanting2("请检查错误的雷管是否存在线夹进水进泥等情况!检查无误后点击确定重新检测。");//弹出框
//                    }
//                    Log.e(TAG, "大于参考值 ，部分错: stage=" + stage);
//                }

                break;
            case 5:
                secondTxt.setText(getString(R.string.text_firing_tip7) + oneCount + "s)");//"充电检测 ("
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
                sixTxt.setText(getString(R.string.text_firing_tip4) + "(" + twoCount + ")");//"正在充电，请稍后 \n"
//                if (twoCount == 100) {
//                    Log.e(TAG, "未跳转按1+5阶段twoCount: " + twoCount);
//                    AlertDialog dialog = new Builder(this)
//                            .setTitle("高压充电失败")//设置对话框的标题//"成功起爆"
//                            .setMessage("起爆器高压充电失败,请再次启动起爆流程,进行起爆")//设置对话框的内容"本次任务成功起爆！"
//                            //设置对话框的按钮
//                            .setNegativeButton("退出", (dialog1, which) -> {
//                                dialog1.dismiss();
//                                closeThread();
//                                closeForm();
//                                finish();
//                                MmkvUtils.savecode("isTestDenator", "N");
//                            })
//                            .create();
//                    dialog.show();
//                }
                break;
            case 7:
                ctlLinePanel(7);
                if (sevenCount == 300) {
                    if (isJL) {
                        xzqb();
                        //长时间处于高压 限制起爆
                        sendXzqbData("07");
                        isGyError = true;
                        deviceStatus = "27";
                    }
                    sendCmd(ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00"));//35退出起爆
                    AlertDialog dialog = new Builder(this)
                            .setTitle(getResources().getString(R.string.text_alert_tip))//设置对话框的标题//"成功起爆"
                            .setMessage(getResources().getString(R.string.text_fir_dialog21))//设置对话框的内容"本次任务成功起爆！"
                            //设置对话框的按钮
                            .setNeutralButton(getString(R.string.text_test_exit), (dialog12, which) -> {
                                exitJl();
                                dialog12.dismiss();
                                finish();
                                MmkvUtils.savecode("isTestDenator", "N");
                            })
                            .create();
                    dialog.show();
                }
                break;
            case 8:
                Log.e(TAG, "eightCount: " + eightCount);
                ctlLinePanel(8);
                eightTxt.setText(getString(R.string.text_firing_tip13) + eightCount + "s");//"倒计时\n"
                break;
            case 9://起爆之后,弹出对话框
                if (isJL) {
                    deviceStatus = "06";//起爆结束
                    Utils.writeLog(MmkvUtils.getcode("ACode", "") + "子设备起爆成功,该展示出起爆成功弹窗了");
                    AppLogUtils.writeAppXBLog(MmkvUtils.getcode("ACode", "") + "子设备起爆成功,该展示出起爆成功弹窗了");
                    EventBus.getDefault().post(new FirstEvent("qbjs", "B005" + MmkvUtils.getcode("ACode", "") +
                            deviceStatus + qbResult));
                    Log.e("起爆结束了", "子机将起爆结果发送给主控--起爆结果是: " + "B005" + MmkvUtils.getcode("ACode", "") +
                            deviceStatus + qbResult);
                    //热点级联起爆结束发消息
                    int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
                    String stureNum = Utils.strPaddingZero(allNum, 3);
                    String serrNum = Utils.strPaddingZero(errorNumJl, 3);
                    String currentPeak = Utils.strPaddingZero(cPeak, 6);
                    //热点级联发送起爆结果
                    EventBus.getDefault().post(new FirstEvent("qbjg", deviceStatus, currentPeak, stureNum, serrNum));
                }
                eightTxt.setText(R.string.text_firing_qbcg);//"起爆成功！"
                Log.e("起爆成功", "显示出最后的弹窗");
                if(!checkRepeatHis(hisInsertFireDate)){//弹上传的时候再判断一下是否成功生成历史记录了
                    saveFireResult();
                    Utils.writeRecord("--生成历史记录");
                    AppLogUtils.writeAppLog("起爆历史记录未生成，开始生成起爆记录");
                    Log.e(TAG,"起爆历史记录未生成，开始生成起爆记录");
                } else {
                    AppLogUtils.writeAppLog("起爆历史记录已生成，无需再次生成");
                    Log.e(TAG,"起爆历史记录已生成，无需再次生成");
                }
                if (eightCmdFlag == 2) {
                    eightCmdFlag = 0;

                    AlertDialog dialog = new Builder(this)
                            .setTitle(getString(R.string.text_firing_tip15))//设置对话框的标题//"成功起爆"
                            .setMessage(getString(R.string.text_firing_tip16))//设置对话框的内容"本次任务成功起爆！"
                            //设置对话框的按钮
                            .setNeutralButton(getString(R.string.text_test_exit), (dialog13, which) -> {
                                exitJl();
                                dialog13.dismiss();
                                finish();
                                //本次起爆结束  isTestDenator也置为N
                                MmkvUtils.savecode("isTestDenator", "N");
                            })
                            .setPositiveButton(getString(R.string.text_firing_tip17), (dialog14, which) -> {
                                Intent intent = new Intent(FiringMainActivity.this, QueryHisDetail.class);
                                startActivityForResult(intent, 1);
                                dialog14.dismiss();
                                closeThread();
                                closeForm();
                            }).create();
                    if (!FiringMainActivity.this.isFinishing()) {//xActivity即为本界面的Activity
                        dialog.show();
                    }
                }
                break;
            case 10://跳转到查看错误雷管和继续阶段
                Log.e(TAG, "execStage: 10");
                if (totalerrorNum == 0) {
                    stopXunHuan();
                } else if (totalerrorNum == denatorCount && busInfo.getBusCurrentIa() > 21000) {//大于4800u ，全错
                    Log.e(TAG, "大于4000u ，全错: ");
                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                    sendCmd(reCmd);
                    if (chongfu) {
                        initDialog_zanting(getResources().getString(R.string.text_fir_dialog13));//弹出框
                    } else {
                        initDialog(getResources().getString(R.string.text_fir_dialog10), 5);//弹出框
                    }
                } else if (totalerrorNum == denatorCount && busInfo.getBusCurrentIa() < 21000) {//小于4800u ，全错
                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                    sendCmd(reCmd);
                    if (chongfu) {
                        initDialog_zanting(getResources().getString(R.string.text_fir_dialog11));//弹出框
                    } else {
                        initDialog(getResources().getString(R.string.text_fir_dialog12), 5);//弹出框
                    }

                    Log.e(TAG, "小于4000u ，全错: stage=" + stage);
                } else if (totalerrorNum < denatorCount && totalerrorNum != 0 && busInfo.getBusCurrentIa() < denatorCount * cankaodianliu + 100) {//小于参考值 ，部分错
                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                    sendCmd(reCmd);
                    if (chongfu) {
                        initDialog_zanting2(getResources().getString(R.string.text_fir_dialog14));//弹出框
                    } else {
                        initDialog_zanting2(getResources().getString(R.string.text_fir_dialog15));//弹出框
                    }
                    Log.e(TAG, "小于参考值 ，部分错: stage=" + stage);
                } else if (totalerrorNum < denatorCount && totalerrorNum != 0 && busInfo.getBusCurrentIa() > (denatorCount * cankaodianliu + 100)) {//大于参考值 ，部分错
                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                    sendCmd(reCmd);
                    if (chongfu) {
                        initDialog_zanting2(getResources().getString(R.string.text_fir_dialog16));//弹出框
                    } else {
                        initDialog_zanting2(getResources().getString(R.string.text_fir_dialog25));//弹出框
                    }
                    Log.e(TAG, "大于参考值 ，部分错: stage=" + stage);
                } else {
                    stopXunHuan();//检测完成
                }
                break;
            case 11://给范总加的起爆后的放电阶段
                Log.e("进入放电阶段", "显示起爆中view");
                btn_return8.setVisibility(View.GONE);
//                if (isSendWaitQb) {
//                    Log.e("切换模式下", "显示view");
//                    eightTxt.setText(getString(R.string.text_firing_qbz));
//                } else {
                    Log.e("case11", "显示起爆中view");
                    eightTxt.setText(getString(R.string.text_firing_qbz) + elevenCount + "s");
//                }
                break;
            case 12:
                //时钟校验中 显示出起爆中文字
                ctlLinePanel(12);
                Log.e("进入时钟校验阶段", "显示起爆中view");
                eightTxt.setText(getString(R.string.text_firing_qbz));
                break;
            case 13:
                //组合键5秒倒计时后，多检测5秒电流稳定性
                //时钟校验中 显示出起爆中文字
                ctlLinePanel(13);
                Log.e("进入时钟校验阶段", "显示起爆中view");
                eightTxt.setText(getString(R.string.text_firing_qbz));
                break;
            case 14:
                String content;
                if (isCasePeakWd && isCaseVoltageWd) {
                    content = "电流和电压都";
                } else if (isCasePeakWd) {
                    content = "电流";
                } else {
                    content = "电压";
                }
                //起爆时电流不稳定，此时dialog提示用户
                AlertDialog dialog = new Builder(FiringMainActivity.this)
                        .setTitle(getResources().getString(R.string.text_alert_tip))//设置对话框的标题
                        .setCancelable(false)
                        .setMessage(getResources().getString(R.string.text_dlyc6))//设置对话框的内容
                        //设置对话框的按钮
                        .setNeutralButton (getResources().getString(R.string.text_tc), (dialog1, which) -> {
                            exitJl();
                            byte[] exCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35 退出起爆
                            sendCmd(exCmd);
                            dialog1.dismiss();
                            finish();
                            MmkvUtils.savecode("isTestDenator", "N");
                        })
                        .setNegativeButton(getResources().getString(R.string.text_dialog_jxqb), (dialog15, which) -> {
                            Utils.writeRecord("---1+5阶段电流不稳定强制起爆---");
                            dialog15.dismiss();
                            //发出34 起爆命令
                            byte[] qbCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_5("00");
                            sendCmd(qbCmd);
                            Log.e("起爆", "第一次发送起爆指令: ");
                            eightCmdFlag = 1;
                        }).create();
                dialog.show();
                Log.e("case14", "显示起爆电流不稳定dialog");
                break;
            case 99://暂停阶段
                break;
            case 33:
                ctlLinePanel(6);
                sixTxt.setText(getResources().getString(R.string.text_fir_dialog27) + thirdWriteCount + getResources().getString(R.string.text_fir_dialog28));
                break;

            case 44:
                if (thirdWriteCount == 1) {
                    ll_firing_errorAmount_2.setText("0");
                }
                if (thirdWriteErrorDenator2 != null) {//写入未返回的错误雷管
                    show_Toast(thirdWriteErrorDenator2.getShellBlastNo() + getResources().getString(R.string.text_xpwfh));
                    thirdWriteErrorDenator2 = null;//设置错误雷管
                }

                secondTxt.setText(getResources().getString(R.string.text_cfd) + thirdWriteCount + getResources().getString(R.string.text_firing_err_lg));
                //写入通信未返回

                break;
            case 55:
                ctlLinePanel(6);
                sixTxt.setText(getResources().getString(R.string.text_cfd) + thirdWriteCount + getResources().getString(R.string.text_fir_dialog28));
                break;
            case 66:
                ctlLinePanel(6);
                sixTxt.setText(getResources().getString(R.string.text_fjd) + thirdWriteCount + getResources().getString(R.string.text_fld));
                break;
            default:

        }

    }

    private void exitJl(){
        if (isJL) {
            isQbFail = true;
            deviceStatus = "08";
            sendXzqbData("08");
        }
    }
    private long zeroStartTime = 0;//第1阶段每个雷管返回命令计时器
    private Handler handler = new Handler(); // Handler to post delayed tasks
    /***
     * 全部
     * @author zenghp
     *
     */
    private class ThreadFirst extends Thread {
        public volatile boolean exit = false;
        private VoDenatorBaseInfo tempBaseInfo = null;
        private VoDenatorBaseInfo tempBaseInfo2 = null;
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
                                zeroStartTime = System.currentTimeMillis();
                                byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");//13
                                sendCmd(powerCmd);
                                Log.e(TAG, "第一次发送13指令");
                            }
                            if (System.currentTimeMillis() - zeroStartTime > 1200) {
                                //13返回未超时  重发13
                                byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");//13
                                sendCmd(powerCmd);
                                zeroStartTime = System.currentTimeMillis();
                                Log.e(TAG, "1.2秒内没收到芯片回复13,重发13指令");
                                AppLogUtils.writeAppLog("case0-1.2秒内没收到芯片回复13,重发13指令");
                            }
//                            increase(1);
//                            Log.e(TAG, "increase: 1");
//                            zeroCmdReFlag = 1;
//                            sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41
                            if (zeroCmdReFlag == 1) {
                                break;
                            }
                            zeroCount++;
                            if (zeroCount > 50) {//等待时间答应5秒，退出
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                exit = true;
                                AppLogUtils.writeAppLog("case0-等待时间一直无响应,退出起爆流程");
                            }
                            Log.e(TAG,"case0--zeroCount: " + zeroCount);

                            break;
                        case 1://等待总线稳定时间
                            Thread.sleep(1000);
                            oneCount++;
                            //说明电源打开命令未返回
                            int pid = Process.myPid(); // 获取当前进程的ID
                            int Tid = Process.myTid(); // 获取当前线程的ID
                            Log.e(TAG, "1阶段firstCmdReFlag: " + firstCmdReFlag + "  oneCount:" + oneCount + "  stage:" + stage + "  pid:" + pid + "  Tid:" + Tid);
                            Utils.writeRecord("1阶段firstCmdReFlag: " + firstCmdReFlag + "  oneCount:" + oneCount + "  stage:" + stage + "  pid:" + pid + "  Tid:" + Tid);
                            if (firstCmdReFlag == 0 && oneCount > 1) {
                                exit = true;
                            }
//                            if (firstWaitCount > 1) {
//                                sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40 获取电源状态指令
//                            }
                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;

                        case 2://
//                            Log.e(TAG, "----------------------------: " );
//                            Log.e(TAG, "oneCount_panduan: "+oneCount_panduan );
//                            Log.e(TAG, "oneCount: "+oneCount );
//                            Log.e(TAG, "oneCount_max: "+oneCount_max );
//                            Log.e(TAG, "JianCe_time: "+JianCe_time );
                            //发出进入起爆模式命令  准备测试计时器    报错
                            if (oneCount >= oneCount_max && secondCmdFlag == 0) {//
                                if (list_dianliu.size() > 5 && list_dianliu.get(list_dianliu.size() - 1) - list_dianliu.get(list_dianliu.size() - 5) < 20 && list_dianliu.get(list_dianliu.size() - 1) - list_dianliu.get(list_dianliu.size() - 5) > -20) {
                                    oneCount_panduan++;
                                    Log.e(TAG, "oneCount_max:" + oneCount_max + "--oneCount_panduan: "+oneCount_panduan );
                                    if(oneCount_panduan>=5){
                                        cankao_ic_diya = busInfo.getBusCurrentIa();//低压参考电流值
                                        byte[] powerCmd = ThreeFiringCmd.setToXbCommon_FiringExchange("00");//0038充电
                                        sendCmd(powerCmd);
                                        increase(5);
                                        Log.e("第5阶段-increase", "5");
                                        Log.e("充电检测WaitCount", Wait_Count + "");
                                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    }
                                }else {
                                    sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40 获取电源状态指令
//                                    int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
//                                    String stureNum = Utils.strPaddingZero(allNum, 3);
//                                    String serrNum = Utils.strPaddingZero(errorNumJl, 3);
//                                    String currentPeak = Utils.strPaddingZero(cPeak, 6);
//                                    EventBus.getDefault().post(new FirstEvent("ddjc", stureNum,serrNum,currentPeak));
                                }
//                                else if(list_dianliu.size() < 5 ){
//                                    cankao_ic_diya = busInfo.getBusCurrentIa();//低压参考电流值
//                                    byte[] powerCmd = ThreeFiringCmd.setToXbCommon_FiringExchange("00");//0038充电
//                                    sendCmd(powerCmd);
//                                    increase(5);
//                                }

                                //时间到达最大值,就直接跳转
                                if (oneCount >= JianCe_time) {
                                    byte[] powerCmd = ThreeFiringCmd.setToXbCommon_FiringExchange("00");//0038充电
                                    sendCmd(powerCmd);
                                    increase(5);
                                    Log.e("第5阶段-increase", "5");
                                    Log.e("充电检测WaitCount", Wait_Count + "");
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                }
                            } else {//
                                //得到电流电压信息
                                sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40 获取电源状态指令
                                int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
                                String stureNum = Utils.strPaddingZero(allNum, 3);
                                String serrNum = Utils.strPaddingZero(errorNumJl, 3);
                                String currentPeak = Utils.strPaddingZero(cPeak, 6);
                                EventBus.getDefault().post(new FirstEvent("ddjc", stureNum,serrNum,currentPeak));
                            }
                            Thread.sleep(1000);
                            oneCount++;
                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;
                        case 3://写入延时时间，检测结果看雷管是否正常

                            if (reThirdWriteCount == thirdWriteCount) {//判断是否全部测试完成
                                Log.e("第4阶段-increase", "thirdWriteCount:" + thirdWriteCount);
//                                Thread.sleep(50);
                                thirdStartTime = 0;
                                writeDenator = null;
                                //检测一次
                                if (blastQueue == null || blastQueue.size() < 1) {

                                    Log.e("第4阶段-increase", "4-2");
                                    if (denatorCount >= 300 && totalerrorNum != 0) {
                                        Handler_tip.sendMessage(Handler_tip.obtainMessage(6));//重置错误数量
                                        getErrblastQueue();//获取错误列表 重新给雷管队列赋值
                                        increase(44);//之前是4
                                    } else {
                                        increase(4);//之前是4
                                        getblastQueue();//重新获取数据,用来充电
                                        int a = Integer.parseInt(ll_firing_errorAmount_2.getText().toString());
                                        GreenDaoMaster master = new GreenDaoMaster();
                                        errlist = master.queryErrLeiGuan(mRegion);//带参数是查一个区域,不带参数是查所有
                                        if (a == 1 && errlist != null && errlist.size() > 0) {
                                            sendCmd(ThreeFiringCmd.send_36("00", errlist.get(0).getZhu_yscs()));//36 在网读ID检测
                                        } else {
                                            sendCmd(ThreeFiringCmd.send_36("00", "0000"));//36 在网读ID检测
                                        }
                                    }

//                                    sendCmd(ThreeFiringCmd.setToXbCommon_FiringExchange_5523_7("00"));//36 在网读ID检测
                                    fourOnlineDenatorFlag = 0;
                                    break;
                                }

                                //检测两次
//                                if (blastQueue == null || blastQueue.size() < 1) {//检测结束后的操作
//                                    //如果过错误数量不为为0才发第二次
////                                    if(!ll_firing_errorAmount_2.getText().equals("0")){
////                                        //检测一次
////                                        increase(4);//之前是4
////                                        Log.e("第4阶段-increase", "4-2");
////                                    }else {
//                                    Log.e("雷管队列数量", "blastQueue.size():" + blastQueue.size());
//                                    Utils.writeRecord("--第一轮检测结束-------------");
//                                    //检测两次
//                                    getblastQueue();
//                                    Thread.sleep(1000);//在第二次检测前等待1s
//                                    increase(33);//之前是4
//                                    totalerrorNum = 0;//重置错误数量
////                                  }
//
//                                    fourOnlineDenatorFlag = 0;
//                                    break;
//                                }
                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo = write;

                                String shellStr = write.getShellBlastNo();
                                if (shellStr == null || shellStr.length() != 13)
                                    continue;//// 判读是否是十三位
                                if (write.getDenatorId() == null || write.getDenatorId().length() < 8) {
                                    Message msg = Handler_tip.obtainMessage();
                                    msg.what = 2;
                                    Bundle b = new Bundle();
                                    msg.setData(b);
                                    Handler_tip.sendMessage(msg);
                                    closeThread();
                                    break;
                                }
//                                String denatorId = Utils.DetonatorShellToSerialNo_new(shellStr);//新协议
//                                String denatorId = Utils.DetonatorShellToSerialNo(shellStr);//旧协议
                                String denatorId = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorId());//新芯片
                                denatorId = Utils.getReverseDetonatorNo(denatorId);

                                short delayTime = write.getDelay();
                                byte[] delayBye = Utils.shortToByte(delayTime);
                                String delayStr = Utils.bytesToHexFun(delayBye);//延时时间
                                String zhuangtai = write.getLgzt();
                                if (zhuangtai == null) {
                                    zhuangtai = "00";
                                }
                                if (denatorCount < 200) {//雷管数量小于200,全部00
                                    zhuangtai = "00";
                                }
                                //电流小于参考值一半,全部00
                                if (busInfo.getBusCurrentIa() < denatorCount * cankaodianliu * 0.5) {
                                    zhuangtai = "00";
                                }
                                String data = denatorId + delayStr + write.getZhu_yscs() + "00";
                                if (write.getDenatorIdSup() != null && write.getDenatorIdSup().length() > 4) {
                                    String denatorIdSup = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorIdSup());//新芯片
                                    denatorIdSup = Utils.getReverseDetonatorNo(denatorIdSup);
                                    data = denatorId + delayStr + write.getZhu_yscs() + denatorIdSup + write.getCong_yscs();
                                    Utils.writeRecord("--设置雷管延时:" + "主芯片:" + denatorId + "延时:" + delayStr + "从芯片:" + denatorIdSup);
                                }
                                //发送31命令---------------------------------------------
                                initBuf = ThreeFiringCmd.send31("00", data);//31写入延时时间
                                sendCmd(initBuf);
                                revCmd = "";//清空缓存
                                thirdStartTime = System.currentTimeMillis();
                                writeDenator = write;
                                thirdWriteCount++;
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());

                            } else {
                                long thirdEnd = System.currentTimeMillis();
                                long spanTime = thirdEnd - thirdStartTime;
                                if (spanTime > 3500 && tempBaseInfo != null) {//发出本发雷管时，没返回超时了
                                    thirdStartTime = 0;
                                    //充电检测错误 tempBaseInfo报错 tempBaseInfo为空 未返回
//                                    Log.e("雷管异常", "tempBaseInfo: "+tempBaseInfo.toString());//雷管超时容易报错,这个就是起爆检测闪退的地方
                                    VoFiringTestError errorDe = new VoFiringTestError();
                                    errorDe.setBlastserial(tempBaseInfo.getBlastserial());//
                                    errorDe.setShellBlastNo(tempBaseInfo.getShellBlastNo());
                                    errorDe.setDelay(tempBaseInfo.getDelay());
                                    errorDe.setError(1);
                                    thirdWriteErrorDenator = errorDe;//(应该只发个管壳码就可以)
//                                    errorList.offer(errorDe);
                                    //尝试修改当单片机未返回时的更新错误状态方法,因为有可能会导致错误更新
                                    From32DenatorFiring fromDataErr = new From32DenatorFiring();
                                    fromDataErr.setShellNo(tempBaseInfo.getShellBlastNo());
                                    fromDataErr.setDenaId(tempBaseInfo.getDenatorId());
                                    fromDataErr.setDelayTime(tempBaseInfo.getDelay());
                                    fromDataErr.setCommicationStatus("AF");
                                    updateDenator(fromDataErr, tempBaseInfo.getDelay());
                                    //发出错误
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    writeDenator = null;
                                    tempBaseInfo = null;
                                    revCmd = "";//清空缓存
                                    reThirdWriteCount++;
                                } else {
                                    Thread.sleep(20);
                                }
                            }
                            break;
                        case 4:
                            if (fourthDisplay == 0) {
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            }
//                            Thread.sleep(1000);
//                            Log.e("等待充电", "------");
                            break;
                        case 5://充电检测阶段38指令计时器

                            Thread.sleep(1000);
                            Log.e("充电检测oneCount", oneCount + "");
                            //说明电源打开命令未返回
                            if (oneCount - oneCount_max >= 5) {
//                              exit = true;
                                secondCmdFlag = 1;
                                thirdWriteCount = 0;
                                increase(3);
                                Log.e("第3阶段-increase", "3--oneCount:" + oneCount
                                + "--oneCount_max:" + oneCount_max);
                            }
                            oneCount++;
                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;
                        case 6://充电阶段

                            if (twoCount == 0) {
                                sendCmd(ThreeFiringCmd.setToXbCommon_FiringExchange_5523_3("00"));//32充电
                            }

                            if (twoCount == 3) {//第3秒时,单发充电
                                if (changjia.equals("CQ")) {
                                    increase(33);
                                } else {
                                    sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40
                                }
                            }
                            if (Fujian.equals("复检") && twoCount == 4) {//第3秒时,单发充电
                                getblastQueue();//重新获取数据,用来给充电list复制
                                increase(66);
                            }

                            if (twoCount == 5) {//第5秒时,发送高压充电指令,继电器应该响
                                Log.e(TAG, "发送33高压输出指令");
                                sendCmd(ThreeFiringCmd.setToXbCommon_FiringExchange_5523_4("00"));//33高压输出
                            }

                            if (twoCount > sixExchangeCount && list_dianliu.get(list_dianliu.size() - 1) - list_dianliu.get(list_dianliu.size() - 5) < 20 && list_dianliu.get(list_dianliu.size() - 1) - list_dianliu.get(list_dianliu.size() - 5) > -20) {//48
                                twoCount_panduan++;
                                Log.e(TAG, "twoCount_panduan: "+twoCount_panduan );
                                if(twoCount_panduan>5){
                                    Log.e("第7阶段-increase", "sixCmdSerial:" + sixCmdSerial);
                                    boolean isStopJl = checkIsStopJl();
                                    if (isStopJl) {
                                        xzqb();
                                    } else {
                                        if (!isInterunptQb) {
                                            deviceStatus = "05";
                                            int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
                                            String stureNum = Utils.strPaddingZero(allNum, 3);
                                            String serrNum = Utils.strPaddingZero(errorNumJl, 3);
                                            String currentPeak = Utils.strPaddingZero(cPeak, 6);
                                            //热点级联发送等待起爆
                                            EventBus.getDefault().post(new FirstEvent("ddqb", stureNum, serrNum, currentPeak));
                                        }
                                    }
//                                    if (sixCmdSerial == 3) {
                                    //跳转到1+5倒数计时5分钟阶段
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                                    Thread.sleep(1000);
                                    increase(7);
//                                    Log.e(TAG, "第7阶段-increase:7");
                                    MmkvUtils.savecode("endTime", System.currentTimeMillis());//应该是从退出页面开始计时
                                    zanting();
//                                        break;
//                                    }
                                }

                            }
                            Log.e(TAG, "twoCount: "+twoCount );
                            Log.e(TAG, "ChongDian_time: "+ChongDian_time );
                            //到达最大值之后,直接跳转
                            if (twoCount >= ChongDian_time) {
                                if (sixCmdSerial == 3) {
                                    //跳转到1+5倒数计时5分钟阶段
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                                    Thread.sleep(1000);
                                    increase(7);
//                                    Log.e(TAG,"到达最大值--第7阶段-increase:7");
                                    boolean isStopJl = checkIsStopJl();
                                    if (isStopJl) {
                                        xzqb();
                                    } else {
                                        if (!isInterunptQb) {
                                            deviceStatus = "05";
                                            int aNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
                                            String tureNum = Utils.strPaddingZero(aNum, 3);
                                            String seNum = Utils.strPaddingZero(errorNumJl, 3);
                                            String peak = Utils.strPaddingZero(cPeak, 6);
                                            //热点级联发送等待起爆
                                            EventBus.getDefault().post(new FirstEvent("ddqb", tureNum, seNum, peak));
                                        }
                                    }
                                    MmkvUtils.savecode("endTime", System.currentTimeMillis());//应该是从退出页面开始计时
                                    zanting();
                                    break;
                                }
                            }

//                            if (sixExchangeCount == -1) {//切换电源是否返回正确
//                                exit = true;
////                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                                break;
//                            }
                            Thread.sleep(1000);
                            twoCount++;
                            //得到电流电压信息210  190
                            if (twoCount != -2 && twoCount != 3 && twoCount != 4 && twoCount != 5) {
                                sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40
                            }
                            if (stage == 6) mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;
                        case 7://1+5 等待阶段
                            Log.e(TAG, "进入case7了sevenCount--1+5 等待阶段sevenCount: " + sevenCount);
                            Thread.sleep(1000);
                            sevenCount++;
//                            if (sevenDisplay == 0){}
//                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                            sevenDisplay = 1;
                            if (keyFireCmd == 1) {

                                increase(8);
                                Log.e(TAG,"increase:8");
                                keyFireCmd = 0;
                                eightCmdExchangePower = 1;
                            }
                            Log.e(TAG, "eightCmdExchangePower7: "+eightCmdExchangePower);
                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            break;
                        case 8://5s倒计时后,发送起爆指令,起爆阶段
//                            if (eightCount == 1) {
//                                byte[] reCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41开启总线电源指令,切换低压
//                                sendCmd(reCmd);
//                            }
                            Log.e(TAG, "eightCmdFlag: "+eightCmdFlag);
                            Log.e(TAG, "eightCmdExchangePower8: "+eightCmdExchangePower);
                            if (eightCount >= 1) {
                                sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                Thread.sleep(1000);
                                eightCount--;
                                Log.e(TAG, "eightCount减1: ");
                            } else {
                                if (eightCmdFlag == 0) {
                                    if (eightCmdExchangePower == 1) {//
                                        //2代芯片不切换低压
//                                        byte[] reCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41开启总线电源指令,切换低压
//                                        sendCmd(reCmd);
                                        //发出34 起爆命令
                                        initBuf = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_5("00");
                                        sendCmd(initBuf);
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
                                byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35 退出起爆
                                sendCmd(reCmd);
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                AppLogUtils.writeAppXBLog("neightCount:" + neightCount + "--" + MmkvUtils.getcode("ACode", "") + "子设备起爆成功弹窗已展示,发35指令");
                                Utils.writeLog("neightCount:" + neightCount + "--" + MmkvUtils.getcode("ACode", "") + "子设备起爆成功弹窗已展示,发35指令");
                            }
                            neightCount++;
                            break;
                        case 10://查看错误阶段

                            break;
                        case 11://放电阶段
                            if (isJL) {
                                isEnterQbDjs = true;
                            }
                            Thread.sleep(1000);
                            elevenCount--;
                            Log.e(TAG, "进入case11了开始递减了:" + elevenCount);
                            mHandler_1.sendMessage(mHandler_1.obtainMessage());
//                            Log.e(TAG, "case11开始递减handler了--elevenCount:" + elevenCount);
                            if (elevenCount <= 0) {
                                Log.e(TAG, "case11起爆成功了，开始进入case9--elevenCount:" + elevenCount);
                                increase(9);
                            }
                            AppLogUtils.writeAppXBLog(MmkvUtils.getcode("ACode", "") + "子设备起爆中,elevenCount:" + elevenCount);
                            Utils.writeLog(MmkvUtils.getcode("ACode", "") + "子设备起爆中,elevenCount:" + elevenCount);
//                            Log.e(TAG, "正在放电阶段elevenCount:" + elevenCount);
                            break;

                        case 12://切换模式放电阶段
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - lastProcessedTime > 1000 && !isGetQbResult) {
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                lastProcessedTime = currentTime;
                                Log.e("为防止ANR,1秒send一次指令", "坐等返回34");
                            }
                            break;
                        case 13://起爆检测电流稳定性
                            if (thirteenCount >= 1) {
                                sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01"));//40
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                Thread.sleep(1000);
                                thirteenCount--;
                            } else {
                                if (eightCmdFlag == 0) {
                                    if (thirteenCmdExchangePower == 1) {//
                                        //2代芯片不切换低压
//                                        byte[] reCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41开启总线电源指令,切换低压
//                                        sendCmd(reCmd);
                                        //发出34 起爆命令
                                        initBuf = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_5("00");
                                        sendCmd(initBuf);
                                        Log.e("起爆", "第一次发送起爆指令: ");
                                        eightCmdFlag = 1;
                                    } else {
                                        exit = true;
                                        mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                        break;
                                    }

                                }
                                if (thirteenCount <= -5) {
                                    exit = true;
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    break;
                                }
                                Thread.sleep(1000);
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            }
                            break;

                        case 33://第一次充电
                            if (reThirdWriteCount == thirdWriteCount) {//判断是否全部测试完成
                                thirdStartTime = 0;
                                writeDenator = null;
                                if (blastQueue == null || blastQueue.size() < 1) {
                                    Utils.writeRecord("--充电结束-------------");
                                    Utils.writeRecord("blastQueue.size():" + blastQueue.size());
                                    //检测一次
                                    Log.e("充电阶段-totalerrorCDNum", totalerrorCDNum+"");
                                    if (totalerrorCDNum != 0) {
                                        totalerrorCDNum = 0;
                                        getErrblastQueue_CD();//重新给雷管队列赋值
                                        increase(55);//之前是4
                                    } else {
                                        increase(6);//之前是4
                                    }
                                    Log.e("第4阶段-检测阶段-increase33", "4-2");
                                    fourOnlineDenatorFlag = 0;
                                    break;
                                }

                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo2 = write;

                                String shellStr = write.getShellBlastNo();
                                if (shellStr == null || shellStr.length() != 13)
                                    continue;//// 判读是否是十三位
                                if (write.getDenatorId() == null) {
                                    Message msg = Handler_tip.obtainMessage();
                                    msg.what = 2;
                                    Bundle b = new Bundle();
                                    msg.setData(b);
                                    Handler_tip.sendMessage(msg);
                                    closeThread();
                                    break;
                                }
                                String denatorId = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorId());//新芯片
                                denatorId = Utils.getReverseDetonatorNo(denatorId);
                                //发送38命令---------------------------------------------
                                initBuf = ThreeFiringCmd.send38("00", denatorId);//38充电
                                sendCmd(initBuf);
                                thirdStartTime = System.currentTimeMillis();
                                writeDenator = write;
                                thirdWriteCount++;
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            } else {
                                long thirdEnd = System.currentTimeMillis();
                                long spanTime = thirdEnd - thirdStartTime;
                                if (spanTime > 3500 && tempBaseInfo2 != null) {//发出本发雷管时，没返回超时了
                                    thirdStartTime = 0;
                                    //充电检测错误 tempBaseInfo报错 tempBaseInfo为空 单片机未返回
//                                    Log.e("雷管异常", "tempBaseInfo: "+tempBaseInfo.toString());//雷管超时容易报错,这个就是起爆检测闪退的地方
                                    VoFiringTestError errorDe = new VoFiringTestError();
                                    errorDe.setBlastserial(tempBaseInfo2.getBlastserial());//
                                    errorDe.setShellBlastNo(tempBaseInfo2.getShellBlastNo());
                                    errorDe.setDelay(tempBaseInfo2.getDelay());
                                    errorDe.setError(1);
                                    thirdWriteErrorDenator = errorDe;
                                    errorList.offer(errorDe);
                                    //发出错误
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    writeDenator = null;
                                    tempBaseInfo2 = null;
                                    reThirdWriteCount++;
                                } else {
                                    Thread.sleep(20);
                                }
                            }
                            break;

                        case 44://写入延时时间，检测结果看雷管是否正常

                            if (reThirdWriteCount == thirdWriteCount) {//判断是否全部测试完成
                                Log.e("第4阶段-increase", "thirdWriteCount:" + thirdWriteCount);
//                                Thread.sleep(50);
                                thirdStartTime = 0;
                                writeDenator = null;
                                //检测一次
                                if (blastQueue == null || blastQueue.size() < 1) {
                                    getblastQueue();//重新获取数据,用来给充电list复制
                                    increase(4);//之前是4
                                    Log.e("第4阶段-increase", "4-2");
                                    int a = Integer.parseInt(ll_firing_errorAmount_2.getText().toString());
                                    GreenDaoMaster master = new GreenDaoMaster();
                                    errlist = master.queryErrLeiGuan(mRegion);//带参数是查一个区域,不带参数是查所有
                                    if (a == 1 && errlist != null && errlist.size() > 0) {
                                        sendCmd(ThreeFiringCmd.send_36("00", errlist.get(0).getZhu_yscs()));//36 在网读ID检测
                                    } else {
                                        sendCmd(ThreeFiringCmd.send_36("00", "0000"));//36 在网读ID检测
                                    }
                                    fourOnlineDenatorFlag = 0;
                                    break;
                                }

                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo = write;

                                String shellStr = write.getShellBlastNo();
                                if (shellStr == null || shellStr.length() != 13)
                                    continue;//// 判读是否是十三位
                                if (write.getDenatorId() == null || write.getDenatorId().length() < 8) {
                                    Message msg = Handler_tip.obtainMessage();
                                    msg.what = 2;
                                    Bundle b = new Bundle();
                                    msg.setData(b);
                                    Handler_tip.sendMessage(msg);
                                    closeThread();
                                    break;
                                }
                                String denatorId = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorId());//新芯片
                                denatorId = Utils.getReverseDetonatorNo(denatorId);

                                short delayTime = write.getDelay();
                                byte[] delayBye = Utils.shortToByte(delayTime);
                                String delayStr = Utils.bytesToHexFun(delayBye);//延时时间

                                String data = denatorId + delayStr + write.getZhu_yscs() + "00";
                                if (write.getDenatorIdSup() != null && write.getDenatorIdSup().length() > 4) {
                                    String denatorIdSup = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorIdSup());//新芯片
                                    denatorIdSup = Utils.getReverseDetonatorNo(denatorIdSup);
                                    data = denatorId + delayStr + write.getZhu_yscs() + denatorIdSup + write.getCong_yscs();
                                    Utils.writeRecord("--设置雷管延时:" + "主芯片:" + denatorId + "延时:" + delayStr + "从芯片:" + denatorIdSup);
                                }
                                //发送31命令---------------------------------------------
                                initBuf = ThreeFiringCmd.send31("00", data);//31写入延时时间
                                sendCmd(initBuf);
                                revCmd = "";//清空缓存
                                thirdStartTime = System.currentTimeMillis();
                                writeDenator = write;
                                thirdWriteCount++;
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());

                            } else {
                                long thirdEnd = System.currentTimeMillis();
                                long spanTime = thirdEnd - thirdStartTime;
                                if (spanTime > 3500 && tempBaseInfo != null) {//发出本发雷管时，没返回超时了
                                    thirdStartTime = 0;
                                    //充电检测错误 tempBaseInfo报错 tempBaseInfo为空 未返回
//                                    Log.e("雷管异常", "tempBaseInfo: "+tempBaseInfo.toString());//雷管超时容易报错,这个就是起爆检测闪退的地方
                                    VoFiringTestError errorDe = new VoFiringTestError();
                                    errorDe.setBlastserial(tempBaseInfo.getBlastserial());//
                                    errorDe.setShellBlastNo(tempBaseInfo.getShellBlastNo());
                                    errorDe.setDelay(tempBaseInfo.getDelay());
                                    errorDe.setError(1);
                                    thirdWriteErrorDenator = errorDe;//(应该只发个管壳码就可以)
//                                    errorList.offer(errorDe);
                                    //尝试修改当单片机未返回时的更新错误状态方法,因为有可能会导致错误更新
                                    From32DenatorFiring fromDataErr = new From32DenatorFiring();
                                    fromDataErr.setShellNo(tempBaseInfo.getShellBlastNo());
                                    fromDataErr.setDenaId(tempBaseInfo.getDenatorId());
                                    fromDataErr.setDelayTime(tempBaseInfo.getDelay());
                                    fromDataErr.setCommicationStatus("AF");
                                    updateDenator(fromDataErr, tempBaseInfo.getDelay());
                                    //发出错误
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    writeDenator = null;
                                    tempBaseInfo = null;
                                    revCmd = "";//清空缓存
                                    reThirdWriteCount++;
                                } else {
                                    Thread.sleep(20);
                                }
                            }
                            break;

                        case 55://写入延时时间，检测结果看雷管是否正常
                            if (reThirdWriteCount == thirdWriteCount) {//判断是否全部测试完成
                                thirdStartTime = 0;
                                writeDenator = null;
                                if (blastQueue == null || blastQueue.size() < 1) {
                                    Utils.writeRecord("--重发错误雷管结束-------------");
                                    Log.e(TAG, "重发错误雷管totalerrorCDNum: "+totalerrorCDNum );
                                    if (totalerrorCDNum != 0) {
                                        totalerrorCDNum = 0;
                                        Handler_tip.sendMessage(Handler_tip.obtainMessage(7));

                                    }else {
                                        //检测一次
                                        increase(6);//之前是4
                                    }


                                    Log.e("第4阶段-检测阶段-increase33", "4-2");
                                    fourOnlineDenatorFlag = 0;
                                    break;
                                }

                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo2 = write;

                                String shellStr = write.getShellBlastNo();
                                if (shellStr == null || shellStr.length() != 13)
                                    continue;//// 判读是否是十三位
                                if (write.getDenatorId() == null) {
                                    Message msg = Handler_tip.obtainMessage();
                                    msg.what = 2;
                                    Bundle b = new Bundle();
                                    msg.setData(b);
                                    Handler_tip.sendMessage(msg);
                                    closeThread();
                                    break;
                                }
                                String denatorId = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorId());//新芯片
                                denatorId = Utils.getReverseDetonatorNo(denatorId);
                                //发送38命令---------------------------------------------
                                initBuf = ThreeFiringCmd.send38("00", denatorId);//38充电
                                sendCmd(initBuf);
                                thirdStartTime = System.currentTimeMillis();
                                writeDenator = write;
                                thirdWriteCount++;
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            } else {
                                long thirdEnd = System.currentTimeMillis();
                                long spanTime = thirdEnd - thirdStartTime;
                                if (spanTime > 3500 && tempBaseInfo2 != null) {//发出本发雷管时，没返回超时了
                                    thirdStartTime = 0;
                                    //充电检测错误 tempBaseInfo报错 tempBaseInfo为空 单片机未返回
//                                    Log.e("雷管异常", "tempBaseInfo: "+tempBaseInfo.toString());//雷管超时容易报错,这个就是起爆检测闪退的地方
                                    VoFiringTestError errorDe = new VoFiringTestError();
                                    errorDe.setBlastserial(tempBaseInfo2.getBlastserial());//
                                    errorDe.setShellBlastNo(tempBaseInfo2.getShellBlastNo());
                                    errorDe.setDelay(tempBaseInfo2.getDelay());
                                    errorDe.setError(1);
                                    thirdWriteErrorDenator = errorDe;
                                    errorList.offer(errorDe);
                                    //发出错误
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    writeDenator = null;
                                    tempBaseInfo2 = null;
                                    reThirdWriteCount++;
                                } else {
                                    Thread.sleep(20);
                                }
                            }
                            break;

                        case 66://写入延时时间，检测结果看雷管是否正常
                            if (reThirdWriteCount == thirdWriteCount) {//判断是否全部测试完成
                                thirdStartTime = 0;
                                writeDenator = null;
                                if (blastQueue == null || blastQueue.size() < 1) {
                                    Utils.writeRecord("--复检结束-------------");
                                    if (totalerrorCDNum != 0) {
                                        totalerrorCDNum = 0;
                                        Handler_tip.sendMessage(Handler_tip.obtainMessage(8));

                                    }else {
                                        //检测一次
                                        increase(6);//之前是4
                                    }
                                    Log.e("第4阶段-检测阶段-increase33", "4-2");
                                    fourOnlineDenatorFlag = 0;
                                    break;
                                }

                                VoDenatorBaseInfo write = blastQueue.poll();
                                tempBaseInfo2 = write;

                                String shellStr = write.getShellBlastNo();
                                if (shellStr == null || shellStr.length() != 13)
                                    continue;//// 判读是否是十三位
                                if (write.getDenatorId() == null) {
                                    Message msg = Handler_tip.obtainMessage();
                                    msg.what = 2;
                                    Bundle b = new Bundle();
                                    msg.setData(b);
                                    Handler_tip.sendMessage(msg);
                                    closeThread();
                                    break;
                                }
                                String denatorId = Utils.DetonatorShellToSerialNo_newXinPian(write.getDenatorId());//新芯片
                                denatorId = Utils.getReverseDetonatorNo(denatorId);
                                //发送39命令---------------------------------------------
                                initBuf = ThreeFiringCmd.send39("00", denatorId);//39充电
                                sendCmd(initBuf);
                                thirdStartTime = System.currentTimeMillis();
                                writeDenator = write;
                                thirdWriteCount++;
                                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                            } else {
                                long thirdEnd = System.currentTimeMillis();
                                long spanTime = thirdEnd - thirdStartTime;
                                if (spanTime > 3500 && tempBaseInfo2 != null) {//发出本发雷管时，没返回超时了
                                    thirdStartTime = 0;
                                    //充电检测错误 tempBaseInfo报错 tempBaseInfo为空 单片机未返回
//                                    Log.e("雷管异常", "tempBaseInfo: "+tempBaseInfo.toString());//雷管超时容易报错,这个就是起爆检测闪退的地方
                                    VoFiringTestError errorDe = new VoFiringTestError();
                                    errorDe.setBlastserial(tempBaseInfo2.getBlastserial());//
                                    errorDe.setShellBlastNo(tempBaseInfo2.getShellBlastNo());
                                    errorDe.setDelay(tempBaseInfo2.getDelay());
                                    errorDe.setError(1);
                                    thirdWriteErrorDenator = errorDe;
                                    errorList.offer(errorDe);
                                    //发出错误
                                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                                    writeDenator = null;
                                    tempBaseInfo2 = null;
                                    reThirdWriteCount++;
                                } else {
                                    Thread.sleep(20);
                                }
                            }
                            break;
                        case 99://暂停阶段

                            break;

                    }
                }
            } catch (InterruptedException e) {
            }
        }

    }

    //C000310C 3D EA FF 00 0A 00 00 00 00 00 A21E C0
    private void getblastQueue() {
        allBlastQu = new ConcurrentLinkedQueue<>();
        errorList = new ConcurrentLinkedQueue<>();

        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> denatorlist = master.queryDetonatorRegionAscNew(qyIdList);//不分区域
//        for (DenatorBaseinfo d : denatorlist) {
//            VoDenatorBaseInfo vo = new VoDenatorBaseInfo();
//            vo.setBlastserial(d.getBlastserial());
//            vo.setDelay((short) d.getDelay());
//            vo.setShellBlastNo(d.getShellBlastNo());
//            vo.setDenatorId(d.getDenatorId());
//            vo.setDenatorIdSup(d.getDenatorIdSup());
//            vo.setZhu_yscs(d.getZhu_yscs());
//            vo.setCong_yscs(d.getCong_yscs());
//            vo.setLgzt(d.getErrorCode());
//            allBlastQu.offer(vo);
//            list_all_lg.add(vo);
//        }
//
//        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao().loadAll();
        for (DenatorBaseinfo denatorBaseinfo : denatorlist) {
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
            vo.setLgzt(denatorBaseinfo.getErrorCode());
            allBlastQu.offer(vo);
        }
        reThirdWriteCount = 0;
        thirdWriteCount = 0;
        firstThread.blastQueue = allBlastQu;
    }

    /**
     * 获取错误雷管
     */
    private void getErrblastQueue() {
        allBlastQu = new ConcurrentLinkedQueue<>();
        errorList = new ConcurrentLinkedQueue<>();
//        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao().loadAll();
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryErrLeiGuanNew(qyIdList);//带参数是查一个区域,不带参数是查所有
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
            allBlastQu.offer(vo);
        }
        reThirdWriteCount = 0;
        thirdWriteCount = 0;
        firstThread.blastQueue = allBlastQu;
    }

    private void getErrblastQueue_CD() {
        allBlastQu = new ConcurrentLinkedQueue<>();
        errorList = new ConcurrentLinkedQueue<>();
        Log.e(TAG, "allBlastQu: " + allBlastQu.size());
        Log.e(TAG, "errorList: " + errorList.size());
//        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao().loadAll();
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryErrLeiGuan_CDNew(qyIdList);//带参数是查一个区域,不带参数是查所有
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
            allBlastQu.offer(vo);
        }
        reThirdWriteCount = 0;
        thirdWriteCount = 0;
        firstThread.blastQueue = allBlastQu;
        Log.e(TAG, "充电失败雷管--allBlastQu: " + allBlastQu.size());
        Log.e(TAG, "充电失败雷管--list: " + list.size());
    }


    /**
     * 按1和5起爆
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_1) {
            m0UpTime = System.currentTimeMillis();
            AppLogUtils.writeAppXBLog("点击了数字1按键");
        } else if (keyCode == KeyEvent.KEYCODE_3 && !Build.DEVICE.equals("KT50_B2") && !Build.DEVICE.equals("KT50")) {
            AppLogUtils.writeAppXBLog("点击了数字3按键");
            m5DownTime = System.currentTimeMillis();
            long spanTime = m5DownTime - m0UpTime;
            if (spanTime < 500) {
                if (stage == 7) {
                    if (kaiguan) {
                        jixu();
                        kaiguan = false;
                    }
                    Log.e(TAG, "继续1: ");
                    keyFireCmd = 1;
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_5 && (Build.DEVICE.equals("KT50_B2") || Build.DEVICE.equals("KT50"))) {
            AppLogUtils.writeAppXBLog("点击了数字5按键");
            m5DownTime = System.currentTimeMillis();
            long spanTime = m5DownTime - m0UpTime;
            if (spanTime < 500) {
                if (stage == 7) {
                    if (kaiguan) {
                        jixu();
                        kaiguan = false;
                    }
                    Log.e(TAG, "继续2: ");
                    keyFireCmd = 1;
                }
            }
        }
        AppLogUtils.writeAppXBLog("按组合键起爆");
        Utils.writeRecord("--按组合键起爆--");
        return super.dispatchKeyEvent(event);
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


    private TextView mOffTextView;
    private Handler mOffHandler;
    private Timer mOffTime;
    private android.app.Dialog mDialog;
    private CommonDialog dialog;//自定义dialog


    /**
     * 倒计结束后,重新上电开始检测
     */
    private void off() {

        initParam();//先重置数据
        ctlLinePanel(0);
        chongfu = true;//已经检测了一次
        loadBlastModel();
        getblastQueue();//重新给雷管队列赋值
//        increase(1);
        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//        byte[] powerCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41
//        sendCmd(powerCmd);
//        if(blastQueue.size() == 0){
//            blastQueue = allBlastQu;
//        }
//        ll_firing_deAmount_4.setText("0");
        ll_firing_errorAmount_4.setText("0");
        ll_firing_errorAmount_2.setText("0");
        ll_firing_Volt_2.setText("0V");
        ll_firing_Volt_4.setText("0V");
        ll_firing_Volt_5.setText("0V");
        ll_firing_IC_2.setText("0μA");
        ll_firing_IC_4.setText("0μA");
        ll_firing_IC_5.setText("0μA");
        ll_firing_errorAmount_4.setTextColor(Color.GREEN);
        ll_firing_errorAmount_2.setTextColor(Color.GREEN);
        firstTxt.setText((oneCount) + "s");
        secondTxt.setText(getString(R.string.text_firing_tip7) + (oneCount) + "s)");

    }

    private void endTest() {
        increase(4);//跳到第四阶段
    }

    private void stopXunHuan() {
//        endTest();
        ctlLinePanel(4);//修改页面显示项
        getErrorBlastCount();
        sendCmd(ThreeFiringCmd.send_36("00", "0000"));//36 在网读ID检测
    }

    private void initDialog(String tip, int daojishi) {

        mOffTextView = new TextView(this);
        mOffTextView.setTextSize(25);
        mOffTextView.setText(tip + "\n" + getResources().getString(R.string.text_fir_dialog1));
        mDialog = new Builder(this)
                .setTitle(getResources().getString(R.string.text_fir_dialog2))
                .setCancelable(false)
                .setView(mOffTextView)
//                .setPositiveButton("确定", (dialog, id) -> {
//                    mOffTime.cancel();//清除计时
//                    stopXunHuan();//关闭后的一些操作
//                })
                .setNeutralButton(getResources().getString(R.string.text_tc), (dialog, id) -> {
                    exitJl();
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
                mOffTextView.setText(tip + "\n" + getResources().getString(R.string.text_fir_dialog1) + msg.what);
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
            private int countTime = daojishi;

            public void run() {
                if (countTime > 0) {
                    countTime--;
                }
                if (countTime == 118) {
                    byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                    sendCmd(reCmd);
                }
                Message msg = new Message();
                msg.what = countTime;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 1000, 1000);
    }


    private void initDialog_zanting(String tip) {
        if (!FiringMainActivity.this.isFinishing()) {
            chongfu = true;//已经检测了一次
            AlertDialog dialog = new Builder(FiringMainActivity.this)
                    .setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题//"成功起爆"
                    .setMessage(tip)//设置对话框的内容"本次任务成功起爆！"
                    //设置对话框的按钮
                    .setNegativeButton (getResources().getString(R.string.text_fir_dialog3), (dialog1, which) -> {
                        off();//重新检测
                        dialog1.dismiss();
                    })
                    .setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog12, which) -> {
                        AppLogUtils.writeAppXBLog("点击了退出按钮");
                        exitJl();
                        dialog12.cancel();
                        closeThread();
                        closeForm();
                        exitJl();
                    })
                    .setPositiveButton(getResources().getString(R.string.text_firing_jixu), (dialog12, which) -> {
//                    off();//重新检测
                        increase(2);
                        dialog12.cancel();
                    })
                    .create();
            dialog.show();
        }
    }

    private void initDialog_zanting_stop(String tip) {
        if (!FiringMainActivity.this.isFinishing()) {
            firstThread.exit = true;
            firstThread.interrupt();
            try {
                firstThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isJL) {
                isDlShow = true;
            }
            chongfu = true;//已经检测了一次
//            AlertDialog dialog = new Builder(FiringMainActivity.this)
            dlDialog = new Builder(FiringMainActivity.this)
                    .setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题//"成功起爆"
                    .setMessage(tip)//设置对话框的内容"本次任务成功起爆！"
                    //设置对话框的按钮

                    .setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog12, which) -> {
                        AppLogUtils.writeAppXBLog("点击了退出按钮");
                        exitJl();
                        byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                        sendCmd(reCmd);
                        closeThread();
                        closeForm();

                        dialog12.cancel();
                    })
                    .setPositiveButton(getResources().getString(R.string.text_firing_jixu), (dialog12, which) -> {
                        AppLogUtils.writeAppXBLog("出现异常弹窗后点击了继续按钮");
//                    off();//重新检测
                        firstThread = new ThreadFirst(allBlastQu);
                        firstThread.exit = false;
                        firstThread.start();
                        dialog12.cancel();
                        if (isJL) {
                            isDlShow = false;
                            if (isDlShow) {
                                sendErrorData("03");
                                deviceStatus = "13";
                            } else if (isDlgdShow) {
                                sendErrorData("02");
                                deviceStatus = "12";
                            } else if (isDlycShow) {
                                sendErrorData("01");
                                deviceStatus = "11";
                            } else if (isDlycbdShow) {
                                sendErrorData("04");
                                deviceStatus = "14";
                            } else if (isCheckLgNumShow) {
                                sendErrorData("07");
                                deviceStatus = "17";
                            } else {
                                updateJlStatus();
                            }
                            sendClycjg();
                        }
                    })
                    .create();
            dlDialog.setCancelable(false);
            dlDialog.show();
//            dialog.setCancelable(false);
//            dialog.show();
        }
    }

    private void initDialog_zanting2(String tip) {
        if (!FiringMainActivity.this.isFinishing()) {
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
//        ListView errlistview = getlistview.findViewById(R.id.X_listview);
//        errlistview.setVisibility(View.GONE);
//        SimpleAdapter adapter = new SimpleAdapter(this, errDeData, R.layout.firing_error_item,
//                new String[]{"serialNo", "shellNo", "errorName", "delay"},
//                new int[]{R.id.X_item_no, R.id.X_item_shellno, R.id.X_item_errorname, R.id.X_item_delay});
//        // 给listview加入适配器
//        errlistview.setAdapter(adapter);

            // 给ListView绑定内容
            ListView errlistview = getlistview.findViewById(R.id.X_listview);
            ErrListAdapter mAdapter = new ErrListAdapter(this, errDeData, R.layout.firing_error_item);
            errlistview.setAdapter(mAdapter);

            Builder builder = new Builder(this);
            builder.setTitle(getResources().getString(R.string.text_fir_dialog2));//"错误雷管列表"
            builder.setView(getlistview);
            builder.setPositiveButton(getResources().getString(R.string.text_fir_dialog3), (dialog, which) -> {
                dialogOFF(dialog);
//            byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
//            sendCmd(reCmd);
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                off();//重新检测
                dialog.dismiss();

            });
            builder.setNeutralButton(getResources().getString(R.string.text_fir_dialog4), (dialog, which) -> {
                dialogOFF(dialog);
                stopXunHuan();
                dialog.dismiss();
            });
            builder.setNegativeButton(getResources().getString(R.string.text_fir_dialog5), (dialog, which) -> {
                AppLogUtils.writeAppXBLog("点击了查看错误雷管按钮");
//            stopXunHuan();
                llview.setVisibility(View.VISIBLE);
                text_tip.setVisibility(View.GONE);
                errlistview.setVisibility(View.VISIBLE);
                dialogOn(dialog);
            });
            builder.create().show();
        }
    }

    private void initDialog_chongdian(String tip) {
        if (!FiringMainActivity.this.isFinishing()) {
            firstThread.exit = true;
            firstThread.interrupt();
            try {
                firstThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chongfu = true;//已经检测了一次
            loadErrCDBlastModel();//重新给雷管队列赋值
            LayoutInflater inflater = LayoutInflater.from(this);
            View getlistview = inflater.inflate(R.layout.firing_error_listview, null);
            LinearLayout llview = getlistview.findViewById(R.id.ll_dialog_err);
//            llview.setVisibility(View.GONE);
            TextView text_tip = getlistview.findViewById(R.id.dialog_tip);
            text_tip.setText(tip);
            text_tip.setVisibility(View.VISIBLE);
            // 给ListView绑定内容
            ListView errlistview = getlistview.findViewById(R.id.X_listview);
            ErrListAdapter mAdapter = new ErrListAdapter(this, errDeData, R.layout.firing_error_item);
            errlistview.setAdapter(mAdapter);
            cdfailDialog = new Builder(this)
                    .setTitle(getResources().getString(R.string.text_fir_dialog2)).setView(getlistview)
                    .setPositiveButton(getResources().getString(R.string.text_firing_jixu), (dialog, which) -> {
                        dialog.dismiss();
                        firstThread = new ThreadFirst(allBlastQu);
                        firstThread.exit = false;
                        firstThread.start();
                        increase(6);//之前是4
                        deviceStatus = "04";
                        isInterunptQb = false;
                        isHaveError = false;
                        sendClycjg();
                    })
                    .setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog, which) -> {
                        AppLogUtils.writeAppLog(tip + "--点击了退出按钮");
                        exitJl();
                        byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
                        sendCmd(reCmd);
                        closeThread();
                        closeForm();
                    })
                    .create();
            cdfailDialog.setCanceledOnTouchOutside(false);
            cdfailDialog.show();
//            Builder builder = new Builder(this);
//            builder.setTitle(getResources().getString(R.string.text_fir_dialog2));//"错误雷管列表"
//            builder.setView(getlistview);
//            builder.setPositiveButton(getResources().getString(R.string.text_firing_jixu), (dialog, which) -> {
//                dialogOFF(dialog);
//                firstThread = new ThreadFirst(allBlastQu);
//                firstThread.exit = false;
//                firstThread.start();
//                increase(6);//之前是4
//            });
//            builder.setNeutralButton(getResources().getString(R.string.text_test_exit), (dialog, which) -> {
//                exitJl();
//                byte[] reCmd = ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00");//35退出起爆
//                sendCmd(reCmd);
//                closeThread();
//                closeForm();
//            });
//            builder.setNegativeButton("查看充电错误雷管", (dialog, which) -> {
////            stopXunHuan();
//                llview.setVisibility(View.VISIBLE);
//                text_tip.setVisibility(View.GONE);
//                errlistview.setVisibility(View.VISIBLE);
//                dialogOn(dialog);
//            });
//            builder.create().show();
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

    /***
     * 得到最大序号
     * @return
     */
    private int getMinDelay() {//
        Cursor cursor = db.rawQuery("select min(delay) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where piece =?", new String[]{mRegion});
        if (cursor != null && cursor.moveToNext()) {
            int delayMin = cursor.getInt(0);
            cursor.close();
            Log.e(TAG, "当前" + mRegion + "区域最小延时: " + delayMin);
            return delayMin;
        }
        return 0;
    }

    /***
     * 得到最大序号
     * 获取单选区域的最大延时
     * @return
     */
    private int getMaxDelay() {//
        Cursor cursor = db.rawQuery("select max(delay) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where piece =?", new String[]{mRegion});
        if (cursor != null && cursor.moveToNext()) {
            if (!cursor.isNull(0)) {
                int delayMax = cursor.getInt(0);
                Utils.writeLog("数据库查出的当前" + mRegion + "区域最大延时是:" + delayMax);
                Log.e(TAG, "当前" + mRegion + "区域最大延时: " + delayMax);
                return delayMax;
            } else {
                Utils.writeLog("数据库获取到的最大延时为null");
                Log.e(TAG, "数据库获取到的最大延时为null--No valid delay values found (max is null).");
            }
            cursor.close();
        } else {
            Log.e(TAG, "数据库查出最大延时出错是:" + cursor.getCount());
            Utils.writeLog("数据库查出最大延时出错是:" + cursor.getCount());
        }
        return 0;
    }


    /**
     * 找到所有选中区域雷管的最大延时并比较，最后得到最大延时
     * @return：最大延时（作为起爆结束倒计时时间）
     */
    private int getRegionMaxDelay() {
        int maxDelay = 0;  // 初始值为0，表示没有延时数据
        List<String> selectedRegions = new ArrayList<>();
        if (mRegion1) selectedRegions.add("1");
        if (mRegion2) selectedRegions.add("2");
        if (mRegion3) selectedRegions.add("3");
        if (mRegion4) selectedRegions.add("4");
        if (mRegion5) selectedRegions.add("5");
        if (selectedRegions.isEmpty()) {
            return maxDelay; // 如果没有选中任何区域，直接返回0
        }
        // 构建 SQL 查询，使用 IN 查询多个区域
        StringBuilder queryBuilder = new StringBuilder("select max(delay) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where piece IN (");
        for (int i = 0; i < selectedRegions.size(); i++) {
            queryBuilder.append("?");
            if (i < selectedRegions.size() - 1) {
                queryBuilder.append(",");
            }
        }
        queryBuilder.append(")");
        // 执行查询
        Cursor cursor = db.rawQuery(queryBuilder.toString(), selectedRegions.toArray(new String[0]));
        if (cursor != null && cursor.moveToNext()) {
            if (!cursor.isNull(0)) {
                maxDelay = cursor.getInt(0);
                Log.e(TAG, "所有选中区域的最大延时为: " + maxDelay);
            } else {
                Log.e(TAG, "数据库获取到的最大延时为null");
            }
            cursor.close();
        } else {
            Log.e(TAG, "查询出错或无数据");
        }
        return maxDelay;  // 返回选中区域的最大延时
    }

    /**
     * 获取所选区域的最大延时
     * @param qyidList:多选区域的id
     * @return
     */
    private int getRegionMaxDelay(List<Integer> qyidList) {
        int maxDelay = 0;  // 初始值为0，表示没有延时数据
        // 如果 qyidList 为空，直接返回最大延时为 0
        if (qyidList == null || qyidList.isEmpty()) {
            return maxDelay;
        }
        // 构建 SQL 查询，使用 IN 查询多个区域
        StringBuilder queryBuilder = new StringBuilder("select max(delay) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where piece IN (");
        // 使用 ? 占位符来动态传入参数
        for (int i = 0; i < qyidList.size(); i++) {
            queryBuilder.append("?");
            if (i < qyidList.size() - 1) {
                queryBuilder.append(",");
            }
        }
        queryBuilder.append(")");
        // 执行查询
        Cursor cursor = db.rawQuery(queryBuilder.toString(), convertListToStringArray(qyidList));
        if (cursor != null && cursor.moveToNext()) {
            if (!cursor.isNull(0)) {
                maxDelay = cursor.getInt(0);
                Log.e(TAG, "所有选中区域的最大延时为: " + maxDelay);
            } else {
                Log.e(TAG, "数据库获取到的最大延时为null");
            }
            cursor.close();
        } else {
            Log.e(TAG, "查询出错或无数据");
        }
        return maxDelay;  // 返回选中区域的最大延时
    }

    // 辅助方法：将 List<Integer> 转换为 String[]
    private String[] convertListToStringArray(List<Integer> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = String.valueOf(list.get(i));  // 将 Integer 转换为 String
        }
        return array;
    }

    private void sendClycjg() {
        //子机异常弹窗处理结束后给主控发消息更新最新状态
        int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
        String stureNum = Utils.strPaddingZero(allNum, 3);
        String serrNum = Utils.strPaddingZero(errorNumJl, 3);
        String currentPeak = Utils.strPaddingZero(cPeak, 6);
        //热点级联发送起爆结果
        EventBus.getDefault().post(new FirstEvent("clycjg", deviceStatus, currentPeak, stureNum, serrNum));
    }

    private void updateJlStatus() {
        //处理完异常弹窗后更新当前状态
        if (stage == 2  || stage == 3 || stage == 5 || stage == 44) {
            isInterunptQb = false;
            isHaveError = false;
            deviceStatus = "02";
        } else if (stage == 4) {
            if (!checkIsStopJl()) {
                isInterunptQb = false;
                isHaveError = false;
                deviceStatus = "03";
            } else {
                isHaveError = true;
                isQbFail = true;
                isInterunptQb = true;
                deviceStatus = "21";
            }
        } else if (stage == 6 || stage == 33 || stage == 55 || stage == 66) {
            isInterunptQb = false;
            isHaveError = false;
            deviceStatus = "04";
        } else if (stage == 7) {
            isInterunptQb = false;
            isHaveError = false;
            deviceStatus = "05";
        }
    }
    /**
     * 级联接收命令代码 eventbus
     */
    private long lastProcessedTime = 0;
    private boolean isSend83 = true;//防止起爆时主的子设备重复执行83指令
    private boolean otherA5 = true;//防止起爆时其他子设备重复关闭串口
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FirstEvent event) {
        String msg = event.getMsg();
//        Log.e(TAG + "eventBus消息", "msg: " + msg);
        if (msg.equals("jixu")) {
            //如果是限制起爆状态  级联指令也不操作
            if (!isQbFail) {
                show_Toast(getString(R.string.text_sync_tip5));
                if (stage == 4) {
                    increase(6);
                    Utils.writeRecord("接到级联指令开始充电了");
                }
                EventBus.getDefault().post(new FirstEvent("sendA4Data", "B4" + MmkvUtils.getcode("ACode", "") +
                        deviceStatus + qbResult));
            }
        } else  if (msg.equals("rejixu")) {
            if (!isQbFail) {
                show_Toast(getString(R.string.text_sync_tip5));
                if (stage == 4) {
                    increase(6);
                    Utils.writeRecord("接到级联指令开始充电了");
                }
                EventBus.getDefault().post(new FirstEvent("reSendA4Data", "BBA4" + MmkvUtils.getcode("ACode", "") +
                        deviceStatus + qbResult));
            }
        } else if (msg.equals("qibao")) {
            if (!isQbFail && !isInterunptQb) {
                show_Toast(getString(R.string.text_sync_tip6));
                Log.e("起爆页面", "收到级联起爆指令--isQbFail:" + isQbFail + "--isInterunptQb:" + isInterunptQb);
                Log.e("起爆页面", "收到级联起爆指令时--eightCount " + eightCount);
                if (kaiguan) {
                    AppLogUtils.writeAppXBLog("接到级联指令开始起爆了");
                    jixu();
                    kaiguan = false;
                }
                Log.e(TAG, "继续3: ");
                if (stage == 7) {
                    keyFireCmd = 1;
                    Log.e("起爆页面", "keyFireCmd: " + keyFireCmd);
                }
            }
        } else if (msg.equals("finish")) {
            AppLogUtils.writeAppXBLog("退出起爆页面了");
            closeThread();
            closeForm();
            finish();
        } else if (msg.equals("exitPage")) {
            AppLogUtils.writeAppXBLog("退出起爆页面了");
            //收到主控退出当前起爆页面之类   为防止有子设备仍处于高压状态   此时发送35指令下电
            sendCmd(ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00"));//35退出起爆
            closeThread();
            closeForm();
        } else if (msg.equals("pollMsg")) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastProcessedTime > 1000) {
                //总是出现给主控发多次消息情况  所以检查是否距离上次处理超过 1 秒，优化为：1秒内只发送一次数据给主控
                //收到主控轮巡的消息了  将实时的电流及设备状态发送给串口进行同步
                int allNum = Integer.parseInt(ll_firing_deAmount_4.getText().toString());
                String stureNum = Utils.strPaddingZero(allNum, 3);
                String serrNum = Utils.strPaddingZero(errorNumJl, 3);
                String currentPeak = Utils.strPaddingZero(cPeak, 6);
                EventBus.getDefault().post(new FirstEvent("ssjc", deviceStatus, "", stureNum, serrNum, currentPeak));
                lastProcessedTime = currentTime; // 更新上次处理时间
                qbResult = stureNum + serrNum + currentPeak;
                Log.e(TAG + "子机发送轮询消息", "deviceStatus:" + deviceStatus + "--B3指令:" + qbResult);
            }
        } else if (msg.equals("sendCmd83")) {
            if (!isQbFail && !isInterunptQb) {
                if (isSend83) {
                    isSend83 = false;
                    show_Toast(getString(R.string.text_sync_tip6));
                    // 此时进入时钟同步模式  向核心板发送指令  让核心板决定谁起爆
                    Log.e(TAG,"sendCmd83--主的子设备可以起爆了--发83指令");
                    Utils.writeRecord("主的子设备：" + MmkvUtils.getcode("ACode", "") + "下发83指令");
                    sendCmd(ThreeFiringCmd.setToXbCommon_Translate_83("" + MmkvUtils.getcode("ACode", "")));
                    EventBus.getDefault().post(new FirstEvent("close485"));
                }
            }
        } else if (msg.equals("sendWaitQb")) {
            AppLogUtils.writeAppXBLog("起爆中了");
            //子机展示为起爆中，请稍后状态
            if (!isQbFail && !isInterunptQb) {
                show_Toast(getString(R.string.text_sync_tip6));
                isSendWaitQb = true;
                if (kaiguan) {
                    jixu();
                    kaiguan = false;
                }
                if (!isEnterQbDjs) {
                    //如果已经进入起爆倒计时  就不再显示起爆中了  等待起爆即可
                    //此时在页面显示出时钟校验的文字
                    increase(12);
                    AppLogUtils.writeAppXBLog("所有子设备：" + MmkvUtils.getcode("ACode", "") + "显示起爆中view");
                    Log.e(TAG,"sendWaitQb级联--所有子设备显示起爆中view");
                    Utils.writeLog("所有子设备：" + MmkvUtils.getcode("ACode", "") + "显示起爆中view");
                } else {
                    increase(11);
                    AppLogUtils.writeAppXBLog("级联已进入起爆倒计时，不再做处理--elevenCount:" + elevenCount);
                    Utils.writeLog("级联已进入起爆倒计时，不再做处理--elevenCount:" + elevenCount);
                    Log.e(TAG,"级联已进入起爆倒计时，不再做处理elevenCount:" + elevenCount);
                }
            }
//        } else if (msg.equals("otherA5")) {
//            //A5起爆指令  说明是其他子设备收到了A5指令  此时需要关闭串口
//            if (!isQbFail && !isInterunptQb) {
//                if (otherA5) {
//                    otherA5 = false;
//                    EventBus.getDefault().post(new FirstEvent("otherClose"));
//                    Log.e(TAG,"其他子设备开始关串口执行起爆");
//                    Utils.writeLog("其他子设备" + MmkvUtils.getcode("ACode", "") + "开始关串口执行起爆");
//                }
//            }
        } else if (msg.equals("handleJx")) {
            Log.e(TAG,"handleJx的消息内容:" + event.getData() + "--当前阶段:" + stage);
            /**
             * 此时需要处理主控发的异常情况“继续”按钮逻辑
             * 01电流异常  02电流过大  03短路  04电流异常波动  05有充电失败雷管 06有复检失败雷管
             */
            if ("01".equals(event.getData())) {
                if (dlycDialog != null && dlycDialog.isShowing()) {
                    dlycDialog.dismiss();
                    isDlycShow = false;
                }
                if (isDlycShow) {
                    sendErrorData("01");
                    deviceStatus = "11";
                } else if (isDlgdShow) {
                    sendErrorData("02");
                    deviceStatus = "12";
                }  else if (isDlShow) {
                    sendErrorData("03");
                    deviceStatus = "13";
                } else if (isDlycbdShow) {
                    sendErrorData("04");
                    deviceStatus = "14";
                } else if (isCheckLgNumShow) {
                    sendErrorData("07");
                    deviceStatus = "17";
                }  else {
                    isInterunptQb = false;
                    isHaveError = false;
                    deviceStatus = "05";
                }
            } else if ("02".equals(event.getData())) {
                if (dlgdDialog != null && dlgdDialog.isShowing()) {
                    dlgdDialog.dismiss();
                    isDlgdShow = false;
                }
                if (isDlgdShow) {
                    sendErrorData("02");
                    deviceStatus = "12";
                } else if (isDlycShow) {
                    sendErrorData("01");
                    deviceStatus = "11";
                }  else if (isDlShow) {
                    sendErrorData("03");
                    deviceStatus = "13";
                } else if (isDlycbdShow) {
                    sendErrorData("04");
                    deviceStatus = "14";
                } else if (isCheckLgNumShow) {
                    sendErrorData("07");
                    deviceStatus = "17";
                } else {
                    updateJlStatus();
                }
            } else if ("03".equals(event.getData())) {
                if (dlDialog != null && dlDialog.isShowing()) {
                    firstThread = new ThreadFirst(allBlastQu);
                    firstThread.exit = false;
                    firstThread.start();
                    dlDialog.cancel();
                    isDlShow = false;
                }
                if (isDlShow) {
                    sendErrorData("03");
                    deviceStatus = "13";
                } else if (isDlgdShow) {
                    sendErrorData("02");
                    deviceStatus = "12";
                } else if (isDlycShow) {
                    sendErrorData("01");
                    deviceStatus = "11";
                } else if (isDlycbdShow) {
                    sendErrorData("04");
                    deviceStatus = "14";
                } else if (isCheckLgNumShow) {
                    sendErrorData("07");
                    deviceStatus = "17";
                }  else {
                    updateJlStatus();
                }
            } else if ("04".equals(event.getData())) {
                if (dlycbdDialog != null && dlycbdDialog.isShowing()) {
                    dlycbdDialog.dismiss();
                    isDlycbdShow = false;
                }
                if (isDlycbdShow) {
                    sendErrorData("04");
                    deviceStatus = "14";
                } else if (isDlgdShow) {
                    sendErrorData("02");
                    deviceStatus = "12";
                } else if (isDlShow) {
                    sendErrorData("03");
                    deviceStatus = "13";
                } else if (isDlycShow) {
                    sendErrorData("01");
                    deviceStatus = "11";
                } else if (isCheckLgNumShow) {
                    sendErrorData("07");
                    deviceStatus = "17";
                } else {
                    isInterunptQb = false;
                    isHaveError = false;
                    deviceStatus = "05";
                }
            } else if ("05".equals(event.getData()) || "06".equals(event.getData()) ) {
                if (cdfailDialog != null && cdfailDialog.isShowing()) {
                    cdfailDialog.dismiss();
                    firstThread = new ThreadFirst(allBlastQu);
                    firstThread.exit = false;
                    firstThread.start();
                    increase(6);//之前是4
                }
                isInterunptQb = false;
                isHaveError = false;
                deviceStatus = "04";
            } else if ("07".equals(event.getData())) {
                if (checkLgNumDialog != null && checkLgNumDialog.isShowing()) {
                    if (checkLgNumFlag == 1) {
                    } else {
                        fourOnlineDenatorFlag = 3;
                    }
                    checkLgNumDialog.dismiss();
                    isCheckLgNumShow = false;
                }
                if (isCheckLgNumShow) {
                    sendErrorData("07");
                    deviceStatus = "17";
                } else if (isDlgdShow) {
                    sendErrorData("02");
                    deviceStatus = "12";
                }  else if (isDlShow) {
                    sendErrorData("03");
                    deviceStatus = "13";
                } else if (isDlycbdShow) {
                    sendErrorData("04");
                    deviceStatus = "14";
                } else if (isDlycShow) {
                    sendErrorData("01");
                    deviceStatus = "11";
                } else {
                    updateJlStatus();
                }
            } else if ("08".equals(event.getData())) {
                if (registDialog != null && registDialog.isShowing()) {
                    isHaveError = false;
                    isInterunptQb = false;
                    deviceStatus = "01";
                    registDialog.dismiss();
                    firstThread.start();
                }
            }
            sendClycjg();
        }
    }

    /**
     * 检查重复的历史记录
     * @param time
     * @return
     */
    public boolean checkRepeatHis(String time) {
        Log.e(TAG, "判断是否生成历史记录 time: "+ time);
//        DenatorHis_Main denatorHis_Main = new GreenDaoMaster().checkRepeatHis(time);
        long recordCount = new GreenDaoMaster().checkRepeatHis(time);
        if (recordCount > 0) {
            Log.e(TAG, "判断是否生成历史记录 his: "+true + "--recordCount:" + recordCount);
            return true;
        } else {
            Log.e(TAG, "判断是否生成历史记录 his: "+false  + "--recordCount:" + recordCount);
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppLogUtils.writeAppXBLog("退出起爆页面了");
            exitJl();
            closeThread();
            closeForm();
            Utils.writeRecord("---点击返回按键退出起爆界面---");
            return true;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (db != null) db.close();
//        Utils.saveFile();//把软存中的数据存入磁盘中
        sendCmd(ThreeFiringCmd.setToXbCommon_FiringExchange_5523_6("00"));//35
        closeThread();
        closeForm();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication.closeSerialPort();
                Log.e(TAG, "调用mApplication.closeSerialPort()开始关闭串口了。。");
                mSerialPort = null;
            }
        }).start();
        super.onDestroy();
        fixInputMethodManagerLeak(this);
        removeActivity();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        AppLogUtils.writeAppXBLog("退出起爆界面");
        Utils.writeRecord("---退出起爆页面---");
    }
}
