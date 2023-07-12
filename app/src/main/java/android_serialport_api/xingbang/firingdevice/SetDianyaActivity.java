package android_serialport_api.xingbang.firingdevice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetDianyaActivity extends BaseActivity {


    @BindView(R.id.et_set_delay)
    EditText etSetDelay;
    @BindView(R.id.et_set_lowVoltage)
    EditText etSetLowVoltage;
    @BindView(R.id.btn_lowVoltage)
    Button btnLowVoltage;
    @BindView(R.id.et_set_highVoltage)
    EditText etSetHighVoltage;
    @BindView(R.id.btn_highVoltage)
    Button btnHighVoltage;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.ll_firing_0)
    LinearLayout llFiring0;
    private Handler mHandler_tip = null;//提示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_dianya);
        ButterKnife.bind(this);

        mHandler_tip = new Handler(msg -> {
            Log.e("mHandler_tip", "msg.arg1:"+msg.arg1);
            if(msg.arg1 == 1){
                show_Toast("设置低压成功");
            }else if(msg.arg1 == 2) {
                show_Toast("设置高压成功");
            }else if(msg.arg1 == 3){
                show_Toast("您输入电压不符合规范,请重新输入");
            }else {
                String time = (String) msg.obj;
                show_Toast("起爆记录条数最大200条,已删除" + time + "记录");
            }
            return false;
        }) ;
    }


    @OnClick({R.id.btn_lowVoltage, R.id.btn_highVoltage, R.id.btn_start})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_lowVoltage:
                String str_lowVoltage=  etSetLowVoltage.getText().toString();
                int low = Double.valueOf(Utils.convertToDouble(str_lowVoltage,7.0)*10).intValue();
                Log.e("设置电压", "low: "+low );
//                if(Utils.isNumber(str_lowVoltage)){
//                    edit.putString("lowVoltage", str_lowVoltage);
//                    edit.commit();//点击提交编辑器
//                    byte[] delayBye = Utils.shortToByte((short) low);
//                    String delayStr = Utils.bytesToHexFun(delayBye);
//                    String b=delayStr.substring(0,2);
//                    String c=delayStr.substring(2);
//                    byte[] reCmd1 = FourStatusCmd.setToXbCommon_SetLowVoltage("00", c+b);//
//                    sendCmd(reCmd1);
//                    Log.e("设置低压", " c+b: "+ c+b);
//                }else {
//                    Message msg = mHandler_tip.obtainMessage();
//                    msg.arg1 = 3;
//                    mHandler_tip.sendMessage(msg);
//                }
                break;
            case R.id.btn_highVoltage:
                String str_highVoltage=  etSetHighVoltage.getText().toString();
                int high = Double.valueOf(Utils.convertToDouble(str_highVoltage,16.0)*10).intValue();
                Log.e("设置电压", "low: "+high );
//                if(Utils.isNumber(str_highVoltage)){
//                    edit.putString("highVoltage", str_highVoltage);
//                    edit.commit();//点击提交编辑器
//                    byte[] delayBye = Utils.shortToByte((short) high);
//                    String delayStr = Utils.bytesToHexFun(delayBye);
//                    String b=delayStr.substring(0,2);
//                    String c=delayStr.substring(2);
//                    byte[] reCmd1 = FourStatusCmd.setToXbCommon_SetHighVoltage("00",  c+b);//新芯片顺序和旧的不一样
//                    sendCmd(reCmd1);
//                    Log.e("设置高压", " c+b: "+ c+b);
//                }else {
//                    Message msg = mHandler_tip.obtainMessage();
//                    msg.arg1 = 3;
//                    mHandler_tip.sendMessage(msg);
//                }
                break;
            case R.id.btn_start:
                break;
        }
    }
}