package android_serialport_api.xingbang.firingdevice;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.services.sendmessge.MessageEvent;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.QRCodeUtils;
import android_serialport_api.xingbang.utils.ThreeDES;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;
public class ProjectDetailActivity extends BaseActivity {
    @BindView(R.id.down_at_xmbh)
    TextView downAtXmbh;
    @BindView(R.id.down_at_htid)
    TextView downAtHtid;
    @BindView(R.id.down_at_dwdm)
    TextView downAtDwdm;
    @BindView(R.id.down_at_project_name)
    TextView downAtProjectName;
    @BindView(R.id.down_at_coordxy)
    TextView downAtCoordxy;
    @BindView(R.id.down_at_bprysfz)
    TextView downAtBprysfz;
    @BindView(R.id.down_gsxz)
    TextView downAtGsxz;
    @BindView(R.id.ll_xmxx)
    LinearLayout llXmxx;
    @BindView(R.id.ll_dwxx)
    LinearLayout llDwxx;
    @BindView(R.id.btn_set_project)
    Button btnSetproject;
    @BindView(R.id.rlXmCode)
    RelativeLayout rlXmCode;
    private String select_business;
    private String TAG = "项目详情页面";
    private String htbh = "", xmbh = "", coordxy = "", business = "", project_name = "", bprysfz = "", dwdm = "";
    private SQLiteDatabase db;
    private DatabaseHelper mMyDatabaseHelper;
    private TextView totalbar_title;
    @BindView(R.id.ivXmCode)
    ImageView ivXmCode;
    private int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        ButterKnife.bind(this);
        AppLogUtils.writeAppLog("--进入到项目详情页面--");
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        SQLiteStudioService.instance().start(this);
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void initData() {
        htbh = !TextUtils.isEmpty(getIntent().getStringExtra("htbh")) ?
                getIntent().getStringExtra("htbh") : "";
        xmbh = !TextUtils.isEmpty(getIntent().getStringExtra("xmbh")) ?
                getIntent().getStringExtra("xmbh") : "";
        project_name = !TextUtils.isEmpty(getIntent().getStringExtra("project_name")) ?
                getIntent().getStringExtra("project_name") : "";
        dwdm = !TextUtils.isEmpty(getIntent().getStringExtra("dwdm")) ?
                getIntent().getStringExtra("dwdm") : "";
        Log.e(TAG,"DW:" + dwdm);
        bprysfz = !TextUtils.isEmpty(getIntent().getStringExtra("bprysfz")) ?
                getIntent().getStringExtra("bprysfz") : "";
        coordxy = !TextUtils.isEmpty(getIntent().getStringExtra("coordxy")) ?
                getIntent().getStringExtra("coordxy") : "";
        business = !TextUtils.isEmpty(getIntent().getStringExtra("business")) ?
                getIntent().getStringExtra("business") : "";
        Log.e(TAG, "公司性质:" + business);
        totalbar_title = findViewById(R.id.title_text);
        TextView tv_right = findViewById(R.id.title_right);
        ImageView title_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        title_add.setVisibility(View.GONE);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("编辑");
        totalbar_title.setText("项目详情");
        iv_back.setOnClickListener(v -> finish());
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //如果是点击了列表中的项目进入该页面，所有输入项不可编辑

        tv_right.setOnClickListener(v -> {
            //如果是使用中的项目，进入项目编辑页面
            Intent intent = new Intent(this, ProjectManagerActivity.class);
            intent.putExtra("xmPageFlag", "detail");
            intent.putExtra("htbh", htbh);
            intent.putExtra("dwdm", dwdm);
            intent.putExtra("xmbh", xmbh);
            intent.putExtra("coordxy", coordxy);
            intent.putExtra("business", business);
            intent.putExtra("project_name", project_name);
            intent.putExtra("bprysfz", bprysfz);
            startActivityForResult(intent,REQUEST_CODE);
        });
        getErCode(project_name);
    }

