package android_serialport_api.xingbang.utils.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: XbUtils
 * @Description: 兴邦工具类
 * @Date: 11/25/20 11:27 AM
 * @Author: kalinaji
 */
public class XbUtils {

    /**
     * 2字节交换排序
     *
     * @param hexStr
     */
    public static String swop2ByteOrder(String hexStr) {

        int len = hexStr.length() % 4;
        if (len != 0) return "-1";
        len = hexStr.length();
        StringBuilder orderStr = new StringBuilder();
        String b1 = "", b2 = "";
        for (int i = 0; i < len; i += 4) {
            b1 = hexStr.substring(i, i + 2);
            b2 = hexStr.substring(i + 2, i + 4);
            orderStr.append(b2).append(b1);
        }
        return orderStr.toString();

    }

    /**
     * 4字节交换排序
     *
     * @param hexStr
     */
    public static String swop4ByteOrder(String hexStr) {
        int len = hexStr.length() % 8;
        if (len != 0) {
            return "-1";
        }
        len = hexStr.length();
        StringBuilder orderStr = new StringBuilder();
        String b1, b2, b3, b4;
        for (int i = 0; i < len; i += 8) {
            b1 = hexStr.substring(i, i + 2);
            b2 = hexStr.substring(i + 2, i + 4);
            b3 = hexStr.substring(i + 4, i + 6);
            b4 = hexStr.substring(i + 6, i + 8);
            orderStr.append(b4).append(b3).append(b2).append(b1);
        }
        return orderStr.toString();
    }

    /**
     * 得到Float两位小数
     *
     * @param ft
     * @param scale        =2          设置位数
     * @param roundingMode =4 表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
     */
    public static float getFloatToFormat(float ft, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(ft);
        bd = bd.setScale(scale, roundingMode);
        ft = bd.floatValue();
        return ft;
    }

