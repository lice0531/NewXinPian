package android_serialport_api.xingbang.firingdevice;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.MyRecyclerView;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_DetailDao;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From12Reister;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.Defactory;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.Denator_type;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 单发检测
 */
public class ReisterMainPage_line extends SerialPortActivity {

    @BindView(R.id.text_start)
    TextView textStart;
    @BindView(R.id.entBF2Bit_st)
    EditText entBF2BitSt;
    @BindView(R.id.entproduceDate_st)
    EditText entproduceDateSt;
    @BindView(R.id.entAT1Bit_st)
    EditText entAT1BitSt;
    @BindView(R.id.entboxNoAndSerial_st)
    EditText entboxNoAndSerialSt;
    @BindView(R.id.btn_ReisterScanStart_st)
    Button btnReisterScanStartSt;//扫描起始位
    @BindView(R.id.btn_ReisterScanStart_ed)
    Button btnReisterScanStartEd;//扫描按钮终止位
    @BindView(R.id.entBF2Bit_ed)
    EditText entBF2BitEd;
    @BindView(R.id.entproduceDate_ed)
    EditText entproduceDateEd;
    @BindView(R.id.entAT1Bit_ed)
    EditText entAT1BitEd;
    @BindView(R.id.entboxNoAndSerial_ed)
    EditText entboxNoAndSerialEd;
    @BindView(R.id.setDelayTime_startDelaytime)
    EditText et_startDelay;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.btn_f1)
    Button reBtnF1;
    @BindView(R.id.re_et_f1)
    EditText reEtF1;
    @BindView(R.id.btn_f2)
    Button reBtnF2;
    @BindView(R.id.re_et_f2)
    EditText reEtF2;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.ly_setDelay)
    LinearLayout lySetDelay;
    @BindView(R.id.btn_return)
    Button btnReturn;
    @BindView(R.id.btn_inputOk)
    Button btnInputOk;
    @BindView(R.id.btn_singleReister)
    Button btnSingleReister;
    @BindView(R.id.btn_LookHistory)
    Button btnLookHistory;
    @BindView(R.id.btn_setdelay)
    Button btnSetdelay;
    @BindView(R.id.mainBasicPage)
    LinearLayout mainBasicPage;
    @BindView(R.id.txt_currentVolt)
    TextView txtCurrentVolt;
    @BindView(R.id.txt_currentIC)
    TextView txtCurrentIC;
    @BindView(R.id.txt_reisteramount)
    TextView txtReisteramount;
    @BindView(R.id.factory_listView)
    MyRecyclerView mListView;
    @BindView(R.id.ly_showData)
    LinearLayout lyShowData;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.container1)
    LinearLayout container1;
    @BindView(R.id.textView9)
    TextView textView9;
    @BindView(R.id.re_gkm)
    LinearLayout regkm;
    @BindView(R.id.text_gkm1)
    TextView text_gkm;
    @BindView(R.id.text_gkm2)
    TextView text_uid;

    @BindView(R.id.tr_1)
    TableRow tr1;
    @BindView(R.id.re_num_f1)
    TextView reNumF1;
    @BindView(R.id.re_et_nei1)
    Button btnFan1;
    @BindView(R.id.re_btn_f1)
    Button btnDuan1;
    @BindView(R.id.tr_2)
    TableRow tr2;
    @BindView(R.id.re_num_f2)
    TextView reNumF2;
    @BindView(R.id.re_btn_f2)
    Button btnDuan2;
    @BindView(R.id.re_et_nei2)
    Button btnFan2;
    @BindView(R.id.tr_3)
    TableRow tr3;
    @BindView(R.id.re_num_f3)
    TextView reNumF3;
    @BindView(R.id.re_btn_f3)
    Button btnDuan3;
    @BindView(R.id.re_et_nei3)
    Button btnFan3;
    @BindView(R.id.tr_4)
    TableRow tr4;
    @BindView(R.id.re_num_f4)
    TextView reNumF4;
    @BindView(R.id.re_btn_f4)
    Button btnDuan4;
    @BindView(R.id.re_et_nei4)
    Button btnFan4;
    @BindView(R.id.tr_5)
    TableRow tr5;
    @BindView(R.id.re_num_f5)
    TextView reNumF5;
    @BindView(R.id.re_btn_f5)
    Button btnDuan5;
    @BindView(R.id.re_et_nei5)
    Button btnFan5;
    @BindView(R.id.tr_6)
    TableRow tr6;
    @BindView(R.id.re_num_f6)
    TextView reNumF6;
    @BindView(R.id.re_btn_f6)
    Button btnDuan6;
    @BindView(R.id.re_et_nei6)
    Button btnFan6;
    @BindView(R.id.tr_7)
    TableRow tr7;
    @BindView(R.id.re_num_f7)
    TextView reNumF7;
    @BindView(R.id.re_btn_f7)
    Button btnDuan7;
    @BindView(R.id.re_et_nei7)
    Button btnFan7;
    @BindView(R.id.tr_8)
    TableRow tr8;
    @BindView(R.id.re_num_f8)
    TextView reNumF8;
    @BindView(R.id.re_et_nei8)
    Button btnFan8;
    @BindView(R.id.re_btn_f8)
    Button btnDuan8;
    @BindView(R.id.tr_9)
    TableRow tr9;
    @BindView(R.id.re_num_f9)
    TextView reNumF9;
    @BindView(R.id.re_btn_f9)
    Button btnDuan9;
    @BindView(R.id.re_et_nei9)
    Button btnFan9;
    @BindView(R.id.tr_10)
    TableRow tr10;
    @BindView(R.id.re_num_f10)
    TextView reNumF10;
    @BindView(R.id.re_btn_f10)
    Button btnDuan10;
    @BindView(R.id.re_et_nei10)
    Button btnFan10;
    @BindView(R.id.tr_11)
    TableRow tr11;
    @BindView(R.id.re_num_f11)
    TextView reNumF11;
    @BindView(R.id.re_btn_f11)
    Button btnDuan11;
    @BindView(R.id.re_et_nei11)
    Button btnFan11;
    @BindView(R.id.tr_12)
    TableRow tr12;
    @BindView(R.id.re_num_f12)
    TextView reNumF12;
    @BindView(R.id.re_btn_f12)
    Button btnDuan12;
    @BindView(R.id.re_et_nei12)
    Button btnFan12;
    @BindView(R.id.tr_13)
    TableRow tr13;
    @BindView(R.id.re_num_f13)
    TextView reNumF13;
    @BindView(R.id.re_btn_f13)
    Button btnDuan13;
    @BindView(R.id.re_et_nei13)
    Button btnFan13;
    @BindView(R.id.tr_14)
    TableRow tr14;
    @BindView(R.id.re_num_f14)
    TextView reNumF14;
    @BindView(R.id.re_btn_f14)
    Button btnDuan14;
    @BindView(R.id.re_et_nei14)
    Button btnFan14;
    @BindView(R.id.tr_15)
    TableRow tr15;
    @BindView(R.id.re_num_f15)
    TextView reNumF15;
    @BindView(R.id.re_btn_f15)
    Button btnDuan15;
    @BindView(R.id.re_et_nei15)
    Button btnFan15;
    @BindView(R.id.tr_16)
    TableRow tr16;
    @BindView(R.id.re_num_f16)
    TextView reNumF16;
    @BindView(R.id.re_btn_f16)
    Button btnDuan16;
    @BindView(R.id.re_et_nei16)
    Button btnFan16;
    @BindView(R.id.tr_17)
    TableRow tr17;
    @BindView(R.id.re_num_f17)
    TextView reNumF17;
    @BindView(R.id.re_btn_f17)
    Button btnDuan17;
    @BindView(R.id.re_et_nei17)
    Button btnFan17;
    @BindView(R.id.tr_18)
    TableRow tr18;
    @BindView(R.id.re_num_f18)
    TextView reNumF18;
    @BindView(R.id.re_btn_f18)
    Button btnDuan18;
    @BindView(R.id.re_et_nei18)
    Button btnFan18;
    @BindView(R.id.tr_19)
    TableRow tr19;
    @BindView(R.id.re_num_f19)
    TextView reNumF19;
    @BindView(R.id.re_btn_f19)
    Button btnDuan19;
    @BindView(R.id.re_et_nei19)
    Button btnFan19;
    @BindView(R.id.tr_20)
    TableRow tr20;
    @BindView(R.id.re_num_f20)
    TextView reNumF20;
    @BindView(R.id.re_btn_f20)
    Button btnDuan20;
    @BindView(R.id.re_et_nei20)
    Button btnFan20;

    private SimpleCursorAdapter adapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private String factoryCode = null;//厂家代码
    private String factoryFeature = null;////厂家特征码
    private String deTypeName = null;//雷管类型名称
    private String deTypeSecond = null;//该类型雷管最大延期值
    //是否单发注册
    private int isSingleReisher = 0;
    //单发注册
    //设置延时
    private int sanButtonFlag = 0;//1s是起始按钮，2是终止按钮
    private Handler mHandler_tip = new Handler();//错误提示
    private Handler mHandler_1 = new Handler();//提示电源信息
    private Handler mHandler_2 = new Handler();//显示进度条

    private static int tipInfoFlag = 0;//提示类型1.显示电流电压2.提示未注册雷管
    private EditText edit_start_entBF2Bit_st;//开始厂家码
    private EditText edit_start_entproduceDate_st;//开始日期码
    private EditText edit_start_entAT1Bit_st;//开始特征码
    private EditText edit_start_entboxNoAndSerial_st;//开始流水号
    private EditText edit_end_entBF2Bit_en;//结束厂家码
    private EditText edit_end_entproduceDate_ed;//结束日期码
    private EditText edit_end_entAT1Bit_ed;//结束特征码
    private EditText edit_end_entboxNoAndSerial_ed;//结束流水号
    private String singleShellNo;//单发注册
    private String lg_No;//重复雷管管壳码
    private String lg_Piece;//重复雷管区号
    private From42Power busInfo;
    private int continueScanFlag = 0;
    private TextView txt_currentVolt;
    private TextView txt_currentIC;
    private SendOpenPower sendOpenThread;
    private CloseOpenPower closeOpenThread;
    private ScanInterface scanDecode;
    private volatile int initCloseCmdReFlag = 0;
    private volatile int initOpenCmdReFlag = 0;
    private volatile int revCloseCmdReFlag = 0;
    private volatile int revOpenCmdReFlag = 0;
    private volatile int zhuce_Flag = 0;//单发检测时发送40的标识
    private From12Reister zhuce_form = null;
    private ProgressDialog builder = null;
    private LoadingDialog tipDlg = null;
    private int isCorrectReisterFea = 0; //是否正确的管厂码
    private TextView f1DelayTxt;//孔内延时
    private TextView f2DelayTxt;//孔间延时
    private TextView deTotalTxt;//雷管总数
    private LinearLayout ly_setDelay;//设置延时
    private LinearLayout ly_showData;//展示栏
    private int maxSecond = 0;//最大秒数
    private int pb_show = 0;
    private String delay_set = "0";//是f1还是f2
    private String selectDenatorId;//选择的管壳码
    //这是注册了一个观察者模式
    public static final Uri uri = Uri.parse("content://android_serialport_api.xingbang.denatorBaseinfo");
    private String qiaosi_set = "";//是否检测桥丝
    private String version = "";//版本
    private List<DenatorBaseinfo> list_data = new ArrayList<>();
    private final ArrayList lg2_yanshi = new ArrayList();
    private int send_13 = 0;
    private int send_10 = 0;
    private int send_41 = 0;
    private int send_40 = 0;

    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private DetonatorAdapter_Paper<DenatorBaseinfo> mAdapter;
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private Handler mHandler_0 = new Handler();     // UI处理
    private String mOldTitle;   // 原标题
    private String mRegion;     // 区域
    private boolean switchUid = true;//切换uid/管壳码

    //段属性
    private int duan = 1;//duan
    private int maxDuanNo = 3;
    private Handler mHandler_showNum = new Handler();//显示雷管数量
    private String duan_set = "0";//是duan1还是duan2
    private int f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20;
    private int n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20 = 0;
    private String TAG="单发注册";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reister_main_page_line);
        ButterKnife.bind(this);
        SoundPlayUtils.init(this);

        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        //扫描参数设置
        init();
        //管壳号扫描分码--结束
        getUserMessage();
        getFactoryCode();//获取厂家码
        getFactoryType();//获取延期最大值

        initView();

        btn_onClick();//button的onClick

        handler();//所有的handler
        scan();//扫描初始化

        if (factoryFeature != null && factoryFeature.trim().length() == 1) {
            edit_end_entAT1Bit_ed.setText(factoryFeature);
            edit_start_entAT1Bit_st.setText(factoryFeature);
        }
        if (factoryCode != null && factoryCode.trim().length() > 0) {
            edit_end_entBF2Bit_en.setText(factoryCode);
            edit_end_entBF2Bit_en.setFocusable(false);
            edit_start_entBF2Bit_st.setFocusable(false);
            edit_start_entBF2Bit_st.setText(factoryCode);
        }

        showDenatorSum();//显示雷管总数
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        Utils.writeRecord("---进入单发注册页面---");
        if (version.equals("01")) {
            sendCmd(FourStatusCmd.send46("00", "02"));//20(第一代)
        } else {
            sendCmd(FourStatusCmd.send46("00", "02"));//20(第二代)
        }
