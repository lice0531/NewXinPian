package android_serialport_api.xingbang.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DenatorBaseinfo;

public class ErrShouQuanListAdapter extends BaseAdapter {
    private List<DenatorBaseinfo> list;
    private Context mContext;
    private int itemListId;

    public ErrShouQuanListAdapter(Context context, List<DenatorBaseinfo> list, int itemListId) {
        this.list = list;
        this.mContext = context;
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(itemListId, null);
            viewHolder.X_item_no = convertView.findViewById(R.id.X_item_no);
            viewHolder.X_item_shellno = convertView.findViewById(R.id.X_item_shellno);
            viewHolder.X_item_delay = convertView.findViewById(R.id.X_item_delay);
            viewHolder.X_item_errorname = convertView.findViewById(R.id.X_item_errorname);
            viewHolder.X_item_duanNo = convertView.findViewById(R.id.X_item_duan);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.X_item_no.setText(String.valueOf(list.get(position).getBlastserial()));//序号
        viewHolder.X_item_duanNo.setText((String)list.get(position).getDuanNo());//段号
        viewHolder.X_item_shellno.setText((String) list.get(position).getShellBlastNo());//管壳码
        viewHolder.X_item_delay.setText(String.valueOf(list.get(position).getDelay()));//延时
//        viewHolder.X_item_errorname.setText((String) list.get(position).get);//错误
        return convertView;
    }

    public class ViewHolder {
        private TextView X_item_no;
        private TextView X_item_shellno;
        private TextView X_item_delay;
        private TextView X_item_errorname;
        private TextView X_item_duanNo;
    }


}
