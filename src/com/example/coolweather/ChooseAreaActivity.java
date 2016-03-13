package com.example.coolweather;

import android.os.Bundle;
import android.text.TextUtils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.model.City;
import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private TextView titletext;
	private ListView ListView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> datalist=new ArrayList<String>();
	//省列表
	private List<Province> provinceList;
	//市列表
	private List<City> cityList;
	//县列表
	private List<County> countyList;
	//选中省
	private Province selectedProvince;
	private City selectedCity;
	//private County selectedCounty;
	//当前选中级别
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//???
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.choose_area);
		ListView=(ListView) findViewById(R.id.list_View);
	    titletext=(TextView) findViewById(R.id.title_text);
	   //???  
	    //queryProvinces();
	    //???
	    adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,datalist); 
	    ListView.setAdapter(adapter);
	   // registerForContextMenu(ListView);
	    
	    coolWeatherDB=CoolWeatherDB.getInstance(this);
	    ListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				// TODO Auto-generated method stub
				  //从哪里收到值
				if (currentLevel==LEVEL_PROVINCE) {
					selectedProvince=provinceList.get(index);
				     queryCities(); //加载市级数据
				}else if(currentLevel==LEVEL_CITY){
					
					selectedCity=cityList.get(index);
					queryCounties();
				}
			}
	    
	    });
	    //到此初始化工作完成
	    
	    //加载省级数据
	      queryProvinces();
	}
	//优先查找数据库 如果没有从服务器查询
    private void queryProvinces(){
    	provinceList=coolWeatherDB.loadProvinces();
    	if(provinceList.size()>0){
    		datalist.clear();
    		for(Province province:provinceList){
    			datalist.add(province.getProvinceName());
    			
    		}
    		//???    显示到界面 牵扯到UI操作
    		adapter.notifyDataSetChanged();
    		         //??  0?
    		/*ListView.postDelayed(new Runnable() {  
    		    @Override  
    	      public void run() {  
    	       ListView.requestFocusFromTouch();  
    	       ListView.setAdapter(adapter);
    		   ListView.setSelection(0); 
    		   titletext.setText("中国");
    		    }  
    		},500);*/

    		ListView.setAdapter(adapter);
    		//ListView.requestFocusFromTouch();//获取焦点  
    	    
    		ListView.setSelection(0);
    	    titletext.setText("中国");
    		currentLevel=LEVEL_PROVINCE;
    	}else{
    		queryFormServer(null,"province");
    		
    	}
    }
  //查询选中省内所有的市 优先从数据库查询 若没有再去服务器
    private void queryCities(){
	  cityList=coolWeatherDB.loadCities(selectedProvince.getId());
	  if(cityList.size()>0){
		  datalist.clear();
		  for(City city: cityList){
			  datalist.add(city.getCityName());
		  }
		  adapter.notifyDataSetChanged();
		  ListView.setSelection(0);
		  titletext.setText(selectedProvince.getProvinceName());
		  currentLevel=LEVEL_CITY;
	  }else {
		queryFormServer(selectedProvince.getProvinceCode(),"city");
	}
  }
    
    
    private void queryCounties(){
    	                       //做了什么
	  countyList=coolWeatherDB.loadCounties(selectedCity.getId());
	  if(countyList.size()>0){
		  datalist.clear();
		  for(County county: countyList){
			  datalist.add(county.getcountyName());
		  }
		  adapter.notifyDataSetChanged();
		  ListView.setSelection(0);
		  titletext.setText(selectedCity.getCityName());
		  currentLevel=LEVEL_COUNTY;
	  }else {
		queryFormServer(selectedCity.getCityCode(),"city");
	}
  }   
    //据传入的数据类型从服务器上查询省市县数据
    private void queryFormServer(final String code,final String type){
    	String address;
    	//工具类 判断传入的值是否为空 类似equal的作用
    	//Toast.makeText(this, "只能从服务器找喽",Toast.LENGTH_LONG);
    	if(!TextUtils.isEmpty(code)){
    		   address="http://apis.baidu.com/apistore/weatherservice/citylist"+code+".xml";
    		//address="http://www.weather.com.cn/data/list3/city"+code+".xml";
    	}else{
    		address="http://apis.baidu.com/apistore/weatherservice/citylist.xml";
    		
    		//address="http://www.weather.com.cn/data/list3/city.xml";
    		
    	}
    	showProgressDialog();
    	//Toast.makeText(ChooseAreaActivity.this, "正在加载.......",Toast.LENGTH_LONG);
    	//???   向服务器发送请求
    	HttpUtil.sendHttpRequst(address, new HttpCallbackListener() {
			
			@Override  //相应后会回调
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if("province".equals(type)){
					
					//??result有什么用  解析处理服务器返回的数据
					result=Utility.handleProvincesResponse(coolWeatherDB, response);
				}else if("city".equals(type)){
					result=Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if("county".equals(type)){
					
					result=Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
				if(result){
					//通过runOnUiThread 子线程切主线程处理逻辑（异步消息处理）
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								
								//再次调用 重新加载省级数据
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				  }   
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						 closeProgressDialog();
						 Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_LONG);
					}
				});
				
			}
		});
    }
    //显示进度兑换狂
    private void showProgressDialog(){
    	if(progressDialog==null){
    		progressDialog=new ProgressDialog(this);
    		progressDialog.setMessage("正在加载.............");
    		
    		//???
    		progressDialog.setCanceledOnTouchOutside(false);
    	}
    	progressDialog.show();
    }
    //关闭进度框
   private void closeProgressDialog() {
		// TODO Auto-generated method stub
             if(progressDialog!=null){
            	 progressDialog.dismiss();
            	 
             }
	}
   //捕获back建 决定返回省 或 市列表 或退出
   @Override
public void onBackPressed() {
	// TODO Auto-generated method stub
     if(currentLevel==LEVEL_COUNTY){
    	 queryCities();
     }	else if(currentLevel==LEVEL_CITY){
    	 queryProvinces();
     }else{
    	 finish();
     }
}
   
   
  
   
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
