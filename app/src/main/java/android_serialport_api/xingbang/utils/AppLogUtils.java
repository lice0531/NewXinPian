package android_serialport_api.xingbang.utils;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.db.SysLog;
import android_serialport_api.xingbang.db.greenDao.SysLogDao;
/**
 * 存储程序详细日志
 */
public class AppLogUtils {
    private static final long MAX_FILE_SIZE = 1048576; // 1MB
    private static final String LOG_DIRECTORY; // 日志文件目录
    private static final String LOG_XBDIRECTORY; // 日志文件目录
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    private static SimpleDateFormat updateTimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String currentTimestamp; // 当前时间戳，用于文件命名
    private static File currentLogFile; // 当前日志文件
    private static File currentXBLogFile; // 当前XB日志文件
    private static String TAG = "程序日志页面工具类";

    static {
        // 根据是否有SD卡来设置日志路径
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            // 如果有SD卡，使用外部存储路径
            LOG_DIRECTORY = Environment.getExternalStorageDirectory().toString() + File.separator + "/APP程序运行日志/";
        } else {
            // 如果没有SD卡，使用内部存储路径
            LOG_DIRECTORY = Environment.getDownloadCacheDirectory().toString() + File.separator + "/APP程序运行日志/";
        }
        // 创建日志目录（如果不存在）
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            boolean dirCreated = logDir.mkdirs();  // 创建目录
            if (dirCreated) {
                Log.e(TAG, "APP运行日志文件夹创建成功！");
            } else {
                Log.e(TAG, "APP运行日志文件夹创建失败！");
            }
        }
    }

    static {
        // 根据是否有SD卡来设置日志路径
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            // 如果有SD卡，使用外部存储路径
            LOG_XBDIRECTORY = Environment.getExternalStorageDirectory().toString() + File.separator + "/APP-XB程序运行日志/";
        } else {
            // 如果没有SD卡，使用内部存储路径
            LOG_XBDIRECTORY = Environment.getDownloadCacheDirectory().toString() + File.separator + "/APP-XB程序运行日志/";
        }
        // 创建日志目录（如果不存在）
        File logDir = new File(LOG_XBDIRECTORY);
        if (!logDir.exists()) {
            boolean dirCreated = logDir.mkdirs();  // 创建目录
            if (dirCreated) {
                Log.e(TAG, "程序XB日志文件夹创建成功！");
            } else {
                Log.e(TAG, "程序XB日志文件夹创建失败！");
            }
        }
    }

    public static void writeAppLog(String logContent) {
        try {
            // 初始化时获取当前时间戳和文件对象
            if (currentTimestamp == null || currentLogFile == null || currentLogFile.length() > MAX_FILE_SIZE) {
                initNewLogFile();
            }
            // 获取当前时间戳，用于记录日志创建时间
            String timestamp = updateTimeSdf.format(new Date());
            // 在日志内容前面加上时间戳
            String logWithTimestamp = Utils.getDateFormat_log(new Date()) + " - " + logContent;
            // 将日志内容写入当前文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentLogFile, true))) {
                writer.write(logWithTimestamp);  // 写入带时间戳的日志
                writer.newLine();
            }
            // 将日志信息存储到数据库
            insertOrUpdateSysLog(currentLogFile.getName(), currentLogFile.getAbsolutePath(), timestamp);
        } catch (IOException e) {
            Log.e(TAG, "日志写入失败: " + e.getMessage());
        }
    }

    // 初始化新的日志文件或重新创建文件

    private static void initNewLogFile() {
        // 获取日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        // 获取当前日期
        String currentDate = sdf.format(new Date());
        // 获取当前目录中的所有文件，筛选出以当前日期为前缀的文件
        File logDirectory = new File(LOG_DIRECTORY);
        File[] files = logDirectory.listFiles((dir, name) -> name.startsWith(currentDate) &&
                name.endsWith(".txt"));
//        Log.e(TAG,"APPLog当前系统日期:" + currentDate);
        if (files != null && files.length > 0) {
            // 如果已有当天日期开头的文件，选择第一个文件作为当前日志文件
            currentLogFile = files[0];
            if (currentLogFile.length() > MAX_FILE_SIZE) {
                //如果当前文件已大于1MB  就重新生成文件
                renameLogFile();
            }
        } else {
            // 如果没有文件，生成新的文件
            currentTimestamp = timeSdf.format(new Date());
            currentLogFile = new File(LOG_DIRECTORY + currentTimestamp + ".txt");
            try {
                if (currentLogFile.createNewFile()) {
                    Log.e(TAG, "APP程序运行日志文件已创建: " + currentLogFile.getName());
                }
            } catch (IOException e) {
                Log.e(TAG, "无法创建APP程序运行日志文件: " + e.getMessage());
            }
        }
    }

    // 重命名日志文件，并递增时间戳
    private static void renameLogFile() {
        // 重命名当前日志文件
        File renamedFile = new File(LOG_DIRECTORY + currentTimestamp + ".txt");
        boolean renamed = currentLogFile.renameTo(renamedFile);
        if (renamed) {
            Log.e(TAG,"日志文件已重命名为: " + renamedFile.getName());

            // 递增时间戳，确保文件名唯一
            increaseTimestamp();
            currentLogFile = new File(LOG_DIRECTORY + currentTimestamp + ".txt");
            // 创建一个新的日志文件
            try {
                if (currentLogFile.createNewFile()) {
                    Log.e(TAG,"新日志文件已创建: " + currentLogFile.getName());
                }
            } catch (IOException e) {
                Log.e(TAG,"无法创建新日志文件: " + e.getMessage());
            }
        } else {
            Log.e(TAG,"重命名文件失败!");
        }
    }
    public static void writeAppXBLog(String logContent) {
        try {
            // 初始化时获取当前时间戳和文件对象
            if (currentTimestamp == null || currentXBLogFile == null || currentXBLogFile.length() > MAX_FILE_SIZE) {
                initXBNewLogFile();
            }
            // 获取当前时间戳，用于记录日志创建时间
//            String timestamp = updateTimeSdf.format(new Date());
            // 在日志内容前面加上时间戳
            String logWithTimestamp = Utils.getDateFormat_log(new Date()) + " - " + logContent;

            // 将日志内容写入当前文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentXBLogFile, true))) {
                writer.write(logWithTimestamp);  // 写入带时间戳的日志
                writer.newLine();
            }
        } catch (IOException e) {
            Log.e(TAG, "APP-XB日志写入失败: " + e.getMessage());
        }
    }

    // 初始化新的日志文件或重新创建文件
    private static void initXBNewLogFile() {
        // 获取日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        // 获取当前日期
        String currentDate = sdf.format(new Date());
        // 获取当前目录中的所有文件，筛选出以当前日期为前缀的文件
        File logDirectory = new File(LOG_XBDIRECTORY);
        File[] files = logDirectory.listFiles((dir, name) -> name.startsWith(currentDate));
//        Log.e(TAG,"XBLog当前系统日期:" + currentDate);
        if (files != null && files.length > 0) {
            // 如果已有当天日期开头的文件，选择第一个文件作为当前日志文件
            currentXBLogFile = files[0];
            if (currentXBLogFile.length() > MAX_FILE_SIZE) {
                //如果当前文件已大于1MB  就重新生成文件
                renameXBLogFile();
            }
        } else {
            // 如果没有文件，生成新的文件
            currentTimestamp = timeSdf.format(new Date());
            currentXBLogFile = new File(LOG_XBDIRECTORY + currentTimestamp + ".txt");
            try {
                if (currentXBLogFile.createNewFile()) {
                    Log.e(TAG, "新APP-XB程序运行日志文件已创建: " + currentXBLogFile.getName());
                }
            } catch (IOException e) {
                Log.e(TAG, "无法创建APP-XB程序运行日志文件: " + e.getMessage());
            }
        }
    }

    // 重命名日志文件，并递增时间戳
    private static void renameXBLogFile() {
        // 重命名当前日志文件
        File renamedFile = new File(LOG_XBDIRECTORY + currentTimestamp + ".txt");
        boolean renamed = currentXBLogFile.renameTo(renamedFile);
        if (renamed) {
            Log.e(TAG,"日志文件已重命名为: " + renamedFile.getName());
            // 递增时间戳，确保文件名唯一
            increaseTimestamp();
            currentXBLogFile = new File(LOG_XBDIRECTORY + currentTimestamp + ".txt");
            // 创建一个新的日志文件
            try {
                if (currentXBLogFile.createNewFile()) {
                    Log.e(TAG,"新APP-XB程序日志文件已创建: " + currentXBLogFile.getName());
                }
            } catch (IOException e) {
                Log.e(TAG,"无法创建新APP-XB程序日志文件: " + e.getMessage());
            }
        } else {
            Log.e(TAG,"重命名APP-XB程序文件失败!");
        }
    }

    // 递增时间戳，按分钟递增
    private static void increaseTimestamp() {
        try {
            Date date = sdf.parse(currentTimestamp); // 将当前时间戳转为Date对象
            long timeInMillis = date.getTime();
            timeInMillis += 60000; // 增加60秒，即按分钟递增
            currentTimestamp = sdf.format(new Date(timeInMillis)); // 更新当前时间戳
        } catch (Exception e) {
            Log.e(TAG,"递增时间戳时出错: " + e.getMessage());
        }
    }

    private static void insertOrUpdateSysLog(String filename, String path, String timestamp) {
        // 提取日期部分（假设日期格式为 "dd-MM-yy"）
        String date = timestamp.substring(0, 8);  // "2024-12-23" 这个部分
        // 获取数据库会话
        SysLogDao sysLogDao = Application.getDaoSession().getSysLogDao();
        // 查询相同日期的所有记录
        List<SysLog> existingLogs = sysLogDao.queryBuilder()
                .where(SysLogDao.Properties.Filename.eq(filename),
                        SysLogDao.Properties.UpdataTime.like(date + "%")).list();
        if (!existingLogs.isEmpty()) {
            // 如果存在记录，遍历所有记录并进行判断
            boolean isUpdated = false;
            for (SysLog existingLog : existingLogs) {
                if ("未上传".equals(existingLog.getUpdataState())) {
                    // 如果记录的 UpdataState 是 "未上传"，则更新 UpdataTime
                    existingLog.setUpdataTime(timestamp);
                    sysLogDao.update(existingLog); // 更新数据库
                    isUpdated = true;
                    break;  // 找到一个未上传的记录后就停止
                }
            }
            // 如果没有更新记录，说明是 "已上传"，需要插入新记录
            if (!isUpdated) {
                SysLog newSysLog = new SysLog();
                newSysLog.setFilename(filename);
                newSysLog.setPath(path);
                newSysLog.setUpdataState("未上传");
                newSysLog.setUpdataTime(timestamp);  // 设置更新时间
                sysLogDao.insert(newSysLog); // 插入新记录
            }
        } else {
            // 如果没有找到相同日期的记录，插入新记录
            SysLog newSysLog = new SysLog();
            newSysLog.setFilename(filename);
            newSysLog.setPath(path);
            newSysLog.setUpdataState("未上传");
            newSysLog.setUpdataTime(timestamp);  // 设置更新时间
            sysLogDao.insert(newSysLog); // 插入新记录
        }
    }

    /**
     * 根据传入的 updateTime 字段，查找并拼接所有相关日志文件的内容
     * @param updateTime 数据库中的 updateTime 字段，格式如："2024-12-24 10:36:40"
     * @return 拼接的日志文件内容
     */
    public static String getLogsByDate(int type,String updateTime) {
        StringBuilder logContent = new StringBuilder();
        // 检查传入的 updateTime 是否符合预期格式
        if (updateTime == null || !updateTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            Log.e(TAG, "读取日志txt出错: Invalid updateTime format. Expected format: yyyy-MM-dd HH:mm:ss");
            return "Invalid updateTime format. Expected format: yyyy-MM-dd HH:mm:ss";
        }
        // 提取日期部分（yyyy-MM-dd），去掉时分秒部分
        String datePart = updateTime.substring(0, 8);  // 例如 "2021-03-22"
        // 获取日志目录
        String path = "";
        if (type == 1) {
            path = LOG_DIRECTORY;
        } else {
            path = LOG_XBDIRECTORY;
        }
        Log.e(TAG,"path:" + path);
        File logDir = new File(path);
        if (logDir.exists() && logDir.isDirectory()) {
            // 遍历目录中的所有文件
            File[] files = logDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 只处理文件名包含日期部分且扩展名为 .txt 的文件
                    if (file.getName().startsWith(datePart) && file.getName().endsWith(".txt")) {
                        // 逐行读取文件内容并拼接
                        appendFileContent(file, logContent);
                    }
                }
            }
        } else {
            Log.e(TAG, "日志目录不存在或无法访问：" + path);
            return "日志目录不存在或无法访问";
        }
        // 返回拼接后的日志内容
        return logContent.toString();
    }

    /**
     * 逐行读取文件内容并拼接到 StringBuilder
     *
     * @param file         要读取的日志文件
     * @param logContent   拼接内容的 StringBuilder
     */
    private static void appendFileContent(File file, StringBuilder logContent) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");  // 拼接文件内容
            }
        } catch (IOException e) {
            Log.e(TAG, "读取文件失败：" + file.getName(), e);
        }
    }
}
