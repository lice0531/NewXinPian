package android_serialport_api.xingbang.utils.uploadUtils;


import static android_serialport_api.xingbang.Application.getContext;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android_serialport_api.xingbang.R;


/**
 * Toast工具类
 */
public class ToastUtils {

    public static void longs(String message) {
        message(false, message);
    }

    private static void message(boolean isLong, String message) {
        // 加载Toast布局
        View toastRoot = LayoutInflater.from(getContext()).inflate(R.layout.liyi_toast_custom, null);
        // 初始化布局控件
        TextView mTextView = toastRoot.findViewById(R.id.txt_toast);
        // 为控件设置属性
        mTextView.setText(message);
        // Toast的初始化
        Toast toastStart = new Toast(getContext());
        // 获取屏幕高度
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        // Toast的Y坐标是屏幕高度的2/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.TOP, 0, height / 3 * 2);
        toastStart.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toastStart.setView(toastRoot);
        toastStart.show();
    }


}

