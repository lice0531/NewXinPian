package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

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

import androidx.annotation.NonNull;
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
import android_serialport_api.xingbang.utils.MyAlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuYuActivity extends BaseActivity {


    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.title_add)
    ImageView titleAdd;
    @BindView(R.id.title_right)
    TextView titleRight;
    @BindView(R.id.rl_quyu)
    RecyclerView rlQuyu;
    @BindView(R.id.lay_bottom)
    LinearLayout layBottom;
    @BindView(R.id.tv_input)
    TextView tv_input;
    @BindView(R.id.tv_check_all)
    TextView tv_check_all;
    TextView totalbar_title;
    private QuYuAdapter quyuAdapter;
    private List<QuYu> mListData = new ArrayList<>();
    private List<QuYuData> mQuYuList = new ArrayList<>();
    private ArrayList<Integer> qyIdList = new ArrayList<>();
    private MyAlertDialog myDialog;
    private Handler mHandle;
    private LoadingDialog loadingDialog;
    private String pageFlag = "";//区分是从主页哪个模块进入区域选择页面
    private String TAG = "区域选择页面";
    private String qbxm_id = "-1";
    private String qbxm_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qu_yu);
        ButterKnife.bind(this);

        loadingDialog = new LoadingDialog(QuYuActivity.this)
                .setLoadingText("加载中...");
        titleText.setText("选择区域");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            qbxm_id = !TextUtils.isEmpty((String)bundle.get("qbxm_id")) ?
                    (String)bundle.get("qbxm_id") : "";
            qbxm_name = !TextUtils.isEmpty((String) bundle.get("qbxm_name")) ?
                    (String) bundle.get("qbxm_name") : "";
        } else {
            qbxm_id = "-1";
            qbxm_name = "";
        }
        pageFlag = !TextUtils.isEmpty(intent.getStringExtra("pageFlag"))?
                intent.getStringExtra("pageFlag") : "";
        if (!TextUtils.isEmpty(pageFlag)) {
            if ("zhuce".equals(pageFlag)) {
                layBottom.setVisibility(View.GONE);
                titleAdd.setVisibility(View.VISIBLE);
            } else {
                if ("testDenator".equals(pageFlag)) {
                    tv_input.setText(getResources().getString(R.string.text_zwcs));
                } else {
                    tv_input.setText(getResources().getString(R.string.text_zwqb));
                }
                layBottom.setVisibility(View.VISIBLE);
                titleAdd.setVisibility(View.GONE);
            }
        }
        mListData = new GreenDaoMaster().queryQuYu();
        // 线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rlQuyu.setLayoutManager(linearLayoutManager);
        quyuAdapter = new QuYuAdapter(R.layout.item_quyu, mQuYuList);
        rlQuyu.setAdapter(quyuAdapter);
        if (!TextUtils.isEmpty(pageFlag) && !"zhuce".equals(pageFlag)) {
            quyuAdapter.showCheckBox(true);
        }
        quyuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if ("zhuce".equals(pageFlag)) {
//                loadingDialog.show();
                    String str1 = mListData.get(position).getId() + "";
                    Intent intent = new Intent(QuYuActivity.this, ReisterMainPage_scan.class);
                    intent.putExtra("quyuId", str1);
                    startActivity(intent);
                    finish();
//                loadingDialog.close();
                }
            }
        });
        quyuAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                return false;
            }
        });


        mHandle = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
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
                            if (!mQuYuList.contains(qyData)) {
                                mQuYuList.add(qyData);
                            }
                        }

                        quyuAdapter.setNewData(mQuYuList);
                        rlQuyu.setAdapter(quyuAdapter);
                        break;
                    case 2:
                        break;
                }
                return false;
            }
        });

        mHandle.sendMessage(mHandle.obtainMessage(1));
    }

    private boolean isSelectAll = true;//是否全选
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 2000; // 快速点击间隔
    @OnClick({R.id.title_back, R.id.title_add, R.id.tv_check_all,R.id.tv_input})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back://注册
                finish();
                break;
            case R.id.title_add://注册
                carteQuYu();
                break;
            case R.id.tv_check_all://全选
                if (isSelectAll) {
                    tv_check_all.setText("取消全选");
                    isSelectAll = false;
                    setAllItemChecked(true);
                } else {
                    tv_check_all.setText("全选");
                    isSelectAll = true;
                    setAllItemChecked(false);
                }
                break;
            case R.id.tv_input:
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
                    show_Toast("请先选择区域");
                    return;
                }
                Intent intent = new Intent();
                if ("testDenator".equals(pageFlag)) {
                    //进入网检页面
                    intent.setClass(QuYuActivity.this, TestDenatorActivity.class);
                    intent.putIntegerArrayListExtra("qyList",qyIdList);
                    intent.putExtra("dataSend", "测试");
                } else {
                    //进入起爆页面
                    intent.setClass(QuYuActivity.this, FiringMainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("qbxm_id", qbxm_id);
                    bundle.putString("qbxm_name", qbxm_name);
                    intent.putExtras(bundle);
                    intent.putIntegerArrayListExtra("qyList",qyIdList);
                    intent.putExtra("dataSend", "起爆");
                }
                startActivity(intent);
                Log.e(TAG,"区域页面多选结果:" + qyIdList.toString());
                finish();
                break;
        }
    }

    private void carteQuYu() {

        myDialog = new MyAlertDialog(this).builder();
        int maxNo = new GreenDaoMaster().getPieceMaxqyid();
        myDialog.setTitle("新增区域"+(maxNo+1)+"设置")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", v -> {
//                    EditText name = myDialog.getView().findViewById(R.id.et_name);
//                    EditText startDelay = myDialog.getView().findViewById(R.id.txt_startDelay);
                    EditText kongDelay = myDialog.getView().findViewById(R.id.txt_kongDelay);
                    EditText paiDelay = myDialog.getView().findViewById(R.id.txt_paiDelay);
//                    Log.e("打印", "name: " + name.getText());
//                    Log.e("打印", "startDelay: " + startDelay);
                    Log.e("打印", "kongDelay: " + kongDelay);
                    Log.e("打印", "paiDelay: " + paiDelay);
//                    if (name.getText().length() > 0) {

                        QuYu quYu = new QuYu();
//                        quYu.setName(name.getText().toString());
                        quYu.setQyid((maxNo+1));
//                        quYu.setStartDelay(startDelay.getText().toString());
                        quYu.setKongDelay(kongDelay.getText().toString());
                        quYu.setPaiDelay(paiDelay.getText().toString());
                        getDaoSession().getQuYuDao().insert(quYu);
                        mHandle.sendMessage(mHandle.obtainMessage(1));
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