package android_serialport_api.xingbang.db;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.content.Context;
import android.database.Cursor;
import android.os.Message;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.db.greenDao.DefactoryDao;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_DetailDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_MainDao;
import android_serialport_api.xingbang.db.greenDao.Denator_typeDao;
import android_serialport_api.xingbang.db.greenDao.DetonatorTypeNewDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.db.greenDao.ShouQuanDao;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.DanLingOffLinBean;

/**
 * 管理GreenDao查询语句
 */
public class GreenDaoMaster {

    private DefactoryDao mDefactoryDao;
    private DenatorBaseinfoDao mDeantorBaseDao;
    private ProjectDao mProjectDao;
    private Denator_typeDao mDenatorType;
    private MessageBeanDao messageBeanDao;
    private DetonatorTypeNewDao detonatorTypeNewDao;
    private ShouQuanDao mShouquanDao;
    private DenatorHis_MainDao denatorHis_mainDao;

    public GreenDaoMaster() {
        this.mDefactoryDao = Application.getDaoSession().getDefactoryDao();
        this.detonatorTypeNewDao = Application.getDaoSession().getDetonatorTypeNewDao();
        this.mDeantorBaseDao = Application.getDaoSession().getDenatorBaseinfoDao();
        this.mProjectDao = Application.getDaoSession().getProjectDao();
        this.mDenatorType = Application.getDaoSession().getDenator_typeDao();
        this.messageBeanDao = Application.getDaoSession().getMessageBeanDao();
        this.mShouquanDao = Application.getDaoSession().getShouQuanDao();
        this.denatorHis_mainDao = Application.getDaoSession().getDenatorHis_MainDao();
    }

    public List<Defactory> queryDefactoryToIsSelected(String selected) {
        QueryBuilder<Defactory> result = mDefactoryDao.queryBuilder();
        result = result.where(DefactoryDao.Properties.IsSelected.eq(selected));
        return result.list();
    }
    public List<Project> queryProjectIsSelected(String selected) {
        QueryBuilder<Project> result = mProjectDao.queryBuilder();
        result = result.where(ProjectDao.Properties.Selected.eq(selected));
        return result.list();
    }

    public List<Defactory> queryDefactoryToDeEntCode(String deEntCode) {
        QueryBuilder<Defactory> result = mDefactoryDao.queryBuilder();
        result = result.where(DefactoryDao.Properties.DeEntCode.eq(deEntCode));
        return result.list();
    }

    public List<DenatorBaseinfo> queryDenatorBaseinfoToShellBlastNoe(String shellBlastNo) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result = result.where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(shellBlastNo));
        return result.list();
    }

    /**
     * 查询正确雷管
     */
    public List<DenatorBaseinfo> queryDenatorBaseinfoToStatusCode(String statusCode) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        return result.where(DenatorBaseinfoDao.Properties.StatusCode.eq(statusCode)).list();
    }

    public List<DenatorBaseinfo> queryDenatorBaseinfo() {
        return mDeantorBaseDao.loadAll();
    }

    public List<Project> queryProjectToProject_name(String project_name) {
        QueryBuilder<Project> result = mProjectDao.queryBuilder();
        result = result.where(ProjectDao.Properties.Project_name.eq(project_name));
        return result.list();
    }
    public void updateProject(Project project){
        mProjectDao.update(project);
    }

    public List<Denator_type> queryDefactoryTypeToIsSelected(String selected) {
        QueryBuilder<Denator_type> result = mDenatorType.queryBuilder();
        result = result.where(Denator_typeDao.Properties.IsSelected.eq(selected));
        return result.list();
    }

    /**
     * 查询错误雷管
     */
    public List<DenatorBaseinfo> queryErrLeiGuan() {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result = result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF"));
        return result.list();
    }

    /**
     * 删除错误代码不等于FF的所有雷管
     */
    public void deleteErrLeiGuan() {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF")).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public List<MessageBean> queryUsetMessgae() {
        QueryBuilder<MessageBean> result = messageBeanDao.queryBuilder();
        return result.where(MessageBeanDao.Properties.Id.eq(1)).list();
    }
    /**
     * 通过芯片码获取管壳码
     * */
    public String queryDetonatorTypeNew(String detonatorId) {
        Log.e("模糊查询", "detonatorId.substring(7): "+detonatorId.substring(7) );
        List<DetonatorTypeNew> dt =detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.DetonatorId.like("%"+detonatorId.substring(7))).list();
