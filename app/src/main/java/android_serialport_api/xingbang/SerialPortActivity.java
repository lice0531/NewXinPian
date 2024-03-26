/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android_serialport_api.xingbang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;


import com.orhanobut.dialogplus.DialogPlus;

import android_serialport_api.SerialPort;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.utils.LoadingUtils;
import android_serialport_api.xingbang.utils.Utils;

public abstract class SerialPortActivity extends BaseActivity {


    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    public ReadThread mReadThread;
    protected String revCmd;//执行命令
    protected String afterCmd;//后续命令
    private boolean isStop = false;
    // 通用
    public DialogPlus mDialogPlus;
    public Context mContext;
    private class ReadThread extends Thread {

        public boolean exit = false;

        @Override
        public void run() {
            super.run();
            try {
                while (!isInterrupted() && !exit) {
                    int size;

                    Thread.sleep(20);
                    if (exit) return;
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    //Utils.writeLog("Read------11111111");
                    size = mInputStream.read(buffer);
//                    mSerialPort.tcflush();//刷新方法,添加上后会丢失串口数据,以后再实验
                    //Utils.writeLog("Read------22222222");
                    if (size > 0) {
//                        byte[] cmdBuf = new byte[size];
//                        System.arraycopy(buffer, 0, cmdBuf, 0, size);
//                        String fromCommad = Utils.bytesToHexFun(cmdBuf);
//                        Log.e("收到: ",fromCommad );
                        onDataReceived(buffer, size);
                    }

                }
            } catch (IOException | InterruptedException e) {
                //e.printStackTrace();
                //return;
            }
//			Utils.writeLog("ReadThread End:"+Thread.currentThread().getName());
        }
    }
    public String[] mArr_Permissions = new String[]{
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SerialPortActivity.this.finish();
            }
        });
        b.show();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("父页面", "onCreate: " );
        try {
            mSerialPort = mApplication.getSerialPort();
            mSerialPort.tcflush();

            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            /* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        }
    }
    // 进度条
    public void showDialog() {
        mDialogPlus = LoadingUtils.loadDialog(mContext);
        mDialogPlus.show();
    }
    // 进度条
    public void initSerialPort() {
        Log.e("父页面", "initSerialPort: " );
//        mReadThread.exit = false;
        try {
            mSerialPort = mApplication.getSerialPort();
            mSerialPort.tcflush();

            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            /* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        }
    }
    protected abstract void onDataReceived(final byte[] buffer, final int size);

    @Override
    protected void onDestroy() {
        if (mReadThread != null) {

            mReadThread.exit = true;
            mReadThread.interrupt();
            sendInterruptCmd();
        }
        mApplication.closeSerialPort();
        mSerialPort = null;
        Log.e("父页面-destroy", "mReadThread: "+mReadThread.exit );
        super.onDestroy();
    }

    public void sendInterruptCmd() {

    }

    /**
     * 负责把分开的两条命令拼接成一个
     * */
    protected int completeValidCmd(String cmd) {
        if (cmd == null) return -1;
        if (revCmd == null) revCmd = "";
        this.afterCmd = "";
        revCmd += cmd;//把分两次发送的命令拼一起
        //分解判断命令
        String reVal = analysisCmd(revCmd);
        if (reVal.equals("00")) {
            return 0;
        }
        return 1;
    }

    /***
     * 分析命令
     * @param localcmd
     * @return
     */
    private String analysisCmd(String localcmd) {
        if (localcmd == null || localcmd.trim().length() < 1) return "-1";
        if (localcmd.trim().length() <= 12) return "-1";
        if (localcmd.indexOf("C0") == 0) {//说明是命令起点
            String dataLenStr = localcmd.substring(6, 8);
            int dataLen = Integer.parseInt(dataLenStr, 16) * 2;    //一个字节两个字符BCD码
            int cmdLen = dataLen + 14;
            if (cmdLen <= localcmd.length()) {//说明命令已经合适

                String endC0 = localcmd.substring(dataLen + 12, cmdLen);
                //Utils.writeLog("endC0="+endC0);
                if (endC0.equals("C0")) {//说明正确的命令
                    //正确的命令
                    String correctCmd = localcmd.substring(0, cmdLen);
                    if (cmdLen + 2 <= localcmd.length()) {//说明附带了后面命令数据
                        String afCmd = localcmd.substring(cmdLen);
                        if (afCmd.indexOf("C0") == 0) {//后续命令保存
                            afterCmd = afCmd;
                        }
                    }
                    //正确命令
                    revCmd = correctCmd;
                    return "00";
                }
            }
        } else {
            revCmd = "";
            afterCmd = "";
        }
        return "-1";
    }

    //是否正确结尾
    private int isCorrectCmd(String localcmd) {

        if (localcmd == null || localcmd.trim().length() < 1) return 1;
        if (localcmd.trim().length() <= 8) return 1;

        String dataLenStr = localcmd.substring(6, 8);
        int dataLen = Integer.parseInt(dataLenStr, 16);

        String complexCmd = localcmd;

        if ((dataLen + 14) == complexCmd.length()) {
            return 0;
        }
        return 1;
    }
}
