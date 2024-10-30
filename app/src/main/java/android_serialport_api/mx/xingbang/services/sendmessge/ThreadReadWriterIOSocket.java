package android_serialport_api.mx.xingbang.services.sendmessge;


import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android_serialport_api.mx.xingbang.utils.Utils;

public class ThreadReadWriterIOSocket implements Runnable {
    private Socket client;
    private Context context;
    private BufferedOutputStream out;
    private BufferedInputStream in;
    private int sum=0;

    ThreadReadWriterIOSocket(Context context, Socket client) {

        this.client = client;
        this.context = context;
    }

    public void sendData(String data) {
        try {
            out.write(data.getBytes());
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        Log.e("socket线程", Thread.currentThread().getName()
                + "---->"
                + "a client has connected to server!");

        try {
            /* PC端发来的数据msg */
            String currCMD = "";
            out = new BufferedOutputStream(client.getOutputStream());
            in = new BufferedInputStream(client.getInputStream());
            // testSocket();// 测试socket方法
            ConnectService.ioThreadFlag = true;

            while (ConnectService.ioThreadFlag) {
                try {
                    if (!client.isConnected()) {
                        break;
                    }

                    sum++;
                    /* 接收PC发来的数据 */
//                    Log.e("socket线程", Thread.currentThread().getName() + "---->" + "will read......");
                    /* 读操作命令 */
                    currCMD = readCMDFromSocket(in);
//                    Log.e("socket线程", Thread.currentThread().getName() + "  currCMD ==== " + currCMD);
//                    Log.e("socket线程", "currCMD==" + currCMD.toString());
//                    out.write("ok".getBy
                    /* 根据命令分别处理数据 */

                    if (currCMD.equals("1")) {
                        out.write("OK".getBytes());
                        out.flush();
                    } else if (currCMD.equals("2")) {
                        out.write("OK".getBytes());
                        out.flush();
                    } else if (currCMD.equals("Upload success")) {
                        EventBus.getDefault().post(new MessageEvent("上传数据成功"));
                    } else if (currCMD.equals("ok")) {
                        EventBus.getDefault().post(new MessageEvent("通讯成功"));
 //                       out.write("ok".getBytes());
//                        out.flush();

                    } else if (currCMD.equals("3")) {
                        out.write("OK".getBytes());
                        out.flush();
                    } else if (currCMD.equals("4")) {
                        /* 准备接收文件数据 */
                        try {
                            out.write("service receive OK".getBytes());
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        /* 接收文件数据，4字节文件长度，4字节文件格式，其后是文件数据 */
                        byte[] filelength = new byte[4];
                        byte[] fileformat = new byte[4];
                        byte[] filebytes = null;

                        /* 从socket流中读取完整文件数据 */
                        filebytes = receiveFileFromSocket(in, out, filelength,
                                fileformat);

                        // Log.e(Service139.TAG, "receive data =" + new
                        // String(filebytes));
                        try {
                            /* 生成文件 */
                            File file = FileHelper.newFile("R0013340.JPG");
                            FileHelper.writeFile(file, filebytes, 0,
                                    filebytes.length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (currCMD.equals("exit")) {

                    }
                } catch (Exception e) {
                    // try {
                    // out.write("error".getBytes("utf-8"));
                    // out.flush();
                    // } catch (IOException e1) {
                    // e1.printStackTrace();
                    // }
                    Log.e("socket线程", Thread.currentThread().getName()
                            + "---->" + "read write error111111");
                }
            }

            out.close();
            in.close();
        } catch (Exception e) {
            Log.e("socket线程", Thread.currentThread().getName()
                    + "---->" + "read write error222222");
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    Log.e("socket线程", Thread.currentThread().getName()
                            + "---->" + "client.close()");
                    client.close();
                }
            } catch (IOException e) {
                Log.e("socket线程", Thread.currentThread().getName()
                        + "---->" + "read write error333333");
                e.printStackTrace();
            }
        }
    }

    /**
     * 功能：从socket流中读取完整文件数据
     * <p>
     * InputStream in：socket输入流
     * <p>
     * byte[] filelength: 流的前4个字节存储要转送的文件的字节数
     * <p>
     * byte[] fileformat：流的前5-8字节存储要转送的文件的格式（如.apk）
     */
    public static byte[] receiveFileFromSocket(InputStream in,
                                               OutputStream out, byte[] filelength, byte[] fileformat) {
        byte[] filebytes = null;// 文件数据
        try {
            in.read(filelength);// 读文件长度
            int filelen = Utils.bytesToInt(filelength);// 文件长度从4字节byte[]转成Int
            String strtmp = "read file length ok:" + filelen;
            out.write(strtmp.getBytes("utf-8"));
            out.flush();

            filebytes = new byte[filelen];
            int pos = 0;
            int rcvLen = 0;
            while ((rcvLen = in.read(filebytes, pos, filelen - pos)) > 0) {
                pos += rcvLen;
            }
            Log.e("socket线程", Thread.currentThread().getName()
                    + "---->" + "read file OK:file size=" + filebytes.length);
            out.write("read file ok".getBytes("utf-8"));
            out.flush();
        } catch (Exception e) {
            Log.e("socket线程", Thread.currentThread().getName()
                    + "---->" + "receiveFileFromSocket error");
            e.printStackTrace();
        }
        return filebytes;
    }

    /* 读取命令 */
    public static String readCMDFromSocket(InputStream in) {
        int MAX_BUFFER_BYTES = 2048;
        String msg = "";
        byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
        try {
            int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
            msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
            tempbuffer = null;
        } catch (Exception e) {
            Log.e("socket线程", Thread.currentThread().getName()
                    + "---->" + "readFromSocket error");
            e.printStackTrace();
        }
        // Log.e(Service139.TAG, "msg=" + msg);
        return msg;
    }
}
