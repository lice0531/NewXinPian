package android_serialport_api.mx.xingbang.cmd;


/**
 * @ClassName: Cmd_STM32IAP
 * @Description:
 * @Date: 11/26/20 12:42 PM
 * @Author: kalinaji
 */
public class Cmd_EX {

    /**
     * 强制升级指令 (上位机——下位机)
     */
    public static byte[] sendE0() {
        return DefCmd.getCommandBytes("E000");
    }

    /**
     * 开始升级指令 (上位机——下位机)
     *
     * @param number 固件分割的块数
     * @param format 每块固件分割大小(单位：01 表示 1024字节，02表示2048……）
     */
    public static byte[] sendE1(int number, int format) {

        if (format == 1024){
            return DefCmd.getCommandBytes("E102" + number + "01");
        } else if (format == 2048){
            return DefCmd.getCommandBytes("E102" + number + "02");
        } else {
            return null;
        }
    }

    /**
     * 固件发送和接收：(上位机——下位机）
     * @param ZZ 1024字节数据
     */
    public static byte[] sendE2(String ZZ) {
        return DefCmd.getCommandBytes("E201" + ZZ);
    }

    /**
     * 重启下位机指令: (上位机——下位机）
     */
    public static byte[] sendE3() {
        return DefCmd.getCommandBytes("E300");
    }

    /**
     * 重启下位机指令: (上位机——下位机）
     */
    public static byte[] sendE4() {
        return DefCmd.getCommandBytes("E400");
    }


}
