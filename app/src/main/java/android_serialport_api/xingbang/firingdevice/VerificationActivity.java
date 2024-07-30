package android_serialport_api.xingbang.firingdevice;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.LoadListView;
import android_serialport_api.xingbang.custom.VerificationAdapter;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.ShouQuan;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.services.LocationService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 验证爆破范围页面
 */
public class VerificationActivity extends BaseActivity implements AdapterView.OnItemClickListener, LoadListView.OnLoadMoreListener {

    @BindView(R.id.lv_yanzheng)
    LoadListView lvYanzheng;
    @BindView(R.id.tv_yz_jd)
    EditText tvYzJd;
    @BindView(R.id.ll_yz_dw)
    LinearLayout llYzDw;
    @BindView(R.id.btn_yz)
    Button btnYz;
    @BindView(R.id.btn_yz_dw)
    Button btnYzDw;

    private VerificationAdapter mAdapter;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private int totalNum;//总的数据条数
    private int lg_totalNum;//雷管总的数据条数
    private int pageSize = 600;//每页显示的数据
    private int totalPage;//总的页数
    private int currentPage = 1;//当前页数
    private Handler mHandler_httpresult;
    private double bendi_jd;
    private double bendi_wd;
    private String dqdz;//当前地址
    //高德地图
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private List<Map<String, Object>> map_dl = new ArrayList<>();
    private String qbxm_id = "1";
    private String qbxm_name = "1";
    private List<VoBlastModel> list_data = new ArrayList<>();
    private LocationService locationService;
    private String mRegion;     // 区域

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //获取区号
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");

        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        loadShouQuan();
        loadMoreData();

        mAdapter = new VerificationAdapter(this, map_dl, R.layout.item_list_shouquan);
        lvYanzheng.setAdapter(mAdapter);
        lvYanzheng.setOnItemClickListener(this);
        lvYanzheng.setLoadMoreListener(this);

//        dingwei();//高德定位

//        baidudingwei();//百度定位
        mHandler_httpresult = new Handler(msg -> {
            Bundle bundle = msg.getData();
            mAdapter.notifyDataSetChanged();
            return false;
        });

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
                    tvYzJd.setText(location.getLongitude() + "," + location.getLatitude() + "");
                    dqdz = location.getLongitude() + "," + location.getLatitude() + "";
                    bendi_jd = location.getLongitude();
                    bendi_wd = location.getLatitude();
                } else {
//                    tvYzJd.setText("定位失败,可手动输入经纬度");
                }
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
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
                        Poi poi = location.getPoiList().get(i);
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
                Log.e("百度地图", "定位: " + sb.toString());
            }
        }

    };

    private void dingwei() {
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                        tvYzJd.setText(aMapLocation.getLongitude() + "," + aMapLocation.getLatitude() + "");
                        dqdz = aMapLocation.getLongitude() + "," + aMapLocation.getLatitude() + "";
                        bendi_jd = aMapLocation.getLongitude();
                        bendi_wd = aMapLocation.getLatitude();
                        Log.e("定位", "经纬度: " + aMapLocation.getLongitude() + "---" + aMapLocation.getLatitude());
                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                        show_Toast("GPS 定位失败，由于设备当前 GPS 状态差,建议持设备到相对开阔的露天场所再次尝试");
                    }
                }
            }
        };
        //初始化定位 高德地图定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        AMapLocationClientOption option = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void loadShouQuan() {
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

    }

    public int updataState(String id) {
        Log.e("更新起爆状态", "id: " + id);
        ContentValues values = new ContentValues();
        values.put("qbzt", "已起爆");
        db.update(DatabaseHelper.TABLE_NAME_SHOUQUAN, values, "id=?", new String[]{"" + id});
        return 1;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String sqrq = map_dl.get(position).get("spare2").toString();
        List<DetonatorTypeNew> mListData = new GreenDaoMaster().queryDetonatorShouQuanForSqrq(sqrq);
        ArrayList<String> list_lg_down = new ArrayList<>();
        ArrayList<String> list_lg2 = new ArrayList<>();
        for (int i = 0; i < mListData.size(); i++) {
            list_lg_down.add(mListData.get(i).getShellBlastNo());
        }
        for (int i = 0; i < list_data.size(); i++) {
            list_lg2.add(list_data.get(i).getShellBlastNo());//UID和管壳码一致
//            list_lg2.add(Utils.ShellNo13toSiChuan(list_data.get(i).getShellBlastNo()));//四川编码规则
//            list_lg2.add(Utils.ShellNo13toSiChuan_new(list_data.get(i).getShellBlastNo()));//四川包工定的编码规则
        }
        for (int i = 0; i < list_lg2.size(); i++) {
            if (!list_lg_down.contains(list_lg2.get(i))) {
                Log.e("对比", "list_lg_down: " + list_lg_down + "---list_lg2.get(i)" + list_lg2.get(i));
                show_Toast("注册雷管与下载雷管不符");
                return;
            }
        }
        show_Toast("在准爆范围内,可以起爆");
        Log.e("验证数据", "map_dl.get(position): " + map_dl.get(position).toString());
        Log.e("验证数据", "map_dl.get(position).get(id): " + map_dl.get(position).get("id"));
        qbxm_id = map_dl.get(position).get("id") + "";
        qbxm_name = map_dl.get(position).get("spare1").toString();
        Intent intent = new Intent(this, FiringMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("qbxm_id", qbxm_id);
        bundle.putString("qbxm_name", qbxm_name);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void loadMore() {
        lvYanzheng.setFooterGone();
    }


    @Override
    protected void onDestroy() {
//        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
//        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        super.onDestroy();
    }

    @OnClick({R.id.ll_yz_dw, R.id.btn_yz, R.id.btn_yz_dw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_yz_dw:
                //启动定位
                mLocationClient.startLocation();
                break;
            case R.id.btn_yz:
                Intent intent = new Intent(this, FiringMainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("qbxm_id", qbxm_id);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_yz_dw:
                //启动定位
                mLocationClient.startLocation();
                break;
        }
    }

    /**
     * 加载数据
     */
    private void loadMoreData() {

        List<DenatorBaseinfo> denatorBaseinfos = new GreenDaoMaster().queryDetonatorRegionAsc();
        //int count=0;
        for (DenatorBaseinfo a : denatorBaseinfos) {
            VoBlastModel item = new VoBlastModel();
            item.setBlastserial(a.getBlastserial());
            item.setDelay((short) a.getDelay());
            item.setShellBlastNo(a.getShellBlastNo());
            item.setDenatorId(a.getDenatorId());
            item.setDenatorIdSup(a.getDenatorIdSup());
            item.setZhu_yscs(a.getZhu_yscs());
            item.setCong_yscs(a.getCong_yscs());
            list_data.add(item);
        }


    }


}