    private void getErCode(String pName) {
        Project project = Application.getDaoSession().getProjectDao().queryBuilder().where(ProjectDao.Properties.Project_name.eq(pName)).unique();
        String mPname = project.getProject_name();
        String mHtbh = project.getHtbh();
        String mXmbh = project.getXmbh();
        String mDwdm = project.getDwdm();
        String mCoordxy = project.getCoordxy();
        String mBprysfz = project.getBprysfz();
        String mBusiness = project.getBusiness();
        if (mBusiness.startsWith("非营业性")) {
            Log.e(TAG,"非营业性");
            llXmxx.setVisibility(View.GONE);
            llDwxx.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG,"营业性");
            llXmxx.setVisibility(View.VISIBLE);
            llDwxx.setVisibility(View.GONE);
        }
        downAtProjectName.setText(mPname);
        downAtHtid.setText(mHtbh);
        downAtXmbh.setText(mXmbh);
        downAtDwdm.setText(mDwdm);
        downAtCoordxy.setText(mCoordxy);
        downAtBprysfz.setText(mBprysfz);
        downAtGsxz.setText(mBusiness);
        String content = "htbh:" + mHtbh + ";xmbh:" + mXmbh + ";project_name:" + mPname
                + ";dwdm:" + mDwdm + ";bprysfz:" + mBprysfz + ";coordxy:" + mCoordxy + ";business:"
                + mBusiness;
        // 生成二维码
        Log.e(TAG,"加密前的项目信息:" + content);
        final String key = "jadl12345678912345678912";
        String ercontent = content.replace("\n", "");
        try {
            String encode = ThreeDES.encryptThreeDESECB(ercontent, key);
            Bitmap qrCodeBitmap = QRCodeUtils.generateQRCode(encode);
            ivXmCode.setImageBitmap(qrCodeBitmap);
        } catch (Exception e) {
            AppLogUtils.writeAppLog("二维码生成失败:" + e.getMessage().toString());
            Log.e(TAG,"生成加密二维码失败:" + e.getMessage().toString());
            throw new RuntimeException(e);
        }
    }

    @OnClick({R.id.btn_set_project, R.id.rlXmCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlXmCode:
                Intent intent = new Intent(this, ProjectErCodeActivity.class);
                intent.putExtra("htbh", htbh);
                intent.putExtra("dwdm", dwdm);
                intent.putExtra("xmbh", xmbh);
                intent.putExtra("coordxy", coordxy);
                intent.putExtra("business", business);
                intent.putExtra("project_name", project_name);
                intent.putExtra("bprysfz", bprysfz);
                startActivity(intent);
                break;
            case R.id.btn_set_project:
                AppLogUtils.writeAppLog("点击了'设置为当前项目'按钮");
                updateData();
                break;
        }
    }

    /**
     * 保存信息
     */
    private void updateData() {
        //先查询出之前使用中的项目，把状态改为未使用
        Project beforeProject = Application.getDaoSession().getProjectDao().queryBuilder().where(ProjectDao.Properties.Selected.eq("true")).unique();
        if (beforeProject != null) {
            beforeProject.setSelected("false");
            Application.getDaoSession().getProjectDao().update(beforeProject);
        }
        //接着将本项目改为使用中  起爆器只能有一个项目可以为使用中
        Project project = Application.getDaoSession().getProjectDao().queryBuilder().where(ProjectDao.Properties.Project_name.eq(project_name)).unique();
        project.setSelected("true");
        Application.getDaoSession().getProjectDao().update(project);
        finish();
    }

    //隐藏键盘
    public void hideInputKeyboard() {
        downAtProjectName.clearFocus();//取消焦点
        downAtBprysfz.clearFocus();//取消焦点
        downAtDwdm.clearFocus();
        downAtHtid.clearFocus();
        downAtXmbh.clearFocus();
        downAtCoordxy.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 判断请求码是否匹配
        if (requestCode == REQUEST_CODE) {
            if (resultCode == 1) {
                // 获取传递过来的数据
                String pName = data.getStringExtra("projectName");
                Log.e(TAG,"传值得到的项目名称:" + pName);
                getErCode(pName);
                project_name = pName;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FirstEvent event) {
        Log.e(TAG,"详情页面eventBus: " + event.getMsg());
        if (!TextUtils.isEmpty(event.getMsg()) && "finishDetailPage".equals(event.getMsg())) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SQLiteStudioService.instance().stop();
        if (db != null) db.close();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
            Log.e(TAG,"EVENTBUS取消注册");
        }
    }
}