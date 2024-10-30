package android_serialport_api.mx.xingbang.cmd;

import android.util.Log;

import android_serialport_api.mx.xingbang.utils.CRC16;

/**
 * 默认命令
 */
public class DefCmd {

    // 1.X指令
    public final static String CMD_10 = "10"; // 进入自动注册模式
    public final static String CMD_11 = "11"; // 提示有雷管接入
    public final static String CMD_12 = "12"; // 自动返回雷管ID号
    public final static String CMD_13 = "13"; // 退出自动注册模式
    public final static String CMD_14 = "14"; // 核心板自检
    // 2.X指令
    public final static String CMD_20 = "20"; // 进入测试模式
    public final static String CMD_21 = "21"; // 写入延时时间，检测结果看雷管是否正常
    public final static String CMD_22 = "22"; // 退出测试模式
    // 3.X指令
    public final static String CMD_30 = "30"; // 进入起爆模式
    public final static String CMD_31 = "31"; // 写入延时时间，检测结果看雷管是否正常
    public final static String CMD_32 = "32"; // 充电（雷管充电命令 等待6S（500米线，200发雷管），5.5V充电）
    public final static String CMD_33 = "33"; // 高压输出（继电器切换，等待12S（500米线，200发雷管）16V充电）
    public final static String CMD_34 = "34"; // 起爆
    public final static String CMD_35 = "35"; // 退出起爆模式
    public final static String CMD_36 = "36"; // 在网读ID检测
    public final static String CMD_37 = "37"; // 异常终止起爆
    public final static String CMD_38 = "38"; // 充电检测
    // 4.X指令
    public final static String CMD_40 = "40"; // 获取电源状态指令
    public final static String CMD_41 = "41"; // 开启总线电源指令
    public final static String CMD_42 = "42"; // 总线翻转指令(检测设备)
    public final static String CMD_43 = "43"; // 获取软件版本
    public final static String CMD_44 = "44"; // 获取硬件版本
    public final static String CMD_45 = "45"; // 设置单片机版本
    // 5.X指令
    public final static String CMD_50 = "50"; // 进入检测模式
    public final static String CMD_51 = "51"; // 写入ID
    public final static String CMD_52 = "52"; // 写入ID查询（多通道）
    public final static String CMD_53 = "53"; // 检测
    public final static String CMD_54 = "54"; // 检测查询（多通道）
    public final static String CMD_55 = "55"; // 读ID
    public final static String CMD_5A = "5A"; // 退出检测模式
    public final static String CMD_5B = "5B"; // 设置低压
    public final static String CMD_5C = "5C"; // 设置高压
    // 7.X指令
    public final static String CMD_70 = "70"; // 传递uid
    public final static String CMD_71 = "71"; // 启动组网测试
    public final static String CMD_72 = "72"; // 最后一通道主动发送指令
    public final static String CMD_73 = "73"; // 读取组网检测结果
    public final static String CMD_74 = "74"; // 传递uid和延期
    public final static String CMD_75 = "75"; // 启动起爆检测
    public final static String CMD_76 = "76"; // 最后一通道主动发送指令
    public final static String CMD_77 = "77"; // 读取起爆检测结果


    /**
     * 得到发出命令字节
     * @param baseCmd 例如: 001000
     */
    public static byte[] getCommandBytes(String baseCmd) {
        String realCmd = getCommandHex(baseCmd);
        // String -> byte
        return CRC16.hexStringToByte(realCmd);
    }

    /**
     * 得到字符串CRC，高字节在前
     * @param baseCmd
     */
    public static String getCRCCode(String baseCmd) {
        byte[] cy = CRC16.hexStringToByte(baseCmd);
        byte[] crcb = CRC16.GetCRC(cy);
        return CRC16.bytesToHexString(crcb);
    }

    /**
     * 得到CRC 低字节在前
     * @param baseCmd
     */
    public static String getLowByteBeforeCRCCode(String baseCmd) {
        String crs16 = getCRCCode(baseCmd);
        return crs16.substring(2) + crs16.substring(0, 2);
    }

    /**
     * 得到命令的Hex(16进制表达)
     *
     * @param baseCmd
     * @return C0 001000
     */
    public static String getCommandHex(String baseCmd) {
        String crs16 = getCRCCode(baseCmd);
        return "C0" + baseCmd + crs16.substring(2) + crs16.substring(0, 2) + "C0";
    }

    /**
     * 一版指令返回
     * @param cmd
     */
    public static String getCmd(String cmd) {
//        Log.e("DefCmd", "getCmd cmd: " + cmd);
        if (cmd.length() > 4) {
            return cmd.substring(4, 6);
        }
        return null;
    }

    /**
     * 升级指令返回
     * @param cmd
     */
    public static String getCmdUp(String cmd) {
//        Log.e("DefCmd", "getCmdUp cmd: " + cmd);
        if (cmd.length() > 4) {
            return cmd.substring(2, 4);
        }
        return null;
    }

    /**
     * 得到 返回地址
     * @param cmdInfo
     */
    public static String getAddress(String cmdInfo) {
//        Log.e("返回地址", cmdInfo);
        if (cmdInfo.length() > 4){
            return cmdInfo.substring(2, 4);
        }
        return null;
    }
    /**
     * 获取E4返回指令一致数量
     * @param cmd
     */
    public static String getE4AgreementNumber(String cmd) {
        Log.e("DefCmd", "getE4AgreementNumber cmd: " + cmd);
        if (cmd.length() > 4) {
            // 截取命令
            return cmd.substring(6, 8);
        }
        return null;
    }

    /**
     * 指令解析(检测指令正确性)
     * @return 指令不正确：-1 -2
     * @return 指令正确： info
     */
    public synchronized static String decodeCommand(String command) {

        // 将String类型指令转化成 全大写
        String cmd = command.toUpperCase();

        // 指令不正确
        if (cmd.trim().length() < 6) {
            return "-1";
        }

        int head_position = cmd.indexOf("C0");
        int end_position;
        String subStr;

        if (head_position >= 0) {
            int twoPos = cmd.lastIndexOf("C0");
            if (twoPos == head_position) {//说明起始位存在两个C0C0
                head_position += 2;
                subStr = cmd.substring(head_position);
                end_position = subStr.indexOf("C0");
                if (end_position > 1) {
                    subStr = subStr.substring(0, end_position);
                } else {
                    return "-1";
                }

            } else {
                if (twoPos < 0) {//说明不存在两个C0C0
                    subStr = cmd.substring(head_position + 2);
                    end_position = subStr.indexOf("C0");
                    if (end_position > 1) {
                        subStr = subStr.substring(0, end_position);
                    } else {
                        return "-1";
                    }
                } else {
                    end_position = twoPos;
                    subStr = cmd.substring(head_position + 2, end_position);
                }
            }

        } else {

            return "-1";
        }

        if (subStr.length() > 4) {
            String ocrc = subStr.substring(subStr.length() - 4);
            String inInfo = subStr.substring(0, subStr.length() - 4);
            String dcrc = getLowByteBeforeCRCCode(inInfo);

            if (!dcrc.equals(ocrc)) {
                return "-2";
            } else {
                return inInfo;
            }

        } else {
            return "-1";
        }

    }


}
