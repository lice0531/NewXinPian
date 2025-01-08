package android_serialport_api.xingbang.custom;


import android_serialport_api.xingbang.db.PaiData;

public class PaiDataSelect extends PaiData {
    private boolean select = false;


    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    public String toString() {
        return "PaiDataSelect{" +
                "select=" + select +
                '}';
    }
}
