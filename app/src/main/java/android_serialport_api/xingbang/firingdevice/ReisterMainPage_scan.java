package android_serialport_api.xingbang.firingdevice;


import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_TRACKER_EN;
import static android_serialport_api.xingbang.Application.getDaoSession;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kfree.comm.system.ScanQrControl;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.suke.widget.SwitchButton;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From12Reister;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.custom.DenatorBaseinfoSelect;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.OnChildButtonClickListener;
import android_serialport_api.xingbang.custom.OngroupButtonClickListener;
import android_serialport_api.xingbang.custom.PaiDataSelect;
import android_serialport_api.xingbang.custom.ZhuCeScanAdapter;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.Defactory;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.PaiData;
import android_serialport_api.xingbang.db.QuYu;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_DetailDao;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.MyAlertDialog;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 雷管注册
 */
public class ReisterMainPage_scan extends SerialPortActivity implements LoaderCallbacks<Cursor>, OnChildButtonClickListener, OngroupButtonClickListener {

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
    RelativeLayout container1;
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

    @BindView(R.id.btn_addDelay)
    Button btnAddDelay;
    @BindView(R.id.btn_tk_F1)
    Button btnTkF1;
    @BindView(R.id.btn_JH_F1)
    Button btnJHF1;
    @BindView(R.id.btn_JH_F2)
    Button btnJHF2;
    @BindView(R.id.btn_start_delay)
    Button btnStartDelay;

    @BindView(R.id.btn_tk)
    Button btnTk;
    @BindView(R.id.et_tk)
    EditText etTk;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    @BindView(R.id.qy_no)
    TextView qyNo;
    @BindView(R.id.qy_txt_total)
    TextView qyTxtTotal;
    @BindView(R.id.qy_txt_totalPai)
    TextView qyTxtTotalPai;
    @BindView(R.id.qy_txt_minDealy)
    TextView qyTxtMinDealy;
    @BindView(R.id.qy_txt_maxDealy)
    TextView qyTxtMaxDealy;
    @BindView(R.id.cl_top)
    LinearLayout clTop;
    @BindView(R.id.btn_pai)
    Button btnPai;
    @BindView(R.id.btn_kong)
    Button btnKong;
    @BindView(R.id.btn_wei)
    Button btnWei;
    @BindView(R.id.zc_list)
    ExpandableListView zcList;
    @BindView(R.id.lay_bottom)
    LinearLayout lay_bottom;
    @BindView(R.id.cd_title)
    CardView cd_title;
    @BindView(R.id.tv_check_all)
    TextView tv_check_all;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.ly_xinxi)
    LinearLayout lyXinxi;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.title_add)
    ImageView titleAdd;
    @BindView(R.id.title_right2)
    TextView titleRight2;
    @BindView(R.id.title_lefttext)
    TextView title_lefttext;
    private SimpleCursorAdapter adapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private String factoryCode = "";//厂家代码
    private String factoryFeature = "";////厂家特征码
    private String deTypeName = "";//雷管类型名称
    private String deTypeSecond = "";//该类型雷管最大延期值
    private String scanInfo = "";
    //是否单发注册
    private boolean isSingleReisher = true;
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
    private String lg_wei;//重复雷管管壳码的位号
    private String lg_pai;//重复雷管管壳码的排号
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
    private String delay_set = "f1";//是f1还是f2
    private String selectDenatorId;//选择的管壳码
    //这是注册了一个观察者模式
    public static final Uri uri = Uri.parse("content://android_serialport_api.xingbang.denatorBaseinfo");
    private String qiaosi_set = "";//是否检测桥丝
    private String version = "";//是否检测桥丝

    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private DetonatorAdapter_Paper<DenatorBaseinfo> mAdapter;
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private List<DenatorBaseinfoSelect> mListDataSelect = new ArrayList<>();
    private Handler mHandler_0 = new Handler();     // UI处理
    private String mOldTitle;   // 原标题
    private String mRegion = "1";     // 区域
    private boolean switchUid = true;//切换uid/管壳码

    //段属性
    private int duan_new = 1;//duan
    private int maxDuanNo = 3;
    private Handler mHandler_showNum = new Handler();//显示雷管数量
    private String duan_set = "0";//是duan1还是duan2
    private int n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20 = 0;//翻转
    private int n21, n22, n23, n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35, n36, n37, n38, n39, n40 = 0;//翻转
    private String TAG = "扫码注册";
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private Boolean charu = false;
    private DenatorBaseinfo db_charu;
    private ScanQrControl mScaner = null;
    private int xiangHao_errNum = 0;//箱码重复数量
    private String quyuId;
    private List<PaiDataSelect> groupList = new ArrayList<>();
    private List<List<DenatorBaseinfoSelect>> childList = new ArrayList<>();
    private ZhuCeScanAdapter zhuceAdapter;
    private int paiMax = 0;
    private int paiMin = 0;
    private int paiChoice = 0;
    private int childListChoice = 0;
    private int groupListChoice = 0;
    private int kongChoice = 0;
    private int weiChoice = 0;
    private MyAlertDialog myDialog;
    QuYu quYu_choice;
    PaiData choicepaiData;
    private String start_delay_data = "";//开始延时
    private String f1_delay_data = "";//孔间延时
    private String f2_delay_data = "";//孔内延时
    private int kongSum = 1;
    private boolean check_gone = false;

    private From12Reister zhuce_form = null;
    private volatile int zhuce_Flag = 0;//单发检测时发送40的标识
    private boolean flag_zhuce = false;//是否新注册
    private boolean flag_add = true;//是否加排或孔
    private boolean flag_saoma = false;//是否加排或孔
    private boolean flag_delete = true;//是否删除

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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mRegion = (String) bundle.get("quyuId");
        Log.e(TAG, "区域 mRegion: " + mRegion);
        quYu_choice = GreenDaoMaster.geQuyu(mRegion);
        Log.e(TAG, "区域 quYu_choice: " + quYu_choice);

        init();
        initList();
        btn_onClick();//button的onClick
        Log.e(TAG, "quYu_choice.getStartDelay(): " + quYu_choice.getStartDelay());

        handler();//所有的handler
        scan();//扫描初始化//扫描参数设置

        hideInputKeyboard();//隐藏焦点
        Utils.writeRecord("---进入手动输入和扫码注册页面---");
        AppLogUtils.writeAppLog("---进入手动输入和扫码注册页面---");


//        MmkvUtils.savecode("duan", 1);//每次进入都重置段位参数

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

        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

        hideInputKeyboard();//隐藏焦点
        Utils.writeRecord("---进入手动输入和扫码注册页面---");
    }

    private void initList() {
        kongChoice = new GreenDaoMaster().queryDetonatorPai(mRegion, paiChoice).size();
        Log.e("初始化", "kongChoice: " + kongChoice);
        //只展开一行
        zcList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < groupList.size(); i++) {
                    if (groupPosition != i) {
                        zcList.collapseGroup(i);
                    }
                }
            }
        });
        //长按事件
        zcList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ExpandableListView parent2 = (ExpandableListView) parent;
                long packedPos = parent2.getExpandableListPosition(position);
                // 获取对应的组或子项位置
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
                //更新选中的排,孔参数
                groupListChoice = groupPosition + 1;//选中排
                childListChoice = childPosition + 1;//选中孔
                check_gone = true;
                paiChoice = groupList.get(groupListChoice - 1).getPaiId();
                if (childListChoice > 0) {
                    kongChoice = childList.get(groupListChoice - 1).get(childListChoice - 1).getBlastserial();
                    weiChoice = childList.get(groupListChoice - 1).get(childListChoice - 1).getDuanNo();
                }
                Log.e("长按item", "更新视图 groupListChoice:" + (groupListChoice));
                Log.e("长按item", "更新视图 childListChoice:" + (childListChoice));
                Log.e("长按item", "更新视图 pai:" + (paiChoice));
                Log.e("长按item", "更新视图 kongChoice:" + (kongChoice));
                Log.e("长按item", "更新视图 weiChoice:" + (weiChoice));
                // 刷新适配器
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1003));

//                deleteLG((ExpandableListView) parent, position, id);//删除雷管


                return true;
            }
        });
//一级点击监听
        zcList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            Log.e(TAG, "一级点击监听: " );
            if (check_gone) {
                check_gone = false;
                // 刷新适配器
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                return true;
            }

            //如果你处理了并且消费了点击返回true,这是一个基本的防止onTouch事件向下或者向上传递的返回机制
//            v.setBackgroundColor(Color.GREEN);
            Log.e(TAG, "根据选中排更新排号  1 paiChoice: " + paiChoice);
            Log.e(TAG, "根据选中排更新排号  1 kongChoice: " + kongChoice);
            Log.e(TAG, "根据选中排更新排号  1 groupListChoice: " + groupListChoice);
            Log.e(TAG, "根据选中排更新排号  1 childListChoice: " + childListChoice);

            if (groupListChoice != groupPosition + 1) {//当更换排之后
                kongChoice = new GreenDaoMaster().queryDetonatorPai(mRegion, groupList.get(groupPosition).getPaiId()).size();
                Log.e(TAG, "----根据选中排更新排号--更换排  mRegion, groupList.get(groupPosition).getPaiId(): " + groupList.get(groupPosition).getPaiId());
                Log.e(TAG, "----根据选中排更新排号--更换排  kongChoice: " + kongChoice);

                childListChoice = kongChoice;
            }
            groupListChoice = groupPosition + 1;
            paiChoice = groupList.get(groupListChoice - 1).getPaiId();


            Log.e(TAG, "根据选中排更新排号  2 paiChoice: " + paiChoice);
            Log.e(TAG, "根据选中排更新排号  2 kongChoice: " + kongChoice);
            Log.e(TAG, "根据选中排更新排号  2 groupListChoice: " + groupListChoice);
            Log.e(TAG, "根据选中排更新排号  2 childListChoice: " + childListChoice);

            GreenDaoMaster master = new GreenDaoMaster();
            int childList_size = master.queryDetonatorPai(mRegion, paiChoice).size();
            zhuceAdapter.setSelcetPosition(groupPosition, childList_size - 1);
            //根据选择的排确定延时的值
            choicepaiData = GreenDaoMaster.gePaiData(mRegion, paiChoice + "");
            //如果有默认排的话,就默认该排的延时
            if (choicepaiData != null) {
                start_delay_data = choicepaiData.getStartDelay();
                f1_delay_data = choicepaiData.getKongDelay();
                f2_delay_data = choicepaiData.getNeiDelay();
                flag_jh_f1 = !choicepaiData.getDiJian();
                kongSum = choicepaiData.getKongNum();
                Log.e(TAG, "根据选中排更新是否递减  flag_jh_f1: " + flag_jh_f1);
            }
            Log.e(TAG, "start_delay_data:" + start_delay_data + " -f1_delay_data:" + f1_delay_data + "-f2_delay_data:" + f2_delay_data);
            Log.e(TAG, "1级监听-id:" + id + " -paiChoice:" + paiChoice + "-kongChoice:" + kongChoice);

            return false;
        });
        //二级点击监听
        zcList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                //如果你处理了并且消费了点击返回true
                kongChoice = childPosition + 1;
                childListChoice = childPosition + 1;
                DenatorBaseinfo info = childList.get(groupPosition).get(childPosition);
                zhuceAdapter.setSelcetPosition(groupPosition, childPosition);
                Log.e(TAG, "2级监听--点击position: " + childPosition + "----info.排号:" + info.getPai() + "-" + info.getBlastserial() + "-" + info.getDuanNo());
                Log.e(TAG, "2级监听--groupListChoice: " + groupListChoice);
                Log.e(TAG, "2级监听--childListChoice: " + childListChoice);
                // 序号 延时 管壳码
//            modifyBlastBaseInfo(no, delay, shellBlastNo);
//                modifyBlastBaseInfo(info, groupPosition,childPosition);//序号,孔号,延时,管壳码
                return false;
            }
        });

    }

    private void deleteLG(ExpandableListView parent, int position, long id) {
        // 确定是否长按的是组项或子项
        boolean isGroup = false;
        if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            isGroup = true;
        }

        long packedPos = parent.getExpandableListPosition(position);
        Log.e(TAG, "长按事件 父位置: packedPos=" + packedPos);
        // 获取对应的组或子项位置
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
        int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
        // 删除数据
        if (isGroup) {
            // 删除组
//                    groupList.remove(groupPosition);
//                    childList.remove(groupPosition);
            zhuceAdapter.removeGroup(groupPosition);
        } else {
            // 删除子项
//                    childList.get(groupPosition).remove(childPosition-1) ;
            zhuceAdapter.removeChild(groupPosition, childPosition);
        }
    }

    //子点击时间
    @Override
    public void onChildButtonClick(int groupPosition, int childPosition) {
        // 处理按钮点击事件
        String group = groupList.get(groupPosition).toString();
        DenatorBaseinfo child = childList.get(groupPosition).get(childPosition);
        Log.e(TAG, "子控件groupPosition: " + groupPosition + "--childPosition" + childPosition);
        Log.e(TAG, "group: " + group);
        Log.e(TAG, "child: " + child);
        modifyBlastBaseInfo(child.getBlastserial(), child.getDelay(), child.getShellBlastNo(), child.getDenatorId(), child.getDuan(), child.getDuanNo(), child);
    }

    //父点击时间
    @Override
    public void OngroupButtonClickListener(View view, int groupPosition) {
        switch (view.getId()) {
            case R.id.im_xiugai1:
                // 处理按钮点击事件
                String group = groupList.get(groupPosition).toString();
                groupListChoice = groupPosition + 1;
                paiChoice = groupList.get(groupListChoice - 1).getPaiId();
                Log.e(TAG, "处理按钮点击事件---父控件groupPosition: " + groupPosition);
                Log.e(TAG, "处理按钮点击事件---group: " + group);
                Log.e(TAG, "处理按钮点击事件---paiChoice: " + paiChoice);
                XiuGaiPai(groupList.get(groupPosition));
                break;
            case R.id.pai_check:

                break;
        }


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
        switch (Build.DEVICE) {
            // KT50 起爆器设备
            case "T-QBZD-Z6":
            case "M900": {
                // 创建扫描头操作对象，并注册回调
                mScaner = new ScanQrControl(this);
                mScaner.registerScanCb(new ScanQrControl.IScan() {
                    @Override
                    public void onScanStart(int timeoutSec) {
                    }

                    @Override
                    public void onScanResult(boolean isSuccess, String scanResultStr) {
                        saoma(scanResultStr);
                    }
                });
                break;
            }
            default: {
                scanDecode = new ScanDecode(this);
                scanDecode.initService("true");//初始化扫描服务
                scanDecode.getBarCode(data -> {
                    saoma(data);
                });
            }

        }
    }

    private void saoma(String data) {
        if (checkDelay()) return;
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
        if (data.length() == 19) {//扫描盒号
            addHeHao(data);
        }
        if (data.length() == 18) {//扫描箱号
            addXiangHao(data);
        }
        if (sanButtonFlag > 0) {//扫码结果设置到输入框里
            Log.e("扫码注册", "data: " + data);
            decodeBar(data);
            Message msg = new Message();
            msg.obj = data;
            msg.what = 9;
            mHandler_tip.sendMessage(msg);
//            scanDecode.stopScan();
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
                if (data.charAt(0) == 'Y') {
                    barCode = data.substring(1, 14);
                    String a = data.substring(14, 24);
                    denatorId = a.substring(0, 2) + "2" + a.substring(2, 4) + "00" + a.substring(4);
                    String yscs = data.substring(24);
                    Log.e("四川扫码", "barCode: " + barCode);
                    Log.e("四川扫码", "denatorId: " + denatorId);
                    Log.e("四川扫码", "yscs: " + yscs);
                    insertSingleDenator_2(barCode, denatorId, yscs, "1", "0");//因为四川二维码不带段位和版本号,所以写个固定的
                } else {//其他规则
                    //内蒙版
                    barCode = data.substring(0, 13);
                    denatorId = "A621" + data.substring(13, 22);
                    String yscs = data.substring(22, 26);
                    String version = data.substring(26, 27);
                    String duan = data.substring(27, 28);

                    insertSingleDenator_2(barCode, denatorId, yscs, version, duan);//同时注册管壳码和芯片码
                }

            } else if (data.length() == 13) {
                flag_saoma=true;
                barCode = getContinueScanBlastNo(data);//VR:1;SC:5600508H09974;
                Log.e(TAG, "barCode: " + barCode);
//                insertSingleDenator(barCode);
                insertSingleDenator_2(barCode, "", "", "1", "0");//同时注册管壳码和芯片码
            } else if (data.length() == 14) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(12));
            }
            hideInputKeyboard();//隐藏光标
        }
    }

    private void handler() {
        mHandler_0 = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1001:
                    Log.e("liyi_1001", "更新视图 pai:" + paiChoice);
                    Log.e("liyi_1001", "更新视图 groupListChoice:" + groupListChoice);
                    Log.e("liyi_1001", "更新视图 区域:" + mRegion);
                    Log.e("liyi_1001", "更新视图 雷管数量: " + mListData.size());

                    updataTitle();

                    paiMax = new GreenDaoMaster().getMaxPaiId(mRegion);
                    paiMin = new GreenDaoMaster().getMinPaiId(mRegion);
                    groupList.clear();
                    childList.clear();
                    GreenDaoMaster master = new GreenDaoMaster();
                    groupList = master.queryPaiSelect(mRegion);
                    Log.e(TAG, "刷新适配器--groupList.size(): " + groupList.size());
                    Log.e(TAG, "刷新适配器--paiMax: " + paiMax);
                    for (int i = 0; i < groupList.size(); i++) {
                        List<DenatorBaseinfoSelect> list_pai = master.queryDetonatorPaiSelect(mRegion, groupList.get(i).getPaiId());
                        childList.add(list_pai);
                    }
                    zhuceAdapter = new ZhuCeScanAdapter(groupList, childList, quYu_choice.getQyid() + "", this, this);
                    Log.e(TAG, "是否显示选中框check_gone: " + check_gone);
                    //显示checkbox
                    if (check_gone) {
                        zhuceAdapter.setCheckBox(false);
                        lay_bottom.setVisibility(View.VISIBLE);
                    } else {
                        zhuceAdapter.setCheckBox(true);
                        lay_bottom.setVisibility(View.GONE);
                    }
                    zhuceAdapter.setUid(switchUid);//是否显示UID
                    zcList.setAdapter(zhuceAdapter);

                    int groupCount = zcList.getCount();


                    Log.e(TAG, "paiChoice: " + paiChoice);
                    Log.e(TAG, "kongChoice: " + kongChoice);
                    Log.e(TAG, "groupListChoice: " + groupListChoice);
                    Log.e(TAG, "childListChoice: " + childListChoice);
                    if (paiMax != 0) {
                        Log.e("handler1001", "groupCount:" + groupCount +
                                "--groupListChoice:" + groupListChoice + "--paiMax:" + paiMax);
                        if (groupListChoice == 0) {//初始化的时候,默认展开后一排,选中最后一发管
                            groupListChoice = groupCount;
                            paiChoice = groupList.get(groupListChoice - 1).getPaiId();
                        }
                        if (childListChoice == 0) {//初始化的时候,默认展开后一排,选中最后一发管
                            childListChoice = master.queryDetonatorPai(mRegion, paiChoice).size();
                            kongChoice = childListChoice;//??先这么写看看
                        }
                        Log.e(TAG, "光标移动-flag_zhuce: " + flag_zhuce);
                        //注册新雷管,光标挪到最后一位
                        if (flag_zhuce) {
                            List<DenatorBaseinfo> list = master.queryDetonatorPaiDesc(mRegion, paiChoice);

                            for (int a = 0; a < childList.get(groupListChoice - 1).size(); a++) {//因为有同孔的存在,孔号不能代表在列表中的位置
                                if (list.get(0).getId().equals(childList.get(groupListChoice - 1).get(a).getId())) {
                                    kongChoice = a + 1;
                                    childListChoice = kongChoice;
                                }
                            }
//                            kongChoice = list.size();//插入光标不对
//                            kongChoice = list.get(0).getBlastserial();//同孔光标不对
                            Log.e(TAG, "新注册,光标移动到kongChoice: " + kongChoice);
                            Log.e(TAG, "新注册,光标移动到childListChoice: " + childListChoice);
                            flag_zhuce = false;
                        }
                        Log.e(TAG, "设置默认选中-childListChoice0: " + childListChoice);
                        //除了在最新的管后面加孔,光标放到新注册的这发
                        if (!flag_add && childListChoice + 1 < childList.get(groupListChoice - 1).size()) {
                            flag_add = true;
                            childListChoice = childListChoice + 1;
                        }
                        Log.e(TAG, "设置默认选中-groupListChoice: " + groupListChoice);
                        Log.e(TAG, "设置默认选中-childListChoice: " + childListChoice);
                        zhuceAdapter.setSelcetPosition(groupListChoice - 1, childListChoice - 1);
                        //默认展开
                        if (groupListChoice >= groupCount) {
                            Log.e(TAG, "光标选中1: ");
                            zcList.expandGroup(zhuceAdapter.getGroupCount() - 1);
                            if (childList.get(zhuceAdapter.getGroupCount() - 1).size() != 0) {
                                zhuceAdapter.setSelcetPosition(zhuceAdapter.getGroupCount() - 1, childListChoice - 1);
                                zcList.setSelectedChild(zhuceAdapter.getGroupCount() - 1, childListChoice - 1, true);
                            }
                        } else {
                            Log.e(TAG, "光标选中2: ");
                            zcList.expandGroup(groupListChoice - 1);
                            if (childList.get(groupListChoice - 1).size() != 0) {
                                zcList.setSelectedChild(groupListChoice - 1, childListChoice - 1, true);
                            }
                        }
                    }

                    //根据选择的排确定延时的值
                    choicepaiData = GreenDaoMaster.gePaiData(mRegion, paiChoice + "");
                    //如果有默认排的话,就默认该排的延时
                    if (choicepaiData != null) {
                        start_delay_data = choicepaiData.getStartDelay();
                        f1_delay_data = choicepaiData.getKongDelay();
                        f2_delay_data = choicepaiData.getNeiDelay();
                        flag_jh_f1 = !choicepaiData.getDiJian();
                        kongSum = choicepaiData.getKongNum();
                        Log.e(TAG, "根据选中排更新是否递减  flag_jh_f1: " + flag_jh_f1);
                        Log.e(TAG, "根据选中排更新同孔数量  kongNum: " + kongSum);
                    }

                    //重置同孔标志
                    flag_t1 = true;

                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());
