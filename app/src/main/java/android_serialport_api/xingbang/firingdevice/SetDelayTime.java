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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.CustomSimpleCursorAdapter;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置延时页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetDelayTime extends BaseActivity {
    @BindView(R.id.setDelayMainlistView)
    RecyclerView setDelay_listView;
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
    private String selectDenatorId;
    private Button btn_OK;
    private Button btn_suidao;
    private int maxSecond = 0;//最大秒数
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler_loading = new Handler();//显示进度条
    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private DetonatorAdapter_Paper<DenatorBaseinfo> mAdapter;
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private Handler mHandler_0 = new Handler();     // UI处理
    private String mOldTitle;   // 原标题
    private String mRegion;     // 区域
    private boolean mRegion1, mRegion2, mRegion3, mRegion4, mRegion5 = true;//是否选中区域1,2,3,4,5
    private TextView totalbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_delay_time);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        getDenatorType();//获取最大延时
        initView();


        initHandle();
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        Utils.writeRecord("---进入设置延时页面---");
    }

    private void initHandle() {
        mHandler_loading = new Handler(msg -> {
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });
        mHandler_0 = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1001:
                    Log.e("liyi_1001", "更新视图 区域" + mRegion);
                    Log.e("liyi_1001", "更新视图 雷管数量: " + mListData.size());
                    // 查询全部雷管 倒叙(序号)
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    StringBuilder a = new StringBuilder();
                    if (mRegion1) {
                        a.append("1");
                    }
                    if (mRegion2) {
                        a.append(",2");
                    }
                    if (mRegion3) {
                        a.append(",3");
                    }
                    if (mRegion4) {
                        a.append(",4");
                    }
                    if (mRegion5) {
                        a.append(",5");
                    }
                    // 设置标题区域
                    setTitleRegion(a.toString(), mListData.size());
                    // 显示提示

                    deTotalTxt.setText(getString(R.string.text_delay_total) + mListData.size());//"雷管总数量："
                    endNoTxt.setText("" + mListData.size());
                    break;

                // 重新排序 更新视图
                case 1002:
                    // 雷管孔号排序 并 重新查询
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    StringBuilder b = new StringBuilder();
                    if (mRegion1) {
                        b.append("1");
                    }
                    if (mRegion2) {
                        b.append(",2");
                    }
                    if (mRegion3) {
                        b.append(",3");
                    }
                    if (mRegion4) {
                        b.append(",4");
                    }
                    if (mRegion5) {
                        b.append(",5");
                    }
                    // 设置标题区域
                    setTitleRegion(b.toString(), mListData.size());
                    break;
                case 1005://按管壳码排序
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    Collections.sort(mListData);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 2001://按管壳码排序
                    show_Toast(msg.obj.toString());
                    break;

                default:
                    break;
            }

            return false;
        });


    }

    private void initView() {
        mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);
        mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);
        mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);
        mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);
        mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);

        totalbar_title = findViewById(R.id.title_text);
        totalbar_title.setText("删除");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setOnClickListener(v -> {
            choiceQuYu();
        });
        iv_back.setOnClickListener(v -> finish());

        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //         获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        // 原标题
        mOldTitle = getSupportActionBar().getTitle().toString();
        // 设置标题区域
        setTitleRegion(mRegion, -1);

        startNoTxt = (TextView) findViewById(R.id.setDelayTime_FirstNo);
        endNoTxt = (TextView) findViewById(R.id.setDelayTime_EndNo);
        holeDeAmoTxt = (TextView) findViewById(R.id.setDelayTime_holedetonator);
        startDelayTxt = (TextView) findViewById(R.id.setDelayTime_startDelaytime);
        holeinDelayTxt = (TextView) findViewById(R.id.setDelayTime_holein_Delaytime);
        holeBetweentTxt = (TextView) findViewById(R.id.setDelayTime_holemiddle_Delaytime);
        deTotalTxt = (TextView) findViewById(R.id.setDelayTime_deAmount);
        btn_return = (Button) findViewById(R.id.btn_setDelayTime_return);
        btn_return.setOnClickListener(v -> {
            Intent intentTemp = new Intent();
            intentTemp.putExtra("backString", "");
            setResult(1, intentTemp);
            finish();
        });
        btn_suidao = findViewById(R.id.btn_setDelayTime_suidao);
        btn_suidao.setOnClickListener(v -> {
            hideInputKeyboard();
            Intent intent3 = new Intent(SetDelayTime.this, SetDelayTime_suidao.class);
            startActivity(intent3);
            finish();
        });
        btn_OK = findViewById(R.id.btn_setDelayTime_inputOK);
        btn_OK.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(SetDelayTime.this)
                    .setTitle("是否修改延时")//设置对话框的标题//"成功起爆"
                    .setMessage("当前正在进行修改延时操作,请确认是否修改延时!")//设置对话框的内容"本次任务成功起爆！"
                    //设置对话框的按钮
                    .setNegativeButton("继续", (dialog13, which) -> {
                        dialog13.dismiss();

                        hideInputKeyboard();

                        String checstr = checkData();
                        if (checstr == null || checstr.trim().length() < 1) {
                            int maxDelay = getComputerDenDelay();
                            Log.e("延时1", "maxDelay: " + maxDelay);//9010
                            Log.e("延时2", "maxSecond: " + maxSecond);//5000
                            if (maxSecond > 0 && maxSecond < maxDelay) {
                                show_Toast("当前设置延时已超出最大值" + maxSecond + "限制,请重新设置延时");
                                return;
                            }
                            pb_show = 1;
                            runPbDialog();
                            new Thread(() -> setDenatorDelay()).start();
                            show_Toast(getString(R.string.text_error_tip36));
                        } else {
                            show_Toast(checstr);
                        }
                    })
                    .setNeutralButton("退出", (dialog2, which) -> {
                        dialog2.dismiss();
                        finish();
                    })
                    .create();
            dialog.show();


        });

        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new DetonatorAdapter_Paper<>(this, 4);
        setDelay_listView.setLayoutManager(linearLayoutManager);
        setDelay_listView.setAdapter(mAdapter);
        mAdapter.setOnItemLongClick(position -> {
            Log.e("长按", "mListData.size(): " + mListData.size());
            Log.e("长按", "position: " + position);
            DenatorBaseinfo info = mListData.get(position);

            int no = info.getBlastserial();
            int delay = info.getDelay();
            String shellBlastNo = info.getShellBlastNo();

            // 序号 延时 管壳码
            modifyBlastBaseInfo(no, delay, shellBlastNo);
        });

