package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.utils.LocalManageUtil;

public class SetLanguageActivity extends BaseActivity {
    private TextView mUserSelect;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlanguage);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        mUserSelect = (TextView) findViewById(R.id.tv_user_select);
        mUserSelect.setText(getString(R.string.user_select_language, LocalManageUtil.getSelectLanguage(this)));
        //
        setClick();
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, SetLanguageActivity.class);
        context.startActivity(intent);
    }

    private void selectLanguage(int select) {
        LocalManageUtil.saveSelectLanguage(this, select);
        XingbangMain.reStart(this);
    }

    private void setClick() {
        //跟随系统
        findViewById(R.id.btn_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(0);
            }
        });
        //简体中文
        findViewById(R.id.btn_cn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(1);
            }
        });
        //繁体中文
//        findViewById(R.id.btn_traditional).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectLanguage(2);
//
//            }
//        });
        //english
        findViewById(R.id.btn_en).setOnClickListener(v -> selectLanguage(3));
    }
}
