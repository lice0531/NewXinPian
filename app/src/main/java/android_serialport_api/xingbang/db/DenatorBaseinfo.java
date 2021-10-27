package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.litepal.crud.LitePalSupport;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xingbang on 2020/6/4.
 */
@Entity(nameInDb = "denatorBaseinfo")
public class DenatorBaseinfo extends LitePalSupport {

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
    @Property(nameInDb = "denatorId2")
    private String denatorId2;


    @Generated(hash = 637017625)
    public DenatorBaseinfo(Long id, int blastserial, int sithole,
            String shellBlastNo, String denatorId, int delay, String statusCode,
            String statusName, String errorName, String errorCode,
            String authorization, String remark, String regdate, String wire,
            String name, String denatorId2) {
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
        this.denatorId2 = denatorId2;
    }


    @Generated(hash = 1775503899)
    public DenatorBaseinfo() {
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
                ", denatorId2='" + denatorId2 + '\'' +
                '}';
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


    public String getDenatorId2() {
        return this.denatorId2;
    }


    public void setDenatorId2(String denatorId2) {
        this.denatorId2 = denatorId2;
    }
}
