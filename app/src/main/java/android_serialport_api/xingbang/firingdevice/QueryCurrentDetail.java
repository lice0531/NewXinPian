package android_serialport_api.xingbang.firingdevice;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadAdapter;
import android_serialport_api.xingbang.custom.LoadListView;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 当前雷管数据
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class QueryCurrentDetail extends BaseActivity implements LoadListView.OnLoadMoreListener {

    @BindView(R.id.tx_total)
    TextView txTotal;
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
    private LoadAdapter mAdapter;
    private LoadListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_currentinfo);
        ButterKnife.bind(this);

        btn_return = (Button) findViewById(R.id.btn_del_return);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
            }
        });

        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();

        int totalNum = (int) Application.getDaoSession().getDenatorBaseinfoDao().count();
        totalPage = (int) Math.ceil(totalNum / (float) pageSize);//通过计算得到总的页数
        txTotal.setText(getString(R.string.text_total) + totalNum);
        loadMoreData();

        mListView = (LoadListView) findViewById(R.id.denator_query_listview);
        mAdapter = new LoadAdapter(this, list, R.layout.query_current_item, 1);
        mListView.setAdapter(mAdapter);
        mListView.setLoadMoreListener(this);
    }

    private void loadMoreData() {
        list = Application.getDaoSession().getDenatorBaseinfoDao().loadAll();
    }

    @Override
    public void loadMore() {
        if (currentPage <= totalPage) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMoreData();
                    showLoadMore();
                }
            }, 1000);
        } else {
            if (isBottom == false) {
                show_Toast(getString(R.string.text_error_tip48));
                isBottom = true;
            }
        }
        mListView.setFooterGone();
    }

    private void showLoadMore() {
        mAdapter.notifyDataSetChanged();
    }


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
