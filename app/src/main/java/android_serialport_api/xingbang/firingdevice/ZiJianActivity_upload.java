package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.NetUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.upload.FTP;
import android_serialport_api.xingbang.utils.upload.IntervalUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

public class ZiJianActivity_upload extends BaseActivity {

    @BindView(R.id.tv_zj_num)
    TextView tvZjNum;
    @BindView(R.id.tv_zj_dy)
    TextView tvZjDy;
    @BindView(R.id.tv_zj_gy)
    TextView tvZjGy;
    @BindView(R.id.btn_leixing)
    Button btnLx;
    @BindView(R.id.sp_leixing)
    Spinner spLx;
    SharedPreferences.Editor edit;
    private float dianya_low = 0;//低压信息
    private float dianya_high = 0;//高压信息
    private double lowVoltage;
    private String lowTiaoZheng;
    private String highTiaoZheng;
    private String version;
    private String version_cloud;
    private double highVoltage;
    private From42Power busInfo;
    private Handler busHandler = null;//总线信息
    private int flag = 0;
    private ZiJianThread ziJianThread;
    private volatile int firstCount = 3;

    // FTP参数
    private FTP mFTP;
    private String mIP = "182.92.61.78";
    private String mUserName = "xingbang";
    private String mPassWord = "xingbang666";
    private String mSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xb";
    private List<FTPFile> mList_FtpFileName = new ArrayList<>();

    // E2
    private int mGetFrom;                       // 1:从Assets获得 2:从文件管理器获得 3:从xb获得
    private String mFileName = "micro.bin";     // 1
    private Uri mUri;                           // 2
    private List<byte[]> mList_Byte = new ArrayList<>();
    private int mNumber_E2 = 0;
    private int mIndex_E2 = 0;
    private int mFormat = 1024;
    private String mTip = "";

    public volatile String mDownLoadFilePath;   // 下载文件路径 3
    public volatile long mDownLoadFileSize;     // 下载文件大小
    public String CJ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zi_jian);
        ButterKnife.bind(this);
        initPower();                // 初始化上电方式()
        powerOnDevice(PIN_ADSL);    // 上电
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //获取偏好设置的编辑器
        SharedPreferences sp = getSharedPreferences("config", 0);
        edit = sp.edit();
        //获取低压高压值
        lowVoltage = Utils.convertToDouble(sp.getString("lowVoltage", "8.5"), 8.5);
        highVoltage = Utils.convertToDouble(sp.getString("highVoltage", "16"), 16);
        lowTiaoZheng = sp.getString("lowTiaoZheng", "0");
        highTiaoZheng = sp.getString("highTiaoZheng", "0");
        initHandler();
        initFTP();              // 初始化FTP
        ziJianThread = new ZiJianThread();
//        ziJianThread.start();
        Utils.writeRecord("--进入起爆器--");
        quanxian();//申请权限

        CJ="SC_";//SC-四川 NM-内蒙(不同的版本需要修改)
