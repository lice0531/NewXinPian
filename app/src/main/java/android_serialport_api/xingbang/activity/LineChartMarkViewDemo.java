package android_serialport_api.xingbang.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;

public class LineChartMarkViewDemo extends MarkerView {

    DecimalFormat df = new DecimalFormat(".00");
    private TextView mXValueTv;
    private TextView mYValueTv;
    List mlist = new ArrayList();


    public LineChartMarkViewDemo(Context context) {
        super(context, R.layout.mark_view_demo);

        mXValueTv = findViewById(R.id.xValues_tv);
        mYValueTv = findViewById(R.id.yValue_tv);
        mlist.add(0);
        mlist.add(5);
        mlist.add(10);
        mlist.add(15);
        mlist.add(20);
        mlist.add(25);
        mlist.add(30);
        mlist.add(35);
        mlist.add(40);
        mlist.add(45);
        mlist.add(50);
        mlist.add(55);
        mlist.add(60);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //展示自定义X轴值 后的X轴内容
        mXValueTv.setText("时间 = " +mlist.get((int)e.getX())+"s");
        mYValueTv.setText("电流 = " + df.format(e.getY())+"μA");
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}