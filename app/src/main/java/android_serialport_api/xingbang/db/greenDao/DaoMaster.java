package android_serialport_api.xingbang.db.greenDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * Master of DAO (schema version 31): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 31;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(Database db, boolean ifNotExists) {
        DefactoryDao.createTable(db, ifNotExists);
        DenatorBaseinfoDao.createTable(db, ifNotExists);
        DenatorBaseinfo_allDao.createTable(db, ifNotExists);
        DenatorHis_DetailDao.createTable(db, ifNotExists);
        DenatorHis_Detail_allDao.createTable(db, ifNotExists);
        DenatorHis_MainDao.createTable(db, ifNotExists);
        DenatorHis_Main_allDao.createTable(db, ifNotExists);
        Denator_typeDao.createTable(db, ifNotExists);
        DetonatorTypeNewDao.createTable(db, ifNotExists);
        ErrLogDao.createTable(db, ifNotExists);
        MessageBeanDao.createTable(db, ifNotExists);
        PaiDataDao.createTable(db, ifNotExists);
        ProjectDao.createTable(db, ifNotExists);
        QuYuDao.createTable(db, ifNotExists);
        ShouQuanDao.createTable(db, ifNotExists);
        SingleRegisterDenatorDao.createTable(db, ifNotExists);
        SysLogDao.createTable(db, ifNotExists);
        Temporary_databaseDao.createTable(db, ifNotExists);
        UserMainDao.createTable(db, ifNotExists);
    }

    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(Database db, boolean ifExists) {
        DefactoryDao.dropTable(db, ifExists);
        DenatorBaseinfoDao.dropTable(db, ifExists);
        DenatorBaseinfo_allDao.dropTable(db, ifExists);
        DenatorHis_DetailDao.dropTable(db, ifExists);
        DenatorHis_Detail_allDao.dropTable(db, ifExists);
        DenatorHis_MainDao.dropTable(db, ifExists);
        DenatorHis_Main_allDao.dropTable(db, ifExists);
        Denator_typeDao.dropTable(db, ifExists);
        DetonatorTypeNewDao.dropTable(db, ifExists);
        ErrLogDao.dropTable(db, ifExists);
        MessageBeanDao.dropTable(db, ifExists);
        PaiDataDao.dropTable(db, ifExists);
        ProjectDao.dropTable(db, ifExists);
        QuYuDao.dropTable(db, ifExists);
        ShouQuanDao.dropTable(db, ifExists);
        SingleRegisterDenatorDao.dropTable(db, ifExists);
        SysLogDao.dropTable(db, ifExists);
        Temporary_databaseDao.dropTable(db, ifExists);
        UserMainDao.dropTable(db, ifExists);
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     * Convenience method using a {@link DevOpenHelper}.
     */
    public static DaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(DefactoryDao.class);
        registerDaoClass(DenatorBaseinfoDao.class);
        registerDaoClass(DenatorBaseinfo_allDao.class);
        registerDaoClass(DenatorHis_DetailDao.class);
        registerDaoClass(DenatorHis_Detail_allDao.class);
        registerDaoClass(DenatorHis_MainDao.class);
        registerDaoClass(DenatorHis_Main_allDao.class);
        registerDaoClass(Denator_typeDao.class);
        registerDaoClass(DetonatorTypeNewDao.class);
        registerDaoClass(ErrLogDao.class);
        registerDaoClass(MessageBeanDao.class);
        registerDaoClass(PaiDataDao.class);
        registerDaoClass(ProjectDao.class);
        registerDaoClass(QuYuDao.class);
        registerDaoClass(ShouQuanDao.class);
        registerDaoClass(SingleRegisterDenatorDao.class);
        registerDaoClass(SysLogDao.class);
        registerDaoClass(Temporary_databaseDao.class);
        registerDaoClass(UserMainDao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    /**
     * Calls {@link #createAllTables(Database, boolean)} in {@link #onCreate(Database)} -
     */
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
