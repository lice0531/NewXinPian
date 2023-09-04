package android_serialport_api.xingbang.firingdevice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.ChaKan_SQAdapter;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SouSuoSQActivity extends AppCompatActivity {


    @BindView(R.id.ss_btn_ss)
    Button ss_btn_ss;
    @BindView(R.id.tv_lg_uid)
    TextView tv_lg_uid;
    @BindView(R.id.tv_lg_yxq)
    TextView tv_lg_yxq;
    @BindView(R.id.tv_lg_qb)
    TextView tv_lg_qb;
    @BindView(R.id.re_ss)
    RecyclerView re_ss;
    @BindView(R.id.edit_gkm)
    EditText edit_gkm;

    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private ChaKan_SQAdapter<DetonatorTypeNew> mAdapter;
    private List<DetonatorTypeNew> mListData = new ArrayList<>();
    private Handler mHandler_UI = new Handler();     // UI处理
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sou_suo_sqactivity);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ChaKan_SQAdapter<>(this, 0);
        re_ss.setLayoutManager(linearLayoutManager);
        re_ss.setAdapter(mAdapter);

        mHandler_UI = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1:
                    mListData = new GreenDaoMaster().queryDetonatorShouQuanForGkm(msg.obj.toString());
                    Log.e("查询", "mListData: "+mListData.toString() );
                    mAdapter.setListData(mListData, 0);
                    mAdapter.notifyDataSetChanged();
                    hideInputKeyboard();//隐藏键盘,取消焦点
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    @OnClick({R.id.ss_btn_ss})

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ss_btn_ss:
                String gkm = edit_gkm.getText().toString();
                Message msg = new Message();
                msg.what=1;
                msg.obj=gkm;
                mHandler_UI.sendMessage(msg);
                break;
        }
    }

    //隐藏键盘
    public void hideInputKeyboard() {

        edit_gkm.clearFocus();//取消焦点

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}