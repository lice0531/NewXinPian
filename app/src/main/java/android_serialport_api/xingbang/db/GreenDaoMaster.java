package android_serialport_api.xingbang.db;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.content.Context;
import android.database.Cursor;
import android.os.Message;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;
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
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.DenatorId.like(detonatorId.substring(7)))
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
}
