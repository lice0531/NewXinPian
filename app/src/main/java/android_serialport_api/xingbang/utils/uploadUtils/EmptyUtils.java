package android_serialport_api.xingbang.utils.uploadUtils;

import android.os.Build;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 非空判断工具类
 */
public class EmptyUtils {

    private EmptyUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断对象是否为空
     * @param obj 对象
     * @return {@code true}: 为空<br>{@code false}: 不为空
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String && obj.toString().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof SparseArray && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseBooleanArray && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseIntArray && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        return obj instanceof SparseLongArray && ((SparseLongArray) obj).size() == 0;
    }

    /**
     * 判断list是否为空
     * @param str String
     * @return boolean true:空;false:非空
     */
    public static boolean isListEmpty(List str) {
        return ((str == null) || (str.size()<=0));
    }


    /**
     * 判断对象是否非空
     *
     * @param obj 对象
     * @return {@code true}: 非空<br>{@code false}: 空
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }


    //判断是否为空，可能已分配空间有值为 "" (空串)，也可能未分配空间没有值为 null
    public static boolean isStrEmpty(String str) {
        return "".equals(str) || str == null;
    }
    //判断是否不为空
    public static boolean isStrNotEmpty(String str) {
        return !"".equals(str) && str != null;
    }
}
