package com.healthlife.activity;

import com.baidu.mapapi.SDKInitializer;
import com.healthlife.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	
	private Button heartBtn;
	private Button locateBtn;
	private Button sportBtn;
	private Button musicBtn;
	private Button heatAddBtn;
	private LinearLayout linearLayout;
	
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
		heatAddBtn = (Button)findViewById(R.id.main_heataddbtn);
		linearLayout = (LinearLayout)findViewById(R.id.main_layout);
		
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
		//������뿨·�ﰴť��Ӧ
		heatAddBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CalCalorie.class);
				startActivity(intent);
			}
		});
		//��Ӷ���layout�����Ӧ
		linearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//linearLayout.setBackgroundColor(Color.parseColor("#f0f0f0"));
				linearLayout.setBackgroundResource(R.drawable.mainlayout_selecor);
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ShowCalDiff.class);
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
		if (id == R.id.personal_center) {
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
}
