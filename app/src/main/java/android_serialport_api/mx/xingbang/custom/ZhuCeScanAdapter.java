package android_serialport_api.mx.xingbang.custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import android_serialport_api.mx.xingbang.R;

public class ZhuCeScanAdapter extends BaseExpandableListAdapter {
    List<String> mGroupList;//一级List
    List<List<String>> mChildList;//二级List 注意!这里是List里面套了一个List<String>,实际项目你可以写一个pojo类来管理2层数据

    public ZhuCeScanAdapter(List<String> groupList, List<List<String>> childList){
        mGroupList = groupList;
        mChildList = childList;
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
            convertView.setTag(viewHolder1);
        } else {
            viewHolder1 = (ViewHolder1) convertView.getTag();
        }
        viewHolder1.tv1_zc_pai.setText((groupPosition+1)+"排");
        viewHolder1.tv1_zc_startTime.setText("起始延时:0ms");
        viewHolder1.tv1_zc_total.setText("数量:0");
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
        viewHolder2.tv2_zc_no.setText((groupPosition+1)+"-1-"+(childPosition+1));
        viewHolder2.tv2_zc_id.setText("5390418050000");
        viewHolder2.tv2_zc_delay.setText("0");
        viewHolder2.tv2_zc_status.setText("正常");
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
    }
    public class ViewHolder2 {
        private TextView tv2_zc_no;
        private TextView tv2_zc_id;
        private TextView tv2_zc_delay;
        private TextView tv2_zc_status;
    }
}
