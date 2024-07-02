package android_serialport_api.xingbang.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.orhanobut.logger.Logger;

import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.databinding.ActivityGpsDemoBinding;
import android_serialport_api.xingbang.databinding.ActivityLoginBinding;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.utils.BaiduUtils;
import android_serialport_api.xingbang.utils.MmkvUtils;

/**
 * 单次定位 and 连续定位
 */
public class GpsDemoActivity extends CheckPermissionsActivity implements View.OnClickListener {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView mOneLocTV;
    private TextView mContinuoueLocTV;
    private TextView gpsTvTip;
    private TextView gpsTvjingdu;
    private TextView gpsTvweidu;
    private Button saveGps;
    private boolean isFirstLoc = true;
    private int mCount = 0;
    private LocationClient mLocClientOne = null;
    private LocationClient mLocClientContinuoue = null;
    private Button mOneLocationBt;
    private Button mContinuoueLocaionBt;
    private BitmapDescriptor mBitmapRed = BitmapDescriptorFactory.fromResource(R.drawable.marker);
    private BitmapDescriptor mBitmapBlue = BitmapDescriptorFactory.fromResource(R.drawable.markerblue);
    private Marker mContinuoueLocMarker = null;
    private Marker mOneLocMarker = null;
    ActivityGpsDemoBinding binding;
    String jingdu;
    String weidu;
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String pro_name = "";//项目名称
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGpsDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mOneLocationBt = findViewById(R.id.one_location);
        mOneLocTV = findViewById(R.id.one_loc_tv);
        mContinuoueLocaionBt = findViewById(R.id.continuous_location);
        mContinuoueLocTV = findViewById(R.id.continuoue_loc_tv);
        mMapView = findViewById(R.id.map_view);
        gpsTvTip = findViewById(R.id.gps_tv_tip);
        gpsTvjingdu = findViewById(R.id.gps_tv_jingdu);
        gpsTvweidu = findViewById(R.id.gps_tv_weidu);
        saveGps = findViewById(R.id.gps_btn_saveGps);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mOneLocationBt.setOnClickListener(this);
        mContinuoueLocaionBt.setOnClickListener(this);
        saveGps.setOnClickListener(this);
        startContinuoueLocaton();
        getUserMessage();
        initAutoComplete("history_projectName", binding.downAtProjectName);
    }
    //获取用户信息
    private void getUserMessage() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<Project> projects = master.queryProjectIsSelected("true");
        if(projects.size()>0){
            pro_name=projects.get(0).getProject_name();
        }
        binding.downAtProjectName.setText(pro_name);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.one_location:
                String startStr = getResources().getString(R.string.start_one_Loc);
                String stopStr = getResources().getString(R.string.stop_one_Loc);
                if (mOneLocationBt.getText().equals(startStr)) {
                    startOneLocaton();
                    mOneLocationBt.setText(stopStr);
                } else {
                    stopOneLocaton();
                    mOneLocationBt.setText(startStr);
                }
                break;
            case R.id.continuous_location:
                String startContinueStr = getResources().getString(R.string.start_continue_Loc);
                String stopContinueStr = getResources().getString(R.string.stop_continue_Loc);
                if (mContinuoueLocaionBt.getText().equals(startContinueStr)) {
                    startContinuoueLocaton();
                    mContinuoueLocaionBt.setText(stopContinueStr);
                } else {
                    stopContinuoueLocaton();
                    mContinuoueLocaionBt.setText(startContinueStr);
                }
                break;
            case R.id.gps_btn_saveGps:
                stopContinuoueLocaton();
                MmkvUtils.savecode("jingdu",jingdu);
                MmkvUtils.savecode("weidu",weidu);
                if(binding.downAtProjectName.getText().toString().length()>0){
                    GreenDaoMaster greenDaoMaster = new GreenDaoMaster();
                    List<Project> list_pro= greenDaoMaster.queryProjectToProject_name(binding.downAtProjectName.getText().toString());
                    Project project =list_pro.get(0);
                    project.setCoordxy(jingdu+","+weidu);
                    greenDaoMaster.updateProject(project);
                    Logger.e("项目名称"+binding.downAtProjectName.getText().toString());
                    Logger.e("项目"+list_pro.get(0).toString());
                    Logger.e("保存-保存    "+ "经度: "+jingdu+"纬度:"+weidu);
                    Logger.e("保存"+ "经度: "+MmkvUtils.getcode("jingdu","0.0")+"纬度:"+MmkvUtils.getcode("weidu","0.0") );
                    show_Toast("保存成功");
                }else {
                    show_Toast("请先设置项目");
                }

                break;
            default:
                break;
        }
    }

    /**
     * 启动单次定位
     */
    private void startOneLocaton() {
        mLocClientOne = new LocationClient(this);
        mLocClientOne.registerLocationListener(oneLocationListener);
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        locationClientOption.setCoorType("bd09ll");
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        locationClientOption.setScanSpan(0);
        // 设置是否进行单次定位，单次定位时调用start之后会默认返回一次定位结果
        locationClientOption.setOnceLocation(true);
        //可选，设置是否使用gps，默认false
        locationClientOption.setOpenGps(true);
        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为true
        locationClientOption.setIsNeedAddress(true);
        // 设置定位参数
        mLocClientOne.setLocOption(locationClientOption);
        // 开启定位
        mLocClientOne.start();
    }

    /**
     * 停止单次定位
     */
    private void stopOneLocaton() {
        if (null != mLocClientOne) {
            mLocClientOne.stop();
        }
    }

    /**
     * 启动连续定位
     */
    private void startContinuoueLocaton() {
        // 定位初始化
        mLocClientContinuoue = new LocationClient(this);
        mLocClientContinuoue.registerLocationListener(continuoueLocationListener);
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        locationClientOption.setCoorType("bd09ll");
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        locationClientOption.setScanSpan(3000);
        //可选，设置是否使用gps，默认false
        locationClientOption.setOpenGps(true);
        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为tru
        locationClientOption.setIsNeedAddress(true);
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation
        locationClientOption.setIsNeedLocationPoiList(true);
        // 设置定位参数
        mLocClientContinuoue.setLocOption(locationClientOption);
        // 开启定位
        mLocClientContinuoue.start();
    }

    /**
     * 停止连续定位
     */
    private void stopContinuoueLocaton() {
        if (null != mLocClientContinuoue) {
            mLocClientContinuoue.stop();
            isFirstLoc = true;
        }
    }

    /**
     * 添加marker
     * @param latLng
     */
    private void addContinuoueLocMarker(LatLng latLng) {
        if (null != mContinuoueLocMarker) {
            mContinuoueLocMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(mBitmapRed);
        mContinuoueLocMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
    }

    /**
     * 添加marker
     * @param latLng
     */
    private void addOneLocMarker(LatLng latLng) {
        if (null != mOneLocMarker) {
            mOneLocMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(mBitmapBlue);
        mOneLocMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopContinuoueLocaton();
        stopOneLocaton();
        mBitmapRed.recycle();
        mBaiduMap.clear();
        mMapView.onDestroy();
    }

    /*****
     *
     * 单次定位回调监听
     *
     */
    private BDAbstractLocationListener oneLocationListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null == location || null == mBaiduMap) {
                return;
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            gpsTvjingdu.setText(String.format("%s", location.getLatitude()));
            gpsTvweidu.setText(String.valueOf(location.getLongitude()));
            addOneLocMarker(latLng);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(latLng);
            int padding = 0;
            int paddingBottom = 600;
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(), padding,
                    padding, padding, paddingBottom);
            StringBuffer sb = new StringBuffer(256);
            // 更新地图状态
            mBaiduMap.animateMapStatus(mapStatusUpdate);
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("离线定位成功");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("服务端网络定位失败");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            String locationStr = BaiduUtils.getLocationStr(location, mLocClientOne);
            if (!TextUtils.isEmpty(locationStr)) {
                sb.append(locationStr);
            }
            if (null != mOneLocTV) {
                mOneLocTV.setText(sb.toString());
                gpsTvTip.setText(sb.toString());
                Log.e("定位", "信息: "+sb.toString() );
            }
        }
    };


    /*****
     *
     * 连续定位回调监听
     *
     */
    private BDAbstractLocationListener continuoueLocationListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null == location || null == mBaiduMap) {
                return;
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (isFirstLoc) {
                addContinuoueLocMarker(latLng);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(latLng);
                int padding = 0;
                int paddingBottom = 600;
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(), padding,
                        padding, padding, paddingBottom);
                // 更新地图状态
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                isFirstLoc = false;
            }
            mContinuoueLocMarker.setPosition(latLng);
            StringBuffer sb = new StringBuffer(256);
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("离线定位成功");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("服务端网络定位失败");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\n连续定位次数 : ");
            sb.append(mCount ++);
            String locationStr = BaiduUtils.getLocationStr(location,mLocClientContinuoue);
            if (!TextUtils.isEmpty(locationStr)) {
                sb.append(locationStr);
            }
            gpsTvjingdu.setText(String.format("经度%s", location.getLongitude()));
            jingdu= location.getLongitude() + "";
            weidu = location.getLatitude()+"";

            gpsTvweidu.setText(String.format("纬度:%s", location.getLatitude()));
            if (null != mContinuoueLocTV){
                mContinuoueLocTV.setText(sb.toString());
//                gpsTvTip.setText(sb.toString());
                Log.e("定位", "信息: "+sb.toString() );
                Log.e("定位", "jingdu: "+jingdu +"weidu: "+weidu);
            }
        }
    };
}
