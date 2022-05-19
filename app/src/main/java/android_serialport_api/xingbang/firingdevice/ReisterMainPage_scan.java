package android_serialport_api.xingbang.firingdevice;


import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_TRACKER_EN;

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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
    ListView reisterListView;
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
    private String lg_No;//单发注册
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reister_main_page_scan);
        ButterKnife.bind(this);
        SoundPlayUtils.init(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 22);
        db = mMyDatabaseHelper.getReadableDatabase();
        getUserMessage();
        getFactoryType();//获取延期最大值
        showDenatorSum();//显示雷管总数
        //扫描参数设置

        //管壳号扫描分码--结束

        btn_onClick();//button的onClick
        getFactoryCode();//获取厂家码
        handler();//所有的handler
        scan();//扫描初始化
        init();
        hideInputKeyboard();//隐藏焦点
        Utils.writeRecord("---进入扫码注册页面---");
    }

    private void getUserMessage() {
        List<MessageBean> list = getDaoSession().getMessageBeanDao().loadAll();
        qiaosi_set = list.get(0).getQiaosi_set();
        version = list.get(0).getVersion();
//        String selection = "id = ?"; // 选择条件，给null查询所有
//        String[] selectionArgs = {"1"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
//        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_MESSQGE, null, selection, selectionArgs, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {  //cursor不位空,可以移动到第一行
//            qiaosi_set = cursor.getString(10);
//            version = cursor.getString(17);
//            cursor.close();
//        }
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    private void updateMessage(String version) {
        MessageBean bean = GreenDaoMaster.getAllFromInfo_bean();
        bean.setVersion(version);
        getDaoSession().getMessageBeanDao().update(bean);
        Utils.saveFile_Message();
    }

    private long queryMessage() {
        return getDaoSession().getMessageBeanDao().count();
    }


    /**
     * 扫码注册方法/扫描头返回方法
     */
    private void scan() {
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");//初始化扫描服务

        scanDecode.getBarCode(data -> {
//            Log.e("雷管", "data: " + data);
            if (deleteList()) return;
            hideInputKeyboard();//隐藏光标
            //根据二维码长度判断新旧版本,兼容01一代,02二代芯片
            if (data.length() == 13) {
                updateMessage("01");
            }else if (data.length() == 28) {//P53904180500005390418050000
                updateMessage("02");
            } else if (data.length() == 30) {//5620302H00001A62F400FFF20AB603
                updateMessage("02");
            }
            if (data.length() == 19) {//扫描箱号
                addXiangHao(data);
            }
            if (sanButtonFlag > 0) {//扫码结果设置到输入框里
                decodeBar(data);
                Message msg = new Message();
                msg.obj = data;
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(9));
                scanDecode.stopScan();
            } else {
                String barCode;
                String denatorId;
                if (data.length() == 30) {//5620302H00001A62F400FFF20AB603
                    barCode = data.substring(0, 13);
                    denatorId = data.substring(13,26);
                    insertSingleDenator_2(barCode, denatorId,data.substring(26));//同时注册管壳码和芯片码
                } else {
                    barCode = getContinueScanBlastNo(data);//VR:1;SC:5600508H09974;
                    insertSingleDenator(barCode);
                }
                hideInputKeyboard();//隐藏光标
            }
        });
    }

    private void handler() {
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
                show_Toast("与第" + lg_No + "发" + singleShellNo + "重复");
                int total = showDenatorSum();
                reisterListView.setSelection(total - Integer.parseInt(lg_No));
            } else if (msg.what == 6) {
                SoundPlayUtils.play(4);
                show_Toast("当前管壳码超出13位,请检查雷管或系统版本是否符合后,再次注册");
            } else if (msg.what == 7) {
                SoundPlayUtils.play(4);
                show_Toast_long("与第" + lg_No + "发" + singleShellNo + "重复");
            } else if (msg.what == 8) {
                SoundPlayUtils.play(4);
                show_Toast("有延时为空,请先设置延时");
            } else if (msg.what == 9) {
                decodeBar(msg.obj.toString());
            }  else if (msg.what == 10) {
                show_Toast("找不到对应的生产数据,请先导入生产数据");
            }else {
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
            et_startDelay.setText("10");
        }

        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " ", null);
        int serNum = cursor.getCount();//得到数据的总条数
        cursor.close();

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
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryDenatorBaseinfo();
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
                        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                insertDenator(prex, finalStrNo, finalEndNo);//添加
            }
        }).start();
    }

    private void init() {
        adapter = new SimpleCursorAdapter(
                ReisterMainPage_scan.this,
                R.layout.item_blast,
                null,
                new String[]{"blastserial", "sithole", "shellBlastNo", "delay"},
                new int[]{R.id.blastserial, R.id.sithole, R.id.shellBlastNo, R.id.delay},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //adapter.
        reisterListView.setAdapter(adapter);
        reisterListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int arg2, long arg3) {
                LinearLayout myte = (LinearLayout) view;
                TextView seralNoTxt = (TextView) myte.getChildAt(0);
                TextView holeTxt = (TextView) myte.getChildAt(1);
                TextView delayTxt = (TextView) myte.getChildAt(2);
                TextView denatorTxt = (TextView) myte.getChildAt(3);
                String serialNo = seralNoTxt.getText().toString().trim();
                String holeNo = holeTxt.getText().toString().trim();
                String denatorNo = denatorTxt.getText().toString().trim();
                String delaytime = delayTxt.getText().toString().trim();
                selectDenatorId = serialNo;
                modifyBlastBaseInfo(serialNo, holeNo, delaytime, denatorNo);//序号,孔号,延时,管壳码
                return false;
            }
        });
        getLoaderManager().initLoader(0, null, this);
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
        container1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != ReisterMainPage_scan.this.getCurrentFocus()) {
                    container1.setFocusable(true);
                    container1.setFocusableInTouchMode(true);
                    container1.requestFocus();
                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    return mInputMethodManager.hideSoftInputFromWindow(ReisterMainPage_scan.this.getCurrentFocus().getWindowToken(), 0);
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
        List<DenatorBaseinfo> list = master.queryDenatorBaseinfoToStatusCode("02");
        txtReisteramount.setText("已注册:" + list.size());
        return list.size();
    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(ReisterMainPage_scan.this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
        divider.setBackgroundColor(Color.TRANSPARENT);
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
            Log.e("关闭线程", "关闭线程: ");
        }
        Log.e("延时长度", "reEtF1.getText().length(): " + reEtF1.getText().length());
        if (reEtF1.getText().length() > 0) {
            MmkvUtils.savecode("f1", reEtF1.getText().toString());
        }
        if (reEtF2.getText().length() > 0) {
            MmkvUtils.savecode("f2", reEtF1.getText().toString());
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

        return tipStr;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        ContextMenuInfo menuInfo = (ContextMenuInfo) item.getMenuInfo();
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
                getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
                break;
            case 2:
                this.modifyBlastBaseInfo(id);
                Temp = "修改";
                break;
            default:
                break;
        }
        Toast.makeText(this, Temp + "处理", Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
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
                Toast.makeText(ReisterMainPage_scan.this, "管壳码: " + a + ", 延时: " + b, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void modifyBlastBaseInfo(String serialNo, String hoteNo, String delaytime, final String denatorNo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setIcon(R.drawable.ic_launcher);
        //   builder.setTitle("修改延时信息");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(this).inflate(R.layout.delaymodifydialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        final EditText serialNoTxt = (EditText) view.findViewById(R.id.serialNo);
        final EditText denatorNoTxt = (EditText) view.findViewById(R.id.denatorNo);
        final EditText delaytimeTxt = (EditText) view.findViewById(R.id.delaytime);

        serialNoTxt.setEnabled(false);
        denatorNoTxt.setEnabled(false);

        serialNoTxt.setText(serialNo);
        denatorNoTxt.setText(denatorNo);
        delaytimeTxt.setText(delaytime);

        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String a = username.getText().toString().trim();
                String b = delaytimeTxt.getText().toString().trim();
                if (maxSecond != 0 && Integer.parseInt(b) > maxSecond) {//
                    mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                    dialog.dismiss();
                } else if (b == null || b.trim().length() < 1 || (maxSecond > 0 && Integer.parseInt(b) > maxSecond)) {
                    show_Toast(getString(R.string.text_error_tip37));
                    dialog.dismiss();
                } else {
                    Utils.writeRecord("-单发修改延时:" + "-管壳码:" + denatorNo + "-延时:" + b);
                    modifyDelayTime(selectDenatorId, b);
                    getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
                    //    将输入的用户名和密码打印出来
                    show_Toast(getString(R.string.text_error_tip38));
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                pb_show = 1;
                runPbDialog();
                Utils.writeRecord("-单发删除:" + "-删除管壳码:" + denatorNo + "-延时" + delaytime);
                new Thread(() -> {
                    String whereClause = "shellBlastNo = ?";
                    String[] whereArgs = {denatorNo};
                    db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, whereClause, whereArgs);
                    Utils.deleteData(ReisterMainPage_scan.this);//重新排序雷管
                    getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
                    tipDlg.dismiss();
                    Utils.saveFile();//把软存中的数据存入磁盘中
                    pb_show = 0;
                }).start();
            }
        });
        builder.show();
    }

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
                if (checkRepeatShellNo(barCode) ) {
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

    private int check(String shellNo) {
        if (reEtF1.getText().length() < 1 || reEtF2.getText().length() < 1) {
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
            singleShellNo = "";
            singleShellNo = shellNo;
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
        return 0;
    }

    private DetonatorTypeNew serchDenatorId(String shellBlastNo) {
        GreenDaoMaster master = new GreenDaoMaster();
        //        Log.e("查询生产数据库查管壳码", "denatorId: "+denatorId);
//        Log.e("查询生产数据库查管壳码", "shellBlastNo: "+shellBlastNo);
        return master.queryShellBlastNoTypeNew(shellBlastNo);
    }
    /**
     * 查询生产表中对应的管壳码
     * */
    private DetonatorTypeNew serchDenatorForDetonatorTypeNew(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryDetonatorForTypeNew(denatorId);
    }

    /***
     * 单发注册(扫码注册,用到)
     */
    private int insertSingleDenator(String shellNo) {
        if (shellNo.length() != 13) {
            return -1;
        }
        if (check(shellNo) == -1) {
            return -1;
        }
//        String denatorId = serchDenatorId(shellNo);
        DetonatorTypeNew detonatorTypeNew = serchDenatorId(shellNo);
        if (detonatorTypeNew == null) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
            return -1;
        }
        int index = getEmptyDenator(-1);
        int maxNo = getMaxNumberNo();
        int start_delay = Integer.parseInt(String.valueOf(et_startDelay.getText()));//开始延时
        int f1 = Integer.parseInt(String.valueOf(reEtF1.getText()));//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
        int delay = getMaxDelay(maxNo);//获取最大延时
        Log.e("扫码", "delay_set: " + delay_set);
        Log.e("扫码", "detonatorTypeNew: " + detonatorTypeNew.toString());
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

        if (index < 0) {//说明没有空余的序号可用
            ContentValues values = new ContentValues();
            maxNo++;
            values.put("blastserial", maxNo);
            values.put("sithole", maxNo);
            values.put("shellBlastNo", shellNo);
            if(!detonatorTypeNew.getDetonatorId().equals("0")){
                values.put("denatorId",detonatorTypeNew.getDetonatorId());
            }

            values.put("delay", delay);
            values.put("regdate", Utils.getDateFormatLong(new Date()));
            values.put("statusCode", "02");
            values.put("statusName", "已注册");
            values.put("errorCode", "FF");
            values.put("errorName", "");
            values.put("wire", "");
            values.put("zhu_yscs",detonatorTypeNew.getZhu_yscs());
            //向数据库插入数据
            db.insert("denatorBaseinfo", null, values);

        } else {
            ContentValues values = new ContentValues();
            values.put("shellBlastNo", shellNo);//key为字段名，value为值
            values.put("statusCode", "02");
            values.put("statusName", "已注册");
            values.put("errorCode", "FF");
            values.put("errorName", "");
            values.put("regdate", Utils.getDateFormatLong(new Date()));
            db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + index});
        }
        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        Utils.writeRecord("单发输入注册:--管壳码:" + shellNo + "--延时:" + delay);
        return 0;
    }

    /***
     * 单发注册(同时注册管壳码和芯片码)
     */
    private int insertSingleDenator_2(String shellNo, String denatorId,String yscs) {
        if (shellNo.length() != 13) {
            return -1;
        }
        if (check(shellNo) == -1) {
            return -1;
        }
        int index = getEmptyDenator(-1);
        int maxNo = getMaxNumberNo();
        int start_delay = Integer.parseInt(String.valueOf(et_startDelay.getText()));//开始延时
        int f1 = Integer.parseInt(String.valueOf(reEtF1.getText()));//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
        int delay = getMaxDelay(maxNo);//获取最大延时
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
        Utils.writeRecord("单发输入注册:--管壳码:" + shellNo + "芯片码" + denatorId + "--延时:" + delay);
        if (index < 0) {//说明没有空余的序号可用
            ContentValues values = new ContentValues();
            maxNo++;
            values.put("blastserial", maxNo);
            values.put("sithole", maxNo);
            values.put("shellBlastNo", shellNo);
            values.put("denatorId", denatorId);
            values.put("delay", delay);
            values.put("regdate", Utils.getDateFormatLong(new Date()));
            values.put("statusCode", "02");
            values.put("statusName", "已注册");
            values.put("errorCode", "FF");
            values.put("errorName", "");
            values.put("wire", "");
            values.put("yscs_zhu", yscs);
            //向数据库插入数据
            db.insert("denatorBaseinfo", null, values);

        } else {
            ContentValues values = new ContentValues();
            values.put("shellBlastNo", shellNo);//key为字段名，value为值
            values.put("statusCode", "02");
            values.put("statusName", "已注册");
            values.put("errorCode", "FF");
            values.put("errorName", "");
            values.put("regdate", Utils.getDateFormatLong(new Date()));
            db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + index});
        }
        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        return 0;
    }

    /***
     * 手动输入注册(通过开始管壳码和截止管壳码计算出所有管壳码)
     */
    private int insertDenator(String prex, int start, int end) {

        if (end < start) return -1;
        if (start < 0 || end > 99999) return -1;
        String shellNo = "";
        int flag = 0;
        int index = getEmptyDenator(-1);
        int maxNo = getMaxNumberNo();
        int start_delay = Integer.parseInt(String.valueOf(et_startDelay.getText()));//开始延时
        int f1 = Integer.parseInt(String.valueOf(reEtF1.getText()));//f1延时
        int f2 = Integer.parseInt(String.valueOf(reEtF2.getText()));//f2延时
        Log.e("注册", "f1: " + f1);
        Log.e("注册", "f2: " + f2);
        int delay = getMaxDelay(maxNo);//获取最大延时
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
        ContentValues values = new ContentValues();
        int reCount = 0;
        Utils.writeRecord("--手动输入注册--前8位:" + prex + "--开始后5位:" + start +
                "--结束后5位:" + end + "--开始延时:" + start_delay);
        for (int i = start; i <= end; i++) {
            shellNo = prex + String.format("%05d", i);
            if (checkRepeatShellNo(shellNo)) {
                singleShellNo = "";
                singleShellNo = shellNo;
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));

                break;
            }
            DetonatorTypeNew detonatorTypeNew = serchDenatorId(shellNo);
            if (detonatorTypeNew == null) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
                pb_show = 0;
                return -1;
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
            if (index < 0) {//说明没有空余的序号可用
                maxNo++;
                values.put("blastserial", maxNo);
                values.put("sithole", maxNo);
                values.put("shellBlastNo", shellNo);
                if(!detonatorTypeNew.getDetonatorId().equals("0")){
                    values.put("denatorId",detonatorTypeNew.getDetonatorId());
                }
                values.put("delay", delay);
                values.put("regdate", Utils.getDateFormatLong(new Date()));
                values.put("statusCode", "02");
                values.put("statusName", "已注册");
                values.put("errorCode", "FF");
                values.put("errorName", "");
                values.put("wire", "");//桥丝状态
                values.put("zhu_yscs", detonatorTypeNew.getZhu_yscs());
                //向数据库插入数据
                db.insert("denatorBaseinfo", null, values);
            } else {
                values = new ContentValues();
                values.put("shellBlastNo", shellNo);//key为字段名，value为值
                values.put("statusCode", "");
                values.put("statusName", "");
                values.put("regdate", Utils.getDateFormatLong(new Date()));
                values.put("statusCode", "02");
                values.put("statusName", "已注册");
                values.put("errorCode", "FF");
                values.put("errorName", "");
                db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + index});

            }
            reCount++;
        }
        getLoaderManager().restartLoader(1, null, ReisterMainPage_scan.this);
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
     * 检查重复的数据
     *
     * @param shellBlastNo
     * @return
     */
    public boolean checkRepeatShellNo(String shellBlastNo) {
        DenatorBaseinfo denatorBaseinfo = new GreenDaoMaster().checkRepeatShellNo_2(shellBlastNo);
        if (denatorBaseinfo != null) {
            Log.e("注册", "denatorBaseinfo: " + denatorBaseinfo.toString());
            lg_No = denatorBaseinfo.getBlastserial() + "";
            return true;
        } else {
            return false;
        }
    }

    int flag1 = 0;
    int flag2 = 0;

    @OnClick({R.id.btn_scanReister, R.id.re_btn_f1, R.id.re_btn_f2, R.id.btn_setdelay, R.id.btn_input, R.id.btn_single,
            R.id.btn_inputOk, R.id.btn_return, R.id.btn_singleReister, R.id.btn_ReisterScanStart_st,
            R.id.btn_ReisterScanStart_ed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scanReister:
                if (delay_set.equals("0")) {
                    show_Toast("请设置延时");
                    break;
                }
                if (reEtF1.getText().length() < 1 || reEtF2.getText().length() < 1) {
                    show_Toast("有延时为空,请先设置延时");
                    break;
                }
                if (deleteList()) return;
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
            case R.id.re_btn_f1:
                hideInputKeyboard();
                if (reEtF1.getText().equals("")) {
                    show_Toast("当前设置延时为空,请重新设置");
                    return;
                }
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
            case R.id.re_btn_f2:
                hideInputKeyboard();
                if (reEtF2.getText().toString().equals("")) {
                    show_Toast("当前设置延时为空,请重新设置");
                    Log.e("f1", reEtF2.getText().toString());
                    return;
                }
                if (maxSecond != 0) {
                    if (Integer.parseInt(reEtF2.getText().toString()) > maxSecond) {
                        show_Toast("当前设置延时已超过最大延时" + maxSecond + "ms,请重新设置");
                        return;
                    }
                    if (Integer.parseInt(et_startDelay.getText().toString()) > maxSecond) {
                        show_Toast("当前开始延时已超过最大延时" + maxSecond + "ms,请重新设置");
                        return;
                    }
                }
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
                if (llStart.getVisibility() == View.GONE) {
                    lySetDelay.setVisibility(View.GONE);
                    llSingle.setVisibility(View.GONE);
                    llStart.setVisibility(View.VISIBLE);
                    llEnd.setVisibility(View.VISIBLE);
                    llNum.setVisibility(View.VISIBLE);
                    btnInputOk.setVisibility(View.VISIBLE);
                    btnSingle.setText("返回");
                } else {
                    lySetDelay.setVisibility(View.VISIBLE);
                    llSingle.setVisibility(View.VISIBLE);
                    llStart.setVisibility(View.GONE);
                    llEnd.setVisibility(View.GONE);
                    llNum.setVisibility(View.GONE);
                    btnInputOk.setVisibility(View.GONE);
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
                if (reEtF1.getText().length() < 1 || reEtF2.getText().length() < 1) {
                    show_Toast("有延时为空,请先设置延时");
                    break;
                }
                if (deleteList()) return;//判断列表第一发是否在历史记录里
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
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                insertDenator(prex, start, end);
                                Log.e("手动输入", "prex: " + prex);
                                Log.e("手动输入", "start: " + start);
                                Log.e("手动输入", "end: " + end);
                            }
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
                if (reEtF1.getText().length() < 1 || reEtF2.getText().length() < 1) {
                    show_Toast("有延时为空,请先设置延时");
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
        }
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

        String subBarCode;

        if (strParamBarcode.trim().length() > 14) {
            int index = strParamBarcode.indexOf("SC:");
            if (index > 0) {
                show_Toast("不正确的编码，请扫描选择正确的编码");
                return;
            }
            subBarCode = strParamBarcode.substring(index + 3, index + 16);
            if (subBarCode.trim().length() < 13) {
                show_Toast("不正确的编码，请扫描选择正确的编码");
                return;
            }
        } else {
            if (strParamBarcode.trim().length() == 14) {//煤许雷管
                subBarCode = strParamBarcode.substring(0, 13);
            } else if (strParamBarcode.trim().length() == 13) {
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

            edit_end_entBF2Bit_en.setText("");
            edit_end_entproduceDate_ed.setText("");
            edit_end_entAT1Bit_ed.setText("");
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


}
