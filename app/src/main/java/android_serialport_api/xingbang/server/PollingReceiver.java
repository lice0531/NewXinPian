package android_serialport_api.xingbang.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PollingReceiver extends BroadcastReceiver {
    public static final String TAG = "轮询PollingReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "调用了onReceive" );
        Log.e(TAG,"服务开启了。。。");
        Intent mIntent = new Intent(context, PollingService.class);
        mIntent.putExtra("name","test");
        PollingService.enqueueWork(context,mIntent);
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PollingService.enqueueWork(context,mIntent);
            }else {
                context.startService(mIntent);
            }*/
        PollingUtils.startExactAgain(context, PollingUtils.seconds, PollingReceiver.class, PollingUtils.ACTION);
    }
}
