package android_serialport_api.xingbang.custom;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.DanLingBean;

/**
 * 工作码下载页面 适配器
 */
public class ShouQuanAdapter2<T> extends RecyclerView.Adapter<ShouQuanAdapter2.ViewHolder> {

    private List<Map<String,Object>> list = new ArrayList<>();
    private Context mContext;
    private OnItemClick onitemClick;

    public ShouQuanAdapter2(Context context) {
        mContext = context;
    }

    /**
     * 加载数据
     *
     */
    public void setListData(List<Map<String,Object>> listData) {
        this.list = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_shouquan, parent, false);
        return new ViewHolder(view,onitemClick);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DanLingBean dl=(DanLingBean)list.get(position).get("danLingBean");
//        Log.e("adapter", "list.get(position): "+list.get(position).toString());
        if (((dl.getZbqys().getZbqy().get(0).getZbqssj() + "").equals("null"))) {
            holder.tv_sq_start_time.setText("无");
        } else {
            holder.tv_sq_start_time.setText(" " + dl.getZbqys().getZbqy().get(0).getZbqssj());//开始时间
        }
        if ((dl.getZbqys().getZbqy().get(0).getZbjzsj() + "").equals("null")) {
            holder.tv_sq_end_time.setText("无");
        } else {
            holder.tv_sq_end_time.setText(" " + dl.getZbqys().getZbqy().get(0).getZbjzsj());//结束时间
        }
        if(list.get(position).get("errNum").toString().length()>0&&!list.get(position).get("errNum").equals("0")){
            holder.tv_err_sum.setText(list.get(position).get("errNum")+"发");
            holder.tv_err_sum.setTextColor(Color.RED);
        }else {
            holder.tv_err_sum.setTextColor(Color.WHITE);
            holder.tv_err_sum.setText("0发");
        }
        if(list.get(position).get("qbzt").equals("已起爆")){
            holder.tv_sq_qbzt.setTextColor(Color.RED);
        }else {
            holder.tv_sq_qbzt.setTextColor(Color.WHITE);
        }
//        Log.e("adapter", "position: "+position);
        if(((DanLingBean)list.get(position).get("danLingBean")).getZbqys().getZbqy().size()>1){
            String coordxy = list.get(position).get("coordxy").toString();
            String xy[] = coordxy.split(",");//经纬度
            holder.tv_sq_jd.setText(xy[0]);//经度
            holder.tv_sq_wd.setText(xy[1]);//纬度
            holder.tv_sq_bpqy.setText(list.get(position).get("spare1").toString());//爆破区域名称
        }else {
            holder.tv_sq_bpqy.setText(((DanLingBean)list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbqymc());//爆破区域名称
            holder.tv_sq_jd.setText(((DanLingBean)list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbqyjd());//经度
            holder.tv_sq_wd.setText(((DanLingBean)list.get(position).get("danLingBean")).getZbqys().getZbqy().get(0).getZbqywd());//纬度

        }
        holder.tv_sq_time.setText(((DanLingBean)list.get(position).get("danLingBean")).getSqrq());//申请时间
        holder.tv_sq_qbzt.setText((list.get(position).get("qbzt"))+"");//起爆状态
        holder.tv_sq_sum.setText(((DanLingBean)list.get(position).get("danLingBean")).getLgs().getLg().size()+"发");//总数
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_sq_bpqy;
        TextView tv_sq_time;
        TextView tv_sq_start_time;
        TextView tv_sq_end_time;
        TextView tv_sq_jd;
        TextView tv_sq_wd;
        TextView tv_err_sum;
        TextView tv_sq_sum;
        TextView tv_sq_qbzt;
        TextView tv_chakan_sq;
        Button btn_del_sq;
        LinearLayout ly_sq;

        ViewHolder(View convertView,final OnItemClick onitemClick) {
            super(convertView);
            tv_sq_bpqy = (TextView) convertView.findViewById(R.id.tv_sq_bpqy);
            tv_sq_time = (TextView) convertView.findViewById(R.id.tv_sq_time);
            tv_sq_start_time = (TextView) convertView.findViewById(R.id.tv_sq_start_time);
            tv_sq_end_time = (TextView) convertView.findViewById(R.id.tv_sq_end_time);
            tv_sq_jd = (TextView) convertView.findViewById(R.id.tv_sq_jd);
            tv_sq_wd = (TextView) convertView.findViewById(R.id.tv_sq_wd);
            tv_err_sum = (TextView) convertView.findViewById(R.id.tv_err_sum);
            tv_sq_sum = (TextView) convertView.findViewById(R.id.tv_sq_sum);
            tv_sq_qbzt = (TextView) convertView.findViewById(R.id.tv_sq_qbzt);
            tv_chakan_sq = (TextView) convertView.findViewById(R.id.tv_chakan_sq);
            btn_del_sq = (Button) convertView.findViewById(R.id.btn_del_sq);
            ly_sq = (LinearLayout) convertView.findViewById(R.id.ly_sq);
            tv_chakan_sq.setOnClickListener(v -> {
                if (onitemClick != null) {
                    int position = getBindingAdapterPosition();
                    //确保position值有效
                    if (position != RecyclerView.NO_POSITION) {
                        onitemClick.onButtonClicked(v, position);
                    }
                }
            });
            btn_del_sq.setOnClickListener(v -> {
                if (onitemClick != null) {
                    int position = getBindingAdapterPosition();
                    //确保position值有效
                    if (position != RecyclerView.NO_POSITION) {
                        onitemClick.onButtonClicked(v, position);
                    }
                }
            });
            ly_sq.setOnClickListener(v -> {
                if (onitemClick != null) {
                    int position = getBindingAdapterPosition();
                    //确保position值有效
                    if (position != RecyclerView.NO_POSITION) {
                        onitemClick.onItemClick(v, position);
                    }
                }
            });
        }
    }

    // 接口回调，定义view点击事件
    public interface OnItemClick {
        void onButtonClicked(View view, int position);
        void onItemClick(View view,int position);
    }

    public void setOnItemClick(OnItemClick onitemClick) {
        this.onitemClick = onitemClick;
    }
}