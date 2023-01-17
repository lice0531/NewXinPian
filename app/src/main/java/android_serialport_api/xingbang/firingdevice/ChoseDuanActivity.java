package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.utils.MmkvUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoseDuanActivity extends AppCompatActivity {

    @BindView(R.id.tr_1)
    TableRow tr1;
    @BindView(R.id.re_num_f1)
    TextView reNumF1;
    @BindView(R.id.re_et_nei1)
    Button btnFan1;
    @BindView(R.id.re_btn_f1)
    Button btnDuan1;
    @BindView(R.id.tr_2)
    TableRow tr2;
    @BindView(R.id.re_num_f2)
    TextView reNumF2;
    @BindView(R.id.re_btn_f2)
    Button btnDuan2;
    @BindView(R.id.re_et_nei2)
    Button btnFan2;
    @BindView(R.id.tr_3)
    TableRow tr3;
    @BindView(R.id.re_num_f3)
    TextView reNumF3;
    @BindView(R.id.re_btn_f3)
    Button btnDuan3;
    @BindView(R.id.re_et_nei3)
    Button btnFan3;
    @BindView(R.id.tr_4)
    TableRow tr4;
    @BindView(R.id.re_num_f4)
    TextView reNumF4;
    @BindView(R.id.re_btn_f4)
    Button btnDuan4;
    @BindView(R.id.re_et_nei4)
    Button btnFan4;
    @BindView(R.id.tr_5)
    TableRow tr5;
    @BindView(R.id.re_num_f5)
    TextView reNumF5;
    @BindView(R.id.re_btn_f5)
    Button btnDuan5;
    @BindView(R.id.re_et_nei5)
    Button btnFan5;
    @BindView(R.id.tr_6)
    TableRow tr6;
    @BindView(R.id.re_num_f6)
    TextView reNumF6;
    @BindView(R.id.re_btn_f6)
    Button btnDuan6;
    @BindView(R.id.re_et_nei6)
    Button btnFan6;
    @BindView(R.id.tr_7)
    TableRow tr7;
    @BindView(R.id.re_num_f7)
    TextView reNumF7;
    @BindView(R.id.re_btn_f7)
    Button btnDuan7;
    @BindView(R.id.re_et_nei7)
    Button btnFan7;
    @BindView(R.id.tr_8)
    TableRow tr8;
    @BindView(R.id.re_num_f8)
    TextView reNumF8;
    @BindView(R.id.re_et_nei8)
    Button btnFan8;
    @BindView(R.id.re_btn_f8)
    Button btnDuan8;
    @BindView(R.id.tr_9)
    TableRow tr9;
    @BindView(R.id.re_num_f9)
    TextView reNumF9;
    @BindView(R.id.re_btn_f9)
    Button btnDuan9;
    @BindView(R.id.re_et_nei9)
    Button btnFan9;
    @BindView(R.id.tr_10)
    TableRow tr10;
    @BindView(R.id.re_num_f10)
    TextView reNumF10;
    @BindView(R.id.re_btn_f10)
    Button btnDuan10;
    @BindView(R.id.re_et_nei10)
    Button btnFan10;
    @BindView(R.id.tr_11)
    TableRow tr11;
    @BindView(R.id.re_num_f11)
    TextView reNumF11;
    @BindView(R.id.re_btn_f11)
    Button btnDuan11;
    @BindView(R.id.re_et_nei11)
    Button btnFan11;
    @BindView(R.id.tr_12)
    TableRow tr12;
    @BindView(R.id.re_num_f12)
    TextView reNumF12;
    @BindView(R.id.re_btn_f12)
    Button btnDuan12;
    @BindView(R.id.re_et_nei12)
    Button btnFan12;
    @BindView(R.id.tr_13)
    TableRow tr13;
    @BindView(R.id.re_num_f13)
    TextView reNumF13;
    @BindView(R.id.re_btn_f13)
    Button btnDuan13;
    @BindView(R.id.re_et_nei13)
    Button btnFan13;
    @BindView(R.id.tr_14)
    TableRow tr14;
    @BindView(R.id.re_num_f14)
    TextView reNumF14;
    @BindView(R.id.re_btn_f14)
    Button btnDuan14;
    @BindView(R.id.re_et_nei14)
    Button btnFan14;
    @BindView(R.id.tr_15)
    TableRow tr15;
    @BindView(R.id.re_num_f15)
    TextView reNumF15;
    @BindView(R.id.re_btn_f15)
    Button btnDuan15;
    @BindView(R.id.re_et_nei15)
    Button btnFan15;
    @BindView(R.id.tr_16)
    TableRow tr16;
    @BindView(R.id.re_num_f16)
    TextView reNumF16;
    @BindView(R.id.re_btn_f16)
    Button btnDuan16;
    @BindView(R.id.re_et_nei16)
    Button btnFan16;
    @BindView(R.id.tr_17)
    TableRow tr17;
    @BindView(R.id.re_num_f17)
    TextView reNumF17;
    @BindView(R.id.re_btn_f17)
    Button btnDuan17;
    @BindView(R.id.re_et_nei17)
    Button btnFan17;
    @BindView(R.id.tr_18)
    TableRow tr18;
    @BindView(R.id.re_num_f18)
    TextView reNumF18;
    @BindView(R.id.re_btn_f18)
    Button btnDuan18;
    @BindView(R.id.re_et_nei18)
    Button btnFan18;
    @BindView(R.id.tr_19)
    TableRow tr19;
    @BindView(R.id.re_num_f19)
    TextView reNumF19;
    @BindView(R.id.re_btn_f19)
    Button btnDuan19;
    @BindView(R.id.re_et_nei19)
    Button btnFan19;
    @BindView(R.id.tr_20)
    TableRow tr20;
    @BindView(R.id.re_num_f20)
    TextView reNumF20;
    @BindView(R.id.re_btn_f20)
    Button btnDuan20;
    @BindView(R.id.re_et_nei20)
    Button btnFan20;

    private String mOldTitle;   // 原标题
    private String mRegion;     // 区域

    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;

    //段属性
    private int duan = 1;//duan
    private int maxDuanNo = 3;
    private Handler mHandler_showNum = new Handler();//显示雷管数量
    private String duan_set = "0";//是duan1还是duan2
    private int f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20;
    private int n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20 = 0;
    private String TAG = "单发注册";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_duan);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();

        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        showDenatorSum();//显示雷管总数
                //初始化段间延时显示
        int maxduan = getMaxDuanNo();
        Log.e("显示", "maxduan: " + maxduan);
        if (maxduan < 3) {
            maxDuanNo = 3;
        } else {
            maxDuanNo = maxduan;
            for (int i = maxDuanNo; i > 3; i--) {
                setView(i);
                Log.e("显示", "maxDuanNo: " + maxDuanNo);
            }
        }
        //初始化雷管数量
        for (int i = 1; i < 21; i++) {
            showDuanSum(i);
        }
        //初始化翻转按钮颜色
        setFan();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            finish();//退出方法
        }
        return true;
    }

    public void hideInputKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.re_btn_f1, R.id.re_btn_f2, R.id.re_btn_f3,
            R.id.re_btn_f4, R.id.re_btn_f5, R.id.re_btn_f6, R.id.re_btn_f7,
            R.id.re_btn_f8, R.id.re_btn_f9, R.id.re_btn_f10, R.id.re_btn_f11, R.id.re_btn_f12, R.id.re_btn_f13,
            R.id.re_btn_f14, R.id.re_btn_f15, R.id.re_btn_f16, R.id.re_btn_f17, R.id.re_btn_f18, R.id.re_btn_f19,
            R.id.re_btn_f20, R.id.re_et_nei1, R.id.re_et_nei2, R.id.re_et_nei3,
            R.id.re_et_nei4, R.id.re_et_nei5, R.id.re_et_nei6, R.id.re_et_nei7,
            R.id.re_et_nei8, R.id.re_et_nei9, R.id.re_et_nei10, R.id.re_et_nei11, R.id.re_et_nei12, R.id.re_et_nei13,
            R.id.re_et_nei14, R.id.re_et_nei15, R.id.re_et_nei16, R.id.re_et_nei17, R.id.re_et_nei18, R.id.re_et_nei19,
            R.id.re_et_nei20,})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.re_btn_f1:
                hideInputKeyboard();
                duan = 1;
                initUI();
                reNumF1.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f2:
                hideInputKeyboard();
                duan = 2;
                initUI();
                reNumF2.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f3:
                hideInputKeyboard();
                duan = 3;
                initUI();
                reNumF3.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f4:
                hideInputKeyboard();
                duan = 4;
                initUI();
                reNumF4.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f5:
                hideInputKeyboard();
                duan = 5;
                initUI();
                reNumF5.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f6:
                hideInputKeyboard();
                duan = 6;
                initUI();
                reNumF6.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f7:
                hideInputKeyboard();
                duan = 7;
                initUI();
                reNumF7.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f8:
                hideInputKeyboard();
                duan = 8;
                initUI();
                reNumF8.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f9:
                hideInputKeyboard();
                duan = 9;
                initUI();
                reNumF9.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f10:
                hideInputKeyboard();
                duan = 10;

                initUI();
                reNumF10.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f11:
                hideInputKeyboard();
                duan = 11;
                initUI();
                reNumF11.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f12:
                hideInputKeyboard();
                duan = 12;
                initUI();
                reNumF12.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f13:
                hideInputKeyboard();
                duan = 13;
                initUI();
                reNumF13.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f14:
                hideInputKeyboard();
                duan = 14;
                initUI();
                reNumF14.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f15:
                hideInputKeyboard();
                duan = 15;
                initUI();
                reNumF15.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f16:
                hideInputKeyboard();
                duan = 16;
                initUI();
                reNumF16.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f17:
                hideInputKeyboard();
                duan = 17;
                initUI();
                reNumF17.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f18:
                hideInputKeyboard();
                duan = 18;
                initUI();
                reNumF18.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f19:
                hideInputKeyboard();
                duan = 19;
                initUI();
                reNumF19.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f20:
                hideInputKeyboard();
                duan = 20;
                initUI();
                reNumF20.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_et_nei1:
                fanzhuan(1);
                if (n1 == 1) {
                    n1 = 0;
                } else {
                    n1 = 1;
                }
                break;
            case R.id.re_et_nei2:
                fanzhuan(2);
                if (n2 == 1) {
                    n2 = 0;
                } else {
                    n2 = 1;
                }
                break;
            case R.id.re_et_nei3:
                fanzhuan(3);
                if (n3 == 1) {
                    n3 = 0;
                } else {
                    n3 = 1;
                }
                break;
            case R.id.re_et_nei4:
                fanzhuan(4);
                if (n4 == 1) {
                    n4 = 0;
                } else {
                    n4 = 1;
                }
                break;
            case R.id.re_et_nei5:
                fanzhuan(5);
                if (n5 == 1) {
                    n5 = 0;
                } else {
                    n5 = 1;
                }
                break;
            case R.id.re_et_nei6:
                fanzhuan(6);
                if (n6 == 1) {
                    n6 = 0;
                } else {
                    n6 = 1;
                }
                break;
            case R.id.re_et_nei7:
                fanzhuan(7);
                if (n7 == 1) {
                    n7 = 0;
                } else {
                    n7 = 1;
                }
                break;
            case R.id.re_et_nei8:
                fanzhuan(8);
                if (n8 == 1) {
                    n8 = 0;
                } else {
                    n8 = 1;
                }
                break;
            case R.id.re_et_nei9:
                fanzhuan(9);
                if (n9 == 1) {
                    n9 = 0;
                } else {
                    n9 = 1;
                }
                break;
            case R.id.re_et_nei10:
                fanzhuan(10);
                if (n10 == 1) {
                    n10 = 0;
                } else {
                    n10 = 1;
                }
                break;
            case R.id.re_et_nei11:
                fanzhuan(11);
                if (n11 == 1) {
                    n11 = 0;
                } else {
                    n11 = 1;
                }
                break;
            case R.id.re_et_nei12:
                fanzhuan(12);
                if (n12 == 1) {
                    n12 = 0;
                } else {
                    n12 = 1;
                }
                break;
            case R.id.re_et_nei13:
                fanzhuan(13);
                if (n13 == 1) {
                    n13 = 0;
                } else {
                    n13 = 1;
                }
                break;
            case R.id.re_et_nei14:
                fanzhuan(14);
                if (n14 == 1) {
                    n14 = 0;
                } else {
                    n14 = 1;
                }
                break;
            case R.id.re_et_nei15:
                fanzhuan(15);
                if (n15 == 1) {
                    n15 = 0;
                } else {
                    n15 = 1;
                }
                break;
            case R.id.re_et_nei16:
                fanzhuan(16);
                if (n16 == 1) {
                    n16 = 0;
                } else {
                    n16 = 1;
                }
                break;
            case R.id.re_et_nei17:
                fanzhuan(17);
                if (n17 == 1) {
                    n17 = 0;
                } else {
                    n17 = 1;
                }
                break;
            case R.id.re_et_nei18:
                fanzhuan(18);
                if (n18 == 1) {
                    n18 = 0;
                } else {
                    n18 = 1;
                }
                break;
            case R.id.re_et_nei19:
                fanzhuan(19);
                if (n19 == 1) {
                    n19 = 0;
                } else {
                    n19 = 1;
                }
                break;
            case R.id.re_et_nei20:
                fanzhuan(20);
                if (n20 == 1) {
                    n20 = 0;
                } else {
                    n20 = 1;
                }
                break;

        }
    }

    private int showDenatorSum() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryDetonatorRegionDesc(mRegion);
