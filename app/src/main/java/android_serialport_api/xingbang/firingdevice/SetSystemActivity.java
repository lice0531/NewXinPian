package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.suke.widget.SwitchButton;

import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.utils.PropertiesUtil;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android_serialport_api.xingbang.Application.getDaoSession;

public class SetSystemActivity extends BaseActivity {

    @BindView(R.id.sw_setsys)
    SwitchButton swSetsys;
    @BindView(R.id.sw_yanzheng)
    SwitchButton swYanzheng;
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
    private String Shangchuan = "";//是否上传错误雷管
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private PropertiesUtil mProp;
    private Handler Handler_tip = null;//提示信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 22);
        db = mMyDatabaseHelper.getWritableDatabase();
        mProp = PropertiesUtil.getInstance(this).init();
        mProp.open();
        Yanzheng = mProp.readString("Yanzheng", "验证");
        Shangchuan = mProp.readString("Shangchuan", "是");
        Log.e("验证", "Yanzheng: " + Yanzheng);
        Log.e("上传", "Shangchuan: " + Shangchuan);
        getUserMessage();
        Log.e("设置页面", "qiaosi_set: " + qiaosi_set);
        if (qiaosi_set.equals("true")) {
            swSetsys.setChecked(true);
        }
        if (Yanzheng.equals("验证")) {
            swYanzheng.setChecked(true);
        }
        if (Shangchuan.equals("是")) {
            swShangchuan.setChecked(true);
        }
        Handler_tip = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();
                String shellStr = b.getString("shellStr");
                if (msg.arg1 == 1) {
                    show_Toast(getString(R.string.text_systip_3));
                }else if(msg.arg1 == 2) {
                    show_Toast(getString(R.string.text_systip_2));
                }else if(msg.arg1 == 3) {
                    show_Toast("充电时间请大于8s");
                }else if(msg.arg1 == 4){
                    show_Toast(getString(R.string.text_systip_1));
                }


            }
        };
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
                if (swYanzheng.isChecked()) {
                    mProp.writeString("Yanzheng", "验证");
                } else {
                    mProp.writeString("Yanzheng", "不验证");
                }
                if (swShangchuan.isChecked()) {
                    mProp.writeString("Shangchuan", "是");
                } else {
                    mProp.writeString("Shangchuan", "否");
                }
                mProp.commit();

                ContentValues values = new ContentValues();
                if (swSetsys.isChecked()) {
                    values.put("qiaosi_set", "true");
                } else {
                    values.put("qiaosi_set", "false");
                }

                int flag1 = 0,flag2=0,flag3=0;
                if (!TextUtils.isEmpty(etSetPreparation.getText())) {//准备时间
                        Log.e("准备时间", "Preparation_time: " + etSetPreparation.getText().toString());
                        values.put("Preparation_time", etSetPreparation.getText().toString());
                }
                if (!TextUtils.isEmpty(etSetJiancetime.getText())) {//充电检测时间
                    if (Integer.parseInt(etSetJiancetime.getText().toString()) >= 5) {
                        Log.e("检测时间", "etSetJiancetime: " + etSetJiancetime.getText().toString());
                        values.put("jiance_time", etSetJiancetime.getText().toString());
                    } else {
                        flag2=1;
                    }
                }
                if (!TextUtils.isEmpty(etSetChongdiantime.getText())) {//充电时间
                    if (Integer.parseInt(etSetChongdiantime.getText().toString()) >= 8) {
                        Log.e("充电时间", "etSetChongdiantime: " + etSetChongdiantime.getText().toString());
                        values.put("chongdian_time", etSetChongdiantime.getText().toString());
                    } else {
                        flag3=1;
                    }
                }
                db.update(DatabaseHelper.TABLE_NAME_USER_MESSQGE, values, "id=?", new String[]{"1"});
                Utils.saveFile_Message();
                if(flag1==1){
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 1;
                    Handler_tip.sendMessage(msg);
                }
                if(flag2==1){
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 2;
                    Handler_tip.sendMessage(msg);
                }
                if(flag3==1){
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 3;
                    Handler_tip.sendMessage(msg);
                }
                if(flag1==0&&flag2==0&&flag3==0){
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
