package android_serialport_api.xingbang;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import android.DeviceControl;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.serialport.DeviceControlSpd;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.senter.pda.iam.libgpiot.Gpiot1;

import android_serialport_api.xingbang.utils.MmkvUtils;
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
	                    break;
	                }  
	            }  
	        }catch(Throwable t){  
	            t.printStackTrace();  
	        }  
	    }  
	}


	// 上电方式
	public int mPowerOnMode;                // 上电指数
	public DeviceControl mDeviceControl;    // 0: Dc上电
	public DeviceControlSpd mDeviceControlSpd ;    // 2: 团标上电
	public Gpiot1 mGpiot1;                  // 1:Gpio包上电

	/**
	 * 实例化上电方式
	 */
	public void initPower() {
		mPowerOnMode = mApplication.getPowerIndex();
		Log.e("上电", "mPowerOnMode: "+ mPowerOnMode);
		if (mPowerOnMode == 0) {
			try {
				mDeviceControl = new DeviceControl(DeviceControl.PowerType.MAIN, 94, 93);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.e("BaseActivity", "实例化 DeviceControl");
		} else if (mPowerOnMode == 1) {
			mGpiot1 = new Gpiot1();
			Log.e("BaseActivity", "实例化 Gpiot1");
		} else if (mPowerOnMode == 2) {
			try {
				mDeviceControlSpd = new DeviceControlSpd("NEW_MAIN_FG", 108);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.e("BaseActivity", "实例化 DeviceControl");
		}  else {
			Log.e("BaseActivity", "实例化 空");
		}
	}


	/**
	 * 主板上电
	 */
	public void powerOnDevice(String mode){
		try {
			Thread.sleep(100);
			Log.e("BaseActivity", "上电延时100ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// DC 主板上电
		if (mPowerOnMode == 0) {
			Log.e("BaseActivity", "DC 主板上电");
			try {
				mDeviceControl.PowerOnDevice();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (mPowerOnMode == 1) {// libgpiotp_6.11.jar 上电
			Log.e("BaseActivity", "Gpiot1 主板上电");
			mGpiot1.setEnable(mode, true);
			mGpiot1.setUartGpio(true);  // 串口上电

		} else if (mPowerOnMode == 2) {// 团标起爆器上电
			Log.e("BaseActivity", "DC 主板上电");
			try {
				mDeviceControlSpd.PowerOnDevice();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 主板下电
	 */
	public void powerOffDevice(String mode) {

		try {
			Thread.sleep(100);
			Log.e("BaseActivity", "下电延时100ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// DC 主板下电
		if (mPowerOnMode == 0) {
			Log.e("BaseActivity", "DC 主板下电");
			try {
				mDeviceControl.PowerOffDevice();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}// DC 主板下电
		if (mPowerOnMode == 2) {
			Log.e("BaseActivity", "DC 主板下电");
			try {
				mDeviceControlSpd.PowerOffDevice();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		// libgpiotp_6.11.jar 下电
		else if (mPowerOnMode == 1) {
			Log.e("BaseActivity", "Gpiot1 主板下电");
			mGpiot1.setEnable(mode, false);

			mGpiot1.setUartGpio(false);      // 串口下电
//            optGpio(PIN_ADSL);

		}

	}

	/**
	 * 扫码头上电
	 */
	public void powerOnScanDevice(String mode) {
		Log.e("BaseActivity", "mPowerOnMode: "+mPowerOnMode);
		if (mPowerOnMode == 1) {// libgpiotp_6.11.jar 上电
			Log.e("BaseActivity", "Gpiot1 扫码头上电");
			mGpiot1.setEnable(mode, true);
		}

	}

	/**
	 * 扫码头下电
	 */
	public void powerOffScanDevice(String mode) {
		if (mPowerOnMode == 0) {// KT50扫码头下电
			Log.e("BaseActivity", "KT50扫码头下电");
		} else if (mPowerOnMode == 1) {// ST327扫码头下电
			Log.e("BaseActivity", "ST327扫码头下电");
			mGpiot1.setEnable(mode, false);
		}
	}

	//gpio单独置高方法
	private void optGpio(String name) {
		boolean before = mGpiot1.isHi(name);
		mGpiot1.setEnable(name, !before);
		Log.e("BaseActivity", "optGpio: " + name);
		Toast.makeText(this, name + "由 " + (before ? "高" : "低") + "，变成 " + (!before ? "高" : "低"), Toast.LENGTH_LONG).show();
	}

	/**
	 * 初始化AutoCompleteTextView，最多显示5项提示，使
	 * AutoCompleteTextView在一开始获得焦点时自动提示
	 *
	 * @param field 保存在sharedPreference中的字段名
	 * @param auto  要操作的AutoCompleteTextView
	 */
	public void initAutoComplete(String field, AutoCompleteTextView auto) {
		String longhistory =(String) MmkvUtils.getcode(field, "当前无记录");
		String[] hisArrays = longhistory.split("#");

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_auto_textview, hisArrays);
		//只保留最近的20条的记录
		if (hisArrays.length > 20) {
			String[] newArrays = new String[20];
			System.arraycopy(hisArrays, 0, newArrays, 0, 20);
			adapter = new ArrayAdapter<>(this, R.layout.item_auto_textview, newArrays);
		}
		auto.setAdapter(adapter);
		auto.setDropDownHeight(500);
		auto.setDropDownWidth(450);
		auto.setThreshold(1);
		auto.setCompletionHint("最近的20条记录");
		auto.setOnFocusChangeListener((v, hasFocus) -> {
			AutoCompleteTextView view = (AutoCompleteTextView) v;
			if (hasFocus) {
				view.showDropDown();
			}
		});
	}

	/**
	 * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符段
	 *
	 * @param field 保存在sharedPreference中的字段名
	 * @param auto  要操作的AutoCompleteTextView
	 */
	public void saveHistory(String field, AutoCompleteTextView auto) {
		String text = auto.getText().toString();
		String longhistory = (String)MmkvUtils.getcode(field, "");
		if (!longhistory.contains(text + "#")) {
			StringBuilder sb = new StringBuilder(longhistory);
			sb.insert(0, text + "#");
			MmkvUtils.savecode(field, sb.toString());
		}
	}

}