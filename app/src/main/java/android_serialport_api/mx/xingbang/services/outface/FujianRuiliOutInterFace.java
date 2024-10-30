package android_serialport_api.mx.xingbang.services.outface;

import java.util.HashMap;
import java.util.List;

import android_serialport_api.mx.xingbang.services.SyncHttp;
import android_serialport_api.mx.xingbang.services.constant.FujianRuiliConstant;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class FujianRuiliOutInterFace {

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

		SyncHttp syncHttp = new SyncHttp();
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
}