//        GreenDaoMaster master = new GreenDaoMaster();
//        List<DenatorBaseinfo> list = master.queryDetonatorRegionDesc(mRegion);
//        int totalNum = list.size();//得到数据的总条数
//        deTotalTxt.setText(getString(R.string.text_delay_total) + totalNum);//"雷管总数量："
//        endNoTxt.setText("" + totalNum);
        startNoTxt.setText("1");

        holeDeAmoTxt.setText("1");
        startDelayTxt.setText("10");
        holeinDelayTxt.setText("0");
        holeBetweentTxt.setText("10");


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
//        divider.setBackgroundColor(Color.TRANSPARENT);
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

    /**
     * 修改雷管延期 弹窗
     */
    private void modifyBlastBaseInfo(int no, int delay, final String shellBlastNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delaymodifydialog, null);
        builder.setView(view);

        EditText et_no = view.findViewById(R.id.serialNo);
        EditText et_shell = view.findViewById(R.id.denatorNo);
        EditText et_delay = view.findViewById(R.id.delaytime);

        et_no.setText(String.valueOf(no));
        et_delay.setText(String.valueOf(delay));
        et_shell.setText(shellBlastNo);
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("删除", (dialog, which) -> {
            dialog.dismiss();

            // TODO 开启进度条

            new Thread(() -> {
                // 删除某一发雷管
                new GreenDaoMaster().deleteDetonator(shellBlastNo);
                Utils.deleteData(mRegion);//重新排序雷管
                Utils.writeRecord("--删除雷管:" + shellBlastNo);
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1002));

            }).start();

        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            String delay1 = et_delay.getText().toString();
            Utils.writeRecord("-单发修改延时:" + "-管壳码:" + shellBlastNo + "-延时:" + delay1);
            if (maxSecond != 0 && Integer.parseInt(delay1) >= maxSecond) {
                mHandler_0.sendMessage(mHandler_0.obtainMessage(2001, "已达到最大延时限制" + maxSecond + "ms"));
            } else if (delay1.trim().length() < 1 || maxSecond > 0 && Integer.parseInt(delay1) > maxSecond) {
                mHandler_0.sendMessage(mHandler_0.obtainMessage(2001, "延时为空或大于最大设定延时，修改失败! "));
            } else {
                // 修改雷管延时
                new GreenDaoMaster().updateDetonatorDelay(shellBlastNo, Integer.parseInt(delay1));
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                show_Toast(shellBlastNo + "\n修改成功");
                Utils.saveFile();
            }
            dialog.dismiss();
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
    protected void onResume() {
        //         获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        Log.e("设置延时", "onResume: mHandler_0");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

                db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=? and piece =? ", new String[]{String.valueOf(iLoop), mRegion});
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
//        getLoaderManager().restartLoader(1, null, SetDelayTime.this);
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        Utils.writeRecord("--设置延时:起始序号:" + startNoStr + ",终点序号:" + endNoStr + ",孔内雷管数:" + holeDeAmoStr
                + ",开始延时:" + startDelayStr + ",孔内延时:" + holeinDelayStr + ",孔间延时:" + holeBetweentStr);
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


    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 打开菜单
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 点击item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mRegion = String.valueOf(item.getOrder());

        switch (item.getItemId()) {

            case R.id.item_1:
            case R.id.item_2:
            case R.id.item_3:
            case R.id.item_4:
            case R.id.item_5:
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                // 显示提示
                show_Toast("已选择 区域" + mRegion);
                // 延时选择重置
//                resetView();
//                delay_set = "0";
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * 设置标题区域
     */
    private void setTitleRegion(String region, int size) {

        String str = " 区域" + region;
        // 设置标题
        getSupportActionBar().setTitle(mOldTitle + str);
        // 保存区域参数
        totalbar_title.setText(mOldTitle + str);
        Log.e("liyi_Region", "已选择" + str);
    }

    private void choiceQuYu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("请选择区域");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choice_quyu, null);
        builder.setView(view);
        final CheckBox cb_mRegion1 = view.findViewById(R.id.dialog_cb_mRegion1);
        final CheckBox cb_mRegion2 = view.findViewById(R.id.dialog_cb_mRegion2);
        final CheckBox cb_mRegion3 = view.findViewById(R.id.dialog_cb_mRegion3);
        final CheckBox cb_mRegion4 = view.findViewById(R.id.dialog_cb_mRegion4);
        final CheckBox cb_mRegion5 = view.findViewById(R.id.dialog_cb_mRegion5);
        cb_mRegion1.setChecked(mRegion1);
        cb_mRegion2.setChecked(mRegion2);
        cb_mRegion3.setChecked(mRegion3);
        cb_mRegion4.setChecked(mRegion4);
        cb_mRegion5.setChecked(mRegion5);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {

            if (cb_mRegion1.isChecked() || cb_mRegion2.isChecked() || cb_mRegion3.isChecked() || cb_mRegion4.isChecked() || cb_mRegion5.isChecked()) {

                mRegion1 = cb_mRegion1.isChecked();
                mRegion2 = cb_mRegion2.isChecked();
                mRegion3 = cb_mRegion3.isChecked();
                mRegion4 = cb_mRegion4.isChecked();
                mRegion5 = cb_mRegion5.isChecked();

                MmkvUtils.savecode("mRegion1", mRegion1);
                MmkvUtils.savecode("mRegion2", mRegion2);
                MmkvUtils.savecode("mRegion3", mRegion3);
                MmkvUtils.savecode("mRegion4", mRegion4);
                MmkvUtils.savecode("mRegion5", mRegion5);

                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

            } else {
                show_Toast("请至少选择一个区域");
            }

        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }
}
