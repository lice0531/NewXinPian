package android_serialport_api.xingbang.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.DanLingBean;

public class ErrListAdapter extends BaseAdapter {
    private ArrayList<Map<String, Object>> list;
    private Context mContext;
    private int itemListId;

    public ErrListAdapter(Context context, ArrayList<Map<String, Object>> list, int itemListId) {
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
        Log.e("错误列表adapter", "list.get(position): "+list.get(position) );
        viewHolder.X_item_no.setText((position + 1)+"");//雷管uid
        viewHolder.X_item_duanNo.setText((String)list.get(position).get("duanNo"));//段号
        viewHolder.X_item_shellno.setText((String) list.get(position).get("shellNo"));//管壳码
        viewHolder.X_item_delay.setText(list.get(position).get("delay")+"");//延时
        viewHolder.X_item_errorname.setText((String) list.get(position).get("errorName"));//错误

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
