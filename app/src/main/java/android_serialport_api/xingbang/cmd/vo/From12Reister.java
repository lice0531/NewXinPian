package android_serialport_api.xingbang.cmd.vo;

public class From12Reister {
	
	private String readStatus;//读取状态
	
    private String denaId;//雷管id
    private String facCode;//管厂码
    private String feature;//特征码    
    private String wire;//桥丝
	private String denaIdSup;//雷管id

	public String getWire() {
		return wire;
	}

	public void setWire(String wire) {
		this.wire = wire;
	}

	public String getDenaId() {
		return denaId;
	}

	public void setDenaId(String denaId) {
		this.denaId = denaId;
	}

	public String getDenaIdSup() {
		return denaIdSup;
	}

	public void setDenaIdSup(String denaIdSup) {
		this.denaIdSup = denaIdSup;
	}

	public String getFacCode() {
		return facCode;
	}

	public void setFacCode(String facCode) {
		this.facCode = facCode;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(String readStatus) {
		this.readStatus = readStatus;
	}


    

}
