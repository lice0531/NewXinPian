package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.proguard.X;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.ThreeFiringCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.custom.ErrShouQuanListAdapter;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.models.DownloadVersionBean;
import android_serialport_api.xingbang.models.IsRenewBean;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.utils.upload.FTP;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;//主板上电
import static android_serialport_api.xingbang.Application.getContext;
import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.annotation.NonNull;

import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONException;
import org.json.JSONObject;


public class XingbangMain extends SerialPortActivity {

    @BindView(R.id.tv_main_no)
    TextView tvMainNo;
    @BindView(R.id.btn_main_reister)//注册
    Button btnMainReister;
    @BindView(R.id.btn_main_test)//测试
    Button btnMainTest;
    @BindView(R.id.btn_main_delayTime)//延时
    Button btnMainDelayTime;
    @BindView(R.id.btn_main_del)//删除
    Button btnMainDel;
    @BindView(R.id.btn_main_blast)//起爆
    Button btnMainBlast;
    @BindView(R.id.btn_main_query)//查询
    Button btnMainQuery;
    @BindView(R.id.btn_main_setevn)//设置
    Button btnMainSetevn;
    @BindView(R.id.btn_main_help)//帮助
    Button btnMainHelp;
    @BindView(R.id.btn_main_downWorkCode)//项目管理
    Button btnMainDownWorkCode;
    @BindView(R.id.btn_main_exit)//退出
    Button btnMainExit;
    @BindView(R.id.btn_main_exit2)
    Button btnMainExit2;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.tv_main_version)
    TextView tvMainVersion;
    @BindView(R.id.btn_main_lianxi)
    Button btnMainLianxi;
    @BindView(R.id.txt_currentVolt)
    TextView txt_Volt;
    @BindView(R.id.txt_currentIC)
    TextView txt_IC;
    private long time = 0;
    private ArrayList<Map<String, Object>> helpData = new ArrayList<Map<String, Object>>();//错误雷管
    private SQLiteDatabase db;
    private DatabaseHelper mMyDatabaseHelper;

    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String server_addr = "";
    private String server_port = "";
    private String server_http = "";
    private String server_ip = "";
    private String server_type1 = "";
    private String server_type2 = "";
    private String Yanzheng = "";//是否验证地理位置
    private String version = "02";//版本号
    private int Preparation_time;//准备时间
    private int ChongDian_time;//准备时间
    private int jiance_time;//检测时间
    private String qiaosi_set = "";//是否检测桥丝
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler_loading = new Handler();//显示进度条
    private Handler mHandler_updata = new Handler();//更新主页面信息
    private Handler mHandler_load = new Handler();//查了雷管
    private Handler mHandler_updataVersion = new Handler();//更新版本
    private List<DenatorBaseinfo> list_data = new ArrayList<>();
    private ArrayList<String> lg2_yanshi = new ArrayList<>();
    private String TAG = "主页面";

    private String mOldTitle;   // 原标题
    private String mRegion;     // 区域
    private int region_0, region_1, region_2, region_3, region_4, region_5;
    private boolean mRegion1, mRegion2, mRegion3, mRegion4, mRegion5 = true;//是否选中区域1,2,3,4,5
    // FTP参数
    private FTP mFTP;
    private String mIP = "182.92.61.78";
    private String mUserName = "xingbang";
    private String mPassWord = "xingbang666";
    private String mSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xb";
    private List<FTPFile> mList_FtpFileName = new ArrayList<>();
    TextView totalbar_title;
    private int Yanzheng_sq_size = 0;
    private String Yanzheng_sq = "";//是否验雷管已经授权
    private String app_version_name = "";
    private From42Power busInfo;
    private Handler busHandler = null;//总线信息
    Handler openHandler = new Handler();//重新打开串口
    private SendPower sendPower;
    private OpenPower openPower;
    private volatile int get41Resp = 0;
    private String changjia = "";
    private boolean isCmdClosed = false;//是否已经关闭串口
    private boolean isRestarted = false;
    private boolean threadStarted = false;
    private boolean isshow1 = true;
    private int duanlu_sun=0;
    private List<DenatorBaseinfo> list_shou;
    //最大线程数设置为2，队列最大能存2，使用主线程执行的拒绝策略
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,2,0, TimeUnit.SECONDS,new LinkedBlockingQueue<>(2),new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        MessageBean messageBean = GreenDaoMaster.getAllFromInfo_bean();
        equ_no = messageBean.getEqu_no();
        Yanzheng_sq = (String) MmkvUtils.getcode("Yanzheng_sq", "不验证");
        changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
        Log.e(TAG, "验证授权Yanzheng_sq: " + Yanzheng_sq);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loadMoreData_all_lg();//获取雷管信息
        mHandler_updata.sendMessage(mHandler_updata.obtainMessage());//更新设备编号
        getPropertiesData();
        isRestarted = true;
//        getUserMessage();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xingbang_main);
        ButterKnife.bind(this);
        SQLiteStudioService.instance().start(this);

        Yanzheng_sq = (String) MmkvUtils.getcode("Yanzheng_sq", "不验证");
        Log.e(TAG, "验证授权Yanzheng_sq: " + Yanzheng_sq);
        changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
        Log.e(TAG, "changjia: " + changjia);
        if(changjia.equals("XJ")){
            app_version_name =getString(R.string.app_version_name2);
        }else if(changjia.equals("CQ")){
            app_version_name =getString(R.string.app_version_name3);
        }else {
            app_version_name =getString(R.string.app_version_name);
        }


        initPower();                // 初始化上电方式()
        initView();         // 初始化控件

        tipDlg = new LoadingDialog(XingbangMain.this);
        initHandler();//初始化handler
        pb_show = 1;
        runPbDialog();
        new Thread() {
            @Override
            public void run() {
                if (queryTotal() == 0) {
                    readCVS();//读取雷管列表
                }
                Log.e("读取数据", "version: " + version);
                readCVS_pro();//把备份的信息写入到数据库中
                pb_show = 0;
                getUserMessage();//获取用户信息
                GreenDaoMaster.setDenatorType();//延时最大值默认值
//                GreenDaoMaster.setFactory();//厂家码默认值
            }
        }.start();
        loadMoreData_all_lg();//查询雷管延时是否为0
        mHandler_updata.sendMessage(mHandler_updata.obtainMessage());//更新设备编号
