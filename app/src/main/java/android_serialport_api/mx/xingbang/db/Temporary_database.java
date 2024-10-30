package android_serialport_api.mx.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "Temporary_database")
public class Temporary_database {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "denatorId")
    private String denatorId;
    @Property(nameInDb = "shellBlastNo")
    private String shellBlastNo;
    @Generated(hash = 964621531)
    public Temporary_database(Long id, String denatorId, String shellBlastNo) {
        this.id = id;
        this.denatorId = denatorId;
        this.shellBlastNo = shellBlastNo;
    }
    @Generated(hash = 193547012)
    public Temporary_database() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDenatorId() {
        return this.denatorId;
    }
    public void setDenatorId(String denatorId) {
        this.denatorId = denatorId;
    }
    public String getShellBlastNo() {
        return this.shellBlastNo;
    }
    public void setShellBlastNo(String shellBlastNo) {
        this.shellBlastNo = shellBlastNo;
    }
}
