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
import android.graphics.Typeface;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.google.gson.Gson;
import com.scandecode.inf.ScanInterface;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.DetonatorAdapter_Paper;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.MlistView;
import android_serialport_api.xingbang.custom.ShouQuanAdapter;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.QuYu;
import android_serialport_api.xingbang.db.ShouQuan;
import android_serialport_api.xingbang.db.SysLog;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.db.greenDao.QuYuDao;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.services.LocationService;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.AMapUtils;
import android_serialport_api.xingbang.utils.AppLogUtils;
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

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DownWorkCode extends BaseActivity implements LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, ShouQuanAdapter.InnerItemOnclickListener {
//    @BindView(R.id.ly_setUpdata)
//    LinearLayout lySetUpData;
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
    @BindView(R.id.entBF2Bit_ed)//结束厂家码
    EditText edit_end_entBF2Bit_en;
    @BindView(R.id.entproduceDate_ed)//结束日期码
    EditText edit_end_entproduceDate_ed;
    @BindView(R.id.entAT1Bit_ed)//结束特征码
    EditText edit_end_entAT1Bit_ed;
    @BindView(R.id.entboxNoAndSerial_ed)//结束流水号
    EditText edit_end_entboxNoAndSerial_ed;
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
    NestedScrollView setDelayTimeMainPage;
    @BindView(R.id.et_num)
    EditText etNum;
    @BindView(R.id.ll_num)
    LinearLayout llNum;
    @BindView(R.id.et_duan)
    EditText etDuan;
    @BindView(R.id.ll_xmxx)
    LinearLayout llXmxx;
    @BindView(R.id.ll_dwxx)
    LinearLayout llDwxx;
    @BindView(R.id.btn_down_offline)
    Button btnOffline;
    private ShouQuanAdapter mAdapter_sq;//授权
    private SQLiteDatabase db;
    private Handler mHandler_httpresult;
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
    private TextView totalbar_title,title_lefttext;
    private String select_business;
    private String TAG = "下载项目页面";
    private List<Integer> qyIdList = new ArrayList<>();//用户多选的区域id
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_workcode);
        ButterKnife.bind(this);
        DatabaseHelper mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        AppLogUtils.writeAppLog("---进入下载项目页面---");
        baidudingwei();
        getUserMessage();//获取用户信息
//        getPropertiesData();//第二种获取用户信息
        //读取项目名称
        //获取偏好设置的编辑器
//        kv = MMKV.defaultMMKV();//
        SharedPreferences sp = getSharedPreferences("config", 0);
        //获取偏好设置的编辑器
        edit = sp.edit();
        pro_name = sp.getString("pro_name", "");
        at_projectName.setText(pro_name);

        mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);
        mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);
        mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);
        mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);
        mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);

        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        totalbar_title = findViewById(R.id.title_text);
        title_lefttext = findViewById(R.id.title_lefttext);
        totalbar_title.setVisibility(View.GONE);
        title_lefttext.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.GONE);
        iv_add.setVisibility(View.GONE);
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

        mAdapter_sq = new ShouQuanAdapter(this, map_dl, R.layout.item_list_shouquan_new);
        mAdapter_sq.setOnInnerItemOnClickListener(this);
        lvShouquan.setAdapter(mAdapter_sq);
        lvShouquan.setOnItemClickListener(this);

//        list_adapter = new SimpleCursorAdapter(
//                DownWorkCode.this,
//                R.layout.item_blast,
//                null,
//                new String[]{"blastserial", "sithole", "delay", "shellBlastNo"},
//                new int[]{R.id.blastserial, R.id.sithole, R.id.delay, R.id.shellBlastNo},
//                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
//        mListView.setAdapter(list_adapter);
//        getLoaderManager().initLoader(0, null, this);
        SpinnerAdapter adapter= ArrayAdapter.createFromResource(this, R.array.gsxz_name,android.R.layout.simple_spinner_dropdown_item);
        // 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new DetonatorAdapter_Paper<>(this, 4);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.setAdapter(mAdapter);

//        scan();//扫描初始化
        initHandle();//handle初始化
        edit_start_entBF2Bit_st.addTextChangedListener(st_1_watcher);
        edit_start_entproduceDate_st.addTextChangedListener(st_2_watcher);
        edit_start_entAT1Bit_st.addTextChangedListener(st_3_watcher);
        edit_start_entboxNoAndSerial_st.addTextChangedListener(st_4_watcher);
        edit_end_entBF2Bit_en.addTextChangedListener(end_1_watcher);
        edit_end_entproduceDate_ed.addTextChangedListener(end_2_watcher);
        edit_end_entAT1Bit_ed.addTextChangedListener(end_3_watcher);
        edit_end_entboxNoAndSerial_ed.addTextChangedListener(end_4_watcher);
        initCardViewData();
//        initAutoComplete("history_htid", at_htid);//输入历史记录
//        initAutoComplete("history_xmbh", at_xmbh);
//        initAutoComplete("history_dwdm", at_dwdm);
//        initAutoComplete("history_bprysfz", at_bprysfz);
//        initAutoComplete("history_coordxy", at_coordxy);
//        initAutoComplete("history_projectName", at_projectName);//项目名称
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

            Log.e("输入项目", "list: " + list.toString());
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

        //目前都是不可修改的设置项  所以不再根据字数设置背景显示
//        at_htid.addTextChangedListener(htbh_watcher);//长度监听
//        at_xmbh.addTextChangedListener(xmbh_watcher);//长度监听
//        at_dwdm.addTextChangedListener(dwdm_watcher);//长度监听
//        at_bprysfz.addTextChangedListener(sfz_watcher);//长度监听

        // 区域 更新视图
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        Utils.writeRecord("---进入项目下载页面---");
        AppLogUtils.writeAppLog("---进入项目下载页面---");

        //模拟下载---测试完要记得关闭
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                test();
//            }
//        }).start();

        //模拟插入数据
//        DanLingBean.LgsBean.LgBean lgBean=new DanLingBean.LgsBean.LgBean();
//        lgBean.setUid("1021019900401");
//        lgBean.setGzm("94242214050");
//        lgBean.setGzmcwxx("0");
//        GreenDaoMaster.updateLgState(lgBean);
    }

    private void initCardViewData() {
        Project usedProject = Application.getDaoSession().getProjectDao().queryBuilder().where(ProjectDao.Properties.Selected.eq("true")).unique();
        if (usedProject != null) {
            at_projectName.setText(usedProject.getProject_name());
            at_htid.setText(usedProject.getHtbh());
            at_xmbh.setText(usedProject.getXmbh());
            at_dwdm.setText(usedProject.getDwdm());
            at_coordxy.setText(usedProject.getCoordxy());
            String business = usedProject.getBusiness();
            at_bprysfz.setText(usedProject.getBprysfz());
            if (business.startsWith("非营业性")) {
                llXmxx.setVisibility(View.GONE);
                llDwxx.setVisibility(View.VISIBLE);
            } else {
                llXmxx.setVisibility(View.VISIBLE);
                llDwxx.setVisibility(View.GONE);
            }
        }
    }

    private void test() {
//        show_Toast("测试完要记得关闭测试test方法!!!");
        //旧编码
//        String res = "{\"cwxx\":\"0\",\"sqrq\":\"2022-05-11 17:36:16\",\"sbbhs\":[{\"sbbh\":\"F56A6800213\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"普格县辉隆聚鑫矿业01\",\"zbqyjd\":\"102.678632\",\"zbqywd\":\"27.319725\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格聚鑫矿业测\",\"zbqyjd\":\"102.679603\",\"zbqywd\":\"27.319692\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格县辉隆聚鑫矿业\",\"zbqyjd\":\"102.678327\",\"zbqywd\":\"27.319431\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"00000DB119124\",\"yxq\":\"2022-05-14 17:36:16\",\"gzm\":\"70107707\",\"gzmcwxx\":\"0\"}]}}";
        //新规则
//        String res = "{\"cwxx\":\"0\",\"sqrq\":\"2022-05-11 17:36:16\",\"sbbhs\":[{\"sbbh\":\"F56A6800213\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"普格县辉隆聚鑫矿业01\",\"zbqyjd\":\"102.678632\",\"zbqywd\":\"27.319725\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格聚鑫矿业测\",\"zbqyjd\":\"102.679603\",\"zbqywd\":\"27.319692\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"普格县辉隆聚鑫矿业\",\"zbqyjd\":\"102.678327\",\"zbqywd\":\"27.319431\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"5620418H70107\",\"yxq\":\"2022-05-14 17:36:16\",\"gzm\":\"FFA7666B05\",\"gzmcwxx\":\"0\"}]}}";
        //四川m900下载
        String res2 ="{\"cwxx\":\"0\",\"sqrq\":\"2025-01-25 11:00:27\",\"dwdm\":\"\",\"htid\":\"511522323120001\",\"xmbh\":\"\",\"sbbhs\":[{\"sbbh\":\"F5606803035\"}],\"zbqys\":{\"zbqy\":[" +
                "{\"zbqymc\":\"向家坝北总干渠一期二步工程施工第三标段\",\"zbqyjd\":\"104.8285619478\",\"zbqywd\":\"28.9722018873\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}," +
                "{\"zbqymc\":\"向家坝\",\"zbqyjd\":\"105.8285619\",\"zbqywd\":\"24.9722018\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}," +
                "{\"zbqymc\":\"累口岩隧洞\",\"zbqyjd\":\"104.8285619\",\"zbqywd\":\"28.972201\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"zbqydbxs\":{\"zbqydbx\":[]},\"jbqys\":{\"jbqy\":[]}," +
                "\"lgs\":{\"lg\":[{\"fbh\":\"5640311H69608\",\"uid\":\"5640311H69608\",\"yxq\":\"2025-01-28 11:00:27\",\"gzm\":\"27AEF72104\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"5640311H69644\",\"uid\":\"5640311H69644\",\"yxq\":\"2024-04-28 11:00:27\",\"gzm\":\"266CB84104\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"}]}}";
