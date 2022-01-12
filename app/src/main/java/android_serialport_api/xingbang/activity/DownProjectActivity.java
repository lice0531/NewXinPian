package android_serialport_api.xingbang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.firingdevice.SaveProjectActivity;

public class DownProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_project);
        TextView title = findViewById(R.id.title_text);
        title.setText("项目管理");
        ImageView add = findViewById(R.id.title_add);
        ImageView back = findViewById(R.id.title_back);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownProjectActivity.this, AddProjectActivity.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}