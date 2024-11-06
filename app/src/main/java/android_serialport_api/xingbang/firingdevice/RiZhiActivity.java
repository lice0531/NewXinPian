package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.zxing.util.Constant;
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

public class RiZhiActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_openFile1)
    Button btnOpenFile1;
    @BindView(R.id.text_filePath1)
    TextView textFilePath1;
    @BindView(R.id.btn_openFile2)
    Button btnOpenFile2;
    @BindView(R.id.text_filePath2)
    TextView textFilePath2;
    @BindView(R.id.btn_OK)
    Button btnOK;

    private String path;
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String server_addr = "";
    private String server_port = "";
    private String server_http = "";
    private String server_ip = "";
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler_loading = new Handler();//显示进度条
    private ArrayList<String> list_uid = new ArrayList<>();
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private String mOldTitle;   // 原标题
    private String mRegion;     // 区域

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ri_zhi);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));


        getUserMessage();
        mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
        mListData = new GreenDaoMaster().queryDetonatorRegionDesc(mRegion);
        list_uid.clear();
        for (int i = 0; i < mListData.size(); i++) {
            list_uid.add(mListData.get(i).getShellBlastNo());
        }
    }

    private void getUserMessage() {
        MessageBean bean = GreenDaoMaster.getAllFromInfo_bean();
        pro_bprysfz = bean.getPro_bprysfz();
        pro_htid = bean.getPro_htid();
        pro_xmbh = bean.getPro_xmbh();
        equ_no = bean.getEqu_no();
        pro_coordxy = bean.getPro_coordxy();
        server_addr = bean.getServer_addr();
        server_port = bean.getServer_port();
        server_http = bean.getServer_http();
        server_ip = bean.getServer_ip();
        pro_dwdm = bean.getPro_dwdm();
    }

    @OnClick({R.id.btn_openFile1, R.id.btn_openFile2, R.id.btn_OK})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_openFile1:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");//txt文件
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_openFile2:
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.setType("text/plain");//txt文件
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent2, 2);
                break;
            case R.id.btn_OK:
                String blastdate = Utils.getDateFormatLong(new Date());//日期
                String htbh =pro_htid;//合同编号
                String dwdm = pro_dwdm;//单位代码
                String xmbh = pro_xmbh;//项目编号
                String[] xy = pro_coordxy.split(",");//经纬度
                String jd = xy[0];//经度
                String wd = xy[1];//纬度
                String qbxm_name = "错误日志";//项目名称
                String log = Utils.readOffline(textFilePath1.getText().toString()).replace("\n", "");//日志
                String log_cmd = Utils.readOffline(textFilePath2.getText().toString()).replace("\n", "");//日志
                if (pro_coordxy.length() < 2 && jd == null) {
                    show_Toast("经纬度为空，不能执行上传");
                    return;
                }
                pb_show = 1;
                runPbDialog();//loading画面
                Log.e("上传日志", "blastdate: " + blastdate);
                Log.e("上传日志", "qbxm_name: " + qbxm_name);
                upload_xingbang(blastdate, htbh, jd, wd, xmbh, dwdm, qbxm_name, log,log_cmd);//我们自己的网址
                break;
        }
    }

    private void upload_xingbang(final String blastdate, final String htid, final String jd, final String wd, final String xmbh, final String dwdm, final String qbxm_name, final String log, final String log_cmd) {
        final String key = "jadl12345678912345678912";
        String url = Utils.httpurl_xb_his;//公司服务器上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 1; i < mListData.size(); i++) {
            list_uid.add(mListData.get(i).getShellBlastNo() + "#" + mListData.get(i).getDelay() + "#" + mListData.get(i).getErrorName());
        }
        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e("上传uid", uid);
        String xy[] = pro_coordxy.split(",");//经纬度
        try {
            object.put("sbbh", equ_no);//起爆器设备编号
            if (jd != null) {
                object.put("jd", jd);//经度
            } else if (pro_coordxy.length() > 5) {
                object.put("jd", xy[0]);//经度
            }
            if (wd != null) {
                object.put("wd", wd);//纬度
            } else if (pro_coordxy.length() > 5) {
                object.put("wd", xy[1]);//纬度
            }
            if (htid != null) {
                object.put("htid", htid);//合同编号
            } else {
                object.put("htid", pro_htid);//合同编号
            }
            object.put("bpsj", blastdate.replace("/", "-").replace(",", " "));//爆破时间blastdate.replace("/","-").replace(","," ")
            object.put("bprysfz", pro_bprysfz);//人员身份证
            object.put("uid", uid);//雷管uid
            object.put("dwdm", pro_dwdm);//单位代码
            object.put("xmbh", pro_xmbh);//项目编号
            object.put("log", log);//日志
            object.put("log_cmd", log_cmd);//日志
            object.put("yj_version", MmkvUtils.getcode("yj_version", "默认版本"));//硬件版本
            object.put("rj_version", "KT50_3.25_MX_240724_14");//软件版本
            if (qbxm_name != null && qbxm_name.length() > 1) {
                object.put("name", qbxm_name);//项目名称
            } else {
                object.put("name", MmkvUtils.getcode("pro_name", ""));//项目名称
            }

        } catch (JSONException  e) {
            e.printStackTrace();
        }
//3des加密
        String json = MyUtils.getBase64(MyUtils.encryptMode(key.getBytes(), object.toString().getBytes()));
        JSONObject object2 = new JSONObject();
        try {
            object2.put("param", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = FormBody.create(JSON, object2.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json; charset=utf-8")//text/plain  application/json  application/x-www-form-urlencoded
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                Log.e("上传公司网络请求", "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("上传", "返回: " + response.toString());
                pb_show = 0;

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("离线下载-结果回调", "requestCode: "+requestCode+"--resultCode: "+resultCode );

        if (requestCode != Constant.REQ_QR_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                if(requestCode==1){
                    textFilePath1.setText(path);
                }else {
                    textFilePath2.setText(path);
                }

                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                if(requestCode==1){
                    textFilePath1.setText(path);
                }else {
                    textFilePath2.setText(path);
                }
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                if(requestCode==1){
                    textFilePath1.setText(path);
                }else {
                    textFilePath2.setText(path);
                }
                Toast.makeText(this, path + "222222", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void runPbDialog() {
        pb_show = 1;
        tipDlg = new LoadingDialog(RiZhiActivity.this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        new Thread(() -> {
            mHandler_loading.sendMessage(mHandler_loading.obtainMessage());
            try {
                while (pb_show == 1) {
                    Thread.sleep(100);
                }
                mHandler_loading.sendMessage(mHandler_loading.obtainMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}