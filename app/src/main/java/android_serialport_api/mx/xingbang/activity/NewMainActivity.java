package android_serialport_api.mx.xingbang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.databinding.ActivityNewMainBinding;
import android_serialport_api.mx.xingbang.firingdevice.DownWorkCode;
import android_serialport_api.mx.xingbang.firingdevice.QueryHisDetail;
import android_serialport_api.mx.xingbang.firingdevice.ReisterMainPage_line;
import android_serialport_api.mx.xingbang.firingdevice.SetEnvMainActivity;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class NewMainActivity extends AppCompatActivity implements View.OnClickListener{

    ActivityNewMainBinding binding ;
    private String TAG ="主页";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_main);
        // 新办法
        binding = ActivityNewMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SQLiteStudioService.instance().start(this);
        initView();
    }

    private void initView() {
        TextView title=findViewById(R.id.title_text);
        title.setText("通用型起爆器App首页");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setVisibility(View.GONE);
        iv_back.setVisibility(View.GONE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Log.e(TAG, "onClick: ");
        switch (view.getId()){
            case R.id.cardView1://项目管理
                Intent intent = new Intent(NewMainActivity.this,DownProjectActivity.class);
                startActivity(intent);
                break;
            case R.id.cardView2://GPS定位
//                startActivity(new Intent(NewMainActivity.this,GetGPSActivity.class));
                startActivity(new Intent(NewMainActivity.this,GpsDemoActivity.class));
                break;
            case R.id.cardView3://单发雷管检测
                startActivity(new Intent(NewMainActivity.this, ReisterMainPage_line.class));
                break;
            case R.id.cardView4://下载工作码
                startActivity(new Intent(NewMainActivity.this, DownWorkCode.class));
                break;
            case R.id.cardView5://雷管注册
                startActivity(new Intent(NewMainActivity.this, ZhuCeActivity_scan.class));
                break;
            case R.id.cardView6://充电/起爆
                String str5 = "起爆";
                Intent intent5;//金建华
//                if (Yanzheng.equals("验证")) {
//                    //Intent intent5 = new Intent(XingbangMain.this, XingBangApproveActivity.class);//人脸识别环节
////                    intent5 = new Intent(this, VerificationActivity.class);
//                    intent5 = new Intent(this, FiringMainActivity.class);
//                } else {
//                    intent5 = new Intent(this, FiringMainActivity.class);
//                }
                intent5 = new Intent(this, QiBaoActivity.class);
                intent5.putExtra("dataSend", str5);
                startActivity(intent5);
                break;
            case R.id.cardView7://上传数据
                startActivity(new Intent(NewMainActivity.this, QueryHisDetail.class));
                break;
            case R.id.cardView8://设置
                startActivity(new Intent(NewMainActivity.this, SetEnvMainActivity.class));
                break;
        }
    }
}