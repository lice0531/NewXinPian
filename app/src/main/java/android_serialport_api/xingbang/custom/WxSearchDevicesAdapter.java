package android_serialport_api.xingbang.custom;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.DeviceBean;

public class WxSearchDevicesAdapter extends BaseAdapter {
    private List<DeviceBean> list = new ArrayList<>();
    private Context mContext;
    private int itemListId;

    public WxSearchDevicesAdapter(Context context,int itemListId) {
        this.mContext = context;
        this.itemListId = itemListId;
    }

    public void setListData(List<DeviceBean> mlist) {
        this.list = mlist;
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(itemListId, null);
            viewHolder.tvSerid = convertView.findViewById(R.id.tv_serid);
            viewHolder.tvInfo = convertView.findViewById(R.id.tv_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvSerid.setText(list.get(position).getName());//无线设备序列号
        viewHolder.tvInfo.setText(list.get(position).getInfo());//无线设备详细信息
        return convertView;
    }

    public class ViewHolder {
        private TextView tvSerid,tvInfo;
    }
}
