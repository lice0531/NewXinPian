package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * @ClassName: DetonatorTypeNew
 * @Description: 雷管类型_新
 * @Date: 2021/7/12 14:40
 * @Author: kalinaji
 */
@Entity(nameInDb = "DetonatorTypeNew")
public class DetonatorTypeNew {

    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long Id;             // 雷管自增ID--0
    @Property(nameInDb = "shellBlastNo")
    private String shellBlastNo;   // 管壳码——1
    @Property(nameInDb = "denatorId")
    private String detonatorId;    // 芯片码——2
    @Property(nameInDb = "denatorIdSup")
    private String detonatorIdSup;    // 从芯片码——3
    @Property(nameInDb = "zhu_yscs")
    private String zhu_yscs;//主芯片延时参数
    @Property(nameInDb = "cong_yscs")
    private String cong_yscs;//从芯片延时参数
    @Property(nameInDb = "time")//牵扯到删除问题(自动删除历史记录的时候,删除相同的下载数据,所以时间是22-10-20)
    private String time;//从芯片延时参数
    @Property(nameInDb = "qibao")
    private String qibao;//是否起爆

    @Generated(hash = 1535113931)
    public DetonatorTypeNew(Long Id, String shellBlastNo, String detonatorId,
            String detonatorIdSup, String zhu_yscs, String cong_yscs, String time,
            String qibao) {
        this.Id = Id;
        this.shellBlastNo = shellBlastNo;
        this.detonatorId = detonatorId;
        this.detonatorIdSup = detonatorIdSup;
        this.zhu_yscs = zhu_yscs;
        this.cong_yscs = cong_yscs;
        this.time = time;
        this.qibao = qibao;
    }
    @Generated(hash = 1791749386)
    public DetonatorTypeNew() {
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getShellBlastNo() {
        return this.shellBlastNo;
    }
    public void setShellBlastNo(String shellBlastNo) {
        this.shellBlastNo = shellBlastNo;
    }
    public String getDetonatorId() {
        return this.detonatorId;
    }
    public void setDetonatorId(String detonatorId) {
        this.detonatorId = detonatorId;
    }
    public String getDetonatorIdSup() {
        return this.detonatorIdSup;
    }
    public void setDetonatorIdSup(String detonatorIdSup) {
        this.detonatorIdSup = detonatorIdSup;
    }
    public String getZhu_yscs() {
        return this.zhu_yscs;
    }
    public void setZhu_yscs(String zhu_yscs) {
        this.zhu_yscs = zhu_yscs;
    }
    public String getCong_yscs() {
        return this.cong_yscs;
    }
    public void setCong_yscs(String cong_yscs) {
        this.cong_yscs = cong_yscs;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getQibao() {
        return this.qibao;
    }
    public void setQibao(String qibao) {
        this.qibao = qibao;
    }

    @Override
    public String toString() {
        return "DetonatorTypeNew{" +
                "Id=" + Id +
                ", shellBlastNo='" + shellBlastNo + '\'' +
                ", detonatorId='" + detonatorId + '\'' +
                ", detonatorIdSup='" + detonatorIdSup + '\'' +
                ", zhu_yscs='" + zhu_yscs + '\'' +
                ", cong_yscs='" + cong_yscs + '\'' +
                ", time='" + time + '\'' +
                ", qibao='" + qibao + '\'' +
                '}';
    }
}
