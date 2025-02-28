package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.litepal.LitePal;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
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
import android_serialport_api.xingbang.custom.ChaKan_SQAdapter;
import android_serialport_api.xingbang.custom.DataAdapter;
import android_serialport_api.xingbang.custom.DenatorBaseinfoSelect;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.ShouQuanData;
import android_serialport_api.xingbang.custom.SingleRegisterData;
import android_serialport_api.xingbang.custom.SrDenatorAdapter;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.Defactory;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.PaiData;
import android_serialport_api.xingbang.db.SingleRegisterDenator;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleRegisterActivity extends SerialPortActivity implements SrDenatorAdapter.InnerItemOnclickListener{
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
    @BindView(R.id.txt_currentVolt)
    TextView txtCurrentVolt;
    @BindView(R.id.txt_currentIC)
    TextView txtCurrentIC;
    @BindView(R.id.txt_reisteramount)
    TextView txtReisteramount;
    @BindView(R.id.rv_zclist)
    RecyclerView rvZcList;
    @BindView(R.id.lay_bottom)
    LinearLayout layBottom;//底部布局
    @BindView(R.id.tv_check_all)
    TextView tv_check_all;
    @BindView(R.id.tv_input)
    TextView tv_input;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.re_gkm)
    LinearLayout regkm;
    @BindView(R.id.text_gkm1)
    TextView text_gkm;
    @BindView(R.id.text_gkm2)
    TextView text_uid;
    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private SrDenatorAdapter mAdapter;
    private List<SingleRegisterDenator> mListData = new ArrayList<>();
    private List<SingleRegisterData> mList = new ArrayList<>();
    private Handler mHandler_UI = new Handler(Looper.getMainLooper());     // UI处理
    private int paiChoice=1;
    private int kongChoice = 0;
    private static final int STATE_DEFAULT = 0;//默认状态
    private static final int STATE_EDIT = 1;//编辑状态
    private int mEditMode = STATE_DEFAULT;
    private boolean editorStatus = false;//是否为编辑状态
    private int index = 0;//当前选中的item数
    private String mRegion;     // 区域
    private String mOldTitle;   // 原标题
    String TAG = "单发注册";
    private String factoryCode = "";//厂家代码
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler2;
    private int weiChoice;
    private String qiaosi_set = "";//是否检测桥丝
    private From12Reister zhuce_form = null;
    private volatile int zhuce_Flag = 0;//单发检测时发送40的标识
    private boolean flag_zhuce = false;//是否新注册
    private From42Power busInfo;
    private Handler mHandler_0 = new Handler();     // UI处理
    private Handler mHandler_1 = new Handler();//提示电源信息
    private Handler mHandler_tip = new Handler();//错误提示
    private static int tipInfoFlag = 0;
    private String singleShellNo;//单发注册
    private String lg_No;//重复雷管管壳码
    private String lg_Piece;//重复雷管区号
    private int maxSecond = 0;//最大秒数
    private String deTypeName = "";//雷管类型名称
    private String deTypeSecond = "";//该类型雷管最大延期值
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_register);
        ButterKnife.bind(this);
        SoundPlayUtils.init(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        // 设置标题
        titleBack.setVisibility(View.GONE);
        title_lefttext.setVisibility(View.VISIBLE);
        titleRight2.setVisibility(View.VISIBLE);
        titleAdd.setVisibility(View.GONE);
        title_lefttext.setText(getResources().getString(R.string.text_singleReister));
        titleRight2.setText(getResources().getString(R.string.text_jczxlg));
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            paiChoice = bundle.getInt("paiChoice");
            kongChoice = bundle.getInt("kongChoice");
            mRegion = bundle.getString("mRegion");
            delay_set = bundle.getString("delay_set");
            flag_jh_f1 = bundle.getBoolean("flag_jh_f1");
            flag_jh_f2 = bundle.getBoolean("flag_jh_f2");
            btn_start = bundle.getBoolean("btn_start");
            flag_tk = bundle.getBoolean("flag_tk");
            weiChoice = bundle.getInt("weiChoice");
            Log.e(TAG, "传递的值mRegion: " + mRegion);
            Log.e(TAG, "传递的值paiChoice: " + paiChoice);
            Log.e(TAG, "传递的值kongChoice: " + kongChoice);
            Log.e(TAG, "传递的值weiChoice:" + weiChoice);
            Log.e(TAG, "传递的值delay_set: "+delay_set);
            DenatorBaseinfo denatorBaseinfo_choice = new GreenDaoMaster().queryDetonatorPaiAndKongAndWei(mRegion, paiChoice, kongChoice, weiChoice);
            Log.e(TAG, "denatorBaseinfo_choice: " + denatorBaseinfo_choice);
        }
        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SrDenatorAdapter(R.layout.a_item_single_register, mList);//绑定视图和数据
        rvZcList.setLayoutManager(linearLayoutManager);
        rvZcList.setAdapter(mAdapter);
        getFactoryCode();
        getFactoryType();//获取延期最大值
        initHandle();
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.showCheckBox(true);
                layBottom.setVisibility(View.VISIBLE);
                return true;
            }
        });
        mAdapter.setOnInnerItemOnClickListener(this);
        this.isSingleReisher = 0;
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

    private void getFactoryCode() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<Defactory> list = master.queryDefactoryToIsSelected("是");
        if (list.size() > 0) {
            factoryCode = list.get(0).getDeEntCode();
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

    private void initHandle() {
        mHandler2 = new Handler(msg -> {
            //显示或隐藏loding界面
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });
        mHandler_UI = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1:
                    showUiToast(getResources().getString(R.string.text_del_ok));
                    break;
                case 2:
                    showUiToast(getResources().getString(R.string.text_lgvf));
                    break;
                case 3:
                    showUiToast(getResources().getString(R.string.text_cdcw));
                    break;
                case 7:
                    showUiToast(getResources().getString(R.string.text_send_tip26));
                    break;
                case 9:
                    showUiToast(getResources().getString(R.string.text_error_tip1));
                    break;
            }
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
//                show_Toast("已达到最大延时限制" + maxSecond + "ms");
            } else if (msg.what == 4) {
                SoundPlayUtils.play(4);
                show_Toast_long(getString(R.string.text_error_tip69) + singleShellNo + getString(R.string.text_error_tip71));
//                show_Toast_long(getString(R.string.text_error_tip69) + lg_Piece + getString(R.string.text_error_tip70) + lg_pai + "-" + lg_No + "-" + lg_wei + " " + singleShellNo + getString(R.string.text_error_tip71));
                int total = showDenatorSum();
//                reisterListView.setSelection(total - Integer.parseInt(lg_No));
                MoveToPosition(linearLayoutManager, rvZcList, total - Integer.parseInt(lg_No));
            } else if (msg.what == 6) {
                SoundPlayUtils.play(4);
                show_Toast(getString(R.string.text_line_tip7));
            } else if (msg.what == 7) {
                SoundPlayUtils.play(4);
                show_Toast_long(getString(R.string.text_error_tip69) + lg_No + "-" + singleShellNo + getString(R.string.text_error_tip71));
//                show_Toast_long(getString(R.string.text_error_tip69) + lg_Piece + getString(R.string.text_error_tip70) + lg_pai + "-" + lg_No + "-" + lg_wei + " " + singleShellNo + getString(R.string.text_error_tip71));
            } else if (msg.what == 8) {
                SoundPlayUtils.play(4);
//                show_Toast(getString(R.string.text_error_tip64));
                show_Toast(getString(R.string.text_line_tip19));
            } else if (msg.what == 9) {
//                decodeBar(msg.obj.toString());
            } else if (msg.what == 10) {
                show_Toast(getString(R.string.text_line_tip8));
            } else if (msg.what == 11) {
                show_Toast(getString(R.string.text_error_tip66));
            } else if (msg.what == 12) {
                show_Toast(getString(R.string.text_mx_zcsb));
            } else if (msg.what == 13) {
                SoundPlayUtils.play(4);
                show_Toast("延时不能小于0ms");
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
            }
            if (tipInfoFlag == 89) {//刷新界面
                show_Toast(getResources().getString(R.string.text_line_tip15));
                showDenatorSum();
            }
            return false;
        });
        mHandler_0 = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1001:
                    Log.e("liyi_1001", "更新视图 雷管数量: " + mListData.size());
                    // 查询全部雷管 倒叙(序号)
                    mListData = new GreenDaoMaster().querySrDetonatorAllAsc();
                    mList.clear();
                    Log.e("加载", "mListData.size(): " + mListData.size());
                    for (SingleRegisterDenator item : mListData) {
                        SingleRegisterData singleRegisterData = new SingleRegisterData();
                        singleRegisterData.setId(item.getId());
                        singleRegisterData.setShellBlastNo(item.getShellBlastNo());
                        singleRegisterData.setDetonatorId(item.getDetonatorId());
                        singleRegisterData.setCong_yscs(item.getCong_yscs());
                        singleRegisterData.setZhu_yscs(item.getZhu_yscs());
                        singleRegisterData.setQibao(item.getQibao());
                        singleRegisterData.setDelay(item.getDelay());
                        singleRegisterData.setTime(item.getTime());
                        if (!mList.contains(singleRegisterData)) {
                            mList.add(singleRegisterData);
                        }
                    }
                    mAdapter.setNewData(mList);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 1002:
                    // 删除雷管后更新列表
                    mListData = new GreenDaoMaster().querySrDetonatorAllAsc();
                    mList.clear();
                    Log.e("加载", "mListData.size(): " + mListData.size());
                    for (SingleRegisterDenator item : mListData) {
                        SingleRegisterData singleRegisterData = new SingleRegisterData();
                        singleRegisterData.setId(item.getId());
                        singleRegisterData.setShellBlastNo(item.getShellBlastNo());
                        singleRegisterData.setDetonatorId(item.getDetonatorId());
                        singleRegisterData.setCong_yscs(item.getCong_yscs());
                        singleRegisterData.setZhu_yscs(item.getZhu_yscs());
                        singleRegisterData.setQibao(item.getQibao());
                        singleRegisterData.setDelay(item.getDelay());
                        singleRegisterData.setTime(item.getTime());
                        if (!mList.contains(singleRegisterData)) {
                            mList.add(singleRegisterData);
                        }
                    }
                    mAdapter.setNewData(mList);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.showCheckBox(false);
                    isSelectAll = true;
                    setAllItemChecked(false);
                    tv_check_all.setText(getResources().getString(R.string.text_qx));
                    layBottom.setVisibility(View.GONE);
                    SoundPlayUtils.play(1);
                    AppLogUtils.writeAppLog("点击了单发注册列表中多选删除雷管按钮");
                    break;
                // 电源显示
                case 1003:
                    if (busInfo != null) {
                        txtCurrentVolt.setText(getString(R.string.text_reister_vol) + busInfo.getBusVoltage() + "V");
                        txtCurrentIC.setText(getString(R.string.text_reister_ele) + Math.round(busInfo.getBusCurrentIa() * 1000*2) + "μA");
                        // 判断当前电流是否偏大
                        if (Math.round(busInfo.getBusCurrentIa() * 1000) > 70) {
                            txtCurrentIC.setTextColor(Color.RED);
                            txtCurrentIC.setText(getString(R.string.text_reister_ele) + Math.round(busInfo.getBusCurrentIa() * 1000*2) + "μA"+getString(R.string.text_line_pd));
                        } else {
                            txtCurrentIC.setTextColor(Color.GREEN);
                        }
                    }
                    break;
                default:
                    break;
            }

            return false;
        });
    }

    private void showUiToast(String msg) {
        mHandler_UI.post(() -> show_Toast(msg));
    }

    private int showDenatorSum() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<SingleRegisterDenator> list = master.querySrDetonatorAllAsc();
        txtReisteramount.setText(getString(R.string.text_reister_tip1) + list.size());
        return list.size();
    }

    /***
     * 处理
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf) {
        String fromCommad = Utils.bytesToHexFun(cmdBuf);
        AppLogUtils.writeAppXBLog(fromCommad);
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
//                Utils.writeRecord("--单发注册--:管壳码:" + serchShellBlastNo(detonatorId) + " 芯片码:" + detonatorId + "该雷管桥丝异常");
//                AppLogUtils.writeAppLog("--单发注册--:管壳码:" + serchShellBlastNo(detonatorId) + " 芯片码:" + detonatorId + "该雷管桥丝异常");
            Log.e(TAG,"管壳码:" + serchShellBlastNo(detonatorId) + " 芯片码:" + detonatorId + "该雷管桥丝异常");
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
            Log.e(TAG,"收到13退出自动注册模式指令了");
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
                    AppLogUtils.writeAppLog("单发注册:管壳码:" + serchShellBlastNo(detonatorId) + "芯片码" + zhuce_form.getDenaId() + "该雷管电流过大");
                    Utils.writeRecord("--单发注册--:管壳码:" + serchShellBlastNo(detonatorId) + "芯片码" + zhuce_form.getDenaId() + "该雷管电流过大");
                }
//                else {
                if (zhuce_form != null) {//管厂码,特征码,雷管id
//                        // 获取 管壳码
//                        String shellNo = new GreenDaoMaster().getShellNo(detonatorId);
                    insertSingleDenator(detonatorId, zhuce_form);//单发注册
                    zhuce_Flag = 0;
                    Log.e(TAG,"开始注册雷管了...detonatorId:" + detonatorId);
                }
            }
        } else if (DefCommand.CMD_4_XBSTATUS_7.equals(cmd)) { //46
            byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Exit12_4("00");//13
            sendCmd(powerCmd);
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
        DetonatorTypeNew detonatorTypeNew = serchForDetonatorTypeNew(detonatorId);
//        if (detonatorTypeNew == null) {//考虑到可以直接注册A6
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
//            return -1;
//        }

        if (checkSrRepeatdenatorId(detonatorId)) {//判断芯片码(要传13位芯片码,不要传8位的,里有截取方法)
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
        //判断管壳码
        if (detonatorTypeNew != null && detonatorTypeNew.getShellBlastNo().length() == 13 && checkSrRepeatShellNo(detonatorTypeNew.getShellBlastNo())) {
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
        int maxNo = new GreenDaoMaster().getSrDenatorMaxNum();
        // 获取 该区域 最大序号
        if (!zhuce_form.getWire().equals("无")) {//说明没有空余的序号可用
            maxNo++;
            //从绑码库中获取到的数据
            SingleRegisterDenator denatorBaseinfo = new SingleRegisterDenator();
//            DenatorBaseinfo denatorBaseinfo = serchLastLG();
            Log.e("列表最后一发雷管", "denatorBaseinfo: "+denatorBaseinfo.getShellBlastNo() );
            if (detonatorTypeNew != null && detonatorTypeNew.getShellBlastNo().length() == 13) {
                denatorBaseinfo.setShellBlastNo(detonatorTypeNew.getShellBlastNo());
                denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
                denatorBaseinfo.setRegdate(detonatorTypeNew.getTime());
                Utils.writeRecord("--单发注册--" + "注册雷管码:" + detonatorTypeNew.getShellBlastNo() + " --芯片码:" + zhuce_form.getDenaId());
            } else {
                denatorBaseinfo.setShellBlastNo(detonatorId);
                denatorBaseinfo.setZhu_yscs(zhuce_form.getZhu_yscs());
                denatorBaseinfo.setRegdate(Utils.getDateFormat(new Date()));
                Utils.writeRecord("--单发注册--" + " --芯片码:" + zhuce_form.getDenaId());
            }
            if(zhuce_form.getReadStatus().equals("F1")){
                denatorBaseinfo.setAuthorization("01");
            }else if(zhuce_form.getReadStatus().equals("F2")){
                denatorBaseinfo.setAuthorization("02");
            }else{
                denatorBaseinfo.setAuthorization("02");
            }
            Log.e("单发注册", "zhuce_form.getReadStatus(): "+zhuce_form.getReadStatus() );
            if (zhuce_form.getDenaIdSup() != null) {
                String detonatorId_Sup = Utils.GetShellNoById_newXinPian(zhuce_form.getFacCode(), zhuce_form.getFeature(), zhuce_form.getDenaIdSup());
                denatorBaseinfo.setDenatorIdSup(detonatorId_Sup);//从芯片
                denatorBaseinfo.setCong_yscs(detonatorTypeNew.getCong_yscs());
                Utils.writeRecord("--单发注册: 从芯片码:" + zhuce_form.getDenaIdSup());
            }
            denatorBaseinfo.setBlastserial(maxNo);
            denatorBaseinfo.setDelay(0);
            denatorBaseinfo.setDetonatorId(detonatorId);
            denatorBaseinfo.setStatusCode("02");
            denatorBaseinfo.setStatusName("已注册");
            denatorBaseinfo.setErrorCode("FF");
            denatorBaseinfo.setErrorName("正常");
            denatorBaseinfo.setWire(zhuce_form.getWire());//桥丝状态
            //向数据库插入数据
            getDaoSession().getSingleRegisterDenatorDao().insert(denatorBaseinfo);
        }
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        SoundPlayUtils.play(1);
        return 0;
    }

    /**
     * 检查单发注册列表中重复的数据
     *
     * @param detonatorId 芯片码
     * @return 是否重复
     */
    public boolean checkSrRepeatdenatorId(String detonatorId) {
        Log.e("检查重复的数据", "detonatorId: " + detonatorId);
        GreenDaoMaster master = new GreenDaoMaster();
        List<SingleRegisterDenator> list_lg = master.checkRepeatSrDenatorId(detonatorId.substring(5));
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
     * 检查雷管注中重复的数据
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
     * 查询生产表中对应的管壳码
     */
    private DetonatorTypeNew serchForDetonatorTypeNew(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryDetonatorForTypeNew(denatorId);
    }

    //发送命令
    public void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
//					mOutputStream.write(mBuffer);
                String str = Utils.bytesToHexFun(mBuffer);
//                Utils.writeLog("Reister sendTo:" + str);
                Log.e("发送命令", str);
                AppLogUtils.writeAppXBLog(str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            return;
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
     * 修改雷管延期 删除单发雷管弹窗
     */
    private void modifyBlastBaseInfo(int no, int delay, final String shellBlastNo) {
        Log.e(TAG,"当前雷管延时:" + delay);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delaymodifydialog, null);
        builder.setView(view);
        LinearLayout dm_ll_4 = view.findViewById(R.id.dm_ll_4);
        EditText et_no = view.findViewById(R.id.serialNo);
        EditText et_shell = view.findViewById(R.id.denatorNo);
        EditText et_delay = view.findViewById(R.id.delaytime);
        dm_ll_4.setVisibility(View.GONE);
        et_no.setText(String.valueOf(no));
        et_delay.setText(String.valueOf(delay));
        et_shell.setText(shellBlastNo);
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("删除", (dialog, which) -> {
            dialog.dismiss();
            // TODO 开启进度条
            new Thread(() -> {
                // 删除某一发雷管
                new GreenDaoMaster().deleteSrDetonator(shellBlastNo);
                AppLogUtils.writeAppLog("单发注册表中单发删除雷管:" + shellBlastNo);
//                Utils.deleteData(mRegion);//重新排序雷管
//                Utils.writeRecord("--删除雷管:" + shellBlastNo);
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
            }).start();
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            String delay1 = et_delay.getText().toString();
            if (maxSecond != 0 && Integer.parseInt(delay1) > maxSecond) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2001, getString(R.string.text_reister_tip9)  + maxSecond + "ms"));
            } else if (delay1.trim().length() < 1 || maxSecond > 0 && Integer.parseInt(delay1) > maxSecond) {
                show_Toast(getString(R.string.text_reister_tip8));
            } else {
//                Utils.writeRecord("-单发修改延时:" + "-管壳码:" + shellBlastNo + "-延时:" + delay1);
                // 修改雷管延时
                Log.e(TAG,"当前雷管修改后的延时:" + delay1);
                new GreenDaoMaster().updateSrDetonatorDelay(shellBlastNo, Integer.parseInt(delay1));
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                show_Toast(shellBlastNo + getString(R.string.text_dialog_xgcg));
                Utils.saveFile();
            }
            dialog.dismiss();
        });
        builder.show();
    }

    private boolean isSelectAll = true;//是否全选
    //是否单发注册
    private int isSingleReisher = 0;
    private boolean switchUid = true;//切换uid/管壳码
    @OnClick({R.id.tv_check_all,R.id.tv_input,R.id.title_right2,R.id.tv_cancel,R.id.tv_delete,
    R.id.re_gkm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.re_gkm:
                if (switchUid) {
                    switchUid = false;
                    mAdapter.setUid(false);
                    text_uid.setTextColor(Color.GREEN);
                    text_gkm.setTextColor(Color.BLACK);
                } else {
                    switchUid = true;
                    mAdapter.setUid(true);
                    text_uid.setTextColor(Color.BLACK);
                    text_gkm.setTextColor(Color.GREEN);
                }
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                break;
            case R.id.tv_cancel:
                mAdapter.showCheckBox(false);
                layBottom.setVisibility(View.GONE);
                isSelectAll = true;
                setAllItemChecked(false);
                tv_check_all.setText(getResources().getString(R.string.text_qx));
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.title_right2:
                if (isSingleReisher == 0 ) {
                    show_Toast(getResources().getString(R.string.text_line_tip1));
                    titleRight2.setText(getResources().getText(R.string.text_tzjc));
                    isSingleReisher = 1;
                    sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令
                } else  {
                    txtCurrentVolt.setText(R.string.text_reister_vol);
                    txtCurrentIC.setText(R.string.text_reister_ele);
                    txtCurrentIC.setTextColor(Color.BLACK);
                    txtCurrentVolt.setTextColor(Color.BLACK);
                    titleRight2.setText(getResources().getText(R.string.text_jczxlg));
                    isSingleReisher = 0;
                    // 13 退出注册模式
                    sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));
                }
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
            case R.id.tv_delete:
                List<SingleRegisterData> seleceList = new ArrayList<>();
                for (SingleRegisterData data : mList) {
                    if (data.isSelect()) {
                        seleceList.add(data);
                    }
                }
                if (seleceList.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_selectlg));
                    return;
                }
                Log.e(TAG,"选中的雷管数量:" + seleceList.size());
                AppLogUtils.writeAppLog("删除单发注册表中雷管数量:" + seleceList.size());
                if (!SingleRegisterActivity.this.isFinishing()) {
                    AlertDialog dialog = new AlertDialog.Builder(SingleRegisterActivity.this)
                            .setTitle(getResources().getString(R.string.text_fir_dialog2))
                            .setMessage(getResources().getString(R.string.text_his_sclg2))
                            //设置对话框的按钮
                            .setNeutralButton(getResources().getString(R.string.text_dialog_qx), (dialog1, which) -> {
                                dialog1.dismiss();
                            })
                            .setPositiveButton(getString(R.string.text_dialog_qd), (dialog14, which) -> {
                                runPbDialog();
                                new Thread(() -> {
                                    for (SingleRegisterData data : seleceList) {
                                        if (data.isSelect()) {
                                            if (data.getShellBlastNo().length() != 13) {
                                                new GreenDaoMaster().deleteSrDetonator(data.getId());
                                            } else {
                                                new GreenDaoMaster().deleteSrDetonator(data.getShellBlastNo());
                                            }
                                            AppLogUtils.writeAppLog("删除单发注册表中雷管:" + data.getShellBlastNo());
                                        }
                                    }
                                    mHandler_UI.sendMessage(mHandler_UI.obtainMessage(1));
                                    mHandler_0.sendMessage(mHandler_UI.obtainMessage(1002));
                                    pb_show = 0;
                                }).start();
                            }).create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                break;
            case R.id.tv_input:
                List<SingleRegisterData> sList = new ArrayList<>();
                for (SingleRegisterData data : mList) {
                    if (data.isSelect()) {
                        sList.add(data);
                    }
                }
                if (sList.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_selectlg));
                    return;
                }
                Log.e(TAG,"选中的雷管数量:" + sList.size());
                AppLogUtils.writeAppLog("单发注册表中导入注册列表的雷管数量是:" + sList.size());
                inputLeiGuan();
                break;
        }
    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
        new Thread(new Runnable() {

            @Override
            public void run() {
                mHandler2.sendMessage(mHandler2.obtainMessage());
                try {
                    while (pb_show == 1) {
                        Thread.sleep(100);
                    }
                    mHandler2.sendMessage(mHandler2.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     *   注册选中的item:跟授权注册一样，如果是选中了一发，就注册进对应的位或者孔，如果大于1发，就直接注册进最后
     */
    private void inputLeiGuan() {
        // 在主线程显示加载框
        pb_show = 1;
        runPbDialog();
        // 使用一个长度为1的 int 数组来共享 isSuccess
        final int[] registResult = {0};  // 0 表示失败，1 表示成功
        // 创建后台线程执行任务
        new Thread(new Runnable() {
            @Override
            public void run() {
                int a = 0; // 选中数量
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).isSelect()) {
                        a++;
                    }
                }
                // 根据选中的数量执行不同操作
                if (a == 1) {
                    for (SingleRegisterData data : mList) {
                        if (data.isSelect()) {
                            Log.e(TAG, "进行单发注册--雷管:" + data.getShellBlastNo());
                            int result = registerDetonator(data, true);
                            if (result == 1) {  // 如果返回值是1，表示成功
                                registResult[0] = 1;  // 注册成功，设置为 1
                            } else {
                                registResult[0] = 0;  // 注册失败，设置为 0
                                break;  // 一旦失败，退出循环
                            }
                        }
                    }
                } else {
                    for (SingleRegisterData data : mList) {
                        if (data.isSelect()) {
                            Log.e(TAG, "进行多发注册--雷管:" + data.getShellBlastNo());
                            int result = registerDetonator(data, false);
                            if (result == 1) {  // 如果返回值是1，表示成功
                                registResult[0] = 1;  // 注册成功，设置为 1
                            } else {
                                registResult[0] = 0;  // 注册失败，设置为 0
                                break;  // 一旦失败，退出循环
                            }
                        }
                    }
                }
                     // 在主线程更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb_show = 0;  // 隐藏加载框
                            if (registResult[0] == 1) {
                                // 如果注册成功，更新UI
                                Log.e(TAG, "导入注册成功了");
                                AppLogUtils.writeAppLog("单发注册页面雷管导入注册列表成功了");
                                show_Toast(getResources().getString(R.string.text_zccg));  // 显示成功消息
                                updateSrListView();
                            }
                        }
                    });
            }
        }).start();  // 启动后台线程
    }

    /**
     * 雷管导入到注册列表后   需从当前单发注册表删除刚才已注册的雷管
     */
    private void updateSrListView() {
        // 使用 Iterator 进行移除
        Iterator<SingleRegisterData> iterator = mList.iterator();
        while (iterator.hasNext()) {
            SingleRegisterData data = iterator.next();
            if (data.isSelect()) {
                iterator.remove();  // 使用 Iterator 的 remove 方法移除元素
                //导入成功后删除单发注册列表中的数据
                new GreenDaoMaster().deleteSrDetonator(data.getId());
                Log.e(TAG,data.getShellBlastNo() + "已导入注册列表,正在更新排");
            }
        }
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1002));
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
     * 注册雷管
     * @param db
     * @param flag:true(单发雷管授权注册  需注册到对应孔或位) false(多发雷管授权注册  直接注册到列表最后即可)
     */
    private int registerDetonator(SingleRegisterData db,Boolean flag) {
        boolean chongfu = false;
//        int maxNo = getMaxNumberNo();
        Log.e(TAG,"接收注册--shellNo: " + db.getShellBlastNo());
        if (db.getShellBlastNo().length() < 13) {
            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(3));
            return -1;
        }
        if (db.getShellBlastNo().length() > 13) {
            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(7));
            return -1;
        }
        //检查芯片码重复数据
        if (db.getDetonatorId() != null && db.getDetonatorId().length() == 13 && checkRepeatDenatorId(db.getDetonatorId())) {
            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(2));
            return -1;
        }
        if (checkRepeatShellNo(db.getShellBlastNo())) {//检查管壳码重复数据
            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(2));
            return -1;
        }
        PaiData paiData = GreenDaoMaster.getPaiData(mRegion, paiChoice + "");
        int maxNo = new GreenDaoMaster().getPieceMaxNum(mRegion);//获取该区域最大序号
        int maxKong = new GreenDaoMaster().getPieceAndPaiMaxKong(mRegion, paiChoice);//获取该区域最大孔号
        int total = new GreenDaoMaster().queryDetonatorPaiSize(mRegion, paiChoice + "");//获取该区域最大孔号
        int delay_max = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiChoice);//获取该区域 最大序号的延时
        int delay_min = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, paiChoice);
        int duanNo2 = new GreenDaoMaster().getPaiMaxDuanNo((maxKong + 1), mRegion, paiChoice);//获取该区域 最大duanNo
        int start_delay = Integer.parseInt(paiData.getStartDelay());//开始延时
        int f1 = Integer.parseInt(paiData.getKongDelay());//f1延时
        int f2 = 0;//f2延时(好像用不上了)
        int tk_num = 0;
        int kongSum = paiData.getKongNum();
        String f2_delay_data = paiData.getNeiDelay();
        int delay_start = delay_max;
        Log.e(TAG, "当前段最大延时1: " + delay_max);
        String facCode = Utils.getDetonatorShellToFactoryCodeStr(db.getShellBlastNo());
        //雷管信息有误，管厂码不正确，请检查
        if (factoryCode != null && factoryCode.trim().length() > 0 && factoryCode.indexOf(facCode) < 0) {
            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(9));
            return -1;
        }

        delay_max = getDelay(maxKong, delay_max, start_delay, f1, tk_num, f2, delay_min, duanNo2);
        Log.e(TAG, "计算后的延时: " + delay_max);
        String version = null;
        String yscs = null;
        if (db.getDetonatorId() != null) {
//            duan = db.getCong_yscs();
            version = db.getDenatorIdSup();
            yscs = db.getZhu_yscs();
        }

        String duan = "1";
        int duanNUM = new GreenDaoMaster().getDuanNo(mRegion, duan);
        Log.e(TAG,"搜索--duanNUM: "+duanNUM);
        Log.e(TAG,"搜索--maxKong: "+maxKong);
        maxNo++;
        DenatorBaseinfo denator = new DenatorBaseinfo();
        denator.setBlastserial((maxKong + 1));
        denator.setSithole((maxKong + 1) + "");
        denator.setDenatorId(db.getDetonatorId());
        denator.setShellBlastNo(db.getShellBlastNo());
        denator.setZhu_yscs(yscs);
