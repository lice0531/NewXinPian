package android_serialport_api.mx.xingbang.models;
/**
 * 雷管起爆信息
 * */
public class VoFireHisMain {

	private String id;
	private String blastdate;//爆炸时间
	private String uploadStatus;//上传状态
	private String longitude;//经度坐标   
	private String latitude;//纬度    
	private String userid;//操作人
	private String firedNo;//起爆器编号
	private String serialNo;//序号
	private String remark;
	private String projectNo;//合同编号
	private String dwdm;//单位代码
	private String xmbh;//项目编号
	private String log;//日志

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBlastdate() {
		return blastdate;
	}
	public void setBlastdate(String blastdate) {
		this.blastdate = blastdate;
	}
	public String getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFiredNo() {
		return firedNo;
	}
	public void setFiredNo(String firedNo) {
		this.firedNo = firedNo;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getProjectNo() {
		return projectNo;
	}
	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}
	public String getDwdm() {
		return dwdm;
	}
	public void setDwdm(String dwdm) {
		this.dwdm = dwdm;
	}
	public String getXmbh() {
		return xmbh;
	}
	public void setXmbh(String xmbh) {
		this.xmbh = xmbh;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	@Override
	public String toString() {
		return "VoFireHisMain{" +
				"id='" + id + '\'' +
				", blastdate='" + blastdate + '\'' +
				", uploadStatus='" + uploadStatus + '\'' +
				", longitude='" + longitude + '\'' +
				", latitude='" + latitude + '\'' +
				", userid='" + userid + '\'' +
				", firedNo='" + firedNo + '\'' +
				", serialNo='" + serialNo + '\'' +
				", remark='" + remark + '\'' +
				", projectNo='" + projectNo + '\'' +
				", dwdm='" + dwdm + '\'' +
				", xmbh='" + xmbh + '\'' +
				", log='" + log + '\'' +
				'}';
	}
}
