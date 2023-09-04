package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.BaseActivity;
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

public class ShouQuanActivity extends BaseActivity {
    @BindView(R.id.sq_appBar)
    AppBarLayout sq_appBar;
    @BindView(R.id.tv_sq_ysy)
    TextView tv_ysy;
    @BindView(R.id.tv_sq_wsy)
    TextView tv_wsy;
    @BindView(R.id.tv_lg_uid)
    TextView tv_lg_uid;
    @BindView(R.id.tv_lg_yxq)
    TextView tv_lg_yxq;
    @BindView(R.id.tv_lg_qb)
    TextView tv_lg_qb;
    @BindView(R.id.ll_bar)
    LinearLayout ll_bar;
    @BindView(R.id.rv_chakan)
    RecyclerView chakan;
    @BindView(R.id.tv_check_all)
    TextView tv_check_all;
    @BindView(R.id.tv_input)
    TextView tv_input;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.lay_bottom)
    LinearLayout layBottom;//底部布局
    @BindView(R.id.btn_ss_px)
    Button btn_ss_px;
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
    private String mRegion;     // 区域


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shou_quan);
        ButterKnife.bind(this);
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
//         获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");

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
//        mAdapter = new ChaKan_SQAdapter<>(this, 0);
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
                    Log.e("加载", "mListData.size(): "+mListData.size() );
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
                        if (!mList.contains(shouQuanData)) {
                            mList.add(shouQuanData);
                        }
                    }
                    mAdapter2.setNewData(mList);
                    mAdapter2.notifyDataSetChanged();
//                    mAdapter.setListData(mListData, 0);
//                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.finishRefresh(true);
                    GreenDaoMaster master = new GreenDaoMaster();
                    List<DetonatorTypeNew> list = master.queryDetonatorShouQuan("未使用");
                    List<DetonatorTypeNew> list2 = master.queryDetonatorShouQuan("已起爆");
                    tv_ysy.setText("已起爆:" + list2.size());
                    tv_wsy.setText("未使用:" + list.size());
                    break;
                case 2:
                    Log.e("刷新", "mListData.size(): "+mListData.size() );
                    mListData = new GreenDaoMaster().queryDetonatorShouQuan2(page);
//                    mListData_ALL.addAll(mListData);

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
                        if (!mList.contains(shouQuanData)) {
                            mList.add(shouQuanData);
                        }
                    }
                    Log.e("授权页面", "mList.size(): " + mList.size());
                    mRefreshLayout.finishLoadMore(true);
//                    mAdapter.addMoreValue(mListData);
//                    mAdapter.notifyDataSetChanged();
                    mAdapter2.setNewData(mList);
                    mAdapter2.notifyDataSetChanged();
                    page++;
                    break;
                case 3:
