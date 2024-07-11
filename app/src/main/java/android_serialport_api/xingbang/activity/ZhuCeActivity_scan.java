package android_serialport_api.xingbang.activity;

import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kfree.comm.system.ScanQrControl;
import com.orhanobut.logger.Logger;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.custom.ZhuCeScanAdapter;
import android_serialport_api.xingbang.databinding.ActivityZhuCeScanBinding;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.Defactory;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.greenDao.DenatorBaseinfoDao;
import android_serialport_api.xingbang.db.greenDao.DenatorHis_DetailDao;
import android_serialport_api.xingbang.firingdevice.ReisterMainPage_scan;
import android_serialport_api.xingbang.models.ZhuCeListBean;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;

public class ZhuCeActivity_scan extends SerialPortActivity implements View.OnClickListener {
    ActivityZhuCeScanBinding binding;
    String qsys;
    String pjys;
    String kjys;
    String knys;
    String f1ys;
    String f2ys;
    private List<DenatorBaseinfo> mListData = new ArrayList<>();//所有雷管列表
    private String TAG = "注册页面";
    private List<ZhuCeListBean> groupList;
    private List<List<DenatorBaseinfo>> childList;
    private ZhuCeScanAdapter demoAdapter;
    private int paiMax = 0;
    private int paiChoice = 1;
    private ScanBar scanBarThread;
    private ScanInterface scanDecode;
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private String delay_set = "0";//是f1还是f2
    private int maxSecond = 0;//最大秒数
    private String factoryCode = "";//厂家代码
    private String factoryFeature = "";////厂家特征码
    private String deTypeSecond = "";//该类型雷管最大延期值
    private int isCorrectReisterFea = 0; //是否正确的管厂码
    private Handler mHandler_tip = new Handler();//错误提示
    private Handler mHandler_1 = new Handler();//提示电源信息
    private Handler mHandler_2 = new Handler();//显示进度条
    private static int tipInfoFlag = 0;
    private String lg_No;//单发注册
    private String singleShellNo;//单发注册
    private ProgressDialog builder = null;
    private LoadingDialog tipDlg = null;
    private int pb_show = 0;
    private ScanQrControl mScaner = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_zhu_ce_scan);
        binding = ActivityZhuCeScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SoundPlayUtils.init(this);
        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, DatabaseHelper.TABLE_VERSION);
        db = mMyDatabaseHelper.getReadableDatabase();

        TextView title = findViewById(R.id.title_text);
        title.setText("雷管注册");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setOnClickListener(v -> startActivity(new Intent(ZhuCeActivity_scan.this, UpdataDelayActivity.class)));
        iv_back.setOnClickListener(v -> finish());


        mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
        binding.zcTxtTotal.setText("共:" + mListData.size() + "发雷管");
        groupList = new ArrayList<>();
        childList = new ArrayList<>();
        paiMax = LitePal.max(DenatorBaseinfo.class, "pai", int.class);

        GreenDaoMaster master = new GreenDaoMaster();
        for (int i = 1; i <= paiMax; i++) {
            List<DenatorBaseinfo> list_pai = master.queryDetonatorPai(i);
            Logger.e("list_pai: " + list_pai);
            ZhuCeListBean zhuCeListBean = new ZhuCeListBean();
            zhuCeListBean.setPai(i);
            zhuCeListBean.setStartDelay(list_pai.get(0).getDelay());
            zhuCeListBean.setTotal(list_pai.size());
            groupList.add(zhuCeListBean);
            childList.add(list_pai);
        }

        demoAdapter = new ZhuCeScanAdapter(groupList, childList);
        binding.zcList.setAdapter(demoAdapter);
        //一级点击监听
        binding.zcList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            //如果你处理了并且消费了点击返回true,这是一个基本的防止onTouch事件向下或者向上传递的返回机制
//            v.setBackgroundColor(Color.GREEN);

            demoAdapter.setSelcetPosition(groupPosition, 0);
            paiChoice = groupPosition + 1;
            Logger.e("1级监听-id:" + id + " -groupPosition:" + groupPosition+"-paiChoice:"+paiChoice);
            return false;
        });
        //二级点击监听
        binding.zcList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Logger.e("2级监听");
            //如果你处理了并且消费了点击返回true
            return false;
        });
        //默认展开
        int groupCount = binding.zcList.getCount();
        for (int i=0; i<groupCount; i++) {
            binding.zcList.expandGroup(i);

        }