//        getMaxNumberNo();
        Utils.writeRecord("---进入主页面---");

        getleveup();

        busHandler = new Handler(msg -> {
            txt_Volt.setText(getResources().getString(R.string.text_reister_vol)+ busInfo.getBusVoltage() + "V");
            txt_IC.setText(getResources().getString(R.string.text_reister_ele) + (int)busInfo.getBusCurrentIa() + "μA");
            String displayIcStr = (int) busInfo.getBusCurrentIa() + "μA";//保留两位小数
            float displayIc = busInfo.getBusCurrentIa();

            if (displayIc > 21000 ) {
//                displayIcStr = getResources().getString(R.string.text_reister_ele) + displayIcStr + getString(R.string.text_text_ysdl);
                txt_IC.setTextColor(Color.RED);//设置颜色
//                txt_IC.setText(displayIcStr);
                Utils.writeRecord("--主页--当前电流:" + displayIcStr + "  当前电压:" + busInfo.getBusVoltage() + "V,疑似短路");
            }else {
//                displayIcStr = getResources().getString(R.string.text_reister_ele) + displayIcStr;
                txt_IC.setTextColor(Color.WHITE);//设置颜色
//                txt_IC.setText(displayIcStr);
            }
            if (busInfo.getBusVoltage() < 6&&isshow1) {
                Utils.writeRecord("--主页--:总线短路");
                isshow1=false;
                TextView view = new TextView(this);
                view.setTextSize(25);
                view.setTextColor(Color.RED);
                view.setText("总线线路异常,可能有短路的情况，请检查线路");
                view.setTypeface(null, Typeface.BOLD);
                AlertDialog dialog = new AlertDialog.Builder(XingbangMain.this)
                        .setTitle(getString(R.string.text_fir_dialog7))//设置对话框的标题//"成功起爆"
                        .setView(view)//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNeutralButton(getString(R.string.text_test_exit), (dialog12, which) -> {
                            sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
                            dialog12.dismiss();
                            finish();
                        })
                        .setNegativeButton(getResources().getString(R.string.text_firing_jixu), (dialog1, which) -> {
                            isshow1=true;
                            dialog1.dismiss();
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                dialog.show();
            }
            if ((int)busInfo.getBusCurrentIa()>30000) {
                duanlu_sun++;
                if(duanlu_sun==6){

                    Utils.writeRecord("--主页--:总线短路");
                    TextView view = new TextView(this);
                    view.setTextSize(25);
                    view.setTextColor(Color.RED);
                    view.setText(getString(R.string.text_fir_dlyc));
                    view.setTypeface(null, Typeface.BOLD);
                    AlertDialog dialog = new AlertDialog.Builder(XingbangMain.this)
                            .setTitle(getString(R.string.text_fir_dlgd))//设置对话框的标题//"成功起爆"
                            .setView(view)
                            //设置对话框的按钮
                            .setNeutralButton (getString(R.string.text_test_exit), (dialog12, which) -> {
                                sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
                                dialog12.dismiss();
                                finish();
                                close();
                                duanlu_sun=0;
                            })
                            .setNegativeButton(getString(R.string.text_firing_jixu), (dialog1, which) -> {
                                duanlu_sun=0;
                                dialog1.dismiss();
                            })
                            .create();
                    dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                    dialog.show();
                }

            }
            return false;
        });
        openPower = new OpenPower();
        openPower.start();

//        sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令

