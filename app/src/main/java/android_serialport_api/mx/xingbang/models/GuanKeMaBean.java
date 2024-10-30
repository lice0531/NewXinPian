package android_serialport_api.mx.xingbang.models;

/**
 * Created by xingbang on 2019/11/21.
 */

public class GuanKeMaBean {
    private int id;
    private int blastserial;//序号
    private int sithole;//孔号
    private String shellBlastNo;//管壳码
    private String denatorId;//芯片码
    private int delay;//延时
    private String statusCode;//读取状态
    private String statusName;//状态名称
    private String errorName;//错误名称
    private String errorCode;//错误代码
    private String authorization;//授权
    private String remark;//评论
    private String regdate;//注册日期
    private String wire;//桥丝状态
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBlastserial() {
        return blastserial;
    }

    public void setBlastserial(int blastserial) {
        this.blastserial = blastserial;
    }

    public int getSithole() {
        return sithole;
    }

    public void setSithole(int sithole) {
        this.sithole = sithole;
    }

    public String getShellBlastNo() {
        return shellBlastNo;
    }

    public void setShellBlastNo(String shellBlastNo) {
        this.shellBlastNo = shellBlastNo;
    }

    public String getDenatorId() {
        return denatorId;
    }

    public void setDenatorId(String denatorId) {
        this.denatorId = denatorId;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getWire() {
        return wire;
    }

    public void setWire(String wire) {
        this.wire = wire;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GuanKeMaBean{" +
                "id=" + id +
                ", blastserial=" + blastserial +
                ", sithole=" + sithole +
                ", shellBlastNo='" + shellBlastNo + '\'' +
                ", denatorId='" + denatorId + '\'' +
                ", delay=" + delay +
                ", statusCode='" + statusCode + '\'' +
                ", statusName='" + statusName + '\'' +
                ", errorName='" + errorName + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", authorization='" + authorization + '\'' +
                ", remark='" + remark + '\'' +
                ", regdate='" + regdate + '\'' +
                ", wire='" + wire + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
