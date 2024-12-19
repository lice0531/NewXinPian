package android_serialport_api.xingbang.firingdevice;
import static android_serialport_api.xingbang.Application.getDaoSession;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_MainDao;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailLogActivity extends BaseActivity {
    @BindView(R.id.tvNormalLog)
    TextView tvNormalLog;
    @BindView(R.id.tvErrorLog)
    TextView tvErrorLog;
    @BindView(R.id.rvNormalLog)
    RecyclerView rvNormalLog;
    @BindView(R.id.rvErrorLog)
    RecyclerView rvErrorLog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_log);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        tipDlg = new LoadingDialog(DetailLogActivity.this);
        changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
        Log.e("上传", "changjia: "+changjia );

        getUserMessage();//获取用户信息
        getPropertiesData();//第二种获取用户信息
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
        tipDlg = new LoadingDialog(DetailLogActivity.this);
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
//        Shangchuan = (String) MmkvUtils.getcode("Shangchuan","否");
        if(changjia.equals("XJ")){
            Shangchuan="否";
        }else {
            Shangchuan="是";
        }
        Log.e("是否上传错误雷管", "changjia: "+changjia );
        Log.e("是否上传错误雷管", "Shangchuan: "+Shangchuan );
        Utils.writeRecord("==是否上传错误雷管:"+Shangchuan);
    }

    @OnClick({R.id.tvNormalLog,R.id.tvErrorLog})
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
            rvNormalLog.setVisibility(View.VISIBLE);
            rvErrorLog.setVisibility(View.GONE);
            tvNormalLog.setBackgroundResource(R.drawable.blue_deep_style);
            tvErrorLog.setBackgroundResource(R.drawable.gray_style);
            tvNormalLog.setTextColor(Color.WHITE);
            tvErrorLog.setTextColor(Color.BLACK);
        } else {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailLogActivity.this);
                builder.setTitle(getResources().getString(R.string.text_queryHis_dialog1));//"请输入用户名和密码"
                View view = LayoutInflater.from(DetailLogActivity.this).inflate(R.layout.userlogindialog_delete, null);
                builder.setView(view);
                final EditText password = view.findViewById(R.id.password);
                builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {

                    String b = password.getText().toString().trim();
                    if (b == null || b.trim().length() < 1) {
                        show_Toast(getString(R.string.text_alert_password));
                        return;
                    }
//                    Log.e("删除已上传记录", "list_savedate.size() : "+list_savedate.size() );
                    if ( b.equals("123")) {
                        List<DenatorHis_Main> list = getDaoSession().getDenatorHis_MainDao().queryBuilder().orderDesc(DenatorHis_MainDao.Properties.Id).list();
                        GreenDaoMaster master = new GreenDaoMaster();
                        for (DenatorHis_Main his:list) {
                            if(his.getUploadStatus().equals("已上传")){
                                master.deleteType(his.getBlastdate());//删除生产数据中对应的雷管
                                master.deleteForHis(his.getBlastdate());
                                master.deleteForDetail(his.getBlastdate());
                            }
                        }

                        show_Toast(getResources().getString(R.string.text_his_scyscjl));
                    } else {
                        show_Toast(getResources().getString(R.string.text_mmcw));
                    }
//                    loadMoreData(currentPage);//读取数据
//                    showLoadMore();
                    dialog.dismiss();
                });
                builder.setNeutralButton(getString(R.string.text_alert_cancel), (dialog, which) -> dialog.dismiss());
                builder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
