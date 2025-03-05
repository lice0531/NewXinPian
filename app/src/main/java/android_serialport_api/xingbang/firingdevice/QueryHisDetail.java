package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadHisDetailRecyclerAdapter;
import android_serialport_api.xingbang.custom.LoadHisFireAdapter;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorHis_Detail;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_MainDao;
import android_serialport_api.xingbang.db.greenDao.ShouQuanDao;
import android_serialport_api.xingbang.models.VoFireHisMain;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.NetUtils;
import android_serialport_api.xingbang.utils.PropertiesUtil;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android_serialport_api.xingbang.Application.getContext;
import static android_serialport_api.xingbang.Application.getDaoSession;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * 查看历史记录
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class QueryHisDetail extends BaseActivity {
    @BindView(R.id.btn_del_return)
    Button btnDelReturn;
    @BindView(R.id.btn_del_all)
    Button btnDelAll;
    @BindView(R.id.denator_del_func)
    LinearLayout denatorDelFunc;
    @BindView(R.id.denator_query_his_listview)
    RecyclerView denatorQueryHisListview;
    @BindView(R.id.denator_del_mainpage)
    LinearLayout denatorDelMainpage;
    @BindView(R.id.lay_bottom)
    LinearLayout layBottom;
    @BindView(R.id.tv_input)
    TextView tv_input;
    @BindView(R.id.tv_check_all)
    TextView tv_check_all;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    private List<VoFireHisMain> list_savedate = new ArrayList<>();
    private int totalNum;//总的数据条数
    private int pageSize = 600;//每页显示的数据
    private int totalPage;//总的页数
    private int currentPage = 1;//当前页数
    private View getlistview;
    private boolean isBottom = false;
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
    private String Shangchuan;
    private Button btn_del_all;

    private Handler mHandler_2 = new Handler();//显示进度条
    private Handler mHandler_tip = new Handler();//提示
    private Handler mHandler_update = new Handler();//更新状态
    private LoadingDialog tipDlg = null;
    private int pb_show = 0;
    private ArrayList<Map<String, Object>> hisListData = new ArrayList<>();//错误雷管
    private LoadHisFireAdapter mAdapter;
    private LoadHisDetailRecyclerAdapter hisAdapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private PropertiesUtil mProp;
    private String changjia = "TY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_hisinfo);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        tipDlg = new LoadingDialog(QueryHisDetail.this);
        changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
        Log.e("上传", "changjia: " + changjia);
        AppLogUtils.writeAppLog("---进入起爆历史记录上传页面");
        getUserMessage();//获取用户信息
        getPropertiesData();//第二种获取用户信息


        totalNum = getDaoSession().getDenatorHis_DetailDao().loadAll().size();//得到数据的总条数
        totalPage = (int) Math.ceil(totalNum / (float) pageSize);//通过计算得到总的页数
        if (1 == currentPage) {
            loadMoreData(currentPage);//读取数据
        }
        //初始化listview
//        mAdapter = new LoadHisFireAdapter(this, list_savedate, R.layout.item_query_his, 1);
//        mAdapter.setOnInnerItemOnClickListener(this);

//        denatorQueryHisListview.setAdapter(mAdapter);
//        denatorQueryHisListview.setLoadMoreListener(this);
//        denatorQueryHisListview.setOnItemClickListener(this);

        // 线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        denatorQueryHisListview.setLayoutManager(linearLayoutManager);
        hisAdapter = new LoadHisDetailRecyclerAdapter(R.layout.item_query_his, list_savedate);
        denatorQueryHisListview.setAdapter(hisAdapter);
        hisAdapter.setStatus(this, Shangchuan);
        hisAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                layBottom.setVisibility(View.VISIBLE);
                hisAdapter.showCheckBox(true);
                return true;
            }
        });
        hisAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VoFireHisMain vo = list_savedate.get(position);
                if (vo != null) {
                    createDialog(vo);//
                } else {
                    show_Toast(getString(R.string.text_error_tip54));
                }
            }
        });
        hisAdapter.setOnItemClickListener(new LoadHisDetailRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onButtonClicked(View v, int position) {
                switch (v.getId()) {
                    case R.id.bt_upload://上传按钮
                        if (!NetUtils.haveNetWork(getContext())) {
                            show_Toast(getResources().getString(R.string.text_jcwl));
                            return;
                        }
//                        int pos = (Integer) v.getTag(R.id.bt_upload);//位置
                        String blastdate = list_savedate.get(position).getBlastdate();//日期
                        String htbh = list_savedate.get(position).getProjectNo();//合同编号
                        String dwdm = list_savedate.get(position).getDwdm();//单位代码
                        String xmbh = list_savedate.get(position).getXmbh();//项目编号
                        String jd = list_savedate.get(position).getLongitude();//经度
                        String wd = list_savedate.get(position).getLatitude();//纬度
                        String qbxm_id = list_savedate.get(position).getSerialNo();//项目编号
                        String qbxm_name = list_savedate.get(position).getProjectName();//项目名称
                        pro_bprysfz = list_savedate.get(position).getUserid();//身份证
                        String log = list_savedate.get(position).getLog();//日志
//                        mAdapter.notifyDataSetChanged();
                        getHisDetailList(blastdate, 0);//获取起爆历史详细信息
                        if (blastdate == null || blastdate.trim().length() < 8) {
                            int count = getBlastModelCount();
                            if (count < 1) {
                                show_Toast(getResources().getString(R.string.text_error_tip55));
                                return;
                            }
                            String fireDate = Utils.getDateFormatLong(new Date());
                            saveFireResult(fireDate);
                            blastdate = fireDate;
                        }
//                        Utils.writeLog("项目上传信息:" + list_savedate.get(pos));
                        Log.e("上传-经纬度", "pro_coordxy: " + pro_coordxy);
                        Log.e("上传-经纬度", "jd: " + jd);
                        if (pro_coordxy.length() < 2 && jd == null) {
                            show_Toast(getResources().getString(R.string.text_his_jwdwk));
                            return;
                        }
                        if (equ_no.length() < 1) {
                            show_Toast(getResources().getString(R.string.text_down_err2));
                            return;
                        }

                        if (server_type2.equals("0") && server_type1.equals("0")) {
                            show_Toast(getResources().getString(R.string.text_his_scwz));
                        }
//                modifyFactoryInfo(blastdate, pos,htbh,jd,wd,xmbh,dwdm);//用于确认上传信息()
                        pb_show = 1;
                        runPbDialog();//loading画面
                        if (server_type1.equals("1")) {
                            upload(blastdate, position, htbh, jd, wd, xmbh, dwdm);//丹灵上传信息
                        }
                        if (server_type2.equals("2")) {
                            performUp(blastdate, position, htbh, jd, wd);//中爆上传
                        }
                        upload_xingbang(blastdate, position, htbh, jd, wd, xmbh, dwdm, qbxm_name, log);//我们自己的网址

                        break;
                    case R.id.bt_delete:
//                        AlertDialog dialog = new AlertDialog.Builder(QueryHisDetail.this)
//                                .setTitle(getResources().getString(R.string.text_queryHis_dialog1))//设置对话框的标题//"成功起爆"
//                                .setMessage(getResources().getString(R.string.text_queryHis_dialog9))//设置对话框的内容"本次任务成功起爆！"
//                                //设置对话框的按钮
//                                .setNeutralButton(getResources().getString(R.string.text_alert_cancel), (dialog1, which) -> dialog1.dismiss())
//                                .setPositiveButton(getResources().getString(R.string.text_queryHis_dialog10), (dialog12, which) -> {
//                                    dialog12.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(QueryHisDetail.this);
                        builder.setTitle(getResources().getString(R.string.text_queryHis_dialog1));//"请输入用户名和密码"
                        View view = LayoutInflater.from(QueryHisDetail.this).inflate(R.layout.userlogindialog_delete, null);
                        TextView tvTitle = view.findViewById(R.id.tvTitle);
                        tvTitle.setText("请输入密码后,再进行删除记录操作");
                        builder.setView(view);
                        final EditText password = view.findViewById(R.id.password);
                        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String b = password.getText().toString().trim();
                                if (b == null || b.trim().length() < 1) {
                                    show_Toast(getString(R.string.text_alert_password));
                                    return;
                                }
                                if (b.equals("123")) {
                                    String t = (String) v.getTag(R.id.bt_delete);
                                    if (delHisInfo(t) == 0) {
                                        show_Toast(getString(R.string.xingbang_main_page_btn_del) + t + getString(R.string.text_success));
                                    }
                                } else {
                                    show_Toast(getResources().getString(R.string.text_mmcw));
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
//                                }).create();
//                        dialog.show();

                        break;
                }
            }

            @Override
            public void onItemClick(View view, int position) {
                VoFireHisMain vo = list_savedate.get(position);
                if (vo != null) {
                    createDialog(vo);//
                } else {
                    show_Toast(getString(R.string.text_error_tip54));
                }
            }
        });
        mHandler_2 = new Handler(this.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (pb_show == 1 && tipDlg != null) tipDlg.show();
                if (pb_show == 0 && tipDlg != null) {
                    tipDlg.hide();
                    showLoadMore();
                }
            }
        };
        mHandler_tip = new Handler(msg -> {

            switch (msg.what) {
                case 1:
                    show_Toast(getResources().getString(R.string.text_xzsb16));
                    break;
                case 2:
                    show_Toast(getResources().getString(R.string.text_dlsccg));
                    break;
                case 3:
                    show_Toast(getResources().getString(R.string.text_xzsb17) + msg.obj);
                    break;
                case 4:
                    show_Toast(getResources().getString(R.string.text_uploda_tip3));
                    break;
                case 5:
                    String tip = "";
                    switch (msg.obj.toString()) {
                        case "0":
                            tip = "成功";
                            break;
                        case "1":
                            tip = "非法的申请信息";
                            break;
                        case "2":
                            tip = "未找到该起爆器设备信息";
                            break;
                        case "3":
                            tip = "该起爆器未设置作业任务";
                            break;
                        case "4":
                            tip = "起爆器在黑名单中";
                            break;
                        case "5":
                            tip = "起爆位置不在起爆区域内";
                            break;
                        case "6":
                            tip = "起爆位置在禁爆区域内";
                            break;
                        case "7":
                            tip = "该起爆器已注销/报废";
                            break;
                        case "8":
                            tip = "禁爆任务";
                            break;
                    }
                    show_Toast(tip);
                    break;
            }
            return false;
        });
        mHandler_update = new Handler(msg -> {
            Object result = msg.obj;
            updataState(result + "");//更新上传状态
            updataState_sq_dl(result + "");
            int pos = msg.arg1;
            list_savedate.get(pos).setUploadStatus("已上传");
            hisAdapter.setDataSource(list_savedate);
//            showLoadMore();
            hisAdapter.notifyItemChanged(pos);
//            mAdapter.notifyDataSetChanged();
            return false;
        });
        hideInputKeyboard();
    }

    public void hideInputKeyboard() {
        denatorDelMainpage.requestFocus();//获取焦点,
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    //获取配置文件中的值
    private void getPropertiesData() {
        Shangchuan = (String) MmkvUtils.getcode("Shangchuan", "否");
//        if(changjia.equals("XJ")){
//            Shangchuan="否";
//        }else {
//            Shangchuan="是";
//        }
        Log.e("是否上传错误雷管", "changjia: " + changjia);
        Log.e("是否上传错误雷管", "Shangchuan: " + Shangchuan);
        Utils.writeRecord("==是否上传错误雷管:" + Shangchuan);
        AppLogUtils.writeAppLog("==是否上传错误雷管:" + Shangchuan);
    }

    private void getUserMessage() {
        MessageBean bean = GreenDaoMaster.getAllFromInfo_bean();
        pro_bprysfz = bean.getPro_bprysfz();
        pro_htid = bean.getPro_htid();
        pro_xmbh = bean.getPro_xmbh();
        equ_no = bean.getEqu_no();
        pro_coordxy = bean.getPro_coordxy();
        server_addr = bean.getServer_addr();
        server_port = bean.getServer_port();
        server_http = bean.getServer_http();
        server_ip = bean.getServer_ip();
        server_type1 = bean.getServer_type1();
        server_type2 = bean.getServer_type2();
        pro_dwdm = bean.getPro_dwdm();
    }

    private void runPbDialog() {//Loading界面
        pb_show = 1;
        tipDlg = new LoadingDialog(QueryHisDetail.this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
        //tipDlg.setMessage("正在操作,请等待...").show();
        // tipDlg.show();

        new Thread(() -> {
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
        }).start();
    }

    /**
     * 更新历史记录的上传信息状态
     */
    public int modifyUploadStatus(String id, String uploadStatus) {
        ContentValues values = new ContentValues();
        values.put("uploadStatus", uploadStatus);
        db.update(DatabaseHelper.TABLE_NAME_HISMAIN, values, "blastdate=?", new String[]{"" + id});
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }

    /**
     * 更新丹灵网上传信息状态
     */
    public void updataState_sq_dl(String blastdate) {
        Log.e("更新起爆状态-丹灵", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("dl_state", "已上传");
        db.update(DatabaseHelper.TABLE_NAME_SHOUQUAN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 更新中爆网上传信息状态
     */
    public void updataState_sq_zb(String blastdate, int pos) {
        Log.e("更新起爆状态-中爆", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("zb_state", "已上传");
        db.update(DatabaseHelper.TABLE_NAME_SHOUQUAN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把软存中的数据存入磁盘中

        Message message = new Message();
        message.obj = blastdate;
        message.arg1 = pos;
        mHandler_update.sendMessage(message);
    }

    private void loadMoreData(int cp) {
        list_savedate.clear();
        getDaoSession().clear();
        List<DenatorHis_Main> list = getDaoSession().getDenatorHis_MainDao().queryBuilder().orderDesc(DenatorHis_MainDao.Properties.Id).list();
        for (int i = 0; i < list.size(); i++) {
            VoFireHisMain item = new VoFireHisMain();
            item.setBlastdate(list.get(i).getBlastdate());
            item.setFiredNo(list.get(i).getEqu_no());
            item.setLatitude(list.get(i).getLatitude());
            item.setLongitude(list.get(i).getLongitude());
            item.setRemark(list.get(i).getRemark());
            item.setSerialNo(list.get(i).getSerialNo() + "");
            item.setUploadStatus(list.get(i).getUploadStatus());
            item.setUserid(list.get(i).getPro_bprysfz());
            item.setProjectName(list.get(i).getUserid());
            item.setId(list.get(i).getId() + "");
            item.setProjectNo(list.get(i).getPro_htid());
            item.setDwdm(list.get(i).getPro_dwdm());
            item.setXmbh(list.get(i).getPro_xmbh());
            item.setLog(list.get(i).getLog());
            list_savedate.add(item);
        }
    }


    private void showLoadMore() {//刷新页面
//        loadMoreData(currentPage);
        hisAdapter.setDataSource(list_savedate);
//        mAdapter.notifyDataSetChanged();
    }


    private String updata(String blastdate, String htid, final String jd, final String wd) {
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 1; i < hisListData.size(); i++) {
            list_uid.add(hisListData.get(i).get("shellNo") + "O");//中爆网雷管以O为分割(英文O)
        }
        if (htid == null) {
            htid = pro_htid;
        }
        if (equ_no.length() > 8) {//中爆网起爆器编号8位
//            String qbq_no = equ_no.substring(0, 5) + equ_no.substring(8);//截取起爆器编号前5位和后3位
            String qbq_no = equ_no.substring(1, 3) + equ_no.substring(5);//截取起爆器编号前23位和后6位
            return Utils.uploadFireData(QueryHisDetail.this, list_uid, pro_bprysfz, htid, pro_xmbh, (jd + "," + wd), server_type2, qbq_no, server_ip, server_port, server_http, blastdate);
        }
        return Utils.uploadFireData(QueryHisDetail.this, list_uid, pro_bprysfz, htid, pro_xmbh, (jd + "," + wd), server_type2, equ_no, server_ip, server_port, server_http, blastdate);

    }

    /**
     * 删除历史记录
     */
    private int delHisInfo(String blastdate) {
        if (blastdate == null) return 1;
        if (getString(R.string.text_alert_tip3).equals(blastdate)) {
            show_Toast(getString(R.string.text_error_tip52));
            return 1;
        }
        new GreenDaoMaster().deleteType(blastdate);//删除生产数据中对应的雷管
        //从表
        String selection = "blastdate = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {blastdate + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        db.delete(DatabaseHelper.TABLE_NAME_HISDETAIL, selection, selectionArgs);
        //主表
        db.delete(DatabaseHelper.TABLE_NAME_HISMAIN, selection, selectionArgs);

        if (list_savedate != null && list_savedate.size() > 0) {

            for (int i = list_savedate.size() - 1; i >= 0; i--) {
                VoFireHisMain vo = list_savedate.get(i);
                if (blastdate.equals(vo.getBlastdate())) {
                    list_savedate.remove(vo);
                }
            }
        }
        showLoadMore();
        return 0;
    }


    /**
     * 上传核对
     */
    private void modifyFactoryInfo(final String blastdate, final int id, final String htbh, final String jd, final String wd, final String xmbh, final String dwdm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QueryHisDetail.this);
        // builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请核对上传信息");//"请修改厂家信息"
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(QueryHisDetail.this).inflate(R.layout.update_message, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        final EditText dialog_xmbh = (EditText) view.findViewById(R.id.dialog_xmbh);
        final EditText dialog_htbh = (EditText) view.findViewById(R.id.dialog_htbh);
        final EditText dialog_dwdm = (EditText) view.findViewById(R.id.dialog_dwdm);
        final EditText dialog_jwd = (EditText) view.findViewById(R.id.dialog_jwd);
        final EditText dialog_sfz = (EditText) view.findViewById(R.id.dialog_sfz);
        dialog_xmbh.setText(xmbh);
        dialog_htbh.setText(htbh);
        dialog_dwdm.setText(dwdm);
        dialog_jwd.setText(jd + "," + wd);
        dialog_sfz.setText(pro_bprysfz);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {


        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> {

        });
        builder.show();
    }


    /**
     * 更新上传状态
     *
     * @param fireDateId
     */
    private void performUp(final String fireDateId, final int pos, final String htid, final String jd, final String wd) {
        new Thread(() -> {
            Looper.prepare();
            if (updata(fireDateId, htid, jd, wd).equals("0")) {//updata是上传中爆网方法成功返回0
                modifyUploadStatus(fireDateId, getString(R.string.text_query_uploaded));//
                updataState_sq_zb(fireDateId, pos);

                if (list_savedate != null && list_savedate.size() > 0) {
                    for (int i = list_savedate.size() - 1; i >= 0; i--) {
                        VoFireHisMain vo = list_savedate.get(i);
                        if (fireDateId.equals(vo.getBlastdate())) {
                            vo.setUploadStatus(getString(R.string.text_query_uploaded));
                        }
                    }
                }
            }
            pb_show = 0;
            Looper.loop();
        }).start();
    }

    /**
     * 获取起爆历史详细信息
     */
    private void getHisDetailList(String blastdate, int type) {
        hisListData.clear();
        Map<String, Object> item = new HashMap<>();
        item.put("no", getString(R.string.text_list_Serial));//"序号"
//        item.put("serialNo", getString(R.string.text_list_Serial));//"序号"
        item.put("piece", getString(R.string.text_list_piace));//"区域"
        item.put("kongNo", getString(R.string.text_list_kong));//"孔号（pai-blastserial-duanNo）"
        item.put("shellNo", getString(R.string.text_list_guan));//"管壳码"
        item.put("delay", "" + getString(R.string.text_list_delay));//"延时"
        item.put("errorName", getString(R.string.text_list_state));//"状态"
        hisListData.add(item);
        Cursor cursor;
        int a = 1;
        if (type == 1) {
            cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null, null, null, null, null);
        } else {
            if (Shangchuan.equals("是")) {
                String selection = "blastdate = ?"; // 选择条件，给null查询所有//+" and errorCode = ?"   new String[]{"FF"}
                String[] selectionArgs = {blastdate};//选择条件参数,会把选择条件中的？替换成这个数组中的值
                cursor = db.query(DatabaseHelper.TABLE_NAME_HISDETAIL, null, selection, selectionArgs, null, null, "piece asc");
            } else {
                String selection = "blastdate = ?and errorCode  like ? "; // 选择条件，给null查询所有//+" and errorCode = ?"   new String[]{"FF"}
                String[] selectionArgs = {blastdate, "F%"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
                cursor = db.query(DatabaseHelper.TABLE_NAME_HISDETAIL, null, selection, selectionArgs, null, null, "piece asc");
            }
        }
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                String shellNo = cursor.getString(3);//管壳号
                String errorName = cursor.getString(8);//错误信息
                int delay = cursor.getInt(5); //延时
                String piece = cursor.getString(15); //区域
                int duanNo = cursor.getInt(17); //段位号
                String pai = cursor.getString(18); //排
                String kongNo = pai + "-" + serialNo + "-" + duanNo;
                item = new HashMap<>();
                item.put("no", a);
//                item.put("serialNo", serialNo);
                item.put("kongNo", kongNo);
                item.put("shellNo", shellNo);
                item.put("delay", "" + delay);
                item.put("piece", "" + piece);
                if (errorName == null || errorName.trim().length() < 1) errorName = " ";
                item.put("errorName", errorName);
                hisListData.add(item);
                a++;
            }
            cursor.close();
        }
        Utils.saveFile();//把闪存中的数据存入磁盘中
    }

    /**
     * 更新上传信息状态
     */
    public void updataState(String blastdate) {
        Log.e("更新起爆状态", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("uploadStatus", "已上传");
        db.update(DatabaseHelper.TABLE_NAME_HISMAIN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把闪存中的数据存入磁盘中
    }

    /**
     * 添加错误日志
     */
    private void updatalog(String blastdate, String err) {
        GreenDaoMaster master = new GreenDaoMaster();
        DenatorHis_Main his_main = master.queryDetonatorForMainHis(blastdate);
        his_main.setLog(his_main.getLog() + "\n" + err);
        getDaoSession().getDenatorHis_MainDao().update(his_main);
    }

    /***
     * 建立雷管信息表对话框
     */
    public void createDialog(VoFireHisMain voFireHisMain) {
        LayoutInflater inflater = LayoutInflater.from(QueryHisDetail.this);
        getlistview = inflater.inflate(R.layout.query_his_detail_listview, null);
        int flag = 0;
        if (getString(R.string.text_alert_tip3).equals(voFireHisMain.getBlastdate())) {//"当前雷管记录"
            flag = 1;
        }
        getHisDetailList(voFireHisMain.getBlastdate(), flag);//获取起爆历史详细信息
        // 给ListView绑定内容
        ListView listview = getlistview.findViewById(R.id.his_detail_listview);
        TextView txtView = getlistview.findViewById(R.id.his_detail_count);
        int count = hisListData.size();
        if (count > 0) count -= 1;
        txtView.setText(getString(R.string.text_alert_tip4) + count);//"雷管总数:"
        SimpleAdapter adapter = new SimpleAdapter(QueryHisDetail.this, hisListData, R.layout.query_his_detail_item,
                new String[]{"no", "piece", "kongNo", "shellNo", "delay", "errorName"},
                new int[]{R.id.X_item_no, R.id.X_item_piece, R.id.X_item_kongNo, R.id.X_item_shellno,
                        R.id.X_item_delay, R.id.X_item_errorname});
        // 给listview加入适配器
        listview.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (flag == 1)
            builder.setTitle(getString(R.string.text_alert_tip5));//"当前雷管列表"
        else
            builder.setTitle(getString(R.string.text_alert_tip6));//"已爆雷管列表"
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> dialog.dismiss());
//        builder.setNeutralButton("日志", (dialog, which) -> {//不让客户看见比较好
//            Intent intent = new Intent(QueryHisDetail.this, WriteLogActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("log",voFireHisMain.getLog());
//            intent.putExtras(bundle);
//            startActivity(intent);
//        });
        builder.create().show();

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

//        Utils.saveFile();//把软存中的数据存入磁盘中
        super.onDestroy();
        fixInputMethodManagerLeak(this);
    }

    /***
     * 加载雷管信息
     */
    private int getBlastModelCount() {

        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO, null);
        int totalNum = cursor.getCount();//得到数据的总条数

        //cursor不位空,可以移动到第一行
        cursor.close();
        return totalNum;
    }

    /**
     * 保存起爆结果
     */
    public synchronized void saveFireResult(String fireDate) {

        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO, null);
        int totalNum = cursor.getCount();//得到数据的总条数
        if (totalNum < 1) return;

        ContentValues values = new ContentValues();
        int maxNo = getHisMaxNumberNo();
        maxNo++;

        //hisInsertFireDate = fireDate;

        values.put("blastdate", fireDate);
        values.put("uploadStatus", "0");
        values.put("longitude", "0");
        values.put("latitude", "0");
        values.put("remark", "");
        values.put("userid", pro_bprysfz);
        values.put("equ_no", equ_no);
        values.put("serialNo", "" + maxNo);
        db.insert("denatorHis_Main", null, values);
        VoFireHisMain item = new VoFireHisMain();
        item.setBlastdate(fireDate);
        item.setFiredNo(equ_no);
        item.setLatitude("0");
        item.setLongitude("0");
        item.setRemark("");
        item.setSerialNo("" + maxNo);
        item.setUploadStatus(getString(R.string.text_query_up));//"未上传"
        item.setUserid(pro_bprysfz);
        item.setId("0");
        list_savedate.add(item);
        cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null, null, null, null, " blastserial asc");
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                values.clear();
                values.put("blastserial", cursor.getInt(1));
                values.put("sithole", cursor.getInt(2));
                values.put("shellBlastNo", cursor.getString(3));
                values.put("denatorId", cursor.getString(4));
                values.put("delay", cursor.getInt(5));
                values.put("statusCode", cursor.getString(6));
                values.put("statusName", cursor.getString(7));
                values.put("errorName", cursor.getString(8));
                values.put("errorCode", cursor.getString(9));
                values.put("authorization", cursor.getString(10));
                values.put("remark", cursor.getString(11));
                values.put("blastdate", fireDate);
                db.insert("denatorHis_Detail", null, values);
            }
            cursor.close();
        }
        Utils.saveFile();//把闪存中的数据存入磁盘中
        // db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO,null,null);
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
        Utils.saveFile();//把闪存中的数据存入磁盘中
    }

    /**
     * 获取最大序号
     */
    private int getHisMaxNumberNo() {
        Cursor cursor = db.rawQuery("select max(serialNo) from " + DatabaseHelper.TABLE_NAME_HISMAIN, null);
        if (cursor != null && cursor.moveToNext()) {
            String maxStr = cursor.getString(0);
            int maxNo = 0;
            if (maxStr != null && maxStr.trim().length() > 0) {
                maxNo = Integer.parseInt(maxStr);
            }
            cursor.close();
            return maxNo;
        }
        return 1;

    }

    /**
     * 丹灵上传方法
     */
    private void upload(final String blastdate, final int pos, final String htid, final String jd, final String wd, final String xmbh, final String dwdm) {
        final String key = "jadl12345678912345678912";
        String url = Utils.httpurl_upload_dl;//丹灵上传
//        String url = Utils.httpurl_upload_test;//丹灵上传

        // 创建OkHttpClient.Builder对象
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // 设置连接超时时间：50秒
        builder.connectTimeout(50, TimeUnit.SECONDS);

        // 设置读取超时时间：50秒
        builder.readTimeout(50, TimeUnit.SECONDS);

        // 设置写入超时时间：50秒
        builder.writeTimeout(50, TimeUnit.SECONDS);

        // 创建OkHttpClient实例
        OkHttpClient client = builder.build();

//        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 1; i < hisListData.size(); i++) {
            list_uid.add(hisListData.get(i).get("shellNo") + "");
        }

        //四川uid转换规则
