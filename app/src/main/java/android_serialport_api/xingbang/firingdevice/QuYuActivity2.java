package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.QuYuAdapter;
import android_serialport_api.xingbang.custom.QuYuData;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.QuYu;
import android_serialport_api.xingbang.db.greenDao.QuYuDao;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.MyAlertDialog;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuYuActivity2 extends BaseActivity {
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.title_add)
    ImageView titleAdd;
    @BindView(R.id.title_right1)
    TextView titleRight1;
    @BindView(R.id.title_right2)
    TextView titleRight2;
    @BindView(R.id.title_delete)
    ImageView titleDelete;
    @BindView(R.id.title_lefttext)
    TextView title_lefttext;
    @BindView(R.id.rl_quyu)
    RecyclerView rlQuyu;
    @BindView(R.id.lay_bottom)
    LinearLayout layBottom;
    @BindView(R.id.tv_input)
    TextView tv_input;
    @BindView(R.id.tv_check_all)
    TextView tv_check_all;
    @BindView(R.id.tv_sure)
    TextView tv_ture;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    private QuYuAdapter quyuAdapter;
    private List<QuYu> mListData = new ArrayList<>();
    private List<QuYuData> mQuYuList = new ArrayList<>();
    private ArrayList<Integer> qyIdList = new ArrayList<>();
    private MyAlertDialog myDialog;
    private Handler mHandle;
    private LoadingDialog loadingDialog;
    private String TAG = "区域选择页面";
    private String qbxm_id = "-1";
    private String qbxm_name = "";
    private boolean isDelete = true;//是否展示底部的多选删除按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qu_yu2);
        ButterKnife.bind(this);

        loadingDialog = new LoadingDialog(QuYuActivity2.this)
                .setLoadingText("加载中...");
        titleText.setText("选择区域");
        titleText.setVisibility(View.GONE);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            qbxm_id = !TextUtils.isEmpty((String) bundle.get("qbxm_id")) ?
                    (String) bundle.get("qbxm_id") : "";
            qbxm_name = !TextUtils.isEmpty((String) bundle.get("qbxm_name")) ?
                    (String) bundle.get("qbxm_name") : "";
        } else {
            qbxm_id = "-1";
            qbxm_name = "";
        }
        SoundPlayUtils.init(this);
        titleBack.setVisibility(View.GONE);
        title_lefttext.setVisibility(View.VISIBLE);
        title_lefttext.setText(getResources().getString(R.string.text_qygl));
        titleRight1.setVisibility(View.GONE);
        titleRight2.setVisibility(View.VISIBLE);
        titleRight1.setText(getResources().getString(R.string.text_zw));
        titleRight2.setText(getResources().getString(R.string.text_addqy));
        titleDelete.setVisibility(View.GONE);
        titleAdd.setVisibility(View.GONE);
        layBottom.setVisibility(View.GONE);
        // 线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rlQuyu.setLayoutManager(linearLayoutManager);
        quyuAdapter = new QuYuAdapter(R.layout.item_quyu, mQuYuList);
        rlQuyu.setAdapter(quyuAdapter);
        quyuAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                return;
            }
            lastClickTime = System.currentTimeMillis();
            if (isDelete) {
                String str1 = mListData.get(position).getQyid() + "";
                Intent intent1 = new Intent(QuYuActivity2.this, ReisterMainPage_scan.class);
                intent1.putExtra("quyuId", str1);
                startActivity(intent1);
//                finish();
            }
        });
        quyuAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                isDelete = false;
                titleRight1.setText(getResources().getString(R.string.text_dialog_qx));
                layBottom.setVisibility(View.VISIBLE);
                quyuAdapter.showCheckBox(true);
                return true;
            }
        });
        mHandle = new Handler(msg -> {
            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    mListData = new GreenDaoMaster().queryQuYu();
                    mQuYuList.clear();
                    Log.e("加载", "mListData.size(): " + mListData.size());
                    for (QuYu item : mListData) {
                        QuYuData qyData = new QuYuData();
                        qyData.setId(item.getId());
                        qyData.setQyid(item.getQyid());
                        qyData.setName(item.getName());
                        qyData.setKongDelay(item.getKongDelay());
                        qyData.setPaiDelay(item.getPaiDelay());
                        qyData.setStartDelay(item.getStartDelay());
                        qyData.setSelected(item.getSelected());
                        qyData.setIsQb(item.getIsQb());
                        if (!mQuYuList.contains(qyData)) {
                            mQuYuList.add(qyData);
                        }
                    }
                    quyuAdapter.setNewData(mQuYuList);
                    rlQuyu.setAdapter(quyuAdapter);
                    //新增区域后要有提示音
                    if ("xz".equals(result)) {
                        SoundPlayUtils.play(1);
                    } else if ("zw".equals(result)) {
                        isDelete = true;
                        titleRight1.setText(getResources().getString(R.string.text_zw));
                        layBottom.setVisibility(View.GONE);
                        quyuAdapter.showCheckBox(false);
                        tv_check_all.setText(getResources().getString(R.string.text_qx));
                        isSelectAll = true;
                        setAllItemChecked(false);
                        SoundPlayUtils.play(1);
                    }
                    break;
                case 2:
                    if (mListData.size() > 0) {
                        QuYuDao qyDao = getDaoSession().getQuYuDao();
                        List<QuYu> selectedQy = qyDao.queryBuilder()
                                .where(QuYuDao.Properties.Selected.eq("true"))
                                .list();
                        //如果区域列表中没有找到已组网（selected字段为"true"）的区域，就设置列表中第一个区域为 组网区域
                        if (selectedQy.isEmpty()) {
                            // 获取第一条记录并更新其 selected 为 "true"
                            List<QuYu> allQuYuList = qyDao.loadAll();
                            if (!allQuYuList.isEmpty()) {
                                QuYu firstQuYu = allQuYuList.get(0);
                                firstQuYu.setSelected("true");
                                // 更新该记录
                                qyDao.update(firstQuYu);
                                quyuAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    layBottom.setVisibility(View.GONE);
                    quyuAdapter.showCheckBox(false);
                    mListData = new GreenDaoMaster().queryQuYu();
                    mQuYuList.clear();
                    Log.e("加载", "mListData.size(): " + mListData.size());
                    for (QuYu item : mListData) {
                        QuYuData qyData = new QuYuData();
                        qyData.setId(item.getId());
                        qyData.setQyid(item.getQyid());
                        qyData.setName(item.getName());
                        qyData.setKongDelay(item.getKongDelay());
                        qyData.setPaiDelay(item.getPaiDelay());
                        qyData.setStartDelay(item.getStartDelay());
                        qyData.setSelected(item.getSelected());
                        qyData.setIsQb(item.getIsQb());
                        if (!mQuYuList.contains(qyData)) {
                            mQuYuList.add(qyData);
                        }
                    }
                    quyuAdapter.setNewData(mQuYuList);
                    rlQuyu.setAdapter(quyuAdapter);
                    isDelete = true;
                    titleRight1.setText(getResources().getString(R.string.text_zw));
                    layBottom.setVisibility(View.GONE);
                    quyuAdapter.showCheckBox(false);
                    tv_check_all.setText(getResources().getString(R.string.text_qx));
                    isSelectAll = true;
                    setAllItemChecked(false);
                    SoundPlayUtils.play(1);
                    break;
            }
            return false;
        });
        Message msg = new Message();
        msg.what = 1;
        msg.obj = "cx";
        mHandle.sendMessage(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListData = new GreenDaoMaster().queryQuYu();
        if (mListData.isEmpty()) {
            show_Toast(getResources().getString(R.string.text_tjqy));
        }
        Log.e(TAG,"区域数据:" + mListData.toString());
        quyuAdapter.notifyDataSetChanged();
    }

    private boolean isSelectAll = true;//是否全选
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 2000; // 快速点击间隔

    @OnClick({R.id.title_back, R.id.title_add, R.id.title_right1,R.id.title_right2,R.id.title_delete,
            R.id.tv_check_all, R.id.tv_input,R.id.tv_sure,R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back://注册
                finish();
                break;
            case R.id.title_right2:
            case R.id.title_add://添加区域
                carteQuYu();
                break;
            case R.id.tv_cancel:
                isDelete = true;
                layBottom.setVisibility(View.GONE);
                quyuAdapter.showCheckBox(false);
                tv_check_all.setText(getResources().getString(R.string.text_qx));
                isSelectAll = true;
                setAllItemChecked(false);
                break;
            case R.id.title_right1:
                if (mListData.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_tjqy));
                    return;
                }
                if (isDelete) {
                    isDelete = false;
                    titleRight1.setText(getResources().getString(R.string.text_dialog_qx));
                    layBottom.setVisibility(View.VISIBLE);
                    quyuAdapter.showCheckBox(true);
                } else {
                    isDelete = true;
                    titleRight1.setText(getResources().getString(R.string.text_zw));
                    layBottom.setVisibility(View.GONE);
                    quyuAdapter.showCheckBox(false);
                }
                break;
            case R.id.tv_check_all://全选
                if (mListData.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_tjqy));
                    return;
                }
                if (isSelectAll) {
                    tv_check_all.setText(getResources().getString(R.string.text_qxqx));
                    isSelectAll = false;
                    setAllItemChecked(true);
                } else {
                    tv_check_all.setText(getResources().getString(R.string.text_qx));
                    isSelectAll = true;
                    setAllItemChecked(false);
                }
                break;
            case R.id.tv_input:
                Log.e(TAG, "记录时间: " + lastClickTime);
                Log.e(TAG, "点击时间: " + (System.currentTimeMillis() - lastClickTime));
                if (mListData.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_tjqy));
                    return;
                }
                if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                    return;
                }
                lastClickTime = System.currentTimeMillis();

                for (QuYuData data : mQuYuList) {
                    if (data.isSelect()) {
                        qyIdList.add(data.getQyid());
                    }
                }
                if (qyIdList.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_selectqy));
                    return;
                }
                if (!QuYuActivity2.this.isFinishing()) {
                    AlertDialog dialog = new AlertDialog.Builder(QuYuActivity2.this)
                            .setTitle(getResources().getString(R.string.text_fir_dialog2))//设置对话框的标题
                            .setMessage(getResources().getString(R.string.text_scsyqy))//设置对话框的内容
                            //设置对话框的按钮
                            .setNeutralButton(getResources().getString(R.string.text_dialog_qx), (dialog1, which) -> {
                                dialog1.dismiss();
                            })
                            .setNegativeButton(R.string.text_dialog_zsclg, (dialog1, which) -> {
                                for (QuYuData data : mQuYuList) {
                                    if (data.isSelect()) {
                                        GreenDaoMaster master = new GreenDaoMaster();
                                        master.deleteLeiGuanFroPiece(data.getQyid() + "");
                                        master.updataPaiFroPiace(data.getQyid() + "");
                                    }
                                }
                                //只删除雷管时，选中的区域，“已组网”、”已起爆“需消失
                                GreenDaoMaster master = new GreenDaoMaster();
                                //取消区域”已起爆“状态
                                master.cancelQyQbStataus(qyIdList);
                                //取消区域”已组网“状态
                                master.setQyZwStataus(qyIdList,"false");
                                show_Toast(getResources().getString(R.string.text_del_ok));
                                mHandle.sendMessage(mHandle.obtainMessage(2));
                                qyIdList.clear();
                                Utils.saveFile();//把软存中的数据存入磁盘中
                                Log.e(TAG, "区域页面多选结果:" + qyIdList.toString());
                                AppLogUtils.writeAppLog("点击了多选删除区域按钮");
                            } )
                            .setPositiveButton(R.string.text_dialog_qbsc, (dialog14, which) -> {
                                for (QuYuData data : mQuYuList) {
                                    if (data.isSelect()) {
                                        GreenDaoMaster master = new GreenDaoMaster();
                                        master.deleteQuYuForId(data.getQyid());
                                        master.deletePaiFroPiace(data.getQyid() + "");
                                        master.deleteLeiGuanFroPiece(data.getQyid() + "");


                                    }
                                }
                                show_Toast(getResources().getString(R.string.text_del_ok));
                                mHandle.sendMessage(mHandle.obtainMessage(2));
                                qyIdList.clear();
                                Utils.saveFile();//把软存中的数据存入磁盘中
                                Log.e(TAG, "区域页面多选结果:" + qyIdList.toString());
                                AppLogUtils.writeAppLog("点击了多选删除区域按钮");
                            }).create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                break;
            case R.id.tv_sure:
                if (mListData.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_tjqy));
                    return;
                }
                if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                    return;
                }
                lastClickTime = System.currentTimeMillis();
                for (QuYuData data : mQuYuList) {
                    if (data.isSelect()) {
                        qyIdList.add(data.getQyid());
                    }
                }
                if (qyIdList.isEmpty()) {
                    show_Toast(getResources().getString(R.string.text_selectqy));
                    return;
                }
                Log.e(TAG,"id集合:" + qyIdList.toString());
                //先将之前已经组网的区域selected改为false 再将最新选中的区域selected改为true
                QuYuDao qyDao = getDaoSession().getQuYuDao();
                // 1. 查询并将所有 selected 为 true 的记录更新为 false
                List<QuYu> selectedQyList = qyDao.queryBuilder()
                        .where(QuYuDao.Properties.Selected.eq("true"))
                        .list();
                // 遍历所有已选中的记录并更新 selected 字段为 false
                for (QuYu quyu : selectedQyList) {
                    quyu.setSelected("false");  // 将 selected 字段更新为 false
                }
                // 批量更新所有 selected 为 true 的记录为 false
                qyDao.updateInTx(selectedQyList);
                // 2. 查询并更新当前选中的记录（根据 qyIdList 更新）
                List<QuYu> quyuList = qyDao.queryBuilder()
                        .where(QuYuDao.Properties.Qyid.in(qyIdList))  // 根据 qyIdList 查找
                        .list();
                // 将这些记录的 selected 字段更新为 true
                for (QuYu quyu : quyuList) {
                    quyu.setSelected("true");  // 设置 selected 字段为 "true"
                }
                // 批量更新选中的记录
                qyDao.updateInTx(quyuList);
                show_Toast(getResources().getString(R.string.text_zwcg));
                Message msg = new Message();
                msg.what = 1;
                msg.obj = "zw";
                mHandle.sendMessage(msg);
