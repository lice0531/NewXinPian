package android_serialport_api.xingbang.firingdevice;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.QRCodeUtils;
import android_serialport_api.xingbang.utils.ThreeDES;
import butterknife.BindView;
import butterknife.ButterKnife;
public class ProjectErCodeActivity extends BaseActivity {
    @BindView(R.id.ivXmCode)
    ImageView ivXmCode;
    @BindView(R.id.btnClose)
    Button btnClose;
    private String htbh = "",xmbh = "",coordxy = "",business = "",project_name = "",bprysfz = "",dwdm = "";
    private TextView totalbar_title;
    private String TAG = "项目二维码信息页面";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_ercode);
        ButterKnife.bind(this);
        AppLogUtils.writeAppLog("--进入到项目二维码信息页面--");
        initData();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        totalbar_title =  findViewById(R.id.title_text);
        ImageView title_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        title_add.setVisibility(View.GONE);
        iv_back.setVisibility(View.GONE);
        TextView title_lefttext = findViewById(R.id.title_lefttext);
        title_lefttext.setVisibility(View.VISIBLE);
        title_lefttext.setText(getResources().getString(R.string.text_xmm));
        totalbar_title.setVisibility(View.GONE);
        iv_back.setOnClickListener(v -> finish());
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        htbh = !TextUtils.isEmpty(getIntent().getStringExtra("htbh")) ?
                getIntent().getStringExtra("htbh") : "";
        xmbh = !TextUtils.isEmpty(getIntent().getStringExtra("xmbh")) ?
                getIntent().getStringExtra("xmbh") : "";
        project_name = !TextUtils.isEmpty(getIntent().getStringExtra("project_name")) ?
                getIntent().getStringExtra("project_name") : "";
        dwdm = !TextUtils.isEmpty(getIntent().getStringExtra("dwdm")) ?
                getIntent().getStringExtra("dwdm") : "";
        bprysfz = !TextUtils.isEmpty(getIntent().getStringExtra("bprysfz")) ?
                getIntent().getStringExtra("bprysfz") : "";
        coordxy = !TextUtils.isEmpty(getIntent().getStringExtra("coordxy")) ?
                getIntent().getStringExtra("coordxy") : "";
        business = !TextUtils.isEmpty(getIntent().getStringExtra("business")) ?
                getIntent().getStringExtra("business") : "";
        String content = "htbh:" + htbh + ";xmbh:" + xmbh + ";project_name:" + project_name
                + ";dwdm:" + dwdm + ";bprysfz:" + bprysfz + ";coordxy:" + coordxy + ";business:"
                + business;
        // 生成二维码   注：暂时先不加密了
//        Log.e(TAG,"加密前的项目信息:" + content);
//        final String key = "jadl12345678912345678912";
//        String ercontent = content.replace("\n", "");
//        try {
//            String encode = ThreeDES.encryptThreeDESECB(ercontent, key);
//            Bitmap qrCodeBitmap = QRCodeUtils.generateQRCode(encode);
//            ivXmCode.setImageBitmap(qrCodeBitmap);
//        } catch (Exception e) {
//            Log.e(TAG,"生成加密二维码失败:" + e.getMessage().toString());
//            AppLogUtils.writeAppLog("二维码生成失败:" + e.getMessage().toString());
//            throw new RuntimeException(e);
//        }
        Bitmap qrCodeBitmap = QRCodeUtils.generateQRCode(content);
        ivXmCode.setImageBitmap(qrCodeBitmap);
        AppLogUtils.writeAppLog("项目二维码生成的信息:" + content);
    }
}
