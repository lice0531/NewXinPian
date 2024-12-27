package android_serialport_api.xingbang.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.db.ErrLog;
import android_serialport_api.xingbang.db.SysLog;
import android_serialport_api.xingbang.db.greenDao.ErrLogDao;
import android_serialport_api.xingbang.db.greenDao.SysLogDao;

/**
 * 系统处理异常类，处理整个APP的异常
 */
public class CrashExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;

    // 本类实例
    private static CrashExceptionHandler myCrashHandler;

    // 系统默认的UncaughtExceptionHandler
    private Thread.UncaughtExceptionHandler mDefaultException;
    private String TAG = "异常捕获页面";

    // 保证只有一个实例
    public CrashExceptionHandler() {
    }

    // 单例模式
    public synchronized static CrashExceptionHandler newInstance() {
        if (myCrashHandler == null) {
            myCrashHandler = new CrashExceptionHandler();
        }
        return myCrashHandler;
    }


    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.mContext = context;
        // 系统默认处理类
        this.mDefaultException = Thread.getDefaultUncaughtExceptionHandler();
        // 将该类设置为系统默认处理类
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 异常处理与提示
     */
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.e("报错信息", "处理报错: " );
        if (!handleExample(e) && mDefaultException != null) { //判断异常是否已经被处理
            mDefaultException.uncaughtException(t, e);
        } else {
            // 睡眠3s主要是为了下面的Toast能够显示出来，否则，Toast是没有机会显示的
            try {
                Thread.sleep(500);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }
    /**
     * 提示用户出现异常，将异常信息保存/上传
     *
     * @param ex
     * @return
     */
    private boolean handleExample(Throwable ex) {
        Log.e(TAG,"EX为空:" + ex);
        if (ex == null) {
            return false;
        }
        saveCrash(ex);
        new Thread(() -> {
            Looper.prepare();

            // 不能使用这个ToastUtils.show()，不能即时的提示，会因为异常出现问题
//            ToastUtils.show("很抱歉，程序出现异常，即将退出！");
//            MyToast.toast(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG);
            Toast.makeText(mContext, "很抱歉，程序出现异常，即将退出", Toast.LENGTH_LONG).show();
//            Utils.showToast(mContext,"很抱歉，程序出现异常，即将退出",3000);
            Looper.loop();
        }).start();

//        saveCrashInfoToFile(ex);
//        saveCrash(ex);

        return true;
    }

    private void saveCrash(Throwable e) {
        String date = Utils.getDate(new Date());
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable exCause = e.getCause();
        while (exCause != null) {
            exCause.printStackTrace(printWriter);
            exCause = exCause.getCause();
        }
        printWriter.close();
        String logPath;// logPath: /storage/emulated/0//XB错误日志
        String oldPath;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        //filePath: /storage/emulated/0//XB程序日志/21-03-08程序日志.txt
        if (hasSDCard) {
            logPath = Environment.getExternalStorageDirectory().toString() + File.separator + "/程序运行崩溃日志/" + date + ".txt";
            oldPath = Environment.getExternalStorageDirectory().toString() + File.separator + "/程序运行崩溃日志/" + date + "_" + date + ".txt";
        } else {
            logPath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/程序运行崩溃日志/" + date + ".txt";
            oldPath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/程序运行崩溃日志/" + date + "_" + date + ".txt";
        }
        File file = new File(logPath);
        try {
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            } else {
                long fileS = getFileSize(file);
                if (fileS > 1073741824) {//大于1M，
                    if (file.renameTo(new File(oldPath))) {
                        Log.e(TAG, "日志文件已重命名为: " + oldPath);
                    }
                }
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logPath, true))) {
                bw.write(Utils.getDateFormatLong(new Date()) + "错误原因：\n");
                // 错误信息
                // 这里还可以加上当前的系统版本，机型型号 等等信息
                StackTraceElement[] stackTrace = e.getStackTrace();
                bw.write("崩溃信息" + e.getMessage() + "\n");
//                for (int i = 0; i < stackTrace.length; i++) {
//                    fw.write("方法:" + stackTrace[i].getFileName() +
//                            " -" + stackTrace[i].getClassName() +
//                            " -" + stackTrace[i].getMethodName() +
//                            " -" + stackTrace[i].getLineNumber() + "\n");
//                }
                bw.write(writer.toString());
                bw.write("\n");
                bw.close();
                // 写入数据库
                String fName = Utils.getDate(new Date()) + ".txt";
                SimpleDateFormat updateTimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestamp = updateTimeSdf.format(new Date());
                saveCrashLogToDatabase(fName, logPath, timestamp);
            } catch (IOException ec) {
                Log.e(TAG, "崩溃日志写入失败: " + ec.getMessage());
            }
        } catch (Exception ex) {
            Log.e(TAG, "崩溃日志文件生成失败:" + ex.getMessage().toString());
            e.printStackTrace();
        }
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        }
        return size;
    }

    /**
     * 将崩溃日志保存到 GreenDAO 数据库
     */
    private void saveCrashLogToDatabase(String filename, String path,String timestamp) {
        ErrLogDao errLogDao = Application.getDaoSession().getErrLogDao();
        List<ErrLog> existLogs = errLogDao.queryBuilder()
                .where(ErrLogDao.Properties.Filename.eq(filename)).list();
        if (!existLogs.isEmpty()) {
            // 如果存在记录，遍历所有记录并进行判断
            boolean isUpdated = false;
            for (ErrLog log : existLogs) {
                if ("未上传".equals(log.getUpdataState())) {
                    // 如果记录的 UpdataState 是 "未上传"，则更新 UpdataTime
                    log.setUpdataTime(timestamp);
                    errLogDao.update(log); // 更新数据库
                    Log.e(TAG, "崩溃日志已更新到数据库");
                    isUpdated = true;
                    break;  // 找到一个未上传的记录后就停止
                }
            }
            // 如果没有更新记录，说明是 "已上传"，需要插入新记录
            if (!isUpdated) {
                ErrLog newErrorLog = new ErrLog();
                newErrorLog.setFilename(filename);
                newErrorLog.setPath(path);
                newErrorLog.setUpdataState("未上传");
                newErrorLog.setUpdataTime(timestamp);  // 设置更新时间
                errLogDao.insert(newErrorLog); // 插入新记录
                Log.e(TAG, "崩溃日志已保存到数据库");
            }
        } else {
            ErrLog errLog = new ErrLog();
            errLog.setFilename(filename);
            errLog.setPath(path);
            errLog.setUpdataState("未上传");
            errLog.setUpdataTime(timestamp);  // 设置更新时间
            errLogDao.insert(errLog); // 插入新记录
            Log.e(TAG, "崩溃日志已保存到数据库");
        }
    }

    /**
     * 保存异常信息到本地
     *
     * @param ex
     */
    private void saveCrashInfoToFile(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable exCause = ex.getCause();
        while (exCause != null) {
            exCause.printStackTrace(printWriter);
            exCause = exCause.getCause();
        }
        printWriter.close();

        long timeMillis = System.currentTimeMillis();
        //错误日志文件名称
//        String fileName = "crash-" + timeMillis + ".txt";
        String fileName = Utils.getDate(new Date()) + ".txt";
        //文件存储位置
//        String path = mContext.getFilesDir() + "/crash_logInfo/";
        String path = Environment.getExternalStorageDirectory().toString() + File.separator + "/程序运行日志/";//+
        File fl = new File(path);
        //创建文件夹
        if (!fl.exists()) {
            fl.mkdirs();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path + fileName);
            fileOutputStream.write(writer.toString().getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


