package android_serialport_api.mx.xingbang.cmd.vo;

public class To32FiringDenator {
	
	private String shellNo;//管壳码 
	
    private String denaId;//雷管id
   
    private int  delayTime;//延时

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