//                    showDuanSum(duan_new);
                    resetView_start();
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

                // 删除后,光标不动,同时更新视图
                case 1003:
                    updataTitle();
                    //删除后更新选中孔号
                    int total = new GreenDaoMaster().queryDetonatorPai(mRegion, paiChoice).size();
                    if (kongChoice > total) {
                        kongChoice = total;
                    }

                    GreenDaoMaster master2 = new GreenDaoMaster();
                    int lg_total = master2.queryDetonatorPai(mRegion, paiChoice).size();
                    Log.e("liyi_1003", "lg_total:" + (lg_total));//list里面需要减1,查询不用减1
                    Log.e("liyi_1003", "childListChoice:" + (childListChoice));//list里面需要减1,查询不用减1
                    if (childListChoice > lg_total) {//如果删除最后一发雷管,就把光标上移
                        childListChoice = lg_total;
                        Log.e("liyi_1003", "childListChoice:" + (childListChoice));//list里面需要减1,查询不用减1
                    }

                    Log.e("liyi_1003", "删除后,更新视图 pai:" + (paiChoice));//list里面需要减1,查询不用减1
                    Log.e("liyi_1003", "删除后,更新视图 childListChoice-1:" + (childListChoice - 1));
                    Log.e("liyi_1003", "删除后,更新视图 区域:" + mRegion);
                    Log.e("liyi_1003", "删除后,更新视图 雷管数量: " + mListData.size());
                    Log.e(TAG, "groupListChoice:" + groupListChoice);
                    //根据选择的排确定延时的值
                    choicepaiData = GreenDaoMaster.gePaiData(mRegion, (paiChoice) + "");
                    //如果有默认排的话,就默认该排的延时
                    if (choicepaiData != null) {
                        start_delay_data = choicepaiData.getStartDelay();
                        f1_delay_data = choicepaiData.getKongDelay();
                        f2_delay_data = choicepaiData.getNeiDelay();
                        flag_jh_f1 = !choicepaiData.getDiJian();
                        kongSum = choicepaiData.getKongNum();
                        Log.e(TAG, "根据选中排更新是否递减  choicepaiData.getDiJian(): " + choicepaiData.getDiJian());
                        Log.e(TAG, "根据选中排更新是否递减  flag_jh_f1: " + flag_jh_f1);
                        Log.e(TAG, "根据选中排更新同孔数量  kongNum: " + kongSum);
                    }


                    paiMax = new GreenDaoMaster().getMaxPaiId(mRegion);
                    paiMin = new GreenDaoMaster().getMinPaiId(mRegion);
                    groupList.clear();
                    childList.clear();

                    groupList = new GreenDaoMaster().queryPaiSelect(mRegion);
                    Log.e("liyi_1003", "刷新适配器--groupList.size(): " + groupList.size());
                    Log.e("liyi_1003", "刷新适配器--paiMax: " + paiMax);
                    for (int i = 0; i < groupList.size(); i++) {
                        List<DenatorBaseinfoSelect> list_pai = new GreenDaoMaster().queryDetonatorPaiSelect(mRegion, groupList.get(i).getPaiId());
                        childList.add(list_pai);
                    }
                    zhuceAdapter = new ZhuCeScanAdapter(groupList, childList, quYu_choice.getQyid() + "", this, this);

                    //显示checkbox
                    if (check_gone) {
                        zhuceAdapter.setCheckBox(false);
                        lay_bottom.setVisibility(View.VISIBLE);
                    } else {
                        zhuceAdapter.setCheckBox(true);
                        lay_bottom.setVisibility(View.GONE);
                    }
                    zcList.setAdapter(zhuceAdapter);

//                    kongChoice = new GreenDaoMaster().queryDetonatorPai(paiChoice).size();
//                    paiChoice = zcList.getCount();
                    int groupCount2 = zcList.getCount();
                    Log.e(TAG, "handler1003-paiMax: "+paiMax );
                    if (paiMax != 0) {
                        Log.e("handler1003", "groupCount:" + groupCount2 +
                                "--groupListChoice:" + groupListChoice + "--paiMax:" + paiMax);
                        if (groupListChoice == 0) {//初始化的时候,默认展开后一排,选中最后一发管
                            groupListChoice = groupCount2;
                            paiChoice = groupList.get(groupListChoice - 1).getPaiId();
                        }
                        zhuceAdapter.setSelcetPosition(groupListChoice - 1, childListChoice - 1);
                        //默认展开
                        if (delete_pai) {//进行了删排操作
                            delete_pai=false;
                            childListChoice=childList.get(zhuceAdapter.getGroupCount() - 1).size();
                            groupListChoice=zhuceAdapter.getGroupCount();
                            paiChoice = groupList.get(groupListChoice - 1).getPaiId();
                            zcList.expandGroup(zhuceAdapter.getGroupCount() - 1);
                            Log.e("adapter删除操作", "groupSize:" + zhuceAdapter.getGroupCount() + "--childSize:" + childList.get(zhuceAdapter.getGroupCount() - 1).size());
//                            if (childList.get(zhuceAdapter.getGroupCount() - 1).size() != 0) {
                                Log.e(TAG, "光标选中3: "+"groupListChoice:" + (groupListChoice)+" childListChoice:"+(childListChoice));
                                zhuceAdapter.setSelcetPosition(groupListChoice - 1, childListChoice - 1);
                                zcList.setSelectedChild(groupListChoice - 1, childListChoice - 1, true);
//                            }
                        } else {
                            zcList.expandGroup(groupListChoice - 1);
                            if (childList.get(groupListChoice - 1).size() != 0) {
                                zcList.setSelectedChild(groupListChoice - 1, childListChoice - 1, true);
                            }
                        }
                    }



                    //重置同孔标志
                    flag_t1 = true;
                    break;
                case 1005://按管壳码排序
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    Collections.sort(mListData);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 1006:
                    MmkvUtils.savecode("duan", 1);
                    duan_new = 1;
                    btnAddDelay.setText(getResources().getString(R.string.text_reister_dw) + duan_new);
                    break;
                case 1007:
                    for (int a = 0; a < groupList.size(); a++) {
                        int paiId = groupList.get(a).getPaiId();
                        GreenDaoMaster master3 = new GreenDaoMaster();
                        int total2 = master3.queryDetonatorPaiSize(mRegion, paiId + "");//有过
                        int delay_max_new = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiId);//获取该区域 最大序号的延时
                        int delay_minNum_new = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, paiId);

                        Log.e(TAG, "更新排数据 updataPaiData  paiChoice: " + paiId);
                        Log.e(TAG, "更新排数据 updataPaiData  total: " + total2);
                        Log.e(TAG, "更新排数据 updataPaiData  delay_max_new: " + delay_max_new);
                        Log.e(TAG, "更新排数据 updataPaiData  delay_minNum_new: " + delay_minNum_new);
                        choicepaiData = GreenDaoMaster.gePaiData(mRegion, paiId + "");
                        if (choicepaiData != null) {
                            choicepaiData.setSum(total2 + "");//
                            choicepaiData.setDelayMin(delay_minNum_new + "");
                            choicepaiData.setDelayMax(delay_max_new + "");

                            getDaoSession().getPaiDataDao().update(choicepaiData);
                        }
                    }


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
//                show_Toast(getString(R.string.text_error_tip69) + lg_Piece + getString(R.string.text_error_tip70) + lg_No + getString(R.string.text_error_tip72) + singleShellNo + getString(R.string.text_error_tip71));
                show_Toast_long(getString(R.string.text_error_tip69) + lg_Piece + getString(R.string.text_error_tip70) + lg_pai + "-" + lg_No + "-" + lg_wei + " " + singleShellNo + getString(R.string.text_error_tip71));

                int total = showDenatorSum();
//                reisterListView.setSelection(total - Integer.parseInt(lg_No));
//                MoveToPosition(linearLayoutManager, mListView, total - Integer.parseInt(lg_No));
            } else if (msg.what == 6) {
                SoundPlayUtils.play(4);
                show_Toast(getString(R.string.text_line_tip7));
            } else if (msg.what == 7) {
                SoundPlayUtils.play(4);
                show_Toast_long(getString(R.string.text_error_tip69) + lg_Piece + getString(R.string.text_error_tip70) + lg_pai + "-" + lg_No + "-" + lg_wei + " " + singleShellNo + getString(R.string.text_error_tip71));
            } else if (msg.what == 8) {
                SoundPlayUtils.play(4);
//                show_Toast(getString(R.string.text_error_tip64));
                show_Toast(getString(R.string.text_line_tip19));
            } else if (msg.what == 9) {
                decodeBar(msg.obj.toString());
            } else if (msg.what == 10) {
                show_Toast(getString(R.string.text_line_tip8));
            } else if (msg.what == 11) {
                show_Toast(getString(R.string.text_error_tip66));
            } else if (msg.what == 12) {
                show_Toast(getString(R.string.text_mx_zcsb));
            } else if (msg.what == 13) {
                SoundPlayUtils.play(4);
                show_Toast("延时不能小于0ms");
            } else if (msg.what == 20) {
                SoundPlayUtils.play(4);
                show_Toast("共有" + xiangHao_errNum + "盒重复");
                xiangHao_errNum = 0;
            } else if (msg.what == 2001) {
                show_Toast(msg.obj.toString());
                SoundPlayUtils.play(4);
            } else {
                SoundPlayUtils.play(4);
                show_Toast(getString(R.string.text_line_tip9));
            }
            return false;
        });
        mHandler_1 = new Handler(msg -> {
            if (tipInfoFlag == 1) {
                if (busInfo != null) {
                    txtCurrentVolt.setText(getResources().getString(R.string.text_reister_vol) + busInfo.getBusVoltage() + "V");
                    BigDecimal b = BigDecimal.valueOf((busInfo.getBusCurrentIa() * 1.25 / 1.2));//处理大额数据专用类
                    float dianliu = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    txtCurrentIC.setText(getResources().getString(R.string.text_reister_ele) + dianliu + "μA");
                    if (Math.round(busInfo.getBusCurrentIa()) > 60) {//判断当前电流是否偏大
                        txtCurrentIC.setTextColor(Color.RED);
                    } else {
                        txtCurrentIC.setTextColor(Color.GREEN);
                    }
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
                show_Toast(getResources().getString(R.string.text_line_tip15));
                showDenatorSum();
            }
            return false;
        });

        mHandler_showNum = new Handler(msg -> {
            int pos = msg.arg1;//段号
            Log.e("更新段雷管数量", "pos: " + pos);
//            showDuanSum(pos);
            return false;
        });
    }

    private void updataTitle() {
        int total = new GreenDaoMaster().queryDetonatorSize(quYu_choice.getQyid() + "");
//        int maxPai = new GreenDaoMaster().getPieceMaxPai(quYu_choice.getQyid() + "");
        int paisum = new GreenDaoMaster().getPaisum(mRegion + "");
        int max = new GreenDaoMaster().getPieceMaxNumDelay(quYu_choice.getQyid() + "");
        int min = new GreenDaoMaster().getPieceMinNumDelay(quYu_choice.getQyid() + "");
        int kong = new GreenDaoMaster().querytotalKongNew(quYu_choice.getQyid() + "");
        qyNo.setText("区域:" + quYu_choice.getName());
        qyTxtMaxDealy.setText("最大延时:" + max);
        qyTxtMinDealy.setText("最小延时:" + min);
        qyTxtTotal.setText("共:" + total + "发");
        qyTxtTotalPai.setText("共:" + paisum + "排" + kong + "孔");
        Log.e(TAG, "------更新区域内容name: " + quYu_choice.getName());
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
                zhuceAdapter.setUid(false);
                text_uid.setTextColor(Color.GREEN);
                text_gkm.setTextColor(Color.BLACK);
            } else {
                a = 4;
                switchUid = true;
                zhuceAdapter.setUid(true);
                text_uid.setTextColor(Color.BLACK);
                text_gkm.setTextColor(Color.GREEN);
            }
            mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
            //切换UID后再设置一下长按方法
//            mAdapter = new DetonatorAdapter_Paper<>(ReisterMainPage_scan.this, a);
//            mListView.setLayoutManager(linearLayoutManager);
//            mListView.setAdapter(mAdapter);

//            mAdapter.setOnItemLongClick(position -> {
//                DenatorBaseinfo info = mListData.get(position);
//                int no = info.getBlastserial();
//                int delay = info.getDelay();
//                String shellBlastNo = info.getShellBlastNo();
//                String denatorId = info.getDenatorId();
//                int duan = info.getDuan();
//                int duanNo = info.getDuanNo();
//                // 序号 延时 管壳码
//                modifyBlastBaseInfo(no, delay, shellBlastNo, denatorId, duan, duanNo, info);
//            });
        });


    }

    private long mExitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("点击按键", "keyCode: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_THUMBS_DOWN || keyCode == KeyEvent.KEYCODE_PROFILE_SWITCH || keyCode == 289) {//287
            if (Build.DEVICE.equals("M900") || Build.DEVICE.equals("T-QBZD-Z6")) {
                mScaner.startScan();
            }

            return true;
        }
        if (keyCode == event.KEYCODE_BACK) {
            clearText();
            if (llStart.getVisibility() == View.VISIBLE) {
                cd_title.setVisibility(View.VISIBLE);
                llEnd.setVisibility(View.GONE);
                llStart.setVisibility(View.GONE);
                llNum.setVisibility(View.GONE);
                btnInputOk.setVisibility(View.GONE);
                llSingle.setVisibility(View.GONE);
                btnReturn.setVisibility(View.GONE);
                btnScanReister.setVisibility(View.VISIBLE);
//                    lySetDelay.setVisibility(View.VISIBLE);
                btnInput.setText(getResources().getString(R.string.text_scan_sdsr));
                return true;
            } else if (!isSingleReisher) {

                lyXinxi.setVisibility(View.GONE);
                btnInputOk.setEnabled(true);
                txtCurrentIC.setTextColor(Color.BLACK);
                isSingleReisher = true;
                // 13 退出注册模式
                sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));
                return true;
            } else {
                finish();
                return true;
            }


        }
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
                .setTitle(getResources().getString(R.string.scan_text_tip1))//设置对话框的标题//"成功起爆"
                .setMessage(getResources().getString(R.string.scan_text_tip2))//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                .setNegativeButton(getResources().getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.text_line_qrqk), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null);