//        denator.setDelay(Integer.parseInt(delay));//PT不更新延时
        if (!TextUtils.isEmpty(db.getQibao())) {
            if (db.getQibao().equals("雷管正常")||db.getQibao().equals("已起爆")) {
                denator.setRegdate(db.getTime());
            } else {
                denator.setRegdate(Utils.getDateFormat(new Date()));
            }
        } else {
            denator.setRegdate(Utils.getDateFormat(new Date()));
        }
        denator.setDelay(delay_max);
        denator.setStatusCode("02");
        denator.setStatusName("已注册");
        denator.setErrorCode("00");
        denator.setErrorName("");
        denator.setWire("");
        denator.setPiece(mRegion);
        denator.setDuanNo(1);
        denator.setDuan(Integer.parseInt(duan));
        denator.setPai(paiChoice+"");
        denator.setAuthorization(version);//导入默认是02版

        int duanNo1 = new GreenDaoMaster().getPaiMaxDuanNo(maxKong, mRegion, paiChoice);//获取该区域 最大duanNo
        if (!flag_t1 || (kongSum >= 1 + duanNo1)) {//判断同孔
            int kong = maxKong;

            if (duanNo1 == 0) {
                kong = maxKong + 1;
            }
            Log.e(TAG,"扫码-单孔多发判断--duanNo1: " + duanNo1);
            Log.e(TAG,"扫码-单孔多发判断--delay_start: " + delay_start);
            Log.e(TAG,"扫码-单孔多发判断--delay_min: " + delay_min);
            Log.e(TAG,"扫码-单孔多发判断--f2_delay_data: " + f2_delay_data);
            duanNo1 = duanNo1 + 1;
            denator.setSithole(kong + "");
            denator.setBlastserial(kong);
            denator.setDuanNo((duanNo1));
            if (duanNo1 > 1) {
                if (!flag_jh_f1) {//孔内是否递减
                    denator.setDelay((delay_min - Integer.parseInt(f2_delay_data)));
                } else {
                    denator.setDelay((delay_start + Integer.parseInt(f2_delay_data)));
                }
            }
        }
        DenatorBaseinfo denatorBaseinfo_choice = new GreenDaoMaster().queryDetonatorPaiAndKongAndWei(mRegion, paiChoice,kongChoice,weiChoice);
        Log.e(TAG, "flag: "+flag );
        if (denatorBaseinfo_choice == null) {
            //向数据库插入数据
            Log.e(TAG,db.getShellBlastNo() + "已导入注册列表" + paiChoice + "排" + weiChoice + "位");
            getDaoSession().getDenatorBaseinfoDao().insert(denator);
        } else {
            Log.e(TAG, "denatorBaseinfo_choice: " + denatorBaseinfo_choice.toString());
            if (flag && denatorBaseinfo_choice.getShellBlastNo().length() < 13) {
                Log.e(TAG, "更新排数据 雷管孔号: " + denatorBaseinfo_choice.getPai() + "-" + denatorBaseinfo_choice.getBlastserial() + "-" + denatorBaseinfo_choice.getDuanNo());
                denatorBaseinfo_choice.setDenatorId(denator.getDenatorId());
                denatorBaseinfo_choice.setShellBlastNo(denator.getShellBlastNo());
                denatorBaseinfo_choice.setZhu_yscs(denator.getZhu_yscs());
                denatorBaseinfo_choice.setAuthorization(denator.getAuthorization());
                Log.e(TAG,db.getShellBlastNo() + "已导入注册列表" + paiChoice + "排" + weiChoice + "位");
                getDaoSession().getDenatorBaseinfoDao().update(denatorBaseinfo_choice);
            } else {
                //向数据库插入数据
                Log.e(TAG,db.getShellBlastNo() + "已导入注册列表" + paiChoice + "排" + weiChoice + "位");
                getDaoSession().getDenatorBaseinfoDao().insert(denator);
            }
        }
        updataPaiData();
        return 1;
    }

    int flag1 = 0;
    int flag2 = 0;
    boolean flag_t1 = true;//同孔标志
    boolean flag_jh_f1 = true;//减号标志
    boolean flag_jh_f2 = true;//减号标志
    boolean btn_start = false;//减号标志
    boolean flag_tk = false;//跳孔标志
    private String delay_set = "f1";//是f1还是f2
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

    //更新排
    private void updataPaiData() {
        GreenDaoMaster master = new GreenDaoMaster();
        int total = master.queryDetonatorPaiSize(mRegion, paiChoice + "");//有过
        int delay_max_new = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, paiChoice);//获取该区域 最大序号的延时
        int delay_minNum_new = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, paiChoice);
        Log.e(TAG, "updataPaiData  total: " + total);
        PaiData choicepaiData = GreenDaoMaster.getPaiData(mRegion, paiChoice + "");
        if (choicepaiData != null) {
            choicepaiData.setSum(total + "");
            choicepaiData.setDelayMin(delay_minNum_new + "");
            choicepaiData.setDelayMax(delay_max_new + "");

            getDaoSession().getPaiDataDao().update(choicepaiData);
        }

    }
    /**
     * 得到最大序号
     */
    private int getMaxNumberNo() {
        return LitePal.max(DenatorBaseinfo.class, "blastserial", int.class);
    }

    /**
     * 检查雷管表中重复的数据
     *
     * @param denatorId
     */
    public boolean checkRepeatDenatorId(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> denatorBaseinfo = master.checkRepeatdenatorId(denatorId);
        if (denatorBaseinfo.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询雷管表中重复的芯片码
     * @param shellBlastNo
     * @return
     */
    public boolean checkRepeatShellNo(String shellBlastNo) {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> denatorBaseinfo = master.checkRepeatShellNo(shellBlastNo);
        if (denatorBaseinfo.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询单发注册列表中重复的芯片码
     * @param shellBlastNo
     * @return
     */
    public boolean checkSrRepeatShellNo(String shellBlastNo) {
        GreenDaoMaster master = new GreenDaoMaster();
        List<SingleRegisterDenator> denatorBaseinfo = master.checkRepeatSrShellNo(shellBlastNo);
        if (denatorBaseinfo.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //雷管全选和取消全选
    private void setAllItemChecked(boolean isSelected) {
        if (mAdapter == null) return;
        if (isSelected) {
            for (SingleRegisterData data : mList) {
                data.setSelect(true);
            }
        } else {
            for (SingleRegisterData data : mList) {
                data.setSelect(false);
            }
        }
        mAdapter.setNewData(mList);
        mAdapter.notifyDataSetChanged();
        index = mList.size();
    }


    @Override
    public void itemClick(View v, int position) {
        if (v.getId() == R.id.iv_edit) {
            SingleRegisterDenator info = mListData.get(position);
            int no = info.getBlastserial();
            int delay = info.getDelay();
            String shellBlastNo = info.getShellBlastNo();
            modifyBlastBaseInfo(no, delay, shellBlastNo);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            if (isSingleReisher != 0) {
                show_Toast(getResources().getString(R.string.text_reister_tip3));
            } else {
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tipDlg != null) {
            tipDlg.dismiss();
            tipDlg = null;
        }
    }
}