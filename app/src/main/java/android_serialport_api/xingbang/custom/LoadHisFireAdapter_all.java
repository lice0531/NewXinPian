package android_serialport_api.xingbang.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.VoFireHisMain;

public class LoadHisFireAdapter_all extends BaseAdapter implements OnClickListener {
    private List<VoFireHisMain> list;
    private Context mContext;
	private InnerItemOnclickListener mListener;
    private int itemListId;
    public LoadHisFireAdapter_all(Context context, List<VoFireHisMain> list, int itemListId, int type){
       this.list = list;
        this.mContext  = context;
        this.itemListId = itemListId;
    }
   public interface InnerItemOnclickListener {
		void itemClick(View v);
	}
    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener){
		this.mListener=listener;
	}
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mListener.itemClick(v);

	}

	@Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
    	return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder  = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(itemListId,null);
            viewHolder.serialNo = (TextView) convertView.findViewById(R.id.serialNo);
            viewHolder.fireDate = (TextView)convertView.findViewById(R.id.fireDate);
            viewHolder.txtstatus = (TextView) convertView.findViewById(R.id.txtstatus);
            viewHolder.bt_operat = (Button) convertView.findViewById(R.id.bt_operat);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bt_operat.setText(mContext.getString(R.string.text_tip_delete));
        viewHolder.bt_operat.setOnClickListener(this);
        viewHolder.bt_operat.setTag(R.id.bt_operat,list.get(position).getBlastdate());
        viewHolder.serialNo.setText(""+position);
        viewHolder.fireDate.setText(""+list.get(position).getBlastdate());
        viewHolder.txtstatus.setText(list.get(position).getRemark());


        return convertView;
    }

    public class ViewHolder{
        private TextView serialNo;
        private TextView fireDate;
        private TextView txtstatus;
        private Button bt_operat;
    }

}
