package android_serialport_api.xingbang.firingdevice;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.gzuliyujiang.wheelpicker.AddressPicker;
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode;
import com.github.gzuliyujiang.wheelpicker.contract.OnLinkageSelectedListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.models.JJHYanZheng;
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
    @BindView(R.id.et_userpwd)
    EditText etUserPwd;
    @BindView(R.id.et_lgc)
    EditText etLgc;
    @BindView(R.id.et_qbgs)
    EditText etQbgs;
    @BindView(R.id.et_dwdm)
    EditText etDwdm;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_sfz)
    EditText etSfz;
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
//    JDCityPicker cityPicker;
//    private JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();
//    public JDCityConfig.ShowType mWheelType = JDCityConfig.ShowType.PRO_CITY_DIS;
    private String uProvince;//省
    private String uMarket;//市
    private String uCounty;//县
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
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

        MessageBean messageBean = GreenDaoMaster.getAllFromInfo_bean();
        equ_no = messageBean.getEqu_no();
        pro_coordxy=messageBean.getPro_coordxy();
        pro_bprysfz=messageBean.getPro_bprysfz();
        pro_htid=messageBean.getPro_htid();
        pro_dwdm=messageBean.getPro_dwdm();
//        etUser.setText((String)MmkvUtils.getcode("username",""));//
//        etQbgs.setText((String)MmkvUtils.getcode("uCName",""));
//        etName.setText((String)MmkvUtils.getcode("uFName",""));
//
//        tv_province.setText((String)MmkvUtils.getcode("uProvince",""));
//        tv_market.setText((String)MmkvUtils.getcode("uMarket",""));
//        tv_county.setText((String)MmkvUtils.getcode("uCounty",""));


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
                String uDwdm = etDwdm.getText().toString();//单位代码
                String uCName = etQbgs.getText().toString();//爆破单位
                String uFName = etName.getText().toString();//人员姓名
                String uIDCard = etSfz.getText().toString();//身份证
                String uPwd = etUserPwd.getText().toString();

//                if(uPhone.length()<11){
//                    show_Toast("请输入手机号");
//                    return;
//                }else if(uCName.length()<1) {
//                    show_Toast("请输入爆破单位名称");
//                    return;
//                }else if(uFName.length()<1) {
//                    show_Toast("请输入人员名称");
//                    return;
//                }else if(uIDCard.length()<18) {
//                    show_Toast("请输入正确的身份证号");
//                    return;
//                }else if(tv_province.getText().length()<2) {
//                    show_Toast("请选择省市县");
//                    return;
//                }else if(etDwdm.getText().length()<13) {
//                    show_Toast("请输入正确的单位代码");
//                    return;
//                }

//                MmkvUtils.savecode("uPhone", uPhone);
//                MmkvUtils.savecode("uCName", uCName);
//                MmkvUtils.savecode("uFName", uFName);
//
//                MmkvUtils.savecode("province", tv_province.getText());
//                MmkvUtils.savecode("market", tv_market.getText());
//                MmkvUtils.savecode("county", tv_county.getText());

//                show_Toast("保存成功");
                upload(uPhone,uPwd,uIDCard,uCName,uFName,uDwdm);
                break;
            case R.id.jd_btn://城市

                AddressPicker picker = new AddressPicker(this);
                picker.setAddressMode(AddressMode.PROVINCE_CITY_COUNTY);
//                picker.setDefaultValue("北京市", "北京市", "房山区");
                picker.getWheelLayout().setOnLinkageSelectedListener(new OnLinkageSelectedListener() {
                    @Override
                    public void onLinkageSelected(Object first, Object second, Object third) {
                        picker.getTitleView().setText(String.format("%s%s%s",
                                picker.getFirstWheelView().formatItem(first),
                                picker.getSecondWheelView().formatItem(second),
                                picker.getThirdWheelView().formatItem(third)));


                        uProvince =first.toString().split("\'")[1];
                        uMarket =second.toString().split("\'")[1];
                        uCounty =third.toString().split("\'")[1];
                        Log.e("选择", "uProvince: "+uProvince);
                        Log.e("选择", "uMarket: "+uMarket);
                        Log.e("选择", "uCounty': "+uCounty);
                        tv_province.setText(picker.getFirstWheelView().formatItem(first) );
                        tv_market.setText(picker.getSecondWheelView().formatItem(second));
                        tv_county.setText(picker.getFirstWheelView().formatItem(third) );
                    }
                });
                picker.show();
//                showJD();
                break;
        }

    }



    private void upload(String uPhone, String uPwd, String uIDCard, String uCName, String uFName, String uDwdm) {

        String url = Utils.httpurl_jjh_test_check;//公司服务器上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();

        try {
            object.put("deviceNO", equ_no);//民爆网备案的起爆器编号
            object.put("lngLat", pro_coordxy);//GPS坐标
            object.put("gpsCoordinateSystems", "WGS84");//坐标系
            object.put("burstOrgCode", pro_dwdm);//爆破公司民爆网备案的企业代码
            object.put("projectCode", pro_htid);//民爆网备案的项目编码
            object.put("userIdCard", pro_bprysfz);//身份证号
            object.put("appVersion", "1.0.0");//接入应用的客户端版本号

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = object.toString();
        MediaType JSON = MediaType.parse("application/json;charset=UTF-8");
        Log.e("注册请求", "json: " + json);
        RequestBody requestBody = FormBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("access-token","CCDE3AE0CB8C4CF3A21925EE")
                .post(requestBody)
                .addHeader("Content-Type", "application/json;charset=UTF-8")//text/plain  application/json  application/x-www-form-urlencoded
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e("上传公司网络请求", "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                if (res != null) {
                    Gson gson = new Gson();
                    JJHYanZheng yanZheng = gson.fromJson(res, JJHYanZheng.class);
                    Log.e("上传", "返回: " + yanZheng);
                }

//                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
            }
        });
    }

}