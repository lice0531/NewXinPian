package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xingbang on 2019/11/23.
 */
@Entity(nameInDb = "Message")
public class MessageBean {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "pro_bprysfz")
    private String pro_bprysfz;
    @Property(nameInDb = "pro_htid")
    private String pro_htid;
    @Property(nameInDb = "pro_xmbh")
    private String pro_xmbh;
    @Property(nameInDb = "equ_no")
    private String equ_no;
    @Property(nameInDb = "pro_coordxy")
    private String pro_coordxy;
    @Property(nameInDb = "server_addr")
    private String server_addr;
    @Property(nameInDb = "server_port")
    private String server_port;
    @Property(nameInDb = "server_http")
    private String server_http;
    @Property(nameInDb = "server_ip")
    private String server_ip;
    @Property(nameInDb = "qiaosi_set")
    private String qiaosi_set;
    @Property(nameInDb = "preparation_time")
    private String preparation_time;
    @Property(nameInDb = "chongdian_time")
    private String chongdian_time;
    @Property(nameInDb = "server_type1")
    private String server_type1;
    @Property(nameInDb = "server_type2")
    private String server_type2;
    @Property(nameInDb = "pro_dwdm")
    private String pro_dwdm;
    @Property(nameInDb = "jiance_time")
    private String jiance_time;
    @Property(nameInDb = "version")//代表新旧版本(01)是旧(02)是新
    private String version;
    @Generated(hash = 949237065)
    public MessageBean(Long id, String pro_bprysfz, String pro_htid,
            String pro_xmbh, String equ_no, String pro_coordxy, String server_addr,
            String server_port, String server_http, String server_ip,
            String qiaosi_set, String preparation_time, String chongdian_time,
            String server_type1, String server_type2, String pro_dwdm,
            String jiance_time, String version) {
        this.id = id;
        this.pro_bprysfz = pro_bprysfz;
        this.pro_htid = pro_htid;
        this.pro_xmbh = pro_xmbh;
        this.equ_no = equ_no;
        this.pro_coordxy = pro_coordxy;
        this.server_addr = server_addr;
        this.server_port = server_port;
        this.server_http = server_http;
        this.server_ip = server_ip;
        this.qiaosi_set = qiaosi_set;
        this.preparation_time = preparation_time;
        this.chongdian_time = chongdian_time;
        this.server_type1 = server_type1;
        this.server_type2 = server_type2;
        this.pro_dwdm = pro_dwdm;
        this.jiance_time = jiance_time;
        this.version = version;
    }
    @Generated(hash = 1588632019)
    public MessageBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPro_bprysfz() {
        return this.pro_bprysfz;
    }
    public void setPro_bprysfz(String pro_bprysfz) {
        this.pro_bprysfz = pro_bprysfz;
    }
    public String getPro_htid() {
        return this.pro_htid;
    }
    public void setPro_htid(String pro_htid) {
        this.pro_htid = pro_htid;
    }
    public String getPro_xmbh() {
        return this.pro_xmbh;
    }
    public void setPro_xmbh(String pro_xmbh) {
        this.pro_xmbh = pro_xmbh;
    }
    public String getEqu_no() {
        return this.equ_no;
    }
    public void setEqu_no(String equ_no) {
        this.equ_no = equ_no;
    }
    public String getPro_coordxy() {
        return this.pro_coordxy;
    }
    public void setPro_coordxy(String pro_coordxy) {
        this.pro_coordxy = pro_coordxy;
    }
    public String getServer_addr() {
        return this.server_addr;
    }
    public void setServer_addr(String server_addr) {
        this.server_addr = server_addr;
    }
    public String getServer_port() {
        return this.server_port;
    }
    public void setServer_port(String server_port) {
        this.server_port = server_port;
    }
    public String getServer_http() {
        return this.server_http;
    }
    public void setServer_http(String server_http) {
        this.server_http = server_http;
    }
    public String getServer_ip() {
        return this.server_ip;
    }
    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }
    public String getQiaosi_set() {
        return this.qiaosi_set;
    }
    public void setQiaosi_set(String qiaosi_set) {
        this.qiaosi_set = qiaosi_set;
    }
    public String getPreparation_time() {
        return this.preparation_time;
    }
    public void setPreparation_time(String preparation_time) {
        this.preparation_time = preparation_time;
    }
    public String getChongdian_time() {
        return this.chongdian_time;
    }
    public void setChongdian_time(String chongdian_time) {
        this.chongdian_time = chongdian_time;
    }
    public String getServer_type1() {
        return this.server_type1;
    }
    public void setServer_type1(String server_type1) {
        this.server_type1 = server_type1;
    }
    public String getServer_type2() {
        return this.server_type2;
    }
    public void setServer_type2(String server_type2) {
        this.server_type2 = server_type2;
    }
    public String getPro_dwdm() {
        return this.pro_dwdm;
    }
    public void setPro_dwdm(String pro_dwdm) {
        this.pro_dwdm = pro_dwdm;
    }
    public String getJiance_time() {
        return this.jiance_time;
    }
    public void setJiance_time(String jiance_time) {
        this.jiance_time = jiance_time;
    }
    public String getVersion() {
        return this.version;
    }
    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public String toString() {
        return "MessageBean{" +
                "id=" + id +
                ", pro_bprysfz='" + pro_bprysfz + '\'' +
                ", pro_htid='" + pro_htid + '\'' +
                ", pro_xmbh='" + pro_xmbh + '\'' +
                ", equ_no='" + equ_no + '\'' +
                ", pro_coordxy='" + pro_coordxy + '\'' +
                ", server_addr='" + server_addr + '\'' +
                ", server_port='" + server_port + '\'' +
                ", server_http='" + server_http + '\'' +
                ", server_ip='" + server_ip + '\'' +
                ", qiaosi_set='" + qiaosi_set + '\'' +
                ", preparation_time='" + preparation_time + '\'' +
                ", chongdian_time='" + chongdian_time + '\'' +
                ", server_type1='" + server_type1 + '\'' +
                ", server_type2='" + server_type2 + '\'' +
                ", pro_dwdm='" + pro_dwdm + '\'' +
                ", jiance_time='" + jiance_time + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
