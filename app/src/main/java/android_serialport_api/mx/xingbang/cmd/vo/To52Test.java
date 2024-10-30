package android_serialport_api.mx.xingbang.cmd.vo;

public class To52Test {
	
	private String shellNo;//管壳码 
    private String denaId;//雷管id
    private String facCode;//管厂码
    private String feature;//特征码
    
    private int  delayTime;//延时

    private int isFireTest;//是否桥丝检测
    
    public int getIsFireTest() {
		return isFireTest;
	}

	public void setIsFireTest(int isFireTest) {
		this.isFireTest = isFireTest;
	}

	private byte[] sendCmd;//发送命令
    
	public String getShellNo() {
		return shellNo;
	}

	public void setShellNo(String shellNo) {
		this.shellNo = shellNo;
	}

	public String getDenaId() {
		return denaId;
	}

	public void setDenaId(String denaId) {
		this.denaId = denaId;
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

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	public byte[] getSendCmd() {
		return sendCmd;
	}

	public void setSendCmd(byte[] sendCmd) {
		this.sendCmd = sendCmd;
	}
    

}
