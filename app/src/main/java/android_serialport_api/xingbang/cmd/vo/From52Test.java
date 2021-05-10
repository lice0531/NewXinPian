package android_serialport_api.xingbang.cmd.vo;

public class From52Test {
	
	private String chipBaseStatus;//芯片状态
	private int chipPlusIa;//芯片电流
	private int chipRevIa;//芯片反向电流
	private int emptyIa=0;
	private int dePlusIa;//雷管电流 = chipPlusIa-18
	private int deRevIa;//雷管反向电流 chipRevIa-18
	
	private int busV;//总线电压
    private String denaId;//雷管id
    private String facCode;//管厂码
    private String feature;//特征码
    
    private String delayWriteStatus;//延时写入状态
    private String shiptestStatus;//芯片检测状态
    
    private int  delayTime;//延时
    private int isExchangeCir;//充电回路
    private int isDischageCir;//放电回路
    private int isGetFireCir;//发火回路
    private int isDelaySet;//延时设置
    private int isDelaySyn;//延时同步
    private int isReadId;//读取Id
    
    public String getChsString(){
    	return "雷管id="+this.denaId+",管厂码="+this.facCode+",特征码="+this.feature+",芯片状态="+this.chipBaseStatus+",芯片电流="+this.chipPlusIa
    			+",芯片反向电流="+this.chipRevIa+",总线电压="+this.busV+",延时写入状态="+this.delayWriteStatus
    			+",芯片检测状态="+this.shiptestStatus+",延时="+this.delayTime;
    }
    
    public int getDePlusIa() {
    	
    	if(chipPlusIa- emptyIa<0)return 0;
    	else return chipPlusIa- emptyIa;
		
	}

	public void setDePlusIa(int dePlusIa) {
		
		this.dePlusIa = chipPlusIa- emptyIa;
	}

	public int getDeRevIa() {
		if(chipRevIa- emptyIa<0)return 0;
    	else return chipRevIa- emptyIa;
	}

	public void setDeRevIa(int deRevIa) {
		this.deRevIa = deRevIa;
	}

	private int getChipTestStatus(int pos){   
    	if(shiptestStatus==null)return 0;
    	byte b = Byte.valueOf(shiptestStatus, 16);
    	int i = b>>pos&0x1;
    	return i;
    }
	public String getChipBaseStatus() {
		return chipBaseStatus;
	}
	public String getChipBaseStatusChsName() {
		if("FF".equals(chipBaseStatus)){
			return "正常";
		}else{
			return "异常";
		}
		//return chipBaseStatus;
	}
	public void setChipBaseStatus(String chipBaseStatus) {
		this.chipBaseStatus = chipBaseStatus;
	}
	public int getChipPlusIa() {
		return chipPlusIa;
	}
	public void setChipPlusIa(int chipPlusIa) {
		this.chipPlusIa = chipPlusIa;
	}
	public int getChipRevIa() {
		return chipRevIa;
	}
	public void setChipRevIa(int chipRevIa) {
		this.chipRevIa = chipRevIa;
	}
	public int getBusV() {
		return busV;
	}
	public void setBusV(int busV) {
		this.busV = busV;
	}
	public String getDenaId() {
		return denaId;
	}
	public void setDenaId(String denaId) {
		this.denaId = denaId;
	}
	public String getFacCode() {
		return facCode;
	}
	public void setFacCode(String facCode) {
		this.facCode = facCode;
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	public String getDelayWriteStatus() {
		return delayWriteStatus;
	}
	public void setDelayWriteStatus(String delayWriteStatus) {
		this.delayWriteStatus = delayWriteStatus;
	}
	public String getShiptestStatus() {
		return shiptestStatus;
	}
	public void setShiptestStatus(String shiptestStatus) {
		this.shiptestStatus = shiptestStatus;
	}
	public int getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}
	
	public void setIsExchangeCir(int isExchangeCir) {
		this.isExchangeCir = isExchangeCir;
	}

	public void setIsDischageCir(int isDischageCir) {
		this.isDischageCir = isDischageCir;
	}
	
	

	public void setIsGetFireCir(int isGetFireCir) {
		this.isGetFireCir = isGetFireCir;
	}
	public String getIsExchangeCirName() {
		
		String ch = "";
		if("00".equals(getChipBaseStatus())){
			ch="无芯片";
			return ch;
		}
		if("01".equals(getChipBaseStatus())){
			ch="芯片故障";
			return ch;
		}
		if("02".equals(getChipBaseStatus())){
			ch="电流过大";
			return ch;
		}
		if("FF".equals(getChipBaseStatus())){
			if(getChipTestStatus(0)==1){
				ch = "充电回路正常";
			}else{
				ch = "充电回路异常";
			}
		}		
		return ch;
	}
	public String getIsDischageCirName() {
		String ch = "放电回路异常";
		if(getChipTestStatus(1)==1){
			ch = "放电回路正常";
		}
		return ch;
	}
	public String getIsGetFireCirName() {
		String ch = "发火回路异常";
		if(getChipTestStatus(2)==1){
			ch = "发火回路正常";
		}
		return ch;
	}
	public String getIsDelaySetName() {
		String ch = "未设置延时";
		if(getChipTestStatus(3)==1){
			ch = "已设置延时";
		}
		return ch;
	}
	public String getIsDelaySynName() {
		
		String ch = "未同步校验";
		if(getChipTestStatus(4)==1){
			ch = "已同步校验";
		}
		return ch;
	}
	public String getIsReadIdName() {
		String ch = "未读取";
		if(getChipTestStatus(5)==1){
			ch = "已读取";
		}
		return ch;
	}
	public void setIsDelaySet(int isDelaySet) {
		this.isDelaySet = isDelaySet;
	}
	
	public void setIsDelaySyn(int isDelaySyn) {
		
		this.isDelaySyn = isDelaySyn;
	}
	
	public void setIsReadId(int isReadId) {
		
		this.isReadId = isReadId;
	}

	public int getIsExchangeCir() {
		return getChipTestStatus(0);
	}

	public int getIsDischageCir() {
		return getChipTestStatus(1);
	}

	public int getIsGetFireCir() {
		return  getChipTestStatus(2);
	}
	public int getIsDelaySet() {
		return getChipTestStatus(3);
	}

	public int getIsDelaySyn() {
		return getChipTestStatus(4);
	}

	public int getIsReadId() {
		return getChipTestStatus(5);
	}
    
    
    
}
