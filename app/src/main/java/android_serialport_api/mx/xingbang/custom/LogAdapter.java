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

import java.util.List;

import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.db.SysLog;

/**
 * 验证起爆范围
 */
public class LogAdapter extends BaseAdapter implements OnClickListener {
    private List<SysLog> list;
    private Context mContext;
    private int itemListId;
    private InnerItemOnclickListener mListener;

    public LogAdapter(Context context, List<SysLog> list, int itemListId) {
        this.list = list;
        this.mContext = context;
        this.itemListId = itemListId;
    }

    @Override
    public void onClick(View v) {
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
            viewHolder.tv_syslog_name = (TextView) convertView.findViewById(R.id.item_syslog_name);
            viewHolder.tv_syslog_stata = (TextView) convertView.findViewById(R.id.item_syslog_stata);
            viewHolder.bt_upload = (Button) convertView.findViewById(R.id.lf_bt_upload);
            viewHolder.bt_delete = (Button) convertView.findViewById(R.id.lf_bt_delete);
            viewHolder.ll_log = (LinearLayout) convertView.findViewById(R.id.ll_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_syslog_name.setText(list.get(position).getFilename());//总数
        viewHolder.tv_syslog_stata.setText(list.get(position).getUpdataState());//起爆状态
        viewHolder.bt_delete.setTag(R.id.lf_bt_delete, list.get(position).getFilename());
        viewHolder.bt_upload.setTag(R.id.bt_upload,position);
        viewHolder.bt_upload.setOnClickListener(this);
        viewHolder.bt_delete.setOnClickListener(this);
        return convertView;
    }

    public class ViewHolder {
        private TextView tv_syslog_name;//起爆状态
        private TextView tv_syslog_stata;//雷管总数
        private Button bt_upload;
        private Button bt_delete;
        private LinearLayout ll_log;
    }

}
