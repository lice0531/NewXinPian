package android_serialport_api.xingbang.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import android_serialport_api.xingbang.R;

/***
 * 弹框提示
 */
public class MyAlertDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private LinearLayout ll_start;
    private LinearLayout ll_kong;
    private LinearLayout ll_dijian;
    private LinearLayout ll_fanzhuan;
    private TextView txt_title;
    private TextView txt_pai;
    private TextView txt_msg;
    private Button btn_neg;
    private Button btn_pos;
    private Button btn_fanzhuan;
    private ImageView img_line;
    private Display display;
    private boolean showTitle = false;
    private boolean showStart = false;
    private boolean showFa = false;
    private boolean showDijian = false;
    private boolean showFanZhuan = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    View view;
    public MyAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public MyAlertDialog builder() {
         view = LayoutInflater.from(context).inflate(
                R.layout.view_alert_dialog, null);

        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        ll_start = (LinearLayout) view.findViewById(R.id.ll_start);
        ll_kong = (LinearLayout) view.findViewById(R.id.ll_fa);
        ll_dijian = (LinearLayout) view.findViewById(R.id.ll_dijian);
        ll_fanzhuan = (LinearLayout) view.findViewById(R.id.ll_fanzhuan);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_pai = (TextView) view.findViewById(R.id.txt_pai);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_fanzhuan = (Button) view.findViewById(R.id.btn_fanzhuan);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        setGone();
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

        return this;
    }

    /**
     * 恢复初始
     * @return
     */
    public MyAlertDialog setGone() {
        if (lLayout_bg != null) {
            txt_title.setVisibility(View.GONE);
            ll_start.setVisibility(View.GONE);
            ll_kong.setVisibility(View.GONE);
            ll_dijian.setVisibility(View.GONE);
            ll_fanzhuan.setVisibility(View.GONE);
//            txt_msg.setVisibility(View.GONE);
            btn_neg.setVisibility(View.GONE);
            btn_pos.setVisibility(View.GONE);
            img_line.setVisibility(View.GONE);

        }
        showTitle = false;
        showMsg = false;
        showPosBtn = false;
        showNegBtn = false;
        return this;
    }

    public View getView() {

        return view;
    }
    /**
     * 设置title
     * @param title
     * @return
     */
    public MyAlertDialog setTitle(String title) {
        showTitle = true;
        if (TextUtils.isEmpty(title)) {
            txt_title.setText("提示");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    /**
     * 设置title
     * @return
     */
    public MyAlertDialog setStart() {
        showStart = true;
        return this;
    }/**
     * 设置title
     * @return
     */
    public MyAlertDialog setFa() {
        showFa = true;
        return this;
    }/**
     * 设置title
     * @return
     */
    public MyAlertDialog setDijian() {
        showDijian = true;
        return this;
    }/**
     * 设置title
     * @return
     */
    public MyAlertDialog setFanZhuan() {
        showFanZhuan = true;
        return this;
    }

    /**
     * 设置title
     * @return
     */
    public MyAlertDialog setpaiText(String paiText) {
        txt_pai.setText(paiText);
        return this;
    }

    /**
     * 设置Message
     * @param msg
     * @return
     */
    public MyAlertDialog setMsg(String msg) {
        showMsg = true;
        if (TextUtils.isEmpty(msg)) {
            txt_msg.setText("");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    /**
     * 设置点击外部是否消失
     * @param cancel
     * @return
     */
    public MyAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    /**
     * 右侧按钮
     *
     * @param text
     * @param listener
     * @return
     */
    public MyAlertDialog setPositiveButton(String text,
                                           final OnClickListener listener) {
        return setPositiveButton(text, -1, listener);
    }

    public MyAlertDialog setPositiveButton(String text, int color,
                                           final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("");
        } else {
            btn_pos.setText(text);
        }
        if (color == -1) {
            color = R.color.action_sheet_blue;
        }
        btn_pos.setTextColor(ContextCompat.getColor(context, color));
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(v);
                dismiss();
            }
        });
        return this;
    }

    /**
     * 左侧按钮
     *
     * @param text
     * @param listener
     * @return
     */

    public MyAlertDialog setNegativeButton(String text,
                                           final OnClickListener listener) {

        return setNegativeButton(text, -1, listener);
    }

    public MyAlertDialog setNegativeButton(String text, int color,
                                           final OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("");
        } else {
            btn_neg.setText(text);
        }
        if (color == -1) {
            color = R.color.action_sheet_blue;
        }
        btn_neg.setTextColor(ContextCompat.getColor(context, color));

        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(v);
                dismiss();
            }
        });
        return this;
    }

    /**
     * 设置显示
     */
    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }
        if (showStart) {
            ll_start.setVisibility(View.VISIBLE);
        }
        if (showFa) {
            ll_kong.setVisibility(View.VISIBLE);
        }
        if (showDijian) {
            ll_dijian.setVisibility(View.VISIBLE);
        }
        if (showFanZhuan) {
            ll_fanzhuan.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alert_dialog_selector);
            btn_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alert_dialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alert_dialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alert_dialog_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alert_dialog_selector);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public boolean isShowing() {
        if (dialog != null) {
            if (dialog.isShowing())
                return true;
            else
                return false;
        }
        return false;
    }

    public void dismiss() {
        if (dialog!=null){
            dialog.dismiss();
        }

    }
}
