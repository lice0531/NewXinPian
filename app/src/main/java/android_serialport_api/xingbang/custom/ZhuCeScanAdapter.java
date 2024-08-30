package android_serialport_api.xingbang.custom;

import android.graphics.Color;
import android.renderscript.ScriptIntrinsicColorMatrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.models.ZhuCeListBean;

public class ZhuCeScanAdapter extends BaseExpandableListAdapter {
    List<ZhuCeListBean> mGroupList;//一级List
    List<List<DenatorBaseinfo>> mChildList;//二级List 注意!这里是List里面套了一个List<String>,实际项目你可以写一个pojo类来管理2层数据
    int mGroupPosition=-1;
    int mChildPosition=-1;
    public ZhuCeScanAdapter(List<ZhuCeListBean> groupList, List<List<DenatorBaseinfo>> childList){
        mGroupList = groupList;
        mChildList = childList;
    }

    public void setSelcetPosition(int mGroupPosition,int mChildPosition){
        this.mGroupPosition = mGroupPosition;
        this.mChildPosition = mChildPosition;
        notifyDataSetChanged();
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
            convertView.setTag(viewHolder1);
        } else {
            viewHolder1 = (ViewHolder1) convertView.getTag();
        }
        viewHolder1.tv1_zc_pai.setText(mGroupList.get(groupPosition).getPai()+"排");
        viewHolder1.tv1_zc_startTime.setText("起始延时:"+mGroupList.get(groupPosition).getStartDelay()+"ms");
        viewHolder1.tv1_zc_total.setText("数量:"+mGroupList.get(groupPosition).getTotal());
        if(mGroupPosition == groupPosition) {
            viewHolder1.itme_ll.setBackgroundColor(Color.GREEN);
            //这是关键部分 通过mGroupPosition 和 groupPosition 进行比对，然后再通过 mChildPosition 和 childPosition进行比对，就是你点击的那个Iten     写入你要实现的逻辑
        }else {
            viewHolder1.itme_ll.setBackgroundResource(R.color.result_minor_text);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder2 viewHolder2;
        if (convertView == null) {
            viewHolder2 = new ViewHolder2();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zc_scan_leve2, null);
            viewHolder2.tv2_zc_no=convertView.findViewById(R.id.item2_no);
            viewHolder2.tv2_zc_id=convertView.findViewById(R.id.item2_id);
            viewHolder2.tv2_zc_delay=convertView.findViewById(R.id.item2_delay);
            viewHolder2.tv2_zc_status=convertView.findViewById(R.id.item2_status);
            convertView.setTag(viewHolder2);
        } else {
            viewHolder2 = (ViewHolder2) convertView.getTag();
        }
        viewHolder2.tv2_zc_no.setText(
                mChildList.get(groupPosition).get(childPosition).getPai()+"-"+
                        mChildList.get(groupPosition).get(childPosition).getSithole());
                //+"-"+mChildList.get(groupPosition).get(childPosition).getSitholeNum())
        viewHolder2.tv2_zc_id.setText(mChildList.get(groupPosition).get(childPosition).getShellBlastNo());
        viewHolder2.tv2_zc_delay.setText(mChildList.get(groupPosition).get(childPosition).getDelay()+"");
        viewHolder2.tv2_zc_status.setTextColor("异常".equals(mChildList.get(groupPosition).
                get(childPosition).getStatusName()) ? Color.RED : Color.BLACK);
        viewHolder2.tv2_zc_status.setText(mChildList.get(groupPosition).get(childPosition).getStatusName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class ViewHolder1 {
        private TextView tv1_zc_pai;
        private TextView tv1_zc_startTime;
        private TextView tv1_zc_total;
        private LinearLayout itme_ll;
    }
    public class ViewHolder2 {
        private TextView tv2_zc_no;
        private TextView tv2_zc_id;
        private TextView tv2_zc_delay;
        private TextView tv2_zc_status;
    }
}
