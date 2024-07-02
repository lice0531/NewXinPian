package android_serialport_api.xingbang.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.RecyclerViewAdapter_Denator;
import android_serialport_api.xingbang.databinding.ActivityDownProjectBinding;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.ShouQuan;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.utils.AMapUtils;
import android_serialport_api.xingbang.utils.LngLat;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownProjectActivity extends BaseActivity  implements View.OnClickListener{
    ActivityDownProjectBinding binding;
    private RecyclerViewAdapter_Denator<DenatorBaseinfo> mAdapter;
    private List<DenatorBaseinfo> mListData = new ArrayList<>();//所有雷管列表
    private LinearLayoutManager linearLayoutManager;
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String pro_name = "";//项目名称
    private String jd = "";
    private String wd = "";
    private ArrayList<String> list_uid = new ArrayList<>();
    private int pb_show = 0;//等待动画
    private LoadingDialog tipDlg = null;
    private Handler mHandler_loding = new Handler();//等待动画
    private Handler mHandler_tip = new Handler();//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_down_project);
        binding=ActivityDownProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        TextView title = findViewById(R.id.title_text);
        title.setText("下载工作码");
        ImageView add = findViewById(R.id.title_add);
        ImageView back = findViewById(R.id.title_back);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(DownProjectActivity.this, AddProjectActivity.class);
            startActivity(intent);
        });
        add.setVisibility(View.VISIBLE);
        back.setOnClickListener(v -> finish());

        linearLayoutManager = new LinearLayoutManager(this);
        binding.zclRlLgRv.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerViewAdapter_Denator<>(this, 6);
        binding.zclRlLgRv.setAdapter(mAdapter);

        mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
        mAdapter.setListData(mListData, 2);//类型
        mAdapter.notifyDataSetChanged();
        initAutoComplete("history_projectName", binding.downAtProjectName);
        getUserMessage();

        for (int i = 0; i < mListData.size(); i++) {
            list_uid.add(mListData.get(i).getShellBlastNo());
        }

        mHandler_loding = new Handler(message -> {
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });
        mHandler_tip = new Handler(msg -> {
            switch (msg.what){
                case 1:
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
                    mAdapter.setListData(mListData, 2);//类型
                    mAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    break;
            }
            return false;
        });
    }
    //获取用户信息
    private void getUserMessage() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<Project> projects = master.queryProjectIsSelected("true");
        if(projects.size()>0){
            pro_name=projects.get(0).getProject_name();
            pro_bprysfz=projects.get(0).getBprysfz();
            pro_coordxy=projects.get(0).getCoordxy();
            pro_dwdm=projects.get(0).getDwdm();
            pro_htid=projects.get(0).getHtbh();
            pro_xmbh=projects.get(0).getXmbh();
        }
        MessageBean messages = master.getAllFromInfo_bean();
        equ_no=messages.getEqu_no();
        binding.downAtProjectName.setText(pro_name);
    }

    @Override
    protected void onPause() {
        mAdapter.notifyDataSetChanged();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.dp_btn_xiazai){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("下载提示")//设置对话框的标题//"成功起爆"
                    .setMessage("请确认项目编号,地理位置等信息输入无误后,点击确认下载")//设置对话框的内容"本次任务成功起爆！"
                    //设置对话框的按钮
                    .setNegativeButton("再次确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确认下载", (dialog1, which) -> {
                        dialog1.dismiss();
                        if (checkMessage()) {//校验输入的项目信息是否和法
                            upload();
                        } else {
                            return;
                        }
                    }).create();
            dialog.show();
        }
    }

    private boolean checkMessage() {
        if (pro_coordxy==null) {
            Log.e("长度", "" + list_uid.size());
            show_Toast("当前项目还未定位,请先进行定位");
            return false;
        }
        String sfz = pro_bprysfz.trim().replace(" ", "");//证件号码
        String tx_htid = pro_htid.trim().replace(" ", "");//合同编号 15位
        String tv_xmbh = pro_xmbh.trim().replace(" ", "");//项目编号
        String xy[] = pro_coordxy.replace("\n", "").replace("，", ",").replace(" ", "").split(",");//经纬度
        String tv_dwdm = pro_dwdm.trim();//单位代码 13位
        if (list_uid.size() < 1) {
            Log.e("长度", "" + list_uid.size());
            show_Toast("当前雷管为空,请先注册雷管");
            return false;
        }
        if (equ_no.length() < 1) {
            show_Toast("当前设备编号为空,请先设置设备编号");
            return false;
        }
        if (pro_coordxy.trim().length() < 1) {
            show_Toast("经纬度不能为空!");
            return false;
        }
        if (sfz.length() < 18) {
            show_Toast("人员证号格式不对!");
            return false;
        }
        if (!pro_coordxy.trim().contains(",")) {
            show_Toast("经纬度格式不对");
            return false;
        }
        if (pro_coordxy.trim().contains("4.9E-")) {
            show_Toast("经纬度格式不对，请按照例如116.38,39.90格式输入");
            return false;
        }
        if (StringUtils.isBlank(tx_htid) && StringUtils.isBlank(tv_xmbh) && StringUtils.isBlank(tv_dwdm)) {
            show_Toast("合同编号,项目编号,单位代码不能同时为空");
            return false;
        }
        if (tx_htid.length() != 0 && tx_htid.length() < 15) {
            Log.e("验证", "tx_htid.length(): " + tx_htid.length());
            Log.e("验证", "tx_htid: " + tx_htid);
            show_Toast("合同编号小于15位,请重新核对");
            return false;
        }
        return true;
    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(DownProjectActivity.this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
        divider.setBackgroundColor(Color.TRANSPARENT);
        //tipDlg.setMessage("正在操作,请等待...").show();

        new Thread(() -> {

            //mHandler_2
            mHandler_loding.sendMessage(mHandler_loding.obtainMessage());
            //builder.show();
            try {
                while (pb_show == 1) {
                    Thread.sleep(100);
                }
                //builder.dismiss();
                mHandler_loding.sendMessage(mHandler_loding.obtainMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void upload() {
        pb_show = 1;
//        runPbDialog();//loading画面
        final String key = "jadl12345678912345678912";
        String url = Utils.httpurl_down_dl;//丹灵下载
//        String url = Utils.httpurl_down;//丹灵下载
        OkHttpClient client = new OkHttpClient();

        JSONObject object = new JSONObject();
        String sfz = pro_bprysfz.replace(" ", "");//证件号码
        String tx_htid = pro_htid.trim().replace(" ", "");//合同编号 15位
        String tv_xmbh = pro_xmbh.trim().replace(" ", "");//项目编号
        Logger.e("地理位置"+pro_coordxy);
        final String xy[] = pro_coordxy.replace("\n", "").replace("，", ",").replace(" ", "").split(",");//经纬度
        String tv_dwdm = pro_dwdm.trim();//单位代码 13位

        //四川转换规则
        if (list_uid != null && list_uid.get(0).length() < 14) {
            for (int i = 0; i < list_uid.size(); i++) {
                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan(list_uid.get(i)));//替换
//                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan_new(list_uid.get(i)));//替换
            }
        }
        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e("uid", uid);
        try {
            object.put("sbbh", equ_no);//起爆器设备编号XBTS0003
            object.put("jd", xy[0]);//经度
            object.put("wd", xy[1]);//纬度
            object.put("uid", uid);//雷管uid
            object.put("xmbh", tv_xmbh);//项目编号370101318060006
            object.put("htid", tx_htid);//合同编号370100X15040027
            object.put("dwdm", tv_dwdm);//单位代码
            Log.e("上传信息", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = MyUtils.getBase64(MyUtils.encryptMode(key.getBytes(), object.toString().getBytes()));
        RequestBody requestBody = new FormBody.Builder()
                .add("param", json.replace("\n", ""))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "text/plain")//text/plain  application/json  application/x-www-form-urlencoded
                .build();
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pb_show = 0;
                show_Toast_ui("网络请求失败,请检查网络后再次尝试");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                pb_show = 0;
                String res;
                try {
                    res = new String(MyUtils.decryptMode(key.getBytes(), Base64.decode(response.body().string().toString(), Base64.DEFAULT)));
                } catch (Exception e) {
                    show_Toast_ui("丹灵系统异常，请与丹灵管理员联系后再尝试下载");
                    return;
                }
                Log.e("网络请求", "res: " + res);
                Gson gson = new Gson();
                DanLingBean danLingBean = gson.fromJson(res, DanLingBean.class);
                try {
                    JSONObject object1 = new JSONObject(res);
                    String cwxx = object1.getString("cwxx");
                    if (cwxx.equals("0")) {
                        int err = 0;
                        for (int i = 0; i < danLingBean.getLgs().getLg().size(); i++) {
                            if (!danLingBean.getLgs().getLg().get(i).getGzmcwxx().equals("0")) {
                                err++;
                            }
                            updataLeiGuan(mListData.get(i),danLingBean.getLgs().getLg().get(i).getYxq(),danLingBean.getLgs().getLg().get(i).getGzmcwxx());
                        }
                        Log.e("下载的雷管", "错误数量: " + err);
                        if (danLingBean.getCwxx().equals("0")) {
                            if (danLingBean.getZbqys().getZbqy().size() > 0) {
                                double zbqyjd = Double.parseDouble(xy[0]);//116.456535
                                double zbqywd = Double.parseDouble(xy[1]);//37.427541
                                for (int i = 0; i < danLingBean.getZbqys().getZbqy().size(); i++) {
                                    double jingdu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqyjd());
                                    double weidu = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqywd());
                                    double banjing = Double.parseDouble(danLingBean.getZbqys().getZbqy().get(i).getZbqybj());
                                    //判断经纬度
                                    LngLat start = new LngLat(zbqyjd, zbqywd);
                                    LngLat end = new LngLat(jingdu, weidu);
                                    double juli3 = AMapUtils.calculateLineDistance(start, end);
                                    Log.e("经纬度", "juli3: "+juli3 );
                                    if (juli3 < banjing) {
                                        insertJson(pro_htid, pro_xmbh, res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
//                                        insertJson_new(at_htid.getText().toString().trim(), at_xmbh.getText().toString().trim(), res, err, (danLingBean.getZbqys().getZbqy().get(i).getZbqyjd() + "," + danLingBean.getZbqys().getZbqy().get(i).getZbqywd()), danLingBean.getZbqys().getZbqy().get(i).getZbqymc());
                                    }
                                }
                            }
                        }
//                        mHandler_httpresult.sendMessage(mHandler_httpresult.obtainMessage());//刷新数据
                        if (err != 0) {
                            Log.e("下载", "err: " + err);
//                            show_Toast_ui(danLingBean.getZbqys().getZbqy().get(0).getZbqymc() + "下载的雷管出现错误,请检查数据");
                        }
                        show_Toast_ui("项目下载成功");

                        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
                    } else if (cwxx.equals("1")) {
                        show_Toast_ui(object1.getString("cwxxms"));
                    } else if (cwxx.equals("2")) {
                        show_Toast_ui("未找到该起爆器设备信息或起爆器未设置作业任务");
                    } else if (cwxx.equals("3")) {
                        show_Toast_ui("该起爆器未设置作业任务");
                    } else if (cwxx.equals("4")) {
                        show_Toast_ui("起爆器在黑名单中");
                    } else if (cwxx.equals("5")) {
                        show_Toast_ui("起爆位置不在起爆区域内 ");
                    } else if (cwxx.equals("6")) {
                        show_Toast_ui("起爆位置在禁爆区域内");
                    } else if (cwxx.equals("7")) {
                        show_Toast_ui("该起爆器已注销/报废");
                    } else if (cwxx.equals("8")) {
                        show_Toast_ui("禁爆任务");
                    } else if (cwxx.equals("9")) {
                        show_Toast_ui("作业合同存在项目");
                    } else if (cwxx.equals("10")) {
                        show_Toast_ui("作业任务未设置准爆区域");
                    } else if (cwxx.equals("11")) {
                        show_Toast_ui("离线下载不支持生产厂家试爆");
                    } else if (cwxx.equals("12")) {
                        show_Toast_ui("营业性单位必须设置合同或者项目");
                    } else if (cwxx.equals("99")) {
                        show_Toast_ui(danLingBean.getCwxxms());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 向数据库中插入数据
     */
    public void insertJson(String htbh, String xmbh, String json, int errNum, String coordxy, String name) {
        ShouQuan shouQuan = new ShouQuan();
        shouQuan.setHtbh( htbh);
        shouQuan.setXmbh(xmbh);
        shouQuan.setJson(json);
        shouQuan.setErrNum(errNum+"");
        shouQuan.setQbzt("未爆破");
        shouQuan.setDl_state("未上传");
        shouQuan.setZb_state("未上传");
        shouQuan.setSpare1(name);
        shouQuan.setBprysfz(pro_bprysfz);//身份证号
        shouQuan.setCoordxy(coordxy);
        if (pro_dwdm.length() < 1) {//单位代码
            shouQuan.setDwdm("");
        } else {
            shouQuan.setDwdm("pro_dwdm");
        }
        Application.getDaoSession().getShouQuanDao().insert(shouQuan);
        Log.e("插入数据", "成功");
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    //更新雷管状态
    public void updataLeiGuan(DenatorBaseinfo denatorBaseinfo, String authorization,String downloadStatus) {
        Logger.e("shellBlastNo:"+denatorBaseinfo.getShellBlastNo()+",authorization:"+authorization+",downloadStatus:"+downloadStatus);
        denatorBaseinfo.setDownloadStatus(downloadStatus);
        denatorBaseinfo.setAuthorization(authorization);
        Application.getDaoSession().getDenatorBaseinfoDao().update(denatorBaseinfo);
    }
}