package android_serialport_api.xingbang.db.greenDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import android_serialport_api.xingbang.db.DetonatorTypeNew;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DetonatorTypeNew".
*/
public class DetonatorTypeNewDao extends AbstractDao<DetonatorTypeNew, Long> {

    public static final String TABLENAME = "DetonatorTypeNew";

    /**
     * Properties of entity DetonatorTypeNew.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "Id", true, "id");
        public final static Property ShellBlastNo = new Property(1, String.class, "shellBlastNo", false, "shellBlastNo");
        public final static Property DetonatorId = new Property(2, String.class, "detonatorId", false, "denatorId");
    }


    public DetonatorTypeNewDao(DaoConfig config) {
        super(config);
    }
    
    public DetonatorTypeNewDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DetonatorTypeNew\" (" + //
                "\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: Id
                "\"shellBlastNo\" TEXT," + // 1: shellBlastNo
                "\"denatorId\" TEXT);"); // 2: detonatorId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DetonatorTypeNew\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DetonatorTypeNew entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
        }
 
        String shellBlastNo = entity.getShellBlastNo();
        if (shellBlastNo != null) {
            stmt.bindString(2, shellBlastNo);
        }
 
        String detonatorId = entity.getDetonatorId();
        if (detonatorId != null) {
            stmt.bindString(3, detonatorId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DetonatorTypeNew entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
        }
 
        String shellBlastNo = entity.getShellBlastNo();
        if (shellBlastNo != null) {
            stmt.bindString(2, shellBlastNo);
        }
 
        String detonatorId = entity.getDetonatorId();
        if (detonatorId != null) {
            stmt.bindString(3, detonatorId);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DetonatorTypeNew readEntity(Cursor cursor, int offset) {
        DetonatorTypeNew entity = new DetonatorTypeNew( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // Id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // shellBlastNo
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // detonatorId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DetonatorTypeNew entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setShellBlastNo(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDetonatorId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DetonatorTypeNew entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DetonatorTypeNew entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DetonatorTypeNew entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
