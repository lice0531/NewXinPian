package android_serialport_api.xingbang.custom;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.QuYu;

/**
 * 适配器
 */
public class QuYuAdapter extends BaseQuickAdapter<QuYuData, BaseViewHolder> {

    private static final int STATE_DEFAULT = 0;//默认状态
    int mEditMode = STATE_DEFAULT;

    public QuYuAdapter(int layoutResId, @Nullable List<QuYuData> data) {
        super(layoutResId, data);
    }

    private boolean isShowCheck = false;
    public void showCheckBox(boolean isShow){
        isShowCheck = isShow;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, QuYuData item) {
        int position = helper.getLayoutPosition();
//        Log.e(TAG, "item.getDetonatorIdSup(): "+item.toString() );
//        Log.e(TAG, "item.getDetonatorIdSup(): "+item.getDetonatorIdSup() );
        LinearLayout cl_top = helper.getView(R.id.cl_top);
        cl_top.setBackgroundColor("true".equals(item.getSelected()) ? Color.GREEN : Color.WHITE);
        CheckBox cbIsSelected = helper.getView(R.id.cb_check);
        cbIsSelected.setVisibility(isShowCheck ? View.VISIBLE : View.INVISIBLE);
        cbIsSelected.setChecked(item.isSelect());
        cbIsSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setSelect(isChecked);
            }
        });
        GreenDaoMaster master = new GreenDaoMaster();
        int total=new GreenDaoMaster().queryDetonatorSize(item.getQyid()+"");
        int paisum = master.getPaisum(item.getQyid()+"");
        int max = master.getPieceMaxNumDelay(item.getQyid()+"");
        int min = master.getPieceMinNumDelay(item.getQyid()+"");
        int kong = master.querytotalKong(item.getQyid()+"");
        TextView tv_yzw = helper.getView(R.id.tv_yzw);
        tv_yzw.setVisibility("true".equals(item.getSelected()) ? View.VISIBLE : View.GONE);
        helper.setText(R.id.qy_no, item.getQyid() + "");//
        helper.setText(R.id.qy_txt_total, "共:" + kong+"孔");//
        helper.setText(R.id.qy_txt_totalPai, "共:" +paisum+"排"+ kong+"孔"+ total+"发");//
        helper.setText(R.id.qy_txt_minDealy, "最小延时:"+min );//
        helper.setText(R.id.qy_txt_maxDealy, "最大延时:"+max);//
        TextView qy_txt_Dealy = helper.getView(R.id.qy_txt_Dealy);
        qy_txt_Dealy.setText("延时:" + min + "~" + max + "ms");
    }

    /**
     * 设置编辑状态   接收Activity中传递的值，并改变Adapter的状态
     */
    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();//刷新
    }

}
