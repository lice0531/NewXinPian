package android_serialport_api.xingbang.firingdevice;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.MlistView;
import android_serialport_api.xingbang.custom.SaveProjectAdapter;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.Project;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaveProjectActivity extends BaseActivity implements SaveProjectAdapter.InnerItemOnclickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.btn_down_return)
    Button btnDownReturn;
    @BindView(R.id.btn_down_inputOK)
    Button btnDownInputOK;
    @BindView(R.id.down_at_htid)
    AutoCompleteTextView at_htid;
    @BindView(R.id.btn_clear_htid)
    Button btnClearHtid;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.down_at_xmbh)
    AutoCompleteTextView at_xmbh;
    @BindView(R.id.btn_clear_xmbh)
    Button btnClearXmbh;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.down_at_dwdm)
    AutoCompleteTextView at_dwdm;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.textView10)
    TextView textView10;
    @BindView(R.id.down_at_coordxy)
    AutoCompleteTextView at_coordxy;
    @BindView(R.id.btn_location)
    Button btnLocation;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.down_at_bprysfz)
    AutoCompleteTextView at_bprysfz;
    @BindView(R.id.btn_clear_sfz)
    Button btnClearSfz;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.ly_setUpdata)
    LinearLayout lySetUpdata;
    @BindView(R.id.lv_project)
    MlistView lvProject;
    @BindView(R.id.down_at_project_name)
    AutoCompleteTextView at_projectName;
    @BindView(R.id.btn_clear_project_name)
    Button btnClearProjectName;
    @BindView(R.id.btn_clear_dwdm)
    Button btnClearDwdm;
    @BindView(R.id.sv_sp)
    ScrollView svSp;
    @BindView(R.id.btn_down_offline)
    Button btnOffline;

    private SaveProjectAdapter mAdapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private List<Map<String, Object>> map_project = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_project);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        loadMoreData();
        mAdapter = new SaveProjectAdapter(this, map_project, R.layout.item_list_saveproject);
        mAdapter.setOnInnerItemOnClickListener(this);
        lvProject.setAdapter(mAdapter);
        lvProject.setOnItemClickListener(this);

        initAutoComplete("history_htid", at_htid);//输入历史记录
        initAutoComplete("history_projectName", at_projectName);//输入历史记录
        initAutoComplete("history_xmbh", at_xmbh);
        initAutoComplete("history_dwdm", at_dwdm);
        initAutoComplete("history_bprysfz", at_bprysfz);
        initAutoComplete("history_coordxy", at_coordxy);

        at_htid.addTextChangedListener(htbh_watcher);//长度监听
        at_xmbh.addTextChangedListener(xmbh_watcher);//长度监听
        at_bprysfz.addTextChangedListener(sfz_watcher);//长度监听
        at_dwdm.addTextChangedListener(dwdm_watcher);//长度监听
    }

    private void loadMoreData() {
        map_project.clear();
        GreenDaoMaster daoMaster = new GreenDaoMaster();
        List<Project> list_pj = daoMaster.queryProject();
        for (Project project : list_pj) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", project.getId());
            item.put("htbh", project.getHtbh());
            item.put("xmbh", project.getXmbh());
            item.put("coordxy", project.getCoordxy());
            item.put("project_name", project.getProject_name());
            item.put("dwdm", project.getDwdm());
            item.put("bprysfz", project.getBprysfz());
            map_project.add(item);
        }
    }


    //隐藏键盘
    public void hideInputKeyboard() {

        at_projectName.clearFocus();//取消焦点
        at_bprysfz.clearFocus();//取消焦点
        at_htid.clearFocus();
        at_xmbh.clearFocus();
        at_coordxy.clearFocus();
        at_dwdm.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * 保存信息
     */
    private void saveData() {
        String checstr = checkData();
        if (checstr.length()>0){
            show_Toast(checstr);
            return ;
        }
        int totalNum = LitePal.count(Project.class);//得到数据的总条数
        Log.e("保存项目", "totalNum: "+totalNum );
        //如果总数大于20,删除第一个数据
        if(totalNum>=20){
            Project pro=LitePal.findFirst(Project.class);//获取第一条记录
            Log.e("保存项目", "pro: "+pro.toString());
            pro.delete();
        }
        if (checstr == null || checstr.trim().length() < 1) {
            String a = at_bprysfz.getText().toString().trim().replace(" ", "");
            String b = at_htid.getText().toString().trim().replace(" ", "");
            String c = at_xmbh.getText().toString().trim().replace(" ", "");
            String d = at_coordxy.getText().toString().trim().replace("\n", "").replace("，", ",").replace(" ", "");
            String e = at_dwdm.getText().toString().trim().replace(" ", "");
            String f = at_projectName.getText().toString().trim().replace(" ", "");
            Project project =new Project();
            project.setXmbh(c);
            project.setDwdm(e);
            project.setBprysfz(a);
            project.setHtbh(b);
            project.setCoordxy(d);
            project.setProject_name(f);
            project.save();
            show_Toast("数据保存成功");
        } else {
            show_Toast(checstr);
        }

        saveHistory("history_projectName", at_projectName);//保存输入的项目编号
        saveHistory("history_xmbh", at_xmbh);//保存输入的项目编号
        saveHistory("history_htid", at_htid);//保存输入的合同编号
        saveHistory("history_dwdm", at_dwdm);//保存输入的合同编号
        saveHistory("history_bprysfz", at_bprysfz);//保存输入的身份证号
        saveHistory("history_coordxy", at_coordxy);//保存输入的经纬度

        initAutoComplete("history_projectName", at_projectName);
        initAutoComplete("history_htid", at_htid);
        initAutoComplete("history_xmbh", at_xmbh);
        initAutoComplete("history_dwdm", at_dwdm);
        initAutoComplete("history_bprysfz", at_bprysfz);
        initAutoComplete("history_coordxy", at_coordxy);
    }

    /****
     * 校验数据
     *
     */
    private String checkData() {
        String tipStr = "";
        String sfz = at_bprysfz.getText().toString().trim().replace(" ", "");
        String htid = at_htid.getText().toString().trim().replace(" ", "");
        String xmbh = at_xmbh.getText().toString().trim().replace(" ", "");
        String coordxy = at_coordxy.getText().toString().trim().replace("\n", "").replace("，", ",").replace(" ", "");
        String dwdm = at_dwdm.getText().toString().trim().replace(" ", "");
        String name = at_projectName.getText().toString().trim().replace(" ", "");
        if(htid.length()>1&&htid.length()<15){
            return "当前合同编号小于15位,请重新输入";
        }
        if(xmbh.length()>1&&xmbh.length()<15){
            return "当前项目编号小于15位,请重新输入";
        }
        if(sfz==null||sfz.length()<18){
            return "请输入爆破员身份证";
        }
        if(name==null){
            return "请输入项目名称";
        }

        if (coordxy == null || coordxy.trim().length() < 8 || coordxy.indexOf(",") < 5) {
            tipStr = "经度纬度设置不正确，具体格式为如:116.585989,36.663456";
            return tipStr;
        }
        List<Project> newsList = LitePal.where("project_name = ?", name).find(Project.class);
        Log.e("项目保存", "newsList: "+newsList.toString() );
        Log.e("项目保存", "size: "+newsList.size());
        if (newsList.size()>0) {
                return "项目名称重复";
        } else {
            return "";
        }
    }


    @OnClick({R.id.btn_down_return, R.id.btn_down_inputOK, R.id.btn_clear_htid, R.id.btn_clear_xmbh,
            R.id.btn_location, R.id.btn_clear_sfz, R.id.btn_clear_project_name, R.id.btn_clear_dwdm,
            R.id.sv_sp,R.id.btn_down_offline})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_down_return:
                finish();
                break;
            case R.id.btn_down_inputOK:
                hideInputKeyboard();//隐藏键盘
                if (at_coordxy.getText().toString().trim().length() < 1) {
                    show_Toast("经纬度不能为空!");
                    return;
                }
                if (at_bprysfz.getText().toString().trim().length() < 1) {
                    show_Toast("人员证号不能为空!");
                    return;
                }
                saveData();
                loadMoreData();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_clear_htid:
                deleteHistory("history_htid", at_htid);
                break;
            case R.id.btn_clear_xmbh:
                deleteHistory("history_xmbh", at_xmbh);
                break;
            case R.id.btn_location:
                deleteHistory("history_coordxy", at_coordxy);
                break;
            case R.id.btn_clear_sfz:
                deleteHistory("history_bprysfz", at_bprysfz);
                break;
            case R.id.btn_clear_project_name:
                deleteHistory("history_projectName", at_projectName);
                break;
            case R.id.btn_clear_dwdm:
                deleteHistory("history_dwdm", at_dwdm);
                break;
            case R.id.sv_sp:
                hideInputKeyboard();
                break;
            case R.id.btn_down_offline:
                Intent intent = new Intent(this, DownOfflineActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void itemClick(View v) {
        int position = (int) v.getTag();
        if(v.getId() == R.id.btn_del_name){
            delShouQuan(map_project.get(position).get("project_name").toString());//删除方法
            if (map_project != null && map_project.size() > 0) {//移除map中的值
                map_project.remove(position);
            }
            mAdapter.notifyDataSetChanged();
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
        show_Toast("删除成功");
        initAutoComplete("history_projectName", at_projectName);
        return 0;
    }

    private void deleteHistory(String field, AutoCompleteTextView auto) {
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        sp.edit().putString(field, "").apply();
        initAutoComplete(field, auto);
        show_Toast("删除成功");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected void onStart() {
        /***
         * 发送初始化命令
         */
        hideInputKeyboard();
        btnDownReturn.setFocusable(true);
        btnDownReturn.setFocusableInTouchMode(true);
        btnDownReturn.requestFocus();
        btnDownReturn.findFocus();
        super.onStart();
    }

    TextWatcher htbh_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 15) {
                at_htid.setBackgroundColor(Color.GREEN);
            }else {
                at_htid.setBackgroundColor(Color.RED);
            }
        }
    };
    TextWatcher xmbh_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 15) {
                at_xmbh.setBackgroundColor(Color.GREEN);
            }else {
                at_xmbh.setBackgroundColor(Color.RED);
            }
        }
    };
    TextWatcher sfz_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 18) {
                at_bprysfz.setBackgroundColor(Color.GREEN);
            }else {
                at_bprysfz.setBackgroundColor(Color.RED);
            }
        }
    };

    TextWatcher dwdm_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 13) {
                at_dwdm.setBackgroundColor(Color.GREEN);
            } else {
                at_dwdm.setBackgroundColor(Color.RED);
            }
        }
    };
}
