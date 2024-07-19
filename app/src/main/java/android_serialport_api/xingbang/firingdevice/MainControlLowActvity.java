package android_serialport_api.xingbang.firingdevice;

import static com.senter.pda.iam.libgpiot.Gpiot1.PIN_ADSL;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.custom.DeviceAdapter;
import android_serialport_api.xingbang.custom.ListViewForScrollView;
import android_serialport_api.xingbang.models.DeviceBean;
import butterknife.BindView;
import butterknife.ButterKnife;
public class MainControlLowActvity extends SerialPortActivity {
    @BindView(R.id.lv)
    ListViewForScrollView lv;
    @BindView(R.id.btn_net_test)
    Button btnNetTest;
    @BindView(R.id.btn_prepare_charge)
    Button btnPrepareCharge;
    @BindView(R.id.btn_qibao)
    Button btnQibao;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.lv_net)
    ListViewForScrollView lvNet;
    @BindView(R.id.lv_qibao)
    ListViewForScrollView lvQibao;
    @BindView(R.id.lv_chongdian)
    ListViewForScrollView lvChongdian;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    private List<DeviceBean> list_device = new ArrayList<>();
    private DeviceAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxjl_maincontrol);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        initPower();                // 初始化上电方式()
        powerOnDevice(PIN_ADSL);    // 上电
    }

    private void initView() {
        adapter = new DeviceAdapter(this, list_device,false);
        lv.setAdapter(adapter);
    }


    @Override
    protected void onDataReceived(byte[] buffer, int size) {

    }
}
