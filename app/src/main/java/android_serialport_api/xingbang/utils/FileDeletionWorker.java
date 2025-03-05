package android_serialport_api.xingbang.utils;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * APP进入程序后自动删除三个月内的日志txt
 */
public class FileDeletionWorker extends Worker {

    private String TAG = "定期删除txt日志页面";
    public FileDeletionWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        Log.e(TAG,"进入到删除日志的方法里了");
        // 获取日志文件夹路径
        File logDirectory1 = new File(Environment.getExternalStorageDirectory(), "APP程序运行日志");
        // 检查该路径是否存在，并打印日志
        if (!logDirectory1.exists() || !logDirectory1.isDirectory()) {
            Log.e(TAG, logDirectory1.getAbsolutePath() + "是否存在: " + logDirectory1.exists() +
                    "--是否是目录: " + logDirectory1.isDirectory());
            // 如果目录不存在，则返回失败
            return Result.failure(); // 目录不存在，任务失败
        }
        // 获取日志文件夹中的所有文件
        File[] files1 = logDirectory1.listFiles();
        if (files1 != null) {
            // 获取当前日期，并减去三个月
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -3);
            long threeMonthsAgo = calendar.getTimeInMillis();
            // 定义日期格式，用于解析文件名中的日期部分
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            for (File file : files1) {
                if (file.getName().endsWith(".txt")) {
                    try {
                        // 提取文件名中的日期部分
                        String fileName = file.getName();
                        String dateString = fileName.substring(0, 19); // 获取类似 "2025-03-05 09:35:09" 的部分
                        // 解析文件名中的日期
                        Date fileDate = dateFormat.parse(dateString);
                        if (fileDate != null && fileDate.getTime() < threeMonthsAgo) {
                            // 如果文件的日期小于三个月前，删除该文件
                            boolean deleted = file.delete();
                            if (!deleted) {
                                // 如果删除失败，打印日志
                                Log.e(TAG, file.getName() + "日志删除失败:" + file.getAbsolutePath());
                            } else {
                                Log.e(TAG,file.getName() + "已删除");
                                AppLogUtils.writeAppLog(file.getName() + "已删除");
                            }
                        } else {
                            Log.e(TAG,"APP程序运行日志没有可以删除的日志txt");
                        }
                    } catch (Exception e) {
                        // 解析失败时打印异常
                        e.printStackTrace();
                        Log.e(TAG,file.getName() + "删除报错:" + e.getMessage().toString());
                    }
                }
            }
        }
        // 获取日志文件夹路径
        File logDirectory2 = new File(Environment.getExternalStorageDirectory(), "APP-XB程序运行日志");
        // 检查该路径是否存在，并打印日志
        Log.e(TAG, logDirectory2.getAbsolutePath() + " 是否存在: " + logDirectory2.exists() +
                " -- 是否是目录: " + logDirectory2.isDirectory());
        if (!logDirectory2.exists() || !logDirectory2.isDirectory()) {
            Log.e(TAG, logDirectory2.getAbsolutePath() + "是否存在: " + logDirectory2.exists() +
                    "--是否是目录: " + logDirectory2.isDirectory());
            // 如果目录不存在，则返回失败
            return Result.failure(); // 目录不存在，任务失败
        }
        // 获取日志文件夹中的所有文件
        File[] files2 = logDirectory2.listFiles();
        if (files2 != null) {
            // 获取当前日期，并减去三个月
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -3);
            long threeMonthsAgo = calendar.getTimeInMillis();
            // 定义日期格式，用于解析文件名中的日期部分
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            for (File file : files2) {
                if (file.getName().endsWith(".txt")) {
                    try {
                        // 提取文件名中的日期部分
                        String fileName = file.getName();
                        String dateString = fileName.substring(0, 19); // 获取类似 "2025-03-05 09:35:09" 的部分
                        // 解析文件名中的日期
                        Date fileDate = dateFormat.parse(dateString);
                        if (fileDate != null && fileDate.getTime() < threeMonthsAgo) {
                            // 如果文件的日期小于三个月前，删除该文件
                            boolean deleted = file.delete();
                            if (!deleted) {
                                // 如果删除失败，打印日志
                                Log.e(TAG, file.getName() + "日志删除失败:" + file.getAbsolutePath());
                            } else {
                                Log.e(TAG,file.getName() + "已删除");
                                AppLogUtils.writeAppLog(file.getName() + "已删除");
                            }
                        } else {
                            Log.e(TAG,"APP-XB程序运行日志没有可以删除的日志txt");
                        }
                    } catch (Exception e) {
                        // 解析失败时打印异常
                        e.printStackTrace();
                        Log.e(TAG,file.getName() + "删除报错:" + e.getMessage().toString());
                    }
                }
            }
        }
        return Result.success();
    }
}
