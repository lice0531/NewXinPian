package android_serialport_api.xingbang.cmd.vo;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;

public class From38ChongDian {
	
	private String denaId;//雷管id
	private String denatorStatus;//雷管状态 
	private String commicationStatus;//通信状态 ,00--（充电失败）	FF--充电成功
	private String shellNo;//管壳码 
    private int  delayTime;//延时

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

	public String getDenatorStatus() {
		return denatorStatus;
	}

	public void setDenatorStatus(String denatorStatus) {
		this.denatorStatus = denatorStatus;
	}

	public String getCommicationStatus() {
		return commicationStatus;
	}

	public void setCommicationStatus(String commicationStatus) {
		this.commicationStatus = commicationStatus;
	}

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}
	/***
	 * 得到通信状态(主芯片)
	 * @return
	 */
	public String getCommicationStatusName(){
		String name="";
		if("00".equals(this.getCommicationStatus())){
			name=Application.getContext().getString(R.string.text_chongdian_state0);//"充电失败"
		} else {
			name=Application.getContext().getString(R.string.text_chongdian_stateFF);//充电成功
		}
		return name;
	}

	@Override
	public String toString() {
		return "雷管状态{" +
				"  通信状态1='" + getCommicationStatusName() + '\'' +
				", 管壳码='" + shellNo + '\'' +
				", 芯片码='" + denaId + '\'' +
				", 延时=" + delayTime +
				'}';
	}
}