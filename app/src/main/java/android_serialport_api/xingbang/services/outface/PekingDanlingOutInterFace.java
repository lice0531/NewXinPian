package android_serialport_api.xingbang.services.outface;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.xingbang.services.SyncHttp;
import android_serialport_api.xingbang.services.constant.FujianRuiliConstant;

import android_serialport_api.xingbang.utils.ThreeDES;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class PekingDanlingOutInterFace {

	public static  SyncHttp syncHttp = new SyncHttp();
	public static String key = "jadl12345678912345678912";
	
	public static String testDenatorToJson(String sbbh,String jd,String wd,String bpsj,String bprysfz,String htid,String xmbh,List<String> uidList){
		String  uid="";//"6070309506100,6070309506101,6070309506102,6070309506103,6070309506104";
		for(int i=0;i<uidList.size();i++){
			uid+=","+uidList.get(i);
		}
		if(uid!=null&&uid.length()>0)uid=uid.substring(1);
		
		testDenatorToJson(sbbh, jd, wd, bpsj, bprysfz, htid, xmbh, uid);
		JSONObject json = new JSONObject();
		json.put("sbbh", sbbh);
		json.put("jd", jd);
		json.put("wd", wd);
		json.put("bpsj", bpsj);
		json.put("bprysfz", bprysfz);
		json.put("uid", uid);
		json.put("htid", htid);
		json.put("xmbh", xmbh);
		
		return json.toString();
	}
	/***
	 * 
	 * @param sbbh//起爆器设备编号
	 * @param jd//经度
	 * @param wd//纬度
	 * @param bpsj//爆破时间
	 * @param bprysfz//爆破人员身份证
	 * @param htid//合同ID
	 * @param xmbh//项目编号
	 * @param uid//雷管UID
	 * @return
	 */
	public static String testDenatorToJson(String sbbh,String jd,String wd,String bpsj,String bprysfz,String htid,String xmbh,String  uid){
		
		JSONObject json = new JSONObject();
		json.put("sbbh", sbbh);
		json.put("jd", jd);
		json.put("wd", wd);
		json.put("bpsj", bpsj);
		json.put("bprysfz", bprysfz);
		json.put("uid", uid);
		json.put("htid", htid);
		json.put("xmbh", xmbh);
		
		return json.toString();
	}
	
	 public static void main(String args[]) throws Exception { 
		 //testUp();	
		 testDown();
	 } 
	 public static void testUp(){
		 String  baseUrl="http://139.129.216.133:8080/mbdzlgtxzx/servlet/DzlgSysbJsonServlert";
		 String sbbh ="XBTS0001";//起爆器设备编号
		 String jd ="117.585984";//经度
		 String wd="36.445464";//纬度
		 String bpsj ="2018-12-03 22:50:18";//爆破时间
		 
		 String bprysfz ="130682199606071234";//爆破人员身份证
		 String uid = "";//雷管UID
		 String htid = "520101181218002";//合同ID
		 String xmbh = "520100X18121802";//项目编号
		 List<String> dataList = new ArrayList<String>();
		 dataList.add("6070309506100");
		 dataList.add("6070309506101");		 
		 dataList.add("6070309506102");
		 dataList.add("6070309506103");
		 dataList.add("6070309506104");
		 PekingDanlingOutInterFace.upFireToReport(baseUrl, sbbh, jd, wd, bpsj, bprysfz, htid, xmbh, dataList);
	 }
	 public static int upFireToReport(String baseUrl,String sbbh,String jd,String wd,String bpsj,String bprysfz,String htid,String xmbh,List<String> dataList){
		 String dataTest = PekingDanlingOutInterFace.testDenatorToJson(sbbh, jd, wd, bpsj, bprysfz, htid, xmbh, dataList);
         String data = "";
	     try {
				data = ThreeDES.encryptThreeDESECB(dataTest, key);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		 }
       try {
			data =URLEncoder.encode(data,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return upBlastInfo(baseUrl,"param="+data);
	 }
	 public static String decodeFromCode(String params){
	     String data = "";
	     
	     try {
	    	 data = ThreeDES.decryptThreeDESECB(params, key);
	    	   data =URLDecoder.decode(data,"utf-8");
			   			                                       
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		 }
	     return data;
	 }
	 
	 public static String getBlastStrFromList(List<String> dataList){
		 String str="";
		 if(dataList!=null&&dataList.size()>0){
			 for(String v:dataList){
				 str+=","+v;
			 }
			 str = str.substring(1);
		 }
		 return str;
	 }

	 public static String Des3Encrpyt(String srcCode,String key) throws Exception{
		 			
			System.out.println(srcCode);
			String encode = ThreeDES.encryptThreeDESECB(srcCode, key);
			System.out.println(encode);
			return encode;
	 }
	 public static String Des3Decodecrpyt(String encode,String key) throws Exception{
		 
			String decode = ThreeDES.decryptThreeDESECB(encode, key);
			System.out.println(decode);
			return decode;
	 }

	 public static JSONObject getHttpDataToJson(String url,String params){
		 //SyncHttp syncHttp = new SyncHttp();
			try{				
				String retStr = syncHttp.httpGet(url, params);
				if(retStr==null||retStr.trim().length()<1){
					return null;
				}
				JSONObject jsonObject =  JSONObject.fromObject(retStr);
				return jsonObject;
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
	 }
	 public static String getHttpDataToString(String url,String params){
		 //SyncHttp syncHttp = new SyncHttp();
			try{				
				String retStr = syncHttp.httpGet(url, params);
				return retStr;
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
	 }
	 public static int upBlastInfo(String url,String params){
		 
		JSONObject jsonObject =  getHttpDataToJson(url,params);
		
		String retCode = jsonObject.getString("success");
		if (retCode!=null&&retCode.equals("true")){
			return 0;
		}
		else{
			return 1;
		}
	 }
	 
	 public static int getWorkCodes(String url,String params){
		 
			String reVal =  getHttpDataToString(url,params);
			//String retCode = jsonObject.getString("cwxx");
			System.out.println(reVal);
			reVal = decodeFromCode(reVal);
			System.out.println(reVal);
			return 1;
		 }
	/***
	 * 
	 * @param interPro
	 * @return
	 */
	public static int getBlastList(int interPro,String blastNo,List<HashMap<String, Object>> itemList) {

		blastNo ="67281707346562";
		//
		String url = "http://125.77.73.145:21280/test/entweb/mbapi.do?action=getBlastList" + "getBlastList";
		String params = "";
		params = "serialNumber="+blastNo;

		//SyncHttp syncHttp = new SyncHttp();
		try{
			//
			String retStr = syncHttp.httpGet(url, params);
			if(retStr==null||retStr.trim().length()<1){
				return -1;
			}
			JSONObject jsonObject =  JSONObject.fromObject(retStr);
			//
			String retCode = jsonObject.getString("isSuccess");
			if (retCode!=null&&retCode.equals("true")){
					JSONArray newslist = jsonObject.getJSONArray("result");
					for(int i=0;i<newslist.size();i++){
						JSONObject newsObject = (JSONObject)newslist.opt(i); 
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put(FujianRuiliConstant.ITEM_KEY_ID, newsObject.getInt("id"));
						
						if (newsObject.getString("name")!=null)
							hashMap.put(FujianRuiliConstant.ITEM_KEY_NAME, newsObject.getString("name"));
						else
							hashMap.put(FujianRuiliConstant.ITEM_KEY_NAME,"");
						
						itemList.add(hashMap);
					}
				return -2;
			}
			else{
				return -3;
			}
		} catch (Exception e){
			e.printStackTrace();
			return -3;
		}
	}
	
	/***
	 * 获取工作代码
	 * @param sbbh//起爆器设备编号
	 * @param jd//经度
	 * @param wd//纬度
	 * @param uid//雷管UID
	 * @param htid//合同ID
	 * @param xmbh//项目编号	
	 * @param dwdm //企业代码 
	 * @return
	 */
	public static String getWorkParamToJson(String sbbh,String jd,String wd,String  uids,String htid,String xmbh,String dwdm){
		
		JSONObject json = new JSONObject();
		json.put("sbbh", sbbh);
		json.put("jd", jd);
		json.put("wd", wd);
		json.put("uid", uids);
		json.put("htid", htid);
		json.put("xmbh", xmbh);		
		json.put("dwdm", dwdm);
		
		return json.toString();
	}
	
	public static int getDenatorCodeFromDanling(String baseUrl,String sbbh,String jd,String wd,String htid,String xmbh,String dwdm,List<String> dataList){
		String  uid="";//"6070309506100,6070309506101,6070309506102,6070309506103,6070309506104";
		for(int i=0;i<dataList.size();i++){
			uid+=","+dataList.get(i);
		}
		if(uid!=null&&uid.length()>0)uid=uid.substring(1);

		String dataTest = PekingDanlingOutInterFace.getWorkParamToJson(sbbh, jd, wd,uid, htid, xmbh,dwdm);
        String data = "";
	     try {
				data = ThreeDES.encryptThreeDESECB(dataTest, key);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		 }
      try {
			data =URLEncoder.encode(data,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      return getWorkCodes(baseUrl,"param="+data);
	 }
	
	public static void testDown(){
		 //离线下载地址
		 //String  baseUrl="http://139.129.216.133:8080/mbdzlgtxzx/servlet/DzlgMmlxxzJsonServlert";
		//离线下载地址
		String  baseUrl="http://139.129.216.133:8080/mbdzlgtxzx/servlet/DzlgMmxzJsonServlert";
		 
		 String sbbh ="F20181127";//起爆器设备编号
		 String jd ="117.585984";//经度
		 String wd="36.445464";//纬度
		 String uid = "";//雷管UID
		 String dwdm="3701004200003";		 
		 String htid = "370101318060005";//合同ID
		 String xmbh = "370100X15040026";//项目编号
		 List<String> dataList = new ArrayList<String>();
		 dataList.add("0000060092088");
		 dataList.add("0000061092088");		 
		 dataList.add("0000062092088");
		 dataList.add("0000063092088");
		 dataList.add("0000064092088");
		 /*
		 5680804H02400,
		 5680804H02401,
		 5680804H02402,
		 5680804H02403,
		 5680804H02404,
		 */
		 
		 PekingDanlingOutInterFace.getDenatorCodeFromDanling(baseUrl, sbbh, jd, wd, htid, xmbh, dwdm, dataList);
	 }
}
