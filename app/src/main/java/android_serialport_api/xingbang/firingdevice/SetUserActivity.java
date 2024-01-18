package android_serialport_api.xingbang.firingdevice;


import org.apache.commons.lang.StringUtils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;

import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.services.UserLoad;
import android_serialport_api.xingbang.utils.Utils;

/**
 * @author zenghp
 *用户管理
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetUserActivity extends BaseActivity  implements LoaderCallbacks<Cursor>{
//	public class ReisterMainPage_scan extends SerialPortActivity  implements LoaderCallbacks<Cursor>{
   
	public static final Uri uri = Uri.parse("content://android_serialport_api.xingbang.UserMain");
	private SimpleCursorAdapter adapter;
	private DatabaseHelper mMyDatabaseHelper;
	private SQLiteDatabase db;
	private Button btn_return;
	private Button btn_inputOk;

	private EditText et_user_name;
	private EditText et_user_pw;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setenv_activity_user_main);
		// 标题栏
		setSupportActionBar(findViewById(R.id.toolbar));
		//扫描结束		
		//管壳号扫描分码--开始
		et_user_name =  (EditText) this.findViewById(R.id.user_name);		
		et_user_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
	           @Override  
	           public void onFocusChange(View v, boolean hasFocus) {  
	        	   displayInputKeyboard(v,hasFocus);
	           }  
	     });  
		et_user_pw=  (EditText) this.findViewById(R.id.user_pw);
		et_user_pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
	           @Override  
	           public void onFocusChange(View v, boolean hasFocus) {  
	        	   displayInputKeyboard(v,hasFocus);
	           }  
	     });  
		
		 mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
		 db = mMyDatabaseHelper.getReadableDatabase();
		ListView listView = (ListView) this.findViewById(R.id.setenv_user_listview); 
		adapter = new SimpleCursorAdapter(SetUserActivity.this, R.layout.item_user,
					null,  new String[]{"uname", "upassword"}, new int[]{R.id.user_name, R.id.user_pw},  
	                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);  
		  //adapter.
		listView.setAdapter(adapter);
		getLoaderManager().initLoader(0, null,this);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
		listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                ListView lr = (ListView) v;
                LinearLayout myte = (LinearLayout) lr.getChildAt(0);
                TextView dd = (TextView) myte.getChildAt(1);
                menu.setHeaderIcon(R.drawable.icon);
                menu.setHeaderTitle(dd.getText().toString());
                menu.add(0, 1, 3, "删除");
              
                menu.add(0, 2, 2, "修改");
                //设置第三个参数反向  所以出现的菜单是反着的

            }
        });
       btn_return = (Button) findViewById(R.id.btn_user_main_exit);
       btn_return.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intentTemp = new Intent();
               intentTemp.putExtra("backString","");
               setResult(1,intentTemp);
               finish();
           }
       });
       btn_inputOk = (Button) findViewById(R.id.btn_user_main_save);
       btn_inputOk.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               
              hideInputKeyboard();
              
              String checstr = checkData();
              if(checstr==null||checstr.trim().length()<1){
            	  String prex = "";
            	  String user_name = et_user_name.getText().toString();
          		  String user_pw = et_user_pw.getText().toString();
          		  
          		  insertDenator(user_name,user_pw);
          		 
              }else{
				  show_Toast(checstr);
              }
              
           }
       });
	}
	
	public void displayInputKeyboard(View v, boolean hasFocus){
		//获取系统 IMM  
        InputMethodManager imm = (InputMethodManager)  
                getSystemService(Context.INPUT_METHOD_SERVICE);  
        if (!hasFocus) {  
            //隐藏 软键盘  
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
        } else {  
            //显示 软键盘  
            imm.showSoftInput(v, 0);  
        } 
	}
	public void hideInputKeyboard(){
		
		 et_user_name.clearFocus();//取消焦点
		 et_user_pw.clearFocus();
		 
  	     InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
         imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
	}
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		 args = new Bundle();
		// TODO Auto-generated method stub
		args.putString("key", "1");
		 UserLoad myLoad = new UserLoad(SetUserActivity.this, args);
	        return myLoad;  
	}
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.changeCursor(data);  
		
	}
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (db != null) db.close();
//		Utils.saveFile();//把软存中的数据存入磁盘中
		super.onDestroy();
		fixInputMethodManagerLeak(this); 
	}
    /****
     * 校验数据
     */
	private String checkData(){
		
		String tipStr = "";

		String st2Bit = et_user_name.getText().toString();
		String stproDt = et_user_pw.getText().toString();
		
		
		if(!StringUtils.isNotBlank(st2Bit)){
			tipStr=getString(R.string.text_user_err1);
			return tipStr;
		}
		if(!StringUtils.isNotBlank(stproDt)){
			tipStr=getString(R.string.text_user_err2);
			return tipStr;
		}
		
		return tipStr;
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
		
		 ContextMenuInfo menuInfo = (ContextMenuInfo) item.getMenuInfo();       
	     AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();   
	     int id = (int)info.id;//这里的info.id对应的就是数据库中_id的值  
	       
        String Temp="";
        switch (item.getItemId()) {
            case 1:
                Temp="删除";
                String whereClause="id=?";  
                String[] whereArgs={String.valueOf(id)};  
                db.delete(DatabaseHelper.TABLE_USER_MAIN, whereClause, whereArgs);  
                getLoaderManager().restartLoader(1, null, SetUserActivity.this);
                break;
            case 2:
            	this.modifyBlastBaseInfo(id);
                Temp="修改";
                break;
            default:
                break;
        }
		show_Toast(Temp+"处理");
        return super.onContextItemSelected(item);
    }
	/***
	 * 保存
	 */
	private void insertDenator(String name,String pw){
		
		if(checkRepeatUserName(name)==1){
			show_Toast("用户名: " +name+"重复");
		    return ;
		}
		
		ContentValues values = new ContentValues();
		values.put("uname", name);
		values.put("upassword", pw);
		values.put("isface", 0);
		values.put("facepath", "");
		//向数据库插入数据
		db.insert(DatabaseHelper.TABLE_USER_MAIN, null, values);
		getLoaderManager().restartLoader(1, null, SetUserActivity.this);
		  
		return ;
	}
	private void modifyBlastBaseInfo(int id){
		AlertDialog.Builder builder = new AlertDialog.Builder(SetUserActivity.this);
       // builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("修改密码信息");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(SetUserActivity.this).inflate(R.layout.usermodifydialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        
        final EditText username = (EditText)view.findViewById(R.id.username);
        final EditText password = (EditText)view.findViewById(R.id.password);
        username.setEnabled(false);
        String selection = "id = ?"; // 选择条件，给null查询所有  
        String[] selectionArgs = {id+""};//选择条件参数,会把选择条件中的？替换成这个数组中的值  
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER_MAIN, null, selection, selectionArgs, null, null, null);  
        if(cursor != null && cursor.moveToFirst()){ 
            String name = cursor.getString(1);  
            String age = cursor.getString(2);  
            username.setText(name);
            password.setText(age);
            cursor.close();
        }
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String a = username.getText().toString().trim();
                String b = password.getText().toString().trim();
                modifyPw(a,b);
                getLoaderManager().restartLoader(1, null, SetUserActivity.this);
                //    将输入的用户名和密码打印出来
				show_Toast("修改成功");
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
        });
        builder.show();
	}
	
	public int modifyPw(String name,String pw){
		ContentValues values = new ContentValues();	       		
   		values.put("upassword", pw);   				       		
   		db.update(DatabaseHelper.TABLE_USER_MAIN, values, "uname=?", new String[]{""+name});
        Utils.saveFile();//把软存中的数据存入磁盘中
   		return 1;
	}
	/**
	 * 检查重复的数据
	 * @param userName
	 * @return
	 */
	public int checkRepeatUserName(String userName){
		String selection = "uname = ?"; // 选择条件，给null查询所有  
		String[] selectionArgs = {userName+""};//选择条件参数,会把选择条件中的？替换成这个数组中的值  
		Cursor cursor = db.query(DatabaseHelper.TABLE_USER_MAIN, null, selection, selectionArgs, null, null, null);  
		if(cursor != null){  //cursor不位空,可以移动到第一行
			 boolean flag = cursor.moveToFirst();			 
		    cursor.close();
		    if(flag)
		    	return 1;
		    else
		    	return 0;
		}else{
			//if(cursor != null)cursor.close();
			return 0;
		}
	}
}