//                        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
                        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

                        dialog.dismiss();
                        Utils.saveFile();//把软存中的数据存入磁盘中
                    }
                }).create();
        dialog.show();
    }

    /**
     * 扫描盒号
     */
    private void addHeHao(String data) {
        char[] xh = data.toCharArray();
        char[] strNo1 = {xh[1], xh[2], xh[9], xh[10], xh[11], xh[12], xh[13], xh[14]};//箱号数组
        final String strNo = "00";
        String a = xh[5] + "" + xh[6];
        String endNo = Utils.HeHao(a);
        final String prex = String.valueOf(strNo1);
        final int finalEndNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + xh[17] + endNo);
        final int finalStrNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + xh[17] + strNo);
        if (factoryCode != null && factoryCode.trim().length() > 0 && !factoryCode.contains(prex.substring(0, 2))) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
            return;
        }
        new Thread(() -> {
            insertDenator(prex, finalStrNo, finalEndNo, true);//添加
        }).start();
    }

    /**
     * 扫描箱号
     */
    private void addXiangHao(String data) {
        //J 5 3 z c 1 0 S 1 9 0 4 1 5 1 0 1
//        （1）J代表产品名称，就是电子毫秒电雷管（也就是我们平常的工业电子雷管）；
//        （2）53代表企业代号，金建华公司代号；
//        （3）z代表是段别，电子雷管均采用其它段z表示；
//        （4）c代表管壳材料为钢质；
//        （5）10代表箱内盒数，金建华都是每箱10盒。
//        （6）S代表箱代码，对应上述表1序号5的箱代码；
//        （7）190415代表生产日期2019年4月15日；
//        （8）1代表特征号，理解成机台号，1号机；01代表箱号，只能01-99。
        char[] xh = data.toCharArray();
        //                  5      3      9       0       4       1       5       1
        char[] strNo1 = {xh[1], xh[2], xh[9], xh[10], xh[11], xh[12], xh[13], xh[14]};//箱号数组
        final String strNo = "00";
        //                            1            0
        int a = Integer.parseInt(xh[5] + "" + xh[6]);//代表几盒 10
        //                                 S
        int endNo = Utils.XiangHao(xh[7] + "");//判断每盒几发   8
        final String prex = String.valueOf(strNo1);
        //5630921A
        //53904151
        //01
//        final int finalEndNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + xh[17] + endNo);
//        final int finalStrNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + strNo);//01
        if (factoryCode != null && factoryCode.trim().length() > 0 && !factoryCode.contains(prex.substring(0, 2))) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
            return;
        }
        new Thread(() -> {
            for (int b = 0; b < a; b++) {
                String xuhao = xh[15] + "" + xh[16] + b + "00";
                Log.e(TAG, "第" + b + "盒序号: " + xuhao);
                int finalStrNo = Integer.parseInt(xuhao);
                insertDenator(prex, finalStrNo, finalStrNo + (endNo - 1), false);//添加
            }

        }).start();
        if (xiangHao_errNum != 0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(20));

        }
    }

    private void init() {
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
//         获取 区域参数
//        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        // 原标题
        mOldTitle = getSupportActionBar().getTitle().toString();
        // 设置标题区域
        setTitleRegion(mRegion, -1);
        titleBack.setVisibility(View.GONE);
        title_lefttext.setVisibility(View.VISIBLE);
        titleRight2.setVisibility(View.VISIBLE);
        titleRight2.setText(getResources().getString(R.string.text_gdcz));
        titleAdd.setVisibility(View.GONE);
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
            modifyBlastBaseInfo(no, delay, shellBlastNo, denatorId, duan, duanNo, info);
        });
        this.isSingleReisher = true;

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

    private void clearText() {
        if (TextUtils.isEmpty(factoryCode)) {
            //厂家
            edit_start_entBF2Bit_st.getText().clear();
            edit_start_entBF2Bit_st.setBackgroundResource(R.drawable.translucent);
            edit_end_entBF2Bit_en.getText().clear();
            edit_end_entBF2Bit_en.setBackgroundResource(R.drawable.translucent);
            editScanChangjia.getText().clear();
            editScanChangjia.setBackgroundResource(R.drawable.translucent);
            //特征号
            edit_start_entAT1Bit_st.getText().clear();
            edit_end_entAT1Bit_ed.getText().clear();
            editScanTezheng.getText().clear();
            edit_start_entAT1Bit_st.setBackgroundResource(R.drawable.translucent);
            edit_end_entAT1Bit_ed.setBackgroundResource(R.drawable.translucent);
            editScanTezheng.setBackgroundResource(R.drawable.translucent);
        }
        //日期
        edit_start_entproduceDate_st.getText().clear();
        edit_end_entproduceDate_ed.getText().clear();
        editScanRiqi.getText().clear();
        edit_start_entproduceDate_st.setBackgroundResource(R.drawable.translucent);
        edit_end_entproduceDate_ed.setBackgroundResource(R.drawable.translucent);
        editScanRiqi.setBackgroundResource(R.drawable.translucent);
        //流水号
        edit_start_entboxNoAndSerial_st.getText().clear();
        edit_start_entboxNoAndSerial_st.getText().clear();
        editScanHehao.getText().clear();
        editScanLiushui.getText().clear();
        edit_start_entboxNoAndSerial_st.setBackgroundResource(R.drawable.translucent);
        edit_start_entboxNoAndSerial_st.setBackgroundResource(R.drawable.translucent);
        editScanHehao.setBackgroundResource(R.drawable.translucent);
        editScanLiushui.setBackgroundResource(R.drawable.translucent);
        //连续注册个数
        etNum.getText().clear();
    }

    /**
     * 停止扫码
     */
    private void stopScan() {
        continueScanFlag = 0;
        btnScanReister.setText(getResources().getString(R.string.text_reister_scanReister));//"扫码注册"
        btnScanReister.setTextSize(23);
        btnSetdelay.setEnabled(true);
        btnInput.setEnabled(true);
        switch (Build.DEVICE) {
            case "T-QBZD-Z6":
            case "M900": {
                //M900关闭扫码
                mScaner.stopScan();
                break;
            }
            case "ST327":
            case "S337": {
                //st327扫码下电
                powerOffScanDevice(PIN_TRACKER_EN);//扫码头下电
                break;
            }
            default: {
                //kt50停止扫码头方法
                scanDecode.stopScan();//停止扫描
            }
        }
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
        txtReisteramount.setText(getString(R.string.text_reister_tip1) + list.size());
        return list.size();
    }

    private void runPbDialog() {
        pb_show = 1;
        tipDlg = new LoadingDialog(ReisterMainPage_scan.this);
//        Context context = tipDlg.getContext();
//        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
//        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
        tipDlg.setMessage(getResources().getString(R.string.text_loading)).show();

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
                Log.e("发送命令", str);
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
        duan_new = (int) MmkvUtils.getcode("duan", 1);
        Log.e(TAG, "返回当前页面-更新段位-duan_new: " + duan_new);
        btnAddDelay.setText(getResources().getString(R.string.text_reister_dw) + duan_new);
    }

    @Override
    protected void onResume() {
        //         获取 区域参数
//        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        flag_zhuce = true;//光标挪动到最后一位
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1007));
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (db != null) {
            db.close();
        }
        if (tipDlg != null) {
            tipDlg.dismiss();
            tipDlg = null;
        }
//        Utils.saveFile();//把软存中的数据存入磁盘中
        if (mScaner != null) {
            mScaner.unregisterScanCb();
        }
        if (scanDecode != null) {
            scanDecode.stopScan();//停止扫描
            scanDecode.onDestroy();//回复初始状态
        }
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication.closeSerialPort();
                Log.e("ReisterMainPage_scan", "调用mApplication.closeSerialPort()开始关闭串口了。。");
                mSerialPort = null;
            }
        }).start();
        Log.e("延时长度", "f1_delay_data.length(): " + f1_delay_data.length());
        if (f1_delay_data.length() > 0) {
            MmkvUtils.savecode("f1", f1_delay_data.toString());
        }
        if (f2_delay_data.length() > 0) {
            MmkvUtils.savecode("f2", f1_delay_data.toString());
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
        String addNum = etNum.getText().toString();

        if (StringUtils.isBlank(st2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip11);//"起始厂家码不能为空"
            return tipStr;
        }
        if (StringUtils.isBlank(stsno)) {
            tipStr = getResources().getString(R.string.text_scan_liushuihaocuowu);//"起始厂家码不能为空"
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
            tipStr = getResources().getString(R.string.text_scan_cuowu1);//  "结束序列号不能为空";
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
            tipStr = getResources().getString(R.string.text_scan_riqicuowu);
            return tipStr;
        }

        return tipStr;
    }

    private int check(String shellNo) {

//        if (reEtF1.getText().length() < 1 || reEtF2.getText().length() < 1 || et_startDelay.getText().length() < 1) {
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
//            return -1;
//        }
        if (shellNo.length() == 13) {
            //管厂码
            String facCode = Utils.getDetonatorShellToFactoryCodeStr(shellNo);
            //特征码
            String facFea = Utils.getDetonatorShellToFeatureStr(shellNo);
            //雷管信息有误，管厂码不正确，请检查
            if (factoryCode != null && factoryCode.trim().length() > 0 && factoryCode.indexOf(facCode) < 0) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
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
        }

        if (shellNo.length() > 13) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(6));
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
                Temp = getResources().getString(R.string.text_tip_delete);
                String whereClause = "id=?";
                String[] whereArgs = {String.valueOf(id)};
                db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, whereClause, whereArgs);
                Utils.saveFile();//把软存中的数据存入磁盘中
//                getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                break;
            case 2:
                this.modifyBlastBaseInfo(id);
                Temp = getResources().getString(R.string.text_tip_modify);
                break;
            default:
                break;
        }
        show_Toast(Temp + getResources().getString(R.string.text_tip_handle));
        return super.onContextItemSelected(item);
    }

    /**
     * 修改雷管延期 弹窗
     */
    private void modifyBlastBaseInfo(int no, int delay, final String shellBlastNo, final String denatorId, final int duan, final int duanNo, DenatorBaseinfo info) {
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
        tv_duan.setText(info.getPai() + "-" +
                info.getSithole()
                + "-" + info.getDuanNo());
        et_duanNo.setText(duanNo + "");
        int d = getFan(info.getDuan());
        Log.e(TAG, "是否翻转: " + d);
        Log.e(TAG, "是否翻转: info.getFanzhuan()" + info.getFanzhuan());
//        builder.setNegativeButton(getResources().getString(R.string.text_crk), (dialog, which) -> {
//            if (info.getFanzhuan() != null && info.getFanzhuan().equals("0") || d == 1) {
//                show_Toast(getResources().getString(R.string.text_lgfz));
//            } else {
//                //插入方法
//                getSupportActionBar().setTitle(getResources().getString(R.string.text_zzcrk));
//                GreenDaoMaster master = new GreenDaoMaster();
//                db_charu = master.querylgMaxduanNo(info.getDuanNo(), info.getDuan(), mRegion);
//                Log.e(TAG, "选中插入的雷管: " + info.getShellBlastNo() + " 延时:" + info.getDelay());
//                Log.e(TAG, "选中插入的雷管: " + db_charu.getShellBlastNo() + " 延时:" + db_charu.getDelay());
//                charu = true;
//            }
//
//        });
        builder.setNeutralButton(getResources().getString(R.string.text_tip_delete), (dialog, which) -> {
            dialog.dismiss();
            AlertDialog dialog2 = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.text_queryHis_dialog1))//设置对话框的标题//"成功起爆"
                    .setMessage(getResources().getString(R.string.text_queryHis_dialog2))//设置对话框的内容"本次任务成功起爆！"
                    //设置对话框的按钮
                    .setNeutralButton(getResources().getString(R.string.text_alert_cancel), (dialog1, which1) -> dialog1.dismiss())
                    .setPositiveButton(getResources().getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (info.getFanzhuan() != null && info.getFanzhuan().equals("0") || d == 1) {
                                show_Toast(getResources().getString(R.string.text_queryHis_dialog4));
                            } else {
                                // TODO 开启进度条
                                runPbDialog();
                                new Thread(() -> {
                                    if (info.getShellBlastNo().length() != 13) {
                                        new GreenDaoMaster().deleteDetonator(info.getId());
                                    } else {
                                        new GreenDaoMaster().deleteDetonator(info.getShellBlastNo());
                                    }
                                    //先更新排数据,再删除排
                                    updataPaiData();
//                                    // 删除某一发雷管
//                                    int duan_guan = new GreenDaoMaster().getDuan(shellBlastNo);
//                                    new GreenDaoMaster().deleteDetonator(shellBlastNo);
//                                    Utils.writeRecord("--删除雷管:" + shellBlastNo);
//                                    AppLogUtils.writeAppLog("--删除雷管:" + shellBlastNo);
////                                    Utils.deleteData(mRegion, info.getDuan());//重新排序雷管
//                                    //更新每段雷管数量
//                                    Message msg = new Message();
//                                    msg.arg1 = duan_guan;
//                                    mHandler_showNum.sendMessage(msg);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            show_Toast(getResources().getString(R.string.text_del_ok));
                                        }
                                    });
                                    // 区域 更新视图
                                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1003));//删除后更新视图
                                    pb_show = 0;
                                }).start();
                            }
                        }
                    }).create();
            dialog2.show();
        });
        builder.setPositiveButton(getResources().getString(R.string.text_alert_sure), (dialog, which) -> {
//            int a = new GreenDaoMaster().querylgNum(info.getDuanNo(), info.getDuan(), mRegion);
//            Log.e(TAG, "a: "+a );
//            if (a > 1) {
//                show_Toast(getResources().getString(R.string.text_queryHis_dialog6));
//                return;
//            }
//            if (info.getFanzhuan() != null && info.getFanzhuan().equals("0") || d == 1) {
//                show_Toast(getResources().getString(R.string.text_queryHis_dialog7));
//                return;
//            }
            String delay1 = et_delay.getText().toString();
            AppLogUtils.writeAppLog("-单发修改延时:" + "-管壳码:" + shellBlastNo + "-延时:" + delay1);
            Utils.writeRecord("-单发修改延时:" + "-管壳码:" + shellBlastNo + "-延时:" + delay1);
            Log.e("单发修改", "delay1: " + delay1);
            Log.e("单发修改", "maxSecond: " + maxSecond);
            if (delay1 == null || delay1.trim().length() < 1 || maxSecond > 0 && Integer.parseInt(delay1) > maxSecond) {
                show_Toast(getResources().getString(R.string.text_reister_tip8));

            } else if (maxSecond != 0 && Integer.parseInt(delay1) > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2001, getResources().getString(R.string.text_reister_tip5) + maxSecond + "ms"));

            } else {
                // 修改雷管延时
                new GreenDaoMaster().updateDetonatorDelay(info.getId(), shellBlastNo, Integer.parseInt(delay1), Integer.parseInt(et_duanNo.getText().toString()));
                //更新排数据
                updataPaiData();
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                if (shellBlastNo.length() == 0) {
                    show_Toast(shellBlastNo + getResources().getString(R.string.text_dialog_xgcg2));
                } else {
                    show_Toast(shellBlastNo + getResources().getString(R.string.text_dialog_xgcg));
                }


                Utils.saveFile();
            }
            dialog.dismiss();

        });


        builder.show();
    }

    private void modifyBlastBaseInfo(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReisterMainPage_scan.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getResources().getString(R.string.text_reister_tip6));
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
                show_Toast(getResources().getString(R.string.text_error_tip3) + a + getResources().getString(R.string.text_regist_tip7) + b);
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void XiuGaiPai(PaiData paiData) {
        myDialog = new MyAlertDialog(this).builder();
        int maxPai = new GreenDaoMaster().getMaxPaiId(mRegion);
        EditText kongSum = myDialog.getView().findViewById(R.id.txt_fa);
        EditText startDelay = myDialog.getView().findViewById(R.id.txt_startDelay);
        EditText kongDelay = myDialog.getView().findViewById(R.id.txt_kongDelay);
        EditText neiDelay = myDialog.getView().findViewById(R.id.txt_paiDelay);
        SwitchButton sw_dijian = myDialog.getView().findViewById(R.id.sw_dijian);
        int delay_max_new = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiMax);//获取该区域 最大序号的延时
        if (delay_max_new == 0) {
            startDelay.setText("0");
        } else {
            startDelay.setText((delay_max_new + Integer.parseInt(quYu_choice.getPaiDelay())) + "");
        }

        kongDelay.setText(quYu_choice.getKongDelay());
        kongSum.setText(paiData.getKongNum() + "");
        startDelay.setText(paiData.getStartDelay());
        kongDelay.setText(paiData.getKongDelay());
        neiDelay.setText(paiData.getNeiDelay());
        sw_dijian.setChecked(paiData.getDiJian());
        myDialog.setGone()
                .setTitle("修改" + paiData.getPaiId() + "排设置")
                .setStart()
                .setFa()
                .setDijian()
                .setpaiText("孔内延时")
                .setCancelable(false)
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                })
                .setPositiveButton("确定", v -> {


//                    Log.e("打印", "name: " + name.getText());
                    Log.e("更新排", "kongSun: " + kongSum.getText().toString());
                    Log.e("更新排", "startDelay: " + startDelay.getText().toString());
                    Log.e("更新排", "kongDelay: " + kongDelay.getText().toString());
                    Log.e("更新排", "neiDelay: " + neiDelay.getText().toString());
                    Log.e("更新排", "sw_dijian: " + sw_dijian.isChecked());
                    Log.e("更新排", "paiChoice: " + paiChoice);
//                    paiData.setPaiId((maxPai + 1));
                    paiData.setQyid(Integer.parseInt(mRegion));
                    paiData.setStartDelay(startDelay.getText().toString());
                    paiData.setKongNum(Integer.parseInt(kongSum.getText().toString()));
                    paiData.setKongDelay(kongDelay.getText().toString());
                    paiData.setNeiDelay(neiDelay.getText().toString());
                    paiData.setDiJian(sw_dijian.isChecked());
                    getDaoSession().getPaiDataDao().update(paiData);
                    //起始序号
                    int startNoStr = new GreenDaoMaster().getPieceAndPaiMinKong(mRegion, paiData.getPaiId());
                    //终点序号
//                    int endNoStr = new GreenDaoMaster().getPieceAndPaiMaxKong(mRegion, paiData.getPaiId());
                    int endNoStr = childList.get(groupListChoice - 1).size();
                    //孔内雷管数
                    String holeDeAmoStr = kongSum.getText().toString();
                    //开始延时
                    String startDelayStr = startDelay.getText().toString();
                    //孔内延时
                    String holeinDelayStr = neiDelay.getText().toString();
                    //孔间延时
                    String holeBetweentStr = kongDelay.getText().toString();
                    //递减,开始序号,结束序号,孔内雷管数,开始延时,孔内延时,孔间延时
                    Log.e(TAG, "是否递减: " + sw_dijian.isChecked());
                    setDalay(!sw_dijian.isChecked(), startNoStr, endNoStr, holeDeAmoStr, startDelayStr, holeinDelayStr, holeBetweentStr);

                    //更新排数据
                    updataPaiData();
                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));// 区域 更新视图
//                    mHandle.sendMessage(mHandle.obtainMessage(1));
//                    }

                }).show();

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
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);//fromCommad为返回的16进制命令
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                Log.e(TAG, "命令错误: " );
                return;
            } else {
                String cmd = DefCommand.getCmd(fromCommad);
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                    doWithReceivData(cmd, localBuf);//处理cmd命令
                }
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
        String fromCommad = Utils.bytesToHexFun(cmdBuf);

        if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//41开启总线电源指令
