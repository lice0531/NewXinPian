package android_serialport_api.xingbang.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.SysLog;
import android_serialport_api.xingbang.services.outface.FinishDenatorToUpMain;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import static android_serialport_api.xingbang.Application.getDaoSession;

public class Utils {
    public static String httpurl_down_test = "http://test.mbdzlg.com/mbdzlgtxzx/servlet/DzlgMmxzJsonServlert";//在线下载测试
    public static String httpurl_down_dl = "http://qq.mbdzlg.com/mbdzlgtxzx/servlet/DzlgMmxzJsonServlert";//王工给的在线下载
    public static String httpurl_down_dl_new = "http://qq.mbdzlg.com/mbdzlgtxzx/servlet/DzlgMmxzJsonServlert";//王工给的在线下载
    public static String httpurl_upload_test = "http://test.mbdzlg.com/mbdzlgtxzx/servlet/DzlgSysbJsonServlert";//上传测试
    public static String httpurl_upload_dl = "http://qq.mbdzlg.com/mbdzlgtxzx/servlet/DzlgSysbJsonServlert";//王工给的丹灵上传
    public static String httpurl_off = "http://139.129.216.133:8080/mbdzlgtxzx/servlet/DzlgMmlxxzJsonServlert";//离线下载
    public static String httpurl_zbw = "14.23.69.2";
    public static String httpurl_zbw_port = "1088";
    public static String httpurl_face = "http://125.77.73.145:8180/entweb/WSPROXY.do";
    public static String httpurl_xb_upload = "http://182.92.61.78:83/Data/QueryGK";
    public static String httpurl_xb_erweima = "http://182.92.61.78:83/APK_Pro/";
    private static SQLiteDatabase db;
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String bytesToHexFun(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for (byte b : bytes) { // 使用除与取余进行转换
            if (b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }

    /**
     * int转16进制,可以根据字符表转换
     * 普通16进制转换可以用 Integer.toHexString(a).toUpperCase(Locale.ROOT);方法
     */
    public static String intToHex(int n) {
        StringBuilder sb = new StringBuilder(8);
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            sb = sb.append(b[n % 16]);
            n = n / 16;
        }
        a = sb.reverse().toString();
        return a;
    }

    public static String addZero(String str, int lenght){
        String shell=str;
        for (int i=0;i<(lenght-str.length());i++){
            shell="0"+shell;
        }
        return shell;
    }
    //管壳码转换雷管序号Id
    public static String DetonatorShellToSerialNo(String shellStr) {

        String yearStr = shellStr.substring(2, 3);
        String monthStr = shellStr.substring(3, 5);
        String dayStr = shellStr.substring(5, 7);
        String noStr = shellStr.substring(8, 13);

        int day = Integer.parseInt(dayStr);
        int month = Integer.parseInt(monthStr);
        int year = Integer.parseInt(yearStr);
        int no = Integer.parseInt(noStr);
        int serialId = GetSerialNo(year, month, day, no);
        //INT转换为字节
        byte[] bytes = intToByteArray(serialId);
        //byte转换为16进制
        return bytesToHexFun(bytes);
    }

    //新规则得到的管壳码转换雷管序号Id
    public static String DetonatorShellToSerialNo_new(String shellStr) {
        String yearStr = shellStr.substring(2, 3);
        String monthStr = shellStr.substring(3, 5);
        String dayStr = shellStr.substring(5, 7);
        String teStr = shellStr.substring(7, 8);
        String noStr = shellStr.substring(8, 13);
        int day = Integer.parseInt(dayStr);
        int month = Integer.parseInt(monthStr);
        if (month < 8 && yearStr.equals("9")) {
            String str = DetonatorShellToSerialNo(shellStr);
            return str;
        }
//        char[] chars = teStr.toCharArray();
//        int i=(int)chars[0];
//        if (i>70){
//            return "0";
//        }
//        Log.e("雷管转化", "i: "+i );
        int year = Integer.parseInt(yearStr);
        int no = Integer.parseInt(noStr);
        int te = Integer.parseInt(teStr, 16);
        int serialId2 = GetSerialNo_new(te, month, day, no, year);
        //INT转换为字节
        byte[] bytes = intToByteArray(serialId2);
        //byte转换为16进制
        return bytesToHexFun(bytes);
    }

    //截取芯片码中的雷管id(后6位)
    public static String DetonatorShellToSerialNo_newXinPian(String shellStr) {
        //A621407FFFDE5
        //StringIndexOutOfBoundsException: length=4; index=5(直接注册会出现这个问题)
        if(shellStr.length()==8){
            return shellStr;
        }else {
            return shellStr.substring(5);
        }

    }

    /***
     * 反转雷管ID
     * @param id
     * @return 反序id
     */
    public static String getReverseDetonatorNo(String id) {
        return id.substring(6) + id.substring(4, 6) + id.substring(2, 4) + id.substring(0, 2);
    }

    /**
     * 得到管壳码的字节
     * @param shellStr
     * @return
     */
    public static byte getDetonatorShellToFactoryCodeByte(String shellStr) {

        String code = getDetonatorShellToFactoryCodeStr(shellStr);
        return Byte.parseByte(code);
    }

    /***
     * 解析管壳码中的管厂码
     * @param shellStr
     * @return
     */
    public static String getDetonatorShellToFactoryCodeStr(String shellStr) {
        return shellStr.substring(0, 2);
    }

    /***
     * 得到特征码
     * @param shellStr
     * @return
     */
    public static String getDetonatorShellToFeatureStr(String shellStr) {
        return shellStr.substring(7, 8);

    }

    /***
     * 得到特征码
     * @param shellStr
     * @return
     */
    public static byte getDetonatorShellToFeature(String shellStr) {
        String code = shellStr.substring(7, 8);
        return (byte) code.charAt(0);
    }

    //根据年月/序号得到雷管ID
    public static int GetSerialNo(int year, int month, int day, int serialNo) {

        day = day << 19;
        month = month << 24;
        year = year << 28;

        int tempNo = serialNo ^ day;
        tempNo = tempNo ^ month;
        tempNo = tempNo ^ year;

        return tempNo;
    }

    //根据年月/序号得到雷管ID
    public static int GetSerialNo_new(int te, int month, int day, int serialNo, int year) {//9,7,29,55000,9

        int yearStr;
        if (year == 9) {
            yearStr = 0;
        } else if (year == 0) {
            yearStr = 1;
        } else if (year == 1) {
            yearStr = 2;
        } else {
            yearStr = 3;
        }
        char c = (char) te;
        int i = c;
        day = day << 19;//29
        month = month << 24;//7
        te = te << 28;//0
        i = i << 28;//0
        yearStr = yearStr << 17;//9
        Log.e("测试21延时", "noStr  " + serialNo);
        int tempNo = serialNo ^ yearStr;
        tempNo = tempNo ^ day;
        tempNo = tempNo ^ month;
        tempNo = tempNo ^ i;

        return tempNo;
    }


    public static String ShellNo13to17(String shellNo) {
        //13位管壳码转换为17位管壳码
        byte[] sb = shellNo.getBytes();
        String c = CRC16.getCRC(sb);
        String d = (shellNo + c).toUpperCase();//大小写转换
        return d;
    }


    /***
     * 根据雷管ID得到管壳码
     * @param facode //
     * @param feature
     * @param denatorId
     * @return
     */
    public static String GetShellNoById(String facode, String feature, String denatorId) {
        int baseV = new BigInteger(denatorId, 16).intValue();
        //int baseV = 	Integer.parseInt(denatorId, 16);
        //得到序列号
        int serialNo = baseV;
        serialNo = serialNo << 13;
        serialNo = serialNo >>> 13;

        String serNoStr = String.format("%05d", serialNo);

        //得到天
        int day = baseV;
        day = day << 8;
        day = day >>> 27;
        String dayStr = String.format("%02d", day);

        //得到月
        int month = baseV;
        month = month << 4;
        month = month >>> 28;
        String monthStr = String.format("%02d", month);

        //年
        int year = baseV;
        year = year >>> 28;
        //防止新编码规则
//        if(feature.length()!=1){
//            return "0";
//        }
        Log.e("雷管", "feature.length(): " + feature.length());
        return facode + year + monthStr + dayStr + feature + serNoStr;
    }


    /***
     * 根据新协议,读取到雷管ID得到管壳码
     * @param facode
     * @param feature
     * @param denatorId
     * @return
     */
    public static String GetShellNoById_new(String facode, String feature, String denatorId) {
        int baseV = new BigInteger(denatorId, 16).intValue();
        //int baseV = 	Integer.parseInt(denatorId, 16);
        //得到序列号
        int serialNo = baseV;
        serialNo = serialNo << 15;
        serialNo = serialNo >>> 15;

        String serNoStr = String.format("%05d", serialNo);//至少5位10进制数

        //得到天
        int day = baseV;
        day = day << 8;
        day = day >>> 27;
        String dayStr = String.format("%02d", day);//至少2位10进制数

        //得到月
        int month = baseV;
        month = month << 4;
        month = month >>> 28;
        String monthStr = String.format("%02d", month);

        //特征号
        int te = baseV;
        te = te >>> 28;
        String teStr = String.format("%1d", te);

        int t = Integer.parseInt(teStr);
        char a = HEX_CHAR[t];

        //得到年
        int year = baseV;
        year = year << 13;
        year = year >>> 30;
        String yearStr = String.format("%01d", year);
        if (yearStr.equals("0")) {
            yearStr = "9";
        } else if (yearStr.equals("1")) {
            yearStr = "0";
        } else if (yearStr.equals("2")) {
            yearStr = "1";
        } else {
            yearStr = "2";
        }
        String shellNo2 = facode + yearStr + monthStr + dayStr + a + serNoStr;
        Log.e("新的编码规则", "shellNo2: " + shellNo2);
        return shellNo2;
    }


    /**
     * 新 芯片
     * @param facode
     * @param feature
     * @param denatorId
     */
    public static String GetShellNoById_newXinPian(String facode,String feature,String denatorId){
        //C0AA51170C090009003204E5FDFF0641A600330000E5FDFF064D96F5ECC0
        Log.e("ID转换", "facode: "+facode+",feature: "+feature+",denatorId: " +denatorId);
        String a=feature.substring(0,1);
        String b=feature.substring(1);
        //从20年开始为2,到30年要改为3
        return facode + "2"+b+a+denatorId;
    }


    //int To byte[]
    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >>> 24) & 0xFF),
                (byte) ((a >>> 16) & 0xFF),
                (byte) ((a >>> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static String str2HexStr(String origin) {
        byte[] bytes = origin.getBytes();
        return bytesToHexString(bytes);
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr_2(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    public static String strPaddingZero(int val, int len) {

        return strPaddingZero(String.valueOf(val), len);
    }

    /**
     * 截取字符串
     */
    public static String strPaddingZero(String str, int len) {
        int fl = str.length();

        if (fl > len) {
            return str.substring(0, len);
        } else {
            String zero = "";
            for (int i = 0; i < len - fl; i++) {
                zero += "0";
            }
            return zero + str;
        }

    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String hexStr2Str(String hex) {
        byte[] bb = hexStringToBytes(hex);
        String rr = new String(bb);
        return rr;
    }

    //byte[]与int转换
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    //byte[]与short转换
    public static int byteArrayToShort(byte[] b) {
        return b[1] & 0xFF |
                (b[0] & 0xFF) << 8;
    }

    public static int ascStrToInt(String str) {

        int len = str.length() / 2;
        int data = 0;
        int index = 0;
        int bitData = 0;
        for (int i = len - 1; i >= 0; i--) {
            int j = Integer.parseInt(str.substring(index, index + 2), 16);
            bitData = Integer.parseInt(String.valueOf((char) j));
            data = data + (int) Math.pow(10, i) * bitData;
            index += 2;
        }
        return data;
    }

    /**
     * short转换为byte[]
     *
     * @param number
     * @return byte[]
     */
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2]; // 将最低位保存在最低位
        b[0] = (byte) (temp & 0xff);
        temp = temp >>> 8; // 向右移8位
        b[1] = (byte) (temp & 0xff);
        return b;
    }

    /**
     * byte[]转换数字
     *
     * @param bytes
     * @param off
     * @return
     */
    public static int byte2ToUnsignedShort(byte[] bytes, int off) {
        //if(bytes==null)return;
        int high = bytes[off];
        int low = bytes[off + 1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }

    //16进制转换字节
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
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

    /***
     * 得到Float两位小数
     * @param ft
     * @param scale =   2;//设置位数
     * @param roundingMode=  4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
     * @return
     */
    public static float getFloatToFormat(float ft, int scale, int roundingMode) {

        BigDecimal bd = new BigDecimal((double) ft);
        bd = bd.setScale(scale, roundingMode);
        ft = bd.floatValue();
        return ft;
    }

    //数字验证
    public static boolean isNum(String str) {

        Pattern pattern = Pattern.compile("^-?[0-9]+");
        if (pattern.matcher(str).matches()) {
            //数字
            return true;
        } else {
            //非数字
            return false;
        }
    }

    //校验13位管壳码
    public static int checkShellNo(String subBarCode) {
        if (subBarCode.trim().length() < 13) {
            return 1;
        }
        String dayCode = subBarCode.substring(2, 7);
        String serialNo = subBarCode.substring(8);
        if (Utils.isNum(dayCode) == false) {
            return 2;
        }
        if (Utils.isNum(serialNo) == false) {
            return 3;
        }
        return 0;
    }

    public static String swop2ByteOrder(String hexStr) {

        int len = hexStr.length() % 4;
        if (len != 0) return "-1";
        len = hexStr.length();
        String orderStr = "";
        String b1 = "", b2 = "";
        for (int i = 0; i < len; i += 4) {
            b1 = hexStr.substring(i, i + 2);
            b2 = hexStr.substring(i + 2, i + 4);

            orderStr += b2 + b1;
            b1 = "";
            b2 = "";
        }
        return orderStr;

    }

    /***
     * 4字节交换排序
     * @param hexStr
     * @return
     */
    public static String swop4ByteOrder(String hexStr) {

        int len = hexStr.length() % 8;
        if (len != 0) return "-1";
        len = hexStr.length();
        String orderStr = "";
        String b1 = "", b2 = "", b3 = "", b4 = "", b5 = "";
        for (int i = 0; i < len; i += 8) {
            b1 = hexStr.substring(i, i + 2);
            b2 = hexStr.substring(i + 2, i + 4);
            b3 = hexStr.substring(i + 4, i + 6);
            b4 = hexStr.substring(i + 6, i + 8);
            orderStr += b4 + b3 + b2 + b1;
            b1 = "";
            b2 = "";
            b3 = "";
            b4 = "";
        }
        return orderStr;

    }

    public static void main(String args[]) {
//        String dela = DetonatorShellToSerialNo("5680625H00002");
//        String dd = getReverseDetonatorNo(dela);
//        System.out.print(dela);
        /**
         String data = "2970102G99999";
         //得到管壳码
         String str = DetonatorShellToSerialNo(data);
         //字节重排，得到雷管下发Id
         String lowThigh = swop4ByteOrder(str);
         byte[] idByte =  hexStringToBytes(lowThigh);
         //得到管厂码
         byte fcode = getDetonatorShellToFactoryCodeByte(data);
         //得到特征码
         byte fea = getDetonatorShellToFeature(data);
         //
         byte[] delayTime = shortToByte((short)10);

         byte[]  dataBy =  new byte[8];
         System.arraycopy(idByte, 0, dataBy, 0, idByte.length);
         dataBy[4] = fcode;
         dataBy[5] = fea;
         dataBy[6] = delayTime[0];
         dataBy[7] = delayTime[1];

         String hexString="C0AA5506FE00D88600007E78C0";//"C0AA5111FF270026005215112233444838FF1FD204F310C0";

         byte[] cmdBuf =  Utils.hexStringToBytes(hexString);

         From52Test fromData = FiveTestingCmd.decodeFromReceiveDataTestingCommand25("AA", cmdBuf);
         String shellNo = "";
         if(fromData!=null){
         shellNo =  GetShellNoById(fromData.getFacCode(),fromData.getFeature(),fromData.getDenaId());
         }
         **/
        // int test =
        // int baseV = 	Integer.parseInt(80001327, 16);

        //System.out.println("000");
        // int y =  byteArrayToInt(by);

    }

    public static String getDateFormatToFileName() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
        String dateString = formatter.format(date);

        return dateString;
    }

    public static String getDateFormatToFileName2() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
        String dateString = formatter.format(date);

        return dateString;
    }

    public static String getyyMMddHHmmssToDateFormat(String date) {

        String reg = "(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})";

        date = date.replaceAll(reg, "$1-$2-$3 $4:$5:$6");

        return date;
    }


    public static String getDateFormatLong(Date date) {

        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss:SSS");
        String dateString = formatter.format(date);

        return dateString;
    }

    public static String getDateFormat_log(Date date) {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateString = formatter.format(date);

        return dateString;
    }

    public static String getDate(Date date) {

        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd");
        String dateString = formatter.format(date);

        return dateString;
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        }
        return size;
    }

    /***
     * 写入日志
     * @param str
     */
    public static void writeLog(String str) {

        String filePath;
        String oldPath;
        String withDate = getDateFormat_log(new Date()) + " " + str + "\r";
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        //filePath: /storage/emulated/0//XB程序日志/21-03-08程序日志.txt
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "/XB程序日志/" + Utils.getDate(new Date()) + ".txt";
            oldPath = Environment.getExternalStorageDirectory().toString() + File.separator + "/XB程序日志/" + Utils.getDate(new Date()) + "_" + Utils.getDate(new Date()) + ".txt";
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/XB程序日志/" + Utils.getDate(new Date()) + ".txt";
            oldPath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/XB程序日志/" + Utils.getDate(new Date()) + "_" + Utils.getDate(new Date()) + ".txt";
        }
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                File dir = new File(file.getParent());
                boolean a=dir.mkdirs();
                boolean b=file.createNewFile();
                //把文件名存入到数据库中
//                SysLog sysLog = new SysLog();
//                sysLog.setFilename(Utils.getDate(new Date()));
//                sysLog.setPath(filePath);
//                sysLog.setUpdataState("否");
//                sysLog.setUpdataTime("");
//                Application.getDaoSession().getSysLogDao().insert(sysLog);
            } else {
                long fileS = getFileSize(file);
                if (fileS > 1073741824) {//大于1M，
                    boolean ret = file.renameTo(new File(oldPath));
                }
            }
            RandomAccessFile raf = null;
            FileOutputStream out = null;
            try {
                long fileS = getFileSize(file);
                if (fileS > 1073741824) {//大于1M，
                    FileOutputStream outStream = new FileOutputStream(file);
                    outStream.write(withDate.getBytes());
                    outStream.write("\n".getBytes());
                    outStream.close();
                } else {
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(file.length());
                    raf.write(withDate.getBytes());
                    raf.write("\n".getBytes());
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /**
             FileOutputStream outStream = new FileOutputStream(file);
             outStream.write(str.getBytes());
             outStream.close();
             ***/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 写入日志
     * @param str
     */
    public static void writeRecord(String str) {

        String filePath;
        String oldPath;
        String withDate = getDateFormat_log(new Date()) + " " + str + "\r";
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        //filePath: /storage/emulated/0//XB程序日志/21-03-08程序日志.txt
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "/程序运行日志/" + Utils.getDate(new Date()) + ".txt";
            oldPath = Environment.getExternalStorageDirectory().toString() + File.separator + "/程序运行日志/" + Utils.getDate(new Date()) + "_" + Utils.getDate(new Date()) + ".txt";
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/程序运行日志/" + Utils.getDate(new Date()) + ".txt";
            oldPath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/程序运行日志/" + Utils.getDate(new Date()) + "_" + Utils.getDate(new Date()) + ".txt";
        }
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                File dir = new File(file.getParent());
                boolean a=dir.mkdirs();
                boolean b=file.createNewFile();
                //把文件名存入到数据库中
                SysLog sysLog = new SysLog();
                sysLog.setFilename(Utils.getDate(new Date()));
                sysLog.setPath(filePath);
                sysLog.setUpdataState("否");
                sysLog.setUpdataTime("");
                Application.getDaoSession().getSysLogDao().insert(sysLog);
            } else {
                long fileS = getFileSize(file);
                if (fileS > 1073741824) {//大于1M，
                    boolean ret = file.renameTo(new File(oldPath));
                }
            }
            RandomAccessFile raf = null;
            FileOutputStream out = null;
            try {
                long fileS = getFileSize(file);
                if (fileS > 1073741824) {//大于1M，
                    FileOutputStream outStream = new FileOutputStream(file);
                    outStream.write(withDate.getBytes());
                    outStream.write("\n".getBytes());
                    outStream.close();
                } else {
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(file.length());
                    raf.write(withDate.getBytes());
                    raf.write("\n".getBytes());
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /**
             FileOutputStream outStream = new FileOutputStream(file);
             outStream.write(str.getBytes());
             outStream.close();
             ***/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 把雷管写到本地
     * @param str
     */
    public static void writeLeiGuan(String str) {

        String filePath;
        String withDate = str;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        Log.e("是否存在内存卡", "hasSDCard: " + hasSDCard);
        //+getDate(new Date())//获得当前日期
        if (hasSDCard) {

            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "/XingBang/" + "雷管列表.txt";
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/XingBang/" + "雷管列表.txt";
        }
        try {

            File file = new File(filePath);

            if (!file.exists()) {//如果不存在则创建新的文件
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            } else {//如果存在就删除原来的文件,新建一个
                delete(filePath);
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
//                long fileS = getFileSize(file);
//                if (fileS > 1 * 1073741824) {//大于1M，就修改文件的名字
//                    //if(fileS>2*120) {//
//                    boolean ret = file.renameTo(new File(oldPath));
//                }
            }
            RandomAccessFile raf = null;
            FileOutputStream out = null;
            try {
                long fileS = getFileSize(file);
                if (fileS > 1 * 1073741824) {//大于1M，
                    FileOutputStream outStream = new FileOutputStream(file);
                    outStream.write(withDate.getBytes());
                    outStream.write("\n".getBytes());
                    outStream.close();
                } else {
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(file.length());
                    raf.write(withDate.getBytes());
                    raf.write("\n".getBytes());
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /**
             FileOutputStream outStream = new FileOutputStream(file);
             outStream.write(str.getBytes());
             outStream.close();
             ***/

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    /**
     * 读入TXT文件
     */
    public static String readFile() {
        String pathname = Environment.getExternalStorageDirectory().toString() + File.separator + "/XingBang/" + "雷管列表.txt";
        // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (
                FileReader reader = new FileReader(pathname);
                BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {

            String line;
            StringBuffer sb = new StringBuffer();
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "0";
        }
//        return "0";
    }

    /**
     * 读入TXT文件
     */
    public static String readFile(String path) {
        // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (
                FileReader reader = new FileReader(path);
                BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {

            String line;
            StringBuffer sb = new StringBuffer();
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0";
    }
    /**
     * 读入TXT文件
     */
    public static String fenxiLog(String path) {

        // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (
                FileReader reader = new FileReader(path);
                BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {

            String line;
            StringBuffer sb = new StringBuffer();
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                sb.append(line+ ",");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static String readLog(String blastdate) {//2021-08-20
        String time;
        if (blastdate.length() == 8) {
            time = blastdate;
        } else {
            time = blastdate.split(",")[0].replace("/", "-").substring(2);
        }

        String pathname = Environment.getExternalStorageDirectory().toString() + File.separator + "程序运行日志" + File.separator + time + ".txt";
        Log.e("读取日志", "time: " + time);
        Log.e("读取日志", "pathname: " + pathname);
        // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (
                FileReader reader = new FileReader(pathname);
                BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {

            String line;
            StringBuffer sb = new StringBuffer();
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "当前日志为空";
    }


    /**
     * 读入TXT文件
     */
    public static String readLog() {
        String pathname = Environment.getExternalStorageDirectory().toString() + File.separator + "程序运行日志" + File.separator + Utils.getDate(new Date()) + ".txt";
        // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (
                FileReader reader = new FileReader(pathname);
                BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {

            String line;
            StringBuffer sb = new StringBuffer();
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "当前日志为空";
    }


    /***
     * 把本地雷管写到程序
     * @param str
     */
    public static void readLeiGuan(String str) {

        String filePath = null;
        String oldPath = null;
        String withDate = str;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "leiguan.txt";
            oldPath = Environment.getExternalStorageDirectory().toString() + File.separator + "leiguan_" + getDateFormatToFileName() + ".txt";
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "leiguan.txt";
            oldPath = Environment.getDownloadCacheDirectory().toString() + File.separator + "leiguan_" + getDateFormatToFileName() + ".txt";
        }
        try {

            File file = new File(filePath);

            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            } else {
                long fileS = getFileSize(file);
                if (fileS > 1 * 1073741824) {//大于1M，
                    //if(fileS>2*120) {//
                    boolean ret = file.renameTo(new File(oldPath));
                }
            }
            RandomAccessFile raf = null;
            FileOutputStream out = null;
            try {
                long fileS = getFileSize(file);
                if (fileS > 1 * 1073741824) {//大于1M，
                    FileOutputStream outStream = new FileOutputStream(file);
                    outStream.write(withDate.getBytes());
                    outStream.write("\n".getBytes());
                    outStream.close();
                } else {
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(file.length());
                    raf.write(withDate.getBytes());
                    raf.write("\n".getBytes());
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /**
             FileOutputStream outStream = new FileOutputStream(file);
             outStream.write(str.getBytes());
             outStream.close();
             ***/

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    public static void writePersist(String str) {

        String filePath = null;
        String withDate = getDateFormatLong(new Date()) + " " + str;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        FileOutputStream out = null;
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "xb_store.txt";
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "xb_store.txt";
        }
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(withDate.getBytes());
            outStream.write("\n".getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        String regEx = "[/\\:*?<>|\"\n\t]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    //中爆网上传数据
    public static String uploadFireData(Context context, List<String> dataList, String pro_bprysfz, String pro_htid, String pro_xmbh, String pro_coordxy, String server_type, String equ_no, String serverIpStr, String server_portStr, String server_httpStr, String hisInsertFireDate) {

        String date = hisInsertFireDate.replace(",", " ").replace("/", "-");
        date = Utils.getyyMMddHHmmssToDateFormat(date);
        if ((server_type != null && "2".equals(server_type.trim()))) {//中爆网
            String rev = netParamCheck(1, pro_bprysfz, pro_htid, pro_xmbh, pro_coordxy, server_type, equ_no, serverIpStr, server_portStr, server_httpStr, hisInsertFireDate);
            if (rev != null) {
                showToast(context, "上传数据设置错误！1-"+rev, 3000);
                return "1";
            } else {
                String[] xy = pro_coordxy.split(",");
                String x = xy[0];
                String y = xy[1];
                showToast(context, "开始上传", 500);
                String re = sendZBW(equ_no, serverIpStr, Integer.parseInt(server_portStr), x, y, date, dataList);
                Log.e("中爆网上传", "re: " + re);
                if (re.equals("0")) {
                    showToast(context, "中爆网上传数据成功！", 3000);
                } else if (re.equals(" ECONNREFUSED (Connection refused)")) {
                    showToast(context, "中爆网上传数据失败，请确认IP地址正确后,再次上传！", 3000);
                } else if (re.equals(" ENETUNREACH (Network is unreachable)")) {
                    showToast(context, "中爆网上传数据失败，请确认网络是否正确连接后,再次上传！", 3000);
                } else {
                    showToast(context, "中爆网上传数据失败，请重传！", 3000);
                }
                return re;
            }
        } else {
            return "0";
        }

    }

    /**
     * 检测格式
     */
    private static String netParamCheck(int type, String pro_bprysfz, String pro_htid, String pro_xmbh, String pro_coordxy, String server_type, String equ_no, String serverIpStr, String server_portStr, String server_httpStr, String hisInsertFireDate) {

        if (pro_coordxy == null || pro_coordxy.trim().length() < 1) {
            return "位置信息未设置";
        }
        if (!pro_coordxy.contains(",")) {
            return "位置信息设置错误";
        }
        if (equ_no == null || equ_no.trim().length() < 1) {
            return "设备编号未设置";
        }

        if (type == 1) {
            if (serverIpStr == null || serverIpStr.trim().length() < 1) {
                Log.e("服务器地址错误", "serverIpStr: " + serverIpStr + "");
                return "服务器地址错误";
            }
            if (server_portStr == null || server_portStr.trim().length() < 1) {
                return "服务器端口错误";
            }
        } else {
            if (server_httpStr == null || server_httpStr.trim().length() < 1) {
                return "服务器地址错误";
            }
            if (pro_htid == null || pro_htid.trim().length() < 1) {
                return "合同编号未设置";
            }
            if (pro_xmbh == null || pro_xmbh.trim().length() < 1) {
                return "项目编号未设置";
            }

        }
        return null;
    }

    public static String sendZBW(String equNo, String ip, int port, String x, String y, String fireDate, List<String> dataList) {
        String flag = "0";
        try {
            Log.e("中爆网-上传方法", "dataList: " + dataList.toString());
            List<String> sendListPack = FinishDenatorToUpMain.packSendData(equNo, x, y, fireDate, dataList);

            Socket socket = new Socket(ip, port);
            //BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            // BufferedReader is=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InputStream inputStream;
            //  OutputStream outputStream;
            inputStream = socket.getInputStream();
            // outputStream = socket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;
            if (sendListPack != null && sendListPack.size() > 0) {
                Log.e("中爆网", "第一步: ");
                String readline = "", writeline;
                for (int i = 0; i < sendListPack.size(); i++) {
                    writeline = sendListPack.get(i);
                    Log.e("中爆网", "第" + i + "步" + sendListPack.get(i));
                    os.println(writeline);
                    os.flush();
                }

                long currentTime = System.currentTimeMillis();
                long pertTime;
                long spanTime;

                boolean exitFlag = false;

                while (exitFlag == false) {

                    pertTime = System.currentTimeMillis();
                    spanTime = pertTime - currentTime;
                    if (spanTime > 1000 * 60) {
                        flag = "2";
                        break;
                    }
                    if (inputStream.available() != 0) {
                        //读取数据
                        bytes = inputStream.read(buffer);
                        if (bytes > 0) {
                            final byte[] data = new byte[bytes];
                            System.arraycopy(buffer, 0, data, 0, bytes);
                            String msg = new String(data);
//                            Utils.writeLog("readline=" + msg);
                            Log.e("中爆网上传", "返回信息: " + msg);
                            if (msg.indexOf("#08O100$") < 0) {//如果字符串中没有这个字符,返回上传失败
                                flag = "1";
                            }
                            break;
                        }
                    }
                }
                os.close(); //关闭Socket输出流
                //  is.close(); //关闭Socket输入流
                inputStream.close();
                socket.close(); //关闭Socket
            } else {
                flag = "1";
            }
        } catch (Exception e) {
            Log.e("中爆网上传失败", "Error1: " + e);//出错，则打印出错信息
            String[] a = e.getMessage().split(":");
            flag = a[2];
        }

        return flag;

    }

    public static String sendMessage(String equNo, String ip, int port, List<String> dataList) {

        String flag = "0";
        try {
            Log.e("中爆网-上传方法", "dataList: " + dataList.toString());
            List<String> sendListPack = FinishDenatorToUpMain.packSendData(equNo, "0", "0", "0", dataList);

            Socket socket = new Socket(ip, port);
            //BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            // BufferedReader is=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InputStream inputStream;
            //  OutputStream outputStream;
            inputStream = socket.getInputStream();
            // outputStream = socket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;
            if (sendListPack != null && sendListPack.size() > 0) {
                Log.e("中爆网", "第一步: ");
//                Log.e("中爆网", "命令列表: "+sendListPack.toString());
                String readline = "", writeline;
                for (int i = 0; i < sendListPack.size(); i++) {
                    writeline = sendListPack.get(i);
                    Log.e("中爆网", "第" + i + "步" + sendListPack.get(i));
                    os.println(writeline);
                    os.flush();
                }

                long currentTime = System.currentTimeMillis();
                long pertTime = currentTime;
                long spanTime = 0;

//                Utils.writeLog("readline_11=" + readline);
                boolean exitFlag = false;

                while (exitFlag == false) {

                    pertTime = System.currentTimeMillis();
                    spanTime = pertTime - currentTime;
                    if (spanTime > 1000 * 60) {
                        flag = "2";
                        break;
                    }
                    if (inputStream.available() != 0) {
                        //读取数据
                        bytes = inputStream.read(buffer);
                        if (bytes > 0) {
                            final byte[] data = new byte[bytes];
                            System.arraycopy(buffer, 0, data, 0, bytes);
                            String msg = new String(data);
//                            Utils.writeLog("readline=" + msg);
                            Log.e("中爆网上传", "返回信息: " + msg);
                            if (msg.indexOf("#08O100$") < 0) {//如果字符串中没有这个字符,返回上传失败
                                flag = "1";
                            }
                            break;
                        }
                    }
                }
                os.close(); //关闭Socket输出流

                //  is.close(); //关闭Socket输入流
                inputStream.close();
                socket.close(); //关闭Socket

            } else {
                flag = "1";
            }
        } catch (Exception e) {
            Log.e("中爆网上传失败", "Error1: " + e);//出错，则打印出错信息
            String[] a = e.getMessage().split(":");
            flag = a[2];
        }

        return flag;

    }

    //3des加密
    public static String encrypt(String key, String text) throws Exception {
        try {
            byte[] src = text.getBytes("utf-8");
            //DESedeKeySpec会帮你生成24位秘钥，key可以是任意长度
            DESedeKeySpec spec = new DESedeKeySpec(key.getBytes("utf-8"));
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = factory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] res = cipher.doFinal(src);
            //encodeBase64会对字符串3位一组自动补全，因而最后可能会出现 == 或者 =
            return new String(Base64.encodeBase64(res), "utf-8");

        } catch (Exception e) {
            System.out.println("error");
        }
        return null;
    }

    // 加密
    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    // 解密
    public static String getFromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = Utils.deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = Utils.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 煤许 重新排序段号
     */
    public static void deleteData(String mRegion) {
        for (int m = 1; m < 6; m++){
            List<DenatorBaseinfo> list_lg = new GreenDaoMaster().getDuanNoList(mRegion,m+"");
            Log.e("排序雷管", "list_lg: " + list_lg.size());
            for (int i = 0; i < list_lg.size(); i++) {
                DenatorBaseinfo denatorBaseinfo = list_lg.get(i);
                denatorBaseinfo.setDuanNo(list_lg.get(i).getDuan()+"-"+(i + 1));
                getDaoSession().getDenatorBaseinfoDao().update(denatorBaseinfo);
            }
        }
        deleteDataforXuHao(mRegion);
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 重新排序雷管(序号)
     */
    public static void deleteDataforXuHao(String mRegion) {

//        Log.e("排序雷管", "list_lg: " + list_lg.size());
        List<DenatorBaseinfo> list_lg = new GreenDaoMaster().queryDetonatorRegionAsc(mRegion);
        for (int i = 0; i < list_lg.size(); i++) {
            DenatorBaseinfo denatorBaseinfo = list_lg.get(i);
            denatorBaseinfo.setBlastserial(i+1);
            denatorBaseinfo.setSithole((i+1)+"");
            getDaoSession().getDenatorBaseinfoDao().update(denatorBaseinfo);
        }
        Utils.saveFile();//把软存中的数据存入磁盘中
    }


    private static Toast toast;


    public static void showToast(final Context context, final String msg, int time) {
        if (toast == null)
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);

        toast.setGravity(Gravity.BOTTOM, 0, 0);

        Activity context1 = (Activity) context;

        LayoutInflater layoutInflater = context1.getLayoutInflater();

        View inflate = layoutInflater.inflate(R.layout.toast_layout, null);

        TextView toast_msg = inflate.findViewById(R.id.toast_msg);

        toast_msg.setText(msg);

        toast.setView(inflate);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 2000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, time);


    }

    /**
     * 保存文件,把软存中的数据存入磁盘中
     */
    public static void saveFile_1() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String content = " ";
            if (!content.equals("")) {
                //列表中有数据
                File file = new File(Environment.getExternalStorageDirectory() + "/xb/");
                if (!file.exists()) {
                    file.mkdir();
                }
                String path = Environment.getExternalStorageDirectory() + "/xb/" + "test.txt";
//                FileUtil fileUtil = new FileUtil(this);
                try {
                    FileOutputStream fos = new FileOutputStream(new File(path));
                    fos.write(content.getBytes());
                    //将fos的数据保存到内核缓冲区
                    //不能确保数据保存到物理存储设备上，如突然断点可能导致文件未保存
                    fos.flush();
                    //将数据同步到达物理存储设备
                    FileDescriptor fd = fos.getFD();
                    fd.sync();
                    fos.close();
//                    Utils.showTs("保存成功");
//                    fileUtil.save(Environment.getExternalStorageDirectory() + "/xb/" + System.currentTimeMillis() + ".txt", "haha");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存文件
     */
    public static void saveFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String content = GreenDaoMaster.getAllFromInfo();
            if (!content.equals("")) {
                //列表中有数据
                File file = new File(Environment.getExternalStorageDirectory() + "/xb/");
                if (!file.exists()) {
                    file.mkdir();
                }
                String path = Environment.getExternalStorageDirectory() + "/xb/" + "list.csv";
                try {
                    FileOutputStream fos = new FileOutputStream(new File(path));
                    fos.write(content.getBytes());
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存用户文件
     */
    public static void saveFile_Message() {
        MessageBean bean = GreenDaoMaster.getAllFromInfo_bean();
        MmkvUtils.savecode("id", bean.getId().intValue());
        MmkvUtils.savecode("pro_bprysfz", bean.getPro_bprysfz());
        MmkvUtils.savecode("pro_htid", bean.getPro_htid());
        MmkvUtils.savecode("pro_xmbh", bean.getPro_xmbh());
        MmkvUtils.savecode("equ_no", bean.getEqu_no());
        if (bean.getPro_coordxy() != null) {
            MmkvUtils.savecode("pro_coordxy", bean.getPro_coordxy());
        }
        MmkvUtils.savecode("server_addr", bean.getServer_addr());
        MmkvUtils.savecode("server_port", bean.getServer_port());
        MmkvUtils.savecode("server_http", bean.getServer_http());
        MmkvUtils.savecode("server_ip", bean.getServer_ip());
        MmkvUtils.savecode("qiaosi_set", bean.getQiaosi_set());
        MmkvUtils.savecode("preparation_time", Integer.parseInt(bean.getPreparation_time()));
        MmkvUtils.savecode("chongdian_time", Integer.parseInt(bean.getChongdian_time()));
        MmkvUtils.savecode("server_type1", bean.getServer_type1());
        MmkvUtils.savecode("server_type2", bean.getServer_type2());
        MmkvUtils.savecode("pro_dwdm", bean.getPro_dwdm());
        MmkvUtils.savecode("jiance_time", Integer.parseInt(bean.getJiance_time()));
        MmkvUtils.savecode("version", bean.getVersion());
    }

    /**
     * 把String转化为float
     */
    public static float convertToFloat(String number, float defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 把String转化为double
     */
    public static double convertToDouble(String number, double defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 把String转化为int
     */
    public static int convertToInt(String number, int defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** byte[]转Int */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 24) & 0xFF000000);
        return addr;
    }

    /**
     * 四川uid上传规则
     */
    public static String ShellNo13toSiChuan(String shellNo) {
        String denatorId = Utils.DetonatorShellToSerialNo(shellNo);
        String yearStr = denatorId.substring(0, 2);//96
        String monthStr = denatorId.substring(2, 4);//28
        String dayStr = denatorId.substring(4, 6);//00
        String noStr = denatorId.substring(6);//e0
        return "00000" + (noStr + dayStr + monthStr + yearStr).toLowerCase();
    }

    /**
     * 四川uid上传规则(包工意见)把00000改为56060
     */
    public static String ShellNo13toSiChuan_new(String shellNo) {
        String yearStr2 = shellNo.substring(2, 3);//9
        int monthStr2 = Integer.parseInt(shellNo.substring(3, 5));//28

        String denatorId = Utils.DetonatorShellToSerialNo(shellNo);
        String yearStr = denatorId.substring(0, 2);//96
        String monthStr = denatorId.substring(2, 4);//28
        String dayStr = denatorId.substring(4, 6);//00
        String noStr = denatorId.substring(6);//e0
        Log.e("转换", "monthStr2: " + monthStr2);
        if ((yearStr2.equals("1") && monthStr2 > 5) || (yearStr2.equals("0"))) {
            return "00000" + (noStr + dayStr + monthStr + yearStr).toLowerCase();
        }
        return "56060" + (noStr + dayStr + monthStr + yearStr).toLowerCase();
    }

    //新规则得到的管壳码转换雷管序号Id
    public static String  ShellNo_NewDanLing(String shellStr) {
        String changjia = shellStr.substring(0, 2);
        String yearStr = shellStr.substring(2, 3);
        String monthStr = shellStr.substring(3, 5);
        String dayStr = shellStr.substring(5, 7);
        String teStr = shellStr.substring(7, 8);
        String noStr = shellStr.substring(8, 13);
        int day = Integer.parseInt(dayStr);
        if(monthStr.equals("11")){
            monthStr="12";
        }else if(monthStr.equals("12")){
            monthStr="13";
        }
        int t = Integer.parseInt(monthStr);
        char a = HEX_CHAR[t];
        Log.e("转换", "shellStr: "+changjia+"2"+yearStr+a+dayStr+teStr+noStr );
        return changjia+"2"+yearStr+a+dayStr+teStr+noStr;
    }

    /**
     * 读入TXT文件
     */
    public static String readOffline(String pathname) {
//         pathname = Environment.getExternalStorageDirectory().toString() + File.separator+"错误日志"+ File.separator + "错误日志.txt";
        // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (
                FileReader reader = new FileReader(pathname);
                BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {

            String line;
            StringBuffer sb = new StringBuffer();
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "当前日志为空";
    }

    //去除回车空格
    public static String replace(String str) {
        String destination = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            destination = m.replaceAll("");
        }
        return destination;
    }

    public static String XiangHao(String data) {
        String endNo = null;
        switch (data) {
            case "01":
                endNo = "04";
                break;
            case "02":
                endNo = "09";
                break;
            case "03":
                endNo = "14";
                break;
            case "04":
                endNo = "19";
                break;
            case "05":
                endNo = "24";
                break;
            case "06":
                endNo = "29";
                break;
            case "07":
                endNo = "34";
                break;
            case "08":
                endNo = "39";
                break;
            case "09":
                endNo = "44";
                break;
            case "10":
                endNo = "49";
                break;
            case "11":
                endNo = "54";
                break;
            case "12":
                endNo = "59";
                break;
            case "13":
                endNo = "64";
                break;
            case "14":
                endNo = "69";
                break;
            case "15":
                endNo = "74";
                break;
            case "16":
                endNo = "79";
                break;
            case "17":
                endNo = "84";
                break;
            case "18":
                endNo = "89";
                break;
            case "19":
                endNo = "94";
                break;
            case "20":
                endNo = "99";
                break;
            case "A1":
                endNo = "00";
                break;
            case "A2":
                endNo = "01";
                break;
            case "A3":
                endNo = "02";
                break;
            case "A4":
                endNo = "03";
                break;
            case "A6":
                endNo = "05";
                break;
            case "A7":
                endNo = "06";
                break;
            case "A8":
                endNo = "07";
                break;
            case "A9":
                endNo = "08";
                break;
            case "B1":
                endNo = "10";
                break;
            case "B2":
                endNo = "11";
                break;
            case "B3":
                endNo = "12";
                break;
            case "B4":
                endNo = "13";
                break;
            case "B6":
                endNo = "15";
                break;
            case "B7":
                endNo = "16";
                break;
            case "B8":
                endNo = "17";
                break;
            case "B9":
                endNo = "18";
                break;
        }
        return endNo;
    }


    /**
     * 存储单位.
     */
    private static final int STOREUNIT = 1024;

    /**
     * 时间毫秒单位.
     */
    private static final int TIMEMSUNIT = 1000;

    /**
     * 时间单位.
     */
    private static final int TIMEUNIT = 60;


    /**
     * 转化文件单位.
     *
     * @param size 转化前大小(byte)
     * @return 转化后大小
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / STOREUNIT;
        if (kiloByte < 1) {
            return size + " Byte";
        }

        double megaByte = kiloByte / STOREUNIT;
        if (megaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB";
        }

        double gigaByte = megaByte / STOREUNIT;
        if (gigaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB";
        }

        double teraBytes = gigaByte / STOREUNIT;
        if (teraBytes < 1) {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB";
    }

    /**
     * 转化时间单位.
     *
     * @param time 转化前大小(MS)
     * @return 转化后大小
     */
    public static String getFormatTime(long time) {
        double second = (double) time / TIMEMSUNIT;
        if (second < 1) {
            return time + " MS";
        }

        double minute = second / TIMEUNIT;
        if (minute < 1) {
            BigDecimal result = new BigDecimal(Double.toString(second));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " SEC";
        }

        double hour = minute / TIMEUNIT;
        if (hour < 1) {
            BigDecimal result = new BigDecimal(Double.toString(minute));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MIN";
        }

        BigDecimal result = new BigDecimal(Double.toString(hour));
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " H";
    }

    /**
     * 转化字符串.
     *
     * @param source   转化前字符串
     * @param encoding 编码格式
     * @return 转化后字符串
     */
    public static String convertString(String source, String encoding) {
        try {
            byte[] data = source.getBytes("ISO8859-1");
            return new String(data, encoding);
        } catch (UnsupportedEncodingException ex) {
            return source;
        }
    }

    private static SimpleDateFormat simpleDateFormat = null;
    public  static String formatDateTime(long time, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (simpleDateFormat == null) {
            try {
                simpleDateFormat = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            simpleDateFormat.applyPattern(strPattern);
        }
        return simpleDateFormat == null ? "NULL" : simpleDateFormat.format(time);
    }
    public static String getLocationStr(BDLocation location, LocationClient locationClient) {
        if (null == location) {
            return null;
        }
        StringBuffer sb = new StringBuffer(256);
        sb.append("\n定位时间 : ");
        sb.append(location.getTime());
        sb.append("\n回调时间: " + formatDateTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
        sb.append("\n定位类型 : ");// 定位类型
        sb.append(location.getLocType());
        sb.append("\n经度 : ");// 纬度
        sb.append(location.getLongitude());
        sb.append("\n纬度 : ");// 经度
        sb.append(location.getLatitude());
        sb.append("\n精度 : ");// 半径
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            // 运营商信息
            if (location.hasAltitude()) {// *****如果有海拔高度*****
                sb.append("海拔高度 : ");
                sb.append(location.getAltitude());// 单位：米
            }
        }
        sb.append("\n方向: ");
        sb.append(location.getDirection());// 方向
        sb.append("\n国家编码 : ");// 国家码
        sb.append(location.getCountryCode());
        sb.append("\n国家 : ");// 城市
        sb.append(location.getCountry());
        sb.append("\n省份 : ");// 获取省份
        sb.append(location.getProvince());
        sb.append("\n城市编码 : ");// 城市编码
        sb.append(location.getCityCode());
        sb.append("\n城市 : ");// 国家名称
        sb.append(location.getCity());
        sb.append("\n区县 : ");// 区
        sb.append(location.getDistrict());
        sb.append("\n乡镇街道 : ");// 获取镇信息
        sb.append(location.getTown());
        sb.append("\n地址 : ");// 地址信息
        sb.append(location.getAddrStr());
        sb.append("\n附近街道 : ");// 街道
        sb.append(location.getStreet());
        sb.append("\n室内外结果 : ");// *****返回用户室内外判断结果*****
        sb.append(location.getUserIndoorState());
        sb.append("\n位置语义化 : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        sb.append("\nPOI兴趣点 : ");// POI信息
        if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
            for (int i = 0; i < location.getPoiList().size(); i++) {
                Poi poi = (Poi) location.getPoiList().get(i);
                sb.append("\n"+" POI名称 : ");
                sb.append(poi.getName() + ", ");
                sb.append("POI类型 : ");
                sb.append(poi.getTags());
            }
        }
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\n速度 : ");
            sb.append(location.getSpeed());// 速度 单位：km/h
            sb.append("\n卫星数 : ");
            sb.append(location.getSatelliteNumber());// 卫星数目
            sb.append("\n海拔高度 : ");
            sb.append(location.getAltitude());// 海拔高度 单位：米
        }
        sb.append("\nSDK版本 : ");
        if (null != locationClient) {
            String version = locationClient.getVersion();// 获取sdk版本
            sb.append(version);
        }

        return  sb.toString();
    }


    /***
     * 删除日志
     */
    public static void deleteRecord() {
        String filePath;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        //filePath: /storage/emulated/0//XB程序日志/21-03-08程序日志.txt
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "/程序运行日志/" + Utils.getDate(new Date()) + ".txt";
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "/程序运行日志/" + Utils.getDate(new Date()) + ".txt";
        }

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void deleteDirWihtFile(String str) {
        String filePath;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + str ;
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + str ;
        }
        Log.e("删除数据", "deleteDirWihtFile: 1" );
        File dir = new File(filePath);



        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(str); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
        Log.e("删除数据", "deleteDirWihtFile: 2" );
    }

    public static  void deleteFile(File file) {
        Log.e("删除数据", "deleteDirWihtFile: 1" );
        if (file.isFile()) {
            file.delete();
            return;
        }
        Log.e("删除数据", "deleteDirWihtFile: 2" );
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                Log.e("删除数据", "i" +i);
                deleteFile(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 读入TXT文件
     */
    public static String readLog_cmd(String blastdate) {//2021-08-20
        Log.e("读取日志1", "blastdate: " + blastdate);
        String time;
        if (blastdate.length() == 8) {
            time = blastdate;
        } else {
            time = blastdate.split(" ")[0].replace("/", "-");
        }

        String pathname = Environment.getExternalStorageDirectory().toString() + File.separator + "xb程序日志" + File.separator + time + ".txt";
        Log.e("读取日志", "blastdate: " + blastdate);
//        Log.e("读取日志", "time: " + time);
//        Log.e("读取日志", "pathname: " + pathname);
        // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (
                FileReader reader = new FileReader(pathname);
                BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {

            String line;
            StringBuffer sb = new StringBuffer();
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "当前日志为空";
    }

    public static  void deleteRiZhi(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            if(childFiles.length>60){
                for (int i = 0; i < childFiles.length-5; i++) {
                    Log.e("删除数据", "childFiles[i].getName()" +childFiles[i].getName());
                    deleteRiZhi(childFiles[i]);
                }
            }
//            for (int i = 0; i < childFiles.length; i++) {
//                Log.e("删除数据", "childFiles[i].getName()" +childFiles[i].getName());
//                deleteRiZhi(childFiles[i]);
//            }
//            file.delete();
        }
    }

}
