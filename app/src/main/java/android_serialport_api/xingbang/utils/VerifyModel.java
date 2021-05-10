package android_serialport_api.xingbang.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VerifyModel {
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1000;
    public static final int PHOTO_WITH_CAMERA = 1001;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    Activity context;

    //進行拍照
    private static VerifyModel verifyModel;
    private String imageUriDir;
    private String timeUriDir;
    public String imageUri;
    public String timeUri;
    private final File dir;

    private VerifyModel(Activity context) {
        this.context = context;
        imageUriDir = Environment.getExternalStorageDirectory() + "/beng/photos/";
        timeUri = Environment.getExternalStorageDirectory() + "/beng/time/";
        dir = new File(imageUriDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        timeUri = timeUriDir + "file.txt";
    }

    public static VerifyModel getInstance(Activity context) {
        verifyModel = new VerifyModel(context);
        return verifyModel;
    }

    //進行身份認證
    public void verify() {


    }

    public void takePhone() {
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);

            } else {
                takePhoto();

            }
        } else {
            takePhoto();
        }
    }

    public void takePhoto() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            Date date = new Date();
            String format = formatter.format(date);
            imageUri=imageUriDir+format+".jpg";
            Log.e("拍照模块", "照片路径takePhoto: "+imageUri);
            Uri uri = Uri.fromFile(new File(imageUri));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            context.startActivityForResult(intent, PHOTO_WITH_CAMERA);
        } else {
            Toast.makeText(context, "没有SD卡", Toast.LENGTH_LONG).show();
        }
    }

    public void clearImageDir() {
        if (dir != null && dir.exists()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }
    }
}
