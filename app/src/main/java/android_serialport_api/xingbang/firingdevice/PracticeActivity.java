package android_serialport_api.xingbang.firingdevice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.jilian.SyncActivity;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android_serialport_api.xingbang.Application.getDaoSession;

/**
 * 实验发送命令页面
 */
public class PracticeActivity extends BaseActivity {

    @BindView(R.id.tv_ceshi_dianliu)
    TextView tvCeshiDianliu;
    @BindView(R.id.tv_ceshi_dianya)
    TextView tvCeshiDianya;
    @BindView(R.id.but_pre)
    Button butPre;
    @BindView(R.id.ll_firing_4)
    LinearLayout llFiring4;
    @BindView(R.id.activity_practice)
    ScrollView activityPractice;
    @BindView(R.id.but_write)
    Button butWrite;
    @BindView(R.id.btn_read)
    Button butRead;
    @BindView(R.id.btn_read_log)
    Button butReadLog;
    @BindView(R.id.text_android_ip)
    TextView textAndroidIp;
    @BindView(R.id.text_setvice_ip)
    EditText textSetviceIp;
    @BindView(R.id.but_receive)
    Button butReceive;
    @BindView(R.id.but_lianjie)
    Button butLianjie;
    @BindView(R.id.but_send)
    Button butSend;
    @BindView(R.id.btn_openFile)
    Button btnOpenFile;
    @BindView(R.id.text_filePath)
    TextView textFilePath;
    @BindView(R.id.but_test)
    Button butTest;
    @BindView(R.id.but_version)
    Button butVersion;
    @BindView(R.id.but_sendMsg)
    Button but_sendMsg;

    private DatabaseHelper mMyDatabaseHelper;
    private List<DenatorBaseinfo> list_uid = new ArrayList<>();
    private SQLiteDatabase db;
    private Handler busHandler = null;//总线信息
    private static volatile int stage;
    private volatile int firstCount = 0;
    private From42Power busInfo;
    private volatile int initCloseCmdReFlag = 0;
    private volatile int revCloseCmdReFlag = 0;
    private volatile int revOpenCmdReFlag = 0;
    private volatile int revOpenCmdTestFlag = 0;//收到了打开测试命令
    private Handler handler_zhuce;
    // 写入和读取操作
    private int currentPage = 1;//当前页数
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler_2 = new Handler();//显示进度条

    private static int StringProt = 30000;

    // 存放接收到的文字信息
    private boolean revice_type = true;
    private int denatorCount = 0;//雷管总数
    /**
     * 线程池
     */
    private ExecutorService executorService = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(128));
    private String path;
    private Handler Handler_tip = null;//提示信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        ButterKnife.bind(this);
// 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null,  DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();
        Log.e("本机ip", "ip:: " + getlocalip());
        textAndroidIp.setText("本机IP地址:" + getlocalip());

        initHandle();

        loadMoreData();
