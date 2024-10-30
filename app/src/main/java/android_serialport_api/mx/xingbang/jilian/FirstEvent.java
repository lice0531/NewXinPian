package android_serialport_api.mx.xingbang.jilian;

/**
 * Created by Administrator on 2016/6/8.
 */
public class FirstEvent {
    private String mMsg;

    private String data;

    private String position;

    public FirstEvent(String msg) {
        mMsg = msg;
    }

    public FirstEvent(String msg, String data) {
        this.data = data;
        mMsg = msg;
    }

    public FirstEvent(String msg, String data, String position) {
        this.position = position;
    }

    public String getMsg() {
        return mMsg;
    }

    public String getData() {
        return data;
    }

    public String getPosition() {
        return position;
    }
}
