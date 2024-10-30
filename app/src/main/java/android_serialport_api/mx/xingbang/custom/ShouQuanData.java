package android_serialport_api.mx.xingbang.custom;


import android_serialport_api.mx.xingbang.db.DetonatorTypeNew;

public class ShouQuanData extends DetonatorTypeNew {
    private boolean select = false;


    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

}
