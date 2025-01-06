package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "PaiData")
public class PaiData {

    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "paiId")
    private int paiId;
    @Property(nameInDb = "qyid")
    private int qyid;
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
    @Property(nameInDb = "neiDelay")
    private String neiDelay;
    @Property(nameInDb = "paiDelay")
    private String paiDelay;
    @Property(nameInDb = "kongNum")
    private int kongNum;
    @Property(nameInDb = "diJian")
    private boolean diJian;
    @Generated(hash = 854914857)
    public PaiData(Long id, int paiId, int qyid, String sum, String delayMin,
            String delayMax, String shouquan, String startDelay, String kongDelay,
            String neiDelay, String paiDelay, int kongNum, boolean diJian) {
        this.id = id;
        this.paiId = paiId;
        this.qyid = qyid;
        this.sum = sum;
        this.delayMin = delayMin;
        this.delayMax = delayMax;
        this.shouquan = shouquan;
        this.startDelay = startDelay;
        this.kongDelay = kongDelay;
        this.neiDelay = neiDelay;
        this.paiDelay = paiDelay;
        this.kongNum = kongNum;
        this.diJian = diJian;
    }
    @Generated(hash = 1436589176)
    public PaiData() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getPaiId() {
        return this.paiId;
    }
    public void setPaiId(int paiId) {
        this.paiId = paiId;
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
    public String getNeiDelay() {
        return this.neiDelay;
    }
    public void setNeiDelay(String neiDelay) {
        this.neiDelay = neiDelay;
    }
    public String getPaiDelay() {
        return this.paiDelay;
    }
    public void setPaiDelay(String paiDelay) {
        this.paiDelay = paiDelay;
    }
    public int getQyid() {
        return this.qyid;
    }
    public void setQyid(int qyid) {
        this.qyid = qyid;
    }
    public boolean getDiJian() {
        return this.diJian;
    }
    public void setDiJian(boolean diJian) {
        this.diJian = diJian;
    }
    public int getKongNum() {
        return this.kongNum;
    }
    public void setKongNum(int kongNum) {
        this.kongNum = kongNum;
    }

    @Override
    public String toString() {
        return "PaiData{" +
                "id=" + id +
                ", paiId=" + paiId +
                ", qyid=" + qyid +
                ", sum='" + sum + '\'' +
                ", delayMin='" + delayMin + '\'' +
                ", delayMax='" + delayMax + '\'' +
                ", shouquan='" + shouquan + '\'' +
                ", startDelay='" + startDelay + '\'' +
                ", kongDelay='" + kongDelay + '\'' +
                ", neiDelay='" + neiDelay + '\'' +
                ", paiDelay='" + paiDelay + '\'' +
                ", kongNum=" + kongNum +
                ", diJian=" + diJian +
                '}';
    }
}
