package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WriteLogActivity extends BaseActivity {

    @BindView(R.id.tv_log)
    TextView tvLog;
    @BindView(R.id.btn_log_del)
    Button btnLogDel;

    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler_loding = new Handler();//显示进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_log);
        ButterKnife.bind(this);
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
//        mHandler_loding = new Handler(message -> {
//            if (pb_show == 1 && tipDlg != null) tipDlg.show();
//            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
//            return false;
//        });
//        pb_show = 1;
        runPbDialog();
//        new Thread(() -> {
//            String log = Utils.readLog();
//            tvLog.setText(log);
//            pb_show = 0;
//        }).start();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String log =bundle.getString("log");
        Log.e("日志", "log: "+log );
        tvLog.setText(log);
    }

    private void runPbDialog() {
//        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
        divider.setBackgroundColor(Color.TRANSPARENT);
        //tipDlg.setMessage("正在操作,请等待...").show();

        new Thread(() -> {
            mHandler_loding.sendMessage(mHandler_loding.obtainMessage());
            try {
                while (pb_show == 1) {
                    Thread.sleep(100);
                }
                mHandler_loding.sendMessage(mHandler_loding.obtainMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @OnClick(R.id.btn_log_del)
    public void onViewClicked() {
        String pathname = Environment.getExternalStorageDirectory().toString() + File.separator + "错误日志" + File.separator + "错误日志.txt";
        Utils.delete(pathname);
        show_Toast_long("当前日志已删除");
//                String log = Utils.readLog();
//                tvLog.setText(log);
        tvLog.setText("");
//        pb_show = 1;
//        runPbDialog();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                pb_show = 0;
//            }
//        }).start();

    }
}