//        send 12("C000120AFF0191A8FF007DA6CB04B2E6C0");//测试命令用
        hideInputKeyboard();

        //初始化段间延时显示
        int maxduan = getMaxDuanNo();
        Log.e("显示", "maxduan: " + maxduan);
        if (maxduan < 3) {
            maxDuanNo = 3;
        } else {
            maxDuanNo = maxduan;
            for (int i = maxDuanNo; i > 3; i--) {
                setView(i);
                Log.e("显示", "maxDuanNo: " + maxDuanNo);
            }
        }
        //初始化雷管数量
        for (int i = 1; i < 21; i++) {
            showDuanSum(i);
        }
        //初始化翻转按钮颜色
        setFan();
    }

    private void initView() {
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
//         获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        // 原标题
        mOldTitle = getSupportActionBar().getTitle().toString();
        // 设置标题区域
        setTitleRegion(mRegion, -1);
        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new DetonatorAdapter_Paper<>(this, 4);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnItemLongClick(position -> {
            Log.e("长按", "mListData.size(): " + mListData.size());
            Log.e("长按", "position: " + position);
            DenatorBaseinfo info = mListData.get(position);

            int no = info.getBlastserial();
            int delay = info.getDelay();
            String shellBlastNo = info.getShellBlastNo();

            // 序号 延时 管壳码
            modifyBlastBaseInfo(no, delay, shellBlastNo);
        });
        this.isSingleReisher = 0;
    }

    private void getUserMessage() {
        MessageBean messageBean = GreenDaoMaster.getAllFromInfo_bean();
        qiaosi_set = messageBean.getQiaosi_set();
        version = messageBean.getVersion() + "";
    }

    private void scan() {
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");//初始化扫描服务

        scanDecode.getBarCode(data -> {
//            if (data.length() == 19) {
//                Log.e("箱号", "getBarcode: " + data);
//                addXiangHao(data);
//            }
            if (sanButtonFlag > 0) {
                scanDecode.stopScan();
                decodeBar(data);
            } else {
//                if (continueScanFlag == 1) {
//                    String barCode = getContinueScanBlastNo(data);
//                    if (barCode == null) return;
//
//                    if (checkRepeatShellNo(barCode)) {
//                        show_Toast(getResources().getString(R.string.text_error_tip9));
//                        SoundPlayUtils.play(3);
//                        return;
//                    } else {
//                        show_Toast(getResources().getString(R.string.text_error_tip10) + barCode);
//                    }
//                    SoundPlayUtils.play(1);//1,2,3,4
//                    insertSingleDenator(barCode);
//                }
            }
        });
    }

    private void handler() {
        mHandler_0 = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1001:
                    // 查询全部雷管 倒叙(序号)
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();

                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());
                    break;

                // 重新排序 更新视图
                case 1002:
                    // 雷管孔号排序 并 重新查询
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());
                    break;

                // 电源显示
                case 1003:
                    if (busInfo != null) {
                        txt_currentVolt.setText("当前电压:" + busInfo.getBusVoltage() + "V");
                        txt_currentIC.setText("当前电流:" + Math.round(busInfo.getBusCurrentIa() * 1000) + "μA");
                        // 判断当前电流是否偏大
                        if (Math.round(busInfo.getBusCurrentIa() * 1000) > 24) {
                            txt_currentIC.setTextColor(Color.RED);
                        } else {
                            txt_currentIC.setTextColor(Color.GREEN);
                        }
                    }
                    break;
                case 1005://按管壳码排序
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    Collections.sort(mListData);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;


                default:
                    break;
            }

            return false;
        });


        mHandler_2 = new Handler(message -> {
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });
        mHandler_tip = new Handler(msg -> {
            if (msg.what == 1) {
                SoundPlayUtils.play(4);
                show_Toast(getResources().getString(R.string.text_error_tip1));
                //"雷管信息有误，管厂码不正确，请检查"
            } else if (msg.what == 2) {
                SoundPlayUtils.play(4);
                show_Toast(getResources().getString(R.string.text_error_tip2));
            } else if (msg.what == 3) {
                SoundPlayUtils.play(4);
                show_Toast("已达到最大延时限制" + maxSecond + "ms");
            } else if (msg.what == 4) {
                SoundPlayUtils.play(4);
                show_Toast_long("与"+lg_Piece+"区第" + lg_No + "发" + singleShellNo + "重复");
                int total = showDenatorSum();
//                reisterListView.setSelection(total - Integer.parseInt(lg_No));
                MoveToPosition(linearLayoutManager, mListView, total - Integer.parseInt(lg_No));
            } else if (msg.what == 6) {
                SoundPlayUtils.play(4);
                show_Toast("当前管壳码不等于13位,请检查雷管或系统版本是否符合后,再次注册");
            } else if (msg.what == 10) {
                show_Toast("找不到对应的生产数据,请先导入生产数据");
            } else if (msg.what == 99) {
                adapter.notifyDataSetChanged();
            }else if (msg.what == 2001) {
                show_Toast(msg.obj.toString());
                SoundPlayUtils.play(4);
            } else {
                SoundPlayUtils.play(4);
                show_Toast("注册失败");
            }
            return false;
        });
        mHandler_1 = new Handler(message -> {
            if (tipInfoFlag == 1) {
                if (busInfo != null) {
                    txt_currentVolt.setText(getResources().getString(R.string.text_reister_vol) + busInfo.getBusVoltage() + "V");
                    txt_currentIC.setText(getResources().getString(R.string.text_reister_ele) + busInfo.getBusCurrentIa() + "μA");
                    if (Math.round(busInfo.getBusCurrentIa()) > 24) {//判断当前电流是否偏大
                        txt_currentIC.setTextColor(Color.RED);
                    } else {
                        txt_currentIC.setTextColor(Color.GREEN);
                    }
                }
            }
            if (tipInfoFlag == 2) {//提示已注册多少发
                if (busInfo != null) {
                    byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//获取电源信息
                    sendCmd(reCmd);
                }
                showDenatorSum();//得到数据的总条数
            }
            if (tipInfoFlag == 3) {//未收到关闭电源命令
                show_Toast("未收到单片机返回命令");
            }
            if (tipInfoFlag == 4) {//未收到打开电源命令
                show_Toast(getResources().getString(R.string.text_error_tip6));
            }
            if (tipInfoFlag == 5) {//桥丝不正常
                show_Toast(getResources().getString(R.string.text_error_tip7));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 6) {//桥丝不正常
                show_Toast("请先设置延时");
            }
            if (tipInfoFlag == 7) {//桥丝不正常
                show_Toast("当前注册雷管电流过大,请检查雷管");
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 8) {//桥丝不正常
                show_Toast("当前雷管有异常,请检测后重新注册");
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 9) {//桥丝不正常
                show_Toast("当前雷管读码异常,请检查该雷管编码规则");
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 88) {//刷新界面
                showDenatorSum();
                edit_start_entboxNoAndSerial_st.getText().clear();
                edit_end_entboxNoAndSerial_ed.getText().clear();//.setText("")
            }
            if (tipInfoFlag == 89) {//刷新界面
                show_Toast("输入的管壳码重复");
                showDenatorSum();
                SoundPlayUtils.play(4);
            }

            return false;
        });

        mHandler_showNum = new Handler(msg -> {
            int pos = msg.arg1;//段号
            Log.e("更新段雷管数量", "pos: " + pos);
            showDuanSum(pos);
            return false;
        });
    }

    private void btn_onClick() {
        //设置延时
        f1DelayTxt = findViewById(R.id.re_et_f1);//F1延时
        f2DelayTxt = findViewById(R.id.re_et_f2);//F2延时
        ly_setDelay = findViewById(R.id.ly_setDelay);
        ly_showData = findViewById(R.id.ly_showData);
        String save_f1 = (String) MmkvUtils.getcode("f1", "1");
        String save_f2 = (String) MmkvUtils.getcode("f2", "1");
        String save_start = (String) MmkvUtils.getcode("start", "1");
        if (!save_f1.equals("1")) {
            f1DelayTxt.setText(save_f1);
        } else {
            f1DelayTxt.setText("10");
        }
        if (!save_f2.equals("1")) {
            f2DelayTxt.setText(save_f2);
        } else {
            f2DelayTxt.setText("15");
        }
        if (!save_start.equals("1")) {
            et_startDelay.setText(save_start);
        } else {
            et_startDelay.setText("10");
        }

//        List<denatorBaseinfo> list = LitePal.findAll(denatorBaseinfo.class);
//        int serNum =list.size();//得到数据的总条数

        regkm.setOnClickListener(v -> {
            int a;
            if (switchUid) {
                a = 6;
                switchUid = false;
                text_uid.setTextColor(Color.GREEN);
                text_gkm.setTextColor(Color.BLACK);
            } else {
                a = 4;
                switchUid = true;
                text_uid.setTextColor(Color.BLACK);
                text_gkm.setTextColor(Color.GREEN);
            }
            mAdapter = new DetonatorAdapter_Paper<>(ReisterMainPage_line.this, a);
            mListView.setLayoutManager(linearLayoutManager);
            mListView.setAdapter(mAdapter);
            mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        });
    }



    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        container1 = (LinearLayout) findViewById(R.id.container1);
        txt_currentVolt = (TextView) findViewById(R.id.txt_currentVolt);
        txt_currentIC = (TextView) findViewById(R.id.txt_currentIC);
        //扫描结束
        //管壳号扫描分码--开始
        edit_start_entBF2Bit_st = (EditText) this.findViewById(R.id.entBF2Bit_st);//开始厂家码
        edit_start_entBF2Bit_st.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayInputKeyboard(v, hasFocus);
            }
        });
        edit_start_entBF2Bit_st.addTextChangedListener(st_1_watcher);
        edit_start_entproduceDate_st = (EditText) this.findViewById(R.id.entproduceDate_st);//开始日期码
        edit_start_entproduceDate_st.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayInputKeyboard(v, hasFocus);
            }
        });
        edit_start_entproduceDate_st.addTextChangedListener(st_2_watcher);

        edit_start_entAT1Bit_st = (EditText) this.findViewById(R.id.entAT1Bit_st);//开始特征码
        edit_start_entAT1Bit_st.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayInputKeyboard(v, hasFocus);
            }
        });
        edit_start_entAT1Bit_st.addTextChangedListener(st_3_watcher);

        edit_start_entboxNoAndSerial_st = findViewById(R.id.entboxNoAndSerial_st);//开始流水号
        edit_start_entboxNoAndSerial_st.setOnFocusChangeListener((v, hasFocus) -> displayInputKeyboard(v, hasFocus));

        container1.setOnTouchListener((v, event) -> {//点击空白位置 隐藏软键盘
            if (null != ReisterMainPage_line.this.getCurrentFocus()) {
                container1.setFocusable(true);
                container1.setFocusableInTouchMode(true);
                container1.requestFocus();
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return mInputMethodManager.hideSoftInputFromWindow(ReisterMainPage_line.this.getCurrentFocus().getWindowToken(), 0);
            }
            return false;
        });
        edit_start_entboxNoAndSerial_st.addTextChangedListener(st_4_watcher);
        edit_start_entboxNoAndSerial_st.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    edit_end_entboxNoAndSerial_ed.setFocusable(true);
                    edit_end_entboxNoAndSerial_ed.setFocusableInTouchMode(true);
                    edit_end_entboxNoAndSerial_ed.requestFocus();
                    edit_end_entboxNoAndSerial_ed.findFocus();
                }
                return false;
            }
        });

        edit_end_entBF2Bit_en = (EditText) this.findViewById(R.id.entBF2Bit_ed);
        edit_end_entBF2Bit_en.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayInputKeyboard(v, hasFocus);
            }
        });
        edit_end_entBF2Bit_en.addTextChangedListener(end_1_watcher);

        edit_end_entproduceDate_ed = (EditText) this.findViewById(R.id.entproduceDate_ed);
        edit_end_entproduceDate_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayInputKeyboard(v, hasFocus);
            }
        });
        edit_end_entproduceDate_ed.addTextChangedListener(end_2_watcher);

        edit_end_entAT1Bit_ed = (EditText) this.findViewById(R.id.entAT1Bit_ed);
        edit_end_entAT1Bit_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayInputKeyboard(v, hasFocus);
            }
        });
        edit_end_entAT1Bit_ed.addTextChangedListener(end_3_watcher);

        edit_end_entboxNoAndSerial_ed = (EditText) this.findViewById(R.id.entboxNoAndSerial_ed);
        edit_end_entboxNoAndSerial_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayInputKeyboard(v, hasFocus);
            }
        });
        edit_end_entboxNoAndSerial_ed.addTextChangedListener(end_4_watcher);
    }

    private int showDenatorSum() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryDetonatorRegionDesc(mRegion);
        txtReisteramount.setText("已注册:" + list.size());
        return list.size();
    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(ReisterMainPage_line.this);
        Context context = tipDlg.getContext();
