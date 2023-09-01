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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.ChaKan_SQAdapter;
import android_serialport_api.xingbang.custom.DataAdapter;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.MlistView;
import android_serialport_api.xingbang.custom.ShouQuanData;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.utils.Utils;
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
    @BindView(R.id.lay_bottom)
    LinearLayout layBottom;//底部布局
    // 1、page变量，标记每次请求的页面number
    private int page = 1;
    private SmartRefreshLayout refreshLayout;
    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private ChaKan_SQAdapter<DetonatorTypeNew> mAdapter;
    private DataAdapter mAdapter2;
    private List<DetonatorTypeNew> mListData = new ArrayList<>();
    private List<ShouQuanData> mList = new ArrayList<>();
    private List<DetonatorTypeNew> mListData_ALL = new ArrayList<>();
    private Handler mHandler_UI = new Handler();     // UI处理
    TextView totalbar_title;
    private boolean paixu_flag = true;//排序标志
    private static final int STATE_DEFAULT = 0;//默认状态
    private static final int STATE_EDIT = 1;//编辑状态
    private int mEditMode = STATE_DEFAULT;
    private boolean editorStatus = false;//是否为编辑状态
    private int index = 0;//当前选中的item数


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
//            startActivity(new Intent(this, SouSuoSQActivity.class));
            updateEditState();
        });
        iv_back.setOnClickListener(v -> finish());

        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));

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
        mAdapter2 = new DataAdapter(R.layout.item_shouquan, mList);//绑定视图和数据
        chakan.setLayoutManager(linearLayoutManager);
//        chakan.setAdapter(mAdapter);
        chakan.setAdapter(mAdapter2);

        mHandler_UI = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1:
//                    mListData = new GreenDaoMaster().queryDetonatorShouQuan();
                    mListData = new GreenDaoMaster().queryDetonatorShouQuan2(page);
                    mListData_ALL = mListData;
                    for (DetonatorTypeNew item : mListData) {
                        ShouQuanData shouQuanData = new ShouQuanData();
                        shouQuanData.setId(item.getId());
                        shouQuanData.setShellBlastNo(item.getShellBlastNo());
                        shouQuanData.setDetonatorId(item.getDetonatorId());
                        shouQuanData.setDetonatorIdSup(item.getDetonatorIdSup());
                        shouQuanData.setCong_yscs(item.getCong_yscs());
                        shouQuanData.setZhu_yscs(item.getZhu_yscs());
                        shouQuanData.setQibao(item.getQibao());
                        shouQuanData.setTime(item.getTime());
                        mList.add(shouQuanData);
                    }
                    mRefreshLayout.finishRefresh(true);
//                    mAdapter.setListData(mListData, 0);
//                    mAdapter.notifyDataSetChanged();
                    mAdapter2.notifyDataSetChanged();

                    break;
                case 2:
                    mListData = new GreenDaoMaster().queryDetonatorShouQuan2(page);
                    mListData_ALL.addAll(mListData);
                    Log.e("授权页面", "mListData: " + mListData.toString());
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
        List<DetonatorTypeNew> list = master.queryDetonatorShouQuan("未使用");
        List<DetonatorTypeNew> list2 = master.queryDetonatorShouQuan("已起爆");
        tv_ysy.setText("已起爆:" + list2.size());
        tv_wsy.setText("未使用:" + list.size());

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

    //改变编辑状态
    private void updateEditState() {
        mEditMode = mEditMode == STATE_DEFAULT ? STATE_EDIT : STATE_DEFAULT;
        if (mEditMode == STATE_EDIT) {
//            tvEdit.setText("取消");
            layBottom.setVisibility(View.VISIBLE);
            editorStatus = true;
        } else {
//            tvEdit.setText("编辑");
            layBottom.setVisibility(View.GONE);
            editorStatus = false;

//            setAllItemUnchecked();//取消全选
        }
        mAdapter2.setEditMode(mEditMode);

        mAdapter2.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (editorStatus) {//编辑状态
                    ShouQuanData dataBean = mList.get(position);
                    boolean isSelect = dataBean.isSelect();
                    if (!isSelect) {
                        index++;
                        dataBean.setSelect(true);

                    } else {
                        dataBean.setSelect(false);
                        index--;
                    }
//                    if (index == 0) {
//                        tvDelete.setText("删除");
//                    } else {
//                        tvDelete.setText("删除(" + index + ")");
//                    }

                    mAdapter2.notifyDataSetChanged();
                }
            }
        });
    }


    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sq, menu);
        return true;
    }

    /**
     * 打开菜单
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 点击item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_1:
                startActivity(new Intent(this, SouSuoSQActivity.class));

                return true;
            case R.id.item_2:
                updateEditState();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}