package android_serialport_api.xingbang.utils;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by suwen on 2017/8/31.
 */

public class ActivityManager {
    private List<Activity> activityList = new LinkedList<>();
    private static ActivityManager instance;

    private ActivityManager() {
    }

    // 单例模式中获取唯一的MyApplication实例
    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 移除一个activity
    public void removeActivity(Activity activity) {
        if (activityList != null && activityList.size() > 0) {
            if (activity != null) {
                activity.finish();
                activityList.remove(activity);
                activity = null;
            }

        }
    }

    // 遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
