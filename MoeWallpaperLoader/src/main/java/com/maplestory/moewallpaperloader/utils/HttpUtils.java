package com.maplestory.moewallpaperloader.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Entity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;




public class HttpUtils {

	public HttpUtils() {
		// TODO Auto-generated constructor stub
	}
/*	public  String getContent(String strUrl)
	 // 一个public方法，返回字符串，错误则返回"error open url"
	 {
	  try {
	//	  initSSLALL();
		  URL url = new URL(strUrl);
		  BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		  String s = "";
		  StringBuffer sb = new StringBuffer("");
		  while ((s = br.readLine()) != null) {
			  sb.append(s + "\r\n");
			  //    System.out.println("add new line");
		  }
		  br.close();
		  return sb.toString();
	  }
	  catch(Exception e){
		  e.printStackTrace();
	   return "error open url" + strUrl;
	   
	  }  
	 }*/

/*	public static String getContent(String strUrl){

		HttpClient httpClient = HttpClientHelper.getHttpClient();
		InputStream content = null;
		try {
			HttpResponse response = httpClient.execute(new HttpGet("https://yande.re/post"));
			content = response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = "";
		System.out.println(content == null);
		StringBuilder total = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(content));
		try {
			while ((line = rd.readLine()) != null) {
                total.append(line+"\r\n");
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return total.toString();
	}*/




	public static int getMaxPageNumber(String htmlString) {
		Pattern pagePattern = Pattern.compile("<a href=(.)*?>[0-9]+</a>");
		Matcher matchMaxPage = pagePattern.matcher(htmlString);
		int maxiumPageNum = 0;
		while(matchMaxPage.find()) {
			String maxPage = matchMaxPage.group();
			int tempMaxPage = Integer.parseInt(maxPage.substring(maxPage.indexOf(">")+1, maxPage.lastIndexOf("<")));
			if (tempMaxPage > maxiumPageNum){
			maxiumPageNum = tempMaxPage;
				}
		}
		return maxiumPageNum;
	}
	
	public static ArrayList<ImageProfile> getNewImageValues(String htmlString){
		ArrayList<ImageProfile> imageProfile = new ArrayList();
		Pattern imgPattern = Pattern.compile("Post.register\\(\\{.*\\}\\)");
		Matcher matcher = imgPattern.matcher(htmlString);
		while(matcher.find()){
			String str = matcher.group().substring(14, matcher.group().length() - 1);
			ImageProfile ip = new ImageProfile(str);
			imageProfile.add(ip);
		}
		return imageProfile;
	}
	
	public static ArrayList<ImageProfile> loadDefaultImages(int pageNumber)
	{	
		String htmlResult = siteAddressGen.getSiteAddress(pageNumber);
		ArrayList<ImageProfile> ip = getNewImageValues(htmlResult);
		if(ip.size() == 0){
			return null;
		}else {
			return ip;
		}
	}
	
	public static ArrayList<ImageProfile> loadTagImages(int pageNumber,String tags) {
		String htmlResult = siteAddressGen.getSiteAddress(pageNumber, tags);
		ArrayList<ImageProfile> ip = getNewImageValues(htmlResult);
		if(ip.size() == 0){
			return null;
		}else {
			return ip;
		}
	}




	/**
	 * HttpUrlConnection支持所有Https免验证，不建议使用
	 *
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public void initSSLALL() throws KeyManagementException, NoSuchAlgorithmException, IOException {
		URL url = new URL("https://yande.re/post");
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, new TrustManager[]{new TrustAllManager()}, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(false);
		connection.setRequestMethod("GET");
		connection.connect();
		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = "";
		StringBuffer result = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		Log.e("TTTT", result.toString());
	}

	/**
	 * HttpClient方式实现，支持所有Https免验证方式链接
	 *
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getContent(String strUrl)  {
	    String returnString = "";
		int timeOut = 30 * 1000;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, timeOut);
		HttpConnectionParams.setSoTimeout(param, timeOut);
		HttpConnectionParams.setTcpNoDelay(param, true);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);
		DefaultHttpClient client = new DefaultHttpClient(manager, param);

		HttpGet request = new HttpGet(strUrl);
		// HttpGet request = new HttpGet("https://www.alipay.com/");
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		try {
			returnString =  EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnString;
	}





	public class TrustAllManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}




