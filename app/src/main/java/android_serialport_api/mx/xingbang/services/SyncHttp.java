package android_serialport_api.mx.xingbang.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


public class SyncHttp
{
	
	
	public String httpGet(String url, String params) throws Exception
	{
		String response = null; //返回信息
		//拼接请求URL
		if (null!=params&&!params.equals(""))
		{
			url += "?" + params;
		}
		
		int timeoutConnection = 3000;  
		int timeoutSocket = 5000;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
	    
		// 构�?HttpClient的实�?
		HttpClient httpClient = new DefaultHttpClient(httpParameters);  
		// 创建GET方法的实�?
		//URLEncoder.encode(url,"UTF-8");

		HttpGet httpGet = new HttpGet(url);
		try
		{
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) //SC_OK = 200
			{
				// 获得返回结果
				response = EntityUtils.toString(httpResponse.getEntity());
				System.out.println(response);
			}
			else
			{
				response = "返回码："+statusCode;
			}
		} catch (Exception e)
		{
			throw new Exception(e);
		}
		finally{
			if (httpClient != null && httpClient.getConnectionManager() != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}


		return response;
	}

	/**
	 * 通过POST方式发请求
	 * @param url URL地址
	 * @param params 参数
	 * @return
	 * @throws Exception
	 */
	public String httpPost(String url, List<Parameter> params)
	{
		String response = null;
		int timeoutConnection = 3000;  
		int timeoutSocket = 5000;  
		HttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);  
		// 构�?HttpClient的实�?
		HttpClient httpClient = new DefaultHttpClient(httpParameters);  
		HttpPost httpPost = new HttpPost(url);
		//使用execute方法发�?HTTP Post请求，并返回HttpResponse对象
		HttpResponse httpResponse;

