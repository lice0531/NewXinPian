package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.os.Build;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.CustomSimpleCursorAdapter;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.Utils;

/**
 * 设置延时页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetDelayTime extends BaseActivity implements LoaderCallbacks<Cursor> {

    private CustomSimpleCursorAdapter adapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    //private ScrollView scrollview;
    private TextView startNoTxt;//起始序号
    private TextView endNoTxt;//终点序号
    private TextView holeDeAmoTxt;//孔内雷管数
    private TextView startDelayTxt;//开始延时
    private TextView holeinDelayTxt;//孔内延时
    private TextView holeBetweentTxt;//孔间延时
    private TextView deTotalTxt;//雷管总数
    private Button btn_return;
    private ListView setDelay_listView;
    private String selectDenatorId;
    private Button btn_OK;
    private Button btn_suidao;
    private int maxSecond = 0;//最大秒数
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler_loading = new Handler();//显示进度条


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_delay_time);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        getDenatorType();//获取最大延时
        startNoTxt = (TextView) findViewById(R.id.setDelayTime_FirstNo);
        endNoTxt = (TextView) findViewById(R.id.setDelayTime_EndNo);
        holeDeAmoTxt = (TextView) findViewById(R.id.setDelayTime_holedetonator);
        startDelayTxt = (TextView) findViewById(R.id.setDelayTime_startDelaytime);
        holeinDelayTxt = (TextView) findViewById(R.id.setDelayTime_holein_Delaytime);
        holeBetweentTxt = (TextView) findViewById(R.id.setDelayTime_holemiddle_Delaytime);

        deTotalTxt = (TextView) findViewById(R.id.setDelayTime_deAmount);

        //	this.scrollview = (ScrollView)findViewById(R.id.setDelayTimeMainPage);
        btn_return = (Button) findViewById(R.id.btn_setDelayTime_return);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
            }
        });
        btn_suidao = (Button) findViewById(R.id.btn_setDelayTime_suidao);
        btn_suidao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputKeyboard();
                Intent intent3 = new Intent(SetDelayTime.this, SetDelayTime_suidao.class);
                startActivity(intent3);
            }
        });
        btn_OK = (Button) findViewById(R.id.btn_setDelayTime_inputOK);
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInputKeyboard();

                String checstr = checkData();
                if (checstr == null || checstr.trim().length() < 1) {
                    int maxDelay = getComputerDenDelay();
                    Log.e("延时", "maxSecond: " + maxSecond);
                    if (maxSecond > 0 && maxSecond < maxDelay && maxSecond > 15000) {
                        show_Toast("当前设置延时已超出最大值限制,请重新设置延时");
                        return;
                    }
                    pb_show = 1;
                    runPbDialog();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setDenatorDelay();
                        }
                    }).start();
                    show_Toast(getString(R.string.text_error_tip36));
                } else {
                    show_Toast(checstr);
                }
            }
        });


        setDelay_listView = (ListView) this.findViewById(R.id.setDelayMainlistView);
        adapter = new CustomSimpleCursorAdapter(SetDelayTime.this,
                R.layout.item_delayset,
                null,
                new String[]{"blastserial", "sithole", "delay", "shellBlastNo"},
                new int[]{R.id.blastserial, R.id.sithole, R.id.setdelaytxt, R.id.shellBlastNo},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setDelay_listView.setAdapter(adapter);

        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where statusCode =?", new String[]{"02"});
        int totalNum = cursor.getCount();//得到数据的总条数
        if (cursor != null) cursor.close();

        cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " ", null);
        int serNum = cursor.getCount();//得到数据的总条数
        if (cursor != null) cursor.close();

        deTotalTxt.setText(getString(R.string.text_delay_total) + totalNum);//"雷管总数量："
        endNoTxt.setText("" + serNum);
        startNoTxt.setText("1");

        holeDeAmoTxt.setText("1");
        startDelayTxt.setText("10");
        holeinDelayTxt.setText("0");
        holeBetweentTxt.setText("10");

        getLoaderManager().initLoader(0, null, this);

        setDelay_listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int arg2, long arg3) {
                LinearLayout myte = (LinearLayout) view;
                TextView seralNoTxt = (TextView) myte.getChildAt(0);
                TextView holeTxt = (TextView) myte.getChildAt(1);
                TextView delayTxt = (TextView) myte.getChildAt(2);
                TextView denatorTxt = (TextView) myte.getChildAt(3);
                String serialNo = seralNoTxt.getText().toString().trim();
                String holeNo = holeTxt.getText().toString().trim();
                String denatorNo = denatorTxt.getText().toString().trim();
                String delaytime = delayTxt.getText().toString().trim();
                selectDenatorId = serialNo;
                modifyBlastBaseInfo(serialNo, holeNo, delaytime, denatorNo);//序号,孔号,延时,管壳码
                return false;
            }
        });


        mHandler_loading = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (pb_show == 1 && tipDlg != null) tipDlg.show();
                if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
                super.handleMessage(msg);
            }
        };

    }

    private int getDenatorMaxDelay() {
        Cursor cursor = db.rawQuery("Select max(delay) from denatorBaseinfo where statusCode =?", new String[]{"02"});
        int total = 0;//得到数据的总条数
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
            cursor.close();
        }
        if (cursor != null) cursor.close();

        return total;
    }

    /**
     * 获得设置中的最大延时
     */
    private void getDenatorType() {

        String selection = "isSelected = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"是"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOR_TYPE, null, selection, selectionArgs, null, null, null);
        String second = "0";
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getString(2).matches("\\d+")) {//判断是否是数字
                second = cursor.getString(2);
            }
            Log.e("延时", "second: " + second);
            cursor.close();
        }
        maxSecond = Integer.parseInt(second);//类型转换异常

    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(SetDelayTime.this);

        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
        divider.setBackgroundColor(Color.TRANSPARENT);
        Window window = getWindow();//最大化dialog
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);

        //tipDlg.setMessage("正在操作,请等待...").show();

        new Thread(new Runnable() {

            @Override
            public void run() {

                //mHandler_2
                mHandler_loading.sendMessage(mHandler_loading.obtainMessage());
                //builder.show();
                try {
                    while (pb_show == 1) {
                        Thread.sleep(100);
                    }
                    //builder.dismiss();
                    mHandler_loading.sendMessage(mHandler_loading.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void modifyBlastBaseInfo(String serialNo, String hoteNo, String delaytime, String denatorNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetDelayTime.this);
        // builder.setIcon(R.drawable.ic_launcher);
        //   builder.setTitle("修改延时信息");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(SetDelayTime.this).inflate(R.layout.delaymodifydialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        final EditText serialNoTxt = (EditText) view.findViewById(R.id.serialNo);
        final EditText denatorNoTxt = (EditText) view.findViewById(R.id.denatorNo);
        final EditText delaytimeTxt = (EditText) view.findViewById(R.id.delaytime);

        serialNoTxt.setEnabled(false);
        denatorNoTxt.setEnabled(false);

        serialNoTxt.setText(serialNo);
        denatorNoTxt.setText(denatorNo);
        delaytimeTxt.setText(delaytime);

        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String a = username.getText().toString().trim();
                String b = delaytimeTxt.getText().toString().trim();
                if (b == null || b.trim().length() < 1 || (maxSecond > 0 && Integer.parseInt(b) > maxSecond)) {
                    show_Toast(getString(R.string.text_error_tip37));
                } else {
                    modifyDelayTime(selectDenatorId, b);
                    getLoaderManager().restartLoader(1, null, SetDelayTime.this);
                    //    将输入的用户名和密码打印出来
                    show_Toast(getString(R.string.text_error_tip38));
                }
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public int isDel(String id) {

        Cursor cursor = db.rawQuery("Select * from denatorBaseinfo where statusCode =? and blastserial=?", new String[]{"02", id});

        if (cursor != null) {
            int total = cursor.getCount();//得到数据的总条数
            cursor.close();
            return total;
        }
        return 0;

    }

    public int modifyDelayTime(String id, String delay) {
        ContentValues values = new ContentValues();
        values.put("delay", delay);
        db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + id});
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        /***
         * 发送初始化命令
         */

        hideInputKeyboard();
        btn_return.setFocusable(true);
        btn_return.setFocusableInTouchMode(true);
        btn_return.requestFocus();
        btn_return.findFocus();
        int maxDelay = getDenatorMaxDelay();
        if (maxSecond > 0 && maxSecond < maxDelay) {
            show_Toast(getString(R.string.text_error_tip39));
        }

        super.onStart();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (db != null)
            db.close();
        if (tipDlg != null) {
            tipDlg.dismiss();
            tipDlg = null;
        }
        super.onDestroy();
        fixInputMethodManagerLeak(this);
    }

    public void hideInputKeyboard() {

        startNoTxt.clearFocus();//取消焦点
        endNoTxt.clearFocus();
        holeDeAmoTxt.clearFocus();
        startDelayTxt.clearFocus();
        holeinDelayTxt.clearFocus();
        holeBetweentTxt.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
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

    /**
     * 获取总延时值
     */
    private int getComputerDenDelay() {

        //起始序号
        String startNoStr = startNoTxt.getText().toString();
        //终点序号
        String endNoStr = endNoTxt.getText().toString();
        //孔内雷管数
        String holeDeAmoStr = holeDeAmoTxt.getText().toString();
        //开始延时
        String startDelayStr = startDelayTxt.getText().toString();
        //孔内延时
        String holeinDelayStr = holeinDelayTxt.getText().toString();
        //孔间延时
        String holeBetweentStr = holeBetweentTxt.getText().toString();

        int start = Integer.parseInt(startNoStr);
        int end = Integer.parseInt(endNoStr);
        int holeDeAmo = Integer.parseInt(holeDeAmoStr);
        int startDelay = Integer.parseInt(startDelayStr);
        int holeinDelay = Integer.parseInt(holeinDelayStr);
        int holeBetweent = Integer.parseInt(holeBetweentStr);
        int holeLoop = 1;
        int delayCount = startDelay;
        for (int iLoop = start; iLoop < end; iLoop++) {

            //int isExist = isDel(""+iLoop);

            for (int i = 1; i <= holeDeAmo; i++) {

                if (i < holeDeAmo) {
                    delayCount += holeinDelay;
                    iLoop++;
                }
                if (iLoop > end) break;
            }
            holeLoop++;
            delayCount += holeBetweent;
        }
        return delayCount;
    }

    private void setDenatorDelay() {

        //起始序号
        String startNoStr = startNoTxt.getText().toString();
        //终点序号
        String endNoStr = endNoTxt.getText().toString();
        //孔内雷管数
        String holeDeAmoStr = holeDeAmoTxt.getText().toString();
        //开始延时
        String startDelayStr = startDelayTxt.getText().toString();
        //孔内延时
        String holeinDelayStr = holeinDelayTxt.getText().toString();
        //孔间延时
        String holeBetweentStr = holeBetweentTxt.getText().toString();

        int start = Integer.parseInt(startNoStr);
        int end = Integer.parseInt(endNoStr);
        int holeDeAmo = Integer.parseInt(holeDeAmoStr);
        int startDelay = Integer.parseInt(startDelayStr);
        int holeinDelay = Integer.parseInt(holeinDelayStr);
        int holeBetweent = Integer.parseInt(holeBetweentStr);
        int holeLoop = 1;
        int delayCount = startDelay;
        for (int iLoop = start; iLoop <= end; iLoop++) {

            //int isExist = isDel(""+iLoop);

            for (int i = 1; i <= holeDeAmo; i++) {
                ContentValues values = new ContentValues();
//				values.put("sithole", holeLoop);
                values.put("delay", delayCount);

                db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{String.valueOf(iLoop)});
                //getLoaderManager().initLoader(0, null,this);
                if (i < holeDeAmo) {
                    delayCount += holeinDelay;
                    iLoop++;
                }
                if (iLoop > end) break;
            }
            holeLoop++;
            delayCount += holeBetweent;
        }
        pb_show = 0;
        mHandler_loading.sendMessage(mHandler_loading.obtainMessage());
        Utils.saveFile();//把软存中的数据存入磁盘中
        getLoaderManager().restartLoader(1, null, SetDelayTime.this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        args = new Bundle();
        // TODO Auto-generated method stub
        args.putString("key", "3");
        MyLoad myLoad = new MyLoad(SetDelayTime.this, args);
        return myLoad;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
        //System.out.print("111-"+System.currentTimeMillis());
        //Utility.setListViewHeightBasedOnChildren(setDelay_listView);
        //System.out.print("22"+System.currentTimeMillis());
        //dealWithListViewAndScrollViewTouch();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {


        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {

            return;

        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0); // 计算子项View 的宽高

            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        // setDelay_listView.getDividerHeight()获取子项间分隔符占用的高度

        // params.height最后得到整个ListView完整显示需要的高度

        listView.setLayoutParams(params);

    }

    /****
     * 校验数据
     */
    private String checkData() {

        String tipStr = "";

        //起始序号
        String startNo = startNoTxt.getText().toString();
        //终点序号
        String endNo = endNoTxt.getText().toString();
        //孔内雷管数
        String holeDeAmo = holeDeAmoTxt.getText().toString();
        //开始延时
        String startDelay = startDelayTxt.getText().toString();
        //孔内延时
        String holeinDelay = holeinDelayTxt.getText().toString();
        //孔间延时
        String holeBetweent = holeBetweentTxt.getText().toString();

        if (!Utils.isNum(startNo)) {
            tipStr = getString(R.string.text_error_tip25);//开始序号不是数字
            return tipStr;
        }
        if (!Utils.isNum(endNo)) {
            tipStr = getString(R.string.text_error_tip26);//"结束序号不是数字"
            return tipStr;
        }

        int start = Integer.parseInt(startNo);
        int end = Integer.parseInt(endNo);

        if (end < start) {
            tipStr = getString(R.string.text_error_tip27);//"结束序号不能小于开始序号";
            return tipStr;
        }

        if (start < 0 || end > 10000) {
            tipStr = getString(R.string.text_error_tip40);//"起始/结束序号不符合要求";
        }
        if (!Utils.isNum(holeDeAmo)) {
            tipStr = getString(R.string.text_error_tip41);//"孔内雷管数不是数字";
            return tipStr;
        }
        if (!Utils.isNum(startDelay)) {
            tipStr = getString(R.string.text_error_tip42);//"开始延时不是数字";
            return tipStr;
        }
        if (!Utils.isNum(holeinDelay)) {
            tipStr = getString(R.string.text_error_tip43);//"孔内延时不是数字";
            return tipStr;
        }
        if (!Utils.isNum(holeBetweent)) {
            tipStr = getString(R.string.text_error_tip44);//"孔间延时不是数字";
            return tipStr;
        }
        if (maxSecond <= 0) return tipStr;
        else {
            if (Integer.parseInt(startDelay) > maxSecond) {
                tipStr = getString(R.string.text_error_tip45);//"开始延时超出最大设定时间";
                return tipStr;
            }
            if (Integer.parseInt(holeinDelay) > maxSecond) {
                tipStr = getString(R.string.text_error_tip46);//"孔内延时超出最大设定时间";
                return tipStr;
            }
            if (Integer.parseInt(holeBetweent) > maxSecond) {
                tipStr = getString(R.string.text_error_tip47);//"孔间延时超出最大设定时间";
                return tipStr;
            }
        }
        return tipStr;
    }
}
