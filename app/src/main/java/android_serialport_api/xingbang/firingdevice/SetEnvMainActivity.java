package android_serialport_api.xingbang.firingdevice;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.bugly.Bugly;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tencent.bugly.beta.Beta.checkUpgrade;

/**
 * 设置页面
 */
public class SetEnvMainActivity extends BaseActivity {

    @BindView(R.id.btn_set_user)//用户管理
    Button btnSetUser;
    @BindView(R.id.btn_set_onlineuprade)//在线升级
    Button btnSetOnlineuprade;
    @BindView(R.id.btn_set_netmodel)//网络模式
    Button btnSetNetmodel;
    @BindView(R.id.btn_set_upload)//上传数据
    Button btnSetUpload;
    @BindView(R.id.btn_set_warrant)//授权码数据
    Button btnSetWarrant;
    @BindView(R.id.btn_set_dechip)//雷管芯片
    Button btnSetDechip;
    @BindView(R.id.btn_set_facCode)//厂家码
    Button btnSetFacCode;
    @BindView(R.id.btn_set_flow)//设备编号
    Button btnSetFlow;
    @BindView(R.id.btn_set_ver)//版本
    Button btnSetVer;
    @BindView(R.id.btn_set_system)//系统设置
    Button btnSetSystem;
    @BindView(R.id.btn_set_exit)//退出
    Button btnSetExit;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    private SQLiteDatabase db;
    private DatabaseHelper mMyDatabaseHelper;
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String server_addr = "";
    private String server_port = "";
    private String server_http = "";
    private String server_ip = "";
    private String server_type1 = "";
    private String server_type2 = "";
    private String denator_Type_isSelected = "";//是否设置雷管最大延时
    private int Preparation_time;//准备时间
    private int ChongDian_time;//准备时间
    private String qiaosi_set = "";//是否检测桥丝


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setvpage);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        getUserMessage();
    }

    private void getUserMessage() {
        MessageBean bean = GreenDaoMaster.getAllFromInfo_bean();
//            Preparation_time = Integer.parseInt(message.get(0).getJiance_time());
        Preparation_time = Integer.parseInt(bean.getPreparation_time());//跟起爆测试一样
        pro_bprysfz = bean.getPro_bprysfz();
        pro_htid = bean.getPro_htid();
        pro_xmbh = bean.getPro_xmbh();
        equ_no = bean.getEqu_no();
        pro_coordxy = bean.getPro_coordxy();
        server_addr = bean.getServer_addr();
        server_port = bean.getServer_port();
        server_http = bean.getServer_http();
        server_ip = bean.getServer_ip();
        qiaosi_set = bean.getQiaosi_set();
        ChongDian_time = Integer.parseInt(bean.getChongdian_time());
        server_type1 = bean.getServer_type1();
        server_type2 = bean.getServer_type2();
    }




    public int checkUserName(String userName, String pw) {
        String selection = "uname = ? and upassword =?"; // 选择条件，给null查询所有
        String[] selectionArgs = {userName + "", pw + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER_MAIN, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            boolean flag = cursor.moveToFirst();
            cursor.close();
            if (flag)
                return 1;
            else
                return 0;
        } else {
            //if(cursor != null)cursor.close();
            return 0;
        }
    }


    private void setServerIpEnv() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SetEnvMainActivity.this);
        //  builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getString(R.string.text_alert_fwqsz));//"服务器设置"
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(SetEnvMainActivity.this).inflate(R.layout.setenv_activity_serverip_page, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        final EditText et_server_ip = (EditText) view.findViewById(R.id.server_ip);//服务器地址
        final EditText et_server_port = (EditText) view.findViewById(R.id.server_port);//端口
        final TextView et_server_http = (TextView) view.findViewById(R.id.server_http);//网址
        final TextView et_server_http_test = (TextView) view.findViewById(R.id.server_http_cs);//测试网址
        final LinearLayout Linear_dl = (LinearLayout) view.findViewById(R.id.denator_danling_func);//丹灵按钮
        final LinearLayout Linear_zbw = (LinearLayout) view.findViewById(R.id.denator_del_func);//中爆按钮
        final LinearLayout denator_danling_cs = (LinearLayout) view.findViewById(R.id.denator_danling_cs);//中爆按钮
        final CheckBox cb_dl = (CheckBox) view.findViewById(R.id.rbtn_danling);
        final CheckBox cb_zbw = (CheckBox) view.findViewById(R.id.rbtn_zbw);
        final CheckBox cb_test = (CheckBox) view.findViewById(R.id.rbtn_cs);
        if (!TextUtils.isEmpty(server_ip)) {
            et_server_ip.setText(server_ip);
        }
        if (!TextUtils.isEmpty(server_port)) {
            et_server_port.setText(server_port);
        }
        if (!TextUtils.isEmpty(server_http)) {
            et_server_http.setText(server_http);
        }
        if ("1".equals(server_type1)) {
            cb_dl.setChecked(true);
            Linear_dl.setVisibility(View.VISIBLE);
        } else {
            Linear_dl.setVisibility(View.GONE);
        }
        if ("2".equals(server_type2)) {
            cb_zbw.setChecked(true);
            Linear_zbw.setVisibility(View.VISIBLE);
        } else {
            Linear_zbw.setVisibility(View.GONE);
        }
        cb_dl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_dl.isChecked()) {
                    Linear_dl.setVisibility(View.VISIBLE);
                } else {
                    Linear_dl.setVisibility(View.GONE);
                }
            }
        });
        cb_zbw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_zbw.isChecked()) {
                    Linear_zbw.setVisibility(View.VISIBLE);
                } else {
                    Linear_zbw.setVisibility(View.GONE);
                }
            }
        });
        //测试网址
        cb_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_test.isChecked()) {
                    denator_danling_cs.setVisibility(View.VISIBLE);
                    et_server_http_test.setText(Utils.httpurl_down_test);
                } else {
                    denator_danling_cs.setVisibility(View.GONE);
                }
            }
        });

        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String server_ip = et_server_ip.getText().toString().trim();//地址
                String server_port = et_server_port.getText().toString().trim();//端口
                String server_http = et_server_http.getText().toString().trim();//网址
                String type = "";
                String type2 = "";
                if (cb_dl.isChecked()) {
                    type = "1";
                } else {
                    type = "0";
                }
                if (cb_zbw.isChecked()) {
                    type2 = "2";
                } else {
                    type2 = "0";
                }
                ContentValues values = new ContentValues();
                values.put("server_type1", type);//key为字段名，value为值
                values.put("server_type2", type2);
                if (!TextUtils.isEmpty(server_ip)) {
                    values.put("server_ip", server_ip);//中爆网址
                } else {
                    show_Toast("ip地址不能为空");
                }
                if (!TextUtils.isEmpty(server_port)) {
                    values.put("server_port", server_port);//中爆端口
                } else {
                    show_Toast("ip端口不能为空");
                }
                if (!TextUtils.isEmpty(server_http)) {
                    values.put("server_http", server_http);//丹灵网址
                }
                db.update(DatabaseHelper.TABLE_NAME_USER_MESSQGE, values, "id=?", new String[]{"1"});
                dialog.dismiss();
                getUserMessage();
                Utils.saveFile_Message();//保存用户信息
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //builder.
                dialog.dismiss();
            }
        });
        builder.show();


    }

    private void setEquNoEnv() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SetEnvMainActivity.this);
        builder.setTitle(getString(R.string.text_alert_sbbh));//"设备编号设置"
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(SetEnvMainActivity.this).inflate(R.layout.setenv_activity_equno_page, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        final EditText current_No = (EditText) view.findViewById(R.id.current_no);
        final EditText newequ_No = (EditText) view.findViewById(R.id.newequ_no);
        current_No.setText(equ_no);
        builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String b = newequ_No.getText().toString().trim();
                ContentValues values = new ContentValues();
                if (!TextUtils.isEmpty(b)) {
                    values.put("equ_no", b);//设备编号
                } else {
                    values.put("equ_no", "");//设备编号
                    show_Toast("请注意,您已设置设备编号为空");
                }
                db.update(DatabaseHelper.TABLE_NAME_USER_MESSQGE, values, "id=?", new String[]{"1"});
                dialog.dismiss();
                getUserMessage();
                Utils.saveFile_Message();//保存用户信息
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //builder.
                dialog.dismiss();
            }
        });
        builder.show();


    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }


    @OnClick({R.id.btn_set_user, R.id.btn_set_onlineuprade, R.id.btn_set_netmodel,
            R.id.btn_set_upload, R.id.btn_set_warrant, R.id.btn_set_dechip, R.id.btn_set_facCode,
            R.id.btn_set_flow, R.id.btn_set_ver, R.id.btn_set_system, R.id.btn_set_exit, R.id.btn_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_set_user:
                String str1 = new String("注册");
                Intent intent = new Intent(SetEnvMainActivity.this, SetUserActivity.class);
                intent.putExtra("dataSend", str1);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_set_onlineuprade://在线升级
//                show_Toast(getString(R.string.text_error_tip57));
                checkUpgrade();
                Log.e("版本号", "Bugly.getAppChannel(): " + Bugly.getAppChannel());
                break;
            case R.id.btn_set_netmodel://日志
//                Intent intent5 = new Intent(SetEnvMainActivity.this, WriteLogActivity.class);
//                startActivity(intent5);
                break;
            case R.id.btn_set_upload://上传数据
                setServerIpEnv();
                break;
            case R.id.btn_set_warrant://授权码
                show_Toast(getString(R.string.text_error_tip57));
                break;
            case R.id.btn_set_dechip://雷管芯片
                startActivity(new Intent(SetEnvMainActivity.this, SetDenatorTypeActivity.class));
                break;
            case R.id.btn_set_facCode:
                String str2 = new String(getString(R.string.text_lgxp_setFac));//"厂家设置"
                Intent intent2 = new Intent(SetEnvMainActivity.this, SetFactoryActivity.class);
                intent2.putExtra("dataSend", str2);
                startActivityForResult(intent2, 1);
                break;
            case R.id.btn_set_flow:
                setEquNoEnv();//设置起爆编号
                break;
            case R.id.btn_set_ver://查看版本号
                Intent intent6 = new Intent(SetEnvMainActivity.this, ZhuCeActivity.class);
                startActivityForResult(intent6, 1);
                break;
            case R.id.btn_set_system:
                Intent intent3 = new Intent(SetEnvMainActivity.this, SetSystemActivity.class);
                String str3 = new String("系统设置");
                intent3.putExtra("dataSend", str3);
                startActivityForResult(intent3, 1);
                break;
            case R.id.btn_set_exit:
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
                break;
            case R.id.btn_upload:
                //信息采集
                Intent intent4 = new Intent(SetEnvMainActivity.this, PracticeActivity.class);
                startActivity(intent4);
                break;
        }
    }

    @OnClick()
    public void onViewClicked() {
    }
}
