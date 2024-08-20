package android_serialport_api.xingbang.jianlian;

/**
 * Created by Administrator on 2016/6/8.
 */
public class FirstEvent {
    private String mMsg;

    private String data;

    private String position;

    private String tureNum;

    private String errNum;

    //电流值
    private String currentPeak;

    public FirstEvent(String msg) {
        mMsg = msg;
    }

    public FirstEvent(String msg, String data) {
        this.data = data;
        mMsg = msg;
    }
    public FirstEvent(String msg, String data, String currentPeak) {
        this.data = data;
        mMsg = msg;
        this.currentPeak = currentPeak;
    }
//    public FirstEvent(String msg, String data, String position) {
//        this.position = position;
//    }
    public FirstEvent(String msg, String data, String position, String tureNum, String errNum) {
        this.mMsg = msg;
        this.data = data;
        this.position = position;
        this.tureNum = tureNum;
        this.errNum = errNum;
    }
    public FirstEvent(String msg, String data, String position, String tureNum, String errNum, String currentPeak) {
        this.mMsg = msg;
        this.data = data;
        this.position = position;
        this.tureNum = tureNum;
        this.errNum = errNum;
        this.currentPeak = currentPeak;
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

    public String getTureNum() {
        return tureNum;
    }

    public void setTureNum(String tureNum) {
        this.tureNum = tureNum;
    }

    public String getErrNum() {
        return errNum;
    }

    public void setErrNum(String errNum) {
        this.errNum = errNum;
    }

    public String getCurrentPeak() {
        return currentPeak;
    }

    public void setCurrentPeak(String currentPeak) {
        this.currentPeak = currentPeak;
    }
}
