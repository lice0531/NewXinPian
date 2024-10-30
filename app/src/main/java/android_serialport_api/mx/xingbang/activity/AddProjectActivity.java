package android_serialport_api.mx.xingbang.activity;

import android_serialport_api.mx.xingbang.BaseActivity;
import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.databinding.ActivityAddProjectBinding;
import android_serialport_api.mx.xingbang.db.Project;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.List;

public class AddProjectActivity extends BaseActivity {
    ActivityAddProjectBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_project);
        binding=ActivityAddProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView title=findViewById(R.id.title_text);
        title.setText("新增项目");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setVisibility(View.GONE);
        iv_back.setOnClickListener(v -> finish());

        Spinner spinner =findViewById(R.id.add_gsxz);
        SpinnerAdapter adapter;
        adapter= ArrayAdapter.createFromResource(this,R.array.gsxz_name,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button saveProject =findViewById(R.id.add_btn_save_pro);
        saveProject.setOnClickListener(v -> {
            hideInputKeyboard();//隐藏键盘
//                if (at_coordxy.getText().toString().trim().length() < 1) {
//                    show_Toast("经纬度不能为空!");
//                    return;
//                }
            if (binding.downAtBprysfz.getText().toString().trim().length() < 1) {
                show_Toast("人员证号不能为空!");
                return;
            }
            saveData();
        });

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
        if (checstr.trim().length() < 1) {
            String a = binding.downAtBprysfz.getText().toString().trim().replace(" ", "");
            String b = binding.downAtHtid.getText().toString().trim().replace(" ", "");
            String c = binding.downAtXmbh.getText().toString().trim().replace(" ", "");
//            String d = at_coordxy.getText().toString().trim().replace("\n", "").replace("，", ",").replace(" ", "");
            String e = binding.downAtDwdm.getText().toString().trim().replace(" ", "");
            String f =  binding.downAtProjectName.getText().toString().trim().replace(" ", "");
            Project project =new Project();
            project.setXmbh(c);
            project.setDwdm(e);
            project.setBprysfz(a);
            project.setHtbh(b);
//            project.setCoordxy(d);
            project.setProject_name(f);
            project.save();
            show_Toast("数据保存成功");
        } else {
            show_Toast(checstr);
        }

        saveHistory("history_projectName", binding.downAtProjectName);//保存输入的项目编号
        saveHistory("history_xmbh", binding.downAtXmbh);//保存输入的项目编号
        saveHistory("history_htid", binding.downAtHtid);//保存输入的合同编号
        saveHistory("history_dwdm", binding.downAtDwdm);//保存输入的合同编号
        saveHistory("history_bprysfz", binding.downAtBprysfz);//保存输入的身份证号
//        saveHistory("history_coordxy", at_coordxy);//保存输入的经纬度

        initAutoComplete("history_projectName", binding.downAtProjectName);
        initAutoComplete("history_htid", binding.downAtHtid);
        initAutoComplete("history_xmbh", binding.downAtXmbh);
        initAutoComplete("history_dwdm", binding.downAtDwdm);
        initAutoComplete("history_bprysfz", binding.downAtBprysfz);
//        initAutoComplete("history_coordxy", at_coordxy);
    }

    /****
     * 校验数据
     *
     */
    private String checkData() {
        String tipStr = "";
        String sfz = binding.downAtBprysfz.getText().toString().trim().replace(" ", "");
        String htid = binding.downAtHtid.getText().toString().trim().replace(" ", "");
        String xmbh = binding.downAtXmbh.getText().toString().trim().replace(" ", "");
//        String coordxy = at_coordxy.getText().toString().trim().replace("\n", "").replace("，", ",").replace(" ", "");
        String dwdm = binding.downAtDwdm.getText().toString().trim().replace(" ", "");
        String name = binding.downAtProjectName.getText().toString().trim().replace(" ", "");
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

//        if (coordxy == null || coordxy.trim().length() < 8 || coordxy.indexOf(",") < 5) {
//            tipStr = "经度纬度设置不正确，具体格式为如:116.585989,36.663456";
//            return tipStr;
//        }
        List<Project> newsList = LitePal.where("project_name = ?", name).find(Project.class);
        Log.e("项目保存", "newsList: "+newsList.toString() );
        Log.e("项目保存", "size: "+newsList.size());
        if (newsList.size()>0) {
            return "项目名称重复";
        } else {
            return "";
        }
    }


    //隐藏键盘
    public void hideInputKeyboard() {

        binding.downAtProjectName.clearFocus();//取消焦点
        binding.downAtBprysfz.clearFocus();//取消焦点
        binding.downAtDwdm.clearFocus();
        binding.downAtHtid.clearFocus();
        binding.downAtXmbh.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}