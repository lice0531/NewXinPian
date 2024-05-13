package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.mContext;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.angmarch.views.NiceSpinner;
import org.apache.commons.net.ftp.FTPFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.DownloadVersionBean;
import android_serialport_api.xingbang.utils.DownloadTest;
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
    private DownloadManager manager = null;
    private String app_name;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mContext = this;
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        String mOldTitle = getSupportActionBar().getTitle().toString();
        getSupportActionBar().setTitle(mOldTitle );
        // xb文件夹不存在则创建
        XbUtils.isFileExistence(mSaveDirPath);

        mArr_Permissions = new String[]{
                Manifest.permission.READ_PHONE_STATE
        };

//        initFTP();              // 初始化FTP
//        initData();
        initView();
        initHandler();          // 初始化Handler



//        download = findViewById(R.id.down);
//        progressTxt = findViewById(R.id.progress);
        pb_update = findViewById(R.id.pb_update);
//        mCancleBtn = findViewById(R.id.cancle_down);


        downloadManager = (DownloadManager)this.getSystemService(Context.DOWNLOAD_SERVICE);
        query = new DownloadManager.Query();
        mDownloadTest = new DownloadTest(this);

        //定时器
        mTimer = new Timer();

        //注册广播，监听下载状态
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentfilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(receiver, intentfilter);

        //获取传过来的下载地址
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String a = (String)bundle.get("dataSend");

        Gson gson = new Gson();
        DownloadVersionBean dv = gson.fromJson(a, DownloadVersionBean.class);
        String shengji = dv.getNewVersionPath();
        app_name=dv.getNewVersion();
        Log.e("下载地址", "app_name: "+app_name );
        Log.e("下载地址", "shengji: "+shengji );

        if(shengji.length()>0){
//            Download_APK(shengji);//ftp下载
            //如果之前存在就删除之前的,重新下载
            if(XbUtils.isExists(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/"+app_name)){
                XbUtils.delete(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/"+app_name) ;
            }
            // 删除list.csv 防错误
            String file_path = mSaveDirPath + "/list.csv";
            // 如果文件存在
            if (XbUtils.isExists(file_path)) {
                // 删除list.csv
                XbUtils.delete(file_path);
            }
            mDownloadId =  mDownloadTest.downloadAPK(shengji, downloadManager,app_name);
            mTimer.schedule(mTimerTask, 0,1000);
            mBtnVersion.setEnabled(false);
        }
    }


    /**
     * 初始化控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        mTvMessage = findViewById(R.id.tv_download_message);
//        mNS_Version = findViewById(R.id.nice_spinner_version);
//        mNS_Version.attachDataSource(new LinkedList<>(Arrays.asList(mArr_Version)));
//        mNS_Version.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {
//            String item = parent.getItemAtPosition(position).toString();
//            ToastUtils.longs("已选择: " + item);
//            mIndex = position;
//            Log.e("liyi", "选择了服务器上文件名为 " + mList_version.get(mIndex) + " 的文件");
//        });

//        for (int i = 0; i < mArr_Version.length; i++) {
//            // 设置默认版本
//            if (mArr_Version[i].equals(mIndex)) {
//                mNS_Version.setSelectedIndex(i);
//            }
//        }


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


        mBtnVersion = findViewById(R.id.btn_download_0);
        mBtnVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //            if (IntervalUtil.isFastClick_2()) {//下载项目
//                Download_APK(mList_version.get(mIndex));
//            }
            }
        });

//        mCancleBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                downloadManager.remove(mDownloadId);
//                mBtnVersion.setText("立即下载");
//            }
//        });
    }





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
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(receiver);

    }


    private DownloadManager downloadManager;

    private DownloadManager.Query query;

    private DownloadTest mDownloadTest;


    private Timer mTimer;

    //下载任务的id
    private long mDownloadId;
    private TextView download;
    private TextView progressTxt;
    private ProgressBar pb_update;
    private Button mCancleBtn;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Toast.makeText(DownLoadActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                try {
                    XbUtils.openAPK(DownLoadActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/"+app_name);    // 升级程序下载成功
                    //跳转到显示下载内容的activity界面
//                    Intent dm = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
//                    dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(dm);
                } catch (ActivityNotFoundException ex){
                    Log.e("下载",  "no activity for " + ex.getMessage());
                }
            } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                Toast.makeText(DownLoadActivity.this, "用户点击了通知栏", Toast.LENGTH_SHORT).show();
            }
        }
    };


    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int pro = bundle.getInt("progressMsg");
            pb_update.setProgress(pro);
            mTvMessage.setText("下载进度：" + pro + "%");
        }
    };

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            Cursor cursor = downloadManager.query(query.setFilterById(mDownloadId));

            if (cursor != null && cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                Log.e("down", "status:" + status);
                switch (status) {
                    //下载暂停
                    case DownloadManager.STATUS_PAUSED:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBtnVersion.setText("下载暂停");
                            }
                        });
                        break;
                    //下载延迟
                    case DownloadManager.STATUS_PENDING:
                        Log.e("down", "====下载延迟=====");
                        break;
                    //正在下载
                    case DownloadManager.STATUS_RUNNING:
                        //Log.e("down", "====正在下载中=====");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBtnVersion.setText("正在下载中......");
                            }
                        });
                        break;
                    //下载完成
                    case DownloadManager.STATUS_SUCCESSFUL:
                        //下载完成安装APK
                        //Log.e("down", "====下载完成=====");
                        mTimerTask.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb_update.setProgress(100);
                                mBtnVersion.setText("下载完成");
                            }
                        });
                        break;
                    //下载失败
                    case DownloadManager.STATUS_FAILED:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DownLoadActivity.this, "下载失败", Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }


                long bytesDownload = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                String descrition = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                String id = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String mimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                long totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                Log.e("down", "bytesDownload:" + bytesDownload);
                Log.e("down", "totalSize:" + totalSize);

                int progress = (int)(bytesDownload*100/totalSize) ;
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt("progressMsg", progress);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
            cursor.close();
        }
    };




    @Override
    protected void onResume() {
        super.onResume();


    }







}
