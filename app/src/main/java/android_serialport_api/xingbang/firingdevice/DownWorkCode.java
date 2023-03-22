package android_serialport_api.xingbang.firingdevice;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.google.gson.Gson;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.ErrDuanAdapter;
import android_serialport_api.xingbang.custom.ErrListAdapter;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.MlistView;
import android_serialport_api.xingbang.custom.ShouQuanAdapter;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.ShouQuan;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.services.LocationService;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.AMapUtils;
import android_serialport_api.xingbang.utils.LngLat;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.PropertiesUtil;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android_serialport_api.xingbang.Application.getContext;
import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DownWorkCode extends BaseActivity implements LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, ShouQuanAdapter.InnerItemOnclickListener {
    @BindView(R.id.ly_setUpdata)
    LinearLayout lySetUpData;
    @BindView(R.id.btn_down_return)
    Button btnDownReturn;
    @BindView(R.id.btn_down_inputOK)
    Button btnDownInputOK;
    @BindView(R.id.btn_down_workcode)
    Button btnDownWorkcode;
    @BindView(R.id.down_at_htid)//合同编号
    AutoCompleteTextView at_htid;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.down_at_xmbh)//项目编号
    AutoCompleteTextView at_xmbh;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.down_at_dwdm)//单位代码
    AutoCompleteTextView at_dwdm;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.down_at_coordxy)//经纬度
    AutoCompleteTextView at_coordxy;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.down_at_bprysfz)//证件号码
    AutoCompleteTextView at_bprysfz;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.text_start)
    TextView textStart;
    @BindView(R.id.entBF2Bit_st)//开始厂家码
    EditText edit_start_entBF2Bit_st;
    @BindView(R.id.entproduceDate_st)//开始日期码
    EditText edit_start_entproduceDate_st;
    @BindView(R.id.entAT1Bit_st)//开始特征码
    EditText edit_start_entAT1Bit_st;
    @BindView(R.id.entboxNoAndSerial_st)//开始流水号
    EditText edit_start_entboxNoAndSerial_st;
    @BindView(R.id.btn_ReisterScanStart_st)//开始码扫描
    Button btnReisterScanStartSt;
    @BindView(R.id.entBF2Bit_ed)//结束厂家码
    EditText edit_end_entBF2Bit_en;
    @BindView(R.id.entproduceDate_ed)//结束日期码
    EditText edit_end_entproduceDate_ed;
    @BindView(R.id.entAT1Bit_ed)//结束特征码
    EditText edit_end_entAT1Bit_ed;
    @BindView(R.id.entboxNoAndSerial_ed)//结束流水号
    EditText edit_end_entboxNoAndSerial_ed;
    @BindView(R.id.btn_ReisterScanStart_ed)//结束码扫描
    Button btnReisterScanStartEd;
    @BindView(R.id.btn_inputOk)
    Button btnInputOk;
    @BindView(R.id.ly_input)
    LinearLayout lyInput;
    @BindView(R.id.lv_shouquan)
    MlistView lvShouquan;
    @BindView(R.id.btn_inputGKM)
    Button btnInputGKM;
    @BindView(R.id.factory_listView)
    RecyclerView mListView;
    @BindView(R.id.btn_location)
    Button btnLocation;
    @BindView(R.id.btn_scanReister)
    Button btnScanReister;
    @BindView(R.id.btn_setdelay)
    Button btnSetdelay;
    @BindView(R.id.btn_clear_htid)
    Button btnClearHtid;
    @BindView(R.id.btn_clear_xmbh)
    Button btnClearXmbh;
    @BindView(R.id.btn_clear_sfz)
    Button btnClearSfz;
    @BindView(R.id.down_at_project_name)
    AutoCompleteTextView at_projectName;
    @BindView(R.id.btn_clear_project_name)
    Button btnClearProjectName;
    @BindView(R.id.textView10)
    TextView textView10;
    @BindView(R.id.setDelayTimeMainPage)
    ScrollView setDelayTimeMainPage;
    private ShouQuanAdapter mAdapter_sq;//授权
    private SQLiteDatabase db;
    private Handler mHandler_httpresult;
    private Handler mHandler_httpresult2;
    private Handler mHandler_1 = new Handler();//错误提示
    private Handler mHandler2;
    private String selectDenatorId;
    private List<Map<String, Object>> map_dl = new ArrayList<>();
    private int pageSize = 500;//每页显示的数据
    private int currentPage = 1;//当前页数
    private List<VoBlastModel> list_all = new ArrayList<>();
    private ArrayList<String> list_uid = new ArrayList<>();
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String pro_name = "";//项目名称
    private String jd = "";
    private String wd = "";
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private static int tipInfoFlag = 0;
    private String factoryCode = null;//厂家代码
    private String factoryFeature = null;////厂家特征码
    private SimpleCursorAdapter list_adapter;
    private ScanInterface scanDecode;
    private int sanButtonFlag = 0;//1s是起始按钮，2是终止按钮
    private int continueScanFlag = 0;//是否继续扫码标志 0否1是
    private String singleShellNo;//单发注册
    private int isCorrectReisterFea = 0; //是否正确的管厂码
    private Handler mHandler_3 = new Handler();//错误信息提醒
    private String lg_No;//雷管编号
    private ScanBar scanBarThread;
    //定位
    private Geocoder geocoder;
    private List<Address> addressList;
    private StringBuilder sb;

    private LocationService locationService;
    private PropertiesUtil mProp;

    private List<Map<String, Object>> map_project = new ArrayList<Map<String, Object>>();
    //    private MMKV kv;//以后替换SharedPreferences
    SharedPreferences.Editor edit;

    private LinearLayoutManager linearLayoutManager;
    private DetonatorAdapter_Paper<DenatorBaseinfo> mAdapter;
    private String mOldTitle;   // 原标题
    private String mRegion;     // 区域
    private Handler mHandler_0 = new Handler();     // UI处理
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private boolean mRegion1, mRegion2, mRegion3, mRegion4, mRegion5 = true;//是否选中区域1,2,3,4,5
    private TextView totalbar_title;

    private ArrayList<Map<String, Object>> list_data = new ArrayList<>();//段位错误的雷管信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_workcode);
        ButterKnife.bind(this);
        DatabaseHelper mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        baidudingwei();
        getUserMessage();//获取用户信息
//        getPropertiesData();//第二种获取用户信息
        initUi();

        // 区域 更新视图
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        Utils.writeRecord("---进入项目下载页面---");
//        test();//模拟下载
//        jiami();//生成离线加密文档,写在程序日志里面

    }

    private void jiami() {
//                String res2 = "{\"lgs\":{\"lg\":[{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00001\",\"fbh\":\"A62D700846B6A\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846B6AC10411\"},{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00002\",\"fbh\":\"A62D70084695E\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"84695E800411\"},{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00003\",\"fbh\":\"A62D7008469F1\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"8469F19A0412\"},\n" +
//                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00004\",\"fbh\":\"A62D700846A73\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846A734C0412\"},\n" +
//                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00005\",\"fbh\":\"A62D700846AFC\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846AFCC10413\"},\n" +
//                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00006\",\"fbh\":\"A62D700846867\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846AFCC10413\"},\n" +
//                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00007\",\"fbh\":\"A62D70084684D\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"84684D050414\"},\n" +
//                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00008\",\"fbh\":\"A62D700846764\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"8467647E0414\"},\n" +
//                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00009\",\"fbh\":\"A62D700846B80\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846B80700415\"},{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00010\",\"fbh\":\"A62D700846B5B\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846B5B050415\"}]},\"jbqys\":{\"jbqy\":[]},\"sbbhs\":[{\"sbbh\":\"F9900210001\"},{\"sbbh\":\"BQ2A4221234\"},{\"sbbh\":\"F6410000001\"},{\"sbbh\":\"F64C5000003\"},{\"sbbh\":\"F64C5000004\"}],\"zbqys\":{\"zbqy\":[{\"zbqybj\":\"5000\",\"zbqymc\":\"煋邦煤许测试1\",\"zbqywd\":\"34.297\",\"zbqssj\":null,\"zbqyjd\":\"109.1172\",\"zbjzsj\":null}]},\"sqrq\":\"2023-03-16 14:35:26\",\"cwxx\":\"0\"}";
        String res2="{\"lgs\":{\"lg\":[{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00011\",\"fbh\":\"A62D700846A8D\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846A8D554E11\"},{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00012\",\"fbh\":\"A62D70084695D\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"84695D004A11\"},{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00013\",\"fbh\":\"A62D7008467DA\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"8467DA001212\"},\n" +
                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00014\",\"fbh\":\"A62D700846B18\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846B18003A12\"},\n" +
                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00015\",\"fbh\":\"A62D700846891\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846891AA4813\"},\n" +
                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00016\",\"fbh\":\"A62D700846B0A\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846B0A002713\"},\n" +
                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00017\",\"fbh\":\"A62D700846B9C\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846B9CAA5014\"},\n" +
                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00018\",\"fbh\":\"A62D700846B54\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"846B54AA2A14\"},\n" +
                "{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00019\",\"fbh\":\"A62D7008469FC\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"8469FCAA1715\"},{\"gzmcwxx\":\"0\",\"uid\":\"5630309B00020\",\"fbh\":\"A62D7008468A4\",\"yxq\":\"2023-03-18 14:35:26\",\"gzm\":\"8468A4553415\"}]},\"jbqys\":{\"jbqy\":[]},\"sbbhs\":[{\"sbbh\":\"F9900210001\"},{\"sbbh\":\"BQ2A4221234\"},{\"sbbh\":\"F6410000001\"},{\"sbbh\":\"F64C5000003\"},{\"sbbh\":\"F64C5000004\"}],\"zbqys\":{\"zbqy\":[{\"zbqybj\":\"5000\",\"zbqymc\":\"煋邦煤许测试2\",\"zbqywd\":\"34.297\",\"zbqssj\":null,\"zbqyjd\":\"109.1172\",\"zbjzsj\":null}]},\"sqrq\":\"2023-03-16 14:35:26\",\"cwxx\":\"0\"}";
        String json = MyUtils.getBase64(MyUtils.encryptMode("jadl12345678901234370030".getBytes(), res2.getBytes()));
        Utils.writeRecord(json);
    }

    private void initUi() {
        //获取偏好设置的编辑器
        pro_name =(String) MmkvUtils.getcode("pro_name","") ;
        at_projectName.setText(pro_name);

        mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);
        mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);
        mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);
        mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);
        mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);

        totalbar_title = findViewById(R.id.title_text);
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setOnClickListener(v -> {
            choiceQuYu();
        });
        iv_back.setOnClickListener(v -> finish());
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //         获取 区域参数
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        // 原标题
        mOldTitle = getSupportActionBar().getTitle().toString();
        loadMoreData_sq();

