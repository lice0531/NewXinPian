package android_serialport_api.xingbang.db.greenDao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import android_serialport_api.xingbang.db.Defactory;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DenatorBaseinfo_all;
import android_serialport_api.xingbang.db.DenatorHis_Detail;
import android_serialport_api.xingbang.db.DenatorHis_Detail_all;
import android_serialport_api.xingbang.db.DenatorHis_Main;
import android_serialport_api.xingbang.db.DenatorHis_Main_all;
import android_serialport_api.xingbang.db.Denator_type;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.ErrLog;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.Project;
import android_serialport_api.xingbang.db.ShouQuan;
import android_serialport_api.xingbang.db.SysLog;
import android_serialport_api.xingbang.db.Temporary_database;
import android_serialport_api.xingbang.db.UserMain;

import android_serialport_api.xingbang.db.greenDao.DefactoryDao;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfo_allDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_DetailDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_Detail_allDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_MainDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_Main_allDao;
import android_serialport_api.xingbang.db.greenDao.Denator_typeDao;
import android_serialport_api.xingbang.db.greenDao.DetonatorTypeNewDao;
import android_serialport_api.xingbang.db.greenDao.ErrLogDao;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.db.greenDao.ProjectDao;
import android_serialport_api.xingbang.db.greenDao.ShouQuanDao;
import android_serialport_api.xingbang.db.greenDao.SysLogDao;
import android_serialport_api.xingbang.db.greenDao.Temporary_databaseDao;
import android_serialport_api.xingbang.db.greenDao.UserMainDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig defactoryDaoConfig;
    private final DaoConfig denatorBaseinfoDaoConfig;
    private final DaoConfig denatorBaseinfo_allDaoConfig;
    private final DaoConfig denatorHis_DetailDaoConfig;
    private final DaoConfig denatorHis_Detail_allDaoConfig;
    private final DaoConfig denatorHis_MainDaoConfig;
    private final DaoConfig denatorHis_Main_allDaoConfig;
    private final DaoConfig denator_typeDaoConfig;
    private final DaoConfig detonatorTypeNewDaoConfig;
    private final DaoConfig errLogDaoConfig;
    private final DaoConfig messageBeanDaoConfig;
    private final DaoConfig projectDaoConfig;
    private final DaoConfig shouQuanDaoConfig;
    private final DaoConfig sysLogDaoConfig;
    private final DaoConfig temporary_databaseDaoConfig;
    private final DaoConfig userMainDaoConfig;

    private final DefactoryDao defactoryDao;
    private final DenatorBaseinfoDao denatorBaseinfoDao;
    private final DenatorBaseinfo_allDao denatorBaseinfo_allDao;
    private final DenatorHis_DetailDao denatorHis_DetailDao;
    private final DenatorHis_Detail_allDao denatorHis_Detail_allDao;
    private final DenatorHis_MainDao denatorHis_MainDao;
    private final DenatorHis_Main_allDao denatorHis_Main_allDao;
    private final Denator_typeDao denator_typeDao;
    private final DetonatorTypeNewDao detonatorTypeNewDao;
    private final ErrLogDao errLogDao;
    private final MessageBeanDao messageBeanDao;
    private final ProjectDao projectDao;
    private final ShouQuanDao shouQuanDao;
    private final SysLogDao sysLogDao;
    private final Temporary_databaseDao temporary_databaseDao;
    private final UserMainDao userMainDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        defactoryDaoConfig = daoConfigMap.get(DefactoryDao.class).clone();
        defactoryDaoConfig.initIdentityScope(type);

        denatorBaseinfoDaoConfig = daoConfigMap.get(DenatorBaseinfoDao.class).clone();
        denatorBaseinfoDaoConfig.initIdentityScope(type);

        denatorBaseinfo_allDaoConfig = daoConfigMap.get(DenatorBaseinfo_allDao.class).clone();
        denatorBaseinfo_allDaoConfig.initIdentityScope(type);

        denatorHis_DetailDaoConfig = daoConfigMap.get(DenatorHis_DetailDao.class).clone();
        denatorHis_DetailDaoConfig.initIdentityScope(type);

        denatorHis_Detail_allDaoConfig = daoConfigMap.get(DenatorHis_Detail_allDao.class).clone();
        denatorHis_Detail_allDaoConfig.initIdentityScope(type);

        denatorHis_MainDaoConfig = daoConfigMap.get(DenatorHis_MainDao.class).clone();
        denatorHis_MainDaoConfig.initIdentityScope(type);

        denatorHis_Main_allDaoConfig = daoConfigMap.get(DenatorHis_Main_allDao.class).clone();
        denatorHis_Main_allDaoConfig.initIdentityScope(type);

        denator_typeDaoConfig = daoConfigMap.get(Denator_typeDao.class).clone();
        denator_typeDaoConfig.initIdentityScope(type);

        detonatorTypeNewDaoConfig = daoConfigMap.get(DetonatorTypeNewDao.class).clone();
        detonatorTypeNewDaoConfig.initIdentityScope(type);

        errLogDaoConfig = daoConfigMap.get(ErrLogDao.class).clone();
        errLogDaoConfig.initIdentityScope(type);

        messageBeanDaoConfig = daoConfigMap.get(MessageBeanDao.class).clone();
        messageBeanDaoConfig.initIdentityScope(type);

        projectDaoConfig = daoConfigMap.get(ProjectDao.class).clone();
        projectDaoConfig.initIdentityScope(type);

        shouQuanDaoConfig = daoConfigMap.get(ShouQuanDao.class).clone();
        shouQuanDaoConfig.initIdentityScope(type);

        sysLogDaoConfig = daoConfigMap.get(SysLogDao.class).clone();
        sysLogDaoConfig.initIdentityScope(type);

        temporary_databaseDaoConfig = daoConfigMap.get(Temporary_databaseDao.class).clone();
        temporary_databaseDaoConfig.initIdentityScope(type);

        userMainDaoConfig = daoConfigMap.get(UserMainDao.class).clone();
        userMainDaoConfig.initIdentityScope(type);

        defactoryDao = new DefactoryDao(defactoryDaoConfig, this);
        denatorBaseinfoDao = new DenatorBaseinfoDao(denatorBaseinfoDaoConfig, this);
        denatorBaseinfo_allDao = new DenatorBaseinfo_allDao(denatorBaseinfo_allDaoConfig, this);
        denatorHis_DetailDao = new DenatorHis_DetailDao(denatorHis_DetailDaoConfig, this);
        denatorHis_Detail_allDao = new DenatorHis_Detail_allDao(denatorHis_Detail_allDaoConfig, this);
        denatorHis_MainDao = new DenatorHis_MainDao(denatorHis_MainDaoConfig, this);
        denatorHis_Main_allDao = new DenatorHis_Main_allDao(denatorHis_Main_allDaoConfig, this);
        denator_typeDao = new Denator_typeDao(denator_typeDaoConfig, this);
        detonatorTypeNewDao = new DetonatorTypeNewDao(detonatorTypeNewDaoConfig, this);
        errLogDao = new ErrLogDao(errLogDaoConfig, this);
        messageBeanDao = new MessageBeanDao(messageBeanDaoConfig, this);
        projectDao = new ProjectDao(projectDaoConfig, this);
        shouQuanDao = new ShouQuanDao(shouQuanDaoConfig, this);
        sysLogDao = new SysLogDao(sysLogDaoConfig, this);
        temporary_databaseDao = new Temporary_databaseDao(temporary_databaseDaoConfig, this);
        userMainDao = new UserMainDao(userMainDaoConfig, this);

        registerDao(Defactory.class, defactoryDao);
        registerDao(DenatorBaseinfo.class, denatorBaseinfoDao);
        registerDao(DenatorBaseinfo_all.class, denatorBaseinfo_allDao);
        registerDao(DenatorHis_Detail.class, denatorHis_DetailDao);
        registerDao(DenatorHis_Detail_all.class, denatorHis_Detail_allDao);
        registerDao(DenatorHis_Main.class, denatorHis_MainDao);
        registerDao(DenatorHis_Main_all.class, denatorHis_Main_allDao);
        registerDao(Denator_type.class, denator_typeDao);
        registerDao(DetonatorTypeNew.class, detonatorTypeNewDao);
        registerDao(ErrLog.class, errLogDao);
        registerDao(MessageBean.class, messageBeanDao);
        registerDao(Project.class, projectDao);
        registerDao(ShouQuan.class, shouQuanDao);
        registerDao(SysLog.class, sysLogDao);
        registerDao(Temporary_database.class, temporary_databaseDao);
        registerDao(UserMain.class, userMainDao);
    }
    
    public void clear() {
        defactoryDaoConfig.clearIdentityScope();
        denatorBaseinfoDaoConfig.clearIdentityScope();
        denatorBaseinfo_allDaoConfig.clearIdentityScope();
        denatorHis_DetailDaoConfig.clearIdentityScope();
        denatorHis_Detail_allDaoConfig.clearIdentityScope();
        denatorHis_MainDaoConfig.clearIdentityScope();
        denatorHis_Main_allDaoConfig.clearIdentityScope();
        denator_typeDaoConfig.clearIdentityScope();
        detonatorTypeNewDaoConfig.clearIdentityScope();
        errLogDaoConfig.clearIdentityScope();
        messageBeanDaoConfig.clearIdentityScope();
        projectDaoConfig.clearIdentityScope();
        shouQuanDaoConfig.clearIdentityScope();
        sysLogDaoConfig.clearIdentityScope();
        temporary_databaseDaoConfig.clearIdentityScope();
        userMainDaoConfig.clearIdentityScope();
    }

    public DefactoryDao getDefactoryDao() {
        return defactoryDao;
    }

    public DenatorBaseinfoDao getDenatorBaseinfoDao() {
        return denatorBaseinfoDao;
    }

    public DenatorBaseinfo_allDao getDenatorBaseinfo_allDao() {
        return denatorBaseinfo_allDao;
    }

    public DenatorHis_DetailDao getDenatorHis_DetailDao() {
        return denatorHis_DetailDao;
    }

    public DenatorHis_Detail_allDao getDenatorHis_Detail_allDao() {
        return denatorHis_Detail_allDao;
    }

    public DenatorHis_MainDao getDenatorHis_MainDao() {
        return denatorHis_MainDao;
    }

    public DenatorHis_Main_allDao getDenatorHis_Main_allDao() {
        return denatorHis_Main_allDao;
    }

    public Denator_typeDao getDenator_typeDao() {
        return denator_typeDao;
    }

    public DetonatorTypeNewDao getDetonatorTypeNewDao() {
        return detonatorTypeNewDao;
    }

    public ErrLogDao getErrLogDao() {
        return errLogDao;
    }

    public MessageBeanDao getMessageBeanDao() {
        return messageBeanDao;
    }

    public ProjectDao getProjectDao() {
        return projectDao;
    }

    public ShouQuanDao getShouQuanDao() {
        return shouQuanDao;
    }

    public SysLogDao getSysLogDao() {
        return sysLogDao;
    }

    public Temporary_databaseDao getTemporary_databaseDao() {
        return temporary_databaseDao;
    }

    public UserMainDao getUserMainDao() {
        return userMainDao;
    }

}
