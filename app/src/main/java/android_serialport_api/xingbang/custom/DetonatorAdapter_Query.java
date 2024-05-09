package android_serialport_api.xingbang.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DenatorBaseinfo;


/**
 * 雷管信息页面 适配器
 */
public class DetonatorAdapter_Query<T> extends RecyclerView.Adapter<DetonatorAdapter_Query.ViewHolder> {

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
    public DetonatorAdapter_Query(Context context, int line) {
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item_query, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


            List<DenatorBaseinfo> list_detonatorBaseInfo = (List<DenatorBaseinfo>) mListData;

            DenatorBaseinfo detonatorBaseInfo = list_detonatorBaseInfo.get(position);
            holder.mTvBlastSerial.setText((list_detonatorBaseInfo.size() - position) + "");                 // 序号
            holder.mTvShellBlastNo.setText(detonatorBaseInfo.getShellBlastNo());   // 管壳号
//            if(mLine == 6){
//                holder.mTvShellBlastNo.setText(detonatorBaseInfo.getDenatorId());   // 芯片码
//                holder.mTvSitHole.setText(detonatorBaseInfo.getDuan() +"-"+detonatorBaseInfo.getDuanNo());     // 段号
//                holder.mTvDelay.setText(detonatorBaseInfo.getDelay() + "");         // 延时
//            }
//            if (mLine == 4) {
//                holder.mTvSitHole.setText(detonatorBaseInfo.getDuan() +"-"+detonatorBaseInfo.getDuanNo());      // 段号
//                holder.mTvDelay.setText(detonatorBaseInfo.getDelay() + "");         // 延时
//            }
//            if ( mLine == 5) {
//                holder.mTvSitHole.setText(detonatorBaseInfo.getDuan() +"-"+detonatorBaseInfo.getDuanNo());     // 孔号
//                holder.mTvDelay.setText(detonatorBaseInfo.getDelay() + "");         // 延时
//                if (detonatorBaseInfo.getErrorCode().equals("FF")) {
//                    holder.mTvStatus.setTextColor(Color.GREEN);
//                } else {
//                    holder.mTvStatus.setTextColor(Color.RED);
//                }
//            }
            if (mIndex == 1) {

                if(position!=0){
                    DenatorBaseinfo detonatorBaseInfo2 = list_detonatorBaseInfo.get(position-1);
                    int a = Integer.parseInt(detonatorBaseInfo.getShellBlastNo().substring(10));//5340821A00001
                    int b = Integer.parseInt(detonatorBaseInfo2.getShellBlastNo().substring(10));//5340821A00001
                    if(b-a==2){
                        holder.mLl_item.setBackgroundResource(R.drawable.a_bg_border_red_2);
                    }else {
                        holder.mLl_item.setBackgroundResource(R.drawable.a_bg_border_blue_1dp);
                    }
                }
            }
                holder.mTvStatus.setText(detonatorBaseInfo.getErrorName());
                holder.mTvDuanNo.setText(detonatorBaseInfo.getDuan() +"-"+detonatorBaseInfo.getDuanNo());
                holder.mTvPiace.setText(detonatorBaseInfo.getPiece());
                if (detonatorBaseInfo.getErrorCode().equals("FF")) {
                    holder.mTvStatus.setTextColor(Color.GREEN);
                } else {
                    holder.mTvStatus.setTextColor(Color.RED);
                }

//            if (mLine == 5) {
//                holder.mTvStatus.setText(detonatorBaseInfo.getErrorName());
//                if (detonatorBaseInfo.getErrorCode().equals("FF")) {
//                    holder.mTvStatus.setTextColor(Color.GREEN);
//                } else {
//                    holder.mTvStatus.setTextColor(Color.RED);
//                }
//            }
//            if(mLine == 7){//点击管壳码/uid 切换显示
//                holder.mTvShellBlastNo.setText(detonatorBaseInfo.getDenatorId());   // 芯片码
//                holder.mTvStatus.setText(detonatorBaseInfo.getErrorName());
//                holder.mTvDuanNo.setText(detonatorBaseInfo.getDuan() +"-"+detonatorBaseInfo.getDuanNo());
//                holder.mTvPiace.setText(detonatorBaseInfo.getPiece());
//                if (detonatorBaseInfo.getErrorCode().equals("FF")) {
//                    holder.mTvStatus.setTextColor(Color.GREEN);
//                } else {
//                    holder.mTvStatus.setTextColor(Color.RED);
//                }
//            }

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
        TextView mTvDuanNo;
        TextView mTvPiace;
        LinearLayout mLl_item;

        ViewHolder(View view) {
            super(view);
            mTvBlastSerial = view.findViewById(R.id.tv_blastserial);
            mTvSitHole = view.findViewById(R.id.tv_sithole);
            mTvDelay = view.findViewById(R.id.tv_delay);
            mTvShellBlastNo = view.findViewById(R.id.tv_shellBlastNo);
            mTvStatus = view.findViewById(R.id.tv_status);
            mTvDuanNo = view.findViewById(R.id.tv_duanNo);
            mTvPiace = view.findViewById(R.id.tv_piace);
            mLl_item = view.findViewById(R.id.ll_item);
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