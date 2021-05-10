package android_serialport_api.xingbang.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {
	private LayoutInflater inflater;
	private Context context;
	private int layout;
	static final class ViewHolder{
	    TextView blastserial;
	    TextView sithole;
	    TextView setdelaytxt;
	    TextView shellBlastNo;
	}

	@SuppressLint("NewApi")
	public CustomSimpleCursorAdapter(Context context, int layout, Cursor c,
									 String[] from, int[] to, int flags) {
		
		super(context, layout, c, from, to, flags);
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.layout = layout;
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void changeCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		super.changeCursor(cursor);
	}


	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
		
	}


	/**
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		//super.bindView(view, context, cursor);
		ViewHolder holder;
		View convertView = null;
        if (view == null) {
            //convertView = miInflater.inflate(R.layout.histortyorder, null);
            holder = new ViewHolder();
            convertView = inflater.inflate(layout, null);
            holder.blastserial = (TextView) convertView.findViewById(R.id.blastserial);
            holder.sithole = (TextView) convertView.findViewById(R.id.sithole);
            holder.setdelaytxt = (TextView) convertView.findViewById(R.id.setdelaytxt);
            holder.shellBlastNo = (TextView) convertView.findViewById(R.id.shellBlastNo);

          //  holder.blastserial.setText(position + "");
            holder.sithole.setText("World");
            
            convertView.setTag(holder);
            
        } else {
            convertView = view;
        }
        
	}

	 @Override
	    public int getCount() {
		return 10;
	    }
**/

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	/**
		ViewHolder holder;
		
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(layout, null);
            holder.blastserial = (TextView) convertView.findViewById(R.id.blastserial);
            holder.sithole = (TextView) convertView.findViewById(R.id.sithole);
            holder.setdelaytxt = (TextView) convertView.findViewById(R.id.setdelaytxt);
            holder.shellBlastNo = (TextView) convertView.findViewById(R.id.shellBlastNo);

            holder.blastserial.setText(position + "");
            holder.sithole.setText("World");
            
            convertView.setTag(holder);
        } else {
            //直接通过holder获取下面三个子控件，不必使用findviewbyid，加快了 UI 的响应速度
            holder = (ViewHolder) convertView.getTag();
           Object o = this.getItem(position);
        }
        return  convertView;
        ***/
		
		return super.getView(position, convertView, parent);
	}

}
