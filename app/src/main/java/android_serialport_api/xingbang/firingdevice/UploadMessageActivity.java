package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.UploadMessageAdapter;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.CRC16;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadMessageActivity extends BaseActivity implements AdapterView.OnItemClickListener, UploadMessageAdapter.InnerItemOnclickListener {

    @BindView(R.id.lv_updata)
    ListView lvUpdata;
    @BindView(R.id.btn_del_return)
    Button btnDelReturn;
    @BindView(R.id.btn_del_reduction)
    Button btnDelReduction;

    private UploadMessageAdapter mAdapter;
    private List<Map<String, Object>> list_message = new ArrayList<Map<String, Object>>();
    private int currentPage = 1;//当前页数
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private List<VoBlastModel> leiguan_list = new ArrayList<>();
    private ArrayList<String> list_uid = new ArrayList<>();

    ArrayList<Map<String, Object>> hisListData = new ArrayList<Map<String, Object>>();//起爆雷管
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String server_addr = "";
    private String server_port = "";
    private String server_http = "";
    private String server_ip = "";
    private String server_type1 = "";
    private String server_type2 = "";

    private Handler mHandler_update = new Handler();//更新状态
    private LoadingDialog tipDlg = null;
    private int pb_show = 0;
    private Handler mHandler_loading = new Handler();//显示进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_data);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();

        if (1 == currentPage) {
            loadMoreData_shouquan(currentPage);
        }
        Log.e("数据上传", "list_message: "+list_message.size());
        getUserMessage();//获取用户信息
//        loadMoreData_lg(currentPage);//查询所有雷管
        mAdapter = new UploadMessageAdapter(this, list_message, R.layout.item_list_upload);
        mAdapter.setOnInnerItemOnClickListener(this);
        lvUpdata.setAdapter(mAdapter);
        lvUpdata.setOnItemClickListener(this);

        mHandler_update = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Object result = msg.obj;
                updataState(result + "");//更新上传状态
                int pos = msg.arg1;
//                list_savedate.get(pos).setUploadStatus("已上传");
                loadMoreData_shouquan(currentPage);//读取授权记录列表数据
                mAdapter.update(pos,lvUpdata);
                mAdapter.notifyDataSetChanged();
            }
        };

        mHandler_loading = new Handler(this.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (pb_show == 1 && tipDlg != null) tipDlg.show();
                if (pb_show == 0 && tipDlg != null) {
                    tipDlg.hide();
                    //currentPage = 1;
                    //list_savedate.clear();
                    //loadMore();
                    mAdapter.notifyDataSetChanged();
                }
//                 Looper.loop();
            }
        };
    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(UploadMessageActivity.this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
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
     * 更新上传信息状态
     */
    public void updataState(String blastdate) {
        Log.e("更新起爆状态", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("uploadStatus", "已上传");
        db.update(DatabaseHelper.TABLE_NAME_HISMAIN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 更新丹灵网上传信息状态
     */
    public void updataState_sq_dl(String blastdate) {
        Log.e("更新起爆状态", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("dl_state", "已上传");
        db.update(DatabaseHelper.TABLE_NAME_SHOUQUAN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 更新中爆网上传信息状态
     */
    public void updataState_sq_zb(String blastdate) {

        Log.e("更新起爆状态", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("zb_state", "已上传");
        db.update(DatabaseHelper.TABLE_NAME_SHOUQUAN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 更新中爆网上传信息状态
     */
    public void updataState_sq_zb_huanyuan(String blastdate) {
        Log.e("更新起爆状态", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("zb_state", "未上传");
        db.update(DatabaseHelper.TABLE_NAME_SHOUQUAN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把软存中的数据存入磁盘中
    }


    private void getUserMessage() {
        String selection = "id = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"1"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_MESSQGE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {  //cursor不位空,可以移动到第一行
            //int _id = cursor.getInt(0);
            pro_bprysfz = cursor.getString(1);
            pro_htid = cursor.getString(2);
            pro_xmbh = cursor.getString(3);
            equ_no = cursor.getString(4);
            pro_coordxy = cursor.getString(5);
            server_addr = cursor.getString(6);
            server_port = cursor.getString(7);
            server_http = cursor.getString(8);
            server_ip = cursor.getString(9);
            server_type1 = cursor.getString(13);
            server_type2 = cursor.getString(14);
            pro_dwdm = cursor.getString(15);
            cursor.close();
        }

    }

    /**
     * 查询所有雷管
     */
    private void loadMoreData_lg(int cp) {
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO;
        Cursor cursor = db.rawQuery(sql, null);//new String[]{(index) + "", pageSize + ""}
        leiguan_list.clear();
        this.currentPage = cp;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                int holeNo = cursor.getInt(2);
                String shellNo = cursor.getString(3);//管壳号
                int delay = cursor.getInt(5);
                String stCode = cursor.getString(6);//状态
                String stName = cursor.getString(7);//
                String errorCode = cursor.getString(9);//状态
                String errorName = cursor.getString(8);//

                VoBlastModel item = new VoBlastModel();
                item.setBlastserial(serialNo);
                item.setSithole(holeNo+"");
                item.setDelay((short) delay);
                item.setShellBlastNo(shellNo);
                item.setErrorCode(errorCode);
                item.setErrorName(errorName);
                item.setStatusCode(stCode);
                item.setStatusName(stName);
                leiguan_list.add(item);
            }
            cursor.close();
            this.currentPage++;
        }
        if (leiguan_list == null) {
            show_Toast("请注册雷管!");
        }
        list_uid.clear();
        for (int i = 0; i < leiguan_list.size(); i++) {
            list_uid.add(leiguan_list.get(i).getShellBlastNo());
        }
        Log.e("雷管", "list_uid: " + list_uid.toString());
    }

    private void loadMoreData_shouquan(int cp) {
        list_message.clear();
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_SHOUQUAN;//+" order by htbh "
        Cursor cursor = db.rawQuery(sql, null);
        //return getCursorTolist(cursor);
        this.currentPage = cp;
        if (cursor != null) {
            Gson gson = new Gson();
            DanLingBean danLingBean;
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String xmbh = cursor.getString(1); //项目编号//获取第二列的值 ,序号
                String htbh = cursor.getString(2);//合同编号
                String json = cursor.getString(3);//管壳号
                String errNum = cursor.getString(4);//错误数量
                String qbzt = cursor.getString(5);//起爆状态
                String blastdate = cursor.getString(6);//起爆时间
                String dl_state = cursor.getString(7);//丹灵上传状态
                String zb_state = cursor.getString(8);//中爆上传状态
                String dwdm = cursor.getString(9);//单位代码
                String bprysfz = cursor.getString(10);//爆破员身份证
                String coordxy = cursor.getString(11);//经纬度
                String qblgNum = cursor.getString(12);//已起爆雷管数量
                danLingBean = gson.fromJson(json, DanLingBean.class);

                if (qbzt.equals("已起爆")){
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("id", id);
                    item.put("htbh", htbh);
                    item.put("xmbh", xmbh);
                    item.put("qbzt", qbzt);
                    item.put("errNum", errNum);
                    item.put("danLingBean", danLingBean);
                    item.put("blastdate", blastdate);
                    item.put("dl_state", dl_state);
                    item.put("zb_state", zb_state);
                    item.put("at_dwdm", dwdm);
                    item.put("bprysfz", bprysfz);
                    item.put("coordxy", coordxy);
                    item.put("qblgNum", qblgNum);
                    list_message.add(item);
                }

            }
            cursor.close();
            this.currentPage++;
        }
    }

    /***
     * 建立雷管信息表对话框
     */
    public void createDialog(String blastdate) {
        LayoutInflater inflater = LayoutInflater.from(UploadMessageActivity.this);
        View getlistview = inflater.inflate(R.layout.query_his_detail_listview, null);
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
        SimpleAdapter adapter = new SimpleAdapter(UploadMessageActivity.this, hisListData, R.layout.query_his_detail_item, new String[]{"serialNo", "shellNo", "delay", "errorName"},
                new int[]{R.id.X_item_no, R.id.X_item_shellno, R.id.X_item_delay, R.id.X_item_errorname});
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
            cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null, null, null, null, null);
        } else {
            String selection = "blastdate = ? and errorCode = ?"; // 选择条件，给null查询所有//+" and errorCode = ?"   new String[]{"FF"}
            String[] selectionArgs = {blastdate,"FF"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
//            String selection = "blastdate = ? "; // 选择条件，给null查询所有//+" and errorCode = ?"   new String[]{"FF"}
//            String[] selectionArgs = {blastdate};//选择条件参数,会把选择条件中的？替换成这个数组中的值
            cursor = db.query(DatabaseHelper.TABLE_NAME_HISDETAIL, null, selection, selectionArgs, null, null, "blastserial asc");

        }
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                String errorCode = cursor.getString(9);//管壳号

                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                String shellNo = cursor.getString(3);//管壳号
                String errorName = cursor.getString(8);//错误信息
                int delay = cursor.getInt(5); //延时
                item = new HashMap<>();
                item.put("serialNo", serialNo);
                item.put("shellNo", shellNo);
                item.put("delay", "" + delay);
                if (errorName == null || errorName.trim().length() < 1) errorName = " ";
                item.put("errorName", errorName);
                hisListData.add(item);

            }
            Log.e("获取上传数据", "hisListData: " + hisListData.toString());
            cursor.close();
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("点击项目", "position: " + position);
        Intent intent = new Intent(UploadMessageActivity.this, ShouQuanLegActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("list_dl", (Serializable) list_message);
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void itemClick(View v) {
        int position = (int) v.getTag();
        Log.e("点击", "position: " + position);
        switch (v.getId()) {
            case R.id.btn_del_sq://删除按钮
//                delShouQuan(list_message.get(position).get("id").toString());//删除方法
                if (list_message != null && list_message.size() > 0) {//移除map中的值
                    list_message.remove(position);
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_upload://上传按钮
                if (list_message.get(position).get("qbzt").toString().equals("未爆破")) {
                    show_Toast("当前项目未起爆,请上传已起爆数据");
                } else {
                    String blastdate = list_message.get(position).get("blastdate").toString();//日期
                    String htbh = list_message.get(position).get("htbh").toString();//合同编号
                    String coordxy = list_message.get(position).get("coordxy").toString();//合同编号
                    String xy[] = coordxy.split(",");
                    String jd = xy[0];//经度
                    String wd = xy[1];//纬度
                    getHisDetailList(blastdate, 0);//获取起爆历史详细信息
                    Log.e("上传按钮", "hisListData: " + hisListData.toString());
                    pb_show = 1;
                    runPbDialog();
                    if (server_type1.equals("1")) {
                        upload(blastdate, position, htbh, jd, wd);//丹灵上传信息
                    }
                    if (server_type2.equals("2")) {//中爆上传
                        performUp(blastdate, htbh, jd, wd, position);
                    }
                    if (server_type2.equals("0") && server_type1.equals("0")) {
                        show_Toast("设备当前未设置上传网址,请先设置上传网址");
                    }
                }
                break;
            case R.id.tv_chakan_sq://查看按钮
                if (list_message.get(position).get("qbzt").toString().equals("未爆破")) {
                    show_Toast("当前项目未起爆,请上传已起爆数据");
                }else {
                    Log.e("起爆时间", list_message.get(position).get("blastdate").toString() );
                    createDialog(list_message.get(position).get("blastdate").toString());
                }

//                Intent intent2 = new Intent(UploadMessageActivity.this, ShouQuanLegActivity.class);
//                Bundle bundle2 = new Bundle();
//                bundle2.putSerializable("list_dl", (Serializable) list_message);
//                bundle2.putInt("position", position);
//                intent2.putExtras(bundle2);
//                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    /**
     * 更新上传状态
     */
    public int modifyUploadStatus(String id, String uploadStatus) {
        ContentValues values = new ContentValues();
        values.put("uploadStatus", uploadStatus);
        db.update(DatabaseHelper.TABLE_NAME_HISMAIN, values, "blastdate=?", new String[]{"" + id});
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }


    /**
     * 更新上传状态
     *
     * @param blastdate
     */
    private void performUp(final String blastdate, final String htid, final String jd, final String wd, final int position) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (updata(blastdate, htid, jd, wd).equals("0")) {//updata是上传中爆网方法成功返回0
                    updataState(blastdate);
                    updataState_sq_zb(blastdate);
                    list_message.get(position).put("zb_state", "已上传");
                    Message message = new Message();
                    message.obj = blastdate;
                    message.arg1 = position;
                    mHandler_update.sendMessage(message);
                }
                pb_show = 0;
                Looper.loop();
            }
        }).start();


    }

    private String updata(String blastdate, final String htid, final String jd, final String wd) {
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 1; i < hisListData.size(); i++) {
            list_uid.add(hisListData.get(i).get("shellNo") + "O");
        }
        Log.e("中爆网上传", "list_uid: " + list_uid.toString());
        if (equ_no.length() > 8) {//中爆网起爆器编号8位
            String qbq_no = equ_no.substring(0, 5) + equ_no.substring(8);//截取起爆器编号前4位和后四位
            Log.e("中爆网上传", "qbq_no: " + qbq_no);
            return Utils.uploadFireData(UploadMessageActivity.this, list_uid, pro_bprysfz, htid, pro_xmbh, (jd + "," + wd), server_type2, qbq_no, server_ip, server_port, server_http, blastdate);
        }
        return Utils.uploadFireData(UploadMessageActivity.this, list_uid, pro_bprysfz, htid, pro_xmbh, (jd + "," + wd), server_type2, equ_no, server_ip, server_port, server_http, blastdate);

    }

    /**
     * 读取详细历史信息
     */
    public void loadHisDetail(String blastdate, List<String> hisListData, String endStr) {//
        //List<String> hisListData = new ArrayList<String>();
        Cursor cursor = null;
        String selection = "blastdate = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {blastdate + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        cursor = db.query(DatabaseHelper.TABLE_NAME_HISDETAIL, null, selection, selectionArgs, null, null, "blastserial asc");
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                String shellNo = cursor.getString(3);//管壳号
                hisListData.add(shellNo + endStr);
            }
            cursor.close();
        }
    }


    /**
     * 丹灵上传方法
     */
    private void upload(final String blastdate, final int pos, final String htid, final String jd, final String wd) {
        final String key = "jadl12345678912345678912";
        String url = Utils.httpurl_upload_dl;//丹灵上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 1; i < hisListData.size(); i++) {
            list_uid.add(hisListData.get(i).get("shellNo") + "");
        }
        String uid = list_uid.toString().replace("[", "").replace("]", "").trim();
        if (uid.length() > 10) {//13位管壳码转换为17位uid
            for (int i = 0; i < list_uid.size(); i++) {
                byte[] uid1 = list_uid.get(i).getBytes();
                String c = CRC16.getCRC(uid1);
                String d = (list_uid.get(i) + c).toUpperCase();//
                Collections.replaceAll(list_uid, list_uid.get(i), d);
            }
            uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        }
        Log.e("uid", uid);
        Log.e("blastdate", blastdate);
        String xy[] = pro_coordxy.toString().split(",");//经纬度
        try {
            object.put("sbbh", equ_no);//起爆器设备编号
            if (jd != null) {
                object.put("jd", jd);//经度
            } else {
                object.put("jd", xy[0]);//经度
            }
            if (wd != null) {
                object.put("wd", wd);//纬度
            } else {
                object.put("wd", xy[1]);//纬度
            }
            object.put("htid", htid);//合同编号
            object.put("bpsj", blastdate.replace("/", "-").replace(",", " "));//爆破时间blastdate.replace("/","-").replace(","," ")
            object.put("bprysfz", pro_bprysfz);//人员身份证
            object.put("uid", uid);//雷管uid
            object.put("at_dwdm", pro_dwdm);//单位代码
            object.put("xmbh", pro_xmbh);//项目编号
            Log.e("上传信息", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = MyUtils.getBase64(MyUtils.encryptMode(key.getBytes(), object.toString().getBytes()));
        RequestBody requestBody = new FormBody.Builder()
                .add("param", json.replace("\n", ""))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "text/plain")//text/plain  application/json  application/x-www-form-urlencoded
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("网络请求", "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject object = null;
//				mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());
                try {
                    object = new JSONObject(response.body().string().toString());
                    Log.e("上传", "丹灵返回: " + object.toString());
                    String success = object.getString("success");
                    if (success.equals("true")) {
                        updataState_sq_dl(blastdate);
                        list_message.get(pos).put("dl_state", "已上传");
                        Message message = new Message();
                        message.obj = blastdate;
                        message.arg1 = pos;
                        mHandler_update.sendMessage(message);
                        show_Toast_ui("丹灵上传成功");
                        if(!server_type2.equals("2")){
                            pb_show = 0;
                        }
                    } else if (success.equals("fail")) {
                        String cwxx = object.getString("cwxx");
                        if (cwxx.equals("1")) {
                            show_Toast_ui("错误信息:" + object.getString("cwxxms"));
                        } else if (cwxx.equals("2")) {
                            show_Toast_ui("起爆器未备案或未设置作业任务");
                        } else {
                            show_Toast_ui(object.getString("cwxxms"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @OnClick({R.id.btn_del_return, R.id.btn_del_reduction})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_del_return:
                finish();
                break;
            case R.id.btn_del_reduction:
                updataState_sq_zb_huanyuan("2019/10/11,09:26:18");
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
//        Utils.saveFile();//把软存中的数据存入磁盘中
        super.onDestroy();
    }
}
