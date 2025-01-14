package android_serialport_api.xingbang.custom;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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
        void itemViewClick(View v,int position,boolean isChecked);
    }

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener) {
        this.mListener = listener;
    }

    private boolean isShowCheck = false;
    public void showCheckBox(boolean isShow){
        isShowCheck = isShow;
        notifyDataSetChanged();
    }

    private boolean isAllCheck = false;
    public void AllCheckBox(boolean isAllChecked){
        isAllCheck = isAllChecked;
        notifyDataSetChanged();
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
            viewHolder.tv_gsxz = convertView.findViewById(R.id.tv_gsxz);
            viewHolder.ll_item_dwxx = convertView.findViewById(R.id.ll_item_dwxx);
            viewHolder.ll_item_xmxx = convertView.findViewById(R.id.ll_item_xmxx);
            viewHolder.cbIsSelected = convertView.findViewById(R.id.cbIsSelected);
            viewHolder.iv_used = convertView.findViewById(R.id.iv_used);
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
        viewHolder.iv_used.setVisibility(("true".equals(list.get(position).get("selected").toString())
                ? View.VISIBLE : View.GONE));
        viewHolder.cbIsSelected.setVisibility(isShowCheck ? View.VISIBLE : View.GONE);
        viewHolder.cbIsSelected.setChecked(isAllCheck);
        if (list.get(position).get("business") == null) {
            viewHolder.tv_gsxz.setVisibility(View.GONE);
        } else {
            viewHolder.tv_gsxz.setText(list.get(position).get("business").toString());
            if (list.get(position).get("business").toString().startsWith("营业性")) {
                viewHolder.ll_item_xmxx.setVisibility(View.VISIBLE);
                viewHolder.ll_item_dwxx.setVisibility(View.GONE);
            } else {
                viewHolder.ll_item_xmxx.setVisibility(View.GONE);
                viewHolder.ll_item_dwxx.setVisibility(View.VISIBLE);
            }
        }
        viewHolder.btn_del.setTag(position);
        viewHolder.ly_sq.setTag(position);
//        viewHolder.btn_del.setOnClickListener(this);
//        viewHolder.ly_sq.setOnClickListener(this);
//        viewHolder.cbIsSelected.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean isChecked = viewHolder.cbIsSelected.isChecked();
//                if (mListener != null) {
//                    //确保position值有效
//                    mListener.itemViewClick(viewHolder.cbIsSelected, position, isChecked);
//                }
//            }
//        });
        viewHolder.cbIsSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mListener != null) {
                    //确保position值有效
                    mListener.itemViewClick(buttonView, position,isChecked);
                }
            }
        });
        return convertView;
    }

    public class ViewHolder {
        private TextView tv_sp_name;
        private TextView tv_sp_htbh;
        private TextView tv_sp_xmbh;
        private TextView tv_sp_dwdm;
        private TextView tv_sp_coordxy;
        private TextView tv_sp_sfz;
        private TextView tv_gsxz;
        private Button btn_del;
        private LinearLayout ly_sq;
        private LinearLayout ll_item_xmxx;
        private LinearLayout ll_item_dwxx;
        private CheckBox cbIsSelected;
        private ImageView iv_used;
    }
}