//        15:55:02:883 ->:C0003400CD05C0
//        15:55:03:039 <-:C000170A000039087E0338088203ADFFC0
//        15:55:03:091 <-:C0003401FF64B0C0
//        String cmd = "C0003401FF64B0C0";//
//        Log.e(TAG, "completeValidCmd(cmd): "+completeValidCmd(cmd) );
//        Log.e(TAG, "DefCommand.getCmd2(fromCommad): "+DefCommand.getCmd2(cmd) );
//        Log.e(TAG, "DefCommand.decodeCommand(fromCommad): "+DefCommand.decodeCommand(cmd) );

    }
    @Override
    protected void onResume() {
        duanlu_sun=0;
        getPropertiesData();//重新读取备份数据会导致已修改的数据重置
        // 获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);
        mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);
        mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);
        mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);
        mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);
        // 设置标题区域
        setTitleRegion();
        // 获取区域雷管数量
        getRegionNumber();
        super.onResume();
        // 创建一个 Handler 对象

        // 延时3秒后执行的操作
        openHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openSerial();
            }
        }, 2500);

    }

    private void openSerial(){
        if (isRestarted) {
            initSerialPort();
            openPower = new OpenPower();
            openPower.start();
            Log.e(TAG,"重新开启41指令线程");
        }
    }

    private void queryBeian() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format1 = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        GreenDaoMaster master = new GreenDaoMaster();
        list_shou = master.queryLeiGuan(format1, mRegion);
        Yanzheng_sq_size = list_shou.size();
        Log.e(TAG, "超过授权日期list_shou: " + list_shou.size());
        Log.e(TAG, "超过授权日期list_shou: " + list_shou.toString());
    }

    /**
     * 初始化控件
     */
    private void initView() {
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        // 获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);
        mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);
        mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);
        mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);
        mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);

        totalbar_title = findViewById(R.id.title_text);

        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setOnClickListener(v -> {
            choiceQuYu();
        });
        iv_back.setOnClickListener(v -> finish());
        iv_back.setVisibility(View.GONE);
        // 原标题
        mOldTitle = getSupportActionBar().getTitle().toString();
        // 设置标题区域
        setTitleRegion();

    }

    private void choiceQuYu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle(R.string.text_dialog_choice);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choice_quyu, null);
        builder.setView(view);
        final CheckBox cb_mRegion1 = view.findViewById(R.id.dialog_cb_mRegion1);
        final CheckBox cb_mRegion2 = view.findViewById(R.id.dialog_cb_mRegion2);
        final CheckBox cb_mRegion3 = view.findViewById(R.id.dialog_cb_mRegion3);
        final CheckBox cb_mRegion4 = view.findViewById(R.id.dialog_cb_mRegion4);
        final CheckBox cb_mRegion5 = view.findViewById(R.id.dialog_cb_mRegion5);
        cb_mRegion1.setChecked(mRegion1);
        cb_mRegion2.setChecked(mRegion2);
        cb_mRegion3.setChecked(mRegion3);
        cb_mRegion4.setChecked(mRegion4);
        cb_mRegion5.setChecked(mRegion5);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {

            if (cb_mRegion1.isChecked() || cb_mRegion2.isChecked() || cb_mRegion3.isChecked() || cb_mRegion4.isChecked() || cb_mRegion5.isChecked()) {
                StringBuilder a = new StringBuilder();
                mRegion1 = cb_mRegion1.isChecked();
                mRegion2 = cb_mRegion2.isChecked();
                mRegion3 = cb_mRegion3.isChecked();
                mRegion4 = cb_mRegion4.isChecked();
                mRegion5 = cb_mRegion5.isChecked();
                if (mRegion1) {
                    a.append(getString(R.string.text_zone1));
                }
                if (mRegion2) {
                    a.append(",2");
                }
                if (mRegion3) {
                    a.append(",3");
                }
                if (mRegion4) {
                    a.append(",4");
                }
                if (mRegion5) {
                    a.append(",5");
                }
                MmkvUtils.savecode("mRegion1", mRegion1);
                MmkvUtils.savecode("mRegion2", mRegion2);
                MmkvUtils.savecode("mRegion3", mRegion3);
                MmkvUtils.savecode("mRegion4", mRegion4);
                MmkvUtils.savecode("mRegion5", mRegion5);
                // 区域 更新视图
//                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                //更新标题
                setTitleRegion();
                // 显示提示
                show_Toast(getString(R.string.text_suidao_yxz) + a);
                mHandler_load.sendMessage(mHandler_load.obtainMessage());
            } else {
                show_Toast(getString(R.string.text_suidao_tip));
            }

        });
        builder.setNeutralButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void initHandler() {
        mHandler_loading = new Handler(this.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (pb_show == 1 && tipDlg != null) tipDlg.show();
                if (pb_show == 0 && tipDlg != null) {
                    tipDlg.hide();
                }
            }
        };
        mHandler_updata = new Handler(msg -> {
            Log.e("起爆器编号", "equ_no: " + equ_no);
            if (!equ_no.equals("")) {
                tvMainNo.setText(getString(R.string.text_query_num) + equ_no);
                CrashReport.setUserId(equ_no);
            }
            return false;
        });

        mHandler_load = new Handler(msg -> {
            loadMoreData_all_lg();//查询雷管延时是否为0
            return false;
        });

        mHandler_updataVersion = new Handler(msg -> {
            if (msg.what == 1) {
                DownloadVersionBean path = (DownloadVersionBean) msg.obj;
                createDialog_download(path, getString(R.string.text_updata_sys_2), 1);
            }
            if (msg.what == 2) {
                DownloadVersionBean path = (DownloadVersionBean) msg.obj;
                createDialog_download(path, getString(R.string.text_updata_sys_6), 2);
            }

            return false;
        });
    }


    private void runPbDialog() {
        pb_show = 1;
        tipDlg = new LoadingDialog(XingbangMain.this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
        //tipDlg.setMessage("正在操作,请等待...").show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                mHandler_loading.sendMessage(mHandler_loading.obtainMessage());
                try {
                    while (pb_show == 1) {
                        Thread.sleep(100);
                    }
                    mHandler_loading.sendMessage(mHandler_loading.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getUserMessage() {
        List<MessageBean> message = getDaoSession().getMessageBeanDao().loadAll();
//        Log.e(TAG, "message: " + message.toString());
        if (message.size() > 0) {
            pro_bprysfz = message.get(0).getPro_bprysfz();
            pro_htid = message.get(0).getPro_htid();
            pro_xmbh = message.get(0).getPro_xmbh();
            equ_no = message.get(0).getEqu_no();
            pro_coordxy = message.get(0).getPro_coordxy();
            server_addr = message.get(0).getServer_addr();
            server_port = message.get(0).getServer_port();
            server_http = message.get(0).getServer_http();
            server_ip = message.get(0).getServer_ip();
            qiaosi_set = message.get(0).getQiaosi_set();
            Preparation_time = Integer.parseInt(message.get(0).getChongdian_time());
            ChongDian_time = Integer.parseInt(message.get(0).getChongdian_time());
            server_type1 = message.get(0).getServer_type1();
            server_type2 = message.get(0).getServer_type2();
        } else {
            setUserMessage();//如果为空就新建一个
        }
    }

    private void getPropertiesData() {
        pro_bprysfz = (String) MmkvUtils.getcode("pro_bprysfz", "");
        pro_htid = (String) MmkvUtils.getcode("pro_htid", "");
        pro_xmbh = (String) MmkvUtils.getcode("pro_xmbh", "");
        equ_no = (String) MmkvUtils.getcode("equ_no", "");
        pro_coordxy = (String) MmkvUtils.getcode("pro_coordxy", "");
        server_addr = (String) MmkvUtils.getcode("server_addr", "");
        server_port = (String) MmkvUtils.getcode("server_port", "6088");
        server_http = (String) MmkvUtils.getcode("server_http", "http://qq.mbdzlg.com/mbdzlgtxzx/servlet/DzlgSysbJsonServlert");
        server_ip = (String) MmkvUtils.getcode("server_ip", "119.29.111.172");
        qiaosi_set = (String) MmkvUtils.getcode("qiaosi_set", "false");
        Preparation_time = (int) MmkvUtils.getcode("preparation_time", 48);//组网检测时间
        ChongDian_time = (int) MmkvUtils.getcode("chongdian_time", 68);
        server_type1 = (String) MmkvUtils.getcode("server_type1", "1");
        server_type2 = (String) MmkvUtils.getcode("server_type2", "0");
        pro_dwdm = (String) MmkvUtils.getcode("pro_dwdm", "");
        jiance_time = (int) MmkvUtils.getcode("jiance_time", 48);
        Yanzheng = (String) MmkvUtils.getcode("Yanzheng", "验证");
        version = (String) MmkvUtils.getcode("version", "02");
        Log.e(TAG, "Yanzheng: " + Yanzheng);
    }

    private void setUserMessage() {
        MessageBean message = new MessageBean();
        message.setId((long) 1);
        message.setPro_bprysfz("");
        message.setPro_htid("");
        message.setPro_xmbh("");
        message.setEqu_no("");
        message.setPro_coordxy("");
        message.setServer_addr("");
        message.setServer_port("6088");
        message.setServer_http("http://qq.mbdzlg.com/mbdzlgtxzx/servlet/DzlgSysbJsonServlert");
        message.setServer_ip("119.29.111.172");
        message.setQiaosi_set("false");
        message.setPreparation_time("48");
        message.setChongdian_time("68");
        message.setServer_type1("1");
        message.setServer_type2("0");
        message.setPro_dwdm("");
        message.setJiance_time("48");
        message.setVersion("02");
        getDaoSession().getMessageBeanDao().insert(message);
        Utils.saveFile_Message();//把软存中的数据存入磁盘中
    }

    private void loginToSetEnv() {

        AlertDialog.Builder builder = new AlertDialog.Builder(XingbangMain.this);
        //  builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getString(R.string.text_alert_user));//"请输入用户名和密码"
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(XingbangMain.this).inflate(R.layout.userlogindialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        final EditText username = (EditText) view.findViewById(R.id.username);
        final EditText password = (EditText) view.findViewById(R.id.password);
        username.setInputType(InputType.TYPE_CLASS_TEXT);
        // username.setText("xingbang");
        username.setFocusable(true);
        username.setFocusableInTouchMode(true);
        username.requestFocus();
        username.findFocus();
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {
            String a = username.getText().toString().trim();
            String b = password.getText().toString().trim();
            if (a.trim().length() < 1) {
                show_Toast(getString(R.string.text_alert_username));
//                    dialogOn(dialog);//取消按钮不可点击
                return;
            }
            if (b.trim().length() < 1) {
                show_Toast(getString(R.string.text_alert_password));
//                    dialogOn(dialog);
                return;
            }
            if (a.equals("xingbang") && b.equals("123456")) {
                close();//停止访问电流
                String str1 = "设置";
                Intent intent = new Intent(XingbangMain.this, SetEnvMainActivity.class);
                intent.putExtra("dataSend", str1);
                startActivity(intent);

                dialog.dismiss();
            } else if (!a.equals("xingbang")) {
                show_Toast(getString(R.string.text_main_yhmcw));
                dialogOn(dialog);
            } else if (!b.equals("123456")) {
                show_Toast(getString(R.string.text_main_mmcw));
                dialogOn(dialog);
            } else {
                show_Toast(getString(R.string.text_error_tip50));
                dialogOn(dialog);
//                    dialog.dismiss();
            }

            //  builder.
        });
        builder.setNeutralButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
            //builder.
            dialogOFF(dialog);
//                finish();
            dialog.dismiss();
        });
        builder.show();
    }

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


    //退出方法
    private void exit() {

        //如果在两秒大于2秒
        if (System.currentTimeMillis() - time > 2000) {
            //获得当前的时间
            time = System.currentTimeMillis();
            show_Toast(getString(R.string.text_error_tip56));
        } else {
            Utils.writeRecord("---点击返回按键退出程序---");
            powerOffDevice(PIN_ADSL);//主板下电
            //点击在两秒以内
            removeALLActivity();//执行移除所以Activity方法
        }
    }

    int m6KeyDown_Action, m6KeyUp_Action;
    int m0KeyDown_Action, m0KeyUp_Action;
    int keyFlag = 0;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            exit();//退出方法
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_1 && event.getAction() == KeyEvent.ACTION_DOWN) {
            m6KeyDown_Action = 1;//记录按下状态
            m6KeyUp_Action = 0;

        } else if (keyCode == KeyEvent.KEYCODE_1 && event.getAction() == KeyEvent.ACTION_UP) {
            //m6KeyDown_Action = 1;
            m6KeyUp_Action = 2;
        } else if (keyCode == KeyEvent.KEYCODE_5 && event.getAction() == KeyEvent.ACTION_DOWN) {
            m0KeyDown_Action = 1;  //记录按下状态
            m0KeyUp_Action = 0;
            if (m6KeyDown_Action == 1 && m6KeyUp_Action == 2) {
                keyFlag = 1;
                m6KeyDown_Action = 0;
            } else {
                m6KeyUp_Action = 0;
                m6KeyDown_Action = 0;
            }
        } else if (keyCode == KeyEvent.KEYCODE_5 && event.getAction() == KeyEvent.ACTION_UP) {
            m0KeyDown_Action = 0;
            m0KeyUp_Action = 2;
        }

        if (m0KeyUp_Action == 2 && m6KeyUp_Action == 2 && keyFlag == 1) {
            m0KeyUp_Action = 0;
            m6KeyUp_Action = 0;
            keyFlag = 0;
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, XingbangMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //全局定义
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 2000; // 快速点击间隔

    @OnClick({R.id.btn_main_reister, R.id.btn_main_test, R.id.btn_main_delayTime, R.id.btn_main_del, R.id.btn_main_blast, R.id.btn_main_query, R.id.btn_main_setevn, R.id.btn_main_help, R.id.btn_main_downWorkCode, R.id.btn_main_exit})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.btn_main_reister://注册
                close();//停止访问电流
                String str1 = "注册";
                Intent intent = new Intent(XingbangMain.this, ReisterMainPage_scan.class);//金建华
                intent.putExtra("dataSend", str1);
                startActivityForResult(intent, 1);
                break;

            case R.id.btn_main_test://测试
                //2次点击
                if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                    return;
                }
                lastClickTime = System.currentTimeMillis();

                queryBeian();
                //验证是否授权
                if (Yanzheng_sq.equals("验证") && Yanzheng_sq_size > 0) {
                    initDialog_shouquan();
                    return;
                }



                long time = System.currentTimeMillis();
                long endTime = (long) MmkvUtils.getcode("endTime", (long) 0);
                if (time - endTime < 180000) {//第二次启动时间不重置
                    int a = (int) (180000 - (time - endTime)) / 1000 + 5;
                    if (a < 180000) {
                        initDialog_fangdian(getString(R.string.text_main_tip1), a, "组网");
                    }
                    return;
                }
                close();//停止访问电流
                Log.e("测试页面", "测试: ");
                String str2 = "测试";
                Intent intent2 = new Intent(XingbangMain.this, TestDenatorActivity.class);
                intent2.putExtra("dataSend", str2);
                startActivityForResult(intent2, 1);
                break;

            // 单发检测
            case R.id.btn_main_delayTime:
                close();//停止访问电流
                Intent intent3 = new Intent(this, ReisterMainPage_line.class);
                startActivityForResult(intent3, 1);
                break;

            // 删除
            case R.id.btn_main_del:
                close();//停止访问电流
                startActivityForResult(new Intent(this, DelDenatorMainPage.class), 1);
                break;

            case R.id.btn_main_blast://起爆
                if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                    return;
                }
                lastClickTime = System.currentTimeMillis();
                //
                queryBeian();
                //验证是否授权
                if (Yanzheng_sq.equals("验证") && Yanzheng_sq_size > 0) {
                    initDialog_shouquan();
                    return;
                }
                time = System.currentTimeMillis();
                endTime = (long) MmkvUtils.getcode("endTime", (long) 0);

                if (time - endTime < 180000) {//第二次启动时间不重置
                    int a = (int) (180000 - (time - endTime)) / 1000 + 5;
                    if (a < 180000) {
                        initDialog_fangdian(getString(R.string.text_main_tip1), a, "起爆");
                        return;
                    }
//                    Log.e(TAG, "a: "+a);
//                    Log.e(TAG, "time: "+time);
//                    Log.e(TAG, "endTime: "+endTime);
                }

                String str5 = "起爆";
                Log.e("验证2", "Yanzheng: " + Yanzheng);
                Intent intent5;//金建华
                if (Yanzheng.equals("验证")) {
                    //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
                    intent5 = new Intent(this, VerificationActivity.class);
                } else {
                    Log.e(TAG, "验证2: "+Yanzheng_sq);
                    intent5 = new Intent(this, FiringMainActivity.class);
                }
                close();//停止访问电流
                intent5.putExtra("dataSend", str5);
                startActivityForResult(intent5, 1);
                break;

            case R.id.btn_main_query://查看
                String str6 = "查看雷管";
                close();//停止访问电流
                Intent intent6 = new Intent(XingbangMain.this, QueryMainActivity.class);
                intent6.putExtra("dataSend", str6);
                startActivityForResult(intent6, 1);
                break;

            case R.id.btn_main_setevn://设置
                loginToSetEnv();
                break;

            case R.id.btn_main_help://辅助功能
