package android_serialport_api.xingbang.utils;

import com.baidu.mapapi.http.HttpClient;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 使用okhttpclient发起网络请求，多次new产生OOM问题，为避免该问题，使用单例模式创建okhttpclient
 */
public class OkhttpClientUtils {
    private static OkHttpClient client;

    private OkhttpClientUtils() {}
    public static OkHttpClient getInstance() {
        if (client == null) {
            synchronized (HttpClient.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return client;
    }

    public static Call postCallBack(String url,String json) {
        //取消网络请求是newcall.cancel();
        RequestBody requestBody = new FormBody.Builder()
                .add("param",json.replace("\n", ""))
                .build();
        Request req = new Request.Builder()
                .url(url)
                .post(requestBody)
                // text/plain  application/json  application/x-www-form-urlencoded
                .addHeader("Content-Type", "text/plain")
                .build();
        Call newcall = getInstance().newCall(req);
        return newcall;
    }

    //post请求
    public static void post(int type,String url, String json, Callback callback) {
        //type:1丹灵   2:煋邦
        if (type == 1) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("param",json.replace("\n", ""))
                    .build();
            Request req = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    // text/plain  application/json  application/x-www-form-urlencoded
                    .addHeader("Content-Type", "text/plain")
                    .build();
            getInstance().newCall(req).enqueue(callback);
        } else {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = FormBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json; charset=utf-8")//text/plain  application/json  application/x-www-form-urlencoded
                    .build();
            getInstance().newCall(request).enqueue(callback);
        }
    }

    // get请求
    public static void get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        getInstance().newCall(request).enqueue(callback);
    }


}