//        String res2 = "{\"cwxx\":\"0\",\"sqrq\":\"2025-01-08 13:50:55\",\"dwdm\":\"\",\"htid\":\"652926323110002\",\"xmbh\":\"\",\"sbbhs\":[{\"sbbh\":\"F1506803036\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"天辰四标\",\"zbqyjd\":\"82.2158\",\"zbqywd\":\"42.1197\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"zbqydbxs\":{\"zbqydbx\":[]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"fbh\":\"1540323228437\",\"uid\":\"1540323228437\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86562CFC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228431\",\"uid\":\"1540323228431\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866C782A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232416\",\"uid\":\"1540323232416\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863B4C960310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232423\",\"uid\":\"1540323232423\",\"yxq\":\"2025-01-11 13:50:55\",\"gzm\":\"8709BC060410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207708\",\"uid\":\"1540323207708\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86082D2E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239025\",\"uid\":\"1540323239025\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857441590410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207713\",\"uid\":\"1540323207713\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86E97EFA0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238040\",\"uid\":\"1540323238040\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8621B81A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236805\",\"uid\":\"1540323236805\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86EA0C540410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228107\",\"uid\":\"1540323228107\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A63CD80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228133\",\"uid\":\"1540323228133\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8569AA490410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238001\",\"uid\":\"1540323238001\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858499420410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239010\",\"uid\":\"1540323239010\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"C686F1250410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228439\",\"uid\":\"1540323228439\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866CAF180410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238035\",\"uid\":\"1540323238035\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86DAA0D30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228417\",\"uid\":\"1540323228417\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"864D54FC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237917\",\"uid\":\"1540323237917\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863A4C8C0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236814\",\"uid\":\"1540323236814\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86BB1D560410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228118\",\"uid\":\"1540323228118\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856B411A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236838\",\"uid\":\"1540323236838\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8605F6F20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237816\",\"uid\":\"1540323237816\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AB902E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207733\",\"uid\":\"1540323207733\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"860D5EA80410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236846\",\"uid\":\"1540323236846\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863984150410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236827\",\"uid\":\"1540323236827\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DE64DB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238014\",\"uid\":\"1540323238014\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A7C3380410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228402\",\"uid\":\"1540323228402\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86CE97450410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239041\",\"uid\":\"1540323239041\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865972920310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228445\",\"uid\":\"1540323228445\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AC5A780310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228148\",\"uid\":\"1540323228148\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857677220410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236828\",\"uid\":\"1540323236828\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D8711E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207730\",\"uid\":\"1540323207730\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8653714C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228404\",\"uid\":\"1540323228404\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866A53190410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237806\",\"uid\":\"1540323237806\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8698B0240410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238005\",\"uid\":\"1540323238005\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85EC74CA0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228447\",\"uid\":\"1540323228447\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85A38D180410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228134\",\"uid\":\"1540323228134\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8569C3000410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207739\",\"uid\":\"1540323207739\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86FEAF5F0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236844\",\"uid\":\"1540323236844\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86E3D2B60310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207736\",\"uid\":\"1540323207736\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86EEC6410410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236826\",\"uid\":\"1540323236826\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857AA3060410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232424\",\"uid\":\"1540323232424\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86E6A2C10310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207700\",\"uid\":\"1540323207700\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F834F70310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239003\",\"uid\":\"1540323239003\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866F92180410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236821\",\"uid\":\"1540323236821\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"860EFEF20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228146\",\"uid\":\"1540323228146\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"864292E80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228138\",\"uid\":\"1540323228138\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85697A350410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238032\",\"uid\":\"1540323238032\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B9FD110410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207715\",\"uid\":\"1540323207715\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85857D240410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228446\",\"uid\":\"1540323228446\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85A3BC600410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232406\",\"uid\":\"1540323232406\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86D2C6F20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237800\",\"uid\":\"1540323237800\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863ED7B60310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238042\",\"uid\":\"1540323238042\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D250580310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207742\",\"uid\":\"1540323207742\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86FB722C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236818\",\"uid\":\"1540323236818\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865D6EEC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237906\",\"uid\":\"1540323237906\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DE26500410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232440\",\"uid\":\"1540323232440\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8603014E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232441\",\"uid\":\"1540323232441\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"860304CA0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228428\",\"uid\":\"1540323228428\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86553DB20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228429\",\"uid\":\"1540323228429\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8656415B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237805\",\"uid\":\"1540323237805\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858C2C1D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236835\",\"uid\":\"1540323236835\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8606A9060410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232431\",\"uid\":\"1540323232431\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A7AB2B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228126\",\"uid\":\"1540323228126\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86380EE40310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236800\",\"uid\":\"1540323236800\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F297FE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226147\",\"uid\":\"1540323226147\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859FD4310410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239004\",\"uid\":\"1540323239004\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866F97E80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239027\",\"uid\":\"1540323239027\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85933B380410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207738\",\"uid\":\"1540323207738\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86409B350410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207724\",\"uid\":\"1540323207724\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859E78260410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232402\",\"uid\":\"1540323232402\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"870810030410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238000\",\"uid\":\"1540323238000\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8619CD120410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232444\",\"uid\":\"1540323232444\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8660331A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236823\",\"uid\":\"1540323236823\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"860F7A2E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237940\",\"uid\":\"1540323237940\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8658C8D40310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238024\",\"uid\":\"1540323238024\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"868457FF0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239024\",\"uid\":\"1540323239024\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857436920410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207740\",\"uid\":\"1540323207740\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857C31350410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226144\",\"uid\":\"1540323226144\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A2E1B80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239020\",\"uid\":\"1540323239020\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"862D36390410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232442\",\"uid\":\"1540323232442\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866024020410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228120\",\"uid\":\"1540323228120\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85B0772D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239019\",\"uid\":\"1540323239019\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856FD3100410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239043\",\"uid\":\"1540323239043\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A0BDCE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236812\",\"uid\":\"1540323236812\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86EBC6370410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237945\",\"uid\":\"1540323237945\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B4715C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207732\",\"uid\":\"1540323207732\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8640A90E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238039\",\"uid\":\"1540323238039\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"C6DB5D850310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207723\",\"uid\":\"1540323207723\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AE8E1A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228124\",\"uid\":\"1540323228124\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858D881B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239008\",\"uid\":\"1540323239008\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866F2A060410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236843\",\"uid\":\"1540323236843\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F5E69E0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228137\",\"uid\":\"1540323228137\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8569EF490410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236804\",\"uid\":\"1540323236804\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86D426220410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238028\",\"uid\":\"1540323238028\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85F0ADEC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228147\",\"uid\":\"1540323228147\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"C6B32E910410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238048\",\"uid\":\"1540323238048\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8706EC890310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237928\",\"uid\":\"1540323237928\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"87170DE20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207725\",\"uid\":\"1540323207725\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86471AB30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207744\",\"uid\":\"1540323207744\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85EB8F370410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207701\",\"uid\":\"1540323207701\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85AD5A430410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228448\",\"uid\":\"1540323228448\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8628FF0C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239017\",\"uid\":\"1540323239017\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856FDA2E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239049\",\"uid\":\"1540323239049\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86FD36420410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237926\",\"uid\":\"1540323237926\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8716EBDD0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239016\",\"uid\":\"1540323239016\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"867DE2140410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238029\",\"uid\":\"1540323238029\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85F5B5F20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237813\",\"uid\":\"1540323237813\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856B92590410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232446\",\"uid\":\"1540323232446\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"862007850410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237913\",\"uid\":\"1540323237913\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8646FEAE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238049\",\"uid\":\"1540323238049\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D25D820410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238034\",\"uid\":\"1540323238034\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859D252D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239030\",\"uid\":\"1540323239030\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85A262FB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238012\",\"uid\":\"1540323238012\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D811A60310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228426\",\"uid\":\"1540323228426\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85CC35C40410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238008\",\"uid\":\"1540323238008\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85EC66EF0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239028\",\"uid\":\"1540323239028\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859322420410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228438\",\"uid\":\"1540323228438\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8640D5370410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232434\",\"uid\":\"1540323232434\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A8461B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228142\",\"uid\":\"1540323228142\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863D582E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239021\",\"uid\":\"1540323239021\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8695AB130410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239039\",\"uid\":\"1540323239039\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8700E9C30410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237907\",\"uid\":\"1540323237907\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DDF3EE0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238033\",\"uid\":\"1540323238033\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863914180410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226141\",\"uid\":\"1540323226141\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85EFFFDF0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232418\",\"uid\":\"1540323232418\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85B41A4C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232415\",\"uid\":\"1540323232415\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85B422070410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207735\",\"uid\":\"1540323207735\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857D00670410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228111\",\"uid\":\"1540323228111\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8567AA420410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232430\",\"uid\":\"1540323232430\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AA39D60310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239037\",\"uid\":\"1540323239037\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"862B823B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207726\",\"uid\":\"1540323207726\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AB50510410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237803\",\"uid\":\"1540323237803\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863ECCDC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228416\",\"uid\":\"1540323228416\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85A3325A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236802\",\"uid\":\"1540323236802\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"869623F10310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207745\",\"uid\":\"1540323207745\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8630A02E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236801\",\"uid\":\"1540323236801\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F336B60310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236836\",\"uid\":\"1540323236836\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8605E3EB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207721\",\"uid\":\"1540323207721\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D3CB150410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207747\",\"uid\":\"1540323207747\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86ADE3440410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237815\",\"uid\":\"1540323237815\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"867DA7CA0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232414\",\"uid\":\"1540323232414\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B82A9C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237804\",\"uid\":\"1540323237804\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858C3F350410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239005\",\"uid\":\"1540323239005\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866FC8000410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239045\",\"uid\":\"1540323239045\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865DBFC30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228125\",\"uid\":\"1540323228125\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858E21F10310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228407\",\"uid\":\"1540323228407\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86C65C3D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236831\",\"uid\":\"1540323236831\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86638D380410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238047\",\"uid\":\"1540323238047\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8619C2220410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228149\",\"uid\":\"1540323228149\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863D6C400410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237827\",\"uid\":\"1540323237827\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F35D420410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238018\",\"uid\":\"1540323238018\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A40DDE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228412\",\"uid\":\"1540323228412\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86D081310410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232427\",\"uid\":\"1540323232427\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8693BBAE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239022\",\"uid\":\"1540323239022\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8695780D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228143\",\"uid\":\"1540323228143\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B2D7880410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228119\",\"uid\":\"1540323228119\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856B341D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236810\",\"uid\":\"1540323236810\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86EBBDCD0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232422\",\"uid\":\"1540323232422\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B7F5560410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228139\",\"uid\":\"1540323228139\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8569F6C50310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239012\",\"uid\":\"1540323239012\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8686552E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236817\",\"uid\":\"1540323236817\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865F14EC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236803\",\"uid\":\"1540323236803\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"869576E80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207702\",\"uid\":\"1540323207702\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"860809600410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239014\",\"uid\":\"1540323239014\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"867DD63A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228108\",\"uid\":\"1540323228108\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859B4E060410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237931\",\"uid\":\"1540323237931\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85B3B7320410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207703\",\"uid\":\"1540323207703\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85E720B30410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238006\",\"uid\":\"1540323238006\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8619E48C0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237923\",\"uid\":\"1540323237923\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"871726C50310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228432\",\"uid\":\"1540323228432\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"864516090410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239036\",\"uid\":\"1540323239036\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86057CF30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239031\",\"uid\":\"1540323239031\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85A1E9D70310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207722\",\"uid\":\"1540323207722\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D3C6340410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207746\",\"uid\":\"1540323207746\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AD82F20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236829\",\"uid\":\"1540323236829\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DE55E40310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228113\",\"uid\":\"1540323228113\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856AC03B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238045\",\"uid\":\"1540323238045\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8621D91A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239002\",\"uid\":\"1540323239002\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866FC3160410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238043\",\"uid\":\"1540323238043\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8609C60F0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237939\",\"uid\":\"1540323237939\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DDD4180410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237822\",\"uid\":\"1540323237822\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859E17080410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207741\",\"uid\":\"1540323207741\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85EB8C680410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228406\",\"uid\":\"1540323228406\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"862A1E100410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237946\",\"uid\":\"1540323237946\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"870D921E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228421\",\"uid\":\"1540323228421\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8655364C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228110\",\"uid\":\"1540323228110\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85680E010410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236845\",\"uid\":\"1540323236845\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8584E1C40410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228122\",\"uid\":\"1540323228122\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86438BD40310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228109\",\"uid\":\"1540323228109\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"864085FD0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228400\",\"uid\":\"1540323228400\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B08D890310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232429\",\"uid\":\"1540323232429\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85B0B58F0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232404\",\"uid\":\"1540323232404\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8707DF240410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238015\",\"uid\":\"1540323238015\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A742260410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228140\",\"uid\":\"1540323228140\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8641ED1C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207710\",\"uid\":\"1540323207710\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F0B1FA0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207716\",\"uid\":\"1540323207716\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F0C6430410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237927\",\"uid\":\"1540323237927\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8717ACDD0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228127\",\"uid\":\"1540323228127\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85E923E20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232437\",\"uid\":\"1540323232437\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A473F80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238027\",\"uid\":\"1540323238027\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86077EA20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232448\",\"uid\":\"1540323232448\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"862010000410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232449\",\"uid\":\"1540323232449\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8620094C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207711\",\"uid\":\"1540323207711\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85E9BA720410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239047\",\"uid\":\"1540323239047\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86FD56D40310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237933\",\"uid\":\"1540323237933\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8580754C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232426\",\"uid\":\"1540323232426\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8709C7220410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238010\",\"uid\":\"1540323238010\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A339E40310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237918\",\"uid\":\"1540323237918\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"864714E80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237809\",\"uid\":\"1540323237809\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"867A73240410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236808\",\"uid\":\"1540323236808\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8593142E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239033\",\"uid\":\"1540323239033\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86BE6C980310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228427\",\"uid\":\"1540323228427\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85C5D7240410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239040\",\"uid\":\"1540323239040\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86FD358D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239018\",\"uid\":\"1540323239018\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863FCBF20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237812\",\"uid\":\"1540323237812\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AB4DE70310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239007\",\"uid\":\"1540323239007\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"A66F47420410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236840\",\"uid\":\"1540323236840\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"864F28140410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237915\",\"uid\":\"1540323237915\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858EE1750410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237908\",\"uid\":\"1540323237908\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DEB9B30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238003\",\"uid\":\"1540323238003\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858CA2D70310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237942\",\"uid\":\"1540323237942\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"868CC83D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238023\",\"uid\":\"1540323238023\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858D21380410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232417\",\"uid\":\"1540323232417\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86D91DDA0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232420\",\"uid\":\"1540323232420\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86D91AF20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228440\",\"uid\":\"1540323228440\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"862981E50310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228130\",\"uid\":\"1540323228130\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8569D52B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228415\",\"uid\":\"1540323228415\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86293B610310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228441\",\"uid\":\"1540323228441\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86D150160410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237916\",\"uid\":\"1540323237916\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858F79EB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237935\",\"uid\":\"1540323237935\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858070390410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238025\",\"uid\":\"1540323238025\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858D57FE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237820\",\"uid\":\"1540323237820\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859D96100410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207719\",\"uid\":\"1540323207719\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8689C2E60310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232445\",\"uid\":\"1540323232445\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"861FF6E10310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239000\",\"uid\":\"1540323239000\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866F2FD10310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237921\",\"uid\":\"1540323237921\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858EE9320410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232435\",\"uid\":\"1540323232435\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AA4C0E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228104\",\"uid\":\"1540323228104\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A6A6CB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236809\",\"uid\":\"1540323236809\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86EA191A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237947\",\"uid\":\"1540323237947\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B4EA860410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228410\",\"uid\":\"1540323228410\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8628BC530410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226145\",\"uid\":\"1540323226145\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85EFF8F70310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226146\",\"uid\":\"1540323226146\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856830210410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232408\",\"uid\":\"1540323232408\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B9A9380410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237807\",\"uid\":\"1540323237807\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8581D84C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238036\",\"uid\":\"1540323238036\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86408BDF0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238019\",\"uid\":\"1540323238019\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A406F20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236824\",\"uid\":\"1540323236824\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858AAE6C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238046\",\"uid\":\"1540323238046\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8609D2140410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239013\",\"uid\":\"1540323239013\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8571C36C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207743\",\"uid\":\"1540323207743\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86FB891B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237937\",\"uid\":\"1540323237937\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F4C1E30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232409\",\"uid\":\"1540323232409\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86FB40920310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237944\",\"uid\":\"1540323237944\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86190E0C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232413\",\"uid\":\"1540323232413\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85FA33F30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207706\",\"uid\":\"1540323207706\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86FAB0D70310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226148\",\"uid\":\"1540323226148\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85C0AA2B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228102\",\"uid\":\"1540323228102\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86408E4C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228414\",\"uid\":\"1540323228414\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85A362520410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238030\",\"uid\":\"1540323238030\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86399AF90310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239026\",\"uid\":\"1540323239026\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8679A31A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232401\",\"uid\":\"1540323232401\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863D6B330410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239032\",\"uid\":\"1540323239032\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8700E2FC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228411\",\"uid\":\"1540323228411\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"868632420410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228449\",\"uid\":\"1540323228449\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86ACF0060410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238016\",\"uid\":\"1540323238016\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865857DE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238022\",\"uid\":\"1540323238022\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"869016EE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239009\",\"uid\":\"1540323239009\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866F42420410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238009\",\"uid\":\"1540323238009\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"861118400410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228136\",\"uid\":\"1540323228136\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8569624C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237924\",\"uid\":\"1540323237924\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"87170C010410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228424\",\"uid\":\"1540323228424\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8630F12D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228401\",\"uid\":\"1540323228401\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B060A70310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238021\",\"uid\":\"1540323238021\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86411A2E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238044\",\"uid\":\"1540323238044\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8706E50A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237919\",\"uid\":\"1540323237919\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"871760910310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207712\",\"uid\":\"1540323207712\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85E9CDDC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237823\",\"uid\":\"1540323237823\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F3621B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232410\",\"uid\":\"1540323232410\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86E6860B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237904\",\"uid\":\"1540323237904\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DEA6D70310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232425\",\"uid\":\"1540323232425\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85FA44DB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239046\",\"uid\":\"1540323239046\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865E641F0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237825\",\"uid\":\"1540323237825\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8636F6CA0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207704\",\"uid\":\"1540323207704\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85AD5D2B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237808\",\"uid\":\"1540323237808\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"C67A86D40310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228106\",\"uid\":\"1540323228106\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86211F060410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228442\",\"uid\":\"1540323228442\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86D0EE410410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237922\",\"uid\":\"1540323237922\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8716AE010410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226140\",\"uid\":\"1540323226140\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A2EF900310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228408\",\"uid\":\"1540323228408\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866A7C380410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237929\",\"uid\":\"1540323237929\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"871727B60310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207718\",\"uid\":\"1540323207718\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85858F860410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237829\",\"uid\":\"1540323237829\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8697D4E80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232443\",\"uid\":\"1540323232443\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85BD0EB30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236833\",\"uid\":\"1540323236833\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"862476F80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238038\",\"uid\":\"1540323238038\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"87079B9E0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237814\",\"uid\":\"1540323237814\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85FBC04C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228141\",\"uid\":\"1540323228141\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85768B4A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232428\",\"uid\":\"1540323232428\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863AA3B90310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207737\",\"uid\":\"1540323207737\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8653EB120410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238017\",\"uid\":\"1540323238017\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A325CE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207707\",\"uid\":\"1540323207707\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85E69CF20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228115\",\"uid\":\"1540323228115\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856B5B120410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232407\",\"uid\":\"1540323232407\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86D2B0DF0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207714\",\"uid\":\"1540323207714\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85F3A31F0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236848\",\"uid\":\"1540323236848\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"864E8FDE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236811\",\"uid\":\"1540323236811\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86BB22520410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237801\",\"uid\":\"1540323237801\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858123910310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238037\",\"uid\":\"1540323238037\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86BA15DA0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236819\",\"uid\":\"1540323236819\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865F090A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238002\",\"uid\":\"1540323238002\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858C828B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239011\",\"uid\":\"1540323239011\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8571B8F70310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236849\",\"uid\":\"1540323236849\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8584F0930410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228409\",\"uid\":\"1540323228409\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B5D8F90310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226142\",\"uid\":\"1540323226142\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AC48400410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228423\",\"uid\":\"1540323228423\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86318F1A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228436\",\"uid\":\"1540323228436\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DD01560410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239015\",\"uid\":\"1540323239015\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863734D80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237909\",\"uid\":\"1540323237909\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"861C47F20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228419\",\"uid\":\"1540323228419\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85EB6B1A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228425\",\"uid\":\"1540323228425\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85C5D2560410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236841\",\"uid\":\"1540323236841\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86E437150410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237903\",\"uid\":\"1540323237903\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85706C3F0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237932\",\"uid\":\"1540323237932\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8685FE100410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237920\",\"uid\":\"1540323237920\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"87173BA20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226149\",\"uid\":\"1540323226149\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85683B560410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232439\",\"uid\":\"1540323232439\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A47A750410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237905\",\"uid\":\"1540323237905\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857075050410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236815\",\"uid\":\"1540323236815\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865DD7C20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207720\",\"uid\":\"1540323207720\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86472DCD0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238031\",\"uid\":\"1540323238031\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"870790CF0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238026\",\"uid\":\"1540323238026\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"1F31C8920410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228117\",\"uid\":\"1540323228117\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856ACC780410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237819\",\"uid\":\"1540323237819\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"867D8EFB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228135\",\"uid\":\"1540323228135\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856A3CCE0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232438\",\"uid\":\"1540323232438\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865553E30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207734\",\"uid\":\"1540323207734\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857C94BF0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207749\",\"uid\":\"1540323207749\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857C40700410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232421\",\"uid\":\"1540323232421\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8705890E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232419\",\"uid\":\"1540323232419\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85B0450C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232412\",\"uid\":\"1540323232412\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"870608100410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228101\",\"uid\":\"1540323228101\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859B59380410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237817\",\"uid\":\"1540323237817\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85FBC7030410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207748\",\"uid\":\"1540323207748\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86308D7B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237821\",\"uid\":\"1540323237821\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8704E3F50310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228116\",\"uid\":\"1540323228116\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856B4D220410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237818\",\"uid\":\"1540323237818\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86CD6C560410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237826\",\"uid\":\"1540323237826\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8704524D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207717\",\"uid\":\"1540323207717\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85747C5E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228434\",\"uid\":\"1540323228434\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"864173300410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228114\",\"uid\":\"1540323228114\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856B275B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236807\",\"uid\":\"1540323236807\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8592E87E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228132\",\"uid\":\"1540323228132\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856A4D1A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228121\",\"uid\":\"1540323228121\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86435D660410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237802\",\"uid\":\"1540323237802\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8698A9F10310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232447\",\"uid\":\"1540323232447\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85BD98FC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232433\",\"uid\":\"1540323232433\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865552030410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228105\",\"uid\":\"1540323228105\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85BB24B40410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237811\",\"uid\":\"1540323237811\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86E7FF1F0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239006\",\"uid\":\"1540323239006\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866F7FC60310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228403\",\"uid\":\"1540323228403\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86CEA0320410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207728\",\"uid\":\"1540323207728\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86ADFFF00310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236813\",\"uid\":\"1540323236813\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86EBD5F00310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236842\",\"uid\":\"1540323236842\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863993EC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228422\",\"uid\":\"1540323228422\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85CBC92D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239048\",\"uid\":\"1540323239048\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86FDAEE10310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232400\",\"uid\":\"1540323232400\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"863CC8100410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207727\",\"uid\":\"1540323207727\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86AB5CFB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228123\",\"uid\":\"1540323228123\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85E92E040410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207729\",\"uid\":\"1540323207729\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859DDD310410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237900\",\"uid\":\"1540323237900\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"A5DE53E90310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237941\",\"uid\":\"1540323237941\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86CE1EF20310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323226143\",\"uid\":\"1540323226143\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86ACAA130410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228420\",\"uid\":\"1540323228420\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85EB7F160410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237943\",\"uid\":\"1540323237943\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8658E28D0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237936\",\"uid\":\"1540323237936\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F4D8C50310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238013\",\"uid\":\"1540323238013\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D8C10C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228430\",\"uid\":\"1540323228430\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DCCC1A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236830\",\"uid\":\"1540323236830\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"1EE45DD90410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228443\",\"uid\":\"1540323228443\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8686A2170410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239023\",\"uid\":\"1540323239023\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8679AE880410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237902\",\"uid\":\"1540323237902\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DE66550410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207705\",\"uid\":\"1540323207705\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86704C050410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237901\",\"uid\":\"1540323237901\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85DE13300410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228103\",\"uid\":\"1540323228103\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86212E350410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236816\",\"uid\":\"1540323236816\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86EBDE440410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237948\",\"uid\":\"1540323237948\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86198F1A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228129\",\"uid\":\"1540323228129\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85B07A3D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236832\",\"uid\":\"1540323236832\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866442690410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228145\",\"uid\":\"1540323228145\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85FA070C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228100\",\"uid\":\"1540323228100\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8616FCE90310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228131\",\"uid\":\"1540323228131\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"856969120410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207731\",\"uid\":\"1540323207731\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"860D76640410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237912\",\"uid\":\"1540323237912\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858C992E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239034\",\"uid\":\"1540323239034\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"862B7FF30310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236822\",\"uid\":\"1540323236822\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858AF7180410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228413\",\"uid\":\"1540323228413\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86CE79390410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228144\",\"uid\":\"1540323228144\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85FA12160410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237824\",\"uid\":\"1540323237824\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8636EDB80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236834\",\"uid\":\"1540323236834\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8606C2890310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236806\",\"uid\":\"1540323236806\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86D3E0EF0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228444\",\"uid\":\"1540323228444\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"C686915D0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232411\",\"uid\":\"1540323232411\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"869446D50310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236839\",\"uid\":\"1540323236839\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8624692E0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237930\",\"uid\":\"1540323237930\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"868616170410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228418\",\"uid\":\"1540323228418\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B65E060410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239038\",\"uid\":\"1540323239038\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86BE7F050410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237925\",\"uid\":\"1540323237925\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8716ED110410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237810\",\"uid\":\"1540323237810\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86E7FC0B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239044\",\"uid\":\"1540323239044\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86A0B6050410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239042\",\"uid\":\"1540323239042\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865979830310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323207709\",\"uid\":\"1540323207709\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85F3AC090410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232405\",\"uid\":\"1540323232405\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86B9ACFB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236837\",\"uid\":\"1540323236837\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86E5FE6A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237828\",\"uid\":\"1540323237828\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8697E7300410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236825\",\"uid\":\"1540323236825\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D874240410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228405\",\"uid\":\"1540323228405\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86C65A2C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238020\",\"uid\":\"1540323238020\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85F0AEEB0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238041\",\"uid\":\"1540323238041\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"1EEEB5EA0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228433\",\"uid\":\"1540323228433\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865D580B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236847\",\"uid\":\"1540323236847\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86F6147C0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238007\",\"uid\":\"1540323238007\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8610AB1A0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232436\",\"uid\":\"1540323232436\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D904FC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228128\",\"uid\":\"1540323228128\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"86380B360410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232432\",\"uid\":\"1540323232432\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D914EE0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239001\",\"uid\":\"1540323239001\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"866FAA510410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237911\",\"uid\":\"1540323237911\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"858C8A1C0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323236820\",\"uid\":\"1540323236820\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"857AA20F0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237910\",\"uid\":\"1540323237910\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"8639B5CA0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237938\",\"uid\":\"1540323237938\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85D74F750310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239035\",\"uid\":\"1540323239035\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"860565BC0310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237934\",\"uid\":\"1540323237934\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85B33D600410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238004\",\"uid\":\"1540323238004\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85849C6B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228435\",\"uid\":\"1540323228435\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865D18150410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323238011\",\"uid\":\"1540323238011\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"865858100410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237914\",\"uid\":\"1540323237914\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"861C380B0410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323228112\",\"uid\":\"1540323228112\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"85681A920410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323237949\",\"uid\":\"1540323237949\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"868CBE240410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323232403\",\"uid\":\"1540323232403\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"859E12060410\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"},{\"fbh\":\"1540323239029\",\"uid\":\"1540323239029\",\"yxq\":\"2024-05-11 13:50:55\",\"gzm\":\"862D5DE80310\",\"sjlx\":\"1\",\"gzmcwxx\":\"0\"}]}}";
        //        String res2 = "{\"cwxx\":\"0\",\"sqrq\":\"2024-04-30 11:00:27\",\"sbbhs\":[{\"sbbh\":\"F56A6800213\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"普格县辉隆聚鑫矿业01\",\"zbqyjd\":\"102.678632\",\"zbqywd\":\"27.319725\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"3031004206729\",\"yxq\":\"2024-04-30 11:00:27\",\"gzm\":\"FFA7666B01\",\"gzmcwxx\":\"0\"},{\"uid\":\"3031004206923\",\"yxq\":\"2024-04-30 11:00:27\",\"gzm\":\"FFA7666B02\",\"gzmcwxx\":\"0\"},{\"uid\":\"3031004273208\",\"yxq\":\"2024-04-30 11:00:27\",\"gzm\":\"FFA7666B03\",\"gzmcwxx\":\"0\"},{\"uid\":\"3031004293124\",\"yxq\":\"2024-04-30 11:00:27\",\"gzm\":\"FFA7666B04\",\"gzmcwxx\":\"0\"},{\"uid\":\"3031004273226\",\"yxq\":\"2024-04-30 11:00:27\",\"gzm\":\"FFA7666B05\",\"gzmcwxx\":\"0\"}]}}";
        //错误经纬度下载
