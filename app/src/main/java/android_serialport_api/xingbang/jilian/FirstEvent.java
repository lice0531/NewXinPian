package android_serialport_api.xingbang.jilian;

/**
 * Created by Administrator on 2016/6/8.
 */
public class FirstEvent {
    private String mMsg;

    private String data;

    private String position;

    private int tureNum;

    private int errNum;

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
    public FirstEvent(String msg, String data, String position,int tureNum,int errNum) {
        this.mMsg = msg;
        this.data = data;
        this.position = position;
        this.tureNum = tureNum;
        this.errNum = errNum;
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

    public int getTureNum() {
        return tureNum;
    }

    public void setTureNum(int tureNum) {
        this.tureNum = tureNum;
    }

    public int getErrNum() {
        return errNum;
    }

    public void setErrNum(int errNum) {
        this.errNum = errNum;
    }
}
