package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.CustomSimpleCursorAdapter;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.MlistView;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.SharedPreferencesHelper;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置延时页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetDelayTime_suidao extends BaseActivity implements LoaderCallbacks<Cursor> {

    @BindView(R.id.btn_setDelayTime_return)
    Button btn_return;
    @BindView(R.id.btn_setDelayTime_inputOK)
    Button btn_OK;
    @BindView(R.id.setDelayTime_startDelaytime_1)
    EditText setDelayTimeStartDelaytime1;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.et_duan_num_1)
    EditText etDuanNum1;
    @BindView(R.id.et_duan_total_1)
    EditText etDuanTotal1;
    @BindView(R.id.et_duan_delaytime_1)
    EditText etDuanDelaytime1;
    @BindView(R.id.et_duan_num_2)
    EditText etDuanNum2;
    @BindView(R.id.et_duan_total_2)
    EditText etDuanTotal2;
    @BindView(R.id.et_duan_delaytime_2)
    EditText etDuanDelaytime2;
    @BindView(R.id.et_duan_num_3)
    EditText etDuanNum3;
    @BindView(R.id.et_duan_total_3)
    EditText etDuanTotal3;
    @BindView(R.id.et_duan_delaytime_3)
    EditText etDuanDelaytime3;
    @BindView(R.id.et_duan_num_4)
    EditText etDuanNum4;
    @BindView(R.id.et_duan_total_4)
    EditText etDuanTotal4;
    @BindView(R.id.et_duan_delaytime_4)
    EditText etDuanDelaytime4;
    @BindView(R.id.et_duan_num_5)
    EditText etDuanNum5;
    @BindView(R.id.et_duan_total_5)
    EditText etDuanTotal5;
    @BindView(R.id.et_duan_delaytime_5)
    EditText etDuanDelaytime5;
    @BindView(R.id.et_duan_num_6)
    EditText etDuanNum6;
    @BindView(R.id.et_duan_total_6)
    EditText etDuanTotal6;
    @BindView(R.id.et_duan_delaytime_6)
    EditText etDuanDelaytime6;
    @BindView(R.id.et_duan_num_7)
    EditText etDuanNum7;
    @BindView(R.id.et_duan_total_7)
    EditText etDuanTotal7;
    @BindView(R.id.et_duan_delaytime_7)
    EditText etDuanDelaytime7;
    @BindView(R.id.et_duan_num_8)
    EditText etDuanNum8;
    @BindView(R.id.et_duan_total_8)
    EditText etDuanTotal8;
    @BindView(R.id.et_duan_delaytime_8)
    EditText etDuanDelaytime8;
    @BindView(R.id.et_duan_num_9)
    EditText etDuanNum9;
    @BindView(R.id.et_duan_total_9)
    EditText etDuanTotal9;
    @BindView(R.id.et_duan_delaytime_9)
    EditText etDuanDelaytime9;
    @BindView(R.id.et_duan_num_10)
    EditText etDuanNum10;
    @BindView(R.id.et_duan_total_10)
    EditText etDuanTotal10;
    @BindView(R.id.et_duan_delaytime_10)
    EditText etDuanDelaytime10;
    @BindView(R.id.et_duan_num_11)
    EditText etDuanNum11;
    @BindView(R.id.et_duan_total_11)
    EditText etDuanTotal11;
    @BindView(R.id.et_duan_delaytime_11)
    EditText etDuanDelaytime11;
    @BindView(R.id.et_duan_num_12)
    EditText etDuanNum12;
    @BindView(R.id.et_duan_total_12)
    EditText etDuanTotal12;
    @BindView(R.id.et_duan_delaytime_12)
    EditText etDuanDelaytime12;
    @BindView(R.id.et_duan_num_13)
    EditText etDuanNum13;
    @BindView(R.id.et_duan_total_13)
    EditText etDuanTotal13;
    @BindView(R.id.et_duan_delaytime_13)
    EditText etDuanDelaytime13;
    @BindView(R.id.et_duan_num_14)
    EditText etDuanNum14;
    @BindView(R.id.et_duan_total_14)
    EditText etDuanTotal14;
    @BindView(R.id.et_duan_delaytime_14)
    EditText etDuanDelaytime14;
    @BindView(R.id.et_duan_num_15)
    EditText etDuanNum15;
    @BindView(R.id.et_duan_total_15)
    EditText etDuanTotal15;
    @BindView(R.id.et_duan_delaytime_15)
    EditText etDuanDelaytime15;
    @BindView(R.id.et_duan_num_16)
    EditText etDuanNum16;
    @BindView(R.id.et_duan_total_16)
    EditText etDuanTotal16;
    @BindView(R.id.et_duan_delaytime_16)
    EditText etDuanDelaytime16;
    @BindView(R.id.et_duan_num_17)
    EditText etDuanNum17;
    @BindView(R.id.et_duan_total_17)
    EditText etDuanTotal17;
    @BindView(R.id.et_duan_delaytime_17)
    EditText etDuanDelaytime17;
    @BindView(R.id.et_duan_num_18)
    EditText etDuanNum18;
    @BindView(R.id.et_duan_total_18)
    EditText etDuanTotal18;
    @BindView(R.id.et_duan_delaytime_18)
    EditText etDuanDelaytime18;
    @BindView(R.id.et_duan_num_19)
    EditText etDuanNum19;
    @BindView(R.id.et_duan_total_19)
    EditText etDuanTotal19;
    @BindView(R.id.et_duan_delaytime_19)
    EditText etDuanDelaytime19;
    @BindView(R.id.et_duan_num_20)
    EditText etDuanNum20;
    @BindView(R.id.et_duan_total_20)
    EditText etDuanTotal20;
    @BindView(R.id.et_duan_delaytime_20)
    EditText etDuanDelaytime20;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.setDelayTime_FirstNo)//起始序号
            EditText startNoTxt;
    @BindView(R.id.setDelayTime_EndNo)//终点序号
            EditText endNoTxt;
    @BindView(R.id.setDelayTime_holedetonator)//孔内雷管数
            EditText holeDeAmoTxt;
    @BindView(R.id.setDelayTime_startDelaytime)//开始延时
            EditText startDelayTxt;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.setDelayTime_holein_Delaytime)//孔内延时
            EditText holeinDelayTxt;
    @BindView(R.id.setDelayTime_holemiddle_Delaytime)//孔间延时
            EditText holeBetweentTxt;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.ly_setUpdata)
    LinearLayout lySetUpdata;
    @BindView(R.id.setDelayTime_deAmount)//雷管总数
            TextView deTotalTxt;
    @BindView(R.id.setDelayTime_tipinfo_fragement)
    LinearLayout setDelayTimeTipinfoFragement;
    @BindView(R.id.setDelayMainlistView)
    MlistView setDelayMainlistView;
    @BindView(R.id.setDelayTimeMainPage)
    ScrollView setDelayTimeMainPage;
    private CustomSimpleCursorAdapter adapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    //private ScrollView scrollview;
    private String selectDenatorId;
    private int maxSecond = 0;//最大秒数
    private int totalNum = 0;//雷管总数
    private int dangqianNum = 1;//当前雷管开始序号
    private int totaldelay = 0;//最大秒数
    private Handler mHandler_2 = new Handler();//显示进度条
    private ProgressDialog builder = null;
    private LoadingDialog tipDlg = null;
    private int pb_show = 0;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_delay_time_suidao);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 22);
        db = mMyDatabaseHelper.getReadableDatabase();
        getDenatorType();//获取最大延时
        sharedPreferencesHelper = new SharedPreferencesHelper(this, getApplicationContext().getPackageName());
        adapter = new CustomSimpleCursorAdapter(SetDelayTime_suidao.this, R.layout.item_delayset,
                null, new String[]{"blastserial", "sithole", "delay", "shellBlastNo"},
                new int[]{R.id.blastserial, R.id.sithole, R.id.setdelaytxt, R.id.shellBlastNo},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setDelayMainlistView.setAdapter(adapter);

        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where statusCode =?", new String[]{"02"});
        int totalNum = cursor.getCount();//得到已注册数据的总条数
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
        setData();//设置延时
        getLoaderManager().initLoader(0, null, this);

        setDelayMainlistView.setOnItemLongClickListener(new OnItemLongClickListener() {
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
        mHandler_2 = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (pb_show == 1 && tipDlg != null) tipDlg.show();
                if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
                super.handleMessage(msg);
            }
        };
    }

    /**
     * 获取最大延时的
     */
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
            cursor.close();
        }
        maxSecond = Integer.parseInt(second);//类型转换异常

    }

    private void modifyBlastBaseInfo(String serialNo, String hoteNo, String delaytime, String denatorNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetDelayTime_suidao.this);
        // builder.setIcon(R.drawable.ic_launcher);
        //   builder.setTitle("修改延时信息");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(SetDelayTime_suidao.this).inflate(R.layout.delaymodifydialog, null);
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
                    getLoaderManager().restartLoader(1, null, SetDelayTime_suidao.this);
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
        if (db != null) db.close();
        Utils.saveFile();//把软存中的数据存入磁盘中
        totalNum = 0;
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
        getLoaderManager().restartLoader(1, null, SetDelayTime_suidao.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        args = new Bundle();
        // TODO Auto-generated method stub
        args.putString("key", "3");
        MyLoad myLoad = new MyLoad(SetDelayTime_suidao.this, args);
        return myLoad;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
        //System.out.print("111-"+System.currentTimeMillis());
        //Utility.setListViewHeightBasedOnChildren(listView);
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

        ViewGroup.LayoutParams params = setDelayMainlistView.getLayoutParams();

        params.height = totalHeight
                + (setDelayMainlistView.getDividerHeight() * (listAdapter.getCount() - 1));

        // setDelayMainlistView.getDividerHeight()获取子项间分隔符占用的高度

        // params.height最后得到整个ListView完整显示需要的高度

        setDelayMainlistView.setLayoutParams(params);

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

        if (Utils.isNum(startNo) == false) {
            tipStr = getString(R.string.text_error_tip25);//开始序号不是数字
            return tipStr;
        }
        if (Utils.isNum(endNo) == false) {
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
        if (Utils.isNum(holeDeAmo) == false) {
            tipStr = getString(R.string.text_error_tip41);//"孔内雷管数不是数字";
            return tipStr;
        }
        if (Utils.isNum(startDelay) == false) {
            tipStr = getString(R.string.text_error_tip42);//"开始延时不是数字";
            return tipStr;
        }
        if (Utils.isNum(holeinDelay) == false) {
            tipStr = getString(R.string.text_error_tip43);//"孔内延时不是数字";
            return tipStr;
        }
        if (Utils.isNum(holeBetweent) == false) {
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

    @OnClick({R.id.btn_setDelayTime_return, R.id.btn_setDelayTime_inputOK})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_setDelayTime_return:
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
                break;
            case R.id.btn_setDelayTime_inputOK:
                hideInputKeyboard();
                pb_show = 1;
                runPbDialog();
                saveData();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 1; i < 21; i++) {
                            setSuidaoDelayTime(i);
                            pb_show = 0;
                        }
                    }
                }).start();

                //进行清零
                if(setDelayTimeStartDelaytime1.getText().toString().equals("")){
                    totaldelay=0;
                    setDelayTimeStartDelaytime1.setText("0");
                }else {
                    totaldelay = Integer.parseInt(setDelayTimeStartDelaytime1.getText().toString());
                }
                totalNum = 0;
                dangqianNum = 1;
                getLoaderManager().restartLoader(3, null, SetDelayTime_suidao.this);
                adapter.notifyDataSetChanged();
                show_Toast("延时写入成功");
                break;
        }
    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(SetDelayTime_suidao.this);
        //tipDlg.setMessage("正在操作,请等待...").show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                //mHandler_2
                mHandler_2.sendMessage(mHandler_2.obtainMessage());
                //builder.show();
                try {
                    while (pb_show == 1) {
                        Thread.sleep(100);
                    }
                    //builder.dismiss();
                    mHandler_2.sendMessage(mHandler_2.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setSuidaoDelayTime(int i) {
        switch (i) {
            case 1:
                if (etDuanTotal1.getText().length() != 0 && Integer.parseInt(etDuanTotal1.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal1.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal1.getText().toString());
                    Log.e("延时", "totalNum: " + totalNum);
                    Log.e("延时", "dangqianNum: " + dangqianNum);
                    Log.e("延时", "totaldelay: " + totaldelay);
                    Log.e("延时", "-------------------------: ");
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime1.getText().toString());
                } else {
                    Log.e("延时", "数量为空: ");
                    Log.e("延时", "-------------------------: ");
                    break;
                }

                break;
            case 2:
                if (etDuanTotal2.getText().length() != 0 && Integer.parseInt(etDuanTotal2.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal2.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal2.getText().toString());
                    Log.e("延时", "totalNum: " + totalNum);
                    Log.e("延时", "dangqianNum: " + dangqianNum);
                    Log.e("延时", "totaldelay: " + totaldelay);
                    Log.e("延时", "-------------------------: ");
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime2.getText().toString());
                } else {
                    Log.e("延时", "数量为空: ");
                    Log.e("延时", "-------------------------: ");
                    break;
                }

                break;
            case 3:
                if (etDuanTotal3.getText().length() != 0 && Integer.parseInt(etDuanTotal3.getText().toString()) > 0) {
                    dangqianNum = totalNum;
                    totalNum = (totalNum + Integer.parseInt(etDuanTotal3.getText().toString()));
                    Log.e("延时", "totalNum: " + totalNum);
                    Log.e("延时", "dangqianNum: " + dangqianNum);
                    Log.e("延时", "totaldelay: " + totaldelay);
                    Log.e("延时", "-------------------------: ");
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime3.getText().toString());
                } else {
                    break;
                }

                break;
            case 4:
                if (etDuanTotal4.getText().length() != 0 && Integer.parseInt(etDuanTotal4.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal4.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal4.getText().toString());
                    Log.e("延时", "totalNum: " + totalNum);
                    Log.e("延时", "dangqianNum: " + dangqianNum);
                    Log.e("延时", "totaldelay: " + totaldelay);
                    Log.e("延时", "-------------------------: ");
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime4.getText().toString());
                } else {
                    break;
                }

                break;
            case 5:
                if (etDuanTotal5.getText().length() != 0 && Integer.parseInt(etDuanTotal5.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal5.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal5.getText().toString());
                    Log.e("延时", "totalNum: " + totalNum);
                    Log.e("延时", "dangqianNum: " + dangqianNum);
                    Log.e("延时", "totaldelay: " + totaldelay);
                    Log.e("延时", "-------------------------: ");
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime5.getText().toString());
                } else {
                    break;
                }

                break;
            case 6:
                if (etDuanTotal6.getText().length() != 0 && Integer.parseInt(etDuanTotal6.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal6.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal6.getText().toString());
                    Log.e("延时", "totalNum: " + totalNum);
                    Log.e("延时", "dangqianNum: " + dangqianNum);
                    Log.e("延时", "totaldelay: " + totaldelay);
                    Log.e("延时", "-------------------------: ");
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime6.getText().toString());
                } else {
//                    Log.e("判断数量是否为空6", "退出 ");
                    break;
                }

                break;
            case 7:
                if (etDuanTotal7.getText().length() != 0 && Integer.parseInt(etDuanTotal7.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal7.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal7.getText().toString());
                    Log.e("延时", "totalNum: " + totalNum);
                    Log.e("延时", "dangqianNum: " + dangqianNum);
                    Log.e("延时", "totaldelay: " + totaldelay);
                    Log.e("延时", "-------------------------: ");
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime7.getText().toString());
                } else {
//                    Log.e("判断数量是否为空7", "退出 ");
                    break;
                }

                break;
            case 8:
                if (etDuanTotal8.getText().length() != 0 && Integer.parseInt(etDuanTotal8.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal8.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal8.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime8.getText().toString());
                } else {
//                    Log.e("判断数量是否为空8", "退出 ");
                    break;
                }

                break;
            case 9:
                if (etDuanTotal9.getText().length() != 0 && Integer.parseInt(etDuanTotal9.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal9.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal9.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime9.getText().toString());
                } else {
//                    Log.e("判断数量是否为空9", "退出 ");
                    break;
                }

                break;
            case 10:
                if (etDuanTotal10.getText().length() != 0 && Integer.parseInt(etDuanTotal10.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal10.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal10.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime10.getText().toString());
                } else {
//                    Log.e("判断数量是否为空10", "退出 ");
                    break;
                }

                break;
            case 11:
                if (etDuanTotal11.getText().length() != 0 && Integer.parseInt(etDuanTotal11.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal11.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal11.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime11.getText().toString());
                } else {
//                    Log.e("判断数量是否为空11", "退出 ");
                    break;
                }

                break;
            case 12:
                if (etDuanTotal12.getText().length() != 0 && Integer.parseInt(etDuanTotal12.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal12.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal12.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime12.getText().toString());
                } else {
//                    Log.e("判断数量是否为空12", "退出 ");
                    break;
                }

                break;
            case 13:
                if (etDuanTotal13.getText().length() != 0 && Integer.parseInt(etDuanTotal13.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal13.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal13.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime13.getText().toString());
                } else {
//                    Log.e("判断数量是否为空13", "退出 ");
                    break;
                }

                break;
            case 14:
                if (etDuanTotal14.getText().length() != 0 && Integer.parseInt(etDuanTotal14.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal14.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal14.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime14.getText().toString());
                } else {
//                    Log.e("判断数量是否为空14", "退出 ");
                    break;
                }

                break;
            case 15:
                if (etDuanTotal15.getText().length() != 0 && Integer.parseInt(etDuanTotal15.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal15.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal15.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime15.getText().toString());
                } else {
//                    Log.e("判断数量是否为空15", "退出 ");
                    break;
                }

                break;
            case 16:
                if (etDuanTotal16.getText().length() != 0 && Integer.parseInt(etDuanTotal16.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal16.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal16.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime16.getText().toString());
                } else {
//                    Log.e("判断数量是否为空16", "退出 ");
                    break;
                }

                break;
            case 17:
                if (etDuanTotal17.getText().length() != 0 && Integer.parseInt(etDuanTotal17.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal17.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal17.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime17.getText().toString());
                } else {
//                    Log.e("判断数量是否为空17", "退出 ");
                    break;
                }

                break;
            case 18:
                if (etDuanTotal18.getText().length() != 0 && Integer.parseInt(etDuanTotal18.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal18.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal18.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime18.getText().toString());
                } else {
//                    Log.e("判断数量是否为空18", "退出 ");
                    break;
                }


                break;
            case 19:
                if (etDuanTotal19.getText().length() != 0 && Integer.parseInt(etDuanTotal19.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal19.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal19.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime19.getText().toString());
                } else {
//                    Log.e("判断数量是否为空19", "退出 ");
                    break;
                }


                break;
            case 20:
                if (etDuanTotal20.getText().length() != 0 && Integer.parseInt(etDuanTotal20.getText().toString()) > 0) {
                    totalNum = totalNum + Integer.parseInt(etDuanTotal20.getText().toString());
                    dangqianNum = totalNum - Integer.parseInt(etDuanTotal20.getText().toString());
                    setSuidaoDelay(dangqianNum, totalNum, totaldelay);
                    //设置完延时增加延时间隔
                    totaldelay = totaldelay + Integer.parseInt(etDuanDelaytime20.getText().toString());
                } else {
//                    Log.e("判断数量是否为空20", "退出 ");
                    break;
                }

                break;
        }

    }

    private void setSuidaoDelay(int dangqianNum, int totalNum, int totaldelay) {
        int start = dangqianNum + 1;//起始序号
        int end = totalNum;//终点序号
        for (int i = start; i <= end; i++) {
            ContentValues values = new ContentValues();
            values.put("delay", totaldelay);
            db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{String.valueOf(i)});
        }
        getLoaderManager().restartLoader(1, null, SetDelayTime_suidao.this);
    }

    //保存当前隧道设置
    private void saveData() {
        if(etDuanDelaytime1.length()<1){
            etDuanDelaytime1.setText("0");
        }
        if(etDuanTotal1.length()<1){
            etDuanTotal1.setText("0");
        }

        if(etDuanDelaytime2.length()<1){
            etDuanDelaytime2.setText("0");
        }
        if(etDuanTotal2.length()<1){
            etDuanTotal2.setText("0");
        }
        if(etDuanDelaytime3.length()<1){
            etDuanDelaytime3.setText("0");
        }
        if(etDuanTotal3.length()<1){
            etDuanTotal3.setText("0");
        }
        if(etDuanDelaytime4.length()<1){
            etDuanDelaytime4.setText("0");
        }
        if(etDuanTotal4.length()<1){
            etDuanTotal4.setText("0");
        }
        if(etDuanDelaytime5.length()<1){
            etDuanDelaytime5.setText("0");
        }
        if(etDuanTotal5.length()<1){
            etDuanTotal5.setText("0");
        }
        if(etDuanDelaytime6.length()<1){
            etDuanDelaytime6.setText("0");
        }
        if(etDuanTotal6.length()<1){
            etDuanTotal6.setText("0");
        }
        if(etDuanDelaytime7.length()<1){
            etDuanDelaytime7.setText("0");
        }
        if(etDuanTotal7.length()<1){
            etDuanTotal7.setText("0");
        }
        if(etDuanDelaytime8.length()<1){
            etDuanDelaytime8.setText("0");
        }
        if(etDuanTotal8.length()<1){
            etDuanTotal8.setText("0");
        }
        if(etDuanDelaytime9.length()<1){
            etDuanDelaytime9.setText("0");
        }
        if(etDuanTotal9.length()<1){
            etDuanTotal9.setText("0");
        }
        if(etDuanDelaytime10.length()<1){
            etDuanDelaytime10.setText("0");
        }
        if(etDuanTotal10.length()<1){
            etDuanTotal10.setText("0");
        }
        if(etDuanDelaytime11.length()<1){
            etDuanDelaytime11.setText("0");
        }
        if(etDuanTotal11.length()<1){
            etDuanTotal11.setText("0");
        }
        if(etDuanDelaytime12.length()<1){
            etDuanDelaytime12.setText("0");
        }
        if(etDuanTotal12.length()<1){
            etDuanTotal12.setText("0");
        }
        if(etDuanDelaytime13.length()<1){
            etDuanDelaytime13.setText("0");
        }
        if(etDuanTotal13.length()<1){
            etDuanTotal13.setText("0");
        }
        if(etDuanDelaytime14.length()<1){
            etDuanDelaytime14.setText("0");
        }
        if(etDuanTotal14.length()<1){
            etDuanTotal14.setText("0");
        }
        if(etDuanDelaytime15.length()<1){
            etDuanDelaytime15.setText("0");
        }
        if(etDuanTotal15.length()<1){
            etDuanTotal15.setText("0");
        }
        if(etDuanDelaytime16.length()<1){
            etDuanDelaytime16.setText("0");
        }
        if(etDuanTotal16.length()<1){
            etDuanTotal16.setText("0");
        }
        if(etDuanDelaytime17.length()<1){
            etDuanDelaytime17.setText("0");
        }
        if(etDuanTotal17.length()<1){
            etDuanTotal17.setText("0");
        }
        if(etDuanDelaytime18.length()<1){
            etDuanDelaytime18.setText("0");
        }
        if(etDuanTotal18.length()<1){
            etDuanTotal18.setText("0");
        }
        if(etDuanDelaytime19.length()<1){
            etDuanDelaytime19.setText("0");
        }
        if(etDuanTotal19.length()<1){
            etDuanTotal19.setText("0");
        }
        if(etDuanDelaytime20.length()<1){
            etDuanDelaytime20.setText("0");
        }
        if(etDuanTotal20.length()<1){
            etDuanTotal20.setText("0");
        }
        sharedPreferencesHelper.put("setDelayTimeStartDelaytime1", setDelayTimeStartDelaytime1.getText().toString());//开始时间

        sharedPreferencesHelper.put("etDuanDelaytime1", etDuanDelaytime1.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal1", etDuanTotal1.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime2", etDuanDelaytime2.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal2", etDuanTotal2.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime3", etDuanDelaytime3.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal3", etDuanTotal3.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime4", etDuanDelaytime4.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal4", etDuanTotal4.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime5", etDuanDelaytime5.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal5", etDuanTotal5.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime6", etDuanDelaytime6.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal6", etDuanTotal6.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime7", etDuanDelaytime7.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal7", etDuanTotal7.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime8", etDuanDelaytime8.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal8", etDuanTotal8.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime9", etDuanDelaytime9.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal9", etDuanTotal9.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime10", etDuanDelaytime10.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal10", etDuanTotal10.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime11", etDuanDelaytime11.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal11", etDuanTotal11.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime12", etDuanDelaytime12.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal12", etDuanTotal12.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime13", etDuanDelaytime13.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal13", etDuanTotal13.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime14", etDuanDelaytime14.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal14", etDuanTotal14.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime15", etDuanDelaytime15.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal15", etDuanTotal15.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime16", etDuanDelaytime16.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal16", etDuanTotal16.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime17", etDuanDelaytime17.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal17", etDuanTotal17.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime18", etDuanDelaytime18.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal18", etDuanTotal18.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime19", etDuanDelaytime19.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal19", etDuanTotal19.getText().toString());//段中雷管数

        sharedPreferencesHelper.put("etDuanDelaytime20", etDuanDelaytime20.getText().toString());//间隔时间
        sharedPreferencesHelper.put("etDuanTotal20", etDuanTotal20.getText().toString());//段中雷管数

    }

    private void setData() {
        setDelayTimeStartDelaytime1.setText(sharedPreferencesHelper.getString("setDelayTimeStartDelaytime1", "10"));//开始时间

        etDuanDelaytime1.setText(sharedPreferencesHelper.getString("etDuanDelaytime1", "0"));//间隔时间
        etDuanTotal1.setText(sharedPreferencesHelper.getString("etDuanTotal1", "0"));//段中雷管数

        etDuanDelaytime2.setText(sharedPreferencesHelper.getString("etDuanDelaytime2", "0"));//间隔时间
        etDuanTotal2.setText(sharedPreferencesHelper.getString("etDuanTotal2", "0"));//段中雷管数

        etDuanDelaytime3.setText(sharedPreferencesHelper.getString("etDuanDelaytime3", "0"));//间隔时间
        etDuanTotal3.setText(sharedPreferencesHelper.getString("etDuanTotal3", "0"));//段中雷管数

        etDuanDelaytime4.setText(sharedPreferencesHelper.getString("etDuanDelaytime4", "0"));//间隔时间
        etDuanTotal4.setText(sharedPreferencesHelper.getString("etDuanTotal4", "0"));//段中雷管数

        etDuanDelaytime5.setText(sharedPreferencesHelper.getString("etDuanDelaytime5", "0"));//间隔时间
        etDuanTotal5.setText(sharedPreferencesHelper.getString("etDuanTotal5", "0"));//段中雷管数

        etDuanDelaytime6.setText(sharedPreferencesHelper.getString("etDuanDelaytime6", "0"));//间隔时间
        etDuanTotal6.setText(sharedPreferencesHelper.getString("etDuanTotal6", "0"));//段中雷管数

        etDuanDelaytime7.setText(sharedPreferencesHelper.getString("etDuanDelaytime7", "0"));//间隔时间
        etDuanTotal7.setText(sharedPreferencesHelper.getString("etDuanTotal7", "0"));//段中雷管数

        etDuanDelaytime8.setText(sharedPreferencesHelper.getString("etDuanDelaytime8", "0"));//间隔时间
        etDuanTotal8.setText(sharedPreferencesHelper.getString("etDuanTotal8", "0"));//段中雷管数

        etDuanDelaytime9.setText(sharedPreferencesHelper.getString("etDuanDelaytime9", "0"));//间隔时间
        etDuanTotal9.setText(sharedPreferencesHelper.getString("etDuanTotal9", "0"));//段中雷管数

        etDuanDelaytime10.setText(sharedPreferencesHelper.getString("etDuanDelaytime10", "0"));//间隔时间
        etDuanTotal10.setText(sharedPreferencesHelper.getString("etDuanTotal10", "0"));//段中雷管数

        etDuanDelaytime11.setText(sharedPreferencesHelper.getString("etDuanDelaytime11", "0"));//间隔时间
        etDuanTotal11.setText(sharedPreferencesHelper.getString("etDuanTotal11", "0"));//段中雷管数

        etDuanDelaytime12.setText(sharedPreferencesHelper.getString("etDuanDelaytime12", "0"));//间隔时间
        etDuanTotal12.setText(sharedPreferencesHelper.getString("etDuanTotal12", "0"));//段中雷管数

        etDuanDelaytime13.setText(sharedPreferencesHelper.getString("etDuanDelaytime13", "0"));//间隔时间
        etDuanTotal13.setText(sharedPreferencesHelper.getString("etDuanTotal13", "0"));//段中雷管数

        etDuanDelaytime14.setText(sharedPreferencesHelper.getString("etDuanDelaytime14", "0"));//间隔时间
        etDuanTotal14.setText(sharedPreferencesHelper.getString("etDuanTotal14", "0"));//段中雷管数

        etDuanDelaytime15.setText(sharedPreferencesHelper.getString("etDuanDelaytime15", "0"));//间隔时间
        etDuanTotal15.setText(sharedPreferencesHelper.getString("etDuanTotal15", "0"));//段中雷管数

        etDuanDelaytime16.setText(sharedPreferencesHelper.getString("etDuanDelaytime16", "0"));//间隔时间
        etDuanTotal16.setText(sharedPreferencesHelper.getString("etDuanTotal16", "0"));//段中雷管数

        etDuanDelaytime17.setText(sharedPreferencesHelper.getString("etDuanDelaytime17", "0"));//间隔时间
        etDuanTotal17.setText(sharedPreferencesHelper.getString("etDuanTotal17", "0"));//段中雷管数

        etDuanDelaytime18.setText(sharedPreferencesHelper.getString("etDuanDelaytime18", "0"));//间隔时间
        etDuanTotal18.setText(sharedPreferencesHelper.getString("etDuanTotal18", "0"));//段中雷管数

        etDuanDelaytime19.setText(sharedPreferencesHelper.getString("etDuanDelaytime19", "0"));//间隔时间
        etDuanTotal19.setText(sharedPreferencesHelper.getString("etDuanTotal19", "0"));//段中雷管数

        etDuanDelaytime20.setText(sharedPreferencesHelper.getString("etDuanDelaytime20", "0"));//间隔时间
        etDuanTotal20.setText(sharedPreferencesHelper.getString("etDuanTotal20", "0"));//段中雷管数

    }
}
