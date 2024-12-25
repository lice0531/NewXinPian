package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadHisDetailRecyclerAdapter;
import android_serialport_api.xingbang.custom.QuYuAdapter;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.QuYu;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuYuActivity extends AppCompatActivity {


    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.title_add)
    ImageView titleAdd;
    @BindView(R.id.title_right)
    TextView titleRight;
    @BindView(R.id.rl_quyu)
    RecyclerView rlQuyu;

    TextView totalbar_title;
    private QuYuAdapter hisAdapter;
    private List<QuYu> mListData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qu_yu);
        ButterKnife.bind(this);

        titleText.setText("选择区域");
        mListData = new GreenDaoMaster().queryQuYu();
        // 线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rlQuyu.setLayoutManager(linearLayoutManager);
        hisAdapter = new QuYuAdapter(R.layout.item_quyu, mListData);
        rlQuyu.setAdapter(hisAdapter);



    }

    @OnClick({R.id.title_back,R.id.title_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back://注册
                finish();
                break;
            case R.id.title_add://注册
                carteQuYu();
                break;
        }
    }

    private void carteQuYu() {
        QuYu quYu = new QuYu();
        quYu.setName("区域1");
        quYu.setQyid("1");
        getDaoSession().getQuYuDao().insert(quYu);
    }
}