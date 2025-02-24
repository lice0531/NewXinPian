package android_serialport_api.xingbang.db;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.custom.DenatorBaseinfoSelect;
import android_serialport_api.xingbang.custom.PaiDataSelect;
import android_serialport_api.xingbang.custom.QuYuData;
import android_serialport_api.xingbang.db.greenDao.DefactoryDao;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfo_allDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_DetailDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_MainDao;
import android_serialport_api.xingbang.db.greenDao.Denator_typeDao;
import android_serialport_api.xingbang.db.greenDao.DetonatorTypeNewDao;
import android_serialport_api.xingbang.db.greenDao.ErrLogDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.db.greenDao.PaiDataDao;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.db.greenDao.QuYuDao;
import android_serialport_api.xingbang.db.greenDao.ShouQuanDao;
import android_serialport_api.xingbang.db.greenDao.SysLogDao;
import android_serialport_api.xingbang.models.DanLingBean;
import android_serialport_api.xingbang.models.DanLingOffLinBean;
import android_serialport_api.xingbang.utils.AppLogUtils;
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
    private QuYuDao quyuDao;
    private PaiDataDao paiDataDao;
    private ShouQuanDao mShouquanDao;
    private DenatorHis_DetailDao denatorHis_detailDao;
    private DenatorHis_MainDao denatorHis_mainDao;
    private SysLogDao sysLogDao;
    private ErrLogDao errLogDao;

    public GreenDaoMaster() {
        this.mDefactoryDao = Application.getDaoSession().getDefactoryDao();
        this.detonatorTypeNewDao = Application.getDaoSession().getDetonatorTypeNewDao();
        this.mDenatorBaseDao_all = Application.getDaoSession().getDenatorBaseinfo_allDao();
        this.mDeantorBaseDao = Application.getDaoSession().getDenatorBaseinfoDao();
        this.mProjectDao = Application.getDaoSession().getProjectDao();
        this.mDenatorType = Application.getDaoSession().getDenator_typeDao();
        this.denatorHis_detailDao = Application.getDaoSession().getDenatorHis_DetailDao();
        this.denatorHis_mainDao = Application.getDaoSession().getDenatorHis_MainDao();
        this.mShouquanDao = Application.getDaoSession().getShouQuanDao();
        this.quyuDao = Application.getDaoSession().getQuYuDao();
        this.paiDataDao = Application.getDaoSession().getPaiDataDao();
        this.sysLogDao = Application.getDaoSession().getSysLogDao();
        this.errLogDao = Application.getDaoSession().getErrLogDao();
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
    /**
     * 查询所有雷管
     *
     * @return
     */
    public DenatorBaseinfo querylg(String gkm) {
        return mDeantorBaseDao.queryBuilder().where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(gkm)).unique();
    }
    /**
     * 查询所有雷管
     *
     * @return
     */
    public DenatorBaseinfo querylgForXh(String xuhao,String piece) {
        return mDeantorBaseDao.queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Blastserial.eq(xuhao))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .unique();
    }
    /**
     * 查询X区域,X段最大序号雷管
     *
     * @return
     */
    public DenatorBaseinfo querylgMaxduanNo(int duanNo,int duan,String piece) {
        List<DenatorBaseinfo> list_lg=mDeantorBaseDao.queryBuilder()
                .where(DenatorBaseinfoDao.Properties.DuanNo.eq(duanNo))
                .where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial).list();
        return list_lg.get(0);
    }

    /**
     * 查询X区域,X段最大序号雷管
     *
     * @return
     */
    public int querylgNum(int duanNo,int duan,String piece) {
        List<DenatorBaseinfo> list_lg=mDeantorBaseDao.queryBuilder()
                .where(DenatorBaseinfoDao.Properties.DuanNo.eq(duanNo))
                .where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial).list();
        return list_lg.size();
    }

    /**
     * 查询X区域,X段后一发雷管
     *
     * @return
     */
    public DenatorBaseinfo querylgduanNo(int duanNo,int duan,String piece) {
        List<DenatorBaseinfo> list_lg=mDeantorBaseDao.queryBuilder()
                .where(DenatorBaseinfoDao.Properties.DuanNo.eq(duanNo))
                .where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial).list();
        Log.e("后一发雷管", "list_lg.size: "+list_lg.size() );
        if(list_lg!=null&&list_lg.size()==1){
            return list_lg.get(0);
        }else if(list_lg.size()>1) {
            return list_lg.get(list_lg.size()-1);
        }else {
            return null;
        }

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

    /**
     * 查询错误雷管
     */
    public List<DenatorBaseinfo> queryErrLeiGuan(String piece) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result = result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF"))
                .where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("F1"))
                .where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("F2"))
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.notEq(""))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece));
        return result.list();
    }
    public List<DenatorBaseinfo> queryErrLeiGuan_CD(String piece) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result = result.where(DenatorBaseinfoDao.Properties.StatusCode.notEq("FF"))
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
     * @param idList:区域页面多选的id
     * @return
     */
    public List<DenatorBaseinfo> queryErrLeiGuanNew(List<Integer> idList) {
        List<DenatorBaseinfo> mListData = new ArrayList<>();
        for (int id : idList) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan(String.valueOf(id)));
        }
        return mListData;
    }
    public List<DenatorBaseinfo> queryErrLeiGuan_CD() {
        List<DenatorBaseinfo> mListData = new ArrayList<>();
        boolean mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);//是否选中区域1
        boolean mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);//是否选中区域2
        boolean mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);//是否选中区域3
        boolean mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);//是否选中区域4
        boolean mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);//是否选中区域5
        if (mRegion1) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan_CD("1"));
        }if (mRegion2) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan_CD("2"));
        }if (mRegion3) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan_CD("3"));
        }if (mRegion4) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan_CD("4"));
        }if (mRegion5) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan_CD("5"));
        }
        return mListData;
    }

    /**
     * @param idList:区域页面多选的id
     * @return
     */
    public List<DenatorBaseinfo> queryErrLeiGuan_CDNew(List<Integer> idList) {
        List<DenatorBaseinfo> mListData = new ArrayList<>();
        for (int id : idList) {
            mListData.addAll(new GreenDaoMaster().queryErrLeiGuan_CD(String.valueOf(id)));
        }
        return mListData;
    }

    /**
     * 删除错误代码不等于FF的所有雷管
     */
    public void deleteErrLeiGuan(String piece) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result.where(DenatorBaseinfoDao.Properties.ErrorCode.notEq("FF"))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 根据区域删除所有雷管
     */
    public void deleteLeiGuanFroPiace(String piece) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        result.where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }



    /**
     * 删除所有雷管
     */
    public void deleteAllLeiGuan() {
        mDeantorBaseDao.deleteAll();
        mDenatorBaseDao_all.deleteAll();
    }

    /**
     * 删除选中区域的所有排
     */
    public void deletePaiFroPiace(String qyid) {
        Log.e("删除选中区域的所有排", "qyid: "+qyid);
        QueryBuilder<PaiData> result = paiDataDao.queryBuilder();
        result.where(PaiDataDao.Properties.Qyid.eq(qyid)).buildDelete().executeDeleteWithoutDetachingEntities();
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
        List<DetonatorTypeNew> dt = detonatorTypeNewDao.queryBuilder()
                .where(DetonatorTypeNewDao.Properties.DetonatorId.like("%" + detonatorId.substring(5)))
                .list();
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

        List<DetonatorTypeNew> dt = detonatorTypeNewDao.queryBuilder()
                .where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(shellBlastNo))
                .list();
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
        String str = "ID,序号,孔号,管壳码,芯片码,延时,读取状态,状态名称,错误名称,错误代码,授权期限,备注,注册日期,桥丝状态,名称,从芯片码,主延时参数,从延时参数,区域,段,段号,翻转,排\n";
        String content;
        for (int i = 0; i < list.size(); i++) {
            content = list.get(i).getId() + "," + list.get(i).getBlastserial() + "," + list.get(i).getSithole() + ","
                    + list.get(i).getShellBlastNo() + "," + list.get(i).getDenatorId() + "," + list.get(i).getDelay() + ","
                    + list.get(i).getStatusCode() + "," + list.get(i).getStatusName() + "," + list.get(i).getErrorName() + ","
                    + list.get(i).getErrorCode() + "," + list.get(i).getAuthorization() + "," + list.get(i).getRemark() + ","
                    + list.get(i).getRegdate() + "," + list.get(i).getWire() + "," + list.get(i).getName() + ","
                    + list.get(i).getDenatorIdSup() + "," + list.get(i).getZhu_yscs() + "," + list.get(i).getCong_yscs() + ","
                    + list.get(i).getPiece() + "," + list.get(i).getDuan() + "," + list.get(i).getDuanNo() + ","
                    + list.get(i).getFanzhuan()+ list.get(i).getPai() + "\n";
            str = str + content;
        }
        return str;
    }

    public static String getAllshengchan() {
        String content = "";//A620000942422
        List<DetonatorTypeNew> list = getDaoSession().getDetonatorTypeNewDao().loadAll();
        String str = "";
        Log.e("生产", "list.size() : " + list.size());
        for (int i = 0; i < list.size(); i++) {
            content = list.get(i).getShellBlastNo() + ","
                    + list.get(i).getShellBlastNo() + "," +
                    list.get(i).getDetonatorId() + list.get(i).getZhu_yscs() +"0"+ ","
                    + list.get(i).getTime() + "\n";
            str = str + content;
        }
        return str;
    }

    //丹灵下载后更新雷管芯片码//在线下载,离线下载
    public static void updateLgState(DanLingBean.LgsBean.LgBean lgBean,String yxq) {

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
    private static void registerDetonator_typeNew(DenatorBaseinfo leiguan) {
//        getDaoSession().getDetonatorTypeNewDao().deleteAll();//读取生产数据前先清空旧的数据
        // 检查重复数据
        if (checkRepeatShellBlastNo_typeNew(leiguan.getShellBlastNo())) {
            return;
        }
        // 雷管类型_新
        DetonatorTypeNew detonatorTypeNew = new DetonatorTypeNew();
        detonatorTypeNew.setShellBlastNo(leiguan.getShellBlastNo());
        detonatorTypeNew.setDetonatorId(leiguan.getDenatorId());
        detonatorTypeNew.setZhu_yscs(leiguan.getZhu_yscs());
        detonatorTypeNew.setCong_yscs(leiguan.getCong_yscs());
        //牵扯到删除问题(自动删除历史记录的时候,删除相同的下载数据,所以时间是22-10-20)
        detonatorTypeNew.setTime(leiguan.getRegdate().substring(0, 8));
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
     * 清空授权数据
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
//        return mShouquanDao.queryBuilder().orderDesc(ShouQuanDao.Properties.Id).list();
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
     * 修改雷管延时
     *
     * @param db 雷管信息
     */
    public void updateDetonator(DenatorBaseinfo db) {
        Log.e("更新雷管信息", "db: "+db );
        DenatorBaseinfo entity = mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Id.eq(db.getId()))
                .build()
                .unique();
//        entity.setId(db.getId());
        entity.setBlastserial(db.getBlastserial());
        entity.setSithole(db.getSithole());
        entity.setDuanNo(db.getDuanNo());
        entity.setDelay(db.getDelay());
        Log.e("更新数据", "getDuanNo: "+ db.getDuanNo());
        mDeantorBaseDao.update(entity);
    }

    /**
     * 修改雷管延时
     *
     * @param shell 管壳号
     * @param delay 延时
     */
    public void updateDetonatorDelay(String shell, int delay,int duanNo) {
        DenatorBaseinfo entity = mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(shell))
                .build()
                .unique();
        entity.setDelay(delay);
        entity.setDuanNo(duanNo);
        mDeantorBaseDao.update(entity);
    }
    public void updateDetonatorDelay(long id ,String shell, int delay,int duanNo) {
        DenatorBaseinfo entity = mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Id.eq(id))
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(shell))
                .build()
                .unique();
        entity.setDelay(delay);
        entity.setDuanNo(duanNo);
        mDeantorBaseDao.update(entity);
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
     * 删除某一发雷管
     */
    public void deleteDetonator(long id) {
        DenatorBaseinfo entity = mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Id.eq(id))
                .unique();
        mDeantorBaseDao.delete(entity);
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
     * 查询雷管 按区域、排正序排列
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public List<DenatorBaseinfo> queryDetonatorRegionAsc(String piece) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.notEq(""))
                .orderAsc(DenatorBaseinfoDao.Properties.Piece,DenatorBaseinfoDao.Properties.Pai)
                .list();
    }

    /**
     * 查询ShellBlastNo不为空的所有雷管  按区域、排正序排列
     */
    public List<DenatorBaseinfo> queryAllDetonator() {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.notEq(""))
                .orderAsc(DenatorBaseinfoDao.Properties.Piece,DenatorBaseinfoDao.Properties.Pai)
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
     *
     * @param idList:区域页面多选的id
     * @return
     */
    public List<DenatorBaseinfo> queryDetonatorRegionAscNew(List<Integer> idList) {
        List<DenatorBaseinfo> mListData = new ArrayList<>();
        for (int id : idList) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionAsc(String.valueOf(id)));
        }
        return mListData;
    }

    /**
     * 查询出已组网区域的雷管总数量
     * @param idList: 已组网的区域id
     * @return: 已组网区域的雷管总数量
     */
    public int queryDetonatorNumAscNew(List<Integer> idList) {
        List<DenatorBaseinfo> mListData = new ArrayList<>();
        for (int id : idList) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionAsc(String.valueOf(id)));
        }
        return mListData.size();
    }

    /**
     * 查询雷管 区域正序(序号)
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public List<DenatorBaseinfo> queryDetonatorRegionAndDUanAsc(String piece, int duan) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }

    /**
     * 查询雷管 区域倒序(序号)
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public List<DenatorBaseinfo> queryDetonatorRegionDesc(String piece) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.notEq(""))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }

    /**
     * 查询雷管 按组网区域倒序(序号)
     * 空雷管不计入总数
     * @param piece 区域号 1 2 3 4 5
     */
    public List<DenatorBaseinfo> queryDetonatorRegionDescNew(String piece) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.notEq(""))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }

    public List<DenatorBaseinfo> queryDetonatorRegionDesc() {
        return mDeantorBaseDao
                .queryBuilder()
                .orderDesc(DenatorBaseinfoDao.Properties.Id)
                .list();
    }

    /**
     * 查询所有已组网区域的雷管数据（按雷管id倒序排列）
     * @param idList:区域页面多选的id
     * @return
     */
    public List<DenatorBaseinfo> queryDetonatorRegionDescNew(List<Integer> idList) {
        List<DenatorBaseinfo> mListData = new ArrayList<>();
        for (int id : idList) {
            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDescNew(String.valueOf(id)));
        }
        return mListData;
    }

    /**
     * 查询雷管 区域倒序(序号)
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public List<DenatorBaseinfo> queryDetonatorRegionDesc(int duan,String piece) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }
    public List<DenatorBaseinfo> queryDetonatorRegionAsc(int duan,String piece) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
    }

//    public List<DenatorBaseinfo> queryDetonatorRegionDesc() {
//        List<DenatorBaseinfo> mListData = new ArrayList<>();
//        boolean mRegion1 = (boolean) MmkvUtils.getcode("mRegion1", true);//是否选中区域1
//        boolean mRegion2 = (boolean) MmkvUtils.getcode("mRegion2", true);//是否选中区域2
//        boolean mRegion3 = (boolean) MmkvUtils.getcode("mRegion3", true);//是否选中区域3
//        boolean mRegion4 = (boolean) MmkvUtils.getcode("mRegion4", true);//是否选中区域4
//        boolean mRegion5 = (boolean) MmkvUtils.getcode("mRegion5", true);//是否选中区域5
//        if (mRegion5) {
//            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("5"));
//        }if (mRegion4) {
//            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("4"));
//        }if (mRegion3) {
//            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("3"));
//        }if (mRegion2) {
//            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("2"));
//        }if (mRegion1) {
//            mListData.addAll(new GreenDaoMaster().queryDetonatorRegionDesc("1"));
//        }
//        return mListData;
//    }

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

    public int getPieceMaxNum(int duan ,String piece) {
        // 倒叙查询
        List<DenatorBaseinfo> mList = queryDetonatorRegionDesc(duan,piece);

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
     * 获取 该区域 最大的延时
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceMaxNumDelay(String piece) {
        int delay;
        String sql = "select max(delay) from denatorBaseinfo where  piece = "+piece;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            delay = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceMaxNumDelay", "获取最大序号 的延时: "+delay);
            return delay;
        }else {
            Log.e("getPieceMaxNumDelay", "获取最大序号 的延时: 0");
            return 0;
        }
    }
    /**
     * 获取 该区域 最大序号 的延时
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceMaxNumDelay(int duan , String piece) {

        // 倒叙查询
        List<DenatorBaseinfo> mList = queryDetonatorRegionDesc(duan,piece);

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
     * 获取 该区域 最大序号 的延时
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceMinNoDelay(int duan , String piece) {

        // 倒叙查询
        List<DenatorBaseinfo> mList = queryDetonatorRegionAsc(duan,piece);
        Log.e("延时", "mList: "+mList.toString());
        Log.e("延时", "duan: "+duan);
        Log.e("延时", "piece: "+piece);
        // 如果有数据
        if (mList.size() > 0) {
            // 第一个雷管数据 该区域 最大序号 的延时
            int delay = mList.get(0).getDelay();
            Log.e("getPieceMinNoDelay", "获取最小序号 的延时: " + delay);
            return delay;
            // 如果没数据
        } else {
            Log.e("getPieceMinNoDelay", "获取最大序号 的延时: 0");
            return 0;
        }

    }

    /**
     * 获取 该区域 最小序号 的延时
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceMinNumDelay(int duan , String piece) {
        int delay;
        String sql = "select min(delay) from denatorBaseinfo where duan = "+duan + " and piece = "+piece;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            delay = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceMaxNumDelay", "获取最小序号 的延时1: "+delay);
            return delay;
        }else {
            Log.e("getPieceMaxNumDelay", "获取最小序号 的延时2: 0");
            return 0;
        }

    }

    /**
     * 获取 该区域 最小序号 的延时
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceMaxDuan(String piece) {
        int duan;
        String sql = "select max(duan) from denatorBaseinfo where piece = "+piece;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            duan = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceMaxNumDelay", "获取当前区域最大段号: "+duan);
            return duan;
        }else {
            Log.e("getPieceMaxNumDelay", "获取最小序号 的延时: 1");
            return 1;
        }

    }

    /**
     * 获取 该段 最大序号 的段序号
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceMaxDuanNo(int duan ,String piece) {
        int duanNo;
        String sql = "select max(duanNo) from denatorBaseinfo where duan = "+duan + " and piece = "+piece;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            duanNo = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceMaxDuanNo", "获取最大序号1: "+duanNo);
            return duanNo;
        }else {
            Log.e("getPieceMaxDuanNo", "获取最大序号2 的延时: 1");
            return 1;
        }
//        // 倒叙查询
//        List<DenatorBaseinfo> mList = queryDetonatorRegionDesc(duan,piece);
//
//        // 如果有数据
//        if (mList.size() > 0) {
//            // 第一个雷管数据 该区域 最大序号 的延时
//            String duanNo = mList.get(0).getDuanNo();
//            Log.e("getPieceMaxNumDelay", "获取最大序号 的延时: " + duanNo);
//            return Integer.parseInt(duanNo);
//            // 如果没数据
//        } else {
//            Log.e("getPieceMaxNumDelay", "获取最大序号 的延时: 0");
//            return 0;
//        }
    }

    /**
     * 获取 该段 最大序号 的段序号
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPaiMaxDuanNo(int blastserial ,String piece,int pai) {
        Log.e("查询", "blastserial: "+blastserial +" piece: "+piece+" pai: "+pai);
        int duanNo;
        String sql = "select max(duanNo) from denatorBaseinfo where blastserial = "+blastserial + " and piece = "+piece+ " and pai = "+pai;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            duanNo = cursor.getInt(0);
            cursor.close();
            Log.e("getPaiMaxDuanNo", "获取最大序号1: "+duanNo);
            return duanNo;
        }else {
            Log.e("getPaiMaxDuanNo", "获取最大序号2 的延时: 1");
            return 1;
        }
    }


    /**
     * 删除生产数据中的雷管
     */
    public void deleteTypeLeiGuan(String time) {
        QueryBuilder<DetonatorTypeNew> result = detonatorTypeNewDao.queryBuilder();
        result.where(DetonatorTypeNewDao.Properties.Time.le(time)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
    /**
     * 删除生产数据中的雷管
     */
    public  void deleteTypeLeiGuanFroTime(String time) {
        Log.e("删除生产数据中的雷管", "time: "+time );
        QueryBuilder<DetonatorTypeNew> result = detonatorTypeNewDao.queryBuilder();
        result.where(DetonatorTypeNewDao.Properties.Time.eq(time)).buildDelete().executeDeleteWithoutDetachingEntities();
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

    public DetonatorTypeNew serchDenatorId(String shellBlastNo) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryShellBlastNoTypeNew(shellBlastNo);
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

    public static void setFactory() {
        List<Defactory> msg = getDaoSession().getDefactoryDao().loadAll();
        if (msg.size() == 0) {
            Defactory message = new Defactory();
            message.setDeName("scyb");
            message.setDeEntCode("56");
            message.setDeFeatureCode("H");
            message.setIsSelected("是");
            getDaoSession().getDefactoryDao().insert(message);
        }
    }

    /***
     * @param duan
     * @return
     */
    public List<DenatorBaseinfo> queryLeiguanDuan(int duan, String mRegion) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        return result.where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();//.orderAsc(DenatorBaseinfoDao.Properties.Delay)
    }
    public List<DenatorBaseinfo> queryLeiguanPai(int pai, String mRegion) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        return result.where(DenatorBaseinfoDao.Properties.Pai.eq(pai))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();//.orderAsc(DenatorBaseinfoDao.Properties.Delay)
    }
    public List<DenatorBaseinfo> queryLeiguanPaiDesc(int pai, String mRegion) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        return result.where(DenatorBaseinfoDao.Properties.Pai.eq(pai))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();//.orderAsc(DenatorBaseinfoDao.Properties.Delay)
    }
    /***
     * @param pai
     * @return
     */
    public List<DenatorBaseinfo> queryLeiguanPai(int pai, String mRegion,String fz) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        Log.e("查询", "段位雷管: ");
        return result.where(DenatorBaseinfoDao.Properties.Pai.eq(pai))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .where(DenatorBaseinfoDao.Properties.Fanzhuan.eq(fz))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();//.orderAsc(DenatorBaseinfoDao.Properties.Delay)
    }
    public List<DenatorBaseinfo> queryLeiguanDuanDesc(int duan, String mRegion) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        return result.where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();//.orderAsc(DenatorBaseinfoDao.Properties.Delay)
    }
    /***
     * @param duan
     * @return
     */
    public List<DenatorBaseinfo> queryLeiguanDuan(int duan, String mRegion,String fz) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        Log.e("查询", "段位雷管: ");
        return result.where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .where(DenatorBaseinfoDao.Properties.Fanzhuan.eq(fz))
                .orderDesc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();//.orderAsc(DenatorBaseinfoDao.Properties.Delay)
    }


    public List<DenatorBaseinfo> queryLeiguanDuanforDelay(int duan, String mRegion) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        return result.where(DenatorBaseinfoDao.Properties.Duan.eq(duan))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();//.orderAsc(DenatorBaseinfoDao.Properties.Delay)
    }

    /***
     * @param duanNo
     * @return
     */
    public List<DenatorBaseinfo> queryLeiguanDuanNo(String duanNo, String mRegion) {
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();

        return result.where(DenatorBaseinfoDao.Properties.DuanNo.eq(duanNo))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion)).list();
    }

    /**
     * 检查重复雷管
     */
    public int getDuan(String shellBlastNo) {
        DenatorBaseinfo a = mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.eq(shellBlastNo))
                .unique();
        if (a != null) {
            return a.getDuan();
        } else {
            return 1;
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


//------授权用--------/

    /**
     * 查询生产库中雷管
     */
    public List<DetonatorTypeNew> queryDetonatorShouQuan() {
        return detonatorTypeNewDao
                .queryBuilder()
                .orderDesc(DetonatorTypeNewDao.Properties.Id)
                .list();
    }
    public List<DetonatorTypeNew> queryDetonatorShouQuan(String sqrq) {
        return detonatorTypeNewDao
                .queryBuilder()
                .where(DetonatorTypeNewDao.Properties.Time.eq(sqrq))
                .orderDesc(DetonatorTypeNewDao.Properties.Id)
                .list();
    }
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
                .where(DetonatorTypeNewDao.Properties.ShellBlastNo.like("%" + gkm+"%"))
//                .whereOr(DetonatorTypeNewDao.Properties.Time.eq(sqrq),DetonatorTypeNewDao.Properties.ShellBlastNo.like("%" + gkm+"%"))
                .orderDesc(DetonatorTypeNewDao.Properties.Id)
                .list();
    }
    public List<DetonatorTypeNew> queryDetonatorShouQuan2() {
        return detonatorTypeNewDao
                .queryBuilder()
                .where(DetonatorTypeNewDao.Properties.Qibao.eq("雷管正常"))
                .orderDesc(DetonatorTypeNewDao.Properties.Id)
                .list();
    }
    public List<DetonatorTypeNew> queryDetonatorShouQuan2(String sqrq) {
        return detonatorTypeNewDao
                .queryBuilder()
                .where(DetonatorTypeNewDao.Properties.Qibao.eq("雷管正常"))
                .where(DetonatorTypeNewDao.Properties.Time.eq(sqrq))
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
        Log.e("更新生产库中的起爆状态", "shell: " + shell);
        if(entity!=null&& (!TextUtils.isEmpty(entity.getQibao()) && entity.getQibao().equals("雷管正常"))){
            Log.e("更新生产库中的起爆状态", "entity: "+entity.toString() );
            entity.setQibao(qibao);
            detonatorTypeNewDao.update(entity);
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

    /**
     * 更新授权数量
     */
    public  void updataShouQuan(String time,int total) {
        QueryBuilder<ShouQuan> result = mShouquanDao.queryBuilder();
        ShouQuan shouQuan =result.where(ShouQuanDao.Properties.Spare2.eq(time)).unique();
        shouQuan.setTotal(total);
        mShouquanDao.update(shouQuan);
    }
    /**
     * 更新授权数量
     */
    public  void updataShouQuan(String time) {
        QueryBuilder<ShouQuan> result = mShouquanDao.queryBuilder();
        List<DetonatorTypeNew> list_total =queryDetonatorShouQuanForSqrq(time);
        if(time.length()==0){
            return;
        }
        Log.e("更新授权数量", "time: "+time );
        Log.e("更新授权数量", "list_total: "+list_total );
        List<ShouQuan> list_sq=result.where(ShouQuanDao.Properties.Spare2.eq(time)).list();
        for (int a=0;a<list_sq.size();a++){
            list_sq.get(a).setTotal(list_total.size());
            if(list_total.size()==0){
                mShouquanDao.delete(list_sq.get(a));
            }else {
                mShouquanDao.update(list_sq.get(a));
            }
        }

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
     * 删除授权数据
     */
    public  void deleteShouQuan(String time) {
        QueryBuilder<ShouQuan> result = mShouquanDao.queryBuilder();
        result.where(ShouQuanDao.Properties.Spare2.lt(time)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 查询超时的雷管
     */
    public  List<DenatorBaseinfo> queryLeiGuan(String time,String mRegion) {
        Log.e("查询超时的雷管", "time: "+time );
        QueryBuilder<DenatorBaseinfo> result = mDeantorBaseDao.queryBuilder();
        return result.where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.notEq(""))
                .where(DenatorBaseinfoDao.Properties.Regdate.lt(time))
                .orderAsc(DenatorBaseinfoDao.Properties.Pai)
                .list();
    }

    /**
     * 查询已组网区域超时的雷管
     */
    public  List<DenatorBaseinfo> queryLeiGuanZqRegion(String time,List<Integer> idList) {
        Log.e("查询超时的雷管", "time: "+time);
        List<DenatorBaseinfo> mListData = new ArrayList<>();
        for (int id : idList) {
            mListData.addAll(new GreenDaoMaster().queryLeiGuan(time,String.valueOf(id)));
        }
        return mListData;
    }



    public DenatorHis_Main queryDetonatorForMainHis(String time) {
        return denatorHis_mainDao
                .queryBuilder()
                .where(DenatorHis_MainDao.Properties.Blastdate.eq(time))
                .unique();
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

    /**
     * 检查重复历史记录
     */
    public long checkRepeatHis(String time) {
        return denatorHis_mainDao
                .queryBuilder()
                .where(DenatorHis_MainDao.Properties.Blastdate.eq(time))
                .count();
    }

    /**
     * 检查重复历史记录
     */
//    public DenatorHis_Main checkRepeatHis(String time) {
//        return denatorHis_mainDao
//                .queryBuilder()
//                .where(DenatorHis_MainDao.Properties.Blastdate.eq(time))
//                .unique();
//    }

    /**
     * 程序日志上传页面-删除单条日志记录
     * @param filename:日志文件名
     * @return
     */
    public List<SysLog> deleteAppLogsByName(String filename) {
        // 提取前缀（假设前缀是 filename 的前8个字符，可以根据需求调整）
        String prefix = filename.substring(0, 10);
        // 查询所有以该前缀开头的日志记录
        return sysLogDao.queryBuilder()
                .where(SysLogDao.Properties.Filename.like(prefix + "%"))  // 使用前缀进行匹配
                .list();
    }

    /**
     * 程序日志上传页面-删除单条日志记录
     * @param id:日志文件id
     * @return
     */
    public List<SysLog> deleteAppLogsById(Long id) {
        // 查询所有以该前缀开头的日志记录
        return sysLogDao.queryBuilder()
                .where(SysLogDao.Properties.Id.eq(id))  // 使用前缀进行匹配
                .list();
    }

    /**
     * 程序日志上传页面-删除单条日志记录
     * @param id:日志文件id
     * @return
     */
    public List<ErrLog> deleteAppErrorLogsById(Long id) {
        // 查询所有以该前缀开头的日志记录
        return errLogDao.queryBuilder()
                .where(ErrLogDao.Properties.Id.eq(id))  // 使用前缀进行匹配
                .list();
    }

    /**
     * 程序日志上传页面-更新日志上传状态
     * @param id:当前条目日志记录数据库的id
     */
    public void updateAppLog(Long id) {
        // 提取日期部分（yyyy-MM-dd）
//        String date = dateTime.split(" ")[0];  // 以空格分隔，取第一部分即为 "2024-12-24"
        // 按日期前缀和更新时间排序查询
        List<SysLog> logs = sysLogDao.queryBuilder()
                .where(SysLogDao.Properties.Id.eq(id))
                // 使用 LIKE 操作符匹配 updateTime 字段的前 10 个字符 (即 yyyy-MM-dd 部分)
//                .where(SysLogDao.Properties.UpdataTime.like(date + "%"))  // 按更新时间降序排列
                .list();
        // 遍历查询结果并更新 upState 字段
        for (SysLog log : logs) {
            if (!TextUtils.isEmpty(log.getUpdataTime())) {
                    log.setUpdataState("已上传");  // 设置状态为“已上传”
            }
        }
        // 在事务中批量更新日志记录
        sysLogDao.updateInTx(logs);  //
    }

    /**
     * 程序日志上传页面-更新日志上传状态
     * @param id:当前条目日志记录数据库的id
     */
    public void updateErrorAppLog(Long id) {
        // 提取日期部分（yyyy-MM-dd）
//        String date = dateTime.split(" ")[0];  // 以空格分隔，取第一部分即为 "2024-12-24"
        // 按日期前缀和更新时间排序查询
        List<ErrLog> logs = errLogDao.queryBuilder()
                .where(ErrLogDao.Properties.Id.eq(id))
                .list();
        // 遍历查询结果并更新 upState 字段
        for (ErrLog log : logs) {
            if (!TextUtils.isEmpty(log.getUpdataTime())) {
                    log.setUpdataState("已上传");  // 设置状态为“已上传”
            }
        }
        // 在事务中批量更新日志记录
        errLogDao.updateInTx(logs);  //
    }

    /**
     * 程序日志上传页面-查询日志记录
     * @return
     */
    public List<SysLog> getAppLogList() {
        // 按日期前缀和更新时间排序查询
        List<SysLog> sysLogs = sysLogDao.queryBuilder()
                .orderDesc(SysLogDao.Properties.UpdataTime) // 按更新时间降序排列
                .list();
        // 存储去重后的日志
        List<SysLog> list = new ArrayList<>();
        for (SysLog log : sysLogs) {
            /**
             * 之前存储在sysLog表中的日志UpdataTime是24-10-29 最新的日志UpdataTime是2024-10-29
             * 所以先排除掉之前的旧数据
             */
            if (!TextUtils.isEmpty(log.getUpdataTime())) {
                // 如果没有该日期前缀的记录，或者遇到较新的记录，则更新
                list.add(log);
            }
        }
        return list;
    }

    /**
     * 程序日志上传页面-查询日志记录
     * @return
     */
    public List<ErrLog> getAppErrorLogList() {
        // 按日期前缀和更新时间排序查询
        List<ErrLog> errLogs = errLogDao.queryBuilder()
                .orderDesc(ErrLogDao.Properties.UpdataTime) // 按更新时间降序排列
                .list();
        List<ErrLog> list = new ArrayList<>();
        for (ErrLog log : errLogs) {
            list.add(log);
        }
        return errLogDao.queryBuilder()
                .orderDesc(ErrLogDao.Properties.UpdataTime) // 按更新时间降序排列
                .list();
    }

    /**
     * 根据申请日期查询生产库中雷管
     */
    public List<QuYu> queryQuYu() {
        return quyuDao.queryBuilder()
                .list();
    }

    /**
     * 获取 该区域 最大排号
     *
     */
    public int getPieceMaxPai(String qyid) {
        int pai;
        String sql = "select max(paiId) from PaiData where qyid = "+qyid;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            pai = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceMaxNumDelay", "获取当前区域最大排号: "+pai);
            return pai;
        }else {
            Log.e("getPieceMaxNumDelay", "获取当前区域最大排号: 0");
            return 0;
        }

    }

    /**
     * 获取 该区域 最大排号
     *
     */
    public int getPaisum(String qyid) {
        QueryBuilder<PaiData> result = getDaoSession().getPaiDataDao().queryBuilder();
        result = result.where(PaiDataDao.Properties.Qyid.eq(qyid));
        return result.list().size();

    }
    /**
     * 根据区域更新所有排数据
     */
    public void updataPaiFroPiace(String qyid) {
        List<PaiData> result = getDaoSession().getPaiDataDao().queryBuilder().where(PaiDataDao.Properties.Qyid.eq(qyid)).list();
        for (PaiData item : result) {
            item.setSum(0+"");
            getDaoSession().getPaiDataDao().update(item);
        }
    }

    /**
     * 获取 该区域 最小的延时
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceMinNumDelay(String piece) {
        int delay;
        String sql = "select min(delay) from denatorBaseinfo where piece = "+piece;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            delay = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceMinNumDelay", "获取最小序号 的延时: "+delay);
            return delay;
        }else {
            Log.e("getPieceMinNumDelay", "获取最小序号 的延时: 0");
            return 0;
        }
    }
    /**
     * 获取 该区域 最大的id
     */
    public int getPieceMaxqyid() {
        int id;
        String sql = "select max(qyid) from QuYu  ";
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            id = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceMinNumDelay", "获取最大序号 : "+id);
            return id;
        }else {
            Log.e("getPieceMinNumDelay", "获取最大序号: 0");
            return 0;
        }
    }

    /**
     * 查询对应的区域
     *
     * @return
     */
    public static QuYu geQuyu(String quyuId) {

        return getDaoSession().getQuYuDao().queryBuilder().where(QuYuDao.Properties.Qyid.eq(quyuId)).unique();
    }
    /**
     * 查询对应的排
     *
     * @return
     */
    public static PaiData gePaiData(String mRegion,String paiId) {
        return getDaoSession().getPaiDataDao().queryBuilder()
                .where(PaiDataDao.Properties.PaiId.eq(paiId))
                .where(PaiDataDao.Properties.Qyid.eq(mRegion)).unique();
    }

    /**
     * 查询雷管 按排号查询
     */
    public  List<DenatorBaseinfo> queryDetonatorPai(String mRegion,int pai) {
        QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
        result = result.where(DenatorBaseinfoDao.Properties.Pai.eq(pai))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .orderAsc(DenatorBaseinfoDao.Properties.Id);
        return result.list();
    }

    /**
     * 查询雷管 按排号查询
     */
    public  List<DenatorBaseinfo> queryDetonatorPaiDesc(String mRegion,int pai) {
        QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
        result = result.where(DenatorBaseinfoDao.Properties.Pai.eq(pai))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .orderDesc(DenatorBaseinfoDao.Properties.Id);
        return result.list();
    }
    /**
     * 查询雷管 按区域,排号,孔号查询
     */
    public  DenatorBaseinfo queryDetonatorPaiAndKong(String mRegion,int pai,int kong) {
        QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
        return result.where(DenatorBaseinfoDao.Properties.Pai.eq(pai))
                .where(DenatorBaseinfoDao.Properties.Blastserial.eq(kong))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .unique();
    }
    /**
     * 查询雷管 按区域,排号,孔号,位查询
     */
    public  DenatorBaseinfo queryDetonatorPaiAndKongAndWei(String mRegion,int pai,int kong,int wei) {
        QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
        return result.where(DenatorBaseinfoDao.Properties.Pai.eq(pai))
                .where(DenatorBaseinfoDao.Properties.Blastserial.eq(kong))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .where(DenatorBaseinfoDao.Properties.DuanNo.eq(wei))
                .unique();
    }


    /**
     * 查询雷管 按排号查询
     */
    public  List<DenatorBaseinfoSelect> queryDetonatorPaiSelect(String mRegion,int pai) {
        QueryBuilder<DenatorBaseinfo> result = getDaoSession().getDenatorBaseinfoDao().queryBuilder();
        result = result.where(DenatorBaseinfoDao.Properties.Pai.eq(pai))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial);

        List<DenatorBaseinfoSelect> mchildList = new ArrayList<>();
        for (DenatorBaseinfo item : result.list()) {
            DenatorBaseinfoSelect childData = new DenatorBaseinfoSelect();
            childData.setId(item.getId());
            childData.setBlastserial(item.getBlastserial());
            childData.setSithole(item.getSithole());
            childData.setShellBlastNo(item.getShellBlastNo());
            childData.setDenatorId(item.getDenatorId());
            childData.setDelay(item.getDelay());
            childData.setRegdate(item.getRegdate());
            childData.setStatusCode(item.getStatusCode());
            childData.setStatusName(item.getStatusName());
            childData.setErrorCode(item.getErrorCode());
            childData.setErrorName(item.getErrorName());
            childData.setAuthorization(item.getAuthorization());
            childData.setRemark(item.getRemark());
            childData.setWire(item.getWire());//桥丝状态
            childData.setName(item.getName());
            childData.setDenatorIdSup(item.getDenatorIdSup());
            childData.setZhu_yscs(item.getZhu_yscs());
            childData.setCong_yscs(item.getCong_yscs());
            childData.setPiece(item.getPiece());
            childData.setDuan(item.getDuan());
            childData.setDuanNo(item.getDuanNo());
            childData.setFanzhuan(item.getFanzhuan());
            childData.setPai(item.getPai());
            if (!mchildList.contains(childData)) {
                mchildList.add(childData);
            }
        }
        return mchildList;
    }

    /**
     * 查询排
     */
    public  List<PaiData> queryPai(String qyId) {
        QueryBuilder<PaiData> result = getDaoSession().getPaiDataDao().queryBuilder();
        result = result.where(PaiDataDao.Properties.Qyid.eq(qyId));
        return result.list();
    }
    public  List<PaiDataSelect> queryPaiSelect(String qyId) {
        QueryBuilder<PaiData> result = getDaoSession().getPaiDataDao()
                .queryBuilder()
                .orderAsc(PaiDataDao.Properties.PaiId);  // 排号升序排序;
        result = result.where(PaiDataDao.Properties.Qyid.eq(qyId));
        List<PaiDataSelect> mPaiList = new ArrayList<>();
        for (PaiData item : result.list()) {
            PaiDataSelect paiData = new PaiDataSelect();
            paiData.setId(item.getId());
            paiData.setPaiId(item.getPaiId());
            paiData.setQyid(item.getQyid());
            paiData.setSum(item.getSum());
            paiData.setDelayMin(item.getDelayMin());
            paiData.setDelayMax(item.getDelayMax());
            paiData.setShouquan(item.getShouquan());
            paiData.setStartDelay(item.getStartDelay());
            paiData.setKongDelay(item.getKongDelay());
            paiData.setNeiDelay(item.getNeiDelay());
            paiData.setPaiDelay(item.getPaiDelay());
            paiData.setKongNum(item.getKongNum());
            paiData.setDiJian(item.getDiJian());
            if (!mPaiList.contains(paiData)) {
                mPaiList.add(paiData);
            }
        }
        return mPaiList;
    }

    /**
     * 获取 该区域 最大的paiId
     * @param qyid 区域号
     */
    public int getMaxPaiId(String qyid) {
        int paiId;
        String sql = "select max(paiId) from PaiData where qyid = "+qyid;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            paiId = cursor.getInt(0);
            cursor.close();
            Log.e("getMaxPaiId", "获取最大排号: "+paiId);
            return paiId;
        }else {
            Log.e("getMaxPaiId", "获取最大排号: 0");
            return 0;
        }
    }
    /**
     * 获取 该区域 最大的paiId
     * @param qyid 区域号
     */
    public int getMinPaiId(String qyid) {
        int paiId;
        String sql = "select min(paiId) from PaiData where qyid = "+qyid;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            paiId = cursor.getInt(0);
            cursor.close();
            Log.e("getMaxPaiId", "获取最小排号: "+paiId);
            return paiId;
        }else {
            Log.e("getMaxPaiId", "获取最小排号: 0");
            return 0;
        }
    }

    /**
     * 查询雷管数量
     * @return
     */
    public int queryDetonatorSize(String piece) {
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.notEq(""))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list().size();
    }

    /**
     * 查询当前区域当前排雷管数量
     * @param piece:区域id
     * @param paiId:排id
     * @return
     */
    public int queryDetonatorByPaiSize(String piece,String paiId) {
        List<DenatorBaseinfo> list  = mDeantorBaseDao.queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                .where(DenatorBaseinfoDao.Properties.ShellBlastNo.notEq(""))
                .orderAsc(DenatorBaseinfoDao.Properties.Blastserial)
                .list();
        if(list != null){
            return mDeantorBaseDao.queryBuilder()
                    .where(DenatorBaseinfoDao.Properties.Pai.eq(paiId))
                    .where(DenatorBaseinfoDao.Properties.ShellBlastNo.notEq(""))
                    .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
                    .list().size();
        } else {
            return 0;
        }
    }

    /**
     * 查询雷管数量
     * @return
     */
    public int queryDetonatorPaiSize(String mRegion,String paiId) {
        List<DenatorBaseinfo> list = mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Pai.eq(paiId))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                .list();
        if(list!=null){
            return mDeantorBaseDao
                    .queryBuilder()
                    .where(DenatorBaseinfoDao.Properties.Pai.eq(paiId))
                    .where(DenatorBaseinfoDao.Properties.Piece.eq(mRegion))
                    .list().size();
        }else {
            return 0;
        }

    }

    /**
     * 查询雷管数量
     * @return
     */
    public DenatorBaseinfo serchDenatorIdForChoice(int paiChoice,int kongChoice) {
        return mDeantorBaseDao.queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Pai.eq(paiChoice))
                .where(DenatorBaseinfoDao.Properties.Blastserial.eq(kongChoice)).unique();

    }

    public DenatorBaseinfo serchDenatorId(int paiChoice,int kongChoice) {
        return mDeantorBaseDao.queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Pai.eq(paiChoice))
                .where(DenatorBaseinfoDao.Properties.Blastserial.eq(kongChoice)).unique();

    }

    /**
     * 删除所有区域
     */
    public void deleteQuYu() {
        quyuDao.queryBuilder().buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删除所有区域
     */
    public void deleteQuYuForId(int id) {

        QuYu entity= quyuDao
                .queryBuilder()
                .where(QuYuDao.Properties.Qyid.eq(id))
                .unique();
        quyuDao.delete(entity);
    }

    /**
     * 删除所有排
     */
    public void deletePai() {
        paiDataDao.queryBuilder().buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 获取 该区域 该排 最大的延时
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceAndPaiMaxDelay(String piece,int pai) {
        int delay;
        String sql = "select max(delay) from denatorBaseinfo where  piece = "+piece+" and pai = "+pai;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            delay = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceAndPaiMaxDelay", "获取 该区域 该排 最大的延时: "+delay);
            return delay;
        }else {
            Log.e("getPieceAndPaiMaxDelay", "获取 该区域 该排 最大的延时: 0");
            return 0;
        }
    }

    public int getPieceAndPaiAndKongMaxDelay(String piece,int pai,int kong) {
        int delay;
        String sql = "select max(delay) from denatorBaseinfo where  piece = "+piece+" and pai = "+pai+" and blastserial = "+kong;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            delay = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceAndPaiMaxDelay", "获取 该区域 该排 最大的延时: "+delay);
            return delay;
        }else {
            Log.e("getPieceAndPaiMaxDelay", "获取 该区域 该排 最大的延时: 0");
            return 0;
        }
    }

    /**
     * 获取 该区域 该排 最小的延时
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceAndPaiMinDelay(String piece,int pai) {
        int delay;
        String sql = "select min(delay) from denatorBaseinfo where  piece = "+piece+" and pai = "+pai;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            delay = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceAndPaiMinDelay", "获取 该区域 该排 最小的延时: "+delay);
            return delay;
        }else {
            Log.e("getPieceAndPaiMinDelay", "获取 该区域 该排 最小的延时: 0");
            return 0;
        }
    }
    /**
     * 获取 该区域 该排 最小的延时
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceAndPaiAndKongMinDelay(String piece,int pai,int kong) {
        int delay;
        String sql = "select min(delay) from denatorBaseinfo where  piece = "+piece+" and pai = "+pai+" and blastserial = "+kong;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            delay = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceAndPaiMinDelay", "获取 该区域 该排 最小的延时: "+delay);
            return delay;
        }else {
            Log.e("getPieceAndPaiMinDelay", "获取 该区域 该排 最小的延时: 0");
            return 0;
        }
    }

    /**
     * 获取 该区域 该排 最大的孔号
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceAndPaiMaxKong(String piece,int pai) {
        int blastserial;
        String sql = "select max(blastserial) from denatorBaseinfo where  piece = "+piece+" and pai = "+pai;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            blastserial = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceAndPaiMaxKong", "获取该区域 该排的 最大孔号: "+blastserial);
            return blastserial;
        }else {
            Log.e("getPieceAndPaiMaxKong", "获取该区域 该排的 最大孔号: 0");
            return 0;
        }
    }

    /**
     * 获取 该区域 该排 最大的孔号
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getKongCount(String piece,int pai) {
        //先查询出所有雷管  由于孔和位的blastserial一致，所以如果遇到一样的blastserial就只计1个雷管
       List<DenatorBaseinfo> list = mDeantorBaseDao.queryBuilder()
        .where(DenatorBaseinfoDao.Properties.Piece.eq(piece))
               .where(DenatorBaseinfoDao.Properties.Pai.eq(pai)).list();
        // 使用 Set 来去重 blastSerial，确保相同的 blastSerial 只计数一次
        Set<Integer> uniqueBlastSerials = new HashSet<>();
        for (DenatorBaseinfo item : list) {
            uniqueBlastSerials.add(item.getBlastserial());
        }
        // 获取去重后的数量
        return uniqueBlastSerials.size();
    }

    /**
     * 获取 该区域 该排 最小的孔号
     *
     * @param piece 区域号 1 2 3 4 5
     */
    public int getPieceAndPaiMinKong(String piece,int pai) {
        int sithole;
        String sql = "select min(blastserial) from denatorBaseinfo where  piece = "+piece+" and pai = "+pai;
        Cursor cursor = Application.getDaoSession().getDatabase().rawQuery(sql, null);

        if (cursor != null && cursor.moveToNext()) {
            sithole = cursor.getInt(0);
            cursor.close();
            Log.e("getPieceAndPaiMinKong", "获取该区域 该排的 最小孔号: "+sithole);
            return sithole;
        }else {
            Log.e("getPieceAndPaiMinKong", "获取该区域 该排的 最小孔号: 0");
            return 0;
        }
    }

    /**
     * 删除某一排
     */
    public void deletepai(String piece,long id) {
        Log.e("删除某一排", "piece: "+piece+" id:"+id );
        PaiData entity = paiDataDao
                .queryBuilder()
                .where(PaiDataDao.Properties.Id.eq(id))
                .unique();
        paiDataDao.delete(entity);
    }

    /**
     * 根据PaiData表中的paiId、qyid去雷管表中查询当前区域当前排是否还有雷管
     * @param paiId: 排id
     * @param qyid: 区域id
     * @return
     */
    public long queryLgByPai(int paiId,int qyid) {
        return mDeantorBaseDao.queryBuilder()
                .where(DenatorBaseinfoDao.Properties.Pai.eq(String.valueOf(paiId)))
                .where(DenatorBaseinfoDao.Properties.Piece.eq(String.valueOf(qyid)))
                .count();
    }

    /**
     * 获取已组网的区域idList
     */
    public List<Integer> getSelectedQyIdList() {
        // 创建查询条件，获取 selected == "true" 的所有记录
        List<QuYu> quYuList = quyuDao.queryBuilder()
                .where(QuYuDao.Properties.Selected.eq("true"))
                .list();
        List<Integer> qyidList = new ArrayList<>();
        for (QuYu quYu : quYuList) {
            qyidList.add(quYu.getQyid());
        }
        return qyidList;
    }

    /**
     * 查询是否有使用中的项目
     */
    public List<Project> querySelectedProject() {
        // 查询 selected 为 true 的记录
        return mProjectDao.queryBuilder()
                .where(ProjectDao.Properties.Selected.eq("true"))
                .list();
    }
    /**
     * 查询区域有多少孔
     */
    public int querytotalKong(String qyid){
        int maxPai=getMaxPaiId(qyid);
//        Log.e("查询区域有多少孔", "maxPai: "+maxPai );
        int total=0;
        for (int a =1;a<=maxPai;a++){
            total = total+ getPieceAndPaiMaxKong(qyid,a);
//            Log.e("查询区域有多少孔", a+"排孔数: "+total );
        }
        return total;
    }

    /**
     * 查询区域有多少孔
     * 之前是查询每个区域最大孔号  目前改为：查询现有区域所有的孔数量
     */
    public int querytotalKongNew(String qyid){
        int maxPai=getMaxPaiId(qyid);
//        Log.e("查询区域有多少孔", "maxPai: "+maxPai );
        int total=0;
        for (int a =1;a<=maxPai;a++){
            total = total+ getKongCount(qyid,a);
//            Log.e("查询区域有多少孔", a+"排孔数: "+total );
        }
        return total;
    }

    /**
     * 查询生产库中雷管
     */
    public int queryHis(String blastdate,String shuangchuan) {
        if(shuangchuan.equals("否")){
            return denatorHis_detailDao
                    .queryBuilder()
                    .where(DenatorHis_DetailDao.Properties.Blastdate.eq(blastdate))
                    .where(DenatorHis_DetailDao.Properties.ErrorCode.like("F%"))
                    .list().size();
        }else {
            return denatorHis_detailDao
                    .queryBuilder()
                    .where(DenatorHis_DetailDao.Properties.Blastdate.eq(blastdate))
                    .list().size();
        }
    }

    /**
     * 根据区域id更新已起爆的区域状态为true
     * @param idList
     */
    public void updateQyQbStataus(List<Integer> idList) {
        // 执行批量更新
        List<QuYu> quYuList = quyuDao.queryBuilder().where(QuYuDao.Properties.Qyid.in(idList)).list();  // 获取符合条件的记录
        // 遍历并更新每条记录的isQb字段
        for (QuYu quYu : quYuList) {
            quYu.setIsQb("true");  // 设置isQb字段为true
        }
        // 批量保存更新后的记录
        quyuDao.updateInTx(quYuList);  // 使用updateInTx进行批量更新
    }

    /**
     * 根据区域id更新已起爆的区域状态为true
     * @param idList
     */
    public void cancelQyQbStataus(List<Integer> idList) {
        // 执行批量更新
        List<QuYu> quYuList = quyuDao.queryBuilder().where(QuYuDao.Properties.Qyid.in(idList)).list();  // 获取符合条件的记录
        // 遍历并更新每条记录的isQb字段
        for (QuYu quYu : quYuList) {
            quYu.setIsQb("false");  // 设置isQb字段为true
        }
        // 批量保存更新后的记录
        quyuDao.updateInTx(quYuList);  // 使用updateInTx进行批量更新
    }
}
