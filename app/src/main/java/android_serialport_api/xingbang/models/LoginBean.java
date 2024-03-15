package android_serialport_api.xingbang.models;

import java.io.Serializable;

public class LoginBean implements Serializable {

    private String uPhone;
    private String uPwd;
    private String uCid;
    private String uCName;
    private String uFName;
    private String status;
    private String msg;

    public String getUPhone() {
        return uPhone;
    }

    public void setUPhone(String uPhone) {
        this.uPhone = uPhone;
    }

    public String getUPwd() {
        return uPwd;
    }

    public void setUPwd(String uPwd) {
        this.uPwd = uPwd;
    }

    public String getUCid() {
        return uCid;
    }

    public void setUCid(String uCid) {
        this.uCid = uCid;
    }

    public String getUCName() {
        return uCName;
    }

    public void setUCName(String uCName) {
        this.uCName = uCName;
    }

    public String getUFName() {
        return uFName;
    }

    public void setUFName(String uFName) {
        this.uFName = uFName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
