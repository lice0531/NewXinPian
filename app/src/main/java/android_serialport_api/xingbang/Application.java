package android_serialport_api.xingbang;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.tencent.bugly.Bugly;
import com.tencent.mmkv.MMKV;

import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.litepal.LitePal;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import android_serialport_api.xingbang.db.MyOpenHelper;
import android_serialport_api.xingbang.db.greenDao.DaoMaster;
import android_serialport_api.xingbang.db.greenDao.DaoSession;
import android_serialport_api.xingbang.services.LocationService;
import android_serialport_api.xingbang.utils.MmkvUtils;

public class Application extends MultiDexApplication {

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;
    public LocationService locationService;
    public static Context mContext;
    public static int db_version = 22;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            String path;
            path = "/dev/ttyMT1";//KT50
//            path = "/dev/ttyS2";
//            path = "dev/ttys1";
            int baudrate = 115200;
            mSerialPort = new SerialPort(new File(path), baudrate, 0, 8, 1);
        }
        return mSerialPort;
    }

    public void serialFlush() {
        if (mSerialPort != null) {
            mSerialPort.tcflush();
        }
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    private List<Activity> oList;//用于存放所有启动的Activity的集合

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        oList = new ArrayList<>();

        Bugly.init(this, "e43df75202", false);//四川id

        String dir = Environment.getExternalStorageDirectory() +  File.separator +"Xingbang"+ "/mmkv";
        MMKV.initialize(dir);//替代SharedPreferences
        MmkvUtils.getInstance();
        //定位初始化
        locationService = new LocationService(getApplicationContext());
        //数据库实例化
//      打开错误日志，保存到sd卡
//        MyCrashHandler crashHandler = MyCrashHandler.getInstance();
//        crashHandler.init(this);

        initGreenDao();
        LitePal.initialize(getBaseContext());//数据存储工具
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initGreenDao() {
        MyOpenHelper helper = new MyOpenHelper(this, "denatorSys.db", null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());

//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "denatorSys.db");
//        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession(IdentityScopeType.None);
    }

    public static DaoSession daoSession;

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
        // 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
        //判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
