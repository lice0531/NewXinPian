package android_serialport_api.xingbang.custom;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.QuYu;

/**
 * 适配器
 */
public class QuYuAdapter extends BaseQuickAdapter<QuYu, BaseViewHolder> {

    private static final int STATE_DEFAULT = 0;//默认状态
    int mEditMode = STATE_DEFAULT;

    public QuYuAdapter(int layoutResId, @Nullable List<QuYu> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, QuYu item) {
        int position = helper.getLayoutPosition();
//        Log.e(TAG, "item.getDetonatorIdSup(): "+item.toString() );
//        Log.e(TAG, "item.getDetonatorIdSup(): "+item.getDetonatorIdSup() );
        GreenDaoMaster master = new GreenDaoMaster();
        int total=new GreenDaoMaster().queryDetonatorRegionDesc().size();
        int maxPai = master.getPieceMaxPai();
        int max = master.getPieceMaxNumDelay(item.getQyid());
        int min = master.getPieceMinNumDelay(item.getQyid());

        helper.setText(R.id.qy_no, item.getName() + "");//
        helper.setText(R.id.qy_txt_total, "共:" + total+"发");//
        helper.setText(R.id.qy_txt_totalPai, "共:" +maxPai+"排"+ total+"发");//
        helper.setText(R.id.qy_txt_minDealy, "最小延时:"+min );//
        helper.setText(R.id.qy_txt_maxDealy, "最大延时:"+max);//

    }

    /**
     * 设置编辑状态   接收Activity中传递的值，并改变Adapter的状态
     */
    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();//刷新
    }

}