//                createHelpDialog();
                close();//停止访问电流
                String str8 = "查看雷管";
                Intent intent8 = new Intent(this, PracticeActivity.class);
                intent8.putExtra("dataSend", str8);
                startActivityForResult(intent8, 1);
                break;

            case R.id.btn_main_downWorkCode://下载
                close();//停止访问电流
                String str7 = "下载";
                Intent intent7 = new Intent(XingbangMain.this, DownWorkCode.class);
                intent7.putExtra("dataSend", str7);
                startActivityForResult(intent7, 1);
//            Intent intent7 = new Intent(XingbangMain.this, SetDelayTime.class);
//            startActivity(intent7);
                break;

            case R.id.btn_main_exit://退出
                close();//停止访问电流
                exit();//退出方法
                break;
        }
    }

    /**
     * 读取备份文件
     */
    private void readCVS() {
        int i = 0;
        String path = Environment.getExternalStorageDirectory() + "/xb/" + "list.csv";
        File f = new File(path);

        if (!f.exists()) {
            return;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (i == 0) {//去掉第一行文字表头
                    i = 1;
                    continue;
                }
                String a[] = line.replace("null", "").split(",", -1);
//          Log.e("写入文件数据",
//          "序号：" + a[0] + ",孔号：" + a[1] + ",管壳码：" + a[2] + ",延期：" + a[3] + ",状态：" + a[4]
//          + ",错误：" + a[5] + ",授权期限：" + a[6] + ",序列号：" + a[7] + ",备注：" + a[8]);
                if (a.length == 21) {
//向数据库插入数据
                    DenatorBaseinfo baseinfo = new DenatorBaseinfo();
                    baseinfo.setBlastserial(Integer.parseInt(a[1]));
                    baseinfo.setSithole(a[2]);
                    baseinfo.setShellBlastNo(a[3]);
                    baseinfo.setDenatorId(a[4]);
                    baseinfo.setDelay(Integer.parseInt(a[5]));
                    baseinfo.setStatusCode(a[6]);
                    baseinfo.setStatusName(a[7]);
                    baseinfo.setErrorName(a[8]);
                    baseinfo.setErrorCode(a[9]);
                    baseinfo.setAuthorization(a[10]);
                    baseinfo.setRemark(a[11]);
                    baseinfo.setRegdate(a[12]);
                    baseinfo.setWire(a[13]);
                    baseinfo.setName(a[14]);
                    baseinfo.setDenatorIdSup(a[15]);
                    baseinfo.setZhu_yscs(a[16]);
                    baseinfo.setCong_yscs(a[17]);
                    baseinfo.setPiece(a[18]);
                    baseinfo.setDuan(Integer.parseInt(a[19]));
                    baseinfo.setDuanNo(Integer.parseInt(a[20]));
                    getDaoSession().getDenatorBaseinfoDao().insert(baseinfo);
                } else {
                    f.delete();//如果字段个数不对,先删除list,再跳出循环
                    return;
                }

                i++;
            }
        } catch (FileNotFoundException e) {
            Log.e("读取备份", "readCVS: 1");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("读取备份", "readCVS: 2");
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("读取备份", "readCVS: 3");
            }
        }
    }

    private void readCVS_pro() {
        getPropertiesData();//读取偏好存储
        String path = Environment.getExternalStorageDirectory() + "/Xingbang/" + "properties.ini";
        File f = new File(path);
        if (!f.exists()) {
            return;
        }
        MessageBean message = new MessageBean();
        message.setPro_bprysfz(pro_bprysfz);
        message.setPro_htid(pro_htid);
        message.setPro_xmbh(pro_xmbh);
        message.setEqu_no(equ_no);
        message.setPro_coordxy(pro_coordxy);
        message.setServer_addr(server_addr);
        message.setServer_port(server_port);
        message.setServer_http(server_http);
        message.setServer_ip(server_ip);
        message.setQiaosi_set(qiaosi_set);
        message.setPreparation_time(String.valueOf(Preparation_time));
        message.setChongdian_time(String.valueOf(ChongDian_time));
        message.setServer_type1(server_type1);
        message.setServer_type2(server_type2);
        message.setPro_dwdm(pro_dwdm);
        message.setJiance_time(String.valueOf(jiance_time));
        message.setVersion(version);//单片机系统版本/旧01/新02
        if (queryMessage() == 1) {
            message.setId((long) 1);
            getDaoSession().getMessageBeanDao().update(message);
        } else {
            getDaoSession().getMessageBeanDao().insert(message);
        }
        Log.e("读取数据", "readCVS_pro: ");
    }

    private void loadMoreData_all_lg() {
        lg2_yanshi.clear();
        list_data.clear();
        list_data = new GreenDaoMaster().queryDetonatorRegionDesc();
        for (int i = 0; i < list_data.size(); i++) {
            lg2_yanshi.add(list_data.get(i).getDelay() + "");
        }
    }


    /***
     * 建立对话框
     */
    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_alert_tip_wsq));//"说明"
        builder.setMessage(getString(R.string.text_alert_tip_wsqtx));
        builder.setNegativeButton(getString(R.string.text_alert_tip_fhck), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    /***
     * 建立对话框
     */
    public void createDialog_kaiguan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_alert_tip_aqtx));//"说明"
        builder.setMessage(getString(R.string.text_alert_tip_gbkg));
        builder.setNegativeButton(getString(R.string.text_return), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private long queryTotal() {
        return getDaoSession().getDenatorBaseinfoDao().count();
    }

    private long queryMessage() {
        return getDaoSession().getMessageBeanDao().count();
    }


    /***
     * 得到最大序号
     * @return
     */
    private int getMaxNumberNo() {
        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao().queryBuilder().orderDesc(DenatorBaseinfoDao.Properties.Blastserial).list();
        if (list.size() > 0) {
            return list.get(0).getBlastserial();
        }
        return 1;
    }

    /**
     * 检查重复的数据
     *
     * @param shellBlastNo
     */
    public int checkRepeatShellNo(String shellBlastNo) {
        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao().queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(shellBlastNo)).list();
        if (list.size() > 0) {
            return 1;//重复
        } else {
            return 0;
        }
    }

    /**
     * 设置标题区域
     */
    private void setTitleRegion() {
        StringBuilder a = new StringBuilder();
        if (mRegion1) {
            a.append("1");
        }
        if (mRegion2) {
            a.append(",2");
        }
        if (mRegion3) {
            a.append(",3");
        }
        if (mRegion4) {
            a.append(",4");
        }
        if (mRegion5) {
            a.append(",5");
        }
        String str = getString(R.string.text_list_piace) + a;
        // 设置标题
        getSupportActionBar().setTitle(mOldTitle + changjia + str);
        // 保存区域参数(单选的时候要放开,多选关闭)
//        SPUtils.put(this, Constants_SP.RegionCode, mRegion);
        totalbar_title.setText(mOldTitle + changjia + "/" + str);
    }

    /**
     * 获取区域雷管数量
     */
    private void getRegionNumber() {
        GreenDaoMaster g = new GreenDaoMaster();
        region_0 = g.queryAllDetonatorAsc().size();
        region_1 = g.queryDetonatorRegionDesc("1").size();
        region_2 = g.queryDetonatorRegionDesc("2").size();
        region_3 = g.queryDetonatorRegionDesc("3").size();
        region_4 = g.queryDetonatorRegionDesc("4").size();
        region_5 = g.queryDetonatorRegionDesc("5").size();

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
                // 设置标题区域
                setTitleRegion();
                // 显示提示
                show_Toast(getString(R.string.text_show_1) + mRegion);
                // 延时选择重置
//                resetView();
//                delay_set = "0";
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private TextView mOffTextView;
    private Handler mOffHandler;
    private java.util.Timer mOffTime;
    private android.app.Dialog mDialog;

    private void initDialog_fangdian(String tip, int daojishi, String c) {
        String str5 = c;
        Log.e(TAG, "倒计时: " + daojishi);
        mOffTextView = new TextView(this);
        mOffTextView.setTextSize(25);
        mOffTextView.setText(tip + "\n" + getString(R.string.text_fir_dialog1));
        mDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.text_fir_dialog2))
                .setCancelable(false)
                .setView(mOffTextView)
