package android_serialport_api.xingbang.firingdevice;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.LoadAdapter;
import android_serialport_api.xingbang.custom.LoadListView;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 删除页面
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DelDenatorMainPage extends BaseActivity  {

    @BindView(R.id.btn_all_del)
    Button btnAllDel;
    @BindView(R.id.btn_error_del)
    Button btnErrorDel;
    @BindView(R.id.btn_serialNo_del)
    Button btnSerialNoDel;
    @BindView(R.id.btn_del_return)
    Button btnDelReturn;
    @BindView(R.id.setDelayTime_FirstNo)//起始序号
    EditText setDelayTimeFirstNo;
    @BindView(R.id.setDelayTime_EndNo)//终点序号
    EditText setDelayTimeEndNo;
    @BindView(R.id.denator_del_func)
    LinearLayout denatorDelFunc;
    @BindView(R.id.denator_del_listview)
    RecyclerView denatorDelListview;
    @BindView(R.id.denator_del_mainpage)
    LinearLayout denatorDelMainpage;
    private DatabaseHelper mMyDatabaseHelper;
    private List<DenatorBaseinfo> list_lg = new ArrayList<>();
    private SQLiteDatabase db;
    private int totalPage;//总的页数
    private int currentPage = 1;//当前页数
    private String selectDenatorSerialNo;//选中的雷管
    private boolean isBottom = false;
//    private LoadAdapter mAdapter;
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private String TAG = "删除页面";

    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private DetonatorAdapter_Paper<DenatorBaseinfo> mAdapter;
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private Handler mHandler_0 = new Handler();     // UI处理
    private String mOldTitle;   // 原标题
    private String mRegion;     // 区域
    private boolean switchUid = true;//切换uid/管壳码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_denator_time);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //点击其他地方隐藏输入框
        //db实例化
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        loadMoreData();//获取数据保存到list
        initView();
        initHandle();
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
    }

    private void initView() {
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
//         获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        // 原标题
        mOldTitle = getSupportActionBar().getTitle().toString();
        // 设置标题区域
        setTitleRegion(mRegion, -1);
        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new DetonatorAdapter_Paper<>(this, 5);
        denatorDelListview.setLayoutManager(linearLayoutManager);
        denatorDelListview.setAdapter(mAdapter);
    }

    private void initHandle() {
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
                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());
                    break;

                // 重新排序 更新视图
                case 1002:
                    // 雷管孔号排序 并 重新查询
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;

                case 1005://按管壳码排序
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    Collections.sort(mListData);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;


                default:
                    break;
            }

            return false;
        });
    }


    public void tipALLDelDenator() {
        Builder builder = new Builder(DelDenatorMainPage.this);
        builder.setTitle(getString(R.string.text_alert_tip));//"提示"
        builder.setMessage(getString(R.string.text_alert_del_all));//是否全部删除注册雷管数
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GreenDaoMaster master = new GreenDaoMaster();
                Log.e(TAG, "全部删除:mRegion "+mRegion );
                master.deleteLeiGuanFroPiace(mRegion);
//                db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null);
                db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO_ALL, null, null);
                refreshData();
                dialog.dismiss();
                chongZhiFan();//重置所有翻转标记
                Utils.saveFile();//把软存中的数据存入磁盘中
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
            }
        });
        builder.setNeutralButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void Del_Err_Denator() {
        Builder builder = new Builder(DelDenatorMainPage.this);
        builder.setTitle(getString(R.string.text_alert_tip));//"提示"
        builder.setMessage("是否删除错误雷管");//是否全部删除注册雷管数
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                GreenDaoMaster master = new GreenDaoMaster();
                master.deleteErrLeiGuan(mRegion);
                deleteListErrorDel();

                Utils.deleteData(mRegion);//重新排序雷管
                loadMoreData();//获取数据保存到list

                refreshData();
                dialog.dismiss();
                Utils.saveFile();//把软存中的数据存入磁盘中
            }
        });
        builder.setNeutralButton(getString(R.string.text_alert_cancel), (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    /**
     * 读取数据库中的数据
     */
    private void loadMoreData() {
//        list_lg.clear();
        list_lg = Application.getDaoSession().getDenatorBaseinfoDao().loadAll();
    }

    private void refreshData() {
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
    }

    /**
     * 按序号删除list集合中的管壳码
     */
    private void deleteListSerialNoDel(int start, int end) {
        if (list_lg != null && list_lg.size() > 0) {
            List<DenatorBaseinfo> newlist = new ArrayList<>();
            for (DenatorBaseinfo vo : list_lg) {
                if (vo.getBlastserial() >= start && vo.getBlastserial() <= end) {
                    newlist.add(vo);
                }
            }
            list_lg.remove(newlist);
            for (int i = 0; i < newlist.size(); i++) {
                list_lg.remove(newlist.get(i));
            }
        }
    }

    /**
     * 按序号删除list集合中的管壳码
     */
    private void deleteListSerialNoDel(String serialNo) {
        if (list_lg != null && list_lg.size() > 0) {
            List<DenatorBaseinfo> newlist = new ArrayList<>();
            for (DenatorBaseinfo vo : list_lg) {//遍历集合,得到含有该管壳码的元素集合
                if (vo.getShellBlastNo().equals(serialNo)) {
                    newlist.add(vo);
                }
            }
            list_lg.remove(newlist);
        }
    }

    /**
     * 删除列表中的错误雷管
     */
    private void deleteListErrorDel() {
        if (list_lg != null && list_lg.size() > 0) {
            List<DenatorBaseinfo> newlist = new ArrayList<>();
            for (DenatorBaseinfo vo : list_lg) {
                if (!vo.getErrorCode().equals("FF")) {
                    newlist.add(vo);
                }
            }
            for (int i = 0; i < newlist.size(); i++) {
                list_lg.remove(newlist.get(i));
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (db != null)
            db.close();

        super.onDestroy();
        fixInputMethodManagerLeak(this);
    }

    public void hideInputKeyboard() {
        setDelayTimeFirstNo.clearFocus();//取消焦点
        setDelayTimeEndNo.clearFocus();
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
     * 按序号删除雷管号
     */
    private void deleteDenatorforNo(String startNoStr, String endNoStr) {
        int start = Integer.parseInt(startNoStr);
        int end = Integer.parseInt(endNoStr);
        String whereClause = "blastserial>=? and blastserial<=?  and piece =?";
        String[] whereArgs = {start + "", end + "", mRegion};
        db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, whereClause, whereArgs);//删除数据
//        deleteListSerialNoDel(start, end);
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /****
     * 校验数据
     */
    private String checkData() {
        String tipStr = "";
        //起始序号
        String startNo = setDelayTimeFirstNo.getText().toString();
        //终点序号
        String endNo = setDelayTimeEndNo.getText().toString();
        if (Utils.isNum(startNo) == false) {
            tipStr = getString(R.string.text_error_tip25);//"开始序号不是数字";
            return tipStr;
        }
        if (Utils.isNum(endNo) == false) {
            tipStr = getString(R.string.text_error_tip26);// "结束序号不是数字";
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
        return tipStr;
    }

    private void runPbDialog() {
        pb_show = 1;
        tipDlg = new LoadingDialog(DelDenatorMainPage.this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (pb_show == 1) {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @OnClick({R.id.btn_all_del, R.id.btn_error_del, R.id.btn_serialNo_del, R.id.btn_del_return})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_all_del:
                tipALLDelDenator();
                break;
            case R.id.btn_error_del://删除错误雷管
                pb_show = 1;
                runPbDialog();
                Del_Err_Denator();

//                loadMoreData();//获取数据保存到list
//                refreshData();
                pb_show = 0;
                tipDlg.dismiss();

                break;
            case R.id.btn_serialNo_del://按序号删除
                hideInputKeyboard();
                AlertDialog dialog = new Builder(this)
                        .setTitle(getResources().getString(R.string.text_queryHis_dialog1))//设置对话框的标题//"成功起爆"
                        .setMessage(getResources().getString(R.string.text_delete_tip12))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNegativeButton(getResources().getString(R.string.text_alert_cancel), (dialog12, which) -> dialog12.dismiss())
                        .setPositiveButton(getResources().getString(R.string.text_queryHis_dialog10), (dialog1, which) -> {
                            pb_show = 1;
                            runPbDialog();
                            String checstr = checkData();
                            if (checstr == null || checstr.trim().length() < 1) {
                                //起始序号
                                String startNoStr = setDelayTimeFirstNo.getText().toString();
                                //终点序号
                                String endNoStr = setDelayTimeEndNo.getText().toString();
                                deleteDenatorforNo(startNoStr, endNoStr);

                                //(之前是屏蔽的,因为华丰翻转后再按序号删除会报错,所以屏蔽的,但是不排序的话,400发雷管,按序号删除200到300中间部分雷管,这样设置延时会报错)
                                Utils.deleteData(mRegion);//重新排序雷管
//                                loadMoreData();//获取数据保存到list
                                //加上后就立刻更新(暂时不加上的原因是按序号删除后,序号没变的话,感觉没删除,怕再次点击)
//                                    mAdapter = new LoadAdapter(DelDenatorMainPage.this, list_lg, R.layout.item_deldenator, 0);//(手动输入管壳码之后,错误码为空,会报空指针)
//                                    denatorDelListview.setAdapter(mAdapter);
                                refreshData();//刷新列表
                                tipDlg.dismiss();
                                show_Toast(getString(R.string.text_del_ok));
                                pb_show = 0;
                            } else {
                                show_Toast(checstr);
                            }
                            dialog1.dismiss();
                        }).create();
                dialog.show();


                break;
            case R.id.btn_del_return:
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
                break;
        }
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
        SPUtils.put(this, Constants_SP.RegionCode, mRegion);
        switch (item.getItemId()) {

            case R.id.item_1:
            case R.id.item_2:
            case R.id.item_3:
            case R.id.item_4:
            case R.id.item_5:
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                // 显示提示
                show_Toast(getResources().getString(R.string.text_show_1) + mRegion);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    /**
     * 设置标题区域
     */
    private void setTitleRegion(String region, int size) {

        String str;
        if (size == -1) {
            str = getResources().getString(R.string.text_list_piace) + region;
        } else {
            str = getResources().getString(R.string.text_list_piace) + region + "(" + getResources().getString(R.string.text_main_sl) + ": " + size + ")";
        }
        // 设置标题
        getSupportActionBar().setTitle(mOldTitle + str);
        // 保存区域参数
        SPUtils.put(this, Constants_SP.RegionCode, region);

        Log.e("liyi_Region", "已选择" + str);
    }

    /**
     * 重置所有翻转标记
     * */
    private void chongZhiFan() {
        MmkvUtils.savecode(mRegion+"n1", 0);
        MmkvUtils.savecode(mRegion+"n2", 0);
        MmkvUtils.savecode(mRegion+"n3", 0);
        MmkvUtils.savecode(mRegion+"n4", 0);
        MmkvUtils.savecode(mRegion+"n5", 0);
        MmkvUtils.savecode(mRegion+"n6", 0);
        MmkvUtils.savecode(mRegion+"n7", 0);
        MmkvUtils.savecode(mRegion+"n8", 0);
        MmkvUtils.savecode(mRegion+"n9", 0);
        MmkvUtils.savecode(mRegion+"n10", 0);
        MmkvUtils.savecode(mRegion+"n11", 0);
        MmkvUtils.savecode(mRegion+"n12", 0);
        MmkvUtils.savecode(mRegion+"n13", 0);
        MmkvUtils.savecode(mRegion+"n14", 0);
        MmkvUtils.savecode(mRegion+"n15", 0);
        MmkvUtils.savecode(mRegion+"n16", 0);
        MmkvUtils.savecode(mRegion+"n17", 0);
        MmkvUtils.savecode(mRegion+"n18", 0);
        MmkvUtils.savecode(mRegion+"n19", 0);
        MmkvUtils.savecode(mRegion+"n20", 0);
        MmkvUtils.savecode(mRegion+"n21", 0);
        MmkvUtils.savecode(mRegion+"n22", 0);
        MmkvUtils.savecode(mRegion+"n23", 0);
        MmkvUtils.savecode(mRegion+"n24", 0);
        MmkvUtils.savecode(mRegion+"n25", 0);
        MmkvUtils.savecode(mRegion+"n26", 0);
        MmkvUtils.savecode(mRegion+"n27", 0);
        MmkvUtils.savecode(mRegion+"n28", 0);
        MmkvUtils.savecode(mRegion+"n29", 0);
        MmkvUtils.savecode(mRegion+"n30", 0);
        MmkvUtils.savecode(mRegion+"n31", 0);
        MmkvUtils.savecode(mRegion+"n32", 0);
        MmkvUtils.savecode(mRegion+"n33", 0);
        MmkvUtils.savecode(mRegion+"n34", 0);
        MmkvUtils.savecode(mRegion+"n35", 0);
        MmkvUtils.savecode(mRegion+"n36", 0);
        MmkvUtils.savecode(mRegion+"n37", 0);
        MmkvUtils.savecode(mRegion+"n38", 0);
        MmkvUtils.savecode(mRegion+"n39", 0);
        MmkvUtils.savecode(mRegion+"n40", 0);
    }

}
