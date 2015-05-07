package com.healthlife.activity;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.healthlife.R;
/**
 * Title: GetLocation.java
 * @author Jusitn Yin
 * 2015-5-7
 */
public class GetLocation extends Activity {
	
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	private LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	private MyLocationConfiguration.LocationMode mLocationConfiguration = MyLocationConfiguration.LocationMode.FOLLOWING;
	private volatile boolean isFirstLocation = true;
	private boolean judge = true;
	private Double mCurrentLantitude;  
    private Double mCurrentLongitude;
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.location_main);
		
		initMap();
		initMyLocation();
		
	}
	
	private void initMyLocation()
	{
		// ��λ��ʼ��
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ
		option.setOpenGps(true); // ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(5000);
		mLocationClient.setLocOption(option);
	}
	
	//��ͼ��ʼ��
	private void initMap(){
		mMapView = (MapView)findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		LatLng cenpt = new LatLng(30.513236,114.419936); 
		MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(18).build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		mBaiduMap.setMapStatus(mMapStatusUpdate);
	}

	/**
	 * ʵ��ʵλ�ص�����
	 */
	public class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{
			MyLocationData locData = new MyLocationData.Builder()  
			    .accuracy(location.getRadius())  
			    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
			    .direction(0).latitude(location.getLatitude())  
			    .longitude(location.getLongitude()).build();  
			// ���ö�λ����  
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();  
            mCurrentLongitude = location.getLongitude();
            
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());       
    		String date = sDateFormat.format(new java.util.Date());  
    		/*
    		Point p = new Point();
    		p.setDate(date);
    		p.setLatitude(mCurrentLantitude);
    		p.setLongitude(mCurrentLongitude);
    		points.add(p);
    		
    		ActionBar actionBar = getActionBar();
    		actionBar.setTitle(String.valueOf(points.size()));
            */
            Log.i("Lat", "γ�ȣ�" + mCurrentLantitude.toString());
            Log.i("Lon", "����" + mCurrentLongitude.toString());
            /*
            // ���ö�λͼ������ã���λģʽ���Ƿ���������Ϣ���û��Զ��嶨λͼ�꣩  
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory  
			    .fromResource(R.drawable.locimg);
			MyLocationConfiguration config = new MyLocationConfiguration(mLocationConfiguration, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config); 
			*/
			// ��һ�ζ�λʱ������ͼλ���ƶ�����ǰλ��
			if (isFirstLocation)
			{
				isFirstLocation = false;
				returnMyLoc();
			}
		}
	}
	
	//������ҵ�λ�á�
	private void returnMyLoc(){
		mLocationClient.requestLocation();
		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);  
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);  
        mBaiduMap.animateMapStatus(u);
	}
	
	@Override
	protected void onStart() {
		// ����ͼ�㶨λ  
        mBaiduMap.setMyLocationEnabled(true);  
        if (!mLocationClient.isStarted())  
        {  
            mLocationClient.start();  
        }
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy(); 
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause(); 
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume(); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.get_location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.location_returnloc) {
			try{
				returnMyLoc();
			}catch(Exception e){
				Toast.makeText(this, "û�м�⵽λ��", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		if (id == R.id.location_switchtype){
			if(judge)
			{
				item.setTitle("�л�����ͨͼ");
				judge = false;
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);  //��������ͼ
			}else{
				item.setTitle("�л�������ͼ");
				judge = true;
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
