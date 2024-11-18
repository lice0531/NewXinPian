package android_serialport_api.xingbang.custom;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import android_serialport_api.xingbang.R;

/**
 * 适配器
 */
public class DataAdapter extends BaseQuickAdapter<ShouQuanData, BaseViewHolder> {

    private static final int STATE_DEFAULT = 0;//默认状态
    int mEditMode = STATE_DEFAULT;

    public DataAdapter(int layoutResId, @Nullable List<ShouQuanData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShouQuanData item) {
        int position = helper.getLayoutPosition();
//        Log.e(TAG, "item.getDetonatorIdSup(): "+item.toString() );
//        Log.e(TAG, "item.getDetonatorIdSup(): "+item.getDetonatorIdSup() );
        helper.setText(R.id.tv_lg_id, item.getShellBlastNo() + "");//UID
        if((item.getQibao()+"").equals("雷管正常")||(item.getQibao()+"").equals("已起爆")){
            helper.setText(R.id.tv_lg_uid, item.getTime());// yxq
        }else {
            helper.setText(R.id.tv_lg_uid, "");// yxq
        }
        if((item.getQibao()+"").equals("雷管正常")||(item.getQibao()+"").equals("已起爆")){
            if(item.getDetonatorId()!=null&&item.getDetonatorId().length()==13){
                if(item.getDetonatorIdSup()!=null&&item.getDetonatorIdSup().length()==2){
                    helper.setText(R.id.tv_lg_yxq, item.getDetonatorId().substring(7)+
                            item.getZhu_yscs()+
                            item.getDetonatorIdSup().substring(1)+
                            item.getCong_yscs());// 煤许工作码
                }else {
                    helper.setText(R.id.tv_lg_yxq, item.getDetonatorId().substring(7)+
                            item.getZhu_yscs());// PT工作码ccccccccccc`
                }


            }
        } else {
            helper.setText(R.id.tv_lg_yxq,"");
        }

        if (item.getQibao()!=null){
            helper.setText(R.id.tv_lg_qb, item.getQibao());// 状态
        }else {
            helper.setText(R.id.tv_lg_qb, "");// 状态
        }

        if((item.getQibao()+"").equals("雷管正常")){
            helper.setTextColor(R.id.tv_lg_qb, Color.GREEN);// 是否起爆
        }else {
            helper.setTextColor(R.id.tv_lg_qb, Color.RED);// 是否起爆
        }

        helper.addOnClickListener(R.id.item_data);//添加item点击事件

        if (mEditMode == STATE_DEFAULT) {
            //默认不显示
            helper.getView(R.id.iv_check).setVisibility(View.GONE);
        } else {
            //显示   显示之后再做点击之后的判断
            helper.getView(R.id.iv_check).setVisibility(View.VISIBLE);

            if (item.isSelect()) {//点击时，true 选中
                helper.getView(R.id.iv_check).setBackgroundResource(R.drawable.icon_choose_selected);
            } else {//false 取消选中
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
