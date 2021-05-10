package android_serialport_api.xingbang.firingdevice;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.MyProgressDialog;
import android_serialport_api.xingbang.utils.Utils;
import android_serialport_api.xingbang.utils.VerifyModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadFaceDataActivity extends BaseActivity {

    @BindView(R.id.et_upload_name)
    AutoCompleteTextView etUploadName;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.et_upload_position)
    AutoCompleteTextView etUploadPosition;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.et_upload_bprysfz)
    EditText etUploadBprysfz;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.btn_pic)
    Button btnPic;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.ly_setUpdata)
    LinearLayout lySetUpdata;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.tv_photo_src)
    TextView tvPhotoSrc;
    private String equ_no = "";//设备编码
    private VerifyModel verifyModel;
    private MyProgressDialog instance;
    private static File file_c;//处理后的图片
    private static String pic;//转成字符串的图片
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_message);
        ButterKnife.bind(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 21);
        db = mMyDatabaseHelper.getWritableDatabase();
        getUserMessage();
    }
    private void getUserMessage() {
        String selection = "id = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"1"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_MESSQGE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {  //cursor不位空,可以移动到第一行
            equ_no = cursor.getString(4);
            cursor.close();
        }
        if (equ_no.equals("")){
            show_Toast("设备编号为空,请先设置设备编号");
        }
    }

    private void upload() {
        File file = new File(verifyModel.imageUri);
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), newOpts);// 此时返回bm为空
        //设置参数
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        if (w > 720 || h > 1080) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            File dir = file.getParentFile();
            Date date = new Date();
            String format = timeFormat.format(date);
            file_c = new File(dir, format+".jpg");
            // 现在主流手机比较多是800*4
            float wR = w / 720.00f;
            float hR = h / 1080.00f;
            float r=1;
            if (wR >= hR && hR>=1) {
                r = wR;
            } else if (hR>wR && wR>=1){
                r = hR;
            } else{
                r=1;
            }
            newOpts.inSampleSize = (int) r;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            newOpts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), newOpts);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file_c));

                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
                bos.flush();
                bos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            String photo =Utils.imageToBase64(file_c.getAbsolutePath());
            pic=photo;
        } else {
            String photo =Utils.imageToBase64(file.getAbsolutePath());
            pic=photo;
        }
        String url = Utils.httpurl_face;//人脸识别
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody2 = new FormBody.Builder()
                .add("sbbh",equ_no)
                .add("pic",pic.replace("\n",""))
                .add("name",etUploadName.getText().toString())
                .add("sfzh",etUploadBprysfz.getText().toString())
                .add("pathVar","apiFaceVerify/faceVerifyInfoUp.do")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody2)
                .removeHeader("User-Agent")
                .addHeader("user-agent", "webservice")
                .addHeader("token","REJfVGFibGVfQ2FjaGU6Rml4ZWQ6T1VUU0lERV9BQ0NFU1NfSU5GTzozMDlhZmIyNmM2MDE0NDgwOTQ3MDljZTNhMTM4ZWY4N05qY3dNekJtWm1ZdFpHVm1ZaTAwWTJOakxUazVNbUl0TkRZek1HUXpPRGhoTldKaA==" )
                .build();
        //.addHeader("Content-Type", "text/plain")//text/plain  application/json  application/x-www-form-urlencoded
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("网络请求失败", "IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string().toString();
                Log.e("网络请求成功", "res: " + res);
                show_Toast_ui("上传成功");
                Gson gson = new Gson();

            }
        });
    }

    @Override
    public void onActivityResult(final int req, final int res, Intent data) {
        Log.e("人脸识别页面--", "onActivityResult:1 ");
        switch (req) {

            /**
             * 拍照的请求标志
             */
            case VerifyModel.PHOTO_WITH_CAMERA:
                if (res == RESULT_OK) {
                    Log.e("tag", "onActivityResult:2 ");
                    //拍照成功
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            instance = MyProgressDialog.getInstance(UploadFaceDataActivity.this, "正在验证身份...");
//                            instance.show();
//
//                        }
//                    });
                    File photo = new File(verifyModel.imageUri);
                    tvPhotoSrc.setText(verifyModel.imageUri);
//                    try {
//                        doNotify(checkCount, photo);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    //上传图片
//                    final HashMap<String, String> map = new HashMap<>();
//
//                    map.put("imei", "1");
//                    map.put("imei", "2");
//                    map.put("imei", "3");
                }

                break;
            default:
                break;
        }
    }


    @OnClick({R.id.btn_pic, R.id.btn_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pic:
                verifyModel = VerifyModel.getInstance(this);
                verifyModel.takePhone();
                break;
            case R.id.btn_upload:
                upload();
                break;
        }
    }
}
