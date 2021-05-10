package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.litepal.crud.LitePalSupport;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xingbang on 2021/1/7.
 */
@Entity(nameInDb = "Defactory")
public class Defactory extends LitePalSupport {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "deName")
    private String deName;//雷管名称
    @Property(nameInDb = "deEntCode")
    private String deEntCode;//雷管代号
    @Property(nameInDb = "deFeatureCode")
    private String deFeatureCode;//厂家代号
    @Property(nameInDb = "isSelected")
    private String isSelected;//是否选择

    @Generated(hash = 608545312)
    public Defactory(Long id, String deName, String deEntCode, String deFeatureCode,
            String isSelected) {
        this.id = id;
        this.deName = deName;
        this.deEntCode = deEntCode;
        this.deFeatureCode = deFeatureCode;
        this.isSelected = isSelected;
    }

    @Generated(hash = 1506150089)
    public Defactory() {
    }

    @Override
    public String toString() {
        return "Defactory{" +
                "id=" + id +
                ", deName='" + deName + '\'' +
                ", deEntCode='" + deEntCode + '\'' +
                ", deFeatureCode='" + deFeatureCode + '\'' +
                ", isSelected='" + isSelected + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeName() {
        return deName;
    }

    public void setDeName(String deName) {
        this.deName = deName;
    }

    public String getDeEntCode() {
        return deEntCode;
    }

    public void setDeEntCode(String deEntCode) {
        this.deEntCode = deEntCode;
    }

    public String getDeFeatureCode() {
        return deFeatureCode;
    }

    public void setDeFeatureCode(String deFeatureCode) {
        this.deFeatureCode = deFeatureCode;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }
}
