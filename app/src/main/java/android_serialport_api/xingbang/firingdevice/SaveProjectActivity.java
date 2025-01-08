package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.utils.AppLogUtils;
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
//    @BindView(R.id.sv_sp)
//    ScrollView svSp;
    @BindView(R.id.btn_down_offline)
    Button btnOffline;
    @BindView(R.id.btn_add_project)
    Button btnAddProject;
    @BindView(R.id.btn_down_project)
    Button btnDownProject;
    @BindView(R.id.btn_delete_project)
    Button btnDelete;
    private SaveProjectAdapter mAdapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private List<Map<String, Object>> map_project = new ArrayList<Map<String, Object>>();
    private TextView totalbar_title,tv_right;
    private boolean isDelete = true;//是否展示列表中的多选按钮

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_project);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        AppLogUtils.writeAppLog("---进入项目列表页面----");
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        totalbar_title =  findViewById(R.id.title_text);
        tv_right = findViewById(R.id.title_right);
        ImageView title_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        title_add.setVisibility(View.GONE);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(getResources().getString(R.string.text_gl));
        totalbar_title.setText("项目列表");
        iv_back.setOnClickListener(v -> finish());
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map_project.size() == 0) {
                    show_Toast(getResources().getString(R.string.text_xzxm));
                    return;
                }
                if (isDelete) {
                    isDelete = false;
                    mAdapter.showCheckBox(true);
                    btnDelete.setVisibility(View.VISIBLE);
                    tv_right.setText(getResources().getString(R.string.text_alert_cancel));
                    pnList.clear();
                } else {
                    pnList.clear();
                    isDelete = true;
                    mAdapter.showCheckBox(false);
                    btnDelete.setVisibility(View.GONE);
                    tv_right.setText(getResources().getString(R.string.text_gl));
                }
            }
        });
