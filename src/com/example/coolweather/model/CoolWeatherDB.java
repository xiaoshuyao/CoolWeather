package com.example.coolweather.model;

import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//该类用来封装常用的数据库
public class CoolWeatherDB {
    //数据库名
	public static final String DB_NAME="cool_weather";
	//数据库版本
	public static final int VERSION=1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	//构造方法
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
	
		db =dbHelper.getWritableDatabase();
		
	}
	//获取CoolWeatherDB的实例
	       //????
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB==null){
			
			coolWeatherDB =new CoolWeatherDB(context);
			
		}
		
		return coolWeatherDB;
	}
	
	//将 province 实例存储到数据库
	public void saveProvince(Province province){
		if(province !=null){
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
			
		}
		
	}
	
	//从数据库都去全国省份信息
	public List<Province> loadProvinces(){
		List<Province> list=new ArrayList<Province>(); 
		 //??              //1 表名参数2 null表示查询所有列 参3查询条件 参4对查询条件赋值null(按顺序) 5语法have 6排序方式
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		           //??
		if(cursor.moveToFirst()){
			do{
				Province province=new Province();
				                     //??
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				
				//落写了
				province.setprovinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				
				province.setprovincCode(cursor.getString(cursor.getColumnIndex("province_code")));
				
				list.add(province);
				      //???
			}  while(cursor.moveToNext());
		}
		return list;	
	}

	
	
	
	
	public void saveCity(City city){
		if(city!=null){
			//存储键值对
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			//values.put("province_id", city.getProvinceId());
            db.insert("City", null, values); //可以返回新天记录的行号			
	           //??
            
		}
		
	}
	public String searchcityCode(String cityname){
		//String cityCode;
		
		Cursor cursor=db.query("City",new String[]{"city_code"}, "city_name=\"北京\"", null, null, null ,null);
		 String cityCode=cursor.getString(cursor.getColumnIndex("city_code"));
		return cityCode;
		
	}
	
	
	
	public List<City> loadCities(){    //从数据库读取城市列表
		List<City> list=new ArrayList<City>(); 
		 //??              //??                                            //将int变量转换成字符串
		Cursor cursor=db.query("City", null,null, null, null, null, null);
		           //??
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				                     //??
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				//city.setProvinceId(provinceId);
				
				list.add(city);
				      //???
			}  while(cursor.moveToNext());
		}
		return list;	
	}
	

	//将County实例存储到数据库
	public void saveCounty(County county) {
		if(county !=null){
			ContentValues values=new ContentValues();
			values.put("county_name", county.getcountyName());
			values.put("county_code", county.getcountyCode());
			values.put("county_id", county.getCityId());
			db.insert("County", null, values);
			
		}
		
	}
	//从数据库都去某个城市下县区信息
	public List<County> loadCounties(int cityId){
		List<County> list=new ArrayList<County>(); 
		 //??              //??                                            //将int变量转换成字符串
		Cursor cursor=db.query("Couny", null,"city_id=?", new String[] {String.valueOf(cityId)}, null, null, null);
		           //??
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				                     //??
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setcountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setcountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
				      //???
			}  while(cursor.moveToNext());
		}
		return list;	
	}
	
	
}
