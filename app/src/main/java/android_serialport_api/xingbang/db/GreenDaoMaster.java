package android_serialport_api.xingbang.db;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.db.greenDao.DefactoryDao;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfo_allDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_DetailDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_MainDao;
import android_serialport_api.xingbang.db.greenDao.Denator_typeDao;
import android_serialport_api.xingbang.db.greenDao.DetonatorTypeNewDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.db.greenDao.ShouQuanDao;
import android_serialport_api.xingbang.db.greenDao.UserMainDao;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;

/**
 * 管理GreenDao查询语句
 */
public class GreenDaoMaster {

    private DefactoryDao mDefactoryDao;
    private DenatorBaseinfoDao mDeantorBaseDao;
    private DenatorBaseinfo_allDao mDenatorBaseDao_all;
    private ProjectDao mProjectDao;
    private Denator_typeDao mDenatorType;
    private MessageBeanDao messageBeanDao;
    private DetonatorTypeNewDao detonatorTypeNewDao;
    private ShouQuanDao mShouquanDao;
    private DenatorHis_DetailDao denatorHis_detailDao;
    private DenatorHis_MainDao denatorHis_mainDao;
    private UserMainDao mUserDao;

    public GreenDaoMaster() {
        this.mDefactoryDao = Application.getDaoSession().getDefactoryDao();
        this.detonatorTypeNewDao = Application.getDaoSession().getDetonatorTypeNewDao();
        this.mDenatorBaseDao_all = Application.getDaoSession().getDenatorBaseinfo_allDao();
        this.mDeantorBaseDao = Application.getDaoSession().getDenatorBaseinfoDao();
        this.mProjectDao = Application.getDaoSession().getProjectDao();
        this.mDenatorType = Application.getDaoSession().getDenator_typeDao();
        this.denatorHis_detailDao = Application.getDaoSession().getDenatorHis_DetailDao();
        this.denatorHis_mainDao = Application.getDaoSession().getDenatorHis_MainDao();
        this.mUserDao = Application.getDaoSession().getUserMainDao();
        this.mShouquanDao = Application.getDaoSession().getShouQuanDao();
    }

    public ShouQuan getShouquan(int p) {
        return getDaoSession().getShouQuanDao().queryBuilder().where(ShouQuanDao.Properties.Id.eq(p)).unique();

    }


