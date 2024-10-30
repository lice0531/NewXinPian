package android_serialport_api.mx.xingbang.services.socket;

import android_serialport_api.mx.xingbang.services.socket.SocketService.ClientService;

public class DenatorClient {

	private ClientService client;
	private String equNo;//设备编号
	private String ip;//设备地址
	private int serial;//序号
	private String state;//状态
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getEquNo() {
		return equNo;
	}
	public void setEquNo(String equNo) {
		this.equNo = equNo;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getSerial() {
		return serial;
	}
	public void setSerial(int serial) {
		this.serial = serial;
	}
	public ClientService getClient() {
		return client;
	}
	public void setClient(ClientService client) {
		this.client = client;
	}
	
	
}
