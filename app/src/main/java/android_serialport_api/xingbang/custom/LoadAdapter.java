package android_serialport_api.xingbang.custom;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.models.VoBlastModel;

public class LoadAdapter extends BaseAdapter {
    private List<DenatorBaseinfo> list;
    private Context mContext;
    private int itemListId;
    public  LoadAdapter(Context context, List<DenatorBaseinfo> list, int itemListId, int type){
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
            viewHolder.blastserial = (TextView) convertView.findViewById(R.id.blastserial);
            viewHolder.sithole = (TextView)convertView.findViewById(R.id.sithole);
            viewHolder.shellBlastNo = (TextView) convertView.findViewById(R.id.shellBlastNo);
            viewHolder.setdelaytxt = (TextView)convertView.findViewById(R.id.setdelaytxt);
            viewHolder.txtstatus = (TextView) convertView.findViewById(R.id.txtstatus);
            
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.blastserial.setText(""+list.get(position).getBlastserial());//(position+1)//序号
        viewHolder.sithole.setText(""+list.get(position).getSithole());//孔号
        viewHolder.shellBlastNo.setText(list.get(position).getShellBlastNo());//延时
        viewHolder.setdelaytxt.setText(""+list.get(position).getDelay());//管壳码
        viewHolder.txtstatus.setText(list.get(position).getErrorName());//错误状态
        if(list.get(position).getErrorName()!=null&&!list.get(position).getErrorName().equals("通信成功")){
            viewHolder.txtstatus.setTextColor(Color.RED);
        }else {
            viewHolder.txtstatus.setTextColor(Color.BLACK);
        }
        return convertView;
    }

    public class ViewHolder{
        private TextView blastserial;
        private TextView sithole;
        private TextView shellBlastNo;
        private TextView setdelaytxt;
        private TextView txtstatus;
    }

}
