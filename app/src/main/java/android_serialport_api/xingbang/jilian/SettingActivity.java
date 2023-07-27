package android_serialport_api.xingbang.jilian;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.utils.MmkvUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 级联设置
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_delay)
    EditText etDelay;
    @BindView(R.id.btn)
    Button btn;

    private int delay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tongbu_setting);
        ButterKnife.bind(this);
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        SharedPreferences sp = getSharedPreferences("setting", 0);
        final SharedPreferences.Editor editor = sp.edit();
        delay = sp.getInt("delay", 0);
        etDelay.setText(delay + "");

        etDelay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    return;
                }
                delay = Integer.parseInt(s.toString());
                editor.putInt("delay", delay).apply();
                MmkvUtils.savecode("delay",delay);
            }
        });

        String a2 = sp.getString("device", "");
        etCode.setText(a2);

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    return;
                }
                editor.putString("device", s.toString()).apply();
                MmkvUtils.savecode("ACode",s.toString());
            }
        });


    }

    @OnClick(R.id.btn)
    public void onViewClicked() {
        finish();
    }
}
