package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.ChaKan_SQAdapter;
import android_serialport_api.xingbang.custom.DataAdapter;
import android_serialport_api.xingbang.custom.ShouQuanData;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SouSuoSQActivity extends BaseActivity {


    @BindView(R.id.ss_btn_ss)
    Button ss_btn_ss;
    @BindView(R.id.ss_btn_px)
    Button ss_btn_px;
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
    @BindView(R.id.lay_bottom)
    LinearLayout layBottom;//底部布局
    @BindView(R.id.tv_check_all)
    TextView tv_check_all;
    @BindView(R.id.tv_input)
    TextView tv_input;
    @BindView(R.id.tv_ture)
    TextView tv_delete;
    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private ChaKan_SQAdapter<DetonatorTypeNew> mAdapter;
    private DataAdapter mAdapter2;
    private List<DetonatorTypeNew> mListData = new ArrayList<>();
    private List<ShouQuanData> mList = new ArrayList<>();
    private Handler mHandler_UI = new Handler();     // UI处理
    private String sqrq="";
    private int paiChoice=1;
    private static final int STATE_DEFAULT = 0;//默认状态
    private static final int STATE_EDIT = 1;//编辑状态
    private int mEditMode = STATE_DEFAULT;
    private boolean editorStatus = false;//是否为编辑状态
    private int index = 0;//当前选中的item数
    private String mRegion;     // 区域
    private String mOldTitle;   // 原标题
    private String isShowZc = "";//用来判断是否需要展示“选择雷管”按钮进行注册
    String TAG="授权注册";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sou_suo_sqactivity);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        // 原标题
        mOldTitle = getSupportActionBar().getTitle().toString();
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if(bundle.getString("sqrq")!=null){
                sqrq = bundle.getString("sqrq");
            }

            paiChoice = bundle.getInt("paiChoice");
            mRegion = bundle.getString("mRegion");
            isShowZc = !TextUtils.isEmpty(bundle.getString("isShowZc")) ?
                    bundle.getString("isShowZc") : "";
        }
        Log.e(TAG, "paiChoice: "+paiChoice);
        Log.e(TAG, "mRegion: "+mRegion);
        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter2 = new DataAdapter(R.layout.item_shouquan, mList);//绑定视图和数据
        re_ss.setLayoutManager(linearLayoutManager);
        re_ss.setAdapter(mAdapter2);

        mHandler_UI = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1:

                    Log.e("授权查询", "授权日期sqrq: " + sqrq);
                    if (sqrq.equals("")) {
                        mListData = new GreenDaoMaster().queryDetonatorShouQuan();
                    } else {
//                        Log.e("授权查询", "管壳码Gkm: " + msg.obj.toString());
                        mListData = new GreenDaoMaster().queryDetonatorShouQuanForGkm(msg.obj.toString(), sqrq);
                    }
                    Log.e("查询", "mListData.size: " + mListData.size());
                    if (mListData.size() == 0) {
                        show_Toast(getResources().getString(R.string.text_wzd));
                    }
                    mList.clear();
                    Log.e("加载", "mListData.size(): " + mListData.size());
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
                    hideInputKeyboard();//隐藏键盘,取消焦点
                    break;
                case 2:
                    show_Toast(getResources().getString(R.string.text_lgvf));
                    break;
                case 3:
                    show_Toast(getResources().getString(R.string.text_cdcw));
                    break;
                case 4:
                    try {
                        Collections.sort(mList);
                        mAdapter2.setNewData(mList);
                        mAdapter2.notifyDataSetChanged();
                    } catch (Exception e) {
                        show_Toast(getResources().getString(R.string.text_pxsb));
                    }
                    break;
                case 6:
                    mListData.clear();
                    if (sqrq.equals("")) {
                        mListData = new GreenDaoMaster().queryDetonatorShouQuan2();
                    } else {
                        mListData = new GreenDaoMaster().queryDetonatorShouQuan2(sqrq);
                    }
                    Log.e("查询", "mListData: " + mListData.toString());
                    if (mListData.size() == 0) {
                        show_Toast("未找到当前雷管");
                    }
                    setmOldTitle();
                    mList.clear();
                    Log.e("加载全部项目", "mListData.size(): " + mListData.size());
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
                    hideInputKeyboard();//隐藏键盘,取消焦点
                    break;
                case 7:
                    show_Toast(getResources().getString(R.string.text_send_tip26));
                    break;
                default:
                    break;
            }
            return false;
        });

        mHandler_UI.sendMessage(mHandler_UI.obtainMessage(1));
    }


    //隐藏键盘
    public void hideInputKeyboard() {

        edit_gkm.clearFocus();//取消焦点

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
    private void setmOldTitle() {
        getSupportActionBar().setTitle(mOldTitle + "(共:" + mListData.size() + ")");

    }
    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sq2, menu);
        MenuItem item1 = menu.findItem(R.id.item_1);
        //如果是从项目管理模块进来的   暂时屏蔽“选择雷管”注册功能
        item1.setVisible(TextUtils.isEmpty(isShowZc) ? true : false); // 显示特定菜单项
        // 初始时确保菜单项是可见的
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
                updateEditState();
                return true;
            case R.id.item_2:
                mHandler_UI.sendMessage(mHandler_UI.obtainMessage(6));
            default:
                return super.onOptionsItemSelected(item);
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

    private boolean isSelectAll = true;//是否全选
    @OnClick({R.id.tv_check_all, R.id.tv_input, R.id.tv_ture, R.id.ss_btn_ss, R.id.ss_btn_px})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ss_btn_ss:
                String gkm = edit_gkm.getText().toString();
                Message msg = new Message();
                msg.what = 1;
                msg.obj = gkm;
                mHandler_UI.sendMessage(msg);
                break;
            case R.id.ss_btn_px:
                mHandler_UI.sendMessage(mHandler_UI.obtainMessage(4));
                break;
            case R.id.tv_check_all:
                if (isSelectAll) {
                    tv_check_all.setText(getResources().getString(R.string.text_qxqx));
                    isSelectAll = false;
                    setAllItemChecked(true);
                } else {
                    tv_check_all.setText(getResources().getString(R.string.text_qx));
                    isSelectAll = true;
                    setAllItemChecked(false);
                }
