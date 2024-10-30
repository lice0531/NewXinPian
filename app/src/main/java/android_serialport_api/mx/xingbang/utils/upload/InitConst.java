package android_serialport_api.mx.xingbang.utils.upload;


/**
 * 初始化常量集合
 */
public class InitConst {

    // /dev/ttyMT1
    // /dev/ttyGS3 /dev/ttyGS2 /dev/ttyGS1 /dev/ttyGS0
    // /dev/ttyS3 /dev/ttyS2 /dev/ttyS1 /dev/ttyS0

//    // St327 起爆器设备
//    public static final String SPORT_NAME = "/dev/ttyMSM0";  // 串口号
//    public static final boolean IS_Portrait = true;          // true竖屏
//    public static final int IS_PowerMode = 1;                // 上电模式


//    // Kt50 起爆器设备
//    public static final String SPORT_NAME = "/dev/ttyMT1";   // 串口号
//    public static final boolean IS_Portrait = true;          // true竖屏
//    public static final int IS_PowerMode = 0;                // 上电模式

//    // 级联设备 平板
//    public static final String SPORT_NAME = "/dev/ttyS3";    // 串口号
//    public static final boolean IS_Portrait = false;         // false横屏
//    public static final int IS_PowerMode = 0;                // 上电模式


    public static final String SPORT_NAME = "/dev/ttyS3";   // 串口号
    // 0 50 75 ... 400 0000
    public static final int BAUD_RATE = 115200;             // 波特率
    // 8 7 6 5
    public static final int DATA_BITS = 8;                  // 数据位
    // 0 1 2
    public static final int PARITY_BITS = 0;                // 校验位
    // 1 2
    public static final int STOP_BITS = 1;                  // 停止位
    // 0 1 2
    public static final int FLOW_CONS = 0;                  // 流控
    // 10 13 40
    public static final String CMD_CODE = "13";             // 指令号
    // 00 01 02 03 04
    public static final String ADDRESS_CODE = "00";         // 地址
    // 0 1 2
    public static final int VERSION_INDEX = 0;              // 起爆器版本指数


}
