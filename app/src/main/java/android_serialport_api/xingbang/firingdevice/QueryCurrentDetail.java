package android_serialport_api.xingbang.firingdevice;

import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.LoadAdapter;
import android_serialport_api.xingbang.custom.LoadListView;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 当前雷管数据
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class QueryCurrentDetail extends BaseActivity {

    @BindView(R.id.tx_total)
    TextView txTotal;
    @BindView(R.id.re_gkm)
    LinearLayout regkm;
    @BindView(R.id.text_gkm1)
    TextView text_gkm;
    @BindView(R.id.text_gkm2)
    TextView text_uid;
    private DatabaseHelper mMyDatabaseHelper;
    private List<DenatorBaseinfo> list = new ArrayList<>();
    private SQLiteDatabase db;
    //private ScrollView scrollview;
    private TextView startNoTxt;//起始序号
    private TextView endNoTxt;//终点序号
    private int totalNum;//总的数据条数
    private int pageSize = 600;//每页显示的数据
    private int totalPage;//总的页数
    private int currentPage = 1;//当前页数
    private boolean isBottom = false;
    private Button btn_return;
    private Button btn_paixu;
//    private LoadAdapter mAdapter;
//    private LoadListView mListView;
    private RecyclerView mListView;
    private Handler mHandler_ui = new Handler();     // UI处理
    private List<DenatorBaseinfo> mListData = new ArrayList<>();//所有雷管列表
    private DetonatorAdapter_Paper<DenatorBaseinfo> mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean paixu_flag = true;//排序标志
    private boolean switchUid =true;//切换uid/管壳码
    // 雷管列表
    private String mOldTitle;   // 原标题
    private String mRegion;     // 区域
    private boolean mRegion1, mRegion2, mRegion3, mRegion4, mRegion5 = true;//是否选中区域1,2,3,4,5
    private TextView totalbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_currentinfo);
        ButterKnife.bind(this);
        initView();


        mHandler_ui.sendMessage(mHandler_ui.obtainMessage(1001));
    }

    private void initView() {
        mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);
        mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);
        mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);
        mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);
        mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);

        totalbar_title = findViewById(R.id.title_text);
        totalbar_title.setText(getString(R.string.xingbang_main_page_btn_query_current));
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setOnClickListener(v -> {
            choiceQuYu();
        });
        iv_back.setOnClickListener(v -> finish());

        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        // 原标题
        mOldTitle = getSupportActionBar().getTitle().toString();
        // 设置标题区域
        setTitleRegion(mRegion, -1);

        btn_return = (Button) findViewById(R.id.btn_del_return);
        btn_return.setOnClickListener(v -> {
            Intent intentTemp = new Intent();
            intentTemp.putExtra("backString", "");
            setResult(1, intentTemp);
            finish();
        });
        btn_paixu = findViewById(R.id.btn_paixu);
        btn_paixu.setOnClickListener(v -> {
            if (paixu_flag) {
                mHandler_ui.sendMessage(mHandler_ui.obtainMessage(1005));
                paixu_flag = false;
            } else {
                mHandler_ui.sendMessage(mHandler_ui.obtainMessage(1001));
                paixu_flag = true;
            }
        });
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();

        mListView = findViewById(R.id.denator_query_listview);
//        mAdapter = new LoadAdapter(this, list, R.layout.query_current_item, 1);
//        mListView.setAdapter(mAdapter);
//        mListView.setLoadMoreListener(this);

        //新的适配方法 适配器

        linearLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(linearLayoutManager);
        mAdapter = new DetonatorAdapter_Paper<>(this, 3);
        mListView.setAdapter(mAdapter);

//        mListData = new GreenDaoMaster().queryDetonatorDesc();
//        mAdapter.setListData(mListData, 1);
//        mAdapter.notifyDataSetChanged();

        mHandler_ui = new Handler(msg -> {

            switch (msg.what) {

                // 区域 更新视图
                case 1001:
                    // 查询全部雷管 倒叙(序号)
//                    mListData = new GreenDaoMaster().queryDetonatorDesc();
//                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
                    mAdapter.setListData(mListData, 1);
                    txTotal.setText(getString(R.string.text_total) + mListData.size());
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
                    break;
                case 1005://按管壳码排序
                    Log.e("扫码注册", "按管壳码排序flag: " + paixu_flag);
//                    mListData = new GreenDaoMaster().queryDetonatorDesc();
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    Collections.sort(mListData);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();

                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());
                    break;
            }
            return false;
        });

        regkm.setOnClickListener(v -> {
            int a;
            if(switchUid){
                a=7;
                switchUid=false;
                text_uid.setTextColor(Color.GREEN);
                text_gkm.setTextColor(Color.BLACK);
            }else {
                a=3;
                switchUid=true;
                text_uid.setTextColor(Color.BLACK);
                text_gkm.setTextColor(Color.GREEN);
            }
            mAdapter = new DetonatorAdapter_Paper<>(QueryCurrentDetail.this, a);
            mListView.setLayoutManager(linearLayoutManager);
            mListView.setAdapter(mAdapter);
            mHandler_ui.sendMessage(mHandler_ui.obtainMessage(1001));

        });
    }

    private void loadMoreData() {
        list = Application.getDaoSession().getDenatorBaseinfoDao().loadAll();
    }

//    @Override
//    public void loadMore() {
//        if (currentPage <= totalPage) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    loadMoreData();
//                    showLoadMore();
//                }
//            }, 1000);
//        } else {
//            if (isBottom == false) {
//                show_Toast(getString(R.string.text_error_tip48));
//                isBottom = true;
//            }
//        }
//        mListView.setFooterGone();
//    }

//    private void showLoadMore() {
//        mAdapter.notifyDataSetChanged();
//    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (db != null)
            db.close();

//        Utils.saveFile();//把软存中的数据存入磁盘中
        super.onDestroy();
        fixInputMethodManagerLeak(this);
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
                mHandler_ui.sendMessage(mHandler_ui.obtainMessage(1001));
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

        String str;
        if (size == -1) {
            str = " 区域" + region;
        } else {
            str = " 区域" + region + "(数量: " + size + ")";
        }
        // 设置标题
        getSupportActionBar().setTitle(mOldTitle + str);
        // 保存区域参数
//        SPUtils.put(this, Constants_SP.RegionCode, region);

        Log.e("liyi_Region", "已选择" + str);
    }

    private void choiceQuYu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle(R.string.text_dialog_choice);
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
                mHandler_ui.sendMessage(mHandler_ui.obtainMessage(1001));

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
