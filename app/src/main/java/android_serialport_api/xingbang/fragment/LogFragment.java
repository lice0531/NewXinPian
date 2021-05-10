package android_serialport_api.xingbang.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.orhanobut.dialogplus.DialogPlus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.greenDao.SysLogDao;
import android_serialport_api.xingbang.custom.LogAdapter;
import android_serialport_api.xingbang.custom.MlistView;
import android_serialport_api.xingbang.db.SysLog;
import android_serialport_api.xingbang.utils.FTP;
import android_serialport_api.xingbang.utils.LoadingUtils;
import android_serialport_api.xingbang.utils.NetUtils;
import android_serialport_api.xingbang.utils.Result;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android_serialport_api.xingbang.Application.getDaoSession;
import static android_serialport_api.xingbang.utils.Utils.deleteDirectory;

/**
 *
 */
public class LogFragment extends Fragment implements LogAdapter.InnerItemOnclickListener{

    @BindView(R.id.fg_syslog)
    MlistView fgSyslog;
    Unbinder unbinder;

    // 通用
    public DialogPlus mDialogPlus;


    // FTP参数
    private FTP mFtp;
    private String mIP = "182.92.61.78";
    private String mUserName = "xingbang";
    private String mPassWord = "xingbang666";
    private String mSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xb";
    private List<SysLog> list;
    private static Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFTP();              // 初始化FTP
        initHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        unbinder = ButterKnife.bind(this, view);

         list = getDaoSession().getSysLogDao().loadAll();
        LogAdapter adapter = new LogAdapter(Application.getContext(),list,R.layout.item_list_syslog);
        adapter.setOnInnerItemOnClickListener(this);
        fgSyslog.setAdapter(adapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 初始化FTP
     */
    private void initFTP() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mFtp = new FTP(mIP, mUserName, mPassWord);
    }

    private void upload(String path){
        if (!NetUtils.haveNetWork(getContext())) {
            mTip = "访问服务器失败\n(可能是没有连接网络)";
            mDialogPlus.dismiss();
            return;
        }
        // 如果登录成功
        try {
            if (mFtp.openConnect()) {
                mHandler.sendMessage(mHandler.obtainMessage(1400, path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String FtpPath="/F5310000/";
//        Log.e("ftp上传", "path: "+path );
//        File log = new File(path);
//        try {
//            Result result =mFtp.uploading(log,FtpPath,path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void initHandler() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    // 上传开始
                    case 1400:
                        // 进度条
                        showDialog();
                        final String fileName = (String) msg.obj;
                        final String ftpPath = "F5310000";
                        final File file = new File(fileName);
                        Log.e("上传", "正在下载文件名称: " + fileName);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Result result =mFtp.uploading(file, ftpPath, mSaveDirPath);
                                    mHandler.sendMessage(mHandler.obtainMessage(1401, result));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        break;

                    // 下载成功
                    case 1401:
                        Result result = (Result) msg.obj;
                        if (result.isSucceed()) {
                            Utils.showToast(getContext(),"上传成功",Toast.LENGTH_LONG);
                        } else {
                            Utils.showToast(getContext(),"上传失败",Toast.LENGTH_LONG);
                        }
                        Log.e("上传结果", "响应结果: " + result.isSucceed() + " 响应内容: " + result.getResponse());
                        mDialogPlus.dismiss();
                        break;

                }
                return false;
            }
        });
    }


    @Override
    public void itemClick(final View v) {
        switch (v.getId()) {
            case R.id.lf_bt_upload://上传按钮
                int pos = (Integer) v.getTag(R.id.bt_upload);//位置
                String path =list.get(pos).getPath();
                upload(path);
                break;
            case R.id.lf_bt_delete://删除按钮
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("删除提示")//设置对话框的标题//"成功起爆"
                        .setMessage("请确认是否删除当前记录")//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String t = (String) v.getTag(R.id.lf_bt_delete);
                                Log.e("删除", "t: "+t);
                                if (delLogHis(t) == 0) {
                                    Toast.makeText(getContext(),"删除成功",Toast.LENGTH_LONG).show();
                                }
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                break;
        }

    }

    private String mTip = "";
    private int delLogHis(String string){
        List<SysLog> list =getDaoSession().getSysLogDao().queryBuilder().where(SysLogDao.Properties.Filename.eq(string)).list();
        delete(list.get(0).getPath());
        getDaoSession().getSysLogDao().deleteByKey(list.get(0).getId());
        return 0;
    }
    /** 删除文件，可以是文件或文件夹
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            Log.e("--Method--","删除文件失败:" + delFile + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile);
        }
    }
    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                Log.e("--Method--","删除单个文件" + filePath$Name + "失败！");
                return false;
            }
        } else {
            Log.e("--Method--","删除单个文件失败：" + filePath$Name + "不存在！");
            return false;
        }
    }





    // 进度条
    public void showDialog() {
        mDialogPlus = LoadingUtils.loadDialog(getContext());
        mDialogPlus.show();
    }

}
