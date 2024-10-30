package android_serialport_api.mx.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xingbang on 2021/2/2.
 */
@Entity(nameInDb = "denatorHis_Detail")
public class DenatorHis_Detail {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "blastserial")
    private int blastserial;
    @Property(nameInDb = "sithole")
    private String sithole;
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
    @Property(nameInDb = "blastdate")
    private String blastdate;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "piece")
    private String piece;
    @Generated(hash = 929310764)
    public DenatorHis_Detail(Long id, int blastserial, String sithole,
            String shellBlastNo, String denatorId, int delay, String statusCode,
            String statusName, String errorName, String errorCode,
            String authorization, String remark, String regdate, String blastdate,
            String name, String piece) {
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
        this.blastdate = blastdate;
        this.name = name;
        this.piece = piece;
    }
    @Generated(hash = 1925319946)
    public DenatorHis_Detail() {
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
    public String getSithole() {
        return this.sithole;
    }
    public void setSithole(String sithole) {
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
    public String getBlastdate() {
        return this.blastdate;
    }
    public void setBlastdate(String blastdate) {
        this.blastdate = blastdate;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPiece() {
        return this.piece;
    }
    public void setPiece(String piece) {
        this.piece = piece;
    }


    @Override
    public String toString() {
        return "DenatorHis_Detail{" +
                "id=" + id +
                ", blastserial=" + blastserial +
                ", sithole='" + sithole + '\'' +
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
                ", blastdate='" + blastdate + '\'' +
                ", name='" + name + '\'' +
                ", piece='" + piece + '\'' +
                '}';
    }
}
