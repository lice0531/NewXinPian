package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

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
    @Property(nameInDb = "uCid")
    private String uCid;
    @Property(nameInDb = "uCName")
    private String uCName;
    @Property(nameInDb = "uFName")
    private String uFName;
    @Property(nameInDb = "uIDCard")
    private String uIDCard;


    @Generated(hash = 1555218758)
    public UserMain(Long id, String uname, String upassword, String isface,
            String facepath, String uCid, String uCName, String uFName,
            String uIDCard) {
        this.id = id;
        this.uname = uname;
        this.upassword = upassword;
        this.isface = isface;
        this.facepath = facepath;
        this.uCid = uCid;
        this.uCName = uCName;
        this.uFName = uFName;
        this.uIDCard = uIDCard;
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
    public String getUCid() {
        return this.uCid;
    }
    public void setUCid(String uCid) {
        this.uCid = uCid;
    }
    public String getUCName() {
        return this.uCName;
    }
    public void setUCName(String uCName) {
        this.uCName = uCName;
    }
    public String getUFName() {
        return this.uFName;
    }
    public void setUFName(String uFName) {
        this.uFName = uFName;
    }
    public String getUIDCard() {
        return this.uIDCard;
    }
    public void setUIDCard(String uIDCard) {
        this.uIDCard = uIDCard;
    }



}
