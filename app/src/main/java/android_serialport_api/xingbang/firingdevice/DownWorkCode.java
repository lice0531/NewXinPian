package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
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
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.tencent.mmkv.MMKV;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.MlistView;
import android_serialport_api.xingbang.custom.ShouQuanAdapter;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.ShouQuan;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.services.LocationService;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.AMapUtils;
import android_serialport_api.xingbang.utils.LngLat;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android_serialport_api.xingbang.Application.getContext;


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
    ListView listView;
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
    private ShouQuanAdapter mAdapter;
    private SQLiteDatabase db;
    private Handler mHandler_httpresult;
    private Handler mHandler_1 = new Handler();//提示电源信息
    private Handler mHandler2;
    private String selectDenatorId;

    private List<Map<String, Object>> map_dl = new ArrayList<>();
    private int pageSize = 500;//每页显示的数据
    private int currentPage = 1;//当前页数
    private List<VoBlastModel> list = new ArrayList<>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_workcode);
        ButterKnife.bind(this);
        DatabaseHelper mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 21);
        db = mMyDatabaseHelper.getReadableDatabase();
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

        if (1 == currentPage) {
            loadMoreData();
        }
        loadMoreData_lg(currentPage);//查询所有雷管
        mAdapter = new ShouQuanAdapter(this, map_dl, R.layout.item_list_shouquan);
        mAdapter.setOnInnerItemOnClickListener(this);
        lvShouquan.setAdapter(mAdapter);
        lvShouquan.setOnItemClickListener(this);

        list_adapter = new SimpleCursorAdapter(
                DownWorkCode.this,
                R.layout.item_blast,
                null,
                new String[]{"blastserial", "sithole", "delay", "shellBlastNo"},
                new int[]{R.id.blastserial, R.id.sithole, R.id.delay, R.id.shellBlastNo},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(list_adapter);
        getLoaderManager().initLoader(0, null, this);
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

        at_projectName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideInputKeyboard();
                String project_name = parent.getAdapter().getItem(position).toString();
                Log.e("输入项目", "position: "+position );
                Log.e("输入项目", "project_name: "+project_name );
                Log.e("输入项目", "getItem(position): "+parent.getAdapter().getItem(position).toString() );
                GreenDaoMaster master = new GreenDaoMaster();
                List<Project> list = master.queryProjectToProject_name(project_name);

                Log.e("输入项目", "list: "+list.toString() );
                if(list.size()>0){
                    if (list.get(0).getHtbh().length() > 0) {
                        at_htid.setText(list.get(0).getHtbh());
                    }else {
                        at_htid.setText("");

                    }
                    if (list.get(0).getXmbh().length() > 0) {
                        at_xmbh.setText(list.get(0).getXmbh());
                    }else {
                        at_xmbh.setText("");

                    }
                    if (list.get(0).getDwdm().length() > 0) {
                        at_dwdm.setText(list.get(0).getDwdm());
                    }else {
                        at_dwdm.setText("");

                    }
                    if (list.get(0).getCoordxy().length() > 0) {
                        at_coordxy.setText(list.get(0).getCoordxy());
                    }else {
                        at_coordxy.setText("");

                    }
                    at_bprysfz.setText(list.get(0).getBprysfz());
                }

                saveData();
                initView();//把输入框颜色初始化
            }
        });

        at_htid.addTextChangedListener(htbh_watcher);//长度监听
        at_xmbh.addTextChangedListener(xmbh_watcher);//长度监听
        at_dwdm.addTextChangedListener(dwdm_watcher);//长度监听
        at_bprysfz.addTextChangedListener(sfz_watcher);//长度监听

    }
    private void initView(){
        Resources resources = getContext().getResources();
        Drawable btnDrawable = resources.getDrawable(R.drawable.translucent);
        at_htid.setBackground(btnDrawable);
        at_xmbh.setBackground(btnDrawable);
        at_dwdm.setBackground(btnDrawable);
        at_coordxy.setBackground(btnDrawable);
        at_bprysfz.setBackground(btnDrawable);
    }

    private void initHandle() {
        mHandler_1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (tipInfoFlag == 3) {//未收到关闭电源命令
                    show_Toast(getResources().getString(R.string.text_error_tip5));
                }
                if (tipInfoFlag == 4) {//未收到打开电源命令
                    show_Toast(getResources().getString(R.string.text_error_tip6));
                }
                if (tipInfoFlag == 5) {//桥丝不正常
                    show_Toast(getResources().getString(R.string.text_error_tip7));
                }
                if (tipInfoFlag == 89) {//刷新界面
                    show_Toast("输入的管壳码重复");
                }
                super.handleMessage(msg);
            }
        };

        mHandler_httpresult = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                loadMoreData();//读取数据
                mAdapter.notifyDataSetChanged();
            }
        };
        mHandler2 = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                //显示或隐藏loding界面
                if (pb_show == 1 && tipDlg != null) tipDlg.show();
                if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
                super.handleMessage(msg);

            }
        };
        mHandler_3 = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
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
                super.handleMessage(msg);
            }
        };
    }

    //获取配置文件中的值
    private void getPropertiesData() {
        mProp = PropertiesUtil.getInstance(this);
        mProp.open();
        pro_bprysfz = mProp.readString("pro_bprysfz", "");//证件号码
        pro_htid = mProp.readString("pro_htid", "");//合同号码
        pro_xmbh = mProp.readString("pro_xmbh", "");//项目编号
        pro_coordxy = mProp.readString("pro_coordxy", "");//经纬度
        pro_dwdm = mProp.readString("pro_dwdm", "");//单位代码
        equ_no = mProp.readString("equ_no", "");//设备编号

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                insertDenator(prex, finalStrNo, finalEndNo);//添加
            }
        }).start();
    }

    /**
     * 初始化AutoCompleteTextView，最多显示5项提示，使
     * AutoCompleteTextView在一开始获得焦点时自动提示
     *
     * @param field 保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
    private void initAutoComplete(String field, AutoCompleteTextView auto) {
        SharedPreferences sp = getSharedPreferences("network_url", 0);
//        MMKV kv = MMKV.mmkvWithID("network_url");
//        kv.importFromSharedPreferences(sp);
        String longhistory = sp.getString(field, "当前无记录");
        String[] hisArrays = longhistory.split("#");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_auto_textview, hisArrays);
        auto.setAdapter(adapter);
        auto.setDropDownHeight(500);
        auto.setDropDownWidth(450);
        auto.setThreshold(1);
        auto.setCompletionHint("最近的20条记录");
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
    }

    /**
     * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符段
     *
     * @param field 保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
    private void saveHistory(String field, AutoCompleteTextView auto) {
        String text = auto.getText().toString();
        Log.e("保存输入框历史", "text: " + text);//MMKV保存为空,有空再排错
//        MMKV mmkv_his = MMKV.mmkvWithID("network_url");
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        String longhistory = sp.getString(field, "");
        if (!longhistory.contains(text + "#")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + "#");
            sp.edit().putString(field, sb.toString()).apply();
        }
    }

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
//        String url = Utils.httpurl_down_dl;//丹灵下载
        String url = Utils.httpurl_down;//丹灵下载
        OkHttpClient client = new OkHttpClient();

        JSONObject object = new JSONObject();
        String sfz = at_bprysfz.getText().toString().trim().replace(" ", "");//证件号码
        String tx_htid = at_htid.getText().toString().trim().replace(" ", "");//合同编号 15位
        String tv_xmbh = at_xmbh.getText().toString().trim().replace(" ", "");//项目编号
        final String xy[] = at_coordxy.getText().toString().replace("\n", "").replace("，", ",").replace(" ", "").split(",");//经纬度
        String tv_dwdm = at_dwdm.getText().toString().trim();//单位代码 13位

        //四川转换规则
        if (list_uid != null && list_uid.get(0).length() < 14) {
            for (int i = 0; i < list_uid.size(); i++) {
                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan(list_uid.get(i)));//替换
//                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan_new(list_uid.get(i)));//替换
            }
        }
        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e("uid4", uid);
        try {
            object.put("sbbh", equ_no);//起爆器设备编号XBTS0003
            object.put("jd", xy[0]);//经度
            object.put("wd", xy[1]);//纬度
            object.put("uid", uid);//雷管uid
            object.put("xmbh", tv_xmbh);//项目编号370101318060006
            object.put("htid", tx_htid);//合同编号370100X15040027
            object.put("dwdm", tv_dwdm);//单位代码
            Log.e("上传信息", object.toString());
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
                pb_show = 0;
                String res;
                try {
                    res = new String(MyUtils.decryptMode(key.getBytes(), Base64.decode(response.body().string().toString(), Base64.DEFAULT)));
                } catch (Exception e) {
                    show_Toast_ui("丹灵系统异常，请与丹灵管理员联系后再尝试下载");
                    return;
                }
                Log.e("网络请求", "res: " + res);
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
                                    Log.e("经纬度", "juli3: "+juli3 );
                                    if (juli3 < banjing) {
                                        insertJson(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
//                                        insertJson_new(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
                                    }
                                }
                            }
                        }
                        mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据
                        if (err != 0) {
                            Log.e("下载", "err: " + err);
//                            show_Toast_ui(danLingBean.getZbqys().getZbqy().get(0).getZbqymc() + "下载的雷管出现错误,请检查数据");
                        }
                        show_Toast_ui("项目下载成功");
                    } else if (cwxx.equals("1")) {
                        show_Toast_ui(object1.getString("cwxxms"));
                    } else if (cwxx.equals("2")) {
                        show_Toast_ui("未找到该起爆器设备信息或起爆器未设置作业任务");
                    } else if (cwxx.equals("3")) {
                        show_Toast_ui("该起爆器未设置作业任务");
                    } else if (cwxx.equals("4")) {
                        show_Toast_ui("起爆器在黑名单中");
                    } else if (cwxx.equals("5")) {
                        show_Toast_ui("起爆位置不在起爆区域内 ");
                    } else if (cwxx.equals("6")) {
                        show_Toast_ui("起爆位置在禁爆区域内");
                    } else if (cwxx.equals("7")) {
                        show_Toast_ui("该起爆器已注销/报废");
                    } else if (cwxx.equals("8")) {
                        show_Toast_ui("禁爆任务");
                    } else if (cwxx.equals("9")) {
                        show_Toast_ui("作业合同存在项目");
                    } else if (cwxx.equals("10")) {
                        show_Toast_ui("作业任务未设置准爆区域");
                    } else if (cwxx.equals("11")) {
                        show_Toast_ui("离线下载不支持生产厂家试爆");
                    } else if (cwxx.equals("12")) {
                        show_Toast_ui("营业性单位必须设置合同或者项目");
                    } else if (cwxx.equals("99")) {
                        show_Toast_ui(danLingBean.getCwxxms());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        /***
         * 发送初始化命令
         */
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
        if (coordxy == null || coordxy.trim().length() < 8 || coordxy.indexOf(",") < 5) {
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
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                at_coordxy.setText(jd_5.getText().toString() + jd_4.getText().toString() + "," + wd_5.getText().toString() + wd_4.getText().toString() + "");
                dialog.dismiss();
            }
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
        int maxNo = getMaxNumberNo();
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
            int index = getEmptyDenator(-1);
            if (index < 0) {//说明没有空余的序号可用
                maxNo++;
                values.put("blastserial", maxNo);
                values.put("sithole", maxNo);
                values.put("shellBlastNo", shellNo);
                values.put("delay", 0);
                values.put("regdate", Utils.getDateFormatLong(new Date()));
                values.put("statusCode", "02");
                values.put("statusName", "已注册");
                values.put("errorCode", "");
                values.put("errorName", "");
                values.put("wire", "");//桥丝状态
                //向数据库插入数据
                db.insert("denatorBaseinfo", null, values);
            } else {
                values = new ContentValues();
                values.put("shellBlastNo", shellNo);//key为字段名，value为值
                values.put("statusCode", "");
                values.put("statusName", "");
                values.put("regdate", Utils.getDateFormatLong(new Date()));
                values.put("statusCode", "02");
                values.put("statusName", "已注册");
                values.put("errorCode", "");
                values.put("errorName", "");
                db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + index});

            }
            Utils.saveFile();//把软存中的数据存入磁盘中
            reCount++;
        }
        getLoaderManager().restartLoader(1, null, this);
        pb_show = 0;
        if (flag == 0) tipInfoFlag = 88;
        mHandler_1.sendMessage(mHandler_1.obtainMessage());
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
        divider.setBackgroundColor(Color.TRANSPARENT);
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
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.ly_sq://
                Log.e("点击项目", "position: " + map_dl.get(position));
                Intent intent = new Intent(DownWorkCode.this, ShouQuanLegActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list_dl", (Serializable) map_dl);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.tv_chakan_sq://
                Log.e("点击项目", "position: " + map_dl.get(position));
                Intent intent2 = new Intent(DownWorkCode.this, ShouQuanLegActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("list_dl", (Serializable) map_dl);
                bundle2.putInt("position", position);
                intent2.putExtras(bundle2);
                startActivity(intent2);
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
        ShouQuan sq= new ShouQuan();
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
    private void loadMoreData_lg(int cp) {
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO;
        Cursor cursor = db.rawQuery(sql, null);//new String[]{(index) + "", pageSize + ""}
        list.clear();
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
                item.setSithole(holeNo);
                item.setDelay((short) delay);
                item.setShellBlastNo(shellNo);
                item.setErrorCode(errorCode);
                item.setErrorName(errorName);
                item.setStatusCode(stCode);
                item.setStatusName(stName);
                list.add(item);
            }
            cursor.close();
            this.currentPage++;
        }
        if (list == null) {
            show_Toast("请注册雷管!");
        }
        list_uid.clear();
        for (int i = 0; i < list.size(); i++) {
            list_uid.add(list.get(i).getShellBlastNo());
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

    private void loadMoreData() {
        map_dl.clear();
//        List<ShouQuan> list = LitePal.findAll(ShouQuan.class);//ErrNum总是为空
//        Log.e("查询", "list getErrNum: "+list.get(0).getErrNum() );
//        Gson gson = new Gson();
//        DanLingBean danLingBean;
//        Log.e("查询", "ErrNum: "+list.get(0).getErrNum() );
//        for (ShouQuan sq : list) {
//            danLingBean = gson.fromJson(sq.getJson(), DanLingBean.class);
//            Map<String, Object> item = new HashMap<String, Object>();
//            item.put("id", sq.getId());
//            item.put("htbh", sq.getHtbh());
//            item.put("xmbh", sq.getXmbh());
//            item.put("qbzt", sq.getQbzt());
//            item.put("errNum", sq.getErrNum());
//            item.put("coordxy", sq.getCoordxy());
//            item.put("spare1", sq.getSpare1());
//            item.put("danLingBean", danLingBean);
//            map_dl.add(item);
//        }
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_SHOUQUAN;//+" order by htbh "
        Cursor cursor = db.rawQuery(sql, null);
        //return getCursorTolist(cursor);
        if (cursor != null) {
            Gson gson = new Gson();
            DanLingBean danLingBean;
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String xmbh = cursor.getString(1); //获取第二列的值 ,序号
                String htbh= cursor.getString(2);
                String json = cursor.getString(3);//管壳号
                String errNum = cursor.getString(4);//错误数量
                String qbzt = cursor.getString(5);//起爆状态
                String coordxy = cursor.getString(11);//经纬度
                String spare1 = cursor.getString(13);//工程名称
                danLingBean = gson.fromJson(json, DanLingBean.class);
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", id);
                item.put("htbh", htbh);
                item.put("xmbh", xmbh);
                item.put("qbzt", qbzt);
                item.put("spare1", spare1);
                item.put("coordxy", coordxy);
                item.put("errNum", errNum);
                item.put("danLingBean", danLingBean);
                map_dl.add(item);
            }
            cursor.close();
        }
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
                if (lySetUpData.getVisibility() == View.GONE) {
                    btnDownReturn.setText("隐藏内容");
                    lySetUpData.setVisibility(View.VISIBLE);
                } else {
                    lySetUpData.setVisibility(View.GONE);
                    btnDownReturn.setText("添加项目");
                }
                break;
            case R.id.btn_down_inputOK://保存
//                hideInputKeyboard();//隐藏键盘
//                saveData();
//                show_Toast("数据保存成功");
                Intent intent = new Intent(this, SaveProjectActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_down_workcode://下载
                loadMoreData_lg(currentPage);
                saveData();
                hideInputKeyboard();//隐藏键盘,取消焦点
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("下载提示")//设置对话框的标题//"成功起爆"
                        .setMessage("请确认项目编号,地理位置等信息输入无误后,点击确认下载")//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNegativeButton("再次确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确认下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (checkMessage()) {//校验输入的项目信息是否和法
                                    upload();
                                } else {
                                    return;
                                }


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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            insertDenator(prex, start, end);
                            Log.e("添加码", "prex: " + prex);
                            Log.e("添加码", "start: " + start);
                            Log.e("添加码", "end: " + end);
                        }
                    }).start();
                    loadMoreData_lg(currentPage);//查询所有雷管
                    // int reCount = insertDenator(prex,start,end);
                    //tipDlg.dismiss();
                    // pb_show = 0;

                    // Toast.makeText(ReisterMainPage_scan.this, "本次注册雷管数量为:"+reCount, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, checstr1, Toast.LENGTH_SHORT).show();
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
                at_xmbh.setBackgroundColor(Color.GREEN);
            } else {
                at_xmbh.setBackgroundColor(Color.RED);
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
}
