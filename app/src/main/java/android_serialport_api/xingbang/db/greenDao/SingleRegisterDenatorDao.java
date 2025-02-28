package android_serialport_api.xingbang.db.greenDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import android_serialport_api.xingbang.db.SingleRegisterDenator;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SingleRegisterDenator".
*/
public class SingleRegisterDenatorDao extends AbstractDao<SingleRegisterDenator, Long> {

    public static final String TABLENAME = "SingleRegisterDenator";

    /**
     * Properties of entity SingleRegisterDenator.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "Id", true, "id");
        public final static Property Blastserial = new Property(1, int.class, "blastserial", false, "blastserial");
        public final static Property Sithole = new Property(2, String.class, "sithole", false, "sithole");
        public final static Property ShellBlastNo = new Property(3, String.class, "shellBlastNo", false, "shellBlastNo");
        public final static Property DetonatorId = new Property(4, String.class, "detonatorId", false, "denatorId");
        public final static Property DenatorIdSup = new Property(5, String.class, "denatorIdSup", false, "denatorIdSup");
        public final static Property Zhu_yscs = new Property(6, String.class, "zhu_yscs", false, "zhu_yscs");
        public final static Property Cong_yscs = new Property(7, String.class, "cong_yscs", false, "cong_yscs");
        public final static Property Time = new Property(8, String.class, "time", false, "time");
        public final static Property Delay = new Property(9, int.class, "delay", false, "delay");
        public final static Property StatusCode = new Property(10, String.class, "statusCode", false, "statusCode");
        public final static Property StatusName = new Property(11, String.class, "statusName", false, "statusName");
        public final static Property ErrorName = new Property(12, String.class, "errorName", false, "errorName");
        public final static Property ErrorCode = new Property(13, String.class, "errorCode", false, "errorCode");
        public final static Property Authorization = new Property(14, String.class, "authorization", false, "authorization");
        public final static Property Remark = new Property(15, String.class, "remark", false, "remark");
        public final static Property Regdate = new Property(16, String.class, "regdate", false, "regdate");
        public final static Property Wire = new Property(17, String.class, "wire", false, "wire");
        public final static Property Name = new Property(18, String.class, "name", false, "name");
        public final static Property Piece = new Property(19, String.class, "piece", false, "piece");
        public final static Property Duan = new Property(20, int.class, "duan", false, "duan");
        public final static Property DuanNo = new Property(21, int.class, "duanNo", false, "duanNo");
        public final static Property Fanzhuan = new Property(22, String.class, "fanzhuan", false, "fanzhuan");
        public final static Property Pai = new Property(23, String.class, "pai", false, "pai");
        public final static Property Qibao = new Property(24, String.class, "qibao", false, "qibao");
    }


    public SingleRegisterDenatorDao(DaoConfig config) {
        super(config);
    }
    
    public SingleRegisterDenatorDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SingleRegisterDenator\" (" + //
                "\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: Id
                "\"blastserial\" INTEGER NOT NULL ," + // 1: blastserial
                "\"sithole\" TEXT," + // 2: sithole
                "\"shellBlastNo\" TEXT," + // 3: shellBlastNo
                "\"denatorId\" TEXT," + // 4: detonatorId
                "\"denatorIdSup\" TEXT," + // 5: denatorIdSup
                "\"zhu_yscs\" TEXT," + // 6: zhu_yscs
                "\"cong_yscs\" TEXT," + // 7: cong_yscs
                "\"time\" TEXT," + // 8: time
                "\"delay\" INTEGER NOT NULL ," + // 9: delay
                "\"statusCode\" TEXT," + // 10: statusCode
                "\"statusName\" TEXT," + // 11: statusName
                "\"errorName\" TEXT," + // 12: errorName
                "\"errorCode\" TEXT," + // 13: errorCode
                "\"authorization\" TEXT," + // 14: authorization
                "\"remark\" TEXT," + // 15: remark
                "\"regdate\" TEXT," + // 16: regdate
                "\"wire\" TEXT," + // 17: wire
                "\"name\" TEXT," + // 18: name
                "\"piece\" TEXT," + // 19: piece
                "\"duan\" INTEGER NOT NULL ," + // 20: duan
                "\"duanNo\" INTEGER NOT NULL ," + // 21: duanNo
                "\"fanzhuan\" TEXT," + // 22: fanzhuan
                "\"pai\" TEXT," + // 23: pai
                "\"qibao\" TEXT);"); // 24: qibao
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SingleRegisterDenator\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SingleRegisterDenator entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
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
 
        String detonatorId = entity.getDetonatorId();
        if (detonatorId != null) {
            stmt.bindString(5, detonatorId);
        }
 
        String denatorIdSup = entity.getDenatorIdSup();
        if (denatorIdSup != null) {
            stmt.bindString(6, denatorIdSup);
        }
 
        String zhu_yscs = entity.getZhu_yscs();
        if (zhu_yscs != null) {
            stmt.bindString(7, zhu_yscs);
        }
 
        String cong_yscs = entity.getCong_yscs();
        if (cong_yscs != null) {
            stmt.bindString(8, cong_yscs);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(9, time);
        }
        stmt.bindLong(10, entity.getDelay());
 
        String statusCode = entity.getStatusCode();
        if (statusCode != null) {
            stmt.bindString(11, statusCode);
        }
 
        String statusName = entity.getStatusName();
        if (statusName != null) {
            stmt.bindString(12, statusName);
        }
 
        String errorName = entity.getErrorName();
        if (errorName != null) {
            stmt.bindString(13, errorName);
        }
 
        String errorCode = entity.getErrorCode();
        if (errorCode != null) {
            stmt.bindString(14, errorCode);
        }
 
        String authorization = entity.getAuthorization();
        if (authorization != null) {
            stmt.bindString(15, authorization);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(16, remark);
        }
 
        String regdate = entity.getRegdate();
        if (regdate != null) {
            stmt.bindString(17, regdate);
        }
 
        String wire = entity.getWire();
        if (wire != null) {
            stmt.bindString(18, wire);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(19, name);
        }
 
        String piece = entity.getPiece();
        if (piece != null) {
            stmt.bindString(20, piece);
        }
        stmt.bindLong(21, entity.getDuan());
        stmt.bindLong(22, entity.getDuanNo());
 
        String fanzhuan = entity.getFanzhuan();
        if (fanzhuan != null) {
            stmt.bindString(23, fanzhuan);
        }
 
        String pai = entity.getPai();
        if (pai != null) {
            stmt.bindString(24, pai);
        }
 
        String qibao = entity.getQibao();
        if (qibao != null) {
            stmt.bindString(25, qibao);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SingleRegisterDenator entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
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
 
        String detonatorId = entity.getDetonatorId();
        if (detonatorId != null) {
            stmt.bindString(5, detonatorId);
        }
 
        String denatorIdSup = entity.getDenatorIdSup();
        if (denatorIdSup != null) {
            stmt.bindString(6, denatorIdSup);
        }
 
        String zhu_yscs = entity.getZhu_yscs();
        if (zhu_yscs != null) {
            stmt.bindString(7, zhu_yscs);
        }
 
        String cong_yscs = entity.getCong_yscs();
        if (cong_yscs != null) {
            stmt.bindString(8, cong_yscs);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(9, time);
        }
        stmt.bindLong(10, entity.getDelay());
 
        String statusCode = entity.getStatusCode();
        if (statusCode != null) {
            stmt.bindString(11, statusCode);
        }
 
        String statusName = entity.getStatusName();
        if (statusName != null) {
            stmt.bindString(12, statusName);
        }
 
        String errorName = entity.getErrorName();
        if (errorName != null) {
            stmt.bindString(13, errorName);
        }
 
        String errorCode = entity.getErrorCode();
        if (errorCode != null) {
            stmt.bindString(14, errorCode);
        }
 
        String authorization = entity.getAuthorization();
        if (authorization != null) {
            stmt.bindString(15, authorization);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(16, remark);
        }
 
        String regdate = entity.getRegdate();
        if (regdate != null) {
            stmt.bindString(17, regdate);
        }
 
        String wire = entity.getWire();
        if (wire != null) {
            stmt.bindString(18, wire);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(19, name);
        }
 
        String piece = entity.getPiece();
        if (piece != null) {
            stmt.bindString(20, piece);
        }
        stmt.bindLong(21, entity.getDuan());
        stmt.bindLong(22, entity.getDuanNo());
 
        String fanzhuan = entity.getFanzhuan();
        if (fanzhuan != null) {
            stmt.bindString(23, fanzhuan);
        }
 
        String pai = entity.getPai();
        if (pai != null) {
            stmt.bindString(24, pai);
        }
 
        String qibao = entity.getQibao();
        if (qibao != null) {
            stmt.bindString(25, qibao);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public SingleRegisterDenator readEntity(Cursor cursor, int offset) {
        SingleRegisterDenator entity = new SingleRegisterDenator( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // Id
            cursor.getInt(offset + 1), // blastserial
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // sithole
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // shellBlastNo
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // detonatorId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // denatorIdSup
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // zhu_yscs
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // cong_yscs
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // time
            cursor.getInt(offset + 9), // delay
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // statusCode
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // statusName
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // errorName
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // errorCode
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // authorization
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // remark
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // regdate
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // wire
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // name
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // piece
            cursor.getInt(offset + 20), // duan
            cursor.getInt(offset + 21), // duanNo
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // fanzhuan
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // pai
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24) // qibao
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SingleRegisterDenator entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBlastserial(cursor.getInt(offset + 1));
        entity.setSithole(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setShellBlastNo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDetonatorId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDenatorIdSup(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setZhu_yscs(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCong_yscs(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTime(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDelay(cursor.getInt(offset + 9));
        entity.setStatusCode(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setStatusName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setErrorName(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setErrorCode(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setAuthorization(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setRemark(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setRegdate(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setWire(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setName(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setPiece(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setDuan(cursor.getInt(offset + 20));
        entity.setDuanNo(cursor.getInt(offset + 21));
        entity.setFanzhuan(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setPai(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setQibao(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SingleRegisterDenator entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SingleRegisterDenator entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SingleRegisterDenator entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