    /**
     * 16进制 转换 byte[]字节数组
     *
     * @param hexStr
     */
    public static byte[] hexStringToBytes(String hexStr) {
        if (hexStr == null || hexStr.equals("")) {
            return null;
        }
        hexStr = hexStr.toUpperCase();
        int length = hexStr.length() / 2;
        char[] hexChars = hexStr.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * byte[]字节数组 转换 数字
     *
     * @param bytes
     * @param off
     */
    public static int byte2ToUnsignedShort(byte[] bytes, int off) {
        int high = bytes[off];
        int low = bytes[off + 1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }


    /**
     * 通过 Assets/FileManager 获取 文件字节流数组
     *
     * @param context  上下文
     * @param getFrom  1:从Assets获得 2:从文件管理器获得 3:从xb获得
     * @param fileName 1 文件名称
     * @param uri      2 文件uri路径
     * @param path     3 文件绝对路径
     */
    public static byte[] getBinFileByte(Context context, int getFrom,
                                        String fileName,
                                        Uri uri,
                                        String path

    ) {
        InputStream inputStream = null;
        try {
            // 从Assets获得
            if (getFrom == 1) {
                inputStream = context.getResources().getAssets().open(fileName);
                Log.e("getBinFileByte", "Assets fileName: " + fileName);

                // 从文件管理器获得
            } else if (getFrom == 2) {
                inputStream = context.getContentResolver().openInputStream(uri);
                Log.e("getBinFileByte", "文件管理器 uri: " + uri);

                // 从xb获得
            } else if (getFrom == 3) {
                Log.e("getBinFileByte", "从xb获得 path: " + path);

//                Uri uri1;
//                // 4.4以上
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//                    path = FileInfoUtils.getPath(context, uri1);
//
//                    // 4.4以下
//                } else {
//                    path = FileInfoUtils.getRealFilePath(context, uri1);
//                }
//                FileUriPathUtils a = new FileUriPathUtils();
//                Uri uri1 = Uri.parse(path);

                inputStream = new FileInputStream(path);
//                Uri uri1 = getFileContentUri(context, new File(path));
//                Log.e("getBinFileByte", "从xb获得 uri1: " + uri1);
//                inputStream = context.getContentResolver().openInputStream(uri1);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("XbUtils", "getBinFileByte e: " + e.toString());
        }

        if (inputStream != null) {

            try {
                return toByteArray(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            return null;
        }

        return new byte[0];
    }


    /**
     * 输入流InputStream 转 字节数组byte[]
     *
     * @param input 输入流
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        Log.e("XbUtils", "output.toByteArray().length:" + output.toByteArray().length);
        return output.toByteArray();
    }

    /**
     * 固件分割的块数
     *
     * @param context  上下文
     * @param format   格式      1024
     * @param isAssets true/false 从Assets获取文件/从文件管理器获取文件
     * @param fileName Assets目录下 文件名称
     * @param uri      文件管理器    文件路径
     */
    public static int getDivisionNumber(Context context,
                                        int format,
                                        int isAssets,
                                        String fileName,
                                        Uri uri,
                                        String path

    ) {
        int number;
        int remainder;
        int file_length = getBinFileByte(
                context,
                isAssets,
                fileName,
                uri,
                path).length;
        number = file_length / format;
        remainder = file_length % format;
        if (remainder != 0) {
            number = number + 1;
        }

        Log.e("XbUtils", "getDivisionNumber 固件分割的块数: " + number);

        return number;
    }


    /**
     * 合并byte[]数组 （不改变原数组）
     *
     * @param byte_1
     * @param byte_2
     * @return 合并后的数组
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    /**
     * 遍历文件
     *
     * @param path
     * @param fileList
     */
    private void getFileList(File path, HashMap<String, String> fileList) {
        // 如果是文件夹的话
        if (path.isDirectory()) {
            // 返回文件夹中有的数据
            File[] files = path.listFiles();
            // 先判断下有没有权限，如果没有权限的话，就不执行了
            if (null == files)
                return;

            for (File file : files) {
                getFileList(file, fileList);
            }
        }
        // 如果是文件的话直接加入
        else {
            Log.e("liyi", path.getAbsolutePath());
            //进行文件的处理
            String filePath = path.getAbsolutePath();
            //文件名
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            //添加
            fileList.put(fileName, filePath);
        }
    }


    /**
     * 获取 指定文件夹下 的 指定类型 的 文件名称列表
     *
     * @param path
     */
    public static List<String> getFileNameList(String path, String type) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e("getFilesAllName", "空目录");
            return null;
        }
        List<String> list = new ArrayList<>();
        for (File value : files) {
            if (value.getName().endsWith(type)) {
                list.add(value.getAbsolutePath());
            }
        }
//        Log.e("getFilesAllName", "list.size(): " + list.size());
        return list;
    }


    /**
     * 版本号比较
     *
     * @param version1
     * @param version2
     * @return 0: 代表 相等
     * 1: 代表 version1 大于 version2
     * -1: 代表 version1 小于 version2
     */
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");

        Log.e("compareVersion", "version1.length: " + version1Array.length);
        Log.e("compareVersion", "version2.length: " + version2Array.length);

        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        // 循环判断每位的大小
//        Log.e("compareVersion", "循环判断每位的大小: " + version1Array[index]);
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }


    /**
     * 获取本地最高版本Apk
     *
     * @param list 本地apk文件列表
     */
    public static String getLocalMaxApk(List<String> list) {

        String fileName;

        // 本地存在 多个安装包
        if (list.size() > 1) {

            // 获取文件名称，截取版本号
            String fileName_max = list.get(0);
            String version_max = InterceptedVersion(fileName_max);

            // 循环比较大小
            for (int i = 1; i < list.size(); i++) {
                // 获取文件名称，截取版本号
                String name_curr = list.get(i);
                String version_curr = InterceptedVersion(name_curr);

                // 0:等于 1:大于 -1:小于
                int index = compareVersion(version_max, version_curr);

                // 如果 左小 右大(如果比第一个安装包 版本高则 替换)
                if (index == -1) {
                    fileName_max = list.get(i);
                    version_max = version_curr;
                }

            }

            fileName = fileName_max;
        }
        // 本地只存在一个安装包
        else {
            fileName = list.get(0);
        }
        Log.e("getLocalMaxApk", "本地安装包中版本最高: " + fileName);

        return fileName;
    }


    /**
     * 是否有需要的安装包
     *
     * @param ftpFileName 需要下载的服务器文件名称
     * @param list        本地apk文件列表
     */
    public static String isHaveApk(String ftpFileName, List<String> list) {

        // 截取服务器Apk版本号
        String str_version_ftp = XbUtils.InterceptedVersion(ftpFileName); // OldCode_Version_23
        Log.e("liyi_isHaveApk", "str_version_ftp: " + str_version_ftp);
        String fileName;

        // 循环比较大小
        for (int i = 0; i < list.size(); i++) {

            // 本地APK名称
            fileName = list.get(i);
            Log.e("liyi_isHaveApk", "fileName_local: " + fileName);

            // 对比是不是同一种APK
            String str_sub_ftp = ftpFileName.substring(0, 10);
            Log.e("liyi_isHaveApk", "str_sub_ftp: " + str_sub_ftp);

            // 如果 本地有APK 进行版本对比(本地APK 和 服务器APK)
            if (fileName.contains(str_sub_ftp)) {

                // 截取本地apk 版本号
                String str_version_local = InterceptedVersion(fileName);
                Log.e("liyi_isHaveApk", "str_version_local: " + str_version_local); // OldCode_Version_22

                String str_ftp_code = str_version_ftp.substring(str_version_ftp.length() - 2);
                String str_local_code = str_version_local.substring(str_version_local.length() - 2);
                int ftp_code = Integer.parseInt(str_ftp_code);        // 23
                int local_code = Integer.parseInt(str_local_code);    // 22

                // 服务器版本 <= 本地版本
                if (ftp_code <= local_code) {
                    Log.e("liyi_isHaveApk", "服务器版本 <= 本地版本: " + fileName);
                    return fileName;
                }
                // 服务器版本 > 本地版本  -> 删除本地版本
                else {
                    Log.e("liyi_isHaveApk", "服务器版本 > 本地版本: " + fileName);
                    // 删除本地版本 (旧版本)
                    XbUtils.delete(fileName);
                    return "";
                }

            }

        }

        Log.e("liyi_isHaveApk", "本地没有安装包: 空");
        return "";
    }

    /**
     * 截取apk版本
     *
     * @param fileName
     */
    public static String InterceptedVersion(String fileName) {
        return fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf(".apk"));
    }

    /**
     * 获取版本号
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName + "." + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 判断文件是否存在
     */
    public static boolean isExists(String path) {

        try {
            File file = new File(path);
            if (file.exists()) {
                Log.e("liyi", path + " 路径下文件存在");
                return true;
            } else {
                Log.e("liyi", path + " 路径下文件不存在");
                return false;
            }

        } catch (Exception e) {
            Log.e("liyi", "isExists e: " + e.toString());
            return false;
        }

    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            Log.e("liyi", "删除文件失败:" + delFile + "不存在!");
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile);
        }
        return false;
    }

    /**
     * 删除单个文件
     *
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("liyi", "删除单个文件: " + filePath$Name + " 成功！");
                return true;
            } else {
                Log.e("liyi", "删除单个文件: " + filePath$Name + " 失败!");
                return false;
            }
        } else {
            Log.e("liyi", "删除单个文件失败: " + filePath$Name + " 不存在!");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Log.e("liyi", "删除目录失败:" + filePath + "不存在!");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            Log.e("liyi", "删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e("--Method--", "Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！");
            return true;
        } else {
            Log.e("liyi", "删除目录：" + filePath + "失败！");
            return false;
        }
    }


    /**
     * 打开安装包(8.0以下使用)
     *
     * @param context
     * @param apkPath
     */
    public static void openAPKFile_7(Context context, String apkPath) {
        if (context == null || TextUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 会提示完成、打开。
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, "${applicationId}.fileProvider", file);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

        // 关闭程序
//        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /**
     * 打开安装包(兼容8.0)
     */
    public static void openAPKFile_8(Activity activity, String path) {
        String mimeDefault = "application/vnd.android.package-archive";

        File apkFile = null;
        if (!TextUtils.isEmpty(path)) {
            // mApkUri是apk下载完成后在本地的存储路径
            apkFile = new File(Uri.parse(path).getPath());
        }
        if (apkFile == null) {
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 兼容7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // 这里牵涉到7.0系统中URI读取的变更
                Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", apkFile);
                intent.setDataAndType(contentUri, mimeDefault);
                // 兼容8.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // 如果没有权限
                    if (!activity.getPackageManager().canRequestPackageInstalls()) {
                        startInstallPermissionSettingActivity(activity);
                        return;
                    }
                }
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), mimeDefault);
            }
            if (activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                //如果APK安装界面存在，携带请求码跳转。使用forResult是为了处理用户 取消 安装的事件。外面这层判断理论上来说可以不要，但是由于国内的定制，这个加上还是比较保险的
                activity.startActivityForResult(intent, 3500);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startInstallPermissionSettingActivity(Activity activity) {
        //后面跟上包名，可以直接跳转到对应APP的未知来源权限设置界面。使用startActivityForResult 是为了在关闭设置界面之后，获取用户的操作结果，然后根据结果做其他处理
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, 2500);
    }


    /**
     * 打开Apk
     */
    public static void openAPK(Activity activity, String path) {

        // 兼容8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 如果没有权限
            if (!activity.getPackageManager().canRequestPackageInstalls()) {
                startInstallPermissionSettingActivity(activity);
            }
            // 如果有权限
            else {
                openAPKFile_8(activity, path);
            }
        }
        // 8.0以下
        else {
            openAPKFile_7(activity, path);
        }
    }


    /**
     * 检测文件是否存在
     *
     * @param path
     */
    public static void isFileExistence(String path) {
        Log.e("liyi", path + " 是否存在: " + !XbUtils.isExists(path));
        // 如果 文件夹不存在
        if (!XbUtils.isExists(path)) {
            // 创建文件夹
            new File(path).mkdirs();
        }
    }


    public static long getPercent(long count, long total) {
        if (total == 0) {
            return 0;
        }
        BigDecimal currentCount = new BigDecimal(count);
        BigDecimal totalCount = new BigDecimal(total);
        BigDecimal divide = currentCount.divide(totalCount, 2, BigDecimal.ROUND_HALF_UP);
        return divide.multiply(new BigDecimal(100)).longValue();
    }

}
