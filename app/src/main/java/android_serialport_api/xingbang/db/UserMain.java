package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by xingbang on 2021/2/2.
 */
@Entity(nameInDb = "UserMain")
public class UserMain {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "uname")
    private String uname;
    @Property(nameInDb = "upassword")
    private String upassword;
    @Property(nameInDb = "isface")
    private String isface;
    @Property(nameInDb = "facepath")
    private String facepath;
    @Generated(hash = 474102012)
    public UserMain(Long id, String uname, String upassword, String isface,
            String facepath) {
        this.id = id;
        this.uname = uname;
        this.upassword = upassword;
        this.isface = isface;
        this.facepath = facepath;
    }
    @Generated(hash = 959343184)
    public UserMain() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUname() {
        return this.uname;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }
    public String getUpassword() {
        return this.upassword;
    }
    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }
    public String getIsface() {
        return this.isface;
    }
    public void setIsface(String isface) {
        this.isface = isface;
    }
    public String getFacepath() {
        return this.facepath;
    }
    public void setFacepath(String facepath) {
        this.facepath = facepath;
    }



}