//                mHandle.sendMessage(mHandle.obtainMessage(1));
                qyIdList.clear();
                break;
        }
    }

    private void carteQuYu() {

        myDialog = new MyAlertDialog(this).builder();
        int maxNo = new GreenDaoMaster().getPieceMaxqyid();
        myDialog.setTitle("新增区域" + (maxNo + 1) + "设置")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", v -> {
//                    EditText name = myDialog.getView().findViewById(R.id.et_name);
//                    EditText startDelay = myDialog.getView().findViewById(R.id.txt_startDelay);
                    EditText kongDelay = myDialog.getView().findViewById(R.id.txt_kongDelay);
                    EditText paiDelay = myDialog.getView().findViewById(R.id.txt_paiDelay);
//                    Log.e("打印", "name: " + name.getText());
//                    Log.e("打印", "startDelay: " + startDelay);
//                    Log.e("打印", "kongDelay: " + kongDelay);
//                    Log.e("打印", "paiDelay: " + paiDelay);
//                    if (name.getText().length() > 0) {

                    QuYu quYu = new QuYu();
                    quYu.setName((maxNo + 1) + "");
                    quYu.setQyid((maxNo + 1));
                    quYu.setStartDelay("0");
                    quYu.setKongDelay(kongDelay.getText().toString());
                    quYu.setPaiDelay(paiDelay.getText().toString());
                    if (mListData.size() < 1) {
                        quYu.setSelected("true");
                    } else {
                        quYu.setSelected("false");
                    }
                    getDaoSession().getQuYuDao().insert(quYu);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = "xz";
                    mHandle.sendMessage(msg);
//                    mHandle.sendMessage(mHandle.obtainMessage(1));
//                    }

                }).show();

    }

    private int index = 0;//当前选中的item数
    private static final int STATE_DEFAULT = 0;//默认状态
    private static final int STATE_EDIT = 1;//编辑状态
    private int mEditMode = STATE_DEFAULT;
    private boolean editorStatus = false;//是否为编辑状态

    //区域全选和取消全选
    private void setAllItemChecked(boolean isSelected) {
        if (quyuAdapter == null) return;
        if (isSelected) {
            for (QuYuData yuData : mQuYuList) {
                yuData.setSelect(true);
            }
        } else {
            for (QuYuData yuData : mQuYuList) {
                yuData.setSelect(false);
            }
        }
        quyuAdapter.notifyDataSetChanged();
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
        quyuAdapter.setEditMode(mEditMode);

        quyuAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (editorStatus) {//编辑状态
                    QuYuData dataBean = mQuYuList.get(position);
                    boolean isSelect = dataBean.isSelect();
                    if (!isSelect) {
                        index++;
                        dataBean.setSelect(true);

                    } else {
                        dataBean.setSelect(false);
                        index--;
                    }


                    quyuAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}