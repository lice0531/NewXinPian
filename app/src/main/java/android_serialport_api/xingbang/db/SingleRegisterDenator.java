package android_serialport_api.xingbang.db;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 单发注册雷管表
 */
@Entity(nameInDb = "SingleRegisterDenator")
public class SingleRegisterDenator implements Comparable<SingleRegisterDenator>{

    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long Id;             // 雷管自增ID--0
    @Property(nameInDb = "blastserial")
    private int blastserial;
    @Property(nameInDb = "sithole")
    private String sithole;
    @Property(nameInDb = "shellBlastNo")
    private String shellBlastNo;   // 管壳码——1
    @Property(nameInDb = "denatorId")
    private String detonatorId;    // 芯片码——2
    @Property(nameInDb = "denatorIdSup")
    private String denatorIdSup;    // 从芯片码——3
    @Property(nameInDb = "zhu_yscs")
    private String zhu_yscs;//主芯片延时参数
    @Property(nameInDb = "cong_yscs")
    private String cong_yscs;//从芯片延时参数
    @Property(nameInDb = "time")//牵扯到删除问题(自动删除历史记录的时候,删除相同的下载数据,所以时间是22-10-20)
    private String time;//从芯片延时参数
    @Property(nameInDb = "delay")
    private int delay;
    @Property(nameInDb = "statusCode")
    private String statusCode;
    @Property(nameInDb = "statusName")
    private String statusName;
    @Property(nameInDb = "errorName")
    private String errorName;
    @Property(nameInDb = "errorCode")
    private String errorCode;
    @Property(nameInDb = "authorization")
    private String authorization;
    @Property(nameInDb = "remark")
    private String remark;
    @Property(nameInDb = "regdate")
    private String regdate;
    @Property(nameInDb = "wire")
    private String wire;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "piece")
    private String piece;    // 区域
    @Property(nameInDb = "duan")
    private int duan;
    @Property(nameInDb = "duanNo")
    private int duanNo;
    @Property(nameInDb = "fanzhuan")
    private String fanzhuan;
    @Property(nameInDb = "pai")
    private String pai;
    @Property(nameInDb = "qibao")
    private String qibao;//是否起爆

    @Generated(hash = 1692776)
    public SingleRegisterDenator(Long Id, int blastserial, String sithole,
            String shellBlastNo, String detonatorId, String denatorIdSup, String zhu_yscs,
            String cong_yscs, String time, int delay, String statusCode, String statusName,
            String errorName, String errorCode, String authorization, String remark,
            String regdate, String wire, String name, String piece, int duan, int duanNo,
            String fanzhuan, String pai, String qibao) {
        this.Id = Id;
        this.blastserial = blastserial;
        this.sithole = sithole;
        this.shellBlastNo = shellBlastNo;
        this.detonatorId = detonatorId;
        this.denatorIdSup = denatorIdSup;
        this.zhu_yscs = zhu_yscs;
        this.cong_yscs = cong_yscs;
        this.time = time;
        this.delay = delay;
        this.statusCode = statusCode;
        this.statusName = statusName;
        this.errorName = errorName;
        this.errorCode = errorCode;
        this.authorization = authorization;
        this.remark = remark;
        this.regdate = regdate;
        this.wire = wire;
        this.name = name;
        this.piece = piece;
        this.duan = duan;
        this.duanNo = duanNo;
        this.fanzhuan = fanzhuan;
        this.pai = pai;
        this.qibao = qibao;
    }

    @Generated(hash = 1577580714)
    public SingleRegisterDenator() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public int getBlastserial() {
        return blastserial;
    }

    public void setBlastserial(int blastserial) {
        this.blastserial = blastserial;
    }

    public String getSithole() {
        return sithole;
    }

    public void setSithole(String sithole) {
        this.sithole = sithole;
    }

    public String getShellBlastNo() {
        return shellBlastNo;
    }

    public void setShellBlastNo(String shellBlastNo) {
        this.shellBlastNo = shellBlastNo;
    }

    public String getDetonatorId() {
        return detonatorId;
    }

    public void setDetonatorId(String detonatorId) {
        this.detonatorId = detonatorId;
    }

    public String getDenatorIdSup() {
        return denatorIdSup;
    }

    public void setDenatorIdSup(String denatorIdSup) {
        this.denatorIdSup = denatorIdSup;
    }

    public String getZhu_yscs() {
        return zhu_yscs;
    }

    public void setZhu_yscs(String zhu_yscs) {
        this.zhu_yscs = zhu_yscs;
    }

    public String getCong_yscs() {
        return cong_yscs;
    }

    public void setCong_yscs(String cong_yscs) {
        this.cong_yscs = cong_yscs;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getWire() {
        return wire;
    }

    public void setWire(String wire) {
        this.wire = wire;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

    public int getDuan() {
        return duan;
    }

    public void setDuan(int duan) {
        this.duan = duan;
    }

    public int getDuanNo() {
        return duanNo;
    }

    public void setDuanNo(int duanNo) {
        this.duanNo = duanNo;
    }

    public String getFanzhuan() {
        return fanzhuan;
    }

    public void setFanzhuan(String fanzhuan) {
        this.fanzhuan = fanzhuan;
    }

    public String getPai() {
        return pai;
    }

    public void setPai(String pai) {
        this.pai = pai;
    }

    public String getQibao() {
        return qibao;
    }

    public void setQibao(String qibao) {
        this.qibao = qibao;
    }

    @Override
    public int compareTo(SingleRegisterDenator denator) {
        // 检查 null
        if (denator == null) {
            throw new NullPointerException("Cannot compare with null");
        }

        // 获取管壳码
        String shellBlastNo1 = this.shellBlastNo;
        String shellBlastNo2 = denator.getShellBlastNo();

        // 处理 A6 开头的特殊情况
        boolean isA6_1 = shellBlastNo1.startsWith("A6");
        boolean isA6_2 = shellBlastNo2.startsWith("A6");

        if (isA6_1 && isA6_2) {
            return 0; // 如果都以 A6 开头，视为相等
        } else if (isA6_1) {
            return -1; // 如果当前对象以 A6 开头，优先排序
        } else if (isA6_2) {
            return 1; // 如果参数对象以 A6 开头，参数对象优先
        }

        // 检查长度是否为 13
        if (shellBlastNo1.length() != 13 || shellBlastNo2.length() != 13) {
            // 如果长度不为 13，视为无效数据，当前对象优先
            return shellBlastNo1.length() == 13 ? -1 : 1;
        }

        // 解析日期部分
        SimpleDateFormat md = new SimpleDateFormat("MMdd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = md.parse(shellBlastNo1.substring(3, 7));
            date2 = md.parse(shellBlastNo2.substring(3, 7));
        } catch (ParseException e) {
            // 如果日期解析失败，视为无效数据，当前对象优先
            return shellBlastNo1.substring(3, 7).compareTo(shellBlastNo2.substring(3, 7));
        }

        // 比较日期
        int dateCompare = date1.compareTo(date2);
        if (dateCompare != 0) {
            return dateCompare;
        }

        // 比较流水号
        int liushui1 = Integer.parseInt(shellBlastNo1.substring(8));
        int liushui2 = Integer.parseInt(shellBlastNo2.substring(8));
        return Integer.compare(liushui1, liushui2);
    }
}