//        loadMoreData_lg(currentPage);//查询所有雷管

        mAdapter_sq = new ShouQuanAdapter(this, map_dl, R.layout.item_list_shouquan);
        mAdapter_sq.setOnInnerItemOnClickListener(this);
        lvShouquan.setAdapter(mAdapter_sq);
        lvShouquan.setOnItemClickListener(this);

        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new DetonatorAdapter_Paper<>(this, 4);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.setAdapter(mAdapter);

        scan();//扫描初始化
        initHandle();//handle初始化
        edit_start_entBF2Bit_st.addTextChangedListener(st_1_watcher);
        edit_start_entproduceDate_st.addTextChangedListener(st_2_watcher);
        edit_start_entAT1Bit_st.addTextChangedListener(st_3_watcher);
        edit_start_entboxNoAndSerial_st.addTextChangedListener(st_4_watcher);
        edit_end_entBF2Bit_en.addTextChangedListener(end_1_watcher);
        edit_end_entproduceDate_ed.addTextChangedListener(end_2_watcher);
        edit_end_entAT1Bit_ed.addTextChangedListener(end_3_watcher);
        edit_end_entboxNoAndSerial_ed.addTextChangedListener(end_4_watcher);

        initAutoComplete("history_htid", at_htid);//输入历史记录
        initAutoComplete("history_xmbh", at_xmbh);
        initAutoComplete("history_dwdm", at_dwdm);
        initAutoComplete("history_bprysfz", at_bprysfz);
        initAutoComplete("history_coordxy", at_coordxy);
        initAutoComplete("history_projectName", at_projectName);//项目名称
        getFactoryCode();//获取厂家码
        if (factoryFeature != null && factoryFeature.trim().length() == 1) {
            edit_end_entAT1Bit_ed.setText(factoryFeature);
            edit_start_entAT1Bit_st.setText(factoryFeature);
        }
        if (factoryCode != null && factoryCode.trim().length() > 0) {
            edit_end_entBF2Bit_en.setText(factoryCode);
            edit_end_entBF2Bit_en.setFocusable(false);
            edit_start_entBF2Bit_st.setFocusable(false);
            edit_start_entBF2Bit_st.setText(factoryCode);
        }

        at_projectName.setOnItemClickListener((parent, view, position, id) -> {
            hideInputKeyboard();
            String project_name = parent.getAdapter().getItem(position).toString();
            Log.e("输入项目", "position: " + position);
            Log.e("输入项目", "project_name: " + project_name);
            Log.e("输入项目", "getItem(position): " + parent.getAdapter().getItem(position).toString());
            GreenDaoMaster master = new GreenDaoMaster();
            List<Project> list = master.queryProjectToProject_name(project_name);
            if (list.size() > 0) {
                if (list.get(0).getHtbh().length() > 0) {
                    at_htid.setText(list.get(0).getHtbh());
                } else {
                    at_htid.setText("");
                }
                if (list.get(0).getXmbh().length() > 0) {
                    at_xmbh.setText(list.get(0).getXmbh());
                } else {
                    at_xmbh.setText("");
                }
                if (list.get(0).getDwdm().length() > 0) {
                    at_dwdm.setText(list.get(0).getDwdm());
                } else {
                    at_dwdm.setText("");
                }
                if (list.get(0).getCoordxy().length() > 0) {
                    at_coordxy.setText(list.get(0).getCoordxy());
                } else {
                    at_coordxy.setText("");
                }
                at_bprysfz.setText(list.get(0).getBprysfz());
            }
            saveData();
            initView();//把输入框颜色初始化
        });

        at_htid.addTextChangedListener(htbh_watcher);//长度监听
        at_xmbh.addTextChangedListener(xmbh_watcher);//长度监听
        at_dwdm.addTextChangedListener(dwdm_watcher);//长度监听
        at_bprysfz.addTextChangedListener(sfz_watcher);//长度监听
    }

    private void test() {
        //旧编码
//        String res = "{\"cwxx\":\"0\",\"sqrq\":\"2022-05-11 17:36:16\",\"sbbhs\":[{\"sbbh\":\"F56A6800213\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"普格县辉隆聚鑫矿业01\",\"zbqyjd\":\"102.678632\",\"zbqywd\":\"27.319725\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格聚鑫矿业测\",\"zbqyjd\":\"102.679603\",\"zbqywd\":\"27.319692\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格县辉隆聚鑫矿业\",\"zbqyjd\":\"102.678327\",\"zbqywd\":\"27.319431\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"00000DB119124\",\"yxq\":\"2022-05-14 17:36:16\",\"gzm\":\"70107707\",\"gzmcwxx\":\"0\"}]}}";
        //新规则
//        String res = "{\"cwxx\":\"0\",\"sqrq\":\"2022-05-11 17:36:16\",\"sbbhs\":[{\"sbbh\":\"F56A6800213\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"普格县辉隆聚鑫矿业01\",\"zbqyjd\":\"102.678632\",\"zbqywd\":\"27.319725\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格聚鑫矿业测\",\"zbqyjd\":\"102.679603\",\"zbqywd\":\"27.319692\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格县辉隆聚鑫矿业\",\"zbqyjd\":\"102.678327\",\"zbqywd\":\"27.319431\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"5620418H70107\",\"yxq\":\"2022-05-14 17:36:16\",\"gzm\":\"FFA7666B05\",\"gzmcwxx\":\"0\"}]}}";
        //二代煤许模拟
//        String res2 ="{\"cwxx\":\"0\",\"sqrq\":\"2023-02-17 10:57:14\",\"sbbhs\":[{\"sbbh\":\"F56A6M22076\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"富兴煤矿\",\"zbqyjd\":\"108.348469\",\"zbqywd\":\"31.586952\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"5630206A02001\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDC1CF9C0921\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02002\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDC2CF9C0922\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02003\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDC3CF9C0923\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02004\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDC4CF9C0924\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02005\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDC5CF9C0925\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02006\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDC6CF9C0921\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02007\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDC7CF9C0922\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02008\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDC8CF9C0923\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02009\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDC9CF9C0924\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02010\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDA1CF9C0921\",\"gzmcwxx\":\"0\"},{\"uid\":\"5630206A02011\",\"yxq\":\"2023-02-20 10:57:14\",\"gzm\":\"FDA2CF9C0922\",\"gzmcwxx\":\"0\"}]}}";

        //二代模拟
        String res2 = "{\"cwxx\":\"0\",\"sqrq\":\"2022-05-11 17:36:16\",\"sbbhs\":[{\"sbbh\":\"F56A6800213\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"普格县辉隆聚鑫矿业01\",\"zbqyjd\":\"102.678632\",\"zbqywd\":\"27.319725\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格聚鑫矿业测\",\"zbqyjd\":\"102.679603\",\"zbqywd\":\"27.319692\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格县辉隆聚鑫矿业\",\"zbqyjd\":\"102.678327\",\"zbqywd\":\"27.319431\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"5620418H70101\",\"yxq\":\"2022-05-14 17:36:16\",\"gzm\":\"FFA7666B0121\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620418H70102\",\"yxq\":\"2022-05-14 17:36:16\",\"gzm\":\"FFA7666B0222\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620418H70103\",\"yxq\":\"2022-05-14 17:36:16\",\"gzm\":\"FFA7666B0323\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620418H70104\",\"yxq\":\"2022-05-14 17:36:16\",\"gzm\":\"FFA7666B0421\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620418H70105\",\"yxq\":\"2022-05-14 17:36:16\",\"gzm\":\"FFA7666B0521\",\"gzmcwxx\":\"0\"}]}}";
        //模拟下载150发雷管
//        String res2 = "{\"cwxx\":\"0\",\"sqrq\":\"2022-08-14 09:29:20\",\"sbbhs\":[{\"sbbh\":\"F560600002\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"德凤矿业\",\"zbqyjd\":\"104.802639\",\"zbqywd\":\"28.351421\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"5620705H84983\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA8BC04104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80098\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA3C1EF003\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84834\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB66FD6B04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84951\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB7877240421\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84839\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB8E7BA304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84833\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB76FF5404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80075\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0532474D0422\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80054\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"053CBBD70323\"," +
//                "\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84973\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA9C0CD50424\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84980\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB6CDD460425\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80029\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAC4BE990422\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84959\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB84EA170421\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80040\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82384AA30321\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80056\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04D128DD0321\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80077\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"058FC41B04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80059\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA440A3804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80047\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"050A593104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80019\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FABF1FF304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80052\",\"yxq\":\"2022-08-17 09:29:20\"" +
//                ",\"gzm\":\"0503180504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80069\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5A226204\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80018\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA4B2C3404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80024\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBDA92A604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84975\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB5D004204\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80071\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"052207D603\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84986\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB886A7D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80095\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0552FB4D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80009\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"049B0F0304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80013\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0560C8C603\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84970\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAA8ACB803\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80060\"" +
//                ",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA4B6CB804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80023\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBB5946604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80033\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBBAA55E04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84989\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA9173E203\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80088\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5C0B1704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80011\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"054C58AB03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84968\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB67384C04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84953\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBA4023F04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80084\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0520FF3304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80089\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"052C5B0004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84976\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAA6884704\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H84836\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB469E1304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80091\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0557E16704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80065\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04F02A8D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84999\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB978AF703\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80072\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"054C51DE03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80039\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBA45B4304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84837\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAB6DD7504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84957\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB54265104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80034\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"053371F903\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80006\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA3C464D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80086\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"052998B703\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80053\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04FB832404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80003\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04A0E62504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80037\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0512A79904\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80087\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA46625704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84988\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA8BA61E05\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80021\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0584060804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80022\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBB62B6104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84954\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB7FC45304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84994\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA84528804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84981\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA7C28B604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80051\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82E6C9EA03\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80058\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04DDE0FE03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80074\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBBA0CF503\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80093\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5BE64704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80092\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0522B46904\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80028\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"05288BC003\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84987\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB7B5F9C04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80038\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"051CF41D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80080\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBB77AB004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84991\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB9C653104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80000\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04FBDFB004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80079\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA6A05BC04\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80050\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA764D1504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84972\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB75B90D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84985\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB84940104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84969\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FACA8FFD03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80010\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA46AE6804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84997\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAE6110704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80004\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"050B1F8E04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80015\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA3C813304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80076\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0596973F04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84955\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB50718C03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80094\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0573C76704\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80081\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBA82E5304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80049\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04B08E2C04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80064\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBCE9E6404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80066\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"049EC12304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80002\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA56CFCB04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84974\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5D6AAC03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80073\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"053E48AA03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80097\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0567207804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80090\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FABB25AB04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80083\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"051E4D2B04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84995\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB55429904\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80085\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0573C2D403\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80041\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"051EE38404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84998\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB6CEF3D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80030\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAC1979404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84992\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB7E6F4404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80014\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA49666D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84990\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAF1080604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80061\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA46707A04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84982\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA63E82704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80048\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0556D37104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80063\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0484D18F04\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80017\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"055BD65504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80043\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"050BA3E503\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80099\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0525283004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80045\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82DB100504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84978\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5F260E04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80027\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBD2653704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84996\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FABF508404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80005\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0586007503\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80078\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"050EAA6804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80082\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA55537804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84958\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA8B037604\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80057\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82E66A7C04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80046\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"05415E4C04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80096\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA49D3F103\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80035\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"05796A7F04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80042\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82E6B97003\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80020\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"058332F703\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84838\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB811A9304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80031\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"053E45EE03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80012\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAB45C7D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80007\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5B0A0604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80044\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0509BAC903\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H84835\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB62A4ED04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80008\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04BE8B1704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84971\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA9C708604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80016\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBE2B6F203\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80026\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBA4538004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80062\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04F48AD503\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80070\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0498A9CA03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80036\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"054BFA6304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80025\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBCF0AF203\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84832\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB90393E04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84952\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB91728204\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H84977\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB55785704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80067\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBCDB03104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84979\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA7EEC5604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80068\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBD74B3F04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84956\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB5AB23504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84993\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB57E78904\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80032\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBB76C1A05\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80001\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA4A02B804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84984\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB88F86C04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80055\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0523C9A804\",\"gzmcwxx\":\"0\"}]}}";


        Gson gson = new Gson();
        DanLingBean danLingBean = gson.fromJson(res2, DanLingBean.class);
        Log.e("测试", "danLingBean: " + danLingBean);
        try {
            JSONObject object1 = new JSONObject(res2);
            String cwxx = null;

            cwxx = object1.getString("cwxx");

            if (cwxx.equals("0")) {
                int err = 0;
                for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                    if (!danLingBean.getLgs().getLg().get(i).getGzmcwxx().equals("0")) {
                        err++;
                    }
                }
                Log.e("下载的雷管", "错误数量: " + err);
                if (danLingBean.getCwxx().equals("0")) {
                    if (danLingBean.getZbqys().getZbqy().size() > 0) {
//                        double zbqyjd = Double.parseDouble(xy[0]);//116.456535
//                        double zbqywd = Double.parseDouble(xy[1]);//37.427541
                        for (int i = 0; i < danLingBean.getZbqys().getZbqy().size(); i++) {
                            double jingdu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqyjd());
                            double weidu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqywd());
                            double banjing = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqybj());
                            //判断经纬度
//                            LngLat start = new LngLat(zbqyjd, zbqywd);
//                            LngLat end = new LngLat(jingdu, weidu);
//                            double juli3 = AMapUtils.calculateLineDistance(start, end);
//                            Log.e("经纬度", "juli3: " + juli3);
//                            if (juli3 < banjing) {
                            insertJson(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res2, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
//                                        insertJson_new(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
//                            }
                        }
                    }
                }
                mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据

                if (danLingBean.getLgs().getLg().size() > 0) {
                    for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                        GreenDaoMaster.updateLgState(danLingBean.getLgs().getLg().get(i));
                    }
                }
