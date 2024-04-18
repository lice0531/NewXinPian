package android_serialport_api.xingbang.firingdevice;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.MyUtils;
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

public class ZhuCeActivity extends BaseActivity {

    @BindView(R.id.et_user)
    EditText etUser;
    @BindView(R.id.et_lgc)
    EditText etLgc;
    @BindView(R.id.et_qbgs)
    EditText etQbgs;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.set_save)
    Button setSave;
    @BindView(R.id.jd_btn)
    Button jd_btn;
    @BindView(R.id.province)
    TextView tv_province;
    @BindView(R.id.market)
    TextView tv_market;
    @BindView(R.id.county)
    TextView tv_county;

    private Handler mHandler_tip = new Handler();//显示进度条
    JDCityPicker cityPicker;
    private JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();
    public JDCityConfig.ShowType mWheelType = JDCityConfig.ShowType.PRO_CITY_DIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhu_ce);
        ButterKnife.bind(this);

        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        mHandler_tip = new Handler(msg -> {
            switch (msg.what) {
                case 1:
                    show_Toast("网络请求失败,请检查网络正确连接后,再次上传");
                    break;
                case 2:
                    show_Toast("注册成功");
                    break;
                case 3:
                    show_Toast("错误信息:" + msg.obj);
                    break;
                case 4:
                    show_Toast("起爆器未备案或未设置作业任务");

                    break;
                case 5:
                    show_Toast(msg.obj.toString());
                    break;
            }
            return false;
        });

        cityPicker = new JDCityPicker();
        //初始化数据
        cityPicker.init(this);
        //设置JD选择器样式位只显示省份和城市两级
        cityPicker.setConfig(jdCityConfig);
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                tv_province.setText(province.getName());
                tv_market.setText(city.getName());
                tv_county.setText(district.getName());
            }

            @Override
            public void onCancel() {
            }
        });

        etUser.setText((String)MmkvUtils.getcode("username",""));//
        etQbgs.setText((String)MmkvUtils.getcode("uCName",""));
        etName.setText((String)MmkvUtils.getcode("uFName",""));
        tv_province.setText((String)MmkvUtils.getcode("province",""));
        tv_market.setText((String)MmkvUtils.getcode("market",""));
        tv_county.setText((String)MmkvUtils.getcode("county",""));


    }

    @OnClick({R.id.set_save, R.id.jd_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_save://保存
                //uPhone 用户名(手机号),
                //uPwd 密码,
                //uCid 单位Code,
                //uCName 起爆单位,
                //uFName 姓名

                String uPhone = etUser.getText().toString();//手机号
                String uCid = etLgc.getText().toString();//厂家码
                String uCName = etQbgs.getText().toString();//爆破单位
                String uFName = etName.getText().toString();//人员姓名

//                if(uPhone.length()<1){
//                    show_Toast("请输入手机号");
//                    return;
//                }else if(uCName.length()<1) {
//                    show_Toast("请输入爆破单位名称");
//                    return;
//                }else if(uFName.length()<1) {
//                    show_Toast("请输入人员名称");
//                    return;
//                }else if(tv_province.getText().length()<2) {
//                    show_Toast("请选择省市县");
//                    return;
//                }

                MmkvUtils.savecode("uPhone", uPhone);
                MmkvUtils.savecode("uCName", uCName);
                MmkvUtils.savecode("uFName", uFName);

                MmkvUtils.savecode("province", tv_province.getText());
                MmkvUtils.savecode("market", tv_market.getText());
                MmkvUtils.savecode("county", tv_county.getText());

                show_Toast("保存成功");
//        upload(uPhone,uPwd,uCid,uCName,uFName);
                break;
            case R.id.jd_btn://城市
                showJD();
                break;
        }

    }

    private void showJD() {
        cityPicker.showCityPicker();
    }

    private void upload(String uPhone, String uPwd, String uCid, String uCName, String uFName) {

        String url = "http://111.194.155.18:999/Handset/Register";//公司服务器上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();

        try {
            object.put("uPhone", uPhone);//用户名
            object.put("uPwd", uPwd);//密码
            object.put("uCid", uCid);//厂家代码
            object.put("uCName", uCName);//起爆公司
            object.put("uFName", uFName);//人员姓名
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = object.toString();
        MediaType JSON = MediaType.parse("application/json");
        Log.e("注册请求", "json: " + json);
        RequestBody requestBody = FormBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")//text/plain  application/json  application/x-www-form-urlencoded
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e("上传公司网络请求", "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("上传", "返回: " + response.toString());
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
            }
        });
    }

}