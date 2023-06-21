package android_serialport_api.xingbang.cmd.vo;

import android.content.Context;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;

public class From32DenatorFiring {
	
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
			name=Application.getContext().getString(R.string.text_communication_state1);//"雷管通信失败"
			return name;
		}
		if("01".equals(this.getCommicationStatus())){
			name=Application.getContext().getString(R.string.text_communication_state2);//"延时写入不一致";
			return name;
		}
		if("02".equals(this.getCommicationStatus())){
			return Application.getContext().getString(R.string.text_communication_state7);// "桥丝异常";
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
		if("AF".equals(this.getCommicationStatus())){
			name=Application.getContext().getString(R.string.text_communication_state3);
			return name;
		}
		if("FF".equals(this.getCommicationStatus())){
			name=Application.getContext().getString(R.string.text_communication_state4);//通讯成功
			return name;
		}if("F1".equals(this.getCommicationStatus())){
			name=Application.getContext().getString(R.string.text_communication_state4);//通讯成功
			return name;
		}if("F2".equals(this.getCommicationStatus())){
			name=Application.getContext().getString(R.string.text_communication_state4);//通讯成功
			return name;
		}
		return Application.getContext().getString(R.string.text_communication_state5);//"未知";
	}
	/***
	 * 得到通信状态(从芯片)
	 * @return
	 */
	public String getCommicationStatusCong(){
		String name="";
		if("00".equals(this.getDenatorStatus())){
			name=Application.getContext().getString(R.string.text_communication_state1);//"雷管通信失败"
			return name;
		}
		if("01".equals(this.getDenatorStatus())){
			name=Application.getContext().getString(R.string.text_communication_state2);//"延时写入不一致";
			return name;
		}
		if("02".equals(this.getDenatorStatus())){
			return Application.getContext().getString(R.string.text_communication_state7);// "桥丝异常";
		}
		if("03".equals(this.getDenatorStatus())){
			return "充电不正常";
		}
		if("04".equals(this.getDenatorStatus())){
			return "延时写入失败";
		}
		if("05".equals(this.getDenatorStatus())){
			return "延时同步不正确";
		}
		if("06".equals(this.getDenatorStatus())){
			return "其他错误";
		}
		if("AF".equals(this.getDenatorStatus())){
			name=Application.getContext().getString(R.string.text_communication_state3);
			return name;
		}
		if("FF".equals(this.getDenatorStatus())){
			name=Application.getContext().getString(R.string.text_communication_state4);//通讯成功
			return name;
		}
		return Application.getContext().getString(R.string.text_communication_state5);//"未知";
	}
	/***
	 * 得到雷管状态
	 * @return
	 */
	public String getDenatorStatusName(){
		String name="";
		if("00".equals(this.getCommicationStatus())){
			name="雷管正常";
			return name;
		}
		return Application.getContext().getString(R.string.text_communication_state5);
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