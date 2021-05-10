package android_serialport_api.xingbang.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import android_serialport_api.xingbang.R;

/**
 * Created by guaju on 2018/10/16.
 */

public class MyProgressDialog {
    Activity activity;
    String title;
    static MyProgressDialog myProgressDialog;
    AlertDialog dialog;

    private MyProgressDialog(Activity activity, String title) {
        this.title = title;
        this.activity = activity;
        View view = View.inflate(activity, R.layout.my_progress_dialog, null);
        TextView tv_title = (TextView)view.findViewById(R.id.dialog_title);
        tv_title.setText(title);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);

    }

    public static MyProgressDialog getInstance(Activity activity, String title) {
        if (myProgressDialog == null) {
            myProgressDialog = new MyProgressDialog(activity, title);
        }
        return myProgressDialog;

    }
    public void show(){
        if (dialog!=null&&!dialog.isShowing()){
            dialog.show();
        }
    }
    public void hide(){
        if (dialog!=null&&dialog.isShowing()){
            dialog.hide();
            drop();
        }
    }
    public void drop(){
        if (dialog!=null){
            dialog=null;
        }
        if (myProgressDialog!=null){
            myProgressDialog=null;
        }
    }
}
