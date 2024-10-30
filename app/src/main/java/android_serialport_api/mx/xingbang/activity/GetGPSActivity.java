package android_serialport_api.mx.xingbang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.databinding.ActivityGetGpsactivityBinding;

public class GetGPSActivity extends AppCompatActivity {
    ActivityGetGpsactivityBinding binding;
    private MapView mMapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetGpsactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mMapView = findViewById(R.id.gps_mapView);
        //获取地图控件引用
        BaiduMap mBaiduMap = mMapView.getMap();
        //显示卫星图层
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }
}