//        List<DetonatorTypeNew> dt =detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.DetonatorId.eq(detonatorId)).list();
        if(dt.size()>=1){
            return dt.get(0).getShellBlastNo();
        }else {
            return "0";
        }
    }
    /**
     * 通过管壳码获取芯片码
     * */
    public String queryShellBlastNoTypeNew2(String shellBlastNo) {
        List<DetonatorTypeNew> dt =detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(shellBlastNo)).list();
        if(dt.size()>=1){
            return dt.get(0).getDetonatorId();
        }else {
            return "0";
        }
    }
    /**
     * 通过管壳码获取芯片码
     */
    public DetonatorTypeNew queryShellBlastNoTypeNew(String shellBlastNo) {
        Log.e("通过管壳码获取芯片码", "shellBlastNo: "+shellBlastNo );
        List<DetonatorTypeNew> dt = detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(shellBlastNo)).list();
        Log.e("通过管壳码获取芯片码", "dt.size(): "+dt.size() );
        if (dt.size() >= 1) {
            return dt.get(0);
        } else {
            return null;
        }
    }

    /**
     * 查询授权信息(倒序)
     * */
    public List<ShouQuan> queryShouQuan(){
        QueryBuilder<ShouQuan> result = mShouquanDao.queryBuilder();
        return result.orderDesc(ShouQuanDao.Properties.Id).list();
    }
    /**
     * 查询授权信息(倒序)
     * */
    public List<Project> queryProject(){
        QueryBuilder<Project> result = mProjectDao.queryBuilder();
        return result.orderDesc(ProjectDao.Properties.Id).list();
    }

    /**
     * 通过芯片码获取管壳码
     *
     * @param detonatorId 芯片码
     */
    public String getShellNo(String detonatorId) {
        return detonatorTypeNewDao
                .queryBuilder()
                .where(DetonatorTypeNewDao.Properties.DetonatorId.eq(detonatorId))
                .unique()
                .getShellBlastNo();
    }

    /**
     * 查询注册列表中的重复芯片码
     * */
    public List<DenatorBaseinfo> checkRepeatdenatorId(String detonatorId){
//        return mDeantorBaseDao
//                .queryBuilder()
//                .where(DenatorBaseinfoDao.Properties.DenatorId.eq(detonatorId))
//                .list();
//        return mDeantorBaseDao
//                .queryBuilder()
//                .where(DenatorBaseinfoDao.Properties.DenatorId.like(detonatorId.substring(7)))
//                .list();//0209为李斌改的不用4字节的首字节进行的模糊查询

        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.DenatorId.like("%" + detonatorId))
                .list();//0209为李斌改的不用4字节的首字节进行的模糊查询
    }
    /**
     * 查询注册列表中的重复雷管
     * */
    public List<DenatorBaseinfo> checkRepeatShellNo(String ShellBlastNo){
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(ShellBlastNo))
                .list();
    }
    /**
     * 检查重复雷管
     */
    public DenatorBaseinfo checkRepeatShellNo_2(String shellBlastNo) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(shellBlastNo))
                .unique();
    }
    /**
     * 查询生产列表中的重复雷管
     * */
    public DetonatorTypeNew checkRepeat_DetonatorTypeNew(String ShellBlastNo){
        return detonatorTypeNewDao
                .queryBuilder()
                .where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(ShellBlastNo))
                .unique();
    }
    /**
     * 从数据库表中拿数据
     *
     * @return
     */
    public static MessageBean getAllFromInfo_bean() {
        return getDaoSession().getMessageBeanDao().loadAll().get(0);
    }

    public static String getAllFromInfo() {
        List<DenatorBaseinfo> list=getDaoSession().getDenatorBaseinfoDao().loadAll();
        String str ="ID,序号,孔号,管壳码,芯片码,延时,读取状态,状态名称,错误名称,错误代码,授权期限,备注,注册日期,桥丝状态,名称,从芯片码\n";
        String content ;
        for(int i=0;i<list.size();i++){
            content = list.get(i).getId() + "," + list.get(i).getBlastserial() + "," + list.get(i).getSithole() + ","
                    + list.get(i).getShellBlastNo() + "," +list.get(i).getDenatorId() + "," + list.get(i).getDelay() + ","
                    + list.get(i).getStatusCode() + "," + list.get(i).getStatusName() + "," + list.get(i).getErrorName()+ ","
                    + list.get(i).getErrorCode() + "," +list.get(i).getAuthorization()+ "," + list.get(i).getRemark() + ","
                    + list.get(i).getRegdate() + "," + list.get(i).getWire() + "," + list.get(i).getName() +"," +list.get(i).getDenatorIdSup()+ "\n";
            str = str + content;
        }
        return str;
    }

    /**
     * 查询雷管 区域倒序(序号)
     * 区域号 1 2 3 4 5
     */
    public List<DenatorBaseinfo> queryDetonatorRegionDesc() {
        return mDeantorBaseDao
                .queryBuilder()
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }

    /**
     * 获取第一发雷管
     */
    public static String serchFristLG() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryDenatorBaseinfo();
        if (list.size() > 0) {
            return list.get(0).getShellBlastNo();
        } else {
            return "";
        }
    }

    /**
     * 注册列表的第一发是否在历史列表里
     */
    public static int serchFristLGINdenatorHis(String no) {
        if (no.length() > 12) {
            return getDaoSession().getDenatorHis_DetailDao().queryBuilder().where(DenatorHis_DetailDao.Properties.ShellBlastNo.eq(no)).list().size();
        } else {
            return 0;
        }
    }

    /**
     * 查询生产表中对应的管壳码
     * */
    public static String serchShellBlastNo(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryDetonatorTypeNew(denatorId);
    }

    public static int showDenatorSum() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryDenatorBaseinfoToStatusCode("02");
        return list.size();
    }

    /**
     * 查询雷管 按排号查询
     * 排号 1 2 3 4 5
     */
    public  List<DenatorBaseinfo> queryDetonatorPai(int pai) {
        QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
        result = result.where(DenatorBaseinfoDao.Properties.Pai.eq(pai))
                .orderDesc(DenatorBaseinfoDao.Properties.Delay);
        return result.list();
    }
    /**
     * 通过芯片码获取管壳码
     */
    public DetonatorTypeNew queryDetonatorForTypeNew(String detonatorId) {
        Log.e("模糊查询", "detonatorId.substring(5): " + detonatorId.substring(5));//A621400FED518
        List<DetonatorTypeNew> dt = detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.DetonatorId.like("%" + detonatorId.substring(5))).list();
