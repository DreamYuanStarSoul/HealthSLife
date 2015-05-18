package com.healthlife.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.baidu.mapapi.SDKInitializer;
import com.healthlife.R;
import com.healthlife.db.DBManager;
import com.healthlife.entity.Calorie;
import com.healthlife.entity.Sports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button heartBtn;
	private Button locateBtn;
	private Button sportBtn;
	private Button musicBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//ʹ��SDK֮ǰ�������ʼ��context��Ϣ������ApplicationContext
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.healthlife_main);
		
		getActionBar().setHomeButtonEnabled(true);
		
		heartBtn = (Button)findViewById(R.id.heartbtn);
		sportBtn = (Button)findViewById(R.id.sportbtn);
		musicBtn = (Button)findViewById(R.id.musicbtn);
		
		//���ʰ�ť��Ӧ
		heartBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, HeartRate.class);
				startActivity(intent);
			}
		});
		
		//���ֲ��Ű�ť��Ӧ
		musicBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MusicMain.class);
				startActivity(intent);
			}
		});
		//�˶����ݰ�ť��Ӧ
		sportBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SelectSportsActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, BackupsActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("SimpleDateFormat")
	private float getTodayCalorie(){
		DBManager db = new DBManager(this);
		SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String today=simpleFormatter.format(new java.util.Date());
		
		Calorie cal = db.queryCalorieByDate(today);
		ArrayList<Sports> sportsList = db.querySportsByDate(today);
		
		float sumCalorie=0;
		for(int i=0;i<sportsList.size();i++){
			sumCalorie+=sportsList.get(i).getCalorie();
		}
		cal.setCalorie(cal.getCalorie()-sumCalorie);
		
		return cal.getCalorie();
		
	}
}