    public List<Defactory> queryDefactoryToIsSelected(String selected) {
        QueryBuilder<Defactory> result = mDefactoryDao.queryBuilder();
        result = result.where(DefactoryDao.Properties.IsSelected.eq(selected));
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

    /**
     * 查询所有雷管
     *
     * @return
     */
    public List<DenatorBaseinfo> queryDenatorBaseinfo() {
        return mDeantorBaseDao.loadAll();
    }

    public List<Project> queryProjectToProject_name(String project_name) {
        QueryBuilder<Project> result = mProjectDao.queryBuilder();
        result = result.where(ProjectDao.Properties.Project_name.eq(project_name));
        return result.list();
    }

    public List<Denator_type> queryDefactoryTypeToIsSelected(String selected) {
        QueryBuilder<Denator_type> result = mDenatorType.queryBuilder();
        result = result.where(Denator_typeDao.Properties.IsSelected.eq(selected));
        return result.list();
    }
    public List<UserMain> queryUser(String name) {
        QueryBuilder<UserMain> result = mUserDao.queryBuilder();
        result = result.where(UserMainDao.Properties.Uname.eq(name));
        return result.list();
    }

    public List<UserMain> queryAllUser() {
        QueryBuilder<UserMain> result = mUserDao.queryBuilder();
        return result.list();
    }
    /**
     * 查询错误雷管
     */
    public List<DenatorBaseinfo> queryErrLeiGuan(String piece) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result = result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF"))
                .where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("F1"))
                .where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("F2"))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece));
        return result.list();
    }
    public List<DenatorBaseinfo> queryErrLeiGuan() {
        List<DenatorBaseinfo> mListData = new ArrayList<>();
        boolean mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);//是否选中区域1
        boolean mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);//是否选中区域2
        boolean mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);//是否选中区域3
        boolean mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);//是否选中区域4
        boolean mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);//是否选中区域5
        if (mRegion1) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan("1"));
        }if (mRegion2) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan("2"));
        }if (mRegion3) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan("3"));
        }if (mRegion4) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan("4"));
        }if (mRegion5) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan("5"));
        }
        return mListData;
    }

    /**
     * 删除错误代码不等于FF的所有雷管
     */
    public void deleteErrLeiGuan(String piece) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF")).where(DenatorBaseinfoDao.Properties.Piece.eq(piece)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
    public void deleteErrLeiGuan() {
        boolean mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);//是否选中区域1
        boolean mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);//是否选中区域2
        boolean mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);//是否选中区域3
        boolean mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);//是否选中区域4
        boolean mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);//是否选中区域5



        if (mRegion5) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            Log.e("删除所有", "5: ");
            result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF")).where(DenatorBaseinfoDao.Properties.Piece.eq("5")).buildDelete().executeDeleteWithoutDetachingEntities();
        }if (mRegion4) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            Log.e("删除所有", "4: ");
            result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF")).where(DenatorBaseinfoDao.Properties.Piece.eq("4")).buildDelete().executeDeleteWithoutDetachingEntities();
        }if (mRegion3) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            Log.e("删除所有", "3: ");
            result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF")).where(DenatorBaseinfoDao.Properties.Piece.eq("3")).buildDelete().executeDeleteWithoutDetachingEntities();
        }if (mRegion2) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            Log.e("删除所有", "2: ");
            result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF")).where(DenatorBaseinfoDao.Properties.Piece.eq("2")).buildDelete().executeDeleteWithoutDetachingEntities();
        }if (mRegion1) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            Log.e("删除所有", "1: ");
            result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF")).where(DenatorBaseinfoDao.Properties.Piece.eq("1")).buildDelete().executeDeleteWithoutDetachingEntities();
        }
    }

    /**
     * 删除错误代码不等于FF的所有雷管
     */
    public void deleteLeiGuanFroPiace(String piece) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result.where(DenatorBaseinfoDao.Properties.Piece.eq(piece)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
    public void deleteLeiGuanFroPiace() {
        boolean mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);//是否选中区域1
        boolean mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);//是否选中区域2
        boolean mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);//是否选中区域3
        boolean mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);//是否选中区域4
        boolean mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);//是否选中区域5



        if (mRegion5) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            result.where(DenatorBaseinfoDao.Properties.Piece.eq("5")).buildDelete().executeDeleteWithoutDetachingEntities();
        }if (mRegion4) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            result.where(DenatorBaseinfoDao.Properties.Piece.eq("4")).buildDelete().executeDeleteWithoutDetachingEntities();
        }if (mRegion3) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            result.where(DenatorBaseinfoDao.Properties.Piece.eq("3")).buildDelete().executeDeleteWithoutDetachingEntities();
        }if (mRegion2) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            result.where(DenatorBaseinfoDao.Properties.Piece.eq("2")).buildDelete().executeDeleteWithoutDetachingEntities();
        }if (mRegion1) {
            QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
            result.where(DenatorBaseinfoDao.Properties.Piece.eq("1")).buildDelete().executeDeleteWithoutDetachingEntities();
        }

    }

    public List<MessageBean> queryUsetMessgae() {
        QueryBuilder<MessageBean> result = messageBeanDao.queryBuilder();
        return result.where(MessageBeanDao.Properties.Id.eq(1)).list();
    }

    /**
     * 通过芯片码获取管壳码
     */
    public String queryDetonatorTypeNew(String detonatorId) {
        Log.e("模糊查询", "detonatorId.substring(5): " + detonatorId.substring(5));
        List<DetonatorTypeNew> dt = detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.DetonatorId.like("%" + detonatorId.substring(5))).list();
//        List<DetonatorTypeNew> dt =detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.DetonatorId.eq(detonatorId)).list();
        if (dt.size() >= 1) {
            return dt.get(0).getShellBlastNo();
        } else {
            return "0";
        }
    }

    /**
     * 通过芯片码获取管壳码
     */
    public DetonatorTypeNew queryDetonatorForTypeNew(String detonatorId) {
        Log.e("模糊查询", "detonatorId.substring(7): " + detonatorId.substring(5));//A621400FED518
        List<DetonatorTypeNew> dt = detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.DetonatorId.like("%" + detonatorId.substring(5))).list();