//        List<DetonatorTypeNew> dt =detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.DetonatorId.eq(detonatorId)).list();
        if (dt.size() >= 1) {
            return dt.get(0);
        } else {
            return null;
        }
    }

    /**
     * 删除某一发雷管
     */
    public void deleteDetonator(String shell) {
        DenatorBaseinfo entity = mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(shell))
                .unique();
        mDeantorBaseDao.delete(entity);
    }

    /**
     * 获取 该区域 最小序号 的延时
     *
     */
    public int getPieceMaxPai() {
        int pai;
        String sql = "select max(pai) from denatorBaseinfo  ";
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            pai = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceMaxNumDelay", "获取当前区域最大段号: "+pai);
            return pai;
        }else {
            Log.e("getPieceMaxNumDelay", "获取最小序号 的延时: 1");
            return 1;
        }

    }

    public DenatorHis_Main queryDetonatorForMainHis(String time) {
        return denatorHis_mainDao
                .queryBuilder()
                .where(DenatorHis_MainDao.Properties.Blastdate.eq(time))
                .unique();
    }

    /**
     * 查询雷管 区域正序
     */
    public List<DenatorBaseinfo> queryDetonatorRegionAsc() {
        return mDeantorBaseDao
                .queryBuilder()
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }

    public static void setDenatorType() {
        List<Denator_type> msg = getDaoSession().getDenator_typeDao().loadAll();
        if (msg.size() == 0) {
            Denator_type message = new Denator_type();
            message.setDeTypeName("scyb");
            message.setDeTypeSecond("10000");
            message.setIsSelected("是");
            getDaoSession().getDenator_typeDao().insert(message);
        }
    }

    //丹灵下载后更新雷管芯片码//在线下载,离线下载
    public static void updateLgState(DanLingBean.LgsBean.LgBean lgBean, String yxq) {

        //94242214050
        //F42F1C 2E0A 2 5
        Log.e("雷管下载", "lgBean.getUid(): "+lgBean.getUid() );
        if ( !lgBean.getUid().startsWith("00000")) {
            String uid = null;
            String yscs = null;
            String duan = null;
            String version = null;

            Log.e("第2-1步", "更正注册列表中雷管的芯片码,授权日期,延时参数,延时: --------------" );
            QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
            DenatorBaseinfo db = result.where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(lgBean.getUid())).unique();
            if (db != null) {
//                Log.e("查询数据库中是否有对应的数据", "db: " + db);
                if(lgBean.getGzm().length()>=10){//雷管已使用下载下来是8个0
                    uid = "A62F400" + lgBean.getGzm().substring(0, 6);
                    yscs = lgBean.getGzm().substring(6, 10);
                    db.setDenatorId(uid);
                }

                if(lgBean.getGzm().length()>=10){
                    db.setZhu_yscs(yscs);//有延时参数就更新延时参数
                    db.setRegdate(yxq);
                }
                Log.e("雷管下载", "lgBean.getGzm(): "+lgBean.getGzm() );
                if (lgBean.getGzm().length() == 12) {//煤许下载更新延时,非煤许不更新延时
                    duan = lgBean.getGzm().substring(11,12);
                    version = lgBean.getGzm().substring(10,11);
                    int delay=0;
                    switch (duan){
                        case "1":
                            break;
                        case "2":
                            delay=25;
                            break;
                        case "3":
                            delay=50;
                            break;
                        case "4":
                            delay=75;
                            break;
                        case "5":
                            delay=100;
                            break;
                    }
                    if(!duan.equals("0")){//更新煤许段位
                        db.setDelay(delay);
                    }
                    db.setCong_yscs(duan);//因为以后用不到从延时参数,就放成煤许段位了
                    db.setAuthorization("0"+version);

                    //小于0x0600的就是快速
                    //0x09C1就是慢速的
                    //0x04C1就是快速的
//                    String yscs2=yscs.substring(2)+yscs.substring(0,2);
//                    BigInteger one = new BigInteger(yscs2,16);
//                    BigInteger two = new BigInteger("0699",16);
//                    Log.e("判断产品型号-lgBean.getGzm()",lgBean.getGzm());
//                    Log.e("判断产品型号-version",version);
//                    Log.e("判断产品型号-比较",one.compareTo(two)+"");
//                    if(one.compareTo(two) > 0&&!version.equals("2")){
//                        db.setAuthorization("02");
//                        Log.e("判断产品型号","02");
//                    }else if(one.compareTo(two) < 0&&!version.equals("1")){
//                        Log.e("判断产品型号","01");
//                        db.setAuthorization("01");
//                    }

//                    db.setDuan(duan);//因为以后用不到从延时参数,就放成煤许段位了
                }
                getDaoSession().getDenatorBaseinfoDao().update(db);
                Log.e("第2-1步", "结束: --------------" );
                if (lgBean.getGzmcwxx().equals("0")){
                    db.setErrorName("雷管正常");
                }else if(lgBean.getGzmcwxx().equals("1")){
                    db.setErrorName("雷管在黑名单中");
                }else if(lgBean.getGzmcwxx().equals("2")){
                    db.setErrorName("雷管已使用");
                }else if(lgBean.getGzmcwxx().equals("3")){
                    db.setErrorName("申请的雷管UID不存在");
                }
                Log.e("第3步", "把雷管信息更新到生产数据库中: --------------" );
                registerDetonator_typeNew(db,yxq);//更新到生产数据库中
            }
        }
    }

    /**
     * 读取输入注册
     */
    private static void registerDetonator_typeNew(DenatorBaseinfo leiguan,String yxq) {
//        getDaoSession().getDetonatorTypeNewDao().deleteAll();//读取生产数据前先清空旧的数据
        // 检查重复数据

        Log.e("typeNew", "yxq: "+yxq  );
        Log.e("typeNew", "leiguan.getAuthorization(): "+leiguan.getAuthorization()  );
        // 雷管类型_新
        DetonatorTypeNew detonatorTypeNew = new DetonatorTypeNew();
        detonatorTypeNew.setShellBlastNo(leiguan.getShellBlastNo());
        detonatorTypeNew.setDetonatorId(leiguan.getDenatorId());
        detonatorTypeNew.setDetonatorIdSup(leiguan.getAuthorization());//放得版本号
        detonatorTypeNew.setZhu_yscs(leiguan.getZhu_yscs());
        detonatorTypeNew.setCong_yscs(leiguan.getCong_yscs());//放得段号
        detonatorTypeNew.setTime(yxq);//2023-06-15 17:20:40  leiguan.getRegdate().substring(0, 10)
        detonatorTypeNew.setQibao(leiguan.getErrorName());//先根据丹灵返回是否正常

        //对比时间
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format1 = sd.format(new Date(System.currentTimeMillis() ));//当前时间

        if(leiguan.getErrorName().equals("雷管正常")){

            try {
                Date date1 = sd.parse(format1);//当前日期

                Date date2 = sd.parse(yxq);//有效期
                Log.e("日期", "date1: "+date1 );
                Log.e("日期", "date2: "+date2 );
                Log.e("日期", "date1.compareTo(date2): "+date1.compareTo(date2) );
                if(date1.compareTo(date2)>0){
                    detonatorTypeNew.setQibao("雷管过期");
                    detonatorTypeNew.setDetonatorId(null);
                }else {
                    detonatorTypeNew.setDetonatorId(leiguan.getDenatorId());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //检查在授权表中是否重复
        if (checkRepeatShellBlastNo_typeNew(leiguan.getShellBlastNo())) {
            GreenDaoMaster master = new GreenDaoMaster();
            DetonatorTypeNew detonatorType2 = master.queryShellBlastNoTypeNew(detonatorTypeNew.getShellBlastNo());
            String time_bf=detonatorType2.getTime();//更新前的时间
            detonatorType2.setDetonatorId(leiguan.getDenatorId());
            detonatorType2.setDetonatorIdSup(leiguan.getAuthorization());//放得版本号
            detonatorType2.setZhu_yscs(leiguan.getZhu_yscs());
            detonatorType2.setCong_yscs(leiguan.getCong_yscs());//放得段号
            detonatorType2.setTime(yxq);
            detonatorType2.setQibao(leiguan.getErrorName());
            if(detonatorType2.getQibao().equals("雷管正常")){
                try {
                    Date date1 = sd.parse(format1);//当前日期
                    Date date2 = sd.parse(leiguan.getRegdate());//有效期
                    if(date1.compareTo(date2)>0){
                        detonatorType2.setQibao("雷管过期");
                        detonatorType2.setDetonatorId(null);
                    }else {
                        detonatorType2.setDetonatorId(leiguan.getDenatorId());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            getDaoSession().getDetonatorTypeNewDao().update(detonatorType2);

//            master.updataShouQuan(time_bf);//更新授权数量(在页面更新了,这里应该不用了)
            Log.e("更新生产库中的雷管信息",": " );
//
            return;
        }
        Log.e("插入新生产库中的雷管信息", ": ");
        getDaoSession().getDetonatorTypeNewDao().insert(detonatorTypeNew);
        Log.e("第3步", "结束: --------------" );
    }

    /**
     * 检查重复的数据
     *
     * @param ShellBlastNo
     */
    public static boolean checkRepeatShellBlastNo_typeNew(String ShellBlastNo) {
        GreenDaoMaster master = new GreenDaoMaster();
        DetonatorTypeNew detonatorTypeNew = master.checkRepeat_DetonatorTypeNew(ShellBlastNo);
        if (detonatorTypeNew != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void updateLgState_lixian(DanLingOffLinBean.Lgs.Lg lgBean, String yxq) {
        //94242214050
        //F42F1C 2E0A 2 5
        if ( !lgBean.getUid().startsWith("00000")) {
            String uid = null;
            String yscs = null;
            String duan = null;
            String version = null;
            if(lgBean.getGzm().length()>=10){//雷管已使用下载下来是8个0
                uid = "A62F400" + lgBean.getGzm().substring(0, 6);
                yscs = lgBean.getGzm().substring(6, 10);
            }
            if (lgBean.getGzm().length() == 12) {//煤许下载更新延时,非煤许不更新延时
                duan = lgBean.getGzm().substring(11, 12);
                version = lgBean.getGzm().substring(10, 11);
            }
            DenatorBaseinfo db_sc= new DenatorBaseinfo();
            db_sc.setShellBlastNo(lgBean.getUid());
            db_sc.setDenatorId(uid);
            if(version!=null){
                db_sc.setAuthorization("0"+version);
            }

            db_sc.setZhu_yscs(yscs);
            db_sc.setCong_yscs(duan);

            db_sc.setRegdate(lgBean.getYxq());//原来是截取到日,现在改到小时
            if (lgBean.getGzmcwxx().equals("0")){
                db_sc.setErrorName("雷管正常");
            }else if(lgBean.getGzmcwxx().equals("1")){
                db_sc.setErrorName("雷管在黑名单中");
            }else if(lgBean.getGzmcwxx().equals("2")){
                db_sc.setErrorName("雷管已使用");
            }else if(lgBean.getGzmcwxx().equals("3")){
                db_sc.setErrorName("申请的雷管UID不存在");
            }
            //小于0x0600的就是快速
            //0x09C1就是慢速的
            //0x04C1就是快速的
//            String yscs2=yscs.substring(2)+yscs.substring(0,2);
//            BigInteger one = new BigInteger(yscs2,16);
//            BigInteger two = new BigInteger("0699",16);
//            Log.e("判断产品型号-lgBean.getGzm()",lgBean.getGzm());
//            Log.e("判断产品型号-version",version);
//            Log.e("判断产品型号-比较",one.compareTo(two)+"");
//            if(one.compareTo(two) > 0&&!version.equals("2")){
//                db_sc.setAuthorization("02");
//                Log.e("判断产品型号","02");
//            }else if(one.compareTo(two) < 0&&!version.equals("1")){
//                Log.e("判断产品型号","01");
//                db_sc.setAuthorization("01");
//            }
            registerDetonator_typeNew(db_sc,yxq);//更新到生产数据库中


            SimpleDateFormat sd2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format2 = sd2.format(new Date(System.currentTimeMillis() ));
//            Utils.writeRecord("format2: " + format2);
            QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
            DenatorBaseinfo db = result.where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(lgBean.getUid())).unique();
            if (db != null) {
                Log.e("查询数据库中是否有对应的数据", "db: " + db);
//                Utils.writeRecord("db: " + db.toString());
//                Utils.writeRecord("uid: " + uid);
                db.setDenatorId(uid);
                db.setRegdate(lgBean.getYxq());
                if(lgBean.getGzm().length()>=10){
                    db.setZhu_yscs(yscs);//有延时参数就更新延时参数
                }

                if (lgBean.getGzm().length() == 12) {//煤许下载更新延时,非煤许不更新延时
                    int delay=0;
                    switch (duan){
                        case "1":
                            break;
                        case "2":
                            delay=25;
                            break;
                        case "3":
                            delay=50;
                            break;
                        case "4":
                            delay=75;
                            break;
                        case "5":
                            delay=100;
                            break;
                    }
                    if(!duan.equals("0")){
                        db.setDelay(delay);
                    }
                    db.setCong_yscs(duan);//因为以后用不到从延时参数,就放成煤许段位了
                    db.setAuthorization("0"+version);
//                    db.setDuan(duan);//因为以后用不到从延时参数,就放成煤许段位了
                }
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format1 = sd.format(new Date(System.currentTimeMillis() ));
                try {
                    Date date1 = sd.parse(format1);//当前日期
                    Date date2 = sd.parse(db.getRegdate());//有效期
                    if (date1.compareTo(date2) <= 0) {
                        getDaoSession().getDenatorBaseinfoDao().update(db);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
