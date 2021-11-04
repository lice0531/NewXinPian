package android_serialport_api.xingbang.firingdevice;

import android.DeviceControl;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.PropertiesUtil;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

import static android_serialport_api.xingbang.Application.getDaoSession;


public class XingbangMain extends BaseActivity {

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
    FrameLayout container;
    @BindView(R.id.tv_main_version)
    TextView tvMainVersion;
    @BindView(R.id.btn_main_lianxi)
    Button btnMainLianxi;
    private long time = 0;
    private DeviceControl deviceControl;
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
    private int Preparation_time;//准备时间
    private int ChongDian_time;//准备时间
    private int jiance_time;//检测时间
    private String qiaosi_set = "";//是否检测桥丝
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler_loading = new Handler();//显示进度条
    private Handler mHandler_updata = new Handler();//更新主页面信息
    private List<DenatorBaseinfo> list_data = new ArrayList<>();
    private ArrayList<String> lg2_yanshi = new ArrayList<>();
    private String TAG = "主页";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xingbang_main);
        ButterKnife.bind(this);
        SQLiteStudioService.instance().start(this);
        try {
            deviceControl = new DeviceControl(DeviceControl.PowerType.MAIN, 94, 93);
//            deviceControl.PowerOnDevice();//主板上电
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 22);
//        db = mMyDatabaseHelper.getReadableDatabase();

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
                readCVS_pro();//把备份的信息写入到数据库中
                pb_show = 0;
                getUserMessage();//获取用户信息
            }
        }.start();
        loadMoreData_all_lg();//查询雷管延时是否为0
        mHandler_updata.sendMessage(mHandler_updata.obtainMessage());//更新设备编号
