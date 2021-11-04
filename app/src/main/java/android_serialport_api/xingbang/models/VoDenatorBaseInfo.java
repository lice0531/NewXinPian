package android_serialport_api.xingbang.models;
/**
 * 通气孔基本信息表
 * */
public class VoDenatorBaseInfo  {

	private int blastserial;//序号	
	private String shellBlastNo;//管壳号
	private String denatorId;//芯片码
	private String denatorIdSup;//从芯片码
	private short delay;//延时

	public String getDenatorIdSup() {
		return denatorIdSup;
	}

	public void setDenatorIdSup(String denatorIdSup) {
		this.denatorIdSup = denatorIdSup;
	}

	public int getBlastserial() {
		return blastserial;
	}
	public void setBlastserial(int blastserial) {
		this.blastserial = blastserial;
	}
	public String getShellBlastNo() {
		return shellBlastNo;
	}
	public void setShellBlastNo(String shellBlastNo) {
		this.shellBlastNo = shellBlastNo;
	}
	public short getDelay() {
		return delay;
	}
	public void setDelay(short delay) {
		this.delay = delay;
	}

	public String getDenatorId() {
		return denatorId;
	}

	public void setDenatorId(String denatorId) {
		this.denatorId = denatorId;
	}

	@Override
	public String toString() {
		return "VoDenatorBaseInfo{" +
				"blastserial=" + blastserial +
				", shellBlastNo='" + shellBlastNo + '\'' +
				", delay=" + delay +
				", denatorId=" + denatorId +
				'}';
	}
}
