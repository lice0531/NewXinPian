package android_serialport_api.xingbang.firingdevice;


import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_TRACKER_EN;

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
import android.os.Build;
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
import android.widget.TextView;

import com.kfree.comm.system.ScanQrControl;
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
public class ReisterMainPage_line extends SerialPortActivity implements LoaderCallbacks<Cursor> {

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
    EditText setDelayTimeStartDelaytime;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.re_btn_f1)
    Button reBtnF1;
    @BindView(R.id.re_et_f1)
    EditText reEtF1;
    @BindView(R.id.re_btn_f2)
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
    RecyclerView mListView;
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
    private ScanQrControl mScaner = null;
    private int xiangHao_errNum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reister_main_page_line);
        ButterKnife.bind(this);
        SoundPlayUtils.init(this);

        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
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

    }

    private void initView() {
        //         获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
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
            modifyBlastBaseInfo(no, delay, shellBlastNo,info.getDenatorId());
        });
        this.isSingleReisher = 0;
    }

    private void getUserMessage() {
        MessageBean messageBean = GreenDaoMaster.getAllFromInfo_bean();
        qiaosi_set = messageBean.getQiaosi_set();
        version = messageBean.getVersion() + "";
    }

    private void scan() {
        switch (Build.DEVICE) {
            // KT50 起爆器设备
            case "M900": {
                // 创建扫描头操作对象，并注册回调
                mScaner = new ScanQrControl(this);
                mScaner.registerScanCb(new ScanQrControl.IScan() {
                    @Override
                    public void onScanStart(int timeoutSec) {
                    }

                    @Override
                    public void onScanResult(boolean isSuccess, String scanResultStr) {
                        Log.e( "扫码结果: ", "ScanResult:" + isSuccess + "|" + scanResultStr);
                        saoma(scanResultStr);
                    }
                });
                break;
            }
            default:{
                scanDecode = new ScanDecode(this);
                scanDecode.initService("true");//初始化扫描服务

                scanDecode.getBarCode(data -> {
                    Log.e("触发扫码", "scan: " );
                    saoma(data);
                });
            }
        }
    }

    private void saoma(String data) {
        Log.e("扫码", "data: " + data);
        Log.e("扫码", "data.length(): " + data.length());
//        if (deleteList()) return;
        hideInputKeyboard();//隐藏光标
        //根据二维码长度判断新旧版本,兼容01一代,02二代芯片

        if (data.length() == 19) {//扫描盒号
            addHeHao(data);
        }if (data.length() == 18) {//扫描箱号
            addXiangHao(data);
        } else {
            if (sanButtonFlag > 0) {//扫码结果设置到输入框里
                Log.e("扫码注册", "data: " + data);
                decodeBar(data);
                Message msg = new Message();
                msg.obj = data;
                msg.what = 9;
                mHandler_tip.sendMessage(msg);
                tingzhiScan();
//                scanDecode.stopScan();
            } else {
                String barCode;
                String denatorId;
                if (data.length() == 30) {//5620302H00001A62F400FFF20AB603
                    barCode = data.substring(0, 13);
                    denatorId = data.substring(13, 26);
                    insertSingleDenator_2(barCode, denatorId, data.substring(26));//同时注册管壳码和芯片码
                } else if (data.length() == 28) {
                    Log.e("扫码", "data: " + data);
                    //5620302H00001A62F400FFF20AB603
                    //5420302H00001A6F4FFF20AB603
                    //Y5620413H00009A630FD74D87604
                    //M5621132A9999900F491EF8B0922
                    if (data.charAt(0) == 'Y') {
                        barCode = data.substring(1, 14);
                        String a = data.substring(14, 24);
                        denatorId = a.substring(0, 2) + "2" + a.substring(2, 4) + "00" + a.substring(4);
                        Log.e("扫码", "barCode: " + barCode);
                        Log.e("扫码", "denatorId: " + denatorId);
                        Log.e("扫码", "data.substring(24): " + data.substring(24));
                        insertSingleDenator_2(barCode, denatorId, data.substring(24));//同时注册管壳码和芯片码
                    }else {//其他规则
                        barCode = data.substring(0, 13);
                        denatorId = "A621" + data.substring(13, 22);
                        String yscs = data.substring(22, 26);
                        Log.e("扫码", "barCode: " + barCode);
                        Log.e("扫码", "denatorId: " + denatorId);
                        Log.e("扫码", "data.substring(24): " + data.substring(24));
                        insertSingleDenator_2(barCode, denatorId, yscs);//同时注册管壳码和芯片码
                    }

                }
//                    else if{
//                        barCode = getContinueScanBlastNo(data);//VR:1;SC:5600508H09974;
//                        insertSingleDenator(barCode);
//                    }
                hideInputKeyboard();//隐藏光标
            }
        }
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
            insertDenator(prex, finalStrNo, finalEndNo,true);//添加
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
        int a = Integer.parseInt(xh[5] + "" + xh[6]) ;//代表几盒 10
        //                                 S
        int endNo = Utils.XiangHao(xh[7]+"");//判断每盒几发   8
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
            for (int b =0;b<a;b++){
                String xuhao=xh[15] + "" + xh[16]+b+"00";
                int finalStrNo =Integer.parseInt(xuhao);
                insertDenator(prex, finalStrNo, finalStrNo + (endNo - 1),false);//添加
            }

        }).start();
        Log.e("提示", "xiangHao_errNum: "+xiangHao_errNum );
        if(xiangHao_errNum!=0){
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(20));
        }
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
                case 1003:
                    if (busInfo != null) {
                        txt_currentVolt.setText(getResources().getString(R.string.text_reister_vol) + busInfo.getBusVoltage() + "V");
                        txt_currentIC.setText(getResources().getString(R.string.text_reister_ele)+ Math.round(busInfo.getBusCurrentIa() * 1000) + "μA");
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
                show_Toast(getString(R.string.text_reister_tip5) + maxSecond + "ms");
            } else if (msg.what == 4) {
                SoundPlayUtils.play(4);
                show_Toast_long("与" + lg_Piece + "区第" + lg_No + "发" + singleShellNo + "重复");
                int total = showDenatorSum();
//                reisterListView.setSelection(total - Integer.parseInt(lg_No));
                MoveToPosition(linearLayoutManager, mListView, total - Integer.parseInt(lg_No));
            } else if (msg.what == 6) {
                SoundPlayUtils.play(4);
                show_Toast(getString(R.string.text_line_tip7));
            } else if (msg.what == 10) {
                show_Toast(getString(R.string.text_line_tip8));
            }else if (msg.what == 20) {
                SoundPlayUtils.play(4);
                show_Toast("共有"+xiangHao_errNum+"盒重复");
                xiangHao_errNum=0;
            } else if (msg.what == 99) {
                adapter.notifyDataSetChanged();
            } else if (msg.what == 2001) {
                show_Toast(msg.obj.toString());
                SoundPlayUtils.play(4);
            } else {
                SoundPlayUtils.play(4);
                show_Toast(getString(R.string.text_line_tip9));
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
                show_Toast(getString(R.string.text_line_tip10));
            }
            if (tipInfoFlag == 4) {//未收到打开电源命令
                show_Toast(getResources().getString(R.string.text_error_tip6));
            }
            if (tipInfoFlag == 5) {//桥丝不正常
                show_Toast(getResources().getString(R.string.text_error_tip7));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 6) {//桥丝不正常
                show_Toast(getString(R.string.text_line_tip11));
            }
            if (tipInfoFlag == 7) {//桥丝不正常
                show_Toast(getString(R.string.text_line_tip12));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 8) {//桥丝不正常
                show_Toast(getString(R.string.text_line_tip13));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 9) {//桥丝不正常
                show_Toast(getString(R.string.text_line_tip14));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 88) {//刷新界面
                showDenatorSum();
                edit_start_entboxNoAndSerial_st.getText().clear();
                edit_end_entboxNoAndSerial_ed.getText().clear();//.setText("")
            }
            if (tipInfoFlag == 89) {//刷新界面
                show_Toast(getString(R.string.text_line_tip15));
                showDenatorSum();
                SoundPlayUtils.play(4);
            }

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
            setDelayTimeStartDelaytime.setText(save_start);
        } else {
            setDelayTimeStartDelaytime.setText("10");
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
            mAdapter.setOnItemLongClick(position -> {
                DenatorBaseinfo info = mListData.get(position);
                int no = info.getBlastserial();
                int delay = info.getDelay();
                String shellBlastNo = info.getShellBlastNo();
                // 序号 延时 管壳码
                modifyBlastBaseInfo(no, delay, shellBlastNo,info.getDenatorId());
            });
            mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        });
    }


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

        edit_start_entboxNoAndSerial_st = (EditText) this.findViewById(R.id.entboxNoAndSerial_st);//开始流水号
        edit_start_entboxNoAndSerial_st.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                displayInputKeyboard(v, hasFocus);
            }
        });

        container1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != ReisterMainPage_line.this.getCurrentFocus()) {
                    container1.setFocusable(true);
                    container1.setFocusableInTouchMode(true);
                    container1.requestFocus();
                    /**
                     * 点击空白位置 隐藏软键盘
                     */
                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    return mInputMethodManager.hideSoftInputFromWindow(ReisterMainPage_line.this.getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
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
        txtReisteramount.setText(getString(R.string.text_reister_tip1) + list.size());
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        args = new Bundle();
        args.putString("key", "1");
        return new MyLoad(this, args);
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
        if(mScaner!=null){
            mScaner.unregisterScanCb();
        }
        if(scanDecode!=null){
            scanDecode.stopScan();//停止扫描
            scanDecode.onDestroy();//回复初始状态
        }
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
//        show_Toast(Temp + "处理");
        return super.onContextItemSelected(item);
    }

    private void modifyBlastBaseInfo(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle(R.string.text_reister_tip6);
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
            show_Toast(getString(R.string.text_error_tip3) + a + getString(R.string.text_regist_tip7) + b);
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> {

        });
        builder.show();

    }

    /**
     * 修改雷管延期 弹窗
     */
    private void modifyBlastBaseInfo(int no, int delay, final String shellBlastNo, final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delaymodifydialog, null);
        builder.setView(view);

        EditText et_no = view.findViewById(R.id.serialNo);
        EditText et_shell = view.findViewById(R.id.denatorNo);
        EditText et_delay = view.findViewById(R.id.delaytime);
        EditText et_uid = view.findViewById(R.id.UIDNo);

        et_no.setText(String.valueOf(no));
        et_delay.setText(String.valueOf(delay));
        et_shell.setText(shellBlastNo);
        et_uid.setText(uid);
        builder.setNegativeButton(R.string.text_dialog_qx, (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton(R.string.text_dialog_sc, (dialog, which) -> {
            dialog.dismiss();

            // TODO 开启进度条

            new Thread(() -> {
                // 删除某一发雷管
                new GreenDaoMaster().deleteDetonator(shellBlastNo);
                Utils.deleteData(mRegion);//重新排序雷管
                Utils.writeRecord("--删除雷管:" + shellBlastNo);
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1002));

            }).start();

        });
        builder.setPositiveButton(R.string.text_dialog_qd, (dialog, which) -> {
            String delay1 = et_delay.getText().toString();

            if (delay1.trim().length() < 1 || maxSecond > 0 && Integer.parseInt(delay1) > maxSecond) {
                show_Toast(getString(R.string.text_reister_tip8));

            } else if (maxSecond != 0 && Integer.parseInt(delay1) > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2001, getString(R.string.text_reister_tip9) + maxSecond + "ms"));

            } else {
                Utils.writeRecord("-单发修改延时:" + "-管壳码:" + shellBlastNo + "-延时:" + delay1);
                // 修改雷管延时
                new GreenDaoMaster().updateDetonatorDelay(shellBlastNo, Integer.parseInt(delay1));

                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

                show_Toast(shellBlastNo + getString(R.string.text_dialog_xgcg));

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
                    doWithReceivData(cmd, localBuf);//处理cmd命令

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
                if (busInfo.getBusCurrentIa() > 40) {//判断当前电流是否偏大
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
     * 单发注册(存储桥丝状态)
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
        if (factoryCode != null && factoryCode.trim().length() > 0 && !factoryCode.contains(facCode) && !facCode.equals("A6")) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
            return -1;
        }
        //验证特征码
//        if (factoryFeature != null && factoryFeature.trim().length() > 0 && !factoryFeature.contains(facFea)) {
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
//            return -1;
//        }
        Log.e("查询生产数据库查管壳码", "detonatorId: " + detonatorId);
        if (setDelayTimeStartDelaytime.getText().length() == 0 && reEtF1.getText().length() == 0 && reEtF2.getText().length() == 0) {
            tipInfoFlag = 6;
            mHandler_1.sendMessage(mHandler_1.obtainMessage());
            Log.e("验证是否输入延时", "tipInfoFlag: ");
            return -1;
        }
//        int maxNo = getMaxNumberNo();
        int start = Integer.parseInt(String.valueOf(setDelayTimeStartDelaytime.getText()));//开始延时
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
            denatorBaseinfo.setSithole(maxNo + "");
            denatorBaseinfo.setDelay(delay);
            denatorBaseinfo.setDenatorId(detonatorId);
            denatorBaseinfo.setRegdate(Utils.getDateFormatLong(new Date()));
            denatorBaseinfo.setStatusCode("02");
            denatorBaseinfo.setStatusName("已注册");
            denatorBaseinfo.setErrorCode("FF");
            denatorBaseinfo.setErrorName("正常");
            denatorBaseinfo.setWire(zhuce_form.getWire());//桥丝状态
            denatorBaseinfo.setPiece(mRegion);
            //向数据库插入数据
            getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
            //向数据库插入数据
        }

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
            lg_Piece = list_lg.get(0).getPiece();
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
            lg_Piece = list_lg.get(0).getPiece();
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
                    dialog12.dismiss();
                }).create();
        Utils.saveFile();//把软存中的数据存入磁盘中
        dialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.re_btn_f1, R.id.re_btn_f2, R.id.btn_singleReister, R.id.btn_LookHistory, R.id.btn_setdelay, R.id.btn_ReisterScanStart_st, R.id.btn_ReisterScanStart_ed, R.id.btn_return, R.id.btn_inputOk, R.id.re_et_f1, R.id.re_et_f2, R.id.setDelayTime_startDelaytime})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.re_btn_f1:

                hideInputKeyboard();
                if (reEtF1.getText().toString().equals("")) {
                    show_Toast(getString(R.string.text_line_tip16));
                    return;
                }
                delay_set = "f1";
                if (maxSecond != 0) {
                    if (Integer.parseInt(reEtF1.getText().toString()) > maxSecond) {
                        show_Toast(getString(R.string.text_line_tip3)  + maxSecond + getString(R.string.text_line_tip4));
                        return;
                    }
                    if (Integer.parseInt(setDelayTimeStartDelaytime.getText().toString()) > maxSecond) {
                        show_Toast(getString(R.string.text_line_tip6) + maxSecond + getString(R.string.text_line_tip4));
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

            case R.id.re_btn_f2:
                hideInputKeyboard();
                if (reEtF2.getText().toString().equals("")) {
                    show_Toast(getString(R.string.text_line_tip2));
                    return;
                }
                delay_set = "f2";
                if (maxSecond != 0) {
                    if (Integer.parseInt(reEtF2.getText().toString()) > maxSecond) {
                        show_Toast(getString(R.string.text_line_tip3) + maxSecond + getString(R.string.text_line_tip4));
                        return;
                    }
                    if (Integer.parseInt(setDelayTimeStartDelaytime.getText().toString()) > maxSecond) {
                        show_Toast(getString(R.string.text_line_tip5) + maxSecond + getString(R.string.text_line_tip4));
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
                if (isSingleReisher == 0) {
                    String shellBlastNo = serchFristLG();
                    int num = serchFristLGINdenatorHis(shellBlastNo);

                    if (num > 0) {
                        showAlertDialog();
                    }
                    show_Toast(getString(R.string.text_line_tip1));
                    btnInputOk.setEnabled(false);
                    btnSingleReister.setText(R.string.text_line_stop);
                    isSingleReisher = 1;
                    closeThread();
//                    closeOpenThread = new CloseOpenPower();
//                    closeOpenThread.start();
                    sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令

                } else {
                    btnInputOk.setEnabled(true);
                    btnSingleReister.setText(R.string.text_line_single);
                    txt_currentVolt.setText(R.string.text_line_IV);
                    txt_currentIC.setText(R.string.text_line_ic);
                    txt_currentIC.setTextColor(Color.BLACK);
                    isSingleReisher = 0;
                    initCloseCmdReFlag = 0;
                    initOpenCmdReFlag = 0;
                    revCloseCmdReFlag = 0;
                    revOpenCmdReFlag = 0;
                    // 13 退出注册模式
                    sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));
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
                show_Toast(getString(R.string.text_reister_tip2));
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
        String startDelay = setDelayTimeStartDelaytime.getText().toString();
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
                show_Toast(getString(R.string.text_reister_tip3));
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
        setDelayTimeStartDelaytime.setBackgroundResource(R.drawable.translucent);

        reBtnF1.setBackgroundResource(R.drawable.bt_mainpage_style);
        reBtnF2.setBackgroundResource(R.drawable.bt_mainpage_style);

        reEtF1.clearFocus();
        reEtF2.clearFocus();
        setDelayTimeStartDelaytime.clearFocus();
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
                show_Toast(getString(R.string.text_reister_tip4) + mRegion);
                // 延时选择重置
                resetView();
                delay_set = "0";
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
            str = getString(R.string.text_dfzc_qy) + region;
        } else {
            str = getString(R.string.text_dfzc_qy) + region + getString(R.string.text_dfzc_sl) + size + ")";
        }
        // 设置标题
        getSupportActionBar().setTitle(mOldTitle + str);
        // 保存区域参数
        SPUtils.put(this, Constants_SP.RegionCode, region);

        Log.e("liyi_Region", "已选择" + str);
    }


    /***
     * 手动输入注册(通过开始管壳码和截止管壳码计算出所有管壳码)
     */
    private int insertDenator(String prex, int start, int end,boolean x) {

        if (end < start) return -1;
        if (start < 0 || end > 99999) return -1;
        String shellNo = "";
        int flag = 0;
        int start_delay = 0;//开始延时
        int f1 = 0;//f1延时
        int f2 = 0;//f2延时
//        int maxNo = getMaxNumberNo();
//        int delay = getMaxDelay(maxNo);//获取最大延时
        int maxNo = new GreenDaoMaster().getPieceMaxNum(mRegion);//获取该区域最大序号
        int delay = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);//获取该区域 最大序号的延时
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
                if(x){
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
                }
                if(!x){
                    xiangHao_errNum++;
                }

                break;
            }
            DetonatorTypeNew detonatorTypeNew = new GreenDaoMaster().serchDenatorId(shellNo);
