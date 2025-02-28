package android_serialport_api.xingbang.custom;
import android_serialport_api.xingbang.db.SingleRegisterDenator;

public class SingleRegisterData extends SingleRegisterDenator {
    private boolean select = false;


    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

}
