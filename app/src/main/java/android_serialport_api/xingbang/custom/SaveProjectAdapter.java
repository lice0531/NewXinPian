package android_serialport_api.xingbang.custom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.R;

public class SaveProjectAdapter extends BaseAdapter implements OnClickListener {
    private List<Map<String, Object>> list;
    private Context mContext;
    private int itemListId;
    private InnerItemOnclickListener mListener;

    public SaveProjectAdapter(Context context, List<Map<String, Object>> list, int itemListId) {
        this.list = list;
        this.mContext = context;
        this.itemListId = itemListId;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        mListener.itemClick(v);
    }


    public interface InnerItemOnclickListener {
        void itemClick(View v);
    }

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener) {
        this.mListener = listener;
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
            viewHolder.tv_sp_name = (TextView) convertView.findViewById(R.id.tv_sp_name);
            viewHolder.tv_sp_htbh = (TextView) convertView.findViewById(R.id.tv_sp_htbh);
            viewHolder.tv_sp_xmbh = (TextView) convertView.findViewById(R.id.tv_sp_xmbh);
            viewHolder.tv_sp_dwdm = (TextView) convertView.findViewById(R.id.tv_sp_dwdm);
            viewHolder.tv_sp_coordxy = (TextView) convertView.findViewById(R.id.tv_sp_coordxy);
            viewHolder.tv_sp_sfz = (TextView) convertView.findViewById(R.id.tv_sp_sfz);
            viewHolder.btn_del = (Button) convertView.findViewById(R.id.btn_del_name);
            viewHolder.ly_sq = (LinearLayout) convertView.findViewById(R.id.ly_sq);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_sp_name.setText(list.get(position).get("project_name").toString());
        viewHolder.tv_sp_htbh.setText(list.get(position).get("htbh").toString());
        viewHolder.tv_sp_xmbh.setText(list.get(position).get("xmbh").toString());
        viewHolder.tv_sp_dwdm.setText(list.get(position).get("dwdm").toString());
        viewHolder.tv_sp_sfz.setText(list.get(position).get("bprysfz").toString());
        viewHolder.tv_sp_coordxy.setText(list.get(position).get("coordxy").toString());
        viewHolder.btn_del.setTag(position);
        viewHolder.ly_sq.setTag(position);
        viewHolder.btn_del.setOnClickListener(this);
        viewHolder.ly_sq.setOnClickListener(this);
        return convertView;
    }

    public class ViewHolder {
        private TextView tv_sp_name;
        private TextView tv_sp_htbh;
        private TextView tv_sp_xmbh;
        private TextView tv_sp_dwdm;
        private TextView tv_sp_coordxy;
        private TextView tv_sp_sfz;
        private Button btn_del;
        private LinearLayout ly_sq;
    }

}
