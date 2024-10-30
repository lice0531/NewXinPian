package android_serialport_api.mx.xingbang.utils.upload;


import android.util.Log;

/**
 * 点击间隔时间工具类
 */
public class IntervalUtil {

    private static long lastClickTime = 0L;

    public static boolean isFastClick_1() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= 1000) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    /**
     * 防止连点
     */
    public static boolean isFastClick_2() {

        boolean flag = false;

        long curClickTime = System.currentTimeMillis();

        if ((curClickTime - lastClickTime) >= 1500) {

            flag = true;

        } else {

            // 第一次点击
            if (lastClickTime == 0L) {
                flag = true;
            }

        }

        lastClickTime = curClickTime;

        Log.e("isFastClick", "flag: " + flag);
        return flag;
    }



}

