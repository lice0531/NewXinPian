package android_serialport_api.xingbang.custom;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.Project;


/**
 * 雷管信息页面 适配器
 */
public class RecyclerViewAdapter_Project<T> extends RecyclerView.Adapter<RecyclerViewAdapter_Project.ViewHolder> {

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
    public RecyclerViewAdapter_Project(Context context, int line) {
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
         if (mLine == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        }
        return new ViewHolder(view,mOnItemClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       if (mIndex == 1) {
                List<Project> list_pro = (List<Project>) mListData;
                holder.tv_sp_name.setText(list_pro.get(position).getProject_name());
                if(list_pro.get(position).getHtbh().length()>1){
                    holder.tv_sp_htbh.setText(list_pro.get(position).getHtbh());
                    holder.ll_htbh.setVisibility(View.VISIBLE);
                    holder.ll_xmbh.setVisibility(View.GONE);
                    holder.ll_dwdm.setVisibility(View.GONE);
                }else if (list_pro.get(position).getXmbh().length()>1){
                    holder.ll_xmbh.setVisibility(View.VISIBLE);
                    holder.ll_htbh.setVisibility(View.GONE);
                    holder.ll_dwdm.setVisibility(View.GONE);
                    holder.tv_sp_xmbh.setText(list_pro.get(position).getXmbh());
                }else if (list_pro.get(position).getDwdm().length()>1){
                    holder.ll_dwdm.setVisibility(View.VISIBLE);
                    holder.ll_htbh.setVisibility(View.GONE);
                    holder.ll_xmbh.setVisibility(View.GONE);
                    holder.tv_sp_dwdm.setText(list_pro.get(position).getDwdm());
                }
                holder.tv_sp_sfz.setText(list_pro.get(position).getBprysfz());
                holder.ly_sq.setTag(position);
                holder.cb_isChoice.setChecked(list_pro.get(position).getSelected().equals("true"));
                Logger.e("adapter是否选中"+list_pro.get(position).getSelected().equals("true"));
                if(list_pro.get(position).getSelected().equals("true")){
                    holder.cb_isChoice.setVisibility(View.VISIBLE);
                }else {
                    holder.cb_isChoice.setVisibility(View.GONE);
                }
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_htbh;
        private LinearLayout ll_xmbh;
        private LinearLayout ll_dwdm;
        private TextView tv_sp_name;
        private TextView tv_sp_htbh;
        private TextView tv_sp_xmbh;
        private TextView tv_sp_dwdm;
        private CheckBox cb_isChoice;
        private TextView tv_sp_coordxy;
        private TextView tv_sp_sfz;
        private TextView tv_sp_yyx_1;
        private TextView tv_sp_yyx_2;
        private TextView tv_sp_yyx_3;
        private CardView ly_sq;
        private ImageView iv_sp_xiugai;

        ViewHolder(View view,final OnItemClickListener onClickListener) {
            super(view);
            //添加项目页面
            ll_htbh = view.findViewById(R.id.ll_htbh);
            ll_xmbh = view.findViewById(R.id.ll_xmbh);
            ll_dwdm = view.findViewById(R.id.ll_dwdm);
            cb_isChoice = view.findViewById(R.id.isChoice);
            tv_sp_name = view.findViewById(R.id.tv_sp_name);
            tv_sp_htbh = view.findViewById(R.id.tv_sp_htbh);
            tv_sp_xmbh = view.findViewById(R.id.tv_sp_xmbh);
            tv_sp_dwdm = view.findViewById(R.id.tv_sp_dwdm);
            tv_sp_yyx_1 = view.findViewById(R.id.tv_sp_yyx_1);
            tv_sp_yyx_2 = view.findViewById(R.id.tv_sp_yyx_2);
            tv_sp_yyx_3 = view.findViewById(R.id.tv_sp_yyx_3);
//            viewHolder.tv_sp_coordxy = (TextView) convertView.findViewById(R.id.tv_sp_coordxy);
            tv_sp_sfz = view.findViewById(R.id.tv_sp_sfz);
            ly_sq = view.findViewById(R.id.ly_sq);
            iv_sp_xiugai = view.findViewById(R.id.iv_sp_xiugai);
            iv_sp_xiugai.setOnClickListener(v -> {
                if (onClickListener != null) {
                    int position = getBindingAdapterPosition();
                    //确保position值有效
                    if (position != RecyclerView.NO_POSITION) {
                        onClickListener.onButtonClicked(view, position);
                    }
                }
            });
            ly_sq.setOnClickListener(v -> {

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