//        List<DetonatorTypeNew> dt =detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.DetonatorId.eq(detonatorId)).list();
        if (dt.size() >= 1) {
            return dt.get(0);
        } else {
            return null;
        }
    }

    /**
     * 通过管壳码获取芯片码
     */
    public DetonatorTypeNew queryShellBlastNoTypeNew(String shellBlastNo) {
        Log.e("通过管壳码获取芯片码", "shellBlastNo: " + shellBlastNo);

        List<DetonatorTypeNew> dt = detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(shellBlastNo)).list();
//        Log.e("通过管壳码获取芯片码", "dt.size(): " + dt.size());
        if (dt.size() >= 1) {
            return dt.get(0);
        } else {
            return null;
        }
    }

    /**
     * 查询授权信息(倒序)
     */
    public List<ShouQuan> queryShouQuan() {
        QueryBuilder<ShouQuan> result = mShouquanDao.queryBuilder();
        return result.orderDesc(ShouQuanDao.Properties.Id).list();
    }

    /**
     * 查询授权信息(倒序)
     */
    public List<Project> queryProject() {
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
     */
    public List<DenatorBaseinfo> checkRepeatdenatorId(String detonatorId) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.DenatorId.like("%" + detonatorId))
                .list();//0209为李斌改的不用4字节的首字节进行的模糊查询
    }

    /**
     * 查询注册列表中的重复雷管
     */
    public List<DenatorBaseinfo> checkRepeatShellNo(String ShellBlastNo) {
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

    public List<DenatorBaseinfo> checkShellNo_2() {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Duan.notEq(DenatorBaseinfoDao.Properties.Cong_yscs))
                .list();
    }

    /**
     * 查询生产列表中的重复雷管
     */
    public DetonatorTypeNew checkRepeat_DetonatorTypeNew(String ShellBlastNo) {
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
        List<DenatorBaseinfo> list = getDaoSession().getDenatorBaseinfoDao().loadAll();
        String str = "ID,序号,孔号,管壳码,芯片码,延时,读取状态,状态名称,错误名称,错误代码,授权期限,备注,注册日期,桥丝状态,名称,从芯片码,主延时参数,从延时参数\n";
        String content;
        for (int i = 0; i < list.size(); i++) {
            content = list.get(i).getId() + "," + list.get(i).getBlastserial() + "," + list.get(i).getSithole() + ","
                    + list.get(i).getShellBlastNo() + "," + list.get(i).getDenatorId() + "," + list.get(i).getDelay() + ","
                    + list.get(i).getStatusCode() + "," + list.get(i).getStatusName() + "," + list.get(i).getErrorName() + ","
                    + list.get(i).getErrorCode() + "," + list.get(i).getAuthorization() + "," + list.get(i).getRemark() + ","
                    + list.get(i).getRegdate() + "," + list.get(i).getWire() + "," + list.get(i).getName() + ","
                    + list.get(i).getDenatorIdSup() + "," + list.get(i).getZhu_yscs() + "," + list.get(i).getCong_yscs() + ","
                    + list.get(i).getPiece()+ "," +list.get(i).getDuan()+ "," +list.get(i).getDuanNo() + "\n";
            str = str + content;
        }
        return str;
    }

    //丹灵下载后更新雷管芯片码//在线下载,离线下载
    public static void updateLgState(DanLingBean.LgsBean.LgBean lgBean,String sqrq) {
        //94242214050
        //F42F1C 2E0A 2 5
        if (lgBean.getGzmcwxx().equals("0") && !lgBean.getUid().startsWith("00000")) {
            String uid = "";
            String yscs = "";
            String duan = "";
            String version = "";
            uid = "A62F400" + lgBean.getGzm().substring(0, 6);
            if(lgBean.getGzm().length()>=10){//雷管已使用下载下来是8个0
                yscs = lgBean.getGzm().substring(6, 10);
            }


            QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
            DenatorBaseinfo db = result.where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(lgBean.getUid())).unique();
            if (db != null) {
//                Log.e("查询数据库中是否有对应的数据", "db: " + db);
                db.setDenatorId(uid);
                if(lgBean.getGzm().length()>=10){
                    db.setZhu_yscs(yscs);//有延时参数就更新延时参数
                }

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
                    if(!duan.equals("0")){
                        db.setDelay(delay);
                    }
                    db.setCong_yscs(duan);//因为以后用不到从延时参数,就放成煤许段位了
                    db.setAuthorization("0"+version);
                    db.setRegdate(lgBean.getYxq());
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
                registerDetonator_typeNew(db,sqrq);//更新到生产数据库中
                Utils.saveFile();//把软存中的数据存入磁盘中
            }

        }

    }
    public static void updateLgState_lixian(DanLingBean.LgsBean.LgBean lgBean,String sqrq) {
        //94242214050
        //F42F1C 2E0A 2 5
        if (lgBean.getGzmcwxx().equals("0") && !lgBean.getUid().startsWith("00000")) {
            String uid = "";
            String yscs = "";
            String duan = "";
            String version = "";
            uid = "A62F400" + lgBean.getGzm().substring(0, 6);
            if(lgBean.getGzm().length()>=10){//雷管已使用下载下来是8个0
                yscs = lgBean.getGzm().substring(6, 10);
            }
            if (lgBean.getGzm().length() == 12) {//煤许下载更新延时,非煤许不更新延时
                duan = lgBean.getGzm().substring(11, 12);
                version = lgBean.getGzm().substring(10, 11);
            }
            DenatorBaseinfo db_sc= new DenatorBaseinfo();
            db_sc.setShellBlastNo(lgBean.getUid());
            db_sc.setDenatorId(uid);
            db_sc.setAuthorization("0"+version);
            db_sc.setZhu_yscs(yscs);
            db_sc.setCong_yscs(duan);
            db_sc.setRegdate(lgBean.getYxq());//原来是截取到日,现在改到小时
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
//            db_sc.setRegdate(lgBean.getYxq().substring(0, 8));
            registerDetonator_typeNew(db_sc,sqrq);//更新到生产数据库中

            QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
            DenatorBaseinfo db = result.where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(lgBean.getUid())).unique();
            if (db != null) {
//                Log.e("查询数据库中是否有对应的数据", "db: " + db);
                db.setDenatorId(uid);
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
                getDaoSession().getDenatorBaseinfoDao().update(db);

                Utils.saveFile();//把软存中的数据存入磁盘中
            }

        }

    }

    /**
     * 读取输入注册
     */
    private static void registerDetonator_typeNew(DenatorBaseinfo leiguan,String sqrq) {
//        getDaoSession().getDetonatorTypeNewDao().deleteAll();//读取生产数据前先清空旧的数据
        // 检查重复数据

        // 雷管类型_新
        DetonatorTypeNew detonatorTypeNew = new DetonatorTypeNew();
        detonatorTypeNew.setShellBlastNo(leiguan.getShellBlastNo());
        detonatorTypeNew.setDetonatorId(leiguan.getDenatorId());
        detonatorTypeNew.setDetonatorIdSup(leiguan.getAuthorization());//放得版本号
        detonatorTypeNew.setZhu_yscs(leiguan.getZhu_yscs());
        detonatorTypeNew.setCong_yscs(leiguan.getCong_yscs());//放得段号
        detonatorTypeNew.setTime(leiguan.getRegdate());//2023-06-15 17:20:40  leiguan.getRegdate().substring(0, 10)
        detonatorTypeNew.setQibao("雷管正常");
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format1 = sd.format(new Date(System.currentTimeMillis() ));
        try {
            Date date1 = sd.parse(format1);//当前日期
            Date date2 = sd.parse(leiguan.getRegdate());//有效期
            if(date1.compareTo(date2)>0){
                detonatorTypeNew.setQibao("雷管过期");
                detonatorTypeNew.setDetonatorId("");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (checkRepeatShellBlastNo_typeNew(leiguan.getShellBlastNo())) {

            GreenDaoMaster master = new GreenDaoMaster();
            DetonatorTypeNew detonatorType2 = master.queryShellBlastNoTypeNew(detonatorTypeNew.getShellBlastNo());
            detonatorType2.setTime(leiguan.getRegdate());
            getDaoSession().getDetonatorTypeNewDao().update(detonatorType2);
            Log.e("更新生产库中的雷管信息", "detonatorType2: "+detonatorType2);
            return;
        }
        getDaoSession().getDetonatorTypeNewDao().insert(detonatorTypeNew);
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

    /**
     * 查询雷管 区域倒序(序号)
     * 区域号 1 2 3 4 5
     */
    public List<DenatorBaseinfo> queryDetonatorDesc() {
        return mDeantorBaseDao
                .queryBuilder()
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }

    /**
     * 从数据库表中拿数据
     *
     * @return
     */
    public static void delAllMessage() {
        getDaoSession().getShouQuanDao().deleteAll();
    }
    /**
     * 从数据库表中拿数据
     *
     * @return
     */
    public static void delAllDetonatorTypeNew() {
        getDaoSession().getDetonatorTypeNewDao().deleteAll();
    }
    /**
     * 从数据库表中拿数据
     *
     * @return
     */
    public static List<ShouQuan> getAllShouQuan() {
//        return getDaoSession().getShouQuanDao().loadAll();
        return getDaoSession().getShouQuanDao().queryBuilder().orderDesc(ShouQuanDao.Properties.Id).list();
    }

    /**
     * 修改雷管延时
     *
     * @param shell 管壳号
     * @param delay 延时
     */
    public void updateDetonatorDelay(String shell, int delay) {
        DenatorBaseinfo entity = mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(shell))
                .build()
                .unique();
        entity.setDelay(delay);
        mDeantorBaseDao.update(entity);
    }

    /**
     * 删除某一发雷管
     */
    public void deleteDetonator(String gkm) {
        DenatorBaseinfo entity = mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(gkm))
                .unique();
        if(entity!=null){
            mDeantorBaseDao.delete(entity);
        }
    }


    /**
     * 查询全部雷管 正序(序号)
     */
    public List<DenatorBaseinfo> queryAllDetonatorAsc() {
        return mDeantorBaseDao
                .queryBuilder()
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }

    /**
     * 删除全部雷管
     */
    public void deleteAllDetonator() {
        mDeantorBaseDao.deleteAll();
        mDenatorBaseDao_all.deleteAll();
    }


    /**
     * 查询雷管 区域正序(序号) 1到100
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public List<DenatorBaseinfo> queryDetonatorRegionAsc(String piece) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }
    public List<DenatorBaseinfo> queryDetonatorRegionAsc() {
        List<DenatorBaseinfo> mListData = new ArrayList<>();
        boolean mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);//是否选中区域1
        boolean mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);//是否选中区域2
        boolean mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);//是否选中区域3
        boolean mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);//是否选中区域4
        boolean mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);//是否选中区域5
        if (mRegion1) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionAsc("1"));
        }if (mRegion2) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionAsc("2"));
        }if (mRegion3) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionAsc("3"));
        }if (mRegion4) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionAsc("4"));
        }if (mRegion5) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionAsc("5"));
        }
        return mListData;
    }

    /**
     * 查询雷管 区域倒序(序号)  100 - 1
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public List<DenatorBaseinfo> queryDetonatorRegionDesc(String piece) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }
    public List<DenatorBaseinfo> queryDetonatorRegionDesc() {
        List<DenatorBaseinfo> mListData = new ArrayList<>();
         boolean mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);//是否选中区域1
         boolean mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);//是否选中区域2
         boolean mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);//是否选中区域3
         boolean mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);//是否选中区域4
         boolean mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);//是否选中区域5
        if (mRegion5) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("5"));
        }if (mRegion4) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("4"));
        }if (mRegion3) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("3"));
        }if (mRegion2) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("2"));
        }if (mRegion1) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("1"));
        }
        return mListData;
    }

    /**
     * 获取 该区域 最大序号
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceMaxNum(String piece) {
        // 倒叙查询
        List<DenatorBaseinfo> mList = queryDetonatorRegionDesc(piece);

        // 如果有数据
        if (mList.size() > 0) {
            // 第一个雷管数据是最大序号
            int num = mList.get(0).getBlastserial();
            Log.e("getPieceMaxNum", "获取最大序号: " + num);
            return num;
            // 如果没数据
        } else {
            Log.e("getPieceMaxNum", "获取最大序号: 0");
            return 0;
        }
    }

    /**
     * 获取 该区域 最大序号 的延时
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceMaxNumDelay(String piece) {
        // 倒叙查询
        List<DenatorBaseinfo> mList = queryDetonatorRegionDesc(piece);

        // 如果有数据
        if (mList.size() > 0) {
            // 第一个雷管数据 该区域 最大序号 的延时
            int delay = mList.get(0).getDelay();
            Log.e("getPieceMaxNumDelay", "获取最大序号 的延时: " + delay);
            return delay;
            // 如果没数据
        } else {
            Log.e("getPieceMaxNumDelay", "获取最大序号 的延时: 0");
            return 0;
        }
    }

    /**
     * 删除生产数据中的雷管
     */
    public  void deleteTypeLeiGuan(String time) {
        Log.e("删除生产数据中的雷管", "time: "+time );
        QueryBuilder<DetonatorTypeNew> result = detonatorTypeNewDao.queryBuilder();
        result.where(DetonatorTypeNewDao.Properties.Time.lt(time)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
    /**
     * 删除授权数据
     */
    public  void deleteShouQuan(String time) {
        QueryBuilder<ShouQuan> result = mShouquanDao.queryBuilder();
        result.where(ShouQuanDao.Properties.Spare2.lt(time)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
    /**
     * 删除授权数据
     */
    public  void updataShouQuan(String time,int total) {
        QueryBuilder<ShouQuan> result = mShouquanDao.queryBuilder();
        ShouQuan shouQuan =result.where(ShouQuanDao.Properties.Spare2.eq(time)).unique();
        shouQuan.setTotal(total);
        mShouquanDao.update(shouQuan);
    }
    /**
     * 删除某一发雷管
     */
    public void deleteDetonatorForType(String gkm) {
        DetonatorTypeNew entity = detonatorTypeNewDao
                .queryBuilder()
                .where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(gkm))
                .unique();
        if (entity != null) {
            detonatorTypeNewDao.delete(entity);
        }

    }

    /**
     * 删除生产数据中对应的雷管
     */
    public void deleteType(String time) {
        List<DenatorHis_Detail> list_lg = queryDetonatorForHis(time);
//        Log.e("删除生产数据中对应的雷管", "list_lg.size: " + list_lg.size());
        if (list_lg != null && list_lg.size() > 0) {
//            Log.e("删除生产数据中对应的雷管", "list_lg: " + list_lg.toString());
//            Log.e("删除生产数据中对应的雷管", "list_lg.get(0).getShellBlastNo(): " + list_lg.get(0).getShellBlastNo());
            DetonatorTypeNew entity = detonatorTypeNewDao
                    .queryBuilder()
                    .where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(list_lg.get(0).getShellBlastNo()))
                    .unique();

            if (entity != null) {
//                Log.e("删除生产数据中对应的雷管", "entity: " + entity.toString());//
                for (DenatorHis_Detail a : list_lg) {
                    deleteDetonatorForType(a.getShellBlastNo());
                }
            }

        }

    }

    /**
     * 根据历史记录查询雷管
     *
     * @param time 区域号 1 2 3 4 5
     */
    public List<DenatorHis_Detail> queryDetonatorForHis(String time) {
        return denatorHis_detailDao
                .queryBuilder()
                .where(DenatorHis_DetailDao.Properties.Blastdate.eq(time))
                .list();
    }

    /**
     * 根据起爆时间删除对应的历史记录
     */
    public void deleteForHis(String time) {
        denatorHis_mainDao
                .queryBuilder().where(DenatorHis_MainDao.Properties.Blastdate.eq(time))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }/**
     * 根据起爆时间删除历史记录对应的雷管数据
     */
    public void deleteForDetail(String time) {
        denatorHis_detailDao
                .queryBuilder().where(DenatorHis_DetailDao.Properties.Blastdate.eq(time))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public DetonatorTypeNew serchDenatorId(String shellBlastNo) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryShellBlastNoTypeNew(shellBlastNo);
    }

    public static void setDenatorType() {
        List<Denator_type> msg = getDaoSession().getDenator_typeDao().loadAll();
        if(msg.size()==0){
            Denator_type message = new Denator_type();
            message.setDeTypeName("scyb");
            message.setDeTypeSecond("10000");
            message.setIsSelected("是");
            getDaoSession().getDenator_typeDao().insert(message);
        }

    }
    public static void setFactory() {
        List<Defactory> msg = getDaoSession().getDefactoryDao().loadAll();
        if(msg.size()==0){
            Defactory message = new Defactory();
            message.setDeName("scyb");
            message.setDeEntCode("56");
            message.setDeFeatureCode("A");
            message.setIsSelected("是");
            getDaoSession().getDefactoryDao().insert(message);
        }
    }

    public int getDuanNo(String piece,String duan ) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list().size();
    }
    public  List<DenatorBaseinfo> getDuanNoList(String piece,String duan ) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .orderAsc(DenatorBaseinfoDao.Properties.Id)
                .list();
    }

    /**
     * 查询生产库中雷管
     */
    public List<DetonatorTypeNew> queryDetonatorShouQuan() {
        return detonatorTypeNewDao
                .queryBuilder()
                .orderDesc(DetonatorTypeNewDao.Properties.Id)
                .list();
    }

    /**
     * 查询生产库中雷管
     */
    public void deleteDetonatorShouQuan(String gkm) {
        detonatorTypeNewDao
                .queryBuilder().where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(gkm))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 查询生产库中雷管
     */
    public List<DetonatorTypeNew> queryDetonatorShouQuan(String zt,String sqrq) {
        return detonatorTypeNewDao
                .queryBuilder()
                .where(DetonatorTypeNewDao.Properties.Qibao.eq(zt))
                .where(DetonatorTypeNewDao.Properties.Time.eq(sqrq))
                .orderDesc(DetonatorTypeNewDao.Properties.Id)
                .list();
    }

    /**
     * 查询生产库中雷管
     */
    public List<DetonatorTypeNew> queryDetonatorShouQuanForGkm(String gkm,String sqrq) {
        return detonatorTypeNewDao
                .queryBuilder()
                .where(DetonatorTypeNewDao.Properties.Time.eq(sqrq))
                .where(DetonatorTypeNewDao.Properties.ShellBlastNo.like("%" + gkm+"%"))
                .orderDesc(DetonatorTypeNewDao.Properties.Id)
                .list();
    }

    /**
     * 查询生产库中雷管
     */
    public List<DetonatorTypeNew> queryDetonatorShouQuan2(int offset) {
        return detonatorTypeNewDao.queryBuilder().
                orderDesc(DetonatorTypeNewDao.Properties.Id)
                .offset(offset * 100).limit(100).list();
    }
    /**
     * 根据申请日期查询生产库中雷管
     */
    public List<DetonatorTypeNew> queryDetonatorShouQuanForSqrq(String sqrq) {
        return detonatorTypeNewDao.queryBuilder().
                 where(DetonatorTypeNewDao.Properties.Time.eq(sqrq))
                .orderDesc(DetonatorTypeNewDao.Properties.Id)
                .list();
    }
    /**
     * 修改授权库中雷管状态
     *
     * @param shell 管壳号
     * @param qibao 状态
     */
    public void updateDetonatorTypezt(String shell, String qibao) {
        DetonatorTypeNew entity = detonatorTypeNewDao
                .queryBuilder()
                .where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(shell))
                .build()
                .unique();
        Log.e("更新生产库中的起爆状态", "shell: "+shell );
        if(entity!=null){
            Log.e("更新生产库中的起爆状态", "entity: "+entity.toString() );
            entity.setQibao(qibao);
            detonatorTypeNewDao.update(entity);
        }

    }
}
