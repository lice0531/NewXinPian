package android_serialport_api.xingbang.firingdevice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.DanLingOffLinBean;
import android_serialport_api.xingbang.models.ErweimaBean;
import android_serialport_api.xingbang.utils.AMapUtils;
import android_serialport_api.xingbang.utils.LngLat;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.zxing.activity.CaptureActivity;
import android_serialport_api.xingbang.zxing.util.Constant;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownOfflineActivity extends BaseActivity {
    @BindView(R.id.btn_openFile)
    Button btnOpenFile;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.text_filePath)
    TextView textFilePath;
    @BindView(R.id.btn_OK)
    Button btnOK;
    @BindView(R.id.edit_mima)
    EditText editMima;
//    @BindView(R.id.btn_clear_htid)
//    Button btnClearHtid;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
//    @BindView(R.id.btn_clear_xmbh)
//    Button btnClearXmbh;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
//    @BindView(R.id.btn_clear_dwdm)
//    Button btnClearDwdm;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
//    @BindView(R.id.btn_clear_sfz)
//    Button btnClearSfz;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.ly_setUpdata)
    LinearLayout lySetUpdata;
    @BindView(R.id.df_at_htid)
    AutoCompleteTextView dfAtHtid;
    @BindView(R.id.df_at_xmbh)
    AutoCompleteTextView dfAtXmbh;
    @BindView(R.id.df_at_dwdm)
    AutoCompleteTextView dfAtDwdm;
    @BindView(R.id.df_at_bprysfz)
    AutoCompleteTextView dfAtBprysfz;
    @BindView(R.id.ll_xmxx)
    LinearLayout llXmxx;
    @BindView(R.id.ll_dwxx)
    LinearLayout llDwxx;
    private String path;
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler2;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private String TAG = "离线下载";
    private Handler mHandler_1;
    private Handler mHandler_loading = new Handler();//显示进度条
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String pro_name = "";//项目名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_offline);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        initHandle();

        initAutoComplete("history_htid", dfAtHtid);//输入历史记录
        initAutoComplete("history_xmbh", dfAtXmbh);
        initAutoComplete("history_dwdm", dfAtDwdm);
        initAutoComplete("history_bprysfz", dfAtBprysfz);
        mHandler2 = new Handler(msg -> {
            //显示或隐藏loding界面
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });

//        dfAtHtid.addTextChangedListener(htbh_watcher);//长度监听
//        dfAtXmbh.addTextChangedListener(xmbh_watcher);//长度监听
//        dfAtDwdm.addTextChangedListener(dwdm_watcher);//长度监听
//        dfAtBprysfz.addTextChangedListener(sfz_watcher);//长度监听
        getUserMessage();//获取用户信息
        initCardViewData();
