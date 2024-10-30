package android_serialport_api.mx.xingbang.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.List;
import java.util.Map;

import android_serialport_api.mx.xingbang.R;

public class ItemProjectAdapter extends BaseAdapter implements OnClickListener {
    private List<Map<String, Object>> list;
    private Context mContext;
    private int itemListId;
    private InnerItemOnclickListener mListener;

    public ItemProjectAdapter(Context context, List<Map<String, Object>> list, int itemListId) {
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
            viewHolder.ll_htbh = convertView.findViewById(R.id.ll_htbh);
            viewHolder.ll_xmbh = convertView.findViewById(R.id.ll_xmbh);
            viewHolder.ll_dwdm = convertView.findViewById(R.id.ll_dwdm);
            viewHolder.tv_sp_name = convertView.findViewById(R.id.tv_sp_name);
            viewHolder.tv_sp_htbh = convertView.findViewById(R.id.tv_sp_htbh);
            viewHolder.tv_sp_xmbh = convertView.findViewById(R.id.tv_sp_xmbh);
            viewHolder.tv_sp_dwdm = convertView.findViewById(R.id.tv_sp_dwdm);
            viewHolder.tv_sp_yyx_1 = convertView.findViewById(R.id.tv_sp_yyx_1);
            viewHolder.tv_sp_yyx_2 = convertView.findViewById(R.id.tv_sp_yyx_2);
            viewHolder.tv_sp_yyx_3 = convertView.findViewById(R.id.tv_sp_yyx_3);
//            viewHolder.tv_sp_coordxy = (TextView) convertView.findViewById(R.id.tv_sp_coordxy);
            viewHolder.tv_sp_sfz = convertView.findViewById(R.id.tv_sp_sfz);
            viewHolder.btn_del = convertView.findViewById(R.id.btn_del_name);
            viewHolder.ly_sq = convertView.findViewById(R.id.ly_sq);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_sp_name.setText(list.get(position).get("project_name").toString());
        if(list.get(position).get("htbh").toString().length()>1){
            viewHolder.tv_sp_htbh.setText(list.get(position).get("htbh").toString());
            viewHolder.ll_xmbh.setVisibility(View.GONE);
            viewHolder.ll_dwdm.setVisibility(View.GONE);
        }else if (list.get(position).get("xmbh").toString().length()>1){
            viewHolder.tv_sp_xmbh.setText(list.get(position).get("xmbh").toString());
            viewHolder.ll_htbh.setVisibility(View.GONE);
            viewHolder.ll_dwdm.setVisibility(View.GONE);
        }else if (list.get(position).get("dwdm").toString().length()>1){
            viewHolder.tv_sp_dwdm.setText(list.get(position).get("dwdm").toString());
            viewHolder.ll_htbh.setVisibility(View.GONE);
            viewHolder.ll_xmbh.setVisibility(View.GONE);
        }



        viewHolder.tv_sp_sfz.setText(list.get(position).get("bprysfz").toString());
//        viewHolder.tv_sp_coordxy.setText(list.get(position).get("coordxy").toString());
        viewHolder.btn_del.setTag(position);
        viewHolder.ly_sq.setTag(position);
        viewHolder.btn_del.setOnClickListener(this);
        viewHolder.ly_sq.setOnClickListener(this);
        return convertView;
    }

    public class ViewHolder {
        private LinearLayout ll_htbh;
        private LinearLayout ll_xmbh;
        private LinearLayout ll_dwdm;
        private TextView tv_sp_name;
        private TextView tv_sp_htbh;
        private TextView tv_sp_xmbh;
        private TextView tv_sp_dwdm;
        private TextView tv_sp_coordxy;
        private TextView tv_sp_sfz;
        private TextView tv_sp_yyx_1;
        private TextView tv_sp_yyx_2;
        private TextView tv_sp_yyx_3;
        private Button btn_del;
        private CardView ly_sq;
    }

}