//        getMaxNumberNo();
        Utils.writeRecord("---进入主页面---");
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
        mHandler_updata = new Handler(this.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.e("起爆器编号", "equ_no: " + equ_no);
                if (!equ_no.equals("")) {
                    tvMainNo.setText("设备编号:" + equ_no);
                    CrashReport.setUserId(equ_no);
                }
            }
        };
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
        pro_bprysfz = (String) MmkvUtils.decode("pro_bprysfz", "");
        pro_htid = (String) MmkvUtils.decode("pro_htid", "");
        pro_xmbh = (String) MmkvUtils.decode("pro_xmbh", "");
        equ_no = (String) MmkvUtils.decode("equ_no", "");
        pro_coordxy = (String) MmkvUtils.decode("pro_coordxy", "");
        server_addr = (String) MmkvUtils.decode("server_addr", "");
        server_port = (String) MmkvUtils.decode("server_port", "6088");
        server_http = (String) MmkvUtils.decode("server_http", "http://qq.mbdzlg.com/mbdzlgtxzx/servlet/DzlgSysbJsonServlert");
        server_ip = (String) MmkvUtils.decode("server_ip", "119.29.111.172");
        qiaosi_set = (String) MmkvUtils.decode("qiaosi_set", "false");
        Preparation_time = (int) MmkvUtils.decode("preparation_time", 50);
        ChongDian_time = (int) MmkvUtils.decode("chongdian_time", 28);
        server_type1 = (String) MmkvUtils.decode("server_type1", "1");
        server_type2 = (String) MmkvUtils.decode("server_type2", "0");
        pro_dwdm = (String) MmkvUtils.decode("pro_dwdm", "");
        jiance_time = (int) MmkvUtils.decode("jiance_time", 50);
        Yanzheng = (String) MmkvUtils.decode("Yanzheng","验证");
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
        message.setPreparation_time("20");
        message.setChongdian_time("28");
        message.setServer_type1("1");
        message.setServer_type2("0");
        message.setPro_dwdm("");
        message.setJiance_time("5");
        getDaoSession().getMessageBeanDao().insert(message);
        Utils.saveFile();//把软存中的数据存入磁盘中
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

        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                    String str1 = "设置";
                    Intent intent = new Intent(XingbangMain.this, SetEnvMainActivity.class);
                    intent.putExtra("dataSend", str1);
                    startActivityForResult(intent, 1);
                    dialog.dismiss();
                } else if (!a.equals("xingbang")) {
                    show_Toast("用户名错误");
                    dialogOn(dialog);
                } else if (!b.equals("123456")) {
                    show_Toast("密码错误");
                    dialogOn(dialog);
                } else {
                    show_Toast(getString(R.string.text_error_tip50));
                    dialogOn(dialog);
//                    dialog.dismiss();
                }

                //  builder.
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
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

    /**
     *
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
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
            try {
                if (deviceControl != null) deviceControl.PowerOffDevice();//主板下电
            } catch (IOException e) {
                e.printStackTrace();
            }//下电
            //点击在两秒以内
            removeALLActivity();//执行移除所以Activity方法
        }
    }

    int m6KeyDown_Action, m6KeyUp_Action;
    int m0KeyDown_Action, m0KeyUp_Action;
    int keyFlag = 0;

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {//
//        return super.onKeyUp(keyCode, event);
//    }


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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loadMoreData_all_lg();
        if (!equ_no.equals("")) {
            tvMainNo.setText("设备编号:" + equ_no);
            CrashReport.setUserId(equ_no);
        }
        getPropertiesData();
        super.onRestart();
    }


    @Override
    protected void onResume() {
//        getPropertiesData();//重新读取备份数据会导致已修改的数据重置
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        SQLiteStudioService.instance().stop();
//        if (db != null) db.close();
//        if (tipDlg != null) {
//            tipDlg.dismiss();
//            tipDlg = null;
//        }
        super.onDestroy();
    }

    @OnClick({R.id.btn_main_reister, R.id.btn_main_test, R.id.btn_main_delayTime, R.id.btn_main_del, R.id.btn_main_blast, R.id.btn_main_query, R.id.btn_main_setevn, R.id.btn_main_help, R.id.btn_main_downWorkCode, R.id.btn_main_exit})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.btn_main_reister://注册
                String str1 = "注册";
                Intent intent = new Intent(XingbangMain.this, ReisterMainPage_scan.class);//金建华
                intent.putExtra("dataSend", str1);
                startActivityForResult(intent, 1);
                break;

            case R.id.btn_main_test://测试
                Log.e("测试页面", "测试: ");
                String str2 = "测试";
                Intent intent2 = new Intent(XingbangMain.this, TestDenatorActivity.class);//金建华
                intent2.putExtra("dataSend", str2);
                startActivityForResult(intent2, 1);
                break;

                // 单发检测
            case R.id.btn_main_delayTime:
                Intent intent3 = new Intent(this, ReisterMainPage_line.class);//金建华
                startActivityForResult(intent3, 1);
                break;

                // 删除
            case R.id.btn_main_del:
                startActivityForResult(new Intent(this, DelDenatorMainPage.class), 1);
                break;

            case R.id.btn_main_blast://起爆
                for (int i = 0; i < lg2_yanshi.size(); i++) {
                    if (lg2_yanshi.get(i).equals("0")) {
                        createDialog();
                        return;
                    }
                }
                String str5 = "起爆";
                Log.e("验证2", "Yanzheng: " + Yanzheng);
                Intent intent5;//金建华
                if (Yanzheng.equals("验证")) {
                    //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
                    intent5 = new Intent(this, VerificationActivity.class);
                } else {
                    intent5 = new Intent(this, FiringMainActivity.class);
                }
                intent5.putExtra("dataSend", str5);
                startActivityForResult(intent5, 1);
                break;

            case R.id.btn_main_query://查看
                String str6 = "查看雷管";
                Intent intent6 = new Intent(XingbangMain.this, QueryMainActivity.class);
                intent6.putExtra("dataSend", str6);
                startActivityForResult(intent6, 1);
                break;

            case R.id.btn_main_setevn://设置
                loginToSetEnv();
                break;

            case R.id.btn_main_help://辅助功能
//                createHelpDialog();
                String str8 = "查看雷管";
                Intent intent8 = new Intent(this, PracticeActivity.class);
                intent8.putExtra("dataSend", str8);
                startActivityForResult(intent8, 1);
                break;

            case R.id.btn_main_downWorkCode://下载
                String str7 = "下载";
                Intent intent7 = new Intent(XingbangMain.this, DownWorkCode.class);
                intent7.putExtra("dataSend", str7);
                startActivityForResult(intent7, 1);
//            Intent intent7 = new Intent(XingbangMain.this, SetDelayTime.class);
//            startActivity(intent7);
                break;

            case R.id.btn_main_exit://退出
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
                String a[] = line.split(",", -1);
//          Log.e("写入文件数据",
//          "序号：" + a[0] + ",孔号：" + a[1] + ",管壳码：" + a[2] + ",延期：" + a[3] + ",状态：" + a[4]
//          + ",错误：" + a[5] + ",授权期限：" + a[6] + ",序列号：" + a[7] + ",备注：" + a[8]);
                //向数据库插入数据

                DenatorBaseinfo baseinfo = new DenatorBaseinfo();
                baseinfo.setBlastserial(Integer.parseInt(a[1]));
                baseinfo.setSithole(Integer.parseInt(a[2]));
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
                getDaoSession().getDenatorBaseinfoDao().insert(baseinfo);
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
        message.setVersion("");//单片机系统版本/旧01/新02
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
        GreenDaoMaster master = new GreenDaoMaster();
        list_data = master.queryDenatorBaseinfoToStatusCode("02");
//        list_data = getDaoSession().getDenatorBaseinfoDao().loadAll();
        for (int i = 0; i < list_data.size(); i++) {
            lg2_yanshi.add(list_data.get(i).getDelay() + "");
        }
//        Log.e("起爆延时验证", lg2_yanshi.toString());
    }

//    private void loadMoreData_all_lg() {
//        pb_show = 0;
//        lg2_yanshi.clear();
//        list_data.clear();
//        StringBuffer sb = new StringBuffer();
//        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, "statusCode=?", new String[]{"02"}, null, null, " blastserial asc");
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
//                int holeNo = cursor.getInt(2);
//                String shellNo = cursor.getString(3);//管壳号
//                int delay = cursor.getInt(5);
//                String stCode = cursor.getString(6);//状态
//                String stName = cursor.getString(7);//
//                String errorCode = cursor.getString(9);//状态
//                String errorName = cursor.getString(8);//
//
//                sb.append(shellNo).append("#").append(delay).append(",");
//
//                VoBlastModel item = new VoBlastModel();
//                item.setBlastserial(serialNo);
//                item.setSithole(holeNo);
//                item.setDelay((short) delay);
//                item.setShellBlastNo(shellNo);
//                item.setErrorCode(errorCode);
//                item.setErrorName(errorName);
//                item.setStatusCode(stCode);
//                item.setStatusName(stName);
//                list_data.add(item);
//            }
//            cursor.close();
//        }
//        for (int i = 0; i < list_data.size(); i++) {
//            lg2_yanshi.add(list_data.get(i).getDelay() + "");
//        }
//        Utils.writeLeiGuan(sb.toString());//保存一份txt备份
//        Log.e("雷管txt备份", "备份成功");
//        Log.e("起爆延时验证", lg2_yanshi.toString());
//    }

    /***
     * 建立对话框
     */
    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒");//"说明"
        builder.setMessage("有未设置延时的雷管,是否继续起爆?");
        builder.setPositiveButton("继续起爆", (dialog, which) -> {
            String str5 = "起爆";
            if (Yanzheng.equals("验证")) {
                //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
                Intent intent5 = new Intent(XingbangMain.this, VerificationActivity.class);//验证爆破范围页面
                intent5.putExtra("dataSend", str5);
                startActivityForResult(intent5, 1);
            } else {
                Intent intent5 = new Intent(XingbangMain.this, FiringMainActivity.class);//金建华
                intent5.putExtra("dataSend", str5);
                startActivityForResult(intent5, 1);
            }
            dialog.dismiss();
        });
        builder.setNegativeButton("返回查看", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private long queryTotal() {
        return getDaoSession().getDenatorBaseinfoDao().count();
    }

    private long queryMessage() {
        return getDaoSession().getMessageBeanDao().count();
    }

    /***
     * 手动输入注册
     */
    private int insertDenator(String leiguan) {
        String[] lg = leiguan.split(",");
        String shellNo;
        int maxNo = getMaxNumberNo();
        int reCount = 0;
        for (int i = lg.length; i > 0; i--) {
            shellNo = lg[i - 1];
            Log.e("雷管信息", "shellNo: " + shellNo);
            String[] a = shellNo.split("#");
            //检查重复数据
            if (checkRepeatShellNo(a[0]) == 1) {
                reCount++;
                continue;
            }
            maxNo++;
            DenatorBaseinfo baseinfo = new DenatorBaseinfo();
            baseinfo.setBlastserial(maxNo);
            baseinfo.setSithole(maxNo);
            baseinfo.setShellBlastNo(a[0]);
            baseinfo.setDelay(Integer.parseInt(a[1]));
            baseinfo.setRegdate(Utils.getDateFormatLong(new Date()));
            baseinfo.setStatusCode("02");
            baseinfo.setStatusName("已注册");
            baseinfo.setErrorCode("");
            baseinfo.setErrorCode("FF");
            baseinfo.setWire("");
            getDaoSession().getDenatorBaseinfoDao().insert(baseinfo);
            reCount++;
        }
        return reCount;
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
}
