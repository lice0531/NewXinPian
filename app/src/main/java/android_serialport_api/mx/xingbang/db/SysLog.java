package android_serialport_api.mx.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by xingbang on 2021/3/3.
 */
@Entity(nameInDb = "SysLog")
public class SysLog {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "filename")
    private String filename;
    @Property(nameInDb = "path")
    private String path;
    @Property(nameInDb = "updataState")
    private String updataState;
    @Property(nameInDb = "updataTime")
    private String updataTime;
    @Generated(hash = 1726499208)
    public SysLog(Long id, String filename, String path, String updataState,
            String updataTime) {
        this.id = id;
        this.filename = filename;
        this.path = path;
        this.updataState = updataState;
        this.updataTime = updataTime;
    }

    @Override
    public String toString() {
        return "SysLog{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", path='" + path + '\'' +
                ", updataState='" + updataState + '\'' +
                ", updataTime='" + updataTime + '\'' +
                '}';
    }

    @Generated(hash = 1793142996)
    public SysLog() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFilename() {
        return this.filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getUpdataState() {
        return this.updataState;
    }
    public void setUpdataState(String updataState) {
        this.updataState = updataState;
    }
    public String getUpdataTime() {
        return this.updataTime;
    }
    public void setUpdataTime(String updataTime) {
        this.updataTime = updataTime;
    }

}
