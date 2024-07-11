package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.FaceBean;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.utils.MyProgressDialog;
import android_serialport_api.xingbang.utils.NetUtils;
import android_serialport_api.xingbang.utils.SharedPreferencesHelper;
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


@SuppressLint("LongLogTag")
public class XingBangApproveActivity extends BaseActivity {

    @BindView(R.id.tv_ocation)
    TextView tv_ocation;
    @BindView(R.id.bt_approve1)
    Button btApprove1;
    @BindView(R.id.bt_approve2)
    Button btApprove2;
    @BindView(R.id.bt_approve3)
    Button btApprove3;
    @BindView(R.id.bt_all)
    Button btAll;
    @BindView(R.id.tv_approve_name1)
    TextView tvApproveName1;
    @BindView(R.id.tv_approve_name2)
    TextView tvApproveName2;
    @BindView(R.id.tv_approve_name3)
    TextView tvApproveName3;
    @BindView(R.id.et_approve_sfz1)
    AutoCompleteTextView etApproveSfz1;
    @BindView(R.id.et_approve_sfz2)
    AutoCompleteTextView etApproveSfz2;
    @BindView(R.id.et_approve_sfz3)
    AutoCompleteTextView etApproveSfz3;
    @BindView(R.id.btn_del_his_1)
    Button btnDelHis1;
    @BindView(R.id.btn_del_his_2)
    Button btnDelHis2;
    @BindView(R.id.btn_del_his_3)
    Button btnDelHis3;
    @BindView(R.id.btn_del_his_4)
    Button btnDelHis4;
    @BindView(R.id.rl_del_his_1)
    LinearLayout rlDelHis1;
    private VerifyModel verifyModel;
    int checkCount = 0;
    boolean isChecked1 = false;
    boolean isChecked2 = false;
    boolean isChecked3 = false;
    private boolean isChecked;
    private MyProgressDialog instance;
    private long prelongTim = 0;//定义上一次单击的时间
    private long curTime = 0;//定义上第二次单击的时间
    private int THYDAY_HOUR_TIME = 600000;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private boolean isactive = false;
    private static File file_c;//处理后的图片
    private static String pic;//转成字符串的图片
    private String equ_no = "";//设备编码
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPhotoError();
        setContentView(R.layout.activity_xing_bang_approve);
        ButterKnife.bind(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(XingBangApproveActivity.this, getApplicationContext().getPackageName());
        initTime();
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getWritableDatabase();
        getUserMessage();
        if (equ_no.equals("")) {
            show_Toast("设备编号为空,请先设置设备编号");
        }
        initAutoComplete("etApproveSfz1", etApproveSfz1);//输入历史记录
        initAutoComplete("etApproveSfz2", etApproveSfz2);
        initAutoComplete("etApproveSfz3", etApproveSfz3);
    }

