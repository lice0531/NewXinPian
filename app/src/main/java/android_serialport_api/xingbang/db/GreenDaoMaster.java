package android_serialport_api.xingbang.db;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.content.Context;
import android.os.Message;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;
import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.db.greenDao.DefactoryDao;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.Denator_typeDao;
import android_serialport_api.xingbang.db.greenDao.DetonatorTypeNewDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;

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


    public GreenDaoMaster() {
        this.mDefactoryDao = Application.getDaoSession().getDefactoryDao();
        this.detonatorTypeNewDao = Application.getDaoSession().getDetonatorTypeNewDao();
        this.mDeantorBaseDao = Application.getDaoSession().getDenatorBaseinfoDao();
        this.mProjectDao = Application.getDaoSession().getProjectDao();
        this.mDenatorType = Application.getDaoSession().getDenator_typeDao();
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
        List<DetonatorTypeNew> dt =detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.DetonatorId.eq(detonatorId)).list();
        if(dt.size()>=1){
            return dt.get(0).getShellBlastNo();
        }else {
            return "0";
        }
    }
    /**
     * 通过管壳码获取芯片码
     * */
    public String queryShellBlastNoTypeNew(String shellBlastNo) {
        List<DetonatorTypeNew> dt =detonatorTypeNewDao.queryBuilder().where(DetonatorTypeNewDao.Properties.ShellBlastNo.eq(shellBlastNo)).list();
        if(dt.size()>=1){
            return dt.get(0).getDetonatorId();
        }else {
            return "0";
        }
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
        return mDeantorBaseDao
                .queryBuilder()
                .where(DenatorBaseinfoDao.Properties.DenatorId.eq(detonatorId))
                .list();
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
        String str = "ID,序号,孔号,管壳码,芯片码,延时,读取状态,状态名称,错误名称,错误代码,授权期限,备注,注册日期,桥丝状态,名称\n";
        String content ;
        for(int i=0;i<list.size();i++){
            content = list.get(i).getId() + "," + list.get(i).getBlastserial() + "," + list.get(i).getSithole() + "," + list.get(i).getShellBlastNo() + "," +list.get(i).getDenatorId() + "," + list.get(i).getDelay() + "," + list.get(i).getStatusCode() + "," + list.get(i).getStatusName() + "," + list.get(i).getErrorName()+ "," + list.get(i).getErrorCode() + "," +list.get(i).getAuthorization()+ "," + list.get(i).getRemark() + "," + list.get(i).getRegdate() + "," + list.get(i).getWire() + "," + list.get(i).getName() + "\n";
            str = str + content;
        }
        return str;
    }
}