//            sendOpenThread.exit = true;
//            Log.e("是否检测桥丝", "qiaosi_set: " + qiaosi_set);
            if (qiaosi_set.equals("true")) {//10 进入自动注册模式(00不检测01检测)桥丝
                sendCmd(OneReisterCmd.setToXbCommon_Reister_Init12_2("00", "01"));
            } else {
                sendCmd(OneReisterCmd.setToXbCommon_Reister_Init12_2("00", "00"));
            }


        } else if (DefCommand.CMD_1_REISTER_1.equals(cmd)) {//10 进入自动注册模式
            //发送获取电源信息
            byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00");//40
            sendCmd(reCmd);

        } else if (DefCommand.CMD_1_REISTER_3.equals(cmd)) {//12 有雷管接入
            //C0001208 FF 00 B6E6FF00 41 A6 1503 C0  普通雷管
            //C000120C FF 00 B6E6FF00 41 A6 B6E6FF00 1503 C0
            //C000120A FF 00 67D0FA00 03 A6 1704 7F24 C0

//            zhuce_form = OneReisterCmd.decodeFromReceiveAutoDenatorCommand14("00", cmdBuf, qiaosi_set);//桥丝检测
            zhuce_form = OneReisterCmd.decode14_newXinPian("00", cmdBuf, qiaosi_set);//桥丝检测
            if (qiaosi_set.equals("true") && zhuce_form.getWire().equals("无")) {
                tipInfoFlag = 5;//提示类型桥丝不正常
                mHandler_1.sendMessage(mHandler_1.obtainMessage());
                String detonatorId = Utils.GetShellNoById_newXinPian(zhuce_form.getFacCode(), zhuce_form.getFeature(), zhuce_form.getDenaId());
                Utils.writeRecord("--单发注册--:管壳码:" + serchShellBlastNo(detonatorId) + " 芯片码:" + detonatorId + "该雷管桥丝异常");
                AppLogUtils.writeAppLog("--单发注册--:管壳码:" + serchShellBlastNo(detonatorId) + " 芯片码:" + detonatorId + "该雷管桥丝异常");
            }
            zhuce_Flag = 1;

            try {
                Thread.sleep(500);//
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //2  连续发三次询问电流指令
            byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00");//40获取电源信息
            sendCmd(reCmd);
        } else if (DefCommand.CMD_1_REISTER_4.equals(cmd)) {//13 退出自动注册模式
//            if (initCloseCmdReFlag == 1) {//打开电源
//                revCloseCmdReFlag = 1;
//                closeOpenThread.exit = true;
//                sendOpenThread = new SendOpenPower();
//                sendOpenThread.start();
//            }

        } else if (DefCommand.CMD_4_XBSTATUS_1.equals(cmd)) { //40 总线电流电压
            busInfo = FourStatusCmd.decodeFromReceiveDataPower24_1("00", cmdBuf);//解析 40指令
            tipInfoFlag = 1;
            mHandler_1.sendMessage(mHandler_1.obtainMessage());

            if (zhuce_Flag == 1) {//多次单发注册后闪退,busInfo.getBusCurrentIa()为空
                String detonatorId = Utils.GetShellNoById_newXinPian(zhuce_form.getFacCode(), zhuce_form.getFeature(), zhuce_form.getDenaId());
                if (busInfo.getBusCurrentIa() > 60) {//判断当前电流是否偏大//20221019(范总说取消掉单发注册电流判断)
                    tipInfoFlag = 7;
                    mHandler_1.sendMessage(mHandler_1.obtainMessage());
                    SoundPlayUtils.play(4);
                    zhuce_Flag = 0;
                    AppLogUtils.writeAppLog("--单发注册--:管壳码:" + serchShellBlastNo(detonatorId) + "芯片码" + zhuce_form.getDenaId() + "该雷管电流过大");
                    Utils.writeRecord("--单发注册--:管壳码:" + serchShellBlastNo(detonatorId) + "芯片码" + zhuce_form.getDenaId() + "该雷管电流过大");
                }
//                else {
                if (zhuce_form != null) {//管厂码,特征码,雷管id
//                        // 获取 管壳码
//                        String shellNo = new GreenDaoMaster().getShellNo(detonatorId);
                    insertSingleDenator(zhuce_form);//单发注册
                    zhuce_Flag = 0;
                }

//                }
            }
        } else if (DefCommand.CMD_4_XBSTATUS_7.equals(cmd)) { //46
            byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");//13
            sendCmd(powerCmd);
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
     * 单发注册 方法(单发输入会用到)
     */
    private int insertSingleDenator(String shellNo) {
        int f = getFan(duan_new);
        if (f == 1) {
            show_Toast(getResources().getString(R.string.text_queryHis_dialog8));
            return -1;
        }
        Log.e("扫码", "单发注册方法1: ");
        if (shellNo.length() < 13 && shellNo.length() > 0) {
            return -1;
        }
        Log.e(TAG, "start_delay_data: " + start_delay_data);
        if (start_delay_data.length() == 0||groupList.size()==0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
            return -1;
        }
        if (check(shellNo) == -1) {
            return -1;
        }
//        String denatorId = serchDenatorId(shellNo);
        DetonatorTypeNew detonatorTypeNew = new GreenDaoMaster().serchDenatorId(shellNo);
        //判断芯片码(要传13位芯片码,不要传8位的,里有截取方法)//判断8位芯片码
        if (detonatorTypeNew != null && detonatorTypeNew.getDetonatorId() != null && checkRepeatdenatorId(detonatorTypeNew.getDetonatorId())) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
//        if (detonatorTypeNew == null) {
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
//            return -1;
//        }

        PaiData paiData = groupList.get(groupListChoice - 1);
        int start_delay = Integer.parseInt(paiData.getStartDelay());//开始延时
        int f1 = Integer.parseInt(paiData.getKongDelay());//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
//        int maxNo = getMaxNumberNo();
//        int delay = getMaxDelay(maxNo);//获取最大延时
        // 获取 该区域 最大序号
        int maxNo = new GreenDaoMaster().getPieceMaxNum(mRegion);//获取该区域最大序号
        int maxKong = new GreenDaoMaster().getPieceAndPaiMaxKong(mRegion, paiChoice);//获取该区域最大孔号
        int total = new GreenDaoMaster().queryDetonatorPaiSize(mRegion, paiChoice + "");//获取该区域最大孔号
        int delay_max = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiChoice);//获取该区域 最大序号的延时
        int delay_min = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, paiChoice);
        Log.e(TAG, "当前段最小序号延时: " + delay_min);
        Log.e(TAG, "当前段最大延时参数: " + duan_new);
        Log.e(TAG, "当前段最大延时: " + delay_max);
        Log.e(TAG, "当前选择排号 paiChoice: " + paiChoice);
        Log.e(TAG, "当前选择孔号 kongChoice: " + kongChoice);
        Log.e(TAG, "当前段最大孔号 maxKong: " + maxKong);
        Log.e(TAG, "当前段最大孔号 total: " + total);
//        if (childList.get(groupListChoice - 1).size() > 0) {
//            Log.e(TAG, "当前选择管 childList.get(paiChoice).get(kongChoice): " + childList.get(groupListChoice - 1).get(childListChoice - 1).toString());//没有雷管的时候会报错
//        }
        int duanNo2 = new GreenDaoMaster().getPaiMaxDuanNo((maxKong + 1), mRegion, paiChoice);//获取该区域 最大duanNo
        Log.e("扫码", "获取当前孔的位数 duanNo2: " + duanNo2);
//        if (delay_max == 0 && duanNo2 == 0) {
//            delay_max = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);
//        }
        int delay_start = delay_max;

        if (btn_start || maxKong == 0) {
            delay_start = start_delay;
        }
        delay_set = "f1";
        Log.e("单发输入", "delay_start: " + delay_start);


        //判断延时是否超出范围
        if (!flag_jh_f1 || !flag_jh_f2) {
            if (delay_set.equals("f1")) {
                if (maxSecond != 0 && start_delay - f1 < 0) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
                    return -1;
                }
            } else if (delay_set.equals("f2")) {
                if (maxSecond != 0 && start_delay - f2 < 0) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
                    return -1;
                }
            }
        } else {
            if (delay_set.equals("f1")) {
                if (maxSecond != 0 && delay_max + f1 > maxSecond) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                    return -1;
                }
            } else if (delay_set.equals("f2")) {
                if (maxSecond != 0 && delay_max + f2 > maxSecond) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                    return -1;
                }
            }
        }

        int tk_num = 0;
        if (etTk.getText().toString() != null && etTk.getText().toString().length() > 0) {
            tk_num = Integer.parseInt(etTk.getText().toString());
        }

        delay_max = getDelay(maxKong, delay_max, start_delay, f1, tk_num, f2, delay_min, duanNo2);
        if (delay_max < 0) {//
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
            return -1;
        }
        int duanNUM = getDuanNo(duan_new, mRegion);//也得做区域区分
        Log.e("扫码", "delay_max: " + delay_max);
        Log.e("扫码", "duanNo2: " + duanNo2);
        maxNo++;
        DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
        denatorBaseinfo.setBlastserial((maxKong + 1));
        denatorBaseinfo.setSithole((maxKong + 1) + "");
        denatorBaseinfo.setShellBlastNo(shellNo);
        denatorBaseinfo.setDelay(delay_max);
        denatorBaseinfo.setRegdate(Utils.getDateFormat(new Date()));
        denatorBaseinfo.setStatusCode("FF");
        denatorBaseinfo.setStatusName("已注册");
        denatorBaseinfo.setErrorCode("00");
        denatorBaseinfo.setErrorName("");
        denatorBaseinfo.setWire("");//桥丝状态
        denatorBaseinfo.setPiece(mRegion);
        denatorBaseinfo.setDuan(duan_new);
        denatorBaseinfo.setDuanNo(1);
        denatorBaseinfo.setPai(paiChoice + "");
        denatorBaseinfo.setAuthorization("1");
        Log.e(TAG, "同孔  paiChoice: " + paiChoice);
        Log.e(TAG, "同孔  flag_t1: " + flag_t1);
        Log.e(TAG, "同孔  duanNo2: " + duanNo2);
        int duanNo1 = new GreenDaoMaster().getPaiMaxDuanNo(maxKong, mRegion, paiChoice);//获取该区域 最大duanNo
        if (!flag_t1 || (kongSum >= 1 + duanNo1)) {//判断同孔
            int kong = maxKong;

            if (duanNo1 == 0) {
                kong = maxKong + 1;
            }
            Log.e("扫码-单孔多发判断", "duanNo1: " + duanNo1);
            Log.e("扫码-单孔多发判断", "delay_min: " + delay_min);
            Log.e("扫码-单孔多发判断", "delay_start: " + delay_start);
            Log.e("扫码-单孔多发判断", "f2_delay_data: " + f2_delay_data);
            duanNo1 = duanNo1 + 1;
            denatorBaseinfo.setSithole(kong + "");
            denatorBaseinfo.setBlastserial(kong);
            denatorBaseinfo.setDuanNo((duanNo1));
            if (duanNo1 > 1) {
                if (!flag_jh_f1) {//孔内是否递减
                    denatorBaseinfo.setDelay((delay_min - Integer.parseInt(f2_delay_data)));
                } else {
                    denatorBaseinfo.setDelay((delay_start + Integer.parseInt(f2_delay_data)));
                }
            }
        }
        int delay_add = 0;
        Log.e("插入--单发输入--插入标志", "00 - groupListChoice: " + groupListChoice + "--childListChoice:" + childListChoice);
        Log.e("插入--单发输入--插入标志1", "charu: " + charu);
        if (kongChoice != total && childList.get(groupListChoice - 1).size() > 0 && !flag_add) {//加孔加位操作,
            charu = true;
        }
        Log.e("插入--单发输入--插入标志", "charu: " + charu);
        Log.e("插入--单发输入--插入标志", "kongChoice: " + kongChoice);
        Log.e("插入--单发输入--插入标志", "maxKong: " + maxKong);
        Log.e("插入--单发输入--插入标志", "total: " + total);
        Log.e("插入--单发输入--插入标志", "groupListChoice: " + groupListChoice + "--childListChoice:" + childListChoice);
        Log.e("插入--单发输入--插入标志", "childList.get(groupListChoice - 1).size(): " + childList.get(groupListChoice - 1).size());
        if (charu) {
            Log.e("插入--单发输入--插入标志", "childList.get(groupListChoice - 1): " + childList.get(groupListChoice - 1));
            db_charu = childList.get(groupListChoice - 1).get(childListChoice - 1);//越界问题
            Log.e("插入--单发输入--插入标志", "db_charu: " + db_charu.toString());
            int konghao = db_charu.getBlastserial() + 1;

            denatorBaseinfo.setSithole(konghao + "");
            Log.e(TAG, "插入--选中插入的雷管: " + db_charu.getShellBlastNo() + " 延时:" + db_charu.getDelay());
            Log.e(TAG, "插入--插入孔前一发延时: " + db_charu.getDelay());
            Log.e("插入--单发输入--插入孔号", "konghao: " + konghao);
            if (!flag_t1) {//插入位
                denatorBaseinfo.setBlastserial(db_charu.getBlastserial());
                denatorBaseinfo.setDuanNo((db_charu.getDuanNo() + 1));
                denatorBaseinfo.setDelay(db_charu.getDelay());
                Log.e("插入--单发输入--插入孔号", "konghao: " + konghao);
            } else {
                if (!flag_jh_f1) {
                    if (delay_set.equals("f1")) {
                        delay_add = -f1;
                    }
                } else {
                    if (delay_set.equals("f1")) {
                        delay_add = f1;
                    }
                }
                delay_max = getDelay_charu(start_delay, f1, f2, maxNo, delay_min, tk_num);
                Log.e("插入--单发输入--插入延时", "delay_max: " + delay_max);
                denatorBaseinfo.setDelay(delay_max);
                denatorBaseinfo.setBlastserial(konghao);
                denatorBaseinfo.setDuanNo(1);//加孔都是从序号1开始
            }
            //所有后续雷管号加1
            Utils.charuData(mRegion, db_charu, flag_t1, delay_add, db_charu.getPai(), paiChoice);//插入雷管的后面所有雷管序号+1
            denatorBaseinfo.setDuan(db_charu.getDuan());
        }


        if (detonatorTypeNew != null && detonatorTypeNew.getDetonatorId() != null && !detonatorTypeNew.getDetonatorId().equals("0")) {
            denatorBaseinfo.setDenatorId(detonatorTypeNew.getDetonatorId());
            denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
            denatorBaseinfo.setRegdate(detonatorTypeNew.getTime());
            denatorBaseinfo.setAuthorization(detonatorTypeNew.getDetonatorIdSup());//雷管芯片型号
        }

        DenatorBaseinfo denatorBaseinfo_choice = null;//Index: 5, Size: 4
        Log.e(TAG, "更新排数据 paiChoice: " + paiChoice);
        Log.e(TAG, "更新排数据 kongChoice: " + kongChoice);
        Log.e(TAG, "更新排数据 flag_add: " + flag_add);
        if (childList.get(groupListChoice - 1).size() > 0) {
            denatorBaseinfo_choice = childList.get(groupListChoice - 1).get(childListChoice - 1);
        }
        if (denatorBaseinfo_choice != null && denatorBaseinfo_choice.getShellBlastNo().length() < 13 && (flag_add||flag_saoma)) {
            Log.e(TAG, "更新排数据 getBlastserial: " + denatorBaseinfo_choice.getBlastserial());
            flag_add = true;
            denatorBaseinfo_choice.setDenatorId(denatorBaseinfo.getDenatorId());
            denatorBaseinfo_choice.setShellBlastNo(denatorBaseinfo.getShellBlastNo());
            denatorBaseinfo_choice.setZhu_yscs(denatorBaseinfo.getZhu_yscs());
            denatorBaseinfo_choice.setAuthorization(denatorBaseinfo.getAuthorization());
            getDaoSession().getDenatorBaseinfoDao().update(denatorBaseinfo_choice);
        } else {
            Log.e(TAG, "判断是否是插入 charu: " + charu);
            if (!charu) {
                flag_zhuce = true;//标记新注册,使光标移动到新的雷管上
            }

            //向数据库插入数据
            getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
        }
        //更新排数据
        updataPaiData();

        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        Utils.writeRecord("单发注册:--管壳码:" + shellNo + "--延时:" + delay_max);
        AppLogUtils.writeAppLog("单发注册:--管壳码:" + shellNo + "--延时:" + delay_max);
        charu = false;
        flag_saoma = false;

        return 0;
    }

    /***
     * 单发注册方法
     */
    private int insertSingleDenator(From12Reister zhuce_form) {
        String detonatorId = Utils.GetShellNoById_newXinPian(zhuce_form.getFacCode(), zhuce_form.getFeature(), zhuce_form.getDenaId());
        int f = getFan(duan_new);
        if (f == 1) {
            show_Toast(getResources().getString(R.string.text_queryHis_dialog8));
            return -1;
        }
        Log.e("扫码", "单发注册方法1: ");

        Log.e(TAG, "start_delay_data: " + start_delay_data);
        if (start_delay_data.length() == 0||groupList.size()==0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
            return -1;
        }

        if (checkRepeatdenatorId(detonatorId)) {//判断芯片码(要传13位芯片码,不要传8位的,里有截取方法)
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
//        String denatorId = serchDenatorId(shellNo);
        DetonatorTypeNew detonatorTypeNew = serchDenatorForDetonatorTypeNew(detonatorId);
        //判断芯片码(要传13位芯片码,不要传8位的,里有截取方法)//判断8位芯片码
        if (detonatorTypeNew != null && detonatorTypeNew.getDetonatorId() != null && checkRepeatdenatorId(detonatorTypeNew.getDetonatorId())) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
//        if (detonatorTypeNew == null) {
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
//            return -1;
//        }

        PaiData paiData = groupList.get(groupListChoice - 1);
        int start_delay = Integer.parseInt(paiData.getStartDelay());//开始延时
        int f1 = Integer.parseInt(paiData.getKongDelay());//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
//        int maxNo = getMaxNumberNo();
//        int delay = getMaxDelay(maxNo);//获取最大延时
        // 获取 该区域 最大序号
        int maxNo = new GreenDaoMaster().getPieceMaxNum(mRegion);//获取该区域最大序号
        int maxKong = new GreenDaoMaster().getPieceAndPaiMaxKong(mRegion, paiChoice);//获取该区域最大孔号
        int delay_max = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiChoice);//获取该区域 最大序号的延时
        int delay_min = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, paiChoice);
        Log.e(TAG, "当前段最小序号延时: " + delay_min);
        Log.e(TAG, "当前段最大延时参数: " + duan_new);
        Log.e(TAG, "当前段最大延时: " + delay_max);
        Log.e(TAG, "当前段最大孔号 maxKong: " + maxKong);
        int duanNo2 = new GreenDaoMaster().getPaiMaxDuanNo((maxKong + 1), mRegion, paiChoice);//获取该区域 最大duanNo
        Log.e("扫码", "获取 duanNo2: " + duanNo2);
