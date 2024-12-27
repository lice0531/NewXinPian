package android_serialport_api.xingbang.firingdevice;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.services.LocationService;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看页面
 */
public class QueryMainActivity extends BaseActivity {

    @BindView(R.id.btn_query_return)
    Button btnQueryReturn;
    @BindView(R.id.btn_currentinfo)
    Button btnCurrentinfo;
    @BindView(R.id.btn_currentinfo_all)
    Button btnCurrentinfoAll;
    @BindView(R.id.btn_hisinfo)
    Button btnHisinfo;
    @BindView(R.id.btn_hisinfo_all)
    Button btnHisinfoAll;
    @BindView(R.id.tv_jd)
    TextView tvJD;
    @BindView(R.id.tv_wd)
    TextView tvWD;
    @BindView(R.id.txt_fireno)
    TextView txtFireno;
    @BindView(R.id.setDelayTime_tipinfo_fragement)
    LinearLayout setDelayTimeTipinfoFragement;
    @BindView(R.id.setDelayTimeMainPage)
    LinearLayout setDelayTimeMainPage;
    @BindView(R.id.txt_gps)
    TextView txtGps;
    @BindView(R.id.btn_upload_log)
    Button btnUploadLog;
    @BindView(R.id.textView1)
    TextView textView1;
    private long time = 0;
    private String equ_no = "";//设备编码
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private Context context;

    private LocationService locationService;
    private TextView LocationResult;
    private LocationClient mLocClientContinuoue = null;
    private LocationClient mLocClientOne = null;
    private boolean isFirstLoc = true;
    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_query_main);
        ButterKnife.bind(this);
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        AppLogUtils.writeAppLog("---进入查看页面---");
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        getUserMessage();
        txtFireno.setText(getString(R.string.text_query_num) + equ_no);
        LocationResult = (TextView) findViewById(R.id.textView1);
        LocationResult.setMovementMethod(ScrollingMovementMethod.getInstance());
//        ding();
//        baidudingwei();
//        startContinuoueLocaton();
        startOneLocaton();
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

    /**
     * 启动单次定位
     */
    private void startOneLocaton() {
        mLocClientOne = new LocationClient(this);
        mLocClientOne.registerLocationListener(oneLocationListener);
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
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
        locationClientOption.setIsNeedAddress(false);
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
        locationClientOption.setScanSpan(1000);
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


    /*****
     *
     * 单次定位回调监听
     *
     */
    private final BDAbstractLocationListener oneLocationListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null == location) {
                return;
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            addOneLocMarker(latLng);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(latLng);
            int padding = 0;
            int paddingBottom = 600;
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(), padding,
                    padding, padding, paddingBottom);
            StringBuffer sb = new StringBuffer(256);
            // 更新地图状态
//            mBaiduMap.animateMapStatus(mapStatusUpdate);
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
            String locationStr = Utils.getLocationStr(location, mLocClientOne);
            Log.e("定位", "locationStr: "+locationStr );
            if (!TextUtils.isEmpty(locationStr)) {
                sb.append(locationStr);
            }
                double a = 4.9E-324;
                if (location.getLatitude() != a) {
                    tvWD.setText(getResources().getString(R.string.text_query_lat) + location.getLatitude());
                }

                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                if (location.getLatitude() != a) {
                    tvJD.setText(getResources().getString(R.string.text_query_lon) + location.getLongitude());
                }
        }
    };

    /*****
     *
     * 连续定位回调监听
     *
     */
    private final BDAbstractLocationListener continuoueLocationListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null == location ) {
                return;
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (isFirstLoc) {
//                addContinuoueLocMarker(latLng);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(latLng);
                int padding = 0;
                int paddingBottom = 600;
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(), padding,
                        padding, padding, paddingBottom);
                // 更新地图状态
//                mBaiduMap.animateMapStatus(mapStatusUpdate);
                isFirstLoc = false;
            }
//            mContinuoueLocMarker.setPosition(latLng);
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
            String locationStr = Utils.getLocationStr(location,mLocClientContinuoue);
            Log.e("连续定位", "locationStr: "+locationStr );
            if (!TextUtils.isEmpty(locationStr)) {
                sb.append(locationStr);
            }
                double a = 4.9E-324;
                if (location.getLatitude() != a) {
                    tvWD.setText(getResources().getString(R.string.text_query_lat) + location.getLatitude());
                }

                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                if (location.getLatitude() != a) {
                    tvJD.setText(getResources().getString(R.string.text_query_lon) + location.getLongitude());
                }
        }
    };

    @Override
    protected void onDestroy() {
        if (db != null)
            db.close();

//        locationService.unregisterListener(mListener); //注销掉监听
//        locationService.stop(); //停止定位服务

        stopContinuoueLocaton();
        stopOneLocaton();
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }


    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
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
                    tvWD.setText(getResources().getString(R.string.text_query_lat) + location.getLatitude());
                }

                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                if (location.getLatitude() != a) {
                    tvJD.setText(getResources().getString(R.string.text_query_lon) + location.getLongitude());
                }
                //保存数据
