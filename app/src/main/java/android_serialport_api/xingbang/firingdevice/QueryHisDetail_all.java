package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadHisFireAdapter_all;
import android_serialport_api.xingbang.custom.LoadListView;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.models.VoFireHisMain;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看检测历史记录
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class QueryHisDetail_all extends BaseActivity implements LoadListView.OnLoadMoreListener, LoadHisFireAdapter_all.InnerItemOnclickListener,
        OnItemClickListener {

    @BindView(R.id.btn_del_return)
    Button btnDelReturn;
    @BindView(R.id.btn_del_all)
    Button btnDelAll;
    @BindView(R.id.denator_del_func)
    LinearLayout denatorDelFunc;
    @BindView(R.id.denator_query_his_listview)
    LoadListView denatorQueryHisListview;
    @BindView(R.id.denator_del_mainpage)
    LinearLayout denatorDelMainpage;
    private DatabaseHelper mMyDatabaseHelper;
    private List<VoFireHisMain> list = new ArrayList<>();

    private SQLiteDatabase db;
    //private ScrollView scrollview;
    private TextView startNoTxt;//起始序号
    private TextView endNoTxt;//终点序号
    private int totalNum;//总的数据条数
    private int pageSize = 600;//每页显示的数据
    private int totalPage;//总的页数
    private int currentPage = 1;//当前页数
    private View getlistview;
    private boolean isBottom = false;
    private String equ_no = "";//设备编码
    private String pro_bprysfz= "";//证件号码
    private String pro_htid= "";//合同号码
    private String pro_xmbh= "";//项目编号
    private String pro_coordxy="";//经纬度
    private String pro_dwdm= "";//单位代码
    private String server_addr= "";
    private String server_port= "";
    private String server_http= "";
    private String server_ip= "";
    private String server_type1= "";
    private String server_type2= "";
    private String denator_Type_isSelected= "";//是否设置雷管最大延时
    private int Preparation_time;//准备时间
    private int ChongDian_time;//准备时间
    private String qiaosi_set= "";//是否检测桥丝

    private Handler mHandler_2 = new Handler();//显示进度条
    private Handler mHandler_update = new Handler();//显示进度条
    private LoadingDialog tipDlg = null;
    private int pb_show = 0;
    private ArrayList<Map<String, Object>> hisListData = new ArrayList<Map<String, Object>>();//错误雷管
    private LoadHisFireAdapter_all mAdapter;
    private LoadListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_hisinfo_all);
        ButterKnife.bind(this);
        tipDlg = new LoadingDialog(QueryHisDetail_all.this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 22);
        db = mMyDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select* from " + DatabaseHelper.TABLE_NAME_HISMAIN_ALL+" where remark=?", new String[]{"未注册"});
        totalNum = cursor.getCount();//得到数据的总条数
        totalPage = (int) Math.ceil(totalNum / (float) pageSize);//通过计算得到总的页数
        VoFireHisMain item = new VoFireHisMain();
        item.setBlastdate("当前检测记录");//"当前雷管记录"
        item.setFiredNo("");
        item.setLatitude("");
        item.setLongitude("");
        item.setRemark("");
        item.setSerialNo("");
        item.setUploadStatus("未上传");
        item.setUserid("");
        item.setId("");
        list.add(item);
        if (cursor != null) cursor.close();
        if (1 == currentPage) {
            loadMoreData(currentPage);//读取数据
        }
        mListView = (LoadListView) findViewById(R.id.denator_query_his_listview);
        mAdapter = new LoadHisFireAdapter_all(this, list, R.layout.item_query_his_all, 1);
        mAdapter.setOnInnerItemOnClickListener(this);
        mListView.setAdapter(mAdapter);
        mListView.setLoadMoreListener(this);
        mListView.setOnItemClickListener(this);
        mHandler_2 = new Handler(this.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (pb_show == 1 && tipDlg != null) tipDlg.show();
                if (pb_show == 0 && tipDlg != null) {
                    tipDlg.hide();
                    //currentPage = 1;
                    //list.clear();
                    //loadMore();
                    showLoadMore();
                }
//                 Looper.loop();
            }
        };
        mHandler_update = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Object result = msg.obj;
                updataState(result + "");//更新上传状态
                int pos = msg.arg1;
                list.get(pos).setUploadStatus("已上传");
                loadMoreData(currentPage);//读取历史记录列表数据
                mAdapter.notifyDataSetChanged();
            }
        };
    }

    private void loadMoreData(int cp) {
        Log.e("查询历史记录", "loadMoreData: ");
        int index = (currentPage - 1) * pageSize;//当前页   起始的下标  （2-1）*10
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_HISMAIN_ALL + " where remark = ? order by blastdate desc limit ?,?";
        Cursor cursor = db.rawQuery(sql, new String[]{"未注册",(index) + "", pageSize + ""});
        //return getCursorTolist(cursor);
        this.currentPage = cp;
        if (cursor != null) {
            while (cursor.moveToNext()) {

                String id = cursor.getString(0);
                String blastdate = cursor.getString(1); //获取第二列的值 ,序号
                String uploadStatus = cursor.getString(2);
                String longitude = cursor.getString(3);//管壳号
                String latitude = cursor.getString(4);
                String userid = cursor.getString(5);//
                String firedNo = cursor.getString(6);//
                String serialNo = cursor.getString(7);//
                String remark = cursor.getString(8);//

                VoFireHisMain item = new VoFireHisMain();
                item.setBlastdate(blastdate);
                item.setFiredNo(firedNo);
                item.setLatitude(latitude);
                item.setLongitude(longitude);
                item.setRemark(remark);
                item.setSerialNo(serialNo);
                item.setUploadStatus(uploadStatus);
                item.setUserid(userid);
                item.setId(id);
                list.add(item);

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

    //整体item
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VoFireHisMain vo = list.get(position);
        if (vo != null) {
            createDialog(vo.getBlastdate());//
        } else {
            show_Toast(getString(R.string.text_error_tip54));
        }
    }


    private int delHisInfo(String blastdate) {//删除雷管
        if (blastdate == null) return 1;
        if (getString(R.string.text_alert_tip3).equals(blastdate)) {
            show_Toast(getString(R.string.text_error_tip52));
            return 1;
        }
        //从表
        String selection = "blastdate = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {blastdate + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        db.delete(DatabaseHelper.TABLE_NAME_HISDETAIL_ALL, selection, selectionArgs);
        //主表
        db.delete(DatabaseHelper.TABLE_NAME_HISMAIN_ALL, selection, selectionArgs);

        if (list != null && list.size() > 0) {

            for (int i = list.size() - 1; i >= 0; i--) {
                VoFireHisMain vo = list.get(i);
                if (blastdate.equals(vo.getBlastdate())) {
                    list.remove(vo);
                }
            }
        }
        showLoadMore();
        return 0;
    }

    @Override
    public void itemClick(View v) {
        switch (v.getId()) {

            case R.id.bt_delete:
                String t = (String) v.getTag(R.id.bt_delete);
                if (delHisInfo(t) == 0) {
                    show_Toast(getString(R.string.xingbang_main_page_btn_del) + t + getString(R.string.text_success));
                }
                break;
            default:
                break;
        }
        //Toast.makeText(QueryHisDetail.this,""+ position, Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取起爆历史详细信息
     */
    private void getHisDetailList(String blastdate, int type) {
        hisListData.clear();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("serialNo", getString(R.string.text_list_Serial));//"序号"
        item.put("shellNo", getString(R.string.text_list_guan));//"管壳码"
        item.put("delay", "" + getString(R.string.text_list_delay));//"延时"
        item.put("errorName", getString(R.string.text_list_state));//"状态"
        hisListData.add(item);
        Cursor cursor = null;
        if (type == 1) {
            cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO_ALL, null, null, null, null, null, null);
        } else {
            String selection = "blastdate = ?"; // 选择条件，给null查询所有
            String[] selectionArgs = {blastdate};//选择条件参数,会把选择条件中的？替换成这个数组中的值
            cursor = db.query(DatabaseHelper.TABLE_NAME_HISDETAIL_ALL, null, selection, selectionArgs, null, null, "blastserial asc");
        }
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                String errorCode = cursor.getString(9);//管壳号
                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                String shellNo = cursor.getString(3);//管壳号
                String errorName = cursor.getString(8);//错误信息
                int delay = cursor.getInt(5); //延时
                item = new HashMap<String, Object>();
                item.put("serialNo", serialNo);
                item.put("shellNo", shellNo);
                item.put("delay", "" + delay);
                if (errorName == null || errorName.trim().length() < 1) errorName = " ";
                item.put("errorName", errorName);
                hisListData.add(item);

            }
            cursor.close();
        }

    }

    /**
     * 更新上传信息状态
     */
    public void updataState(String blastdate) {
        Log.e("更新起爆状态", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("uploadStatus", "已上传");
        db.update(DatabaseHelper.TABLE_NAME_HISMAIN_ALL, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把软存中的数据存入磁盘中
    }


    /***
     * 建立雷管信息表对话框
     */
    public void createDialog(String blastdate) {
        LayoutInflater inflater = LayoutInflater.from(QueryHisDetail_all.this);
        getlistview = inflater.inflate(R.layout.query_his_detail_listview, null);
        int flag = 0;
        if (getString(R.string.text_alert_tip3).equals(blastdate)) {//"当前雷管记录"
            flag = 1;
        }
        getHisDetailList(blastdate, flag);//获取起爆历史详细信息
        // 给ListView绑定内容
        ListView listview = (ListView) getlistview.findViewById(R.id.his_detail_listview);
        TextView txtView = (TextView) getlistview.findViewById(R.id.his_detail_count);
        int count = hisListData.size();
        if (count > 0) count -= 1;
        txtView.setText(getString(R.string.text_alert_tip4) + count);//"雷管总数:"
        SimpleAdapter adapter = new SimpleAdapter(QueryHisDetail_all.this, hisListData,
                R.layout.query_his_detail_all_item,
                new String[]{"shellNo", "delay", "errorName"},
                new int[]{ R.id.X_item_shellno, R.id.X_item_delay, R.id.X_item_errorname});//"serialNo",  R.id.X_item_no,
        // 给listview加入适配器
        listview.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (flag == 1)
            builder.setTitle(getString(R.string.text_alert_tip5));//"当前雷管列表"
        else
            builder.setTitle(getString(R.string.text_alert_tip6));//"已爆雷管列表"
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

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
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("删除提示")//设置对话框的标题//"成功起爆"
                        .setMessage("该操作会按序号删除表里的数据,是否删除?")//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.delete(DatabaseHelper.TABLE_NAME_HISDETAIL_ALL, null, null);
                                //主表
                                db.delete(DatabaseHelper.TABLE_NAME_HISMAIN_ALL, null, null);
                                if (list != null && list.size() > 0) {
                                    for (int i = list.size() - 1; i > 0; i--) {
                                        VoFireHisMain vo = list.get(i);
                                        list.remove(vo);
                                    }
                                }
                                showLoadMore();
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();

                break;
        }
    }
}
