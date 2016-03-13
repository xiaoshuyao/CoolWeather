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
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HttpURLConnection connection=null;
				try{
				URL url=new URL(address);
				connection=(HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(8000);
				connection.setReadTimeout(8000);
				
				InputStream in=connection.getInputStream();
				
				BufferedReader reader=new BufferedReader(new InputStreamReader(in));
				String line;
				//response 对象??？
				StringBuilder response=new StringBuilder();  
				while((line=reader.readLine())!=null){
					response.append(line);
				}
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
