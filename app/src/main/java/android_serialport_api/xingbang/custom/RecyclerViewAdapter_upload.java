package android_serialport_api.xingbang.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DenatorHis_Main;


/**
 * 雷管信息页面 适配器
 */
public class RecyclerViewAdapter_upload<T> extends RecyclerView.Adapter<RecyclerViewAdapter_upload.ViewHolder> {

    private List<T> mListData = new ArrayList<>();
    private Context mContext;
    private int mIndex;

    private int mLine;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onButtonClicked(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.mOnItemClickListener = clickListener;
    }
    /**
     * 构造方法
     * @param context
     * @param line    3: 序号 管壳号 状态
     */
    public RecyclerViewAdapter_upload(Context context, int line) {
        mContext = context;
        mLine = line;
    }

    /**
     * 加载数据
     * @param listData<T>
     * @param index       1: DetonatorBaseInfo 2:
     */
    public void setListData(List<T> listData, int index) {
        this.mListData = listData;
        this.mIndex = index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
         if (mLine == 7) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item_upload, parent, false);
        }
        return new ViewHolder(view,mOnItemClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       if (mIndex == 7) {
            if (mLine == 7) {//上传页面
                List<DenatorHis_Main> list_lg = (List<DenatorHis_Main>) mListData;
                DenatorHis_Main dbInfo = list_lg.get(position);
                holder.mTvtime.setText(dbInfo.getBlastdate());   // 日期
                if (dbInfo.getPro_htid().length() > 0) {
                    holder.mTvhtbh.setText("合同编号:" + dbInfo.getPro_htid() + "");//合同编号
                } else if (dbInfo.getPro_xmbh().length() > 0) {
                    holder.mTvhtbh.setText("项目编号:" + dbInfo.getPro_xmbh() + "");//项目编号
                } else if (dbInfo.getPro_dwdm().length() > 0) {
                    holder.mTvhtbh.setText("单位代码:" + dbInfo.getPro_dwdm() + "");//合同编号
                }else {
                    holder.mTvhtbh.setText("合同编号:");
                }

                holder.mTvtotal.setText(dbInfo.getTotal()+"");//数量
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvhtbh;
        TextView mTvtotal;
        TextView mTvtime;
        Button btnUplod;

        ViewHolder(View view,final OnItemClickListener onClickListener) {
            super(view);

            //上传数据页面
            mTvhtbh = view.findViewById(R.id.tv_htbh);
            mTvtotal = view.findViewById(R.id.tv_total);
            mTvtime = view.findViewById(R.id.tv_time);
            btnUplod = view.findViewById(R.id.btn_upload);
            btnUplod.setOnClickListener(v -> {
                if (onClickListener != null) {
                    int position = getBindingAdapterPosition();
                    //确保position值有效
                    if (position != RecyclerView.NO_POSITION) {
                        onClickListener.onButtonClicked(view, position);
                    }
                }
            });
        }
    }

    // 接口回调，定义长按事件
    private OnItemLongClick onitemLongClick;

    public interface OnItemLongClick {
        void itemLongClick(int position);
    }

    //条目长按接口回调
    public void setOnItemLongClick(OnItemLongClick onitemLongClick) {
        this.onitemLongClick = onitemLongClick;
    }


}