    private void getUserMessage() {
        String selection = "id = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"1"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_MESSQGE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {  //cursor不位空,可以移动到第一行
            equ_no = cursor.getString(4);
            cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public static String getPicNameFromPath(String picturePath) {
        String temp[] = picturePath.replaceAll("\\\\", "/").split("/");
        String fileName = "baseimg\\/1\\/Tonny2.jpg";
        if (temp.length > 1) {
            fileName = temp[temp.length - 1];
        }
        return fileName;
    }


    //获取系统时间
    private void initTime() {
        if (TextUtils.isEmpty(sharedPreferencesHelper.getString("time", ""))) {
            //进入时判断是否存过time值，如果没存过，按照第一次逻辑走，如果存过则进行时间校验
            isactive = false;
            return;
        }
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean isChecked1 = sharedPreferencesHelper.getBoolean("isChecked1", false);
        boolean isChecked2 = sharedPreferencesHelper.getBoolean("isChecked2", false);
        boolean isChecked3 = sharedPreferencesHelper.getBoolean("isChecked3", false);


        if (isChecked1 && isChecked2 && isChecked3) {
            String time = sharedPreferencesHelper.getString("time", "");
            try {
                Date format = formatter.parse(time);
                if (!(currentTime.getTime() - format.getTime() < THYDAY_HOUR_TIME)) {
                    sharedPreferencesHelper.put("isChecked1", false);
                    sharedPreferencesHelper.put("isChecked2", false);
                    sharedPreferencesHelper.put("isChecked3", false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            ToastUtil.show("验证失效，请重新验证");
                            btApprove1.setText("认证");
                            btApprove2.setText("认证");
                            btApprove3.setText("认证");
                            btApprove1.setEnabled(true);
                            btApprove2.setEnabled(true);
                            btApprove3.setEnabled(true);
                            btApprove1.setBackgroundResource(R.drawable.bt_mainpage_style);
                            btApprove2.setBackgroundResource(R.drawable.bt_mainpage_style);
                            btApprove3.setBackgroundResource(R.drawable.bt_mainpage_style);
                            btAll.setVisibility(View.GONE);
                            isactive = false;
                            return;
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            show_Toast("认证识别在有效期内");
                            tvApproveName1.setText(sharedPreferencesHelper.getString("name1", ""));
                            tvApproveName2.setText(sharedPreferencesHelper.getString("name2", ""));
                            tvApproveName3.setText(sharedPreferencesHelper.getString("name3", ""));
                            btApprove1.setText("通过");
                            btApprove2.setText("通过");
                            btApprove3.setText("通过");
                            btApprove1.setEnabled(false);
                            btApprove2.setEnabled(false);
                            btApprove3.setEnabled(false);
                            btAll.setVisibility(View.VISIBLE);
                            isactive = true;
                        }
                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 初始化AutoCompleteTextView，最多显示5项提示，使
     * AutoCompleteTextView在一开始获得焦点时自动提示
     *
     * @param field 保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
//    private void initAutoComplete(String field, final AutoCompleteTextView auto) {
//        SharedPreferences sp = getSharedPreferences("face_sfz", 0);
//        String longhistory = sp.getString(field, "当前无记录");
//        final String[] hisArrays = longhistory.split(",");
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                R.layout.item_auto_textview, hisArrays);
//        //只保留最近的10条的记录
//        if (hisArrays.length > 10) {
//            String[] newArrays = new String[10];
//            System.arraycopy(hisArrays, 0, newArrays, 0, 10);
//            adapter = new ArrayAdapter<>(this, R.layout.item_auto_textview, newArrays);
//        }
//        auto.setAdapter(adapter);
//        auto.setDropDownHeight(500);
//        auto.setDropDownWidth(450);
//        auto.setThreshold(1);
//        auto.setCompletionHint("最近的10条记录");
//        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                AutoCompleteTextView view = (AutoCompleteTextView) v;
//                if (hasFocus) {
//                    view.showDropDown();
//                }
//            }
//        });
//    }

    /**
     * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符段
     *
     * @param field 保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
//    private void saveHistory(String field, AutoCompleteTextView auto) {
//        String text = auto.getText().toString();
//        SharedPreferences sp = getSharedPreferences("face_sfz", 0);
//        String longhistory = sp.getString(field, "");
//        if (!longhistory.contains(text + ",")) {
//            StringBuilder sb = new StringBuilder(longhistory);
//            sb.insert(0, text + ",");
//            sp.edit().putString(field, sb.toString()).commit();
//        }
//    }

    private void deleteHistory(String field, AutoCompleteTextView auto) {
        SharedPreferences sp = getSharedPreferences("face_sfz", 0);
        sp.edit().putString(field, "").commit();
    }


    private void initNet() {
        boolean connected = NetUtils.isConnected(XingBangApproveActivity.this);
        if (connected) {
            boolean wifi = NetUtils.isWifi(XingBangApproveActivity.this);
            boolean rd = NetUtils.is3rd(XingBangApproveActivity.this);
            if (wifi) {
                show_Toast("WIFI已经连接");
            } else if (rd) {
                show_Toast("手机流量已经连接");
            } else {
                show_Toast("网络连接不可用，请检查网络设置");
                NetUtils.openSetting(XingBangApproveActivity.this);
            }
        } else {
            show_Toast("网络连接不可用，请检查网络设置");
            NetUtils.openSetting(XingBangApproveActivity.this);
        }
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            instance = MyProgressDialog.getInstance(XingBangApproveActivity.this, "正在验证身份...");
                            instance.show();

                        }
                    });
                    File photo = new File(verifyModel.imageUri);

                    upload(checkCount, photo);
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }


    private void upload(int type, File photo) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(), newOpts);// 此时返回bm为空
        //设置参数
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        if (w > 720 || h > 1080) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            File dir = photo.getParentFile();
            Date date = new Date();
            String format = timeFormat.format(date);
            file_c = new File(dir, format + ".jpg");
            // 现在主流手机比较多是800*4
            float wR = w / 720.00f;
            float hR = h / 1080.00f;
            float r = 1;
            if (wR >= hR && hR >= 1) {
                r = wR;
            } else if (hR > wR && wR >= 1) {
                r = hR;
            } else {
                r = 1;
            }
            newOpts.inSampleSize = (int) r;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            newOpts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(), newOpts);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file_c));

                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
                bos.flush();
                bos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            pic = Utils.imageToBase64(file_c.getAbsolutePath());
        } else {
            pic = Utils.imageToBase64(photo.getAbsolutePath());
        }
        String url = Utils.httpurl_face;//人脸识别
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody2;
        if (type == 1) {
            Log.e("身份证号", "upload: " + etApproveSfz1.getText().toString());
            if (etApproveSfz1.getText().toString().isEmpty()) {
                show_Toast("身份证号为空");
                instance.hide();
                return;
            }
            requestBody2 = new FormBody.Builder()
                    .add("sbbh", equ_no)
                    .add("pic", pic.replace("\n", ""))
                    .add("sfzh", etApproveSfz1.getText().toString())
                    .add("pathVar", "apiFaceVerify/faceVerify.do")
                    .build();
        } else if (type == 2) {
            if (etApproveSfz2.getText().toString().isEmpty()) {
                show_Toast("身份证号为空");
                instance.hide();
                return;
            }
            requestBody2 = new FormBody.Builder()
                    .add("sbbh", equ_no)
                    .add("pic", pic.replace("\n", ""))
                    .add("sfzh", etApproveSfz2.getText().toString())
                    .add("pathVar", "apiFaceVerify/faceVerify.do")
                    .build();
        } else {
            if (etApproveSfz3.getText().toString().isEmpty()) {
                show_Toast("身份证号为空");
                instance.hide();
                return;
            }
            requestBody2 = new FormBody.Builder()
                    .add("sbbh", equ_no)
                    .add("pic", pic.replace("\n", ""))
                    .add("sfzh", etApproveSfz3.getText().toString())
                    .add("pathVar", "apiFaceVerify/faceVerify.do")
                    .build();
        }

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody2)
                .removeHeader("User-Agent")
                .addHeader("user-agent", "webservice")
                .addHeader("token", "REJfVGFibGVfQ2FjaGU6Rml4ZWQ6T1VUU0lERV9BQ0NFU1NfSU5GTzozMDlhZmIyNmM2MDE0NDgwOTQ3MDljZTNhMTM4ZWY4N05qY3dNekJtWm1ZdFpHVm1ZaTAwWTJOakxUazVNbUl0TkRZek1HUXpPRGhoTldKaA==")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("认证页面", "onActivityResult:访问失败 " + e);
                verifyModel.clearImageDir();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        instance.hide();
                        show_Toast("验证未通过，请重新验证");
                        if (checkCount == 1) {
                            btApprove1.setText("请重新验证");
                            btApprove1.setTextSize(14);
                        } else if (checkCount == 2) {
                            btApprove2.setText("请重新验证");
                        } else if (checkCount == 3) {
                            btApprove3.setText("请重新验证");
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string().toString();
                Log.e("网络请求成功", "res: " + res);
                Gson gson = new Gson();
                final FaceBean faceBean = gson.fromJson(res, FaceBean.class);
                if (faceBean.getIsSuccess().equals("true")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            instance.hide();
                            final String name = faceBean.getResult().getName();
                            final int success = faceBean.getResult().getSftg();
                            if (success == 1) {
                                isChecked = true;
                            } else {
                                isChecked = false;
                            }
                            if (isChecked) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        instance.hide();
                                        show_Toast("验证通过");
                                        if (checkCount == 1) {
                                            isChecked1 = true;
                                            sharedPreferencesHelper.put("isChecked1", isChecked1);
                                            btApprove1.setText("验证通过");
                                            // TODO 认证通过设置人名
                                            Log.e("认证页面--认证通过", "name--" + name);
                                            tvApproveName1.setVisibility(View.VISIBLE);
                                            tvApproveName1.setText(name);
                                            sharedPreferencesHelper.put("name1", name);
                                            btApprove1.setEnabled(false);

                                        } else if (checkCount == 2) {//其他两个认证
                                            isChecked2 = true;
                                            sharedPreferencesHelper.put("isChecked2", isChecked2);
                                            tvApproveName2.setText(name);
                                            btApprove2.setText("验证通过");
                                            // TODO 认证通过设置人名
                                            String picNameFromPath = getPicNameFromPath(name).replace(".jpg", "")
                                                    .replace(".jpeg", "")
                                                    .replace(".png", "")
                                                    .replace(".gif", "");
                                            Log.e("test", "run: 这个就是：" + picNameFromPath);
                                            tvApproveName2.setVisibility(View.VISIBLE);
                                            tvApproveName2.setText(name);
                                            sharedPreferencesHelper.put("name2", name);
                                            btApprove2.setEnabled(false);
                                        } else if (checkCount == 3) {
                                            isChecked3 = true;
                                            sharedPreferencesHelper.put("isChecked3", isChecked3);
                                            tvApproveName3.setVisibility(View.VISIBLE);
                                            tvApproveName3.setText(name);
                                            btApprove3.setText("验证通过");
                                            // TODO 认证通过设置人名
                                            String picNameFromPath = getPicNameFromPath(name).replace(".jpg", "")
                                                    .replace(".jpeg", "")
                                                    .replace(".png", "")
                                                    .replace(".gif", "");
                                            Log.e("test", "run: 这个就是：" + picNameFromPath);
                                            tvApproveName3.setText(name);
                                            sharedPreferencesHelper.put("name3", name);
                                            btApprove3.setEnabled(false);
                                            btAll.setVisibility(View.VISIBLE);
                                        }
//                                            //都认证通过后，记录通过时间
                                        isactive = true;
                                        Date currentTime = new Date();
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        sharedPreferencesHelper.put("time", formatter.format(currentTime));
//                                        }

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        instance.hide();
                                        show_Toast("验证未通过");
                                        if (checkCount == 1) {
                                            btApprove1.setText("请重新验证");
                                        } else if (checkCount == 2) {
                                            btApprove2.setText("请重新验证");
                                        } else if (checkCount == 3) {
                                            btApprove3.setText("请重新验证");
                                        }

                                    }
                                });
                            }
                        }

                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            instance.hide();
                            show_Toast("验证未通过" + faceBean.getErrMsg());
                            if (checkCount == 1) {
                                btApprove1.setText("请重新验证");
                            } else if (checkCount == 2) {
                                btApprove2.setText("请重新验证");
                            } else if (checkCount == 3) {
                                btApprove3.setText("请重新验证");
                            }

                        }
                    });
                }


            }
        });
    }


    @OnClick({R.id.bt_approve1, R.id.bt_approve2, R.id.bt_approve3,
            R.id.bt_all,R.id.btn_del_his_1, R.id.btn_del_his_2, R.id.btn_del_his_3, R.id.btn_del_his_4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_approve1:
                if (etApproveSfz1.getText().toString().isEmpty()) {
                    show_Toast("请先输入身份证号");
                    return;
                }
                verifyModel = VerifyModel.getInstance(this);
                checkCount = 1;
                verifyModel.takePhone();
                saveHistory("etApproveSfz1", etApproveSfz1);//保存输入的身份证号
                initAutoComplete("etApproveSfz1", etApproveSfz1);
                break;
            case R.id.bt_approve2:
                if (etApproveSfz2.getText().toString().isEmpty()) {
                    show_Toast("请先输入身份证号");
                    return;
                }
                if (isChecked1) {
                    checkCount = 2;
                    verifyModel.takePhone();
                } else {
                    show_Toast("请先完成技术员认证");
                }
                saveHistory("etApproveSfz2", etApproveSfz2);//保存输入的身份证号
                initAutoComplete("etApproveSfz2", etApproveSfz2);
                break;
            case R.id.bt_approve3:
                if (etApproveSfz3.getText().toString().isEmpty()) {
                    show_Toast("请先输入身份证号");
                    return;
                }
                if (isChecked2) {
                    checkCount = 3;
                    verifyModel.takePhone();
                } else {
                    show_Toast("请先完成爆破员认证");
                }
                saveHistory("etApproveSfz3", etApproveSfz3);//保存输入的身份证号
                initAutoComplete("etApproveSfz3", etApproveSfz3);
                break;
            case R.id.bt_all:
                boolean isChecked1 = sharedPreferencesHelper.getBoolean("isChecked1", false);
                boolean isChecked2 = sharedPreferencesHelper.getBoolean("isChecked2", false);
                boolean isChecked3 = sharedPreferencesHelper.getBoolean("isChecked3", false);
                if (isChecked1 && isChecked2 && isChecked3) {
                    initTime();
                }
                if (isactive) {
                    Intent intent = new Intent(XingBangApproveActivity.this, FiringMainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("qbxm_id", "-1");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    show_Toast("请先进行认证！");
                }
                break;
            case R.id.btn_del_his_1:
                deleteHistory("etApproveSfz1",etApproveSfz1);
                break;
            case R.id.btn_del_his_2:
                deleteHistory("etApproveSfz2",etApproveSfz2);
                break;
            case R.id.btn_del_his_3:
                deleteHistory("etApproveSfz3",etApproveSfz3);
                break;
            case R.id.btn_del_his_4:
                if (btnDelHis1.getVisibility()==View.INVISIBLE){
                    btnDelHis1.setVisibility(View.VISIBLE);
                    btnDelHis2.setVisibility(View.VISIBLE);
                    btnDelHis3.setVisibility(View.VISIBLE);
                }else {
                    btnDelHis1.setVisibility(View.INVISIBLE);
                    btnDelHis2.setVisibility(View.INVISIBLE);
                    btnDelHis3.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

}
