package android_serialport_api.xingbang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.databinding.ActivityNewMainBinding;
import android_serialport_api.xingbang.firingdevice.SetEnvMainActivity;

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

        initView();
    }

    private void initView() {
        TextView title=findViewById(R.id.title_text);
        title.setText("通用性起爆器App首页");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setVisibility(View.GONE);
        iv_back.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "onClick: ");
        switch (view.getId()){
            case R.id.cardView1:
                Intent intent = new Intent(NewMainActivity.this,DownProjectActivity.class);
                startActivity(intent);
                break;
            case R.id.cardView2:
                startActivity(new Intent(NewMainActivity.this,GetGPSActivity.class));
                break;
            case R.id.cardView3:
                break;
            case R.id.cardView4:
                break;
            case R.id.cardView5:
                break;
            case R.id.cardView6:
//                startActivity(new Intent(NewMainActivity.this,QiBaoActivity.class));
                startActivity(new Intent(NewMainActivity.this,LineChartDemo.class));
                break;
            case R.id.cardView7:
                break;
            case R.id.cardView8:
                startActivity(new Intent(NewMainActivity.this, SetEnvMainActivity.class));
                break;
        }
    }
}