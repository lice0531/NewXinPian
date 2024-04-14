package android_serialport_api.xingbang.firingdevice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.LoginBean;
import android_serialport_api.xingbang.utils.MmkvUtils;
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

public class DengLuActivity extends BaseActivity {

    @BindView(R.id.et_user)
    EditText etUser;
    @BindView(R.id.linearLayout10)
    LinearLayout linearLayout10;
    @BindView(R.id.et_passward)
    EditText etPassward;
    @BindView(R.id.linearLayout11)
    LinearLayout linearLayout11;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_zhuce)
    Button btnZhuce;

    private Handler mHandler_tip = new Handler();//显示进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deng_lu);
        ButterKnife.bind(this);

        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));

        String username= (String) MmkvUtils.getcode("username", "");
        if(username.length()>1){
            etUser.setText(username);
        }
        mHandler_tip = new Handler(msg -> {
            switch (msg.what) {
                case 1:
                    show_Toast("网络请求失败,请检查网络正确连接后,再次上传");
                    break;
                case 2:
                    show_Toast("登陆成功");
                    Intent intent = new Intent(DengLuActivity.this, XingbangMain.class);
                    startActivity(intent);
                    finish();
                    break;
                case 3:
                    show_Toast("错误信息:" + msg.obj);
                    break;
                case 5:
                    show_Toast(msg.obj.toString());
                    break;
                case 6:
                    show_Toast("用户名 不存在");
                    break;
                case 7:
                    show_Toast("密码错误");
                    break;
                case 8:
                    show_Toast("无参");
                    break;
                case 9:
                    show_Toast("账号未激活");
                    break;
            }
            return false;
        });
    }


    private void upload(String uPhone, String uPwd) {

        String url = Utils.httpurl_xb_denglu;//公司服务器上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();

        try {
            object.put("loginUsername", uPhone);//用户名
            object.put("loginPassword", uPwd);//密码
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
            public void onResponse(Call call, Response response) {
                try {
                    String res = response.body().string();
                    Gson gson = new Gson();
                    LoginBean loginBean = gson.fromJson(res, LoginBean.class);
//                    Log.e("登陆返回", "res: "+res );
                    Log.e("登陆返回", "loginBean: "+loginBean.toString() );
                    MmkvUtils.savecode("uIDCard",loginBean.getUIDCard());//登陆本人身份证
                    upData(loginBean);
                    ////用户 返回参数指令
                    ////1001  用户名 不存在
                    ////1002  密码错误
                    ////1003  无参
                    ////1004  账号未激活
                    if (loginBean.getStatus().equals("200")) {
                        MmkvUtils.savecode("username", etUser.getText().toString());
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
                    } else if (loginBean.getStatus().equals("1001")) {
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(6));
                    } else if (loginBean.getStatus().equals("1002")) {
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(7));
                    } else if (loginBean.getStatus().equals("1003")) {
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
                    } else if (loginBean.getStatus().equals("1004")) {
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(9));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void upData(LoginBean loginBean) {
        if (loginBean.getLst().size() > 0) {
            GreenDaoMaster master = new GreenDaoMaster();
            for (int i = 0; i < loginBean.getLst().size(); i++) {
                master.updateUser(loginBean.getLst().get(i));
            }

        }


    }

    @OnClick({R.id.btn_login, R.id.btn_zhuce})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                hideInputKeyboard();
                String uPhone = etUser.getText().toString();
                String uPwd = etPassward.getText().toString();
                upload(uPhone, uPwd);
                break;
            case R.id.btn_zhuce:
                Intent intent = new Intent(DengLuActivity.this, ZhuCeActivity.class);
                startActivity(intent);
                break;
        }
    }


    public void hideInputKeyboard() {

        etUser.clearFocus();//取消焦点
        etPassward.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}