//                mHandler_httpresult2.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据
                mHandler_1.sendMessage(mHandler_1.obtainMessage(0));//项目下载成功
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //模拟替换雷管
//        if (danLingBean.getLgs().getLg().size() > 0) {
//            for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
//                GreenDaoMaster.updateLgState(danLingBean.getLgs().getLg().get(i));
//            }
//        }
    }

    private void initView() {
        Resources resources = getContext().getResources();
        Drawable btnDrawable = resources.getDrawable(R.drawable.translucent);
        at_htid.setBackground(btnDrawable);
        at_xmbh.setBackground(btnDrawable);
        at_dwdm.setBackground(btnDrawable);
        at_coordxy.setBackground(btnDrawable);
        at_bprysfz.setBackground(btnDrawable);
    }

    private void initHandle() {
        mHandler_0 = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1001:
                    Log.e("liyi_1001", "更新视图 区域" + mRegion);
                    Log.e("liyi_1001", "更新视图 雷管数量: " + mListData.size());

                    // 查询全部雷管 倒叙(序号)
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    list_uid.clear();
                    for (int i = 0; i < mListData.size(); i++) {
                        list_uid.add(mListData.get(i).getShellBlastNo());
                    }
                    StringBuilder a = new StringBuilder();
                    if (mRegion1) {
                        a.append("1");
                    }
                    if (mRegion2) {
                        a.append(",2");
                    }
                    if (mRegion3) {
                        a.append(",3");
                    }
                    if (mRegion4) {
                        a.append(",4");
                    }
                    if (mRegion5) {
                        a.append(",5");
                    }

                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());
                    break;

                // 重新排序 更新视图
                case 1002:
                    // 雷管孔号排序 并 重新查询
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
//                    mAdapter.setListData(mListData, 1);
//                    mAdapter.notifyDataSetChanged();

                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());

//                    Log.e("liyi_1002", "更新视图 区域" + mRegion);
//                    Log.e("liyi_1002", "更新视图 雷管数量" + mListData.size());
                    break;
                default:
                    break;
            }

            return false;
        });

        mHandler_1 = new Handler(msg -> {
            switch (msg.what) {
                case 0:
                    show_Toast("项目下载成功");
                    break;
                case 1:
                case 99:
                    show_Toast(String.valueOf(msg.obj));
                    break;
                case 2:
                    show_Toast("未找到该起爆器设备信息或起爆器未设置作业任务");
                    break;
                case 3:
                    show_Toast("该起爆器未设置作业任务");
                    break;
                case 4:
                    show_Toast("起爆器在黑名单中");
                    break;
                case 5:
                    show_Toast("起爆位置不在起爆区域内");
                    break;
                case 6:
                    show_Toast("起爆位置在禁爆区域内");
                    break;
                case 7:
                    show_Toast("该起爆器已注销/报废");
                    break;
                case 8:
                    show_Toast("禁爆任务");
                    break;
                case 9:
                    show_Toast("作业合同存在项目");
                    break;
                case 10:
                    show_Toast("作业任务未设置准爆区域");
                    break;
                case 11:
                    show_Toast("离线下载不支持生产厂家试爆");
                    break;
                case 12:
                    show_Toast("营业性单位必须设置合同或者项目");
                    break;
                case 89:
                    show_Toast("输入的管壳码重复");
                    break;


            }
            return false;
        });
        mHandler_httpresult = new Handler(msg -> {
            loadMoreData_sq();//读取数据
            mAdapter_sq.notifyDataSetChanged();
            return false;
        });
        mHandler_httpresult2 = new Handler(msg -> {
            loadMoreData_duanErr();
            return false;
        });
        mHandler2 = new Handler(msg -> {
            //显示或隐藏loding界面
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });
        mHandler_3 = new Handler(msg -> {
            if (isCorrectReisterFea == 1) {
                SoundPlayUtils.play(3);
                show_Toast(getResources().getString(R.string.text_error_tip1));
                //"雷管信息有误，管厂码不正确，请检查"
            } else if (isCorrectReisterFea == 2) {
                SoundPlayUtils.play(3);
                show_Toast(getResources().getString(R.string.text_error_tip2));
            } else if (isCorrectReisterFea == 3) {
                SoundPlayUtils.play(3);
                show_Toast("已达到最大延时限制");
            } else if (isCorrectReisterFea == 4) {
                SoundPlayUtils.play(3);
                show_Toast("与第" + lg_No + "发" + singleShellNo + "重复");
            } else {
                SoundPlayUtils.play(3);
                show_Toast("注册失败");
            }
            isCorrectReisterFea = 0;
            return false;
        });
    }

    //获取配置文件中的值
    private void getPropertiesData() {
        pro_bprysfz = (String) MmkvUtils.getcode("pro_bprysfz", "");//证件号码
        pro_htid = (String) MmkvUtils.getcode("pro_htid", "");//合同号码
        pro_xmbh = (String) MmkvUtils.getcode("pro_xmbh", "");//项目编号
        pro_coordxy = (String) MmkvUtils.getcode("pro_coordxy", "");//经纬度
        pro_dwdm = (String) MmkvUtils.getcode("pro_dwdm", "");//单位代码
        equ_no = (String) MmkvUtils.getcode("equ_no", "");//设备编号

        at_bprysfz.setText(pro_bprysfz);
        at_htid.setText(pro_htid);
        at_xmbh.setText(pro_xmbh);
    }

    private void baidudingwei() {
        // -----------location config ------------
        locationService = ((Application) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK
    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }


    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                double a = 4.9E-324;
                if (location.getLatitude() != a) {
                    wd = location.getLatitude() + "";
                }
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                if (location.getLatitude() != a) {
                    jd = location.getLongitude() + "";
                }
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
//                Log.e("百度定位", "定位数据: "+sb.toString() );
            }
        }

    };


    /**
     * 扫码注册方法
     */
    private void scan() {
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");//初始化扫描服务

        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String data) {
                // scanInfo = data;
                if (data.length() == 19) {
                    Log.e("箱号", "getBarcode: " + data);
                    addXiangHao(data);//扫描箱号
                }
                if (sanButtonFlag > 0) {
                    scanDecode.stopScan();
                    decodeBar(data);
                } else {
                    if (continueScanFlag == 1) {
                        String barCode = getContinueScanBlastNo(data);
                        if (barCode == null) return;
                        if (checkRepeatShellNo(barCode) == 1) {
                            singleShellNo = barCode;
                            isCorrectReisterFea = 4;
                            mHandler_3.sendMessage(mHandler_3.obtainMessage());
                            return;
                        } else {
                            show_Toast(getResources().getString(R.string.text_error_tip10) + barCode);
                        }
                        SoundPlayUtils.play(1);
                        insertSingleDenator(barCode);
                    }
                }
            }
        });
    }

    /**
     * 停止扫码
     */
    private void stopScan() {
        continueScanFlag = 0;
        btnScanReister.setText(getResources().getString(R.string.text_reister_scanReister));//"扫码注册"
        btnSetdelay.setEnabled(true);
        scanDecode.stopScan();//停止扫描
        if (scanBarThread != null) {
            scanBarThread.exit = true;  // 终止线程thread
            try {
                scanBarThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /***
     * 单发注册
     */
    private int insertSingleDenator(String shellNo) {
        //管厂码
        String facCode = Utils.getDetonatorShellToFactoryCodeStr(shellNo);
        //特征码
        String facFea = Utils.getDetonatorShellToFeatureStr(shellNo);
        //雷管信息有误，管厂码不正确，请检查
        if (factoryCode != null && factoryCode.trim().length() > 0 && factoryCode.indexOf(facCode) < 0) {
            isCorrectReisterFea = 1;
            mHandler_3.sendMessage(mHandler_3.obtainMessage());
            return -1;
        }
        //雷管信息有误，特征码不正确，请检查
        if (factoryFeature != null && factoryFeature.trim().length() > 0 && factoryFeature.indexOf(facFea) < 0) {
            isCorrectReisterFea = 2;
            mHandler_3.sendMessage(mHandler_3.obtainMessage());
            return -1;
        }
        //检查重复数据
        if (checkRepeatShellNo(shellNo) == 1) {
            singleShellNo = "";
            singleShellNo = shellNo;
            mHandler_3.sendMessage(mHandler_3.obtainMessage());
            return -1;
        }
        int index = getEmptyDenator(-1);
        int maxNo = getMaxNumberNo();
        if (index < 0) {//说明没有空余的序号可用
            ContentValues values = new ContentValues();
            maxNo++;
            values.put("blastserial", maxNo);
            values.put("sithole", maxNo);
            values.put("shellBlastNo", shellNo);
            values.put("delay", "");
            values.put("regdate", Utils.getDateFormatLong(new Date()));
            values.put("statusCode", "02");
            values.put("statusName", "已注册");
            values.put("errorCode", "FF");
            values.put("errorName", "");
            values.put("wire", "");
            //向数据库插入数据
            db.insert("denatorBaseinfo", null, values);
            getLoaderManager().restartLoader(1, null, DownWorkCode.this);
        } else {

            ContentValues values = new ContentValues();
            values.put("shellBlastNo", shellNo);//key为字段名，value为值
            values.put("statusCode", "02");
            values.put("statusName", "已注册");
            values.put("errorCode", "FF");
            values.put("errorName", "");
            values.put("regdate", Utils.getDateFormatLong(new Date()));

            db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + index});

        }
        Utils.saveFile();//把软存中的数据存入磁盘中
//        Utils.saveFile_Message();//保存用户信息
        return 0;
    }

    //得到连续管壳码
    private String getContinueScanBlastNo(String strBarcode) {

        if (strBarcode.length() < 13) return null;
        if (strBarcode.trim().length() == 14) {
            strBarcode = strBarcode.substring(1);
            return strBarcode;
        } else if (strBarcode.trim().length() == 13) {
            //strBarcode= strBarcode;
            return strBarcode;
        }
        int index = strBarcode.indexOf("SC:");
        if (index < 0) return null;
        String subBarCode = strBarcode.substring(index + 3, index + 16);
        if (subBarCode.trim().length() < 13) {
            /*
            Toast.makeText(ReisterMainPage_scan.this, "不正确的编码，请扫描选择正确的编码",
                    Toast.LENGTH_SHORT).show();
                    **/
            return null;
        }
        return subBarCode;
    }

    //扫码方法
    private void decodeBar(String strParamBarcode) {
        String subBarCode = "";
        Log.e("扫码结果", "strParamBarcode: " + strParamBarcode);
        if (strParamBarcode.trim().length() >= 14) {
            int index = strParamBarcode.indexOf("SC:");
            subBarCode = strParamBarcode.substring(index + 3, index + 16);
            if (subBarCode.trim().length() < 13) {
                show_Toast("不正确的编码，请扫描选择正确的编码");
                return;
            }
        } else {
            if (strParamBarcode.trim().length() == 14) {
                subBarCode = strParamBarcode.substring(1);
            } else if (strParamBarcode.trim().length() == 13) {
                subBarCode = strParamBarcode;
            } else
                return;
        }
        String facCode = subBarCode.substring(0, 2);
        String dayCode = subBarCode.substring(2, 7);
        String featureCode = subBarCode.substring(7, 8);
        String serialNo = subBarCode.substring(8);
        Log.e("注册页面--扫码注册", "facCode: " + facCode + "  dayCode:" + dayCode + "  featureCode:" + featureCode + "  serialNo:" + serialNo);

        if (sanButtonFlag == 1) {
            edit_start_entBF2Bit_st.setText(facCode);
            edit_start_entproduceDate_st.setText(dayCode);//日期码
            edit_start_entAT1Bit_st.setText(featureCode);
            edit_start_entboxNoAndSerial_st.setText(serialNo);

            edit_end_entBF2Bit_en.setText("");
            edit_end_entproduceDate_ed.setText("");
            edit_end_entAT1Bit_ed.setText("");
            edit_end_entboxNoAndSerial_ed.setText("");
            btnScanReister.setEnabled(true);
        }
        if (sanButtonFlag == 2) {
            edit_end_entBF2Bit_en.setText(facCode);
            edit_end_entproduceDate_ed.setText(dayCode);
            edit_end_entAT1Bit_ed.setText(featureCode);
            edit_end_entboxNoAndSerial_ed.clearFocus();
            edit_end_entboxNoAndSerial_ed.setText(serialNo);
            btnScanReister.setEnabled(true);
        }
        sanButtonFlag = 0;
    }

    /**
     * 扫描箱号
     */
    private void addXiangHao(String data) {
        char[] xh = data.toCharArray();
        char[] strNo1 = {xh[1], xh[2], xh[9], xh[10], xh[11], xh[12], xh[13], xh[14]};//箱号数组
        final String strNo = "00";
        String a = xh[5] + "" + xh[6];
        String endNo = Utils.XiangHao(a);
        final String prex = String.valueOf(strNo1);
        final int finalEndNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + xh[17] + endNo);
        final int finalStrNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + xh[17] + strNo);
        new Thread(() -> {
            insertDenator(prex, finalStrNo, finalEndNo);//添加
        }).start();
    }

