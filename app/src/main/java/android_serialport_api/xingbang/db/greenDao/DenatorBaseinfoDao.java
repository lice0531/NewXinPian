package android_serialport_api.xingbang.db.greenDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import android_serialport_api.xingbang.db.DenatorBaseinfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "denatorBaseinfo".
*/
public class DenatorBaseinfoDao extends AbstractDao<DenatorBaseinfo, Long> {

    public static final String TABLENAME = "denatorBaseinfo";

    /**
     * Properties of entity DenatorBaseinfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "id");
        public final static Property Blastserial = new Property(1, int.class, "blastserial", false, "blastserial");
        public final static Property Sithole = new Property(2, String.class, "sithole", false, "sithole");
        public final static Property ShellBlastNo = new Property(3, String.class, "shellBlastNo", false, "shellBlastNo");
        public final static Property DenatorId = new Property(4, String.class, "denatorId", false, "denatorId");
        public final static Property Delay = new Property(5, int.class, "delay", false, "delay");
        public final static Property StatusCode = new Property(6, String.class, "statusCode", false, "statusCode");
        public final static Property StatusName = new Property(7, String.class, "statusName", false, "statusName");
        public final static Property ErrorName = new Property(8, String.class, "errorName", false, "errorName");
        public final static Property ErrorCode = new Property(9, String.class, "errorCode", false, "errorCode");
        public final static Property Authorization = new Property(10, String.class, "authorization", false, "authorization");
        public final static Property Remark = new Property(11, String.class, "remark", false, "remark");
        public final static Property Regdate = new Property(12, String.class, "regdate", false, "regdate");
        public final static Property Wire = new Property(13, String.class, "wire", false, "wire");
        public final static Property Name = new Property(14, String.class, "name", false, "name");
        public final static Property DenatorIdSup = new Property(15, String.class, "denatorIdSup", false, "denatorIdSup");
        public final static Property Zhu_yscs = new Property(16, String.class, "zhu_yscs", false, "zhu_yscs");
        public final static Property Cong_yscs = new Property(17, String.class, "cong_yscs", false, "cong_yscs");
        public final static Property Piece = new Property(18, String.class, "piece", false, "piece");
        public final static Property Duan = new Property(19, int.class, "duan", false, "duan");
        public final static Property DuanNo = new Property(20, int.class, "duanNo", false, "duanNo");
        public final static Property Fanzhuan = new Property(21, String.class, "fanzhuan", false, "fanzhuan");
        public final static Property Pai = new Property(22, String.class, "pai", false, "pai");
    }


    public DenatorBaseinfoDao(DaoConfig config) {
        super(config);
    }
    
    public DenatorBaseinfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"denatorBaseinfo\" (" + //
                "\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"blastserial\" INTEGER NOT NULL ," + // 1: blastserial
                "\"sithole\" TEXT," + // 2: sithole
                "\"shellBlastNo\" TEXT," + // 3: shellBlastNo
                "\"denatorId\" TEXT," + // 4: denatorId
                "\"delay\" INTEGER NOT NULL ," + // 5: delay
                "\"statusCode\" TEXT," + // 6: statusCode
                "\"statusName\" TEXT," + // 7: statusName
                "\"errorName\" TEXT," + // 8: errorName
                "\"errorCode\" TEXT," + // 9: errorCode
                "\"authorization\" TEXT," + // 10: authorization
                "\"remark\" TEXT," + // 11: remark
                "\"regdate\" TEXT," + // 12: regdate
                "\"wire\" TEXT," + // 13: wire
                "\"name\" TEXT," + // 14: name
                "\"denatorIdSup\" TEXT," + // 15: denatorIdSup
                "\"zhu_yscs\" TEXT," + // 16: zhu_yscs
                "\"cong_yscs\" TEXT," + // 17: cong_yscs
                "\"piece\" TEXT," + // 18: piece
                "\"duan\" INTEGER NOT NULL ," + // 19: duan
                "\"duanNo\" INTEGER NOT NULL ," + // 20: duanNo
                "\"fanzhuan\" TEXT," + // 21: fanzhuan
                "\"pai\" TEXT);"); // 22: pai
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"denatorBaseinfo\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DenatorBaseinfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getBlastserial());
 
        String sithole = entity.getSithole();
        if (sithole != null) {
            stmt.bindString(3, sithole);
        }
 
        String shellBlastNo = entity.getShellBlastNo();
        if (shellBlastNo != null) {
            stmt.bindString(4, shellBlastNo);
        }
 
        String denatorId = entity.getDenatorId();
        if (denatorId != null) {
            stmt.bindString(5, denatorId);
        }
        stmt.bindLong(6, entity.getDelay());
 
        String statusCode = entity.getStatusCode();
        if (statusCode != null) {
            stmt.bindString(7, statusCode);
        }
 
        String statusName = entity.getStatusName();
        if (statusName != null) {
            stmt.bindString(8, statusName);
        }
 
        String errorName = entity.getErrorName();
        if (errorName != null) {
            stmt.bindString(9, errorName);
        }
 
        String errorCode = entity.getErrorCode();
        if (errorCode != null) {
            stmt.bindString(10, errorCode);
        }
 
        String authorization = entity.getAuthorization();
        if (authorization != null) {
            stmt.bindString(11, authorization);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(12, remark);
        }
 
        String regdate = entity.getRegdate();
        if (regdate != null) {
            stmt.bindString(13, regdate);
        }
 
        String wire = entity.getWire();
        if (wire != null) {
            stmt.bindString(14, wire);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(15, name);
        }
 
        String denatorIdSup = entity.getDenatorIdSup();
        if (denatorIdSup != null) {
            stmt.bindString(16, denatorIdSup);
        }
 
        String zhu_yscs = entity.getZhu_yscs();
        if (zhu_yscs != null) {
            stmt.bindString(17, zhu_yscs);
        }
 
        String cong_yscs = entity.getCong_yscs();
        if (cong_yscs != null) {
            stmt.bindString(18, cong_yscs);
        }
 
        String piece = entity.getPiece();
        if (piece != null) {
            stmt.bindString(19, piece);
        }
        stmt.bindLong(20, entity.getDuan());
        stmt.bindLong(21, entity.getDuanNo());
 
        String fanzhuan = entity.getFanzhuan();
        if (fanzhuan != null) {
            stmt.bindString(22, fanzhuan);
        }
 
        String pai = entity.getPai();
        if (pai != null) {
            stmt.bindString(23, pai);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DenatorBaseinfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getBlastserial());
 
        String sithole = entity.getSithole();
        if (sithole != null) {
            stmt.bindString(3, sithole);
        }
 
        String shellBlastNo = entity.getShellBlastNo();
        if (shellBlastNo != null) {
            stmt.bindString(4, shellBlastNo);
        }
 
        String denatorId = entity.getDenatorId();
        if (denatorId != null) {
            stmt.bindString(5, denatorId);
        }
        stmt.bindLong(6, entity.getDelay());
 
        String statusCode = entity.getStatusCode();
        if (statusCode != null) {
            stmt.bindString(7, statusCode);
        }
 
        String statusName = entity.getStatusName();
        if (statusName != null) {
            stmt.bindString(8, statusName);
        }
 
        String errorName = entity.getErrorName();
        if (errorName != null) {
            stmt.bindString(9, errorName);
        }
 
        String errorCode = entity.getErrorCode();
        if (errorCode != null) {
            stmt.bindString(10, errorCode);
        }
 
        String authorization = entity.getAuthorization();
        if (authorization != null) {
            stmt.bindString(11, authorization);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(12, remark);
        }
 
        String regdate = entity.getRegdate();
        if (regdate != null) {
            stmt.bindString(13, regdate);
        }
 
        String wire = entity.getWire();
        if (wire != null) {
            stmt.bindString(14, wire);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(15, name);
        }
 
        String denatorIdSup = entity.getDenatorIdSup();
        if (denatorIdSup != null) {
            stmt.bindString(16, denatorIdSup);
        }
 
        String zhu_yscs = entity.getZhu_yscs();
        if (zhu_yscs != null) {
            stmt.bindString(17, zhu_yscs);
        }
 
        String cong_yscs = entity.getCong_yscs();
        if (cong_yscs != null) {
            stmt.bindString(18, cong_yscs);
        }
 
        String piece = entity.getPiece();
        if (piece != null) {
            stmt.bindString(19, piece);
        }
        stmt.bindLong(20, entity.getDuan());
        stmt.bindLong(21, entity.getDuanNo());
 
        String fanzhuan = entity.getFanzhuan();
        if (fanzhuan != null) {
            stmt.bindString(22, fanzhuan);
        }
 
        String pai = entity.getPai();
        if (pai != null) {
            stmt.bindString(23, pai);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DenatorBaseinfo readEntity(Cursor cursor, int offset) {
        DenatorBaseinfo entity = new DenatorBaseinfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // blastserial
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // sithole
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // shellBlastNo
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // denatorId
            cursor.getInt(offset + 5), // delay
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // statusCode
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // statusName
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // errorName
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // errorCode
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // authorization
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // remark
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // regdate
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // wire
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // name
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // denatorIdSup
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // zhu_yscs
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // cong_yscs
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // piece
            cursor.getInt(offset + 19), // duan
            cursor.getInt(offset + 20), // duanNo
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // fanzhuan
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22) // pai
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DenatorBaseinfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBlastserial(cursor.getInt(offset + 1));
        entity.setSithole(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setShellBlastNo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDenatorId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDelay(cursor.getInt(offset + 5));
        entity.setStatusCode(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setStatusName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setErrorName(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setErrorCode(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setAuthorization(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setRemark(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setRegdate(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setWire(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setName(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setDenatorIdSup(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setZhu_yscs(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setCong_yscs(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setPiece(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setDuan(cursor.getInt(offset + 19));
        entity.setDuanNo(cursor.getInt(offset + 20));
        entity.setFanzhuan(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setPai(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DenatorBaseinfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DenatorBaseinfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DenatorBaseinfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
