package android_serialport_api.xingbang.custom;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;
import android_serialport_api.xingbang.R;

/**
 * 适配器
 */
public class SrDenatorAdapter extends BaseQuickAdapter<SingleRegisterData, BaseViewHolder> {
    private InnerItemOnclickListener mListener;
    public SrDenatorAdapter(int layoutResId, @Nullable List<SingleRegisterData> data) {
        super(layoutResId, data);
    }

    private boolean isShowCheck = false;
    public void showCheckBox(boolean isShow){
        isShowCheck = isShow;
        notifyDataSetChanged();
    }

    boolean Uid_gone=true;
    public void setUid(boolean set) {
        Uid_gone = set;
    }

    @Override
    protected void convert(BaseViewHolder helper, SingleRegisterData item) {
        int position = helper.getLayoutPosition();
        helper.setText(R.id.tv_blastserial,(position + 1) + "");
        helper.setText(R.id.tv_delay,item.getDelay() + "");      // 序号
//        helper.setText(R.id.tv_shellBlastNo,item.getDetonatorId());   // 管壳号
        TextView tv_shellBlastNo = helper.getView(R.id.tv_shellBlastNo);
        if(Uid_gone){
            tv_shellBlastNo.setText(item.getShellBlastNo());
        }else {
            tv_shellBlastNo.setText(item.getDetonatorId());
        }
        ImageView iv_edit = helper.getView(R.id.iv_edit);
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    //确保position值有效
                    mListener.itemClick(v, position);
                }
            }
        });
        CheckBox iv_check = helper.getView(R.id.iv_check);
        iv_check.setVisibility(isShowCheck ? View.VISIBLE : View.INVISIBLE);
        iv_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setSelect(isChecked);
            }
        });
        iv_check.setChecked(item.isSelect());
        helper.addOnClickListener(R.id.item_data);//添加item点击事件
    }

    public interface InnerItemOnclickListener {
        void itemClick(View v,int position);
    }

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener) {
        this.mListener = listener;
    }
}
