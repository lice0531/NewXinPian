package android_serialport_api.xingbang.custom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.VoBlastModel;

public class LoadAdapter_all extends BaseAdapter {
    private List<VoBlastModel> list;
    private Context mContext;
    private int itemListId;
    public LoadAdapter_all(Context context, List<VoBlastModel> list, int itemListId, int type){
       this.list = list;
        this.mContext  = context;
        this.itemListId = itemListId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder  = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(itemListId,null);
            viewHolder.shellBlastNo = (TextView) convertView.findViewById(R.id.shellBlastNo);
            viewHolder.txtstatus = (TextView) convertView.findViewById(R.id.txtstatus);
            viewHolder.tv_regdate = (TextView) convertView.findViewById(R.id.queryall_tv_regdate);
            viewHolder.tv_errName = (TextView) convertView.findViewById(R.id.tv_errName);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        viewHolder.blastserial.setText(""+list.get(position).getBlastserial());//(position+1)//序号
        viewHolder.shellBlastNo.setText(list.get(position).getShellBlastNo());//管壳码
        viewHolder.txtstatus.setText(list.get(position).getStatusName());//状态
        viewHolder.tv_regdate.setText(list.get(position).getRegdate());//注册时间
        viewHolder.tv_errName.setText(list.get(position).getErrorName());//错误状态
        return convertView;
    }

    public class ViewHolder{
        private TextView shellBlastNo;
        private TextView txtstatus;
        private TextView tv_regdate;
        private TextView tv_errName;
    }

}
