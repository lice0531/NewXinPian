package android_serialport_api.xingbang.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DenatorHis_Main;


/**
 * 雷管信息页面 适配器
 */
public class RecyclerViewAdapter_Denator<T> extends RecyclerView.Adapter<RecyclerViewAdapter_Denator.ViewHolder> {

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
    public RecyclerViewAdapter_Denator(Context context, int line) {
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
        View view = null;
        if (mLine == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item_line, parent, false);
        } else if (mLine == 3) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item_query, parent, false);
        } else if (mLine == 4) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item_paper, parent, false);
        } else if (mLine == 5) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item_del_detonator, parent, false);
        } else if (mLine == 6) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item_downproject, parent, false);
        } else if (mLine == 7) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item_upload, parent, false);
        } else if (mLine == 8) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item_qibao, parent, false);
        }
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mIndex == 1) {
            List<DenatorBaseinfo> list_detonatorBaseInfo = (List<DenatorBaseinfo>) mListData;
            DenatorBaseinfo detonatorBaseInfo = list_detonatorBaseInfo.get(position);
            holder.mTvBlastSerial.setText((list_detonatorBaseInfo.size() - position) + "");                 // 序号
            holder.mTvShellBlastNo.setText(detonatorBaseInfo.getShellBlastNo());   // 管壳号

            if (mLine == 4 || mLine == 5) {
                holder.mTvSitHole.setText(detonatorBaseInfo.getSithole() + "");     // 孔号
                holder.mTvDelay.setText(detonatorBaseInfo.getDelay() + "");         // 延时
            }
            if (mLine == 2) {//单发检测页面
                holder.mTvZhuangTai.setTextColor("异常".equals(detonatorBaseInfo.getStatusName()) ?
                        Color.RED : Color.BLACK);
                holder.mTvZhuangTai.setText(detonatorBaseInfo.getStatusName() + "");//状态
                holder.mTvShuJu.setText((!TextUtils.isEmpty(detonatorBaseInfo.getVoltage()) ? detonatorBaseInfo.getVoltage()
                        : "0")+"V " +(!TextUtils.isEmpty(detonatorBaseInfo.getCurrent()) ? detonatorBaseInfo.getCurrent()
                        : "0")+"μA");
            }

            if (mLine == 3 || mLine == 5) {
                holder.mTvStatus.setText(detonatorBaseInfo.getErrorName());
                if (detonatorBaseInfo.getErrorCode().equals("FF")) {
                    holder.mTvStatus.setTextColor(Color.GREEN);
                } else {
                    holder.mTvStatus.setTextColor(Color.RED);
                }

            }
            // 长按
            if (onitemLongClick != null) {
                holder.itemView.setOnLongClickListener(v -> {
                    onitemLongClick.itemLongClick(position);
                    return true;
                });
            }
        } else if (mIndex == 2) {
            if (mLine == 6) {//下载页面
                List<DenatorBaseinfo> list_lg = (List<DenatorBaseinfo>) mListData;
                DenatorBaseinfo dbInfo = list_lg.get(position);
                holder.mTvShellBlastNo.setText(dbInfo.getShellBlastNo());   // 管壳号
                holder.mTvYouXiaoQi.setText(!TextUtils.isEmpty(dbInfo.getAuthorization()) ?
                        dbInfo.getAuthorization() : "");//授权有效期
                if (!TextUtils.isEmpty(dbInfo.getDownloadStatus())) {
                    if (dbInfo.getDownloadStatus().equals("0")){
                        holder.mTvZhuangTai.setText("雷管正常");
                        holder.mTvZhuangTai.setTextColor(Color.GREEN);
                    }else if(dbInfo.getDownloadStatus().equals("1")){
                        holder.mTvZhuangTai.setText("雷管在黑名单中");
                        holder.mTvZhuangTai.setTextColor(Color.RED);
                    }else if(dbInfo.getDownloadStatus().equals("2")){
                        holder.mTvZhuangTai.setText("雷管已使用");
                        holder.mTvZhuangTai.setTextColor(Color.RED);
                    }else if(dbInfo.getDownloadStatus().equals("3")){
                        holder.mTvZhuangTai.setText("申请的雷管UID不存在");
                        holder.mTvZhuangTai.setTextColor(Color.RED);
                    }else {
                        holder.mTvZhuangTai.setText("");
                    }
                } else {
                    holder.mTvZhuangTai.setText("");
                }
            }
        } else if (mIndex == 7) {
            if (mLine == 7) {//上传页面
                List<DenatorHis_Main> list_lg = (List<DenatorHis_Main>) mListData;
                DenatorHis_Main dbInfo = list_lg.get(position);
                holder.mTvtime.setText(dbInfo.getBlastdate());   // 日期
                if (dbInfo.getPro_htid().length() > 0) {
                    holder.mTvhtbh.setText("合同编号" + dbInfo.getPro_htid() + "");//合同编号
                } else if (dbInfo.getPro_xmbh().length() > 0) {
                    holder.mTvhtbh.setText("项目编号" + dbInfo.getPro_xmbh() + "");//合同编号
                } else if (dbInfo.getPro_dwdm().length() > 0) {
                    holder.mTvhtbh.setText("单位代码" + dbInfo.getPro_dwdm() + "");//合同编号
                }

//                holder.mTvtotal.setText(dbInfo.get());//数量
            }
        }else if (mIndex == 8) {
            List<DenatorBaseinfo> list_lg = (List<DenatorBaseinfo>) mListData;
            DenatorBaseinfo dbInfo = list_lg.get(position);
            holder.mTvBlastSerial.setText(dbInfo.getPai()+"-"+dbInfo.getSithole()+"-");//+dbInfo.getSitholeNum() 序号
            holder.mTvShellBlastNo.setText(dbInfo.getShellBlastNo());   // 管壳号
            holder.mTvZhuangTai.setText(dbInfo.getErrorName() + "");//状态
            holder.mTvDelay.setText(dbInfo.getDelay()+"");//有效期
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
        TextView mTvZhuangTai;
        TextView mTvShuJu;
        TextView mTvYouXiaoQi;
        TextView mTvhtbh;
        TextView mTvtotal;
        TextView mTvtime;
        Button btnUplod;

        ViewHolder(View view) {
            super(view);
            mTvBlastSerial = view.findViewById(R.id.tv_blastserial);
            mTvSitHole = view.findViewById(R.id.tv_sithole);
            mTvDelay = view.findViewById(R.id.tv_delay);
            //下载页面
            mTvShellBlastNo = view.findViewById(R.id.tv_shellBlastNo);
            mTvStatus = view.findViewById(R.id.tv_status);
            mTvZhuangTai = view.findViewById(R.id.tv_zhuangtai);
            mTvShuJu = view.findViewById(R.id.tv_shuju);
            mTvYouXiaoQi = view.findViewById(R.id.tv_youxiaoqi);
            //上传数据页面
            mTvhtbh = view.findViewById(R.id.tv_htbh);
            mTvtotal = view.findViewById(R.id.tv_total);
            mTvtime = view.findViewById(R.id.tv_time);
            btnUplod = view.findViewById(R.id.btn_upload);
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

    private int mPosition;

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

}