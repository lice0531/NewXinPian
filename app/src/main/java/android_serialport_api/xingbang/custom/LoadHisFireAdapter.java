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
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.models.VoFireHisMain;

public class LoadHisFireAdapter extends BaseAdapter implements OnClickListener {
    private List<VoFireHisMain> list_his;
    private Context mContext;
	private InnerItemOnclickListener mListener;
    private int itemListId;
    public  LoadHisFireAdapter(Context context, List<VoFireHisMain> list, int itemListId, int type){
       this.list_his = list;
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
        return list_his.size();
    }

    @Override
    public Object getItem(int position) {
    	return list_his.get(position);
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
            viewHolder.bt_upload = (Button)convertView.findViewById(R.id.bt_upload);
            viewHolder.bt_operat = (Button) convertView.findViewById(R.id.bt_operat);
            viewHolder.ly_his = (LinearLayout) convertView.findViewById(R.id.ly_his);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        if(position==0){
//            viewHolder.bt_upload.setVisibility(View.INVISIBLE);
//            viewHolder.bt_operat.setVisibility(View.INVISIBLE);
//        }
        viewHolder.serialNo.setText(""+position);
        viewHolder.fireDate.setText(""+ list_his.get(position).getBlastdate());
        viewHolder.bt_operat.setText(mContext.getString(R.string.text_tip_delete));
        viewHolder.bt_upload.setOnClickListener(this);
        viewHolder.bt_operat.setOnClickListener(this);
        //viewHolder.bt_operat.setTag(R.id.bt_operat,list_his.get(position).getId());
        viewHolder.bt_operat.setTag(R.id.bt_operat, list_his.get(position).getBlastdate());
        viewHolder.bt_upload.setTag(R.id.bt_upload,position);
//        if(position==0){
//            viewHolder.bt_upload.setVisibility(View.INVISIBLE);
//        }else {
//            viewHolder.bt_upload.setVisibility(View.VISIBLE);
//        }
        if("未上传".equals(list_his.get(position).getUploadStatus())){
        	viewHolder.txtstatus.setText(mContext.getString(R.string.text_query_up));	//"未上传"
        	viewHolder.bt_upload.setText(mContext.getString(R.string.text_query_uploda));//"上传"
            viewHolder.ly_his.setBackgroundResource(R.drawable.textview_border_green);
            viewHolder.serialNo.setBackgroundResource(R.drawable.textview_border_green);
            viewHolder.fireDate.setBackgroundResource(R.drawable.textview_border_green);
            viewHolder.txtstatus.setBackgroundResource(R.drawable.textview_border_green);
        }else{
        	viewHolder.txtstatus.setText(mContext.getString(R.string.text_query_uploaded));//"已上传"
        	viewHolder.bt_upload.setText(mContext.getString(R.string.text_query_chong));//"重传"
            viewHolder.ly_his.setBackgroundResource(R.drawable.textview_border_red);
            viewHolder.serialNo.setBackgroundResource(R.drawable.textview_border_red);
            viewHolder.fireDate.setBackgroundResource(R.drawable.textview_border_red);
            viewHolder.txtstatus.setBackgroundResource(R.drawable.textview_border_red);
        }

        return convertView;
    }

    public class ViewHolder{
        private TextView serialNo;
        private TextView fireDate;
        private TextView txtstatus;
        private Button bt_upload;
        private Button bt_operat;
        private LinearLayout ly_his;
    }

}