//        startQrCode();
    }

    private void initCardViewData() {
        Project usedProject = Application.getDaoSession().getProjectDao().queryBuilder().where(ProjectDao.Properties.Selected.eq("true")).unique();
        if (usedProject != null) {
            ll1.setVisibility(TextUtils.isEmpty(usedProject.getHtbh()) ? View.GONE : View.VISIBLE);
            ll2.setVisibility(TextUtils.isEmpty(usedProject.getXmbh()) ? View.GONE : View.VISIBLE);
            dfAtHtid.setText(usedProject.getHtbh());
            dfAtXmbh.setText(usedProject.getXmbh());
            dfAtDwdm.setText(usedProject.getDwdm());
            dfAtBprysfz.setText(usedProject.getBprysfz());
            String business = usedProject.getBusiness();
            if (business.startsWith("非营业性")) {
                llXmxx.setVisibility(View.GONE);
//                llDwxx.setVisibility(View.VISIBLE);
            } else {
                llXmxx.setVisibility(View.VISIBLE);
//                llDwxx.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    private void initHandle() {
        mHandler_loading = new Handler(msg -> {
            //显示或隐藏loding界面
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });
        mHandler_1 = new Handler(msg -> {
            switch (msg.what) {
                case 0:
                    show_Toast(getResources().getString(R.string.text_xxcg));
                    break;
                case 1:
                case 99:
                    show_Toast(String.valueOf(msg.obj));
                    break;
                case 2:
                    show_Toast(getResources().getString(R.string.text_xzsb1));
                    break;
                case 3:
                    show_Toast(getResources().getString(R.string.text_xzsb2));
                    break;
                case 4:
                    show_Toast(getResources().getString(R.string.text_xzsb3));
                    break;
                case 5:
                    show_Toast(getResources().getString(R.string.text_xzsb4));
                    break;
                case 6:
                    show_Toast(getResources().getString(R.string.text_xzsb5));
                    break;
                case 7:
                    show_Toast(getResources().getString(R.string.text_xzsb6));
                    break;
                case 8:
                    show_Toast(getResources().getString(R.string.text_xzsb7));
                    break;
                case 9:
                    show_Toast(getResources().getString(R.string.text_xzsb8));
                    break;
                case 10:
                    show_Toast(getResources().getString(R.string.text_xzsb9));
                    break;
                case 11:
                    show_Toast(getResources().getString(R.string.text_xzsb10));
                    break;
                case 12:
                    show_Toast(getResources().getString(R.string.text_xzsb11));
                    break;
                case 13:
                case 15:
                    show_Toast(getResources().getString(R.string.text_xzsb12));
                    break;
                case 14:
                    show_Toast(getResources().getString(R.string.text_xzsb13));
                    break;
                case 16:
                    show_Toast(getResources().getString(R.string.text_xzsb18));
                    break;
                case 17:
                    pb_show = 1;
                    runPbDialog();
                    String name = msg.obj.toString();
//                    show_Toast("扫码成功");
                    new Thread(() -> {
                        upload_xingbang(name);
                    }).start();

                    break;
                case 18:
//                    String mima = editMima.getText().toString();//txt中的密文
                    String res = msg.obj.toString();
//                    if (mima.length() < 6) {
//                        show_Toast(getResources().getString(R.string.text_xzsb19));
//                    } else {
//                        insertData(res, mima);
//                    }
                    insertData(res, "");
                    break;
                case 89:
                    show_Toast(getResources().getString(R.string.text_line_tip15));
                    break;


            }
            return false;
        });
    }


    //获取用户信息
    private void getUserMessage() {
        String selection = "id = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"1"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_MESSQGE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {  //cursor不位空,可以移动到第一行
            pro_bprysfz = cursor.getString(1);
            pro_htid = cursor.getString(2);
            pro_xmbh = cursor.getString(3);
            pro_coordxy = cursor.getString(5);
            pro_dwdm = cursor.getString(15);
            cursor.close();
        }
        dfAtBprysfz.setText(pro_bprysfz);
        dfAtHtid.setText(pro_htid);
        dfAtXmbh.setText(pro_xmbh);
        dfAtDwdm.setText(pro_dwdm);

    }

    private void runPbDialog() {
        pb_show = 1;
        tipDlg = new LoadingDialog(DownOfflineActivity.this);
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


    private void insertData(String json, String mima) {

        String key = "jadl12345678901234" + mima;
        String res;
        String tx_htid = dfAtHtid.getText().toString().trim().replace(" ", "");//合同编号 15位
        String tv_xmbh = dfAtXmbh.getText().toString().trim().replace(" ", "");//项目编号
        String tv_dwdm = dfAtDwdm.getText().toString().trim();//单位代码 13位
        String tv_sfz = dfAtBprysfz.getText().toString().trim();//单位代码 13位
//        if (StringUtils.isBlank(tx_htid) && StringUtils.isBlank(tv_xmbh) && StringUtils.isBlank(tv_dwdm)) {
//            show_Toast(getResources().getString(R.string.text_down_err8));
//            return;
//        }
//        if (StringUtils.isBlank(tv_sfz)) {
//            show_Toast(getResources().getString(R.string.text_down_err11));
//            return;
//        }
        try {
//            res = new String(MyUtils.decryptMode(key.getBytes(), Base64.decode(json, Base64.DEFAULT)));
            res =json.replace("\\", "");
            Log.e(TAG, "res: "+res );
//            Utils.writeRecord("离线下载:" + res);
        } catch (IllegalArgumentException e) {
            show_Toast(getResources().getString(R.string.text_down_err12));
            e.printStackTrace();
            return;
        } catch (NullPointerException e) {
            show_Toast(getResources().getString(R.string.text_down_err13));
            e.printStackTrace();
            return;
        }
        Gson gson = new Gson();
        DanLingOffLinBean danLingBean = gson.fromJson(res, DanLingOffLinBean.class);

        try {
            JSONObject object1 = new JSONObject(res);
//            Utils.writeRecord("解密信息"+object1.toString());
            String cwxx = object1.getString("cwxx");
            String sqrq2=danLingBean.getSqrq();
            Log.e(TAG, "sqrq2: "+sqrq2);
            long time2 = (long) 3 * 86400000;
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String yxq="";
            try {
                Date date3 = sd.parse(sqrq2);//当前日期
                yxq = sd.format(date3.getTime() + time2);
                Log.e("获取申请日期3天后的日期", "yxq: "+yxq+" sqrq2:"+sqrq2 );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (cwxx.equals("0")) {

                int err = 0;
                for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
//                    Log.e(TAG, "danLingBean.getLgs().getLg().get(i): "+danLingBean.getLgs().getLg().get(i) );
                    if (!danLingBean.getLgs().getLg().get(i).getGzmcwxx().equals("0")) {
                        err++;
                    }
                }
                if (danLingBean.getCwxx().equals("0")) {
                    if (danLingBean.getZbqys().getZbqy().size() > 0) {
                        for (int i = 0; i < danLingBean.getZbqys().getZbqy().size(); i++) {
                            insertJson(tx_htid, tv_xmbh, res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc(),yxq,danLingBean.getLgs().getLg().size());
                        }
                    }
                }
                Log.e(TAG, "danLingBean.getLgs().getLg().size(): "+danLingBean.getLgs().getLg().size() );

                if (danLingBean.getLgs().getLg().size() > 0) {//更新雷管信息
                    for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                        GreenDaoMaster.updateLgState_lixian(danLingBean.getLgs().getLg().get(i),yxq);
                    }
                }
//                if (err != 0) {
//                    show_Toast(danLingBean.getZbqys().getZbqy().get(0).getZbqymc() + "下载的雷管出现错误,请检查数据");
//                }
                pb_show = 0;//结束动画
//                show_Toast("项目下载成功");
                mHandler_1.sendMessage(mHandler_1.obtainMessage(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向数据库中插入数据
     * @param htbh
     * @param xmbh
     * @param json
     * @param errNum
     * @param coordxy
     */
    public void insertJson(String htbh, String xmbh, String json, int errNum, String coordxy) {
        ContentValues values = new ContentValues();
        values.put("htbh", htbh);
        values.put("xmbh", xmbh);
        values.put("json", json);
        values.put("errNum", errNum + "");
        values.put("qbzt", "未爆破");
        values.put("dl_state", "未上传");
        values.put("zb_state", "未上传");
        values.put("bprysfz", dfAtBprysfz.getText().toString().trim());//身份证号
        values.put("coordxy", coordxy.replace("\n", "").replace("，", ",").replace(" ", ""));//经纬度
        if (dfAtDwdm.getText().toString().trim().length() < 1) {//单位代码
            values.put("dwdm", "");
        } else {
            values.put("dwdm", dfAtDwdm.getText().toString().trim());
        }

        Log.e(TAG, "插入数据-成功");
        db.insert(DatabaseHelper.TABLE_NAME_SHOUQUAN, null, values);
        Utils.saveFile();//把软存中的数据存入磁盘中
        saveData(htbh, xmbh, dfAtBprysfz.getText().toString().trim(), coordxy);
    }

    /**
     * 向数据库中插入数据
     * @param htbh
     * @param xmbh
     * @param json
     * @param errNum
     * @param coordxy
     * @param name
     * @param yxq
     * @param toal
     */
    public void insertJson(String htbh, String xmbh, String json, int errNum, String coordxy, String name,String yxq,int toal) {
        ContentValues values = new ContentValues();
        values.put("htbh", htbh);
        values.put("xmbh", xmbh);
        values.put("json", json);
        values.put("errNum", errNum);
        values.put("qbzt", "未爆破");
        values.put("dl_state", "未上传");
        values.put("zb_state", "未上传");
        values.put("spare1", name);
        values.put("spare2", yxq);//申请日期 yxq.substring(0, 10)
        values.put("total", toal);//总数
        values.put("bprysfz",dfAtBprysfz.getText().toString().trim());//身份证号
        values.put("coordxy", coordxy.replace("\n", "").replace("，", ",").replace(" ", ""));//经纬度
        if (dfAtDwdm.getText().toString().trim().length() < 1) {//单位代码
            values.put("dwdm", "");
        } else {
            values.put("dwdm", dfAtDwdm.getText().toString().trim());
        }

        Log.e("插入数据", "成功");
        db.insert(DatabaseHelper.TABLE_NAME_SHOUQUAN, null, values);

    }


    /**
     * 向数据库中插入数据
     */
    public void insertJson(String htbh, String xmbh, String json, int errNum, String coordxy, String name) {
        ContentValues values = new ContentValues();
        values.put("htbh", htbh);
        values.put("xmbh", xmbh);
        values.put("json", json);
        values.put("errNum", errNum);
        values.put("qbzt", "未爆破");
        values.put("dl_state", "未上传");
        values.put("zb_state", "未上传");
        values.put("spare1", name);
        values.put("bprysfz",dfAtBprysfz.getText().toString().trim());//身份证号
        values.put("coordxy", coordxy.replace("\n", "").replace("，", ",").replace(" ", ""));//经纬度
        if (dfAtDwdm.getText().toString().trim().length() < 1) {//单位代码
            values.put("dwdm", "");
        } else {
            values.put("dwdm", dfAtDwdm.getText().toString().trim());
        }

        Log.e("插入数据", "成功");
        db.insert(DatabaseHelper.TABLE_NAME_SHOUQUAN, null, values);
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 保存信息
     *
     * @param htbh
     * @param xmbh
     * @param sfz
     * @param coordxy
     */
    private void saveData(String htbh, String xmbh, String sfz, String coordxy) {

        ContentValues values = new ContentValues();
        values.put("pro_xmbh", xmbh);
        values.put("pro_dwdm", "");
        values.put("pro_bprysfz", sfz);
        values.put("pro_htid", htbh);
        values.put("pro_coordxy", coordxy);
        db.update(DatabaseHelper.TABLE_NAME_USER_MESSQGE, values, "id = ?", new String[]{"1"});

        Utils.saveFile_Message();//保存用户信息
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            Log.e("扫码结果", "scanResult: "+scanResult );
            //将扫描出的信息显示出来

            Message msg = new Message();
            msg.obj = scanResult;
            msg.what=17;
            mHandler_1.sendMessage(msg);
        }
        if (requestCode != Constant.REQ_QR_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                textFilePath.setText(path);
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                textFilePath.setText(path);
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                textFilePath.setText(path);
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


    @OnClick({R.id.btn_openFile, R.id.btn_OK,R.id.btn_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_openFile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                //intent.setType("*/*");//不限制
                intent.setType("text/plain");//txt文件
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_OK:
                hideInputKeyboard();//隐藏键盘
                saveData();

                String json = Utils.readOffline(textFilePath.getText().toString()).replace("\n", "");//txt中的密文
//                String mima = editMima.getText().toString();//txt中的密文
//                if (mima.length() < 6) {
//                    show_Toast(getResources().getString(R.string.text_xzsb19));
//                } else {
//                    insertData(json, mima);
//                }
                insertData(json, "");
                break;
            case R.id.btn_scan:
                saveData();
                startQrCode();
                break;
        }
    }

    // 开始扫码
    private void startQrCode() {
        // 申请相机权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(DownOfflineActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(DownOfflineActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQ_PERM_EXTERNAL_STORAGE);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(DownOfflineActivity.this, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_QR_CODE);
    }
    private void saveData() {
        saveHistory("history_xmbh", dfAtXmbh);//保存输入的项目编号
        saveHistory("history_htid", dfAtHtid);//保存输入的合同编号
        saveHistory("history_dwdm", dfAtDwdm);//保存输入的合同编号
        saveHistory("history_bprysfz", dfAtBprysfz);//保存输入的身份证号

        initAutoComplete("history_htid", dfAtHtid);//输入历史记录
        initAutoComplete("history_xmbh", dfAtXmbh);
        initAutoComplete("history_dwdm", dfAtDwdm);
        initAutoComplete("history_bprysfz", dfAtBprysfz);
    }


    //隐藏键盘
    public void hideInputKeyboard() {
        dfAtXmbh.clearFocus();//取消焦点
        dfAtHtid.clearFocus();//取消焦点
        dfAtDwdm.clearFocus();
        dfAtBprysfz.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    TextWatcher htbh_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 15) {
                dfAtHtid.setBackgroundColor(Color.GREEN);
            }else {
                dfAtHtid.setBackgroundColor(Color.RED);
            }
        }
    };
    TextWatcher xmbh_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 15) {
                dfAtXmbh.setBackgroundColor(Color.GREEN);
            }else {
                dfAtXmbh.setBackgroundColor(Color.RED);
            }
        }
    };
    TextWatcher sfz_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 18) {
                dfAtBprysfz.setBackgroundColor(Color.GREEN);
            }else {
                dfAtBprysfz.setBackgroundColor(Color.RED);
            }
        }
    };
    TextWatcher dwdm_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 13) {
                dfAtDwdm.setBackgroundColor(Color.GREEN);
            } else {
                dfAtDwdm.setBackgroundColor(Color.RED);
            }
        }
    };

    private void upload_xingbang(String name) {
        Log.e("loding画面", "画面开始: " );
//        String url = Utils.httpurl_xb_erweima+name;//煋邦下载
        String url = Utils.httpurl_xb_erweima_new+name;//煋邦下载
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")//text/plain  application/json  application/x-www-form-urlencoded
                .build();
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                mHandler_1.sendMessage(mHandler_1.obtainMessage(15));
            }

            @Override
            public void onResponse(Call call, Response response) {

                String res;
                try {
                    res = response.body().string();//response.body()只能调用一次,第二次调用就会变成null
                } catch (Exception e) {

                    mHandler_1.sendMessage(mHandler_1.obtainMessage(16));
                    return;
                }
                Gson gson = new Gson();
                ErweimaBean erweimaBean = gson.fromJson(res, ErweimaBean.class);
                if(erweimaBean.getStatus().equals("200")){
                    Message msg = new Message();
                    msg.obj = erweimaBean.getMsg();
                    msg.what=18;
                    mHandler_1.sendMessage(msg);
                }else if(erweimaBean.getStatus().equals("401")){
                    Message msg = new Message();
                    msg.obj = erweimaBean.getMsg();
                    msg.what=99;
                    mHandler_1.sendMessage(msg);
                }

                Log.e("网络请求返回", "response: " + response.toString());
                Log.e("网络请求返回", "res: " + res);
//                Utils.writeRecord("---煋邦离线扫码返回:" + res);

                pb_show = 0;//loding画面结束
            }
        });
    }
}
