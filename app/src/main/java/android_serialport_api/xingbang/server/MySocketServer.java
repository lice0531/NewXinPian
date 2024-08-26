package android_serialport_api.xingbang.server;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.xingbang.models.DeviceBean;
import android_serialport_api.xingbang.utils.GetIpAddress;
import android_serialport_api.xingbang.utils.upload.InitConst;

/**
 * Created by suwen on 2018/4/8.
 */

public class MySocketServer {
    private boolean isEnable;
    private final WebConfig webConfig;//配置信息类
    private final ExecutorService threadPool;//线程池
    private final ExecutorService threadPoolWrite;//线程池
    private ServerSocket socket;
    private String TAG = "MySocketServer";
    private Map<String, Socket> socketMap = new HashMap<>();
    private Handler handler;

    public MySocketServer(WebConfig webConfig, Handler handler) {
        this.webConfig = webConfig;
        threadPool = Executors.newCachedThreadPool();
        this.handler = handler;
        threadPoolWrite = Executors.newCachedThreadPool();
    }

    /**
     * 开启server
     */
    public void startServerAsync() {
        isEnable = true;
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                doProcSync();
            }
        });
    }

    /**
     * 关闭server
     */
    public void stopServerAsync() throws IOException {
        if (!isEnable) {
            return;
        }
        isEnable = true;
        socket.close();
        socket = null;
    }

    private void doProcSync() {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(webConfig.getPort());
            socket = new ServerSocket();
            socket.bind(socketAddress);
            GetIpAddress.getLocalIpAddress(socket);
            Message msg = new Message();
            msg.what = 6;
            msg.obj = GetIpAddress.getIP();
            handler.sendMessage(msg);
            while (isEnable) {
                final Socket remotePeer = socket.accept();

//                socketMap.put(remotePeer.getRemoteSocketAddress().toString(), remotePeer);
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG,"remotePeer..............." + remotePeer.getRemoteSocketAddress().toString());
//                        LLogger.e("remotePeer..............."+remotePeer.getRemoteSocketAddress().toString());
                        onAcceptRemotePeer(remotePeer);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 如果没有收到指令，那么则需要延迟5秒再次发送指令
     * 1.通信协议请求受控指令等待主APP发出指令，收到指令则连接成功
     * 2.主APP发出网络检测的指令，分机APP进行网络监测的功能，检测完成发出指令结果，收到主APP应答
     * 3.充电指令
     * 4.起爆指令，时间同步
     */
    OutputStream os;

    private void onAcceptRemotePeer(Socket remotePeer) {
        try {
//            remotePeer.getOutputStream().write("0001".getBytes());//告诉客户端连接成功
            // 从Socket当中得到InputStream对象
            InputStream inputStream = remotePeer.getInputStream();
            byte buffer[] = new byte[60];
            int temp = 0;
            os = remotePeer.getOutputStream();
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = inputStream.read(buffer)) != -1) {
//                LLogger.e(new String(buffer, 0, temp,"UTF-8"));
                String res = new String(buffer, 0, temp, "UTF-8");
                Log.e(TAG,"接收来自客户端的数据：" + res);
//                Utils.showTs(new String(buffer, 0, temp, "UTF-8"));
                Message msg = new Message();
                if (res.startsWith("00")) {
                    if (res.startsWith("0001")) {
                        //同步
                        msg.what = 0;
                        DeviceBean bean = new DeviceBean();
                        bean.setName(remotePeer.getRemoteSocketAddress().toString());
                        bean.setSocket(remotePeer);
                        bean.setRes(res.substring(0, 4));
                        bean.setCode(res.substring(4));
                        msg.obj = bean;
                    } else if (res.startsWith("0002")) {
                        //网络测试交互指令
                        msg.what = InitConst.CODE_NET;
//                        String c[] = res.substring(4).split(",", -1);
//                        NetBean bean = new NetBean();
//                        bean.setCode(c[0]);
//                        bean.setmType(c[1]);
//                        bean.setTotalNum(c[2]);
//                        bean.setFaultNum(c[3]);
//                        bean.setTotalU(c[4]);
//                        bean.setTotalI(c[5]);
//                        bean.setTip(c[6]);
//                        msg.obj = bean;
                    } else if (res.startsWith("0003")) {
                        //充电
                        msg.what = InitConst.CODE_CHONGDIAN;
//                        String c[] = res.substring(4).split(",", -1);
//                        ChargeReBean bean = new ChargeReBean();
//                        bean.setCode(c[0]);
//                        bean.setType(c[1]);
//                        bean.setU(c[2]);
//                        bean.setI(c[3]);
//                        msg.obj = bean;
                    } else if (res.startsWith("0004")) {
                        //起爆
                        msg.what = InitConst.CODE_QIBAO;
//                        String c[] = res.substring(4).split(",", -1);
//                        QibaoReBean bean = new QibaoReBean();
//                        bean.setCode(c[0]);
//                        bean.setType(c[1]);
//                        bean.setTip(c[2]);
//                        msg.obj = bean;
                    } else if (res.equals("0006")) {
                        msg.what = InitConst.CODE_QIBAO_TAG;
                    }

                } else {
                    DeviceBean bean = new DeviceBean();
                    bean.setRes("--");
                    msg.what = 0;
                    msg.obj = bean;
                }

                handler.sendMessage(msg);


                /*if (res.length() > 4 && res.substring(0, 5).equals("0001")) {
                    //00头部，01受控指令
                    os.write("A001\n".getBytes());//同步确认指令
                    os.flush();
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送给最新客户端的同步指令
     *
     * @param content
     */
    public void writeData(final String content) {
        switch (Build.DEVICE){
            case "T-QBZD-Z6":
            case "M900":


                break;
            default:
                threadPoolWrite.execute(() -> {
                    try {
                        os.write((content + "\n").getBytes());//同步确认指令
                        os.flush();
                    } catch (IOException e) {
                        handler.sendEmptyMessage(1);
                        e.printStackTrace();
                    }
                });
                break;
        }

    }

    /**
     * 给所有设备发送的指令
     *
     * @param content
     * @param list
     */
    public void writeData(final String content, final List<DeviceBean> list) {

        threadPoolWrite.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < list.size(); i++) {
                        Socket socket = list.get(i).getSocket();
                        OutputStream os = socket.getOutputStream();
                        os.write((content + "\n").getBytes());
                        os.flush();
                    }
                } catch (IOException e) {
                    handler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
            }

        });
    }

    private boolean isHeart = false;
    private List<DeviceBean> list = new ArrayList<>();

    public void setList(List<DeviceBean> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    /**
     * 维持心跳包
     */
    public void heart() {
        threadPoolWrite.execute(new Runnable() {
            @Override
            public void run() {
                int faultPos = -1;
                try {
                    isHeart = true;
                    while (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            faultPos = i;
                            Socket socket = list.get(i).getSocket();
                            if (!socket.isClosed() && !socket.isOutputShutdown()) {
                                OutputStream os = socket.getOutputStream();
                                os.write(("FF" + "\n").getBytes());
                                os.flush();
                            } else {
                                Message msg = Message.obtain();
                                msg.what = InitConst.CODE_HEART;
                                msg.obj = faultPos;
                                handler.sendMessage(msg);
                                continue;
                            }
                        }
                        Thread.sleep(3000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //刷新列表
                    Message msg = Message.obtain();
                    msg.what = InitConst.CODE_HEART;
                    msg.obj = faultPos;
                    handler.sendMessage(msg);
                }
            }

        });
    }
}
