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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;

import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.Utils;

/**
 * @author zenghp
 * 雷管芯片
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetDenatorTypeActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

    public static final Uri uri = Uri.parse("content://android_serialport_api.xingbang.Defactory");
    private SimpleCursorAdapter adapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private Button btn_return;
    private Button btn_inputOk;
    private EditText denator_type_Name;
    private EditText denator_type_second;
    private CheckBox et_factory_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_denatort_ype_page);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        ListView listView = this.findViewById(R.id.factory_listView);
        adapter = new SimpleCursorAdapter(SetDenatorTypeActivity.this, R.layout.denator_type_item,
                null, new String[]{"deTypeName", "deTypeSecond", "isSelected"}, new int[]{R.id.de_Type_Name, R.id.de_Type_second, R.id.de_Type_isSelected},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //adapter.
        listView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);

        denator_type_Name = this.findViewById(R.id.denator_type_Name);
        denator_type_Name.setOnFocusChangeListener((v, hasFocus) -> displayInputKeyboard(v, hasFocus));
        denator_type_second = this.findViewById(R.id.denator_type_second);
        denator_type_second.setOnFocusChangeListener((v, hasFocus) -> displayInputKeyboard(v, hasFocus));

        et_factory_selected = this.findViewById(R.id.denator_type_isSelected);

        btn_return = findViewById(R.id.btn_factory_return);
        btn_return.setOnClickListener(v -> {
            Intent intentTemp = new Intent();
            intentTemp.putExtra("backString", "");
            setResult(1, intentTemp);
            finish();
        });

        btn_inputOk = (Button) findViewById(R.id.btn_factory_inputOk);
        btn_inputOk.setOnClickListener(v -> {
            hideInputKeyboard();
            String checstr = checkData();
            if (checstr == null || checstr.trim().length() < 1) {
                String facname = denator_type_Name.getText().toString();
                String faccode = denator_type_second.getText().toString();
                String facselected = "否";
                if (et_factory_selected.isChecked()) {
                    facselected = "是";
                }
                int reVal = inserDenatorType(facname, faccode, facselected);
                if (reVal == 0)
                    show_Toast(getString(R.string.text_fac_tip1));
            } else {
                show_Toast(checstr);
            }
            getLoaderManager().restartLoader(1, null, SetDenatorTypeActivity.this);

        });

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
                menu.add(0, 1, 3, getString(R.string.text_tip_delete));//删除
                menu.add(0, 2, 2, getString(R.string.text_tip_modify));//"修改"
                //设置第三个参数反向  所以出现的菜单是反着的

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO Auto-generated method stub
        args = new Bundle();
        args.putString("key", "4");
        MyLoad myLoad = new MyLoad(SetDenatorTypeActivity.this, args);
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
        if (db != null)
            db.close();
//		Utils.saveFile();//把软存中的数据存入磁盘中

        super.onDestroy();
    }

    public void displayInputKeyboard(View v, boolean hasFocus) {
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

    public void hideInputKeyboard() {

        denator_type_Name.clearFocus();//取消焦点
        denator_type_second.clearFocus();
        et_factory_selected.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /***
     * 保存
     */
    private int inserDenatorType(String name, String code, String selected) {
        if ("1".equals(selected)) {
            if (checkRepeatSelected(code, selected) == 1) {
                show_Toast(getString(R.string.text_lgxp_default) + name + getString(R.string.text_fac_tip2));//
                return 1;
            }
        }
        ContentValues values = new ContentValues();
        values.put("deTypeName", name);
        values.put("deTypeSecond", code);
        values.put("isSelected", selected);
        //向数据库插入数据
        db.insert(DatabaseHelper.TABLE_NAME_DENATOR_TYPE, null, values);
        getLoaderManager().restartLoader(1, null, SetDenatorTypeActivity.this);
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 0;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        ContextMenuInfo menuInfo = (ContextMenuInfo) item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) info.id;//这里的info.id对应的就是数据库中_id的值

        String Temp = "";
        switch (item.getItemId()) {
            case 1:
                Temp = getString(R.string.text_tip_delete);//"删除";
                String whereClause = "id=?";
                String[] whereArgs = {String.valueOf(id)};
                db.delete(DatabaseHelper.TABLE_NAME_DENATOR_TYPE, whereClause, whereArgs);
                getLoaderManager().restartLoader(1, null, SetDenatorTypeActivity.this);
                break;
            case 2:
                this.modifyFactoryInfo(id);
                Temp = getString(R.string.text_tip_modify);//"修改";
                break;
            default:
                break;
        }
        show_Toast(Temp + getString(R.string.text_tip_handle));
        return super.onContextItemSelected(item);
    }

    private void modifyFactoryInfo(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetDenatorTypeActivity.this);
        // builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getString(R.string.text_alert_xiugai));//"修改信息"
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(SetDenatorTypeActivity.this).inflate(R.layout.denator_type_modifydialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        final EditText factoryname = (EditText) view.findViewById(R.id.item_denator_type_Name);
        final EditText factorycode = (EditText) view.findViewById(R.id.item_denator_type_second);
        final CheckBox factoryselected = (CheckBox) view.findViewById(R.id.denator_type_isSelected);
        final String typeId = "" + id;
        //factorycode.setEnabled(false);
        String selection = "id = ?"; // 选择条件，给null查询所有  
        String[] selectionArgs = {id + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOR_TYPE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(1);
            String code = cursor.getString(2);
            String selected = cursor.getString(3);
            factoryname.setText(name);
            factorycode.setText(code);
            if ("是".equals(selected)) {
                factoryselected.setChecked(true);
            }
            // factoryselected.setText(selected);
            cursor.close();
        }
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String a = factoryname.getText().toString().trim();
                String b = factorycode.getText().toString().trim();
                String d = "否";
                if (factoryselected.isChecked()) {
                    d = "是";
                }

                modifyDenatorType(typeId, a, b, d);
                getLoaderManager().restartLoader(1, null, SetDenatorTypeActivity.this);
                //    将输入的用户名和密码打印出来
                show_Toast(getString(R.string.text_error_tip38));
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    /**
     * 检查重复的数据
     *
     * @param //shellBlastNo
     * @return
     */
    public int checkRepeatCode(String deEntCode) {
        String selection = "deEntCode = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {deEntCode + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DEFACTORY, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            boolean flag = cursor.moveToFirst();
            cursor.close();
            if (flag)
                return 1;
            else
                return 0;
        } else {
            //if(cursor != null)cursor.close();
            return 0;
        }
    }

    public int checkRepeatSelected(String deEntCode, String selected) {
        String selection = "deEntCode = ? and isSelected =?"; // 选择条件，给null查询所有
        String[] selectionArgs = {deEntCode + "", selected + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DEFACTORY, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            boolean flag = cursor.moveToFirst();
            cursor.close();
            if (flag)
                return 1;
            else
                return 0;
        } else {
            //if(cursor != null)cursor.close();
            return 0;
        }
    }

    public int modifyDenatorType(String id, String name, String code, String selected) {
        if ("1".equals(selected)) {
            ContentValues val = new ContentValues();
            val.put("isSelected", "0");//key为字段名，value为值
            db.update(DatabaseHelper.TABLE_NAME_DENATOR_TYPE, val, null, null);
        }
        ContentValues values = new ContentValues();
        values.put("deTypeName", name);  //雷管类型名称
        values.put("deTypeSecond", code); //最大延期值
        values.put("isSelected", selected); //是否选择
        db.update(DatabaseHelper.TABLE_NAME_DENATOR_TYPE, values, "id=?", new String[]{"" + id});
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }

    private String checkData() {
        String tipStr = "";
        String st2Bit = denator_type_Name.getText().toString();
        String stproDt = denator_type_second.getText().toString();
        if (StringUtils.isNotBlank(st2Bit) == false) {
            tipStr = getString(R.string.text_error_tip60);//"类别名不能为空";
            return tipStr;
        }
        if (StringUtils.isNotBlank(stproDt) == false) {
            tipStr = getString(R.string.text_error_tip61);//"秒数不能为空";
            return tipStr;
        }
        return tipStr;
    }
}
