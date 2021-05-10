package android_serialport_api.xingbang.utils;

import android.content.Context;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import android_serialport_api.xingbang.R;

public class LoadingUtils {
    /**
     * 进度条
     */
    public static DialogPlus loadDialog(Context context) {

        return DialogPlus
                .newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.liyi_view_loading))
                .setContentBackgroundResource(R.color.colorTransparent)
                .setOverlayBackgroundResource(R.color.colorTransparent)
                .setInAnimation(R.anim.loading_mask_in)
                .setOutAnimation(R.anim.loading_mask_out)
                .create();

    }


}