//        String res2 = "{\"cwxx\":\"0\",\"sqrq\":\"2023-02-27 09:29:50\",\"sbbhs\":[{\"sbbh\":\"F56A6800261\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"讲治镇光明隧道出口\",\"zbqyjd\":\"107.94954\",\"zbqywd\":\"31.026169\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"明月山隧道进口\",\"zbqyjd\":\"107.89732\",\"zbqywd\":\"341.0355\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"明月山隧道出口\",\"zbqyjd\":\"107.846616\",\"zbqywd\":\"31.045254\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"5621126H81213\",\"yxq\":\"2023-03-02 09:29:50\",\"gzm\":\"939D64B603\",\"gzmcwxx\":\"0\"}]}}";
//        String res2 = "{\"cwxx\":\"0\",\"sqrq\":\"2022-12-16 17:45:05\",\"sbbhs\":[{\"sbbh\":\"F5606802310\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"渝兴\",\"zbqyjd\":\"108.425483\",\"zbqywd\":\"29.410818\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null},{\"zbqymc\":\"渝兴萤石矿\",\"zbqyjd\":\"29.407371\",\"zbqywd\":\"108.424906\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"5621113H32582\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32577\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32587\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32594\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32584\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32583\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32592\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32586\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32599\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32578\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32595\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32588\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32593\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32591\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32597\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32581\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32590\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32589\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32579\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32598\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32576\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32580\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32596\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"},{\"uid\":\"5621113H32585\",\"yxq\":\"\",\"gzm\":\"\",\"gzmcwxx\":\"2\"}]}}";

        //模拟下载150发雷管