//        if (delay_max == 0 && duanNo2 == 0) {
//            delay_max = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);
//        }
        int delay_start = delay_max;

        if (btn_start || maxKong == 0) {
            delay_start = start_delay;
        }
        delay_set = "f1";
        Log.e("单发输入", "delay_start: " + delay_start);


        //判断延时是否超出范围
        if (!flag_jh_f1 || !flag_jh_f2) {
            if (delay_set.equals("f1")) {
                if (maxSecond != 0 && start_delay - f1 < 0) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
                    return -1;
                }
            } else if (delay_set.equals("f2")) {
                if (maxSecond != 0 && start_delay - f2 < 0) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
                    return -1;
                }
            }
        } else {
            if (delay_set.equals("f1")) {
                if (maxSecond != 0 && delay_max + f1 > maxSecond) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                    return -1;
                }
            } else if (delay_set.equals("f2")) {
                if (maxSecond != 0 && delay_max + f2 > maxSecond) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                    return -1;
                }
            }
        }

        int tk_num = 0;
        if (etTk.getText().toString() != null && etTk.getText().toString().length() > 0) {
            tk_num = Integer.parseInt(etTk.getText().toString());
        }

        delay_max = getDelay(maxKong, delay_max, start_delay, f1, tk_num, f2, delay_min, duanNo2);
        if (delay_max < 0) {//
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
            return -1;
        }
        int duanNUM = getDuanNo(duan_new, mRegion);//也得做区域区分
        Log.e("扫码", "delay_max: " + delay_max);
        Log.e("扫码", "duanNo2: " + duanNo2);
        maxNo++;
        DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();

        if (detonatorTypeNew != null && detonatorTypeNew.getShellBlastNo().length() == 13) {
            denatorBaseinfo.setShellBlastNo(detonatorTypeNew.getShellBlastNo());
            denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
            denatorBaseinfo.setRegdate(detonatorTypeNew.getTime());
            denatorBaseinfo.setAuthorization(detonatorTypeNew.getDetonatorIdSup());//雷管芯片型号
            AppLogUtils.writeAppLog("--单发注册--" + "注册雷管码:" + detonatorTypeNew.getShellBlastNo() + " --芯片码:" + zhuce_form.getDenaId());
            Utils.writeRecord("--单发注册--" + "注册雷管码:" + detonatorTypeNew.getShellBlastNo() + " --芯片码:" + zhuce_form.getDenaId());
        } else {
            denatorBaseinfo.setShellBlastNo(detonatorId);
            denatorBaseinfo.setZhu_yscs(zhuce_form.getZhu_yscs());
            denatorBaseinfo.setRegdate(Utils.getDateFormat(new Date()));
            denatorBaseinfo.setAuthorization("1");
            AppLogUtils.writeAppLog("--单发注册--" + " --芯片码:" + zhuce_form.getDenaId());
            Utils.writeRecord("--单发注册--" + " --芯片码:" + zhuce_form.getDenaId());
        }

        denatorBaseinfo.setDenatorId(detonatorId);
        denatorBaseinfo.setBlastserial((maxKong + 1));
        denatorBaseinfo.setSithole((maxKong + 1) + "");
        denatorBaseinfo.setDelay(delay_max);
        denatorBaseinfo.setStatusCode("FF");
        denatorBaseinfo.setStatusName("已注册");
        denatorBaseinfo.setErrorCode("00");
        denatorBaseinfo.setErrorName("");
        denatorBaseinfo.setWire(zhuce_form.getWire());//桥丝状态
        denatorBaseinfo.setPiece(mRegion);
        denatorBaseinfo.setDuan(duan_new);
        denatorBaseinfo.setDuanNo(1);
        denatorBaseinfo.setPai(paiChoice + "");
        Log.e(TAG, "注册  mRegion: " + mRegion);
        Log.e(TAG, "注册  paiChoice: " + paiChoice);
        Log.e(TAG, "注册  flag_t1: " + flag_t1);
        Log.e(TAG, "注册  duanNo2: " + duanNo2);
        int duanNo1 = new GreenDaoMaster().getPaiMaxDuanNo(maxKong, mRegion, paiChoice);//获取该区域 最大duanNo
        if (!flag_t1 || (kongSum >= 1 + duanNo1)) {//判断同孔
            int kong = maxKong;

            if (duanNo1 == 0) {
                kong = maxKong + 1;
            }
            Log.e("扫码-单孔多发判断", "duanNo1: " + duanNo1);
            Log.e("扫码-单孔多发判断", "delay_min: " + delay_min);
            Log.e("扫码-单孔多发判断", "delay_start: " + delay_start);
            Log.e("扫码-单孔多发判断", "delay_start: " + delay_start);
            Log.e("扫码-单孔多发判断", "f2_delay_data: " + f2_delay_data);
            duanNo1 = duanNo1 + 1;
            denatorBaseinfo.setSithole(kong + "");
            denatorBaseinfo.setBlastserial(kong);
            denatorBaseinfo.setDuanNo((duanNo1));
            if (duanNo1 > 1) {
                if (!flag_jh_f1) {//孔内是否递减
                    denatorBaseinfo.setDelay((delay_min - Integer.parseInt(f2_delay_data)));
                } else {
                    denatorBaseinfo.setDelay((delay_start + Integer.parseInt(f2_delay_data)));
                }
            }
        }
        int delay_add = 0;
        if (charu) {
            Log.e(TAG, "插入孔前一发延时: " + db_charu.getDelay());
            if (!flag_t1) {//同孔
                denatorBaseinfo.setDuanNo(db_charu.getDuanNo());
                denatorBaseinfo.setDelay(db_charu.getDelay());
            } else {
                if (!flag_jh_f1) {
                    if (delay_set.equals("f1")) {
                        delay_add = -f1;
                    }
                } else {
                    if (delay_set.equals("f1")) {
                        delay_add = f1;
                    }
                }
//                delay_max = getDelay_charu(start_delay, f1, f2, maxNo, delay_min, tk_num);
                delay_max = getDelay(maxKong, delay_max, start_delay, f1, tk_num, f2, delay_min, db_charu.getDuanNo());
                Log.e("单发输入--插入延时", "delay_max: " + delay_max);
                Log.e("单发输入--插入延时", "delay_add: " + delay_add);
                Log.e("单发输入--插入flag_t1", "flag_t1: " + flag_t1);
//                if(flag_t1&&delay==db_charu.getDelay()){
//                    show_Toast("没选同孔,不能设置跟选中雷管相同延时");
//                    return -1;
//                }
                denatorBaseinfo.setDelay(delay_max);
                if (!flag_t1) {
                    denatorBaseinfo.setDuanNo(db_charu.getDuanNo() + 1);
                } else {
                    denatorBaseinfo.setDuanNo(db_charu.getDuanNo());
                }

            }

            Utils.charuData(mRegion, db_charu, flag_t1, delay_add, db_charu.getPai(), paiChoice);//插入雷管的后面所有雷管序号+1
            int xuhao = db_charu.getBlastserial() + 1;
            int konghao = Integer.parseInt(db_charu.getSithole()) + 1;
            denatorBaseinfo.setBlastserial(konghao);
            denatorBaseinfo.setSithole(konghao + "");
            denatorBaseinfo.setDuan(db_charu.getDuan());


        }


        if (detonatorTypeNew != null && detonatorTypeNew.getDetonatorId() != null && !detonatorTypeNew.getDetonatorId().equals("0")) {
            denatorBaseinfo.setDenatorId(detonatorTypeNew.getDetonatorId());
            denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
            denatorBaseinfo.setRegdate(detonatorTypeNew.getTime());
            denatorBaseinfo.setAuthorization(detonatorTypeNew.getDetonatorIdSup());//雷管芯片型号
        }

        DenatorBaseinfo denatorBaseinfo_choice = null;//Index: 5, Size: 4
        Log.e(TAG, "单发注册paiChoice: " + paiChoice);
        Log.e(TAG, "单发注册kongChoice: " + kongChoice);
        Log.e(TAG, "childList.get(groupListChoice - 1).size(): " + childList.get(groupListChoice - 1).size());
        Log.e(TAG, "单发注册flag_add: " + flag_add);
        if (childList.get(groupListChoice - 1).size() > 0) {
            denatorBaseinfo_choice = childList.get(groupListChoice - 1).get(childListChoice - 1);
            Log.e(TAG, "单发注册denatorBaseinfo_choice.getShellBlastNo().length(): " + denatorBaseinfo_choice.getShellBlastNo().length());
        }

        if (denatorBaseinfo_choice != null && denatorBaseinfo_choice.getShellBlastNo().length() < 13) {
            flag_add = true;
            denatorBaseinfo_choice.setDenatorId(denatorBaseinfo.getDenatorId());
            denatorBaseinfo_choice.setShellBlastNo(denatorBaseinfo.getShellBlastNo());
            denatorBaseinfo_choice.setZhu_yscs(denatorBaseinfo.getZhu_yscs());
            denatorBaseinfo_choice.setAuthorization(denatorBaseinfo.getAuthorization());
            getDaoSession().getDenatorBaseinfoDao().update(denatorBaseinfo_choice);
        } else {
            if (!charu) {
                flag_zhuce = true;//标记新注册,使光标移动到新的雷管上
            }
            //向数据库插入数据
            getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
        }
        //更新排数据
        updataPaiData();

        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        Utils.writeRecord("单发注册:--芯片码:" + detonatorId + "--延时:" + delay_max);
        AppLogUtils.writeAppLog("单发注册:--芯片码:" + detonatorId + "--延时:" + delay_max);
        charu = false;
        return 0;
    }


    /***
     * 扫码注册方法
     */
    private int insertSingleDenator_2(String shellNo, String denatorId, String yscs, String version, String duan_scan) {
        Log.e("扫码", "单发注册方法2: ");
//        if (shellNo.length() != 13) {
//            return -1;
//        }
        if (!duan_scan.equals("0")) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(12));
            return -1;
        }
//        if (start_delay_data.length() == 0||groupList.size()==0) {
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
//            return -1;
//        }
        if (check(shellNo) == -1) {
            return -1;
        }
        if (checkRepeatdenatorId(denatorId)) {//芯片码查重
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
//        int maxNo = getMaxNumberNo();
        PaiData paiData = groupList.get(groupListChoice - 1);
        int start_delay = Integer.parseInt(paiData.getStartDelay());//开始延时
        int f1 = Integer.parseInt(paiData.getKongDelay());//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
        Log.e(TAG, "start_delay: " + start_delay);
        Log.e(TAG, "f1: " + f1);
        Log.e(TAG, "f2: " + f2);
//        int delay = getMaxDelay(maxNo);//获取最大延时

        int maxNo = new GreenDaoMaster().getPieceMaxNum(mRegion);//获取该区域最大序号
        int maxKong = new GreenDaoMaster().getPieceAndPaiMaxKong(mRegion, paiChoice);//获取该区域最大孔号
        int delay_max = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiChoice);//获取该区域 最大序号的延时
        int delay_min = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, paiChoice);
        Log.e("扫码", "kongChoice: " + kongChoice);
        Log.e("扫码", "mRegion: " + mRegion);
        Log.e("扫码", "delay_max: " + delay_max);
        int duanNo2 = new GreenDaoMaster().getPaiMaxDuanNo((maxKong + 1), mRegion, paiChoice);//获取该区域 最大duanNo
        // 查询前一个区域,新雷管位为2,新雷管位0

//        if (delay_max == 0 && duanNo2 == 0) {
//            delay_max = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);
//        }
        int delay_start = delay_max;
        if (btn_start || maxKong == 0) {
            delay_start = start_delay;
        }
        delay_set = "f1";
        Log.e("扫码", "delay_set: " + delay_set);

        Log.e("扫码", "是否递减 flag_jh_f1: " + flag_jh_f1);
        Log.e("扫码", "maxKong: " + maxKong);
        Log.e("扫码", "duanNo2: " + duanNo2);
        //判断延时是否超出范围
        if (!flag_jh_f1 || !flag_jh_f2) {
            if (delay_set.equals("f1")) {
                if (maxSecond != 0 && start_delay - f1 < 0) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
                    return -1;
                }
            } else if (delay_set.equals("f2")) {
                if (maxSecond != 0 && start_delay - f2 < 0) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
                    return -1;
                }
            }
        } else {
            if (delay_set.equals("f1")) {
                if (maxSecond != 0 && delay_max + f1 > maxSecond) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                    return -1;
                }
            } else if (delay_set.equals("f2")) {
                if (maxSecond != 0 && delay_max + f2 > maxSecond) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                    return -1;
                }
            }
        }

        int tk_num = 0;
        if (etTk.getText().toString() != null && etTk.getText().toString().length() > 0) {
            tk_num = Integer.parseInt(etTk.getText().toString());
        }
        delay_max = getDelay(maxKong, delay_max, start_delay, f1, tk_num, f2, delay_min, duanNo2);
        if (delay_max < 0) {//
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
            return -1;
        }
        AppLogUtils.writeAppLog("单发注册:--管壳码:" + shellNo + "芯片码" + denatorId + "--延时:" + delay_max);
        Utils.writeRecord("单发注册:--管壳码:" + shellNo + "芯片码" + denatorId + "--延时:" + delay_max);
        int a = 0;
        if (duan_scan.equals("0")) {//普通雷管按当前页面选择的来
            a = duan_new;
        } else {
            a = Integer.parseInt(duan_scan);//煤许雷管按二维码设置的来
        }
        int duanNUM = getDuanNo(a, mRegion);//也得做区域区分
        Log.e("扫码", "duanNo2: " + duanNo2);
        maxNo++;
        Log.e(TAG, "雷管最后延时:delay_max: " + delay_max);
        DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
        denatorBaseinfo.setBlastserial((maxKong + 1));
        denatorBaseinfo.setSithole((maxKong + 1) + "");
        denatorBaseinfo.setShellBlastNo(shellNo);
        denatorBaseinfo.setDelay(delay_max);
        denatorBaseinfo.setRegdate(Utils.getDateFormat(new Date()));
        denatorBaseinfo.setStatusCode("FF");
        denatorBaseinfo.setStatusName("已注册");
        denatorBaseinfo.setErrorCode("00");
        denatorBaseinfo.setErrorName("");
        denatorBaseinfo.setWire("");//桥丝状态

        denatorBaseinfo.setDenatorId(denatorId);
        denatorBaseinfo.setZhu_yscs(yscs);
        denatorBaseinfo.setDuan(a);
        denatorBaseinfo.setDuanNo(1);
        denatorBaseinfo.setPiece(mRegion);
        denatorBaseinfo.setPai(paiChoice + "");
        DetonatorTypeNew detonatorTypeNew = new GreenDaoMaster().serchDenatorId(shellNo);
        if (detonatorTypeNew != null && detonatorTypeNew.getDetonatorId() != null && !detonatorTypeNew.getDetonatorId().equals("0")) {
            denatorBaseinfo.setRegdate(detonatorTypeNew.getTime());
            denatorBaseinfo.setAuthorization(detonatorTypeNew.getDetonatorIdSup());//雷管芯片型号
        } else {
            denatorBaseinfo.setAuthorization(version);//雷管芯片型号
        }
//        Log.e("扫码-单孔多发判断", "---------------" );
        int duanNo1 = new GreenDaoMaster().getPaiMaxDuanNo(maxKong, mRegion, paiChoice);//获取该区域 最大duanNo
        Log.e("扫码-单孔多发判断", "flag_t1: " + flag_t1);
        Log.e("扫码-单孔多发判断", "kongSum: " + kongSum);
        Log.e("扫码-单孔多发判断", "duanNo1: " + duanNo1);
        Log.e("扫码-单孔多发判断", "maxKong: " + maxKong);
        if (!flag_t1 || (kongSum >= 1 + duanNo1)) {//判断同孔
            int kong = maxKong;

            if (duanNo1 == 0) {
                kong = maxKong + 1;
            }
            Log.e("扫码-单孔多发判断", "duanNo1: " + duanNo1);
            Log.e("扫码-单孔多发判断", "delay_min: " + delay_min);
            Log.e("扫码-单孔多发判断", "delay_start: " + delay_start);
            Log.e("扫码-单孔多发判断", "delay_start: " + delay_start);
            Log.e("扫码-单孔多发判断", "f2_delay_data: " + f2_delay_data);
            Log.e("扫码-单孔多发判断", "--------------: ");
            duanNo1 = duanNo1 + 1;
            denatorBaseinfo.setSithole(kong + "");
            denatorBaseinfo.setBlastserial(kong);
            denatorBaseinfo.setDuanNo((duanNo1));
            if (duanNo1 > 1) {
                if (!flag_jh_f1) {//孔内是否递减
                    denatorBaseinfo.setDelay((delay_min - Integer.parseInt(f2_delay_data)));
                } else {
                    denatorBaseinfo.setDelay((delay_start + Integer.parseInt(f2_delay_data)));
                }
            }


        }
        int delay_add = 0;
        Log.e("插入--单发输入--插入标志", "charu: " + charu);
        if (charu) {
            Log.e(TAG, "插入孔前一发延时: " + db_charu.getDelay());
            if (!flag_t1) {//同孔
                denatorBaseinfo.setDuanNo(db_charu.getDuanNo());
                denatorBaseinfo.setDelay(db_charu.getDelay());
            } else {
                if (!flag_jh_f1) {
                    if (delay_set.equals("f1")) {
                        delay_add = -f1;
                    }
                } else {
                    if (delay_set.equals("f1")) {
                        delay_add = f1;
                    }
                }
                delay_max = getDelay_charu(start_delay, f1, f2, maxNo, delay_min, tk_num);

                denatorBaseinfo.setDelay(delay_max);
                if (!flag_t1) {
                    denatorBaseinfo.setDuanNo(db_charu.getDuanNo() + 1);
                } else {
                    denatorBaseinfo.setDuanNo(db_charu.getDuanNo());
                }
            }

            Utils.charuData(mRegion, db_charu, flag_t1, delay_add, db_charu.getPai(), paiChoice);//插入雷管的后面所有雷管序号+1
            int xuhao = db_charu.getBlastserial() + 1;
            int konghao = Integer.parseInt(db_charu.getSithole()) + 1;
            denatorBaseinfo.setBlastserial(konghao);
            denatorBaseinfo.setSithole(konghao + "");
            denatorBaseinfo.setDuan(db_charu.getDuan());


        }

        //查询选中的雷管是否管壳码为空

//        DenatorBaseinfo denatorBaseinfo_choice = new GreenDaoMaster().serchDenatorIdForChoice(paiChoice, kongChoice);
        DenatorBaseinfo denatorBaseinfo_choice = null;//Index: 5, Size: 4
        Log.e(TAG, "扫码注册paiChoice: " + paiChoice);
        Log.e(TAG, "扫码注册kongChoice: " + kongChoice);
        Log.e(TAG, "扫码注册groupListChoice: " + groupListChoice);
        Log.e(TAG, "扫码注册childListChoice: " + childListChoice);
        Log.e(TAG, "childList.get(groupListChoice - 1).size(): " + childList.get(groupListChoice - 1).size());

        if (childList.get(groupListChoice - 1).size() > 0) {
            denatorBaseinfo_choice = childList.get(groupListChoice - 1).get(childListChoice - 1);//有为0的情况
        }

        if (denatorBaseinfo_choice != null && denatorBaseinfo_choice.getShellBlastNo().length() < 13) {
            flag_add = true;
            denatorBaseinfo_choice.setDenatorId(denatorBaseinfo.getDenatorId());
            denatorBaseinfo_choice.setShellBlastNo(denatorBaseinfo.getShellBlastNo());
            denatorBaseinfo_choice.setZhu_yscs(denatorBaseinfo.getZhu_yscs());
            denatorBaseinfo_choice.setAuthorization(denatorBaseinfo.getAuthorization());
            getDaoSession().getDenatorBaseinfoDao().update(denatorBaseinfo_choice);
        } else {
            if (!charu) {
                flag_zhuce = true;//标记新注册,使光标移动到新的雷管上
            }
            //向数据库插入数据
            getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
        }

        //更新排数据
        updataPaiData();

        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

//        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        charu = false;
        return 0;
    }


//    崩溃信息Attempt to invoke virtual method 'void android_serialport_api.xingbang.db.PaiData.setSum(java.lang.String)' on a null object reference
//    java.lang.NullPointerException: Attempt to invoke virtual method 'void android_serialport_api.xingbang.db.PaiData.setSum(java.lang.String)' on a null object reference
//    at android_serialport_api.xingbang.firingdevice.ReisterMainPage_scan.updataPaiData(ReisterMainPage_scan.java:2816)
//    at android_serialport_api.xingbang.firingdevice.ReisterMainPage_scan.onViewClicked(ReisterMainPage_scan.java:3262)
//    at android_serialport_api.xingbang.firingdevice.ReisterMainPage_scan_ViewBinding$21.doClick(ReisterMainPage_scan_ViewBinding.java:316)
//    at butterknife.internal.DebouncingOnClickListener.onClick(DebouncingOnClickListener.java:26)
//    at android.view.View.performClick(View.java:6603)
//    at android.view.View.performClickInternal(View.java:6576)
//    at android.view.View.access$3100(View.java:780)
//    at android.view.View$PerformClick.run(View.java:26090)
//    at android.os.Handler.handleCallback(Handler.java:873)
//    at android.os.Handler.dispatchMessage(Handler.java:99)
//    at android.os.Looper.loop(Looper.java:193)
//    at android.app.ActivityThread.main(ActivityThread.java:6702)
//    at java.lang.reflect.Method.invoke(Native Method)
//    at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
//    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:911)


    /***
     * 注册方法
     * 手动输入注册(通过开始管壳码和截止管壳码计算出所有管壳码)
     */
    private int insertDenator(String prex, int start, int end, boolean x) {
        Log.e("扫码", "单发注册方法3: ");
        if (end < start) return -1;
        if (start < 0 || end > 99999) return -1;
        String shellNo = "";
        int flag = 0;
        PaiData paiData = groupList.get(groupListChoice - 1);
        Log.e(TAG, "手动输入 paiData.getKongDelay: " + paiData.getKongDelay());
        Log.e(TAG, "手动输入 paiData.getStartDelay: " + paiData.getStartDelay());
        int start_delay = Integer.parseInt(paiData.getStartDelay());//开始延时
        int f1 = Integer.parseInt(paiData.getKongDelay());//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
        Log.e(TAG, "start_delay: " + start_delay);
        Log.e(TAG, "f1: " + f1);
        Log.e(TAG, "f2: " + f2);
//        int maxNo = getMaxNumberNo();
//        int delay = getMaxDelay(maxNo);//获取最大延时


        if (maxSecond != 0 && f1 > maxSecond) {//
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
            return -1;
        }
        if (maxSecond != 0 && f2 > maxSecond) {//
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
            return -1;
        }
        int reCount = 0;//统计注册了多少发雷管
        AppLogUtils.writeAppLog("--手动输入注册--前8位:" + prex + "--开始后5位:" + start +
                "--结束后5位:" + end + "--开始延时:" + start_delay);
        Utils.writeRecord("--手动输入注册--前8位:" + prex + "--开始后5位:" + start +
                "--结束后5位:" + end + "--开始延时:" + start_delay);
        for (int i = start; i <= end; i++) {
            int maxNo = new GreenDaoMaster().getPieceMaxNum(mRegion);//获取该区域最大序号
            int maxKong = new GreenDaoMaster().getPieceAndPaiMaxKong(mRegion, paiChoice);//获取该区域最大孔号
            int duanNo2 = new GreenDaoMaster().getPaiMaxDuanNo((maxKong + 1), mRegion, paiChoice);//获取该区域 最大duanNo
            int delay_max = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiChoice);//获取该区域 最大序号的延时
            int delay_min = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, paiChoice);
            Log.e("扫码", "kongChoice: " + kongChoice);
            Log.e("扫码", "mRegion: " + mRegion);
            Log.e("扫码", "delay_max: " + delay_max);
            delay_set = "f1";//默认选中f1,f2留着同孔
            Log.e("单发输入", "delay_set: " + delay_set);
            int delay_start = delay_max;
            if (btn_start || maxKong == 0) {
                delay_start = start_delay;
            }
            if (!flag_jh_f1 || !flag_jh_f2) {
                if (delay_set.equals("f1")) {
                    if (maxSecond != 0 && start_delay - f1 < 0) {//
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
                        return -1;
                    }
                } else if (delay_set.equals("f2")) {
                    if (maxSecond != 0 && start_delay - f2 < 0) {//
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
                        return -1;
                    }
                }
            } else {
                if (delay_set.equals("f1")) {
                    if (maxSecond != 0 && delay_max + f1 > maxSecond) {//
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                        return -1;
                    }
                } else if (delay_set.equals("f2")) {
                    if (maxSecond != 0 && delay_max + f2 > maxSecond) {//
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                        return -1;
                    }
                }
            }
            shellNo = prex + String.format("%05d", i);
            if (checkRepeatShellNo(shellNo)) {
                singleShellNo = shellNo;
                if (x) {
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
                }
                if (!x) {
                    xiangHao_errNum++;
                }
                break;
            }
            DetonatorTypeNew detonatorTypeNew = new GreenDaoMaster().serchDenatorId(shellNo);
