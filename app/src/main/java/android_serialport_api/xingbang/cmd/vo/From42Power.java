package android_serialport_api.xingbang.cmd.vo;

public class From42Power {
	
	private String powerStatus;//电源状态
	private float denatorIa;//当前接入雷管电流
	private float busVoltage;//总线电压
	private float busCurrentIa;//总线电流
	private float firingVoltage;//起爆电压
	
	public String getPowerStatusName(){
		//00—正常
		//01—5.5V电源异常
		//02—12V电源异常
		//04—总线短路

		if("00".equals(getPowerStatus())){
			return "";
		}
		if("01".equals(getPowerStatus())){
			return "5.5V电源异常";
		}
		if("02".equals(getPowerStatus())){
			return "12V电源异常";
		}
		if("03".equals(getPowerStatus())){
			return "5.5V，12V电源异常";
		}
		if("04".equals(getPowerStatus())){
			return "总线短路";
		}
		return "未知错误";
	}
	public String getPowerStatus() {
		return powerStatus;
	}
	public void setPowerStatus(String powerStatus) {
		this.powerStatus = powerStatus;
	}
	public float getDenatorIa() {
		return denatorIa;
	}
	public void setDenatorIa(float denatorIa) {
		this.denatorIa = denatorIa;
	}
	public float getBusVoltage() {
		return busVoltage;
	}
	public void setBusVoltage(float busVoltage) {
		this.busVoltage = busVoltage;
	}
	public float getBusCurrentIa() {
		return busCurrentIa;
	}
	public void setBusCurrentIa(float busCurrentIa) {
		this.busCurrentIa = busCurrentIa;
	}
	public float getFiringVoltage() {
		return firingVoltage;
	}
	public void setFiringVoltage(float firingVoltage) {
		this.firingVoltage = firingVoltage;
	}

	@Override
	public String toString() {
		return "From42Power{" +
				"powerStatus='" + powerStatus + '\'' +
				", denatorIa=" + denatorIa +
				", busVoltage=" + busVoltage +
				", busCurrentIa=" + busCurrentIa +
				", firingVoltage=" + firingVoltage +
				'}';
	}
}
