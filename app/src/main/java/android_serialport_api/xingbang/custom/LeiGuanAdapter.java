package android_serialport_api.xingbang.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.R;

public class LeiGuanAdapter extends BaseAdapter {
    private DanLingBean list;
    private Context mContext;
    private int itemListId;
    public LeiGuanAdapter(Context context, DanLingBean list, int itemListId){
       this.list = list;
        this.mContext  = context;
        this.itemListId = itemListId;
    }
    @Override
    public int getCount() {
        return list.getLgs().getLg().size();
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
            viewHolder.tv_lg_uid = (TextView) convertView.findViewById(R.id.tv_lg_uid);
            viewHolder.tv_lg_yxq = (TextView)convertView.findViewById(R.id.tv_lg_yxq);
            viewHolder.tv_lg_gzm = (TextView) convertView.findViewById(R.id.tv_lg_gzm);
            viewHolder.tv_lg_gzmcwxx = (TextView)convertView.findViewById(R.id.tv_lg_gzmcwxx);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_lg_uid.setText(list.getLgs().getLg().get(position).getUid());//雷管uid
        viewHolder.tv_lg_yxq.setText(list.getLgs().getLg().get(position).getYxq());//有效期
        viewHolder.tv_lg_gzm.setText(list.getLgs().getLg().get(position).getGzm());//工作码

        if (list.getLgs().getLg().get(position).getGzmcwxx().equals("0")){
            viewHolder.tv_lg_gzmcwxx.setText("雷管正常");
            viewHolder.tv_lg_gzmcwxx.setTextColor(Color.GREEN);
        }else if(list.getLgs().getLg().get(position).getGzmcwxx().equals("1")){
            viewHolder.tv_lg_gzmcwxx.setText("雷管在黑名单中");
            viewHolder.tv_lg_gzmcwxx.setTextColor(Color.RED);
        }else if(list.getLgs().getLg().get(position).getGzmcwxx().equals("2")){
            viewHolder.tv_lg_gzmcwxx.setText("雷管已使用");
            viewHolder.tv_lg_gzmcwxx.setTextColor(Color.RED);
        }else if(list.getLgs().getLg().get(position).getGzmcwxx().equals("3")){
            viewHolder.tv_lg_gzmcwxx.setText("申请的雷管UID不存在");
            viewHolder.tv_lg_gzmcwxx.setTextColor(Color.RED);
        }

        return convertView;
    }

    public class ViewHolder{
        private TextView tv_lg_uid;
        private TextView tv_lg_yxq;
        private TextView tv_lg_gzm;
        private TextView tv_lg_gzmcwxx;
    }


}