//        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
//        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
        //tipDlg.setMessage("正在操作,请等待...").show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                //mHandler_2
                mHandler_2.sendMessage(mHandler_2.obtainMessage());
                //builder.show();
                try {
                    while (pb_show == 1) {
                        Thread.sleep(100);
                    }
                    //builder.dismiss();
                    mHandler_2.sendMessage(mHandler_2.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private ProgressDialog showPbDialog() {
        builder = new ProgressDialog(ReisterMainPage_line.this);
        View view = LayoutInflater.from(ReisterMainPage_line.this).inflate(R.layout.pb_loading, null);
        builder.setView(view);
        return builder;
    }

    /**
     * 获得厂家管码
     */
    private void getFactoryCode() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<Defactory> list = master.queryDefactoryToIsSelected("是");
        if (list.size() > 0) {
            factoryCode = list.get(0).getDeEntCode();
            factoryFeature = list.get(0).getDeFeatureCode();
        }
        Log.e("厂家管码", "factoryCode: " + factoryCode);
    }

    /**
     * 获得最大延时
     */
    private void getFactoryType() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<Denator_type> list = master.queryDefactoryTypeToIsSelected("是");
        if (list.size() > 0) {
            deTypeName = list.get(0).getDeTypeName();
            deTypeSecond = list.get(0).getDeTypeSecond();
        }
        if (deTypeSecond != null) {
            maxSecond = Integer.parseInt(deTypeSecond);
        }
    }

    @Override
    public void sendInterruptCmd() {
        byte[] reCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");
        try {
            mOutputStream.write(reCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.sendInterruptCmd();
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

    public void hideInputKeyboard() {

        edit_start_entBF2Bit_st.clearFocus();//取消焦点
        edit_start_entproduceDate_st.clearFocus();
        edit_start_entAT1Bit_st.clearFocus();
        edit_start_entboxNoAndSerial_st.clearFocus();
        edit_end_entBF2Bit_en.clearFocus();
        edit_end_entproduceDate_ed.clearFocus();
        edit_end_entAT1Bit_ed.clearFocus();
        edit_end_entboxNoAndSerial_ed.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        //         获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
//        if (reEtF1.getText().length() > 0) {
//            MmkvUtils.savecode("f1", reEtF1.getText().toString());
//        }
//        if (reEtF2.getText().length() > 0) {
//            MmkvUtils.savecode("f2", reEtF2.getText().toString());
//        }
//        MmkvUtils.savecode("start", setDelayTimeStartDelaytime.getText().toString());

        Utils.saveFile();//把软存中的数据存入磁盘中
//        loadMoreData_all_lg();//

        if (db != null) db.close();
        scanDecode.stopScan();//停止扫描
        scanDecode.onDestroy();//回复初始状态
        if (reEtF1.getText().length() > 0) {
            MmkvUtils.savecode("f1", reEtF1.getText().toString());
        }
        if (reEtF2.getText().length() > 0) {
            MmkvUtils.savecode("f2", reEtF2.getText().toString());
        }
        MmkvUtils.savecode("start", et_startDelay.getText().toString());
        super.onDestroy();
        fixInputMethodManagerLeak(this);
    }

    /****
     * 校验数据
     */
    private String checkData() {
        String tipStr = "";
        String st2Bit = edit_start_entBF2Bit_st.getText().toString();
        String stproDt = edit_start_entproduceDate_st.getText().toString();
        String st1Bit = edit_start_entAT1Bit_st.getText().toString();
        String stsno = edit_start_entboxNoAndSerial_st.getText().toString();
        String ed2Bit = edit_end_entBF2Bit_en.getText().toString();
        String edproDt = edit_end_entproduceDate_ed.getText().toString();
        String ed1Bit = edit_end_entAT1Bit_ed.getText().toString();
        String edsno = edit_end_entboxNoAndSerial_ed.getText().toString();
        if (!StringUtils.isNotBlank(st2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip11);//"起始厂家码不能为空"
            return tipStr;
        }
        if (!StringUtils.isNotBlank(stproDt)) {
            tipStr = getResources().getString(R.string.text_error_tip12);//起始生产日期不能为空
            return tipStr;
        }
        if (!StringUtils.isNotBlank(st1Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip13);// "起始特征码不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(stsno)) {
            tipStr = getResources().getString(R.string.text_error_tip14); //"起始序号不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(ed2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip15);// "结束厂家码不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(edproDt)) {
            tipStr = getResources().getString(R.string.text_error_tip16);//  "结束生产日期不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(ed1Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip17);//  "结束特征码不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(edsno)) {
            tipStr = getResources().getString(R.string.text_error_tip18);//  "结束序列号不能为空";
            return tipStr;
        }
        if (!st2Bit.equals(ed2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip19);//  "管厂码不一致";
            return tipStr;
        }
        if (factoryCode != null && factoryCode.trim().length() > 0 && !factoryCode.contains(st2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip21);//  "管厂码与系统中定义的管厂码不一致";
            return tipStr;
        }
        if (!stproDt.equals(edproDt)) {
            tipStr = getResources().getString(R.string.text_error_tip22);// "日期不一致";
            return tipStr;
        }
        if (!st1Bit.equals(ed1Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip23);//  "特征码不一致";
            return tipStr;
        }
        if (factoryFeature != null && factoryFeature.trim().length() > 0 && !factoryFeature.contains(st1Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip24);//  "特征码与系统中定义的特征码不一致";
            return tipStr;
        }
        if (!Utils.isNum(stsno)) {
            tipStr = getResources().getString(R.string.text_error_tip25);//  "开始序号不是数字";
            return tipStr;
        }
        if (!Utils.isNum(edsno)) {
            tipStr = getResources().getString(R.string.text_error_tip26);//  "结束序号不是数字";
            return tipStr;
        }
        int start = Integer.parseInt(stsno);
        int end = Integer.parseInt(edsno);
        if (end < start) {
            tipStr = getResources().getString(R.string.text_error_tip27);//  "结束序号不能小于开始序号";
        }
        if (start < 0 || end > 99999) {
            tipStr = getResources().getString(R.string.text_error_tip28);//  "起始/结束序号不符合要求";
        }
        if ((end - start) > 1000)
            tipStr = getResources().getString(R.string.text_error_tip29);//  "每一次注册数量不能大于1000";
        return tipStr;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) info.id;//这里的info.id对应的就是数据库中_id的值
        String Temp = "";
        switch (item.getItemId()) {
            case 1:
                Temp = "删除";
                String whereClause = "id=?";
                String[] whereArgs = {String.valueOf(id)};
                db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, whereClause, whereArgs);
//                getLoaderManager().restartLoader(1, null, ReisterMainPage_line.this);
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                Utils.saveFile();//把软存中的数据存入磁盘中
                break;

            case 2:
                this.modifyBlastBaseInfo(id);
                Temp = "修改";
                break;

            default:
                break;
        }
        show_Toast(Temp + "处理");
        return super.onContextItemSelected(item);
    }

    private void modifyBlastBaseInfo(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请修改雷管信息");
        View view = LayoutInflater.from(this).inflate(R.layout.blastbasedialog, null);
        builder.setView(view);
        final EditText username = (EditText) view.findViewById(R.id.blast_shellBlastNo_field);
        final EditText password = (EditText) view.findViewById(R.id.blast_delay_field);
        String selection = "id = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {id + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {  //cursor不位空,可以移动到第一行
            //int _id = cursor.getInt(0);
            String name = cursor.getString(1);
            String age = cursor.getString(2);
            username.setText(name);
            password.setText(age);
            cursor.close();
        }
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {
            String a = username.getText().toString().trim();
            String b = password.getText().toString().trim();
            //    将输入的用户名和密码打印出来
            show_Toast("管壳码: " + a + ", 延时: " + b);
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> {

        });
        builder.show();

    }

    /**
     * 修改雷管延期 弹窗
     */
    private void modifyBlastBaseInfo(int no, int delay, final String shellBlastNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delaymodifydialog, null);
        builder.setView(view);

        EditText et_no = view.findViewById(R.id.serialNo);
        EditText et_shell = view.findViewById(R.id.denatorNo);
        EditText et_delay = view.findViewById(R.id.delaytime);

        et_no.setText(String.valueOf(no));
        et_delay.setText(String.valueOf(delay));
        et_shell.setText(shellBlastNo);
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("删除", (dialog, which) -> {
            dialog.dismiss();

            // TODO 开启进度条

            new Thread(() -> {
                int duan1 =  new GreenDaoMaster().getDuan(shellBlastNo);
                Log.e("单发删除", "duan1: "+duan1);
                // 删除某一发雷管
                new GreenDaoMaster().deleteDetonator(shellBlastNo);
                Utils.deleteData(mRegion);//重新排序雷管
                Utils.writeRecord("--删除雷管:"+shellBlastNo);
                //更新每段雷管数量
                Message msg = new Message();
                msg.arg1 = duan1;
                mHandler_showNum.sendMessage(msg);

                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1002));

            }).start();

        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            String delay1 = et_delay.getText().toString();

            if (maxSecond != 0 && Integer.parseInt(delay1) > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2001, "已达到最大延时限制" + maxSecond + "ms"));

            } else if (delay1.trim().length() < 1 || maxSecond > 0 && Integer.parseInt(delay1) > maxSecond) {
                show_Toast("延时为空或大于最大设定延时，修改失败! ");

            } else {
                Utils.writeRecord("-单发修改延时:" + "-管壳码:" + shellBlastNo + "-延时:" + delay1);
                // 修改雷管延时
                new GreenDaoMaster().updateDetonatorDelay(shellBlastNo, Integer.parseInt(delay1));

                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

                show_Toast(shellBlastNo + "\n修改成功");

                Utils.saveFile();
            }
            dialog.dismiss();
        });
        builder.show();
    }


    public int modifyDelayTime(String id, String delay) {
        ContentValues values = new ContentValues();
        values.put("delay", delay);
        db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + id});
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }

    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);

        String fromCommad = Utils.bytesToHexFun(cmdBuf);
