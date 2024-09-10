package android_serialport_api.xingbang;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.mmkv.MMKV;

import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.litepal.LitePal;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import android_serialport_api.xingbang.db.MyOpenHelper;
import android_serialport_api.xingbang.db.greenDao.DaoMaster;
import android_serialport_api.xingbang.db.greenDao.DaoSession;
import android_serialport_api.xingbang.services.LocationService;
import android_serialport_api.xingbang.utils.CrashExceptionHandler;
import android_serialport_api.xingbang.utils.CrashHandler;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.MyCrashHandler;

public class Application extends MultiDexApplication {

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;
    public LocationService locationService;
    public static Context mContext;
    public static int db_version = 23;
    private static String TAG = "煋邦起爆器";

    private String mSportName;
    private int mPowerIndex;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
//        SerialPortFinder finder= new SerialPortFinder();
//        Log.e("搜寻串口地址", "finder.getAllDevices(): "+finder.getAllDevices() );
        if (mSerialPort == null) {
            switch (Build.DEVICE) {
                // KT50 起爆器设备
                case "KT50_B2": {
                    mSportName = "/dev/ttyMT1";
                    mPowerIndex = 0;
                    break;
                }
                // ST327 S337 起爆器设备
                case "ST327":
                case "S337": {
                    mSportName = "/dev/ttyMSM0";
                    mPowerIndex = 1;
                    break;
                }
                case "FG50": {//波特率230400
                    mSportName = "/dev/ttyS0";
                    mPowerIndex = 2;
                    break;
                }case "KT50": {//新设备
                    mSportName = "/dev/ttyS0";//ttyS0或者ttyS1
                    mPowerIndex = 3;
                    break;
                }
                case "T-QBZD-Z6":
                case "M900": {//新设备
                    mSportName = "/dev/ttyS1";//ttyS0或者ttyS1
                    mPowerIndex = 4;
                    break;
                }
                default:
                    Log.e("上电", "当前机型为: " + Build.DEVICE + " 该机型没有被适配");
                    break;
            }
            Log.e("application", "device: " + Build.DEVICE + "串口号: " + mSportName+" mPowerIndex:"+mPowerIndex);
            mSerialPort = new SerialPort(new File(mSportName), 115200, 0, 8, 1);
        }
        return mSerialPort;
    }
    /**
     * 获取上电指数
     */
    public int getPowerIndex() {
        return mPowerIndex;
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

        Log.e("application", "设备型号: " + Build.DEVICE);
        //定位初始化
        locationService = new LocationService(getApplicationContext());

//      打开错误日志，保存到sd卡
        if(!isApkInDebug(getApplicationContext())){
            //测试过的错误拦截
            CrashExceptionHandler crashExceptionHandler = CrashExceptionHandler.newInstance();
            crashExceptionHandler.init(getApplicationContext());
        }


        Beta.autoCheckUpgrade = false;
        Bugly.init(this, "ed1fa80af8", false);//四川id(腾讯错误日志)//原来的id e43df75202  内蒙 ed1fa80af8

        String dir = Environment.getExternalStorageDirectory() + File.separator + "Xingbang" + "/mmkv";
        MMKV.initialize(dir);//替代SharedPreferences(腾讯工具)
        MmkvUtils.getInstance();
        initGreenDao();//数据存储工具
        LitePal.initialize(getBaseContext());//数据存储工具

        Logger.t(TAG);
        Logger.addLogAdapter(new AndroidLogAdapter());//日志工具

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        // 默认本地个性化地图初始化方法
        SDKInitializer.initialize(getApplicationContext());
        // 自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        // 包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
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
        MultiDex.install(this);//解决65536问题
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }


}
