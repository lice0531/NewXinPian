/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api.mx.xingbang;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class BasePreferences extends ListPreference {

	private static final String LOGTAG="BasePreferences";	
	private CharSequence[] entries;	
	private CharSequence[] entryValues;	
	private int selectedId;	
	private Context cxt;	
	private int indexOfValue;
	
	public BasePreferences(Context context, AttributeSet attrs) {
	
		super(context, attrs);		
		cxt=context;
	
	}

 

//重写这个方法，用于同步Summary

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

		super.onSetInitialValue(restoreValue, defaultValue);	
		entries = getEntries();	
		entryValues = getEntryValues();
		String value= getValue();//这个可以删除，只是用于debug
	
		indexOfValue=this.findIndexOfValue(getSharedPreferences().getString(this.getKey(), ""));
	
	       if(indexOfValue>=0){
	    	   String key=String.valueOf(entries[indexOfValue]);
		       if(null!=key){		
		    	   setSummary(key);
		       }	
	      }
	}
    //重写这个方法，添加一个OK按钮

	@Override	
	protected void onPrepareDialogBuilder(Builder builder) {

		// super.onPrepareDialogBuilder(builder);//不能调用父类的这个方法，否则点击列表项会关闭对话框
		builder.setSingleChoiceItems(entries, indexOfValue, new DialogInterface.OnClickListener() {		
			public void onClick(DialogInterface dialog, int which) {		
				selectedId=which;		
			}
		});
	// builder.setPositiveButton(null, null);//ListPreference源码中设置是这样写的，这里我们需要重写
		builder.setPositiveButton(getPositiveButtonText()==null?"OK":getPositiveButtonText(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
	
				if(selectedId>=0){			
					setSummary(entries[selectedId]);
					paramDialogInterface.dismiss();
					BasePreferences.this.persistString(entryValues[selectedId].toString());
					BasePreferences.this.callChangeListener(entryValues[selectedId]);
				}
			}
		});
	}
}