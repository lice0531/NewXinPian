package android_serialport_api.xingbang.models;

public class VoFiringTestError  extends VoDenatorBaseInfo {

	private int error;//错误状态，1发出写入，未响应,

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
	
	
	
}
