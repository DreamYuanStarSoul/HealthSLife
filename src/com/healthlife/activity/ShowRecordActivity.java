package com.healthlife.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.healthlife.R;
import com.healthlife.db.DBManager;
import com.healthlife.entity.Record;


public class ShowRecordActivity extends Activity {
	
	private DBManager db;
	private Record record;
	private Button btnClear;
	private TextView textDistance,textSpeed,textPushUps,textPerfectPushUps,textValidPushUps,textSitUps,textValidSitUps,textPerfectSitUps,textSteps,textTotalDistance,textTotalPushUps,textTotalSitUps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_records);
		
		btnClear = (Button)findViewById(R.id.button_record_clear);
		
		
		textDistance=(TextView)findViewById(R.id.text_record_distance);
		textSpeed=(TextView)findViewById(R.id.text_record_avgspeed);	
		textPushUps=(TextView)findViewById(R.id.text_record_pushups);
		textPerfectPushUps=(TextView)findViewById(R.id.text_record_pushups_perfect);	
		textValidPushUps=(TextView)findViewById(R.id.text_record_pushups_valid);		
		textSitUps=(TextView)findViewById(R.id.text_record_situps);
		textValidSitUps	=(TextView)findViewById(R.id.text_record_situps_valid	);
		textPerfectSitUps=(TextView)findViewById(R.id.text_record_situps_perfect);
		textSteps=(TextView)findViewById(R.id.text_record_steps);
		textTotalDistance=(TextView)findViewById(R.id.text_record_totaldistance);
		textTotalPushUps=(TextView)findViewById(R.id.text_record_totalpushups);
		textTotalSitUps=(TextView)findViewById(R.id.text_record_totalsitups);
		
		db = new DBManager(this);
		record = db.queryRecord();
		
		textDistance.setText("�����: "+String.valueOf(""+record.getDistance())+" km");
		textSpeed.setText("ƽ���ٶ�: "+String.valueOf(""+record.getAVGSpeed())+" km/h");
		textPushUps.setText("�վ����ԳŴ�: "+String.valueOf(""+record.getNumPushUp())+" ��");
		textPerfectPushUps.setText("���ԳŶ���������: "+String.valueOf(record.getPerfectNumPushUp())+" ��");
		textValidPushUps.setText("���ԳŶ����ϸ���: "+String.valueOf(record.getValidNumPushUp())+" ��");
		textSitUps.setText("�վ���������: "+String.valueOf(record.getNumSitUp())+" ��");
		textValidSitUps.setText("��������������: "+String.valueOf(record.getValidNumSitUp())+" ��");
		textPerfectSitUps.setText("���������ϸ���: "+String.valueOf(record.getPerfectNumSitUp())+" ��");
		textSteps.setText("�ܲ���: "+String.valueOf(record.getSteps())+" ��");
		textTotalDistance.setText("�����ܾ���: "+String.valueOf(record.getTotalDistance())+" km");
		textTotalPushUps.setText("���Գ�����: "+String.valueOf(record.getTotalNumPushUp())+" ��");
		textTotalSitUps.setText("������������: "+String.valueOf(record.getTotalNumSitUp())+" ��");
			
		btnClear.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				db.clearRecord(record.getRecordId());
			}		
		});
		Log.i("dd", "aaa");
	}
}
