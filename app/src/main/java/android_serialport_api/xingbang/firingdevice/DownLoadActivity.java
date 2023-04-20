package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.mContext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import org.angmarch.views.NiceSpinner;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.utils.NetUtils;
import android_serialport_api.xingbang.utils.upload.FTP;
import android_serialport_api.xingbang.utils.upload.InitConst;
import android_serialport_api.xingbang.utils.upload.IntervalUtil;
import android_serialport_api.xingbang.utils.upload.Result;
import android_serialport_api.xingbang.utils.upload.XbUtils;
import android_serialport_api.xingbang.utils.uploadUtils.ToastUtils;

/**
 * 下载起爆器程序 Activity
 */
public class DownLoadActivity extends BaseActivity {

    private static Handler mHandler = new Handler();

    // FTP参数
    private FTP mFtp;
    private String mIP = "182.92.61.78";
    private String mUserName = "xingbang";
    private String mPassWord = "xingbang666";
    private String mSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xb";
    // 起爆器版本列表
    private List<String> mList_version = new ArrayList<>();

    // Spinner
    private NiceSpinner mNS_Version;
    // 数据列表
    private String[] mArr_Version;
    private int mIndex = InitConst.VERSION_INDEX;

    private TextView mTvMessage;

    private Button
            mBtnCommonly_1,
            mBtnCommonly_2,
            mBtnCommonly_3,
            mBtnCommonly_4,
            mBtnVersion;

    private String mTip = "";
    private List<FTPFile> mList_FtpFileName = new ArrayList<>();     // 获取服务器文件列表

    private String mPath_Local;                 // 本地已下载安装路径
    private ProgressThread mProgressThread;     // 下载百分比 线程
    public volatile String mDownLoadFilePath;   // 下载文件路径
    public volatile long mDownLoadFileSize;     // 下载文件大小
    public volatile Result mResult;             // 下载完成返回结果

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mContext = this;

        // xb文件夹不存在则创建
        XbUtils.isFileExistence(mSaveDirPath);

        mArr_Permissions = new String[]{
                Manifest.permission.READ_PHONE_STATE
        };

