package android_serialport_api.xingbang.cmd;

import android.util.Log;

import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.utils.Utils;

public class FourStatusCmd {
    /***
     * 4.1获取电源状态指令
     * @param
     * @return
     */
    public static byte[] setToXbCommon_Power_Status24_1(String addr, String data) {

        String command = addr + DefCommand.CMD_4_XBSTATUS_1 + "01" + data;//4001
        return DefCommand.getCommadBytes(command);
    }

    /***
     * 4.1处理返回,获取电源状态指令
     * @param addr
     * @param from
     * @return
     */
    public static int getFromXbCommon_Power_Status24_1(String addr, String from) {

        if (from == null) return -1;
        String command = addr + DefCommand.CMD_4_XBSTATUS_1 + "08";
        //String realyCmd1 =DefCommand.getCommadHex(command);
        if (from.indexOf(command) >= 0) return 0;
        else return -1;
    }

    /***
     * 4.1 解码获取电源状态指令
     * @param addr
     * @param cmd
     * @return
     */
    public static From42Power decodeFromReceiveDataPower24_1(String addr, byte[] cmd) {
        //C0 00 40 08 00 00 0900 0100 0900 240B C0
        //C0 00 40 08 00 00 BB00 0000 BB00 1375 C0
        String fromCommad = Utils.bytesToHexFun(cmd);
        String realyCmd1 = DefCommand.decodeCommand(fromCommad);

        if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
            return null;
        }
        if (getFromXbCommon_Power_Status24_1(addr, realyCmd1) == 0) {
            if (realyCmd1 != null && realyCmd1.length() == 22) {

                From42Power vo = new From42Power();
                String dataHex = realyCmd1.substring(6);//取得返回数据
                String commicationStatus = dataHex.substring(0, 2);//电源状态
                vo.setPowerStatus(commicationStatus);

                String tempData = dataHex.substring(2, 4);//雷管状态
                int ia = Integer.parseInt(tempData, 16);
                vo.setDenatorIa(ia);

                tempData = dataHex.substring(4, 8);//总线电压
                String strLow = tempData.substring(0, 2);
                String strHigh = tempData.substring(2);
                int volthigh = Integer.parseInt(strHigh, 16) * 256;
                int voltLowInt = Integer.parseInt(strLow, 16);
                //可调电压版本,系数为0.011,不可调为0.006
//				double voltTotal =(volthigh+voltLowInt)/4.095*3.0 * 0.006;
                double voltTotal = (volthigh + voltLowInt) * 3.0 * 11 / 4.096 / 1000;//新芯片
//				double voltTotal =(volthigh+voltLowInt)/4.095*3.0 * 0.011;//可调电压
                float busVoltage = (float) voltTotal;
                busVoltage = Utils.getFloatToFormat(busVoltage, 2, 4);
                vo.setBusVoltage(busVoltage);

                tempData = dataHex.substring(8, 12);//总线电流
                String strLow2 = tempData.substring(0, 2);
                String strHigh2 = tempData.substring(2);
                if (strLow2.isEmpty()) {
                    strLow2 = "00";
                }
                if (strHigh2.isEmpty()) {
                    strHigh2 = "00";
                }
                int ichigh = Integer.parseInt(strHigh2, 16) * 256;
                int icLowInt = Integer.parseInt(strLow2, 16);
//				double icTotal =(ichigh+ icLowInt)/4.096*3.0 * 0.0098;//普通版本
                double icTotal = (ichigh + icLowInt) * 3.0 / (4.096 * 0.35);//新芯片
                float busCurrent = (float) (icTotal*1.8);//
                if(busCurrent<0){
                    busCurrent=0;
                }
                busCurrent = Utils.getFloatToFormat(busCurrent, 2, 4);
                vo.setBusCurrentIa(busCurrent);//设置总线电流


                tempData = dataHex.substring(12);//起爆电压
                strLow = tempData.substring(0, 2);
                strHigh = tempData.substring(2);
                volthigh = Integer.parseInt(strHigh, 16) * 256;
                voltLowInt = Integer.parseInt(strLow, 16);
                voltTotal = (volthigh + voltLowInt) / 4.095 * 3.0 * 0.011;
                float busVolt = (float) voltTotal;
                busVolt = Utils.getFloatToFormat(busVolt, 1, 4);
                vo.setFiringVoltage(busVolt);//设置起爆电压
                return vo;
            }
        }
        return null;
    }

    /***
     * 4.2开启总线电源指令
     * @param
     * @return
     */
    public static byte[] setToXbCommon_OpenPower_42_2(String addr) {

        String command = addr + DefCommand.CMD_4_XBSTATUS_2 + "00";
        return DefCommand.getCommadBytes(command);
    }

    /***
     * 校验处理返回  4.2开启总线电源指令
     * @param addr
     * @param from
     * @return
     */
    public static int getCheckFromXbCommon_OpenPower_42_2(String addr, String from) {

        String cmd = DefCommand.decodeCommand(from);
        if ("-1".equals(cmd) || "-2".equals(cmd)) {
            return -1;
        }
        if (cmd == null || cmd.trim().length() < 1) return -1;

        String command = addr + DefCommand.CMD_4_XBSTATUS_2 + "00";
        if (cmd.indexOf(command) >= 0) return 0;
        else return -1;
    }

    /***
     * 4.3、总线翻转指令(检测设备)
     * @param addr
     * @return
     */
    public static byte[] setToXbCommon_BusReversal_42_3(String addr) {

        String command = addr + DefCommand.CMD_4_XBSTATUS_3 + "00";
        return DefCommand.getCommadBytes(command);
    }

    /***
     * 处理返回 4.3、总线翻转指令(检测设备)
     * @param addr
     * @param from
     * @return
     */
    public static int getCheckFromXbCommon_BusReversal_42_3(String addr, String from) {

        String cmd = DefCommand.decodeCommand(from);
        if ("-1".equals(cmd) || "-2".equals(cmd)) {
            return -1;
        }
        if (cmd == null || cmd.trim().length() < 1) return -1;

        String command = addr + DefCommand.CMD_4_XBSTATUS_3 + "00";
        if (cmd.indexOf(command) >= 0) return 0;
        else return -1;
    }

    /***
     *4.4、读软版本号
     * @param addr
     * @return
     */
    public static byte[] setToXbCommon_ReadVer_42_4(String addr) {

        String command = addr + DefCommand.CMD_4_XBSTATUS_4 + "00";
        return DefCommand.getCommadBytes(command);
    }

    /***
     * 4.5复位芯片
     * @param
     * @return
     */
    public static byte[] setToXbCommon_Power_Status_42_5(String addr) {

        String command = addr + DefCommand.CMD_4_XBSTATUS_5 + "00";
        return DefCommand.getCommadBytes(command);
    }

    /**
     * 发送设置低压指令
     */
    public static byte[] setToXbCommon_SetLowVoltage(String addr, String data) {
        String command = addr + DefCommand.CMD_5_TEST_8 + "02" + data;//5B02
        return DefCommand.getCommadBytes(command);
    }

    /**
     * 发送设置高压指令
     */
    public static byte[] setToXbCommon_SetHighVoltage(String addr, String data) {
        String command = addr + DefCommand.CMD_5_TEST_9 + "02" + data;//5C02
        return DefCommand.getCommadBytes(command);
    }

    /**
     * 获得软件版本
     */
    public static byte[] getSoftVersion(String addr) {
        String command = addr + DefCommand.CMD_4_XBSTATUS_4 + "00";//5B02
        return DefCommand.getCommadBytes(command);
    }

    /**
     * 获得硬件件版本
     */
    public static byte[] getHardVersion(String addr) {
        String command = addr + DefCommand.CMD_4_XBSTATUS_5 + "00";//5B02
        return DefCommand.getCommadBytes(command);
    }

    /**
     * 设置单片机版本
     */
    public static byte[] setHardVersion(String addr, String data) {
        String command = addr + DefCommand.CMD_4_XBSTATUS_6 + "04" + data;//5B02
        return DefCommand.getCommadBytes(command);
    }

    /***
     *4.6、切换模块版本
     * @param addr
     * @param version
     * @return
     */
    public static byte[] send46(String addr, String version) {
        GreenDaoMaster master = new GreenDaoMaster();
        int total = master.queryDenatorBaseinfo().size();
        int a;
        if (total == 0) {
            a = 0;
        } else {
            a = (total - 1) / 50;
        }

        String b = Utils.intToHex(a);
        String c = Utils.addZero(b, 2);
        String command = addr + DefCommand.CMD_4_XBSTATUS_7 + "02" + version + c;//46
        return DefCommand.getCommadBytes(command);
    }

}
