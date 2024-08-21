package android_serialport_api.xingbang.services;

import static android_serialport_api.xingbang.Application.getContext;
import static android_serialport_api.xingbang.Application.getDaoSession;
import static android_serialport_api.xingbang.Application.getDb;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_MainDao;
import android_serialport_api.xingbang.jianlian.FirstEvent;
import android_serialport_api.xingbang.models.VoFireHisMain;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.MyUtils;
import android_serialport_api.xingbang.utils.NetUtils;
import android_serialport_api.xingbang.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 闲时上传调度器
 */
public class UploadWorker extends Worker {
    private String TAG = "闲时上传UploadWorker--";
    private ArrayList<Map<String, Object>> hisListData = new ArrayList<>();//错误雷管
    private List<VoFireHisMain> list_savedate = new ArrayList<>();
    private int currentPage = 1;//当前页数
    private int totalNum;//总的数据条数
    private int pageSize = 600;//每页显示的数据
    private int totalPage;//总的页数
    private String equ_no = "";//设备编码
    private String pro_bprysfz = "";//证件号码
    private String pro_htid = "";//合同号码
    private String pro_xmbh = "";//项目编号
    private String pro_coordxy = "";//经纬度
    private String pro_dwdm = "";//单位代码
    private String server_addr = "";
    private String server_port = "";
    private String server_http = "";
    private String server_ip = "";
    private String server_type1 = "";
    private String server_type2 = "";
    private int uploadIndex = 0;//还有多少条数据需要一键上传
    List<String> dateList = new ArrayList<>();
    private int isDlUploadSuccess = 0;//丹灵是否上传成功 0:未上传  200:上传成功  201:上传失败
    private int isZbUploadSuccess = 0;//中爆是否上传成功 0:未上传  200:上传成功  201:上传失败
    private int isXbUploadSuccess = 0;//煋邦是否上传成功 0:未上传  200:上传成功  201:上传失败

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        getUserMessage();//获取用户信息
        totalNum = getDaoSession().getDenatorHis_DetailDao().loadAll().size();//得到数据的总条数
        totalPage = (int) Math.ceil(totalNum / (float) pageSize);//通过计算得到总的页数
        if (1 == currentPage) {
            loadMoreData(currentPage);//读取数据
        }
        /**
         *  闲时上传这里   先查询出所有未上传的数据  然后再使用递归方式上传
         *  递归：第一条上传不管成功失败，都上传下一条，直至数据全部上传完毕
         */
        long currentTimeMillis = System.currentTimeMillis();
        long threeDaysAgoMillis = currentTimeMillis - 3 * 24 * 60 * 60 * 1000; // 3天前的时间戳