//            if (detonatorTypeNew == null) {
//                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
//                pb_show = 0;
//                return -1;
//            }8
            if (delay_set.equals("f1")) {//获取最大延时有问题
                if (maxNo == 0) {
                    delay = delay + start_delay;
                } else {
                    delay = delay + f1;
                }
            } else if (delay_set.equals("f2")) {
                if (maxNo == 0) {
                    delay = delay + start_delay;
                } else {
                    delay = delay + f2;
                }
            }
            if (maxSecond != 0 && delay > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                break;
            }
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
     * 扫码注册方法
     */
    private void insertSingleDenator_2(String shellNo, String denatorId, String yscs) {
        Log.e("检查管厂码", "factoryCode: "+factoryCode );
        Log.e("检查管厂码", "shellNo.substring(0,2): "+shellNo.substring(0,2) );
        if (factoryCode != null && factoryCode.trim().length() > 0 && !factoryCode.equals(shellNo.substring(0,2)) ) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));//  "管厂码与系统中定义的管厂码不一致";
            return  ;
        }
        //雷管信息有误，特征码不正确，请检查 5620819H00001
        if (factoryFeature != null && factoryFeature.trim().length() > 0 && factoryFeature.equals(shellNo.substring(7,8))) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
            return ;
        }
        if (shellNo.length() != 13) {
            return ;
        }
        if (check(shellNo) == -1) {
            return ;
        }
