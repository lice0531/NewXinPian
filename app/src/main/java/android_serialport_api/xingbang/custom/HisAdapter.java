package android_serialport_api.xingbang.custom;

import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.utils.MmkvUtils;

/**
 * 适配器
 */
public class HisAdapter extends BaseQuickAdapter<QueryHisData, BaseViewHolder> {

    private static final int STATE_DEFAULT = 0;//默认状态
    int mEditMode = STATE_DEFAULT;
    private String Shangchuan = (String) MmkvUtils.getcode("Shangchuan", "是");
    public HisAdapter(int layoutResId, @Nullable List<QueryHisData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, QueryHisData item) {
        int position = helper.getLayoutPosition();

        helper.setText(R.id.serialNo,  (getData().size()- position)+"");//序号
        helper.setText(R.id.fireDate,item.getBlastdate());
        helper.setText(R.id.txtstatus,item.getBlastdate());
        helper.setText(R.id.txtsum,new GreenDaoMaster().queryHis(item.getBlastdate(),Shangchuan)+"");

        if("未传".equals(item.getUploadStatus())){
            helper.setText(R.id.txtstatus,mContext.getString(R.string.text_query_up));	//"未上传"
            helper.setText(R.id.bt_upload,mContext.getString(R.string.text_query_uploda));//"上传"
            helper.setBackgroundRes(R.id.ly_his,R.drawable.textview_border_green);
            helper.setBackgroundRes(R.id.serialNo,R.drawable.textview_border_green);
            helper.setBackgroundRes(R.id.fireDate,R.drawable.textview_border_green);
            helper.setBackgroundRes(R.id.txtstatus,R.drawable.textview_border_green);
        }else {
            helper.setText(R.id.txtstatus,mContext.getString(R.string.text_query_uploaded));//"已上传"
            helper.setText(R.id.bt_upload,mContext.getString(R.string.text_query_chong));//"重传"
            helper.setBackgroundRes(R.id.ly_his,R.drawable.textview_border_red);
            helper.setBackgroundRes(R.id.serialNo,R.drawable.textview_border_red);
            helper.setBackgroundRes(R.id.fireDate,R.drawable.textview_border_red);
            helper.setBackgroundRes(R.id.txtstatus,R.drawable.textview_border_red);
        }


        helper.addOnClickListener(R.id.bt_upload);//添加item点击事件
        helper.addOnClickListener(R.id.bt_delete);//添加item点击事件

        if (mEditMode == STATE_DEFAULT) {
            //默认不显示
            helper.getView(R.id.iv_check).setVisibility(View.GONE);
        } else {
            //显示   显示之后再做点击之后的判断
            helper.getView(R.id.iv_check).setVisibility(View.VISIBLE);

            if (item.isSelect()) {//点击时，true 选中
                //设置选中时的背景图片
                helper.getView(R.id.iv_check).setBackgroundResource(R.drawable.icon_choose_selected);
            } else {//false 取消选中
                //设置取消选中时的背景图片
                helper.getView(R.id.iv_check).setBackgroundResource(R.drawable.icon_choose_default);
            }
        }
    }

    /**
     * 设置编辑状态   接收Activity中传递的值，并改变Adapter的状态
     */
    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();//刷新
    }


}
