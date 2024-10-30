package android_serialport_api.mx.xingbang.custom;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import android_serialport_api.mx.xingbang.R;

/**
 * Created by xingbang on 2021/1/28.
 */

public class ZhuCeAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private int defaultSelection = -1;
    public ZhuCeAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.mContext = mContext;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_blast, parent, false);
//            viewHolder.txt_item = (TextView) convertView.findViewById(R.id.txt_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        viewHolder.txt_item.setText(getItem(position).toString());
//
//        if (position == defaultSelection) {// 选中时设置单纯颜色
//            viewHolder.txt_item.setTextColor(text_selected_color);
//            convertView.setBackgroundColor(bg_selected_color);
//        } else {// 未选中时设置selector
//            viewHolder.txt_item.setTextColor(colors);
//            convertView.setBackgroundResource(R.drawable.listview_color_selector);
//        }
        return convertView;
    }

    class ViewHolder {
//        TextView txt_item;
    }

    /**
     * @param position
     *            设置高亮状态的item
     */
//    public void setSelectPosition(int position) {
//        if (!(position < 0 || position > list.size())) {
//            defaultSelection = position;
//            notifyDataSetChanged();
//        }
//    }

}
