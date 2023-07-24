package android_serialport_api.xingbang.firingdevice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LeiGuanAdapter;
import android_serialport_api.xingbang.custom.LoadListView;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.ShouQuan;
import android_serialport_api.xingbang.models.DanLingBean;
import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * 显示授权雷管信息
 * */
public class ShouQuanLegActivity extends BaseActivity implements LoadListView.OnLoadMoreListener{

    @BindView(R.id.lv_sq_lg)
    LoadListView lvSqLg;

    private LeiGuanAdapter mAdapter;
    private List<Map<String,Object>> map_dl=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shou_quan_leg);
        ButterKnife.bind(this);
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        map_dl = (List<Map<String, Object>>) bundle.getSerializable("list_dl");
        int p=bundle.getInt("position");
        GreenDaoMaster master = new GreenDaoMaster();

        ShouQuan sq =master.getShouquan(p);
//        Log.e("显示授权", "sq: "+sq.toString() );
        Gson gson = new Gson();
        DanLingBean danLingBean = gson.fromJson(sq.getJson(), DanLingBean.class);
        mAdapter= new LeiGuanAdapter(this, danLingBean, R.layout.item_list_lg);
        lvSqLg.setAdapter(mAdapter);
        lvSqLg.setLoadMoreListener(this);
    }
    @Override
    public void loadMore() {
        lvSqLg.setFooterGone();
    }
}
