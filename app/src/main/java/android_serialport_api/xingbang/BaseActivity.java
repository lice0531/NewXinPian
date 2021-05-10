package android_serialport_api.xingbang;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import android_serialport_api.xingbang.utils.Utils;

public class  BaseActivity extends Activity {
	
	protected Application mApplication;
	private BaseActivity oContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		//禁止横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (mApplication == null) {
		    // 得到Application对象
			mApplication = (Application) getApplication();
		}
		oContext = this;// 把当前的上下文对象赋值给BaseActivity
		addActivity();// 调用添加方法
	}
	// 添加Activity方法
	public void addActivity() {
		mApplication.addActivity_(oContext);// 调用myApplication的添加Activity方法
	}
	//销毁当个Activity方法
	public void removeActivity() {
		mApplication.removeActivity_(oContext);// 调用myApplication的销毁单个Activity方法
	}
	//销毁所有Activity方法
	public void removeALLActivity() {
		mApplication.removeALLActivity_();// 调用myApplication的销毁所有Activity方法
	}
	/* 把Toast定义成一个方法  可以重复使用，使用时只需要传入需要提示的内容即可*/
	public void show_Toast(String text) {
		Utils.showToast(this,text,3000);
	}
    public void show_Toast_ui(String text) {
        Looper.prepare();
		Utils.showToast(this,text,3000);
        Looper.loop();
    }
	public void show_Toast_long(String text) {
		Utils.showToast(this,text,5000);
	}

	public static void fixInputMethodManagerLeak(Context destContext) {  
	    if (destContext == null) {  
	        return;  
	    }  
	      
	    InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);  
	    if (imm == null) {  
	        return;  
	    }  
	  
	    String [] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};  
	    Field f = null;  
	    Object obj_get = null;  
	    for (int i = 0;i < arr.length;i ++) {  
	        String param = arr[i];  
	        try{  
	            f = imm.getClass().getDeclaredField(param);  
	            if (f.isAccessible() == false) {  
	                f.setAccessible(true);  
	            } // author: sodino mail:sodino@qq.com  
	            obj_get = f.get(imm);  
	            if (obj_get != null && obj_get instanceof View) {  
	                View v_get = (View) obj_get;  
	                if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的  
	                    f.set(imm, null); // 置空，破坏掉path to gc节点  
	                } else {  
	                    // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了  
	                   /** if (QLog.isColorLevel()) {  
	                        QLog.d(ReflecterHelper.class.getSimpleName(), QLog.CLR, "fixInputMethodManagerLeak break, context is not suitable, get_context=" + v_get.getContext()+" dest_context=" + destContext);  
	                    }  **/
	                    break;  
	                }  
	            }  
	        }catch(Throwable t){  
	            t.printStackTrace();  
	        }  
	    }  
	}  
}