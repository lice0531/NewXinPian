package android_serialport_api.xingbang.cmd;

import android.util.Log;

import android_serialport_api.xingbang.cmd.vo.From12Reister;
import android_serialport_api.xingbang.utils.Utils;

public class OneReisterCmd {
    /***
     * 1.1进入自动注册模式
     * @param
     * @return
     */
    public static byte[] setToXbCommon_Reister_Init12_1(String addr) {

        String command = addr + DefCommand.CMD_1_REISTER_1 + "00";//001000
        return DefCommand.getCommadBytes(command);
    }

    /***
     * 1.1进入自动注册模式(检测桥丝)
     * @param test(00不检测01检测)
     * @return
     */
    public static byte[] setToXbCommon_Reister_Init12_2(String addr, String test) {

        String command = addr + DefCommand.CMD_1_REISTER_1 + "01" + test;//00100101
        return DefCommand.getCommadBytes(command);
    }

    /***
     * 1.1处理返回,进入自动注册模式
     * @param addr
     * @param from
     * @return
     */
    public static int getFromXbCommon_Reister_Init12_1(String addr, String from) {

        if (from == null) return -1;
        String command = addr + DefCommand.CMD_1_REISTER_1 + "00";
        String realyCmd1 = DefCommand.getCommadHex(command);
        if (from.indexOf(realyCmd1) >= 0) return 0;
        else return -1;
    }

    /***
     * 1.2提示有雷管接入
     * @param1 addr
     * @param2 data：
     * @return
     */
    public static int getFromXbCommon_Reister_Access12_2(String addr, String from) {

        if (from == null) return -1;
        String command = addr + DefCommand.CMD_1_REISTER_2 + "00";
        String realyCmd1 = DefCommand.getCommadHex(command);
        if (from.indexOf(realyCmd1) >= 0) return 0;
        else return -1;
    }

    /***
     * 是否包含120A
     * @param1 addr
     * @param2 data：
     * @return
     */
    public static int contains120A(String addr, String from) {

        if (from == null) return -1;
        String command = addr + DefCommand.CMD_1_REISTER_3 + "0A";//添加检测桥丝前是07//08
        //String realyCmd1 =DefCommand.getCommadHex(command);
        if (from.contains(command)) return 0;
        else return -1;
    }

