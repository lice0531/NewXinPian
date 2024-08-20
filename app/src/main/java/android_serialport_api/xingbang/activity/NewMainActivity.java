package android_serialport_api.xingbang.activity;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.kfree.expd.ExpdDevMgr;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.databinding.ActivityNewMainBinding;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.firingdevice.SetEnvMainActivity;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class NewMainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityNewMainBinding binding;

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
    private String version = "";//版本号
    private int Preparation_time;//准备时间
    private int ChongDian_time;//准备时间
    private int jiance_time;//检测时间
    private String qiaosi_set = "";//是否检测桥丝
    private String TAG = "主页";
    private int pb_show = 0;
    public ExpdDevMgr mExpDevMgr ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_main);
        // 新办法
        binding = ActivityNewMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SQLiteStudioService.instance().start(this);
        initView();
        mExpDevMgr= new ExpdDevMgr(this);
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
            }
        }.start();

    }

    private void initView() {
        TextView title = findViewById(R.id.title_text);
        title.setText("通用型起爆器App首页");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setVisibility(View.GONE);
        iv_back.setVisibility(View.GONE);
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
                if (a.length == 16) {
                    baseinfo.setDenatorIdSup(a[15]);
                } else if (a.length > 16) {
                    baseinfo.setZhu_yscs(a[16]);
                    baseinfo.setCong_yscs(a[17]);
                }
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
        Log.e("读取数据", "version: " + version);
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
        Log.e("读取数据", "version: " + version);
        if (queryMessage() == 1) {
            message.setId((long) 1);
            getDaoSession().getMessageBeanDao().update(message);
        } else {
            getDaoSession().getMessageBeanDao().insert(message);
        }
        Log.e("读取数据", "readCVS_pro: ");
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
        Preparation_time = (int) MmkvUtils.getcode("preparation_time", 50);
        ChongDian_time = (int) MmkvUtils.getcode("chongdian_time", 28);
        server_type1 = (String) MmkvUtils.getcode("server_type1", "1");
        server_type2 = (String) MmkvUtils.getcode("server_type2", "0");
        pro_dwdm = (String) MmkvUtils.getcode("pro_dwdm", "");
        jiance_time = (int) MmkvUtils.getcode("jiance_time", 50);
        Yanzheng = (String) MmkvUtils.getcode("Yanzheng", "验证");
        version = (String) MmkvUtils.getcode("version", "02");
        Log.e(TAG, "Yanzheng: " + Yanzheng);
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
        message.setVersion("02");
        getDaoSession().getMessageBeanDao().insert(message);
        Utils.saveFile_Message();//把软存中的数据存入磁盘中
    }

    private long queryTotal() {
        return getDaoSession().getDenatorBaseinfoDao().count();
    }

    private long queryMessage() {
        return getDaoSession().getMessageBeanDao().count();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Log.e(TAG, "onClick: ");
        switch (view.getId()) {
            case R.id.cardView1://项目管理
                Intent intent = new Intent(NewMainActivity.this, ProjectManagementActivity.class);
                startActivity(intent);
                break;
            case R.id.cardView2://GPS定位
//                startActivity(new Intent(NewMainActivity.this,GetGPSActivity.class));
                startActivity(new Intent(NewMainActivity.this, GpsDemoActivity.class));
                break;
            case R.id.cardView3://单发雷管检测
                startActivity(new Intent(NewMainActivity.this, ZhuCeActivity_line.class));
                break;
            case R.id.cardView4://下载工作码
                startActivity(new Intent(NewMainActivity.this, DownProjectActivity.class));
                break;
            case R.id.cardView5://雷管注册
                startActivity(new Intent(NewMainActivity.this, ZhuCeActivity_scan.class));
                break;
            case R.id.cardView6://充电/起爆
                if (mExpDevMgr.isSafeSwitchOpen()) {
                    createDialog_kaiguan();
                    return;
                }
                String str5 = "起爆";
                Intent intent5;//金建华
//                if (Yanzheng.equals("验证")) {
//                    //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
////                    intent5 = new Intent(this, VerificationActivity.class);
//                    intent5 = new Intent(this, FiringMainActivity.class);
//                } else {
//                    intent5 = new Intent(this, FiringMainActivity.class);
//                }
                intent5 = new Intent(this, QiBaoActivity.class);
                intent5.putExtra("dataSend", str5);
                startActivity(intent5);
                break;
            case R.id.cardView7://上传数据
                startActivity(new Intent(NewMainActivity.this, UploadDataActivity.class));
                break;
            case R.id.cardView8://设置
                startActivity(new Intent(NewMainActivity.this, SetEnvMainActivity.class));
//                startActivity(new Intent(NewMainActivity.this, ReisterMainPage_scan.class));
                break;
        }
    }

    /***
     * 建立对话框
     */
    public void createDialog_kaiguan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("安全提醒");//"说明"
        builder.setMessage("检测到您的安全开关处于开启状态,请先关闭掌机右侧的安全开关,再进行检测!");
        builder.setNegativeButton("返回", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}