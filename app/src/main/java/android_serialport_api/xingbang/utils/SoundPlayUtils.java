package android_serialport_api.xingbang.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import android_serialport_api.xingbang.R;

public class SoundPlayUtils {
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    public static SoundPlayUtils soundPlayUtils;
    // 上下文
    static Context mContext;

    /**
     * 初始化
     * 
     * @param context
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }

        // 初始化声音
        mContext = context;

        mSoundPlayer.load(mContext, R.raw.beep, 1);// 1
        mSoundPlayer.load(mContext, R.raw.success, 2);// 2成功
        mSoundPlayer.load(mContext, R.raw.fail, 3);// 3失败
//        mSoundPlayer.load(mContext, R.raw.blippy1, 4);// 4失败
        mSoundPlayer.load(mContext, R.raw.awo_err, 4);// 4失败
        return soundPlayUtils;
    }

    /**
     * 播放声音
     * 
     * @param soundID
     */
    public static int play(int soundID) {
       return mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }

}