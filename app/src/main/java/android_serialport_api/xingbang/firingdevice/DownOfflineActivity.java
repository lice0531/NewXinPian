package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.utils.AMapUtils;
import android_serialport_api.xingbang.utils.LngLat;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownOfflineActivity extends BaseActivity {
    @BindView(R.id.btn_openFile)
    Button btnOpenFile;
    @BindView(R.id.text_filePath)
    TextView textFilePath;
    @BindView(R.id.btn_OK)
    Button btnOK;
    @BindView(R.id.edit_mima)
    EditText editMima;
    @BindView(R.id.btn_clear_htid)
    Button btnClearHtid;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.btn_clear_xmbh)
    Button btnClearXmbh;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.btn_clear_dwdm)
    Button btnClearDwdm;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.btn_clear_sfz)
    Button btnClearSfz;
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
    private String path;
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler2;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private String TAG = "离线下载";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_offline);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));

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

        dfAtHtid.addTextChangedListener(htbh_watcher);//长度监听
        dfAtXmbh.addTextChangedListener(xmbh_watcher);//长度监听
        dfAtBprysfz.addTextChangedListener(sfz_watcher);//长度监听
    }

    private void insertData(String json, String mima) {

        String key = "jadl12345678901234" + mima;
        String res;
        String tx_htid = dfAtHtid.getText().toString().trim().replace(" ", "");//合同编号 15位
        String tv_xmbh = dfAtXmbh.getText().toString().trim().replace(" ", "");//项目编号
        String tv_dwdm = dfAtDwdm.getText().toString().trim();//单位代码 13位
        String tv_sfz = dfAtBprysfz.getText().toString().trim();//单位代码 13位
        if (StringUtils.isBlank(tx_htid) && StringUtils.isBlank(tv_xmbh) && StringUtils.isBlank(tv_dwdm)) {
            show_Toast("合同编号,项目编号,单位代码不能同时为空");
            return;
        }
        if (StringUtils.isBlank(tv_sfz)) {
            show_Toast("爆破人员身份证号不能为空");
            return;
        }
        try {
            res = new String(MyUtils.decryptMode(key.getBytes(), Base64.decode(json, Base64.DEFAULT)));
            Utils.writeLog("离线下载" + res);
        } catch (IllegalArgumentException e) {
            show_Toast("解密失败,请检查txt文件是否正确或密码是否正确");
            e.printStackTrace();
            return;
        } catch (NullPointerException e) {
            show_Toast("解密失败,请核对信息是否正确后再次尝试");
            e.printStackTrace();
            return;
        }
        Gson gson = new Gson();
        DanLingBean danLingBean = gson.fromJson(res, DanLingBean.class);
        pb_show = 0;//结束动画
        try {
            JSONObject object1 = new JSONObject(res);
            String cwxx = object1.getString("cwxx");
            if (cwxx.equals("0")) {

                int err = 0;
                for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                    if (!danLingBean.getLgs().getLg().get(i).getGzmcwxx().equals("0")) {
                        err++;
                    }
                }
                if (danLingBean.getCwxx().equals("0")) {
                    if (danLingBean.getZbqys().getZbqy().size() > 0) {
                        for (int i = 0; i < danLingBean.getZbqys().getZbqy().size(); i++) {
                                insertJson(tx_htid, tv_xmbh, res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
                        }
                    }
                }

                if (err != 0) {
                    show_Toast(danLingBean.getZbqys().getZbqy().get(0).getZbqymc() + "下载的雷管出现错误,请检查数据");
                }
                show_Toast("项目下载成功");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向数据库中插入数据
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
//        Utils.saveFile();//把软存中的数据存入磁盘中
        saveData(htbh, xmbh, dfAtBprysfz.getText().toString().trim(), coordxy);
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
        if (resultCode == Activity.RESULT_OK) {
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


    @OnClick({R.id.btn_openFile, R.id.btn_OK})
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
                String mima = editMima.getText().toString();//txt中的密文
                if (mima.length() < 6) {
                    show_Toast("请输入文件6位序列号");
                } else {
                    insertData(json, mima);
                }

                break;
        }
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
}