//    private void initAutoComplete(String field, AutoCompleteTextView auto) {
//        SharedPreferences sp = getSharedPreferences("network_url", 0);
////        MMKV kv = MMKV.mmkvWithID("network_url");
////        kv.importFromSharedPreferences(sp);
//        String longhistory = sp.getString(field, "当前无记录");
//        String[] hisArrays = longhistory.split("#");
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_auto_textview, hisArrays);
//        auto.setAdapter(adapter);
//        auto.setDropDownHeight(500);
//        auto.setDropDownWidth(450);
//        auto.setThreshold(1);
//        auto.setCompletionHint("最近的20条记录");
//        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                AutoCompleteTextView view = (AutoCompleteTextView) v;
//                if (hasFocus) {
//                    view.showDropDown();
//                }
//            }
//        });
//    }

//    private void saveHistory(String field, AutoCompleteTextView auto) {
//        String text = auto.getText().toString();
//        Log.e("保存输入框历史", "text: " + text);//MMKV保存为空,有空再排错
////        MMKV mmkv_his = MMKV.mmkvWithID("network_url");
//        SharedPreferences sp = getSharedPreferences("network_url", 0);
//        String longhistory = sp.getString(field, "");
//        if (!longhistory.contains(text + "#")) {
//            StringBuilder sb = new StringBuilder(longhistory);
//            sb.insert(0, text + "#");
//            sp.edit().putString(field, sb.toString()).apply();
//        }
//    }

    //获取用户信息
    private void getUserMessage() {
        String selection = "id = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"1"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_MESSQGE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {  //cursor不位空,可以移动到第一行
            pro_bprysfz = cursor.getString(1);
            pro_htid = cursor.getString(2);
            pro_xmbh = cursor.getString(3);
            equ_no = cursor.getString(4);
            pro_coordxy = cursor.getString(5);
            pro_dwdm = cursor.getString(15);
            cursor.close();
        }
        at_bprysfz.setText(pro_bprysfz);
        at_htid.setText(pro_htid);
        at_xmbh.setText(pro_xmbh);
        at_dwdm.setText(pro_dwdm);
        if (pro_coordxy.equals("")) {
            baidudingwei();
        } else {
            at_coordxy.setText(pro_coordxy);
        }

    }


    private void upload() {
        pb_show = 1;
        runPbDialog();//loading画面
        final String key = "jadl12345678912345678912";
        String url = Utils.httpurl_down_dl;//丹灵下载
        OkHttpClient client = new OkHttpClient();

        JSONObject object = new JSONObject();
        String sfz = at_bprysfz.getText().toString().trim().replace(" ", "");//证件号码
        String tx_htid = at_htid.getText().toString().trim().replace(" ", "");//合同编号 15位
        String tv_xmbh = at_xmbh.getText().toString().trim().replace(" ", "");//项目编号
        final String[] xy = at_coordxy.getText().toString().replace("\n", "").replace("，", ",").replace(" ", "").split(",");//经纬度
        String tv_dwdm = at_dwdm.getText().toString().trim();//单位代码 13位

        //四川转换规则
//        if (list_uid != null && list_uid.get(0).length() < 14) {
//            for (int i = 0; i < list_uid.size(); i++) {
//                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan(list_uid.get(i)));//替换
////                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan_new(list_uid.get(i)));//替换
//            }
//        }
        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e("uid", uid);
        try {
            object.put("sbbh", equ_no);//起爆器设备编号XBTS0003
            object.put("jd", xy[0]);//经度
            object.put("wd", xy[1]);//纬度
            object.put("uid", uid);//雷管uid
            object.put("xmbh", tv_xmbh);//项目编号370101318060006
            object.put("htid", tx_htid);//合同编号370100X15040027
            object.put("dwdm", tv_dwdm);//单位代码
            Log.e("上传信息", object.toString());
            Utils.writeRecord("---上传丹灵信息:" + object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = MyUtils.getBase64(MyUtils.encryptMode(key.getBytes(), object.toString().getBytes()));
        RequestBody requestBody = new FormBody.Builder()
                .add("param", json.replace("\n", ""))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "text/plain")//text/plain  application/json  application/x-www-form-urlencoded
                .build();
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                show_Toast_ui("网络请求失败,请检查网络后再次尝试");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res;
                try {
                    res = new String(MyUtils.decryptMode(key.getBytes(), Base64.decode(response.body().string().toString(), Base64.DEFAULT)));
                } catch (Exception e) {
                    show_Toast_ui("丹灵系统异常，请与丹灵管理员联系后再尝试下载");
                    return;
                }
                Log.e("网络请求", "res: " + res);
                Utils.writeRecord("---丹灵网返回:" + res);
                Gson gson = new Gson();
                DanLingBean danLingBean = gson.fromJson(res, DanLingBean.class);
                try {
                    JSONObject object1 = new JSONObject(res);
                    String cwxx = object1.getString("cwxx");
                    if (cwxx.equals("0")) {
                        int err = 0;
                        for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                            if (!danLingBean.getLgs().getLg().get(i).getGzmcwxx().equals("0")) {
                                err++;
                            }
                        }
                        Log.e("下载的雷管", "错误数量: " + err);
                        if (danLingBean.getCwxx().equals("0")) {
                            if (danLingBean.getZbqys().getZbqy().size() > 0) {
                                double zbqyjd = Double.parseDouble(xy[0]);//116.456535
                                double zbqywd = Double.parseDouble(xy[1]);//37.427541
                                for (int i = 0; i < danLingBean.getZbqys().getZbqy().size(); i++) {
                                    double jingdu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqyjd());
                                    double weidu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqywd());
                                    double banjing = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqybj());
                                    //判断经纬度
                                    LngLat start = new LngLat(zbqyjd, zbqywd);
                                    LngLat end = new LngLat(jingdu, weidu);
                                    double juli3 = AMapUtils.calculateLineDistance(start, end);
                                    Log.e("经纬度", "juli3: " + juli3);
                                    if (juli3 < banjing) {
                                        insertJson(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
//                                        insertJson_new(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
                                    }
                                }
                            }
                        }
                        mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据

                        if (danLingBean.getLgs().getLg().size() > 0) {
                            for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                                GreenDaoMaster.updateLgState(danLingBean.getLgs().getLg().get(i));
                            }

                        }

                        if (err != 0) {
                            Log.e("下载", "err: " + err);
//                            show_Toast_ui(danLingBean.getZbqys().getZbqy().get(0).getZbqymc() + "下载的雷管出现错误,请检查数据");
                        }
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(0));//"项目下载成功"
                        pb_show = 0;//loding画面结束
                    } else if (cwxx.equals("1")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(1, object1.getString("cwxxms")));
                    } else if (cwxx.equals("2")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(2));
                    } else if (cwxx.equals("3")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(3));//该起爆器未设置作业任务
                    } else if (cwxx.equals("4")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(4));//起爆器在黑名单中
                    } else if (cwxx.equals("5")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(5));//起爆位置不在起爆区域内
                    } else if (cwxx.equals("6")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(6));//起爆位置在禁爆区域内
                    } else if (cwxx.equals("7")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(7));//该起爆器已注销/报废
                    } else if (cwxx.equals("8")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(8));//禁爆任务
                    } else if (cwxx.equals("9")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(9));//作业合同存在项目
                    } else if (cwxx.equals("10")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(10));//作业任务未设置准爆区域
                    } else if (cwxx.equals("11")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(11));//离线下载不支持生产厂家试爆
                    } else if (cwxx.equals("12")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(12));//营业性单位必须设置合同或者项目
                    } else if (cwxx.equals("99")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(99, danLingBean.getCwxxms()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pb_show = 0;//loding画面结束
            }
        });
    }

    private void upload_xingbang() {
        show_Toast("当前为煋邦测试下载");
        pb_show = 1;
        runPbDialog();//loading画面
        String url = Utils.httpurl_xb_upload;//煋邦下载
        OkHttpClient client = new OkHttpClient();

        String sfz = at_bprysfz.getText().toString().trim().replace(" ", "");//证件号码
        String tx_htid = at_htid.getText().toString().trim().replace(" ", "");//合同编号 15位
        String tv_xmbh = at_xmbh.getText().toString().trim().replace(" ", "");//项目编号
        final String[] xy = at_coordxy.getText().toString().replace("\n", "").replace("，", ",").replace(" ", "").split(",");//经纬度
        String tv_dwdm = at_dwdm.getText().toString().trim();//单位代码 13位

        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e("uid", uid);

        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("sbbh", equ_no);//起爆器设备编号XBTS0003
            json.put("jd", xy[0]);//经度
            json.put("wd", xy[1]);//纬度
            json.put("uid", uid);//雷管uid
            json.put("xmbh", tv_xmbh);//项目编号370101318060006
            json.put("htid", tx_htid);//合同编号370100X15040027
            json.put("dwdm", tv_dwdm);//单位代码
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")//text/plain  application/json  application/x-www-form-urlencoded
                .build();
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                show_Toast_ui("网络请求失败,请检查网络后再次尝试");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res ;
                try {
                    res = response.body().string();//response.body()只能调用一次,第二次调用就会变成null
                } catch (Exception e) {
                    show_Toast_ui("煋邦网络异常，请与煋邦管理员联系后再尝试下载");
                    return;
                }
                Log.e("网络请求返回", "res: " + res);
                Utils.writeRecord("---煋邦返回:" + res);
                Gson gson = new Gson();
                DanLingBean danLingBean = gson.fromJson(res, DanLingBean.class);
                try {
                    JSONObject object1 = new JSONObject(res);
                    String cwxx = object1.getString("cwxx");
                    if (cwxx.equals("0")) {
                        int err = 0;
                        for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                            if (!danLingBean.getLgs().getLg().get(i).getGzmcwxx().equals("0")) {
                                err++;
                            }
                        }
                        Log.e("下载的雷管", "错误数量: " + err);
                        if (danLingBean.getCwxx().equals("0")) {
                            if (danLingBean.getZbqys().getZbqy().size() > 0) {
                                double zbqyjd = Double.parseDouble(xy[0]);//116.456535
                                double zbqywd = Double.parseDouble(xy[1]);//37.427541
                                for (int i = 0; i < danLingBean.getZbqys().getZbqy().size(); i++) {
                                    double jingdu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqyjd());
                                    double weidu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqywd());
//                                    double banjing = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqybj());
                                    double banjing = 5000;
                                    //判断经纬度
                                    LngLat start = new LngLat(zbqyjd, zbqywd);
                                    LngLat end = new LngLat(jingdu, weidu);
                                    double juli3 = AMapUtils.calculateLineDistance(start, end);
                                    Log.e("经纬度", "juli3: " + juli3);
                                    if (juli3 < banjing) {
                                        insertJson(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
//                                        insertJson_new(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
                                    }
                                }
                            }
                        }
                        mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据

                        if (danLingBean.getLgs().getLg().size() > 0) {
                            for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                                GreenDaoMaster.updateLgState(danLingBean.getLgs().getLg().get(i));
                            }
                        }

                        if (err != 0) {
                            Log.e("下载", "err: " + err);
//                            show_Toast_ui(danLingBean.getZbqys().getZbqy().get(0).getZbqymc() + "下载的雷管出现错误,请检查数据");
                        }
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(0));//"项目下载成功"
                        pb_show = 0;//loding画面结束
                    } else if (cwxx.equals("1")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(1, object1.getString("cwxxms")));
                    } else if (cwxx.equals("2")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(2));
                    } else if (cwxx.equals("3")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(3));//该起爆器未设置作业任务
                    } else if (cwxx.equals("4")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(4));//起爆器在黑名单中
                    } else if (cwxx.equals("5")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(5));//起爆位置不在起爆区域内
                    } else if (cwxx.equals("6")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(6));//起爆位置在禁爆区域内
                    } else if (cwxx.equals("7")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(7));//该起爆器已注销/报废
                    } else if (cwxx.equals("8")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(8));//禁爆任务
                    } else if (cwxx.equals("9")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(9));//作业合同存在项目
                    } else if (cwxx.equals("10")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(10));//作业任务未设置准爆区域
                    } else if (cwxx.equals("11")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(11));//离线下载不支持生产厂家试爆
                    } else if (cwxx.equals("12")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(12));//营业性单位必须设置合同或者项目
                    } else if (cwxx.equals("99")) {
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(99, danLingBean.getCwxxms()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pb_show = 0;//loding画面结束
            }
        });
    }


    /***
     * 发送初始化命令
     */
    @Override
    protected void onStart() {
        hideInputKeyboard();
        btnDownReturn.setFocusable(true);
        btnDownReturn.setFocusableInTouchMode(true);
        btnDownReturn.requestFocus();
        btnDownReturn.findFocus();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initAutoComplete("history_htid", at_htid);//输入历史记录
        initAutoComplete("history_xmbh", at_xmbh);
        initAutoComplete("history_dwdm", at_dwdm);
        initAutoComplete("history_bprysfz", at_bprysfz);
        initAutoComplete("history_coordxy", at_coordxy);
        initAutoComplete("history_projectName", at_projectName);//项目名称

        mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mListener != null) {
            locationService.unregisterListener(mListener); //注销掉监听
            locationService.stop(); //停止定位服务
        }
        if (tipDlg != null) {
            tipDlg.dismiss();
            tipDlg = null;
        }
        if (db != null) db.close();