//        loadMoreData();
        mAdapter = new SaveProjectAdapter(this, map_project, R.layout.item_list_saveproject_new);
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
            item.put("business", project.getBusiness());
            item.put("selected", project.getSelected());
            item.put("dwdm", project.getDwdm());
            item.put("bprysfz", project.getBprysfz());
            map_project.add(item);
        }
        // 使用 Comparator 对 map_project 进行排序，确保 selected = "true" 的条目排在前面
        Collections.sort(map_project, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                String selected1 = (String) o1.get("selected");
                String selected2 = (String) o2.get("selected");
                // 先把字符串 "true" 转换为布尔值，再进行比较
                boolean isSelected1 = "true".equals(selected1);
                boolean isSelected2 = "true".equals(selected2);

                // selected 为 "true" 的排在前面
                return Boolean.compare(isSelected2, isSelected1); // 反向排序
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMoreData();
        mAdapter.notifyDataSetChanged();
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
            show_Toast(getString(R.string.text_bccg));
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
            return getResources().getString(R.string.text_htxy);
        }
        if(xmbh.length()>1&&xmbh.length()<15){
            return getResources().getString(R.string.text_xmxy);
        }
        if(sfz==null||sfz.length()<18){
            return getResources().getString(R.string.text_sfz);
        }
        if(name==null){
            return getResources().getString(R.string.text_xmmc);
        }

        if (coordxy == null || coordxy.trim().length() < 8 || coordxy.indexOf(",") < 5) {
            tipStr = getResources().getString(R.string.text_down_tip11);
            return tipStr;
        }
        List<Project> newsList = LitePal.where("project_name = ?", name).find(Project.class);
        Log.e("项目保存", "newsList: "+newsList.toString() );
        Log.e("项目保存", "size: "+newsList.size());
        if (newsList.size()>0) {
                return getResources().getString(R.string.text_mccf);
        } else {
            return "";
        }
    }


    @OnClick({R.id.btn_down_return, R.id.btn_down_inputOK, R.id.btn_clear_htid, R.id.btn_clear_xmbh,
            R.id.btn_location, R.id.btn_clear_sfz, R.id.btn_clear_project_name, R.id.btn_clear_dwdm,
            R.id.btn_down_offline,R.id.btn_add_project,R.id.btn_down_project,R.id.btn_delete_project})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_down_return:
                finish();
                break;
            case R.id.btn_down_inputOK:
                hideInputKeyboard();//隐藏键盘
                if (at_coordxy.getText().toString().trim().length() < 1) {
                    show_Toast(getResources().getString(R.string.text_down_err3));
                    return;
                }
                if (at_bprysfz.getText().toString().trim().length() < 1) {
                    show_Toast(getResources().getString(R.string.text_down_err10));
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
//            case R.id.sv_sp:
//                hideInputKeyboard();
//                break;
            case R.id.btn_down_offline:
                // 判断集合中是否有 selected 为 true 的数据
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    // 判断 selected 字段是否为 "true"  判断是否正在使用中的项目
                    boolean hasSelectedTrue = map_project.stream()
                            .anyMatch(map -> "true".equals(map.get("selected")));
                    if (!hasSelectedTrue) {
                        show_Toast(getResources().getString(R.string.text_szxm));
                        return;
                    }
                }
                Intent intent = new Intent(this, DownOfflineActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_add_project:
                Intent im = new Intent(this, ProjectManagerActivity.class);
                startActivity(im);
                break;
            case R.id.btn_down_project:
                if (map_project.size() == 0) {
                    show_Toast(getResources().getString(R.string.text_xzxm));
                    return;
                }
                // 判断集合中是否有 selected 为 true 的数据
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    // 判断 selected 字段是否为 "true"  判断是否正在使用中的项目
                    boolean hasSelectedTrue = map_project.stream()
                            .anyMatch(map -> "true".equals(map.get("selected")));
                    if (!hasSelectedTrue) {
                        show_Toast(getResources().getString(R.string.text_szxm));
                        return;
                    }
                }
                String str7 = "下载";
                Intent intent7 = new Intent(SaveProjectActivity.this, DownWorkCode.class);
                intent7.putExtra("dataSend", str7);
                startActivityForResult(intent7, 1);
                break;
            case R.id.btn_delete_project:
                AppLogUtils.writeAppLog("点击了'删除项目'按钮执行多些删除项目操作");
                if (pnList.isEmpty()) {
                    show_Toast("请先选中要删除的项目");
                    return;
                }
                //先弹出是否确认删除项目dialog  确定后执行删除操作
                if (!SaveProjectActivity.this.isFinishing()) {
                    AlertDialog dialog = new AlertDialog.Builder(SaveProjectActivity.this)
                            .setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题
                            .setMessage(getResources().getString(R.string.text_scsyxm))//设置对话框的内容
                            //设置对话框的按钮
                            .setNeutralButton(getResources().getString(R.string.text_dialog_qx), (dialog1, which) -> {
                                dialog1.dismiss();
                            })
                            .setPositiveButton(getString(R.string.text_dialog_qd), (dialog14, which) -> {
                                Log.e("页面","选中的项目: " + pnList.toString());
                                for (String pn : pnList) {
                                    delShouQuan(pn);//删除方法
                                }
                                loadMoreData();
                                mAdapter.notifyDataSetChanged();
                                if (map_project.size() == 0) {
                                    pnList.clear();
                                    isDelete = true;
                                    mAdapter.showCheckBox(false);
                                    btnDelete.setVisibility(View.GONE);
                                    tv_right.setText(getResources().getString(R.string.text_gl));
                                }
                                AppLogUtils.writeAppLog("点击了多选删除项目按钮");
                            }).create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
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
        if ("true".equals(map_project.get(position).get("selected").toString())) {
            //如果是使用中的项目，进入项目编辑页面
            Intent intent = new Intent(this,ProjectManagerActivity.class);
            intent.putExtra("xmPageFlag","Y");
            intent.putExtra("proId",map_project.get(position).get("id").toString());
            intent.putExtra("htbh",map_project.get(position).get("htbh").toString());
            intent.putExtra("dwdm",map_project.get(position).get("dwdm").toString());
            intent.putExtra("xmbh",map_project.get(position).get("xmbh").toString());
            intent.putExtra("coordxy",map_project.get(position).get("coordxy").toString());
            intent.putExtra("business",map_project.get(position).get("business").toString());
            intent.putExtra("project_name",map_project.get(position).get("project_name").toString());
            intent.putExtra("bprysfz",map_project.get(position).get("bprysfz").toString());
            startActivity(intent);
        } else {
            //如果不是使用中的项目，进入项目详情页面
            Intent intent = new Intent(this,ProjectDetailActivity.class);
            intent.putExtra("proId",map_project.get(position).get("id").toString());
            intent.putExtra("htbh",map_project.get(position).get("htbh").toString());
            intent.putExtra("dwdm",map_project.get(position).get("dwdm").toString());
            intent.putExtra("xmbh",map_project.get(position).get("xmbh").toString());
            intent.putExtra("coordxy",map_project.get(position).get("coordxy").toString());
            intent.putExtra("business",map_project.get(position).get("business").toString());
            intent.putExtra("project_name",map_project.get(position).get("project_name").toString());
            intent.putExtra("bprysfz",map_project.get(position).get("bprysfz").toString());
            startActivity(intent);
        }
    }

    private List<String> pnList = new ArrayList<>();
    @Override
    public void itemViewClick(View v, int index,boolean isChecked) {
        int position = index;
        if (v.getId() == R.id.cbIsSelected) {
            if (isChecked) {
                //多选   选中的项目  可执行多条删除功能
                pnList.add(map_project.get(position).get("project_name").toString());
            } else {
                pnList.remove(map_project.get(position).get("project_name").toString());
            }
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
        initAutoComplete("history_projectName", at_projectName);
        return 0;
    }

    private void deleteHistory(String field, AutoCompleteTextView auto) {
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        sp.edit().putString(field, "").apply();
        initAutoComplete(field, auto);
        show_Toast(getResources().getString(R.string.text_del_ok));
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
