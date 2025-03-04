package android_serialport_api.xingbang.custom;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.models.VoFireHisMain;
public class LoadHisDetailRecyclerAdapter extends BaseQuickAdapter<VoFireHisMain, BaseViewHolder> {
    private List<VoFireHisMain> list_his;
    private Context mContext;
    private String mShangchuan;
    private OnItemClickListener onItemClickListener;//声明自定义的监听接口

    public LoadHisDetailRecyclerAdapter(int layoutResId, @Nullable List<VoFireHisMain> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, VoFireHisMain item) {
        Log.e("上传","Shangchuan:" + mShangchuan);
        int position = holder.getLayoutPosition();
        holder.setText(R.id.serialNo,(position+1)+"");
        holder.setText(R.id.fireDate,item.getBlastdate());
        Button bt_delete = holder.getView(R.id.bt_delete);
        Button bt_upload = holder.getView(R.id.bt_upload);
        holder.setText(R.id.txtsum,new GreenDaoMaster().queryHis(item.getBlastdate(),mShangchuan)+"");
        if("未上传".equals(item.getUploadStatus())){
            holder.setText(R.id.txtstatus,mContext.getString(R.string.text_query_up));	//"未上传"
            bt_upload.setText(mContext.getString(R.string.text_query_uploda));//"上传"
            holder.setBackgroundRes(R.id.ly_his,R.drawable.textview_border_green);
//            holder.setBackgroundRes(R.id.serialNo,R.drawable.textview_border_green);
            holder.setBackgroundRes(R.id.fireDate,R.drawable.textview_border_green);
            holder.setBackgroundRes(R.id.txtstatus,R.drawable.textview_border_green);
        }else{
            holder.setText(R.id.txtstatus,mContext.getString(R.string.text_query_uploaded));//"已上传"
            bt_upload.setText(mContext.getString(R.string.text_query_chong));//"重传"
            holder.setBackgroundRes(R.id.ly_his,R.drawable.textview_border_red);
//            holder.setBackgroundRes(R.id.serialNo,R.drawable.textview_border_red);
            holder.setBackgroundRes(R.id.fireDate,R.drawable.textview_border_red);
            holder.setBackgroundRes(R.id.txtstatus,R.drawable.textview_border_red);
        }
        holder.itemView.setTag(position);
        CheckBox iv_check = holder.getView(R.id.iv_check);
        iv_check.setVisibility(isShowCheck ? View.VISIBLE : View.INVISIBLE);
        iv_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setSelect(isChecked);
            }
        });
        iv_check.setChecked(item.isSelect());
        bt_upload.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                //确保position值有效
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onButtonClicked(view, position);
                }
            }
        });

        bt_delete.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                //确保position值有效
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onButtonClicked(view, position);
                }
            }
        });
    }

    //提供set方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //定义接口
    public interface OnItemClickListener {
        void onButtonClicked(View view, int position);
        void onItemClick(View view,int position);
    }

    public void setDataSource(List<VoFireHisMain> list_his) {
        this.list_his = list_his;
        notifyDataSetChanged();
    }

    public void setStatus(Context context,String Shangchuan) {
        this.mContext = context;
        this.mShangchuan = Shangchuan;
    }

    private boolean isShowCheck = false;
    public void showCheckBox(boolean isShow){
        isShowCheck = isShow;
        notifyDataSetChanged();
    }
}
