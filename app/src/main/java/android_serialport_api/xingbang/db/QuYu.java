package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
@Entity(nameInDb = "QuYu")
public class QuYu {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "qyid")
    private int qyid;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "sum")
    private String sum;
    @Property(nameInDb = "delayMin")
    private String delayMin;
    @Property(nameInDb = "delayMax")
    private String delayMax;
    @Property(nameInDb = "shouquan")
    private String shouquan;
    @Property(nameInDb = "startDelay")
    private String startDelay;
    @Property(nameInDb = "kongDelay")
    private String kongDelay;
    @Property(nameInDb = "paiDelay")
    private String paiDelay;
    @Generated(hash = 922467455)
    public QuYu(Long id, int qyid, String name, String sum, String delayMin,
            String delayMax, String shouquan, String startDelay, String kongDelay,
            String paiDelay) {
        this.id = id;
        this.qyid = qyid;
        this.name = name;
        this.sum = sum;
        this.delayMin = delayMin;
        this.delayMax = delayMax;
        this.shouquan = shouquan;
        this.startDelay = startDelay;
        this.kongDelay = kongDelay;
        this.paiDelay = paiDelay;
    }
    @Generated(hash = 206389652)
    public QuYu() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getQyid() {
        return this.qyid;
    }
    public void setQyid(int qyid) {
        this.qyid = qyid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSum() {
        return this.sum;
    }
    public void setSum(String sum) {
        this.sum = sum;
    }
    public String getDelayMin() {
        return this.delayMin;
    }
    public void setDelayMin(String delayMin) {
        this.delayMin = delayMin;
    }
    public String getDelayMax() {
        return this.delayMax;
    }
    public void setDelayMax(String delayMax) {
        this.delayMax = delayMax;
    }
    public String getShouquan() {
        return this.shouquan;
    }
    public void setShouquan(String shouquan) {
        this.shouquan = shouquan;
    }
    public String getStartDelay() {
        return this.startDelay;
    }
    public void setStartDelay(String startDelay) {
        this.startDelay = startDelay;
    }
    public String getKongDelay() {
        return this.kongDelay;
    }
    public void setKongDelay(String kongDelay) {
        this.kongDelay = kongDelay;
    }
    public String getPaiDelay() {
        return this.paiDelay;
    }
    public void setPaiDelay(String paiDelay) {
        this.paiDelay = paiDelay;
    }
   

}
