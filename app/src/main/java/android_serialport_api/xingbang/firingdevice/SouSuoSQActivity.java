package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
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
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    // 雷管列表
    private LinearLayoutManager linearLayoutManager;
    private ChaKan_SQAdapter<DetonatorTypeNew> mAdapter;
    private DataAdapter mAdapter2;
    private List<DetonatorTypeNew> mListData = new ArrayList<>();
    private List<ShouQuanData> mList = new ArrayList<>();
    private Handler mHandler_UI = new Handler();     // UI处理
    private String sqrq;
    private static final int STATE_DEFAULT = 0;//默认状态
    private static final int STATE_EDIT = 1;//编辑状态
    private int mEditMode = STATE_DEFAULT;
    private boolean editorStatus = false;//是否为编辑状态
    private int index = 0;//当前选中的item数
    private String mRegion;     // 区域

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sou_suo_sqactivity);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        sqrq = bundle.getString("sqrq");
//        long time = (long) 5 * 86400000;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String format2 = simpleDateFormat.format(sqrq);
//        String format1 = simpleDateFormat.format(format2+ time);
//        Log.e("获取到有效期为", "format1: "+format1 );
        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter2 = new DataAdapter(R.layout.item_shouquan, mList);//绑定视图和数据
        re_ss.setLayoutManager(linearLayoutManager);
        re_ss.setAdapter(mAdapter2);

        mHandler_UI = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1:
                    Log.e("查询", "msg.obj.toString(): " + msg.obj.toString());
                    mListData = new GreenDaoMaster().queryDetonatorShouQuanForGkm(msg.obj.toString(), sqrq);
                    Log.e("查询", "mListData: " + mListData.toString());
                    if (mListData.size() == 0) {
                        show_Toast("未找到当前雷管");
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
                    show_Toast("雷管重复");
                    break;
                case 3:
                    show_Toast("雷管管壳码长度错误");
                    break;
                case 4:
                    try {
                        Collections.sort(mList);
                        mAdapter2.setNewData(mList);
                        mAdapter2.notifyDataSetChanged();
                    } catch (Exception e) {
                        show_Toast("排序失败!");
                    }
                    break;
                case 7:
                    show_Toast("数据异常,请检查雷管厂家是否正确");
                    break;
                default:
                    break;
            }
            return false;
        });
    }


    //隐藏键盘
    public void hideInputKeyboard() {

        edit_gkm.clearFocus();//取消焦点

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sq2, menu);
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

    @OnClick({R.id.tv_check_all, R.id.tv_input, R.id.tv_delete, R.id.ss_btn_ss, R.id.ss_btn_px})
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
                setAllItemChecked();
                break;
            case R.id.tv_input:
                inputLeiGuan();
                break;
            case R.id.tv_delete:
                deleteCheckItem();
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

        for (int i = mList.size() - 1; i >= 0; i--) {

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

        String duan = "1";
        String version = null;
        String yscs = null;
        if (db.getDetonatorId() != null) {
            duan = db.getCong_yscs();
            version = db.getDetonatorIdSup();
            yscs = db.getZhu_yscs();
        }
        String delay = "";
        switch (duan) {
            case "1":
                delay = "0";
                break;
            case "2":
                delay = "25";
                break;
            case "3":
                delay = "50";
                break;
            case "4":
                delay = "75";
                break;
            case "5":
                delay = "100";
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
        if (db.getQibao().equals("雷管正常")||db.getQibao().equals("已起爆")) {
            denator.setRegdate(db.getTime());
        } else {
            denator.setRegdate(Utils.getDateFormat(new Date()));
        }
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
}