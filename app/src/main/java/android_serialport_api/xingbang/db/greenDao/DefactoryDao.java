package android_serialport_api.xingbang.db.greenDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import android_serialport_api.xingbang.db.Defactory;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "Defactory".
*/
public class DefactoryDao extends AbstractDao<Defactory, Long> {

    public static final String TABLENAME = "Defactory";

    /**
     * Properties of entity Defactory.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "id");
        public final static Property DeName = new Property(1, String.class, "deName", false, "deName");
        public final static Property DeEntCode = new Property(2, String.class, "deEntCode", false, "deEntCode");
        public final static Property DeFeatureCode = new Property(3, String.class, "deFeatureCode", false, "deFeatureCode");
        public final static Property IsSelected = new Property(4, String.class, "isSelected", false, "isSelected");
    }


    public DefactoryDao(DaoConfig config) {
        super(config);
    }
    
    public DefactoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"Defactory\" (" + //
                "\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"deName\" TEXT," + // 1: deName
                "\"deEntCode\" TEXT," + // 2: deEntCode
                "\"deFeatureCode\" TEXT," + // 3: deFeatureCode
                "\"isSelected\" TEXT);"); // 4: isSelected
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"Defactory\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Defactory entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deName = entity.getDeName();
        if (deName != null) {
            stmt.bindString(2, deName);
        }
 
        String deEntCode = entity.getDeEntCode();
        if (deEntCode != null) {
            stmt.bindString(3, deEntCode);
        }
 
        String deFeatureCode = entity.getDeFeatureCode();
        if (deFeatureCode != null) {
            stmt.bindString(4, deFeatureCode);
        }
 
        String isSelected = entity.getIsSelected();
        if (isSelected != null) {
            stmt.bindString(5, isSelected);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Defactory entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deName = entity.getDeName();
        if (deName != null) {
            stmt.bindString(2, deName);
        }
 
        String deEntCode = entity.getDeEntCode();
        if (deEntCode != null) {
            stmt.bindString(3, deEntCode);
        }
 
        String deFeatureCode = entity.getDeFeatureCode();
        if (deFeatureCode != null) {
            stmt.bindString(4, deFeatureCode);
        }
 
        String isSelected = entity.getIsSelected();
        if (isSelected != null) {
            stmt.bindString(5, isSelected);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Defactory readEntity(Cursor cursor, int offset) {
        Defactory entity = new Defactory( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // deName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // deEntCode
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // deFeatureCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // isSelected
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Defactory entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeEntCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDeFeatureCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setIsSelected(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Defactory entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Defactory entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Defactory entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
