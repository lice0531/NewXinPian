package android_serialport_api.xingbang.db;

import java.util.List;

import static android_serialport_api.xingbang.Application.getDaoSession;

/**
 * Created by xingbang on 2019/11/21.
 */

public class MyDB {
    private static String TAG="备份";
    /**
     * 从数据库表中拿数据
     *
     * @return
     */
    public static String getAllFromInfo() {
        List<DenatorBaseinfo> list=getDaoSession().getDenatorBaseinfoDao().loadAll();
        String str = "ID,序号,孔号,管壳码,芯片码,延时,读取状态,状态名称,错误名称,错误代码,授权期限,备注,注册日期,桥丝状态,名称\n";
        String content ;
        for(int i=0;i<list.size();i++){
            content = list.get(i).getId() + "," + list.get(i).getBlastserial() + "," + list.get(i).getSithole() + "," + list.get(i).getShellBlastNo() + "," +list.get(i).getDenatorId() + "," + list.get(i).getDelay() + "," + list.get(i).getStatusCode() + "," + list.get(i).getStatusName() + "," + list.get(i).getErrorName()+ "," + list.get(i).getErrorCode() + "," +list.get(i).getAuthorization()+ "," + list.get(i).getRemark() + "," + list.get(i).getRegdate() + "," + list.get(i).getWire() + "," + list.get(i).getName() + "\n";
            str = str + content;
        }
        return str;
    }

    /**
     * 从数据库表中拿数据
     *(2021/2/1日 没有用到)
     * @return
     */
    public static String getAllFromInfo_Message() {
        String str = "ID,身份证,合同编号,项目编号,设备编号,地理位置,网络地址,网络端口,网址,网络ip,是否检测桥丝,准备时间,充电时间,服务器1,服务器2,单位代码,检测时间\n";
        String content ;
        List<MessageBean> list= getDaoSession().getMessageBeanDao().loadAll();
        for (int i=0;i<list.size();i++){
            content = list.get(i).getId() + "#" + list.get(i).getPro_bprysfz() + "#" + list.get(i).getPro_htid() + "#" + list.get(i).getPro_xmbh() + "#" + list.get(i).getEqu_no() + "#"  + list.get(i).getServer_addr() + "#" + list.get(i).getServer_port() + "#" + list.get(i).getServer_http() + "#" + list.get(i).getServer_ip() + "#" + list.get(i).getQiaosi_set() + "#" + list.get(i).getPreparation_time() + "#" + list.get(i).getChongdian_time() + "#" + list.get(i).getServer_type1() + "#" + list.get(i).getServer_type2()+ "#" + list.get(i).getPro_dwdm()+ "#" + list.get(i).getJiance_time() + "\n";
            str = str + content;
        }
        return str;
    }
    /**
     * 从数据库表中拿数据
     *
     * @return
     */
    public static MessageBean getAllFromInfo_bean() {
        return getDaoSession().getMessageBeanDao().loadAll().get(0);
    }

}