        initFTP();              // 初始化FTP
        initData();
        initView();
        initHandler();          // 初始化Handler

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String shengji = (String) bundle.get("dataSend");
        Log.e("升级", "传递-dataSend: " + shengji);
//        if (shengji.equals("SC_KT50_Second_Version_16")) {//下载项目 16V板子 16V程序
//            Download_APK("SC_KT50_Second_Version_16");
//        } else if (shengji.equals("SC_KT50_Second_Version_15")) {//下载项目 16V板子 17V程序
//            Download_APK("SC_KT50_Second_Version_15");
//        } else if (shengji.equals("SC_KT50_Second_Version_17")) {//下载项目  17V板子 17V程序
//            Download_APK("SC_KT50_Second_Version_17");
//        }
        mNS_Version.setSelectedIndex(6);//默认二代PT10
        if(shengji.length()>1){
            Download_APK(shengji);
        }
    }

    /**
     * 初始化FTP
     */
    private void initFTP() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mFtp = new FTP(mIP, mUserName, mPassWord);
    }

    /**
     * 初始化版本列表
     */
    private void initData() {
        mArr_Version = getResources().getStringArray(R.array.arr_versions);
        // 列表值(6月2日)
        mList_version.add("KT50_OldCode_Version");  // KT50_OldCode_Version_23.apk 1
        mList_version.add("KT50_NewCode_Version");  // KT50_NewCode_Version_22.apk 2
        mList_version.add("KT50_Permit_Version");   // KT50_Permit_Version_20.apk 3
        mList_version.add("ST327_OldCode_Version"); // ST327_OldCode_Version_20.apk 4
        mList_version.add("KT50_NeiMeng_Version");   // KT50_NeiMeng_Version_20.apk 5
        mList_version.add("KT50_ZheJiang_Version");   // KT50_ZheJiang_Version_20.apk 6
        mList_version.add("KT50_Second_Version");   // KT50_Permit_Version_20.apk 7

//        mList_version.add("KT50_Test_Version");     // KT50_Test_Version.apk 9
//        mList_version.add("ST327_Test_Version");    // ST327_Test_Version.apk 10
    }

    /**
     * 初始化控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        mTvMessage = findViewById(R.id.tv_download_message);
        mNS_Version = findViewById(R.id.nice_spinner_version);
        mNS_Version.attachDataSource(new LinkedList<>(Arrays.asList(mArr_Version)));
        mNS_Version.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            ToastUtils.longs("已选择: " + item);
            mIndex = position;
            Log.e("liyi", "选择了服务器上文件名为 " + mList_version.get(mIndex) + " 的文件");
        });

        for (int i = 0; i < mArr_Version.length; i++) {
            // 设置默认版本
            if (mArr_Version[i].equals(mIndex)) {
                mNS_Version.setSelectedIndex(i);
            }
        }

        mBtnVersion = findViewById(R.id.btn_download_0);
        mBtnVersion.setOnClickListener(v -> {
            if (IntervalUtil.isFastClick_2()) {//下载项目
                Download_APK(mList_version.get(mIndex));
            }
        });

        mBtnCommonly_1 = findViewById(R.id.btn_download_1);
        mBtnCommonly_1.setOnLongClickListener(v -> {
            if (IntervalUtil.isFastClick_2()) {
//                Download_APK_1("qqlite_4.0.1.1060_537064365.apk");
            }
            return true;
        });


        mBtnCommonly_2 = findViewById(R.id.btn_download_2);
        mBtnCommonly_2.setOnLongClickListener(v -> {
            if (IntervalUtil.isFastClick_2()) {
//                Download_APK_1("screenshot.apk");
            }
            return true;
        });

        mBtnCommonly_3 = findViewById(R.id.btn_download_3);
        mBtnCommonly_3.setOnClickListener(v -> {
            requestPermissions(mArr_Permissions, 9999);
        });

        mBtnCommonly_4 = findViewById(R.id.btn_download_4);
        mBtnCommonly_4.setOnClickListener(v -> {
//            if (IntervalUtil.isFastClick_3()) {
//            }
        });


    }


    /**
     * 下载所需文件
     */
    private void Download_APK(String name) {
        Log.e("Download_APK", "需要下载文件: " + name);

        // 删除list.csv 防错误
        String file_path = mSaveDirPath + "/list.csv";
        // 如果文件存在
        if (XbUtils.isExists(file_path)) {
            // 删除list.csv
            XbUtils.delete(file_path);
        }

        // 重置提示
        mTip = "";
        mTvMessage.setBackgroundResource(R.color.white);

        if (!NetUtils.haveNetWork(mContext)) {
            mTip = "访问服务器失败\n(可能是没有连接网络)";
            mTvMessage.setText(mTip);
            mTvMessage.setBackgroundResource(R.color.colorRed);
            if (mDialogPlus != null) {
                mDialogPlus.dismiss();
            }
            return;
        }

        try {
            // 服务器 APK名称 APK版本
            String ftpFileName = null;

            mPath_Local = "";

            // 如果登录成功
            if (mFtp.openConnect()) {
                Log.e("Download_APK", "FTP服务器登录成功");

                // 获取服务器文件列表
                mList_FtpFileName.clear();
                mList_FtpFileName = mFtp.listFiles("/");
                for (int i = 0; i < mList_FtpFileName.size(); i++) {
                    String fileName = mList_FtpFileName.get(i).getName();
                    long fileSize = mList_FtpFileName.get(i).getSize();
//                    Log.e("file_ftp", "服务器 文件序号: " + i + " 文件名称: " + fileName + " 文件大小: " + fileSize);

                    if (fileName.contains(name)) {
                        ftpFileName = fileName;
                        mDownLoadFilePath = mSaveDirPath + "/" + ftpFileName;
                        mDownLoadFileSize = fileSize;
                        Log.e("file_ftp", "需要下载文件名称: " + ftpFileName + " \n需要下载文件大小: " + mDownLoadFileSize + " \n需要下载文件路径: " + mDownLoadFilePath);
                    }

                }

                // 如果服务器没有所需文件
                if (ftpFileName == null) {
                    mTip = "服务器上没有\n升级程序安装包(请联系管理员上传安装包)";
                    mTvMessage.setText(mTip);
                    mTvMessage.setBackgroundResource(R.color.colorRed);
                    return;
                }

                // 获取本地APK文件列表
                List<String> list_localFileName = XbUtils.getFileNameList(mSaveDirPath, ".apk");

                // 本地没有APK文件列表
                if (list_localFileName == null) {
                    // 本地已下载安装路径
                    mPath_Local = "";
                }
                // 本地有APK文件列表
                else {
                    // 本地已下载安装路径 服务器版本 大于 本地版本 为空
                    mPath_Local = XbUtils.isHaveApk(ftpFileName, list_localFileName);

                    for (int i = 0; i < list_localFileName.size(); i++) {
                        Log.e("file_local", "本地 文件序号: " + i + " 文件路径: " + list_localFileName.get(i));
                    }
                }
                Log.e("file_local", "本地已下载安装路径: " + mPath_Local);


                // 本地没有文件
                if (mPath_Local.isEmpty()) {
                    // FTP服务器 开始下载 所需文件
                    mHandler.sendMessage(mHandler.obtainMessage(1400, ftpFileName));
                    mTip = "FTP服务器登录成功" + "\n正在下载程序...\n" + ftpFileName + " 文件\n";
                    mTvMessage.setText(mTip);
                    Log.e("Download_APK", "index == -1 " + "开始下载");

                }
                // 本地有文件
                else {
                    // 打开apk
                    XbUtils.openAPK(this, mPath_Local);
                }


            } else {
                Log.e("Download_APK", "FTP服务器登录失败");
                mTip = "FTP服务器登录失败";
                mTvMessage.setText(mTip);
                mTvMessage.setBackgroundResource(R.color.colorRed);
                mDialogPlus.dismiss();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }





//    /**
//     * 下载文件(不对比版本)
//     */
//    private void Download_APK_1(String name) {
//        Log.e("Download_APK_1", "需要下载文件: " + name);
//
//        // 重置提示
//        mTip = "";
//        mTvMessage.setBackgroundResource(R.color.colorWhite);
//
//        if (!NetUtils.haveNetWork(mContext)) {
//            mTip = "访问服务器失败\n(可能是没有连接网络)";
//            mTvMessage.setText(mTip);
//            mTvMessage.setBackgroundResource(R.color.colorRed);
//            mDialogPlus.dismiss();
//            return;
//        }
//
//        try {
//            // 服务器 APK名称
//            String ftpFileName = null;
//
//            // 如果登录成功
//            if (mFtp.openConnect()) {
//                Log.e("Download_APK_1", "FTP服务器登录成功");
//
//                // 获取服务器文件列表
//                mList_FtpFileName.clear();
//                mList_FtpFileName = mFtp.listFiles("/");
//                for (int i = 0; i < mList_FtpFileName.size(); i++) {
//                    String fileName = mList_FtpFileName.get(i).getName();
//                    Log.e("file_1", "服务器 文件序号: " + i + " 文件名称: " + fileName + " 文件大小: " + mList_FtpFileName.get(i).getSize());
//                    if (fileName.contains(name)) {
//                        ftpFileName = fileName;
//                        Log.e("file_1", "需要下载文件名称: " + fileName);
//                    }
//                }
//
//                if (ftpFileName == null) {
//                    mTip = "服务器上没有\n升级程序安装包(请联系管理员上传安装包)";
//                    mTvMessage.setText(mTip);
//                    mTvMessage.setBackgroundResource(R.color.colorRed);
//                    return;
//                }
//
//                // 获取本地apk文件路径
//                List<String> list_localFileName = XbUtils.getFileNameList(mSaveDirPath, ".apk");
//
//                for (int i = 0; i < list_localFileName.size(); i++) {
//                    String fileName = list_localFileName.get(i);
//                    Log.e("file_1", "本地 文件序号: " + i + " 文件路径: " + fileName);
//
//                    // 如果本地有文件
//                    if (fileName.contains(name)) {
//                        mPath_Local = fileName;
//                        Log.e("file_1", "如果本地有文件: " + mPath_Local);
//                    }
//
//                }
//
//                // 如果本地有安装包
//                if (mPath_Local != null) {
//                    // 打开apk
//                    XbUtils.openAPK(this, mPath_Local);
//                }
//                // 如果本地没有安装包
//                else {
//                    // FTP服务器 开始下载 所需文件
//                    mHandler.sendMessage(mHandler.obtainMessage(1400, ftpFileName));
//
//                    mTip = "FTP服务器登录成功" + "\n正在下载程序...\n" + ftpFileName + " 文件\n文件较大请耐心等待...\n\n注意！如果文件解析包安装错误\n请删除 xb文件夹下的.apk文件";
//                    mTvMessage.setText(mTip);
//
//                    Log.e("Download_APK_1", "开始下载");
//                }
//
//            } else {
//                Log.e("Download_APK_1", "FTP服务器登录失败");
//                mTip = "FTP服务器登录失败";
//                mTvMessage.setText(mTip);
//                mTvMessage.setBackgroundResource(R.color.colorRed);
//                mDialogPlus.dismiss();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    /**
     * 初始化Handler
     */
    private void initHandler() {

        mHandler = new Handler(msg -> {

            switch (msg.what) {

                // 下载开始
                case 1400:
                    // 进度条
                    showDialog();
                    String fileName = (String) msg.obj;
                    Log.e("liyi", "线程开启 开始下载文件: " + fileName);

                    // 开始下载
                    new Thread(() -> {
                        try {
                            mResult = mFtp.download("/", fileName, mSaveDirPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                    // 进度条线程
                    mProgressThread = new ProgressThread(mDownLoadFileSize, mDownLoadFilePath);
                    mProgressThread.start();
                    break;

                // 下载进度条
                case 1410:
                    long percentage = XbUtils.getPercent((long) msg.obj, mDownLoadFileSize);
                    Log.e("liyi", "下载百分比: " + percentage + "%");

                    mTip = mTip + percentage + "%... ";
                    mTvMessage.setText(mTip);
                    break;

                // 下载成功
                case 1420:

                    if (mResult != null) {

                        if (mResult.isSucceed()) {
                            mTip = mTip + "升级程序下载成功\n";
                            mTvMessage.setBackgroundResource(R.color.colorGreen);
                            // 打开APK
                            XbUtils.openAPK(this, mResult.getPath());    // 升级程序下载成功
                        } else {
                            mTip = mTip + "升级程序下载失败\n";
                            mTvMessage.setBackgroundResource(R.color.colorRed);
                        }

                        mTvMessage.setText(mTip);
                        Log.e("liyi", "响应结果: " + mResult.isSucceed() + " 响应内容: " + mResult.getResponse());
                        mDialogPlus.dismiss();

                    }
                    break;

            }

            return false;
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, int[] grantResults) {

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission Granted 授予权限
//            if (IntervalUtil.isFastClick_2()) {
//                startActivity(new Intent(mContext, MessageActivity.class));
//            }
        } else {
            Log.e("liyi", "Permission Denied 权限被拒绝");
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // 未知安装权限页面
            if (requestCode == 2500) {
                XbUtils.openAPKFile_8(this, mPath_Local);
            }

        } else {

            if (requestCode == 2500) {
                // 8.0手机位置来源安装权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!getPackageManager().canRequestPackageInstalls()) {
                        Log.e("liyi", "没有赋予 未知来源安装权限");
                        ToastUtils.longs("没有赋予 未知来源安装权限");
                    }
                }
            } else if (requestCode == 3500) {
                Log.e("liyi", "再次点击下载，进行安装");
                ToastUtils.longs("再次点击下载，进行安装");
            }
        }

    }

    /**
     * 进度条线程
     */
    private static class ProgressThread extends Thread {

        private long max_size;
        private long curr_size;
        private String path;

        public int mIndex;
        public boolean mExit = false;

        public ProgressThread(long mDownLoadFileSize, String mDownLoadFilePath) {
            this.max_size = mDownLoadFileSize;
            this.path = mDownLoadFilePath;
        }

        @Override
        public void run() {
            super.run();

            try {
                while (!isInterrupted() && !mExit) {
                    Thread.sleep(2500);

                    if (mExit) {
                        return;
                    }

                    // 文件大小
                    curr_size = new File(path).length();
                    Log.e("liyi_Progress", mIndex + " 正在下载 " + curr_size + " KB");

                    // 更新进度条
                    mHandler.sendMessage(mHandler.obtainMessage(1410, curr_size));

                    // 下载完成
                    if (max_size == curr_size) {
                        Log.e("liyi_Progress", "下载完成");
                        mHandler.sendMessage(mHandler.obtainMessage(1420));
                        mExit = true;
                    }

                    mIndex++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 只是页面显示中断 实际还在下载
        if (mProgressThread != null) {
            mProgressThread.mExit = true;
        }
    }
}
