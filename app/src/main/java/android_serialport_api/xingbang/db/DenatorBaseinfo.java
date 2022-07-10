package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.litepal.crud.LitePalSupport;
import org.greenrobot.greendao.annotation.Generated;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xingbang on 2020/6/4.
 */
@Entity(nameInDb = "denatorBaseinfo")
public class DenatorBaseinfo extends LitePalSupport implements Comparable<DenatorBaseinfo>{

    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "blastserial")
    private int blastserial;
    @Property(nameInDb = "sithole")
    private int sithole;
    @Property(nameInDb = "shellBlastNo")
    private String shellBlastNo;
    @Property(nameInDb = "denatorId")
    private String denatorId;
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
    @Property(nameInDb = "denatorIdSup")
    private String denatorIdSup;
    @Property(nameInDb = "zhu_yscs")
    private String zhu_yscs;//
    @Property(nameInDb = "cong_yscs")
    private String cong_yscs;//
    @Property(nameInDb = "piece")
    private String piece;           // 区域

    @Generated(hash = 648500599)
    public DenatorBaseinfo(Long id, int blastserial, int sithole, String shellBlastNo,
            String denatorId, int delay, String statusCode, String statusName,
            String errorName, String errorCode, String authorization, String remark,
            String regdate, String wire, String name, String denatorIdSup, String zhu_yscs,
            String cong_yscs, String piece) {
        this.id = id;
        this.blastserial = blastserial;
        this.sithole = sithole;
        this.shellBlastNo = shellBlastNo;
        this.denatorId = denatorId;
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
        this.denatorIdSup = denatorIdSup;
        this.zhu_yscs = zhu_yscs;
        this.cong_yscs = cong_yscs;
        this.piece = piece;
    }
    @Generated(hash = 1775503899)
    public DenatorBaseinfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getBlastserial() {
        return this.blastserial;
    }
    public void setBlastserial(int blastserial) {
        this.blastserial = blastserial;
    }
    public int getSithole() {
        return this.sithole;
    }
    public void setSithole(int sithole) {
        this.sithole = sithole;
    }
    public String getShellBlastNo() {
        return this.shellBlastNo;
    }
    public void setShellBlastNo(String shellBlastNo) {
        this.shellBlastNo = shellBlastNo;
    }
    public String getDenatorId() {
        return this.denatorId;
    }
    public void setDenatorId(String denatorId) {
        this.denatorId = denatorId;
    }
    public int getDelay() {
        return this.delay;
    }
    public void setDelay(int delay) {
        this.delay = delay;
    }
    public String getStatusCode() {
        return this.statusCode;
    }
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    public String getStatusName() {
        return this.statusName;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    public String getErrorName() {
        return this.errorName;
    }
    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }
    public String getErrorCode() {
        return this.errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getAuthorization() {
        return this.authorization;
    }
    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRegdate() {
        return this.regdate;
    }
    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }
    public String getWire() {
        return this.wire;
    }
    public void setWire(String wire) {
        this.wire = wire;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDenatorIdSup() {
        return this.denatorIdSup;
    }
    public void setDenatorIdSup(String denatorIdSup) {
        this.denatorIdSup = denatorIdSup;
    }
    public String getZhu_yscs() {
        return this.zhu_yscs;
    }
    public void setZhu_yscs(String zhu_yscs) {
        this.zhu_yscs = zhu_yscs;
    }
    public String getCong_yscs() {
        return this.cong_yscs;
    }
    public void setCong_yscs(String cong_yscs) {
        this.cong_yscs = cong_yscs;
    }

    @Override
    public String toString() {
        return "DenatorBaseinfo{" +
                "id=" + id +
                ", blastserial=" + blastserial +
                ", sithole=" + sithole +
                ", shellBlastNo='" + shellBlastNo + '\'' +
                ", denatorId='" + denatorId + '\'' +
                ", delay=" + delay +
                ", statusCode='" + statusCode + '\'' +
                ", statusName='" + statusName + '\'' +
                ", errorName='" + errorName + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", authorization='" + authorization + '\'' +
                ", remark='" + remark + '\'' +
                ", regdate='" + regdate + '\'' +
                ", wire='" + wire + '\'' +
                ", name='" + name + '\'' +
                ", denatorIdSup='" + denatorIdSup + '\'' +
                ", zhu_yscs='" + zhu_yscs + '\'' +
                ", cong_yscs='" + cong_yscs + '\'' +
                ", piece='" + piece + '\'' +
                '}';
    }

    @Override
    public int compareTo(DenatorBaseinfo denator) {//53904180500000
        // 返回值0代表相等，1表示大于，-1表示小于；
        if(denator.getShellBlastNo().contains("A6")){
            return -1;
        }
        if(shellBlastNo.contains("A6")){
            return -1;
        }
        SimpleDateFormat md = new SimpleDateFormat("MMdd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = md.parse(shellBlastNo.substring(3, 7));
            date2 = md.parse(denator.getShellBlastNo().substring(3, 7));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int liushui1 = Integer.parseInt(shellBlastNo.substring(9));
        int liushui2 = Integer.parseInt(denator.getShellBlastNo().substring(9));

        if (date1.before(date2)) {
            return 1;
        } else if (date1.after(date2)) {
            return -1;
        } else {
            if (liushui1 > liushui2) {
                return -1;
            } else if (liushui1 < liushui2) {
                return 1;
            } else {
                return 0;
            }
        }

    }
    public String getPiece() {
        return this.piece;
    }
    public void setPiece(String piece) {
        this.piece = piece;
    }
}