//                    mListData = new GreenDaoMaster().queryDetonatorShouQuan();
//                    mListData = new GreenDaoMaster().queryDetonatorShouQuan2(page);
                    Collections.sort(mList);
                    mAdapter2.setNewData(mList);
                    mAdapter2.notifyDataSetChanged();

                    break;
                case 4:
                    show_Toast("选中雷管注册到区域" + mRegion + "中");
                    break;
                case 5:
                    show_Toast("导入成功,有数据注册重复");
                    break;
                case 6:
                    show_Toast("数据不完整");
                    break;
                default:
                    break;
            }
            return false;
        });
        // 更新视图
        mHandler_UI.sendMessage(mHandler_UI.obtainMessage(1));



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


    @OnClick({R.id.tv_check_all, R.id.tv_input, R.id.tv_delete, R.id.btn_ss_px})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_check_all:
                setAllItemChecked();
                break;
            case R.id.tv_input:
                inputLeiGuan();
                break;
            case R.id.tv_delete:
                deleteCheckItem();
                break;
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

    //删除选中的item
    private void deleteCheckItem() {
        if (mAdapter2 == null) return;
        GreenDaoMaster master = new GreenDaoMaster();

        for (int i = mList.size() - 1; i >= 0; i--) {

            if (mList.get(i).isSelect()) {
                master.deleteDetonatorShouQuan(mList.get(i).getShellBlastNo());
                mList.remove(i);
            }
        }
        show_Toast("删除成功");
        //删除选中的item之后判断是否还有数据，没有则退出编辑模式
        if (mList.size() != 0) {
            index = 0;//删除之后置为0
//            tvDelete.setText("删除");
        } else {
//            tvEdit.setText("编辑");
            layBottom.setVisibility(View.GONE);
            editorStatus = false;
            mAdapter2.setEditMode(mEditMode);
            //没有数据自然也不存在编辑了
//            tvEdit.setVisibility(View.GONE);
//            rvNormalShow.setVisibility(View.VISIBLE);
//            //启用下拉
//            refresh.setEnableRefresh(true);
//
//            //下拉刷新
//            refresh.setOnRefreshListener(refreshLayout -> {
//                //重新装填数据
//                initList();
//                index = 0;
//                mEditMode = STATE_DEFAULT;//恢复默认状态
//                editorStatus = false;//恢复默认状态
//                tvDelete.setText("删除");
//                tvEdit.setVisibility(View.VISIBLE);//显示编辑
//            });
        }
        updateEditState();
        mHandler_UI.sendMessage(mHandler_UI.obtainMessage(1));
        mAdapter2.notifyDataSetChanged();
    }

    //注册选中的item
    private void inputLeiGuan() {

        for (int i = mList.size() - 1; i >= 0; i--) {

            if (mList.get(i).isSelect()) {
                registerDetonator(mList.get(i));
            }
        }
        updateEditState();
        show_Toast("注册成功");
        mHandler_UI.sendMessage(mHandler_UI.obtainMessage(1));
        mAdapter2.notifyDataSetChanged();
    }


    /**
     * 注册雷管
     */
    private void registerDetonator(ShouQuanData db) {
        boolean chongfu = false;
        int maxNo = getMaxNumberNo();
        Log.e("接收注册", "shellNo: " + db.getShellBlastNo());
        Log.e("接收注册", "区域mRegion: " + mRegion);
        if (db.getShellBlastNo().length() < 13) {

            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(6));
            return;
        }
        if (checkRepeatDenatorId(db.getShellBlastNo())) {//检查重复数据
            chongfu = true;
            return;
        }

        String duan = db.getCong_yscs();
        String version =db.getDetonatorIdSup();
        String yscs=db.getZhu_yscs();
        String delay="";
        switch (duan){
            case "1":
                delay="0";
                break;
            case "2":
                delay="25";
                break;
            case "3":
                delay="50";
                break;
            case "4":
                delay="75";
                break;
            case "5":
                delay="100";
                break;
        }
        int duanNUM = new GreenDaoMaster().getDuanNo(mRegion, duan);
        maxNo++;
        DenatorBaseinfo denator = new DenatorBaseinfo();
        denator.setBlastserial(maxNo);
        denator.setSithole(maxNo + "");
        denator.setDenatorId(db.getDetonatorId());
        denator.setShellBlastNo(db.getShellBlastNo());
        denator.setZhu_yscs(yscs);
        denator.setDelay(Integer.parseInt(delay));
        denator.setRegdate(Utils.getDateFormatLong(new Date()));
        denator.setStatusCode("02");
        denator.setStatusName("已注册");
        denator.setErrorCode("FF");
        denator.setErrorName("");
        denator.setWire("");
        denator.setPiece(mRegion);
        denator.setDuanNo(duan + "-" + (duanNUM + 1));//段序号
        denator.setDuan(duan);
        denator.setAuthorization(version);//导入默认是02版
        getDaoSession().getDenatorBaseinfoDao().insert(denator);




    }

    /**
     * 得到最大序号
     */
    private int getMaxNumberNo() {
        return LitePal.max(DenatorBaseinfo.class, "blastserial", int.class);
    }

    /**
     * 检查重复的数据
     *
     * @param denatorId
     */
    public boolean checkRepeatDenatorId(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> denatorBaseinfo = master.checkRepeatdenatorId(denatorId);
        if (denatorBaseinfo.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //全部选中
    private void setAllItemChecked() {
        if (mAdapter2 == null) return;
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelect(true);
        }
        mAdapter2.setNewData(mList);
        mAdapter2.notifyDataSetChanged();
        index = mList.size();
//        tvDelete.setText("删除(" + index + ")");
    }
}