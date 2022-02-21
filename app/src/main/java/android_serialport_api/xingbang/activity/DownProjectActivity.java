package android_serialport_api.xingbang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.ItemProjectAdapter;
import android_serialport_api.xingbang.custom.MlistView;
import android_serialport_api.xingbang.db.DatabaseHelper;

public class DownProjectActivity extends BaseActivity implements ItemProjectAdapter.InnerItemOnclickListener, AdapterView.OnItemClickListener{
    private ItemProjectAdapter mAdapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private List<Map<String, Object>> map_project = new ArrayList<>();
    MlistView lvProject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_project);

        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 22);
        db = mMyDatabaseHelper.getReadableDatabase();

        TextView title = findViewById(R.id.title_text);
        title.setText("项目管理");
        ImageView add = findViewById(R.id.title_add);
        ImageView back = findViewById(R.id.title_back);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(DownProjectActivity.this, AddProjectActivity.class);
            startActivity(intent);
        });
        back.setOnClickListener(v -> finish());
        lvProject=findViewById(R.id.lv_project);
        loadMoreData();

        mAdapter = new ItemProjectAdapter(this, map_project, R.layout.item_project);
        mAdapter.setOnInnerItemOnClickListener(this);
        lvProject.setAdapter(mAdapter);
        lvProject.setOnItemClickListener(this);
    }

    private void loadMoreData() {
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
//        initAutoComplete("history_projectName", at_projectName);
        return 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
}