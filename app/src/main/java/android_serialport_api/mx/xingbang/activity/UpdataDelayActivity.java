package android_serialport_api.mx.xingbang.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android_serialport_api.mx.xingbang.BaseActivity;
import android_serialport_api.mx.xingbang.R;
import android_serialport_api.mx.xingbang.databinding.ActivityUpdataDelayBinding;
import android_serialport_api.mx.xingbang.utils.MmkvUtils;

public class UpdataDelayActivity extends BaseActivity implements View.OnClickListener{

    ActivityUpdataDelayBinding binding;
    private String TAG ="修改延时";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_updata_delay);
        // 新方法
        binding = ActivityUpdataDelayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.udEtQsys.setText((String)MmkvUtils.getcode("qsys","0"));//起始延时
        binding.udEtPjys.setText((String)MmkvUtils.getcode("pjys","0"));//排间延时
        binding.udEtKjys.setText((String)MmkvUtils.getcode("kjys","0"));//孔间延时
        binding.udEtKnys.setText((String)MmkvUtils.getcode("knys","0"));//孔内延时
        binding.udEtF1ys.setText((String)MmkvUtils.getcode("f1ys","0"));//f1延时
        binding.udEtF2ys.setText((String)MmkvUtils.getcode("f2ys","0"));//f2延时
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "onClick: "+binding.udEtQsys.getText());
        if(view.getId()==R.id.ud_btn_save){
            MmkvUtils.savecode("qsys",binding.udEtQsys.getText());//起始延时
            MmkvUtils.savecode("pjys",binding.udEtPjys.getText());//排间延时
            MmkvUtils.savecode("kjys",binding.udEtKjys.getText());//孔间延时
            MmkvUtils.savecode("knys",binding.udEtKnys.getText());//孔内延时
            MmkvUtils.savecode("f1ys",binding.udEtF1ys.getText());//f1延时
            MmkvUtils.savecode("f2ys",binding.udEtF2ys.getText());//f2延时
            show_Toast("保存成功");
        }
    }
}