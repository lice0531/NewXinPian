package android_serialport_api.xingbang.activity;


import static android_serialport_api.xingbang.Application.getDaoSession;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android_serialport_api.xingbang.Application;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.SerialPortActivity;
import android_serialport_api.xingbang.cmd.DefCommand;
import android_serialport_api.xingbang.cmd.FourStatusCmd;
import android_serialport_api.xingbang.cmd.OneReisterCmd;
import android_serialport_api.xingbang.cmd.vo.From12Reister;
import android_serialport_api.xingbang.cmd.vo.From42Power;
import android_serialport_api.xingbang.custom.RecyclerViewAdapter_Denator;
import android_serialport_api.xingbang.custom.LoadingDialog;
import android_serialport_api.xingbang.databinding.ActivityZhuCeLineBinding;
import android_serialport_api.xingbang.db.DatabaseHelper;
import android_serialport_api.xingbang.db.Defactory;
import android_serialport_api.xingbang.db.DenatorBaseinfo;
import android_serialport_api.xingbang.db.DetonatorTypeNew;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.db.MessageBean;
import android_serialport_api.xingbang.db.greenDao.MessageBeanDao;
import android_serialport_api.xingbang.firingdevice.SetDelayTime;
import android_serialport_api.xingbang.utils.MmkvUtils;
import android_serialport_api.xingbang.utils.SoundPlayUtils;
import android_serialport_api.xingbang.utils.Utils;

public class ZhuCeActivity_line extends SerialPortActivity implements View.OnClickListener {
    ActivityZhuCeLineBinding binding;
    private RecyclerViewAdapter_Denator<DenatorBaseinfo> mAdapter;
    private List<DenatorBaseinfo> mListData = new ArrayList<>();//所有雷管列表
    private LinearLayoutManager linearLayoutManager;
    private int maxSecond = 20000;//最大延时
    private int send_13 = 0;
    private int send_10 = 0;
    private int send_41 = 0;
    private int send_40 = 0;
    //是否单发注册
    private int isSingleReisher = 0;
    private Handler mHandler_0 = new Handler();     // UI处理
    private Handler mHandler_1 = new Handler();//提示电源信息
    private Handler mHandler_tip = new Handler();//提示电源信息
    private Handler mHandler_loding = new Handler();//等待动画
    private String qiaosi_set = "";//是否检测桥丝
    private String version = "";//版本
    private From12Reister zhuce_form = null;
    private From42Power busInfo;//电压电流信息
    private volatile int zhuce_Flag = 0;//单发检测时发送40的标识
    private String lg_No;//重复雷管编号
    private String singleShellNo;//单发注册
    private String factoryCode = null;//厂家代码
    private String factoryFeature = null;//厂家特征码
    private DatabaseHelper mMyDatabaseHelper;
    private SQLiteDatabase db;
    private int pb_show = 0;//等待动画
    private LoadingDialog tipDlg = null;
    String f1ys;
    String f2ys;
    private String TAG = "单发检测页面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_zhu_ce_line);
        binding = ActivityZhuCeLineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //标题设置
        TextView title = findViewById(R.id.title_text);
        title.setText("单发检测雷管");
        ImageView iv_add = findViewById(R.id.title_add);
        ImageView iv_back = findViewById(R.id.title_back);
        iv_add.setOnClickListener(v -> startActivity(new Intent(ZhuCeActivity_line.this, SetDelayTime.class)));
        iv_back.setOnClickListener(v -> finish());

        mMyDatabaseHelper = new DatabaseHelper(this, "denatorSys.db", null, 22);
        db = mMyDatabaseHelper.getReadableDatabase();
        getFactoryCode();//获取厂家号
        getUserMessage();//获取版本号
        //新的适配方法 适配器
        linearLayoutManager = new LinearLayoutManager(this);
        binding.zclRlLgRv.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerViewAdapter_Denator<>(this, 2);
        binding.zclRlLgRv.setAdapter(mAdapter);
        mAdapter.setOnItemLongClick(position -> {
            DenatorBaseinfo info = mListData.get(position);
            Logger.e("长按"+ "position: " + position + "info.getBlastserial()" + info.getBlastserial());
            // 序号 延时 管壳码
//            modifyBlastBaseInfo(no, delay, shellBlastNo);
            modifyBlastBaseInfo(info, position);//序号,孔号,延时,管壳码
        });
        mListData.clear();
        mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
        mAdapter.setListData(mListData, 1);
        mAdapter.notifyDataSetChanged();