//        String res2 = "{\"cwxx\":\"0\",\"sqrq\":\"2022-08-14 09:29:20\",\"sbbhs\":[{\"sbbh\":\"F560600002\"}],\"zbqys\":{\"zbqy\":[{\"zbqymc\":\"德凤矿业\",\"zbqyjd\":\"104.802639\",\"zbqywd\":\"28.351421\",\"zbqybj\":\"5000\",\"zbqssj\":null,\"zbjzsj\":null}]},\"jbqys\":{\"jbqy\":[]},\"lgs\":{\"lg\":[{\"uid\":\"5620705H84983\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA8BC04104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80098\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA3C1EF003\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84834\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB66FD6B04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84951\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB78772404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84839\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB8E7BA304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84833\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB76FF5404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80075\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0532474D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80054\",\"yxq\":\"2022-08-17 09:29:20\"," +
//                "\"gzm\":\"053CBBD703\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84973\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA9C0CD504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84980\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB6CDD4604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80029\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAC4BE9904\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84959\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB84EA1704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80040\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82384AA303\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80056\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04D128DD03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80077\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"058FC41B04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80059\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA440A3804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80047\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"050A593104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80019\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FABF1FF304\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80052\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0503180504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80069\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5A226204\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80018\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA4B2C3404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80024\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBDA92A604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84975\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB5D004204\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80071\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"052207D603\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84986\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB886A7D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80095\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0552FB4D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80009\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"049B0F0304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80013\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0560C8C603\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H84970\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAA8ACB803\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80060\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA4B6CB804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80023\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBB5946604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80033\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBBAA55E04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84989\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA9173E203\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80088\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5C0B1704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80011\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"054C58AB03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84968\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB67384C04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84953\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBA4023F04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80084\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0520FF3304\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80089\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"052C5B0004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84976\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAA6884704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84836\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB469E1304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80091\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0557E16704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80065\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04F02A8D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84999\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB978AF703\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80072\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"054C51DE03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80039\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBA45B4304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84837\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAB6DD7504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84957\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB54265104\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80034\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"053371F903\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80006\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA3C464D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80086\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"052998B703\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80053\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04FB832404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80003\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04A0E62504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80037\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0512A79904\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80087\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA46625704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84988\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA8BA61E05\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80021\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0584060804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80022\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBB62B6104\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H84954\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB7FC45304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84994\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA84528804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84981\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA7C28B604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80051\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82E6C9EA03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80058\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04DDE0FE03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80074\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBBA0CF503\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80093\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5BE64704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80092\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0522B46904\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80028\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"05288BC003\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84987\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB7B5F9C04\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80038\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"051CF41D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80080\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBB77AB004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84991\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB9C653104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80000\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04FBDFB004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80079\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA6A05BC04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80050\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA764D1504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84972\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB75B90D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84985\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB84940104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84969\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FACA8FFD03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80010\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA46AE6804\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H84997\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAE6110704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80004\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"050B1F8E04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80015\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA3C813304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80076\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0596973F04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84955\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB50718C03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80094\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0573C76704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80081\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBA82E5304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80049\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04B08E2C04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80064\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBCE9E6404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80066\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"049EC12304\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80002\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA56CFCB04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84974\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5D6AAC03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80073\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"053E48AA03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80097\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0567207804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80090\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FABB25AB04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80083\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"051E4D2B04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84995\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB55429904\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80085\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0573C2D403\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80041\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"051EE38404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84998\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB6CEF3D04\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80030\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAC1979404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84992\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB7E6F4404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80014\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA49666D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84990\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAF1080604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80061\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA46707A04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84982\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA63E82704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80048\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0556D37104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80063\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0484D18F04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80017\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"055BD65504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80043\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"050BA3E503\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80099\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0525283004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80045\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82DB100504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84978\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5F260E04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80027\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBD2653704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84996\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FABF508404\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80005\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0586007503\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80078\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"050EAA6804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80082\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA55537804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84958\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA8B037604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80057\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82E66A7C04\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80046\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"05415E4C04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80096\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA49D3F103\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80035\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"05796A7F04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80042\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"82E6B97003\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80020\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"058332F703\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84838\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB811A9304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80031\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"053E45EE03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80012\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FAB45C7D04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80007\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA5B0A0604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80044\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0509BAC903\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H84835\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB62A4ED04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80008\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04BE8B1704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84971\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA9C708604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80016\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBE2B6F203\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80026\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBA4538004\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80062\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"04F48AD503\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80070\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0498A9CA03\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80036\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"054BFA6304\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80025\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBCF0AF203\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84832\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB90393E04\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H84952\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB91728204\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84977\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB55785704\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80067\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBCDB03104\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84979\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA7EEC5604\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80068\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBD74B3F04\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84956\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB5AB23504\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84993\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB57E78904\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80032\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FBB76C1A05\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H80001\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FA4A02B804\",\"gzmcwxx\":\"0\"},{\"uid\":\"5620705H84984\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"FB88F86C04\",\"gzmcwxx\":\"0\"}," +
//                "{\"uid\":\"5620705H80055\",\"yxq\":\"2022-08-17 09:29:20\",\"gzm\":\"0523C9A804\",\"gzmcwxx\":\"0\"}]}}";
        final String[] xy = "104.8285619,28.972201".replace("\n", "").replace("，", ",").replace(" ", "").split(",");//经纬度
        Gson gson = new Gson();
        DanLingBean danLingBean = gson.fromJson(res2, DanLingBean.class);
        Log.e("测试", "danLingBean: " + danLingBean);
        try {
            JSONObject object1 = new JSONObject(res2);
            String cwxx = null;

            cwxx = object1.getString("cwxx");
            String sqrq2 = danLingBean.getSqrq();
            long time2 = (long) 4 * 86400000;
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String yxq = "";
            try {
                Date date3 = sd.parse(sqrq2);//当前日期
                yxq = sd.format(date3.getTime() + time2);
                Log.e("获取申请日期3天后的日期", "yxq: " + yxq + " sqrq2:" + sqrq2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (cwxx.equals("0")) {
                int err = 0;
                for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                    if (!danLingBean.getLgs().getLg().get(i).getGzmcwxx().equals("0")) {
                        err++;
                    }
                }


                Log.e("第一步", "插入下载授权信息: --------------" );
                if (danLingBean.getCwxx().equals("0")) {
                    if (danLingBean.getZbqys().getZbqy().size() > 0) {
                        double zbqyjd = Double.parseDouble(xy[0]);//116.456535
                        double zbqywd = Double.parseDouble(xy[1]);//37.427541
                        for (int i = 0; i < danLingBean.getZbqys().getZbqy().size(); i++) {
                            try {
                                double jingdu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqyjd());
                                double weidu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqywd());
                                double banjing = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqybj());
                                //判断经纬度
                                LngLat start = new LngLat(zbqyjd, zbqywd);
                                LngLat end = new LngLat(jingdu, weidu);
                                double juli3 = AMapUtils.calculateLineDistance(start, end);
                                Log.e("经纬度", "juli3: " + juli3);
                                if (juli3 < banjing) {
                                    Log.e("经纬度", "小于范围: " );
                                    insertJson(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res2, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc(), yxq);
//                                        insertJson_new(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
                                }
                            } catch (Exception e) {
                                show_Toast(danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd() + "经纬度错误");
                                e.printStackTrace();
                            }
                        }
                    }
                }


                mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据
                Log.e("第二步", "插入下载的雷管授权信息到备份库: --------------" );
                if (danLingBean.getLgs().getLg().size() > 0) {
                    for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                        GreenDaoMaster.updateLgState(danLingBean.getLgs().getLg().get(i), yxq);
                    }
                }
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
//                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    // 查询全部雷管 倒叙(序号)
                    qyIdList = new GreenDaoMaster().getSelectedQyIdList();
