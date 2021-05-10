package android_serialport_api.xingbang.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.db.ErrLog;
import android_serialport_api.xingbang.db.SysLog;

/**
 * Created by suwen on 2017/8/29.
 */

public class MyCrashHandler implements Thread.UncaughtExceptionHandler {

    private static MyCrashHandler instance;
    private Context mContext;

    public static MyCrashHandler getInstance() {
        if (instance == null) {
            instance = new MyCrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        mContext=ctx;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Utils.showToast(mContext,"很抱歉,程序出现异常,即将重启.",Toast.LENGTH_LONG);
        String logPath;// logPath: /storage/emulated/0//XB错误日志
        String fileName;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            logPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + File.separator
                    + File.separator
                    + "XB错误日志";
            File file = new File(logPath);
            if (!file.exists()) {
                file.mkdirs();

                ErrLog errLog = new ErrLog();
                errLog.setFilename(Utils.getDate(new Date()) + "错误日志");
                errLog.setPath(logPath);
                errLog.setUpdataState("否");
                errLog.setUpdataTime("");
                Application.getDaoSession().getErrLogDao().insert(errLog);
            }
            try {
                FileWriter fw = new FileWriter(logPath + File.separator + Utils.getDate(new Date()) + "报错信息.txt", true);
                fw.write(new Date() + "错误原因：\n");
                // 错误信息
                // 这里还可以加上当前的系统版本，机型型号 等等信息
                StackTraceElement[] stackTrace = e.getStackTrace();
                fw.write(e.getMessage() + "\n");
                for (int i = 0; i < stackTrace.length; i++) {
                    fw.write("file:" + stackTrace[i].getFileName() + " class:"
                            + stackTrace[i].getClassName() + " method:"
                            + stackTrace[i].getMethodName() + " line:"
                            + stackTrace[i].getLineNumber() + "\n");
                }
                fw.write("\n");
                fw.close();
            } catch (IOException e1) {
                Log.e("错误拦截", "load file failed...", e1.getCause());
            }
        }
        e.printStackTrace();
        ActivityManager.getInstance().exit();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
