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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.DanLingBean;

public class UploadMessageAdapter extends BaseAdapter implements OnClickListener {
    private List<Map<String, Object>> list;
    private Context mContext;
    private int itemListId;
    private InnerItemOnclickListener mListener;

    public UploadMessageAdapter(Context context, List<Map<String, Object>> list, int itemListId) {
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

    public void update(int index,ListView listview){
        //得到第一个可见item项的位置
        int visiblePosition = listview.getFirstVisiblePosition();
        //得到指定位置的视图，对listview的缓存机制不清楚的可以去了解下
        View view = listview.getChildAt(index - visiblePosition);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.tv_up_dl_state = (TextView) view.findViewById(R.id.tv_up_dl_state);//丹灵上传状态
        viewHolder.tv_up_zb_state = (TextView) view.findViewById(R.id.tv_up_zb_state);//中爆上传状态
        setData(viewHolder,index);
    }
    private void setData(ViewHolder viewHolder,int index){
        Map<String, Object>map = list.get(index);
        viewHolder.tv_up_dl_state.setText(map.get("dl_state").toString());
        viewHolder.tv_up_zb_state.setText(map.get("zb_state").toString());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(itemListId, null);
            viewHolder.tv_sq_bpqy = (TextView) convertView.findViewById(R.id.tv_sq_bpqy);
            viewHolder.tv_sq_time = (TextView) convertView.findViewById(R.id.tv_sq_time);
            viewHolder.tv_up_dl_state = (TextView) convertView.findViewById(R.id.tv_up_dl_state);//丹灵上传状态
            viewHolder.tv_up_zb_state = (TextView) convertView.findViewById(R.id.tv_up_zb_state);//中爆上传状态
            viewHolder.tv_sq_jd = (TextView) convertView.findViewById(R.id.tv_sq_jd);
            viewHolder.tv_sq_wd = (TextView) convertView.findViewById(R.id.tv_sq_wd);
            viewHolder.tv_err_sum = (TextView) convertView.findViewById(R.id.tv_err_sum);
            viewHolder.tv_sq_sum = (TextView) convertView.findViewById(R.id.tv_sq_sum);
            viewHolder.tv_sq_qbzt = (TextView) convertView.findViewById(R.id.tv_sq_qbzt);
            viewHolder.tv_chakan_sq = (TextView) convertView.findViewById(R.id.tv_chakan_sq);
            viewHolder.btn_del_sq = (Button) convertView.findViewById(R.id.btn_del_sq);
            viewHolder.btn_upload = (Button) convertView.findViewById(R.id.btn_upload);
            viewHolder.ly_sq = (LinearLayout) convertView.findViewById(R.id.ly_sq);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DanLingBean dl = (DanLingBean) list.get(position).get("danLingBean");
        viewHolder.tv_up_dl_state.setText(" " + list.get(position).get("dl_state"));//丹灵上传状态
        if(list.get(position).get("dl_state").equals("未上传")){
            viewHolder.tv_up_dl_state.setTextColor(Color.RED);
        }else {
            viewHolder.tv_up_dl_state.setTextColor(Color.GREEN);
        }
        viewHolder.tv_up_zb_state.setText(" " + list.get(position).get("zb_state"));//中爆上传状态
        if(list.get(position).get("zb_state").equals("未上传")){
            viewHolder.tv_up_zb_state.setTextColor(Color.RED);
        }else {
            viewHolder.tv_up_zb_state.setTextColor(Color.GREEN);
        }

        if (!list.get(position).get("errNum").equals("0")) {
            viewHolder.tv_err_sum.setText(list.get(position).get("qblgNum") + "发");
            viewHolder.tv_err_sum.setTextColor(Color.RED);
        } else {
            viewHolder.tv_err_sum.setTextColor(Color.WHITE);
        }
        if (list.get(position).get("qbzt").equals("已起爆")) {
            viewHolder.tv_sq_qbzt.setTextColor(Color.RED);
        } else {
            viewHolder.tv_sq_qbzt.setTextColor(Color.WHITE);
        }
        viewHolder.tv_sq_bpqy.setText(dl.getZbqys().getZbqy().get(0).getZbqymc());//爆破区域名称
        viewHolder.tv_sq_time.setText(dl.getSqrq());//申请时间
        viewHolder.tv_sq_qbzt.setText((list.get(position).get("qbzt")) + "");//起爆状态
        viewHolder.tv_sq_jd.setText(dl.getZbqys().getZbqy().get(0).getZbqyjd());//经度
        viewHolder.tv_sq_wd.setText(dl.getZbqys().getZbqy().get(0).getZbqywd());//纬度
        viewHolder.tv_sq_sum.setText(dl.getLgs().getLg().size() + "发");//总数
        viewHolder.btn_del_sq.setTag(position);
        viewHolder.btn_upload.setTag(position);
        viewHolder.ly_sq.setTag(position);
        viewHolder.tv_chakan_sq.setTag(position);
        viewHolder.btn_del_sq.setOnClickListener(this);
        viewHolder.btn_upload.setOnClickListener(this);
        viewHolder.ly_sq.setOnClickListener(this);
        viewHolder.tv_chakan_sq.setOnClickListener(this);
        return convertView;
    }

    public class ViewHolder {
        private TextView tv_sq_bpqy;
        private TextView tv_sq_time;
        private TextView tv_up_dl_state;
        private TextView tv_up_zb_state;
        private TextView tv_sq_jd;
        private TextView tv_sq_wd;
        private TextView tv_err_sum;
        private TextView tv_sq_sum;//雷管总数
        private TextView tv_sq_qbzt;//起爆状态
        private TextView tv_chakan_sq;//查看
        private Button btn_del_sq;
        private Button btn_upload;
        private LinearLayout ly_sq;
    }

}
