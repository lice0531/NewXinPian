package android_serialport_api.xingbang.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.Socket;

/**
 * Created by lice on 2023/10/8.
 */

public class DeviceBean implements Parcelable {
    private String name;
    private String res;
    private String code;
    private String info;
    private String trueNum;
    private String errNum;
    private String currentPeak;
    private Socket socket;
    private float busVoltage;

    public float getBusVoltage() {
        return busVoltage;
    }

    public void setBusVoltage(float busVoltage) {
        this.busVoltage = busVoltage;
    }

    public String getIsSend() {
        return isSend;
    }

    public void setSend(String send) {
        isSend = send;
    }

    // 主机是否已发给子机
    private String isSend;

    protected DeviceBean(Parcel in) {
        name = in.readString();
        res = in.readString();
        code = in.readString();
        info = in.readString();
        trueNum = in.readString();
        errNum = in.readString();
        currentPeak = in.readString();
        isSend = in.readString();

    }

    public DeviceBean() {
    }

    public static final Creator<DeviceBean> CREATOR = new Creator<DeviceBean>() {
        @Override
        public DeviceBean createFromParcel(Parcel in) {
            return new DeviceBean(in);
        }

        @Override
        public DeviceBean[] newArray(int size) {
            return new DeviceBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(res);
        dest.writeString(code);
        dest.writeString(info);
        dest.writeString(trueNum);
        dest.writeString(errNum);
        dest.writeString(currentPeak);
        dest.writeString(isSend);

    }

    public String getCurrentPeak() {
        return currentPeak;
    }

    public void setCurrentPeak(String currentPeak) {
        this.currentPeak = currentPeak;
    }

    public String getTrueNum() {
        return trueNum;
    }

    public void setTrueNum(String trueNum) {
        this.trueNum = trueNum;
    }

    public String getErrNum() {
        return errNum;
    }

    public void setErrNum(String errNum) {
        this.errNum = errNum;
    }

//    @Override
//    public String toString() {
//        return "DeviceBean{" +
//                "name='" + name + '\'' +
//                ", res='" + res + '\'' +
//                ", code='" + code + '\'' +
//                ", info='" + info + '\'' +
//                ", trueNum='" + trueNum + '\'' +
//                ", errNum='" + errNum + '\'' +
//                ", socket=" + socket +
//                '}';
//    }

    @Override
    public String toString() {
        return "DeviceBean{" +
                "name='" + name + '\'' +
                ", res='" + res + '\'' +
                ", code='" + code + '\'' +
                ", info='" + info + '\'' +
                ", trueNum='" + trueNum + '\'' +
                ", errNum='" + errNum + '\'' +
                ", currentPeak='" + currentPeak + '\'' +
                ", isSend='" + isSend + '\'' +
                ", socket=" + socket +
                '}';
    }
}
