package com.chenhp.heartattack;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class HttpRequest {
	public static void heartAttack(String username,String password){
		//先获得初使cookie;
		String urlGetUserCookie = "http://172.18.8.254:8080/portal/index_default.jsp";
		String getUserCooie = "hello1=chenhongp; hello2=false; hello3=; hello4=; hello5=";
		String res0[] = HttpRequest.request(urlGetUserCookie, getUserCooie, null);
		String url = "http://172.18.8.254:8080/portal/pws?t=li";
		String cookie =res0[1] + ";" + getUserCooie;
		System.out.println("cookie:" + cookie);
		String body = 
				"userName="+ username +
				"&userPwd=" + ToolCommonFunction.escape(Base64.encode(password.getBytes())) +
				"&serviceType=" +
				"&userurl=" +
				"&userip=" +
				"&basip=" + 
				"&language=Chinese" +
				"&portalProxyIP=172.18.8.254" + 
				"&portalProxyPort=50200" +
				"&dcPwdNeedEncrypt=1"+
				"&assignIpType=0&appRootUrl=http%3A%2F%2F172.18.8.254%3A8080%2Fportal%2F&manualUrl=&manualUrlEncryptKey=rTCZGLy2wJkfobFEj0JF8A%3D%3D"
		;
		String res1[] = HttpRequest.request(url, cookie, body);
		System.out.println("cookie" + res1[0]);
		System.out.println("body" + res1[1]);
	}
	public static String[] request(String urlstr, String cookie, String requestBody,boolean flag) {
		String response[] = new String[2];
		URL url = null;
		HttpURLConnection urlc = null;
		try {
			url = new URL(urlstr);
			urlc = (HttpURLConnection) url.openConnection();
			if (cookie != null) {
				urlc.setRequestProperty("Cookie", cookie);
			}
			if (true) {

				urlc.setRequestProperty("x-requested-with", "XMLHttpRequest");
				urlc.setRequestProperty("Accept-Language", "zh-cn");
				urlc.setRequestProperty("Accept", "text/plain, */*; q=0.01");
				urlc.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				urlc.setRequestProperty("Accept-Encoding", "gzip, deflate");
				urlc.setRequestProperty("Host", "172.18.8.254:8080");
				urlc.setRequestProperty(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; InfoPath.2; .NET4.0E)");
				urlc.setRequestProperty("Host", "172.18.8.254:8080");
				urlc.setRequestProperty("Connection", "Keep-Alive");
			}
			urlc.setRequestMethod("POST");
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			// urlc.setReadTimeout(5000);
			if (requestBody != null && !requestBody.equals("")) {
				byte[] bytes = requestBody.getBytes("UTF-8");
				urlc.getOutputStream().write(bytes);
			} 
			InputStream inStream = urlc.getInputStream();
			if(flag = false){
				response[0] = inputStream2String(inStream,false);
			}else{
				response[0] = inputStream2String(inStream,true);
			}
			response[1] = urlc.getHeaderField("Set-Cookie");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (urlc != null) {
					urlc.disconnect();
					urlc = null;
				}
				if (url != null) {
					url = null;
				}
			} catch (Exception e) {
			}
		}
		return response;
	}
	public static String[] request(String urlstr, String cookie, String requestBody) {
		return request(urlstr, cookie, requestBody,false);
	}
	public static String inputStream2String(InputStream is, boolean flag)
			throws Exception {
		java.util.zip.GZIPInputStream gzstream = null;
		try {
			if (!flag) {
				return inputStream2String(is);
			}
			StringBuffer out = new StringBuffer();
			gzstream = new GZIPInputStream(is);
			byte[] b = new byte[2048];
			for (int n; (n = gzstream.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
			return out.toString();
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (Exception e) {
			}
			try {
				if (gzstream != null) {
					gzstream.close();
					gzstream = null;
				}
			} catch (Exception e) {

			}
		}
	}
	public static String inputStream2String(InputStream is) throws Exception {
		try {
			StringBuffer out = new StringBuffer();
			byte[] b = new byte[1024];
			for (int n; (n = is.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
			return out.toString();
		} catch (Exception e) {
			return null;
		} finally {
			try {
				is.close();
				is = null;
			} catch (Exception e) {
			}
		}
	}
	
}