//            if (detonatorTypeNew == null) {
//                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
//                pb_show = 0;
//                return -1;
//            }
            int tk_num = 0;
            if (etTk.getText().toString() != null && etTk.getText().toString().length() > 0) {
                tk_num = Integer.parseInt(etTk.getText().toString());
            }
            delay_max = getDelay(maxKong, delay_max, start_delay, f1, tk_num, f2, delay_min, duanNo2);
            Log.e("手动输入-最终延时", "delay_max: " + delay_max);
            if (delay_max < 0) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(13));
                return -1;
            }
            if (maxSecond != 0 && delay_max > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                break;
            }
            duanNo2 = new GreenDaoMaster().getPaiMaxDuanNo((maxKong + 1), mRegion, paiChoice);//获取该区域 最大duanNo
//            int duanNUM = getDuanNo(duan, mRegion);//也得做区域区分
            Log.e("手动输入3", "duanNo2: " + duanNo2);
            Log.e("手动输入3", "duan: " + duan_new);
            maxNo++;
            DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
            denatorBaseinfo.setBlastserial(maxKong + 1);
            denatorBaseinfo.setSithole((maxKong + 1) + "");
            denatorBaseinfo.setShellBlastNo(shellNo);
            denatorBaseinfo.setDelay(delay_max);
            denatorBaseinfo.setRegdate(Utils.getDateFormat(new Date()));
            denatorBaseinfo.setStatusCode("FF");
            denatorBaseinfo.setStatusName("已注册");
            denatorBaseinfo.setErrorCode("00");
            denatorBaseinfo.setErrorName("");
            denatorBaseinfo.setWire("");//桥丝状态
            denatorBaseinfo.setPiece(mRegion);
            denatorBaseinfo.setDuan(duan_new);
            denatorBaseinfo.setDuanNo(1);
            denatorBaseinfo.setPai(paiChoice + "");
            denatorBaseinfo.setAuthorization("1");


            Log.e("手动输入3", "flag_t1: " + flag_t1);
            int duanNo1 = new GreenDaoMaster().getPaiMaxDuanNo(maxKong, mRegion, paiChoice);//获取该区域 最大duanNo
            Log.e("扫码-单孔多发判断", "flag_t1: " + flag_t1);
            Log.e("扫码-单孔多发判断", "kongSum: " + kongSum);
            Log.e("扫码-单孔多发判断", "duanNo1: " + duanNo1);
            Log.e("扫码-单孔多发判断", "maxKong: " + maxKong);
            if (!flag_t1 || (kongSum >= 1 + duanNo1)) {//判断同孔
                int kong = maxKong;

                if (duanNo1 == 0) {
                    kong = maxKong + 1;
                }
                Log.e("扫码-单孔多发判断", "duanNo1: " + duanNo1);
                Log.e("扫码-单孔多发判断", "delay_min: " + delay_min);
                Log.e("扫码-单孔多发判断", "delay_start: " + delay_start);
                Log.e("扫码-单孔多发判断", "delay_start: " + delay_start);
                Log.e("扫码-单孔多发判断", "f2_delay_data: " + f2_delay_data);
                Log.e("扫码-单孔多发判断", "--------------: ");
                duanNo1 = duanNo1 + 1;
                denatorBaseinfo.setSithole(kong + "");
                denatorBaseinfo.setBlastserial(kong);
                denatorBaseinfo.setDuanNo((duanNo1));
                if (duanNo1 > 1) {
                    if (!flag_jh_f1) {//孔内是否递减
                        denatorBaseinfo.setDelay((delay_min - Integer.parseInt(f2_delay_data)));
                    } else {
                        denatorBaseinfo.setDelay((delay_start + Integer.parseInt(f2_delay_data)));
                    }
                }
            }
            int delay_add = 0;
            if (charu) {
                Log.e(TAG, "插入孔前一发延时: " + db_charu.getDelay());
                if (!flag_t1) {//同孔
                    denatorBaseinfo.setDuanNo(db_charu.getDuanNo());
                    denatorBaseinfo.setDelay(db_charu.getDelay());
                } else {
                    if (!flag_jh_f1) {
                        if (delay_set.equals("f1")) {
                            delay_add = -f1;
                        }
                    } else {
                        if (delay_set.equals("f1")) {
                            delay_add = f1;
                        }
                    }
                    delay_max = getDelay_charu(start_delay, f1, f2, maxNo, delay_min, tk_num);

                    denatorBaseinfo.setDelay(delay_max);
                    if (!flag_t1) {
                        denatorBaseinfo.setDuanNo(db_charu.getDuanNo() + 1);
                    } else {
                        denatorBaseinfo.setDuanNo(db_charu.getDuanNo());
                    }
                }

                Utils.charuData(mRegion, db_charu, flag_t1, delay_add, db_charu.getPai(), paiChoice);//插入雷管的后面所有雷管序号+1
                int xuhao = db_charu.getBlastserial() + 1;
                int konghao = Integer.parseInt(db_charu.getSithole()) + 1;
                denatorBaseinfo.setBlastserial(konghao);
                denatorBaseinfo.setSithole(konghao + "");
                denatorBaseinfo.setDuan(db_charu.getDuan());

                charu = false;
            }
            //如果授权库里面有管就更新数据
            if (detonatorTypeNew != null && detonatorTypeNew.getDetonatorId() != null && !detonatorTypeNew.getDetonatorId().equals("0")) {
                denatorBaseinfo.setDenatorId(detonatorTypeNew.getDetonatorId());
                denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
                denatorBaseinfo.setRegdate(detonatorTypeNew.getTime());
                denatorBaseinfo.setAuthorization(detonatorTypeNew.getDetonatorIdSup());//雷管芯片型号
            }
            //向数据库插入数据
            getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
            reCount++;
        }

        //更新排数据
        updataPaiData();
        flag_zhuce = true;//标记新注册,使光标移动到新的雷管上
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
//        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
        pb_show = 0;
        tipInfoFlag = 88;
        mHandler_1.sendMessage(mHandler_1.obtainMessage());