        // 查询近三天的数据
//        QueryBuilder<MyEntity> queryBuilder = myEntityDao.queryBuilder();
//        queryBuilder.where(MyEntityDao.Properties.Timestamp.ge(threeDaysAgoMillis));
        List<DenatorHis_Main> list = getDaoSession().getDenatorHis_MainDao().queryBuilder()
//                .where(DenatorHis_MainDao.Properties.Blastdate.between())
                .orderDesc(DenatorHis_MainDao.Properties.Id).list();
        for (DenatorHis_Main his:list) {
            if(his.getUploadStatus().equals("未上传")){
                dateList.add(his.getBlastdate());
            }
        }
        if (dateList.size() > 0) {
            Log.e(TAG,"未上传的date集合:" + dateList.toString());
            uploadNext(dateList,uploadIndex);
        } else {
            Log.e(TAG,"当前没有需要上传的数据");
        }
        getBlastModelCount();
        return Result.success();
    }

    // 递归方法，逐个上传数据
    private void uploadNext(List<String> dateList, int index) {
        if (index >= dateList.size()) {
            Log.e(TAG,"闲时上传已结束,需要上传的起爆信息总数为:" + dateList.size());
            Utils.writeLog("闲时上传已结束,需要上传的起爆信息总数为:" + dateList.size());
            return;
        }
        Log.e(TAG,"isDlUploadSuccess:" + isDlUploadSuccess + "--isXbUploadSuccess:" + isXbUploadSuccess);
        String data = dateList.get(index);
        for (int i = 0; i < list_savedate.size(); i++) {
            if (data.equals(list_savedate.get(i).getBlastdate())) {
                Log.e(TAG,"闲时上传的数据下标是:" + i + "--日期是:" + list_savedate.get(i).getBlastdate());
                uploadQbData(i);
            }
        }
    }

    private void getUserMessage() {
        MessageBean bean = GreenDaoMaster.getAllFromInfo_bean();
        pro_bprysfz = bean.getPro_bprysfz();
        pro_htid = bean.getPro_htid();
        pro_xmbh = bean.getPro_xmbh();
        equ_no = bean.getEqu_no();
        pro_coordxy = bean.getPro_coordxy();
        server_addr = bean.getServer_addr();
        server_port = bean.getServer_port();
        server_http = bean.getServer_http();
        server_ip = bean.getServer_ip();
        server_type1 = bean.getServer_type1();
        server_type2 = bean.getServer_type2();
        pro_dwdm = bean.getPro_dwdm();
        Log.e(TAG,"数据库存储的经纬度:" + (!TextUtils.isEmpty(pro_coordxy) ? pro_coordxy : "40°N,116°E")
                + "--server_type1:" + server_type1 + "--server_type2:" + server_type2);
    }

    private void loadMoreData(int currentPage) {
        list_savedate.clear();
        getDaoSession().clear();
        List<DenatorHis_Main> list = getDaoSession().getDenatorHis_MainDao().queryBuilder().orderDesc(DenatorHis_MainDao.Properties.Id).list();
        for (DenatorHis_Main hisMain : list) {
            String date = hisMain.getBlastdate();
            getHisDetailList(date,0);
            int count = hisListData.size();
            if (count > 0) count -= 1;
            Log.e(TAG,"已爆雷管个数:" + count);
            VoFireHisMain item = new VoFireHisMain();
            item.setBlastdate(hisMain.getBlastdate());
            item.setFiredNo(hisMain.getEqu_no());
            item.setLatitude(hisMain.getLatitude());
            item.setLongitude(hisMain.getLongitude());
            item.setRemark(hisMain.getRemark());
            item.setSerialNo(hisMain.getSerialNo() + "");
            item.setUploadStatus(hisMain.getUploadStatus());
            item.setUserid(hisMain.getUserid());
            item.setId(hisMain.getId() + "");
            item.setProjectNo(hisMain.getPro_htid());
            item.setDwdm(hisMain.getPro_dwdm());
            item.setXmbh(hisMain.getPro_xmbh());
            item.setLog(hisMain.getLog());
            item.setTotal(count);
            list_savedate.add(item);
        }
    }

    private void uploadQbData(int position) {
        SQLiteDatabase db = getDb();
        if (!NetUtils.haveNetWork(getContext())) {
            EventBus.getDefault().post(new FirstEvent("spareUploadError"));
            Log.e(TAG,"当前无网络，闲时上传无法进行");
            Utils.writeLog("当前无网络，闲时上传无法进行");
            return;
        }
        int pos = position;//位置
        String blastdate = list_savedate.get(pos).getBlastdate();//日期
        String htbh = list_savedate.get(pos).getProjectNo();//合同编号
        String dwdm = list_savedate.get(pos).getDwdm();//单位代码
        String xmbh = list_savedate.get(pos).getXmbh();//项目编号
        String jd = list_savedate.get(pos).getLongitude();//经度
        String wd = list_savedate.get(pos).getLatitude();//纬度
        String qbxm_id = list_savedate.get(pos).getXmbh();//项目编号
        String qbxm_name = list_savedate.get(pos).getUserid();//项目名称
        String log = list_savedate.get(pos).getLog();//日志
        getHisDetailList(blastdate, 0);//获取起爆历史详细信息
        if (blastdate == null || blastdate.trim().length() < 8) {
            int count = getBlastModelCount();
            if (count < 1) {
                Log.e(TAG,"当前没有没有数据，不能执行上传");
                Utils.writeLog("当前没有没有数据，不能执行上传");
                return;
            }
            String fireDate = Utils.getDateFormatLong(new Date());
            saveFireResult(fireDate);
            blastdate = fireDate;
        }
        Log.e(TAG + "上传-经纬度", "pro_coordxy: " + pro_coordxy);
        Log.e(TAG + "上传-经纬度", "jd: " + jd);
        if (pro_coordxy.length() < 2 && (jd == null || wd == null)) {
            Log.e(TAG,"经纬度为空，不能执行上传");
            Utils.writeLog("经纬度为空，不能执行上传");
            return;
        }
        if (server_type2.equals("0") && server_type1.equals("0")) {
            Log.e(TAG,"设备当前未设置上传网址,请先设置上传网址");
            Utils.writeLog("设备当前未设置上传网址,请先设置上传网址");
        }
        if (server_type2.equals("2")) {
            performUp(blastdate, pos, htbh, jd, wd);//中爆上传
        }
        upload_xingbang(blastdate, pos, htbh, jd, wd, xmbh, dwdm, qbxm_name, log);//我们自己的网址
    }

    /**
     * 更新上传状态
     *
     * @param fireDateId
     */
    private void performUp(final String fireDateId, final int pos, final String htid, final String jd, final String wd) {
        new Thread(() -> {
            Looper.prepare();
            if (updata(fireDateId, htid, jd, wd).equals("0")) {//updata是上传中爆网方法成功返回0
                modifyUploadStatus(fireDateId, getContext().getString(R.string.text_query_uploaded));//
                updataState_sq_zb(fireDateId, pos);

                if (list_savedate != null && list_savedate.size() > 0) {
                    for (int i = list_savedate.size() - 1; i >= 0; i--) {
                        VoFireHisMain vo = list_savedate.get(i);
                        if (fireDateId.equals(vo.getBlastdate())) {
                            vo.setUploadStatus(getContext().getString(R.string.text_query_uploaded));
                        }
                    }
                }
            }
            Looper.loop();
        }).start();
    }

    /**
     * 更新中爆网上传信息状态
     */
    public void updataState_sq_zb(String blastdate, int pos) {
        Log.e(TAG + "更新起爆状态-中爆", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("zb_state", "已上传");
        getDb().update(DatabaseHelper.TABLE_NAME_SHOUQUAN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把软存中的数据存入磁盘中
        Log.e(TAG,"更新中爆网上传状态");
        updateSqlDataStatus(blastdate,pos);
    }

    public int getIsDlUploadSuccess() {
        return isDlUploadSuccess;
    }

    public void setIsDlUploadSuccess(int isDlUploadSuccess) {
        this.isDlUploadSuccess = isDlUploadSuccess;
    }

    public int getIsZbUploadSuccess() {
        return isZbUploadSuccess;
    }

    public void setIsZbUploadSuccess(int isZbUploadSuccess) {
        this.isZbUploadSuccess = isZbUploadSuccess;
    }

    public int getIsXbUploadSuccess() {
        return isXbUploadSuccess;
    }

    public void setIsXbUploadSuccess(int isXbUploadSuccess) {
        this.isXbUploadSuccess = isXbUploadSuccess;
    }

    private void updateSqlDataStatus(String blastdate, int pos){
        updataState(blastdate + "");//更新上传状态
        updataState_sq_dl(blastdate + "");
        isDlUploadSuccess = getIsDlUploadSuccess();
        isXbUploadSuccess = getIsXbUploadSuccess();
        Log.e(TAG,"上传结果已返回isDlUploadSuccess:" + isDlUploadSuccess +
                "--isXbUploadSuccess:" + isXbUploadSuccess);
        if (isDlUploadSuccess != 0 || isXbUploadSuccess != 0) {
            uploadIndex ++ ;
            uploadNext(dateList,uploadIndex);
        }
    }

    /**
     * 更新丹灵网上传信息状态
     */
    public void updataState_sq_dl(String blastdate) {
        Log.e("更新起爆状态-丹灵", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("dl_state", "已上传");
        getDb().update(DatabaseHelper.TABLE_NAME_SHOUQUAN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    /**
     * 更新上传信息状态
     */
    public void updataState(String blastdate) {
        Log.e("更新起爆状态", "id: " + blastdate);
        ContentValues values = new ContentValues();
        values.put("uploadStatus", "已上传");
        getDb().update(DatabaseHelper.TABLE_NAME_HISMAIN, values, "blastdate=?", new String[]{"" + blastdate});
        Utils.saveFile();//把闪存中的数据存入磁盘中
    }

    
    /**
     * 更新历史记录的上传信息状态
     */
    public int modifyUploadStatus(String id, String uploadStatus) {
        ContentValues values = new ContentValues();
        values.put("uploadStatus", uploadStatus);
        getDb().update(DatabaseHelper.TABLE_NAME_HISMAIN, values, "blastdate=?", new String[]{"" + id});
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }
    
    private String updata(String blastdate, String htid, final String jd, final String wd) {
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 1; i < hisListData.size(); i++) {
            list_uid.add(hisListData.get(i).get("shellNo") + "O");//中爆网雷管以O为分割(英文O)
        }
        if (htid == null) {
            htid = pro_htid;
        }
        if (equ_no.length() > 8) {//中爆网起爆器编号8位
//            String qbq_no = equ_no.substring(0, 5) + equ_no.substring(8);//截取起爆器编号前5位和后3位
            String qbq_no = equ_no.substring(1, 3) + equ_no.substring(5);//截取起爆器编号前23位和后6位
            return Utils.uploadFireData(getContext(), list_uid, pro_bprysfz, htid, pro_xmbh, (jd + "," + wd), server_type2, qbq_no, server_ip, server_port, server_http, blastdate);
        }
        return Utils.uploadFireData(getContext(), list_uid, pro_bprysfz, htid, pro_xmbh, (jd + "," + wd), server_type2, equ_no, server_ip, server_port, server_http, blastdate);

    }

    private void upload_xingbang(final String blastdate, final int pos, final String htid, final String jd, final String wd, final String xmbh, final String dwdm, final String qbxm_name, final String log) {
        final String key = "jadl12345678912345678912";
//        String url = "http://xbmonitor.xingbangtech.com/XB/DataUpload";//公司服务器上传
//        String url = "http://xbmonitor.xingbangtech.com:800/XB/DataUpload";//公司服务器上传
//        String url = "http://xbmonitor1.xingbangtech.com:800/XB/DataUpload";//新;//公司服务器上传
//        String url = "http://111.194.155.18:999/XB/DataUpload";//测试
        String url = "http://xbmonitor1.xingbangtech.com:666/XB/DataUpload";//新;//公司服务器上传

        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 1; i < hisListData.size(); i++) {
            list_uid.add(hisListData.get(i).get("shellNo").toString() + "#" + hisListData.get(i).get("delay") + "#" + hisListData.get(i).get("errorName"));
        }
        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e(TAG + "煋邦上传uid", uid);
        String xy[] = pro_coordxy.split(",");//经纬度
        String app_version_name = getContext().getString(R.string.app_version_name);
        try {
            object.put("sbbh", equ_no);//起爆器设备编号
            if (jd != null) {
                object.put("jd", jd);//经度
            } else if (pro_coordxy.length() > 5) {
                object.put("jd", xy[0]);//经度
            }
            if (wd != null) {
                object.put("wd", wd);//纬度
            } else if (pro_coordxy.length() > 5) {
                object.put("wd", xy[1]);//纬度
            }
            if (htid != null) {
                object.put("htid", htid);//合同编号
            } else {
                object.put("htid", pro_htid);//合同编号
            }
            object.put("bpsj", blastdate.replace("/", "-").replace(",", " "));//爆破时间blastdate.replace("/","-").replace(","," ")
            object.put("bprysfz", pro_bprysfz);//人员身份证
            object.put("uid", uid);//雷管uid
            object.put("dwdm", pro_dwdm);//单位代码
            object.put("xmbh", pro_xmbh);//项目编号
            object.put("log", log);//日志
            object.put("log_cmd", Utils.readLog_cmd(blastdate.split(" ")[0].replace("/", "-")));//日志
            object.put("yj_version", MmkvUtils.getcode("yj_version", "KT50_V1.3_17V_V1.3.18.bin"));//硬件版本
            PackageInfo pi = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            object.put("rj_version", app_version_name);//软件版本
            object.put("name", qbxm_name);//项目名称
            Log.e(TAG + "煋邦上传信息-项目名称", qbxm_name);
        } catch (JSONException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //3des加密
        String json = MyUtils.getBase64(MyUtils.encryptMode(key.getBytes(), object.toString().getBytes()));
        JSONObject object2 = new JSONObject();
        try {
            object2.put("param", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = FormBody.create(JSON, object2.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json; charset=utf-8")//text/plain  application/json  application/x-www-form-urlencoded
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG + "煋邦后台上传失败", "IOException: " + e);
                Utils.writeLog("煋邦网络上传错误-IOException:" + e);
                setIsXbUploadSuccess(201);
                updatalog(blastdate, "煋邦网络上传错误-IOException:" + e);
                updateSqlDataStatus(blastdate,pos);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG + "煋邦上传成功", "返回: " + response.toString());
                Utils.writeLog("煋邦网络上传成功-IOException:" + response.toString());
                setIsXbUploadSuccess(200);
                updateSqlDataStatus(blastdate,pos);
            }
        });
    }


    /**
     * 丹灵上传方法
     */
    private void upload(final String blastdate, final int pos, final String htid, final String jd, final String wd, final String xmbh, final String dwdm) {
        final String key = "jadl12345678912345678912";
        String url = Utils.httpurl_upload_test;//丹灵上传
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();
        ArrayList<String> list_uid = new ArrayList<>();
        for (int i = 1; i < hisListData.size(); i++) {
            list_uid.add(hisListData.get(i).get("shellNo") + "");
        }

        //四川uid转换规则
//        if (list_uid.get(0).length() < 14) {
//            for (int i = 0; i < list_uid.size(); i++) {
//                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan(list_uid.get(i)));//替换
////                    Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toSiChuan_new(list_uid.get(i)));//替换
//            }
//        }
        //丹灵新uid转换规则
//        if (list_uid != null && list_uid.get(0).length() < 14) {
//            for (int i = 0; i < list_uid.size(); i++) {
//                Collections.replaceAll(list_uid, list_uid.get(i), Utils.ShellNo13toNewddanling(list_uid.get(i)));//替换
//            }
//        }

        String uid = list_uid.toString().replace("[", "").replace("]", "").replace(" ", "").trim();
        Log.e(TAG + "丹灵上传uid", uid);
        String xy[] = pro_coordxy.split(",");//经纬度
        try {
            object.put("sbbh", equ_no);//起爆器设备编号
            if (jd != null) {
                object.put("jd", jd);//经度
            } else {
                object.put("jd", xy[0]);//经度
            }
            if (wd != null) {
                object.put("wd", wd);//纬度
            } else {
                object.put("wd", xy[1]);//纬度
            }
            if (htid != null) {
                object.put("htid", htid);//合同编号
            } else {
                object.put("htid", pro_htid);//合同编号
            }
            object.put("bpsj", blastdate.replace("/", "-").replace(",", " "));//爆破时间blastdate.replace("/","-").replace(","," ")
            object.put("bprysfz", pro_bprysfz);//人员身份证
            object.put("uid", uid);//雷管uid
            object.put("dwdm", pro_dwdm);//单位代码
            object.put("xmbh", pro_xmbh);//项目编号
            Log.e(TAG + "丹灵上传信息", object.toString());
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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                setIsDlUploadSuccess(201);
                Log.e(TAG + "丹灵上传失败", "网络请求失败,请检查网络正确连接后,再次上传--IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject object;
                try {
                    object = new JSONObject(response.body().string());
                    String success = object.getString("success");
                    if (success.equals("true")) {
                        setIsDlUploadSuccess(200);
                        Log.e(TAG + "丹灵上传成功", "丹灵返回: " + object.toString());
                        updateSqlDataStatus(blastdate,pos);
                    } else if (success.equals("fail")) {
                        setIsDlUploadSuccess(201);
                        String cwxx = object.getString("cwxx");
                        if (cwxx.equals("2")) {
                            Log.e(TAG + "丹灵上传失败", " 上传失败，起爆器未备案或未设置作业任务");
                        } else {
                            Log.e(TAG + "丹灵上传失败", "丹灵返回: " + object.toString() + "--错误信息:" + cwxx);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 添加错误日志
     */
    private void updatalog(String blastdate, String err) {
        GreenDaoMaster master = new GreenDaoMaster();
        DenatorHis_Main his_main = master.queryDetonatorForMainHis(blastdate);
        his_main.setLog(his_main.getLog() + "\n" + err);
        getDaoSession().getDenatorHis_MainDao().update(his_main);
    }

    /**
     * 保存起爆结果
     */
    public synchronized void saveFireResult(String fireDate) {
        SQLiteDatabase db = getDb();
        Cursor cursor = db.rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO, null);
        int totalNum = cursor.getCount();//得到数据的总条数
        if (totalNum < 1) return;

        ContentValues values = new ContentValues();
        int maxNo = getHisMaxNumberNo();
        maxNo++;

        //hisInsertFireDate = fireDate;

        values.put("blastdate", fireDate);
        values.put("uploadStatus", "0");
        values.put("longitude", "0");
        values.put("latitude", "0");
        values.put("remark", "");
        values.put("userid", pro_bprysfz);
        values.put("equ_no", equ_no);
        values.put("serialNo", "" + maxNo);
        db.insert("denatorHis_Main", null, values);
        VoFireHisMain item = new VoFireHisMain();
        item.setBlastdate(fireDate);
        item.setFiredNo(equ_no);
        item.setLatitude("0");
        item.setLongitude("0");
        item.setRemark("");
        item.setSerialNo(maxNo + "");
        item.setUploadStatus(getContext().getString(R.string.text_query_up));//"未上传"
        item.setUserid(pro_bprysfz);
        item.setId("0");
        list_savedate.add(item);
        cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null, null, null, null, " blastserial asc");
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                values.clear();
                values.put("blastserial", cursor.getInt(1));
                values.put("sithole", cursor.getInt(2));
                values.put("shellBlastNo", cursor.getString(3));
                values.put("denatorId", cursor.getString(4));
                values.put("delay", cursor.getInt(5));
                values.put("statusCode", cursor.getString(6));
                values.put("statusName", cursor.getString(7));
                values.put("errorName", cursor.getString(8));
                values.put("errorCode", cursor.getString(9));
                values.put("authorization", cursor.getString(10));
                values.put("remark", cursor.getString(11));
                values.put("blastdate", fireDate);
                db.insert("denatorHis_Detail", null, values);
            }
            cursor.close();
        }
        Utils.saveFile();//把闪存中的数据存入磁盘中
        // db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO,null,null);
    }

    /**
     * 获取最大序号
     */
    private int getHisMaxNumberNo() {
        Cursor cursor = getDb().rawQuery("select max(serialNo) from " + DatabaseHelper.TABLE_NAME_HISMAIN, null);
        if (cursor != null && cursor.moveToNext()) {
            String maxStr = cursor.getString(0);
            int maxNo = 0;
            if (maxStr != null && maxStr.trim().length() > 0) {
                maxNo = Integer.parseInt(maxStr);
            }
            cursor.close();
            return maxNo;
        }
        return 1;

    }
    
    /**
     * 获取起爆历史详细信息
     */
    private void getHisDetailList(String blastdate, int type) {
        SQLiteDatabase db = getDb();
        hisListData.clear();
        Map<String, Object> item = new HashMap<>();
        item.put("no", getContext().getString(R.string.text_list_Serial));//"序号"
        item.put("serialNo", getContext().getString(R.string.text_list_Serial));//"序号"
        item.put("shellNo", getContext().getString(R.string.text_list_guan));//"管壳码"
        item.put("delay", "" + getContext().getString(R.string.text_list_delay));//"延时"
        item.put("errorName", getContext().getString(R.string.text_list_state));//"状态"
        hisListData.add(item);
        Cursor cursor;
        int a = 1;
        if (type == 1) {
            cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null, null, null, null, null);
        } else {
            String selection = "blastdate = ?and errorCode  like ? "; // 选择条件，给null查询所有//+" and errorCode = ?"   new String[]{"FF"}
            String[] selectionArgs = {blastdate, "F%"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
            cursor = db.query(DatabaseHelper.TABLE_NAME_HISDETAIL, null, selection, selectionArgs, null, null, null);
        }
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                int serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                String shellNo = cursor.getString(3);//管壳号
                String errorName = cursor.getString(8);//错误信息
                int delay = cursor.getInt(5); //延时
                item = new HashMap<>();
                item.put("no", a);
                item.put("serialNo", serialNo);
                item.put("shellNo", shellNo);
                item.put("delay", "" + delay);
                if (errorName == null || errorName.trim().length() < 1) errorName = " ";
                item.put("errorName", errorName);
                hisListData.add(item);
                a++;
            }
            cursor.close();
        }
        Utils.saveFile();//把闪存中的数据存入磁盘中
    }
    
    /***
     * 加载雷管信息
     */
    private int getBlastModelCount() {
        Cursor cursor = getDb().rawQuery(DatabaseHelper.SELECT_ALL_DENATOBASEINFO, null);
        int totalNum = cursor.getCount();//得到数据的总条数
        //cursor不位空,可以移动到第一行
        cursor.close();
        return totalNum;
    }
}
