package android_serialport_api.mx.xingbang.jilian;

import android_serialport_api.mx.xingbang.utils.CRC16;

/**
 * Created by Administrator on 2017/7/23.
 */

public class Protcol {

    static public int DetonatorNum;

    static public void InitDetonatorNum(int num) {
        DetonatorNum = num;
    }

    static public void AddDetonatorNum() {
        DetonatorNum++;
    }

    static public int GetDetonatorNum() {
        return DetonatorNum;
    }

    /**
     * 新的指令说明
     */
    //自动注册指令
    static public int ZCFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x10;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    static public int ZCFunc0(byte[] date, int num) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x10;
        date[3] = (byte) 0x01;
        date[4] = (byte) num;
        byte[] tm = new byte[4];
        for (int i = 0; i < 4; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 4);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }


    /**
     * 开启总线电源指令
     *
     * @return
     */
    static public int DcFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x41;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    /**
     * 网络测试指令
     *
     * @param date
     * @return
     */
    static public int CE(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x20;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    /**
     * 延期指令
     *
     * @param date
     * @return
     */
    static public int YQFunc(byte[] date, byte[] setpara) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x21;
        date[3] = (byte) 0x06;
        for (int i = 0; i < 6; i++) date[4 + i] = setpara[i];
        byte[] tm = new byte[9];
        for (int i = 0; i < 9; i++) {
            tm[i] = date[i + 1];
        }
//        int crc = MyFunc.exccitt(tm, 9);
//        date[10] = (byte) ((crc & 0xff00) >> 8);
//        date[11] = (byte) (crc & 0xff);
        byte[] crca = CRC16.GetCRC(tm);
        /*String crc = MyFunc.getCRC(tm);
        byte c[] = MyTools.hexStringToBytes(crc);*/
        date[10] = crca[0];
        date[11] = crca[1];
        date[12] = (byte) 0xC0;
        return 13;
    }

    /**
     * 退出网络测试
     *
     * @param date
     * @return
     */
    static public int EXITCE(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x22;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    /**
     * 起爆指令
     *
     * @param date
     * @return
     */
    static public int QB(byte[] date, int num) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x30;
        date[3] = (byte) 0x01;
        date[4] = (byte) num;
        byte[] tm = new byte[4];
        for (int i = 0; i < 4; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 4);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }

    /**
     * 起爆延期指令
     *
     * @param date
     * @return
     */
    static public int YQQBFunc(byte[] date, byte[] setpara) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x31;
        date[3] = (byte) 0x06;
        for (int i = 0; i < 6; i++) date[4 + i] = setpara[i];
        byte[] tm = new byte[9];
        for (int i = 0; i < 9; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 9);
        date[10] = (byte) ((crc & 0xff00) >> 8);
        date[11] = (byte) (crc & 0xff);
        date[12] = (byte) 0xC0;
        return 13;
    }

    /**
     * @param date
     * @return
     */
    static public int QB38Func(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x38;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    /**
     * 在网读ID检测
     *
     * @param date
     * @return
     */

    static public int CODE36Func(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x36;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    /**
     * 充电指令
     *
     * @param date
     * @return
     */

    static public int PWFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x32;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    /**
     * 高压输出
     *
     * @param date
     * @return
     */
    static public int GYFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x33;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    /**
     * 起爆指令
     *
     * @param date
     * @return
     */
    static public int QB1Func(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x34;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    /**
     * 退出起爆指令
     *
     * @param date
     * @return
     */

    static public int EXITQBFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x35;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    static public int ECFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x43;
        date[4] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 5);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }

    static public int EPFunc1(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x50;
        date[4] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 5);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }

    static public int EPFunc2(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x70;
        date[4] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 5);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }

    static public int EBFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x42;
        date[4] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 5);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }

    static public int EDFunc1(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x44;
        date[4] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 5);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }

    static public int EDFunc2(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x64;
        date[4] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 5);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }

    static public int ETFunc(byte[] date, byte[] setpara) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x54;
        date[4] = (byte) 0x06;
        for (int i = 0; i < 6; i++) date[5 + i] = setpara[i];
        int crc = MyFunc.exccitt(date, 5);
        date[11] = (byte) ((crc & 0xff00) >> 8);
        date[12] = (byte) (crc & 0xff);
        date[13] = (byte) 0xC0;
        return 14;
    }

    static public int DHFunc(byte[] date, int num) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x44;
        date[3] = (byte) 0x48;
        date[4] = (byte) 0x01;
        date[5] = (byte) num;
        int crc = MyFunc.exccitt(date, 6);
        date[6] = (byte) ((crc & 0xff00) >> 8);
        date[7] = (byte) (crc & 0xff);
        date[8] = (byte) 0xC0;
        return 9;
    }

    static public int DPFunc(byte[] date, int num) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x44;
        date[3] = (byte) 0x50;
        date[4] = (byte) 0x01;
        date[5] = (byte) num;
        int crc = MyFunc.exccitt(date, 6);
        date[6] = (byte) ((crc & 0xff00) >> 8);
        date[7] = (byte) (crc & 0xff);
        date[8] = (byte) 0xC0;
        return 9;
    }

    static public int ELFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x4C;
        date[4] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 5);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }

    /**
     * 电源信息查询
     */
    static public int CRFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x40;
        date[3] = (byte) 0x01;
        date[4] = (byte) 0x00;
        byte[] tm = new byte[4];
        for (int i = 0; i < 4; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 4);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) ((crc & 0xff00) >> 8);
        date[7] = (byte) 0xC0;
        return 8;
    }

    static public int EIFunc(byte[] date, int num) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x69;
        date[4] = (byte) 0x01;
        date[5] = (byte) num;
        int crc = MyFunc.exccitt(date, 6);
        date[6] = (byte) ((crc & 0xff00) >> 8);
        date[7] = (byte) (crc & 0xff);
        date[8] = (byte) 0xC0;
        return 9;
    }

    static public int ErFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x72;
        date[4] = (byte) 0x02;
        date[5] = (byte) 0x01;
        date[6] = (byte) 0x86;
        int crc = MyFunc.exccitt(date, 7);
        date[7] = (byte) ((crc & 0xff00) >> 8);
        date[8] = (byte) (crc & 0xff);
        date[9] = (byte) 0xC0;
        return 10;
    }

    static public int ErFuncForGetCode(byte[] date, byte code) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x45;
        date[3] = (byte) 0x72;
        date[4] = (byte) 0x02;
        date[5] = (byte) 0x01;
        date[6] = code;//0x05获取工厂代码，0x04获取特征号
        int crc = MyFunc.exccitt(date, 7);
        date[7] = (byte) ((crc & 0xff00) >> 8);
        date[8] = (byte) (crc & 0xff);
        date[9] = (byte) 0xC0;
        return 10;
    }

    static public int RDFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x56;
        date[3] = (byte) 0x52;
        date[4] = (byte) 0x00;
        date[5] = (byte) 0x73;
        date[6] = (byte) 0x00;
        date[7] = (byte) 0xC0;
        return 8;
    }

    static public int QuitZCFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x13;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    static public int QuitFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x44;
        date[3] = (byte) 0x43;
        date[4] = (byte) 0x01;
        date[5] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 6);
        date[6] = (byte) ((crc & 0xff00) >> 8);
        date[7] = (byte) (crc & 0xff);
        date[8] = (byte) 0xC0;
        return 9;
    }

    //C0 00 56 52 00 73 00 C0
    static public int getVersionFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x56;
        date[3] = (byte) 0x52;
        date[4] = (byte) 0x00;
        date[5] = (byte) 0x73;
        date[6] = (byte) 0x00;
        date[7] = (byte) 0xC0;
        return 8;
    }

    static public int getVersionNew(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x43;
        date[3] = (byte) 0x00;
        byte[] tm = new byte[3];
        for (int i = 0; i < 3; i++) {
            tm[i] = date[i + 1];
        }
        int crc = MyFunc.exccitt(tm, 3);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    static public int getSoftVersionFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x44;
        date[3] = (byte) 0x56;
        date[4] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 5);
        date[5] = (byte) ((crc & 0xff00) >> 8);
        date[6] = (byte) (crc & 0xff);
        date[7] = (byte) 0xC0;
        return 8;
    }

    /**
     * 软件版本
     */
    static public int getVersion1Func(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x43;
        date[3] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 4);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    /**
     * 硬件
     */
    static public int getVersion2Func(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x44;
        date[3] = (byte) 0x00;
        int crc = MyFunc.exccitt(date, 4);
        date[4] = (byte) ((crc & 0xff00) >> 8);
        date[5] = (byte) (crc & 0xff);
        date[6] = (byte) 0xC0;
        return 7;
    }

    static public int VSettingFunc(byte[] date, byte tag, byte v1, byte v2) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = tag;
        date[3] = (byte) 0x02;
        date[4] = v1;
        date[5] = v2;
        int crc = MyFunc.exccitt(date, 6);
        date[6] = (byte) ((crc & 0xff00) >> 8);
        date[7] = (byte) (crc & 0xff);
        date[8] = (byte) 0xC0;
        return 9;
    }

    static public int VSettingFunc2(byte[] date, byte v1, byte v2, byte v11, byte v22) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x14;
        date[3] = (byte) 0x04;
        date[4] = v1;
        date[5] = v2;
        date[6] = v11;
        date[7] = v22;
        int crc = MyFunc.exccitt(date, 8);
        date[8] = (byte) ((crc & 0xff00) >> 8);
        date[9] = (byte) (crc & 0xff);
        date[10] = (byte) 0xC0;
        return 11;
    }

    static public int testFunc(byte[] date) {
        date[0] = (byte) 0xC0;
        date[1] = (byte) 0x00;
        date[2] = (byte) 0x43;
        date[3] = (byte) 0x52;
        date[4] = (byte) 0x01;
        date[5] = (byte) 0x02;
        int crc = MyFunc.exccitt(date, 6);
        date[6] = (byte) ((crc & 0xff00) >> 8);
        date[7] = (byte) (crc & 0xff);
        return 8;
    }

    static public int CheCkRe(byte[] date, int size, byte[] redata) {
        if (date[0] != (byte) 0xC0) {
            return 0xff;
        } else {
            if (date[2] == (byte) 0x41) {
                return 0x41;   //DC 2.0开启电源
            }
            if (date[2] == (byte) 0x40) {
                return 0x40;
            }
            if (date[2] == (byte) 0x10) {
                return 0x10;   //ZC 2.0自动注册
            }
            if (date[2] == (byte) 0x20) {
                return 0x20;   //CE 2.0网络测试
            }
            if (date[2] == (byte) 0x21) {
                return 0x21;   //CE 2.0延期数值
            }
            if (date[2] == (byte) 0x21) {
                return 0x21;   //CE 2.0延期数值
            }
            if (date[2] == (byte) 0x30) {
                return 0x30;   //QB 2.0起爆流程
            }
            if (date[2] == (byte) 0x31) {
                return 0x31;   //QB 2.起爆延期
            }
            if (date[2] == (byte) 0x32) {
                return 0x32;   //QB 2.充电指令
            }
            if (date[2] == (byte) 0x33) {
                return 0x33;   //QB 2.高压输出
            }
            if (date[2] == (byte) 0x36) {
                return 0x36;   //QB 2.在网读ID检测
            }
            if (date[2] == (byte) 0x34) {
                return 0x34;   //QB 2.起爆指令
            }
        }
        return 0;
    }

    static public int CheCkRe1(byte[] date, int size, byte[] redata) {
        if (date[0] != (byte) 0xC0) {
            return 0xff;
        } else {

            if (date[2] == (byte) 0x45 && date[3] == (byte) 0x72)    //ER
            {
                if (date[4] == (byte) 0x01) {
                    for (int i = 0; i < 2; i++) {
                        redata[i] = date[2 + i];
                    }
                    return 0x16;
                }
            } else if (date[2] == (byte) 0x00 || date[3] == (byte) 0x00) {
                return 0x01;
            }
        }
        return 0;
    }

}
