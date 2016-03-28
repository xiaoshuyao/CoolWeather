package com.example.coolweather.service;

import com.example.coolweather.WeatherActivity;
import com.example.coolweather.receiver.AutoUpdateReceiver;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
     @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	// TODO Auto-generated method stub
    	 new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();
    	 AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
    	 int anHour=8*60*60*1000;  //8小时
    	 
    	 long triggerAtTime=SystemClock.elapsedRealtime()+anHour;
    	 
    	 Intent i=new Intent(this,AutoUpdateReceiver.class);
    	                                 //？？？参数
    	 PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
    	 manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    	 
    	 
    	  return super.onStartCommand(intent, flags, startId);
    }
	//更新天气
	private void updateWeather(){
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		String cityCode=prefs.getString("city_code", "");
		String address = "https://api.heweather.com/x3/weather?cityid=" + cityCode + "&key=ca4daf7337314503bd1c0c953a972fb4";
	       HttpUtil.sendHttpRequst(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
	               /*  runOnUiThread(new Runnable() {
	                     @Override
	                     public void run() {
	                         showWeather();
	                     } 
	                  */
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				 e.printStackTrace();
			}
		});
			
	 }
}