        mHandler_0 = new Handler(msg -> {
            switch (msg.what) {
                // 区域 更新视图
                case 1001:
                    // 查询全部雷管 倒叙(序号)
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;
                // 重新排序 更新视图
                case 1002:

                    break;
                // 扫描方法
                case 1004:
//                    decodeBar(msg.obj.toString());
                    break;
                case 1005://按管壳码排序
//                    Logger.e("扫码注册"+ "按管壳码排序flag: " + paixu_flag);
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
                    Collections.sort(mListData);
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
            return false;
        });

        mHandler_1 = new Handler(message -> {
            switch (message.what) {
                case 1:
                    if (busInfo != null) {//显示电流电压
                    }
                    break;
                case 2://提示已注册多少发
                    if (busInfo != null) {
                        byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "01");//获取电源信息
                        sendCmd(reCmd);
                    }
                    break;
                case 3://未收到关闭电源命令
                    show_Toast("未收到单片机返回命令");
                    break;
                case 4://未收到打开电源命令
                    show_Toast(getResources().getString(R.string.text_error_tip6));
                    break;
                case 5://桥丝不正常
                    show_Toast(getResources().getString(R.string.text_error_tip7));
                    SoundPlayUtils.play(4);
                    break;
                case 6:
                    show_Toast("请先设置延时");
                    break;
                case 7:
                    show_Toast("当前注册雷管电流过大,请检查雷管");
                    SoundPlayUtils.play(4);
                    break;
                case 8:
                    show_Toast("当前雷管有异常,请检测后重新注册");
                    SoundPlayUtils.play(4);
                    break;
                case 9:
                    show_Toast("当前雷管读码异常,请检查该雷管编码规则");
                    SoundPlayUtils.play(4);
                    break;
                case 10:
                    show_Toast("当前雷管为煤许产品,请用煤许版本进行注册");
                    SoundPlayUtils.play(4);
                    break;
                case 11:
                    SoundPlayUtils.play(4);
                    show_Toast(getResources().getString(R.string.text_error_tip1));
                    //"雷管信息有误，管厂码不正确，请检查"
                    break;
                case 12:
                    SoundPlayUtils.play(4);
                    show_Toast(getResources().getString(R.string.text_error_tip2));
                    break;
                case 13:
                    SoundPlayUtils.play(4);
                    show_Toast("已达到最大延时限制" + maxSecond + "ms");
                    break;
                case 14://重复的时候跳到对应的条目
                    SoundPlayUtils.play(4);
                    show_Toast_long("与第" + lg_No + "发" + singleShellNo + "重复");
                    int total = GreenDaoMaster.showDenatorSum();
                    MoveToPosition(linearLayoutManager, binding.zclRlLgRv, total - Integer.parseInt(lg_No));
                    break;
                case 16:
                    SoundPlayUtils.play(4);
                    show_Toast("当前管壳码不等于13位,请检查雷管或系统版本是否符合后,再次注册");
                    break;
                case 99://刷新页面
                    mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
                    mAdapter.setListData(mListData, 1);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 88://刷新界面(未完成)
//                    showDenatorSum();
//                    edit_start_entboxNoAndSerial_st.getText().clear();
//                    edit_end_entboxNoAndSerial_ed.getText().clear();//.setText("")
                    break;
                case 89:
                    show_Toast("输入的管壳码重复");
//                    showDenatorSum();
                    SoundPlayUtils.play(4);
                    break;
            }
            return false;
        });
        mHandler_tip = new Handler(msg -> {
            if (msg.what == 1) {
                SoundPlayUtils.play(4);
                show_Toast(getResources().getString(R.string.text_error_tip1));
                //"雷管信息有误，管厂码不正确，请检查"
            } else if (msg.what == 2) {
                SoundPlayUtils.play(4);
                show_Toast(getResources().getString(R.string.text_error_tip2));
            } else if (msg.what == 3) {
                SoundPlayUtils.play(4);
                show_Toast("已达到最大延时限制" + maxSecond + "ms");
            } else if (msg.what == 4) {
                SoundPlayUtils.play(4);
                show_Toast_long("与第" + lg_No + "发" + singleShellNo + "重复");
                int total = showDenatorSum();
//                reisterListView.setSelection(total - Integer.parseInt(lg_No));//跳到对应的条目
            } else if (msg.what == 6) {
                SoundPlayUtils.play(4);
                show_Toast("当前管壳码不等于13位,请检查雷管或系统版本是否符合后,再次注册");
            } else if (msg.what == 10) {
                show_Toast("找不到对应的生产数据,请先导入生产数据");
            } else if (msg.what == 99) {
                mListData = new GreenDaoMaster().queryDetonatorRegionDesc();
                mAdapter.setListData(mListData, 1);
                mAdapter.notifyDataSetChanged();
            } else {
                SoundPlayUtils.play(4);
                show_Toast("注册失败");
            }
            return false;
        });
        mHandler_loding = new Handler(message -> {
            if (pb_show == 1 && tipDlg != null) tipDlg.show();
            if (pb_show == 0 && tipDlg != null) tipDlg.dismiss();
            return false;
        });
        //获取常用值
        f1ys = (String) MmkvUtils.getcode("f1ys", "0");
        f2ys = (String) MmkvUtils.getcode("f2ys", "0");

