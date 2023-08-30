package android_serialport_api.xingbang.firingdevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.ChaKan_SQAdapter;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShouQuanActivity extends AppCompatActivity {
    @BindView(R.id.rv_chakan)
    RecyclerView chakan;
    // 1、page变量，标记每次请求的页面number
    private int page = 1;
    private SmartRefreshLayout refreshLayout;
    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private ChaKan_SQAdapter<DetonatorTypeNew> mAdapter;
    private List<DetonatorTypeNew> mListData = new ArrayList<>();
    private Handler mHandler_UI = new Handler();     // UI处理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shou_quan);
        ButterKnife.bind(this);

        RefreshLayout mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }
        });

        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ChaKan_SQAdapter<>(this, 0);
        chakan.setLayoutManager(linearLayoutManager);
        chakan.setAdapter(mAdapter);

        mHandler_UI = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1001:
                    mListData = new GreenDaoMaster().queryDetonatorShouQuan();
                    mAdapter.setListData(mListData, 0);
                    mAdapter.notifyDataSetChanged();

                    break;
                default:
                    break;
            }
            return false;
        });
        // 区域 更新视图
        mHandler_UI.sendMessage(mHandler_UI.obtainMessage(1001));
    }



}