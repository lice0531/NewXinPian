package android_serialport_api.xingbang.firingdevice;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.github.ihsg.patternlocker.OnPatternChangeListener;
import com.github.ihsg.patternlocker.PatternIndicatorView;
import com.github.ihsg.patternlocker.PatternLockerView;

import java.util.List;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.utils.shoushi.PatternHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShouShiActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pattern_indicator_view)
    PatternIndicatorView patternIndicatorView;
    @BindView(R.id.pattern_lock_view)
    PatternLockerView patternLockView;
    @BindView(R.id.textMsg)
    TextView textMsg;


    private String TAG = "手势";
    PatternHelper helper = new PatternHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shou_shi);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);



        patternLockView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(@NonNull PatternLockerView patternLockerView) {
                //根据需要添加业务逻辑
            }

            @Override
            public void onChange(@NonNull PatternLockerView patternLockerView, @NonNull List<Integer> list) {
                //根据需要添加业务逻辑
            }

            @Override
            public void onComplete(@NonNull PatternLockerView patternLockerView, @NonNull List<Integer> list) {
                //根据需要添加业务逻辑
                boolean isOk = isPatternOk(list);
                patternLockerView.updateStatus(!isOk);
                patternIndicatorView.updateState(list, !isOk);
                textMsg.setText(helper.getMessage());
            }

            @Override
            public void onClear(@NonNull PatternLockerView patternLockerView) {
                //根据需要添加业务逻辑
//                finishIfNeeded();
            }
        });

    }

    private void finishIfNeeded() {
        if (helper.isFinish()) {
            finish();
        }
    }

    private boolean isPatternOk(List<Integer> list) {
        helper.validateForSetting(list);
        return helper.isOk();
    }



}