//        CJ="XB_";//实验用
        if (IntervalUtil.isFastClick_2()) {
            //有三个版本,16V-普通板子 16V-11000版子  17V-11000板子
            //UpgradeActivity里面的对应值也要改
            GetFileName(CJ+"KT50_V1.3_16V", ".bin");//17V是电流11000,16V是改变前的
        }
        deleteRiZhi();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lx_array, R.layout.spinner_item_lx);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLx.setAdapter(adapter);
        spLx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 当选择下拉列表中的一个项时，会调用此方法
                // 可以在这里处理选中事件
                String selectedItem = parent.getItemAtPosition(position).toString();
                MmkvUtils.savecode("leixing",selectedItem);
                Log.e("列表", "selectedItem: "+selectedItem );
                // 使用选中的值
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 无选项被选中时的处理
            }
        });
        btnLx.setOnClickListener(v -> {

            Intent intent = new Intent(ZiJianActivity_upload.this, ReisterMainPage_line.class);
            startActivity(intent);
            finish();//如果不结束当前页面的话,会和后面的页面抢命令
        });


    }

    /**
     * 初始化FTP
     */
    private void initFTP() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mFTP = new FTP(mIP, mUserName, mPassWord);
    }


    private void GetFileName(String name, String type) {

        // 如果是Bin文件
        if (type.equals(".bin")) {
            mGetFrom = 3;    // 从xb获得
        }
        Log.e("是否有网", NetUtils.haveNetWork(this) + "");
        // 网络判断
        if (!NetUtils.haveNetWork(this)) {
            return;
        }

        try {
            String ftpFileName = null;  // 所需文件在 服务器文件名称
            String time_0 = null;       // bin文件日期最新日期

            // 如果登录成功
            if (mFTP.openConnect()) {
                // 获取服务器文件列表
                mList_FtpFileName.clear();
                mList_FtpFileName = mFTP.listFiles("/");

                for (int i = 0; i < mList_FtpFileName.size(); i++) {
                    String fileName = mList_FtpFileName.get(i).getName();
                    long fileSize = mList_FtpFileName.get(i).getSize();

                    if (fileName.contains(name)) {

                        // 如果是Bin文件
                        if (type.equals(".bin")) {

                            // 截取bin文件日期
                            String time_1 = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf(".bin"));

                            // 如果 这是第一个符合条件文件
                            if (time_0 == null) {
                                ftpFileName = fileName;
                                time_0 = time_1;
                                mDownLoadFilePath = mSaveDirPath + "/" + ftpFileName;
                                mDownLoadFileSize = fileSize;
                                version_cloud = ftpFileName;
                                Log.e("Download_Bin_1", "需下载文件名称: " + ftpFileName + " 需下载文件大小: " + mDownLoadFileSize + " 需下载文件路径: " + mDownLoadFilePath);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void quanxian() {
        final String TAG = "权限";
        List<PermissonItem> permissonItems = new ArrayList<>();
        permissonItems.add(new PermissonItem(Manifest.permission.CAMERA, "照相机", R.drawable.permission_ic_memory));
        permissonItems.add(new PermissonItem(Manifest.permission.ACCESS_FINE_LOCATION, "定位", R.drawable.permission_ic_location));
        permissonItems.add(new PermissonItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写", R.drawable.permission_ic_memory));
        permissonItems.add(new PermissonItem(Manifest.permission.READ_EXTERNAL_STORAGE, "读", R.drawable.permission_ic_memory));
        permissonItems.add(new PermissonItem(Manifest.permission.INTERNET, "网络", R.drawable.permission_ic_memory));
        permissonItems.add(new PermissonItem(Manifest.permission.RECEIVE_BOOT_COMPLETED, "网络", R.drawable.permission_ic_memory));
        HiPermission.create(this)
                .permissions(permissonItems)
                .title("用户您好")
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        show_Toast("用户关闭权限申请");
                    }

                    @Override
                    public void onFinish() {
//                        show_Toast("所有权限申请完成");
                        ziJianThread.start();
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                        Log.i(TAG, "onDeny");
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                    }
                });
    }

    //退出方法
    private void exit() {
        powerOffDevice(PIN_ADSL);//主板下电
        //点击在两秒以内
        removeALLActivity();//执行移除所以Activity方法
    }

    private class ZiJianThread extends Thread {
        public volatile boolean exit = false;

        public void run() {
            while (!exit) {
                try {
                    busHandler.sendMessage(busHandler.obtainMessage(1));

                    Thread.sleep(1000);
//                    if (firstCount == 2) {
//                        test();//检测设备是否正常
//                    }
                    if (firstCount == 0) {
                        busHandler.sendMessage(busHandler.obtainMessage(3));
                        exit = true;
//                        createDialog_cj();

                        break;
                    }
                    firstCount--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private void test() {
//        Log.e("高压和低压值", "lowVoltage: " + lowVoltage + "  highVoltage: " + highVoltage);
//        int a = Double.valueOf(lowVoltage * 10).intValue();
//        byte[] delayBye = Utils.shortToByte((short) a);
//        String delayStr = Utils.bytesToHexFun(delayBye);
//        String b = delayStr.substring(0, 2);
//        String c = delayStr.substring(2);
//
//        int a2 = Double.valueOf(highVoltage * 10).intValue();
//        byte[] delayBye2 = Utils.shortToByte((short) a2);
//        String delayStr1 = Utils.bytesToHexFun(delayBye2);
//        String d = delayStr1.substring(0, 2);
//        String e = delayStr1.substring(2);
//        Log.e("低压", "c+b: " + c + b);
//        Log.e("高压", "e+d: " + e + d);
//        Utils.writeRecord("设置低压" + (c + b) + "--设置高压" + (e + d));
//        byte[] powerCmd = OneReisterCmd.setToXbCommon_Reister_Test((c + b) + (e + d));//14
//        sendCmd(powerCmd);
//    }

    @Override
    protected void onStart() {

        super.onStart();
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
        message.setPreparation_time("28");
        message.setChongdian_time("68");
        message.setServer_type1("1");
        message.setServer_type2("0");
        message.setPro_dwdm("");
        message.setJiance_time("50");
        message.setVersion("02");
        getDaoSession().getMessageBeanDao().insert(message);
        Utils.saveFile_Message();//把软存中的数据存入磁盘中
    }
    private void getUserMessage() {
        List<MessageBean> message = getDaoSession().getMessageBeanDao().loadAll();
//        Log.e(TAG, "message: " + message.toString());
        if (message.size() > 0) {
        } else {
            setUserMessage();//如果为空就新建一个
        }
    }
    private void initHandler() {
        getUserMessage();


        busHandler = new Handler(message -> {
            switch (message.what) {
                case 1:
                    tvZjNum.setText(firstCount + "s");
                    break;
                    case 3:
                    tvZjNum.setText("请选择将要抽检的雷管类型");
                    break;
                case 2:
                    //旧version: KT50_V1.3_16V_V1.3.15D
                    //新version: SC_KT50_V1.3_16V_V1.3.15D
                    //version_cloud: SC_KT50_V1.3_16V_V1.3.16B.bin
                    Log.e("自检", "version: " + version);
                    Log.e("自检", "version_cloud: " + version_cloud);

                    if (version_cloud != null&&version_cloud.substring(0,16).equals(version.substring(0,16)) &&!version_cloud .contains(version)) {
                        Log.e("自检", "对比version_cloud.substring(0,16).equals(version.substring(0,16)): " + version_cloud.substring(0,16).equals(version.substring(0,16)));
                        ziJianThread.exit = true;
                        createDialog(version_cloud);
                    }
                    break;
            }

            return false;
        });
    }


    /***
     * 建立对话框
     * @param version_cloud
     */
    public void createDialog(String version_cloud) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("升级提醒");//"说明"
        builder.setMessage("检测到有新的硬件程序版本,请确定您当前的网络环境稳定,建议在WIFI环境或者稳定的4G网络热点下再进行更新,是否进行更新?");
        builder.setPositiveButton("进行更新", (dialog, which) -> {
//            show_Toast("当前系统程序有新版本,正在升级,请稍等!");
            finish();
            Intent intent = new Intent(this, UpgradeActivity.class);
            intent.putExtra("dataSend", version_cloud);
            startActivity(intent);
            dialog.dismiss();
        });
//        builder.setNeutralButton("退出", (dialog, which) -> {
//            dialog.dismiss();
//            finish();
//        });
        builder.setNegativeButton("不更新", (dialog, which) -> {
            finish();
            Intent intent = new Intent(this, XingbangMain.class);
            startActivity(intent);
            dialog.dismiss();
        });
        builder.create().show();
    }

    public void createDialog_cj() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择抽检雷管类型");//"说明"
        builder.setMessage("请在进入抽检页面前先选择雷管类型");
        builder.setPositiveButton("进行更新", (dialog, which) -> {
            Intent intent = new Intent(this, UpgradeActivity.class);
            intent.putExtra("dataSend", version_cloud);
            startActivity(intent);
            dialog.dismiss();
        });
        builder.setNeutralButton("退出", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.setNegativeButton("不更新", (dialog, which) -> {
            finish();
            Intent intent = new Intent(this, XingbangMain.class);
            startActivity(intent);
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void deleteRiZhi(){
        String filePath;
        String filePath2;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "/程序运行日志/" ;
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/程序运行日志/" ;
        }
        if (hasSDCard) {
            filePath2 = Environment.getExternalStorageDirectory().toString() + File.separator + "/XB程序日志/" ;
        } else {
            filePath2 = Environment.getDownloadCacheDirectory().toString() + File.separator + "/XB程序日志/" ;
        }

        File dir = new File(filePath);
        Utils.deleteFile(dir);
        File dir2 = new File(filePath2);
        Utils.deleteFile(dir2);
//                Utils.deleteDirWihtFile("/程序运行日志/");
//                Utils.deleteDirWihtFile("/XB程序日志/");
//        show_Toast("删除成功");
    }
}
