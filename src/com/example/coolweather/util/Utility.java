package com.example.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coolweather.model.City;
import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

import android.R.string;
import android.app.Fragment.SavedState;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;

public class Utility {
    //解析和处理服务器返回的省级数据 Json
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response)
	{        //???
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(",");
			if ( allProvinces !=null && allProvinces.length>0) {
				for(String p:allProvinces){//加强for循环
					String[] array=p.split("\\|");
				Province province =new Province();
				province.setprovincCode(array[0]);
				province.setprovinceName(array[1]);
				//解析出来的数据存到Province类
				coolWeatherDB.saveProvince(province);
				}
				return true;
			}	
		}
		    return false;
	}
	
    //解析和处理服务器返回的市级数据 Json
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response)
	{    
		//String response="{\"city_info\":[{\"city\":\"南子岛\",\"cnty\":\"中国\",\"id\":\"CN101310230\",\"lat\":\"11.26\",\"lon\":\"114.20\",\"prov\":\"海南\"},{\"city\":\"北京\",\"cnty\":\"中国\",\"id\":\"CN101010100\",\"lat\":\"39.904000\",\"lon\":\"116.391000\",\"prov\":\"直辖市\"},{\"city\":\"海淀\",\"cnty\":\"中国\",\"id\":\"CN101010200\",\"lat\":\"39.590000\",\"lon\":\"116.170000\",\"prov\":\"直辖市\"},{\"city\":\"朝阳\",\"cnty\":\"中国\",\"id\":\"CN101010300\",\"lat\":\"39.570000\",\"lon\":\"116.290000\",\"prov\":\"直辖市\"}],\"status\":\"ok\"}";
		
		// if(!TextUtils.isEmpty(response)){
	
		/*	if(!TextUtils.isEmpty(response)){
				String[] allCities=response.split(",");
				if ( allCities !=null && allCities.length>0) {
					for(String c:allCities){//加强for循环
						String[] array=c.split("\\|");
					City city =new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//解析出来的数据存到Province类
					coolWeatherDB.saveCity(city);
					}
		f			return true;
				}
			}
			 return false;
		}
		*/
		try{
			
			JSONObject jsonObject=new JSONObject(response);
			JSONArray jsonArray=jsonObject.getJSONArray("city_info");
			
			for(int i=0;i<50;i++){
			JSONObject city_info=jsonArray.getJSONObject(i);
			String cityName=city_info.getString("city");
			String cityCode=city_info.getString("id");
					 City city =new City();
					city.setCityCode(cityCode);
					 city.setCityName(cityName);
					coolWeatherDB.saveCity(city);
				//saveCityInfo(context, cityName,cityCode);	
			}
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		return true;
}

	public static void saveCityInfo(Context context, String cityName,String cityCode){
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("city_name", cityName);
		editor.putString("city_code", cityCode);
		editor.commit();
	}	 
	
	
	
    //解析和处理服务器返回的县级数据 Json
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId)
	{  
		
	
	       //???
		if(!TextUtils.isEmpty(response)){
			String[] allCounties=response.split(",");
			if ( allCounties !=null && allCounties.length>0) {
				for(String c:allCounties){//加强for循环
					String[] array=c.split("\\|");
				County county =new County();
				county.setcountyCode(array[0]);
				county.setcountyName(array[1]);
				county.setCityId(cityId);
				//解析出来的数据存到Province类
				coolWeatherDB.saveCounty(county);
				}
				return true;
			}	
		}    
		    return false;
	}
	
	
	public static void handleWeatherResponse(Context context,String response){
		try{
			JSONObject jsonObject=new JSONObject(response);
			
/*			JSONObject weatherInfo=jsonObject.getJSONObject("weatherInfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
			weatherDesp, publishTime);
		*/	
			//???
			JSONArray jsonArray=jsonObject.getJSONArray("HeWeather data service 3.0");
			JSONObject newInfo=jsonArray.getJSONObject(0);
			//获得basic ???
			JSONObject basic=newInfo.getJSONObject("basic");
			
			JSONArray daily_forecast=newInfo.getJSONArray("daily_forecast");
			JSONObject condInfo=daily_forecast.getJSONObject(0);
			JSONObject cond=condInfo.getJSONObject("cond");
			JSONObject temp=condInfo.getJSONObject("tmp");
			JSONObject wind=condInfo.getJSONObject("wind");
			
		    String cityName= basic.getString("city");
		    String weatherNow=cond.getString("txt_d");
		    String weatherNext=cond.getString("txt_n");
		    String temp2=temp.getString("max");
		    String temp1=temp.getString("min");
		    
		    String windDirection=wind.getString("dir");
		    String windLevel=wind.getString("sc");
		    saveWeatherInfo(context, cityName, weatherNow, weatherNext, temp1, temp2, windDirection, windLevel);
		   
		}
		catch(JSONException e){
			e.printStackTrace();
		}
	}
	    private static void saveWeatherInfo(Context context,String cityName,String weatherNow,String weatherNext,String temp1,String temp2,String windDirection,String windLevel){
	    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
	    	SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
	    	editor.putBoolean("city_selected", true);
	    	editor.putString("city_name", cityName);
	    	editor.putString("weather_now", weatherNow);
	    	editor.putString("weather_next", weatherNext);
	    	editor.putString("tmp1", temp1);
	    	editor.putString("tmp2", temp2);
	    	editor.putString("windDirection", windDirection);
	    	editor.putString("windLevel", windLevel);
	    	//editor.putString("windLevel", windLevel);
	    	                               //???
	    	editor.putString("currnt_date", sdf.format(new Date()));
	        editor.commit(); 	
	       }
	  }
	

		
	
	
	
	

