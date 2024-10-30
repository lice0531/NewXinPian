package android_serialport_api.mx.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.litepal.crud.LitePalSupport;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xingbang on 2020/12/25.
 * <p>
 * + "id integer primary key autoincrement, "
 * + "project_name text, "//项目名称
 * + "xmbh text, "//项目编号
 * + "htbh text, "//合同编号
 * + "dwdm text,"//单位代码
 * + "bprysfz text,"//爆破员身份证
 * + "coordxy text"//经纬度
 * + ")";
 */
@Entity(nameInDb = "Project")
public class Project extends LitePalSupport {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "project_name")
    private String project_name;//项目名称
    @Property(nameInDb = "xmbh")
    private String xmbh;//项目编号
    @Property(nameInDb = "htbh")
    private String htbh;//合同编号
    @Property(nameInDb = "dwdm")
    private String dwdm;//单位代码
    @Property(nameInDb = "bprysfz")
    private String bprysfz;//爆破员身份证
    @Property(nameInDb = "coordxy")
    private String coordxy;//经纬度

    @Generated(hash = 1076235564)
    public Project(Long id, String project_name, String xmbh, String htbh,
            String dwdm, String bprysfz, String coordxy) {
        this.id = id;
        this.project_name = project_name;
        this.xmbh = xmbh;
        this.htbh = htbh;
        this.dwdm = dwdm;
        this.bprysfz = bprysfz;
        this.coordxy = coordxy;
    }

    @Generated(hash = 1767516619)
    public Project() {
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", project_name='" + project_name + '\'' +
                ", xmbh='" + xmbh + '\'' +
                ", htbh='" + htbh + '\'' +
                ", dwdm='" + dwdm + '\'' +
                ", bprysfz='" + bprysfz + '\'' +
                ", coordxy='" + coordxy + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getXmbh() {
        return xmbh;
    }

    public void setXmbh(String xmbh) {
        this.xmbh = xmbh;
    }

    public String getHtbh() {
        return htbh;
    }

    public void setHtbh(String htbh) {
        this.htbh = htbh;
    }

    public String getDwdm() {
        return dwdm;
    }

    public void setDwdm(String dwdm) {
        this.dwdm = dwdm;
    }

    public String getBprysfz() {
        return bprysfz;
    }

    public void setBprysfz(String bprysfz) {
        this.bprysfz = bprysfz;
    }

    public String getCoordxy() {
        return coordxy;
    }

    public void setCoordxy(String coordxy) {
        this.coordxy = coordxy;
    }
}
