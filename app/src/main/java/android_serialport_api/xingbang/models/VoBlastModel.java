package android_serialport_api.xingbang.models;
/**
 * 通气孔模型
 * */
public class VoBlastModel extends VoDenatorBaseInfo {

	private String sithole;//孔号
	private String status;//状态
	private String errorInfo;//错误
	private String authorization;//授权期限
	private String remark;//备注
	private String regdate;//注册时间
	private String statusCode;
	private String statusName;
	private String errorName;
	private String errorCode;
	private String wire;//桥丝
	private String zhu_yscs;//主芯片延时参数
	private String cong_yscs;//从芯片延时参数

	public String getZhu_yscs() {
		return zhu_yscs;
	}

	public void setZhu_yscs(String zhu_yscs) {
		this.zhu_yscs = zhu_yscs;
	}

	public String getCong_yscs() {
		return cong_yscs;
	}

	public void setCong_yscs(String cong_yscs) {
		this.cong_yscs = cong_yscs;
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
	public String getSithole() {
		return sithole;
	}
	public void setSithole(String sithole) {
		this.sithole = sithole;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
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

	@Override
	public String toString() {
		return "VoBlastModel{" +
				"sithole=" + sithole +
				", status='" + status + '\'' +
				", errorInfo='" + errorInfo + '\'' +
				", authorization='" + authorization + '\'' +
				", remark='" + remark + '\'' +
				", regdate='" + regdate + '\'' +
				", statusCode='" + statusCode + '\'' +
				", statusName='" + statusName + '\'' +
				", errorName='" + errorName + '\'' +
				", errorCode='" + errorCode + '\'' +
				", wire='" + wire + '\'' +
				'}';
	}
}
