package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getContext;
import static android_serialport_api.xingbang.Application.getDaoSession;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.greendao.query.Query;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.AppDetaiLogAdapter;
import android_serialport_api.xingbang.custom.AppErrorLogAdapter;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.ErrLog;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.SysLog;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_MainDao;
import android_serialport_api.xingbang.db.greenDao.ErrLogDao;
import android_serialport_api.xingbang.db.greenDao.SysLogDao;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.NetUtils;
import android_serialport_api.xingbang.utils.OkhttpClientUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class AppDetailLogActivity extends BaseActivity {
    @BindView(R.id.tvNormalLog)
    TextView tvNormalLog;
    @BindView(R.id.tvErrorLog)
    TextView tvErrorLog;
    @BindView(R.id.rvNormalLog)
    RecyclerView rvNormalLog;
    @BindView(R.id.rvErrorLog)
    RecyclerView rvErrorLog;
    @BindView(R.id.tvDate)
    TextView tvDate;
    private SQLiteDatabase db;
    private Handler mHandler_2 = new Handler();//显示进度条
    private Handler mHandler_tip = new Handler();//提示
    private Handler mHandler_update = new Handler();//更新状态
    private LoadingDialog tipDlg = null;
    private int pb_show = 0;
    private DatabaseHelper mMyDatabaseHelper;
    private String changjia = "TY";
    private int totalNum;//总的数据条数
    private int pageSize = 600;//每页显示的数据
    private int totalPage;//总的页数
    private int currentPage = 1;//当前页数
    private String Shangchuan;
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
    private List<SysLog> normalLogList = new ArrayList<>();
    private AppDetaiLogAdapter appLogAdapter;
    private AppErrorLogAdapter errorAdapter;
    private String TAG = "程序日志上传页面";
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private ArrayList<String> list_uid = new ArrayList<>();
    private String mRegion;     // 区域
    private List<ErrLog> errorLogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_log);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        AppLogUtils.writeAppLog("--进入程序日志上传页面--");
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        tipDlg = new LoadingDialog(AppDetailLogActivity.this);
        changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
        Log.e(TAG, "changjia: " + changjia);
        initData();
    }

    private void initData() {
        getUserMessage();//获取用户信息
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mListData = new GreenDaoMaster().queryDetonatorRegionAsc(mRegion);
        Log.e(TAG, "程序日志上传--查询雷管:mListData: " + mListData.toString());
        list_uid.clear();
        for (int i = 0; i < mListData.size(); i++) {
            list_uid.add(mListData.get(i).getShellBlastNo());
        }
        Log.e(TAG, "程序日志上传--查询雷管:list_uid: " + list_uid.toString());
        getPropertiesData();//第二种获取用户信息
        totalNum = getDaoSession().getSysLogDao().loadAll().size();//得到数据的总条数
        totalPage = (int) Math.ceil(totalNum / (float) pageSize);//通过计算得到总的页数
        normalLogList = new GreenDaoMaster().getAppLogList();
        // 线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvNormalLog.setLayoutManager(linearLayoutManager);
        appLogAdapter = new AppDetaiLogAdapter(this, normalLogList);
        rvNormalLog.setAdapter(appLogAdapter);
        appLogAdapter.setOnItemClickListener(new AppDetaiLogAdapter.OnItemClickListener() {
            @Override
            public void onButtonClicked(View v, int position) {
                switch (v.getId()) {
                    case R.id.bt_delete:
                        //先弹出是否确认删除项目dialog  确定后执行删除操作
                        if (!AppDetailLogActivity.this.isFinishing()) {
                            AlertDialog dialog = new AlertDialog.Builder(AppDetailLogActivity.this)
                                    .setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题
                                    .setMessage(getResources().getString(R.string.text_scriz))//设置对话框的内容
                                    //设置对话框的按钮
                                    .setNeutralButton(getResources().getString(R.string.text_dialog_qx), (dialog1, which) -> {
                                        dialog1.dismiss();
                                    })
                                    .setPositiveButton(getString(R.string.text_dialog_qd), (dialog14, which) -> {
                                        deleteNormalLogs(normalLogList.get(position).getFilename(), normalLogList.get(position).getId());
                                    }).create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                        break;
                    case R.id.bt_upload:
                        if (equ_no.length() < 1) {
                            show_Toast("当前设备编号为空,请先设置设备编号");
                            return;
                        }
                        if (!NetUtils.haveNetWork(getContext())) {
                            show_Toast(getResources().getString(R.string.text_jcwl));
                            return;
                        }
                        int pos = (Integer) v.getTag(R.id.bt_upload);//位置
                        String blastdate = normalLogList.get(pos).getUpdataTime();//日期
                        String htbh = pro_htid;//合同编号
                        String dwdm = pro_dwdm;//单位代码
                        String xmbh = pro_xmbh;//项目编号
                        String[] xy = pro_coordxy.split(",");//经纬度
                        String jd;//经度
                        String wd;//纬度
                        if (pro_coordxy != null && pro_coordxy.length() > 5) {
                            jd = xy[0];//经度
                            wd = xy[1];//纬度
                        } else {
                            jd = "";//经度
                            wd = "";//纬度
                        }
                        String qbxm_name = "APP详细错误日志";//项目名称
                        String log = AppLogUtils.getLogsByDate(1,normalLogList.get(position).getUpdataTime());//日志
                        String log_cmd = AppLogUtils.getLogsByDate(2,normalLogList.get(position).getUpdataTime());//日志
                        if (pro_coordxy.length() < 2 && jd == null) {
                            show_Toast("经纬度为空，不能执行上传");
                            return;
                        }
                        AppLogUtils.writeAppLog("日常日志点击了上传按钮");
                        pb_show = 1;
                        runPbDialog();//loading画面
                        upload_xingbang(1,position,blastdate, htbh, jd, wd, xmbh, dwdm, qbxm_name, log, log_cmd);//我们自己的网址
                        break;
                }
            }

            @Override
            public void onItemClick(View v, int position) {

            }
        });
        errorLogList = new GreenDaoMaster().getAppErrorLogList();
        // 线性布局
        LinearLayoutManager errLManager = new LinearLayoutManager(this);
        rvErrorLog.setLayoutManager(errLManager);
        errorAdapter = new AppErrorLogAdapter(this, errorLogList);
        rvErrorLog.setAdapter(errorAdapter);
        errorAdapter.setOnItemClickListener(new AppErrorLogAdapter.OnItemClickListener() {
            @Override
            public void onButtonClicked(View v, int position) {
                switch (v.getId()) {
                    case R.id.bt_delete:
                        deleteErrorLogs(errorLogList.get(position).getId());
                        break;
                    case R.id.bt_upload:
                        if (equ_no.length() < 1) {
                            show_Toast("当前设备编号为空,请先设置设备编号");
                            return;
                        }
                        if (!NetUtils.haveNetWork(getContext())) {
                            show_Toast(getResources().getString(R.string.text_jcwl));
                            return;
                        }
                        int pos = (Integer) v.getTag(R.id.bt_upload);//位置
                        String blastdate = errorLogList.get(pos).getUpdataTime();//日期
                        String htbh = pro_htid;//合同编号
                        String dwdm = pro_dwdm;//单位代码
                        String xmbh = pro_xmbh;//项目编号
                        String[] xy = pro_coordxy.split(",");//经纬度
                        String jd;//经度
                        String wd;//纬度
                        if (pro_coordxy != null && pro_coordxy.length() > 5) {
                            jd = xy[0];//经度
                            wd = xy[1];//纬度
                        } else {
                            jd = "";//经度
                            wd = "";//纬度
                        }
                        String qbxm_name = "APP详细崩溃日志";//项目名称
                        String log = Utils.readOffline(errorLogList.get(position).getPath());//日志
                        String log_cmd = "";//日志
                        if (pro_coordxy.length() < 2 && jd == null) {
                            show_Toast("经纬度为空，不能执行上传");
                            return;
                        }
                        AppLogUtils.writeAppLog("崩溃日志点击了上传按钮");
                        pb_show = 1;
                        runPbDialog();//loading画面
                        upload_xingbang(2,position,blastdate, htbh, jd, wd, xmbh, dwdm, qbxm_name, log, log_cmd);//我们自己的网址
                        break;
                }
            }

            @Override
            public void onItemClick(View view, int position) {

            }
        });
        mHandler_tip = new Handler(msg -> {
            switch (msg.what) {
                case 1:
                    show_Toast("上传成功");
                    break;
            }
            return false;
        });
        mHandler_update = new Handler(msg -> {
            switch (msg.what) {
                case 1:
                    int pos = msg.arg1;
                    updateUpStateByDate(normalLogList.get(pos).getId(),1);
                    normalLogList.get(pos).setUpdataState("已上传");
                    refreshData(1);
                    appLogAdapter.notifyItemChanged(pos);
                    break;
                case 2:
                    int pos1 = msg.arg1;
                    updateUpStateByDate(errorLogList.get(pos1).getId(),2);
                    errorLogList.get(pos1).setUpdataState("已上传");
                    refreshData(2);
                    errorAdapter.notifyItemChanged(pos1);
                    break;
            }
            return false;
        });
    }

    // 由于列表展示是查询出相同前缀的数据后，只展示最新的一条记录即可，所以删除要删除掉相同前缀filename的所有数据
    // 根据传入的完整 filename 删除所有以相同前缀开始的日志
    public void deleteNormalLogs(String filename,Long id) {
        // 提取前缀（假设前缀是 filename 的前8个字符，可以根据需求调整）
        String prefix = filename.substring(0, 10);
        // 获取 SysLogDao 实例
        SysLogDao sysLogDao = getDaoSession().getSysLogDao();
        // 查询所有以该前缀开头的日志记录
        List<SysLog> logsToDelete = new GreenDaoMaster().deleteAppLogsById(id);
        // 如果有匹配的日志记录，进行删除
        if (!logsToDelete.isEmpty()) {
            // 批量删除符合条件的日志
            sysLogDao.deleteInTx(logsToDelete);
            if (normalLogList != null && normalLogList.size() > 0) {
                for (int i = normalLogList.size() - 1; i >= 0; i--) {
                    SysLog sys = normalLogList.get(i);
                    if (id == sys.getId()) {
                        normalLogList.remove(sys);
                    }
                }
            }
            show_Toast(getResources().getString(R.string.text_del_ok));
            refreshData(1);
        } else {
            Log.e(TAG, "没有找到匹配的日志记录，前缀: " + prefix);
        }
    }


    // 由于列表展示是查询出相同前缀的数据后，只展示最新的一条记录即可，所以删除要删除掉相同前缀filename的所有数据
    // 根据传入的完整 filename 删除所有以相同前缀开始的日志
    public void deleteErrorLogs(Long id) {
        // 提取前缀（假设前缀是 filename 的前8个字符，可以根据需求调整）
        // 获取 SysLogDao 实例
        ErrLogDao errLogDao = getDaoSession().getErrLogDao();
        // 查询所有以该前缀开头的日志记录
        List<ErrLog> logsToDelete = new GreenDaoMaster().deleteAppErrorLogsById(id);
        // 如果有匹配的日志记录，进行删除
        if (!logsToDelete.isEmpty()) {
            // 批量删除符合条件的日志
            errLogDao.deleteInTx(logsToDelete);
            if (errorLogList != null && errorLogList.size() > 0) {
                for (int i = errorLogList.size() - 1; i >= 0; i--) {
                    ErrLog sys = errorLogList.get(i);
                    if (id == sys.getId()) {
                        errorLogList.remove(sys);
                    }
                }
            }
            show_Toast(getResources().getString(R.string.text_del_ok));
            refreshData(2);
        } else {
            Log.e(TAG, "崩溃日志删除:未找到匹配的日志记录");
        }
    }


    // 更新指定日期的日志状态为“已上传”
    public void updateUpStateByDate(Long id,int type) {
        if (type == 1) {
            new GreenDaoMaster().updateAppLog(id);
            appLogAdapter.notifyDataSetChanged();
        } else {
            new GreenDaoMaster().updateErrorAppLog(id);
            errorAdapter.notifyDataSetChanged();
        }
    }

    private void refreshData(int type) {
        if (type == 1) {
            Log.e(TAG,"刷新:" + normalLogList.toString());
            appLogAdapter.setDataSource(normalLogList);
        } else {
            Log.e(TAG,"刷新:" + errorLogList.toString());
            errorAdapter.setDataSource(errorLogList);
        }
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
        tipDlg = new LoadingDialog(AppDetailLogActivity.this);
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

    //获取配置文件中的值
    private void getPropertiesData() {
        Shangchuan = (String) MmkvUtils.getcode("Shangchuan","否");
//        if (changjia.equals("XJ")) {
//            Shangchuan = "否";
//        } else {
//            Shangchuan = "是";
//        }
        Log.e("是否上传错误雷管", "changjia: " + changjia);
        Log.e("是否上传错误雷管", "Shangchuan: " + Shangchuan);
        Utils.writeRecord("==是否上传错误雷管:" + Shangchuan);
    }

    @OnClick({R.id.tvNormalLog, R.id.tvErrorLog})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvNormalLog:
                setViewColor(1);
                break;
            case R.id.tvErrorLog:
                setViewColor(2);
                break;
        }
    }

    private void setViewColor(int type) {
        if (type == 1) {
            refreshData(1);
            rvNormalLog.setVisibility(View.VISIBLE);
            rvErrorLog.setVisibility(View.GONE);
            tvNormalLog.setBackgroundResource(R.drawable.blue_deep_style);
            tvErrorLog.setBackgroundResource(R.drawable.gray_style);
            tvNormalLog.setTextColor(Color.WHITE);
            tvErrorLog.setTextColor(Color.BLACK);
        } else {
            refreshData(2);
            rvNormalLog.setVisibility(View.GONE);
            rvErrorLog.setVisibility(View.VISIBLE);
            tvErrorLog.setBackgroundResource(R.drawable.blue_deep_style);
            tvNormalLog.setBackgroundResource(R.drawable.gray_style);
            tvErrorLog.setTextColor(Color.WHITE);
            tvNormalLog.setTextColor(Color.BLACK);
        }
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hisdelete, menu);
        MenuItem item1 = menu.findItem(R.id.item_1);
        item1.setVisible(false); // 显示特定菜单项
        // 初始时确保菜单项是可见的
        MenuItem item2 = menu.findItem(R.id.item_2);
        item2.setVisible(true); // 显示特定菜单项
        MenuItem item3 = menu.findItem(R.id.item_3);
        item3.setVisible(true); // 显示特定菜单项
        return true;
    }

    /**
     * 打开菜单
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 在菜单准备好时，进一步确保菜单项可见
        MenuItem item1 = menu.findItem(R.id.item_1);
        item1.setVisible(false); // 显示特定菜单项
        // 初始时确保菜单项是可见的
        MenuItem item2 = menu.findItem(R.id.item_2);
        item2.setVisible(true); // 显示特定菜单项
        MenuItem item3 = menu.findItem(R.id.item_3);
        item3.setVisible(true); // 显示特定菜单项
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 点击item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_2:
                SysLogDao sysLogDao = getDaoSession().getSysLogDao();
                // 查询所有 updataState 为 "已上传" 的记录
                Query<SysLog> query = sysLogDao.queryBuilder()
                        .where(SysLogDao.Properties.UpdataState.eq("已上传"))
                        .build();
                List<SysLog> uploadedLogs = query.list(); // 获取符合条件的记录列表
                if (uploadedLogs.isEmpty()) {
                    // 如果没有已上传的数据，提示用户
                    show_Toast(getResources().getString(R.string.text_his_zwsjsc));
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AppDetailLogActivity.this);
                builder.setTitle(getResources().getString(R.string.text_queryHis_dialog1));//"请输入用户名和密码"
                View view = LayoutInflater.from(AppDetailLogActivity.this).inflate(R.layout.userlogindialog_delete, null);
                builder.setView(view);
                final EditText password = view.findViewById(R.id.password);
                builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {
                    String b = password.getText().toString().trim();
                    if (b == null || b.trim().length() < 1) {
                        show_Toast(getString(R.string.text_alert_password));
                        return;
                    }
                    if (b.equals("123")) {
                        // 使用事务批量删除
                        sysLogDao.getDatabase().beginTransaction();
                        try {
                            for (SysLog log : uploadedLogs) {
                                sysLogDao.delete(log); // 删除符合条件的记录
                            }
                            sysLogDao.getDatabase().setTransactionSuccessful(); // 设置事务成功
                            show_Toast("已删除 " + uploadedLogs.size() + " 条已上传的数据");
                        } finally {
                            sysLogDao.getDatabase().endTransaction(); // 结束事务
                        }
                        if (normalLogList != null && normalLogList.size() > 0) {
                            for (int i = normalLogList.size() - 1; i >= 0; i--) {
                                SysLog sys = normalLogList.get(i);
                                if ("已上传".equals(sys.getUpdataState())) {
                                    normalLogList.remove(sys);
                                }
                            }
                            refreshData(1);
                            show_Toast(getResources().getString(R.string.text_his_scyscjl));
                        }
                    } else {
                        show_Toast(getResources().getString(R.string.text_mmcw));
                    }
                    dialog.dismiss();
                });
                builder.setNeutralButton(getString(R.string.text_alert_cancel), (dialog, which) -> dialog.dismiss());
                builder.show();
                return true;
            case R.id.item_3:
                ErrLogDao errorSysLogDao = getDaoSession().getErrLogDao();
                // 查询所有 updataState 为 "已上传" 的记录
                Query<ErrLog> errQuery = errorSysLogDao.queryBuilder()
                        .where(ErrLogDao.Properties.UpdataState.eq("已上传"))
                        .build();
                List<ErrLog> errUploadedLogs = errQuery.list(); // 获取符合条件的记录列表
                if (errUploadedLogs.isEmpty()) {
                    // 如果没有已上传的数据，提示用户
                    show_Toast(getResources().getString(R.string.text_his_zwsjsc));
                    return false;
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AppDetailLogActivity.this);
                builder1.setTitle(getResources().getString(R.string.text_queryHis_dialog1));//"请输入用户名和密码"
                View view1 = LayoutInflater.from(AppDetailLogActivity.this).inflate(R.layout.userlogindialog_delete, null);
                builder1.setView(view1);
                final EditText password1 = view1.findViewById(R.id.password);
                builder1.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {
                    String b = password1.getText().toString().trim();
                    if (b == null || b.trim().length() < 1) {
                        show_Toast(getString(R.string.text_alert_password));
                        return;
                    }
                    if (b.equals("123")) {
                        // 使用事务批量删除
                        errorSysLogDao.getDatabase().beginTransaction();
                        try {
                            for (ErrLog log : errUploadedLogs) {
                                errorSysLogDao.delete(log); // 删除符合条件的记录
                            }
                            errorSysLogDao.getDatabase().setTransactionSuccessful(); // 设置事务成功
                            show_Toast("已删除 " + errUploadedLogs.size() + " 条已上传的数据");
                        } finally {
                            errorSysLogDao.getDatabase().endTransaction(); // 结束事务
                        }
                        if (errorLogList != null && errorLogList.size() > 0) {
                            for (int i = errorLogList.size() - 1; i >= 0; i--) {
                                ErrLog errLog = errorLogList.get(i);
                                if ("已上传".equals(errLog.getUpdataState())) {
                                    errorLogList.remove(errLog);
                                }
                            }
                            refreshData(2);
                            show_Toast(getResources().getString(R.string.text_his_scyscjl));
                        }
                    } else {
                        show_Toast(getResources().getString(R.string.text_mmcw));
                    }
                    dialog.dismiss();
                });
                builder1.setNeutralButton(getString(R.string.text_alert_cancel), (dialog, which) -> dialog.dismiss());
                builder1.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void upload_xingbang(final int type,int pos,String blastdate, final String htid, final String jd, final String wd, final String xmbh, final String dwdm, final String qbxm_name, final String log, final String log_cmd) {
        final String key = "jadl12345678912345678912";
        String url = Utils.httpurl_xb_his;//公司服务器上传
        OkHttpClient client = OkhttpClientUtils.getInstance();
        JSONObject object = new JSONObject();
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 0; i < mListData.size(); i++) {//上传页面从1开始,是因为单独添加了个表头,其他从0开始
            list_uid.add(mListData.get(i).getShellBlastNo() + "#" + mListData.get(i).getDelay() + "#" + mListData.get(i).getErrorName());
        }
        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e("上传uid", uid);
        Log.e("上传list_uid", list_uid.toString());
        Log.e("上传mListData", mListData.toString());
        String xy[] = pro_coordxy.split(",");//经纬度
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
            String app_verson_name ;
            changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
            if(changjia.equals("XJ")){
                app_verson_name =getString(R.string.app_version_name2);
            }else if(changjia.equals("CQ")){
                app_verson_name =getString(R.string.app_version_name3);
            }else {
                app_verson_name =getString(R.string.app_version_name);
            }
            object.put("bpsj", blastdate.replace("/", "-").replace(",", " "));//爆破时间blastdate.replace("/","-").replace(","," ")
            object.put("bprysfz", pro_bprysfz);//人员身份证
            object.put("uid", uid);//雷管uid
            object.put("dwdm", pro_dwdm);//单位代码
            object.put("xmbh", pro_xmbh);//项目编号
            object.put("log", log);//日志
            object.put("log_cmd", log_cmd);//日志
            object.put("yj_version", MmkvUtils.getcode("yj_version", "默认版本"));//硬件版本
            object.put("rj_version", app_verson_name);//软件版本
            if (qbxm_name != null && qbxm_name.length() > 1) {
                object.put("name", qbxm_name);//项目名称
            } else {
                object.put("name", MmkvUtils.getcode("pro_name", ""));//项目名称
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG,"上传入参:" + object.toString());
//        Utils.writeRecord("上传入参:" + object.toString());
        //3des加密
        String json = MyUtils.getBase64(MyUtils.encryptMode(key.getBytes(), object.toString().getBytes()));
        JSONObject object2 = new JSONObject();
        try {
            object2.put("param", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Utils.writeRecord("上传加密后的入参:" + json);
        OkhttpClientUtils.post(2, url, object2.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                Log.e("上传公司网络请求", "IOException: " + e);
                AppLogUtils.writeAppXBLog("程序日志上传煋邦出错:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("上传", "返回: " + response.toString());
                pb_show = 0;
                if (type == 1) {
                    Message message = new Message();
                    message.what = 1;
                    message.arg1 = pos;
                    mHandler_update.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 2;
                    message.arg1 = pos;
                    mHandler_update.sendMessage(message);
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
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
}