        Utils.writeRecord("---进入单发注册页面---");
        if (version.equals("01")) {
            sendCmd(FourStatusCmd.send46("00", "01"));//20(第一代)
        } else {
            sendCmd(FourStatusCmd.send46("00", "02"));//20(第二代)
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        byte[] cmdBuf = new byte[size];
        System.arraycopy(buffer, 0, cmdBuf, 0, size);

        String fromCommad = Utils.bytesToHexFun(cmdBuf);
//        Logger.e("注册"+ "fromCommad: "+fromCommad );
        if (completeValidCmd(fromCommad) == 0) {
//            fromCommad = this.revCmd;//如果revcmd不清空,会导致收到40,但却变成上次的13,先屏蔽
//            if (this.afterCmd != null && this.afterCmd.length() > 0) this.revCmd = this.afterCmd;
//            else this.revCmd = "";
            String realyCmd1 = DefCommand.decodeCommand(fromCommad);
            if ("-1".equals(realyCmd1) || "-2".equals(realyCmd1)) {

            } else {
                String cmd = DefCommand.getCmd(fromCommad);
                if (cmd != null) {
                    doWithReceivData(cmd, cmdBuf);
                }
            }
        }
    }

    /**
     * 处理芯片返回命令
     */
    private void doWithReceivData(String cmd, byte[] cmdBuf) {
        String fromCommad = Utils.bytesToHexFun(cmdBuf);
        if (DefCommand.CMD_4_XBSTATUS_2.equals(cmd)) {//41开启总线电源指令
            send_41 = 0;
//            sendOpenThread.exit = true;
//            Logger.e("是否检测桥丝"+ "qiaosi_set: " + qiaosi_set);
            if (qiaosi_set.equals("true")) {//10 进入自动注册模式(00不检测01检测)桥丝
                sendCmd(OneReisterCmd.send_10("00", "01"));
            } else {
                sendCmd(OneReisterCmd.send_10("00", "00"));
            }
        } else if (DefCommand.CMD_1_REISTER_1.equals(cmd)) {//10 进入自动注册模式
            send_10 = 0;
            //发送获取电源信息
            byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00");//40
            sendCmd(reCmd);
        } else if (DefCommand.CMD_1_REISTER_3.equals(cmd)) {//12 有雷管接入
            //C0001208 FF 00 B6E6FF00 41 A6 1503 C0  普通雷管
            //C000120C FF 00 B6E6FF00 41 A6 B6E6FF00 1503 C0
//            try {
//                Thread.sleep(500);//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //2  连续发三次询问电流指令
            byte[] reCmd = FourStatusCmd.setToXbCommon_Power_Status24_1("00", "00");//40获取电源信息
            sendCmd(reCmd);
//            zhuce_form = OneReisterCmd.decodeFromReceiveAutoDenatorCommand14("00", cmdBuf, qiaosi_set);//桥丝检测
            zhuce_form = OneReisterCmd.decode12_newXinPian("00", cmdBuf, qiaosi_set);//桥丝检测
            if (qiaosi_set.equals("true") && zhuce_form.getWire().equals("无")) {
                mHandler_1.sendMessage(mHandler_1.obtainMessage(5));//提示类型桥丝不正常
                String detonatorId = Utils.GetShellNoById_newXinPian(zhuce_form.getFacCode(), zhuce_form.getFeature(), zhuce_form.getDenaId());
                Utils.writeRecord("--单发注册--:管壳码:" + GreenDaoMaster.serchShellBlastNo(detonatorId) + " 芯片码:" + detonatorId + "该雷管桥丝异常");
            }
            zhuce_Flag = 1;

        } else if (DefCommand.CMD_1_REISTER_4.equals(cmd)) {//13 退出自动注册模式
            send_13 = 0;
//            if (initCloseCmdReFlag == 1) {//打开电源
//                revCloseCmdReFlag = 1;
//                closeOpenThread.exit = true;
//                sendOpenThread = new SendOpenPower();
//                sendOpenThread.start();
//            }

        } else if (DefCommand.CMD_4_XBSTATUS_1.equals(cmd)) { //40 总线电流电压
            send_40 = 0;
            busInfo = FourStatusCmd.decode_40("00", cmdBuf);//解析 40指令
            mHandler_1.sendMessage(mHandler_1.obtainMessage(1));

            if (zhuce_Flag == 1) {//多次单发注册后闪退,busInfo.getBusCurrentIa()为空
                String detonatorId = Utils.GetShellNoById_newXinPian(zhuce_form.getFacCode(), zhuce_form.getFeature(), zhuce_form.getDenaId());
                if (busInfo.getBusCurrentIa() > 70) {//判断当前电流是否偏大
                    mHandler_1.sendMessage(mHandler_1.obtainMessage(7));//电流过大
                    SoundPlayUtils.play(4);
                    zhuce_Flag = 0;
                    Logger.e("芯片码:"+detonatorId);
                    Utils.writeRecord("--单发注册--:管壳码:" + GreenDaoMaster.serchShellBlastNo(detonatorId) + "芯片码" + zhuce_form.getDenaId() + "该雷管电流过大");
                } else {
                    if (zhuce_form != null) {//管厂码,特征码,雷管id
//                        // 获取 管壳码
//                        String shellNo = new GreenDaoMaster().getShellNo(detonatorId);
                        insertSingleDenator(detonatorId, zhuce_form);//单发注册
                        zhuce_Flag = 0;
                    }

                }
            }
        }

    }

