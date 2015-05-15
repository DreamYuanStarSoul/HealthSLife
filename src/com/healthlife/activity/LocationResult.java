package com.healthlife.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.healthlife.R;
import com.healthlife.db.DBManager;
import com.healthlife.entity.GlobalVariables;
import com.healthlife.entity.Position;
import com.healthlife.entity.Record;
import com.healthlife.entity.Sports;

public class LocationResult extends Activity{
	
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	private Sports newSports;
	private Position newPosition;
	private List<LatLng> pts;
	private ArrayList<Position> pos;
	private List<Position> points;
	private TextView durationTv;
	private TextView distanceTv;
	private TextView speedTv;
	private TextView stepsTv;
	private TextView heatTv;
	private Button saveBtn;
	private Double centerLatitude;
    private Double centerLongitude;
    private int steps;
    private boolean isFirst = true;
    private double distance;
    private long recordTime;
    private float speed;
	private long sportID;
    private String date;
	private String duration;
	private int showmode;
	private float calorie;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_result);

		newSports = new Sports();
		newPosition = new Position();
		
		durationTv = (TextView)findViewById(R.id.location_result_duration);
		distanceTv = (TextView)findViewById(R.id.location_result_distance);
		speedTv = (TextView)findViewById(R.id.location_result_speed);
		stepsTv = (TextView)findViewById(R.id.location_result_pace);
		heatTv = (TextView)findViewById(R.id.location_result_heat);
		saveBtn = (Button)findViewById(R.id.location_result_savebtn);
		
		Intent intent = getIntent();
		showmode = intent.getIntExtra("showmode",-1);
		if(showmode == GlobalVariables.MODE_SHOW_SAVED){
			DBManager myDB = new DBManager(LocationResult.this);
			newSports = (Sports)intent.getSerializableExtra("jog");
			date = newSports.getDate();
			centerLatitude = Double.valueOf((double)newSports.getGoodNum());
			centerLongitude = Double.valueOf((double)newSports.getPerfectNum());
			distance = (double)newSports.getDistance();
			duration = newSports.getDuration();
			speed = newSports.getAVGSpeed();
			steps = newSports.getNum();
			calorie = newSports.getCalorie();
			
			saveBtn.setVisibility(View.GONE);
			pos = myDB.getPosList(newSports.getSportsID());
			initMap(centerLatitude, centerLongitude);
			pts = new ArrayList<LatLng>();
			for(int i = 0;i<pos.size();i++)
			{
				LatLng pt = new LatLng(pos.get(i).getLatitude(), pos.get(i).getLongitude());
				pts.add(pt);
			}
				//��������յ�
				LatLng start = new LatLng(pos.get(0).getLatitude(), pos.get(0).getLongitude());
				LatLng end = new LatLng(pos.get(pos.size()-1).getLatitude(),
						pos.get(pos.size()-1).getLongitude());
				BitmapDescriptor bitmap = BitmapDescriptorFactory
						.fromResource(R.drawable.icon_st);
				BitmapDescriptor bitmap1 = BitmapDescriptorFactory
						.fromResource(R.drawable.icon_en);
				OverlayOptions option = new MarkerOptions()  
			    	.position(start)
			    	.icon(bitmap);
				OverlayOptions option1 = new MarkerOptions()  
		    		.position(end)
		    		.icon(bitmap1);
				mBaiduMap.addOverlay(option);
				mBaiduMap.addOverlay(option1);
				
				//�����������ߵ�Option����  
				OverlayOptions polylineOption = new PolylineOptions()  
				    .points(pts)
				    .width(9)
				    .color(0xAAFF4F4F);
				mBaiduMap.addOverlay(polylineOption);
		}else if(showmode == GlobalVariables.MODE_SHOW_UNSAVED){
			points = (List<Position>) intent.getSerializableExtra("locinfo");
			date = intent.getStringExtra("date");
			centerLatitude = intent.getDoubleExtra("cenlat", 39.923963);
			centerLongitude = intent.getDoubleExtra("cenlon", 116.403029);
			distance = intent.getDoubleExtra("distance", 0.0);
			recordTime = intent.getLongExtra("rectime", 0);
			duration = intent.getStringExtra("duration");
			speed = (float)(distance / recordTime);
			steps = intent.getIntExtra("steps", 0);

			float min = ((float)recordTime)/60;
			float hour = min/60;
			float speedPer400m=400/(float)distance*min;
			float K=30/speedPer400m;
			calorie = hour*60/*kg*/*K;
			
			Log.i("Test", String.valueOf(centerLatitude.floatValue()));
			//newSports.setUserId(1); //������1
			newSports.setDate(date);	
			newSports.setType(GlobalVariables.SPORTS_TYPE_JOG);
			newSports.setGoodNum(centerLatitude.floatValue());  //����γ��
			newSports.setPerfectNum(centerLongitude.floatValue()); //���ľ���
			newSports.setDistance((float)distance);
			newSports.setDuration(duration);
			newSports.setAVGSpeed(speed);
			newSports.setNum(steps);
			newSports.setCalorie(calorie);
			
			if(points.size() != 0){
				initMap(centerLatitude, centerLongitude);
				pts = new ArrayList<LatLng>();
				for(int i = 0;i<points.size();i++)
				{
					LatLng pt = new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude());
					pts.add(pt);
				}
				if(points.size() > 1){
					//��������յ�
					LatLng start = new LatLng(points.get(0).getLatitude(), points.get(0).getLongitude());
					LatLng end = new LatLng(points.get(points.size()-1).getLatitude(),
							points.get(points.size()-1).getLongitude());
					BitmapDescriptor bitmap = BitmapDescriptorFactory
							.fromResource(R.drawable.icon_st);
					BitmapDescriptor bitmap1 = BitmapDescriptorFactory
							.fromResource(R.drawable.icon_en);
					OverlayOptions option = new MarkerOptions()  
				    	.position(start)
				    	.icon(bitmap);
					OverlayOptions option1 = new MarkerOptions()  
			    		.position(end)
			    		.icon(bitmap1);
					mBaiduMap.addOverlay(option);
					mBaiduMap.addOverlay(option1);
					
					//�����������ߵ�Option����  
					OverlayOptions polylineOption = new PolylineOptions()  
					    .points(pts)
					    .width(9)
					    .color(0xAAFF4F4F);
					mBaiduMap.addOverlay(polylineOption);
				}	
			}else {
				Toast.makeText(this, "����Ϊ�գ�", Toast.LENGTH_SHORT).show();
			}
		}else {
			Log.i("Test", "No intent data.");
		}
		
		Log.i("Test", "�������ľ���Ϊ��" + String.valueOf(distance));
		
		durationTv.setText(duration);
		distanceTv.setText(String.format("%.1f", distance) + "��");
		speedTv.setText(String.format("%.1f", speed) + "m/s");
		stepsTv.setText(String.valueOf(steps));
		heatTv.setText(String.valueOf(calorie));

		
		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// �������ݿ�
				DBManager myDB = new DBManager(LocationResult.this);
				sportID = myDB.insertSport(newSports);
				for(int i=0;i<points.size();i++){
					newPosition.setSportId(sportID);
					newPosition.setTime(points.get(i).getTime());
					newPosition.setLatitude(points.get(i).getLatitude());
					newPosition.setLongitude(points.get(i).getLongitude());
					myDB.insertPosition(newPosition);
					
				}
				Record record = new Record();
				
				record = myDB.queryRecord();
				if(record.getRecordId()!=0){
					record.setCalOfJog(record.getCalOfJog()+newSports.getCalorie());
					record.setTotalCal(record.getTotalCal()+newSports.getCalorie());
					record.setTotalDistance(record.getTotalDistance()+newSports.getDistance());
					record.setTotalSteps(record.getTotalSteps()+newSports.getNum());
					
					record.setDurationSitUp(record.getDurationJog()+getDurationInFloat(newSports.getDuration()));
		    		record.setTotalDuration(record.getTotalDuration()+getDurationInFloat(newSports.getDuration()));
		    		myDB.updateRecord(record);
				}
	    		
				else{
					record.setCalOfJog(newSports.getCalorie());
					record.setTotalCal(newSports.getCalorie());
					record.setTotalDistance(newSports.getDistance());
					record.setTotalSteps(newSports.getNum());
					
					record.setDurationSitUp(getDurationInFloat(newSports.getDuration()));
		    		record.setTotalDuration(getDurationInFloat(newSports.getDuration()));
		    		myDB.insertRecord(record);
				}
	    		
				finish();
				
				Toast.makeText(LocationResult.this, "��¼����ɹ���", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	//��ͼ��ʼ��
	private void initMap(double cenLat, double cenLon){
		mMapView = (MapView)findViewById(R.id.location_result_bmapView);
		mBaiduMap = mMapView.getMap();
		LatLng cenpt = new LatLng(cenLat, cenLon);
//		LatLng cenpt = new LatLng(30.516939,114.441744);   //0.001��Χ
//		LatLng cenpt = new LatLng(30.516989,114.440994); 
		MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(15).build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		mBaiduMap.setMapStatus(mMapStatusUpdate);
	}
	
	private float getDurationInFloat(String duration){

    	float millis=-1;
    	duration="1970-1-1"+" "+duration; 
    	
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
			millis = formatter.parse(duration).getTime()-formatter.parse("1970-1-1 00:00:00").getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return millis;
    }
}