//        if (list_uid.get(0).length() < 14) {
//            for (int i = 0; i < list_uid.size(); i++) {
//                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan(list_uid.get(i)));//替换
////                    Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan_new(list_uid.get(i)));//替换
//            }
//        }
        //丹灵新uid转换规则
//        if (list_uid != null && list_uid.get(0).length() < 14) {
//            for (int i = 0; i < list_uid.size(); i++) {
//                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toNewddanling(list_uid.get(i)));//替换
//            }
//        }

        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e("上传uid", uid);
        String xy[] = pro_coordxy.split(",");//经纬度
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
            if (htid != null) {
                object.put("htid", htid);//合同编号
            } else {
                object.put("htid", pro_htid);//合同编号
            }
            if (dwdm != null) {
                object.put("dwdm", dwdm);//合同编号
            } else {
                object.put("dwdm", pro_dwdm);//单位代码
            }
            if (xmbh != null) {
                object.put("xmbh", xmbh);//合同编号
            } else {
                object.put("xmbh", pro_xmbh);//项目编号
            }
            object.put("bpsj", "20" + blastdate.replace("/", "-").replace(",", " "));//爆破时间blastdate.replace("/","-").replace(","," ")
            object.put("bprysfz", pro_bprysfz);//人员身份证
            object.put("uid", uid);//雷管uid


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
                pb_show = 0;
                Log.e("网络请求", "IOException: " + e);

                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject object;
                try {
                    if (!server_type2.equals("2")) {
                        pb_show = 0;
                    }
                    object = new JSONObject(response.body().string());
                    Log.e("上传", "丹灵返回: " + object.toString());
                    String success = object.getString("success");
                    if (success.equals("true")) {
                        Message message = new Message();
                        message.obj = blastdate;
                        message.arg1 = pos;
                        mHandler_update.sendMessage(message);
                        if (!server_type2.equals("2")) {
                            pb_show = 0;
                        }
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));

                    } else if (success.equals("fail")) {
                        String cwxx = object.getString("cwxx");
                        Message msg = new Message();
                        msg.what = 5;
                        msg.obj = cwxx;
                        mHandler_tip.sendMessage(msg);
//                        if (cwxx.equals("1")) {
//                            Message msg = new Message();
//                            msg.what=3;
//                            msg.obj=cwxx;
//                            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
//                        } else if (cwxx.equals("2")) {
//                            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
//                        } else {
//                            Message msg = new Message();
//                            msg.what=5;
//                            msg.obj=cwxx;
//                            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(5));
//                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void upload_xingbang(final String blastdate, final int pos, final String htid, final String jd, final String wd, final String xmbh, final String dwdm, final String qbxm_name, final String log) {
        final String key = "jadl12345678912345678912";
        String url = Utils.httpurl_xb_his;//公司服务器上传

        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 1; i < hisListData.size(); i++) {
            list_uid.add(hisListData.get(i).get("shellNo").toString() + "#" + hisListData.get(i).get("delay") + "#" + hisListData.get(i).get("errorName"));
        }
        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e("上传uid", uid);
        String xy[] = pro_coordxy.split(",");//经纬度
        String app_verson_name;
        changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
        if (changjia.equals("XJ")) {
            app_verson_name = getString(R.string.app_version_name2);
        } else if (changjia.equals("CQ")) {
            app_verson_name = getString(R.string.app_version_name3);
        } else {
            app_verson_name = getString(R.string.app_version_name);
        }
        try {
            object.put("sbbh", equ_no);//起爆器设备编号
            if (jd != null) {
                object.put("jd", jd);//经度
            } else if (pro_coordxy.length() > 5) {
                object.put("jd", xy[0]);//经度
            }
            if (wd != null) {
                object.put("wd", wd);//纬度
            } else if (pro_coordxy.length() > 5) {
                object.put("wd", xy[1]);//纬度
            }
            if (htid != null) {
                object.put("htid", htid);//合同编号
            } else {
                object.put("htid", pro_htid);//合同编号
            }
            object.put("bpsj", blastdate.replace("/", "-").replace(",", " "));//爆破时间blastdate.replace("/","-").replace(","," ")
            object.put("bprysfz", pro_bprysfz);//人员身份证
            object.put("uid", uid);//雷管uid
            object.put("dwdm", pro_dwdm);//单位代码
            object.put("xmbh", pro_xmbh);//项目编号
            object.put("log", log);//日志
            object.put("log_cmd", Utils.readLog_cmd(blastdate.split(" ")[0].replace("/", "-")));//日志
            object.put("yj_version", MmkvUtils.getcode("yj_version", "KT50_V1.3_17V_V1.3.18.bin"));//硬件版本
            PackageInfo pi = this.getPackageManager().getPackageInfo(Application.getContext().getPackageName(), 0);
            object.put("rj_version", app_verson_name);//软件版本
            object.put("name", qbxm_name);//项目名称
            Log.e("上传信息-项目名称", qbxm_name);
        } catch (JSONException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = MyUtils.getBase64(MyUtils.encryptMode(key.getBytes(), object.toString().getBytes()));
        JSONObject object2 = new JSONObject();
        try {
            object2.put("param", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = FormBody.create(JSON, object2.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json; charset=utf-8")//text/plain  application/json  application/x-www-form-urlencoded
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                Log.e("上传公司网络请求", "IOException: " + e);
                Utils.writeLog("煋邦网络上传错误-IOException:" + e);
                AppLogUtils.writeAppXBLog("煋邦网络上传错误-IOException:" + e);
                updatalog(blastdate, "煋邦网络上传错误-IOException:" + e);
                Message msg = new Message();
                msg.what = 5;
                msg.obj = getResources().getString(R.string.text_xbscsb);
                mHandler_tip.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("上传", "返回: " + response.toString());
                pb_show = 0;
            }
        });
    }

    private boolean isSelectAll = true;//是否全选

    @OnClick({R.id.btn_del_return, R.id.btn_del_all, R.id.tv_check_all, R.id.tv_input, R.id.tv_cancel})
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
                        .setTitle(getResources().getString(R.string.text_queryHis_dialog1))//设置对话框的标题//"成功起爆"
                        .setMessage(getResources().getString(R.string.text_queryHis_dialog11))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNegativeButton(getResources().getString(R.string.text_alert_cancel), (dialog1, which) -> dialog1.dismiss())
                        .setPositiveButton(getResources().getString(R.string.text_queryHis_dialog10), (dialog12, which) -> {
                            dialog12.dismiss();
                            db.delete(DatabaseHelper.TABLE_NAME_HISDETAIL, null, null);
                            //主表
                            db.delete(DatabaseHelper.TABLE_NAME_HISMAIN, null, null);
                            if (list_savedate != null && list_savedate.size() > 0) {
                                for (int i = list_savedate.size() - 1; i > 0; i--) {
                                    VoFireHisMain vo = list_savedate.get(i);
                                    list_savedate.remove(vo);
                                }
                            }
                            showLoadMore();
                        }).create();
                dialog.show();

                break;
            case R.id.tv_cancel:
                layBottom.setVisibility(View.GONE);
                hisAdapter.showCheckBox(false);
                layBottom.setVisibility(View.GONE);
                tv_check_all.setText(getResources().getString(R.string.text_qx));
                isSelectAll = true;
                setAllItemChecked(false);
                break;
            case R.id.tv_check_all://全选
                if (isSelectAll) {
                    tv_check_all.setText(getResources().getString(R.string.text_qxqx));
                    isSelectAll = false;
                    setAllItemChecked(true);
                } else {
                    tv_check_all.setText(getResources().getString(R.string.text_qx));
                    isSelectAll = true;
                    setAllItemChecked(false);
                }
                break;
            case R.id.tv_input:
                if (list_savedate.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_selectjl));
                    return;
                }
                List<VoFireHisMain> selectList = new ArrayList<>();
                for (VoFireHisMain hisMain : list_savedate) {
                    if (hisMain.isSelect()) {
                        selectList.add(hisMain);
                    }
                }
                if (selectList.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_selectjl));
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(QueryHisDetail.this);
                builder.setTitle(getResources().getString(R.string.text_queryHis_dialog1));//"请输入用户名和密码"
                View v = LayoutInflater.from(QueryHisDetail.this).inflate(R.layout.userlogindialog_delete, null);
                builder.setView(v);
                final EditText password = v.findViewById(R.id.password);
                builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog1, which) -> {

                    String b = password.getText().toString().trim();
                    if (b == null || b.trim().length() < 1) {
                        show_Toast(getString(R.string.text_alert_password));
                        return;
                    }
                    Log.e("删除已上传记录", "list_savedate.size() : " + list_savedate.size());
                    if (b.equals("123")) {
                        for (VoFireHisMain data : selectList) {
                            if (data.isSelect()) {
                                GreenDaoMaster master = new GreenDaoMaster();
                                master.deleteType(data.getBlastdate());//删除生产数据中对应的雷管
                                master.deleteForHis(data.getBlastdate());
                                master.deleteForDetail(data.getBlastdate());
                            }
                        }
                        show_Toast(getResources().getString(R.string.text_del_ok));
                        loadMoreData(currentPage);//读取数据
                        showLoadMore();
                        dialog1.dismiss();
                        loadMoreData(currentPage);//读取数据
                        showLoadMore();
                        AppLogUtils.writeAppLog("点击了多选删除起爆历史记录按钮");
                        layBottom.setVisibility(View.GONE);
                        hisAdapter.showCheckBox(false);
                        layBottom.setVisibility(View.GONE);
                        tv_check_all.setText(getResources().getString(R.string.text_qx));
                        isSelectAll = true;
                        setAllItemChecked(false);
                    } else {
                        show_Toast(getResources().getString(R.string.text_mmcw));
                    }
                    dialog1.dismiss();
                });
                builder.setNeutralButton(getString(R.string.text_alert_cancel), (dialog2, which) -> dialog2.dismiss());
                builder.show();
                break;
        }
    }

    //区域全选和取消全选
    private void setAllItemChecked(boolean isSelected) {
        if (hisAdapter == null) return;
        if (isSelected) {
            for (VoFireHisMain data : list_savedate) {
                data.setSelect(true);
            }
        } else {
            for (VoFireHisMain data : list_savedate) {
                data.setSelect(false);
            }
        }
        hisAdapter.notifyDataSetChanged();
    }


    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hisdelete, menu);
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
        switch (item.getItemId()) {

            case R.id.item_1:
                AlertDialog.Builder builder = new AlertDialog.Builder(QueryHisDetail.this);
                builder.setTitle(getResources().getString(R.string.text_queryHis_dialog1));//"请输入用户名和密码"
                View view = LayoutInflater.from(QueryHisDetail.this).inflate(R.layout.userlogindialog_delete, null);
                builder.setView(view);
                final EditText password = view.findViewById(R.id.password);
                builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {

                    String b = password.getText().toString().trim();
                    if (b == null || b.trim().length() < 1) {
                        show_Toast(getString(R.string.text_alert_password));
                        return;
                    }
                    Log.e("删除已上传记录", "list_savedate.size() : " + list_savedate.size());
                    if (b.equals("123")) {
                        AppLogUtils.writeAppLog("点击了'删除所有已上传记录'按钮");
                        List<DenatorHis_Main> list = getDaoSession().getDenatorHis_MainDao().queryBuilder().orderDesc(DenatorHis_MainDao.Properties.Id).list();
                        GreenDaoMaster master = new GreenDaoMaster();
                        for (DenatorHis_Main his : list) {
                            if (his.getUploadStatus().equals("已上传")) {
                                master.deleteType(his.getBlastdate());//删除生产数据中对应的雷管
                                master.deleteForHis(his.getBlastdate());
                                master.deleteForDetail(his.getBlastdate());
                            }
                        }

                        show_Toast(getResources().getString(R.string.text_his_scyscjl));
                    } else {
                        show_Toast(getResources().getString(R.string.text_mmcw));
                    }
                    loadMoreData(currentPage);//读取数据
                    showLoadMore();
                    dialog.dismiss();
                });
                builder.setNeutralButton(getString(R.string.text_alert_cancel), (dialog, which) -> dialog.dismiss());


                builder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