//                .setPositiveButton("确定", (dialog, id) -> {
//                    mOffTime.cancel();//清除计时
//                    stopXunHuan();//关闭后的一些操作
//                })
                .setNeutralButton(getString(R.string.text_tc), (dialog, id) -> {
                    dialog.cancel();
                    mOffTime.cancel();
                })
                .setNegativeButton(getString(R.string.text_firing_jixu), (dialog2, which) -> {
                    dialog2.dismiss();
                    Intent intent5;//金建华
                    close();//停止访问电流
                    if (str5.equals("组网")) {
                        intent5 = new Intent(this, TestDenatorActivity.class);
                    } else {
                        Log.e("验证2", "Yanzheng: " + Yanzheng);
                        if (Yanzheng.equals("验证")) {
                            intent5 = new Intent(this, VerificationActivity.class);
                        } else {
                            intent5 = new Intent(this, FiringMainActivity.class);
                        }
                    }

                    intent5.putExtra("dataSend", str5);
                    startActivityForResult(intent5, 1);
                    mOffTime.cancel();
                })
                .create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);

        mOffHandler = new Handler(msg -> {
            if (msg.what > 0) {
                //动态显示倒计时
                mOffTextView.setText(tip + "\n" + getString(R.string.text_fir_dialog1) + msg.what);
            } else {
                //倒计时结束自动关闭
                if (mDialog != null) {
                    mDialog.dismiss();

                }
//                off();//关闭后的操作
                mOffTime.cancel();//终止此计时器，丢弃任何当前计划的任务
                mOffTime.purge();//从此计时器的任务队列中删除所有取消的任务
            }
            return false;
        });

        //倒计时

        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            private int countTime = daojishi;

            public void run() {
//                if(countTime==0){
//                    mOffTime.cancel();
//                    mOffTime.purge();
//                }
                if (countTime > 0) {
                    countTime--;
                }
                Log.e(TAG, "countTime: " + countTime);
                Message msg = new Message();
                msg.what = countTime;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 1000, 1000);
    }


    /***
     * 建立对话框
     */
    public void createDialog_download(DownloadVersionBean name, String message, int version) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_updata_sys_1);//"说明"
        builder.setMessage(message);
        builder.setPositiveButton(R.string.text_updata_sys_3, (dialog, which) -> {
//            show_Toast("当前系统程序有新版本,正在升级,请稍等!");
            close();//停止访问电流
            finish();
            if (version == 1) {
                Intent intent = new Intent(this, DownLoadActivity.class);
                intent.putExtra("dataSend", name.toString());
//            intent.putExtra("dataSend", "四川更新2");//11000版本升级
                startActivity(intent);
            } else {

                Intent intent = new Intent(this, UpgradeActivity.class);
                intent.putExtra("dataSend", name.toString());
                startActivity(intent);
            }

            dialog.dismiss();
        });