//        Utils.saveFile();//把软存中的数据存入磁盘中
        scanDecode.stopScan();//停止扫描
        if (scanBarThread != null) {
            scanBarThread.exit = true;  // 终止线程thread
            try {
                scanBarThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        scanDecode.onDestroy();//回复初始状态
        super.onDestroy();
        fixInputMethodManagerLeak(this);
    }

    //隐藏键盘
    public void hideInputKeyboard() {

        at_bprysfz.clearFocus();//取消焦点
        at_htid.clearFocus();
        at_xmbh.clearFocus();
        at_coordxy.clearFocus();
        at_dwdm.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public void displayInputKeyboard(View v, boolean hasFocus) {
        //获取系统 IMM
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!hasFocus) {
            //隐藏 软键盘  
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } else {
            //显示 软键盘  
            imm.showSoftInput(v, 0);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        args = new Bundle();
        // TODO Auto-generated method stub
        args.putString("key", "1");
        MyLoad myLoad = new MyLoad(DownWorkCode.this, args);
        return myLoad;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        list_adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        list_adapter.changeCursor(null);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { //返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /****
     * 校验数据
     */
    private String checkData() {
        String tipStr = "";
        String startNo = at_bprysfz.getText().toString();
        String htNo = at_htid.getText().toString();
        String holeDeAmo = at_xmbh.getText().toString();
        String coordxy = at_coordxy.getText().toString().replace("\n", "").replace("，", ",").replace(" ", "");
        String []a =coordxy.split("\\.");
        if (coordxy == null || coordxy.trim().length() < 8 || coordxy.indexOf(",") < 5||a.length!=3) {
            tipStr = "经度纬度设置不正确，具体格式为如:116.585989,36.663456";
            return tipStr;
        }
        String xy[] = coordxy.split(",");

//        if (xy == null || xy.length != 2) {
//            tipStr = "经度纬度长度不正确，具体格式为如:116.585989,36.663456";
//            return tipStr;
//        }
//        if (xy[0].indexOf(".") < 2 || xy[1].indexOf(".") != 2) {
//            tipStr = "经度纬度标点不正确，具体格式为如:116.585989,36.663456";
//            return tipStr;
//        }
        return tipStr;
    }


    /***
     * 建立对话框
     */
    public void createHelpDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_down_tip, null);
        final EditText jd_5 = (EditText) view.findViewById(R.id.jd_5);
        final EditText jd_4 = (EditText) view.findViewById(R.id.jd_4);
        final EditText wd_5 = (EditText) view.findViewById(R.id.wd_5);
        final EditText wd_4 = (EditText) view.findViewById(R.id.wd_4);
        jd_5.setText(jd.substring(0, 6));
        jd_4.setText(jd.substring(6));
        wd_5.setText(wd.substring(0, 6));
        wd_4.setText(wd.substring(6));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("经纬度说明");//"说明"
        builder.setView(view);
        builder.setPositiveButton("确定", (dialog, which) -> {
            at_coordxy.setText(jd_5.getText().toString() + jd_4.getText().toString() + "," + wd_5.getText().toString() + wd_4.getText().toString() + "");
            dialog.dismiss();
        });
        builder.create().show();
    }

    /**
     * 保存信息
     */
    private void saveData() {
        MmkvUtils.savecode("pro_name", at_projectName.getText().toString());

        saveHistory("history_xmbh", at_xmbh);//保存输入的项目编号
        saveHistory("history_htid", at_htid);//保存输入的合同编号
        saveHistory("history_bprysfz", at_bprysfz);//保存输入的身份证号
        saveHistory("history_coordxy", at_coordxy);//保存输入的经纬度
        saveHistory("history_dwdm", at_dwdm);//保存输入的经纬度


        initAutoComplete("history_htid", at_htid);  
        initAutoComplete("history_xmbh", at_xmbh);
        initAutoComplete("history_bprysfz", at_bprysfz);
        initAutoComplete("history_coordxy", at_coordxy);
        initAutoComplete("history_dwdm", at_dwdm);
        String checstr = checkData();
        if (checstr == null || checstr.trim().length() < 1) {
            String a = at_bprysfz.getText().toString().trim().replace(" ", "");
            String b = at_htid.getText().toString().trim().replace(" ", "");
            String c = at_xmbh.getText().toString().trim().replace(" ", "");
            String d = at_coordxy.getText().toString().trim().replace("\n", "").replace("，", ",").replace(" ", "");
            String e = at_dwdm.getText().toString().trim().replace(" ", "");
            ContentValues values = new ContentValues();
            if (at_xmbh.getText().toString().trim().length() < 1) {
                values.put("pro_xmbh", "");
            } else {
                values.put("pro_xmbh", c);
            }
            if (at_dwdm.getText().toString().trim().length() < 1) {
                values.put("pro_dwdm", "");
            } else {
                values.put("pro_dwdm", e);
            }
            values.put("pro_bprysfz", a);
            values.put("pro_htid", b);
            values.put("pro_coordxy", d);
            db.update(DatabaseHelper.TABLE_NAME_USER_MESSQGE, values, "id = ?", new String[]{"1"});

            Utils.saveFile_Message();//保存用户信息
        } else {
            show_Toast(checstr);
        }
    }


    /**
     * 获取厂家码
     */
    private void getFactoryCode() {

        String selection = "isSelected = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"是"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DEFACTORY, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String code = cursor.getString(2);
            String feature = cursor.getString(3);
            factoryCode = code;
            factoryFeature = feature;
            cursor.close();
        }

    }

    /***
     * 生成序列号
     */
    private int insertDenator(String prex, int start, int end) {
        if (end < start) return -1;
        if (start < 0 || end > 99999) return -1;
        String shellNo = "";
//        int maxNo = getMaxNumberNo();
        int maxNo = new GreenDaoMaster().getPieceMaxNum(mRegion);//获取该区域最大序号
        int delay = new GreenDaoMaster().getPieceMaxNumDelay(mRegion);//获取该区域 最大序号的延时
        int flag = 0;
        ContentValues values = new ContentValues();
        int reCount = 0;
        for (int i = start; i <= end; i++) {
            shellNo = prex + String.format("%05d", i);
            if (checkRepeatShellNo(shellNo) == 1) {
                tipInfoFlag = 89;
                flag = 1;
                break;
            }
            DetonatorTypeNew detonatorTypeNew = new GreenDaoMaster().serchDenatorId(shellNo);

            maxNo++;
            DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
            denatorBaseinfo.setBlastserial(maxNo);
            denatorBaseinfo.setSithole(maxNo + "");
            denatorBaseinfo.setShellBlastNo(shellNo);
            denatorBaseinfo.setDelay(delay);
            denatorBaseinfo.setRegdate(Utils.getDateFormatLong(new Date()));
            denatorBaseinfo.setStatusCode("02");
            denatorBaseinfo.setStatusName("已注册");
            denatorBaseinfo.setErrorCode("FF");
            denatorBaseinfo.setErrorName("");
            denatorBaseinfo.setWire("");//桥丝状态
            denatorBaseinfo.setPiece(mRegion);
            if (detonatorTypeNew != null && !detonatorTypeNew.getDetonatorId().equals("0")) {
                denatorBaseinfo.setDenatorId(detonatorTypeNew.getDetonatorId());
                denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
            }
            //向数据库插入数据
            getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
            Utils.saveFile();//把软存中的数据存入磁盘中
            reCount++;
        }
        pb_show = 0;
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        return reCount;
    }

    private int getEmptyDenator(int start) {

        String selection = "shellBlastNo = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, selection, selectionArgs, null, null, null);
        int serialNo = -1;
        if (cursor != null) {  //cursor不位空,可以移动到第一行

            while (cursor.moveToNext()) {

                serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                if (start < 0) {
                    break;
                } else {
                    if (serialNo < start) {
                        serialNo = -1;
                        continue;
                    } else {
                        break;
                    }
                }
            }
            cursor.close();
        }
        return serialNo;
    }

    /***
     * 得到最大序号
     * @return
     */
    private int getMaxNumberNo() {
        Cursor cursor = db.rawQuery("select max(blastserial) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null);
        if (cursor != null && cursor.moveToNext()) {
            int maxNo = cursor.getInt(0);
            cursor.close();
            return maxNo;
        }
        return 1;
    }

    /**
     * 检查重复的数据
     *
     * @param shellBlastNo
     * @return
     */
    public int checkRepeatShellNo(String shellBlastNo) {
        String selection = "shellBlastNo = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {shellBlastNo + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            boolean flag = cursor.moveToFirst();
            cursor.close();
            if (flag)
                return 1;
            else
                return 0;
        } else {
            //if(cursor != null)cursor.close();
            return 0;
        }
    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
        //tipDlg.setMessage("正在操作,请等待...").show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                mHandler2.sendMessage(mHandler2.obtainMessage());
                try {
                    while (pb_show == 1) {
                        Thread.sleep(100);
                    }
                    mHandler2.sendMessage(mHandler2.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /****
     * 校验数据
     */
    private String checkInputData() {
        String tipStr = "";
        //开始序号
        String st2Bit = edit_start_entBF2Bit_st.getText().toString();
        String stproDt = edit_start_entproduceDate_st.getText().toString();
        String st1Bit = edit_start_entAT1Bit_st.getText().toString();
        String stsno = edit_start_entboxNoAndSerial_st.getText().toString();
        //结束序号
        String ed2Bit = edit_end_entBF2Bit_en.getText().toString();
        String edproDt = edit_end_entproduceDate_ed.getText().toString();
        String ed1Bit = edit_end_entAT1Bit_ed.getText().toString();
        String edsno = edit_end_entboxNoAndSerial_ed.getText().toString();

        if (!StringUtils.isNotBlank(st2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip11);//"起始厂家码不能为空"
            return tipStr;
        }
        if (!StringUtils.isNotBlank(stproDt)) {
            tipStr = getResources().getString(R.string.text_error_tip12);//起始生产日期不能为空
            return tipStr;
        }
        if (!StringUtils.isNotBlank(st1Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip13);// "起始特征码不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(stsno)) {
            tipStr = getResources().getString(R.string.text_error_tip14); //"起始序号不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(ed2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip15);// "结束厂家码不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(edproDt)) {
            tipStr = getResources().getString(R.string.text_error_tip16);//  "结束生产日期不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(ed1Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip17);//  "结束特征码不能为空";
            return tipStr;
        }
        if (!StringUtils.isNotBlank(edsno)) {
            tipStr = getResources().getString(R.string.text_error_tip18);//  "结束序列号不能为空";
            return tipStr;
        }
        if (!st2Bit.equals(ed2Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip19);//  "管厂码不一致";
            return tipStr;
        }
        if (factoryCode != null && factoryCode.trim().length() > 0 && factoryCode.indexOf(st2Bit) < 0) {
            tipStr = getResources().getString(R.string.text_error_tip21);//  "管厂码与系统中定义的管厂码不一致";
            return tipStr;
        }

        if (!stproDt.equals(edproDt)) {
            tipStr = getResources().getString(R.string.text_error_tip22);// "日期不一致";
            return tipStr;
        }
        if (!st1Bit.equals(ed1Bit)) {
            tipStr = getResources().getString(R.string.text_error_tip23);//  "特征码不一致";
            return tipStr;
        }
        if (factoryFeature != null && factoryFeature.trim().length() > 0 && factoryFeature.indexOf(st1Bit) < 0) {
            tipStr = getResources().getString(R.string.text_error_tip24);//  "特征码与系统中定义的特征码不一致";
            return tipStr;
        }
        if (!Utils.isNum(stsno)) {
            tipStr = getResources().getString(R.string.text_error_tip25);//  "开始序号不是数字";
            return tipStr;
        }
        if (!Utils.isNum(edsno)) {
            tipStr = getResources().getString(R.string.text_error_tip26);//  "结束序号不是数字";
            return tipStr;
        }
        int start = Integer.parseInt(stsno);
        int end = Integer.parseInt(edsno);
        if (end < start) {
            tipStr = getResources().getString(R.string.text_error_tip27);//  "结束序号不能小于开始序号";
        }
        if (start < 0 || end > 99999) {
            tipStr = getResources().getString(R.string.text_error_tip28);//  "起始/结束序号不符合要求";
        }
        if ((end - start) > 1000)
            tipStr = getResources().getString(R.string.text_error_tip29);//  "每一次注册数量不能大于1000";
        return tipStr;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("点击项目", "position: " + position);
        Intent intent = new Intent(DownWorkCode.this, ShouQuanLegActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("list_dl", (Serializable) map_dl);
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void itemClick(View v) {
        int position = (int) v.getTag();
        switch (v.getId()) {
            case R.id.btn_del_sq://删除按钮
                delShouQuan(map_dl.get(position).get("id").toString());//删除方法
                if (map_dl != null && map_dl.size() > 0) {//移除map中的值
                    map_dl.remove(position);
                }
                mAdapter_sq.notifyDataSetChanged();
                break;
            case R.id.ly_sq://
            case R.id.tv_chakan_sq:
                Log.e("点击项目", "position: " + map_dl.get(position));
                Intent intent = new Intent(DownWorkCode.this, ShouQuanLegActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list_dl", (Serializable) map_dl);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 向数据库中插入数据
     */
    public void insertJson(String htbh, String xmbh, String json, int errNum, String coordxy, String name) {
        ContentValues values = new ContentValues();
        values.put("htbh", htbh);
        values.put("xmbh", xmbh);
        values.put("json", json);
        values.put("errNum", errNum);
        values.put("qbzt", "未爆破");
        values.put("dl_state", "未上传");
        values.put("zb_state", "未上传");
        values.put("spare1", name);
        values.put("bprysfz", at_bprysfz.getText().toString().trim());//身份证号
        values.put("coordxy", coordxy.replace("\n", "").replace("，", ",").replace(" ", ""));//经纬度
        if (at_dwdm.getText().toString().trim().length() < 1) {//单位代码
            values.put("dwdm", "");
        } else {
            values.put("dwdm", at_dwdm.getText().toString().trim());
        }

        Log.e("插入数据", "成功");
        db.insert(DatabaseHelper.TABLE_NAME_SHOUQUAN, null, values);
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 向数据库中插入数据
     */
    public void insertJson_new(String htbh, String xmbh, String json, int errNum, String coordxy, String name) {
        ShouQuan sq = new ShouQuan();
        sq.setErrNum(String.valueOf(errNum));
        sq.setXmbh(xmbh);
        sq.setHtbh(htbh);
        sq.setJson(json);
        sq.setQbzt("未爆破");
        sq.setDl_state("未上传");
        sq.setZb_state("未上传");
        sq.setSpare1(name);
        sq.setBprysfz(at_bprysfz.getText().toString().trim());
        sq.setCoordxy(coordxy.replace("\n", "").replace("，", ",").replace(" ", ""));
        if (at_dwdm.getText().toString().trim().length() < 1) {//单位代码
            sq.setDwdm("");
        } else {
            sq.setDwdm(at_dwdm.getText().toString().trim());
        }
        sq.save();
        Log.e("插入数据", "成功");
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 查询所有雷管
     */
    private void loadMoreData_duanErr() {
        String sql = "Select * from denatorBaseinfo where duan != cong_yscs" ;
        Cursor cursor = db.rawQuery(sql, null);//new String[]{(index) + "", pageSize + ""}
        list_data.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                String shellNo = cursor.getString(3);//管壳号
                String cong_yscs = cursor.getString(17);//密码中的段
                String duan = cursor.getString(19);//段
                String duanNo = cursor.getString(20);//段

                Map<String, Object> item = new HashMap<>();
                item.put("serialNo",serialNo);
                item.put("shellNo", shellNo);
                item.put("cong_yscs", cong_yscs);
                item.put("duan", duan);
                item.put("duanNo", duanNo);
                list_data.add(item);
            }
            cursor.close();
        }
        if (list_data != null) {
            createDialog();
        }
        Log.e("雷管", "list_uid: "+list_data.size()+"==" + list_data);

    }
    /***
     * 建立错误对话框
     */
    public void createDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View getlistview = inflater.inflate(R.layout.firing_errorduan_listview, null);

        // 给ListView绑定内容
        ListView errlistview = getlistview.findViewById(R.id.X_listview);
//        SimpleAdapter adapter = new SimpleAdapter(this, errDeData, R.layout.firing_error_item,
//                new String[]{"serialNo", "shellNo", "errorName", "delay"},
//                new int[]{R.id.X_item_no, R.id.X_item_shellno, R.id.X_item_errorname, R.id.X_item_delay});
//        // 给listview加入适配器
//        errlistview.setAdapter(adapter);
        ErrDuanAdapter mAdapter = new ErrDuanAdapter(this, list_data, R.layout.firing_errorduan_item);
        errlistview.setAdapter(mAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_alert_tablename1));//"错误雷管列表"
        builder.setView(getlistview);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    /**
     * 读取详细历史信息
     */
    public ArrayList loadHisProject(String name) {//
        List<Project> list = LitePal.select(name).find(Project.class);
        ArrayList<String> listPro = new ArrayList<String>();
        for (Project pro : list) {
            switch (name) {
                case "htbh":
                    if (pro.getHtbh().length() > 0) {
                        listPro.add(pro.getHtbh());
                    }
                    break;
                case "xmbh":
                    if (pro.getXmbh().length() > 0) {
                        listPro.add(pro.getXmbh());
                    }
                    break;
                case "dwdm":
                    if (pro.getDwdm().length() > 0) {
                        listPro.add(pro.getDwdm());
                    }
                    break;
                case "bprysfz":
                    if (pro.getBprysfz().length() > 0) {
                        listPro.add(pro.getBprysfz());
                    }
                    break;
                case "coordxy":
                    if (pro.getCoordxy().length() > 0) {
                        listPro.add(pro.getCoordxy());
                    }
                    break;
                case "project_name":
                    if (pro.getProject_name().length() > 0) {
                        listPro.add(pro.getProject_name());
                    }
                    break;
            }

        }
        return listPro;
    }

    private void loadMoreData_sq() {
        map_dl.clear();
        List<ShouQuan> list = GreenDaoMaster.getAllShouQuan();
//        Log.e("查询", "list : " + list.toString());
        Gson gson = new Gson();
        DanLingBean danLingBean;
        for (ShouQuan sq : list) {
            danLingBean = gson.fromJson(sq.getJson(), DanLingBean.class);
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", sq.getId());
            item.put("htbh", sq.getHtbh());
            item.put("xmbh", sq.getXmbh());
            item.put("qbzt", sq.getQbzt());
            item.put("errNum", sq.getErrNum());
            item.put("coordxy", sq.getCoordxy());
            item.put("spare1", sq.getSpare1());
            item.put("danLingBean", danLingBean);
            map_dl.add(item);
        }

//        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_SHOUQUAN;//+" order by htbh "
//        Cursor cursor = db.rawQuery(sql, null);
//        //return getCursorTolist(cursor);
//        if (cursor != null) {
//            Gson gson = new Gson();
//            DanLingBean danLingBean;
//            while (cursor.moveToNext()) {
//                String id = cursor.getString(0);
//                String xmbh = cursor.getString(1); //获取第二列的值 ,序号
//                String htbh = cursor.getString(2);
//                String json = cursor.getString(3);//管壳号
//                String errNum = cursor.getString(4);//错误数量
//                String qbzt = cursor.getString(5);//起爆状态
//                String coordxy = cursor.getString(11);//经纬度
//                String spare1 = cursor.getString(13);//工程名称
//                danLingBean = gson.fromJson(json, DanLingBean.class);
//                Map<String, Object> item = new HashMap<String, Object>();
//                item.put("id", id);
//                item.put("htbh", htbh);
//                item.put("xmbh", xmbh);
//                item.put("qbzt", qbzt);
//                item.put("spare1", spare1);
//                item.put("coordxy", coordxy);
//                item.put("errNum", errNum);
//                item.put("danLingBean", danLingBean);
//                map_dl.add(item);
//            }
//            cursor.close();
//        }
    }

    private int delShouQuan(String id) {//删除雷管
        String selection = "id = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {id + ""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        db.delete(DatabaseHelper.TABLE_NAME_SHOUQUAN, selection, selectionArgs);
        show_Toast("删除成功");
        return 0;
    }


    //开始厂家码
    TextWatcher st_1_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 2) {
                edit_end_entBF2Bit_en.setText("" + edit_start_entBF2Bit_st.getText());
                edit_start_entproduceDate_st.setFocusable(true);
                edit_start_entproduceDate_st.setFocusableInTouchMode(true);
                edit_start_entproduceDate_st.requestFocus();
                edit_start_entproduceDate_st.findFocus();
                edit_start_entBF2Bit_st.setBackgroundColor(Color.GREEN);
            } else {
                edit_start_entBF2Bit_st.setBackgroundColor(Color.RED);
            }

        }
    };
    //开始日期码
    TextWatcher st_2_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {//开始日期码
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 5) {
                edit_start_entproduceDate_st.setBackgroundColor(Color.GREEN);
                if (factoryFeature == null || factoryFeature.trim().length() < 1) {
                    edit_end_entBF2Bit_en.setText("" + edit_start_entBF2Bit_st.getText());
                    edit_end_entproduceDate_ed.setText("" + edit_start_entproduceDate_st.getText());
                    edit_start_entAT1Bit_st.setFocusable(true);
                    edit_start_entAT1Bit_st.setFocusableInTouchMode(true);
                    edit_start_entAT1Bit_st.requestFocus();
                    edit_start_entAT1Bit_st.findFocus();

                } else {
                    //
                    edit_end_entproduceDate_ed.setText("" + edit_start_entproduceDate_st.getText());
                    edit_start_entboxNoAndSerial_st.setFocusable(true);//开始流水号
                    edit_start_entboxNoAndSerial_st.setFocusableInTouchMode(true);
                    edit_start_entboxNoAndSerial_st.requestFocus();
                    edit_start_entboxNoAndSerial_st.findFocus();
                }
            } else {
                edit_start_entproduceDate_st.setBackgroundColor(Color.RED);
            }
        }
    };
    //开始特征码
    TextWatcher st_3_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 1) {
                edit_end_entBF2Bit_en.setText("" + edit_start_entBF2Bit_st.getText());
                edit_end_entproduceDate_ed.setText("" + edit_start_entproduceDate_st.getText());
                edit_end_entAT1Bit_ed.setText("" + edit_start_entAT1Bit_st.getText());
                edit_start_entboxNoAndSerial_st.setFocusable(true);
                edit_start_entboxNoAndSerial_st.setFocusableInTouchMode(true);
                edit_start_entboxNoAndSerial_st.requestFocus();
                edit_start_entboxNoAndSerial_st.findFocus();
                edit_start_entAT1Bit_st.setBackgroundColor(Color.GREEN);
            } else {
                edit_start_entAT1Bit_st.setBackgroundColor(Color.RED);
            }
        }
    };
    //开始流水号
    TextWatcher st_4_watcher = new TextWatcher() {
        private int cou = 0;
        int selectionEnd = 0;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            cou = before + count;
            String editable = edit_start_entboxNoAndSerial_st.getText().toString();
            String str = Utils.stringFilter(editable); //过滤特殊字符
            if (!editable.equals(str)) {
                edit_start_entboxNoAndSerial_st.setText(str);
            }
            edit_start_entboxNoAndSerial_st.setSelection(edit_start_entboxNoAndSerial_st.length());
            cou = edit_start_entboxNoAndSerial_st.length();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 5) {
                edit_start_entboxNoAndSerial_st.setBackgroundColor(Color.GREEN);
                /*
                edit_end_entBF2Bit_en.setFocusable(true);
            	edit_end_entBF2Bit_en.setFocusableInTouchMode(true);
            	edit_end_entBF2Bit_en.requestFocus();
            	edit_end_entBF2Bit_en.findFocus();
            	*/
                //与扫描冲突
//            	edit_end_entboxNoAndSerial_ed.setFocusable(true);
//            	edit_end_entboxNoAndSerial_ed.setFocusableInTouchMode(true);
//            	edit_end_entboxNoAndSerial_ed.requestFocus();
//            	edit_end_entboxNoAndSerial_ed.findFocus();

            } else {
                edit_start_entboxNoAndSerial_st.setBackgroundColor(Color.RED);
            }
        }
    };

    //结束厂家码
    TextWatcher end_1_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 2) {
                edit_end_entBF2Bit_en.setBackgroundColor(Color.GREEN);
                edit_end_entproduceDate_ed.setFocusable(true);
                edit_end_entproduceDate_ed.setFocusableInTouchMode(true);
                edit_end_entproduceDate_ed.requestFocus();
                edit_end_entproduceDate_ed.findFocus();
            } else {
                edit_end_entBF2Bit_en.setBackgroundColor(Color.RED);
            }

        }
    };
    //结束日期码
    TextWatcher end_2_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 5) {
                edit_end_entproduceDate_ed.setBackgroundColor(Color.GREEN);
                if (factoryFeature == null || factoryFeature.trim().length() < 1) {
                    edit_end_entAT1Bit_ed.setFocusable(true);
                    edit_end_entAT1Bit_ed.setFocusableInTouchMode(true);
                    edit_end_entAT1Bit_ed.requestFocus();
                    edit_end_entAT1Bit_ed.findFocus();
                } else {
                    edit_end_entboxNoAndSerial_ed.setFocusable(true);
                    edit_end_entboxNoAndSerial_ed.setFocusableInTouchMode(true);
                    edit_end_entboxNoAndSerial_ed.requestFocus();
                    edit_end_entboxNoAndSerial_ed.findFocus();
                }

            } else {
                edit_end_entproduceDate_ed.setBackgroundColor(Color.RED);
            }
        }
    };
    //结束特征码
    TextWatcher end_3_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 1) {
                edit_end_entboxNoAndSerial_ed.setFocusable(true);
                edit_end_entboxNoAndSerial_ed.setFocusableInTouchMode(true);
                edit_end_entboxNoAndSerial_ed.requestFocus();
                edit_end_entboxNoAndSerial_ed.findFocus();
                edit_end_entAT1Bit_ed.setBackgroundColor(Color.GREEN);
            } else {
                edit_end_entAT1Bit_ed.setBackgroundColor(Color.RED);
            }
        }
    };
    //结束流水号
    TextWatcher end_4_watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            if (s.length() == 5) {
                edit_end_entboxNoAndSerial_ed.setBackgroundColor(Color.GREEN);
            } else {
                edit_end_entboxNoAndSerial_ed.setBackgroundColor(Color.RED);
            }
        }
    };

    private boolean checkMessage() {
        String sfz = at_bprysfz.getText().toString().trim().replace(" ", "");//证件号码
        String tx_htid = at_htid.getText().toString().trim().replace(" ", "");//合同编号 15位
        String tv_xmbh = at_xmbh.getText().toString().trim().replace(" ", "");//项目编号
        String xy[] = at_coordxy.getText().toString().replace("\n", "").replace("，", ",").replace(" ", "").split(",");//经纬度
        String tv_dwdm = at_dwdm.getText().toString().trim();//单位代码 13位
        if (list_uid.size() < 1) {
            Log.e("长度", "" + list_uid.size());
            show_Toast("当前雷管为空,请先注册雷管");
            return false;
        }
        if (equ_no.length() < 1) {
            show_Toast("当前设备编号为空,请先设置设备编号");
            return false;
        }
        if (at_coordxy.getText().toString().trim().length() < 1) {
            show_Toast("经纬度不能为空!");
            return false;
        }
        if (sfz.length() < 18) {
            show_Toast("人员证号格式不对!");
            return false;
        }
        if (!at_coordxy.getText().toString().trim().contains(",")) {
            show_Toast("经纬度格式不对");
            return false;
        }
        if (at_coordxy.getText().toString().trim().contains("4.9E-")) {
            show_Toast("经纬度格式不对，请按照例如116.38,39.90格式输入");
            return false;
        }
        if (StringUtils.isBlank(tx_htid) && StringUtils.isBlank(tv_xmbh) && StringUtils.isBlank(tv_dwdm)) {
            show_Toast("合同编号,项目编号,单位代码不能同时为空");
            return false;
        }
        if (tx_htid.length() != 0 && tx_htid.length() < 15) {
            Log.e("验证", "tx_htid.length(): " + tx_htid.length());
            Log.e("验证", "tx_htid: " + tx_htid);
            show_Toast("合同编号小于15位,请重新核对");
            return false;
        }
        return true;
    }


    @OnClick({R.id.btn_down_return, R.id.btn_down_inputOK, R.id.btn_down_workcode, R.id.btn_ReisterScanStart_st,
            R.id.btn_ReisterScanStart_ed, R.id.btn_inputOk,
            R.id.ly_setUpdata, R.id.btn_inputGKM, R.id.btn_location, R.id.btn_scanReister, R.id.btn_setdelay,
            R.id.btn_clear_htid, R.id.btn_clear_xmbh, R.id.btn_clear_sfz, R.id.btn_clear_project_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_down_return://添加项目
//                if (lySetUpData.getVisibility() == View.GONE) {
//                    btnDownReturn.setText("隐藏内容");
//                    lySetUpData.setVisibility(View.VISIBLE);
//                } else {
//                    lySetUpData.setVisibility(View.GONE);
//                    btnDownReturn.setText("添加项目");
//                }
                AlertDialog dialog2 = new AlertDialog.Builder(this)
                        .setTitle("清空提示")//设置对话框的标题//"成功起爆"
                        .setMessage("请确认是否清空所有下载信息,点击确认清空")//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("确认", (dialog, which) -> {
                            dialog.dismiss();
                            GreenDaoMaster.delAllMessage();//清空数据
                            mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据
                        }).create();
                dialog2.show();

                break;
            case R.id.btn_down_inputOK://保存