//        Utils.saveFile();//把软存中的数据存入磁盘中
        return reCount;
    }

    private int getDelay(int maxNo, int delay_max, int start_delay, int f1, int tk_num, int f2, int delay_min, int duanNo2) {
        Log.e(TAG, "是否递减--flag_jh_f1: " + flag_jh_f1);
        Log.e(TAG, "孔间还是排间delay_set: " + delay_set);
        Log.e(TAG, "maxNo: " + maxNo);
        Log.e(TAG, "delay: " + delay_max);
        Log.e(TAG, "start_delay: " + start_delay);
        Log.e(TAG, "delay_minNum: " + delay_min);
        Log.e(TAG, "tk_num: " + tk_num);
        Log.e(TAG, "----------------: ");
        if (!flag_jh_f1) {
            Log.e(TAG, "getDelay: " + 1);
            if (delay_set.equals("f1")) {//孔间延时
                Log.e(TAG, "getDelay: " + 11);
                if (maxNo == 0) {
                    Log.e(TAG, "getDelay: " + 111);
                    delay_max = start_delay - delay_max;
                    Log.e(TAG, "start_delay: " + start_delay);
                    Log.e(TAG, "delay: " + delay_max);
                } else {
                    Log.e(TAG, "getDelay: " + 112);
                    if (flag_tk) {
                        Log.e(TAG, "getDelay: " + 1121);
                        delay_max = delay_min - f1 * (tk_num + 1);
                    } else {
                        Log.e(TAG, "getDelay: " + 1122);
                        delay_max = delay_min - f1;
                    }

                }
            } else if (delay_set.equals("f2")) {//排间延时
                Log.e(TAG, "getDelay: " + 12);
                if (maxNo == 0) {
                    Log.e(TAG, "getDelay: " + 121);
                    delay_max = delay_max + start_delay;
                } else {
                    Log.e(TAG, "getDelay: " + 122);
                    if (flag_tk) {
                        delay_max = delay_min + f2 * (tk_num + 1);
                    } else {
                        delay_max = delay_min + f2;
                    }
                }
            }
        } else {
            Log.e(TAG, "getDelay: " + 2);
            if (delay_set.equals("f1")) {//孔间延时
                if (maxNo == 0) {
                    delay_max = delay_max + start_delay;
                } else {
                    if (flag_tk) {
                        delay_max = delay_max + f1 * (tk_num + 1);
                    } else {
                        delay_max = delay_max + f1;
                    }

                }
                Log.e(TAG, "maxNo: " + maxNo);
                Log.e(TAG, "开始延时: " + start_delay);
                Log.e(TAG, "孔间延时: " + delay_max);
            } else if (delay_set.equals("f2")) {//孔内延时

                if (maxNo == 0) {
                    delay_max = delay_max + start_delay;
                } else {
                    if (flag_tk) {
                        delay_max = delay_min + f2 * (tk_num + 1);
                    } else {
                        delay_max = delay_min + f2;
                    }
                }
                Log.e(TAG, "孔内延时: " + delay_max);
            } else {
                delay_max = start_delay;
            }
        }

        return delay_max;
    }

    private int getDelay_charu(int start_delay, int f1, int f2, int maxNo, int delay_minNum, int tk_num) {
        int delay_max;
        delay_max = db_charu.getDelay();

        if (!flag_jh_f1) {
            if (delay_set.equals("f1")) {//获取最大延时有问题

                if (maxNo == 0) {
                    delay_max = start_delay - delay_max;
                } else if (btn_start) {
                    delay_max = start_delay;
                } else {
                    if (flag_tk) {
                        delay_max = delay_max - f1 * (tk_num + 1);
                    } else {
                        delay_max = delay_max - f1;
                    }
                }
            }
        } else {
            if (delay_set.equals("f1")) {//获取最大延时有问题

                if (maxNo == 0) {
                    delay_max = delay_max + start_delay;
                } else {
                    if (flag_tk) {
                        delay_max = delay_max + f1 * (tk_num + 1);
                    } else {
                        delay_max = delay_max + f1;
                    }
                }
            }
        }


        return delay_max;
    }

    private void updataPaiData() {
        GreenDaoMaster master = new GreenDaoMaster();
        int total = master.queryDetonatorPaiSize(mRegion, paiChoice + "");//有过
        int delay_max_new = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiChoice);//获取该区域 最大序号的延时
        int delay_minNum_new = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, paiChoice);

        Log.e(TAG, "更新排数据 updataPaiData  paiChoice: " + paiChoice);
        Log.e(TAG, "更新排数据 updataPaiData  total: " + total);
        Log.e(TAG, "更新排数据 updataPaiData  delay_max_new: " + delay_max_new);
        Log.e(TAG, "更新排数据 updataPaiData  delay_minNum_new: " + delay_minNum_new);
        choicepaiData = GreenDaoMaster.gePaiData(mRegion, paiChoice + "");
        if (choicepaiData != null) {
            choicepaiData.setSum(total + "");//
            choicepaiData.setDelayMin(delay_minNum_new + "");
            choicepaiData.setDelayMax(delay_max_new + "");

            getDaoSession().getPaiDataDao().update(choicepaiData);
        }

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
        if (cursor != null) {
            cursor.close();
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
        if (cursor != null) {
            cursor.close();
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
            lg_wei = denatorBaseinfo.getDuanNo() + "";
            lg_pai = denatorBaseinfo.getPai() + "";
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
        if (detonatorId.length() == 0) {
            return false;
        }
        Log.e("检查重复的数据", "detonatorId: " + detonatorId);
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list_lg = master.checkRepeatdenatorId(detonatorId.substring(5));
        if (list_lg.size() > 0) {
            lg_No = list_lg.get(0).getBlastserial() + "";
            lg_Piece = list_lg.get(0).getPiece();
            singleShellNo = list_lg.get(0).getShellBlastNo();
            lg_wei = list_lg.get(0).getDuanNo() + "";
            lg_pai = list_lg.get(0).getPai() + "";
            Log.e("检查重复的数据", "lg_No" + lg_No);
            Log.e("检查重复的数据", "lg_Piece" + lg_Piece);
            Log.e("检查重复的数据", "singleShellNo" + singleShellNo);
            return true;
        } else {
            return false;
        }
    }


    int flag1 = 0;
    int flag2 = 0;
    boolean flag_t1 = true;//同孔标志
    boolean flag_jh_f1 = true;//减号标志
    boolean flag_jh_f2 = true;//减号标志
    boolean btn_start = false;//减号标志
    boolean flag_tk = false;//跳孔标志
    private boolean isSelectAll = true;//是否全选
    private boolean delete_pai = false;//是否删除排

    @OnClick({R.id.btn_scanReister, R.id.btn_f1, R.id.btn_f2, R.id.btn_tk_F1, R.id.btn_JH_F1, R.id.btn_JH_F2, R.id.btn_tk, R.id.btn_setdelay, R.id.btn_input, R.id.btn_single,
            R.id.btn_inputOk, R.id.btn_return, R.id.btn_singleReister, R.id.btn_ReisterScanStart_st, R.id.tv_cancel, R.id.title_right2,
            R.id.btn_ReisterScanStart_ed, R.id.btn_addDelay, R.id.btn_start_delay, R.id.btn_pai, R.id.btn_kong, R.id.btn_wei, R.id.tv_delete, R.id.tv_check_all
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_right2:
                // 创建一个对话框
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popView = inflater.inflate(R.layout.layout_reigst_more, null);
                // 创建 PopupWindow
                PopupWindow popupWindow = new PopupWindow(popView,
                        400,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        true);
                // 显示 PopupWindow 在 TextView 下方
                // 手动输入
                TextView item_1 = popView.findViewById(R.id.item_1);
                // 单发注册
                TextView item_2 = popView.findViewById(R.id.item_2);
                // 授权注册
                TextView item_3 = popView.findViewById(R.id.item_3);
                // 隧道模式
                TextView item_4 = popView.findViewById(R.id.item_4);
                // 创建三个按钮并添加到布局中
                item_1.setOnClickListener(v -> {
                    popupWindow.dismiss();
                    if (checkDelay()) return;
                    btnInputOk.setEnabled(true);
                    isSingleReisher = false;
                    clearText();
                    if (llStart.getVisibility() == View.GONE) {
                        cd_title.setVisibility(View.GONE);
                        llEnd.setVisibility(View.VISIBLE);
                        llStart.setVisibility(View.VISIBLE);
                        llNum.setVisibility(View.VISIBLE);
                        btnReturn.setVisibility(View.VISIBLE);
                        btnInputOk.setVisibility(View.VISIBLE);
                        btnScanReister.setVisibility(View.GONE);
                        lySetDelay.setVisibility(View.GONE);
                        llSingle.setVisibility(View.VISIBLE);
                        btnInput.setText(getResources().getString(R.string.text_return));
                    } else {
                        cd_title.setVisibility(View.VISIBLE);
                        llEnd.setVisibility(View.GONE);
                        llStart.setVisibility(View.GONE);
                        llNum.setVisibility(View.GONE);
                        btnInputOk.setVisibility(View.GONE);
                        llSingle.setVisibility(View.GONE);
                        btnReturn.setVisibility(View.GONE);
                        btnScanReister.setVisibility(View.VISIBLE);
//                    lySetDelay.setVisibility(View.VISIBLE);
                        btnInput.setText(getResources().getString(R.string.text_scan_sdsr));
                    }
                });
                item_2.setOnClickListener(v -> {
                    popupWindow.dismiss();
                    hideInputKeyboard();
                    if (checkDelay()) return;
                    if (isSingleReisher) {
                        show_Toast(getResources().getString(R.string.text_line_tip1));
                        btnInputOk.setEnabled(false);
                        btnSingleReister.setText(getResources().getString(R.string.text_singleReister_stop));
                        isSingleReisher = false;
                        lyXinxi.setVisibility(View.VISIBLE);
                        closeThread();
                        sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令

                    } else {
                        lyXinxi.setVisibility(View.GONE);
                        btnInputOk.setEnabled(true);
                        txtCurrentIC.setTextColor(Color.BLACK);
                        isSingleReisher = true;
                        // 13 退出注册模式
                        sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));
                    }
                });
                item_3.setOnClickListener(v -> {

                    popupWindow.dismiss();
                    if (checkDelay()) return;
                    int mKong = 0;
                    int mWei = 0;
                    //防止当前排没数据时 授权注册崩溃问题
                    if (childListChoice > 0 && childList.get(groupListChoice - 1).size() > 0) {
                        mKong = childList.get(groupListChoice - 1).get(childListChoice - 1).getBlastserial();
                        mWei = childList.get(groupListChoice - 1).get(childListChoice - 1).getDuanNo();
                    }
                    Intent intent = new Intent(this, SouSuoSQActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("paiChoice", paiChoice);//用来判断是否需要展示注册功能
                    bundle.putInt("kongChoice", mKong);//用来判断是否需要展示注册功能
                    bundle.putString("mRegion", mRegion);//用来判断是否需要展示注册功能
                    bundle.putBoolean("flag_jh_f1", flag_jh_f1);
                    bundle.putBoolean("flag_jh_f2", flag_jh_f2);
                    bundle.putBoolean("btn_start", btn_start);
                    bundle.putBoolean("flag_tk", flag_tk);
                    bundle.putString("delay_set", delay_set);
                    bundle.putInt("weiChoice", mWei);
                    Log.e(TAG, "传给搜索界面delay_set: " + delay_set);
                    intent.putExtras(bundle);
                    startActivity(intent);
                });
                item_4.setOnClickListener(v -> {
                    popupWindow.dismiss();
                    startActivity(new Intent(ReisterMainPage_scan.this, SetDelayTime_suidao.class));
                });
                popupWindow.showAsDropDown(titleRight2);
                break;
            case R.id.tv_check_all:
                if (isSelectAll) {
                    tv_check_all.setText(getResources().getString(R.string.text_qxqx));
                    isSelectAll = false;
                    setAllItemChecked(true);
                } else {
                    tv_check_all.setText(getResources().getString(R.string.text_qx));
                    isSelectAll = true;
                    setAllItemChecked(false);
                }

                break;
            case R.id.tv_cancel:
                check_gone = false;
                zhuceAdapter.setCheckBox(true);
                lay_bottom.setVisibility(View.GONE);
                zhuceAdapter.notifyDataSetChanged();
                tv_check_all.setText(getResources().getString(R.string.text_qx));
                isSelectAll = true;
                setAllItemChecked(false);
                break;
            case R.id.tv_delete:
                Log.e(TAG, "点击删除选中雷管: ");
                String checkStr = "";
                List<DenatorBaseinfoSelect> selectChildIdList = new ArrayList<>();
                List<PaiDataSelect> selectgroupIdList = new ArrayList<>();
                if (groupList.size() != 0) {
                    for (PaiDataSelect data : groupList) {
                        if (data.isSelect()) {
                            selectgroupIdList.add(data);
                        }
                    }
                }
                for (int i = 0; i < childList.size(); i++) {
                    for (DenatorBaseinfoSelect data : childList.get(i)) {
                        if (data.isSelect()) {
                            selectChildIdList.add(data);
                        }
                    }
                }
                // 假设 PaiDataSelect 类有 getSum() 方法
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    //选中的数据中是否有没有雷管的排
                    boolean haveNoChildPai = selectgroupIdList.stream()
                            .anyMatch(item -> Integer.parseInt(item.getSum()) == 0);
                    if (haveNoChildPai) {
                        checkStr = "";
                        Log.e(TAG, "当前有排没有子级数据");
                    } else {
                        Log.e(TAG, "当前排都有子级数据--选中的子机数量:" + selectChildIdList.size());
                        if (selectChildIdList.isEmpty()) {
                            checkStr = getResources().getString(R.string.text_selectlg);
                        }
                    }
                }
                if (checkStr.length() > 0) {
                    show_Toast(checkStr);
                    return;
                }
                if (!ReisterMainPage_scan.this.isFinishing()) {
                    AlertDialog dialog = new AlertDialog.Builder(ReisterMainPage_scan.this)
                            .setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题
                            .setMessage(getResources().getString(R.string.text_his_sclg2))//设置对话框的内容
                            //设置对话框的按钮
                            .setNeutralButton(getResources().getString(R.string.text_dialog_qx), (dialog1, which) -> {
                                dialog1.dismiss();
                            })
                            .setPositiveButton(getString(R.string.text_dialog_qd), (dialog14, which) -> {
                                int delete_sum = 0;
                                for (int i = 0; i < childList.size(); i++) {
                                    for (DenatorBaseinfoSelect data : childList.get(i)) {
                                        if (data.isSelect()) {
                                            delete_sum++;
                                            if (data.getShellBlastNo().length() != 13) {
                                                new GreenDaoMaster().deleteDetonator(data.getId());
                                            } else {
                                                new GreenDaoMaster().deleteDetonator(data.getShellBlastNo());
                                            }

                                            Utils.writeRecord("--删除雷管:" + data.getShellBlastNo());
                                        }
                                    }
                                }
                                Log.e(TAG, "paiChoice: " + paiChoice);
                                Log.e(TAG, "kongChoice: " + kongChoice);
                                Log.e(TAG, "groupListChoice: " + groupListChoice);
                                Log.e(TAG, "childListChoice: " + childListChoice);
                                //先更新排数据,再删除排
                                updataPaiData();
                                Log.e(TAG, "groupList1: " + groupList.toString());
                                if (groupList.size() != 0) {
                                    for (PaiDataSelect data : groupList) {
                                        if (data.isSelect()) {
                                            GreenDaoMaster daoMaster = new GreenDaoMaster();
                                            //查询出当前排是否还有雷管
                                            long lgCount = daoMaster.queryLgByPai(data.getPaiId(), data.getQyid());
                                            Log.e(TAG, "Pai表qyId:" + data.getQyid() + "--paiId:"
                                                    + data.getPaiId() + "--雷管数量:" + lgCount);
                                            if (lgCount < 1) {
                                                daoMaster.deletepai(mRegion, data.getId());
                                            }
                                        }
                                    }
                                }
                                if (delete_sum > 0) {
                                    show_Toast(getResources().getString(R.string.text_del_ok));
                                }
                                int a = new GreenDaoMaster().getPaisum(mRegion);
                                int total = new GreenDaoMaster().queryDetonatorPaiSize(mRegion, paiChoice + "");//有过
                                if (paiChoice > total) {//选中删除的地方如果小于雷管总数
                                    if (a != 0) {
                                        paiChoice = a;//重置参数
                                    } else {
                                        paiChoice = 1;//重置参数
                                    }
                                }


                                check_gone = false;//重置参数
                                isSelectAll = true;//重置参数
                                delete_pai=true;
                                mHandler_0.sendMessage(mHandler_0.obtainMessage(1003));// 区域 更新视图
                                tv_check_all.setText(getResources().getString(R.string.text_qx));

                                Utils.saveFile();//把软存中的数据存入磁盘中
                                AppLogUtils.writeAppLog("点击注册页面的多选删除雷管按钮");
                            }).create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }


                break;
            case R.id.btn_pai://加排
                if (paiChoice == 0) {
                    paiChoice = 1;
                }
                creatPai();
                break;
            case R.id.btn_kong://加孔
                flag_add = false;
                insertSingleDenator("");
                break;
            case R.id.btn_wei:
                flag_add = false;
                flag_t1 = false;
                insertSingleDenator("");
                break;
            case R.id.btn_tk:
                int c = getFan(duan_new);
                if (c == 1) {
                    show_Toast(getResources().getString(R.string.text_queryHis_dialog7));
                    return;
                }
                if (etTk.getText().length() == 0) {
                    show_Toast(getResources().getString(R.string.text_sctkgs));
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
                int f = getFan(duan_new);
                if (f == 1) {
                    show_Toast(getResources().getString(R.string.text_queryHis_dialog7));
                    return;
                }
                btnTkF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                if (flag_t1) {
                    btnTkF1.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                    flag_t1 = false;
                } else {
                    btnTkF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                    flag_t1 = true;
                }
                break;
            case R.id.btn_JH_F1:
                btnJHF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                if (flag_jh_f1) {
                    btnJHF1.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                    flag_jh_f1 = false;
                } else {
                    btnJHF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                    flag_jh_f1 = true;
                }
                delay_set = "f1";//是f1还是f2
                reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style);
                reEtF2.setBackgroundResource(R.drawable.translucent);
                flag2 = 0;
                break;
            case R.id.btn_JH_F2:
                btnJHF2.setBackgroundResource(R.drawable.bt_mainpage_style);
                if (flag_jh_f2) {
                    btnJHF2.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                    flag_jh_f2 = false;
                } else {
                    btnJHF2.setBackgroundResource(R.drawable.bt_mainpage_style);
                    flag_jh_f2 = true;
                }
                break;
            case R.id.btn_start_delay:
                btnStartDelay.setBackgroundResource(R.drawable.bt_mainpage_style);
                if (btn_start) {
                    AppLogUtils.writeAppLog("取消选中了起始延时");
                    btnStartDelay.setBackgroundResource(R.drawable.bt_mainpage_style);
                    btn_start = false;
                } else {
                    AppLogUtils.writeAppLog("选中了起始延时");
                    btnStartDelay.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                    btn_start = true;
                }
                break;
            case R.id.btn_scanReister:
//                int d = getFan(duan_new);
//                if (d == 1) {
//                    show_Toast(getResources().getString(R.string.text_queryHis_dialog5));
//                    return;
//                }
//                Log.e(TAG, "是否翻转: " + d);
                if (checkDelay()) return;
                if (f1_delay_data.length() < 1 || f2_delay_data.length() < 1 || start_delay_data.length() < 1||groupList.size()==0) {
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
                    break;
                }
//                if (deleteList()) return;
                container1.requestFocus();//获取焦点,
//                scanDecode.starScan();

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

                    kaishiScan();
                    //kt50持续扫码线程
                    scanBarThread = new ScanBar();
                    scanBarThread.start();
                    btnScanReister.setTextSize(19);
                    btnScanReister.setText(getResources().getString(R.string.text_reister_scaning));//"正在扫码"
                    btnReisterScanStartEd.setEnabled(false);
                    btnReisterScanStartSt.setEnabled(false);
                } else {
                    continueScanFlag = 0;
                    btnScanReister.setTextSize(23);
                    btnScanReister.setText(getResources().getString(R.string.text_reister_scanReister));//"扫码注册"
                    btnReisterScanStartEd.setEnabled(true);
                    btnReisterScanStartSt.setEnabled(true);
                    tingzhiScan();

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
                switch (flag1) {
                    case 0:
                        AppLogUtils.writeAppLog("选中了孔间延时");
                        reBtnF1.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                        reEtF1.setBackgroundResource(R.drawable.textview_border_green);
                        flag1 = 1;
                        break;
                    case 1:
                        AppLogUtils.writeAppLog("取消选中孔间延时");
                        reBtnF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                        reEtF1.setBackgroundResource(R.drawable.translucent);
                        flag1 = 0;
                        break;
                }
                reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style);
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
                        AppLogUtils.writeAppLog("选中了排间延时");
                        reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                        reEtF2.setBackgroundResource(R.drawable.textview_border_green);
                        flag2 = 1;
                        break;
                    case 1:
                        AppLogUtils.writeAppLog("选中了孔排间延时");
                        reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style);
                        reEtF2.setBackgroundResource(R.drawable.translucent);
                        flag2 = 0;
                        break;
                }
                reEtF1.setBackgroundResource(R.drawable.translucent);
                reEtF1.clearFocus();
                reEtF2.clearFocus();

                btnJHF1.setBackgroundResource(R.drawable.bt_mainpage_style);
                flag_jh_f1 = true;

                break;
            case R.id.btn_setdelay:
                int a = getFan(duan_new);
                if (a == 1) {
                    show_Toast(getResources().getString(R.string.text_queryHis_dialog7));
                    return;
                }
                String str3 = "设置延时";//"当前雷管信息"
                Intent intent3 = new Intent(this, SetDelayTime.class);
                intent3.putExtra("dataSend", str3);
                startActivityForResult(intent3, 1);
                break;
            case R.id.btn_single:
                if (btnSingle.getText().toString().equals(getResources().getString(R.string.text_scan_sdsr))) {
                    int b = getFan(duan_new);
                    if (b == 1) {
                        show_Toast(getResources().getString(R.string.text_queryHis_dialog7));
                        return;
                    }
                }
                if (checkDelay()) return;
                if (llStart.getVisibility() == View.GONE) {
                    lySetDelay.setVisibility(View.GONE);
                    llSingle.setVisibility(View.GONE);
                    llStart.setVisibility(View.VISIBLE);
                    llEnd.setVisibility(View.VISIBLE);
                    llNum.setVisibility(View.VISIBLE);
                    btnInputOk.setVisibility(View.VISIBLE);
                    btnScanReister.setVisibility(View.GONE);
                    btnSingle.setText(getResources().getString(R.string.text_return));
                } else {
                    lySetDelay.setVisibility(View.VISIBLE);
                    llSingle.setVisibility(View.VISIBLE);
                    llStart.setVisibility(View.GONE);
                    llEnd.setVisibility(View.GONE);
                    llNum.setVisibility(View.GONE);
                    btnInputOk.setVisibility(View.GONE);
                    btnScanReister.setVisibility(View.VISIBLE);
                    btnSingle.setText(getResources().getString(R.string.text_scan_sdsr));
                }

                break;
            case R.id.btn_input:
                if (llStart.getVisibility() == View.GONE) {
                    cd_title.setVisibility(View.GONE);
                    llEnd.setVisibility(View.VISIBLE);
                    llStart.setVisibility(View.VISIBLE);
                    llNum.setVisibility(View.VISIBLE);
                    btnInputOk.setVisibility(View.VISIBLE);
                    btnScanReister.setVisibility(View.GONE);
                    lySetDelay.setVisibility(View.GONE);
                    btnInput.setText(getResources().getString(R.string.text_return));
                } else {
                    cd_title.setVisibility(View.VISIBLE);
                    llEnd.setVisibility(View.GONE);
                    llStart.setVisibility(View.GONE);
                    llNum.setVisibility(View.GONE);
                    btnInputOk.setVisibility(View.GONE);
                    btnScanReister.setVisibility(View.VISIBLE);
//                    lySetDelay.setVisibility(View.VISIBLE);
                    btnInput.setText(getResources().getString(R.string.text_scan_sdsr));
                }
                break;
            case R.id.btn_inputOk:
                if (f1_delay_data.length() < 1 || start_delay_data.length() < 1 || f2_delay_data.length() < 1||groupList.size()==0) {
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
                        if (Integer.parseInt(addNum) > 1000) {
                            show_Toast(getResources().getString(R.string.text_scan_cuowu7));
                            return;
                        }
                        if (edsno.length() > 1) {
                            show_Toast(getResources().getString(R.string.text_scan_bntssr));
                            return;
                        }
                        num = Integer.parseInt(addNum);//连续注册个数
                        pb_show = 1;
                        runPbDialog();
                        new Thread(() -> {
                            insertDenator(prex, start, start + (num - 1), true);
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
                            show_Toast(getResources().getString(R.string.text_js5));//  "结束序号不能小于开始序号";
                            return;
                        }
                        if (stproDt.length() < 5) {
                            show_Toast(getResources().getString(R.string.text_rq5));//  "结束序号不能小于开始序号";
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
                            insertDenator(prex, start, end, true);
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
//                closeThread();
//                Intent intentTemp = new Intent();
//                intentTemp.putExtra("backString", "");
//                setResult(1, intentTemp);
//                finish();


                if (llStart.getVisibility() == View.GONE) {
                    cd_title.setVisibility(View.GONE);
                    llEnd.setVisibility(View.VISIBLE);
                    llStart.setVisibility(View.VISIBLE);
                    llNum.setVisibility(View.VISIBLE);
                    btnReturn.setVisibility(View.VISIBLE);
                    btnInputOk.setVisibility(View.VISIBLE);
                    btnScanReister.setVisibility(View.GONE);
                    lySetDelay.setVisibility(View.GONE);
                    llSingle.setVisibility(View.VISIBLE);
                    btnInput.setText(getResources().getString(R.string.text_return));
                } else {
                    cd_title.setVisibility(View.VISIBLE);
                    llEnd.setVisibility(View.GONE);
                    llStart.setVisibility(View.GONE);
                    llNum.setVisibility(View.GONE);
                    btnInputOk.setVisibility(View.GONE);
                    llSingle.setVisibility(View.GONE);
                    btnReturn.setVisibility(View.GONE);
                    btnScanReister.setVisibility(View.VISIBLE);
//                    lySetDelay.setVisibility(View.VISIBLE);
                    btnInput.setText(getResources().getString(R.string.text_scan_sdsr));
                }
                break;
            case R.id.btn_singleReister:
                hideInputKeyboard();
                if (checkDelay()) return;
                if (isSingleReisher) {
                    show_Toast(getResources().getString(R.string.text_line_tip1));
                    btnInputOk.setEnabled(false);
                    btnSingleReister.setText(getResources().getString(R.string.text_singleReister_stop));
                    isSingleReisher = false;
                    closeThread();
                    sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令

                } else {
                    btnInputOk.setEnabled(true);
                    btnSingleReister.setText(getResources().getString(R.string.text_singleReister));
                    txtCurrentVolt.setText(getResources().getString(R.string.text_reister_vol));
                    txtCurrentIC.setText(getResources().getString(R.string.text_reister_ele));
                    txtCurrentIC.setTextColor(Color.BLACK);
                    isSingleReisher = true;
                    initCloseCmdReFlag = 0;
                    initOpenCmdReFlag = 0;
                    revCloseCmdReFlag = 0;
                    revOpenCmdReFlag = 0;
                    // 13 退出注册模式
                    sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));
                }
                break;
            case R.id.btn_ReisterScanStart_st:
                hideInputKeyboard();
                if (continueScanFlag == 0) {
                    continueScanFlag = 1;
                    kaishiScan();//启动扫描
                } else {
                    continueScanFlag = 0;
                    tingzhiScan();//停止扫描
                }
                sanButtonFlag = 1;
                break;
            case R.id.btn_ReisterScanStart_ed:
                hideInputKeyboard();
                if (continueScanFlag == 0) {
                    continueScanFlag = 1;
                    kaishiScan();//启动扫描
                } else {
                    continueScanFlag = 0;
                    tingzhiScan();//停止扫描
                }
                sanButtonFlag = 2;
                break;

            case R.id.btn_addDelay:
//                maxDuanNo = maxDuanNo + 1;
//                setView(maxDuanNo);

                Intent intent = new Intent(this, ChoseDuanActivity.class);
                intentActivityResultLauncher.launch(intent);
                break;


        }
    }

    private void creatPai() {

        myDialog = new MyAlertDialog(this).builder();
        int maxPai = new GreenDaoMaster().getMaxPaiId(mRegion);
        EditText kongSun = myDialog.getView().findViewById(R.id.txt_fa);
        EditText startDelay = myDialog.getView().findViewById(R.id.txt_startDelay);
        EditText kongDelay = myDialog.getView().findViewById(R.id.txt_kongDelay);
        EditText neiDelay = myDialog.getView().findViewById(R.id.txt_paiDelay);
        SwitchButton sw_dijian = myDialog.getView().findViewById(R.id.sw_dijian);
        int delay_min_new = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, paiMax);//获取该区域 前一排的最小延时
        int delay_max_new = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiMax);//获取该区域 前一排的最大延时
        PaiData paiData1 = GreenDaoMaster.gePaiData(mRegion, paiMax + "");
        int paiDelay = Integer.parseInt(quYu_choice.getPaiDelay());
        Log.e(TAG, "paiMax: " + paiMax);
        Log.e(TAG, "delay_min_new: " + delay_min_new);
        Log.e(TAG, "quYu_choice.getPaiDelay(): " + quYu_choice.getPaiDelay());
        if (paiMax == 0) {
            startDelay.setText("0");
        } else {
            startDelay.setText((delay_min_new + Integer.parseInt(quYu_choice.getPaiDelay())) + "");
        }
        sw_dijian.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (sw_dijian.isChecked()) {
                    if (paiMax == 0) {
                        startDelay.setText(delay_max_new + "");
                    } else {
                        startDelay.setText((delay_max_new + Integer.parseInt(quYu_choice.getPaiDelay())) + "");
                    }
                } else {
                    if (paiMax == 0) {
                        startDelay.setText("0");
                    } else {
                        startDelay.setText((delay_min_new + Integer.parseInt(quYu_choice.getPaiDelay())) + "");
                    }
                }
            }
        });
        kongDelay.setText(quYu_choice.getKongDelay());

        myDialog.setGone()
                .setTitle("新增第" + (maxPai + 1) + "排设置")
                .setStart()
                .setFa()
                .setDijian()
                .setpaiText("孔内延时")
                .setCancelable(false)
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                })
                .setPositiveButton("确定", v -> {


//                    Log.e("打印", "name: " + name.getText());
                    Log.e("新建排", "kongSun: " + kongSun.getText().toString());
                    Log.e("新建排", "startDelay: " + startDelay.getText().toString());
                    Log.e("新建排", "kongDelay: " + kongDelay.getText().toString());
                    Log.e("新建排", "neiDelay: " + neiDelay.getText().toString());
                    Log.e("新建排", "sw_dijian: " + sw_dijian.isChecked());
                    PaiData paiData = new PaiData();
                    paiData.setPaiId((maxPai + 1));
                    paiData.setQyid(Integer.parseInt(mRegion));
                    paiData.setStartDelay(startDelay.getText().toString());
                    paiData.setKongNum(Integer.parseInt(kongSun.getText().toString()));
                    paiData.setKongDelay(kongDelay.getText().toString());
                    paiData.setNeiDelay(neiDelay.getText().toString());
                    paiData.setDiJian(sw_dijian.isChecked());
                    paiData.setPaiDelay(quYu_choice.getPaiDelay());
                    paiData.setDelayMin("0");
                    paiData.setDelayMax("0");
                    paiData.setSum("0");
                    getDaoSession().getPaiDataDao().insert(paiData);
                    groupListChoice = 0;//重置选中的排
                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));// 区域 更新视图

                    SoundPlayUtils.play(1);
                }).show();
    }

    private void kaishiScan() {
        switch (Build.DEVICE) {
            case "T-QBZD-Z6":
            case "M900": {
                //M900打开扫码
                mScaner.startScan();
                break;
            }
            case "ST327":
            case "S337": {
                //st327上电
                powerOnScanDevice(PIN_TRACKER_EN);//扫码头上电
                break;
            }
            default: {
                scanDecode.starScan();

            }
        }
    }

    private void tingzhiScan() {
        switch (Build.DEVICE) {
            case "T-QBZD-Z6":
            case "M900": {
                //M900关闭扫码
                mScaner.stopScan();
                break;
            }
            case "ST327":
            case "S337": {
                //st327扫码下电
                powerOffScanDevice(PIN_TRACKER_EN);//扫码头下电
                break;
            }
            default: {
                //kt50停止扫码头方法
                scanDecode.stopScan();//停止扫描
            }
        }
    }

    private boolean checkDelay() {
        Log.e(TAG, "扫码检测 paiChoice: " + paiChoice);
        if (groupList.size() == 0) {
            Log.e(TAG, "扫码检测 没有排: ");
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
            return true;
        }
        if (f1_delay_data.toString().equals("")) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
            Log.e("f2", reEtF2.getText().toString());
            return true;
        }
        if (f2_delay_data.toString().equals("")) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));

            Log.e("f2", reEtF2.getText().toString());
            return true;
        }
        if (start_delay_data.length() == 0||groupList.size()==0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
            return true;
        }
        if (maxSecond != 0) {
            if (Integer.parseInt(reEtF2.getText().toString()) > maxSecond) {
                show_Toast(getResources().getString(R.string.text_line_tip3) + maxSecond + getResources().getString(R.string.text_line_tip4));
                return true;
            }
            if (Integer.parseInt(start_delay_data.toString()) > maxSecond) {
                show_Toast(getResources().getString(R.string.text_line_tip3) + maxSecond + getResources().getString(R.string.text_line_tip4));
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

                insertDenator(prex, start, start + (num - 1), true);

            }

        }
    }


    private class ScanBar extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    switch (Build.DEVICE) {
                        case "T-QBZD-Z6":
                        case "M900": {
                            mScaner.startScan();
                            break;
                        }
                        default: {
                            scanDecode.starScan();
                        }
                    }
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
                String gkm = editScanChangjia.getText().toString() + editScanRiqi.getText().toString()
                        + editScanTezheng.getText().toString() + editScanHehao.getText().toString()
                        + editScanLiushui.getText().toString();
