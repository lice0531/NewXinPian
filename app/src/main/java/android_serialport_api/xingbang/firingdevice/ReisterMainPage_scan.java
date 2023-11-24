package android_serialport_api.xingbang.firingdevice;


import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_TRACKER_EN;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.MyRecyclerView;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_DetailDao;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.Defactory;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 雷管注册
 */
public class ReisterMainPage_scan extends SerialPortActivity implements LoaderCallbacks<Cursor> {

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
    Button btnReisterScanStartSt;
    @BindView(R.id.entBF2Bit_ed)
    EditText entBF2BitEd;
    @BindView(R.id.entproduceDate_ed)
    EditText entproduceDateEd;
    @BindView(R.id.entAT1Bit_ed)
    EditText entAT1BitEd;
    @BindView(R.id.entboxNoAndSerial_ed)
    EditText entboxNoAndSerialEd;
    @BindView(R.id.btn_ReisterScanStart_ed)
    Button btnReisterScanStartEd;//扫描按钮终止位
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
    @BindView(R.id.ly_setDelay)//设置延时
    LinearLayout lySetDelay;
    @BindView(R.id.btn_return)
    Button btnReturn;
    @BindView(R.id.btn_inputOk)
    Button btnInputOk;
    @BindView(R.id.btn_singleReister)
    Button btnSingleReister;
    @BindView(R.id.btn_scanReister)
    Button btnScanReister;
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
    RecyclerView mListView;
    @BindView(R.id.ly_showData)
    LinearLayout lyShowData;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.container1)
    LinearLayout container1;
    @BindView(R.id.ll_start)
    LinearLayout llStart;
    @BindView(R.id.ll_end)
    LinearLayout llEnd;
    @BindView(R.id.btn_input)
    Button btnInput;
    @BindView(R.id.et_test)
    EditText etTest;
    @BindView(R.id.et_num)
    EditText etNum;
    @BindView(R.id.ll_num)
    LinearLayout llNum;
    @BindView(R.id.btn_single)
    Button btnSingle;
    @BindView(R.id.edit_scan_changjia)//单发输入厂家码
    EditText editScanChangjia;
    @BindView(R.id.edit_scan_riqi)//单发输入日期码
    EditText editScanRiqi;
    @BindView(R.id.edit_scan_tezheng)//单发输入特征码
    EditText editScanTezheng;
    @BindView(R.id.edit_scan_hehao)
    EditText editScanHehao;
    @BindView(R.id.edit_scan_liushui)
    EditText editScanLiushui;
    @BindView(R.id.ll_single)
    LinearLayout llSingle;
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

    @BindView(R.id.btn_addDelay)
    Button btnAddDelay;
    @BindView(R.id.btn_tk_F1)
    Button btnTkF1;

    @BindView(R.id.btn_tk)
    Button btnTk;
    @BindView(R.id.et_tk)
    EditText etTk;
    private SimpleCursorAdapter adapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private String factoryCode = "";//厂家代码
    private String factoryFeature = "";////厂家特征码
    private String deTypeName = "";//雷管类型名称
    private String deTypeSecond = "";//该类型雷管最大延期值
    private String scanInfo = "";
    //是否单发注册
    private int isSingleReisher;
    //单发注册

    private int sanButtonFlag = 0;//1s是起始按钮，2是终止按钮
    private Handler mHandler_tip = new Handler();//错误提示
    private Handler mHandler_1 = new Handler();//提示电源信息
    private Handler mHandler_2 = new Handler();//显示进度条
    private static int tipInfoFlag = 0;
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
    private int continueScanFlag = 0;//是否继续扫码标志 0否1是
    private SendOpenPower sendOpenThread;
    private CloseOpenPower closeOpenThread;
    private ScanBar scanBarThread;
    private ScanInterface scanDecode;
    private volatile int initCloseCmdReFlag = 0;
    private volatile int initOpenCmdReFlag = 0;
    private volatile int revCloseCmdReFlag = 0;
    private volatile int revOpenCmdReFlag = 0;
    private ZhuceThread zhuceThread;
    private volatile String prex = "";
    private volatile int start = 0;
    private volatile int num = 0;
    private ProgressDialog builder = null;
    private LoadingDialog tipDlg = null;
    private int isCorrectReisterFea = 0; //是否正确的管厂码
    private int maxSecond = 0;//最大秒数
    private int pb_show = 0;
    private String delay_set = "0";//是f1还是f2
    private String selectDenatorId;//选择的管壳码
    //这是注册了一个观察者模式
    public static final Uri uri = Uri.parse("content://android_serialport_api.xingbang.denatorBaseinfo");
    private String qiaosi_set = "";//是否检测桥丝
    private String version = "";//是否检测桥丝

    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private DetonatorAdapter_Paper<DenatorBaseinfo> mAdapter;
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private Handler mHandler_0 = new Handler();     // UI处理
    private String mOldTitle;   // 原标题
    private String mRegion = "1";     // 区域
    private boolean switchUid = true;//切换uid/管壳码

    //段属性
    private int duan = 1;//duan
    private int maxDuanNo = 3;
    private Handler mHandler_showNum = new Handler();//显示雷管数量
    private String duan_set = "0";//是duan1还是duan2
    private int n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20 = 0;

    private String TAG = "扫码注册";
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private Boolean charu=false;
    private DenatorBaseinfo db_charu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reister_main_page_scan);
        ButterKnife.bind(this);
        SoundPlayUtils.init(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        getUserMessage();
        getFactoryCode();//获取厂家码
        getFactoryType();//获取延期最大值
        //管壳号扫描分码--结束

        init();
        btn_onClick();//button的onClick

        handler();//所有的handler
        scan();//扫描初始化//扫描参数设置
        hideInputKeyboard();//隐藏焦点
        Utils.writeRecord("---进入手动输入和扫码注册页面---");
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

        MmkvUtils.savecode("duan", 1);//每次进入都重置段位参数
        //初始化段间延时显示
//        int maxduan = getMaxDuanNo();
//        Log.e("显示", "maxduan: " + maxduan);
//        if (maxduan < 3) {
//            maxDuanNo = 3;
//        } else {
//            maxDuanNo = maxduan;
//            for (int i = maxDuanNo; i > 3; i--) {
//                setView(i);
//                Log.e("显示", "maxDuanNo: " + maxDuanNo);
//            }
//        }
//        //初始化雷管数量
//        for (int i = 1; i < 21; i++) {
//            showDuanSum(i);
//        }
//        //初始化翻转按钮颜色
//        setFan();

        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //此处是跳转的result回调方法
                if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                    String a = result.getData().getStringExtra("data_return");
                    Log.e(TAG, "选择页面返回数据: " + a);
                }
            }
        });
    }

    private void getUserMessage() {
        List<MessageBean> list = getDaoSession().getMessageBeanDao().loadAll();
        qiaosi_set = list.get(0).getQiaosi_set();
        version = list.get(0).getVersion();
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    private void updateMessage(String version) {
        MessageBean bean = GreenDaoMaster.getAllFromInfo_bean();
        bean.setVersion(version);
        getDaoSession().getMessageBeanDao().update(bean);
        Utils.saveFile_Message();
    }


    /**
     * 扫码注册方法/扫描头返回方法
     */
    private void scan() {
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");//初始化扫描服务

        scanDecode.getBarCode(data -> {
            Log.e("扫码", "data: " + data);
//            if (deleteList()) return;
            hideInputKeyboard();//隐藏光标
            //根据二维码长度判断新旧版本,兼容01一代,02二代芯片
            if (data.length() == 13) {
                updateMessage("01");
            } else if (data.length() == 28) {//P53904180500005390418050000
                updateMessage("02");
            } else if (data.length() == 30) {//5620302H00001A62F400FFF20AB603
                updateMessage("02");
            }
            if (data.length() == 19) {//扫描箱号
                addXiangHao(data);
            }
            if (sanButtonFlag > 0) {//扫码结果设置到输入框里
                Log.e("扫码注册", "data: " + data);
                decodeBar(data);
                Message msg = new Message();
                msg.obj = data;
                msg.what = 9;
                mHandler_tip.sendMessage(msg);
                scanDecode.stopScan();
            } else {
                String barCode;
                String denatorId;
                if (data.length() == 28) {
                    //Y5620413H00009A630FD74D87604()
                    //5620722H12345+000ABCDEF+B603+0+1  13 22 26 27 28
                    Log.e("扫码", "data: " + data);
                    //5620302H00001A62F400FFF20AB603
                    //5420302H00001A6F4FFF20AB603
                    //Y5620413H00009A630FD74D87604
//                    barCode = data.substring(1, 14);
//                    String a = data.substring(13, 22);
//                    denatorId = a.substring(0, 2) + "2" + a.substring(2, 4) + "00" + a.substring(4);

                    //内蒙版
                    barCode = data.substring(0, 13);
                    denatorId = "A621" + data.substring(13, 22);
                    String yscs = data.substring(22, 26);
                    String version = data.substring(26, 27);
                    String duan = data.substring(27, 28);

                    insertSingleDenator_2(barCode, denatorId, yscs, version, duan);//同时注册管壳码和芯片码
                } else if (data.length() == 13) {
                    barCode = getContinueScanBlastNo(data);//VR:1;SC:5600508H09974;
                    insertSingleDenator(barCode);
                }
                hideInputKeyboard();//隐藏光标
            }
        });
    }

    private void handler() {
        mHandler_0 = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1001:
                    Log.e("liyi_1001", "更新视图 区域" + mRegion);
                    Log.e("liyi_1001", "更新视图 雷管数量: " + mListData.size());

                    // 查询全部雷管 倒叙(序号)
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();

                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());
                    showDuanSum(duan);
                    break;

                // 重新排序 更新视图
                case 1002:
                    // 雷管孔号排序 并 重新查询
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();

                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());

                    Log.e("liyi_1002", "更新视图 区域" + mRegion);
                    Log.e("liyi_1002", "更新视图 雷管数量" + mListData.size());
                    break;

                // 电源显示