//                hideInputKeyboard();//隐藏键盘
//                saveData();
//                show_Toast("数据保存成功");
                Intent intent = new Intent(this, SaveProjectActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_down_workcode://下载
//                loadMoreData_lg(currentPage);
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                saveData();
                hideInputKeyboard();//隐藏键盘,取消焦点
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("下载提示")//设置对话框的标题//"成功起爆"
                        .setMessage("请确认项目编号,地理位置等信息输入无误后,点击确认下载")//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNegativeButton("再次确认", (dialog1, which) -> dialog1.dismiss())
                        .setPositiveButton("确认下载", (dialog12, which) -> {
                            dialog12.dismiss();
                            if (checkMessage()) {//校验输入的项目信息是否和法
                                upload();
//                                upload_xingbang();
                            }
                        }).create();
                dialog.show();
                break;
            case R.id.btn_inputGKM://输入管壳码
                if (lyInput.getVisibility() == View.GONE) {
                    btnInputGKM.setText("隐藏内容");
                    lyInput.setVisibility(View.VISIBLE);
                    lvShouquan.setVisibility(View.GONE);
                } else {
                    lyInput.setVisibility(View.GONE);
                    lvShouquan.setVisibility(View.VISIBLE);
                    btnInputGKM.setText("输入管壳码");
                }
//                Intent intent = new Intent(this,ReisterMainPage_scan.class);
//                startActivity(intent);
                break;
            case R.id.btn_location://启动定位
                if (jd.equals("")) {
                    show_Toast("定位中,请稍等");
                    break;
                }
                createHelpDialog();
//                mLocationClient.startLocation();
                break;
            case R.id.btn_inputOk://确定
                hideInputKeyboard();
                String checstr1 = checkInputData();
                if (checstr1 == null || checstr1.trim().length() < 1) {
                    String st2Bit = edit_start_entBF2Bit_st.getText().toString();//开始厂家码
                    String stproDt = edit_start_entproduceDate_st.getText().toString();//开始日期码
                    String st1Bit = edit_start_entAT1Bit_st.getText().toString();//开始特征码
                    String stsno = edit_start_entboxNoAndSerial_st.getText().toString();//开始流水号
                    final String prex = st2Bit + stproDt + st1Bit;
                    String edsno = edit_end_entboxNoAndSerial_ed.getText().toString();//结束流水号
                    final int start = Integer.parseInt(stsno);
                    final int end = Integer.parseInt(edsno);
                    pb_show = 1;
                    runPbDialog();
                    new Thread(() -> {
                        insertDenator(prex, start, end);
                        Log.e("添加码", "prex: " + prex);
                        Log.e("添加码", "start: " + start);
                        Log.e("添加码", "end: " + end);
                    }).start();
//                    loadMoreData_lg(currentPage);//查询所有雷管
                    mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                } else {
                    show_Toast(checstr1);
                }


                break;
            case R.id.btn_scanReister://扫码注册
                if (continueScanFlag == 0) {
                    continueScanFlag = 1;
                    if (scanBarThread != null) {
                        scanBarThread.exit = true;  // 终止线程thread
                        try {
                            scanBarThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    scanBarThread = new ScanBar();
                    scanBarThread.start();
                    btnScanReister.setText(getResources().getString(R.string.text_reister_scaning));//"正在扫码"
                } else {
                    continueScanFlag = 0;
                    btnScanReister.setText(getResources().getString(R.string.text_reister_scanReister));//"扫码注册"
                    scanDecode.stopScan();//停止扫描
                    if (scanBarThread != null) {
                        scanBarThread.exit = true;  // 终止线程thread
                        try {
                            scanBarThread.join();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.btn_setdelay://修改延时
                String str3 = new String("设置延时");//"当前雷管信息"
                Intent intent3 = new Intent(this, SetDelayTime.class);
                intent3.putExtra("dataSend", str3);
                startActivityForResult(intent3, 1);
                break;
            case R.id.btn_clear_htid:
                deleteHistory("history_htid", at_htid);

                break;
            case R.id.btn_clear_project_name:
                deleteHistory("history_project", at_htid);

                break;
            case R.id.btn_clear_xmbh:
                deleteHistory("history_xmbh", at_xmbh);
                break;
            case R.id.btn_clear_sfz:
                deleteHistory("history_bprysfz", at_bprysfz);
                break;
            case R.id.btn_ReisterScanStart_st:
                hideInputKeyboard();
                if (continueScanFlag == 0) {
                    continueScanFlag = 1;
                    scanDecode.starScan();//启动扫描
                } else {
                    continueScanFlag = 0;
                    scanDecode.stopScan();//停止扫描
                }
                sanButtonFlag = 2;
                break;
            case R.id.btn_ReisterScanStart_ed:
                hideInputKeyboard();
                if (continueScanFlag == 0) {
                    continueScanFlag = 1;
                    scanDecode.starScan();//启动扫描
                } else {
                    continueScanFlag = 0;
                    scanDecode.stopScan();//停止扫描
                }
                sanButtonFlag = 2;
                break;

        }
    }


    private void deleteHistory(String field, AutoCompleteTextView auto) {
//        MMKV kv = MMKV.mmkvWithID("network_url");
//        kv.encode(field, "");
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        sp.edit().putString(field, "").apply();
        initAutoComplete(field, auto);
        show_Toast("清空历史成功");
    }

    @OnClick(R.id.btn_clear_project_name)
    public void onViewClicked() {
    }

    private class ScanBar extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    scanDecode.starScan();
                    Thread.sleep(1250);
                    //break;

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    TextWatcher htbh_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 15) {
                at_htid.setBackgroundColor(Color.GREEN);
            } else {
                at_htid.setBackgroundColor(Color.RED);
            }
        }
    };
    TextWatcher xmbh_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 15) {
                at_xmbh.setBackgroundColor(Color.GREEN);
            } else {
                at_xmbh.setBackgroundColor(Color.RED);
            }
        }
    };
    TextWatcher dwdm_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 13) {
                at_dwdm.setBackgroundColor(Color.GREEN);
            } else {
                at_dwdm.setBackgroundColor(Color.RED);
            }
        }
    };
    TextWatcher sfz_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 18) {
                at_bprysfz.setBackgroundColor(Color.GREEN);
            } else {
                at_bprysfz.setBackgroundColor(Color.RED);
            }
        }
    };


    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        mRegion = String.valueOf(item.getOrder());
        // 保存区域参数
        SPUtils.put(this, Constants_SP.RegionCode, mRegion);
        switch (item.getItemId()) {

            case R.id.item_1:
            case R.id.item_2:
            case R.id.item_3:
            case R.id.item_4:
            case R.id.item_5:
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                // 显示提示
                show_Toast("已选择 区域" + mRegion);
                // 延时选择重置
//                resetView();
//                delay_set = "0";
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * 设置标题区域
     */
    private void setTitleRegion(String region, int size) {

        String str;
        if (size == -1) {
            str = " 区域" + region;
        } else {
            str = " 区域" + region + "(共:" + size + ")";
        }
        // 设置标题
        getSupportActionBar().setTitle(mOldTitle + str);

        totalbar_title.setText(mOldTitle + str);
        Log.e("liyi_Region", "已选择" + str);
    }

    private void choiceQuYu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("请选择区域");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choice_quyu, null);
        builder.setView(view);
        final CheckBox cb_mRegion1 = view.findViewById(R.id.dialog_cb_mRegion1);
        final CheckBox cb_mRegion2 = view.findViewById(R.id.dialog_cb_mRegion2);
        final CheckBox cb_mRegion3 = view.findViewById(R.id.dialog_cb_mRegion3);
        final CheckBox cb_mRegion4 = view.findViewById(R.id.dialog_cb_mRegion4);
        final CheckBox cb_mRegion5 = view.findViewById(R.id.dialog_cb_mRegion5);
        cb_mRegion1.setChecked(mRegion1);
        cb_mRegion2.setChecked(mRegion2);
        cb_mRegion3.setChecked(mRegion3);
        cb_mRegion4.setChecked(mRegion4);
        cb_mRegion5.setChecked(mRegion5);
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {

            if (cb_mRegion1.isChecked() || cb_mRegion2.isChecked() || cb_mRegion3.isChecked() || cb_mRegion4.isChecked() || cb_mRegion5.isChecked()) {

                mRegion1 = cb_mRegion1.isChecked();
                mRegion2 = cb_mRegion2.isChecked();
                mRegion3 = cb_mRegion3.isChecked();
                mRegion4 = cb_mRegion4.isChecked();
                mRegion5 = cb_mRegion5.isChecked();

                MmkvUtils.savecode("mRegion1", mRegion1);
                MmkvUtils.savecode("mRegion2", mRegion2);
                MmkvUtils.savecode("mRegion3", mRegion3);
                MmkvUtils.savecode("mRegion4", mRegion4);
                MmkvUtils.savecode("mRegion5", mRegion5);
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));

            } else {
                show_Toast("请至少选择一个区域");
            }

        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

}