//        List<DenatorBaseinfo> denator = LitePal.findAll(DenatorBaseinfo.class);
//        List<MessageBean> message = LitePal.findAll(MessageBean.class);
//        Log.e("注册", "denator: " + denator.toString());
//        Log.e("注册", "message: " + message.toString());
    }

    private void initHandle() {
        handler_zhuce = new Handler(msg -> {

            final String lg = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    // 从客户端接收到消息
//                    runPbDialog();
                    show_Toast("接收成功,正在导入数据,请稍等");
                    new Thread(() -> {
                        String leiguan = Utils.replace(lg);//去除回车
                        Log.e("从客户端收到的雷管", "leiguan: " + leiguan);
                        if (leiguan != null) {
                            // 注册雷管
                            registerDetonator(leiguan);
                        } else {
//                            tipDlg.dismiss();
                            show_Toast("没有接收到数据");
                        }
                    }).start();
                    break;

                case 2:
                    show_Toast(msg.obj.toString());
                    break;

                default:
                    break;
            }


            return false;
        });

        busHandler = new Handler(msg -> {
            if (busInfo != null) {
                BigDecimal b = BigDecimal.valueOf(busInfo.getBusCurrentIa());//处理大额数据专用类
                float dianliu = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                String displayIcStr = dianliu + "μA";// 保留两位小数

                if (dianliu > 4800) {
                    displayIcStr = displayIcStr + "(疑似短路)";
                    tvCeshiDianliu.setTextColor(Color.RED);
                } else if (dianliu < 4800 && dianliu > denatorCount * 24) {
                    displayIcStr = displayIcStr + "(电流过大)";
                    tvCeshiDianliu.setTextColor(Color.RED);
                } else if (dianliu < 8) {
                    displayIcStr = displayIcStr + "(疑似断路)";
                    tvCeshiDianliu.setTextColor(Color.RED);
                } else {
                    tvCeshiDianliu.setTextColor(Color.GREEN);
                }
                tvCeshiDianliu.setText(displayIcStr);
                tvCeshiDianya.setText(busInfo.getBusVoltage() + "V");
            }
            busInfo = null;
            return false;
        });

        mHandler_2 = new Handler(msg -> {
            if (pb_show == 1 && tipDlg != null)
                tipDlg.show();
            if (pb_show == 0 && tipDlg != null)
                tipDlg.dismiss();
            return false;
        });
    }

    private void runPbDialog() {
        pb_show = 1;
        tipDlg = new LoadingDialog(this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
//        divider.setBackgroundColor(Color.TRANSPARENT);
        new Thread(() -> {
            mHandler_2.sendMessage(mHandler_2.obtainMessage());
            try {
                while (pb_show == 1) {
                    Thread.sleep(100);
                }
                mHandler_2.sendMessage(mHandler_2.obtainMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadMoreData() {
        list_uid.clear();

        list_uid = getDaoSession().getDenatorBaseinfoDao().loadAll();
        denatorCount = list_uid.size();
    }

    private void loadMoreData_out() {
        list_uid.clear();
        StringBuilder sb = new StringBuilder();
        list_uid = getDaoSession().getDenatorBaseinfoDao().loadAll();
        for (int i = 0; i < list_uid.size(); i++) {
            sb.append(list_uid.get(i).getShellBlastNo()).append("#").append(list_uid.get(i).getDelay()).append(",");
        }

        Utils.writeLeiGuan(sb.toString());
        show_Toast("写入成功");
    }

    private int registerDetonator(String leiguan) {
        String[] lg = leiguan.split(",");
        String shellNo;
        int maxNo = getMaxNumberNo();
        int reCount = 0;
        Log.e("接收注册", "lg.length: " + lg.length);
        for (int i = lg.length; i > 0; i--) {
            shellNo = lg[i - 1];
            String[] a = shellNo.split("#");
            if (checkRepeatDenatorId(a[0])) {//检查重复数据
                reCount++;
                continue;
            }
            maxNo++;
            DenatorBaseinfo denator = new DenatorBaseinfo();
            denator.setBlastserial(maxNo);
            denator.setSithole(maxNo+"");
            denator.setDenatorId(a[0]);
            if (a.length == 3) {
                denator.setShellBlastNo(a[2]);
            }
            if (a.length == 4) {
                denator.setDuanNo(a[3]);
                denator.setDuan(Integer.parseInt(a[3].substring(0,1)));
            }
            denator.setDelay(Integer.parseInt(a[1]));
            denator.setRegdate(Utils.getDateFormatLong(new Date()));
            denator.setStatusCode("02");
            denator.setStatusName("已注册");
            denator.setErrorCode("FF");
            denator.setErrorName("");
            denator.setWire("");
            Log.e("接收注册", "denator: " + denator.toString());
            getDaoSession().getDenatorBaseinfoDao().insert(denator);
            reCount++;
        }
        pb_show = 0;
        show_Toast_ui("导入成功");
        return reCount;
    }


    /**
     * 读取输入注册
     */
    private void registerDetonator_typeNew(String leiguan) {
        getDaoSession().getDetonatorTypeNewDao().deleteAll();//读取生产数据前先清空旧的数据
        String[] lg = leiguan.split(",");
        String shellNo;
//        int maxNo = getMaxNumberNo();
        for (int i = 0; i < lg.length; i++) {
            shellNo = lg[i];
            String[] a = shellNo.split("#");
            Log.e("注册", "管壳码 a[0]: " + a[0]);
            Log.e("注册", "芯片码 a[1]: " + a[1]);
            Log.e("注册", "a.length: " + a.length);

            // 检查重复数据
            if (checkRepeatShellBlastNo_typeNew(a[0])) {
                continue;
            }
            // 雷管类型_新
            DetonatorTypeNew detonatorTypeNew = new DetonatorTypeNew();
            detonatorTypeNew.setShellBlastNo(a[0]);
            detonatorTypeNew.setDetonatorId(a[1]);
            if (a.length == 3) {//不算从芯片生产数据
                detonatorTypeNew.setZhu_yscs(a[2]);
            } else if (a.length == 5) {
                detonatorTypeNew.setDetonatorIdSup(a[2]);
                detonatorTypeNew.setZhu_yscs(a[3]);
                detonatorTypeNew.setCong_yscs(a[4]);
            }
            getDaoSession().getDetonatorTypeNewDao().insert(detonatorTypeNew);
        }
        pb_show = 0;
        show_Toast_ui("读取成功");
    }


    /**
     * 读取输入注册
     */
    private void registerLog(String logstr) {
        String[] log = logstr.split(",");
        String shellNo;
        Log.e("分析日志", "log: " + log);
        for (int i = 0; i < log.length; i++) {
            shellNo = log[i];
            if (shellNo.length() != 5) {
                String[] ml = shellNo.split(":");
                Log.e("分析日志", "ml: " + ml[2]);
                String cmd = DefCommand.getCmd(ml[2]);//得到 返回命令
                if (cmd != null) {
                    int localSize = ml[2].length() / 2;
                    byte[] localBuf = Utils.hexStringToBytes(ml[2]);//将字符串转化为数组
                    doWithReceivData_fenxi(cmd, localBuf, localSize);
                }
            }

        }
        show_Toast("解析成功");
    }

    //C000120CFF000BE6FF0041A6A2DEFF00028DC0
    //C000310C0BE6FF000A00A2DEFF00E20FC0

    /**
     * 检查重复的数据
     *
     * @param denatorId
     */
    public boolean checkRepeatDenatorId(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> denatorBaseinfo = master.checkRepeatdenatorId(denatorId);
        if (denatorBaseinfo.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查重复的数据
     *
     * @param ShellBlastNo
     */
    public boolean checkRepeatShellBlastNo_typeNew(String ShellBlastNo) {
        GreenDaoMaster master = new GreenDaoMaster();
        DetonatorTypeNew detonatorTypeNew = master.checkRepeat_DetonatorTypeNew(ShellBlastNo);
        if (detonatorTypeNew != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到最大序号
     */
    private int getMaxNumberNo() {
        return LitePal.max(DenatorBaseinfo.class, "blastserial", int.class);
    }




    /**
     * 处理接收到的cmd命令
     */
    private void doWithReceivData_fenxi(String cmd, byte[] cmdBuf, int size) {
        byte[] locatBuf = new byte[size];
        System.arraycopy(cmdBuf, 0, locatBuf, 0, size); // 将cmdBuf数组复制到locatBuf数组

        if ("20".equals(cmd)) {//进入测试模式
        } else if ("40".equals(cmd)) {
            busInfo = FourStatusCmd.decodeFromReceiveDataPower24_1("00", locatBuf);
            Log.e("40命令", "busInfo: " + busInfo.toString());
        } else if ("22".equals(cmd)) { // 关闭测试
        } else if ("13".equals(cmd)) { // 关闭电源
        } else if ("41".equals(cmd)) { // 开启总线电源指令
        }
    }



    @OnClick({R.id.but_pre, R.id.but_jilian, R.id.but_write, R.id.btn_read, R.id.btn_read_log, R.id.but_send, R.id.but_lianjie, R.id.but_receive, R.id.btn_openFile, R.id.but_version, R.id.but_test, R.id.but_sendMsg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.but_jilian://进入级联页面
                Intent intent9 = new Intent(this, SyncActivity.class);//热点方式
                startActivity(intent9);
                finish();
                break;
            case R.id.but_version://进入级联页面
                Intent intent11 = new Intent(this, SystemVersionActivity.class);//版本号
                startActivity(intent11);
                break;
            case R.id.but_sendMsg://进入数据互传页面
                Intent intent12 = new Intent(this, SendMsgActivity.class);//版本号
                startActivity(intent12);
                break;
            case R.id.but_pre://开启测试

                Intent intent2 = new Intent(this, TestICActivity.class);
                startActivity(intent2);
                break;

            case R.id.but_write://写入雷管
                if (currentPage == 1) {
                    loadMoreData_out();
                }
                break;

            // 读取雷管
            case R.id.btn_read:
                pb_show = 1;
                runPbDialog();

                if (TextUtils.isEmpty(path)) {
                    show_Toast("请选择雷管列表文件");
                    return;
                }

                new Thread(() -> {
                    String detonator = Utils.readFile(path);
//                    Log.e("读取到的雷管", "雷管: " + detonator);

                    if (!detonator.equals("0")) {
                        registerDetonator_typeNew(detonator);

                    } else {
                        tipDlg.dismiss();
                        pb_show = 0;
                        show_Toast_ui("当前文件目录里没有 雷管文件.txt");
                    }
                }).start();
                break;
            case R.id.btn_read_log:
                String log = Utils.fenxiLog(path);
                registerLog(log);
                break;
            case R.id.but_send://发送
                StringBuffer sb = new StringBuffer();
                Log.e("发送消息", "list_uid: " + list_uid.size());
                if (list_uid.size() == 0) {
                    show_Toast("获取数据异常");
                    return;
                }
                for (int i = 0; i < list_uid.size(); i++) {
                    if (list_uid.get(i).getShellBlastNo().length() == 13 && list_uid.get(i).getDenatorId().length() > 7) {
                        sb.append(list_uid.get(i).getDenatorId() + "#" + list_uid.get(i).getDelay() + "#" + list_uid.get(i).getShellBlastNo()+ "#" + list_uid.get(i).getDuanNo() + ",");
                    } else {
                        sb.append(list_uid.get(i).getDenatorId() + "#" + list_uid.get(i).getDelay() + ",");
                    }

//                    Log.e("添加信息", "sb: " + (list_uid.get(i).getShellBlastNo() + "#" + list_uid.get(i).getDelay() + ","));
                }
                String ip = textSetviceIp.getText().toString();
                if (TextUtils.isEmpty(ip)) {
                    show_Toast("ip地址异常，请检查网络是否连接");
                    return;
                }
                Log.e("发送消息", "sb: " + sb.toString());
                //与刘鹏飞通讯级联发送
//                strMessage = sb.toString();
//                new Thread(sendThread).start();

                // 启动线程 向服务器发送信息//需要换成服务器端的IP地址
                sendStringMessage(sb.toString(), ip);
//                Utils.sendMessage("F5310000",ip,30000,list_upload_uid);
                break;

            case R.id.but_lianjie:
                if (!isConnect) {
                    new Thread(connectThread).start();
                }
                break;

            case R.id.but_receive://接收
                if (revice_type) {
                    //创建接收文本消息的服务//作为接收端的手机，需要放开。
                    createStringServerSocket();
                    butReceive.setText("正在接收");
                    revice_type = false;
                } else {
                    butReceive.setText("接收数据");
                    revice_type = true;
                }
                break;

            case R.id.btn_openFile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");//txt文件
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;

            case R.id.but_test:
//                startActivity(new Intent(this, TestActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                textFilePath.setText(path);
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                textFilePath.setText(path);
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                textFilePath.setText(path);
                Toast.makeText(this, path + "222222", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 或取本机的ip地址
     */
    private String getlocalip() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0) {
            return "请检查是否连接WIFI";
        }
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }







    /**
     * 创建服务端ServerSocket
     * 接收文本消息
     */
    private void createStringServerSocket() {
        Runnable run = () -> {
            Bundle bundle = new Bundle();
            bundle.clear();
            OutputStream out;
            //给发送端返回一个消息，告诉他链接接收成功。
            String str = "发送成功";
            try {
                ServerSocket serverSocket = new ServerSocket(StringProt);
                while (true) {
                    try {
                        //此处是线程阻塞的,所以需要在子线程中
                        Socket socket = serverSocket.accept();
                        //请求成功，响应客户端的请求
                        out = socket.getOutputStream();
                        out.write(str.getBytes("utf-8"));
                        out.flush();
                        socket.shutdownOutput();
                        //获取输入流,读取客户端发送来的文本消息
                        BufferedReader bf_read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line;
                        StringBuilder buffer = new StringBuilder();
                        while ((line = bf_read.readLine()) != null) {
                            buffer.append(line);
                        }
                        buffer.append("\n");
                        //
                        Log.e("接收消息", "buffer.toString(): " + buffer.toString());
                        Message m = new Message();
                        m.what = 1;
                        m.obj = buffer.toString();
                        handler_zhuce.sendMessage(m);
                        bf_read.close();
                        out.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        };
        executorService.execute(run);
    }

    /**
     * 启动线程 向服务器发送文本消息
     */
    private void sendStringMessage(final String txt1, final String ip) {
        Runnable run = () -> {
            try {
                Socket socket = new Socket();
                //端口号为30000
                socket.connect(new InetSocketAddress(ip, StringProt));
                //获取输出流
                OutputStream ou = socket.getOutputStream();
                //读取服务器响应
                BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                String buffer = "";
                while ((line = bff.readLine()) != null) {
                    buffer = line + buffer;
                }
                //向服务器发送文本信息
                ou.write(txt1.getBytes(StandardCharsets.UTF_8));
                //关闭各种输入输出流
                ou.flush();
//                bff.close();
//                ou.close();
//                socket.close();
                // 服务器返回
                Message message = new Message();
                message.what = 2;
                message.obj = buffer;
                handler_zhuce.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        executorService.execute(run);
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        Utils.saveFile();//把软存中的数据存入磁盘中
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        hideInputKeyboard();
        activityPractice.setFocusable(true);
        activityPractice.setFocusableInTouchMode(true);
        activityPractice.requestFocus();
        activityPractice.findFocus();
        super.onStart();
    }

    //隐藏键盘
    public void hideInputKeyboard() {
        textSetviceIp.clearFocus();//取消焦点
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }



    private Socket socket = null;
    private String strMessage;
    private boolean isConnect = false;
    private OutputStream outStream;
    private boolean isReceive = false;
    private ReceiveThread receiveThread = null;
    Runnable connectThread = () -> {
        // TODO Auto-generated method stub
        try {
            socket = new Socket(textSetviceIp.getText().toString(), 30000);
            isConnect = true;
            isReceive = true;
            receiveThread = new ReceiveThread(socket);
            receiveThread.start();
            System.out.println("----connected success----");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("UnknownHostException-->" + e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("IOException" + e.toString());
        }
    };

    Runnable sendThread = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            byte[] sendBuffer = null;
            try {
                sendBuffer = strMessage.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                outStream = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                outStream.write(sendBuffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private class ReceiveThread extends Thread {
        private InputStream inStream = null;

        private byte[] buffer;
        private String str = null;

        ReceiveThread(Socket socket) {
            try {
                inStream = socket.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (isReceive) {
                buffer = new byte[512];
                try {
                    inStream.read(buffer);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    str = new String(buffer, "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.obj = str;
//                myHandler.sendMessage(msg);
            }
        }
    }

}
