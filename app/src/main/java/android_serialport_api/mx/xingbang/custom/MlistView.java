package android_serialport_api.mx.xingbang.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
/**
 * 为了配合scrollview的自定义listview
 * */
public class MlistView extends ListView {
	 
	public MlistView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
 
	public MlistView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
 
	public MlistView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
 
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int hms = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, hms);
	}
 
}