//        Log.e("注册", "fromCommad: "+fromCommad );
        if (completeValidCmd(fromCommad) == 0) {
//            fromCommad = this.revCmd;//如果revcmd不清空,会导致收到40,但却变成上次的13,先屏蔽
//            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
//            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
            } else {
                String cmd = DefCommand.getCmd(fromCommad);
                if (cmd != null) {
                    doWithReceivData(cmd, cmdBuf);
                }
            }
        }
    }

    /**
     * 关闭守护线程
     */
    private void closeThread() {
        if (closeOpenThread != null) {
            closeOpenThread.exit = true;  // 终止线程thread
            try {
                closeOpenThread.join();
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
                e.printStackTrace();
            }
        }
    }

    //发送命令
    public synchronized void sendCmd(byte[] mBuffer) {//0627添加synchronized,尝试加锁
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Utils.writeLog("->:" + str);
                Log.e("发送命令", str);
                if (str.contains("C00010")) {
                    send_10 = 1;
                } else if (str.contains("C00041")) {
                    send_41 = 1;
                } else if (str.contains("C00013")) {
                    send_13 = 1;
                } else if (str.contains("C00040")) {
                    send_40 = 1;
                }
                mOutputStream.write(mBuffer);
                //实验有没有用
//                mOutputStream.flush();
//                mSerialPort.tcflush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    //模拟
    private void send12() {
        String subStr = "00120CFF00B6E6FF0041A6B6E6FF001503";
        String inInfo = subStr.substring(0, subStr.length() - 4);
        String dcrc = DefCommand.getLowByteBeforeCRCCode(inInfo);
        String fromCommad = dcrc;
        byte[] localBuf = Utils.hexStringToBytes(fromCommad);
        String cmd = DefCommand.getCmd(fromCommad);
        doWithReceivData(cmd, localBuf);//处理cmd命令
        //C00031 0C B6E6FF00 0A00 B6E6FF00 2473 C0
    }

    /**
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf) {
        String fromCommad = Utils.bytesToHexFun(cmdBuf);

        if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//41开启总线电源指令
            send_41 = 0;
//            sendOpenThread.exit = true;
//            Log.e("是否检测桥丝", "qiaosi_set: " + qiaosi_set);
            if (qiaosi_set.equals("true")) {//10 进入自动注册模式(00不检测01检测)桥丝
                sendCmd(OneReisterCmd.setToXbCommon_Reister_Init12_2("00", "01"));
            } else {
                sendCmd(OneReisterCmd.setToXbCommon_Reister_Init12_2("00", "00"));
            }


        } else if (DefCommand.CMD_1_REISTER_1.equals(cmd)) {//10 进入自动注册模式
            send_10 = 0;
            //发送获取电源信息
            byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00");//40
            sendCmd(reCmd);

        } else if (DefCommand.CMD_1_REISTER_3.equals(cmd)) {//12 有雷管接入
            //C0001208 FF 00 B6E6FF00 41 A6 1503 C0  普通雷管
            //C000120C FF 00 B6E6FF00 41 A6 B6E6FF00 1503 C0
            //C000120A FF 00 67D0FA00 03 A6 1704 7F24 C0
            try {
                Thread.sleep(500);//
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //2  连续发三次询问电流指令
            byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00");//40获取电源信息
            sendCmd(reCmd);
//            zhuce_form = OneReisterCmd.decodeFromReceiveAutoDenatorCommand14("00", cmdBuf, qiaosi_set);//桥丝检测
            zhuce_form = OneReisterCmd.decode14_newXinPian("00", cmdBuf, qiaosi_set);//桥丝检测
            if (qiaosi_set.equals("true") && zhuce_form.getWire().equals("无")) {
                tipInfoFlag = 5;//提示类型桥丝不正常
                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                String detonatorId = Utils.GetShellNoById_newXinPian(zhuce_form.getFacCode(), zhuce_form.getFeature(), zhuce_form.getDenaId());
                Utils.writeRecord("--单发注册--:管壳码:" + serchShellBlastNo(detonatorId) + " 芯片码:" + detonatorId + "该雷管桥丝异常");
            }
            zhuce_Flag = 1;

        } else if (DefCommand.CMD_1_REISTER_4.equals(cmd)) {//13 退出自动注册模式
            send_13 = 0;
//            if (initCloseCmdReFlag == 1) {//打开电源
//                revCloseCmdReFlag = 1;
//                closeOpenThread.exit = true;
//                sendOpenThread = new SendOpenPower();
//                sendOpenThread.start();
//            }

        } else if (DefCommand.CMD_4_XBSTATUS_1.equals(cmd)) { //40 总线电流电压
            send_40 = 0;
            busInfo = FourStatusCmd.decodeFromReceiveDataPower24_1("00", cmdBuf);//解析 40指令
            tipInfoFlag = 1;
            mHandler_1.sendMessage(mHandler_1.obtainMessage());

            if (zhuce_Flag == 1) {//多次单发注册后闪退,busInfo.getBusCurrentIa()为空
                String detonatorId = Utils.GetShellNoById_newXinPian(zhuce_form.getFacCode(), zhuce_form.getFeature(), zhuce_form.getDenaId());
                if (busInfo.getBusCurrentIa() > 24) {//判断当前电流是否偏大
                    tipInfoFlag = 7;
                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                    SoundPlayUtils.play(4);
                    zhuce_Flag = 0;
                    Utils.writeRecord("--单发注册--:管壳码:" + serchShellBlastNo(detonatorId) + "芯片码" + zhuce_form.getDenaId() + "该雷管电流过大");
                }
//                else {
                    if (zhuce_form != null) {//管厂码,特征码,雷管id
//                        // 获取 管壳码
//                        String shellNo = new GreenDaoMaster().getShellNo(detonatorId);
                        insertSingleDenator(detonatorId, zhuce_form);//单发注册
                        zhuce_Flag = 0;
                    }

//                }
            }
        }

    }

    /**
     * 单发注册(存储桥丝状态) 单发注册方法
     */
    private int insertSingleDenator(String detonatorId, From12Reister zhuce_form) {
        // 管厂码
        String facCode = Utils.getDetonatorShellToFactoryCodeStr(detonatorId);
        // 特征码
        String facFea = Utils.getDetonatorShellToFeatureStr(detonatorId);

        //352841778FDE5
        //A62141778FDE5
        Log.e("注册", "detonatorId: " + detonatorId);
        Log.e("注册", "zhuce_form.getZhu_yscs(): " + zhuce_form.getZhu_yscs());
//        String shellBlastNo = serchShellBlastNo(detonatorId);
        DetonatorTypeNew detonatorTypeNew = serchDenatorForDetonatorTypeNew(detonatorId);
//        if (detonatorTypeNew == null) {//考虑到可以直接注册A6
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
//            return -1;
//        }

        if (checkRepeatdenatorId(detonatorId)) {//判断芯片码(要传13位芯片码,不要传8位的,里有截取方法)
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
        //判断管壳码
        if (detonatorTypeNew != null && detonatorTypeNew.getShellBlastNo().length() == 13 && checkRepeatShellNo(detonatorTypeNew.getShellBlastNo())) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
        if (detonatorId.startsWith("00000", 2)) {//判断芯片码开头是否全为0
            tipInfoFlag = 8;
            mHandler_1.sendMessage(mHandler_1.obtainMessage());
            return -1;
        }
        if (detonatorId.length() != 13) {//判断芯片码是否为13位
            tipInfoFlag = 9;
            mHandler_1.sendMessage(mHandler_1.obtainMessage());
            return -1;
        }
        if (detonatorTypeNew != null) {//考虑到可以直接注册A6
            facCode = Utils.getDetonatorShellToFactoryCodeStr(detonatorTypeNew.getShellBlastNo());
        }
        Log.e("查询生产数据库查管壳码", "factoryCode: " + factoryCode);
        Log.e("查询生产数据库查管壳码", "facCode: " + facCode);
//        Log.e("查询生产数据库查管壳码", "ShellBlastNo: " + detonatorTypeNew.getShellBlastNo());
        if (factoryCode != null && factoryCode.trim().length() > 0 && !factoryCode.contains(facCode)&&!facCode.equals("A6")) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
            return -1;
        }
        //验证特征码
//        if (factoryFeature != null && factoryFeature.trim().length() > 0 && !factoryFeature.contains(facFea)) {
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
//            return -1;
//        }
        Log.e("查询生产数据库查管壳码", "detonatorId: " + detonatorId);
        if (et_startDelay.getText().length() == 0 && reEtF1.getText().length() == 0 && reEtF2.getText().length() == 0) {
            tipInfoFlag = 6;
            mHandler_1.sendMessage(mHandler_1.obtainMessage());
            Log.e("验证是否输入延时", "tipInfoFlag: ");
            return -1;
        }
//        int maxNo = getMaxNumberNo();
        int start = Integer.parseInt(String.valueOf(et_startDelay.getText()));//开始延时
        int f1 = Integer.parseInt(String.valueOf(reEtF1.getText()));//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
//        int delay = getMaxDelay(maxNo);//获取最大延时
        // 获取 该区域 最大序号
        int maxNo = new GreenDaoMaster().getPieceMaxNum(mRegion);
        // 获取 该区域 最大序号的延时
        int delay = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);
        if (delay_set.equals("f1")) {
            if (maxSecond != 0 && delay + f1 > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxSecond != 0 && delay + f2 > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        }

        if (maxSecond != 0 && f1 > maxSecond) {//
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
            return -1;
        }
        if (maxSecond != 0 && f2 > maxSecond) {//
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
            return -1;
        }
        if (delay_set.equals("f1")) {//获取最大延时有问题
            if (maxNo == 0) {
                delay = delay + start;
            } else {
                delay = delay + f1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxNo == 0) {
                delay = delay + start;
            } else {
                delay = delay + f2;
            }
        }
        int duanNUM = getDuanNo(duan);//也得做区域区分

        if (!zhuce_form.getWire().equals("无")) {//说明没有空余的序号可用
            maxNo++;
            //从绑码库中获取到的数据
            DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();

            if (detonatorTypeNew != null && detonatorTypeNew.getShellBlastNo().length() == 13) {
                denatorBaseinfo.setShellBlastNo(detonatorTypeNew.getShellBlastNo());
                denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
                Utils.writeRecord("--单发注册--" + "注册雷管码:" + detonatorTypeNew.getShellBlastNo() + " --芯片码:" + zhuce_form.getDenaId());
            } else {
                denatorBaseinfo.setShellBlastNo(detonatorId);
                denatorBaseinfo.setZhu_yscs(zhuce_form.getZhu_yscs());
                Utils.writeRecord("--单发注册--" + " --芯片码:" + zhuce_form.getDenaId());
            }

            if (zhuce_form.getDenaIdSup() != null) {
                String detonatorId_Sup = Utils.GetShellNoById_newXinPian(zhuce_form.getFacCode(), zhuce_form.getFeature(), zhuce_form.getDenaIdSup());
                denatorBaseinfo.setDenatorIdSup(detonatorId_Sup);//从芯片
                denatorBaseinfo.setCong_yscs(detonatorTypeNew.getCong_yscs());
                Utils.writeRecord("--单发注册: 从芯片码:" + zhuce_form.getDenaIdSup());
            }
            denatorBaseinfo.setBlastserial(maxNo);
            denatorBaseinfo.setSithole(maxNo+"");
            denatorBaseinfo.setDelay(delay);
            denatorBaseinfo.setDenatorId(detonatorId);
            denatorBaseinfo.setRegdate(Utils.getDateFormatLong(new Date()));
            denatorBaseinfo.setStatusCode("02");
            denatorBaseinfo.setStatusName("已注册");
            denatorBaseinfo.setErrorCode("FF");
            denatorBaseinfo.setErrorName("正常");
            denatorBaseinfo.setWire(zhuce_form.getWire());//桥丝状态
            denatorBaseinfo.setPiece(mRegion);
            denatorBaseinfo.setDuan(duan);
            denatorBaseinfo.setDuanNo(duan + "-" + (duanNUM + 1));
            //向数据库插入数据
            getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
            //向数据库插入数据
        }
        //更新每段雷管数量
        Message msg = new Message();
        msg.arg1 = duan;
        mHandler_showNum.sendMessage(msg);
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        SoundPlayUtils.play(1);
        return 0;
    }


    /***
     * 得到最大序号
     * @return
     */
    private int getMaxNumberNo() {
        Cursor cursor = db.rawQuery("select max(blastserial) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null);
        if (cursor != null && cursor.moveToNext()) {
            int maxNo = cursor.getInt(0);
            cursor.close();
            return maxNo;
        }
//        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }

    /***
     * 得到最大延时
     * @return
     */
    private int getMaxDelay(int no) {
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where blastserial =? ";
        Cursor cursor = db.rawQuery(sql, new String[]{no + ""});
        if (cursor != null && cursor.moveToNext()) {
            Log.e("延时", "最大延时序号: " + cursor.getInt(0));
            Log.e("延时", "最大延时: " + cursor.getInt(5));
            int maxDelay = cursor.getInt(5);
            cursor.close();
            return maxDelay;
        }
        return 0;
    }

    private int getEmptyDenator(int start) {

        String selection = "shellBlastNo = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, selection, selectionArgs, null, null, null);
        int serialNo = -1;
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                if (start < 0) {
                    break;
                } else {
                    if (serialNo < start) {
                        serialNo = -1;
                        continue;
                    } else {
                        break;
                    }
                }
            }
            cursor.close();
        }
        return serialNo;
    }

    /**
     * 检查重复的数据
     *
     * @param detonatorId 芯片码
     * @return 是否重复
     */
    public boolean checkRepeatdenatorId(String detonatorId) {
        Log.e("检查重复的数据", "detonatorId: " + detonatorId);
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list_lg = master.checkRepeatdenatorId(detonatorId.substring(5));
        if (list_lg.size() > 0) {
            lg_No = list_lg.get(0).getBlastserial() + "";
            lg_Piece=list_lg.get(0).getPiece();
            singleShellNo = list_lg.get(0).getShellBlastNo();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查重复的数据
     *
     * @param shellNo
     * @return
     */
    public boolean checkRepeatShellNo(String shellNo) {
        Log.e("检查重复的数据", "shellNo: " + shellNo);
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list_lg = master.checkRepeatShellNo(shellNo);
        if (list_lg.size() > 0) {
            Log.e("注册", "list_lg: " + list_lg.toString());
            lg_No = list_lg.get(0).getBlastserial() + "";
            singleShellNo = list_lg.get(0).getShellBlastNo();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询生产表中对应的管壳码
     */
    private String serchShellBlastNo(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryDetonatorTypeNew(denatorId);
    }

    /**
     * 查询生产表中对应的管壳码
     */
    private DetonatorTypeNew serchDenatorForDetonatorTypeNew(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryDetonatorForTypeNew(denatorId);
    }


    /**
     * 获取第一发雷管
     */
    private String serchFristLG() {
        List<DenatorBaseinfo> list = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
        if (list.size() > 0) {
            return list.get(0).getShellBlastNo();
        } else {
            return "";
        }
    }

    /**
     * 注册列表的第一发是否在历史列表里
     */
    private int serchFristLGINdenatorHis(String no) {
        if (no.length() > 12) {
            return getDaoSession().getDenatorHis_DetailDao().queryBuilder().where(DenatorHis_DetailDao.Properties.ShellBlastNo.eq(no)).list().size();
        } else {
            return 0;
        }
    }

    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("注册提示")
                .setMessage("注册列表中的雷管已在起爆历史记录里,是否清空列表")//设置对话框的内容"本次任务成功起爆！"
                .setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton("确认清空", (dialog12, which) -> {
                    Application.getDaoSession().getDenatorBaseinfoDao().deleteAll();
//                    getLoaderManager().restartLoader(1, null, ReisterMainPage_line.this);
                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                    //初始化雷管数量
                    for (int i = 1; i < 21; i++) {
                        showDuanSum(i);
                    }
                    dialog12.dismiss();
                }).create();
        Utils.saveFile();//把软存中的数据存入磁盘中
        dialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_f1, R.id.btn_f2, R.id.btn_singleReister, R.id.btn_LookHistory,
            R.id.btn_setdelay, R.id.btn_ReisterScanStart_st, R.id.btn_ReisterScanStart_ed,
            R.id.btn_return, R.id.btn_inputOk, R.id.re_et_f1, R.id.re_et_f2,
            R.id.setDelayTime_startDelaytime, R.id.btn_addDelay,
            R.id.re_btn_f1, R.id.re_btn_f2, R.id.re_btn_f3,
            R.id.re_btn_f4, R.id.re_btn_f5, R.id.re_btn_f6, R.id.re_btn_f7,
            R.id.re_btn_f8, R.id.re_btn_f9, R.id.re_btn_f10, R.id.re_btn_f11, R.id.re_btn_f12, R.id.re_btn_f13,
            R.id.re_btn_f14, R.id.re_btn_f15, R.id.re_btn_f16, R.id.re_btn_f17, R.id.re_btn_f18, R.id.re_btn_f19,
            R.id.re_btn_f20, R.id.re_et_nei1, R.id.re_et_nei2, R.id.re_et_nei3,
            R.id.re_et_nei4, R.id.re_et_nei5, R.id.re_et_nei6, R.id.re_et_nei7,
            R.id.re_et_nei8, R.id.re_et_nei9, R.id.re_et_nei10, R.id.re_et_nei11, R.id.re_et_nei12, R.id.re_et_nei13,
            R.id.re_et_nei14, R.id.re_et_nei15, R.id.re_et_nei16, R.id.re_et_nei17, R.id.re_et_nei18, R.id.re_et_nei19,
            R.id.re_et_nei20,})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.btn_f1:

                hideInputKeyboard();
                if (reEtF1.getText().toString().equals("")) {
                    show_Toast("当前设置延时为空,请重新设置");
                    return;
                }
                delay_set = "f1";
                if (maxSecond != 0) {
                    if (Integer.parseInt(reEtF1.getText().toString()) > maxSecond) {
                        show_Toast("当前设置延时已超过最大延时" + maxSecond + "ms,请重新设置");
                        return;
                    }
                    if (Integer.parseInt(et_startDelay.getText().toString()) > maxSecond) {
                        show_Toast("当前开始延时已超过最大延时" + maxSecond + "ms,请重新设置");
                        return;
                    }
                }
                reEtF1.setBackgroundResource(R.drawable.textview_border_green);
                reEtF2.setBackgroundResource(R.drawable.translucent);
                reBtnF1.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style);
                reEtF1.clearFocus();
                reEtF2.clearFocus();
                break;

            case R.id.btn_f2:
                hideInputKeyboard();
                if (reEtF2.getText().toString().equals("")) {
                    show_Toast("当前设置延时为空,请重新设置");
                    return;
                }
                delay_set = "f2";
                if (maxSecond != 0) {
                    if (Integer.parseInt(reEtF2.getText().toString()) > maxSecond) {
                        show_Toast("当前设置延时已超过最大延时" + maxSecond + "ms,请重新设置");
                        return;
                    }
                    if (Integer.parseInt(et_startDelay.getText().toString()) > maxSecond) {
                        show_Toast("当前设置延时已超过最大延时" + maxSecond + "ms,请重新设置");
                        return;
                    }
                }
                reEtF1.setBackgroundResource(R.drawable.translucent);
                reEtF2.setBackgroundResource(R.drawable.textview_border_green);
                reBtnF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                reEtF1.clearFocus();
                reEtF2.clearFocus();
                break;

            case R.id.btn_singleReister:

//                if (delay_set.equals("0")) {
//                    show_Toast("请设置延时");
//                    break;
//                }
//                if (reEtF1.getText().length() < 1 || reEtF2.getText().length() < 1) {
//                    show_Toast("有延时为空,请先设置延时");
//                    break;
//                }
                if (isSingleReisher == 0 && send_10 == 0 && send_13 == 0 && send_41 == 0 && send_40 == 0) {
                    String shellBlastNo = serchFristLG();
                    int num = serchFristLGINdenatorHis(shellBlastNo);

                    if (num > 0) {
                        showAlertDialog();
                    }
                    show_Toast("请等待电流电压显示出来后，再连接雷管!");
                    btnInputOk.setEnabled(false);
                    btnSingleReister.setText("停止注册");
                    isSingleReisher = 1;
                    closeThread();
//                    closeOpenThread = new CloseOpenPower();
//                    closeOpenThread.start();
                    sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令

                } else if (send_10 == 0 && send_13 == 0 && send_41 == 0 && send_40 == 0) {
                    btnInputOk.setEnabled(true);
                    btnSingleReister.setText("单发注册");
                    txt_currentVolt.setText("当前电压:");
                    txt_currentIC.setText("当前电流:");
                    txt_currentIC.setTextColor(Color.BLACK);
                    isSingleReisher = 0;
                    initCloseCmdReFlag = 0;
                    initOpenCmdReFlag = 0;
                    revCloseCmdReFlag = 0;
                    revOpenCmdReFlag = 0;
                    // 13 退出注册模式
                    sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));
                } else {
                    show_Toast("正在与单片机通讯,请稍等一下再退出注册模式!");
                }
                break;

            case R.id.btn_LookHistory:
                Intent intent2 = new Intent(this, QueryCurrentDetail_All.class);
                intent2.putExtra("dataSend", "当前雷管信息");
                startActivity(intent2);
                break;

            case R.id.btn_setdelay:
                Intent intent3 = new Intent(this, SetDelayTime.class);
                intent3.putExtra("dataSend", "设置延时");
                startActivity(intent3);

