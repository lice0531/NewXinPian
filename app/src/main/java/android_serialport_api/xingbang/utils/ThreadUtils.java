package android_serialport_api.xingbang.utils;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author : sheng
 * date :   On 2016/11/24
 */
public class ThreadUtils {
    private static ExecutorService executorService = null;

    /**
     * 初始化线程池
     *
     * @return
     */
    public static ExecutorService getThreadPool_Instance() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(4); // 调整线程数
        }
        return executorService;
    }

    public static void threadPoolExecutor(Runnable r) {
        executorService.submit(r);
    }

    /**
     * 创建单一线程池,操作数据库使用
     */
    static ExecutorService singleService;

    public static synchronized ExecutorService getSingleThreadExecutor() {
        if (singleService == null) {
            singleService = Executors.newSingleThreadExecutor();
        }
        return singleService;
    }

    /**
     *
     */
    public static synchronized void closeExecutor() {
        if (singleService != null) {
            singleService.shutdown();
        }
    }
    public static void closeThreadPool() {
        if (executorService != null && !executorService.isShutdown()) {
            Log.e("线程池页面","关闭线程池了");
            executorService.shutdown(); // 优雅关闭线程池
        }
    }
}
