package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

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

import androidx.appcompat.app.AppCompatActivity;

import com.coder.vincent.smart_toast.SmartToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.greenDao.DaoSession;
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
    @BindView(R.id.re_num_f21)
    TextView reNumF21;
    @BindView(R.id.re_et_nei21)
    Button btnFan21;
    @BindView(R.id.re_btn_f21)
    Button btnDuan21;
    @BindView(R.id.tr_22)
    TableRow tr22;
    @BindView(R.id.re_num_f22)
    TextView reNumF22;
    @BindView(R.id.re_btn_f22)
    Button btnDuan22;
    @BindView(R.id.re_et_nei22)
    Button btnFan22;
    @BindView(R.id.tr_23)
    TableRow tr23;
    @BindView(R.id.re_num_f23)
    TextView reNumF23;
    @BindView(R.id.re_btn_f23)
    Button btnDuan23;
    @BindView(R.id.re_et_nei23)
    Button btnFan23;
    @BindView(R.id.tr_24)
    TableRow tr24;
    @BindView(R.id.re_num_f24)
    TextView reNumF24;
    @BindView(R.id.re_btn_f24)
    Button btnDuan24;
    @BindView(R.id.re_et_nei24)
    Button btnFan24;
    @BindView(R.id.tr_25)
    TableRow tr25;
    @BindView(R.id.re_num_f25)
    TextView reNumF25;
    @BindView(R.id.re_btn_f25)
    Button btnDuan25;
    @BindView(R.id.re_et_nei25)
    Button btnFan25;
    @BindView(R.id.tr_26)
    TableRow tr26;
    @BindView(R.id.re_num_f26)
    TextView reNumF26;
    @BindView(R.id.re_btn_f26)
    Button btnDuan26;
    @BindView(R.id.re_et_nei26)
    Button btnFan26;
    @BindView(R.id.tr_27)
    TableRow tr27;
    @BindView(R.id.re_num_f27)
    TextView reNumF27;
    @BindView(R.id.re_btn_f27)
    Button btnDuan27;
    @BindView(R.id.re_et_nei27)
    Button btnFan27;
    @BindView(R.id.tr_28)
    TableRow tr28;
    @BindView(R.id.re_num_f28)
    TextView reNumF28;
    @BindView(R.id.re_et_nei28)
    Button btnFan28;
    @BindView(R.id.re_btn_f28)
    Button btnDuan28;
    @BindView(R.id.tr_29)
    TableRow tr29;
    @BindView(R.id.re_num_f29)
    TextView reNumF29;
    @BindView(R.id.re_btn_f29)
    Button btnDuan29;
    @BindView(R.id.re_et_nei29)
    Button btnFan29;
    @BindView(R.id.tr_30)
    TableRow tr30;
    @BindView(R.id.re_num_f30)
    TextView reNumF30;
    @BindView(R.id.re_btn_f30)
    Button btnDuan30;
    @BindView(R.id.re_et_nei30)
    Button btnFan30;
    @BindView(R.id.tr_31)
    TableRow tr31;
    @BindView(R.id.re_num_f31)
    TextView reNumF31;
    @BindView(R.id.re_btn_f31)
    Button btnDuan31;
    @BindView(R.id.re_et_nei31)
    Button btnFan31;
    @BindView(R.id.tr_32)
    TableRow tr32;
    @BindView(R.id.re_num_f32)
    TextView reNumF32;
    @BindView(R.id.re_btn_f32)
    Button btnDuan32;
    @BindView(R.id.re_et_nei32)
    Button btnFan32;
    @BindView(R.id.tr_33)
    TableRow tr33;
    @BindView(R.id.re_num_f33)
    TextView reNumF33;
    @BindView(R.id.re_btn_f33)
    Button btnDuan33;
    @BindView(R.id.re_et_nei33)
    Button btnFan33;
    @BindView(R.id.tr_34)
    TableRow tr34;
    @BindView(R.id.re_num_f34)
    TextView reNumF34;
    @BindView(R.id.re_btn_f34)
    Button btnDuan34;
    @BindView(R.id.re_et_nei34)
    Button btnFan34;
    @BindView(R.id.tr_35)
    TableRow tr35;
    @BindView(R.id.re_num_f35)
    TextView reNumF35;
    @BindView(R.id.re_btn_f35)
    Button btnDuan35;
    @BindView(R.id.re_et_nei35)
    Button btnFan35;
    @BindView(R.id.tr_36)
    TableRow tr36;
    @BindView(R.id.re_num_f36)
    TextView reNumF36;
    @BindView(R.id.re_btn_f36)
    Button btnDuan36;
    @BindView(R.id.re_et_nei36)
    Button btnFan36;
    @BindView(R.id.tr_37)
    TableRow tr37;
    @BindView(R.id.re_num_f37)
    TextView reNumF37;
    @BindView(R.id.re_btn_f37)
    Button btnDuan37;
    @BindView(R.id.re_et_nei37)
    Button btnFan37;
    @BindView(R.id.tr_38)
    TableRow tr38;
    @BindView(R.id.re_num_f38)
    TextView reNumF38;
    @BindView(R.id.re_btn_f38)
    Button btnDuan38;
    @BindView(R.id.re_et_nei38)
    Button btnFan38;
    @BindView(R.id.tr_39)
    TableRow tr39;
    @BindView(R.id.re_num_f39)
    TextView reNumF39;
    @BindView(R.id.re_btn_f39)
    Button btnDuan39;
    @BindView(R.id.re_et_nei39)
    Button btnFan39;
    @BindView(R.id.tr_40)
    TableRow tr40;
    @BindView(R.id.re_num_f40)
    TextView reNumF40;
    @BindView(R.id.re_btn_f40)
    Button btnDuan40;
    @BindView(R.id.re_et_nei40)
    Button btnFan40;

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
    private int n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20 = 0;//0是未翻转，1是已翻转
    private int n21, n22, n23, n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35, n36, n37, n38, n39, n40 = 0;
    private String TAG = "选择段位页面";

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
        for (int i = 1; i < 41; i++) {
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
            R.id.re_btn_f20, R.id.re_btn_f21, R.id.re_btn_f22, R.id.re_btn_f23,
            R.id.re_btn_f24, R.id.re_btn_f25, R.id.re_btn_f26, R.id.re_btn_f27,
            R.id.re_btn_f28, R.id.re_btn_f29, R.id.re_btn_f30, R.id.re_btn_f31, R.id.re_btn_f32, R.id.re_btn_f33,
            R.id.re_btn_f34, R.id.re_btn_f35, R.id.re_btn_f36, R.id.re_btn_f37, R.id.re_btn_f38, R.id.re_btn_f39,
            R.id.re_btn_f40, R.id.re_et_nei1, R.id.re_et_nei2, R.id.re_et_nei3,
            R.id.re_et_nei4, R.id.re_et_nei5, R.id.re_et_nei6, R.id.re_et_nei7,
            R.id.re_et_nei8, R.id.re_et_nei9, R.id.re_et_nei10, R.id.re_et_nei11, R.id.re_et_nei12, R.id.re_et_nei13,
            R.id.re_et_nei14, R.id.re_et_nei15, R.id.re_et_nei16, R.id.re_et_nei17, R.id.re_et_nei18, R.id.re_et_nei19,
            R.id.re_et_nei20, R.id.re_et_nei21, R.id.re_et_nei22, R.id.re_et_nei23,
            R.id.re_et_nei24, R.id.re_et_nei25, R.id.re_et_nei26, R.id.re_et_nei27,
            R.id.re_et_nei28, R.id.re_et_nei29, R.id.re_et_nei30, R.id.re_et_nei31, R.id.re_et_nei32, R.id.re_et_nei33,
            R.id.re_et_nei34, R.id.re_et_nei35, R.id.re_et_nei36, R.id.re_et_nei37, R.id.re_et_nei38, R.id.re_et_nei39,
            R.id.re_et_nei40})
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
            case R.id.re_btn_f21:
                hideInputKeyboard();
                duan = 21;
                initUI();
                reNumF21.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f22:
                hideInputKeyboard();
                duan = 22;
                initUI();
                reNumF22.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f23:
                hideInputKeyboard();
                duan = 23;
                initUI();
                reNumF23.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f24:
                hideInputKeyboard();
                duan = 24;
                initUI();
                reNumF24.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f25:
                hideInputKeyboard();
                duan = 25;
                initUI();
                reNumF25.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f26:
                hideInputKeyboard();
                duan = 26;
                initUI();
                reNumF26.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f27:
                hideInputKeyboard();
                duan = 27;
                initUI();
                reNumF27.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f28:
                hideInputKeyboard();
                duan = 28;
                initUI();
                reNumF28.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f29:
                hideInputKeyboard();
                duan = 29;
                initUI();
                reNumF29.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f30:
                hideInputKeyboard();
                duan = 30;
                initUI();
                reNumF30.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f31:
                hideInputKeyboard();
                duan = 31;
                initUI();
                reNumF31.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f32:
                hideInputKeyboard();
                duan = 32;
                initUI();
                reNumF32.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f33:
                hideInputKeyboard();
                duan = 33;
                initUI();
                reNumF33.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f34:
                hideInputKeyboard();
                duan = 34;
                initUI();
                reNumF34.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f35:
                hideInputKeyboard();
                duan = 35;
                initUI();
                reNumF35.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f36:
                hideInputKeyboard();
                duan = 36;
                initUI();
                reNumF36.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f37:
                hideInputKeyboard();
                duan = 37;
                initUI();
                reNumF37.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f38:
                hideInputKeyboard();
                duan = 38;
                initUI();
                reNumF38.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f39:
                hideInputKeyboard();
                duan = 39;
                initUI();
                reNumF39.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_btn_f40:
                hideInputKeyboard();
                duan = 40;
                initUI();
                reNumF40.setBackgroundResource(R.drawable.textview_border_green);
                break;
            case R.id.re_et_nei1:
                fanzhuan(1, n1);
                if (n1 == 1) {
                    n1 = 0;
                } else {
                    n1 = 1;
                }
                break;
            case R.id.re_et_nei2:
                fanzhuan(2, n2);
                if (n2 == 1) {
                    n2 = 0;
                } else {
                    n2 = 1;
                }
                break;
            case R.id.re_et_nei3:
                fanzhuan(3, n3);
                if (n3 == 1) {
                    n3 = 0;
                } else {
                    n3 = 1;
                }
                break;
            case R.id.re_et_nei4:
                fanzhuan(4, n4);
                if (n4 == 1) {
                    n4 = 0;
                } else {
                    n4 = 1;
                }
                break;
            case R.id.re_et_nei5:
                fanzhuan(5, n5);
                if (n5 == 1) {
                    n5 = 0;
                } else {
                    n5 = 1;
                }
                break;
            case R.id.re_et_nei6:
                fanzhuan(6, n6);
                if (n6 == 1) {
                    n6 = 0;
                } else {
                    n6 = 1;
                }
                break;
            case R.id.re_et_nei7:
                fanzhuan(7, n7);
                if (n7 == 1) {
                    n7 = 0;
                } else {
                    n7 = 1;
                }
                break;
            case R.id.re_et_nei8:
                fanzhuan(8, n8);
                if (n8 == 1) {
                    n8 = 0;
                } else {
                    n8 = 1;
                }
                break;
            case R.id.re_et_nei9:
                fanzhuan(9, n9);
                if (n9 == 1) {
                    n9 = 0;
                } else {
                    n9 = 1;
                }
                break;
            case R.id.re_et_nei10:
                fanzhuan(10, n10);
                if (n10 == 1) {
                    n10 = 0;
                } else {
                    n10 = 1;
                }
                break;
            case R.id.re_et_nei11:
                fanzhuan(11, n11);
                if (n11 == 1) {
                    n11 = 0;
                } else {
                    n11 = 1;
                }
                break;
            case R.id.re_et_nei12:
                fanzhuan(12, n12);
                if (n12 == 1) {
                    n12 = 0;
                } else {
                    n12 = 1;
                }
                break;
            case R.id.re_et_nei13:
                fanzhuan(13, n13);
                if (n13 == 1) {
                    n13 = 0;
                } else {
                    n13 = 1;
                }
                break;
            case R.id.re_et_nei14:
                fanzhuan(14, n14);
                if (n14 == 1) {
                    n14 = 0;
                } else {
                    n14 = 1;
                }
                break;
            case R.id.re_et_nei15:
                fanzhuan(15, n15);
                if (n15 == 1) {
                    n15 = 0;
                } else {
                    n15 = 1;
                }
                break;
            case R.id.re_et_nei16:
                fanzhuan(16, n16);
                if (n16 == 1) {
                    n16 = 0;
                } else {
                    n16 = 1;
                }
                break;
            case R.id.re_et_nei17:
                fanzhuan(17, n17);
                if (n17 == 1) {
                    n17 = 0;
                } else {
                    n17 = 1;
                }
                break;
            case R.id.re_et_nei18:
                fanzhuan(18, n18);
                if (n18 == 1) {
                    n18 = 0;
                } else {
                    n18 = 1;
                }
                break;
            case R.id.re_et_nei19:
                fanzhuan(19, n19);
                if (n19 == 1) {
                    n19 = 0;
                } else {
                    n19 = 1;
                }
                break;
            case R.id.re_et_nei20:
                fanzhuan(20, n20);
                if (n20 == 1) {
                    n20 = 0;
                } else {
                    n20 = 1;
                }
                break;
            case R.id.re_et_nei21:
                fanzhuan(21, n21);
                if (n21 == 1) {
                    n21 = 0;
                } else {
                    n21 = 1;
                }
                break;
            case R.id.re_et_nei22:
                fanzhuan(22, n22);
                if (n22 == 1) {
                    n22 = 0;
                } else {
                    n22 = 1;
                }
                break;
            case R.id.re_et_nei23:
                fanzhuan(23, n23);
                if (n23 == 1) {
                    n23 = 0;
                } else {
                    n23 = 1;
                }
                break;
            case R.id.re_et_nei24:
                fanzhuan(24, n24);
                if (n24 == 1) {
                    n24 = 0;
                } else {
                    n24 = 1;
                }
                break;
            case R.id.re_et_nei25:
                fanzhuan(25, n25);
                if (n25 == 1) {
                    n25 = 0;
                } else {
                    n25 = 1;
                }
                break;
            case R.id.re_et_nei26:
                fanzhuan(26, n26);
                if (n26 == 1) {
                    n26 = 0;
                } else {
                    n26 = 1;
                }
                break;
            case R.id.re_et_nei27:
                fanzhuan(27, n27);
                if (n27 == 1) {
                    n27 = 0;
                } else {
                    n27 = 1;
                }
                break;
            case R.id.re_et_nei28:
                fanzhuan(28, n28);
                if (n28 == 1) {
                    n28 = 0;
                } else {
                    n28 = 1;
                }
                break;
            case R.id.re_et_nei29:
                fanzhuan(29, n29);
                if (n29 == 1) {
                    n29 = 0;
                } else {
                    n29 = 1;
                }
                break;
            case R.id.re_et_nei30:
                fanzhuan(30, n30);
                if (n30 == 1) {
                    n30 = 0;
                } else {
                    n30 = 1;
                }
                break;
            case R.id.re_et_nei31:
                fanzhuan(31, n31);
                if (n31 == 1) {
                    n31 = 0;
                } else {
                    n31 = 1;
                }
                break;
            case R.id.re_et_nei32:
                fanzhuan(32, n32);
                if (n32 == 1) {
                    n32 = 0;
                } else {
                    n32 = 1;
                }
                break;
            case R.id.re_et_nei33:
                fanzhuan(33, n33);
                if (n33 == 1) {
                    n33 = 0;
                } else {
                    n33 = 1;
                }
                break;
            case R.id.re_et_nei34:
                fanzhuan(34, n34);
                if (n34 == 1) {
                    n34 = 0;
                } else {
                    n34 = 1;
                }
                break;
            case R.id.re_et_nei35:
                fanzhuan(35, n35);
                if (n35 == 1) {
                    n35 = 0;
                } else {
                    n35 = 1;
                }
                break;
            case R.id.re_et_nei36:
                fanzhuan(36, n36);
                if (n36 == 1) {
                    n36 = 0;
                } else {
                    n36 = 1;
                }
                break;
            case R.id.re_et_nei37:
                fanzhuan(37, n37);
                if (n37 == 1) {
                    n37 = 0;
                } else {
                    n37 = 1;
                }
                break;
            case R.id.re_et_nei38:
                fanzhuan(38, n38);
                if (n38 == 1) {
                    n38 = 0;
                } else {
                    n38 = 1;
                }
                break;
            case R.id.re_et_nei39:
                fanzhuan(39, n39);
                if (n39 == 1) {
                    n39 = 0;
                } else {
                    n39 = 1;
                }
                break;
            case R.id.re_et_nei40:
                fanzhuan(40, n40);
                if (n40 == 1) {
                    n40 = 0;
                } else {
                    n40 = 1;
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

    private List<String> list_delay = new ArrayList();

    private void getDelay() {
        list_delay.clear();
        String sql = "SELECT delay FROM denatorBaseinfo group by delay order by delay desc";//+" order by htbh "
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String delay = cursor.getString(0);
                list_delay.add(delay);
            }
            cursor.close();
        }
        Log.e(TAG, "list_delay: " + list_delay.toString());
    }

    private void fanzhuan(int duan, int fz) {
//        getDelay();


        Log.e("注册页面", "翻转: ");
        AlertDialog dialog = new AlertDialog.Builder(ChoseDuanActivity.this)
                .setTitle(getResources().getString(R.string.text_fzts))//设置对话框的标题//"成功起爆"
                .setMessage(getResources().getString(R.string.text_ssfz))//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                .setNegativeButton(getResources().getString(R.string.text_alert_cancel), (dialog12, which) -> dialog12.dismiss())
                .setPositiveButton(getResources().getString(R.string.text_verify), (dialog1, which) -> {

                    GreenDaoMaster master = new GreenDaoMaster();
                    DaoSession session = getDaoSession();
                    List<DenatorBaseinfo> list_up;//翻转前
                    List<DenatorBaseinfo> list2;//翻转后
                    List<DenatorBaseinfo> list5;//翻转后
                    String strSql;//除了序号最小的所有重复雷管
                    String strSql2;//序号最小的重复雷管
                    String strSql3;//所有不重复延时
                    String strSql4;//所有不重复孔号
                    String sql;//所有延时列表
                    Log.e(TAG, "fz: "+fz);
                    if(fz==0){//翻转
                        list_up = master.queryLeiguanDuan(duan, mRegion);
                        list2 = master.queryLeiguanDuan(duan, mRegion);
                        list5 = master.queryLeiguanDuanDesc(duan, mRegion);
                        strSql = "SELECT * FROM denatorBaseinfo a WHERE (a.delay) IN (SELECT delay FROM denatorBaseinfo where duan =" + duan + " and piece = "+mRegion+" GROUP BY delay HAVING COUNT(*) > 1) AND blastserial NOT IN (SELECT MIN(blastserial) FROM denatorBaseinfo where duan = "+duan+" and piece = "+mRegion+" GROUP BY delay HAVING COUNT(*)>1)";
                        strSql2 = "SELECT * FROM denatorBaseinfo a WHERE (a.delay) IN (SELECT delay FROM denatorBaseinfo where duan = " + duan + " and piece = "+mRegion+" GROUP BY delay HAVING COUNT(*) > 1) AND blastserial IN (SELECT MIN(blastserial) FROM denatorBaseinfo where duan = "+duan+" and piece = "+mRegion+" GROUP BY delay HAVING COUNT(*)>1)";
                        strSql3 = "SELECT  delay , duanNo FROM denatorBaseinfo where duan =" + duan +  " and piece = "+mRegion +" group by delay order by blastserial desc";//之前是id,但是插入雷管翻转延时不对,改为按序号排序
                        sql = "SELECT delay FROM denatorBaseinfo  where duan =" + duan +" and piece = "+mRegion +" order by blastserial";//+" order by htbh "
                        strSql4="SELECT  duanNo FROM denatorBaseinfo where duan = "+duan +" and piece = "+mRegion+" group by duanNo order by duanNo desc";//所有不重复孔号
                    }else {//复位
                        list_up = master.queryLeiguanDuan(duan, mRegion,"0");
                        list2 = master.queryLeiguanDuan(duan, mRegion,"0");
                        list5 = master.queryLeiguanDuan(duan, mRegion);
                        strSql = "SELECT * FROM denatorBaseinfo a WHERE (a.delay) IN (SELECT delay FROM denatorBaseinfo where duan =" + duan +" and piece = "+mRegion+ " and fanzhuan = 0 GROUP BY delay HAVING COUNT(*) > 1) AND blastserial NOT IN (SELECT MAX(blastserial) FROM denatorBaseinfo where duan = "+duan+" and piece = "+mRegion+" GROUP BY delay HAVING COUNT(*)>1)";
                        strSql2 = "SELECT * FROM denatorBaseinfo a WHERE (a.delay) IN (SELECT delay FROM denatorBaseinfo where duan = " + duan +" and piece = "+mRegion+ " and fanzhuan = 0 GROUP BY delay HAVING COUNT(*) > 1) AND blastserial IN (SELECT MAX(blastserial) FROM denatorBaseinfo where duan = "+duan+" and piece = "+mRegion+" GROUP BY delay HAVING COUNT(*)>1)";
                        strSql3 = "SELECT  delay , duanNo FROM denatorBaseinfo where duan =" + duan +" and piece = "+mRegion+ " and fanzhuan = 0 group by delay order by blastserial asc";
                        sql = "SELECT delay FROM denatorBaseinfo  where duan =" + duan +" and piece = "+mRegion +" order by blastserial" ;//+" order by htbh "
                        strSql4="SELECT  duanNo FROM denatorBaseinfo where duan = "+duan +" and piece = "+mRegion+ " and fanzhuan = 0  group by duanNo order by duanNo desc";//所有不重复孔号
                    }
//                    list_delay.clear();
//                    Cursor cursor = session.getDatabase().rawQuery(sql, null);
//                    if (cursor != null) {
//                        while (cursor.moveToNext()) {
//                            String delay = cursor.getString(0);
//                            list_delay.add(delay);
//                        }
//                        cursor.close();
//                    }


                    List<DenatorBaseinfo> list3 = getList(strSql);//除了序号最小的所有重复雷管

                    List<DenatorBaseinfo> list4 = getList(strSql2);//序号最小的重复雷管

                    List<Map<String, Object>> list_delay = new ArrayList<>();//所有不重复延时

                    Cursor cursor3 = session.getDatabase().rawQuery(strSql3, null);
                    if (cursor3 != null) {
                        while (cursor3.moveToNext()) {
                            int delay = cursor3.getInt(0);
                            String duanNo = cursor3.getString(1);
                            Map<String, Object> item = new HashMap<String, Object>();
                            item.put("delay", delay);
                            item.put("duanNo", duanNo);
                            list_delay.add(item);
                        }
                        cursor3.close();
                    }
                    List<Integer> list_duanNo = new ArrayList<>();//

                    Cursor cursor4 = session.getDatabase().rawQuery(strSql4, null);
                    if (cursor4 != null) {
                        while (cursor4.moveToNext()) {
                            int duanNo = cursor4.getInt(0);
                            list_duanNo.add(duanNo);
                        }
                        cursor4.close();
                    }
                    List<Integer> list_alldelay = new ArrayList<>();//
                    Cursor cursor5 = session.getDatabase().rawQuery(sql, null);
                    if (cursor5 != null) {
                        while (cursor5.moveToNext()) {
                            int delay = cursor5.getInt(0);
                            list_alldelay.add(delay);
                        }
                        cursor5.close();
                    }

                    Log.e(TAG, "list_alldelay: "+list_alldelay.toString() );
                    if(!isIncreasing(list_alldelay)&&! isDecreasing(list_alldelay)){
                        show_Toast(getResources().getString(R.string.text_xgys));
                        return;
                    }

                    Log.e(TAG, duan + "段 雷管list_up: " + list_up.toString());
                    Log.e(TAG, "翻转后的列表list2: " + list2.toString());
                    Log.e(TAG, "除了序号最小的所有重复雷管list3: " + list3.toString());
                    Log.e(TAG, "序号最小的重复雷管list4: " + list4.toString());
                    Log.e(TAG, "list_delay: " + list_delay.toString());
                    Log.e(TAG, "list_duanNo: " + list_duanNo.toString());
//                    Log.e(TAG, "判断: "+list.contains(list3.get(0)) );
//                    Log.e(TAG, "判断: "+list.contains(list2.get(0)) );
                    //遍历List并比较元素是否相等，判断是否包含"apple"元素

                    for (int i = 0; i < list_up.size(); i++) {
                        Log.e(TAG, "开始----------------: ");
                        DenatorBaseinfo lg = list_up.get(i);
                        Log.e(TAG, "第" + i + "发管: " + lg.toString());

                        boolean contains = false;//包含在 除了序号最小的所有重复雷管
                        for (DenatorBaseinfo db : list3) {
                            if (db.getId().equals(lg.getId())) {
                                contains = true;
                                break;
                            }
                        }
                        boolean contains2 = false;//包含在 序号最小的所有重复雷管
                        for (DenatorBaseinfo db : list4) {
                            if (db.getId().equals(lg.getId())) {
                                contains2 = true;
                                break;
                            }
                        }

                        Log.e(TAG, "是否 包含在 除了序号最小的所有重复雷管: " + contains);//list3是否包含当前雷管
                        Log.e(TAG, "最大序号的(i-1)=" + (i - 1));
                        if (contains) {//包含在 除了序号最小的所有重复雷管  list2
                            DenatorBaseinfo lg2;
                            if(i==0){
                                lg2 = master.querylg(list2.get(i).getShellBlastNo());
                            }else {
                                Log.e(TAG, "最大序号的list2.get(i-1)" + list2.get(i - 1).getShellBlastNo());
                                Log.e(TAG, "最大序号的list2.get(i-1)" + list2.get(i - 1).getDuanNo());
                                lg2 = master.querylg(list2.get(i - 1).getShellBlastNo());
                            }


                            Log.e(TAG, "最大序号的list2.get(i)" + list2.get(i).getShellBlastNo());
                            Log.e(TAG, "最大序号的list2.get(i)" + list2.get(i).getDuanNo());
                            Log.e(TAG, "最大序号的lg2.getDuanNo()" + lg2.getDuanNo());
                            lg.setDelay(lg2.getDelay());

                            if(i!=0&&list2.get(i - 1).getDuanNo()==list2.get(i).getDuanNo()){//同孔,获取前一发孔号
                                lg.setDuanNo(lg2.getDuanNo());
                                Log.e(TAG, "翻转最终孔号1:" + lg2.getDuanNo());
                            }else {//不同孔,在前一发的基础上加1
                                lg.setDuanNo(list_duanNo.get(0));
                                Log.e(TAG, "翻转最终孔号2:" + list_duanNo.get(0));
                                list_duanNo.remove(0);
                            }

                        } else {//不包含
                            Log.e(TAG, "是否 包含在 序号最小的所有重复雷管2: " + contains2);//list4是否包含当前雷管
                            Log.e(TAG, "翻转最终孔号3:" + list_duanNo.size());
                            Log.e(TAG, "翻转最终孔号3:" + list_duanNo.get(0));
                            Log.e(TAG, "所有不重复延时-list_delay.get(0): " + list_delay.get(0));
                            lg.setDuanNo(list_duanNo.get(0));
                            lg.setDelay((Integer) list_delay.get(0).get("delay"));

                            //清除list第一条数据
                            list_delay.remove(0);
                            list_duanNo.remove(0);
                        }

                        lg.setFanzhuan(fz + "");

                        int delay=0;
                        if(i>0){
                            int delau_up=master.querylg(list2.get(i-1).getShellBlastNo()).getDelay();//前一发雷管的延时
                            int delay_1=list2.get(i-1).getDelay();
                            int delay_2=list2.get(i).getDelay();
                            delay= delau_up+(delay_1-delay_2);
                            Log.e(TAG, "新方法 翻转后,前一发的管壳码: "+list2.get(i-1).getShellBlastNo() +"--delay="+list2.get(i-1).getDelay());
                            Log.e(TAG, "新方法 翻转后,前一发的延时delau_up: "+delau_up );
                            Log.e(TAG, "新方法 翻转前,前一发延时delay_1: "+list2.get(i-1).getShellBlastNo()+"-delay="+list2.get(i-1).getDelay() );
                            Log.e(TAG, "新方法 翻转前,当前延时delay_2: "+list2.get(i).getShellBlastNo()+"-delay="+list2.get(i).getDelay()  );
                        }else {
                            delay= list_up.get(list_up.size()-1).getDelay();

                        }
                        Log.e(TAG, "新方法获取的第"+i+"发"+lg.getShellBlastNo()+"--delay: "+delay );
                        lg.setDelay(delay);
                        getDaoSession().getDenatorBaseinfoDao().update(lg);

                        Log.e(TAG, "结束----------------: ");
                    }
                    show_Toast(getResources().getString(R.string.text_fzwc));
//                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                    setBtnColor(duan);

                }).create();

        dialog.show();
    }

    private List<DenatorBaseinfo> getList(String sql) {
        List<DenatorBaseinfo> list_db = new ArrayList<>();
        DaoSession session = getDaoSession();

        Cursor cursor = session.getDatabase().rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                int blastserial = cursor.getInt(1);
                String sithole = cursor.getString(2);
                String shellBlastNo = cursor.getString(3);//管壳号
                String denatorId = cursor.getString(4);
                String delay = cursor.getString(5);
                String statusCode = cursor.getString(6);
                String statusName = cursor.getString(7);
                String errorName = cursor.getString(8);
                String errorCode = cursor.getString(9);
                String authorization = cursor.getString(10);
                String remark = cursor.getString(11);
                String regdate = cursor.getString(12);
                String wire = cursor.getString(13);
                String name = cursor.getString(14);
                String denatorIdSup = cursor.getString(15);
                String zhu_yscs = cursor.getString(16);
                String cong_yscs = cursor.getString(17);
                String piece = cursor.getString(18);
                int duan = cursor.getInt(19);
                int duanNo = cursor.getInt(20);

                DenatorBaseinfo lg = new DenatorBaseinfo();
                lg.setId(Long.valueOf(id));
                lg.setBlastserial(blastserial);
                lg.setSithole(sithole);
                lg.setShellBlastNo(shellBlastNo);
                lg.setDenatorId(denatorId);
                lg.setDelay(Integer.parseInt(delay));
                lg.setStatusCode(statusCode);
                lg.setStatusName(statusName);
                lg.setErrorName(errorName);
                lg.setErrorCode(errorCode);
                lg.setAuthorization(authorization);
                lg.setRemark(remark);
                lg.setRegdate(regdate);
                lg.setWire(wire);
                lg.setName(name);
                lg.setDenatorIdSup(denatorIdSup);
                lg.setZhu_yscs(zhu_yscs);
                lg.setCong_yscs(cong_yscs);
                lg.setPiece(piece);
                lg.setDuan(duan);
                lg.setDuanNo(duanNo);
                list_db.add(lg);
            }
            cursor.close();
        }
        return list_db;
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
                MmkvUtils.savecode(mRegion+"n1", n1);
                break;
            case 2:
                if (n2 == 1) {
                    btnFan2.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan2.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n2", n2);
                break;
            case 3:
                if (n3 == 1) {
                    btnFan3.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan3.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n3", n3);
                break;
            case 4:
                if (n4 == 1) {
                    btnFan4.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan4.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n4", n4);
                break;
            case 5:
                if (n5 == 1) {
                    btnFan5.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan5.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n5", n5);
                break;
            case 6:
                if (n6 == 1) {
                    btnFan6.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan6.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n6", n6);
                break;
            case 7:
                if (n7 == 1) {
                    btnFan7.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan7.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n7", n7);
                break;
            case 8:
                if (n8 == 1) {
                    btnFan8.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan8.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n8", n8);
                break;
            case 9:
                if (n9 == 1) {
                    btnFan9.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan9.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n9", n9);
                break;
            case 10:
                if (n10 == 1) {
                    btnFan10.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan10.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n10", n10);
                break;
            case 11:
                if (n11 == 1) {
                    btnFan11.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan11.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n11", n11);
                break;
            case 12:
                if (n12 == 1) {
                    btnFan12.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan12.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n12", n12);
                break;
            case 13:
                if (n13 == 1) {
                    btnFan13.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan13.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n13", n13);
                break;
            case 14:
                if (n14 == 1) {
                    btnFan14.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan14.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n14", n14);
                break;
            case 15:
                if (n15 == 1) {
                    btnFan15.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan15.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n15", n15);
                break;
            case 16:
                if (n16 == 1) {
                    btnFan16.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan16.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n16", n16);
                break;
            case 17:
                if (n17 == 1) {
                    btnFan17.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan17.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n17", n17);
                break;
            case 18:
                if (n18 == 1) {
                    btnFan18.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan18.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n18", n18);
                break;
            case 19:
                if (n19 == 1) {
                    btnFan19.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan19.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n19", n19);
                break;
            case 20:
                if (n20 == 1) {
                    btnFan20.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan20.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n20", n20);
                break;
            case 21:
                if (n21 == 1) {
                    btnFan21.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan21.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n21", n21);
                break;
            case 22:
                if (n22 == 1) {
                    btnFan22.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan22.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n22", n22);
                break;
            case 23:
                if (n23 == 1) {
                    btnFan23.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan23.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n23", n23);
                break;
            case 24:
                if (n24 == 1) {
                    btnFan24.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan24.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n24", n24);
                break;
            case 25:
                if (n25 == 1) {
                    btnFan25.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan25.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n25", n25);
                break;
            case 26:
                if (n26 == 1) {
                    btnFan26.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan26.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n26", n26);
                break;
            case 27:
                if (n27 == 1) {
                    btnFan27.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan27.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n27", n27);
                break;
            case 28:
                if (n28 == 1) {
                    btnFan28.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan28.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n28", n28);
                break;
            case 29:
                if (n29 == 1) {
                    btnFan29.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan29.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n29", n29);
                break;
            case 30:
                if (n30 == 1) {
                    btnFan30.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan30.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n30", n30);
                break;
            case 31:
                if (n31 == 1) {
                    btnFan31.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan31.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n31", n31);
                break;
            case 32:
                if (n32 == 1) {
                    btnFan32.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan32.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n32", n32);
                break;
            case 33:
                if (n33 == 1) {
                    btnFan33.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan33.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n33", n33);
                break;
            case 34:
                if (n34 == 1) {
                    btnFan34.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan34.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n34", n34);
                break;
            case 35:
                if (n35 == 1) {
                    btnFan35.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan35.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n35", n35);
                break;
            case 36:
                if (n36 == 1) {
                    btnFan36.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan36.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n36", n36);
                break;
            case 37:
                if (n37 == 1) {
                    btnFan37.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan37.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n37", n37);
                break;
            case 38:
                if (n38 == 1) {
                    btnFan38.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan38.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n38", n38);
                break;
            case 39:
                if (n39 == 1) {
                    btnFan39.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan39.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n39", n39);
                break;
            case 40:
                if (n40 == 1) {
                    btnFan40.setBackgroundResource(R.drawable.bt_mainpage_style_green);
                } else {
                    btnFan40.setBackgroundResource(R.drawable.bt_mainpage_style);
                }
                MmkvUtils.savecode(mRegion+"n40", n40);
                break;
        }
    }

    private void setFan() {
        n1 = (int) MmkvUtils.getcode(mRegion+"n1", 0);
        n2 = (int) MmkvUtils.getcode(mRegion+"n2", 0);
        n3 = (int) MmkvUtils.getcode(mRegion+"n3", 0);
        n4 = (int) MmkvUtils.getcode(mRegion+"n4", 0);
        n5 = (int) MmkvUtils.getcode(mRegion+"n5", 0);
        n6 = (int) MmkvUtils.getcode(mRegion+"n6", 0);
        n7 = (int) MmkvUtils.getcode(mRegion+"n7", 0);
        n8 = (int) MmkvUtils.getcode(mRegion+"n8", 0);
        n9 = (int) MmkvUtils.getcode(mRegion+"n9", 0);
        n10 = (int) MmkvUtils.getcode(mRegion+"n10", 0);
        n11 = (int) MmkvUtils.getcode(mRegion+"n11", 0);
        n12 = (int) MmkvUtils.getcode(mRegion+"n12", 0);
        n13 = (int) MmkvUtils.getcode(mRegion+"n13", 0);
        n14 = (int) MmkvUtils.getcode(mRegion+"n14", 0);
        n15 = (int) MmkvUtils.getcode(mRegion+"n15", 0);
        n16 = (int) MmkvUtils.getcode(mRegion+"n16", 0);
        n17 = (int) MmkvUtils.getcode(mRegion+"n17", 0);
        n18 = (int) MmkvUtils.getcode(mRegion+"n18", 0);
        n19 = (int) MmkvUtils.getcode(mRegion+"n19", 0);
        n20 = (int) MmkvUtils.getcode(mRegion+"n20", 0);
        n21 = (int) MmkvUtils.getcode(mRegion+"n21", 0);
        n22 = (int) MmkvUtils.getcode(mRegion+"n22", 0);
        n23 = (int) MmkvUtils.getcode(mRegion+"n23", 0);
        n24 = (int) MmkvUtils.getcode(mRegion+"n24", 0);
        n25 = (int) MmkvUtils.getcode(mRegion+"n25", 0);
        n26 = (int) MmkvUtils.getcode(mRegion+"n26", 0);
        n27 = (int) MmkvUtils.getcode(mRegion+"n27", 0);
        n28 = (int) MmkvUtils.getcode(mRegion+"n28", 0);
        n29 = (int) MmkvUtils.getcode(mRegion+"n29", 0);
        n30 = (int) MmkvUtils.getcode(mRegion+"n30", 0);
        n31 = (int) MmkvUtils.getcode(mRegion+"n31", 0);
        n32 = (int) MmkvUtils.getcode(mRegion+"n32", 0);
        n33 = (int) MmkvUtils.getcode(mRegion+"n33", 0);
        n34 = (int) MmkvUtils.getcode(mRegion+"n34", 0);
        n35 = (int) MmkvUtils.getcode(mRegion+"n35", 0);
        n36 = (int) MmkvUtils.getcode(mRegion+"n36", 0);
        n37 = (int) MmkvUtils.getcode(mRegion+"n37", 0);
        n38 = (int) MmkvUtils.getcode(mRegion+"n38", 0);
        n39 = (int) MmkvUtils.getcode(mRegion+"n39", 0);
        n40 = (int) MmkvUtils.getcode(mRegion+"n40", 0);
        for (int i = 1; i < 41; i++) {
            setBtnColor(i);
        }
    }

    public void initUI() {
        Log.e(TAG, "duan: " + duan);
        MmkvUtils.savecode("duan", duan);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("data_return", duan + "");
        intent.putExtras(bundle);
        setResult(0, intent);
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
        reNumF21.setBackgroundResource(R.drawable.translucent);
        reNumF22.setBackgroundResource(R.drawable.translucent);
        reNumF23.setBackgroundResource(R.drawable.translucent);
        reNumF24.setBackgroundResource(R.drawable.translucent);
        reNumF25.setBackgroundResource(R.drawable.translucent);
        reNumF26.setBackgroundResource(R.drawable.translucent);
        reNumF27.setBackgroundResource(R.drawable.translucent);
        reNumF28.setBackgroundResource(R.drawable.translucent);
        reNumF29.setBackgroundResource(R.drawable.translucent);
        reNumF30.setBackgroundResource(R.drawable.translucent);
        reNumF31.setBackgroundResource(R.drawable.translucent);
        reNumF32.setBackgroundResource(R.drawable.translucent);
        reNumF33.setBackgroundResource(R.drawable.translucent);
        reNumF34.setBackgroundResource(R.drawable.translucent);
        reNumF35.setBackgroundResource(R.drawable.translucent);
        reNumF36.setBackgroundResource(R.drawable.translucent);
        reNumF37.setBackgroundResource(R.drawable.translucent);
        reNumF38.setBackgroundResource(R.drawable.translucent);
        reNumF39.setBackgroundResource(R.drawable.translucent);
        reNumF40.setBackgroundResource(R.drawable.translucent);
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
    private int getDuanNo(int duan, String piece) {
        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where duan =? and piece = ? ", new String[]{duan + "", piece});
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
        if(totalNum==0){
            setBtnColor(a);
        }
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
            case 21:
                reNumF21.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n21", 0);
                }
                break;
            case 22:
                reNumF22.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n22", 0);
                }
                break;
            case 23:
                reNumF23.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n23", 0);
                }
                break;
            case 24:
                reNumF24.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n24", 0);
                }
                break;
            case 25:
                reNumF25.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n25", 0);
                }
                break;
            case 26:
                reNumF26.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n26", 0);
                }
                break;
            case 27:
                reNumF27.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n27", 0);
                }
                break;
            case 28:
                reNumF28.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n28", 0);
                }
                break;
            case 29:
                reNumF29.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n29", 0);
                }
                break;
            case 30:
                reNumF30.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n30", 0);
                }
                break;
            case 31:
                reNumF31.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n31", 0);
                }
                break;
            case 32:
                reNumF32.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n32", 0);
                }
                break;
            case 33:
                reNumF33.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n33", 0);
                }
                break;
            case 34:
                reNumF34.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n34", 0);
                }
                break;
            case 35:
                reNumF35.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n35", 0);
                }
                break;
            case 36:
                reNumF36.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n36", 0);
                }
                break;
            case 37:
                reNumF37.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n37", 0);
                }
                break;
            case 38:
                reNumF38.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n38", 0);
                }
                break;
            case 39:
                reNumF39.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n39", 0);
                }
                break;
            case 40:
                reNumF40.setText(totalNum + "");
                if (totalNum == 0) {
                    MmkvUtils.savecode("n40", 0);
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

    public void show_Toast(String text) {
//		Utils.showToast(this,text,3000);
        SmartToast.classic()
                .config()
                .messageColor(Color.RED)
                .backgroundColorResource(R.color.toast_bg_color)
                .messageSize(30f)//设置文本大小，单位sp，默认14sp
                .messageBold(true)//设置文本为粗体，默认false
                .apply()
                .show(text);
    }

    /**
     * 判断是否递增
     * */
    public static boolean isIncreasing(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) < list.get(i - 1)) {
                Log.e("递增", "false: " );
                return false;
            }
        }
        Log.e("递增", "true: " );
        return true;
    }
    /**
     * 判断是否递减
     * */
    public static boolean isDecreasing(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            Log.e("递减", "list.get(i): " +list.get(i));
            Log.e("递减", "list.get(i - 1): " +list.get(i - 1));
            Log.e("递减", "list.get(i) >= list.get(i - 1): " +(list.get(i) >= list.get(i - 1)));
            if (list.get(i) > list.get(i - 1)) {
                Log.e("递减", "false: " );
                return false;
            }
        }
        Log.e("递减", "true: " );
        return true;
    }


}