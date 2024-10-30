package android_serialport_api.mx.xingbang.custom;

import android.content.Context;
import android.graphics.Color;
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

import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.models.DanLingBean;

/**
 * 验证起爆范围
 */
public class VerificationAdapter extends BaseAdapter implements OnClickListener {
    private List<Map<String, Object>> list;
    private Context mContext;
    private int itemListId;
    private InnerItemOnclickListener mListener;

    public VerificationAdapter(Context context, List<Map<String, Object>> list, int itemListId) {
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
            viewHolder.tv_sq_bpqy = (TextView) convertView.findViewById(R.id.tv_sq_bpqy);
            viewHolder.tv_sq_time = (TextView) convertView.findViewById(R.id.tv_sq_time);
            viewHolder.tv_sq_start_time = (TextView) convertView.findViewById(R.id.tv_sq_start_time);
            viewHolder.tv_sq_end_time = (TextView) convertView.findViewById(R.id.tv_sq_end_time);
            viewHolder.tv_sq_jd = (TextView) convertView.findViewById(R.id.tv_sq_jd);
            viewHolder.tv_sq_sum = (TextView) convertView.findViewById(R.id.tv_sq_sum);
            viewHolder.tv_sq_qbzt = (TextView) convertView.findViewById(R.id.tv_sq_qbzt);
            viewHolder.tv_sq_wd = (TextView) convertView.findViewById(R.id.tv_sq_wd);
            viewHolder.btn_del_sq = (Button) convertView.findViewById(R.id.btn_del_sq);
            viewHolder.ll_btn = (LinearLayout) convertView.findViewById(R.id.ll_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String coordxy = list.get(position).get("coordxy").toString();
        String xy[] = coordxy.split(",");//经纬度
        viewHolder.tv_sq_jd.setText(xy[0]);//经度
        viewHolder.tv_sq_wd.setText(xy[1]);//纬度
        viewHolder.tv_sq_bpqy.setText(list.get(position).get("spare1").toString());//爆破区域名称

        viewHolder.tv_sq_time.setText("" + ((DanLingBean) list.get(position).get("danLingBean")).getSqrq());//申请时间
        if ((((DanLingBean) list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbqssj() + "").equals("null")) {
            viewHolder.tv_sq_start_time.setText("无");
        } else {
            viewHolder.tv_sq_start_time.setText(" " + ((DanLingBean) list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbqssj());//开始时间
        }
        if ((((DanLingBean) list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbjzsj() + "").equals("null")) {
            viewHolder.tv_sq_end_time.setText("无");
        } else {
            viewHolder.tv_sq_end_time.setText(" " + ((DanLingBean) list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbjzsj());//结束时间
        }
        if (list.get(position).get("qbzt").equals("已起爆")) {
            viewHolder.tv_sq_qbzt.setTextColor(Color.RED);
        } else {
            viewHolder.tv_sq_qbzt.setTextColor(Color.WHITE);
        }
        viewHolder.tv_sq_sum.setText(( list.get(position).get("total") + "发"));//总数
        viewHolder.tv_sq_qbzt.setText((list.get(position).get("qbzt")) + "");//起爆状态
        viewHolder.btn_del_sq.setTag(position);
        viewHolder.btn_del_sq.setOnClickListener(this);
        viewHolder.ll_btn.setVisibility(View.GONE);
        return convertView;
    }

    public class ViewHolder {
        private TextView tv_sq_bpqy;
        private TextView tv_sq_time;
        private TextView tv_sq_start_time;
        private TextView tv_sq_end_time;
        private TextView tv_sq_jd;
        private TextView tv_sq_wd;
        private TextView tv_sq_qbzt;//起爆状态
        private TextView tv_sq_sum;//雷管总数
        private Button btn_del_sq;
        private LinearLayout ll_btn;
    }

}
