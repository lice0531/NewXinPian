package android_serialport_api.xingbang.server;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import android_serialport_api.xingbang.utils.upload.InitConst;

public class PollingUtils {
    public final static String ACTION = "android_serialport_api.xingbang.server.PollingUtils";
    //轮询时间
    public final static int seconds = InitConst.POLLING_TIME;
    public static int count = 0;
    public static void startPollingService(Context context, int seconds, Class<?> cls, String action){
        Log.d("轮询PollingUtils", "调用了");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.putExtra("Id", "Id");
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerTime = SystemClock.elapsedRealtime();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime+seconds*1000, pendingIntent);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime+seconds*1000, pendingIntent);
        }else {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, seconds*1000, pendingIntent);
        }
    }
    public static void startExactAgain(Context context, int seconds, Class<?> cls, String action){
        AlarmManager alarmManager = (AlarmManager)context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerTime = SystemClock.elapsedRealtime();
        long triggerTime2 = System.currentTimeMillis();
        Log.e("轮询PollingUtils", "triggerTime="+triggerTime);//241107415+4000
        Log.e("轮询PollingUtils", "triggerTime2="+triggerTime2);//241107415+4000
        Log.e("轮询PollingUtils", "Build.VERSION.SDK_INT="+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime+seconds*1000, pendingIntent);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime+seconds*1000, pendingIntent);
        }
    }
    public static void stopPollingService(Context context, Class<?> cls, String action){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}
