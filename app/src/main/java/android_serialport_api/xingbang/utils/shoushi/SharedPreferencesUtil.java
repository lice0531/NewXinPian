package android_serialport_api.xingbang.utils.shoushi;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android_serialport_api.xingbang.Application;

/**
 * Created by hsg on 14/10/2017.
 */

public class SharedPreferencesUtil {
    private static SharedPreferencesUtil instance;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefer;

    public SharedPreferencesUtil() {
        this.prefer = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
        this.editor = this.prefer.edit();
    }

    public static SharedPreferencesUtil getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtil();
                }
            }
        }

        return instance;
    }

    public void saveString(String name, String data) {
        this.editor.putString(name, data);
        this.editor.commit();
    }

    public String getString(String name) {
        return this.prefer.getString(name, null);
    }
}
