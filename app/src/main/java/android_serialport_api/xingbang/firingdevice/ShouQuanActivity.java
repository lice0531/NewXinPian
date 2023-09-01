package android_serialport_api.xingbang.firingdevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.ChaKan_SQAdapter;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.MlistView;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShouQuanActivity extends AppCompatActivity {
    @BindView(R.id.rv_chakan)
    RecyclerView chakan;
    @BindView(R.id.tv_sq_ysy)
    TextView tv_ysy;
    @BindView(R.id.tv_sq_wsy)
    TextView tv_wsy;
    @BindView(R.id.btn_ss_px)
    Button btn_ss_px;
    // 1、page变量，标记每次请求的页面number
    private int page = 1;
    private SmartRefreshLayout refreshLayout;
    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private ChaKan_SQAdapter<DetonatorTypeNew> mAdapter;
    private List<DetonatorTypeNew> mListData = new ArrayList<>();
    private List<DetonatorTypeNew> mListData_ALL = new ArrayList<>();
    private Handler mHandler_UI = new Handler();     // UI处理
    TextView totalbar_title;
    private boolean paixu_flag = true;//排序标志
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shou_quan);
        ButterKnife.bind(this);

        totalbar_title = findViewById(R.id.title_text);
        totalbar_title.setText("授权列表");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setOnClickListener(v -> {
            startActivity(new Intent(this, SouSuoSQActivity.class));
        });
        iv_back.setOnClickListener(v -> finish());
        RefreshLayout mRefreshLayout = findViewById(R.id.refreshLayout);
        //刷新监听
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mHandler_UI.sendMessage(mHandler_UI.obtainMessage(1));
            }
        });
        //加载监听
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mHandler_UI.sendMessage(mHandler_UI.obtainMessage(2));
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
                case 1:
//                    mListData = new GreenDaoMaster().queryDetonatorShouQuan();
                    mListData = new GreenDaoMaster().queryDetonatorShouQuan2(page);
                    mListData_ALL=mListData;
                    mRefreshLayout.finishRefresh(true);
                    mAdapter.setListData(mListData, 0);
                    mAdapter.notifyDataSetChanged();

                    break;
                case 2:
                    mListData = new GreenDaoMaster().queryDetonatorShouQuan2(page);
                    mListData_ALL.addAll(mListData);
                    Log.e("授权页面", "mListData: "+mListData.toString() );
                    mRefreshLayout.finishLoadMore(true);
                    mAdapter.addMoreValue(mListData);
                    mAdapter.notifyDataSetChanged();
                    page++;
                    break;
                case 3:
//                    mListData = new GreenDaoMaster().queryDetonatorShouQuan();
//                    mListData = new GreenDaoMaster().queryDetonatorShouQuan2(page);
                    Collections.sort(mListData_ALL);
                    mAdapter.setListData(mListData_ALL, 0);
                    mAdapter.notifyDataSetChanged();

                    break;
                default:
                    break;
            }
            return false;
        });
        // 区域 更新视图
        mHandler_UI.sendMessage(mHandler_UI.obtainMessage(1));

        GreenDaoMaster master = new GreenDaoMaster();
        List<DetonatorTypeNew> list=master.queryDetonatorShouQuan("未使用");
        List<DetonatorTypeNew> list2=master.queryDetonatorShouQuan("已起爆");
        tv_ysy.setText("已起爆:"+list2.size());
        tv_wsy.setText("未使用:"+list.size());
    }


    @OnClick({R.id.btn_ss_px})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ss_px:
//                if (paixu_flag) {
                    mHandler_UI.sendMessage(mHandler_UI.obtainMessage(3));
//                    paixu_flag = false;
//                } else {
//                    mHandler_UI.sendMessage(mHandler_UI.obtainMessage(1));
//                    paixu_flag = true;
//                }
                break;
        }
    }
}