    /**
     * 单发注册(存储桥丝状态)
     */
    private void insertSingleDenator(String detonatorId, From12Reister zhuce_form) {
        // 管厂码
        String facCode = Utils.getDetonatorShellToFactoryCodeStr(detonatorId);
        // 特征码
        String facFea = Utils.getDetonatorShellToFeatureStr(detonatorId);
        Logger.e("注册"+ "detonatorId: " + detonatorId);
//        String shellBlastNo = serchShellBlastNo(detonatorId);
        DetonatorTypeNew detonatorTypeNew = serchDenatorForDetonatorTypeNew(detonatorId);
//        if (detonatorTypeNew == null) {//考虑到可以直接注册A6
//            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(10));
//            return -1;
//        }
        if (checkRepeatdenatorId(detonatorId)) {//判断8位芯片码
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
        }
        if (detonatorTypeNew != null && detonatorTypeNew.getShellBlastNo().length() == 13 && checkRepeatShellNo(detonatorTypeNew.getShellBlastNo())) {//判断管壳码
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(4));
        }
        if (detonatorId.startsWith("00000", 2)) {
            mHandler_1.sendMessage(mHandler_1.obtainMessage(8));
        }
        if (detonatorId.length() != 13) {
            mHandler_1.sendMessage(mHandler_1.obtainMessage(9));
        }
        if (factoryCode != null && factoryCode.trim().length() > 0 && !factoryCode.contains(facCode)) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(1));
        }

        if (factoryFeature != null && factoryFeature.trim().length() > 0 && !factoryFeature.contains(facFea)) {
            mHandler_tip.sendMessage(mHandler_tip.obtainMessage(2));
        }
        Logger.e("查询生产数据库查管壳码"+ "detonatorId: " + detonatorId);
        int maxNo = getMaxNumberNo();
        int pai = getMaxPai();
        Logger.e("最大排号"+ "pai: " + pai);
        int sitholeNum = getMaxSitholeNum(pai);//目前是单孔单发,考虑一下怎么注册单孔多发
        maxNo++;
        DenatorBaseinfo denatorBaseinfo = new DenatorBaseinfo();
        //从绑码库中获取到的数据
        if (detonatorTypeNew != null && detonatorTypeNew.getShellBlastNo().length() == 13) {
            denatorBaseinfo.setShellBlastNo(detonatorTypeNew.getShellBlastNo());
            denatorBaseinfo.setZhu_yscs(detonatorTypeNew.getZhu_yscs());
            Utils.writeRecord("--单发注册--" + "注册雷管码:" + detonatorTypeNew.getShellBlastNo() + " --芯片码:" + zhuce_form.getDenaId());
        } else {
            denatorBaseinfo.setShellBlastNo(detonatorId);
            denatorBaseinfo.setZhu_yscs(zhuce_form.getZhu_yscs());
            Utils.writeRecord("--单发注册--" + " --芯片码:" + zhuce_form.getDenaId());
        }
        if (zhuce_form.getDenaIdSup() != null) {
            denatorBaseinfo.setDenatorIdSup(zhuce_form.getDenaIdSup());//从芯片
            denatorBaseinfo.setCong_yscs(detonatorTypeNew.getCong_yscs());
            Utils.writeRecord("--单发注册: 从芯片码:" + zhuce_form.getDenaIdSup());
        }
        denatorBaseinfo.setBlastserial(maxNo);
        denatorBaseinfo.setSithole(maxNo);
        denatorBaseinfo.setDenatorId(zhuce_form.getDenaId());//主芯片
        denatorBaseinfo.setDelay(0);
        denatorBaseinfo.setRegdate(Utils.getDateFormatLong(new Date()));
        denatorBaseinfo.setStatusCode("02");
        denatorBaseinfo.setStatusName("正常");
        denatorBaseinfo.setErrorCode("FF");
        denatorBaseinfo.setErrorName("正常");
        denatorBaseinfo.setWire(zhuce_form.getWire());//桥丝状态
        denatorBaseinfo.setPai(pai);
        denatorBaseinfo.setSitholeNum(1);
        denatorBaseinfo.setAuthorization("");
        denatorBaseinfo.setDownloadStatus("");
        denatorBaseinfo.setVoltage(busInfo.getBusVoltage()+"");
        denatorBaseinfo.setCurrent(busInfo.getBusCurrentIa()+"");
        getDaoSession().getDenatorBaseinfoDao().insert(denatorBaseinfo);
        mHandler_tip.sendMessage(mHandler_tip.obtainMessage(99));
        SoundPlayUtils.play(1);
    }

    private void modifyBlastBaseInfo(DenatorBaseinfo info, int position) {
        Logger.e("长按"+ "info: " + info.toString());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delaymodifydialog, null);
        builder.setView(view);
        final EditText serialNoTxt = view.findViewById(R.id.serialNo);
        final EditText denatorNoTxt = view.findViewById(R.id.denatorNo);
        final EditText delaytimeTxt = view.findViewById(R.id.delaytime);
        serialNoTxt.setEnabled(false);
        denatorNoTxt.setEnabled(false);
        serialNoTxt.setText((mListData.size() - position) + "");
        denatorNoTxt.setText(info.getShellBlastNo() + "");
        delaytimeTxt.setText(info.getDelay() + "");
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {
            String delay = delaytimeTxt.getText().toString().trim();
            if (delay.trim().length() < 1 || (maxSecond > 0 && Integer.parseInt(delay) > maxSecond)) {
                show_Toast(getString(R.string.text_error_tip37));
                dialog.dismiss();
            } else if (maxSecond != 0 && Integer.parseInt(delay) > maxSecond || Integer.parseInt(delay) > 8000) {//
                mHandler_1.sendMessage(mHandler_1.obtainMessage(13));
                dialog.dismiss();
            } else {
                Utils.writeRecord("-单发修改延时:" + "-管壳码:" + info.getShellBlastNo() + "-延时:" + delay);
                modifyDelayTime(info, delay);
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
                //    将输入的用户名和密码打印出来
                show_Toast(getString(R.string.text_error_tip38));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("删除", (dialog, which) -> {
            dialog.dismiss();
//            pb_show = 1;
//            runPbDialog();
            Utils.writeRecord("-单发删除:" + "-删除管壳码:" + info.getShellBlastNo() + "-延时" + info.getDelay());
            new Thread(() -> {
                String whereClause = "shellBlastNo = ?";
                String[] whereArgs = {info.getShellBlastNo()};
                db.delete(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, whereClause, whereArgs);
                Utils.deleteData(ZhuCeActivity_line.this);//重新排序雷管
                mHandler_0.sendMessage(mHandler_0.obtainMessage(1001));
//                tipDlg.dismiss();
                Utils.saveFile();//把软存中的数据存入磁盘中
                pb_show = 0;
            }).start();
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.zc_btn_jiance) {
            Logger.e("isSingleReisher="+isSingleReisher);
            Logger.e("send_10="+send_10);
            Logger.e("send_13="+send_13);
            Logger.e("send_41="+send_41);
            Logger.e("send_40="+send_40);
            if (isSingleReisher == 0 && send_10 == 0 && send_13 == 0 && send_41 == 0 && send_40 == 0) {
                String shellBlastNo = GreenDaoMaster.serchFristLG();
                int num = GreenDaoMaster.serchFristLGINdenatorHis(shellBlastNo);
                if (num > 0) {
                    showAlertDialog();
                }
//                binding.zcBtnJiance.setEnabled(false);
                binding.zcBtnJiance.setText("停止检测");
                isSingleReisher = 1;
                sendCmd(FourStatusCmd.setToXbCommon_OpenPower_42_2("00"));//41 开启总线电源指令
            } else if (send_10 == 0 && send_13 == 0 && send_41 == 0 && send_40 == 0) {
//                binding.zcBtnJiance.setEnabled(true);
                binding.zcBtnJiance.setText("检测在线雷管");
                isSingleReisher = 0;
                // 13 退出注册模式
                sendCmd(OneReisterCmd.send_13("00"));
            } else {
                show_Toast("正在与单片机通讯,请稍等一下再退出注册模式!");
            }
        }
    }

    //发送命令
    public synchronized void sendCmd(byte[] mBuffer) {//0627添加synchronized,尝试加锁
        if (mSerialPort != null && mOutputStream != null) {
            try {
                String str = Utils.bytesToHexFun(mBuffer);
//                Utils.writeLog("Reister sendTo:" + str);
                Logger.e("发送命令"+ str);
                if (str.contains("C00010")) {
                    send_10 = 1;
                } else if (str.contains("C00041")) {
                    send_41 = 1;
                } else if (str.contains("C00013")) {
                    send_13 = 1;
                } else if (str.contains("C00040")) {
                    send_40 = 1;
                }
                mOutputStream.write(mBuffer);
                //实验有没有用
//                mOutputStream.flush();
//                mSerialPort.tcflush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("注册提示")
                .setMessage("注册列表中的雷管已在起爆历史记录里,是否清空列表")//设置对话框的内容"本次任务成功起爆！"
                .setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton("确认清空", (dialog12, which) -> {
                    Application.getDaoSession().getDenatorBaseinfoDao().deleteAll();
                    mListData.clear();
                    mAdapter.notifyDataSetChanged();
                    dialog12.dismiss();
                }).create();
        Utils.saveFile();//把软存中的数据存入磁盘中
        dialog.show();
    }

    /**
     * 检查重复的数据
     *
     * @param detonatorId 管壳码
     * @return 是否重复
     */
    public boolean checkRepeatdenatorId(String detonatorId) {
        Logger.e("检查重复的数据"+ "detonatorId: " + detonatorId);
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list_lg = master.checkRepeatdenatorId(detonatorId);
        if (list_lg.size() > 0) {
            Logger.e("注册"+ "list_lg: " + list_lg.toString());
            lg_No = list_lg.get(0).getBlastserial() + "";
            singleShellNo = list_lg.get(0).getShellBlastNo();
            return true;
        } else {
            return false;
        }
    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager       设置RecyclerView对应的manager
     * @param mRecyclerView 当前的RecyclerView
     * @param n             要跳转的位置
     */
    public static void MoveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }

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
//        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }

    /**
     * 获得厂家管码
     */
    private void getFactoryCode() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<Defactory> list = master.queryDefactoryToIsSelected("是");
        if (list.size() > 0) {
            factoryCode = list.get(0).getDeEntCode();
            factoryFeature = list.get(0).getDeFeatureCode();
        }
        Logger.e("厂家管码"+ "factoryCode: " + factoryCode);
    }

    private void getUserMessage() {
        List<MessageBean> message = getDaoSession().getMessageBeanDao().queryBuilder().where(MessageBeanDao.Properties.Id.eq((long) 1)).list();
        if (message.size() > 0) {
            qiaosi_set = message.get(0).getQiaosi_set();
            version = message.get(0).getVersion() + "";
        }
    }

    /**
     * 获取空白序号
     */
    private int getEmptyDenator(int start) {
        String selection = "shellBlastNo = ?"; // 选择条件，给null查询所有
        String[] selectionArgs = {""};//选择条件参数,会把选择条件中的？替换成这个数组中的值
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DENATOBASEINFO, null, selection, selectionArgs, null, null, null);
        int serialNo = -1;
        if (cursor != null) {  //cursor不位空,可以移动到第一行
            while (cursor.moveToNext()) {
                serialNo = cursor.getInt(1); //获取第二列的值 ,序号
                if (start < 0) {
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
        Logger.e(TAG+ "getEmptyDenator方法返回值serialNo: " + serialNo);
        return serialNo;
    }

    //更新延时
    public int modifyDelayTime(DenatorBaseinfo info, String delay) {
        int time = Integer.parseInt(delay);
        info.setDelay(time);
        Application.getDaoSession().getDenatorBaseinfoDao().update(info);
        Utils.saveFile();//把软存中的数据存入磁盘中
        return 1;
    }

    private void runPbDialog() {
        pb_show = 1;
        //  builder = showPbDialog();
        tipDlg = new LoadingDialog(ZhuCeActivity_line.this);
        Context context = tipDlg.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = tipDlg.findViewById(divierId);
        divider.setBackgroundColor(Color.TRANSPARENT);
        //tipDlg.setMessage("正在操作,请等待...").show();

        new Thread(() -> {

            //mHandler_2
            mHandler_loding.sendMessage(mHandler_loding.obtainMessage());
            //builder.show();
            try {
                while (pb_show == 1) {
                    Thread.sleep(100);
                }
                //builder.dismiss();
                mHandler_loding.sendMessage(mHandler_loding.obtainMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 查询生产表中对应的管壳码
     */
    private DetonatorTypeNew serchDenatorForDetonatorTypeNew(String denatorId) {
        GreenDaoMaster master = new GreenDaoMaster();
        return master.queryDetonatorForTypeNew(denatorId);
    }

    private int showDenatorSum() {
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list = master.queryDenatorBaseinfoToStatusCode("02");
        return list.size();
    }

    /**
     * 检查重复的数据
     *
     * @param shellNo
     * @return
     */
    public boolean checkRepeatShellNo(String shellNo) {
        Logger.e("检查重复的数据"+ "shellNo: " + shellNo);
        GreenDaoMaster master = new GreenDaoMaster();
        List<DenatorBaseinfo> list_lg = master.checkRepeatShellNo(shellNo);
        if (list_lg.size() > 0) {
            Logger.e("注册"+ "list_lg: " + list_lg.toString());
            lg_No = list_lg.get(0).getBlastserial() + "";
            singleShellNo = list_lg.get(0).getShellBlastNo();
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
            if(maxNo==0){
                maxNo=1;
            }
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
            Logger.e("最大孔内序号"+ "maxSitholeNum: " + cursor.getInt(19));
            int maxSitholeNum = cursor.getInt(19);
            cursor.close();
            return maxSitholeNum;
        }
        return 1;
    }

    @Override
    public void sendInterruptCmd() {
        byte[] reCmd = OneReisterCmd.send_13("00");//23 退出注册模式
        sendCmd(reCmd);
        super.sendInterruptCmd();
    }
}