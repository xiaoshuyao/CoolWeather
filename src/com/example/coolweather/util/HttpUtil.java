package com.example.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import android.drm.DrmManagerClient.OnErrorListener;

public class HttpUtil {

	
	public static void sendHttpRequst(final String address,final HttpCallbackListener listener){
	
		new Thread(new Runnable() {  //开启子线程 但子线程内无法return返回数据
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HttpURLConnection connection=null;
				try{
				URL url=new URL(address);
				connection=(HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setConnectTimeout(8000);
				connection.setReadTimeout(8000);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				
				InputStream in=connection.getInputStream();
				//response 对象??？
				StringBuilder response=new StringBuilder();  
				
				
				BufferedReader reader=new BufferedReader(new InputStreamReader(in, "UTF-8"));
				String line;
				while((line=reader.readLine())!=null){
					response.append(line);
					//response.append("\r\n");
				}
				reader.close();
				if(listener!=null){
					//回调onFinish方法
					listener.onFinish(response.toString());
				}
		     }catch(Exception e){
		    	 if(listener!=null){
		    	   //回调onError方法
		    		 listener.onError(e);
		    	 }
		     }finally{
		    	 if (connection!=null) {
					connection.disconnect();
				}
		     }
		  }
		}).start();
		
	}
	
}