		try {
			if (params.size()>=0)
			{
				//设置httpPost请求参数
				httpPost.setEntity(new UrlEncodedFormEntity(buildNameValuePair(params),HTTP.UTF_8));
			}
			
			httpResponse = httpClient.execute(httpPost);
		
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode==HttpStatus.SC_OK)
			{
				//获得返回结果
				response = EntityUtils.toString(httpResponse.getEntity());
			}
			else
			{
				response = "返回码："+statusCode;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (httpClient != null && httpClient.getConnectionManager() != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		
		return response;
	}
	
	/**
	 * 把Parameter类型集合转换成NameValuePair类型集合
	 * @param params 参数集合
	 * @return
	 */
	private List<BasicNameValuePair> buildNameValuePair(List<Parameter> params)
	{
		List<BasicNameValuePair> result = new ArrayList<BasicNameValuePair>();
		for (Parameter param : params)
		{
			BasicNameValuePair pair = new BasicNameValuePair(param.getName(), param.getValue());
			result.add(pair);
		}
		return result;
	}
	
	public static String jsonPost(String strURL,Map<String, String> params){
		String data ="";//JSONUtil.toJSON(params);
		//data = "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";
		//data = "{\"uid\":\"5980503106B44,598050311A46B,59805030B63AD,59805030BBB2F,59805030BCCF8,59805030B6AE0,59805030BE740,5980503120DD0,598050311D89B,5980503107244,59805030FCED6,5980503107B78,59805030FD050,59805031196B0,598050311C27F,598050310776E,59805030BC74E,59805030BC7E9,59805030BEAFA,59805030B6095,59805031220E2,59805030B6D8C,5980503127A1D,5980503118E36,5980503127CDF,598050311CB5D,598050311190C,5980503115F8D,59805031062B5,5980503101E6D,598050311912B,59805030FDF53,5980503102F07,5980503125A16,5980503105862,598050311E4E6,5980503122F4C,5980503126082,5980503107FF2,59805031229CD,598050312159B,59805031025DC,5980503109218,5980503108BCB,5980503119C67,598050311DAF3,5980503122C2C,59805030FA033,598050311BD0E,5980503103B5E,598050310C3EB,5980503127A7C,598050311C69E,5980503118497,5980503106BAA,598050310732E,5980503106229,598050311DCC8,598050310CEEB,5980503110065,5980503115AFA,5980503119119,5980503106908,5980503121241,598050317BDAC,5980503120607,598050310BC05,5980503118821,59805031100E9,5980503127D3F,5980503115FAB,598050310C30B,5980503108D95,5980503100513,5980503124F8E,59805030FAE96,5980503104287,5980503115D98,59805031086E3,5980503119457,598050311C9BE,5980503115F41,5980503127B31,59805031107BE,59805031214EE,5980503119234,59805031116BC,5980503127241,59805030FD872,598050311E092,59805031101B6,5980503127D67,5980503118CC5,5980503127F5C,5980503103AC2,5980503103844,5980503120268,5980503101638,59805031206DE,598050312282C\",\"sbbh\":\"F6400000029\",\"jd\":\"115.487789\",\"bpsj\":\"2018-06-01 17:47:04\",\"xmbh\":\"522400X18050010\",\"wd\":\"37.764935\",\"bprysfz\":\"522121196410065631\",\"htid\":\"\"}";
		
		String requestStr = jsonPost(strURL,data);
		System.out.println(requestStr);
		return requestStr;
	}
	/**
	 * 发送HttpPost请求
	 * 
	 * @param strURL
	 *            服务地址
	 * @param params
	 * 
	 * @return 成功:返回json字符串<br/>
	 */
	public static String jsonPost(String strURL,String postData) {
		try {
			//postData = "oEWm475y+ld+VtBLcs7V1qaz+fz9toy6x/elA52nIV8PN3jxgnzjbFhsgPNzGH+arIwwcrrS0ACoOVj2C6/Xk6GAay1CkrLv5zhGumlAg1Zsy7gDYWfeFqDEDEd7r28Q13wOMesDNOiUva9dPAtLbQqhfrgVkAcVflbQS3LO1da2sLAxw7sBUFmpLDtj9BDB4RjIsbVk6JPb6yJ176ovQzPFM9zO+BvoAc+b14a9AWOhgGstQpKy7/gNJlFdp9fG3BecQfQHJ6yQr0Yzewf+OMQ5DPPkGJPa2PWPhPXFDiC4hz7WbpJEi6GAay1CkrLvTm2hXyNc861fiwc0djWyMGu7P6pUYJpJcxyx4os6UH5tbk+QryIt8l6kV1GXpMihflbQS3LO1dYZy1VU2mAg5WzLuANhZ94W3+ho35gOstKpLfBerOlIK6yMMHK60tAADcrWrkOwcnF+VtBLcs7V1mJepIkhPvAzW7Z6wmhJqIKfb72cOZGKLryt0kvJwbvF0UZWo8OtvvgjSwxyeYfvRX5W0EtyztXWoaQW6L6tApzUDM2yi43iuZ5fRHm1LpD8geEdwsPRt59ZdzcpBrhfesnjpbKZ5Nd9flbQS3LO1daJ5oADS5sXUgnROVhKCOlJYGqrXYdpBPBsZ8bpqi4gQvaHWvWD++8ZcUgbq04VSBJ+VtBLcs7V1ulfbqt+oZOWk0ewbQKHlivbMTJHwl5w/gjF+n0qCePf9oda9YP77xmqZfTvE99kOH5W0EtyztXWO5lzJh3jt66Omjk7WdPyjsmiKZIgc5qYdXpK8eAcpKPjt0E5OZcVjPxLpadYzBZuflbQS3LO1dYfmdxfz+rfl//V1FHi0En1CvKwFU26wl2kx6J79pAgR9FGVqPDrb74ZQ/xynA3vDx+VtBLcs7V1mLPXOtWsxdx6Gn+zKEftLxHS59jrijuZep5SQAgxtFEWXc3KQa4X3rHz6JU/ePhvn5W0EtyztXWFwm0yeAw+BMdkZ3WZnszIMP0yUTGuLpTUhVYGTm1aG5tbk+QryIt8v917y2+8c8xflbQS3LO1db6/pDzZuPkxv/V1FHi0En1HdvHKHkrEuRRVGyDEz5BzFl3NykGuF966xSLeeWDF/J+VtBLcs7V1hzbs3MVgJOK3BecQfQHJ6wb7kLOjXDr9Tp5r7Ga9S4x47dBOTmXFYzx3j9FOVh++35W0EtyztXWbuKjvVX19JG3O5y5guxb7qKK01Ch6Bc4xII83dDVwredbSnW4cb5eSpFEsYzjNrPflbQS3LO1dZU9e17TFQO6JbDV3IpdwW1zp9YlE4ORdjqskxQrdpWXFl3NykGuF964S09dbtChFd+VtBLcs7V1rWjcxAYuBu98VYm0Y6akO+kq4x8cKShiHbINXMr2HGpw882a2SIiGkaDP4EaFVasX5W0EtyztXW7yyutwY7eKxl4S+uuTrYMIGGork4Z+axXqFpZWroZRqaNp14xex2dI90hO4HLfWoflbQS3LO1dZ8w8wzYmiBnmXhL665OtgwaPTAbuaRsDWe2t8EA+i6KrW2hIT9TU8vqNEBAcjlj1t+VtBLcs7V1q3PMzdwl4bq3BecQfQHJ6wQtiVXK+1g6PfKjxOc88hE0UZWo8OtvvhC85KUCFbnuqGAay1CkrLvzV4uPN0Qf3bt+hTo5pXcYZCdT2TdGGFJoEEWSFRzNyIzxTPczvgb6N0vCS/0IXqFflbQS3LO1dad3VN4+MQf25bDV3IpdwW1RGckjIN6X8zCDqpvpof/2/aHWvWD++8ZqmesgRKBO2t+VtBLcs7V1t/9C73QnLOF1AzNsouN4rlSfcm9WVMeHPYDHU/ByK5zw882a2SIiGmnGxHB1c1ASvs3vABYFL2XiH3AXiPt4d+4oJoA1xyZIP30yf5AxDiPAgaLQabTA7/TLKG603UPalezM3n55a4w6bnDSpSyWD2wA0reRYEv2j1xPB0/EDHuFYkTno2gQHQ647Tt4IrYqq6voL70U9k6Pk9XzQqa1xuHR+w3Tswo49y6a94kjnHcefJjyDujILZNJZOdN0F5Ei7iSCEfoB2o7DMZGVScMi0=";
			URL url = new URL(strURL);// 创建连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST"); // 设置请求方式
			connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
			connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
			connection.connect();
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
			out.append(postData);
			out.flush();
			out.close();
 
			int code = connection.getResponseCode();
			InputStream is = null;
			if (code == 200) {
				is = connection.getInputStream();
			} else {
				is = connection.getErrorStream();
			}
 
			// 读取响应
			int length = (int) connection.getContentLength();// 获取长度
			if (length != -1) {
				byte[] data = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, data, destPos, readLen);
					destPos += readLen;
				}
				String result = new String(data, "UTF-8"); // utf-8编码
				return result;
			}
 
		} catch (IOException e) {
			//LOG.error("Exception occur when send http post request!", e);
		}
		return "error"; // 自定义错误信息
	}

}