//                case 1003:
//                    if (busInfo != null) {
//                        txt_currentVolt.setText("当前电压:" + busInfo.getBusVoltage() + "V");
//                        txt_currentIC.setText("当前电流:" + Math.round(busInfo.getBusCurrentIa() * 1000) + "μA");
//                        // 判断当前电流是否偏大
//                        if (Math.round(busInfo.getBusCurrentIa() * 1000) > 80) {
//                            txt_currentIC.setTextColor(Color.RED);
//                        } else {
//                            txt_currentIC.setTextColor(Color.GREEN);
//                        }
//                    }
//                    break;
                case 1005://按管壳码排序
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    Collections.sort(mListData);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 1006:
                    MmkvUtils.savecode("duan", 1);
                    duan=1;
                    btnAddDelay.setText("段位:" + duan);
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
            Log.e("handler", "msg.what: " + msg.what);
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
                show_Toast("与" + lg_Piece + "区第" + lg_No + "发" + singleShellNo + "重复");
                int total = showDenatorSum();
//                reisterListView.setSelection(total - Integer.parseInt(lg_No));
                MoveToPosition(linearLayoutManager, mListView, total - Integer.parseInt(lg_No));
            } else if (msg.what == 6) {
                SoundPlayUtils.play(4);
                show_Toast("当前管壳码超出13位,请检查雷管或系统版本是否符合后,再次注册");
            } else if (msg.what == 7) {
                SoundPlayUtils.play(4);
                show_Toast_long("与" + lg_Piece + "区第" + lg_No + "发" + singleShellNo + "重复");
            } else if (msg.what == 8) {
                SoundPlayUtils.play(4);
                show_Toast("有延时为空,请先设置延时");
            } else if (msg.what == 9) {
                decodeBar(msg.obj.toString());
            } else if (msg.what == 10) {
                show_Toast("找不到对应的生产数据,请先导入生产数据");
            } else if (msg.what == 11) {
                show_Toast("输入的日期格式不对");
            } else if (msg.what == 12) {
                show_Toast("当前雷管为煤许产品,注册失败");
            } else if (msg.what == 2001) {
                show_Toast(msg.obj.toString());
                SoundPlayUtils.play(4);
            } else {
                SoundPlayUtils.play(4);
                show_Toast("注册失败");
            }
            return false;
        });
        mHandler_1 = new Handler(msg -> {
            if (tipInfoFlag == 1) {
                if (busInfo != null) {
                    txtCurrentVolt.setText(getResources().getString(R.string.text_reister_vol) + busInfo.getBusVoltage() + "V");
                    txtCurrentIC.setText(getResources().getString(R.string.text_reister_ele) + Math.round(busInfo.getBusCurrentIa() * 1000) + "μA");
                }
            }
            if (tipInfoFlag == 2) {//提示已注册多少发
                if (busInfo != null) {
                    byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");
                    sendCmd(reCmd);
                }
                showDenatorSum();
            }
            if (tipInfoFlag == 3) {//未收到关闭电源命令
                show_Toast(getResources().getString(R.string.text_error_tip5));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 4) {//未收到打开电源命令
                show_Toast(getResources().getString(R.string.text_error_tip6));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 5) {//桥丝不正常
                show_Toast(getResources().getString(R.string.text_error_tip7));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 88) {//刷新界面
                showDenatorSum();
                edit_start_entboxNoAndSerial_st.getText().clear();
                edit_end_entboxNoAndSerial_ed.getText().clear();//.setText("")
//                    etNum.getText().clear();//连续注册个数
            }
            if (tipInfoFlag == 89) {//刷新界面
                show_Toast("输入的管壳码重复");
                showDenatorSum();
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
        String save_f1 = (String) MmkvUtils.getcode("f1", "1");
        String save_f2 = (String) MmkvUtils.getcode("f2", "1");
        String save_start = (String) MmkvUtils.getcode("start", "1");
        if (!save_f1.equals("1")) {
            reEtF1.setText(save_f1);
        } else {
            reEtF1.setText("10");
        }
        if (!save_f2.equals("1")) {
            reEtF2.setText(save_f2);
        } else {
            reEtF2.setText("15");
        }
        if (!save_start.equals("1")) {
            et_startDelay.setText(save_start);
        } else {
            et_startDelay.setText("0");
        }

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
            //切换UID后再设置一下长按方法
            mAdapter = new DetonatorAdapter_Paper<>(ReisterMainPage_scan.this, a);
            mListView.setLayoutManager(linearLayoutManager);
            mListView.setAdapter(mAdapter);
            mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
            mAdapter.setOnItemLongClick(position -> {
                DenatorBaseinfo info = mListData.get(position);
                int no = info.getBlastserial();
                int delay = info.getDelay();
                String shellBlastNo = info.getShellBlastNo();
                String denatorId = info.getDenatorId();
                int duan = info.getDuan();
                int duanNo = info.getDuanNo();
                // 序号 延时 管壳码
                modifyBlastBaseInfo(no, delay, shellBlastNo, denatorId, duan, duanNo,info);
            });
        });


    }

    private long mExitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && pb_show == 1) {
//            if ((System.currentTimeMillis() - mExitTime) > 2000) {// System.currentTimeMillis()无论何时调用，肯定大于2000
//                show_Toast("正在运行程序请稍后退出");
//                mExitTime = System.currentTimeMillis();
//            }
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
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
                .setTitle("注册提示")//设置对话框的标题//"成功起爆"
                .setMessage("注册列表中的雷管已在起爆历史记录里,是否清空列表")//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确认清空", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null);
//                        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
                        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                        //初始化雷管数量
                        for (int i = 1; i < 21; i++) {
                            showDuanSum(i);
                        }
                        dialog.dismiss();
                        Utils.saveFile();//把软存中的数据存入磁盘中
                    }
                }).create();
        dialog.show();
    }

    /**
     * 扫描箱号
     */
    private void addXiangHao(String data) {
        char[] xh = data.toCharArray();
        char[] strNo1 = {xh[1], xh[2], xh[9], xh[10], xh[11], xh[12], xh[13], xh[14]};//箱号数组
        final String strNo = "00";
        String a = xh[5] + "" + xh[6];
        String endNo = Utils.XiangHao(a);
        final String prex = String.valueOf(strNo1);
        final int finalEndNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + xh[17] + endNo);
        final int finalStrNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + xh[17] + strNo);
        new Thread(() -> {
            insertDenator(prex, finalStrNo, finalEndNo);//添加
        }).start();
    }

    private void init() {
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
            DenatorBaseinfo info = mListData.get(position);
            int no = info.getBlastserial();
            int delay = info.getDelay();
            String shellBlastNo = info.getShellBlastNo();
            String denatorId = info.getDenatorId();
            int duan = info.getDuan();
            int duanNo = info.getDuanNo();
            // 序号 延时 管壳码
            modifyBlastBaseInfo(no, delay, shellBlastNo, denatorId, duan, duanNo,info);
        });
        this.isSingleReisher = 0;

        //扫描结束
        //单发输入监听
        editScanChangjia.addTextChangedListener(single_1_changjia);
        editScanRiqi.addTextChangedListener(single_1_riqi);
        editScanTezheng.addTextChangedListener(single_1_tezheng);
        editScanHehao.addTextChangedListener(single_1_hehao);
        editScanLiushui.addTextChangedListener(single_1_liushui);
        //管壳号扫描分码--开始
        edit_start_entBF2Bit_st = (EditText) this.findViewById(R.id.entBF2Bit_st);//开始厂家码
        edit_start_entBF2Bit_st.addTextChangedListener(st_1_watcher);
        edit_start_entproduceDate_st = (EditText) this.findViewById(R.id.entproduceDate_st);//开始日期码
        edit_start_entproduceDate_st.addTextChangedListener(st_2_watcher);
        edit_start_entAT1Bit_st = (EditText) this.findViewById(R.id.entAT1Bit_st);//开始特征码
        edit_start_entAT1Bit_st.addTextChangedListener(st_3_watcher);
        edit_start_entboxNoAndSerial_st = (EditText) this.findViewById(R.id.entboxNoAndSerial_st);//开始流水号
        //点击空白位置 隐藏软键盘
        container1.setOnTouchListener((v, event) -> {
            if (null != ReisterMainPage_scan.this.getCurrentFocus()) {
                container1.setFocusable(true);
                container1.setFocusableInTouchMode(true);
                container1.requestFocus();
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return mInputMethodManager.hideSoftInputFromWindow(ReisterMainPage_scan.this.getCurrentFocus().getWindowToken(), 0);
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
        edit_end_entBF2Bit_en.addTextChangedListener(end_1_watcher);

        edit_end_entproduceDate_ed = (EditText) this.findViewById(R.id.entproduceDate_ed);
        edit_end_entproduceDate_ed.addTextChangedListener(end_2_watcher);

        edit_end_entAT1Bit_ed = (EditText) this.findViewById(R.id.entAT1Bit_ed);
        edit_end_entAT1Bit_ed.addTextChangedListener(end_3_watcher);

        edit_end_entboxNoAndSerial_ed = (EditText) this.findViewById(R.id.entboxNoAndSerial_ed);
        edit_end_entboxNoAndSerial_ed.addTextChangedListener(end_4_watcher);

        et_startDelay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    stopScan();
                }
                displayInputKeyboard(v, hasFocus);
            }
        });
        if (factoryFeature != null && factoryFeature.trim().length() == 1) {
            edit_end_entAT1Bit_ed.setText(factoryFeature);
            edit_start_entAT1Bit_st.setText(factoryFeature);
            editScanTezheng.setText(factoryFeature);
            edit_end_entAT1Bit_ed.setFocusable(false);
            edit_start_entAT1Bit_st.setFocusable(false);
            editScanTezheng.setFocusable(false);
        }
        Log.e("厂家", "factoryCode: " + factoryCode + "--factoryCode:" + factoryCode);
        if (factoryCode != null && factoryCode.trim().length() > 0) {
            edit_end_entBF2Bit_en.setText(factoryCode);
            edit_start_entBF2Bit_st.setText(factoryCode);
            editScanChangjia.setText(factoryCode);
            edit_end_entBF2Bit_en.setFocusable(false);
            edit_start_entBF2Bit_st.setFocusable(false);
            editScanChangjia.setFocusable(false);

        }


    }

    /**
     * 停止扫码
     */
    private void stopScan() {
        continueScanFlag = 0;
        btnScanReister.setText(getResources().getString(R.string.text_reister_scanReister));//"扫码注册"
        btnSetdelay.setEnabled(true);
        btnInput.setEnabled(true);
        scanDecode.stopScan();//停止扫描
        if (scanBarThread != null) {
            scanBarThread.exit = true;  // 终止线程thread
            try {
                scanBarThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private int showDenatorSum() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryDetonatorRegionDesc(mRegion);
        txtReisteramount.setText("已注册:" + list.size());
        return list.size();
    }

    private void runPbDialog() {
        pb_show = 1;
        tipDlg = new LoadingDialog(ReisterMainPage_scan.this);
//        Context context = tipDlg.getContext();
//        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
//        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
        tipDlg.setMessage("正在操作,请等待...").show();

        new Thread(() -> {
            mHandler_2.sendMessage(mHandler_2.obtainMessage());
            try {
                while (pb_show == 1) {
                    Thread.sleep(100);
                }
                mHandler_2.sendMessage(mHandler_2.obtainMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private ProgressDialog showPbDialog() {

        builder = new ProgressDialog(ReisterMainPage_scan.this);

        View view = LayoutInflater.from(ReisterMainPage_scan.this).inflate(R.layout.pb_loading, null);

        builder.setView(view);

        return builder;
    }

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
     * 获得设置中的最大延时
     */
    private void getFactoryType() {
        String selection = " isSelected = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"是"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOR_TYPE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            deTypeName = cursor.getString(1);
            deTypeSecond = cursor.getString(2);
            cursor.close();
        }
        if (deTypeSecond != null && deTypeSecond.length() > 0) {
            maxSecond = Integer.parseInt(deTypeSecond);
        }
        Log.e("最大延时", "deTypeSecond: " + deTypeSecond);
    }

    @Override
    public void sendInterruptCmd() {
        byte[] reCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");
        try {
            mOutputStream.write(reCmd);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.sendInterruptCmd();
    }

    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
//					mOutputStream.write(mBuffer);
                String str = Utils.bytesToHexFun(mBuffer);
//                Utils.writeLog("Reister sendTo:" + str);
                Log.e("发送命令", "sendCmd: " + str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            return;
        }
    }

    public void displayInputKeyboard(View v, boolean hasFocus) {
        //获取系统 IMM
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hasFocus) {
            //显示 软键盘
            imm.showSoftInput(v, 0);
        } else {
            //隐藏 软键盘
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
        container1.requestFocus();//获取焦点,
        et_startDelay.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        args = new Bundle();
        // TODO Auto-generated method stub
        args.putString("key", "1");
        MyLoad myLoad = new MyLoad(ReisterMainPage_scan.this, args);
        return myLoad;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        duan = (int) MmkvUtils.getcode("duan", 1);
        btnAddDelay.setText("段位:" + duan);
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
        // TODO Auto-generated method stub
        if (db != null) db.close();
        if (tipDlg != null) {
            tipDlg.dismiss();
            tipDlg = null;
        }
//        Utils.saveFile();//把软存中的数据存入磁盘中
        scanDecode.stopScan();//停止扫描
        if (scanBarThread != null) {
            scanBarThread.exit = true;  // 终止线程thread
            try {
                scanBarThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (zhuceThread != null) {
            scanBarThread.exit = true;  // 终止线程thread
            try {
                zhuceThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//                zhuceThread.interrupt();
//            Log.e("关闭线程", "关闭线程: ");
        }
        Log.e("延时长度", "reEtF1.getText().length(): " + reEtF1.getText().length());
        if (reEtF1.getText().length() > 0) {
            MmkvUtils.savecode("f1", reEtF1.getText().toString());
        }
        if (reEtF2.getText().length() > 0) {
            MmkvUtils.savecode("f2", reEtF2.getText().toString());
        }
        MmkvUtils.savecode("start", et_startDelay.getText().toString());
        scanDecode.onDestroy();//回复初始状态

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
        String addNum = etNum.getText().toString();

        if (StringUtils.isBlank(st2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip11);//"起始厂家码不能为空"
            return tipStr;
        }
        if (StringUtils.isBlank(stsno)) {
            tipStr = "开始流水号不能为空";//"起始厂家码不能为空"
            return tipStr;
        }
        if (StringUtils.isBlank(stproDt)) {
            tipStr = getResources().getString(R.string.text_error_tip12);//起始生产日期不能为空
            return tipStr;
        }
        if (StringUtils.isBlank(st1Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip13);// "起始特征码不能为空";
            return tipStr;
        }
        if (StringUtils.isBlank(stsno)) {
            tipStr = getResources().getString(R.string.text_error_tip14); //"起始序号不能为空";
            return tipStr;
        }

        if (StringUtils.isBlank(ed2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip15);// "结束厂家码不能为空";
            return tipStr;
        }
        if (StringUtils.isBlank(edproDt)) {
            tipStr = getResources().getString(R.string.text_error_tip16);//  "结束生产日期不能为空";
            return tipStr;
        }
        if (StringUtils.isBlank(ed1Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip17);//  "结束特征码不能为空";
            return tipStr;
        }
        if (StringUtils.isBlank(edsno) && !StringUtils.isNotBlank(addNum)) {
            tipStr = "结束序列号和连续注册个数不能同时为空";//  "结束序列号不能为空";
            return tipStr;
        }

        if (!st2Bit.equals(ed2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip19);//  "管厂码不一致";
            return tipStr;
        }

        if (factoryCode != null && factoryCode.trim().length() > 0 && factoryCode.indexOf(st2Bit) < 0) {
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
        if (factoryFeature != null && factoryFeature.trim().length() > 0 && factoryFeature.indexOf(st1Bit) < 0) {
            tipStr = getResources().getString(R.string.text_error_tip24);//  "特征码与系统中定义的特征码不一致";
            return tipStr;
        }
        if (!Utils.isNum(stsno)) {
            tipStr = getResources().getString(R.string.text_error_tip25);//  "开始序号不是数字";
            return tipStr;
        }
        if (!Utils.isNum(edsno) && StringUtils.isBlank(addNum)) {
            tipStr = getResources().getString(R.string.text_error_tip26);//  "结束序号不是数字";
            return tipStr;
        }
        //90418
        String yue = stproDt.substring(1, 3);
        String ri = stproDt.substring(3, 5);
        if (!dateStrIsValid(yue + "-" + ri, "MM-dd")) {
            tipStr = "输入日期格式不对";
            return tipStr;
        }

        return tipStr;
    }

    private int check(String shellNo) {

        if (reEtF1.getText().length() < 1 || reEtF2.getText().length() < 1 || et_startDelay.getText().length() < 1) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
            return -1;
        }
        //管厂码
        String facCode = Utils.getDetonatorShellToFactoryCodeStr(shellNo);
        //特征码
        String facFea = Utils.getDetonatorShellToFeatureStr(shellNo);
        //雷管信息有误，管厂码不正确，请检查
        if (factoryCode != null && factoryCode.trim().length() > 0 && factoryCode.indexOf(facCode) < 0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
            return -1;
        }
        if (shellNo.length() > 13) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(6));
            return -1;
        }
        //雷管信息有误，特征码不正确，请检查
        if (factoryFeature != null && factoryFeature.trim().length() > 0 && factoryFeature.indexOf(facFea) < 0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
            return -1;
        }
        //检查重复数据
        if (checkRepeatShellNo(shellNo)) {
            singleShellNo = shellNo;
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }

        String yue = shellNo.substring(3, 5);
        String ri = shellNo.substring(5, 7);

        if (!dateStrIsValid(yue + "-" + ri, "MM-dd")) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(11));
            return -1;
        }
        return 0;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) info.id;//这里的info.id对应的就是数据库中_id的值

        String Temp = "";
        switch (item.getItemId()) {
            case 1:
                Temp = "删除";
                String whereClause = "id=?";
                String[] whereArgs = {String.valueOf(id)};
                db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, whereClause, whereArgs);
                Utils.saveFile();//把软存中的数据存入磁盘中
//                getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
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

    /**
     * 修改雷管延期 弹窗
     */
    private void modifyBlastBaseInfo(int no, int delay, final String shellBlastNo, final String denatorId, final int duan, final int duanNo,DenatorBaseinfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delaymodifydialog, null);
        builder.setView(view);

        EditText et_no = view.findViewById(R.id.serialNo);
        EditText et_shell = view.findViewById(R.id.denatorNo);
        EditText et_delay = view.findViewById(R.id.delaytime);
        EditText et_duanNo = view.findViewById(R.id.et_duanNo);
        TextView tv_duan = view.findViewById(R.id.tv_duan);

        et_no.setText(String.valueOf(no));
        et_delay.setText(String.valueOf(delay));
        et_shell.setText(shellBlastNo);
        tv_duan.setText(duan + "-"+duanNo);
        et_duanNo.setText(duanNo+"");
        builder.setNegativeButton("插入孔", (dialog, which) -> {
            if(info.getFanzhuan()!=null && info.getFanzhuan().equals("0")){
                show_Toast("当前雷管已翻转,请恢复后再插入新的雷管");
            }else {
                //插入方法
                getSupportActionBar().setTitle("正在插入孔");
                GreenDaoMaster master = new GreenDaoMaster();
                db_charu=master.querylgMaxduanNo(info.getDuanNo(),info.getDuan(),mRegion);
                Log.e(TAG, "选中插入的雷管: "+info.getShellBlastNo()+" 延时:"+info.getDelay() );
                Log.e(TAG, "选中插入的雷管: "+db_charu.getShellBlastNo()+" 延时:"+db_charu.getDelay() );
                charu=true;
            }

        });
        builder.setNeutralButton("删除", (dialog, which) -> {
            dialog.dismiss();

            // TODO 开启进度条
            runPbDialog();
            new Thread(() -> {
                int a = new GreenDaoMaster().querylgNum(info.getDuanNo(),info.getDuan(),mRegion);
                if(a==1){
                    //查找后一发雷管
                    DenatorBaseinfo denatorBaseinfo = new GreenDaoMaster().querylgduanNo(info.getDuanNo()+1,info.getDuan(),mRegion);
                    if(denatorBaseinfo!=null){//
                        int delay_add = denatorBaseinfo.getDelay()-info.getDelay();
                        Utils.jianshaoData(mRegion,info,flag_t1,delay_add,duan);//插入雷管的后面所有雷管序号+1
                    }
                }

                // 删除某一发雷管
                int duan_guan = new GreenDaoMaster().getDuan(shellBlastNo);
                new GreenDaoMaster().deleteDetonator(shellBlastNo);
                Utils.writeRecord("--删除雷管:" + shellBlastNo);
                Utils.deleteData(mRegion);//重新排序雷管
                //更新每段雷管数量
                Message msg = new Message();
                msg.arg1 = duan_guan;
                mHandler_showNum.sendMessage(msg);
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                pb_show = 0;
            }).start();

        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            String delay1 = et_delay.getText().toString();
            Utils.writeRecord("-单发修改延时:" + "-管壳码:" + shellBlastNo + "-延时:" + delay1);
            Log.e("单发修改", "delay1: " + delay1);
            Log.e("单发修改", "maxSecond: " + maxSecond);
            if (maxSecond != 0 && Integer.parseInt(delay1) > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2001, "已达到最大延时限制" + maxSecond + "ms"));

            } else if (delay1.trim().length() < 1 || maxSecond > 0 && Integer.parseInt(delay1) > maxSecond) {
                show_Toast("延时为空或大于最大设定延时，修改失败! ");

            } else {
                // 修改雷管延时
                new GreenDaoMaster().updateDetonatorDelay(shellBlastNo, Integer.parseInt(delay1), Integer.parseInt(et_duanNo.getText().toString()));
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

                show_Toast(shellBlastNo + "\n修改成功");

                Utils.saveFile();
            }
            dialog.dismiss();
        });


        builder.show();
    }

    private void modifyBlastBaseInfo(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReisterMainPage_scan.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请修改雷管信息");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(ReisterMainPage_scan.this).inflate(R.layout.blastbasedialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
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


        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String a = username.getText().toString().trim();
                String b = password.getText().toString().trim();
                //    将输入的用户名和密码打印出来
                show_Toast("管壳码: " + a + ", 延时: " + b);
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

//    private void modifyBlastBaseInfo(String serialNo, String hoteNo, String delaytime, final String denatorNo) {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        // builder.setIcon(R.drawable.ic_launcher);
//        //   builder.setTitle("修改延时信息");
//        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
//        View view = LayoutInflater.from(this).inflate(R.layout.delaymodifydialog, null);
//        //    设置我们自己定义的布局文件作为弹出框的Content
//        builder.setView(view);
//
//        final EditText serialNoTxt = (EditText) view.findViewById(R.id.serialNo);
//        final EditText denatorNoTxt = (EditText) view.findViewById(R.id.denatorNo);
//        final EditText delaytimeTxt = (EditText) view.findViewById(R.id.delaytime);
//
//        serialNoTxt.setEnabled(false);
//        denatorNoTxt.setEnabled(false);
//
//        serialNoTxt.setText(serialNo);
//        denatorNoTxt.setText(denatorNo);
//        delaytimeTxt.setText(delaytime);
//
//        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //String a = username.getText().toString().trim();
//                String b = delaytimeTxt.getText().toString().trim();
//                if (maxSecond != 0 && Integer.parseInt(b) > maxSecond) {//
//                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
//                    dialog.dismiss();
//                } else if (b == null || b.trim().length() < 1 || (maxSecond > 0 && Integer.parseInt(b) > maxSecond)) {
//                    show_Toast(getString(R.string.text_error_tip37));
//                    dialog.dismiss();
//                } else {
//                    Utils.writeRecord("-单发修改延时:" + "-管壳码:" + denatorNo + "-延时:" + b);
//                    modifyDelayTime(selectDenatorId, b);
////                    getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
//                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
//                    //    将输入的用户名和密码打印出来
//                    show_Toast(getString(R.string.text_error_tip38));
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.setNeutralButton("删除", (dialog, which) -> {
//            dialog.dismiss();
//            pb_show = 1;
//            runPbDialog();
//            Utils.writeRecord("-单发删除:" + "-删除管壳码:" + denatorNo + "-延时" + delaytime);
//            new Thread(() -> {
//                String whereClause = "shellBlastNo = ?";
//                String[] whereArgs = {denatorNo};
//                db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, whereClause, whereArgs);
//                Utils.deleteData(mRegion);//重新排序雷管
////                    getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
//                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
//                tipDlg.dismiss();
//                Utils.saveFile();//把软存中的数据存入磁盘中
//                pb_show = 0;
//            }).start();
//        });
//        builder.show();
//    }

    //更新延时
    public int modifyDelayTime(String id, String delay) {
        ContentValues values = new ContentValues();
        values.put("delay", delay);
        db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + id});
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }

    protected void onDataReceived(byte[] buffer, int size) {
        // ignore incoming data
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        //String crs16 = CRC16.bytesToHexString(cmdBuf);
        //System.out.println(crs16);

        String fromCommad = Utils.bytesToHexFun(cmdBuf);
        //Utils.writeLog("Firing recTemp:"+fromCommad);
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
            //	System.out.println("fromCommad="+fromCommad);
//            Utils.writeLog("reister recFrom:" + fromCommad);
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd(fromCommad);
                if (cmd != null) {
                    doWithReceivData(cmd, cmdBuf);
                }
            }
        } else {
            String data = new String(cmdBuf).trim();//使用构造函数转换成字符串
            Utils.writeLog("扫码结果:" + data);
            //扫码注册
            if (data.length() == 19) {//扫描箱号
                addXiangHao(data);
            }
            if (sanButtonFlag > 0 && data.length() == 13) {
//                optGpio_down(PIN_TRACKER_EN);//扫描头下电
                powerOffScanDevice(PIN_TRACKER_EN);//扫码头下电

//                mHandler_0.sendMessage(mHandler_0.obtainMessage(1004, data));

            } else {
                String barCode = getContinueScanBlastNo(data);
                if (barCode == null) return;
                if (checkRepeatShellNo(barCode)) {
                    singleShellNo = barCode;
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
                    return;
                }
                insertSingleDenator(barCode);
            }

        }
    }

    /**
     * 关闭守护线程
     */
    private void closeThread() {

        //Thread_stage_1 ttst_1
        if (closeOpenThread != null) {
            closeOpenThread.exit = true;  // 终止线程thread
            try {
                closeOpenThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //closeOpenThread =null;
        if (sendOpenThread != null) {
            sendOpenThread.exit = true;  // 终止线程thread
            try {
                sendOpenThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (this.scanBarThread != null) {
            scanBarThread.exit = true;  // 终止线程thread
            try {
                scanBarThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /***
     * 处理
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf) {

        if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//开启总线电源指令
            sendOpenThread.exit = true;
//            Log.e("是否检测桥丝", "qiaosi_set: "+qiaosi_set);
            if (qiaosi_set.equals("true")) {
                byte[] reCmd = OneReisterCmd.setToXbCommon_Reister_Init12_2("00", "01");//进入自动注册模式(00不检测01检测)桥丝
                sendCmd(reCmd);
            } else {
                byte[] reCmd = OneReisterCmd.setToXbCommon_Reister_Init12_2("00", "00");//进入自动注册模式(00不检测01检测)桥丝
                sendCmd(reCmd);
            }


        } else if (DefCommand.CMD_1_REISTER_1.equals(cmd)) {//进入自动注册模式
            //发送获取电源信息
            byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00");
            sendCmd(reCmd);

        } else if (DefCommand.CMD_1_REISTER_3.equals(cmd)) {//有雷管接入

        } else if (DefCommand.CMD_1_REISTER_4.equals(cmd)) {//退出自动注册模式
            if (initCloseCmdReFlag == 1) {//打开电源
                revCloseCmdReFlag = 1;
                closeOpenThread.exit = true;
                sendOpenThread = new SendOpenPower();
                sendOpenThread.start();
            } else {
                //发送停止获取电源信息
                byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");
                sendCmd(reCmd);
            }
        } else if (DefCommand.CMD_4_XBSTATUS_1.equals(cmd)) {//总线电流电压
            From42Power fromData = FourStatusCmd.decodeFromReceiveDataPower24_1("00", cmdBuf);
            busInfo = fromData;
            tipInfoFlag = 1;
            mHandler_1.sendMessage(mHandler_1.obtainMessage());

        } else {

        }

    }


    /**
     * 查询生产表中对应的管壳码
     */
    private DetonatorTypeNew serchDenatorForDetonatorTypeNew(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryDetonatorForTypeNew(denatorId);
    }

    /***
     * 单发注册方法(扫码注册,单发输入会用到)
     */
    private int insertSingleDenator(String shellNo) {
        Log.e("扫码", "单发注册方法1: " );
        if (shellNo.length() != 13) {
            return -1;
        }
        if (et_startDelay.getText().length() == 0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
            return -1;
        }
        if (check(shellNo) == -1) {
            return -1;
        }
//        String denatorId = serchDenatorId(shellNo);
        DetonatorTypeNew detonatorTypeNew = new GreenDaoMaster().serchDenatorId(shellNo);
        //判断芯片码(要传13位芯片码,不要传8位的,里有截取方法)//判断8位芯片码
        if (detonatorTypeNew != null && checkRepeatdenatorId(detonatorTypeNew.getDetonatorId())) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
//        if (detonatorTypeNew == null) {
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
//            return -1;
//        }

        int start_delay = Integer.parseInt(String.valueOf(et_startDelay.getText()));//开始延时
        int f1 = Integer.parseInt(String.valueOf(reEtF1.getText()));//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
//        int maxNo = getMaxNumberNo();
//        int delay = getMaxDelay(maxNo);//获取最大延时
        // 获取 该区域 最大序号
        int maxNo = new GreenDaoMaster().getPieceMaxNum(duan,mRegion);
        // 获取 该区域 最大序号的延时
        int delay = new GreenDaoMaster().getPieceMaxNumDelay(duan,mRegion);
        if(delay==0){
            delay = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);
        }
        int delay_start = delay;
        Log.e("扫码", "delay_set: " + delay_set);
        if (delay_set.equals("f1")) {
            if (maxSecond != 0 && delay + f1 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxSecond != 0 && delay + f2 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        }
        int tk_num=0;
        if(etTk.getText().toString()!=null&&etTk.getText().toString().length()>0){
            tk_num= Integer.parseInt(etTk.getText().toString());
        }

        if (delay_set.equals("f1")) {//获取最大延时有问题
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                if(flag_tk){
                    delay = delay + f1*(tk_num+1);
                }else {
                    delay = delay + f1;
                }

            }
        } else if (delay_set.equals("f2")) {
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                if(flag_tk){
                    delay = delay + f2*(tk_num+1);
                }else {
                    delay = delay + f2;
                }
            }
        }
        int duanNUM = getDuanNo(duan, mRegion);//也得做区域区分
        int duanNo2 = new GreenDaoMaster().getPieceMaxDuanNo(duan, mRegion);//获取该区域 最大序号的延时
        Log.e("扫码", "duanNo2: " + duanNo2);
        maxNo++;
        DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
        denatorBaseinfo.setBlastserial(maxNo);
        denatorBaseinfo.setSithole(maxNo + "");
        denatorBaseinfo.setShellBlastNo(shellNo);
        denatorBaseinfo.setDelay(delay);
        denatorBaseinfo.setRegdate(Utils.getDateFormatLong(new Date()));
        denatorBaseinfo.setStatusCode("02");
        denatorBaseinfo.setStatusName("已注册");
        denatorBaseinfo.setErrorCode("FF");
        denatorBaseinfo.setErrorName("");
        denatorBaseinfo.setWire("");//桥丝状态
        denatorBaseinfo.setPiece(mRegion);
        denatorBaseinfo.setDuan(duan);
        denatorBaseinfo.setDuanNo((duanNo2 + 1) );
        if (!flag_t1) {//同孔
            if(duanNo2==0){
                duanNo2=1;
            }
            denatorBaseinfo.setDuanNo((duanNo2) );
            denatorBaseinfo.setDelay(delay_start);
        }
        int delay_add=0;
        if(charu){
            Log.e(TAG, "插入孔前一发延时: "+db_charu.getDelay() );
            if (!flag_t1) {//同孔
                denatorBaseinfo.setDuanNo(db_charu.getDuanNo() );
                denatorBaseinfo.setDelay(db_charu.getDelay());
            }else {

                delay=db_charu.getDelay();
                if (delay_set.equals("f1")) {//获取最大延时有问题
                    delay_add=f1;
                    if (maxNo == 0) {
                        delay = delay + start_delay;
                    } else {
                        if(flag_tk){
                            delay = delay + f1*(tk_num+1);
                        }else {
                            delay = delay + f1;
                        }
                    }
                } else if (delay_set.equals("f2")) {
                    delay_add=f2;
                    if (maxNo == 0) {
                        delay = delay + start_delay;
                    } else {
                        if(flag_tk){
                            delay = delay + f2*(tk_num+1);
                        }else {
                            delay = delay + f2;
                        }
                    }
                }

                if(flag_t1&&delay==db_charu.getDelay()){
                    show_Toast("没选同孔,不能设置跟选中雷管相同延时");
                    return -1;
                }
                denatorBaseinfo.setDelay(delay);
                denatorBaseinfo.setDuanNo(db_charu.getDuanNo()+1);
            }

            Utils.charuData(mRegion,db_charu,flag_t1,delay_add,duan);//插入雷管的后面所有雷管序号+1
            int xuhao =db_charu.getBlastserial()+1;
            int konghao = Integer.parseInt(db_charu.getSithole())+1;
            denatorBaseinfo.setBlastserial(xuhao);
            denatorBaseinfo.setSithole(konghao + "");
            denatorBaseinfo.setDuan(db_charu.getDuan());

            charu=false;
        }


        if (detonatorTypeNew != null && !detonatorTypeNew.getDetonatorId().equals("0")) {
            denatorBaseinfo.setDenatorId(detonatorTypeNew.getDetonatorId());
            denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
        }

        //向数据库插入数据
        getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);

        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        Utils.writeRecord("单发注册:--管壳码:" + shellNo + "--延时:" + delay);
        return 0;
    }

    /***
     * 扫码注册方法
     */
    private int insertSingleDenator_2(String shellNo, String denatorId, String yscs, String version, String duan_scan) {
        Log.e("扫码", "单发注册方法2: " );
        if (shellNo.length() != 13) {
            return -1;
        }
        if (!duan_scan.equals("0")) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(12));
            return -1;
        }
        if (et_startDelay.getText().length() == 0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
            return -1;
        }
        if (check(shellNo) == -1) {
            return -1;
        }
        if (checkRepeatdenatorId(denatorId)) {//芯片码查重
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
//        int maxNo = getMaxNumberNo();
        int start_delay = Integer.parseInt(String.valueOf(et_startDelay.getText()));//开始延时
        int f1 = Integer.parseInt(String.valueOf(reEtF1.getText()));//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
//        int delay = getMaxDelay(maxNo);//获取最大延时

        int maxNo = new GreenDaoMaster().getPieceMaxNum(duan,mRegion);//获取该区域最大序号
        int delay = new GreenDaoMaster().getPieceMaxNumDelay(duan,mRegion);//获取该区域 最大序号的延时
        if(delay==0){
            delay = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);
        }
        int delay_start = delay;
        Log.e("扫码", "delay_set: " + delay_set);
        if (delay_set.equals("f1")) {
            if (maxSecond != 0 && delay + f1 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxSecond != 0 && delay + f2 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        }
        int tk_num=0;
        if(etTk.getText().toString()!=null&&etTk.getText().toString().length()>0){
            tk_num= Integer.parseInt(etTk.getText().toString());
        }

        if (delay_set.equals("f1")) {//获取最大延时有问题
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                if(flag_tk){
                    delay = delay + f1*(tk_num+1);
                }else {
                    delay = delay + f1;
                }

            }
        } else if (delay_set.equals("f2")) {
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                if(flag_tk){
                    delay = delay + f2*(tk_num+1);
                }else {
                    delay = delay + f2;
                }
            }
        }
        Utils.writeRecord("单发注册:--管壳码:" + shellNo + "芯片码" + denatorId + "--延时:" + delay);
        int a = 0;
        if (duan_scan.equals("0")) {//普通雷管按当前页面选择的来
            a = duan;
        } else {
            a = Integer.parseInt(duan_scan);//煤许雷管按二维码设置的来
        }
        int duanNUM = getDuanNo(a, mRegion);//也得做区域区分
        int duanNo2 = new GreenDaoMaster().getPieceMaxDuanNo(duan, mRegion);//获取该区域 最大序号
        Log.e("扫码", "duanNo2: " + duanNo2);
        maxNo++;
        DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
        denatorBaseinfo.setBlastserial(maxNo);
        denatorBaseinfo.setSithole(maxNo + "");
        denatorBaseinfo.setShellBlastNo(shellNo);
        denatorBaseinfo.setDelay(delay);
        denatorBaseinfo.setRegdate(Utils.getDateFormatLong(new Date()));
        denatorBaseinfo.setStatusCode("02");
        denatorBaseinfo.setStatusName("已注册");
        denatorBaseinfo.setErrorCode("FF");
        denatorBaseinfo.setErrorName("");
        denatorBaseinfo.setWire("");//桥丝状态
        denatorBaseinfo.setPiece(mRegion);
        denatorBaseinfo.setDenatorId(denatorId);
        denatorBaseinfo.setZhu_yscs(yscs);
        denatorBaseinfo.setDuan(a);
        denatorBaseinfo.setDuanNo((duanNo2 + 1) );
        Log.e("扫码", "flag_t1: " + flag_t1);
        if (!flag_t1) {
            if(duanNo2==0){
                duanNo2=1;
            }
            denatorBaseinfo.setDuanNo((duanNo2) );
            denatorBaseinfo.setDelay(delay_start);
        }


        denatorBaseinfo.setAuthorization(version);//雷管芯片型号
        //向数据库插入数据
        getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

//        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        return 0;
    }

    /***
     * 注册方法
     * 手动输入注册(通过开始管壳码和截止管壳码计算出所有管壳码)
     */
    private int insertDenator(String prex, int start, int end) {
        Log.e("扫码", "单发注册方法3: " );
        if (end < start) return -1;
        if (start < 0 || end > 99999) return -1;
        String shellNo = "";
        int flag = 0;
        int start_delay = Integer.parseInt(String.valueOf(et_startDelay.getText()));//开始延时
        int f1 = Integer.parseInt(String.valueOf(reEtF1.getText()));//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
//        int maxNo = getMaxNumberNo();
//        int delay = getMaxDelay(maxNo);//获取最大延时
        int maxNo = new GreenDaoMaster().getPieceMaxNum(duan,mRegion);//获取该区域最大序号
        int delay = new GreenDaoMaster().getPieceMaxNumDelay(duan,mRegion);//获取该区域 最大序号的延时
        if(delay==0){
            delay = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);
        }
        int delay_start = delay;
        if (delay_set.equals("f1")) {
            if (maxSecond != 0 && delay + f1 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxSecond != 0 && delay + f2 > maxSecond) {//
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
        int reCount = 0;//统计注册了多少发雷管
        Utils.writeRecord("--手动输入注册--前8位:" + prex + "--开始后5位:" + start +
                "--结束后5位:" + end + "--开始延时:" + start_delay);
        for (int i = start; i <= end; i++) {
            shellNo = prex + String.format("%05d", i);
            if (checkRepeatShellNo(shellNo)) {
                singleShellNo = shellNo;
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
                break;
            }
            DetonatorTypeNew detonatorTypeNew = new GreenDaoMaster().serchDenatorId(shellNo);
//            if (detonatorTypeNew == null) {
//                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
//                pb_show = 0;
//                return -1;
//            }
            int tk_num=0;
            if(etTk.getText().toString()!=null&&etTk.getText().toString().length()>0){
                tk_num= Integer.parseInt(etTk.getText().toString());
            }

            if (delay_set.equals("f1")) {//获取最大延时有问题
                if (maxNo == 0) {
                    delay = delay + start_delay;
                } else {
                    if(flag_tk){
                        delay = delay + f1*(tk_num+1);
                    }else {
                        delay = delay + f1;
                    }
                }
            } else if (delay_set.equals("f2")) {
                if (maxNo == 0) {
                    delay = delay + start_delay;
                } else {
                    if(flag_tk){
                        delay = delay + f2*(tk_num+1);
                    }else {
                        delay = delay + f2;
                    }
                }
            }
            if (maxSecond != 0 && delay > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                break;
            }
//            int duanNUM = getDuanNo(duan, mRegion);//也得做区域区分
            int duanNo2 = new GreenDaoMaster().getPieceMaxDuanNo(duan, mRegion);//获取该区域 最大序号的延时
            Log.e("手动输入3", "duanNo2: " + duanNo2);
            Log.e("手动输入3", "duan: " + duan);
            maxNo++;
            DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
            denatorBaseinfo.setBlastserial(maxNo);
            denatorBaseinfo.setSithole(maxNo + "");
            denatorBaseinfo.setShellBlastNo(shellNo);
            denatorBaseinfo.setDelay(delay);
            denatorBaseinfo.setRegdate(Utils.getDateFormatLong(new Date()));
            denatorBaseinfo.setStatusCode("02");
            denatorBaseinfo.setStatusName("已注册");
            denatorBaseinfo.setErrorCode("FF");
            denatorBaseinfo.setErrorName("");
            denatorBaseinfo.setWire("");//桥丝状态
            denatorBaseinfo.setPiece(mRegion);
            denatorBaseinfo.setDuan(duan);
            denatorBaseinfo.setDuanNo((duanNo2 + 1) );
            Log.e("手动输入3", "flag_t1: " + flag_t1);
            if ( !flag_t1) {
                if(duanNo2==0){
                    duanNo2=1;
                }
                denatorBaseinfo.setDuanNo((duanNo2) );
                denatorBaseinfo.setDelay(delay_start);
            }
            if (detonatorTypeNew != null && !detonatorTypeNew.getDetonatorId().equals("0")) {
                denatorBaseinfo.setDenatorId(detonatorTypeNew.getDetonatorId());
                denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
            }
            //向数据库插入数据
            getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
            reCount++;
        }


        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
//        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
        pb_show = 0;
        tipInfoFlag = 88;
        mHandler_1.sendMessage(mHandler_1.obtainMessage());
        Utils.saveFile();//把软存中的数据存入磁盘中
        return reCount;
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
        return 1;
    }

    /***
     * 得到最大延时
     * @return
     */
    private int getMaxDelay() {
        Cursor cursor = db.rawQuery("select max(blastserial) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null);
        if (cursor != null && cursor.moveToNext()) {
            Log.e("延时", "getMaxDelay: " + cursor.getInt(0));
            int maxDelay = cursor.getInt(0);
            cursor.close();
            return maxDelay;
        }
        return 0;
    }

    /***
     * 得到最大延时
     * @return
     */
    private int getMaxDelay(int no) {
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where blastserial =? ";
        Cursor cursor = db.rawQuery(sql, new String[]{no + ""});
        if (cursor != null && cursor.moveToNext()) {
            Log.e("延时", "getMaxDelay: " + cursor.getInt(0));
            Log.e("延时", "getMaxDelay: " + cursor.getInt(5));
            int maxDelay = cursor.getInt(5);
            cursor.close();
            return maxDelay;
        }
        return 0;
    }

    /**
     * 好像没什么用
     */
    private int getEmptyDenator(int start) {
        String selection = "shellBlastNo = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, selection, selectionArgs, null, null, null);
        int serialNo = -1;
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                if (start < 0) {///???start一直为-1
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
     * 检查重复的管壳码
     *
     * @param shellBlastNo
     * @return
     */
    public boolean checkRepeatShellNo(String shellBlastNo) {
        DenatorBaseinfo denatorBaseinfo = new GreenDaoMaster().checkRepeatShellNo_2(shellBlastNo);
        if (denatorBaseinfo != null) {
            Log.e("注册", "denatorBaseinfo: " + denatorBaseinfo.toString());
            lg_No = denatorBaseinfo.getBlastserial() + "";
            lg_Piece = denatorBaseinfo.getPiece();
            return true;
        } else {
            return false;
        }
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
            lg_Piece = list_lg.get(0).getPiece();
            singleShellNo = list_lg.get(0).getShellBlastNo();
            return true;
        } else {
            return false;
        }
    }


    int flag1 = 0;
    int flag2 = 0;
    boolean flag_t1 = true;//同孔标志
    boolean flag_tk = false;//跳孔标志

    @OnClick({R.id.btn_scanReister, R.id.btn_f1, R.id.btn_f2, R.id.btn_tk_F1, R.id.btn_tk, R.id.btn_setdelay, R.id.btn_input, R.id.btn_single,
            R.id.btn_inputOk, R.id.btn_return, R.id.btn_singleReister, R.id.btn_ReisterScanStart_st,
            R.id.btn_ReisterScanStart_ed, R.id.btn_addDelay,
            R.id.re_btn_f1, R.id.re_btn_f2, R.id.re_btn_f3,
            R.id.re_btn_f4, R.id.re_btn_f5, R.id.re_btn_f6, R.id.re_btn_f7,
            R.id.re_btn_f8, R.id.re_btn_f9, R.id.re_btn_f10, R.id.re_btn_f11, R.id.re_btn_f12, R.id.re_btn_f13,
            R.id.re_btn_f14, R.id.re_btn_f15, R.id.re_btn_f16, R.id.re_btn_f17, R.id.re_btn_f18, R.id.re_btn_f19,
            R.id.re_btn_f20, R.id.re_et_nei1, R.id.re_et_nei2, R.id.re_et_nei3,
            R.id.re_et_nei4, R.id.re_et_nei5, R.id.re_et_nei6, R.id.re_et_nei7,
            R.id.re_et_nei8, R.id.re_et_nei9, R.id.re_et_nei10, R.id.re_et_nei11, R.id.re_et_nei12, R.id.re_et_nei13,
            R.id.re_et_nei14, R.id.re_et_nei15, R.id.re_et_nei16, R.id.re_et_nei17, R.id.re_et_nei18, R.id.re_et_nei19,
            R.id.re_et_nei20})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tk:
                if(etTk.getText().length()==0){
                    show_Toast("请输入跳孔个数");
                    return;
                }
                hideInputKeyboard();
                btnTk.setBackgroundResource(R.drawable.bt_mainpage_style);
                if (!flag_tk) {
                    btnTk.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                    flag_tk = true;
                } else {
                    btnTk.setBackgroundResource(R.drawable.bt_mainpage_style);
                    flag_tk = false;
                }
                break;
            case R.id.btn_tk_F1:
                btnTkF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                if (flag_t1) {
                    btnTkF1.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                    flag_t1 = false;
                } else {
                    btnTkF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                    flag_t1 = true;
                }
                break;

            case R.id.btn_scanReister:
                if (checkDelay()) return;
                if (reEtF1.getText().length() < 1 || reEtF2.getText().length() < 1 || et_startDelay.getText().length() < 1) {
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
                    break;
                }
//                if (deleteList()) return;
                container1.requestFocus();//获取焦点,
                scanDecode.starScan();

                if (continueScanFlag == 0) {
                    continueScanFlag = 1;
                    if (scanBarThread != null) {
                        scanBarThread.exit = true;  // 终止线程thread
                        try {
                            scanBarThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //kt50持续扫码线程
                    scanBarThread = new ScanBar();
                    scanBarThread.start();
                    //st327上电
                    powerOnScanDevice(PIN_TRACKER_EN);//扫码头上电

                    btnScanReister.setText(getResources().getString(R.string.text_reister_scaning));//"正在扫码"
                    btnReisterScanStartEd.setEnabled(false);
                    btnReisterScanStartSt.setEnabled(false);
                } else {
                    continueScanFlag = 0;
                    btnScanReister.setText(getResources().getString(R.string.text_reister_scanReister));//"扫码注册"
                    btnReisterScanStartEd.setEnabled(true);
                    btnReisterScanStartSt.setEnabled(true);
                    //kt50停止扫码头方法
                    scanDecode.stopScan();//停止扫描
                    //st327扫码下电
                    powerOffScanDevice(PIN_TRACKER_EN);//扫码头下电
                    if (scanBarThread != null) {
                        scanBarThread.exit = true;  // 终止线程thread
                        try {
                            scanBarThread.join();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.btn_f1:
                hideInputKeyboard();
                if (checkDelay()) return;
                delay_set = "f1";
                flag2 = 0;
                reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style);
                switch (flag1) {
                    case 0:
                        reBtnF1.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                        flag1 = 1;
                        break;
                    case 1:
                        reBtnF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                        flag1 = 0;
                        break;
                }
                reEtF1.setBackgroundResource(R.drawable.textview_border_green);
                reEtF2.setBackgroundResource(R.drawable.translucent);
                reEtF1.clearFocus();
                reEtF2.clearFocus();
                break;
            case R.id.btn_f2:
                hideInputKeyboard();
                if (checkDelay()) return;
                delay_set = "f2";
                flag1 = 0;
                reBtnF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                switch (flag2) {
                    case 0:
                        reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                        flag2 = 1;
                        break;
                    case 1:
                        reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style);
                        flag2 = 0;
                        break;
                }
                reEtF1.setBackgroundResource(R.drawable.translucent);
                reEtF2.setBackgroundResource(R.drawable.textview_border_green);
                reEtF1.clearFocus();
                reEtF2.clearFocus();
                break;
            case R.id.btn_setdelay:
                String str3 = "设置延时";//"当前雷管信息"
                Intent intent3 = new Intent(this, SetDelayTime.class);
                intent3.putExtra("dataSend", str3);
                startActivityForResult(intent3, 1);
                break;
            case R.id.btn_single:
                if (checkDelay()) return;
                if (llStart.getVisibility() == View.GONE) {
                    lySetDelay.setVisibility(View.GONE);
                    llSingle.setVisibility(View.GONE);
                    llStart.setVisibility(View.VISIBLE);
                    llEnd.setVisibility(View.VISIBLE);
                    llNum.setVisibility(View.VISIBLE);
                    btnInputOk.setVisibility(View.VISIBLE);
                    btnScanReister.setVisibility(View.GONE);
                    btnSingle.setText("返回");
                } else {
                    lySetDelay.setVisibility(View.VISIBLE);
                    llSingle.setVisibility(View.VISIBLE);
                    llStart.setVisibility(View.GONE);
                    llEnd.setVisibility(View.GONE);
                    llNum.setVisibility(View.GONE);
                    btnInputOk.setVisibility(View.GONE);
                    btnScanReister.setVisibility(View.VISIBLE);
                    btnSingle.setText("手动输入");
                }

                break;
            case R.id.btn_input:
                if (llStart.getVisibility() == View.GONE) {
                    llEnd.setVisibility(View.VISIBLE);
                    llStart.setVisibility(View.VISIBLE);
                    llNum.setVisibility(View.VISIBLE);
                    btnInputOk.setVisibility(View.VISIBLE);
                    btnScanReister.setVisibility(View.GONE);
                    lySetDelay.setVisibility(View.GONE);

                    btnInput.setText("扫码注册");
                } else {
                    llEnd.setVisibility(View.GONE);
                    llStart.setVisibility(View.GONE);
                    llNum.setVisibility(View.GONE);
                    btnInputOk.setVisibility(View.GONE);
                    btnScanReister.setVisibility(View.VISIBLE);
                    lySetDelay.setVisibility(View.VISIBLE);
                    btnInput.setText("手动输入");
                }
                break;
            case R.id.btn_inputOk:
                if (reEtF1.getText().length() < 1 || et_startDelay.getText().length() < 1 || reEtF2.getText().length() < 1) {
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
                    break;
                }
//                if (deleteList()) return;//判断列表第一发是否在历史记录里
                hideInputKeyboard();
                String checstr = checkData();//数据校验

                if (checstr == null || checstr.trim().length() < 1) {
                    String st2Bit = edit_start_entBF2Bit_st.getText().toString();
                    String stproDt = edit_start_entproduceDate_st.getText().toString();
                    String st1Bit = edit_start_entAT1Bit_st.getText().toString();
                    String stsno = edit_start_entboxNoAndSerial_st.getText().toString();
                    prex = st2Bit + stproDt + st1Bit;
                    String edsno = edit_end_entboxNoAndSerial_ed.getText().toString();
                    String addNum = etNum.getText().toString();
                    start = Integer.parseInt(stsno);//开始流水号


                    if (addNum.length() > 0) {
                        if (Integer.parseInt(addNum) > 500) {
                            show_Toast("单次最大注册不能超过500发");
                            return;
                        }
                        if (edsno.length() > 1) {
                            show_Toast("终止序号和连续注册个数不能同时输入");
                            return;
                        }
                        num = Integer.parseInt(addNum);//连续注册个数
                        pb_show = 1;
                        runPbDialog();
                        new Thread(() -> {
                            insertDenator(prex, start, start + (num - 1));
                            Log.e("手动输入", "厂号日期: " + prex);
                            Log.e("手动输入", "start: " + start);
                            pb_show = 0;
                        }).start();
                        return;
                    } else {
                        final int end = Integer.parseInt(edsno);//结束流水号
                        if (end < start) {
                            show_Toast(getResources().getString(R.string.text_error_tip27));//  "结束序号不能小于开始序号";
                            return;
                        }
                        if (edsno.length() < 5) {
                            show_Toast("结束序号必须为5位");//  "结束序号不能小于开始序号";
                            return;
                        }
                        if (stproDt.length() < 5) {
                            show_Toast("日期编码必须为5位");//  "结束序号不能小于开始序号";
                            return;
                        }
                        if (start < 0 || end > 99999) {
                            show_Toast(getResources().getString(R.string.text_error_tip28));//  "起始/结束序号不符合要求";
                            return;
                        }
                        if ((end - start) > 1000) {
                            show_Toast(getResources().getString(R.string.text_error_tip29));//  "每一次注册数量不能大于1000";
                            return;
                        }
                        pb_show = 1;
                        runPbDialog();
                        new Thread(() -> {
                            insertDenator(prex, start, end);
                            Log.e("手动输入", "prex: " + prex);
                            Log.e("手动输入", "start: " + start);
                            Log.e("手动输入", "end: " + end);
                            pb_show = 0;
                        }).start();
                    }
                    // int reCount = insertDenator(prex,start,end);
                    //tipDlg.dismiss();
                    // pb_show = 0;

                    // Toast.makeText(ReisterMainPage_scan.this, "本次注册雷管数量为:"+reCount, Toast.LENGTH_LONG).show();
                } else {
                    show_Toast(checstr);
                }

                break;
            case R.id.btn_return:
                closeThread();
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
                break;
            case R.id.btn_singleReister:
                if (reEtF1.getText().length() < 1 || reEtF2.getText().length() < 1 || et_startDelay.getText().length() < 1) {
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
                    break;
                }
                if (isSingleReisher == 0) {
                    btnScanReister.setEnabled(false);
                    btnInputOk.setEnabled(false);
                    btnSingleReister.setText(getResources().getString(R.string.text_singleReister_stop));//"停止注册"
                    isSingleReisher = 1;
                    closeThread();
                    closeOpenThread = new CloseOpenPower();
                    closeOpenThread.start();

                } else {
                    btnScanReister.setEnabled(true);
                    btnInputOk.setEnabled(true);
                    btnSingleReister.setText(getResources().getString(R.string.text_singleReister));//"单发注册"
                    isSingleReisher = 0;
                    initCloseCmdReFlag = 0;
                    initOpenCmdReFlag = 0;
                    revCloseCmdReFlag = 0;
                    revOpenCmdReFlag = 0;

                    byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");
                    sendCmd(powerCmd);
                }
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

            case R.id.btn_addDelay:
//                maxDuanNo = maxDuanNo + 1;
//                setView(maxDuanNo);

                Intent intent = new Intent(this, ChoseDuanActivity.class);
                intentActivityResultLauncher.launch(intent);
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

    private boolean checkDelay() {
        if (reEtF1.getText().toString().equals("")) {
            show_Toast("当前设置延时为空,请重新设置");
            Log.e("f2", reEtF2.getText().toString());
            return true;
        }
        if (reEtF2.getText().toString().equals("")) {
            show_Toast("当前设置延时为空,请重新设置");
            Log.e("f2", reEtF2.getText().toString());
            return true;
        }
        if (et_startDelay.getText().length() == 0) {
            show_Toast("当前起始延时为空,请重新设置");
            return true;
        }
        if (maxSecond != 0) {
            if (Integer.parseInt(reEtF2.getText().toString()) > maxSecond) {
                show_Toast("当前设置延时已超过最大延时" + maxSecond + "ms,请重新设置");
                return true;
            }
            if (Integer.parseInt(et_startDelay.getText().toString()) > maxSecond) {
                show_Toast("当前开始延时已超过最大延时" + maxSecond + "ms,请重新设置");
                return true;
            }
        }
        return false;
    }

    //跳转页面后更新数据
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));// 区域 更新视图
    }

    /**
     * 判断列表第一发是否在历史记录里
     */
    private boolean deleteList() {
        String shellBlastNo = serchFristLG();//获取第一发雷管
        int no = serchFristLGINdenatorHis(shellBlastNo);
        if (no > 0) {
            showAlertDialog();
            scanDecode.stopScan();
            return true;
        }
        return false;
    }


    private class SendOpenPower extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    if (zeroCount == 0) {
                        byte[] powerCmd = FourStatusCmd.setToXbCommon_OpenPower_42_2("00");
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
                        byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");

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

    private class ZhuceThread extends Thread {
        public volatile boolean exit = false;

        public void run() {
            while (exit) {

                insertDenator(prex, start, start + (num - 1));

            }

        }
    }


    private class ScanBar extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    scanDecode.starScan();
                    Thread.sleep(1250);
                    //break;

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
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
                edit_end_entBF2Bit_en.setText(edit_start_entBF2Bit_st.getText());
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
                /*
                edit_end_entBF2Bit_en.setFocusable(true);
            	edit_end_entBF2Bit_en.setFocusableInTouchMode(true);
            	edit_end_entBF2Bit_en.requestFocus();
            	edit_end_entBF2Bit_en.findFocus();
            	*/
                //与扫描冲突
//            	edit_end_entboxNoAndSerial_ed.setFocusable(true);
//            	edit_end_entboxNoAndSerial_ed.setFocusableInTouchMode(true);
//            	edit_end_entboxNoAndSerial_ed.requestFocus();
//            	edit_end_entboxNoAndSerial_ed.findFocus();

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
//                edit_end_entproduceDate_ed.setFocusable(true);
//                edit_end_entproduceDate_ed.setFocusableInTouchMode(true);
//                edit_end_entproduceDate_ed.requestFocus();
//                edit_end_entproduceDate_ed.findFocus();
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

    //单独注册--厂家
    TextWatcher single_1_changjia = new TextWatcher() {
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
                editScanChangjia.setBackgroundColor(Color.GREEN);
                editScanRiqi.setFocusable(true);
                editScanRiqi.setFocusableInTouchMode(true);
                editScanRiqi.requestFocus();
                editScanRiqi.findFocus();
            } else {
                editScanChangjia.setBackgroundColor(Color.RED);
            }
        }
    };

    //单独注册--日期
    TextWatcher single_1_riqi = new TextWatcher() {
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
                if (factoryFeature == null || factoryFeature.trim().length() < 1) {
                    editScanRiqi.setBackgroundColor(Color.GREEN);
                    editScanTezheng.setFocusable(true);
                    editScanTezheng.setFocusableInTouchMode(true);
                    editScanTezheng.requestFocus();
                    editScanTezheng.findFocus();
                } else {
                    editScanRiqi.setBackgroundColor(Color.GREEN);
                    editScanHehao.setFocusable(true);
                    editScanHehao.setFocusableInTouchMode(true);
                    editScanHehao.requestFocus();
                    editScanHehao.findFocus();
                }

            } else {
                editScanRiqi.setBackgroundColor(Color.RED);
            }

        }
    };

    //单独注册--特征
    TextWatcher single_1_tezheng = new TextWatcher() {
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
                editScanTezheng.setBackgroundColor(Color.GREEN);
                editScanHehao.setFocusable(true);
                editScanHehao.setFocusableInTouchMode(true);
                editScanHehao.requestFocus();
                editScanHehao.findFocus();
            } else {
                editScanTezheng.setBackgroundColor(Color.RED);
            }
        }
    };
    //单独注册--盒号
    TextWatcher single_1_hehao = new TextWatcher() {
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
                editScanHehao.setBackgroundColor(Color.GREEN);
                editScanLiushui.setFocusable(true);
                editScanLiushui.setFocusableInTouchMode(true);
                editScanLiushui.requestFocus();
                editScanLiushui.findFocus();
            } else {
                editScanHehao.setBackgroundColor(Color.RED);
            }
        }
    };
    //单独注册--流水号
    TextWatcher single_1_liushui = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 3) {
                editScanLiushui.setBackgroundColor(Color.GREEN);

                insertSingleDenator(editScanChangjia.getText().toString() + editScanRiqi.getText().toString()
                        + editScanTezheng.getText().toString() + editScanHehao.getText().toString()
                        + editScanLiushui.getText().toString());
                editScanLiushui.getText().clear();
            } else {
                editScanLiushui.setBackgroundColor(Color.RED);
            }
        }
    };


    //得到连续管壳码
    private String getContinueScanBlastNo(String strBarcode) {

        if (strBarcode.length() < 13) return null;
        if (strBarcode.trim().length() == 14) {
            strBarcode = strBarcode.substring(0, 13);
            return strBarcode;
        } else if (strBarcode.trim().length() == 13) {
            //strBarcode= strBarcode;
            return strBarcode;
        }
        int index = strBarcode.indexOf("SC:");
        if (index < 0) return null;
        String subBarCode = strBarcode.substring(index + 3, index + 16);
        if (subBarCode.trim().length() < 13) {
            show_Toast("当前二维码不符合规范,请检查后再扫");
            return null;
        }
        return subBarCode;
    }

    //扫码方法
    private void decodeBar(String strParamBarcode) {

        String subBarCode = strParamBarcode;

        if (strParamBarcode.trim().length() > 14) {
            int index = strParamBarcode.indexOf("SC:");
            Log.e("扫码", "index: " + index);
            if (index > 0) {
                show_Toast("不正确的编码，请扫描选择正确的编码");
                return;
            }
            //四川28位的Y5620528H01709A637FFC9741B05
            //内蒙28位的5620722H12345000ABCDEFB60301

            if (strParamBarcode.length() == 28) {
                if (strParamBarcode.substring(0, 1).equals("Y")) {
                    subBarCode = strParamBarcode.substring(1, 14);
                } else {
                    subBarCode = strParamBarcode.substring(0, 13);
                }

            }
        } else {
            if (strParamBarcode.trim().length() == 13) {
                subBarCode = strParamBarcode;
            } else
                return;
        }
        Log.e("扫码结果", "subBarCode: " + subBarCode);
        String facCode = subBarCode.substring(0, 2);
        String dayCode = subBarCode.substring(2, 7);
        String featureCode = subBarCode.substring(7, 8);
        String serialNo = subBarCode.substring(8);
        Log.e("注册页面--扫码注册", "facCode: " + facCode + "  dayCode:" + dayCode + "  featureCode:" + featureCode + "  serialNo:" + serialNo);

        if (sanButtonFlag == 1) {
            edit_start_entBF2Bit_st.setText(facCode);
            edit_start_entproduceDate_st.setText(dayCode);//日期码
            edit_start_entAT1Bit_st.setText(featureCode);
            edit_start_entboxNoAndSerial_st.setText(serialNo);

//            edit_end_entBF2Bit_en.setText("");
//            edit_end_entproduceDate_ed.setText("");
//            edit_end_entAT1Bit_ed.setText("");
            edit_end_entboxNoAndSerial_ed.setText("");
            btnReisterScanStartEd.setEnabled(true);
            btnScanReister.setEnabled(true);
        }
        if (sanButtonFlag == 2) {
            edit_end_entBF2Bit_en.setText(facCode);
            edit_end_entproduceDate_ed.setText(dayCode);
            edit_end_entAT1Bit_ed.setText(featureCode);
            edit_end_entboxNoAndSerial_ed.clearFocus();
            edit_end_entboxNoAndSerial_ed.setText(serialNo);
            btnReisterScanStartSt.setEnabled(true);
            btnScanReister.setEnabled(true);
        }
        sanButtonFlag = 0;
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
                duan=1;
                MmkvUtils.savecode("duan", 1);
                btnAddDelay.setText("段位:" + duan);
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

    /**
     * 验证字符串是否为指定日期格式
     *
     * @param oriDateStr 待验证字符串
     * @param pattern    日期字符串格式, 例如 "yyyy-MM-dd"
     * @return 有效性结果, true 为正确, false 为错误
     */
    public static boolean dateStrIsValid(String oriDateStr, String pattern) {
        Log.e("验证日期", "date: " + oriDateStr);
        if (StringUtils.isBlank(oriDateStr) || StringUtils.isBlank(pattern)) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        try {
            Date date = dateFormat.parse(oriDateStr);
            return oriDateStr.equals(dateFormat.format(date));
        } catch (ParseException e) {
            return false;
        }
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
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("翻转提示")//设置对话框的标题//"成功起爆"
                .setMessage("是否翻转当前段位延时")//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                .setNegativeButton("取消", (dialog12, which) -> dialog12.dismiss())
                .setPositiveButton("确认", (dialog1, which) -> {

                    GreenDaoMaster master = new GreenDaoMaster();
                    List<DenatorBaseinfo> list = master.queryLeiguanDuan(duan, mRegion);
                    List<DenatorBaseinfo> list2 = master.queryLeiguanDuan(duan, mRegion);
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

//        lySetDelay.setFocusable(true);
//        lySetDelay.setFocusableInTouchMode(true);
//        lySetDelay.requestFocus();

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
    private int getDuanNo(int duan, String piece) {
        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where duan =? and piece = ? ", new String[]{duan + "", piece});
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
        Log.e(TAG, "当前区域段数totalNum: " + totalNum);
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
        Cursor cursor = db.rawQuery("select max(duan) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where piece =? ", new String[]{mRegion});
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

}