//        txtReisteramount.setText("已注册:" + list.size());
        return list.size();
    }

    private void setView(int i) {

        switch (i) {
            case 4:
                tr4.setVisibility(View.VISIBLE);
                break;
            case 5:
                tr5.setVisibility(View.VISIBLE);
                break;
            case 6:
                tr6.setVisibility(View.VISIBLE);
                break;
            case 7:
                tr7.setVisibility(View.VISIBLE);
                break;
            case 8:
                tr8.setVisibility(View.VISIBLE);
                break;
            case 9:
                tr9.setVisibility(View.VISIBLE);
                break;
            case 10:
                tr10.setVisibility(View.VISIBLE);
                break;
            case 11:
                tr11.setVisibility(View.VISIBLE);
                break;
            case 12:
                tr12.setVisibility(View.VISIBLE);
                break;
            case 13:
                tr13.setVisibility(View.VISIBLE);
                break;
            case 14:
                tr14.setVisibility(View.VISIBLE);
                break;
            case 15:
                tr15.setVisibility(View.VISIBLE);
                break;
            case 16:
                tr16.setVisibility(View.VISIBLE);
                break;
            case 17:
                tr17.setVisibility(View.VISIBLE);
                break;
            case 18:
                tr18.setVisibility(View.VISIBLE);
                break;
            case 19:
                tr19.setVisibility(View.VISIBLE);
                break;
            case 20:
                tr20.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void fanzhuan(int duan) {
        Log.e("注册页面", "翻转: ");
        AlertDialog dialog = new AlertDialog.Builder(ChoseDuanActivity.this)
                .setTitle("翻转提示")//设置对话框的标题//"成功起爆"
                .setMessage("是否翻转当前段位延时")//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                .setNegativeButton("取消", (dialog12, which) -> dialog12.dismiss())
                .setPositiveButton("确认", (dialog1, which) -> {

                    GreenDaoMaster master = new GreenDaoMaster();
                    List<DenatorBaseinfo> list = master.queryLeiguanDuan(duan, mRegion);
                    List<DenatorBaseinfo> list2 = master.queryLeiguanDuan(duan, mRegion);
                    for (int i = 0; i < list.size(); i++) {
                        DenatorBaseinfo lg = list.get(i);
                        lg.setDelay(list2.get(list.size() - 1 - i).getDelay());
                        getDaoSession().getDenatorBaseinfoDao().update(lg);
                    }
//                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                    setBtnColor(duan);
                }).create();

        dialog.show();
    }

    private void setBtnColor(int duanChose) {
        switch (duanChose) {
            case 1:
                Log.e("注册", "n1: " + n1);
                if (n1 == 1) {
                    btnFan1.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan1.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n1", n1);
                break;
            case 2:
                if (n2 == 1) {
                    btnFan2.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan2.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n2", n2);
                break;
            case 3:
                if (n3 == 1) {
                    btnFan3.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan3.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n3", n3);
                break;
            case 4:
                if (n4 == 1) {
                    btnFan4.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan4.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n4", n4);
                break;
            case 5:
                if (n5 == 1) {
                    btnFan5.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan5.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n5", n5);
                break;
            case 6:
                if (n6 == 1) {
                    btnFan6.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan6.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n6", n6);
                break;
            case 7:
                if (n7 == 1) {
                    btnFan7.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan7.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n7", n7);
                break;
            case 8:
                if (n8 == 1) {
                    btnFan8.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan8.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n8", n8);
                break;
            case 9:
                if (n9 == 1) {
                    btnFan9.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan9.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n9", n9);
                break;
            case 10:
                if (n10 == 1) {
                    btnFan10.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan10.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n10", n10);
                break;
            case 11:
                if (n11 == 1) {
                    btnFan11.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan11.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n11", n11);
                break;
            case 12:
                if (n12 == 1) {
                    btnFan12.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan12.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n12", n12);
                break;
            case 13:
                if (n13 == 1) {
                    btnFan13.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan13.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n13", n13);
                break;
            case 14:
                if (n14 == 1) {
                    btnFan14.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan14.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n14", n14);
                break;
            case 15:
                if (n15 == 1) {
                    btnFan15.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan15.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n15", n15);
                break;
            case 16:
                if (n16 == 1) {
                    btnFan16.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan16.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n16", n16);
                break;
            case 17:
                if (n17 == 1) {
                    btnFan17.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan17.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n17", n17);
                break;
            case 18:
                if (n18 == 1) {
                    btnFan18.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan18.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n18", n18);
                break;
            case 19:
                if (n19 == 1) {
                    btnFan19.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan9.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n19", n19);
                break;
            case 20:
                if (n20 == 1) {
                    btnFan20.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan20.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode("n20", n20);
                break;
        }
    }

    private void setFan() {
        n1 = (int) MmkvUtils.getcode("n1", 0);
        n2 = (int) MmkvUtils.getcode("n2", 0);
        n3 = (int) MmkvUtils.getcode("n3", 0);
        n4 = (int) MmkvUtils.getcode("n4", 0);
        n5 = (int) MmkvUtils.getcode("n5", 0);
        n6 = (int) MmkvUtils.getcode("n6", 0);
        n7 = (int) MmkvUtils.getcode("n7", 0);
        n8 = (int) MmkvUtils.getcode("n8", 0);
        n9 = (int) MmkvUtils.getcode("n9", 0);
        n10 = (int) MmkvUtils.getcode("n10", 0);
        n11 = (int) MmkvUtils.getcode("n11", 0);
        n12 = (int) MmkvUtils.getcode("n12", 0);
        n13 = (int) MmkvUtils.getcode("n13", 0);
        n14 = (int) MmkvUtils.getcode("n14", 0);
        n15 = (int) MmkvUtils.getcode("n15", 0);
        n16 = (int) MmkvUtils.getcode("n16", 0);
        n17 = (int) MmkvUtils.getcode("n17", 0);
        n18 = (int) MmkvUtils.getcode("n18", 0);
        n19 = (int) MmkvUtils.getcode("n19", 0);
        n20 = (int) MmkvUtils.getcode("n20", 0);
        for (int i = 1; i < 21; i++) {
            setBtnColor(i);
        }
    }

    public void initUI() {
        Log.e(TAG, "duan: "+duan );
        MmkvUtils.savecode("duan",duan);
        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putString("data_return", duan+"");
        intent.putExtras(bundle);
        setResult(0,intent);
        reNumF1.setBackgroundResource(R.drawable.translucent);
        reNumF2.setBackgroundResource(R.drawable.translucent);
        reNumF3.setBackgroundResource(R.drawable.translucent);
        reNumF4.setBackgroundResource(R.drawable.translucent);
        reNumF5.setBackgroundResource(R.drawable.translucent);
        reNumF6.setBackgroundResource(R.drawable.translucent);
        reNumF7.setBackgroundResource(R.drawable.translucent);
        reNumF8.setBackgroundResource(R.drawable.translucent);
        reNumF9.setBackgroundResource(R.drawable.translucent);
        reNumF10.setBackgroundResource(R.drawable.translucent);
        reNumF11.setBackgroundResource(R.drawable.translucent);
        reNumF12.setBackgroundResource(R.drawable.translucent);
        reNumF13.setBackgroundResource(R.drawable.translucent);
        reNumF14.setBackgroundResource(R.drawable.translucent);
        reNumF15.setBackgroundResource(R.drawable.translucent);
        reNumF16.setBackgroundResource(R.drawable.translucent);
        reNumF17.setBackgroundResource(R.drawable.translucent);
        reNumF18.setBackgroundResource(R.drawable.translucent);
        reNumF19.setBackgroundResource(R.drawable.translucent);
        reNumF20.setBackgroundResource(R.drawable.translucent);

//        lySetDelay.setFocusable(true);
//        lySetDelay.setFocusableInTouchMode(true);
//        lySetDelay.requestFocus();
    }

    /***
     * 得到某段的总数
     * @return
     */
    private int getDuanNo(int duan) {
        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where duan =?", new String[]{duan + ""});
        int totalNum = cursor.getCount();//得到数据的总条数
        cursor.close();
        return totalNum;
    }
    /***
     * 得到某段的总数
     * @return
     */
    private int getDuanNo(int duan,String piece) {
        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where duan =? and piece = ? ", new String[]{duan + "",piece});
        int totalNum = cursor.getCount();//得到数据的总条数
        cursor.close();
        return totalNum;
    }
    /***
     * 得到某段的总数
     * @return
     */
    private int getDuanByDenatorNo(String shellBlastNo) {
        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where shellBlastNo =?", new String[]{shellBlastNo + ""});
        if (cursor != null && cursor.moveToNext()) {
            int duan = cursor.getInt(15);
            cursor.close();
            return duan;
        }
        return 1;
    }

    /**
     * 显示雷管数量
     */
    private void showDuanSum(int a) {
        List<DenatorBaseinfo> list = new GreenDaoMaster().queryDetonatorRegionAndDUanAsc(mRegion, a);
        int totalNum = list.size();//得到数据的总条数
        Log.e(TAG, "当前区域段数totalNum: " + totalNum);
        switch (a) {
            case 1:
                reNumF1.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n1", 0);
                }
                break;
            case 2:
                reNumF2.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n2", 0);
                }
                break;
            case 3:
                reNumF3.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n3", 0);
                }
                break;
            case 4:
                reNumF4.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n4", 0);
                }
                break;
            case 5:
                reNumF5.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n5", 0);
                }
                break;
            case 6:
                reNumF6.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n6", 0);
                }
                break;
            case 7:
                reNumF7.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n7", 0);
                }
                break;
            case 8:
                reNumF8.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n8", 0);
                }
                break;
            case 9:
                reNumF9.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n9", 0);
                }
                break;
            case 10:
                reNumF10.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n10", 0);
                }
                break;
            case 11:
                reNumF11.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n11", 0);
                }
                break;
            case 12:
                reNumF12.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n12", 0);
                }
                break;
            case 13:
                reNumF13.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n13", 0);
                }
                break;
            case 14:
                reNumF14.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n14", 0);
                }
                break;
            case 15:
                reNumF15.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n15", 0);
                }
                break;
            case 16:
                reNumF16.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n16", 0);
                }
                break;
            case 17:
                reNumF17.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n17", 0);
                }
                break;
            case 18:
                reNumF18.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n18", 0);
                }
                break;
            case 19:
                reNumF19.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n19", 0);
                }
                break;
            case 20:
                reNumF20.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n20", 0);
                }
                break;
        }
    }

    /**
     * 获取最大段号
     */
    private int getMaxDuanNo() {
        Cursor cursor = db.rawQuery("select max(duan) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where piece =? ", new String[]{mRegion});
        if (cursor != null && cursor.moveToNext()) {
            String maxDuan = cursor.getString(0);
            if (maxDuan != null) {
                cursor.close();
                Log.e("获取最大段号", "maxDuan: " + maxDuan);
                return Integer.parseInt(maxDuan);
            }
        }
        return 3;
    }
}