//                setAllItemChecked();
                break;
            case R.id.tv_input:
                List<ShouQuanData> seleceList = new ArrayList<>();
                for (ShouQuanData data : mList) {
                    if (data.isSelect()) {
                        seleceList.add(data);
                    }
                }
                if (seleceList.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_selectlg));
                    return;
                }
                inputLeiGuan();
                break;
            case R.id.tv_ture:
//                deleteCheckItem();
                break;
        }
    }

    //删除选中的item
    private void deleteCheckItem() {
        if (mAdapter2 == null) return;
        if (mList == null) return;
        GreenDaoMaster master = new GreenDaoMaster();

        for (int i = mList.size() - 1; i >= 0; i--) {

            if (mList.get(i).isSelect()) {
                master.deleteDetonatorShouQuan(mList.get(i).getShellBlastNo());
                mList.remove(i);
            }
        }
        show_Toast(getResources().getString(R.string.text_del_ok));
        Log.e("删除", "mList.size(): " + mList.size());
        master.updataShouQuan(sqrq, mList.size());
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
        String gkm = edit_gkm.getText().toString();
        Message msg = new Message();
        msg.what = 1;
        msg.obj = gkm;
        mHandler_UI.sendMessage(msg);
        mAdapter2.notifyDataSetChanged();
    }

    //注册选中的item
    private void inputLeiGuan() {

        for (int i = 0; i <mList.size(); i++) {

            if (mList.get(i).isSelect()) {
                registerDetonator(mList.get(i));
            }
        }
        updateEditState();
        show_Toast("注册成功");
//        String gkm = edit_gkm.getText().toString();
//        Message msg = new Message();
//        msg.what=1;
//        msg.obj=gkm;
//        mHandler_UI.sendMessage(msg);
        mAdapter2.notifyDataSetChanged();
    }


    /**
     * 注册雷管
     */
    private void registerDetonator(ShouQuanData db) {
        boolean chongfu = false;
        int maxNo = getMaxNumberNo();
        Log.e("接收注册", "shellNo: " + db.getShellBlastNo());
        if (db.getShellBlastNo().length() < 13) {

            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(3));
            return;
        }
        if (db.getShellBlastNo().length() > 13) {

            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(7));
            return;
        }
        //检查芯片码重复数据
        if (db.getDetonatorId() != null && db.getDetonatorId().length() == 13 && checkRepeatDenatorId(db.getDetonatorId())) {
            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(2));
            return;
        }
        if (checkRepeatShellNo(db.getShellBlastNo())) {//检查管壳码重复数据
            mHandler_UI.sendMessage(mHandler_UI.obtainMessage(2));
            return;
        }


        String version = null;
        String yscs = null;
        if (db.getDetonatorId() != null) {
//            duan = db.getCong_yscs();
            version = db.getDetonatorIdSup();
            yscs = db.getZhu_yscs();
        }
        int maxKong = new GreenDaoMaster().getPieceAndPaiMaxKong(mRegion, paiChoice);//获取该区域最大孔号
        String duan = "1";
        int duanNUM = new GreenDaoMaster().getDuanNo(mRegion, duan);
        Log.e("搜索", "duanNUM: "+duanNUM);
        Log.e("搜索", "maxKong: "+maxKong);
        maxNo++;
        DenatorBaseinfo denator = new DenatorBaseinfo();
        denator.setBlastserial((maxKong + 1));
        denator.setSithole((maxKong + 1) + "");
        denator.setDenatorId(db.getDetonatorId());
        denator.setShellBlastNo(db.getShellBlastNo());
        denator.setZhu_yscs(yscs);