//        getDate();
        scan();//扫描初始化
        getFactoryType();//获取延期最大值
        getFactoryCode();//获取厂家码
        mHandler_2 = new Handler(message -> {
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });
        mHandler_tip = new Handler(msg -> {
            if (isCorrectReisterFea == 1) {
                SoundPlayUtils.play(4);
                show_Toast(getResources().getString(R.string.text_error_tip1));
                //"雷管信息有误，管厂码不正确，请检查"
            } else if (isCorrectReisterFea == 2) {
                SoundPlayUtils.play(4);
                show_Toast(getResources().getString(R.string.text_error_tip2));
            } else if (isCorrectReisterFea == 3) {
                SoundPlayUtils.play(4);
                show_Toast("已达到最大延时限制" + maxSecond + "ms");
            } else if (isCorrectReisterFea == 4) {
                SoundPlayUtils.play(4);
                show_Toast("与第" + lg_No + "发" + singleShellNo + "重复");
                int total = showDenatorSum();
//                reisterListView.setSelection(total - Integer.parseInt(lg_No));
            } else if (isCorrectReisterFea == 6) {
                SoundPlayUtils.play(4);
                show_Toast("当前管壳码超出13位,请检查雷管或系统版本是否符合后,再次注册");
            } else if (isCorrectReisterFea == 7) {
                SoundPlayUtils.play(4);
                show_Toast_long("与第" + lg_No + "发" + singleShellNo + "重复");
            } else if (isCorrectReisterFea == 8) {
                SoundPlayUtils.play(4);
                show_Toast("有延时为空,请先设置延时");
            } else if (isCorrectReisterFea == 9) {
                decodeBar(msg.obj.toString());
            } else {
                SoundPlayUtils.play(4);
                show_Toast("注册失败");
            }
            isCorrectReisterFea = 0;
            return false;
        });

        mHandler_1 = new Handler(msg -> {
            if (tipInfoFlag == 2) {//提示已注册多少发
                showDenatorSum();
            }
            if (tipInfoFlag == 3) {//未收到关闭电源命令
                show_Toast(getResources().getString(R.string.text_error_tip5));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 4) {//未收到打开电源命令
                show_Toast(getResources().getString(R.string.text_error_tip6));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 5) {//桥丝不正常
                show_Toast(getResources().getString(R.string.text_error_tip7));
                SoundPlayUtils.play(4);
            }
            if (tipInfoFlag == 88) {//刷新界面
                showDenatorSum();
                initList();
            }
            if (tipInfoFlag == 89) {//刷新界面
                show_Toast("输入的管壳码重复");
                showDenatorSum();
            }
            return false;
        });


        demoAdapter.setSelcetPosition(paiChoice-1, 0);
    }

    private void initList(){
        paiMax = LitePal.max(DenatorBaseinfo.class, "pai", int.class);
        groupList.clear();
        childList.clear();
        GreenDaoMaster master = new GreenDaoMaster();
        for (int i = 1; i <= paiMax; i++) {
            List<DenatorBaseinfo> list_pai = master.queryDetonatorPai(i);
            Logger.e("list_pai: " + list_pai);
            ZhuCeListBean zhuCeListBean = new ZhuCeListBean();
            zhuCeListBean.setPai(i);
            zhuCeListBean.setStartDelay(list_pai.get(0).getDelay());
            zhuCeListBean.setTotal(list_pai.size());
            groupList.add(zhuCeListBean);
            childList.add(list_pai);
        }
        demoAdapter = new ZhuCeScanAdapter(groupList, childList);
        binding.zcList.setAdapter(demoAdapter);
        //默认展开
        int groupCount = binding.zcList.getCount();
        for (int i=0; i<groupCount; i++) {
            binding.zcList.expandGroup(i);

        }
        demoAdapter.setSelcetPosition(paiChoice-1, 0);
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {

    }

    private void getDate() {
        qsys = (String) MmkvUtils.getcode("qsys", "0");
        pjys = (String) MmkvUtils.getcode("pjys", "0");
        kjys = (String) MmkvUtils.getcode("kjys", "0");
        knys = (String) MmkvUtils.getcode("knys", "0");
        f1ys = (String) MmkvUtils.getcode("f1ys", "0");
        f2ys = (String) MmkvUtils.getcode("f2ys", "0");
        binding.zcBtnF1.setText("F1:+" + f1ys + "ms");//F1:+20ms
        binding.zcBtnF2.setText("F2:+" + f2ys + "ms");//F1:+20ms
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.zc_btn_f1) {
            delay_set="f1";
            binding.zcBtnF1.setBackgroundColor(Color.GREEN);
            binding.zcBtnF2.setBackgroundColor(Color.GRAY);
        } else if (v.getId() == R.id.zc_btn_f2) {
            delay_set="f2";
            binding.zcBtnF1.setBackgroundColor(Color.GRAY);
            binding.zcBtnF2.setBackgroundColor(Color.GREEN);
        } else if (v.getId() == R.id.zc_btn_addPai) {
            Logger.e("点击方法");
            paiMax = paiMax + 1;
            List<DenatorBaseinfo> list_pai2 = new ArrayList<>();
            ZhuCeListBean zhuCeListBean = new ZhuCeListBean();
            zhuCeListBean.setPai(paiMax);
            zhuCeListBean.setStartDelay(0);
            zhuCeListBean.setTotal(0);
            groupList.add(zhuCeListBean);
            childList.add(list_pai2);

            demoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDate();
    }

    /**
     * 扫码注册方法/扫描头返回方法
     */
    private void scan() {
        switch (Build.DEVICE) {
            // KT50 起爆器设备
            case "M900": {
                // 创建扫描头操作对象，并注册回调
                mScaner = new ScanQrControl(this);
                mScaner.registerScanCb(new ScanQrControl.IScan() {
                    @Override
                    public void onScanStart(int timeoutSec) {
                    }

                    @Override
                    public void onScanResult(boolean isSuccess, String scanResultStr) {
                        Log.e( "扫码结果: ", "ScanResult:" + isSuccess + "|" + scanResultStr);
                        saoma(scanResultStr);
                    }
                });
                break;
            }
            default:{
                scanDecode = new ScanDecode(this);
                scanDecode.initService("true");//初始化扫描服务

                scanDecode.getBarCode(data -> {
                    saoma(data);
                });
            }

        }



//        scanDecode = new ScanDecode(this);
//        scanDecode.initService("true");//初始化扫描服务
//
//        scanDecode.getBarCode(data -> {
//            Log.e("扫码", "data: " + data);
////            if (deleteList()) return;
//            hideInputKeyboard();//隐藏光标
//            //根据二维码长度判断新旧版本,兼容01一代,02二代芯片
//            if (data.length() == 13) {
//                updateMessage("01");
//            } else if (data.length() == 28) {//P53904180500005390418050000
//                updateMessage("02");
//            } else if (data.length() == 30) {//5620302H00001A62F400FFF20AB603
//                updateMessage("02");
//            }
//            if (data.length() == 19) {//扫描箱号
//                addXiangHao(data);
//            }
//            if (sanButtonFlag > 0) {//扫码结果设置到输入框里
//                Log.e("扫码注册", "data: " + data);
//                decodeBar(data);
//                Message msg = new Message();
//                msg.obj = data;
//                msg.what = 9;
//                mHandler_tip.sendMessage(msg);
//                scanDecode.stopScan();
//            } else {
//                String barCode;
//                String denatorId;
//                if (data.length() == 28) {
//                    //Y5620413H00009A630FD74D87604()
//                    //5620722H12345+000ABCDEF+B603+0+1  13 22 26 27 28
//                    Log.e("扫码", "data: " + data);
//                    //5620302H00001A62F400FFF20AB603
//                    //5420302H00001A6F4FFF20AB603
//                    //Y5620413H00009A630FD74D87604
////                    barCode = data.substring(1, 14);
////                    String a = data.substring(13, 22);
////                    denatorId = a.substring(0, 2) + "2" + a.substring(2, 4) + "00" + a.substring(4);
//
//                    //内蒙版
//                    barCode = data.substring(0, 13);
//                    denatorId = "A621" + data.substring(13, 22);
//                    String yscs = data.substring(22, 26);
//                    String version = data.substring(26, 27);
//                    String duan = data.substring(27, 28);
//
//                    insertSingleDenator_2(barCode, denatorId, yscs, version, duan);//同时注册管壳码和芯片码
//                } else if (data.length() == 13) {
//                    barCode = getContinueScanBlastNo(data);//VR:1;SC:5600508H09974;
//                    insertSingleDenator(barCode);
//                }
//                hideInputKeyboard();//隐藏光标
//            }
//        });
    }
    private void saoma(String data) {
        Log.e("扫码", "data: " + data);
//            if (deleteList()) return;
        hideInputKeyboard();//隐藏光标
        //根据二维码长度判断新旧版本,兼容01一代,02二代芯片
        if (data.length() == 13) {
            updateMessage("01");
        } else if (data.length() == 28) {//P53904180500005390418050000
            updateMessage("02");
        } else if (data.length() == 30) {//5620302H00001A62F400FFF20AB603
            updateMessage("02");
        }
        if (data.length() == 19) {//扫描箱号
            addXiangHao(data);
        }
//        if (sanButtonFlag > 0) {//扫码结果设置到输入框里
//            Log.e("扫码注册", "data: " + data);
//            decodeBar(data);
//            Message msg = new Message();
//            msg.obj = data;
//            msg.what = 9;
//            mHandler_tip.sendMessage(msg);
////            scanDecode.stopScan();
//        } else {
            String barCode;
            String denatorId;
            if (data.length() == 28) {
                //Y5620413H00009A630FD74D87604()
                //5620722H12345+000ABCDEF+B603+0+1  13 22 26 27 28
                Log.e("扫码", "data: " + data);
                //5620302H00001A62F400FFF20AB603
                //5420302H00001A6F4FFF20AB603
                //Y5620413H00009A630FD74D87604
//                    barCode = data.substring(1, 14);
//                    String a = data.substring(13, 22);
//                    denatorId = a.substring(0, 2) + "2" + a.substring(2, 4) + "00" + a.substring(4);
                if (data.charAt(0) == 'Y') {
                    barCode = data.substring(1, 14);
                    String a = data.substring(14, 24);
                    denatorId = a.substring(0, 2) + "2" + a.substring(2, 4) + "00" + a.substring(4);
                    String yscs =data.substring(24);
                    Log.e("扫码", "barCode: " + barCode);
                    Log.e("扫码", "denatorId: " + denatorId);
                    Log.e("扫码", "yscs: " +yscs);
                    insertSingleDenator_2(barCode, denatorId, yscs,"1", "0");//因为四川二维码不带段位和版本号,所以写个固定的
                }else {//其他规则
                    //内蒙版
                    barCode = data.substring(0, 13);
                    denatorId = "A621" + data.substring(13, 22);
                    String yscs = data.substring(22, 26);
                    String version = data.substring(26, 27);
                    String duan = data.substring(27, 28);

                    insertSingleDenator_2(barCode, denatorId, yscs, version, duan);//同时注册管壳码和芯片码
                }

            } else if (data.length() == 13) {
                barCode = getContinueScanBlastNo(data);//VR:1;SC:5600508H09974;
                insertSingleDenator(barCode);
            }
            hideInputKeyboard();//隐藏光标
//        }
    }

    private class ScanBar extends Thread {
        public volatile boolean exit = false;

        public void run() {
            int zeroCount = 0;

            while (!exit) {
                try {
                    scanDecode.starScan();
                    Thread.sleep(1250);
                    //break;

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 判断列表第一发是否在历史记录里
     */
    private boolean deleteList() {
        String shellBlastNo = serchFristLG();//获取第一发雷管
        int no = serchFristLGINdenatorHis(shellBlastNo);
        if (no > 0) {
            showAlertDialog();
            scanDecode.stopScan();
            return true;
        }
        return false;
    }
    /**
     * 获取第一发雷管
     */
    private String serchFristLG() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryDenatorBaseinfo();
        if (list.size() > 0) {
            return list.get(0).getShellBlastNo();
        } else {
            return "";
        }
    }

    /**
     * 注册列表的第一发是否在历史列表里
     */
    private int serchFristLGINdenatorHis(String no) {
        if (no.length() > 12) {
            return getDaoSession().getDenatorHis_DetailDao().queryBuilder().where(DenatorHis_DetailDao.Properties.ShellBlastNo.eq(no)).list().size();
        } else {
            return 0;
        }
    }

    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("注册提示")//设置对话框的标题//"成功起爆"
                .setMessage("注册列表中的雷管已在起爆历史记录里,是否清空列表")//设置对话框的内容"本次任务成功起爆！"
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确认清空", (dialog1, which) -> {
                    db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, null);
                    dialog1.dismiss();
                    demoAdapter.notifyDataSetChanged();
                    Utils.saveFile();//把软存中的数据存入磁盘中
                }).create();
        dialog.show();
    }

    public void hideInputKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
    private void updateMessage(String version) {
        MessageBean bean = GreenDaoMaster.getAllFromInfo_bean();
        bean.setVersion(version);
        getDaoSession().getMessageBeanDao().update(bean);
        Utils.saveFile_Message();
    }

    /**
     * 扫描箱号
     */
    private void addXiangHao(String data) {
        char[] xh = data.toCharArray();
        char[] strNo1 = {xh[1], xh[2], xh[9], xh[10], xh[11], xh[12], xh[13], xh[14]};//箱号数组
        final String strNo = "00";
        String a = xh[5] + "" + xh[6];
        String endNo = Utils.XiangHao(a);
        final String prex = String.valueOf(strNo1);
        final int finalEndNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + xh[17] + endNo);
        final int finalStrNo = Integer.parseInt(xh[15] + "" + xh[16] + "" + xh[17] + strNo);
        new Thread(() -> {
            insertDenator(prex, finalStrNo, finalEndNo);//添加
        }).start();
    }

    /***
     * 手动输入注册(通过开始管壳码和截止管壳码计算出所有管壳码)
     */
    private int insertDenator(String prex, int start, int end) {

        if (end < start) return -1;
        if (start < 0 || end > 99999) return -1;
        String shellNo = "";
        int flag = 0;
        int index = getEmptyDenator(-1);
        int maxNo = getMaxNumberNo();
        int start_delay = 0;//开始延时
        int f1 = Integer.parseInt(String.valueOf(f1ys));//f1延时
        int f2 = Integer.parseInt(String.valueOf(f2ys));//f2延时
        Log.e("注册", "f1: " + f1);
        Log.e("注册", "f2: " + f2);
        int delay = getMaxDelay(maxNo);//获取最大延时
        if (delay_set.equals("f1")) {
            if (maxSecond != 0 && delay + f1 > maxSecond) {//
                isCorrectReisterFea = 3;
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage());
                return -1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxSecond != 0 && delay + f2 > maxSecond) {//
                isCorrectReisterFea = 3;
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage());
                return -1;
            }
        }
        if (maxSecond != 0 && f1 > maxSecond) {//
            isCorrectReisterFea = 3;
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage());
            return -1;
        }
        if (maxSecond != 0 && f2 > maxSecond) {//
            isCorrectReisterFea = 3;
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage());
            return -1;
        }
        ContentValues values = new ContentValues();
        int reCount = 0;
        Utils.writeRecord("--手动输入注册--前8位:" + prex + "--开始后5位:" + start +
                "--结束后5位:" + end + "--开始延时:" + start_delay);
        for (int i = start; i <= end; i++) {
            shellNo = prex + String.format("%05d", i);
            if (checkRepeatShellNo(shellNo)) {
                singleShellNo = "";
                singleShellNo = shellNo;
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));

                break;
            }
            DetonatorTypeNew detonatorTypeNew = serchDenatorId(shellNo);
            if (detonatorTypeNew == null) {
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
                return -1;
            }
            if (delay_set.equals("f1")) {//获取最大延时有问题
                if (maxNo == 0) {
                    delay = delay + start_delay;
                } else {
                    delay = delay + f1;
                }
            } else if (delay_set.equals("f2")) {
                if (maxNo == 0) {
                    delay = delay + start_delay;
                } else {
                    delay = delay + f2;
                }
            }
            if (index < 0) {//说明没有空余的序号可用
                maxNo++;
                values.put("blastserial", maxNo);
                values.put("sithole", maxNo);
                values.put("shellBlastNo", shellNo);
                if(!detonatorTypeNew.getDetonatorId().equals("0")){
                    values.put("denatorId",detonatorTypeNew.getDetonatorId());
                }
                values.put("delay", delay);
                values.put("regdate", Utils.getDateFormatLong(new Date()));
                values.put("statusCode", "02");
                values.put("statusName", "已注册");
                values.put("errorCode", "FF");
                values.put("errorName", "");
                values.put("wire", "");//桥丝状态
                values.put("zhu_yscs", detonatorTypeNew.getZhu_yscs());
                //向数据库插入数据
                db.insert("denatorBaseinfo", null, values);
            } else {
                values = new ContentValues();
                values.put("shellBlastNo", shellNo);//key为字段名，value为值
                values.put("statusCode", "");
                values.put("statusName", "");
                values.put("regdate", Utils.getDateFormatLong(new Date()));
                values.put("statusCode", "02");
                values.put("statusName", "已注册");
                values.put("errorCode", "FF");
                values.put("errorName", "");
                db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + index});

            }
            reCount++;
        }
        demoAdapter.notifyDataSetChanged();
        pb_show = 0;
        tipInfoFlag = 88;
        mHandler_1.sendMessage(mHandler_1.obtainMessage());
        Utils.saveFile();//把软存中的数据存入磁盘中
        return reCount;
    }

    /***
     * 单发注册(扫码注册,用到)
     */
    private int insertSingleDenator(String shellNo) {
        if (shellNo.length() != 13) {
            return -1;
        }
        if (check(shellNo) == -1) {
            return -1;
        }
        DetonatorTypeNew detonatorTypeNew = serchDenatorId(shellNo);
        if (detonatorTypeNew == null) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
            return -1;
        }
        int index = getEmptyDenator(-1);
        int maxNo = getMaxNumberNo();
        int start_delay = 0;//开始延时
        int f1 = Integer.parseInt(String.valueOf(f1ys));//f1延时
        int f2 = Integer.parseInt(String.valueOf(f2ys));//f2延时
        int delay = getMaxDelay(maxNo);//获取最大延时
        Log.e("扫码", "delay_set: " + delay_set);
        Log.e("扫码", "detonatorTypeNew: " + detonatorTypeNew.toString());
        if (delay_set.equals("f1")) {
            if (maxSecond != 0 && delay + f1 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxSecond != 0 && delay + f2 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        }
        if (delay_set.equals("f1")) {//获取最大延时有问题
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                delay = delay + f1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                delay = delay + f2;
            }
        }

        if (index < 0) {//说明没有空余的序号可用
            ContentValues values = new ContentValues();
            maxNo++;
            values.put("blastserial", maxNo);
            values.put("sithole", maxNo);
            values.put("shellBlastNo", shellNo);
            if(!detonatorTypeNew.getDetonatorId().equals("0")){
                values.put("denatorId",detonatorTypeNew.getDetonatorId());
            }

            values.put("delay", delay);
            values.put("regdate", Utils.getDateFormatLong(new Date()));
            values.put("statusCode", "02");
            values.put("statusName", "已注册");
            values.put("errorCode", "FF");
            values.put("errorName", "");
            values.put("wire", "");
            values.put("zhu_yscs",detonatorTypeNew.getZhu_yscs());
            //向数据库插入数据
            db.insert("denatorBaseinfo", null, values);

        } else {
            ContentValues values = new ContentValues();
            values.put("shellBlastNo", shellNo);//key为字段名，value为值
            values.put("statusCode", "02");
            values.put("statusName", "已注册");
            values.put("errorCode", "FF");
            values.put("errorName", "");
            values.put("regdate", Utils.getDateFormatLong(new Date()));
            db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + index});
        }
        demoAdapter.notifyDataSetChanged();
        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        Utils.writeRecord("单发输入注册:--管壳码:" + shellNo + "--延时:" + delay);
        return 0;
    }

    /***
     * 单发注册(同时注册管壳码和芯片码)
     */
    private int insertSingleDenator_2(String shellNo, String denatorId, String yscs, String version, String duan_scan) {
        if (shellNo.length() != 13) {
            return -1;
        }
        if (check(shellNo) == -1) {
            return -1;
        }
        int index = getEmptyDenator(-1);
        int maxNo = getMaxNumberNo();
        int start_delay = 0;//开始延时
        int f1 = Integer.parseInt(String.valueOf(f1ys));//f1延时
        int f2 = Integer.parseInt(String.valueOf(f2ys));//f2延时
        int delay = getMaxDelay(maxNo);//获取最大延时
        Log.e("扫码", "delay_set: " + delay_set);
        if (delay_set.equals("f1")) {
            if (maxSecond != 0 && delay + f1 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxSecond != 0 && delay + f2 > maxSecond) {//
                mHandler_tip.sendMessage(mHandler_tip.obtainMessage(3));
                return -1;
            }
        }
        if (delay_set.equals("f1")) {//获取最大延时有问题
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                delay = delay + f1;
            }
        } else if (delay_set.equals("f2")) {
            if (maxNo == 0) {
                delay = delay + start_delay;
            } else {
                delay = delay + f2;
            }
        }
        Utils.writeRecord("单发输入注册:--管壳码:" + shellNo + "芯片码" + denatorId + "--延时:" + delay);
