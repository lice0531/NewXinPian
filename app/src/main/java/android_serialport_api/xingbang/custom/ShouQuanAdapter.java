package android_serialport_api.xingbang.custom;

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

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.DanLingBean;

public class ShouQuanAdapter extends BaseAdapter implements OnClickListener{
    private List<Map<String,Object>> list;
    private Context mContext;
    private int itemListId;
    private InnerItemOnclickListener mListener;

    public ShouQuanAdapter(Context context, List<Map<String,Object>> list, int itemListId) {
        this.list = list;
        this.mContext = context;
        this.itemListId = itemListId;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        mListener.itemClick(v);
    }


    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public interface InnerItemOnclickListener {
        void itemClick(View v);
    }

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener) {
        this.mListener=listener;
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
            viewHolder.tv_sq_wd = (TextView) convertView.findViewById(R.id.tv_sq_wd);
            viewHolder.tv_err_sum = (TextView) convertView.findViewById(R.id.tv_err_sum);
            viewHolder.tv_sq_sum = (TextView) convertView.findViewById(R.id.tv_sq_sum);
            viewHolder.tv_sq_qbzt = (TextView) convertView.findViewById(R.id.tv_sq_qbzt);
            viewHolder.tv_chakan_sq = (TextView) convertView.findViewById(R.id.tv_chakan_sq);
            viewHolder.btn_del_sq = (Button) convertView.findViewById(R.id.btn_del_sq);
            viewHolder.ly_sq = (LinearLayout) convertView.findViewById(R.id.ly_sq);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DanLingBean dl=(DanLingBean)list.get(position).get("danLingBean");
//        Log.e("adapter", "list.get(position): "+list.get(position).toString());
        if (((dl.getZbqys().getZbqy().get(0).getZbqssj() + "").equals("null"))) {
            viewHolder.tv_sq_start_time.setText("无");
        } else {
            viewHolder.tv_sq_start_time.setText(" " + dl.getZbqys().getZbqy().get(0).getZbqssj());//开始时间
        }
        if ((dl.getZbqys().getZbqy().get(0).getZbjzsj() + "").equals("null")) {
            viewHolder.tv_sq_end_time.setText("无");
        } else {
            viewHolder.tv_sq_end_time.setText(" " + dl.getZbqys().getZbqy().get(0).getZbjzsj());//结束时间
        }
        if(list.get(position).get("errNum").toString().length()>0&&!list.get(position).get("errNum").equals("0")){
            viewHolder.tv_err_sum.setText(list.get(position).get("errNum")+"发");
            viewHolder.tv_err_sum.setTextColor(Color.RED);
        }else {
            viewHolder.tv_err_sum.setTextColor(Color.WHITE);
            viewHolder.tv_err_sum.setText("0发");
        }

        if(list.get(position).get("qbzt").equals("已起爆")){
            viewHolder.tv_sq_qbzt.setTextColor(Color.RED);
        }else {
            viewHolder.tv_sq_qbzt.setTextColor(Color.WHITE);
        }
//        Log.e("adapter", "position: "+position);

        if(((DanLingBean)list.get(position).get("danLingBean")).getZbqys().getZbqy().size()>1){
            String coordxy = list.get(position).get("coordxy").toString();
            String xy[] = coordxy.split(",");//经纬度
            viewHolder.tv_sq_jd.setText(xy[0]);//经度
            viewHolder.tv_sq_wd.setText(xy[1]);//纬度
            viewHolder.tv_sq_bpqy.setText(list.get(position).get("spare1").toString());//爆破区域名称
        }else {
            viewHolder.tv_sq_bpqy.setText(((DanLingBean)list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbqymc());//爆破区域名称
            viewHolder.tv_sq_jd.setText(((DanLingBean)list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbqyjd());//经度
            viewHolder.tv_sq_wd.setText(((DanLingBean)list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbqywd());//纬度

        }
        viewHolder.tv_sq_time.setText(((DanLingBean)list.get(position).get("danLingBean")).getSqrq());//申请时间
        viewHolder.tv_sq_qbzt.setText((list.get(position).get("qbzt"))+"");//起爆状态
        viewHolder.tv_sq_sum.setText((list.get(position).get("total"))+"发");//总数

        viewHolder.btn_del_sq.setTag(position);
        viewHolder.ly_sq.setTag(position);
        viewHolder.tv_chakan_sq.setTag(position);
        viewHolder.btn_del_sq.setOnClickListener(this);
        viewHolder.ly_sq.setOnClickListener(this);
        viewHolder.tv_chakan_sq.setOnClickListener(this);
        return convertView;
    }

    public class ViewHolder {
        private TextView tv_sq_bpqy;
        private TextView tv_sq_time;
        private TextView tv_sq_start_time;
        private TextView tv_sq_end_time;
        private TextView tv_sq_jd;
        private TextView tv_sq_wd;
        private TextView tv_err_sum;
        private TextView tv_sq_sum;//雷管总数
        private TextView tv_sq_qbzt;//起爆状态
        private TextView tv_chakan_sq;//查看
        private Button btn_del_sq;
        private LinearLayout ly_sq;
    }

}