//        denator.setDelay(Integer.parseInt(delay));//PT不更新延时
        if (!TextUtils.isEmpty(db.getQibao())) {
            if (db.getQibao().equals("雷管正常")||db.getQibao().equals("已起爆")) {
                denator.setRegdate(db.getTime());
            } else {
                denator.setRegdate(Utils.getDateFormat(new Date()));
            }
        } else {
            denator.setRegdate(Utils.getDateFormat(new Date()));
        }
        denator.setStatusCode("02");
        denator.setStatusName("已注册");
        denator.setErrorCode("00");
        denator.setErrorName("");
        denator.setWire("");
        denator.setPiece(mRegion);
        denator.setDuanNo(1);
        denator.setDuan(Integer.parseInt(duan));
        denator.setPai(paiChoice+"");
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

    public boolean checkRepeatShellNo(String shellBlastNo) {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> denatorBaseinfo = master.checkRepeatShellNo(shellBlastNo);
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

    //区域全选和取消全选
    private void setAllItemChecked(boolean isSelected) {
        if (mAdapter2 == null) return;
        if (isSelected) {
            for (ShouQuanData data : mList) {
                data.setSelect(true);
            }
        } else {
            for (ShouQuanData data : mList) {
                data.setSelect(false);
            }
        }
        mAdapter2.setNewData(mList);
        mAdapter2.notifyDataSetChanged();
        index = mList.size();
    }

    //全部正确选中
    private void setAllTureChecked() {
        if (mAdapter2 == null) return;
        for (int i = 0; i < mList.size(); i++) {
            if(mList.get(i).getQibao().equals("雷管正常")){
                mList.get(i).setSelect(true);
            }
        }
        mAdapter2.setNewData(mList);
        mAdapter2.notifyDataSetChanged();
        index = mList.size();
    }
}