//        builder.setNeutralButton("退出", (dialog, which) -> {
//            dialog.dismiss();
//            finish();
//        });
        builder.setNeutralButton(R.string.text_updata_sys_4, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void getleveup() {
        String url = Utils.httpurl_xb_leveup;//公司服务器上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();

        try {
            object.put("sbbh", equ_no);//设备编号
            object.put("rj_version", app_version_name);//软件版本
            object.put("yj_version", MmkvUtils.getcode("yj_version", "默认版本"));//硬件版本
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = object.toString();
        MediaType JSON = MediaType.parse("application/json");
        Log.e("注册请求", "json: " + json);
        RequestBody requestBody = FormBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")//text/plain  application/json  application/x-www-form-urlencoded
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                Log.e("上传公司网络请求", "IOException: " + e);
//                Utils.writeRecord("上传公司网络请求失败" + "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) {

                try {
                    String res = response.body().string();
                    Log.e(TAG, "onResponse: " + res.toString());
                    Gson gson = new Gson();
                    IsRenewBean isRenewBean = gson.fromJson(res, IsRenewBean.class);
                    if (isRenewBean.getIs_rj_version() == 1) {
                        //需要升级app
                        getApp();
                    }
                    if (isRenewBean.getIs_yj_version() == 1) {
                        //需要升级bin
                        getBin();
                    }
                    pb_show = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void getApp() {
        String url = Utils.httpurl_xb_download;//公司服务器上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();

        try {
            String uniqueId =Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            PackageInfo pi = this.getPackageManager().getPackageInfo(Application.getContext().getPackageName(), 0);
            object.put("sbbh", equ_no);//设备编号
            object.put("machine", uniqueId);//设备唯一标识 8d47e396-daed-451d-9b0e-61bc1bb6b134
            object.put("version", app_version_name);//版本号版本 v3.22
//            object.put("version", "v3.22");//版本号版本 测试数据
            object.put("type", "1");//软件=1 硬件=2
            object.put("is_force", 0);//是否强制升级

            Log.e(TAG, "pi.versionCode: " + pi.versionCode);
            Log.e(TAG, "pi.versionName: " + pi.versionName);
        } catch (JSONException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = object.toString();
        MediaType JSON = MediaType.parse("application/json");
        Log.e("获取下载请求", "json: " + json);
        RequestBody requestBody = FormBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")//text/plain  application/json  application/x-www-form-urlencoded
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                Log.e("上传公司网络请求", "IOException: " + e);
                Utils.writeRecord("上传公司网络请求失败" + "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) {

                try {

                    String res = response.body().string();

                    Log.e(TAG, "onResponse: " + res.toString());
                    Gson gson = new Gson();
                    DownloadVersionBean dv = gson.fromJson(res, DownloadVersionBean.class);
                    if (dv.getStatus().equals("200")) {
                        Message msg = new Message();
                        msg.obj = dv;
                        msg.what = 1;
                        mHandler_updataVersion.sendMessage(msg);//更新设备编号
                        pb_show = 0;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getBin() {
        String url = Utils.httpurl_xb_download;//公司服务器上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();

        try {
            String uniqueId =Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            PackageInfo pi = this.getPackageManager().getPackageInfo(Application.getContext().getPackageName(), 0);
            object.put("sbbh", equ_no);//设备编号
            object.put("machine", uniqueId);//设备唯一标识 8d47e396-daed-451d-9b0e-61bc1bb6b134
            object.put("version", MmkvUtils.getcode("yj_version", "默认版本"));//版本号版本 v3.22
//            object.put("version", "v3.22");//版本号版本 测试数据
            object.put("type", "2");//软件=1 硬件=2
            object.put("is_force", 0);//是否强制升级

            Log.e(TAG, "pi.versionCode: " + pi.versionCode);
            Log.e(TAG, "pi.versionName: " + pi.versionName);
        } catch (JSONException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = object.toString();
        MediaType JSON = MediaType.parse("application/json");
        Log.e("获取下载请求", "json: " + json);
        RequestBody requestBody = FormBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")//text/plain  application/json  application/x-www-form-urlencoded
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                Log.e("上传公司网络请求", "IOException: " + e);
                Utils.writeRecord("上传公司网络请求失败" + "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) {

                try {

                    String res = response.body().string();

                    Log.e(TAG, "onResponse: " + res.toString());
                    Gson gson = new Gson();
                    DownloadVersionBean dv = gson.fromJson(res, DownloadVersionBean.class);
                    if (dv.getStatus().equals("200")) {
                        Message msg = new Message();
                        msg.obj = dv;
                        msg.what = 2;
                        mHandler_updataVersion.sendMessage(msg);//更新设备编号
                        pb_show = 0;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //发送命令
    public synchronized void sendCmd(byte[] mBuffer) {
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
                Utils.writeLog("->:" + str);
                Log.e("发送命令", str);
                mOutputStream.write(mBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);
        String fromCommad = Utils.bytesToHexFun(cmdBuf);
//        Log.e("自检收到", "fromCommad: "+fromCommad );
        if (completeValidCmd(fromCommad) == 0) {
            fromCommad = this.revCmd;
            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
                return;
            } else {
                String cmd = DefCommand.getCmd(fromCommad);
                if (cmd != null) {
                    int localSize = fromCommad.length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(fromCommad);
                    doWithReceivData(cmd, localBuf);
                }
            }
        }
    }

    /**
     * 处理接收到的cmd命令
     */
    private void doWithReceivData(String cmd, byte[] locatBuf) {
        if (DefCommand.CMD_4_XBSTATUS_1.equals(cmd)) {//40 获取电源状态指令
            busInfo = FourStatusCmd.decodeFromReceiveDataPower24_1("00", locatBuf);
            busHandler.sendMessage(busHandler.obtainMessage());
        } else if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//41开启总线电源指令
            get41Resp = 1;
            Log.e(TAG, "已收到41指令");
            open();
        }
    }
    /**
     * 打开电源
     */
    private class OpenPower extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;
            while (!exit) {
                try {
//                    long time = System.currentTimeMillis();
//                    long endTime = (long) MmkvUtils.getcode("endTime", (long) 0);
//                    if (zeroCount == 0 && time - endTime < 60000) {
//                        Log.e(TAG,"断电后重新发送41指令");
//                        sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令
//                    }
                    if (get41Resp == 1) {
                        exit = true;
                        break;
                    }
                    if (zeroCount > 0 && zeroCount <= 3 && get41Resp == 0) {
                        Log.e(TAG,"发送41指令");
                        sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令
                        Thread.sleep(3500);
                    } else if (zeroCount > 3){
                        Log.e(TAG,"41指令未返回已发送3次，停止发送41指令");
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

    /**
     * 获取电源信息
     */
    private class SendPower extends Thread {
        public volatile boolean exit = true;
        private long lastProcessedTime = 0;
        public void run() {

            while (!exit) {
//                try {
                //发送获取电源信息
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastProcessedTime > 1000){
                    sendCmd(FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00"));
                    lastProcessedTime = currentTime;
                }
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }
        }
    }

    private void open() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendPower = new SendPower();//40指令线程
        sendPower.exit = false;
        if (!threadStarted) {
            threadPoolExecutor.execute( sendPower);

            Log.e(TAG,"已开启40线程");
            threadStarted = true;
        } else {
            Log.e(TAG,"40线程已开启过，不再开启");
        }
    }

    private void close() {
        isCmdClosed = true;
        isRestarted = false;
        threadStarted = false;
        get41Resp = 0;
        threadPoolExecutor.shutdown();
        Log.e(TAG, "close: 关闭线程池" );
        Utils.writeLog("首页:关闭获取电流电压线程池");
//        if (sendPower != null) {
//            sendPower.exit = true;  // 终止线程thread
//                sendPower.interrupt();
//        }

        if (openPower != null) {
            openPower.exit = true;  // 终止线程thread
//            try {
            openPower.interrupt();
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication.closeSerialPort();
                Log.e(TAG,"调用mApplication.closeSerialPort()开始关闭串口了。。");
                Utils.writeLog("首页:关闭串口");
                mSerialPort = null;
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        sendCmd(OneReisterCmd.setToXbCommon_Reister_Exit12_4("00"));//13
        close();
        SQLiteStudioService.instance().stop();
        if (sendPower != null) {
            sendPower.exit = true;  // 终止线程thread
            sendPower.interrupt();
            Log.e(TAG,"已关闭sendPower");
        }

        if (openPower != null) {
            openPower.exit = true;  // 终止线程thread
            openPower.interrupt();
            Log.e(TAG,"已关闭openPower");
        }
        if (!isCmdClosed) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mApplication.closeSerialPort();
                    Log.e(TAG,"调用mApplication.closeSerialPort()开始关闭串口了。。");
                    mSerialPort = null;
                }
            }).start();
        }
//        if (tipDlg != null) {
//            tipDlg.dismiss();
//            tipDlg = null;
//        }
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        openHandler.removeCallbacksAndMessages(null);
    }

    /***
     * 建立对话框
     */
    private void initDialog_shouquan() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View getlistview = inflater.inflate(R.layout.firing_error_shouquan_listview, null);
        LinearLayout llview = getlistview.findViewById(R.id.ll_dialog_err);
        llview.setVisibility(View.GONE);
        TextView text_tip = getlistview.findViewById(R.id.dialog_tip);
        text_tip.setText(R.string.text_alert_tip_wsqtx);
        text_tip.setVisibility(View.VISIBLE);
        // 给ListView绑定内容
        ListView errlistview = getlistview.findViewById(R.id.X_listview);
        errlistview.setVisibility(View.GONE);
        ErrShouQuanListAdapter mAdapter = new ErrShouQuanListAdapter(this, list_shou, R.layout.firing_error_item_shouquan);
        errlistview.setAdapter(mAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_alert_tip_wsq);//"错误雷管列表"
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
            dialogOFF(dialog);
            dialog.dismiss();


        });
        builder.setNeutralButton(getString(R.string.text_fir_dialog5), (dialog, which) -> {
//            stopXunHuan();
            llview.setVisibility(View.VISIBLE);
            text_tip.setVisibility(View.GONE);
            errlistview.setVisibility(View.VISIBLE);
            dialogOn(dialog);
        });
//        builder.setNegativeButton(getString(R.string.text_fir_dialog5), (dialog, which) -> {
////            stopXunHuan();
//            llview.setVisibility(View.VISIBLE);
//            text_tip.setVisibility(View.GONE);
//            errlistview.setVisibility(View.VISIBLE);
//            dialogOn(dialog);
//        });
        builder.create().show();
    }
}
