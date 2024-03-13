package android_serialport_api.xingbang.firingdevice;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
    @BindView(R.id.et_psw)
    EditText etPsw;
    @BindView(R.id.et_lgc)
    EditText etLgc;
    @BindView(R.id.et_qbgs)
    EditText etQbgs;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.set_save)
    Button setSave;


    private Handler mHandler_tip = new Handler();//显示进度条
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhu_ce);
        ButterKnife.bind(this);

        mHandler_tip = new Handler(msg -> {
            switch (msg.what){
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

    }

    @OnClick(R.id.set_save)
    public void onClick() {
        //uPhone 用户名(手机号),
        //uPwd 密码,
        //uCid 单位Code,
        //uCName 起爆单位,
        //uFName 姓名

        String uPhone=etUser.getText().toString();
        String uPwd=etPsw.getText().toString();
        String uCid=etLgc.getText().toString();
        String uCName=etQbgs.getText().toString();
        String uFName=etName.getText().toString();
        upload(uPhone,uPwd,uCid,uCName,uFName);
    }

    private void upload(String uPhone,String uPwd,String uCid,String uCName,String uFName) {

        String url = "http://111.194.155.18:999/Handset/Register";//公司服务器上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();

        try {
            object.put("uPhone", uPhone);//用户名
            object.put("uPwd", uPwd);//密码
            object.put("uCid", uCid);//厂家代码
            object.put("uCName", uCName);//起爆公司
            object.put("uFName", uFName);//人员姓名
        } catch (JSONException  e) {
            e.printStackTrace();
        }
        //3des加密
        String json = object.toString();
        MediaType JSON = MediaType.parse("application/json");
        Log.e("注册请求", "json: " + json);
        RequestBody requestBody = FormBody.create(JSON,  json );
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