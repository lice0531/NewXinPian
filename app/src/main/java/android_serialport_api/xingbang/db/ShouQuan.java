package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.litepal.crud.LitePalSupport;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xingbang on 2021/1/27.
 */
@Entity(nameInDb = "ShouQuan")
public class ShouQuan extends LitePalSupport {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "xmbh")
    private String xmbh;
    @Property(nameInDb = "htbh")
    private String htbh;
    @Property(nameInDb = "json")
    private String json;
    @Property(nameInDb = "errNum")
    private String errNum;
    @Property(nameInDb = "qbzt")
    private String qbzt;
    @Property(nameInDb = "blastdate")
    private String blastdate;
    @Property(nameInDb = "dl_state")
    private String dl_state;
    @Property(nameInDb = "zb_state")
    private String zb_state;
    @Property(nameInDb = "dwdm")
    private String dwdm;
    @Property(nameInDb = "bprysfz")
    private String bprysfz;
    @Property(nameInDb = "coordxy")
    private String coordxy;
    @Property(nameInDb = "qblgNum")
    private String qblgNum;
    @Property(nameInDb = "spare1")
    private String spare1;
    @Property(nameInDb = "spare2")
    private String spare2;

    @Generated(hash = 1544132589)
    public ShouQuan(Long id, String xmbh, String htbh, String json, String errNum,
            String qbzt, String blastdate, String dl_state, String zb_state,
            String dwdm, String bprysfz, String coordxy, String qblgNum,
            String spare1, String spare2) {
        this.id = id;
        this.xmbh = xmbh;
        this.htbh = htbh;
        this.json = json;
        this.errNum = errNum;
        this.qbzt = qbzt;
        this.blastdate = blastdate;
        this.dl_state = dl_state;
        this.zb_state = zb_state;
        this.dwdm = dwdm;
        this.bprysfz = bprysfz;
        this.coordxy = coordxy;
        this.qblgNum = qblgNum;
        this.spare1 = spare1;
        this.spare2 = spare2;
    }

    @Generated(hash = 1572074155)
    public ShouQuan() {
    }

    @Override
    public String toString() {
        return "ShouQuan{" +
                "id=" + id +
                ", xmbh='" + xmbh + '\'' +
                ", htbh='" + htbh + '\'' +
                ", json='" + json + '\'' +
                ", errNum='" + errNum + '\'' +
                ", qbzt='" + qbzt + '\'' +
                ", blastdate='" + blastdate + '\'' +
                ", dl_state='" + dl_state + '\'' +
                ", zb_state='" + zb_state + '\'' +
                ", dwdm='" + dwdm + '\'' +
                ", bprysfz='" + bprysfz + '\'' +
                ", coordxy='" + coordxy + '\'' +
                ", qblgNum='" + qblgNum + '\'' +
                ", spare1='" + spare1 + '\'' +
                ", spare2='" + spare2 + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getErrNum() {
        return errNum;
    }

    public void setErrNum(String errNum) {
        this.errNum = errNum;
    }

    public String getQbzt() {
        return qbzt;
    }

    public void setQbzt(String qbzt) {
        this.qbzt = qbzt;
    }

    public String getBlastdate() {
        return blastdate;
    }

    public void setBlastdate(String blastdate) {
        this.blastdate = blastdate;
    }

    public String getDl_state() {
        return dl_state;
    }

    public void setDl_state(String dl_state) {
        this.dl_state = dl_state;
    }

    public String getZb_state() {
        return zb_state;
    }

    public void setZb_state(String zb_state) {
        this.zb_state = zb_state;
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

    public String getQblgNum() {
        return qblgNum;
    }

    public void setQblgNum(String qblgNum) {
        this.qblgNum = qblgNum;
    }

    public String getSpare1() {
        return spare1;
    }

    public void setSpare1(String spare1) {
        this.spare1 = spare1;
    }

    public String getSpare2() {
        return spare2;
    }

    public void setSpare2(String spare2) {
        this.spare2 = spare2;
    }
}