//                insertSingleDenator(gkm);
                insertSingleDenator_2(gkm, "", "", "1", "0");//同时注册管壳码和芯片码
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
            show_Toast(getResources().getString(R.string.text_scan_cuowu11));
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
                show_Toast(getResources().getString(R.string.text_scan_cuowu12));
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
     * 重置控件
     */
    private void resetView_start() {
        btn_start = false;
        et_startDelay.setBackgroundResource(R.drawable.translucent);
        btnStartDelay.setBackgroundResource(R.drawable.bt_mainpage_style);
        et_startDelay.clearFocus();
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
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
        if (checkDelay()) {
            return true;
        }
        switch (item.getItemId()) {

            case R.id.item_1://手动输入

                clearText();
                if (llStart.getVisibility() == View.GONE) {
                    cd_title.setVisibility(View.GONE);
                    llEnd.setVisibility(View.VISIBLE);
                    llStart.setVisibility(View.VISIBLE);
                    llNum.setVisibility(View.VISIBLE);
                    btnReturn.setVisibility(View.VISIBLE);
                    btnInputOk.setVisibility(View.VISIBLE);
                    btnScanReister.setVisibility(View.GONE);
                    lySetDelay.setVisibility(View.GONE);
                    llSingle.setVisibility(View.VISIBLE);
                    btnInput.setText(getResources().getString(R.string.text_return));
                } else {
                    cd_title.setVisibility(View.VISIBLE);
                    llEnd.setVisibility(View.GONE);
                    llStart.setVisibility(View.GONE);
                    llNum.setVisibility(View.GONE);
                    btnInputOk.setVisibility(View.GONE);
                    llSingle.setVisibility(View.GONE);
                    btnReturn.setVisibility(View.GONE);
                    btnScanReister.setVisibility(View.VISIBLE);
//                    lySetDelay.setVisibility(View.VISIBLE);
                    btnInput.setText(getResources().getString(R.string.text_scan_sdsr));
                }
                return true;
            case R.id.item_2://单发输入
                hideInputKeyboard();
                if (checkDelay()) {
                    return true;
                }

                if (isSingleReisher) {
                    show_Toast(getResources().getString(R.string.text_line_tip1));
                    btnInputOk.setEnabled(false);
                    btnSingleReister.setText(getResources().getString(R.string.text_singleReister_stop));
                    isSingleReisher = false;
                    lyXinxi.setVisibility(View.VISIBLE);
                    closeThread();
                    sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令

                } else {
                    lyXinxi.setVisibility(View.GONE);
                    btnInputOk.setEnabled(true);
                    txtCurrentIC.setTextColor(Color.BLACK);
                    isSingleReisher = true;
                    // 13 退出注册模式
                    sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));
                }

                return true;
            case R.id.item_3://授权注册
                Intent intent = new Intent(this, SouSuoSQActivity.class);
                Bundle bundle = new Bundle();

                bundle.putInt("paiChoice", paiChoice);//用来判断是否需要展示注册功能
                bundle.putString("mRegion", mRegion);//用来判断是否需要展示注册功能
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.item_4:
                startActivity(new Intent(ReisterMainPage_scan.this, SetDelayTime_suidao.class));
                return true;
//            case R.id.item_5:
//                // 区域 更新视图
//                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
//
//                // 显示提示
//                show_Toast(getResources().getString(R.string.text_show_1) + mRegion);
//                // 延时选择重置
//                resetView();
//                delay_set = "0";
//
//                duan_new = 1;
//                MmkvUtils.savecode("duan", 1);
//                btnAddDelay.setText(getResources().getString(R.string.text_reister_dw) + duan_new);
//                AppLogUtils.writeAppLog("已选中区域" + mRegion);
//                return true;

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
            str = getResources().getString(R.string.text_list_piace) + "  " + region;
        } else {
            str = getResources().getString(R.string.text_list_piace) + "  " + region;//+ "(" + getResources().getString(R.string.text_main_sl) + ": " + size + ")"
        }
        // 设置标题
        getSupportActionBar().setTitle(mOldTitle + str);
        title_lefttext.setText(mOldTitle + str);
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
        if (oriDateStr.equals("02-29")) {
            return true;
        }
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


    private void fanzhuan(int duan) {
        Log.e("注册页面", "翻转: ");
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.text_fzts))//设置对话框的标题//"成功起爆"
                .setMessage(getResources().getString(R.string.text_ssfz))//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                .setNegativeButton(getResources().getString(R.string.text_alert_cancel), (dialog12, which) -> dialog12.dismiss())
                .setPositiveButton(getResources().getString(R.string.text_alert_sure), (dialog1, which) -> {

                    GreenDaoMaster master = new GreenDaoMaster();
                    List<DenatorBaseinfo> list = master.queryLeiguanDuan(duan, mRegion);
                    List<DenatorBaseinfo> list2 = master.queryLeiguanDuan(duan, mRegion);
                    for (int i = 0; i < list.size(); i++) {
                        DenatorBaseinfo lg = list.get(i);
                        lg.setDelay(list2.get(list.size() - 1 - i).getDelay());
                        getDaoSession().getDenatorBaseinfoDao().update(lg);
                    }
                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                }).create();

        dialog.show();
    }


    private void setFan() {
        n1 = (int) MmkvUtils.getcode(mRegion + "n1", 0);
        n2 = (int) MmkvUtils.getcode(mRegion + "n2", 0);
        n3 = (int) MmkvUtils.getcode(mRegion + "n3", 0);
        n4 = (int) MmkvUtils.getcode(mRegion + "n4", 0);
        n5 = (int) MmkvUtils.getcode(mRegion + "n5", 0);
        n6 = (int) MmkvUtils.getcode(mRegion + "n6", 0);
        n7 = (int) MmkvUtils.getcode(mRegion + "n7", 0);
        n8 = (int) MmkvUtils.getcode(mRegion + "n8", 0);
        n9 = (int) MmkvUtils.getcode(mRegion + "n9", 0);
        n10 = (int) MmkvUtils.getcode(mRegion + "n10", 0);
        n11 = (int) MmkvUtils.getcode(mRegion + "n11", 0);
        n12 = (int) MmkvUtils.getcode(mRegion + "n12", 0);
        n13 = (int) MmkvUtils.getcode(mRegion + "n13", 0);
        n14 = (int) MmkvUtils.getcode(mRegion + "n14", 0);
        n15 = (int) MmkvUtils.getcode(mRegion + "n15", 0);
        n16 = (int) MmkvUtils.getcode(mRegion + "n16", 0);
        n17 = (int) MmkvUtils.getcode(mRegion + "n17", 0);
        n18 = (int) MmkvUtils.getcode(mRegion + "n18", 0);
        n19 = (int) MmkvUtils.getcode(mRegion + "n19", 0);
        n20 = (int) MmkvUtils.getcode(mRegion + "n20", 0);
        n21 = (int) MmkvUtils.getcode(mRegion + "n21", 0);
        n22 = (int) MmkvUtils.getcode(mRegion + "n22", 0);
        n23 = (int) MmkvUtils.getcode(mRegion + "n23", 0);
        n24 = (int) MmkvUtils.getcode(mRegion + "n24", 0);
        n25 = (int) MmkvUtils.getcode(mRegion + "n25", 0);
        n26 = (int) MmkvUtils.getcode(mRegion + "n26", 0);
        n27 = (int) MmkvUtils.getcode(mRegion + "n27", 0);
        n28 = (int) MmkvUtils.getcode(mRegion + "n28", 0);
        n29 = (int) MmkvUtils.getcode(mRegion + "n29", 0);
        n30 = (int) MmkvUtils.getcode(mRegion + "n30", 0);
        n31 = (int) MmkvUtils.getcode(mRegion + "n31", 0);
        n32 = (int) MmkvUtils.getcode(mRegion + "n32", 0);
        n33 = (int) MmkvUtils.getcode(mRegion + "n33", 0);
        n34 = (int) MmkvUtils.getcode(mRegion + "n34", 0);
        n35 = (int) MmkvUtils.getcode(mRegion + "n35", 0);
        n36 = (int) MmkvUtils.getcode(mRegion + "n36", 0);
        n37 = (int) MmkvUtils.getcode(mRegion + "n37", 0);
        n38 = (int) MmkvUtils.getcode(mRegion + "n38", 0);
        n39 = (int) MmkvUtils.getcode(mRegion + "n39", 0);
        n40 = (int) MmkvUtils.getcode(mRegion + "n40", 0);
    }

    /**
     * 获取对应段位翻转值
     */
    public int getFan(int duan) {
        setFan();
        switch (duan) {
            case 1:
                return n1;
            case 2:
                return n2;
            case 3:
                return n3;
            case 4:
                return n4;
            case 5:
                return n5;
            case 6:
                return n6;
            case 7:
                return n7;
            case 8:
                return n8;
            case 9:
                return n9;
            case 10:
                return n10;
            case 11:
                return n11;
            case 12:
                return n12;
            case 13:
                return n13;
            case 14:
                return n14;
            case 15:
                return n15;
            case 16:
                return n16;
            case 17:
                return n17;
            case 18:
                return n18;
            case 19:
                return n19;
            case 20:
                return n20;
            case 21:
                return n21;
            case 22:
                return n22;
            case 23:
                return n23;
            case 24:
                return n24;
            case 25:
                return n25;
            case 26:
                return n26;
            case 27:
                return n27;
            case 28:
                return n28;
            case 29:
                return n29;
            case 30:
                return n30;
            case 31:
                return n31;
            case 32:
                return n32;
            case 33:
                return n33;
            case 34:
                return n34;
            case 35:
                return n35;
            case 36:
                return n36;
            case 37:
                return n37;
            case 38:
                return n38;
            case 39:
                return n39;
            case 40:
                return n40;

        }
        return 0;
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


    /**
     * 查询生产表中对应的管壳码
     */
    private String serchShellBlastNo(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryDetonatorTypeNew(denatorId);
    }


    //区域全选和取消全选
    private void setAllItemChecked(boolean isSelected) {
//        if (quyuAdapter == null) return;
        if (isSelected) {
            for (int i = 0; i < childList.size(); i++) {
                for (DenatorBaseinfoSelect data : childList.get(i)) {
                    data.setSelect(true);
                }
            }

        } else {
            for (int i = 0; i < childList.size(); i++) {
                for (DenatorBaseinfoSelect data : childList.get(i)) {
                    data.setSelect(false);
                }
            }
        }

        if (isSelected) {
            for (PaiDataSelect data : groupList) {
                data.setSelect(true);
            }
        } else {
            for (PaiDataSelect data : groupList) {
                data.setSelect(false);
            }
        }
        zhuceAdapter.notifyDataSetChanged();
    }


    private void setDalay(boolean dijia, int startNoStr, int endNoStr, String holeDeAmoStr, String startDelayStr, String holeinDelayStr, String holeBetweentStr) {
        hideInputKeyboard();
        String checstr = null;
        if (checstr == null || checstr.trim().length() < 1) {
            int maxDelay = getComputerDenDelay(dijia, startNoStr, endNoStr, holeDeAmoStr, startDelayStr, holeinDelayStr, holeBetweentStr);
            Log.e("延时1", "maxDelay: " + maxDelay);//9010
            Log.e("延时2", "maxSecond: " + maxSecond);//5000
            if (maxSecond >= 0 && maxSecond < maxDelay) {
                show_Toast(getResources().getString(R.string.text_setDelay_dialog5) + maxSecond + getResources().getString(R.string.text_setDelay_dialog6));
                return;
            }
            if (maxDelay < 0) {
                show_Toast("延时不能小于0ms");
                return;
            }
            pb_show = 1;
            runPbDialog();
            new Thread(() -> setDenatorDelay(dijia, startNoStr, endNoStr, holeDeAmoStr, startDelayStr, holeinDelayStr, holeBetweentStr)).start();
            show_Toast(getString(R.string.text_error_tip36));
        } else {
            show_Toast(checstr);
        }
    }

    /**
     * 获取总延时值
     */
    private int getComputerDenDelay(boolean dijia, int startNoStr, int endNoStr, String holeDeAmoStr, String startDelayStr, String holeinDelayStr, String holeBetweentStr) {

//        //起始序号
//        String startNoStr = startNoTxt.getText().toString();
//        //终点序号
//        String endNoStr = endNoTxt.getText().toString();
//        //孔内雷管数
//        String holeDeAmoStr = holeDeAmoTxt.getText().toString();
//        //开始延时
//        String startDelayStr = startDelayTxt.getText().toString();
//        //孔内延时
//        String holeinDelayStr = holeinDelayTxt.getText().toString();
//        //孔间延时
//        String holeBetweentStr = holeBetweentTxt.getText().toString();

        int start = startNoStr;
        int end = endNoStr;
        int holeDeAmo = Integer.parseInt(holeDeAmoStr);
        int startDelay = Integer.parseInt(startDelayStr);
        int holeinDelay = Integer.parseInt(holeinDelayStr);
        int holeBetweent = Integer.parseInt(holeBetweentStr);
        int holeLoop = 1;
        int delayCount = startDelay;
        for (int iLoop = start; iLoop < end; iLoop++) {

            //int isExist = isDel(""+iLoop);

            for (int i = 1; i <= holeDeAmo; i++) {
                if (dijia) {
                    if (i < holeDeAmo) {
                        delayCount += holeinDelay;
                        iLoop++;
                    }
                } else {
                    if (i < holeDeAmo) {
                        delayCount -= holeinDelay;
                        iLoop++;
                    }
                }

                if (iLoop > end) break;
            }
            holeLoop++;
            if (dijia) {
                delayCount += holeBetweent;
            } else {
                delayCount -= holeBetweent;
            }

        }
        return delayCount;
    }

    private void setDenatorDelay(boolean dijia, int start, int end, String holeDeAmoStr, String startDelayStr, String holeinDelayStr, String holeBetweentStr) {
//        Log.e("设置延时", "递加 dijia: " + dijia);
//        Log.e("设置延时", "start: " + start);
//        Log.e("设置延时", "end: " + end);
//        Log.e("设置延时", "孔内雷管总数数holeDeAmoStr: " + holeDeAmoStr);
//        Log.e("设置延时", "开始延时startDelayStr: " + startDelayStr);
//        Log.e("设置延时", "孔内延时holeinDelayStr: " + holeinDelayStr);
//        Log.e("设置延时", "孔间延时holeBetweentStr: " + holeBetweentStr);
        int holeDeAmo = Integer.parseInt(holeDeAmoStr);
        int startDelay = Integer.parseInt(startDelayStr);
        int holeinDelay = Integer.parseInt(holeinDelayStr);
        int holeBetweent = Integer.parseInt(holeBetweentStr);
        int holeLoop = 1;
        int delayCount = startDelay;
        List<DenatorBaseinfoSelect> list_lg = childList.get(groupListChoice - 1);

        for (int i = 0; i < list_lg.size(); i++) {

            ContentValues values = new ContentValues();

            if (dijia) {
                delayCount = startDelay + (list_lg.get(i).getBlastserial() - 1) * holeBetweent + (list_lg.get(i).getDuanNo() - 1) * holeinDelay;
            } else {
                delayCount = startDelay - (list_lg.get(i).getBlastserial() - 1) * holeBetweent - (list_lg.get(i).getDuanNo() - 1) * holeinDelay;

            }
            values.put("delay", delayCount);
            Log.e("设置延时", "ShellBlastNo:" + list_lg.get(i).getShellBlastNo() + " 更新delay: " + delayCount);

            db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "id =? and piece =? and pai =? ", new String[]{list_lg.get(i).getId() + "", mRegion, paiChoice + ""});


        }
        pb_show = 0;
        mHandler_2.sendMessage(mHandler_2.obtainMessage());
        Utils.saveFile();//把软存中的数据存入磁盘中
//        getLoaderManager().restartLoader(1, null, SetDelayTime.this);
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        AppLogUtils.writeAppLog("--设置延时:起始序号:" + start + ",终点序号:" + end + ",孔内雷管数:" + holeDeAmoStr
                + ",开始延时:" + startDelayStr + ",孔内延时:" + holeinDelayStr + ",孔间延时:" + holeBetweentStr);
        Utils.writeRecord("--设置延时:起始序号:" + start + ",终点序号:" + end + ",孔内雷管数:" + holeDeAmoStr
                + ",开始延时:" + startDelayStr + ",孔内延时:" + holeinDelayStr + ",孔间延时:" + holeBetweentStr);
    }


}
