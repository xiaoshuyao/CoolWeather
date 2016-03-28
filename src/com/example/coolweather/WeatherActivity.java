package com.example.coolweather;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.service.AutoUpdateService;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
 
        private LinearLayout weatherInfoLayout;	
 	      
	    private TextView cityNameText;

	     
	    private TextView publishText;

	    private TextView weatherDespText;

	    private TextView temp1Text;
	    private TextView temp2Text;
	    
	    private TextView windText;

	   
	    private TextView currentDateText;

	    private Button switchCity;

	    private Button refreshWeather;

	   private String county = null;
	    
	    

	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.weather_layout);
	        //初始化控件
	        cityNameText = (TextView) findViewById(R.id.city_name);
	        publishText = (TextView) findViewById(R.id.publish_text);
	        weatherDespText = (TextView) findViewById(R.id.weather_desp);
	        temp1Text = (TextView) findViewById(R.id.temp1);
	        temp2Text=(TextView) findViewById(R.id.temp2);
	        windText = (TextView) findViewById(R.id.wind);
	        currentDateText = (TextView) findViewById(R.id.current_date);
	        switchCity = (Button) findViewById(R.id.switch_city);
	        refreshWeather = (Button) findViewById(R.id.refresh_weather);
	        String countyCode =getIntent().getStringExtra("city_code");
	        //Log.i(countyCode, "countyname==="+countyCode);
	       // String countyCode=CoolWeatherDB.getInstance(this).searchcityCode(countyName);
	        //Log.i(countyCode, "countyCode==="+countyCode);
		       
	        
	       
	       
	        
	       // if (!TextUtils.isEmpty(countyCode)) {
	            //有县级代号就去查询天气
	            publishText.setText("同步中...");
	            //??
	           
	            queryWeatherCode(countyCode);
	      //  } else {
	            //没有县级代号天气就直接显示当地天气
	            
	        	showWeather();
	        	publishText.setText(" ");
	     //   }
	        switchCity.setOnClickListener(this);
	        refreshWeather.setOnClickListener(this);
	    }

	    @Override
	    public void onClick(View v) {
	        switch (v.getId()) {
	            case R.id.switch_city:
	                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
	                 intent.putExtra("from_weather_activity", true);
	                startActivity(intent);
	                finish();
	                break;
	            case R.id.refresh_weather:

	            /*	publishText.setText("同步中...");
	            	SharedPreferences prefs = PreferenceManager.
	            	getDefaultSharedPreferences(this);
	            	String weatherCode = prefs.getString("weather_code", "");
	            	if (!TextUtils.isEmpty(weatherCode)) {
	            	queryWeatherInfo(weatherCode);
	            	}
	            	*/
	            	publishText.setText("同步中...");
	                queryWeatherCode(county);
	                SimpleDateFormat formatter = new SimpleDateFormat ("MM月dd日 HH:mm:ss ");
	                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
	                String str = formatter.format(curDate);
	                publishText.setText(str+"刷新");
	                break;
	            default:
	                break;
	        }
	    }

	    /**
	     * 查询县级代号所对应的天气代号
	     */
	    private void queryWeatherCode(String countyCode) {
	    	//String address = "https://api.heweather.com/x3/weather?cityid="+countyCode+"&key=ca4daf7337314503bd1c0c953a972fb4"; 
	    	String address = "https://api.heweather.com/x3/weather?cityid=" + countyCode + "&key=ca4daf7337314503bd1c0c953a972fb4";
	        queryFromServer(address, "city_code");
	        //String address = "https://api.heweather.com/x3/weather?city=" + countyCode + "&key=ca4daf7337314503bd1c0c953a972fb4";
	       // queryFromServer(address, "county_code");
	    }

	  /* private void queryWeatherInfo(String weatherCode) {
	    	
	    	String address = "http://www.weather.com.cn/data/cityinfo/" +
	    			weatherCode + ".html";
	    	//String address = " https://api.heweather.com/x3/condition?search="+weatherCode+"&key=ca4daf7337314503bd1c0c953a972fb4";
	    	queryFromServer(address, "weatherCode");
	    	}
	  */
	    private void queryFromServer(final String address, final String type) {
	        HttpUtil.sendHttpRequst(address, new HttpCallbackListener() {
	            @Override
	            public void onFinish(String response) {  //新加
	            	//if("countyCode".equals(type)){
	            	if (!TextUtils.isEmpty(response)) {
	            		/*	String[] array=response.split("\\|");
	            			if(array!=null&&array.length==2){
	            				String weatherCode=array[1];
	            				queryWeatherInfo(weatherCode);
	            			 }
	            		  
	            		}else if("weatherCode".equals(type)){
	            			//从服务器返回的数据中解析出天气
	            		 */
	            			 Utility.handleWeatherResponse(WeatherActivity.this, response);
	 	                    runOnUiThread(new Runnable() {
	 	                        @Override
	 	                        public void run() {
	 	                            showWeather();
	 	                        } 
	 	                      
	                    });
	                }
	            }


	            @Override
	            public void onError(Exception e) {
	                runOnUiThread(new Runnable() {
	                    @Override
	                    public void run() {
	                        publishText.setText("同步失败");
	                    }
	                });
	            }
	        });
	    }

	    
	    private void showWeather() {
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	        cityNameText.setText(preferences.getString("city_name", ""));

	      // String temp = preferences.getString("temp1","")+"~"+preferences.getString("temp2","");;
	       temp1Text.setText(preferences.getString("tmp1", "")+"℃");
	       temp2Text.setText(preferences.getString("tmp2", "")+"℃");
	        
	       Log.i("temp","temp=  "+preferences.getString("tmp1", null) );
	       /* weatherDespText.setText(preferences.getString("weather_desp", ""));
	        publishText.setText("今天" + preferences.getString("publish_time", "") + "发布");
	        currentDateText.setText(preferences.getString("current_date", ""));
	        weatherInfoLayout.setVisibility(View.VISIBLE);
	        cityNameText.setVisibility(View.VISIBLE);
	       */
	       // tempText.setText(temp);
	        String wind =preferences.getString("windDirection", "")+"--"+preferences.getString("windLevel","")+"级";
	        windText.setText(wind);

	        currentDateText.setText(preferences.getString("current_date", ""));
	        String weatherNow = preferences.getString("weather_now","");
	        String weatherNext= preferences.getString("weather_next", "");
	        if (weatherNow.equals(weatherNext)){
	            weatherDespText.setText(weatherNow);
	        }else {
	            weatherDespText.setText(weatherNow+"转"+weatherNext);
	        }
	        
	        Intent intent=new Intent(this,AutoUpdateService.class);
	        startService(intent);
	    }
	}
	

