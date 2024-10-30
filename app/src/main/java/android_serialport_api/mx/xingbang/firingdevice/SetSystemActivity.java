package android_serialport_api.mx.xingbang.firingdevice;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.suke.widget.SwitchButton;

import java.util.List;

import android_serialport_api.mx.xingbang.BaseActivity;
import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.db.DatabaseHelper;
import android_serialport_api.mx.xingbang.db.GreenDaoMaster;
import android_serialport_api.mx.xingbang.db.MessageBean;
import android_serialport_api.mx.xingbang.utils.MmkvUtils;
import android_serialport_api.mx.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android_serialport_api.mx.xingbang.Application.getDaoSession;

public class SetSystemActivity extends BaseActivity {

    @BindView(R.id.sw_setsys)
    SwitchButton swSetsys;
    @BindView(R.id.sw_yanzheng)
    SwitchButton swYanzheng;
    @BindView(R.id.sw_yanzheng_sq)
    SwitchButton swYanzheng_sq;
    @BindView(R.id.sw_shangchuan)
    SwitchButton swShangchuan;
    @BindView(R.id.et_set_Preparation)
    EditText etSetPreparation;
    @BindView(R.id.set_languages)
    Button set_languages;
    @BindView(R.id.set_save)
    Button setSave;
    @BindView(R.id.et_set_chongdiantime)
    EditText etSetChongdiantime;
    @BindView(R.id.et_set_Jiancetime)
    EditText etSetJiancetime;
    @BindView(R.id.set_Voltage)
    Button setVoltage;

    private int Preparation_time;//准备时间
    private int ChongDian_time;//准备时间
    private int JianCe_time;//准备时间
    private String qiaosi_set = "";//是否检测桥丝
    private String Yanzheng = "";//是否验证地理位置
    private String Yanzheng_sq = "";//是否验雷管已经授权
    private String Shangchuan = "";//是否上传错误雷管
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private Handler Handler_tip = null;//提示信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        Yanzheng = (String) MmkvUtils.getcode("Yanzheng", "验证");
        Yanzheng_sq = (String) MmkvUtils.getcode("Yanzheng_sq", "不验证");
        Shangchuan = (String) MmkvUtils.getcode("Shangchuan", "是");
        getUserMessage();
        Log.e("设置页面", "qiaosi_set: " + qiaosi_set);
        Log.e("设置页面", "Shangchuan: " + Shangchuan);
        if (qiaosi_set.equals("true")) {
            swSetsys.setChecked(true);
        }
        if (Yanzheng.equals("验证")) {
            swYanzheng.setChecked(true);
        }
        if (Yanzheng_sq.equals("验证")) {
            swYanzheng_sq.setChecked(true);
        }
        if (Shangchuan.equals("是")) {
            swShangchuan.setChecked(true);
        }
        Handler_tip=new Handler(msg -> {
            Bundle b = msg.getData();
            String shellStr = b.getString("shellStr");
            if (msg.arg1 == 1) {
                show_Toast(getString(R.string.text_systip_3));
            } else if (msg.arg1 == 2) {
                show_Toast(getString(R.string.text_systip_2));
            } else if (msg.arg1 == 3) {
                show_Toast(getString(R.string.text_systip_11));
            } else if (msg.arg1 == 4) {
                show_Toast(getString(R.string.text_systip_1));
            }
            return false;
        });
    }

    private void getUserMessage() {
        List<MessageBean> list = getDaoSession().getMessageBeanDao().loadAll();
        qiaosi_set = list.get(0).getQiaosi_set();
        Preparation_time = Integer.parseInt(list.get(0).getPreparation_time());
        ChongDian_time = Integer.parseInt(list.get(0).getChongdian_time());
        JianCe_time = Integer.parseInt(list.get(0).getJiance_time());
        etSetPreparation.setText(Preparation_time + "");
        etSetChongdiantime.setText(ChongDian_time + "");
        etSetJiancetime.setText(JianCe_time + "");
    }

    @OnClick({R.id.set_languages, R.id.set_save, R.id.set_Voltage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_Voltage://设置电压
                Intent intent = new Intent(SetSystemActivity.this, SetVoltageActivity.class);
                startActivity(intent);
                break;
            case R.id.set_languages://设置语言
                SetLanguageActivity.enter(SetSystemActivity.this);
                break;
            case R.id.set_save://保存设置
                MessageBean message = GreenDaoMaster.getAllFromInfo_bean();
                if (swYanzheng.isChecked()) {
                    MmkvUtils.savecode("Yanzheng", "验证");
                } else {
                    MmkvUtils.savecode("Yanzheng", "不验证");
                }
                if (swYanzheng_sq.isChecked()) {
                    MmkvUtils.savecode("Yanzheng_sq", "验证");
                } else {
                    MmkvUtils.savecode("Yanzheng_sq", "不验证");
                }
                if (swShangchuan.isChecked()) {
                    MmkvUtils.savecode("Shangchuan", "是");
                } else {
                    MmkvUtils.savecode("Shangchuan", "否");
                }

                if (swSetsys.isChecked()) {
                    message.setQiaosi_set("true");
                } else {
                    message.setQiaosi_set("false");
                }

                int flag1 = 0, flag2 = 0, flag3 = 0;
                if (!TextUtils.isEmpty(etSetPreparation.getText())) {//网络测试准备时间
                    message.setPreparation_time(etSetPreparation.getText().toString());
                    Log.e("准备时间", "Preparation_time: " + etSetPreparation.getText().toString());
                } else {
                    flag1 = 1;
                }
                if (!TextUtils.isEmpty(etSetJiancetime.getText())) {//起爆检测时间
                    Log.e("组网检测时间", "etSetJiancetime: " + etSetJiancetime.getText().toString());
                    message.setJiance_time(etSetJiancetime.getText().toString());
                } else {
                    flag2 = 1;
                }
                if (!TextUtils.isEmpty(etSetChongdiantime.getText())) {//低压充电时间
                    if (Integer.parseInt(etSetChongdiantime.getText().toString()) >= 8) {
                        Log.e("充电时间", "etSetChongdiantime: " + etSetChongdiantime.getText().toString());
                        message.setChongdian_time(etSetChongdiantime.getText().toString());
                    } else {
                        flag3 = 1;
                    }
                }
                message.setId((long) 1);
                getDaoSession().getMessageBeanDao().update(message);

                Utils.saveFile_Message();
                if (flag1 == 1) {
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 1;
                    Handler_tip.sendMessage(msg);
                }
                if (flag2 == 1) {
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 2;
                    Handler_tip.sendMessage(msg);
                }
                if (flag3 == 1) {
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 3;
                    Handler_tip.sendMessage(msg);
                }
                if (flag1 == 0 && flag2 == 0 && flag3 == 0) {
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 4;
                    Handler_tip.sendMessage(msg);
                }
                break;

        }
    }


    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }
}
