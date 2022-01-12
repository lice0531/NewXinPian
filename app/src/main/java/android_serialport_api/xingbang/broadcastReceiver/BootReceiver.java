package android_serialport_api.xingbang.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.senter.pda.iam.libgpiot.Gpiot1;

/**
 * Created by xingbang on 2019/6/4.
 */

public class BootReceiver extends BroadcastReceiver {
    private Gpiot1 gpiot1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("启动监听", "启动完成");

        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
//                Intent mBootIntent = new Intent(context, ZiJianActivity.class);
//                // 下面这句话必须加上才能开机自动运行app的界面
//                mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(mBootIntent);
                Log.e("TAG", "手机开机了");
                break;
            case Intent.ACTION_SHUTDOWN:
                Log.e("TAG", "手机关机了");
                break;
            case Intent.ACTION_SCREEN_ON:
                Log.e("TAG", "亮屏");
                if(Build.DEVICE.equals("ST327")||Build.DEVICE.equals("ST337")){
                    gpiot1 = new Gpiot1(); // 初始化GPIO
                    gpiot1.setUartGpio(true);//串口通电
                }

                break;
            case Intent.ACTION_SCREEN_OFF:
                Log.e("TAG", "息屏");
                break;
            case Intent.ACTION_USER_PRESENT:
                Log.e("TAG", "手机解锁");
                break;
        }

    }
}
