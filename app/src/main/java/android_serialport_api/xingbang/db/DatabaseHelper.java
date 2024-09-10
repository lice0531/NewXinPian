package android_serialport_api.xingbang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    //用于创建雷管基本信息
    public static final String CREATE_DENATO = "create table denatorBaseinfo ("
            + "id integer primary key autoincrement, "
            + "blastserial integer, "//序号
            + "sithole integer, "//空号
            + "shellBlastNo text, "//管壳码
            + "denatorId text, "//芯片码
            + "delay integer, "//延时
            + "statusCode text, "//读取状态//00 雷管正常//01 未注册//02 已注册
            + "statusName text, "//状态名称
            + "errorName text, "//错误名称
            + "errorCode text, "//错误代码//通信状态 ,00-与雷管通信失败，01-延期写入不一致，FF-通信成功，AF-未返回命令
            + "authorization text, "//授权  记录芯片版本
            + "remark text, " //评论  已起爆/未起爆
            + "regdate text, " //注册日期
            + "wire text, "//桥丝状态
            + "name text, "
            + "denatorIdSup text, "//从芯片码
            + "zhu_yscs text, "//主芯片参数
            + "cong_yscs text, "//从芯片参数
            + "piece text "//区域
            + ")";
    public static final String CREATE_DENATO_ALL = "create table denatorBaseinfo_all ("
            + "id integer primary key autoincrement, "
            + "blastserial integer, "//序号
            + "sithole integer, "//孔号
            + "shellBlastNo text, "//管壳码
            + "denatorId text, "//芯片码
            + "delay integer, "//延时
            + "statusCode text, "//读取状态
            + "statusName text, "//状态名称
            + "errorName text, "//错误名称
            + "errorCode text, "//错误代码
            + "authorization text, "//授权
            + "remark text, " //评论
            + "regdate text, " //注册日期
            + "wire text, "//桥丝状态
            + "name text, "
            + "denatorIdSup text "//从芯片码
            + ")";

    //用于创建爆炸雷管历史主表
    public static final String CREATE_HIS_MAIN_DENATO = "create table denatorHis_Main ("
            + "id integer primary key autoincrement, "
            + "blastdate text, " //爆炸时间
            + "uploadStatus text, "//上传状态
            + "longitude text, "    //经度坐标
            + "latitude text, "    //纬度
            + "userid text, "    //操作人
            + "equ_no text, "    //起爆器编号
            + "serialNo integer, "    //验证页面选择的id
            + "pro_xmbh text, "    //项目编号
            + "pro_htid text, "    //合同编号
            + "pro_dwdm text, "    //单位代码
            + "remark text)";     //备注

    //用于创建爆炸雷管历史主表
    public static final String CREATE_HIS_MAIN_DENATO_ALL = "create table denatorHis_Main_all ("
            + "id integer primary key autoincrement, "
            + "blastdate text, " //爆炸时间
            + "uploadStatus text, "//上传状态
            + "longitude text, "    //经度坐标
            + "latitude text, "    //纬度
            + "userid text, "    //操作人
            + "firedNo text, "    //起爆器编号
            + "serialNo integer, "    //序号
            + "contactNo text, "    //项目编号
            + "projectNo text, "    //合同编号
            + "remark text)";     //备注

    //用于创建爆炸雷管历史基本明细信息
    public static final String CREATE_HIS_DETAIL_DENATO = "create table denatorHis_Detail ("
            + "id integer primary key autoincrement, "
            + "blastserial integer, "
            + "sithole integer, "
            + "shellBlastNo text, "
            + "denatorId text, "
            + "delay integer, "
            + "statusCode text, "
            + "statusName text, "
            + "errorName text, "
            + "errorCode text, "
            + "authorization text, "
            + "remark text, "
            + "regdate text, "
            + "blastdate text, " //爆炸时间
            + "name text)";

    //用于创建爆炸雷管历史基本明细信息
    public static final String CREATE_HIS_DETAIL_DENATO_ALL = "create table denatorHis_Detail_all ("
            + "id integer primary key autoincrement, "
            + "blastserial integer, "
            + "sithole integer, "
            + "shellBlastNo text, "
            + "denatorId text, "
            + "delay integer, "
            + "statusCode text, "
            + "statusName text, "
            + "errorName text, "
            + "errorCode text, "
            + "authorization text, "
            + "remark text, "
            + "regdate text, "
            + "blastdate text, " //爆炸时间
            + "name text)";
    //用于创建雷管厂表
    public static final String CREATE_DEFACTORY = "create table Defactory ("
            + "id integer primary key autoincrement, "
            + "deName text, "//雷管名称
            + "deEntCode text, "//雷管代号
            + "deFeatureCode text, "//厂家代号
            + "isSelected text)";//是否选择
    //用于创建雷管类别
    public static final String CREATE_DENATOR_TYPE = "create table denator_type ("
            + "id integer primary key autoincrement, "
            + "deTypeName text, "//雷管类型名称
            + "deTypeSecond text, "  //最大延期值
            + "isSelected text)";//是否选择
    //用于创建用户表
    public static final String CREATE_USER = "create table UserMain ("
            + "id integer primary key autoincrement, "
            + "uname text, "
            + "upassword text, "
            + "isface int, "
            + "facepath text)";
    //用于创建授权信息表
    public static final String CREATE_SHOUQUAN = "create table ShouQuan ("
            + "id integer primary key autoincrement, "
            + "xmbh text, "//项目编号
            + "htbh text, "//合同编号
            + "json text, "
            + "errNum text, "//错误数量
            + "qbzt text,"//起爆状态
            + "blastdate text,"//起爆时间
            + "dl_state text,"//丹灵上传状态
            + "zb_state text,"//中爆上传状态
            + "dwdm text,"//单位代码
            + "bprysfz text,"//爆破员身份证
            + "coordxy text,"//经纬度
            + "qblgNum text,"//已起爆雷管数量
            + "spare1 text,"//项目名称
            + "spare2 text,"//备用2
            + "total int )";//总数

    //用于创建授权信息表
    public static final String CREATE_PROJECT = "create table Project ("
            + "id integer primary key autoincrement, "
            + "project_name text, "//项目名称
            + "xmbh text, "//项目编号
            + "htbh text, "//合同编号
            + "dwdm text,"//单位代码
            + "bprysfz text,"//爆破员身份证
            + "coordxy text"//经纬度
            + ")";
    //用于创建授权信息表
    public static final String CREATE_MESSAGE = "create table Message ("
            + "id integer primary key autoincrement, "
            + "pro_bprysfz text, "
            + "pro_htid text, "
            + "pro_xmbh text, "
            + "equ_no text, "
            + "pro_coordxy text, "
            + "server_addr text, "
            + "server_port text, "
            + "server_http text, "
            + "server_ip text, "
            + "qiaosi_set text, "
            + "preparation_time text, "
            + "chongdian_time text, "
            + "server_type1 text, "
            + "server_type2 text, "
            + "pro_dwdm text, "
            + "jiance_time text,"
            + "version text"
            + ")";

    //用于错误日志表
    public static final String CREATE_ErrLog = "create table ErrLog ("
            + "id integer primary key autoincrement, "
            + "filename text, "
            + "path text, "
            + "updataState text, "
            + "updataTime text)";

    public static final int TABLE_VERSION = 25;
    public static final String TABLE_NAME_DENATOBASEINFO = "denatorBaseinfo";   //雷管表
    public static final String TABLE_NAME_DENATOBASEINFO_ALL = "denatorBaseinfo_all";   //全部雷管表
    public static final String TABLE_NAME_HISMAIN = "denatorHis_Main";//历史时间表
    public static final String TABLE_NAME_HISDETAIL = "denatorHis_Detail";//历史详细表
    public static final String TABLE_NAME_HISMAIN_ALL = "denatorHis_Main_all";
    public static final String TABLE_NAME_HISDETAIL_ALL = "denatorHis_Detail_all";
    public static final String TABLE_USER_MAIN = "UserMain";     //用户表
    public static final String TABLE_NAME_DEFACTORY = "Defactory";//厂家表
    public static final String TABLE_NAME_DENATOR_TYPE = "denator_type";//雷管类型
    public static final String TABLE_NAME_SHOUQUAN = "ShouQuan";//授权信息表
    public static final String TABLE_NAME_PROJECT = "Project";//项目保存信息表
    public static final String TABLE_NAME_USER_MESSQGE = "Message";//用户信息表

    public static final String SELECT_ALL_DENATOBASEINFO = "Select * from denatorBaseinfo";
    public static final String SELECT_ALL_DENATOBASEINFO_ZHENGCHANG =
            "select * from denatorBaseinfo  a where  not exists (select 1 from denatorBaseinfo where a.shellBlastNo=shellBlastNo and a.blastserial = blastserial and id<a.id)  order by blastserial asc";
    public static final String SELECT_ALL_SHOUQUAN = "Select * from ShouQuan";
    public static final String SELECT_ALL_PROJECT = "Select * from Project";
    public static final String SELECT_ALL_DENATORHIS = "Select * from denatorHis_Main";
    public static final String DROP_DENATOBASEINFO = "drop table if exists denatorBaseinfo";
    public static final String DROP_HIS_MAIN_DENATO = "drop table if exists denatorHis_Main";
    public static final String DROP_HIS_DETAIL_DENATO = "drop table if exists denatorHis_Detail";
    public static final String DROP_HIS_USER_MAIN = "drop table if exists UserMain";
    public static final String DROP_HIS_FACTORY_MAIN = "drop table if exists Defactory";
    public static final String DROP_DENATOR_TYPE = "drop table if exists denator_type";

    private Context mContext;

    public DatabaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    private void manageTable(SQLiteDatabase db) {
        db.execSQL(DROP_DENATOBASEINFO);
        db.execSQL(DROP_HIS_MAIN_DENATO);
        db.execSQL(DROP_HIS_DETAIL_DENATO);
        db.execSQL(DROP_HIS_USER_MAIN);
        db.execSQL(DROP_HIS_FACTORY_MAIN);
        db.execSQL(DROP_DENATOR_TYPE);
        db.execSQL(CREATE_DENATO);
        db.execSQL(CREATE_DENATO_ALL);
        db.execSQL(CREATE_DEFACTORY);
        db.execSQL(CREATE_HIS_MAIN_DENATO);
        db.execSQL(CREATE_HIS_MAIN_DENATO_ALL);
        db.execSQL(CREATE_HIS_DETAIL_DENATO);
        db.execSQL(CREATE_HIS_DETAIL_DENATO_ALL);
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_SHOUQUAN);
        db.execSQL(CREATE_PROJECT);
        db.execSQL(CREATE_MESSAGE);
        db.execSQL(CREATE_DENATOR_TYPE);
        Log.e("创建数据库", "manageTable: ");
    }

    //第一次创建数据库时会调用此方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        manageTable(db);
    }

    //数据库版本升级时会调用此方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*第一版的数据库只有Book表，第二版的数据库增加了Category表，
         * 为了保证用户体验，在不干扰前一版的数据的情况下，实现对数据
         * 库的平滑升级，简单的可以用此方法进行判断在升级*/
        /***
         switch (oldVersion){
         case 1:
         db.execSQL(CREATE_CATEGORY);
         case 2:
         db.execSQL("alter table Book add column category_id integer");
         default:
         }**/
        // db.execSQL(CREATE_DEFACTORY);
        manageTable(db);
    }

}
