package android_serialport_api.mx.xingbang.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xingbang on 2021/2/2.
 */
@Entity(nameInDb = "denator_type")
public class Denator_type {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "deTypeName")
    private String deTypeName;
    @Property(nameInDb = "deTypeSecond")
    private String deTypeSecond;
    @Property(nameInDb = "isSelected")
    private String isSelected;
    @Generated(hash = 647656831)
    public Denator_type(Long id, String deTypeName, String deTypeSecond,
            String isSelected) {
        this.id = id;
        this.deTypeName = deTypeName;
        this.deTypeSecond = deTypeSecond;
        this.isSelected = isSelected;
    }
    @Generated(hash = 1553345018)
    public Denator_type() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDeTypeName() {
        return this.deTypeName;
    }
    public void setDeTypeName(String deTypeName) {
        this.deTypeName = deTypeName;
    }
    public String getDeTypeSecond() {
        return this.deTypeSecond;
    }
    public void setDeTypeSecond(String deTypeSecond) {
        this.deTypeSecond = deTypeSecond;
    }
    public String getIsSelected() {
        return this.isSelected;
    }
    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }


}
