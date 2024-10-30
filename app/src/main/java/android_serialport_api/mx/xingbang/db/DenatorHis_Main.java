package android_serialport_api.mx.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xingbang on 2021/2/2.
 */
@Entity(nameInDb = "denatorHis_Main")
public class DenatorHis_Main {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "blastdate")
    private String blastdate;
    @Property(nameInDb = "uploadStatus")
    private String uploadStatus;
    @Property(nameInDb = "longitude")
    private String longitude;
    @Property(nameInDb = "latitude")
    private String latitude;
    @Property(nameInDb = "userid")
    private String userid;
    @Property(nameInDb = "equ_no")
    private String equ_no;
    @Property(nameInDb = "serialNo")
    private int serialNo;
    @Property(nameInDb = "pro_xmbh")
    private String pro_xmbh;
    @Property(nameInDb = "pro_htid")
    private String pro_htid;
    @Property(nameInDb = "pro_dwdm")
    private String pro_dwdm;
    @Property(nameInDb = "remark")
    private String remark;
    @Property(nameInDb = "log")
    private String log;
    @Generated(hash = 960757556)
    public DenatorHis_Main(Long id, String blastdate, String uploadStatus,
            String longitude, String latitude, String userid, String equ_no,
            int serialNo, String pro_xmbh, String pro_htid, String pro_dwdm,
            String remark, String log) {
        this.id = id;
        this.blastdate = blastdate;
        this.uploadStatus = uploadStatus;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userid = userid;
        this.equ_no = equ_no;
        this.serialNo = serialNo;
        this.pro_xmbh = pro_xmbh;
        this.pro_htid = pro_htid;
        this.pro_dwdm = pro_dwdm;
        this.remark = remark;
        this.log = log;
    }
    @Generated(hash = 812921624)
    public DenatorHis_Main() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBlastdate() {
        return this.blastdate;
    }
    public void setBlastdate(String blastdate) {
        this.blastdate = blastdate;
    }
    public String getUploadStatus() {
        return this.uploadStatus;
    }
    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
    public String getLongitude() {
        return this.longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLatitude() {
        return this.latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getUserid() {
        return this.userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getEqu_no() {
        return this.equ_no;
    }
    public void setEqu_no(String equ_no) {
        this.equ_no = equ_no;
    }
    public int getSerialNo() {
        return this.serialNo;
    }
    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }
    public String getPro_xmbh() {
        return this.pro_xmbh;
    }
    public void setPro_xmbh(String pro_xmbh) {
        this.pro_xmbh = pro_xmbh;
    }
    public String getPro_htid() {
        return this.pro_htid;
    }
    public void setPro_htid(String pro_htid) {
        this.pro_htid = pro_htid;
    }
    public String getPro_dwdm() {
        return this.pro_dwdm;
    }
    public void setPro_dwdm(String pro_dwdm) {
        this.pro_dwdm = pro_dwdm;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getLog() {
        return this.log;
    }
    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public String toString() {
        return "DenatorHis_Main{" +
                "id=" + id +
                ", blastdate='" + blastdate + '\'' +
                ", uploadStatus='" + uploadStatus + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", userid='" + userid + '\'' +
                ", equ_no='" + equ_no + '\'' +
                ", serialNo=" + serialNo +
                ", pro_xmbh='" + pro_xmbh + '\'' +
                ", pro_htid='" + pro_htid + '\'' +
                ", pro_dwdm='" + pro_dwdm + '\'' +
                ", remark='" + remark + '\'' +
                ", log='" + log + '\'' +
                '}';
    }
}
