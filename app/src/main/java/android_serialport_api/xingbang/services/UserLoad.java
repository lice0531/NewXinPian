package android_serialport_api.xingbang.services;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.db.DatabaseHelper;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UserLoad extends AsyncTaskLoader<Cursor> {  
	  
    private Bundle bundle;  
    private String   querykey;
    private DatabaseHelper mMyDatabaseHelper;
  
    public UserLoad(Context context,Bundle bundle) {  
        super(context);  
        this.bundle = bundle;  
        
        if(bundle != null){  
        	querykey = bundle.getString("key");
        }
        mMyDatabaseHelper = new DatabaseHelper(context, "denatorSys.db", null, Application.db_version);
    }  
  
    public UserLoad(Context context) {  
        super(context);  
        mMyDatabaseHelper = new DatabaseHelper(context, "denatorSys.db", null, Application.db_version);
    }  
  
    @Override  
    protected void onStartLoading() {  
        // TODO Auto-generated method stub  
        super.onStartLoading();  
        forceLoad();  
    }  
  
    @Override  
    public Cursor loadInBackground() {  
     
        SQLiteDatabase db = mMyDatabaseHelper.getReadableDatabase();
        Cursor cursor = null;
       if(querykey!=null&&"1".equals(querykey)){
        
         cursor = db.rawQuery("SELECT Id AS _id,uname,upassword,isface,facepath FROM UserMain", null);
       }
            
        return cursor;  
    }  
  
}  
