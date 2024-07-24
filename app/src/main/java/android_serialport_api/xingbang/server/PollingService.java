package android_serialport_api.xingbang.server;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import org.greenrobot.eventbus.EventBus;

import android_serialport_api.xingbang.jilian.FirstEvent;

public class PollingService extends JobIntentService {
    private String TAG = "轮询PollingService::";
    static final int JOB_ID = 10111;
    private static Context mContext ;

    public static void enqueueWork(Context context, Intent work) {
        mContext = context ;
        enqueueWork(context, PollingService.class, JOB_ID, work);
        Log.e("PollingService::","enqueueWork....");
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent == null) {
            Log.e(TAG,"intent == null");
            return;
        }
        //接收到轮询消息后进行处理
        Log.e(TAG,"轮询运行中。。");
        //在这里给子机发消息获取需要的数据
        EventBus.getDefault().post(new FirstEvent("pollingService"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }
}