//        int maxNo = getMaxNumberNo();
        int start_delay = 0;//开始延时
        int f1 = Integer.parseInt(String.valueOf(reEtF1.getText()));//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
//        int delay = getMaxDelay(maxNo);//获取最大延时

        int maxNo = new GreenDaoMaster().getPieceMaxNum(mRegion);//获取该区域最大序号
        int delay = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);//获取该区域 最大序号的延时
        Log.e("扫码", "delay_set: " + delay_set);
        if (delay_set.equals("f1")) {
            if (maxSecond != 0 && delay + f1 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return ;
            }
        } else if (delay_set.equals("f2")) {
            if (maxSecond != 0 && delay + f2 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return ;
            }
        }
        if (delay_set.equals("f1")) {//获取最大延时有问题
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                delay = delay + f1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                delay = delay + f2;
            }
        }
        Utils.writeRecord("单发注册:--管壳码:" + shellNo + "芯片码" + denatorId + "--延时:" + delay);

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
        //向数据库插入数据
        getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
//        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        return ;
    }


    private void tingzhiScan() {
        switch (Build.DEVICE) {
            case "M900": {
                //M900关闭扫码
                mScaner.stopScan();
                break;
            }
            case "ST327":
            case "S337":  {
                //st327扫码下电
                powerOffScanDevice(PIN_TRACKER_EN);//扫码头下电
                break;
            }
            default:{
                //kt50停止扫码头方法
                scanDecode.stopScan();//停止扫描
            }
        }
    }

    private int check(String shellNo) {
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
            Log.e("检测重复", "check: " );
            singleShellNo = shellNo;
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
        String yue = shellNo.substring(3, 5);
        String ri = shellNo.substring(5, 7);


        return 0;
    }
}
