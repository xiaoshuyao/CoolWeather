package com.example.coolweather.util;

import com.example.coolweather.model.City;
import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

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
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId)
	{        //???
		if(!TextUtils.isEmpty(response)){
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
				return true;
			}	
		}
		    return false;
	}
	
    //解析和处理服务器返回的县级数据 Json
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId)
	{        //???
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
	
	
	
	
	
}
