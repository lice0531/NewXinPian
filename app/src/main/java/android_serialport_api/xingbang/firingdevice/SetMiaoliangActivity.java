package android_serialport_api.xingbang.firingdevice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android_serialport_api.xingbang.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetMiaoliangActivity extends AppCompatActivity {
    @BindView(R.id.et_set_delay)
    EditText etSetDelay;
    @BindView(R.id.btn_start)
    Button btnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_miaoliang);
        ButterKnife.bind(this);
    }
    @OnClick({ R.id.btn_start})
    public void onClick(View view) {
        if (view.getId() == R.id.btn_start) {
            Intent intent5 = new Intent(this, FiringMainActivity.class);
            intent5.putExtra("yanshi", etSetDelay.getText());
            startActivity(intent5);
        }
    }
}