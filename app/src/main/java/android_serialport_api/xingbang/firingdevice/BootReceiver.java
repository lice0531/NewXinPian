package android_serialport_api.xingbang.firingdevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by xingbang on 2019/6/4.
 */

public class BootReceiver extends BroadcastReceiver {
    static final String action_boot ="android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive (Context context, Intent intent) {
        Log.i("charge start", "启动完成");
//
//        if (intent.getAction().equals(action_boot)){
//            Intent mBootIntent = new Intent(context, ZiJianActivity.class);
//            // 下面这句话必须加上才能开机自动运行app的界面
//            mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mBootIntent);
//        }
    }
}
