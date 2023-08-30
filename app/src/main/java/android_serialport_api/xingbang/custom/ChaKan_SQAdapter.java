package android_serialport_api.xingbang.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.Temporary_database;


/**
 * 雷管信息页面 适配器
 */
public class ChaKan_SQAdapter<T> extends RecyclerView.Adapter<ChaKan_SQAdapter.ViewHolder> {

    private List<T> mListData = new ArrayList<>();
    private Context mContext;
    private int mIndex;

    private int mLine;

    /**
     * 构造方法
     *
     * @param context
     * @param line    3: 序号 管壳号 状态
     *                4: 序号 孔号 延时 管壳号
     *                5: 序号 孔号 延时 管壳号 状态
     */
    public ChaKan_SQAdapter(Context context, int line) {
        mContext = context;
        mLine = line;
    }

    /**
     * 加载数据
     *
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
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shouquan, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            List<DetonatorTypeNew> list_detonator = (List<DetonatorTypeNew>) mListData;

//            holder.mTvBlastSerial.setText((list_detonator.size() - position) + "");                 // 序号
            holder.mTvShellBlastNo.setText(list_detonator.get(position).getDetonatorId());   // 管壳号

            // 长按
            if (onitemLongClick != null) {
                holder.itemView.setOnLongClickListener(v -> {
                    onitemLongClick.itemLongClick(position);
                    return true;
                });
            }

    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTvBlastSerial;
        TextView mTvSitHole;
        TextView mTvDelay;
        TextView mTvShellBlastNo;
        TextView mTvStatus;

        ViewHolder(View view) {
            super(view);
            mTvSitHole = view.findViewById(R.id.tv_sithole);
            mTvDelay = view.findViewById(R.id.tv_delay);
            mTvShellBlastNo = view.findViewById(R.id.tv_lg_uid);
            mTvStatus = view.findViewById(R.id.tv_status);
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

    //点击变色功能
    public interface GetListener {
        void onClick(int position);
    }

    private GetListener getListener;

    public void setGetListener(GetListener getListener) {
        this.getListener = getListener;
    }
    private  int mPosition;

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

}