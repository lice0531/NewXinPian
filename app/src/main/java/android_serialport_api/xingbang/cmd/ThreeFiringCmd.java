package android_serialport_api.xingbang.cmd;

import android.util.Log;

import android_serialport_api.xingbang.cmd.vo.From32DenatorFiring;
import android_serialport_api.xingbang.cmd.vo.To32FiringDenator;
import android_serialport_api.xingbang.utils.Utils;

public class ThreeFiringCmd {
	/***
	 * 3.1起爆,进入起爆模式
	 * @param 
	 * @return
	 */
	public static byte[] setToXbCommon_Firing_Init23_1(String addr){
		
		String command = addr + DefCommand.CMD_3_DETONATE_1+"00";
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 3.1起爆,进入起爆模式(同时检测桥丝)
	 * @param
	 * @return
	 */
	public static byte[] setToXbCommon_Firing_Init23_2(String addr){

		String command = "00" + DefCommand.CMD_3_DETONATE_1+addr;//00300101
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 处理返回,3.1起爆,进入起爆模式
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getFromXbCommon_Firing_Init23_1(String addr ,String from){
		
		if(from==null)return -1;
		String command = addr + DefCommand.CMD_3_DETONATE_1+"00";
		String realyCmd1 = DefCommand.getCommadHex(command);
		if(from.indexOf(realyCmd1)>=0)return 0;
		else return -1;
	}
	/***
	 *3.2、写入延时时间，检测结果看雷管是否正常
	 * @param addr
	 * @param data：共6字节，字节1，字节2，字节3，字节4为ID ，字节5，字节6为延期（ms，低字节在前）
	 * @return
	 */
	public static byte[] send31(String addr, String data){
		//C0 00 31 06 14E0FF000000 D2D4 C0
		String command;
//		Log.e("长度", "data: "+data.length() );
		if(data.length()==18){
			command = addr + DefCommand.CMD_3_DETONATE_2+"09"+data;
		}else {
			command = addr + DefCommand.CMD_3_DETONATE_2+"0E"+data;
		}
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 检验返回检测命令是否正确
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  isCorrectFromXbCommon_CheckDenator23_2(String addr ,String from){
		
		if(from==null)return -1;
		String command = addr + DefCommand.CMD_3_DETONATE_2+"04";
		if(from.contains(command))return 0;
		else return -1;
	}
	/***
	 * 解码写入雷管延时
	 * @param addr
	 * @return
	 */
	public static From32DenatorFiring decodeFromReceiveDataWriteDelay23_2(String addr , byte[] cmd){
		//C00031 04 FF FF F4 01 0059 C0
		String fromCommad =  Utils.bytesToHexFun(cmd);
		String realyCmd1 = DefCommand.decodeCommand(fromCommad);
	
		if("-1".equals(realyCmd1)||"-2".equals(realyCmd1)){
			return null;
		}
		if(isCorrectFromXbCommon_CheckDenator23_2(addr,realyCmd1)==0){
			if(realyCmd1!=null&&realyCmd1.length()==14){
				String dataHex =  realyCmd1.substring(6, 14);//取得返回数据
				
				String commicationStatus = dataHex.substring(0,2);//通信状态
				String denatorStatus = dataHex.substring(2,4);//雷管状态
				
				String delayTime = dataHex.substring(4);//延时
				delayTime = Utils.swop2ByteOrder(delayTime);
				byte[] dataBytes = Utils.hexStringToBytes(delayTime);
				int ia = Utils.byte2ToUnsignedShort(dataBytes, 0);
				From32DenatorFiring vo = new From32DenatorFiring()	;
				vo.setCommicationStatus(commicationStatus);
				vo.setDenatorStatus(denatorStatus);
				vo.setDelayTime(ia);
				
				return vo;
			}
		}
		return null;
	}
	/***
	 * 得到发送检测雷管 命令数据
	 * @param addr：设备地址
	 * @param denatorShell,管壳码
	 * @return
	 */
	public static To32FiringDenator getSendFiring23_2(String addr, String denatorShell, short delayTime){
		
		 //得到管壳码
		 String str = Utils.DetonatorShellToSerialNo(denatorShell);
		 //字节重排，得到雷管下发Id
		 String lowThigh = Utils.swop4ByteOrder(str);
		 
		 To32FiringDenator vo = new To32FiringDenator();
		 
		 vo.setDenaId(lowThigh);
		 byte[] idByte =  Utils.hexStringToBytes(lowThigh);
		 
		 //
		 byte[] delayBye = Utils.shortToByte((short)delayTime);
		 
		 byte[]  dataBy =  new byte[6];
		 
		 System.arraycopy(idByte, 0, dataBy, 0, idByte.length);
		
		 dataBy[4] = delayBye[0];
		 dataBy[5] = delayBye[1];
		 String data = Utils.bytesToHexFun(dataBy);
		 byte[] sendCmd = send31(addr,data);
		 vo.setSendCmd(sendCmd);
		 return vo;
	}
	
	/***
	 * 3.3 充电（雷管充电命令 等待6S（500米线，200发雷管），5.5V充电）
	 * @param 
	 * @return
	 */
	public static byte[] setToXbCommon_FiringExchange_5523_3(String addr){
		
		String command = addr + DefCommand.CMD_3_DETONATE_3+"00";
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 3.3 充电（雷管充电命令 等待6S（500米线，200发雷管），5.5V充电）
	 * @param
	 * @return
	 */
	public static byte[] setToXbCommon_FiringExchange(String addr){

		String command = addr + DefCommand.CMD_3_DETONATE_9+"00";
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 校验处理返回  3.3 充电（雷管充电命令 等待6S（500米线，200发雷管），5.5V充电
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getCheckFromXbCommon_FiringExchange_5523_3(String addr ,String from){
		
		String cmd = DefCommand.decodeCommand(from);
		if("-1".equals(cmd)||"-2".equals(cmd)){
			return -1;
		}
		if(cmd==null||cmd.trim().length()<1)return -1;
		
		String command = addr + DefCommand.CMD_3_DETONATE_3+"00";
		if(cmd.indexOf(command)>=0)return 0;
		else return -1;
	}
	/***
	 * 发送，高压输出（继电器切换，等待12S（500米线，200发雷管）16V充电）
	 * @param addr
	 * @return
	 */
	public static byte[] setToXbCommon_FiringExchange_5523_4(String addr){
		
		String command = addr + DefCommand.CMD_3_DETONATE_4+"00";
		return DefCommand.getCommadBytes(command);
	}
	
	/***
	 * 处理返回 高压输出（继电器切换，等待12S（500米线，200发雷管）16V充电）
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getCheckFromXbCommon_FiringExchange_5523_4(String addr ,String from){
		
		String cmd = DefCommand.decodeCommand(from);
		if("-1".equals(cmd)||"-2".equals(cmd)){
			return -1;
		}
		if(cmd==null||cmd.trim().length()<1)return -1;
		
		String command = addr + DefCommand.CMD_3_DETONATE_4+"00";
		if(cmd.indexOf(command)>=0)return 0;
		else return -1;
	}
	
	/***
	 *起爆
	 * @param addr
	 * @return
	 */
	public static byte[] setToXbCommon_FiringExchange_5523_5(String addr){
		
		String command = addr + DefCommand.CMD_3_DETONATE_5+"00";
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 处理 起爆返回
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getCheckFromXbCommon_FiringExchange_5523_5(String addr ,String from){
		
		String cmd = DefCommand.decodeCommand(from);
		if("-1".equals(cmd)||"-2".equals(cmd)){
			return -1;
		}
		if(cmd==null||cmd.trim().length()<1)return -1;
		
		String command = addr + DefCommand.CMD_3_DETONATE_5+"00";
		if(cmd.indexOf(command)>=0)return 0;
		else return -1;
	}
	
	/***
	 *退出起爆
	 * @param addr
	 * @return
	 */
	public static byte[] setToXbCommon_FiringExchange_5523_6(String addr){
		
		String command = addr + DefCommand.CMD_3_DETONATE_6+"00";
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 处理 退出起爆
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getCheckFromXbCommon_FiringExchange_5523_6(String addr ,String from){
		
		String cmd = DefCommand.decodeCommand(from);
		if("-1".equals(cmd)||"-2".equals(cmd)){
			return -1;
		}
		if(cmd==null||cmd.trim().length()<1)return -1;
		
		String command = addr + DefCommand.CMD_3_DETONATE_6+"00";
		if(cmd.indexOf(command)>=0)return 0;
		else return -1;
	}
	
	/***
	 *在网读ID检测
	 * @param addr
	 * @return
	 */
	public static byte[] setToXbCommon_FiringExchange_5523_7(String addr){
		
		String command = addr + DefCommand.CMD_3_DETONATE_7+"00";//36
		return DefCommand.getCommadBytes(command);
	}

	/***
	 * 发送切换模式指令
	 * @param addr
	 * @return
	 */
	public static byte[] setToXbCommon_Translate_83(String addr){
		String command = addr + DefCommand.CMD_5_TRANSLATE_83 + "00";//83
		return DefCommand.getCommadBytes(command);
	}

	/***
	 * 处理 在网读ID检测
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getCheckFromXbCommon_FiringExchange_5523_7(String addr ,String from){
		
		String cmd = DefCommand.decodeCommand(from);
		if("-1".equals(cmd)||"-2".equals(cmd)){
			return -1;
		}
		if(cmd==null||cmd.trim().length()<1)return -1;
		
		String command = addr + DefCommand.CMD_3_DETONATE_7+"07";
		if(cmd.indexOf(command)>=0)return 0;
		else return -1;
	}
	/***
	 * 处理在网读ID检测返回值
	 * @param addr
	 * @param from
	 * @return
	 */
	public static String  getCheckFromXbCommon_FiringExchange_5523_7_reval(String addr ,String from){
		
		int iscorrent =getCheckFromXbCommon_FiringExchange_5523_7(addr,from);
		if(iscorrent==0){
			String cmd = DefCommand.decodeCommand(from);
			return cmd.substring(6,8);
		}else{
			return null;
		}
		
	}
	/***
	 *异常终止起爆
	 * @param addr
	 * @return
	 */
	public static byte[] setToXbCommon_FiringExchange_5523_8(String addr){
		
		String command = addr + DefCommand.CMD_3_DETONATE_8+"00";
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 处理异常终止起爆
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getCheckFromXbCommon_FiringExchange_5523_8(String addr ,String from){
		
		String cmd = DefCommand.decodeCommand(from);
		if("-1".equals(cmd)||"-2".equals(cmd)){
			return -1;
		}
		if(cmd==null||cmd.trim().length()<1)return -1;
		
		String command = addr + DefCommand.CMD_3_DETONATE_8+"00";
		if(cmd.indexOf(command)>=0)return 0;
		else return -1;
	}


	/***
	 *在网读ID检测
	 * @param addr
	 * @return
	 */
	public static byte[] send_36(String addr,String yscs){
		String command = addr + DefCommand.CMD_3_DETONATE_7+"02"+yscs;//36
		return DefCommand.getCommadBytes(command);
	}

	/***
	 * 处理在网读ID检测返回值
	 * @param addr
	 * @param from
	 * @return
	 */
	public static String jiexi_36(String addr , String from){

		int iscorrent =getCheckFromXbCommon_FiringExchange_5523_7(addr,from);//判断命令是否完整
		if(iscorrent==0){
			String cmd = DefCommand.decodeCommand(from);
			//jiexi_36--cmd: 003607FF000000000000
			return cmd.substring(6,8);
		}else{
			return null;
		}

	}
}