//                saveData(location.getLongitude(),location.getLatitude());

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
                Log.e("百度定位数据", sb.toString());
//                logMsg(sb);
            }
        }

    };

    /**
     * 显示请求字符串
     *
     * @param str
     */
    public void logMsg(StringBuffer str) {
        final StringBuffer s = str;
        try {
            if (LocationResult != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LocationResult.post(new Runnable() {
                            @Override
                            public void run() {
                                LocationResult.setText(s);
                                Log.e("百度地图", "定位: " + s);
                            }
                        });

                    }
                }).start();
//                locationService.stop(); //停止定位服务(不停止的话,会一直定位)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ding() {
        context = this;

        //获取LocationManager
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /**
         * 参1:选择定位的方式
         * 参2:定位的间隔时间
         * 参3:当位置改变多少时进行重新定位
         * 参4:位置的回调监听
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, new LocationListener() {
            //当位置改变的时候调用
            @Override
            public void onLocationChanged(Location location) {

                //经度
                double longitude = location.getLongitude();
                //纬度
                double latitude = location.getLatitude();

                //海拔
                double altitude = location.getAltitude();

                txtGps.setText(getResources().getString(R.string.text_query_jd) + longitude + getResources().getString(R.string.text_query_wd) + latitude + "\n" + getResources().getString(R.string.text_query_hb) + altitude);
            }

            //当GPS状态发生改变的时候调用
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {


                switch (status) {

                    case LocationProvider.AVAILABLE:
                        show_Toast(getResources().getString(R.string.text_gps1));

                        break;

                    case LocationProvider.OUT_OF_SERVICE:
                        show_Toast(getResources().getString(R.string.text_gps2));

                        break;

                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        show_Toast(getResources().getString(R.string.text_gps3));
                        break;


                }

            }

            //GPS开启的时候调用
            @Override
            public void onProviderEnabled(String provider) {
                show_Toast(getResources().getString(R.string.text_gps4));

            }

            //GPS关闭的时候调用
            @Override
            public void onProviderDisabled(String provider) {
                show_Toast(getResources().getString(R.string.text_gps5));

            }
        });


    }


    private void getUserMessage() {
        String selection = "id = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"1"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_MESSQGE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {  //cursor不位空,可以移动到第一行
            equ_no = cursor.getString(4);
            cursor.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            // text1.setText(data.getStringExtra("backString"));
        }
    }






    @OnClick({R.id.btn_query_return, R.id.btn_currentinfo, R.id.btn_currentinfo_all,
            R.id.btn_hisinfo, R.id.btn_hisinfo_all,R.id.btn_upload_log})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_query_return:
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
                break;
            case R.id.btn_currentinfo://当前雷管信息
                String str1 = new String("数据上传");//"当前雷管信息"
                Intent intent = new Intent(QueryMainActivity.this, UploadMessageActivity.class);
                intent.putExtra("dataSend", str1);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_currentinfo_all:
                String str2 = new String(getString(R.string.text_query_dqlg));//"当前雷管信息"
                Intent intent2 = new Intent(QueryMainActivity.this, QueryCurrentDetail.class);
                intent2.putExtra("dataSend", str2);
                startActivityForResult(intent2, 1);
                break;
            case R.id.btn_hisinfo:
                String str3 = new String(getString(R.string.text_query_lslg));
                Intent intent3 = new Intent(QueryMainActivity.this, QueryHisDetail.class);
                intent3.putExtra("dataSend", str3);
                startActivityForResult(intent3, 1);
                break;
            case R.id.btn_hisinfo_all:
                String str4 = new String("全部检测数据");
                Intent intent4 = new Intent(QueryMainActivity.this, QueryHisDetail_all.class);
                intent4.putExtra("dataSend", str4);
                startActivityForResult(intent4, 1);
                break;
            case R.id.btn_upload_log:
                String str5 = new String("全部检测数据");
                Intent intent5 = new Intent(QueryMainActivity.this, UpLoadLogActivity.class);
                intent5.putExtra("dataSend", str5);
                startActivityForResult(intent5, 1);
                break;
        }
    }

    /**
     * 保存信息
     *
     * @param jingdu
     * @param weidu
     */
    private void saveData(double jingdu, double weidu) {
        String d = (jingdu + "," + weidu).toString().trim().replace("\n", "").replace("，", ",").replace(" ", "");
        ContentValues values = new ContentValues();
        values.put("pro_coordxy", d);
        db.update(DatabaseHelper.TABLE_NAME_USER_MESSQGE, values, "id = ?", new String[]{"1"});
        Utils.saveFile_Message();//保存用户信息
    }
}

