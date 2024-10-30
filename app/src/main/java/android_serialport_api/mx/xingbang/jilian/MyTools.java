package android_serialport_api.mx.xingbang.jilian;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import android_serialport_api.mx.xingbang.Application;

/**
 * Created by suwen on 2017/8/2.
 */

public class MyTools {

    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    public static void putTag(String id) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("id", id);
        editor.apply();
    }

    public static String getTag() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getString("id", "");
    }

    public static void setCloseHigh(boolean is) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("high", is);
        editor.apply();
    }

    public static boolean getCloseHigh() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getBoolean("high", false);
    }

    public static void putCloseHighTime(long currentTime) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("time", currentTime);
        editor.apply();
    }

    public static void putIdCard(String idCard) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("idCard", idCard);
        editor.apply();
    }

    public static long getCloseHighTime() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getLong("time", 0);
    }

    public static String getIdCard() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getString("idCard", "");
    }

    public static void putFactory(String factory) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("factory", factory);
        editor.apply();
    }

    public static String getFactory() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getString("factory", "");
    }

    public static void putFeather(String feather) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("feather", feather);
        editor.apply();
    }

    public static String getFeather() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getString("feather", "");
    }

    public static String getDelay() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getString("delay", "");
    }

    public static String getTwoDecimal(float a) {
        DecimalFormat df = new DecimalFormat("0.000");
        return String.valueOf(df.format(a));
    }

    public static void putCheck(int a) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("check", a);
        editor.apply();
    }

    public static int getCheck() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getInt("check", 1);
    }

    public static void putF1(int a) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("f1", a);
        editor.apply();
    }

    public static int getF1() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getInt("f1", 10);
    }

    public static void putF2(int a) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("f2", a);
        editor.apply();
    }

    public static int getF2() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getInt("f2", 15);
    }

    public static void putF0(int a) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("f0", a);
        editor.apply();
    }

    public static int getF0() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getInt("f0", 10);
    }

    public static void putV1(float a) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("v1", a);
        editor.apply();
    }

    public static float getV1() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getFloat("v1", 8);
    }

    public static void putV2(float a) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("v2", a);
        editor.apply();
    }

    public static float getV2() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getFloat("v2", 16);
    }

    public static int getADelay() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        return sp.getInt("delay", 0);
    }

    public static String getACode() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        return sp.getString("device", "");
    }

    public static String getHexString(byte[] b) {
        String string = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            // System.out.print(hex.toUpperCase());
            string = string + hex.toUpperCase();
        }
        return string;
    }

    public static String getHexString(byte b) {
        String string = "";
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        // System.out.print(hex.toUpperCase());
        string = string + hex.toUpperCase();
        return string;
    }

    public static String getThreeDecimal(float a) {
        DecimalFormat df = new DecimalFormat("0.000");
        return String.valueOf(df.format(a));
    }

    public static String getThreeDecimalDouble(double a) {
        DecimalFormat df = new DecimalFormat("0.000");
        return String.valueOf(df.format(a));
    }

    public static void putqiao(String tag) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("qiao", tag);
        editor.apply();
    }

    public static String getqiao() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getString("qiao", "");
    }

    public static void put30(String tag) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("code30", Integer.valueOf(tag));
        editor.apply();
    }

    public static int get30() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getInt("code30", 20);
    }

    public static void put38(String tag) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("code38", Integer.valueOf(tag));
        editor.apply();
    }

    public static void putdy(String tag) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("codedy", Integer.valueOf(tag));
        editor.apply();
    }

    public static int getdy() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getInt("codedy", 10);
    }


    public static int get38() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        return sp.getInt("code38", 5);
    }

    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < 4; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * byte转HexString 没有空格
     *
     * @param b byte[] a = new byte[2];
     *          a[0] = (byte) 0x00;
     *          a[1] = (byte) 0xC8;
     * @return 00C8
     */
    public static String byte2HexStrNoBlank(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < 4; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append("");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String byte2HexStrNoBlank2(byte b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        stmp = Integer.toHexString(b & 0xFF);
        sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
        sb.append("");
        return sb.toString().toUpperCase().trim();
    }

    /**
     * 16进制字符串转字符串
     *
     * @param src
     * @return
     */
    public static String hexString2String(String src) {
        String temp = "";
        for (int i = 0; i < src.length() / 2; i++) {
            temp = temp
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),
                    16).byteValue();
        }
        return temp;
    }

    public static String getDelayHexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 4; n < 6; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * Convert hex string to byte[] 把为字符串转化为字节数组
     *
     * @param hexString the hex string 字符串中不能有空格例如00c8
     * @return byte[]
     */
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

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * int 转hexString
     *
     * @param num num
     * @return string
     */
    public static String getHexStringFromInt(int num) {
        String str = "";
        String a = Integer.toHexString(num);
        if (a.length() == 1) {
            str = "000" + a;
        } else if (a.length() == 2) {
            str = "00" + a;
        } else if (a.length() == 3) {
            str = "0" + a;
        } else if (a.length() == 4) {
            str = a;
        } else {
            return "0000";
        }

        return str;
    }

    /**
     * 二进制转16进制
     *
     * @param bString
     * @return
     */
    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 16进制转2进制
     *
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * 获取校验和
     */
    public static byte getXor(byte[] data, int begin, int end) {
        byte A = 0;
        for (int i = begin; i < end; i++) {
            A ^= data[i];
        }
        return A;
    }

    public static int getRandom() {
        Random random = new Random();
        return random.nextInt(9000) + 1000;
    }

    public static String getCode() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        String code = sp.getString("code", "");
        return code;
    }

    public static String getLock() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        String code = sp.getString("lock", "");
        return code;
    }

    /**
     * 解锁数据
     */
    public static void putLock() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lock", "false");
        editor.commit();
    }

    /**
     * 锁定数据
     */
    public static void lock() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lock", "true");
        editor.putString("time", (new SimpleDateFormat("yyMMddHHmmss")).format(new Date()));
        editor.putString("time1", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
        editor.apply();
    }

    /**
     * 获取time
     */
    public static String getTime() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        return sp.getString("time", "");
    }

    /**
     * 获取time
     */
    public static String getTime1() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        return sp.getString("time1", "");
    }

    /**
     * 获取ip
     */
    public static String getIp() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        return sp.getString("ip", "");
    }

    /**
     * 获取端口
     *
     * @return 端口号
     */
    public static int getPort() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        return sp.getInt("port", 0);
    }

    /**
     * 获取选择的模式
     */
    public static String getMode() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        return sp.getString("mode", "");
    }

    /**
     * 获取经度
     */
    public static String getLat() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        return sp.getString("lat", "");
    }

    /**
     * 获取纬度
     */
    public static String getLng() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("setting", 0);
        return sp.getString("lng", "");
    }

    public static String getStrFromStr(String value) {
        if (value.equals("")) {
            return "10.0001";
        }
        int start = value.indexOf(".");
        return value.substring(0, start + 5);
    }

    /**
     * 存入次数
     */
    public static void putNum(int num) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("num", num);
        editor.apply();
    }

    /**
     * 存入上传模式，0：都有，1：中爆网，2：丹灵
     */
    public static void putUpload(int code) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("upload", code);
        editor.apply();
    }

    /**
     * 获取模式数据
     *
     * @return
     */
    public static int getUpload() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", 0);
        return sp.getInt("upload", 1);
    }

    /**
     * 保存数组
     */
    public static void putArr(Set<String> mSet) {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("set", mSet);
        editor.apply();
    }

    /**
     * 获取数组
     */
    public static Set<String> getArr() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", 0);
        return sp.getStringSet("set", new HashSet<String>());
    }

    /**
     * 获取次数
     */
    public static int getNum() {
        SharedPreferences sp = Application.getContext().getSharedPreferences("info", 0);
        return sp.getInt("num", 0);
    }

}
