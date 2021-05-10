package android_serialport_api.xingbang.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import org.json.JSONException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UploadHelper1 {

    public static final String TAG = "UploadHelper";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    //这是okhttp  ,,这是老吴搞得那个图片压缩
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.SECONDS)//超时时间
            .readTimeout(5000, TimeUnit.SECONDS)
            .build();
    private static File file_c;
    private static RequestBody fileBody;
    private static MultipartBody.Builder builder;
    private static String pic;

    public static String upload(final String url, final String fileParamName, final File file, int type, String szImei, Callback callback) throws JSONException {//String areaid,
        Log.e("上传图片--", "url: "+url);
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
            String photo =imageToBase64(file_c.getAbsolutePath());
            pic=photo;
        } else {
            String photo =imageToBase64(file.getAbsolutePath());
            pic=photo;
        }
        //原始请求参数
        RequestBody requestBody2 = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("sbbh", szImei)
                .addFormDataPart("pic", pic)
                .addFormDataPart("name", "测试")
                .addFormDataPart("sfzh", "452601199105033919")
                .addFormDataPart("pathVar", "apiFaceVerify/faceVerify.do")
                .build();
//        Utils.writeLog(pic);
//        FileUtil.writeData(content);
        Request request = new Request.Builder()
                .url(Utils.httpurl_face)//
                .post(requestBody2)
                .removeHeader("User-Agent")
                .addHeader("user-agent", "webservice")
                .addHeader("token","REJfVGFibGVfQ2FjaGU6Rml4ZWQ6T1VUU0lERV9BQ0NFU1NfSU5GTzozMDlhZmIyNmM2MDE0NDgwOTQ3MDljZTNhMTM4ZWY4N05qY3dNekJtWm1ZdFpHVm1ZaTAwWTJOakxUazVNbUl0TkRZek1HUXpPRGhoTldKaA==" )//text/plain  application/json  application/x-www-form-urlencoded
                .build();//"Content-Type","application/json"
        client.newCall(request).enqueue(callback);
        Log.e("上传图片--", "网络请求完成: ");
        //http://pm.xingbangtech.com/webservice/UserInfoWebService.asmx/faceup?ID=&time=&areaid=&purl=
        //http://guangxi.xingbangtech.com:38888/www/test.php
        //http://guangxi.xingbangtech.com:38888/www/upload.php

        return null;
    }
    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path){
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }
}