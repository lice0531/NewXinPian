package android_serialport_api.xingbang.cmd;

import android.util.Log;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.CRC16;
import android_serialport_api.xingbang.utils.Utils;

public class DefCommand {
	
	public static String CMD_1_REISTER_1="10";//进入自动注册模式
	public static String CMD_1_REISTER_2="11";//提示有雷管接入
	public static String CMD_1_REISTER_3="12";//自动返回雷管ID号
	public static String CMD_1_REISTER_4="13";//退出自动注册模式
	public static String CMD_1_REISTER_5="14";//核心板自检

	public static String CMD_2_NETTEST_1="20";//进入测试模式
	public static String CMD_2_NETTEST_2="21";//写入延时时间，检测结果看雷管是否正常
	public static String CMD_2_NETTEST_3="22";//退出测试模式
	
	public static String CMD_3_DETONATE_1="30";//进入起爆模式
	public static String CMD_3_DETONATE_2="31";//写入延时时间，检测结果看雷管是否正常
	public static String CMD_3_DETONATE_3="32";//充电（雷管充电命令 等待6S（500米线，200发雷管），5.5V充电）
	public static String CMD_3_DETONATE_4="33";//高压输出（继电器切换，等待12S（500米线，200发雷管）16V充电）
	public static String CMD_3_DETONATE_5="34";//起爆
	public static String CMD_3_DETONATE_6="35";//退出起爆模式
	public static String CMD_3_DETONATE_7="36";//在网读ID检测
	public static String CMD_3_DETONATE_8="37";//异常终止起爆
	public static String CMD_3_DETONATE_9="38";//充电检测
	public static String CMD_3_DETONATE_10="39";//复检-充电检测

	public static String CMD_4_XBSTATUS_1="40";//获取电源状态指令
	public static String CMD_4_XBSTATUS_2="41";//开启总线电源指令
	public static String CMD_4_XBSTATUS_3="42";//总线翻转指令(检测设备)
	public static String CMD_4_XBSTATUS_4="43";//获取软件版本
	public static String CMD_4_XBSTATUS_5="44";//获取硬件版本
	public static String CMD_4_XBSTATUS_6="45";//设置单片机版本
	public static String CMD_4_XBSTATUS_7="46";//设置模块芯片版本

	public static String CMD_5_TEST_1="50";//进入检测模式
	public static String CMD_5_TEST_2="51";//写入ID
	public static String CMD_5_TEST_3="52";//写入ID查询（多通道）
	public static String CMD_5_TEST_4="53";//检测
	
	public static String CMD_5_TEST_5="54";//检测查询（多通道）
	public static String CMD_5_TEST_6="55";//读ID
	
	public static String CMD_5_TEST_7="5A";//退出检测模式

	public static String CMD_5_TEST_8="5B";//设置低压
	public static String CMD_5_TEST_9="5C";//设置高压
	public static String CMD_5_TRANSLATE_83="83";//切换模式

	
	
	/***
	 * 得到发出命令字节
	 * @param baseBaseCmd
	 * @return
	 */
	public static byte[] getCommadBytes(String baseBaseCmd){
		byte[] mBuffer;
		String realyCmd1 =getCommadHex(baseBaseCmd);
		mBuffer = CRC16.hexStringToByte(realyCmd1);
		return mBuffer;
	}
	
	/***
	 * 得到字符串CRC，高字节在前
	 * @param baseBaseCmd
	 * @return
	 */
	public static String getCRCCode(String baseBaseCmd){
		
		byte[] cy = CRC16.hexStringToByte(baseBaseCmd);
		byte[] crcb =  CRC16.GetCRC(cy);
		String crs16 = CRC16.bytesToHexString(crcb);
		return crs16;
	}
	/***
	 * 得到CRC 低字节在前
	 * @param baseBaseCmd
	 * @return
	 */
	public static String getLowByteBeforeCRCCode(String baseBaseCmd){
		String crchtol = getCRCCode(baseBaseCmd);
//		Log.e("crc校验--原码",baseBaseCmd);
		return crchtol.substring(2)+crchtol.substring(0, 2);
	}
	/***
	 * 得到命令的Hex(16进制表达)
	 * @param baseBaseCmd
	 * @return
	 */
	public static String getCommadHex(String baseBaseCmd){		
		String crs16 = 	getCRCCode(baseBaseCmd);
		String realyCmd1 ="C0"+ baseBaseCmd + crs16.substring(2)+crs16.substring(0,2)+"C0";
		return realyCmd1;
	}
	/***
	 * 得到 返回命令 
	 * @param cmdInfo
	 * @return
	 */
	public static String getCmd(String cmdInfo){
		Log.e("返回命令",cmdInfo);
		Utils.writeLog("<-:"+cmdInfo);
		AppLogUtils.writeAppXBLog("<-:"+cmdInfo);
		if(cmdInfo.length()>4)return cmdInfo.substring(4,6);
		return null;
	}
	public static String getCmd2(String cmdInfo){
		Log.e("返回命令",cmdInfo);

		if(cmdInfo.length()>4)return cmdInfo.substring(4,6);
		return null;
	}
	/***
	 * 返回返回命令解码
	 * @command
	 * @return
	 */
	public synchronized  static String decodeCommand(String command){
		String cmd = command.toUpperCase();//将字符串转化成大写
		//命令不正确
		if(cmd.trim().length() < 6)return "-1";
		int firstpos = cmd.indexOf("C0");
		int endpos;
		String subStr;
		if(firstpos>=0){
			int twoPos = cmd.lastIndexOf("C0");
			if(twoPos==firstpos){//说明起始位存在两个C0C0
				firstpos +=2;
				subStr = cmd.substring(firstpos);
				endpos = subStr.indexOf("C0");
				if(endpos>1){
					subStr= subStr.substring(0,endpos);
				}else{
					return "-1";
				}
			}else{
				if(twoPos<0){//说明不存在两个C0C0
					subStr = cmd.substring(firstpos+2);
					endpos = subStr.indexOf("C0");
					if(endpos>1){
						subStr= subStr.substring(0,endpos);
					}
					else return "-1";
				}else{
					endpos = twoPos;
					subStr = cmd.substring(firstpos+2,endpos);
				}
			}
		}else{
			return "-1";
		}
		
		if(subStr.length() > 4){
			String ocrc = subStr.substring(subStr.length()-4);
			String inInfo = subStr.substring(0, subStr.length()-4);
			String dcrc = getLowByteBeforeCRCCode(inInfo);
			if(!dcrc.equals(ocrc))return "-2";
			else{
				return inInfo;
			}
		}else{
			return "-1";
		}
	}
	
	 public static void main(String args[]) { 
		String cmd = "";
		 decodeCommand(cmd);

	    } 
}