    /***
     * 解码检测返回的1.3命令，自动返回雷管ID号
     * @param addr
     * @return
     */
    public static From12Reister decodeFromReceiveAutoDenatorCommand13(String addr, byte[] cmd) {

        String fromCommad = Utils.bytesToHexFun(cmd);
        String realyCmd1 = DefCommand.decodeCommand(fromCommad);
        if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
            return null;
        }
        if (contains120A(addr, realyCmd1) == 0) {
            if (realyCmd1 != null && realyCmd1.length() == 20) {
                String dataHex = realyCmd1.substring(6, 20);//取得返回数据
                From12Reister vo = new From12Reister();
                String readStatus = dataHex.substring(0, 2);//读取状态
//				Log.e("自动返回雷管id", "读取状态readStatus: "+readStatus );
                vo.setReadStatus(readStatus);

                String denaId = dataHex.substring(2, 10);//雷管id
                denaId = Utils.swop4ByteOrder(denaId);
                vo.setDenaId(denaId);
//				Log.e("自动返回雷管id", "雷管id denaId: "+denaId );
                String feature = dataHex.substring(10, 12);//特征号
                char c = (char) Integer.parseInt(feature, 16);
                vo.setFeature("" + c);

                String facCode = dataHex.substring(12);//特征号
                vo.setFacCode("" + Integer.parseInt(facCode, 16));
//				Log.e("自动返回雷管id", "特征号 facCode: "+facCode );
                return vo;
            } else {

            }
        } else {

        }
        return null;
    }

    /***
     * 解码检测返回的1.3命令，自动返回雷管ID号(桥丝检测)
     * @param addr
     * @return
     */
    public static From12Reister decodeFromReceiveAutoDenatorCommand14(String addr, byte[] cmd, String qiaosi_set) {
        //C0001208 FF 00 C5F97817 48 35 6EAAC0  新芯片
        String fromCommad = Utils.bytesToHexFun(cmd);
        String realyCmd1 = DefCommand.decodeCommand(fromCommad);

        if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
            return null;
        }
        if (contains120A(addr, realyCmd1) == 0) {
            if (realyCmd1 != null && realyCmd1.length() == 22) {
                String dataHex = realyCmd1.substring(6, 22);//取得返回数据
                From12Reister vo = new From12Reister();
                String readStatus = dataHex.substring(0, 2);//读取状态
                Log.e("自动返回雷管id", "dataHex: " + dataHex);
                vo.setReadStatus(readStatus);
                String wire = dataHex.substring(2, 4);//桥丝状态
//				Log.e("自动返回雷管id", "桥丝状态wire: "+wire );
                if (qiaosi_set.equals("true")) {
                    if (wire.equals("01")) {//&&readStatus.equals("FF")
                        vo.setWire("有");
                    } else {
                        vo.setWire("无");
                    }
                } else {
                    vo.setWire("不检测");
                }
//				vo.setWire(wire);
                String denaId = dataHex.substring(4, 12);//雷管id
                denaId = Utils.swop4ByteOrder(denaId);//字节码换位
                vo.setDenaId(denaId);
//				Log.e("自动返回雷管id", "雷管id denaId: "+denaId );
                String feature = dataHex.substring(12, 14);//特征号
                char c = (char) Integer.parseInt(feature, 16);
                vo.setFeature("" + c);
//                Log.e("新编码规则", "特征号 feature: "+feature );
//                Log.e("自动返回雷管id", "特征号 feature: "+Integer.parseInt(feature, 16) );
//                Log.e("自动返回雷管id", "特征号 c: "+c );
                String facCode = dataHex.substring(14);//管厂码
                int a = Integer.parseInt(facCode, 16);
                if (a < 10) {
                    String b = "0" + a;
                    vo.setFacCode("" + b);//因为转换int类型,所以06类型的就变成6,少了一位
                } else {
                    vo.setFacCode("" + a);
                }
//				Log.e("自动返回雷管id", "10进制管厂码 facCode: "+Integer.parseInt(facCode, 16) );
                return vo;
            }
        }
        return null;
    }

    /***
     * 解码检测返回的1.3命令，自动返回雷管ID号(桥丝检测)
     * @param addr
     * @return
     */
    public static From12Reister decode14_newXinPian(String addr, byte[] cmd, String qiaosi_set) {
        //C0001208 FF 00 C5F97817 48 35 6EAAC0  新芯片
        String fromCommad = Utils.bytesToHexFun(cmd);
        String realyCmd1 = DefCommand.decodeCommand(fromCommad);
        Log.e("自动返回雷管id", "realyCmd1: "+realyCmd1 );
        if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {
            return null;
        }
        if (contains120A(addr, realyCmd1) == 0) {//单芯片长度
            String dataHex = realyCmd1.substring(6);//取得返回数据
            From12Reister vo = new From12Reister();

            String readStatus = dataHex.substring(0, 2);//读取状态
            vo.setReadStatus(readStatus);

            String wire = dataHex.substring(2, 4);//桥丝状态
            if (qiaosi_set.equals("true")) {
                if (wire.equals("01")) {//&&readStatus.equals("FF")
                    vo.setWire("有");
                } else {
                    vo.setWire("无");
                }
            } else {
                vo.setWire("不检测");
            }
            String denaId = dataHex.substring(4, 12);//雷管id
            denaId = Utils.swop4ByteOrder(denaId);//字节码换位
            vo.setDenaId(denaId);

            String feature = dataHex.substring(12, 14);//特征号
            vo.setFeature(feature);

            String facCode = dataHex.substring(14, 16);//管厂码
            vo.setFacCode(facCode);

            String zhu_yscs = dataHex.substring(16);//主延时参数
            vo.setZhu_yscs(zhu_yscs);
//				Log.e("自动返回雷管id", "zhu_yscs: "+zhu_yscs );
            return vo;
        } else {
            //C0001208 FF 00 C5F97817 48 35 6EAAC0  新芯片
            //C000120C FF 00 C5F97817 48 35 C5F97817 6EAAC0  新芯片
            String dataHex = realyCmd1.substring(6, 30);//取得返回数据
            From12Reister vo = new From12Reister();
            String readStatus = dataHex.substring(0, 2);//读取状态
//            Log.e("自动返回雷管id", "dataHex: " + dataHex);
            vo.setReadStatus(readStatus);
            String wire = dataHex.substring(2, 4);//桥丝状态
//				Log.e("自动返回雷管id", "桥丝状态wire: "+wire );
            if (qiaosi_set.equals("true")) {
                if (wire.equals("01")) {//&&readStatus.equals("FF")
                    vo.setWire("有");
                } else {
                    vo.setWire("无");
                }
            } else {
                vo.setWire("不检测");
            }
            String denaId = dataHex.substring(4, 12);//雷管id
            denaId = Utils.swop4ByteOrder(denaId);//字节码换位
            vo.setDenaId(denaId);
//				Log.e("自动返回雷管id", "雷管id denaId: "+denaId );
            String feature = dataHex.substring(12, 14);//特征号
            vo.setFeature(feature);
//                Log.e("新编码规则", "特征号 feature: "+feature );
//                Log.e("自动返回雷管id", "特征号 feature: "+Integer.parseInt(feature, 16) );
//                Log.e("自动返回雷管id", "特征号 c: "+c );
            String facCode = dataHex.substring(14, 16);//管厂码
            vo.setFacCode(facCode);

            String zhu_yscs = dataHex.substring(16,20);//主芯片延时参数
            vo.setZhu_yscs(zhu_yscs);

            String denaId2 = dataHex.substring(20,28);//从雷管id
            denaId2 = Utils.swop4ByteOrder(denaId2);//字节码换位
            vo.setDenaIdSup(denaId2);

            String cong_yscs = dataHex.substring(28);//从芯片延时参数
            vo.setCong_yscs(cong_yscs);

//				Log.e("自动返回雷管id", "10进制管厂码 facCode: "+Integer.parseInt(facCode, 16) );
            return vo;
        }
    }

    /***
     * 1.4退出自动注册模式
     * @param
     * @return
     */
    public static byte[] setToXbCommon_Reister_Exit12_4(String addr) {

        String command = addr + DefCommand.CMD_1_REISTER_4 + "00";
        return DefCommand.getCommadBytes(command);
    }

    /***
     * 1.4处理返回,退出自动注册模式
     * @param addr
     * @param from
     * @return
     */
    public static int getFromXbCommon_Reister_Exit12_4(String addr, String from) {

        if (from == null) return -1;
        String command = addr + DefCommand.CMD_1_REISTER_4 + "00";
        String realyCmd1 = DefCommand.getCommadHex(command);
        if (from.indexOf(realyCmd1) >= 0) return 0;
        else return -1;
    }

    /***
     * 1.5核心板自检
     * @param
     * @return
     */
    public static byte[] setToXbCommon_Reister_Test(String data) {

        String command = "00" + DefCommand.CMD_1_REISTER_5 + "04" + data;
        return DefCommand.getCommadBytes(command);
    }
}
