package android_serialport_api.xingbang.firingdevice;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.LoadAdapter;
import android_serialport_api.xingbang.custom.LoadListView;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.db.DatabaseHelper;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_currentinfo);
        ButterKnife.bind(this);

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
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 22);
        db = mMyDatabaseHelper.getReadableDatabase();

        int totalNum = (int) Application.getDaoSession().getDenatorBaseinfoDao().count();
        totalPage = (int) Math.ceil(totalNum / (float) pageSize);//通过计算得到总的页数
        txTotal.setText(getString(R.string.text_total) + totalNum);
        loadMoreData();

        mListView = findViewById(R.id.denator_query_listview);
//        mAdapter = new LoadAdapter(this, list, R.layout.query_current_item, 1);
//        mListView.setAdapter(mAdapter);
//        mListView.setLoadMoreListener(this);

        //新的适配方法 适配器

        linearLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(linearLayoutManager);
        mAdapter = new DetonatorAdapter_Paper<>(this, 3);
        mListView.setAdapter(mAdapter);

        mListData = new GreenDaoMaster().queryDetonatorDesc();
        mAdapter.setListData(mListData, 1);
        mAdapter.notifyDataSetChanged();

        mHandler_ui = new Handler(msg -> {

            switch (msg.what) {

                // 区域 更新视图
                case 1001:
                    // 查询全部雷管 倒叙(序号)
                    mListData = new GreenDaoMaster().queryDetonatorDesc();
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 1005://按管壳码排序
                    Log.e("扫码注册", "按管壳码排序flag: " + paixu_flag);
                    mListData = new GreenDaoMaster().queryDetonatorDesc();
                    Collections.sort(mListData);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
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


}
