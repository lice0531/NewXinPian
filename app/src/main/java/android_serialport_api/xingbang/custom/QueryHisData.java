package android_serialport_api.xingbang.custom;


import android_serialport_api.xingbang.models.VoFireHisMain;

public class QueryHisData extends VoFireHisMain {
    private boolean select = false;


    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

}
