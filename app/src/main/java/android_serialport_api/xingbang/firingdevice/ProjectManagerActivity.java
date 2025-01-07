package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_TRACKER_EN;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.kfree.comm.system.ScanQrControl;
import com.orhanobut.logger.Logger;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.EncryptionUtils;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.QRCodeUtils;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.ThreeDES;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class ProjectManagerActivity extends BaseActivity {
    @BindView(R.id.tvDeviceNo)
    TextView tvDeviceNo;
    @BindView(R.id.add_gsxz)
    Spinner addGsxz;
    @BindView(R.id.down_at_xmbh)
    AutoCompleteTextView downAtXmbh;
    @BindView(R.id.down_at_htid)
    AutoCompleteTextView downAtHtid;
    @BindView(R.id.down_at_dwdm)
    AutoCompleteTextView downAtDwdm;
    @BindView(R.id.down_at_project_name)
    AutoCompleteTextView downAtProjectName;
    @BindView(R.id.down_at_coordxy)
    AutoCompleteTextView downAtCoordxy;
    @BindView(R.id.down_at_bprysfz)
    AutoCompleteTextView downAtBprysfz;
    @BindView(R.id.ll_xmxx)
    LinearLayout llXmxx;
    @BindView(R.id.ll_dwxx)
    LinearLayout llDwxx;
    private String select_business;
    private String TAG = "项目管理页面";
    private String pageFlag = "";//根据不同情况进入页面显示右上角的文字不一样
    private String htbh = "",xmbh = "",coordxy = "",business = "",project_name = "",bprysfz = "",dwdm = "";
    private SQLiteDatabase db;
    private DatabaseHelper mMyDatabaseHelper;
    private TextView totalbar_title;
    private ScanInterface scanDecode;
    private ScanQrControl mScaner = null;
    private ScanBar scanBarThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_manager);
        ButterKnife.bind(this);
        AppLogUtils.writeAppLog("--进入到项目管理页面--");
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        SQLiteStudioService.instance().start(this);
        SoundPlayUtils.init(this);
        initData();
        //初始化扫码功能
        scan();
    }

    private void initData() {
        pageFlag = !TextUtils.isEmpty(getIntent().getStringExtra("xmPageFlag")) ?
                getIntent().getStringExtra("xmPageFlag") : "";
        htbh = !TextUtils.isEmpty(getIntent().getStringExtra("htbh")) ?
                getIntent().getStringExtra("htbh") : "";
        xmbh = !TextUtils.isEmpty(getIntent().getStringExtra("xmbh")) ?
                getIntent().getStringExtra("xmbh") : "";
        project_name = !TextUtils.isEmpty(getIntent().getStringExtra("project_name")) ?
                getIntent().getStringExtra("project_name") : "";
        dwdm = !TextUtils.isEmpty(getIntent().getStringExtra("dwdm")) ?
                getIntent().getStringExtra("dwdm") : "";
        bprysfz = !TextUtils.isEmpty(getIntent().getStringExtra("bprysfz")) ?
                getIntent().getStringExtra("bprysfz") : "";
        coordxy = !TextUtils.isEmpty(getIntent().getStringExtra("coordxy")) ?
                getIntent().getStringExtra("coordxy") : "";
        business = !TextUtils.isEmpty(getIntent().getStringExtra("business")) ?
                getIntent().getStringExtra("business") : "";
        Log.e(TAG,"性质:" + business + "--pageFlag:" + pageFlag);
        totalbar_title =  findViewById(R.id.title_text);
        TextView tv_right = findViewById(R.id.title_right);
        ImageView title_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        title_add.setVisibility(View.GONE);
        totalbar_title.setText("项目管理");
        iv_back.setOnClickListener(v -> finish());
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        SpinnerAdapter adapter= ArrayAdapter.createFromResource(this, R.array.gsxz_name,android.R.layout.simple_spinner_dropdown_item);
        addGsxz.setAdapter(adapter);
        addGsxz.setSelection(0);
        if (!TextUtils.isEmpty(pageFlag)) {
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setText("删除项目");
            downAtProjectName.setText(project_name);
            downAtHtid.setText(htbh);
            downAtXmbh.setText(xmbh);
            downAtDwdm.setText(dwdm);
            downAtCoordxy.setText(coordxy);
            downAtBprysfz.setText(bprysfz);
            if (business.startsWith("非营业性")) {
                addGsxz.setSelection(0);
                llXmxx.setVisibility(View.GONE);
                llDwxx.setVisibility(View.VISIBLE);
                Log.e(TAG,"进来非营业性了。。");
            } else {
                llXmxx.setVisibility(View.VISIBLE);
                llDwxx.setVisibility(View.GONE);
                addGsxz.setSelection(1);
                Log.e(TAG,"进来营业性了。。");
            }
        } else {
            addGsxz.setSelection(0);
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setText("扫码新增项目");
            llXmxx.setVisibility(View.GONE);
            llDwxx.setVisibility(View.VISIBLE);
        }
        tv_right.setOnClickListener(v -> {
            if (tv_right.getText().toString().trim().contains("删除")) {
                //先弹出是否确认删除项目dialog  确定后执行删除操作
                if (!ProjectManagerActivity.this.isFinishing()) {
                    AlertDialog dialog = new AlertDialog.Builder(ProjectManagerActivity.this)
                            .setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题
                            .setMessage(getResources().getString(R.string.text_scxm))//设置对话框的内容
                            //设置对话框的按钮
                            .setNeutralButton(getResources().getString(R.string.text_dialog_qx), (dialog1, which) -> {
                                dialog1.dismiss();
                            })
                            .setPositiveButton(getString(R.string.text_dialog_qd), (dialog14, which) -> {
                                //此时再执行删除项目功能
                                if (!TextUtils.isEmpty(pageFlag) && "detail".equals(pageFlag)) {
                                    EventBus.getDefault().post(new FirstEvent("finishDetailPage"));
                                }
                                delShouQuan(project_name);//删除方法
                                AppLogUtils.writeAppLog("点击了删除项目");
                                finish();
                            })
                            .create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            } else {
                AppLogUtils.writeAppLog("点击了扫码新增项目按钮");
                if (scanBarThread != null) {
                    scanBarThread.exit = true;  // 终止线程thread
                    try {
                        scanBarThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                kaishiScan();
                //kt50持续扫码线程
                scanBarThread = new ScanBar();
                scanBarThread.start();
            }
        });
        MessageBean messageBean = GreenDaoMaster.getAllFromInfo_bean();
        String equ_no = messageBean.getEqu_no();
        Log.e("起爆器编号", "equ_no: " + equ_no);
        if (!equ_no.equals("")) {
            tvDeviceNo.setText(getString(R.string.text_query_num) + equ_no);
            CrashReport.setUserId(equ_no);
        }

        addGsxz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] delay = getResources().getStringArray(R.array.gsxz_name);
                select_business = delay[i];
                Log.e(TAG ,"公司性质选择的是:" + select_business);
                if (i == 0) {
                    llXmxx.setVisibility(View.GONE);
                    llDwxx.setVisibility(View.VISIBLE);
                } else {
                    llXmxx.setVisibility(View.VISIBLE);
                    llDwxx.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        initAutoComplete("history_projectName", downAtProjectName);
        initAutoComplete("history_htid", downAtHtid);
        initAutoComplete("history_xmbh", downAtXmbh);
        initAutoComplete("history_dwdm", downAtDwdm);
        initAutoComplete("history_coordxy", downAtCoordxy);
        initAutoComplete("history_bprysfz", downAtBprysfz);
    }

    /**
     * 扫码注册方法/扫描头返回方法
     */
    String key = "jadl12345678912345678912";
    private void scan() {
        switch (Build.DEVICE) {
            // KT50 起爆器设备
            case "T-QBZD-Z6":
            case "M900": {
                // 创建扫描头操作对象，并注册回调
                mScaner = new ScanQrControl(this);
                mScaner.registerScanCb(new ScanQrControl.IScan() {
                    @Override
                    public void onScanStart(int timeoutSec) {
                    }

                    @Override
                    public void onScanResult(boolean isSuccess, String scanResultStr) {
                        Log.e("设备扫码结果: ", "ScanResult:" + isSuccess + "|" + scanResultStr);
                        Log.e(TAG,"设备扫码内容:" + scanResultStr);
                        Utils.writeLog("设备扫码出来项目信息是:" + scanResultStr);
                        tingzhiScan();
                        if (scanBarThread != null) {
                            scanBarThread.exit = true;  // 终止线程thread
                            try {
                                scanBarThread.join();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        if (scanResultStr != null && scanResultStr.length() > 7) {
                            String result = scanResultStr.substring(7);
                            try {
                                String res = ThreeDES.decryptThreeDESECB(result, key);
                                Log.e(TAG,"设备扫码出来的内容是:" + res);
                                Utils.writeLog("设备扫码解密后的结果是:" + res);
                                getScanDataShow(res);
                            } catch (Exception e) {
                                Utils.writeLog("设备解密失败:" + e.getMessage().toString());
                                Log.e(TAG,result + "设备解密失败:" + e.getMessage().toString());
                                throw new RuntimeException(e);
                            }
                        } else {
                            Log.e(TAG,"扫码结果长度不足7,不合规");
                        }
                    }
                });
                break;
            }
            default: {
                scanDecode = new ScanDecode(this);
                scanDecode.initService("true");//初始化扫描服务
                scanDecode.getBarCode(data -> {
                    Log.e("默认扫码结果: ", "ScanResult:" + data);
                    Log.e(TAG,"默认扫码内容:" + data);
                    Utils.writeLog("默认扫码出来项目信息是:" + data);
                    tingzhiScan();
                    if (scanBarThread != null) {
                        scanBarThread.exit = true;  // 终止线程thread
                        try {
                            scanBarThread.join();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (data != null && data.length() > 7) {
                        String result = data.substring(7);
                        try {
                            String res = ThreeDES.decryptThreeDESECB(result, key);
                            Log.e(TAG,"默认扫码出来的内容是:" + res);
                            Utils.writeLog("默认扫码解密后的结果是:" + res);
                            getScanDataShow(res);
                        } catch (Exception e) {
                            Utils.writeLog("默认解密失败:" + e.getMessage().toString());
                            Log.e(TAG,result + "默认解密失败:" + e.getMessage().toString());
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.e(TAG,"默认扫码结果长度不足7,不合规");
                    }
                });
            }
        }
    }

    private void kaishiScan() {
        switch (Build.DEVICE) {
            case "T-QBZD-Z6":
            case "M900": {
                //M900打开扫码
                mScaner.startScan();
                break;
            }
            case "ST327":
            case "S337": {
                //st327上电
                powerOnScanDevice(PIN_TRACKER_EN);//扫码头上电
                break;
            }
            default: {
                scanDecode.starScan();

            }
        }
    }

    private void tingzhiScan() {
        switch (Build.DEVICE) {
            case "T-QBZD-Z6":
            case "M900": {
                //M900关闭扫码
                mScaner.stopScan();
                break;
            }
            case "ST327":
            case "S337": {
                //st327扫码下电
                powerOffScanDevice(PIN_TRACKER_EN);//扫码头下电
                break;
            }
            default: {
                //kt50停止扫码头方法
                scanDecode.stopScan();//停止扫描
            }
        }
    }

    private void getScanDataShow(String content){
        // 如果字符串以分号结尾，移除它
        if (content.endsWith(";")) {
            content = content.substring(0, content.length() - 1);
        }
        // 将解密后的字符串根据分号分割为字段
        String[] pairs = content.split(";");
        // 创建一个 Map 来存储键值对
        Map<String, String> fieldMap = new HashMap<>();
        // 遍历每个字段，将其按冒号分割为键值对
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                // 将键值对存入 Map
                fieldMap.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        // 提取并打印具体字段
        String shtbh = fieldMap.get("htbh");
        String sxmbh = fieldMap.get("xmbh");
        String sprojectName = fieldMap.get("project_name");
        String sdwdm = fieldMap.get("dwdm");
        String sbprysfz = fieldMap.get("bprysfz");
        String scoordxy = fieldMap.get("coordxy");
        String sbusiness = fieldMap.get("business");
        // 输出结果
        Log.e(TAG,"htbh: " + shtbh);
        Log.e(TAG,"xmbh: " + sxmbh);
        Log.e(TAG,"project_name: " + sprojectName);
        Log.e(TAG,"dwdm: " + sdwdm);
        Log.e(TAG,"bprysfz: " + sbprysfz);
        Log.e(TAG,"coordxy: " + scoordxy);
        Log.e(TAG,"business: " + sbusiness);
        if (!TextUtils.isEmpty(sbusiness)) {
            SoundPlayUtils.play(1);
            if (sbusiness.startsWith("非营业性")) {
                addGsxz.setSelection(0);
                llXmxx.setVisibility(View.GONE);
                llDwxx.setVisibility(View.VISIBLE);
            } else {
                llXmxx.setVisibility(View.VISIBLE);
                llDwxx.setVisibility(View.GONE);
                addGsxz.setSelection(1);
            }
            downAtProjectName.setText(sprojectName);
            downAtHtid.setText(shtbh);
            downAtXmbh.setText(sxmbh);
            downAtDwdm.setText(sdwdm);
            downAtCoordxy.setText(scoordxy);
            downAtBprysfz.setText(sbprysfz);
        }
    }

    private class ScanBar extends Thread {
        public volatile boolean exit = false;

        public void run() {

            while (!exit) {
                try {
                    switch (Build.DEVICE) {
                        case "T-QBZD-Z6":
                        case "M900": {
                            mScaner.startScan();
                            break;
                        }
                        default: {
                            scanDecode.starScan();
                        }
                    }
                    Thread.sleep(1250);
                    //break;

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @OnClick({R.id.btn_down_inputOK,R.id.btn_down_ercode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_down_inputOK:
                hideInputKeyboard();//隐藏键盘
                if (downAtCoordxy.getText().toString().trim().length() < 1) {
                    show_Toast(getResources().getString(R.string.text_down_err3));
                    return;
                }
                if (downAtBprysfz.getText().toString().trim().length() < 1) {
                    show_Toast("人员证号不能为空!");
                    return;
                }
                saveData();
                break;
            case R.id.btn_down_ercode:
                Intent intent = new Intent(this,ProjectErCodeActivity.class);
                intent.putExtra("htbh",downAtHtid.getText().toString().trim());
                intent.putExtra("dwdm",downAtDwdm.getText().toString().trim());
                intent.putExtra("xmbh",downAtXmbh.getText().toString().trim());
                intent.putExtra("coordxy",downAtCoordxy.getText().toString().trim());
                intent.putExtra("business",select_business);
                intent.putExtra("project_name",downAtProjectName.getText().toString().trim());
                intent.putExtra("bprysfz",downAtBprysfz.getText().toString().trim());
                startActivity(intent);
                break;
        }
    }

    private int delShouQuan(String project_name) {//删除雷管
        String selection = "project_name = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {project_name + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        db.delete(DatabaseHelper.TABLE_NAME_PROJECT, selection, selectionArgs);

        SharedPreferences sp = getSharedPreferences("network_url", 0);
        String longhistory = sp.getString("history_projectName", "");

        sp.edit().remove("history_projectName");

        String[] hisArrays = longhistory.split("#");
        //去重
        ArrayList<String> his = new ArrayList();
        Set set = new HashSet();
        for (String str : hisArrays) {
            if (set.add(str)) {
                his.add(str);
            }
        }
        //删除选中项
        his.remove(project_name);
        Log.e("删除项目", "his: " + his.toString());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < his.size(); i++) {
            sb.insert(0, his.get(i) + "#");
        }
        Log.e("删除项目", "history_projectName: " + sb.toString());
        sp.edit().putString("history_projectName", sb.toString()).apply();
        show_Toast(getResources().getString(R.string.text_del_ok));
        initAutoComplete("history_projectName", downAtProjectName);
        return 0;
    }

    /**
     * 保存信息
     */
    private void saveData() {
        Log.e(TAG, "选中项" + addGsxz.getSelectedItem());
        String checstr = checkData();
        if (checstr.length()>0){
            show_Toast(checstr);
            return ;
        }
//        int totalNum = LitePal.count(Project.class);//得到数据的总条数
//        Logger.e("保存项目"+ "totalNum: "+totalNum );
//        //如果总数大于20,删除第一个数据
//        if(totalNum>=20){
//            Project pro=LitePal.findFirst(Project.class);//获取第一条记录
//            Logger.e("保存项目"+ "pro: "+pro.toString());
//            pro.delete();
//        }
        if (checstr == null || checstr.trim().length() < 1) {
            String a = downAtBprysfz.getText().toString().trim().replace(" ", "");
            String b = downAtHtid.getText().toString().trim().replace(" ", "");
            String c = downAtXmbh.getText().toString().trim().replace(" ", "");
            String d = downAtCoordxy.getText().toString().trim().replace("\n", "").replace("，", ",").replace(" ", "");
            String e = downAtDwdm.getText().toString().trim().replace(" ", "");
            String f = downAtProjectName.getText().toString().trim().replace(" ", "");
            if (!TextUtils.isEmpty(pageFlag)) {
                //该操作是更新已保存的项目信息
                Project project = Application.getDaoSession().getProjectDao().queryBuilder().where(ProjectDao.Properties.Project_name.eq(project_name)).unique();
                project.setBprysfz(a);
                project.setHtbh(b);
                project.setXmbh(c);
                project.setCoordxy(d);
                project.setDwdm(e);
                project.setProject_name(f);
                project.setBusiness(select_business);
                //先查询出之前使用中的项目，把状态改为未使用
                Application.getDaoSession().getProjectDao().update(project);
                Utils.writeLog("项目管理页面更新项目信息成功");
                if ("detail".equals(pageFlag)) {
                    Intent intent = new Intent();
                    intent.putExtra("projectName",downAtProjectName.getText().toString().trim());
                    setResult(1,intent);
                    Log.e(TAG,"更新详情项目了");
                }
            } else {
                //该操作新增项目信息
                Project project =new Project();
                project.setXmbh(c);
                project.setDwdm(e);
                project.setBprysfz(a);
                project.setHtbh(b);
                project.setCoordxy(d);
                project.setProject_name(f);
                project.setBusiness(select_business);
                project.setSelected("false");
                Application.getDaoSession().getProjectDao().insert(project);
                Utils.writeLog("项目管理页面新增项目信息成功");
            }
            show_Toast("数据保存成功");
            finish();
        } else {
            show_Toast(checstr);
        }
        saveHistory("history_projectName", downAtProjectName);//保存输入的项目编号
        saveHistory("history_xmbh", downAtXmbh);//保存输入的项目编号
        saveHistory("history_htid", downAtHtid);//保存输入的合同编号
        saveHistory("history_dwdm", downAtDwdm);//保存输入的单位代码
        saveHistory("history_bprysfz", downAtBprysfz);//保存输入的身份证号
        saveHistory("history_coordxy", downAtCoordxy);//保存输入的经纬度
        initAutoComplete("history_projectName", downAtProjectName);
        initAutoComplete("history_htid", downAtHtid);
        initAutoComplete("history_xmbh", downAtXmbh);
        initAutoComplete("history_dwdm", downAtDwdm);
        initAutoComplete("history_coordxy", downAtCoordxy);
        initAutoComplete("history_bprysfz", downAtBprysfz);
    }

    /****
     * 校验数据
     */
    private String checkData() {
        String sfz = downAtBprysfz.getText().toString().trim().replace(" ", "");
        String htid = downAtHtid.getText().toString().trim().replace(" ", "");
        String xmbh = downAtXmbh.getText().toString().trim().replace(" ", "");
        String coordxy = downAtCoordxy.getText().toString().trim().replace("\n", "").replace("，", ",").replace(" ", "");
        String dwdm = downAtDwdm.getText().toString().trim().replace(" ", "");
        String name = downAtProjectName.getText().toString().trim().replace(" ", "");
        if (htid.length() > 1 && htid.length() < 15) {
            return "当前合同编号小于15位,请重新输入";
        }
        if (xmbh.length() > 1 && xmbh.length() < 15) {
            return "当前项目编号小于15位,请重新输入";
        }
        if (sfz == null || sfz.length() < 18) {
            return "请输入爆破员身份证";
        }
        if (name == null) {
            return "请输入项目名称";
        }
        if (coordxy == null || coordxy.trim().length() < 8 || coordxy.indexOf(",") < 5) {
            return getResources().getString(R.string.text_down_tip11);
        }
        List<Project> newsList = LitePal.where("project_name = ?", name).find(Project.class);
        Log.e("项目保存", "newsList: " + newsList.toString());
        Log.e("项目保存", "size: " + newsList.size());
//        if (TextUtils.isEmpty(pageFlag)) {
//            if (newsList.size() > 0) {
//                return "项目名称重复";
//            } else {
//                return "";
//            }
//        }
        return "";
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
    protected void onDestroy() {
        super.onDestroy();
        SQLiteStudioService.instance().stop();
        if (db != null) db.close();
        if (mScaner != null) {
            mScaner.unregisterScanCb();
        }
        if (scanDecode != null) {
            scanDecode.stopScan();//停止扫描
            scanDecode.onDestroy();//回复初始状态
        }
        if (scanBarThread != null) {
            scanBarThread.exit = true;  // 终止线程thread
            try {
                scanBarThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}