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

    @Generated(hash = 1483443620)
    public DetonatorTypeNew(Long Id, String shellBlastNo, String detonatorId) {
        this.Id = Id;
        this.shellBlastNo = shellBlastNo;
        this.detonatorId = detonatorId;
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

 

}
