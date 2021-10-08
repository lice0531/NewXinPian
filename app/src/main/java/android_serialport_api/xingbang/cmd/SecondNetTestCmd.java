package android_serialport_api.xingbang.cmd;

import android_serialport_api.xingbang.cmd.vo.From22WriteDelay;
import android_serialport_api.xingbang.cmd.vo.To22WriteTest;
import android_serialport_api.xingbang.utils.Utils;

public class SecondNetTestCmd {
	/***
	 * 2.1进入测试模式
	 * @param 
	 * @return
	 */
	public static byte[] setToXbCommon_Testing_Init22_1(String addr){
		
		String command = addr + DefCommand.CMD_2_NETTEST_1+"00";
		return DefCommand.getCommadBytes(command);
	}
	public static byte[] send21(String addr,String qiaosi,String version){

		String command = addr + DefCommand.CMD_2_NETTEST_1+"01"+version+qiaosi;
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 2.1处理返回,获取检测状态
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getFromXbCommon_Testing_Init22_1(String addr ,String from){
		
		if(from==null)return -1;
		String command = addr + DefCommand.CMD_2_NETTEST_1+"00";
		String realyCmd1 = DefCommand.getCommadHex(command);
		if(from.indexOf(realyCmd1)>=0)return 0;
		else return -1;
	}

	/***
	 * 2.2写入延时时间，检测结果看雷管是否正常
	 * @param addr
	 * @param data：第1~4字节--雷管ID号 第5~6字节--延时时间，以毫秒为单位
	 * @return
	 */
	public static byte[] setToXbCommon_WriteDelay22(String addr,String data){
		
		String command = addr + DefCommand.CMD_2_NETTEST_2+"06"+data;
		
		return DefCommand.getCommadBytes(command);
	}
	
	/***
	 * 得到发送写入延时命令数据
	 * @param addr：设备地址
	 * @param denatorShell,管壳码
	 * @return
	 */
	public static To22WriteTest getSendWriteDelay22(String addr, String denatorId, short delayTime){
		
		 //字节重排，得到雷管下发Id
		 String lowThigh = Utils.swop4ByteOrder(denatorId);
		 
		 To22WriteTest vo = new To22WriteTest();
		 vo.setDenaId(lowThigh);
		 
		 byte[] idByte =  Utils.hexStringToBytes(lowThigh);
		
		 //
		 byte[] delayBye = Utils.shortToByte((short)delayTime);
		 
		 byte[]  dataBy =  new byte[6];
		 System.arraycopy(idByte, 0, dataBy, 0, idByte.length);
		 
		 dataBy[4] = delayBye[0];
		 dataBy[5] = delayBye[1];
		 String data = Utils.bytesToHexFun(dataBy);
		 byte[] sendCmd = setToXbCommon_WriteDelay22(addr,data);
		 vo.setSendCmd(sendCmd);
		 return vo;
	}
	
	/***
	 * 检验返回写入命令是否正确
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  isCorrectFromXbCommon_WriteDelay22(String addr ,String from){
		
		if(from==null)return -1;
		String command = addr + DefCommand.CMD_2_NETTEST_2+"04";
		if(from.contains(command))return 0;
		else return -1;
	}
	
	/***
	 * 解码检测返回的22命令
	 * @param addr
	 * @param cmd
	 * @return
	 */
	public static From22WriteDelay decodeFromReceiveDataWriteCommand22(String addr , byte[] cmd){
		
		String fromCommad =  Utils.bytesToHexFun(cmd);
		String realyCmd1 = DefCommand.decodeCommand(fromCommad);
		if("-1".equals(realyCmd1)||"-2".equals(realyCmd1)){
			return null;
		}
		if(isCorrectFromXbCommon_WriteDelay22(addr,realyCmd1)==0){
			if(realyCmd1.length() == 14){

				String dataHex =  realyCmd1.substring(6, 14);//取得返回数据
				From22WriteDelay vo = new From22WriteDelay();
				String writeStatus = dataHex.substring(0,2);//写入状态
				vo.setCommicationStatus(writeStatus);

				String denatorStatus = dataHex.substring(2,4);//雷管状态
				vo.setDenatorStatus(denatorStatus);
				
				String delayTime = dataHex.substring(4);//延时
				delayTime = Utils.swop2ByteOrder(delayTime);
				byte[] dataBytes = Utils.hexStringToBytes(delayTime);
				int dyt = Utils.byte2ToUnsignedShort(dataBytes, 0);
				vo.setDelayTime(dyt);
				return vo;
			}
		}
		return null;
	}
	
	/***
	 * 2.3退出测试模式
	 * @param 
	 * @return
	 */
	public static byte[] setToXbCommon_Testing_Exit22_3(String addr){
		
		String command = addr + DefCommand.CMD_2_NETTEST_3+"00";
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 2.3退出测试模式
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getFromXbCommon_Testing_Exit22_3(String addr ,String from){
		
		if(from==null)return -1;
		String command = addr + DefCommand.CMD_2_NETTEST_3+"00";
		String realyCmd1 = DefCommand.getCommadHex(command);
		if(from.indexOf(realyCmd1)>=0)return 0;
		else return -1;
	}

}
