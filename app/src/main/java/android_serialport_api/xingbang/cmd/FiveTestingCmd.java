package android_serialport_api.xingbang.cmd;

import android.util.Log;

import android_serialport_api.xingbang.cmd.vo.From52Read;
import android_serialport_api.xingbang.cmd.vo.From52Test;
import android_serialport_api.xingbang.cmd.vo.To52Test;
import android_serialport_api.xingbang.utils.Utils;

public class FiveTestingCmd {
	/***
	 * 5.检测,进入检测模式命令 单通道 ----进入检测模式
	 * @param addr：AA或者0x01 – 0x0A，0x01 – 0x14，0x00为广播地址
	 * @return
	 */
	public static byte[] setToXbCommon_InCheckModel_Init25(String addr){
		/***
		 * 如单通道	发送：C0 AA 50 00 CRC(2字节) C0
				           返回：C0 AA 50 00 CRC(2字节) C0
	                    如多通道	发送：C0 00 50 00 CRC(2字节) C0
		 */
		String command = addr + "50"+"00";
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 处理返回进入检测命令模式的数据处理
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getFromXbCommon_InCheckModel_Init25(String addr ,String from){
		
		if(from==null)return -1;
		String command = addr + "50"+"00";
		String realyCmd1 = DefCommand.getCommadHex(command);
		if(from.indexOf(realyCmd1)>=0)return 0;
		else return -1;
	}
	/***
	 * 2.5写入雷管Id
	 * @param addr
	 * @param data：共8字节，字节1，字节2，字节3，字节4为ID ，字节5为特征号 ，字节6为厂管码 ，字节7，字节8为延期（ms，低字节在前）
	 * @return
	 */
	public static byte[] setToXbCommon_CheckDenator25(String addr,String data){
		/***
		 * 
		 */
		String command = addr + "51"+"09"+data;
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 检验返回检测命令是否正确
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  isCorrectFromXbCommon_CheckDenator25(String addr ,String from){
		
		if(from==null)return -1;
		String command = addr + "51"+"11";
		//String realyCmd1 =DefCommand.getCommadHex(command);
		
		if(from.indexOf(command)>=0)return 0;
		else return -1;
	}
	/***
	 * 返回检测数据解包
	 * @param realyCmd1
	 * @return
	 */
    private static From52Test decodeTestingData(String realyCmd1){
    	

		String dataHex =  realyCmd1.substring(6, 40);//取得返回数据
		From52Test vo = new From52Test();
		String chipBaseStatus = dataHex.substring(0,2);//芯片状态
		vo.setChipBaseStatus(chipBaseStatus);
		
		String chipPlusIa = dataHex.substring(2,6);//芯片电流
		chipPlusIa = Utils.swop2ByteOrder(chipPlusIa);
		byte[] dataBytes = Utils.hexStringToBytes(chipPlusIa);
		int ia = Utils.byte2ToUnsignedShort(dataBytes, 0);
		vo.setChipPlusIa(ia);
		
		String chipRevIa =  dataHex.substring(6,10);//芯片反向电流
		chipRevIa = Utils.swop2ByteOrder(chipRevIa);
		dataBytes = Utils.hexStringToBytes(chipRevIa);
		ia = Utils.byte2ToUnsignedShort(dataBytes, 0);
		vo.setChipRevIa(ia);
		
		String busV = dataHex.substring(10,14);//总线电压
		busV = Utils.swop2ByteOrder(busV);
		dataBytes = Utils.hexStringToBytes(busV);
		ia = Utils.byte2ToUnsignedShort(dataBytes, 0);
		vo.setBusV(ia);
		
		String denaId = dataHex.substring(14,22);//雷管id
		denaId = Utils.swop4ByteOrder(denaId);
		vo.setDenaId(denaId);
		
		String feature = dataHex.substring(22,24);//特征码				
		char c = (char)Integer.parseInt(feature, 16);
		vo.setFeature(""+c);
		
		String facCode =  dataHex.substring(24,26);//管厂码
		vo.setFacCode(""+Integer.parseInt(facCode, 16));
		
		
		
		String delayWriteStatus = dataHex.substring(26,28);//延时写入状态
		vo.setDelayWriteStatus(delayWriteStatus);
		
		String shiptestStatus = dataHex.substring(28,30);//芯片检测状态
		vo.setShiptestStatus(shiptestStatus);
		
		String delayTime = dataHex.substring(30);//延时
		delayTime = Utils.swop2ByteOrder(delayTime);
		dataBytes = Utils.hexStringToBytes(delayTime);
		ia = Utils.byte2ToUnsignedShort(dataBytes, 0);
		vo.setDelayTime(ia);
						
		return vo;
	
    	
    }
	
	/***
	 * 解码检测返回的52命令
	 * @param addr
	 * @param cmd
	 * @return
	 */
	public static From52Test decodeFromReceiveDataTestingCommand25(String addr , byte[] cmd){
		
		String fromCommad =  Utils.bytesToHexFun(cmd);
		String realyCmd1 = DefCommand.decodeCommand(fromCommad);
	
		if("-1".equals(realyCmd1)||"-2".equals(realyCmd1)){
			return null;
		}
		if(isCorrectFromXbCommon_CheckDenator25(addr,realyCmd1)==0){
			if(realyCmd1!=null&&realyCmd1.length()==40){
				return decodeTestingData(realyCmd1);				
			}
		}
		return null;
	}
	/***
	 * 得到发送检测命令数据
	 * @param addr：设备地址
	 * @param denatorShell,管壳码
	 * @return
	 */
	public static To52Test getSendTesting25(String addr, String denatorShell, short delayTime, int isFireTest){
		
		 //得到管壳码
		 String str = Utils.DetonatorShellToSerialNo(denatorShell);
		 //字节重排，得到雷管下发Id
		 String lowThigh = Utils.swop4ByteOrder(str);
		 
		 To52Test vo = new To52Test();
		 vo.setDenaId(lowThigh);
		 
		 byte[] idByte =  Utils.hexStringToBytes(lowThigh);
		 //得到管厂码
		 byte fcode = Utils.getDetonatorShellToFactoryCodeByte(denatorShell);
		 vo.setFacCode(Utils.getDetonatorShellToFactoryCodeStr(denatorShell));
		 
		 //得到特征码
		 byte fea = Utils.getDetonatorShellToFeature(denatorShell);
		 vo.setFeature(Utils.getDetonatorShellToFeatureStr(denatorShell));
		 
		 //
		 byte[] delayBye = Utils.shortToByte((short)delayTime);
		 
		 byte[]  dataBy =  new byte[9];
		 System.arraycopy(idByte, 0, dataBy, 0, idByte.length);
		 dataBy[4] = fea ;
		 dataBy[5] = fcode;
		 dataBy[6] = delayBye[0];
		 dataBy[7] = delayBye[1];
		 if(isFireTest==1){
			 byte b = Byte.parseByte( "01" ); 	
			 dataBy[8] =b;
		 }else{
			 byte b = Byte.parseByte( "00" ); 	
			 dataBy[8] =b;
		 }
		 String data = Utils.bytesToHexFun(dataBy);
		 byte[] sendCmd = setToXbCommon_CheckDenator25(addr,data);
		 vo.setSendCmd(sendCmd);
		 return vo;
	}
	/**
	 * 5.4、检测,发送
	 * @param addr
	 * @param data
	 * @return
	 */
	public static byte[] setToXbCommon_Testing25_4(String addr,String data){
		String command = addr + "53"+"01"+data;
		return DefCommand.getCommadBytes(command);
	}
	
	/***
	 * 解码检测返回的54命令
	 * @param addr
	 * @param cmd
	 * @return
	 */
	public static From52Test decodeFromReceiveDataTestingCmd54(String addr , byte[] cmd){
		
		String fromCommad =  Utils.bytesToHexFun(cmd);
		String realyCmd1 = DefCommand.decodeCommand(fromCommad);
	
		if("-1".equals(realyCmd1)||"-2".equals(realyCmd1)){
			return null;
		}
		if(isCorrectFromXbCommon_Testing25_4(addr,realyCmd1)==0){
			if(realyCmd1!=null&&realyCmd1.length()==40){
				return decodeTestingData(realyCmd1);				
			}
		}
		return null;
	}
	/***
	 * 检验返回检测命令是否正确
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  isCorrectFromXbCommon_Testing25_4(String addr ,String from){
		
		if(from==null)return -1;
		String command = addr + "53"+"11";
		//String realyCmd1 =DefCommand.getCommadHex(command);
		
		if(from.contains(command))return 0;
		else return -1;
	}
	
	/****
	 * 发送读取命令
	 * @param addr
	 * @return
	 */
	public static byte[] setToXbCommon_ReadId_4(String addr){
		
		String command = addr + "55"+"00";
		return DefCommand.getCommadBytes(command);
	}
	
	public static int  isCorrectFromXbCommon_ReadId25(String addr ,String from){
		
		if(from==null)return -1;
		String command = addr + "55"+"06";
		//String realyCmd1 =DefCommand.getCommadHex(command);
		
		if(from.contains(command))return 0;
		else return -1;
	}
	/***
	 * 解码检测返回的5.6命令，自动返回雷管ID号
	 * @param addr
	 * @param cmd
	 * @return
	 */
	public static From52Read decodeFromReceiveReadId(String addr , byte[] cmd){
		
		String fromCommad =  Utils.bytesToHexFun(cmd);
		String realyCmd1 = DefCommand.decodeCommand(fromCommad);
	
		if("-1".equals(realyCmd1)||"-2".equals(realyCmd1)){
			return null;
		}
		if(isCorrectFromXbCommon_ReadId25(addr,realyCmd1)==0){
			if(realyCmd1!=null&&realyCmd1.length()==18){
				String dataHex =  realyCmd1.substring(6, 18);//取得返回数据
				From52Read vo = new From52Read();
				String denaId = dataHex.substring(0,8);//雷管id
				denaId = Utils.swop4ByteOrder(denaId);
				vo.setDenaId(denaId);
				
				String feature = dataHex.substring(8,10);//特征号
				char c = (char)Integer.parseInt(feature, 16);
				vo.setFeature(""+c);
				
			    String facCode = dataHex.substring(10);//特征号			    
				vo.setFacCode(""+Integer.parseInt(facCode, 16));
				
				return vo;
			}
		}
		return null;
	}
	
	/***
	 * 5.7退出检测模式
	 * @param addr
	 * @return
	 */
	public static byte[] setToXbCommon_InCheckModel_Exit54(String addr){
		
		String command = addr + "5A"+"00";
		return DefCommand.getCommadBytes(command);
	}
	/***
	 * 校验处理返回退出检测命令模式的数据处理
	 * @param addr
	 * @param from
	 * @return
	 */
	public static int  getCheckFromXbCommon_InCheckModel_Exit54(String addr ,String from){
		
		String cmd = DefCommand.decodeCommand(from);
		if("-1".equals(cmd)||"-2".equals(cmd)){
			return -1;
		}
		if(cmd==null||cmd.trim().length()<1)return -1;
		
		String command = addr + "5A"+"00";
		if(cmd.contains(command))return 0;
		else return -1;
	}


	public static String decodeCmd5B(String addr , byte[] cmd){
		String fromCommad =  Utils.bytesToHexFun(cmd);
		String realyCmd1 = DefCommand.decodeCommand(fromCommad);
		Log.e("5B命令", "realyCmd1: "+realyCmd1 );
		if("-1".equals(realyCmd1)||"-2".equals(realyCmd1)||realyCmd1.length()<7){
			return null;
		}
		return decode5B(realyCmd1);
	}

	public static String decodeCmd5C(String addr , byte[] cmd){
		String fromCommad =  Utils.bytesToHexFun(cmd);
		String realyCmd1 = DefCommand.decodeCommand(fromCommad);

		if("-1".equals(realyCmd1)||"-2".equals(realyCmd1)){
			return null;
		}
		return decode5C(realyCmd1);
	}

	/***
	 * 解析5B
	 * @param realyCmd1
	 * @return
	 */
	private static String decode5B(String realyCmd1){
		//C0005B04410600FF867FC0
		//005B04410600FF

		String dataHex =  realyCmd1.substring(4, 15);//取得返回数据
		Log.e("解析5B", "dataHex: "+dataHex );
		return "0";
	}
	/***
	 * 返回检测数据解包
	 * @param realyCmd1
	 * @return
	 */
	private static String decode5C(String realyCmd1){
		return "0";
	}


	
}
