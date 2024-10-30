package android_serialport_api.mx.xingbang.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class DownloadTest {
    private Context mContext;
    private String mSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xb";

    public DownloadTest(Context context) {
        mContext = context;
    }

    public long downloadAPK(String downloadurl, DownloadManager downloadManager,String app_name) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadurl));

        //设置Notifcaiton的标题和描述
        request.setTitle(app_name);
        request.setDescription("最新版本下载中.....");

        //指定在WIFI状态下，执行下载操作。
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //指定在MOBILE状态下，执行下载操作
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        //下载情况是否显示在systemUI下拉状态栏中
        request.setVisibleInDownloadsUi(true);

        //设置Notification的显示，和隐藏
        /*
        在下载进行中时显示，在下载完成后就不显示了。可以设置如下三个值：
        VISIBILITY_HIDDEN 下载UI不会显示，也不会显示在通知中，如果设置该值，需要声明android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
        VISIBILITY_VISIBLE 当处于下载中状态时，可以在通知栏中显示；当下载完成后，通知栏中不显示
        VISIBILITY_VISIBLE_NOTIFY_COMPLETED 当处于下载中状态和下载完成时状态，均在通知栏中显示
        VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION 只在下载完成时显示在通知栏中。
        * */
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();

        //设置下载路径 sdcard/download/
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, app_name);

        //加入到下载的队列中。一旦下载管理器准备好执行并且连接可用，下载将自动启动。
        //一个下载任务对应唯一个ID， 此id可以用来去查询下载内容的相关信息
        long downloadID = downloadManager.enqueue(request);

        return downloadID;
    }
}
