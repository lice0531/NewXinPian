package android_serialport_api.xingbang.custom;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.models.DeviceBean;

/**
 * Created by suwen on 2018/4/9.
 */

public class DeviceAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<DeviceBean> list = new ArrayList<>();
    private Context context;
    private boolean isShowCPeak;//是否显示电流view

    public DeviceAdapter(Context context, List<DeviceBean> list, boolean isShow) {
        this.context = context;
        this.list = list;
        this.isShowCPeak = isShow;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView tv_name, tv_code, tv_state,tv_tureNum,tv_errNum,tv_currentPeak,tv_status;
        LinearLayout ll_currentPeak;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_device, null);

            holder.tv_name =  convertView.findViewById(R.id.tv_name);
            holder.tv_code =  convertView.findViewById(R.id.tv_code);
            holder.tv_state = convertView.findViewById(R.id.tv_state);
            holder.tv_tureNum =  convertView.findViewById(R.id.tv_tureNum);
            holder.tv_errNum =  convertView.findViewById(R.id.tv_errNum);
            holder.tv_currentPeak = convertView.findViewById(R.id.tv_currentPeak);
            holder.tv_status = convertView.findViewById(R.id.tv_status);
            holder.ll_currentPeak = convertView.findViewById(R.id.ll_currentPeak);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (isShowCPeak) {
            holder.ll_currentPeak.setVisibility(View.VISIBLE);
            holder.tv_status.setVisibility(View.GONE);
        } else {
            holder.ll_currentPeak.setVisibility(View.GONE);
            holder.tv_status.setVisibility(View.VISIBLE);
        }
        holder.tv_name.setText((position+1) + "");
        holder.tv_code.setText(list.get(position).getCode());
        holder.tv_state.setText(list.get(position).getInfo());
        holder.tv_status.setText(list.get(position).getInfo());
        holder.tv_currentPeak.setText(list.get(position).getCurrentPeak());
        if (list.get(position).getTrueNum() != null){
            holder.tv_tureNum.setText(list.get(position).getTrueNum());
//            holder.tv_tureNum.setTextColor(Color.GREEN);
            holder.tv_errNum.setTextColor(Color.RED);
            holder.tv_errNum.setText(list.get(position).getErrNum());
        }

//        if (list.get(position).getInfo() != null && !list.get(position).getInfo().equals("")) {
//            String f = "";
//            String a[] = list.get(position).getInfo().split(",");
//            for (int i = 0; i < a.length; i++) {
//                if (i == 0) {
//                    f = a[i];
//                } else {
//                    f = f + "\n" + a[i];
//                }
//            }
//            holder.tv_info.setText(f);
//        }
        return convertView;
    }
}
