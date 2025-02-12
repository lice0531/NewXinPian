package android_serialport_api.xingbang.custom;
/**
 * 数据互传所使用的雷管相关数据（区域、排、雷管信息）
 */
public class SendMsgDenatorData {
    //区域id
    private int qyid;
    //区域最小延时
    private String qyDelayMin;
    //区域最大延时
    private String qyDelayMax;
    //区域起始延时
    private String qyStartDelay;
    //区域孔间延时
    private String qyKongDelay;
    //区域排间延时
    private String qyPaiDelay;
    //排id
    private int paiId;
    //排最小延时
    private String paiDelayMin;
    //区排最大延时
    private String paiDelayMax;
    //排起始延时
    private String paiStartDelay;
    //排孔间延时
    private String paiKongDelay;
    //排孔内延时
    private String paiNeiDelay;
    //排：是否递减
    private boolean paiDiJian;
    //孔号
    private String sithole;
    private String shellBlastNo;
    private String denatorId;
    //雷管延时
    private int delay;
    private String zhu_yscs;
    //雷管所属区域
    private String piece;
    private int duan;
    private int duanNo;
    //雷管所属区排
    private String pai;
    public int getQyid() {
        return qyid;
    }

    public void setQyid(int qyid) {
        this.qyid = qyid;
    }

    public String getQyDelayMin() {
        return qyDelayMin;
    }

    public void setQyDelayMin(String qyDelayMin) {
        this.qyDelayMin = qyDelayMin;
    }

    public String getQyDelayMax() {
        return qyDelayMax;
    }

    public void setQyDelayMax(String qyDelayMax) {
        this.qyDelayMax = qyDelayMax;
    }

    public String getQyStartDelay() {
        return qyStartDelay;
    }

    public void setQyStartDelay(String qyStartDelay) {
        this.qyStartDelay = qyStartDelay;
    }

    public String getQyKongDelay() {
        return qyKongDelay;
    }

    public void setQyKongDelay(String qyKongDelay) {
        this.qyKongDelay = qyKongDelay;
    }

    public int getPaiId() {
        return paiId;
    }

    public void setPaiId(int paiId) {
        this.paiId = paiId;
    }

    public String getPaiDelayMin() {
        return paiDelayMin;
    }

    public void setPaiDelayMin(String paiDelayMin) {
        this.paiDelayMin = paiDelayMin;
    }

    public String getPaiDelayMax() {
        return paiDelayMax;
    }

    public void setPaiDelayMax(String paiDelayMax) {
        this.paiDelayMax = paiDelayMax;
    }

    public String getPaiStartDelay() {
        return paiStartDelay;
    }

    public void setPaiStartDelay(String paiStartDelay) {
        this.paiStartDelay = paiStartDelay;
    }

    public String getPaiKongDelay() {
        return paiKongDelay;
    }

    public void setPaiKongDelay(String paiKongDelay) {
        this.paiKongDelay = paiKongDelay;
    }

    public String getPaiNeiDelay() {
        return paiNeiDelay;
    }

    public void setPaiNeiDelay(String paiNeiDelay) {
        this.paiNeiDelay = paiNeiDelay;
    }

    public boolean isPaiDiJian() {
        return paiDiJian;
    }

    public void setPaiDiJian(boolean paiDiJian) {
        this.paiDiJian = paiDiJian;
    }

    public String getSithole() {
        return sithole;
    }

    public void setSithole(String sithole) {
        this.sithole = sithole;
    }

    public String getShellBlastNo() {
        return shellBlastNo;
    }

    public void setShellBlastNo(String shellBlastNo) {
        this.shellBlastNo = shellBlastNo;
    }

    public String getDenatorId() {
        return denatorId;
    }

    public void setDenatorId(String denatorId) {
        this.denatorId = denatorId;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getZhu_yscs() {
        return zhu_yscs;
    }

    public void setZhu_yscs(String zhu_yscs) {
        this.zhu_yscs = zhu_yscs;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

    public int getDuan() {
        return duan;
    }

    public void setDuan(int duan) {
        this.duan = duan;
    }

    public int getDuanNo() {
        return duanNo;
    }

    public void setDuanNo(int duanNo) {
        this.duanNo = duanNo;
    }

    public String getPai() {
        return pai;
    }

    public void setPai(String pai) {
        this.pai = pai;
    }

    public String getQyPaiDelay() {
        return qyPaiDelay;
    }

    public void setQyPaiDelay(String qyPaiDelay) {
        this.qyPaiDelay = qyPaiDelay;
    }
}
