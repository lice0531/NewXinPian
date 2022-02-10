package android_serialport_api.xingbang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.ZhuCeScanAdapter;
import android_serialport_api.xingbang.databinding.ActivityZhuCeScanBinding;
import android_serialport_api.xingbang.utils.MmkvUtils;

public class ZhuCeActivity_scan extends AppCompatActivity {
    ActivityZhuCeScanBinding binding;
    ExpandableListView zc_list;
    String qsys ;
    String pjys ;
    String kjys ;
    String knys ;
    String f1ys ;
    String f2ys ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_zhu_ce_scan);
        binding = ActivityZhuCeScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView title=findViewById(R.id.title_text);
        title.setText("雷管注册");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setOnClickListener(v -> startActivity(new Intent(ZhuCeActivity_scan.this, UpdataDelayActivity.class)));
        iv_back.setOnClickListener(v -> finish());

        qsys=(String) MmkvUtils.getcode("qsys","0");
        pjys=(String) MmkvUtils.getcode("pjys","0");
        kjys=(String) MmkvUtils.getcode("kjys","0");
        knys=(String) MmkvUtils.getcode("knys","0");
        f1ys=(String) MmkvUtils.getcode("f1ys","0");
        f2ys=(String) MmkvUtils.getcode("f2ys","0");

        zc_list = findViewById(R.id.zc_list);
        List<String> groupList = new ArrayList<>();
        groupList.add("一");
        groupList.add("二");
        groupList.add("三");

        List<List<String>> childList = new ArrayList<>();
        List<String> childList1 = new ArrayList<>();
        childList1.add("1");
        childList1.add("1");
        childList1.add("1");
        List<String> childList2 = new ArrayList<>();
        childList2.add("2");
        childList2.add("2");
        childList2.add("2");
        List<String> childList3 = new ArrayList<>();
        childList3.add("3");
        childList3.add("3");
        childList3.add("3");

        childList.add(childList1);
        childList.add(childList2);
        childList.add(childList3);

        ZhuCeScanAdapter demoAdapter = new ZhuCeScanAdapter(groupList, childList);
        zc_list.setAdapter(demoAdapter);

        //一级点击监听
        zc_list.setOnGroupClickListener((parent, v, groupPosition, id) -> {

            //如果你处理了并且消费了点击返回true,这是一个基本的防止onTouch事件向下或者向上传递的返回机制
            return false;
        });

        //二级点击监听
        zc_list.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            //如果你处理了并且消费了点击返回true
            return false;
        });

        binding.zcBtnF1.setText("F1:+"+f1ys+"ms");//F1:+20ms
        binding.zcBtnF2.setText("F2:+"+f2ys+"ms");//F1:+20ms
    }
}