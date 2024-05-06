package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

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
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.UserMain;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.LoginBean;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.NetUtils;
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
    private Handler mHandler2;
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;

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

        mHandler2 = new Handler(msg -> {
            //显示或隐藏loding界面
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });
    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
        //tipDlg.setMessage("正在操作,请等待...").show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                mHandler2.sendMessage(mHandler2.obtainMessage());
                try {
                    while (pb_show == 1) {
                        Thread.sleep(100);
                    }
                    mHandler2.sendMessage(mHandler2.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void upload(String uPhone, String uPwd) {

        String url = Utils.httpurl_xb_denglu_ceshi;//公司服务器上传
//        String url = Utils.httpurl_xb_denglu;//公司服务器上传
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
                pb_show = 0;
                Log.e("上传公司网络请求", "IOException: " + e);
                Utils.writeRecord("上传公司网络请求失败"+"IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) {

                try {
                    String res = response.body().string();
                    Gson gson = new Gson();
                    LoginBean loginBean = gson.fromJson(res, LoginBean.class);
                    Log.e("登陆返回", "res: "+res );
                    Log.e("登陆返回", "loginBean: "+loginBean.toString() );

                    ////用户 返回参数指令
                    ////1001  用户名 不存在
                    ////1002  密码错误
                    ////1003  无参
                    ////1004  账号未激活
                    if (loginBean.getStatus().equals("200")) {
                        MmkvUtils.savecode("uIDCard",loginBean.getUIDCard());//登陆本人身份证
                        MmkvUtils.savecode("uCName",loginBean.getUCName());//登陆本人身份证
                        MmkvUtils.savecode("uFName",loginBean.getUFName());//登陆本人身份证
                        MmkvUtils.savecode("uProvince",loginBean.getUProvince());//省
                        MmkvUtils.savecode("uMarket",loginBean.getUMarket());//市
                        MmkvUtils.savecode("uCounty",loginBean.getUCounty());//县
                        upData(loginBean);
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

                    pb_show = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void upData(LoginBean loginBean) {
        GreenDaoMaster master = new GreenDaoMaster();
        getDaoSession().getUserMainDao().deleteAll();
        if (loginBean.getLst().size() > 0) {
            for (int i = 0; i < loginBean.getLst().size(); i++) {
                master.updateUser(loginBean.getLst().get(i));
            }
        }
    }

    @OnClick({R.id.btn_login, R.id.btn_zhuce})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                pb_show = 1;
                runPbDialog();//loading画面
                hideInputKeyboard();
                String uPhone = etUser.getText().toString();
                String uPwd = etPassward.getText().toString();
                GreenDaoMaster master = new GreenDaoMaster();

                if (NetUtils.haveNetWork(this)) {
                    upload(uPhone, uPwd);
                }else {
                    UserMain user= master.queryUsername(uPhone);
                    if(user==null){
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(6));
                        pb_show = 0;
                        return;
                    }
                    if(user.getUpassword().equals(uPwd)){
                        MmkvUtils.savecode("username", etUser.getText().toString());
                        MmkvUtils.savecode("uIDCard",user.getUIDCard());//登陆本人身份证
                        MmkvUtils.savecode("uCName",user.getUCName());//登陆本人身份证
                        MmkvUtils.savecode("uFName",user.getUFName());//登陆本人身份证
                        MmkvUtils.savecode("uProvince",user.getUProvince());//省
                        MmkvUtils.savecode("uMarket",user.getUMarket());//市
                        MmkvUtils.savecode("uCounty",user.getUCounty());//县
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
                    }else {
                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(7));
                    }
                    pb_show = 0;
                }

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