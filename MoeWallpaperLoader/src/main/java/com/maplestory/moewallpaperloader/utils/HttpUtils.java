package com.maplestory.moewallpaperloader.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;



public class HttpUtils {

	public HttpUtils() {
		// TODO Auto-generated constructor stub
	}
	public static String getContent(String strUrl)
	 // 一个public方法，返回字符串，错误则返回"error open url"
	 {
	  try{
	   URL url=new URL(strUrl);
	   BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
	   String s="";
	   StringBuffer sb=new StringBuffer("");
	   while((s=br.readLine())!=null)
	   {     
	    sb.append(s+"\r\n");    
	//    System.out.println("add new line");
	   }
	   br.close();
	   return sb.toString();
	  }
	  catch(Exception e){
		  e.printStackTrace();
	   return "error open url" + strUrl;
	   
	  }  
	 }
	public static boolean httpDownload(String httpUrl,String saveFile){  
	       // 下载网络文件  
	       int bytesum = 0;  
	       int byteread = 0;  
	  
	       URL url = null;  
	    try {  
	        url = new URL(httpUrl);  
	    } catch (MalformedURLException e1) {  
	        // TODO Auto-generated catch block  
	        e1.printStackTrace();  
	        return false;  
	    }  
	  
	       try {  
	           URLConnection conn = url.openConnection();  
	           InputStream inStream = conn.getInputStream();  
	           FileOutputStream fs = new FileOutputStream(saveFile);  
	  
	           byte[] buffer = new byte[1024];  
	           while ((byteread = inStream.read(buffer)) != -1) {  
	               bytesum += byteread;  
	     //          System.out.println(bytesum);  
	               fs.write(buffer, 0, byteread);  
	           }  
	           return true;  
	       } catch (FileNotFoundException e) {  
	           e.printStackTrace();  
	           return false;  
	       } catch (IOException e) {  
	           e.printStackTrace();  
	           return false;  
	       }  
	   }
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
			String str = matcher.group().substring(14, matcher.group().length()-1);
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
	
	
	
	
	
}
