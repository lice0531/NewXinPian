package android_serialport_api.xingbang.db;

import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

public class QuYu {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "qyid")
    private String qyid;
    @Property(nameInDb = "sum")
    private String sum;
    @Property(nameInDb = "delayMin")
    private String delayMin;
    @Property(nameInDb = "delayMax")
    private String delayMax;

}