//                send12();
                break;

            case R.id.btn_ReisterScanStart_st:
                hideInputKeyboard();
                if (continueScanFlag == 0) {
                    continueScanFlag = 1;
                    scanDecode.starScan();//启动扫描
                } else {
                    continueScanFlag = 0;
                    scanDecode.stopScan();//停止扫描
                }
                sanButtonFlag = 1;
                break;

            case R.id.btn_ReisterScanStart_ed:
                hideInputKeyboard();
                if (continueScanFlag == 0) {
                    continueScanFlag = 1;
                    scanDecode.starScan();//启动扫描
                } else {
                    continueScanFlag = 0;
                    scanDecode.stopScan();//停止扫描
                }
                sanButtonFlag = 2;
                break;

            case R.id.btn_return:
                closeThread();
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
                break;

            case R.id.btn_inputOk:
                break;

            case R.id.re_et_f1:
            case R.id.re_et_f2:
            case R.id.setDelayTime_startDelaytime:
                reEtF1.setBackgroundResource(R.drawable.translucent);
                reEtF2.setBackgroundResource(R.drawable.translucent);
                break;
            case R.id.btn_addDelay:
                maxDuanNo = maxDuanNo + 1;
                setView(maxDuanNo);
                break;
            case R.id.re_btn_f1:
                hideInputKeyboard();
                duan = 1;
                initUI();
                reNumF1.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f2:
                hideInputKeyboard();
                duan = 2;
                initUI();
                reNumF2.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f3:
                hideInputKeyboard();
                duan = 3;
                initUI();
                reNumF3.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f4:
                hideInputKeyboard();
                duan = 4;
                initUI();
                reNumF4.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f5:
                hideInputKeyboard();
                duan = 5;
                initUI();
                reNumF5.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f6:
                hideInputKeyboard();
                duan = 6;
                initUI();
                reNumF6.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f7:
                hideInputKeyboard();
                duan = 7;
                initUI();
                reNumF7.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f8:
                hideInputKeyboard();
                duan = 8;
                initUI();
                reNumF8.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f9:
                hideInputKeyboard();
                duan = 9;
                initUI();
                reNumF9.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f10:
                hideInputKeyboard();
                duan = 10;
                initUI();
                reNumF10.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f11:
                hideInputKeyboard();
                duan = 11;
                initUI();
                reNumF11.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f12:
                hideInputKeyboard();
                duan = 12;
                initUI();
                reNumF12.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f13:
                hideInputKeyboard();
                duan = 13;
                initUI();
                reNumF13.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f14:
                hideInputKeyboard();
                duan = 14;
                initUI();
                reNumF14.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f15:
                hideInputKeyboard();
                duan = 15;
                initUI();
                reNumF15.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f16:
                hideInputKeyboard();
                duan = 16;
                initUI();
                reNumF16.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f17:
                hideInputKeyboard();
                duan = 17;
                initUI();
                reNumF17.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f18:
                hideInputKeyboard();
                duan = 18;
                initUI();
                reNumF18.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f19:
                hideInputKeyboard();
                duan = 19;
                initUI();
                reNumF19.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f20:
                hideInputKeyboard();
                duan = 20;
                initUI();
                reNumF20.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_et_nei1:
                fanzhuan(1);
                if (n1 == 1) {
                    n1 = 0;
                } else {
                    n1 = 1;
                }
                break;
            case R.id.re_et_nei2:
                fanzhuan(2);
                if (n2 == 1) {
                    n2 = 0;
                } else {
                    n2 = 1;
                }
                break;
            case R.id.re_et_nei3:
                fanzhuan(3);
                if (n3 == 1) {
                    n3 = 0;
                } else {
                    n3 = 1;
                }
                break;
            case R.id.re_et_nei4:
                fanzhuan(4);
                if (n4 == 1) {
                    n4 = 0;
                } else {
                    n4 = 1;
                }
                break;
            case R.id.re_et_nei5:
                fanzhuan(5);
                if (n5 == 1) {
                    n5 = 0;
                } else {
                    n5 = 1;
                }
                break;
            case R.id.re_et_nei6:
                fanzhuan(6);
                if (n6 == 1) {
                    n6 = 0;
                } else {
                    n6 = 1;
                }
                break;
            case R.id.re_et_nei7:
                fanzhuan(7);
                if (n7 == 1) {
                    n7 = 0;
                } else {
                    n7 = 1;
                }
                break;
            case R.id.re_et_nei8:
                fanzhuan(8);
                if (n8 == 1) {
                    n8 = 0;
                } else {
                    n8 = 1;
                }
                break;
            case R.id.re_et_nei9:
                fanzhuan(9);
                if (n9 == 1) {
                    n9 = 0;
                } else {
                    n9 = 1;
                }
                break;
            case R.id.re_et_nei10:
                fanzhuan(10);
                if (n10 == 1) {
                    n10 = 0;
                } else {
                    n10 = 1;
                }
                break;
            case R.id.re_et_nei11:
                fanzhuan(11);
                if (n11 == 1) {
                    n11 = 0;
                } else {
                    n11 = 1;
                }
                break;
            case R.id.re_et_nei12:
                fanzhuan(12);
                if (n12 == 1) {
                    n12 = 0;
                } else {
                    n12 = 1;
                }
                break;
            case R.id.re_et_nei13:
                fanzhuan(13);
                if (n13 == 1) {
                    n13 = 0;
                } else {
                    n13 = 1;
                }
                break;
            case R.id.re_et_nei14:
                fanzhuan(14);
                if (n14 == 1) {
                    n14 = 0;
                } else {
                    n14 = 1;
                }
                break;
            case R.id.re_et_nei15:
                fanzhuan(15);
                if (n15 == 1) {
                    n15 = 0;
                } else {
                    n15 = 1;
                }
                break;
            case R.id.re_et_nei16:
                fanzhuan(16);
                if (n16 == 1) {
                    n16 = 0;
                } else {
                    n16 = 1;
                }
                break;
            case R.id.re_et_nei17:
                fanzhuan(17);
                if (n17 == 1) {
                    n17 = 0;
                } else {
                    n17 = 1;
                }
                break;
            case R.id.re_et_nei18:
                fanzhuan(18);
                if (n18 == 1) {
                    n18 = 0;
                } else {
                    n18 = 1;
                }
                break;
            case R.id.re_et_nei19:
                fanzhuan(19);
                if (n19 == 1) {
                    n19 = 0;
                } else {
                    n19 = 1;
                }
                break;
            case R.id.re_et_nei20:
                fanzhuan(20);
                if (n20 == 1) {
                    n20 = 0;
                } else {
                    n20 = 1;
                }
                break;

        }
    }


    private class SendOpenPower extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    if (zeroCount == 0) {
                        byte[] powerCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");//41 开启总线电源指令
                        sendCmd(powerCmd);
                    }
                    if (revOpenCmdReFlag == 1) {
                        exit = true;
                        break;
                    }
                    Thread.sleep(100);
                    if (zeroCount > 30) {
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
                    if (zeroCount > 240) {
                        tipInfoFlag = 3;//未收到关闭电源命令
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

    //开始厂家码
    TextWatcher st_1_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 2) {
                edit_start_entproduceDate_st.setFocusable(true);
                edit_start_entproduceDate_st.setFocusableInTouchMode(true);
                edit_start_entproduceDate_st.requestFocus();
                edit_start_entproduceDate_st.findFocus();
                edit_start_entBF2Bit_st.setBackgroundColor(Color.GREEN);
            } else {
                edit_start_entBF2Bit_st.setBackgroundColor(Color.RED);
            }

        }
    };
    //开始日期码
    TextWatcher st_2_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {//开始日期码
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 5) {
                edit_start_entproduceDate_st.setBackgroundColor(Color.GREEN);
                if (factoryFeature == null || factoryFeature.trim().length() < 1) {
                    edit_end_entproduceDate_ed.setText("" + edit_start_entproduceDate_st.getText());
                    edit_start_entAT1Bit_st.setFocusable(true);
                    edit_start_entAT1Bit_st.setFocusableInTouchMode(true);
                    edit_start_entAT1Bit_st.requestFocus();
                    edit_start_entAT1Bit_st.findFocus();

                } else {
                    //
                    edit_end_entproduceDate_ed.setText("" + edit_start_entproduceDate_st.getText());
                    edit_start_entboxNoAndSerial_st.setFocusable(true);//开始流水号
                    edit_start_entboxNoAndSerial_st.setFocusableInTouchMode(true);
                    edit_start_entboxNoAndSerial_st.requestFocus();
                    edit_start_entboxNoAndSerial_st.findFocus();
                }
            } else {
                edit_start_entproduceDate_st.setBackgroundColor(Color.RED);
            }
        }
    };
    //开始特征码
    TextWatcher st_3_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 1) {
                edit_end_entBF2Bit_en.setText("" + edit_start_entBF2Bit_st.getText());
                edit_end_entproduceDate_ed.setText("" + edit_start_entproduceDate_st.getText());
                edit_end_entAT1Bit_ed.setText("" + edit_start_entAT1Bit_st.getText());
                edit_start_entboxNoAndSerial_st.setFocusable(true);
                edit_start_entboxNoAndSerial_st.setFocusableInTouchMode(true);
                edit_start_entboxNoAndSerial_st.requestFocus();
                edit_start_entboxNoAndSerial_st.findFocus();
                edit_start_entAT1Bit_st.setBackgroundColor(Color.GREEN);
            } else {
                edit_start_entAT1Bit_st.setBackgroundColor(Color.RED);
            }
        }
    };
    //开始流水号
    TextWatcher st_4_watcher = new TextWatcher() {
        private int cou = 0;
        int selectionEnd = 0;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            cou = before + count;
            String editable = edit_start_entboxNoAndSerial_st.getText().toString();
            String str = Utils.stringFilter(editable); //过滤特殊字符
            if (!editable.equals(str)) {
                edit_start_entboxNoAndSerial_st.setText(str);
            }
            edit_start_entboxNoAndSerial_st.setSelection(edit_start_entboxNoAndSerial_st.length());
            cou = edit_start_entboxNoAndSerial_st.length();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 5) {
                edit_start_entboxNoAndSerial_st.setBackgroundColor(Color.GREEN);
            } else {
                edit_start_entboxNoAndSerial_st.setBackgroundColor(Color.RED);
            }
        }
    };

    //结束厂家码
    TextWatcher end_1_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 2) {
                edit_end_entBF2Bit_en.setBackgroundColor(Color.GREEN);
                edit_end_entproduceDate_ed.setFocusable(true);
                edit_end_entproduceDate_ed.setFocusableInTouchMode(true);
                edit_end_entproduceDate_ed.requestFocus();
                edit_end_entproduceDate_ed.findFocus();
            } else {
                edit_end_entBF2Bit_en.setBackgroundColor(Color.RED);
            }
        }
    };
    //结束日期码
    TextWatcher end_2_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 5) {
                edit_end_entproduceDate_ed.setBackgroundColor(Color.GREEN);
                if (factoryFeature == null || factoryFeature.trim().length() < 1) {
                    edit_end_entAT1Bit_ed.setFocusable(true);
                    edit_end_entAT1Bit_ed.setFocusableInTouchMode(true);
                    edit_end_entAT1Bit_ed.requestFocus();
                    edit_end_entAT1Bit_ed.findFocus();
                } else {
                    edit_end_entboxNoAndSerial_ed.setFocusable(true);
                    edit_end_entboxNoAndSerial_ed.setFocusableInTouchMode(true);
                    edit_end_entboxNoAndSerial_ed.requestFocus();
                    edit_end_entboxNoAndSerial_ed.findFocus();
                }

            } else {
                edit_end_entproduceDate_ed.setBackgroundColor(Color.RED);
            }
        }
    };
    //结束特征码
    TextWatcher end_3_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 1) {
                edit_end_entboxNoAndSerial_ed.setFocusable(true);
                edit_end_entboxNoAndSerial_ed.setFocusableInTouchMode(true);
                edit_end_entboxNoAndSerial_ed.requestFocus();
                edit_end_entboxNoAndSerial_ed.findFocus();
                edit_end_entAT1Bit_ed.setBackgroundColor(Color.GREEN);
            } else {
                edit_end_entAT1Bit_ed.setBackgroundColor(Color.RED);
            }
        }
    };
    //结束流水号
    TextWatcher end_4_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 5) {
                edit_end_entboxNoAndSerial_ed.setBackgroundColor(Color.GREEN);
            } else {
                edit_end_entboxNoAndSerial_ed.setBackgroundColor(Color.RED);
            }
        }
    };

    //得到连续管壳码
    private String getContinueScanBlastNo(String strBarcode) {

        if (strBarcode.length() < 13) return null;
        if (strBarcode.trim().length() == 14) {
            strBarcode = strBarcode.substring(1);
            return strBarcode;
        } else if (strBarcode.trim().length() == 13) {
            //strBarcode= strBarcode;
            return strBarcode;
        }
        int index = strBarcode.indexOf("SC:");
        if (index < 0) return null;
        String subBarCode = strBarcode.substring(index + 3, index + 16);
        if (subBarCode.trim().length() < 13) {
            return null;
        }
        return subBarCode;
    }

    //扫码方法
    private void decodeBar(String strParamBarcode) {
        String subBarCode = "";
        Log.e("扫码结果", "strParamBarcode: " + strParamBarcode);
        if (strParamBarcode.trim().length() >= 14) {
            int index = strParamBarcode.indexOf("SC:");
            subBarCode = strParamBarcode.substring(index + 3, index + 16);
            if (subBarCode.trim().length() < 13) {
                show_Toast("不正确的编码，请扫描选择正确的编码");
                return;
            }
        } else {
            if (strParamBarcode.trim().length() == 14) {
                subBarCode = strParamBarcode.substring(1);
            } else if (strParamBarcode.trim().length() == 13) {
                subBarCode = strParamBarcode;
            } else
                return;
        }
        String facCode = subBarCode.substring(0, 2);
        String dayCode = subBarCode.substring(2, 7);
        String featureCode = subBarCode.substring(7, 8);
        String serialNo = subBarCode.substring(8);
        Log.e("注册页面--扫码注册", "facCode: " + facCode + "  dayCode:" + dayCode + "  featureCode:" + featureCode + "  serialNo:" + serialNo);
        Log.e("注册页面--扫码注册", "sanButtonFlag: " + sanButtonFlag);

        if (sanButtonFlag == 1) {
            edit_start_entBF2Bit_st.setText(facCode);
            edit_start_entproduceDate_st.setText(dayCode);//日期码
            edit_start_entAT1Bit_st.setText(featureCode);
            edit_start_entboxNoAndSerial_st.setText(serialNo);

            edit_end_entBF2Bit_en.setText("");
            edit_end_entproduceDate_ed.setText("");
            edit_end_entAT1Bit_ed.setText("");
            edit_end_entboxNoAndSerial_ed.setText("");
            btnReisterScanStartEd.setEnabled(true);
        }
        if (sanButtonFlag == 2) {
            edit_end_entBF2Bit_en.setText(facCode);
            edit_end_entproduceDate_ed.setText(dayCode);
            edit_end_entAT1Bit_ed.setText(featureCode);
            edit_end_entboxNoAndSerial_ed.clearFocus();
            edit_end_entboxNoAndSerial_ed.setText(serialNo);
            btnReisterScanStartSt.setEnabled(true);
        }
        sanButtonFlag = 0;
    }

    /**
     * 校验数据
     */
    private String checkData_delay() {
        String tipStr = "";
        //开始延时
        String startDelay = et_startDelay.getText().toString();
        //孔内延时
        String holeinDelay = f1DelayTxt.getText().toString();
        //孔间延时
        String holeBetweent = f2DelayTxt.getText().toString();

        if (!Utils.isNum(startDelay)) {
            tipStr = getString(R.string.text_error_tip42);//"开始延时不是数字";
            return tipStr;
        }
        if (!Utils.isNum(holeinDelay)) {
            tipStr = getString(R.string.text_error_tip43);//"孔内延时不是数字";
            return tipStr;
        }
        if (!Utils.isNum(holeBetweent)) {
            tipStr = getString(R.string.text_error_tip44);//"孔间延时不是数字";
            return tipStr;
        }
        if (maxSecond <= 0) return tipStr;
        else {
            if (Integer.parseInt(startDelay) > maxSecond) {
                tipStr = getString(R.string.text_error_tip45);//"开始延时超出最大设定时间";
                return tipStr;
            }
            if (Integer.parseInt(holeinDelay) > maxSecond) {
                tipStr = getString(R.string.text_error_tip46);//"孔内延时超出最大设定时间";
                return tipStr;
            }
            if (Integer.parseInt(holeBetweent) > maxSecond) {
                tipStr = getString(R.string.text_error_tip47);//"孔间延时超出最大设定时间";
                return tipStr;
            }
        }
        return tipStr;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            if (isSingleReisher != 0) {
                show_Toast("请停止注册后再退出当前页面");
            } else {
                finish();
            }
        }
        return true;
    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager       设置RecyclerView对应的manager
     * @param mRecyclerView 当前的RecyclerView
     * @param n             要跳转的位置
     */
    public static void MoveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }

    }

    /**
     * 重置控件
     */
    private void resetView() {
        reEtF1.setBackgroundResource(R.drawable.translucent);
        reEtF2.setBackgroundResource(R.drawable.translucent);
        et_startDelay.setBackgroundResource(R.drawable.translucent);

        reBtnF1.setBackgroundResource(R.drawable.bt_mainpage_style);
        reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style);

        reEtF1.clearFocus();
        reEtF2.clearFocus();
        et_startDelay.clearFocus();
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 打开菜单
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 点击item
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mRegion = String.valueOf(item.getOrder());

        switch (item.getItemId()) {

            case R.id.item_1:
            case R.id.item_2:
            case R.id.item_3:
            case R.id.item_4:
            case R.id.item_5:
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

                // 显示提示
                show_Toast("已选择 区域" + mRegion);
                // 延时选择重置
                resetView();
                delay_set = "0";
                //初始化雷管数量
                for (int i = 1; i < 21; i++) {
                    showDuanSum(i);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * 设置标题区域
     */
    private void setTitleRegion(String region, int size) {

        String str;
        if (size == -1) {
            str = " 区域" + region;
        } else {
            str = " 区域" + region + "(数量: " + size + ")";
        }
        // 设置标题
        getSupportActionBar().setTitle(mOldTitle + str);
        // 保存区域参数
        SPUtils.put(this, Constants_SP.RegionCode, region);

        Log.e("liyi_Region", "已选择" + str);
    }

    private void setView(int i) {

        switch (i) {
            case 4:
                tr4.setVisibility(View.VISIBLE);
                break;
            case 5:
                tr5.setVisibility(View.VISIBLE);
                break;
            case 6:
                tr6.setVisibility(View.VISIBLE);
                break;
            case 7:
                tr7.setVisibility(View.VISIBLE);
                break;
            case 8:
                tr8.setVisibility(View.VISIBLE);
                break;
            case 9:
                tr9.setVisibility(View.VISIBLE);
                break;
            case 10:
                tr10.setVisibility(View.VISIBLE);
                break;
            case 11:
                tr11.setVisibility(View.VISIBLE);
                break;
            case 12:
                tr12.setVisibility(View.VISIBLE);
                break;
            case 13:
                tr13.setVisibility(View.VISIBLE);
                break;
            case 14:
                tr14.setVisibility(View.VISIBLE);
                break;
            case 15:
                tr15.setVisibility(View.VISIBLE);
                break;
            case 16:
                tr16.setVisibility(View.VISIBLE);
                break;
            case 17:
                tr17.setVisibility(View.VISIBLE);
                break;
            case 18:
                tr18.setVisibility(View.VISIBLE);
                break;
            case 19:
                tr19.setVisibility(View.VISIBLE);
                break;
            case 20:
                tr20.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void fanzhuan(int duan) {
        Log.e("注册页面", "翻转: ");
        AlertDialog dialog = new AlertDialog.Builder(ReisterMainPage_line.this)
                .setTitle("翻转提示")//设置对话框的标题//"成功起爆"
                .setMessage("是否翻转当前段位延时")//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                .setNegativeButton("取消", (dialog12, which) -> dialog12.dismiss())
                .setPositiveButton("确认", (dialog1, which) -> {

                    GreenDaoMaster master = new GreenDaoMaster();
                    List<DenatorBaseinfo> list = master.queryLeiguanDuan(duan,mRegion);
                    List<DenatorBaseinfo> list2 = master.queryLeiguanDuan(duan,mRegion);
                    for (int i = 0; i < list.size(); i++) {
                        DenatorBaseinfo lg = list.get(i);
                        lg.setDelay(list2.get(list.size() - 1 - i).getDelay());
                        getDaoSession().getDenatorBaseinfoDao().update(lg);
                    }
                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                    setBtnColor(duan);
                }).create();

        dialog.show();
    }

    private void setBtnColor(int duanChose) {
        switch (duanChose) {
            case 1:
                Log.e("注册", "n1: " + n1);
                if (n1 == 1) {
                    btnFan1.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan1.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n1", n1);
                break;
            case 2:
                if (n2 == 1) {
                    btnFan2.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan2.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n2", n2);
                break;
            case 3:
                if (n3 == 1) {
                    btnFan3.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan3.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n3", n3);
                break;
            case 4:
                if (n4 == 1) {
                    btnFan4.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan4.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n4", n4);
                break;
            case 5:
                if (n5 == 1) {
                    btnFan5.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan5.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n5", n5);
                break;
            case 6:
                if (n6 == 1) {
                    btnFan6.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan6.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n6", n6);
                break;
            case 7:
                if (n7 == 1) {
                    btnFan7.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan7.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n7", n7);
                break;
            case 8:
                if (n8 == 1) {
                    btnFan8.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan8.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n8", n8);
                break;
            case 9:
                if (n9 == 1) {
                    btnFan9.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan9.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n9", n9);
                break;
            case 10:
                if (n10 == 1) {
                    btnFan10.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan10.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n10", n10);
                break;
            case 11:
                if (n11 == 1) {
                    btnFan11.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan11.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n11", n11);
                break;
            case 12:
                if (n12 == 1) {
                    btnFan12.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan12.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n12", n12);
                break;
            case 13:
                if (n13 == 1) {
                    btnFan13.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan13.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n13", n13);
                break;
            case 14:
                if (n14 == 1) {
                    btnFan14.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan14.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n14", n14);
                break;
            case 15:
                if (n15 == 1) {
                    btnFan15.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan15.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n15", n15);
                break;
            case 16:
                if (n16 == 1) {
                    btnFan16.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan16.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n16", n16);
                break;
            case 17:
                if (n17 == 1) {
                    btnFan17.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan17.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n17", n17);
                break;
            case 18:
                if (n18 == 1) {
                    btnFan18.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan18.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n18", n18);
                break;
            case 19:
                if (n19 == 1) {
                    btnFan19.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan9.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n19", n19);
                break;
            case 20:
                if (n20 == 1) {
                    btnFan20.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan20.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n20", n20);
                break;
        }
    }

    private void setFan() {
        n1 = (int) MmkvUtils.getcode("n1", 0);
        n2 = (int) MmkvUtils.getcode("n2", 0);
        n3 = (int) MmkvUtils.getcode("n3", 0);
        n4 = (int) MmkvUtils.getcode("n4", 0);
        n5 = (int) MmkvUtils.getcode("n5", 0);
        n6 = (int) MmkvUtils.getcode("n6", 0);
        n7 = (int) MmkvUtils.getcode("n7", 0);
        n8 = (int) MmkvUtils.getcode("n8", 0);
        n9 = (int) MmkvUtils.getcode("n9", 0);
        n10 = (int) MmkvUtils.getcode("n10", 0);
        n11 = (int) MmkvUtils.getcode("n11", 0);
        n12 = (int) MmkvUtils.getcode("n12", 0);
        n13 = (int) MmkvUtils.getcode("n13", 0);
        n14 = (int) MmkvUtils.getcode("n14", 0);
        n15 = (int) MmkvUtils.getcode("n15", 0);
        n16 = (int) MmkvUtils.getcode("n16", 0);
        n17 = (int) MmkvUtils.getcode("n17", 0);
        n18 = (int) MmkvUtils.getcode("n18", 0);
        n19 = (int) MmkvUtils.getcode("n19", 0);
        n20 = (int) MmkvUtils.getcode("n20", 0);
        for (int i = 1; i < 21; i++) {
            setBtnColor(i);
        }
    }

    public void initUI() {
        reNumF1.setBackgroundResource(R.drawable.translucent);
        reNumF2.setBackgroundResource(R.drawable.translucent);
        reNumF3.setBackgroundResource(R.drawable.translucent);
        reNumF4.setBackgroundResource(R.drawable.translucent);
        reNumF5.setBackgroundResource(R.drawable.translucent);
        reNumF6.setBackgroundResource(R.drawable.translucent);
        reNumF7.setBackgroundResource(R.drawable.translucent);
        reNumF8.setBackgroundResource(R.drawable.translucent);
        reNumF9.setBackgroundResource(R.drawable.translucent);
        reNumF10.setBackgroundResource(R.drawable.translucent);
        reNumF11.setBackgroundResource(R.drawable.translucent);
        reNumF12.setBackgroundResource(R.drawable.translucent);
        reNumF13.setBackgroundResource(R.drawable.translucent);
        reNumF14.setBackgroundResource(R.drawable.translucent);
        reNumF15.setBackgroundResource(R.drawable.translucent);
        reNumF16.setBackgroundResource(R.drawable.translucent);
        reNumF17.setBackgroundResource(R.drawable.translucent);
        reNumF18.setBackgroundResource(R.drawable.translucent);
        reNumF19.setBackgroundResource(R.drawable.translucent);
        reNumF20.setBackgroundResource(R.drawable.translucent);

        lySetDelay.setFocusable(true);
        lySetDelay.setFocusableInTouchMode(true);
        lySetDelay.requestFocus();

    }

    /***
     * 得到某段的总数
     * @return
     */
    private int getDuanNo(int duan) {
        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where duan =?", new String[]{duan + ""});
        int totalNum = cursor.getCount();//得到数据的总条数
        cursor.close();
        return totalNum;
    }

    /***
     * 得到某段的总数
     * @return
     */
    private int getDuanByDenatorNo(String shellBlastNo) {
        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where shellBlastNo =?", new String[]{shellBlastNo + ""});
        if (cursor != null && cursor.moveToNext()) {
            int duan = cursor.getInt(15);
            cursor.close();
            return duan;
        }
        return 1;
    }

    /**
     * 显示雷管数量
     */
    private void showDuanSum(int a) {
        List<DenatorBaseinfo> list = new GreenDaoMaster().queryDetonatorRegionAndDUanAsc(mRegion, a);
        int totalNum = list.size();//得到数据的总条数
        Log.e(TAG, "当前区域段数totalNum: "+totalNum );
        switch (a) {
            case 1:
                reNumF1.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n1", 0);
                }
                break;
            case 2:
                reNumF2.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n2", 0);
                }
                break;
            case 3:
                reNumF3.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n3", 0);
                }
                break;
            case 4:
                reNumF4.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n4", 0);
                }
                break;
            case 5:
                reNumF5.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n5", 0);
                }
                break;
            case 6:
                reNumF6.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n6", 0);
                }
                break;
            case 7:
                reNumF7.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n7", 0);
                }
                break;
            case 8:
                reNumF8.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n8", 0);
                }
                break;
            case 9:
                reNumF9.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n9", 0);
                }
                break;
            case 10:
                reNumF10.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n10", 0);
                }
                break;
            case 11:
                reNumF11.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n11", 0);
                }
                break;
            case 12:
                reNumF12.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n12", 0);
                }
                break;
            case 13:
                reNumF13.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n13", 0);
                }
                break;
            case 14:
                reNumF14.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n14", 0);
                }
                break;
            case 15:
                reNumF15.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n15", 0);
                }
                break;
            case 16:
                reNumF16.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n16", 0);
                }
                break;
            case 17:
                reNumF17.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n17", 0);
                }
                break;
            case 18:
                reNumF18.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n18", 0);
                }
                break;
            case 19:
                reNumF19.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n19", 0);
                }
                break;
            case 20:
                reNumF20.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n20", 0);
                }
                break;
        }
    }

    /**
     * 获取最大段号
     */
    private int getMaxDuanNo() {
        Cursor cursor = db.rawQuery("select max(duan) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where piece =? ", new String[]{mRegion} );
        if (cursor != null && cursor.moveToNext()) {
            String maxDuan = cursor.getString(0);
            if (maxDuan != null) {
                cursor.close();
                Log.e("获取最大段号", "maxDuan: " + maxDuan);
                return Integer.parseInt(maxDuan);
            }
        }
        return 3;
    }

    ///---末尾---///
}
