package android_serialport_api.xingbang.services.outface;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.xingbang.services.socket.ChinaFireSocketClient;

import android_serialport_api.xingbang.utils.Utils;

public class FinishDenatorToUpMain {
    private static ChinaFireSocketClient socketClient;

    public static void main(String args[]) {

        startList("14.23.69.2", 1088);
        String equNo = "XB0001";
        String x = "117.585984";
        String y = "36.445464";
        String fireDate = "2019-5-01 22:50:18";
        List<String> dataList = new ArrayList<String>();
        dataList.add("5891213H00001O");
        dataList.add("5891213H00002O");
        dataList.add("5891213H00003O");
        dataList.add("5891213H00004O");
        dataList.add("5891213H00005O");

        // int test = Utils.ascStrToInt("3132");
        // System.out.println(test);

        // String crc = FinishDenatorToUpMain.getDataXor("*020004800XB00011175859364454181203225018005");
        // System.out.println(crc);
        List<String> sendListPack = FinishDenatorToUpMain.packSendData(equNo, x, y, fireDate, dataList);


        for (int j = 0; j < sendListPack.size(); j++) {
            System.out.println(sendListPack.get(j));
            String pstr = (sendListPack.get(j));
            //#14R020102117$
            socketClient.sendData(pstr);
            //break;
        }
        boolean sendFlag = false;
        int loopCount = 0;
        do {
            try {
                Thread.sleep(100);
                if (socketClient.getResponFlag() == 2) {
                    //socketClient.setResponFlag(0);

                    String responseText = socketClient.getResposeText();

                    if ("#08O100$".equals(responseText)) {//完全成功
                        {
                            sendFlag = true;
                        }
                    } else {
                        if (loopCount > 2) break;

                        //处理未发成功的记录数据
                        String endStr = responseText.substring(responseText.length() - 1);
                        int responseTextLen = responseText.length();
                        String dataErrorSerial = "";
                        if (responseText.indexOf("#") == 0 && endStr.equals("$")) {
                            String data = responseText.substring(8);
                            data = data.substring(0, data.length() - 4);

                            int errAmount = Utils.ascStrToInt(data);

                            int subLen = 8;
                            for (int i = 0; i < errAmount; i++) {
                                subLen = 8 + i * 4;
                                if (subLen + 4 <= responseTextLen) {
                                    data = responseText.substring(subLen, subLen + 4);
                                    dataErrorSerial = "#" + data;
                                }
                            }
                            if (dataErrorSerial != null && dataErrorSerial.length() > 0) {
                                String no = "";
                                for (int j = 0; j < sendListPack.size(); j++) {
                                    String pstr = (sendListPack.get(j));
                                    no = pstr.substring(6, 10);
                                    if (dataErrorSerial.indexOf("#" + no) >= 0) {
                                        //byte[] bytes= pstr.getBytes();
                                        socketClient.sendData(pstr);
                                    }
                                    //break;
                                }
                                loopCount++;
                            }
                        } else {

                        }
                    }
                } else {

                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (sendFlag == false);

    }

    public static int sendData_new(String equNo, String ip, int port, String x, String y, String fireDate, List<String> dataList) {

        List<String> sendListPack = FinishDenatorToUpMain.packSendData(equNo, x, y, fireDate, dataList);
        socketClient.sendData(sendListPack);
        boolean sendFlag = false;
        int loopCount = sendListPack.size();
        int i = 0;
        do {
            try {
                Thread.sleep(100);
                if (socketClient.getResponFlag() == 2) {
                    sendFlag = true;
                    FinishDenatorToUpMain.stopSocket();
                } else {
                    i++;
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (sendFlag == false && i <= loopCount);

        if (sendFlag == false) return 1;

        return 0;
    }

    public static int sendData(String equNo, String ip, int port, String x, String y, String fireDate, List<String> dataList) {

        List<String> sendListPack = FinishDenatorToUpMain.packSendData(equNo, x, y, fireDate, dataList);
        for (int j = 0; j < sendListPack.size(); j++) {
            System.out.println(sendListPack.get(j));
            String pstr = (sendListPack.get(j));
            byte[] bytes = pstr.getBytes();
            //#14R020102117$
            socketClient.sendData(pstr);

            //break;
        }
        boolean sendFlag = false;
        int loopCount = 0;
        do {
            try {
                Thread.sleep(100);
                if (socketClient.getResponFlag() == 2) {
                    //socketClient.setResponFlag(0);

                    String responseText = socketClient.getResposeText();

                    if ("#08O100$".equals(responseText)) {//完全成功
                        {
                            sendFlag = true;
                        }
                    } else {
                        if (loopCount > 2) break;

                        //处理未发成功的记录数据
                        String endStr = responseText.substring(responseText.length() - 1);
                        int responseTextLen = responseText.length();
                        String dataErrorSerial = "";
                        if (responseText.indexOf("#") == 0 && endStr.equals("$")) {
                            String data = responseText.substring(8);
                            data = data.substring(0, data.length() - 4);

                            int errAmount = Utils.ascStrToInt(data);

                            int subLen = 8;
                            for (int i = 0; i < errAmount; i++) {
                                subLen = 8 + i * 4;
                                if (subLen + 4 <= responseTextLen) {
                                    data = responseText.substring(subLen, subLen + 4);
                                    dataErrorSerial = "#" + data;
                                }
                            }
                            if (dataErrorSerial != null && dataErrorSerial.length() > 0) {
                                String no = "";
                                for (int j = 0; j < sendListPack.size(); j++) {
                                    String pstr = (sendListPack.get(j));
                                    no = pstr.substring(6, 10);
                                    if (dataErrorSerial.indexOf("#" + no) >= 0) {
                                        //byte[] bytes= pstr.getBytes();
                                        socketClient.sendData(pstr);
                                    }
                                    //break;
                                }
                                loopCount++;
                            }
                        } else {

                        }
                    }
                } else {
                    Thread.sleep(1000);
                    loopCount++;
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (sendFlag == false && loopCount <= 2);

        if (sendFlag == false) return 1;

        return 0;
    }
    /**
     * 把数据封装成中爆网接收形式
     * */
    public static List<String> packSendData(String equNo, String x, String y, String fireDate, List<String> dataList) {

        List<String> pkList = new ArrayList<String>();

        int amount = dataList.size();

        int packPure = amount % 10 != 0 ? amount / 10 + 1 : amount / 10;
        int packTotal = packPure + 1;

        String tempequNo = equNo;
        String headMSG = packDataHead(tempequNo, x, y, fireDate, amount);

        pkList.add(headMSG);

        String totalPackStr = Utils.strPaddingZero(packTotal, 2);
        String currentSerial = "";
        String headPrior = "*" + totalPackStr + currentSerial;
        String eNo = "";
        String headAfter = "";
        int currentPackLen = 0;
        String currentPackLenStr = "";
        eNo = Utils.strPaddingZero(tempequNo, 8);

        int currentDataIndex = 0;
        for (int i = 2; i <= packTotal; i++) {
            currentSerial = Utils.strPaddingZero(i, 2);
            headPrior = "*" + totalPackStr + currentSerial;
            String tempSubDeStr = "";
            //for(int curretPk=0;curretPk<packPure;curretPk++){//包数

            for (int w = 0; w < 10; w++) {//雷管数

                tempSubDeStr += Utils.strPaddingZero(dataList.get(currentDataIndex), 14);
                currentDataIndex++;
                if (currentDataIndex >= amount) break;

            }
            //}
            headAfter = eNo + tempSubDeStr;
            currentPackLen = headAfter.length();
            currentPackLen = 1 + 2 + 2 + 3 + currentPackLen + 3 + 1;
            currentPackLenStr = Utils.strPaddingZero(currentPackLen, 3);
            String baseHeadPack = headPrior + currentPackLenStr + headAfter;
            String checkStr = getDataXor(baseHeadPack);
            String subPack = baseHeadPack + checkStr + "$";
            pkList.add(subPack);
        }
        return pkList;
    }

    public static String getDataXor(String data) {
        byte[] byteArray = data.getBytes();
        byte by = getXor(byteArray, 0, data.length());
        int checkby = Utils.byteToInt(by);
        String checkStr = Utils.strPaddingZero(checkby, 3);
        return checkStr;
    }

    public static String packDataHead(String equNo, String x, String y, String fireDate, int amount) {

        int packPure = amount % 10 != 0 ? amount / 10 + 1 : amount / 10;
        int packTotal = packPure + 1;

        x = x.replace(".", "");
        x = Utils.strPaddingZero(x, 7);
        y = y.replace(".", "");
        y = Utils.strPaddingZero(y, 6);

        String denatorCount = Utils.strPaddingZero(amount, 3);
        equNo = Utils.strPaddingZero(equNo, 8);
        String totalPackStr = Utils.strPaddingZero(packTotal, 2);
        String currentSerial = Utils.strPaddingZero("1", 2);
        int currentPackLen = 0;
        String dateStr = fireDate.substring(0, 10);
        dateStr = dateStr.replaceAll("-", "");
        dateStr = dateStr.substring(2);
        String timeStr = fireDate.substring(11);
        timeStr = timeStr.replaceAll(":", "");

        String headPrior = "*" + totalPackStr + currentSerial;//数据包总数+数据包序号
        String headAfter = equNo + x + y + dateStr + timeStr + denatorCount;//起爆器编号+经度+纬度+日期+时间+雷管总数
        currentPackLen = headPrior.length() + 3 + headAfter.length() + 3 + 1;//数据包总长度
        String currentPackLenStr = Utils.strPaddingZero(currentPackLen, 3);//保证长度为3位
        String baseHeadPack = headPrior + currentPackLenStr + headAfter;//数据包
        String checkStr = getDataXor(baseHeadPack);//校验和
        String headPack = baseHeadPack + checkStr + "$";//第一次数据包

        return headPack;

    }

    private static byte getXor(byte[] data, int pos, int len) {
        byte A = 0;
        for (int i = pos; i < len; i++) {
            A ^= data[i];
        }
        return A;
    }

    public static void stopSocket() {
        if (socketClient != null) socketClient.stopSocket();
    }

    public static void startList(String serverAddress, int port) {

        Socket socket;
        try {
            socket = new Socket(serverAddress, port);
            socketClient = new ChinaFireSocketClient(socket, null);
            socketClient.start();


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
