package android_serialport_api.xingbang.services;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android_serialport_api.xingbang.db.DatabaseHelper;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyLoad extends AsyncTaskLoader<Cursor> {  
	  
    private Bundle bundle;  
    private Uri uri =  Uri.parse("content://应用程序的包名.数据库名/表名(一般自定义)");  

    private String   querykey;
    private DatabaseHelper mMyDatabaseHelper;
  
    public MyLoad(Context context,Bundle bundle) {  
        super(context);  
        this.bundle = bundle;  
        
        if(bundle != null){  
        	querykey = bundle.getString("key");
        }
        mMyDatabaseHelper = new DatabaseHelper(context, "denatorSys.db", null, 21);
    }  
  
    public MyLoad(Context context) {  
        super(context);  
        mMyDatabaseHelper = new DatabaseHelper(context, "denatorSys.db", null, 21);
    }  
  
    @Override  
    protected void onStartLoading() {  
        // TODO Auto-generated method stub  
        super.onStartLoading();  
        forceLoad();  
    }  
  
    @Override  
    public Cursor loadInBackground() {  
        // TODO Auto-generated method stub  
        /** if(bundle != null){  
             selection = "uname like ?";  
             selectionArgs = new String[] {"%"+bundle.getString("key")+"%"};  
         }  
        Cursor cursor = getContext().getContentResolver().query(uri,  
                columns, selection, selectionArgs, null);  
     **/
        SQLiteDatabase db = mMyDatabaseHelper.getReadableDatabase();
        Cursor cursor = null;
       if(querykey!=null&&"1".equals(querykey)){
        
         cursor = db.rawQuery("select Id AS _id,blastserial,sithole,shellBlastNo,name,delay from denatorBaseinfo a where not exists (select 1 from denatorBaseinfo where a.shellBlastNo=shellBlastNo and a.blastserial = blastserial and id>a.id)order by Id desc",null);//
       }
       if(querykey!=null&&"2".equals(querykey)){           
           cursor = db.rawQuery("SELECT Id AS _id,deName,deEntCode,deFeatureCode,isSelected FROM Defactory order by Id desc", null);
         }
       if(querykey!=null&&"3".equals(querykey)){
           
           cursor = db.rawQuery("SELECT Id AS _id,blastserial,sithole,shellBlastNo,name,delay FROM denatorBaseinfo order by Id desc", null);
         } 
       if(querykey!=null&&"4".equals(querykey)){           
           cursor = db.rawQuery("SELECT Id AS _id,deTypeName,deTypeSecond,isSelected FROM denator_type order by Id desc", null);
         }
      //对数据库表进行查询，会返回游标
        
      //Cursor cursor = db.query("denatorBaseinfo", null, null, null, null, null, null);

      
        return cursor;  
    }  
  
}  
