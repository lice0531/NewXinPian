package android_serialport_api.xingbang.firingdevice;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadAdapter_all;
import android_serialport_api.xingbang.custom.LoadListView;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 当前雷管数据
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class QueryCurrentDetail_All extends BaseActivity implements LoadListView.OnLoadMoreListener {

    @BindView(R.id.tx_total)
    TextView txTotal;
    @BindView(R.id.btn_del_return)
    Button btnDelReturn;
    @BindView(R.id.btn_del_all)
    Button btnDelAll;
    @BindView(R.id.denator_del_func)
    LinearLayout denatorDelFunc;
    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.denator_query_listview)
    LoadListView denatorQueryListview;
    @BindView(R.id.denator_del_mainpage)
    LinearLayout denatorDelMainpage;
    private DatabaseHelper mMyDatabaseHelper;
    private List<VoBlastModel> lg_list = new ArrayList<>();
    private SQLiteDatabase db;
    //private ScrollView scrollview;
    private TextView startNoTxt;//起始序号
    private TextView endNoTxt;//终点序号
    private int totalNum;//总的数据条数
    private int pageSize = 600;//每页显示的数据
    private int totalPage;//总的页数
    private int currentPage = 1;//当前页数
    private boolean isBottom = false;
    private LoadAdapter_all mAdapter;
    private LoadListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_currentinfo_all);
        ButterKnife.bind(this);

        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from denatorBaseinfo_all ", null);
        totalNum = cursor.getCount();//得到数据的总条数
        totalPage = (int) Math.ceil(totalNum / (float) pageSize);//通过计算得到总的页数
        txTotal.setText(getString(R.string.text_total) + totalNum);
        if (cursor != null) cursor.close();
        if (1 == currentPage) {
            loadMoreData(currentPage);
        }
        Log.e("雷管列表", "lg_list: "+lg_list.toString() );
        mListView = (LoadListView) findViewById(R.id.denator_query_listview);
        mAdapter = new LoadAdapter_all(this, lg_list, R.layout.query_current_all_item, 1);
        mListView.setAdapter(mAdapter);
        mListView.setLoadMoreListener(this);
    }

    private void loadMoreData(int cp) {

        int index = (currentPage - 1) * pageSize;//当前页   起始的下标  （2-1）*10
        String sql = "Select * from denatorBaseinfo_all order by id desc ";//order by blastserial asc limit ?,?
        Cursor cursor = db.rawQuery(sql, null);
        //return getCursorTolist(cursor);
        this.currentPage = cp;
        if (cursor != null) {
            while (cursor.moveToNext()) {

                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                int holeNo = cursor.getInt(2);//孔号
                String shellNo = cursor.getString(3);//管壳号
                int delay = cursor.getInt(5);//延时
                String stCode = cursor.getString(6);//状态
                String stName = cursor.getString(7);//
                String errorCode = cursor.getString(9);//状态
                String errorName = cursor.getString(8);//
                String regdate = cursor.getString(12);//注册日期
                String wire = cursor.getString(13);//

                VoBlastModel item = new VoBlastModel();
                item.setBlastserial(serialNo);
                item.setSithole(holeNo);
                item.setDelay((short) delay);
                item.setShellBlastNo(shellNo);
                item.setErrorCode(errorCode);
                item.setErrorName(errorName);
                item.setStatusCode(stCode);
                item.setStatusName(stName);
                item.setWire(wire);
                item.setRegdate(regdate);
                lg_list.add(item);

            }

            cursor.close();
            this.currentPage++;
        }
    }

    @Override
    public void loadMore() {
        if (currentPage <= totalPage) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMoreData(currentPage);
                    showLoadMore();
                }
            }, 1000);
        } else {
            mListView.setFooterGone();
            if (isBottom == false) {
                show_Toast(getString(R.string.text_error_tip48));
                isBottom = true;
            }
        }
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

    public void tipALLDelDenator() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QueryCurrentDetail_All.this);
        builder.setTitle(getString(R.string.text_alert_tip));//"提示"

        builder.setMessage(getString(R.string.text_alert_del_all));//是否全部删除注册雷管数
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO_ALL, null, null);
                lg_list.clear();
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
                //  builder.
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //  builder.
            }
        });
        builder.show();
    }

    @OnClick({R.id.btn_del_return, R.id.btn_del_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_del_return:
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
                break;
            case R.id.btn_del_all:
                tipALLDelDenator();
                break;
        }
    }
}
