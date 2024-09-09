package android_serialport_api.xingbang.activity;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.orhanobut.logger.Logger;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.RecyclerViewAdapter_Denator;
import android_serialport_api.xingbang.custom.ItemProjectAdapter;
import android_serialport_api.xingbang.custom.RecyclerViewAdapter_Project;
import android_serialport_api.xingbang.custom.SaveProjectAdapter;
import android_serialport_api.xingbang.databinding.ActivityProjectManagementBinding;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;

public class ProjectManagementActivity extends BaseActivity implements ItemProjectAdapter.InnerItemOnclickListener,SaveProjectAdapter.InnerItemOnclickListener, AdapterView.OnItemClickListener {
    private List<Project> list_project = new ArrayList<>();
    ActivityProjectManagementBinding binding;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter_Project<Project> mAdapter;
    private SaveProjectAdapter mAdapter1;
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String pro_name = "";//项目名称
    private String jd = "";
    private String wd = "";
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private List<Map<String, Object>> map_project = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        TextView title = findViewById(R.id.title_text);
        title.setText("项目管理");
        ImageView add = findViewById(R.id.title_add);
        ImageView back = findViewById(R.id.title_back);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectManagementActivity.this, AddProjectActivity.class);
//            startActivity(intent);
            startActivityForResult(intent, 1);
        });
        back.setOnClickListener(v -> finish());

        loadMoreData();//获取数据
        //新的适配方法 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        binding.dpLvProject.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerViewAdapter_Project<>(this, 1);
        binding.dpLvProject.setAdapter(mAdapter);

        mAdapter.setListData(list_project, 1);
        mAdapter.setOnItemLongClick(position -> {

            Logger.e("长按事件");
        });
        mAdapter.setOnItemClickListener((view, position) -> {
            Project info = list_project.get(position);
            modifyBlastBaseInfo(info, position);//序号,孔号,延时,管壳码
            Logger.e("点击事件");
        });

        loadMoreData1();
        mAdapter1 = new SaveProjectAdapter(this, map_project, R.layout.item_list_saveproject);
        mAdapter1.setOnInnerItemOnClickListener(this);
        binding.lvProject.setAdapter(mAdapter1);
        binding.lvProject.setOnItemClickListener(this);
    }

    //获取用户信息
    private void getUserMessage() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<Project> projects = master.queryProjectIsSelected("true");
        if(projects.size()>0){
            pro_bprysfz = projects.get(0).getBprysfz();
            pro_htid = projects.get(0).getHtbh();
            pro_xmbh = projects.get(0).getXmbh();
            pro_coordxy = projects.get(0).getCoordxy();
            pro_dwdm = projects.get(0).getDwdm();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadMoreData() {
//        list_project.clear();
        GreenDaoMaster daoMaster = new GreenDaoMaster();
        list_project = daoMaster.queryProject();
        Logger.e("list_project" + list_project.toString());
    }

    private void loadMoreData1() {
        map_project.clear();
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_PROJECT;//+" order by htbh "
        Cursor cursor = db.rawQuery(sql, null);
        //return getCursorTolist(cursor);
        if (cursor != null) {

            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String project_name = cursor.getString(1); //获取第二列的值 ,序号
                String xmbh = cursor.getString(2);
                String htbh = cursor.getString(3);//管壳号
                String dwdm = cursor.getString(4);//错误数量
                String bprysfz = cursor.getString(5);//起爆状态
                String coordxy = cursor.getString(6);//经纬度

                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", id);
                item.put("project_name", project_name);
                item.put("xmbh", xmbh);
                item.put("htbh", htbh);
                item.put("dwdm", dwdm);
                item.put("bprysfz", bprysfz);
                item.put("coordxy", coordxy);

                map_project.add(item);
            }
            cursor.close();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void itemClick(View v) {
        int position = (int) v.getTag();
        Logger.e("itemClick");
        if (v.getId() == R.id.iv_sp_xiugai) {
        }
    }

    private void modifyBlastBaseInfo(Project project, int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.project_dialog, null);
        builder.setView(view);

        final EditText pro_name = view.findViewById(R.id.et_name);
        final EditText pro_xmbh = view.findViewById(R.id.et_xmbh);
        final EditText pro_htbh = view.findViewById(R.id.et_htbh);
        final EditText pro_dwdm = view.findViewById(R.id.et_dwdm);
        final EditText pro_sfz = view.findViewById(R.id.et_sfz);
        final SwitchButton pro_choice = view.findViewById(R.id.sw_choice);

        pro_name.setText(project.getProject_name());
        pro_xmbh.setText(project.getXmbh());
        pro_htbh.setText(project.getHtbh());
        pro_dwdm.setText(project.getDwdm());
        pro_sfz.setText(project.getBprysfz());
        pro_choice.setChecked(project.getSelected().equals("true"));

        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {
            project.setProject_name(pro_name.getText().toString().trim());
            project.setBprysfz(pro_sfz.getText().toString().trim());
            project.setXmbh(pro_xmbh.getText().toString().trim());
            project.setHtbh(pro_htbh.getText().toString().trim());
            project.setDwdm(pro_dwdm.getText().toString().trim());
            project.setSelected(pro_choice.isChecked() + "");
            if(checkRepeatSelected()||!pro_choice.isChecked()){
                MmkvUtils.savecode("pro_name",project.getProject_name());
                updateProject(project);
                show_Toast(getString(R.string.text_error_tip38));
            }else {
                show_Toast("已有选中项,修改失败");
            }
            loadMoreData();
            mAdapter.setListData(list_project, 1);
            mAdapter.notifyDataSetChanged();
            dialog.dismiss();

        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("删除", (dialog, which) -> {
            delShouQuan(project);
        });
        builder.show();
    }
    //是否可以设置选中
    public boolean checkRepeatSelected() {
        GreenDaoMaster master = new GreenDaoMaster();
        if (master.queryProjectIsSelected("true").size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private void updateProject(Project project) {

        getDaoSession().getProjectDao().update(project);

    }

    private void delShouQuan(Project project) {//删除雷管
        getDaoSession().getProjectDao().deleteByKey(project.getId());
        list_project.remove(project);
        String longhistory = (String) MmkvUtils.getcode("history_projectName", "");
        MmkvUtils.removeKey("history_projectName");

        String[] hisArrays = longhistory.split("#");
        //去重
        ArrayList<String> his = new ArrayList<>();
        HashSet<String> set = new HashSet<>();
        for (String str : hisArrays) {
            if (set.add(str)) {
                his.add(str);
            }
        }
        //删除选中项
        his.remove(project.getProject_name());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < his.size(); i++) {
            sb.insert(0, his.get(i) + "#");
        }
        MmkvUtils.savecode("history_projectName", sb.toString());
        show_Toast("删除成功");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
//            list_project.clear();
            loadMoreData();
            mAdapter.setListData(list_project, 1);
            mAdapter.notifyDataSetChanged();
        }

    }
}