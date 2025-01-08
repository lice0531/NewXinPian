package android_serialport_api.xingbang.custom;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.R2;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.PaiData;
import android_serialport_api.xingbang.models.ZhuCeListBean;

public class ZhuCeScanAdapter extends BaseExpandableListAdapter {
    List<PaiDataSelect> mGroupList;//一级List
    List<List<DenatorBaseinfoSelect>> mChildList;//二级List
    int mGroupPosition=-1;
    int mChildPosition=-1;
    private OnChildButtonClickListener listener;
    private OngroupButtonClickListener listener_group;
    private boolean checkBox_gone=true;
    private boolean Uid_gone=true;
    public ZhuCeScanAdapter(List<PaiDataSelect> groupList, List<List<DenatorBaseinfoSelect>> childList, OnChildButtonClickListener listener,OngroupButtonClickListener listener_group){
        mGroupList = groupList;
        mChildList = childList;
        this.listener = listener;
        this.listener_group = listener_group;
    }

    public void setSelcetPosition(int mGroupPosition,int mChildPosition){
        this.mGroupPosition = mGroupPosition;
        this.mChildPosition = mChildPosition;
        notifyDataSetChanged();
    }

    public void setCheckBox(boolean set) {
        checkBox_gone= set;
    }
    public void setUid(boolean set) {
        checkBox_gone= set;
    }
    @Override
    public int getGroupCount() {//返回第一级List长度
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {//返回指定groupPosition的第二级List长度
        return mChildList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {//返回一级List里的内容
        return  mGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {//返回二级List的内容
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition + childPosition;
    }

    public void setNewChild(List<List<DenatorBaseinfoSelect>> childList){
        mChildList = childList;
        notifyDataSetChanged();
    }
    /**
     * 指示在对基础数据进行更改时子ID和组ID是否稳定
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder1 viewHolder1;
        if (convertView == null) {
            viewHolder1 = new ViewHolder1();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zc_scan_leve1, null);
            viewHolder1.tv1_zc_pai=convertView.findViewById(R.id.item1_pai);
            viewHolder1.tv1_zc_startTime=convertView.findViewById(R.id.item1_startTime);
            viewHolder1.tv1_zc_total=convertView.findViewById(R.id.item1_total);
            viewHolder1.itme_ll=convertView.findViewById(R.id.item1_ll);
            viewHolder1.im_xiugai1=convertView.findViewById(R.id.im_xiugai1);
            viewHolder1.pai_check=convertView.findViewById(R.id.pai_check);
            convertView.setTag(viewHolder1);
        } else {
            viewHolder1 = (ViewHolder1) convertView.getTag();
        }
        viewHolder1.tv1_zc_pai.setText(mGroupList.get(groupPosition).getPaiId()+"排");
        viewHolder1.tv1_zc_startTime.setText("延时:"+mGroupList.get(groupPosition).getDelayMin()+"~"+mGroupList.get(groupPosition).getDelayMax()+"ms");
        viewHolder1.tv1_zc_total.setText("数量:"+mGroupList.get(groupPosition).getSum());
        if(checkBox_gone){
            viewHolder1.pai_check.setVisibility(View.GONE);
        }else {
            viewHolder1.pai_check.setVisibility(View.VISIBLE);
        }
        if(mGroupPosition == groupPosition) {
            viewHolder1.itme_ll.setBackgroundColor(Color.GREEN);
            //这是关键部分 通过mGroupPosition 和 groupPosition 进行比对，然后再通过 mChildPosition 和 childPosition进行比对，就是你点击的那个Iten     写入你要实现的逻辑
        }else {
            viewHolder1.itme_ll.setBackgroundResource(R.color.result_minor_text);
        }
        viewHolder1.pai_check.setChecked(mGroupList.get(groupPosition).isSelect());
        viewHolder1.pai_check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mGroupList.get(groupPosition).setSelect(isChecked);
        });
        viewHolder1.im_xiugai1.setOnClickListener(v -> {
            if (listener != null) {
                listener_group.OngroupButtonClickListener(groupPosition);
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent ) {
        ViewHolder2 viewHolder2;
        if (convertView == null) {
            viewHolder2 = new ViewHolder2();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zc_scan_leve2, null);
            viewHolder2.tv2_zc_no=convertView.findViewById(R.id.item2_no);
            viewHolder2.tv2_zc_id=convertView.findViewById(R.id.item2_id);
            viewHolder2.tv2_zc_delay=convertView.findViewById(R.id.item2_delay);
            viewHolder2.tv2_zc_status=convertView.findViewById(R.id.item2_status);
            viewHolder2.itme2_ll=convertView.findViewById(R.id.item2_ll);
            viewHolder2.im_xiugai2=convertView.findViewById(R.id.im_xiugai2);
            viewHolder2.kong_check=convertView.findViewById(R.id.kong_check);
            convertView.setTag(viewHolder2);
        } else {
            viewHolder2 = (ViewHolder2) convertView.getTag();
        }

        if(mChildPosition == childPosition && mGroupPosition == groupPosition) {
            viewHolder2.itme2_ll.setBackgroundColor(Application.getContext().getResources().getColor(R.color.item_yellow));
            //这是关键部分 通过mGroupPosition 和 groupPosition 进行比对，然后再通过 mChildPosition 和 childPosition进行比对，就是你点击的那个Iten     写入你要实现的逻辑
        }else {
            viewHolder2.itme2_ll.setBackgroundResource(R.color.white);
        }
        viewHolder2.tv2_zc_no.setText(
                mChildList.get(groupPosition).get(childPosition).getPai()+"-"+
                        mChildList.get(groupPosition).get(childPosition).getBlastserial()
                        +"-"+mChildList.get(groupPosition).get(childPosition).getDuanNo());
        //+"-"+mChildList.get(groupPosition).get(childPosition).getSitholeNum())
        if(Uid_gone){
            viewHolder2.tv2_zc_id.setText(mChildList.get(groupPosition).get(childPosition).getShellBlastNo());
        }else {
            viewHolder2.tv2_zc_id.setText(mChildList.get(groupPosition).get(childPosition).getDenatorId());
        }

        viewHolder2.tv2_zc_delay.setText(mChildList.get(groupPosition).get(childPosition).getDelay()+"");
        viewHolder2.tv2_zc_status.setTextColor("异常".equals(mChildList.get(groupPosition).
                get(childPosition).getStatusName()) ? Color.RED : Color.BLACK);
        viewHolder2.tv2_zc_status.setText(mChildList.get(groupPosition).get(childPosition).getStatusName());
        if(checkBox_gone){
            viewHolder2.kong_check.setVisibility(View.GONE);
        }else {
            viewHolder2.kong_check.setVisibility(View.VISIBLE);
        }
        viewHolder2.kong_check.setChecked(mChildList.get(groupPosition).get(childPosition).isSelect());
        viewHolder2.kong_check.setOnCheckedChangeListener((buttonView, isChecked) ->
                mChildList.get(groupPosition).get(childPosition).setSelect(isChecked));
        viewHolder2.im_xiugai2.setTag(childPosition); // 设置一个tag来识别按钮
        viewHolder2.im_xiugai2.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChildButtonClick(groupPosition, childPosition);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void removeGroup(int groupPosition) {
        mGroupList .remove(groupPosition);
        mChildList.remove(groupPosition);
    }

    public void removeChild(int groupPosition, int childPosition) {
        mChildList.get(groupPosition).remove(childPosition) ;
    }

    public class ViewHolder1 {
        private TextView tv1_zc_pai;
        private TextView tv1_zc_startTime;
        private TextView tv1_zc_total;
        private ImageView im_xiugai1;
        private LinearLayout itme_ll;
        private CheckBox pai_check;


    }
    public class ViewHolder2 {
        private LinearLayout itme2_ll;
        private TextView tv2_zc_no;
        private TextView tv2_zc_id;
        private TextView tv2_zc_delay;
        private TextView tv2_zc_status;
        private ImageView im_xiugai2;
        private CheckBox kong_check;
    }
}
