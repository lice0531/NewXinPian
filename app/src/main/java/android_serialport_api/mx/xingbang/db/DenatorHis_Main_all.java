package android_serialport_api.mx.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xingbang on 2021/2/2.
 */
@Entity(nameInDb = "denatorHis_Main_all")
public class DenatorHis_Main_all {
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
    @Property(nameInDb = "firedNo")
    private String firedNo;
    @Property(nameInDb = "serialNo")
    private int serialNo;
    @Property(nameInDb = "contactNo")
    private String contactNo;
    @Property(nameInDb = "projectNo")
    private String projectNo;
    @Property(nameInDb = "remark")
    private String remark;
    @Generated(hash = 701214006)
    public DenatorHis_Main_all(Long id, String blastdate, String uploadStatus,
            String longitude, String latitude, String userid, String firedNo,
            int serialNo, String contactNo, String projectNo, String remark) {
        this.id = id;
        this.blastdate = blastdate;
        this.uploadStatus = uploadStatus;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userid = userid;
        this.firedNo = firedNo;
        this.serialNo = serialNo;
        this.contactNo = contactNo;
        this.projectNo = projectNo;
        this.remark = remark;
    }
    @Generated(hash = 2059840929)
    public DenatorHis_Main_all() {
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
    public String getFiredNo() {
        return this.firedNo;
    }
    public void setFiredNo(String firedNo) {
        this.firedNo = firedNo;
    }
    public int getSerialNo() {
        return this.serialNo;
    }
    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }
    public String getContactNo() {
        return this.contactNo;
    }
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    public String getProjectNo() {
        return this.projectNo;
    }
    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }


}
