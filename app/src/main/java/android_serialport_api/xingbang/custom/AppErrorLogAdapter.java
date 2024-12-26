package android_serialport_api.xingbang.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.ErrLog;
import android_serialport_api.xingbang.db.SysLog;

public class AppErrorLogAdapter extends RecyclerView.Adapter<AppErrorLogAdapter.ViewHolder> implements View.OnClickListener {
    private List<ErrLog> list_his;
    private Context mContext;
    private OnItemClickListener onItemClickListener;//声明自定义的监听接口

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建新的定义列表元素UI的View
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_query_his, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view,onItemClickListener);
    }
    //提供set方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (list_his!=null){
            //这里使用getTag方法获取position
            onItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    //定义接口
    public interface OnItemClickListener {
        void onButtonClicked(View view, int position);
        void onItemClick(View view,int position);
    }


    public AppErrorLogAdapter(Context mContext, List<ErrLog> list_his) {
        this.mContext = mContext;
        this.list_his = list_his;
    }
    public void setDataSource(List<ErrLog> list_his) {
        this.list_his = list_his;
        notifyDataSetChanged();
    }


    /**
     * 通过 ViewHolder 来绑定数据
     *
     * @param holder
     * @param position
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.fireDate.setTextSize(18);
        holder.txtstatus.setTextSize(18);
        holder.serialNo.setText((position+1)+"");
        holder.fireDate.setText(list_his.get(position).getUpdataTime());
        holder.bt_delete.setTag(R.id.bt_delete, list_his.get(position).getUpdataTime());
        holder.bt_upload.setTag(R.id.bt_upload,position);
        if("未上传".equals(list_his.get(position).getUpdataState())){
            holder.txtstatus.setText(mContext.getString(R.string.text_query_up));	//"未上传"
            holder.bt_upload.setText(mContext.getString(R.string.text_query_uploda));//"上传"
            holder.ly_his.setBackgroundResource(R.drawable.textview_border_green);
            holder.serialNo.setBackgroundResource(R.drawable.textview_border_green);
            holder.fireDate.setBackgroundResource(R.drawable.textview_border_green);
            holder.txtstatus.setBackgroundResource(R.drawable.textview_border_green);
        }else{
            holder.txtstatus.setText(mContext.getString(R.string.text_query_uploaded));//"已上传"
            holder.bt_upload.setText(mContext.getString(R.string.text_query_chong));//"重传"
            holder.ly_his.setBackgroundResource(R.drawable.textview_border_red);
            holder.serialNo.setBackgroundResource(R.drawable.textview_border_red);
            holder.fireDate.setBackgroundResource(R.drawable.textview_border_red);
            holder.txtstatus.setBackgroundResource(R.drawable.textview_border_red);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list_his.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView serialNo;
        private TextView fireDate;
        private TextView txtstatus;
        private Button bt_upload;
        private Button bt_delete;
        private LinearLayout ly_his;
        public ViewHolder(View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            serialNo = itemView.findViewById(R.id.serialNo);
            fireDate = itemView.findViewById(R.id.fireDate);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            bt_upload = itemView.findViewById(R.id.bt_upload);
            bt_delete = itemView.findViewById(R.id.bt_delete);
            ly_his = itemView.findViewById(R.id.ly_his);

            bt_upload.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    int position1 = getAdapterPosition();
                    //确保position值有效
                    if (position1 != RecyclerView.NO_POSITION) {
                        onItemClickListener.onButtonClicked(view, position1);
                    }
                }
            });

            bt_delete.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    int position1 = getAdapterPosition();
                    //确保position值有效
                    if (position1 != RecyclerView.NO_POSITION) {
                        onItemClickListener.onButtonClicked(view, position1);
                    }
                }
            });
        }
    }
}