//        if (index < 0) {//说明没有空余的序号可用
//            ContentValues values = new ContentValues();
//            maxNo++;
//            values.put("blastserial", maxNo);
//            values.put("sithole", maxNo);
//            values.put("shellBlastNo", shellNo);
//            values.put("denatorId", denatorId);
//            values.put("delay", delay);
//            values.put("regdate", Utils.getDateFormatLong(new Date()));
//            values.put("statusCode", "02");
//            values.put("statusName", "已注册");
//            values.put("errorCode", "FF");
//            values.put("errorName", "");
//            values.put("authorization", "");
//            values.put("wire", "");
//            //向数据库插入数据
//            db.insert("denatorBaseinfo", null, values);
//
//        } else {
//            ContentValues values = new ContentValues();
//            values.put("shellBlastNo", shellNo);//key为字段名，value为值
//            values.put("statusCode", "02");
//            values.put("statusName", "已注册");
//            values.put("errorCode", "FF");
//            values.put("errorName", "");
//            values.put("regdate", Utils.getDateFormatLong(new Date()));
//            db.update(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, values, "blastserial=?", new String[]{"" + index});
//        }

        maxNo++;
        DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
        denatorBaseinfo.setBlastserial(maxNo);
        denatorBaseinfo.setSithole(maxNo );
        denatorBaseinfo.setShellBlastNo(shellNo);
        denatorBaseinfo.setDenatorId(denatorId);
        denatorBaseinfo.setDelay(delay);
        denatorBaseinfo.setRegdate(Utils.getDateFormat(new Date()));
        denatorBaseinfo.setStatusCode("02");
        denatorBaseinfo.setStatusName("已注册");
        denatorBaseinfo.setErrorCode("FF");
        denatorBaseinfo.setErrorName("");
        denatorBaseinfo.setWire("");//桥丝状态
        denatorBaseinfo.setPai(paiChoice);
        denatorBaseinfo.setAuthorization(version);//雷管芯片型号
        denatorBaseinfo.setZhu_yscs(yscs);
        denatorBaseinfo.setCurrent("0");
        denatorBaseinfo.setVoltage("0");
        //向数据库插入数据
        getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);

        demoAdapter.notifyDataSetChanged();
        tipInfoFlag = 88;
        mHandler_1.sendMessage(mHandler_1.obtainMessage());

        Utils.saveFile();//把闪存中的数据存入磁盘中
        SoundPlayUtils.play(1);
        return 0;
    }
    /**
     * 好像没什么用
     */
    private int getEmptyDenator(int start) {
        String selection = "shellBlastNo = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, selection, selectionArgs, null, null, null);
        int serialNo = -1;
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                if (start < 0) {///???start一直为-1
                    break;
                } else {
                    if (serialNo < start) {
                        serialNo = -1;
                        continue;
                    } else {
                        break;
                    }
                }
            }
            cursor.close();
        }
        return serialNo;
    }

    /***
     * 得到最大序号
     * @return
     */
    private int getMaxNumberNo() {
        Cursor cursor = db.rawQuery("select max(blastserial) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null);
        if (cursor != null && cursor.moveToNext()) {
            int maxNo = cursor.getInt(0);
            cursor.close();
            return maxNo;
        }
        return 1;
    }

    /***
     * 得到最大延时
     * @return
     */
    private int getMaxDelay(int no) {
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where blastserial =? ";
        Cursor cursor = db.rawQuery(sql, new String[]{no + ""});
        if (cursor != null && cursor.moveToNext()) {
            Log.e("延时", "getMaxDelay: " + cursor.getInt(0));
            Log.e("延时", "getMaxDelay: " + cursor.getInt(5));
            int maxDelay = cursor.getInt(5);
            cursor.close();
            return maxDelay;
        }
        return 0;
    }

    /**
     * 获得设置中的最大延时
     */
    private void getFactoryType() {
        String selection = " isSelected = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {"是"};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOR_TYPE, null, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            deTypeSecond = cursor.getString(2);
            cursor.close();
        }
        if (deTypeSecond != null && deTypeSecond.length() > 0) {
            maxSecond = Integer.parseInt(deTypeSecond);
        }
        Log.e("最大延时", "deTypeSecond: " + deTypeSecond);
    }
    /**
     * 获取厂家号
     * */
    private void getFactoryCode() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<Defactory> list = master.queryDefactoryToIsSelected("是");
        if (list.size() > 0) {
            factoryCode = list.get(0).getDeEntCode();
            factoryFeature = list.get(0).getDeFeatureCode();
        }
        Log.e("厂家管码", "factoryCode: " + factoryCode);
    }

    private int showDenatorSum() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryDenatorBaseinfoToStatusCode("02");
        binding.zcTxtTotal.setText("共:" + list.size()+"发");
        return list.size();
    }
    private DetonatorTypeNew serchDenatorId(String shellBlastNo) {
        GreenDaoMaster master = new GreenDaoMaster();
        //        Log.e("查询生产数据库查管壳码", "denatorId: "+denatorId);
//        Log.e("查询生产数据库查管壳码", "shellBlastNo: "+shellBlastNo);
        return master.queryShellBlastNoTypeNew(shellBlastNo);
    }
    /**
     * 检查重复的数据
     *
     * @param shellBlastNo
     * @return
     */
    public boolean checkRepeatShellNo(String shellBlastNo) {
        DenatorBaseinfo denatorBaseinfo = new GreenDaoMaster().checkRepeatShellNo_2(shellBlastNo);
        if (denatorBaseinfo != null) {
            Log.e("注册", "denatorBaseinfo: " + denatorBaseinfo.toString());
            lg_No = denatorBaseinfo.getBlastserial() + "";
            return true;
        } else {
            return false;
        }
    }

    /***
     * 得到最大排号
     * @return
     */
    private int getMaxPai() {
        Cursor cursor = db.rawQuery("select max(pai) from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null);
        if (cursor != null && cursor.moveToNext()) {
            int maxNo = cursor.getInt(0);
            cursor.close();
            return maxNo;
        }
        return 1;
    }
    /***
     * 得到最大孔内序号
     * @return
     */
    private int getMaxSitholeNum(int no) {
        String sql = "Select * from " + DatabaseHelper.TABLE_NAME_DENATOBASEINFO + " where pai =? ";
        Cursor cursor = db.rawQuery(sql, new String[]{no + ""});
        if (cursor != null && cursor.moveToNext()) {
            Log.e("最大孔内序号", "maxSitholeNum: " + cursor.getInt(19));
            int maxSitholeNum = cursor.getInt(19);
            cursor.close();
            return maxSitholeNum;
        }
        return 0;
    }

    //扫码方法
    private void decodeBar(String strParamBarcode) {

        String subBarCode;

        if (strParamBarcode.trim().length() > 14) {
            int index = strParamBarcode.indexOf("SC:");
            if (index > 0) {
                show_Toast("不正确的编码，请扫描选择正确的编码");
                return;
            }
            subBarCode = strParamBarcode.substring(index + 3, index + 16);
            if (subBarCode.trim().length() < 13) {
                show_Toast("不正确的编码，请扫描选择正确的编码");
                return;
            }
        } else {
            if (strParamBarcode.trim().length() == 14) {//煤许雷管
                subBarCode = strParamBarcode.substring(0, 13);
            } else if (strParamBarcode.trim().length() == 13) {
                subBarCode = strParamBarcode;
            } else
                return;
        }
        Log.e("扫码结果", "subBarCode: " + subBarCode);
        String facCode = subBarCode.substring(0, 2);
        String dayCode = subBarCode.substring(2, 7);
        String featureCode = subBarCode.substring(7, 8);
        String serialNo = subBarCode.substring(8);
        Log.e("注册页面--扫码注册", "facCode: " + facCode + "  dayCode:" + dayCode + "  featureCode:" + featureCode + "  serialNo:" + serialNo);

//        if (sanButtonFlag == 1) {
//            edit_start_entBF2Bit_st.setText(facCode);
//            edit_start_entproduceDate_st.setText(dayCode);//日期码
//            edit_start_entAT1Bit_st.setText(featureCode);
//            edit_start_entboxNoAndSerial_st.setText(serialNo);
//
//            edit_end_entBF2Bit_en.setText("");
//            edit_end_entproduceDate_ed.setText("");
//            edit_end_entAT1Bit_ed.setText("");
//            edit_end_entboxNoAndSerial_ed.setText("");
//            btnReisterScanStartEd.setEnabled(true);
//            btnScanReister.setEnabled(true);
//        }
//        if (sanButtonFlag == 2) {
//            edit_end_entBF2Bit_en.setText(facCode);
//            edit_end_entproduceDate_ed.setText(dayCode);
//            edit_end_entAT1Bit_ed.setText(featureCode);
//            edit_end_entboxNoAndSerial_ed.clearFocus();
//            edit_end_entboxNoAndSerial_ed.setText(serialNo);
//            btnReisterScanStartSt.setEnabled(true);
//            btnScanReister.setEnabled(true);
//        }
//        sanButtonFlag = 0;
    }

    private int check(String shellNo) {
        if (f1ys.length() < 1 || f2ys.length() < 1) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(8));
            return -1;
        }
        //管厂码
        String facCode = Utils.getDetonatorShellToFactoryCodeStr(shellNo);
        //特征码
        String facFea = Utils.getDetonatorShellToFeatureStr(shellNo);
        //雷管信息有误，管厂码不正确，请检查
        if (factoryCode != null && factoryCode.trim().length() > 0 && factoryCode.indexOf(facCode) < 0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
            return -1;
        }
        if (shellNo.length() > 13) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(6));
            return -1;
        }
        //雷管信息有误，特征码不正确，请检查
        if (factoryFeature != null && factoryFeature.trim().length() > 0 && factoryFeature.indexOf(facFea) < 0) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
            return -1;
        }
        //检查重复数据
        if (checkRepeatShellNo(shellNo)) {
            singleShellNo = "";
            singleShellNo = shellNo;
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
            return -1;
        }
        return 0;
    }

    //得到连续管壳码
    private String getContinueScanBlastNo(String strBarcode) {

        if (strBarcode.length() < 13) return null;
        if (strBarcode.trim().length() == 14) {
            strBarcode = strBarcode.substring(0, 13);
            return strBarcode;
        } else if (strBarcode.trim().length() == 13) {
            //strBarcode= strBarcode;
            return strBarcode;
        }
        int index = strBarcode.indexOf("SC:");
        if (index < 0) return null;
        String subBarCode = strBarcode.substring(index + 3, index + 16);
        if (subBarCode.trim().length() < 13) {
            show_Toast("当前二维码不符合规范,请检查后再扫");
            return null;
        }
        return subBarCode;
    }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        if (tipDlg != null) {
            tipDlg.dismiss();
            tipDlg = null;
        }
        if(mScaner!=null){
            mScaner.unregisterScanCb();
        }
        if(scanDecode!=null){
            scanDecode.stopScan();//停止扫描
            scanDecode.onDestroy();//回复初始状态
        }
        if (scanBarThread != null) {
            scanBarThread.exit = true;  // 终止线程thread
            try {
                scanBarThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("点击按键", "keyCode: "+keyCode );
        if (keyCode == KeyEvent.KEYCODE_THUMBS_DOWN ||keyCode == KeyEvent.KEYCODE_PROFILE_SWITCH||keyCode == 289) {//287
            if(Build.DEVICE.equals("M900")){
                mScaner.startScan();
            }

            return true;
        }
//        if (keyCode == KeyEvent.KEYCODE_BACK && pb_show == 1) {
//            if ((System.currentTimeMillis() - mExitTime) > 2000) {// System.currentTimeMillis()无论何时调用，肯定大于2000
//                show_Toast("正在运行程序请稍后退出");
//                mExitTime = System.currentTimeMillis();
//            }
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }
}