//                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
                    mListData = new GreenDaoMaster().queryDetonatorRegionAscNew(qyIdList);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    list_uid.clear();
                    for (int i = 0; i < mListData.size(); i++) {
                        list_uid.add(mListData.get(i).getShellBlastNo());
                    }

                    // 设置标题区域
//                    setTitleRegion(mRegion, mListData.size());
                    setTitleRegionNew(qyIdList, mListData.size());
                    break;

                // 重新排序 更新视图
                case 1002:
                    // 雷管孔号排序 并 重新查询
//                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
                    // 查询全部雷管 倒叙(序号)
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
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
                    show_Toast(getResources().getString(R.string.text_xxcg));
                    break;
                case 1:
                case 99:
                    show_Toast(String.valueOf(msg.obj));
                    break;
                case 2:
                    show_Toast(getResources().getString(R.string.text_xzsb1));
                    break;
                case 3:
                    show_Toast(getResources().getString(R.string.text_xzsb2));
                    break;
                case 4:
                    show_Toast(getResources().getString(R.string.text_xzsb3));
                    break;
                case 5:
                    show_Toast(getResources().getString(R.string.text_xzsb4));
                    break;
                case 6:
                    show_Toast(getResources().getString(R.string.text_xzsb5));
                    break;
                case 7:
                    show_Toast(getResources().getString(R.string.text_xzsb6));
                    break;
                case 8:
                    show_Toast(getResources().getString(R.string.text_xzsb7));
                    break;
                case 9:
                    show_Toast(getResources().getString(R.string.text_xzsb8));
                    break;
                case 10:
                    show_Toast(getResources().getString(R.string.text_xzsb9));
                    break;
                case 11:
                    show_Toast(getResources().getString(R.string.text_xzsb10));
                    break;
                case 12:
                    show_Toast(getResources().getString(R.string.text_xzsb11));
                    break;
                case 13:
                    show_Toast(getResources().getString(R.string.text_xzsb12));
                    break;
                case 14:
                    show_Toast(getResources().getString(R.string.text_xzsb13));
                    break;
                case 15:
                    show_Toast(getResources().getString(R.string.text_xzsb14));
                    break;
                case 89:
                    show_Toast(getResources().getString(R.string.text_xzsb15));
                    break;


            }
            return false;
        });
        mHandler_httpresult = new Handler(msg -> {
            upData_sq_size();//更新授权数量
            loadMoreData_sq();//读取数据
            mAdapter_sq.notifyDataSetChanged();
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
                show_Toast(getResources().getString(R.string.text_reister_tip5));
            } else if (isCorrectReisterFea == 4) {
                SoundPlayUtils.play(3);
                show_Toast(getResources().getString(R.string.text_error_tip69) + lg_No + getString(R.string.text_error_tip72) + singleShellNo + getString(R.string.text_error_tip71));
//                show_Toast("与第" + lg_No + "发" + singleShellNo + "重复");
            } else {
                SoundPlayUtils.play(3);

                show_Toast(getResources().getString(R.string.text_line_tip9));
            }
            isCorrectReisterFea = 0;
            return false;
        });


    }

    private void upData_sq_size() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<ShouQuan> list_sq = master.queryShouQuan();
        for (int a = 0; a < list_sq.size(); a++) {
            master.updataShouQuan(list_sq.get(a).getSpare2());
        }
    }

    @Override
    protected void onResume() {
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
        super.onResume();
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
            values.put("regdate", Utils.getDateFormat(new Date()));
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
            values.put("regdate", Utils.getDateFormat(new Date()));

            db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + index});

        }

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
//        String url = Utils.httpurl_down_test;//丹灵下载测试
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
            AppLogUtils.writeAppLog("---上传丹灵信息:" + object);
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

                mHandler_1.sendMessage(mHandler_1.obtainMessage(13));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res;
                try {
                    res = new String(MyUtils.decryptMode(key.getBytes(), Base64.decode(response.body().string().toString(), Base64.DEFAULT)));
                } catch (Exception e) {
                    mHandler_1.sendMessage(mHandler_1.obtainMessage(14));

                    return;
                }
                Log.e("网络请求", "res: " + res);
                Utils.writeRecord("---丹灵网返回:" + res);
                AppLogUtils.writeAppLog("---丹灵网返回:" + res);
                Gson gson = new Gson();
                DanLingBean danLingBean = gson.fromJson(res, DanLingBean.class);
                try {
                    JSONObject object1 = new JSONObject(res);
                    String cwxx = object1.getString("cwxx");



                    if (cwxx.equals("0")) {
                        String sqrq2 = danLingBean.getSqrq();
                        long time2 = (long) 3 * 86400000;
                        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String yxq = "";
                        try {
                            Date date3 = sd.parse(sqrq2);//当前日期
                            yxq = sd.format(date3.getTime() + time2);
                            Log.e("获取申请日期3天后的日期", "yxq: " + yxq + " sqrq2:" + sqrq2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

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
                                    try {
                                        double jingdu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqyjd());
                                        double weidu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqywd());
                                        double banjing = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqybj());
                                        //判断经纬度
                                        LngLat start = new LngLat(zbqyjd, zbqywd);
                                        LngLat end = new LngLat(jingdu, weidu);
                                        double juli3 = AMapUtils.calculateLineDistance(start, end);
                                        Log.e("经纬度", "juli3: " + juli3);

                                        if (juli3 < banjing) {
                                            insertJson(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc(), yxq);
//                                        insertJson_new(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
                                        }
                                    } catch (Exception e) {
                                        show_Toast(danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd() + "经纬度错误");
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }


                        if (danLingBean.getLgs().getLg().size() > 0) {
                            for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                                GreenDaoMaster.updateLgState(danLingBean.getLgs().getLg().get(i), yxq);
                            }

                        }

                        if (err != 0) {
                            Log.e("下载", "err: " + err);
//                            show_Toast_ui(danLingBean.getZbqys().getZbqy().get(0).getZbqymc() + "下载的雷管出现错误,请检查数据");
                        }
                        mHandler_1.sendMessage(mHandler_1.obtainMessage(0));//"项目下载成功"
                        mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据
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
                    } else {
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
        pb_show = 1;
        runPbDialog();//loading画面
        final String key = "jadl12345678912345678912";
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
                mHandler_1.sendMessage(mHandler_1.obtainMessage(13));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res;
                try {
                    res = response.body().string();//response.body()只能调用一次,第二次调用就会变成null
                } catch (Exception e) {
                    mHandler_1.sendMessage(mHandler_1.obtainMessage(15));
                    return;
                }
                Log.e("网络请求返回", "res: " + res);
                Utils.writeRecord("---煋邦返回:" + res);
                AppLogUtils.writeAppLog("---煋邦返回:" + res);
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
//                                    Log.e("经纬度", "juli3: " + juli3);
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
                                GreenDaoMaster.updateLgState(danLingBean.getLgs().getLg().get(i), danLingBean.getSqrq());
                            }
                        }

                        if (err != 0) {
                            Log.e("下载", "err: " + err);
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
        Utils.saveFile();//把软存中的数据存入磁盘中
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
        String[] a = coordxy.split("\\.");
        if (coordxy == null || coordxy.trim().length() < 8 || coordxy.indexOf(",") < 5 || a.length != 3) {
            tipStr = getResources().getString(R.string.text_down_tip11);
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
        builder.setTitle(getResources().getString(R.string.text_down_tip12));//"说明"
        builder.setView(view);
        builder.setPositiveButton(getResources().getString(R.string.text_alert_sure), (dialog, which) -> {
            at_coordxy.setText(jd_5.getText().toString() + jd_4.getText().toString() + "," + wd_5.getText().toString() + wd_4.getText().toString() + "");
            dialog.dismiss();
        });
        builder.create().show();
    }

    /**
     * 保存信息
     */
    private void saveData() {
        edit.putString("pro_name", at_projectName.getText().toString());
        edit.commit();//点击提交编辑器

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
        int duan = Integer.parseInt(etDuan.getText().toString());

        int reCount = 0;
        for (int i = start; i <= end; i++) {
            shellNo = prex + String.format("%05d", i);
            if (checkRepeatShellNo(shellNo) == 1) {
                tipInfoFlag = 89;
                break;
            }
            DetonatorTypeNew detonatorTypeNew = new GreenDaoMaster().serchDenatorId(shellNo);
            int duanNUM = getDuanNo(duan, mRegion);//也得做区域区分
            maxNo++;
            DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
            denatorBaseinfo.setBlastserial(maxNo);
            denatorBaseinfo.setSithole(maxNo + "");
            denatorBaseinfo.setShellBlastNo(shellNo);
            denatorBaseinfo.setDelay(delay);
            denatorBaseinfo.setRegdate(Utils.getDateFormat(new Date()));
            denatorBaseinfo.setStatusCode("02");
            denatorBaseinfo.setStatusName("已注册");
            denatorBaseinfo.setErrorCode("FF");
            denatorBaseinfo.setErrorName("");
            denatorBaseinfo.setWire("");//桥丝状态
            denatorBaseinfo.setPiece(mRegion);
            denatorBaseinfo.setDuan(duan);
            denatorBaseinfo.setDuanNo((duanNUM + 1));
            if (detonatorTypeNew != null && !detonatorTypeNew.getDetonatorId().equals("0")) {
                denatorBaseinfo.setDenatorId(detonatorTypeNew.getDetonatorId());
                denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
            }
            //向数据库插入数据
            getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
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
        String addNum = etNum.getText().toString();
        String duan = etDuan.getText().toString();
        if (!StringUtils.isNotBlank(duan)) {
            tipStr = getResources().getString(R.string.text_down_tip18);
            return tipStr;
        }
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
        if (!StringUtils.isNotBlank(edsno) && !StringUtils.isNotBlank(addNum)) {
            tipStr = getResources().getString(R.string.text_scan_cuowu1);//  "结束序列号不能为空";
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
        if (!Utils.isNum(edsno) && !StringUtils.isNotBlank(addNum)) {
            tipStr = getResources().getString(R.string.text_error_tip26);//  "结束序号不是数字";
            return tipStr;
        }
        return tipStr;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("点击项目", "position: " + position);
        Intent intent = new Intent(DownWorkCode.this, ShouQuanActivity.class);
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
//                TextView textview = new TextView(this);
//                textview.setTextSize(25);
//                textview.setTextColor(Color.RED);
//                textview.setText( getResources().getString(R.string.text_down_tip19));
//                textview.setTypeface(null, Typeface.BOLD);
//                AlertDialog dialog2 = new AlertDialog.Builder(this)
//                        .setTitle(getResources().getString(R.string.text_queryHis_dialog1))//设置对话框的标题
//                        .setView(textview)
//                        //设置对话框的按钮
//                        .setPositiveButton(getResources().getString(R.string.text_verify), (dialog3, which) -> {
//                            dialog3.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(DownWorkCode.this);
                            builder.setTitle(getResources().getString(R.string.text_queryHis_dialog1));//"请输入用户名和密码"
                            View view = LayoutInflater.from(DownWorkCode.this).inflate(R.layout.userlogindialog_delete, null);
                            TextView tvTitle = view.findViewById(R.id.tvTitle);
                            tvTitle.setText("请输入密码后,再进行删除授权操作");
                            builder.setView(view);
                            final EditText password = view.findViewById(R.id.password);
                            password.setHint(getResources().getString(R.string.text_mmts));
                            builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String b = password.getText().toString().trim();
                                    if (b == null || b.trim().length() < 1) {
                                        show_Toast(getString(R.string.text_alert_password));
                                        return;
                                    }
                                    if (b.equals("123")) {
                                        delShouQuan(map_dl.get(position).get("id").toString());//删除方法
                                        GreenDaoMaster master = new GreenDaoMaster();
                                        master.deleteTypeLeiGuanFroTime(map_dl.get(position).get("spare2").toString());
                                        if (map_dl != null && map_dl.size() > 0) {//移除map中的值
                                            map_dl.remove(position);
                                        }
                                        mAdapter_sq.notifyDataSetChanged();
                                        show_Toast(getResources().getString(R.string.text_scsq));
                                    } else {
                                        show_Toast(getResources().getString(R.string.text_mmcw));
                                    }
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
//                        })
//                        .setNeutralButton(getResources().getString(R.string.text_alert_cancel), (dialog3, which) -> {
//                            dialog3.dismiss();
//                        })
//                        .create();
//                dialog2.show();
                break;
            case R.id.ly_sq://
            case R.id.tv_chakan_sq:
                Log.e("点击项目", "position: " + position);
                Log.e("点击项目", "id: " + map_dl.get(position).get("id").toString());
                Intent intent = new Intent(DownWorkCode.this, ShouQuanActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sqrq", map_dl.get(position).get("spare2").toString());//申请日期
                bundle.putInt("position", Integer.parseInt(map_dl.get(position).get("id").toString()));
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
    public void insertJson(String htbh, String xmbh, String json, int errNum, String coordxy, String name, String yxq) {
        ContentValues values = new ContentValues();
        values.put("htbh", htbh);
        values.put("xmbh", xmbh);
        values.put("json", json);
        values.put("errNum", errNum);
        values.put("qbzt", "未爆破");
        values.put("dl_state", "未上传");
        values.put("zb_state", "未上传");
        values.put("spare1", name);//项目名称
        values.put("spare2", yxq);//下载日期
        values.put("total", list_uid.size());//总数
        values.put("bprysfz", at_bprysfz.getText().toString().trim());//身份证号
        values.put("coordxy", coordxy.replace("\n", "").replace("，", ",").replace(" ", ""));//经纬度
        if (at_dwdm.getText().toString().trim().length() < 1) {//单位代码
            values.put("dwdm", "");
        } else {
            values.put("dwdm", at_dwdm.getText().toString().trim());
        }

        Log.e("注册数据", "成功");
        db.insert(DatabaseHelper.TABLE_NAME_SHOUQUAN, null, values);
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
    }

    /**
     * 查询所有雷管
     */
    private void loadMoreData_lg(int cp) {
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO;
        Cursor cursor = db.rawQuery(sql, null);//new String[]{(index) + "", pageSize + ""}
        list_all.clear();
        this.currentPage = cp;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                int holeNo = cursor.getInt(2);
                String shellNo = cursor.getString(3);//管壳号
                int delay = cursor.getInt(5);
                String stCode = cursor.getString(6);//状态
                String stName = cursor.getString(7);//
                String errorCode = cursor.getString(9);//状态
                String errorName = cursor.getString(8);//

                VoBlastModel item = new VoBlastModel();
                item.setBlastserial(serialNo);
                item.setSithole(holeNo + "");
                item.setDelay((short) delay);
                item.setShellBlastNo(shellNo);
                item.setErrorCode(errorCode);
                item.setErrorName(errorName);
                item.setStatusCode(stCode);
                item.setStatusName(stName);
                list_all.add(item);
            }
            cursor.close();
            this.currentPage++;
        }
        if (list_all == null) {
            show_Toast(getResources().getString(R.string.text_down_tip20));
        }
        list_uid.clear();
        for (int i = 0; i < list_all.size(); i++) {
            list_uid.add(list_all.get(i).getShellBlastNo());
        }
        Log.e("雷管", "list_uid: " + list_uid.toString());
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
            item.put("spare2", sq.getSpare2());//申请日期
            item.put("total", sq.getTotal());
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
        show_Toast(getResources().getString(R.string.text_del_ok));
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
            show_Toast(getResources().getString(R.string.text_down_err1));
            return false;
        }
        if (equ_no.length() < 1) {
            show_Toast(getResources().getString(R.string.text_down_err2));
            return false;
        }
        if (at_coordxy.getText().toString().trim().length() < 1) {
            show_Toast(getResources().getString(R.string.text_down_err3));
            return false;
        }
        if (sfz.length() < 18) {
            show_Toast(getResources().getString(R.string.text_down_err5));
            return false;
        }
        if (!at_coordxy.getText().toString().trim().contains(",")) {
            show_Toast(getResources().getString(R.string.text_down_err6));
            return false;
        }
        if (at_coordxy.getText().toString().trim().contains("4.9E-")) {
            show_Toast(getResources().getString(R.string.text_down_err7));
            return false;
        }
        if (StringUtils.isBlank(tx_htid) && StringUtils.isBlank(tv_xmbh) && StringUtils.isBlank(tv_dwdm)) {
            show_Toast(getResources().getString(R.string.text_down_err8));
            return false;
        }
        if (tx_htid.length() != 0 && tx_htid.length() < 15) {
            Log.e("验证", "tx_htid.length(): " + tx_htid.length());
            Log.e("验证", "tx_htid: " + tx_htid);
            show_Toast(getResources().getString(R.string.text_down_err9));
            return false;
        }
        return true;
    }


    @OnClick({R.id.btn_down_return, R.id.btn_down_inputOK, R.id.btn_down_workcode, R.id.btn_inputOk,
            R.id.btn_inputGKM, R.id.btn_location, R.id.btn_scanReister, R.id.btn_setdelay,R.id.btn_down_offline,
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
                int sqTotal = (int) getDaoSession().getDetonatorTypeNewDao().count();
                AppLogUtils.writeAppLog("授权记录总数:" + sqTotal);
                if (sqTotal < 1) {
                    show_Toast(getResources().getString(R.string.text_his_zwsqsc));
                    return;
                }
//                TextView textview = new TextView(this);
//                textview.setTextSize(25);
//                textview.setTextColor(Color.RED);
//                textview.setText( getResources().getString(R.string.text_down_dialog2));
//                textview.setTypeface(null, Typeface.BOLD);
//                AlertDialog dialog2 = new AlertDialog.Builder(this)
//                        .setTitle(getResources().getString(R.string.text_down_dialog1))
////                        .setMessage(getResources().getString(R.string.text_down_dialog2))
//                        .setView(textview)
//                        //设置对话框的按钮
//                        .setNeutralButton(getResources().getString(R.string.text_alert_cancel), (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton(getResources().getString(R.string.text_verify), (dialog, which) -> {
//                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(DownWorkCode.this);
                            builder.setTitle(getResources().getString(R.string.text_queryHis_dialog12));//"请输入用户名和密码"
                            View v = LayoutInflater.from(DownWorkCode.this).inflate(R.layout.userlogindialog_delete, null);
                            TextView tvTitle = v.findViewById(R.id.tvTitle);
                            tvTitle.setText(getResources().getString(R.string.text_his_qksqjl));
                            builder.setView(v);
                            final EditText password = v.findViewById(R.id.password);
                            password.setHint(getResources().getString(R.string.text_mmts));
                            builder.setPositiveButton(getString(R.string.text_alert_sure), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String b = password.getText().toString().trim();
                                    if (b == null || b.trim().length() < 1) {
                                        show_Toast(getString(R.string.text_alert_password));
                                        return;
                                    }
                                    if (b.equals("123")) {
                                        GreenDaoMaster.delAllMessage();//清空数据
                                        GreenDaoMaster.delAllDetonatorTypeNew();//清空授权数据
                                        mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据
                                        show_Toast(getResources().getString(R.string.text_qksq));
                                    } else {
                                        show_Toast(getResources().getString(R.string.text_mmcw));
                                    }
                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton(getString(R.string.text_alert_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
//                        }).create();
//                dialog2.show();
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
                        .setTitle(getResources().getString(R.string.text_down_dialog4))//设置对话框的标题//"成功起爆"
                        .setMessage(getResources().getString(R.string.text_down_dialog8))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNeutralButton(getResources().getString(R.string.text_down_dialog6), (dialog1, which) -> dialog1.dismiss())
                        .setPositiveButton(getResources().getString(R.string.text_down_dialog7), (dialog12, which) -> {
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
                    btnInputGKM.setText(getResources().getString(R.string.text_ycnr));
                    lyInput.setVisibility(View.VISIBLE);
                    lvShouquan.setVisibility(View.GONE);
                } else {
                    lyInput.setVisibility(View.GONE);
                    lvShouquan.setVisibility(View.VISIBLE);
                    btnInputGKM.setText(getResources().getString(R.string.text_down_srgkm));
                }
//                Intent intent = new Intent(this,ReisterMainPage_scan.class);
//                startActivity(intent);
                break;
            case R.id.btn_location://启动定位
                if (jd.equals("")) {
                    show_Toast(getResources().getString(R.string.text_down_show1));
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
                    String addNum = etNum.getText().toString();
                    pb_show = 1;
                    runPbDialog();
                    if (addNum.length() > 0) {
                        if (Integer.parseInt(addNum) > 500) {
                            show_Toast(getResources().getString(R.string.text_scan_cuowu8));
                            return;
                        }
                        if (edsno.length() > 1) {
                            show_Toast(getResources().getString(R.string.text_scan_bntssr));
                            return;
                        }
                        final int num = Integer.parseInt(addNum);//连续注册个数

                        new Thread(() -> {
                            insertDenator(prex, start, start + (num - 1));
                        }).start();
                        return;
                    } else {
                        int end = Integer.parseInt(edsno);
                        if (end < start) {
                            show_Toast(getResources().getString(R.string.text_error_tip27));//  "结束序号不能小于开始序号";
                            return;
                        }
                        if (edsno.length() < 5) {
                            show_Toast(getResources().getString(R.string.text_js5));//  "结束序号不能小于开始序号";
                            return;
                        }
                        if (start < 0 || end > 99999) {
                            show_Toast(getResources().getString(R.string.text_error_tip28));//  "起始/结束序号不符合要求";
                            return;
                        }
                        if ((end - start) > 1000) {
                            show_Toast(getResources().getString(R.string.text_error_tip29));//  "每一次注册数量不能大于1000";
                            return;
                        }
                        new Thread(() -> {
                            insertDenator(prex, start, end);
                        }).start();
                    }


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
            case R.id.btn_down_offline:
                Intent offIntent = new Intent(this, DownOfflineActivity.class);
                startActivity(offIntent);
                break;
        }
    }


    private void deleteHistory(String field, AutoCompleteTextView auto) {
//        MMKV kv = MMKV.mmkvWithID("network_url");
//        kv.encode(field, "");
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        sp.edit().putString(field, "").apply();
        initAutoComplete(field, auto);
        show_Toast(getResources().getString(R.string.text_down_show2));
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

        switch (item.getItemId()) {

            case R.id.item_1:
            case R.id.item_2:
            case R.id.item_3:
            case R.id.item_4:
            case R.id.item_5:
                // 区域 更新视图
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                // 显示提示
                show_Toast(getResources().getString(R.string.text_show_1) + mRegion);
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
            str = getString(R.string.text_list_piace) + region;
        } else {
            str = getString(R.string.text_list_piace) + region + getString(R.string.text_gong) + size + ")";
        }
        // 设置标题
        getSupportActionBar().setTitle(mOldTitle + str);

        totalbar_title.setText(mOldTitle + str);
        Log.e("liyi_Region", "已选择" + str);
    }

    /**
     * 设置标题区域
     */
    private void setTitleRegionNew(List<Integer> idList, int size) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String result = idList.stream()
                    .map(String::valueOf)  // 转换为字符串
                    .collect(Collectors.joining(","));
            String str;
            if (size == -1) {
                str = getString(R.string.text_list_piace) + result;
            } else {
                str = getString(R.string.text_list_piace) + result + getString(R.string.text_gong) + size + ")";
            }
            // 设置标题
            getSupportActionBar().setTitle(mOldTitle + str);

            title_lefttext.setText(mOldTitle + str);
            Log.e("liyi_Region", "已选择" + str);
        }
    }



    /***
     * 得到某段的总数
     * @return
     */
    private int getDuanNo(int duan, String piece) {
        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO + " where duan =? and piece = ? ", new String[]{duan + "", piece});
        int totalNum = cursor.getCount();//得到数据的总条数
        cursor.close();
        return totalNum;
    }

    private void choiceQuYu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle(R.string.text_dialog_choice);
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
                show_Toast(getString(R.string.text_suidao_tip));
            }

        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

}
