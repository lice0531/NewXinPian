package android_serialport_api.xingbang.utils;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import android_serialport_api.xingbang.R;

public class CustomDialog extends Dialog {

    private Context context;
    private String title;
    private String message;
    private String positiveButtonText;
    private String negativeButtonText;
    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnClickListener negativeListener;

    public CustomDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog2);

        TextView titleTextView = findViewById(R.id.dialog_title);
        TextView messageTextView = findViewById(R.id.dialog_message);
        Button positiveButton = findViewById(R.id.dialog_button_negative);
        Button negativeButton = findViewById(R.id.dialog_button_positive);

        titleTextView.setText(title);
        messageTextView.setText(message);
        positiveButton.setText(positiveButtonText);
        negativeButton.setText(negativeButtonText);

        positiveButton.setOnClickListener(v -> {
            if (positiveListener != null) {
                positiveListener.onClick(CustomDialog.this, DialogInterface.BUTTON_POSITIVE);
            }
            dismiss();
        });

        negativeButton.setOnClickListener(v -> {
            if (negativeListener != null) {
                negativeListener.onClick(CustomDialog.this, DialogInterface.BUTTON_NEGATIVE);
            }
            dismiss();
        });
    }

    // Setter methods for the dialog
    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPositiveButton(String text, DialogInterface.OnClickListener listener) {
        this.positiveButtonText = text;
        this.positiveListener = listener;
    }

    public void setNegativeButton(String text, DialogInterface.OnClickListener listener) {
        this.negativeButtonText = text;
        this.negativeListener = listener;
    }
}