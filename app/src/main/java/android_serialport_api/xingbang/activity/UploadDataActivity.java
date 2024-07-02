package android_serialport_api.xingbang.activity;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.RecyclerViewAdapter_upload;
import android_serialport_api.xingbang.databinding.ActivityUploadDataBinding;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_MainDao;

public class UploadDataActivity extends AppCompatActivity implements View.OnClickListener{
    
    ActivityUploadDataBinding binding;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter_upload<DenatorHis_Main> mAdapter;
    private List<DenatorHis_Main> list_savedate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //标题设置
        TextView title=findViewById(R.id.title_text);
        title.setText("单发检测雷管");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setVisibility(View.GONE);
        iv_back.setOnClickListener(v -> finish());

        loadMoreData();
        //新的适配方法 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        binding.udJiluRv.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerViewAdapter_upload<>(this, 7);
        binding.udJiluRv.setAdapter(mAdapter);
        mAdapter.setListData(list_savedate, 7);//类型
        mAdapter.setOnItemClickListener((view, position) -> {
            Logger.e("点击事件"+"position: "+position );
            Logger.e("点击事件"+ "list_savedate.get(position): "+list_savedate.get(position).toString() );
        });
        mAdapter.notifyDataSetChanged();
    }
    private void loadMoreData() {
        list_savedate.clear();
        getDaoSession().clear();
        list_savedate = getDaoSession().getDenatorHis_MainDao().queryBuilder().orderDesc(DenatorHis_MainDao.Properties.Id).list();
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ud_btn_upload){
            upload();
        }
    }

    private void upload() {

    }
}