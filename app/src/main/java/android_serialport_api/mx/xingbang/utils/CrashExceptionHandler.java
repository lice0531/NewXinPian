package android_serialport_api.mx.xingbang.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

/**
 * 系统处理异常类，处理整个APP的异常
 */
public class CrashExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;

    // 本类实例
    private static CrashExceptionHandler myCrashHandler;

    // 系统默认的UncaughtExceptionHandler
    private Thread.UncaughtExceptionHandler mDefaultException;

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
        if (ex == null) {
            return false;
        }

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
        saveCrash(ex);

        return true;
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
        String path = Environment.getExternalStorageDirectory().toString() + File.separator + "/MX程序运行日志/";//+
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

    private void saveCrash(Throwable e) {
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
        String fileName;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            logPath = Environment.getExternalStorageDirectory().toString() + File.separator + "/MX程序运行日志/" + Utils.getDate(new Date()) + ".txt";
            try {
                FileWriter fw = new FileWriter(logPath, true);
                fw.write(Utils.getDate(new Date()) + "错误原因：\n");
                // 错误信息
                // 这里还可以加上当前的系统版本，机型型号 等等信息
                StackTraceElement[] stackTrace = e.getStackTrace();
                fw.write("崩溃信息"+e.getMessage() + "\n");
//                for (int i = 0; i < stackTrace.length; i++) {
//                    fw.write("方法:" + stackTrace[i].getFileName() +
//                            " -" + stackTrace[i].getClassName() +
//                            " -" + stackTrace[i].getMethodName() +
//                            " -" + stackTrace[i].getLineNumber() + "\n");
//                }
                fw.write(writer.toString());
                fw.write("\n");
                fw.close();
            } catch (IOException e1) {
                Log.e("错误拦截", "load file failed...", e1.getCause());
            }
        }
    }
}


