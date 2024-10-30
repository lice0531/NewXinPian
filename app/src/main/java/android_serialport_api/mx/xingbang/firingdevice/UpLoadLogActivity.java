package android_serialport_api.mx.xingbang.firingdevice;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.fragment.ErrLogFragment;
import android_serialport_api.mx.xingbang.fragment.LogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpLoadLogActivity extends FragmentActivity {
    @BindView(R.id.home_ctv)
    CheckedTextView homeCtv;
    @BindView(R.id.info_ctv)
    CheckedTextView infoCtv;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private List<Fragment> fglist = new ArrayList<>();

    private int currentTab = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_load_log);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        LogFragment lfg = new LogFragment();
        ErrLogFragment efg = new ErrLogFragment();
        fglist.add(lfg);
        fglist.add(efg);
        viewpager.setAdapter(new homeFragmentAdapter(getSupportFragmentManager()));
    }

    @OnClick({R.id.home_ctv, R.id.info_ctv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_ctv:
                swichTab(0);
                break;
            case R.id.info_ctv:
                swichTab(1);
                break;
        }
    }

    /**
     * @author Administrator
     */
    class homeFragmentAdapter extends FragmentStatePagerAdapter {

        public homeFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return fglist.get(arg0);
        }

        @Override
        public int getCount() {
            return fglist.size();
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            // TODO Auto-generated method stub
            super.finishUpdate(container);
            if (viewpager.getCurrentItem() == currentTab) {
                return;
            }
            currentTab = viewpager.getCurrentItem();
        }
    }


    /**
     * Tab之间的切换
     *
     * @param postion
     */
    private void swichTab(int postion) {
        // TODO Auto-generated method stub
        viewpager.setCurrentItem(postion);
    }

}
