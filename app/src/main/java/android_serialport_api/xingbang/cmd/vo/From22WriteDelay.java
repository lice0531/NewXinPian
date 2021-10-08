package android_serialport_api.xingbang.cmd.vo;

import android.content.Context;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;


public class From22WriteDelay {
	
	private String denaId;//雷管id
	private String denatorStatus;//雷管状态 
	private String commicationStatus;//通信状态 ,00--（与雷管通信失败）01--延期写入不一致	FF--通信成功。AF:未返回命令
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
	
	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	public String getCommicationStatus() {
		return commicationStatus;
	}

	public void setCommicationStatus(String commicationStatus) {
		this.commicationStatus = commicationStatus;
	}
	 /***
     * 得到通信状态
     * @return
     */
	public String getCommicationStatusName(){
		String name="";
		if("00".equals(this.getCommicationStatus())){
			name= Application.getContext().getString(R.string.text_communication_state1);
			return name;
		}
		if("01".equals(this.getCommicationStatus())){
			name=Application.getContext().getString(R.string.text_communication_state2);
			return name;
		}
		if("02".equals(this.getCommicationStatus())){
			return "桥丝异常";
		}
		if("03".equals(this.getCommicationStatus())){
			return "充电不正常";
		}
		if("04".equals(this.getCommicationStatus())){
			return "延时写入失败";
		}
		if("05".equals(this.getCommicationStatus())){
			return "延时同步不正确";
		}
		if("06".equals(this.getCommicationStatus())){
			return "其他错误";
		}
//		if("AF".equals(this.getCommicationStatus())){
//			name=context.getString(R.string.text_communication_state3);
//			return name;
//		}
		if("FF".equals(this.getCommicationStatus())){
			name=Application.getContext().getString(R.string.text_communication_state4);
			return name;
		}
		return Application.getContext().getString(R.string.text_communication_state5);
	}
    /***
     * 得到雷管状态
     * @return
     */
	public String getDenatorStatusName(){
		if("00".equals(this.getCommicationStatus())){
			return "雷管正常";
		}
		return "未知";
	}

	@Override
	public String toString() {
		return "测试数据{" +
				", 通信状态='" + this.getCommicationStatusName() + '\'' +
				", 管壳码='" + shellNo + '\'' +
				", 芯片码='" + denaId + '\'' +
				", 延时=" + delayTime +
				'}';
	}
}
