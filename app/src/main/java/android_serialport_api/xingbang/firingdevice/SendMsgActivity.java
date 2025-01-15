package android_serialport_api.xingbang.firingdevice;

import static android_serialport_api.xingbang.Application.getDaoSession;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import android_serialport_api.xingbang.a_new.Constants_SP;
import android_serialport_api.xingbang.a_new.SPUtils;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.PaiDataSelect;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.PaiData;
import android_serialport_api.xingbang.db.QuYu;
import android_serialport_api.xingbang.models.VoBlastModel;
import android_serialport_api.xingbang.utils.AppLogUtils;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendMsgActivity extends BaseActivity {
    @BindView(R.id.text_android_ip)
    TextView textAndroidIp;
    @BindView(R.id.text_filePath)
    TextView textFilePath;
    @BindView(R.id.text_setvice_ip_start)
    TextView textIpStart;
    @BindView(R.id.text_setvice_ip)
    EditText textSetviceIp;
    @BindView(R.id.but_receive)
    Button butReceive;
    @BindView(R.id.but_send)
    Button butSend;


    private Handler handler_zhuce;
    private int pb_show = 0;
    private LoadingDialog tipDlg = null;
    private Handler mHandler_2 = new Handler();//显示进度条
    private From42Power busInfo;
    private List<DenatorBaseinfo> list_uid = new ArrayList<>();
    private int denatorCount = 0;//雷管总数
    /**
     * 线程池
     */
    private ExecutorService executorService = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(128));
    private String path;
    private static int StringProt = 30000;
    private boolean revice_type = true;
    private String mOldTitle;   // 原标题
    private String mRegion;     // 区域
    private Handler mHandler_0 = new Handler();     // UI处理
    private List<DenatorBaseinfo> mListData = new ArrayList<>();
    private GreenDaoMaster master;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);
        ButterKnife.bind(this);
        initHandle();
        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        //获取 区域参数
        master = new GreenDaoMaster();
        mRegion =  String.valueOf(master.getPieceMaxqyid());
        // 原标题
        mOldTitle = getSupportActionBar().getTitle().toString();
        // 设置标题区域
        setTitleRegion(mRegion, -1);
        loadMoreData();

        Log.e("本机ip", "ip:: " + getlocalip());
        textAndroidIp.setText(getString(R.string.text_sendMsg_ip) + getlocalip());
        if (getlocalip().contains(".")) {
            String[] b = getlocalip().split("\\.");
            textIpStart.setText(b[0] + "." + b[1] + "." + b[2] + ".");
        }
    }

    private void initHandle() {
        handler_zhuce = new Handler(msg -> {

            final String lg = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    // 从客户端接收到消息
//                    runPbDialog();
//                    mRegion = (String) SPUtils.get(this, Constants_SP.RegionCode, "1");
                    mRegion =  String.valueOf(master.getPieceMaxqyid());
                    show_Toast(getString(R.string.text_send_tip12));
                    new Thread(() -> {
                        String leiguan = Utils.replace(lg);//去除回车
//                        String leiguan = lg;//去除回车
                        Log.e("从客户端收到的雷管", "leiguan: " + leiguan);
                        Utils.writeRecord("--从客户端收到的雷管:" + leiguan);
                        if (leiguan != null) {
                            // 注册雷管
//                            registerDetonator(leiguan);
                            createQuYu(leiguan);
                        } else {
//                            tipDlg.dismiss();
                            show_Toast(getString(R.string.text_send_tip13));
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

        mHandler_2 = new Handler(msg -> {
            if (pb_show == 1 && tipDlg != null)
                tipDlg.show();
            if (pb_show == 0 && tipDlg != null)
                tipDlg.dismiss();
            return false;
        });

        mHandler_0 = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1001:
                    Log.e("1001", "更新视图 区域" + mRegion);
                    loadMoreData();
                    // 查询全部雷管 倒叙(序号)
                    mListData = master.queryDetonatorRegionDesc(mRegion);
                    // 设置标题区域
                    setTitleRegion(mRegion, mListData.size());
                    break;
                case 1002:
                    // 查询全部雷管 倒叙(序号)
                    mRegion =  String.valueOf(master.getPieceMaxqyid());
                    Log.e("1002", "更新视图 区域" + mRegion);
                    mListData = master.queryDetonatorRegionDesc(mRegion);
                    // 设置标题区域
                    Log.e("1002", "更新视图 mListData.size()" + mListData.size());
                    String str = getResources().getString(R.string.text_list_piace) + mRegion + "(" + getResources().getString(R.string.text_main_sl) + ": " + mListData.size() + ")";

                    setTitleRegion(mRegion, mListData.size());
                    show_Toast(getString(R.string.text_sendMsg_dr) + msg.arg1+ getString(R.string.text_sendMsg_lgcg));
                    break;
                case 1:
                    show_Toast(msg.obj.toString());
                    break;
                default:
                    break;
            }

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

    //获取雷管
    private void loadMoreData() {
        list_uid.clear();
        list_uid = master.queryDetonatorRegionDesc(mRegion);
        denatorCount = list_uid.size();
    }

    //导出数据
    private void loadMoreData_out() {
        list_uid.clear();
        StringBuilder sb = new StringBuilder();
        list_uid = getDaoSession().getDenatorBaseinfoDao().loadAll();
        for (int i = 0; i < list_uid.size(); i++) {
            sb.append(list_uid.get(i).getShellBlastNo()).append("#").append(list_uid.get(i).getDenatorId()).append("#").append(list_uid.get(i).getZhu_yscs()).append(",");
        }

        Utils.writeLeiGuan(sb.toString());
        show_Toast(getString(R.string.text_send_tip17));
    }

    /**
     * 得到最大序号
     */
    private int getMaxNumberNo() {
        return LitePal.max(DenatorBaseinfo.class, "blastserial", int.class);
    }

    /**
     * 检查重复的芯片码
     *
     * @param denatorId
     */
    public boolean checkRepeatDenatorId(String denatorId) {
        List<DenatorBaseinfo> denatorBaseinfo = master.checkRepeatdenatorId(denatorId);
        Log.e("检查重复的芯片码", "数量: "+denatorBaseinfo.size() );
        if (denatorBaseinfo.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 接收数据需要作区分：
     * 1.老版本没有的字段，接收到数据后，新建区域直接放第一排就行
     * 2.新版本所有字段都有，发送时候需要发送上新加的字段，接收也要处理下
     */
    private List<QuYu> mQyListData = new ArrayList<>();
    private void createQuYu(String leiguan) {
        mQyListData = master.queryQuYu();
        int maxNo = master.getPieceMaxqyid();
        QuYu quYu = new QuYu();
        quYu.setName((maxNo + 1) + "");
        quYu.setQyid((maxNo + 1));
        quYu.setStartDelay("0");
        quYu.setKongDelay("0");
        quYu.setPaiDelay("0");
        if (mQyListData.size() < 1) {
            quYu.setSelected("true");
        } else {
            quYu.setSelected("false");
        }
        getDaoSession().getQuYuDao().insert(quYu);
        String qyId = String.valueOf(maxNo + 1);
        Log.e(TAG,"创建区域:" + qyId);
        createPai(leiguan,qyId);
    }

    private void createPai(String leiguan,String qyId) {
        int maxPai = master.getMaxPaiId(qyId);
        Log.e(TAG,"创建排:" + maxPai);
        PaiData paiData = new PaiData();
        paiData.setPaiId((maxPai + 1));
        paiData.setQyid(Integer.parseInt(qyId));
        paiData.setStartDelay("0");
        paiData.setKongNum(1);
        paiData.setKongDelay("0");
        paiData.setNeiDelay("0");
        paiData.setDiJian(false);
        paiData.setDelayMin("0");
        paiData.setDelayMax("0");
        paiData.setSum("0");
        getDaoSession().getPaiDataDao().insert(paiData);
        insertDenatorNew(leiguan,qyId);
    }

    private String TAG = "数据互传页面";
    private List<PaiDataSelect> groupList = new ArrayList<>();
    private int insertDenatorNew(String leiguan,String qyId) {
        Log.e(TAG,"注册雷管了--qyId:" + qyId);
        mQyListData = master.queryQuYu();
        groupList = master.queryPaiSelect(qyId);
        int paiChoice = 1;//默认直接注册雷管进第1排
        Log.e(TAG, "雷管注册");
        PaiData paiData = groupList.get(0);
        Log.e(TAG, "雷管注册paiData.getKongDelay: "+paiData.getKongDelay() );
        Log.e(TAG, "雷管注册paiData.getStartDelay: "+paiData.getStartDelay() );
        AppLogUtils.writeAppLog("--数据互传雷管注册");
        //第一种 4条
        //第二种 2条
        String[] lg = leiguan.split(",");
        Log.e(TAG,"雷管txt个数:" + lg.length);
        String shellNo;
        int maxNo = master.getPieceMaxNum(qyId);
        int maxKong = 0;
        Log.e(TAG,"maxKong:" + maxKong);
        //获取该区域 最大序号的延时
        int delay_max = master.getPieceAndPaiMaxDelay(qyId, paiChoice);

        int reCount = 0;
        for (int i = lg.length; i > 0; i--) {
            shellNo = lg[i - 1];
            String[] a = shellNo.split("#");
            Log.e("分割", "a.length: " + a.length);
            Log.e("分割", "a[3]" + a[3]);
            String[] duan = a[3].split("-");
            int duanNo = 0;
            if(!a[3].contains("-")){
                duan[0]="1";
                duanNo= master.getPieceMaxDuanNo(Integer.parseInt(duan[0]), qyId);//获取该区域 最大序号的延时;
            }
            Log.e(TAG,"duanNo:" + duanNo);
            if (!a[0].equals("无") && checkRepeatDenatorId(a[0])) {//检查重复数据
                reCount++;
                continue;
            }
            if (checkRepeatShellNo(a[2])) {//检查重复数据
                reCount++;
                continue;
            }
            if(a[0].equals("无")){
                a[0]="";
            }
            maxKong++;
            DenatorBaseinfo denator = new DenatorBaseinfo();
            denator.setBlastserial(maxKong);
            denator.setSithole(maxKong + "");
            denator.setDenatorId(a[0]);
            denator.setShellBlastNo(a[2]);
            denator.setDuan(Integer.parseInt(duan[0]));
            if(!a[3].contains("-")){
                denator.setDuanNo((duanNo + 1));
                Log.e(TAG,"不包含-:" + (duanNo + 1));
            }else {
                denator.setDuanNo(Integer.parseInt(duan[1]));
                Log.e(TAG,"包含-:1");
            }
            denator.setDelay(Integer.parseInt(a[1]));
            denator.setRegdate(Utils.getDateFormat(new Date()));
            denator.setStatusCode("02");
            denator.setStatusName("已注册");
            denator.setErrorCode("00");
            denator.setErrorName("");
            denator.setWire("");
            denator.setPiece(qyId);
            denator.setPai(paiChoice + "");
            denator.setAuthorization("1");
            if(a.length==4){
                denator.setZhu_yscs(a[3]);
            }else {
                denator.setZhu_yscs(a[4]);
            }
            getDaoSession().getDenatorBaseinfoDao().insert(denator);
            reCount++;
        }
        pb_show = 0;
        Message msg =new Message();
        msg.what=1002;
        msg.arg1= lg.length;
        mHandler_0.sendMessage(msg);
        updataPaiData();
        return reCount;
    }

    private void updataPaiData() {
        GreenDaoMaster master = new GreenDaoMaster();
        int total = master.queryDetonatorPaiSize(mRegion, 1 + "");//有过
        int delay_max_new = new GreenDaoMaster().getPieceAndPaiMaxDelay(mRegion, 1);//获取该区域 最大序号的延时
        int delay_minNum_new = new GreenDaoMaster().getPieceAndPaiMinDelay(mRegion, 1);
        Log.e(TAG, "updataPaiData  total: " + total);
        PaiData choicepaiData = GreenDaoMaster.gePaiData(mRegion, 1 + "");
        if(choicepaiData!=null){
            choicepaiData.setSum(total + "");//
            choicepaiData.setDelayMin(delay_minNum_new + "");
            choicepaiData.setDelayMax(delay_max_new + "");
            getDaoSession().getPaiDataDao().update(choicepaiData);
        }
    }

    /**
     * 注册雷管 数据互传接收方法
     */
    private int registerDetonator(String leiguan) {
        //第一种 4条
        //第二种 2条
        mRegion =  String.valueOf(master.getPieceMaxqyid());
        String[] lg = leiguan.split(",");
        String shellNo;
        int maxNo = master.getPieceMaxNum(mRegion);
        int reCount = 0;
        for (int i = lg.length; i > 0; i--) {
            shellNo = lg[i - 1];
            String[] a = shellNo.split("#");
            Log.e("分割", "a.length: " + a.length);
            Log.e("分割", "a[3]" + a[3]);
            String[] duan = a[3].split("-");
            int duanNo = 0;
            if(!a[3].contains("-")){
                duan[0]="1";
                duanNo= master.getPieceMaxDuanNo(Integer.parseInt(duan[0]), mRegion);//获取该区域 最大序号的延时;
            }
            if (!a[0].equals("无") && checkRepeatDenatorId(a[0])) {//检查重复数据
                reCount++;
                continue;
            }
            if (checkRepeatShellNo(a[2])) {//检查重复数据
                reCount++;
                continue;
            }
            if(a[0].equals("无")){
                a[0]="";
            }
            maxNo++;
            DenatorBaseinfo denator = new DenatorBaseinfo();
            denator.setBlastserial(maxNo);
            denator.setSithole(maxNo + "");
            denator.setDenatorId(a[0]);
            denator.setShellBlastNo(a[2]);
            denator.setDuan(Integer.parseInt(duan[0]));

            if(!a[3].contains("-")){
                denator.setDuanNo((duanNo + 1));
            }else {
                denator.setDuanNo(Integer.parseInt(duan[1]));
            }
            denator.setDelay(Integer.parseInt(a[1]));
            denator.setRegdate(Utils.getDateFormat(new Date()));
            denator.setStatusCode("02");
            denator.setStatusName("已注册");
            denator.setErrorCode("00");
            denator.setErrorName("");
            denator.setWire("");
            denator.setPiece(mRegion);
            if(a.length==4){
                denator.setZhu_yscs(a[3]);
            }else {
                denator.setZhu_yscs(a[4]);
            }


            getDaoSession().getDenatorBaseinfoDao().insert(denator);
            reCount++;
        }
        pb_show = 0;
        Message msg =new Message();
        msg.what=1002;
        msg.arg1= lg.length;
        mHandler_0.sendMessage(msg);
        return reCount;
    }


    /**
     * 检查重复的管壳码
     * @param shellNo
     * @return
     */
    public boolean checkRepeatShellNo(String shellNo) {
        List<DenatorBaseinfo> list_lg = master.checkRepeatShellNo(shellNo);
        Log.e("检查重复的管壳码", "重复数量: "+list_lg.size() );
        return list_lg.size() > 0;
    }

    @OnClick({R.id.but_write, R.id.btn_read, R.id.btn_read_log, R.id.but_send, R.id.but_lianjie, R.id.but_receive, R.id.btn_openFile})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.but_write://写入雷管
                loadMoreData_out();
                break;

            // 读取雷管
            case R.id.btn_read:
                pb_show = 1;
                runPbDialog();

                if (TextUtils.isEmpty(path)) {
                    show_Toast(getString(R.string.text_practice_tip4));
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
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = getString(R.string.text_practice_tip5);
                        mHandler_0.sendMessage(msg);
                    }
                }).start();
                break;
            case R.id.btn_read_log:
//                String log = Utils.fenxiLog(path);
//
//                registerLog(log);
                readCVS();
                break;
            case R.id.but_send://数据互传 发送
                AlertDialog dialog = new AlertDialog.Builder(SendMsgActivity.this)
                        .setTitle(getString(R.string.text_practice_tip12))//设置对话框的标题//"成功起爆"
                        .setMessage(getString(R.string.text_practice_tip13))//设置对话框的内容"本次任务成功起爆！"
                        //设置对话框的按钮
                        .setNegativeButton(getString(R.string.text_alert_sure), (dialog13, which) -> {
                            StringBuffer sb = new StringBuffer();
                            Log.e("发送消息", "list_uid: " + list_uid.size());
                            if (list_uid.size() == 0) {
                                show_Toast(getString(R.string.text_practice_tip6));
                                return;
                            }
                            for (int i = 0; i < list_uid.size(); i++) {//芯片码#延时#管壳码#段位-段号#延时参数
//                    if (list_uid.get(i).getShellBlastNo().length() == 13 && list_uid.get(i).getDenatorId() !=null) {
                                sb.append((list_uid.get(i).getDenatorId() + "").replace("null", "无") + "#" + list_uid.get(i).getDelay() + "#" + list_uid.get(i).getShellBlastNo() + "#" +list_uid.get(i).getDuan()+"-"+ list_uid.get(i).getDuanNo() + "#" + (list_uid.get(i).getZhu_yscs() + "").replace("null", "无") + ",");
//                    } else {
//                        sb.append(list_uid.get(i).getShellBlastNo() + "#" + list_uid.get(i).getDelay() + ",");
//                    }

                            }
                            String ip = textIpStart.getText().toString() + textSetviceIp.getText().toString();
                            if (TextUtils.isEmpty(ip)) {
                                show_Toast(getString(R.string.text_practice_tip7));
                                return;
                            }
                            Log.e("发送消息", "sb: " + sb.toString());
                            Utils.writeRecord("--发送数据:" + sb.toString());
                            //与刘鹏飞通讯级联发送
//                strMessage = sb.toString();
//                new Thread(sendThread).start();

                            // 启动线程 向服务器发送信息//需要换成服务器端的IP地址
                            sendStringMessage(sb.toString(), ip);
//                Utils.sendMessage("F5310000",ip,30000,list_upload_uid);
                            hideInputKeyboard();
                        })
                        .setNeutralButton(getString(R.string.text_alert_cancel), (dialog2, which) -> {
                            dialog2.dismiss();
                        })
                        .create();
                dialog.show();

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
                    butReceive.setText(getString(R.string.text_practice_tip8));
                    revice_type = false;
                } else {
                    butReceive.setText(getString(R.string.text_practice_tip9));
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
                show_Toast(path + "11111");
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                textFilePath.setText(path);
                show_Toast(path);

            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                textFilePath.setText(path);
                show_Toast(path + "222222");
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
            return getString(R.string.text_practice_tip11);
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
            String str = getString(R.string.text_sync_tip14);
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
//                        Log.e("接收消息", "buffer.toString(): " + buffer.toString());
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
                ou.flush();//刷新
                //关闭()
                bff.close();
                ou.close();
                socket.close();
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
    protected void onStart() {
        hideInputKeyboard();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (executorService != null && (!executorService.isShutdown())) {
            executorService.shutdown();
//            executorService.shutdownNow();
        }
        super.onDestroy();
    }

    //隐藏键盘
    public void hideInputKeyboard() {
        textSetviceIp.clearFocus();//取消焦点
        textIpStart.clearFocus();//取消焦点
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
            Log.e("打开线程", "----打开socket成功----");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("线程报错", "----UnknownHostException----" + e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("线程报错", "----IOException----" + e.toString());
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
        show_Toast(getString(R.string.text_send_tip27));
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


    /**
     * 读取输入注册
     */
    private void registerDetonator_typeNew(String leiguan) {
        String time = Utils.getDateFormat(new Date());
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
            detonatorTypeNew.setTime(time);
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

        Message msg = new Message();
        msg.what = 1;
        msg.obj = getString(R.string.text_send_tip15);
        mHandler_0.sendMessage(msg);
    }

    /**
     * 检查重复的数据
     *
     * @param ShellBlastNo
     */
    public boolean checkRepeatShellBlastNo_typeNew(String ShellBlastNo) {
        DetonatorTypeNew detonatorTypeNew = master.checkRepeat_DetonatorTypeNew(ShellBlastNo);
        if (detonatorTypeNew != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建菜单
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    /**
     * 打开菜单
     */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);
//    }

    /**
     * 点击item
     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        mRegion = String.valueOf(item.getOrder());
//        switch (item.getItemId()) {
//
//            case R.id.item_1:
//            case R.id.item_2:
//            case R.id.item_3:
//            case R.id.item_4:
//            case R.id.item_5:
//                // 区域 更新视图
//                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
//                // 显示提示
//                show_Toast(getString(R.string.text_show_1) + mRegion);
//                // 延时选择重置
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

    /**
     * 设置标题区域
     */
    private void setTitleRegion(String region, int size) {
        Log.e("1002", "更新视图 size" + size);
        String str;
        if (Integer.parseInt(region) < 1) {
            str = "";
        } else {
            str = getResources().getString(R.string.text_list_piace) + region;
        }
//        if (size == -1) {
//            str = " 区域" + region;
//        } else {
//            str = " 区域" + region + "(数量: " + size + ")";
//        }
        Log.e("1002", "更新视图 str" + str);
        // 设置标题
//        getSupportActionBar().setTitle(mOldTitle + str);
        getSupportActionBar().setTitle(mOldTitle + str);
        // 保存区域参数
        SPUtils.put(this, Constants_SP.RegionCode, region);

        Log.e("liyi_Region", "已选择" + str);
    }

    private void readCVS() {
        int i = 0;
        String path = Environment.getExternalStorageDirectory() + "/xb/" + "list_data2.csv";
        File f = new File(path);

        if (!f.exists()) {
            return;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
//                if (i == 0) {//去掉第一行文字表头
//                    i = 1;
//                    continue;
//                }
                String a[] = line.split(",", -1);
                //,,,5620811H08989,085060B804,,,,,,,,,,,,,,
                Log.e("写入文件数据", a[3] + "--" + a[4]);
                String uid = "A62F400" + a[4].substring(0, 6);
                String yscs = a[4].substring(6);
                DenatorBaseinfo baseinfo = new DenatorBaseinfo();
                baseinfo.setShellBlastNo(a[3]);
                baseinfo.setDenatorId(uid);
                baseinfo.setZhu_yscs(yscs);
                baseinfo.setPiece("1");
                getDaoSession().getDenatorBaseinfoDao().insert(baseinfo);

                i++;
            }
        } catch (FileNotFoundException e) {
            Log.e("读取备份", "readCVS: 1");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("读取备份", "readCVS: 2");
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("读取备份", "readCVS: 3");
            }
        }
    }

}