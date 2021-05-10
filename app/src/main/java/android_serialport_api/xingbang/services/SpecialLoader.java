package android_serialport_api.xingbang.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.firingdevice.ReisterMainPage_scan;

/**
 * Created by Administrator on 2015/10/7.
 */
public class SpecialLoader extends SimpleCursorLoader {

    ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Context context;

    public SpecialLoader(Context context) {
        super(context);
        this.context = context;

    }

    @Override
    public Cursor loadInBackground() {
        DatabaseHelper dh = new DatabaseHelper(this.context, "denatorSys.db", null, 21);
        
        SQLiteDatabase database = dh.getReadableDatabase();
        String table = "denatorBaseinfo";
        String[] columns = new String[]{"Name", "No"};
        //这个地方因为我用的是activeandroid 的orm 框架，所以默认的自增长主键是Id，但是SimpleCursorAdapter
        //需要的是_id 否则会报错，所以这里要重命名一下
        Cursor cursor = database.rawQuery("SELECT Id AS _id,blastserial,sithole,shellBlastNo,name FROM denatorBaseinfo", null);
        if (database != null) {
            if (cursor != null) {
                //注册一下这个观察者
                cursor.registerContentObserver(mObserver);
                //这边也要注意 一定要监听这个uri的变化。但是如果你这个uri没有对应的provider的话
                //记得在你操作数据库的时候 通知一下这个uri
                cursor.setNotificationUri(context.getContentResolver(), ReisterMainPage_scan.uri);
            }

        }
        return cursor;
    }
}