package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.suke.widget.SwitchButton;

import java.util.List;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.jilian.FirstEvent;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SetSystemActivity extends BaseActivity {
    @BindView(R.id.sw_yanzheng_sq)
    SwitchButton swYanzheng_sq;
    @BindView(R.id.sw_fujian)
    SwitchButton swFujian;
    @BindView(R.id.sw_setsys)
    SwitchButton swSetsys;
    @BindView(R.id.sw_yanzheng)
    SwitchButton swYanzheng;
    @BindView(R.id.sw_shangchuan)
    SwitchButton swShangchuan;
    @BindView(R.id.sw_qzqb)
    SwitchButton swQzqb;
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
    @BindView(R.id.et_set_qibaotime)
    EditText etSetQibaotime;
    private int ChongDian_time;//准备时间
    private int JianCe_time;//准备时间
    private String qiaosi_set = "";//是否检测桥丝
    private String Yanzheng = "";//是否验证地理位置
    private String Shangchuan = "";//是否上传错误雷管
    private String changjia = "";
    private String Qzqb = "";//是否强制起爆（起爆时有错误雷管是否继续）
    private String Qibaotime = "5";//设置起爆等待时间
    private String Yanzheng_sq = "";//是否验雷管已经授权
    private String Fujian = "";//是否复检
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
        AppLogUtils.writeAppXBLog("--进入系统设置页面--");
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        Yanzheng = (String) MmkvUtils.getcode("Yanzheng", "验证");
        Shangchuan = (String) MmkvUtils.getcode("Shangchuan", "否");
        Qibaotime = (String) MmkvUtils.getcode("Qibaotime", "5");
        Yanzheng_sq = (String) MmkvUtils.getcode("Yanzheng_sq", "验证");
        Fujian = (String) MmkvUtils.getcode("Fujian", "不复检");
        Qzqb = (String) MmkvUtils.getcode("Qzqb", "是");
        changjia = (String) MmkvUtils.getcode("sys_ver_name", "TY");
        getUserMessage();
        Log.e("设置页面", "qiaosi_set: " + qiaosi_set);
        Log.e("设置页面", "Shangchuan: " + Shangchuan);
        Log.e("设置页面", "Yanzheng_sq: " + Yanzheng_sq);
        Log.e("设置页面", "Fujian: " + Fujian);
        if (qiaosi_set.equals("true")) {
            swSetsys.setChecked(true);
        }
        if (Yanzheng.equals("验证")) {
            swYanzheng.setChecked(true);
        }
        if (Yanzheng_sq.equals("验证")) {
            swYanzheng_sq.setChecked(true);
        }
        if (Fujian.equals("复检")) {
            swFujian.setChecked(true);
        }
        if (Shangchuan.equals("是")) {
            swShangchuan.setChecked(true);
        }
        if (Qzqb.equals("是")) {
            swQzqb.setChecked(true);
        }
        etSetQibaotime.setText(Qibaotime);
        Handler_tip = new Handler(msg -> {
            Bundle b = msg.getData();
            String shellStr = b.getString("shellStr");
            if (msg.arg1 == 1) {
                show_Toast(getString(R.string.text_systip_3));
            } else if (msg.arg1 == 2) {
                show_Toast(getString(R.string.text_systip_2));
            } else if (msg.arg1 == 3) {
                show_Toast(getString(R.string.text_systip_11));
            }  else if (msg.arg1 == 5) {
                show_Toast(getString(R.string.text_systip_12));
            } else if (msg.arg1 == 4) {
                show_Toast(getString(R.string.text_systip_1));
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SetSystemActivity.this);
                builder.setTitle(getString(R.string.text_czts));//"请输入用户名和密码"
                View view2 = LayoutInflater.from(SetSystemActivity.this).inflate(R.layout.userlogindialog_set, null);
                builder.setView(view2);
                final EditText password = view2.findViewById(R.id.password);
                builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {

                    String b = password.getText().toString().trim();
                    if (b == null || b.trim().length() < 1) {
                        show_Toast(getString(R.string.text_alert_password));
                        return;
                    }
                    if ( b.equals("A6XBSM")) {
                        Intent intent = new Intent(SetSystemActivity.this, SetVoltageActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        show_Toast(getString(R.string.text_mmcw));
                    }
                });
                builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> dialog.dismiss());


                builder.show();

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
                if (swShangchuan.isChecked()) {
                    MmkvUtils.savecode("Shangchuan", "是");
                } else {
                    MmkvUtils.savecode("Shangchuan", "否");
                }
                if (swYanzheng_sq.isChecked()) {
                    MmkvUtils.savecode("Yanzheng_sq", "验证");
                    Log.e("设置页面", "Yanzheng_sq: " + "验证");
                } else {
                    MmkvUtils.savecode("Yanzheng_sq", "不验证");
                }
                if (swFujian.isChecked()) {
                    MmkvUtils.savecode("Fujian", "复检");
                    Log.e("设置页面", "Fujian: " + "复检");
                } else {
                    MmkvUtils.savecode("Fujian", "不复检");
                }
                if (swSetsys.isChecked()) {
                    message.setQiaosi_set("true");
                } else {
                    message.setQiaosi_set("false");
                }
                if (swQzqb.isChecked()) {
                    MmkvUtils.savecode("Qzqb", "是");
                    MmkvUtils.savecode("Shangchuan", "是");
                } else {
                    MmkvUtils.savecode("Qzqb", "否");
                    MmkvUtils.savecode("Shangchuan", "否");
                }
                int flag1 = 0, flag2 = 0, flag3 = 0;
                if (!TextUtils.isEmpty(etSetPreparation.getText())&&Integer.parseInt(etSetPreparation.getText().toString())>47) {//准备时间
                    message.setPreparation_time(etSetPreparation.getText().toString());
                    Log.e("准备时间", "Preparation_time: " + etSetPreparation.getText().toString());
                } else {
                    flag1 = 1;
                }
                if (!TextUtils.isEmpty(etSetJiancetime.getText())&&Integer.parseInt(etSetJiancetime.getText().toString())>47) {//组网检测时间
                    Log.e("组网检测时间", "etSetJiancetime: " + etSetJiancetime.getText().toString());
                    message.setJiance_time(etSetJiancetime.getText().toString());
                } else {
                    flag2 = 1;
                }
                if (!TextUtils.isEmpty(etSetChongdiantime.getText())) {//高压充电时间
                    if (Integer.parseInt(etSetChongdiantime.getText().toString()) >= 68) {
                        Log.e("充电时间", "etSetChongdiantime: " + etSetChongdiantime.getText().toString());
                        message.setChongdian_time(etSetChongdiantime.getText().toString());
                    } else {
                        flag3 = 1;
                    }
                }
                if (!TextUtils.isEmpty(etSetQibaotime.getText())) {//起爆等待时间
                    Log.e("起爆等待时间", "etSetQibaotime: " + etSetQibaotime.getText().toString());
                    MmkvUtils.savecode("Qibaotime", etSetQibaotime.getText());
                    AppLogUtils.writeAppLog("设置的起爆按键倒计时时间是:" + etSetQibaotime.getText());
                } else {
                    flag2 = 1;
                }
                message.setId((long) 1);
                getDaoSession().getMessageBeanDao().update(message);

                Utils.saveFile_Message();
                if (flag1 == 1) {
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 3;
                    Handler_tip.sendMessage(msg);
                }
                if (flag2 == 1) {
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 3;
                    Handler_tip.sendMessage(msg);
                }
                if (flag3 == 1) {
                    Message msg = Handler_tip.obtainMessage();
                    msg.arg1 = 5;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FirstEvent event) {
        if (!TextUtils.isEmpty(event.getMsg()) && "setChangJia".equals(event.getMsg())) {
            changjia = event.getData();
            MmkvUtils.savecode("sys_ver_name",changjia);
            //验证雷管收否授权: 默认都为是
            swYanzheng_sq.setChecked(true);
            MmkvUtils.savecode("Yanzheng_sq", "验证");
            if(changjia.equals("XJ")) {
                //厂家为XJ时，有错误雷管不能接续起爆  是否上传错误雷管:否
                swQzqb.setChecked(false);
                swShangchuan.setChecked(false);
                MmkvUtils.savecode("Qzqb", "否");
                MmkvUtils.savecode("Shangchuan", "否");
            } else {
                swQzqb.setChecked(true);
                swShangchuan.setChecked(true);
                MmkvUtils.savecode("Shangchuan", "是");
                MmkvUtils.savecode("Qzqb", "是");
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
